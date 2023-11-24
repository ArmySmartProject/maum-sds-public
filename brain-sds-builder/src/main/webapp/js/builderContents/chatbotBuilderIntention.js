var isSearch;
var nqaUpload;

function chatClick(host, cp) {

    var obj = new Object();
    obj.lang = nowSearchLang;
    obj.chatName = $('#chatListUl>li.active>div:first>span').text();
    obj.host = host;
    if ($("#intentCp").val() != undefined && $("#intentCp").val() != 0) {
      obj.cp = $("#intentCp").val();
    } else {
      obj.cp = cp;
    }

    if ($("#intentTextSess").val() != undefined && $("#intentTextSess").val() != 0) {
      obj.searchIntent = $("#intentTextSess").val();
      $('input[name="intentText"]').val($("#intentTextSess").val());
    } else if (isSearch == true) {
      obj.searchIntent = $("input[name='intentText']").val();
      isSearch = false;
    } else {
      obj.searchIntent = "";
    }

    $("#intentCp").val(0);
    $("#intentTextSess").val(0);


    addAjax();
  $.ajax({
        url : "getIntention",
        data : JSON.stringify(obj),
        type: "POST",
        contentType: 'application/json'
    }).success(
        function(result) {
            finAjax();
            checkNqaTrained();
            var list = result.intentStcList;
            var paging = result.paging;
            var cnt = paging.startRow + 1;
            var searchIntent = result.searchIntent;
            $("#searchIntentText").val(searchIntent);

            if($('#chatListUl>li.active>div:first>span').text().length>0){
                $("#chatTitle").text($('#chatListUl>li.active>div:first>span').text());
            }
            hostToPage = parseInt(String(host));
            console.log("hostToPage grim : " + hostToPage);
            $("#intentBody").empty();
            $(".fr.iptBox").empty();
            $("#intentColgroup").empty();
            $("#intentHead").empty();
            var innerHTML = '';
            innerHTML += '<col style="width: 65px;"><col style="width: 15%;"><col>';
            if (nowSearchLang == 1) {
              innerHTML += '<col>';
            }
            innerHTML += '<col style="width: 85px;"><col style="width: 85px;">';
            $("#intentColgroup").append(innerHTML);
            innerHTML = '';
            innerHTML += '<tr><th>순번</th><th>의도</th><th>정규식 문장</th>';
            if (nowSearchLang == 1) {
              innerHTML += '<th>학습 문장<i style="margin: 5px; font-size: 11px">(학습문장은 한국어만 사용 가능합니다.)</i></th>';
              $("#chatbot_learning").css('display', 'inline-block');
            } else {
              $("#chatbot_learning").css('display', 'none');
            }
            innerHTML += ''
                // + '<th>BERT 학습 문장</th>'
                + '<th><span class="text_hide">상세</span></th><th><span class="text_hide">삭제</span></th></tr>';
            $("#intentHead").append(innerHTML);
            innerHTML = '';
            innerHTML += '<input type="text" class="ipt_txt search" name="intentText" autocomplete="off">';
            innerHTML += '<button type="button" class="btn_search" id="searchIntent"><span class="text_hide">검색하기</span></button>';
            $(".fr.iptBox").append(innerHTML);
            $("input[name='intentText']").val(obj.searchIntent);

            innerHTML = "";

            $.each(list, function (i, v) {

                var regexIntentNo = "";
                if (v.regex != undefined) {
                  var regexList = v.regex.split('@');
                }
                if (v.sentences == undefined) {
                    v.sentences = "";
                }
                innerHTML += '<tr>';
                innerHTML += '<td>'+ cnt +'</td>';
                innerHTML += '<td><span name="intent">'+ v.Name +'</span></td>';
                innerHTML += '<td><span class="text_ellipsis">';
                if (regexList != undefined) {
                  for (var i=0; i<regexList.length; i++) {
                    innerHTML += '<span class="regex_type intent" name="regex">' + regexList[i] + '</span>';
                  }
                }
                if (nowSearchLang == 1) {
                  if (v.question != undefined) {
                    innerHTML += '<td><span class="text_ellipsis" name="question">'+ v.question +'</span></td>';
                  } else {
                    innerHTML += '<td></td>';
                  }
                }
                // innerHTML += '<td><span class="text_ellipsis" name="sentences">'+ v.sentences +'</span></td>';
                // innerHTML += '</span>';
                // innerHTML += '</td>';
                if (v.answerId != undefined) {
                  innerHTML += '<td class="no_bg"><button onclick="goIntentionDetail(this.value,' + v.answerId + ',' + v.categoryId + ')" value="'+ v.No +'" class="btn_line_primary">상세</button></td>';
                } else {
                  innerHTML += '<td class="no_bg"><button onclick="goIntentionDetail(this.value, \'\', ' + v.categoryId + ')" value="'+ v.No +'" class="btn_line_primary">상세</button></td>';
                }
                innerHTML += '<td class="no_bg"><a href="#chat_row_delete" class="btn_line_warning btn_lyr_open" onclick="deletePage(\''+ v.Name +'\')">삭제</a></td>';
                innerHTML += '</tr>';

                host = v.host;
                cnt++;
            });

            $("#intentBody").append(innerHTML);
            innerHTML = "";
            $(".pagination").empty();

            var columns = ["intent", "sentences", "regex"];
            if (searchIntent != '') {
              for (var i = 0; i < 3; i++) {
                Array.prototype.forEach.call(document.getElementsByName(columns[i]), function(el) {
                  var regex = new RegExp(searchIntent, "gi");
                  el.innerHTML = el.innerHTML.replace(regex, "<span style='color:red'>"+searchIntent+"</span>");
                });
              }
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

            $('.ipt_txt.search').on('keyup', function(){
              if (event.keyCode == 13) {
                $(this).next('#searchIntent').trigger('click');
              }
            });
            $('#searchIntent').on('click', function(){
              isSearch = true;
              var host = $('#chatListUl>li.active').val();
              chatClick(host, 1);
            });

        }).fail(function(result) {
        finAjax();
        console.log("getIntentList error");
    });
};

$(document).ready(function () {
    var host = $('#chatListUl>li.active').val();
    if(hostToPage==-1 || hostToPage==undefined || isNaN(hostToPage)){chatClick(host, 1);}
    else{chatClick(hostToPage, 1);}
    // AMR 돋보기 아이콘이 있는 검색 영역에서 엔터 시 검색버튼 눌림
    $('.ipt_txt.search').on('keyup', function(){
      if (event.keyCode == 13) {
        $(this).next('#searchIntent').trigger('click');
      }
    });

  $('input[type="file"]').change(function (e) {
    var fileName = e.target.files[0].name;
    $('input[name="excel_file_name"]').val(fileName);
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

    $('#searchIntent').on('click', function(){
      isSearch = true;
      var host = $('#chatListUl>li.active').val();
      chatClick(host, 1);
    });

  });

  function goPage(host, cp){
    if( cp != 0){
      $('#currentPage').val(cp);
    }

    chatClick(host, cp);
  }

  function goIntentionDetail(bertIntentNum, answerId, categoryId){
    var obj = new Object();
    obj.bertIntentNo = bertIntentNum;
    var intentCp = $('.pages .active').text();
    var searchIntentText = $("#searchIntentText").val();

    addAjax();
    $.ajax({
      url : "builderContentsIntentionDetail",
      data : JSON.stringify(obj),
      type: "POST",
      contentType: 'application/json'
    }).success(
        function(result) {
        finAjax();
          $(".menu_cont").empty();
          $(".menu_cont").append(result.toString());
          $("#intentCp").val(intentCp);
          $("#searchIntentText").val(searchIntentText);
          if (answerId != undefined) {
            $("#nqaAnswerId").val(answerId);
          }
          if (categoryId != undefined) {
            $("#categoryId").val(categoryId);
          }
          chatClick('',1, bertIntentNum, "regex");
        })
    addAjax();
    $.ajax({
      url : "builderBottomIntentionDetail",
      data : JSON.stringify(obj),
      type: "POST",
      contentType: 'application/json'
    }).success(
        function(result) {
        finAjax();
        overlayTutorial(5);
          $(".non_wrap").empty();
          $(".non_wrap").append(result.toString());
        }).fail(function(result) {
                  finAjax();
              })
  }

function deletePage (bertIntentName) {
  delCacheNow();
  var winHeight = $(window).height() * 0.7,
      hrefId = "#chat_row_delete";

  $('body').css('overflow', 'hidden');
  $('body').find(hrefId).wrap('<div class="lyrWrap"></div>');
  $('body').find(hrefId).before('<div class="lyr_bg"></div>');
  $('#bertIntentName').val(bertIntentName);
  //대화 UI
  $('.lyrBox .lyr_mid').each(function () {
    $(this).css('max-height', Math.floor(winHeight) + 'px');
  });

};

function addPage() {
  delCacheNow();
  var winHeight = $(window).height() * 0.7,
      hrefId = "#chat_intention_add";

  $('body').css('overflow', 'hidden');
  $('body').find(hrefId).wrap('<div class="lyrWrap"></div>');
  $('body').find(hrefId).before('<div class="lyr_bg"></div>');
  //대화 UI
  $('#label_intention_add').val("");
  $('.lyrBox .lyr_mid').each(function () {
    $(this).css('max-height', Math.floor(winHeight) + 'px');
  });
  overlayTutorial(3);
};

  function deleteIntention () {
    var obj = new Object();
    obj.bertIntentName = $('#bertIntentName').val();
    var host = $('#chatListUl>li.active').val();
    if (host == undefined || host == "") {
      host = hostToPage;
    }
    obj.host = host;
    addAjax();
    $.ajax({
      url : "deleteIntent",
      data : JSON.stringify(obj),
      type: "POST",
      contentType: 'application/json'
    }).success(
        function(result) {
        finAjax();
        delCacheNow();
          mui.alert("삭제가 완료되었습니다.");
          closeBtn('#chat_row_delete');
          var host = $('#chatListUl>li.active').val();
          chatClick(host, 1);
        }).fail(function(result) {
        finAjax();
          mui.alert("삭제를 실패하였습니다.");
          closeBtn('#chat_row_delete');
    });
  }

  function addIntention() {
    delCacheNow();
    var obj = new Object();
    var bertIntent = $('#label_intention_add').val();
    if (hostToPage==-1 || hostToPage==undefined || isNaN(hostToPage)) {
      mui.alert("의도 추가할 챗봇이 없습니다.");
      return;
    }
    console.log("hostToPage null :" + hostToPage );
    var host = $('#chatListUl>li.active').val();
    if (host == undefined || host == "") {
      host = hostToPage;
    }
    obj.bertIntent = bertIntent;
    obj.host = host;
    addAjax();
    $.ajax({
      url : "insertIntention",
      data : JSON.stringify(obj),
      type: "POST",
      contentType: 'application/json'
    }).success(
        function(result) {
        finAjax();
          if (result.result == 'success') {
            mui.alert("의도추가가 완료됐습니다.");
            closeBtn('#chat_intention_add');
            var host = $('#chatListUl>li.active').val();
            console.log("host add: " + host);
            console.log("hosttoPage add : " + hostToPage);
            if (host == undefined || host == "") {
              host = hostToPage;
            }
            chatClick(host, 1);
          } else {
            mui.alert("중복된 의도명 입니다.");
          }
        }).fail(function(result) {
        finAjax();
          mui.alert("의도추가가 실패했습니다.");
          closeBtn('#chat_intention_add')
    });
    overlayTutorial(4);
  }


function nqaExcelDownload(){
  var obj = new Object();
  obj.channelId = $("#chatListUl>li.active").val();
  $("#nqa_excel_download_ing").show();
  $("#nqa_excel_download").hide();

  return fetch('api/nqa/qa-sets/download-file', {
      method: 'POST',
      mode: 'cors',
      cache: 'no-cache',
      credentials: 'same-origin',
      headers: {
          'Content-Type': 'application/json'
      },
      redirect: 'follow',
      referrer: 'no-referrer',
      body: JSON.stringify(obj),
      timeOut: 300000
  }).then(res => {
      $("#nqa_excel_download_ing").hide();
      $("#nqa_excel_download").show();
      if(res.status == 424 || res.status == 200) {
          return res.blob();
      }else{
          return;
      }
  }).then(function (blob){
      if(!blob){
          mui.alert('excel file download failed.');
      }else if(blob.size == 0 && blob.type == ''){
          mui.alert('다운로드할 데이터가 존재하지 않습니다.');
      }else {
          var a = document.createElement("a");
          var url = URL.createObjectURL(blob);
          a.href = url;
          a.download = 'NQAItemList.xlsx';
          document.body.appendChild(a);
          a.click();
          window.URL.revokeObjectURL(url);
      }
  });
}

function closeBtn(modal) {
  var hrefId = modal;
  $('body').css('overflow', '');
  $('body').find(hrefId).unwrap('<div class="lyrWrap"></div>');
  if ($(".lyrWrap .lyr_bg").length == 0) {
    $('.lyr_bg').remove();
  }

  if(modal == '#nqa_train_wait'){
    clearInterval(waitInterval);
  }
}

function uploadExcel() {
  delCacheNow()
  var txt = '저장';
  var changeTxt = '저장중';
  var host = $("#chatListUl>li.active").val();
  var hostName = $("#chatListUl>li.active div:nth-child(2) span").text().split(":");
  hostName = hostName[1].replace(" ", "");
  var file = $("#excelFile").val();
  if (file === "" || file === null) {
    mui.alert('파일을 선택 해주세요.');
  }

  $('#btn_upload').text(changeTxt).addClass('gradient');
  var options = {
    success: function (response) {
      $('#btn_upload').text(txt).removeClass('gradient');
      // mui.alert("모든 데이터가 업로드 되었습니다.");
      getNqaCount();
      getNqaUpdatedDtm();
      chatClick(host, 1);
      $('#chat_intention_upload .lyr_top .btn_lyr_close').trigger('click');
    }, error: function (response) {
      $('#btn_upload').text(txt).removeClass('gradient');
      mui.alert("작업 중 오류가 발생했습니다.");
      console.log(response);
      $('#chat_intention_upload .lyr_top .btn_lyr_close').trigger('click');
    },
    type: "POST"
  };
  $("#intentUpload input[name='host']").val(host);
  $("#intentUpload input[name='hostName']").val(hostName);
  $("#intentUpload").ajaxSubmit(options);
  nqaUploadConfirm();
}

var trainInterval = false;
var checkTrainingHost = true;
var startIndexing = false;
var waitChannelId = 0;
var waitInterval = null;
var firstCheck = true;

function nqaTrain() {
  var obj = new Object();
  var channelId = $("#chatListUl>li.active").val();
  obj.channelId = channelId;
  obj.checkTrainingHost = true;

  addAjax();
  $.ajax({
    url : "api/nqa/indexing/getIndexingStatus",
    data : JSON.stringify(obj),
    type: "POST",
    dataType : 'json',
    contentType: 'application/json'
  }).success(function(data) {
    finAjax();

    if (data.message != channelId.toString() && data.status){
      console.log("학습중 : " + data.status);
      firstCheck = true;
      waitChannelId = data.message;
      waitProgressBar();
      waitInterval = setInterval(checkWaitTrain, 1000);
    } else {
      console.log("이전 학습완료 : " + data.status);
      trainInterval = false;
      firstCheck = false;
      nqaTrain02();
    }
  }).error(function(data,errorThrown) {
    console.log("first train nqa Error");
    finAjax();
  });

}

function checkWaitTrain(){
  console.log('nqa train wait~~');

  var obj = new Object();
  obj.channelId = waitChannelId;
  obj.checkTrainingHost = true;

  addAjax();
    $.ajax({
      url : "api/nqa/indexing/getIndexingStatus",
      data : JSON.stringify(obj),
      type: "POST",
      contentType: 'application/json'
    }).success(function(data) {
      finAjax();
      if(data.status){
        var per = data.processed / data.total * 100;
        $('#nqa_wait_progress_bar .progress_bar').css({'transform':'translate('+ per +'%)'});
      }else{
        clearInterval(waitInterval);
        closeBtn('#nqa_train_wait');
        firstCheck = false;
        trainInterval = false;
        setTimeout(nqaTrain02,2000);
      }
      console.log("wait per : " + per);
    }).error(function(data,error) {
      finAjax();
      console.log('nqa wait Error');
    });
}

function nqaTrain02() {
  delCacheNow();
  console.log('nqa train start!');

  toggleTrainUi(true);
  $('#nqa_progress_bar .progress_bar').css({'transform':'translate(0%)'});

  var obj = new Object();
  obj.channelId = $("#chatListUl>li.active").val();
  addAjax();
  $.ajax({
    url : "api/nqa/indexing/fullIndexing",
    data : JSON.stringify(obj),
    type: "POST",
    contentType: 'application/json'
  }).success(function(result) {
    overlayTutorial(9);
    finAjax();

  }).fail(function(result) {
    finAjax();
    if (result.status == 424) {
      mui.alert('학습할 데이터가 존재하지 않습니다. 데이터를 먼저 추가해주세요.')
    } else {
      mui.alert('시스템상의 문제로 학습에 실패하였습니다. 잠시 후 다시 시도해주세요.')
    }
  });

  if (!trainInterval) {
    startIndexing = true;
    checkTrainingHost = true;
    trainInterval = setInterval(checkTrainPercent, 1000);
  }

}


function checkTrainPercent() {
  console.log('check nqa train percent');
  var obj = new Object();
  var channelId = $("#chatListUl>li.active").val();
  obj.channelId = channelId;
  obj.checkTrainingHost = checkTrainingHost;
  addAjax();
  $.ajax({
    url : "api/nqa/indexing/getIndexingStatus",
    data : JSON.stringify(obj),
    type: "POST",
    contentType: 'application/json'
  }).success(function(result) {
    finAjax();
    var per = result.processed / result.total * 100;
    console.log("result.message : " + result.message);
    if ((result.message === null || result.message == channelId.toString() || (result.message != channelId.toString() && !firstCheck))
        && per < 100) {
      checkTrainingHost = false; // 학습중인 host가 현재 host와 같은 것을 확인하였으니 매번 db 조회하지 않도록 false로 바꿔준다.
      if ($('#nqa_progress_bar').css('display') == 'none') {
        toggleTrainUi(true);
      }

      $('#nqa_progress_bar .progress_bar').css({'transform':'translate('+ per +'%)'});
    } else {
      if(per == 100){
          mui.alert('학습이 완료 되었습니다.');
      }
      clearTrainStatus();
    }
    console.log("per : " + per);
    console.log("startIndexing : " + startIndexing);

  }).fail(function(result,error) {
    finAjax();
    console.log('getIndexingStatus failed');
    console.log('responseText : ' + result.responseText );
    console.log('error : ' + error );
    clearTrainStatus();
  });
}

function clearTrainStatus() {
  // indexing 완료 후 result값이 fail로 돌아오기 때문에 success에서 clear해주지 않음
  clearInterval(trainInterval);
  trainInterval = false;
  toggleTrainUi(false);
  getTrainHistory();
  // 학습이 가능한 상태이므로 status 조회시 host 체크할 필요 없음
  checkTrainingHost = false;
  firstCheck=true;
}

function toggleTrainUi(isTraining) {
  if (isTraining) { // 학습중
    $('#nqa_trained_at').css('display', 'none');
    $('#nqa_progress_bar').css('display', 'block');
    $('#nqa_train_button').attr('disabled', true);
    $('#alert_nqa_train_start').css('display', 'none');
    $('#alert_nqa_training').css('display', 'block');
  } else { // 학습 가능
    $('#nqa_trained_at').css('display', 'block');
    $('#nqa_progress_bar').css('display', 'none');
    $('#nqa_train_button').attr('disabled', false);
    $('#alert_nqa_training').css('display', 'none');
  }
}

function handleLyr() {
    delCacheNow();
    overlayTutorial(8);
    var winHeight = $(window).height() * 0.7,
        hrefId = $(event.target).attr('href'),
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

    $('.lyrBox .lyr_mid').each(function () {
        $(this).css('max-height', Math.floor(winHeight) + 'px');
    });

  getNqaCount();
  if (!trainInterval) {
    startIndexing = false;
    checkTrainingHost = true;
    trainInterval = setInterval(checkTrainPercent, 1000);
  } else {
      nqaUploadConfirm();
  }

    //Layer popup close lyr_bg click
    $('.lyr_bg').on('click', function (e) {
        hrefId = $(this).next('.lyrBox').attr('id');
        var id = '#' + hrefId;
        thisLayer = $(id);
        e.stopPropagation();
        lyrClose(thisLayer);
    });

}

function waitProgressBar(){
  var winHeight = $(window).height() * 0.7,
      hrefId = "#nqa_train_wait";
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

  //Layer popup close
  $('.btn_lyr_close').on('click', function() {
    $('.actPop').removeClass('actPop');
    $('body').css('overflow', '');
    $('body').find(hrefId).unwrap('<div class="lyrWrap"></div>');
  });
}

function getNqaCount() {
  var obj = new Object();
  obj.channelId = $("#chatListUl>li.active").val();
  addAjax();
  $.ajax({
    url : "api/nqa/qa-sets/answers/qaCount",
    data : JSON.stringify(obj),
    type: "POST",
    contentType: 'application/json'
  }).success(function(result) {
    finAjax();
    console.log('getNqaCount');
    console.log(result);
    $('#nqa_answer_cnt').text(result.totalCount);
    $('#nqa_question_cnt').text(result.questionCount);
  }).fail(function(result) {
    finAjax();
    console.log('getNqaCount failed');
    console.log(result);
    $('#nqa_answer_cnt').text('-');
    $('#nqa_question_cnt').text('-');
  });
}

function getTrainHistory() {
  return new Promise((resolve, reject) => {
    var obj = new Object();
    obj.channelId = $("#chatListUl>li.active").val();
    addAjax();
    $.ajax({
      url : "api/nqa/indexing/getIndexingHistory",
      data : JSON.stringify(obj),
      type: "POST",
      contentType: 'application/json'
    }).success(function(result) {
      finAjax();
      console.log('getIndexingHistory success');
      console.log(result);
      $('#nqa_trained_at').text(result.createdAt);
      resolve(true);
    }).fail(function(result) {
      finAjax();
      console.log('getIndexingHistory failed');
      console.log(result);
      $('#nqa_trained_at').text('-');
      resolve(false);
    });
  });
}

function getNqaUpdatedDtm() {
  return new Promise((resolve, reject) => {
    var obj = new Object();
    obj.channelId = $("#chatListUl>li.active").val();
    addAjax();
    $.ajax({
      url : "api/nqa/qa-sets/answers/getChannelInfo",
      data : JSON.stringify(obj),
      type: "POST",
      contentType: 'application/json'
    }).success(function(result) {
      finAjax();
      console.log('getNqaUpdatedDtm success');
      console.log(result);
      if (result.status == 200) {
        if (result.isValid) {
          var channelInfo = result.channelInfo;
          $('#nqa_updated_at').text(channelInfo.updateDtm);
        } else {
          $('#nqa_updated_at').text('-');
        }
        resolve(true);
      } else if (result.status == 500) {
        $('#nqa_updated_at').text('-');
        resolve(false);
      }
    }).fail(function(result) {
      finAjax();
      console.log('getNqaUpdatedDtm failed');
      console.log(result);
      $('#nqa_updated_at').text('-');
      resolve(false);
    });
  });
}

function checkNqaTrained() {
  getNqaUpdatedDtm().then((res) => {
    getTrainHistory().then((res2) => {
      var updateTime = $('#nqa_updated_at').text();
      var trainedTime = $('#nqa_trained_at').text();
      console.log('nqa updateTime: ' + updateTime + ' / trainedTime: ' + trainedTime);
      if (updateTime != '-' && trainedTime == '-') {
        $('#alert_nqa_train_start').css('display', 'block');
      } else if (Date.parse(updateTime) > Date.parse(trainedTime) && $('#alert_nqa_training').css('display') !== 'block') {
        $('#alert_nqa_train_start').css('display', 'block');
      } else if ((Date.parse(updateTime) < Date.parse(trainedTime)) || (updateTime == '-' && trainedTime == '-')) {
        $('#alert_nqa_train_start').css('display', 'none');
      }
    });
  });
}

function regexExcelModalOpen() {
    var winHeight = $(window).height() * 0.7,
        hrefId = "#chat_regex_upload";

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

function regexExcelUpload(){
    var txt = '저장';
    var changeTxt = '저장중';
    var host = $("#chatListUl>li.active").val();
    if (host == undefined || host == "") {
        host = hostToPage;
    }
    var file = $("#regexExcelFile").val();
    if (file === "" || file === null) {
        mui.alert('파일을 선택 해주세요.');
    }

    $('#btn_regex_upload').text(changeTxt).addClass('gradient');
    var options = {
        success: function (response) {
            $('#btn_regex_upload').text(txt).removeClass('gradient');
            mui.alert(response.status);
            $('#chat_regex_upload .btnBox .btn_lyr_close').click();
            changeContents(1);
        }, error: function (response) {
            $('#btn_regex_upload').text(txt).removeClass('gradient');
            if(response.status == undefined || response.status == ""){
                mui.alert("작업 중 오류가 발생했습니다.");
            }else{
                mui.alert(response.status);
            }
        },
        type: "POST"
    };
    $("#regexUpload input[name='host']").val(host);
    $("#regexUpload").ajaxSubmit(options);
}

function regexExcelDownload() {
    var $preparingFileModal = $("#preparing-file-modal");
    $preparingFileModal.dialog({modal: true});
    $("#progressbar").progressbar({value: false});
    var obj = new Object();
    var host = $('#chatListUl>li.active').val();
    if (host == undefined || host == "") {
        host = hostToPage;
    }
    obj.host = host;
    $.fileDownload(getContextPath() + '/upload/regexExcelDown', {
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

function getNqaUploadStatus(){
    var obj = new Object();
    obj.host = $("#chatListUl>li.active").val();

    addAjax();
    $.ajax({
        url : "getNqaStatus",
        data : JSON.stringify(obj),
        type: "POST",
        contentType: 'application/json'
    }).success(function(result) {
        finAjax();
        // result.nqaUploadStatus - uploading(진행중), success(업로드 성공), fail(업로드 실패), preparation(준비상태)
        if(result.nqaUploadStatus == 'uploading'){
            $('#alert_nqa_train_start').hide();
            $('#alert_nqa_training').hide();
            $('#alert_nqa_uploading').show();
            $('#nqaExcelUploadBtn').hide();
            $('#nqaExcelUploadingBtn').show();
            $('#nqa_train_button').attr('disabled', true);
        }else{
            $('#alert_nqa_uploading').hide();
            $('#nqaExcelUploadBtn').show();
            $('#nqaExcelUploadingBtn').hide();
            $('#nqa_train_button').attr('disabled', false);
            clearInterval(nqaUpload);
            checkNqaTrained();
        }
    }).fail(function(result) {
        finAjax();
        console.log("getNqaUploadStatus error");
    });
}

function nqaUploadConfirm(){
    clearInterval(nqaUpload);
    nqaUpload = setInterval(getNqaUploadStatus, 1000);
    clearTrainStatus();
}

