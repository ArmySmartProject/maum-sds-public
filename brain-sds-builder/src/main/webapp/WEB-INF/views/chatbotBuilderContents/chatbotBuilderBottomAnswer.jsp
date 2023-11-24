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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/jquery-ui-1.12.0/jquery-ui.min.css"/>
<%--    <script src="${pageContext.request.contextPath}/js/jquery-3.1.0.min.js"></script>--%>
    <script src="${pageContext.request.contextPath}/js/jquery-ui-1.12.0/jquery-ui.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/jquery.fileDownload.js"></script>
    <title>chatbot builder Answer</title>
    <script src="${pageContext.request.contextPath}/js/builderContents/chatbotBuilderAnswer.js"></script>

    <script src="${pageContext.request.contextPath}/js/rcrop.min.js"></script>
    <link href="${pageContext.request.contextPath}/css/rcrop.min.css" media="screen" rel="stylesheet" type="text/css">
</head>
<body>
<!-- 모달 챗봇 삭제 -->
<div id="chat_list_delete" class="lyrBox">
    <div class="lyr_top">
        <h3>챗봇 삭제</h3>
        <button class="btn_lyr_close"><span class="text_hide">닫기</span></button>
    </div>
    <div class="lyr_mid">
        <p class="infoTxt"><em>선택한 챗봇</em>을(를) 삭제합니다.</p>
    </div>
    <div class="lyr_btm">
        <div class="btnBox sz_small">
            <button type="button" class="btn_primary btn_submit btn_lyr_close">확인</button>
            <button type="button" class="btn_primary btn_submit btn_lyr_close">취소</button>
        </div>
    </div>
</div>
<!-- //모달 챗봇 삭제 -->

<!-- 모달 답변 파일 업로드 -->
<div id="chat_answer_upload" class="lyrBox">
    <div class="lyr_top">
        <h3>엑셀 파일 업로드</h3>
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
                <label for="excelFile" class="label_file">찾아보기..</label>
                <form id="chatUpload" name="chatUpload" method="post" enctype="multipart/form-data"
                      action="${pageContext.request.contextPath}/upload/insertChatExcel">
                <input type="file" name="excelFile" id="excelFile" accept=".xls, .xlsx" style="display: none;">
                <input type="text" name="host" style="display: none;">
                </form>
            </dd>
        </dl>
        <p class="info_text">* 파일 업로드 시 태스크 및 답변이 덮어쓰기 됩니다.</p>
        <%-- [D] progress_bar를 채우려면 transform: translate(10%) 속성을 이용하면 됩니다.
         진행되지 않은 상태는 0%이고 bar가 꽉 찬 상태는 100% 입니다.  --%>
        <div class="progress" style="display: none;"><div class="progress_bar" style="transform: translate(30%)"></div></div>
    </div>
    <div class="lyr_btm">
        <div class="btnBox sz_small">
            <button type="button" id="btn_upload" class="btn_primary btn_submit" onclick="uploadExcel()">저장</button>
            <button type="button" class="btn_primary btn_lyr_close">취소</button>
        </div>
    </div>
</div>
<!-- //모달 답변 파일 업로드 -->

<!-- 모달 답변 삭제 -->
<div id="chat_row_delete" class="lyrBox">
    <div class="lyr_top">
        <h3>챗봇 답변 삭제</h3>
        <button class="btn_lyr_close"><span class="text_hide">닫기</span></button>
    </div>
    <div class="lyr_mid">
        <p class="infoTxt"><em>선택한 태스크</em>을(를) 삭제합니다.</p>
    </div>
    <div class="lyr_btm">
        <div class="btnBox sz_small">
            <button type="button" class="btn_primary btn_submit btn_lyr_close">확인</button>
            <button type="button" class="btn_primary btn_submit btn_lyr_close">취소</button>
        </div>
    </div>
</div>
<!-- //모달 답변 삭제 -->

<!-- 모달 개별 추가 및 설정 -->
<div id="chat_answer_setting" class="lyrBox">
    <div class="lyr_top">
        <h3>챗봇 답변</h3>
        <div class="lang_list" id="ans_detail_lang_list">
            <ul>
                <li class="korea none" onclick="langDetailOnclick(1)">한국어</li>
                <li class="us none"    onclick="langDetailOnclick(2)">영어</li>
                <li class="japan none" onclick="langDetailOnclick(3)">일본어</li>
                <li class="china none" onclick="langDetailOnclick(4)">중국어</li>
            </ul>
        </div>
        <button class="btn_lyr_close"><span class="text_hide">닫기</span></button>
    </div>
    <div class="lyr_mid">
        <div class="skeleton"><img src="../images/skeleton_answer.png" alt="백그라운드"></div>
        <div class="chat_setting answer_preview">
            <div class="ipt_container" id="ori_ipt">
            </div>
            <div class="ipt_container" id="sec_ipt">
            </div>
        </div>

        <ul class="chat_preview scroll" id="chat_intent_answer_preview">
            <li class="bot botMsg_swiper swiper-container" id="chat_preview_carousel">
                <div class="swiper-wrapper">
                    <div class="swiper-slide">
                    </div>
                </div>
                <div class="swiper-pagination"></div>
                <div class="swiper-button-prev"></div>
                <div class="swiper-button-next"></div>
            </li>

            <li class="bot" id="chat_preview_answer">
                <div id="answerPreview" class="txt">답변</div>
            </li>

            <li class="bot" id="chat_preview_button">
                <div class="bot_msg">
                    <div class="btnItem">
                        <ul>
                            <li><a class="intent btnStart" href="#" data-display="처음으로" data-intent="처음으로">처음으로</a></li>
                        </ul>
                    </div>
                </div>
<%--                <div class="date">2020.11.12 AM 9:16</div>--%>
            </li>
        </ul>
    </div>
    <div class="lyr_btm">
        <div class="btnBox sz_small">
            <button type="button" class="btn_primary btn_submit btn_lyr_close_submit">확인</button>
            <button type="button" class="btn_primary btn_submit btn_lyr_close">취소</button>
        </div>
    </div>
</div>
<!-- //모달 개별 추가 및 설정 -->


<!-- 서브모달 이미지 카드 이미지 등록 -->
<div id="carousel_image_setting" class="lyrBox sub_lyr">
    <div class="lyr_top">
        <h3>이미지 등록</h3>
        <button class="btn_lyr_close" id="btn_setting_lyr_close_id" onclick="exitCropPage()"><span class="text_hide">닫기</span></button>
    </div>
    <div class="lyr_mid">
        <dl class="dl_inline">
            <dt>
                <div class="iptBox">
                    <input type="text" name="carousel_image_file" value="선택된 파일 없음" class="ipt_txt ipt_file" disabled="">
                </div>
            </dt>
            <dd>
                <label for="carousel_image_file" class="label_file">찾아보기..</label>
                <input type="file" name="" id="carousel_image_file" accept="image/*" onchange="setFileName()" style="display: none;">
            </dd>
        </dl>
        <p class="info_text" style="display: inline-block">* 이미지는 360x300(6:5)이 최적 사이즈입니다.</p>
        <p class="size_info_text" id="size_info"> 360x300(6:5) </p>
        <div class="image_setting">
            <p class="choose_img">이미지를 선택해주세요.</p>
            <img src="${pageContext.request.contextPath}/images/sample/sample_image_crop.png" alt="샘플이미지" style="width: 100%;  height: 100%;">
        </div>
    </div>
    <div class="lyr_btm">
        <div class="btnBox sz_small">
            <button type="button" class="btn_primary btn_lyr_close_submit_img_set" onclick="cropImageSubmit()">확인</button>
            <button type="button" class="btn_primary btn_lyr_close_end_img_set" onclick="exitCropPage()">취소</button>
        </div>
    </div>
</div>
<div title="Data Download" id="preparing-file-modal" style="display: none;">
    <div id="progressbar" style="width: 100%; height: 22px; margin-top: 20px;"></div>
</div>
<div title="Error" id="error-modal" style="display: none;">
    <p>생성실패.</p>
</div>
<style type="text/css">
    .txt_btn {display:block; padding:10px 0 0 0; text-align:center;}
    .txt_btn a {display:inline-block; height:30px; padding:0 15px; font-size:13px; text-align:center; text-decoration:none !important; line-height:28px; border:1px solid #ebeaef; border-radius:50px; box-shadow:rgba(53,60,79,0) 0 0 0 2px inset; background:#fff; color:rgba(53,60,79,1.0) !important; transition:all 0.3s ease-out;}
    .txt_btn a:hover {border:1px solid rgba(53,60,79,0) !important; box-shadow:rgba(53,60,79,1.0) 0 80px 0 2px inset; color:#fff !important; transition:all 0.3s ease-out;}
    .txt_btns {display:block; padding:5px 0 0 0; text-align:center;}
    .txt_btns a {display:inline-block; height:30px; margin:5px 10px 0 0; padding:0 15px; font-size:13px; text-align:center; text-decoration:none !important; line-height:28px; border:1px solid #ebeaef; border-radius:50px; box-shadow:rgba(53,60,79,0) 0 0 0 2px inset; background:#fff; color:rgba(53,60,79,1.0) !important; transition:all 0.3s ease-out;}
    .txt_btns a:last-child {margin-right:0;}
    .txt_btns a:hover {border:1px solid rgba(53,60,79,0) !important; box-shadow:rgba(53,60,79,1.0) 0 80px 0 2px inset; color:#fff !important; transition:all 0.3s ease-out;}
    .searchTextInTable{display: inline-block; color: #fa5151}
    .spliter_line{position: relative; width:100%; height: 16px;}
    .spliter_color{background-color: #f5f6f8; position: absolute; z-index: 99999; top:4px; left:-16px; width:240px; height: 8px;}
</style>
</body>
</html>
