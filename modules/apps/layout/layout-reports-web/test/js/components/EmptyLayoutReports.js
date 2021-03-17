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

import {cleanup, render} from '@testing-library/react';
import React from 'react';

import EmptyLayoutReports from '../../../src/main/resources/META-INF/resources/js/components/EmptyLayoutReports';

import '@testing-library/jest-dom/extend-expect';

const testProps = {
	assetsPath: 'assetsPath',
	configurePageSpeedURL: null,
};

describe('EmptyLayoutReports', () => {
	afterEach(cleanup);

	it('renders empty view with information', () => {
		const {getByText} = render(
			<EmptyLayoutReports
				assetsPath={testProps.assetsPath}
				configurePageSpeedURL={testProps.configurePageSpeedURL}
			/>
		);

		expect(
			getByText(
				"check-issues-that-impact-on-your-page's-accessibility-and-seo"
			)
		).toBeInTheDocument();
		expect(
			getByText(
				'to-run-a-page-audit,-connect-with-pagespeed-from-instance-settings-pages-pagespeed'
			)
		).toBeInTheDocument();
	});

	it('renders empty view with information and button', () => {
		const {getByText} = render(
			<EmptyLayoutReports
				assetsPath={testProps.assetsPath}
				configurePageSpeedURL="validURL"
			/>
		);

		expect(
			getByText(
				"check-issues-that-impact-on-your-page's-accessibility-and-seo"
			)
		).toBeInTheDocument();
		expect(
			getByText('connect-to-pagespeed-to-run-a-page-audit')
		).toBeInTheDocument();
		expect(getByText('connect-to-pagespeed')).toBeInTheDocument();
	});
});
