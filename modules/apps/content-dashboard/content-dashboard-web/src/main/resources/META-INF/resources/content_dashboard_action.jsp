<%@ page import="com.liferay.portal.kernel.dao.search.ResultRow" %>
<%@ page import="com.liferay.portal.kernel.util.WebKeys" %>
<%@ page import="com.liferay.petra.string.StringPool" %>
<%@ page
	import="com.liferay.content.dashboard.web.internal.info.item.ContentDashboardInfoItem" %><%--
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
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);

ContentDashboardAdminDisplayContext contentDashboardAdminDisplayContext = (ContentDashboardAdminDisplayContext)request.getAttribute(ContentDashboardPortletKeys.CONTENT_DASHBOARD_ADMIN_DISPLAY_CONTEXT);


ContentDashboardInfoItem contentDashboardInfoItem = (ContentDashboardInfoItem)row.getObject();
%>

<liferay-ui:icon-menu
	direction="left-side"
	icon="<%= StringPool.BLANK %>"
	markupView="lexicon"
	message='<%= LanguageUtil.get(request, "actions") %>'
	showWhenSingleIcon="<%= true %>"
>

		<liferay-ui:icon
			message="edit"
			url="<%= contentDashboardAdminDisplayContext.getEditURL(contentDashboardInfoItem) %>"
		/>

</liferay-ui:icon-menu>