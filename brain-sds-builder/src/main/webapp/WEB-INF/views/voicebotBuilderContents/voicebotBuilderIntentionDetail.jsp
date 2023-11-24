<%--
  Created by IntelliJ IDEA.
  User: mindslab
  Date: 2021-05-17
  Time: 오전 11:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <script src="${pageContext.request.contextPath}/js/voicebotBuilderContents/voicebotBuilderIntentionDetail.js?v=${queryString}"></script>
  <title>음성봇빌더 || 의도상세</title>
</head>
<body>
<%--
  의도 추가/설정 페이지는 모든 화면에서 접근이 가능합니다.
  모든 화면 : 시나리오, 의도, TASK
--%>
<div>
  <span class="content_title">${title}</span>
</div>

<div id="intent_detail_container" class="dl_container">
  <dl>
    <dt>의도 명</dt>
    <dd>
      <div class="ipt_box">
        <input type="text" name="intent_name" class="ipt_txt">
      </div>
    </dd>
  </dl>
  <%-- [D] dl_dropdown에 "active" class가 추가되면 dd가 보여집니다 --%>
  <dl class="dl_dropdown name">
    <dt>
      TASK 연결
      <div class="help">?
        <div class="help_desc" style="width: 350px;">
        - 태스크와 의도를 연결하는 작업입니다.<br>
        - 현재 의도를 선택한 태스크와 연결합니다.<br>
        - 전체 태스크에 작동하게 하려면 테이블 제목줄에 있는 체크박스에 체크를 해주세요.<br>
        - 테스크관계 테이블 : 태스크 열에 있는 태스크가 현재 의도를 타면<br> 다음 태스크 열에 설정한 태스크가 실행됩니다.
        </div>
      </div>

      <div class="btn_control">
        <div class="fold">
          <svg width="15" height="8" viewBox="0 0 15 8" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M0 1.63966L1.20363 0.0423927L8.39135 5.45873L7.18772 7.056L0 1.63966Z" fill="#97A1AB"></path>
            <path d="M13.2154 0L14.4191 1.59727L7.23133 7.01361L6.0277 5.41634L13.2154 0Z" fill="#97A1AB"></path>
          </svg>
        </div>
        <div class="unfold">
          <svg width="15" height="8" viewBox="0 0 15 8" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M14.4191 5.41634L13.2154 7.01361L6.02772 1.59727L7.23135 9.53674e-07L14.4191 5.41634Z" fill="#97A1AB"></path>
            <path d="M1.20364 7.056L1.33514e-05 5.45873L7.18773 0.0423937L8.39136 1.63966L1.20364 7.056Z" fill="#97A1AB"></path>
          </svg>
        </div>
      </div>
    </dt>
    <dd>
      <p id="intent_seleted_task" class="ipt_txt">선택한 TASK 나열, 선택한 TASK 나열, 선택한 TASK 나열, 선택한 TASK 나열, 선택한 TASK 나열, 선택한 TASK 나열, 선택한 TASK 나열, 선택한 TASK 나열, 선택한 TASK 나열</p>
      <input type="hidden" name="intent_seleted_task" value="선택한 TASK 나열" readonly>
      <div class="float_box tbl_command_box">
        <input type="text" name="intent_task_search" class="ipt_txt search fr" placeholder="TASK 검색">
        <button type="button" class="btn_search">검색하기</button>
      </div>
      <%-- table_wrap --%>
      <div id="list_intent_task" class="table_wrap">
        <%-- table --%>
        <table class="tbl_common">
          <colgroup>
            <col style="width: 40px;">
            <col style="width: 50%;">
            <col style="width: 200px;">
            <col style="width: 50%;">
          </colgroup>
          <thead>
          <tr>
            <th>
              <input type="checkbox" name="intent_task_all" class="checkbox" id="list_all">
              <label for="list_all"></label>
            </th>
            <th>태스크</th>
            <th>의도</th>
            <th>다음 태스크</th>
          </tr>
          </thead>
          <tbody>
          <tr>
            <td colspan="5" class="no_list">등록된 의도가 없습니다.</td>
          </tr>
          <tr>
            <td>
              <input type="checkbox" name="intent_task" class="checkbox" id="list01">
              <label for="list01"></label>
            </td>
            <td>
              <a href="#none">리스트</a>
            </td>
            <td>의도명</td>
            <td>task select</td>
          </tr>
          <tr>
            <td>
              <input type="checkbox" name="intent_task" class="checkbox" id="list02">
              <label for="list02"></label>
            </td>
            <td>
              <a href="#none">리스트</a>
            </td>
            <td>의도명</td>
            <td>task select</td>
          </tr>
          <tr>
            <td>
              <input type="checkbox" name="intent_task" class="checkbox" id="list03">
              <label for="list03"></label>
            </td>
            <td>
              <a href="#none">리스트</a>
            </td>
            <td>의도명</td>
            <td>task select</td>
          </tr>
          <tr>
            <td>
              <input type="checkbox" name="intent_task" class="checkbox" id="list04">
              <label for="list04"></label>
            </td>
            <td>
              <a href="#none">리스트</a>
            </td>
            <td>의도명</td>
            <td>task select</td>
          </tr>
          </tbody>
        </table>
      </div>
      <button type="button" class="btn_secondary small btn_reset" onclick="resetTempIntentTaskRel();">적용한 내용 초기화</button>
    </dd>
  </dl>

  <dl class="dl_dropdown regex">
    <dt>
      정규식
      <div class="btn_control">
        <div class="fold">
          <svg width="15" height="8" viewBox="0 0 15 8" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M0 1.63966L1.20363 0.0423927L8.39135 5.45873L7.18772 7.056L0 1.63966Z" fill="#97A1AB"></path>
            <path d="M13.2154 0L14.4191 1.59727L7.23133 7.01361L6.0277 5.41634L13.2154 0Z" fill="#97A1AB"></path>
          </svg>
        </div>
        <div class="unfold">
          <svg width="15" height="8" viewBox="0 0 15 8" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M14.4191 5.41634L13.2154 7.01361L6.02772 1.59727L7.23135 9.53674e-07L14.4191 5.41634Z" fill="#97A1AB"></path>
            <path d="M1.20364 7.056L1.33514e-05 5.45873L7.18773 0.0423937L8.39136 1.63966L1.20364 7.056Z" fill="#97A1AB"></path>
          </svg>
        </div>
      </div>
    </dt>
    <dd>
      <div class="dd_col">
        <div class="float_box tbl_command_box">
          <button type="button" class="btn_line_secondary fl" onclick="applyDeleteRegex()">선택 삭제</button>
          <input type="text" name="intent_regex_search" class="ipt_txt search fr" placeholder="정규식 검색">
          <button type="button" class="btn_search">검색하기</button>
        </div>
        <div id="list_intent_regex" class="table_wrap">
          <table class="tbl_common">
            <colgroup>
              <col style="width: 40px;"><col>
            </colgroup>
            <thead>
              <tr>
                <th></th>
                <th>정규식</th>
              </tr>
            </thead>
            <tbody>
            <tr>
              <td class="no_list" colspan="2">등록된 데이터가 없습니다</td>
            </tr>
            </tbody>
          </table>
        </div>
        <button type="button" class="btn_secondary small btn_reset" onclick="resetRegexTable()">적용한 내용 초기화</button>
      </div>

      <div class="dd_col">
        <div>
          <span class="col_title">정규식 추가/수정
            <div class="help">?
              <div class="help_desc" style="width: 400px; line-height: 20px;">
                * 정규식 규칙과 문장(텍스트)의 관계<br>
                1. "시작 텍스트" 는 처음에 위치하고, 문장(텍스트)의 처음을 검사합니다.<br>
                2. "종료 텍스트" 는 마지막에 위치하고, 문장(텍스트)의 마지막을 검사합니다.<br>
                3. "텍스트에 포함" 은 여러개를 입력할 수 있으며 시작과 종료 사이에 위치하고, 전체 문장(텍스트)에 규칙이 있는지를 검사합니다.<br>
                4. "텍스트에 포함하지 않음" 은 시작보다 앞에 위치하고, 전체 문장(텍스트)에 규칙이 있는지를 검사합니다.<br>
                5. "텍스트가 정확하게 일치함" 은 단독으로만 사용이 가능하고, 전체 문장(텍스트)이 규칙과 일치하는지를 검사합니다.<br><br>
                &#8251; 정규식 추가 시 결과 정규식 기준으로 저장이 됩니다.<br><br>
                - 규칙은 위 규칙을 지키며, 입력한 순서를 따릅니다.<br>
                - 규칙추가 리스트 사용 또는 정규식 직접입력 중 한가지 방법만 사용할 수 있습니다.<br>
                - 리스트를 잡고 위,아래로 이동하면 규칙의 순서를 바꿀 수 있습니다.<br>
                - "또는" 규칙 사용법 : shift + \(엔터 위 원화 키) 를 누르면 나오는 키 "|" 를 단어와 단어 사이에 사용합니다.<br>
                ex) 또는 규칙 사용 : 문장(텍스트)에 커피 또는 원두가 포함 되었는지 검사
                <br>
                <div class="image">
                  <img src="${pageContext.request.contextPath}/images/help_img_regex_or.png" alt="정규식 또는 규칙 사용">
                </div>
                ex) 또는 규칙 미사용 : 문장(텍스트)에 커피와 원두가 모두 포함 되었는지 검사<br>
                <div class="image">
                  <img src="${pageContext.request.contextPath}/images/help_img_regex.png" alt="정규식 또는 규칙 미사용">
                </div>
              </div>
            </div>
          </span>
          <button type="button" id="btn_add_regex" class="fr btn_secondary small" onclick="addRegexRuleDetail()">규칙 추가</button>
        </div>

        <template id="tempRegexList">
          <li>
            <select name="regex_type" class="select" onchange="changeRegexRuleSelect(this)">
              <option value="start">시작 텍스트</option>
              <option value="match">텍스트가 정확하게 일치함</option>
              <option value="include" selected>텍스트에 포함</option>
              <option value="exclude">텍스트에 포함하지 않음</option>
              <option value="end">종료 텍스트</option>
            </select>
            <input type="text" class="ipt_txt regex_type include" value="" oninput="makeRegexRuleResult()">
            <button class="btn_regex_delete" onclick="removeRegexRuleDetail()">삭제하기</button>
          </li>
        </template>
        <ul id="regex_list" class="drag_lst common_lst scroll">
          <li>
            <select name="regex_type" class="select" onchange="changeRegexRuleSelect(this)">
              <option value="start" selected>시작 텍스트</option>
              <option value="match">텍스트가 정확하게 일치함</option>
              <option value="include" selected>텍스트에 포함</option>
              <option value="exclude">텍스트에 포함하지 않음</option>
              <option value="end">종료 텍스트</option>
            </select>
            <input type="text" class="ipt_txt regex_type include" oninput="makeRegexRuleResult()">
            <button class="btn_regex_delete" onclick="removeRegexRuleDetail()">삭제하기</button>
          </li>
        </ul>
        <div class="regex_check float_box">
          <div class="ipt_box regex_direct active">
            <label>정규식 직접입력</label>
            <input type="checkbox" name="regex_rule_direct" id="regex_rule_direct" class="checkbox" onchange="setRegexRuleDirect(this.checked)">
            <label for="regex_rule_direct"></label>
          </div>

          <div class="view regex_direct">
            <span>정규식 직접입력</span>
            <div class="checked"></div>
          </div>

          <div class="ipt_box">
            <label for="regex_rule">결과 정규식</label>
            <textarea id="regex_rule" class="ipt_txt" oninput="writeDirectRegexRule()" readonly></textarea>
          </div>

          <div class="ipt_box">
            <label for="regex_test">테스트 문장</label>
            <input type="text" id="regex_test" class="ipt_txt" onkeypress="enterCheckRegexTest(event.keyCode)" >
          </div>

          <button type="button" id="btn_regex_test" class="fr btn_line_secondary" onclick="testRegexRule()">테스트</button>
        </div>

        <div id="regex_test_result"></div>
        <%-- <div id="regex_test_result" class="match">MATCH</div> --%>
        <%-- <div id="regex_test_result" class="unmatch">UNMATCH</div> --%>

        <div class="btn_box regex_add">
          <button type="button" class="btn_secondary small" onclick="applyInsertRegex()">추가</button>
        </div>
        <div class="btn_box regex_modify">
          <button type="button" class="btn_secondary small" onclick="applyInsertRegex()">수정</button>
          <button type="button" class="btn_secondary small" onclick="initRegexDetailList()">취소</button>
        </div>
      </div>
    </dd>
  </dl>

  <dl class="dl_dropdown nqa">
    <dt id="intentNqaDlDropdown">
      학습문장
      <div class="btn_control">
        <div class="fold">
          <svg width="15" height="8" viewBox="0 0 15 8" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M0 1.63966L1.20363 0.0423927L8.39135 5.45873L7.18772 7.056L0 1.63966Z" fill="#97A1AB"></path>
            <path d="M13.2154 0L14.4191 1.59727L7.23133 7.01361L6.0277 5.41634L13.2154 0Z" fill="#97A1AB"></path>
          </svg>
        </div>
        <div class="unfold">
          <svg width="15" height="8" viewBox="0 0 15 8" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M14.4191 5.41634L13.2154 7.01361L6.02772 1.59727L7.23135 9.53674e-07L14.4191 5.41634Z" fill="#97A1AB"></path>
            <path d="M1.20364 7.056L1.33514e-05 5.45873L7.18773 0.0423937L8.39136 1.63966L1.20364 7.056Z" fill="#97A1AB"></path>
          </svg>
        </div>
      </div>
    </dt>
    <dd>
      <div class="dd_col">
        <div class="float_box tbl_command_box">
          <button type="button" class="btn_line_secondary" onclick="deleteNqaStudyStc()">선택 삭제</button>
          <input type="text" id="intent_nqa_search" name="intent_nqa_search" class="ipt_txt search fr" placeholder="학습문장 검색" style="width:200px;">
          <button type="button" class="btn_search">검색하기</button>
        </div>

        <div id="nqaStcTbl" class="table_wrap">
          <table class="tbl_common">
            <colgroup>
              <col style="width: 40px;"><col>
            </colgroup>
            <thead>
            <tr>
              <th></th>
              <th>학습문장</th>
            </tr>
            </thead>
            <tbody>
            <tr>
              <td colspan="2">등록된 데이터가 없습니다.</td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="dd_col">
        <span class="col_title">학습문장 추가/수정</span>
        <input type="hidden" id="intent_npa_id" />
        <textarea name="intent_npa" id="intent_npa" class="ipt_txt" onkeydown="pressEnterNqa(event.keyCode)"></textarea>

        <div id="afterAddIntentInfo" class="info_box">
          <p class="info_small warning">&#8251; 학습문장은 파란색 저장버튼과는 별개로 작동합니다.</p>
          <p class="info_small warning">&#8251; 추가, 수정 시 데이터가 바로 저장되므로, 초기화 기능을 이용할 수 없습니다.</p>
        </div>
        <div id="beforeAddIntentInfo" class="info_box">
          <p class="info_small warning">&#8251; 학습문장은 의도를 저장 한 후 추가할 수 있습니다.</p>
        </div>

        <div class="btn_box"id="insertNqaBtn">
          <!-- insert -->
          <button type="button" class="btn_secondary small" onclick="insertNqaStc()">추가</button>
        </div>
        <div class="btn_box" id="updateNqaBtn" style="display:none;">
          <!-- update -->
          <button type="button" class="btn_secondary small" onclick="updateNqaStc()">수정</button>
          <button type="button" class="btn_secondary small" onclick="modNqaStc('insert')">취소</button>
        </div>
      </div>
    </dd>
  </dl>
</div>

<div class="btn_box">
  <button type="button" class="btn_primary" onclick="saveIntent()">저장</button>
  <button type="button" class="btn_primary bubble_box" data-modal="learning_nqa" onclick="handleModal()">학습하기
    <span id="alert_nqa_train_start" class="bubble nqa">학습해야 할 데이터가 있습니다.</span>
  </button>
</div>

<%--모달--%>
<div class="vb_modal" id="upload_excel">
  <div class="vb_modal_dialog">
    <div class="dlg_header">
      <span class="title">엑셀 업로드</span>
      <button type="button" class="btn_modal_close">닫기</button>
    </div>
    <div class="dlg">
      <div class="ipt_file_box">
        <input type="text" name="upload_excel_file" class="ipt_txt" placeholder="선택된 파일 없음" disabled>
        <input type="file" id="upload_excel_file" class="ipt_file">
        <span class="file_label">
            <label for="upload_excel_file">찾아보기..</label>
          </span>
      </div>
      <div class="info_box">
        <p class="info_small primary">* 파일 업로드 시 기존 데이터가 덮어쓰기 됩니다.</p>
        <p class="info_small primary">* 업로드 전 기존 데이터를 다운로드 받으시길 권장합니다.</p>
      </div>
    </div>
    <div class="dlg_footer">
      <button type="button" class="btn_primary btn_modal_close">확인</button>
      <button type="button" class="btn_secondary btn_modal_close">취소</button>
    </div>
  </div>
</div>
</body>
</html>
