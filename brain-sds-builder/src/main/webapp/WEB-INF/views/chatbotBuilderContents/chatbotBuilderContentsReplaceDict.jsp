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
  <title>치환사전</title>
  <script src="${pageContext.request.contextPath}/js/builderContents/chatbotBuilderReplaceDict.js?v=${queryString}"></script>
</head>
<body>
  <div class="tit">
    <h3>치환사전 </h3>
    <div class="help">?<div class="help_desc" id="replace_help"><i>치환사전이란 동의어 사전으로, 등록해 놓은 단어를 변경해 주는 역할을 합니다. 비슷한 의미를 가진 단어를 교체해 학습량을 줄이거나, STT의 오류를 잡을 수 있습니다.</i></div></div>
  </div>
  <div class="title">
    치환사전 추가
  </div>
  <table id="chatbot_replace_add" class="tbl_box_lst" summary="치환 전, 치환 후">
    <caption class="hide">치환 전, 치환 후</caption>
    <colgroup>
      <col>
      <col style="width: 50px;">
      <col>
    </colgroup>
    <thead>
    <tr>
      <th>치환 전</th>
      <th></th>
      <th>치환 후</th>
      <th class="text_hide">추가</th>
    </tr>
    </thead>
    <tbody>
      <td><input id="replaceDictBeforeAdd" type="text" class="ipt_txt"></td>
      <td>&#187;</td>
      <td><input id="replaceDictAfterAdd" type="text" class="ipt_txt"></td>
      <td><button type="button" class="btn_line_primary" onclick="replaceDictMod('add',0);">추가</button></td>
    </tbody>
  </table>

  <div class="title">
    치환사전 목록
    <div class="help">?
      <div id="replace_list_help" class="help_desc">엑셀로 업로드 할 경우 기존 목록이 엑셀로 덮어쓰기 됩니다. 엑셀 업로드 시 꼭 아래와 같이 작성해주세요.
        <div class="image">
          <img src="${pageContext.request.contextPath}/images/help_img_replace.png" alt="엑셀 업로드 작성 예시">
        </div>
      </div>
    </div>
  </div>

  <div id="chatbot_replace_list" class="inline_block">

  <div class="tbl_box_info">
      <div class="fl">
        <a href="#chat_replace_upload" class="btn_secondary btn_lyr_upload_open">엑셀 업로드</a>
        <button type="button" class="btn_secondary" onclick="excelDown()">엑셀 다운로드</button>
      </div>

      <div class="fr srchArea">
        <div class="iptBox">
            <!-- [D] 인풋에서 엔터 키 입력 시 btn_search가 클릭됩니다 -->
            <input type="text" class="ipt_txt search" name="searchReplaceDict" autocomplete="off">
            <button type="button" class="btn_search" id="searchReplaceDict"><span class="text_hide">검색하기</span></button>
        </div>
      </div>
  </div>



    <div class="tbl_wrap scroll">
      <input type="hidden" id="orderCol" name="orderCol" value="${orderCol}">
      <input type="hidden" id="orderSort" name="orderSort" value="${orderSort}">

      <table class="tbl_box_lst" summary="순번, 치환 전, 치환 후, 삭제">
        <caption class="hide">순번, 치환 전, 치환 후, 삭제</caption>
        <colgroup>
          <col style="width: 50px;">
          <col style="width: 282px;">
          <col style="width: 50px;">
          <col style="width: 282px;">
          <col style="width: 145px;">
        </colgroup>
        <thead>
        <tr>
          <th>순번</th>
  <%--        [D] 정렬 화살표(sort) : 정렬 아이콘 클릭 시 오름차순 > 내림차순 > 기본상태 순으로 정렬됩니다.
              기본상태 : .sort
              오름차순 : .sort.up
              내림차순 : .sort.down
   --%>
          <th id="replaceDictThBefore" class="sort" onclick="replaceDictOrder('Before');" >치환 전</th>
          <th></th>
          <th id="replaceDictThAfter" class="sort" onclick="replaceDictOrder('After');" >치환 후</th>
          <th></th>
        </tr>
        </thead>
        <tbody id="replaceDictBody">
          <td>1</td>
          <td><input type="text" class="ipt_txt" value="test"></td>
          <td>&#187;</td>
          <td><input type="text" class="ipt_txt" value="test"></td>
          <td>
            <a href="#" class="btn_line_primary btn_lyr_open">수정</a>
            <a href="#chat_row_delete" class="btn_line_warning btn_lyr_open">삭제</a>
          </td>
        </tbody>
      </table>
    </div>

    <%--    [D] pagination은 타 jsp 태그 그대로 가져옴--%>
    <div class="pagination">
      <button type="button" class="first" href="javascript:goPage(1)"><span>&laquo;</span></button>
      <button type="button" class="prev" href="javascript:goPage('${paging.prevPage}')"><span>&lsaquo;</span></button>
      <div class="pages">
        <c:forEach begin="${paging.pageRangeStart}" end="${paging.pageRangeEnd}" varStatus="loopIdx">
          <c:choose>
            <c:when test="${paging.currentPage eq loopIdx.index}">
              <span class="page active">${loopIdx.index}</span>
            </c:when>
            <c:otherwise>
              <span class="page" href="javascript:goPage('${loopIdx.index}')">${loopIdx.index}</span>
            </c:otherwise>
          </c:choose>
        </c:forEach>
      </div>
      <button type="button" class="next" href="javascript:goPage('${paging.nextPage}')"><span>&rsaquo;</span></button>
      <button type="button" class="last" href="javascript:goPage('${paging.totalPage}')"><span>&raquo;</span></button>
    </div>
  </div>

</body>
</html>