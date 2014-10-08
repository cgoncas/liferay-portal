<#include "../init.ftl">

<#assign latitude = "">
<#assign longitude = "">

<#assign coordinatesContainerCssClass = "hide">

<#assign fieldRawValue = paramUtil.getString(request, "${namespacedFieldName}", fieldRawValue)>

<#if (fieldRawValue != "")>
	<#assign geolocationJSONObject = jsonFactoryUtil.createJSONObject(fieldRawValue)>

	<#assign latitude = geolocationJSONObject.getDouble("latitude")>
	<#assign longitude = geolocationJSONObject.getDouble("longitude")>

	<#assign coordinatesContainerCssClass = "">
</#if>

<#assign group = themeDisplay.getScopeGroup() />

<#assign mapsAPIProvider = group.getLiveParentTypeSettingsProperty("mapsAPIProvider")!"" />

<#assign companyPortletPreferences = prefsPropsUtil.getPreferences(company.getCompanyId()) />

<#if mapsAPIProvider = "">
	<#assign mapsAPIProvider = companyPortletPreferences.getValue("mapsAPIProvider", "googleMaps") />
</#if>

<@aui["field-wrapper"] cssClass="geolocation-field" data=data label=label required=required>
	<@aui.input name=namespacedFieldName type="hidden" value=fieldRawValue />

	<div id="${portletNamespace}${namespacedFieldName}CoordinatesContainer" style="padding: 15px;">
		<div class="glyphicon glyphicon-map-marker" id="${portletNamespace}${namespacedFieldName}Location"></div>

		<@liferay_ui["map"] geolocation=true latitude=latitude longitude=longitude name=namespacedFieldName provider=mapsAPIProvider />
	</div>

	${fieldStructure.children}
</@>