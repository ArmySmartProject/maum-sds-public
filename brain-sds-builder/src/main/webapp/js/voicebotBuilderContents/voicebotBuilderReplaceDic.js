$(document).ready(function(){
  $('#table_replace_list th.sort').on('click', function(){
    var th = $(this);
    var ths = $('#table_replace_list th.sort');
    var text = th.text();
    if(text == "치환 전"){
    	$("#orderCol").val("Before");
    }else if(text == "치환 후"){
    	$("#orderCol").val("After");
    }
    
    //정렬순서 : 기본 > 오름차순 > 내림차순
    if ( th.hasClass('up') ) {
      ths.removeClass('up down');
      th.removeClass('up');
      th.addClass('down');
      $("#orderSort").val("DESC");
      replaceDictList(data.host, $("#currentPage").val());
    } else if ( th.hasClass('down') ) {
      ths.removeClass('up down');
      th.removeClass('down');
      $("#orderSort").val("");
      replaceDictList(data.host, $("#currentPage").val());
    } else {
      ths.removeClass('up down');
      th.addClass('up');
      $("#orderSort").val("ASC");
      replaceDictList(data.host, $("#currentPage").val());
    }
  });
  
  // AMR 돋보기 아이콘이 있는 검색 영역에서 엔터 시 검색버튼 눌림
  $('.ipt_txt.search').on('keyup', function(){
		$(this).next('#searchReplaceDict').trigger('click');
  });
  
  replaceDictList(data.host, 1);

  $('#excelFile_replaceDict').change(function (e) {
	  var fileName = e.target.files[0].name;
      $('input[id="excel_file_dict_label"]').val(fileName);
  });
  
});



function replaceDictList(host, cp){
	
	var lang = data.lang;
	
	var obj = new Object();
	obj.host = host;
	obj.lang = lang;
	obj.cp = cp;
	obj.orderCol = $("#orderCol").val();
	obj.orderSort = $("#orderSort").val();
	obj.searchReplaceDict = $("input[name='searchReplaceDict']").val();
	
	$.ajax({
	    url: "voiceBot/getReplaceDictList",
	    data: JSON.stringify(obj),
	    type: "POST",
	    contentType: 'application/json'
	  }).then(function (response) {
		  var replaceDictList = response.replaceDictList;
		  var paging = response.paging;
		  
		  $(".pagination").empty();
		  var pagingHtml = "";
		  
		  pagingHtml += '<button type="button" class="first" onclick="goPage('+ host + ',1)"><span>&laquo;</span></button>';
		  pagingHtml += '<button type="button" class="prev" onclick="goPage('+ host + ',' + paging.prevPage +')"><span>&lsaquo;</span></button>';
		  pagingHtml += '<div class="pages">';
		  
		  for (var i = paging.pageRangeStart; i<=paging.pageRangeEnd; i++){
              if(i == paging.currentPage){
            	  pagingHtml += '<span class="page active">'+ i +'</span>';
              }else{
            	  pagingHtml += '<span class="page" onclick="goPage('+ host + ',' + i +')">'+ i +'</span>';
              }
          }

		  pagingHtml += '</div>';
		  pagingHtml += '<button type="button" class="next" onclick="goPage('+ host + ',' + paging.nextPage +')"><span>&rsaquo;</span></button>';
		  pagingHtml += '<button type="button" class="last" onclick="goPage('+ host + ',' + paging.totalPage +')"><span>&raquo;</span></button>';
		  pagingHtml += '</div>';
		  
		  $(".pagination").append(pagingHtml);
		  
		  $("#replaceDictTable tbody").empty();
		  
		  var replaceDictHtml = "";
		  
		  if(replaceDictList.length > 0){
			  for(var i = 0; i < replaceDictList.length; i++){
				  replaceDictHtml += "<tr>";
				  replaceDictHtml += "<td>"+replaceDictList[i].RNUM+"</td>";
				  replaceDictHtml += "<td><input type='text' class='ipt_txt replaceBeforeTxt' value=\""+replaceDictList[i].Before+"\"></td>";
				  replaceDictHtml += "<td><input type='text' class='ipt_txt replaceAfterTxt' value=\""+replaceDictList[i].After+"\"></td>";
				  replaceDictHtml += "<td>";
				  replaceDictHtml += "<button type='button' class='btn_line_save' onclick='updateReplaceDict(\""+replaceDictList[i].No+"\", this)'>수정</button>";
				  replaceDictHtml += "<button type='button' class='btn_line_warning' onclick='deleteReplaceDict(\""+replaceDictList[i].No+"\")'>삭제</button>";
				  replaceDictHtml += "</td>";
				  replaceDictHtml += "</tr>";
			  }
		  }else{
			  replaceDictHtml += "<tr>";
			  replaceDictHtml += "<td colspan='4'>등록 된 데이터가 없습니다.</td>";
			  replaceDictHtml += "</tr>";
		  }
		  
		  $("#replaceDictTable tbody").append(replaceDictHtml);
		  
	  }).catch(function () {
	    console.log('[getReplaceDictList error]');
	  });
}


function goPage(host, cp){
    if( cp != 0){
        $('#currentPage').val(cp);
    }
    replaceDictList(host, cp);
}
// 치환 사전 추가
function addReplaceDict(){
	var beforeTxt = $("#addBeforeTxt").val();
	var afterTxt = $("#addAfterTxt").val();
	var host = data.host;
	
	if(beforeTxt == null || beforeTxt == ""){
		mui.alert("추가 할 치환 전 텍스트를 입력해주세요.");
		return false;
	}
	if(afterTxt == null || afterTxt == ""){
		mui.alert("추가 할 치환 후 텍스트를 입력해주세요.");
		return false;
	}
	
	var obj = new Object();
	
	obj.host = host;
	obj.lang = data.lang;
	obj.replaceDictBefore = beforeTxt;
	obj.replaceDictAfter = afterTxt;
	
	$.ajax({
		url: "voiceBot/addReplaceDict",
		data: JSON.stringify(obj),
		type: "POST",
		contentType: 'application/json'
    }).then(function (response) {
	    if(response == 1){
	    	mui.alert("치환사전이 추가 되었습니다.");
	    	$(".sort").removeClass('up down');
	    	$(".sort").removeClass('down');
	    	$("#orderCol").val("");
	    	$("#orderSort").val("");
	    	$("#addBeforeTxt").val("");
	    	$("#addAfterTxt").val("");
		    replaceDictList(host, 1);
	    }
	  
    }).catch(function () {
      console.log('[addReplaceDict error]');
    });
}

function updateReplaceDict(No, el){
	var cp = $("#currentPage").val();
	var host = data.host;
	var beforeTxt = $(el).parent().parent().find('.replaceBeforeTxt').val();
	var afterTxt = $(el).parent().parent().find('.replaceAfterTxt').val();
	
	if(beforeTxt == null || beforeTxt == ""){
		mui.alert("수정 할 치환 전 텍스트를 입력해주세요.");
		return false;
	}
	
	if(afterTxt == null || afterTxt == ""){
		mui.alert("수정 할 치환 후 텍스트를 입력해주세요.");
		return false;
	}
	
	var obj = new Object();
	obj.no = No;
	obj.host = host;
	obj.lang = data.lang;
	obj.replaceDictBefore = beforeTxt;
	obj.replaceDictAfter = afterTxt;
	
	mui.confirm('치환 사전 정보를 수정하시겠습니까?', {
		onClose: function(isOk) {
			if (isOk) {
				$.ajax({
				    url: "voiceBot/updateReplaceDict",
				    data: JSON.stringify(obj),
				    type: "POST",
				    contentType: 'application/json'
				}).then(function (response) {
					if(response == 1){
						mui.alert("치환사전이 수정되었습니다.");
						$(".sort").removeClass('up down');
						$(".sort").removeClass('down');
						$("#orderCol").val("");
						$("#orderSort").val("");
						replaceDictList(host, cp);
					}
					  
				}).catch(function () {
						console.log('[updateReplaceDict error]');
				});
			}
	    }
	});
}

function deleteReplaceDict(No){
	var host = data.host;
	var lang = data.lang;
	
	var obj = new Object();
	obj.no = No;
	obj.host = host;
	obj.lang = lang;
	
	mui.confirm('치환 사전을 삭제하시겠습니까?', {
		onClose: function(isOk) {
			if (isOk) {
				$.ajax({
				    url: "voiceBot/deleteReplaceDict",
				    data: JSON.stringify(obj),
				    type: "POST",
				    contentType: 'application/json'
				}).then(function (response) {
					if(response == 1){
						mui.alert("치환사전이 삭제되었습니다.");
						$(".sort").removeClass('up down');
						$(".sort").removeClass('down');
						$("#orderCol").val("");
						$("#orderSort").val("");
						replaceDictList(host, 1);
					}
					  
				}).catch(function () {
						console.log('[updateReplaceDict error]');
				});
	        }
	    }
	});
	
}

function searchReplaceDict(){
	$(".sort").removeClass('up down');
	$(".sort").removeClass('down');
	$("#orderCol").val("");
	$("#orderSort").val("");
	replaceDictList(data.host, 1);
}

//엑셀 다운로드
function excelDown() {
	if($('.replaceBeforeTxt').length == 0){
		mui.alert('다운로드 할 데이터가 없습니다.');
	} else {
		var form = document.downloadReplaceDict;
		// scenario = getScenarioJsonData(); //jsplumb에서 가져옴
		$("#replaceDictCol").val($("#orderCol").val());
		$("#replaceDictSort").val($("#orderSort").val());
		$("#replaceDict_host").val(data.host);
		$("#replaceDict_lang").val(data.lang);

		form.submit();
	}
}

function uploadExcel(){
	  $("#replaceDictHost").val(data.host);
	  $("#replaceDictLang").val(data.lang);

	  var txt = '저장';
	  var changeTxt = '저장중';
	  var file = $("#excelFile_replaceDict").val();

	  if (file === "" || file === null) {
	      mui.alert('업로드 할 파일을 선택해주세요.');
	  } else {
		  $('#btn_upload').text(changeTxt).addClass('gradient');
		  var options = {
			  success: function (response) {
				  $('#orderCol').val("");
				  $('#orderSort').val("");
				  $('#btn_upload').text(txt).removeClass('gradient');
				  mui.alert(response.status);
				  modalClose($('#voicebot_replace_upload'));
				  replaceDictList(data.host, 1);

			  }, error: function (response) {
				  $('#btn_upload').text(txt).removeClass('gradient');
				  mui.alert(response.status);
			  },
			  type: "POST"
		  };
		  $("#replaceDictUpload").ajaxSubmit(options);
	  }
	}


