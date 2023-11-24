/* UXUI Team AMR */
window.mui = {
    // alert 사용 예
    // mui.alert('alert 버튼을 클릭했습니다.');
    // mui.alert('alert 버튼을 클릭했습니다.', '타이틀');
    alert : function(mainTxt, titleTxt){
        var alertHtml = document.createElement('div');
        var alertBox = document.createElement('div');
        var arrayMainTxt = mainTxt.split('<br>');
        alertBox.setAttribute('id', 'mui_alert_box');

        if ( titleTxt ) {
            var alertTop = document.createElement('div');
            alertTop.setAttribute('id', 'mui_alert_top');
            alertTop.textContent = titleTxt;
            alertBox.appendChild(alertTop);
        }

        var alertMid = document.createElement('div');
        alertMid.setAttribute('id', 'mui_alert_mid');
        createTextContent(arrayMainTxt);
        alertBox.appendChild(alertMid);

        var alertBtm = document.createElement('div');
        alertBtm.setAttribute('id', 'mui_alert_btm');
        alertBox.appendChild(alertBtm);

        var okButton = document.createElement('button');
        okButton.setAttribute("type", "button");
        okButton.textContent = '확인';
        okButton.addEventListener('click', alertClose);
        alertBtm.appendChild(okButton);

        alertHtml.setAttribute('id', 'mui_alert');
        alertHtml.appendChild(alertBox);
        document.body.appendChild(alertHtml);
        okButton.focus();

        function createTextContent(array) {
            for (var i=0, l=array.length; i<l; i++ ) {
                var content = document.createElement('p');
                content.textContent = array[i]
                alertMid.appendChild(content);
            }
        }

        function alertClose() {
            document.body.removeChild(alertHtml);
        }
    },

    // confirm 사용 예
    // mui.confirm('confirm 버튼을 클릭했습니다.', {
    //   title: '타이틀',
    //   onClose: function(isOk){ //콜백함수
    //     if(isOk){ //컨펌 창 확인버튼 클릭 시
    //       funcOk();
    //     } else { //컴펌 창 취소버튼 클릭 시 //else 생략 가능
    //       funcNo();
    //     }
    //   }
    // });
    confirm : function(mainTxt, options){
        var titleTxt = options && options.title;
        var alertHtml = document.createElement('div');
        var alertBox = document.createElement('div');
        var arrayMainTxt = mainTxt.split('<br>');
        alertBox.setAttribute('id', 'mui_alert_box');

        if ( titleTxt ) {
            var alertTop = document.createElement('div');
            alertTop.setAttribute('id', 'mui_alert_top');
            alertTop.textContent = titleTxt;
            alertBox.appendChild(alertTop);
        }

        var alertMid = document.createElement('div');
        alertMid.setAttribute('id', 'mui_alert_mid');
        createTextContent(arrayMainTxt);
        alertBox.appendChild(alertMid);

        var alertBtm = document.createElement('div');
        alertBtm.setAttribute('id', 'mui_alert_btm');
        alertBox.appendChild(alertBtm);

        var okButton = document.createElement('button');
        okButton.setAttribute("type", "button");
        okButton.textContent = '확인';
        okButton.addEventListener('click', function(){
            alertClose(true);
        });
        alertBtm.appendChild(okButton);

        var noButton = document.createElement('button');
        noButton.setAttribute("type", "button");
        noButton.textContent = '취소';
        noButton.addEventListener('click', function(){
            alertClose(false);
        });
        alertBtm.appendChild(noButton);

        alertHtml.setAttribute('id', 'mui_alert');
        alertHtml.appendChild(alertBox);
        document.body.appendChild(alertHtml);
        okButton.focus();

        function createTextContent(array) {
            for (var i=0, l=array.length; i<l; i++ ) {
                var content = document.createElement('p');
                content.textContent = array[i]
                alertMid.appendChild(content);
            }
        }

        function alertClose(isOk) {
            options && options.onClose(isOk);
            document.body.removeChild(alertHtml);
        }
    },
}