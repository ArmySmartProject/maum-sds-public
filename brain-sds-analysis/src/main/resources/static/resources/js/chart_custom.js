
//New User Count
var randomScalingFactor = function() {
    return Math.round(Math.random() * 100);
};

const label = ['AM 00','AM 01','AM 02','AM 03','AM 04','AM 05','AM 06','AM 07','AM 08','AM 08','AM 10','AM 11','AM 12','PM 13','PM 14','PM 15', 'PM 16', 'PM 17', 'PM 18', 'PM 19', 'PM 20', 'PM 21', 'PM 22', 'PM 23'];
const data = [
    randomScalingFactor(),
    randomScalingFactor(),
    randomScalingFactor(),
    randomScalingFactor(),
    randomScalingFactor(),
    randomScalingFactor(),
    randomScalingFactor(),
    randomScalingFactor(),
    randomScalingFactor(),
    randomScalingFactor(),
    randomScalingFactor(),
    randomScalingFactor(),
    randomScalingFactor(),
    randomScalingFactor(),
    randomScalingFactor(),
    randomScalingFactor(),
    randomScalingFactor(),
    randomScalingFactor(),
    randomScalingFactor(),
    randomScalingFactor(),
    randomScalingFactor(),
    randomScalingFactor(),
    randomScalingFactor(),
    randomScalingFactor()
];
const nu_label = ['Existing User', 'Today',];
const pm_label = ['PC', 'Mobile',];
const hq_label = ['Homepage', 'QR', 'KakaoTalk'];

function draw_charts(date_label, tm_data, au_data, nu_data, pm_data, hq_data) {
    remove_canvas();
    make_canvas();
    draw_chart("totalMessagesPerHour", get_line_chart_data(date_label, tm_data));
    draw_chart("activeUsersPerHour", get_line_chart_data(date_label, au_data));
    draw_chart("newUserCount", get_doughnut_chart_data(nu_label, nu_data));
    draw_chart("pcMobileCount", get_doughnut_chart_data(pm_label, pm_data));
    draw_chart("hpQrCount", get_doughnut_chart_data(hq_label, hq_data));
    draw_char_sum_num(nu_data, pm_data, hq_data);
}

function remove_canvas() {
    $("canvas").remove();
    $("#map1").remove();
}

function make_canvas() {
    $("#dir_totalMessagesPerHour").append('<canvas id="totalMessagesPerHour"></canvas>')
    $("#dir_activeUsersPerHour").append('<canvas id="activeUsersPerHour"></canvas>')
    $("#dir_newUserCount").append('<canvas id="newUserCount"></canvas>')
    $("#dir_pcMobileCount").append('<canvas id="pcMobileCount"></canvas>')
    $("#dir_hpQrCount").append('<canvas id="hpQrCount"></canvas>')
    $("#mapDiv").append('<div id="map1"></div>')
    $('#map1').vectorMap({
        map: 'world_mill_ko',
        // panOnDrag: true,
        zoomOnScroll: false,
        regionStyle: {
            initial: {
                fill: '#f4f3ff'
            },
        },
        focusOn: {
            x: 0.5,
            y: 0.5,
            scale: 0,
            animate: true
        },
        series: {
            regions: [{
                // [D] scale이 아닌 접속자 수에 따른 색상 변화가 필요합니다.
                values: country_data,
                scale: ['#FFE2E2', '#710000'],
                normalizeFunction: 'polynomial'
            }]
        },
        onRegionTipShow: function(e, el, code){
            if (country_data[code] === undefined) {
                country_data[code] = 0;
            }
            el.html(el.html()+' (Count : '+country_data[code]+')');
        }});
    $('.jvectormap-container').css({'background-color' : 'rgb(255,255,255)'});
}

function draw_char_sum_num(nu_data, pm_data, hq_data) {
    let nu_sum = nu_data.reduce((a, b) => a + b);
    let pm_sum = pm_data.reduce((a, b) => a + b);
    let hq_sum = hq_data.reduce((a, b) => a + b);
    $("#nu_sum").text(nu_sum);
    $("#pm_sum").text(pm_sum);
    $("#hq_sum").text(hq_sum);
}

function draw_chart(eleId, chart_data) {
    let ctx = document.getElementById(eleId).getContext('2d');
    window.myLine = new Chart(ctx, chart_data);
}


function get_line_chart_data(labels, data) {
    return {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Total',
                backgroundColor: 'rgba(215,46,46,1.0)',
                borderColor: 'rgba(215,46,46,1.0)',
                data: data,
                fill: false
            }]
        },
        options: {
            maintainAspectRatio: false,
            responsive: true,
            title: {
                display: false,
            },
            tooltips: {
                mode: 'index',
                intersect: false,
            },
            hover: {
                mode: 'nearest',
                intersect: true
            },
            scales: {
                xAxes: [{
                    display: true,
                    scaleLabel: {
                        display: false,
                        labelString: 'Time'
                    },
                    ticks: {
                        autoSkip: true,
                        maxRotation: 0,
                        minRotation: 0
                    }
                }],
                yAxes: [{
                    display: true,
                    scaleLabel: {
                        display: false,
                        labelString: 'Value'
                    }
                }]
            }
        }
    };
}

function get_doughnut_chart_data(labels, data) {
    return {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: data,
                backgroundColor: [
                    'rgba(215,46,46,1.0)',
                    'rgba(239,132,46,1.0)',
                ],
            }]
        },
        options: {
            maintainAspectRatio: false,
            responsive: true
        }
    };
}