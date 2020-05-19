
<%@ page import="com.liferay.content.dashboard.web.internal.display.context.ContentDashboardAdminManagementToolbarDisplayContext" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %>

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

<%@ include file="/init.jsp" %>

<%
ContentDashboardViewDisplayContext contentDashboardViewDisplayContext = (ContentDashboardViewDisplayContext)request.getAttribute(ContentDashboardPortletKeys.CONTENT_DASHBOARD_VIEW_DISPLAY_CONTEXT);

ContentDashboardAdminManagementToolbarDisplayContext contentDashboardAdminManagementToolbarDisplayContext = (ContentDashboardAdminManagementToolbarDisplayContext)request.getAttribute(ContentDashboardPortletKeys.CONTENT_DASHBOARD_ADMIN_MANAGEMENT_TOOLBAR_DISPLAY_CONTEXT);
%>

<clay:container
	className="main-content-body"
>
	<div class="sheet">
		<h2 class="sheet-title">
			<%= LanguageUtil.format(request, "content-x", contentDashboardAdminManagementToolbarDisplayContext.getItemsTotal(), false) %>
		</h2>

		<clay:management-toolbar
			displayContext="<%= contentDashboardAdminManagementToolbarDisplayContext %>"
		/>

		<div class="sheet-section">
			<liferay-ui:search-container
				id="contents"
				searchContainer="<%= contentDashboardViewDisplayContext.getSearchContainer() %>"
			>
				<liferay-ui:search-container-row
					className="com.liferay.content.dashboard.web.internal.display.ContentDashboardInfoItemDisplay"
					keyProperty="id"
					modelVar="contentDashboardInfoItemDisplay"
				>
					<liferay-ui:search-container-column-text
						name="title"
						value="<%= HtmlUtil.escape(contentDashboardInfoItemDisplay.getTitle(locale)) %>"
					/>

					<liferay-ui:search-container-column-text
						name="type"
						value="<%= HtmlUtil.escape(contentDashboardInfoItemDisplay.getType(locale)) %>"
					/>

					<liferay-ui:search-container-column-text
						name="status"
					>
						<clay:label
							label="<%= StringUtil.toUpperCase(contentDashboardInfoItemDisplay.getStatusLabel(locale)) %>"
							style="<%= contentDashboardInfoItemDisplay.getStatusStyle() %>"
						/>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-date
						name="publish-date"
						value="<%= contentDashboardInfoItemDisplay.getPublishDate() %>"
					/>

					<liferay-ui:search-container-column-date
						name="modified-date"
						value="<%= contentDashboardInfoItemDisplay.getModifiedDate() %>"
					/>

					<liferay-ui:search-container-column-date
						name="expired-date"
						value="<%= contentDashboardInfoItemDisplay.getExpiredDate() %>"
					/>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					markupView="lexicon"
				/>
			</liferay-ui:search-container>
		</div>
	</div>
</clay:container>