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

package com.liferay.mentions.web.internal.notifications;

import com.liferay.mentions.constants.MentionsConstants;
import com.liferay.mentions.constants.MentionsPortletKeys;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.notifications.UserNotificationDefinition;
import com.liferay.portal.kernel.notifications.UserNotificationDeliveryType;

import org.osgi.service.component.annotations.Component;

/**
 * @author Sergio González
 */
@Component(
	property = "javax.portlet.name=" + MentionsPortletKeys.MENTIONS,
	service = UserNotificationDefinition.class
)
public class MentionsUserNotificationDefinition
	extends UserNotificationDefinition {

	public MentionsUserNotificationDefinition() {
		super(
			MentionsPortletKeys.MENTIONS, 0,
			MentionsConstants.NOTIFICATION_TYPE_MENTION,
			"receive-a-notification-when-someone-mentions-you-in-a-blogs-" +
				"entry,-comment,-or-message-boards-message");

		addUserNotificationDeliveryType(
			new UserNotificationDeliveryType(
				"email", UserNotificationDeliveryConstants.TYPE_EMAIL, true,
				true));
		addUserNotificationDeliveryType(
			new UserNotificationDeliveryType(
				"website", UserNotificationDeliveryConstants.TYPE_WEBSITE, true,
				true));
	}

}