function chatClick(host, cp) {
    var obj = new Object();
    obj.chatName = $('#chatListUl>li.active>div:first>span').text();
    obj.host = host;
    obj.searchReplaceDict = $("input[name='searchReplaceDict']").val();
    obj.cp = cp;
    obj.lang = nowSearchLang;
    obj.orderCol = $('#orderCol').val();
    obj.orderSort = $('#orderSort').val();

    if($('#chatListUl>li.active>div:first>span').text().length>0){
        $("#chatTitle").text($('#chatListUl>li.active>div:first>span').text());
    }

    addAjax();
    $.ajax({
        url : "getReplaceDict",
        data : JSON.stringify(obj),
        type: "POST",
        contentType: 'application/json'
    }).success(function(result) {
        finAjax();
        var paging = result.paging;
        var cnt = paging.startRow + 1;
        var innerHTML = "";
        var list = result.replaceDictList;
        hostToPage = parseInt(String(host));
        $("#replaceDictBody").empty();

        $.each(list, function (i, v) {
            innerHTML += '<tr id="dictRow_'+cnt+'" >\n' +
                '     <td>' + cnt +'</td>\n' +
                '     <td>'+
                '       <input type="hidden" class="ipt_txt" id="BeforeOld_'+v.No+'" value="'+v.Before+'" />'+
                '       <input type="text" class="ipt_txt" id="BeforeNew_'+v.No+'" value="'+v.Before+'" />'+
                '     </td>\n' +
                '     <td style="text-align:center;"> &#187; </td>\n' +
                '     <td>'+
                '       <input type="hidden" class="ipt_txt" id="AfterOld_'+v.No+'" value="'+v.After+'" />'+
                '       <input type="text" class="ipt_txt" id="AfterNew_' + v.No + '" value="' + v.After + '" />'+
                '     </td>\n' +
                '     <td class="no_bg">'+
                '       <a href="#" class="btn_line_primary btn_lyr_open" onclick="replaceDictMod(\'update\', '+v.No+')">수정</a>'+
                '       <a href="#chat_row_delete" class="btn_line_warning btn_lyr_open" onclick="replaceDictMod(\'delete\','+v.No+')">삭제</a>'+
                '     </td>\n' +
                '</tr>';

            cnt++;
        });

        $("#replaceDictBody").append(innerHTML);

        // 페이징 start
        $(".pagination").empty();
        innerHTML = "";

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
        // 페이징 end

    }).fail(function(result) {
        finAjax();
    });
}

$(document).ready(function () {
    // 치환사전 업로드
    $('.btn_lyr_upload_open').on('click', function () {
        openChatUpload();
    });

    $('input[type="file"').change(function (e) {
        var fileName = e.target.files[0].name;
        $('input[name="excel_file_name"]').val(fileName);
    });

    // 챗봇 목록
    if(hostToPage==-1 || hostToPage==undefined || isNaN(hostToPage)){chatClick(host, 1);}
    else{chatClick(hostToPage, 1);}

    // AMR 돋보기 아이콘이 있는 검색 영역에서 엔터 시 검색버튼 눌림
    $('.ipt_txt.search').on('keyup', function(){
        if (event.keyCode == '13') {
            $(this).next('#searchReplaceDict').trigger('click');
        }
    });

    // 치환사전 검색
    $('#searchReplaceDict').on('click', function(){
      var host = $('#chatListUl>li.active').val();
      chatClick(host, 1);
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

      btnOn.on('click', function(){
        $('.chatbot_builder').addClass('test_on');
        btnOn.addClass('hide');
      });

      btnOff.on('click', function(){
        $('.chatbot_builder').removeClass('test_on');
        btnOn.removeClass('hide');
      });
    }
    handleChatbotTestOnOff();

});


function goPage(host, cp){
    if( cp != 0){
        $('#currentPage').val(cp);
    }
    chatClick(host, cp);
}

var sortNum = 0;
function replaceDictOrder(orderCol){
    var host = $('#chatListUl>li.active').val();
    var cp = $('.pages .active').text();
    var orderSort = '';
    var addClass = '';
    var delClass = '';

    if($('#orderCol').val() == orderCol || $('#orderCol').val() == ''){
        sortNum++;
    }else if($('#orderCol').val() != orderCol){
      $('#replaceDictTh'+$('#orderCol').val()).removeClass('down','up');
      sortNum = 1;
    }else{
      sortNum = 0;
    }

    if(sortNum == 3) sortNum = 0;
    switch(sortNum) {
        case 0: // No 기본정렬
            orderSort = ''; delClass = 'down';
            break;
        case 1:
            orderSort = 'asc'; addClass = 'up'; delClass = 'down';
            break;
        case 2:
            orderSort = 'desc'; addClass = 'down'; delClass = 'up';
    }

    $('#orderCol').val(orderCol);
    $('#orderSort').val(orderSort);

    if(sortNum == 0) {
        $('#replaceDictThAfter').removeClass(delClass);
        $('#replaceDictThBefore').removeClass(delClass);
        chatClick(host, cp);
        return;
    }

    if(orderCol == 'Before'){
        $('#replaceDictThAfter').removeClass(addClass, delClass);

        $('#replaceDictThBefore').removeClass(delClass);
        $('#replaceDictThBefore').addClass(addClass);
    } else {
        $('#replaceDictThBefore').removeClass(addClass, delClass);

        $('#replaceDictThAfter').removeClass(delClass);
        $('#replaceDictThAfter').addClass(addClass);
    }

    chatClick(host, cp);
}

function popOpen(popClass){

}

function replaceDictMod(mod, no){
    var host = $('#chatListUl>li.active').val();
    var obj = new Object();
    obj.host = host;
    obj.lang = nowSearchLang;

    var msg = '';
    if (mod == 'add'){ // 등록
      if($('#replaceDictBeforeAdd').val() == '' || $('#replaceDictBeforeAdd').val() == 'undefined'){
        mui.alert("치환 전 단어를 입력해주세요.");
        return false;
      } else if($('#replaceDictAfterAdd').val() == '' || $('#replaceDictAfterAdd').val() == 'undefined'){
        mui.alert("치환 후 단어를 입력해주세요.");
        return false;
      } else {
        msg = '등록';
        obj.replaceDictBefore = $('#replaceDictBeforeAdd').val();
        obj.replaceDictAfter = $('#replaceDictAfterAdd').val();
      }
    } else if(mod == 'update') { // 수정
      if($('#BeforeNew_'+no).val() == '' || $('#BeforeNew_'+no).val() == 'undefined'){
        mui.alert("치환 전 단어를 입력해주세요.");
        return false;
      } else if($('#AfterNew_'+no).val() == '' || $('#AfterNew_'+no).val() == 'undefined'){
        mui.alert("치환 후 단어를 입력해주세요.");
        return false;
      } else {
        msg = '수정';
        obj.replaceDictBefore = $('#BeforeNew_'+no).val();
        obj.replaceDictAfter = $('#AfterNew_'+no).val();
        obj.no = no;
        console.log($('#BeforeNew_'+no).val());
        console.log($('#AfterNew_'+no).val());
      }
    } else if (mod == 'delete'){
      msg = '삭제';
      obj.no = no;
    } else {
        alert("예기치 못한 오류가 발생 했습니다. 다시 시도해 주세요.");
        return false;
    }

    if(!confirm("치환사전을 " + msg + "하시겠습니까?")){
      return false;
    }

    addAjax();
    $.ajax({
        url : mod + "ReplaceDict",
        data : JSON.stringify(obj),
        type: "POST",
        contentType: 'application/json'
    }).success(function(result) {
        finAjax();
        $('#orderCol').val('');
        $('#orderSort').val('');
        if(result.resultCnt == 1){
          if(mod == 'add'){
            $('#replaceDictBeforeAdd').val('');
            $('#replaceDictAfterAdd').val('');
          }
          $('#replaceDictThAfter').removeClass('up', 'down');
          $('#replaceDictThBefore').removeClass('up', 'down');
          alert("치환사전 "+msg+"했습니다.");
        }else{
          alert("치환사전 "+msg+"(을)를 실패했습니다.");
        }
        chatClick(host, 1);
    }).fail(function(result) {
        finAjax();
        alert("치환사전 "+msg+"(을)를 실패했습니다.");
    });
}

function excelDown() {
  var $preparingFileModal = $("#preparing-file-modal");
  $preparingFileModal.dialog({modal: true});
  $("#progressbar").progressbar({value: false});
  var col = $("#orderCol").val();
  var sort = $("#orderSort").val();

  var host = $('#chatListUl>li.active').val();
  if (host == undefined || host == "") {
    host = hostToPage;
  }

  $.fileDownload(getContextPath() + '/upload/replaceDictExcelDown?host='+host+'&lang='+nowSearchLang+'&orderCol='+col+'&orderSort='+sort, {
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

function openChatUpload(){
    var winHeight = $(window).height() * 0.7,
        hrefId = "#chat_replace_upload";

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

function uploadExcel(){
  $("#replaceDictHost").val($('#chatListUl>li.active').val());
  $("#replaceDictLang").val(nowSearchLang);

  delCacheNow();
  var txt = '저장';
  var changeTxt = '저장중';
  var file = $("#excelFile").val();
  if (file === "" || file === null) {
      mui.alert('파일을 선택 해주세요.');
  }

  $('#btn_upload').text(changeTxt).addClass('gradient');
  var options = {
      success: function (response) {
          $('#orderCol').val("");
          $('#orderSort').val("");
          $('#btn_upload').text(txt).removeClass('gradient');
          mui.alert(response.status);
          $(".btn_lyr_close").click();
          chatClick(hostToPage, 1);

      }, error: function (response) {
          $('#btn_upload').text(txt).removeClass('gradient');
          mui.alert(response.status != null || response.status != "" ? response.status : "작업 중 오류가 발생했습니다.");
      },
      type: "POST"
  };
  $("#replaceDictUpload").ajaxSubmit(options);
}