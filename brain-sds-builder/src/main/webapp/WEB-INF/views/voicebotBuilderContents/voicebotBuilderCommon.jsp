<%--
  Created by IntelliJ IDEA.
  User: mindslab
  Date: 2021-05-17
  Time: 오전 11:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/font.css?v=${queryString}">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset_v2.css?v=${queryString}">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/an-select.css?v=${queryString}">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/datatables.min.css"/>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/voicebotBuilder.css?v=${queryString}">

  <script src="${pageContext.request.contextPath}/js/jquery-3.1.0.min.js"></script>
  <script src="${pageContext.request.contextPath}/js/popper-2.5.4.min.js"></script>
  <script src="${pageContext.request.contextPath}/js/go.js?v=${queryString}"></script>
  <script src="${pageContext.request.contextPath}/js/mui_alert.js?v=${queryString}"></script>
  <script src="${pageContext.request.contextPath}/js/an-select.js?v=${queryString}"></script>
  <script src="${pageContext.request.contextPath}/js/voicebotBuilderContents/voicebotBuilderCommon.js?v=${queryString}"></script>
  <script src="${pageContext.request.contextPath}/js/datatables.min.js"></script>
  <script src="${pageContext.request.contextPath}/js/dataTables.rowGroup.js"></script>
  <script src="${pageContext.request.contextPath}/js/full_numbers_no_ellipses.js"></script>
  <script src="${pageContext.request.contextPath}/js/socket.io-1.4.0.js"></script>
  <script src="${pageContext.request.contextPath}/js/sortable.min.js"></script>

  <title>음성봇빌더</title>
</head>
<body>
<%-- vb_wrap vb:voicebot --%>
<div id="vb_wrap" class="loading">
  <%-- vb_container --%>
  <%-- [D] class "test_on" 이 추가되면 테스트 영역이 보여집니다 --%>
  <div id="vb_container"  class="">
    <%-- container_box --%>
    <div class="container_box">
      <%-- vb_header --%>
      <div id="vb_header">
        <div>
          <span class="title">시나리오 목록</span>
          <input type="text" id="scenario_list" class="select" readonly>
          <button type="button" class="btn_icon_primary setting" data-modal="set_scenario_list" onclick="handleModal()" disabled>설정</button>
          <button type="button" class="btn_icon_primary plus" data-modal="add_scenario_list" onclick="handleModal()">추가</button>
        </div>

        <div id="vb_gnb">
          <ul>
            <%-- [D] button 이 disabled 이면 click, hover 효과가 일어나지 않습니다 --%>
            <li><button type="button" data-key="scenario" disabled onclick="changeContents(PAGES.SCENARIO);">시나리오</button></li>
            <li><button type="button" data-key="task" disabled onclick="changeContents(PAGES.TASK);">TASK</button></li>
            <li><button type="button" data-key="intention" disabled onclick="changeContents(PAGES.INTENTION);">의도</button></li>
          </ul>

          <ul>
            <li><button type="button" data-key="replace" disabled onclick="changeContents(PAGES.REPLACE_DIC);">치환사전</button></li>
          </ul>

          <%-- [D] 이전페이지에 active 가 있어야 보여집니다
          이전페이지 버튼은 테스크 추가, 테스크 수정, 의도 추가, 의도 수정 페이지로 페이지 이동 될 때 보여집니다
          --%>
          <button type="button" id="go_back" class="btn_secondary" onclick="changeContents(menuPrev)">이전페이지</button>
        </div>

        <div class="excel_btns">
          <button type="button" class="btn_secondary" onclick="downloadFile()">엑셀 샘플 다운로드</button>
          <button type="button" title="의도와 TASK가 함께 업로드 됩니다." class="btn_secondary btn_excel_up" data-modal="upload_excel" disabled onclick="handleModal()">엑셀 업로드</button>
          <button type="button" class="btn_secondary btn_excel_down" disabled onclick="scenarioDownloadFile()">엑셀 다운로드</button>
          <button type="button" id="test_on" class="btn_primary" disabled onclick="openTest()">테스트</button>

          <%--          엑셀 샘플 다운로드 form--%>
          <form action="voiceBot/sampleDownload"
                target="downFrame" name="downloadForm" method="post">
          </form>
          <%--          엑셀 다운로드 form--%>
          <form action="simpleBot/scenarioDownload"
                target="downFrame" id="downloadScenario" name="downloadScenario" method="post">
            <input type="hidden" name="scenarioJson" id="scenarioJson">
            <input type="hidden" name="scenarioHost" id="scenarioHost">
          </form>
        </div>
      </div>
      <%-- //vb_header --%>
      <%-- container_box --%>
      <%-- [D] 목록 선택 전 보여줄 화면 --%>
      <div id="vb_content">
        <p class="text_bg">시나리오를 선택해주세요</p>
      </div>
    </div>
    <%-- //vb_cont_box --%>

    <!-- vb_test -->
    <div class="vb_test data_debug scroll ">
      <div class="float_box">
        <button type="button" class="test_off btn_primary fr" onclick="closeTest()">닫기</button>
      </div>

      <!-- resize_areas -->
      <div class="resize_areas">
        <div class="resize_area">
          <!-- vb_tab -->
          <div class="vb_tab">
            <!-- vb_tab_menu 테스트데이터, 디버그 -->
            <div class="vb_tab_menu">
              <button type="button">테스트 데이터</button>
              <button type="button">분석(debug)</button>
            </div>
            <!-- //vb_tab_menu -->
            <!-- vb_tab_cont 테스트데이터 -->
            <div class="vb_tab_cont">
              <div class="test_user_data">
                <div class="float_box tbl_command_box">
                  <button type="button" id="btn_del_cust_row" class="btn_line_secondary" onclick="deleteCustRow();">선택 삭제</button>
                  <button type="button" id="btn_add_cust_row" class="btn_secondary fr" onclick="addCustRow();">테스트 데이터 추가</button>
                </div>

                <ul class="common_lst_tbltype scroll" id="testDataList">
                  <li class="no_list">등록된 테스트 데이터가 없습니다</li>
                  <li>
                    <div class="li_col">
                      <input type="checkbox" name="intent_task" class="checkbox" id="list01">
                      <label for="list01"></label>
                    </div>
                    <div class="li_col">
                      <input type="text" class="ipt_txt" placeholder="이름">
                    </div>
                    <div class="li_col">
                      <input type="text" class="ipt_txt" placeholder="홍길동">
                    </div>
                  </li>
                  <li>
                    <div class="li_col">
                      <input type="checkbox" name="intent_task" class="checkbox" id="list02">
                      <label for="list02"></label>
                    </div>
                    <div class="li_col">
                      <input type="text" class="ipt_txt" placeholder="생년월일">
                    </div>
                    <div class="li_col">
                      <input type="text" class="ipt_txt" placeholder="930111">
                    </div>
                  </li>
                  <li>
                    <div class="li_col">
                      <input type="checkbox" name="intent_task" class="checkbox" id="list03">
                      <label for="list03"></label>
                    </div>
                    <div class="li_col">
                      <input type="text" class="ipt_txt" placeholder="상품명">
                    </div>
                    <div class="li_col">
                      <input type="text" class="ipt_txt" placeholder="네모보험">
                    </div>
                  </li>
                  <li>
                    <div class="li_col">
                      <input type="checkbox" name="intent_task" class="checkbox" id="list04">
                      <label for="list04"></label>
                    </div>
                    <div class="li_col">
                      <input type="text" class="ipt_txt" placeholder="">
                    </div>
                    <div class="li_col">
                      <input type="text" class="ipt_txt" placeholder="">
                    </div>
                  </li>
                </ul>
              </div>
            </div>
            <!-- //vb_tab_cont 테스트데이터 -->
            <!-- vb_tab_cont 디버그 -->
            <div class="vb_tab_cont">
              <div class="test_debug_view">
                <div class="command">
                  <div class="fl">
                    <span>분석결과 <span id="nowDebugIndex">1</span>&#47;<span id="nowDebugStack">1</span></span>
                    <div class="help">?
                      <div class="help_desc" id="debug_test_desc">
                      INPUT : 사용자가 입력한 값에 대한 정보<br/>
                      input text : 사용자가 입력한 텍스트 및 발화<br/>
                      type : 입력 값에 대한 유형 <br/>
                      &emsp;-utter :사용자 문장 <br/>
                      &emsp;-intent : 시작 태스크<br/><br/>
                      INTENT : 의도 정보<br/>
                      engine : 의도를 찾은 엔진에 대한 정보 <br/>
                      &emsp;-REGEX : 정규식<br/>
                      &emsp;-NQA : 학습문장<br/>
                      &emsp;-BERT : 사전 학습 문장<br/>
                      prob : 의도 정확도  (0 ≤ prob ≤ 1)<br/><br/>
                      TASK : 태스크 정보<br/>
                      prev task : 이전 태스크 명<br/>
                      task rel : 이전 태스크와의 관계정보<br/>
                      &emsp;ex) 이전 태스크 -> 의도 = 다음태스크<br/>
                      task : 현재 태스크 명<br/><br/>
                      ANSWER : 현재 태스크에 대한 답변 정보<br/>
                      text : 현재 태스크에 대한 답변 문장<br/><br/>
                      SDS LOG : 각 엔진에 대한 결과 기록<br/>
                      &emsp;-Matched : 해당 엔진에서 확인 <br/>
                      &emsp;-UnMatched : 해당 엔진에서 미확인
                      </div>
                    </div>
                  </div>
                  <div class="fr">
                    <button type="button" class="btn_icon_line prev" onclick="renderDebugList(nowIndexDebug-1)">이전</button>
                    <button type="button" class="btn_icon_line next" onclick="renderDebugList(nowIndexDebug+1)">다음</button>
                    <button type="button" class="btn_icon_line refresh" id="testChatRefresh">새로고침</button>
                  </div>
                </div>
                <dl class="content scroll" id="test_result_id">
                  <dt>INPUT</dt>
                  <dd>
                    <p><span class="highlight">type</span><em></em></p>
                  </dd>
                  <dt>INTENT</dt>
                  <dd>
                    <p><span class="highlight">prob</span><em></em></p>
                  </dd>
                  <dt>TASK</dt>
                  <dd>
                    <p><span class="highlight">prev task</span><em> </em></p>
                    <p><span class="highlight">task rel</span><em> </em></p>
                    <p><span class="highlight">task</span><em></em></p>
                  </dd>
                  <dt>ANSWER</dt>
                  <dd>
                    <p><span class="highlight">text</span><em></em></p>
                  </dd>
                </dl>
              </div>
            </div>
            <!-- //vb_tab_cont 디버그 -->
          </div>
          <!-- //vb_tab -->
        </div>

        <div class="drag_resize"><img src="../images/ico_grabmark.svg" alt="드래그 할 수 있다는 표시"></div>

        <div class="resize_area">
          <!-- vb_tab -->
          <div class="vb_tab">
            <!-- vb_tab_menu 챗봇, 음성봇 -->
            <div class="vb_tab_menu">
              <button type="button">챗봇</button>
              <button type="button">음성봇</button>
            </div>
            <!-- //vb_tab_menu -->
            <!-- vb_tab_cont 챗봇 -->
            <div class="vb_tab_cont" style="position: relative;">
              <button type="button" class="chat_talk_refresh">처음으로</button>
              <ul class="chatting chat_talk scroll">
                <li class="blind">
                  <p>START를 눌러 대화를</p>
                  <p>시작해주세요.</p>
                  <button type="button" class="btn_primary small btn_delete_blind" onclick="startChat();">START</button>
                </li>

                <li class="system_entry">
                  <em>채팅이 연결되었습니다.</em>
                </li>
                <li class="bot">
                  <div class="message">
                    message
                  </div>
                  <div class="time">time</div>
                </li>
                <li class="user">
                  <div class="message">
                    message
                  </div>
                  <div class="time">time</div>
                </li>
              </ul>

              <div class="ipt_box">
                <input type="text" class="ipt_txt" id="sendTxt" placeholder="메세지를 입력해주세요" disabled>
                <button type="button" class="text_hide" id="sendBtn" disabled>전송</button>
              </div>
            </div>
            <!-- //vb_tab_cont -->
            <!-- vb_tab_cont 음성봇 -->
            <div class="vb_tab_cont" style="position:relative;">
              <div class="voice_talk_info">
                <input type="number" class="ipt_txt" id="testerPhone" placeholder="전화번호 입력">
                <button type="button" class="btn_primary small" id="voice_test_start" onclick="voiceTestStart();">전화걸기</button>
              </div>
              <button type="button" class="voice_talk_refresh" onclick="voiceTalkRefresh();">내용 지우기</button>
              <ul class="chatting voice_talk scroll">
                <li class="voicebot_start blind">
                  <p>상단 입력창에서</p>
                  <p>정보를 입력하신 후</p>
                  <p>전화 걸기를 눌러주세요.</p>
                </li>

                <li class="system_entry">
                  <em>전화가 연결되었습니다.</em>
                </li>
                <li class="bot">
                  <div class="message">
                    message
                  </div>
                  <div class="time">time</div>
                </li>
                <li class="user">
                  <div class="message">
                    message
                  </div>
                  <div class="time">time</div>
                </li>
              </ul>
            </div>
            <!-- //vb_tab_cont -->
          </div>
          <!-- //vb_tab -->
        </div>
      </div>
      <!-- //resize_areas -->
    </div>
    <!-- //vb_test -->

    <!-- vb_test -->
    <div class="vb_test only_debug">
      <div class="float_box">
        <button type="button" class="test_off btn_primary fr" onclick="closeTest()">닫기</button>
      </div>
      <div class="test_debug_view">
        <div class="command">
          <div class="fl">
            <span>분석결과 <span id="nowPrevDebugIndex">1</span>&#47;<span id="nowPrevDebugStack">1</span></span>
            	<div class="help">?
                	<div class="help_desc" id="debug_test_desc">
                	INPUT : 사용자가 입력한 값에 대한 정보<br/>
                	input text : 사용자가 입력한 텍스트 및 발화<br/>
                	type : 입력 값에 대한 유형 <br/>
                	&emsp;-utter :사용자 문장 <br/>
                	&emsp;-intent : 시작 태스크<br/><br/>
                	INTENT : 의도 정보<br/>
                	engine : 의도를 찾은 엔진에 대한 정보 <br/>
                	&emsp;-REGEX : 정규식<br/>
                	&emsp;-NQA : 학습문장<br/>
                	&emsp;-BERT : 사전 학습 문장<br/>
                	prob : 의도 정확도  (0 ≤ prob ≤ 1)<br/><br/>
                	TASK : 태스크 정보<br/>
                	prev task : 이전 태스크 명<br/>
                	task rel : 이전 태스크와의 관계정보<br/>
                	&emsp;ex) 이전 태스크 -> 의도 = 다음태스크<br/>
                	task : 현재 태스크 명<br/><br/>
                	ANSWER : 현재 태스크에 대한 답변 정보<br/>
                	text : 현재 태스크에 대한 답변 문장<br/><br/>
                	SDS LOG : 각 엔진에 대한 결과 기록<br/>
                	&emsp;-Matched : 해당 엔진에서 확인 <br/>
                	&emsp;-UnMatched : 해당 엔진에서 미확인
                	</div>
                </div>
          </div>
          <div class="fr">
            <button type="button" class="btn_icon_line prev" onclick="renderPrevDebugList(nowPrevIndexDebug-1)">이전</button>
            <button type="button" class="btn_icon_line next" onclick="renderPrevDebugList(nowPrevIndexDebug+1)">다음</button>
            <button type="button" class="btn_icon_line refresh" onclick="prevTaskDebugRefresh();">새로고침</button>
          </div>
        </div>
        <dl class="content scroll" id="test_prev_result_id">
          <dt>INPUT</dt>
          <dd>
            <p><span class="highlight">type</span><em></em></p>
          </dd>
          <dt>INTENT</dt>
          <dd>
            <p><span class="highlight">prob</span><em></em></p>
          </dd>
          <dt>TASK</dt>
          <dd>
            <p><span class="highlight">prev task</span><em></em></p>
            <p><span class="highlight">task rel</span><em></em></p>
            <p><span class="highlight">task</span><em></em></p>
          </dd>
          <dt>ANSWER</dt>
          <dd>
            <p><span class="highlight">text</span><em></em></p>
          </dd>
        </dl>
      </div>
      <div class="test_debug_data">
        <div class="ipt_box" id="prevTaskDiv">
          <label for="prev_task">이전 태스크</label>
			<input type="text" id="prev_task" class="select" readOnly>
        </div>
        <div class="ipt_box">
          <label for="test_data">태스트 문장</label>
          <input type="text" name="test_data" id="test_data" class="ipt_txt">
        </div>
        <button type="button" id="btn_debug_test" class="btn_secondary" onclick="debugTest();">테스트 하기(ENTER)</button>
      </div>
    </div>
    <!-- //vb_test -->
  </div>
  <%-- //vb_container --%>
</div>
<%-- //vb_wrap --%>

<%-- 모달 --%>
<div class="vb_modal" id="set_scenario_list">
  <div class="vb_modal_dialog">
    <div class="dlg_header">
      <span class="title">시나리오 관리</span>
      <button type="button" class="btn_modal_close">닫기</button>
    </div>
    <div class="dlg">
      <div class="ipt_box">
        <label for="set_scenario">시나리오</label>
        <input type="text" id="set_scenario" class="ipt_txt" disabled>
      </div>
      <div class="ipt_box">
        <label for="mdfy_scenario_name">수정할 이름</label>
        <input type="text" id="mdfy_scenario_name" class="ipt_txt">
      </div>
    </div>
    <div class="dlg_footer">
      <button type="button" class="btn_primary" onclick="handleModifyVoiceBotList()">수정</button>
      <button type="button" class="btn_secondary" onclick="handleDeleteVoiceBotList()">삭제</button>
    </div>
  </div>
</div>

<div class="vb_modal" id="add_scenario_list">
  <div class="vb_modal_dialog">
    <div class="dlg_header">
      <span class="title">시나리오 추가</span>
      <button type="button" class="btn_modal_close">닫기</button>
    </div>
    <div class="dlg">
      <div class="ipt_box">
        <label for="scenario_name">시나리오명</label>
        <input type="text" id="scenario_name" class="ipt_txt">
      </div>
      <div class="ipt_box">
        <label for="scenario_lang">언어</label>
        <select name="scenario_lang" id="scenario_lang" class="select">
          <option value="default">언어를 선택해주세요</option>
          <option value="lang1">한국어</option>
          <option value="lang2">영어</option>
        </select>
      </div>
    </div>
    <div class="dlg_footer">
      <button type="button" class="btn_primary" onclick="handleAddVoiceBotList()">확인</button>
      <button type="button" class="btn_secondary btn_modal_close">취소</button>
    </div>
  </div>
</div>

<div class="vb_modal" id="upload_excel">
  <div class="vb_modal_dialog">
    <div class="dlg_header">
      <span class="title">엑셀 업로드</span>
      <button type="button" class="btn_modal_close">닫기</button>
    </div>
    <div class="dlg">
      <div class="ipt_file_box">
        <form id="excelUploadScenarioForm" name="excelUploadScenarioForm" method="post" enctype="multipart/form-data"
              action="simpleBot/uploadScenarioV2">
          <input type="text" id="excel_file_v2_label" class="ipt_txt" placeholder="선택된 파일 없음" disabled>
          <input type="file" name="excel_file_v2" id="excel_file_v2" class="ipt_file" accept=".xls, .xlsx">
          <input type="hidden" name="simplebotId">
        </form>
        <span class="file_label">
          <label for="excel_file_v2"><spring:message code="MESSAGE.EXCEL.BROWSE" text="찾아보기.."/></label>
        </span>
      </div>
      <div class="info_box">
        <p class="info_small primary">* 파일 업로드 시 기존 데이터가 덮어쓰기 됩니다.</p>
        <p class="info_small primary">* 업로드 전 기존 데이터를 다운로드 받으시길 권장합니다.</p>
      </div>
    </div>
    <div class="dlg_footer">
      <button type="button" class="btn_primary" onclick="uploadV2AndSave()">저장</button>
      <button type="button" class="btn_secondary btn_modal_close">취소</button>
    </div>
  </div>
</div>

<div class="vb_modal" id="learning_nqa">
  <div class="vb_modal_dialog">
    <div class="dlg_header">
      <span class="title">학습하기</span>
      <div class="help">?
        <div class="help_desc" style="width: 250px;">엑셀 업로드 시 엑셀 작성 방법<br><br>A:1 셀에 Intent, B:1 셀에 Question을 작성하신 다음 A:2 부터 학습 할 문장을 제목줄에 맞추어 작성해주세요.<br><br>-제목줄 데이터 의미<br>Intent: 등록된 의도 이름<br>Question: 작성한 Intent에 학습시킬 문장
          <div class="image">
            <img src="${pageContext.request.contextPath}/images/help_img_nqa.png" alt="엑셀 업로드 작성 예시">
          </div>
        </div>
      </div>
      <button type="button" class="btn_modal_close">닫기</button>
    </div>
    <div class="dlg">
      <div class="tbl_command_box">
        <button type="button" class="btn_line_secondary" data-modal="nqa_excel_upload" onclick="handleModal()">엑셀 업로드</button>
        <button type="button" class="btn_line_secondary" onclick="nqaExcelDownload()">엑셀 다운로드</button>
      </div>
      <div class="table_wrap">
        <table id="tbl_nqa_learn" class="tbl_info">
          <colgroup>
            <col>
            <col>
            <col style="width: 70px;">
            <col style="width: 80px;">
          </colgroup>
          <thead>
          <tr>
            <th>업데이트 시간</th>
            <th>학습한 시간</th>
            <th>의도 수</th>
            <th>문장 수</th>
          </tr>
          </thead>
          <tbody>
          <tr>
            <td id="nqa_updated_at">-</td>
            <td>
              <div id="nqa_trained_at">-</div>
              <div class="progress" id="nqa_trained_progress" style="display: none;">
                <div class="progress_bar" style="transform: translate(0%)"></div>
              </div>
            </td>
            <td id="nqa_total_cnt">-</td>
            <td id="nqa_question_cnt">-</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
    <div class="dlg_footer">
      <button type="button" class="btn_primary bubble_box" onclick="nqaTrain()">학습하기
        <span class="bubble arrow_down nqa">학습해야 할 데이터가 있습니다.</span>
      </button>

      <button type="button" class="btn_secondary btn_modal_close">닫기</button>
    </div>
  </div>
</div>

<div class="vb_modal_single" id="nqa_excel_upload">
  <div class="vb_modal_dialog">
    <div class="dlg_header">
      <span class="title">학습문장 엑셀 업로드</span>
      <button type="button" class="btn_modal_close">닫기</button>
    </div>
    <div class="dlg">
      <div class="ipt_file_box">
        <form id="nqaExcelUploadfrm" name="intentUpload" method="post" enctype="multipart/form-data">
          <input type="hidden" name="host" id="nqaExcelUpHost">
          <input type="hidden" name="hostName" id="nqaExcelUpHostName">
          <input type="file" name="excelFile" id="excel_file_nqa" class="ipt_file" accept=".xls, .xlsx">
        </form>
        <input type="text" id="excel_file_nqa_label" class="ipt_txt" placeholder="선택된 파일 없음" disabled>
        <span class="file_label">
          <label for="excel_file_nqa">찾아보기..</label>
        </span>
      </div>


      <div class="info_box">
        <p class="info_small primary">* 파일 업로드 시 기존 데이터가 덮어쓰기 됩니다.</p>
        <p class="info_small primary">* 업로드 전 기존 데이터를 다운로드 받으시길 권장합니다.</p>
      </div>
    </div>
    <div class="dlg_footer">
      <button type="button" class="btn_primary" onclick="nqaExcelUpload()">확인</button>
      <button type="button" class="btn_secondary btn_modal_close">취소</button>
    </div>
  </div>
</div>

<div class="vb_modal_single" id="nqa_waiting_progress">
  <div class="vb_modal_dialog">
    <div class="dlg_header">
      <span class="title">NQA 서버 접속 중</span>
      <button type="button" class="btn_modal_close">닫기</button>
    </div>
    <div class="dlg">
      <div class="progress" id="nqa_waiting_progress_bar">
        <div class="progress_bar" style="transform: translate(0%)"></div>
      </div>
      <div class="info_box">
        <p class="info_small primary">* NQA 학습을 위해 서버 연결중입니다.<br>
          이용자가 많아 접속이 지연되고 있습니다. 잡시만 기다려 주세요.</p>
      </div>
    </div>
    <div class="dlg_footer">
      <button type="button" class="btn_secondary btn_modal_close">학습 취소</button>
    </div>
  </div>
</div>

<input type="hidden" id="m2uUrl" value="${m2uUrl}">
<input type="hidden" id="env" value="${env}">
<input type="hidden" name="uploadExcelV2NoFile" value="<spring:message code="MESSAGE.EXCEL.SELECT" javaScriptEscape="true"/>">
<input type="hidden" name="saveScenarioSuccess" value="<spring:message code="MESSAGE.APPLY.SAVE" javaScriptEscape="true"/>">
<input type="hidden" name="saveScenarioError" value="<spring:message code="MESSAGE.APPLY.CANTSAVE" javaScriptEscape="true"/>">
</body>
</html>
