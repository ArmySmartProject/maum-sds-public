
window.onload = function() {

    // $("#chatbot").append("<iframe id=\"chatbot_frame\" src=\"http://114.108.173.114:10040/?host=5\" frameborder=0 framespacing=0 marginheight=0 marginwidth=0 scrolling=no vspace=0></iframe>");
    $("#chatbot").append("<iframe id=\"chatbot_frame\" src=\"http://localhost:10020/?host=5\" frameborder=0 framespacing=0 marginheight=0 marginwidth=0 scrolling=no vspace=0></iframe>");
    $("#chatbot").append(`
        <div class="chatGreeting">\
            무엇을 도와드릴까요?<br>\
            How may I assist you?
        </div>
    `);
    $("#chatbot").append(`<p class="powered">Powered by <img src="static/resources/images/img_tit_redtie.png" alt="RedTie Butler"></p>`);

    function sendChatBotMeta() {
        document.getElementById("chatbot_frame").contentWindow.postMessage(chat_meta, '*');
    }

    function iframeMsg(e) {
        console.dir(e);
        try {
            if (e.data == "chatbot_open" || e.data == "chatbot_close") {
                $("#chatbot").toggleClass("chatbot_on").toggleClass("chatbot_off");
            }

            if (e.data.type == "href") {
                if (e.data.value == "#") return;
            let win = window.open(e.data.value, '_blank');
                win.focus();
            }
        } catch (exce) {
            ;;
        }
    }

    $("#chatbot_frame").on("load", sendChatBotMeta);

    window.addEventListener("message", iframeMsg, false);

};
