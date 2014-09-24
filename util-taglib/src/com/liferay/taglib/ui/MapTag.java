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

package com.liferay.taglib.ui;

import com.liferay.taglib.util.IncludeTag;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Chema Balsas
 */
public class MapTag extends IncludeTag {

	public void setGeoCode(boolean geocode) {
		_geocode = geocode;
	}

	public void setLatitude(String latitude) {
		_latitude = latitude;
	}

	public void setLongitude(String longitude) {
		_longitude = longitude;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setPoints(String points) {
		_points = points;
	}

	public void setProvider(String provider) {
		_provider = provider;
	}

	@Override
	protected void cleanUp() {
		_geocode = false;
		_latitude = null;
		_longitude = null;
		_name =  null;
		_page = null;
		_points = null;
		_provider = "gmaps";
	}

	@Override
	protected String getPage() {
		return _page;
	}

	@Override
	protected void setAttributes(HttpServletRequest request) {
		_page = "/html/js/maps/" + _provider + ".jsp";

		request.setAttribute("liferay-ui:input-editor:geocode", _geocode);
		request.setAttribute("liferay-ui:input-editor:latitude", _latitude);
		request.setAttribute("liferay-ui:input-editor:longitude", _longitude);
		request.setAttribute("liferay-ui:input-editor:name", _name);
		request.setAttribute("liferay-ui:input-editor:points", _points);
		request.setAttribute("liferay-ui:input-editor:provider", _provider);
	}

	private boolean _geocode = false;
	private String _latitude;
	private String _longitude;
	private String _name;
	private String _page;
	private String _points;
	private String _provider = "gmaps";

}