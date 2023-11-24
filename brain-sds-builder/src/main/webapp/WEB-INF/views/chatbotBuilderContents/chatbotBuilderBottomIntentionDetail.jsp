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
    <title>chatbot builder Intention Detail</title>
    <script src="${pageContext.request.contextPath}/js/builderContents/chatbotBuilderIntentionDetail.js"></script>
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
            <button type="button" class="btn_primary btn_submit">확인</button>
            <button type="button" class="btn_primary btn_submit btn_lyr_close">취소</button>
        </div>
    </div>
</div>
<!-- //모달 챗봇 삭제 -->
<!-- 모달 정규식 설정 -->
<div id="chat_regex_setting" class="lyrBox">
    <div class="lyr_top">
        <h3>의도 정규식 설정</h3>
        <div class="lang_list">
            <ul id="ui_regex_lang_list">
                <li class="regex korea" onclick="regexLangClick('korea')">한국어</li>
                <li class="regex us" onclick="regexLangClick('us')">영어</li>
                <li class="regex japan" onclick="regexLangClick('japan')">일본어</li>
                <li class="regex china" onclick="regexLangClick('china')">중국어</li>
            </ul>
        </div>
        <button class="btn_lyr_close" onclick="closeBtn('#chat_regex_setting')"><span class="text_hide">닫기</span></button>
    </div>
    <div class="lyr_mid">
        <div class="ipt_container">
            <div id="div_lang_viewer" class="lang_viewer korea"></div>
            <dl id="div_regexRule" class="distance_point">
                <dt>규칙<div class="help">?<div class="help_desc"><i>규칙은 아래 순서를 따릅니다.<br>리스트를 잡고 드래그하면 순서를 바꿀 수 있습니다. </i></div></div></dt>
                <dd id="rule_place">
                    <div class="btn_box">
                        <div class="fr">
                            <button type="button" id="btn_addRegexRule" class="btn_secondary" onclick="addRegexList(event)">규칙 추가</button>
                        </div>
                    </div>
                    <ul id="regexList" class="drag_lst common_lst scroll">
                        <li>
                            <select name="regex_type" id="" class="select">
                                <!-- [D] 정규식 옵션 선택 시 옵션에 맞는 bg가 input에 반영되어야 함
                                  정규식 테이블에서 쓰이는 text UI 의 class와 value를 동일하게함
                                  .include : 텍스트에 포함
                                  .exclude : 텍스트에 포함하지 않음
                                  .match : 텍스트가 정확하게 일치함
                                  .start : 시작 텍스트
                                  .end : 종료 텍스트
                                -->
                                <option value="start" selected>시작 텍스트</option>
                                <option value="match">텍스트가 정확하게 일치함</option>
                                <option value="include">텍스트에 포함</option>
                                <option value="exclude">텍스트에 포함하지 않음</option>
                                <option value="end">종료 텍스트</option>
                            </select>
                            <input type="text" class="ipt_txt regex_type include" placeholder="ex) 메뉴">
                            <a class="btn_icon delete btn_lyr_open" onclick="delRegex(this)"></a>
                        </li>
                        <li>
                            <select name="regex_type" id="" class="select">
                                <option value="start">시작 텍스트</option>
                                <option value="match" selected>텍스트가 정확하게 일치함</option>
                                <option value="include">텍스트에 포함</option>
                                <option value="exclude">텍스트에 포함하지 않음</option>
                                <option value="end">종료 텍스트</option>
                            </select>
                            <input type="text" class="ipt_txt regex_type include">
                            <a class="btn_icon delete btn_lyr_open" onclick="delRegex(this)"></a>
                        </li>
                        <li>
                            <select name="regex_type" id="" class="select">
                                <option value="start">시작 텍스트</option>
                                <option value="match">텍스트가 정확하게 일치함</option>
                                <option value="include" selected>텍스트에 포함</option>
                                <option value="exclude">텍스트에 포함하지 않음</option>
                                <option value="end">종료 텍스트</option>
                            </select>
                            <input type="text" class="ipt_txt regex_type include">
                            <a class="btn_icon delete btn_lyr_open" onclick="delRegex(this)"></a>
                        </li>
                        <li>
                            <select name="regex_type" id="" class="select">
                                <option value="start">시작 텍스트</option>
                                <option value="match">텍스트가 정확하게 일치함</option>
                                <option value="include">텍스트에 포함</option>
                                <option value="exclude" selected>텍스트에 포함하지 않음</option>
                                <option value="end">종료 텍스트</option>
                            </select>
                            <input type="text" class="ipt_txt regex_type include">
                            <a class="btn_icon delete btn_lyr_open" onclick="delRegex(this)"></a>
                        </li>
                        <li>
                            <select name="regex_type" id="" class="select">
                                <option value="start">시작 텍스트</option>
                                <option value="match">텍스트가 정확하게 일치함</option>
                                <option value="include">텍스트에 포함</option>
                                <option value="exclude">텍스트에 포함하지 않음</option>
                                <option value="end" selected>종료 텍스트</option>
                            </select>
                            <input type="text" class="ipt_txt regex_type include">
                            <a class="btn_icon delete btn_lyr_open" onclick="delRegex(this)"></a>
                        </li>
                        <template id="tempRegexList">
                            <li>
                                <select name="regex_type" id="" class="select">
                                    <option value="start">시작 텍스트</option>
                                    <option value="match">텍스트가 정확하게 일치함</option>
                                    <option value="include" selected>텍스트에 포함</option>
                                    <option value="exclude">텍스트에 포함하지 않음</option>
                                    <option value="end">종료 텍스트</option>
                                </select>
                                <input type="text" class="ipt_txt regex_type include">
                                <a class="btn_icon delete btn_lyr_open" onclick="delRegex(this)"></a>
                            </li>
                        </template>
                    </ul>
                </dd>
            </dl>
            <dl id="check_regex" class="dl_inline">
                <dt>정규식 직접 입력</dt>
                <dd><input type="checkbox" id="checkRegex" class="checkbox">
                    <label style="height: 10px;width: 10px;" for="checkRegex"></label></dd>
                <dd><p id="regex_warning" style="color : red"></p></dd>
            </dl>
            <dl class="dl_inline">
                <dt>결과 정규식</dt>
                <dd><div class="iptBox"><input type="text" id="regexResult" class="ipt_txt" readonly></div></dd>
            </dl>
            <dl class="dl_inline regex_test">
                <dt>테스트 문장</dt>
                <dd>
                    <div class="iptBox">
                        <input type="text" id="testText" class="ipt_txt">
                        <div class="label">
                            <button type="button" class="btn_secondary btn_regex_test">테스트</button>
                        </div>
                    </div>
                </dd>
            </dl>

            <!-- [D] MATCHED, UNMATCHED 표시  -->
            <div id="matched" class="matched_mark matched" style="display: none">MATCHED</div>
            <div id="unmatched" class="matched_mark unmatched" style="display: none">UNMATCHED</div>
        </div>
        <div class="ipt_container"></div>
    </div>
    <div class="lyr_btm">
        <div class="btnBox sz_small">
            <button type="button" class="btn_primary btn_submit" onclick="confirmRegex()">확인</button>
            <button type="button" class="btn_primary btn_submit btn_lyr_close" onclick="closeBtn('#chat_regex_setting')">취소</button>
        </div>
    </div>
    <input type="hidden" id="isInsert">
</div>
<!-- //모달 정규식 설정 -->

<!-- 서브모달 정규식 삭제 -->
<div id="regex_list_delete" class="lyrBox sub_lyr">
    <div class="lyr_top">
        <h3>정규식 삭제</h3>
        <button class="btn_lyr_close"><span class="text_hide">닫기</span></button>
    </div>
    <div class="lyr_mid">
        <p class="infoTxt"><em>선택한 정규식</em>을(를) 삭제합니다.</p>
    </div>
    <div class="lyr_btm">
        <div class="btnBox sz_small">
            <button type="button" class="btn_primary btn_submit">확인</button>
            <button type="button" class="btn_primary btn_submit btn_lyr_close">취소</button>
        </div>
    </div>
</div>
<!-- 모달 정규식 삭제 -->
<div id="regex_delete" class="lyrBox">
    <div class="lyr_top">
        <h3>챗봇 정규식 삭제</h3>
        <button class="btn_lyr_close" onclick="closeBtn('#regex_delete')"><span class="text_hide">닫기</span></button>
    </div>
    <div class="lyr_mid">
        <p class="infoTxt"><em>선택한 정규식</em>을(를) 삭제합니다.</p>
    </div>
    <div class="lyr_btm">
        <div class="btnBox sz_small">
            <button type="button" class="btn_primary btn_submit" onclick="deleteRegex()">확인</button>
            <button type="button" class="btn_primary btn_submit btn_lyr_close" onclick="closeBtn('#regex_delete')">취소</button>
        </div>
    </div>
    <input type="hidden" id="regexIntentNum">
</div>
<!-- 모달 NQA 상세 및 추가 -->
<div id="chat_nqa_setting" class="lyrBox">
    <div class="lyr_top">
        <!-- [D] 개별 추가에서 띄울 경우 h3는 "NQA 문장 추가" 가 되어야 합니다 -->
        <h3>NQA 문장 상세</h3>
        <button class="btn_lyr_close" onclick="closeBtn('#chat_nqa_setting')"><span class="text_hide">닫기</span></button>
    </div>
    <div class="lyr_mid">
        <div class="tit"><span>의도 &raquo;</span> <span id="nqaIntent">안녕</span></div>
        <div class="ipt_container">
            <dl>
                <dt>학습문장</dt>
                <dd class="iptBox">
                    <input id="text_nqa" type="text" class="ipt_txt">
                </dd>
            </dl>
        </div>
    </div>
    <div class="lyr_btm">
        <div class="btnBox sz_small">
            <button type="button" class="btn_primary" onclick="addNqaSentence()">확인</button>
            <button type="button" class="btn_primary btn_lyr_close" onclick="closeBtn('#chat_nqa_setting')">취소</button>
        </div>
    </div>
</div>
<!-- 모달 NQA 삭제 -->
<div id="nqa_delete" class="lyrBox">
    <div class="lyr_top">
        <h3>NQA 문장 삭제</h3>
        <button class="btn_lyr_close" onclick="closeBtn('#nqa_delete')"><span class="text_hide">닫기</span></button>
    </div>
    <div class="lyr_mid">
        <p class="infoTxt"><em>선택한 NQA 문장</em>을(를) 삭제합니다.</p>
    </div>
    <div class="lyr_btm">
        <div class="btnBox sz_small">
            <button type="button" class="btn_primary btn_submit" onclick="deleteNqa()">확인</button>
            <button type="button" class="btn_primary btn_submit btn_lyr_close" onclick="closeBtn('#nqa_delete')">취소</button>
        </div>
    </div>
    <input type="hidden" id="questionId">
</div>
</body>
</html>
