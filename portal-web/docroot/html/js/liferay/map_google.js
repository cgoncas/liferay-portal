AUI.add(
	'liferay-map-google',
	function(A) {
		var AArray = A.Array;

		var Lang = A.Lang;

		var MapBase = Liferay.MapBase;

		var CONTROLS_CONFIG_MAP = {};

		CONTROLS_CONFIG_MAP[MapBase.CONTROLS.OVERVIEW] = 'overviewMapControl';
		CONTROLS_CONFIG_MAP[MapBase.CONTROLS.PAN] = 'panControl';
		CONTROLS_CONFIG_MAP[MapBase.CONTROLS.ROTATE] = 'rotateControl';
		CONTROLS_CONFIG_MAP[MapBase.CONTROLS.SCALE] = 'scaleControl';
		CONTROLS_CONFIG_MAP[MapBase.CONTROLS.STREETVIEW] = 'streetViewControl';
		CONTROLS_CONFIG_MAP[MapBase.CONTROLS.TYPE] = 'mapTypeControl';
		CONTROLS_CONFIG_MAP[MapBase.CONTROLS.ZOOM] = 'zoomControl';

		var GoogleMap = A.Component.create(
			{
				AUGMENTS: [MapBase],

				EXTENDS: A.Widget,

				NAME: 'lfr-map-google',

				prototype: {
					addControl: function(control, position) {
						var instance = this;

						var map = instance._map;

						if (map.controls[position]) {
							map.controls[position].push(control.getDOMNode());
						}
					},

					addGeoJson: function(geoJson) {
						var instance = this;

						var map = instance._map;

						map.data.addGeoJson(geoJson);

						map.data.setStyle(
							function(feature) {
								return {
									icon: feature.getProperty('icon')
								};
							}
						);
					},

					addMarker: function() {
						var instance = this;

						var marker = new google.maps.Marker(
							{
								draggable: true,
								map: instance._map
							}
						);

						return marker;
					},

					panTo: function(location) {
						var instance = this;

						instance._map.setCenter(location);

						if (instance._mainMarker) {
							instance._mainMarker.setPosition(location);
						}
					},

					_bindMapUI: function() {
						var instance = this;

						var eventHandles = [];

						var searchControl = instance._customControls[MapBase.CONTROLS.SEARCH];

						if (searchControl) {
							var searchBox = new google.maps.places.Autocomplete(searchControl.one('.search-input').getDOMNode());

							eventHandles.push(
								google.maps.event.addListener(searchBox, 'place_changed', A.bind('_onPlaceChanged', instance, searchBox))
							);
						}

						var mainMarker = instance._mainMarker;

						if (mainMarker) {
							eventHandles.push(
								google.maps.event.addListener(mainMarker, 'dragend', A.bind('_onMainMarkerDragEnd', instance))
							);
						}

						google.maps.event.addListener(
							instance._map.data,
							'click',
							function(event) {
								var feature = event.feature;

								var infoWindow = feature.infoWindow;

								if (!infoWindow) {
									infoWindow = new google.maps.InfoWindow(
										{
											content: feature.getProperty('popupContent'),
											position: feature.getGeometry().location
										}
									);

									feature.infoWindow = infoWindow;
								}

								infoWindow.open(instance._map);
							}
						);

						instance._mapEventHandles = eventHandles;
					},

					_geocode: function(latitude, longitude) {
						var instance = this;

						var geocoder = instance._getGeocoder();

						geocoder.geocode(
							{
								location: {
									lat: latitude,
									lng: longitude
								}
							},
							function(results, status) {
								if (status == google.maps.GeocoderStatus.OK) {
									var result = results[0];

									var geometry = result.geometry;

									instance.set(
										'position',
										{
											address: result.formatted_address,
											location: {
												lat: geometry.location.lat(),
												lng: geometry.location.lng()
											}
										}
									);
								}
							}
						);
					},

					_getGeocoder: function() {
						var instance = this;

						var geocoder = instance._geocoder;

						if (!geocoder) {
							geocoder = new google.maps.Geocoder();

							instance._geocoder = geocoder;
						}

						return geocoder;
					},

					_createMap: function(latitude, longitude, controlsConfig) {
						var instance = this;

						var mapConfig = {
							center: {
								lat: latitude,
								lng: longitude
							},
							mapTypeId: google.maps.MapTypeId.ROADMAP,
							zoom: instance.get('zoom')
						};

						return new google.maps.Map(
							instance.get('boundingBox').getDOMNode(),
							A.merge(mapConfig, controlsConfig)
						);
					},

					_onMainMarkerDragEnd: function(event) {
						var instance = this;

						var location = event.latLng;

						instance._geocode(location.lat(), location.lng());
					},

					_onPlaceChanged: function(searchBox) {
						var instance = this;

						var place = searchBox.getPlace();

						if (A.Lang.isObject(place)) {
							var location = place.geometry.location;

							instance._geocode(location.lat(), location.lng());
						}
					},

					CONTROLS_CONFIG_MAP: CONTROLS_CONFIG_MAP
				}
			}
		);

		Liferay.GoogleMap = GoogleMap;
	},
	'',
	{
		requires: ['liferay-map-base']
	}
);