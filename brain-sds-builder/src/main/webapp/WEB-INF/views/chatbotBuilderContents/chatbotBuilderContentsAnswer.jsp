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
    <title>chatbot builder Answer</title>
    <script src="${pageContext.request.contextPath}/js/builderContents/chatbotBuilderAnswer.js?v=${queryString}"></script>
</head>
<body>
<div class="srchArea">
    <div class="fr iptBox">
        <!-- [D] 인풋에서 엔터 키 입력 시 btn_search가 클릭됩니다 -->
        <input type="text" class="ipt_txt search" id="search_keyword_textbox" autocomplete="off">
        <button type="button" class="btn_search"  onclick="searchKeywordAnswer()"><span class="text_hide">검색하기</span></button>
    </div>
</div>

<div class="tbl_box_info">
    <div class="fl">
        <a href="#chat_answer_upload" class="btn_secondary btn_lyr_upload_open">엑셀 업로드</a>
        <button type="button" class="btn_secondary" onclick="excelDown()">엑셀 다운로드</button>
        <button type="button" class="btn_secondary" onclick="sampleExcelDown()" style="margin:0 0 0 0">샘플데이터 다운로드</button>
    </div>
    <div class="fr">
        <a href="#chat_answer_setting" class="btn_secondary btn_lyr_open el_task_step01" onclick="newAnswer()">태스크 추가</a>
    </div>
</div>

<div class="tbl_wrap scroll">
    <table class="tbl_box_lst" summary="순번, 태스크, 디스플레이명, 답변, 상세, 삭제">
        <caption class="hide">순번, 태스크, 디스플레이명, 답변, 상세, 삭제</caption>
        <colgroup>
            <col style="width: 65px;"><col style="width: 15%"><col style="width: 15%"><col><col style="width: 85px;"><col style="width: 85px;">
        </colgroup>
        <thead>
        <tr>
            <th>순번</th>
            <th>
                태스크
                <div class="help">?<div class="help_desc"><i>답변을 구분해주는 고유한 이름입니다.<br>첫 언어의 디스플레이명으로 지정되며<br>챗봇 사용자에게 노출되지 않습니다.</i></div></div>
            </th>
            <th>
                디스플레이명
                <div class="help">?<div class="help_desc"><i>버튼이나 캐러셀로 해당 답변이 노출될 때의 이름입니다.</i></div></div>
            </th>
            <th>
                답변
                <div class="help">?<div class="help_desc"><i>답변이 출력될 때의 텍스트 입니다.</i></div></div>
            </th>
            <th><span class="text_hide">상세</span></th>
            <th><span class="text_hide">삭제</span></th>
        </tr>
        </thead>
        <tbody id="intentBody">
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
</body>
</html>
