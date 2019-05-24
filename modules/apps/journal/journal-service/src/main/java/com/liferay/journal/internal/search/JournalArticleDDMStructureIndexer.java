/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.journal.internal.search;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.journal.configuration.JournalServiceConfiguration;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.service.JournalArticleResourceLocalService;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.DDMStructureIndexer;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.index.IndexStatusManager;

import java.util.List;
import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lucas Marques de Paula
 */
@Component(
	immediate = true,
	property = "ddm.structure.indexer.class.name=com.liferay.journal.model.JournalArticle",
	service = Indexer.class
)
public class JournalArticleDDMStructureIndexer
	extends BaseIndexer<DDMStructure> implements DDMStructureIndexer {

	public static final String CLASS_NAME = DDMStructure.class.getName();

	public JournalArticleDDMStructureIndexer() {
		setDefaultSelectedFieldNames(
			Field.CLASS_NAME_ID, Field.CLASS_PK, Field.COMPANY_ID,
			Field.DESCRIPTION, Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
			Field.NAME, Field.UID);
		setDefaultSelectedLocalizedFieldNames(Field.DESCRIPTION, Field.NAME);
		setPermissionAware(true);
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public void reindexDDMStructures(List<Long> ddmStructureIds)
		throws SearchException {

		try {
			Indexer<JournalArticle> indexer =
				indexerRegistry.nullSafeGetIndexer(JournalArticle.class);

			if (_indexStatusManager.isIndexReadOnly() ||
				!indexer.isIndexerEnabled()) {

				return;
			}

			String[] ddmStructureKeys = new String[ddmStructureIds.size()];

			for (int i = 0; i < ddmStructureIds.size(); i++) {
				long ddmStructureId = ddmStructureIds.get(i);

				DDMStructure ddmStructure =
					ddmStructureLocalService.getDDMStructure(ddmStructureId);

				ddmStructureKeys[i] = ddmStructure.getStructureKey();

				doReindex(ddmStructure);
			}

			ActionableDynamicQuery actionableDynamicQuery =
				journalArticleResourceLocalService.getActionableDynamicQuery();

			actionableDynamicQuery.setAddCriteriaMethod(
				dynamicQuery -> {
					Class<?> clazz = getClass();

					DynamicQuery journalArticleDynamicQuery =
						DynamicQueryFactoryUtil.forClass(
							JournalArticle.class, "journalArticle",
							clazz.getClassLoader());

					journalArticleDynamicQuery.setProjection(
						ProjectionFactoryUtil.property("resourcePrimKey"));

					journalArticleDynamicQuery.add(
						RestrictionsFactoryUtil.eqProperty(
							"journalArticle.resourcePrimKey",
							"this.resourcePrimKey"));

					journalArticleDynamicQuery.add(
						RestrictionsFactoryUtil.eqProperty(
							"journalArticle.groupId", "this.groupId"));

					Property ddmStructureKey = PropertyFactoryUtil.forName(
						"DDMStructureKey");

					journalArticleDynamicQuery.add(
						ddmStructureKey.in(ddmStructureKeys));

					if (!isIndexAllArticleVersions()) {
						Property statusProperty = PropertyFactoryUtil.forName(
							"status");

						Integer[] statuses = {
							WorkflowConstants.STATUS_APPROVED,
							WorkflowConstants.STATUS_IN_TRASH
						};

						journalArticleDynamicQuery.add(
							statusProperty.in(statuses));
					}

					Property resourcePrimKeyProperty =
						PropertyFactoryUtil.forName("resourcePrimKey");

					dynamicQuery.add(
						resourcePrimKeyProperty.in(journalArticleDynamicQuery));
				});
			actionableDynamicQuery.setPerformActionMethod(
				(JournalArticleResource article) -> {
					try {
						indexer.reindex(
							indexer.getClassName(),
							article.getResourcePrimKey());
					}
					catch (Exception e) {
						throw new PortalException(e);
					}
				});

			actionableDynamicQuery.performActions();
		}
		catch (Exception e) {
			throw new SearchException(e);
		}
	}

	@Override
	protected void doDelete(DDMStructure ddmStructure) throws Exception {
		deleteDocument(
			ddmStructure.getCompanyId(), ddmStructure.getStructureId());
	}

	@Override
	protected Document doGetDocument(DDMStructure ddmStructure)
		throws Exception {

		Document document = getBaseModelDocument(CLASS_NAME, ddmStructure);

		Locale defaultLocale = LocaleUtil.getSiteDefault();

		String defaultLanguageId = LocaleUtil.toLanguageId(defaultLocale);

		String[] descriptionLanguageIds = getLanguageIds(
			defaultLanguageId, ddmStructure.getDescription());

		for (String descriptionLanguageId : descriptionLanguageIds) {
			document.addText(
				LocalizationUtil.getLocalizedName(
					Field.DESCRIPTION, descriptionLanguageId),
				ddmStructure.getDescription(descriptionLanguageId));
		}

		String[] nameLanguageIds = getLanguageIds(
			defaultLanguageId, ddmStructure.getName());

		for (String nameLanguageId : nameLanguageIds) {
			document.addText(
				LocalizationUtil.getLocalizedName(Field.NAME, nameLanguageId),
				ddmStructure.getName(nameLanguageId));
		}

		document.addLocalizedKeyword(
			"localized_name",
			LocalizationUtil.populateLocalizationMap(
				ddmStructure.getNameMap(), ddmStructure.getDefaultLanguageId(),
				ddmStructure.getGroupId()),
			true, true);

		return document;
	}

	@Override
	protected Summary doGetSummary(
			Document document, Locale locale, String snippet,
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws Exception {

		return null;
	}

	@Override
	protected void doReindex(DDMStructure ddmStructure) throws Exception {
		Document document = getDocument(ddmStructure);

		indexWriterHelper.updateDocument(
			getSearchEngineId(), ddmStructure.getCompanyId(), document,
			isCommitImmediately());
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		doReindex(ddmStructureLocalService.getStructure(classPK));
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		reindexDDMStructures(companyId);
	}

	protected String[] getLanguageIds(
		String defaultLanguageId, String content) {

		String[] languageIds = LocalizationUtil.getAvailableLanguageIds(
			content);

		if (languageIds.length == 0) {
			languageIds = new String[] {defaultLanguageId};
		}

		return languageIds;
	}

	protected boolean isIndexAllArticleVersions() {
		JournalServiceConfiguration journalServiceConfiguration = null;

		try {
			journalServiceConfiguration =
				configurationProvider.getCompanyConfiguration(
					JournalServiceConfiguration.class,
					CompanyThreadLocal.getCompanyId());

			return journalServiceConfiguration.indexAllArticleVersionsEnabled();
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		return false;
	}

	protected void reindexDDMStructures(long companyId) throws Exception {
		final IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			ddmStructureLocalService.getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(DDMStructure ddmStructure) -> {
				try {
					Document document = getDocument(ddmStructure);

					if (document != null) {
						indexableActionableDynamicQuery.addDocuments(document);
					}
				}
				catch (PortalException pe) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to index ddm structure " +
								ddmStructure.getStructureId(),
							pe);
					}
				}
			});
		indexableActionableDynamicQuery.setSearchEngineId(getSearchEngineId());

		indexableActionableDynamicQuery.performActions();
	}

	@Reference
	protected ConfigurationProvider configurationProvider;

	@Reference
	protected DDMStructureLocalService ddmStructureLocalService;

	@Reference
	protected IndexerRegistry indexerRegistry;

	@Reference
	protected IndexWriterHelper indexWriterHelper;

	@Reference
	protected JournalArticleResourceLocalService
		journalArticleResourceLocalService;

	private void _reindexDDMStructures(List<Long> ddmStructureIds)
		throws PortalException {

		final IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			ddmStructureLocalService.getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property ddmStructureId = PropertyFactoryUtil.forName(
					"structureId");

				dynamicQuery.add(ddmStructureId.in(ddmStructureIds));
			});
		indexableActionableDynamicQuery.setPerformActionMethod(
			(DDMStructure ddmStructure) -> {
				try {
					Document document = getDocument(ddmStructure);

					indexableActionableDynamicQuery.addDocuments(document);
				}
				catch (PortalException pe) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to index DDMStrcutured " +
								ddmStructure.getStructureId(),
							pe);
					}
				}
			});
		indexableActionableDynamicQuery.setSearchEngineId(getSearchEngineId());

		indexableActionableDynamicQuery.performActions();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleDDMStructureIndexer.class);

	@Reference
	private IndexStatusManager _indexStatusManager;

}