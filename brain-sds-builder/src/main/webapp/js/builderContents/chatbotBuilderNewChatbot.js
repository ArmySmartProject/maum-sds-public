
var chatbotBgColor = {r: 84, g: 84, b: 84};
var chatbotTextColor = '#fff'; // #fff(white), #403738(black) 두가지 중 선택가능. defult는 #fff(white)
var chatbotLogo = '../images/logo_testbot.png';
var chatbotIcon = '../images/ico_testbot.png';
var chatbotLogoSize = 34; // default는 50
var chatbotLogoTop = 'center'; // 값은 center 또는 숫자
var chatbotLogoLeft = 'center'; // 값은 center 또는 숫자
var customStyleTemp = ''; //css text가 들어가는 변수
var nowSubmitted = false;
var addedNewBot = -1;

// 색, 이미지 적용 함수
function setCustom() {
    var rgb = 'rgb(' + chatbotBgColor.r + ',' + chatbotBgColor.g + ',' + chatbotBgColor.b + ')';
    var logoSize = 'auto' + ' ' + chatbotLogoSize + 'px';

    $('#picker').val(rgb);
    $('.pick_color_bg').css({ 'background-color': rgb });
    $('.pick_color_bd').css({ 'border-color': rgb });
    $('.pick_color').css({'color': chatbotTextColor});
    $('.pick_logo').css({
        'background-image': 'url(' + chatbotLogo + ')',
        'background-size': logoSize,
        'background-position': formatLogoPosition(chatbotLogoLeft) + ' ' + formatLogoPosition(chatbotLogoTop)
    });
    $('.pick_icon').css({ 'background-image': 'url(' + chatbotIcon + ')' });
}
setCustom();

// 로고, 아이콘 바꾸기
function changeLogoIcon() {
    var target = event.target;
    var fileName = target.files[0].name;
    var $input = $(target);
    var id = $input.attr('id');

    if (target.files && target.files[0]) {
        var reader = new FileReader();

        reader.onload = function (e) {
            var imgUrl = e.target.result;
            $input.prev('input[type="text"]').val(fileName); //input text에 파일이름 입력

            if (id === 'chat_build_logo') {
                chatbotLogo = imgUrl;
                setCustom(); // 컬러, 이미지 적용 함수
                return;
            }

            if (id === 'chat_build_icon') {
                chatbotIcon = imgUrl;
                setCustom(); // 컬러, 이미지 적용 함수
            }
        };
        reader.readAsDataURL(target.files[0]);
    }
}

// text 색 바꾸기
function changeTextColor() {
    var target = event.target;
    var targetAttr = $(target).attr('for');

    if ( targetAttr === 'text_white' ) {
        chatbotTextColor = '#fff'
        setCustom(); // 컬러, 이미지 적용 함수
        return;
    }

    if ( targetAttr === 'text_black' ) {
        chatbotTextColor = '#403738'
        setCustom(); // 컬러, 이미지 적용 함수
    }
}


// css text를 만듬
function createStyle() {
    var rgb = chatbotBgColor.r + ',' + chatbotBgColor.g + ',' + chatbotBgColor.b;
    var styleTemp = $('#css').text();
    var exChager = styleTemp.replace(/(--main-text-rgb|--main-color-rgb|--main-logo|--main-icon|--logo-size-|--logo-left|--logo-top)/g, function(vl){
        switch(vl){
            case "--main-text-rgb" : return chatbotTextColor;
            case "--main-color-rgb" : return rgb;
            case "--main-logo" : return chatbotLogo;
            case "--main-icon" : return chatbotIcon;
            case "--logo-size-" : return chatbotLogoSize;
            case "--logo-left" : return formatLogoPosition(chatbotLogoLeft);
            case "--logo-top" : return formatLogoPosition(chatbotLogoTop);
        }
    });
    customStyleTemp = exChager;
    startSave();
}

// 스크립트 텍스트 복사
$('.btn_copy').on('click', function(){
    var text = $('.copy_text').text();
    var input = $('<input type="text">');
    var createInput = $('body').append(input);

    input.val(text).select();
    document.execCommand('copy');
    input.remove();
    mui.alert('복사가 완료되었습니다.');
});



// 색상 선택
$('#picker').colpick({
    layout: 'rgbhex', //rgb 값
    submit: 0,
    color: chatbotBgColor, //최초에 셋팅될 색
    onChange: function (hsb, hex, rgb) {
        chatbotBgColor = {r: rgb.r, g: rgb.g, b: rgb.b}
        setCustom(); // 컬러, 이미지 적용 함수
    },
})
    .keyup(function () {
        $(this).colpickSetColor(this.value);
    });


// help 메세지에 help_guide가 있을 경우 챗봇 미리보기에서 해당 영역이 mark됨
$('.help_guide').on('mouseenter', function(){
    var $guideMark = $(this);

    if ( $guideMark.hasClass('main-color') ) {
        $('.pick_color_bg, .pick_color_bd').addClass('color_on');
        $('.pick_preview').removeClass('color_on');
    }
    if ( $guideMark.hasClass('main-text') ) {
        $('.pick_color').addClass('on');
    }
    if ( $guideMark.hasClass('main-logo') ) {
        $('.pick_logo').addClass('on');
    }
    if ( $guideMark.hasClass('main-icon') ) {
        $('.pick_icon').addClass('on');
    }
});

$('.help_guide').on('mouseleave', function(){
    $('.pick_color_bg, .pick_color_bd').removeClass('color_on');
    $('.pick_color, .pick_logo, .pick_icon').removeClass('on');
});

// logo 크기 바꾸기
function changeLogoSize() {
    var target = event.target;
    var size = $(target).val();

    if ( size > 50 ) {
        size = 50;
        $(target).val(size);
        mui.alert('이미지 크기는 50px 보다 커질 수 없습니다.')
    } else if ( size < 1 ) {
        size = 1;
        $(target).val(size);
        mui.alert('이미지 크기는 1px 보다 작아질 수 없습니다.')
    }

    var logoSize = 'auto' + ' ' + size + 'px';
    $('.pick_logo').css({'background-size': logoSize});

    chatbotLogoSize = size;
    changeLogoPosition();
}

// logo 위치 바꾸기
function changeLogoPosition() {
    $('.pick_logo').css({
        'background-position': formatLogoPosition(chatbotLogoLeft) + ' ' + formatLogoPosition(chatbotLogoTop)
    });
}

// logo 위치 값 format 문자와 숫자 구별
function formatLogoPosition(value) { //value는 숫자 또는 'center' 문자만 올 수 있다
    var val = 0;
    if ( $.isNumeric( value ) ) { //값이 숫자이면
        val = value + 'px';
    } else {
        val = value;
    }
    return val;
}

// 로고 위치 라벨 클릭 이벤트
$('.logo_position label').on('click', function(){
    var $thisLabel = $(this);
    var id = $thisLabel.attr('for')
    var $thisPosition = $(this).parent('.logo_position');
    var radioGroup = $thisPosition.find($('input[type="radio"]'));
    var val = 0;

    // 직접입력 시 input활성화
    radioGroup.siblings('.ipt_txt').attr('disabled', '');
    if ( $thisLabel.next('.ipt_txt') ) {
        $thisLabel.next('.ipt_txt').removeAttr('disabled');
    }

    // 가운데 정렬, 직접입력 선택에 따른 값 변화
    if ( id == 'hor_center' || id == 'ver_center' ) {
        val = 'center';
        if ( id == 'hor_center' ) {chatbotLogoLeft = val;}
        if ( id == 'ver_center' ) {chatbotLogoTop = val;}
        $thisPosition.find('input[type="hidden"]').val(val).trigger('change');
    } else if ( id == 'hor_input' || id == 'ver_input' ) {
        val = $thisLabel.next('input[type="number"]').val();
        if ( id == 'hor_input' ) {chatbotLogoLeft = val;}
        if ( id == 'ver_input' ) {chatbotLogoTop = val;}
        $thisPosition.find('input[type="hidden"]').val(val).trigger('change');
    }
});

// 로고 위치 number change 이벤트
$('.logo_position input[type="number"]').on('change', function(){
    var $thisPosition = $(this).parent('.logo_position');
    var targetPosition = $(this).attr('name');
    var val = $(this).val();

    if ( val == '' ) {
        val = 0;
        $(this).val(val);
    }

    if ( val < 0 ) {
        val = 0;
        $(this).val(val);
        mui.alert('이미지 위치 값은 0px 보다 작아질 수 없습니다.')
    }

    // 어느 위치가 change 된 것인지 확인 및 그에 따른 값 변화
    if ( targetPosition == 'horizontal' ) {
        chatbotLogoLeft = val;
    } else if ( targetPosition == 'vertical' ) {
        chatbotLogoTop = val;
    }

    // change 된 값을 input hidden에 넘겨줌
    $thisPosition.find('input[type="hidden"]').val(val).trigger('change');
});

// 로고 크기, 위치 / 엔터, 방향키 조정 이벤트
$('.setting_logo input[type="number"]').on('keyup', function(e){
    var $input = $(this);
    var val = Number($input.val());

    if (event.keyCode == 13) { //엔터
        $input.trigger('change');
        return;
    }
    if (event.keyCode == 38) { //위 방향키
        val = val + 1;
        $input.val(val);
        $input.trigger('change');
        return;
    }
    if (event.keyCode == 40) { //아래 방향키
        val = val - 1;
        $input.val(val);
        $input.trigger('change');
        return;
    }
});

// AMR 크롬에서 방향키 조정 시 2씩 증가하는 걸 막는 코드
$('.setting_logo input[type="number"]').on('keydown', function(e) {
    if (event.keyCode == 38) { //위 방향키
        e.preventDefault();
    }
    if (event.keyCode == 40) { //아래 방향키
        e.preventDefault();
    }
});

function goPage(none, none2){
    if(nowSubmitted){
        mui.alert("현재 챗봇 추가 작업중입니다. 잠시만 기다려주세요. 몇 분 내외로 종료됩니다.")
    }
    else if(none==-99){
        hostToPage = $('#chatListUl>li.active').val();
        changeContents(0);
    }
    else{
        mui.confirm('작성하던 내용이 저장되지 않습니다. 계속 하시겠습니까?', {
            title: '',
            onClose: function(isOk){
                if(isOk){
                    hostToPage = $('#chatListUl>li.active').val();
                    changeContents(0);
                }
            }
        });
    }
}


function checkHostNameNew(){
    if(nowSubmitted){
        mui.alert("현재 챗봇 추가중입니다. 잠시만 기다려주세요.")
        return;
    }
    var obj = new Object();
    let nowHostName = $(".setting > div > dl:nth-child(4) > dd > input").val();
    if(nowHostName.trim().length==0){
        mui.alert("챗봇 ID는 필수 입력사항입니다.");
        return;
    }
    let chatbotName = $(".setting > div > dl:nth-child(3) > dd > input").val();
    if(chatbotName.trim().length==0){
        mui.alert("챗봇명은 필수 입력사항입니다.");
        return;
    }
    var retStr = "";
    var nowCount = 1;
    $("#settingLangCheckbox").find("input").each(function () {
        if($(this).is(":checked")){
            retStr += nowCount.toString() + ",";
        }
        nowCount += 1;
    })
    if(retStr.length>0){
        retStr = retStr.substring(0,retStr.length-1);
    }else{
        mui.alert("언어 선택은 최소한 하나 이상 추가되어야 합니다. ")
        nowSubmitted = false;
        return;
    }

    nowSubmitted = true;
    submitButtonText();
    obj.nowNum = addedNewBot;
    obj.hostName = $(".setting > div > dl:nth-child(4) > dd > input").val();
    addAjax();
    $.ajax({
        url : "checkNewHostname",
        data : JSON.stringify(obj),
        type: "POST",
        contentType: 'application/json'
    }).success(
        function(result) {
            finAjax();
            // console.log(result);
            if(result.toString()!="0"){
                mui.alert("중복된 이름의 챗봇 ID가 존재합니다.")
                nowSubmitted = false;
                submitButtonText();
            }else{
                addChatbot();
            }
        }).fail(function(result) {
        finAjax();
        console.log("error : " + result);
    });
}

function addChatbotList(parNo,parName,parHost){
    $(".no_list").remove();
    var innerHTML = "";
    if ($('#chatListUl>li.active').val() == undefined) {
        innerHTML += '<li value="' + parNo + '" onclick="chatbotClickEvent(-1,this)" class="active">';
    } else {
        innerHTML += '<li value="' + parNo + '" onclick="chatbotClickEvent(-1,this)">';
    }
    innerHTML += '<div>';
    innerHTML += '<span>' + parName+ '</span>';
    innerHTML += '</div>';
    innerHTML += '<div>';
    innerHTML += '<span>Host : ' + parHost + '</span>';
    innerHTML += '</div>';
    innerHTML += '<a href="#chat_list_delete" class="btn_icon delete btn_lyr_open" onclick="event.cancelBubble=true; delChatbot('+parNo+',\''+parName+'\')"></a>';
    innerHTML += '</li>';
    $("#chatListUl").append(innerHTML);
}


function addChatbot(){
    var obj = new Object();
    obj.Name = $(".setting > div > dl:nth-child(3) > dd > input").val()!=undefined
        ? $(".setting > div > dl:nth-child(3) > dd > input").val() : "";
    obj.email = $(".setting > div > dl:nth-child(5) > dd > input").val()!=undefined
        ? $(".setting > div > dl:nth-child(5) > dd > input").val() : "";
    obj.host = $(".setting > div > dl:nth-child(4) > dd > input").val()!=undefined
        ? $(".setting > div > dl:nth-child(4) > dd > input").val() : "";
    obj.description = $(".setting > div > dl:nth-child(6) > dd > textarea").val()!=undefined
        ? $(".setting > div > dl:nth-child(6) > dd > textarea").val() : "";
    obj.nowCategory = $("#nowCategoryID").val();

    var retStr = "";
    var nowCount = 1;
    $("#settingLangCheckbox").find("input").each(function () {
        if($(this).is(":checked")){
            retStr += nowCount.toString() + ",";
        }
        nowCount += 1;
    })
    if(retStr.length>0){
        retStr = retStr.substring(0,retStr.length-1);
    }
    obj.langStr = retStr;
    console.log(obj)
    addAjax();
    $.ajax({
        url : "addNewChatbot",
        data : JSON.stringify(obj),
        type: "POST",
        contentType: 'application/json',
        timeout: 1200000,
    }).success(
        function(result) {
            finAjax();
            addedNewBot = result;
            // console.log(result);
            startSaveNew(result);

        }).fail(function(result) {
        finAjax();
        nowSubmitted = false;
        submitButtonText();
        console.log("error : " + result);
    });
}




function logoUploadNew(){
    if($("input[id=chat_build_logo]")[0].files.length>0){
        let logoFormData = new FormData();
        logoFormData.append("host", addedNewBot);
        logoFormData.append("logoImg",$("input[id=chat_build_logo]")[0].files[0]);
        addAjax();
        $.ajax({
            type: "POST",
            enctype: 'multipart/form-data',
            url: "saveLogoImgSetting",
            data: logoFormData,
            processData: false,
            contentType: false,
            cache: false,
            timeout: 600000,
            success: function (data) {
                finAjax();
                // console.log("logo uploaded")
                IconUploadNew();
            },
            error: function (e) {
                finAjax();
                nowSubmitted = false;
                submitButtonText();
                console.log("ERROR : ", e);
            }
        });
    }else{
        IconUploadNew();
    }
}

function IconUploadNew(){
    if($("input[id=chat_build_icon]")[0].files.length>0){
        let IconFormData = new FormData();
        IconFormData.append("host", addedNewBot);
        IconFormData.append("IconImg",$("input[id=chat_build_icon]")[0].files[0]);
        addAjax();
        $.ajax({
            type: "POST",
            enctype: 'multipart/form-data',
            url: "saveIconImgSetting",
            data: IconFormData,
            processData: false,
            contentType: false,
            cache: false,
            timeout: 600000,
            success: function (data) {
                finAjax();
                // console.log("Icon uploaded")
                saveSettingNew();
            },
            error: function (e) {
                finAjax();
                nowSubmitted = false;
                submitButtonText();
                console.log("ERROR : ", e);
            }
        });
    }else{
        saveSettingNew();
    }
}



function startSaveNew(nowHostPK){
    let startFormData = new FormData();
    startFormData.append("host", nowHostPK);
    //StyleCSS 수정 부분
    if(chatbotTextColor=='#fff'){startFormData.append("textColor","255,255,255");} //white
    else{startFormData.append("textColor","64,55,56");} // black
    startFormData.append("mainColor",chatbotBgColor.r+","+chatbotBgColor.g+","+chatbotBgColor.b);


    startFormData.append("logoSize",chatbotLogoSize);
    startFormData.append("posY",chatbotLogoTop==="center" ? '-1' : chatbotLogoTop);
    startFormData.append("posX",chatbotLogoLeft==="center" ? '-1' : chatbotLogoLeft);
    addAjax();
    $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: "startSaveSetting",
        data: startFormData,
        processData: false,
        contentType: false,
        cache: false,
        timeout: 600000,
        success: function (data) {
            finAjax();
            logoUploadNew();
        },
        error: function (e) {
            finAjax();
            nowSubmitted = false;
            submitButtonText();
            console.log("ERROR : ", e);
        }})
}


function saveSettingNew(){
    var formData = new FormData();
    formData.append("host", addedNewBot);
    //Account 수정 부분
    formData.append("name",
        $(".setting > div > dl:nth-child(3) > dd > input").val()!=undefined
            ? $(".setting > div > dl:nth-child(3) > dd > input").val() : "");
    formData.append("hostName",
        $(".setting > div > dl:nth-child(3) > dd > input").val()!=undefined
            ? $(".setting > div > dl:nth-child(4) > dd > input").val() : "");
    formData.append("email",
        $(".setting > div > dl:nth-child(5) > dd > input").val()!=undefined
            ? $(".setting > div > dl:nth-child(5) > dd > input").val() : "");
    formData.append("description",
        $(".setting > div > dl:nth-child(6) > dd > textarea").val()!=undefined
            ? $(".setting > div > dl:nth-child(6) > dd > textarea").val() : "");


    addAjax();
    $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: "saveSetting",
        data: formData,
        processData: false,
        contentType: false,
        cache: false,
        timeout: 600000,
        success: function (data) {
            finAjax();
            insertBotMapping();
        },
        error: function (e) {
            finAjax();
            nowSubmitted = false;
            submitButtonText();
            console.log("ERROR : ", e);
        }
    });
}

function insertBotMapping(){

    window.parent.postMessage({host:addedNewBot, isGroup:isGroup, isInsert:'y'}, '*');
    window.addEventListener("message", callbackResult, false);

}

function callbackResult(e) {
    window.removeEventListener("message", callbackResult);
    var result;
    result = e.data.callback;
    if (result == 'y') {
        insertBotMappingCallback();
    } else {
        nowSubmitted = false;
        submitButtonText();
        mui.alert("챗봇 추가에 실패했습니다")
    }
}

function insertBotMappingCallback(){
    let newName = $(".setting > div > dl:nth-child(3) > dd > input").val()!=undefined
        ? $(".setting > div > dl:nth-child(3) > dd > input").val() : "";
    let newHost = $(".setting > div > dl:nth-child(4) > dd > input").val()!=undefined
        ? $(".setting > div > dl:nth-child(4) > dd > input").val() : "";
    addChatbotList(addedNewBot, newName, newHost);
    nowSubmitted = false;
    submitButtonText();
    mui.alert("저장 완료 되었습니다.");
    hostToPage = -1;
    afterInsert = true;
    getChatList();
    changeContents(0);
    overlayTutorial(2);
    // goPage(-99,0);
}

function submitButtonText(){
    if(nowSubmitted){
        $("#newChatBut").html("저장중");
        $("#nowCategoryID option").not(":selected").attr("disabled", "disabled");
        $(".setting > div > dl:nth-child(3) > dd > input").attr("readonly",true);
        $(".setting > div > dl:nth-child(4) > dd > input").attr("readonly",true);
        $(".setting > div > dl:nth-child(5) > dd > input").attr("readonly",true);
        $(".setting > div > dl:nth-child(6) > dd > textarea").attr("readonly",true);
        $("#picker").attr("readonly",true);
        $("#text_white").attr("readonly",true);
        $("#text_black").attr("readonly",true);
        $("#newBuildLogo").css("display","none");
        $("#newBuildIcon").css("display","none");
        $(".setting > div > dl:nth-child(7)").css("display","none");
        $(".setting > div > dl:nth-child(8)").css("display","none");
    }else{
        $("#newChatBut").html("저장");
        $("#nowCategoryID option").not(":selected").attr("disabled", "");
        $(".setting > div > dl:nth-child(3) > dd > input").removeAttr("readonly");
        $(".setting > div > dl:nth-child(4) > dd > input").removeAttr("readonly");
        $(".setting > div > dl:nth-child(5) > dd > input").removeAttr("readonly");
        $(".setting > div > dl:nth-child(6) > dd > textarea").removeAttr("readonly");
        $("#picker").removeAttr("readonly");
        $("#text_white").removeAttr("readonly");
        $("#text_black").removeAttr("readonly");
        $("#newBuildLogo").css("display","table-cell");
        $("#newBuildIcon").css("display","table-cell");
        $(".setting > div > dl:nth-child(7)").css("display","block");
        $(".setting > div > dl:nth-child(8)").css("display","block");
    }
}