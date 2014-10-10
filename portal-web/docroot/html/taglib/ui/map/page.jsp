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

<%@ include file="/html/taglib/init.jsp" %>

<%
String points = GetterUtil.getString(request.getAttribute("liferay-ui:map:points"));
boolean geolocation = GetterUtil.getBoolean(request.getAttribute("liferay-ui:map:geolocation"));
double latitude = (Double)request.getAttribute("liferay-ui:map:latitude");
double longitude = (Double)request.getAttribute("liferay-ui:map:longitude");
String name = GetterUtil.getString((String)request.getAttribute("liferay-ui:map:name"));
String mapsApiProvider = GetterUtil.getString((String)request.getAttribute("liferay-ui:map:provider"));
boolean skipMapLoading = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:map:skipMapLoading"));

name = namespace + name;

String modules = "liferay-map-" + mapsApiProvider.toLowerCase();
%>

<c:if test='<%= mapsApiProvider.equals("Google") %>'>
	<liferay-util:html-top outputKey="js_maps_google_skip_map_loading">
		<script src="https://maps.googleapis.com/maps/api/js?v=3.exp&libraries=places&apiKey=" type="text/javascript"></script>
	</liferay-util:html-top>
</c:if>

<c:if test='<%= mapsApiProvider.equals("Openstreet") %>'>
	<liferay-util:html-top outputKey="js_maps_openstreet_skip_loading">
		<link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.css" />
		<script src="http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.js"></script>
	</liferay-util:html-top>
</c:if>

<div id="<%= name %>Map"></div>

<aui:script use="<%= modules %>">
	var features = [];

	A.Array.each(
		A.Array(<%= points %>),
		function(item) {
			if (item) {
				features.push(
					{
						type: 'Feature',
						geometry: {
							type: 'Point',
							coordinates: [parseFloat(item.longitude), parseFloat(item.latitude)]
						},
						properties: {
							abstract: item.abstract,
							icon: item.icon,
							title: item.title
						}
					}
				);
			}
		}
	);

	var featureCollection = {
		type: 'FeatureCollection',
		features: features
	};

	var mapConfig = {
		boundingBox: '#<%= name %>Map',
		controls: [],
		data: featureCollection,
		geolocation: <%= geolocation %>
	};

	<c:if test="<%= geolocation %>">
		var MapControls = Liferay.MapBase.CONTROLS;

		mapConfig.controls = [MapControls.GEOLOCATION, MapControls.HOME, MapControls.PAN, MapControls.SEARCH, MapControls.TYPE, MapControls.ZOOM];
	</c:if>

	<c:if test="<%= Validator.isNotNull(latitude) && Validator.isNotNull(longitude) %>">
		mapConfig.position = {
			location: {
				lat: <%= latitude %>,
				lng: <%= longitude %>
			}
		};
	</c:if>

	var map = new Liferay["<%= mapsApiProvider %>Map"](mapConfig).render();

	Liferay.component('<%= name %>', map);
</aui:script>