AUI.add(
	'liferay-google-maps',
	function(A) {
		var AArray = A.Array;

		var Lang = A.Lang;

		var GMAPS_API_URL = '{protocol}://maps.googleapis.com/maps/api/js?v=3.exp&libraries=places&callback=Liferay.GoogleMaps.onApiReady';

		var API_KEY_GMAPS_API_URL = '{protocol}://maps.googleapis.com/maps/api/js?v=3.exp&libraries=places&callback=Liferay.GoogleMaps.onApiReady&apiKey={apiKey}';

		var STR_BOUNDING_BOX = 'boundingBox';

		var STR_CLICK = 'click';

		var STR_LATITUDE = 'latitude';

		var STR_LONGITUDE = 'longitude';

		var STR_STRINGS = 'strings';

		var TPL_HOME_BUTTON = '<button class="btn btn-default home-button"><i class="glyphicon glyphicon-screenshot"></i></button>'

		var TPL_SEARCHBOX = '<div class="col-md-6"><input class="search-input" placeholder="{placeholder}" type="text"></div>';

		var GoogleMaps = A.Component.create(
			{
				ATTRS: {
					apiKey: {
						validator: Lang.isString
					},

					homeButton: {
						validator: Lang.isBoolean,
						value: false
					},

					latitude: {
						validator: Lang.isNumber
					},

					longitude: {
						validator: Lang.isNumber
					},

					markers: {
						validator: Lang.isArray
					},

					searchBox: {
						validator: Lang.isBoolean,
						value: false
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
				},

				EXTENDS: A.Widget,

				NAME: 'lfr-google-maps',

				onApiReady: function() {
					A.Object.each(GoogleMaps._registered, A.bind('_initializeMap', GoogleMaps));

					GoogleMaps._apiReady = true;
				},

				prototype: {
					initializer: function() {
						var instance = this;

						var latitude = instance.get(STR_LATITUDE);
						var longitude = instance.get(STR_LONGITUDE);

						instance._map = new google.maps.Map(
							instance.get(STR_BOUNDING_BOX).getDOMNode(),
							{
								center: {
									lat: latitude,
									lng: longitude
								},
								mapTypeId: google.maps.MapTypeId.ROADMAP,
								zoom: instance.get('zoom')
							}
						);

						if (instance.get('homeButton')) {
							instance._createHomeButton();
						}

						if (instance.get('searchBox')) {
							instance._createSearchBox();
						}

						instance._createMarkers();
					},

					bindUI: function() {
						var instance = this;

						var eventHandles = [];

						var homeButton = instance._homeButton;

						if (homeButton) {
							eventHandles.push(
								google.maps.event.addDomListener(homeButton.getDOMNode(), STR_CLICK, A.bind('_onHomeButtonClick', instance))
							);
						}

						var searchBox = instance._searchBox;

						if (searchBox) {
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

						var markers = instance._markers;

						if (markers) {
							AArray.each(
								markers,
								function(item, index, collection) {
									google.maps.event.addDomListener(item, STR_CLICK, A.bind('_onMarkerClick', instance, item))
								}
							);
						}

						instance.publish(
							'locationUpdated',
							{
								defaultFn: A.bind('_defLocationUpdatedFn', instance)
							}
						);

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

					_createHomeButton: function() {
						var instance = this;

						var homeButtonNode = A.Node.create(
							Lang.sub(
								TPL_HOME_BUTTON,
								{
									title: instance.get(STR_STRINGS).homeButtonTitle
								}
							)
						);

						instance._map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(homeButtonNode.getDOMNode());

						instance._homeButton = homeButtonNode;
					},

					_createMarkers: function() {
						var instance = this;

						var map = instance._map;

						var markers = instance.get('markers');

						if (markers) {
							var gmapsMarkers = [];

							var bounds = new google.maps.LatLngBounds();

							AArray.each(
								markers,
								function(item, index, position) {
									var markerInfoWindow = new google.maps.InfoWindow(
										{
											content: item.abstract || item.title
										}
									);

									var markerPosition = new google.maps.LatLng(item.latitude, item.longitude);

									var marker = new google.maps.Marker(
										{
											abstract: item.abstract,
											icon: item.icon,
											infoWindow: markerInfoWindow,
											map: map,
											position: markerPosition,
											title: item.title
										}
									);

									bounds.extend(markerPosition);

									gmapsMarkers.push(marker);
								}
							);

							map.fitBounds(bounds);
							map.panToBounds(bounds);

							instance._markers = gmapsMarkers;
						}
						else {
							instance._mainMarker = new google.maps.Marker(
								{
									draggable: true,
									map: map
								}
							);

							instance._geocode(instance.get(STR_LATITUDE), instance.get(STR_LONGITUDE));
						}
					},

					_createSearchBox: function() {
						var instance = this;

						var searchBoxNode = A.Node.create(
							A.Lang.sub(
								TPL_SEARCHBOX,
								{
									placeholder: instance.get(STR_STRINGS).searchBoxPlaceholder
								}
							)
						);

						instance._map.controls[google.maps.ControlPosition.TOP_CENTER].push(searchBoxNode.getDOMNode());

						instance._searchBox = new google.maps.places.Autocomplete(searchBoxNode.one('.search-input').getDOMNode());
					},

					_defLocationUpdatedFn: function(event) {
						var instance = this;

						var location = event.location.geometry.location;

						instance._map.setCenter(location);

						if (instance._mainMarker) {
							instance._mainMarker.setPosition(location);
						}
					},

					_geocode: function(latitude, longitude) {
						var instance = this;

						var geocoder = new google.maps.Geocoder();

						geocoder.geocode(
							{
								location: {
									lat: latitude,
									lng: longitude
								}
							},
							function(results, status) {
								if (status == google.maps.GeocoderStatus.OK) {
									instance.fire(
										'locationUpdated',
										{
											location: results[0]
										}
									);
								}
								else {
									instance.fire(
										'locationError',
										{
											status: status
										}
									);
								}
							}
						);
					},

					_onHomeButtonClick: function(event) {
						var instance = this;

						event.preventDefault();

						if (!instance._geocodeFn) {
							instance._geocodeFn = A.bind('_geocode', instance);
						}

						Liferay.Util.getGeolocation(instance._geocodeFn);
					},

					_onMainMarkerDragEnd: function(event) {
						var instance = this;

						var location = event.latLng;

						instance._geocode(location.lat(), location.lng());
					},

					_onMarkerClick: function(marker) {
						var instance = this;

						marker.infoWindow.open(instance._map, marker);
					},

					_onPlaceChanged: function(searchBox) {
						var instance = this;

						var place = searchBox.getPlace();

						if (A.Lang.isObject(place)) {
							var location = place.geometry.location;

							instance._geocode(location.lat(), location.lng());
						}
					}
				},

				register: function(id, config) {
					var instance = this;

					Liferay.component(
						id,
						function() {
							var instances = instance._instances;

							var googleMapInstance = instances[id];

							if (googleMapInstance) {
								googleMapInstance.destroy();
							}

							googleMapInstance = new GoogleMaps(config);

							instances[id] = googleMapInstance;

							return googleMapInstance;
						}
					);

					if (!GoogleMaps._apiReady) {
						if (!GoogleMaps._apiTransaction) {
							var apiURL = Lang.sub(
								config.apiKey ? API_KEY_GMAPS_API_URL : GMAPS_API_URL,
								{
									apiKey: config.apiKey,
									protocol: themeDisplay.getProtocol()
								}
							);

							GoogleMaps._apiTransaction = A.Get.js(apiURL);
						}

						GoogleMaps._registered[id] = config;
					}
					else {
						instance._initializeMap(id);
					}
				},

				_apiReady: false,
				_apiTransaction: null,

				_initializeMap: function(config, id) {
					Liferay.component(id).render();
				},

				_instances: {},
				_registered: {}
			}
		);

		Liferay.GoogleMaps = GoogleMaps;
	},
	'',
	{
		requires: ['aui-base']
	}
);