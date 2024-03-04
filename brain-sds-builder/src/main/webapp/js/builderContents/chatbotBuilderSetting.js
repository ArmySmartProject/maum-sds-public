var chatbotBgColor = {r: 84, g: 84, b: 84};
var chatbotTextColor = '#fff'; // #fff(white), #403738(black) 두가지 중 선택가능. defult는 #fff(white)
var chatbotLogo = './setImage/'+hostToPage+'/logoImg?time='+getUUID();
var chatbotIcon = './setImage/'+hostToPage+'/IconImg?time='+getUUID();
var chatbotLogoSize = 34; // default는 50
var chatbotLogoTop = 'center'; // 값은 center 또는 숫자
var chatbotLogoLeft = 'center'; // 값은 center 또는 숫자
var customStyleTemp = ''; //css text가 들어가는 변수
var nowModifyingStyleNo = -1;
var logoReset = false;
var iconReset = false;


function getUUID() {
    function s4() {
        return ((1 + Math.random()) * 0x10000 | 0).toString(16).substring(1);
    }
    return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4()
        + s4();
}

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
    if(id==="chat_build_logo"){
        console.log("logo changed");
        logoReset = false;
    }
    if(id==="chat_build_icon"){
        console.log("icon changed");
        iconReset = false;
    }

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


function chatClick(host, cp) {
    var obj = new Object();
    obj.host = host;
    nowModifyingStyleNo = -1;
    hostToPage = host;
    chatbotLogo = './setImage/'+hostToPage+'/logoImg?time='+getUUID();
    chatbotIcon = './setImage/'+hostToPage+'/IconImg?time='+getUUID();
    logoReset = false;
    iconReset = false;
    addAjax();
    $.ajax({
        url : "getSetting",
        data : JSON.stringify(obj),
        type: "POST",
        contentType: 'application/json'
    }).success(
        function(result) {
            finAjax();
            $("#chatListUl").find('li').removeClass('active');
            $("#chatListUl").find('li').each(function() {
                if (hostToPage == $(this).val()) {
                    $(this).addClass('active');
                }
            });
            let ansList = result.answerList;

            let nowCount = 0;
            let nowLangCount = 0;
            let defaultLangLst = ['kor','eng','jpn','chn'];
            let addDefualtLang = '<option value="">선택</option>'+
                '<option value="kor">한국어(기본)</optoin>'+
                '<option value="eng">영어</optoin>'+
                '<option value="jpn">일본어</optoin>'+
                '<option value="chn">중국어</optoin>';

            $('#defualtLang option').remove();
            $('#defualtLang').append(addDefualtLang);

            $("#settingLangCheckbox").find("input").each(function () {
                if(nowLangApply[nowCount]){
                    $(this).prop("checked", true);
                    nowLangCount += 1;
                }else{
                    $("#defualtLang option[value='"+defaultLangLst[nowCount]+"']").remove();
                    $(this).prop("checked", false);
                }
                nowCount += 1;
            })
            if(nowLangCount<2){
                $("#preview_lang_globe").css("display","none");
            }else{
                $("#preview_lang_globe").css("display","");
            }

            $(".setting > div > dl:nth-child(3) > dd > input").val(ansList.Name);
            $(".setting > div > dl:nth-child(4) > dd > input").val(ansList.HOST);
            $(".setting > div > dl:nth-child(5) > dd > input").val(ansList.Email);
            $(".setting > div > dl:nth-child(6) > dd > textarea").val(ansList.Description);
            $("#chatTitle").text(ansList.Name);

            // 소스코드복사 부분
            let nowHostName  = ansList.HOST;
            let nowCopyText = "<!-- header에 추가 -->\n" +
                "<link rel=\"stylesheet\" href=\"http://10.50.1.19:28080/css?host="+nowHostName+"\"/>\n" +
                "<!-- body 가장 하단에 추가 -->\n" +
                "<script src=\"http://10.50.1.19:28080/js/" + nowHostName + "\"></script>";
            $(".copy_text").text(nowCopyText);
            let nowQR = "http://10.50.1.19:18090/sds-builder/upload/getTestPage?host="+host;
            console.log("nowQR : " + nowQR)
            qrcode.clear();
            qrcode.makeCode(nowQR);


            let cssStyleList = result.cssList;
            if(cssStyleList!=undefined){
                nowModifyingStyleNo = cssStyleList.styleNo;
                var nowColor = cssStyleList.mainColor.split(",")
                chatbotBgColor = {r: nowColor[0], g: nowColor[1], b: nowColor[2]};
                if(cssStyleList.textColor == "white" || cssStyleList.textColor == "255,255,255"){
                    chatbotTextColor = '#fff'
                    $("#text_white").next().click();
                }
                else {
                    chatbotTextColor = '#403738'
                    $("#text_black").next().click();
                }
                //DB에서 XY Size값 가져오기 (모두 String)
                chatbotLogoSize = cssStyleList.logoSize.toString();
                $(".logo_size").val(chatbotLogoSize);
                if(cssStyleList.posX==-1){
                    chatbotLogoLeft = 'center';
                }else{
                    chatbotLogoLeft = cssStyleList.posX.toString();
                    $(".setting_logo > div:nth-child(2) > label:nth-child(6)").click();
                    $(".setting_logo > div:nth-child(2) > input.ipt_txt").val(cssStyleList.posX);
                    $(".setting_logo > div:nth-child(2) > input.ipt_txt").change();
                }
                if(cssStyleList.posY==-1){
                    chatbotLogoTop = 'center';
                }else{
                    chatbotLogoTop = cssStyleList.posY.toString();
                    $(".setting_logo > div:nth-child(3) > label:nth-child(6)").click();
                    $(".setting_logo > div:nth-child(3) > input.ipt_txt").val(cssStyleList.posY);
                    $(".setting_logo > div:nth-child(3) > input.ipt_txt").change();
                }
                setCustom();
            }else{
                chatbotBgColor = {r: 84, g: 84, b: 84};
                chatbotTextColor = '#fff'
                $("#text_white").next().click();
                setCustom();
            }

        }).fail(function(result) {
        finAjax();
        console.log("getSetting error");
    });
};

function saveSetting(){
    var formData = new FormData();
    formData.append("host", hostToPage);
    //Account 수정 부분
    formData.append("name",
        $(".setting > div > dl:nth-child(3) > dd > input").val()!=undefined
            ? $(".setting > div > dl:nth-child(3) > dd > input").val() : "");
    formData.append("hostName",
        $(".setting > div > dl:nth-child(4) > dd > input").val()!=undefined
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
            mui.alert("저장 완료 되었습니다.")
            $("#searchChatText").val("");
            $("#chatTitle").text($(".setting > div > dl:nth-child(3) > dd > input").val());
            chatSearch();
            chatClick(hostToPage,0);
        },
        error: function (e) {
            finAjax();
            console.log("ERROR : ", e);
        }
    });
}


function logoUpload(){
    if(logoReset){
        let logoFormData = new FormData();
        logoFormData.append("host", hostToPage);
        logoFormData.append("logoIcon","logoImg");
        addAjax();
        $.ajax({
            type: "POST",
            enctype: 'multipart/form-data',
            url: "delLogoImgSetting",
            data: logoFormData,
            processData: false,
            contentType: false,
            cache: false,
            timeout: 600000,
            success: function (data) {
                finAjax();
                console.log("logo Deleted");
                IconUpload();
            },
            error: function (e) {
                finAjax();
                console.log("ERROR : ", e);
            }
        });
    }else if($("input[id=chat_build_logo]")[0].files.length>0){
        let logoFormData = new FormData();
        logoFormData.append("host", hostToPage);
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
                console.log("IconUpload");
                IconUpload();
            },
            error: function (e) {
                finAjax();
                console.log("ERROR : ", e);
            }
        });
    }else{
        console.log("IconUpload");
        IconUpload();
    }
}

function IconUpload(){
    if(iconReset){
        let logoFormData = new FormData();
        logoFormData.append("host", hostToPage);
        logoFormData.append("logoIcon","IconImg");
        addAjax();
        $.ajax({
            type: "POST",
            enctype: 'multipart/form-data',
            url: "delLogoImgSetting",
            data: logoFormData,
            processData: false,
            contentType: false,
            cache: false,
            timeout: 600000,
            success: function (data) {
                finAjax();
                console.log("Icon Deleted");
                saveSetting();
            },
            error: function (e) {
                finAjax();
                console.log("ERROR : ", e);
            }
        });
    }else if($("input[id=chat_build_icon]")[0].files.length>0){
        let IconFormData = new FormData();
        IconFormData.append("host", hostToPage);
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
                saveSetting();
            },
            error: function (e) {
                finAjax();
                console.log("ERROR : ", e);
            }
        });
    }else{
        console.log("saveSetting");
        saveSetting();
    }
}

function startSave(){
    let startFormData = new FormData();
    startFormData.append("host", hostToPage);
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
            // console.log("start save")
            console.log("logoUpload");
            logoUpload();
        },
        error: function (e) {
            finAjax();
            console.log("ERROR : ", e);
        }})
}

function changeLang(){
    let retStr = "";
    let nowCount = 1;
    let retList = [false,false,false,false];
    $("#settingLangCheckbox").find("input").each(function () {
        if($(this).is(":checked")){
            retStr += nowCount.toString() + ",";
            retList[nowCount-1] = true;
        }
        nowCount += 1;
    })
    if(retStr.length>0){
        retStr = retStr.substring(0,retStr.length-1);
    }
    else{
        mui.alert("언어는 한 개 이상 선택 되어야 합니다.");
        return;
    }
    let remLang = "";
    let addLang = "";
    let remPar = "";
    let addPar = "";
    for(let ii = 0; ii<4; ii++){
        if(nowLangApply[ii] && !(retList[ii])){
            remLang += ["한국어","영어","일본어","중국어"][ii] + ",";
            remPar += ii + ",";
        }
        if(!(nowLangApply[ii]) && retList[ii]){
            addLang += ["한국어","영어","일본어","중국어"][ii] + ",";
            addPar += ii + ",";
        }
    }
    let warningComment = "언어 변경으로 인해 ";
    if(remLang.length>0){
        warningComment += remLang.substring(0,remLang.length-1) + " 언어 답변과 의도가 "
        remPar = remPar.substring(remPar.length-1);
        if(addLang.length>0){
            warningComment += "사라지며 ";
        }
        else{
            warningComment += "사라집니다.";
        }
    }
    if(addLang.length>0){
        warningComment += addLang.substring(0,addLang.length-1) + " 언어 답변과 의도가 새롭게 추가됩니다."
        addPar = addPar.substring(addPar.length-1);
    }
    // if(!(confirm(warningComment))){
    //     return;
    // }



    var obj = new Object();
    obj.host = hostToPage;
    obj.lang = retStr;
    obj.remPar = remPar;
    obj.addPar = addPar;
    addAjax();
    $.ajax({
        url : "changeAccountLang",
        data : JSON.stringify(obj),
        type: "POST",
        contentType: 'application/json'
    }).success(
        function(result) {
            finAjax();
            checkHostName();
        }).fail(function(result) {
        finAjax();
        console.log("error : " + result);
    });

}

function checkHostName(){
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
    obj.nowNum = hostToPage;
    obj.hostName = $(".setting > div > dl:nth-child(4) > dd > input").val();
    addAjax();
    $.ajax({
        url : "checkHostname",
        data : JSON.stringify(obj),
        type: "POST",
        contentType: 'application/json'
    }).success(
        function(result) {
            finAjax();
            // console.log(result);
            if(result.toString()!="0"){
                mui.alert("중복된 이름의 챗봇 ID가 존재합니다.")
            }else{
                console.log("createStyle")
                createStyle();
            }
        }).fail(function(result) {
        finAjax();
        console.log("error : " + result);
    });
}

$(document).ready(function () {
    var host = $('#chatListUl>li.active').val();
    if(hostToPage==-1 || hostToPage==undefined || hostToPage==NaN){chatClick(host,0);}
    else{chatClick(hostToPage,0);}
})


function handleChatBuilderImageDelete(e) {
    let target = e.target;
    let $parent = $(target).parents('.cb_img_delete');
    let btnDelete = $parent.find('.btn_icon.delete');
    btnDelete.addClass('active');
    target.onmouseout = function() {
        btnDelete.removeClass('active');
    };
}

function resetSrc(logoBool, thisPointer){
    if(logoBool){
        $(".setting > div > dl:nth-child(9) > dd > div.iptBox > input.ipt_txt").val("기본이미지");
        chatbotLogo = './setImage/-1/logoImg';
        logoReset = true;
        setCustom();
    }else{
        $(".setting > div > dl:nth-child(10) > dd > div.iptBox > input.ipt_txt").val("기본이미지");
        chatbotIcon = './setImage/-1/IconImg';
        iconReset = true;
        setCustom();
    }
}

function selectDefualtLang(){
  var host = $('#chatListUl>li.active').val();
  var copyText = $(".copy_text").text().split('?lang=')[0];
  copyText = copyText.length > 1 ? copyText.split('"></script>')[0] : copyText;

  var defualtLang = $('select[name=defualtLang]').val()==""?"":"?lang="+$('select[name=defualtLang]').val();
  $(".copy_text").text(copyText+defualtLang+'"></script>');
}