AUI.add(
	'liferay-map-openstreet',
	function(A) {
		var AArray = A.Array;

		var Lang = A.Lang;

		var MapBase = Liferay.MapBase;

		var CONTROLS_CONFIG_MAP = {};

		CONTROLS_CONFIG_MAP[MapBase.CONTROLS.ATTRIBUTION] = 'attributionControl';
		CONTROLS_CONFIG_MAP[MapBase.CONTROLS.ZOOM] = 'zoomControl';

		var STR_BOUNDING_BOX = 'boundingBox';

		var TPL_GEOCODING_URL = 'http://nominatim.openstreetmap.org/search?format=json&json_callback={callback}&q={query}';

		var TPL_REVERSE_GEOCODING_URL = 'http://nominatim.openstreetmap.org/reverse?format=json&json_callback={callback}&lat={lat}&lon={lng}';

		var OpenStreetMap = A.Component.create(
			{
				AUGMENTS: [MapBase],

				EXTENDS: A.Widget,

				NAME: 'lfr-map-openstreet',

				prototype: {
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

						L.geoJson(geoJson).addTo(instance._map);
					},

					addMarker: function() {
						var instance = this;

						var marker = L.marker(
							[],
							{
								draggable: true
							}
						).addTo(instance._map);

						return marker;
					},

					panTo: function(location) {
						var instance = this;

						instance._map.panTo(location);

						if (instance._mainMarker) {
							instance._mainMarker.setLatLng(location);
						}
					},

					_createMap: function(latitude, longitude, controlsConfig) {
						var instance = this;

						var mapConfig = {
							center: {
								lat: latitude,
								lng: longitude
							},
							layers: [L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png')],
							zoom: instance.get('zoom')
						};

						return L.map(
							instance.get(STR_BOUNDING_BOX).getDOMNode(),
							A.merge(mapConfig, controlsConfig)
						);
					},

					_bindMapUI: function() {
						var instance = this;

						var eventHandles = [];

						var mainMarker = instance._mainMarker;

						if (mainMarker) {
							eventHandles.push(
								mainMarker.on('dragend', A.bind('_onMainMarkerDragEnd', instance))
							);
						}

						instance._eventHandles = eventHandles;
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

					_geocode: function(latitude, longitude) {
						var instance = this;

						A.jsonp(
							A.Lang.sub(
								TPL_REVERSE_GEOCODING_URL,
								{
									lat: latitude,
									lng: longitude
								}
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

					_onMainMarkerDragEnd: function(event) {
						var instance = this;

						var location = event.target.getLatLng();

						instance._geocode(location.lat, location.lng);
					},

					CONTROLS_CONFIG_MAP: CONTROLS_CONFIG_MAP
				}
			}
		);

		Liferay.OpenStreetMap = OpenStreetMap;
	},
	'',
	{
		requires: ['jsonp', 'liferay-map-base']
	}
);