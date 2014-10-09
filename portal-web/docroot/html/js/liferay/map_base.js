AUI.add(
	'liferay-map-base',
	function(A) {
		var AArray = A.Array;

		var Lang = A.Lang;

		var STR_BOUNDING_BOX = 'boundingBox';

		var STR_CONTROLS = 'controls';

		var STR_CLICK = 'click';

		var STR_STRINGS = 'strings';

		var TPL_HOME_BUTTON = '<button class="btn btn-default home-button"><i class="glyphicon glyphicon-screenshot"></i></button>';

		var TPL_SEARCHBOX = '<div class="col-md-6"><input class="search-input" placeholder="{placeholder}" type="text"></div>';

		var Base = function() {};

		Base.CONTROLS = {
			ATTRIBUTION: 'attribution',
			GEOLOCATION: 'geolocation',
			HOME: 'home',
			OVERVIEW: 'overview',
			PAN: 'pan',
			ROTATE: 'rotate',
			SCALE: 'scale',
			SEARCH: 'search',
			STREETVIEW: 'streetview',
			TYPE: 'type',
			ZOOM: 'zoom'
		};

		Base.POSITION = {
			BOTTOM: 11,
			BOTTOM_CENTER: 11,
			BOTTOM_LEFT: 10,
			BOTTOM_RIGHT: 12,
			CENTER: 13,
			LEFT: 5,
			LEFT_BOTTOM: 6,
			LEFT_CENTER: 4,
			LEFT_TOP: 5,
			RIGHT: 7,
			RIGHT_BOTTOM: 9,
			RIGHT_CENTER: 8,
			RIGHT_TOP: 7,
			TOP: 2,
			TOP_CENTER: 2,
			TOP_LEFT: 1,
			TOP_RIGHT: 3
		};

		Base.prototype = {
			initializer: function() {
				var instance = this;

				var position = instance.get('position');

				var location = (position && position.location) ? position.location : {};

				if (!location.latitude && !location.longitude) {
					Liferay.Util.getGeolocation(
						function(latitude, longitude) {
							instance._initializeMap(latitude, longitude);

							instance.set(
								'position',
								{
									location: {
										latitude: latitude,
										longitude: longitude
									}
								}
							);
						}
					);
				}
				else {
					instance._initializeMap(location.latitude, location.longitude);
				}
			},

			bindUI: function() {
				var instance = this;

				instance._eventHandles = [
					instance.on('positionChange', A.bind('_onPositionChange', instance))
				];
			},

			destructor: function() {
				var instance = this;

				(new A.EventHandle(instance._eventHandles)).detach();
			},

			_onPositionChange: function(event) {
				var instance = this;

				instance.panTo(event.newVal.location);
			},

			_getControlsConfig: function() {
				var instance = this;

				var controls = instance.get(STR_CONTROLS);

				var config = {};

				A.Object.each(
					instance.CONTROLS_CONFIG_MAP,
					function(item, index, collection) {
						config[item] = (AArray.indexOf(controls, index) !== -1);
					}
				);

				return config;
			},

			_createCustomControls: function() {
				var instance = this;

				var controls = instance.get(STR_CONTROLS);

				var customControls = {};

				if (AArray.indexOf(controls, Base.CONTROLS.HOME)) {
					var homeControl = A.Node.create(
						Lang.sub(
							TPL_HOME_BUTTON,
							{
								title: instance.get(STR_STRINGS).homeButtonTitle
							}
						)
					);

					customControls[Base.CONTROLS.HOME] = homeControl;

					instance.addControl(homeControl, Base.POSITION.RIGHT_BOTTOM);
				}

				if (AArray.indexOf(controls, Base.CONTROLS.SEARCH)) {
					var searchControl = A.Node.create(
						A.Lang.sub(
							TPL_SEARCHBOX,
							{
								placeholder: instance.get(STR_STRINGS).searchBoxPlaceholder
							}
						)
					);

					customControls[Base.CONTROLS.SEARCH] = searchControl;

					instance.addControl(searchControl, Base.POSITION.TOP_CENTER);
				}

				instance._customControls = customControls;
			},

			_bindMapUIMB: function() {
				var instance = this;

				var customControls = instance._customControls;

				var homeControl = customControls[Base.CONTROLS.HOME];

				if (homeControl) {
					homeControl.on('click', instance._onHomeButtonClick, instance);
				}

				instance._bindMapUI();
			},

			_initializeMap: function(latitude, longitude) {
				var instance = this;

				var controlsConfig = instance._getControlsConfig();

				instance._map = instance._createMap(latitude, longitude, controlsConfig);

				instance.addGeoJson(instance.get('data'));

				instance._createCustomControls();

				instance._bindMapUIMB();

				if (instance.get('geolocation')) {
					instance._geolocationMarker = instance.addMarker(latitude, longitude);

					instance._geocode(latitude, longitude);
				}
			},

			_onHomeButtonClick: function(event) {
				var instance = this;

				event.preventDefault();

				if (!instance._geocodeFn) {
					instance._geocodeFn = A.bind('_geocode', instance);
				}

				Liferay.Util.getGeolocation(instance._geocodeFn);
			},

			_onMarkerClick: function(marker) {
				var instance = this;

				marker.infoWindow.open(instance._map, marker);
			}
		};

		Base.ATTRS = {
			controls: {
				validator: Lang.isArray,
				value: [
					Base.CONTROLS.PAN,
					Base.CONTROLS.TYPE,
					Base.CONTROLS.ZOOM
				]
			},

			data: {
				validator: Lang.isObject
			},

			geolocation: {
				validator: Lang.isBoolean,
				value: false
			},

			position: {
				validator: Lang.isObject,
				value: {
					location: {
						latitude: 0,
						longitude: 0
					}
				}
			},

			strings: {
				validator: Lang.isObject,
				value: {
					homeButtonTitle: Liferay.Language.get('set-current-location'),
					searchBoxPlaceholder: Liferay.Language.get('search-box')
				}
			},

			zoom: {
				validator: Lang.isNumber,
				value: 11
			}
		};

		Base.NAME = 'lfr-map-base';

		Base.NS = 'lfr-map-base';

		Liferay.MapBase = Base;
	},
	'',
	{
		requires: ['aui-base']
	}
);