<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>index</title>
</head>
<body>
    <h2>fmall</h2>
    上传文件
    <form name="upload file" action="/manage/product/upload_file.do" method="post" enctype="multipart/form-data">
        <input type="file" name="upload_file"/>
        <input type="submit" value=">>>>UPLOAD FILE">
    </form>

    富文本上传
    <form name="upload file" action="/manage/product/upload_richtext_file.do" method="post" enctype="multipart/form-data">
        <input type="file" name="upload_file"/>
        <input type="submit" value=">>>>UPLOAD RICHTEXT">
    </form>
</body>
</html>
