<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">

    <!-- cache 지우는 meta 태그 -->
    <meta http-equiv="Cache-Control" content="no-cache" /> <!-- 캐시가 되지 않도록 정의 -->
    <meta http-equiv="Pragma" content="no-cache" /> <!-- 캐시가 되지 않도록 정의 -->
    <meta http-equiv="Expires" content="-1"> <!-- 즉시 캐시만료 -->

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css?v=${queryString}">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/font.css?v=${queryString}">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/simplebot.css?v=${queryString}">

    <script src="${pageContext.request.contextPath}/js/jquery.js"></script>
    <script src="${pageContext.request.contextPath}/js/jquery.form.js"></script>

    <title>chatbot builder</title>
</head>
<body>
<div class="lotBox chatbot_list">
    <div class="tit">
        <h3>챗봇목록</h3>
        <div class="fr">
            <button type="button" id="addChatbotLeftBut" class="btn_primary el_add_step01" onclick="changeContents(3)">추가</button>
        </div>
    </div>
    <div class="cont">
        <div class="iptBox">
            <!-- [D] 인풋에서 엔터 키 입력 시 btn_search가 클릭됩니다 -->
            <input type="text" class="ipt_txt search" id="searchChatText" name="chatText" autocomplete="off">
            <button type="button" class="btn_search" id="searchChat" onclick="chatSearch()"><span class="text_hide">검색하기</span></button>
        </div>
        <ul class="common_lst scroll" id="chatListUl">
            <c:forEach items="${chatList}" var="chatList" varStatus="itemStatus">
                <c:if test="${host == ''}">
                    <c:choose>
                        <c:when test="${itemStatus.first}">
                            <li class="active" value="${chatList.get("No")}" onclick="goPage(this.value,1)">
                        </c:when>
                        <c:otherwise>
                            <li value="${chatList.get("No")}" onclick="goPage(this.value,1)">
                        </c:otherwise>
                    </c:choose>
                </c:if>
                <c:if test="${host != ''}">
                    <c:choose>
                        <c:when test="${host == chatList.get('No')}">
                            <li class="active" value="${chatList.get("No")}" onclick="goPage(this.value,1)">
                        </c:when>
                        <c:otherwise>
                            <li value="${chatList.get("No")}" onclick="goPage(this.value,1)">
                        </c:otherwise>
                    </c:choose>
                </c:if>
                <div>
                    <span>${chatList.get("Name")}</span>
                </div>
                <div>
                    <span>Host : ${chatList.get("Host")}</span>
                </div>
                <a href="#chat_list_delete" class="btn_icon delete btn_lyr_open"  onclick="event.cancelBubble=true; delChatbot(${chatList.get("No")},'${chatList.get("Name")}')"></a>
                </li>
            </c:forEach>
        </ul>
    </div>
</div>
<script>
  $(document).ready(function () {
    $('#searchChatText').on('keyup', function () {
      if (event.keyCode == 13) {
        $(this).next('.btn_search').trigger('click');
      }
    });
  });

  function chatSearch() {

    var obj = new Object();
    obj.searchChatbot = $("input[name='chatText']").val();
    obj.chatList = chatList;

    $.ajax({
      url : "searchChat",
      data : JSON.stringify(obj),
      type: "POST",
      contentType: 'application/json'
    }).success(
        function(result) {

          var list = result.chatList;
          $("#chatListUl").empty();

          var innerHTML = "";
            allChatbotLang = [];

          $.each(list, function (i, v) {

              if (i == 0) {
                  innerHTML += '<li class="active" value="' + v.No + '" onclick="chatbotClickEvent('+i+',this)">';
              } else {
                  innerHTML += '<li value="' + v.No + '" onclick="chatbotClickEvent('+i+',this)">';
              }
            innerHTML += '<div>';
            innerHTML += '<span>' + v.Name+ '</span>';
            innerHTML += '</div>';
            innerHTML += '<div>';
            innerHTML += '<span>Host : ' + v.Host + '</span>';
            innerHTML += '</div>';
            innerHTML += '<input type="hidden" value="' +v.HTaskYN + '" />';
            innerHTML += '<a href="#chat_list_delete" class="btn_icon delete btn_lyr_open" onclick="event.cancelBubble=true; delChatbot('+v.No+',\''+v.Name+'\')"></a>';
            innerHTML += '</li>';

              let nowNewChatbotLng = [false,false,false,false];
              try{
                  let langDBText = v.Language.split(",");
                  for(let ii = 0; ii<langDBText.length; ii++){
                      nowNewChatbotLng[Number(langDBText[ii].trim())-1] = true;
                  }
              }catch{
                  nowNewChatbotLng = [true,true,true,true]
              }
              allChatbotLang.push(nowNewChatbotLng);
          });

          if(innerHTML==""){
              innerHTML += "<li class=\"no_list\">등록된 챗봇이 없습니다.</li>"
          }

          $("#chatListUl").append(innerHTML);


        }).fail(function(result) {
      console.log("getIntentList error");
    });
  };
</script>
</body>
</html>
