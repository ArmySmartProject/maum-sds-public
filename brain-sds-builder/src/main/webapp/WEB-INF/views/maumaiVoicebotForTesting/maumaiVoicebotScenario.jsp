<%--
  Created by IntelliJ IDEA.
  User: mindslab
  Date: 2021-05-17
  Time: 오전 11:24
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

  <script src="${pageContext.request.contextPath}/js/jquery.form.js?v=${queryString}"></script>
  <script src="${pageContext.request.contextPath}/js/maumaiVoicebotForTesting/maumaiVoicebotScenario.js?v=${queryString}"></script>
  <script src="${pageContext.request.contextPath}/js/voicebotBuilderContents/goJsFlowChart.js?v=${queryString}"></script>
  <title>음성봇빌더 || 시나리오</title>

</head>
<body>
<div class="float_box">
  <%-- specific_tasks --%>
  <div class="specific_tasks fl">
    <span>태스크 그룹 보기</span>
    <div class="tasks">
      <%-- [D] default : 첫번째 셀렉트만 보여짐
        셀렉트를 선택하면 그 다음 depth 셀렉트가 보여져야 합니다
      --%>
      <%--  <input type="text" id="selectTaskGroup1" class="select" disabled readOnly>--%>
      <%--  <input type="text" id="selectTaskGroup2" class="select" readOnly>--%>
      <%--  <input type="text" id="selectTaskGroup3" class="select" readOnly>--%>
    </div>
  </div>
  <%-- //specific_tasks --%>
</div>

<%-- scenario --%>
<div style="position: relative;">
  <button type="button" id="btnFullScreen" class="text_hide" title="플로우 차트 전체화면 on/off">플로우 차트 전체화면 켜기/끄기</button>
  <div id="flowChart" style="height:700px;"></div>
</div>
<input type="hidden" id="nodeData" onchange="setNodeData()">
<%-- //scenario --%>
<div class="scenario_set" style="display: none">
  <a class="now_task" title="현재 태스크 자세히보기">선택 없음</a>

  <%-- save --%>
  <div class="save fr">
    <span id="update_date"></span>
    <%-- [D] 저장을 한 번도 하지 않은 경우 : Never updated
      저장을 한 이력이 있는 경우 : 저장날짜 updated --%>
    <button type="button" class="btn_primary" onclick="saveConfirm()">저장</button>
  </div>
  <%-- //save --%>

  <%-- vb_tab --%>
  <div class="vb_tab">
    <%-- tab_menu 챗봇이 할 말, 설정 --%>
    <div class="vb_tab_menu">
      <button type="button" class="active">답변</button>
      <button type="button">설정</button>
    </div>
    <%-- tab_cont 챗봇이 할 말 --%>
    <div class="vb_tab_cont active">
      <div class="tab_cont_tit">
        <p>기본 답변</p>
      </div>

      <audio controls preload="none" id="TTSAnswer" src=""></audio>

      <%-- vb_answer template --%>
      <template id="add_intent">
        <div class="vb_answer">
          <div class="mapping_info">
            <a title="의도 자세히 보기" class="intentA"></a> &#187; <a title="다음 태스크 자세히 보기" class="taskA"></a>
          </div>
          <p class="ipt_txt answer"></p>
          <div class="btn_box">
            <div>
              <button type="button" class="btn_icon_line text_edit" title="수정">수정</button>
              <button type="button" class="btn_icon_line delete" title="삭제">삭제</button>
              <button type="button" class="btn_icon_line audio_pause" title="일시정지">음성 일시정지</button>
              <button type="button" class="btn_icon_line audio_play" title="음성생성">음성생성</button>
              <button type="button" class="btn_icon_line audio_stop" title="처음으로">음성 처음으로</button>
            </div>
            <div>
              <button type="button" class="btn_icon_line save_temporarily" title="임시저장">임시저장</button>
              <button type="button" class="btn_icon_line cancel" title="취소">취소</button>
            </div>
          </div>
        </div>
      </template>
      <%-- //vb_answer template --%>

      <div id="basic_utter_info" class="vb_answer">
        <p class="ipt_txt answer"></p>
        <%--        <div class="btn_box">--%>
        <%--          <div>--%>
        <%--            <button type="button" class="btn_icon_line text_edit" title="수정">수정</button>--%>
        <%--            <button type="button" class="btn_icon_line audio_pause" title="일시정지">음성 일시정지</button>--%>
        <%--            <button type="button" class="btn_icon_line audio_play" title="음성생성">음성생성</button>--%>
        <%--            <button type="button" class="btn_icon_line audio_stop" title="처음으로">음성 처음으로</button>--%>
        <%--          </div>--%>
        <%--          <div>--%>
        <%--            <button type="button" class="btn_icon_line save_temporarily" title="임시저장">임시저장</button>--%>
        <%--            <button type="button" class="btn_icon_line cancel" title="취소">취소</button>--%>
        <%--          </div>--%>
        <%--        </div>--%>
      </div>

      <div class="float_box">
        <%-- left_side --%>
        <div id="intent_list" class="left_side" style="width: 100%;">
          <%-- cont_tit --%>
          <div class="tab_cont_tit">
            <p>intent 조건 답변</p>
            <button type="button" class="btn_secondary small add_answer" onclick="addIntent('INTENT')" disabled>조건 추가</button>
          </div>
          <%-- //cont_tit --%>
          <%-- cont --%>
          <div class="cont scroll">
            <div class="vb_answer no_list">
              등록된 조건답변이 없습니다.
            </div>
          </div>
          <%-- //cont --%>
        </div>
        <%-- //left_side --%>

        <%-- right_side --%>
        <div id="entity_list" class="right_side" style="display: none;">
          <%-- cont_tit --%>
          <div class="tab_cont_tit">
            <p>entity 조건 답변</p>
            <button type="button" class="btn_secondary small add_answer" onclick="addIntent('ENTITY')" disabled>조건 추가</button>
          </div>
          <%-- //cont_tit --%>
          <%-- cont --%>
          <div class="cont scroll">
            <div class="vb_answer no_list">
              등록된 조건답변이 없습니다.
            </div>
          </div>
          <%-- //cont --%>
        </div>
        <%-- //right_side --%>
      </div>
    </div>
    <%-- //tab_cont 챗봇이 할 말 --%>
    <%-- tab_cont 설정 --%>
    <div class="vb_tab_cont">
      <div class="tab_cont_tit">
        <p>태스크 반복 설정</p>
      </div>
      <div class="vb_answer_set">
        <div class="tit">답변 최대 반복
          <select name="repeat_answer" id="repeat_answer" class="select" onchange="changeMaxTurn(this.value)" disabled>
            <option value="-1">선택</option>
            <option value="1">1</option>
            <option value="2">2</option>
            <option value="3">3</option>
            <option value="4">4</option>
            <option value="5">5</option>
            <option value="6">6</option>
            <option value="7">7</option>
            <option value="8">8</option>
            <option value="9">9</option>
            <option value="0">반복 없음</option>
          </select>
          회
        </div>
        <div id="scenarioOverTask" class="tit">답변 최대 반복 초과 시 실행될 태스크
          <input type="text" name="scenario_over_task" id="scenario_over_task" class="select" onchange="changeTaskOverMax()" readOnly disabled>
        </div>
      </div>

      <div class="tab_cont_tit">
        <p>사용자 설정</p>
      </div>
      <div class="vb_answer_set">
        <div class="tit inline">
          사용자 답변
          <input type="text" name="answer_utter" id="answer_utter" class="select text_ellipsis" onchange="changeInputType()" multiple readonly disabled>
          회차
        </div>
        <div class="tit inline">
          다이얼
          <input type="text" name="answer_dial" id="answer_dial" class="select text_ellipsis" onchange="changeInputType()" multiple readonly disabled>
          회차
        </div>
      </div>

      <div class="tab_cont_tit">
        <p>답변 구간 설정</p>
      </div>
      <div class="vb_answer_set">
        <div class="vb_answer answer_ignore">
          <div class="tit">사용자의 답변을 인식 할 구간</div>
          <p class="ipt_txt">
            <%--            <em class="highlight">먼저, 담당 강사님의 수업 방식과 시간 준수에 대한 질문입니다.</em> 강사님의 수업 방식은 괜찮으셨나요?--%>
          </p>
        </div>
        <div class="vb_answer answer_repeat">
          <div class="tit">반복해서 말 할 구간</div>
          <p class="ipt_txt">
            <%--            먼저, 담당 강사님의 수업 방식과 시간 준수에 대한 질문입니다. <em class="highlight">강사님의 수업 방식은 괜찮으셨나요?</em>--%>
          </p>
        </div>
      </div>
    </div>
    <%-- //tab_cont 설정 --%>
  </div>
  <%-- //vb_tab --%>
</div>

<input type="hidden" name="uploadExcelV2Success" value="<spring:message code="MESSAGE.EXCEL.SUCCESS" javaScriptEscape="true"/>">
<input type="hidden" name="uploadExcelV2Error" value="<spring:message code="MESSAGE.EXCEL.ERROR" javaScriptEscape="true"/>">
</body>
</html>
