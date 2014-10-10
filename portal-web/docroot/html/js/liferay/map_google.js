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

		var GoogleMapMarker = A.Component.create(
			{
				ATTRS: {
					config: {
						validator: Lang.isObject
					},

					map: {
						validator: Lang.isObject
					}
				},

				AUGMENTS: [Liferay.MapMarkerBase],

				EXTENDS: A.Base,

				NAME: 'lfr-map-marker-google',

				prototype: {
					_bindNativeMarker: function(nativeMarker) {
						var instance = this;

						google.maps.event.addListener(nativeMarker, 'click', instance._getNativeEventFn('click'));
						google.maps.event.addListener(nativeMarker, 'dblclick', instance._getNativeEventFn('dblclick'));
						google.maps.event.addListener(nativeMarker, 'drag', instance._getNativeEventFn('drag'));
						google.maps.event.addListener(nativeMarker, 'dragend', instance._getNativeEventFn('dragend'));
						google.maps.event.addListener(nativeMarker, 'dragstart', instance._getNativeEventFn('dragstart'));
						google.maps.event.addListener(nativeMarker, 'mousedown', instance._getNativeEventFn('mousedown'));
						google.maps.event.addListener(nativeMarker, 'mouseout', instance._getNativeEventFn('mouseout'));
						google.maps.event.addListener(nativeMarker, 'mouseover', instance._getNativeEventFn('mouseover'));
					},

					_createNativeMarker: function(location, map) {
						var instance = this;

						var nativeMarker = new google.maps.Marker(
							{
								draggable: true,
								position: location,
								map: map
							}
						);

						return nativeMarker;
					},

					_normalizeEvent: function(nativeEvent) {
						var instance = this;

						return {
							location: {
								lat: nativeEvent.latLng.lat(),
								lng: nativeEvent.latLng.lng()
							}
						}
					}
				}
			}
		);

		var GoogleMap = A.Component.create(
			{
				AUGMENTS: [MapBase],

				EXTENDS: A.Widget,

				NAME: 'lfr-map-google',

				prototype: {
					MarkerImpl: GoogleMapMarker,

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

					panTo: function(location) {
						var instance = this;

						instance._map.setCenter(location);
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

						google.maps.event.addListener(
							instance._map.data,
							'click',
							function(event) {
								var feature = event.feature;

								var infoWindow = feature.infoWindow;

								if (!infoWindow) {
									infoWindow = new google.maps.InfoWindow(
										{
											content: feature.getProperty('abstract'),
											pixelOffset: new google.maps.Size(0, -30),
											position: feature.getGeometry().get('location')
										}
									);

									feature.infoWindow = infoWindow;
								}

								infoWindow.open(instance._map);
							}
						);

						instance._mapEventHandles = eventHandles;
					},

					_geocode: function(location) {
						var instance = this;

						var geocoder = instance._getGeocoder();

						geocoder.geocode(
							{
								location: location
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

					_createMap: function(location, controlsConfig) {
						var instance = this;

						var mapConfig = {
							center: location,
							mapTypeId: google.maps.MapTypeId.ROADMAP,
							zoom: instance.get('zoom')
						};

						return new google.maps.Map(
							instance.get('boundingBox').getDOMNode(),
							A.merge(mapConfig, controlsConfig)
						);
					},

					_onPlaceChanged: function(searchBox) {
						var instance = this;

						var place = searchBox.getPlace();

						if (A.Lang.isObject(place)) {
							instance._geocode(place.geometry.location);
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