var host;
var lang;
var start_intent;
var qr_connect = false;

// QR 접속 여부 확인 (QR용 html에 host ID를 가진 tag가 존재
if ($('#host').length == 1) {
    qr_connect = true;

    host = $('#host').val();
    lang = $('#lang').val();
    start_intent = $('#start_intent').val();
    if (start_intent === "default") {
        console.log("hello");
        start_intent = "처음으로";
    }

    console.log(start_intent);
}

$(document).ready(function (){
    $('#meta').remove();

    // 채팅입력 (Shift + Enter)
    $('#inputArea').keyup(function (event) {

        if (event.keyCode == 13 && event.shiftKey) {
            var chatTxt = this.value;
            this.value = chatTxt.substring(0,caret)+"\n"+chatTxt.substring(carent,chatTxt.length-1);
            event.stopPropagation();
        } else if (event.keyCode == 13){
            $('#btn_chat').trigger('click');
        }
    });

    // chatbot open
    $('#btn_goChat').on('click', function(){

        $(this).addClass('goChat_hide');
        $('#chatUI_wrap').addClass('chatUI_show');

        // 스크롤 하단으로 이동
        var scrollHeight = $('.lst_talk').height();

        $('.chatUI_mid').animate({
            scrollTop: scrollHeight
        },150);
        return false;
    });

    // chatbot close
    $('.btn_chatClose').on('click', function(){
        $('#btn_goChat').removeClass('goChat_hide');
        $('#chatUI_wrap').addClass('chatUI_hide');
        $('#chatUI_wrap').removeClass('chatUI_show');
        setTimeout(function() {
            $('#chatUI_wrap').removeClass('chatUI_hide');
        }, 800);
    });

    // 날짜, 요일 시간 정의
    var year  = new Date().getFullYear();  //현재 년도
    var month = new Date().getMonth()+1;  //현재 월
    var date  = new Date().getDate();  //현재 일
    var week  = ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일'];	  //요일 정의
    var thisWeek  = week[new Date().getDay()];	//현재 요일

    var ampm = new Date().getHours() >= 12 ? '오후' : '오전';
    var	thisHours = new Date().getHours() >=13 ?  new Date().getHours()-12 : new Date().getHours(); //현재 시
    var	thisMinutes = new Date().getMinutes() < 10 ? '0' + new Date().getMinutes() : new Date().getMinutes(); //현재 분
    let NowTime = year + "." + month + "." + date + " " + ampm + " " + thisHours + ':' + thisMinutes;

    // 사이즈 정의
    var winHeight = $(window).height();

    $('#cahtbotWrap').each(function(){
        var cahtbotWrapHeight = $('#cahtbotWrap').height();
        $('.chatUI_mid').css({
            'height': Math.round(cahtbotWrapHeight-145),
        });
    });

    // 첫멘트 시간
    $('.chatUI_mid .lst_talk li.bot .cont:last-child').append('<em class="date"><b>' + ampm + '</b>' + NowTime + '</em>');

    // 내용있을 시 스크롤 최하단
    $('.chatUI_mid').scrollTop($('.chatUI_mid')[0].scrollHeight);


    // 추천질문 (text 출력)
    $('.info_btnBox li button').on('click', function() {
        var recomQust = $(this).text();

        $('#inputArea').val(recomQust);
        $('#btn_chat').trigger('click');

        $('.chatbot_contents .bot_infoBox').css({
            'display':'none',
        });
        $('.chatbot_contents .lst_talk').css({
            'display':'block',
        });

        $('#inputArea').val('');
        $('.chatUI_mid').scrollTop($('.chatUI_mid')[0].scrollHeight);

    });


    // 채팅입력 (text 출력)
    $('#btn_chat').on('click', function() {
        // textarea 텍스트 값 및 엔터처리
        var textValue = $('#inputArea').val().replace(/(?:\r\n|\r|\n)/g, '<br>');

        // 채팅창에 text 출력
        if( $('#inputArea').val().replace(/\s/g,"").length == 0){
            // text가 없으면 실행
        } else {
            // text가 있으면 실행
            answer("utter", textValue, false);
        }
    });
    // Responsed 버튼 입력 (Text 출력)
    $(document).on('click', '.intent', function(){

        // console.dir(this);

        let intent = $(this).data('intent');
        let display = $(this).data('display');

        // console.log("intent : " + intent + ", display : " + display);

        answer("intent", intent, display);
    });

    // Response
    function answer(type, value, display){
        let year  = new Date().getFullYear();  //현재 년도
        let month = new Date().getMonth()+1;  //현재 월
        let date  = new Date().getDate();  //현재 일

        let ampm = new Date().getHours() >= 12 ? '오후' : '오전';
        let	thisHours = new Date().getHours() >=13 ?  new Date().getHours()-12 : new Date().getHours(); //현재 시
        let	thisMinutes = new Date().getMinutes() < 10 ? '0' + new Date().getMinutes() : new Date().getMinutes(); //현재 분
        let NowTime = year + "." + month + "." + date + " " + ampm + " " + thisHours + ':' + thisMinutes;

        if (!display) {
            display = value;
        }
        let data = {"type": type, "input":value.replace("<br>", ""), "host": host, "lang": lang};

        $('.chatUI_mid .lst_talk').append(
            '<li class="user"> \
                <span class="cont"> \
                    <em class="txt">' + display + '</em> \
						<em class="date">' + NowTime + '</em> \
					</span> \
				</li>'
        );
        $('.chatbot_contents .bot_infoBox').css({
            'display':'none',
        });
        $('.chatbot_contents .lst_talk').css({
            'display':'block',
        });
        $('#inputArea').val('');

        callBackend(data);

        $('.chatUI_mid').scrollTop($('.chatUI_mid')[0].scrollHeight);
    }

    function sendHostInfo(e) {

        data = e.data;
        host = data.host;
        lang = data.lang;
        let placeholderText = "";

        switch (lang) {
            case "1": placeholderText = "메세지를 입력해주세요";
                break;
            case "2": placeholderText = "Please enter a message.";
                break;
            case "3": placeholderText = "请输入信息。";
                break;
            case "4": placeholderText = "メッセージを入力してください";
                break;
            default:
        }

        $("#inputArea").attr("placeholder", placeholderText);

        $.ajax({
            url: "/chat/hostInfo",
            data: JSON.stringify(data),
            type: "POST",
            contentType: 'application/json',
        }).done(function (response) {
            callBackend({"type": "intent", "input":start_intent, "host": host, "lang":lang})
        });
    }

    // call Backend
    function callBackend(data) {
        $.ajax({
            url: "/chat/request",
            data: JSON.stringify(data),
            type: "POST",
            contentType: 'application/json',
        }).done(function (response) {
            // console.dir(response);

            for(let index = 0 ; index < response.length; index++) {
                selectResponseType(response[index]);
            }

            selectResponseType(response);
        });

        $('.chatbot_contents .bot_infoBox').css({
            'display':'none',
        });
        $('.chatbot_contents .lst_talk').css({
            'display':'block',
        });
    }

    function selectResponseType(response) {

        var ans = response.answer;
        if (ans.includes("|||MAP|||")) {
            var res = ans.split("|||MAP|||");
            botResponseMap(res[1]);
            response.answer = res[0];
        }

        if (!!response.answer) botResponseText(response.answer);
        if (response.list.length > 0) botResponseList(response.list);
        botResponseButton(response.buttons);
        if (response.carousel.length > 0) botResponseCarousel(response.carousel);
        if (!!response.farewell) botResponseText(response.farewell);
        botResponseTime();
        makeTxtInnerButton();
    }

    function botResponseTime() {
        // 날짜, 요일 시간 정의
        let year  = new Date().getFullYear();  //현재 년도
        let month = new Date().getMonth()+1;  //현재 월
        let date  = new Date().getDate();  //현재 일
        let week  = ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일'];	  //요일 정의
        let ampm = new Date().getHours() >= 12 ? '오후' : '오전';
        let	thisHours = new Date().getHours() >=13 ?  new Date().getHours()-12 : new Date().getHours(); //현재 시
        let	thisMinutes = new Date().getMinutes() < 10 ? '0' + new Date().getMinutes() : new Date().getMinutes(); //현재 분
        let NowTime = year + "." + month + "." + date + " " + ampm + " " + thisHours + ':' + thisMinutes;

        $('.bot:last').append(
            '<div class="date">'+ NowTime +'</div>'
        );
        $('.chatUI_mid').scrollTop($('.chatUI_mid')[0].scrollHeight);

    }
    function botResponseText(botMsg) {
        $('.chatUI_mid .lst_talk').append(
            '<li class="bot"> \
                <span class="cont"> \
                    <em class="txt">' +
            botMsg +
            '</em> \
           </span>\
       </li>'
        );
        $('.chatUI_mid').scrollTop($('.chatUI_mid')[0].scrollHeight);
    }
    function botResponseList(list_response) {
        // console.dir(list_response);

        let list_li = "";

        list_response.forEach(function (list_ele) {
            list_li += '<li><a class="intent" href="#" data-display="'+ list_ele.display +'" data-intent="'+ list_ele.intent.intent +'" >' + list_ele.display  + '</a></li>';
        });

        let list_html =
            '<li class="bot"> \
            <div class="bot_msg">\
                <div class="btnLst">';
        list_html += '<ul>' + list_li + '</ul>\
        </div>\
        </div>\
        </li>';

        $('.chatUI_mid .lst_talk').append(list_html);
        $('.chatUI_mid').scrollTop($('.chatUI_mid')[0].scrollHeight);

    }

    function botResponseButton(button_response) {
        let button_li = "";

        if (button_response.length > 0) {
            button_response.forEach(function (button_ele) {
                button_li += '<li><a class="intent" href="#" data-display="'+ button_ele.display +'" data-intent="'+ button_ele.intent.intent +'">' + button_ele.display  + '</a></li>';
            });
        }

        button_li += '<li><a class="intent btnStart" href="#" data-display="처음으로" data-intent="처음으로">처음으로</a></li>';

        $('.chatUI_mid .lst_talk').append(
            '<li class="bot"> \
            <div class="bot_msg">\
                <div class="btnItem">\
                    <ul>'+button_li+' </ul>\
            </div>	\
        </div>\
        </li>'
        );
        $('.chatUI_mid').scrollTop($('.chatUI_mid')[0].scrollHeight);

    }

    function botResponseCarousel(carousel_response) {
        let carousel_div = "";
        // console.dir(carousel_response);

        carousel_response.forEach(function (carousel_ele) {
            let intent = carousel_ele.intent;

            if (!intent.displayText)  {
                // console.log("displayText Null")
                intent.displayText = "";
            }

            carousel_div +=
                '<div class="swiper-slide"> \
                    <a class="swiper_item intent" href="#" target="_self" data-intent="'+ intent.intent +'" data-display="'+ carousel_ele.display +'">\
                        <span class="item_img"><img src="' + intent.displayUrl + '" onError="this.src=\'/resources/images/img_nopicture.png\'"></span> \
                        <span class="item_tit">'+ intent.displayName +'</span> \
						<span class="item_txt">'+ intent.displayText +'</span> \
					</a> \
				</div>';
        });

        $('.chatUI_mid .lst_talk').append(
            '<li class="bot botMsg_swiper"> \
                <div class="swiper-wrapper">'
            + carousel_div +
            '</div> \
        <div class="swiper-pagination"></div> \
            <div class="swiper-button-prev"></div> \
            <div class="swiper-button-next"></div> \
        </li>'
        );

        let swiper_option = {
            init : false,
            speed : 200,
            slidesPerView:2,
            spaceBetween: 10,
            centeredSlides: false,
            loop: false,
            pagination: {
                el: '.swiper-pagination',
                clickable: true,
            },
            navigation: {
                nextEl: '.swiper-button-next',
                prevEl: '.swiper-button-prev',
            },
        };

        if (carousel_response.length > swiper_option.slidesPerView) {
            swiper_option.loop = true;
        }

        let swiper = new Swiper('.botMsg_swiper', swiper_option);
        $('.botMsg_swiper:last')[0].swiper.init();

        $('.chatUI_mid').scrollTop($('.chatUI_mid')[0].scrollHeight);

    }

    function botResponseMap(map_response) {
        var jsonRes = JSON.parse(map_response);
        // console.log(jsonRes)

        var mapSelectHtml =
            '   <li class="bot"> \
                <div class="bot_msg"> \
                    <div class="btnLst"> \
                        <span class="txt txt_radius">' + jsonRes.answer + '</span> \
                    <div class="iptBox"> \
                        <dl class="dl_ipt"> \
                            <dt>' + jsonRes.texts.start + '</dt> \
                            <dd> \
                                <select class="select start">';

        var startCustomCheck = false;
        jsonRes.starts.forEach(function(start) {
            mapSelectHtml += '<option data-location=';
            if(start.location.hasOwnProperty("get"))
            {
                mapSelectHtml += '"' + start.location.get + '"';
                if(start.location.get == "custom")
                {
                    mapSelectHtml += 'value="direct"  '
                }
            }
            else
            {
                mapSelectHtml += '\'{"lat": ' + start.location.lat + ', "lng": ' + start.location.lng + '}\' ';
            }
            if(start.selected){
                mapSelectHtml += 'selected';
                if(start.location.get == "custom")
                {
                    startCustomCheck = true;
                }
            }
            mapSelectHtml +='> ' + start.name + '</option>';
        });

        mapSelectHtml +=
            '                       </select> \
                                    <input type="text" name="selboxDirect" class="ipt_txt selboxDirect startInput" value="" \
            ';
        if(startCustomCheck){
            mapSelectHtml += 'style="display: inline-block;"'
        }
        mapSelectHtml += '> \
                            </dd> \
                        </dl> \
                        <dl class="dl_ipt"> \
                            <dt>' + jsonRes.texts.end + '</dt> \
                            <dd> \
                                <select class="select end"> \
        ';

        var endCustomCheck = false;
        jsonRes.ends.forEach(function(end) {
            mapSelectHtml += '<option data-location=';
            if(end.location.hasOwnProperty("get"))
            {
                mapSelectHtml += '"' + end.location.get + '"';
                if(end.location.get == "custom")
                {
                    mapSelectHtml += 'value="direct" ';
                }
            }
            else
            {
                mapSelectHtml += '\'{"lat": ' + end.location.lat + ', "lng":' + end.location.lng + '}\' '
            }
            if(end.selected){
                mapSelectHtml +='selected';
                if(end.location.get === "custom")
                {
                    endCustomCheck = true;
                }
            }
            mapSelectHtml +='> ' +  end.name + '</option>';
        });

        mapSelectHtml +=
            ' \
                                    </select> \
                                    <input type="text" name="selboxDirect" class="ipt_txt selboxDirect endInput" value="" \
            ';
        if(endCustomCheck){
            mapSelectHtml += 'style="display: inline-block;"'
        }
        mapSelectHtml += '\
                                > \
                            </dd> \
                        </dl> \
                    </div> \
                    <div class="btnBox"> \
                        <button type="button" class="btn_point btn_map">' + jsonRes.texts.button + '</button> \
                    </div> \
                </div> \
            </div>  \
            </li> \
         ';

        $('.chatUI_mid .lst_talk').append(
            mapSelectHtml
        );

        $('.chatUI_mid').scrollTop($('.chatUI_mid')[0].scrollHeight);

        //191129 추가
        //대화Type (출발지&도착지)
        $('.dl_ipt .select').on('change', function(){
            if ($(this).val() == 'direct') {
                $(this).parent().find('.selboxDirect').show();
            } else {
                $(this).parent().find('.selboxDirect').hide();
            }
        });

        //지도UI
        $('.btn_map').on('click', function(e){
            $('.mapWrap').removeClass('map_hide');
            $('#map').show();
            // 경로 그리기
            var start = $(this).parent().parent().find('select.start')[0];
            var startCoord = start.options[start.selectedIndex].getAttribute('data-location');
            var startData;
            if(startCoord == "current" || startCoord == "custom"){startData = startCoord;}
            else {startData = JSON.parse(startCoord);}

            var end = $(this).parent().parent().find('select.end')[0];
            var endCoord = end.options[end.selectedIndex].getAttribute('data-location');
            var endData;
            if(endCoord == "current" || endCoord == "custom"){endData = endCoord;}
            else {endData = JSON.parse(endCoord);}

            var startInput = $(this).parent().parent().find('input.startInput').val();
            var endInput = $(this).parent().parent().find('input.endInput').val();

            if(startCoord == 'custom') { $('#startLctn').val(startInput); }
            else { $('#startLctn').val(start.value); }

            if(endCoord == 'custom') { $('#endtLctn').val(endInput); }
            else { $('#endtLctn').val(end.value); }

            var panel = document.getElementById('panel');
            directionsBySelection(startData, endData,'TRANSIT', startInput, endInput, panel);
        });

        $('.btn_mapWrap_close').on('click',function(){
            $('.mapWrap').removeClass('detailTransform');
            $('.mapWrap_tit .iptBox .ipt_txt').prop('disabled',false);
            $('.mapWrap').addClass('map_hide').delay(500);
        });

    }

    $(document).on('click', '#chatUI_wrap a', function(event){
        event.preventDefault();
        let href = $(this).attr("href");
        if (href == "#") return;
        let win = window.open(href, "_blank");
        win.focus();
    });

    if (qr_connect) {
        $("#btn_goChat").trigger("click");
        $("#btn_close").remove();
        let HostInfo = {'data' : {'host' : host, 'lang' : lang}};
        sendHostInfo(HostInfo);
    }
});

$(window).resize(function (){
    $('#cahtbotWrap').each(function(){
        var cahtbotWrapHeight = $('#cahtbotWrap').height();
        $('.chatUI_mid').css({
            'height': Math.round(cahtbotWrapHeight-145),
        });
    });
});

function makeTxtInnerButton() {

    $('.txt').each(function(){
        var txt_btn = $(this).find('a');
        var txt_btnLength = $(this).find('a').length;
        if (txt_btnLength > 1) {
            $(this).find('a').wrapAll('<div class="txt_btns"></div>');
        } else {
            $(this).find('a').wrapAll('<div class="txt_btn"></div>');
        }
    });
}

//지도 전체화면
$('.btn_map').on('click', function(){
    $('#chatUI_wrap').append(' \
                <div class="mapWrap"> \
                    <button class="btn_mapWrap_close">닫기</button> \
                    지도  \
                </div>'
    );
    //mapWrap 삭제
    $('.btn_mapWrap_close').on('click',function(){
        $(this).closest('.mapWrap').addClass('map_hide').delay(500).queue(function() { $(this).remove(); });
    });
});