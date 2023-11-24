<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/semantic.min.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/bootstrap.css">
<link rel="stylesheet" href="<c:url value='/js/jquery-ui-1.12.0/jquery-ui.min.css'/>"/>
<script src="<c:url value="/js/jquery-3.1.0.min.js"/>"></script>
<script src="<c:url value="/js/jquery-ui-1.12.0/jquery-ui.min.js"/>"></script>
<script src="<c:url value="/js/jquery.fileDownload.js"/>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
<script type="text/javascript">
    window.onload = function() {
        var num = ${pageMaker.pageNum };
        $("#btn"+num).attr('class', 'page-item active');
    };
    $(function() {
        $("#btnExcel").on("click", function () {
            var $preparingFileModal = $("#preparing-file-modal");
            $preparingFileModal.dialog({modal: true});
            $("#progressbar").progressbar({value: false});
            $.fileDownload("${pageContext.request.contextPath}/upload/logExcelDownload", {
                successCallback: function (url) {
                    $preparingFileModal.dialog('close');
                },
                failCallback: function (responseHtml, url) {
                    $preparingFileModal.dialog('close');
                    $("#error-modal").dialog({modal: true});
                }
            });
            return false;
        });
    });

</script>
</head>
<body>
<div class="ui middle aligned center aligned grid">
    <div class="column">
        <div class="ui large form">
            <div class="ui stacked segment">
                <table class="ui celled table">
                    <thead>
                    <tr>
                        <th>No</th>
                        <th>Chatbot</th>
                        <th>Session</th>
                        <th>Flow_No</th>
                        <th>Utter</th>
                        <th>Prev</th>
                        <th>ButtonYN</th>
                        <th>Intent</th>
                        <th>Answer</th>
                        <th>Confidence</th>
                        <th>Datetime</th>
                    </tr>
                    </thead>
                    <tbody id="list">
                    <c:choose>
                        <c:when test="${fn:length(list) > 0 }">
                        <c:forEach items="${list }" var="bList">
                            <tr>
                                <th scope="row">${bList.id }</th>
                                <td>${bList.name }</td>
                                <td>${bList.session }</td>
                                <td>${bList.flowNo }</td>
                                <td>${bList.utter }</td>
                                <td>${bList.prevIntentUtter }</td>
                                <td><c:if test="${bList.prob eq 1.0}">
                                        <c:out value="Y"/>
                                    </c:if>
                                    <c:if test="${bList.prob ne 1.0}">
                                        <c:out value="N"/>
                                    </c:if>
                                </td>
                                <td>${bList.intent }</td>
                                <td>${bList.answer }</td>
                                <td>${bList.prob }</td>
                                <td>${bList.createDate }</td>
                                </tr>
                        </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="5">조회된 결과가 없습니다.</td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>

            <div class="ui error message"></div>

        </div>
    </div>
</div>

<ul class="pagination pagination-lg justify-content-center">
    <c:if test="${pageMaker.prev }">
        <li class="page-item">
            <a class="page-link" href="${pageContext.request.contextPath}/upload/chatLog?page=${pageMaker.startPage-1 }"/><i class="fa fa-chevron-left"><</i></a>
        </li>
    </c:if>
    <c:forEach begin="${pageMaker.startPage }" end="${pageMaker.endPage }" var="pageNum">
        <li class="page-item" id="btn${pageNum}">
            <a class="page-link" href="${pageContext.request.contextPath}/upload/chatLog?page=${pageNum }" onclick="btnClick(${pageNum})"/><i class="fa">${pageNum }</i></a>
        </li>
    </c:forEach>
    <c:if test="${pageMaker.next && pageMaker.endPage >0 }">
        <li class="page-item">
            <a class="page-link" href="${pageContext.request.contextPath}/upload/chatLog?page=${pageMaker.endPage+1 }"/><i class="fa fa-chevron-right">></i></a>
        </li>
    </c:if>
</ul>
<button type="button" class="btn btn-success" id="btnExcel" name="btnExcel">Excel Download</button>
<div title="Data Download" id="preparing-file-modal" style="display: none;">
    <div id="progressbar" style="width: 100%; height: 22px; margin-top: 20px;"></div>
</div>
<div title="Error" id="error-modal" style="display: none;">
    <p>생성실패.</p>
</div>
</body>
</html>