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
    <title>chatbot builder New builder</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/colpick.css">
    <script src="${pageContext.request.contextPath}/js/colpick.js"></script>
    <script src="${pageContext.request.contextPath}/js/builderContents/chatbotBuilderNewChatbot.js?v=${queryString}"></script>
</head>
<body>
<div class="setting">
    <div class="ipt_container">
        <dl>
            <dt>언어선택<div class="help">?<div class="help_desc"><i>생성할 언어를 선택하는 란입니다.<br>여러 언어를 선택할 수 있습니다.</i></div></div></dt>
            <dd class="iptBox" id="settingLangCheckbox">
                <input type="checkbox" name="text-color" id="lang_ko" class="checkbox" checked>
                <label for="lang_ko">한국어</label>
                <input type="checkbox" name="text-color" id="lang_en" class="checkbox">
                <label for="lang_en">영어</label>
                <input type="checkbox" name="text-color" id="lang_ja" class="checkbox">
                <label for="lang_ja">일본어</label>
                <input type="checkbox" name="text-color" id="lang_cn" class="checkbox">
                <label for="lang_cn">중국어</label>
            </dd>
        </dl>
        <dl>
            <dt>카테고리</dt>
            <dd>
                <select name="" id="nowCategoryID" class="select">
                    <c:forEach items="${domains}" var="domainsElem" >
                        <option value="${domainsElem.DefaultHost}">${domainsElem.Name}</option>
                    </c:forEach>
                </select>
            </dd>
        </dl>
        <dl class="el_add_step02">
            <dt >챗봇명</dt>
            <dd><input type="text" class="ipt_txt"></dd>
        </dl>
        <dl class="el_add_step02">
            <dt>챗봇 ID<div class="help">?<div class="help_desc"><i>챗봇 고유 ID입니다.<br>ex)minds_chatbot</i></div></div></dt>
            <dd><input type="text" class="ipt_txt"></dd>
        </dl>
        <dl>
            <dt>Email<div class="help">?<div class="help_desc"><i>문의, 예약 등 챗봇이 전달하는 내용을 받을 이메일 주소입니다.<br>ex)hello@mindslab.ai</i></div></div></dt>
            <dd><input type="text" class="ipt_txt"></dd>
        </dl>
        <dl>
            <dt>상세설명<div class="help">?<div class="help_desc"><i>챗봇에 대한 설명을 적는 란입니다.<br>ex)마인즈랩 소개 챗봇입니다</i></div></div></dt>
            <dd><textarea name="" id="" class="ipt_txt" cols="30" rows="2"></textarea></dd>
        </dl>
        <dl>
            <dt>메인 색상<div class="help help_guide main-color">?<div class="help_desc"><i>챗봇에 적용할 색상을 고릅니다.</i></div></div></dt>
            <dd id="chatbot_color">
                <input type="text" id="picker" class="ipt_txt">
                <div class="pick_preview pick_color_bg"></div>
            </dd>
        </dl>
        <dl>
            <dt>포인트 글자색상<div class="help help_guide main-text">?<div class="help_desc"><i>챗봇에 적용할 글자 색상을 선택합니다.</i></div></div></dt>
            <dd class="iptBox">
                <input type="radio" name="text-color" id="text_white" class="checkbox" checked>
                <label for="text_white" onclick="changeTextColor()">흰색</label>
                <input type="radio" name="text-color" id="text_black" class="checkbox">
                <label for="text_black" onclick="changeTextColor()">검은색</label>
            </dd>
        </dl>
        <dl>
            <dt>로고 이미지<div class="help help_guide main-logo">?<div class="help_desc"><i>챗봇 상단에 적용될 이미지입니다.</i></div></div></dt>
            <dd>
                <div class="iptBox">
                    <input type="text" class="ipt_txt" readonly>
                    <input type="file" id="chat_build_logo" class="hide" onchange="changeLogoIcon()" accept="image/*">
                    <label for="chat_build_logo" id="newBuildLogo">찾아보기</label>
                </div>
            </dd>
        </dl>
        <dl>
            <dt>아이콘 이미지<div class="help help_guide main-icon">?<div class="help_desc"><i>챗봇 말풍선과 하단 동그라미에 적용될 이미지입니다.</i></div></div></dt>
            <dd>
                <div class="iptBox">
                    <input type="text" class="ipt_txt" readonly>
                    <input type="file" id="chat_build_icon" class="hide" onchange="changeLogoIcon()" accept="image/*">
                    <label for="chat_build_icon" id="newBuildIcon">찾아보기</label>
                </div>
            </dd>
        </dl>
        <div class="btn_box">
            <button type="button" class="btn_primary el_add_step02" onclick="checkHostNameNew()" id="newChatBut" >저장</button>
        </div>
    </div>
</div>

<div class="ipt_container">
    <dl>
        <dt>챗봇 미리보기<div class="help">?<div class="help_desc"><i>챗봇의 색상, 로고, 아이콘을 적용하면 확인하실 수 있습니다.</i></div></div></dt>
        <dd class="floating_ui">
            <div class="chat_preview">
                <div class="chatUI_top">
                    <p class="h1 pick_logo"></p>
                    <div class="langBox">
                        <p class="active"><button class="btn_lang" title="언어선택">언어선택</button></p>
                        <ul class="lst_lang show">
                            <li><button class="btn_lang ico_ko active pick_color_bg pick_color" data-lang="1">한국어</button></li>
                            <li><button class="btn_lang ico_en" data-lang="2">English</button></li>
                            <li><button class="btn_lang ico_ja" data-lang="3">日本語</button></li>
                            <li><button class="btn_lang ico_zh_cn" data-lang="4">中文(汉语)</button></li>
                        </ul>
                    </div>
                    <div class="btn_chatClose">close</div>
                </div>
                <div class="chatUI_mid">
                    <ul class="lst_talk">
                        <li class="newDate"><span>0000년 00월 00일 o요일</span></li>
                        <li class="bot">
                            <div class="pick_icon"></div>
                            <div class="bot_msg">
                                <div class="txt">챗봇의 색상, 로고, 아이콘을 설정해보세요.</div>
                                <div class="date">0000.00.00 00:00</div>
                            </div>
                        </li>
                        <li class="user">
                            <div class="bot_msg">
                                <em class="txt pick_color_bg pick_color">네 설정해주세요.</em>
                                <div class="date">0000.00.00 00:00</div>
                            </div>
                        </li>
                    </ul>
                </div>
                <div class="chatUI_btm">
                    <div class="textArea" disabled rows="1" placeholder="메세지를 입력해 주세요"></div>
                    <div id="btn_chat" class="btn_chat pick_color_bg">전송</div>
                </div>
            </div>
            <div class="chatGreeting pick_color_bg pick_color">
                무엇을 도와드릴까요?<br>How may I help you?
            </div>
            <button type="button" id="btn_goChat" class="pick_color_bd pick_icon">챗봇</button>
        </dd>
    </dl>
</div>
</body>
</html>
