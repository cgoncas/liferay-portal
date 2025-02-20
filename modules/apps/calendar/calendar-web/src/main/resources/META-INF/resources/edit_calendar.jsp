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
String tabs2 = ParamUtil.getString(request, "tabs2", "general");

String redirect = ParamUtil.getString(request, "redirect");

if (Validator.isNull(redirect)) {
	redirect = PortalUtil.getCurrentURL(request);
}

String backURL = ParamUtil.getString(request, "backURL");

Calendar calendar = (Calendar)request.getAttribute(CalendarWebKeys.CALENDAR);

CalendarResource calendarResource = (CalendarResource)request.getAttribute(CalendarWebKeys.CALENDAR_RESOURCE);

if (calendarResource == null) {
	calendarResource = calendar.getCalendarResource();
}

String calendarName = null;

if (calendar != null) {
	calendarName = calendar.getName(locale);
}

String calendarResourceName = calendarResource.getName(locale);

if (Validator.isNotNull(calendarName) && !calendarName.equals(calendarResourceName)) {
	calendarName = calendarResourceName + StringPool.SPACE + StringPool.DASH + StringPool.SPACE + calendarName;
}

String calendarId = (calendar != null) ? String.valueOf(calendar.getCalendarId()) : StringPool.BLANK;
String calendarResourceId = (calendarResource != null) ? String.valueOf(calendarResource.getCalendarResourceId()) : StringPool.BLANK;

PortletURL navigationURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCPath(
	"/edit_calendar.jsp"
).setRedirect(
	redirect
).setBackURL(
	backURL
).setParameter(
	"calendarId", calendarId
).setParameter(
	"calendarResourceId", calendarResourceId
).buildPortletURL();
%>

<clay:navigation-bar
	navigationItems='<%=
		new JSPNavigationItemList(pageContext) {
			{
				navigationURL.setParameter("tabs2", "general");

				add(
					navigationItem -> {
						navigationItem.setActive(tabs2.equals("general"));
						navigationItem.setHref(navigationURL.toString());
						navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "general"));
					});

				if (calendar != null) {
					navigationURL.setParameter("tabs2", "notification-templates");

					add(
						navigationItem -> {
							navigationItem.setActive(tabs2.equals("notification-templates"));
							navigationItem.setHref(navigationURL.toString());
							navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "notification-templates"));
						});
				}
			}
		}
	%>'
/>

<c:choose>
	<c:when test='<%= tabs2.equals("general") %>'>
		<%@ include file="/edit_calendar_general.jspf" %>
	</c:when>
	<c:when test='<%= tabs2.equals("notification-templates") %>'>
		<%@ include file="/edit_calendar_notification_templates.jspf" %>
	</c:when>
</c:choose>