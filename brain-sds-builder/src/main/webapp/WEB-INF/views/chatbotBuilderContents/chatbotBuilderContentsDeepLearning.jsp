<%--
  Created by IntelliJ IDEA.
  User: mindslab
  Date: 2021-07-16
  Time: 오후 4:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>딥러닝</title>
  <script src="${pageContext.request.contextPath}/js/builderContents/chatbotBuilderDeepLearning.js?v=${queryString}"></script>
</head>
<body>
<%--  <div class="tit">--%>
<%--    <h3>딥러닝</h3>--%>
<%--    <div class="help">?<div class="help_desc" id="replace_help"><i>치환사전이란 동의어 사전으로, 등록해 놓은 단어를 변경해 주는 역할을 합니다. 비슷한 의미를 가진 단어를 교체해 학습량을 줄이거나, STT의 오류를 잡을 수 있습니다.</i></div></div>--%>
<%--  </div>--%>

  <div class="learning_box">
      <ul>
        <li><a href="#lyr_bert_learn_req" onclick="handleLyrPopup();" class="btn_secondary">bert 학습</a></li>
        <li><a href="#lyr_mrc_learn_req" onclick="handleLyrPopup();" class="btn_secondary">mrc 학습</a></li>
      </ul>
  </div>

  <div class="tbl_wrap" style="overflow: auto; height: calc(100% - 130px);">
    <table id="table_learning_history" class="tbl_box_lst" summary="순번, 엔진, 진행상황, 파일명, 요청일시, 동작">
      <caption class="hide">순번, 엔진, 진행상황, 파일명, 요청일시, 동작</caption>
      <colgroup>
        <col style="width: 50px;"><col style="width: 100px;"><col style="width: 100px;">
        <col> <%--파일명--%>
        <col style="width: 150px;"><col style="width: 180px;">
      </colgroup>
      <thead>
      <tr>
        <th>No</th>
        <th>엔진</th>
        <th>진행상황</th>
        <th>파일명</th>
        <th>요청일시</th>
        <th>
          다운로드/취소
          <div class="help">?<div class="help_desc" id="help_learning_cancle"><i>다운로드 : 학습요청 할 때 첨부한 파일이 다운로드 됩니다.<br>취소 : 진행상황이 "학습요청" 일 때만 가능합니다.</i></div></div>
        </th>
      </tr>
      </thead>
      <tbody>
<%--      <tr>--%>
<%--        <td>1</td>--%>
<%--        <td>bert</td>--%>
<%--        <td>학습요청</td>--%>
<%--        <td>BERT_Joon_Kiosk_220104 (1).xlsx</td>--%>
<%--        <td>yyyy.mm.dd</td>--%>
<%--        <td>--%>
<%--          <button type="button" class="btn_line_primary">다운로드</button>--%>
<%--          <button type="button" class="btn_line_warning">취소</button>--%>
<%--        </td>--%>
<%--      </tr>--%>
<%--      <tr>--%>
<%--        <td>2</td>--%>
<%--        <td>mrc</td>--%>
<%--        <td>그외</td>--%>
<%--        <td>MRC_Joon_Kiosk_220104 (1).xlsx</td>--%>
<%--        <td>yyyy.mm.dd HH:MM:SS</td>--%>
<%--        <td>--%>
<%--          <button type="button" class="btn_line_primary">다운로드</button>--%>
<%--          <button type="button" class="btn_line_warning">취소</button>--%>
<%--        </td>--%>
<%--      </tr>--%>
      </tbody>
    </table>

    <%--    [D] pagination은 타 jsp 태그 그대로 가져옴--%>
<%--    <div class="pagination">--%>
<%--      <button type="button" class="first" href="javascript:goPage(1)"><span>&laquo;</span></button>--%>
<%--      <button type="button" class="prev" href="javascript:goPage('${paging.prevPage}')"><span>&lsaquo;</span></button>--%>
<%--      <div class="pages">--%>
<%--        <c:forEach begin="${paging.pageRangeStart}" end="${paging.pageRangeEnd}" varStatus="loopIdx">--%>
<%--          <c:choose>--%>
<%--            <c:when test="${paging.currentPage eq loopIdx.index}">--%>
<%--              <span class="page active">${loopIdx.index}</span>--%>
<%--            </c:when>--%>
<%--            <c:otherwise>--%>
<%--              <span class="page" href="javascript:goPage('${loopIdx.index}')">${loopIdx.index}</span>--%>
<%--            </c:otherwise>--%>
<%--          </c:choose>--%>
<%--        </c:forEach>--%>
<%--      </div>--%>
<%--      <button type="button" class="next" href="javascript:goPage('${paging.nextPage}')"><span>&rsaquo;</span></button>--%>
<%--      <button type="button" class="last" href="javascript:goPage('${paging.totalPage}')"><span>&raquo;</span></button>--%>
<%--    </div>--%>
  </div>

  <style type="text/css">
    .learning_box {margin-top: 30px; padding: 25px 0; border: 2px solid #CED2E8; background: #F5F7F9;}
    .learning_box ul {text-align: center;}
    .learning_box ul li {display: inline-block;}
    .learning_box ul li + li {margin-left: 20px;}
    .learning_box ul li .btn_secondary {height: 40px; padding: 0 30px; line-height: 40px; font-size: 13px;}
    .tbl_wrap {margin-top: 20px;}
    #help_learning_cancle {width: 320px; top: calc(100% + 5px); left: auto; right: 0;}
  </style>

</body>
</html>