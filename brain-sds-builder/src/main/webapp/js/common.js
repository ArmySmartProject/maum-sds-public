String.prototype.trim = function() {
    return this.replace(/(^\s*)|(\s*$)/gi, "");
}

Array.prototype.contains = function(elem) {
	for (var i in this) {
		if (this[i] == elem) return true;
	}
	return false;
}

function gfn_isNull(str) {
	if (str == null) return true;
	if (str == "NaN") return true;
	if (new String(str).valueOf() == "undefined") return true;    
    var chkStr = new String(str);
    if( chkStr.valueOf() == "undefined" ) return true;
    if (chkStr == null) return true;    
    if (chkStr.toString().length == 0 ) return true;   
    return false; 
}

function ComSubmit(opt_formId) {
	this.formId = gfn_isNull(opt_formId) == true ? "commonForm" : opt_formId;
	this.url = "";
	
	if(this.formId == "commonForm"){
		$("#commonForm").empty();
		//$("#commonForm")[0].reset();
	}
	
	this.setUrl = function setUrl(url){
		this.url = url;
	};
	
	this.addParam = function addParam(key, value){
		$("#"+this.formId).append("<input type='hidden' name='"+key+"' id='"+key+"' value='' >");
		$("#"+this.formId + " #" + key).val(value);
	};
	
	this.submit = function submit(){
		var frm = $("#"+this.formId)[0];
		frm.action = this.url;
		frm.method = "post";
		frm.submit();	
	};
}

function fnpreventDefault(event) {
	if (event != undefined) {
		if(event.preventDefault) {
	        // 최신 IE, 타 브라우저
	        event.preventDefault();
	    } else {
	        // 구버전 IE를 위하여 사용한다
	        event.returnValue = false;
	    }		
	}		
}

function fnstopPropagation(event) {
	if(event.preventDefault) {
        // 최신 IE, 타 브라우저
		event.stopPropagation();
    } else {
        // 구버전 IE를 위하여 사용한다
        event.returnValue = false;
    }	
}

function fnShowHide(obj) {
	if($(obj).css('display') == 'none')
		$(obj).show();
	else
		$(obj).hide();	
}

function fnWinPop(url, title, width, height, addleft, addtop, resize) {	    
    var top = (window.outerHeight - height) / 2 - addtop;
    var left = (window.outerWidth - width) / 2 + addleft;
    var strresize = 'no'; 
    if (resize)
    	strresize = 'yes';
	
    var strFeature;
    strFeature = 'height=' + height + ',width=' + width + ',menubar=no,toolbar=no,location=no,resizable=' + strresize + ',status=no,scrollbars=yes,top=' + top + ',left=' + left

    var win = window.open(
				url,
				title,
				strFeature
			);
    return win;
}

function fnWinClose() {
	window.close();
}

function fnCursorEnd(el) {
    el.focus();
    if (typeof window.getSelection != "undefined"
            && typeof document.createRange != "undefined") {
        var range = document.createRange();
        range.selectNodeContents(el);
        range.collapse(false);
        var sel = window.getSelection();
        sel.removeAllRanges();
        sel.addRange(range);
    } else if (typeof document.body.createTextRange != "undefined") {
        var textRange = document.body.createTextRange();
        textRange.moveToElementText(el);
        textRange.collapse(false);
        textRange.select();
    }
}
var gfv_ajaxCallback = "";
function ComAjax(opt_formId){
	this.url = "";		
	this.formId = gfn_isNull(opt_formId) == true ? "commonForm" : opt_formId;
	this.param = "";
	this.paramobj;
	this.async = true; 
		
	if(this.formId == "commonForm"){
		//$("#commonForm")[0].reset();
		$("#commonForm").empty();
	}
	
	this.setUrl = function setUrl(url){
		this.url = url;
	};
	
	this.setCallback = function setCallback(callBack){
		fv_ajaxCallback = callBack;
	};
	
	this.setParamObj = function setParamObj(obj) {
		this.paramobj = obj;
	}

	this.addParam = function addParam(key,value){ 
		this.param = this.param + "&" + key + "=" + encodeURIComponent(value); 
	};
	
	this.setAsync = function setUrl(async){
		this.async = async;
	};
	
	this.ajax = function ajax(){
		if(this.formId != "commonForm"){
			this.param += "&" + $("#" + this.formId).serialize();
		}
		$.ajax({
			url : this.url,    
			type : "POST",   
			data : this.param,
			async : this.async,
			dataType : "json", //2017.7.24 추가
			success : function(data, status) {
				try {
					if(typeof(fv_ajaxCallback) == "function"){
						fv_ajaxCallback(data, paramobj);
					}
					else {
						eval(fv_ajaxCallback + "(data);");
					}	
				} catch (e) {
					// TODO: handle exception
				}
			},
			error : function(e) {
				alert('작업 중 오류가 발생 하였습니다.');
				if (this.url.indexOf('dialogslearning.do') > -1) {
					$('#divleanbox').text('학습 중 오류가 발생하였습니다.');
				}
			}
		});
	};
}

/*
divId : 페이징 태그가 그려질 div
pageIndx : 현재 페이지 위치가 저장될 input 태그 id
recordCount : 페이지당 레코드 수
totalCount : 전체 조회 건수 
eventName : 페이징 하단의 숫자 등의 버튼이 클릭되었을 때 호출될 함수 이름
*/
var gfv_pageIndex = null;
var gfv_eventName = null;
function gfn_renderPaging(params){
	var divId = params.divId; //페이징이 그려질 div id
	gfv_pageIndex = params.pageIndex; //현재 위치가 저장될 input 태그
	var totalCount = params.totalCount; //전체 조회 건수
	var currentIndex = $("#"+params.pageIndex).val(); //현재 위치
	if($("#"+params.pageIndex).length == 0 || gfn_isNull(currentIndex) == true){
		currentIndex = 1;
	}
	
	var recordCount = params.recordCount; //페이지당 레코드 수
	if(gfn_isNull(recordCount) == true){
		recordCount = 10;
	}
	var totalIndexCount = Math.ceil(totalCount / recordCount); // 전체 인덱스 수
	gfv_eventName = params.eventName;
	
	$("#"+divId).empty();
	var preStr = "<span class='left'>";
	var postStr = "<span class='right'>";
	var str = "";
	
	var first = (parseInt((currentIndex-1) / 10) * 10) + 1;
	var last = (parseInt(totalIndexCount/10) == parseInt(currentIndex/10)) ? totalIndexCount%10 : 10;
	var prev = (parseInt(currentIndex) - 1 <= first) ? 1 : parseInt(currentIndex) - 1;
	var next = (parseInt(currentIndex) + 1 >= last) ? last : parseInt(currentIndex) + 1;
	/*var prev = (parseInt((currentIndex-1)/10)*10) - 9 > 0 ? (parseInt((currentIndex-1)/10)*10) - 9 : 1; 
	var next = (parseInt((currentIndex-1)/10)+1) * 10 + 1 < totalIndexCount ? (parseInt((currentIndex-1)/10)+1) * 10 + 1 : totalIndexCount;*/
	
	if(totalIndexCount > 1) {
		preStr += '<a href="#" class="front" onclick="_movePage(1);"><img src="../images/btn_front.png" /></a>' +
		'<a href="#" class="prev"><img src="../images/btn_prev.png" onclick="_movePage('+prev+')" /></a>';
	}
	/*if(totalIndexCount > 10){ //전체 인덱스가 10이 넘을 경우, 맨앞, 앞 태그 작성
		preStr += '<a href="#" class="front" onclick="_movePage(1);"><img src="../images/btn_front.png" /></a>' +
				'<a href="#" class="prev"><img src="../images/btn_prev.png" onclick="_movePage('+prev+')" /></a>';				
	}		
	else if(totalIndexCount <=10 && totalIndexCount > 1){ //전체 인덱스가 10보다 작을경우, 맨앞 태그 작성
		preStr += '<a href="#" class="front" onclick="_movePage(1);"><img src="../images/btn_front.png"/></a>';
	}*/
	preStr += "</span>";
	
	if(totalIndexCount > 1) {		
		postStr += '<a href="#" class="next"><img src="../images/btn_next.png" onclick="_movePage('+next+');"; /></a>' +
		'<a href="#" class="back"><img src="../images/btn_back.png" onclick="_movePage('+totalIndexCount+');" /></a>';
	}
	/*if(totalIndexCount > 10){ //전체 인덱스가 10이 넘을 경우, 맨뒤, 뒤 태그 작성
		postStr += '<a href="#" class="next"><img src="../images/btn_next.png" onclick="_movePage('+next+');"; /></a>' +
				'<a href="#" class="back"><img src="../images/btn_back.png" onclick="_movePage('+totalIndexCount+');" /></a>';		
	}
	else if(totalIndexCount <=10 && totalIndexCount > 1){ //전체 인덱스가 10보다 작을경우, 맨뒤 태그 작성
		postStr += '<a href="#" class="next"><img src="../images/btn_next.png" onclick="_movePage('+totalIndexCount+');"; /></a>';
	}*/
	postStr += "</span>";
	
	str += '<span class="num_group">';
	for(var i=first; i<(first+last); i++){
		if(i != currentIndex){
			str += '<a href="#" onclick="_movePage('+ i +')">'+ i +'</a>&nbsp;';
		}
		else{
			str += '<a class="on" href="#" onclick="_movePage('+ i +')">'+ i +'</a>&nbsp;';
		}
	}
	str += '</span>';
	$("#"+divId).append(preStr + str + postStr);
}
/*function gfn_renderPaging(params){
	var divId = params.divId; //페이징이 그려질 div id
	gfv_pageIndex = params.pageIndex; //현재 위치가 저장될 input 태그
	var totalCount = params.totalCount; //전체 조회 건수
	var currentIndex = $("#"+params.pageIndex).val(); //현재 위치
	if($("#"+params.pageIndex).length == 0 || gfn_isNull(currentIndex) == true){
		currentIndex = 1;
	}
	
	var recordCount = params.recordCount; //페이지당 레코드 수
	if(gfn_isNull(recordCount) == true){
		recordCount = 20;
	}
	var totalIndexCount = Math.ceil(totalCount / recordCount); // 전체 인덱스 수
	gfv_eventName = params.eventName;
	
	$("#"+divId).empty();
	var preStr = "";
	var postStr = "";
	var str = "";
	
	var first = (parseInt((currentIndex-1) / 10) * 10) + 1;
	var last = (parseInt(totalIndexCount/10) == parseInt(currentIndex/10)) ? totalIndexCount%10 : 10;
	var prev = (parseInt((currentIndex-1)/10)*10) - 9 > 0 ? (parseInt((currentIndex-1)/10)*10) - 9 : 1; 
	var next = (parseInt((currentIndex-1)/10)+1) * 10 + 1 < totalIndexCount ? (parseInt((currentIndex-1)/10)+1) * 10 + 1 : totalIndexCount;
	
	if(totalIndexCount > 10){ //전체 인덱스가 10이 넘을 경우, 맨앞, 앞 태그 작성
		preStr += "<a href='#this' class='pad_5' onclick='_movePage(1)'>[<<]</a>" +
				"<a href='#this' class='pad_5' onclick='_movePage("+prev+")'>[<]</a>";
	}
	else if(totalIndexCount <=10 && totalIndexCount > 1){ //전체 인덱스가 10보다 작을경우, 맨앞 태그 작성
		preStr += "<a href='#this' class='pad_5' onclick='_movePage(1)'>[<<]</a>";
	}
	
	if(totalIndexCount > 10){ //전체 인덱스가 10이 넘을 경우, 맨뒤, 뒤 태그 작성
		postStr += "<a href='#this' class='pad_5' onclick='_movePage("+next+")'>[>]</a>" +
					"<a href='#this' class='pad_5' onclick='_movePage("+totalIndexCount+")'>[>>]</a>";
	}
	else if(totalIndexCount <=10 && totalIndexCount > 1){ //전체 인덱스가 10보다 작을경우, 맨뒤 태그 작성
		postStr += "<a href='#this' class='pad_5' onclick='_movePage("+totalIndexCount+")'>[>>]</a>";
	}
	
	for(var i=first; i<(first+last); i++){
		if(i != currentIndex){
			str += "<a href='#this' class='pad_5' onclick='_movePage("+i+")'>"+i+"</a>";
		}
		else{
			str += "<strong><a href='#this' class='pad_5' onclick='_movePage("+i+")'>"+i+"</a></strong>";
		}
	}
	$("#"+divId).append(preStr + str + postStr);
}*/

function _movePage(value){
	$("#"+gfv_pageIndex).val(value);
	if(typeof(gfv_eventName) == "function"){
		gfv_eventName(value);
	}
	else {
		eval(gfv_eventName + "(value);");
	}
}

function fnLearningCallBack(data) {
	alert(data.STATUS);
}

function fnStarntCallBack(data) {
	alert(data.STATUS);
}

function fnLoading() {
	if ($('#divLoading').css('display') == 'none')
		$('#divLoading').show();
	else
		$('#divLoading').hide();
}

function fnSaveComplete(data) {
	alert('저장 되었습니다.');
	fnLoading();
	iSeqNo = data.SEQ_NO;
	bWorkChk = false;
}

function setCookie(cookieName, value, exdays){
    var exdate = new Date();
    exdate.setDate(exdate.getDate() + exdays);
    var cookieValue = encodeURI(value) + ((exdays==null) ? "; path=/;" : "; path=/; expires=" + exdate.toGMTString());
    document.cookie = cookieName + "=" + cookieValue;
}
 
function deleteCookie(cookieName){
    var expireDate = new Date();
    expireDate.setDate(expireDate.getDate() - 1);
    document.cookie = cookieName + "= " + "; path=/; expires=" + expireDate.toGMTString();
}
 
function getCookie(cookieName) {
	cookieName = cookieName;
    var cookieData = document.cookie;
    
    return reqCookie(cookieName, cookieData, 0);
}

function reqCookie(cookieName, cookieData, idx) {
	var arrcookie = cookieData.split(';');
	var arritem;
	var cookieValue = '';
	
	for (var i = 0; i < arrcookie.length; i++) {
		arritem = arrcookie[i].split('=');
		if (arritem[0].trim() == cookieName) {
			cookieValue = arritem[1];
		}
	}
		
    return decodeURI(cookieValue);
}

function uniqid(prefix, more_entropy) {
    if( prefix == undefined ) prefix = "";
    if( typeof prefix == undefined ) prefix = "";

    var retId;
    var formatSeed = function (seed, reqWidth) {
        seed = parseInt(seed,10).toString(16); // to hex str
        if (reqWidth < seed.length) { // so long we split
            return seed.slice(seed.length - reqWidth);
        }
        if (reqWidth > seed.length) { // so short we pad
            return Array(1 + (reqWidth - seed.length)).join('0')+seed;
        }
        return seed;
    };

    // BEGIN REDUNDANT
    if (!this.php_js) {
        this.php_js = {};
    }
    // END REDUNDANT
    if (!this.php_js.uniqidSeed) { // init seed with big random int
        this.php_js.uniqidSeed = Math.floor(Math.random() * 0x75bcd15);
    }
    this.php_js.uniqidSeed++;

    retId  = prefix; // start with prefix, add current milliseconds hex string
    retId += formatSeed(parseInt(new Date().getTime()/1000,10),8);
    retId += formatSeed(this.php_js.uniqidSeed,5); // add seed hex string

    if (more_entropy) {
        // for more entropy we add a float lower to 10
        retId += (Math.random()*10).toFixed(8).toString();
    }

    return retId;
}

function fnGetRandomColor() {
	var color = "#"; 
    var max = Math.pow( 256, 3 ); 
	var random = Math.floor( Math.random() * max ).toString( 16 ); 
	var gap = 6 - random.length; 
	
	if ( gap > 0 ) {    for ( var x = 0; x < gap; x++ ) color += "0";    } 
	
	return color + random; 
}

function fnHexc(colorval) {
    var parts = colorval.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
    delete(parts[0]);
    for (var i = 1; i <= 3; ++i) {
        parts[i] = parseInt(parts[i]).toString(16);
        if (parts[i].length == 1) parts[i] = '0' + parts[i];
    }
    color = '#' + parts.join('').toUpperCase();

    return color;
}

function fnReplaceSpace(text) {
	return text.replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/\s+|&nbsp;/g, ' ').replace('&amp;', '&');
}

function fnLayerPop(el, type){
	if (type == 'o') {		
		var temp = $('#' + el);
		temp.show();
		var bg = temp.parent().children().eq(0).hasClass('bg');	//dimmed 레이어를 감지하기 위한 boolean 변수
	
		if(bg){
			$('#layer').fadeIn();	//'bg' 클래스가 존재하면 레이어가 나타나고 배경은 dimmed 된다. 
		}else{
			temp.fadeIn();
		}
	
		// 화면의 중앙에 레이어를 띄운다.
		if (temp.outerHeight() < $(document).height() ) temp.css('margin-top', '-'+temp.outerHeight()/2+'px');
		else temp.css('top', '0px');
		if (temp.outerWidth() < $(document).width() ) temp.css('margin-left', '-'+temp.outerWidth()/2+'px');
		else temp.css('left', '0px');
	}
	else {
		$('#' + el).hide();
		$('#layer').fadeOut();
	}
	/*temp.find('a.cbtn').click(function(e){
		if(bg){
			$('.layer').fadeOut(); //'bg' 클래스가 존재하면 레이어를 사라지게 한다. 
		}else{
			temp.fadeOut();
		}
		e.preventDefault();
	});

	$('.layer .bg').click(function(e){	//배경을 클릭하면 레이어를 사라지게 하는 이벤트 핸들러
		$('.layer').fadeOut();
		e.preventDefault();
	});*/

}

/*function fnLayerPop(id, type){
	if (type == 'o') {
		$('#bg').show();
		$('#' + id).show();
	}
	else {
		$('#bg').hide();
		$('#' + id).hide();
	}
	
}*/

//SLOT 체크
function fnSlotValidation(str, slotdata) {
	var arrReturn = new Array();
	var arrReg = str.match(/<[a-zA-Z0-9]+.[a-zA-Z0-9]+>/g);			
	
	if (arrReg == null) {
		arrReturn[0] = true;
	}
	else {
		var tempclass = '';
		var tempslot = '';
		arrReturn[0] = true;
		
		for (var i = 0; i < arrReg.length; i++) {
			arrReg[i] = arrReg[i].replace(/[<>]/g, ''); 
		
			tempclass = arrReg[i].substring(0, arrReg[i].indexOf('.'));
			tempslot = arrReg[i].substring(arrReg[i].indexOf('.') + 1);
			
			if (slotdata[tempclass] == null) {
				arrReturn[0] = false;
				arrReturn.push(arrReg[i]);
			}
			else {
				var bChk = false;
				for (var j = 0; j < slotdata[tempclass].length; j++) {				
					if(slotdata[tempclass][j].SLOT_NAME == tempslot) {
						bChk = true;
					}
				}
				if (!bChk) {
					arrReturn[0] = false;
					arrReturn.push(arrReg[i]);
				}
			}
		}		
	}
	return arrReturn;
}

function fnUpLoadIntent() {	
	var fupload = document.getElementById('fupload');
	var filetype = '';
	if (fupload.value == '') {
		alert('파일을 선택해주세요.');
		return;
	}
	if (fupload.files[0].type != 'text/plain') {
		alert('txt파일만 가능합니다.');
		return;
	}		
	
	fnUpLoadIntentSubmit();
}

function fnSetNaviTitle(title) {
	if ('<%= session.getAttribute(\"BIZ_CHATBOT\")%>' == null) {
		$(top.document).find('#spNavi').text(title);
	}
}

function fnMain() {
	var comSubmit = new ComSubmit();
	comSubmit.setUrl("<c:url value='/builder/main.go' />");
	comSubmit.submit();		
}

function fnGetSlotColor(classSeq, slotName, url) {			
	var comAjax = new ComAjax();
	comAjax.addParam("CLASS_SEQ_NO", classSeq);
	comAjax.addParam("SLOT_NAME", slotName);
	comAjax.setUrl(url);			
	comAjax.setCallback("fnGetSlotColorCallBack");
	comAjax.setAsync(false);
	comAjax.ajax();
}

function fnGetSlotColorCallBack(data) {
	try {
		$('#hidSlotColor').val(data.COLOR);
	} catch (e) {
		// TODO: handle exception
	}	
}

function fnCountCheckCallBack(data) {
	CheckCnt = data.CNT;
	CheckCntMax = data.MAX_CNT;
}
