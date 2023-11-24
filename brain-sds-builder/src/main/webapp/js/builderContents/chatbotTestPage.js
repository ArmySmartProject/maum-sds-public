var nowDebug = [
    []
]
var nowDebugPK = [];
var nowIndexDebug = 0;
var nowResetClicked = false;

function setCookie(name, value, expireminute ) {
    var exdate = new Date();
    exdate.setMinutes(exdate.getMinutes()+expireminute);
    document.cookie = name +  "=" + escape(value)
        + ((expireminute == null) ? "" : ";expires=" + exdate.toUTCString())
        + "; SameSite=None; Secure";
}

function iframeStr(host){
    if(host!=-1){
        var btnOn = $('.chatbot_builder .btn_test_on');
        $('.chatbot_builder').addClass('test_on');
        btnOn.addClass('hide');
        nowResetClicked = false;
        let url = "getTestPage?host=" + host;
        $('#testIframe').attr('src', url);
        nowDebug = [[]];
        nowDebugPK = [];
        resetList();
        overlayTutorial(10000);
    }

}

function iframeReset() {
    $('#testIframe').get(0).contentWindow.postMessage({data:"resetSession"},"*");
    nowResetClicked = false;
}

window.addEventListener('message', function(e) {
    if(e.data.data==="chatbot_msg"){
        try{
            addChatbotDebugByJson(e.data.jsonDebug);
        }catch (e) {
            console.log(e);
        }
    }
    if(e.data.data==="resetSessionCallback"){
        if(nowResetClicked){
            reloadTestPageIframe();
        }
    }
});

function addChatbotDebugByJson(e){
    console.log("ChatbotDebug[By Json]")
    var debugJson = JSON.parse(e.jsonDebug);
    try {
        let elem = [
            [0, "INPUT"],
            [1, "input text",
                debugJson.input != undefined ? debugJson.input : ""],
            [1, "type", debugJson.type != undefined ? debugJson.type : ""],
            [2, "replace", []],
            [0, "INTENT"],
            [1, "engine",
                debugJson.engine != undefined ? debugJson.engine : ""],
            [1, "model", debugJson.model != undefined ? debugJson.model : ""],
            [1, "prob", debugJson.prob != undefined ? debugJson.prob.toString() : ""],
            [0, "TASK"],
            [1, "prev task",
                debugJson.prevIntent != undefined ? debugJson.prevIntent : " "],
            [1, "task rel",
                debugJson.taskRel != undefined ? debugJson.taskRel : " "],
            [1, "task", e.intent != undefined ? e.intent : ""],
            [0, "ANSWER"],
            [1, "text", e.answer != undefined ? e.answer : ""],
            [0, "SDS LOG"],
            [4, debugJson.sdsLog != undefined ? debugJson.sdsLog : ""]
        ];
        if (e.intent === "챗봇공지사항" || e.intent === "선톡") {
            return;
        }
        if (debugJson.taskRel != undefined) {
            if (debugJson.taskRel.toString().includes("(의도)")) {
                elem.push([3, "WARN",
                    "부합한 태스크 관계가 없습니다. 의도의 이름과 같은 이름의 답변이 연결 되었습니다."])
            }
            if (debugJson.taskRel.toString().includes("(발화)")) {
                elem.push([3, "WARN",
                    "부합한 태스크 관계가 없습니다. 채팅 입력값과 같은 이름의 답변이 연결 되었습니다."])
            }
        }
        if (debugJson.replace != undefined) {
            let debugReplaceList = debugJson.replace.toString().split("<cell>");
            for (let ii = 0; ii < debugReplaceList.length; ii++) {
                if (debugReplaceList[ii].length > 0) {
                    elem[3][2].push(debugReplaceList[ii].split("<split>"));
                }
            }
        }

        if (nowDebug[0].length == 0) {
            nowDebug[0] = elem;
        } else {
            nowDebug.push(elem);
        }
    }catch (e) {

    }
    renderDebugList(-99);
}

function makeResultToDebug(result){
    let checkNowDebugPK = nowDebugPK.find((item) => {
        return item === result.id;
        });
    if(checkNowDebugPK!=undefined){
        return;
    }
    let debugJson;
    try{
        debugJson = JSON.parse(result.debugJson);
    }catch(e){
        debugJson = new Object();
    }

    try{
        let elem = [
            [0,"INPUT"],
            [1,"input text",debugJson.input!=undefined ? debugJson.input : ""],
            [1,"type",debugJson.type!=undefined ? debugJson.type : ""],
            [2,"replace",[]],
            [0,"INTENT"],
            [1,"intent",result.bertIntent!=undefined ? result.bertIntent : ""],
            [1,"engine",debugJson.engine!=undefined ? debugJson.engine : ""],
            [1,"model",debugJson.model!=undefined ? debugJson.model : ""],
            [1,"prob",result.prob!=undefined ? result.prob.toString() : ""],
            [0,"TASK"],
            [1,"prev task",debugJson.prevIntent!=undefined ? debugJson.prevIntent : " "],
            [1,"task rel",debugJson.taskRel!=undefined ? debugJson.taskRel : " "],
            [1,"task",result.intent!=undefined ? result.intent : ""],
            [0,"ANSWER"],
            [1,"text",result.answer!=undefined ? result.answer : ""],
        ];
        if(result.intent==="챗봇공지사항" || result.intent==="선톡"){
            return;
        }
        if(debugJson.taskRel!=undefined){
            if(debugJson.taskRel.toString().includes("(의도)")){
                elem.push([3,"WARN","부합한 태스크 관계가 없습니다. 의도의 이름과 같은 이름의 답변이 연결 되었습니다."])
            }if(debugJson.taskRel.toString().includes("(발화)")){
                elem.push([3,"WARN","부합한 태스크 관계가 없습니다. 채팅 입력값과 같은 이름의 답변이 연결 되었습니다."])
            }
        }
        // console.log(elem)
        if(debugJson.replace != undefined){
            let debugReplaceList = debugJson.replace.toString().split("<cell>");
            for(let ii = 0; ii<debugReplaceList.length; ii++){
                if(debugReplaceList[ii].length>0){
                    elem[3][2].push(debugReplaceList[ii].split("<split>"));
                }
            }
        }

        if(nowDebug[0].length==0){
            nowDebug[0] = elem;
        }
        else{
            nowDebug.push(elem);
        }
        if(result.id!=undefined){
            nowDebugPK.push(result.id);
        }
    }catch(e){
        console.log("SessionError")
    }
}

function renderDebugList(nowIndex){
    if(nowIndex == -99){
        renderDebugList(nowDebug.length-1);
    }else if (nowIndex == -1 || nowIndex == nowDebug.length) {
    }else
    {
        nowIndexDebug = nowIndex;
        $("#nowDebugIndex").text(nowIndexDebug+1);
        $("#nowDebugStack").text(nowDebug.length)
        let nowElem = nowDebug[nowIndex];
        let nowHTML = "";
        let nowElemStack = "";
        for(let ii = 0; ii<nowElem.length; ii++){
            if(nowElem[ii][0]===0){//카테고리
                nowHTML += (nowElemStack.length>0 ? "<dd>"+nowElemStack+"</dd>" : "") + "<dt>"+nowElem[ii][1]+"</dt>\n";
                nowElemStack = "";
            }else if (nowElem[ii][0]===1){//일반 내용
                if(nowElem[ii][2].length>0){
                    nowElemStack += "<p><span class=\"highlight\">"+nowElem[ii][1]+"</span><em>"+nowElem[ii][2]+"</em></p>\n"
                }
            }else if (nowElem[ii][0]===2){//replace
                if(nowElem[ii][2].length>0){
                    let nowText = "";
                    for(let jj = 0; jj<nowElem[ii][2].length; jj++){
                        nowText += nowElem[ii][2][jj][0] + " > " + nowElem[ii][2][jj][1];
                        if(jj!=nowElem[ii][2].length-1){
                            nowText += " , "
                        }
                    }
                    nowElemStack += "<p><span class=\"highlight\">"+nowElem[ii][1]+"</span><em>"+nowText+"</em></p>\n"
                }
            }else if (nowElem[ii][0]===3){//WARN
                if(nowElem[ii][2].length>0){
                    nowElemStack += "<p><span class=\"highlight\">"+nowElem[ii][1]+"</span><em class='warn-em'>"+nowElem[ii][2]+"</em></p>\n"
                }
            }else{//SDS LOG
                if(nowElem[ii][1].length>0){
                    nowElemStack += "<p><em>"+nowElem[ii][1].replace(/\n/gi, "<br>")+"</em></p>\n"
                }
            }
        }
        nowHTML += nowElemStack.length>0 ? "<dd>"+nowElemStack+"</dd>" : "";
        $("#test_result_id").empty();
        $("#test_result_id").html(nowHTML);
    }
}

function resetList(){
    $("#nowDebugIndex").text("");
    $("#nowDebugStack").text("")
    $("#test_result_id").empty();
    $("#test_result_id").html("");
}

function reloadTestPageIframe(){
    nowResetClicked = false;
    let url = "getTestPage?host=" + hostToPage;
    $('#testIframe').attr('src', '');
    $('#testIframe').attr('src', url);
    nowDebug = [[]];
    nowDebugPK = [];
    resetList();
}
