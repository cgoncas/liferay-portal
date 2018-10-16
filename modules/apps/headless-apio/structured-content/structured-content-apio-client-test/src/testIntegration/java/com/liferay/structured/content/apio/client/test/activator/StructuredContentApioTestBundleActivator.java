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

package com.liferay.structured.content.apio.client.test.activator;

import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleConstants;
import com.liferay.journal.model.JournalFolderConstants;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Ruben Pulido
 */
public class StructuredContentApioTestBundleActivator
	implements BundleActivator {

	public static final String NESTED_TEXT_FIELD_LABEL_LOCALE_ES =
		StructuredContentApioTestBundleActivator.class.getSimpleName() +
			"NestedTextFieldLabel_es";

	public static final String NESTED_TEXT_FIELD_LABEL_LOCALE_US =
		StructuredContentApioTestBundleActivator.class.getSimpleName() +
			"NestedTextFieldLabel_us";

	public static final String NESTED_TEXT_FIELD_NAME =
		StructuredContentApioTestBundleActivator.class.getSimpleName() +
			"NestedTextFieldName";

	public static final String NESTED_TEXT_FIELD_VALUE_LOCALE_ES =
		StructuredContentApioTestBundleActivator.class.getSimpleName() +
			"NestedTextFieldValue_es";

	public static final String NESTED_TEXT_FIELD_VALUE_LOCALE_US =
		StructuredContentApioTestBundleActivator.class.getSimpleName() +
			"NestedTextFieldValue_us";

	public static final String NOT_A_SITE_MEMBER_EMAIL_ADDRESS =
		StructuredContentApioTestBundleActivator.class.getSimpleName() +
			"NotASiteMemberUser@liferay.com";

	public static final String SITE_MEMBER_EMAIL_ADDRESS =
		StructuredContentApioTestBundleActivator.class.getSimpleName() +
			"SiteMemberUser@liferay.com";

	public static final String SITE_NAME =
		StructuredContentApioTestBundleActivator.class.getSimpleName() + "Site";

	public static final String TEXT_FIELD_LABEL_LOCALE_ES =
		StructuredContentApioTestBundleActivator.class.getSimpleName() +
			"TextFieldLabel_es";

	public static final String TEXT_FIELD_LABEL_LOCALE_US =
		StructuredContentApioTestBundleActivator.class.getSimpleName() +
			"TextFieldLabel_us";

	public static final String TEXT_FIELD_NAME =
		StructuredContentApioTestBundleActivator.class.getSimpleName() +
			"TextFieldName";

	public static final String TEXT_FIELD_VALUE_LOCALE_ES =
		StructuredContentApioTestBundleActivator.class.getSimpleName() +
			"TextFieldValue_es";

	public static final String TEXT_FIELD_VALUE_LOCALE_US =
		StructuredContentApioTestBundleActivator.class.getSimpleName() +
			"TextFieldValue_us";

	public static final String TITLE_1_LOCALE_ES =
		StructuredContentApioTestBundleActivator.class.getSimpleName() +
			"Title1_es";

	public static final String TITLE_2_LOCALE_DEFAULT =
		StructuredContentApioTestBundleActivator.class.getSimpleName() +
			"Title2_DefaultLocale";

	public static final String TITLE_2_LOCALE_ES =
		StructuredContentApioTestBundleActivator.class.getSimpleName() +
			"Title2_es";

	public static final String TITLE_NO_GUEST_NO_GROUP =
		StructuredContentApioTestBundleActivator.class.getSimpleName() +
			"NoGuestNoGroupTitle";

	public static final String TITLE_NO_GUEST_YES_GROUP =
		StructuredContentApioTestBundleActivator.class.getSimpleName() +
			"NoGuestYesGroupTitle";

	public static final String TITLE_YES_GUEST_YES_GROUP =
		StructuredContentApioTestBundleActivator.class.getSimpleName() +
			"YesGuestYesGroupTitle";

	@Override
	public void start(BundleContext bundleContext) {
		_autoCloseables = new ArrayList<>();

		try {
			_prepareTest();
		}
		catch (Exception e) {
			_cleanUp();

			throw new RuntimeException(e);
		}
	}

	@Override
	public void stop(BundleContext bundleContext) {
		_cleanUp();
	}

	private static void _addDynamicContentsToDynamicElement(
		Map<Locale, String> map, Element dynamicElementElement) {

		for (Map.Entry<Locale, String> entry : map.entrySet()) {
			Element element = dynamicElementElement.addElement(
				"dynamic-content");

			element.addAttribute(
				"language-id", LocaleUtil.toLanguageId(entry.getKey()));
			element.addCDATA(entry.getValue());
		}
	}

	private static Element _addDynamicElementToParentElement(
		String name, Element parentElement) {

		Element dynamicElementElement = parentElement.addElement(
			"dynamic-element");

		dynamicElementElement.addAttribute("index-type", "keyword");
		dynamicElementElement.addAttribute("name", name);
		dynamicElementElement.addAttribute("type", "text");

		return dynamicElementElement;
	}

	private static String _getSampleStructuredContent(
		String name, String keywords, String keywordsES, String nestedName,
		String nestedKeywords, String nestedKeywordsES) {

		Map<Locale, String> contents = new HashMap<>();

		contents.put(Locale.US, keywords);
		contents.put(LocaleUtil.SPAIN, keywordsES);

		Map<Locale, String> nestedContents = new HashMap<>();

		nestedContents.put(Locale.US, nestedKeywords);
		nestedContents.put(LocaleUtil.SPAIN, nestedKeywordsES);

		return _getStructuredContentWithNestedField(
			name, Collections.singletonList(contents), "en_US", nestedName,
			Collections.singletonList(nestedContents));
	}

	private static String _getStructuredContentWithNestedField(
		String name, List<Map<Locale, String>> contents, String defaultLocale,
		String nestedName, List<Map<Locale, String>> nestedContents) {

		StringBundler sb = new StringBundler();

		for (Map<Locale, String> map : contents) {
			for (Locale locale : map.keySet()) {
				sb.append(LocaleUtil.toLanguageId(locale));
				sb.append(StringPool.COMMA);
			}

			sb.setIndex(sb.index() - 1);
		}

		Document document = DDMTemplateTestUtil.createDocument(
			sb.toString(), defaultLocale);

		Element rootElement = document.getRootElement();

		Element dynamicElementElement = _addDynamicElementToParentElement(
			name, rootElement);

		for (Map<Locale, String> map : contents) {
			_addDynamicContentsToDynamicElement(map, dynamicElementElement);
		}

		Element nestedDynamicElementElement = _addDynamicElementToParentElement(
			nestedName, dynamicElementElement);

		for (Map<Locale, String> map : nestedContents) {
			_addDynamicContentsToDynamicElement(
				map, nestedDynamicElementElement);
		}

		return document.asXML();
	}

	private void _addDDMFormFields(DDMForm ddmForm) {
		LocalizedValue textFieldLabelLocalizedValue = _getLocalizedValue(
			TEXT_FIELD_LABEL_LOCALE_US, TEXT_FIELD_LABEL_LOCALE_ES);

		DDMFormField ddmFormField = _createDDMFormField(
			TEXT_FIELD_NAME, textFieldLabelLocalizedValue);

		LocalizedValue nestedTextFieldLabelLocalizedValue = _getLocalizedValue(
			NESTED_TEXT_FIELD_LABEL_LOCALE_US,
			NESTED_TEXT_FIELD_LABEL_LOCALE_ES);

		DDMFormField nestedDDMFormField = _createDDMFormField(
			NESTED_TEXT_FIELD_NAME, nestedTextFieldLabelLocalizedValue);

		List<DDMFormField> nestedDDMFormFields =
			ddmFormField.getNestedDDMFormFields();

		nestedDDMFormFields.add(nestedDDMFormField);

		DDMFormTestUtil.addDDMFormFields(ddmForm, ddmFormField);
	}

	private void _addDDMFormValues(DDMForm ddmForm) {
		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		Value textFieldLocalizedValue = _getLocalizedValue(
			TEXT_FIELD_VALUE_LOCALE_US, TEXT_FIELD_VALUE_LOCALE_ES);

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				TEXT_FIELD_NAME, textFieldLocalizedValue);

		List<DDMFormFieldValue> nestedDDMFormFieldValues =
			ddmFormFieldValue.getNestedDDMFormFieldValues();

		Value nestedTextFieldLocalizedValue = _getLocalizedValue(
			NESTED_TEXT_FIELD_VALUE_LOCALE_US,
			NESTED_TEXT_FIELD_VALUE_LOCALE_ES);

		nestedDDMFormFieldValues.add(
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				NESTED_TEXT_FIELD_NAME, nestedTextFieldLocalizedValue));

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);
	}

	private JournalArticle _addJournalArticle(
			Map<Locale, String> stringMap, long userId, long groupId,
			Locale defaultLocale, boolean addGuestPermissions,
			boolean addGroupPermissions)
		throws Exception {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(addGroupPermissions);
		serviceContext.setAddGuestPermissions(addGuestPermissions);
		serviceContext.setCompanyId(PortalUtil.getDefaultCompanyId());
		serviceContext.setScopeGroupId(groupId);
		serviceContext.setUserId(userId);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			groupId, JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			JournalArticleConstants.CLASSNAME_ID_DEFAULT, StringUtil.randomId(),
			false, stringMap, stringMap, stringMap, null, defaultLocale, null,
			true, true, serviceContext);

		_autoCloseables.add(
			() -> JournalArticleLocalServiceUtil.deleteArticle(journalArticle));

		return journalArticle;
	}

	private JournalArticle _addJournalArticle(
			String title, long userId, long groupId,
			boolean addGuestPermissions, boolean addGroupPermissions)
		throws Exception {

		Map<Locale, String> stringMap = new HashMap<Locale, String>() {
			{
				put(LocaleUtil.getDefault(), title);
			}
		};

		return _addJournalArticle(
			stringMap, userId, groupId, LocaleUtil.getDefault(),
			addGuestPermissions, addGroupPermissions);
	}

	private User _addUser(String emailAddress, long companyId, long groupId)
		throws Exception {

		User existingUser = UserLocalServiceUtil.fetchUserByEmailAddress(
			companyId, emailAddress);

		if (existingUser != null) {
			UserLocalServiceUtil.deleteUser(existingUser.getUserId());
		}

		User user = UserLocalServiceUtil.addUser(
			UserConstants.USER_ID_DEFAULT, companyId, false, Constants.TEST,
			Constants.TEST, true, StringUtil.randomString(20), emailAddress, 0,
			null, PortalUtil.getSiteDefaultLocale(groupId),
			StringUtil.randomString(20), null, StringUtil.randomString(10), 0,
			0, true, 1, 1, 2000, null, new long[] {groupId}, new long[0],
			new long[0], new long[0], false, new ServiceContext());

		_autoCloseables.add(
			() -> UserLocalServiceUtil.deleteUser(user.getUserId()));

		return user;
	}

	private void _cleanUp() {
		Collections.reverse(_autoCloseables);

		for (AutoCloseable autoCloseable : _autoCloseables) {
			try {
				autoCloseable.close();
			}
			catch (Exception e) {
				_log.error(e, e);
			}
		}
	}

	private DDMForm _createDDMForm() {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		Set<Locale> availableLocalesSet = SetUtil.fromArray(
			new Locale[] {LocaleUtil.US, LocaleUtil.SPAIN});

		ddmForm.setAvailableLocales(availableLocalesSet);

		ddmForm.setDefaultLocale(LocaleUtil.getDefault());

		return ddmForm;
	}

	private DDMFormField _createDDMFormField(
		String name, LocalizedValue label) {

		DDMFormField ddmFormField = new DDMFormField(name, "text");

		ddmFormField.setDataType("string");
		ddmFormField.setLocalizable(true);
		ddmFormField.setRepeatable(false);
		ddmFormField.setRequired(false);

		ddmFormField.setLabel(label);

		return ddmFormField;
	}

	private DDMStructure _createDDMStructure(long groupId) throws Exception {
		DDMForm ddmForm = _createDDMForm();

		_addDDMFormFields(ddmForm);

		_addDDMFormValues(ddmForm);

		return DDMStructureTestUtil.addStructure(
			groupId, JournalArticle.class.getName(), ddmForm);
	}

	private LocalizedValue _getLocalizedValue(String valueUS, String valueES) {
		LocalizedValue localizedValue = new LocalizedValue();

		localizedValue.addString(LocaleUtil.US, valueUS);
		localizedValue.addString(LocaleUtil.SPAIN, valueES);

		return localizedValue;
	}

	private String _getScript() {
		StringBundler sb = new StringBundler(5);

		sb.append("$");
		sb.append(TEXT_FIELD_NAME);
		sb.append(".getData(),$");
		sb.append(NESTED_TEXT_FIELD_NAME);
		sb.append(".getData()");

		return sb.toString();
	}

	private void _prepareDataForLocalizationTests(User user, Group group)
		throws Exception {

		Map<Locale, String> titleMap1 = new HashMap<Locale, String>() {
			{
				put(LocaleUtil.SPAIN, TITLE_1_LOCALE_ES);
			}
		};

		_addJournalArticle(
			titleMap1, user.getUserId(), group.getGroupId(), LocaleUtil.SPAIN,
			true, true);

		Map<Locale, String> titleMap2 = new HashMap<Locale, String>() {
			{
				put(LocaleUtil.getDefault(), TITLE_2_LOCALE_DEFAULT);
				put(LocaleUtil.SPAIN, TITLE_2_LOCALE_ES);
			}
		};

		_addJournalArticle(
			titleMap2, user.getUserId(), group.getGroupId(),
			LocaleUtil.getDefault(), true, true);
	}

	private void _prepareTest() throws Exception {
		User user = UserTestUtil.getAdminUser(TestPropsValues.getCompanyId());
		Map<Locale, String> nameMap = Collections.singletonMap(
			LocaleUtil.getDefault(), SITE_NAME);

		Group group = GroupLocalServiceUtil.addGroup(
			user.getUserId(), GroupConstants.DEFAULT_PARENT_GROUP_ID, null, 0,
			GroupConstants.DEFAULT_LIVE_GROUP_ID, nameMap, nameMap,
			GroupConstants.TYPE_SITE_OPEN, true,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION,
			StringPool.SLASH + FriendlyURLNormalizerUtil.normalize(SITE_NAME),
			true, true, ServiceContextTestUtil.getServiceContext());

		_autoCloseables.add(() -> GroupLocalServiceUtil.deleteGroup(group));

		_addUser(
			SITE_MEMBER_EMAIL_ADDRESS, TestPropsValues.getCompanyId(),
			group.getGroupId());

		_addUser(
			NOT_A_SITE_MEMBER_EMAIL_ADDRESS, TestPropsValues.getCompanyId(),
			GroupConstants.DEFAULT_LIVE_GROUP_ID);

		_addJournalArticle(
			TITLE_NO_GUEST_NO_GROUP, user.getUserId(), group.getGroupId(),
			false, false);

		_addJournalArticle(
			TITLE_NO_GUEST_YES_GROUP, user.getUserId(), group.getGroupId(),
			false, true);

		_addJournalArticle(
			TITLE_YES_GUEST_YES_GROUP, user.getUserId(), group.getGroupId(),
			true, true);

		_prepareDataForLocalizationTests(user, group);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StructuredContentApioTestBundleActivator.class);

	private List<AutoCloseable> _autoCloseables;

}