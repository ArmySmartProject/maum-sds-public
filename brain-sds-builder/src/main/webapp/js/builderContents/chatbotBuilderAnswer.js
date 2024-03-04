var imgData                 = []; // 이미지 슬라이드
var imgSelectedData         = [];
var buttonData              = []; // 버튼
var buttonSelectedData      = [];
var taskData1               = [{'label':"항상",'value':0}]; // 이전 태스크
var singleSelectedData1     = [];
var taskData2               = []; // 의도
var singleSelectedData2     = [];
var detail                  = undefined;
var imgSearchKeyword        = "";
var btnSearchKeyword        = "";
var prevSearchKeyword       = "";
var bertIntentKeyword       = "";
var imgCarTimer;
var nowModifyingPK          = -1;
var nowImgUploaded          = false;
var nowSearchKeyword        = "";
var nowCropSrc              = undefined;
var nowDetailSearchLang     = 1;
var newPointer;
var eventPointer;
var nowLoading              = false;//확인 눌렀을 경우
var nowDeleted              = false;

var multiLangDetail         = []
//element -> index:lang, Dictionary {taskname , answer, previewDescription, addedDescription, No}

var nowSortable             = false;
var outerSortable           = false;
var nowContentsOrderList    = [0,1,2];


function chatClick(host, cp) {
    var obj = new Object();
    obj.chatName = $('#chatListUl>li.active>div:first>span').text();
    obj.host = host;
    obj.searchIntent = $("input[name='intentText']").val();
    obj.cp = cp;
    obj.lang = nowSearchLang;

    let postUrl = "getTask"
    if(nowSearchKeyword.length!=0){
        postUrl = "getTaskSearch";
        obj.searchTaskSentence = nowSearchKeyword;
    }

    addAjax();
    $.ajax({
        url : postUrl,
        data : JSON.stringify(obj),
        type: "POST",
        contentType: 'application/json'
    }).success(
        function(result) {
            finAjax();
            var list = result.answerList;
            var paging = result.paging;
            var cnt = paging.startRow + 1;
            var searchIntent = result.searchIntent;
            var chatName = result.chatName;
            if($('#chatListUl>li.active>div:first>span').text().length>0){
                $("#chatTitle").text($('#chatListUl>li.active>div:first>span').text());
            }
            hostToPage = parseInt(String(host));
            $("#intentBody").empty();

            var innerHTML = "";
            let replaceKeyword = nowSearchKeyword.length!=0 ? nowSearchKeyword : "<치환>";

            $.each(list, function (i, v) {
                if (v.answer == undefined) {
                    v.answer = "";
                }
                innerHTML += '<tr>\n' +
                    '                                            <td>' + cnt +'</td>\n' +
                    '                                            <td>'+ replaceKeywordInTable(v.Main,replaceKeyword) +'</td>\n' +
                    '                                            <td>'+ replaceKeywordInTable(v.Name,replaceKeyword) +'</td>\n' +
                    '                                            <td><span class="text_ellipsis">'+replaceKeywordInTable(v.answer,replaceKeyword)+'</span></td>\n' +
                    '                                            <td class="no_bg"><a href="#chat_answer_setting" class="btn_line_primary btn_lyr_open" onclick="answerDetail('+v.No+')">상세</a></td>\n' +
                    '                                            <td class="no_bg"><a href="#chat_row_delete" class="btn_line_warning btn_lyr_open" onclick="delDetail('+v.No+',\''+ v.Main + '\')">삭제</a></td>\n' +
                    '                                        </tr>';

                host = v.Account;
                cnt++;
            });

            $("#intentBody").append(innerHTML);
            innerHTML = "";
            $(".pagination").empty();

            var columns = ["task", "answer"];
            for (var i = 0; i < 2; i++) {
                Array.prototype.forEach.call(document.getElementsByName(columns[i]), function(el) {
                    var regex = new RegExp(searchIntent+"ig");
                    el.innerHTML = el.innerHTML.replace(regex, "<span style='color:red'>"+searchIntent+"</span>");
                });
            }

            innerHTML += '<button type="button" class="first" onclick="goPage('+ host + ',1)"><span>&laquo;</span></button>';
            innerHTML += '<button type="button" class="prev" onclick="goPage('+ host + ',' + paging.prevPage +')"><span>&lsaquo;</span></button>';
            innerHTML += '<div class="pages">';

            for (var i = paging.pageRangeStart; i<=paging.pageRangeEnd; i++){
                if(i == paging.currentPage){
                    innerHTML += '<span class="page active">'+ i +'</span>';
                }else{
                    innerHTML += '<span class="page" onclick="goPage('+ host + ',' + i +')">'+ i +'</span>';
                }
            }

            innerHTML += '</div>';
            innerHTML += '<button type="button" class="next" onclick="goPage('+ host + ',' + paging.nextPage +')"><span>&rsaquo;</span></button>';
            innerHTML += '<button type="button" class="last" onclick="goPage('+ host + ',' + paging.totalPage +')"><span>&raquo;</span></button>';
            innerHTML += '</div>';

            $(".pagination").append(innerHTML);

            var taskLists = result.taskList;
            var bertIntentLists = result.bertIntentList;
            imgData = []; // 이미지 슬라이드
            imgSelectedData = [];
            buttonData = []; // 버튼
            buttonSelectedData = [];
            taskData1 = [{'label':"항상",'value':0}]; // 이전 태스크
            singleSelectedData1 = [];
            taskData2 = []; // 의도
            singleSelectedData2 = [];
            let newElemFirst = {'main':""};
            for (let ii = 0; ii<taskLists.length; ii++){
                if(taskLists[ii].Language.toString()===fixLang.toString()){
                    imgData.push({'main':taskLists[ii].Main, 'name':taskLists[ii].Name, 'label':taskLists[ii].Main +' ('+ taskLists[ii].Name +')', 'value':taskLists[ii].No, 'url':taskLists[ii].URL, 'valueLang1':0, 'valueLang2':0, 'valueLang3':0, 'valueLang4':0})
                    if(taskLists[ii].Main==='처음으로'){
                      newElemFirst = {'main':taskLists[ii].Main, 'name':taskLists[ii].Name, 'label':taskLists[ii].Main +' ('+ taskLists[ii].Name +')','value':taskLists[ii].No, 'valueLang1':0, 'valueLang2':0, 'valueLang3':0, 'valueLang4':0}
                    }else{
                      buttonData.push({'main':taskLists[ii].Main, 'name':taskLists[ii].Name, 'label':taskLists[ii].Main + ' (' + taskLists[ii].Name +')','value':taskLists[ii].No, 'valueLang1':0, 'valueLang2':0, 'valueLang3':0, 'valueLang4':0})
                    }
                    taskData1.push({'main':taskLists[ii].Main, 'name':taskLists[ii].Name, 'label':taskLists[ii].Main + ' (' + taskLists[ii].Name +')','value':taskLists[ii].No, 'valueLang1':0, 'valueLang2':0, 'valueLang3':0, 'valueLang4':0})
                }
            }
            if(newElemFirst.main==='처음으로'){
                buttonData.push(newElemFirst);
            }
            for (let ii = 0; ii<taskLists.length; ii++){
                let imgDataIndex = imgData.findIndex((item, idx) => {
                    return item.main === taskLists[ii].Main;
                });
                let buttonDataIndex = buttonData.findIndex((item, idx) => {
                    return item.main === taskLists[ii].Main;
                });
                let taskData1Index = taskData1.findIndex((item, idx) => {
                    return item.main === taskLists[ii].Main;
                });
                if(imgDataIndex!=-1)imgData[imgDataIndex]["valueLang"+taskLists[ii].Language.toString()] = taskLists[ii].No;
                if(buttonDataIndex!=-1)buttonData[buttonDataIndex]["valueLang"+taskLists[ii].Language.toString()] = taskLists[ii].No;
                if(taskData1Index!=-1)taskData1[taskData1Index]["valueLang"+taskLists[ii].Language.toString()] = taskLists[ii].No;
            }
            for (let ii = 0; ii<bertIntentLists.length; ii++){
                if(bertIntentLists[ii].Language.toString()===fixLang.toString()){
                    taskData2.push({'label':bertIntentLists[ii].Name,'value':bertIntentLists[ii].No, 'valueLang1':0, 'valueLang2':0, 'valueLang3':0, 'valueLang4':0})
                }
            }
            for (let ii = 0; ii<bertIntentLists.length; ii++){
                let taskData2Index = taskData2.findIndex((item, idx) => {
                    return item.label === bertIntentLists[ii].Name;
                });
                if(taskData2Index!=-1)taskData2[taskData2Index]["valueLang"+bertIntentLists[ii].Language] = bertIntentLists[ii].No;
            }
            imgData.sort(function(a, b) {
                let aCompare = 0;
                let bCompare = 0;
                if(a.url!=undefined){
                    if(a.url.length!=0){
                        aCompare = 1;
                    }
                }
                if(b.url!=undefined){
                    if(b.url.length!=0){
                        bCompare = 1;
                    }
                }
                return bCompare - aCompare;
            });
            delIntentBodyHref();
            resetIntents();
        }).fail(function(result) {
        finAjax();
        console.log("getIntentList error");
    });
};

function goIntent(host, cp) {
    var form = document.createElement('form');
    var intention;
    intention = document.createElement('input');
    intention.setAttribute('type', 'hidden');
    intention.setAttribute('name', 'host');
    intention.setAttribute('value', host);
    form.appendChild(intention);
    form.setAttribute('method', 'post');
    form.setAttribute('action', "answer");
    document.body.appendChild(form);
    form.submit();
}

function newAnswer(){
    detail = undefined;
    openDetailPage();
    $("#ori_ipt").empty();
    $("#chat_answer_setting").removeClass("more_lang")
    $("#sec_ipt").empty();
    nowContentsOrderList    = [0,1,2];
    $("#ori_ipt").append(iptConstruct(false));
    armDescription("");
    generateBtnItem();
    resetIntents();
    changeMain("");
    changeDisplay("");
    generateImgCarousel();
    generateBertIntentListRender();
    addRowChatbotTask();
    multiLangDetail = [{},{},{},{},{}];
    for(let ii = 0; ii<5; ii++){
        multiLangDetail[ii] = {
            taskname: "",
            answer : "",
            previewDescription : "",
            addedDescription : "",
            No : ""
        }
    }
    afterIPTConstruct();
    nowModifyingPK = -1;
    nowImgUploaded = false;
    nowDeleted = false;
    $("#chat_answer_setting > div.lyr_mid > div.skeleton").css('display', 'none');


  nowSortable             = false;
  outerSortable           = false;
  nowContentsOrderList    = [0,1,2];
  renderContentsOrder();
}

function answerDetail(intentNum){
    openDetailPage();
    var obj = new Object();
    var nowDetailPlusLang = 0;
    $("#chat_answer_setting > div.lyr_mid > div.skeleton").css('display', 'block');

    let intentIndex = taskData1.findIndex((item, idx) => {
        return item.value === intentNum;
    });
    if(intentIndex==-1){
        let intentIndexLang1 = taskData1.findIndex((item, idx) => {
            return item.valueLang1 === intentNum;
        });
        let intentIndexLang2 = taskData1.findIndex((item, idx) => {
            return item.valueLang2 === intentNum;
        });
        let intentIndexLang3 = taskData1.findIndex((item, idx) => {
            return item.valueLang3 === intentNum;
        });
        let intentIndexLang4 = taskData1.findIndex((item, idx) => {
            return item.valueLang4 === intentNum;
        });
        if(intentIndexLang1!=-1){
            nowDetailPlusLang = 1;
            obj.intentNum = taskData1[intentIndexLang1].value;
        }else if(intentIndexLang2!=-1){
            nowDetailPlusLang = 2;
            obj.intentNum = taskData1[intentIndexLang2].value;
        }else if(intentIndexLang3!=-1){
            nowDetailPlusLang = 3;
            obj.intentNum = taskData1[intentIndexLang3].value;
        }else if(intentIndexLang4!=-1){
            nowDetailPlusLang = 4;
            obj.intentNum = taskData1[intentIndexLang4].value;
        }
    }else{
        obj.intentNum = intentNum;
    }

    obj.accountPara = hostToPage;
    addAjax();
    $.ajax({
        url : "getDetailTask",
        data : JSON.stringify(obj),
        type: "POST",
        contentType: 'application/json'
    }).success(
        function(result) {
            nowContentsOrderList = result.answerList.ResponseOrder == undefined ? "0,1,2" : result.answerList.ResponseOrder.split(",");
            finAjax();
            detail = result.answerList;
            let biList = result.bertIntentList;
            generateBertIntentList(biList);
            try{
                syncSelectBox(detail.Next.toString())
            }catch(e){
                syncSelectBox("");
            }

            $("#ori_ipt").empty();
            $("#chat_answer_setting").removeClass("more_lang")
            $("#sec_ipt").empty();
            $("#ori_ipt").append(iptConstruct(true));
            $('#answerPreview').html((detail.answer!=undefined ? replaceAnswerPreview(detail.answer) : ""));
            delBlank();

            generateBtnItem();

            resetIntents();

            armDescription(detail.Description);
            urlToAMR(detail.URL,
                detail.Name, detail.Description);

            generateImgCarousel();

            changeDisplay(detail.Name);
            generateBertIntentListRender();

            var txt_btn = $('#answerPreview').find('a');
            var txt_btnLength = $('#answerPreview').find('a').length;
            if (txt_btnLength > 1) {
                $('#answerPreview').find('a').attr('href', '#');
                $('#answerPreview').find('a').wrap('<div class="txt_btns"></div>');
            } else {
                $('#answerPreview').find('a').attr('href', '#');
                $('#answerPreview').find('a').wrapAll('<div class="txt_btn"></div>');
            }
            $("#chat_answer_setting > div.lyr_mid > div.chat_setting.answer_preview > div > dl:nth-child(2) > dd > textarea").change();
            $("#chat_answer_setting > div.lyr_mid > div.skeleton").css('display', 'none');

            nowModifyingPK = detail.No;
            nowImgUploaded = false;
            nowDeleted = false;

            let imgLetText = "";
            for(let ii = 0; ii<imgSelectedData.length; ii++){
                imgLetText += imgSelectedData[ii].label;
                if(ii!=imgSelectedData.length-1){
                    imgLetText += ","
                }
            }
            $("#img_slide > p").text(imgLetText);
            let btnLetText = "";
            for(let ii = 0; ii<buttonSelectedData.length; ii++){
                btnLetText += buttonSelectedData[ii].label;
                if(ii!=buttonSelectedData.length-1){
                    btnLetText += ","
                }
            }
            $("#button > p").text(btnLetText);

            multiLangDetail = [{},{},{},{},{}];
            let otherLangs = result.otherLang;
            for (let ii = 0; ii<otherLangs.length; ii++){
                let nowLang = otherLangs[ii];
                try{
                    multiLangDetail[Number(nowLang.Language)] = {
                        taskname: nowLang.Name,
                        answer : nowLang.answer,
                        previewDescription : nowLang.Description,
                        addedDescription : nowLang.Description,
                        No : nowLang.No,
                        A_URL : nowLang.A_URL
                    }
                }catch(e){
                    console.log(e)
                    multiLangDetail[Number(nowLang.Language)] = {
                        taskname: "",
                        answer : "",
                        previewDescription : "",
                        addedDescription : "",
                        No : "",
                        A_URL : ""
                    }
                }
            }
            for(let ii = 0; ii<4; ii++){
                if(nowLangApply[ii] && multiLangDetail[ii+1].taskname==undefined){
                    multiLangDetail[ii+1] = {
                        taskname: "",
                        answer : "",
                        previewDescription : "",
                        addedDescription : "",
                        No : "",
                        A_URL : ""
                    }
                }
            }
            for(let ii = 0; ii<5; ii++){
                if(multiLangDetail[ii].No!=undefined){
                    if (multiLangDetail[ii].answer===undefined){
                        multiLangDetail[ii].answer = "";
                    }
                }
            }
            multiLangDetail[detail.Language].A_URL = detail.A_URL;
            afterIPTConstruct();


          nowSortable             = false;
          outerSortable           = false;
          renderContentsOrder();
        }).fail(function(result) {
        finAjax();
        console.log("getIntentList error");
    });
}

function openChatUpload(){
    var winHeight = $(window).height() * 0.7,
        hrefId = "#chat_answer_upload";

    thisLayer = $(hrefId);

    if ( thisLayer.hasClass('sub_lyr') ) {
        thisLayer.wrap('<div class="lyrWrap"></div>');
        thisLayer.before('<div class="lyr_bg"></div>');
        thisLayer.prev('.lyr_bg').css({'background': 'rgba(0,0,0,0.1)'});
    } else {
        $('body').css('overflow', 'hidden');
        thisLayer.wrap('<div class="lyrWrap"></div>');
        thisLayer.before('<div class="lyr_bg"></div>');
    }

    //대화 UI
    $('.lyrBox .lyr_mid').each(function () {
        $(this).css('max-height', Math.floor(winHeight) + 'px');
    });

    // 수정 201209 AMR Layer popup close btn_close click
    $('.btn_lyr_close').on('click', function (e) {
        hrefId = $(this).parents('.lyrBox').attr('id');
        var id = '#' + hrefId;
        thisLayer = $(id);
        e.stopPropagation();
        $(".sub_lyr").css('display', 'none');
        lyrClose(thisLayer);
        $("#carousel_image_setting").css("display","none");
        delPopper();
    });

    // 확인버튼
    $('.btn_lyr_close_submit').prop("onclick", null).off("click");
    $('#btn_setting_lyr_close_id').prop("onclick", null).off("click");
    $('.btn_lyr_close_submit').on('click', function (e) {
        confirmAnswer($(this),e);
    });
    $('#btn_setting_lyr_close_id').on('click', function (e) {
        exitCropPage();
    });

    // 추가 201209 AMR lyrClose
    function lyrClose(target) {
        var lyrWrap = $('.lyrWrap');
        if (lyrWrap.length) {
            target.unwrap('<div class="lyrWrap"></div>');
        }
        $("#chat_answer_setting").click();
        target.prev('.lyr_bg').remove();


        if ( !target.hasClass('sub_lyr') ) {
            $('body').css('overflow', '');
        }
    }

}

function lyrClose(target) {
    var lyrWrap = $('.lyrWrap');
    if (lyrWrap.length) {
        target.unwrap('<div class="lyrWrap"></div>');
    }
    $("#chat_answer_setting").click();
    target.prev('.lyr_bg').remove();


    if ( !target.hasClass('sub_lyr') ) {
        $('body').css('overflow', '');
    }
}

function uploadExcel() {
    delCacheNow()
    var txt = '저장';
    var changeTxt = '저장중';
    var host = $("#chatListUl>li.active").val();
    var file = $("#excelFile").val();
    if (file === "" || file === null) {
        mui.alert('파일을 선택 해주세요.');
        return;
    }

    $('#btn_upload').text(changeTxt).addClass('gradient');
    var options = {
        success: function (response) {
            $('#btn_upload').text(txt).removeClass('gradient');
            mui.alert(response.status);
            changeContents(0);
        }, error: function (response) {
            $('#btn_upload').text(txt).removeClass('gradient');
            mui.alert("작업 중 오류가 발생했습니다.");
            console.log(response);
        },
        type: "POST"
    };
    if($('#chatListUl>li.active>input[type=hidden]').val() === 'Y') {
        console.log('H_TASK !!! insert');
        $("#chatUpload").attr('action', '/sds-builder/upload/insertChatExcelHTask');
    }
    $("#chatUpload input[name='host']").val(host);
    $("#chatUpload").ajaxSubmit(options);
}

function openDetailPage(){
  overlayTutorial(11);
    var winHeight = $(window).height() * 0.7,
        hrefId = "#chat_answer_setting";

    thisLayer = $(hrefId);

    if ( thisLayer.hasClass('sub_lyr') ) {
        thisLayer.wrap('<div class="lyrWrap"></div>');
        thisLayer.before('<div class="lyr_bg"></div>');
        thisLayer.prev('.lyr_bg').css({'background': 'rgba(0,0,0,0.1)'});
    } else {
        $('body').css('overflow', 'hidden');
        thisLayer.wrap('<div class="lyrWrap"></div>');
        thisLayer.before('<div class="lyr_bg"></div>');
    }


    //대화 UI
    $('.lyrBox .lyr_mid').each(function () {
        $(this).css('max-height', Math.floor(winHeight) + 'px');
    });

    //Layer popup close
    $('.btn_lyr_close').on('click', function () {
        $('body').css('overflow', '');
        var lyrWrap = $('.lyrWrap');
        if (lyrWrap.length) {
            $('body').find(hrefId).unwrap('<div class="lyrWrap"></div>');
        }
        var $tr = $("#tblChatbotTask").find('tbody tr');
        $tr.each(function(){
            $thisTr = $(this);
            $thisTr.remove();
        });
        $('.lyr_bg').remove();
    });

    // 수정 201209 AMR Layer popup close btn_close click
    $('.btn_lyr_close').on('click', function (e) {
        hrefId = $(this).parents('.lyrBox').attr('id');
        var id = '#' + hrefId;
        thisLayer = $(id);
        e.stopPropagation();
        $(".sub_lyr").css('display', 'none');
        lyrClose(thisLayer);
        $("#carousel_image_setting").css("display","none");
        delPopper();
    });

    // 확인버튼
    $('.btn_lyr_close_submit').prop("onclick", null).off("click");
    $('#btn_setting_lyr_close_id').prop("onclick", null).off("click");
    $('.btn_lyr_close_submit').on('click', function (e) {
        confirmAnswer($(this),e);
    });
    $('#btn_setting_lyr_close_id').on('click', function (e) {
        exitCropPage();
    });


    // 추가 201209 AMR lyrClose
    function lyrClose(target) {
        var lyrWrap = $('.lyrWrap');
        if (lyrWrap.length) {
            target.unwrap('<div class="lyrWrap"></div>');
        }
        $("#chat_answer_setting").click();
        target.prev('.lyr_bg').remove();

        if ( !target.hasClass('sub_lyr') ) {
            $('body').css('overflow', '');
        }
    }


    $('#answerPreview').text("답변");
}


function resetIntents(){
    multiSelectReset();
    // 드롭다운 박스 열기 닫기
    $('.dl_dropdown dt').on('click', function(){
        var parent = $(this).parent('.dl_dropdown');
        parent.toggleClass('active');
    });
    // tbl_commands 버튼으로 체크한 테이블 로우 지우기
    $('.btn_remove_row').on('click', function(){
        var $commands = $(this).parents('.tbl_commands');
        var $table = $commands.next();
        var $tr = $table.find('tbody tr');

        $tr.each(function(){
            $thisTr = $(this);
            $thisCheckbox = $thisTr.find('td').eq(0).find('input[type="checkbox"]');

            if ( $thisCheckbox.prop('checked') ) {
                $thisTr.remove();
            }
        });
    });
}
resetIntents();

// AMR 검색기능이 포함된 셀렉트 이벤트
function createMultiSelect($el, data, selectedData){
    var $select = $el.find('p.select'); // 셀렉트버튼
    var $dropdown = $el.find('.fast_popper_option'); // 셀렉트 드롭다운
    var $dropdownList = $el.find('.select_list'); // 드롭다운 내 리스트

    var dropdownController = Popper.createPopper($select[0], $dropdown[0], {
        strategy: 'fixed',
        modifiers: [
            {
                name: 'offset',
                options: {
                    offset: [0, 5],
                },
            },
        ],
    });

    setTimeout(function(){
        $dropdown.appendTo("body"); //ie에서 dropdown 위치를 못잡아서 body appendTo로 해결
    });

    $el.prop("onclick", null).off("click");
    $el.on('click', function(e){ // 버블링 막기
        e.stopPropagation();
    });
    $select.prop("onclick", null).off("click");
    $select.on('click', function(){ // 리스트 열기 닫기
      if(!nowSortable){
        if ( $el.parents('.lyrBox') ) { //모달안에 있을 경우 모달 클릭 시 닫힘
          $('.lyrBox').prop("onclick", null).off("click");
          $('.lyrBox').on('click', function(){
            $dropdown.removeClass('active');
          });
        }

        if ( $dropdown.hasClass('active') ) {
          $dropdown.removeClass('active');
          $dropdown.find('input.search').focusout();
          return;
        }

        $('.fast_popper_option').removeClass('active');
        $dropdown.addClass('active');
        $dropdown.find('input.search').focus();

        $dropdown.width($el.width());
        dropdownController.forceUpdate();
      }

    });

    renderMultiSelect(data, selectedData, $select.selector.toString()); //셀렉트 리스트 그리기
    renderSelectLabel($el); //셀렉트 된 label text를 p에 표시

    $dropdownList.find('label').on('click', function(){

        //IntentRel
        if ( !$el.attr('multiple') ) {
            $dropdown.find('input[type="checkbox"]').prop('checked', false);
            $(this).find('input[type="checkbox"]').prop('checked', true);
            renderSelectLabel($el);

            value = 'single_value'
            // $el.trigger('change');
            return;
        }

        //Carousel & Button
        value = 'multiple_value';
        let selectIndex = renderSelectLabel($el);
        if($el[0].id=="img_slide"){
            for(let ii = 0; ii<selectIndex.length; ii++){
                var nowToggle = false;
                for(let jj = 0; jj<imgSelectedData.length; jj++){
                    if(imgData[selectIndex[ii]].value === imgSelectedData[jj].value){
                        nowToggle = true;
                    }
                }
                if(!nowToggle){
                    imgSelectedData.push(imgData[selectIndex[ii]])
                }
            }
            for(let ii = 0; ii<imgSelectedData.length; ii++){
                var nowToggle = false;
                for(let jj = 0; jj<selectIndex.length; jj++){
                    if(imgSelectedData[ii].value === imgData[selectIndex[jj]].value){
                        nowToggle = true;
                    }
                }
                if(!nowToggle){
                    imgSelectedData.splice(ii, 1);
                }
            }
            var labelBox = $el.find('p.select');
            var nowText = "";
            for(let ii = 0; ii<imgSelectedData.length; ii++){
                nowText += imgSelectedData[ii].label;
                if(ii!=imgSelectedData.length-1){
                    nowText += ",";
                }
            }
            labelBox.empty();
            labelBox.text(nowText);
            generateImgCarousel();
        }else if($el[0].id=="button"){
            for(let ii = 0; ii<selectIndex.length; ii++){
                var nowToggle = false;
                for(let jj = 0; jj<buttonSelectedData.length; jj++){
                    if(buttonData[selectIndex[ii]].value === buttonSelectedData[jj].value){
                        nowToggle = true;
                    }
                }
                if(!nowToggle){
                    buttonSelectedData.push(buttonData[selectIndex[ii]])
                }
            }
            for(let ii = 0; ii<buttonSelectedData.length; ii++){
                var nowToggle = false;
                for(let jj = 0; jj<selectIndex.length; jj++){
                    if(buttonSelectedData[ii].value === buttonData[selectIndex[jj]].value){
                        nowToggle = true;
                    }
                }
                if(!nowToggle){
                    buttonSelectedData.splice(ii, 1);
                }
            }
            var labelBox = $el.find('p.select');
            var nowText = "";
            for(let ii = 0; ii<buttonSelectedData.length; ii++){
                nowText += buttonSelectedData[ii].label;
                if(ii!=buttonSelectedData.length-1){
                    nowText += ",";
                }
            }
            labelBox.empty();
            labelBox.text(nowText);
            generateBtnItem();
        }
    });

    $el.on('change', function(){
        // console.log("$el changed");
    });

    $(".search").off('keyup');
    $(".search").keyup(function(key) {
        if (key.keyCode == 13) {
            $(this).next().click();
        }
    });

    function renderMultiSelect(data, checkData, elem) { //셀렉트 리스트 그리기
        let nowSelectKeyword = ""
        let nowImgSlide = false;
        if(elem == "#img_slide p.select"){
            nowSelectKeyword = imgSearchKeyword;
            nowImgSlide = true;
        }else if(elem == "#button p.select"){
            nowSelectKeyword = btnSearchKeyword;
        }else if(elem == ".single_test1 p.select"){
            nowSelectKeyword = prevSearchKeyword;
        }else if(elem == ".single_test2 p.select"){
            nowSelectKeyword = bertIntentKeyword;
        }

        $dropdownList.empty();

        if ( !$el.attr('multiple') ) {
            $dropdownList.attr('radio', '')
        }

        if ( data.length === 0 ) {
            var $optionHtml = '<li>\
          <p>등록된 데이터가 없습니다.</p>\
        </li>';

            $dropdownList.append($optionHtml);
            return;
        }

        for (var i=0, l=data.length; i<l; i++) {
            var listData = data[i];
            var dataLabel = listData.label;
            var dataValue = listData.value;
            var isChecked = false;
            var isImage = "";
            if(nowImgSlide){
                if(listData.url!=undefined){
                    if(listData.url.length!=0){
                        isImage = "icon_img"
                    }
                }
            }

            for(var ci=0, cl=checkData.length; ci<cl; ci++){
                try{
                    var checkValue = checkData[ci].value;

                    if(dataValue === checkValue){
                        isChecked = true;
                    }
                }
                catch (e) {}
            }

            var checkedAttr = isChecked ? 'checked="checked"' : '';
            var $optionHtml = '<li>\
          <label>\
            <input type="checkbox" '+ checkedAttr + 'data-value="' + dataValue +'" class="checkbox" value="'+dataValue+'">\
            <span class="label '+isImage+'">' + dataLabel + '</span>\
          </label>\
        </li>';

            $dropdownList.append($optionHtml);
            if(nowImgSlide)$dropdownList.addClass("carousel");
        }
    }

    function renderSelectLabel(element){ //element에서 셀렉트 된 label text를 p에 표시
        var labelBox = element.find('p.select');
        var labels = '';
        let nowSelectedIndex = [];
        let index = 0;



        $dropdown.find('.select_list li').each(function(){
            var isChecked = $(this).find('input[type="checkbox"]').prop('checked');
            var label = $(this).find('label').text();
            if ( isChecked ) {
                if ( !element.attr('multiple') ) { // 멀티 셀렉트가 아닌 경우
                    labels = label;
                    nowSelectedIndex.push(index)
                } else { // 멀티 셀렉트인 경우
                    labels += label + ', ';
                    nowSelectedIndex.push(index)
                }
            }
            index += 1;
        });
        if(labels.charAt(labels.length-2)==","){
            labels = labels.substring(0,labels.length-2)
        }

        if(element.selector.toString()=="#img_slide"){

        }else if(element.selector.toString()=="#button"){

        }else{
            labelBox.empty();
            labelBox.text(labels);
        }
        return nowSelectedIndex;
    }
}
// 추가 AMR 201209 선택한 파일이름을 input value에 치환
function setFileName() {
    var target = event.target;
    var fileName = target.files[0].name;
    var targetId = $(target).attr('id');
    var inputText = $('body').find('input[name="'+ targetId +'"]');
    inputText.val(fileName); //target id와 같은 name을 가진 input[text]에 적용
    if (target.files && target.files[0]) {
        var reader = new FileReader();

        reader.onload = function (e) {
            var imgUrl = e.target.result;

            var imgHtml = '<img src="' + imgUrl + '" alt="이미지" style="display: block; max-width: 710px; max-height: 400px; width: auto; height: auto;">'
            $(".image_setting").html(imgHtml);
            $(".choose_img").css('display','none');



            $('#carousel_image_setting > div.lyr_mid > div > img').on('rcrop-changed', function(){
                nowCropSrc = $(this).rcrop('getDataURL');
                let nowVal = $(this).rcrop('getValues');
                let nowWidth = nowVal.width;
                let nowHeight = nowVal.height;
                let nowRatioWidth = nowWidth;
                let nowRatioHeight = nowHeight;
                for(let ii = 2; ii<=nowWidth && ii<=nowHeight; ii++){
                    if (nowRatioWidth%ii == 0 && nowRatioHeight%ii==0){
                        nowRatioWidth /= ii;
                        nowRatioHeight /= ii;
                    }
                }
                let nowStr = "현재 " + nowWidth + "x" + nowHeight + "(" + nowRatioWidth + ":" + nowRatioHeight + ")";
                $("#size_info").text(nowStr);
            })
            $('#carousel_image_setting > div.lyr_mid > div > img').on('rcrop-ready', function(){
                nowCropSrc = $(this).rcrop('getDataURL');
                let nowVal = $(this).rcrop('getValues');
                let nowWidth = nowVal.width;
                let nowHeight = nowVal.height;
                let nowRatioWidth = nowWidth;
                let nowRatioHeight = nowHeight;
                for(let ii = 2; ii<=nowWidth && ii<=nowHeight; ii++){
                    if (nowRatioWidth%ii == 0 && nowRatioHeight%ii==0){
                        nowRatioWidth /= ii;
                        nowRatioHeight /= ii;
                    }
                }
                let nowStr = "현재 " + nowWidth + "x" + nowHeight + "(" + nowRatioWidth + ":" + nowRatioHeight + ")";
                $("#size_info").text(nowStr);

                $(".rcrop-wrapper").css("position","absolute");
                $(".rcrop-wrapper").css("top","50%");
                $(".rcrop-wrapper").css("left","50%");
                $(".rcrop-wrapper").css("transform","translate(-50%, -50%)");
            })
            $("#carousel_image_setting > div.lyr_mid > div > img").rcrop({
                minSize : [6,5],
                preserveAspectRatio : false,
            });


            $("#carousel_image_setting > div.lyr_mid > div > div.rcrop-preview-wrapper > canvas").css("display","none");//미리보기창
            $("#carousel_image_setting > div.lyr_mid > div").css("text-align","center");

        }

        reader.readAsDataURL(target.files[0]);
    }
}

$(document).ready(function () {
    var host = $('#chatListUl>li.active').val();
    if(hostToPage==-1 || hostToPage==undefined || hostToPage==NaN){chatClick(host, 1);}
    else{chatClick(hostToPage, 1);}

    $('.btn_lyr_upload_open').on('click', function () {
        openChatUpload();
    });

    $('input[type="file"]').change(function (e) {
        var fileName = e.target.files[0].name;
        $('input[name="excel_file_name"]').val(fileName);
    });

    // AMR 돋보기 아이콘이 있는 검색 영역에서 엔터 시 검색버튼 눌림
    $('.ipt_txt.search').on('keyup', function(){
        if (event.keyCode == 13) {
            $(this).next('.btn_search').trigger('click');
        }
    });

    // AMR 테이블 페이지네이션 페이지 active
    // .page 클릭 시
    $('.tbl_wrap .pagination .page').on('click', function(){
        var pages = $('.pagination .pages .page');
        var thisPage = $(this);
        pages.removeClass('active');
        thisPage.addClass('active');
    });
    // 버튼 클릭 시
    $('.tbl_wrap .pagination > button').on('click', function(){
        var thisButton = $(this);
        var pages = $('.pagination .pages .page');
        var activePage = $('.pagination .pages .page.active');
        var thisPage = pages.index(activePage);
        var firstPage = 0;
        var lastPage = pages.length - 1;
        var prevPage = thisPage - 1;
        var nextPage = thisPage + 1;

        pages.removeClass('active');

        if (thisButton.hasClass('first')) {
            pages.eq(firstPage).addClass('active');
            return;
        }

        if (thisButton.hasClass('prev')) {
            if (thisPage === 0 ) {
                prevPage = 0;
            }
            pages.eq(prevPage).addClass('active');
            return;
        }

        if (thisButton.hasClass('next')) {
            if (thisPage === lastPage ) {
                nextPage = lastPage;
            }
            pages.eq(nextPage).addClass('active');
            return;
        }

        if (thisButton.hasClass('last')) {
            pages.eq(lastPage).addClass('active');
        }
    });


    // 챗봇빌더 답변 테이블 내 상세, 삭제 버튼 클릭 시 해당 col active
    $('.chatbot_builder table .btn_line_primary, .chatbot_builder table .btn_line_warning').on('click', function(){
        var table = $(this).parents('table');
        var tableCol = table.find('tr');
        var thisCol = $(this).parents('tr');
        tableCol.removeClass('active');
        thisCol.addClass('active');
    });

    // AMR 챗봇빌더 챗봇 테스트 열기닫기
    function handleChatbotTestOnOff() {
        var btnOn = $('.chatbot_builder .btn_test_on');
        var btnOff = $('.chatbot_builder .btn_test_off');

        btnOff.on('click', function(){
            $('.chatbot_builder').removeClass('test_on');
            btnOn.removeClass('hide');
        });
    }
    handleChatbotTestOnOff();

    // 드롭다운 박스 열기 닫기
    $('.dl_dropdown dt').on('click', function(){
        var parent = $(this).parent('.dl_dropdown');
        parent.toggleClass('active');
    });

    // tbl_commands 버튼으로 체크한 테이블 로우 지우기
    $('.btn_remove_row').on('click', function(){
        var $commands = $(this).parents('.tbl_commands');
        var $table = $commands.next();
        var $tr = $table.find('tbody tr');

        $tr.each(function(){
            $thisTr = $(this);
            $thisCheckbox = $thisTr.find('td').eq(0).find('input[type="checkbox"]');

            if ( $thisCheckbox.prop('checked') ) {
                $thisTr.remove();
            }
        });
    });
});

<!-- 개별 js -->

resetIntents()
// swiper
function applySwiper (selector, option) {
    var defaultOption = {
        initialSlide : 0, //처음으로 보여질 슬라이드 index
        speed : 200,
        slidesPerView:2,
        spaceBetween: 10,
        centeredSlides: false,
        loop: true,
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
applySwiper($('.botMsg_swiper'), {initialSlide : 0}); //처음으로 보여질 슬라이드 index 수정

// AMR 이미지 카드 미리보기
function changeImageCard(input) {
    startCropWindow()
}

// AMR 이미지 카드 확정에 미리보기 반영하기
function confirmImageCard() {
    var $this = $(event.target);
    var $imgCardPreview = $this.prev();
    var $imgCardConfirm = $this.next();

    var src = $imgCardPreview.find('img').attr('src');
    var title = $imgCardPreview.find('.item_tit input').val();
    var text = $imgCardPreview.find('.item_txt textarea').val();

    $imgCardConfirm.find('img').attr('src', src);
    $imgCardConfirm.find('.item_tit').text(title);
    $imgCardConfirm.find('.item_txt').text(text);

    if($this.parent().parent().parent().parent().attr('id').toString().trim()==="sec_ipt"){
        multiLangDetail[nowDetailSearchLang].addedDescription = text;
    }


    if(nowCropSrc!=undefined){
        nowImgUploaded = true;
    }

    sec_Images_synchronize();

}

//AMR 카드에 URL 넣기
function urlToAMR(url, titleT, textT){
    let imgHtmlStr = ""
    let toggleBoolean = false;
    try{
      if(url.length>0){
        let fileInput = $("#cicLabel")
        imgHtmlStr = '<img src="' + url + '" alt="백그라운드 이미지">'
        if ( fileInput.prev().length === 0 ) {
          fileInput.parent().prepend(imgHtmlStr);
          toggleBoolean = true;
        }
        if(toggleBoolean==false){
          fileInput.prev().attr('src', url);
        }
      }
      imgHtmlStr = "<p class=\"info_text\">확정</p>\n" +
          "                                <div class=\"swiper_item\">\n" +
          "                                    <span class=\"item_img\"><img src=\""+(url.length > 0 ? url : "../images/img_nopicture.png")+"\"></span>\n" +
          "                                    <span class=\"item_tit\">"+titleT+"</span>\n" +
          "                                    <span class=\"item_txt\">"+textT+"</span>\n" +
          "                                </div>"
    }catch (e) {
      imgHtmlStr = "<p class=\"info_text\">확정</p>\n" +
          "                                <div class=\"swiper_item\">\n" +
          "                                    <span class=\"item_img\"><img src=\""+"../images/img_nopicture.png"+"\"></span>\n" +
          "                                    <span class=\"item_tit\">"+titleT+"</span>\n" +
          "                                    <span class=\"item_txt\">"+textT+"</span>\n" +
          "                                </div>"
    }

    $("#fixed_swiper_item").empty();
    $("#fixed_swiper_item").append(imgHtmlStr);
}

function answerPreview(e) {
    let nowPrevVal = e.target.value.toString();
    if(nowPrevVal.substring(nowPrevVal.length-1,nowPrevVal.length)=="\n"){nowPrevVal += "\n"}
    $('#answerPreview').html(replaceAnswerPreview(nowPrevVal));
    delBlank();
    var txt_btn = $('#answerPreview').find('a');
    var txt_btnLength = $('#answerPreview').find('a').length;
    if (txt_btnLength > 1) {
        $('#answerPreview').find('a').attr('href', '#');
        $('#answerPreview').find('a').wrap('<div class="txt_btns"></div>');
    } else {
        $('#answerPreview').find('a').attr('href', '#');
        $('#answerPreview').find('a').wrapAll('<div class="txt_btn"></div>');
    }
    calcAnswerHeight(e);
}

function addRowChatbotTask() {
    var tblChatbotTask = document.getElementById('tblChatbotTask');
    var tableBody = tblChatbotTask.querySelector('tbody');
    var clonedNode = $($('#tempChatbotTask').html())[0]; //template (ie 호환위해 table부터 작성하고 tr만 가져옴)
    var $tableRow = $(clonedNode.getElementsByTagName('tr'));

    $tableRow.appendTo(tableBody);

    createMultiSelect($tableRow.find('.fast_popper_select').eq(0), taskData1, []);
    createMultiSelect($tableRow.find('.fast_popper_select').eq(1), taskData2, []);
}

function goPage(host, cp){
    if( cp != 0){
        $('#currentPage').val(cp);
    }
    chatClick(host, cp);
}


function iptConstruct(modifyOnOff){
    imgSearchKeyword        = "";
    btnSearchKeyword        = "";
    prevSearchKeyword       = "";
    bertIntentKeyword       = "";

    let isFirstTask = false; //처음으로 태스크
    let firstIntentText = "    <div class=\"fr\">\n" +
        "        <input type=\"checkbox\" id=\"first_intent_checkbox\" class=\"checkbox\" checked=\"checked\"  onclick=\"return false;\">\n" +
        "        <label for=\"first_intent_checkbox\" id='ori_info'>챗봇 시작시 등장하는 태스크입니다.</label>\n" +
        "    </div>\n";
    let taskInnerHtml = '';
    
    if(detail !== undefined) { //detail 이 undefined 면 태스크 추가 팝업
      if (detail.Main==="처음으로") {
        isFirstTask = true;
      }
    }

    if(!modifyOnOff){ //태스크 추가
        buttonSelectedData      = [];
        imgSelectedData         = [];
        singleSelectedData1     = [];
        singleSelectedData2     = [];
        taskInnerHtml = "<input type=\"text\" class=\"ipt_txt\" onchange=\"changeMain(this.value)\" id=\"task_input_name\" autocomplete=\"off\">"

    } else { //태스크 상세 보기
        taskInnerHtml = "<span id=\"task_input_name\" class=\"ipt_txt\" value='" + detail.Main + "' style=\"line-height: 28px;\">" + detail.Main + "</span>";
    }

    ansDetailLang();
    let nowSortableHtml = "";
    for(let i = 0; i<nowContentsOrderList.length; i++){
      if(nowContentsOrderList[i]=='0'){
        nowSortableHtml += "                <dl class=\"order_list\">\n" +
            "                    <dt>캐로셀\n" +
            "                        <div class=\"help\">?\n" +
            "                            <div class=\"help_desc\"><i>선택한 슬라이드를 대답합니다.</i><br>ex)\n" +
            "                                <div class=\"image\"><img src=\"../images/help_img_slide.svg\" alt=\"슬라이드 이미지 예\"></div>\n" +
            "                            </div>\n" +
            "                        </div>\n" +
            "                    </dt>\n" +
            "                    <dd>\n" +
            "                        <div id=\"img_slide\" class=\"fast_popper_select\" multiple  onclick=\"mappingSelectboxOnclick(this)\">\n" +
            "                            <p class=\"select text_ellipsis\"></p>\n" +
            "                            <div class=\"fast_popper_option\">\n" +
            "                                <div class=\"iptBox\">\n" +
            "                                    <input type=\"text\" class=\"ipt_txt search\">\n" +
            "                                    <button type=\"button\" class=\"btn_search\" onclick='imgSearch(this)'><span class=\"text_hide\" >검색하기</span></button>\n" +
            "                                </div>\n" +
            "                                <ul class=\"select_list scroll\"></ul>\n" +
            "                            </div>\n" +
            "                        </div>\n" +
            "                    </dd>\n" +
            "                </dl>\n";
      }if(nowContentsOrderList[i]=='1'){
        nowSortableHtml += "                <dl class=\"order_list\">\n" +
            "                    <dt>답변\n" +
            "                        <div class=\"help\">?\n" +
            "                            <div class=\"help_desc\"><i>작성한 문장을 대답합니다.</i><br>ex)\n" +
            "                                <div class=\"image\"><img src=\"../images/help_img_answer.svg\" alt=\"답변 이미지 예\"></div>\n" +
            "                            </div>\n" +
            "                        </div>\n" +
            "                    </dt>\n" +
            "                    <dd>\n" +
            "                        <textarea name=\"\" id=\"answer_area\" cols=\"10\" rows=\"2\" class=\"ipt_txt answer\" onfocus='handleAnswerHeight(event)' oninput=\"answerPreview(event)\" >" + (modifyOnOff ? (detail.answer!=undefined ? detail.answer.replace(/<br>/gi,"\n") : "") : "") +"</textarea>\n" +
            "                    </dd>\n" +
            "                </dl>\n"
      }if(nowContentsOrderList[i]=='2'){
        nowSortableHtml += "                <dl class=\"order_list\">\n" +
            "                     <dt>버튼\n" +
            "                        <div class=\"help\">?\n" +
            "                            <div class=\"help_desc\"><i>선택한 버튼을 디스플레이 명으로 대답합니다.<br>선택 리스트에는 태스크 명 (디스플레이 명) 으로 표현됩니다</i><br>ex)\n" +
            "                                <div class=\"image\"><img src=\"../images/help_img_button.svg\" alt=\"버튼 이미지 예\"></div>\n" +
            "                            </div>\n" +
            "                        </div>\n" +
            "                    </dt>\n" +
            "                    <dd>\n" +
            "                        <div id=\"button\" class=\"fast_popper_select\" multiple  onclick=\"mappingSelectboxOnclick(this)\">\n" +
            "                            <p class=\"select text_ellipsis\"></p>\n" +
            "                            <div class=\"fast_popper_option\">\n" +
            "                                <div class=\"iptBox\">\n" +
            "                                    <input type=\"text\" class=\"ipt_txt search\">\n" +
            "                                    <button type=\"button\" class=\"btn_search\" onclick='btnSearch(this)'><span class=\"text_hide\">검색하기</span></button>\n" +
            "                                </div>\n" +
            "                                <ul class=\"select_list scroll\"></ul>\n" +
            "                            </div>\n" +
            "                        </div>\n" +
            "                    </dd>\n" +
            "                </dl>\n";
      }
    }
    let returnInnerHtml = "<div class=\"lang_viewer korea\"></div>\n" +
        "<button type=\"button\" class=\"btn_line_primary btn_set_answer\" onclick=\"setAnswerModifyMode()\">답변 순서 설정</button>\n" +
        "                <dl>\n" +
        "                    <dt>태스크 명<div class=\"help\">?<div class=\"help_desc\"><i>답변 이름 ex)조식 가격, 코로나</i></div></div>\n" + (isFirstTask ? firstIntentText : '') + "</dt>"+
        "                    <dd>"+ taskInnerHtml +"</dd>\n" +
        "                </dl>\n" +
        "                <dl>\n" +
        "                    <dt>디스플레이 명<div class=\"help\">?<div class=\"help_desc\"><i>버튼으로 보여줄 때 표시할 이름<br>ex)조식 가격 안내, 코로나</i></div></div>\n" + "</dt>\n"+
        "                    <dd><input type=\"text\" class=\"ipt_txt\" value='"+ (modifyOnOff ? detail.Name : '') +"' onchange=\"changeDisplay(this.value)\" id=\"task_input_display\" autocomplete=\"off\"></dd>\n" +
        "                </dl>\n" +
        "                <div class=\"order_wrap\" id='sortable_wrap'>\n" + nowSortableHtml +
        "                </div>\n" +
        "               <dl class=\"dl_dropdown\">\n" +
        "                     <dt>이미지 카드<div class=\"help\">?<div class=\"help_desc\"><i>이미지 카드를 만들면 다른 태스크에서 이미지 슬라이드로 사용할 수 있습니다.</i></div></div>\n" +
        "                            <div class=\"btn_control fr\">\n" +
        "                            <div class=\"fold\">\n" +
        "                                <span>펼치기</span>\n" +
        "                                <svg width=\"15\" height=\"8\" viewBox=\"0 0 15 8\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
        "                                    <path d=\"M0 1.63966L1.20363 0.0423927L8.39135 5.45873L7.18772 7.056L0 1.63966Z\" fill=\"#97A1AB\"/>\n" +
        "                                    <path d=\"M13.2154 0L14.4191 1.59727L7.23133 7.01361L6.0277 5.41634L13.2154 0Z\" fill=\"#97A1AB\"/>\n" +
        "                                </svg>\n" +
        "                            </div>\n" +
        "                            <div class=\"unfold\">\n" +
        "                                <span>감추기</span>\n" +
        "                                <svg width=\"15\" height=\"8\" viewBox=\"0 0 15 8\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
        "                                    <path d=\"M14.4191 5.41634L13.2154 7.01361L6.02772 1.59727L7.23135 9.53674e-07L14.4191 5.41634Z\" fill=\"#97A1AB\"/>\n" +
        "                                    <path d=\"M1.20364 7.056L1.33514e-05 5.45873L7.18773 0.0423937L8.39136 1.63966L1.20364 7.056Z\" fill=\"#97A1AB\"/>\n" +
        "                                </svg>\n" +
        "                            </div>" +
        "                        </div>\n" +
        "                    </dt>\n" +
        "                    <dd>\n" +
        "                        <div class=\"setting_image_card\">\n" +
        "                            <div class=\"card\" id=\"prev_swiper_item\">\n" +
        "                                <p class=\"info_text\">미리보기</p>\n" +
        "                                <div class=\"swiper_item cb_img_delete\">\n" +
        "                                    <a class=\"btn_icon delete\" onclick='delImg()'>이미지 삭제</a>\n"+
        "                                    <span class=\"item_img\">\n<img src=\"../images/img_nopicture.png\">\n" +
        "                                      <label id=\"cicLabel\" onmouseover=\"handleChatBuilderImageDelete(event)\" onclick='changeImageCard(this)'>+</label>\n" +
        "                                    </span>" +
        "                                    <span class=\"item_tit\"><input type=\"text\" id=\"title_text_input\" placeholder=\"타이틀\" onchange=\"changeDisplay(this.value)\"></span>\n" +
        "                                    <span class=\"item_txt\"><textarea name=\"\" id=\"item_txt_description\" cols=\"30\" rows=\"4\" placeholder=\"내용\"></textarea></span>\n" +
        "                                </div>\n" +
        "                            </div>\n" +
        "                            <button type=\"button\" class=\"btn_secondary\"  onclick=\"confirmImageCard(this)\" id=\"AMR_apply\">적용 &raquo;</button>\n" +
        "                            <div class=\"card\" id=\"fixed_swiper_item\">\n" +
        "                                <p class=\"info_text\">확정</p>\n" +
        "                                <div class=\"swiper_item\">\n" +
        "                                    <span class=\"item_img\"><img src=\"../images/img_nopicture.png\"></span>\n" +
        "                                    <span class=\"item_tit\"></span>\n" +
        "                                    <span class=\"item_txt\"></span>\n" +
        "                                </div>\n" +
        "                            </div>\n" +
        "                        </div>\n" +
        "                    </dd>\n" +
        "                </dl>\n" +
        "                <dl>\n" +
        "                    <dt>태스크 관계\n" +
        "                        <div class=\"help\">?\n" +
        "                            <div class=\"help_desc\" style=\"width: 600px;\">\n" +
        "                                <i>현재 태스크와 연결된 이전 태스크, 의도와의 관계</i>\n" +
        "                                <div class=\"float_box\">\n" +
        "                                    <div class=\"fl\">\n" +
        "                                        <i>ex)<br>\"조식\"에 대해 문의한 다음, \"가격 문의\"를 물어보면 \"조식 가격\"에 대해서 답변한다.<br>현재 태스크 명: 조식 가격 |\n" +
        "                                            이전태스크: 조식 | 의도: 가격 문의</i>\n" +
        "                                    </div>\n" +
        "                                    <div class=\"image fr\"><img src=\"../images/help_img_task.svg\" alt=\"태스크 관계 이미지 예\"></div>\n" +
        "                                </div>\n" +
        "\n" +
        "                                <div class=\"float_box\">\n" +
        "                                    <div class=\"fl\">\n" +
        "                                        <i>ex)<br>이전에 문의한 내용과 상관 없이 \"코로나\"를 물어보면 \"코로나\"에 대해서 답변한다.<br>현재 태스크 명: 코로나 |\n" +
        "                                            이전태스크: * | 의도: 코로나</i>\n" +
        "                                    </div>\n" +
        "                                    <div class=\"image fr\"><img src=\"../images/help_img_task01.svg\" alt=\"태스크 관계 이미지 예\">\n" +
        "                                    </div>\n" +
        "                                </div>\n" +
        "                            </div>\n" +
        "                        </div>\n" +
        "                    </dt>\n" +
        "                    <dd>\n" +
        "                        <div class=\"tbl_commands\">\n" +
        "                            <div class=\"fr\">\n" +
        "                                <button type=\"button\" class=\"btn_secondary btn_remove_row\">선택 삭제</button>\n" +
        "                                <button type=\"button\" class=\"btn_secondary\" onclick=\"addRowChatbotTask(event)\">관계 추가</button>\n" +
        "                            </div>\n" +
        "                        </div>\n" +
        "                        <div class=\"tbl_wrap scroll\">\n" +
        "                            <table id=\"tblChatbotTask\" class=\"tbl_box_lst\">\n" +
        "                                <colgroup>\n" +
        "                                    <col style=\"width: 40px;\"><col><col>\n" +
        "                                </colgroup>\n" +
        "                                <thead>\n" +
        "                                <tr>\n" +
        "                                    <th></th>\n" +
        "                                    <th>이전테스크</th>\n" +
        "                                    <th>의도</th>\n" +
        "                                </tr>\n" +
        "                                </thead>\n" +
        "                                <tbody>\n" +
        "                                <tr>\n" +
        "                                    <td>\n" +
        "                                        <label>\n" +
        "                                            <input type=\"checkbox\" class=\"checkbox\">\n" +
        "                                            <span class=\"label\"></span>\n" +
        "                                        </label>\n" +
        "                                    </td>\n" +
        "                                    <td>\n" +
        "                                        <div class=\"fast_popper_select single_test1\"  onclick=\"mappingSelectboxOnclick(this)\">\n" +
        "                                            <p class=\"select text_ellipsis\"></p>\n" +
        "                                            <div class=\"fast_popper_option\">\n" +
        "                                                <div class=\"iptBox\">\n" +
        "                                                    <input type=\"text\" class=\"ipt_txt search\">\n" +
        "                                                    <button type=\"button\" class=\"btn_search\"><span class=\"text_hide\">검색하기</span></button>\n" +
        "                                                </div>\n" +
        "                                                <ul class=\"select_list scroll\"></ul>\n" +
        "                                            </div>\n" +
        "                                        </div>\n" +
        "                                    </td>\n" +
        "                                    <td>\n" +
        "                                        <div class=\"fast_popper_select single_test2\"  onclick=\"mappingSelectboxOnclick(this)\">\n" +
        "                                            <p class=\"select text_ellipsis\"></p>\n" +
        "                                            <div class=\"fast_popper_option\">\n" +
        "                                                <div class=\"iptBox\">\n" +
        "                                                    <input type=\"text\" class=\"ipt_txt search\">\n" +
        "                                                    <button type=\"button\" class=\"btn_search\"><span class=\"text_hide\">검색하기</span></button>\n" +
        "                                                </div>\n" +
        "                                                <ul class=\"select_list scroll\"></ul>\n" +
        "                                            </div>\n" +
        "                                        </div>\n" +
        "                                    </td>\n" +
        "                                </tr>\n" +
        "                                </tbody>\n" +
        "                            </table>\n" +
        "                            <template id=\"tempChatbotTask\">\n" +
        "                                <table>\n" +
        "                                    <tbody>\n" +
        "                                    <tr>\n" +
        "                                        <td>\n" +
        "                                            <label>\n" +
        "                                                <input type=\"checkbox\" class=\"checkbox\">\n" +
        "                                                <span class=\"label\"></span>\n" +
        "                                            </label>\n" +
        "                                        </td>\n" +
        "                                        <td>\n" +
        "                                            <div class=\"fast_popper_select\"  onclick=\"mappingSelectboxOnclick(this)\">\n" +
        "                                                <p class=\"select text_ellipsis\"></p>\n" +
        "                                                <div class=\"fast_popper_option\">\n" +
        "                                                    <div class=\"iptBox\">\n" +
        "                                                        <input type=\"text\" class=\"ipt_txt search\">\n" +
        "                                                        <button type=\"button\" class=\"btn_search\" onclick='prevIntentSearch(this)'><span class=\"text_hide\">검색하기</span></button>\n" +
        "                                                    </div>\n" +
        "                                                    <ul class=\"select_list scroll\"></ul>\n" +
        "                                                </div>\n" +
        "                                            </div>\n" +
        "                                        </td>\n" +
        "                                        <td>\n" +
        "                                            <div class=\"fast_popper_select\"  onclick=\"mappingSelectboxOnclick(this)\">\n" +
        "                                                <p class=\"select text_ellipsis\"></p>\n" +
        "                                                <div class=\"fast_popper_option\">\n" +
        "                                                    <div class=\"iptBox\">\n" +
        "                                                        <input type=\"text\" class=\"ipt_txt search\">\n" +
        "                                                        <button type=\"button\" class=\"btn_search\" onclick='bertIntentSearch(this)'><span class=\"text_hide\">검색하기</span></button>\n" +
        "                                                    </div>\n" +
        "                                                    <ul class=\"select_list scroll\"></ul>\n" +
        "                                                </div>\n" +
        "                                            </div>\n" +
        "                                        </td>\n" +
        "                                    </tr>\n" +
        "                                    </tbody>\n" +
        "                                </table>\n" +
        "                            </template>\n" +
        "                        </div>\n" +
        "                    </dd>\n" +
        "                </dl>";
    return returnInnerHtml;
}

function generateBtnItem(){
    let retHtml = "<ul>\n"
    for (let ii = 0; ii<buttonSelectedData.length; ii++){
        if(buttonSelectedData[ii].name !== '처음으로'){
            retHtml += "<li><a class=\"intent\" href=\"#\" data-display=\"" +buttonSelectedData[ii].name+"\" data-intent=\""+buttonSelectedData[ii].name+"\">"+buttonSelectedData[ii].name+"</a></li>";
        }
    }
    if(buttonSelectedData.length>0){
        try{
            let firstBut = buttonData.find(isFirst);
            retHtml += "<li><a class=\"intent btnStart\" href=\"#\" data-display=\""+firstBut.name+"\" data-intent=\""+firstBut.name+"\">"+firstBut.name+"</a></li></ul>"
        }catch (e) {
            retHtml += "<li><a class=\"intent btnStart\" href=\"#\" data-display=\"처음으로\" data-intent=\"처음으로\">처음으로</a></li></ul>"
        }
    }
    $(".btnItem").empty();
    $(".btnItem").append(retHtml);
}
function isFirst(element)  {
    if(element.main === '처음으로')  {
        return true;
    }
}

function syncSelectBox(nextString){
    buttonSelectedData = []
    imgSelectedData = []
    let nowNextList = nextString.split(",")
    for(let ii = 0; ii<nowNextList.length; ii++){
        if(nowNextList[ii].charAt(0)=='B'){
            let nowButton = nowNextList[ii].replace("B","")
            for(let tt = 0; tt<buttonData.length; tt++){
                if(buttonData[tt].value == nowButton && buttonData[tt].main!='처음으로 '){
                    buttonSelectedData.push(buttonData[tt]);
                    break;
                }
            }
        }else if(nowNextList[ii].charAt(0)=='I'){
            let nowImg = nowNextList[ii].replace("I","")
            for(let tt = 0; tt<imgData.length; tt++){
                if(imgData[tt].value == nowImg){
                    imgSelectedData.push(imgData[tt]);
                    break;
                }
            }
        }else{
            try{
                if(nowNextList[ii].length==0){
                    // Next가 비어있다면 들어오는 위치
                }
            }catch(e){
                console.log("Error Next : " + nowNextList[ii])
            }
        }
    }
}


function armDescription(str){
    $("#item_txt_description").empty();
    $("#item_txt_description").append(str);
}

function changeMain(str){
    $("#task_input_name").val(str);
}

function changeDisplay(str){
    $("#task_input_display").val(str);
    $("#title_text_input").val(str);
}

function generateImgCarousel(){
    clearTimeout(imgCarTimer);
    imgCarTimer = setTimeout(function() {
        generateImgCarouselTimerFunc()
    }, 100);

}

function generateImgCarouselTimerFunc(){
    let toCarStr = "";
    for(let ii = 0; ii<imgSelectedData.length; ii++){
        toCarStr += imgSelectedData[ii].value;
        if(ii!=imgSelectedData.length-1){
            toCarStr += ",";
        }
    }
    var obj = new Object();
    obj.carStr = toCarStr;
    addAjax();
    $.ajax({
        url : "getImageCarousel",
        data : JSON.stringify(obj),
        type: "POST",
        contentType: 'application/json'
    }).success(
        function(result) {
            finAjax();
            let answerDetailResult = result.answerList;
            let detailResult = [];
            for(let i = 0; i<imgSelectedData.length; i++){
                for(let j = 0; j<answerDetailResult.length; j++){
                    if(imgSelectedData[i].value === answerDetailResult[j].No){
                        detailResult.push(answerDetailResult[j]);
                    }
                }
            }

            let putPreviewStr = "";
            if(detailResult!=undefined){
                if(detailResult.length!=imgSelectedData.length){
                    return;
                }
                else if(detailResult.length>0){
                    putPreviewStr += "<div class=\"swiper-wrapper\">"
                    for(let ii = 0; ii<detailResult.length; ii++){
                        putPreviewStr += "<div class=\"swiper-slide\">\n" +
                            "                        <a class=\"swiper_item\">\n" +
                            "                            <span class=\"item_img\"><img src=\"" + (detailResult[ii].URL.length>3 ? detailResult[ii].URL : "../images/img_nopicture.png") + "\"></span>\n" +
                            "                            <span class=\"item_tit\">"+detailResult[ii].Name+"</span>\n" +
                            "                            <span class=\"item_txt\">"+detailResult[ii].Description+"</span>\n" +
                            "                        </a>\n" +
                            "                    </div>";
                    }
                    putPreviewStr += "</div>\n" +
                        "                <div class=\"swiper-pagination\"></div>\n" +
                        "                <div class=\"swiper-button-prev\"></div>\n" +
                        "                <div class=\"swiper-button-next\"></div>"
                }
            }
            $("#chat_answer_setting > div.lyr_mid > ul > li.bot.botMsg_swiper.swiper-container").empty();
            $("#chat_answer_setting > div.lyr_mid > ul > li.bot.botMsg_swiper.swiper-container").append(putPreviewStr);

            if(detailResult!=undefined){
                if(detailResult.length==1){
                    applySwiper($('.botMsg_swiper'), {initialSlide : 0, loop:false});
                }if(detailResult.length>1){
                    applySwiper($('.botMsg_swiper'), {initialSlide : 0});
                }
            }

        }).fail(function(result) {
        finAjax();
        console.log("getImageCarousel error");
    });
}

function multiSelectReset(){
    createMultiSelect($('#img_slide'), imgData, imgSelectedData);
    createMultiSelect($('#button'), buttonData, buttonSelectedData);
    createMultiSelect($('.single_test1'), taskData1, singleSelectedData1);
    createMultiSelect($('.single_test2'), taskData2, singleSelectedData2);
}


function imgSearch(e){
    let nowListCount = 0;
    imgSearchKeyword = $(e).prev()[0].value;
    let $liList = $(e).parent().next().find('li')
    $liList.each(function(){
        let $thisli = $(this);
        let nowText = $thisli.find('label').find('span').text();
        if(nowText.indexOf(imgSearchKeyword)==-1){
            $thisli.css('display', 'none');
        }else{
            nowListCount++;
            $thisli.css('display', 'block');
        }
    });
    if(nowListCount==0){
        $("#noResultBox").remove();
        var $optionHtml = '<li id="noResultBox">\
          <p>등록된 데이터가 없습니다.</p>\
        </li>';
        $(e).parent().next().append($optionHtml);
    }
}

function btnSearch(e){
    let nowListCount = 0;
    btnSearchKeyword = $(e).prev()[0].value;
    let $liList = $(e).parent().next().find('li')
    $liList.each(function(){
        let $thisli = $(this);
        let nowText = $thisli.find('label').find('span').text();
        if(nowText.indexOf(btnSearchKeyword)==-1){
            $thisli.css('display', 'none');
        }else{
            nowListCount++;
            $thisli.css('display', 'block');
        }
    });
    if(nowListCount==0){
        $("#noResultBox").remove();
        var $optionHtml = '<li id="noResultBox">\
          <p>등록된 데이터가 없습니다.</p>\
        </li>';
        $(e).parent().next().append($optionHtml);
    }
}

function prevIntentSearch(e){
    let nowListCount = 0;
    prevSearchKeyword = $(e).prev()[0].value;
    let $liList = $(e).parent().next().find('li')
    $liList.each(function(){
        let $thisli = $(this);
        let nowText = $thisli.find('label').find('span').text();
        if(nowText.indexOf(prevSearchKeyword)==-1){
            $thisli.css('display', 'none');
        }else{
            nowListCount++;
            $thisli.css('display', 'block');
        }
    });
    if(nowListCount==0){
        $("#noResultBox").remove();
        var $optionHtml = '<li id="noResultBox">\
          <p>등록된 데이터가 없습니다.</p>\
        </li>';
        $(e).parent().next().append($optionHtml);
    }
}

function bertIntentSearch(e){
    let nowListCount = 0;
    bertIntentKeyword = $(e).prev()[0].value;
    let $liList = $(e).parent().next().find('li')
    $liList.each(function(){
        let $thisli = $(this);
        let nowText = $thisli.find('label').find('span').text();
        if(nowText.indexOf(bertIntentKeyword)==-1){
            $thisli.css('display', 'none');
        }else{
            nowListCount++;
            $thisli.css('display', 'block');
        }
    });
    if(nowListCount==0){
        $("#noResultBox").remove();
        var $optionHtml = '<li id="noResultBox"><span class="label new" onclick="newBertIntent(\''+bertIntentKeyword+'\')">의도 추가 : <em>'+bertIntentKeyword+'</em></span></li>';
        $(e).parent().next().append($optionHtml);
    }
}

function generateBertIntentList(bertIntentList){
    singleSelectedData1     = [];
    singleSelectedData2     = [];
    for(let ii =0; ii<bertIntentList.length; ii++){
        try{
            let nowRel = bertIntentList[ii]
            if(nowRel.SrcIntent==0){var nowAll = true;}
            else {
                var nowAll = false;
                var srcIndex = taskData1.find((item,idx) => {return item.value===nowRel.SrcIntent;});
            }
            var brtIndex = taskData2.find((item,idx) => {return item.value===nowRel.BertIntent;});
            if(nowAll) singleSelectedData1.push({'label':"항상",'value':nowRel.SrcIntent});
            else singleSelectedData1.push({'label':srcIndex.label,'value':nowRel.SrcIntent});
            singleSelectedData2.push({'label':brtIndex.label,'value':nowRel.BertIntent})
        }catch (e) {
            console.log("Unexpected input : " + e)
            console.log(bertIntentList[ii])
        }
    }
}

function generateBertIntentListRender(){
    var $tr = $("#tblChatbotTask").find('tbody tr');
    $tr.each(function(){
        $thisTr = $(this);
        $thisTr.remove();
    });
    for(let i = 0; i<singleSelectedData1.length; i++){
        var tblChatbotTask = document.getElementById('tblChatbotTask');
        var tableBody = tblChatbotTask.querySelector('tbody');
        var clonedNode = $($('#tempChatbotTask').html())[0]; //template (ie 호환위해 table부터 작성하고 tr만 가져옴)
        var $tableRow = $(clonedNode.getElementsByTagName('tr'));
        $tableRow.appendTo(tableBody);
        createMultiSelect($tableRow.find('.fast_popper_select').eq(0), taskData1, [singleSelectedData1[i]]);
        createMultiSelect($tableRow.find('.fast_popper_select').eq(1), taskData2, [singleSelectedData2[i]]);
    }
}

function mappingSelectboxOnclick(obj){
    var $lili = $('body').find('.select_list');
    $lili.each(function(){
        var $thisLi = $(this);
        var $lilili = $thisLi.find('li')
        $lilili.each(function () {
                var $lililili = $(this)
                $lililili.css('display', 'block')
            }
        )
    });
    var $textbox = $('.fast_popper_option.active').find(".iptBox").find(".ipt_txt.search");
    $('#noResultBox').remove();
    $textbox.val("");
}

function onclickReset(){
    // 수정 201209 AMR Layer popup open (서브모달 추가)
    $('.btn_lyr_open').on('click', function () {
        var winHeight = $(window).height() * 0.7,
            hrefId = $(this).attr('href');
        thisLayer = $(hrefId);

        if ( thisLayer.hasClass('sub_lyr') ) {
            thisLayer.wrap('<div class="lyrWrap"></div>');
            thisLayer.before('<div class="lyr_bg"></div>');
            thisLayer.prev('.lyr_bg').css({'background': 'rgba(0,0,0,0.1)'});
        } else {
            $('body').css('overflow', 'hidden');
            thisLayer.wrap('<div class="lyrWrap"></div>');
            thisLayer.before('<div class="lyr_bg"></div>');
        }

        //대화 UI
        $('.lyrBox .lyr_mid').each(function () {
            $(this).css('max-height', Math.floor(winHeight) + 'px');
        });
    });

    // 수정 201209 AMR Layer popup close btn_close click
    $('.btn_lyr_close').on('click', function (e) {
        hrefId = $(this).parents('.lyrBox').attr('id');
        var id = '#' + hrefId;
        thisLayer = $(id);
        e.stopPropagation();
        $(".sub_lyr").css('display', 'none');
        lyrClose(thisLayer);
        $("#carousel_image_setting").css("display","none");
        delPopper();
    });

    // 확인버튼
    $('.btn_lyr_close_submit').prop("onclick", null).off("click");
    $('#btn_setting_lyr_close_id').prop("onclick", null).off("click");
    $('.btn_lyr_close_submit').on('click', function (e) {
        confirmAnswer($(this),e);
    });
    $('#btn_setting_lyr_close_id').on('click', function (e) {
        exitCropPage();
    });

    // 추가 201209 AMR lyrClose
    function lyrClose(target) {
        var lyrWrap = $('.lyrWrap');
        if (lyrWrap.length) {
            target.unwrap('<div class="lyrWrap"></div>');
        }
        $("#chat_answer_setting").click();
        target.prev('.lyr_bg').remove();

        if ( !target.hasClass('sub_lyr') ) {
            $('body').css('overflow', '');
        }
    }

    // AMR 돋보기 아이콘이 있는 검색 영역에서 엔터 시 검색버튼 눌림
    $('.ipt_txt.search').on('keyup', function(){
        if (event.keyCode == 13) {
            $(this).next('.btn_search').trigger('click');
        }
    });

    // AMR 테이블 페이지네이션 페이지 active
    // .page 클릭 시
    $('.tbl_wrap .pagination .page').on('click', function(){
        var pages = $('.pagination .pages .page');
        var thisPage = $(this);
        pages.removeClass('active');
        thisPage.addClass('active');
    });
    // 버튼 클릭 시
    $('.tbl_wrap .pagination > button').on('click', function(){
        var thisButton = $(this);
        var pages = $('.pagination .pages .page');
        var activePage = $('.pagination .pages .page.active');
        var thisPage = pages.index(activePage);
        var firstPage = 0;
        var lastPage = pages.length - 1;
        var prevPage = thisPage - 1;
        var nextPage = thisPage + 1;

        pages.removeClass('active');

        if (thisButton.hasClass('first')) {
            pages.eq(firstPage).addClass('active');
            return;
        }

        if (thisButton.hasClass('prev')) {
            if (thisPage === 0 ) {
                prevPage = 0;
            }
            pages.eq(prevPage).addClass('active');
            return;
        }

        if (thisButton.hasClass('next')) {
            if (thisPage === lastPage ) {
                nextPage = lastPage;
            }
            pages.eq(nextPage).addClass('active');
            return;
        }

        if (thisButton.hasClass('last')) {
            pages.eq(lastPage).addClass('active');
        }
    });

    // 챗봇빌더 답변 테이블 내 상세, 삭제 버튼 클릭 시 해당 col active
    $('.chatbot_builder table .btn_line_primary, .chatbot_builder table .btn_line_warning').on('click', function(){
        var table = $(this).parents('table');
        var tableCol = table.find('tr');
        var thisCol = $(this).parents('tr');
        tableCol.removeClass('active');
        thisCol.addClass('active');
    });

    // AMR 챗봇빌더 챗봇 테스트 열기닫기
    function handleChatbotTestOnOff() {
        var btnOn = $('.chatbot_builder .btn_test_on');
        var btnOff = $('.chatbot_builder .btn_test_off');

        btnOff.on('click', function(){
            $('.chatbot_builder').removeClass('test_on');
            btnOn.removeClass('hide');
        });
    }
    handleChatbotTestOnOff();

    // 드롭다운 박스 열기 닫기
    $('.dl_dropdown dt').on('click', function(){
        var parent = $(this).parent('.dl_dropdown');
        parent.toggleClass('active');
    });

    // tbl_commands 버튼으로 체크한 테이블 로우 지우기
    $('.btn_remove_row').on('click', function(){
        var $commands = $(this).parents('.tbl_commands');
        var $table = $commands.next();
        var $tr = $table.find('tbody tr');

        $tr.each(function(){
            $thisTr = $(this);
            $thisCheckbox = $thisTr.find('td').eq(0).find('input[type="checkbox"]');

            if ( $thisCheckbox.prop('checked') ) {
                $thisTr.remove();
            }
        });
    });
}

function confirmAnswer(thisPointer, ee){
    delCacheNow();
    if(nowSortable){
      mui.alert("답변 순서를 설정한 뒤에 적용해주세요.")
      return;
    }
    if(nowLoading){
        mui.alert("현재 반영중입니다. 잠시 기다려주세요.")
        return;
    }
    nowLoading =true;
    newPointer = thisPointer;
    eventPointer = ee;
    let taskName = $("#task_input_name").attr('value');//태스크명 (처음으로 task는 input tag 사용안함)
    let taskDisplay = $("#task_input_display").val();//디스플레이명
    let answerText = $("#answer_area").val().replace("\n","<br>");//답변
    if(taskName.trim().length==0){
        mui.alert("태스크 명은 빈 칸으로 둘 수 없습니다.");
        nowLoading =false;
        return;
    }
    if(taskDisplay.trim().length==0){
        mui.alert("디스플레이 명은 빈 칸으로 둘 수 없습니다.");
        nowLoading =false;
        return;
    }

    let intentRelOn = false;
    let intentRelBlank = false;
    $('#tblChatbotTask > tbody > tr').each(function () {
        intentRelOn = true;
        let srcIntentText = $(this).find('td:nth-child(2)').find("div").find("p").text().toString().trim();
        let bertIntentText = $(this).find('td:nth-child(3)').find("div").find("p").text().toString().trim();
        if(srcIntentText.trim().length==0) intentRelBlank = true;
        if(bertIntentText.trim().length==0) intentRelBlank = true;
    });

    if(intentRelBlank){
      mui.confirm('태스크 관계가 설정되지 않아 빈 칸이 있습니다. 해당 관계는 제외하고 저장됩니다.', {
        title: '',
        onClose: function(isOk){
          if(isOk){
            confirmAnswerAfterCon(thisPointer,ee);
          } else {
            nowLoading =false;
          }
        }
      });
      return;
    }else{
      confirmAnswerAfterCon(thisPointer,ee);
    }
}

function confirmAnswerAfterCon(thisPointer, ee){
  newPointer = thisPointer;
  eventPointer = ee;
  let intentRelOn = false;
  let intentRelBlank = false;
  $('#tblChatbotTask > tbody > tr').each(function () {
    intentRelOn = true;
    let srcIntentText = $(this).find('td:nth-child(2)').find("div").find("p").text().toString().trim();
    let bertIntentText = $(this).find('td:nth-child(3)').find("div").find("p").text().toString().trim();
    if(srcIntentText.trim().length==0) intentRelBlank = true;
    if(bertIntentText.trim().length==0) intentRelBlank = true;
  });
  if(!intentRelOn){
    mui.confirm('태스크 관계가 설정되지 않으면 버튼이나 이미지카드로만 챗봇 대화를 진행할 수 있습니다.', {
      title: '',
      onClose: function(isOk){
        if(isOk){
          getTaskCheckProcess(thisPointer, ee);
        } else {
          nowLoading =false;
        }
      }
    });
    return;
  }
  let intentRelText = "";
  let intentRelIDList = [];
  $('#tblChatbotTask > tbody > tr').each(function () {
    let srcIntentText = $(this).find('td:nth-child(2)').find("div").find("p").text().toString().trim();
    let bertIntentText = $(this).find('td:nth-child(3)').find("div").find("p").text().toString().trim();
    if(srcIntentText.length!=0 && bertIntentText.length!=0){
      let srcIntentElem = taskData1.find((item) =>{
        return item.label.trim() === srcIntentText;
      } )
      let bertIntentElem = taskData2.find((item) =>{
        return item.label.trim() === bertIntentText;
      } )
      let srcIntentNum = 0;
      let bertIntentNum = 0;
      try{
        srcIntentNum = srcIntentElem.value;
        bertIntentNum = bertIntentElem.value;
        if(intentRelIDList.find(element => element==(srcIntentNum.toString() + "," + bertIntentNum.toString()))==undefined){
          intentRelText += srcIntentNum.toString() + "," + bertIntentNum.toString() + "/";
          intentRelIDList.push(srcIntentNum.toString() + "," + bertIntentNum.toString());
        }
      }catch (e) {
      }
    }
  });
  intentRelText = intentRelText.substring(0,intentRelText.length-1);
  if(intentRelText.trim().length<2){
    getTaskCheckProcess(thisPointer, ee);
    return;
  }
  var obj = new Object();
  obj.intentRelText = intentRelText;
  obj.nowPK = nowModifyingPK;
  addAjax();
  $.ajax({
    url : "checkIntentRel",
    data : JSON.stringify(obj),
    type: "POST",
    contentType: 'application/json'
  }).success(
      function(result) {
        finAjax();
        if(result.retDestIntentName==""){
          getTaskCheckProcess(thisPointer, ee);
        }else{
          mui.alert(result.retDestIntentName + " 태스크와 겹치는 태스크 관계를 가지고 있습니다.")
          nowLoading =false;
          return;
        }
      }).fail(function(result) {
    finAjax();
    console.log(result);
    nowLoading =false;
  });
}


function getTaskCheckProcess(thisPointer, ee){
    let taskName = $("#task_input_name").val();//태스크명
    let answerText = $("#answer_area").val().replace("\n","<br>");//답변
    var obj = new Object();
    obj.Main = taskName;
    obj.account = hostToPage;
    addAjax();
    $.ajax({
        url : "getTaskCheck",
        data : JSON.stringify(obj),
        type: "POST",
        contentType: 'application/json'
    }).success(
        function(result) {
            finAjax();
            let answ = result.answerList;

            if(answ.length == 0){
                confirmAnswerFirstly();

            }else{
                if(nowModifyingPK == -1){
                    mui.alert("중복된 태스크 명이 존재합니다.")
                    nowLoading =false;
                }else{
                    if(nowModifyingPK === answ[0].No){
                        confirmAnswerFirstly();
                    }else{
                        mui.alert("중복된 태스크 명이 존재합니다.");
                        nowLoading =false;
                    }
                }
            }
        }).fail(function(result) {
        finAjax();
        mui.alert("중복된 태스크 명이 존재합니다.")
        nowLoading =false;
    });
}

function uuidv4() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

function confirmAnswerFirstly(){
    if($("#fixed_swiper_item > .swiper_item > .item_img > img").attr('src').toString().trim()==="../images/img_nopicture.png"){
        confirmAnswerAfter("-1");
        return;
    }
    try{
        if(nowImgUploaded){
            let nowImgSrc = $("#fixed_swiper_item > div > span.item_img > img").attr('src');
            fetch(nowImgSrc)
                .then(res => res.blob())
                .then(blob => {
                    const fd = new FormData();
                    const file = new File([blob], uuidv4() + ".jpeg");
                    fd.append('imageFile', file);
                    addAjax();
                    $.ajax({
                        type: "POST",
                        enctype: 'multipart/form-data',
                        url: "imageUrl",
                        data: fd,
                        processData: false,
                        contentType: false,
                        cache: false,
                        timeout: 600000,
                        success: function (data) {
                            finAjax();
                            confirmAnswerAfter("http://10.50.1.19:18090/sds-builder/upload/chat/img/" + data.imgName);
                            // confirmAnswerAfter("http://localhost:8080/sds-builder/upload/chat/img/" + data.imgName);
                        },
                        error: function (e) {
                            finAjax();
                            confirmAnswerAfter("");
                        }
                    });
                })
        }else{
            confirmAnswerAfter("");
        }
    }catch (e) {
        confirmAnswerAfter("");
    }
}

function confirmAnswerAfter(ImageURL){
    let imgCardImg = ImageURL;
    let taskName = $("#task_input_name").attr('value');//태스크명
    let taskDisplay = $("#task_input_display").val();//디스플레이명
    let answerText = $("#answer_area").val().replace(/\n/gi,"<br>");//답변
    let carouselList = ["","","",""];
    let buttonList = ["","","",""];
    for(let jj = 0; jj<4; jj++){
        for(let ii = 0; ii<imgSelectedData.length; ii++){
            if(jj==0)        carouselList[jj] += "I" + imgSelectedData[ii].valueLang1;
            else if(jj==1)   carouselList[jj] += "I" + imgSelectedData[ii].valueLang2;
            else if(jj==2)   carouselList[jj] += "I" + imgSelectedData[ii].valueLang3;
            else             carouselList[jj] += "I" + imgSelectedData[ii].valueLang4;

            if(ii!=imgSelectedData.length-1){
                carouselList[jj] += ",";
            }
        }
        for(let ii = 0; ii<buttonSelectedData.length; ii++){
            if(jj==0)        buttonList[jj] += "B" + buttonSelectedData[ii].valueLang1;
            else if(jj==1)   buttonList[jj] += "B" + buttonSelectedData[ii].valueLang2;
            else if(jj==2)   buttonList[jj] += "B" + buttonSelectedData[ii].valueLang3;
            else             buttonList[jj] += "B" + buttonSelectedData[ii].valueLang4;
            if(ii!=buttonSelectedData.length-1){
                buttonList[jj] += ",";
            }
        }
    }
    let nowTextList = ["","","",""];
    for(let jj = 0; jj<4; jj++){
        if(carouselList[jj].toString().trim().length>0 && buttonList[jj].toString().trim().length>0){
            nowTextList[jj] = carouselList[jj].toString().trim() + "," + buttonList[jj].toString().trim();
        }else{
            nowTextList[jj] = carouselList[jj].toString().trim() + buttonList[jj].toString().trim();
        }
    }


    let intentRelText = "";
    let intentRelTextList = ["","","",""];
    let intentRelIDList = [];
    $('#tblChatbotTask > tbody > tr').each(function () {
        let srcIntentText = $(this).find('td:nth-child(2)').find("div").find("p").text().toString().trim();
        let bertIntentText = $(this).find('td:nth-child(3)').find("div").find("p").text().toString().trim();
        let srcIntentElem = taskData1.find((item) =>{
            return item.label.trim() === srcIntentText;
        } )
        let bertIntentElem = taskData2.find((item) =>{
            return item.label.trim() === bertIntentText;
        } )
        let srcIntentNum = [0,0,0,0];
        let bertIntentNum = [0,0,0,0];
        try{
            srcIntentNum[0] = srcIntentElem.valueLang1;
            bertIntentNum[0] = bertIntentElem.valueLang1;
            srcIntentNum[1] = srcIntentElem.valueLang2;
            bertIntentNum[1] = bertIntentElem.valueLang2;
            srcIntentNum[2] = srcIntentElem.valueLang3;
            bertIntentNum[2] = bertIntentElem.valueLang3;
            srcIntentNum[3] = srcIntentElem.valueLang4;
            bertIntentNum[3] = bertIntentElem.valueLang4;

        }catch (e) {
            return;
        }
        try{
            srcIntentNum[0] = srcIntentNum[0] == undefined ? "0" : srcIntentNum[0];
            if(intentRelIDList.find(element => element==(srcIntentNum[0].toString() + "," + bertIntentNum[0].toString()))==undefined){
                for(let jj = 0; jj<4; jj++){
                    try{
                        if(srcIntentNum[jj]==undefined){srcIntentNum[jj] = 0;}
                        intentRelTextList[jj] += srcIntentNum[jj] + "," + bertIntentNum[jj] + ",";
                    }
                    catch(e){

                    }
                }
                intentRelIDList.push(srcIntentNum[0].toString() + "," + bertIntentNum[0].toString());
            }
        }catch(e){

        }

    });
    intentRelText = intentRelText.substring(0,intentRelText.length-1);
    for(let jj = 0; jj<4; jj++){
        intentRelTextList[jj] = intentRelTextList[jj].substring(0,intentRelTextList[jj].length-1);
    }
    let nextText = carouselList + (carouselList.length!=0 && buttonList.length!=0 ? "," : "") + buttonList;
    let intentDescription = $("#item_txt_description").val();


    var obj = new Object();
    var inputList = [];
    let nowLang = "";
    for(let ii = 0 ; ii<4; ii++){
        if(nowLangApply[ii]){
            nowLang += (ii+1).toString();
            let elemParModifyingPK = "";
            let elemTaskName = "";
            let elemAnswerText = "";
            let elemNextText = "";
            let elemIntentRelText = "";
            let elemDescription = "";
            if(ii+1===fixLang){
                elemParModifyingPK = nowModifyingPK;
                elemTaskName = taskDisplay;
                elemAnswerText = answerText;
                elemNextText = nowTextList[ii];
                elemIntentRelText = intentRelTextList[ii];
                elemDescription = intentDescription;
            }else{
                try{
                    elemParModifyingPK = multiLangDetail[ii+1].No;
                }catch(e){elemParModifyingPK = -1;}
                if(elemParModifyingPK.length==0) elemParModifyingPK = -1;
                elemTaskName = multiLangDetail[ii+1].taskname;
                elemAnswerText = multiLangDetail[ii+1].answer;
                elemNextText = nowTextList[ii];
                elemIntentRelText = intentRelTextList[ii];
                elemDescription = multiLangDetail[ii+1].addedDescription;
            }
            try{
                if(detail.Main=="처음으로"){
                    taskName="처음으로";
                }
            }catch(e){}
            let newElem = {
                main: taskName,
                lang: ii+1,
                nowModifyingPK: elemParModifyingPK,
                taskName: elemTaskName,
                answerText: elemAnswerText,
                nextText: elemNextText,
                imgCardImg: imgCardImg,
                intentRelText: elemIntentRelText,
                host: hostToPage,
                description: elemDescription,
                A_URL: multiLangDetail[ii+1].A_URL
            }
            if(elemTaskName.length===0){
                if(elemParModifyingPK===-1){
                    newElem.lang = -2; // 생성하지 않음
                }
                else{
                    newElem.lang = -1; // 삭제만 진행
                }
            }
            inputList.push(newElem);
        }
    }
    for(let i = 0; i<inputList.length; i++){
      if(inputList[i].A_URL==undefined){
        inputList[i].A_URL = "";
      }
    }
    obj.inputList = inputList;
    let ResponseOrderText = "";
    for(let i = 0 ;i<nowContentsOrderList.length; i++){
      ResponseOrderText += nowContentsOrderList[i]
      if(i!=nowContentsOrderList.length-1){ResponseOrderText += ","}
    }
    obj.ResponseOrder = ResponseOrderText;
    addAjax();
    $.ajax({
        url : "addModIntent",
        data : JSON.stringify(obj),
        type: "POST",
        contentType: 'application/json'
    }).success(
        function(result) {
            finAjax();
            mui.alert("성공적으로 저장되었습니다.");
            overlayTutorial(12);
            nowLoading =false;
            var hrefId = newPointer.parents('.lyrBox').attr('id');
            var id = '#' + hrefId;
            var thisLayer = $(id);
            eventPointer.stopPropagation();
            lyrClose(thisLayer);
            chatClick(hostToPage, 1);
            delPopper();
        }).fail(function(result) {
        finAjax();
        nowLoading =false;
        console.log("error : ");
        console.log(result);
    });
}

function delDetail(intentPK, intentName){
    if(intentName=="처음으로"){
        mui.alert("'처음으로' 답변은 삭제가 불가능합니다.");
        return;
    }
    mui.confirm('해당 답변이 삭제됩니다. 진행하시겠습니까?', {
      title: '',
      onClose: function(isOk){
        if(isOk){
          var obj = new Object();
          obj.intentPK = intentPK;
          obj.host = hostToPage;
          obj.intentName = intentName;
          addAjax();
          $.ajax({
            url : "delIntent",
            data : JSON.stringify(obj),
            type: "POST",
            contentType: 'application/json'
          }).success(
              function(result) {
                finAjax();
                if(result.toString().trim()==="FALSE"){
                  mui.alert("문제가 생겼습니다. 다시 시도해주세요.");
                  chatClick(hostToPage, 1);
                }else{
                  mui.alert("성공적으로 삭제되었습니다.");
                  chatClick(hostToPage, 1);
                }
              }).fail(function(result) {
            finAjax();
            console.log("delIntent error");
            mui.alert("문제가 생겼습니다. 다시 시도해주세요.");
            chatClick(hostToPage, 1);
          });
        } else {

        }
      }
    });
}

function searchKeywordAnswer(){
    console.log("searchKeywordAnswer")
    nowSearchKeyword = $("#search_keyword_textbox").val();
    chatClick(hostToPage,1);
}

function replaceKeywordInTable(text,searchKeyword){
    let repText = text.toString()
        .replace(/\n/gi," ")
        .replace(/\<br\>/gi," ")
        .replace(/\<a\>/gi," ")
        .replace(/\<\/a\>/gi," ")
        .replace(/\|\|\|INQUIRY.*/gi,"")
        .replace(/\|\|\|ORDER.*/gi,"")
        .replace(/\|\|\|MAP.*/gi,"");
    let regex = new RegExp("(" + searchKeyword + ")");
    return repText.replace(regex,"<p class='searchTextInTable'>$1</p>")
}

function excelDown() {
    var $preparingFileModal = $("#preparing-file-modal");
    $preparingFileModal.dialog({modal: true});
    $("#progressbar").progressbar({value: false});
    var obj = new Object();
    var host = $('#chatListUl>li.active').val();
    if (host == undefined || host == "") {
        host = hostToPage;
    }
    obj.host = host;

    var downUrl = $('#chatListUl>li.active>input[type=hidden]').val() === 'Y' ? '/upload/chatExcelDownHTask' : '/upload/chatExcelDown';
    $.fileDownload(getContextPath() + downUrl, {
        data: { host: host},
        successCallback: function (url) {
            $preparingFileModal.dialog('close');
        },
        failCallback: function (responseHtml, url) {
            $preparingFileModal.dialog('close');
            $("#error-modal").dialog({modal: true});
        }
    });
    return false;
}
function sampleExcelDown(){
    var host = $('#chatListUl>li.active').val();
    if (host == undefined || host == "") {
        host = hostToPage;
    }

    $.fileDownload(getContextPath() + '/upload/excelSampleDown?host='+host, {
        successCallback: function (url) {
            console.log("샘플 엑셀 다운 OK url : " + url);
        },
        failCallback: function (responseHtml, url) {
            console.log("샘플 엑셀 다운 Fail url : " + url);
        }
    });
    return false;
}


function getContextPath() {
    return sessionStorage.getItem("contextpath");
}

function startCropWindow(){
    nowCropSrc = undefined;
    $("#size_info").text("");
    var newWindowHtml = "<p class=\"choose_img\">이미지를 선택해주세요.</p>\n<img src=\"/images/sample/sample_image_crop.png\" alt=\"샘플이미지\" style=\"width: 100%;  height: 100%;\">";
    $(".image_setting").html(newWindowHtml);

    $(".sub_lyr").css('display', 'block');
    $(".choose_img").css('display','');
    $("#carousel_image_setting > div.lyr_mid > dl > dt > div > input").val("선택된 파일 없음"); //target id와 같은 name을 가진 input[text]에 적용
}

function cropImageSubmit(){
    if(nowCropSrc!=undefined){
        $("#carousel_image_setting").css("display","none");
        let fileInput = $("#cicLabel");
        var nowimgHTML = "";
        var toggleBoolean = false;
        nowimgHTML = '<img src="' + nowCropSrc + '" alt="백그라운드 이미지">'
        if ( fileInput.prev().length === 0 ) {
            fileInput.parent().prepend(nowimgHTML);
            toggleBoolean = true;
        }
        if(toggleBoolean==false){
            fileInput.prev().attr('src', nowCropSrc);
        }
    }else{
        mui.alert("이미지가 업로드 되지 않았습니다.");
    }
    sec_Images_synchronize();
}
function exitCropPage(){
  mui.confirm('진행중인 사항이 사라집니다.', {
    title: '',
    onClose: function(isOk){
      if(isOk){
        $("#carousel_image_setting").css("display","none");
      }
    }
  });
}

function delPopper(){
    $(".fast_popper_option").remove();
}

function replaceAnswerPreview(oriText){
    $('#bot_map').remove();
    if(oriText.includes("\|\|\|MAP\|\|\|")){
        addMapPreview(oriText);
    }
    return oriText
        .replace(/\n/gi,"<br>")
        .replace(/\|\|\|\|\|/gi,"<div class='spliter_line'><div class='spliter_color'></div></div>")
        .replace(/\|\|\|INQUIRY.*/gi,"")
        .replace(/\|\|\|ORDER.*/gi,"")
        .replace(/\|\|\|MAP\|\|\|.*/gi,"")
}

function addMapPreview(oriText){
    try{
        var jsonString = oriText.slice(oriText.indexOf('|||MAP|||')+9);
        jsonString = jsonString.replace(/\</gi,"\"").replace(/\>/gi,"\"")
        var jsonRes = JSON.parse(jsonString);

        var mapSelectHtml =
            '   <li class="bot" id="bot_map"> \
                <div class="bot_msg"> \
                    <div class="btnLst"> \
                        <span class="txt">' + jsonRes.answer + '</span> \
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
                {mapSelectHtml += 'value="direct"  '}
            }
            else
            {mapSelectHtml += '\'{"lat": ' + start.location.lat + ', "lng": ' + start.location.lng + '}\' ';}
            if(start.selected){
                mapSelectHtml += 'selected';
                if(start.location.get == "custom")
                {startCustomCheck = true;}
            }
            mapSelectHtml +='> ' + start.name + '</option>';
        });
        mapSelectHtml +=
            '                       </select> \
                                    <input type="text" name="selboxDirect" class="ipt_txt selboxDirect startInput" value="" \
            ';
        if(startCustomCheck){mapSelectHtml += 'style="display: inline-block;"'}
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
                {mapSelectHtml += 'value="direct" ';}
            }
            else
            {mapSelectHtml += '\'{"lat": ' + end.location.lat + ', "lng":' + end.location.lng + '}\' '}
            if(end.selected){
                mapSelectHtml +='selected';
                if(end.location.get === "custom")
                {endCustomCheck = true;}
            }
            mapSelectHtml +='> ' +  end.name + '</option>';
        });
        mapSelectHtml +=
            ' \
                                    </select> \
                                    <input type="text" name="selboxDirect" class="ipt_txt selboxDirect endInput" value="" \
            ';
        if(endCustomCheck){mapSelectHtml += 'style="display: inline-block;"'}
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
        $("#chat_answer_setting > div.lyr_mid > ul > li:nth-child(2)").after(mapSelectHtml)
    }catch (e) {
        console.log(e)
        var mapSelectHtml = "<li class=\"bot\" id=\"bot_map\">MAP에 관한 내용이 잘못 되었습니다.</li>";
        $("#chat_answer_setting > div.lyr_mid > ul > li:nth-child(2)").after(mapSelectHtml)
    }
}

function delBlank(){
    if($("#chat_answer_setting > div.lyr_mid > ul > li:nth-child(2)").text().replace(/\s/gi,"")==""){
        $("#chat_answer_setting > div.lyr_mid > ul > li:nth-child(2)").css('display', 'none');
    }else{
        $("#chat_answer_setting > div.lyr_mid > ul > li:nth-child(2)").css('display', 'block');
    }
}

function delIntentBodyHref(){
    $("#intentBody").find('tr').each(function () {
        $(this).find('td:nth-child(3) > span').find('a').attr('href', '#');
    });
}

function ansDetailLang(){
    let nowCount = 0;
    nowDetailSearchLang = 0;
    $("#ans_detail_lang_list").find('ul').find('li').each( function()
    {
        $(this).removeClass("active");
        $(this).removeClass("fix");
        if(nowLangApply[nowCount]){
            $(this).removeClass("none");
            if(nowDetailSearchLang==0){
                nowDetailSearchLang = nowCount+1;
                $(this).addClass("active");
                $(this).addClass("fix");
            }
        }else{
            $(this).addClass("none");
        }
        nowCount++;
    } );
}

function langDetailOnclick(langIndex){
    if(nowDetailSearchLang==langIndex && nowDetailSearchLang!=fixLang){
        langDetailOnclick(fixLang);
        return;
    }
    nowDetailSearchLang = langIndex;
    let nowCount = 1;
    $("#ans_detail_lang_list").find('ul').find('li').each( function()
    {
        $(this).removeClass("active");
        if(nowDetailSearchLang==nowCount){
            $(this).addClass("active");
        }
        nowCount++;
    } );
    if(fixLang==langIndex){//옆 박스 비활성화
        $("#chat_answer_setting").removeClass("more_lang")
        $("#sec_ipt").empty();
    }else{
        $("#chat_answer_setting").removeClass("more_lang")
        $("#chat_answer_setting").addClass("more_lang")
        semiIptConstruct(langIndex);
    }
}

function afterIPTConstruct(){
    let firstLang = 0;
    let firstLangCount = 1;
    $("#ans_detail_lang_list").find('ul').find('li').each( function()
    {
        if($(this).hasClass("fix")){
            $("#ori_ipt .lang_viewer").removeClass("korea")
            $("#ori_ipt .lang_viewer").removeClass("us")
            $("#ori_ipt .lang_viewer").removeClass("japan")
            $("#ori_ipt .lang_viewer").removeClass("china")
            let con = ["korea","us","japan","china"];
            for(let ii = 0; ii<con.length; ii++){
                if($(this).hasClass(con[ii])){
                    $("#ori_ipt .lang_viewer").addClass(con[ii])
                }
            }
            firstLang = firstLangCount;
        }
        firstLangCount++;
    } );
    if(nowSearchLang!=firstLang){
        $("#ans_detail_lang_list > ul > li:nth-child("+nowSearchLang+")").click();
    }
}

function semiIptConstruct(lang){
    let returnInnerHtml = "<div class=\"lang_viewer "+["","korea","us","japan","china"][lang]+"\"></div>\n" +
        "                <dl>\n" +
        "                    <dt>디스플레이 명</div>\n" +
        "                    <dd><input type=\"text\" class=\"ipt_txt\" value='"+ multiLangDetail[lang].taskname +"' onchange=\"onchangeSemi(true,this.value,"+lang+",null)\" id=\"task_input_name2\"></dd>\n" +
        "</dt>"+
        "                </dl>\n" +
        "                <dl>\n" +
        "                    <dt>답변\n" +
        "                    </dt>\n" +
        "                    <dd>\n" +
        "                        <textarea name=\"\" id=\"\" cols=\"10\" rows=\"2\" class=\"ipt_txt answer\"  onfocus='handleAnswerHeight(event)'  oninput=\"onchangeSemi(false,this.value,"+lang+",event)\" onchange=\"onchangeSemi(false,this.value,"+lang+",null)\">" + multiLangDetail[lang].answer.replace(/<br>/gi,"\n") +"</textarea>\n" +
        "                    </dd>\n" +
        "                </dl>\n " +
        "    <dl class=\"dl_dropdown\" id='sec_dropdown'>\n" +
        "                     <dt>이미지 카드\n" +
        "                            <div class=\"btn_control fr\">\n" +
        "                            <div class=\"fold\">\n" +
        "                                <span>펼치기</span>\n" +
        "                                <svg width=\"15\" height=\"8\" viewBox=\"0 0 15 8\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
        "                                    <path d=\"M0 1.63966L1.20363 0.0423927L8.39135 5.45873L7.18772 7.056L0 1.63966Z\" fill=\"#97A1AB\"/>\n" +
        "                                    <path d=\"M13.2154 0L14.4191 1.59727L7.23133 7.01361L6.0277 5.41634L13.2154 0Z\" fill=\"#97A1AB\"/>\n" +
        "                                </svg>\n" +
        "                            </div>\n" +
        "                            <div class=\"unfold\">\n" +
        "                                <span>감추기</span>\n" +
        "                                <svg width=\"15\" height=\"8\" viewBox=\"0 0 15 8\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
        "                                    <path d=\"M14.4191 5.41634L13.2154 7.01361L6.02772 1.59727L7.23135 9.53674e-07L14.4191 5.41634Z\" fill=\"#97A1AB\"/>\n" +
        "                                    <path d=\"M1.20364 7.056L1.33514e-05 5.45873L7.18773 0.0423937L8.39136 1.63966L1.20364 7.056Z\" fill=\"#97A1AB\"/>\n" +
        "                                </svg>\n" +
        "                            </div>" +
        "                        </div>\n" +
        "                    </dt>\n" +
        "                    <dd>\n" +
        "                        <div class=\"setting_image_card\">\n" +
        "                            <div class=\"card\">\n" +
        "                                <p class=\"info_text\">미리보기</p>\n" +
        "                                <div class=\"swiper_item\">\n" +
        "                                    <span class=\"item_img\" id='sec_item_img'>\n" +
        "<img src='../images/img_nopicture.png' alt='백그라운드이미지'>\n" +
        "                                    </span>" +
        "                                    <span class=\"item_tit\"><input type=\"text\" id=\"title_text_input2\" placeholder=\"타이틀\" onchange='changeSecAMRTitle(this.value)'></span>\n" +
        "                                    <span class=\"item_txt\"><textarea name=\"\" id=\"item_txt_description2\" cols=\"30\" rows=\"4\" placeholder=\"내용\" onkeyup='changeDescription(false, this, "+lang+")' onchange='changeDescription(false, this, "+lang+")'></textarea></span>\n" +
        "                                </div>\n" +
        "                            </div>\n" +
        "                            <button type=\"button\" class=\"btn_secondary\" onclick='confirmImageCard(this)' id=\"AMR_apply2\">적용 &raquo;</button>\n" +
        "                            <div class=\"card\" id=\"fixed_swiper_item\">\n" +
        "                                <p class=\"info_text\">확정</p>\n" +
        "                                <div class=\"swiper_item\">\n" +
        "                                    <span class=\"item_img\" id='sec_item_img2'>" +
        "<img src='../images/img_nopicture.png' alt='백그라운드이미지'></span>\n" +
        "                                    <span class=\"item_tit\" id='sec_titl'></span>\n" +
        "                                    <span class=\"item_txt\" id='sec_desc'></span>\n" +
        "                                </div>\n" +
        "                            </div>\n" +
        "                        </div>\n" +
        "                    </dd>\n" +
        "                </dl>"

    $("#sec_ipt").html(returnInnerHtml);
    $('#sec_dropdown dt').on('click', function(){
        var parent = $(this).parent('.dl_dropdown');
        parent.toggleClass('active');
    });
    $("#title_text_input2").val(multiLangDetail[lang].taskname);
    $("#item_txt_description2").val(multiLangDetail[lang].previewDescription);
    $("#sec_titl").empty();
    $("#sec_titl").html(multiLangDetail[lang].taskname);
    $("#sec_desc").empty();
    $("#sec_desc").html(multiLangDetail[lang].addedDescription);

    sec_Images_synchronize();
}
function onchangeSemi(taskBool, changedValue, langIndex, event){
    if(taskBool){
        multiLangDetail[langIndex].taskname = changedValue;
        $("#title_text_input2").val(changedValue);
    }else{
        multiLangDetail[langIndex].answer = changedValue;
        try{
            // answerPreview(event);
        }
        catch(e){//textarea 이동했을 경우
        }
    }
}

function sec_Images_synchronize(){
    $("#sec_item_img").empty();
    let nowSource = $("#prev_swiper_item > div > span.item_img > img").attr("src");
    if(nowSource!=undefined) $("#sec_item_img").append("<img src=\""+nowSource+"\" alt=\"백그라운드 이미지\">");

    $("#sec_item_img2").empty();
    let nowSource2 = $("#fixed_swiper_item > div > span.item_img > img").attr("src");
    if(nowSource!=undefined) $("#sec_item_img2").append("<img src=\""+nowSource2+"\" alt=\"백그라운드 이미지\">");
}

function changeSecAMRTitle(str){
    $("#task_input_name2").val(str);
}




function changeDescription(prevBool, changedValue, langIndex){
    multiLangDetail[langIndex].previewDescription = $(changedValue).val();
}

function handleAnswerHeight(e) {
    let target = e.target;
    calcAnswerHeight(e);
    $(target).css('overflow',"hidden");
    $(target).on('focusout', function () {
        $(target).css('height', 'auto');
        $(target).css('overflow',"auto");
        $(target).off('focusout');
    });
}
function calcAnswerHeight(e) {
    let target = e.target;
    let scrollHeight = target.scrollHeight;
    $(target).css('height', (scrollHeight + 2) + 'px');
}

function newBertIntent(newBertIntentKeyword){
    var obj = new Object();
    obj.host = hostToPage;
    obj.keyword = newBertIntentKeyword;
    var nowLangPar = "";
    for(let ii = 0; ii<nowLangApply.length; ii++){
        if(nowLangApply[ii]){
            nowLangPar += (ii+1).toString();
        }
        if(ii!=nowLangApply.length-1){
            nowLangPar += ",";
        }
    }
    obj.nowLang = nowLangPar;
    addAjax();
    $.ajax({
        url : "newBertIntent",
        data : JSON.stringify(obj),
        type: "POST",
        contentType: 'application/json'
    }).success(
        function(result) {
            finAjax();
            if(result.toString().indexOf(":TRUE:")!=-1){
                mui.alert("의도가 추가되었습니다.");
                let nowResult = result.toString().replace(":TRUE:","").split(",");
                let nowElem = {'label':newBertIntentKeyword,'value':0, 'valueLang1':0, 'valueLang2':0, 'valueLang3':0, 'valueLang4':0};
                for(let ii = 0; ii<nowResult.length; ii++){
                    let nowElemSplited = nowResult[ii].split(":");
                    if(nowElemSplited[0]==="1"){nowElem.valueLang1=nowElemSplited[1];}
                    if(nowElemSplited[0]==="2"){nowElem.valueLang2=nowElemSplited[1];}
                    if(nowElemSplited[0]==="3"){nowElem.valueLang3=nowElemSplited[1];}
                    if(nowElemSplited[0]==="4"){nowElem.valueLang4=nowElemSplited[1];}
                    if(nowElemSplited[0]==fixLang.toString()){nowElem.value=nowElemSplited[1];}
                }
                taskData2.push(nowElem)
                taskData2Sync();
                $('#noResultBox').parent().parent().removeClass("active");
                $('#noResultBox').remove();


            }else{
                console.log(result);
            }
        }).fail(function(result) {
        finAjax();
            mui.alert("오류가 발생했습니다. 잠시 후 시도해주세요.");
    });

}

function taskData2Sync(){
    let newBiList = [];
    let intentRelIDList = [];
    $('#tblChatbotTask > tbody > tr').each(function () {
        let srcIntentText = $(this).find('td:nth-child(2)').find("div").find("p").text().toString().trim();
        let bertIntentText = $(this).find('td:nth-child(3)').find("div").find("p").text().toString().trim();
        let srcIntentElem = taskData1.find((item) =>{
            return item.label.trim() === srcIntentText;
        } )
        let bertIntentElem = taskData2.find((item) =>{
            return item.label.trim() === bertIntentText;
        } )
        let srcIntentNum = 0;
        let bertIntentNum = 0;
        try{
            srcIntentNum = srcIntentElem.value;
            bertIntentNum = bertIntentElem.value;
        }catch (e) {
        }
        if(intentRelIDList.find(element => element==(srcIntentNum.toString() + "," + bertIntentNum.toString()))==undefined){
            let newElem = {
                BertIntent : bertIntentNum,
                SrcIntent : srcIntentNum,
                DestIntent : nowModifyingPK
            }
            newBiList.push(newElem);
            intentRelIDList.push(srcIntentNum.toString() + "," + bertIntentNum.toString());
        }
    });
    generateBertIntentList(newBiList);
    generateBertIntentListRender();
}

function handleChatBuilderImageDelete(e) {
    let target = e.target;
    let $parent = $(target).parents('.cb_img_delete');
    let btnDelete = $parent.find('.btn_icon.delete');
    btnDelete.addClass('active');
    target.onmouseout = function() { btnDelete.removeClass('active');};
}

function delImg(){
    nowDeleted = true;
    $("#prev_swiper_item .swiper_item .item_img").empty();
    $("#prev_swiper_item .swiper_item .item_img").append('<img src="../images/img_nopicture.png" alt="백그라운드 이미지">');
    $("#prev_swiper_item .swiper_item .item_img").append('<label id="cicLabel" onmouseover="handleChatBuilderImageDelete(event)" onclick="changeImageCard(this)">+</label>');
    $("#sec_item_img").empty();
    $("#sec_item_img").append('<img src="../images/img_nopicture.png" alt="백그라운드 이미지">');
}



function addSortable(){
  if(outerSortable===false){
    outerSortable = new Sortable(sortable_wrap,
        {
          animation : 100,
          onChange: function(evt){
            changeContentsOrder();
          }
        }
    )
  }else{
    outerSortable.option("disabled", false)
  }
  nowSortable = true;
  makeDivBySortable(buttonSelectedData, "button")
  makeDivBySortable(imgSelectedData, "img_slide")
}

function remSortable(){
  renderOnlyPTag();
  $("#button > p").css('display', 'block');
  $("#img_slide > p").css('display', 'block');
  $(".sortable_div").remove();
  outerSortable.option("disabled", true)

  //TODO : 캐로셀 & 버튼 박스 순서 다시 맞춰주기
  nowSortable = false;
}

function makeDivBySortable(selectedData, nowDivID){
  let nowFindID = "#"+nowDivID;
  let nowDiv = $(nowFindID)
  nowDiv.find("p").css('display', 'none');
  let nowAddingDiv = "<div class='sortable_div' id='"+nowDivID+"_sort'>";
  for(let sdCount = 0; sdCount < selectedData.length; sdCount++){
    nowAddingDiv += "<div class='sortable_elem'>"
        +selectedData[sdCount].main+"</div>"
  }
  nowAddingDiv += "</div>"
  nowDiv.append(nowAddingDiv);
  if(nowDivID=="button"){
    new Sortable(button_sort,
        {
          animation : 100,
          onChange: function(evt){
            let nowSorting = [];
            $("#button_sort").find("div").each(function () {
              let nowElemByData = buttonSelectedData.find(element => element.main == $(this).text())
              nowSorting.push(nowElemByData);
            });
            buttonSelectedData = nowSorting;
            generateBtnItem()
          }
        }
    )
  }else{
    new Sortable(img_slide_sort,
        {
          animation : 100,
          onChange: function(evt){
            let nowSorting = [];
            $("#img_slide_sort").find("div").each(function () {
              let nowElemByData = imgSelectedData.find(element => element.main == $(this).text())
              nowSorting.push(nowElemByData);
            });
            imgSelectedData = nowSorting;
            generateImgCarousel()
          }
        }
    )
  }

}

function changeContentsOrder(){
  let nowList = [];
  let contentMap = {
    "캐로셀" : 0,
    "답변" : 1,
    "버튼" : 2
  }
  $("#sortable_wrap").find("dl").each(function () {
    let nowElem = $(this).find("dt").text().split("?")[0].replace(/\s/gi,"")
    nowList.push(contentMap[nowElem]);
  });
  nowContentsOrderList = nowList;
  renderContentsOrder();
}

function renderContentsOrder(){
  for(let orderCount = 0; orderCount<3; orderCount++){
    if(nowContentsOrderList[orderCount]==0){
      jQuery("#chat_preview_carousel").detach().appendTo('#chat_intent_answer_preview')
    }
    if(nowContentsOrderList[orderCount]==1){
      jQuery("#chat_preview_answer").detach().appendTo('#chat_intent_answer_preview')
    }
    if(nowContentsOrderList[orderCount]==2){
      jQuery("#chat_preview_button").detach().appendTo('#chat_intent_answer_preview')
    }
  }
}

function renderOnlyPTag(){
  let nowImgP = "";
  let nowBtnP = "";
  for (let i = 0; i<imgSelectedData.length; i++){
    nowImgP += imgSelectedData[i].label;
    if(i!=imgSelectedData.length-1) {nowImgP += ","}
  }
  $("#img_slide > p").text(nowImgP)
  for (let i = 0; i<buttonSelectedData.length; i++){
    nowBtnP += buttonSelectedData[i].label;
    if(i!=buttonSelectedData.length-1) {nowBtnP += ","}
  }
  $("#button > p").text(nowBtnP)

}

let AnswerMoreLang = false; //.more_lang 상태 (다른언어 선택 여부)
function setAnswerModifyMode() {
    let target = event.target;
    let parent = $('#chat_answer_setting');
    let iptParent = parent.find('#ori_ipt');

    if ( iptParent.hasClass('set_order') ) {
        if ( setAnswerMoreLang ) { //more_lang 보여주기
            parent.addClass('more_lang');
            parent.find('#sec_ipt').css('display', 'inline-block');
        }
        parent.find('.lang_list').css('display', 'inline-block'); //언어선택 보여주기
        parent.find('.btn_lyr_close').removeAttr('disabled'); //btn_lyr_close 클릭 가능하게하기
        iptParent.removeClass('set_order');
        $(target).text('답변 순서 설정').removeClass('active');
        remSortable();
        return;
    }

    setAnswerMoreLang = parent.hasClass('more_lang') ? true : false; //.more_lang 상태 설정 (다른언어 선택 여부)
    if ( setAnswerMoreLang ) { //more_lang 가리기
        parent.removeClass('more_lang');
        parent.find('#sec_ipt').css('display', 'none');
    }
    parent.find('.lang_list').css('display', 'none'); //언어선택 가리기
    parent.find('.btn_lyr_close').attr('disabled', true); //btn_lyr_close 클릭 불가능하게하기
    iptParent.addClass('set_order');
    $(target).text('순서 설정 적용').addClass('active');
    addSortable();
}