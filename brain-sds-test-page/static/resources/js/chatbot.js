const chat_meta = {"host" : "2", "lang" : "1"};

window.onload = function() {

    $("#chatbot").append("<iframe id=\"chatbot_frame\" class=\"chatbot_off\" src=\"http://sds.maum.ai/\" frameborder=0 framespacing=0 marginheight=0 marginwidth=0 scrolling=no vspace=0></iframe>");
    $("#chatbot").append(`<p class="powered">Powered by <img src="static/resources/images/img_tit_redtie.png" alt="RedTie Butler"></p>`);

    console.log("hello");

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
