function initialize() {
  var myLatlng = new google.maps.LatLng(51.4861532,-0.014835); // St david sq
//  var myLatlng = new google.maps.LatLng(51.4283359, 0.3459448); // gravesend

  console.log(Object.keys(mikerach.venues).length);
  var location = mikerach.venues.id6.location;

  var mapOptions = {
    center: myLatlng,
    zoom: 14
  };

  map = new google.maps.Map(document.getElementById('map-canvas'),
      mapOptions);

  var infowindow = new google.maps.InfoWindow({
      content: 'Blank'
  });

  var i = 0;
  for (venue in mikerach.venues) {
    var venue = mikerach.venues[venue]
    var location = venue.location;
    var myLatlng = new google.maps.LatLng(location.lat, location.lng);
    var marker = new google.maps.Marker({
      position: myLatlng,
      map: map,
      title:venue.name
    });
    var approxText = venue.approximation == "true" ? '(approx)' : '';
    marker.html = '<div style="width: 300px;height: 120px" class="venue-popup">' +
      '<h1>' + venue.name + '</h1>' +
      '<div><a href="https://www.google.com/search?q=' + venue.name + ' ' + venue.postcode + '" target="_blank">search</a> ' +
      '<a href="https://www.google.com/search?q=wedding ' + venue.name + ' ' + venue.postcode + '&tbm=isch" target="_blank">images</a> ' +
      '<a href="http://www.hitched.co.uk/wedding-venues/' + venue.name.trim().replace(new RegExp(' ', 'g'), "-") + '.htm" target="_blank">hitched</a></div>' +
      '<div>Postcode: '+ venue.postcode + ' ' + approxText + '</div>' +
      '<div>Dorset: <span id="dorset-time">??</span> (driving)</div>' +
      '<div>Cardiff: <span id="cardiff-time">??</span> (driving)</div>' +
      '<div>London: <span id="london-time">??</span> (public transport)</div>' +
      '</div>';

    google.maps.event.addListener(marker, 'click', function(event) {
      infowindow.open(map, this);
      infowindow.setContent(this.html);
      var cardiff = "CF144BS";
      var dorset = "Poundbury, Dorset"
      var venue = this.getPosition();

      var service = new google.maps.DistanceMatrixService();
      service.getDistanceMatrix(
        {
          origins: [cardiff,dorset],
          destinations: [venue],
          travelMode: google.maps.TravelMode.DRIVING,
          unitSystem: google.maps.UnitSystem.IMPERIAL,
          durationInTraffic: true,
          avoidHighways: false,
          avoidTolls: false
        }, callback);

      function callback(response, status) {
        document.getElementById('cardiff-time').innerHTML = response.rows[0].elements[0].duration.text;
        document.getElementById('dorset-time').innerHTML = response.rows[1].elements[0].duration.text;
      }

      var tomorrowMidday = new Date();
      tomorrowMidday.setDate(tomorrowMidday.getDate()+1);
      tomorrowMidday.setHours(12);
      tomorrowMidday.setMinutes(0);

      new google.maps.DirectionsService().route(
        {
          origin: "London, UK",
          destination: venue,
          travelMode: google.maps.TravelMode.TRANSIT,
          unitSystem: google.maps.UnitSystem.IMPERIAL,
          transitOptions: {
            arrivalTime: tomorrowMidday
          }
          // add arrival time
        }, londonCallback);

      function londonCallback(result, status) {
        console.log(status);
        document.getElementById('london-time').innerHTML = result.routes[0].legs[0].duration.text;
      }
    });
    i++;
  }
  console.log('Loaded ' + i + ' venues');
//  // To add the marker to the map, use the 'map' property
//  marker = new google.maps.Marker({
//    position: myLatlng,
//    animation: google.maps.Animation.DROP,
//    title:"Hello World!"
//  });
//
//  marker.setMap(map);
//  setTimeout(0, function() {
//    console.log(marker.getPosition());
//    if (map.getBounds().contains(marker.getPosition())) {
//    }
//  })



  //var origin1 = new google.maps.LatLng(55.930385, -3.118425);
  //var origin2 = "Greenwich, England";
  //var destinationA = "Stockholm, Sweden";
  //var destinationB = new google.maps.LatLng(50.087692, 14.421150);
  var origin1 = "DA118SP";
  var destinationA = "E143WE";

  var service = new google.maps.DistanceMatrixService();
  service.getDistanceMatrix(
    {
      origins: [origin1],
      destinations: [destinationA],
      travelMode: google.maps.TravelMode.DRIVING,
      unitSystem: google.maps.UnitSystem.IMPERIAL,
      durationInTraffic: true,
      avoidHighways: false,
      avoidTolls: false
    }, callback);

  function callback(response, status) {
    // See Parsing the Results for
    // the basics of a callback function.
    //window.alert("Distance : " + response.rows[0].elements[0].distance.text + ' Duration : ' + response.rows[0].elements[0].duration.text);
  }
}

google.maps.event.addDomListener(window, 'load', initialize);
