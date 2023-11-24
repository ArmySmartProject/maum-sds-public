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
    <title>chatbot builder IntentionDetail</title>
    <script src="${pageContext.request.contextPath}/js/builderContents/chatbotBuilderIntentionDetail.js?v=${queryString}"></script>
</head>
<body>
<div class="srchArea">
    <div class="fr iptBox">
        <!-- [D] 인풋에서 엔터 키 입력 시 btn_search가 클릭됩니다 -->
        <input type="text" class="ipt_txt search" name="searchIntentDetail" autocomplete="off">
        <button type="button" id="intention_detail" class="btn_search"><span class="text_hide">검색하기</span></button>
    </div>
</div>

<div class="tbl_box_info">
</div>

<div class="tbl_wrap scroll">
    <table class="tbl_box_lst" summary="순번, 문장">
        <caption class="hide">순번, 문장</caption>
        <colgroup id="colType">
            <col style="width: 65px;"><col>
        </colgroup>
        <thead id="intentHead">
        <tr>
            <th>순번</th>
            <th>문장</th>
            <th><span class="text_hide">상세</span></th>
        </tr>
        </thead>
        <tbody id="sentenceBody">
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
<input type="hidden" id="intentCp">
<input type="hidden" id="searchIntentText">
<input type="hidden" id="nqaAnswerId">
<input type="hidden" id="categoryId">
<input type="hidden" id="addNedit">
<input type="hidden" id="questionNo">
<input type="hidden" id="question">
</body>
</html>
