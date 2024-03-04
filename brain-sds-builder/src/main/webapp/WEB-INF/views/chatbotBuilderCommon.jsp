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

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css?v=${queryString}">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/font.css?v=${queryString}">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/swiper.min.css?v=${queryString}">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/simplebot.css?v=${queryString}">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mui_alert.css?v=${queryString}">

    <script src="${pageContext.request.contextPath}/js/corejs-3.js"></script>
    <script src="${pageContext.request.contextPath}/js/jquery.js"></script>
    <script src="${pageContext.request.contextPath}/js/jquery.form.js"></script>
    <script src="${pageContext.request.contextPath}/js/swiper.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/popper-2.5.4.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/sortable.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/builderContents/chatbotBuilderCommon.js?v=${queryString}"></script>
    <script src="${pageContext.request.contextPath}/js/builderContents/chatbotTestPage.js?v=${queryString}"></script>

    <script src="${pageContext.request.contextPath}/js/mui_alert.js?v=${queryString}"></script>

    <title>chatbot builder</title>
</head>
<body id="default_body">
<%-- 튜토리얼 전체스텝 및 현재스텝 표시 --%>
<div id="cb_tutorial_list" style="display:none;">
    <div class="active">
        <span class="step">챗봇 추가</span>
        <span>&#10139</span>
    </div>
    <div>
        <span class="step">의도 생성</span>
        <span>&#10139</span>
    </div>
    <div>
        <span class="step">태스크 생성</span>
        <span>&#10139</span>
    </div>
    <div>
        <span class="step">테스트</span>
    </div>
</div>

<div id="wrap">
    <div id="header">
        <h1>chatbot builder</h1>
    </div>

    <div id="container" class="chatbot_builder">
        <div class="content">
            <div class="multipleBoxType">
                <!-- lot_wrap -->
                <div class="lot_wrap">
                    <%@ include file="chatbotBuilderLeft.jsp"%>
                    <!-- chatbot_content -->
                    <div class="lotBox chatbot_content">
                        <div class="tit">
                            <h3 id="chatTitle"></h3>
                            <div class="lang_list" id="upper_lang_list">
                                <ul id="ul_lang_list">
                                    <li class="korea" onclick="langOnclick(1)">한국어</li>
                                    <li class="us" onclick="langOnclick(2)">영어</li>
                                    <li class="japan" onclick="langOnclick(3)">일본어</li>
                                    <li class="china" onclick="langOnclick(4)">중국어</li>
                                </ul>
                            </div>
                            <div id="nqa_guide" style="display: none; margin: 5px;"><i>학습문장은 한국어만 사용 가능합니다.</i></div>
                            <div class="fr">
                                <!-- [D] 챗봇테스트 버튼 클릭 시 챗봇 테스트 영역이 보여집니다. -->
                                <button type="button" class="btn_primary btn_test_on el_test_step01" onclick="iframeStr(hostToPage)">챗봇 테스트</button>
                            </div>
                        </div>

                        <div class="cont">
                            <ul class="menu">
                                <!-- [D] 메뉴 클릭 시 active class가 추가됩니다. contentIndex 와 data-index가 같아야함 -->
                                <li onclick="changeContents(0)" data-index="0" class="active el_task_step01">답변</li>
                                <li onclick="changeContents(1)" data-index="1" class="el_intent_step01 el_intent_step03">의도</li>
                                <li onclick="changeContents(2)" data-index="2">설정</li>
                                <li onclick="changeContents(4)" data-index="4" class="replace_dic">치환사전</li>
                                <li onclick="changeContents(5)" data-index="5">딥러닝</li>
                            </ul>
                            <div class="prev_menu_cont"></div>
                            <div class="menu_cont">

                            </div>
                        </div>
                    </div>
                    <!-- //chatbot_content -->
                </div>
                <!-- //lot_wrap -->

                <!-- chatbot_test -->
                <div class="lotBox chatbot_test">
                    <div class="tit">
                        <h3>챗봇테스트</h3>
                        <div class="fr">
                            <!-- [D] 닫기 버튼 클릭 시 챗봇 테스트 영역이 사라집니다. -->
                            <button type="button" class="btn_primary btn_test_off" onclick="iframeReset()">닫기</button>
                        </div>
                    </div>
                    <div class="cont">
                        <div class="test_result_command">
                            <div class="fl">
                                <p>분석결과 <span id="nowDebugIndex"></span>&#47;<span id="nowDebugStack"></span></p>
                            </div>
                            <div class="fr">
                                <button type="button" class="btn_line_primary prev" onclick="renderDebugList(nowIndexDebug-1)">이전</button>
                                <button type="button" class="btn_line_primary next" onclick="renderDebugList(nowIndexDebug+1)">다음</button>
                                <button type="button" class="btn_line_primary refresh" onclick="resetSessionByBuilder()">새로고침</button>
                            </div>
                        </div>
                        <div class="resize_areas">
                            <div class="resize_area">
                                <dl class="test_result scroll" id="test_result_id">
                                </dl>
                            </div>
                            <div class="drag_resize"><img src="../images/ico_grabmark.svg" alt="드래그 할 수 있다는 표시"></div>
                            <div class="resize_area">
                                <iframe id="testIframe" class="testIframe" src="" style="width:100%; height:100%; border:0; framespacing:0; marginheight:0; marginwidth:0; scrolling:no;  vspace:0; frameborder:0"></iframe>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- //chatbot_test -->
            </div>
        </div>
        <!-- //content -->
    </div>
    <!-- //container -->
</div>
<!-- //wrap -->
<div class="non_wrap">

</div>
<script>
debugTestResizeArea();
sessionStorage.setItem("contextpath", "${pageContext.request.contextPath}");
</script>
</body>
</html>
