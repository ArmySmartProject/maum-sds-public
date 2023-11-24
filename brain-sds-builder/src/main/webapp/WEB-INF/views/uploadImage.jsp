<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <script type="text/javascript" src="<c:url value='/js/jquery-3.1.0.min.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/js/common.js?ver=9'/>"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7/jquery.js"></script>
    <script src="http://malsup.github.com/jquery.form.js"></script>
    <script type="text/javascript">

      function checkFileType(filePath) {
        var fileFormat = filePath.split(".");
        if (fileFormat.indexOf("png") > -1 || fileFormat.indexOf("jpg") > -1 ||
            fileFormat.indexOf("jpeg") > -1) {
          return true;
        } else {
          return false;
        }

      }

      function check1() {
        var file = $("#imageFile").val();
        var imgName;
        if (file == "" || file == null) {
          alert("파일을 선택해주세요.");
          return false;
        } else if (!checkFileType(file)) {
          alert("이미지 파일만 업로드 가능합니다.");
          return false;
        }

        if (confirm("업로드 하시겠습니까?")) {
          var options = {
            success: function (data) {
              imgName = data.imgName;
              alert("모든 데이터가 업로드 되었습니다.");
              $("#imgUrl").attr("value", data.hostAddr + "/sds-builder/upload/chat/img/"+imgName);

            }, error: function (data) {
              alert("작업 중 오류가 발생하였습니다.");
            },
            type: "POST"
          };
          $("#imageUploadForm").ajaxSubmit(options);

        }
      }

    </script>
</head>
<body>
<form id="imageUploadForm" name="imageUploadForm" method="post" enctype="multipart/form-data"
      action="${pageContext.request.contextPath}/upload/imageUrl">
    <div class="contents">
        <dl class="vm_name">
            <dd><input id="imageFile" type="file" name="imageFile"/></dd>
        </dl>
    </div>
    <div class="bottom">
        &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
        <button type="button" id="addImageBtn" class="btn" onclick="check1()"><span>추가</span>
        </button>
    </div>
    <div id="divImg" name="divImg">
        <input type="text" id="imgUrl" name="imgUrl" style="width: 500px">
    </div>
</form>


</body>
</html>