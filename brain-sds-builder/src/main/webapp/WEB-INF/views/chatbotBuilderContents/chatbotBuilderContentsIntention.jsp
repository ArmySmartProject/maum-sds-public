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
    <title>chatbot builder Intention</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/jquery-ui-1.12.0/jquery-ui.min.css"/>
    <script src="${pageContext.request.contextPath}/js/jquery-ui-1.12.0/jquery-ui.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/builderContents/chatbotBuilderIntention.js?v=${queryString}"></script>
</head>
<body>
<div class="srchArea">
    <div class="fr iptBox">
        <!-- [D] 인풋에서 엔터 키 입력 시 btn_search가 클릭됩니다 -->
        <input type="text" class="ipt_txt search" name="intentText" autocomplete="off">
        <button type="button" class="btn_search" id="searchIntent"><span class="text_hide">검색하기</span></button>
    </div>
</div>

<div class="tbl_box_info">
    <div class="fl">
        <p class="path"><em>의도</em></p>
    </div>
    <div class="fr">
        <div style="display: inline-block; margin-right: 15px;">
            <a href="#chat_regex_upload" class="btn_secondary btn_lyr_open" onclick="regexExcelModalOpen()">정규식 엑셀 업로드</a>
            <a class="btn_secondary btn_lyr_open" onclick="regexExcelDownload()">정규식 엑셀 다운로드</a>
            <a href="#chat_learning_management" id="chatbot_learning" class="btn_secondary btn_lyr_open popper_box" style="display: none" onclick="handleLyr()">챗봇 학습
                <%--            [D] 문장에 따라 width를 다르게 지정해 주어야 합니다. --%>
                <span class="popper bg_bk" id="alert_nqa_train_start" style="width: 160px; display: none">학습되지 않은 데이터가 있습니다. 학습을 시작해 주세요.</span>
                <span class="popper bg_bk" id="alert_nqa_training" style="width: 85px; display: none">학습중 입니다.</span>
                <span class="popper bg_bk" id="alert_nqa_uploading" style="width: 140px; display: none">학습문장을 업로드 중 입니다.</span>
            </a>
        </div>
        <a href="#chat_intention_add" class="btn_secondary btn_lyr_open el_intent_step01" onclick="addPage()">추가</a>
    </div>
</div>

<div class="tbl_wrap scroll">
    <table class="tbl_box_lst" summary="순번, 의도, 정규식 문장, NQA 문장, BERT 문장, 상세, 삭제">
        <caption class="hide">순번, 의도, 정규식 문장, NQA 문장, BERT 문장, 상세, 삭제</caption>
        <colgroup id="intentColgroup">
            <col style="width: 65px;"><col style="width: 15%;"><col><col>
            <col style="width: 85px;"><col style="width: 85px;">
        </colgroup>
        <thead id="intentHead">
        <tr>
            <th>순번</th>
            <th>의도</th>
            <th>정규식 문장</th>
            <th>학습 문장</th>
            <%--<th>BERT 학습 문장</th>--%>
            <th><span class="text_hide">상세</span></th>
            <th><span class="text_hide">삭제</span></th>
        </tr>
        </thead>
        <tbody id="intentBody">
        </tbody>

<%--        [D] 정규식 컬럼 추가--%>
        <tbody id="regexBody">
        </tbody>
    </table>

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
<input type="hidden" id="currentPage" value="1">
<input type="hidden" id="searchIntentText">

<!-- 모달 정규식 파일 업로드 -->
<div id="chat_regex_upload" class="lyrBox">
    <div class="lyr_top">
        <h3>정규식 엑셀 파일 업로드</h3>
        <button class="btn_lyr_close"><span class="text_hide">닫기</span></button>
    </div>
    <div class="lyr_mid">
        <dl class="dl_inline">
            <dt>
                <div class="iptBox">
                    <!-- [D] input[file] value = input[text] value -->
                    <input type="text" name="excel_file_name" value="선택된 파일 없음" class="ipt_txt ipt_file" disabled>
                </div>
            </dt>
            <dd>
                <label for="regexExcelFile" class="label_file">찾아보기..</label>
                <form id="regexUpload" name="regexUpload" method="post" enctype="multipart/form-data"
                      action="${pageContext.request.contextPath}/upload/regexExcelUpload">
                    <input type="file" name="excelFile" id="regexExcelFile" accept=".xls, .xlsx" style="display: none;">
                    <input type="text" name="host" style="display: none;">
                </form>
            </dd>
        </dl>
        <p class="info_text">* 파일 업로드 시 정규식 타입이 직접입력으로 적용됩니다.</p>
        <%-- [D] progress_bar를 채우려면 transform: translate(10%) 속성을 이용하면 됩니다.
         진행되지 않은 상태는 0%이고 bar가 꽉 찬 상태는 100% 입니다.  --%>
        <div class="progress" style="display: none;"><div class="progress_bar" style="transform: translate(30%)"></div></div>
    </div>
    <div class="lyr_btm">
        <div class="btnBox sz_small">
            <button type="button" id="btn_regex_upload" class="btn_primary btn_submit" onclick="regexExcelUpload()">저장</button>
            <button type="button" class="btn_primary btn_lyr_close">취소</button>
        </div>
    </div>
</div>
<!-- //모달 정규식 파일 업로드 -->
<div title="Data Download" id="preparing-file-modal" style="display: none;">
    <div id="progressbar" style="width: 100%; height: 22px; margin-top: 20px;"></div>
</div>
<div title="Error" id="error-modal" style="display: none;">
    <p>생성실패.</p>
</div>
</body>
</html>
