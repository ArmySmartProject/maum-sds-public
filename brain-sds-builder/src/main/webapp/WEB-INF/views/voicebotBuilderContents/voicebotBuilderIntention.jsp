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
  <script src="${pageContext.request.contextPath}/js/voicebotBuilderContents/voicebotBuilderIntention.js?v=${queryString}"></script>
  <title>음성봇빌더 || 의도</title>
</head>
<body>
<div class="content_btns">
  <button type="button" class="btn_primary" onclick="changeContents(PAGES.INTENTION_ADD)">의도 추가</button>
  <button type="button" class="btn_primary" onclick="changeContents(PAGES.INTENTION_ORDER)">의도 순서</button>
  <button type="button" class="btn_primary bubble_box" data-modal="learning_nqa" onclick="handleModal()">학습하기
    <span id="alert_nqa_train_start" class="bubble nqa">학습해야 할 데이터가 있습니다.</span>
  </button>
  <div class="ipt_box fr">
    <input type="text" id="intent_search" class="ipt_txt search" placeholder="INTENT/정규표현식/학습문장 통합검색">
    <button class="btn_search">검색하기</button>
  </div>
</div>
<%-- table_wrap --%>
<div id="table_intent" class="table_wrap">
  <%-- table --%>
  <table class="tbl_common">
    <colgroup>
      <col style="width: 80px;">
      <col><col><col>
      <col style="width: 90px;">
    </colgroup>
    <thead>
    <tr>
      <th>공통 여부</th>
      <th>INTENT</th>
      <th>정규표현식</th>
      <th>학습문장</th>
      <th class="text_hide">삭제하기</th>
    </tr>
    </thead>
    <tbody>
    <tr>
      <td colspan="5" class="no_list">등록된 의도가 없습니다.</td>
    </tr>
<%--    <tr>--%>
<%--      <td>O</td>--%>
<%--      <td><a>일이삼사오육칠팔구십일이삼사오육칠팔구십</a></td>--%>
<%--      <td><a>(네|예|좋아요), (----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----)</a></td>--%>
<%--      <td><a>좋습니다, 그러하다</a></td>--%>
<%--      <td class="btn_cell_box"><button type="button" class="btn_line_warning">삭제</button></td>--%>
<%--    </tr>--%>
    </tbody>
  </table>
  <%-- //table --%>
</div>
<%-- //table_wrap --%>
</body>
</html>
