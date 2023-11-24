var intentOrderData = [];

$(document).ready(function(){
  if ( $('#vb_container').hasClass('test_on') ) {
    $('.btn_test_on').hide();
  }

  if ( scenario ) {
    $('#vb_wrap').addClass('loading');
    getRegexListAll().then(function(result) {
      if ( !result.isSuccess || result.skip ) return;

      drawIntentOrderList();
      $('#vb_wrap').removeClass('loading');
    });
  }
});

function getRegexListAll() {
  var obj = {};
  obj.host = data.host;
  obj.lang = data.lang;

  //getIntention
  return $.ajax({
    url: "voiceBot/getRegexListAll",
    data: JSON.stringify(obj),
    type: "POST",
    contentType: 'application/json'

  }).then(function (res) {
    console.log('getRegexListAll',res)
    var list = res.regexList;

    if ( !list.length ) {
      $('#vb_wrap').removeClass('loading');
      return { isSuccess: true, skip: true };
    }

    intentOrderData = [];

    for (var i = 0; i < list.length; i++) {
      var obj = {};
      obj.bertIntentName = list[i].IntentName;
      obj.bertIntentNo = list[i].IntentNo;
      obj.host = list[i].Host;
      obj.isInsert = "Y"; //순서가 바뀌면 새로 insert 되어야 함
      obj.lang = list[i].Language;
      obj.regex = list[i].Regex;
      // obj.regexIntentNo = list[i].RegexIntentNo;
      obj.regexIntentNo = ''; //순서가 바뀌면 새로 insert 되어야 함

      var hasRegexRule = list[i].RegexRuleAndType;

      if ( hasRegexRule ) {
        var regexRuleList = hasRegexRule.split('@');

        $.each(regexRuleList, function (i, value) {
          var arr = value.split('#');

          if ( i === 0 ) {
            obj.regexValue = arr[0];
            obj.regexType = arr[1];

          } else {
            obj.regexValue += ',' + arr[0];
            obj.regexType += ',' + arr[1];
          }
        });
      }

      intentOrderData.push(obj);
    }

    return { isSuccess: true };

  }).catch(function () {
    mui.alert('의도 순서 정보를 불러오지 못했습니다.<br> 관리자에게 문의해주세요.');
    $('#vb_wrap').removeClass('loading');
    console.log('[getRegexListAll error]');
    return { isSuccess: false };
  });
}

function drawIntentOrderList() {
  $('#order_list').empty();

  $.each(intentOrderData, function(i, listData) {
    var list = $('<li><span><a class="name"></a></span><span class="regex"></span></li>')
    list.find('.name').text(listData.bertIntentName);
    list.find('.regex').text(listData.regex);

    list.find('.name').on('click', function(){
      moveIntentDetail(data.host, listData.bertIntentName, "regex")
    });

    $('#order_list').append(list);
  })

  //정규식 drag 리스트
  var OrderList = new Sortable(order_list,
    {
      sort : true,
      animation : 100,
      ghostClass: 'blue-background-class',

      // Element dragging ended
      onEnd: function (evt) {
        /*Event*/
        //var itemEl = evt.item;  // dragged HTMLElement
        // evt.to;    // target list
        // evt.from;  // previous list
        // evt.oldIndex;  // element's old index within old parent
        // evt.newIndex;  // element's new index within new parent
        // evt.oldDraggableIndex; // element's old index within old parent, only counting draggable elements
        // evt.newDraggableIndex; // element's new index within new parent, only counting draggable elements
        // evt.clone // the clone element
        // evt.pullMode;  // when item is in another sortable: `"clone"` if cloning, `true` if moving

        var oldIndex = evt.oldIndex;
        var newIndex = evt.newIndex;

        if (oldIndex === newIndex) return;
        var endItem = intentOrderData.splice(oldIndex, 1);
        intentOrderData.splice(newIndex, 0, endItem[0])
      },
    }
  )
}

function resetIntentSortList() {
  mui.confirm('설정한 의도 순서가 초기화 됩니다.', {
    onClose: function(isOk) {
      if (isOk) {
        $('#vb_wrap').addClass('loading');
        getRegexListAll().then(function() {
          drawIntentOrderList();
          $('#vb_wrap').removeClass('loading');
        });
      }
    }
  });
}

function saveIntentSortList() {
  mui.confirm('현재 순서로 저장됩니다. 계속 하시겠습니까?', {
    onClose: function(isOk) {
      if (isOk) {
        $.ajax({
          url: "voiceBot/updateOrderRegex",
          data: JSON.stringify(intentOrderData),
          type: "POST",
          contentType: 'application/json'
        }).then(function (res) {
          mui.alert('의도 순서가 저장 되었습니다.');
          return { isSuccess: true };

        }).catch(function (res) {
          mui.alert('의도 순서 저장을 실패하였습니다. 관리자에게 문의해주세요.');
          console.log('[saveIntentSortList] error');
          $('#vb_wrap').removeClass('loading');
          return { isSuccess: false };
        });
      }
    }
  });
}