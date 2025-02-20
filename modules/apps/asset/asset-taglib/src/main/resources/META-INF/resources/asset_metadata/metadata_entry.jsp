<%--
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
--%>

<%@ include file="/asset_metadata/init.jsp" %>

<%
AssetEntry assetEntry = (AssetEntry)request.getAttribute("liferay-asset:asset-metadata:assetEntry");
AssetRenderer<?> assetRenderer = (AssetRenderer<?>)request.getAttribute("liferay-asset:asset-metadata:assetRenderer");
boolean filterByMetadata = GetterUtil.getBoolean(request.getAttribute("liferay-asset:asset-metadata:filterByMetadata"));

String metadataField = (String)request.getAttribute("liferay-asset:asset-metadata:metadataField");

String label = LanguageUtil.get(resourceBundle, metadataField);
String metadataFieldCssClass = "metadata-" + metadataField;

boolean showLabel = true;
String value = null;

if (metadataField.equals("author")) {
	showLabel = false;
	value = "author";
}
else if (metadataField.equals("categories")) {
	List<AssetCategory> assetCategories = assetEntry.getCategories();

	showLabel = false;

	if (!assetCategories.isEmpty()) {
		value = "categories";
	}
}
else if (metadataField.equals("create-date")) {
	value = dateFormatDate.format(assetEntry.getCreateDate());
}
else if (metadataField.equals("expiration-date")) {
	if (assetEntry.getExpirationDate() == null) {
		value = StringPool.BLANK;
	}
	else {
		value = dateFormatDate.format(assetEntry.getExpirationDate());
	}
}
else if (metadataField.equals("modified-date")) {
	value = dateFormatDate.format(assetEntry.getModifiedDate());
}
else if (metadataField.equals("priority")) {
	value = LanguageUtil.get(resourceBundle, "priority") + StringPool.COLON + StringPool.SPACE + assetEntry.getPriority();
}
else if (metadataField.equals("publish-date")) {
	if (assetEntry.getPublishDate() == null) {
		value = StringPool.BLANK;
	}
	else {
		value = dateFormatDate.format(assetEntry.getPublishDate());
	}
}
else if (metadataField.equals("tags")) {
	List<AssetTag> assetTags = assetEntry.getTags();

	showLabel = false;

	if (!assetTags.isEmpty()) {
		value = "tags";
	}
}
else if (metadataField.equals("view-count")) {
	long viewCount = assetEntry.getViewCount();

	value = viewCount + StringPool.SPACE + LanguageUtil.get(resourceBundle, (viewCount == 1) ? "view" : "views");
}
%>

<c:choose>
	<c:when test='<%= Objects.equals(value, "author") %>'>

		<%
		User assetRendererUser = UserLocalServiceUtil.fetchUser(assetRenderer.getUserId());

		String displayDate = StringPool.BLANK;

		if (assetEntry.getPublishDate() != null) {
			Date publishDate = assetEntry.getPublishDate();

			displayDate = LanguageUtil.format(request, "x-ago", LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - publishDate.getTime(), true), false);
		}
		else if (assetEntry.getModifiedDate() != null) {
			Date modifiedDate = assetEntry.getModifiedDate();

			displayDate = LanguageUtil.format(request, "x-ago", LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - modifiedDate.getTime(), true), false);
		}
		%>

		<div class="autofit-padded autofit-row">
			<div class="autofit-col">
				<liferay-ui:user-portrait
					user="<%= assetRendererUser %>"
				/>
			</div>

			<div class="autofit-col autofit-col-expand">
				<c:choose>
					<c:when test="<%= assetRendererUser != null %>">
						<h4 class="component-title mt-0"><%= HtmlUtil.escape(assetRendererUser.getFullName()) %></h4>
					</c:when>
					<c:otherwise>
						<h4 class="component-title mt-0"><liferay-ui:message key="anonymous"></liferay-ui:message></h4>
					</c:otherwise>
				</c:choose>

				<h5 class="component-subtitle"><%= displayDate %></h5>
			</div>
		</div>
	</c:when>
	<c:when test="<%= Validator.isNotNull(value) %>">
		<dt class="metadata-entry-label <%= showLabel ? StringPool.BLANK : "hide" %>"><%= label %></dt>

		<dd class="metadata-entry <%= metadataFieldCssClass %>">
			<c:choose>
				<c:when test='<%= value.equals("categories") %>'>
					<liferay-asset:asset-categories-summary
						className="<%= assetEntry.getClassName() %>"
						classPK="<%= assetEntry.getClassPK() %>"
						portletURL="<%= filterByMetadata ? renderResponse.createRenderURL() : null %>"
					/>
				</c:when>
				<c:when test='<%= value.equals("tags") %>'>
					<liferay-asset:asset-tags-summary
						className="<%= assetEntry.getClassName() %>"
						classPK="<%= assetEntry.getClassPK() %>"
						portletURL="<%= filterByMetadata ? renderResponse.createRenderURL() : null %>"
					/>
				</c:when>
				<c:otherwise>
					<%= value %>
				</c:otherwise>
			</c:choose>
		</dd>
	</c:when>
</c:choose>