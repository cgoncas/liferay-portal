$TextFieldName.getData(),$NestedTextFieldName.getData()

<#if getterUtil.getBoolean(MyBoolean.getData())>  </#if>

${MyColor.getData()}

<#assign MyDate_Data = getterUtil.getString(MyDate.getData())> <#if validator.isNotNull(MyDate_Data)> <#assign MyDate_DateObj = dateUtil.parseDate("yyyy-MM-dd", MyDate_Data, locale)> ${dateUtil.getDate(MyDate_DateObj, "dd MMM yyyy - HH:mm:ss", locale)} </#if>