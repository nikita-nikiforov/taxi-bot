<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset='utf-8' />
        <title>Found place</title>
        <meta name='viewport' content='initial-scale=1,maximum-scale=1,user-scalable=no' />
        <link href="https://fonts.googleapis.com/css?family=Open+Sans" rel="stylesheet">
        <script src='https://api.tiles.mapbox.com/mapbox-gl-js/v0.44.2/mapbox-gl.js'></script>
        <link href='https://api.tiles.mapbox.com/mapbox-gl-js/v0.44.2/mapbox-gl.css' rel='stylesheet' />
        <link rel="stylesheet" href="/static/css/map.css">
    </head>
    <body>

        <div id='map'></div>
        <input type="hidden" id="lat" value='${lat}'>
        <input type="hidden" id="lng" value='${lng}'>
        <script src="/static/js/map.js"></script>
    </body>
</html>

