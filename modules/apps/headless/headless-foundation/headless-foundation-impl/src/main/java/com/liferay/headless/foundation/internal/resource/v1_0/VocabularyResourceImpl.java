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

package com.liferay.headless.foundation.internal.resource.v1_0;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.ClassType;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.headless.common.spi.service.context.ServiceContextUtil;
import com.liferay.headless.foundation.dto.v1_0.AssetType;
import com.liferay.headless.foundation.dto.v1_0.Vocabulary;
import com.liferay.headless.foundation.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.foundation.internal.odata.entity.v1_0.VocabularyEntityModel;
import com.liferay.headless.foundation.resource.v1_0.VocabularyResource;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.resource.EntityModelResource;
import com.liferay.portal.vulcan.util.ContentLanguageUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.portlet.asset.util.AssetVocabularySettingsHelper;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.collections.MapUtils;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/vocabulary.properties",
	scope = ServiceScope.PROTOTYPE, service = VocabularyResource.class
)
public class VocabularyResourceImpl
	extends BaseVocabularyResourceImpl implements EntityModelResource {

	@Override
	public boolean deleteVocabulary(Long vocabularyId) throws Exception {
		_assetVocabularyService.deleteVocabulary(vocabularyId);

		return true;
	}

	@Override
	public Page<Vocabulary> getContentSpaceVocabulariesPage(
			Long contentSpaceId, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			booleanQuery -> {
			},
			filter, AssetVocabulary.class, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ASSET_VOCABULARY_ID),
			searchContext -> {
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setGroupIds(new long[] {contentSpaceId});
			},
			document -> _toVocabulary(
				_assetVocabularyService.getVocabulary(
					GetterUtil.getLong(
						document.get(Field.ASSET_VOCABULARY_ID)))),
			sorts);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public Vocabulary getVocabulary(Long vocabularyId) throws Exception {
		AssetVocabulary assetVocabulary = _assetVocabularyService.getVocabulary(
			vocabularyId);

		ContentLanguageUtil.addContentLanguageHeader(
			assetVocabulary.getAvailableLanguageIds(),
			assetVocabulary.getDefaultLanguageId(), _contextHttpServletResponse,
			contextAcceptLanguage.getPreferredLocale());

		return _toVocabulary(assetVocabulary);
	}

	@Override
	public Vocabulary postContentSpaceVocabulary(
			Long contentSpaceId, Vocabulary vocabulary)
		throws Exception {

		return _toVocabulary(
			_assetVocabularyService.addVocabulary(
				contentSpaceId, null,
				Collections.singletonMap(
					contextAcceptLanguage.getPreferredLocale(),
					vocabulary.getName()),
				Collections.singletonMap(
					contextAcceptLanguage.getPreferredLocale(),
					vocabulary.getDescription()),
				_getSettings(vocabulary.getAssetTypes(), contentSpaceId),
				ServiceContextUtil.createServiceContext(
					contentSpaceId, vocabulary.getViewableByAsString())));
	}

	@Override
	public Vocabulary putVocabulary(Long vocabularyId, Vocabulary vocabulary)
		throws Exception {

		AssetVocabulary assetVocabulary = _assetVocabularyService.getVocabulary(
			vocabularyId);

		return _toVocabulary(
			_assetVocabularyService.updateVocabulary(
				assetVocabulary.getVocabularyId(), null,
				LocalizedMapUtil.merge(
					assetVocabulary.getTitleMap(),
					new AbstractMap.SimpleEntry<>(
						contextAcceptLanguage.getPreferredLocale(),
						vocabulary.getName())),
				LocalizedMapUtil.merge(
					assetVocabulary.getDescriptionMap(),
					new AbstractMap.SimpleEntry<>(
						contextAcceptLanguage.getPreferredLocale(),
						vocabulary.getDescription())),
				_getSettings(
					vocabulary.getAssetTypes(), assetVocabulary.getGroupId()),
				new ServiceContext()));
	}

	private AssetType _getAssetType(
		long groupId, long classNameId, long classTypePK,
		long[] requiredClassNameIds) {

		ClassName className = _classNameLocalService.fetchByClassNameId(
			classNameId);

		return new AssetType() {
			{
				required = ArrayUtil.contains(
					requiredClassNameIds, classNameId);
				setSubtype(
					() -> {
						if (classTypePK ==
								AssetCategoryConstants.ALL_CLASS_TYPE_PK) {

							return "AllAssetSubtypes";
						}

						AssetRendererFactory assetRendererFactory =
							AssetRendererFactoryRegistryUtil.
								getAssetRendererFactoryByClassName(
									className.getClassName());

						ClassTypeReader classTypeReader =
							assetRendererFactory.getClassTypeReader();

						List<ClassType> classTypes =
							classTypeReader.getAvailableClassTypes(
								new long[] {
									groupId, contextCompany.getGroupId()
								},
								contextAcceptLanguage.getPreferredLocale());

						if (ListUtil.isEmpty(classTypes)) {
							return "AllAssetSubtypes";
						}

						for (ClassType classType : classTypes) {
							if (classType.getClassTypeId() == classTypePK) {
								return classType.getName();
							}
						}

						throw new InternalServerErrorException();
					});
				setType(
					() -> {
						if (classNameId ==
								AssetCategoryConstants.ALL_CLASS_NAME_ID) {

							return AssetType.Type.ALL_ASSET_TYPES;
						}

						return _classNameToAssetTypeTypes.get(
							className.getClassName());
					});
			}
		};
	}

	private AssetType[] _getAssetTypes(
			AssetVocabularySettingsHelper assetVocabularySettingsHelper,
			long groupId)
		throws Exception {

		long[] classNameIds = assetVocabularySettingsHelper.getClassNameIds();

		if (ArrayUtil.isEmpty(classNameIds)) {
			return new AssetType[0];
		}

		AssetType[] assetTypes = new AssetType[classNameIds.length];

		long[] classTypePKs = assetVocabularySettingsHelper.getClassTypePKs();
		long[] requiredClassNameIds =
			assetVocabularySettingsHelper.getRequiredClassNameIds();

		for (int i = 0; i < classNameIds.length; i++) {
			long classNameId = classNameIds[i];
			long classTypePK = classTypePKs[i];

			assetTypes[i] = _getAssetType(
				groupId, classNameId, classTypePK, requiredClassNameIds);
		}

		return assetTypes;
	}

	private long _getClassNameId(AssetType.Type assetTypeType) {
		if (Objects.equals(AssetType.Type.ALL_ASSET_TYPES, assetTypeType)) {
			return AssetCategoryConstants.ALL_CLASS_NAME_ID;
		}

		if (!_assetTypeTypeToClassNames.containsKey(assetTypeType)) {
			throw new BadRequestException(
				"Invalid asset type: " + assetTypeType);
		}

		ClassName className = _classNameLocalService.fetchClassName(
			_assetTypeTypeToClassNames.get(assetTypeType));

		return className.getClassNameId();
	}

	private long _getClassTypePK(long classNameId, String subtype, long groupId)
		throws Exception {

		if (Objects.equals("AllAssetSubtypes", subtype) ||
			(classNameId == AssetCategoryConstants.ALL_CLASS_NAME_ID) ||
			(subtype == null)) {

			return AssetCategoryConstants.ALL_CLASS_TYPE_PK;
		}

		ClassName className = _classNameLocalService.fetchByClassNameId(
			classNameId);

		AssetRendererFactory assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				className.getClassName());

		ClassTypeReader classTypeReader =
			assetRendererFactory.getClassTypeReader();

		List<ClassType> classTypes = classTypeReader.getAvailableClassTypes(
			new long[] {groupId, contextCompany.getGroupId()},
			contextAcceptLanguage.getPreferredLocale());

		if (ListUtil.isEmpty(classTypes)) {
			return AssetCategoryConstants.ALL_CLASS_TYPE_PK;
		}

		for (ClassType classType : classTypes) {
			if (Objects.equals(classType.getName(), subtype)) {
				return classType.getClassTypeId();
			}
		}

		throw new BadRequestException("Invalid subtype " + subtype);
	}

	private String _getSettings(AssetType[] assetTypes, long groupId)
		throws Exception {

		AssetVocabularySettingsHelper assetVocabularySettingsHelper =
			new AssetVocabularySettingsHelper();

		if (ArrayUtil.isEmpty(assetTypes)) {
			return assetVocabularySettingsHelper.toString();
		}

		long[] classNameIds = new long[assetTypes.length];
		long[] classTypePKs = new long[assetTypes.length];
		boolean[] requiredClassNameIds = new boolean[assetTypes.length];

		for (int i = 0; i < assetTypes.length; i++) {
			AssetType assetType = assetTypes[i];

			long classNameId = _getClassNameId(assetType.getType());

			classNameIds[i] = classNameId;

			classTypePKs[i] = _getClassTypePK(
				classNameId, assetType.getSubtype(), groupId);

			requiredClassNameIds[i] = assetType.getRequired();
		}

		assetVocabularySettingsHelper.setClassNameIdsAndClassTypePKs(
			classNameIds, classTypePKs, requiredClassNameIds);

		assetVocabularySettingsHelper.setMultiValued(true);

		return assetVocabularySettingsHelper.toString();
	}

	private Vocabulary _toVocabulary(AssetVocabulary assetVocabulary)
		throws Exception {

		return new Vocabulary() {
			{
				assetTypes = _getAssetTypes(
					new AssetVocabularySettingsHelper(
						assetVocabulary.getSettings()),
					assetVocabulary.getGroupId());
				availableLanguages = LocaleUtil.toW3cLanguageIds(
					assetVocabulary.getAvailableLanguageIds());
				contentSpace = assetVocabulary.getGroupId();
				creator = CreatorUtil.toCreator(
					_portal,
					_userLocalService.getUser(assetVocabulary.getUserId()));
				dateCreated = assetVocabulary.getCreateDate();
				dateModified = assetVocabulary.getModifiedDate();
				description = assetVocabulary.getDescription(
					contextAcceptLanguage.getPreferredLocale());
				hasCategories = ListUtil.isNotEmpty(
					assetVocabulary.getCategories());
				id = assetVocabulary.getVocabularyId();
				name = assetVocabulary.getTitle(
					contextAcceptLanguage.getPreferredLocale());
			}
		};
	}

	private static final Map<AssetType.Type, String>
		_assetTypeTypeToClassNames = new HashMap<AssetType.Type, String>() {
			{
				put(AssetType.Type.WEB_SITE, Group.class.getName());
				put(AssetType.Type.WEB_PAGE, Layout.class.getName());
				put(AssetType.Type.ORGANIZATION, Organization.class.getName());
				put(AssetType.Type.USER_ACCOUNT, User.class.getName());
				put(
					AssetType.Type.BLOG_POSTING,
					"com.liferay.blogs.model.BlogsEntry");
				put(
					AssetType.Type.DOCUMENT,
					"com.liferay.document.library.kernel.model.DLFileEntry");
				put(
					AssetType.Type.KNOWLEDGE_BASE_ARTICLE,
					"com.liferay.knowledge.base.model.KBArticle");
				put(
					AssetType.Type.STRUCTURED_CONTENT,
					"com.liferay.journal.model.JournalArticle");
				put(
					AssetType.Type.WIKI_PAGE,
					"com.liferay.wiki.model.WikiPage");
			}
		};
	private static final Map<String, AssetType.Type>
		_classNameToAssetTypeTypes = MapUtils.invertMap(
			_assetTypeTypeToClassNames);
	private static final EntityModel _entityModel = new VocabularyEntityModel();

	@Reference
	private AssetVocabularyService _assetVocabularyService;

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Context
	private HttpServletResponse _contextHttpServletResponse;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}