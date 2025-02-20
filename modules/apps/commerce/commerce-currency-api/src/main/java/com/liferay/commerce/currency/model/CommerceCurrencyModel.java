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

package com.liferay.commerce.currency.model;

import com.liferay.portal.kernel.bean.AutoEscape;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.LocalizedModel;
import com.liferay.portal.kernel.model.MVCCModel;
import com.liferay.portal.kernel.model.ShardedModel;
import com.liferay.portal.kernel.model.StagedAuditedModel;

import java.math.BigDecimal;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The base model interface for the CommerceCurrency service. Represents a row in the &quot;CommerceCurrency&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This interface and its corresponding implementation <code>com.liferay.commerce.currency.model.impl.CommerceCurrencyModelImpl</code> exist only as a container for the default property accessors generated by ServiceBuilder. Helper methods and all application logic should be put in <code>com.liferay.commerce.currency.model.impl.CommerceCurrencyImpl</code>.
 * </p>
 *
 * @author Andrea Di Giorgi
 * @see CommerceCurrency
 * @generated
 */
@ProviderType
public interface CommerceCurrencyModel
	extends BaseModel<CommerceCurrency>, LocalizedModel, MVCCModel,
			ShardedModel, StagedAuditedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. All methods that expect a commerce currency model instance should use the {@link CommerceCurrency} interface instead.
	 */

	/**
	 * Returns the primary key of this commerce currency.
	 *
	 * @return the primary key of this commerce currency
	 */
	public long getPrimaryKey();

	/**
	 * Sets the primary key of this commerce currency.
	 *
	 * @param primaryKey the primary key of this commerce currency
	 */
	public void setPrimaryKey(long primaryKey);

	/**
	 * Returns the mvcc version of this commerce currency.
	 *
	 * @return the mvcc version of this commerce currency
	 */
	@Override
	public long getMvccVersion();

	/**
	 * Sets the mvcc version of this commerce currency.
	 *
	 * @param mvccVersion the mvcc version of this commerce currency
	 */
	@Override
	public void setMvccVersion(long mvccVersion);

	/**
	 * Returns the uuid of this commerce currency.
	 *
	 * @return the uuid of this commerce currency
	 */
	@AutoEscape
	@Override
	public String getUuid();

	/**
	 * Sets the uuid of this commerce currency.
	 *
	 * @param uuid the uuid of this commerce currency
	 */
	@Override
	public void setUuid(String uuid);

	/**
	 * Returns the commerce currency ID of this commerce currency.
	 *
	 * @return the commerce currency ID of this commerce currency
	 */
	public long getCommerceCurrencyId();

	/**
	 * Sets the commerce currency ID of this commerce currency.
	 *
	 * @param commerceCurrencyId the commerce currency ID of this commerce currency
	 */
	public void setCommerceCurrencyId(long commerceCurrencyId);

	/**
	 * Returns the company ID of this commerce currency.
	 *
	 * @return the company ID of this commerce currency
	 */
	@Override
	public long getCompanyId();

	/**
	 * Sets the company ID of this commerce currency.
	 *
	 * @param companyId the company ID of this commerce currency
	 */
	@Override
	public void setCompanyId(long companyId);

	/**
	 * Returns the user ID of this commerce currency.
	 *
	 * @return the user ID of this commerce currency
	 */
	@Override
	public long getUserId();

	/**
	 * Sets the user ID of this commerce currency.
	 *
	 * @param userId the user ID of this commerce currency
	 */
	@Override
	public void setUserId(long userId);

	/**
	 * Returns the user uuid of this commerce currency.
	 *
	 * @return the user uuid of this commerce currency
	 */
	@Override
	public String getUserUuid();

	/**
	 * Sets the user uuid of this commerce currency.
	 *
	 * @param userUuid the user uuid of this commerce currency
	 */
	@Override
	public void setUserUuid(String userUuid);

	/**
	 * Returns the user name of this commerce currency.
	 *
	 * @return the user name of this commerce currency
	 */
	@AutoEscape
	@Override
	public String getUserName();

	/**
	 * Sets the user name of this commerce currency.
	 *
	 * @param userName the user name of this commerce currency
	 */
	@Override
	public void setUserName(String userName);

	/**
	 * Returns the create date of this commerce currency.
	 *
	 * @return the create date of this commerce currency
	 */
	@Override
	public Date getCreateDate();

	/**
	 * Sets the create date of this commerce currency.
	 *
	 * @param createDate the create date of this commerce currency
	 */
	@Override
	public void setCreateDate(Date createDate);

	/**
	 * Returns the modified date of this commerce currency.
	 *
	 * @return the modified date of this commerce currency
	 */
	@Override
	public Date getModifiedDate();

	/**
	 * Sets the modified date of this commerce currency.
	 *
	 * @param modifiedDate the modified date of this commerce currency
	 */
	@Override
	public void setModifiedDate(Date modifiedDate);

	/**
	 * Returns the code of this commerce currency.
	 *
	 * @return the code of this commerce currency
	 */
	@AutoEscape
	public String getCode();

	/**
	 * Sets the code of this commerce currency.
	 *
	 * @param code the code of this commerce currency
	 */
	public void setCode(String code);

	/**
	 * Returns the name of this commerce currency.
	 *
	 * @return the name of this commerce currency
	 */
	public String getName();

	/**
	 * Returns the localized name of this commerce currency in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized name of this commerce currency
	 */
	@AutoEscape
	public String getName(Locale locale);

	/**
	 * Returns the localized name of this commerce currency in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this commerce currency. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@AutoEscape
	public String getName(Locale locale, boolean useDefault);

	/**
	 * Returns the localized name of this commerce currency in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized name of this commerce currency
	 */
	@AutoEscape
	public String getName(String languageId);

	/**
	 * Returns the localized name of this commerce currency in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized name of this commerce currency
	 */
	@AutoEscape
	public String getName(String languageId, boolean useDefault);

	@AutoEscape
	public String getNameCurrentLanguageId();

	@AutoEscape
	public String getNameCurrentValue();

	/**
	 * Returns a map of the locales and localized names of this commerce currency.
	 *
	 * @return the locales and localized names of this commerce currency
	 */
	public Map<Locale, String> getNameMap();

	/**
	 * Sets the name of this commerce currency.
	 *
	 * @param name the name of this commerce currency
	 */
	public void setName(String name);

	/**
	 * Sets the localized name of this commerce currency in the language.
	 *
	 * @param name the localized name of this commerce currency
	 * @param locale the locale of the language
	 */
	public void setName(String name, Locale locale);

	/**
	 * Sets the localized name of this commerce currency in the language, and sets the default locale.
	 *
	 * @param name the localized name of this commerce currency
	 * @param locale the locale of the language
	 * @param defaultLocale the default locale
	 */
	public void setName(String name, Locale locale, Locale defaultLocale);

	public void setNameCurrentLanguageId(String languageId);

	/**
	 * Sets the localized names of this commerce currency from the map of locales and localized names.
	 *
	 * @param nameMap the locales and localized names of this commerce currency
	 */
	public void setNameMap(Map<Locale, String> nameMap);

	/**
	 * Sets the localized names of this commerce currency from the map of locales and localized names, and sets the default locale.
	 *
	 * @param nameMap the locales and localized names of this commerce currency
	 * @param defaultLocale the default locale
	 */
	public void setNameMap(Map<Locale, String> nameMap, Locale defaultLocale);

	/**
	 * Returns the symbol of this commerce currency.
	 *
	 * @return the symbol of this commerce currency
	 */
	@AutoEscape
	public String getSymbol();

	/**
	 * Sets the symbol of this commerce currency.
	 *
	 * @param symbol the symbol of this commerce currency
	 */
	public void setSymbol(String symbol);

	/**
	 * Returns the rate of this commerce currency.
	 *
	 * @return the rate of this commerce currency
	 */
	public BigDecimal getRate();

	/**
	 * Sets the rate of this commerce currency.
	 *
	 * @param rate the rate of this commerce currency
	 */
	public void setRate(BigDecimal rate);

	/**
	 * Returns the format pattern of this commerce currency.
	 *
	 * @return the format pattern of this commerce currency
	 */
	public String getFormatPattern();

	/**
	 * Returns the localized format pattern of this commerce currency in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param locale the locale of the language
	 * @return the localized format pattern of this commerce currency
	 */
	@AutoEscape
	public String getFormatPattern(Locale locale);

	/**
	 * Returns the localized format pattern of this commerce currency in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param locale the local of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized format pattern of this commerce currency. If <code>useDefault</code> is <code>false</code> and no localization exists for the requested language, an empty string will be returned.
	 */
	@AutoEscape
	public String getFormatPattern(Locale locale, boolean useDefault);

	/**
	 * Returns the localized format pattern of this commerce currency in the language. Uses the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @return the localized format pattern of this commerce currency
	 */
	@AutoEscape
	public String getFormatPattern(String languageId);

	/**
	 * Returns the localized format pattern of this commerce currency in the language, optionally using the default language if no localization exists for the requested language.
	 *
	 * @param languageId the ID of the language
	 * @param useDefault whether to use the default language if no localization exists for the requested language
	 * @return the localized format pattern of this commerce currency
	 */
	@AutoEscape
	public String getFormatPattern(String languageId, boolean useDefault);

	@AutoEscape
	public String getFormatPatternCurrentLanguageId();

	@AutoEscape
	public String getFormatPatternCurrentValue();

	/**
	 * Returns a map of the locales and localized format patterns of this commerce currency.
	 *
	 * @return the locales and localized format patterns of this commerce currency
	 */
	public Map<Locale, String> getFormatPatternMap();

	/**
	 * Sets the format pattern of this commerce currency.
	 *
	 * @param formatPattern the format pattern of this commerce currency
	 */
	public void setFormatPattern(String formatPattern);

	/**
	 * Sets the localized format pattern of this commerce currency in the language.
	 *
	 * @param formatPattern the localized format pattern of this commerce currency
	 * @param locale the locale of the language
	 */
	public void setFormatPattern(String formatPattern, Locale locale);

	/**
	 * Sets the localized format pattern of this commerce currency in the language, and sets the default locale.
	 *
	 * @param formatPattern the localized format pattern of this commerce currency
	 * @param locale the locale of the language
	 * @param defaultLocale the default locale
	 */
	public void setFormatPattern(
		String formatPattern, Locale locale, Locale defaultLocale);

	public void setFormatPatternCurrentLanguageId(String languageId);

	/**
	 * Sets the localized format patterns of this commerce currency from the map of locales and localized format patterns.
	 *
	 * @param formatPatternMap the locales and localized format patterns of this commerce currency
	 */
	public void setFormatPatternMap(Map<Locale, String> formatPatternMap);

	/**
	 * Sets the localized format patterns of this commerce currency from the map of locales and localized format patterns, and sets the default locale.
	 *
	 * @param formatPatternMap the locales and localized format patterns of this commerce currency
	 * @param defaultLocale the default locale
	 */
	public void setFormatPatternMap(
		Map<Locale, String> formatPatternMap, Locale defaultLocale);

	/**
	 * Returns the max fraction digits of this commerce currency.
	 *
	 * @return the max fraction digits of this commerce currency
	 */
	public int getMaxFractionDigits();

	/**
	 * Sets the max fraction digits of this commerce currency.
	 *
	 * @param maxFractionDigits the max fraction digits of this commerce currency
	 */
	public void setMaxFractionDigits(int maxFractionDigits);

	/**
	 * Returns the min fraction digits of this commerce currency.
	 *
	 * @return the min fraction digits of this commerce currency
	 */
	public int getMinFractionDigits();

	/**
	 * Sets the min fraction digits of this commerce currency.
	 *
	 * @param minFractionDigits the min fraction digits of this commerce currency
	 */
	public void setMinFractionDigits(int minFractionDigits);

	/**
	 * Returns the rounding mode of this commerce currency.
	 *
	 * @return the rounding mode of this commerce currency
	 */
	@AutoEscape
	public String getRoundingMode();

	/**
	 * Sets the rounding mode of this commerce currency.
	 *
	 * @param roundingMode the rounding mode of this commerce currency
	 */
	public void setRoundingMode(String roundingMode);

	/**
	 * Returns the primary of this commerce currency.
	 *
	 * @return the primary of this commerce currency
	 */
	public boolean getPrimary();

	/**
	 * Returns <code>true</code> if this commerce currency is primary.
	 *
	 * @return <code>true</code> if this commerce currency is primary; <code>false</code> otherwise
	 */
	public boolean isPrimary();

	/**
	 * Sets whether this commerce currency is primary.
	 *
	 * @param primary the primary of this commerce currency
	 */
	public void setPrimary(boolean primary);

	/**
	 * Returns the priority of this commerce currency.
	 *
	 * @return the priority of this commerce currency
	 */
	public double getPriority();

	/**
	 * Sets the priority of this commerce currency.
	 *
	 * @param priority the priority of this commerce currency
	 */
	public void setPriority(double priority);

	/**
	 * Returns the active of this commerce currency.
	 *
	 * @return the active of this commerce currency
	 */
	public boolean getActive();

	/**
	 * Returns <code>true</code> if this commerce currency is active.
	 *
	 * @return <code>true</code> if this commerce currency is active; <code>false</code> otherwise
	 */
	public boolean isActive();

	/**
	 * Sets whether this commerce currency is active.
	 *
	 * @param active the active of this commerce currency
	 */
	public void setActive(boolean active);

	/**
	 * Returns the last publish date of this commerce currency.
	 *
	 * @return the last publish date of this commerce currency
	 */
	public Date getLastPublishDate();

	/**
	 * Sets the last publish date of this commerce currency.
	 *
	 * @param lastPublishDate the last publish date of this commerce currency
	 */
	public void setLastPublishDate(Date lastPublishDate);

	@Override
	public String[] getAvailableLanguageIds();

	@Override
	public String getDefaultLanguageId();

	@Override
	public void prepareLocalizedFieldsForImport() throws LocaleException;

	@Override
	public void prepareLocalizedFieldsForImport(Locale defaultImportLocale)
		throws LocaleException;

	@Override
	public CommerceCurrency cloneWithOriginalValues();

	public default String toXmlString() {
		return null;
	}

}