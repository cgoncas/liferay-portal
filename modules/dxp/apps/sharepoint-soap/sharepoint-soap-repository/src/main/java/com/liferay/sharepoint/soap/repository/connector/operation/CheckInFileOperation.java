/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.sharepoint.soap.repository.connector.operation;

import com.liferay.sharepoint.soap.repository.connector.SharepointConnection;
import com.liferay.sharepoint.soap.repository.connector.SharepointException;
import com.liferay.sharepoint.soap.repository.connector.internal.util.RemoteExceptionSharepointExceptionMapper;

import java.rmi.RemoteException;

/**
 * @author Iván Zaera
 */
public final class CheckInFileOperation extends BaseOperation {

	public boolean execute(
			String filePath, String comment,
			SharepointConnection.CheckInType checkInType)
		throws SharepointException {

		try {
			return listsSoap.checkInFile(
				String.valueOf(toURL(filePath)), comment,
				String.valueOf(checkInType.getProtocolValue()));
		}
		catch (RemoteException remoteException) {
			throw RemoteExceptionSharepointExceptionMapper.map(remoteException);
		}
	}

}