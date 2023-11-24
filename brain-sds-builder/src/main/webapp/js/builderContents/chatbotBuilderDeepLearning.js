$(document).ready(function() {
  checkDeeplearningChatAndSetHistory();
});

function goPage(host, cp){
  hostToPage = host;
  checkDeeplearningChatAndSetHistory();
}

function handleLyrPopup(){
  var winHeight = $(window).height() * 0.7,
    hrefId = $(event.target).attr('href'),
    thisLayer = $(hrefId);

  if ( thisLayer.hasClass('sub_lyr') ) {
    thisLayer.wrap('<div class="lyrWrap"></div>');
    thisLayer.before('<div class="lyr_bg"></div>');
    thisLayer.prev('.lyr_bg').css({'background': 'rgba(0,0,0,0.1)'});
  } else {
    $('body').css('overflow', 'hidden');
    thisLayer.wrap('<div class="lyrWrap"></div>');
    thisLayer.before('<div class="lyr_bg"></div>');
  }

  //대화 UI
  $('.lyrBox .lyr_mid').each(function () {
    $(this).css('max-height', Math.floor(winHeight) + 'px');
  });

  // 수정 201209 AMR Layer popup close btn_close click
  $('.btn_lyr_close').on('click', function (e) {
    hrefId = $(this).parents('.lyrBox').attr('id');
    e.stopPropagation();
    $(".sub_lyr").css('display', 'none');
    lyrClose(thisLayer);
  });
}

function lyrClose(target) {
  var lyrWrap = $('.lyrWrap');
  if (lyrWrap.length) {
    target.unwrap('<div class="lyrWrap"></div>');
  }
  target.prev('.lyr_bg').remove();

  if ( !target.hasClass('sub_lyr') ) {
    $('body').css('overflow', '');
  }
  target.find('.btn_lyr_close').off('click');

  //input 초기화
  target.find('input[type="text"], input[type="email"], input[type="file"]').val('');
}

function checkDeeplearningChatAndSetHistory() {
  const obj = {
    'mngCode': String(hostToPage),
  }

  return $.ajax({
    url: 'https://chatbotmaker.maum.ai/api/avatar/getInfo.json?' + $.param(obj),
    type: "POST",
    contentType: 'application/json',
  }).done(function(res){
    if (res.data === null) { //챗봇등록
      const obj = {
        'mngCode': String(hostToPage),
        'svcMngName': $('#chatTitle').text(),
        'email': '',
      }

      $.ajax({
        url: 'https://chatbotmaker.maum.ai/api/avatar/engn/regist.json?' + $.param(obj),
        type: "POST",
        contentType: 'application/json',
      }).done(function(){
        setTableLearningHistory();

      }).fail(function(error){
        console.log('apply() status code: ' + error.status);

        if ( !error.responseText ) {
          mui.alert('다시 시도해주세요<br> 문제가 지속되면 관리자에게 문의해주세요');
        } else {
          const errorRes = JSON.parse(error.responseText);
          mui.alert(errorRes.failStack[0].error);
        }

      });

    } else {
      setTableLearningHistory();
    }

  }).fail(function(error){
    console.log('apply() status code: ' + error.status);

    if ( !error.responseText ) {
      mui.alert('다시 시도해주세요<br> 문제가 지속되면 관리자에게 문의해주세요');
    } else {
      const errorRes = JSON.parse(error.responseText);
      mui.alert(errorRes.failStack[0].error);
    }
  });
}

function setTableLearningHistory() {
  const obj = {
    'mngCode': String(hostToPage),
  };

  $.ajax({
    url: 'https://chatbotmaker.maum.ai/api/avatar/selectUploadFileList.json?' + $.param(obj),
    type: "POST",
    contentType: 'application/json',
  }).done(function (res) {
    if ( !res.success ) {
      mui.alert("학습이력을 가져오는 데에 실패하였습니다.");
      console.log(res.failStact.error);
      return;
    }

    const $historyTbody = $('#table_learning_history tbody');

    $historyTbody.empty();

    const $tr = $('<tr></tr>');
    const $td = $('<td></td>');

    if ( res.data.length === 0 ) {
      $tr.append($td.attr('colspan', '6').text('요청한 학습이 없습니다.'));
      $historyTbody.append($tr);
      return;
    }

    const data = res.data;
    const learningStatus = {
      'AS': '요청대기',
      'RQ': '학습요청',
      'DO': '학습중',
      'CM': '학습완료',
      'CR': '취소요청',
      'RJ': '요청반려',
      'EX': '폐기',
    };

    for (var i=0, l=data.length; i<l; i++) {
      const $trClone = $tr.clone();
      //첫번째 셀
      $trClone.append($td.clone().text(i+1));

      //두번째 셀
      $trClone.append($td.clone().text(data[i].engnTypeCode));

      //세번째 셀
      const learningCode = data[i].learnStatus;
      $trClone.append($td.clone().text(learningStatus[learningCode]));
      
      //네번째 셀
      $trClone.append($td.clone().text(data[i].atchFileVo.atchOrgFileName));

      //다섯번째 셀
      $trClone.append($td.clone().text(data[i].regDate.replace('.0', '')));

      //여섯번째 셀
      const $tdClone = $td.clone();
      const $buttonDownload = $('<button type="button" class="btn_line_primary">다운로드</button>');
      const learnMngCode = data[i].learnMngCode;

      //다운로드 버튼
      $buttonDownload.on('click', function() {
        const obj = {
          'learnMngCode' : learnMngCode,
        };

        $.fileDownload('https://chatbotmaker.maum.ai/api/avatar/downloadLearnFile.json?' + $.param(obj), {
          successCallback: function (url) {
            console.log("엑셀 다운 OK url : " + url);
          },
          failCallback: function (res, url) {
            mui.alert('업로드 된 학습 파일을 조회할 수 없습니다.');
            console.log("엑셀 다운 Fail url : " + url);
          }
        });
      });

      $tdClone.append($buttonDownload);

      //취소 버튼
      if (learningCode === 'RQ') { //학습요청 상태일 때 취소버튼 노출
        let $buttonCancel = $('<a href="#lyr_learn_cancel" class="btn_line_warning">취소</a>');

        $buttonCancel.on('click', function() {
          handleLyrPopup();

          let $lyrCancel = $('#lyr_learn_cancel');
          $lyrCancel.find('.btn_event_save').on('click', requestCancel);
          $lyrCancel.find('.btn_event_close').on('click', function(){
            offBtnEvent($lyrCancel);
          });
        });

        function offBtnEvent($el) {
          $el.find('.btn_event_save').off('click');
          $el.find('.btn_event_close').off('click');
        }

        function requestCancel() {
          var email = $('#reqCancel').val();
          if ( !email ) {
            mui.confirm('이메일을 입력하지 않고 진행하시겠습니까?', {
              onClose : function(isOk) {
                if (isOk) cancelContinue(email);
              }
            });
          } else {
            cancelContinue(email);
          }
        }

        function cancelContinue(email) {
          let obj = {
            'learnMngCode' : learnMngCode,
            'email' : email,
            'currStatus' : 'RQ',
          };

          $.ajax({
            url: 'https://chatbotmaker.maum.ai/api/avatar/engn/cancelLearn.json?' + $.param(obj),
            type: "POST",
            contentType: 'application/json',
          }).done(function (res) {
            if (res.success) {
              mui.alert('학습 취소 요청이 완료되었습니다.');
              setTableLearningHistory();

              let $lyrCancel = $('#lyr_learn_cancel');
              offBtnEvent($lyrCancel);
              lyrClose($lyrCancel);
            }

            
          }).fail(function(error){
            console.log('apply() status code: ', error.status);

            if ( !error.responseText ) {
              mui.alert('다시 시도해주세요<br> 문제가 지속되면 관리자에게 문의해주세요');
            } else {
              let errorRes = JSON.parse(error.responseText);
              mui.alert(errorRes.failStack[0].error);
            }
          });
        }
        $tdClone.append(' ').append($buttonCancel);

      }

      $trClone.append($tdClone);
      $historyTbody.append($trClone);
    }

  }).fail(function(error) {
    console.log('apply() status code: ' + error.status);

    if ( !error.responseText ) {
      mui.alert('다시 시도해주세요<br> 문제가 지속되면 관리자에게 문의해주세요');
    } else {
      const errorRes = JSON.parse(error.responseText);
      mui.alert(errorRes.failStack[0].error);
    }
  });
}

function downloadSampleFile() {
  let $target = $(event.target);
  let dataEngine = $target.attr('data-engine');
  let obj = {};

  if (dataEngine === 'ITF') {
    obj.engnType = 'ITF';
  } else if (dataEngine === 'MRC') {
    obj.engnType = 'MRC';
  }
  obj.mngCode = String(hostToPage);

  $.fileDownload('https://chatbotmaker.maum.ai/api/avatar/engn/download.json?' + $.param(obj), {
    successCallback: function (url) {
      console.log("샘플 엑셀 다운 OK");
    },
    failCallback: function (res, url) {
      mui.alert('샘플 학습 파일을 조회할 수 없습니다.');
      console.log("샘플 엑셀 다운 Fail");
    }
  });
}

function attachFile() {
  const $target = $(event.target);
  const $parent = $target.parent('.ipt_file_box');
  const $label = $parent.find('input[type="text"].ipt_file');
  const fileSplit = $target.val().split("\\");
  const fileName = fileSplit[fileSplit.length-1];

  $label.val(fileName);
}

function requestLearning() {
  const $target = $(event.target);
  const $thisLayer = $target.parents('.lyrBox');
  const engnType = $target.attr('data-engine');
  const Id = '#' + engnType.toLowerCase();
  const email = $(Id + 'Email').val();

  if ( $(Id + 'File').val() === '' ) {
    mui.alert('파일을 선택해주세요');
    return;
  }

  if ( email === '' ) {
    mui.confirm('이메일을 입력하지 않고 저장하시겠습니까?', {
      onClose : function(isOk) {
        if (isOk) saveContinue();
      }
    });
  } else {
    saveContinue();
  }

  function saveContinue() {
    const formData = new FormData();

    formData.append('mngCode', String(hostToPage));
    formData.append('engnType', engnType);
    formData.append('email', email);
    formData.append("file", $(Id + 'File').prop('files')[0]);

    //전달하는 form 데이터 확인 code
    // for (var pair of formData.entries()) {
    //   console.log(pair[0]+ ', ' + pair[1]);
    // }

    $.ajax({
      url: 'https://chatbotmaker.maum.ai/api/avatar/engn/upload.json',
      data: formData,
      type: "POST",
      processData: false,
      contentType: false,
    }).done(function(res) {
      if (res.success) {
        mui.alert('학습요청이 완료되었습니다.');
        lyrClose($thisLayer);
        setTableLearningHistory();
      }

    }).fail(function(error){
      console.log('apply() status code: ' + error.status);

      if ( !error.responseText ) {
        mui.alert('다시 시도해주세요<br> 문제가 지속되면 관리자에게 문의해주세요');
      } else {
        const errorRes = JSON.parse(error.responseText);
        mui.alert(errorRes.failStack[0].error);
      }
    });
  }
}