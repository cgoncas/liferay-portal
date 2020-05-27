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
ContentDashboardAdminDisplayContext contentDashboardAdminDisplayContext = (ContentDashboardAdminDisplayContext)request.getAttribute(ContentDashboardPortletKeys.CONTENT_DASHBOARD_ADMIN_DISPLAY_CONTEXT);

ContentDashboardAdminManagementToolbarDisplayContext contentDashboardAdminManagementToolbarDisplayContext = (ContentDashboardAdminManagementToolbarDisplayContext)request.getAttribute(ContentDashboardPortletKeys.CONTENT_DASHBOARD_ADMIN_MANAGEMENT_TOOLBAR_DISPLAY_CONTEXT);
%>

<clay:container
	className="main-content-body"
>
	<div class="sheet">
		<h2 class="sheet-title">
			<%= LanguageUtil.format(request, "number-of-contents-per-audience-and-stage-x", 0, false) %>
		</h2>

		<div id="audit-graph">
			<div class="inline-item my-5 p-5 w-100">
				<span aria-hidden="true" class="loading-animation"></span>
			</div>

			<react:component
				module="js/AuditGraphApp"
			/>
		</div>
	</div>
</clay:container>

<clay:container
	className="main-content-body"
>
	<div class="sheet">
		<h2 class="sheet-title">
			<%= LanguageUtil.format(request, "content-x", 0, false) %>
		</h2>

		<clay:management-toolbar
			displayContext="<%= contentDashboardAdminManagementToolbarDisplayContext %>"
		/>

		<div class="sheet-section">
			<liferay-ui:search-container
				id="contents"
				rowChecker="<%= new EmptyOnClickRowChecker(renderResponse) %>"
				searchContainer="<%= contentDashboardAdminDisplayContext.getSearchContainer() %>"
			>
				<liferay-ui:search-container-row
					className="com.liferay.content.dashboard.web.internal.info.item.ContentDashboardInfoItem"
					keyProperty="id"
					modelVar="contentDashboardInfoItem"
				>
					<liferay-ui:search-container-column-text
						name="author"
					>
						<liferay-ui:user-portrait
							userId="<%= contentDashboardInfoItem.getUserId() %>"
						/>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text
						name="title"
						value="<%= HtmlUtil.escape(contentDashboardInfoItem.getTitle(locale)) %>"
					/>

					<liferay-ui:search-container-column-text
						name="type"
						value="<%= HtmlUtil.escape(contentDashboardInfoItem.getType(locale)) %>"
					/>

					<liferay-ui:search-container-column-text
						name="status"
					>
						<clay:label
							label="<%= StringUtil.toUpperCase(contentDashboardInfoItem.getStatusLabel(locale)) %>"
							style="<%= contentDashboardInfoItem.getStatusStyle() %>"
						/>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-date
						name="publish-date"
						value="<%= contentDashboardInfoItem.getPublishDate() %>"
					/>

					<liferay-ui:search-container-column-date
						name="modified-date"
						value="<%= contentDashboardInfoItem.getModifiedDate() %>"
					/>

					<liferay-ui:search-container-column-date
						name="expiration-date"
						value="<%= contentDashboardInfoItem.getExpiredDate() %>"
					/>

				<liferay-ui:search-container-column-jsp
					cssClass="entry-action"
					path="/content_dashboard_action.jsp"
				/>

				</liferay-ui:search-container-row>


				<liferay-ui:search-iterator
					markupView="lexicon"
				/>
			</liferay-ui:search-container>
		</div>
	</div>
</clay:container>