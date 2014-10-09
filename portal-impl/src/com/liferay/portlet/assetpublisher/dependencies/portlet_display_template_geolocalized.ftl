<#assign liferay_aui = taglibLiferayHash["/WEB-INF/tld/liferay-aui.tld"] />
<#assign liferay_portlet = taglibLiferayHash["/WEB-INF/tld/liferay-portlet.tld"] />
<#assign liferay_ui = taglibLiferayHash["/WEB-INF/tld/liferay-ui.tld"] />

<#assign defaultLatitude = -3.6833 />
<#assign defaultLongitude = 40.40 />

<#assign group = themeDisplay.getScopeGroup() />

<#assign mapsAPIProvider = group.getLiveParentTypeSettingsProperty("mapsAPIProvider")!"" />

<#assign companyPortletPreferences = prefsPropsUtil.getPreferences(companyId) />

<#if mapsAPIProvider = "">
	<#assign mapsAPIProvider = companyPortletPreferences.getValue("mapsAPIProvider", "Google") />
</#if>

<#assign jsonArray = jsonFactoryUtil.createJSONArray() />

<#list entries as entry>
	<#assign assetRenderer = entry.getAssetRenderer() />

	<#assign ddmReader = assetRenderer.getDDMFieldReader() />

	<#assign fields = ddmReader.getFields("geolocation") />

	<#list fields.iterator() as field>
		<#assign jsonObject = jsonFactoryUtil.createJSONObject(field.getValue()) />

		<@liferay.silently jsonObject.put("title", assetRenderer.getTitle(locale)) />

		<#assign entryAbstract>
			<@getAbstract asset = entry />
		</#assign>

		<@liferay.silently jsonObject.put("abstract", entryAbstract) />

		<#if mapsAPIProvider = "Google">
			<#assign
				images = {
					"com.liferay.portlet.documentlibrary.model.DLFileEntry": "${themeDisplay.getProtocol()}://maps.google.com/mapfiles/ms/icons/green-dot.png",
					"com.liferay.portlet.dynamicdatalists.model.DDLRecord": "${themeDisplay.getProtocol()}://maps.google.com/mapfiles/ms/icons/red-dot.png",
					"com.liferay.portlet.journal.model.JournalArticle": "${themeDisplay.getProtocol()}://maps.google.com/mapfiles/ms/icons/blue-dot.png",
					"default": "${themeDisplay.getProtocol()}://maps.google.com/mapfiles/ms/icons/yellow-dot.png"
				}
			/>

			<#if images?keys?seq_contains(entry.getClassName())>
				<@liferay.silently jsonObject.put("icon", images[entry.getClassName()]) />
			<#else>
				<@liferay.silently jsonObject.put("icon", images["default"]) />
			</#if>
		</#if>

		<@liferay.silently jsonArray.put(jsonObject) />
	</#list>
</#list>

<style type="text/css">
	#<@liferay_portlet.namespace />assetEntryAbstract {
		min-width: 400px;
	}

	#<@liferay_portlet.namespace />assetEntryAbstract .asset-entry-abstract-image {
		float: left;
	}

	#<@liferay_portlet.namespace />assetEntryAbstract .asset-entry-abstract-image img {
		display: block;
		margin-right: 2em;
	}

	#<@liferay_portlet.namespace />assetEntryAbstract .taglib-icon {
		float: right;
	}

	#<@liferay_portlet.namespace />mapCanvas {
		min-height: 400px;
	}

	#<@liferay_portlet.namespace />mapCanvas img {
		max-width: none;
	}
</style>

<#assign apiKey = group.getLiveParentTypeSettingsProperty("googleMapsAPIKey")!"" />

<#if apiKey = "">
	<#assign apiKey = companyPortletPreferences.getValue("googleMapsAPIKey", "") />
</#if>

<@liferay_ui["map"] points="${jsonArray}" name=namespacedFieldName provider=mapsAPIProvider />

<#macro getAbstract asset>
	<div class="asset-entry-abstract" id="<@liferay_portlet.namespace />assetEntryAbstract">
		<#assign showEditURL = paramUtil.getBoolean(renderRequest, "showEditURL", true) />

		<#assign assetRenderer = asset.getAssetRenderer() />

		<#if showEditURL && assetRenderer.hasEditPermission(permissionChecker)>
			<#assign redirectURL = renderResponse.createLiferayPortletURL(themeDisplay.getPlid(), themeDisplay.getPortletDisplay().getId(), "RENDER_PHASE", false) />

			${redirectURL.setParameter("struts_action", "/asset_publisher/add_asset_redirect")}

			<#assign editPortletURL = assetRenderer.getURLEdit(renderRequest, renderResponse, windowStateFactory.getWindowState("POP_UP"), redirectURL) />

			<#assign taglibEditURL = "javascript:Liferay.Util.openWindow({id: '" + renderResponse.getNamespace() + "editAsset', title: '" + htmlUtil.escapeJS(languageUtil.format(locale, "edit-x", htmlUtil.escape(assetRenderer.getTitle(locale)), false)) + "', uri:'" + htmlUtil.escapeJS(editPortletURL.toString()) + "'});" />

			<@liferay_ui.icon
				image = "edit"
				label = true
				message = "edit"
				url = taglibEditURL
			/>
		</#if>

		<div class="asset-entry-abstract-image">
			<img src="${assetRenderer.getThumbnailPath(renderRequest)}" />
		</div>

		<#assign assetURL = assetPublisherHelper.getAssetViewURL(renderRequest, renderResponse, asset) />

		<div class="asset-entry-abstract-content">
			<h3><a href="${assetURL}">${assetRenderer.getTitle(locale)}</a></h3>

			<div>
				${assetRenderer.getSummary(renderRequest, renderResponse)}
			</div>
		</div>

		<div class="asset-entry-abstract-footer">
			<a href="${assetURL}">${languageUtil.get(locale, "read-more")} &raquo;</a>
		</div>
	</div>
</#macro>