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
portletDisplay.setShowBackIcon(true);

LiferayPortletURL usersAdminURL = liferayPortletResponse.createLiferayPortletURL(UsersAdminPortletKeys.USERS_ADMIN, PortletRequest.RENDER_PHASE);

portletDisplay.setURLBack(usersAdminURL.toString());

renderResponse.setTitle(StringBundler.concat(selectedUser.getFullName(), " - ", LanguageUtil.get(request, "personal-data-erasure")));
%>

<clay:container-fluid
	cssClass="container-form-lg"
>
	<liferay-frontend:empty-result-message
		title='<%= LanguageUtil.get(resourceBundle, "you-have-successfully-anonymized-all-remaining-data") %>'
	/>
</clay:container-fluid>

<portlet:actionURL name="/user_associated_data/delete_user" var="deleteUserURL">
	<portlet:param name="p_u_i_d" value="<%= String.valueOf(selectedUser.getUserId()) %>" />
</portlet:actionURL>

<aui:script>
	Liferay.Util.openConfirmModal({
		message:
			'<%= UnicodeLanguageUtil.get(request, "all-personal-data-associated-with-this-users-applications-has-been-deleted-or-anonymized") %>',
		onConfirm: (isConfirmed) => {
			if (isConfirmed) {
				Liferay.Util.navigate('<%= deleteUserURL.toString() %>');
			}
		},
	});
</aui:script>