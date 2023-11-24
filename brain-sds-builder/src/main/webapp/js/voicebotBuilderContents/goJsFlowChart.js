go.Diagram.licenseKey = "2bf840e5b56758c511d35a25403e7efb0eab2d61cf8049a5590417f2ec5c611c23cce12855d488c8d7aa4cf41d799489cfd56f2fc74c043eb76187db42ea86a9bb6774b01d0f4389a25626c29faa28a1af2b75a291e774f7dd2b9ee2edfd939a5cbaa3d55ada0dba2a781937497eac";
var gojs = go.GraphObject.make;  // for conciseness in defining templates
var myDiagram = null;
var goJSdata = {};
var nowFlowNode = {};
var prevFlowNode = {};
var animation = null;

function makeGoJSdata(){
  goJSdata = {};
  let nodeDataArr = []
  let edgeDataArr = []
  for(let nCnt = 0; nCnt<scenario.nodes.length; nCnt++){
    let nowNode = scenario.nodes[nCnt]
    nodeDataArr.push({
      key: nowNode.id,
      type: nowNode.type,
      text: nowNode.label,
      attr: nowNode.attr,
    })
  }
  for(let eCnt = 0; eCnt<scenario.edges.length; eCnt++){
    let nowEdge = scenario.edges[eCnt]
    let sameEdge = edgeDataArr.find(edge => edge.from === nowEdge.source && edge.to === nowEdge.target && edge.text === nowEdge.data.label );

    if ( !sameEdge ) {
      edgeDataArr.push({
        from: nowEdge.source,
        to: nowEdge.target,
        data: nowEdge.data,
        text : nowEdge.data.label,
      })
    }
  }
  goJSdata = {'node' : nodeDataArr, 'edge' : edgeDataArr };
}

function makeFlowChart() {
  makeGoJSdata();
  myDiagram = gojs(go.Diagram, "flowChart",  // must name or refer to the DIV HTML element
    {
      allowDelete: false,
      "toolManager.mouseWheelBehavior": go.ToolManager.WheelZoom,
      initialAutoScale: go.Diagram.Uniform,  // an initial automatic zoom-to-fit
      contentAlignment: go.Spot.Center,  // align document to the center of the viewport
      layout:
        gojs(go.ForceDirectedLayout,  // automatically spread nodes apart
          { maxIterations: 200, defaultSpringLength: 30, defaultElectricalCharge: 100 }),
    }
  );

  //go js style
  var goFillColor = '#ffe499';
  var goBorderColor =  '#858a9b';
  var goHighLightColor = '#000';
  var goHighLightWidth = 3;
  var goNormalWidth = 1;
  var goOpacity = 1;

  // define each Node's appearance
  myDiagram.nodeTemplate =
    gojs(go.Node, "Auto",  // the whole node panel
      {
        click: function(e, node) {
          if ( nodeData ) {
            var checkDataProblem = checkNodeData();
            if (checkDataProblem) return;
          }

          var val = node.part.key;
          nodeValue = val;

          if (nowAudioButtons.play) {
            TTSAudio.setAttribute('src', '');
            hasAudio = false;
            prevAudioInfo = '';
            nowAudioInfo = '';

            nowAudioButtons.play.removeAttr('disabled').show();
            nowAudioButtons.pause.hide();
            nowAudioButtons.stop.hide();
            nowAudioButtons = {};
          }
          $('#nodeData').trigger('change');

          //node.part.key; node id
          //node.part.data; node obj
          //node.part.linksConnected; link info

          // highlight all Links and Nodes coming out of a given Node
          var diagram = node.diagram;
          diagram.startTransaction("highlight");
          // remove any previous highlighting
          diagram.clearHighlighteds();
          // for each Link coming out of the Node, set Link.isHighlighted
          node.findLinksOutOf().each(function(l) { l.isHighlighted = true; });
          // for each Node destination for the Node, set Node.isHighlighted
          node.findNodesOutOf().each(function(n) { n.isHighlighted = true; });
          diagram.commitTransaction("highlight");
        },
      },
      { locationSpot: go.Spot.Center },
      // define the node's outer shape, which will surround the TextBlock
      gojs(go.Shape, "RoundedRectangle",
        { fill: goFillColor, stroke: goBorderColor, strokeWidth: goNormalWidth, opacity: goOpacity, name: "SHAPE"},
        new go.Binding("isHighlighted", function(h) { return h ? goHighLightColor : goBorderColor; })
          .ofObject(),
        // the Shape.strokeWidth depends on whether Link.isHighlighted is true
        new go.Binding("strokeWidth", "isHighlighted", function(h) { return h ? goHighLightWidth : goNormalWidth; })
          .ofObject(),
      ),
      gojs(go.TextBlock,
        { font: "bold 14px helvetica, arial, sans-serif", margin: 8, opacity: goOpacity, name: "TextBlock" },
        new go.Binding("text", "text")),
    );

  // replace the default Link template in the linkTemplateMap
  myDiagram.linkTemplate =
    gojs(go.Link,  // the whole link panel
      { isLayoutPositioned: false },
      gojs(go.Shape,  // the link shape
        { opacity: goOpacity, name: "SHAPE" },
        // the Shape.stroke color depends on whether Link.isHighlighted is true
        new go.Binding("stroke", "isHighlighted", function(h) { return h ? goHighLightColor : goBorderColor; })
          .ofObject(),
        // the Shape.strokeWidth depends on whether Link.isHighlighted is true
        new go.Binding("strokeWidth", "isHighlighted", function(h) { return h ? goHighLightWidth : goNormalWidth; })
          .ofObject()
      ),
      gojs(go.Shape,  // the arrowhead
        { toArrow: "standard", opacity: goOpacity, name: "SHAPE" },
        new go.Binding("fill", "isHighlighted", function(h) { return h ? goHighLightColor : goBorderColor; })
          .ofObject(),
      ),
      gojs(go.Panel, "Auto",
        gojs(go.TextBlock,  // the label text
          { textAlign: "center", font: "12px helvetica, arial, sans-serif", opacity: goOpacity, name: "TextBlock" },
          new go.Binding("text", "text")
        ),
      )
    );

  myDiagram.model = new go.GraphLinksModel(goJSdata.node, goJSdata.edge);
  // gojs(go.Overview, "myOverviewDiv", { observed: myDiagram }); //오버뷰

  // Resize the diagram with this button
  document.getElementById('btnFullScreen').addEventListener('click', handleFullScreen);
}

function updateFlowChart() {
  //업데이트 data
  makeGoJSdata();
  myDiagram.model = new go.GraphLinksModel(goJSdata.node, goJSdata.edge);
  myDiagram.startTransaction();
  myDiagram.updateAllRelationshipsFromData();
  myDiagram.updateAllTargetBindings();
  myDiagram.commitTransaction("update");

  //특정 태스크 그룹이 선택되어 있었을 경우 이전과 같은 효과
  function getName(searchSelect) {
    var getValue = searchSelect.getValue();
    var label = getValue[0].label;
    return label;
  }

  var thisSelect = null;
  var groupName = '';
  var elem = taskGroup;

  for (var i=1; i<10; i++) { //임의로 10이라는 숫자 입력
    thisSelect = eval('selectTaskGroup' + i);
    groupName = getName(thisSelect);

    if ( !groupName || groupName === '전체선택' ) break;
    else {
      elem = elem.children.find(e => e.text === groupName);
    }
  }

  selectedGroupFlowChart(elem.index);

  //선택되어 있던 node, link 포커스
  var node = myDiagram.findNodeForKey(nodeValue);
  if (node === null) return; //선택되어 있던 node가 없다면
  else {
    // highlight all Links and Nodes coming out of a given Node
    node.diagram.startTransaction("highlight");
    // remove any previous highlighting
    node.diagram.clearHighlighteds();
    // for each Link coming out of the Node, set Link.isHighlighted
    node.findLinksOutOf().each(function(l) { l.isHighlighted = true; });
    // for each Node destination for the Node, set Node.isHighlighted
    node.findNodesOutOf().each(function(n) { n.isHighlighted = true; });
    node.diagram.commitTransaction("highlight");
  }
}

function selectedGroupFlowChart(nodeIndexArr) {
  if ( !nodeIndexArr ) { //전체 선택인 경우
    //모든 노드: opacity 1
    myDiagram.nodes.each(function(node){
      node.findObject("SHAPE").opacity = 1;
      node.findObject('TextBlock').opacity = 1;

      // for each Link coming out of the Node, set Link.isHighlighted
      node.findLinksOutOf().each(function(l) {
        l.isHighlighted = false;
        l.findObject("SHAPE").opacity = 1;
        l.findObject("TextBlock").opacity = 1;
      });

      // for each Node destination for the Node, set Node.isHighlighted
      node.findNodesOutOf().each(function(n) {
        n.findObject("SHAPE").opacity = 1;
      });
    });

    return;
  }

  //전체 선택이 아닌 경우
  //모든 노드: opacity 0.3
  myDiagram.nodes.each(function(node){
    node.findObject("SHAPE").opacity = 0.3;
    node.findObject('TextBlock').opacity = 0.3;

    // for each Link coming out of the Node, set Link.isHighlighted
    node.findLinksOutOf().each(function(l) {
      l.isHighlighted = false;
      l.findObject("SHAPE").opacity = 0.3;
      l.findObject("TextBlock").opacity = 0.3;
    });

    // for each Node destination for the Node, set Node.isHighlighted
    node.findNodesOutOf().each(function(n) {
      n.findObject("SHAPE").opacity = 0.3;
    });
  });

  //그룹에 속한 노드: opacity 1
  var key = null;
  var groupNode = null;

  for (var i=0; i<nodeIndexArr.length; i++) {
    key = scenario.nodes[nodeIndexArr[i]].id;
    groupNode = myDiagram.findNodeForKey(key);
    groupNode.findObject("SHAPE").opacity = 1;
    groupNode.findObject("TextBlock").opacity = 1;

    // for each Link coming out of the Node, set Link.isHighlighted
    groupNode.findLinksOutOf().each(function(l) {
      l.isHighlighted = true;
      l.findObject("SHAPE").opacity = 1;
      l.findObject("TextBlock").opacity = 1;
    });
  }
}

function handleFullScreen() {
  var flowChart = myDiagram.div;
  var $button = $(event.target);
  var $specificTasks = $('.specific_tasks');
  var $vbTest = $('.vb_test');
  var $testButton = $('#test_on');

  if ( $button.hasClass('full') ) {
    document.body.style.overflow = 'visible';
    $button.removeClass('full');
    $testButton.removeClass('full');
    flowChart.removeAttribute('class');
    $specificTasks.removeClass('full');
    $vbTest.removeClass('full');

  } else {
    document.body.style.overflow = 'hidden';
    $button.addClass('full');
    $testButton.addClass('full');
    flowChart.setAttribute('class', 'full');
    $specificTasks.addClass('full');
    $vbTest.addClass('full');
  }

  myDiagram.commandHandler.zoomToFit();
  myDiagram.zoomToFit();
}

function flowNode(taskName) {
  var id = '';
  allTaskInfo = allTask();
  prevFlowNode = { ...nowFlowNode };
  
  if (taskName == "" || taskName == null){
	  taskName == "종료";
  };
  
  if(taskName != "종료"){
	  nodeValue = scenario.nodes.find(e => e.label === taskName).id;
  }
  
  if ( taskName === '종료') {
	  var endTasks = scenario.nodes.filter(e => e.type === 'end');
      id = null;
      
      for (var i=0; i<endTasks.length; i++) {
        var connectedEnd = scenario.edges.find(e => e.source === nodeValue && e.target === endTasks[i].id)

        if(connectedEnd) {
          id = connectedEnd.target;
          break;
        }
      }

  } else {
    var task = allTaskInfo.find(e => e.label === taskName);
    id = task.id;
  }
  nowFlowNode.id = id;
  
  removeEffect(prevFlowNode);
  addEffect(nowFlowNode);

  function removeEffect(obj) {
    if ( !Object.keys(obj).length ) return;

    var node = myDiagram.findNodeForKey(obj.id);
    var nodeShape = node.findObject("SHAPE"); // assumes this Node contains a go.Shape with .name = "SHAPE"
    var nodeText = node.findObject("TextBlock"); // assumes this Node contains a go.Shape with .name = "SHAPE"

    animation.stop();

    nodeShape.opacity = obj.nodeOpacity; //이전 상태로 돌아가기
    nodeText.opacity = obj.textOpacity; //이전 상태로 돌아가기

    if ( obj.linkHighlighted ) {
      node.findLinksOutOf().each(function(l) { l.isHighlighted = obj.linkHighlighted });
    }

  }

  function addEffect(obj) {
    var node = myDiagram.findNodeForKey(obj.id);
    var nodeShape = node.findObject("SHAPE"); // assumes this Node contains a go.Shape with .name = "SHAPE"
    var nodeText = node.findObject('TextBlock');
    var nodeLinkCount = node.findLinksOutOf().count;

    animation = new go.Animation();

    obj.nodeOpacity = nodeShape.opacity; //이전 상태 기록
    obj.textOpacity = nodeText.opacity;

    if ( nodeLinkCount ) { //연결된 link가 있으면
      var isHighlighted = node.findLinksOutOf().tb.j[0].isHighlighted;
      obj.linkHighlighted = isHighlighted ? 'true' : 'false';
    }

    //effect
    nodeShape.opacity = 1;
    nodeText.opacity = 1;
    node.findLinksOutOf().each(function(l) { l.isHighlighted = true; });

    animation.runCount = 'Infinity';
    animation.duration = 1000;
    animation.reversible = true;
    animation.easing('EaseInOutQuad');
    animation.add(nodeShape, "fill", nodeShape.fill, '#B2B2B2');
    animation.add(nodeText, "fill", nodeShape.fill, '#B2B2B2');
    animation.start();

    //노드 zoom
    myDiagram.zoomToRect(node.actualBounds, 0.5);
    myDiagram.centerRect(node.actualBounds);
  }
}