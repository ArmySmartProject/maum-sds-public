var toolkit;
var surface;
var lastTop = -80;
var lastLeft = 0;
var data;
var checkedNodeList = [];

function drawFlowChart(jsonData, lang) {
  if (JSON.stringify(jsonData) === '{}') {
    data = {
      "nodes":[],
      "edges":[],
      "groups":[],
      "ports":[]
    };
  } else {
    data = jsonData;
  }

  try {
    console.log("<drawFlowChart init json data v1>");
    console.log(data);

    lastTop = -80;
    lastLeft = 0;

    // 이상한 edge 제거
    let filterdNodeList = data.nodes;
    let filterdEdgeList = data.edges;
    for (let edgeIdx in data.edges) {
      let source = data.nodes.filter(it => it.id.includes(data.edges[edgeIdx].source))[0];
      let target = data.nodes.filter(it => it.id.includes(data.edges[edgeIdx].target))[0];
      if (source === undefined || target === undefined) {
        filterdEdgeList.splice(edgeIdx, 1);
      }
    }
    // 이상한 node 제거
   for (let nodeIdx in data.nodes) {
     let source = data.edges.filter(it => it.source.includes(data.nodes[nodeIdx].id))[0];
     let target = data.edges.filter(it => it.target.includes(data.nodes[nodeIdx].id))[0];
     if (source === undefined && target === undefined && data.nodes[nodeIdx].type !== 'global') {
       filterdNodeList.splice(nodeIdx, 1);
     }
   }
    // node, edge update
    data.nodes = filterdNodeList;
    data.edges = filterdEdgeList;

    // node 위치 설정
    var sourceList = [];
    var isInitData = true;

    // 첫번째 시작 노드 찾기
    // 첫번째 노드는 target에 존재x
    for (var nodeIdx in data.nodes) {
      if (data.nodes[nodeIdx].type === 'start') {
        sourceList.push(data.nodes[nodeIdx].id);
        break;
      }
    }

    // 엑셀 업로드로 변환된 json listing & 위치 잡기
    if (isInitData && sourceList.length > 0) {
      checkedNodeList = [];
      sourceList = makeNodeList(sourceList[0]);
      setNodeLocation(sourceList.shift(), sourceList, lang);
    }

    var sameEdges;
    var edges = data.edges;

    edges.forEach(edge => {
      sameEdges = edges.filter(it => it.source.includes(edge.source) && it.target.includes(edge.target));
      sameEdges.splice(0, 1);
      if (sameEdges.length > 0) {
        for(var i = 0; i < sameEdges.length; i++) {
          edges.find(function(item, j){
            if(item.data.id === sameEdges[i].data.id){
              edges.splice(j, 1);
              return j;
            }
          });
        }
      }
    });

    data.nodes.forEach(node => {
      if (node.w === undefined && node.type === 'global') {
    	  globalNodeTop -= 100;
        node.top = globalNodeTop;
        node.left = lastLeft;
        node.w = lang === 1 ? node.label.length * 17 + 42 : node.label.length * 10 + 42;
      }
    });

    // get a new instance of the Toolkit
    toolkit = jsPlumbToolkit.newInstance();

    var mainElement = document.querySelector(".scenario_view"),
        canvasElement = mainElement.querySelector(".scenario");

    // container clear
    canvasElement.innerHTML = '';

    surface = toolkit.render({
      container:canvasElement,
      layout:{
        type:"Spring"
      },
      enablePanButtons:false,
      events: {
        canvasClick:function(params) {
          $('.scenario_view ').removeClass('edit_show');
          makeAllPreview();
          changeSelectState();
          surface.zoomToFit();
          toolkit.clearSelection();
        }
      },
      view:{
        nodes:{
          "default":{
            template:"tmplNode",
            events:{
              click:nodeClickEvent
            }
          },
          "start":{
            parent:"default",
            template:"startNode"
          },
          "task":{
            parent:"default",
            template:"taskNode"
          },
          "global":{
            parent:"default",
            template:"globalTaskNode"
          },
          "usedGlobal":{
            parent:"default",
            template:"globalTaskNode"
          },
          "end":{
            parent:"default",
            template:"endNode"
          }
        },
        edges:{
          "connect":{
            anchor:"Continuous",
            endpoint: "Blank",
            connector: "StateMachine",
            paintStyle: { strokeWidth: 2, stroke: "#AAAAAA", outlineWidth: 3, outlineStroke: "transparent" }, //	paint style for this edge type.
            overlays:[
              ["Arrow", { location: 1, width: 10, length: 10 }]
            ]
          },
          "default":{
            parent:"connect",
            overlays:[
              ["Label", {label: "${label}"}]
            ]
          },
          "selected":{
            parent:"connect",
            paintStyle: { strokeWidth: 2, stroke: "#AAAAAA", outlineWidth: 3, outlineStroke: "transparent" },
            overlays:[
              ["Label", {label: "${label}"}]
            ],
          }
        },
        ports: {
          "start": {
            conditionType: "default"
          },
          "source": {
            maxConnections: -1,
            conditionType: "response"
          },
          "target": {
            maxConnections: -1,
            isTarget: true
          }
        },
        "states":{
          "selectState":{
            "*":{
              cssClass:"selectState"
            }
          }
        }
      }
    });
    // load the data.
    toolkit.load({
      data:data,
      onload:function() {
        surface.zoomToFit();
        addDialIcon();
      }
    });
  } catch (e) {
    alert('error');
    console.log(e);
  }
}

function addDialIcon() {
  var nodes = data.nodes;
  nodes.forEach(node => {
  if (node.attr.length > 0 && node.attr[0].hasOwnProperty('inputType')) {
    if (node.attr[0].inputType === 1 || node.attr[0].inputType === 2) {
      $("[data-jtk-node-id=" + node.id + "]").addClass('keypad');
    }
  }
});
}

var nodeClickEvent = function(params) {
  surface.centerOnAndZoom(params.node, 0.1);
  var nodeData = params.node.data;
  var utterList = [];
  var settingList = [];
  var oldKey = "";
  ignoreIdxArr = [];
  repeatIdxArr = [];
  changeNodeData =  nodeData;

  if (nodeData['attr'].length > 0) {
    var nodeAttr = nodeData['attr'][0];
    // '챗봇이 할 말' 탭에서 사용하는 utter list
    var utter = nodeAttr.hasOwnProperty('utter') ? nodeAttr.utter : '';
    var conditionType = nodeAttr.hasOwnProperty('conditionType') ? nodeAttr.conditionType : 0;
    var utterY = nodeAttr.hasOwnProperty('utterY') ? nodeAttr.utterY : '';
    var utterN = nodeAttr.hasOwnProperty('utterN') ? nodeAttr.utterN : '';
    var utterU = nodeAttr.hasOwnProperty('utterU') ? nodeAttr.utterU : '';
    var utterR = nodeAttr.hasOwnProperty('utterR') ? nodeAttr.utterR : '';

    utterList.push(utter, conditionType, utterY, utterN, utterU, utterR);

    var inputType = nodeAttr.hasOwnProperty('inputType') ? nodeAttr.inputType : 0;
    var maxTurn = nodeAttr.hasOwnProperty('maxTurn') ? nodeAttr.maxTurn : 0;
    // turn 제한 없을 경우 -1 : 빈값으로 보여주기
    if (maxTurn == 0) maxTurn = '';
    var taskOverMax = nodeAttr.hasOwnProperty('taskOverMax') ? nodeAttr.taskOverMax : '';
    var acceptSttStcIdx = nodeAttr.hasOwnProperty('acceptSttStcIdx') ? nodeAttr.acceptSttStcIdx : '';
    var repeatAnswerStcIdx = nodeAttr.hasOwnProperty('repeatAnswerStcIdx') ? nodeAttr.repeatAnswerStcIdx : '';
    // '설정' 탭에서 사용하는 info list
    settingList.push(inputType, maxTurn, taskOverMax, acceptSttStcIdx, repeatAnswerStcIdx);
  } else {
    utterList.push('', 0, '', '', '', '');
    settingList.push(-1, '', '', '', '');
  }

  var innerHtml = "";
  for(var key in nodeData['attr'][0]){
	  if(key == 'utterY'){

		  innerHtml += "<div class='user_content'>";
		  innerHtml += "<div class='sub_tit'><span>YES</span></div>";
		  innerHtml += "<a href='#chat_speech_delete' class='btn_icon delete btn_lyr_open'></a>";
		  innerHtml += "<div class='description'>";
		  innerHtml += "<p class='textarea' id='yes_say'></p>";
		  innerHtml += "</div>";
		  innerHtml += "</div>";

	  }else if(key == 'utterN'){

		  innerHtml += "<div class='user_content'>";
		  innerHtml += "<div class='sub_tit'><span>NO</span></div>";
		  innerHtml += "<a href='#chat_speech_delete' class='btn_icon delete btn_lyr_open'></a>";
		  innerHtml += "<div class='description'>";
		  innerHtml += "<p class='textarea' id='no_say'></p>";
		  innerHtml += "</div>";
		  innerHtml += "</div>";

	  }else if(key == 'utterU'){
		  innerHtml += "<div class='user_content'>";

		  innerHtml += "<div class='sub_tit'><span>UNKNOWN</span></div>";
		  innerHtml += "<a href='#chat_speech_delete' class='btn_icon delete btn_lyr_open'></a>";
		  innerHtml += "<div class='description'>";
		  innerHtml += "<p class='textarea' id='unknown_say'></p>";
		  innerHtml += "</div>";
		  innerHtml += "</div>";

	  }else if(key == 'utterR'){

		  innerHtml += "<div class='user_content'>";
		  innerHtml += "<div class='sub_tit'><span>REPEAT</span></div>";
		  innerHtml += "<a href='#chat_speech_delete' class='btn_icon delete btn_lyr_open'></a>";
		  innerHtml += "<div class='description'>";
		  innerHtml += "<p class='textarea' id='repeat_say'></p>";
		  innerHtml += "</div>";
		  innerHtml += "</div>";

	  }else if(key.substring(0,5) == 'utter' && key.split('utter')[1] != "" && key != 'utterY' && key != 'utterN' && key != 'utterU' && key != 'utterR'){

		  innerHtml += "<div class='user_content'>";
		  innerHtml += "<div class='sub_tit'><span>"+ key.split('utter')[1].toUpperCase() +"</span></div>";
		  innerHtml += "<a href='#chat_speech_delete' class='btn_icon delete btn_lyr_open'></a>";
		  innerHtml += "<div class='description'>";
		  innerHtml += "<p class='textarea' id='"+ key.split('utter')[1].toLowerCase() +"_say'>"+nodeData['attr'][0][key]+"</p>";
		  innerHtml += "</div>";
		  innerHtml += "</div>";

	  }
  }
  $('.intent').empty();
  $('.intent').append(innerHtml);


  $('.user_content .sub_tit').on('click', 'span', function(){

      var $thisTitle = $(this);
      var parent = $thisTitle.parent('.sub_tit');
      var desc = $thisTitle.text();
      var temp = '<input type="text" class="ipt_txt" value="' + desc + '">';

      parent.empty().html(temp);
      parent.find('input').focus();
      if(desc == "YES"){
   	      oldKey = "utterY";
      }else if(desc == "NO"){
   	      oldKey = "utterN";
      }else if(desc == "UNKNOWN"){
   	      oldKey = "utterU";
      }else if(desc == "REPEAT"){
       	  oldKey = "utterR";
      }else {
       	  oldKey = "utter"+desc;
      }
  });

  $('.user_content .sub_tit').on('focusout', 'input[type="text"]', function(){
      var $thisTitle = $(this);
      var parent = $thisTitle.parent('.sub_tit');
      var desc = $thisTitle.val();
      var temp = '<span>' + desc + '</span>';
      var warningTemp = '<span class="warning">이미 등록되어 있는 조건입니다.</span>';

      var id = desc.toLowerCase() + "_say";
      $(this).parent().parent().find('.description > p').attr('id',id);

      if ( desc == '중복' ) { //중복된 타이틀을 사용할 경우 focusout이 되지 않고 경고문구 생성
          var hasWarning = parent.find('.warning').length;
          if ( !hasWarning ) {
              parent.append(warningTemp);
              return;
          }

          $thisTitle.focus();
          return;
      }
      parent.empty().html(temp);

      var newKey = "";
      	 if(desc == "YES"){
              newKey = "utterY";
           }else if(desc == "NO"){
              newKey = "utterN";
           }else if(desc == "UNKNOWN"){
              newKey = "utterU";
           }else if(desc == "REPEAT"){
              newKey = "utterR";
           }else{
              newKey = "utter"+desc;
           }

          if(nodeAttr[oldKey] != null){
	          	nodeAttr[newKey] = nodeAttr[oldKey];
          }else {
	          	nodeAttr[newKey] = "";
          }
          delete nodeAttr[oldKey];
  });

  //201125 AMR 텍스트 수정 박스 읽기모드
  $('.user_content .description').on('click', 'p.textarea', function(){
      var $thisParent = $(this).parent('.description');
      var $thisP = $(this);
      var desc = $(this).text();
      var temp = '<textarea id='+$(this).parent('.description').context.id+'>' + desc + '</textarea>';

      $thisP.remove();
      $thisParent.append(temp);
      $thisParent.find('textarea').focus();
  });

  //201125 AMR 텍스트 수정 박스 쓰기모드
  $('.user_content .description').on('focusout', 'textarea', function(){
      var $thisParent = $(this).parent('.description');
      var $thisTextarea = $thisParent.find('textarea');
      var desc = $thisTextarea.val();
      if(desc != null){
	      var temp = '<p class="textarea" id='+$(this).parent('.description').context.id+'>' + desc.replace(/(?!>{)({)/gi, '<span><').replace(/(?!}<)(})/gi, '></span>') + '</p>';
	      var checkId = $(this).parent().parent().find('.sub_tit > span').text().toLowerCase() + "_say";
	      var utterKey = "utter"+ $(this).parent().parent().find('.sub_tit > span').text();
	      $thisTextarea.remove();
	      $thisParent.append(temp);

	      var nodeAttr = changeNodeData['attr'][0];
	      // 기본 및 조건 발화 수정. text 입력 후 focusout이 되야 수정이 됨.
	      if($thisParent.context.id == "first_say"){
              nodeAttr.utter = desc;

              if(nodeAttr.acceptSttStcIdx == null){
              	nodeAttr.acceptSttStcIdx = '';
              }
              if(nodeAttr.repeatAnswerStcIdx == null){
              	nodeAttr.repeatAnswerStcIdx = '';
              }

	            $('.ignore').html(markedSentence(desc,nodeAttr.acceptSttStcIdx.split(',')));
	            $('.repeat').html(markedSentence(desc,nodeAttr.repeatAnswerStcIdx.split(',')));
	      } else if($thisParent.context.id == "yes_say"){
	        nodeAttr.utterY = desc;
	      }else if($thisParent.context.id == "no_say"){
	        nodeAttr.utterN = desc;
	      }else if($thisParent.context.id == "unknown_say"){
	        nodeAttr.utterU = desc;
	      }else if($thisParent.context.id == "repeat_say"){
	        nodeAttr.utterR = desc;
	      }else if($thisParent.context.id == checkId){
	        nodeAttr[utterKey] = desc;
	      }
      }
  });

  $('.btn_lyr_open').on('click', function() {
      var winHeight = $(window).height() * 0.7,
          hrefId = $(this).attr('href');
		$(this).addClass('actPop');
      $('body').css('overflow', 'hidden');
      if ($(".lyrWrap .lyr_bg").length == 0) {
			$('body').find(hrefId).wrap('<div class="lyrWrap"></div>');
          $('body').find(hrefId).before('<div class="lyr_bg"></div>');
      }
      //대화 UI
      $('.lyrBox .lyr_mid').each(function() {
        $(this).css('max-height', Math.floor(winHeight) + 'px');
      });

      //Layer popup close
      $('.btn_lyr_close, .lyr_bg').on('click', function() {
        $('.actPop').removeClass('actPop');
        $('body').css('overflow', '');
        $('body').find(hrefId).unwrap('<div class="lyrWrap"></div>');
        $('.lyr_bg').remove();
      });
    });

  // 기본발화, 조건발화 중 edit 모드인 경우가 있으면 edit 모드 제거
  $('#first_say').blur();
  $('#yes_say').blur();
  $('#no_say').blur();
  $('#unknown_say').blur();
  $('#repeat_say').blur();

  $('.edit_title').find('span').text(nodeData['label']);
  $('#first_say')[0].innerHTML = utterList[0].replace(/(?!>{)({)/gi, '<span>{').replace(/(?!}<)(})/gi, '}</span>');
  if (utterList[1] === 0) { // intent
    $('#condition_type_selectbox option:eq(0)').attr('selected', 'selected');
    $('.intent').css('display', 'block');
    $('.entity').css('display', 'none');
    // $('.add_entity_condition').css('display', 'none');
  } else if (utterList[1] === 1) { //entity
    $('#condition_type_selectbox option:eq(1)').attr('selected', 'selected');
    $('.intent').css('display', 'none');
    $('.entity').css('display', 'block');
    // $('.add_entity_condition').css('display', 'block');
    var edgeList = data.edges.filter(it => it.source.includes(nodeData['id']));
    $('.entity').empty();
    edgeList.forEach(function(edge) {
      var utter = '';
      if (edge.data.attr.length > 0 && edge.data.attr[0].hasOwnProperty('utter')) {
        utter = edge.data.attr[0].utter;
      }
      $('.entity').append('<div>\
          <input type="text" class="ipt_txt sub_tit" value="' + edge.data.label + '" readonly>\
          <textarea name="' + edge.data.id + '" cols="30" rows="2" readonly>' + utter + '</textarea></div>');
    });
  }
  $('#yes_say').text(utterList[2]);
  $('#no_say').text(utterList[3]);
  $('#unknown_say').text(utterList[4]);
  $('#repeat_say').text(utterList[5]);

  if (settingList[0] === 0) {
    $('input:checkbox[id="check01"]').attr("checked", true);
    $('input:checkbox[id="check02"]').attr("checked", false);
  } else if (settingList[0] === 1) {
    $('input:checkbox[id="check01"]').attr("checked", false);
    $('input:checkbox[id="check02"]').attr("checked", true);
  } else if (settingList[0] === 2) {
    $('input:checkbox[id="check01"]').attr("checked", true);
    $('input:checkbox[id="check02"]').attr("checked", true);
  } else {
    $('input:checkbox[id="check01"]').attr("checked", false);
    $('input:checkbox[id="check02"]').attr("checked", false);
  }
  $('#max_turn_cnt').val(settingList[1]);
  $('#task_over_max').val(settingList[2]);
  var ignoreIdx = settingList[3].split(',');
  var repeatIdx = settingList[4].split(',');
  $('.ignore').html(markedSentence(utterList[0], ignoreIdx));
  $('.repeat').html(markedSentence(utterList[0], repeatIdx));
  $('.scenario_view ').addClass('edit_show');
  changeSelectState(nodeData['id']);

  if(ignoreIdx != ""){
	  for(var i = 0; i < ignoreIdx.length; i++){
		  ignoreIdxArr.push(ignoreIdx[i]);
	  }
  }
  if(repeatIdx != ""){
	  for (var i = 0; i < repeatIdx.length; i++){
		  repeatIdxArr.push(repeatIdx[i]);
	  }
  }
  makeAllPreview();
};

function markedSentence(utter, indexList) {

  var sentence = '';
  var replaceUtter = utter.replace(/([.?!~])/gi, '$&|||||');
  var parsedUtter = replaceUtter.split('|||||');
  for (var i = 0; i < parsedUtter.length; i++) {
    if (indexList.includes(i.toString())) {
      parsedUtter[i] = '<em class="on" onclick="utterRange(this, '+i+');" onmouseover="hoverUtter(this);" onmouseout="outUtter(this);"  style="cursor:pointer;">' + parsedUtter[i] + '</em>';
    }else{
      parsedUtter[i] = '<em onclick="utterRange(this, '+i+');" onmouseover="hoverUtter(this);" onmouseout="outUtter(this);" style="cursor:pointer;">' + parsedUtter[i] + '</em>';
    }
    sentence += parsedUtter[i];
  }
  return sentence;
}

function makeNodeList(nodeId) {
  if (checkedNodeList.includes(nodeId)) {
    return nodeId;
  } else {
    checkedNodeList.push(nodeId);
    var res = [];

    var nodes = data.nodes;
    var edges = data.edges;

    // next node id 찾기
    for (var edgeIdx in edges) {
      if (nodeId === edges[edgeIdx].source) {
        res.push(edges[edgeIdx].target);
      }
    }
    // target이 없는 경우 (마지막 노드)
    if (res.length <= 0) {
      return nodeId;
    } else {
      var res_nodes = nodes.filter(it => it.id.includes(res[0]) || it.id.includes(res[1]));
      for (var i = 0; i < res_nodes.length; i++) {
        if (res_nodes[i].type === 'global') {
          var copy_node = $.extend({}, res_nodes[i]);
          copy_node.id = getUUID();
          data.nodes.push(copy_node);
          res_nodes[i].type = 'usedGlobal';
        }
      }

      // target id 2개가 같으면 하나로 줄이기
      if (res[0] === res[1]) {
        var res_edges = edges.filter(it => it.target.includes(res[0]));
        res_edges.forEach(res_edge => {
          if (res_edge.source === nodeId) {
          res_edge.data.label = "ALWAYS";
        }
      });
        res.pop();
        return [nodeId, makeNodeList(res[0])];
      } else if (nodeId === res[0]) { // 다음 노드의 Id가 현재 노드의 Id와 같은 경우 (현재노드 다시 반복하는 경우)
        return [nodeId, makeNodeList( res[1])];
      } else if (nodeId === res[1]) { // 다음 노드의 Id가 현재 노드의 Id와 같은 경우 (현재노드 다시 반복하는 경우)
        return [nodeId, makeNodeList(res[0])];
      } else if (res.length === 1){
        return [nodeId, makeNodeList(res[0])];
      } else {
        return [nodeId, makeNodeList(res[0]), makeNodeList(res[1])];
      }
    }
  }
}

function setNodeLocation(item1, item2, lang) {
  if (typeof (item1) === "string") {
    let node1 = data.nodes.filter(it => it.id.includes(item1))[0];
    if (node1['w'] === undefined) {
      setNodeSize(node1, lang);
    }
  } else if (item1 !== undefined) {
    if (item1.length === 1) {
      setNodeLocation(item1.shift(), undefined, lang);
    } else {
      setNodeLocation(item1.shift(), item1, lang);
    }
  }

  if (typeof (item2) === "string") {
    let node2 =data.nodes.filter(it => it.id.includes(item2))[0];
    if (node2['w'] === undefined) {
      setNodeSize(node2, lang);
    }
  } else if (item2 !== undefined) {
    if (item2.length === 1) {
      setNodeLocation(item2.shift(), undefined, lang);
    } else {
      setNodeLocation(item2.shift(), item2, lang);
    }
  }

  return 0;
}

function setNodeSize(node, lang) {
  let sourceList = [];
  let prevLeft = 0;
  let sourceNode;

  // source node id 찾기
  for (let edgeIdx in data.edges) {
    if (node.id === data.edges[edgeIdx].target) {
      // 중복된 sourceId는 list에 넣지 않도록 한다
      sourceNode = data.nodes.filter(it => it.id.includes(data.edges[edgeIdx].source))[0];
      if (sourceList.indexOf(sourceNode.left + sourceNode.w) === -1 && sourceNode.w !== undefined) {
        sourceList.push(sourceNode.left + sourceNode.w);
      }
    }
  }

  if (sourceList.length <= 0) { // 맨 처음 노드
    prevLeft = -120;
  } else if (sourceList.length == 1) { // sourceNode가 한개만 있는 경우
    prevLeft = sourceList[0];
  } else { // sourceNode가 여러개인 경우, left 값이 가장 큰 노드를 기준으로 한다.
    prevLeft = Math.max.apply(null, sourceList);
  }

  let width = 0;
  if (lang === 1) {
    width = node.label.length * 17 + 42;
  } else {
    width = node.label.length * 10 + 42;
  }

  node.left = prevLeft + 120;
  node.top = lastTop + 80;
  node.w = width;
  node.h = 'auto';

  lastTop = node.top;
  lastLeft = lastLeft < node.left ? node.left : lastLeft;
}

function reloadToolkit(loadData) {
    toolkit.clear();
    toolkit.load({data: loadData});
    addDialIcon();
}

function changeSelectState(nodeId) {

    var resEdges = nodeId ? data.edges.filter(it => it.source.includes(nodeId)):'';
    var resEdgeIds = [];

    if (resEdges.length > 0) {
      resEdges.forEach(edge => {
        resEdgeIds.push(edge['data']['id']);
      });
    }

    // 엣지 타입 바꾸기
    data.edges.forEach(edge => {
      if (resEdgeIds.includes(edge['data']['id'])) {
        edge['data']['type'] = 'selected';
      } else {
        edge['data']['type'] = 'default';
      }
    });
    // 기존에 selectState class를 가지고 있던 node,edge 초기화
    surface.deactivateState('selectState', toolkit);
    reloadToolkit(toolkit.exportData());

    // 현재 클릭된 node와 해당 node에 들어오고 나가는 edge에 selectState class 추가
    if (nodeId) {
      surface.activateState('selectState', nodeId);
    }
}

function getScenarioJsonData() {
  return toolkit.exportData();
}

function getUUID() { // UUID v4 generator in JavaScript (RFC4122 compliant)
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 3 | 8);
    return v.toString(16);
  });
}

