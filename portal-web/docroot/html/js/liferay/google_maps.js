AUI.add(
	'liferay-google-maps',
	function(A) {
		var Lang = A.Lang;

		var GoogleMaps = A.Component.create(
			{
				ATTRS: {

					apiKey: {
						validator: Lang.isString
					},

					addCurrentPositionButton: {
						validator: Lang.isBoolean,
						value: true
					},

					addSearchBox: {
						validator: Lang.isBoolean,
						value: true
					},

					draggableMarker: {
						validator: Lang.isBoolean,
						value: true
					},

					latitude: {
						validator: Lang.isNumber,
						value: 999
					},

					longitude: {
						validator: Lang.isNumber,
						value: 999
					},

					namespace: {
						validator: Lang.isString
					},

					mapContainerId: {
						validator: Lang.isString,
						value: 'map_canvas'
					},

					points: {
						validator: Lang.isArray
					},

					zoom: {
						validator: Lang.isNumber,
						value: 11
					}
				},

				AUGMENTS: [Liferay.PortletBase],

				EXTENDS: A.Widget,

				NAME: 'googlemaps',

				prototype: {
					renderUI: function() {
						var instance = this;

						instance._mapContainer = instance.one('#' + instance.get('mapContainerId'));

						instance._mapContainer.html('<p>' + Liferay.Language.get<('loading') + '</p>');

						instance._initMap();
					},

					getFormattedLocation: function(index) {
						var instance = this;

						return instance._location[index].formatted_address;
					},

					getLatitude: function() {
						var instance = this;

						return instance._latitude;
					},

					getLongitude: function() {
						var instance = this;

						return instance._longitude;
					},

					_addCurrentPositionButton: function() {
						var instance = this;

						instance._homeControlDiv = A.Node.create('<div class="glyphicon glyphicon-screenshot" id="' + instance.get('namespace') + 'home-button" title="' + Liferay.Language.get('set-current-location') + '"></div>');

						instance._homeControlDiv.setStyles(
							{
								backgroundColor: 'white',
								borderRadius: '2px',
								boxShadow: '0 1px 4px rgba(0,0,0,0.3)',
								color: '#6a6a6a',
								cursor: 'pointer',
								fontSize: '19px',
								margin: '5px'
							}
						);

						var homeControlDivDOMNode = instance._homeControlDiv.getDOMNode();

						homeControlDivDOMNode.index = 1;

						instance._map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(homeControlDivDOMNode);

						google.maps.event.addDomListener(
							homeControlDivDOMNode,
							'click',
							function() {
								instance._setCurrentLocation();
							}
						);
					},

					_addErrorMessage : function(message) {
						alert(message);
					},

					_addMarker: function () {
						var instance = this;

						instance._marker = instance._initMarker();

						google.maps.event.addListener(
							instance._marker,
							'dragend',
							function () {
								instance._latLng = instance._marker.getPosition()

								instance._geocode();
							}
						);
					},

					_addMarkers: function (len) {
						var instance = this;

						instance._bounds = new google.maps.LatLngBounds();

						var len = instance._points.length;

						for (var i = 0; i < len; i++) {
							var point = instance._points[i];

							var marker = instance._initMarker(point);

							instance._bounds.extend(marker.position);

							instance._map.fitBounds(instance._bounds);
							instance._map.panToBounds(instance._bounds);

							(function (marker) {
								var infoWindow = new google.maps.InfoWindow(
									{
										content: point.abstract || point.title
									}
								);

								google.maps.event.addListener(
									marker,
									'click',
									function () {
										infoWindow.open(instance._map, marker);
									}
								);
							})(marker);
						}
					},

					_addSearchBox: function () {
						var instance = this;

						instance._searchBox = A.Node.create('<input id="' + instance.get('namespace') + 'pac-input" class="controls" type="text" placeholder="Search Box"> ');

						instance._searchBox.setStyles(
							{
								backgroundColor: '#fff',
								fontFamily: 'Roboto',
								fontSize: '15px',
								fontWeight: '300',
								marginTop: '5px',
								padding: '0 11px 0 13px',
								textOverflow: 'ellipsis',
								width: '400px'
							}
						)

						var searchBoxDOMNode = instance._searchBox.getDOMNode();

						instance._map.controls[google.maps.ControlPosition.TOP_LEFT].push(searchBoxDOMNode);

						var searchBox = new google.maps.places.SearchBox(searchBoxDOMNode);

						google.maps.event.addListener(
							searchBox,
							'places_changed',
							function () {
								var places = searchBox.getPlaces();

								if (places.length == 0) {
									return;
								}

								for (var i = 0, place; place = places[i]; i++) {
									instance._latLng = place.geometry.location;

									instance._geocode();
								}
							}
						);
					},

					_initMarker: function(point) {
						var instance = this;

						if(!point) {
							return new google.maps.Marker(
								{
									animation: google.maps.Animation.DROP,
									draggable: instance.get('draggableMarker'),
									position: instance._latLng,
									map: instance._map,
									title: instance._location[1].formatted_address
								}
							);
						}
						else {
							return new google.maps.Marker(
								{
									icon: point.icon,
									map: instance._map,
									position: new google.maps.LatLng(point.latitude, point.longitude),
									title: point.title
								}
							);
						}
					},

					_geocode: function() {
						var instance = this;

						var geocoder = new google.maps.Geocoder();

						geocoder.geocode(
							{'latLng': instance._latLng},
							function(results, status) {
								if (status == google.maps.GeocoderStatus.OK) {
									if (results[1]) {
										instance._location = results;

										if (instance._marker) {
											instance._moveMarker();
										}
										else {
											instance._points = instance.get('points');

											if (instance._points) {
												instance._addMarkers();
											}
											else {
												instance._addMarker();
											}

											if (!instance._homeControlDiv) {
												instance._addCurrentPositionButton();
											}

											if (!instance._searchBox && instance.get('addSearchBox')) {
												instance._addSearchBox();
											}
										}

										instance._map.setCenter(instance._latLng);

										instance.fire('locationReady');
									}
									else {
										instance._addErrorMessage('No results found');
									}
								}
								else {
									instance._addErrorMessage('Geocoder failed due to: ' + status);
								}
							}
						);
					},

					_initMap: function() {
						var instance = this;

						instance._latitude = instance.get('latitude');
						instance._longitude = instance.get('longitude');

						instance._latLng = new google.maps.LatLng(instance._latitude, instance._longitude);

						var mapOptions = {
							center: instance._latLng,
							zoom: instance.get('zoom'),
							mapTypeId: google.maps.MapTypeId.ROADMAP
						};

						instance._mapContainer.setStyles(
							{
								border: '1px solid #ccc',
								height: '400px',
								width: '100%'
							}
						);

						instance._map = new google.maps.Map(instance._mapContainer.getDOMNode(), mapOptions);

						instance._geocode();
					},

					_moveMarker: function() {
						var instance = this;

						instance._latitude = instance._latLng.lat();
						instance._longitude = instance._latLng.lng();

						instance._marker.setPosition(instance._latLng)
						instance._marker.setTitle(instance.getFormattedLocation(1));
					},

					_setCurrentLocation: function() {
						var instance = this;

						Liferay.Util.getGeolocation(
							function(latitude, longitude) {
								instance._latLng = new google.maps.LatLng(latitude, longitude);

								instance._geocode();
							}
						);
					}
				},

				register: function(id, config) {
					var instance = this;

					GoogleMaps._id = id;
					GoogleMaps._registered[id] = config;

					var apiURL = themeDisplay.getProtocol() + '://maps.googleapis.com/maps/api/js?v=3.exp&libraries=places&callback=Liferay.GoogleMaps.initialize';

					var apiKey = config.apikey;

					if (apiKey) {
						apiURL = themeDisplay.getProtocol() + '://maps.googleapis.com/maps/api/js?v=3.&key=' + apiKey + 'exp&libraries=places&callback=Liferay.GoogleMaps.initialize'
					}

					A.Get.js(apiURL);
				},

				initialize: function() {
					var instance = this;

					var config = GoogleMaps._registered[GoogleMaps._id];
					var id = GoogleMaps._id;

					Liferay.component(
						id,
						function() {
							var instances = instance._instances;

							var googleMapsInstance = instances[id];

							if (!googleMapsInstance) {
								googleMapsInstance = new GoogleMaps(config);

								instances[id] = googleMapsInstance;

								Liferay.fire(config.namespace + 'googleMapsInitialized');
							}

							return googleMapsInstance;
						}
					);

					Liferay.component(id).render();
				},

				_id : {},
				_instances: {},
				_registered: {}
			}
		);

		Liferay.GoogleMaps = GoogleMaps;
	},
	'',
	{
		requires: ['aui-base','liferay-portlet-base']
	}
);