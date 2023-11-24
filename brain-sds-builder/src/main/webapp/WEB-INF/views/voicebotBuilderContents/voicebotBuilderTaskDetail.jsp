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
  <script src="${pageContext.request.contextPath}/js/voicebotBuilderContents/voicebotBuilderTaskDetail.js?v=${queryString}"></script>
  <title>음성봇빌더 || 태스크상세</title>
</head>
<body>
<div>
  <span class="content_title">${title}</span>
</div>
<%-- task_detail_container --%>
<div id="task_detail_container" class="dl_container">
  <dl>
    <dt>태스크 명</dt>
    <dd>
      <input type="text" class="ipt_txt" id="taskName">
    </dd>
  </dl>
  <dl>
    <dt>태스크 그룹 <a class="btn_secondary small" data-modal="set_task_group" onclick="handleModal()">TASK 그룹 관리</a></dt>
    <dd>
      <input type="text" id="task_group_depth1" class="select" readOnly>
    </dd>
  </dl>
  <dl>
    <dt>답변</dt>
    <dd>
      <textarea name="task_answer" id="taskAnswer" class="ipt_txt" cols="30" rows="2"></textarea>
    </dd>
  </dl>
  <dl>
    <dt>성공 실패 여부
      <div class="help">?
        <div class="help_desc" style="width: 280px;">
          <i>
            task 별 성공, 실패 정보를 통해 시나리오를 성공으로 분류할지, 실패로 분류할지에 대한 지표로 사용합니다.<br><br>
            *공통 태스크(*)는 성공, 실패가 없습니다.
          </i>
        </div>
      </div>
    </dt>
    <dd>
      <input type="radio" name="checkSuccessYn" id="checkSuccess" class="radio" value="Y">
      <label for="checkSuccess">성공</label>
      <input type="radio" name="checkSuccessYn" id="checkFail" class="radio" value="N">
      <label for="checkFail">실패</label>
    </dd>
  </dl>
  <dl>
    <dt>다음 TASK</dt>
    <dd>
      <div class="table_wrap">
        <div class="float_box tbl_command_box">
          <button type="button" class="btn_line_secondary" onclick="deleteTaskRelation();">선택 삭제</button>
          <button type="button" class="btn_secondary fr" onclick="addTaskRelation();">TASK 관계 추가</button>
        </div>

        <div id="table_task_relationship">
          <div class="next_task_answer">
    		<span>클릭한태스크명</span>
    		<span> : </span>
    		<span>다음 태스크 답변입니다. 다음 태스크 답변입니다. 다음 태스크 답변입니다. 다음 태스크 답변입니다. 다음 태스크 답변입니다. 다음 태스크 답변입니다. 다음 태스크 답변입니다. 다음 태스크 답변입니다. 다음 태스크 답변입니다. 다음 태스크 답변입니다. 다음 태스크 답변입니다. 다음 태스크 답변입니다. 다음 태스크 답변입니다. 다음 태스크 답변입니다</span>
  		  </div>	
          <div class="table_frozen_head">
            <table class="tbl_common">
              <colgroup>
                <col style="width: 40px;">
                <col style="width: 200px;">
                <col style="width: 230px;">
                <col style="width: 140px;">
                <col>
              </colgroup>
              <thead>
              <tr>
                <th class="text_hide">체크박스</th>
                <th>의도</th>
                <th>다음 태스크</th>
                <th>정보/비고</th>
                <th>태스크 이동 시 답변</th>
              </tr>
              </thead>
            </table>
          </div>
          <div class="table_body scroll">
            <table class="tbl_common" id="nextTaskTable">
              <colgroup>
                <col style="width: 40px;">
                <col style="width: 200px;">
                <col style="width: 230px;">
                <col style="width: 140px;">
                <col>
              </colgroup>
              <tbody>
              <tr>
                <td>
                  <input type="checkbox" id="checkbox01" class="checkbox">
                  <label for="checkbox01"></label>
                </td>
                <td>
                  <input type="text" id="test_intent" class="select" readOnly>
                </td>
                <td>
                  <input type="text" id="test_prev_task" class="select" readOnly>
                </td>
                <td><input type="text" class="ipt_txt"></td>
                <td><input type="text" class="ipt_txt"></td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </dd>
  </dl>
</div>
<%-- //task_detail_container --%>

<div class="btn_box">
  <button type="button" id="saveBtn" class="btn_primary" onclick="updateTask();">저장</button>
</div>

<%--모달--%>
<div class="vb_modal" id="set_task_group">
  <div class="vb_modal_dialog">
    <div class="dlg_header">
      <span class="title">TASK 그룹 추가/설정</span>
      <button type="button" class="btn_modal_close">닫기</button>
    </div>
    <div class="dlg">
      <div class="dl_container">
        <dl>
          <dt>그룹 추가</dt>
          <dd>
            <select name="selectGroupDepth" id="selectGroupDepth" class="select left_side">
              <option value="default">선택안함</option>
              <option value="1depth">1depth</option>
              <option value="2depth">2depth</option>
              <option value="3depth">3depth</option>
            </select>
            <input type="text" class="ipt_txt right_side" id="addGroupName">
            <div class="inline_box">
              <button type="button" class="btn_primary small" onclick="addTaskGroup();">추가</button>
            </div>
          </dd>
          <dt>그룹 관리</dt>
          <dd>
            <select name="selectGroupDepthModify" id="selectGroupDepthModify" class="select left_side" onchange="changeGroupDepth();">
              <option value="default">선택안함</option>
              <option value="1depth">1depth</option>
              <option value="2depth">2depth</option>
              <option value="3depth">3depth</option>
            </select>
            <select name="selectDepthGroup" id="selectDepthGroup" class="select right_side" disabled>
              <option value="default">depth를 선택해주세요</option>
              <option value="group01">group01</option>
              <option value="group02">group02</option>
              <option value="group03">group03</option>
            </select>
            <div>
              <label for="" class="left_side">수정할 이름</label>
              <input type="text" class="ipt_txt right_side" id="updateGroupName">
            </div>
            <div class="inline_box">
              <button type="button" class="btn_primary small" onclick="updateGroupName();">수정</button>
              <button type="button" class="btn_secondary small" onclick="deleteGroup();">삭제</button>
            </div>
          </dd>
        </dl>
      </div>
    </div>
  </div>
</div>
</body>
</html>
