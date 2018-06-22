mapboxgl.accessToken = 'pk.eyJ1IjoibmlraWZvcm92cGl6emEiLCJhIjoiY2ppaXZ3c3N4MW5mMjNxbGkxM2M2OXQwdSJ9.-ae7oZJHWun4nNTrVZ1atA';

var lat = document.getElementById("lat").value;
console.log(document.getElementById("lat"));

var lng = document.getElementById("lng").value;

var map = new mapboxgl.Map({
    container: 'map',
    style: 'mapbox://styles/mapbox/streets-v10',
    center: [lng, lat],
    zoom: 15
});

var el = document.createElement('div');
el.className = 'marker';

map.on('load', function () {
    new mapboxgl.Marker(el)
        .setLngLat([lng, lat])
        .addTo(map);
});

// code from the next step will go here!