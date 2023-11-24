var duplicate = false;
var regexLang;
var isSearch;

function chatClick(temp, cp, bertIntentNo, type) {

    var obj = new Object();
    if (bertIntentNo == undefined || bertIntentNo == '') {
      obj.host = $('#chatListUl>li.active').val();
      obj.bertIntentName = $("#intentTitle").text();
    } else {
      obj.bertIntentNo = bertIntentNo;
    }
    if (isSearch == true) {
      obj.searchSentence = $("input[name='searchIntentDetail']").val();
      isSearch = false;
    } else {
      obj.searchSentence = "";
    }
    obj.cp = cp;
    if (regexLang == undefined || regexLang == "") {
      obj.lang = nowSearchLang;
    } else {
      obj.lang = regexLang;
      nowSearchLang = regexLang;
      var nowCount = 1;
      $("#upper_lang_list").find('ul').find('li').each( function() {
        $(this).removeClass("active");
        if(regexLang==nowCount){
          $(this).addClass("active");
        }
        nowCount++;
      });
    }
    if (type == undefined || type == '') {
      type = $(".btn_switch>button.active")[0].id;
    }
  if (type == 'regex') {
      addAjax();
    $.ajax({
      url : "getRegexList",
      data : JSON.stringify(obj),
      type: "POST",
      contentType: 'application/json'
    }).success(
        function(result) {
            finAjax();
          var list = result.regexList;
          var paging = result.paging;
          var cnt = paging.startRow + 1;
          var searchSentence = result.searchSentence;
          var guide = ['시작 텍스트', '텍스트가 정확하게 일치함', '텍스트에 포함', '텍스트에 포함하지 않음', '종료 텍스트'];
          var type = ['start', 'match', 'include', 'exclude', 'end'];
          if($('#chatListUl>li.active>div:first>span').text().length>0){
            $("#chatTitle").text($('#chatListUl>li.active>div:first>span').text());
          }
          $("#btn_addRegex").show();
          $("#sentenceBody").empty();
          $("#colType").empty();
          $("#intentHead").empty();
          $(".fr.iptBox").empty();
          langCheck();
          $(".tbl_box_info").empty();
          var innerHTML = "";
          innerHTML += '<div class="fl">';
          // innerHTML += '<p class="path"><em><a href="chatbotBuilderIntention.html" title="의도로 돌아가기">의도</a></em><span>&raquo;</span><em>메뉴</em></p>';
          innerHTML += '<p class="path"><em><a href="javascript:goIntention()" title="의도로 돌아가기">의도</a></em><span>&colon;</span><em id="intentTitle">'+list[0].Name+'</em></p>';
          innerHTML += '<div class="btn_switch">';
          innerHTML +=  '<button type="button" id="regex" class="primary active">정규식</button>';
          if (nowSearchLang == 1) {
            innerHTML +=  '<button type="button" id="nqa" class="primary">학습문장</button>';
          }
          // innerHTML +=  '<button type="button" id="sentence" class="primary">BERT 학습문장</button>';
          innerHTML +=  '</div>';
          innerHTML +=  '</div>';
          innerHTML += '<input type="hidden" id="bertIntentNo" value="'+ list[0].BertIntentNo +'">';
          innerHTML +=  '<div id="div_add_modal" class="fr">';
          innerHTML +=  '<a href="#chat_regex_setting" id="btn_addRegex" class="btn_secondary btn_lyr_open" onclick="addRegex()">개별 추가</a>';
          innerHTML +=  '</div>';

          $(".tbl_box_info").append(innerHTML);
          innerHTML = '';
          innerHTML += '<input type="text" class="ipt_txt search" name="searchIntentDetail" autocomplete="off">';
          innerHTML += '<button type="button" id="intention_detail" class="btn_search"><span class="text_hide">검색하기</span></button>';
          $(".fr.iptBox").append(innerHTML);
          $("input[name='searchIntentDetail']").val(obj.searchSentence);
          innerHTML = '';
          innerHTML += '<col style="width: 65px;">';
          innerHTML += '<col style="width: 100%;">';
          innerHTML += '<col style="width: 100%;">';
          innerHTML += '<col style="width: 85px;">';
          innerHTML += '<col style="width: 85px;">';
          $("#colType").append(innerHTML);
          innerHTML = '';
          innerHTML += '<tr>';
          innerHTML += '<th>순번</th>';
          innerHTML += '<th>규칙</th>';
          innerHTML += '<th>정규식</th>';
          innerHTML += '<th><span class="text_hide">상세</span></th>';
          innerHTML += '<th><span class="text_hide">답변</span></th>';
          innerHTML += '</tr>';
          $("#intentHead").append(innerHTML);
          if (searchSentence == "") {
            $(".tbl_box_info").empty();

            innerHTML = "";
            innerHTML += '<div class="fl">';
            // innerHTML += '<p class="path"><em><a href="chatbotBuilderIntention.html" title="의도로 돌아가기">의도</a></em><span>&raquo;</span><em>메뉴</em></p>';
            innerHTML += '<p class="path"><em><a href="javascript:goIntention()" title="의도로 돌아가기">의도</a></em><span>&colon;</span><em id="intentTitle">'+list[0].Name+'</em></p>';
            innerHTML += '<div class="btn_switch">';
            innerHTML +=  '<button type="button" id="regex" class="primary active">정규식</button>';
            if (nowSearchLang == 1) {
              innerHTML +=  '<button type="button" id="nqa" class="primary">학습문장</button>';
            }
            // innerHTML +=  '<button type="button" id="sentence" class="primary">BERT 학습문장</button>';
            innerHTML +=  '</div>';
            innerHTML +=  '</div>';
            innerHTML += '<input type="hidden" id="bertIntentNo" value="'+ list[0].BertIntentNo +'">';
            innerHTML +=  '<div id="div_add_modal" class="fr">';
            innerHTML +=  '<a href="#chat_regex_setting" id="btn_addRegex" class="btn_secondary btn_lyr_open" onclick="addRegex()">개별 추가</a>';
            innerHTML +=  '</div>';

            $(".tbl_box_info").append(innerHTML);
          }
          innerHTML = '';

          $.each(list, function (i, v) {
            if (v.RegexRule != undefined) {
              var regexList = v.RegexRule.split('@');
            }
            if (v.Regex != undefined) {
              innerHTML += '<tr>';
              innerHTML += '<td>'+ cnt +'</td>';
              innerHTML += '<td>';
              if (v.RegexRule != undefined) {
                for (var i=0; i<regexList.length; i++) {
                  var valueNType = regexList[i].split('#');
                  var value = valueNType[0].split('|');
                  for (var j=0; j<value.length; j++) {
                    if (value[j] != "") {
                      innerHTML += '<span class="regex_type ' + valueNType[1] + '" name="regex">' + value[j] + '<div class="regex_desc"><i>';
                      for (var k=0; k<type.length; k++) {
                        if (valueNType[1] == type[k]){
                          innerHTML += guide[k];
                        }
                      }
                      innerHTML += '</i></div></span>';
                    }
                  }
                }
              }
              innerHTML += '<td>'+ v.Regex +'</td>';
              innerHTML += '<input type="hidden" id="regexIntentNo" value="'+ v.RegexIntentNo +'">';
              innerHTML += '</td>';
              innerHTML += '<td class="no_bg"><a href="#chat_regex_setting" class="btn_line_primary btn_lyr_open" onclick="editRegex('+ v.RegexIntentNo +',\'' + v.Regex + '\')">상세</a></td>';
              innerHTML += '<td class="no_bg"><a href="#regex_delete" class="btn_line_warning btn_lyr_open" onclick="deletePage('+ v.RegexIntentNo +', \'regex\')">삭제</a></td>';
              innerHTML += '</tr>';
              cnt++;
            }
          });

          $("#sentenceBody").append(innerHTML);
          innerHTML = "";
          $(".pagination").empty();

          if (searchSentence != '') {
            Array.prototype.forEach.call(document.getElementsByName("regex"),
                function (el) {
                  var regex = new RegExp(searchSentence, "gi");
                  el.innerHTML = el.innerHTML.replace(regex,
                      "<span style='color:red'>" + searchSentence + "</span>");
                });
          }

          innerHTML += '<button type="button" class="first" onclick="goDetailPage(1)"><span>&laquo;</span></button>';
          innerHTML += '<button type="button" class="prev" onclick="goDetailPage('+ paging.prevPage +')"><span>&lsaquo;</span></button>';
          innerHTML += '<div class="pages">';

          for (var i = paging.pageRangeStart; i<=paging.pageRangeEnd; i++){
            if(i == paging.currentPage){
              innerHTML += '<span class="page active">'+ i +'</span>';
            }else{
              innerHTML += '<span class="page" onclick="goDetailPage('+ i +')">'+ i +'</span>';
            }
          }

          innerHTML += '</div>';
          innerHTML += '<button type="button" class="next" onclick="goDetailPage('+ paging.nextPage +')"><span>&rsaquo;</span></button>';
          innerHTML += '<button type="button" class="last" onclick="goDetailPage('+ paging.totalPage +')"><span>&raquo;</span></button>';
          innerHTML += '</div>';

          $(".pagination").append(innerHTML);
          $('.btn_switch button').on('click', function(){
            var $switch = $(this).parent();
            $switch.find('button').removeClass('active');
            $(this).addClass('active');
            if (this.id == 'regex') {
              chatClick('',1, '', 'regex');
            } else if (this.id == 'sentence') {
              chatClick('',1, '', 'sentence');
            } else {
              chatClick('',1, '', 'nqa');
            }
          });

          regexLang = "";
          $('.ipt_txt.search').on('keyup', function () {
            if (event.keyCode == 13) {
              $(this).next('#intention_detail').trigger('click');
            }
          });
          $('#intention_detail').on('click', function () {
            isSearch = true;
            goDetailPage(1);
          });
          // new Sortable(regexList, {
          //   animation: 150,
          //   ghostClass: 'blue-background-class',
          //   onEnd: function () {
          //     getRegexResult();
          //   }
          // });

// 추가AMR 정규식 추가 셀렉트 타입별 UI가 바뀌는 이벤트
//           renderRegexSelect($('select[name="regex_type"]'));

        }).fail(function(result) {
        finAjax();
      console.log("getCustInfoList error");
    });
  } else if (type == 'sentence'){
      addAjax();
    $.ajax({
      url : "getIntentionDetail",
      data : JSON.stringify(obj),
      type: "POST",
      contentType: 'application/json'
    }).success(
        function(result) {
            finAjax();
          var list = result.sentenceList;
          var paging = result.paging;
          var cnt = paging.startRow + 1;
          var searchSentence = result.searchSentence;
          if($('#chatListUl>li.active>div:first>span').text().length>0){
            $("#chatTitle").text($('#chatListUl>li.active>div:first>span').text());
          }
          $("#colType").empty();
          $("#intentHead").empty();
          $("#div_add_modal").empty();
          $(".fr.iptBox").empty();
          langCheck();
          $(".tbl_box_info").empty();
          var innerHTML = "";
          innerHTML += '<div class="fl">';
          // innerHTML += '<p class="path"><em><a href="chatbotBuilderIntention.html" title="의도로 돌아가기">의도</a></em><span>&raquo;</span><em>메뉴</em></p>';
          innerHTML += '<p class="path"><em><a href="javascript:goIntention()" title="의도로 돌아가기">의도</a></em><span>&colon;</span><em id="intentTitle">'+list[0].Name+'</em></p>';
          innerHTML += '<div class="btn_switch">';
          innerHTML +=  '<button type="button" id="regex" class="primary">정규식</button>';
          if (nowSearchLang == 1) {
            innerHTML +=  '<button type="button" id="nqa" class="primary">학습문장</button>';
          }
          // innerHTML +=  '<button type="button" id="sentence" class="primary active">BERT 학습문장</button>';
          innerHTML +=  '</div>';
          innerHTML +=  '</div>';
          innerHTML += '<input type="hidden" id="bertIntentNo" value="'+ list[0].BertIntentNo +'">';
          innerHTML +=  '<div id="div_add_modal" class="fr">';
          innerHTML +=  '</div>';

          $(".tbl_box_info").append(innerHTML);
          innerHTML = '';
          innerHTML += '<input type="text" class="ipt_txt search" name="searchIntentDetail" autocomplete="off">';
          innerHTML += '<button type="button" id="intention_detail" class="btn_search"><span class="text_hide">검색하기</span></button>';
          $(".fr.iptBox").append(innerHTML);
          $("input[name='searchIntentDetail']").val(obj.searchSentence);
          innerHTML = '';
          //카테고리 임시 주석
          //innerHTML = '<col style="width: 65px;"><col style="width: 130px;"><col>';
          innerHTML = '<col style="width: 65px;"><col>';
          $("#colType").append(innerHTML);
          innerHTML = '';
          innerHTML += '<tr>';
          innerHTML += '<th>순번</th>';
          // innerHTML += '<th>카테고리</th>';
          innerHTML += '<th>문장</th>';
          innerHTML += '</tr>';
          $("#intentHead").append(innerHTML);
          $("#sentenceBody").empty();

          innerHTML = "";

          $.each(list, function (i, v) {

            if (v.Sentence != undefined) {
              innerHTML += '<tr>';
              innerHTML += '<td>'+ cnt +'</td>';
              // innerHTML += '<td>카테고리</td>';
              innerHTML += '<td name="sentence">'+ v.Sentence +'</td>';
              innerHTML += '</tr>';

              cnt++;
            }

          });

          $("#sentenceBody").append(innerHTML);
          innerHTML = "";
          $(".pagination").empty();

          if (searchSentence != '') {
            Array.prototype.forEach.call(document.getElementsByName("sentence"), function(el) {
              var regex = new RegExp(searchSentence, "gi");
              el.innerHTML = el.innerHTML.replace(regex, "<span style='color:red'>"+searchSentence+"</span>");
            });
          }

          innerHTML += '<button type="button" class="first" onclick="goDetailPage(1)"><span>&laquo;</span></button>';
          innerHTML += '<button type="button" class="prev" onclick="goDetailPage('+ paging.prevPage +')"><span>&lsaquo;</span></button>';
          innerHTML += '<div class="pages">';

          for (var i = paging.pageRangeStart; i<=paging.pageRangeEnd; i++){
            if(i == paging.currentPage){
              innerHTML += '<span class="page active">'+ i +'</span>';
            }else{
              innerHTML += '<span class="page" onclick="goDetailPage('+ i +')">'+ i +'</span>';
            }
          }

          innerHTML += '</div>';
          innerHTML += '<button type="button" class="next" onclick="goDetailPage('+ paging.nextPage +')"><span>&rsaquo;</span></button>';
          innerHTML += '<button type="button" class="last" onclick="goDetailPage('+ paging.totalPage +')"><span>&raquo;</span></button>';
          innerHTML += '</div>';

          $(".pagination").append(innerHTML);
          $('.btn_switch button').on('click', function(){
            var $switch = $(this).parent();
            $switch.find('button').removeClass('active');
            $(this).addClass('active');
            if (this.id == 'regex') {
              chatClick('',1, '', 'regex');
            } else if (this.id == 'sentence') {
              chatClick('',1, '', 'sentence');
            } else {
              chatClick('',1, '', 'nqa');
            }
          });

          $('.ipt_txt.search').on('keyup', function () {
            if (event.keyCode == 13) {
              $(this).next('#intention_detail').trigger('click');
            }
          });
          $('#intention_detail').on('click', function () {
            isSearch = true;
            goDetailPage(1);
          });

        }).fail(function(result) {
        finAjax();
      console.log("getCustInfoList error");
    });
  } else {
    obj.answerId = $("#nqaAnswerId").val();
    obj.Intent = $("#intentTitle").text();
      addAjax();
    $.ajax({
      url : "api/nqa/qa-sets/getNqaDetail",
      data : JSON.stringify(obj),
      type: "POST",
      contentType: 'application/json'
    }).success(
        function(result) {
            finAjax();
          var list = result.questionList;
          $("#colType").empty();
          $("#intentHead").empty();
          $("#div_add_modal").empty();
          $(".fr.iptBox").empty();
          $("#upper_lang_list").find('ul').find('li').each(function(i, v) {
            $(this).removeClass("active");
            if(i == 0){
              $(this).addClass("active");
            }
          });
          nowSearchLang = 1;
          $(".us").addClass('none');
          $(".japan").addClass('none');
          $(".china").addClass('none');
          $("#nqa_guide").css({display:"inline-block", margin:"5px"});

          var innerHTML = '';
          innerHTML +=  '<div id="div_add_modal" class="fr">';
          innerHTML +=  '<a href="#chat_nqa_setting" id="btn_addNqa" class="btn_secondary btn_lyr_open" onclick="addNqa()">개별 추가</a>';
          innerHTML +=  '</div>';
          $("#div_add_modal").append(innerHTML);

          innerHTML = '';
          //카테고리 임시 주석
          //innerHTML = '<col style="width: 65px;"><col style="width: 130px;"><col>';
          innerHTML = '<col style="width: 65px;"><col>';
          innerHTML += '<col style="width: 85px;">';
          innerHTML += '<col style="width: 85px;">';
          $("#colType").append(innerHTML);
          innerHTML = '';
          innerHTML += '<tr>';
          innerHTML += '<th>순번</th>';
          // innerHTML += '<th>카테고리</th>';
          innerHTML += '<th>문장</th>';
          innerHTML += '<th><span class="text_hide">상세</span></th>';
          innerHTML += '<th><span class="text_hide">답변</span></th>';
          innerHTML += '</tr>';
          $("#intentHead").append(innerHTML);
          $("#sentenceBody").empty();

          innerHTML = "";
          var cnt = 1;

          $.each(list, function (i, v) {

            if (v.question != undefined) {
              innerHTML += '<tr>';
              innerHTML += '<td>'+ cnt +'</td>';
              // innerHTML += '<td>카테고리</td>';
              innerHTML += '<td name="question">'+ v.question +'</td>';
              innerHTML += '<td class="no_bg"><a href="#chat_nqa_setting" class="btn_line_primary btn_lyr_open" onclick="editNqa('+ v.id +',\''+ v.question +'\')">상세</a></td>';
              innerHTML += '<td class="no_bg"><a href="#nqa_delete" class="btn_line_warning btn_lyr_open" onclick="deletePage('+ v.id +', \'nqa\')">삭제</a></td>';
              innerHTML += '</tr>';

              cnt++;
            }

          });

          $("#sentenceBody").append(innerHTML);
          $('.btn_switch button').on('click', function(){
            var $switch = $(this).parent();
            $switch.find('button').removeClass('active');
            $(this).addClass('active');
            if (this.id == 'regex') {
              chatClick('',1, '', 'regex');
            } else if (this.id == 'sentence') {
              chatClick('',1, '', 'sentence');
            } else {
              chatClick('',1, '', 'nqa');
            }
          });

        }).fail(function(result) {
        finAjax();
      console.log("getCustInfoList error");
    });
  }
};

function goIntention() {
  goPage('previous');
}

function goPage(type) {

  var obj = new Object();
  var intentCp = "";
  var searchIntentText = "";
  if (type == 'previous') {
    intentCp = $("#intentCp").val();
    searchIntentText = $("#searchIntentText").val();
  } else {
    hostToPage = type;
  }

    addAjax();
  $.ajax({
    url : "builderContentsIntention",
    data : JSON.stringify(obj),
    type: "POST",
    contentType: 'application/json'
  }).success(
      function(result) {
          finAjax();
        var innerHTML = '<input type="hidden" id="intentCp" value="'+intentCp+'">';
        innerHTML += '<input type="hidden" id="intentTextSess" value="'+searchIntentText+'">';
        $(".menu_cont").empty();
        $(".menu_cont").append(innerHTML);
        $(".menu_cont").append(result.toString());
      }).fail(function(result) {
      finAjax();
  })
    addAjax();
  $.ajax({
    url : "builderBottomIntention",
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

$(document).ready(function () {
  // AMR Layer popup open
  // chatClick(1);
  $('.btn_regex_test').on('click', function () {
    var test = $('#testText').val();
    var regex = $('#regexResult').val();
    try {
      var pattern;
      if (regex.indexOf("(?i)") != -1) {
        regex = regex.replace(/\(\?i\)/g, "");
        pattern = new RegExp(regex, "gi");
      } else {
        pattern = new RegExp(regex);
      }
      var isMatch = pattern.test(test);
    } catch (e) {
      isMatch = 'fail';
    }
    if (isMatch == true) {
      $('#matched').show();
      $('#unmatched').hide();
      $('#wrong').hide();
    } else if (isMatch == false) {
      $('#unmatched').show();
      $('#matched').hide();
      $('#wrong').hide();
    } else {
      $('#wrong').show();
      $('#unmatched').hide();
      $('#matched').hide();
    }
  });

  // AMR 돋보기 아이콘이 있는 검색 영역에서 엔터 시 검색버튼 눌림
  $('.ipt_txt.search').on('keyup', function () {
    if (event.keyCode == 13) {
      $(this).next('#intention_detail').trigger('click');
    }
  });

  $('#testText').on('keyup', function () {
    if (event.keyCode == 13) {
      $(this).next('.btn_regex_test').trigger('click');
    }
  });
  // AMR 테이블 페이지네이션 페이지 active
  // .page 클릭 시
  $('.tbl_wrap .pagination .page').on('click', function () {
    var pages = $('.pagination .pages .page');
    var thisPage = $(this);
    pages.removeClass('active');
    thisPage.addClass('active');
  });
  // 버튼 클릭 시
  $('.tbl_wrap .pagination > button').on('click', function () {
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
      if (thisPage === 0) {
        prevPage = 0;
      }
      pages.eq(prevPage).addClass('active');
      return;
    }

    if (thisButton.hasClass('next')) {
      if (thisPage === lastPage) {
        nextPage = lastPage;
      }
      pages.eq(nextPage).addClass('active');
      return;
    }

    if (thisButton.hasClass('last')) {
      pages.eq(lastPage).addClass('active');
    }
  });

  // AMR 챗봇빌더 목록 active
  $('.chatbot_builder .chatbot_list li').on('click', function () {
    var list = $('.chatbot_builder .chatbot_list li');
    var thisList = $(this);
    list.removeClass('active');
    thisList.addClass('active');
  });

  // AMR 챗봇빌더 메뉴 active
  $('.chatbot_builder .menu li').on('click', function () {
    var menu = $('.chatbot_builder .menu li');
    var thisMenu = $(this);
    menu.removeClass('active');
    thisMenu.addClass('active');
  });

  // 챗봇빌더 답변 테이블 내 상세, 삭제 버튼 클릭 시 해당 col active
  $('.chatbot_builder table .btn_line_primary, .chatbot_builder table .btn_line_warning').on(
      'click', function () {
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

    btnOn.on('click', function () {
      $('.chatbot_builder').addClass('test_on');
      btnOn.addClass('hide');
    });

    btnOff.on('click', function () {
      $('.chatbot_builder').removeClass('test_on');
      btnOn.removeClass('hide');
    });
  }

  handleChatbotTestOnOff();

  $('#intention_detail').on('click', function () {
    isSearch = true;
    goDetailPage(1);
  });

  console.log("chatbotBuilderIntentionDetail");
  // 추가AMR 스위치 버튼

});

  function goDetailPage(cp){
    if( cp != 0){
      $('#currentPage').val(cp);
    }
    var type = $(".btn_switch>button.active")[0].id;
    chatClick('',cp, '', type);
  }

function insertRegex() {
  if ($("#modalRegex").val() == $("#regexResult").val()) {
    mui.alert("저장이 완료되었습니다.");
    closeBtn('#chat_regex_setting');
    chatClick('',1, '', 'regex');
    return false;
  }
  if (duplicate) {
    return false;
  } else {
    duplicate = true;
  }
  var obj = new Object();
  var isInsert = $('#isInsert').val();
  var regexIntentNo = $("#modalRegexIntentNo").val();
  obj.host = $('#chatListUl>li.active').val();
  obj.bertIntentName = $("#intentTitle").text();
  obj.isInsert = isInsert;
  obj.regexIntentNo = regexIntentNo;
  obj.regexValue = "";
  obj.regexType = "";
  if (!$("input:checkbox[id='checkRegex']").is(":checked")) {
    $(".regex_type").each(function (i, v) {
      var value = $(this).attr('value');
      var regexType = $(this).attr('class');
      if (value != undefined && value != "") {
        obj.regexValue += value + ",";
        obj.regexType += regexType + ",";
      }
    });
  }
  obj.regex = $("#regexResult").val();
  obj.bertIntentNo = $("#bertIntentNo").val();
  var lang = $("#div_lang_viewer")[0].classList[1];
  if (lang == 'korea') {
    lang = 1;
  } else if (lang == 'us') {
    lang = 2;
  } else if (lang == 'japan') {
    lang = 3;
  } else {
    lang = 4;
  }
  obj.lang = lang;
  regexLang = lang;
    addAjax();
  $.ajax({
    url: "insertRegex",
    data: JSON.stringify(obj),
    type: "POST",
    contentType: 'application/json'
  }).success(
      function (result) {
          finAjax();
        if (result.result == 'success') {
          mui.alert("저장이 완료되었습니다.");
          duplicate = false;
          closeBtn('#chat_regex_setting');
          chatClick('',1, '', 'regex');
        } else {
          mui.alert("중복된 정규식 입니다.");
          duplicate = false;
        }
      }).fail(function (result) {
      finAjax();
        mui.alert("저장을 실패하였습니다.");
        duplicate = false;
        closeBtn('#chat_regex_setting');
  });
}

function renderRegexSelect(select) {
  select.each(function(){
    var $thisSelect = $(this);
    var $options = $thisSelect.find('option');
    var allType = ''; //option의 모든 value
    var selectedType = $thisSelect.val();

    $options.each(function(i){ //option의 모든 value 구하기
      var value = $(this).attr('value');
      if ( i == 0 ) {
        allType += value
      } else {
        allType += ' ' + value
      }
    });

    setSelected();
    handleSelecte();

    function handleSelecte() {
      $thisSelect.on('change', function(){
        selectedType = $thisSelect.val();
        setSelected();
      });
    }

    function setSelected() {
      $thisSelect.next('.regex_type').removeClass(allType); //class 초기화
      $thisSelect.next('.regex_type').addClass(selectedType); //선택된 class만 적용
      getRegexResult();
    }

  });
}

function delRegex(val) {
  val.closest('li').remove();
  getRegexResult();
}

function confirmRegex() {
  var startCount = 0;
  var matchCount = 0;
  var includeCount = 0;
  var excludeCount = 0;
  var endCount = 0;
  var topCount = 0;
  var isTop = 'n';
  var isBottom = 'n';
  var regexResult = $('#regexResult').val();
  if (regexResult == undefined || regexResult == "") {
    mui.alert("결과 정규식이 없습니다.");
    return;
  }
  var test = $('#testText').val();
  var regex = $('#regexResult').val();
  try {
    var pattern;
    if (regex.indexOf("(?i)") != -1) {
      regex = regex.replace(/\(\?i\)/g, "");
      pattern = new RegExp(regex, "gi");
    } else {
      pattern = new RegExp(regex);
    }
    var isMatch = pattern.test(test);
  } catch (e) {
    $('#wrong').show();
    mui.alert("잘못된 정규식입니다.");
    return false;
  }
  if (!$("input:checkbox[id='checkRegex']").is(":checked")) {
    $(".regex_type").each(function (i, v) {
      var value = $(this).attr('value');
      var valueType = $(this).attr('class');
      if (value != undefined) {
        topCount++;
      }
      if (value != undefined && value != "" && valueType.includes('start')) {
        if (topCount == 1) {
          isTop = 'y';
        }
        startCount++;
      } else if (value != undefined && value != "" && valueType.includes(
          'match')) {
        matchCount++;
      } else if (value != undefined && value != "" && valueType.includes(
          'include')) {
        includeCount++;
      } else if (value != undefined && value != "" && valueType.includes(
          'exclude')) {
        excludeCount++;
      } else if (value != undefined && value != "" && valueType.includes('end')) {
        if (i == $(".regex_type").length - 1) {
          isBottom = 'y';
        }
        endCount++;
      }
    });

    if (startCount > 0 && isTop == 'n') {
      mui.alert("시작 텍스트는 맨 위에 있어야 합니다.");
      return;
    }
    if (startCount > 1) {
      mui.alert("시작 텍스트는 2개 이상 지정할 수 없습니다.");
      return;
    }
    if (endCount > 0 && isBottom == 'n') {
      mui.alert("종료 텍스트는 맨 밑에 있어야 합니다.");
      return;
    }
    if (endCount > 1) {
      mui.alert("종료 텍스트는 2개 이상 지정할 수 없습니다.");
      return;
    }
    if (excludeCount > 0 && startCount == 0 && matchCount == 0 &&
        includeCount == 0 && endCount == 0) {
      mui.alert("'포함하지 않음' 규칙으로만 정규식을 생성할 수 없습니다.");
      return;
    }

    if (matchCount > 0 && (startCount > 0 || excludeCount > 0 ||
        includeCount > 0 || endCount > 0)) {
      mui.alert("'텍스트가 정확하게 일치함' 규칙과 다른 규칙을 같이 사용할 수 없습니다.");
      return;
    }
  }

  insertRegex();

}

function getRegexResult(type) {
  var regexResult = "";
  var isStart = 'n';
  var isExclude = 'n';
  var isStartExclude = 'n';
  var isInclude = 'n';
  $(".regex_type").each(function (i, v) {
    var value = $(this).attr('value');
    var valueType = $(this).attr('class');
    if (value != undefined && value != "" && valueType.includes('start')) {
      if (isExclude == 'y') {
        regexResult += "(" + value + ").*";
      } else {
        regexResult += "^(" + value + ").*";
      }
      isStart = 'y';
      isStartExclude = 'y';
      isExclude = 'n';
    } else if (value != undefined && value != "" && valueType.includes(
        'match')) {
      regexResult += "(" + value + ")";
      isStart = 'n';
      isExclude = 'n';
    } else if (value != undefined && value != "" && valueType.includes(
        'include')) {
      if (regexResult.lastIndexOf(".*") == regexResult.length - 2) {
        regexResult += "(" + value + ").*";
      } else {
        regexResult += ".*(" + value + ").*";
      }
      isStart = 'n';
      isExclude = 'n';
      isInclude = 'y';
    } else if (value != undefined && value != "" && valueType.includes(
        'exclude')) {
      if (isExclude == 'y' && isStartExclude == 'y') {
        regexResult = regexResult.replace('))^', '|' + value + '))');
        isStartExclude = 'n';
      } else if (isExclude == 'y' && isStartExclude == 'n') {
        regexResult = regexResult.replace('))^', '|' + value + '))^');
      } else if (isExclude == 'n' && isStartExclude == 'y') {
        regexResult = "(?!.*(" + value + "))" + regexResult;
        isStartExclude = 'n';
        isExclude = 'y';
      } else {
        regexResult = "(?!.*(" + value + "))^" + regexResult;
        isExclude = 'y';
      }
      isStart = 'n';
    } else if (value != undefined && value != "" && valueType.includes('end')) {
      if (isStart == 'y' || isInclude == 'y') {
        regexResult += "(" + value + ")$";
      } else {
        regexResult += ".*(" + value + ")$";
      }
      isExclude = 'n';
    }
  });
  $('#regexResult').val(regexResult);
}

function addRegex() {
  modalPage();
  var activeLang = $("#ul_lang_list>li.active")[0].classList[0];
  $("#div_lang_viewer").attr('class', 'lang_viewer ' + activeLang);
  $(".regex.korea").attr('style', 'display:none');
  $(".regex.us").attr('style', 'display:none');
  $(".regex.japan").attr('style', 'display:none');
  $(".regex.china").attr('style', 'display:none');
  $("#upper_lang_list").find('ul').find('li').each(function() {
    var lang = $(this)[0].classList[0];
    $(".regex." + lang).attr('style', 'display:block');
  });
  $('#regex_warning').html('');
  $('#regexResult').val("");
  $('#testText').val("");
  $('#matched').hide();
  $('#unmatched').hide();
  $('#wrong').hide();
  $('#btn_addRegexRule').show();
  $('#regexList').empty();
  $('#isInsert').val('Y');
  $('#regexResult').attr('readonly', true);
  $('#div_regexRule').show();
  $('#check_regex').show();
  $('#checkRegex').prop('checked', false);
  $('#btn_addRegexRule').attr('disabled', false);
  var innerHTML = '';
  for (var i=0; i<6; i++) {
    if (i == 5) {
      innerHTML += '<template id="tempRegexList">';
    }
    innerHTML += '<li>';
    innerHTML += '<select name="regex_type" id="" class="select">';
    if (i == 0) {
      innerHTML += '<option value="start" selected>시작 텍스트</option>';
    } else {
      innerHTML += '<option value="start">시작 텍스트</option>';
    }
    if (i == 1) {
      innerHTML += '<option value="match" selected>텍스트가 정확하게 일치함</option>';
    } else {
      innerHTML += '<option value="match">텍스트가 정확하게 일치함</option>';
    }
    if (i == 2 || i == 5) {
      innerHTML += '<option value="include" selected>텍스트에 포함</option>';
    } else {
      innerHTML += '<option value="include">텍스트에 포함</option>';
    }
    if (i == 3) {
      innerHTML += '<option value="exclude" selected>텍스트에 포함하지 않음</option>';
    } else {
      innerHTML += '<option value="exclude">텍스트에 포함하지 않음</option>';
    }
    if (i == 4) {
      innerHTML += '<option value="end" selected>종료 텍스트</option>';
    } else {
      innerHTML += '<option value="end">종료 텍스트</option>';
    }
    innerHTML += '</select>';
    if (i == 0) {
      innerHTML += '<input type="text" class="ipt_txt regex_type start" placeholder="ex) 메뉴">';
    } else  {
      innerHTML += '<input type="text" class="ipt_txt regex_type include">';
    }
    innerHTML += '<button class="btn_icon delete btn_lyr_open" name="del_rule" onclick="delRegex(this)"></button>';
    innerHTML += '</li>';
    if (i == 5) {
      innerHTML += '</template>'
    }
  }
  $('#regexList').append(innerHTML);
  $(".regex_type").on('input', function(){
    getRegexResult();
  });
  new Sortable(regexList, {
    animation: 150,
    ghostClass: 'blue-background-class',
    onEnd: function () {
      getRegexResult();
    }
  });
  renderRegexSelect($('select[name="regex_type"]'));
  $('#checkRegex').on('click', function() {
    if($("input:checkbox[id='checkRegex']").is(":checked")) {
      $('#regex_warning').html(
          '정규식을 직접 입력해서 저장하면 위 규칙을 사용할 수 없게 되며, 추후 정규식을 직접 입력으로만 수정할 수 있습니다.');
      $('#btn_addRegexRule').attr('disabled', true);
      $('#regexResult').attr('readonly', false);
      $('button[name="del_rule"]').attr('disabled', true);
      $(".regex_type").each(function (i, v) {
        $(this).attr('disabled', true);
      });
      $('select[name="regex_type"]').each(function (i, v) {
        $(this).attr('disabled', true);
      });
      $('#regexList li').draggable({ disabled: true });
      renderRegexSelect($('select[name="regex_type"]'));
    } else {
      $('#regex_warning').html('');
      $('#btn_addRegexRule').attr('disabled', false);
      $('#regexResult').attr('readonly', true);
      $('button[name="del_rule"]').attr('disabled', false);
      $(".regex_type").each(function (i, v) {
        $(this).attr('disabled', false);
      });
      $('select[name="regex_type"]').each(function (i, v) {
        $(this).attr('disabled', false);
      });
      getRegexResult();
    }
  });
}

function modalPage () {
  var winHeight = $(window).height() * 0.7,
      hrefId = "#chat_regex_setting";

  $('body').css('overflow', 'hidden');
  $('body').find(hrefId).wrap('<div class="lyrWrap"></div>');
  $('body').find(hrefId).before('<div class="lyr_bg"></div>');

  $('#ui_regex_lang_list').empty();
  var innerHtml = '';
  for (var i=0; i<nowLangApply.length; i++) {
    if (i == 0 && nowLangApply[i] == true) {
      innerHtml += '<li class="regex korea" onclick="regexLangClick(\'korea\')">한국어</li>'
    } else if (i == 1 && nowLangApply[i] == true) {
      innerHtml += '<li class="regex us" onclick="regexLangClick(\'us\')">영어</li>'
    } else if (i == 2 && nowLangApply[i] == true) {
      innerHtml += '<li class="regex japan" onclick="regexLangClick(\'japan\')">일본어</li>'
    } else if (i == 3 && nowLangApply[i] == true) {
      innerHtml += '<li class="regex china" onclick="regexLangClick(\'china\')">중국어</li>'
    }
  }
  $('#ui_regex_lang_list').append(innerHtml);

  //대화 UI
  $('.lyrBox .lyr_mid').each(function () {
    $(this).css('max-height', Math.floor(winHeight) + 'px');
  });

};

function nqaModalPage () {
  var winHeight = $(window).height() * 0.7,
      hrefId = "#chat_nqa_setting";

  $('body').css('overflow', 'hidden');
  $('body').find(hrefId).wrap('<div class="lyrWrap"></div>');
  $('body').find(hrefId).before('<div class="lyr_bg"></div>');

  //대화 UI
  $('.lyrBox .lyr_mid').each(function () {
    $(this).css('max-height', Math.floor(winHeight) + 'px');
  });

};

function addRegexList() {
  var target = document.getElementById('regexList');
  var clonedNode = $($('#tempRegexList').html())[0];
  var $temp = $(clonedNode);
  var select = $temp.find('select');

  $temp.appendTo(target);
  renderRegexSelect(select);
  $(".regex_type").on('input', function(){
    getRegexResult();
  });
}

function editRegex(regexIntentNo, regex) {
  modalPage();
  var activeLang = $("#ul_lang_list>li.active")[0].classList[0];
  $("#div_lang_viewer").attr('class', 'lang_viewer ' + activeLang);
  $(".regex.korea").attr('style', 'display:none');
  $(".regex.us").attr('style', 'display:none');
  $(".regex.japan").attr('style', 'display:none');
  $(".regex.china").attr('style', 'display:none');
  $(".regex." + activeLang).attr('style', 'display:block');
  $('#isInsert').val('N');
  var obj = new Object();
  obj.regexIntentNo = regexIntentNo;
    addAjax();
  $.ajax({
    url : "getRegexDetail",
    data : JSON.stringify(obj),
    type: "POST",
    contentType: 'application/json'
  }).success(
      function(result) {
          finAjax();
        var list = result.regexDetail;
        $('#regexResult').val(list[0].Regex);
        $('#regex_warning').html('');
        $('#testText').val("");
        $('#matched').hide();
        $('#unmatched').hide();
        $('#wrong').hide();
        $('#regexList').empty();
        $('#checkRegex').prop('checked', false);
        $('#btn_addRegexRule').attr('disabled', false);
        var innerHTML = '';
        if (list[0].RuleType == undefined || list[0].RuleType == '') {
          $('#btn_addRegexRule').hide();
          $('#regexResult').attr('readonly', false);
          $('#div_regexRule').hide();
          $('#check_regex').hide();
        } else {
          $('#btn_addRegexRule').show();
          $('#regexResult').attr('readonly', true);
          $('#div_regexRule').show();
          $('#check_regex').show();
          $('#rule_place').attr('disabled', true);
          $.each(list, function (i, v) {
            innerHTML += '<li>';
            innerHTML += '<select name="regex_type" id="" class="select">';
            if (v.RuleType == 'start') {
              innerHTML += '<option value="start" selected>시작 텍스트</option>';
            } else {
              innerHTML += '<option value="start">시작 텍스트</option>';
            }
            if (v.RuleType == 'match') {
              innerHTML += '<option value="match" selected>텍스트가 정확하게 일치함</option>';
            } else {
              innerHTML += '<option value="match">텍스트가 정확하게 일치함</option>';
            }
            if (v.RuleType == 'include') {
              innerHTML += '<option value="include" selected>텍스트에 포함</option>';
            } else {
              innerHTML += '<option value="include">텍스트에 포함</option>';
            }
            if (v.RuleType == 'exclude') {
              innerHTML += '<option value="exclude" selected>텍스트에 포함하지 않음</option>';
            } else {
              innerHTML += '<option value="exclude">텍스트에 포함하지 않음</option>';
            }
            if (v.RuleType == 'end') {
              innerHTML += '<option value="end" selected>종료 텍스트</option>';
            } else {
              innerHTML += '<option value="end">종료 텍스트</option>';
            }
            innerHTML += '</select>';
            innerHTML += '<input type="text" class="ipt_txt regex_type '
                + v.RuleType + '" value="' + v.RuleValue + '">';
            innerHTML += '<button class="btn_icon delete btn_lyr_open" name="del_rule" onclick="delRegex(this)"></button>';
            innerHTML += '</li>';
          });
          innerHTML += '<template id="tempRegexList">';
          innerHTML += '<li>';
          innerHTML += '<select name="regex_type" id="" class="select">';
          innerHTML += '<option value="start">시작 텍스트</option>';
          innerHTML += '<option value="match">텍스트가 정확하게 일치함</option>';
          innerHTML += '<option value="include" selected>텍스트에 포함</option>';
          innerHTML += '<option value="exclude">텍스트에 포함하지 않음</option>';
          innerHTML += '<option value="end">종료 텍스트</option>';
          innerHTML += '</select>';
          innerHTML += '<input type="text" class="ipt_txt regex_type include">';
          innerHTML += '<button class="btn_icon delete btn_lyr_open" name="del_rule" onclick="delRegex(this)"></button>';
          innerHTML += '</li>';
          innerHTML += '</template>';
        }
        innerHTML += '<input type="hidden" id="modalRegexIntentNo" value="'+ regexIntentNo +'">';
        innerHTML += '<input type="hidden" id="modalRegex" value="'+ regex +'">';
        $('#regexList').append(innerHTML);
        $(".regex_type").keyup(function(){
          getRegexResult();
        });
        new Sortable(regexList, {
          animation: 150,
          ghostClass: 'blue-background-class',
          onEnd: function () {
            getRegexResult();
          }
        });
        renderRegexSelect($('select[name="regex_type"]'));

        $('#checkRegex').on('click', function() {
          if($("input:checkbox[id='checkRegex']").is(":checked")) {
            $('#regex_warning').html(
                '정규식을 직접 입력해서 저장하면 위 규칙을 사용할 수 없게 되며, 추후 정규식을 직접 입력으로만 수정할 수 있습니다.');
            $('#btn_addRegexRule').attr('disabled', true);
            $('#regexResult').attr('readonly', false);
            $('button[name="del_rule"]').attr('disabled', true);
            $(".regex_type").each(function (i, v) {
              $(this).attr('disabled', true);
            });
            $('select[name="regex_type"]').each(function (i, v) {
              $(this).attr('disabled', true);
            });
            $('#regexList li').draggable({ disabled: true });
            renderRegexSelect($('select[name="regex_type"]'));
          } else {
            $('#regex_warning').html('');
            $('#btn_addRegexRule').attr('disabled', false);
            $('#regexResult').attr('readonly', true);
            $('button[name="del_rule"]').attr('disabled', false);
            $(".regex_type").each(function (i, v) {
              $(this).attr('disabled', false);
            });
            $('select[name="regex_type"]').each(function (i, v) {
              $(this).attr('disabled', false);
            });
            getRegexResult();
          }
        });
      }).fail(function(result) {
      finAjax();
    console.log("getCustInfoList error");
  });
}

function deletePage (no, type) {
  var winHeight;
  if (type == 'regex') {
    winHeight = $(window).height() * 0.7,
        hrefId = "#regex_delete";
    $('#regexIntentNum').val(no);
  } else if (type == 'nqa') {
    winHeight = $(window).height() * 0.7,
        hrefId = "#nqa_delete";
    $('#questionId').val(no);
  }

  $('body').css('overflow', 'hidden');
  $('body').find(hrefId).wrap('<div class="lyrWrap"></div>');
  $('body').find(hrefId).before('<div class="lyr_bg"></div>');
  //대화 UI
  $('.lyrBox .lyr_mid').each(function () {
    $(this).css('max-height', Math.floor(winHeight) + 'px');
  });

};

function deleteRegex () {
  var obj = new Object();
  obj.regexIntentNo = $('#regexIntentNum').val();
    addAjax();
  $.ajax({
    url : "deleteRegexRule",
    data : JSON.stringify(obj),
    type: "POST",
    contentType: 'application/json'
  }).success(
      function(result) {
          finAjax();
        delCacheNow();
        mui.alert("삭제가 완료되었습니다.");
        closeBtn('#regex_delete');
        chatClick('',1, $("#bertIntentNo").val(), 'regex');
      }).fail(function(result) {
      finAjax();
        mui.alert("삭제를 실패하였습니다.");
        closeBtn('#regex_delete');
  });
}

function closeBtn(modal) {
  var hrefId = modal;
  $('body').css('overflow', '');
  $('body').find(hrefId).unwrap('<div class="lyrWrap"></div>');
  $('.lyr_bg').remove();
};

function addNqa() {
  nqaModalPage();
  $('#addNedit').val('add');
  $('#text_nqa').val('');
  $('#nqaIntent').text($('#intentTitle').text());
  overlayTutorial(6);
}

function addNqaSentence(questionNo, question) {
  var obj = new Object();
  var host = $("#chatListUl>li.active div:nth-child(2) span").text().split(":");
  obj.answer = $('#intentTitle').text();
  obj.answerId = $('#nqaAnswerId').val();
  obj.question = $('#text_nqa').val();
  obj.channelId = $("#chatListUl>li.active").val();
  obj.channelName = host[1].replace(" ", "");

  if (duplicate) {
    return false;
  } else {
    duplicate = true;
  }
  if ($('#addNedit').val() == 'add') {

    obj.categoryId = $('#categoryId').val();

      addAjax();
    $.ajax({
      url: "api/nqa/qa-sets/insertNqaAnswer",
      data: JSON.stringify(obj),
      type: "POST",
      contentType: 'application/json'
    }).success(
        function (result) {
          if (result.result == 'success') {
              finAjax();
            mui.alert("저장이 완료되었습니다.");
            duplicate = false;
            closeBtn('#chat_nqa_setting');
            $("#nqaAnswerId").val(result.answerId);
            chatClick('',1, '', 'nqa');
            overlayTutorial(7);
          } else {
            mui.alert("저장을 실패하였습니다.");
            duplicate = false;
            closeBtn('#chat_nqa_setting');
          }
        }).fail(function (result) {
        finAjax();
          mui.alert("저장을 실패하였습니다.");
          duplicate = false;
          closeBtn('#chat_nqa_setting');
    });
  } else {
    obj.questionId = $('#questionNo').val();
    $.ajax({
      url : "api/nqa/qa-sets/editNqaAnswer",
      data : JSON.stringify(obj),
      type: "POST",
      contentType: 'application/json'
    }).success(
        function(result) {
            finAjax();
          if (result.result == 'success') {
            mui.alert("수정이 완료되었습니다.");
            duplicate = false;
            closeBtn('#chat_nqa_setting');
            $("#nqaAnswerId").val(result.answerId);
            chatClick('', 1, '', 'nqa');
          } else {
            mui.alert("수정을 실패하였습니다.");
            closeBtn('#chat_nqa_setting');
          }
        }).fail(function(result) {
        finAjax();
      mui.alert("수정을 실패하였습니다.");
      closeBtn('#chat_nqa_setting');
    });
  }
}

function editNqa(questionNo, question) {
  nqaModalPage();
  $('#addNedit').val('edit');
  $('#text_nqa').val(question);
  $('#questionNo').val(questionNo);
  $('#question').val(question);
}

function editNqaSentence(questionNo, question) {
  var obj = new Object();
  obj.question = question;
  obj.questionId = questionNo;
  obj.answerId = $("#nqaAnswerId").val();
  obj.answer = $("#intentTitle").text();
    addAjax();
  $.ajax({
    url : "editNqaAnswer",
    data : JSON.stringify(obj),
    type: "POST",
    contentType: 'application/json'
  }).success(
      function(result) {
          finAjax();
        mui.alert("수정이 완료되었습니다.");
        closeBtn('#chat_nqa_setting');
        $("#nqaAnswerId").val(result.answerId);
        chatClick('',1, '', 'nqa');
      }).fail(function(result) {
      finAjax();
    mui.alert("수정을 실패하였습니다.");
    closeBtn('#chat_nqa_setting');
  });
}

function deleteNqa () {
  var obj = new Object();
  obj.questionId = $('#questionId').val();
  obj.answerId = $("#nqaAnswerId").val();
  obj.answer = $("#intentTitle").text();
  obj.channelId = $("#chatListUl>li.active").val();
  obj.channelName = $("#chatListUl>li.active div:first-child span").text();
    addAjax();
  $.ajax({
    url : "api/nqa/qa-sets/deleteNqaAnswer",
    data : JSON.stringify(obj),
    type: "POST",
    contentType: 'application/json'
  }).success(
      function(result) {
          finAjax();
        if (result.result == 'success') {
          delCacheNow();
          mui.alert("삭제가 완료되었습니다.");
          closeBtn('#nqa_delete');
          chatClick('', 1, '', 'nqa');
        } else {
          mui.alert("삭제를 실패하였습니다.");
          closeBtn('#nqa_delete');
        }
      }).fail(function(result) {
      finAjax();
    mui.alert("삭제를 실패하였습니다.");
    closeBtn('#nqa_delete');
  });
}

function regexLangClick(lang) {
  $("#div_lang_viewer").attr('class', 'lang_viewer ' + lang);
}
