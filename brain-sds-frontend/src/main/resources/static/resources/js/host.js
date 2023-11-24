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

    var iframe_url = sds_url + '/' + host_name + '/' + qrLocation;

    var chatbot_tag =
        '<div id="chatbot">\n' +
        '    <iframe id="chatbot_iframe" src="' + iframe_url + '" ' +
        'frameborder=0 framespacing=0 marginheight=0 marginwidth=0 scrolling=no vspace=0></iframe>\n' +
        '</div>';


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

    function iframeMsg(e) {
        try {
            if(e.data.type === 'org.chromium.encryptedMessage' || e.type === 'org.chromium.encryptedMessage'){
                // 아이폰 크롬으로 접속시 iframe 의 도메인이 다를때 보내는 암호화된 메세지 무시
                console.log("iphone chrome connection : chromePostMessage iframeMsg");
            } else {
                if (e.data.data === "resetSession") {
                    $('#chatbot_iframe').get(0).contentWindow.postMessage({data: "resetSession"}, "*");
                }
                if (e.data.data === "resetSessionCallback") {
                    window.parent.postMessage({data: "resetSessionCallback"}, "*");
                }
                if (e.data.data === "chatbot_msg") {
                    window.parent.postMessage(e.data, '*');
                }
                if (e.data === "chatbot_open" || e.data === "chatbot_close") {
                    $("#chatbot").toggleClass("chatOpen").toggleClass("chatClose");
                }

                if (e.data === "aside_open") {
                    $("#chatbot").addClass("aside_area");
                } else if (e.data === "aside_close") {
                    $("#chatbot").removeClass("aside_area");
                }

                if (e.data === "remove_iframe") {
                    console.log("Chatbot Licence Unavailable")
                    $("#chatbot").remove()
                    $(".chatGreeting").remove()
                    $(".powered").remove()
                }

                if (e.data.type === "href") {
                    if (e.data.value === "#") return;
                    var win = window.open(e.data.value, '_blk');
                    win.focus();
                }
            }
        } catch (exce) {
            // Noting to anything
        }
    }

    $("#chatbot_iframe").on("load", sendChatBotMeta);

    window.addEventListener("message", iframeMsg, false);

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


window.addEventListener("load", mainFunction, false);
