var taskGroup = {'text' : "root", 'children' : []};
var selectMaxTaskArr = [];
var selectAnswerTaskArr = [];
var nodeValue = null;
var nodeData = null;
var taskInfo = []; //셀렉트를 위한 task 정보 (종료 태스크에 대한 상세 없음)
var allTaskInfo = []; // 모든 task 정보 (종료 태스크 포함)
var intentEditMode = false;

var TTSAudio = document.getElementById('TTSAnswer');
var prevAudioInfo = '';
var nowAudioInfo = '';
var nowAudioButtons = {};
var hasAudio = false;
var isPause = false;

$(document).ready(function(){

  var anSelect = $('body').find('.an-search_option');
  anSelect.not(anSelect[0]).remove(); //이전에 생성한 an-select 초기화

  handleTabMenu();
  TTSAudioEvent();
  $('#basic_utter_info .btn_box').hide(); //초기 화면에서 버튼 가리기

  //select
  scenarioOverTask = createSearchSelect($('#scenario_over_task')); //답변 최대 반복 초과 시 실행될 태스크
  selectAnswerSpeak = createSearchSelect($('#answer_utter')); //사용자 설정 발화
  selectAnswerDial = createSearchSelect($('#answer_dial')); //사용자 설정 다이얼
  selectAnswerSpeak.search.remove();
  selectAnswerDial.search.remove();

  if ( appliedAt ) { //마지막 저장 날짜
    $('#update_date').text(appliedAt + ' Updated');
  } else {
    $('#update_date').text('Never updated');
  }

  if ( scenario ) { // scenarioJson이 있을 때
    taskGroupFormat(); //taskGroup 생성
    taskFormat(selectMaxTaskArr, 'setting_task_'); //설정 탭 초과 태스크 셀렉트
    taskFormat(selectAnswerTaskArr, 'answer_task_'); //답변 탭 조건 답변 task 셀렉트
    taskFormat(taskInfo, '');

    $('#vb_header .excel_btns .btn_secondary').removeAttr('disabled'); //헤더 excel 모든 버튼 활성화
    $('#test_on').removeAttr('disabled'); //테스트 활성화

    // 특정 태스크 그룹 보기 셀렉트 이벤트
    handleSelectTaskGroup();

    //gojs 시나리오 flow chart 그리기
    makeFlowChart();
  } else {
    $('#vb_header .excel_btns .btn_excel_up').removeAttr('disabled'); //엑셀 업로드 버튼 활성화
    $('#vb_header .excel_btns .btn_excel_down').attr('disabled', ''); //엑셀 다운 버튼 비활성화
    $('#test_on').attr('disabled', ''); //테스트 비활성화

  }
});

//taskGroup 생성
function taskGroupFormat() {
  for (var i=0, l=scenario.nodes.length; i<l; i++) {
    var node = scenario.nodes[i];
    if ( node.taskGroup ) addTaskFull(node.taskGroup , node.id, i);
  }
}

//셀렉트 사용을 위한 태스크 배열 format
function taskFormat(arr, valueText) {
  for (var i=0, l=scenario.nodes.length; i<l; i++) {
    var node = scenario.nodes[i];
    var obj = {'label' : node.label, 'value' : valueText + node.label, 'id' : node.id};

    if ( node.label === '종료' ) { //종료 task는 여러개가 있을 수 있어 id를 지정안함
      obj.id = '종료';
    }

    var sameObj = arr.find(obj => obj.value === valueText + node.label);
    if (!sameObj) {
      arr.push(obj);
    }
  }
}

// 태스크 그룹 데이터 가공
function addTaskFull(text, id, index) {
  var nameArr = text.split('_');
  for(var i=0, l=nameArr.length; i<l; i++) {
    var now_text = "";
    for(var j=0; j<=i; j++){
      now_text += nameArr[j];

      if(j!=i) now_text += "_";
    }
    addTaskGroup(now_text, id, index);
  }
}
function addTaskGroup(text, id, index) {
  var nameArr = text.split('_');
  var nameTree = [];
  for(var i=0; i<nameArr.length; i++){
    var tmpTree = "";
    for (var j=0; j<=i; j++){
      tmpTree += nameArr[j];
      if(j!=i) tmpTree += "_";
    }
    nameTree.push(tmpTree);
  }
  var elem = taskGroup;

  for(var i=0; i<nameTree.length-1; i++){
    elem = elem.children.find(element => element.fullText == nameTree[i]);
  }

  var nowElem = elem.children.find(element => element.fullText == nameTree[nameTree.length-1]);
  if(nowElem==undefined){
    elem.children.push(
      {
        'fullText': text,
        'text' : nameArr[nameArr.length-1],
        'id' : id,
        'index' : [index],
        'children' : [],
      }
    )
  }
  else{
    nowElem.index.push(index);
  }
}

// 특정 태스크 그룹 보기 셀렉트 이벤트
function handleSelectTaskGroup() {
  var selectList = [];
  var groupNameArr = [];
  var eachGroupList = [];

  makeSelect('', 0); //최초생성 (첫번째 댑스 만들기)
  groupNameArr.push('전체선택');

  //셀렉트 생성
  function makeSelect(name, index) { //index: 현재 셀렉트 순번 (첫번째 depth 면 순번은 1)
    var nextIndex = index+1; //다음 셀렉트 순번
    var nextSelect = 'selectTaskGroup' + nextIndex;

    if ( selectList.length === index ) { //다음 셀렉트 신규 생성
      if ( name ) {
        addGroupName();
        deleteReplaceGroupName();
      }

      //전체선택 or 자식 노드가 없을 때는 셀렉트 생성 안함
      if ( name === '전체선택' || !makeData(groupNameArr) ) return;

      selectList.push(nextSelect); //생성한 셀렉트 이름 arr

      //다음 셀렉트 tag 생성
      $('.specific_tasks .tasks').append('<input type="text" id="'+ (nextSelect) +'" class="select" readOnly>');
      //다음 셀렉트 세팅 및 이벤트 등록
      eval(nextSelect + ' = createSearchSelect($("#" + nextSelect))')
      eval(nextSelect + '.setOptions(eachGroupList)');
      eval(nextSelect + '.setValue(["all_" + '+ nextIndex +'])'); //기본세팅 : 전체선택
      eval(nextSelect + '.select.on("change", function(){makeSelect(getName('+ nextSelect +') , '+ nextIndex +'); groupNodes('+ nextIndex +');})');

      function getName(searchSelect) {
        var getValue = searchSelect.getValue();
        var label = getValue[0].label;
        return label;
      }

      function groupNodes(index) {
        var thisSelect = null;
        var groupName = '';

        var elem = taskGroup;
        for (var i=1; i<=index; i++) {
          thisSelect = eval('selectTaskGroup' + i);
          groupName = getName(thisSelect);

          if ( groupName === '전체선택' ) break;
          else {
            elem = elem.children.find(e => e.text === groupName);
          }
        }

        selectedGroupFlowChart(elem.index);
      }

    } else { //다음 셀렉트가 이미 생성되어 있는 경우
      hideNextSelects();
      deleteReplaceGroupName();

      if ( name === '전체선택' ) return; //전체선택은 다음 셀렉트 세팅 안함

      //자식 노드가 있을 때 다음 셀렉트 세팅
      if ( makeData(groupNameArr) ) {
        eval(nextSelect + '.clearOptions()');
        eval(nextSelect + '.setOptions(eachGroupList)');
        eval(nextSelect + '.show()');
        eval(nextSelect + '.setValue(["all_" + nextIndex])');
      }

      //현재 셀렉트(index) 다음에 있는 모든 셀렉트 가리기
      function hideNextSelects() {
        var target = null;
        var arrIndex = selectList.length + 1;
        var idx = index + 1;

        for (var i=idx; i<arrIndex; i++) {
          target = eval('selectTaskGroup' + i);
          target.hide();
        }
      }
    }

    // groupNameArr 선택한 그룹 추가
    function addGroupName() {
      groupNameArr.push(name);
    }

    // groupNameArr 선택한 그룹 수정
    function deleteReplaceGroupName() {
      groupNameArr.splice(index);
      groupNameArr[index-1] = name;
    }

    //셀렉트 데이터 생성
    function makeData(arr) {
      if ( arr.length === 0 ) { //최초생성
        eachGroupList.push({'label': '전체선택', 'value': 'all_' + nextIndex});
        for (var i=0; i<taskGroup.children.length; i++) {
          var group = taskGroup.children[i];
          var obj = {};
          obj.label = group.text;
          obj.value = obj.label + '_' + nextIndex;
          eachGroupList.push(obj);
        }

      } else {
        eachGroupList = [];

        var elem = taskGroup;
        for (var i=0; i<arr.length; i++) {
          elem = elem.children.find(e => e.text === arr[i]);
        }

        //children(연결된 노드) 없을때
        if ( !elem.children.length ) return false;

        eachGroupList.push({'label': '전체선택', 'value': 'all_' + nextIndex});
        for (var i=0; i<elem.children.length; i++) {
          var group = elem.children[i];
          var obj = {};
          obj.label = group.text;
          obj.value = obj.label + '_' + nextIndex;

          eachGroupList.push(obj);
        }
      }

      return true;
    }
  }
}

//노드 클릭 시 노드 정보 세팅
function setNodeData(){
  let tmpSaveCheck = saveCheck;
  nodeData = scenario.nodes.find(node => node.id === nodeValue);
  var typeEnd = nodeData.type === 'end';

  $('.now_task').after('<a class="now_task" data-id="'+ nodeData.id +'" title="현재 태스크 자세히보기">'+ nodeData.label +'</a>');
  $('.now_task').eq(0).remove();
  $('.now_task').on('click', function(){
    changeContents(PAGES.TASK_DETAIL);
    getTaskNode(nodeData.id);
  });

  // 기본 답변, 조건 답변 reset
  $('#basic_utter_info p').empty();
  $('#intent_list .cont').empty();
  $('#entity_list .cont').empty();

  // type : end
  if ( typeEnd ) {
    // 인텐트 답변
    $('#intent_list .cont').append('<div class="vb_answer no_list">등록된 조건답변이 없습니다.</div>');
    // 엔티티 답변
    $('#entity_list .cont').append('<div class="vb_answer no_list">등록된 조건답변이 없습니다.</div>');

    // 답변 최대 반복 초과 시 task
    scenarioOverTask.select.attr('disabled', '');
    scenarioOverTask.clearValue();
    
    // 답변 최대 반복 횟수
    $('select[name="repeat_answer"]').attr('disabled', '').val('');

    // 사용자 설정
    selectAnswerSpeak.select.attr('disabled', '').val('');
    selectAnswerDial.select.attr('disabled', '').val('');

    // 사용자 발화를 인식 할 구간(무시 구간) / 반복해서 말 할 구간(반복 구간)
    $('.answer_repeat').addClass('disabled');
    $('.answer_repeat p').empty();

    $('.answer_ignore').addClass('disabled');
    $('.answer_ignore p').empty();

    saveCheck = tmpSaveCheck;
    return;
  }

  // selected node attr
  var attr = nodeData.attr[0] === undefined ? undefined : nodeData.attr[0];

  // 기본 답변
  var utter = attr.utter;
  $('#basic_utter_info .btn_box').remove();
  $('#basic_utter_info').append(handleBasic(utter));

  // 조건 답변(인텐트) 추가 버튼 활성화
  $('.add_answer').eq(0).removeAttr('disabled');

  // 인텐트 답변
  var intentList = attr.intentList;
  for (var i=0; i<intentList.length; i++) {
    $('#intent_list .cont').append(handleIntent(intentList[i], 'INTENT'));
  }

  // 엔티티 답변
  $('#entity_list .cont').append('<div class="vb_answer no_list">등록된 조건답변이 없습니다.</div>');

  // type : start, task
  // 답변 최대 반복 횟수
  var maxTurn = attr.maxTurn;
  $('select[name="repeat_answer"]').removeAttr('disabled').val(maxTurn); //답변 최대 반복

  // 사용자 설정 (답변/다이얼)
  var inputType = attr.inputType;

  if ( maxTurn === -1 ) { //선택
    selectAnswerSpeak.select.attr('disabled', '').val('');
    selectAnswerDial.select.attr('disabled', '').val('');
  } else {
    selectAnswerSpeak.select.removeAttr('disabled');
    selectAnswerSpeak.setOptions(renderOptionList(0, maxTurn));
    selectAnswerSpeak.setValue(renderValueList(0, inputType));

    selectAnswerDial.select.removeAttr('disabled');
    selectAnswerDial.setOptions(renderOptionList(1, maxTurn));
    selectAnswerDial.setValue(renderValueList(1, inputType));
  }

  setDisabledList(); // ex) 답변 1회차 선택 시 다이얼 1회차는 disabled 처리

  // 답변 최대 반복 초과 시 task
  var taskOverMax = !attr.taskOverMax && attr.maxTurn === '0' ? '' : ( !attr.taskOverMax ? '종료' : attr.taskOverMax);
  scenarioOverTask.setOptions(selectMaxTaskArr); //태스크 리스트

  if ( attr.maxTurn === -1 || attr.maxTurn === '0' ) {//선택(무한x)
    //초과 task 설정 불가
    scenarioOverTask.select.attr('disabled', '');
    scenarioOverTask.clearValue();

  } else {
    scenarioOverTask.select.removeAttr('disabled');
    scenarioOverTask.setValue(['setting_task_' + taskOverMax]);
  }

  // 답변 구간 설정
  var utter = (!attr.utter) ? '' : attr.utter;
  var acceptSttStcIdx = !attr.acceptSttStcIdx ? undefined: attr.acceptSttStcIdx.split(',');
  var repeatAnswerStcIdx = !attr.repeatAnswerStcIdx ? 'default' : attr.repeatAnswerStcIdx.split(',');

  // 사용자 발화를 인식 할 구간(무시 구간) / 반복해서 말 할 구간(반복 구간)
  $('.answer_ignore').removeClass('disabled');
  $('.answer_ignore p').html(markedSentence(utter, acceptSttStcIdx));
  
  $('.answer_repeat').removeClass('disabled');
  $('.answer_repeat p').html(markedSentence(utter, repeatAnswerStcIdx));

  if ( attr.maxTurn === '0' ) {//반복 횟수가 없으면
    $('.answer_repeat').addClass('disabled'); //반복 구간 설정x
    scenarioOverTask.select.attr('disabled', ''); //답변 최대 반복 초과 시 task 설정x
    scenarioOverTask.clearValue();
  }
  saveCheck = tmpSaveCheck;
}

// 다른 노드 클릭 시 노드 정보 check
function checkNodeData() {
  if ( nodeData.type === 'end' ) {
    return false;
  }

  //사용자 설정 선택 없을 시 default 0 (답변)
  var checkInputType = nodeData.attr[0].inputType.replace(-1, 0);
  nodeData.attr[0].inputType = checkInputType;

  var checkMaxTurn = nodeData.attr[0].maxTurn;
  if ( checkMaxTurn === -1 ) {
    mui.alert('최대 반복 횟수를 설정해주세요.');
    return true;
  }
  var checkTaskOverMax = nodeData.attr[0].taskOverMax;
  if ( Number(checkMaxTurn) > 0 && !checkTaskOverMax ) {
    mui.alert('답변 최대 반복 초과 시 실행될 태스크를 선택해주세요.');
    return true;
  }
  return false;
}

// 답변 - 기본 답변 세팅
function handleBasic(answer) {
  var $root = $('#basic_utter_info');
  var originAnswer = answer;
  var answerText = '';

  var $template = $('template#add_intent').clone();
  var $temp = $($template.html());
  var $buttonBox = $temp.find('.btn_box');

  //버튼
  var buttonBox = $buttonBox.find('div'); // eq(0) 수정/삭제/플레이, eq(1) 임시저장/취소
  var editBtn = $buttonBox.find('.text_edit');
  var deleteBtn = $buttonBox.find('.delete');
  var playBtn = $buttonBox.find('.audio_play');
  var saveBtn = $buttonBox.find('.save_temporarily');
  var cancelBtn = $buttonBox.find('.cancel');
  var pauseBtn = $buttonBox.find('.audio_pause');
  var stopBtn = $buttonBox.find('.audio_stop');

  var prevPlayBtn = null;
  var prevStopBtn = null;
  var prevPauseBtn = null;

  $root.find('p').text(originAnswer);

  deleteBtn.remove();
  pauseBtn.hide();
  stopBtn.hide();
  buttonBox.eq(1).hide();

  editBtn.on('click', function(){ //수정 클릭
    if (intentEditMode) {
      mui.alert('수정 중인 답변이 있습니다.<br>먼저 저장해주세요.');
      return;
    }

    resetAudioButton();
    editMode();
    intentEditMode = true;
    buttonBox.eq(0).hide();
    buttonBox.eq(1).show();

    //기본 답변 수정 모드
    function editMode(){
      originAnswer = $root.find('.answer').text();
      var pToTextarea = $('<textarea rows="3" class="ipt_txt answer"></textarea>');

      pToTextarea.val(originAnswer);

      $root.find('.answer').replaceWith(pToTextarea);
      pToTextarea.focus();
    }
  });

  saveBtn.on('click', function(){ //임시저장 클릭
    answerText = $root.find('.answer').val();
    $root.find('.info_small.warning').remove();

    intentEditMode = false;
    buttonBox.eq(0).show();
    buttonBox.eq(1).hide();

    viewMode();

    $root.find('.answer').text(answerText);
    nodeData.attr[0].utter = answerText;
    saveCheck = true;
  });

  playBtn.on('click', function(){ //stt 재생 클릭, TTSAudioEvent()
    // audio 버튼 기록
    if ( nowAudioButtons.play ) {
      prevPlayBtn = nowAudioButtons.play;
      prevPauseBtn = nowAudioButtons.pause;
      prevStopBtn = nowAudioButtons.stop;
    }
    nowAudioButtons.play = playBtn;
    nowAudioButtons.pause = pauseBtn;
    nowAudioButtons.stop = stopBtn;

    if ( prevPlayBtn && ( playBtn !== prevPlayBtn ) ) { //다른 플레이 버튼을 눌렀으면
      prevPlayBtn.show();
      prevPauseBtn.hide();
      prevStopBtn.hide();
      stopBtn.show();
    }

    if (!originAnswer) {
      mui.alert('답변이 비어있습니다. 답변을 채워주세요.');

      TTSAudio.pause();
      isPause = false;
      prevAudioInfo = '';
      return;
    }

    nowAudioInfo = 'basic' + nodeData.label;
    hasAudio = !(!prevAudioInfo || prevAudioInfo !== nowAudioInfo);

    if ( !hasAudio ) { //음성 최초 시작(음성 만들기)
      TTSAudio.pause();

      $('.vb_tab_cont.active').find('.audio_play').attr('disabled', '');
      $('.vb_tab_cont.active').find('.text_edit').attr('disabled', '');
      $('.vb_tab_cont.active').find('.delete').attr('disabled', '');

      playBtn.attr('title', '음성생성 중..');
      stopBtn.attr('disabled', '').show();
      buttonBox.append('<span class="info_small audio_making">음성생성 중..</span>');

      TTSAudioSetting(originAnswer);
      prevAudioInfo = nowAudioInfo;

    } else if ( isPause ) {
      playBtn.hide();
      pauseBtn.show();
      stopBtn.removeAttr('disabled');
    }

    TTSAudio.play();
    isPause = false;
  });

  pauseBtn.on('click', function(){
    hasAudio = true;
    isPause = true;
    TTSAudio.pause();

    playBtn.show();
    pauseBtn.hide();
  });

  stopBtn.on('click', function(){
    hasAudio = true;
    isPause = true;
    TTSAudio.pause();
    TTSAudio.currentTime = 0;

    playBtn.show();
    pauseBtn.hide();
    stopBtn.attr('disabled', '');
  });

  cancelBtn.on('click', function(){ //취소 클릭
    mui.confirm('작성하던 내용이 저장되지 않습니다. 계속 하시겠습니까?', {
      onClose: function(isOk){
        if(isOk){
          intentEditMode = false;
          buttonBox.eq(0).show();
          buttonBox.eq(1).hide();
          viewMode();
          $root.find('.answer').text(originAnswer);
        }
      }
    });
  });

  return $buttonBox;

  //기본 답변 보기 모드
  function viewMode() {
    $root.find('.info_small.warning').remove();

    var textareaToP = $('<p class="ipt_txt answer"></p>');
    $root.find('.answer').replaceWith(textareaToP);
  }

  //오디오 정지 및 버튼 초기화
  function resetAudioButton() {
    var notPlaying = TTSAudio.paused;
    if ( notPlaying ) return;

    TTSAudio.setAttribute('src', '');
    prevAudioInfo = '';

    if ( prevPlayBtn ) {
      prevPlayBtn.show();
      prevPauseBtn.hide();
      prevStopBtn.hide();
    }
    nowAudioButtons.play.removeAttr('disabled').show();
    nowAudioButtons.pause.hide();
  }
}

// 답변 - 조건 답변 세팅
function handleIntent(intentObj, type) { // type : INTENT or ENTITY
  var $template = $('template#add_intent').clone();
  var $temp = $($template.html());
  var intentNumber = null;
  var originAnswer = '';
  var originIntent = null; //intent는 ''(always) 비교값이 있으므로 초기화는 null
  var originTask = '';
  var originTaskId = '';
  var answerText = '';
  var intentText = '';
  var taskText = '';
  var selectAnswerTask = null;

  //버튼
  var buttonBox = $temp.find('.btn_box div'); // eq(0) 수정/삭제/플레이, eq(1) 임시저장/취소
  var editBtn = $temp.find('.btn_box .text_edit');
  var deleteBtn = $temp.find('.btn_box .delete');
  var playBtn = $temp.find('.btn_box .audio_play');
  var saveBtn = $temp.find('.btn_box .save_temporarily');
  var cancelBtn = $temp.find('.btn_box .cancel');
  var pauseBtn = $temp.find('.btn_box .audio_pause');
  var stopBtn = $temp.find('.btn_box .audio_stop');

  var prevPlayBtn = null;
  var prevStopBtn = null;
  var prevPauseBtn = null;

  if (intentObj) { //intentObj를 화면에 보여줌
    originAnswer = intentObj.answer;
    originIntent = !intentObj.intent ? 'always' : intentObj.intent;
    originTask = intentObj.nextTask;

    var findTask = taskInfo.find(e => e.label === intentObj.nextTask);

    if ( !findTask ) {
      mui.alert(intentObj.nextTask + ' (은)는 등록되지 않은 태스크입니다.<br>태스크를 추가 해주세요.');
      $temp.find('.taskA').attr('title', '태스크를 추가해주세요.');
      originTaskId = '';

    } else if ( originTask === '종료' ) {
      $temp.find('.taskA').removeAttr('title').css({
        'text-decoration': 'none',
        'cursor': 'default',
      });
      originTaskId = findTask.id;

    } else {
      originTaskId = findTask.id;
    }

    $temp.find('.intentA').text(originIntent).attr('data-id', originIntent);
    $temp.find('.taskA').text(originTask).attr('data-id', originTaskId);

    $temp.find('a').on('click', function(){ //의도, 태스크 클릭 이벤트
      var thisA = $(this);
      var thisData = thisA.attr('data-id');

      if ( thisA.hasClass('taskA') ) {
        if (findTask && thisData !== '종료') {
          getTaskNode(thisData);
          changeContents(PAGES.TASK_DETAIL);

        } else if (!findTask) {
          mui.alert(thisA.text() + ' (은)는 등록되지 않은 태스크입니다.<br>태스크를 추가 해주세요.')
        }
      }

      if ( thisA.hasClass('intentA') ) {
        var intentName = thisData === 'always' ? '' : thisData;
        moveIntentDetail(data.host, intentName, 'name');
      }
    });

    $temp.find('p').text(intentObj.answer);
    buttonBox.eq(1).hide();

  } else { //신규추가
    editMode();
    intentEditMode = true;
    buttonBox.eq(0).hide();
  }

  pauseBtn.hide();
  stopBtn.hide();

  editBtn.on('click', function(){ //수정 클릭
    if (intentEditMode) {
      mui.alert('수정 중인 답변이 있습니다.<br>먼저 저장해주세요.');
      return;
    }

    resetAudioButton();
    editMode();
    intentEditMode = true;
    buttonBox.eq(0).hide();
    buttonBox.eq(1).show();
  });

  saveBtn.on('click', function(){ //임시저장 클릭
    intentText = $temp.find('.intentInput').val();
    selectAnswerTask.root.next('.info_small.warning').remove();
    var originIntentDup = originIntent === intentText;

    if ( !intentText ) { //인텐트 명을 작성하지 않았을 때
      selectAnswerTask.root.after($('<p class="info_small warning">인텐트를 작성해주세요.</p>'));
      return;
    }

    if ( !originIntentDup ) { //인텐트 명을 기존과 다르게 수정했을 때
      //다른 인텐트 명과 중복 체크
      var intentListDup = nodeData.attr[0].intentList.find(e => e.intent === intentText);

      if ( intentListDup ) {
        selectAnswerTask.root.after($('<p class="info_small warning">이미 등록된 인텐트 입니다.</p>'));
        return;
      }
    }

    var selectTask = selectAnswerTask.getValue();
    taskText = selectTask[0].label;

    if (!taskText) { //태스크 선택을 하지 않았을 때
      selectAnswerTask.root.after($('<p class="info_small warning">태스크를 선택해주세요.</p>'));
      return;
    }

    answerText = $temp.find('.answer').val();

    intentEditMode = false;
    buttonBox.eq(0).show();
    buttonBox.eq(1).hide();
    intentSave(type);
  });

  playBtn.on('click', function(){ //stt 재생 클릭 TTSAudioEvent()
    // audio 버튼 기록
    if ( nowAudioButtons.play ) {
      prevPlayBtn = nowAudioButtons.play;
      prevPauseBtn = nowAudioButtons.pause;
      prevStopBtn = nowAudioButtons.stop;
    }
    nowAudioButtons.play = playBtn;
    nowAudioButtons.pause = pauseBtn;
    nowAudioButtons.stop = stopBtn;

    if ( prevPlayBtn && ( playBtn !== prevPlayBtn ) ) { //다른 플레이 버튼을 눌렀으면
      prevPlayBtn.show();
      prevPauseBtn.hide();
      prevStopBtn.hide();
    }

    if (!originAnswer) {
      mui.alert('답변이 비어있습니다. 답변을 채워주세요.');

      TTSAudio.pause();
      isPause = false;
      prevAudioInfo = '';
      return;
    }

    nowAudioInfo = originIntent + originTask;
    hasAudio = !(!prevAudioInfo || prevAudioInfo !== nowAudioInfo);

    if ( !hasAudio ) { //음성 최초 시작(음성 만들기)
      TTSAudio.pause();

      $('.vb_tab_cont.active').find('.audio_play').attr('disabled', '');
      $('.vb_tab_cont.active').find('.text_edit').attr('disabled', '');
      $('.vb_tab_cont.active').find('.delete').attr('disabled', '');

      playBtn.attr('title', '음성생성 중..');
      stopBtn.attr('disabled', '').show();
      buttonBox.prepend('<span class="info_small audio_making">음성생성 중..</span>');

      TTSAudioSetting(originAnswer);
      prevAudioInfo = nowAudioInfo;

    } else if ( isPause ) {
      playBtn.hide();
      pauseBtn.show();
      stopBtn.removeAttr('disabled');
    }

    TTSAudio.play();
    isPause = false;
  });

  pauseBtn.on('click', function(){
    hasAudio = true;
    isPause = true;
    TTSAudio.pause();

    playBtn.show();
    pauseBtn.hide();
  });

  stopBtn.on('click', function(){
    hasAudio = true;
    isPause = true;
    TTSAudio.pause();
    TTSAudio.currentTime = 0;

    playBtn.show();
    pauseBtn.hide();
    stopBtn.attr('disabled', '');
  });

  cancelBtn.on('click', function(){ //취소 클릭
    mui.confirm('작성하던 내용이 저장되지 않습니다. 계속 하시겠습니까?', {
      onClose: function(isOk){
        if(isOk){
          intentEditMode = false;

          if (!intentObj.intent) { //신규추가 중 취소
            selectAnswerTask.root.replaceWith($('<input type="text" class="taskInput">'));
            selectAnswerTask.remove();
            $temp.remove();
          } else {
            buttonBox.eq(0).show();
            buttonBox.eq(1).hide();

            viewMode();

            //수정 전 값으로 치환
            $temp.find('.answer').text(originAnswer);
            $temp.find('.intentA').text(originIntent);
            $temp.find('.taskA').text(originTask);
          }
          setNoList(type);
        }
      }
    });
  });

  deleteBtn.on('click', function(){ //삭제 클릭
    resetAudioButton();

    mui.confirm('< ' + intentObj.intent + ' >' + ' 인텐트를 삭제하시겠습니까?', {
      onClose: function(isOk){
        if(isOk){
          intentNumber = nodeData.attr[0].intentList.findIndex(e => e.intent === intentObj.intent);

          $temp.remove();
          deleteIntentEdge();
          nodeData.attr[0].intentList.splice(intentNumber, 1);
          handleEdgeLabel(nodeValue, originTaskId, originTask);
          setNoList(type);
          updateFlowChart();

          saveCheck = true;
        }
      }
    });
  });

  return $temp;

  //인텐트 답변 수정 모드
  function editMode(){
    var pToInput = $('<input type="text" class="ipt_txt answer">');
    var intentAToInput = $('<input type="text" class="ipt_txt intentInput">');
    var taskAToSelect = $('<input type="text" id="nextTask" class="select taskInput" readOnly>');

    //값 설정
    var intentCheck = originIntent === '' ? 'always' : originIntent;
    pToInput.val(originAnswer);
    intentAToInput.val(intentCheck);
    taskAToSelect.val(originTask);

    if (!intentObj) { //신규추가
      intentObj = {
        'answer': '',
        'intent': '',
        'nextTask': '',
        'info': '',
      }

      originIntent = null; //저장 시 인텐트 명을 비교하기 때문에 초기화
    }

    //태그 설정
    $temp.find('.answer').replaceWith(pToInput);
    $temp.find('.intentA').replaceWith(intentAToInput);
    $temp.find('.taskA').replaceWith(taskAToSelect);

    selectAnswerTask = createSearchSelect($temp.find('.taskInput'));
    var selectValue = 'answer_task_' + originTask;
    selectAnswerTask.setOptions(selectAnswerTaskArr);
    selectAnswerTask.setValue([selectValue]);

    pToInput.focus();
  }

  //인텐트 답변 보기 모드
  function viewMode() {
    $temp.find('.info_small.warning').remove();

    var inputToP = $('<p class="ipt_txt answer"></p>');
    var intentInputToA = $('<a title="의도 자세히 보기" class="intentA" data-id="'+ intentText +'"></a>');
    var taskSelectToA = $('<a title="다음 태스크 자세히 보기" class="taskA" data-id="'+ originTaskId +'"></a>');

    //값 설정
    inputToP.text(answerText);
    intentInputToA.text(intentText);
    taskSelectToA.text(taskText);

    //태그 설정
    $temp.find('.answer').replaceWith(inputToP);
    $temp.find('.intentInput').replaceWith(intentInputToA);

    selectAnswerTask.root.replaceWith($('<input type="text" class="taskInput">'));
    selectAnswerTask.remove();
    $temp.find('.taskInput').replaceWith(taskSelectToA);

    $temp.find('a').on('click', function(){ //의도, 태스크 클릭 이벤트
      var thisA = $(this);
      var thisData = thisA.attr('data-id');

      if ( thisA.hasClass('taskA') ) {
        if (findTask && thisData !== '종료') {
          getTaskNode(thisData);
          changeContents(PAGES.TASK_DETAIL);

        } else if (!findTask) {
          mui.alert(thisA.text() + ' (은)는 등록되지 않은 태스크입니다.<br>태스크를 추가 해주세요.')
        }
      }

      if ( thisA.hasClass('intentA') ) {
        var intentName = thisData === 'always' ? '' : thisData;
        moveIntentDetail(data.host, intentName, 'name');
      }
    });
  }

  //인텐트 저장 (scenario node)
  function intentSave(type) {
    answerText = $temp.find('.answer').val();
    var intentVal = $temp.find('.intentInput').val();
    taskText = $temp.find('.taskInput').val();
    var endTaskId = null;

    viewMode();

    if (originTask === '종료' || taskText === '종료') {
      endTaskId = handleEndTask();
    }

    intentText = intentVal === 'always' ? '' : intentVal;
    var intentCheck = originIntent === 'always' ? '' : originIntent;
    intentNumber = nodeData.attr[0].intentList.findIndex(e => e.intent === intentCheck);

    if ( originIntent == null ) {//신규추가
      intentObj.answer = answerText;
      intentObj.intent = intentText;
      intentObj.nextTask = taskText;

      if (type === 'INTENT') {
        nodeData.attr[0].intentList.push(intentObj);
      }

    } else {//기존 정보 수정
      if (type === 'INTENT') {
        nodeData.attr[0].intentList[intentNumber].answer = answerText;
        nodeData.attr[0].intentList[intentNumber].intent = intentText;
        nodeData.attr[0].intentList[intentNumber].nextTask = taskText;
      }
    }

    handleIntentEdge(intentNumber, endTaskId);
    updateFlowChart();
    saveCheck = true;

    originAnswer = answerText;
    originIntent = intentText;
    originTask = taskText;
  }
  
  //리스트 없음 체크
  function setNoList(type) {
    if (!nodeData.attr[0].intentList.length) {
      if (type === 'INTENT') {
        $('#intent_list .cont').empty().append('<div class="vb_answer no_list">등록된 조건답변이 없습니다.</div>');
      }
    }
  }

  //종료 task 생성 및 id 확인
  function handleEndTask() {
    var taskId = null;

    var hasEndTask = nodeData.attr[0].intentList.find(e => e.nextTask === '종료');

    if (hasEndTask) { //종료 노드가 있으면 id 반환
      var endTasks = scenario.nodes.filter(e => e.type === 'end');
      taskId = null;

      for (var i=0; i<endTasks.length; i++) {
        var connectedEnd = scenario.edges.find(e => e.source === nodeValue && e.target === endTasks[i].id)

        if(connectedEnd) {
          taskId = connectedEnd.target;
          break;
        }
      }
    } else {//종료 노드가 없으면 생성 및 id 반환
      taskId = generateUUID();

      var newNode = {
        'id': taskId,
        'label': "종료",
        'type': "end",
        'left': 0,
        'top' : 0,
        'attr': [],
      };
      scenario.nodes.push(newNode); //종료 노드 생성
    }

    return taskId;
  }

  //인텐트 엣지(링크)저장 (scenario edge)
  function handleIntentEdge(index, endTaskId) { // edge = link 같은 의미 / index : 인텐트가 없으면(-1) 신규추가, 인텐트가 있으면 기존수정
    allTaskInfo = allTask();

    var source = nodeData;
    var originTarget = allTaskInfo.find(e => e.label === originTask); //기존 태스크
    var target = allTaskInfo.find(e => e.label === taskText);

    if (index === -1) {//신규 엣지(link) 추가
      addEdge();
    } else { //엣지(link) 수정
      modifyEdge();
    }

    function addEdge() {
      var newEdge = {'source': source.id, 'target': target.id, 'data': {'attr': [], 'id': generateUUID(), 'label': intentText, 'type': 'default'},}

      //종료 task는 UI편의 상 각 task에 하나씩 생성할 수 있음
      if (endTaskId) {
        newEdge.target = endTaskId;
        target = allTaskInfo.find(e => e.id === endTaskId);
      }

      scenario.edges.push(newEdge); //edge 추가
      modifyEdge();
    }

    function modifyEdge() {
      if (endTaskId) {
        //종료 였거나, 종료로 수정한 경우 - 종료 id 찾기 혹은 종료 생성
        if (originTask === '종료') {
          originTarget = allTaskInfo.find(e => e.id === endTaskId);
          originTaskId = endTaskId;
        }

        if (taskText === '종료') {
          target = allTaskInfo.find(e => e.id === endTaskId);
        }
      }

      //origin 엣지 label 수정
      if (originTask && originTask !== taskText) {
        //origin 엣지 label 수정
        var originIndex = scenario.edges.findIndex(e => e.source === source.id && e.target === originTarget.id);
        scenario.edges[originIndex].target = target.id; //task 수정

        //origin 태스크가 다른 edge에 연결되어 있는지 확인
        originIndex = scenario.edges.findIndex(e => e.target === originTaskId);

        if ( originIndex === -1 && originTask === '종료' ) {//origin 태스크가 다른 edge와도 연결된게 없다면 삭제
          var nodeIndex = scenario.nodes.findIndex(e => e.id === originTaskId);
          scenario.nodes.splice(nodeIndex, 1);

        } else {
          //origin target 엣지 label 수정
          handleEdgeLabel(source.id, originTarget.id, originTarget.label);
        }
      }

      //target 엣지 label 수정
      handleEdgeLabel(source.id, target.id, target.label);
    }
  }

  //인텐트 엣지(링크)저장 시 라벨 수정
  function handleEdgeLabel(sourceId, targetId, nextTaskText) {
    var originEdgeArr = scenario.edges.filter(e => e.source === sourceId && e.target === targetId);
    var originLabelFilter = nodeData.attr[0].intentList.filter(e => e.nextTask === nextTaskText);
    var originLabelArr = originLabelFilter.map(e => e.intent === '' ? 'always' : e.intent);
    var originLabel = originLabelArr.join('/');

    originEdgeArr.forEach(function(e) {//origin 변경에 대한 라벨 수정
      e.data.label = originLabel;
    });
  }

  //인텐트 삭제
  function deleteIntentEdge() {
    var source = nodeData;

    if ( originTaskId === '종료' ) {
      var endTasks = scenario.nodes.filter(e => e.type === 'end');

      for (var i=0; i<endTasks.length; i++) {
        var connectedEnd = scenario.edges.find(e => e.source === nodeValue && e.target === endTasks[i].id)

        if(connectedEnd) {
          originTaskId = connectedEnd.target;
          break;
        }
      }
    }

    var edgeIndex = scenario.edges.findIndex(e => e.source === source.id && e.target === originTaskId);
    scenario.edges.splice(edgeIndex, 1); //edge 삭제

    //다음태스크가 다른 edge에 연결되어 있는지 확인
    var nextTaskIndex = scenario.edges.findIndex(e => e.target === originTaskId);

    if ( nextTaskIndex === -1 && originTask === '종료' ) {//다음 태스크가 다른 edge와도 연결된게 없다면 다음 태스크 삭제
      var nodeIndex = scenario.nodes.findIndex(e => e.id === originTaskId);

      scenario.nodes.splice(nodeIndex, 1);
    }
  }

  //오디오 정지 및 버튼 초기화
  function resetAudioButton() {
    var notPlaying = TTSAudio.paused;
    if ( notPlaying ) return;

    TTSAudio.setAttribute('src', '');
    prevAudioInfo = '';

    if ( prevPlayBtn ) {
      prevPlayBtn.show();
      prevPauseBtn.hide();
      prevStopBtn.hide();
    }
    nowAudioButtons.play.removeAttr('disabled').show();
    nowAudioButtons.pause.hide();
  }
}

// 답변 - 조건 답변 추가
function addIntent(type) { // type : INTENT or ENTITY
  if (intentEditMode) {
    mui.alert('수정 중인 답변이 있습니다.<br>먼저 저장해주세요.');
    return;
  }

  var target = event.target;
  var targetCont = $(target).parent('.tab_cont_tit').next('.cont');
  targetCont.find('.vb_answer.no_list').remove();
  targetCont.append(handleIntent(null, type));
  targetCont.scrollTop(targetCont[0].scrollHeight);
}

// onchange e, 초과 task
function changeTaskOverMax() {
  saveCheck = true;
  scenarioOverTask.select.attr('disabled', '').val('');

  if ( nodeData.type === 'end' ) return;
  else if ( nodeData.attr[0].maxTurn === -1 || nodeData.attr[0].maxTurn === '0' ) {
    nodeData.attr[0].taskOverMax = '';
    return;
  }
  
  scenarioOverTask.select.removeAttr('disabled');
  var getValue = scenarioOverTask.getValue();
  var label = getValue[0].label;

  scenarioOverTask.select.val(label);
  nodeData.attr[0].taskOverMax = label;
}

// onchange e, 반복 횟수
function changeMaxTurn(val) {
  saveCheck = true;
  if ( nodeData.type === 'end' ) {
    $('#repeat_answer').attr('disabled', '').val('');
    selectAnswerSpeak.select.attr('disabled', '').val('');
    selectAnswerDial.select.attr('disabled', '').val('');
    return;
  }

  val = Number(val); //maxTurn

  $('#repeat_answer').removeAttr('disabled');
  selectAnswerSpeak.select.removeAttr('disabled');
  selectAnswerDial.select.removeAttr('disabled');

  if (val === -1 || val === 0 ) { //선택(무한x), 반복없음
    scenarioOverTask.select.attr('disabled', '').val('');
    $('.answer_repeat').addClass('disabled');
    nodeData.attr[0].taskOverMax = '';

  } else {
    var getValue = scenarioOverTask.getValue();
    var label = !getValue.length ? '' : getValue[0].label;

    scenarioOverTask.select.removeAttr('disabled').val(label);
    $('.answer_repeat').removeClass('disabled');
    nodeData.attr[0].taskOverMax = label;

    if ( val === 0 ) { //반복 없음
      $('.answer_repeat').addClass('disabled');
    }
  }

  var inputType = (val === -1) ? '' : makeInputType(Number(val)+1); //inputType "0,0,1,0"

  nodeData.attr[0].maxTurn = ( val === -1 ? val : String(val) );
  nodeData.attr[0].inputType = inputType;

  if ( val === -1 ) { //선택(무한x)
    selectAnswerSpeak.select.attr('disabled', '').val('');
    selectAnswerDial.select.attr('disabled', '').val('');
  } else {
    selectAnswerSpeak.select.removeAttr('disabled');
    selectAnswerSpeak.setOptions(renderOptionList(0, val));
    selectAnswerSpeak.setValue(renderValueList(0, inputType));

    selectAnswerDial.select.removeAttr('disabled');
    selectAnswerDial.setOptions(renderOptionList(1, val));
    selectAnswerDial.setValue(renderValueList(1, inputType));

    changeInputType();
  }

  function makeInputType(num) {
    var speakType = '0,';
    var repeat = speakType.repeat(num);
    var value = repeat.slice(0, -1);

    return value;
  }
}

// onchange e, 사용자 설정
function changeInputType() {
  saveCheck = true;
  var speakValue = selectAnswerSpeak.getValue();
  var dialValue = selectAnswerDial.getValue();
  var maxTurn = Number(nodeData.attr[0].maxTurn);
  var joinArr = [...speakValue, ...dialValue];
  var inputTypeData = '';

  for(var i=0; i<maxTurn+1; i++) {
    var target = joinArr.find(e => e.label === String(i+1));

    if ( !target ) { //inputType을 선택하지 않으면 -1 로 표시하고, 저장 시 0 (default) 으로 저장
      if (i === 0) {
        inputTypeData = '-1'
      } else {
        inputTypeData = inputTypeData + ',-1';
      }

    } else {
      var split = target.value.split('_');
      var textType = split[1]; //speak or dial
      var indexType = (textType === 'speak') ? '0' : (textType === 'dial' ? '1' : '');

      if (i === 0) {
        inputTypeData = indexType
      } else {
        inputTypeData = inputTypeData + ','+ indexType;
      }
    }
  }

  nodeData.attr[0].inputType = inputTypeData;
  setDisabledList();
}

// onchange e, 답변 구간 설정
function changeSentenceRepeatIgnore() {
  saveCheck = true;
  var ignoreIdx = '';
  var repeatIdx = '';

  $('.answer_ignore p em.highlight').each(function(i) {
    var highlight = $(this);
    var dataParse = highlight.attr('data-parse');

    if ( i === 0 ) {
      ignoreIdx = ignoreIdx + dataParse;
    } else {
      ignoreIdx = ignoreIdx + ',' + dataParse;
    }
  });

  $('.answer_repeat p em.highlight').each(function(i) {
    var highlight = $(this);
    var dataParse = highlight.attr('data-parse');

    if ( i === 0 ) {
      repeatIdx = repeatIdx + dataParse;
    } else {
      repeatIdx = repeatIdx + ',' + dataParse;
    }
  });

  if ( !ignoreIdx ) {
    delete nodeData.attr[0].acceptSttStcIdx;
  } else {
    nodeData.attr[0].acceptSttStcIdx = ignoreIdx;
  }

  if ( !repeatIdx ) {
    delete nodeData.attr[0].repeatAnswerStcIdx;
  } else {
    nodeData.attr[0].repeatAnswerStcIdx = repeatIdx;
  }

}

// 사용자 설정 option list
function renderOptionList(typeIndex, maxTurn) {
  var type = typeIndex === 0 ? 'speak' : ((typeIndex === 1) ? 'dial' : 'undefined');
  var inputOptions = [];

  for (var i=0; i<=maxTurn; i++) {
    var obj = {'label': (i+1), 'value': (i+1) + '_' + type};
    inputOptions.push(obj);
  }

  return inputOptions;
}

// 사용자 설정 value list
function renderValueList(typeIndex, inputType) {
  var type = typeIndex === 0 ? 'speak' : ((typeIndex === 1) ? 'dial' : 'undefined');
  var typeArr = inputType.split(',');
  var inputValues = [];

  for (var i=0; i<typeArr.length; i++) {
    if (typeArr[i] === String(typeIndex)) {
      var value = (i+1) + '_' + type;
      inputValues.push(value);
    }
  }

  return inputValues;
}

// 사용자 설정 set disabled
function setDisabledList() {
  var type = [selectAnswerSpeak, selectAnswerDial];

  for (var i=0; i<type.length; i++ ) {
    var getValue = type[i].getValue();
    var label = getValue.map(e => e.label);
    var target = (i === 0) ? type[1].options : type[0].options;

    target.find('input:checkbox').removeAttr('disabled');

    for (var ii=0; ii<label.length; ii++ ) {
      target.find('input[data-label=' + label[ii] + ']').attr('disabled', '');
    }
  }
}

// 답변 구간 설정 태그
function markedSentence(utter, indexList) {
  var sentence = '';
  var replaceUtter = utter.replace(/([.?!~])/gi, '$&|||||');
  var parsedUtter = replaceUtter.split('|||||');
  var lastUtter = parsedUtter.length - 1;

  if ( !parsedUtter[lastUtter] ) parsedUtter.pop();

  if ( indexList === 'default' ) { //모두 반복
    for (var i=0; i < parsedUtter.length; i++) {
      parsedUtter[i] = '<em class="highlight" onclick="utterHighLight()" data-parse="' + i + '">' + parsedUtter[i] + '</em>';
      sentence += parsedUtter[i];
    }
  } else {
    for (var i=0; i < parsedUtter.length; i++) {
      if ( indexList && indexList.includes(i.toString())) {
        parsedUtter[i] = '<em class="highlight" onclick="utterHighLight()" data-parse="' + i + '">' + parsedUtter[i] + '</em>';
      }else{
        parsedUtter[i] = '<em onclick="utterHighLight()" data-parse="' + i + '">' + parsedUtter[i] + '</em>';
      }
      sentence += parsedUtter[i];
    }
  }
  return sentence;
}

// 답변 구간 설정 highlight
function utterHighLight() {
  var $parseUtter = $(event.target);
  $parseUtter.toggleClass('highlight');

  changeSentenceRepeatIgnore();
}

function saveConfirm() {
  mui.confirm('저장하기 전 원본 엑셀 다운로드를 권장합니다.<br>계속 하시겠습니까?', {
    onClose: function(isOk) {
      if (isOk) {
        intentTableData = [];
        saveScenario();
      }
    }
  });
}

// 시나리오 저장
function saveScenario() {
  $('#vb_wrap').addClass('loading');

  $.ajax({
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

  }).then(function (response) {
    if (response.msg === 'Success') {
      if (response['appliedAt']) { //yyyy.mm.dd hh:mm Updated
        $('#update_date').text(response['appliedAt'] + ' Updated');
      }

      mui.alert($('input[name="saveScenarioSuccess"]').val());
      data.host = response.host;
      updateFlowChart();

    } else {
      mui.alert($('input[name="uploadExcelV2NoFile"]').val());
      console.log('[saveScenario msg] :', response.msg);
    }

    $('#vb_wrap').removeClass('loading');
    saveCheck = false;

  }).catch(function () {
    $('#vb_wrap').removeClass('loading');
    mui.alert($('input[name="saveScenarioError"]').val());
    console.log('[saveScenario error]')
  });
}

// uuid 생성
function generateUUID(pattern){
  if(!pattern){
    pattern = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx';
  }
  return pattern.replace(/[xy]/g, function(c) {
    const r = (Math.random() * 16) | 0, v = c === 'x' ? r : (r & 3) | 8
    return v.toString(16);
  })
}

function allTask() { //모든 task 정보 (종료 포함)
  var arr = [];

  for (var i=0, l=scenario.nodes.length; i<l; i++) {
    var node = scenario.nodes[i];
    var obj = {'label' : node.label, 'id' : node.id};

    arr.push(obj);
  }

  return arr;
}

//audio tag 세팅
function TTSAudioSetting(TTStext) {
  var audioText = TTStext.trim().replace("\n"," ");
  audioText.replace(/\&/g,"%26");

  //audio url 설정
  var srcURI = "ttsGet?nowText=" + audioText;
  var src = encodeURI(srcURI);
  TTSAudio.setAttribute('src', src);

  isPause = false;
}

//TTS audio tag 이벤트
function TTSAudioEvent() {
  TTSAudio.removeEventListener('canplaythrough', TTSCanplaythrough);
  TTSAudio.addEventListener('canplaythrough', TTSCanplaythrough);
  TTSAudio.removeEventListener('ended', TTSEnded);
  TTSAudio.addEventListener('ended', TTSEnded);

  function TTSCanplaythrough() {
    $('.vb_tab_cont.active').find('.audio_play').removeAttr('disabled');
    $('.vb_tab_cont .audio_making').remove();

    if ( !( prevAudioInfo && nowAudioInfo ) ) return;

    if ( !hasAudio ) { //음성 최초 시작
      nowAudioButtons.play.attr('title', '음성생성').hide();
      nowAudioButtons.pause.show();
      nowAudioButtons.stop.removeAttr('disabled');

    } else {
      nowAudioButtons.play.show();
      nowAudioButtons.pause.hide();
      nowAudioButtons.stop.attr('disabled', '');
    }

    $('.vb_tab_cont.active').find('.text_edit').removeAttr('disabled');
    $('.vb_tab_cont.active').find('.delete').removeAttr('disabled');
  }

  function TTSEnded() {
    TTSAudio.currentTime = 0;
    isPause = true;
    hasAudio = true;
  }
}