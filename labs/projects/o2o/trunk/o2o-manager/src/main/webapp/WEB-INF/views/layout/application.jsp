<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<%    
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1    
response.setHeader("Pragma","no-cache"); //HTTP 1.0    
response.setDateHeader ("Expires", 0); //prevents caching at the proxy server    
%>
<!DOCTYPE html>
<html lang="zh">
	<head>
	    <meta charset="utf-8">
	    <title>
			<layout:define name="title">欢迎使用市民卡运营支撑系统</layout:define>
	    </title>
	    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	    <meta name="description" content="">
	    <meta name="author" content="">
		<script type="text/javascript" src="${siteConfig.jsPath}/jquery/jquery-1.7.2.min.js"></script>
		
	  <import:css path="/bootstrap/css/bootstrap" min="true" dev="${siteConfig.dev}" />
	  <import:css path="/bootstrap/css/bootstrap-responsive" min="true" dev="${siteConfig.dev}" />
	  
	  <import:css path="/admin/assets/styles" min="false" dev="${siteConfig.dev}" />
	  
	<script type="text/javascript" src="${siteConfig.cssPath}/bootstrap/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="${siteConfig.baseURL}/fmtag/static/js/aa.js?t=${now.millis}"></script>
	<script type="text/javascript" src="${siteConfig.baseURL}/fmtag/static/js/table.js?t=${now.millis}"></script>
	<script type="text/javascript" src="${siteConfig.jsPath}/My97DatePicker/WdatePicker.js"></script>
    <layout:define name="jsscript"></layout:define>
	<layout:define name="cssscript"></layout:define>
	
	</head>
	<body>
		<div id="showTipsModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		  <div class="modal-header">
		    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
		    <h3 id="tipsTitle" class="modal-title">提示</h3>
		  </div>
		  <div class="modal-body">
		    <p>loading……</p>
		  </div>
		  <div class="modal-footer">
		    <button class="btn" data-dismiss="modal" aria-hidden="true">关闭</button>
		  </div>
		</div>
		
	    <div class="container">
	    	<c:if test="${not empty message || not empty param.message}">
			<div class="alert alert-${t:firstNotblank(messageType, param.messageType, 'block')}  alert-dismissable">
  				<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
  				<strong>提示!</strong>
				${t:escapeHtml(message)}
			  	${t:escapeHtml(param.message)}
			</div>
			<script type="text/javascript">
				$(function() {
					$(".alert").stop().fadeTo(1,0).fadeTo(2000,0.9).delay(1000).fadeTo(1000,0);
				})
			</script>
			</c:if>
			
			<layout:define name="main-content">
			页面详细内容
			</layout:define>
			<br style="clear:both; border:0px; padding:0px; margin:0px;" />
		</div>
      <script type="text/javascript">
      $.jfish.initAfterPage();
      </script>
	</body>
</html>