$TextFieldName.getData(),$NestedTextFieldName.getData()

<#if getterUtil.getBoolean(MyBoolean.getData())>  </#if>

${MyColor.getData()}

<#assign MyDate_Data = getterUtil.getString(MyDate.getData())> <#if validator.isNotNull(MyDate_Data)> <#assign MyDate_DateObj = dateUtil.parseDate("yyyy-MM-dd", MyDate_Data, locale)> ${dateUtil.getDate(MyDate_DateObj, "dd MMM yyyy - HH:mm:ss", locale)} </#if>

${MyDecimal.getData()}

<a href="${MyDocumentsAndMedia.getData()}"> ${languageUtil.format(locale, "download-x", "Documents and Media", false)} </a>


<#assign latitude = 0> <#assign longitude = 0> <#if (MyGeolocation.getData() != "")> <#assign geolocationJSONObject = jsonFactoryUtil.createJSONObject(MyGeolocation.getData())> <#assign latitude = geolocationJSONObject.getDouble("latitude")> <#assign longitude = geolocationJSONObject.getDouble("longitude")> <@liferay_map["map-display"] geolocation=true latitude=latitude longitude=longitude name="MyGeolocation${randomizer.nextInt()}" /> </#if>
