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
    <script src="${pageContext.request.contextPath}/js/builderContents/chatbotBuilderIntention.js"></script>
</head>
<body>
<!-- 모달 의도 추가 -->
<div id="chat_intention_add" class="lyrBox">
    <div class="lyr_top">
        <h3>챗봇 의도 추가</h3>
        <button class="btn_lyr_close" onclick="closeBtn('#chat_intention_add')"><span class="text_hide">닫기</span></button>
    </div>
    <div class="lyr_mid">
        <div class="ipt_container">
            <dl>
                <dt><label for="label_intention_add">의도명</label></dt>
                <dd class="iptBox">
                    <input type="text" id="label_intention_add" class="ipt_txt">
                </dd>
            </dl>
        </div>
    </div>
    <div class="lyr_btm">
        <div class="btnBox sz_small">
            <button type="button" class="btn_primary btn_submit" onclick="addIntention()">확인</button>
            <button type="button" class="btn_primary btn_submit btn_lyr_close" onclick="closeBtn('#chat_intention_add')">취소</button>
        </div>
    </div>
</div>
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

<!-- 모달 의도 삭제 -->
<div id="chat_row_delete" class="lyrBox">
    <div class="lyr_top">
        <h3>챗봇 의도 삭제</h3>
        <button class="btn_lyr_close" onclick="closeBtn('#chat_row_delete')"><span class="text_hide">닫기</span></button>
    </div>
    <div class="lyr_mid">
        <p class="infoTxt"><em>선택한 의도</em>을(를) 삭제합니다.</p>
    </div>
    <div class="lyr_btm">
        <div class="btnBox sz_small">
            <button type="button" class="btn_primary btn_submit" onclick="deleteIntention()">확인</button>
            <button type="button" class="btn_primary btn_submit btn_lyr_close" onclick="closeBtn('#chat_row_delete')">취소</button>
        </div>
    </div>
    <input type="hidden" id="bertIntentName">
</div>
<!-- 모달 챗봇학습 -->
<div id="chat_learning_management" class="lyrBox">
    <div class="lyr_top">
        <h3>챗봇 학습</h3>
        <button class="btn_lyr_close el_intent_step5" onclick="closeBtn('#chat_learning_management')"><span class="text_hide">닫기</span></button>
    </div>
    <div class="lyr_mid">
        <div class="tbl_commands">
            <div class="fr">
                <a id="nqaExcelUploadBtn" href="#chat_intention_upload" class="btn_secondary" onclick="handleLyr()">엑셀 업로드</a>
                <a id="nqaExcelUploadingBtn" href="#chat_intention_upload" class="btn_secondary" style="display: none;">업로드 중...</a>
                <button type="button" class="btn_secondary" id="nqa_excel_download" onclick="nqaExcelDownload()">엑셀 다운로드</button>
                <button type="button" class="btn_secondary" id="nqa_excel_download_ing" onclick="mui.alert('학습문장을 다운로드 중입니다.')" style="display:none;">다운중...</button>
            </div>
        </div>
        <div class="tbl_wrap scroll">
            <table class="tbl_basic">
                <colgroup>
                    <col style="width: 60px;"><col style="width: 135px;"><col style="width: 135px;"><col style="width: 70px;"><col style="width: 70px;">
                    <col style="width: 80px;">
                </colgroup>
                <thead>
                <tr>
                    <th>구분</th>
                    <th>업데이트 시간</th>
                    <th>학습한 시간</th>
                    <th>의도수</th>
                    <th>문장수</th>
                    <th>학습</th>
                </tr>
                </thead>
                <tbody>
                <%--<tr>--%>
                    <%--<td>정규식</td>--%>
                    <%--<td>2020-12-26 11:41</td>--%>
                    <%--<td>-</td>--%>
                    <%--<td>30,000</td>--%>
                    <%--<td>155,555</td>--%>
                    <%--<td>학습불가</td>--%>
                <%--</tr>--%>
                <tr>
                    <td>학습문장</td>
                    <td id="nqa_updated_at">-</td>
                    <td>
                        <div id="nqa_trained_at">-</div>
                        <!-- [D] NQA 학습중 progress bar -->
                        <div class="progress" id="nqa_progress_bar" style="display: none">
                            <div class="progress_bar" style="transform: translate(30%)"></div></div>
                    </td>
                    <td id="nqa_answer_cnt">-</td>
                    <td id="nqa_question_cnt">-</td>
                    <td><button type="button" class="btn_line_primary" id="nqa_train_button" onclick="nqaTrain()" disabled>학습</button></td>
                    <%--<td><button type="button" class="btn_line_primary" onclick="nqaTrain()" disabled>학습</button></td>--%>
                </tr>
                <%--<tr>--%>
                    <%--<td>BERT</td>--%>
                    <%--<td>2020-12-26 11:41</td>--%>
                    <%--<td>-</td>--%>
                    <%--<td>30</td>--%>
                    <%--<td>195</td>--%>
                    <%--<td>학습불가</td>--%>
                <%--</tr>--%>
                </tbody>
            </table>
            <p class="info_text">* 학습문장은 업로드 후 학습을 진행해야 업로드 한 데이터가 적용됩니다.<br>* 다른 챗봇이 학습중인 경우 학습이 제한될 수 있습니다.</p>
        </div>
    </div>
    <div class="lyr_btm">
        <div class="btnBox">
            <button type="button" class="btn_primary btn_lyr_close" onclick="closeBtn('#chat_learning_management'); overlayTutorial(10);">닫기</button>
        </div>
    </div>
</div>
<!-- 모달 챗봇학습 파일 업로드 -->
<div id="chat_intention_upload" class="lyrBox sub_lyr">
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
                <form id="intentUpload" name="intentUpload" method="post" enctype="multipart/form-data"
                      action="${pageContext.request.contextPath}/upload/api/nqa/qa-sets/upload-files">
                <input type="file" name="excelFile" id="excelFile" accept=".xls, .xlsx" style="display: none;">
                <input type="text" name="host" style="display: none;">
                <input type="text" name="hostName" style="display: none;">
                </form>
            </dd>
        </dl>
        <p class="info_text">
            <span class="text">* 파일 업로드 시 기존 데이터가 덮어쓰기 됩니다.</span>
            <span class="text">* 업로드 전 기존 데이터를 다운로드 받으시길 권장합니다.</span>
        </p>
    </div>
    <div class="lyr_btm">
        <div class="btnBox sz_small">
            <button type="button" id="btn_upload" class="btn_primary btn_submit" onclick="uploadExcel()">저장</button>
            <button type="button" class="btn_primary btn_lyr_close" onclick="closeBtn('#chat_intention_upload')">취소</button>
        </div>
    </div>
</div>


<!-- 모달 챗봇학습 대기 -->
<div id="nqa_train_wait" class="lyrBox sub_lyr">
    <div class="lyr_top">
        <h3>NQA 서버 접속 중</h3>
        <button class="btn_lyr_close"><span class="text_hide">닫기</span></button>
    </div>
    <div class="lyr_mid">
        <p class="info_text">
            NQA 학습을 위해 서버 연결중입니다.<br>
            이용자가 많아 접속이 지연되고 있습니다. 잡시만 기다려 주세요.
        </p>
        <div class="progress" id="nqa_wait_progress_bar">
        <div class="progress_bar" style="transform: translate(0%)"></div></div>
    </div>
    <div class="lyr_btm">
        <div class="btnBox sz_small">
            <button type="button" class="btn_primary btn_lyr_close" onclick="closeBtn('#nqa_train_wait')">취소</button>
        </div>
    </div>
</div>


</body>
</html>
