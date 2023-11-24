var dataTableData = null;
var table;

$(document).ready(function(){

  dataTableDataSet();
  if(table != null){
	  table.clear()
	       .draw();
  }
  table = $('#taskTable').DataTable({
	    "language": {
	        "emptyTable": "등록된 태스크가 없습니다.",
	        "lengthMenu": "페이지당 _MENU_ 개씩 보기",
	        "info": "현재 _START_ - _END_ / _TOTAL_건",
	        "infoEmpty": "데이터 없음",
	        "infoFiltered": "( _MAX_건의 데이터에서 필터링됨 )",
	        "search": "",
	        "zeroRecords": "일치하는 데이터가 없습니다.",
	        "loadingRecords": "로딩중...",
	        "processing":     "잠시만 기다려 주세요...",
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
	    searching: true,
	    lengthChange : false,
	    columnDefs: [
	    	    { visible: false , targets:[0,1]},
	            { width: '160', targets: 2},
	    	    { width: '90', targets: 6}
	        ],
	    fixedColumns: true,
	    columns: [
	    	{
	    	 data: "sort",
	    	 title: 'sort',
	    	 searchable: false,
	        },
	    	{
	    	 data: "id",
	    	 name: 'id',
	    	 title: 'id',
	    	 searchable: false,
	    	},
	        {
	         data: "taskGroup",
	         name: 'taskGroup',
	         title: 'TASK 그룹',
	         sortable: false,
	         searchable: true,
	        },
	        {
	         data: "label",
	         name: 'label',
	         title: 'TASK',
	         sortable: false,
	         searchable: true,
	         render: function(data,type,row){
	        	 return "<button type='button' class='btn_line_warning' onclick='deleteTask(\""+row.id+"\",\""+row.label+"\",\""+row.type+"\",this);'>삭제</button>" +
		          		"<a onclick='getTaskNode(\""+row.id+"\"); changeContents(PAGES.TASK_DETAIL);'>"+data+"</a>";
		     }
	        },
	        {
	          data : "intent",
	          title: 'INTENT',
	          sortable: false,
	          searchable: true,
						render: function(name,type,row){
							if(name == "always"){
								return "<span>"+name+"</span>";
							}else {
								return "<a onclick='moveIntentDetail(\""+data.host+"\",\""+name+"\",\""+ "name" +"\")'>"+name+"</a>";
							}
	          }
	        },
	        {
	         data : "nextTask",
	         title: '다음 TASK',
	         sortable: false,
	         searchable: true,
	         render: function(data,type,row){
				 if(row.nextTask === '종료'){
					 return "<span>"+data+"</span>";
				 }else{
					 return "<a onclick='getTaskNode(\""+row.nextTaskId+"\"); changeContents(PAGES.TASK_DETAIL);'>"+data+"</a>";
				 }
		     }
	        },
	        {
	         data : "info",
	         title: '정보/비고',
	         sortable: false,
	         searchable: false,
	        },
	    ],
	    data: dataTableData,
	    
	   rowsGroup: [
		    'id:name',
	        'taskGroup:name',
	        'label:name',
	    ],
	    
	    orderFixed:[[0,"asc"]],
	    paging: true,
	    pagingType : "full_numbers_no_ellipses",

			"autoWidth": false,
			"initComplete": function() {
				if ( dataTableData.length === 0 ) {
					$('.dataTables_paginate').remove();
				}
			},
	});
  
  		// dataTable 통합 검색
  		$("#allSearch").keyup(function(){
  			table.search($(this).val()).draw();
  		});
  
});

function dataTableDataSet(){
	dataTableData = [];
	if(scenario != null){
		var nodes = scenario.nodes;
		var sort = 0;
		for (var i = 0; i < nodes.length; i++) {
			if(nodes[i].type != 'end'){
				if(nodes[i].attr[0] != null){
					var intentList = nodes[i].attr[0].intentList;
					if(intentList.length > 0){
						for (var j = 0; j < intentList.length; j++) {
							
							var obj = new Object();
							
							obj.id = nodes[i].id;
							obj.type = nodes[i].type;
							obj.taskGroup = nodes[i].taskGroup;
							obj.label = nodes[i].label;
							obj.intent = intentList[j].intent != "" ? intentList[j].intent : "always";
							obj.nextTask = intentList[j].nextTask;
							obj.info = intentList[j].info;
							
							for (var k = 0; k < nodes.length; k++) {
								if(nodes[k].label == intentList[j].nextTask){
									obj.nextTaskId = nodes[k].id;
								}
							}
							obj.sort = sort;
							dataTableData.push(obj);
							sort++;
						}
					}else {
						var obj = new Object();
						
						obj.id = nodes[i].id;
						obj.type = nodes[i].type;
						obj.taskGroup = nodes[i].taskGroup;
						obj.label = nodes[i].label;
						obj.intent = "";
						obj.nextTask = "";
						obj.info = "";
						obj.nextTaskId = "";
						obj.sort = sort;
						dataTableData.push(obj);
						sort++;
					}
				}else {
					var obj = new Object();
					obj.id = nodes[i].id;
					obj.type = nodes[i].type;
					obj.taskGroup = "";
					obj.label = nodes[i].label;
					obj.intent = "";
					obj.nextTask = "";
					obj.info = "";
					obj.nextTaskId = "";
					obj.sort = sort;
					dataTableData.push(obj);
					sort++;
				}
			}
		}
	}
}

function addTask(){
	taskNode = null;
}

function deleteTask(nodeId, nodeLabel, type, el) {
	if(type == "start"){
		mui.alert("시작 테스크는 삭제할 수 없습니다.");
		return false;
	}else if(type == "end") {
		mui.alert("종료 테스크는 삭제할 수 없습니다.");
		return false;
	}
	var deleteTaskNm = $(el).next().text(); 
	var deleteAlertTxt = "\"" + $(el).next().text() + "\"을(를) 삭제하시겠습니까?<br> 태스크 삭제 시 다음 태스크로 연결한 설정도 함께 삭제됩니다.";
	
	mui.confirm(deleteAlertTxt, {
	    onClose: function(isOk) {
	      if (isOk) {
	    	  var nodes = scenario.nodes;
	    	  
	    	  for (var i = 0; i < nodes.length; i++) {
	    		  if(nodes[i].attr[0] != null){
	    			  var intentList = nodes[i].attr[0].intentList;
	    			  
	    			  nodes[i].attr[0].intentList = nodes[i].attr[0].intentList.filter((element, index) => {
	    				  return nodes[i].attr[0].intentList[index].nextTask != nodeLabel;
	    			  });
	    		   }
	    	  }
	    	  scenario.nodes = scenario.nodes.filter((element, index) => {
	    		  return scenario.nodes[index].id != nodeId;
	    	  });
	    	  scenario.edges = scenario.edges.filter((element, index) => {
	    		  return scenario.edges[index].source != nodeId;
	    	  });
	    		
	    	  $('#vb_wrap').addClass('loading');
	    	  $(el).text("삭제중");
	    	
	    	  $.ajax({
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
	    		  $('#vb_wrap').removeClass('loading');
	    	      changeContents(menuNow);
	    	  }).catch(function (response) {
	    	      alert('<spring:message code="MESSAGE.APPLY.CANTSAVE" javaScriptEscape="true"/>');
	    	      console.log('apply() status code: ' + response.status);
	    	  });
	       }
	    }
	});
}
