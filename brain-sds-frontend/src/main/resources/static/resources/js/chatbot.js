var host;
var lang;
var supportedLangs; // 챗봇에 지원되는 언어 code 목록 (ex. 1,2,3)
var qrLocation;
var country;
var sessionId;
var previousSess;
var previousTime = 'n';
var previousYn = 'n';
var serverURL;

var writeMessage;
var createLoadingMsg;
var deleteLoadingMsg;
var createUsrMsg;
var socketRoomId;
var afkTimer;
var afkTime = 30*1000; // ms
var recentInput = "";
var sideScreen = false;
var googleMapAPILoaded = false;
var firstDisplay;


// 채팅입력 (Shift + Enter)
$('#inputArea').keyup(function (event) {
    if (event.keyCode == 13 && event.shiftKey) {
        var chatTxt = this.value;
        this.value = chatTxt.substring(0,caret)+"\n"+chatTxt.substring(carent,chatTxt.length-1);
        event.stopPropagation();
    } else if (event.keyCode == 13){
        if($("#inputArea").val().replace(/\n/gi, "").replace(/\s/gi,"")===""){
            $("#inputArea").val("")
        }else{
            $('#btn_chat').trigger('click');
        }
    }
});

$(document).ready(function (){
    $(document).on("click",".txt_btn a",function(){
        addLinkClickLog(this.href);
    });

    var chatOpened = false;
    var chatBtnClicked = false;
    // 한번 접속한 브라우져의 쿠키를 저장하기 위해
    var mainCookie = getCookie("mainCookie");

    // AMR 200514 챗봇 intro popup 닫기
    $('#start_chat').on('click', function(){
        $('#chatUI_wrap').removeClass('intro_open');
    });

    $('#btn_goChat').on('click', chatOpen);
    $('.btn_chatClose').on('click', chatClose);

    // floating animation 끝난 뒤 chatbot open
    $('#btn_goChat').on("animationend", function() {
        if (mainCookie === undefined && !chatBtnClicked && checkDevice() !== 'MOBILE') {
            chatOpen();
            setCookie("mainCookie", "mainCookie", 30);
        }
    });

    function afkCheck(){
        if(chatOpened && recentInput!='선톡' && recentInput!='지도' && !sideScreen){
            console.log('AFK detected');
            var data = {"type": "intent", "input":"선톡",
                "host": host, "lang": lang,
                "jsonData": JSON.stringify(getJsonData())};
            callWSServer(data);
            clearTimeout(afkTimer);
        }
    }

    function chatOpen () {
        chatOpened = true;
        chatBtnClicked = true;
        window.parent.postMessage("chatbot_open", "*");
        $('#btn_goChat').addClass('goChat_hide');
        $('.chatGreeting').addClass('chatGreeting_hide');
        $('#chatUI_wrap').addClass('chatUI_show');
        var scrollHeight = $('.lst_talk').height();
        $('.chatUI_mid').animate({
            scrollTop: scrollHeight
        }, 0);

        // timeout 30 sec
        clearTimeout(afkTimer);
        afkTimer = setTimeout(afkCheck, afkTime); // ms

        // AMR 200514 챗봇 intro popup 열기
        // $('#chatUI_wrap').addClass('intro_open');
    }

    // chatbot close
    function chatClose () {
        chatOpened = false;
        $('#btn_goChat').removeClass('goChat_hide');
        $('.chatGreeting').removeClass('chatGreeting_hide');
        $('#chatUI_wrap').addClass('chatUI_hide');
        $('#chatUI_wrap').removeClass('chatUI_show');
        $('.chatAside').removeClass('aside_show');
        sideScreen = false;
        setTimeout(function() {
            $('#chatUI_wrap').removeClass('chatUI_hide');
            $('.chatAside').removeClass('aside_hide');
            window.parent.postMessage("chatbot_close", "*");
        }, 0);
        clearTimeout(afkTimer);
    }

    // chatbot open or close
    function chatOpenToggle(chatOpenClose) {
        if (!chatOpenClose || chatOpenClose === 'toggle') {
            if (chatOpened) {
                chatClose();
            } else {
                chatOpen();
            }
        } else if (chatOpenClose === 'open' && !chatOpened) {
            chatOpen();
        } else if (chatOpenClose === 'close' && chatOpened) {
            chatClose();
        }
    }

    function setCookie(name, value, expireminute ) {
        var exdate = new Date();
        exdate.setMinutes(exdate.getMinutes()+expireminute);
        document.cookie = name +  "=" + escape(value)
            + ((expireminute == null) ? "" : ";expires=" + exdate.toUTCString())
            + "; SameSite=None; Secure";
    }

    function getCookie(cookie_name) {
        var x, y;
        var val = document.cookie.split(';');

        for (var i = 0; i < val.length; i++) {
            x = val[i].substr(0, val[i].indexOf('='));
            y = val[i].substr(val[i].indexOf('=') + 1);
            x = x.replace(/^\s+|\s+$/g, '');
            if (x == cookie_name) {
                return unescape(y);
            }
        }
    }

    // 날짜, 요일 시간 정의
    var year  = new Date().getFullYear();  //현재 년도
    var month = new Date().getMonth()+1;  //현재 월
    var date  = new Date().getDate();  //현재 일
    var week  = new Array('일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일');	  //요일 정의
    var thisWeek  = week[new Date().getDay()];	//현재 요일

    var ampm = new Date().getHours() >= 12 ? 'PM' : 'AM';
    var	thisHours = new Date().getHours() >=13 ?  new Date().getHours()-12 : new Date().getHours(); //현재 시
    var	thisMinutes = new Date().getMinutes() < 10 ? '0' + new Date().getMinutes() : new Date().getMinutes(); //현재 분
    var NowTime = year + "." + month + "." + date + " " + ampm + " " + thisHours + ':' + thisMinutes;

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
            'display':'none'
        });
        $('.chatbot_contents .lst_talk').css({
            'display':'block'
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

    $('#return_chatbot').on('click', function() {
        endCounseling();
    });

    // Responsed 버튼 입력 (Text 출력)
    $(document).on('click', '.intent', function(){

        var intent = $(this).data('intent');
        var display = $(this).data('display');

        answer("intent", intent, display);
    });

    // 2020.7.13 PM 9:45
    function getNowTime() {

        var year  = new Date().getFullYear();  //현재 년도
        var month = new Date().getMonth()+1;  //현재 월
        var date  = new Date().getDate();  //현재 일

        var ampm = new Date().getHours() >= 12 ? 'PM' : 'AM';
        var	thisHours = new Date().getHours() >=13 ?  new Date().getHours()-12 : new Date().getHours(); //현재 시
        var	thisMinutes = new Date().getMinutes() < 10 ? '0' + new Date().getMinutes() : new Date().getMinutes(); //현재 분
        var NowTime = year + "." + month + "." + date + " " + ampm + " " + thisHours + ':' + thisMinutes;

        return NowTime;
    }

    // Response
    function answer(type, value, display){

        var NowTime = getNowTime();
        var input = value.replace("<br>", "");

        if (!display) {
            display = value;
        }

        if (input.length > 100) {
            input = input.substr(0,100);
        }

        var data = {"type": type, "input": input,
            "display": display,
            "host": host, "lang": lang,
            "jsonData": JSON.stringify(getJsonData())};

        createUsrMsg(display, NowTime);

        callWSServer(data);
    }

    function sendHostInfo(e) {
        data = e.data;

        if (data.hasOwnProperty('chatOpenClose')) {
            chatOpenToggle(data.chatOpenClose);
            return;
        }

        if(data.data==="resetSession"){
            resetSession();
            window.parent.postMessage({data:"resetSessionCallback"}, "*");
            return;
        }

        if(data.host === undefined && data.lang === undefined){
            // host, lang undefined 일시 sds 로직 오류로 인한 분기
            console.log("host, lang is undefined.(" + data.type +")");
        } else {
            if (data.type === 'nofloat_change_lang') {
                var noFloatLang = '';

                if ( data.lang === 'kor' ) noFloatLang = '1';
                else if ( data.lang === 'eng' ) noFloatLang = '2';
                else if ( data.lang === 'jpn' ) noFloatLang = '3';
                else if ( data.lang === 'chn' ) noFloatLang = '4';

                changeTextHolder(noFloatLang);
                // resetSession();
                callWSServer({"type": "intent", "input": "처음으로",
                    "display": getInitComment(noFloatLang), "host": host, "lang":noFloatLang,
                    "jsonData": JSON.stringify(getJsonData())});
                return;
            }

            host = data.host;
            lang = data.lang;
            supportedLangs = data.supportedLangs;
            qrLocation = data.qrLocation;
            country = data.country;
            previousSess = data.previousSess;
            serverURL = data.chatServerURL ? data.chatServerURL : 'http://10.50.1.19:50000';

            console.log('chatWSServer URL is ' + serverURL);

            if (qrLocation !== "" && qrLocation !== undefined) {
                chatOpen();
            }

            // 언어 icon setting, 언어가 지원되는 지 검사.
            lang = showLanguageFlag(lang, supportedLangs);

            changeTextHolder(lang);

            $.ajax({
                url: "/chat/hostInfo",
                data: JSON.stringify(data),
                type: "POST",
                contentType: 'application/json'
            }).done(function (response) {
                createUserRoom();
            });
        }
    }

    window.addEventListener("message", sendHostInfo, false);

    function changeTextHolder(lang, bot) {
        var placeholderText = '';
        if (!bot) bot = true;
        switch (lang.toString()) {
            // KOR
            case "1":
                if (bot) placeholderText = "메세지를 입력해 주세요";
                else  placeholderText = "(상담사) 메세지를 입력해 주세요";
                break;
            // ENG
            case "2":
                if (bot) placeholderText = "Please enter a message.";
                else placeholderText = "(Agent) Please enter a message.";
                break;
            // JPN
            case "3":
                if (bot) placeholderText = "メッセージを入力してください";
                else placeholderText = "(カウンセラー) メッセージを入力してください";
                break;
            // CHN
            case "4":
                if (bot) placeholderText = "请输入信息。";
                else placeholderText = "(諮詢師) 请输入信息。";
                break;
            default:
        }

        $("#inputArea").attr("placeholder", placeholderText);
    }

    ///////////////////////////////////////////////////

    // var userId = sessionId;
    var userType = 'user';

    // var csService = host;
    var csCategory = '';

    var supportType = 'bot';

    console.log('[session] / [supportType]   : ' + sessionId + ' / ' + supportType);

    // socket client for TALKING
    var socketClient;
    var curRoomId = '';
    var isCounseling = false;

    // user용 실행 함수 : room 생성 및 chat window 활성화
    function createUserRoom() {
        manageSession();

        var bot = true;
        // if (supportType === 'supporter') {
        //     bot = false;
        // }

        socketClient = new SocketClient(userType, sessionId);
        socketClient.setEventListeners(writeMessage, createLoadingMsg, deleteLoadingMsg, writeChatList, handleChatEndUI,
            writePreviousMessages, chatbotFirstMsg);

        socketClient.createNJoinRoom(bot, host, csCategory).then(function(_data) {
            curRoomId = _data.roomId;
            var copyPreviousYn = previousYn;
            var data = {"type": "intent", "input":"처음으로",
                "display": getInitComment(lang),
                "host": host, "lang":lang,
                "jsonData": JSON.stringify(getJsonData())};
            callWSServer(data);

            // 공지사항 호출
            if (copyPreviousYn == 'y') previousYn = 'y';
            var data = {"type": "intent", "input":"챗봇공지사항",
                "display": "챗봇공지사항",
                "host": host, "lang":lang,
                "jsonData": JSON.stringify(getJsonData())};
            callWSServer(data);
        });
    }

    writeMessage = function(roomId, talker, talkObj) {

        if (roomId !== socketRoomId && roomId !== curRoomId) {
            return;
        }
        var msg = talkObj.message;
        var date = talkObj.date;
        var time = talkObj.time;
        var timeDetail = talkObj.timeDetail;
        var adapterResponse = talkObj.adapterResponse;
        if (!date) {
            date = getDate();
            talkObj.date = date;
        }
        if (!time) {
            time = getTime();
            talkObj.time = time;
        }
        if (!timeDetail) {
            timeDetail = getTimeWithSecond();
            talkObj.timeDetail = timeDetail;
        }

        // if (talker === 'me') {
        //     createUsrMsg(adapterResponse);
        // } else
        if (talker === 'other') {
            if (!adapterResponse) {
                createBotMsg({answer: msg});
            } else {
                createBotMsg(adapterResponse);
            }
        } else if (talker === 'system') {
            createSystemMsg(msg);
        } else if (talker === 'supporter') {
            createCounselorMsg(msg);
        } else if (talker === 'alarm') {
            createAlarmMsg(talkObj);
        }

        if (previousTime === 'y') {
            var NowTime = timeDetail;
            $('.bot:last').append(
                '<div class="date">'+ NowTime +'</div>'
            );
            previousTime = 'n';
        }

        $('.chatUI_mid').scrollTop($('.chatUI_mid')[0].scrollHeight);
    };

    createLoadingMsg = function() {
        var classList;
        if (!isCounseling) {
            classList = 'bot loading';
        } else if (isCounseling) {
            classList = 'bot counselor loading';
        }

        $('.chatUI_mid .lst_talk').append(
            '<li class="' + classList +'"> \
                 <div class="bot_msg"> \
                      <div class="txt"> \
                           <span class="chatLoading"> \
                                <em class="chatLoading_item01"></em> \
                                <em class="chatLoading_item02"></em> \
                                <em class="chatLoading_item03"></em> \
                           </span> \
                      </div> \
                      <div class="date">' + getNowTime() + '</div> \
                 </div> \
				     </li>'
        );
        $('.chatbot_contents .bot_infoBox').css({'display':'none'});
        $('.chatbot_contents .lst_talk').css({'display':'block'});

        $('.chatUI_mid').scrollTop($('.chatUI_mid')[0].scrollHeight);
    };

    deleteLoadingMsg = function() {
        console.log('deleteLoadingMsg');
        $('.chatUI_mid .loading').remove();
        $('.chatUI_mid').scrollTop($('.chatUI_mid')[0].scrollHeight);
    };

    function createBotMsg(response) {
        $('.chatUI_mid .loading').remove();
        for(var index = 0 ; index < response.length; index++) {
            selectResponseType(response[index]);
        }
        selectResponseType(response);

        $('.chatbot_contents .bot_infoBox').css({'display':'none'});
        $('.chatbot_contents .lst_talk').css({'display':'block'});

        $('.chatUI_mid').scrollTop($('.chatUI_mid')[0].scrollHeight);
    }
    function createAlarmMsg(talkObj) {
        $('.chatUI_mid .loading').remove();
        let msg = talkObj.message;
        let jobList = talkObj.jobList;

        let button_li = '';
        if (jobList) {
            jobList.forEach((job) => {
                button_li += '<li><a class="intent btnStart" ' +
                    'href="' + job.url + '" data-display="' + job.name + '" ' +
                    'data-intent="' + job.name + '">'+ job.name +'</a></li>';
            })
        }
        $('.chatUI_mid .lst_talk').append(
            '<li class="bot counselor"> \
                <span class="cont"> \
                    <em class="txt">' + msg + '</em> \
                    <div class="bot_msg">\
                        <div class="btnItem">\
                            <ul>'+button_li+' </ul>\
                        </div>\
                    </div>\
                    <em class="date">' + getNowTime() + '</em> \
                </span> \
            </li>'
        );
        $('.chatbot_contents .bot_infoBox').css({'display':'none'});
        $('.chatbot_contents .lst_talk').css({'display':'block'});

        $('.chatUI_mid').scrollTop($('.chatUI_mid')[0].scrollHeight);
    }

    // chat window 에 나의 msg UI 생성
    createUsrMsg = function(msg, time) {
        if (!msg) msg = '';
        $('.chatUI_mid .lst_talk').append(
            '<li class="user"> \
                <span class="cont"> \
                    <em class="txt">' + msg + '</em> \
						<em class="date">' + time + '</em> \
					</span> \
				</li>'
        );
        $('.chatbot_contents .bot_infoBox').css({'display':'none'});
        $('.chatbot_contents .lst_talk').css({'display':'block'});
        $('#inputArea').val('');

        $('.chatUI_mid').scrollTop($('.chatUI_mid')[0].scrollHeight);
        if (!isCounseling) {
            createLoadingMsg();
        }
    };

    function createCounselorMsg(msg) {
        $('.chatUI_mid .loading').remove();
        if (!msg) msg = '';
        $('.chatUI_mid .lst_talk').append(
            '<li class="bot counselor"> \
                <span class="cont"> \
                    <em class="txt">' + msg + '</em> \
						<em class="date">' + getNowTime() + '</em> \
					</span> \
				</li>'
        );
        $('.chatbot_contents .bot_infoBox').css({'display':'none'});
        $('.chatbot_contents .lst_talk').css({'display':'block'});

        $('.chatUI_mid').scrollTop($('.chatUI_mid')[0].scrollHeight);
    }

    // chat window 에 system msg UI 생성
    function createSystemMsg(msg) {
        window.parent.postMessage(msg, "*");
        isCounseling = true;
        removeWaitSystemMsg();

        if (!msg) msg = '';

        addEndCounselingBtn(msg);

        var sysChatElem = $('<li class="stmMessage">'+
            '<span></span>'+
            '</li>');
        sysChatElem.find("span").html(msg);

        $(".chatUI_mid .lst_talk").append(sysChatElem);
    }

    function removeWaitSystemMsg() {
        try {
            $('#chatUI_wrap').removeClass('counseling_mode');
        } catch (e) {
            console.log('removeWaitSystemMsg err');
            console.log(e);
        }
    }

    function addEndCounselingBtn(msg) {
        try {
            if (msg === 'Waiting for counselors...') {
                $('#chatUI_wrap').addClass('counseling_mode');
            }
        } catch (e) {
            console.log('addExitCounselorBtn err');
            console.log(e);
        }
    }

    function endCounseling() {
        isCounseling = false;
        createSystemMsg('End this Conversation');
        socketClient.endUserChat(curRoomId);
        var data = {"type": "intent", "input":"처음으로",
            "display": getInitComment(lang),
            "host": host, "lang":lang,
            "jsonData": JSON.stringify(getJsonData())};
        callWSServer(data);
    }


    function writeChatList() {
        // function for supporter
    }

    function handleChatEndUI() {

    }

    function writePreviousMessages() {

    }

    function chatbotFirstMsg(roomId) {
        if(roomId === curRoomId){
            callWSServer({"type": "intent", "input": "처음으로",
                "display": getInitComment(lang), "host": host, "lang":lang,
                "jsonData": JSON.stringify(getJsonData())})
        }
    }

    function getUUID() {
        function s4() {
            return ((1 + Math.random()) * 0x10000 | 0).toString(16).substring(1);
        }

        return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4()
            + s4();
    }

    function manageSession() {
        sessionId = getCookie("sessionId");
        if (sessionId === undefined) {
            sessionId = getUUID();
        }
        setCookie("sessionId", sessionId, 30);
    }



    function resetSession(){
        sessionId = getUUID();
        setCookie("sessionId", sessionId, 30);
    }

    function callWSServer(data) {
        manageSession();
        recentInput = data.input;
        // console.log("Input : " + recentInput)
        // var data = {"type": type, "input":value.replace("<br>", ""),
        //     "host": host, "lang": lang,
        //     "jsonData": JSON.stringify(getJsonData())};

        var msg = data.display;
        if (!msg) {
            msg = data.message;
        }

        var meta = {};
        data.session = sessionId;
        meta.adapterRequest = data;

        if (previousYn === 'n') {
            socketClient.send2Server(curRoomId, msg, data.host, meta);
        }
        previousYn = 'n';
        clearTimeout(afkTimer);
        afkTimer = setTimeout(afkCheck, afkTime);
    }

    // 현재 언어 국기로 보여주기 (지구본 대신)
    function showLanguageFlag(lang, supportedLangs) {
        // supportedLangs : '1,2,3..'
        // lang : '1' or '2' or '3' or '4'

        if (!supportedLangs) {
            supportedLangs = '1,2,3,4';
        }

        var totalLangs = [1, 2, 3, 4];
        supportedLangs = supportedLangs.split(',').map(function(sLang) {return Number(sLang.trim())});

        for (var i in totalLangs) {
            // 지원되지 않는 언어 icon 지우기
            if (supportedLangs.indexOf(totalLangs[i]) == -1) {
                $('.langBox .lst_lang').find(
                    ".btn_lang[data-lang='" + totalLangs[i] + "']").parents('li').remove();
            }
        }

        var langLength = $('.langBox .lst_lang > li').length;
        if ( langLength === 1 ) {
            lang = supportedLangs[0];
        } else {
            if (supportedLangs.indexOf(Number(lang)) == -1) {
                lang = supportedLangs[0];
            }
        }

        // 현재 언어 국기로 보여주기 (지구본 대신)
        var curLangCopy = $('.langBox .lst_lang').find(
          ".btn_lang[data-lang='" + lang + "']").clone();
        $('.langBox p').html(curLangCopy);

        // 현재 lang 설정이 지원되지 않는 언어일 경우, 지원되는 언어 중 하나로 바꿔 return
        return lang;
    }

    ///////////////////////////////////////////////////

    // call Backend
    function callBackend(data) {
        $.ajax({
            url: "/chat/request",
            data: JSON.stringify(data),
            type: "POST",
            contentType: 'application/json'
        }).done(function (response) {

            for(var index = 0 ; index < response.length; index++) {
                selectResponseType(response[index]);
            }
            selectResponseType(response);
        });

        $('.chatbot_contents .bot_infoBox').css({'display':'none'});
        $('.chatbot_contents .lst_talk').css({'display':'block'});
    }

    function selectResponseType(response){
        if(response.intent == '처음으로' && response.display != undefined && response.display != '') {
            firstDisplay = response.display;
        }
        if(!!response.answer){
            if(response.answer.indexOf("|||MAP|||") != -1 && !(googleMapAPILoaded)){
                googleMapAPILoaded = true
                $.getScript("https://maps.googleapis.com/maps/api/js?key=AIzaSyAuZtW-deiqIw5G9iCmTLTU7YDeODD63ag&language=ko&libraries=geometry,places",function (data, textStatus, jqxhr) {
                    console.log('ajax load getScript function was performed. : Googlesapis');
                    $.getScript( "/resources/js/map.js", function(){
                        console.log('ajax load getScript function was performed. : mapJS');
                        var langCode = getGoogleLangCode(lang);
                        setMapAPILanguage(langCode).then(function () {
                            selectResponseTypeAfter(response);
                            // console.log("Input : 지도 (API LOAD)");
                            recentInput = "지도"
                        })
                    })
                });
                return
            }else if(response.answer.indexOf("|||MAP|||") != -1){
                var langCode = getGoogleLangCode(lang);
                setMapAPILanguage(langCode).then(function () {
                    selectResponseTypeAfter(response)
                    // console.log("Input : 지도 (API)");
                    recentInput = "지도"
                })
                return
            }
        }
        selectResponseTypeAfter(response)
    }

    function selectResponseTypeAfter(response) {
        if(!!response.answer) {
            var ans = response.answer;

            // map이나 inquiry 중 하나만 있다고 가정.
            // console.log(ans);

            if (ans.indexOf("|||MAP|||") != -1) {
                var res = ans.split("|||MAP|||");
                botResponseMap(res[1]);
                response.answer = res[0];
            } else if (ans.indexOf("|||INQUIRY|||") != -1) {
                var res = ans.split("|||INQUIRY|||");
                botResponseInquiry(res[1]);
                response.answer = res[0];
            } else if (ans.indexOf("|||ORDER|||") != -1) {
                var res = ans.split("|||ORDER|||");
                botResponseOrder(res[1]);
                response.answer = res[0];
            } else if (ans.indexOf("|||PROMOTION|||") != -1) {
                var res = ans.split("|||PROMOTION|||");
                botResponsePromotion(res[1]);
                response.answer = res[0];
            } else if (ans.indexOf("|||IMG_CAROUSEL|||") != -1) {
                var res = ans.split("|||IMG_CAROUSEL|||");
                // 이미지 캐로셀은 예외적으로 textResponse를 먼저 처리.
                response.answer = res[0];
                if (!!response.answer) botResponseText(response.answer);
                response.answer = undefined;

                botResponseImgCarousel(res[1]);
            } else if (ans.indexOf("|||NOTICE|||") != -1) { // 공지사항
                var res = ans.split("|||NOTICE|||");
                // console.log(res[0]);
                // console.log(res[1]);
                botResponseNotice(res[1]);
                response.answer = undefined;
            }
        }


        if (response.responseOrder != undefined) {
          var responseOrder = response.responseOrder.split(",");
          if (responseOrder[0] == 0) {
            if (!!response.carousel && response.carousel.length > 0) botResponseCarousel(response.carousel);
          } else if (responseOrder[0] == 1) {
            if (!!response.answer) botResponseText(response.answer);
          } else {
            if (!!response.buttons) botResponseButton(response.buttons);
          }
          if (responseOrder[1] == 0) {
            if (!!response.carousel && response.carousel.length > 0) botResponseCarousel(response.carousel);
          } else if (responseOrder[1] == 1) {
            if (!!response.answer) botResponseText(response.answer);
          } else {
            if (!!response.buttons) botResponseButton(response.buttons);
          }
          if (responseOrder[2] == 0) {
            if (!!response.carousel && response.carousel.length > 0) botResponseCarousel(response.carousel);
          } else if (responseOrder[2] == 1) {
            if (!!response.answer) botResponseText(response.answer);
          } else {
            if (!!response.buttons) botResponseButton(response.buttons);
          }
        }
        if (!!response.list && response.list.length > 0) botResponseList(response.list);
        if (!!response.farewell) botResponseText(response.farewell);
        if(response.answer && previousTime === 'n'){
            botResponseTime();
        }
        if(response.answer!=null) makeTxtInnerButton();
    }

    function botResponseTime() {
        // 날짜, 요일 시간 정의
        var year  = new Date().getFullYear();  //현재 년도
        var month = new Date().getMonth()+1;  //현재 월
        var date  = new Date().getDate();  //현재 일
        var week  = new Array('일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일');	  //요일 정의
        var thisWeek  = week[new Date().getDay()];	//현재 요일

        var ampm = new Date().getHours() >= 12 ? 'PM' : 'AM';
        var	thisHours = new Date().getHours() >=13 ?  new Date().getHours()-12 : new Date().getHours(); //현재 시
        var	thisMinutes = new Date().getMinutes() < 10 ? '0' + new Date().getMinutes() : new Date().getMinutes(); //현재 분
        var NowTime = year + "." + month + "." + date + " " + ampm + " " + thisHours + ':' + thisMinutes;

        $('.bot:last').append(
            '<div class="date">'+ NowTime +'</div>'
        );
        $('.chatUI_mid').scrollTop($('.chatUI_mid')[0].scrollHeight);
    }

    function botResponseText(botMsg) {
        if (botMsg.indexOf("|||||") != -1) {
            var res = botMsg.split("|||||");
            for (var i=0; i<res.length; i++) {
                $('.chatUI_mid .lst_talk').append(
                    '<li class="bot"> \
                        <span class="cont"> \
                            <em class="txt">' +
                    res[i] +
                    '</em> \
                   </span><br>\
               </li>'
                );
            }
        } else {
            $('.chatUI_mid .lst_talk').append(
                '<li class="bot"> \
                    <span class="cont"> \
                        <em class="txt">' +
                botMsg +
                '</em> \
               </span><br>\
           </li>'
            );
        }
        $('.chatUI_mid').scrollTop($('.chatUI_mid')[0].scrollHeight);
    }

    function botResponseList(list_response) {
        var list_li = "";

        list_response.forEach(function (list_ele) {
            list_li += '<li><a class="intent" href="#" data-display="'+ list_ele.display +'" data-intent="'+ list_ele.intent.intent +'" >' + list_ele.display  + '</a></li>';
        });

        var list_html =
            '<li class="bot"> \
            <div class="bot_msg">\
                <div class="btnLst">';
        list_html += '<ul>' + list_li + '</ul>\
        </div>\
        </div>\
        </li>';

        $('.chatUI_mid .lst_talk').append(list_html)
        $('.chatUI_mid').scrollTop($('.chatUI_mid')[0].scrollHeight);

    }

    function getInitComment(lang) {
        var first_btn;

        if (lang == 1) {
            first_btn = "처음으로"
        }

        if (lang == 2) {
            first_btn = "Restart"
        }

        if (lang == 3) {
            first_btn = "再起動"
        }

        if (lang == 4) {
            first_btn = "重新启动"
        }

        return first_btn;
    }

    function botResponseButton(button_response) {
        var button_li = "";
        var first_btn;

        if (button_response.length > 0) {
            button_response.forEach(function (button_ele) {
                if (button_ele.intent != "처음으로" && button_ele.intent.intent != "처음으로") {
                    button_li += '<li><a class="intent" href="#" data-display="'+ button_ele.display +'" data-intent="'+ button_ele.intent.intent +'">' + button_ele.display  + '</a></li>';
                }
            });
        }

        if (firstDisplay) {
            first_btn = firstDisplay;
        } else {
            first_btn = getInitComment(lang);
        }

        button_li += '<li><a class="intent btnStart" href="#" data-display="' + first_btn + '" data-intent="처음으로">'+ first_btn +'</a></li>';

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
        var carousel_div = "";

        carousel_response.forEach(function (carousel_ele) {
            var intent = carousel_ele.intent;

            if (!intent.displayText)  {
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

        var swiper_option = {
            init : false,
            speed : 200,
            slidesPerView:2,
            spaceBetween: 10,
            centeredSlides: false,
            loop: false,
            pagination: {
                el: '.swiper-pagination',
                clickable: true
            },
            navigation: {
                nextEl: '.swiper-button-next',
                prevEl: '.swiper-button-prev'
            }
        };

        if (carousel_response.length > swiper_option.slidesPerView) {
            swiper_option.loop = true;
        }

        var swiper = new Swiper('.botMsg_swiper', swiper_option);
        // var lenght = $('.botMsg_swiper').length;
        // if (lenght > 1) {
        //     for (var i = lenght-1; i > 1; i--) {
        //         if ($('.botMsg_swiper')[i].localName === 'li') {
        //             $('.botMsg_swiper')[i].swiper.init();
        //             break;
        //         }
        //     }
        // } else {
        //     $('.botMsg_swiper:last')[0].swiper.init();
        // }

        $('.botMsg_swiper:last')[0].swiper.init();
        $('.chatUI_mid').scrollTop($('.chatUI_mid')[0].scrollHeight);

    }

    function botResponsePromotion(promoResponse) {
        promoResponse = JSON.parse(promoResponse);

        $('.chatUI_mid .lst_talk').append(
            '<li class="bot bot_promotion"> \
            <div class="bot_msg">\
                <div class="generic"> \
                    <span class="generic_img"><img src="' + promoResponse.img + '" alt="프로모션 사진"></span> \
                    <span class="generic_tit">' + promoResponse.title + '</span> \
                        <div class="txt">' + promoResponse.comment + '</div> \
                 </div> \
            </div>\
            </li>'
        );

        $('.chatUI_mid').scrollTop($('.chatUI_mid')[0].scrollHeight);
    }

    /* 예약 일자: datetimepicker */
    $('#reservation_date').datetimepicker({
        minDate: 'now',
        locale: 'ko',
        inline: true,
        format: 'YYYY-MM-DD HH:mm dd',
        dayViewHeaderFormat: 'YYYY / MM ',
        stepping: 30,
        daysOfWeekDisabled: [0],
        disabledDates: ['2020-01-01','2020-03-01','2020-05-01','2020-05-05','2020-06-06','2020-08-15','2020-10-03','2020-10-09','2020-12-25'],
        // 01-01 새해(신정), 03-01 삼일절, 05-01 근로자의날, 05-05 어린이날, 06-06 현충일, 08-15 광복절, 10-03 개천절, 10-09 한글날, 12-25 크리스마스
        // 설날(구정)				음력 1월 1일
        // 석가탄신일(부처님오신날)	 음력 4월 8일
        // 추석						음력 8월 15일
        disabledHours: [0,1,2,3,4,5,6,7,8,20,21,22,23],
        icons: {
            time: 'glyphicon glyphicon-time',
            date: 'glyphicon glyphicon-calendar',
            up: 'glyphicon glyphicon-chevron-up',
            down: 'glyphicon glyphicon-chevron-down',
            previous: 'glyphicon glyphicon-chevron-left',
            next: 'glyphicon glyphicon-chevron-right',
            today: 'glyphicon glyphicon-screenshot',
            clear: 'glyphicon glyphicon-trash',
            close: 'glyphicon glyphicon-remove'
        }
    });

    function initializeInquiryDisplsy(){
        for (i = 0; i < $('.chat_inquiry .chatAside_bd .form dl.dlBox').length; i++) {
            dl = $('.chat_inquiry .chatAside_bd .form dl.dlBox')[i];
            dl.style.display = 'None';
        }
    }

    function botResponseNotice(noticeMessage) {
        noticeMessage = JSON.parse(noticeMessage);
        $('#chatUI_wrap').addClass('noti_on');
        $('.noti_cont .noti').empty();
        // 공지사항 타이틀 추가
        $('.noti_title p').text(noticeMessage.title);
        var contents = noticeMessage.contents;
        for(var i = 0; i < contents.length; i++) {
            console.log(contents[i]);
            // 각각의 공지 내용들마다(조식시간변경, 수영장이용시간 등) 이미지 추가할 수 있도록 함
            if (contents[i].imageUrl !== "") {
                $('.noti_cont .noti').append(
                    '<img src="' + contents[i].imageUrl + '" alt="' + contents[i].imageTitle + '">');
            }
            // n번째 공지 내용 타이틀 (ex. 조식시간 변경안내)
            $('.noti_cont .noti').append('<p class="noti_header">' + contents[i].subTitle + '</p>');
            var subMessage = contents[i].subMessage;
            // n번째 공지 내용 (ex. 기존이용시간: 8시-10시, 변경내용: 8시반-10시)
            for(var j = 0; j < subMessage.length; j++) {
                $('.noti_cont .noti').append('<p class="noti_p">' + subMessage[j] + '</p>');
            }
        }
    }

    function botResponseOrder(orderResponse) {
        var $chatAside;
        var isCafe;
        // 현재 ui hard coding, todo: 메뉴명 db에서 가져오도록.
        if (orderResponse.indexOf("PAVAN") != -1) {
            $chatAside = $('.cafe_order').parent();
            isCafe = true;
            handleChatAsideOrder();
        } else if (orderResponse.indexOf("DELIGHT") != -1) {
            $chatAside = $('.food_order').parent();
            isCafe = false;
            handleChatAsideOrder();
        }

        // 추가 AMR 200412 주문하기 카테고리 메뉴 open close
        function handleChatAsideOrder() {
            var categoryBtn = $('.chatAside .chat_order .category');
            var categoryMenu = $('.chatAside .chat_order .category_menu');
            var $eachmenu = $('.chatAside .chat_order .each_menu');
            var $checkbox = $('.chatAside .chat_order .each_menu [type="checkbox"]');

            categoryBtn.on('click', function(event){
                event.preventDefault();
                var thisCategoryMenu = $(this).siblings(categoryMenu);

                if ( thisCategoryMenu.hasClass('on') ) {
                    categoryMenu.removeClass('on');
                } else {
                    categoryMenu.removeClass('on');
                    thisCategoryMenu.toggleClass('on');
                }
            });

            // 체크박스 체크해제 시 수량 초기화
            $checkbox.on('change', function(event){
                event.preventDefault();
                var $eachmenu = $(this).parents('.each_menu');

                if ( $(this).prop('checked') == false ) {
                    $eachmenu.find('.count').val('');
                }
                calcTotalPrice()
            });

            // 주문 수량을 적으면 메뉴체크
            $chatAside.find('.chat_order .count').on('keyup', function(event){
                event.preventDefault();
                var $eachmenu = $(this).parents('.each_menu');

                $eachmenu.find('[type="checkbox"]').prop('checked', Boolean(Number(this.value)));
                calcTotalPrice()
            });

            // 선택한 메뉴들 합계
            function calcTotalPrice() {
                var totalPrice = 0;
                var $total = $chatAside.find('.chat_order .total_price');

                $eachmenu.each(function(){
                    var price = Number($(this).find('span.price').text().replace(/,/g, ''));
                    var count = Number($(this).find('.count').val());
                    totalPrice = (price * count) + totalPrice;
                });
                var priceText = '총 가격: ' + numberFormat(totalPrice) + '원';
                $total.text(priceText);
                return numberFormat(totalPrice);
            }

            function clearOrderForm () {
                // input field clear
                $chatAside.find(".chat_order input[name='name']").val('');
                $chatAside.find(".chat_order input[name='tel']").val('');
                $chatAside.find(".chat_order input[name='email']").val('');
                // $(".chat_order input[name='pickupTime']").val('');
                $chatAside.find(".chat_order textarea[name='add']").val('');

                // each menu count clear
                $eachmenu.each(function(){
                    $(this).find('.count').val(0);
                });
                // total price clear
                $chatAside.find('.chat_order .total_price').text('');
                $chatAside.find('.stn_area').scrollTop($chatAside.find('.stn_area'));

                //checkbox, radio button clear
                $chatAside.find('input[type="checkbox"], input[type="radio"]').removeAttr('checked');
            }

            function checkOrderData(orderData) {
                if (!orderData.tos) {
                    alert('개인정보동의 약관에 동의해주세요');
                    return false;
                }
                if (orderData.name === undefined || orderData.name === '') {
                    alert('이름을 입력해주세요');
                    return false;
                }
                if (orderData.phone === undefined || orderData.phone === '') {
                    alert('전화번호를 입력해주세요');
                    return false;
                }
                if (orderData.email === undefined || orderData.email === '') {
                    alert('이메일을 입력해주세요');
                    return false;
                }
                if (orderData.reqList === undefined || orderData.reqList.length <= 0) {
                    alert('메뉴를 선택해주세요');
                    return false;
                }
                if (orderData.pickupTime === undefined || orderData.pickupTime === '') {
                    if (isCafe) {alert('픽업시간을 선택해주세요');}
                    else {alert('수령일자를 선택해주세요');}
                    return false;
                }
                if (!isCafe && $chatAside.find('input:radio[name="take"]:checked').length <= 0) {
                    alert('수령시간을 선택해주세요');
                    return false;
                }
                if (orderData.payment === undefined || orderData.payment === '') {
                    alert('결제방법을 선택해주세요');
                    return false;
                }

                return true;
            }

            // aside open
            $(document).on('click', '.order_btn', function(){
                $chatAside.addClass('aside_show');
                sideScreen = true;
                window.parent.postMessage("aside_open", "*");
                // 현재 무조건 checked. todo: 삭제
                $chatAside.find('input[name="agreement"]').prop('checked', true);
            });

            // 약관보기
            $chatAside.find('.btn_terms').on('click', function(){
                $(this).parents('.chatAside_bd').addClass('info_screen');
            });

            // 약관 열어서 확인 (아직 약관 내용 없으므로 작동하지 않음)
            $chatAside.find('.chat_order .btn_point').on('click', function(){
                $(this).parents('.chatAside_bd').removeClass('info_screen');
                $chatAside.find('input[name="agreement"]').prop('checked', true);
            });


            // aside close (input value 초기화 및 창 닫힘)
            $('.btn_chatAside_close').on('click', function(){
                clearOrderForm();
                window.parent.postMessage("aside_close", "*");
                $(this).parents($chatAside).removeClass('aside_show').find('.chatAside_bd').removeClass('success_screen');
                // $(this).parents($chatAside).find('input[type="text"], input[type="tel"], textarea').val('');
                $(this).parents($chatAside).find('input[type="checkbox"], input[type="radio"]').removeAttr('checked');
            });

            /* 수정 200427 AMR주문하기 픽업시간 */
            var dpElement = $('#pickup_time');
            var dp = dpElement.datetimepicker({
                locale: 'ko',
                inline: true,
                format: 'HH:mm',
                dayViewHeaderFormat: 'YYYY 년 MM 월',
                stepping: 10,
                daysOfWeekDisabled: [0],
                disabledDates: ['2020-01-01','2020-03-01','2020-05-01','2020-05-05','2020-06-06','2020-08-15','2020-10-03','2020-10-09','2020-12-25'],
                // 01-01 새해(신정), 03-01 삼일절, 05-01 근로자의날, 05-05 어린이날, 06-06 현충일, 08-15 광복절, 10-03 개천절, 10-09 한글날, 12-25 크리스마스
                // 설날(구정)				음력 1월 1일
                // 석가탄신일(부처님오신날)	 음력 4월 8일
                // 추석						음력 8월 15일
                disabledHours: getDisabledHoursForDp(),
                icons: {
                    time: 'glyphicon glyphicon-time',
                    date: 'glyphicon glyphicon-calendar',
                    up: 'glyphicon glyphicon-chevron-up',
                    down: 'glyphicon glyphicon-chevron-down',
                    previous: 'glyphicon glyphicon-chevron-left',
                    next: 'glyphicon glyphicon-chevron-right',
                    today: 'glyphicon glyphicon-screenshot',
                    clear: 'glyphicon glyphicon-trash',
                    close: 'glyphicon glyphicon-remove'
                }
            });

            //추가 200427 AMR datetimepicker 이전시간 선택 불가
            // 현재시간 인식해서 이전 시간 가져오기
            function getDisabledHoursForDp (selectedDate){
                var thisHour = moment().hour();
                var inactiveHours = [0,1,2,3,4,5,6,7,8,17,18,19,20,21,22,23]

                if(
                    (selectedDate === undefined) ||
                    (selectedDate && selectedDate.format('YYYY-MM-DD') === moment().format('YYYY-MM-DD'))
                ) {
                    for (var hour=0, length = thisHour; hour<length; hour++) {
                        var hasHour = false;
                        for(var h=0, l=inactiveHours.length; h<l; h++){
                            if(inactiveHours[h] === hour){
                                hasHour = true
                            }
                        }
                        if(hasHour === false){
                            inactiveHours.push(hour)
                        }
                    }
                }

                // 24시간 모두 disabled이면 에러가 발생하여 방어코드
                if(inactiveHours.length === 24){
                    inactiveHours.shift()
                }

                return inactiveHours
            }

            // 일시를 전부 사용할 때
            dpElement.on("dp.change", function(event){
                var inactiveHours = getDisabledHoursForDp(event.date);
                dp.disabledHours(inactiveHours);
            });

            // submit btn onclick
            var submitBtn = $chatAside.find('.chatAside_bd div.btnBox button.btn_submit');
            submitBtn.prop("onclick", null).off("click");
            submitBtn.on('click', function(event) {
                var orderList = [];
                $eachmenu.each(function(){
                    var label = $(this).find('label').text();
                    var count = Number($(this).find('.count').val());

                    if (count > 0) {
                        var order = [label, count + "개"];
                        orderList.push(order);
                    }
                });

                var checkedTake = $chatAside.find('input:radio[name="take"]:checked');
                var checkedPayment = $chatAside.find('input:radio[name="payment"]:checked');
                var pickupTime = $chatAside.find(".chat_order input[name='pickupTime']").val();
                var take = checkedTake[0] ? checkedTake.siblings("label[for='" + checkedTake[0].id + "']").text() : "";

                var orderData = {
                    "tos" : $('input[name="agreement"]').is(':checked'),
                    "name": $chatAside.find(".chat_order input[name='name']").val(),
                    "phone": $chatAside.find(".chat_order input[name='tel']").val(),
                    "email": $chatAside.find(".chat_order input[name='email']").val(),
                    "pickupTime": isCafe ? pickupTime : pickupTime + '/' + take,
                    "msg": $chatAside.find(".chat_order textarea[name='add']").val(),
                    "totalPrice": calcTotalPrice(),
                    "reqList": orderList,
                    "take": isCafe ? take : '',
                    "payment": checkedPayment[0]? checkedPayment.siblings("label[for='" + checkedPayment[0].id + "']").text() : ""
                };

                if (!checkOrderData(orderData)){
                    return;
                }

                // console.log(orderData);

                // success 팝업 채우기
                $chatAside.find('.chatAside_bd div.check_order span.order_name').siblings('em').text(orderData.name);
                $chatAside.find('.chatAside_bd div.check_order span.order_phone').siblings('em').text(orderData.phone);
                $chatAside.find('.chatAside_bd div.check_order span.order_email').siblings('em').text(orderData.email);
                $chatAside.find('.chatAside_bd div.check_order span.order_take').siblings('em').text(orderData.take);
                $chatAside.find('.chatAside_bd div.check_order span.order_payment').siblings('em').text(orderData.payment);
                $chatAside.find('.chatAside_bd div.check_order span.order_pickupTime').siblings('em').text(orderData.pickupTime);
                $chatAside.find('.chatAside_bd div.check_order span.order_msg').siblings('p').text(orderData.msg);
                $chatAside.find('.chatAside_bd div.check_order span.order_totalPrice').siblings('em').text(orderData.totalPrice);
                var reqListP = $chatAside.find('.chatAside_bd div.check_order span.order_reqList').siblings('p');
                reqListP.empty();
                var orderResTxt = '';
                for (var i in orderList) {
                    var order = orderList[i];
                    var em = $("<em></em>").text(order[0] + ' ' + order[1]);
                    orderResTxt += order[0] + ' ' + order[1] + '</br>';
                    reqListP.append(em);
                }

                $chatAside.find('.chatAside_bd').addClass('success_screen');
                $chatAside.find('.chatAside_bd div.check_order .btn_point.btn_chatAside_close').off('click');
                $chatAside.find('.chatAside_bd div.check_order .btn_point.btn_chatAside_close').on('click', function (event) {
                    clearOrderForm();
                    callWSServer({"type": "intent",
                        "input": JSON.stringify(orderData),
                        "host": host, "lang":lang,
                        "jsonData": JSON.stringify(getJsonData())});
                    window.parent.postMessage("aside_close", "*");
                    $chatAside.find('.chatAside_bd').removeClass('success_screen');
                    botResponseText(orderResTxt + "주문 완료되었습니다.");
                });
            });
        }
    }

    function botResponseInquiry(iqrResponse) {
        iqrResponse = JSON.parse(iqrResponse);

        initializeInquiryDisplsy();

        // 문의하기 및 예약하기 팝업 채우기
        // title
        $('.chatAside_hd h3').text(iqrResponse.title);
        // 이용약관
        iqrResponse.tos.forEach(function(terms){
            $('.chatAside_bd .tos p.txt').text(terms.title);
            $('.chatAside_bd .tos div.iptBox label').text(terms.check);
            $('.chatAside_bd .tos div.iptBox button.btn_terms').text(terms.btn);
        });

        // form
        $('.chatAside_bd .form p.txt').text(iqrResponse.form.comment);
        fields = iqrResponse.form.field;
        for (name in fields) {
            field = fields[name];
            title = field.title;

            $('.chatAside_bd .form dl.dlBox.form_' + name)[0].style.display = '';
            $('.chatAside_bd .form dl.dlBox.form_' + name + ' dt').text(title);

            if (field.placeholder) {
                $('.chatAside_bd .form dl.dlBox.form_' + name + ' dd input.ipt_txt').placeholder = field.placeholder
            }

            if ($('.chatAside_bd .form dl.dlBox.form_' + name + ' dd .radioBox').length !== 0) {
                // radio btn options
                for (label in field.options) {
                    $('.chatAside_bd .form dl.dlBox.form_' + name + ' dd .radioBox label[for=' + label  + ']')
                    .text(field.options[label].title);
                }
            }
        }

        // datetimepicker 관련 meta 설정
        if (!!fields['datetime']) {
            var meta = fields['datetime'].meta;
            $('.glyphicon-time').attr('time-data-before', meta.timePlaceholder);
            $('.glyphicon-time').attr('date-data-before', meta.datePlaceholder);

            $('.chatAside_bd .form dl.dlBox.form_datetime dd.pick_guide .pick_guide01').next().text(meta.today);
            $('.chatAside_bd .form dl.dlBox.form_datetime dd.pick_guide .pick_guide02').next().text(meta.available);
            $('.chatAside_bd .form dl.dlBox.form_datetime dd.pick_guide .pick_guide03').next().text(meta.selected);
        }

        // submit button
        $('.chatAside_bd div.btnBox button.btn_submit').text(iqrResponse.form.submitBtn);

        var $chatAside = $('.chat_inquiry').parent();

        // submit btn onclick
        var submitBtn = $('.chatAside_bd div.btnBox button.btn_submit');
        submitBtn.prop("onclick", null).off("click");
        submitBtn.on('click', function(){
            var $thisChatAside = $(this).parents($chatAside);
            if ( !!iqrResponse.tos[0] && iqrResponse.tos[0].required &&
                !!$thisChatAside.find('input[name="agreement"]').length &&
                !$thisChatAside.find('input[name="agreement"]').is(':checked') ) {
                // alert('개인정보 약관에 동의해주세요');
                alert(iqrResponse.tos[0].requireAlert);
                return;
            }
            if ( !!fields['name'] && fields['name'].required &&
                !!$thisChatAside.find('input[name="name"]').length &&
                !$thisChatAside.find('input[name="name"]').val().trim() ) {
                // alert('이름을 입력해주세요');
                alert(fields['name'].requireAlert);
                return;
            }
            if ( !!fields['gender'] && fields['gender'].required &&
                !!$thisChatAside.find('input[name="gender"]').length &&
                !$thisChatAside.find('input[name="gender"]').is(':checked') ) {
                // alert('성별을 선택해주세요');
                alert(fields['gender'].requireAlert);
                return;
            }
            if ( !!fields['tel'] && fields['tel'].required &&
                !!$thisChatAside.find('input[name="tel"]').length &&
                !$thisChatAside.find('input[name="tel"]').val().trim() ) {
                // alert('연락처를 입력해주세요');
                alert(fields['tel'].requireAlert);
                return;
            }
            if ( !!fields['email'] && fields['email'].required &&
                !!$thisChatAside.find('input[name="email"]').length &&
                !$thisChatAside.find('input[name="email"]').val().trim() ) {
                // alert('이메일을 입력해주세요');
                alert(fields['email'].requireAlert);
                return;
            }
            if ( !!fields['datetime'] && fields['datetime'].required &&
                !!$thisChatAside.find('input[name="datetime"]').length &&
                !$thisChatAside.find('input[name="datetime"]').val().trim() ) {
                // alert('날짜를 선택해주세요');
                alert(fields['datetime'].requireAlert);
                return;
            }
            if ( !!fields['inquiry'] && fields['inquiry'].required &&
                !!$thisChatAside.find('textarea[name="inquiry"]').length &&
                !$thisChatAside.find('textarea[name="inquiry"]').val().trim() ) {
                // alert('요청사항을 입력해주세요');
                alert(fields['inquiry'].requireAlert);
                return;
            }

            // 문의내용 뒷단으로 넘기기
            var genderId = $thisChatAside.find('input[name="gender"]:checked').attr('id');
            var eventTypeId = $thisChatAside.find('input[name="event_type"]:checked').attr('id');
            var tableStyleId = $thisChatAside.find('input[name="table_style"]:checked').attr('id');

            var inquiryData = {
                "name": $(".form_name").find("input").val(),
                "gender": $thisChatAside.find('label[for="' + genderId + '"]').text(),
                "phone": $(".form_tel").find("input").val(),
                "email": $(".form_email").find("input").val(),
                "datetime": $(".form_datetime").find("input").val(),
                "personCnt": $('.form_person_cnt').find('input').val(),
                "eventType": $thisChatAside.find('label[for="' + eventTypeId + '"]').text(),
                "tableStyle": $thisChatAside.find('label[for="' + tableStyleId + '"]').text(),
                "add": $(".form_add").find("textarea").val(),
                "inquiryMsg": $(".form_inquiry").find("textarea").val()};

            callWSServer({"type": "intent", "input": JSON.stringify(inquiryData), "host": host, "lang":lang, "jsonData": JSON.stringify(getJsonData())});

            // success 팝업 채우기
            console.log($('.chatAside_bd').prop('scrollHeight'));
            var scrollHeight = $('.chatAside_bd').prop('scrollHeight') - $('.chatAside_bd').outerHeight();
            if (scrollHeight > 0) {
                $thisChatAside.find('.chatAside_bd div.stnBox.popup')[0].style.top = scrollHeight + 'px';
            }
            $('.chatAside_bd div.stnBox.popup .popup_content .popup_txt em').text(iqrResponse.form.successPopup.title);
            $('.chatAside_bd div.stnBox.popup .popup_content .popup_txt p').html(iqrResponse.form.successPopup.description);
            $thisChatAside.find('.chatAside_bd').addClass('success_screen');

        });

        // aside open
        $(document).on('click', '.inquiry_btn', function(){
            $chatAside.addClass('aside_show');
            sideScreen = true;
            window.parent.postMessage("aside_open", "*");
        });

        // aside close (input value 초기화 및 창 닫힘)
        $('.btn_chatAside_close').on('click', function(){
            window.parent.postMessage("aside_close", "*");
            $(this).parents($chatAside).removeClass('aside_show').find('.chatAside_bd').removeClass('success_screen');
            sideScreen = false;
            $(this).parents($chatAside).find('input[type="text"], input[type="tel"], textarea').val('');
            $(this).parents($chatAside).find('input[type="checkbox"], input[type="radio"]').removeAttr('checked');
        });

        // 추가 200306 AMR 약관보기
        $chatAside.find('.btn_terms').on('click', function(){
            $(this).parents('.chatAside_bd').addClass('info_screen');
        });

        $chatAside.find('.info_text .btn_point').on('click', function(){
            $(this).parents('.chatAside_bd').removeClass('info_screen');
            $chatAside.find('input[name="agreement"]').prop('checked', true);
        });
    }

    // swiper
    function applySwiper (selector, option) {
        var defaultOption = {
            speed : 200,
            slidesPerView:2,
            spaceBetween: 10,
            centeredSlides: false,
            pagination: {
                el: '.swiper-pagination',
                clickable: true
            },
            navigation: {
                nextEl: '.swiper-button-next',
                prevEl: '.swiper-button-prev'
            }
        };
        return new Swiper(selector, $.extend(defaultOption, option))
    }

    function handleSwipePopupPreviewOpenClose() {
        //open
        $('.full_preview .swiper_item').on('click', function(e){
            $('.popup_swipe_backdrop').addClass('popup_active');
            var slideIndex = $('.full_preview .swiper-slide').index($(this).parent());
            var thisSlide = slideIndex;
            var swiper = applySwiper($('.popup_swipe_backdrop .botMsg_swiper'), { slidesPerView:1, initialSlide: thisSlide, });

            //close (btn click)
            function handleClose(){
                e.stopPropagation();
                swiper.destroy();
                $('.popup_swipe_backdrop .popup_swipe_preview').off('click');
                $('.popup_swipe_backdrop').removeClass('popup_active');
                $('.btn_popupClose').off('click', handleClose);
                $('.popup_swipe_backdrop').off('click');
            }

            $('.btn_popupClose').on('click', handleClose);

            $('.popup_swipe_backdrop .popup_swipe_preview').on('click', function(e){
                e.stopPropagation();
            });

            //close (영역 밖 click)
            $('.popup_swipe_backdrop').on('click', function(){
                if ( $(this).hasClass('popup_active') ) {
                    handleClose();
                }
            });
        });
    }

    function botResponseImgCarousel(imgResponse) {
        imgResponse = JSON.parse(imgResponse);

        var imgCarouselHtml =
            '<li class="bot"> \
                <div data-swiper-id="2" class="botMsg_swiper full_preview">\
                    <div class="swiper-wrapper">';

        // img 클릭시 나오는 Back swiper를 별도로 그려줘야.
        var imgBackdropHtml =
            '<div class="popup_swipe_backdrop"> \
                <div class="popup_swipe_preview"> \
                    <div class="botMsg_swiper"> \
                        <div class="swiper-wrapper">';

        for (i in imgResponse.imgList) {
            var img = imgResponse.imgList[i];
            imgCarouselHtml +=
                '<div class="swiper-slide"> \
                    <a class="swiper_item" href="#" target="_self"> \
                        <span class="item_img"><img src="' + img.src + '" alt="' + img.title + '"></span> \
                    </a> \
                </div>';

            imgBackdropHtml +=
                '<div class="swiper-slide"> \
                    <div class="swiper_item"> \
                        <span class="item_img"><img src="' + img.src + '" alt="' + img.title + '"></span> \
                    </div> \
                </div>';
        }

        imgCarouselHtml +=
            '</div> \
                <!-- [D] Swiper Pagination --> \
            <div class="swiper-pagination"></div> \
            <!-- [D] Swiper navigation buttons --> \
            <div class="swiper-button-prev"></div> \
            <div class="swiper-button-next"></div> \
            </div>\
        </li>';

        imgBackdropHtml +=
            '</div>\
            <!-- [D] Swiper Pagination -->\
            <div class="swiper-pagination"></div>\
            <!-- [D] Swiper navigation buttons -->\
            <div class="swiper-button-prev"></div>\
            <div class="swiper-button-next"></div>\
            <button type="button" class="btn_popupClose"><em>팝업 닫기</em></button>\
        </div>';


        $('.chatUI_mid .lst_talk').append(imgCarouselHtml);
        $('#chatUI_wrap').append(imgBackdropHtml);

        handleSwipePopupPreviewOpenClose();
        applySwiper($('[data-swiper-id="2"]'), {slidesPerView: 1});

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

    //스크롤 AFK 타이머 리셋
    $( '.chatUI_mid' ).scroll(function() {
        clearTimeout(afkTimer);
        afkTimer = setTimeout(afkCheck, afkTime); // ms
    });


    //텍스트 입력 타이머 리셋
    $('.chatUI_btm .textArea').keypress(function(){
        clearTimeout(afkTimer);
        afkTimer = setTimeout(afkCheck, afkTime); // ms
    });


    // guide
    $('.chat_top .btn_cb_guide').on('click', function(){
        window.location.reload();
    });


    $(document).on('click', 'a', function(event){
        if ($(this).attr("href") == '#') {
            return;
        }
        event.preventDefault();
        var message_data = {type:"href", value:$(this).attr("href")};
        window.parent.postMessage(message_data, "*");
    });

    var addClass = ['','ko','en','ja','zh_cn'];
    $('.langBox p').on('click', function(){
        $('.btn_lang').removeClass('active');
        $('.ico_'+addClass[lang]).addClass('active');
        $(this).parents('.langBox').find('.lst_lang').toggleClass('show');
        $(this).toggleClass('active');
    });

    $('.langBox .lst_lang .btn_lang').on('click', function(){
        var langCopy = $(this).clone();
        $('.langBox p').html(langCopy);
        $('.langBox p .btn_lang').removeClass('active');
        $(this).parents('.lst_lang').removeClass('show');
        $('.langBox .lst_lang .btn_lang').removeClass('active');
        $(this).addClass('active');

        var new_lang = $(this).data("lang");

        if (lang == new_lang) {
            return;
        } else {
            lang = new_lang;
            // load google map api
            changeTextHolder(lang);
            callWSServer({"type": "intent", "input": "처음으로",
                "display": getInitComment(lang), "host": host, "lang":lang,
                "jsonData": JSON.stringify(getJsonData())})
        }
    });





});

function replaceAll(str, searchStr, replaceStr) {
    return str.split(searchStr).join(replaceStr);
}

$(window).resize(function (){
    $('#cahtbotWrap').each(function(){
        var cahtbotWrapHeight = $('#cahtbotWrap').height();
        $('.chatUI_mid').css({
            'height': Math.round(cahtbotWrapHeight-145),
        });
    });
});

// 추가: 20191112 유명종
//텍스트 속 버튼
function makeTxtInnerButton() {

    $('.txt').each(function(){
        var txt_btn = $(this).find('a');
        var txt_btnLength = $(this).find('a').length;
        if (txt_btnLength > 1) {
            $(this).find('a').wrap('<div class="txt_btns"></div>');
        } else {
            $(this).find('a').wrapAll('<div class="txt_btn"></div>');
        }
    });
}

//200217 AMR 접속 디바이스 확인
function checkDevice() {
    var navigatorUA = navigator.userAgent;
    var AgentUserOs = navigatorUA.replace(/ /g,'');
    var OSName = '';
    var connectDevice = 'PC';
    var mobileDeviceList  = new Array('Android', 'samsung', 'LG', 'BlackBerry', 'BlackBerry Opera/9.80', 'iPhone', 'iPad');

    new function() {
        var OsNo = navigator.userAgent.toLowerCase();
        jQuery.os = {
            Linux: /linux/.test(OsNo),
            Unix: /x11/.test(OsNo),
            Mac: /mac/.test(OsNo),
            Windows: /win/.test(OsNo)
        }
    };

    if($.os.Windows) {
        if(AgentUserOs.indexOf("Safari") != -1) {OSName="Safari";}
        else if(AgentUserOs.indexOf("Chrome") != -1) {OSName="Chrome";}
        else if(AgentUserOs.indexOf("OPR") != -1) {OSName="opera";}
        else if(AgentUserOs.indexOf("Firefox") != -1) {OSName="firefox";}
        else if(AgentUserOs.indexOf("Edge") != -1) {OSName="Edge";}

        else if(AgentUserOs.indexOf("WOW64") != -1) {
            if(AgentUserOs.indexOf("rv:11") != -1) {OSName="IE 11";}
            if(AgentUserOs.indexOf("MSIE10") != -1) {OSName="IE 10";}
            if(AgentUserOs.indexOf("MSIE9") != -1) {OSName="IE 9";}
        }
        else {OSName="window";}
    } else if ($.os.Linux) {
        if(AgentUserOs.indexOf("Android") != -1) {OSName="Android";}
        else if(AgentUserOs.indexOf("SAMSUNG") != -1) {OSName="samsung";}
        else if(AgentUserOs.indexOf("samsung") != -1) {OSName="samsung";}
        else if(AgentUserOs.indexOf("LG") != -1) {OSName="LG";}
        else if(AgentUserOs.indexOf("lgtelecom") != -1) {OSName="LG";}
        else if(AgentUserOs.indexOf("BlackBerry") != -1) {OSName="BlackBerry";}
        else if(AgentUserOs.indexOf("BlackBerry;Opera Mini") != -1) {OSName="BlackBerry Opera/9.80";}
        else if(AgentUserOs.indexOf("Symbian") != -1) {OSName="Symbian";}
        else if(AgentUserOs.indexOf("Ubuntu") != -1) {OSName="Ubuntu";}
        else if(AgentUserOs.indexOf("PDA") != -1) {OSName="PDA";}
        else {OSName="Linux";}
    }
    else if ($.os.Unix) {OSName="UNIX";}
    else if ($.os.Mac) {
        if(AgentUserOs.indexOf("iPhone") != -1) {
            // if(AgentUserOs.indexOf("iPhoneOS3") != -1) {OSName="iPhone OS 3";}
            // else if(AgentUserOs.indexOf("iPhoneOS4") != -1) {OSName="iPhone OS 4";}
            // else if(AgentUserOs.indexOf("iPhoneOS5") != -1) {OSName="iPhone OS 5";}
            // else if(AgentUserOs.indexOf("iPhoneOS6") != -1) {OSName="iPhone OS 6";}
            // else OSName="iPhone";
            OSName="iPhone";
        }
        else if(AgentUserOs.indexOf("iPad") != -1) {OSName="iPad";}
        else if(AgentUserOs.indexOf("MacOS") != -1) {
            if(AgentUserOs.indexOf("Macintosh") != -1) OSName="Macintosh";
        } else {OSName="MacOS";}
    }
    else {OSName="OS (we should find this OS)";}

    if (OSName.length > 0) {
        if (mobileDeviceList.indexOf(OSName) != -1) {
            connectDevice = 'MOBILE';
        }
    }

    console.debug(OSName,'OSName');
    console.debug(connectDevice,'connect Device');
    return connectDevice;
}

function getJsonData() {
    var jsonData;
    if (qrLocation !== undefined && qrLocation !== "") {
        jsonData = {"device":checkDevice(), "channel":"QR_" + qrLocation, "country":country};
    } else {
        jsonData = {"device":checkDevice(), "channel":"HOMEPAGE", "country":country};
    }
    return jsonData;
}

// 추가 AMR 200412 숫자 천단위 콤마
function numberFormat(inputNumber) {
    return inputNumber.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}


/* Global functions */

function getDate() {
    var today = new Date();
    var year = today.getFullYear(); // 년도
    var month = today.getMonth() + 1;  // 월
    var date = today.getDate();  // 날짜
    var day = today.getDay();  // 요일
    var day_han = new Array('일', '월', '화', '수', '목', '금', '토');
    var res = year + '년 ' + month + '월 ' + date + '일 ' + day_han[day] + '요일';
    return res;
}

function getTime() {
    var today = new Date();
    var time = today.getHours().toString().padStart(2, '0')
        + ":" + today.getMinutes().toString().padStart(2, '0');
    return time;
}

function getTimeWithSecond() {
    var today = new Date();
    var time = today.getHours().toString().padStart(2, '0')
        + ":" + today.getMinutes().toString().padStart(2, '0')
        + ":" + today.getSeconds().toString().padStart(2, '0');
    return time;
}

// AMR 201022 .svc_area (채팅 관련 버튼) 모바일에서 일어나는 이벤트
$('.svc_area').on('click', function(){
    $(this).toggleClass('active');
});

function addLinkClickLog(href){
    var addLinkLog = {};
    addLinkLog.href = href;
    addLinkLog.host = host;
    addLinkLog.lang = lang;
    addLinkLog.sessionId = sessionId;

   $.ajax({
       url: "/aTag/addLinkLog",
       data: JSON.stringify(addLinkLog),
       type: "POST",
       contentType: 'application/json'
   }).done(function (response) {
      console.log("<< Link Log insert >>");
   });
}
