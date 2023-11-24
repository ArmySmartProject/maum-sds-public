var intentTable = null;
var intentStcList = null;

$(document).ready(function(){
  getNqaCount();

  if ( scenario ) {
    intentData.lang = data.lang;
    intentData.chatName = function(){
      var name = data.name;
      if (data.lang === 1) {//한국어
        name.splice(' (한국어)', '');
      } else {//영어
        name.splice(' (영어)', '');
      }
      return name;
    }
    intentData.host = data.host;

    if ( !intentTableData.length ) {
      getIntention(intentData).then(function(){
        drawIntentTable();
      });
    } else {
      drawIntentTable();
    }
  }
});

function getIntention(obj) {
  $('#vb_wrap').addClass('loading');

  //getIntention
  return $.ajax({
    url: "voiceBot/getIntention",
    data: JSON.stringify(obj),
    type: "POST",
    contentType: 'application/json'

  }).then(function (res) {
    intentStcList = res.intentStcList;

    for (var i = 0; i < intentStcList.length; i++) {
      var viewRegex = intentStcList[i].regex;
      var split = viewRegex ? viewRegex.split('@') : '';
      viewRegex = !split[0] ? '' : split[0];

      var obj = {};
      obj.commonIntent = intentStcList[i].Task === '0' || intentStcList[i].Task === 0 ? 'O' : 'X';
      obj.label = intentStcList[i].Name === '' ? 'always' : intentStcList[i].Name;
      obj.regex = viewRegex;
      obj.nqa = intentStcList[i].question ? intentStcList[i].question : '';
      obj.answerId = intentStcList[i].answerId ? intentStcList[i].answerId : '';
      obj.No = intentStcList[i].No ? intentStcList[i].No : '';
      obj.categoryId = intentStcList[i].categoryId ? intentStcList[i].categoryId : '';
      obj.delete = obj.label;

      intentTableData.push(obj);
    }

    $('#vb_wrap').removeClass('loading');

  }).catch(function () {
    mui.alert('페이지를 불러오지 못했습니다.<br> 다시 시도해주세요.');
    $('#vb_wrap').removeClass('loading');
    console.log('[getIntention error]');
  });
}

function deleteIntention(intent) {
  mui.confirm(intent + ' 을(를) 삭제하시겠습니까?', {
    onClose: function(isOk){
      if (isOk) {
        $('#vb_wrap').addClass('loading');

        intentData.bertIntentName = intent;
        intentData.langList = [data.lang];


        intentDeleteScenario(intentData).then(function(result){ //시나리오 제이슨 수정/삭제
          if (!result.isSuccess) return;

          intentDelete(intentData).then(function(result){ //db 삭제
            if (!result.isSuccess) return;

            intentSaveScenario().then(function(result) { //시나리오 제이슨 저장
              if (!result.isSuccess) return;

              updateIntentTable(intentTableData); //의도 table update
              $('#vb_wrap').removeClass('loading');
            });
          });
        });
      }
    }
  });

  //db 삭제
  function intentDelete(obj) {
    return $.ajax({
      url : "voiceBot/deleteByIntentNo",
      data : JSON.stringify(obj),
      type: "POST",
      contentType: 'application/json'
    }).then(function(){
      console.log('[intentDelete success]');
      return {isSuccess: true};

    }).catch(function() {
      mui.alert(intent + ' 삭제를 실패하였습니다.<br> 다시 시도해주세요.');
      $('#vb_wrap').removeClass('loading');
      console.log('[intentDelete error]');
      return {isSuccess: false};
    });
  }

  function intentDeleteScenario(obj) { //시나리오 제이슨 가공
    return $.ajax({
      url : "voiceBot/selectIntent",
      data : JSON.stringify(obj),
      type: "POST",
      contentType: 'application/json'
    }).then(function(res) {
      var connectIntent = res;
      allTaskInfo = allTask();

      for (var i=0; i<connectIntent.length; i++) {
        var srcName = connectIntent[i].srcName;
        var destName = connectIntent[i].destName;
        var src = taskInfo.find(e => e.label === srcName);
        var dest = taskInfo.find(e => e.label === destName);

        deleteScenarioIntent(src.id, dest.id, dest.label, intent);
      }

      var dataIndex = intentTableData.findIndex(e => e.label === intent);
      intentTableData.splice(dataIndex, 1);
      return {isSuccess: true};
    }).catch(function() {
      mui.alert(intent + ' 삭제를 실패하였습니다.<br> 다시 시도해주세요.');
      $('#vb_wrap').removeClass('loading');
      console.log('[deleteIntention error]');
      return {isSuccess: false};
    });
  }

  function intentSaveScenario() {
    return $.ajax({
      url: "voiceBot/saveScenario",
      data: JSON.stringify({
        simplebotId: Number(data.simplebotId),
        userId: data.userId,
        companyId: data.companyId,
        scenarioJson: JSON.stringify(scenario),
        isExcelUpload: 'N',
      }),
      type: "POST",
      contentType: 'application/json'

    }).then(function () {
      mui.alert(intent + ' 을(를) 삭제하였습니다.');
      updateIntentTable(intentTableData);
      $('#vb_wrap').removeClass('loading');

      return {isSuccess: true};
    }).catch(function () {
      $('#vb_wrap').removeClass('loading');
      mui.alert(intent + ' 삭제를 실패하였습니다.<br> 다시 시도해주세요.');
      console.log('[intentSaveScenario error]');
      return {isSuccess: false};
    });
  }
}

function deleteScenarioIntent(sourceId, targetId, target, name) {
  // 노드(intentList) 찾기
  var nodeIndex = scenario.nodes.findIndex(e => e.id === sourceId);
  var intentIndex = scenario.nodes[nodeIndex].attr[0].intentList.findIndex(e => e.intent === name && e.nextTask === target);

  if (intentIndex > -1) { //task와 연결된 intent를 찾음
    if (targetId === '종료') {//종료 노드 id 찾기
      var endTasks = scenario.nodes.filter(e => e.type === 'end');

      for (var i=0; i<endTasks.length; i++) {
        var connectedEnd = scenario.edges.find(e => e.source === sourceId && e.target === endTasks[i].id)

        if (connectedEnd) {
          targetId = connectedEnd.target;
          break;
        }
      }
    }

    //엣지, 노드(intentList) 제거
    var edgeIndex = scenario.edges.findIndex(e => e.source === sourceId && e.target === targetId);
    scenario.edges.splice(edgeIndex, 1);
    scenario.nodes[nodeIndex].attr[0].intentList.splice(intentIndex, 1);

    //다음태스크가 다른 edge에 연결되어 있는지 확인
    var targetIndex = scenario.edges.findIndex(e => e.target === targetId);

    if ( targetIndex === -1 && target === '종료' ) {//다른 edge에 연결되어 있지 않고 종료면 삭제
      var nodeIdx = scenario.nodes.findIndex(e => e.id === targetId);
      scenario.nodes.splice(nodeIdx, 1);
    }

    //엣지 수정
    var labelFilter = scenario.nodes[nodeIndex].attr[0].intentList.filter(e => e.nextTask === target);
    var labelArr = labelFilter.map(e => e.intent === '' ? 'always' : e.intent);
    var label = labelArr.join('/');
    var edgeArr = scenario.edges.filter(e => e.source === sourceId && e.target === targetId);

    edgeArr.forEach(function(e) {
      e.data.label = label;
    });

  } else {
    console.log('[deleteScenarioIntent error]');
  }
}

function drawIntentTable() {
  intentTable = $('#table_intent table').DataTable({
    "language": {
      "emptyTable": "등록된 데이터가 없습니다.",
      "lengthMenu": "페이지당 _MENU_ 개씩 보기",
      "info": "현재 _START_ - _END_ / _TOTAL_건",
      "infoEmpty": "데이터 없음",
      "infoFiltered": "( _MAX_건의 데이터에서 필터링됨 )",
      "search": "",
      "zeroRecords": "일치하는 데이터가 없습니다.",
      "loadingRecords": "로딩중...",
      "paginate": {
        "next": "›",
        "previous": "‹",
        "first" : "«",
        "last" :"»",
      }
    },
    bFilter:false,
    bInfo:false,
    sDom:"lrtip",
    lengthChange : false,
    fixedColumns: true,
    autoWidth: false, //css width
    searching: true, //검색
    ordering : false, //정렬
    paging: true, // paging 디폴트 : row 10개, 페이징 5개
    pagingType : "full_numbers_no_ellipses",
    data: intentTableData, //참조 data
    columns: [
      {
        data: "commonIntent",
        name: 'commonIntent',
        title: '공통 여부',
        searchable: false,
        width: '80px',
      },
      {
        data: "label",
        name: 'label',
        title: 'INTENT',
        searchable: true,
        render: function(data){
          var dropType = 'name';
          return "<a onclick='moveLinkDetail(\""+dropType+"\");'>"+data+"</a>";
        }
      },
      {
        data: "regex",
        title: '정규표현식',
        searchable: true,
        render: function(data){
          var dropType = 'regex';
          return "<a onclick='moveLinkDetail(\""+dropType+"\");'>"+data+"</a>";
        }
      },
      {
        data: "nqa",
        title: '학습문장',
        searchable: true,
        render: function(data){
          var dropType = 'nqa';
          return "<a onclick='moveLinkDetail(\""+dropType+"\");'>"+data+"</a>";
        }
      },
      {
        data: "delete",
        title: '',
        searchable: false,
        width: '90px',
        render: function(data){
          return "<button type='button' class='btn_line_warning' onclick='deleteIntention(\""+data+"\");'>삭제</button>"
        },
      },
    ],
  });
  intentTable.draw();

  // dataTable 통합 검색
  $("#intent_search").keyup(function(){
    intentTable.search($(this).val()).draw();
  });
}

function updateIntentTable(data) {
  intentTable.clear();
  intentTable.rows.add(data).draw();
}

function moveLinkDetail(dropType){ //dropType : name, regex, nqa
  var $target = $(event.target);
  var thisData = intentTable.row($target.parents('tr')).data();

  moveIntentDetail(data.host, thisData.label, dropType);
}

function nqaExcelUpload(){
  $('#nqaExcelUpHostName').val(data.name.replace(" ",""));
  $('#nqaExcelUpHost').val(data.host);

  $('#nqaExcelUploadfrm').ajaxSubmit({
    url : "api/nqa/qa-sets/upload-files",
    type: "POST",
    processData: false,
    contentType: false,
    success: function (result) {
      if(result.status == 'success'){
        mui.alert("모든 데이터가 업로드 되었습니다.");
        $('.btn_modal_close').click();
        intentTableData = [];
        getIntention(intentData).then(function(){
          updateIntentTable(intentTableData);

          if($('.content_title').text() == '의도 상세'){
            for(val in intentTableData){
              if(intentTableData[val].No == intentData.No){
                intentData.answerId = intentTableData[val].answerId;
              }
            }
            getNqaStudySentence();
          }
        });
        getNqaCount();
      }else{
        mui.alert("작업 중 오류가 발생했습니다.");
      }

    }, error: function (response) {
      mui.alert("작업 중 오류가 발생했습니다.");
      console.log("nqaExcelUpload ERROR : " + response);
    }
  });
}

function nqaExcelDownload(){
  if($("#nqa_question_cnt").text() == 0 || $("#nqa_question_cnt").text() == "-"){
    mui.alert('다운로드 할 데이터가 존재하지 않습니다.');
    return false;
  }

  var obj = new Object();
  obj.channelId = intentData.host;
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

function getNqaCount() {
  var obj = new Object();
  obj.channelId = data.host;

  $.ajax({
    url: "api/nqa/qa-sets/answers/qaCount",
    data: JSON.stringify(obj),
    type: "POST",
    contentType: 'application/json'
  }).then(function (result) {
    if(result.questionCount == 0 || result.message == 0) {
      $("#nqa_total_cnt").text("-");
      $("#nqa_question_cnt").text("-");
      $('#nqa_trained_at').text('-');
    } else {
      $("#nqa_total_cnt").text(result.totalCount); // 의도 수
      $("#nqa_question_cnt").text(result.questionCount); // 문장 수
      getTrainHistory();
    }

    getNqaUpdatedDtm();

  }).catch(function (response) {
    console.log('[qaCount error] ' + response);
    $("#nqa_total_cnt").text("-");
    $("#nqa_question_cnt").text("-");
    checkNqaTrained();
  });
}

function getNqaUpdatedDtm() { // nqa 업데이트 시간
    var obj = new Object();
    obj.channelId = data.host;

    $.ajax({
      url : "api/nqa/qa-sets/answers/getChannelInfo",
      data : JSON.stringify(obj),
      type: "POST",
      contentType: 'application/json'
    }).then(function (result) {
      if (result.status == 200) {
        if (result.isValid) {
          var channelInfo = result.channelInfo;
          $('#nqa_updated_at').text(channelInfo.updateDtm);
        } else {
          $('#nqa_updated_at').text('-');
        }
      } else if (result.status == 500) {
        $('#nqa_updated_at').text('-');
      }
      checkNqaTrained();
    }).catch(function (response) {
      console.log('getNqaUpdatedDtm failed : ' + response);
      $('#nqa_updated_at').text('-');
      checkNqaTrained();
    });
}

function getTrainHistory() { // nqa 학습 시간
  return new Promise((resolve, reject) => {
    var obj = new Object();
    obj.channelId = data.host;
    $.ajax({
      url : "api/nqa/indexing/getIndexingHistory",
      data : JSON.stringify(obj),
      type: "POST",
      contentType: 'application/json'
    }).then(function (result) {
      $('#nqa_trained_at').text(result.createdAt);
      checkNqaTrained();
      resolve(true);
    }).catch(function (response) {
      console.log('getIndexingHistory failed : ' + response);
      $('#nqa_trained_at').text('-');
      checkNqaTrained();
      resolve(false);
    });
  });
}

function checkNqaTrained() {
    var updateTime = $('#nqa_updated_at').text();
    var trainedTime = $('#nqa_trained_at').text();
    var nqaCnt = $('#nqa_question_cnt').text();

    if (updateTime != '-' && trainedTime == '-') {
      if(nqaCnt == 0 || nqaCnt == '-'){
        hideNqaTextTrain();
      }else{
        changeNqaTextTrain('학습해야 할 데이터가 있습니다.');
      }

    } else if (Date.parse(updateTime) > Date.parse(trainedTime) && $('#alert_nqa_training').css('display') !== 'block') {
      changeNqaTextTrain('학습해야 할 데이터가 있습니다.');

    } else if ((Date.parse(updateTime) < Date.parse(trainedTime)) || (updateTime == '-' && trainedTime == '-')) {
      hideNqaTextTrain();
    }
}




var waitChannelId = 0;
var waitNqaInterval = null;
var nqaIntervalCheck = false;
var nqaInterval;
var checkTrainingHost = true;
var firstCheck = false;

function nqaTrain() {
  var obj = new Object();
  obj.channelId = data.host;
  obj.checkTrainingHost = true;

  $.ajax({
    url : "api/nqa/indexing/getIndexingStatus",
    data : JSON.stringify(obj),
    type: "POST",
    dataType : 'json',
    contentType: 'application/json'
  }).then(function (result) {
    if(result.status){ // 학습 중
      if(result.message == data.host){ // 학습 진행 상태(progress bar)
        $("#nqa_trained_at").text('');
        $("#nqa_trained_progress").show();
        nqaInterval = setInterval(checkTrainPer,1000);
      }else { // 대기열 요청(modal -> wait progress bar) -- 개발 완료.
        var $modal = $('#nqa_waiting_progress');
        var $dialog = $modal.children();
        var $closeBtns = $modal.find('.btn_modal_close');
        $('body').css('overflow', 'hidden');
        $modal.show();
        $dialog.addClass('active');

        //set interval : 대기 -> 학습요청 및 progress
        waitChannelId = result.message;
        waitNqaInterval = setInterval(checkWaitTrain, 1000);

        $closeBtns.on('click', function(){
          modalClose($modal);
          clearInterval(waitNqaInterval);
        });
      }
    } else { // 학습 완료 -> 학습 요청
      console.log("학습 진행 start");
      nqaIndexingTrain();
    }

  }).catch(function (response) {
    console.log("train nqa Error : " + response);
  });

}


function checkWaitTrain(){
  var obj = new Object();
  obj.channelId = waitChannelId;
  obj.checkTrainingHost = true;

  $.ajax({
    url : "api/nqa/indexing/getIndexingStatus",
    data : JSON.stringify(obj),
    type: "POST",
    contentType: 'application/json'
  }).then(function (result) {
    if(result.status){
      var per = result.processed / result.total * 100;
      $('#nqa_waiting_progress_bar .progress_bar').css({'transform':'translate('+ per +'%)'});
    }else{
      $('#nqa_waiting_progress').children().find('.btn_modal_close').click();
      setTimeout(nqaIndexingTrain,2000); // 학습문장 indexing 진행
    }
    console.log("wait per : " + per);
  }).catch(function (response) {
    clearInterval(waitInterval);
    console.log('nqa wait Error : ' + response);
  });
}


function nqaIndexingTrain(){
  console.log('nqa train start!');

  var obj = new Object();
  obj.channelId = data.host;
  $.ajax({
    url : "api/nqa/indexing/fullIndexing",
    data : JSON.stringify(obj),
    type: "POST",
    contentType: 'application/json'
  }).then(function (result) {
    $("#nqa_trained_at").text("");
    $("#nqa_trained_progress").show();
    nqaIntervalCheck = false;

  }).catch(function (response) {
    nqaIntervalCheck = true;
    if (result.status == 424) {
      mui.alert('학습할 데이터가 존재하지 않습니다. 데이터를 먼저 추가해주세요.');
    } else {
      mui.alert('시스템상의 문제로 학습에 실패하였습니다. 잠시 후 다시 시도해주세요.');
    }
    console.log("nqaIndexingTrain catch response : " + response)
  });

  if (!nqaIntervalCheck) {
    nqaInterval = setInterval(checkTrainPer, 1000);
  }
}

function checkTrainPer(){
  var obj = new Object();
  var channelId = data.host;
  obj.channelId = channelId;
  obj.checkTrainingHost = checkTrainingHost;

  $.ajax({
    url : "api/nqa/indexing/getIndexingStatus",
    data : JSON.stringify(obj),
    type: "POST",
    contentType: 'application/json'
  }).then(function(result) {
    console.log("=======================");
    for(val in result){
      console.log("val : " + val + " / result[val] : " + result[val]);
    }
    console.log("=======================");
    var per = result.processed / result.total * 100;

    if ((result.message === null || result.message == channelId.toString() || (result.message != channelId.toString() && !firstCheck))
        && per < 100) {
      checkTrainingHost = false
      changeNqaTextTrain('학습 중 입니다.');
      // $('#alert_nqa_train_start').text('학습 중 입니다.');
      // $('#alert_nqa_train_start').show();
      $('#nqa_trained_progress .progress_bar').css({'transform':'translate('+ per +'%)'});
    } else {
      if(per == 100){
        mui.alert('학습이 완료 되었습니다.');
      }

      hideNqaTextTrain();
      $('#nqa_trained_progress').hide();
      getNqaCount();
      clearInterval(nqaInterval);
    }
    console.log("per : " + per + " / status : " + result.status);

  }).catch(function (response) {
    clearInterval(nqaInterval);
    console.log('getIndexingStatus err : ' + response);
  });
}

function readyNqaTrainingCheck(){
  var obj = new Object();
  obj.channelId = data.host;
  obj.checkTrainingHost = true;

  $.ajax({
    url : "api/nqa/indexing/getIndexingStatus",
    data : JSON.stringify(obj),
    type: "POST",
    dataType : 'json',
    contentType: 'application/json'
  }).then(function (result) {
    if(result.status && result.message == data.host) { // 학습 중
      nqaInterval = setInterval(checkTrainPer, 1000);
    }
  }).catch(function (response) {
    console.log("readyNqaTrainingCheckErr : " + response);
  });
}