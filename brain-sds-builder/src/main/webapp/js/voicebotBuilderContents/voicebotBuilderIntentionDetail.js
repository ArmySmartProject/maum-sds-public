var intentTaskTable = null;
var intentNqaTable = null;
// Task연결 수정 여부
var editTaskRel = false;

var editRegex = false;
var saveCheckRegex = false;
var intentRegexData = {}; //regex data 'no', 'values', 'types', 'regexResult'
var regexTable = null; //정규식 테이블
var regexTableData = []; //정규식 테이블 데이터
var regexSortableList = null; //정규식 드래그앤드랍 리스트
var updateRegexData = []
var deleteTempRegexData = []; //삭제될 list
var deleteRegexData = []; //삭제될 list

var intentTaskList2 = [];
var nextTaskList2 = [];

$(document).ready(function(){
  var anSelect = $('body').find('.an-search_option');
  anSelect.not(anSelect[0]).remove(); //이전에 생성한 an-select 초기화

	handleDlDropdown();

	if(intentData.No != null){
		$('.dl_dropdown dd').addClass('loading'); //로딩 UI는 각각 통신 완료 시 없어짐

    $('input[name="intent_name"]').val(intentData.bertIntentName);
    $('input[name="intent_name"]').attr('readonly',true);

		getRegexList();

    getNqaStudySentence();
    getNqaCount();
		$('#beforeAddIntentInfo').hide();
		$('#afterAddIntentInfo').show();
		afterAddIntentNqaUI();

	} else {
		$('#beforeAddIntentInfo').show();
		$('#afterAddIntentInfo').hide();
		beforeAddIntentNqaUI();
	}

	getIntentTaskDetail("No");

  // task 연결 리스트 체크박스 전체 선택 / 해제
  $("#list_all").on('click', function(){
	  $("#intent_seleted_task").text('');
	    var checkYn = $("#list_all").is(":checked");
	    var selectTaskStr = "";
	    var idx = 0;

	    if(checkYn){
	    	intentTaskTable.$(".checkbox",{"page":"all"}).each(function(){
	        	$(this).parent().next().next().next().find('input').prop('disabled',false);

	        	var checkedTaskTxt = $(this).parent().next().find('a').text();

	        	if(idx == 0){
	                selectTaskStr = checkedTaskTxt;
	                idx++;
	            }else {
	                selectTaskStr = selectTaskStr + ", " + checkedTaskTxt;
	                idx++;
	            }

	        	$(this).prop('checked',true);
		    });
	    }else{
	    	intentTaskTable.$(".checkbox",{"page":"all"}).each(function(){
	        	$(this).parent().next().next().next().find('input').prop('disabled',true);
	        	$(this).prop('checked',false);
		    });
	    }
	    $("#intent_seleted_task").text(selectTaskStr);
	    editTaskRel = true;
	});


	//정규식 drag 리스트
	regexSortableList = new Sortable(regex_list,
		{
			sort : true,
			animation : 100,
			ghostClass: 'blue-background-class',
			onEnd: function() { // =onUpdate
				if ( regexSortableList.option('sort') ) makeRegexRuleResult();
			},
		}
	)
});

function handleDlDropdown() {
	var dropBox = $('.dl_dropdown');
	var type = intentData.dropdown; //name, regex, nqa

	dropBox.removeClass('active');
	if ( type ) {
		var thisType = $('.dl_dropdown.'+type);
		thisType.addClass('active');
	}

	dropBox.find('dt').on('click', function(){
    var $dl = $(this).parent();
    $dl.toggleClass('active');
  });
}

function saveCheckIntentDetail() {
	if ( editTaskRel || saveCheckRegex ) saveCheck = true;
}

function saveIntent() {
  if ( !$('input[name="intent_name"]').val() ) {
		mui.alert('의도 이름을 작성해주세요.');
		return;
	}
  intentData.bertIntentName = $('input[name="intent_name"]').val();

  if ( intentData.bertIntentName && !intentData.no  ) saveCheck = true;

	if ( !saveCheck ) {
  	mui.alert('수정 혹은 저장할 내용이 없습니다.<br>이전페이지 버튼을 눌러 이동해주세요.');
  	return;
	}

	//신규저장 (시나리오 제이슨 저장 여부에 따라 다름)
	if ( saveCheck && !intentData.No) {
		$('#vb_wrap').addClass('loading');

		saveIntentTaskScenario().then(function(result) { //시나리오 제이슨 저장 (의도 insert 를 함께 함)
			if ( !result.isSuccess ) return;
			var intentName = intentData.bertIntentName;

			if ( result.skip ) { //result 시나리오 제이슨 저장 안함
				insertIntent().then(function(result) { //의도 insert
					if ( !result.isSuccess || result.dup ) return;

					var intentNo = result.intentNo;
					insertAndUpdateRegexList(intentNo, intentName).then(function(result) {
						if ( !result.isSuccess ) return;

						deleteRegexList().then(function(result) {
							if ( !result.isSuccess ) return;

							mui.alert(intentName + ' 의도가 저장 되었습니다.');
							$('#vb_wrap').removeClass('loading');

							saveCheck = false;
							intentTableData = [];
							changeContents(PAGES.INTENTION);
						});
					});
				});

			} else { //result 시나리오 제이슨 저장함
				getIntentNo(intentName).then(function(result){
					if ( !result.isSuccess ) return;

					var intentNo = result.intentNo;
					insertAndUpdateRegexList(intentNo, intentName).then(function(result) {
						if ( !result.isSuccess ) return;

						deleteRegexList().then(function(result) {
							if ( !result.isSuccess ) return;

							mui.alert(intentName + ' 의도가 저장 되었습니다.');
							$('#vb_wrap').removeClass('loading');

							saveCheck = false;
							intentTableData = [];
							changeContents(PAGES.INTENTION);
						});
					});
				});
			}
			
		});
	} else { //수정저장
		$('#vb_wrap').addClass('loading');

		saveIntentTaskScenario().then(function(result) {
			if (!result.isSuccess) return;

			insertAndUpdateRegexList().then(function (result) {
				if (!result.isSuccess) return;

				deleteRegexList().then(function (result) {
					if (!result.isSuccess) return;

					mui.alert(intentData.bertIntentName + ' 의도가 수정 되었습니다.');
					$('#vb_wrap').removeClass('loading');

					saveCheck = false;
					intentTableData = [];
					changeContents(PAGES.INTENTION);
				});
			});
		});
	}

	function insertIntent() {
		var obj = {};
		obj.bertIntent = intentData.bertIntentName;
		obj.host = intentData.host;
		obj.lang = intentData.lang;

		return $.ajax({
			url : "voiceBot/insertIntention",
			data : JSON.stringify(obj),
			type: "POST",
			contentType: 'application/json'
		}).then(function(result) {
			if (result.result == 'success') {
				console.log('[addIntent] success');
				//no 넘기기
				return { isSuccess: true, intentNo: result.intentNo };

			} else {
				mui.alert("중복된 의도명 입니다.<br>다른 이름으로 수정 후 저장해주세요.");
				$('#vb_wrap').removeClass('loading');
				return { isSuccess: false, dup: true };

			}
		}).catch(function() {
			mui.alert("의도추가에 실패했습니다.");
			$('#vb_wrap').removeClass('loading');
			return { isSuccess: false };

		});
	}

  function getIntentNo(name) {
		return $.ajax({
			url : "voiceBot/getIntentNo",
			data : JSON.stringify({
				host : intentData.host,
				bertIntentName : name,
			}),
			type: "POST",
			contentType: 'application/json'
		}).then(function(res) {
			// intentData.No = res;

			return { isSuccess: true, intentNo: res };

		}).catch(function() {
			mui.alert('의도를 매핑하는데 실패하였습니다.<br>관리자에게 문의해주세요.');
			console.log('[saveIntent getIntentNo error]');
			$('#vb_wrap').removeClass('loading');
			return { isSuccess: false };
		});
	}

	function saveIntentTaskScenario() {
	
	//TASK 연결 탭 선택안함 체크	
	var saveIntentTaskRelBoolean = saveIntentTaskRel();
		
	if (saveIntentTaskRelBoolean === false) {
		$('#vb_wrap').removeClass('loading');
		return Promise.resolve({ isSuccess: false});
	}
	
	if ( !editTaskRel ) return Promise.resolve({ isSuccess: true, skip: true });
	
	return $.ajax({
		url: "voiceBot/saveScenario",
		data: JSON.stringify({
			simplebotId: data.simplebotId,
			userId: data.userId,
			companyId: data.companyId,
			scenarioJson: JSON.stringify(scenario),
			isExcelUpload: 'N',
		}),
		type: "POST",
		contentType: 'application/json'
	}).then(function (response) {
		console.log('[saveIntentTaskScenario] success');
		return { isSuccess: true }

	}).catch(function (response) {
		mui.alert('시나리오 제이슨을 저장할 수 없습니다.<br>관리자에게 문의해주세요.');
		console.log('apply() status code: ' + response.status);
		$('#vb_wrap').removeClass('loading');
		return { isSuccess: false }
	});
	}
}

/* TASK 연결 start */
function getIntentTaskDetail(resetYn){
	if(intentData.No != null){
		var obj = new Object();

		obj.host = data.host;
		obj.bertIntentNo = intentData.No;

		$.ajax({
		    url: "voiceBot/getIntentTaskDetail",
		    data: JSON.stringify(obj),
		    type: "POST",
		    contentType: 'application/json'

		  }).then(function (res) {
			  var nodes = scenario.nodes;
			  var getIntentTaskList = res;
			  
			  var intentTaskList = [];
			  var nextTaskList = [];
			  
			  $("#intent_seleted_task").text("");

			  var selectTaskStr = "";

			  // 선택된 태스크 txt 가져오기
			  for (var i = 0; i < getIntentTaskList.length; i++) {
				if(i == 0){
					selectTaskStr = getIntentTaskList[i].Name;
				}else {
					selectTaskStr = selectTaskStr + ", " + getIntentTaskList[i].Name;
				}
			  }

			  $("#intent_seleted_task").text(selectTaskStr);


			  for (var i = 0; i < nodes.length; i++) {
				  if(nodes[i].type != "end" && nodes[i].label.trim() != "*"){
					  var intentTaskObj = new Object();

					  intentTaskObj.id = nodes[i].id;
					  intentTaskObj.Name = nodes[i].label;
					  intentTaskObj.nextTask = "default";
					  intentTaskObj.BertIntentName = intentData.bertIntentName;
					  
					  for (var j = 0; j < getIntentTaskList.length; j++) {
						  
						  if(getIntentTaskList[j].Name == nodes[i].label){
							  intentTaskObj.nextTask = getIntentTaskList[j].nextTask;
						  }
					  }
					  intentTaskList.push(intentTaskObj);

				  }
				  if(nodes[i].label.trim() != "*"){
					  var nextTaskObj = new Object();
					  nextTaskObj.label = nodes[i].label;
					  nextTaskObj.value = nodes[i].label;

					  nextTaskList.push(nextTaskObj);
				  }
			  }
			  
			  intentTaskList2 = intentTaskList;
			  nextTaskList2 = nextTaskList;
			  if(resetYn == "No"){
				  intentTaskDataTable(intentTaskList);
			  }else {
				  intentTaskTable.clear();
				  intentTaskTable.rows.add(intentTaskList).draw();

					if ( intentTaskList.length < 6 ) {
						$('#list_intent_task .dataTables_paginate').hide();

					} else if ( intentTaskList.length > 5 ) {
						$('#list_intent_task .dataTables_paginate').show();
					}
			  }
		  }).catch(function () {
		    console.log('[getIntentTaskDetail error]');
		  });
	}else {
		var nodes = scenario.nodes;

		var intentTaskList = [];
		var nextTaskList = [];

		$("#intent_seleted_task").text("");

		for (var i = 0; i < nodes.length; i++) {
			if(nodes[i].type != "end" && nodes[i].label.trim() != "*"){
				var intentTaskObj = new Object();

				intentTaskObj.id = nodes[i].id;
				intentTaskObj.Name = nodes[i].label;
				intentTaskObj.nextTask = "default";
				intentTaskObj.BertIntentName = "";
				intentTaskList.push(intentTaskObj);
		    }
			if(nodes[i].label.trim() != "*"){
				var nextTaskObj = new Object();
				nextTaskObj.label = nodes[i].label;
				nextTaskObj.value = nodes[i].label;

				nextTaskList.push(nextTaskObj);
			}
	  }
		intentTaskList2 = intentTaskList;
		nextTaskList2 = nextTaskList;
		
		intentTaskDataTable(intentTaskList);
//		setIntentNextTask(nextTaskList, intentTaskList);
	}
}

function intentTaskDataTable(intentTaskList){
	intentTaskTable = $("#list_intent_task table").DataTable({
		"language": {
		      "emptyTable": "등록된 데이터가 없습니다.",
		      "lengthMenu": "페이지당 _MENU_ 개씩 보기",
		      "info": "현재 _START_ - _END_ / _TOTAL_건",
		      "infoEmpty": "데이터 없음",
		      "infoFiltered": "( _MAX_건의 데이터에서 필터링됨 )",
		      "search": "",
		      "zeroRecords": "일치하는 데이터가 없습니다.",
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
			bDestroy: true,
		    lengthChange : false,
		    fixedColumns: true,
		    autoWidth: false, //css width
		    searching: true, //검색
		    ordering : false, //정렬
		    paging: true, // paging 디폴트 : row 10개, 페이징 5개
		    pageLength: 5,
		    pagingType : "full_numbers_no_ellipses",
		    data: intentTaskList, //참조 data
		    columns: [
		      {
		        data: "checkboxes",
		        searchable: false,
		        render: function(data,type,row,meta){
		        	if(row.nextTask != 'default'){
		        		return "<input type='checkbox' name='select_task[]' class='checkbox' id='list0"+meta.row+"' checked> <label for='list0"+meta.row+"'></label>"
		        	}else{
		        		return "<input type='checkbox' name='select_task[]' class='checkbox' id='list0"+meta.row+"'> <label for='list0"+meta.row+"'></label>"
		        	}
		        }
		      },
		      {
		        data: "Name",
		        name: 'Name',
		        searchable: true,
		        render: function(data,type,row,meta){
		        	return "<a onclick='getTaskNode(\""+row.id+"\"); changeContents(PAGES.TASK_DETAIL);'>"+row.Name+"</a>";
		        }
		      },
		      {
		    	  data: "BertIntentName",
		    	  name: 'BertIntentName',
		    	  searchable: false,
		    	  render: function(data,type,row,meta){
		    		  if(intentData.No != null){
		    			  return "<p>"+row.BertIntentName+"</p>";
		    		  }else{
		    			  return "<p class='BertIntentName'></p>";
		    		  }
		    	  }
		      },
		      {
		        data: "nextTask",
		        name: 'nextTask',
		        searchable: false,
		        render: function(data, type, row, meta){
		        	return '<input type="text" id="intent_next_task_'+meta.row+'" class="select select_next_task" readOnly>';
		        }
		      },
		    ],

		"initComplete": function() {
			$('.dl_dropdown.name dd').removeClass('loading');

			if ( intentTaskList.length < 6 ) {
				$('#list_intent_task .dataTables_paginate').hide();
				
			} else if ( intentTaskList.length > 5 ) {
				$('#list_intent_task .dataTables_paginate').show();
			}
		},
		"drawCallback": function(settings){
			test();
		},
	});
	intentTaskTable.draw();

	// intent task dataTable 검색
	$("input[name='intent_task_search']").keyup(function(){
	  intentTaskTable.search($(this).val()).draw();
	});

	intentTaskTable.$(".checkbox",{"page":"all"}).on('click',function(){
	    $("#intent_seleted_task").text('');
	    var selectTaskStr = "";
	    var idx = 0;
	    intentTaskTable.$(".checkbox",{"page":"all"}).each(function(){
	        if($(this).is(":checked") == true){
	        	$(this).parent().next().next().next().find('input').prop('disabled',false);
	            var checkedTaskTxt = $(this).parent().next().find('a').text();
	            if(idx == 0){
	                selectTaskStr = checkedTaskTxt;
	                idx++;
	            }else {
	                selectTaskStr = selectTaskStr + ", " + checkedTaskTxt;
	                idx++;
	            }

	            // 체크박스 모두 체크시 전체선택 체크
	           if(intentTaskTable.$(".checkbox",{"page":"all"}).length == idx){
	        	   $("#list_all").prop('checked',true);
	           }
	        }else{
	        	$("#list_all").prop('checked',false);
	        	$(this).parent().next().next().next().find('input').prop('disabled',true);
	        }
	    });
	    $("#intent_seleted_task").text(selectTaskStr);
	    editTaskRel = true;
	});

	$("input[name='intent_name']").on("keyup",function() {
		var value = $(this).val();
		$(".BertIntentName").text(value);
		intentData.bertIntentName = value;
	});
}
function test(){
	if(intentTaskTable != null){
		$('body').find('.an-search_option').each(function(){
		    if($(this).attr('style') == null){
		        $(this).remove();
		    }
		});
		
		nextTaskList2 = nextTaskList2.filter((element, index) => {
			return nextTaskList2.findIndex((element2 , j) => {
	            return element.label === element2.label && element.label != null && element.label != '';
	        }) === index;
		});
		
		for (var i = intentTaskTable.page.info().start; i < intentTaskTable.page.info().end; i++) {
			for(var i = 0; i < intentTaskList2.length; i++) {
			    var newNextList = [{"label":"선택안함","value":"default_"+i+""}];
			    for(var j = 0; j < nextTaskList2.length; j++){
		            var newNextObj = new Object();
		            newNextObj.label = nextTaskList2[j].label;
		            newNextObj.value = nextTaskList2[j].value +"_"+ i;
		            newNextList.push(newNextObj);
			    }
			    var intentNextTask = 'intent_next_task_' + i;

			    var intentSetValue = intentTaskList2[i].nextTask +"_"+ i;
			    
			    if($("#root_intent_next_task_"+i).attr('id') == null){
			    	window['var intent_next_task_' + i];
			    	eval('intent_next_task_' + i + '= createSearchSelect($("#"+intentNextTask))');
			    	eval("intent_next_task_" + i + ".setOptions(newNextList)");
			    	eval("intent_next_task_" + i + ".setValue([intentSetValue])");
			    }else {
			    	eval("intent_next_task_" + i + ".setValue([intentSetValue])");
			    }
				
				if(intentTaskList2[i].nextTask == 'default'){
					$("#"+intentNextTask).prop('disabled', true);
				}
			}
		}
		
		intentTaskTable.$(".checkbox",{"page":"all"}).each(function(){
			if($(this).is(":checked") == true){
				$(this).parent().parent().find('.select_next_task').attr("disabled",false);
			}else {
				$(this).parent().parent().find('.select_next_task').attr("disabled",true);
			}
		});
		
		$("input[id*='intent_next_task']").on("change",function(){
			editTaskRel = true;
		});
	}
}

//make 시나리오 제이슨
function saveIntentTaskRel(){
		if ( !editTaskRel ) return true;

		//태스크 선택 후 다음 태스크 선택 체크
		var checkBoolean = true;
		
		intentTaskTable.$(".checkbox",{"page":"all"}).each(function(){
			if($(this).is(":checked") == true){
				var checkNothing = $(this).parent().next().next().next().find('input').val();
				var checkNothingTask =$(this).parent().next().find('a').text();
				if(checkNothing == '선택안함'){
					mui.alert(checkNothingTask + "의 다음 태스크를 선택해주세요.");
					checkBoolean = false;
				}
			}
		});
		
		if(checkBoolean === false){
			return false;
		}else {
		
			//해당 task edge List 삭제
			for (var i = 0; i < scenario.nodes.length; i++) {
				if(scenario.nodes[i].type == "end"){
					delete scenario.nodes[i];
				}
			}
			scenario.nodes = scenario.nodes.filter(function(element){
				return element !== null;
			});
			var nodes = scenario.nodes;
			var edges = scenario.edges;
			if(intentData.No != null){
				intentTaskTable.$(".checkbox",{"page":"all"}).each(function(){
					if($(this).is(":checked") == true){
						var checkedTask = $(this).parent().next().find('a').text();
						var intentNm = $(this).parent().next().next().text();
						var nextTaskVal = $(this).parent().next().next().next().find('input').val();
	
						for (var i = 0; i < nodes.length; i++) {
							if(nodes[i].label == checkedTask){
								var intentListObjCheck = false;
								var intentListObj = new Object();
	
								intentListObj.answer = "";
								intentListObj.intent = intentNm;
								intentListObj.nextTask = nextTaskVal;
								intentListObj.info = "";
	
								if(nodes[i].attr.length > 0 && nodes[i].attr[0] != null){
									var intentList = nodes[i].attr[0].intentList;
	
									for (var j = 0; j < intentList.length; j++) {
										if(intentList[j].intent == intentNm){
											intentList[j].nextTask = nextTaskVal;
											intentListObjCheck = true;
										}
									}
									if(intentListObjCheck == false){
										intentList.push(intentListObj);
									}
								}
							}
						}
					}else{
	
						var unCheckedTask = $(this).parent().next().find('a').text();
						var intentNm = $(this).parent().next().next().text();
	
						for (var i = 0; i < nodes.length; i++) {
							if(nodes[i].label == unCheckedTask){
								nodes[i].attr[0].intentList = nodes[i].attr[0].intentList.filter(function(element){
									return element.intent !== intentNm;
								});
							}
						}
					}
				});
			}else {
				intentTaskTable.$(".checkbox",{"page":"all"}).each(function(){
					if($(this).is(":checked") == true){
						var checkedTask = $(this).parent().next().find('a').text();
						var intentNm = $(this).parent().next().next().text();
						var nextTaskVal = $(this).parent().next().next().next().find('input').val();
	
						for (var i = 0; i < nodes.length; i++) {
							if(nodes[i].label.trim() != "*" || nodes[i].type != "end"){
								if(nodes[i].label == checkedTask){
									var newIntentListObj = new Object();
	
									newIntentListObj.answer = '';
									newIntentListObj.intent = intentNm;
									newIntentListObj.nextTask = nextTaskVal;
									newIntentListObj.info = '';
	
									nodes[i].attr[0].intentList.push(newIntentListObj);
								}
							}
						}
					}
				});
			}
	
			var edgeLabelArr = [];
			var edgeDataArr = [];
	
			for(var i = 0; i < nodes.length; i++){
				var endTaskCnt = 1;
				var endTaskId = "";
				if(nodes[i].attr.length > 0){
	
					var intentList = nodes[i].attr[0].intentList;
					for(var j = 0; j < intentList.length; j++){
						if(intentList[j].nextTask == "종료"){
							if(endTaskCnt == 1){
								endTaskId = uuidv4();
								var endTaskObj = new Object();
	
								endTaskObj.id = endTaskId;
								endTaskObj.type ="end";
								endTaskObj.label = "종료";
								endTaskObj.left = 0;
								endTaskObj.top = 0;
								endTaskObj.attr = [];
	
								nodes.push(endTaskObj);
	
								endTaskCnt++;
							}
	
							var edgeId = uuidv4();
	
							var edgeObj = new Object();
							var edgeDataObj = new Object();
							var edgeLabelObj = new Object();
	
							edgeDataObj.id = edgeId;
							edgeDataObj.type = 'default';
							edgeDataObj.label = intentList[j].intent != '' ? intentList[j].intent : 'ALWAYS';
							edgeLabelObj.label = intentList[j].intent != '' ? intentList[j].intent : 'ALWAYS';
							edgeDataObj.attr = [];
							edgeObj.source = nodes[i].id;
							edgeObj.target = endTaskId;
							edgeLabelObj.target = endTaskId;
	
							edgeObj.data = edgeDataObj;
	
							edgeLabelArr.push(edgeLabelObj);
							edgeDataArr.push(edgeObj);
						}else{
							var edgeId = uuidv4();
	
							var edgeObj = new Object();
							var edgeDataObj = new Object();
							var edgeLabelObj = new Object();
	
							edgeDataObj.id = edgeId;
							edgeDataObj.type = 'default';
							edgeDataObj.label = intentList[j].intent != '' ? intentList[j].intent : 'ALWAYS';
							edgeLabelObj.label = intentList[j].intent != '' ? intentList[j].intent : 'ALWAYS';
							edgeDataObj.attr = [];
							edgeObj.source = nodes[i].id;
	
							edgeLabelObj.source = nodes[i].id;
							for(var k = 0; k < nodes.length; k++){
								if(intentList[j].nextTask == nodes[k].label){
									edgeObj.target = nodes[k].id;
									edgeLabelObj.target = nodes[k].id;
								}
							}
	
							edgeObj.data = edgeDataObj;
	
							edgeLabelArr.push(edgeLabelObj);
							edgeDataArr.push(edgeObj);
						}
					}
				}
	
			}
	
			for(var i = 0; i < edgeLabelArr.length; i++){
				var edgeLabel = "";
	
				for(var j = 0; j < edgeLabelArr.length; j++){
					if(edgeLabelArr[i].source == edgeLabelArr[j].source){
						if(edgeLabelArr[i].target == edgeLabelArr[j].target){
							edgeLabel += edgeLabelArr[j].label + "/";
						}
					}
				}
	
				edgeDataArr[i].data.label = edgeLabel.slice(0,-1);
			}
	
			scenario.edges = edgeDataArr;
			return true;
		}
}

//UUID 생성
function uuidv4() {
	  return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
	    (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
	  )
}

// TASK Rel 적용한 내용 초기화
function resetTempIntentTaskRel(){
	if(intentData.No != null) {
		$.ajax({
			url: "simpleBot/getSimplebotById",
			data: JSON.stringify({simplebotId: Number(data.simplebotId)}),
			type: "POST",
			contentType: 'application/json'
		}).then(function (response) {

			scenario = JSON.parse(response.scenarioJson);
			getIntentTaskDetail("Yes");
			editTaskRel = false;
			saveCheckIntentDetail();

		}).catch(function () {
			console.log('[getSimplebotById error]');
		});
	}else{
		if(intentTaskTable.page.info().length > 0){
			for (var i = 0; i < intentTaskTable.page.info().recordsTotal; i++) {
				var setDefault = "default_"+i;
				eval("intent_next_task_" + i + ".setValue([setDefault])");
				var intentNextTask = 'intent_next_task_' + i;

				if(intentTaskList2[i].nextTask == 'default'){
					$("#"+intentNextTask).prop('disabled', true);
				}
			}
			intentTaskTable.$(".checkbox",{"page":"all"}).each(function(){
				$(this).parent().next().next().next().find('input').prop('disabled',true);
				$(this).prop('checked',false);
			});
			$("#list_all").prop('checked',false);
			$("#intent_seleted_task").text('');
		}
		editTaskRel = false;
		saveCheckIntentDetail();
	}
}
/* TASK 연결 end */

/* 정규식 start */
function getRegexList() { //정규식 리스트
	var obj = {
		'host' : intentData.host,
		'bertIntentName' : intentData.bertIntentName,
		'bertIntentNo' : intentData.No,
		'lang' : intentData.lang,
	}

	$.ajax({
		url: "voiceBot/getRegexList",
		data: JSON.stringify(obj),
		type: "POST",
		contentType: 'application/json'
	}).then(function (res) {
		var regexList = res.regexList;
		regexTableData = [];

		if ( !regexList.length || regexList.length === 1 && !regexList[0].Regex ) {
			$('.dl_dropdown.regex dd').removeClass('loading');
			return;
		}

		$.each(regexList, function(i, list) {
			var obj = {};
			obj.regex = list.Regex ? list.Regex : '';
			obj.regexIntentNo = list.RegexIntentNo ? String(list.RegexIntentNo) : '';
			obj._isUpdated = false;
			obj._tempId = '';

			regexTableData.push(obj);
		});

		updateRegexTable();

	}).catch(function(){
		console.log('[getRegexList error]');
	});
}

function getRegexDetail(regexNo, _tempId, _isUpdated) { //정규식 다테일 리스트
	if ( editRegex ) {
		mui.confirm('정규식을 작성하던 내용이 저장되지 않습니다.<br>계속 하시겠습니까?', {
			onClose: function(isOk) {
				if (!isOk) return;
				setDetail();
			}
		});

	} else {
		setDetail();
	}

	function setDetail() {
		intentRegexData.no = regexNo;
		intentRegexData.values = '';
		intentRegexData.types = '';
		intentRegexData.regexResult = '';
		intentRegexData._tempId = _tempId;
		intentRegexData._isUpdated = _isUpdated !== "false";

		var $target = $(event.target);
		$('#list_intent_regex tr').removeClass('active');
		$target.parents('tr').addClass('active');
		$('.btn_box.regex_add').hide();
		$('.btn_box.regex_modify').show();

		if ( intentRegexData._isUpdated ) { //수정된 리스트
			var regexDetail = [];
			var data = null;
			if ( intentRegexData.no ) {
				data = updateRegexData.find(e => e.regexIntentNo === intentRegexData.no );

			} else {
				data = updateRegexData.find(e => e._tempId === intentRegexData._tempId );

			}

			if ( !data.regexType ) { //정규식 직접입력
				var obj = {};
				obj.Regex = data.regex;

				regexDetail.push(obj);

			} else {
				var ruleTypeArr = data.regexType.split(',');
				var RuleValueArr = data.regexValue.split(',');

				for (var i=0; i<ruleTypeArr.length; i++) {
					var obj = {};
					obj.Regex = data.regex;
					obj.RuleType = ruleTypeArr[i];
					obj.RuleValue = RuleValueArr[i];

					regexDetail.push(obj);
				}
			}

			detailList(regexDetail);

		} else {
			$.ajax({
				url: "voiceBot/getRegexDetail",
				data: JSON.stringify({
					regexIntentNo: intentRegexData.no,
				}),
				type: "POST",
				contentType: 'application/json'
			}).then(function (res) {
				var regexDetail = res.regexDetail;
				detailList(regexDetail);

			}).catch(function(){
				console.log('[getRegexDetail error]');
			});
		}
	}

	function detailList(list) {
		$('#regex_list').empty();
		$('#regex_rule').val('');

		//오른쪽 개별 상세 가져오기
		if ( !list.length ) return;

		$('.ipt_box.regex_direct').removeClass('active');
		$('.view.regex_direct').addClass('active');

		if ( !list[0].RuleType ) { //정규식 직접입력
			$('.view.regex_direct div').addClass('checked');
			$('#btn_add_regex').prop('disabled', true);
			$('#regex_rule').prop('readonly', false).val(list[0].Regex);

		} else { //정규식 UI
			$('.view.regex_direct div').removeClass('checked');
			$('#btn_add_regex').prop('disabled', false);
			$('#regex_rule').prop('readonly', true).val(list[0].Regex);

			$.each(list, function(i, list) {
				var $template = $('template#tempRegexList').clone();
				var $temp = $($template.html());

				$temp.find('option[value=\''+ list.RuleType +'\']').prop('selected', true);
				$temp.find('input.regex_type').addClass(list.RuleType).val(list.RuleValue);

				$('#regex_list').append($temp);
				regexSortableList.option('sort', true);
			});
		}
	}
}

function drawRegexTable() { //정규식 테이블
	regexTable = $('#list_intent_regex table').DataTable({
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
		bDestroy: true,
		lengthChange : false,
		fixedColumns: true,
		autoWidth: false, //css width
		searching: true, //검색
		ordering : false, //정렬
		paging: true, // paging 디폴트 : row 10개, 페이징 5개
		pageLength: 5, // row 5개
		pagingType : "full_numbers_no_ellipses",
		data: regexTableData, //참조 data
		columns: [
			{
				data: 'regexIntentNo',
				name: 'regexIntentNo',
				searchable: false,
				render: function(data, type, row){
					var _id = data ? data : row._tempId;
					return "<input type='checkbox' class='checkbox' id='"+ _id +"' _tempId='"+ row._tempId +"' onclick='checkRegexDelete(this)'> <label for='"+ _id +"'></label>";
				}
			},
			{
				data: 'regex',
				name: 'regex',
				title: '정규식',
				searchable: true,
				render: function(data, type, row){
					return "<a onclick='getRegexDetail(\""+row.regexIntentNo+"\", \""+row._tempId+"\", \""+row._isUpdated+"\");'>"+data+"</a>";
				}
			}
		],
		"initComplete": function() {
			$('.dl_dropdown.regex dd').removeClass('loading');

			if ( regexTableData.length < 6 ) {
				$('#list_intent_regex .dataTables_paginate').hide();

			} else if ( regexTableData.length > 5 ) {
				$('#list_intent_regex .dataTables_paginate').show();
			}
		},
	});
	regexTable.draw();
	saveCheckRegex = false;

	// dataTable 통합 검색
	$("input[name='intent_regex_search']").keyup(function(){
		regexTable.search($(this).val()).draw();
	});
}

function updateRegexTable() {
	saveCheckIntentDetail();

	if ( !regexTable ) {
		drawRegexTable();
		initRegexDetailList();
		return;
	}

	regexTable.clear();
	regexTable.rows.add(regexTableData).draw();

	if ( regexTableData.length < 6 ) {
		$('#list_intent_regex .dataTables_paginate').hide();

	} else if ( regexTableData.length > 5 ) {
		$('#list_intent_regex .dataTables_paginate').show();
	}

	initRegexDetailList();
	saveCheckRegex = true;
}

function resetRegexTable() { //정규식 테이블 초기화
	saveCheckRegex = false;
	saveCheckIntentDetail();

	if(intentData.No != null){
		getRegexList();
	}else{
		regexTableData = [];
		updateRegexData = [];
		deleteTempRegexData =[];
		deleteRegexData = [];

		regexTable.clear();
		regexTable.rows.add(regexTableData).draw();
	}
}

function initRegexDetailList() { //정규식 드래그 리스트 초기화면
	intentRegexData = {}
	$('#list_intent_regex tr').removeClass('active');
	$('.ipt_box.regex_direct').addClass('active');
	$('.view.regex_direct').removeClass('active');

	var $template = $('template#tempRegexList').clone();
	var $temp = $($template.html());

	$('#regex_list').empty();
	$('#regex_list').append($temp);
	$('#regex_rule').prop('readonly', true).val('');
	$('#btn_add_regex').prop('disabled', false);
	$('.btn_box.regex_add').show();
	$('.btn_box.regex_modify').hide();
}

function addRegexRuleDetail() { //정규식 디테일 규칙추가
	var $template = $('template#tempRegexList').clone();
	var $temp = $($template.html());
	var $thisSelect = $temp.find('select');

	$('select[name="regex_type"]').each(function(i, select) {
		var value = select.value;

		if ( value === 'match' ) {
			$temp.find('option[value="' + value + '"]').prop('disabled', true);
			$thisSelect.prop('disabled', true).next('input.regex_type').prop('disabled', true);

		} else if ( value !== 'include' ) { //start, end, exclude
			$temp.find('option[value="' + value + '"]').prop('disabled', true);

		}
	});

	$thisSelect.data('prevRegex', $thisSelect.val());
	$('#regex_list').append($temp);
}

function removeRegexRuleDetail() { //정규식 디테일 규칙 삭제
	var list = $(event.target).parent();
	var select = list.find('select[name="regex_type"]');
	var value = select.val();

	var text = '';
	if ( value === 'start' ) text = '시작 텍스트';
	else if ( value === 'match' ) text = '텍스트가 정확하게 일치함';
	else if ( value === 'include' ) text = '텍스트에 포함';
	else if ( value === 'exclude' ) text = '텍스트에 포함하지 않음';
	else if ( value === 'end' ) text = '종료 텍스트';

	mui.confirm('"' + text + '" 정규식을 삭제하시겠습니까?', {
		onClose: function(isOk) {
			if (isOk) {
				select.val('delete');
				changeRegexRuleSelect(select);
				list.remove();
			}
		}
	});
}

function changeRegexRuleSelect(select) { //정규식 디테일 조건 변경
	var $thisSelect = $(select);

	var prevValue = $thisSelect.data('prevRegex');
	var value = $thisSelect.val();

	// 이전 type
	if ( prevValue === 'match' ) {
		$('select[name="regex_type"]').prop('disabled', false).next('input.regex_type').prop('disabled', false);

	} else if ( prevValue !== 'include') { //start, end, exclude
		$('select[name="regex_type"]').find('option[value="' + prevValue + '"]').prop('disabled', false);

	}

	// 현재 type
	if ( value === 'match' ) {
		$('select[name="regex_type"]').not($thisSelect).prop('disabled', true).next('input.regex_type').prop('disabled', true);

	} else if ( value !== 'include' ) { //start, end, exclude
		$('select[name="regex_type"]').not($thisSelect).find('option[value="' + value + '"]').prop('disabled', true);

	}

	$thisSelect.next('input.regex_type').removeClass(prevValue);
	$thisSelect.next('input.regex_type').addClass(value);
	$thisSelect.data('prevRegex', value);

	makeRegexRuleResult();
}

function makeRegexRuleResult() { //정규식 결과 값
	var regexResult = '';
	var excludeRegex = '';
	var startRegex = '';
	var includeRegex = '';
	var endRegex = '';
	var matchRegex = '';

	var values = '';
	var excludeValue = '';
	var startValue = '';
	var includeValue = '';
	var endValue = '';
	var matchValue = '';
	var types = '';
	var includeCount = 0;

	$('#regex_list input.regex_type').each(function(i, input){ //각 regex 만들기
		var value = input.value;
		var isEmpty = value.trim() === '';
		var $input = $(input);
		var regex = '';

		// input value 가 없음
		if ( isEmpty ) return;

		// input이 disabled
		if ( $input.is(':disabled') ) return;

		if ( $input.hasClass('match') ) { // 텍스트가 정확하게 일치 ^(value)$
			regex = '^(' + value + ')$';
			matchValue = value;
			matchRegex = regex;
			return;
		}

		if ( $input.hasClass('start') ) { // 시작 텍스트 ^(value)
			regex = '^(' + value + ')';
			startValue = value;
			startRegex = regex;
			return;
		}

		if ( $input.hasClass('end') ) { // 종료 텍스트 (value)$
			regex = '(' + value + ')$';
			endValue = value;
			endRegex = regex;
			return;
		}

		if ( $input.hasClass('exclude') ) { // 텍스트에 포함하지 않음 (?!.*(value))
			regex = '(?!.*(' + value + '))';
			excludeValue = value;
			excludeRegex = regex;
			return;
		}

		if ( $input.hasClass('include') ) { // 텍스트에 포함 (value)
			regex = '(' + value + ')';

			if ( includeRegex ) {
				includeValue += ',' + value;

			} else {
				includeValue = value;
			}

			includeRegex += '.*' + regex;
			includeCount += 1;
		}
	});

	//각 regex 합치기
	if ( excludeRegex ) {
		values += ',' + excludeValue;
		types += ',exclude';
		regexResult += '.*' + excludeRegex;
	}
	if ( startRegex ) {
		values += ',' + startValue;
		types += ',start';
		regexResult += '.*' + startRegex + '.*';
	}
	if ( includeRegex ) {
		values += ',' + includeValue;
		types += ',include'.repeat(includeCount);
		regexResult += '.*.*' + includeRegex + '.*';
	}
	if ( endRegex ) {
		values += ',' + endValue;
		types += ',end';
		regexResult += '.*.*' + endRegex;
	}
	if ( matchRegex ) {
		values += ',' + matchValue;
		types += ',match';
		regexResult += '.*' + matchRegex;
	}

	regexResult = regexResult.substr(2); //맨 앞 '.*' 제거
	regexResult = regexResult.replace('.*.*.*.*', '.*').replace('.*.*.*', '.*').replace('.*.*', '.*').replace(')).*^(', '))^(');


	$('#regex_rule').val(regexResult);
	$('#regex_test_result').removeClass();

	values = values.substr(1); //맨 앞 , 제거
	types = types.substr(1); //맨 앞 , 제거

	intentRegexData.values = values;
	intentRegexData.types = types;
	intentRegexData.regexResult = regexResult;

	if ( !intentRegexData.no && !intentRegexData.regexResult ) editRegex = false;
	else editRegex = true;
}

function setRegexRuleDirect(checked) { //정규식 직접입력
	if ( checked ) { //rule disabled
		$('#regex_list').find('select[name="regex_type"]').prop('disabled', true).next('input.regex_type').prop('disabled', true);
		$('#regex_rule').prop('readonly', false).val('');
		$('#btn_add_regex').prop('disabled', true);
		regexSortableList.option('sort', false);

	} else {
		$('#regex_rule').prop('readonly', true).val('');
		$('#btn_add_regex').prop('disabled', false);
		regexSortableList.option('sort', true);
		setRegexRule();
	}

	function setRegexRule() {
		var isMatch = false;

		$('select[name="regex_type"]').each(function(i, select){
			isMatch = select.value === 'match';

			if ( isMatch ) {
				var $select = $(select);

				$('#regex_list').find('select[name="regex_type"]').prop('disabled', true).next('input.regex_type').prop('disabled', true);
				$select.prop('disabled', false).next('input.regex_type').prop('disabled', false);
				return false;
			}
		});

		if ( !isMatch ) {
			$('#regex_list').find('select[name="regex_type"]').prop('disabled', false).next('input.regex_type').prop('disabled', false);
		}

		$('#regex_test').val('');
		makeRegexRuleResult();
	}
}

function writeDirectRegexRule() {
	if ( !intentRegexData.no && !$('#regex_rule').val().trim() ) editRegex = false;
	else {
		intentRegexData.regexResult = $('#regex_rule').val();
		editRegex = true;
	}
}

function enterCheckRegexTest(key) { //정규식 테스트 엔터 입력 시
	if ( key === 13 ) testRegexRule();
}

function testRegexRule() { //정규식 테스트
	var testText = $('#regex_test').val().trim();
	var regexRule = $('#regex_rule').val();
	var isTestMatched = null;
	var pattern = null;

	$('#regex_test_result').removeClass();

	if ( !regexRule ) {
		mui.alert('정규식 규칙을 추가하거나<br> 정규식 직접입력을 해주세요.');
		return;
	}

	if( !testText ) {
		mui.alert('정규식 테스트 문장을 입력해주세요.');
		return;
	}

	try {
		pattern = new RegExp(regexRule);
		isTestMatched = pattern.test(testText);

	} catch (e) {
		mui.alert('잘못된 정규식입니다.');
		console.log('[testRegexRule catch]');
	}

	$('#regex_test').val(testText);
	$('#regex_test_result').removeClass();

	if ( isTestMatched ) {
		$('#regex_test_result').addClass('match');
		$('#regex_test_result').text('MATCH');

	} else {
		$('#regex_test_result').addClass('unmatch');
		$('#regex_test_result').text('UNMATCH');
	}
}

function applyInsertRegex() { //정규식 추가,수정 임시 데이터 저장
	if ( !$('#regex_rule').val().trim() ) {
		mui.alert('정규식 규칙을 입력해주세요.');
		return;
	}

	if ( !intentRegexData.regexResult ) {
		mui.alert('수정된 내용이 없습니다.<br>취소를 눌러주세요.')
		return;
	}

	var obj = {};
	obj.host = intentData.host;
	obj.bertIntentNo = intentData.No;
	obj.lang = intentData.lang;
	obj.bertIntentName = intentData.bertIntentName;
	obj.regexIntentNo = intentRegexData.no ? intentRegexData.no : ''; //regex no 전달
	obj.isInsert = intentRegexData.no ? 'N' : 'Y'; //regex no가 없으면 Y
	obj.regexValue = intentRegexData.values; //규칙 순서대로 값 (,)로 연결 intentRegexData.values
	obj.regexType = intentRegexData.types; //규칙 순서대로 타입 (,)로 연결 intentRegexData.types
	obj.regex = $('#regex_rule').val();
	obj._tempId = ( obj.isInsert === 'Y' && !intentRegexData._tempId ) ? uuidv4() : intentRegexData._tempId;
	obj._isUpdated = !intentRegexData._isUpdated ? false : intentRegexData._isUpdated;

	//update data
	if ( obj._isUpdated ) {
		var updateIndex = -1;

		if ( obj.regexIntentNo ) { //regexIntentNo 가 있을 때
			updateIndex = updateRegexData.findIndex(e => e.regexIntentNo === obj.regexIntentNo );

		} else { //없으면 _tempId로 확인
			updateIndex = updateRegexData.findIndex(e => e._tempId === obj._tempId );

		}
		updateRegexData.splice(updateIndex, 1);
	}

	if ( obj._tempId && !obj._isUpdated ) obj._isUpdated = true;
	updateRegexData.push(obj);

	//table data
	var data = null;
	if ( obj.regexIntentNo ) { //regexIntentNo 가 있을 때
		data = regexTableData.find(e => e.regexIntentNo === obj.regexIntentNo);

	} else { //없으면 _tempId로 확인
		data = regexTableData.find(e => e._tempId === obj._tempId);

	}

	if ( !data ) { //신규추가
		regexTableData.push(obj);

	} else {
		data._isUpdated = true;
		data.regex = obj.regex;
		data.regexValue = obj.values;
		data.regexType = obj.types;
	}

	updateRegexTable();
	intentRegexData = {};
	editRegex = false;
	saveCheckRegex = true;
}

//딜리트 arr를 만든 후에
function checkRegexDelete(checkbox) {
	var $checkbox = $(checkbox);
	var obj = {};
	obj.regexIntentNo = $checkbox.attr('id');
	obj._tempId = $checkbox.attr('_tempId');

	if ( $checkbox.prop('checked') ) {
		deleteTempRegexData.push(obj);

	} else {
		var index = -1;

		if ( obj.regexIntentNo === obj._tempId ) {
			index = deleteTempRegexData.findIndex(e => e._tempId === obj._tempId);

		} else {
			index = deleteTempRegexData.findIndex(e => e.regexIntentNo === obj.regexIntentNo);
		}
		deleteTempRegexData.splice(index, 1);
	}
}

function applyDeleteRegex() { //정규식 삭제 임시 데이터 저장
	if ( !deleteTempRegexData.length ) {
		mui.alert('삭제할 정규식이 없습니다.<br>체크박스에 체크해주세요.');
		return;
	}

	var tempData = deleteTempRegexData.slice();

	$.each(tempData, function(i, data) {
		var dataIndex = -1;
		var updataIndex = -1;
		var deleteIndex = -1;

		if ( data.regexIntentNo === data._tempId ) { //table data, update data, delete data 전부 지움
			dataIndex = regexTableData.findIndex(e => e._tempId === data._tempId);
			updataIndex = updateRegexData.findIndex(e => e._tempId === data._tempId);
			deleteIndex = deleteTempRegexData.findIndex(e => e._tempId === data._tempId);

			updateRegexData.splice(updataIndex, 1);
			deleteTempRegexData.splice(deleteIndex, 1);

		} else { //table data 지움
			var no = data.regexIntentNo;
			dataIndex = regexTableData.findIndex(e => e.regexIntentNo === no);

		}

		if ( dataIndex === -1 ) return;
		regexTableData.splice(dataIndex, 1);
	})

	updateRegexTable();
	deleteRegexData = deleteTempRegexData;
	editRegex = false;
	saveCheckRegex = true;
}

function insertAndUpdateRegexList(intentNo, intentName) { //저장 버튼 클릭 시
	if ( updateRegexData.length === 0 ) return Promise.resolve({ isSuccess: true });
	if ( intentNo ) {
		$.each(updateRegexData, function(i, data) {
			data.bertIntentNo = intentNo;
			data.bertIntentName = intentName;
		});
	}

	return $.ajax({
		url: "voiceBot/insertRegex",
		data: JSON.stringify(updateRegexData),
		type: "POST",
		// async: false,
		contentType: 'application/json'
	}).then(
		function (result) {
			updateRegexData = [];
			if ( result.dupRegexCnt > 0 ) {
				var dup = result.dupRegex.split('//');
				var dupRegex = '';
				dup.pop(); //마지막 요소 제거
				$.each(dup, function(i, regex) {
					dupRegex += '<br>' + regex;
				});

				mui.alert('중복된 정규식이 '+ result.dupRegexCnt +' 개 있습니다.<br>중복된 정규식은 아래에서 확인 가능하며, 중복 건은 저장되지 않습니다.' + dupRegex)
			}

			return { isSuccess: true };

		}).catch(function (result) {
		mui.alert("정규식 저장을 실패하였습니다.<br>관리자에게 문의해주세요.");
		console.log('[insertAndUpdateRegexList] error')
		$('#vb_wrap').removeClass('loading');
		return { isSuccess: false };
	});
}

function deleteRegexList() {
	if ( deleteRegexData.length === 0 ) return Promise.resolve({ isSuccess: true });

	return $.ajax({
		url : "voiceBot/deleteRegexRule",
		data : JSON.stringify(deleteRegexData),
		type: "POST",
		//async: false,
		contentType: 'application/json'
	}).then(function() {
		deleteTempRegexData = [];
		deleteRegexData = [];
		console.log('[deleteRegexList] success');
		return { isSuccess: true };

	}).catch(function() {
		mui.alert("정규식 삭제를 실패하였습니다.");
		console.log('[deleteRegexList] error');
		$('#vb_wrap').removeClass('loading');
		return { isSuccess: false };
	});
}
/* 정규식 end */

/* 학습문장 start */
var firstNqaQuestion;

function getNqaStudySentence(){
	intentDetailNqaStcList = [];

	var obj = new Object();
	obj.answerId = intentData.answerId;
	obj.Intent = intentData.bertIntentName;

	$.ajax({
		url: "api/nqa/qa-sets/getNqaDetail",
		data: JSON.stringify(obj),
		type: "POST",
		contentType: 'application/json'

	}).then(function (result) {
		checkNqaTrained();
		intentDetailNqaStcList = result.questionList;

		if(intentNqaTable == null){
			firstNqaQuestion = result.questionList.length == 0 ? "" : intentDetailNqaStcList[0].question;
			getNqaStudySentenceList();
		}else {
			if(intentDetailNqaStcList.length == 0 || firstNqaQuestion != intentDetailNqaStcList[0].question){
				intentTableData = [];

				getIntention(intentData).then(function(){
					updateIntentTable(intentTableData);
				});
			}

			intentNqaTable.clear();
			intentNqaTable.rows.add(intentDetailNqaStcList).draw();

			if ( intentDetailNqaStcList.length < 6 ) {
				$('#nqaStcTbl .dataTables_paginate').hide();

			} else if ( intentDetailNqaStcList.length > 5 ) {
				$('#nqaStcTbl .dataTables_paginate').show();
			}
		}

	}).catch(function (response) {
		console.log('[getNqaDetail error] ' + response);
	});

}


function getNqaStudySentenceList() {
	intentNqaTable = $('#nqaStcTbl table').DataTable({
		"language": {
			"emptyTable": "등록된 데이터가 없습니다.",
			"lengthMenu": "페이지당 _MENU_ 개씩 보기",
			"info": "현재 _START_ - _END_ / _TOTAL_건",
			"infoEmpty": "데이터 없음",
			"infoFiltered": "( _MAX_건의 데이터에서 필터링됨 )",
			"search": "",
			"zeroRecords": "일치하는 데이터가 없습니다.",
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
		bDestroy: true,
		lengthChange : false,
		fixedColumns: true,
		autoWidth: false, //css width
		searching: true, //검색
		ordering : false, //정렬
		paging: true, // paging 디폴트 : row 10개, 페이징 5개
		pageLength: 5,
		pagingType : "full_numbers_no_ellipses",
		data: intentDetailNqaStcList, //참조 data
		columns: [
			{
				data: "id",
				name: "id",
				title: "",
				searchable: false,
				width: '40px',
				render: function(data, type, row, meta){
					return "<input type=\"checkbox\" name=\"delNqaCheck\" class=\"checkbox\" id=\"list_"+meta.row+"\" value=\""+data+"\" >" +
						"<label for=\"list_"+meta.row+"\"></label>";
				}
			},
			{
				data: "question",
				name: 'question',
				title: '학습문장',
				searchable: true,
				render: function(data, type, row, meta){
					return "<a onclick=\"modNqaStc('update')\">"+data+ "</a>";
				}
			},
		],
		"initComplete": function() {
			$('.dl_dropdown.nqa dd').removeClass('loading');

			if ( intentDetailNqaStcList.length < 6 ) {
				$('#nqaStcTbl .dataTables_paginate').hide();

			} else if ( intentDetailNqaStcList.length > 5 ) {
				$('#nqaStcTbl .dataTables_paginate').show();
			}
		},
	});

	intentNqaTable.draw();

	// dataTable 통합 검색
	$("#intent_nqa_search").keyup(function(){
		intentNqaTable.search($(this).val()).draw();
	});
}

function modNqaStc(mode) {
	var rowcollection = intentNqaTable.$("tr",{"page":"all"});
	rowcollection.each(function(){
		$(this).removeClass('active');
	});

	if(mode == 'update'){
		var $target = $(event.target);
		var thisData = intentNqaTable.row($target.parents('tr')).data();
		$("#intent_npa_id").val(thisData.id);
		$("#intent_npa").val(thisData.question);

		$target.parents('tr').addClass('active');
		$('#insertNqaBtn').hide();
		$('#updateNqaBtn').show();
	} else if(mode == 'insert') {
		$("#intent_npa_id").val('');
		$("#intent_npa").val('');

		$('#insertNqaBtn').show();
		$('#updateNqaBtn').hide();
	}
}

function insertNqaStc() {
	var obj = new Object();
	obj.answer = intentData.bertIntentName;
	obj.answerId = intentData.answerId;
	obj.question = $('#intent_npa').val();
	obj.channelId = intentData.host;
	obj.channelName = data.name.replace(" ","");
	obj.categoryId = intentTableData[0].categoryId;

	$.ajax({
		url: "api/nqa/qa-sets/insertNqaAnswer",
		data: JSON.stringify(obj),
		type: "POST",
		contentType: 'application/json'

	}).then(function (result) {
		if (result.result == 'success') {
			mui.alert("저장이 완료되었습니다.");
			intentData.answerId = result.answerId;
			$('#intent_npa').val('');
			getNqaStudySentence();
			getNqaCount();
		} else {
			mui.alert("저장을 실패하였습니다.");
		}

	}).catch(function (response) {
		mui.alert("저장을 실패하였습니다.");
		console.log('[addNqaSentence error] ' + response);
	});
}

function updateNqaStc(){
	var obj = new Object();
	obj.answer = intentData.bertIntentName;
	obj.answerId = intentData.answerId;
	obj.channelId = intentData.host;
	obj.channelName = data.name.replace(" ","");
	obj.categoryId = intentTableData[0].categoryId;
	obj.question = $('#intent_npa').val();
	obj.questionId = $("#intent_npa_id").val();

	$.ajax({
		url : "api/nqa/qa-sets/editNqaAnswer",
		data : JSON.stringify(obj),
		type: "POST",
		contentType: 'application/json'
	}).then(function (result) {
		if (result.result == 'success') {
			mui.alert("수정이 완료되었습니다.");
			intentData.answerId = result.answerId;
			getNqaStudySentence();
			getNqaCount();
			modNqaStc('insert');
		} else {
			mui.alert("수정을 실패하였습니다.");
		}
	}).catch(function (response) {
		mui.alert("수정을 실패하였습니다.");
	});
}

function deleteNqaStudyStc(){
	var rowcollection = intentNqaTable.$(".checkbox",{"page":"all"});
	var deleteNqaList = [];

	rowcollection.each(function(){
		if(this.checked){
			console.log("delete questionId : " + this.value);
			var obj = new Object();
			obj.questionId = this.value;
			obj.answer = intentData.bertIntentName;
			obj.answerId = intentData.answerId;
			obj.channelId = intentData.host;
			obj.channelName = data.name.replace(" ","");
			deleteNqaList.push(obj);
		}
	});
	$("#vb_container").addClass("loading");

	if(deleteNqaList.length > 0){
		$.ajax({
			url : "api/nqa/qa-sets/deleteNqaAnswerList",
			data : JSON.stringify(deleteNqaList),
			type: "POST",
			contentType: 'application/json'
		}).then(function (result) {
			$("#vb_container").removeClass("loading");
			if (result.result == 'success') {
				mui.alert("삭제가 완료되었습니다.");
			} else {
				mui.alert("삭제를 실패하였습니다.");
			}

			getNqaStudySentence();
			getNqaCount();

		}).catch(function (response) {
			$("#vb_container").removeClass("loading");
			mui.alert("삭제를 실패하였습니다.");

			console.log("deleteNqa ERROR : " + response);
		});
	}
}



function pressEnterNqa(key){
	if(key == 13){
		if($('#updateNqaBtn').css('display') == 'block'){
			updateNqaStc();
		}else if($('#insertNqaBtn').css('display') == 'block') {
			insertNqaStc();
		}
	}

}

function beforeAddIntentNqaUI() {
	var nqaDropBox = $('.dl_dropdown.nqa');
	nqaDropBox.find('button, input, textarea').prop('disabled', true);
}

function afterAddIntentNqaUI() {
	var nqaDropBox = $('.dl_dropdown.nqa');
	nqaDropBox.find('button, input, textarea').prop('disabled', false);
}
/* 학습문장 end */