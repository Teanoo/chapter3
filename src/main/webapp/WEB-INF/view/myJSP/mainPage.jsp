<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPEHTML>
<html>
<head>
    <%@include file="../../../jspHead.jsp" %>
    <!--  分页样式  -->
    <style type="text/css">
        .page{
            display:inline-block;			/*  内联对象  */
            border: 1px solid ;			/*  1像素边框  */
            font-size: 20px;				/*  文字大小20像素 */
            width: 30px;					/*  宽度30像素  */
            height: 30px;				/*  高度30像素  */
            background-color: #1faeff;	/*  设置背景色  */
            text-align: center;			/*  居中对齐  */
        }
        a,a:hover{ text-decoration:none; color:#333}
    </style>
</head>
<body>
<table class="table table-striped">
    <tr>
        <th width="70%"> <strong> 标题：</strong> </th>
        <th width="10%"> <strong> 作者</strong> </th>
        <th width="10%"> <strong> 回复/查看</strong> </th>
        <th width="10%"> <strong> 最后发表</strong> </th>
    </tr>
    <!--  choose标签相当于Java代码当中switch case语句 -->
    <c:choose>
        <%--  when标签相当于Java当中switch case语句当中的case，属性test设置条件 --%>
        <c:when test="${not empty main }">
            <!--  forEach相当于Java代码当中的循环  -->
            <!--  属性items为要迭代元素  -->
            <!--  属性item为迭代出来的元素  -->
            <!--  属性varStatus为迭代状态  -->
            <c:forEach items="${main }" var="item" varStatus="vs">
                <tr>
                    <td>
                        <!--  该a标签指向具体帖子连接，点击打开  -->
                        <a href="<%=basePath%>secondPageContent?mainId=${item.main_id}">
                            <img src="<%=basePath %>image/pin_1.gif"
                                 id="${item.main_id}img" />
                            [日月精华]&nbsp;&nbsp;
                            <!--  获取标题  -->
                                ${item.main_title }
                        </a>
                    </td>
                    <td>
                        <!--  获取发帖人  -->
                            ${item.main_creatuser }
                    </td>
                    <td>
                        <!--  获取回复人数与查看人数  -->
                            ${item.info_reply }/${item.info_see }
                    </td>
                    <td>
                        <!--  获取最后发帖人  -->
                            ${item.info_lastuser }
                    </td>
                </tr>
            </c:forEach>
        </c:when>
    </c:choose>
</table>
<div class="row">
    <div class="col-xs-7">

    </div>
    <div class="col-xs-5 text-nowrap">
        <!--  获取分页  -->
        ${pageHtml }
    </div>
</div>

<form action="<%=basePath%>saveUeditorContent" method="post">
    <!--  label标签为input表单定义标注  -->
    <label for="biaoti"> 帖子标题：</label>
    <!--  input标签用于收集用户信息  -->
    <input type="text" id="biaoti" name="mainTitle" placeholder="最大长度80个汉字" style="width: 360px;">
    <!--  button标签放置一个按钮，type属性设置为submit用于提交表单  -->
    <button type="submit" class="btn btn-primary btn-xs text-right">
        发表帖子
    </button>
    <!--  富文本编辑器  -->
    <div style="padding: 0px;margin: 0px;width: 100%;height: 100%;">
        <script id="container" name="content" type="text/plain">

        </script>
    </div>
    <button type="submit"> 保存</button>
</form>


<!-- 配置文件 -->
<script type="text/javascript" src="<%=basePath %>uedit/js/ueditor.config.js"></script>
<!-- 编辑器源码文件 -->
<script type="text/javascript" src="<%=basePath %>uedit/js/ueditor.all.js"></script>
<!-- 实例化编辑器 -->
<script type="text/javascript">
    var editor = UE.getEditor('container');
</script>
<!-- end富文本 -->
</body>
</html>
