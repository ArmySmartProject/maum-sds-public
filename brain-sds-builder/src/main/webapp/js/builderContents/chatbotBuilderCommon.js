debugTestResizeArea();
var chatList;
var isInIframe = false;
var isGroup;
var nowLangApply            = [true,true,true,true];
var allChatbotLang = [];
var nowSearchLang = 1;
var fixLang = 0;
var maxChat;
var afterInsert = false;
var afterDelete = false;
var nowAjax = 0;
var nowDelHost = 0;
var nowChatLists = true;
var userAuthTy = '';
var botMappingList = [];

$(document).ready(function () {

    isInIframe = (window.location != window.parent.location);
    if (isInIframe === true) {
        $("#header").hide();
        window.addEventListener("message", getChatList, false);
    } else {
        getChatList();
    }
});

document.addEventListener('copy', function(e){
    let selection = window.getSelection().toString();
    e.clipboardData.setData('text/plain', selection);
    e.preventDefault();
});

function addToBack(){
  $("#chatListUl>li.active").click();
}

function getChatList(e) {
    console.log("getChatList");
    var obj = new Object();
    if (isInIframe == true && e != undefined) {
        chatList = e.data.chatbotList;
        isGroup = e.data.isGroup;
        maxChat = e.data.maxChat;
        userAuthTy = e.data.userAuthTy;
        if ( e.data.botMappingList ) {
            botMappingList = JSON.parse(e.data.botMappingList);
        }
    } else if (afterInsert == true) {
        chatList += '{BOT_ID=' + addedNewBot + '}';
    } else if (afterDelete == true) {
        chatList = chatList.replace('{BOT_ID=' + nowDelHost + '}', '');
    } else {
        userAuthTy = 'S';
        chatList = '{1111},{404},{51},{1147},{1148},{1149}';
    }
    obj.chatList = chatList;

    addAjax();
    $.ajax({
        url : "getChatList",
        data : JSON.stringify(obj),
        type: "POST",
        contentType: 'application/json'
    }).success(
        function(result) {
            finAjax();

            var list = result.chatList;
            $("#chatListUl").empty();

            var innerHTML = "";
            allChatbotLang = [];
            try{
                $.each(list, function (i, v) {


                    if (i == 0) {
                        innerHTML += '<li class="active" value="' + v.No + '" onclick="chatbotClickEvent('+i+',this)">';
                    } else {
                        innerHTML += '<li value="' + v.No + '" onclick="chatbotClickEvent('+i+',this)">';
                    }
                    innerHTML += '<div>';
                    innerHTML += '<span>' + v.Name+ '</span>';
                    innerHTML += '</div>';
                    innerHTML += '<div>';
                    innerHTML += '<span>Host : ' + v.Host + '</span>';
                    innerHTML += '</div>';
                    innerHTML += '<input type="hidden" value="' +v.HTaskYN + '" />';
                    innerHTML += '<a href="#chat_list_delete" class="btn_icon delete btn_lyr_open"  onclick="event.cancelBubble=true; delChatbot('+v.No+',\''+v.Name+'\')"></a>';
                    innerHTML += '</li>';

                    let nowNewChatbotLng = [false,false,false,false];
                    try{
                        let langDBText = v.Language.split(",");
                        for(let ii = 0; ii<langDBText.length; ii++){
                            nowNewChatbotLng[Number(langDBText[ii].trim())-1] = true;
                        }
                    }catch{
                        nowNewChatbotLng = [true,true,true,true]
                    }
                    allChatbotLang.push(nowNewChatbotLng);
                });
                setChatbotLang(0);

                $("#chatListUl").append(innerHTML);

                changeContents(0);
                nowChatLists = true;
                // overlayTutorial(2);//TODO : REMOVE
            }catch(e){
                $("#chatListUl").append("<li class=\"no_list\">등록된 챗봇이 없습니다.</li>");
                nowChatLists = false;
                overlayTutorial(0);
            }
        }).fail(function(result) {
        finAjax();
        console.log("getIntentList error");
    });
    window.removeEventListener("message", getChatList);
    afterDelete = false;
    afterInsert = false;
};

function changeContents(contentIndex){
    if(contentIndex==3){
        if(nowChatLists===false){
            changeContents(99);
            return;
        }else{
            maxChatbotCheck();
            return;
        }
    }if(contentIndex==99){//챗봇이 아예 없을 때에 강제로 새 챗봇 화면 진입
        contentIndex = 3;
        overlayTutorial(1);
    }
    var obj = new Object();
    var contents = ["Answer","Intention","Setting", "NewChatbot", "ReplaceDict", "DeepLearning"];
    // contentIndex = contentIndex == 5 ? 4 : contentIndex;

    if(checkAjax()){return;}
    else{
        $.ajax({
            url : "builderContents" + contents[contentIndex],
            data : JSON.stringify(obj),
            type: "POST",
            contentType: 'application/json'
        }).success(
            function(result) {
                finAjax();
                var btnOn = $('.chatbot_builder .btn_test_on');
                var btnOff = $('.chatbot_builder .btn_test_off');
                langCheck();
                if(contentIndex==0 || contentIndex==1){ // 답변, 의도
                    btnOn.css("display","block");
                    $(".prev_menu_cont").next().attr('class','menu_cont');
                    $(".prev_menu_cont").prev().css("display","block");
                    $("#upper_lang_list").css("display","inline-block");
                    $("#addChatbotLeftBut").css("display","block");
                }
                else if(contentIndex==2){ // 설정
                    $('.chatbot_builder').removeClass('test_on');
                    btnOn.removeClass('hide');
                    btnOn.css("display","none");
                    $(".prev_menu_cont").next().attr('class','menu_cont chatbot_setting scroll');
                    $(".prev_menu_cont").prev().css("display","block");
                    $("#upper_lang_list").css("display","none");
                    $("#addChatbotLeftBut").css("display","block");
                    iframeReset();
                } else if (contentIndex==4) { // 치환사전
                    $('.chatbot_builder').removeClass('test_on');
                    btnOn.removeClass('hide');
                    btnOn.css("display","none");
                    $(".prev_menu_cont").next().attr('class','menu_cont chatbot_replace');
                    $(".prev_menu_cont").prev().css("display","block");
                    $("#upper_lang_list").css("display","inline-block");
                    $("#addChatbotLeftBut").css("display","block");
                } else if (contentIndex==5) { // 딥러닝
                    $('.chatbot_builder').removeClass('test_on');
                    btnOn.removeClass('hide');
                    btnOn.css("display","none");
                    $(".prev_menu_cont").next().attr('class','menu_cont');
                    $(".prev_menu_cont").prev().css("display","block");
                    $("#upper_lang_list").css("display","inline-block");
                    $("#addChatbotLeftBut").css("display","block");
                } else{// 챗봇 추가부분
                    btnOn.removeClass('hide');
                    btnOn.css("display","none");
                    $(".prev_menu_cont").next().attr('class','menu_cont chatbot_setting scroll');
                    $(".prev_menu_cont").prev().css("display","none");
                    $("#upper_lang_list").css("display","none");
                    iframeReset();
                    $("#chatTitle").text("새 챗봇 추가하기");

                    if(nowChatLists){
                      $("#chatTitle").append('<button type="button" class="btn_primary" style="margin-left: 10px;" onclick="addToBack();">이전</button>');
                    }

                    $("#addChatbotLeftBut").css("display","none");
                }
                $(".prev_menu_cont").next().empty();
                $(".prev_menu_cont").next().append(result.toString());
                if(contentIndex == 1) {
                    checkNqaTrained();
                }

                var nowMenu = $("#container > div > div > div.lot_wrap > div.lotBox.chatbot_content > div.cont > ul").find('li');
                nowMenu.removeClass('active');
                $.each(nowMenu, function (i, v) {
                    var $v = $(v);

                    if ( Number($v.attr('data-index')) === contentIndex ) {
                        $v.addClass('active');
                    }
                });
            });
        $.ajax({
            url : "builderBottom" + contents[contentIndex],
            data : JSON.stringify(obj),
            type: "POST",
            contentType: 'application/json'
        }).success(
            function(result) {
                finAjax();
                $(".non_wrap").empty();
                $(".non_wrap").append(result.toString());
            }).fail(function(result) {
            finAjax();
        })
    }
}


function setLangToggle(){
    let nowCount = 0;
    nowSearchLang = 0;
    fixLang = 0;
    $("#upper_lang_list").find('ul').find('li').each( function()
    {
        $(this).removeClass("active");
        if(nowLangApply[nowCount]){
            if(fixLang==0) fixLang = nowCount+1;
            $(this).removeClass("none");
            if(nowSearchLang==0){
                nowSearchLang = nowCount+1;
                $(this).addClass("active");
            }
        }else{
            $(this).addClass("none");
        }
        nowCount++;
    } );
}

function setChatbotLang(langIndex){
    iframeReset();
    $('.chatbot_builder').removeClass('test_on');
    var btnOn = $('.chatbot_builder .btn_test_on');
    btnOn.removeClass('hide');
    nowLangApply = allChatbotLang[langIndex];
    setLangToggle();
    langCheck();
}

function langOnclick(langIndex){
    nowSearchLang = langIndex;
    let nowCount = 1;
    $("#upper_lang_list").find('ul').find('li').each( function()
    {
        $(this).removeClass("active");
        if(nowSearchLang==nowCount){
            $(this).addClass("active");
        }
        nowCount++;
    } );
    chatClick(hostToPage, 1);
}

function maxChatbotCheck(){
    window.parent.postMessage({check:true, isGroup:isGroup}, '*');
    window.addEventListener("message", callbackMaxChatbot, false);
}

function callbackMaxChatbot(e) {
    window.removeEventListener("message", callbackMaxChatbot);
    var result;
    result = e.data.callback;
    if (result == 'y') {
        changeContents(99);
        return;
    } else {
        if(nowTutorial){
            changeContents(99);
            return;
        }else{
            mui.alert("챗봇이 최대 수를 넘었습니다.")
            return;
        }

    }
}

function langCheck() {
    var lang = ['korea', 'us', 'japan', 'china'];
    if (nowLangApply != undefined) {
        for (var i=0; i<nowLangApply.length; i++) {
            if(nowLangApply[i] == true) {
                $("."+lang[i]).removeClass("none");
            }
        }
        $("#nqa_guide").attr('style', 'display:none');
    }
}

function debugTestResizeArea(){
    let parent = $('.resize_areas');
    let parentHeight = parent.outerHeight() - 14; // dragTarget height 제외
    let childrenArea = parent.find('.resize_area')
    let firstCldHeight = 0;
    let lastCldHeight = 0;
    let startY = 0;
    let delY = 0;

    parent.children('.drag_resize').on('mousedown', function(e){
        let dragTarget = $(this);
        firstCldHeight = childrenArea.eq(0).outerHeight();
        lastCldHeight = childrenArea.eq(1).outerHeight();
        startY = e.pageY;

        childrenArea.append('<div class="frontDrop"></div>');
        dragTarget.addClass('grabbing');
        parent.css('user-select', 'none');

        $('body').on('mousemove', function(e){
            delY = startY - e.pageY;
            let delFirstCldHeight = firstCldHeight - delY;
            let delLastCldHeight = lastCldHeight + delY;

            if ( delFirstCldHeight < 100 ) {
                delFirstCldHeight = 100;
                delLastCldHeight = parentHeight - delFirstCldHeight;
            }

            if ( delLastCldHeight < 100 ) {
                delLastCldHeight = 100;
                delFirstCldHeight = parentHeight - delLastCldHeight;
            }

            childrenArea.eq(0).css('height', delFirstCldHeight + 'px');
            childrenArea.eq(1).css('height', delLastCldHeight + 'px');
        });

        $('body').on('mouseup', function(e){
            childrenArea.find('.frontDrop').remove();
            dragTarget.removeClass('grabbing');
            parent.css('user-select', 'auto');

            $('body').off('mousemove mouseup');
        });
    });
}

function addAjax(){nowAjax++;}
function finAjax(){nowAjax--;}
function checkAjax(){
    if(nowAjax>0){
        console.log("nowAjax == " + nowAjax)
        return true;
    }else{// 화면 변경에는 botton, contents 두개를 각각 요청
        addAjax();
        addAjax();
        return false;
    }
}
// Ajax 요청 바로 이전        :   addAjax();
// Ajax 요청 callback 최상단  :   finAjax();
// 화면 변경 부분             :   if(checkAjax()){return;}
// 화면 변경 콜백             :   finAjax();

var hostToPage=-1;


function resetSessionByBuilder(){
    $('#testIframe').get(0).contentWindow.postMessage({data:"resetSession"},"*");
    nowResetClicked = true;
}


function delChatbot(no, hostName){
    mui.confirm(hostName + " 챗봇을 삭제합니다.", {
        title: '',
        onClose: function(isOk){
            if(isOk){
                delChatbotCheck(no, hostName);
            }
        }
    });
}

function delChatbotCheck(no, hostName){
    var obj = new Object();
    obj.nowDelHost = no;
    addAjax();
    $.ajax({
        url : "delHost",
        data : JSON.stringify(obj),
        type: "POST",
        contentType: 'application/json'
    }).success(
        function(result) {
            finAjax();
            if(result.return==="FAILED"){
                console.log("삭제 도중 오류가 발생했습니다.")
            }else{
                nowDelHost = result.nowDelHost;
                console.log("BotMapping DEL");
                delBotMapping(nowDelHost);
            }
        }).fail(function(result) {
        finAjax();
        console.log("delHost Error");
    });
}

function delBotMapping(host){
    window.parent.postMessage({host:host, isGroup:isGroup}, '*');
    window.addEventListener("message", callbackDelBotMapping, false);
}

function callbackDelBotMapping(e) {
    window.removeEventListener("message", callbackDelBotMapping);
    var result;
    result = e.data.callback;
    if (result == 'y') {
        afterDelChatbot();
    } else {
        console.log('BotMapping 삭제 실패');
    }
}

function afterDelChatbot(){
    hostToPage = -1;
    afterDelete = true;
    getChatList();
    changeContents(0);
}

function chatbotClickEvent(nowLangIndex, thisPointer){
    if(nowAjax===0){
        $("#chatListUl").find('li').each(function () {
            $(this).removeClass("active");
        })
        if(nowLangIndex!=-1){
            setChatbotLang(nowLangIndex);
        }
        $(thisPointer).addClass("active");
        goPage(thisPointer.value,1);
    }else{
        console.log("nowAjax :: " + nowAjax);
    }
}

var tutorialList = [
    ["add_step01",      "챗봇추가 step 01.",              "챗봇추가 버튼을 눌러 챗봇을 추가하려고 합니다",""],
    ["add_step02",      "챗봇추가 step 02.",              "챗봇의 이름과 ID를 설정합니다","챗봇명과 챗봇ID를 자유롭게 입력한 후 저장버튼을 눌러주세요</p>\n"
                                                       + "<p class=\"desc\">튜토리얼에서는 카페 챗봇을 만들어 보겠습니다"],
    ["intent_step01",   "챗봇의도 생성 step 01.",         "이제 고객의 의도를 파악하기 위해 예상되는 의도를 추가하려고 합니다.","의도 추가 버튼을 누르십시오."],
    ["intent_step02",   "챗봇의도 생성 step 02.",         "챗봇 의도 설정이란, 사용자의 발화에 관해 뜻을 파악하는 것입니다.","\"아메리카노\"라는 의도에 대해 등록해 보겠습니다.</p>\n"
                                                       + "<p class=\"desc\">\"아메리카노\"라는 의도를 생성해 주십시오."],
    ["intent_step03",   "챗봇의도 생성 step 03.",         "생성된 의도에 관해 학습문장을 추가하겠습니다.","\"아메리카노\"라는 의도의 상세를 선택해 주세요."],
    ["intent_step04",   "챗봇의도 생성 step 04.",         "학습문장을 추가해 줍시다","학습문장을 클릭한 뒤 개별추가를 눌러주세요"],
    ["intent_step0402", "챗봇의도 생성 step 0402.",       "학습문장등록","사용자가 편하게 작성한 문장에 대해서 알아들을 수 있게 학습문장을 추가합니다.</p>\n"
                                                       + "<p class=\"desc\">추가를 눌러 사용자가 \"아메리카노\"라는 의도에 관해 어떤 학습 문장을 넣을지 자유롭게 생각해 주십시오.</p>\n"
                                                       + "<p class=\"desc\">학습문장 가이드로는 \"아메리카노 주문할게요.\"를 넣어서 추가 버튼을 누르도록 하겠습니다."],
    ["intent_step0403", "챗봇의도 생성 step 0403.",       "사용자의 발화에 관해 이해하도록 학습.","추가한 학습 문장에 관해 챗봇에게 학습을 시키려 합니다.</p>\n"
                                                       + "<p class=\"desc\">의도를 다시 선택해 주십시오."],
    ["intent_step0404", "챗봇의도 생성 step 0404.",       "사용자의 발화에 관해 이해하도록 학습.","학습 버튼을 클릭해 주십시오."],
    ["intent_step0405", "챗봇의도 생성 step 0405.",       "학습문장 학습","학습이 끝났으니 창을 닫아주세요"],
    ["task_step01",     "챗봇태스크 생성 step 01.",       "챗봇의 답변을 설정합시다.","태스크 추가 버튼을 눌러주세요"],
    ["task_step02",     "챗봇태스크 생성 step 02.",       "답변을 입력해 주세요</p>\n",
                                                         "<p class=\"desc\">답변 칸에 \"안녕하세요 마인즈랩 카페입니다\"로 넣어 주세요</p>\n"
                                                       + "<p class=\"desc\">방금 등록한 \"아메리카노\"를 태스크 관계에 설정해주세요. (항상, 아메리카노)</p>\n"
                                                       + "<p class=\"desc\">확인 버튼을 눌러 주세요"],
    ["test_step01",     "챗봇 테스트하기 step 01.",       "등록한 내용에 대해서 테스트를 해볼 수 있습니다.","등록한 \"아메리카노\"로 테스트를 해볼까요??"],
]
var nowTutorial = false; //TODO : false로 바꾸기
function overlayTutorial(index){
    let indexLst = [0,0,1,1,1,1,1,1,1,1,2,2,3];
    let cbTutorialLst = $('#cb_tutorial_list').children('div');

    if(0 <= index && index < 13){
        $('#cb_tutorial_list').attr('style','display:""');
        cbTutorialLst.removeClass('active');
        cbTutorialLst.eq(indexLst[index]).addClass('active');
    }else{
        $('#cb_tutorial_list').attr('style','display:none;');
    }

    if(index>=tutorialList.length){
        $("body").attr('id',"default_body");
        $("body").attr('class',"");
        $("#cb_tutorial_skip").remove();
        $("#cb_tutorial_msg").remove();
        console.log("overlay tutorial ended")
        nowTutorial = false;
        return;
    }
    if(nowTutorial || index==0){
        if($("body").attr('id')==="default_body"){
            $("#default_body").attr('id',"cb_tutorial");
            let nowExitBut = "<button type=\"button\" id=\"cb_tutorial_skip\" onclick=\"overlayTutorial(10000)\"></button>";
            $("#cb_tutorial").prepend(nowExitBut);
        }
        if(index==tutorialList.length-1) {
            $("#cb_tutorial_skip").text("튜토리얼 마치기")
        }else{
            $("#cb_tutorial_skip").text("그만볼래요")
        }
        $("#cb_tutorial_msg").remove();
        let nowHtml = "<div id=\"cb_tutorial_msg\">\n"
            + "    <p class=\"tit\"><span>"+tutorialList[index][1]+"</span>"+tutorialList[index][2]+"<p>\n"
            + "    <p class=\"desc\">"+tutorialList[index][3]+"</p>\n"
            + "</div>";
        $("body").attr('class',tutorialList[index][0]);
        $("#cb_tutorial").prepend(nowHtml);
        nowTutorial = true;
        console.log("overlay tutorial :: " + index);
    }else{
        $('#cb_tutorial_list').attr('style','display:none;');

    }
}

function delCacheByHost(nowDelHost){
    var obj = new Object();
    obj.delHost = nowDelHost;
    $.ajax({
        url : "deleteCache",
        data : JSON.stringify(obj),
        type: "POST",
        contentType: 'application/json'
    }).success(
        function(result) {
            console.log("delCache : " + result);
        }).fail(function(result) {
    });
}

function delCacheNow(){
    delCacheByHost(hostToPage);
}