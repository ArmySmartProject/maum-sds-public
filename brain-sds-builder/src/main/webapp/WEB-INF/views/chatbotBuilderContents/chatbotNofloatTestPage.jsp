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
    <title>chatbot builder TestPage</title>
    <script src="${pageContext.request.contextPath}/js/jquery.js"></script>
    <script src="${pageContext.request.contextPath}/js/jquery.form.js"></script>
    <link rel="stylesheet" href="https://sds-dev.maum.ai/css?host=${hostName}&v=${queryString}"/>
</head>
<body>
<style>
    #exampleFrame{
        width:400px;
        height:400px;
    }
</style>

<iframe id="exampleFrame" src="https://sds-dev.maum.ai/nofloat/${hostName}?lang=kor"></iframe>
</body>
</html>
