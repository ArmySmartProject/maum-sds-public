<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/reset.css">
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/font.css">
  <link rel="stylesheet" type="text/css"
        href="${pageContext.request.contextPath}/css/simplebot-chatbot.css">
  <link rel="stylesheet" type="text/css"
        href="${pageContext.request.contextPath}/css/simplebot.css?version=2.2">
  <link rel="stylesheet" type="text/css"
        href="${pageContext.request.contextPath}/css/jsplumbtoolkit-defaults.css">

  <script src="<c:url value='/js/jquery-3.1.0.min.js'/>"></script>
  <script src="<c:url value='/js/jsplumbtoolkit.js'/>"></script>
  <script src="<c:url value='/js/voicebotBuilderFlowchart.js'/>"></script>
  <script type="text/x-jtk-templates" src="<c:url value='/templates/nodeTemplate.html'/>"></script>
  <script src="<c:url value='/js/jquery.js'/>"></script>
  <script src="<c:url value='/js/jquery.form.js'/>"></script>
  <script src="<c:url value='/js/socket.io-1.4.0.js'/>"></script>
  <script src="https://static.maum.ai/common/resources/js/mui_alert.js"></script>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/js/jquery-ui-1.12.0/jquery-ui.min.css"/>
      <script src="${pageContext.request.contextPath}/js/jquery-ui-1.12.0/jquery-ui.min.js"></script>
  <script src="${pageContext.request.contextPath}/js/jquery.fileDownload.js"></script>
  <title>Simple Bot</title>
</head>
<body>
<div id="wrap">
  <div id="header">
    <h1>음성봇 빌더</h1>
  </div>

  <div id="container" class="simplebot">
    <div class="content">
      <%--      [D] 200423 수정 AMR .content -> #scenario_content --%>
      <div class="multipleBoxType">
        <%--        시나리오 목록--%>
        <div class="lotBox">
          <div class="tit">
            <h3><spring:message code="TITLE.SIMPLEBOT.LIST" text="시나리오 목록"/></h3>
            <div class="fr">
              <a href="#scenario_add" class="btn_primary btn_lyr_open"><spring:message code="LABEL.ADD" text="추가"/></a>
            </div>
          </div>
          <div class="cont">
            <div class="iptBox">
              <input type="text" class="ipt_txt search" autocomplete="off">
              <button type="button" class="btn_search"><span class="text_hide"><spring:message code="LABEL.SEARCHING" text="검색하기"/></span></button>
            </div>
            <div class="tbl_customTd scroll scenario_list">
              <table class="tbl_line_lst" summary="번호/시나리오명/언어/리스트 삭제로 구성됨">
                <caption class="hide"><spring:message code="TITLE.SIMPLEBOT.LIST" text="시나리오 목록"/></caption>
                <colgroup>
                  <col width="40"><col><col><col>
                </colgroup>
                <thead>
                <tr>
                  <th scope="col"><spring:message code="LABEL.NUMBER" text="번호"/></th>
                  <th scope="col"><spring:message code="LABEL.SIMPLEBOT.NAME" text="시나리오명"/></th>
                  <th scope="col"><spring:message code="LABEL.LANGUAGE" text="언어"/></th>
                  <th scope="col"><span class="text_hide"><spring:message code="LABEL.DELETE" text="삭제"/></span></th>
                  <th scope="col" style="display: none">host</th>
                  <th scope="col" style="display: none">simplebotId</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                  <td scope="row">1</td>
                  <td><span class="text_ellipsis">마인즈에듀</span></td>
                  <td><spring:message code="LABEL.LANG.KOR" text="한국어"/></td>
                  <td><a href="#delete_alert" class="tbl_btn delete btn_lyr_open text_hide"><spring:message code="LABEL.DELETE" text="삭제"/></a></td>
                </tr>
                <tr>
                  <td scope="row">2</td>
                  <td><span class="text_ellipsis">TOMS</span></td>
                  <td><spring:message code="LABEL.LANG.KOR" text="한국어"/></td>
                  <td><a href="#delete_alert" class="tbl_btn delete btn_lyr_open text_hide"><spring:message code="LABEL.DELETE" text="삭제"/></a></td>
                </tr>
                <tr>
                  <td scope="row">3</td>
                  <td><span class="text_ellipsis">sully</span></td>
                  <td><spring:message code="LABEL.LANG.ENG" text="영어"/></td>
                  <td><a href="#delete_alert" class="tbl_btn delete btn_lyr_open text_hide"><spring:message code="LABEL.DELETE" text="삭제"/></a></td>
                </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
        <%--        //시나리오 목록--%>

        <%--        시나리오--%>
        <div class="lotBox">
          <div class="tit">
            <h3><spring:message code="TITLE" text="시나리오"/></h3>
            <div class="fr scenario_btns">
              <a href="#table_replace_list" class="btn_primary btn_lyr_open btn_replace_dic">치환사전</a>
            <%--              [D] AMR 201029 한국어/영어 선택은 시나리오 추가 팝업에서 기능해야 합니다. (지워야함)--%>
              <%--<div class="twins" id="twins">--%>
                <%--<button type="button" class="btn_primary active" value=1><spring:message code="LABEL.LANG.KOR" text="한글"/></button>--%>
                <%--<button type="button" class="btn_primary" value=2><spring:message code="LABEL.LANG.ENG" text="영문"/></button>--%>
              <%--</div>--%>
  <%--              //--%>
              <a href="#scenario_upload_v2" class="btn_secondary excel_upload btn_lyr_open"><spring:message code="LABEL.EXCEL.UPLOAD" text="엑셀 업로드"/></a>
              <button type="button" class="btn_secondary" id="scenarioDownload">엑셀 다운로드</button>
              <button type="button" class="btn_secondary excel_download"><spring:message code="LABEL.EXCEL.DOWNLOAD" text="엑셀 샘플 다운로드"/></button>

              <form action="simpleBot/scenarioDownload"
                    target="downFrame" id="downloadScenario" name="downloadScenario" method="post" style="display: inline-block;">
                <input type="hidden" name="scenarioJson" id="scenarioJson" value="">
              </form>
              <form action="simpleBot/sampleDownload"
                    target="downFrame" name="downloadForm" method="post" style="display: inline-block;">
              </form>

              <em class="saved_time">Never updated</em>
              <a href="#scenario_save" id="scenario_save" class="btn_primary btn_save"><spring:message code="LABEL.SAVE" text="저장"/></a>
            </div>
          </div>
          <div class="cont scenario_view">
            <div class="scenario">
              <%--              시나리오 라이브러리가 들어갈 자리<br>--%>
            </div>

            <div class="scenario_edit">
              <div class="edit_title">
                <h4><span>전화상담원 만족도 설문</span></h4>
                <div style="overflow: hidden; width: 0; height: 0">
                  <audio controls preload="none" id="audio" >
                    <source src="none.ogg" type="audio/ogg">
                    <source src="none.mp3" type="audio/mpeg">
                    Your browser does not support the audio element.
                  </audio>
                </div>
                <button type="button" class="btn_primary btn_save"
                        style="display: none;">저장
                </button>
              </div>

              <div class="tab_menu">
                <button type="button" class="on">챗봇이 할 말</button>
                <button type="button">설정</button>
              </div>

              <div class="tab_cont scroll">
                <li class="on">
                  <div class="cont_row">
                    <dl>
                      <dt><spring:message code="LABEL.CHATBOT.BASIC" text="기본 발화"/></dt>
                      <dd>
<%--                    [D] AMR 텍스트 수정 박스 (1845 line)
                          .user_content .description 이 부모
                          p.textarea (읽기모드) / textarea (쓰기모드)
                          p.textarea 클릭 시 textarea 생성
                          생성된 textarea에서 focusout 시 p.textarea 생성 --%>
                        <div class="user_content">
                          <div class="description">
                            <p class="textarea" id='first_say' title="편집하려면 클릭하세요.">
                              하나생명 고객 센터에서 확인 전화드렸습니다. <span>&lt;가입월&gt;</span>월 <span>&lt;가입일&gt;</span>일 하나은행 <span>&lt;가입지점&gt;</span> 지점을 통해 ELS의 정석 변액보험을 가입해주셨는데요. 가입하신 상품에 대한 계약확인 안내 전화입니다. 잠시 통화 가능하신가요?
                            </p>
                          </div>
                        </div>
                      </dd>
                    </dl>
                    <dl class="chatbot_answer">
<%--                      intent 조건발화--%>
                      <dt>
                        <spring:message code="LABEL.CHATBOT.CONDITION" text="조건 발화"/>
                        <%-- [D] AMR select는 disabled 이어야 선택할 수 없음--%>
                        <select name="" id="condition_type_selectbox" class="select">
                          <option value="" selected>intent</option>
<%--                          <option value="">entity</option>--%>
                        </select>
<%--                        <div class="btn_box fr">--%>
<%--                          <button type="button" id="add_entity" class="btn_secondary">발화 추가</button>--%>
<%--                        </div>--%>
                      </dt>
                      <dd class="scroll intent">
                        <%--<div class="user_content">--%>
                          <%--<em class="sub_tit">YES</em>--%>
                          <%--<a href="#chat_speech_delete" class="btn_icon delete btn_lyr_open"></a>--%>
                          <%--<div class="description">--%>
                            <%--<p class="textarea" id="yes_say">Yes</p>--%>
                          <%--</div>--%>
                        <%--</div>--%>
                        <%--<div class="user_content">--%>
                          <%--<em class="sub_tit">NO</em>--%>
                          <%--<a href="#chat_speech_delete" class="btn_icon delete btn_lyr_open"></a>--%>
                          <%--<div class="description">--%>
                            <%--<p class="textarea" id="no_say">No</p>--%>
                          <%--</div>--%>
                        <%--</div>--%>
                        <%--<div class="user_content">--%>
                          <%--<em class="sub_tit">UNKNOWN</em>--%>
                          <%--<a href="#chat_speech_delete" class="btn_icon delete btn_lyr_open"></a>--%>
                          <%--<div class="description">--%>
                            <%--<p class="textarea" id="unknown_say">Unknown</p>--%>
                          <%--</div>--%>
                        <%--</div>--%>
                        <%--<div class="user_content">--%>
                          <%--<em class="sub_tit">REPEAT</em>--%>
                          <%--<a href="#chat_speech_delete" class="btn_icon delete btn_lyr_open"></a>--%>
                          <%--<div class="description">--%>
                            <%--<p class="textarea" id="repeat_say">Repeat</p>--%>
                          <%--</div>--%>
                        <%--</div>--%>
                      </dd>
                      <dd class="scroll entity" style="display: block;">
                        <template id="temp_entity">
                          <div class="user_content">
                            <div class="sub_tit">
                              <span>조건</span>
                            </div>
                            <div class="description">
                              <p class="textarea"></p>
                            </div>
                            <a href="#chat_speech_delete" class="btn_icon delete btn_lyr_open"></a>
                          </div>
                        </template>
                        <div class="user_content">
                          <div class="sub_tit">
                            <span>주민등록증</span>
                          </div>
                          <div class="description">
                            <p class="textarea"></p>
                          </div>
                          <a href="#chat_speech_delete" class="btn_icon delete btn_lyr_open"></a>
                        </div>
                        <div class="user_content">
                          <div class="sub_tit">
                            <span>여권</span>
                          </div>
                          <div class="description">
                            <p class="textarea">문장</p>
                          </div>
                          <a href="#chat_speech_delete" class="btn_icon delete btn_lyr_open"></a>
                        </div>
                        <div class="user_content">
                          <div class="sub_tit">
                            <span>운전면허증</span>
                          </div>
                          <div class="description">
                            <p class="textarea">운전면허증 운전면허증 운전면허증 운전면허증 운전면허증 운전면허증 운전면허증</p>
                          </div>
                          <a href="#chat_speech_delete" class="btn_icon delete btn_lyr_open"></a>
                        </div>
                      </dd>

<%--                      entity 조건발화--%>
<%--                      <dt>--%>
<%--                        <spring:message code="LABEL.CHATBOT.CONDITION" text="조건 발화"/>--%>
<%--                        <select name="" id="" class="select" disabled>--%>
<%--                          <option value="" selected>intent</option>--%>
<%--                          <option value="">entity</option>--%>
<%--                        </select>--%>
<%--                      </dt>--%>
<%--                      <dd class="scroll">--%>
<%--                        <div class="user_content">--%>
<%--                          <em class="sub_tit">YES</em>--%>
<%--                          <div class="description">--%>
<%--                            <p class="textarea">문장</p>--%>
<%--                          </div>--%>
<%--                        </div>--%>
<%--                        <div class="user_content">--%>
<%--                          <em class="sub_tit">NO</em>--%>
<%--                          <div class="description">--%>
<%--                            <p class="textarea">문장</p>--%>
<%--                          </div>--%>
<%--                        </div>--%>
<%--                        <div class="user_content">--%>
<%--                          <em class="sub_tit">UNKNOWN</em>--%>
<%--                          <div class="description">--%>
<%--                            <p class="textarea">문장</p>--%>
<%--                          </div>--%>
<%--                        </div>--%>
<%--                        <div class="user_content">--%>
<%--                          <em class="sub_tit">REPEAT</em>--%>
<%--                          <div class="description">--%>
<%--                            <p class="textarea">문장</p>--%>
<%--                          </div>--%>
<%--                        </div>--%>
<%--                      </dd>--%>

<%--                      entity 조건발화--%>
                    </dl>
                  </div>
                </li>
                <li>
                  <div class="cont_row chatbot_user_setting">
                    <dl>
                      <dt>사용자 설정</dt>
                      <dd>
                        <input type="checkbox" class="checkbox checkSetting" id="check01">
                        <label for="check01">사용자 발화</label>
                        <input type="checkbox" class="checkbox checkSetting" id="check02" checked>
                        <label for="check02">다이얼
                          <svg width="20" height="20" viewBox="0 0 40 50" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <rect x="0.5" y="0.5" width="39" height="49" rx="2.5" fill="#74798C" stroke="#353535"/>
                            <rect x="6.5" y="7.5" width="7" height="7" rx="1.5" fill="#DCE6F2" stroke="#464B60"/>
                            <rect x="16.5" y="7.5" width="7" height="7" rx="1.5" fill="#DCE6F2" stroke="#464B60"/>
                            <rect x="26.5" y="7.5" width="7" height="7" rx="1.5" fill="#DCE6F2" stroke="#464B60"/>
                            <rect x="6.5" y="17.5" width="7" height="7" rx="1.5" fill="#DCE6F2" stroke="#464B60"/>
                            <rect x="16.5" y="17.5" width="7" height="7" rx="1.5" fill="#DCE6F2" stroke="#464B60"/>
                            <rect x="26.5" y="17.5" width="7" height="7" rx="1.5" fill="#DCE6F2" stroke="#464B60"/>
                            <rect x="6.5" y="27.5" width="7" height="7" rx="1.5" fill="#DCE6F2" stroke="#464B60"/>
                            <rect x="16.5" y="27.5" width="7" height="7" rx="1.5" fill="#DCE6F2" stroke="#464B60"/>
                            <rect x="26.5" y="27.5" width="7" height="7" rx="1.5" fill="#DCE6F2" stroke="#464B60"/>
                            <rect x="16.5" y="37.5" width="7" height="7" rx="1.5" fill="#DCE6F2" stroke="#464B60"/>
                          </svg>
                        </label>
                      </dd>
                      <dt>태스크 반복 설정</dt>
                      <dd>
                        <p>발화 최대 반복 <input type="number" class="ipt_txt" id="max_turn_cnt"> 회</p>
                        <p>발화 최대 반복 초과 시 실행될 태스크 <input type="text" class="ipt_txt" id="task_over_max"></p>
                      </dd>
                    </dl>

                    <dl>
                      <dt>발화 무시구간/반복구간 설정</dt>
                      <dd>
                        <em class="sub_tit">사용자 답변 청취 구간</em>
                        <p class="textarea ignore"><em>먼저, 담당 강사님의 수업 방식과 시간 준수에 대한 질문입니다.</em> 강사님의 수업 방식은 괜찮으셨나요?</p>

                        <em class="sub_tit">사용자에게 반복할 구간</em>
                        <p class="textarea repeat">먼저, 담당 강사님의 수업 방식과 시간 준수에 대한 질문입니다. <em>강사님의 수업 방식은 괜찮으셨나요?</em></p>
                      </dd>
                    </dl>
                  </div>
                </li>
              </div>
            </div>
          </div>
        </div>
        <%--        //시나리오--%>

        <%--        시나리오 테스트--%>
        <div class="lotBox">
          <div class="tit">
<%--            <h3><spring:message code="TITLE.SIMPLEBOT.TEST" text="시나리오 테스트"/><div class="help">?<i>고객 데이터를 추가하면 테스트 시 자동으로 적용됩니다.</i></div></h3>--%>
          </div>

          <div class="cont scenario_test">
            <div class="test_data">
              <div class="tbl_commands">
                <div class="fl">
                  <button type="button" class="btn_secondary btn_remove_row" disabled>선택 삭제</button>
                </div>
                <div class="fr">
                  <button type="button" id="btn_add_cust_row" class="btn_secondary">테스트 데이터 추가</button>
                </div>
              </div>
              <div class="tbl_wrap scroll">
                <table id="tblChatbotTestData" class="tbl_box_lst">
                  <colgroup>
                    <col style="width: 30px;"><col style="width: 90px;"><col>
                  </colgroup>
                  <tbody>
                  <%--
                  <tr>
                    <td>
                      <input type="checkbox" id="check001" class="checkbox">
                      <label for="check001"></label>
                    </td>
                    <td>
                      <input type="text" class="ipt_txt" placeholder="이름">
                    </td>
                    <td>
                      <input type="text" class="ipt_txt" placeholder="홍길동">
                    </td>
                  </tr>
                  --%>
                  </tbody>
                </table>
                <template id="tempChatbotTestData">
                  <table>
                    <tbody>
                      <tr>
                        <td>
                          <input type="checkbox" id="check0001" class="checkbox">
                          <label for="check0001"></label>
                        </td>
                        <td>
                          <input type="text" class="ipt_txt" placeholder="고객데이터">
                        </td>
                        <td>
                          <input type="text" class="ipt_txt" placeholder="고객데이터">
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </template>
              </div>
            </div>
            <div class="tab_menu">
              <button type="button" class="on" style="width: 50%;"><spring:message code="LABEL.CHATTEST" text="챗봇"/></button>
              <button type="button" style="width: 50%;"><spring:message code="LABEL.VOICETEST" text="음성봇"/></button>
            </div>

            <ul class="tab_cont">
              <li class="on">
                <div id="chatbot">
                  <div class="chatUI_wrap">
                    <div class="chatUI_mid btmUi scroll">
                      <ul class="lst_talk"></ul>
                    </div>
                    <!-- .chatUI_btm -->
                    <div class="chatUI_btm">
<%--                      <form method="post" action="" id="formChat" name="formChat">--%>
                        <textarea class="textArea" rows="1" placeholder="메세지를 입력해 주세요"></textarea>
                        <input name="btn_chat" id="btn_chat" class="btn_chat" title="전송" value="전송">
<%--                      </form>--%>
                    </div>
                    <!-- //#chatUI_btm -->
                  </div>
                  <button type="button" class="chatbot_refresh"><em class="text_hide"><spring:message code='LABEL.REFRESH' text='새로고침'/></em></button>
                  <div class="blind">
                    <div>
                      <p><spring:message code='MESSAGE.CHATBOT.START' text='START를 눌러 대화를<br>시작해주세요.'/></p>
                      <button type="button" id="chat_test_start" class="btn_primary">START</button>
                    </div>
                  </div>
                </div>
              </li>

              <li>
                <div class="voicebot_area">
                  <div class="make_call">
                    <span class="waiting">
                      <spring:message code='LABEL.VOICEBOT.WAITER' text='현재 통화 대기자'/> : <em>0</em> <spring:message code='LABEL.VOICEBOT.PERSON' text='명'/>
                    </span>
                    <div class="required_value iptBox">
                      <input type="number" style="display: inline-block; width:51%;" name="tel" class="ipt_txt" placeholder="<spring:message code='PLACEHOLDER.PHONE' text='전화번호 입력'/>">
<%--                      <input type="text" name="name" class="ipt_txt" placeholder="<spring:message code='PLACEHOLDER.NAME' text='이름 입력'/>">--%>
                      <%--               [D] 전화걸기와 대기중은 .btn_primary 적용,
                                              통화중은 .btn_secondary 적용입니다 --%>
                      <button type="button" style="width: 35%; margin:20px" id="voice_test_start" class="btn_primary"><spring:message code="LABEL.CALL" text="전화 걸기"/></button>
                    </div>
                    <%--                  <button type="submit" class="btn_primary gradient"><spring:message code="LABEL.WAITING" text="대기중"/></button>--%>
                    <%--                  <button type="submit" class="btn_secondary"><spring:message code="LABEL.CALLING" text="통화중"/></button></button>--%>
                  </div>

<%--              수정 voicebot--%>
<%--              [D] AMR 201029 채팅 .chatUI_wrap을 공통으로 사용 --%>
                  <div id="voicebot" class="scroll">
                    <div class="chatUI_wrap">
                      <div class="chatUI_mid midUi scroll">
                        <ul class="lst_talk">
                          <!-- bot UI -->
                          <li class="bot">
                            <!-- [D] 기본메세지 -->
                            <div class="bot_msg">
                              <em class="txt">안녕하세요. 마음AI 챗봇 설리입니다. 무엇을 도와드릴까요?</em>
                              <div class="date">2019.08.14 12:00</div>
                            </div>
                          </li>
                        </ul>
                      </div>
                    </div>

                    <div class="blind voicebot_start">
                      <div>
                        <p><spring:message code="MESSAGE.VOICEBOT.START" text="상단 입력창에서<br>정보를 입력하신 후<br>전화 걸기를 눌러주세요."/></p>
                      </div>
                    </div>
                  </div>
<%--              //수정 voicebot--%>
                </div>
<%--                //voicebot_area--%>
              </li>
            </ul>
<%--            //tab_cont--%>
          </div>
<%--          //cont--%>
        </div>
        <%--        //시나리오 테스트--%>
      </div>
    </div>
<%--    //content--%>
  </div>
<%--  //container--%>
</div>

<!-- 수정 200401 AMR  -->
<%-- lyr popup --%>

<%--시나리오 목록 추가--%>
<div id="scenario_add" class="lyrBox">
  <div class="lyr_top">
    <h3><spring:message code="TITLE.SIMPLEBOT.ADD" text="시나리오 추가"/></h3>
    <button class="btn_lyr_close"><span class="text_hide"><spring:message code="LABEL.CLOSE" text="닫기"/></span></button>
  </div>
  <div class="lyr_mid">
    <dl class="dl_inline">
      <dt><label for="scenario_name"><spring:message code="LABEL.SIMPLEBOT.NAME" text="시나리오명"/></label></dt>
      <dd>
        <div class="iptBox">
          <input type="text" id="scenario_name" name="" class="ipt_txt">
        </div>
      </dd>
    </dl>
    <dl class="dl_inline">
      <dt><label for="scenario_lang"><spring:message code="LABEL.LANGUAGE" text="언어"/></label></dt>
      <dd>
        <div class="iptBox">
          <select name="scenario_lang" id="scenario_lang" class="select">
            <option value="kor"><spring:message code="LABEL.LANG.KOR" text="한국어"/></option>
<%--            <option value="eng"><spring:message code="LABEL.LANG.ENG" text="영어"/></option>--%>
          </select>
        </div>
      </dd>
    </dl>
  </div>
  <div class="lyr_btm">
    <div class="btnBox sz_small">
      <button type="button" id="btn_add_scenario" class="btn_primary btn_submit btn_lyr_close"><spring:message code="MESSAGE.EXCEL.CONFIRM" text="확인"/></button>
      <button type="button" class="btn_primary btn_lyr_close"><spring:message code="LABEL.CANCEL" text="취소"/></button>
    </div>
  </div>
</div>
<%--//시나리오 목록 추가--%>
<%--시나리오 삭제--%>
<div id="delete_alert" class="lyrBox">
  <div class="lyr_top">
    <h3><spring:message code="TITLE.SIMPLEBOT.DELETE" text="시나리오 삭제"/></h3>
    <button class="btn_lyr_close"><span class="text_hide"><spring:message code="LABEL.CLOSE" text="닫기"/></span></button>
  </div>
  <div class="lyr_mid">
    <p class="infoTxt"><spring:message code="MESSAGE.SIMPLEBOT.DELETE.ENG" text="Delete the"/><em>선택한 시나리오명</em><spring:message code="MESSAGE.SIMPLEBOT.DELETE.KOR" text=" 을(를) 삭제합니다."/></p>
  </div>
  <div class="lyr_btm">
    <div class="btnBox sz_small">
      <button type="button" id="btn_delete_scenario" class="btn_primary btn_submit btn_lyr_close"><spring:message code="MESSAGE.EXCEL.CONFIRM" text="확인"/></button>
      <button type="button" class="btn_primary btn_lyr_close"><spring:message code="LABEL.CANCEL" text="취소"/></button>
    </div>
  </div>
</div>
<%--//시나리오 삭제--%>

<!-- 시나리오 파일 업로드 v2 -->
<div id="scenario_upload_v2" class="lyrBox scenario_upload">
  <div class="lyr_top">
    <h3><spring:message code="LABEL.EXCEL.UPLOAD" text="엑셀 파일 업로드"/></h3>
    <button class="btn_lyr_close"><span class="text_hide"><spring:message code="LABEL.CLOSE" text="닫기"/></span></button>
  </div>
  <div class="lyr_mid">
    <dl class="dl_inline">
      <dt>
        <div class="iptBox">
          <!-- [D] input[file] value = input[text] value -->
          <input type="text" name="excel_file_name" value="선택된 파일 없음" class="ipt_txt" disabled>
        </div>
      </dt>
      <dd>
        <label for="excel_file_v2"><spring:message code="MESSAGE.EXCEL.BROWSE" text="찾아보기.."/></label>
        <form id="excelUploadScenarioForm" name="excelUploadScenarioForm" method="post" enctype="multipart/form-data"
              action="simpleBot/uploadScenarioV2">
          <input type="file" name="excel_file_v2" id="excel_file_v2"
                 accept=".xls, .xlsx" style="display: none;">
          <input type="text" name="simplebotId" style="display: none;">
        </form>
      </dd>
    </dl>
    <p class="info_text"><spring:message code="MESSAGE.EXCEL.GUIDE" text="* 파일 업로드 시 시나리오가 덮어쓰기 됩니다."/></p>

    <!-- [D] input[file] value = input[text] value -->
<%--    <input type="text" name="excel_file_name" value="선택된 파일 없음" class="ipt_txt" disabled>--%>
<%--    <label for="excel_file"><spring:message code="MESSAGE.EXCEL.BROWSE" text="찾아보기.."/></label>--%>
<%--    <form id="excelUploadForm" name="excelUploadForm" method="post" enctype="multipart/form-data"--%>
<%--          action="simpleBot/upload">--%>
<%--      <input type="file" name="excel_file" id="excel_file"--%>
<%--             accept=".xls, .xlsx" style="display: none;">--%>
<%--      <input type="text" name="userId" style="display: none;">--%>
<%--      <input type="text" name="lang" style="display: none;">--%>
<%--    </form>--%>
<%--    <p class="info_text"><spring:message code="MESSAGE.EXCEL.GUIDE" text="* 파일 업로드 시 시나리오가 덮어 쓰기 됩니다."/></p>--%>
  </div>
  <div class="lyr_btm">
    <div class="btnBox sz_small">
      <button type="button" class="btn_primary btn_submit btn_lyr_close"><spring:message code="MESSAGE.EXCEL.CONFIRM" text="확인"/></button>
      <button type="button" class="btn_primary btn_lyr_close"><spring:message code="LABEL.CANCEL" text="취소"/></button>
    </div>
  </div>
</div>
<!-- //시나리오 파일 업로드 테스트-->
<%--발화 삭제--%>
<div id="chat_speech_delete" class="lyrBox">
  <div class="lyr_top">
    <h3>발화 삭제</h3>
    <button class="btn_lyr_close"><span class="text_hide"><spring:message code="LABEL.CLOSE" text="닫기"/></span></button>
  </div>
  <div class="lyr_mid">
    <p class="infoTxt"><spring:message code="MESSAGE.SIMPLEBOT.DELETE.ENG" text="Delete the"/><em>선택한 발화명</em><spring:message code="MESSAGE.SIMPLEBOT.DELETE.KOR" text=" 을(를) 삭제합니다."/></p>
  </div>
  <div class="lyr_btm">
    <div class="btnBox sz_small">
      <button type="button" id="btn_delete_intent" class="btn_primary btn_submit btn_lyr_close"><spring:message code="MESSAGE.EXCEL.CONFIRM" text="확인"/></button>
      <button type="button" class="btn_primary btn_lyr_close"><spring:message code="LABEL.CANCEL" text="취소"/></button>
    </div>
  </div>
</div>
<%--//발화 삭제--%>

<%--치환사전  --%>
<div id="table_replace_list" class="lyrBox">
<input type="hidden" id="currentPage" value="1">
<input type="hidden" id="orderCol" value="">
<input type="hidden" id="orderSort" value="">

  <div class="lyr_top">
    <h3>치환사전</h3>
    <button class="btn_lyr_close"><span class="text_hide"><spring:message code="LABEL.CLOSE" text="닫기"/></span></button>
  </div>
  <div class="lyr_mid">
    <div class="tit">
      <h4>치환사전 추가
        <div class="help">?<div id="replace_help" class="help_desc">치환사전이란 동의어 사전으로, 등록해 놓은 단어를 변경해 주는 역할을 합니다.
        비슷한 의미를 가진 단어를 교체해 학습량을 줄이거나, STT의 오류를 잡을 수 있습니다.</div></div>
      </h4>
    </div>
    <div class="tbl_wrap">
      <table class="tbl_basic">
        <colgroup>
          <col><col><col style="width: 80px;">
        </colgroup>
        <thead>
        <tr>
          <th>치환 전</th>
          <th>치환 후</th>
          <th class="text_hide">추가버튼</th>
        </tr>
        </thead>

        <tbody>
        <tr>
          <td><input type="text" class="ipt_txt" id="addBeforeTxt"></td>
          <td><input type="text" class="ipt_txt" id="addAfterTxt"></td>
          <td><button type="button" class="btn_line_primary" onclick="addReplaceDict();">추가</button></td>
        </tr>
        </tbody>

      </table>
    </div>

    <div class="tit">
      <h4>치환사전 목록
        <div class="help">?
          <div id="replace_list_help" class="help_desc">엑셀로 업로드 할 경우 기존 목록이 엑셀로 덮어쓰기 됩니다. 엑셀 업로드 시 꼭 아래와 같이 작성해주세요.
            <div class="image">
              <img src="../images/help_img_replace.png" alt="엑셀 업로드 작성 예시">
            </div>
          </div>
        </div>
      </h4>
    </div>
    <div class="float_box">
      <%--          엑셀 다운로드 form--%>
      <form action="${pageContext.request.contextPath}/upload/replaceDictExcelDown"
            target="downFrame" id="downloadReplaceDict" name="downloadReplaceDict" method="get">
        <input type="hidden" name="orderCol" id="replaceDictCol" value="">
        <input type="hidden" name="orderSort" id="replaceDictSort" value="">
        <input type="hidden" name="host" id="replaceDict_host">
        <input type="hidden" name="lang" id="replaceDict_lang">
      </form>

      <form id="replaceDictUpload" name="replaceDictUpload" method="post" enctype="multipart/form-data"
              action="${pageContext.request.contextPath}/upload/insertReplaceDictExcel">
        <div class="fl btn_box">
              <input type="file" name="excelFile" id="excel_file_replace" accept=".xls, .xlsx" style="display: inline-block;">
              <input type="hidden" name="Host" id="replaceDictHost">
              <input type="hidden" name="Lang" id="replaceDictLang">
          <button id="btn_upload" type="button" class="btn_secondary" onclick="uploadExcel()">엑셀 저장</button>
          <button type="button" class="btn_secondary" onclick="excelDown()">엑셀 다운로드</button>
        </div>
      </form>

      <div class="fr">
        <input type="text" class="ipt_txt search" name="searchReplaceDict"  autocomplete="off" placeholder="치환 전/후 검색">
        <button type="button" class="btn_search" id="searchReplaceDict" onclick="searchReplaceDict();"></button>
      </div>
    </div>

    <div class="tbl_wrap">
      <table class="tbl_basic" id="replaceDictTable">
        <colgroupt>
          <col style="width: 50px;"><col><col><col style="width: 80px;"><col style="width: 80px;">
        </colgroupt>
        <thead>
        <tr>
          <th>No</th>
          <%--        [D] 정렬 화살표(sort) : 정렬 아이콘 클릭 시 오름차순 > 내림차순 > 기본상태 순으로 정렬됩니다.
            기본상태 : .sort
            오름차순 : .sort.up
            내림차순 : .sort.down
 --%>
          <th class="sort">치환 전</th>
          <th class="sort">치환 후</th>
          <th class="text_hide">수정</th>
          <th class="text_hide">삭제</th>
        </tr>
        </thead>
        <tbody>
        <tr>
          <td>1</td>
          <td><input type="text" class="ipt_txt"></td>
          <td><input type="text" class="ipt_txt"></td>
          <td><button type="button" class="btn_line_primary">수정</button></td>
          <td><button type="button" class="btn_line_warning">삭제</button></td>
        </tr>
        </tbody>
      </table>

      <%-- pagination --%>
      <div class="pagination">
        <button type="button" class="first">
          <span>«</span>
        </button>
        <button type="button" class="prev">
          <span>‹</span>
        </button>
        <div class="pages">
          <span class="page active">1</span>
          <span class="page">2</span>
          <span class="page">3</span>
        </div>
        <button type="button" class="next">
          <span>›</span>
        </button>
        <button type="button" class="last">
          <span>»</span>
        </button>
      </div>
      <%-- //pagination --%>
    </div>

  </div>
  <div class="lyr_btm" style="display:none;">
    <div class="btnBox sz_small">
      <button type="button" class="btn_primary btn_submit btn_lyr_close"><spring:message code="MESSAGE.EXCEL.CONFIRM" text="확인"/></button>
      <button type="button" class="btn_primary btn_lyr_close"><spring:message code="LABEL.CANCEL" text="취소"/></button>
    </div>
  </div>
</div>
<%--//치환사전--%>
<script>

  var langEnum = {'한국어': 1, '영어': 2, '일본어': 3, '중국어': 4};

  var host;
  var hostName;
  var lang;
  var userId;
  var companyId;
  var simplebotId;
  var scenario;

  var testerContractNo;
  var testerTelNo;
  var checkWaitingNo;

  var socket = null;
  var isSocketConnected = false;

  var env = '${env}';
  var m2uUrl = '${m2uUrl}';
  var m2uToken;
  let m2uDeviceId;
  let m2uChatbot = 'SIMPLEBOT';
  var sdsStartChat = '처음으로';
  var changeNodeData = null;
  var ignoreIdxArr = [];
  var repeatIdxArr = [];

  var oldKey = "";

  // uuid 생성
  function generateUUID(pattern){
    if(!pattern){
      pattern = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx';
    }
    return pattern.replace(/[xy]/g, function(c) {
      const r = (Math.random() * 16) | 0, v = c === 'x' ? r : (r & 3) | 8
      return v.toString(16);
    })
  }

  $(document).ready(function () {
    $('#table_replace_list th.sort').on('click', function(){
      var th = $(this);
      var ths = $('#table_replace_list th.sort');
      var text = th.text();
      if(text == "치환 전"){
        $("#orderCol").val("Before");
      }else if(text == "치환 후"){
        $("#orderCol").val("After");
      }

      //정렬순서 : 기본 > 오름차순 > 내림차순
      if ( th.hasClass('up') ) {
        ths.removeClass('up down');
        th.removeClass('up');
        th.addClass('down');
        $("#orderSort").val("DESC");
        replaceDictList(host, $("#currentPage").val());
      } else if ( th.hasClass('down') ) {
        ths.removeClass('up down');
        th.removeClass('down');
        $("#orderSort").val("");
        replaceDictList(host, $("#currentPage").val());
      } else {
        ths.removeClass('up down');
        th.addClass('up');
        $("#orderSort").val("ASC");
        replaceDictList(host, $("#currentPage").val());
      }
    });

    // AMR 돋보기 아이콘이 있는 검색 영역에서 엔터 시 검색버튼 눌림
    $('.ipt_txt.search').on('keyup', function(){
        if (event.keyCode == '13') {
            $(this).next('#searchReplaceDict').trigger('click');
        }
    });

    window.addEventListener("message", getSimpleBotList, false);
    // window.addEventListener("message", initScenarionLang, false);
    // 개발 모드
    if (env != 'prod') {
      // sample data
      getSimpleBotList({data: {userId: 'admin', companyId: '001'}});
    }

    var isInIFrame = (window.location != window.parent.location);
    if (isInIFrame === true) {
      $("#header").hide();
    }

    // toggleLanguage  kor < -- > eng
    $('div.twins .btn_primary').on('click', setScenarioLang);

    //Layer popup open
    var popupOpenEventListener = function() {
      $('.btn_lyr_open').on('click', function() {
        var winHeight = $(window).height() * 0.7,
            hrefId = $(this).attr('href');
            console.log('href : '+hrefId);
            if($(this).attr('href') == '#table_replace_list'){
              if(host == 'undefined'){
                $('#chatListUl>li.active').val()
              }
              replaceDictList(host, 1);
            }
		$(this).addClass('actPop');
        $('body').css('overflow', 'hidden');
//         $('body').find(hrefId).wrap('<div class="lyrWrap"></div>');
//         $('body').find(hrefId).before('<div class="lyr_bg"></div>');
        if ($(".lyrWrap .lyr_bg").length == 0) {
			$('body').find(hrefId).wrap('<div class="lyrWrap"></div>');
            $('body').find(hrefId).before('<div class="lyr_bg"></div>');
        }
        //대화 UI
        $('.lyrBox .lyr_mid').each(function() {
          $(this).css('max-height', Math.floor(winHeight) + 'px');

        });

        //Layer popup close
        $('.btn_lyr_close, .lyr_bg').on('click', function() {
          $('.actPop').removeClass('actPop');
          $('body').css('overflow', '');
          $('body').find(hrefId).unwrap('<div class="lyrWrap"></div>');
          $('.lyr_bg').remove();
        });
      });

      $('.delete_popup').on('click', function() {
        var selectedRow = $('.tbl_line_lst .active').get(0);
        var scenarioName = selectedRow.cells[1].innerText;
        $('.infoTxt em').text(scenarioName);
      });
    };

    function setScenarioLang() {
      if (lang == this.value) {
        return;
      }
      lang = Number(this.value);
      console.log('language change to -> ' + lang);

      // toggle 효과
      $('div.twins .btn_primary').removeClass('active');

      var toggleClass = document.getElementById('twins');
      var childButton = toggleClass.firstElementChild;

      if (lang === 1) {
        childButton = toggleClass.firstElementChild;
      } else if (lang === 2) {
        childButton = toggleClass.lastElementChild;
      }
      childButton.classList.add('active');

      // 하단 info 창 disappear
      $('.scenario_view ').removeClass('edit_show');
      makeAllPreview();

      // 챗봇 테스트 창 초기화
      $('#chatbot .blind').show();
      testBotShow = false;

      sendUserInfo({data:{userId:userId, lang: lang}});
    }

    // url에서 parameter 가져오기
    function getParameterByName(name) {
      name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
      var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
              results = regex.exec(location.search);
      return results == null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    }

    function downloadFile() {
      var frm = document.downloadForm;
      frm.submit();
    }

    $('.excel_download').on('click', function () {
      console.log('excel download');
      downloadFile();
    });

    function scenarioDownloadFile() {
    	var frm = document.downloadScenario;
    	scenario = getScenarioJsonData();

    	$("#scenarioJson").val(JSON.stringify(scenario));
    	frm.submit();
    }

    $('#scenarioDownload').on('click', function(){
    	console.log('excel scenario download');
    	scenarioDownloadFile();
    });

    $('.btn_lyr_close').on('click', handleLyrBoxClose);

    //업로드 확인 테스트
    $('#scenario_upload_v2 .btn_submit').on('click', function () {
      handleLyrBoxClose();
      uploadExcelV2();
    });

    function handleLyrBoxClose() {
      $('.lyrWrap').removeClass('show');
      $('.lyrWrap .lyrBox').removeClass('show');
    }

    //선택한 파일이름을 input value에 치환
    $('input[type="file"]').change(function (e) {
      var fileName = e.target.files[0].name;
      $('input[name="excel_file_name"]').val(fileName);
      $('input[name="excel_file_name_test"]').val(fileName);
    });

    //testbot open & close
    var testBotShow = false;
    $('#chat_test_start').on('click', function () {
      // 저장 된 적 없는 경우 -> '저장을 먼저 눌러주세요'
      if (host === '' || host === undefined || host === 0 || host === -1) {
        alert('<spring:message code="MESSAGE.CHATBOT.SAVE" javaScriptEscape="true"/>');
        return;
      }
      if (!testBotShow) {
        // makeChatBotIframe(hostName, host, lang);
        startChat();
        $('#chatbot .blind').hide();
        testBotShow = true;
      } else {
        testBotShow = false;
      }
    });

    // 챗봇 채팅 전송  (text 출력)
    $('#btn_chat').on('click', function() {
      var inputArea = $('.chatUI_btm .textArea');
      // textarea 텍스트 값 및 엔터처리
      var textValue = inputArea.val().replace(/(?:\r\n|\r|\n)/g, '<br>');
      // 채팅창에 text 출력
      if( inputArea.val().replace(/\s/g,"").length === 0){
        // text가 없으면 실행
      } else {
        // text가 있으면 실행
        inputArea.val('');
        createMsg('chatbot', 'user', textValue);
        m2uTalk(textValue).then( result => {
          createMsg('chatbot', 'bot', result.answer);
          highliteNode(result.intent);
        });
      }
    });

    // 챗봇 채팅 입력 : Shift + Enter
    $('.chatUI_btm .textArea').keyup(function (event) {
      if (event.keyCode == 13 && event.shiftKey) {
        var chatTxt = this.value;
        // this.value = chatTxt.substring(0,caret)+"\n"+chatTxt.substring(carent,chatTxt.length-1);
        event.stopPropagation();

        // 채팅 입력 : Enter
      } else if (event.keyCode == 13){
        $('#btn_chat').trigger('click');
      }
    });

    // 챗봇 재시작 버튼 클릭 이벤트
    $('.chatbot_refresh').on('click', function () {
      // makeChatBotIframe(hostName, host, lang);
      restartChat();
    });

    // 시나리오 저장
    $('.btn_save').on('click', function () {
      var $this = $(this);
      var txt = '저장';
      var changeTxt = '저장중';
      //'저장중' 효과를 백그라운드로 적용
      $this.text(changeTxt).addClass('gradient');

      scenario = getScenarioJsonData();
      $.ajax({
        url: "simpleBot/applyV2",
        data: JSON.stringify({
          simplebotId: simplebotId,
          userId: userId,
          companyId: companyId,
          scenarioJson: JSON.stringify(scenario),
          isExcelUpload: 'N',
        }),
        type: "POST",
        contentType: 'application/json'

      }).done(function (response) {
        clearLoadingUI($this, txt);
        if (response['msg'] && response['msg'].toLowerCase() === 'success') {
          alert('<spring:message code="MESSAGE.APPLY.SAVE" javaScriptEscape="true"/>');
          if (response['appliedAt']) {
            $('em.saved_time').text(response['appliedAt'] + ' Updated');
          }
          host = response.host;
          var selectedRow = $('.tbl_line_lst .active').get(0);
          selectedRow.cells[4].innerText = host;
        } else {
          alert('<spring:message code="MESSAGE.APPLY.CANTSAVE" javaScriptEscape="true"/>');
          console.log(response);
        }
        voiceBotMonitoring();

      }).fail(function (response) {
        clearLoadingUI($this, txt);
        alert('<spring:message code="MESSAGE.APPLY.CANTSAVE" javaScriptEscape="true"/>');
        console.log('apply() status code: ' + response.status);
      });
    });

    // 고객 정보 li 추가
    $('#btn_add_cust_row').on('click', addCustData);
    // 고객정보 input focus out 시 내용 저장
    $('#tblChatbotTestData input.ipt_txt').on('focusout', saveCustData);

    function clearLoadingUI(selector, text) {
      selector.text(text).removeClass('gradient');
    }

    function addScenario(scenarioName, scenarioLang) {
      console.log('add scenario! name: [' + scenarioName + '], lang: [' + scenarioLang.toString() + ']');

      var data = {userId:userId, companyId:companyId, name:scenarioName, lang:scenarioLang};

      $.ajax({
        url: "simpleBot/addScenario",
        data: JSON.stringify(data),
        type: "POST",
        contentType: 'application/json'
      }).done(function (response) {
        if (response === -1) {
          alert("중복된 이름입니다. 다시 시도해주세요.")
        } else {
          lang = scenarioLang;
          var trLength = $('.tbl_line_lst tr').length;
          var listTbody = $('.tbl_line_lst').find('tbody');
          listTbody.append('<tr>\n<td scope="row">'
              + trLength + '</td>\n<td><span class="text_ellipsis">'
              + scenarioName
              + '</span></td>'
              + '<td>' + convertLanguage('', scenarioLang) + '</td>'
              + '\n<td><a href="#delete_alert" class="tbl_btn delete btn_lyr_open text_hide delete_popup"><spring:message code="LABEL.DELETE" text="삭제"/></a></td>\n \
              <td style="display: none;">-1</td> \
              <td style="display: none;">' + response.toString() + '</td></tr>');

          setClickEvent();

          var lastTr = $('.tbl_line_lst tr').get(trLength);
          lastTr.removeEventListener('click', popupOpenEventListener);
          lastTr.addEventListener('click', popupOpenEventListener(), false);
          lastTr.cells[0].click();
          drawFlowChart({}, lang);
        }
      });
    }

    function deleteScenario(host, simplebotId) {
      console.log('get userId[' + userId + '], companyId[' + companyId + '] from FAST-AI. ');

      $.ajax({
        url: "simpleBot/deleteScenario",
        data: JSON.stringify({host:Number(host), simplebotId:Number(simplebotId)}), // simplebotID, host
        type: "POST",
        contentType: 'application/json'
      }).done(function (response) {
        alert('삭제되었습니다.');
        getSimpleBotList({data:{userId:userId, companyId: companyId}});
      }).error(function (req, status, error) {
        console.log('[deleteScenario error] code:'+req.status+'\n'+'message:'+req.responseText+'\n'+'error:'+error);
      });
    }

    function uploadExcelV2() {
      $('body').removeClass('lyr_show');

      var file = $("#excel_file_v2").val();
      if (file === "" || file === null) {
        alert('<spring:message code="MESSAGE.EXCEL.SELECT" javaScriptEscape="true"/>');
      }

      var options = {
        success: function (response) {
          alert('<spring:message code="MESSAGE.EXCEL.SUCCESS" javaScriptEscape="true"/>');
          $('.scenario_view ').removeClass('edit_show');
          makeAllPreview();
          jsPlumbToolkit.ready(function () {
            scenario = JSON.parse(response.scenarioJson);
            let copy = $.extend(true, {}, scenario);
            console.log('upload scenario');
            drawFlowChart(scenario, lang);
          });

        }, error: function (response) {
          alert('<spring:message code="MESSAGE.EXCEL.ERROR" javaScriptEscape="true"/>');
          console.log(response);
        },
        type: "POST"
      };
      $("#excelUploadScenarioForm input[name='simplebotId']").val(simplebotId);
      $("#excelUploadScenarioForm").ajaxSubmit(options);
    }

    function getSimpleBotList(e) {
      // 기존 목록 삭제
      var listTbody = $('.tbl_line_lst').find('tbody');
      listTbody.find('tr').remove();

      userId = e.data.userId;
      companyId = e.data.companyId;
      var keyword = e.data.hasOwnProperty('keyword') ? e.data.keyword : '';

      var data = {userId: userId, companyId: companyId, keyword: keyword};

      console.log('get userId[' + userId + '], companyId[' + companyId + '] from FAST-AI. ');

      $.ajax({
        url: "simpleBot/getSimpleBotList",
        data: JSON.stringify(data),
        type: "POST",
        contentType: 'application/json'
      }).done(function (response) {
        console.log(response);
        // list append
        for (var key in response) {
          // 한국어 simplebot list만 보이도록 처리
          if (response[key].lang != 1) continue;
          var langTmp = response[key].lang == 1 ?
              '<spring:message code="LABEL.LANG.KOR" text="한국어"/>' : '<spring:message code="LABEL.LANG.ENG" text="영어"/>';
          var hostTmp = response[key].host === undefined ? -1 : response[key].host;

          listTbody.append('<tr>\n<td scope="row">'
              + (parseInt(key) + 1) + '</td>\n<td><span class="text_ellipsis">'
              + response[key].name
              + '</span></td>'
              + '<td>' + langTmp + '</td>'
              + '\n<td><a href="#delete_alert" class="tbl_btn delete btn_lyr_open text_hide delete_popup"><spring:message code="LABEL.DELETE" text="삭제"/></a></td>\n \
              <td style="display: none;">' + hostTmp + '</td> \
              <td style="display: none;">' + response[key].id + '</td></tr>');
        }

        var trLength = $('.tbl_line_lst tr').length;
        var lastTr = $('.tbl_line_lst tr').get(trLength - 1);
        lastTr.removeEventListener('click', popupOpenEventListener);
        lastTr.addEventListener('click', popupOpenEventListener(), false);
        setClickEvent();

        if (response.length > 0 ) {
          // 첫번째 scenario select 상태로 보여주기
          var firstTr = $('.tbl_line_lst tr').get(1);
          firstTr.cells[0].click();
        } else {
          drawFlowChart({}, lang);
        }
      }).error(function (req, status, error) {
        console.log('[getSimpleBotList error] code:'+req.status+'\n'+'message:'+req.responseText+'\n'+'error:'+error);
      });
    }

    // 전화 걸기 버튼
    $('#voice_test_start').on('click', function () {

      // 테스트 고객 데이터 저장
      saveCustData().then();

      var requiredValues = $(this).parents('.required_value');
      var testerName = $("input[name='name']").val();
      var testerPhone = $("input[name='tel']").val();

      // 필수값 체크
      if ( requiredValues.find('[name="tel"]').val() === '' ) {
        alert('<spring:message code="MESSAGE.VOICEBOT.TEL" javaScriptEscape="true"/>');
        return;
      }
      if ( requiredValues.find('[name="name"]').val() === '' ) {
        alert('<spring:message code="MESSAGE.VOICEBOT.NAME" javaScriptEscape="true"/>');
        return;
      }
      if (host === undefined || host === '' || host === 0 || userId === undefined || userId === '') {
        alert('<spring:message code="MESSAGE.VOICEBOT.SAVE" javaScriptEscape="true"/>');
        return;
      }

      // 대기중일 때 효과 .gradient
      $(this).text('<spring:message code="LABEL.WAITING" javaScriptEscape="true"/>').addClass('gradient');
      $(this).attr('disabled', true);

      alert('<spring:message code="MESSAGE.VOICEBOT.CALL" javaScriptEscape="true"/>');

      // setTimeout(function() {
      //   $('#voice_test_start').removeClass('btn_primary');
      //   $('#voice_test_start').addClass('btn_secondary');
      //   $('#voice_test_start').text('통화중');
      // }, 3000);

      setTimeout(function() {
        $('#voice_test_start').removeClass('gradient');
        $('#voice_test_start').text('전화 걸기');
        $('#voice_test_start').attr('disabled', false);
      }, 3000);

      if (testerContractNo && testerTelNo == testerPhone) {
           // node UI css clear
           clearNodeEffect();
           // voicebot window clear
           clearVoiceBotWindow();

           callStart().then(() => {
             // 1초마다 대기자수를 체크
             checkWaitingNo = setInterval(getWaitingNo, 1000);
           });
      } else {
        getContractNo(testerPhone).then((contractNo) => {
          // node UI css clear
          clearNodeEffect();
          // voicebot window clear
          clearVoiceBotWindow();

          testerContractNo = contractNo;
          callStart().then(() => {
            // 1초마다 대기자수를 체크
            checkWaitingNo = setInterval(getWaitingNo, 1000);
          });
        });
      }

    });

    // 해당 전화번호의 contractNo 가져오기
    function getContractNo(telNo) {

      let ajPromise = new Promise((resolve, reject) => {
        $.ajax({
          url: "simpleBot/getContractNo",
          data: JSON.stringify({simplebotId: simplebotId, telNo: telNo}),
          type: "POST",
          contentType: 'application/json'
        }).done(function (response) {
          console.log('getContractNo done!!');
          console.log(response);
          resolve(response);
        }).fail(function (response) {
          console.log('saveTesterInfo fail!!');
          console.log(response);
          reject();
        });
      });

      return ajPromise;
    }

    // CM 으로 전화걸기
    function callStart() {
    
   let ajPromise = new Promise((resolve, reject) => {
        $.ajax({
          url: "simpleBot/callStart",
          data: JSON.stringify({contractNo: testerContractNo, lang: lang, userId: userId}),
          type: "POST",
          contentType: 'application/json'
        }).done(function (response) {
          console.log('callStart done!!');
          console.debug(response);
          resolve();
        }).fail(function (response) {
          console.log('callStart fail!!');
          console.debug(response);
          reject();
        });
      });

      return ajPromise;
    }

    // 수신자 정보 조회
    function findTesterInfo() {
      $.ajax({
        url: "simpleBot/getTesterInfo",
        data: JSON.stringify({tester: userId, lang: lang}),
        type: "POST",
        contentType: 'application/json'
      }).done(function (response) {
        // console.log('findTesterInfo res: ' + response);
        if (response !== "WRONG" && response !== "ERROR" && response['status'] !== "failed") {
          var responseData = response['result'];
          testerContractNo = responseData['contract_no'];
          $("input[name='name']").val(responseData['name']);
          $("input[name='tel']").val(responseData['tel_no']);
        }
      }).fail(function (response) {
        console.log('findTesterInfo fail!!');
        console.log(response);
      });
    }

 // 대기 인원 수 조회해오기
    function getWaitingNo() {
      $.ajax({
        url: "simpleBot/callList",
        data: JSON.stringify({contract_no: testerContractNo, lang: lang}),
        type: "POST",
        contentType: 'application/json'
      }).done(function (response) {
        console.log(response !== 0 ? response : 0);
        let voicebotCover = $(".voicebot_start").find('p');
//         if (response['cnt'] === "" || response['cnt'] === undefined || response['cnt'] === "0") {
        if (response === "" || response === undefined || response === 0) {
          // 대기자가 없을 경우
          $(".make_call").children('span').children('em').text("0");
          $("#voicebot").find('.lst_talk').empty();
          $('#voice_test_start').text('<spring:message code="LABEL.CALL" javaScriptEscape="true"/>').removeClass('gradient');
          $('#voice_test_start').attr('disabled', false);
          clearInterval(checkWaitingNo);
          setTimeout(function() {
            $(".make_call").children('span').css('display', 'none');
          }, 3000);
          if (voicebotCover.length > 0) {
            $('.voicebot_start').removeClass('blind');
            voicebotCover.remove();
          }
        } else {
          // 대기자가 있는 경우
//           $(".make_call").children('span').children('em').text(response['cnt']);
          $(".make_call").children('span').children('em').text(response);
       	  setTimeout(function() {
      		 $(".make_call").children('span').css('display', 'block');
          }, 3000);
          if (voicebotCover.length > 0) {
            $(".voicebot_start").find('p').text('<spring:message code="MESSAGE.WAITING" javaScriptEscape="true"/>');
          } else {
            let voicebotStart = document.getElementsByClassName('voicebot_start')[0];
            let p_msg = document.createElement("p");
            p_msg.innerHTML = '<spring:message code="MESSAGE.WAITING" javaScriptEscape="true"/>';
            voicebotStart.appendChild(p_msg);
          }
        }
        if (socket === null || !socket.connected) {
          if (voicebotCover.length > 0) {
            $(".voicebot_start").children('p').text('<spring:message code="MESSAGE.SOCKET.FAIL" javaScriptEscape="true"/>');
          } else {
            let voicebotStart = document.getElementsByClassName('voicebot_start')[0];
            let p_msg = document.createElement("p");
            p_msg.innerHTML = '<spring:message code="MESSAGE.SOCKET.FAIL" javaScriptEscape="true"/>';
            voicebotStart.appendChild(p_msg);
          }
        }
      }).fail(function (response) {
        console.log('callList fail!!');
        console.log(response);
        alert('<spring:message code="MESSAGE.CALLLIST.FAIL" javaScriptEscape="true"/>');
        $(".make_call").children('span').children('em').text("0");
        $("#voicebot").find('.lst_talk').empty();
        $('#voice_test_start').text('<spring:message code="LABEL.CALL" javaScriptEscape="true"/>').removeClass('gradient');
        $('#voice_test_start').attr('disabled', false);
        clearInterval(checkWaitingNo);
        if ($(".voicebot_start").find('p').length > 0) {
          $(".voicebot_start").find('p').remove();
        }
      });
    }

    // 수정 201117 AMR tab_menu 클릭
    $('.tab_menu button').on('click', function () {
      var thisTabMenu = $(this).parent();
      var thisCont = thisTabMenu.next('.tab_cont').children();
      var index = $(this).index();

      thisTabMenu.find('button').removeClass('on');
      thisCont.removeClass('on');

      $(this).addClass('on');
      thisCont.eq(index).addClass('on');
    });

    $('.btn_search').on('click', function() {
      var keyword = $('.search').val();
      var data = {data:{userId: userId, companyId: companyId, keyword: keyword}};
      getSimpleBotList(data);
    });

    $('.search').on('keydown', function(e){
      var keyCode = e.which?e.which:e.keyCode;
      if ( keyCode === 13 ) {
        var keyword = $('.search').val();
        var data = {data:{userId: userId, companyId: companyId, keyword: keyword}};
        getSimpleBotList(data);
      }
    });

    function voiceBotMonitoring() {
      if (host === undefined || host === '' || host === 0
          || userId === undefined || userId === '') {
        return;
      }

      isSocketConnected = true;
      if(socket) {
        socket.disconnect();
      }
      socket = io.connect('wss://fast-aicc.maum.ai:51000', {'force new connection': true});

      // 웹에서 계속 연결 시도하기 때문에 close
      setTimeout(function() {
        if (!socket.connected) {
          socket.close();
          isSocketConnected = false;
          socket = null;
          console.log('socket connection failed');
        }
      }, 3000);

      socket.on('connection', function(data) {
        console.log('connect host: [' + host + ']');
        if(data.type === 'connected') {
          socket.emit('connection', {
            type : 'join',
            name : userId,
            room : host
          });
        }
      });

      // 음성봇 socket msg
      socket.on('message', function(data) {
        console.log('[socket] type: ' + data.type + ', intent: ' + data.intent + ', message: ' + data.message);
        if (data.message === sdsStartChat || data.message.charAt(0) === '#') {
          return;
        }
        createMsg('voicebot', data.type, data.message);
        highliteNode(data.intent);
      });
    }

    /*Chatting UI Func Start*/
    function createMsg(botType='chatbot', userType='user', msg, nowTime) {
      console.log('createMsg');

      let botDiv;
      // botType 체크
      if (botType === 'chatbot') {
        botDiv = $('#chatbot');
      } else if (botType === 'voicebot') {
        botDiv = $('#voicebot');
      } else {
        console.log('[createMsg] wrong botType: ' + botType);
        return;
      }

      // userType 체크
      if (userType !== 'user' && userType !=='bot' && userType !=='system') {
        console.log('[createMsg] wrong userType: ' + userType);
        return;
      }

      // 아직 system용 style이 없으니 bot으로 처리
      if (userType == 'system') {
        userType = 'bot';
      }

      // nowTime 체크
      if (!nowTime) {
        nowTime = getNowTime();
      }

      msg = msg.replace(/\|/gi, "");
      var ul = botDiv.find('.lst_talk')[0];
      let li = document.createElement("li");
      li.setAttribute("class", userType);
      let dv_msg = document.createElement("div");
      let em_msg = document.createElement("em");
      let dv_date = document.createElement("div");
      dv_msg.setAttribute("class", "bot_msg");
      em_msg.setAttribute("class", "txt");
      dv_date.setAttribute("class", "date");
      em_msg.innerHTML = msg;
      dv_date.innerText = nowTime;
      dv_msg.appendChild(em_msg);
      dv_msg.appendChild(dv_date);
      li.appendChild(dv_msg);
      ul.appendChild(li);

      let botMid = botDiv.find('.chatUI_mid');
      botMid.scrollTop(botMid[0].scrollHeight);
    }

    // 채팅 시작 (m2u open)
    function startChat(msg=sdsStartChat) {
      // 테스트 고객 데이터 저장
      saveCustData().then(() => {

        // node 효과 reset
        clearNodeEffect();
        // chatbot 창 clear
        $('#chatbot .lst_talk').empty();

        // call "SignIn" & "Open"
        if (!m2uToken) {
          m2uSignIn().then( token => {
            m2uToken = token;

            m2uOpen(msg).then( answer => {
              createMsg('chatbot', 'bot', answer);
              highliteNode(sdsStartChat);
            }).catch((errMsg) => {
              createMsg('chatbot', 'bot', errMsg)
            });

          }).catch((errMsg) => {
            createMsg('chatbot', 'bot', errMsg);
          });

        } else {

          // call "Open"
          m2uOpen(msg).then( answer => {
            createMsg('chatbot', 'bot', answer);
            highliteNode(sdsStartChat);
          });

        }
      });
    }

    function restartChat(msg=sdsStartChat) {
      // 테스트 고객 데이터 저장
      saveCustData().then(() => {

        // node 효과 reset
        clearNodeEffect();
        // chatbot 창 clear
        $('#chatbot .lst_talk').empty();

        if (!m2uToken) {
          startChat();
          return;
        }

        m2uClose().then( () => {
          m2uOpen(msg).then( answer => {
            createMsg('chatbot', 'bot', answer);
            highliteNode(sdsStartChat);
          });
        });
      });
    }

    // 고객 데이터 UI 추가 함수
    function addCustData(key, val) {

      var customKeySample = ["이름", "생년월일", "상품명", "가입월", "한도", "연령대"];
      var customValSample = ["홍길동", "930111","행복한FAST생명", "2020년 7월", "1000만원", "20대"];

      var tblChatbotTestData = $('#tblChatbotTestData');
      var tableBody = tblChatbotTestData.find('tbody');
      var clonedNode = $($('#tempChatbotTestData').html())[0]; //template (ie 호환위해 table부터 작성하고 tr만 가져옴)
      var $tableRow = $(clonedNode.getElementsByTagName('tr'));
      var checkboxId = generateUUID('checkxxxxx');
      var tblWrap = tblChatbotTestData.parent('.tbl_wrap');

      var lastElIdx = tableBody[0].childElementCount;
      var inputs = $tableRow.find('td input[type="text"]');
      inputs[0].setAttribute("placeholder", customKeySample[(lastElIdx)%customKeySample.length]);
      inputs[1].setAttribute("placeholder", customValSample[(lastElIdx)%customValSample.length]);

      if (key && val) {
        inputs[0].value = key;
        inputs[1].value = val;
      }

      $tableRow.find('input[type="checkbox"]').attr('id', checkboxId);
      $tableRow.find('label').attr('for', checkboxId);
      $tableRow.appendTo(tableBody);

      var scrollHeight = tblWrap[0].scrollHeight;
      tblWrap.scrollTop(scrollHeight);

    }

    // 고객 데이터 저장 ajax
    function saveCustData() {
      let ajPromise = new Promise(resolve => {
        let custData = {};
        try {

          var tblChatbotTestData = $('#tblChatbotTestData');
          var $tableBody = $(tblChatbotTestData.find('tbody')[0]);
          var trs = $tableBody.find('tr');

          for (let i = 0; i < trs.length; i++) {
            var inputs = $(trs[i]).find('td input[type="text"]');
            let key = inputs[0].value;
            let val = inputs[1].value;
            if (key) {
              custData[key] = val;
            }
          }
        } catch (e) {
          console.error(e);
        }

        let custDataStr = JSON.stringify(custData);
        console.log('고객 데이터 저장: ' + custDataStr);

        $.ajax({
          url: "simpleBot/saveTestCustData",
          data: JSON.stringify({simplebotId: simplebotId, custData: custDataStr}),
          type: "POST",
          contentType: 'application/json'
        }).done(function (response) {
          console.debug('saveTestCustData done');
          resolve();

        }).fail(function (response) {
          console.debug('saveTestCustData fail!!');
          resolve();
        });
        });
      return ajPromise;
    }

    // 시뮬레이션 시 node UI highlite
    function highliteNode(nodeLabel) {
      if (!nodeLabel || nodeLabel == '') {
        return;
      }
      clearNodeEffect();

      if (nodeLabel === sdsStartChat) {
        $('.jtk-node:first-child').addClass('gradient');
        return;
      }

      let nodes = $('div.scenario div.jtk-node');
      for (let i = 0; i < nodes.length; i++) {
        let node = nodes[i];
        if (node.innerHTML.trim() === nodeLabel) {
          node.classList.add('gradient');
        }
      }
    }

    function clearNodeEffect() {
      $('div.scenario div.jtk-node').removeClass('gradient');
    }

    function clearVoiceBotWindow() {
      // 음성봇 테스트 창 초기화
      let voicebotCover = $(".voicebot_start").find('p');
      $('.voicebot_start').removeClass('blind');
      voicebotCover.remove();

      $('#voicebot ul.lst_talk').html('');
    }

    /*Chatting UI Func Start*/



     /*M2U Client Function Start*/

    function randomString() {
      const chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz';
      const string_length = 15;
      let randomstring = '';
      for (let i = 0; i < string_length; i++) {
        let rnum = Math.floor(Math.random() * chars.length);
        randomstring += chars.substring(rnum, rnum + 1);
      }
      return randomstring;
    }

    function m2uSignIn() {
     let signInParam = {
        "userKey": "admin",
          "passphrase": "1234"
      };

     let ajPromise = new Promise((resolve, reject) =>
      {

        $.ajax({
          url: m2uUrl + "/api/v3/auth/signIn",
          data: JSON.stringify(signInParam),
          type: "POST",
          beforeSend: function (xhr) {
            xhr.setRequestHeader("Content-type", "application/json");
            xhr.setRequestHeader("m2u-auth-internal", "m2u-auth-internal");
          },
        }).done(function (response) {
          console.log('m2uSignIn done!!');
          console.log(typeof response);
          response = JSON.parse(response);

          if (response.directive.payload.hasOwnProperty('authSuccess')) {
            let token = response.directive.payload.authSuccess.authToken;
            resolve(token);
          } else {
            reject('[ERROR] Cannot connect server! : SignIn auth Fail');
          }
        }).fail(function (response) {
          console.log('m2uSignIn fail!!');
          console.log(response);
          reject('[ERROR] Cannot connect server! : SignIn ajax Fail');
        });
      });

     return ajPromise;
    }


    function m2uOpen(msg) {
      // open에서 m2uLang이 중요하지 x
      let m2uLang;
      if (lang == 1) {
        m2uLang = "ko_KR";
      } else if (lang == 2) {
        m2uLang = "en_US"
      }

      m2uDeviceId = 'WEB_' + randomString();

      let openParam = {
        'payload': {
          'utter': msg,
          'lang': m2uLang,
          'chatbot': m2uChatbot,
          'meta': {
            'debug': true,
            'simplebot.id' : simplebotId.toString()
          }
        },
        'device': {
          'id': m2uDeviceId,
          'type': 'WEB',
          'version': '0.1',
          'channel': 'FAST-SIMPLEBOT'
        },
        'location': {
          'latitude': 10.3,
          'longitude': 20.5,
          'location': 'mindslab'
        },
        'authToken': m2uToken
      };

      let ajPromise = new Promise((resolve, reject) => {
        $.ajax({
          url: m2uUrl + "/api/v3/dialog/open",
          data: JSON.stringify(openParam),
          type: "POST",
          beforeSend: function (xhr) {
            xhr.setRequestHeader("Content-type", "application/json");
            xhr.setRequestHeader("m2u-auth-internal", "m2u-auth-internal");
          },
        }).done(function (response) {
          console.log('m2uOpen done!!');
          console.log(response);
          response = JSON.parse(response);
          let answer;
          if (response.hasOwnProperty('exception')) {
            const exception = response.exception;
            answer = '[ERROR] ' + exception.statusCode + '\nmessage : ' + exception.exMessage;
            reject(answer);
          } else {
            let meta = response.directive.payload.response;
            answer = response.directive.payload.response.speech.utter;
          }
          resolve(answer);
        }).fail(function (response) {
          console.log('m2uOpen fail!!');
          console.log(response);
          reject('[ERROR] Cannot "Open" Server!');
        });

      });

      return ajPromise;
    }

    function m2uTalk(msg) {
      let m2uLang;
      if (lang == 1) {
        m2uLang = "ko_KR";
      } else if (lang == 2) {
        m2uLang = "en_US"
      }

      let textToTextTalkParam = {
        'payload': {
          'utter': msg,
          'lang': m2uLang,
          'meta': {
            debug: true,
            'simplebot.id': simplebotId.toString(),
            number: 1.0,
            text: 'true',
            obj: [
              'complex',
              {example: 2345}
            ]
          }
        },
        'device': {
          'id': m2uDeviceId,
          'type': 'WEB',
          'version': '0.1',
          'channel': 'FAST-SIMPLEBOT'
        },
        'location': {
          'latitude': 10.3,
          'longitude': 20.5,
          'location': 'mindslab'
        },
        'authToken': m2uToken
      };

      let ajPromise = new Promise((resolve, reject) => {
        $.ajax({
          url: m2uUrl + "/api/v3/dialog/textToTextTalk",
          data: JSON.stringify(textToTextTalkParam),
          type: "POST",
          beforeSend: function (xhr) {
            xhr.setRequestHeader("Content-type", "application/json");
            xhr.setRequestHeader("m2u-auth-internal", "m2u-auth-internal");
          },
        }).done(function (response) {
          console.log('m2uTalk done!!');
          console.log(response);
          response = JSON.parse(response);
          let answer;
          let intent;
          if (response.hasOwnProperty('exception')) {
            const exception = response.exception;
            answer = `errCode : ` + exception.statusCode + `<br/> message : ` + exception.exMessage
          } else {
            response = response.directive.payload.response;
            let meta = response.meta;
            answer = response.speech.utter;
            intent = meta['sds.intent'];
          }
          let result = {'answer': answer, 'intent': intent};
          resolve(result);
        }).fail(function (response) {
          console.log('m2uTalk fail!!');
          console.log(response);

          reject('m2uTalk Fail');
        });
      });

      return ajPromise;
    }

    function m2uClose(msg) {

      let closeParam = {
        'device': {
          'id': m2uDeviceId,
          'type': 'WEB',
          'version': '0.1',
          'channel': 'FAST-SIMPLEBOT'
        },
        'location': {
          'latitude': 10.3,
          'longitude': 20.5,
          'location': 'mindslab'
        },
        'authToken': m2uToken
      };

      let ajPromise = new Promise((resolve, reject) => {
        $.ajax({
          url: m2uUrl + "/api/v3/dialog/close",
          data: JSON.stringify(closeParam),
          type: "POST",
          beforeSend: function (xhr) {
            xhr.setRequestHeader("Content-type", "application/json");
            xhr.setRequestHeader("m2u-auth-internal", "m2u-auth-internal");
          },
        }).done(function (response) {
          console.log('m2uClose done!!');
          console.log(response);
          resolve();
        }).fail(function (response) {
          console.log('m2uClose fail!!');
          console.log(response);
          reject('m2uClose Fail');
        });

      });

      return ajPromise;
    }
    /*M2U Client Function End*/


    function setClickEvent() {
      // click event 해제
      $('.tbl_line_lst td').off('click');

      // 추가 AMR tbl_line 테이블에서 td 클릭 시 active
      $('.tbl_line_lst td').on('click', function(){
        $(this).parents('.tbl_line_lst').find('tr').removeClass('active');
        $(this).parent().addClass('active');

        // 클릭한 row의 host, lang으로 scenarioJson 조회
        if ($(this).parent().children().length > 0) {
          var tdSimplebotId = $(this).parent().children()[5];
          getSimplebotById(tdSimplebotId.innerText);
          // 시나리오 변경시 하단 데이터 영역 hide
          $('.scenario_view ').removeClass('edit_show');
          makeAllPreview();
        }
      });
    }

    $('#btn_add_scenario').on('click', function() {
      var scenarioName = $('#scenario_name').val();
      var scenarioLang = convertLanguage($('#scenario_lang option:selected').text(), -1);
      $('#scenario_name').val('');

      addScenario(scenarioName, scenarioLang);
    });

    $('#btn_delete_scenario').on('click', function() {
      var selectedRow = $('.tbl_line_lst .active').get(0);
      var host = selectedRow.cells[4].innerText;
      var simplebotId = selectedRow.cells[5].innerText;

      deleteScenario(host, simplebotId);
    });

    $('#btn_delete_intent').on('click', function(){
		var delIntent = $('.actPop').parent().find('.sub_tit').text();

		var nodeAttr = changeNodeData['attr'][0];
		var intentList = nodeAttr.intentList;
		if(intentList != null && intentList != ''){
			for (var i = 0; i < intentList.length; i++) {
				if(intentList[i].intent == delIntent){
					intentList.splice(i,1);
				}
			}
		}else{
			delIntent = "utter" + delIntent;

			if($('.actPop').parent().find('.sub_tit').text() == 'YES'){
				delIntent = "utterY";
			}else if($('.actPop').parent().find('.sub_tit').text() == 'NO'){
				delIntent = "utterN";
			}else if($('.actPop').parent().find('.sub_tit').text() == 'UNKNOWN'){
				delIntent = "utterU";
			}else if($('.actPop').parent().find('.sub_tit').text() == 'REPEAT'){
				delIntent = "utterR";
			}

			var nodeAttr = changeNodeData['attr'][0];

			delete nodeAttr[delIntent];

		}


		$('.actPop').parent().remove();

    });

    function getSimplebotById(id) {
      $.ajax({
        url: "simpleBot/getSimplebotById",
        data: JSON.stringify({simplebotId: Number(id)}),
        type: "POST",
        contentType: 'application/json'
      }).done(function (response) {
        console.log(response);

        if (!response) {
          alert('삭제된 시나리오 입니다.');
          getSimpleBotList();
        } else {

          simplebotId = response.id;
          hostName = 'ynbot_' + simplebotId;
          host = response.host;
          lang = response.lang;
          try {
            scenario = JSON.parse(response.scenarioJson);
          } catch {
            console.log('scenario json parsing err.');
            console.log(response.scenarioJson);
            scenario = {};
          }

          console.log('call drawFlowChart');
          drawFlowChart(scenario, lang);
          console.log('call drawMetaData');
          drawMetaData(response);
          console.log('call voiceBotMonitoring');
          voiceBotMonitoring();
        }
      }).error(function (req, status, error) {
        console.log('[getSimplebotById error] code:'+req.status+'\n'+'message:'+req.responseText+'\n'+'error:'+error);
      });
    }

    function drawMetaData(simplebot) {
      // 반영 datetime set
      let appliedAt = simplebot.appliedAt;
      if (appliedAt) {
        $('em.saved_time').text(appliedAt + ' Updated');
      }

      testerContractNo = simplebot.testContractNo;

      // 테스트 고객데이터 set
      let testCustData = simplebot.testCustData;
      // 고객데이터 UI clear
      $('table#tblChatbotTestData tbody tr').remove();
      // 고객데이터 있으면
      if (testCustData && testCustData.length > 2) {
        testCustData = JSON.parse(testCustData);
        for (let key in testCustData) {
          let val = testCustData[key];
          addCustData(key, val);
        }
        // 고객데이터 없으면
      } else {
        addCustData();
      }

      // 테스트 전화번호 set
      testerTelNo = simplebot.testTelNo;
      if (testerTelNo) {
        $("input[name='tel']").val(testerTelNo);
      } else {
        $("input[name='tel']").val('');
      }

    }

    function convertLanguage(langStr, langNum) {
      // 1이 넘어오면 한국어, 한국어가 넘어오면 1을 return하도록 한다.
      if (langStr === null || langStr === '') {
        for (var i in langEnum) {
          if (langEnum[i] === langNum) {
            return i;
          }
        }
      } else {
        return langEnum[langStr];
      }
    }

    // 2020.7.13 PM 9:45
    function getNowTime() {

      var year  = new Date().getFullYear();  //현재 년도
      var month = new Date().getMonth()+1;  //현재 월
      var date  = new Date().getDate();  //현재 일

      var ampm = new Date().getHours() >= 12 ? 'PM' : 'AM';
      var	thisHours = new Date().getHours() >=13 ?  new Date().getHours()-12 : new Date().getHours(); //현재 시
      var	thisMinutes = new Date().getMinutes() < 10 ? '0' + new Date().getMinutes() : new Date().getMinutes(); //현재 분
      var NowTime = year + "." + month + "." + date + " " + ampm + " " + thisHours + ':' + thisMinutes;

      return NowTime;
    }

    // 시나리오 테스트 데이터 입력 테이블 선택삭제 버튼 disable 효과
    $('#tblChatbotTestData').on('click', 'label', function(){
      var $tblWrap = $(this).parents('.tbl_wrap');
      var $tbody = $(this).parents('tbody');
      var $btnRowDelete = $tblWrap.prev('.tbl_commands').find('.btn_remove_row');
      var thisCheck = $(this).prev('input[type="checkbox"]').prop('checked');
      var checkboxes = $tbody.find('input[type="checkbox"]');
      var trueLength = 0;

      $tbody.find('input[type="checkbox"]');

      if ( !thisCheck ) {
        $btnRowDelete.removeAttr('disabled');
      } else {
        checkboxes.each(function(){
          var checked = $(this).prop('checked');

          if ( checked ) {
            trueLength += 1;
          }
        });
      }
      trueLength === 1 ? $btnRowDelete.attr('disabled', '') : $btnRowDelete.removeAttr('disabled');
    });

    // 시나리오 테스트 데이터 선택 삭제
    $('.test_data .btn_remove_row').on('click', function(){
      var $commands = $(this).parents('.tbl_commands');
      var $table = $commands.next();
      var $tr = $table.find('tbody tr');

      $tr.each(function(){
        $thisTr = $(this);
        $thisCheckbox = $thisTr.find('td').eq(0).find('input[type="checkbox"]');

        if ( $thisCheckbox.prop('checked') ) {
          $thisTr.remove();
        }
      });

      $(this).attr('disabled', '');

      // 테스트 고객 데이터 저장
      saveCustData().then();
    });

    function handleIntentionUserContent() {
        $('.user_content .sub_tit').on('click', 'span', function(){
            var $thisTitle = $(this);
            var parent = $thisTitle.parent('.sub_tit');
            var desc = $thisTitle.text();
            var temp = '<input type="text" class="ipt_txt" value="' + desc + '">';

            parent.empty().html(temp);
            parent.find('input').focus();

            var nodeAttr = changeNodeData['attr'][0];
            var intentList = nodeAttr.intentList;
            if(intentList != null && intentList != ''){
				oldKey = desc;
            }else{
            	if(desc == "YES"){
            	    oldKey = "utterY";
                }else if(desc == "NO"){
            	    oldKey = "utterN";
                }else if(desc == "UNKNOWN"){
            	    oldKey = "utterU";
                }else if(desc == "REPEAT"){
            	    oldKey = "utterR";
                }else {
            	    oldKey = "utter"+desc;
                }
            }
//             if(desc == "YES"){
//           	  oldKey = "utterY";
//             }else if(desc == "NO"){
//           	  oldKey = "utterN";
//             }else if(desc == "UNKNOWN"){
//           	  oldKey = "utterU";
//             }else if(desc == "REPEAT"){
//           	  oldKey = "utterR";
//             }else {
//           	  oldKey = "utter"+desc;
//             }

        });

        $('.user_content .sub_tit').on('focusout', 'input[type="text"]', function(){
            var $thisTitle = $(this);
            var parent = $thisTitle.parent('.sub_tit');
            var desc = $thisTitle.val();
            var temp = '<span>' + desc + '</span>';
            var warningTemp = '<span class="warning">이미 등록되어 있는 조건입니다.</span>';

            var id = desc.toLowerCase() + "_say";
            $(this).parent().parent().find('.description > p').attr('id',id);

            if ( desc == '중복' ) { //중복된 타이틀을 사용할 경우 focusout이 되지 않고 경고문구 생성
                var hasWarning = parent.find('.warning').length;
                if ( !hasWarning ) {
                    parent.append(warningTemp);
                    return;
                }

                $thisTitle.focus();
                return;
            }
            parent.empty().html(temp);

            var newKey = "";

            var nodeAttr = changeNodeData['attr'][0];
            var intentList = nodeAttr.intentList;

            if(intentList != null && intentList != ''){

            	newKey = desc;

	            if(intentList.length > 0 && ($("p[id*='_say']").length -1) == intentList.length){
	          	  for (var i = 0; i < intentList.length; i++) {
	          		  if(nodeAttr.intentList[i].intent == oldKey){
	          			  nodeAttr.intentList[i].intent = newKey;
	          		  }
	          	  }
	            }else if(($("p[id*='_say']").length -1) > intentList.length){
	              var obj = new Object();
	              obj.intent = newKey;
	              obj.answer = "";
	              obj.info = "";
	              obj.nextTask = "";

	              intentList.push(obj);
	            } else{
	          	  var obj = new Object();
	          	  obj.intent = newKey;
	          	  obj.answer = "";
	          	  obj.info = "";
	          	  obj.nextTask = "";

	          	  intentList.push(obj);
	            }
            }else {
            	 if(desc == "YES"){
                    newKey = "utterY";
                 }else if(desc == "NO"){
                    newKey = "utterN";
                 }else if(desc == "UNKNOWN"){
                    newKey = "utterU";
                 }else if(desc == "REPEAT"){
                    newKey = "utterR";
                 }else{
                    newKey = "utter"+desc;
                 }

	            if(nodeAttr[oldKey] != null){
	  	          	nodeAttr[newKey] = nodeAttr[oldKey];
	            }else {
	  	          	nodeAttr[newKey] = "";
	            }
	            delete nodeAttr[oldKey];
            }


        });

      //201125 AMR 텍스트 수정 박스 읽기모드
        $('.user_content .description').on('click', 'p.textarea', function(){
            var $thisParent = $(this).parent('.description');
            var $thisP = $(this);
            var desc = delNewLine($(this).text());
            var temp = '<textarea id='+$(this).parent('.description').context.id+'>' + desc + '</textarea>';

            $thisP.remove();
            $thisParent.prepend(temp);
            $thisParent.find('textarea').focus();
        });

        //201125 AMR 텍스트 수정 박스 쓰기모드
        $('.user_content .description').on('focusout', 'textarea', function(){
            var $thisParent = $(this).parent('.description');
            var $thisTextarea = $thisParent.find('textarea');
            var desc = delNewLine($thisTextarea.val());
            if(desc != null){
	            var temp = '<p class="textarea" id='+$(this).parent('.description').context.id+'>' + desc.replace(/(?!>{)({)/gi, '<span><').replace(/(?!}<)(})/gi, '></span>') + '</p>';
				var checkId = $(this).parent().parent().find('.sub_tit > span').text().toLowerCase() + "_say";
// 				var utterKey = "utter"+ $(this).parent().parent().find('.sub_tit > span').text();
				var utterKey = $(this).parent().parent().find('.sub_tit > span').text();
	            $thisTextarea.remove();
	            $thisParent.prepend(temp);

	            var nodeAttr = changeNodeData['attr'][0];
	            // 기본 및 조건 발화 수정. text 입력 후 focusout이 되야 수정이 됨.
	            var intentList = nodeAttr.intentList;

	            if($thisParent.context.id == "first_say"){
	                nodeAttr.utter = desc;

	                if(nodeAttr.acceptSttStcIdx == null){
	                	nodeAttr.acceptSttStcIdx = '';
	                }
	                if(nodeAttr.repeatAnswerStcIdx == null){
	                	nodeAttr.repeatAnswerStcIdx = '';
	                }

		            $('.ignore').html(markedSentence(desc,nodeAttr.acceptSttStcIdx.split(',')));
		            $('.repeat').html(markedSentence(desc,nodeAttr.repeatAnswerStcIdx.split(',')));
	            }else{
					if(intentList != null && intentList != ''){
			            for (var i = 0; i < intentList.length; i++) {
			    			 if(intentList[i].intent == utterKey){
				    		 	intentList[i].answer = desc;
				    	 	 }
					 	}
					}else{
						utterKey = "utter"+ utterKey;

						if($thisParent.context.id == "yes_say"){
		 	                nodeAttr.utterY = desc;
		 	            }else if($thisParent.context.id == "no_say"){
		 	                nodeAttr.utterN = desc;
		 	            }else if($thisParent.context.id == "unknown_say"){
		 	                nodeAttr.utterU = desc;
		 	            }else if($thisParent.context.id == "repeat_say"){
		 	                nodeAttr.utterR = desc;
		 	            }else if($thisParent.context.id == checkId){
		 	            	nodeAttr[utterKey] = desc;
		 	            }
					}
	            }



// 	            if($thisParent.context.id == "first_say"){
// 	                nodeAttr.utter = desc;

// 	                if(nodeAttr.acceptSttStcIdx == null){
// 	                	nodeAttr.acceptSttStcIdx = '';
// 	                }
// 	                if(nodeAttr.repeatAnswerStcIdx == null){
// 	                	nodeAttr.repeatAnswerStcIdx = '';
// 	                }

// 		            $('.ignore').html(markedSentence(desc,nodeAttr.acceptSttStcIdx.split(',')));
// 		            $('.repeat').html(markedSentence(desc,nodeAttr.repeatAnswerStcIdx.split(',')));
// 	            }else if($thisParent.context.id == "yes_say"){
// 	                nodeAttr.utterY = desc;
// 	            }else if($thisParent.context.id == "no_say"){
// 	                nodeAttr.utterN = desc;
// 	            }else if($thisParent.context.id == "unknown_say"){
// 	                nodeAttr.utterU = desc;
// 	            }else if($thisParent.context.id == "repeat_say"){
// 	                nodeAttr.utterR = desc;
// 	            }else if($thisParent.context.id == checkId){
// 	            	nodeAttr[utterKey] = desc;
// 	            }
            }
        });

    }
    handleIntentionUserContent();

    // 사용자 설정 사용자 발화 / 다이얼 checkbox 수정
    $(".checkSetting").on("click",function(){
    	var nodeAttr = changeNodeData['attr'][0];
	   	var check01 = $('input:checkbox[id="check01"]').is(":checked");
	   	var check02 = $('input:checkbox[id="check02"]').is(":checked");
   	    if(check01 == true && check02 == true){
   	    	nodeAttr.inputType = 2;
   	    }else if(check01 == true){
   	    	nodeAttr.inputType = 0;
   	    }else if(check02 == true){
   	    	nodeAttr.inputType = 1;
   	    }else{
   	    	nodeAttr.inputType = "";
   	    }
   });
    // 발화 최대 반복 수정
    $("#max_turn_cnt").on("keyup", function(){
    	var nodeAttr = changeNodeData['attr'][0];

    	nodeAttr.maxTurn = $("#max_turn_cnt").val();

    });
    // 발화 최대 반복 초과 시 실행될 태스크 수정
    $("#task_over_max").on("keyup", function(){
    	var nodeAttr = changeNodeData['attr'][0];

    	nodeAttr.taskOverMax = $("#task_over_max").val();

    });

    $('#add_entity').on('click', function(){
        var temp = $($('#temp_entity').html())[0];
        var added_area = $('.chatbot_answer .intent');
        added_area.append(temp);
        popupOpenEventListener();
        handleIntentionUserContent();
        makeAllPreview();
//         added_area.scrollTop(added_area[0].scrollHeight);
    });
  });
// 무시/발화 구간 click시 문장별 구간 설정
function utterRange(el, idx){
	var nodeAttr = changeNodeData['attr'][0];

	if($(el).parent().attr("class") == "textarea ignore"){
		//발화 무시구간 설정
		if($(el).attr("class") == "on"){
			$(el).removeClass();
			ignoreIdxArr.splice(ignoreIdxArr.indexOf(idx.toString()),1);
		}else{
			$(el).addClass("on");
			ignoreIdxArr.push(idx);
		}
		var ignoreStr = "";

		$.each(ignoreIdxArr, function(i,v){
			if(i == 0){
				ignoreStr = v;
			}else{
				ignoreStr = ignoreStr + "," + v;
			}
		});

		nodeAttr.acceptSttStcIdx = ignoreStr.toString();

	}else if($(el).parent().attr("class") == "textarea repeat"){
		//반복구간 설정
		if($(el).attr("class") == "on"){
			$(el).removeClass();
			repeatIdxArr.splice(repeatIdxArr.indexOf(idx.toString()),1);
		}else{
			$(el).addClass("on");
			repeatIdxArr.push(idx);
		}
		var repeatStr = "";

		$.each(repeatIdxArr, function(i,v){
			if(i == 0){
				repeatStr = v;
			}else{
				repeatStr = repeatStr + "," + v;
			}
		});

		nodeAttr.repeatAnswerStcIdx = repeatStr.toString();
	}
}
// 무시/반복 발화구간 문장별 hover 기능
function hoverUtter(el){
	$(el).css("background","#DDDADA");
}
function outUtter(el){
	$(el).css("background","");
}

function makeAllPreview(){
  let music = document.getElementById('audio');
  music.pause();
  var newButtonToSpeak = "<div class='desc_audio'><div class='speakButton' type='button'></div></div>";
  var letfButtonToSpeak = "<div class='desc_audio'><div class='speakButton' type='button'></div></div>";

  $(".desc_audio").remove();

  $("#first_say").after(letfButtonToSpeak);
  $(".chatbot_answer").find('.description').each(
      function(){
          $(this).after(newButtonToSpeak);
      }
  )
  $(".speakButton").each(
          function(){
            $(this).click(
                    function(){
                      if(!($(this).hasClass("disButton")||$(this).hasClass("hlButton"))){
                        let nowText = $(this).parents('.user_content').find('.textarea').text().toString().trim().replace("\n"," ");
                        console.log("TTSGet : " + nowText);
                        nowText.replace(/\&/g,"%26");
                        if(nowText.length>0){
                          let src = "ttsGet?nowText=" + nowText;
                          src = encodeURI(src);
                          try{
                            $("#audio").attr("src",src);
                          }catch (e) {
                            console.log(e);
                          }
                          $("#audio").off('ended');
                          $("#audio").on('ended', function(){
                            $(".speakButton").removeClass("disButton");
                            $(".speakButton").removeClass("hlButton");
                          });
                          let music = document.getElementById('audio');
                          $(".speakButton").addClass("disButton");
                          $(this).addClass("hlButton");
                          $(this).removeClass("disButton");
                          music.play();
                        }
                      }else if($(this).hasClass("hlButton")){
                        makeAllPreview();
                      }
                    }
            )
          }
  )

}makeAllPreview();

function delNewLine(origText){
  return origText.replace(/\n/g," ");
}


  function replaceDictList(host, cp){
  	var obj = new Object();
  	obj.host = host;
  	obj.lang = lang;
  	obj.cp = cp;
  	obj.orderCol = $("#orderCol").val();
  	obj.orderSort = $("#orderSort").val();
  	obj.searchReplaceDict = $("input[name='searchReplaceDict']").val();

  	$.ajax({
  	    url: "voiceBot/getReplaceDictList",
  	    data: JSON.stringify(obj),
  	    type: "POST",
  	    contentType: 'application/json'
  	  }).done(function (response) {
  		  var replaceDictList = response.replaceDictList;
  		  var paging = response.paging;

  		  $(".pagination").empty();
  		  var pagingHtml = "";

  		  pagingHtml += '<button type="button" class="first" onclick="goPage('+ host + ',1)"><span>&laquo;</span></button>';
  		  pagingHtml += '<button type="button" class="prev" onclick="goPage('+ host + ',' + paging.prevPage +')"><span>&lsaquo;</span></button>';
  		  pagingHtml += '<div class="pages">';

  		  for (var i = paging.pageRangeStart; i<=paging.pageRangeEnd; i++){
                if(i == paging.currentPage){
              	  pagingHtml += '<span class="page active">'+ i +'</span>';
                }else{
              	  pagingHtml += '<span class="page" onclick="goPage('+ host + ',' + i +')">'+ i +'</span>';
                }
            }

  		  pagingHtml += '</div>';
  		  pagingHtml += '<button type="button" class="next" onclick="goPage('+ host + ',' + paging.nextPage +')"><span>&rsaquo;</span></button>';
  		  pagingHtml += '<button type="button" class="last" onclick="goPage('+ host + ',' + paging.totalPage +')"><span>&raquo;</span></button>';
  		  pagingHtml += '</div>';

  		  $(".pagination").append(pagingHtml);

  		  $("#replaceDictTable tbody").empty();

  		  var replaceDictHtml = "";

  		  if(replaceDictList.length > 0){
  			  for(var i = 0; i < replaceDictList.length; i++){
  				  replaceDictHtml += "<tr>";
  				  replaceDictHtml += "<td>"+replaceDictList[i].RNUM+"</td>";
  				  replaceDictHtml += "<td><input type='text' class='ipt_txt replaceBeforeTxt' value=\""+replaceDictList[i].Before+"\"></td>";
  				  replaceDictHtml += "<td><input type='text' class='ipt_txt replaceAfterTxt' value=\""+replaceDictList[i].After+"\"></td>";
  				  replaceDictHtml += "<td><button type='button' class='btn_line_primary' onclick='updateReplaceDict(\""+replaceDictList[i].No+"\", this)'>수정</button></td>";
  				  replaceDictHtml += "<td><button type='button' class='btn_line_warning' onclick='deleteReplaceDict(\""+replaceDictList[i].No+"\")'>삭제</button></td>";
  				  replaceDictHtml += "</tr>";
  			  }
  		  }else{
  			  replaceDictHtml += "<tr>";
  			  replaceDictHtml += "<td colspan='5'>등록 된 데이터가 없습니다.</td>";
  			  replaceDictHtml += "</tr>";
  		  }

  		  $("#replaceDictTable tbody").append(replaceDictHtml);

  	  }).fail(function () {
  	    console.log('[getReplaceDictList error]');
  	  });
  }

  function addReplaceDict(){
  	var beforeTxt = $("#addBeforeTxt").val();
  	var afterTxt = $("#addAfterTxt").val();

  	if(beforeTxt == null || beforeTxt == ""){
  		alert("추가 할 치환 전 텍스트를 입력해주세요.");
  		return false;
  	}
  	if(afterTxt == null || afterTxt == ""){
  		alert("추가 할 치환 후 텍스트를 입력해주세요.");
  		return false;
  	}

  	var obj = new Object();

  	obj.host = host;
  	obj.lang = lang;
  	obj.replaceDictBefore = beforeTxt;
  	obj.replaceDictAfter = afterTxt;

  	$.ajax({
  		url: "voiceBot/addReplaceDict",
  		data: JSON.stringify(obj),
  		type: "POST",
  		contentType: 'application/json'
      }).then(function (response) {
  	    if(response == 1){
  	    	alert("치환사전이 추가 되었습니다.");
  	    	$(".sort").removeClass('up down');
  	    	$(".sort").removeClass('down');
  	    	$("#orderCol").val("");
  	    	$("#orderSort").val("");
  	    	$("#addBeforeTxt").val("");
  	    	$("#addAfterTxt").val("");
  		    replaceDictList(host, 1);
  	    }

      }).catch(function () {
        console.log('[addReplaceDict error]');
      });
  }

  function updateReplaceDict(No, el){
  	var cp = $("#currentPage").val();
  	var beforeTxt = $(el).parent().parent().find('.replaceBeforeTxt').val();
  	var afterTxt = $(el).parent().parent().find('.replaceAfterTxt').val();

  	if(beforeTxt == null || beforeTxt == ""){
  		alert("수정 할 치환 전 텍스트를 입력해주세요.");
  		return false;
  	}

  	if(afterTxt == null || afterTxt == ""){
  		alert("수정 할 치환 후 텍스트를 입력해주세요.");
  		return false;
  	}

  	var obj = new Object();
  	obj.no = No;
  	obj.host = host;
  	obj.lang = lang;
  	obj.replaceDictBefore = beforeTxt;
  	obj.replaceDictAfter = afterTxt;
  	mui.confirm('치환 사전 정보를 수정하시겠습니까?', {
  		onClose: function(isOk) {
  			if (isOk) {
  			  $.ajax({
  				    url: "voiceBot/updateReplaceDict",
  				    data: JSON.stringify(obj),
  				    type: "POST",
  				    contentType: 'application/json'
  				}).success(function (response) {
  					if(response == 1){
  						alert("치환사전이 수정되었습니다.");
  						$(".sort").removeClass('up down');
  						$(".sort").removeClass('down');
  						$("#orderCol").val("");
  						$("#orderSort").val("");
  						replaceDictList(host, cp);
  					}

  				}).fail(function () {
  						console.log('[updateReplaceDict error]');
  				});
  			}
  	    }
  	});
  }

  function deleteReplaceDict(No){
  	var obj = new Object();
  	obj.no = No;
  	obj.host = host;
  	obj.lang = lang;

  	mui.confirm('치환 사전을 삭제하시겠습니까?', {
  		onClose: function(isOk) {
  			if (isOk) {
  			  $.ajax({
  				    url: "voiceBot/deleteReplaceDict",
  				    data: JSON.stringify(obj),
  				    type: "POST",
  				    contentType: 'application/json'
  				}).success(function (response) {
  					if(response == 1){
  						alert("치환사전이 삭제되었습니다.");
  						$(".sort").removeClass('up down');
  						$(".sort").removeClass('down');
  						$("#orderCol").val("");
  						$("#orderSort").val("");
  						replaceDictList(host, 1);
  					}

  				}).fail(function () {
  						console.log('[updateReplaceDict error]');
  				});
  	        }
  	    }
  	});
  }

  function searchReplaceDict(){
  	$(".sort").removeClass('up down');
  	$(".sort").removeClass('down');
  	$("#orderCol").val("");
  	$("#orderSort").val("");
  	replaceDictList(host, 1);
  }

  //엑셀 다운로드
  function excelDown() {
    var form = document.downloadReplaceDict;
    // scenario = getScenarioJsonData(); //jsplumb에서 가져옴
    $("#replaceDictCol").val($("#orderCol").val());
    $("#replaceDictSort").val($("#orderSort").val());
    $("#replaceDict_host").val(host);
    $("#replaceDict_lang").val(lang);
    form.submit();
  }

  function uploadExcel(){
  	  $("#replaceDictHost").val(host);
  	  $("#replaceDictLang").val(lang);

  	  var txt = '엑셀 저장';
  	  var changeTxt = '저장중';
  	  var file = $("#excelFile").val();
  	  if (file === "" || file === null) {
  	      mui.alert('파일을 선택 해주세요.');
  	  }

  	  $('#btn_upload').text(changeTxt);
  	  var options = {
  	      success: function (response) {
  	          $('#orderCol').val("");
  	          $('#orderSort').val("");
  	          $('#btn_upload').text(txt);
  	          $('#excel_file_replace').val('');
  	          alert("업로드가 완료되었습니다.");
  	          replaceDictList(host, 1);

  	      }, error: function (response) {
  	          $('#btn_upload').text(txt);
  	          alert("작업 중 오류가 발생했습니다.");
  	      },
  	      type: "POST"
  	  };
  	  $("#replaceDictUpload").ajaxSubmit(options);
  	}

    function goPage(host, cp){
        if( cp != 0){
            $('#currentPage').val(cp);
        }
        replaceDictList(host, cp);
    }
</script>
</body>
</html>
