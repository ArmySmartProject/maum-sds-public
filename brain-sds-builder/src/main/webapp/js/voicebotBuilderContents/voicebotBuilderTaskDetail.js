var testData = [{'label' : '선택안함', 'value' : 'depth1_default'}];
var testData02 = [{'label' : '선택안함', 'value' : 'depth2_default'}];
var testData03 = [{'label' : '선택안함', 'value' : 'depth3_default'}];
var testTaskNextData = [];
var testIntentData = [];
var testSelectedData = [];
var testSelectedData02 = [];
var testSelectedData03 = [];
var selectTaskGroup01 = null;
var selectTaskGroup02 = null;
var selectTaskGroup03 = null;
var selectTaskNext_1;
var selectIntent_1;


$(document).ready(function(){
  var anSelect = $('body').find('.an-search_option');
  anSelect.not(anSelect[0]).remove(); //이전에 생성한 an-select 초기화

  taskGroupMange();
  taskDetail();
  
  if(taskNode != null && taskNode != ''){
	  selectTaskGroup01 = createSearchSelect($('#task_group_depth1'));
	  selectTaskGroup01.setOptions(testData);
	  selectTaskGroup01.setValue(testSelectedData);
	  
	  if(selectTaskGroup01.getValue() != null && selectTaskGroup01.getValue() != ''){
		  if(selectTaskGroup01.getValue()[0].value != 'depth1_default'){
			  var selectTaskGroupHtml = '';
			  selectTaskGroupHtml += '<input type="text" id="task_group_depth2" class="select" readOnly>';
			  $("#root_task_group_depth1").after(selectTaskGroupHtml);
			  selectTaskGroup02 = createSearchSelect($('#task_group_depth2'));
			  selectTaskGroup02.setOptions(testData02);
	
			  if(testSelectedData02[0] == ""){
				  selectTaskGroup02.setValue(["depth2_default"]);
			  }else{
				  selectTaskGroup02.setValue(testSelectedData02);
			  }
		  }
	  }
	  
	  if(selectTaskGroup02 != null && selectTaskGroup02.getValue()[0].value != "depth2_default"){
		  var selectTaskGroupHtml = '';
		  selectTaskGroupHtml += '<input type="text" id="task_group_depth3" class="select" readOnly>';
		  $("#root_task_group_depth2").after(selectTaskGroupHtml);
		  selectTaskGroup03 = createSearchSelect($('#task_group_depth3'));
		  selectTaskGroup03.setOptions(testData03);

		  if(testSelectedData03[0] == ""){
			  selectTaskGroup03.setValue(["depth3_default"]);
		  }else{
			  selectTaskGroup03.setValue(testSelectedData03);
		  }
	  }
  }else{
	  selectTaskGroup01 = createSearchSelect($('#task_group_depth1'));
	  selectTaskGroup01.setOptions(testData);
	  selectTaskGroup01.setValue(['depth1_default']);
  }
  
  if(scenario != null){
	  nextTask();
  }
  
  $("#task_group_depth1").on('change', function (){
	  var getVal = selectTaskGroup01.getValue();
	  if(getVal != ''){
		  if(getVal[0].value == 'depth1_default' && selectTaskGroup02 != null){
				  selectTaskGroup02.clearOptions();
				  selectTaskGroup02 = null;
				  $("#root_task_group_depth2").remove();
				  if(selectTaskGroup03 != null){
					  selectTaskGroup03.clearOptions();
					  selectTaskGroup03 = null;
					  $("#root_task_group_depth3").remove();
				  }
		  }else if(getVal[0].value != 'depth1_default' && selectTaskGroup02 == null) {
			  var selectTaskGroupHtml = '';
			  selectTaskGroupHtml += '<input type="text" id="task_group_depth2" class="select" readOnly onchange="changeDepth2();">';
			  $("#root_task_group_depth1").after(selectTaskGroupHtml);
			  selectTaskGroup02 = createSearchSelect($('#task_group_depth2'));
			  selectTaskGroup02.setOptions(testData02);
			  selectTaskGroup02.setValue(["depth2_default"]);
		  }
	  }
  });
  
  $("#task_group_depth2").on('change', function (){
	  var getVal = selectTaskGroup02.getValue();
	  
	  if(getVal != ''){
		  if(getVal[0].value == 'depth2_default' && selectTaskGroup03 != null){
			  selectTaskGroup03.clearOptions();
			  selectTaskGroup03 = null;
			  $("#root_task_group_depth3").remove();
		  }else if(getVal[0].value != 'depth2_default' && selectTaskGroup03 == null){
			  
			  var selectTaskGroupHtml = '';
			  selectTaskGroupHtml += '<input type="text" id="task_group_depth3" class="select" readOnly>';
			  $("#root_task_group_depth2").after(selectTaskGroupHtml);
			  selectTaskGroup03 = createSearchSelect($('#task_group_depth3'));
			  selectTaskGroup03.setOptions(testData03);
			  selectTaskGroup03.setValue(["depth3_default"]);
		  }
	  }
  });
  
  //task 상세 및 추가 페이지 변경 체크
  $('input,select,textarea').change(function() {
	  saveCheck = true;
  });
  
});

function changeDepth2(){
	
	  var getVal = selectTaskGroup02.getValue();
	  if(getVal != ''){
		  if(getVal[0].value == 'depth2_default' && selectTaskGroup03 != null){
			  selectTaskGroup03.clearOptions();
			  selectTaskGroup03 = null;
			  $("#root_task_group_depth3").remove();
		  }else if(getVal[0].value != 'depth2_default' && selectTaskGroup03 == null){
			  
			  var selectTaskGroupHtml = '';
			  selectTaskGroupHtml += '<input type="text" id="task_group_depth3" class="select" readOnly>';
			  $("#root_task_group_depth2").after(selectTaskGroupHtml);
			  selectTaskGroup03 = createSearchSelect($('#task_group_depth3'));
			  selectTaskGroup03.setOptions(testData03);
			  selectTaskGroup03.setValue(["depth3_default"]);
		  }
	  }
}


function taskDetail(){
	if(taskNode != null && taskNode != ''){
		//task명
		$("#taskName").val(taskNode.label);
		//task 답변
		$("#taskAnswer").val(taskNode.attr[0] != null ? taskNode.attr[0].utter : '');
		
		//task Group 1Depth
		testSelectedData.push(taskNode.taskGroup.split("_")[0] != "" ? "group1_" + taskNode.taskGroup.split("_")[0] : 'depth1_default');
		//task Group 2Depth
		testSelectedData02.push(taskNode.taskGroup.split("_")[1] != null ? "group2_" + taskNode.taskGroup.split("_")[1] : '');
		//task Group 3Depth
		testSelectedData03.push(taskNode.taskGroup.split("_")[2] != null ? "group3_" + taskNode.taskGroup.split("_")[2] : '');
		if(taskNode.label.trim() == "*"){
			$('input[name="checkSuccessYn"]').prop('disabled', true);
		}else{
			if(taskNode.successYn === 'Y'){
				$("#checkSuccess").prop("checked", true);
			}else {
				$("#checkFail").prop("checked", true);
			}
		}
	}
}

function taskGroupMange(){
	if(scenario != null){
		var nodes = scenario.nodes;
		
		var depth1Arr = [{'label' : '선택안함', 'value' : 'depth1_default'}];
		var depth2Arr = [{'label' : '선택안함', 'value' : 'depth2_default'}];
		var depth3Arr = [{'label' : '선택안함', 'value' : 'depth3_default'}];
		
		for (var i = 0; i < nodes.length; i++) {
			if(nodes[i].taskGroup != null && nodes[i].taskGroup != ''){
				var depth1Obj = new Object();
				var depth2Obj = new Object();
				var depth3Obj = new Object();
				
				var taskDepth1 = nodes[i].taskGroup.split("_")[0] != null ? nodes[i].taskGroup.split("_")[0] : '';
				var taskDepth2 = nodes[i].taskGroup.split("_")[1] != null ? nodes[i].taskGroup.split("_")[1] : '';
				var taskDepth3 = nodes[i].taskGroup.split("_")[2] != null ? nodes[i].taskGroup.split("_")[2] : '';
				
				depth1Obj.label = taskDepth1;
				depth1Obj.value = "group1_" + taskDepth1;
				
				depth2Obj.label = taskDepth2;
				depth2Obj.value = "group2_" + taskDepth2;
				
				depth3Obj.label = taskDepth3;
				depth3Obj.value = "group3_" + taskDepth3;
				
				
				depth1Arr.push(depth1Obj);
				depth2Arr.push(depth2Obj);
				depth3Arr.push(depth3Obj);
			}
		}
		testData = depth1Arr.filter((element, index) => {
			return depth1Arr.findIndex((element2 , j) => {
				return element.label === element2.label;
			}) === index;
		});
		
		testData02 = depth2Arr.filter((element, index) => {
			return depth2Arr.findIndex((element2 , j) => {
				return element.label === element2.label && element.value === element2.value && element.label !== null && element.label !== '';
			}) === index;
		});
		
		testData03 = depth3Arr.filter((element, index) => {
			return depth3Arr.findIndex((element2 , j) => {
				return element.label === element2.label && element.value === element2.value && element.label !== null && element.label !== '';
			}) === index;
		});
	}
}

//Task Group 추가
function addTaskGroup(){
	var selectedhGroupDepth = document.getElementById("selectGroupDepth");
	var selectedValue = selectedhGroupDepth.options[selectedhGroupDepth.selectedIndex].value;
	
	var groupObj = new Object();
	var addGroupName = $("#addGroupName").val();

	if(selectedValue == "1depth"){
		if(addGroupName == ""){
			mui.alert("추가 할 그룹 명을 입력해주세요.");
			return false;
		}
		
		groupObj.label = addGroupName;
		groupObj.value = "group1_" + addGroupName;
		
		testSelectedData = testSelectedData != "" ? testSelectedData : ["depth1_default"];
		testData.push(groupObj);
		selectTaskGroup01.clearOptions();
		selectTaskGroup01.setOptions(testData);
		selectTaskGroup01.setValue(testSelectedData);
		
		modalClose($('#set_task_group'));
	}else if(selectedValue == "2depth"){
		if(addGroupName == ""){
			mui.alert("추가 할 그룹 명을 입력해주세요.");
			return false;
		}
		
		groupObj.label = addGroupName;
		groupObj.value = "group2_" + addGroupName;
		
		testSelectedData02 = testSelectedData02 != "" ? testSelectedData02 : ["depth2_default"];
		testData02.push(groupObj);
		selectTaskGroup02.clearOptions();
		selectTaskGroup02.setOptions(testData02);
		selectTaskGroup02.setValue(testSelectedData02);
		
		modalClose($('#set_task_group'));
	}else if(selectedValue == "3depth"){
		if(addGroupName == ""){
			mui.alert("추가 할 그룹 명을 입력해주세요.");
			return false;
		}
		
		groupObj.label = addGroupName;
		groupObj.value = "group3_" + addGroupName;
		
		testSelectedData03 = testSelectedData03 != "" ? testSelectedData03 : ["depth3_default"];
		testData03.push(groupObj);
		selectTaskGroup03.clearOptions();
		selectTaskGroup03.setOptions(testData03);
		selectTaskGroup03.setValue(testSelectedData03);
		
		modalClose($('#set_task_group'));
	}else {
		mui.alert("수정할 그룹 depth를 선택해주세요.");
	}
	
}

//Task Group 수정
function changeGroupDepth(){
	var selectGroupDepthModify = document.getElementById("selectGroupDepthModify");
	var selectedValue = selectGroupDepthModify.options[selectGroupDepthModify.selectedIndex].value;
	
	$("#selectDepthGroup").empty();
	var selectGroupHtml = "";
	
	if(selectedValue == "1depth"){
		$("#selectDepthGroup").attr("disabled", false);
		
		selectGroupHtml += "<option value='default'>depth를 선택해주세요</option>";
		for (var i = 0; i < testData.length; i++) {
			selectGroupHtml += "<option value=\""+testData[i].label+"\">"+ testData[i].label +"</option>";
		}
		
		$("#selectDepthGroup").append(selectGroupHtml);
	}else if(selectedValue == "2depth"){
		$("#selectDepthGroup").attr("disabled", false);
		
		selectGroupHtml += "<option value='default'>depth를 선택해주세요</option>";
		for (var i = 0; i < testData02.length; i++) {
			selectGroupHtml += "<option value=\""+testData02[i].label+"\">"+ testData02[i].label +"</option>";
		}
		
		$("#selectDepthGroup").append(selectGroupHtml);
	}else if(selectedValue == "3depth"){
		$("#selectDepthGroup").attr("disabled", false);
		
		selectGroupHtml += "<option value='default'>depth를 선택해주세요</option>";
		for (var i = 0; i < testData03.length; i++) {
			selectGroupHtml += "<option value=\""+testData03[i].label+"\">"+ testData03[i].label +"</option>";
		}
		
		$("#selectDepthGroup").append(selectGroupHtml);
	}else if(selectedValue == "default"){
		$("#selectDepthGroup").attr("disabled", true);
		
		selectGroupHtml += "<option value='default'>depth를 선택해주세요</option>";
		
		$("#selectDepthGroup").append(selectGroupHtml);
	}
	
}

function updateGroupName() {
	var selectGroupDepthModify = document.getElementById("selectGroupDepthModify");
	var selectedValue = selectGroupDepthModify.options[selectGroupDepthModify.selectedIndex].value;
	
	var updateGroupName = $("#updateGroupName").val();
	
	if(selectedValue == "1depth"){
		var selectDepthGroup = document.getElementById("selectDepthGroup");
		var selectedDepthGroupValue = selectDepthGroup.options[selectDepthGroup.selectedIndex].value;
		
		for (var i = 0; i < testData.length; i++) {
			if(testData[i].label == selectedDepthGroupValue){
				testData[i].label = updateGroupName;
				testData[i].value = "group1_" + updateGroupName;
			}
		}
		
		selectTaskGroup01.clearOptions();
		selectTaskGroup01.setOptions(testData);
		if(selectTaskGroup01.getValue()[0].label === selectedDepthGroupValue){
			selectTaskGroup01.setValue(['group1_'+updateGroupName]);
		}
		//selectTaskGroup01.setValue(['depth1_default']);

		modalClose($('#set_task_group'));
	}else if(selectedValue == "2depth"){
		var selectDepthGroup = document.getElementById("selectDepthGroup");
		var selectedDepthGroupValue = selectDepthGroup.options[selectDepthGroup.selectedIndex].value;
		
		for (var i = 0; i < testData02.length; i++) {
			if(testData02[i].label == selectedDepthGroupValue){
				testData02[i].label = updateGroupName;
				testData02[i].value = "group2_" + updateGroupName;
			}
		}
		
		selectTaskGroup02.clearOptions();
		selectTaskGroup02.setOptions(testData02);
		if(selectTaskGroup02.getValue()[0].label === selectedDepthGroupValue){
			selectTaskGroup02.setValue(['group2_'+updateGroupName]);
		}
		modalClose($('#set_task_group'));
	}else if(selectedValue == "3depth"){
		var selectDepthGroup = document.getElementById("selectDepthGroup");
		var selectedDepthGroupValue = selectDepthGroup.options[selectDepthGroup.selectedIndex].value;
		
		for (var i = 0; i < testData03.length; i++) {
			if(testData03[i].label == selectedDepthGroupValue){
				testData03[i].label = updateGroupName;
				testData03[i].value = "group3_" + updateGroupName;
			}
		}
		
		selectTaskGroup03.clearOptions();
		selectTaskGroup03.setOptions(testData03);
		if(selectTaskGroup03.getValue()[0].label === selectedDepthGroupValue){
			selectTaskGroup03.setValue(['group3_'+updateGroupName]);
		}
		modalClose($('#set_task_group'));
	}else if(selectedValue == "default"){
		mui.alert("수정할 그룹 depth를 선택해주세요.");
	}
}
// task group 삭제
function deleteGroup(){
	var selectGroupDepthModify = document.getElementById("selectGroupDepthModify");
	var selectedValue = selectGroupDepthModify.options[selectGroupDepthModify.selectedIndex].value;
	
	if(selectedValue == "1depth"){
		var selectDepthGroup = document.getElementById("selectDepthGroup");
		var selectedDepthGroupValue = selectDepthGroup.options[selectDepthGroup.selectedIndex].value;
		
		$.each(testData, function(i){
		    if(testData[i].label === selectedDepthGroupValue){
		    	testData.splice(i,1);
		    	return false;
		    }
		});
		
		selectTaskGroup01.clearOptions();
		selectTaskGroup01.setOptions(testData);
		
		modalClose($('#set_task_group'));
	}else if(selectedValue == "2depth"){
		var selectDepthGroup = document.getElementById("selectDepthGroup");
		var selectedDepthGroupValue = selectDepthGroup.options[selectDepthGroup.selectedIndex].value;
		
		$.each(testData02, function(i){
		    if(testData02[i].label === selectedDepthGroupValue){
		    	testData02.splice(i,1);
		    	return false;
		    }
		});
		
		selectTaskGroup02.clearOptions();
		selectTaskGroup02.setOptions(testData02);
		
		modalClose($('#set_task_group'));
	}else if(selectedValue == "3depth"){
		var selectDepthGroup = document.getElementById("selectDepthGroup");
		var selectedDepthGroupValue = selectDepthGroup.options[selectDepthGroup.selectedIndex].value;
		
		$.each(testData03, function(i){
		    if(testData03[i].label === selectedDepthGroupValue){
		    	testData03.splice(i,1);
		    	return false;
		    }
		});
		
		selectTaskGroup03.clearOptions();
		selectTaskGroup03.setOptions(testData03);
		
		modalClose($('#set_task_group'));
	}else if(selectedValue == "default"){
		mui.alert("삭제할 그룹 depth를 선택해주세요.");
	}
}

function getIntentList(){
	testIntentData = [];
	
	let ajPromise = new Promise((resolve, reject) =>
	{ 
		$.ajax({
		url: "voiceBot/getIntentList",
		data: JSON.stringify({
			host: data.host}),
			type: "POST",
			async: false,
			contentType: 'application/json'
		}).then(function (response) {
			var intentList = response;
			
			for (var i = 0; i < intentList.length; i++) {
				var intentObj = new Object();
				intentObj.label = intentList[i].Name;
				intentObj.value = intentList[i].Name;
				
				testIntentData.push(intentObj);
			}
			testIntentData.push({"label":"always","value":"always"})
			resolve(true);
		}).catch(function (response) {
			console.log('apply() status code: ' + response.status);
			resolve(false);
		});
	});
	return ajPromise;
}

function nextTask(){
	
	getIntentList().then(isSuccess => {
		if(!isSuccess) return;
	
	if(taskNode != null && taskNode != ''){
		if(scenario != null){
			var nodes = scenario.nodes;
			
			nodes = nodes.filter((element, index) => {
				return nodes.findIndex((element2 , j) => {
					return element.label === element2.label && element.label != null && element.label != '';
				}) === index;
			});
			
			testTaskNextData = [];
			
			for (var i = 0; i < nodes.length; i++) {
				var nextTaskObj = new Object();
				nextTaskObj.label = nodes[i].label.replace(/ /g,'') != '*' ? nodes[i].label : '공통';
				nextTaskObj.value = nodes[i].label.replace(/ /g,'') != '*' ? nodes[i].label : '공통';
				testTaskNextData.push(nextTaskObj);
			}
			
			$("#nextTaskTable tbody").empty();
			var idx = 0;
			var nodeLabel = "";
			
			if(taskNode.attr[0] != null){
				var intentList = taskNode.attr[0].intentList
				for (var i = 0; i < intentList.length; i++) {
					idx++;
					
					var nextTaskId = 'test_nextTask_' + idx;
					var nextIntentId = 'test_nextIntent_' + idx;
					
					var nextTaskHtml = "";
					for (var j = 0; j < nodes.length; j++) {
						if(intentList[i].nextTask == nodes[j].label){
							nodeLabel = nodes[j].label;
							nextTaskHtml += '<tr>';
							nextTaskHtml += '<td><input type="checkbox" id="checkbox_' + idx + '" class="checkbox" name="checkDelete[]">';
							nextTaskHtml += '<label for="checkbox_' + idx + '"></label></td>';
							nextTaskHtml += '<td><input type="text" id="test_nextIntent_' + idx + '" class="select" readOnly></td>';
							nextTaskHtml += '<td><input type="text" id="test_nextTask_' + idx + '" class="select" style="width:180px;" readOnly>';
							nextTaskHtml += '<div class="help" onclick="showNextAnswer(\''+intentList[i].nextTask+'\')">A<div class="help_desc help_next_task_answer"><i>클릭 시 다음 테스크 답변이 테이블 상단에 보여집니다.</i></div></div>';
							nextTaskHtml += '</td>';
							nextTaskHtml += '<td><input type="text" class="ipt_txt" id="taskInfoAdd_' + idx + '" value=\"' + intentList[i].info + '\"></td>';
							nextTaskHtml += '<td><input type="text" class="ipt_txt" id="taskAnswerAdd_' + idx + '" value=\"' + intentList[i].answer + '\"></td>';
							nextTaskHtml += '</tr>';
							
						}
					}
					$("#nextTaskTable tbody").append(nextTaskHtml);
					
					for (var k = 0; k < testTaskNextData.length; k++) {
						testTaskNextData[k].value = "test_"+ intentList[i].nextTask.replace(/ /g,'_') + "_" + intentList[i].intent + "_" + testTaskNextData[k].label.replace(/ /g,'_');
					}
					
					for (var l = 0; l < testIntentData.length; l++) {
						testIntentData[l].value = "test_" + intentList[i].intent + "_" + intentList[i].nextTask.replace(/ /g,'_') + "_" + testIntentData[l].label;
					}
					
					var setVal = "test_"+ intentList[i].nextTask.replace(/ /g,'_') + "_" + intentList[i].intent + "_" + nodeLabel.replace(/ /g,'_');
					var setIntentVal = "test_" + intentList[i].intent + "_" + nodeLabel.replace(/ /g,'_') + "_" + (intentList[i].intent != "" ? intentList[i].intent : "always");
					
					window['var selectTaskNext_' + idx];
					eval('selectTaskNext_' + idx + '= createSearchSelect($("#"+nextTaskId))');
					eval("selectTaskNext_" + idx + ".setOptions(testTaskNextData)");
					eval("selectTaskNext_" + idx + ".setValue([setVal])");
					
					window['var selectIntent_' + idx];
					eval('selectIntent_' + idx + '= createSearchSelect($("#"+nextIntentId))');
					eval("selectIntent_" + idx + ".setOptions(testIntentData)");
					eval("selectIntent_" + idx + ".setValue([setIntentVal])");
					
					
					eval("selectIntent_" + idx + ".options.find('input[data-label=\"always\"]').attr(\"disabled\",\"\")");
				}
			}
		}	
			
		}else {
			var nodes = scenario.nodes;
			
			nodes = nodes.filter((element, index) => {
				return nodes.findIndex((element2 , j) => {
					return element.label === element2.label && element.label != null && element.label != '';
				}) === index;
			});
			
			testTaskNextData = [];
			
			for (var i = 0; i < nodes.length; i++) {
				var nextTaskObj = new Object();
				
				nextTaskObj.label = nodes[i].label.replace(/ /g,'') != '*' ? nodes[i].label : '공통';
				nextTaskObj.value = nodes[i].label.replace(/ /g,'') != '*' ? nodes[i].label : '공통';
				testTaskNextData.push(nextTaskObj);
			}
			
			$("#nextTaskTable tbody").empty();
			
			var nextTaskHtml = "";
			
			nextTaskHtml += '<tr>';
			nextTaskHtml += '<td><input type="checkbox" id="checkbox_1" class="checkbox" name="checkDelete[]">';
			nextTaskHtml += '<label for="checkbox_1"></label></td>';
			nextTaskHtml += '<td><input type="text" id="test_nextIntent_1" class="select" readOnly></td>';
			nextTaskHtml += '<td><input type="text" id="test_nextTask_1" class="select" style="width:180px;" readOnly>';
			nextTaskHtml += '<div class="help" onclick="showNextAnswer(\'\')">A<div class="help_desc help_next_task_answer"><i>클릭 시 다음 테스크 답변이 테이블 상단에 보여집니다.</i></div></div>';
			nextTaskHtml += '</td>';
			nextTaskHtml += '<td><input type="text" class="ipt_txt" id="taskInfoAdd_1" value=\"\"></td>';
			nextTaskHtml += '<td><input type="text" class="ipt_txt" id="taskAnswerAdd_1" value=\"\"></td>';
			nextTaskHtml += '</tr>';
			
			$("#nextTaskTable tbody").append(nextTaskHtml);
			
			selectTaskNext_1 = createSearchSelect($("#test_nextTask_1"));
			selectTaskNext_1.setOptions(testTaskNextData);
			
			selectIntent_1 = createSearchSelect($("#test_nextIntent_1"));
			selectIntent_1.setOptions(testIntentData);
			
			selectIntent_1.options.find('input[data-label="always"]').attr('disabled','');
			
		}
		
	// nextTask 변경시 nextTask 답변 변경
	$("input[id*='test_nextTask_']").on('change', function(){
		var nodes = scenario.nodes;
		var idx = $(this).attr('id').split('_')[2];
		var nextTaskValue = eval("selectTaskNext_" + idx + ".getValue()");
		
		if(nextTaskValue[0].label != "종료"){
			for(var i = 0; i < nodes.length; i++){
				if(nodes[i].attr[0] != null){
					if(nextTaskValue[0].label === nodes[i].label){
						$(this).parent().next().attr("onclick","showNextAnswer(\""+nodes[i].label+"\")");
					}
				}
			}
		}else {
			$(this).parent().next().attr("onclick","showNextAnswer(\"종료\")");
		}
	});
	});
}

function showNextAnswer(nextTask){
	if(scenario != null){
		var nodes = scenario.nodes;
		
		$(".next_task_answer").empty();
		var answerHtml = "";
		if(nextTask != "종료"){
			for (var i = 0; i < nodes.length; i++) {
				if(nodes[i].label == nextTask){
					$(".next_task_answer").css('display','block');
					answerHtml += "<span>"+nodes[i].label+"</span>";
					answerHtml += "<span>:</span>";
					answerHtml += "<span>"+nodes[i].attr[0].utter+"</span>";
				}
			}
		}else{
			$(".next_task_answer").css('display','block');
			answerHtml += "<span>"+nextTask+"</span>";
			answerHtml += "<span>:</span>";
			answerHtml += "<span></span>";
		}
		$(".next_task_answer").append(answerHtml);
	}
}

// Task 관계 추가
function addTaskRelation() {
	if(scenario != null){
		var idx = $("#nextTaskTable tr").length + 1;
		
		var addTaskRelationHtml = "";
		addTaskRelationHtml += '<tr>';
		addTaskRelationHtml += '<td><input type="checkbox" id="checkboxAdd_' + idx + '" class="checkbox" name="checkDelete[]">';
		addTaskRelationHtml += '<label for="checkboxAdd_' + idx + '"></label></td>';
		addTaskRelationHtml += '<td><input type="text" id="test_nextIntent_' + idx + '" class="select" readOnly></td>';
		addTaskRelationHtml += '<td><input type="text" id="test_nextTask_'+ idx + '" class="select" style="width:180px;" readOnly>';
		addTaskRelationHtml += '<div class="help" onclick="showNextAnswer(\'\')">A<div class="help_desc help_next_task_answer"><i>클릭 시 다음 테스크 답변이 테이블 상단에 보여집니다.</i></div></div>';
		addTaskRelationHtml += '</td>';
		addTaskRelationHtml += '<td><input type="text" class="ipt_txt" id="taskInfoAdd_' + idx + '" value=\"\"></td>';
		addTaskRelationHtml += '<td><input type="text" class="ipt_txt" id="taskAnswerAdd_' + idx + '" value=\"\"></td>';
		addTaskRelationHtml += '</tr>';
		
		$("#nextTaskTable tbody").append(addTaskRelationHtml);
		var nextTaskId = "test_nextTask_" + idx;
		var nextIntentId = "test_nextIntent_" + idx;
		
		for (var i = 0; i < testTaskNextData.length; i++) {
			testTaskNextData[i].value = "test_nextTask_"+ idx + "_" + testTaskNextData[i].label;
		}
		
		for (var j = 0; j < testIntentData.length; j++) {
			testIntentData[j].value = "test_nextIntent_" + idx + "_" + testIntentData[j].label;
		}
		window['var selectTaskNext_' + idx];
		eval('selectTaskNext_' + idx + '= createSearchSelect($("#"+nextTaskId))');
		eval("selectTaskNext_" + idx + ".setOptions(testTaskNextData)");
		
		window['var selectIntent_' + idx];
		eval('selectIntent_' + idx + '= createSearchSelect($("#"+nextIntentId))');
		eval("selectIntent_" + idx + ".setOptions(testIntentData)");
		
		eval("selectIntent_" + idx + ".options.find('input[data-label=\"always\"]').attr(\"disabled\",\"\")");
		// nextTask 변경시 nextTask 답변 변경
		$("input[id*='test_nextTask_']").on('change', function(){
			var nodes = scenario.nodes;
			var idx = $(this).attr('id').split('_')[2];
			var nextTaskValue = eval("selectTaskNext_" + idx + ".getValue()");
			if(nextTaskValue != ''){
				if(nextTaskValue[0].label != "종료"){
					for(var i = 0; i < nodes.length; i++){
						if(nodes[i].attr[0] != null){
							if(nextTaskValue[0].label === nodes[i].label){
								$(this).parent().next().attr("onclick","showNextAnswer(\""+nodes[i].label+"\")");
							}
						}
					}
				}else {
					$(this).parent().next().attr("onclick","showNextAnswer(\"종료\")");
				}
			}
		});
	}
}

function deleteTaskRelation() {
	$("input[name='checkDelete[]']").each(function (i,v) {
	    if($(this).is(":checked") == true){
	        $(this).parent().parent().remove();
	    }
	});
}

//Task 저장
function updateTask(){
	if ( !$('#taskName').val() ) {
		mui.alert('태스크 명을 입력해주세요.');
		return;
	}

	mui.confirm('테스크 정보를 저장하시겠습니까?', {
		onClose: function(isOk) {
			if (isOk) {
				if(scenario != null){
					var nodes = scenario.nodes;
					var edges = scenario.edges;
					var campaignInfoObj = new Object();
					var depth1_value = selectTaskGroup01 != null ? selectTaskGroup01.getValue() : "";
					var depth2_value = selectTaskGroup02 != null ? selectTaskGroup02.getValue() : "";
					var depth3_value = selectTaskGroup03 != null ? selectTaskGroup03.getValue() : "";
					
					var taskGroupDepth1 = "";
					var taskGroupDepth2 = "";
					var taskGroupDepth3 = "";
					
					if(taskNode != null && taskNode != ''){

						campaignInfoObj.updateCheck = "update";
						campaignInfoObj.oldTaskName = taskNode.label;
						campaignInfoObj.newTaskName = $("#taskName").val();
						if(taskNode.label.trim() == "*"){
							campaignInfoObj.successYn = "";
						}else{
							campaignInfoObj.successYn = $('input[name="checkSuccessYn"]:checked').val();
						}

						var taskLabel = taskNode.label;
						for(var i = 0; i < nodes.length; i++){
							// task명 중복 체크
							if(nodes[i].label == $("#taskName").val() && taskLabel != $("#taskName").val()){
								mui.alert("이미 등록된 테스크 명입니다.");
								return false;
							}
							// task명 및 task답변 수정
							if(nodes[i].label == taskLabel){
								nodes[i].label = $("#taskName").val();
								nodes[i].attr[0].utter = $("#taskAnswer").val();
								if(taskNode.label.trim() == "*"){
									nodes[i].successYn = "";
								}else{
									nodes[i].successYn = $('input[name="checkSuccessYn"]:checked').val();
								}

								// task group 수정
								if(depth1_value != '' && depth1_value[0].value != 'depth1_default'){
									taskGroupDepth1 = depth1_value[0].label;
								}
								if(depth2_value != '' && depth2_value[0].value != 'depth2_default'){
									taskGroupDepth2 = "_" + depth2_value[0].label;
								}
								if(depth3_value != '' && depth3_value[0].value != 'depth3_default'){
									taskGroupDepth3 = "_" + depth3_value[0].label;
								}
								nodes[i].taskGroup = taskGroupDepth1 + taskGroupDepth2 + taskGroupDepth3;
								
								//해당 Task의 intentList 삭제
								if(nodes[i].attr[0] != null){
									nodes[i].attr[0].intentList = [];
								}
							}
						}
						//해당 task edge List 삭제
						for (var i = 0; i < edges.length; i++) {
							if(taskNode.id == edges[i].source){
								delete edges[i];
							}
						}
						scenario.edges = scenario.edges.filter(function(element){
							return element !== null;
						});
						
						var taskObj = new Object();
						var taskAttrList = [];
						var attrObj = new Object();
						
						var edgeArr = [];
						var edgeSourceArr = [];
						
						var endTaskCnt = 1;
						var endTaskId = "";
						
						for (var i = 0; i < $("#nextTaskTable tr").length; i++) {
							var nextTask = eval("selectTaskNext_" + (i+1) +".getValue()[0].label");
							var nextIntent = eval("selectIntent_" + (i+1) +".getValue()[0].label");
							if(nextIntent == 'always'){
								nextIntent = "";
							}
							var nextInfo = eval("$('#taskInfoAdd_"+ (i+1) +"').val()");
							var nextAnswer = eval("$('#taskAnswerAdd_"+ (i+1) +"').val()");
							for(var j = 0; j < nodes.length; j++){
								if(nodes[j].label == taskNode.label){
									//nodes 관련
									var intentObj = new Object();
									intentObj.answer = nextAnswer;
									intentObj.intent = nextIntent;
									intentObj.nextTask = nextTask;
									intentObj.info = nextInfo;
									
									nodes[j].attr[0].intentList.push(intentObj);
									//edges 관련
									var edgeId = uuidv4();
									var edgeObj = new Object();
									var edgeDataObj = new Object();
									
									var edgeSourceObj = new Object();
									
									if(nextTask != '종료'){
										edgeSourceObj.source = nodes[j].id;
										edgeSourceObj.label = nextIntent;
										
										edgeDataObj.attr = [];
										edgeDataObj.id = edgeId;
										edgeDataObj.label = nextIntent;
										edgeDataObj.type = "default";
										
										edgeObj.source = taskNode.id;
										
										for (var k = 0; k < nodes.length; k++) {
											if(nodes[k].label == nextTask){
												edgeObj.target = nodes[k].id;
												edgeSourceObj.target = nodes[k].id;
											} 
											//task명이 바꼈을 시 nextTask명도 변경 (자신의 Task 일때 기존 Task 명으로 target 생성)
											if(taskLabel == nextTask){
												edgeObj.target = taskNode.id;
												edgeSourceObj.target = taskNode.id;
											}
										}
										
										edgeObj.data = edgeDataObj;
										
									}else{
										
										if(endTaskCnt == 1){
											endTaskId = uuidv4();
											var endTaskObj = new Object();
											
											endTaskObj.id = endTaskId;
											endTaskObj.type = "end";
											endTaskObj.label = "종료";
											endTaskObj.left = 0;
											endTaskObj.top = 0;
											endTaskObj.attr = [];
											scenario.nodes.push(endTaskObj);
											endTaskCnt++;
										}
										
										edgeSourceObj.source = taskNode.id;
										edgeSourceObj.label = nextIntent;
										
										edgeDataObj.attr = [];
										edgeDataObj.id = edgeId;
										edgeDataObj.label = nextIntent;
										edgeDataObj.type = "default";
										
										edgeObj.source = taskNode.id;
										edgeObj.target = endTaskId;
										edgeSourceObj.target = endTaskId;
										edgeObj.data = edgeDataObj;
									}
									
									edgeArr.push(edgeObj);
									edgeSourceArr.push(edgeSourceObj);
								}
							}
						}
						
						// edge 같은 source label 합치기
						for(var i = 0; i < edgeSourceArr.length; i++){
							var edgeLabel = "";
							for(var j = 0; j < edgeSourceArr.length; j++){
								if(edgeSourceArr[i].source == edgeSourceArr[j].source){
									if(edgeSourceArr[i].target == edgeSourceArr[j].target){
										edgeLabel += edgeSourceArr[j].label + "/";
									}
								}         
							}
							edgeArr[i].data.label = edgeLabel.slice(0,-1);
						}
						for (var i = 0; i < edgeArr.length; i++) {
							scenario.edges.push(edgeArr[i]);
						}
						
						
						for(var i = 0; i < scenario.nodes.length; i++){
							if(scenario.nodes[i].type == 'end'){
								var unUseTask = scenario.edges.filter(element => element.target === scenario.nodes[i].id);
								if(unUseTask.length == 0){
									delete scenario.nodes[i];
								}
							}
						}
						scenario.nodes = scenario.nodes.filter(function(element){
							return element !== null;
						});
						
						//task명이 바꼈을 시 nextTask명도 변경
						if(taskLabel != $("#taskName").val()){
							for(var i = 0; i < scenario.nodes.length; i++){
								if(scenario.nodes[i].attr[0] != null){
									var intentList = scenario.nodes[i].attr[0].intentList;
									for(var j = 0; j < intentList.length; j++){
										if(intentList[j].nextTask == taskLabel){
											intentList[j].nextTask = $("#taskName").val();
										}
									}
								}
							}
						}
						
					}else {
						campaignInfoObj.updateCheck = "add";
						campaignInfoObj.oldTaskName = "";
						campaignInfoObj.newTaskName = "";
						campaignInfoObj.successYn = "";

						var taskObj = new Object();
						var taskAttrList = [];
						var attrObj = new Object();
						var intentList = [];
						
						var id = uuidv4();
						
						var edgeArr = [];
						var edgeSourceArr = [];
						
						var taskName = $("#taskName").val();
						
						// task명 중복 체크
						for (var i = 0; i < nodes.length; i++) {
							if(nodes[i].label == taskName){
								mui.alert("이미 등록된 테스크 명입니다.");
								return false;
							}
						}
						
						var taskAnswer = $("#taskAnswer").val();
						var checkSuccessYn = "";
						if($("#taskName").val().trim() != "*") {
							if ($('input[name="checkSuccessYn"]:checked').val() == null) {
								mui.alert("성공 실패 여부를 체크해주세요.");
								return false;
							}
							checkSuccessYn = $('input[name="checkSuccessYn"]:checked').val();
						}

						if(depth1_value != '' && depth1_value[0].value != 'depth1_default'){
							taskGroupDepth1 = depth1_value[0].label;
						}
						if(depth2_value != '' && depth2_value[0].value != 'depth2_default'){
							taskGroupDepth2 = "_" + depth2_value[0].label;
						}
						if(depth3_value != '' && depth3_value[0].value != 'depth3_default'){
							taskGroupDepth3 = "_" + depth3_value[0].label;
						}
						var taskGroup = taskGroupDepth1 + taskGroupDepth2 + taskGroupDepth3;
						
						var endTaskCnt = 1;
						var endTaskId = "";
						if($("input[id*='nextIntent']").val() != '' || $("input[id*='nextTask']").val() != ''){
						for (var i = 0; i < $("#nextTaskTable tr").length; i++) {
							
							var nextTask = eval("selectTaskNext_" + (i+1) +".getValue()[0].label");
							var nextIntent = eval("selectIntent_" + (i+1) +".getValue()[0].label");
							if(nextIntent == 'always'){
								nextIntent = "";
							}
							var nextInfo = eval("$('#taskInfoAdd_"+ (i+1) +"').val()");
							var nextAnswer = eval("$('#taskAnswerAdd_"+ (i+1) +"').val()");
							
							var intentObj = new Object();
							intentObj.answer = nextAnswer;
							intentObj.intent = nextIntent;
							intentObj.nextTask = nextTask;
							intentObj.info = nextInfo;
							intentList.push(intentObj);
							
							//edges 관련
							var edgeId = uuidv4();
							var edgeObj = new Object();
							var edgeDataObj = new Object();
							
							var edgeSourceObj = new Object();
							
							
							if(nextTask != '종료'){
								edgeSourceObj.source = id;
								edgeSourceObj.label = nextIntent;
								
								edgeDataObj.attr = [];
								edgeDataObj.id = edgeId;
								edgeDataObj.label = nextIntent;
								edgeDataObj.type = "default";
								
								edgeObj.source = id;
								
								for (var k = 0; k < nodes.length; k++) {
									if(nodes[k].label == nextTask){
										edgeObj.target = nodes[k].id;
										edgeSourceObj.target = nodes[k].id;
									}
								}
								edgeObj.data = edgeDataObj;
							} else{
								
								if(endTaskCnt == 1){
									endTaskId = uuidv4();
									var endTaskObj = new Object();
									
									endTaskObj.id = endTaskId;
									endTaskObj.type = "end";
									endTaskObj.label = "종료";
									endTaskObj.left = 0;
									endTaskObj.top = 0;
									endTaskObj.attr = [];
									scenario.nodes.push(endTaskObj);
									endTaskCnt++;
								}
								
								edgeSourceObj.source = id;
								edgeSourceObj.label = nextIntent;
								
								edgeDataObj.attr = [];
								edgeDataObj.id = edgeId;
								edgeDataObj.label = nextIntent;
								edgeDataObj.type = "default";
								
								edgeObj.source = id;
								edgeObj.target = endTaskId;
								edgeSourceObj.target = endTaskId;
								edgeObj.data = edgeDataObj;
								
							}
							edgeArr.push(edgeObj);
							edgeSourceArr.push(edgeSourceObj);
							
						}
						
						// edge 같은 source label 합치기
						for(var i = 0; i < edgeSourceArr.length; i++){
							var edgeLabel = "";
							for(var j = 0; j < edgeSourceArr.length; j++){
								if(edgeSourceArr[i].source == edgeSourceArr[j].source){
									if(edgeSourceArr[i].target == edgeSourceArr[j].target){
										edgeLabel += edgeSourceArr[j].label + "/";
									}
								}         
							}
							edgeArr[i].data.label = edgeLabel.slice(0,-1);
						}
						for (var i = 0; i < edgeArr.length; i++) {
							edges.push(edgeArr[i]);
						}
						
						attrObj.utter = taskAnswer;
						attrObj.inputType = "0";
						attrObj.maxTurn = "0";
						attrObj.taskOverMax = "";
						attrObj.intentList = intentList;
						attrObj.version = "2.0";
						taskAttrList.push(attrObj);
						
						taskObj.id = id;
						taskObj.type = "task";
						taskObj.label = taskName;
						taskObj.successYn = checkSuccessYn;
						taskObj.left = 0;
						taskObj.top = 0;
						taskObj.taskGroup = taskGroup;
						taskObj.attr = taskAttrList;
						
						nodes.push(taskObj);
						}else {
							var nodesObj = new Object();
							
							var startNodeId = uuidv4();
							nodesObj.id = startNodeId;
							nodesObj.type = "task";
							nodesObj.label = $("#taskName").val();
							nodesObj.successYn = checkSuccessYn;
							nodesObj.left = 0;
							nodesObj.top = 0;
							
							var attrArr = new Array();
							var attrObj = new Object();
							
							attrObj.inputType = "0";
							attrObj.intentList = [];
							attrObj.maxTurn = "0";
							attrObj.taskOverMax = "";
							attrObj.utter = $("#taskAnswer").val();
							attrObj.version = "2.0";
							attrArr.push(attrObj);
							
							nodesObj.attr = attrArr;
							nodesObj.taskGroup = taskGroup;
							
							scenario.nodes.push(nodesObj);
							
						}
					}
					
				}else {
					var campaignInfoObj = new Object();
					campaignInfoObj.updateCheck = "add";
					campaignInfoObj.oldTaskName = "";
					campaignInfoObj.newTaskName = "";
					campaignInfoObj.successYn = "";
					scenario = {};

					if($('input[name="checkSuccessYn"]:checked').val() == null){
						mui.alert("성공 실패 여부를 체크해주세요.");
						return false;
					}
					
					var nodesArr = new Array();
					var nodesObj = new Object();
					
					var startNodeId = uuidv4();
					nodesObj.id = startNodeId;
					nodesObj.type = "start";
					nodesObj.label = $("#taskName").val();
					if($("#taskName").val().trim() == "*"){
						nodesObj.successYn = "";
					}else{
						nodesObj.successYn = $('input[name="checkSuccessYn"]:checked').val();
					}
					nodesObj.left = 0;
					nodesObj.top = 0;
					
					var attrArr = new Array();
					var attrObj = new Object();
					
					attrObj.inputType = "0";
					attrObj.intentList = [];
					attrObj.maxTurn = "0";
					attrObj.taskOverMax = "";
					attrObj.utter = $("#taskAnswer").val();
					attrObj.version = "2.0";
					attrArr.push(attrObj);
					
					nodesObj.attr = attrArr;
					nodesObj.taskGroup = "";
					
					nodesArr.push(nodesObj);
					
					var endNodeObj = new Object();
					var endNodeId = uuidv4();
					endNodeObj.id = endNodeId;
					endNodeObj.type = "end";
					endNodeObj.label = "종료";
					endNodeObj.left = 0;
					endNodeObj.top = 0;
					endNodeObj.attr = [];
					
					nodesArr.push(endNodeObj);
					
					scenario.nodes = nodesArr;
					scenario.edges = [];
				}
	
				//'저장중' 효과를 백그라운드로 적용
				$("#saveBtn").text("저장중").addClass("gradient");
				$('#vb_wrap').addClass('loading');
				$.ajax({
					url: "voiceBot/saveScenario",
					data: JSON.stringify({
						simplebotId: data.simplebotId,
						userId: data.userId,
						companyId: data.companyId,
						scenarioJson: JSON.stringify(scenario),
						isExcelUpload: 'N',
						campaignInfoObj: JSON.stringify(campaignInfoObj)
					}),
					type: "POST",
					contentType: 'application/json'
				}).then(function (response) {
					mui.alert('태스크가 저장되었습니다.');
					$("#saveBtn").text("저장").removeClass("gradient");
					$('#vb_wrap').removeClass('loading');

					saveCheck = false;
					changeContents(menuPrev);
				}).catch(function (response) {
					$('#vb_wrap').removeClass('loading');
					mui.alert('태스크 저장을 실패하였습니다.<br>관리자에게 문의해주세요.');
					console.log('apply() status code: ' + response.status);
				});
			}
		}
	});
}
// UUID 생성
function uuidv4() {
	  return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
	    (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
	  )
}
