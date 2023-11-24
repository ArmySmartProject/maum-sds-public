<%--
  Created by IntelliJ IDEA.
  User: mindslab
  Date: 2021-05-17
  Time: 오전 11:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <script src="${pageContext.request.contextPath}/js/voicebotBuilderContents/voicebotBuilderIntentionOrder.js?v=${queryString}"></script>
  <title>음성봇빌더 || 의도</title>
</head>
<body>
<div class="content_btns">
  <button type="button" class="btn_primary" onclick="saveIntentSortList()">저장</button>
  <button type="button" class="btn_primary btn_test_on fr" onclick="openTest()">테스트</button>
</div>
<%-- list_wrap --%>
<div class="list_wrap">
  <div id="intent_order_list">
    <div class="head">
      <span>의도</span><span>정규식</span>
    </div>
    <ul id="order_list" class="drag_lst scroll">
      <li class="no_list">의도 상세 화면에서 정규식을 등록해주세요.</li>
<%--      <li><span><a>의도</a></span><span>정규식</span></li>--%>
    </ul>
  </div>
</div>
<%-- //list_wrap --%>

<button type="button" class="btn_secondary small" style="margin-top: 10px;" onclick="resetIntentSortList()">적용한 내용 초기화</button>
</body>
</html>