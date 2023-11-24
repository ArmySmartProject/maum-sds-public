<%--
  Created by IntelliJ IDEA.
  User: mindslab
  Date: 2021-07-19
  Time: 오후 1:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <script src="${pageContext.request.contextPath}/js/voicebotBuilderContents/voicebotBuilderReplaceDic.js?v=${queryString}"></script>
  <title>음성봇빌더 || 치환사전</title>
</head>
<body>
  <div>
    <span class="content_title">치환사전</span>
    <div class="help">?<div class="help_desc" id="replace_help">치환사전이란 동의어 사전으로, 등록해 놓은 단어를 변경해 주는 역할을 합니다. 비슷한 의미를 가진 단어를 교체해 학습량을 줄이거나, STT의 오류를 잡을 수 있습니다.<br><br>ex) 치환 전: ㅇㅇ, 치환 후: 네</div></div>
  </div>
  
  <div id="replace_container" class="dl_container">
    <dl>
      <dt>치환사전 추가</dt>
      <dd>
        <%-- table --%>
        <div id="table_replace_add" class="inline_block">
          <table class="tbl_common">
            <colgroup>
              <col><col>
            </colgroup>
            <thead>
            <tr>
              <th>치환 전</th>
              <th>치환 후</th>
            </tr>
            </thead>
            <tbody>
            <tr>
              <td><input type="text" class="ipt_txt" id="addBeforeTxt"></td>
              <td><input type="text" class="ipt_txt" id="addAfterTxt"></td>
            </tr>
            </tbody>
          </table>

          <button type="button" class="btn_primary" onclick="addReplaceDict();">추가</button>
        </div>
        <%-- //table --%>
      </dd>
    </dl>
    <dl>
      <dt>
        치환사전 목록
        <div class="help">?
          <div id="replace_list_help" class="help_desc">엑셀 업로드 시 엑셀 작성 방법<br><br>1. A:1 셀에 치환 전, B:1 셀에 치환 후를 작성하신 다음 A:2 부터 치환 할 데이터를 제목줄에 맞추어 작성해주세요.<br>2. 시트 이름을 "치환사전"으로 바꿔주세요.
            <div class="image">
              <img src="${pageContext.request.contextPath}/images/help_img_replace.png" alt="엑셀 업로드 작성 예시">
            </div>
            <img src="${pageContext.request.contextPath}/images/help_image_replace_sheet.png" alt="엑셀 업로드 작성 예시" style="border-radius: 5px;">
          </div>
        </div>
      </dt>
      <dd>
        <%-- table_wrap --%>
        <div id="table_replace_list" class="inline_block">
          <div class="float_box">
            <div class="fl replace_dict_excel">
              <div class="excel_btns">
                <button type="button" class="btn_secondary" data-modal="voicebot_replace_upload" onclick="handleModal()">엑셀 업로드</button>
                <button type="button" class="btn_secondary" onclick="excelDown()">엑셀 다운로드</button>

                <%--          엑셀 다운로드 form--%>
                <form action="${pageContext.request.contextPath}/upload/replaceDictExcelDown"
                      target="downFrame" id="downloadReplaceDict" name="downloadReplaceDict" method="get">
                  <input type="hidden" name="orderCol" id="replaceDictCol" value="">
                  <input type="hidden" name="orderSort" id="replaceDictSort" value="">
                  <input type="hidden" name="host" id="replaceDict_host">
                  <input type="hidden" name="lang" id="replaceDict_lang">
                </form>
              </div>
            </div>

            <div class="fr search_box">
              <input type="text" class="ipt_txt search" name="searchReplaceDict"  autocomplete="off" placeholder="치환 전/후 검색">
              <button type="button" class="btn_search" id="searchReplaceDict" onclick="searchReplaceDict();">검색</button>
            </div>
          </div>

          	
          <%-- table --%>
          <div class="table_frozen_head">
            <table class="tbl_common">
              <colgroup>
                <col style="width: 60px;">
                <col style="width: 230px;">
                <col style="width: 230px;">
                <col style="width: 150px;">
              </colgroup>
              <thead>
              <tr>
                <th>순번</th>
                <th class="sort">치환 전</th>
                <th class="sort">치환 후</th>
                <th class="text_hide">삭제</th>
              </tr>
              </thead>
            </table>
          </div>
          <div class="table_body scroll">
              <table class="tbl_common" id="replaceDictTable">
                <colgroup>
                  <col style="width: 60px;">
                  <col style="width: 230px;">
                  <col style="width: 230px;">
                  <col style="width: 150px;">
                </colgroup>
                <tbody>
                <tr>
                  <td>1</td>
                  <td><input type="text" class="ipt_txt"></td>
                  <td><input type="text" class="ipt_txt"></td>
                  <td>
                    <button type="button" class="btn_line_save">수정</button>
                    <button type="button" class="btn_line_warning">삭제</button>
                  </td>
                </tr>
                <!-- <tr>
                  <td>1</td>
                  <td><input type="text" class="ipt_txt"></td>
                  <td><input type="text" class="ipt_txt"></td>
                  <td>
                    <button type="button" class="btn_line_warning">삭제</button>
                  </td>
                </tr> -->
                </tbody>
              </table>
            </div>
          <%-- //table --%>

          <%-- pagination --%>
          <div class="pagination">
            <button type="button" class="first">
              <span>«</span>
            </button>
            <button type="button" class="prev">
              <span>‹</span>
            </button>
            <div class="pages">
              <button type="button" class="page active">1</button>
              <button type="button" class="page">2</button>
              <button type="button" class="page">3</button>
            </div>
            <button type="button" class="next">
              <span>›</span>
            </button>
            <button type="button" class="last">
              <span>»</span>
            </button>
          </div>
          <%-- //pagination --%>
        </div>
        <%-- //table_wrap --%>
      </dd>
    </dl>

<!--     <button type="button" class="btn_primary">저장</button> -->

<!-- Excel Down Loading 모달 -->
<div title="Data Download" id="preparing-file-modal" style="display: none;">
    <div id="progressbar" style="width: 100%; height: 22px; margin-top: 20px;"></div>
</div>
<div title="Error" id="error-modal" style="display: none;">
    <p>생성실패.</p>
</div>

<!-- 모달 답변 파일 업로드 -->
<div id="voicebot_replace_upload" class="vb_modal">
    <div class="vb_modal_dialog">
    <div class="dlg_header">
      <span class="title">엑셀 업로드</span>
      <button type="button" class="btn_modal_close">닫기</button>
    </div>
    <div class="dlg">
      <div class="ipt_file_box">
        <form id="replaceDictUpload" name="replaceDictUpload" method="post" enctype="multipart/form-data"
              action="${pageContext.request.contextPath}/upload/insertReplaceDictExcel">
            <input type="text" id="excel_file_dict_label" class="ipt_txt" placeholder="선택된 파일 없음" disabled>
        	<input type="file" name="excelFile" id="excelFile_replaceDict" class="ipt_file" accept=".xls, .xlsx" >
            <input type="hidden" name="Host" id="replaceDictHost">
            <input type="hidden" name="Lang" id="replaceDictLang">
        </form>
        <span class="file_label">
          <label for="excelFile_replaceDict">찾아보기..</label>
        </span>
      </div>
      <div class="info_box">
        <p class="info_small primary">* 파일 업로드 시 기존 데이터가 덮어쓰기 됩니다.</p>
        <p class="info_small primary">* 업로드 전 기존 데이터를 다운로드 받으시길 권장합니다.</p>
      </div>
    </div>
    <div class="dlg_footer">
      <button type="button" id="btn_upload" class="btn_primary btn_submit" onclick="uploadExcel()">저장</button>
      <button type="button" class="btn_secondary btn_modal_close">취소</button>
    </div>
    </div>
</div>


  </div>
 <input type="hidden" id="currentPage" value="1">
 <input type="hidden" id="orderCol" value="">
 <input type="hidden" id="orderSort" value="">
</body>
</html>
