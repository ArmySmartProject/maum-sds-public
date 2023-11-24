const hotel = $('#hotelValue').val();
const hotelId = $('#hotelId').val();
let now_date_state = 7;
let category_count = 10;

$(window).load(function() {
    //page loading delete
    $('#pageldg').addClass('pageldg_hide').delay(300).queue(function() { $(this).remove(); });
    $('#select_date').change(function() {
        calendar_self_input_date();
    });
    $('#cat_cnt').change(function () {
        category_count = $("#cat_cnt option:selected").data("cnt");
        // console.log(category_count);

        let data;
        let date = $("#select_date option:selected").data("date");
        let lang = $("#select_lang option:selected").data("lang");
        let count = category_count;
        if (!date) {
            date = 7;
        }
        if (date < 0) {
            let startDate = $('#fromDate').val();
            let endDate = $('#toDate').val();
            data = {host: hotelId, lang: lang, startDate: startDate, endDate: endDate, count: count};
        } else {
            data = {host: hotelId, lang: lang, date: now_date_state, count: count};
        }
        // console.dir(data);

        $.ajax({
            url: '/getCatCnt',
            data: data
        }).done(function (e) {
            // console.dir(e);
            draw_cc_table('cc_tbody', e.ccData);
        })
    });

    $(document).on('click', '.cc_text', function () {

        let category = $(this)[0].text;

        let data;
        let date = $("#select_date option:selected").data("date");
        let lang = $("#select_lang option:selected").data("lang");
        if (!date) {
            date = 7;
        }

        if (date < 0) {
            let startDate = $('#fromDate').val();
            let endDate = $('#toDate').val();
            data = {category: category, host: hotelId, lang: lang, startDate: startDate, endDate: endDate};
        } else {
            data = {category: category, host: hotelId, lang: lang, date: now_date_state};
        }

        $.ajax({
            url: '/getUq',
            data: data
        }).done(function(e) {
            // console.dir(e);
            draw_uq_table("uq_tbody", e.uqData);
        })
    });

    $('.btn_send').click(function() {
        let date = $("#select_date option:selected").data("date");
        let lang = $("#select_lang option:selected").data("lang");
        // console.log(date);
        now_date_state = date;
        if (!date) {
            alert("날짜를 선택 해주세요.");
            return;
        }

        if (date < 0) {
            //TODO : 직접 입력 시 핸들링
            let start_date = $("#fromDate").val();
            let end_date = $("#toDate").val();
            get_custom_info(hotel, lang, start_date, end_date).then(draw_page);
        }
        else {
            get_info(hotel, lang, date).then(draw_page);
        }
    });

    $('#set_email').on('click', function(){
        get_email_info(hotelId).then(res => {
            $("#email_input").find("input").val(res);
        });
    });

    $('#email_input .btn_send').on('click', function(){
        let email = $("#email_input").find("input").val();
        set_email_info(hotelId, email);
    });

    $("#email_text").keyup(function(e){
        if(e.keyCode == 13) {
            let email = $("#email_input").find("input").val();
            set_email_info(hotelId, email);
            $('#email_input').removeClass('on');
        };
    });

    // console.log(hotel);
    get_info(hotel, "1", "7").then(draw_page);
    // insert_channel(channel_data);
});

$(window).bind("pageshow", function (event) {
    sessionCheck();
});

//TODO : Back에서 받아야 할 값 목록

// Page Title
let page_title = "챗봇 통계(Chatbot Data)";
// Total Messages
let tm_num = 0;
// Total Users
let tu_num = 0;
// Avg.conversations.per user
let avg_num = 0;
// Weak understanding
let wu_num = 0;
// Total Email number
let te_num = 0;
// Total Message Per Hour / Active user Per Hour's Time Label
let date_label = ['AM 00','AM 01','AM 02','AM 03','AM 04','AM 05','AM 06','AM 07','AM 08','AM 08','AM 10','AM 11','AM 12','PM 13','PM 14','PM 15', 'PM 16', 'PM 17', 'PM 18', 'PM 19', 'PM 20', 'PM 21', 'PM 22', 'PM 23'];
// // Channel Data
// let channel_data = ["2017 한국농촌관광자원 국제포럼", "24GH명동센터", "4게스트하우스 서울역점"];
// Total Message Per Hour Data
let tm_data = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
// Active Users Per Hour Data
let au_data = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
// New User Count Data
let nu_data = [1, 1];
// PC/Mobile Count Data
let pm_data = [1, 1];
// Homepage/QR/KakaoTalk Count Data
let hq_data = [1, 1, 1];
// Concierge Category Data
let cc_data = [{content: "준비중입니다", count: 0}]
// UserQuestion Data
let uq_data = [{content: "준비중입니다", count: 0}]

function sessionCheck(){
    $.ajax({
        url:"/sessionCheck",
        cache: false,
        method: "POST"
    }).done(function(e) {
        if (e !== true) {
            window.location.href = "/";
        }
    })
}

function insert_channel(channel_data) {
    let data = "";

    for (channel in channel_data) {
        data += "<option>" + channel_data[channel] + "</option>";
    }

    $("#select_channel").append(data);
}

/* 수정 200227 AMR */
function calendar_self_input_date() {
    if (($("#select_date option:selected").text()) === "직접입력") {
        $(".ipt_date").removeAttr("disabled")
        $(".ipt_date").parents('.dlBox').removeClass('date_hide')
        $('.btn_send').addClass('positioning')
    } else {
        $(".ipt_date").attr("disabled", "disabled")
        $(".ipt_date").parents('.dlBox').addClass('date_hide')
        $('.btn_send').removeClass('positioning')
    };
}

function get_info(hotel, lang, date) {
    let data = {hotel: hotel, lang: lang, date: date};

    return new Promise(function(resolve, reject) {
        $.post("/getDrawDate", data, function(e) {
                // console.log("get_info");
                // console.dir(e);
                page_title = e.pageTitle;
                tm_num = e.tmNum;
                tu_num = e.tuNum;
                avg_num = e.avgNum;
                wu_num = e.wuNum;
                te_num = e.teNum;
                // date_label = e.dateLabel;
                tm_data = e.tmData;
                au_data = e.auData;
                nu_data = e.nuData;
                pm_data = e.pmData;
                hq_data = e.hqData;
                cc_data = e.ccData;
                uq_data = e.uqData;
                country_data = e.countryData;

                resolve();
            }
        );
    })
}

function draw_title(page_title) {
    $("#page_title").text(page_title);
}

function draw_main_num(tm_num, tu_num, avg_num, wu_num, te_num) {
    //Total Message Num
    $("#tm_num").text(tm_num);

    //Total User Num
    $("#tu_num").text(tu_num);

    //Avg.conversations per user
    $("#avg_num").text(avg_num);

    //Weak understanding
    $("#wu_num").text(wu_num);

    //Total Emails
    $("#te_num").text(te_num);
}

function draw_cc_table(eleId, data) {
    $(`#${eleId}`).empty();

    let data_html = "";

    for(index in data) {
        data_html += `<tr><td scope="col">`+(Number(index) + 1)+`</td><td><a class="cc_text">` + data[index].content + `</a></td><td class="al_r">` + data[index].count + `</td></tr>`
    }

    if (data.length < 1) {
        data_html += `<tr><td class="al_c" scope="col" colSpan="3">등록된 데이터가 없습니다.</td></tr>`
    }

    $("#" + eleId).append(data_html)
}

function draw_uq_table(eleId, data) {
    $(`#${eleId}`).empty();

    // console.dir(data);

    let data_html = "";

    for(index in data) {
        data_html += `<tr><td>` + data[index].content + `</td><td class="al_r">` + data[index].count + `</td></tr>`
    }

    if (data.length < 1) {
        data_html += `<tr><td class="al_c" scope="col" colSpan="3">등록된 데이터가 없습니다.</td></tr>`
    }

    $(`#${eleId}`).append(data_html);
}

function tbody_empty() {
    $("tbody").empty();
}

function draw_page() {
    // console.log("draw_page");
    draw_title(page_title);
    draw_main_num(tm_num, tu_num, avg_num, wu_num, te_num);
    draw_charts(date_label, tm_data, au_data, nu_data, pm_data, hq_data);
    tbody_empty();
    draw_cc_table("cc_tbody", cc_data);
    draw_uq_table("uq_tbody", uq_data);
}

function get_custom_info(hotel, lang, start_date, end_date) {
    // console.group("get_custom_info");
    // console.log(hotel);
    // console.log(lang);
    // console.log(start_date);
    // console.log(end_date);
    // console.groupEnd();

    let data = {hotel: hotel, lang: lang, startDate: start_date, endDate: end_date};

    // $.post("/getDrawCustomTest", data, function (e) {
    //     console.log("get_info");
    //     console.dir(e);
    // });

    return new Promise(function(resolve, reject) {
        $.post("/getDrawCustom", data, function(e) {
                // console.log("get_info");
                // console.dir(e);
                page_title = e.pageTitle;
                tm_num = e.tmNum;
                tu_num = e.tuNum;
                avg_num = e.avgNum;
                wu_num = e.wuNum;
                te_num = e.teNum;
                // date_label = e.dateLabel;
                tm_data = e.tmData;
                au_data = e.auData;
                nu_data = e.nuData;
                pm_data = e.pmData;
                hq_data = e.hqData;
                cc_data = e.ccData;
                uq_data = e.uqData;

                resolve();
            }
        );
    })
}

function get_email_info(hotelId) {
    let data = {host: hotelId};
    return new Promise(function(resolve, reject) {
        $.post("/getEmailInfo", data, function(e) {
                resolve(e);
            }
        );
    })
}

function set_email_info(hotelId, email) {
    let data = {host: hotelId, email: email};
    console.log(data);
    return new Promise(function(resolve, reject) {
        $.post("/setEmailInfo", data, function(e) {
                resolve();
            }
        );
    })
}
