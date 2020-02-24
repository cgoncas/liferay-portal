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
DepotAdminRolesDisplayContext depotAdminRolesDisplayContext = new DepotAdminRolesDisplayContext(request);
%>

<aui:input name="<%= ActionRequest.ACTION_NAME %>" type="hidden" value="/depot/dummy" />
<aui:input name="DEPOT_COMMAND" type="hidden" value="roles" />

<h3 class="autofit-row sheet-subtitle">
	<span class="autofit-col autofit-col-expand">
		<span class="heading-text"><%= depotAdminRolesDisplayContext.getLabel() %></span>
	</span>
</h3>

<liferay-ui:search-container
	compactEmptyResultsMessage="<%= true %>"
	cssClass="lfr-search-container-depot-roles"
	curParam="depotRolesCur"
	emptyResultsMessage="this-user-is-not-assigned-any-asset-library-roles"
	headerNames="title,asset-library,null"
	iteratorURL="<%= currentURLObj %>"
	total="<%= depotAdminRolesDisplayContext.getUserGroupRolesCount() %>"
>
	<liferay-ui:search-container-results
		results="<%= depotAdminRolesDisplayContext.getUserGroupRoles(searchContainer.getStart(), searchContainer.getResultEnd()) %>"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.portal.kernel.model.UserGroupRole"
		escapedModel="<%= true %>"
		keyProperty="roleId"
		modelVar="userGroupRole"
	>
		<liferay-ui:search-container-column-text
			cssClass="table-cell-content"
			name="title"
		>
			<liferay-ui:icon
				iconCssClass="<%= RolesAdminUtil.getIconCssClass(userGroupRole.getRole()) %>"
				label="<%= true %>"
				message="<%= HtmlUtil.escape(userGroupRole.getRole().getTitle(locale)) %>"
			/>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-content"
			name="<%= depotAdminRolesDisplayContext.getAssetLibraryLabel() %>"
		>
			<liferay-staging:descriptive-name
				group="<%= userGroupRole.getGroup() %>"
			/>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
	/>
</liferay-ui:search-container>