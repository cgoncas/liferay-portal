AUI.add(
	'liferay-map-openstreet',
	function(A) {
		var AArray = A.Array;

		var Lang = A.Lang;

		var MapBase = Liferay.MapBase;

		var CONTROLS_CONFIG_MAP = {};

		CONTROLS_CONFIG_MAP[MapBase.CONTROLS.ATTRIBUTION] = 'attributionControl';
		CONTROLS_CONFIG_MAP[MapBase.CONTROLS.ZOOM] = 'zoomControl';

		var TPL_GEOCODING_URL = 'http://nominatim.openstreetmap.org/search?format=json&json_callback={callback}&q={query}';

		var TPL_REVERSE_GEOCODING_URL = 'http://nominatim.openstreetmap.org/reverse?format=json&json_callback={callback}&lat={lat}&lon={lng}';

		var OpenstreetMapMarker = A.Component.create(
			{
				AUGMENTS: [Liferay.MapMarkerBase],

				EXTENDS: A.Base,

				NAME: 'lfr-map-marker-openstreet',

				prototype: {
					_bindNativeMarker: function(nativeMarker) {
						var instance = this;

						nativeMarker.on('click', instance._getNativeEventFn('click'));
						nativeMarker.on('dblclick', instance._getNativeEventFn('dblclick'));
						nativeMarker.on('drag', instance._getNativeEventFn('drag'));
						nativeMarker.on('dragend', instance._getNativeEventFn('dragend'));
						nativeMarker.on('dragstart', instance._getNativeEventFn('dragstart'));
						nativeMarker.on('mousedown', instance._getNativeEventFn('mousedown'));
						nativeMarker.on('mouseout', instance._getNativeEventFn('mouseout'));
						nativeMarker.on('mouseover', instance._getNativeEventFn('mouseover'));
					},

					_createNativeMarker: function(location, map) {
						var instance = this;

						var nativeMarker = L.marker(
							location,
							{
								draggable: true
							}
						).addTo(map);

						return nativeMarker;
					},

					_normalizeEvent: function(nativeEvent) {
						var instance = this;

						return {
							location: nativeEvent.target.getLatLng()
						}
					}
				}
			}
		);

		var OpenstreetMap = A.Component.create(
			{
				AUGMENTS: [MapBase],

				EXTENDS: A.Widget,

				NAME: 'lfr-map-openstreet',

				prototype: {
					MarkerImpl: OpenstreetMapMarker,

					addControl: function(control, position) {
						var instance = this;

						switch(position) {
							case 'TL':
								position = 'topleft';
								break;
							case 'TR':
								position = 'topright';
								break;
							case 'BR':
								position = 'bottomright';
								break;
							default:
								position = 'bottomleft';
								break;
						}

						var LeafletControl = L.Control.extend(
							{
								onAdd: function(map) {
									return control.getDOMNode();
								},
								options: {
									position: position
								}
							}
						);

						instance._map.addControl(new LeafletControl());
					},

					addGeoJson: function(geoJson) {
						var instance = this;

						L.geoJson(
							geoJson,
							{
								style: function(feature) {
									return { icon: feature.properties.icon };
								},
								onEachFeature: function(feature, layer) {
									layer.bindPopup(feature.properties.abstract);
								}
							}
						).addTo(instance._map);
					},

					panTo: function(location) {
						var instance = this;

						instance._map.panTo(location);
					},

					_createMap: function(location, controlsConfig) {
						var instance = this;

						var mapConfig = {
							center: location,
							layers: [L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png')],
							zoom: instance.get('zoom')
						};

						return L.map(
							instance.get('boundingBox').getDOMNode(),
							A.merge(mapConfig, controlsConfig)
						);
					},

					_bindMapUI: function() {
						var instance = this;
					},

					destructor: function() {
						var instance = this;

						AArray.each(
							instance._eventHandles,
							function(item, index, collection) {
								google.maps.event.removeListener(item);
							}
						);
					},

					_geocode: function(location) {
						var instance = this;

						A.jsonp(
							A.Lang.sub(
								TPL_REVERSE_GEOCODING_URL,
								location
							),
							{
								context: instance,
								on: {
									success: instance._handleJSONP
								}
							}
						);
					},

					_handleJSONP: function(result) {
						var instance = this;

						instance.set(
							'position',
							{
								address: result.display_name,
								location: {
									lat: result.lat,
									lng: result.lon
								}
							}
						);
					},

					CONTROLS_CONFIG_MAP: CONTROLS_CONFIG_MAP
				}
			}
		);

		Liferay.OpenstreetMap = OpenstreetMap;
	},
	'',
	{
		requires: ['jsonp', 'liferay-map-base']
	}
);