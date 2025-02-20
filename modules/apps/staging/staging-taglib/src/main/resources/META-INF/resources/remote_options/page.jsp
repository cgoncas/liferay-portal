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

<%@ include file="/remote_options/init.jsp" %>

<div id="<portlet:namespace />remote">
	<div class="alert alert-info">
		<liferay-ui:message arguments="<%= StringUtil.toLowerCase(liveGroup.getScopeSimpleName(themeDisplay)) %>" key="export-the-selected-data-to-the-x-of-a-remote-portal-or-to-another-x-in-the-same-portal" translateArguments="<%= false %>" />
	</div>

	<aui:fieldset>
		<aui:input disabled="<%= disableInputs %>" label="remote-host-ip" name="remoteAddress" size="20" type="text" value='<%= MapUtil.getString(settingsMap, "remoteAddress", liveGroupTypeSettingsUnicodeProperties.getProperty("remoteAddress")) %>' />

		<aui:input disabled="<%= disableInputs %>" label="remote-port" name="remotePort" size="10" type="text" value='<%= MapUtil.getString(settingsMap, "remotePort", liveGroupTypeSettingsUnicodeProperties.getProperty("remotePort")) %>' />

		<aui:input disabled="<%= disableInputs %>" label="remote-path-context" name="remotePathContext" size="10" type="text" value='<%= MapUtil.getString(settingsMap, "remotePathContext", liveGroupTypeSettingsUnicodeProperties.getProperty("remotePathContext")) %>' />

		<aui:input disabled="<%= disableInputs %>" label='<%= LanguageUtil.format(request, "remote-x-id", liveGroup.getScopeSimpleName(themeDisplay), false) %>' name="remoteGroupId" size="10" type="text" value='<%= MapUtil.getString(settingsMap, "targetGroupId", liveGroupTypeSettingsUnicodeProperties.getProperty("remoteGroupId")) %>' />

		<aui:input disabled="<%= disableInputs %>" name="remotePrivateLayout" type="hidden" value='<%= MapUtil.getBoolean(settingsMap, "remotePrivateLayout", privateLayout) %>' />
	</aui:fieldset>

	<aui:fieldset>
		<aui:input disabled="<%= disableInputs %>" label="use-a-secure-network-connection" name="secureConnection" type="checkbox" value='<%= MapUtil.getString(settingsMap, "secureConnection", liveGroupTypeSettingsUnicodeProperties.getProperty("secureConnection")) %>' />
	</aui:fieldset>
</div>