var data = {userId: null, companyId: null, keyword: '', name: '', lang: -1, host: -1, simplebotId: 587};
// stg용 simplebotId : 5
// 운영서버용 simplebotId : 587
var intentData = {}; //dropdown : name, regex, nqa
var menuNow = ''; //현재메뉴
var menuPrev = ''; //이전메뉴
var scenario;
var saveCheck = false;
var appliedAt = '';
var taskNode;
// var env = '${env}';
//var m2uUrl = '${m2uUrl}';
var env;
var m2uUrl;
var m2uToken;
let m2uDeviceId;
let m2uChatbot = 'SIMPLEBOT';
var sdsStartChat = '처음으로';
var custTestData;

var testerContractNo;
var testerTelNo;
var checkWaitingNo;

var socket = null;
var isSocketConnected = false;

//data
var intentTableData = [];

var chatSessionId;

var nowDebug = [
    []
]
var nowDebugPK = [];
var nowIndexDebug = 0;
var nowResetClicked = false;
var firstUtter;

var prevTaskTest = null;
var nowPrevDebug = [
    []
]
var nowPrevDebugPK = [];
var nowPrevIndexDebug = 0;

var getIntentTxt = "";
var firstIntent = "";

var PAGES = {
    SCENARIO : {
        title: '시나리오',
        content: 'Scenario',
        active: 'scenario',
        render: renderScenarioPage,
    },
}

var selectScenario = null; //시나리오 목록

$(window).scroll(function(){
    var scrollTop = $(window).scrollTop();

    $('.vb_test.data_debug').css({
        'transform': 'translateY(' + scrollTop + 'px)',
    });
});


$(document).ready(function(){
    if (window.navigator.userAgent.match(/MSIE|Internet Explorer|Trident/i)) {
        mui.alert('음성봇 빌더는 크롬(Chrome) 브라우저에 최적화 되어있습니다.<br> 크롬 브라우저에서 음성봇빌더를 만나보세요!', '권장 브라우저 안내');
    }
    debugTestResizeArea();
    handleTabMenu();
    $('#testerPhone').val('');
    //엑셀 업로드 파일 라벨 표시
    $('#excel_file_v2').change(function (e) {
        var fileName = e.target.files[0].name;
        $('input[id="excel_file_v2_label"]').val(fileName);
    });

    //학습문장 엑셀 업로드 파일 라벨 표시
    $('#excel_file_nqa').change(function (e) {
        var fileName = e.target.files[0].name;
        $('input[id="excel_file_nqa_label"]').val(fileName);
    });
    env = $("#env").val();
    console.log(env)
    m2uUrl = $("#m2uUrl").val();
    //챗봇 테스트 Start 버튼 클릭
    $(".btn_delete_blind").on('click', function(){
        $(this).parent().remove();
        $("#sendTxt").attr('disabled', false);
        $("#sendBtn").attr('disabled', false);
    });

    //챗봇 채팅 전송  (text 출력)
    $('#sendBtn').on('click', function() {
        var inputArea = $('.ipt_box .ipt_txt');
        // textarea 텍스트 값 및 엔터처리
        var textValue = inputArea.val().replace(/(?:\r\n|\r|\n)/g, '<br>');
        // 채팅창에 text 출력
        if( inputArea.val().replace(/\s/g,"").length === 0){
            // text가 없으면 실행
        } else {
            // text가 있으면 실행
            inputArea.val('');
            createMsg('chatbot', 'user', textValue);
            m2uTalk(textValue).then( result => {

                createMsg('chatbot', 'bot', result.answer);

                if(getIntentTxt == "종료"){
                    let botUl = $(".chat_talk");
                    var ul = botUl[0];

                    let li = document.createElement("li");
                    li.setAttribute("class", "system_entry end_system_entry");

                    let em_txt = document.createElement("em");
                    em_txt.innerHTML = "채팅이 종료되었습니다.";

                    li.appendChild(em_txt);
                    ul.appendChild(li);

                    $("#sendTxt").attr('disabled', true);
                    $("#sendBtn").attr('disabled', true);

                    botUl.scrollTop(botUl[0].scrollHeight);
                }

            });
        }
    });

    //챗봇 채팅 입력 : Shift + Enter
    $('.ipt_box .ipt_txt').keyup(function (event) {
        if (event.keyCode == 13 && event.shiftKey) {
            var chatTxt = this.value;
            // this.value = chatTxt.substring(0,caret)+"\n"+chatTxt.substring(carent,chatTxt.length-1);
            event.stopPropagation();

            // 채팅 입력 : Enter
        } else if (event.keyCode == 13){
            $('#sendBtn').trigger('click');
        }
    });

    //챗봇 재시작 버튼 클릭 이벤트
    $('.chat_talk_refresh').on('click', function () {
        // makeChatBotIframe(hostName, host, lang);
        resetList();
        restartChat();
    });
    $('#testChatRefresh').on('click', function () {
        // makeChatBotIframe(hostName, host, lang);
        resetList();
        restartChat();
    });
    // 배포 상황아닌 경우
    if (env != 'prod') {
        // sample data
        data.simplebotId = '5'
    }
    // 시나리오 불러오기
    getSimplebotById(data.simplebotId).then(function(){

        changeContents(PAGES.SCENARIO);
        getCustData(custTestData);
        voiceBotMonitoring();
        setFirstIntent();

        //테스트 음성봇 대화 영역 초기화
        $(".voice_talk").find(".blind").remove();

        var voiceTestHtml = "";
        voiceTestHtml += '<li class="voicebot_start blind">';
        voiceTestHtml += '<p>상단 입력창에서</p>';
        voiceTestHtml += '<p>정보를 입력하신 후</p>';
        voiceTestHtml += '<p>전화 걸기를 눌러주세요.</p>';
        voiceTestHtml += '</li>';

        $(".voice_talk").prepend(voiceTestHtml);
    });
    if(env === 'dev'){
        postMessage({userId: '', companyId: '001', userAuthTy: 'S'}, '*');
    }

});

function getSimplebotById(id) {
    return $.ajax({
        url: "simpleBot/getSimplebotById",
        data: JSON.stringify({simplebotId: Number(id)}),
        type: "POST",
        contentType: 'application/json'
    }).then(function (response) {
        if (!response.scenarioJson) {
            scenario = null;
            return Promise.resolve({isSuccess: true});

        } else {
            console.log(response)
            scenario = JSON.parse(response.scenarioJson);
            data.host = response.host
            data.lang = response.lang
            data.name = response.name
            console.log(scenario)
            appliedAt = response.appliedAt; //최근 업데이트 날짜
            custTestData = JSON.parse(response.testCustData != null ? response.testCustData : "{}");
            console.log(custTestData)
            testerTelNo = response.testTelNo;

            return {isSuccess: true};
        }
    }).catch(function () {
        console.log('[getSimplebotById error]');
        return {isSuccess: false};
        mui.alert('bot id를 가져오는데 실패하였습니다.<br>관리자에게 문의해주세요.')
    });
}

//음성봇 목록 추가하기
function handleAddVoiceBotList(){
    var scenarioName = $('#scenario_name').val();
    var scenarioLang = $('#scenario_lang').val().replace('lang', ''); // lang이 1이면 한국어

    $('#add_scenario_list .vali_warning').remove();

    if (scenarioName === '' || scenarioLang === 'default') {
        var warningTmp = '시나리오명 입력 또는 언어를 선택해주세요.';
        $('#add_scenario_list .dlg').append('<p class="info_small warning vali_warning">'+ warningTmp +'</p>')
        return;
    }

    data.name = scenarioName;
    data.lang = scenarioLang;

    addVoiceBotList();
}

//음성봇 목록 수정하기
function handleModifyVoiceBotList(){
    data.name = $('#mdfy_scenario_name').val();
    $('#set_scenario_list .vali_warning').remove();

    if ( !data.name ) {
        var warningTmp = '수정할 이름을 입력해주세요.';
        $('#set_scenario_list .dlg').append('<p class="info_small warning vali_warning">'+ warningTmp +'</p>')
        return;
    }

    modifyVoiceBotList().then(function(){
        var host = data.host === -1 ? 'undefined' : data.host;
        var id = host + '_' + data.simplebotId + '_' + data.lang;
        var lang = data.lang === 1 ? '한국어' : '영어';
        var label = data.name + ' (' + lang + ')';

        $('#' + id).attr('data-label', label);
        $('label[for="'+id+'"]').text(label);
        $('#scenario_list').val(label);

        modalClose($('#set_scenario_list'));
    });
}

//음성봇 목록 삭제하기
function handleDeleteVoiceBotList(){
    mui.confirm('현재 시나리오를 삭제하시겠습니까?', {
        onClose: function(isOk) {
            if (isOk) {
                deleteVoiceBotList();
            }
        }
    });
}

//-----------------------------------------------------ajax
//ajax 음성봇 목록 get
function getVoiceBotList(e){
    if ( e !== undefined ) {
        selectScenario.clearOptions();
        data.userId = e.data.userId;
        data.companyId = e.data.companyId;
        data.userAuthTy = e.data.userAuthTy;
    }

    $('#vb_wrap').addClass('loading');

    return $.ajax({
        url: "voiceBot/getSimpleBotList",
        data: JSON.stringify(data),
        type: "POST",
        contentType: "application/json"
    }).then(function(res) {
        var sceanList = [];

        for (let key of res) {
            var lang = key["lang"] === 1 ? '한국어' : '영어';
            sceanList.push({'label': key["name"] + ' (' + lang + ')', 'value': key["host"] + '_' + key["id"] + '_' + key["lang"]});
        }

        selectScenario.clearOptions(); //목록 지우기
        selectScenario.setOptions(sceanList); //목록 세팅하기

        data.keyword = ''; // 검색한 text 초기화

        $('#vb_wrap').removeClass('loading');
    }).catch(function(){
        console.log('getVoiceBotList error')
    });
}

//ajax 음성봇 목록 add
function addVoiceBotList() {
    $('#vb_wrap').addClass('loading');

    return $.ajax({
        url: "voiceBot/addScenario",
        data: JSON.stringify(data),
        type: "POST",
        contentType: "application/json"
    }).then(function(res) {
        if (res === -1) {
            var warningTmp = '이미 사용하고 있는 이름입니다. 다시 시도해주세요.';
            $('#add_scenario_list .dlg').append('<p class="info_small warning vali_warning">'+ warningTmp +'</p>')
        } else {
            data.host = -1;
            data.simplebotId = res;

            getVoiceBotList().then(function(){
                var id = 'undefined_' + data.simplebotId + '_' + data.lang;
                var list = [];
                list.push(id);
                selectScenario.setValue(list);
                modalClose($('#add_scenario_list'));
            });
        }

        $('#vb_wrap').removeClass('loading');
    }).catch(function(){
        console.log('addVoiceBotList error')
    });
}

//ajax 음성봇 목록 delete
function deleteVoiceBotList() {
    $('#vb_wrap').addClass('loading');

    // delCacheByHost 란?
    if ( data.host === -1 ) { //호스트가 없으면 SIMPLEBOT 테이블만 삭제
        $.ajax({
            url: "voiceBot/deleteBySimplebotIdV2",
            data: JSON.stringify({
                simplebotId:Number(data.simplebotId),
            }),
            type: "POST",
            contentType: "application/json"
        }).then(function() {
            data.host = -1;
            data.simplebotId = -1;

            getVoiceBotList();
            renderCommonPage();
            modalClose($('#set_scenario_list'));

            $('#vb_wrap').removeClass('loading');
            mui.alert('시나리오를 삭제하였습니다.');

        }).catch(function(){
            console.log('deleteVoiceBotList deleteBySimplebotIdV2 error')
            mui.alert('시나리오 삭제를 실패하였습니다.<br>관리자에게 문의해주세요.')
            $('#vb_wrap').removeClass('loading');
        });
        return;
    }

    $.ajax({ //호스트가 있으면 SIMPLEBOT 테이블 +a 삭제
        url: "voiceBot/deleteByHostV2",
        data: JSON.stringify({
            host:Number(data.host),
            simplebotId:Number(data.simplebotId),
            lang: data.lang,
        }),
        type: "POST",
        contentType: "application/json"
    }).then(function() {
        data.host = -1;
        data.simplebotId = -1;

        getVoiceBotList();
        renderCommonPage();
        modalClose($('#set_scenario_list'));

        $('#vb_wrap').removeClass('loading');
        mui.alert('시나리오를 삭제하였습니다.');

    }).catch(function(){
        console.log('deleteVoiceBotList deleteByHostV2 error')
        mui.alert('시나리오 삭제를 실패하였습니다.<br>관리자에게 문의해주세요.')
        $('#vb_wrap').removeClass('loading');
    });
}

//ajax 음성봇 이름 modify
function modifyVoiceBotList(){
    $('#vb_wrap').addClass('loading');

    return $.ajax({
        url: "voiceBot/modifyScenario",
        data: JSON.stringify(data),
        type: "POST",
        contentType: "application/json"
    }).then(function(res) {
        if ( res === -1 ) {
            var warningTmp = '이미 사용하고 있는 이름입니다. 다시 시도해주세요.';
            $('#set_scenario_list .dlg').append('<p class="info_small warning vali_warning">'+ warningTmp +'</p>')
        }

        $('#vb_wrap').removeClass('loading');
    }).catch(function(){
        console.log('modifyVoiceBotList error')
    });
}

//ajax 메뉴 화면 불러오기
function changeContents(option) {
    var gnb = $('#vb_gnb ul li');
    var $container = $('#vb_container');
    var $content = $('#vb_content');
    if ( saveCheck ) {
        mui.confirm('작성하던 내용이 저장되지 않습니다. 계속 하시겠습니까?', {
            onClose: function(isOk){
                if(isOk){
                    saveCheck = false;
                    changeContinue();
                }
            }
        });
        return;
    }

    if (option.content === 'IntentionAdd' && !scenario ) { //의도 추가 화면
        mui.alert('시나리오를 엑셀 업로드하거나,<br>태스크를 추가한 후 의도를 생성할 수 있습니다.');
        return;
    }

    if (option.content === 'ReplaceDic' && !scenario ) { //치환 사전 화면
        mui.alert('시나리오를 엑셀 업로드하거나,<br>태스크를 추가한 후 치환사전을 사용할 수 있습니다.');
        return;
    }

    changeContinue();

    function changeContinue() {
        $('#vb_wrap').addClass('loading');

        $.ajax({
            url: "maumai" + option.content,
            type: "POST",
            contentType: 'application/json'
        }).then(function (result) {

            menuPrev = menuNow; //이전페이지
            menuNow = option; //현재페이지

            $('head title').text(option.title);
            $container.removeClass('data_debug only_debug');

            if ( option.active ) {
                gnb.find('button').removeClass('active');
                gnb.find('button[data-key="'+option.active+'"]').addClass('active');
            }
            option.render();
            $content.empty();
            $content.append(result);

            $('#vb_wrap').removeClass('loading');
        });
    }
}

// 의도 상세 연결
function moveIntentDetail(host, bertIntentName, dropType) { //dropType : name, regex, nqa
    intentData.dropdown = dropType;
    intentData.bertIntentName = bertIntentName;

    return $.ajax({
        url : "voiceBot/getIntentNo",
        data : JSON.stringify({
            host : host,
            bertIntentName : bertIntentName,
        }),
        type: "POST",
        contentType: 'application/json'
    }).then(function(res) {
        changeContents(PAGES.INTENTION_DETAIL);
        intentData.No = res;

        for(val in intentTableData){
            if(intentTableData[val].No == res){
                intentData.answerId = intentTableData[val].answerId;
            }
        }

    }).catch(function() {
        mui.alert('저장을 먼저 진행해주세요.');
        console.log('[moveIntentDetail error]');
    });
}
//-----------------------------------------------------//ajax

//-----------------------------------------------------render page
function renderCommonPage() {
    $('#vb_header .excel_btns').show();
    $('#vb_container').removeClass('test_on');

    // 버튼 disabled
    $('#vb_header .setting').attr('disabled', ''); //헤더 셋팅 버튼
    $('#vb_gnb ul button').attr('disabled', ''); //헤더 시나리오, 의도, task 버튼
    $('#test_on').show(); //테스트 버튼

    //시나리오 선택 목록 초기화
    selectScenario.clearValue();
    // content 초기화
    $('#vb_content').empty().append('<p class="text_bg">시나리오를 선택해주세요</p>');
}
function renderScenarioPage(){
    $('#vb_header .excel_btns').show();
    $('#vb_container').addClass('data_debug');
    $('#go_back').removeClass('active');
    handleTestOpenClose();
}
function renderIntentionPage(){
    $('#vb_header .excel_btns').show();
    $('#vb_container').addClass('only_debug');
    $('#go_back').removeClass('active');
    handleTestOpenClose();
    settingTestPrevTask();
}
function renderTaskPage(){
    $('#vb_header .excel_btns').show();
    $('#vb_container').addClass('only_debug');
    $('#go_back').removeClass('active');
    handleTestOpenClose();
    settingTestPrevTask();
}
function renderIntentionDetailPage(){
    $('#vb_header .excel_btns').hide();
    $('#go_back').addClass('active');
    $('#test_on').hide();
}
function renderIntentionAddPage(){
    $('#vb_header .excel_btns').hide();
    $('#go_back').addClass('active');
    $('#test_on').hide();
    intentData.No = null;
    intentData.bertIntentName = '';
    intentData.dropdown = '';
}
function renderTaskDetailPage(){
    $('#vb_wrap').addClass('loading');
    $('#vb_header .excel_btns').hide();
    $('#go_back').addClass('active');
    $('#test_on').hide();
}
function renderTaskAddPage(){
    $('#vb_header .excel_btns').hide();
    $('#go_back').addClass('active');
    $('#test_on').hide();
}
function renderReplacePage(){
    $('#vb_header .excel_btns').hide();
    $('#go_back').removeClass('active');
    $('#test_on').hide();
}
function renderIntentOrderPage(){
    $('#vb_header .excel_btns').hide();
    $('#vb_container').addClass('only_debug');
    $('#go_back').addClass('active');
    handleTestOpenClose();
    settingTestPrevTask();
}
//-----------------------------------------------------//render page


// 모달 열기 닫기
function handleModal() {
    var dataModal = $(event.target).attr('data-modal');
    var $modal = $('#' + dataModal);
    var $dialog = $modal.children();
    var $closeBtns = $modal.find('.btn_modal_close');
    $('body').css('overflow', 'hidden');
    $modal.show();
    $dialog.addClass('active');

    if (dataModal === 'set_scenario_list') {
        var val = data.lang === 1 ? $('#scenario_list').val().replace(' (한국어)', '') : $('#scenario_list').val().replace(' (영어)', '');
        $('#set_scenario').val(val); //시나리오 관리 모달 현재 시나리오명 표시
    }

    $closeBtns.on('click', function(){
        modalClose($modal);
    });
}

function modalClose(el) {
    if ( el.hasClass('vb_modal') ) {
        $('body').css('overflow', 'visible');
    }
    el.hide();
    el.find('.vb_modal_dialog').removeClass('active');

    //input 초기화
    el.find('input[type="text"], input[type="tel"], input[type="email"], input[type="file"], textarea').val('');
    el.find('input[type="checkbox"], input[type="radio"]').prop('checked', false);
    el.find('select').find('option').prop('selected', false);

    //validation 체크용 경고문구 제거
    el.find('.vali_warning').remove();

    //task Group 관리 수정 depth명 disabled 처리
    el.find('#selectDepthGroup').prop('disabled', true);
}

// 테스트 버튼 열기
function openTest() {
    $('#vb_container').addClass('test_on');
    $('button#test_on, .btn_test_on').hide();
}

// 테스트 버튼 닫기
function closeTest() {
    $('#vb_container').removeClass('test_on');
    $('button#test_on, .btn_test_on').show();
}

function handleTestOpenClose() {
    var container = $('#vb_container');
    if (container.hasClass('test_on')) {
        $('button#test_on, .btn_test_on').hide();
    } else {
        $('button#test_on, .btn_test_on').show();
    }
}

// 탭메뉴 열기 닫기
function handleTabMenu() {
    var tabGroup = '';
    var tabIndex = 1;

    $('.vb_tab').each(function(){
        tabGroup = $(this);
        closeTab();
        openTab(tabIndex);
    });

    $('.vb_tab_menu button').on('click', function(){
        tabGroup = $(this).parents('.vb_tab');
        tabIndex = tabGroup.find('.vb_tab_menu button').index($(this));

        closeTab();
        openTab(tabIndex);
    });

    function openTab(index){
        tabGroup.find('.vb_tab_menu button').eq(index).addClass('active');
        tabGroup.find('.vb_tab_cont').eq(index).addClass('active');
    }

    function closeTab() {
        tabGroup.find('.vb_tab_menu button').removeClass('active');
        tabGroup.find('.vb_tab_cont').removeClass('active');
    }
}

//엑셀 업로드
function uploadV2AndSave() {
    var file = $("#excel_file_v2").val();
    if (file === "" || file === null) {
        mui.alert($('input[name="uploadExcelV2NoFile"]').val());
        return;
    }

    var options = {
        success: function (res) {
            if (res.hasOwnProperty("scenarioJson")) {
                saveCheck = false;
                getSimplebotById(data.simplebotId).then(function(result){
                    if ( !result.isSuccess ) return;

                    saveUploadScenario().then(function(result) {
                        if ( !result.isSuccess ) return;

                        if ( menuNow.content === 'Intention' ) {
                            intentTableData = [];
                        }

                        changeContents(menuNow);
                        getCustData(custTestData);
                        voiceBotMonitoring();
                        $('#vb_header .excel_btns .btn_secondary').removeAttr('disabled'); //헤더 excel 모든 버튼 활성화
                        $('#test_on').removeAttr('disabled');
                    });
                });

            } else if (res.hasOwnProperty("checkTask")) { //태스크 체크 필요
                mui.alert("시나리오 c열에 아래 태스크가 존재하는지 확인해주세요.<br>- 확인이 필요한 태스크 :<br>" + res.checkTask);
                $('#excel_file_v2').attr('type', 'reset');
                $('input[id="excel_file_v2_label"]').val('선택된 파일 없음');
                $('#excel_file_v2').attr('type', 'file');
            }else if(res.hasOwnProperty("checkRegexIntent")){
                mui.alert("정규식 a열에 아래 의도가 매핑되어 있습니다.<br>시나리오 e열에 아래 의도가 존재하는지 확인해주세요.<br>- 확인이 필요한 의도 :<br>" + res.checkRegexIntent);
                $('#excel_file_v2').attr('type', 'reset');
                $('input[id="excel_file_v2_label"]').val('선택된 파일 없음');
                $('#excel_file_v2').attr('type', 'file');
            }

        }, error: function () {
            $('#vb_wrap').removeClass('loading');
            mui.alert($('input[name="uploadExcelV2Error"]').val());
            console.log('[uploadV2AndSave error]')
        },
        type: "POST",
    };

    $("#excelUploadScenarioForm input[name='simplebotId']").val(data.simplebotId);
    $("#excelUploadScenarioForm").ajaxSubmit(options); //시나리오제이슨 저장

    function saveUploadScenario() {
        $('#upload_excel').addClass('loading');

        return $.ajax({
            url: "voiceBot/saveScenario",
            data: JSON.stringify({
                simplebotId: data.simplebotId,
                userId: data.userId,
                companyId: data.companyId,
                scenarioJson: JSON.stringify(scenario),
                isExcelUpload: 'Y',
            }),
            type: "POST",
            contentType: 'application/json'

        }).then(function (res) {
            data.host = res.host;

            $('#upload_excel').removeClass('loading');
            mui.alert($('input[name="saveScenarioSuccess"]').val());
            modalClose($('#upload_excel'));

            return Promise.resolve({ isSuccess: true });

        }).catch(function () {
            $('#vb_wrap').removeClass('loading');
            mui.alert($('input[name="saveScenarioError"]').val());
            console.log('[uploadV2AndSave saveScenario error]')
            return { isSuccess: false };
        });
    }
}

//엑셀 샘플 다운로드
function downloadFile() {
    var form = document.downloadForm;
    form.submit();
}

//엑셀 다운로드
function scenarioDownloadFile() {
    var form = document.downloadScenario;
    // scenario = getScenarioJsonData(); //jsplumb에서 가져옴

    $("#scenarioHost").val(data.host);
    $("#scenarioJson").val(JSON.stringify(scenario));
    form.submit();
}

//taskDetail
function getTaskNode(id){
    var nodes = scenario.nodes;

    for (var i = 0; i < nodes.length; i++) {
        if(nodes[i].id == id){
            taskNode = nodes[i];
        }
    }
}

//테스트 고객 데이터 정보 불러오기
function getCustData(custData){
    $("#testDataList").empty();
    var testDataHtml = ""
    var idx = 0;
    if(Object.keys(custData).length > 0){
        for(var key in custData){
            idx++;
            testDataHtml += '<li>';
            testDataHtml += '<div class="li_col">';
            testDataHtml += '<input type="checkbox" name="intent_task[]" class="checkbox" id="list_'+idx+'">';
            testDataHtml += '<label for="list_'+idx+'"></label>';
            testDataHtml += '</div>';
            testDataHtml += '<div class="li_col">';
            testDataHtml += '<input type="text" id="custDataKey_'+idx+'"class="ipt_txt" placeholder="이름" value="'+key+'">';
            testDataHtml += '</div>';
            testDataHtml += '<div class="li_col">';
            testDataHtml += '<input type="text" id="custDataValue_'+idx+'"class="ipt_txt" placeholder="이름" value="'+custData[key]+'">';
            testDataHtml += '</div>';
            testDataHtml += '</li>';
        }
    }else{
        testDataHtml += '<li class="no_list">등록된 테스트 데이터가 없습니다</li>';
    }
    $("#testDataList").append(testDataHtml);
}

// 테스트 CustData 테스트 데이터 추가
function addCustRow(){
    var testDataLastId = $("#testDataList").find('.checkbox');
    var idx = 0;
    if(testDataLastId.length > 0){
        idx = Number(testDataLastId[testDataLastId.length-1].getAttribute('id').split("_")[1]) + 1;
    }else {
        $("#testDataList").empty();
    }

    var testDataHtml = "";

    testDataHtml += '<li>';
    testDataHtml += '<div class="li_col">';
    testDataHtml += '<input type="checkbox" name="intent_task[]" class="checkbox" id="list_'+idx+'">';
    testDataHtml += '<label for="list_'+idx+'"></label>';
    testDataHtml += '</div>';
    testDataHtml += '<div class="li_col">';
    testDataHtml += '<input type="text" id="custDataKey_'+idx+'"class="ipt_txt" placeholder="이름">';
    testDataHtml += '</div>';
    testDataHtml += '<div class="li_col">';
    testDataHtml += '<input type="text" id="custDataValue_'+idx+'"class="ipt_txt" placeholder="홍길동">';
    testDataHtml += '</div>';
    testDataHtml += '</li>';

    $("#testDataList").append(testDataHtml);

}
// 테스트 CustData 선택 삭제
function deleteCustRow(){
    $('input[name="intent_task[]"]:checked').each(function(){
        $(this).parent().parent().remove();
    });
    var testDataLastId = $("#testDataList").find('.checkbox');
    if(testDataLastId.length == 0){
        var testDataHtml = ""
        testDataHtml += '<li class="no_list">등록된 테스트 데이터가 없습니다</li>';
        $("#testDataList").append(testDataHtml);
    }
    saveTestCustData();
}
// 테스트 CustData 저장 함수
function saveTestCustData(){
    var custData = {};

    var checkList = document.getElementsByName("intent_task[]");

    for (let i = 0; i < checkList.length; i++) {
        var keyInput = $(checkList[i]).parent().parent().find('input[id*="custDataKey_"]').val();
        var valueInput = $(checkList[i]).parent().parent().find('input[id*="custDataValue_"]').val();
        var key = keyInput;
        var val = valueInput;
        if (key) {
            custData[key] = val;
        }
    }
    var custDataStr = JSON.stringify(custData);

    $.ajax({
        url: "voiceBot/saveCustTestData",
        data: JSON.stringify({simplebotId: data.simplebotId, custData: custDataStr}),
        type: "POST",
        contentType: 'application/json'
    }).then(function (response) {
        console.debug('saveTestCustData done');
//        resolve();

    }).catch(function (response) {
        console.debug('saveTestCustData fail!!');
//        resolve();
    });
}

function voiceBotMonitoring() {
    var host = data.host;
    var userId = data.userId;

    if (host === undefined || host === '' || host === 0
        || userId === undefined || userId === '') {
        return;
    }

    isSocketConnected = true;
    if(socket) {
        socket.disconnect();
    }
    socket = io.connect('wss://fast-aicc-dev.maum.ai:51000', {'force new connection': true});

    // 웹에서 계속 연결 시도하기 때문에 close
    setTimeout(function() {
        if (!socket.connected) {
            socket.close();
            isSocketConnected = false;
            socket = null;
            console.log('socket connection failed');
        }
    }, 3000);

    socket.on('connection', function(data) {
        console.log('connect host: [' + host + ']');
        if(data.type === 'connected') {
            socket.emit('connection', {
                type : 'join',
                name : userId,
                room : host
            });
        }
    });

    // 음성봇 socket msg
    socket.on('message', function(data) {

        console.log('[socket] type: ' + data.type + ', intent: ' + data.intent + ', message: ' + data.message);
        if (data.message === sdsStartChat || data.message.charAt(0) === '#') {
            return;
        }
        createMsg('voicebot', data.type, data.message);

        if(data.intent != "" && data.intent != null){
            var intent = data.intent;

            if(intent == "처음으로"){
                intent = scenario.nodes[0].label;
                flowNode(intent);
            }else {
                flowNode(intent);
            }
        }
        // if(data.type == 'bot' && data.intent == '처음으로'){
        //   firstUtter = data.message;
        //   createVoiceDebug(null, data.intent);
        // }

        if(data.type == 'user' && data.message != '처음으로'){
            createVoiceDebug(data.session, data.message);
        }
//      highliteNode(data.intent);
    });
}


/*Chatting UI Func Start*/
function createMsg(botType='chatbot', userType='user', msg, nowTime) {
    console.log('createMsg');

    // let botDiv;
    let botUl;
    // botType 체크
    if (botType === 'chatbot') {
        botUl = $('.chat_talk');
    } else if (botType === 'voicebot') {
        botUl = $('.voice_talk');
    } else {
        console.log('[createMsg] wrong botType: ' + botType);
        return;
    }

    // userType 체크
    if (userType !== 'user' && userType !=='bot' && userType !=='system') {
        console.log('[createMsg] wrong userType: ' + userType);
        return;
    }

    // 아직 system용 style이 없으니 bot으로 처리
    if (userType == 'system') {
        userType = 'bot';
    }

    // nowTime 체크
    if (!nowTime) {
        nowTime = getNowTime();
    }

    msg = msg.replace(/\|/gi, "");
    var ul = botUl[0];
    let li = document.createElement("li");
    li.setAttribute("class", userType);
    let dv_msg = document.createElement("div");
    let dv_date = document.createElement("div");
    dv_msg.setAttribute("class", "message");
    dv_date.setAttribute("class", "time");
    dv_msg.innerHTML = msg;
    dv_date.innerText = nowTime;

    if(botType === "chatbot"){
        if(msg != ""){
            li.appendChild(dv_msg);
            li.appendChild(dv_date);
            ul.appendChild(li);
        }
    }else if(botType === "voicebot"){
        if(msg != ">>>>> 통화 종료"){
            li.appendChild(dv_msg);
            li.appendChild(dv_date);
            ul.appendChild(li);
        }else {

            li.setAttribute("class", "system_entry end_system_entry");
            let em_txt = document.createElement("em");
            em_txt.innerHTML = "전화가 종료되었습니다.";

            li.appendChild(em_txt);
            ul.appendChild(li);
        }
    }



    botUl.scrollTop(botUl[0].scrollHeight);
//  let botMid = botDiv.find('.chatUI_mid');
//  botMid.scrollTop(botMid[0].scrollHeight);
}

//시뮬레이션 시 node UI highlite
//function highliteNode(nodeLabel) {
//  if (!nodeLabel || nodeLabel == '') {
//    return;
//  }
//  clearNodeEffect();
//
//  if (nodeLabel === sdsStartChat) {
//    $('.jtk-node:first-child').addClass('gradient');
//    return;
//  }
//
//  let nodes = $('div.scenario div.jtk-node');
//  for (let i = 0; i < nodes.length; i++) {
//    let node = nodes[i];
//    if (node.innerHTML.trim() === nodeLabel) {
//      node.classList.add('gradient');
//    }
//  }
//}

// Node 효과 초기화
//function clearNodeEffect() {
//    $('div.scenario div.jtk-node').removeClass('gradient');
//}

function clearVoiceBotWindow() {
    // 음성봇 테스트 창 초기화
    let voicebotCover = $(".voicebot_start").find('p');
    $('.voicebot_start').removeClass('blind');
    voicebotCover.remove();

    $('.bot').remove();
    $('.user').remove();
    $('.end_system_entry').remove();
}

/*M2U Client Function Start*/

function randomString() {
    const chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz';
    const string_length = 15;
    let randomstring = '';
    for (let i = 0; i < string_length; i++) {
        let rnum = Math.floor(Math.random() * chars.length);
        randomstring += chars.substring(rnum, rnum + 1);
    }
    return randomstring;
}

function m2uSignIn() {
    let signInParam = {
        "userKey": "admin",
        "passphrase": "1234"
    };

    let ajPromise = new Promise((resolve, reject) =>
    {
        $.ajax({
            url: m2uUrl + "/api/v3/auth/signIn",
            data: JSON.stringify(signInParam),
            type: "POST",
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Content-type", "application/json");
                xhr.setRequestHeader("m2u-auth-internal", "m2u-auth-internal");
            },
        }).done(function (response) {
            console.log('m2uSignIn done!!');
            console.log(typeof response);
            response = JSON.parse(response);

            if (response.directive.payload.hasOwnProperty('authSuccess')) {
                let token = response.directive.payload.authSuccess.authToken;
                resolve(token);
            } else {
                reject('[ERROR] Cannot connect server! : SignIn auth Fail');
            }
        }).fail(function (response) {
            console.log('m2uSignIn fail!!');
            console.log(response);
            reject('[ERROR] Cannot connect server! : SignIn ajax Fail');
        });
    });

    return ajPromise;
}
function m2uOpen(msg) {
    // open에서 m2uLang이 중요하지 x
    let m2uLang;
    if (data.lang == 1) {
        m2uLang = "ko_KR";
    } else if (data.lang == 2) {
        m2uLang = "en_US"
    }

    m2uDeviceId = 'WEB_' + randomString();

    let openParam = {
        'payload': {
            'utter': msg,
            'lang': m2uLang,
            'chatbot': m2uChatbot,
            'meta': {
                'debug': true,
                'simplebot.id' : data.simplebotId.toString()
            }
        },
        'device': {
            'id': m2uDeviceId,
            'type': 'WEB',
            'version': '0.1',
            'channel': 'FAST-SIMPLEBOT'
        },
        'location': {
            'latitude': 10.3,
            'longitude': 20.5,
            'location': 'mindslab'
        },
        'authToken': m2uToken
    };

    let ajPromise = new Promise((resolve, reject) => {
        $.ajax({
            url: m2uUrl + "/api/v3/dialog/open",
            data: JSON.stringify(openParam),
            type: "POST",
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Content-type", "application/json");
                xhr.setRequestHeader("m2u-auth-internal", "m2u-auth-internal");
            },
        }).done(function (response) {
            console.log('m2uOpen done!!');
            // console.log(response);
            response = JSON.parse(response);
            let answer;
            if (response.hasOwnProperty('exception')) {
                const exception = response.exception;
                answer = '[ERROR] ' + exception.statusCode + '\nmessage : ' + exception.exMessage;
                reject(answer);
            } else {
                let meta = response.directive.payload.response;
                answer = response.directive.payload.response.speech.utter;
//          chatSessionId = meta.meta.sessionid;
                firstUtter = response.directive.payload.response.speech.utter;
            }
            resolve(answer);
            flowNode(firstIntent);

            getIntentTxt = "처음으로";

        }).fail(function (response) {
            console.log('m2uOpen fail!!');
            console.log(response);
            reject('[ERROR] Cannot "Open" Server!');
        });

    });

    return ajPromise;
}

function m2uTalk(msg) {
    let m2uLang;
    if (data.lang == 1) {
        m2uLang = "ko_KR";
    } else if (data.lang == 2) {
        m2uLang = "en_US"
    }

    let textToTextTalkParam = {
        'payload': {
            'utter': msg,
            'lang': m2uLang,
            'meta': {
                debug: true,
                'simplebot.id': data.simplebotId.toString(),
                number: 1.0,
                text: 'true',
                obj: [
                    'complex',
                    {example: 2345}
                ]
            }
        },
        'device': {
            'id': m2uDeviceId,
            'type': 'WEB',
            'version': '0.1',
            'channel': 'FAST-SIMPLEBOT'
        },
        'location': {
            'latitude': 10.3,
            'longitude': 20.5,
            'location': 'mindslab'
        },
        'authToken': m2uToken
    };

    let ajPromise = new Promise((resolve, reject) => {
        $.ajax({
            url: m2uUrl + "/api/v3/dialog/textToTextTalk",
            data: JSON.stringify(textToTextTalkParam),
            type: "POST",
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Content-type", "application/json");
                xhr.setRequestHeader("m2u-auth-internal", "m2u-auth-internal");
            },
        }).done(function (response) {
            console.log('m2uTalk done!!');
            console.log(response);
            response = JSON.parse(response);
            let answer;
            let intent;
            if (response.hasOwnProperty('exception')) {
                const exception = response.exception;
                answer = `errCode : ` + exception.statusCode + `<br/> message : ` + exception.exMessage
            } else {
                response = response.directive.payload.response;
                let meta = response.meta;
                answer = response.speech.utter;
                intent = meta['sds.intent'];
                chatSessionId = meta.sessionid;
            }
            let result = {'answer': answer, 'intent': intent};
            resolve(result);

            getIntentTxt = intent;

            if(intent == ""){
                intent = "종료";
            }

            if(intent != "처음으로"){
                flowNode(intent);
            }else if (intent == "처음으로"){
                flowNode(firstIntent);
            }

        }).fail(function (response) {
            console.log('m2uTalk fail!!');
            console.log(response);

            reject('m2uTalk Fail');
        });
    });

    return ajPromise;
}

function m2uClose(msg) {

    let closeParam = {
        'device': {
            'id': m2uDeviceId,
            'type': 'WEB',
            'version': '0.1',
            'channel': 'FAST-SIMPLEBOT'
        },
        'location': {
            'latitude': 10.3,
            'longitude': 20.5,
            'location': 'mindslab'
        },
        'authToken': m2uToken
    };

    let ajPromise = new Promise((resolve, reject) => {
        $.ajax({
            url: m2uUrl + "/api/v3/dialog/close",
            data: JSON.stringify(closeParam),
            type: "POST",
            beforeSend: function (xhr) {
                xhr.setRequestHeader("Content-type", "application/json");
                xhr.setRequestHeader("m2u-auth-internal", "m2u-auth-internal");
            },
        }).done(function (response) {
            console.log('m2uClose done!!');
            console.log(response);
            resolve();
        }).fail(function (response) {
            console.log('m2uClose fail!!');
            console.log(response);
            reject('m2uClose Fail');
        });

    });

    return ajPromise;
}

//채팅 시작 (m2u open)
function startChat(msg=sdsStartChat) {
    //테스트 Cust Data 저장
    saveTestCustData();

    // node 효과 reset
//    clearNodeEffect();

    // chatbot 창 clear
//    $('#chatbot .lst_talk').empty();

    $(".chat_talk").find(".blind").remove();
    $("#sendTxt").attr('disabled', false);
    $("#sendBtn").attr('disabled', false);

    $(".chat_talk").find('.bot').remove();
    $(".chat_talk").find('.user').remove();

    $(".end_system_entry").remove();

    // call "SignIn" & "Open"
    if (!m2uToken) {
        m2uSignIn().then( token => {
            m2uToken = token;

            m2uOpen(msg).then( answer => {
                createMsg('chatbot', 'bot', answer);
                createChatDebug('처음으로');
//            highliteNode(sdsStartChat);
            }).catch((errMsg) => {
                createMsg('chatbot', 'bot', errMsg)
            });

        }).catch((errMsg) => {
            createMsg('chatbot', 'bot', errMsg);
        });

    } else {

        // call "Open"
        m2uOpen(msg).then( answer => {
            createMsg('chatbot', 'bot', answer);
            createChatDebug('처음으로');
//        highliteNode(sdsStartChat);
        });
    }
}
// 채팅 새로고침
function restartChat(msg=sdsStartChat) {
    // 테스트 고객 데이터 저장
    saveTestCustData();

    // node 효과 reset
//      clearNodeEffect();
    // chatbot 창 clear
//      $('#chatbot .lst_talk').empty();
    $(".chat_talk").find('.bot').remove();
    $(".chat_talk").find('.user').remove();

    $(".end_system_entry").remove();

    $("#sendTxt").attr('disabled', false);
    $("#sendBtn").attr('disabled', false);

    if (!m2uToken) {
        startChat();
        return;
    }

    m2uClose().then( () => {
        m2uOpen(msg).then( answer => {
            createMsg('chatbot', 'bot', answer);
            createChatDebug('처음으로');
//          highliteNode(sdsStartChat);
        });
    });
}

function voiceTestStart(){
    saveTestCustData();

    var testerPhone = $("#testerPhone").val();

    if(testerPhone === '' || testerPhone === null){
        alert("전화번호를 입력해주세요.");
        return;
    }

    setTimeout(function() {
        $('#voice_test_start').removeClass('gradient');
        $('#voice_test_start').text('전화 걸기');
        $('#voice_test_start').attr('disabled', false);
    }, 3000);

    if (testerContractNo && testerTelNo == testerPhone) {
        // node UI css clear
//        clearNodeEffect();
        // voicebot window clear
        clearVoiceBotWindow();

        callStart().then(() => {
            // 1초마다 대기자수를 체크
            checkWaitingNo = setInterval(getWaitingNo, 1000);
        });
    } else {
        getContractNo(testerPhone).then((contractNo) => {
            // node UI css clear
//       clearNodeEffect();
            // voicebot window clear
            clearVoiceBotWindow();

            testerContractNo = contractNo;
            callStart().then(() => {
                // 1초마다 대기자수를 체크
                checkWaitingNo = setInterval(getWaitingNo, 1000);
            });
        });
    }
}

//해당 전화번호의 contractNo 가져오기
function getContractNo(telNo) {

    let ajPromise = new Promise((resolve, reject) => {
        $.ajax({
            url: "simpleBot/getContractNo",
            data: JSON.stringify({simplebotId: data.simplebotId, telNo: telNo}),
            type: "POST",
            contentType: 'application/json'
        }).done(function (response) {
            console.log('getContractNo done!!');
            console.log(response);
            resolve(response);
        }).fail(function (response) {
            console.log('saveTesterInfo fail!!');
            console.log(response);
            reject();
        });
    });

    return ajPromise;
}

// CM 으로 전화걸기
function callStart() {

    let ajPromise = new Promise((resolve, reject) => {
        $.ajax({
            url: "simpleBot/callStart",
            data: JSON.stringify({contractNo: testerContractNo, lang: data.lang, userId: data.userId}),
            type: "POST",
            contentType: 'application/json'
        }).done(function (response) {
            console.log('callStart done!!');
            resetList();
            console.debug(response);
            resolve();
        }).fail(function (response) {
            console.log('callStart fail!!');
            console.debug(response);
            reject();
        });
    });

    return ajPromise;
}

// 수신자 정보 조회
function findTesterInfo() {
    $.ajax({
        url: "simpleBot/getTesterInfo",
        data: JSON.stringify({tester: data.userId, lang: data.lang}),
        type: "POST",
        contentType: 'application/json'
    }).done(function (response) {
        // console.log('findTesterInfo res: ' + response);
        if (response !== "WRONG" && response !== "ERROR" && response['status'] !== "failed") {
            var responseData = response['result'];
            testerContractNo = responseData['contract_no'];
            $("input[name='name']").val(responseData['name']);
            $("input[name='tel']").val(responseData['tel_no']);
        }
    }).fail(function (response) {
        console.log('findTesterInfo fail!!');
        console.log(response);
    });
}

// 대기 인원 수 조회해오기
function getWaitingNo() {
    $.ajax({
        url: "simpleBot/callList",
        data: JSON.stringify({contract_no: testerContractNo, lang: data.lang}),
        type: "POST",
        contentType: 'application/json'
    }).done(function (response) {
        console.log(response !== 0 ? response : 0);
        let voicebotCover = $(".voicebot_start").find('p');
//     if (response['cnt'] === "" || response['cnt'] === undefined || response['cnt'] === "0") {
        if (response === "" || response === undefined || response === 0) {
            // 대기자가 없을 경우
            $(".make_call").children('span').children('em').text("0");
            $("#voicebot").find('.lst_talk').empty();
            $('#voice_test_start').text('전화 걸기').removeClass('gradient');
            $('#voice_test_start').attr('disabled', false);
            clearInterval(checkWaitingNo);
            setTimeout(function() {
                $(".make_call").children('span').css('display', 'none');
            }, 3000);
            if (voicebotCover.length > 0) {
                $('.voicebot_start').removeClass('blind');
                voicebotCover.remove();
            }
        } else {
            // 대기자가 있는 경우
//       $(".make_call").children('span').children('em').text(response['cnt']);
            $(".make_call").children('span').children('em').text(response);
            setTimeout(function() {
                $(".make_call").children('span').css('display', 'block');
            }, 3000);
            if (voicebotCover.length > 0) {
                $(".voicebot_start").find('p').text('대기중입니다.');
            } else {
                let voicebotStart = document.getElementsByClassName('voicebot_start')[0];
                let p_msg = document.createElement("p");
                p_msg.innerHTML = '대기중입니다.';
                voicebotStart.appendChild(p_msg);
            }
        }
        if (socket === null || !socket.connected) {
            if (voicebotCover.length > 0) {
                $(".voicebot_start").children('p').text('현재 이용할 수 없습니다.');
            } else {
                let voicebotStart = document.getElementsByClassName('voicebot_start')[0];
                let p_msg = document.createElement("p");
                p_msg.innerHTML = '현재 이용할 수 없습니다.';
                voicebotStart.appendChild(p_msg);
            }
        }
    }).fail(function (response) {
        console.log('callList fail!!');
        console.log(response);
        alert('대기자 수 조회에 실패하였습니다.');
        $(".make_call").children('span').children('em').text("0");
        $("#voicebot").find('.lst_talk').empty();
        $('#voice_test_start').text('전화 걸기').removeClass('gradient');
        $('#voice_test_start').attr('disabled', false);
        clearInterval(checkWaitingNo);
        if ($(".voicebot_start").find('p').length > 0) {
            $(".voicebot_start").find('p').remove();
        }
    });
}

//2020.7.13 PM 9:45
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

function createVoiceDebug(sessionId, textAnswer){
    console.log("createVoiceDebug");
    var voiceObj = new Object();
    voiceObj.host = data.host;
    voiceObj.lang = data.lang;
    voiceObj.roomSession = sessionId;
    voiceObj.utter = textAnswer.replace("\n","");

    if(sessionId != null){
        $.ajax({
            url: "logDebug",
            data: JSON.stringify(voiceObj),
            type: "POST",
            contentType: 'application/json'
        }).then(function (response) {
            console.log("success logDebug : " + response.logDebugErrLog);

            var debugJson = new Object();
            if(response.logDebugErrLog != undefined){
                debugJson.logDebugErrLog = response.logDebugErrLog;
            }else{
                debugJson.input = JSON.parse(response.debugJson).input;
                debugJson.type = JSON.parse(response.debugJson).type;
                debugJson.engine = JSON.parse(response.debugJson).engine;
                debugJson.model = JSON.parse(response.debugJson).model;
                debugJson.prob = JSON.parse(response.debugJson).prob;
                debugJson.replace = JSON.parse(response.debugJson).replace;
                debugJson.prevIntent = JSON.parse(response.debugJson).prevIntent;
                debugJson.taskRel = JSON.parse(response.debugJson).taskRel;
                debugJson.sdsLog = JSON.parse(response.debugJson).sdsLog;
                debugJson.intent = response.intent;
                debugJson.answer = response.answer;
            }

            addChatbotDebugByJson(JSON.stringify(debugJson));
        }).catch(function (response) {
            console.log("fail logDebug : " + response.logDebugErrLog);
            console.debug('saveTestCustData fail!!');
        });
    }else{
        var firstDebugJson = new Object();
        firstDebugJson.type = "intent";
        firstDebugJson.prob = "1";
        firstDebugJson.intent = "처음으로";
        firstDebugJson.answer = firstUtter;
        addChatbotDebugByJson(JSON.stringify(firstDebugJson));
    }
}

// "처음으로" 태스크 분석
function createChatDebug(textAnswer){
    console.log("chat Log");
    var chatObj = new Object();
    chatObj.roomSession = chatSessionId;
    chatObj.utter = textAnswer;
    chatObj.host = data.host;
    chatObj.lang = data.lang;

    var firstDebugJson = new Object();
    firstDebugJson.type = "intent";
    firstDebugJson.prob = "1";
    firstDebugJson.intent = "처음으로";
    firstDebugJson.answer = firstUtter;
    addChatbotDebugByJson(JSON.stringify(firstDebugJson));
}


function addChatbotDebugByJson(e){
    console.log("ChatbotDebug[By Json]");
    var debugJson = JSON.parse(e);
    try {
        var prob = debugJson.prob;
        if(debugJson.prob != undefined){
            if(debugJson.prob > 1.0 && debugJson.prob < 10.0){
                prob = debugJson.prob * 0.1;
            }else if(debugJson.prob > 10.0 && debugJson.prob < 100.0){
                prob = debugJson.prob * 0.01;
            }else if(debugJson.prob > 100.0 && debugJson.prob < 1000.0){
                prob = debugJson.prob * 0.001;
            }
        }

        if(debugJson.engine == "RegEx"){
            regexCheckTest(debugJson, "Scenario");
        }

        let elem = [
            [0, "INPUT"],
            [1, "input text",
                debugJson.input != undefined ? debugJson.input : ""],
            [1, "type", debugJson.type != undefined ? debugJson.type : ""],
            [2, "replace", []],
            [0, "INTENT"],
            [1, "engine",
                debugJson.engine != undefined ? debugJson.engine : ""],
            [1, "match regex",
                debugJson.matchingRegex != undefined ? debugJson.matchingRegex : ""],
            [1, "model", debugJson.model != undefined ? debugJson.model : ""],
            [1, "prob", prob != undefined ? prob.toString() : ""],
            [0, "TASK"],
            [1, "prev task",
                debugJson.prevIntent != undefined ? debugJson.prevIntent : " "],
            [1, "task rel",
                debugJson.taskRel != undefined ? debugJson.taskRel : " "],
            [1, "task", debugJson.intent != undefined ? debugJson.intent : ""],
            [0, "ANSWER"],
            [1, "text", debugJson.answer != undefined ? debugJson.answer : ""],
            [0, "SDS LOG"],
            [4, debugJson.sdsLog != undefined ? debugJson.sdsLog : ""]
        ];
        if (e.intent === "챗봇공지사항" || e.intent === "선톡") {
            return;
        }

        if (debugJson.logDebugErrLog != undefined) {
            elem.push([3, "WARN", debugJson.logDebugErrLog]);
        }

        if (debugJson.taskRel != undefined) {
            if (debugJson.taskRel.toString().includes("(의도)")) {
                elem.push([3, "WARN",
                    "부합한 태스크 관계가 없습니다. 의도의 이름과 같은 이름의 답변이 연결 되었습니다."])
            }
            if (debugJson.taskRel.toString().includes("(발화)")) {
                elem.push([3, "WARN",
                    "부합한 태스크 관계가 없습니다. 채팅 입력값과 같은 이름의 답변이 연결 되었습니다."])
            }
        }
        if (debugJson.replace != undefined) {
            let debugReplaceList = debugJson.replace.toString().split("<cell>");
            for (let ii = 0; ii < debugReplaceList.length; ii++) {
                if (debugReplaceList[ii].length > 0) {
                    elem[3][2].push(debugReplaceList[ii].split("<split>"));
                }
            }
        }

        if (nowDebug[0].length == 0) {
            nowDebug[0] = elem;
        } else {
            nowDebug.push(elem);
        }
    }catch (e) {

    }
    renderDebugList(-99);
}

function renderDebugList(nowIndex){
    if(nowIndex == -99){
        renderDebugList(nowDebug.length-1);
    }else if (nowIndex == -1 || nowIndex == nowDebug.length) {
    }else
    {
        nowIndexDebug = nowIndex;
        $("#nowDebugIndex").text(nowIndexDebug+1);
        $("#nowDebugStack").text(nowDebug.length)
        let nowElem = nowDebug[nowIndex];
        let nowHTML = "";
        let nowElemStack = "";
        for(let ii = 0; ii<nowElem.length; ii++){
            if(nowElem[ii][0]===0){//카테고리
                nowHTML += (nowElemStack.length>0 ? "<dd>"+nowElemStack+"</dd>" : "") + "<dt>"+nowElem[ii][1]+"</dt>\n";
                nowElemStack = "";
            }else if (nowElem[ii][0]===1){//일반 내용
                if(nowElem[ii][2].length>0){
                    if(nowElem[ii][1] == "model"){
                        nowElemStack += "<p><span class=\"highlight\">"+nowElem[ii][1]+"</span><em id=\"scenarioModelText\">"+nowElem[ii][2]+"</em></p>\n"
                    }else {
                        nowElemStack += "<p><span class=\"highlight\">"+nowElem[ii][1]+"</span><em>"+nowElem[ii][2]+"</em></p>\n"
                    }
                }
            }else if (nowElem[ii][0]===2){//replace
                if(nowElem[ii][2].length>0){
                    let nowText = "";
                    for(let jj = 0; jj<nowElem[ii][2].length; jj++){
                        nowText += nowElem[ii][2][jj][0] + " > " + nowElem[ii][2][jj][1];
                        if(jj!=nowElem[ii][2].length-1){
                            nowText += " , "
                        }
                    }
                    nowElemStack += "<p><span class=\"highlight\">"+nowElem[ii][1]+"</span><em>"+nowText+"</em></p>\n"
                }
            }else if (nowElem[ii][0]===3){//WARN
                if(nowElem[ii][2].length>0){
                    nowElemStack += "<p><span class=\"highlight\">"+nowElem[ii][1]+"</span><em class='warn-em' style='color: #f34040;'>"+nowElem[ii][2]+"</em></p>\n"
                }
            }else{//SDS LOG
                if(nowElem[ii][1].length>0){
                    nowElemStack += "<p><em>"+nowElem[ii][1].replace(/\n/gi, "<br>")+"</em></p>\n"
                }
            }
        }
        nowHTML += nowElemStack.length>0 ? "<dd>"+nowElemStack+"</dd>" : "";
        $("#test_result_id").empty();
        $("#test_result_id").html(nowHTML);

        var hasEm = $('.warn-em').text();
        if ( hasEm ) {
            $('#test_result_id').scrollTop($('#test_result_id').prop('scrollHeight'));
        }
    }
}

function resetList(){
    nowDebug = [[]];
    nowDebugPK = [];
    nowIndexDebug = 0;
    chatSessionId = null;
    $("#nowDebugIndex").text("");
    $("#nowDebugStack").text("");
    $("#test_result_id").empty();
    $("#test_result_id").html("");
}

function setFirstIntent(){
    $.ajax({
        url: "voiceBot/getTestIntent",
        data: JSON.stringify({
            host: data.host}),
        type: "POST",
        async: false,
        contentType: 'application/json'
    }).then(function (response) {
        var intentList = response;

        for (var i = 0; i < intentList.length; i++) {
            if(intentList[i].Main == "처음으로"){
                firstIntent = intentList[i].Name;
            }
        }


    }).catch(function (response) {
        console.log('apply() status code: ' + response.status);
    });
}


function settingTestPrevTask(){
    if ( !scenario ) return; //시나리오가 없을 때

    var prevTaskArr = [];

    $.ajax({
        url: "voiceBot/getTestIntent",
        data: JSON.stringify({
            host: data.host}),
        type: "POST",
        async: false,
        contentType: 'application/json'
    }).then(function (response) {
        var intentList = response;

        for (var i = 0; i < intentList.length; i++) {
            if(intentList[i].Main != "종료"){

                var intentObj = new Object();
                intentObj.label = intentList[i].Name;
                intentObj.value = intentList[i].Main;

                prevTaskArr.push(intentObj);
            }
        }

        if(prevTaskTest == null){
            prevTaskTest = createSearchSelect($('#prev_task'));
            prevTaskTest.setOptions(prevTaskArr);
        }else{
            $("#test_data").val("");
            var html = '<input type="text" id="prev_task" class="select" readOnly>';
            prevTaskTest.remove();

            $('#prevTaskDiv').append(html);

            prevTaskTest = createSearchSelect($('#prev_task'));
            prevTaskTest.setOptions(prevTaskArr);
        }

    }).catch(function (response) {
        console.log('apply() status code: ' + response.status);
    });
}


function prevTaskDebugRefresh(){
    nowPrevDebug = [[]];
    nowPrevDebugPK = [];
    nowPrevIndexDebug = 0;
    $("#nowPrevDebugIndex").text("1");
    $("#nowPrevDebugStack").text("1");
    $("#test_prev_result_id").empty();

    var testPrevHtml = "";
    testPrevHtml +='<dt>INPUT</dt>';
    testPrevHtml +='<dd><p><span class="highlight">type</span><em></em></p></dd>';
    testPrevHtml +='<dd><p><span class="highlight">prob</span><em></em></p></dd>';
    testPrevHtml +='<dt>TASK</dt>';
    testPrevHtml +='<dd>';
    testPrevHtml +='<p><span class="highlight">prev task</span><em></em></p>';
    testPrevHtml +='<p><span class="highlight">task rel</span><em></em></p>';
    testPrevHtml +='<p><span class="highlight">task</span><em></em></p>';
    testPrevHtml +='</dd>';
    testPrevHtml +='<dt>ANSWER</dt>';
    testPrevHtml +='<dd><p><span class="highlight">text</span><em></em></p></dd>';

    $("#test_prev_result_id").append(testPrevHtml);

}

function debugTest(){
    console.log("debugTest ~~~~ : ");

    var testSentence = $("#test_data").val();
    var prevIntent = prevTaskTest.getValue()[0].value;

    var testDataObj = new Object();

    testDataObj.utter = testSentence;
    testDataObj.intent = prevIntent;

    var testObj = new Object();

    testObj.host = data.host;
    testObj.session = "test";
    testObj.data = testDataObj;
    testObj.lang = data.lang;

    $.ajax({
        url: "voiceBot/testLogDebug",
        data: JSON.stringify(testObj),
        type: "POST",
        contentType: 'application/json'
    }).then(function (response) {

        addChatbotPrevDebugByJson(response.jsonDebug);

    }).catch(function (response) {
        console.debug('testLogDebug fail!!');
    });
}

function addChatbotPrevDebugByJson(e){
    console.log("ChatbotDebug[By Json]")
    var debugJson = JSON.parse(e);
    try {
        var prob = debugJson.prob;
        if(debugJson.prob != undefined){
            if(debugJson.prob > 1.0 && debugJson.prob < 10.0){
                prob = debugJson.prob * 0.1;
            }else if(debugJson.prob > 10.0 && debugJson.prob < 100.0){
                prob = debugJson.prob * 0.01;
            }else if(debugJson.prob > 100.0 && debugJson.prob < 1000.0){
                prob = debugJson.prob * 0.001;
            }
        }
        if(debugJson.engine == "RegEx"){
            regexCheckTest(debugJson, "Task");
        }


        let elem = [
            [0, "INPUT"],
            [1, "input text",
                debugJson.input != undefined ? debugJson.input : ""],
            [1, "type", debugJson.type != undefined ? debugJson.type : ""],
            [2, "replace", []],
            [0, "INTENT"],
            [1, "engine",
                debugJson.engine != undefined ? debugJson.engine : ""],
            [1, "match regex",
                debugJson.matchingRegex != undefined ? debugJson.matchingRegex : ""],
            [1, "model", debugJson.model != undefined ? debugJson.model : ""],
            [1, "prob", prob != undefined ? prob.toString() : ""],
            [0, "TASK"],
            [1, "prev task",
                debugJson.prevIntent != undefined ? debugJson.prevIntent : " "],
            [1, "task rel",
                debugJson.taskRel != undefined ? debugJson.taskRel : " "],
            [1, "task", debugJson.intent != undefined ? debugJson.intent : ""],
            [0, "ANSWER"],
            [1, "text", debugJson.answer != undefined ? debugJson.answer : ""],
            [0, "SDS LOG"],
            [4, debugJson.sdsLog != undefined ? debugJson.sdsLog : ""]
        ];
        if (e.intent === "챗봇공지사항" || e.intent === "선톡") {
            return;
        }

        if (debugJson.logDebugErrLog != undefined) {
            elem.push([3, "WARN", debugJson.logDebugErrLog]);
        }

        if (debugJson.taskRel != undefined) {
            if (debugJson.taskRel.toString().includes("(의도)")) {
                elem.push([3, "WARN",
                    "부합한 태스크 관계가 없습니다. 의도의 이름과 같은 이름의 답변이 연결 되었습니다."])
            }
            if (debugJson.taskRel.toString().includes("(발화)")) {
                elem.push([3, "WARN",
                    "부합한 태스크 관계가 없습니다. 채팅 입력값과 같은 이름의 답변이 연결 되었습니다."])
            }
        }
        if (debugJson.replace != undefined) {
            let debugReplaceList = debugJson.replace.toString().split("<cell>");
            for (let ii = 0; ii < debugReplaceList.length; ii++) {
                if (debugReplaceList[ii].length > 0) {
                    elem[3][2].push(debugReplaceList[ii].split("<split>"));
                }
            }
        }

        if (nowPrevDebug[0].length == 0) {
            nowPrevDebug[0] = elem;
        } else {
            nowPrevDebug.push(elem);
        }
    }catch (e) {

    }
    renderPrevDebugList(-99);
}

function regexCheckTest(debugJson, TestType) {

    var regex = debugJson.model;

    var findGroupRegex = /\((.[^?!=.]*.)\)/gm;
    var findInnerGroupRegex = /(\((.[^()]*)\))/gm;
    var groupArr = [];
    var groupListIdx = 1;
    while ((matches = findGroupRegex.exec(regex)) !== null){
        if (matches.index === findGroupRegex.lastIndex) {
            findGroupRegex.lastIndex++;
        }

        matches.forEach((match, groupIndex) => {

            if(groupIndex == 1){

                if(match.substr(-1) === ")"){
                    match = "(" + match;
                }else {
                    match = "(" + match + ")";
                }
                var groupObj = new Object();
                groupObj.groupNum = groupListIdx;
                groupObj.groupRegex = match;
                groupArr.push(groupObj);
                groupListIdx++;

                while ((InnerMatches = findInnerGroupRegex.exec(match)) !== null) {

                    if (InnerMatches.index === findInnerGroupRegex.lastIndex) {
                        findInnerGroupRegex.lastIndex++;
                    }

                    InnerMatches.forEach((innerMatch, InnerGroupIndex) => {
                        if(InnerGroupIndex == 1){
                            if(match != innerMatch){
                                var groupObj = new Object();
                                groupObj.groupNum = groupListIdx;
                                groupObj.groupRegex = innerMatch;
                                groupArr.push(groupObj);
                                groupListIdx++;
                            }
                        }
                    });
                }

            }
        });
    }

    var inputText = debugJson.input;
    var pattern = "";

    if (regex.indexOf("(?i)") != -1) {
        regex = regex.replace(/\(\?i\)/g, "");
        pattern = new RegExp(regex, "gi");
    } else {
        pattern = new RegExp(regex);
    }

    var result = inputText.match(pattern);

    var matchingRegex = "";
    for (var i = 1; i < result.length; i++) {
        if(result[i] !== undefined){
            if(matchingRegex == ""){
                matchingRegex = '<button type="button" class="highlight" style="border-radius: 3px; background:#DDE3FF;"onclick="showRegexGroup(\''+groupArr[i-1].groupNum+'\',\''+groupArr[i-1].groupRegex+'\',\''+regex+'\',\''+TestType+'\')">' + result[i] + '</button>';
            }else {
                matchingRegex = matchingRegex + '<button type="button" class="highlight" style="border-radius: 3px; background:#DDE3FF;" onclick="showRegexGroup(\''+groupArr[i-1].groupNum+'\',\''+groupArr[i-1].groupRegex+'\',\''+regex+'\',\''+TestType+'\')">' + result[i] + '</button>';
            }
            debugJson.matchingRegex = matchingRegex;

            debugJson.input = debugJson.input.replace(result[i], "<span style='font-weight: bold; text-decoration: underline;'>" + result[i] + "</span>");
        }
    }
}

function showRegexGroup(groupNum, matchRegex, regex, TestType){

    var groupStartIdxArr = [];
    var groupStartIdx = regex.indexOf("(");

    regex = regex.replaceAll("s*","\\s*");
    matchRegex = matchRegex.replaceAll("s*","\\s*");

    while (groupStartIdx != -1){
        if(groupStartIdx != -1 && regex.substr(groupStartIdx+1,1) !== "?"){
            groupStartIdxArr.push(groupStartIdx);
        }
        groupStartIdx = regex.indexOf("(", groupStartIdx + 1);
    }

    if(TestType == "Task"){
        $("#modelText").empty();
        regex = regex.substr(0,groupStartIdxArr[groupNum-1]) + "<span style='font-weight: bold; text-decoration: underline;'>" + matchRegex + "</span>" +regex.substr(groupStartIdxArr[groupNum-1] + matchRegex.length);
        $("#modelText").append(regex);
    }else {
        $("#scenarioModelText").empty();
        regex = regex.substr(0,groupStartIdxArr[groupNum-1]) + "<span style='font-weight: bold; text-decoration: underline;'>" + matchRegex + "</span>" +regex.substr(groupStartIdxArr[groupNum-1] + matchRegex.length);
        $("#scenarioModelText").append(regex);
    }

}

function renderPrevDebugList(nowIndex){
    if(nowIndex == -99){
        renderPrevDebugList(nowPrevDebug.length-1);
    }else if (nowIndex == -1 || nowIndex == nowPrevDebug.length) {
    }else
    {
        nowPrevIndexDebug = nowIndex;
        $("#nowPrevDebugIndex").text(nowPrevIndexDebug+1);
        $("#nowPrevDebugStack").text(nowPrevDebug.length)
        let nowElem = nowPrevDebug[nowIndex];
        let nowHTML = "";
        let nowElemStack = "";
        for(let ii = 0; ii<nowElem.length; ii++){
            if(nowElem[ii][0]===0){//카테고리
                nowHTML += (nowElemStack.length>0 ? "<dd>"+nowElemStack+"</dd>" : "") + "<dt>"+nowElem[ii][1]+"</dt>\n";
                nowElemStack = "";
            }else if (nowElem[ii][0]===1){//일반 내용
                if(nowElem[ii][2].length>0){
                    if(nowElem[ii][1] == "model"){
                        nowElemStack += "<p><span class=\"highlight\">"+nowElem[ii][1]+"</span><em id=\"modelText\">"+nowElem[ii][2]+"</em></p>\n"
                    }else {
                        nowElemStack += "<p><span class=\"highlight\">"+nowElem[ii][1]+"</span><em>"+nowElem[ii][2]+"</em></p>\n"
                    }
                }
            }else if (nowElem[ii][0]===2){//replace
                if(nowElem[ii][2].length>0){
                    let nowText = "";
                    for(let jj = 0; jj<nowElem[ii][2].length; jj++){
                        nowText += nowElem[ii][2][jj][0] + " > " + nowElem[ii][2][jj][1];
                        if(jj!=nowElem[ii][2].length-1){
                            nowText += " , "
                        }
                    }
                    nowElemStack += "<p><span class=\"highlight\">"+nowElem[ii][1]+"</span><em>"+nowText+"</em></p>\n"
                }
            }else if (nowElem[ii][0]===3){//WARN
                if(nowElem[ii][2].length>0){
                    nowElemStack += "<p><span class=\"highlight\">"+nowElem[ii][1]+"</span><em class='warn-em'>"+nowElem[ii][2]+"</em></p>\n"
                }
            }else{//SDS LOG
                if(nowElem[ii][1].length>0){
                    nowElemStack += "<p><em>"+nowElem[ii][1].replace(/\n/gi, "<br>")+"</em></p>\n"
                }
            }
        }
        nowHTML += nowElemStack.length>0 ? "<dd>"+nowElemStack+"</dd>" : "";
        $("#test_prev_result_id").empty();
        $("#test_prev_result_id").html(nowHTML);

        var hasEm = $('.warn-em').text();
        if ( hasEm ) {
            $('#test_result_id').scrollTop($('#test_result_id').prop('scrollHeight'));
        }
    }
}

//음성봇 테스트시 내용 지우기
function voiceTalkRefresh(){
    $('.bot').remove();
    $('.user').remove();
    $('.end_system_entry').remove();
}

function changeNqaTextTrain(text) {
    $('.bubble.nqa').addClass('active').text(text);
}

function hideNqaTextTrain() {
    $('.bubble.nqa').removeClass('active').text('');
}

function debugTestResizeArea(){
    let parent = $('.resize_areas');
    let parentHeight = 0;
    let childrenArea = parent.find('.resize_area');
    let firstCldHeight = 0;
    let lastCldHeight = 0;
    let startY = 0;
    let delY = 0;
    parent.children('.drag_resize').on('mousedown', function(e){
        let dragTarget = $(this);
        parentHeight = parent.outerHeight() - 14; // dragTarget height 제외
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

            if ( delFirstCldHeight < 150 ) {
                delFirstCldHeight = 150;
                delLastCldHeight = parentHeight - delFirstCldHeight;
            }
            if ( delLastCldHeight < 150 ) {
                delLastCldHeight = 150;
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