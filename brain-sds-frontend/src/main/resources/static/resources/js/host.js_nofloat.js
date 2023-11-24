// var sds_url = 'https://sds.maum.ai';
var chatOpenClose;

function mainFunction() {
  var sds_url = chat_meta.sds_url;
  var host_name = chat_meta.host_name;
  var qrLocation = chat_meta.qrLocation;
  var previousSess = chat_meta.previousSess;
  var hostNo = chat_meta.host;
  var hostAddJs = chat_meta.hostAddJs;

  if (host_name === "chatbot") {
    alert("챗봇 Host 오류");
    throw new Error('챗봇 Host 오류');
  }

  var iframe_url = sds_url + '/' + host_name + '/' + qrLocation + '/noFloat';


  var chatbot_tag =
      '<div id="chatbot" style="width: 100%; height: 100%; margin: 0; padding: 0;">\n' +
      '    <iframe id="chatbot_iframe" src="' + iframe_url + '" ' +
      'frameborder=0 framespacing=0 marginheight=0 marginwidth=0 scrolling=no vspace=0 style="width: 100%; height: 100%; margin: 0; padding: 0;"></iframe>\n' +
      '</div>';

  // $.ajax({
  //   url: iframe_url,
  //   method: "GET"
  // }).done(function(data){
  //   $("body").append(data)
  // })


  var nowSupplierTag = ''
  var nowSupplierHtml = ''
  if (nowSupplier === 'mindslab'){
    nowSupplierTag = '/resources/images/img_tit_mindsLab.png'
  }else if (nowSupplier === 'redtie'){
    nowSupplierTag = '/resources/images/img_tit_redtie.svg'
  }
  if(nowSupplierTag.length>0){
    nowSupplierHtml = '<p class="powered">Powered by <img src="' + sds_url + nowSupplierTag +  '" alt="RedTie Butler"></p>';
  }


  var floating_tag =
      '<div class="chatGreeting">\n' +
      '    무엇을 도와드릴까요?<br>\n' +
      '    How may I help you?\n' +
      '    <button type="button" class="btn_greetClose"><em>말풍선 닫기</em></button>\n' +
      '</div>\n' + nowSupplierHtml;

  if ($("#chatbot").length !== 1) {
    $("body").append(chatbot_tag);
    // qr 버전일때는 floating tag를 띄우지 않음
    if (qrLocation === undefined || qrLocation === '') {
      $("body").append(floating_tag);
    }
    if(hostAddJs === "hostAddJs"){
      var hostAddTag = '<script src="' + sds_url + '/' + hostAddJs + '/' + hostNo + '" />';
      $("body").append(hostAddTag);
    }
  }

  function sendChatBotMeta() {
    document.getElementById("chatbot_iframe").contentWindow.postMessage(chat_meta, '*');
  }



  $("#chatbot_iframe").on("load", sendChatBotMeta);


  //chatGreeting 말풍선 닫기 버튼
  $('.btn_greetClose').on('click', function(){
    $('.chatGreeting').addClass('chatGreeting_hide').delay(300).queue(function() { $(this).remove(); });
  });

  // PARAMETER 'openClose':
  //      'open'      : chatbot open
  //      'close'     : chatbot close
  //      'toggle'    : chatbot open/close toggle
  //      undefined   : chatbot open/close toggle
  chatOpenClose = function (openClose) {
    document.getElementById("chatbot_iframe").contentWindow.postMessage({"chatOpenClose": openClose}, '*');
  };

}

function parentMsg(e){
  if(e.data.type === 'org.chromium.encryptedMessage' || e.type === 'org.chromium.encryptedMessage'){
    // 아이폰 크롬으로 접속시 iframe 의 도메인이 다를때 보내는 암호화된 메세지 무시
    console.log("iphone chrome connection : chromePostMessage parentMsg");
  } else {
    var returnObj = new Object();
    try{
      if (e.data.type === "href") {
        if (e.data.value === "#") return;
        window.parent.parent.location.href = e.data.value;
      }

      if(e.data=='chatbot_open'){
        returnObj.type = "chatbot_open";
      }else if(e.data=='Waiting for counselors...') {
        returnObj.type = "counselor_wait";
      }else if(e.data=='Counselor connected') {
        returnObj.type = "counselor_start";
      }else if(e.data=='End this Conversation') {
        returnObj.type = "counselor_end";
      }else if(e.data.type==='lang'){
        returnObj.type = "nofloat_change_lang";
        returnObj.lang = e.data.lang;
      }else{
        if(e.data.data=="chatbot_msg"){//
          returnObj.type = "chatbot_msg";
          returnObj.session = e.data.contents;
          returnObj.answer = e.data.jsonDebug.answer;
        }else if(e.data.data=="chatbot_counsel_end"){
          returnObj.type = "counselor_end";
        }else{
          returnObj.type = e.data.data;
        }
      }
    }
    catch (e) {
      returnObj.type = e.data;
    }

    try{
      if(returnObj.type!=undefined){
        if(returnObj.type === "nofloat_change_lang") {
          document.getElementById("chatbot_iframe").contentWindow.postMessage(returnObj, '*');
          return;
        }

        if(returnObj.type=="chatbot_msg"&& returnObj.answer.length>0){
          if(window.location != window.parent.location){ // log 분석 을 위한 메세지
            window.parent.postMessage(returnObj, '*');
          }

          console.log(returnObj);
        }
        if(returnObj.type!="chatbot_msg"){
          if(window.location != window.parent.location){
            window.parent.postMessage(returnObj, '*');
          }
          console.log(returnObj);
        }
      }
    }catch (e) {

    }
   }
}
window.addEventListener("message", parentMsg, false);
window.addEventListener("load", mainFunction, false);
