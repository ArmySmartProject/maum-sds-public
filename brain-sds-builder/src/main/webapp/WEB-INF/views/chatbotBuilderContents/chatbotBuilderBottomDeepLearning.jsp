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
    <script src="${pageContext.request.contextPath}/js/jquery-ui-1.12.0/jquery-ui.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/jquery.fileDownload.js"></script>
    <title>chatbot builder Answer</title>
</head>
<body>

<!-- 모달 bert 학습 요청 팝업 -->
<div id="lyr_bert_learn_req" class="lyrBox">
    <div class="lyr_top">
        <h3>bert 학습</h3>
        <button class="btn_lyr_close"><span class="text_hide">닫기</span></button>
    </div>
    <div class="lyr_mid">
        <div>
            <button class="btn_secondary small" data-engine="ITF" onclick="downloadSampleFile();">itf 샘플 파일 다운로드</button>
        </div>

        <div style="margin-top: 15px;">
            <div class="ipt_file_box">
                <!-- [D] input[file] value = input[text] value -->
                <input type="text" name="itfFile" placeholder="선택된 파일 없음" class="ipt_txt ipt_file" disabled>
                <label for="itfFile" class="label_file">찾아보기..</label>
                <input type="file" id="itfFile" name="itfFile" onchange="attachFile();" accept=".xls, .xlsx" style="display: none;">
            </div>

            <div style="margin-top: 5px;">
                <input type="email" id="itfEmail" name="itfEmail" placeholder="이메일을 입력해주세요" class="ipt_txt" autocomplete="off" style="width: 280px;">
            </div>
        </div>

        <p class="info_text">
            <span class="text">* 엑셀만 업로드 가능합니다.</span>
            <span class="text">* 저장 시 이전에 학습한 내용이 사라지며 새로 학습 됩니다.</span>
            <span class="text">* 이메일 입력 시 이메일로 학습진행 상황을 받아볼 수 있습니다.</span>
        </p>
    </div>
    <div class="lyr_btm">
        <div class="btnBox sz_small">
            <button type="button" class="btn_primary" data-engine="ITF" onclick="requestLearning();">학습요청</button>
            <button type="button" class="btn_primary btn_lyr_close">취소</button>
        </div>
    </div>
</div>
<!-- //모달 bert 학습 요청 팝업 -->

<!-- 모달 mrc 학습 요청 팝업 -->
<div id="lyr_mrc_learn_req" class="lyrBox">
    <div class="lyr_top">
        <h3>mrc 학습</h3>
        <button class="btn_lyr_close"><span class="text_hide">닫기</span></button>
    </div>
    <div class="lyr_mid">
        <div>
            <button class="btn_secondary small" data-engine="MRC" onclick="downloadSampleFile();">mrc 샘플 파일 다운로드</button>
        </div>

        <div style="margin-top: 15px;">
            <div class="ipt_file_box">
                <!-- [D] input[file] value = input[text] value -->
                <input type="text" name=mrcFile" placeholder="선택된 파일 없음" class="ipt_txt ipt_file" disabled>
                <label for="mrcFile" class="label_file">찾아보기..</label>
                <input type="file" id="mrcFile" name=mrcFile" onchange="attachFile();" accept=".json" style="display: none;">
            </div>

            <div style="margin-top: 5px;">
                <input type="email" id="mrcEmail" name="mrcEmail" placeholder="이메일을 입력해주세요" class="ipt_txt" autocomplete="off" style="width: 280px;">
            </div>
        </div>

        <p class="info_text">
            <span class="text">* .json 만 업로드 가능합니다.</span>
            <span class="text">* 저장 시 이전에 학습한 내용이 사라지며 새로 학습 됩니다.</span>
            <span class="text">* 이메일 입력 시 이메일로 학습진행 상황을 받아볼 수 있습니다.</span>
        </p>

    </div>
    <div class="lyr_btm">
        <div class="btnBox sz_small">
            <button type="button" class="btn_primary" data-engine="MRC" onclick="requestLearning();">학습요청</button>
            <button type="button" class="btn_primary btn_lyr_close">취소</button>
        </div>
    </div>
</div>
<!-- //모달 mrc 학습 요청 팝업 -->

<!-- 모달 학습 취소 팝업 -->
<div id="lyr_learn_cancel" class="lyrBox">
    <div class="lyr_top">
        <h3>학습요청 취소</h3>
        <button class="btn_lyr_close"><span class="text_hide">닫기</span></button>
    </div>
    <div class="lyr_mid">
        <div style="margin-top: 15px;">
            <div style="margin-top: 5px;">
                <input type="email" id="reqCancel" placeholder="이메일을 입력해주세요" class="ipt_txt" autocomplete="off" style="width: 280px;">
            </div>
        </div>

        <p class="info_text">
            <span class="text">* 이메일 입력 시 이메일로 학습취소 상황을 받아볼 수 있습니다.</span>
        </p>
    </div>
    <div class="lyr_btm">
        <div class="btnBox sz_small">
            <button type="button" class="btn_primary btn_event_save">확인</button>
            <button type="button" class="btn_primary btn_lyr_close btn_event_close">취소</button>
        </div>
    </div>
</div>
<!-- //모달 학습 취소 팝업 -->


<style type="text/css">
    .btn_secondary.small {height: 25px; line-height: 25px;}
</style>
</body>
</html>
