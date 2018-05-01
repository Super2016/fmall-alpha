<%--
  Created by IntelliJ IDEA.
  User: X550V
  Date: 2018/5/1
  Time: 10:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>login</title>
</head>
<body>
    <form name="login" method="post" action="/manage/user/login.do">
        <input type="text" value="username" name="username"/>
        <input type="text" value="password" name="password"/>
        <input type="submit" value="login">
    </form>
</body>
</html>
