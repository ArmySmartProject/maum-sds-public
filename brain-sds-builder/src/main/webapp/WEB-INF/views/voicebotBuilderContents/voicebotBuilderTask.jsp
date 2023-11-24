<%--
  Created by IntelliJ IDEA.
  User: mindslab
  Date: 2021-05-17
  Time: 오전 11:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <script src="${pageContext.request.contextPath}/js/voicebotBuilderContents/voicebotBuilderTask.js"></script>
  <title>음성봇빌더 || 태스크</title>
</head>
<body>
<div class="content_btns">
  <button type="button" class="btn_primary" onclick="changeContents(PAGES.TASK_ADD); addTask();">TASK 추가</button>
  <div class="ipt_box fr">
    <input type="text" class="ipt_txt search" id="allSearch" placeholder="TASK 그룹/TASK/INTENT/다음 TASK 통합검색">
    <button class="btn_search">검색하기</button>
  </div>
</div>
<%-- table_wrap --%>
<div id="table_task" class="table_wrap">
<table class="tbl_common" id="taskTable"></table>

<!--   <div class="pagination" id="pagination"> -->
<!--     <button type="button" class="first"> -->
<!--       <span>«</span> -->
<!--     </button> -->
<!--     <button type="button" class="prev"> -->
<!--       <span>‹</span> -->
<!--     </button> -->
<!--     <div class="pages"> -->
<!--       <button type="button" class="page active">1</button> -->
<!--       <button type="button" class="page">2</button> -->
<!--       <button type="button" class="page">3</button> -->
<!--     </div> -->
<!--     <button type="button" class="next"> -->
<!--       <span>›</span> -->
<!--     </button> -->
<!--     <button type="button" class="last"> -->
<!--       <span>»</span> -->
<!--     </button> -->
<!--   </div> -->
  <%-- //pagination --%>
</div>
<%-- //table_wrap --%>
</body>
</html>
