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

import ClayAlert from '@clayui/alert';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayLayout from '@clayui/layout';
import ClaySticker from '@clayui/sticker';
import ClayTabs from '@clayui/tabs';
import classnames from 'classnames';
import {getSessionValue} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useCallback, useLayoutEffect, useState} from 'react';

import {
	DEFAULT_ACTIVE_PANEL_TAB,
	TABS_STATE_SESSION_KEY,
} from '../../utils/constants';
import Sidebar from '../Sidebar';
import DetailsContent from './DetailsContent';
import ManageCollaborators from './ManageCollaborators';
import Subscribe from './Subscribe';
import VersionsContent from './VersionsContent';

const useInitialActiveTabState = (singlePageApplicationEnabled) => {
	const [activeTab, setActiveTab] = useState(
		singlePageApplicationEnabled ? null : DEFAULT_ACTIVE_PANEL_TAB
	);

	useLayoutEffect(() => {
		if (singlePageApplicationEnabled) {
			getSessionValue(TABS_STATE_SESSION_KEY).then((value) => {
				const parsedValue = parseInt(value, 10);
				const safeValue = isNaN(parsedValue)
					? DEFAULT_ACTIVE_PANEL_TAB
					: parsedValue;

				setActiveTab(safeValue);
			});
		}
	}, [singlePageApplicationEnabled]);

	return [activeTab, setActiveTab];
};

const SidebarPanelInfoView = ({
	classPK,
	createDate,
	description,
	downloadURL,
	getItemVersionsURL,
	languageTag = 'en',
	latestVersions = [],
	modifiedDate,
	specificFields = {},
	subscribe,
	subType,
	tags = [],
	title,
	type,
	preview,
	fetchSharingButtonURL,
	fetchSharingCollaboratorsURL,
	singlePageApplicationEnabled,
	user,
	viewURLs = [],
	vocabularies = {},
}) => {
	const [activeTabKeyValue, setActiveTabKeyValue] = useInitialActiveTabState(
		singlePageApplicationEnabled
	);

	const showTabs = !!getItemVersionsURL;

	const [error, setError] = useState(false);

	const stickerColor = parseInt(user.userId, 10) % 10;

	const handleError = useCallback(() => {
		setError(true);
	}, []);

	const handleTabClick = (tab) => {
		setActiveTabKeyValue(tab);

		if (singlePageApplicationEnabled) {
			Liferay.Util.Session.set(TABS_STATE_SESSION_KEY, tab);
		}
	};

	return (
		<>
			<Sidebar.Header
				actionsSlot={subscribe && <Subscribe {...subscribe} />}
				title={title}
			>
				<ClayLayout.ContentRow>
					<div>
						{error && (
							<ClayAlert
								className="mb-3"
								displayType="warning"
								onClose={() => {
									setError(false);
								}}
								variant="stripe"
							>
								{Liferay.Language.get(
									'there-was-a-problem-retrieving-data-please-try-reloading-the-page'
								)}
							</ClayAlert>
						)}

						<div className="sidebar-section sidebar-section--compress">
							<p
								className="c-mb-1 text-secondary"
								data-qa-id="assetTypeInfo"
							>
								{subType ? `${type} - ${subType}` : `${type}`}
							</p>

							{latestVersions.map((latestVersion) => (
								<div
									className="c-mt-2"
									key={latestVersion.version}
								>
									<ClayLabel displayType="info">
										{Liferay.Language.get('version') + ' '}

										{latestVersion.version}
									</ClayLabel>

									<ClayLabel
										displayType={latestVersion.statusStyle}
									>
										{latestVersion.statusLabel}
									</ClayLabel>
								</div>
							))}
						</div>

						<div className="sidebar-section">
							{fetchSharingCollaboratorsURL ? (
								<ManageCollaborators
									fetchSharingCollaboratorsURL={
										fetchSharingCollaboratorsURL
									}
									onError={handleError}
								/>
							) : (
								<>
									<ClaySticker
										className={classnames(
											'sticker-user-icon',
											{
												[`user-icon-color-${stickerColor}`]: !user.url,
											}
										)}
										shape="circle"
									>
										{user.url ? (
											<img
												alt={`${user.name}.`}
												className="sticker-img"
												src={user.url}
											/>
										) : (
											<ClayIcon symbol="user" />
										)}
									</ClaySticker>
									<span className="c-ml-2 text-secondary">
										{user.name}
									</span>
								</>
							)}
						</div>

						<div className="mb-0 sidebar-section">
							{showTabs && activeTabKeyValue !== null && (
								<ClayTabs modern>
									<ClayTabs.Item
										active={activeTabKeyValue === 0}
										innerProps={{
											'aria-controls': 'details',
										}}
										onClick={() => handleTabClick(0)}
									>
										{Liferay.Language.get('details')}
									</ClayTabs.Item>

									<ClayTabs.Item
										active={activeTabKeyValue === 1}
										innerProps={{
											'aria-controls': 'versions',
										}}
										onClick={() => handleTabClick(1)}
									>
										{Liferay.Language.get('versions')}
									</ClayTabs.Item>
								</ClayTabs>
							)}
						</div>
					</div>
				</ClayLayout.ContentRow>
			</Sidebar.Header>

			<Sidebar.Body>
				<div>
					<ClayTabs.Content activeIndex={activeTabKeyValue} fade>
						<ClayTabs.TabPane aria-labelledby="tab-1">
							<DetailsContent
								classPK={classPK}
								createDate={createDate}
								description={description}
								downloadURL={downloadURL}
								fetchSharingButtonURL={fetchSharingButtonURL}
								languageTag={languageTag}
								modifiedDate={modifiedDate}
								preview={preview}
								specificFields={specificFields}
								tags={tags}
								title={title}
								viewURLs={viewURLs}
								vocabularies={vocabularies}
							/>
						</ClayTabs.TabPane>

						{showTabs && (
							<ClayTabs.TabPane aria-labelledby="tab-2">
								<VersionsContent
									active={activeTabKeyValue === 1}
									getItemVersionsURL={getItemVersionsURL}
									languageTag={languageTag}
									onError={handleError}
								/>
							</ClayTabs.TabPane>
						)}
					</ClayTabs.Content>
				</div>
			</Sidebar.Body>
		</>
	);
};

SidebarPanelInfoView.defaultProps = {
	description: '',
	languageTag: 'en-US',
	propTypes: [],
	vocabularies: {},
};

SidebarPanelInfoView.propTypes = {
	classPK: PropTypes.string.isRequired,
	createDate: PropTypes.string.isRequired,
	description: PropTypes.string,
	fetchSharingButtonURL: PropTypes.string,
	fetchSharingCollaboratorsURL: PropTypes.string,
	getItemVersionsURL: PropTypes.string,
	latestVersions: PropTypes.array.isRequired,
	modifiedDate: PropTypes.string.isRequired,
	preview: PropTypes.object,
	singlePageApplicationEnabled: PropTypes.bool.isRequired,
	specificFields: PropTypes.object.isRequired,
	subType: PropTypes.string.isRequired,
	tags: PropTypes.array,
	title: PropTypes.string.isRequired,
	user: PropTypes.object.isRequired,
	viewURLs: PropTypes.array.isRequired,
	vocabularies: PropTypes.object,
};

export default SidebarPanelInfoView;
