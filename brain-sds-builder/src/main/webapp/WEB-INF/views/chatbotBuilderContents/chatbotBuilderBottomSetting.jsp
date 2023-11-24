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
    <title>chatbot builder Setting</title>
</head>
<body>
<p id="css">
    /* MINDsLab. UX/UI Team. AMR. 20201201 */
    #chatbot {display:block; overflow:hidden; position:fixed; width: 90px; right:0; bottom:55px; height:75px; border-radius:10px; z-index:9999;}
    #chatbot.chatOpen {width: 370px; height:80%; bottom: 0px;}
    #chatbot.aside_area {width: 725px;}
    #chatbot_iframe {position: absolute; right: 0px; bottom: 0px; width:775px;height:100%; border:none;}
    .chatGreeting:hover .btn_greetClose {opacity: 1;}
    .chatGreeting {display:block; position:fixed; right:100px; bottom:30px; margin-bottom:0px; padding:10px 35px 10px 10px; line-height: 18px; color:rgba(--main-text-rgb,1.0); font-size:12px; text-align:right; border-radius:7px; background:rgba(--main-color-rgb,1.0); z-index:700; -webkit-animation-duration:1.2s; animation-duration:1.2s; -webkit-animation-fill-mode:both; animation-fill-mode:both; -webkit-animation-name:chatGreeting; animation-name:chatGreeting; animation-delay:0.5s;}
    /* 200310 AMR animation 경량화 */
    @-webkit-keyframes chatGreeting {
    0%   {opacity:0; display:none; transform: translateY(0px);}
    50%  {opacity:0; display:none; transform: translateY(0px);}
    100% {opacity:1; display:block; transform: translateY(-30px);}
    }
    @keyframes chatGreeting {
    0%   {opacity:0; display:none; transform: translateY(0px);}
    50%  {opacity:0; display:none; transform: translateY(0px);}
    100% {opacity:1; display:block; transform: translateY(-30px);}
    }
    .chatGreeting.chatGreeting_hide {animation-delay:0s; -webkit-animation-duration:0.3s; animation-duration:0.3s; -webkit-animation-fill-mode:both; animation-fill-mode:both; -webkit-animation-name:chatGreeting_hide; animation-name:chatGreeting_hide; }
    @-webkit-keyframes chatGreeting_hide {
    0%   {opacity:1; display:block; transform: translateY(-30px);}
    100% {opacity:0; display:none; transform: translateY(0px);}
    }
    @keyframes chatGreeting_hide {
    0%   {opacity:1; display:block; transform: translateY(-30px);}
    100% {opacity:0; display:none; transform: translateY(0px);}
    }
    .powered {display:block; position:fixed; right:20px; bottom:30px; margin-bottom:0; color:#fff; font-size:12px; text-align:right; text-shadow:1px 1px 3px rgba(0, 0, 0, 0.7); z-index:700;}
    .powered img {height:9px; margin:0 0 1px 4px; vertical-align:middle;}
    #chatbot.chatOpen ~ .chatGreeting {display:none;}
    #chatbot.chatOpen ~ .powered {display:none;}
    .btn_greetClose {overflow: hidden; position:absolute; top: 10px; right: 8px; width: 20px; height: 20px; text-indent:-99px; border:none; border-radius: 50%; cursor:pointer; background-color: rgba(255,255,255,0.7); transition:all 0.3s ease-out;opacity: 0;}
    .btn_greetClose:before,
    .btn_greetClose:after {content:''; position:absolute; top:50%; left:4px; width:12px; height:1px; background:rgba(0,0,0,0.9);}
    .btn_greetClose:before {transform:rotate(-45deg);}
    .btn_greetClose:after {transform:rotate(45deg);}
    @media all and (max-width: 768px) {
    #chatbot {right:0; bottom:40px; width: 75px; height: 55px; border-radius:0; z-index:9999;}
    #chatbot #btn_goChat {bottom:0; width:55px; margin:0 10px 40px 0; padding:50px 0 0 0;}
    #chatbot.chatOpen {bottom:0px; width: 100%; height:100%;}
    .powered {bottom:12px;}
    .chatGreeting {left:auto; right: 80px; width: auto; margin-bottom: -20px; text-align:left; word-break: keep-all;}

    #chatbot_iframe {width: 100%;}

    .chatGreeting .btn_greetClose {opacity: 1;}
    }
    #btn_goChat {border:2px solid rgba(--main-color-rgb,1.0); background:url("--main-icon") 50% 50% no-repeat; background-size:cover;}
    #chatUI_wrap .chatUI_top h1 {color:rgba(--main-color-rgb,1.0);  background: url("--main-logo") no-repeat 50% 0; background-size: auto 50px;}
    #chatUI_wrap .chatUI_top h1 img {height: 50px;}
    #chatUI_wrap .chatUI_top .langBox .lst_lang li button:hover,.chatUI_top .langBox .lst_lang li button:focus {border:1px solid rgba(--main-color-rgb,1.0);}
    #chatUI_wrap .chatUI_top .langBox .lst_lang li button.active {background:rgba(--main-color-rgb,1.0); color:rgba(--main-text-rgb,1.0);}
    #chatUI_wrap .chatUI_mid .lst_talk li.user .txt {background:rgba(--main-color-rgb,1.0); color:rgba(--main-text-rgb,1.0);}
    #chatUI_wrap .chatUI_mid .lst_talk li.bot:first-child:before {background:rgba(--main-color-rgb,1.0) url("--main-icon") 50% 50% no-repeat; background-size:cover; }
    #chatUI_wrap li.user + li.bot:before {background:#fff url("--main-icon") 50% 50% no-repeat; background-size:cover;}
    #chatUI_wrap .chatLoading > em {background-color:rgba(--main-color-rgb,1.0);}
    #chatUI_wrap .chatUI_mid .lst_talk .txt {background:rgba(--main-color-rgb,1.0);}
    #chatUI_wrap .chatUI_mid .lst_talk .generic .generic_tit a {color:rgba(--main-color-rgb,1.0);}
    #chatUI_wrap .chatUI_mid .lst_talk .generic .generic_tit a:before {background:rgba(--main-color-rgb,1.0);}
    #chatUI_wrap .chatUI_mid .lst_talk .generic .generic_tit a:after {background:rgba(--main-color-rgb,1.0);}
    #chatUI_wrap .chatUI_mid .lst_talk .generic .generic_url a {color:rgba(--main-color-rgb,1.0);}
    #chatUI_wrap .chatUI_mid .lst_talk .generic .generic_url a:before {background:rgba(--main-color-rgb,1.0);}
    #chatUI_wrap .chatUI_mid .lst_talk .generic .generic_url a:after {background:rgba(--main-color-rgb,1.0);}
    #chatUI_wrap .chatUI_mid .lst_talk .btnLst ul li a {color:rgba(--main-color-rgb,1.0); }
    #chatUI_wrap .chatUI_mid .lst_talk .btnLst ul li a:hover {box-shadow:rgba(--main-color-rgb,1.0) 0 80px 0px 2px inset;}
    #chatUI_wrap .chatUI_mid .lst_talk .btnLst ul li a.active {background:rgba(--main-color-rgb,1.0); color:rgba(--main-text-rgb,1.0);}
    #chatUI_wrap .chatUI_mid .lst_talk .btnLst .btnBox button.btn_point {background:rgba(--main-color-rgb,1.0); color:rgba(--main-text-rgb,1.0);}
    #chatUI_wrap .chatUI_mid .lst_talk .btnItem ul li a {color:rgba(--main-color-rgb,1.0); box-shadow:rgba(--main-color-rgb,0) 0 0px 0px 2px inset;}
    #chatUI_wrap .chatUI_mid .lst_talk .btnItem ul li a:hover {box-shadow:rgba(--main-color-rgb,1.0) 0 80px 0px 2px inset;}
    #chatUI_wrap .chatUI_mid .lst_talk .btnItem ul li a.btnStart {box-shadow:rgba(--main-color-rgb,1.0) 0 80px 0px 2px inset; background:rgba(--main-color-rgb,1.0); color:rgba(--main-text-rgb,1.0);}
    #chatUI_wrap .chatUI_mid .lst_talk .btnItem ul li a.active {background:rgba(--main-color-rgb,1.0); color:rgba(--main-text-rgb,1.0);}
    #chatUI_wrap li.bot_promotion:before {background: rgba(--main-color-rgb,1.0) url("--main-icon") 50% 50% no-repeat; background-size: cover;}
    #chatUI_wrap .chatUI_mid .lst_talk .bot_promotion .generic .generic_tit {color: rgba(--main-color-rgb,1.0);}
    #chatUI_wrap .chatUI_mid .lst_talk li.botMsg_swiper a.swiper_item:hover {border:1px solid rgba(--main-color-rgb,1.0);}
    #chatUI_wrap .full_preview .swiper-pagination-bullet-active {background:rgba(--main-color-rgb,1.0);}
    #chatUI_wrap .swiper-button-prev, .swiper-container-rtl .swiper-button-next {background:rgba(--main-color-rgb,0.9) url("../images/arw_left_w.png") center center no-repeat;}
    #chatUI_wrap .swiper-button-next, .swiper-container-rtl .swiper-button-prev {background:rgba(--main-color-rgb,0.9) url("../images/arw_right_w.png") center center no-repeat;}
    #chatUI_wrap .swiper-pagination-bullet-active {background:rgba(--main-color-rgb,1.0);}
    #chatUI_wrap .full_preview .swiper-pagination-bullets .swiper-pagination-bullet {border: 1px solid rgba(--main-color-rgb,1.0);}
    #chatUI_wrap .chatUI_btm .textArea:focus {border:1px solid rgba(--main-color-rgb,1.0);}
    #chatUI_wrap .chatUI_btm .btn_chat {background-color:rgba(--main-color-rgb,1.0);}
    .chatAside .checkBox input[type="checkbox"]:checked + label:before{border-color:rgba(--main-color-rgb,1.0); background:rgba(--main-color-rgb,1.0); color:rgba(--main-text-rgb,1.0);}
    .chatAside .radioBox input[type="radio"]:checked + label:before{border-color:rgba(--main-color-rgb,1.0); background:rgba(--main-color-rgb,1.0); color:rgba(--main-text-rgb,1.0);}
    .chatAside .chatAside_hd h3 {color:rgba(--main-color-rgb,1.0);}
    .chatAside .chatAside_bd .btnBox button.btn_point {background:rgba(--main-color-rgb,1.0);}
    .chatAside .chatAside_bd .stnBox.popup .popup_content .ico_circle {background: rgba(--main-color-rgb,1.0);}
    #chatUI_wrap .txt .txt_btn a {color:rgba(--main-color-rgb,1.0) !important; box-shadow:rgba(--main-color-rgb,0) 0 0px 0px 2px inset;}
    #chatUI_wrap .txt .txt_btn a:hover {border:1px solid rgba(--main-color-rgb,0) !important; box-shadow:rgba(--main-color-rgb,1.0) 0 80px 0px 2px inset;}
    #chatUI_wrap .txt .txt_btns a {color:rgba(--main-color-rgb,1.0) !important; box-shadow:rgba(--main-color-rgb,0) 0 0px 0px 2px inset;}
    #chatUI_wrap .txt .txt_btns a:hover {border:1px solid rgba(--main-color-rgb,0) !important;  box-shadow:rgba(--main-color-rgb,1.0) 0 80px 0px 2px inset;}
    #chatUI_wrap .mapWrap .mapWrap_cont .btn_mapLoad_detail {background:rgba(--main-color-rgb,1.0);}
    #chatUI_wrap .chatUI_mid .lst_talk .btnLst .txt.txt_radius {background: rgba(--main-color-rgb,1.0); color:rgba(--main-text-rgb,1.0);}
</p>
</body>
</html>
