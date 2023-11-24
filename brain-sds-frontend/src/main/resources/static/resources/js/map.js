// TODO => defaultCoord has to be on DB
var defaultCoord = {lat: 35.162237, lng: 129.165860}; // 골든 튤립
var service, directionsRenderer, directionsService;
var map, baseCoord;
var responseStorage, indexStorage;
var markers=[];
var polyline, polylines = [];

function getGoogleLangCode(num) {
    var langCode = 'ko';
    switch (num) {
        case 1: langCode = 'ko'; break;
        case 2: langCode = 'en'; break;
        case 3: langCode = 'ja'; break;
        case 4: langCode = 'zh-CN'; break;
        default: break;
    }
    return langCode;
}

function initMap() {
    map = new google.maps.Map(document.getElementById('map'), {
        center: new google.maps.LatLng(defaultCoord.lat, defaultCoord.lng),
        zoom: 13,
        disableDefaultUI: true,
        disableDoubleClickZoom: true,
        draggable: true
    });

    getCurrentCoord(defaultCoord);
    service = new google.maps.places.PlacesService(map);
    directionsRenderer = new google.maps.DirectionsRenderer({map: map});
    directionsService = new google.maps.DirectionsService();
}

function apiLoadedFunc(resolve) {

    var afterMapAPILoaded = function() {
        resolve();
        initMap();
        window.removeEventListener('googleMapsAPILoaded', afterMapAPILoaded);
    };
    window.addEventListener('googleMapsAPILoaded', afterMapAPILoaded);
}

function setMapAPILanguage (lang) {

    var key = 'AIzaSyAuZtW-deiqIw5G9iCmTLTU7YDeODD63ag';
    var libraries = ['geometry', 'places'];

    //Destroy old API
    var scriptList = document.querySelectorAll('script[src^="https://maps.googleapis.com"]');
    for (var i = 0; i < scriptList.length; i ++) {
        scriptList[i].parentNode.removeChild(scriptList[i]);
    }
    if(google) delete google.maps;

    //Generate new Google Maps API script
    var newAPI = document.createElement('script');
    newAPI.src = 'https://maps.googleapis.com/maps/api/js?libraries=' + libraries.join(',') +
        '&key=' + key + '&language=' + lang + '&callback=googleMapsAPILoaded';


    window.googleMapsAPILoaded = function() {
        // 이벤트 생성.
        var event = document.createEvent('Event');
        event.initEvent('googleMapsAPILoaded', true, true);
        window.dispatchEvent(event);
    };

    //Wait for the callback to be executed
    var apiLoaded = new Promise(apiLoadedFunc);

    //Start the script
    document.querySelector('body').appendChild(newAPI);

    return apiLoaded;
}

// EVENTS
function selectSearchHandler(coords, text){
    if(coords == "current") {
        getCurrentCoord(defaultCoord);
        return new Promise(function(resolve, reject) {
          resolve(baseCoord);
        });
    }
    else if(coords == "custom"){
        var request = {
            location: baseCoord,
            radius: 10000,
            query: text
        };
        return new Promise(function(resolve, reject) {
            service.textSearch(request, function(result, status) {
                if (status == google.maps.places.PlacesServiceStatus.OK) {
                    var pos = {
                        lat: result[0].geometry.location.lat(),
                        lng: result[0].geometry.location.lng()
                    };

                    resolve(new google.maps.LatLng(pos.lat, pos.lng));
                }
            });
        });
    }
    return new Promise(function(resolve, reject) {
      resolve(new google.maps.LatLng(coords.lat, coords.lng));
    });
}

// COORDINATE
function getCurrentCoord(defaultCoord) {
    if(navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(position) {
            var pos = {
                lat: position.coords.latitude,
                lng: position.coords.longitude
            };
            baseCoord = new google.maps.LatLng(pos.lat, pos.lng);
        }, function(err) {
            console.log(err);
            baseCoord = new google.maps.LatLng(defaultCoord.lat, defaultCoord.lng);
        });
    }
    else {
        baseCoord = new google.maps.LatLng(defaultCoord.lat, defaultCoord.lng);
    }
}

// DIRECTION
function directionsBySelection(start, end, mode, startInput, endInput, panel) {
    var startCoord;
    var endCoord;
    DeleteAllMarker(markers);
    DeleteAllMarker(polylines);
    selectSearchHandler(start, startInput).then(function (result) {
        startCoord = result;
        selectSearchHandler(end, endInput).then(function (result) {
            endCoord = result;
            getRoute(startCoord, endCoord, mode, panel);
        });
    });
}

// CALCULATE ROUTE
function getRoute(start, end, mode, panel){
    directionsService.route({
        origin: start,
        destination: end,
        provideRouteAlternatives: true,
        travelMode: mode
    }, (function(response, status) {
        if(status === 'OK'){
            responseStorage = response;
            setPanel(response, panel);
            setDirection(response.routes[0]);
            FocusPolyline(response.routes[0]);
        }
        else {
            console.log('Directions request failed due to ' + status);
        }
    }));
}

// SET
function setPanel(response, panel){
    // console.log(response);
    panel.innerHTML = "";
    response.routes.forEach(function(e, index) {
    var route = "";

    for(var i = 0; i < e.legs[0].steps.length; i ++){
        if(e.legs[0].steps[i].travel_mode == 'TRANSIT') {
            if(e.legs[0].steps[i].transit.line.vehicle.type == "SUBWAY" || "HEAVY_RAIL"){
                route += '<li class="ico_subway panelElement" data-index ="' + index + '">';
                break;
            }
            else if(e.legs[0].steps[i].transit.line.vehicle.type == "BUS"){
                route += '<li class="ico_bus panelElement" data-index ="' + index + '">';
                break;
            }
        }
    }
    route +='\
    <span class="time">\
        <em>' + e.legs[0].departure_time.text + ' ~ ' + e.legs[0].arrival_time.text + '</em> \
        <em>' + e.legs[0].duration.text + '</em> \
    </span> \
    <span class="transportation">';

    e.legs[0].steps.forEach(function(step) {
        if(step.travel_mode == 'WALKING'){
            route += '<em class="ico_walk">walk</em>';
        }
        else if(step.travel_mode == 'TRANSIT'){
            var name = validateTransitName(step.transit.line);
            var color = validateTransitColor(step.transit.line);
            if(step.transit.line.vehicle.type ==  'BUS'){
                route += '<em class="ico_bus">';
            }
            else if(step.transit.line.vehicle.type == 'SUBWAY' || 'HEAVY_RAIL'){
                route += '<em class="ico_subway">';
            }
            route += '<strong class="transit" style="background-color: ' + color + '">' + name + '</strong></em>';
        }
    });
    route += '</span>';
    for(var i = 0; i < e.legs[0].steps.length; i ++){
        if(e.legs[0].steps[i].travel_mode == 'TRANSIT') {
            if(e.legs[0].steps[i].transit.line.vehicle.type == "SUBWAY" || "HEAVY_RAIL" || "BUS"){
                route += '<span class="txt">' + e.legs[0].steps[i].transit.departure_time.text + ',' + e.legs[0].steps[i].transit.departure_stop.name + '</span>';
                break;
            }
        }
    }
    route += '<button type="button" class="btn_timeline">상세정보</button>';
    route += '<ul class="mapLoad_timeline">';

    for(var i = 0; i < e.legs[0].steps.length; i ++){
        if(e.legs[0].steps[i].travel_mode == 'WALKING'){
            route += '<li class="ico_walk">';

            if(i == 0){
                route += '<span class="realTime">' + e.legs[0].departure_time.text + '</span>';
            }
            // else if(i == e.legs[0].steps.length -1) {
            //     route += `<span class="realTime">${e.legs[0].arrival_time.text}</span>`;
            // }
            else {
                route += '<span class="realTime"></span>';
            }
            route += '\
                <span class="timeInfo">\
                    <em>\
                        <strong class="tit">' + e.legs[0].steps[i].instructions + '</strong><br>\
                        <strong class="txt">' + e.legs[0].steps[i].duration.text + ',' +  e.legs[0].steps[i].distance.text + '</strong>\
                    </em>\
                </span>\
            </li>\
            ';
        }
        else if(e.legs[0].steps[i].travel_mode == 'TRANSIT') {
            var color = validateTransitColor(e.legs[0].steps[i].transit.line);
            if (e.legs[0].steps[i].transit.line.vehicle.type == 'BUS'){
                route += '<li class="ico_bus">';
            }
            else if(e.legs[0].steps[i].transit.line.vehicle.type == "SUBWAY" || "HEAVY_RAIL" ){
                route += '<li class="ico_subway">';
            }
            route += '<span class="realTime" style="border-right-color:' + color + '">' + e.legs[0].steps[i].transit.departure_time.text + '</span>\
                <span class="timeInfo">\
                    <em>\
                        <strong class="tit">' + e.legs[0].steps[i].transit.departure_stop.name + '</strong>\
                    </em>\
                    <em>\
                        <strong class="tit">' + e.legs[0].steps[i].transit.arrival_stop.name + '</strong>\
                    </em>\
                </span>\
                <span class="realTime" style="border-right-color:' + color +'">' + e.legs[0].steps[i].transit.arrival_time.text + '</span>\
            </li>\
            ';
        }


    }

    route += '</ul></li>';
    panel.innerHTML += route;
    });


    $('.mapLoad_detail_lst li').on('click',function(){
        $('.mapLoad_detail_lst li').removeClass('active');
        $(this).addClass('active');
    });
    $('.btn_timeline').on('click',function(){
        $('.mapLoad_detail_lst li').removeClass('timeline');
        $(this).parent().toggleClass('timeline');
    });
    $('.mapLoad_detail_lst').each(function(){
        $(this).children('li:first').addClass('active');
    });
    $('.panelElement').click(function(){
        indexStorage = $(this).attr('data-index');
        DeleteAllMarker(markers);
        DeleteAllMarker(polylines);
        setDirection(responseStorage.routes[indexStorage]);
    })
}
function setDirection(response) {
    // Draw Polyline
    response.legs[0].steps.forEach(function(step) {
        var lineSymbol = {
            path: google.maps.SymbolPath.CIRCLE,
            fillColor: 'black',
            fillOpacity: 0.7,
            scale: 3
        };
        if(step.travel_mode == "WALKING"){
            polyline = new google.maps.Polyline({
                path: google.maps.geometry.encoding.decodePath(step.encoded_lat_lngs),
                strokeColor: "red",
                strokeOpacity: 0,
                fillOpacity: 0.7,
                icons: [{
                    icon: lineSymbol,
                    offset: '0',
                    repeat: '10px'
                }],
                map: map
            });
            polylines.push(polyline);
        }
        else if(step.travel_mode == "TRANSIT"){
            CreateDirectionMarker(step.start_location, step.transit.departure_stop.name);
            var color;
            if(step.transit.line.color) { color = step.transit.line.color; }
            else {color = 'cyan'; }
            polyline = new google.maps.Polyline({
                path: google.maps.geometry.encoding.decodePath(step.encoded_lat_lngs),
                strokeColor: color,
                strokeWeight: 5,
                map: map
            });
            polylines.push(polyline);
            CreateDirectionMarker(step.end_location, step.transit.arrival_stop.name);
        }
    });

    CreateStartEndMarker(response.legs[0].start_location, "/resources/images/ico_point_start.png");
    CreateStartEndMarker(response.legs[0].end_location, "/resources/images/ico_point_end.png");
}

// VALIDATE TRANSIT NAME & COLOR
function validateTransitName(line) {
    if(line.short_name) { return line.short_name; }
    else if(line.name) { return line.name; }
}
function validateTransitColor(line) {
    if(line.color) { return line.color; }
}

// POLYLINE
function FocusPolyline(response) {
    var polyline = new google.maps.Polyline({
        path: google.maps.geometry.encoding.decodePath(response.overview_polyline),
        strokeOpacity: 0,
        fillOpacity: 0
    });
    polylines.push(polyline);
    var bounds = new google.maps.LatLngBounds();
    for (var i = 0; i < polyline.getPath().getLength(); i++) {
        bounds.extend(polyline.getPath().getAt(i));
    }
    map.fitBounds(bounds);
}

// MARKER
function CreateDirectionMarker(position, text){
    var marker = new google.maps.Marker({
        map: map,
        position: position,
        icon: {
            path: google.maps.SymbolPath.CIRCLE,
            scale: 2,
            labelOrigin: new google.maps.Point(0, 0)
        },
        label: {
            text: text,
            fontSize: '12px',
            color: 'green'
        }
    });
    markers.push(marker);
}
function CreateStartEndMarker(position, url){
    var marker = new google.maps.Marker({
        map: map,
        position: position,
        icon: {
            url: url,
            scale: 2
        }
    });
    markers.push(marker);
}
function DeleteAllMarker(storage) {
    for (var i = 0; i < storage.length; i++) {
        storage[i].setMap(null);
    }
    storage = [];
}

//지도UI (출발지&도착지 바꾸기)
$('.mapWrap .mapWrap_tit').each(function(){
    $('.btn_lctn_change').on('click', function(e){
        var startLctn = $('#startLctn').val();
        var endtLctn = $('#endtLctn').val();

        $(this).parent().find('#startLctn').val(endtLctn);
        $(this).parent().find('#endtLctn').val(startLctn);
    });
});
// 상세보기 to 지도
$('.btn_mapWrap_back').on('click',function(){
    $('.mapWrap').removeClass('detailTransform');
    $('.mapWrap_tit .iptBox .ipt_txt').prop('disabled',false);
    $('#map').show();
    FocusPolyline(responseStorage.routes[indexStorage]);
});
// 지도 to 상세보기
$('.btn_mapLoad_detail').on('click', function(){
    $('.mapWrap').addClass('detailTransform');
    $('.mapWrap_tit .iptBox .ipt_txt').prop('disabled',true);
    $('#map').hide();
});
// 상세보기에서 '상세정보' 버튼 display
$('.mapLoad_detail_lst li').on('click',function(){
    $('.mapLoad_detail_lst li').removeClass('active');
    $(this).addClass('active');
});
// 상세보기에서 타임라인 보기
$('.btn_timeline').on('click',function(){
    $(this).parent().toggleClass('timeline');
});