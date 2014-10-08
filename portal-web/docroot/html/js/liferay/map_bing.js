AUI.add(
	'liferay-map-bing',
	function(A) {
		var AArray = A.Array;

		var Lang = A.Lang;

		var MapBase = Liferay.MapBase;

		var CONTROLS_CONFIG_MAP = {};

		var BingMap = A.Component.create(
			{
				AUGMENTS: [MapBase],

				EXTENDS: A.Widget,

				NAME: 'lfr-map-bing',

				prototype: {
					addControl: function(control, position) {
					},

					addGeoJson: function(geoJson) {
					},

					panTo: function(location) {
					},

					_bindMapUI: function() {
					},

					_createMap: function(latitude, longitude, controlsConfig) {
						var instance = this;

						var mapConfig = {
							credentials: 'AtGvk4u8lZWZvxQoQUq7GFLc5znUilvim-WETm89FWl3iRb9kYZ9eGzzp_7x86cC',
							center: new Microsoft.Maps.Location(latitude, longitude),
							zoom: instance.get('zoom')
						};

						return new Microsoft.Maps.Map(
							instance.get(STR_BOUNDING_BOX).getDOMNode(),
							A.merge(mapConfig, controlsConfig)
						);
					},

					CONTROLS_CONFIG_MAP: CONTROLS_CONFIG_MAP
				}
			}
		);

		Liferay.BingMap = BingMap;
	},
	'',
	{
		requires: ['aui-base', 'liferay-map-base']
	}
);