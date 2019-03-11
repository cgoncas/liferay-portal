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

package com.liferay.headless.web.experience.internal.resource.v1_0;

import static com.liferay.portal.vulcan.util.LocalDateTimeUtil.toLocalDateTime;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializerSerializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializerSerializeResponse;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesSerializerTracker;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.Field;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.dynamic.data.mapping.util.DDMFieldsCounter;
import com.liferay.headless.common.spi.service.context.ServiceContextUtil;
import com.liferay.headless.web.experience.dto.v1_0.Categories;
import com.liferay.headless.web.experience.dto.v1_0.ContentDocument;
import com.liferay.headless.web.experience.dto.v1_0.ContentField;
import com.liferay.headless.web.experience.dto.v1_0.Geo;
import com.liferay.headless.web.experience.dto.v1_0.RenderedContents;
import com.liferay.headless.web.experience.dto.v1_0.StructuredContent;
import com.liferay.headless.web.experience.dto.v1_0.StructuredContentImage;
import com.liferay.headless.web.experience.dto.v1_0.StructuredContentLink;
import com.liferay.headless.web.experience.dto.v1_0.Value;
import com.liferay.headless.web.experience.internal.dto.v1_0.util.AggregateRatingUtil;
import com.liferay.headless.web.experience.internal.dto.v1_0.util.ContentStructureUtil;
import com.liferay.headless.web.experience.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.web.experience.internal.odata.entity.v1_0.EntityFieldsProvider;
import com.liferay.headless.web.experience.internal.odata.entity.v1_0.StructuredContentEntityModel;
import com.liferay.headless.web.experience.resource.v1_0.StructuredContentResource;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.journal.util.JournalContent;
import com.liferay.journal.util.JournalConverter;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.EventsProcessorUtil;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.DummyHttpServletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.resource.EntityModelResource;
import com.liferay.portal.vulcan.util.ContentLanguageUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.ratings.kernel.service.RatingsStatsLocalService;

import java.io.Serializable;

import java.time.LocalDateTime;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/structured-content.properties",
	scope = ServiceScope.PROTOTYPE, service = StructuredContentResource.class
)
public class StructuredContentResourceImpl
	extends BaseStructuredContentResourceImpl implements EntityModelResource {

	@Override
	public boolean deleteStructuredContent(Long structuredContentId)
		throws Exception {

		JournalArticle journalArticle = _journalArticleService.getLatestArticle(
			structuredContentId);

		_journalArticleService.deleteArticle(
			journalArticle.getGroupId(), journalArticle.getArticleId(),
			journalArticle.getArticleResourceUuid(), new ServiceContext());

		return true;
	}

	@Override
	public Page<StructuredContent> getContentSpaceStructuredContentsPage(
			Long contentSpaceId, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return _getStructuredContentsPage(
			contentSpaceId, null, filter, pagination, sorts);
	}

	@Override
	public Page<StructuredContent> getContentStructureStructuredContentsPage(
			Long contentStructureId, Filter filter, Pagination pagination,
			Sort[] sorts)
		throws Exception {

		return _getStructuredContentsPage(
			null, contentStructureId, filter, pagination, sorts);
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap)
		throws Exception {

		List<EntityField> entityFields = null;

		Long contentStructureId = GetterUtil.getLong(
			(String)multivaluedMap.getFirst("content-structure-id"));

		if (contentStructureId > 0) {
			DDMStructure ddmStructure = _ddmStructureService.getStructure(
				contentStructureId);

			entityFields = _entityFieldsProvider.provide(ddmStructure);
		}
		else {
			entityFields = Collections.emptyList();
		}

		return new StructuredContentEntityModel(entityFields);
	}

	@Override
	public StructuredContent getStructuredContent(Long structuredContentId)
		throws Exception {

		JournalArticle journalArticle = _journalArticleService.getLatestArticle(
			structuredContentId);

		ContentLanguageUtil.addContentLanguageHeader(
			journalArticle.getAvailableLanguageIds(),
			journalArticle.getDefaultLanguageId(), _contextHttpServletResponse,
			contextAcceptLanguage.getPreferredLocale());

		return _toStructuredContent(journalArticle);
	}

	@Override
	public String getStructuredContentRenderedContentTemplate(
			Long structuredContentId, Long templateId)
		throws Exception {

		JournalArticle journalArticle = _journalArticleService.getLatestArticle(
			structuredContentId);

		EventsProcessorUtil.process(
			PropsKeys.SERVLET_SERVICE_EVENTS_PRE,
			PropsValues.SERVLET_SERVICE_EVENTS_PRE, _contextHttpServletRequest,
			new DummyHttpServletResponse());

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_contextHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		themeDisplay.setLocale(_contextHttpServletRequest.getLocale());
		themeDisplay.setScopeGroupId(journalArticle.getGroupId());
		themeDisplay.setSiteGroupId(journalArticle.getGroupId());

		DDMTemplate ddmTemplate = _ddmTemplateService.getTemplate(templateId);

		JournalArticleDisplay journalArticleDisplay =
			_journalContent.getDisplay(
				journalArticle.getGroupId(), journalArticle.getArticleId(),
				ddmTemplate.getTemplateKey(), null,
				LocaleUtil.toLanguageId(
					contextAcceptLanguage.getPreferredLocale()),
				themeDisplay);

		String content = journalArticleDisplay.getContent();

		return content.replaceAll("[\\t\\n]", "");
	}

	@Override
	public StructuredContent postContentSpaceStructuredContent(
			Long contentSpaceId, StructuredContent structuredContent)
		throws Exception {

		DDMStructure ddmStructure = _ddmStructureService.getStructure(
			structuredContent.getContentStructureId());
		LocalDateTime localDateTime = toLocalDateTime(
			structuredContent.getDatePublished());

		if (!LocaleUtil.equals(
				LocaleUtil.fromLanguageId(ddmStructure.getDefaultLanguageId()),
				contextAcceptLanguage.getPreferredLocale())) {

			String w3cLanguageId = LocaleUtil.toW3cLanguageId(
				ddmStructure.getDefaultLanguageId());

			throw new BadRequestException(
				"Structured contents can only be created with the default " +
					"language " + w3cLanguageId);
		}

		String viewableBy = Optional.ofNullable(
			structuredContent.getViewableBy()
		).map(
			StructuredContent.ViewableBy::getValue
		).orElse(
			null
		);

		return _toStructuredContent(
			_journalArticleService.addArticle(
				contentSpaceId, 0, 0, 0, null, true,
				new HashMap<Locale, String>() {
					{
						put(
							contextAcceptLanguage.getPreferredLocale(),
							structuredContent.getTitle());
					}
				},
				new HashMap<Locale, String>() {
					{
						put(
							contextAcceptLanguage.getPreferredLocale(),
							structuredContent.getDescription());
					}
				},
				_createJournalArticleContent(
					_toDDMFormFieldValues(
						structuredContent.getContentFields(), ddmStructure,
						contextAcceptLanguage.getPreferredLocale()),
					ddmStructure),
				ddmStructure.getStructureKey(),
				_getDDMTemplateKey(ddmStructure), null,
				localDateTime.getMonthValue() - 1,
				localDateTime.getDayOfMonth(), localDateTime.getYear(),
				localDateTime.getHour(), localDateTime.getMinute(), 0, 0, 0, 0,
				0, true, 0, 0, 0, 0, 0, true, true, null,
				ServiceContextUtil.createServiceContext(
					structuredContent.getKeywords(),
					structuredContent.getCategoryIds(), contentSpaceId,
					viewableBy)));
	}

	@Override
	public StructuredContent putStructuredContent(
			Long structuredContentId, StructuredContent structuredContent)
		throws Exception {

		JournalArticle journalArticle = _journalArticleService.getLatestArticle(
			structuredContentId);

		DDMStructure ddmStructure = journalArticle.getDDMStructure();
		LocalDateTime localDateTime = toLocalDateTime(
			structuredContent.getDatePublished(),
			journalArticle.getDisplayDate());

		String viewableBy = Optional.ofNullable(
			structuredContent.getViewableBy()
		).map(
			StructuredContent.ViewableBy::getValue
		).orElse(
			null
		);

		return _toStructuredContent(
			_journalArticleService.updateArticle(
				journalArticle.getGroupId(), journalArticle.getFolderId(),
				journalArticle.getArticleId(), journalArticle.getVersion(),
				LocalizedMapUtil.merge(
					journalArticle.getTitleMap(),
					new AbstractMap.SimpleEntry<>(
						contextAcceptLanguage.getPreferredLocale(),
						structuredContent.getTitle())),
				LocalizedMapUtil.merge(
					journalArticle.getDescriptionMap(),
					new AbstractMap.SimpleEntry<>(
						contextAcceptLanguage.getPreferredLocale(),
						structuredContent.getDescription())),
				LocalizedMapUtil.merge(
					journalArticle.getFriendlyURLMap(),
					new AbstractMap.SimpleEntry<>(
						contextAcceptLanguage.getPreferredLocale(),
						structuredContent.getTitle())),
				_journalConverter.getContent(
					ddmStructure,
					_toFields(
						structuredContent.getContentFields(), journalArticle)),
				journalArticle.getDDMStructureKey(),
				_getDDMTemplateKey(ddmStructure),
				journalArticle.getLayoutUuid(),
				localDateTime.getMonthValue() - 1,
				localDateTime.getDayOfMonth(), localDateTime.getYear(),
				localDateTime.getHour(), localDateTime.getMinute(), 0, 0, 0, 0,
				0, true, 0, 0, 0, 0, 0, true, true, false, null, null, null,
				null,
				ServiceContextUtil.createServiceContext(
					structuredContent.getKeywords(),
					structuredContent.getCategoryIds(),
					journalArticle.getGroupId(), viewableBy)));
	}

	private void _createFieldsDisplayValue(
		ContentField contentField, List<String> fieldsDisplayValue) {

		String repeatableId = contentField.getRepeatableId();

		if (repeatableId == null) {
			repeatableId = StringUtil.randomId();
		}

		fieldsDisplayValue.add(
			contentField.getName() + DDM.INSTANCE_SEPARATOR + repeatableId);

		for (ContentField nestedContentField : contentField.getNestedFields()) {
			_createFieldsDisplayValue(nestedContentField, fieldsDisplayValue);
		}
	}

	private String _createFieldsDisplayValue(ContentField[] contentFields) {
		List<String> fieldsDisplayValues = new ArrayList<>();

		for (ContentField contentField : contentFields) {
			_createFieldsDisplayValue(contentField, fieldsDisplayValues);
		}

		return String.join(",", fieldsDisplayValues);
	}

	private String _createJournalArticleContent(
			List<DDMFormFieldValue> ddmFormFieldValues,
			DDMStructure ddmStructure)
		throws Exception {

		Locale originalSiteDefaultLocale =
			LocaleThreadLocal.getSiteDefaultLocale();

		try {
			LocaleThreadLocal.setSiteDefaultLocale(
				LocaleUtil.fromLanguageId(ddmStructure.getDefaultLanguageId()));

			ServiceContext serviceContext = new ServiceContext();

			DDMForm ddmForm = ddmStructure.getDDMForm();

			serviceContext.setAttribute(
				"ddmFormValues",
				_toString(
					new DDMFormValues(ddmForm) {
						{
							setAvailableLocales(ddmForm.getAvailableLocales());
							setDDMFormFieldValues(ddmFormFieldValues);
							setDefaultLocale(ddmForm.getDefaultLocale());
						}
					}));

			return _journalConverter.getContent(
				ddmStructure,
				_ddm.getFields(ddmStructure.getStructureId(), serviceContext));
		}
		finally {
			LocaleThreadLocal.setSiteDefaultLocale(originalSiteDefaultLocale);
		}
	}

	private String _getDDMTemplateKey(DDMStructure ddmStructure) {
		List<DDMTemplate> ddmTemplates = ddmStructure.getTemplates();

		if (ddmTemplates.isEmpty()) {
			return StringPool.BLANK;
		}

		DDMTemplate ddmTemplate = ddmTemplates.get(0);

		return ddmTemplate.getTemplateKey();
	}

	private List<String> _getFieldDisplayValue(Fields fields) {
		Field field = fields.get(DDM.FIELDS_DISPLAY_NAME);

		String fieldDisplayValue = (String)field.getValue();

		return transformToList(
			StringUtil.split(fieldDisplayValue),
			value -> StringUtil.extractFirst(value, DDM.INSTANCE_SEPARATOR));
	}

	private List<List<String>> _getFieldsDisplaySubstrings(
		String fieldName, List<String> fieldDisplayNames) {

		List<List<String>> substrings = new ArrayList<>();

		int offset = 0;

		for (int i = 1; i < fieldDisplayNames.size(); i++) {
			if (fieldName.equals(fieldDisplayNames.get(i))) {
				substrings.add(fieldDisplayNames.subList(offset, i));

				offset = i;
			}
		}

		substrings.add(
			fieldDisplayNames.subList(offset, fieldDisplayNames.size()));

		return substrings;
	}

	private Page<StructuredContent> _getStructuredContentsPage(
			Long contentSpaceId, Long contentStructureId, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return SearchUtil.search(
			booleanQuery -> {
				if (contentStructureId != null) {
					BooleanFilter booleanFilter =
						booleanQuery.getPreBooleanFilter();

					booleanFilter.add(
						new TermFilter(
							com.liferay.portal.kernel.search.Field.
								CLASS_TYPE_ID,
							contentStructureId.toString()),
						BooleanClauseOccur.MUST);
				}
			},
			filter, JournalArticle.class, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				com.liferay.portal.kernel.search.Field.ARTICLE_ID,
				com.liferay.portal.kernel.search.Field.SCOPE_GROUP_ID),
			searchContext -> {
				searchContext.setAttribute(
					com.liferay.portal.kernel.search.Field.STATUS,
					WorkflowConstants.STATUS_APPROVED);
				searchContext.setAttribute("head", Boolean.TRUE);
				searchContext.setCompanyId(contextCompany.getCompanyId());

				if (contentSpaceId != null) {
					searchContext.setGroupIds(new long[] {contentSpaceId});
				}
			},
			document -> _toStructuredContent(
				_journalArticleService.getLatestArticle(
					GetterUtil.getLong(
						document.get(
							com.liferay.portal.kernel.search.Field.
								SCOPE_GROUP_ID)),
					document.get(
						com.liferay.portal.kernel.search.Field.ARTICLE_ID),
					WorkflowConstants.STATUS_APPROVED)),
			sorts);
	}

	private boolean _hasComments(JournalArticle journalArticle) {
		int count = _commentManager.getCommentsCount(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());

		if (count > 0) {
			return true;
		}

		return false;
	}

	private ContentField _toContentField(
			DDMFieldsCounter ddmFieldsCounter, DDMFormField ddmFormField,
			DDMStructure ddmStructure, Field field,
			List<String> fieldDisplayValues, Fields fields)
		throws Exception {

		return new ContentField() {
			{
				dataType = ContentStructureUtil.toDataType(ddmFormField);
				inputControl = ContentStructureUtil.toInputControl(
					ddmFormField);
				name = field.getName();
				nestedFields = _toContentFields(
					ddmFormField.getNestedDDMFormFields(),
					ddmFormField -> _toContentFields(
						ddmFieldsCounter, ddmStructure, fields,
						ddmFormField.getName(), fieldDisplayValues));
				value = _toValue(
					ddmFieldsCounter, field,
					contextAcceptLanguage.getPreferredLocale());

				setRepeatableId(
					() -> {
						Field field = fields.get(DDM.FIELDS_DISPLAY_NAME);

						String[] fieldsDisplayValues = StringUtil.split(
							(String)field.getValue());

						String fieldsDisplayValue = fieldsDisplayValues
							[ddmFieldsCounter.get(field.getName())];

						return StringUtil.extractLast(
							fieldsDisplayValue, DDM.INSTANCE_SEPARATOR);
					});
			}
		};
	}

	private ContentField[] _toContentFields(
			DDMFieldsCounter ddmFieldsCounter, DDMStructure ddmStructure,
			Fields fields, String fieldName, List<String> fieldDisplayValues)
		throws Exception {

		Field field = fields.get(fieldName);

		if (field == null) {
			return null;
		}

		DDMFormField ddmFormField = ddmStructure.getDDMFormField(fieldName);

		if (ddmFormField.isRepeatable()) {
			if (fieldDisplayValues.isEmpty()) {
				fieldDisplayValues = _getFieldDisplayValue(fields);
			}

			return _toRepeatableContentField(
				ddmFieldsCounter, ddmStructure, ddmFormField, fields,
				fieldDisplayValues);
		}

		return new ContentField[] {
			_toContentField(
				ddmFieldsCounter, ddmFormField, ddmStructure, field,
				fieldDisplayValues.subList(1, fieldDisplayValues.size()),
				fields)
		};
	}

	private ContentField[] _toContentFields(JournalArticle journalArticle)
		throws Exception {

		DDMFieldsCounter ddmFieldsCounter = new DDMFieldsCounter();

		DDMStructure ddmStructure = journalArticle.getDDMStructure();

		Fields fields = _journalConverter.getDDMFields(
			ddmStructure, journalArticle.getContent());

		return _toContentFields(
			ddmStructure.getRootFieldNames(),
			fieldName -> _toContentFields(
				ddmFieldsCounter, ddmStructure, fields, fieldName,
				Collections.emptyList()));
	}

	private <T> ContentField[] _toContentFields(
		List<T> list,
		UnsafeFunction<T, ContentField[], Exception> unsafeFunction) {

		Stream<T> stream = list.stream();

		return stream.map(
			t -> {
				try {
					return unsafeFunction.apply(t);
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		).filter(
			Objects::nonNull
		).flatMap(
			Stream::of
		).toArray(
			ContentField[]::new
		);
	}

	private List<DDMFormFieldValue> _toDDMFormFieldValues(
		ContentField[] contentFields, DDMStructure ddmStructure,
		Locale locale) {

		if (contentFields == null) {
			return Collections.emptyList();
		}

		return transform(
			Arrays.asList(contentFields),
			contentFieldValue -> new DDMFormFieldValue() {
				{
					setInstanceId(contentFieldValue.getRepeatableId());
					setName(contentFieldValue.getName());
					setNestedDDMFormFields(
						_toDDMFormFieldValues(
							contentFieldValue.getNestedFields(), ddmStructure,
							locale));
					setValue(
						_toDDMValue(contentFieldValue, ddmStructure, locale));
				}
			});
	}

	private com.liferay.dynamic.data.mapping.model.Value _toDDMValue(
			ContentField contentFieldValue, DDMStructure ddmStructure,
			Locale locale)
		throws Exception {

		DDMFormField ddmFormField = ddmStructure.getDDMFormField(
			contentFieldValue.getName());

		final Value value = contentFieldValue.getValue();

		if (ddmFormField.isLocalizable()) {
			return new LocalizedValue() {
				{
					if (Objects.equals(
							DDMFormFieldType.DOCUMENT_LIBRARY,
							ddmFormField.getType())) {

						FileEntry fileEntry = _dlAppService.getFileEntry(
							value.getDocumentId());

						addString(
							locale,
							JSONUtil.put(
								"alt", value.getData()
							).put(
								"classPK", fileEntry.getFileEntryId()
							).put(
								"fileEntryId", fileEntry.getFileEntryId()
							).put(
								"groupId", fileEntry.getGroupId()
							).put(
								"name", fileEntry.getFileName()
							).put(
								"resourcePrimKey", fileEntry.getPrimaryKey()
							).put(
								"title", fileEntry.getFileName()
							).put(
								"type", "document"
							).put(
								"uuid", fileEntry.getUuid()
							).toString());
					}
					else if (Objects.equals(
								DDMFormFieldType.JOURNAL_ARTICLE,
								ddmFormField.getType())) {

						JournalArticle journalArticle =
							_journalArticleService.getLatestArticle(
								value.getStructuredContentId());

						addString(
							locale,
							JSONUtil.put(
								"className", JournalArticle.class.getName()
							).put(
								"classPK", journalArticle.getResourcePrimKey()
							).put(
								"title", journalArticle.getTitle()
							).toString());
					}
					else {
						addString(locale, value.getData());
					}
				}
			};
		}

		if (Objects.equals(
				DDMFormFieldType.GEOLOCATION, ddmFormField.getType())) {

			Geo geo = value.getGeo();

			return new UnlocalizedValue(
				JSONUtil.put(
					"latitude", geo.getLatitude()
				).put(
					"longitude", geo.getLongitude()
				).toString());
		}

		return new UnlocalizedValue(value.getData());
	}

	private Fields _toFields(
			ContentField[] contentFields, JournalArticle journalArticle)
		throws Exception {

		DDMStructure ddmStructure = journalArticle.getDDMStructure();

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAttribute(
			"ddmFormValues",
			_toString(
				new DDMFormValues(ddmStructure.getDDMForm()) {
					{
						DDMForm ddmForm = getDDMForm();

						setAvailableLocales(ddmForm.getAvailableLocales());

						setDDMFormFieldValues(
							_toDDMFormFieldValues(
								contentFields, journalArticle.getDDMStructure(),
								contextAcceptLanguage.getPreferredLocale()));
						setDefaultLocale(ddmForm.getDefaultLocale());
					}
				}));

		Fields newFields = _ddm.getFields(
			ddmStructure.getStructureId(), serviceContext);

		Fields fields = _journalConverter.getDDMFields(
			journalArticle.getDDMStructure(), journalArticle.getContent());

		Iterator<Field> iterator = fields.iterator();

		while (iterator.hasNext()) {
			Field field = iterator.next();

			Field newField = newFields.get(field.getName());

			if (newField != null) {
				List<Serializable> values = newField.getValues(
					contextAcceptLanguage.getPreferredLocale());

				field.setValues(
					contextAcceptLanguage.getPreferredLocale(), values);
			}
		}

		Field field = fields.get(DDM.FIELDS_DISPLAY_NAME);

		field.setValue(
			contextAcceptLanguage.getPreferredLocale(),
			_createFieldsDisplayValue(contentFields));

		return fields;
	}

	private ContentField[] _toRepeatableContentField(
			DDMFieldsCounter ddmFieldsCounter, DDMStructure ddmStructure,
			DDMFormField ddmFormField, Fields fields,
			List<String> fieldDisplayValues)
		throws Exception {

		List<ContentField> contentFields = new ArrayList<>();

		List<List<String>> fieldsDisplaySubstrings =
			_getFieldsDisplaySubstrings(
				ddmFormField.getName(), fieldDisplayValues);

		for (List<String> substring : fieldsDisplaySubstrings) {
			contentFields.add(
				_toContentField(
					ddmFieldsCounter, ddmFormField, ddmStructure,
					fields.get(ddmFormField.getName()),
					substring.subList(1, substring.size()), fields));
		}

		return contentFields.toArray(new ContentField[0]);
	}

	private String _toString(DDMFormValues ddmFormValues) {
		DDMFormValuesSerializer ddmFormValuesSerializer =
			_ddmFormValuesSerializerTracker.getDDMFormValuesSerializer("json");

		DDMFormValuesSerializerSerializeRequest.Builder builder =
			DDMFormValuesSerializerSerializeRequest.Builder.newBuilder(
				ddmFormValues);

		DDMFormValuesSerializerSerializeResponse
			ddmFormValuesSerializerSerializeResponse =
				ddmFormValuesSerializer.serialize(builder.build());

		return ddmFormValuesSerializerSerializeResponse.getContent();
	}

	private StructuredContent _toStructuredContent(
			JournalArticle journalArticle)
		throws Exception {

		DDMStructure ddmStructure = journalArticle.getDDMStructure();

		return new StructuredContent() {
			{
				availableLanguages = LocaleUtil.toW3cLanguageIds(
					journalArticle.getAvailableLanguageIds());
				aggregateRating = AggregateRatingUtil.toAggregateRating(
					_ratingsStatsLocalService.fetchStats(
						JournalArticle.class.getName(),
						journalArticle.getResourcePrimKey()));
				categories = transformToArray(
					_assetCategoryLocalService.getCategories(
						JournalArticle.class.getName(),
						journalArticle.getResourcePrimKey()),
					assetCategory -> new Categories() {
						{
							categoryId = assetCategory.getCategoryId();
							categoryName = assetCategory.getName();
						}
					},
					Categories.class);
				contentFields = _toContentFields(journalArticle);
				contentSpace = journalArticle.getGroupId();
				contentStructureId = ddmStructure.getStructureId();
				creator = CreatorUtil.toCreator(
					_portal,
					_userLocalService.getUserById(journalArticle.getUserId()));
				dateCreated = journalArticle.getCreateDate();
				dateModified = journalArticle.getModifiedDate();
				datePublished = journalArticle.getDisplayDate();
				description = journalArticle.getDescription(
					contextAcceptLanguage.getPreferredLocale());
				hasComments = _hasComments(journalArticle);
				id = journalArticle.getResourcePrimKey();
				keywords = ListUtil.toArray(
					_assetTagLocalService.getTags(
						JournalArticle.class.getName(),
						journalArticle.getResourcePrimKey()),
					AssetTag.NAME_ACCESSOR);
				lastReviewed = journalArticle.getReviewDate();
				renderedContents = transformToArray(
					ddmStructure.getTemplates(),
					ddmTemplate -> new RenderedContents() {
						{
							renderedContentURL = getJAXRSLink(
								"getStructuredContentRenderedContentTemplate",
								journalArticle.getResourcePrimKey(),
								ddmTemplate.getTemplateId());
							templateName = ddmTemplate.getName(
								contextAcceptLanguage.getPreferredLocale());
						}
					},
					RenderedContents.class);
				title = journalArticle.getTitle(
					contextAcceptLanguage.getPreferredLocale());
			}
		};
	}

	private Value _toValue(
			DDMFieldsCounter ddmFieldsCounter, Field field, Locale locale)
		throws Exception {

		DDMStructure ddmStructure = field.getDDMStructure();

		DDMFormField ddmFormField = ddmStructure.getDDMFormField(
			field.getName());

		String value = String.valueOf(
			field.getValue(locale, ddmFieldsCounter.get(field.getName())));

		ddmFieldsCounter.incrementKey(field.getName());

		if (Objects.equals(
				DDMFormFieldType.DOCUMENT_LIBRARY, ddmFormField.getType())) {

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(value);

			long classPK = jsonObject.getLong("classPK");

			if (classPK == 0) {
				return null;
			}

			FileEntry fileEntry = _dlAppService.getFileEntry(classPK);

			return new Value() {
				{
					document = new ContentDocument() {
						{
							contentUrl = _dlurlHelper.getPreviewURL(
								fileEntry, fileEntry.getFileVersion(), null, "",
								false, false);
							encodingFormat = fileEntry.getMimeType();
							fileExtension = fileEntry.getExtension();
							id = fileEntry.getFileEntryId();
							sizeInBytes = fileEntry.getSize();
							title = fileEntry.getTitle();
						}
					};
				}
			};
		}

		if (Objects.equals(
				DDMFormFieldType.GEOLOCATION, ddmFormField.getType())) {

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(value);

			return new Value() {
				{
					geo = new Geo() {
						{
							latitude = jsonObject.getDouble("latitude");
							longitude = jsonObject.getDouble("longitude");
						}
					};
				}
			};
		}

		if (Objects.equals(DDMFormFieldType.IMAGE, ddmFormField.getType())) {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(value);

			long fileEntryId = jsonObject.getLong("fileEntryId");

			if (fileEntryId == 0) {
				return null;
			}

			FileEntry fileEntry = _dlAppService.getFileEntry(fileEntryId);

			return new Value() {
				{
					image = new StructuredContentImage() {
						{
							contentUrl = _dlurlHelper.getPreviewURL(
								fileEntry, fileEntry.getFileVersion(), null, "",
								false, false);
							description = jsonObject.getString("alt");
							encodingFormat = fileEntry.getMimeType();
							fileExtension = fileEntry.getExtension();
							id = fileEntry.getFileEntryId();
							sizeInBytes = fileEntry.getSize();
							title = fileEntry.getTitle();
						}
					};
				}
			};
		}

		if (Objects.equals(
				DDMFormFieldType.JOURNAL_ARTICLE, ddmFormField.getType())) {

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(value);

			long classPK = jsonObject.getLong("classPK");

			if (classPK == 0) {
				return null;
			}

			JournalArticle journalArticle =
				_journalArticleService.getLatestArticle(classPK);

			return new Value() {
				{
					structuredContentLink = new StructuredContentLink() {
						{
							id = journalArticle.getId();
							title = journalArticle.getTitle();
						}
					};
				}
			};
		}

		if (Objects.equals(
				DDMFormFieldType.LINK_TO_PAGE, ddmFormField.getType())) {

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(value);

			long layoutId = jsonObject.getLong("layoutId");

			if (layoutId == 0) {
				return null;
			}

			long groupId = jsonObject.getLong("groupId");
			boolean privateLayout = jsonObject.getBoolean("privateLayout");

			Layout layoutByUuidAndGroupId = _layoutLocalService.getLayout(
				groupId, privateLayout, layoutId);

			return new Value() {
				{
					link = layoutByUuidAndGroupId.getFriendlyURL();
				}
			};
		}

		return new Value() {
			{
				data = value;
			}
		};
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private CommentManager _commentManager;

	@Context
	private HttpServletRequest _contextHttpServletRequest;

	@Context
	private HttpServletResponse _contextHttpServletResponse;

	@Reference
	private DDM _ddm;

	@Reference
	private DDMFormValuesSerializerTracker _ddmFormValuesSerializerTracker;

	@Reference
	private DDMStructureService _ddmStructureService;

	@Reference
	private DDMTemplateService _ddmTemplateService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLURLHelper _dlurlHelper;

	@Reference
	private EntityFieldsProvider _entityFieldsProvider;

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private JournalContent _journalContent;

	@Reference
	private JournalConverter _journalConverter;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private RatingsStatsLocalService _ratingsStatsLocalService;

	@Reference
	private UserLocalService _userLocalService;

}