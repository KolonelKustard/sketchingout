<jsp:useBean id="consequencesBean" scope="page" class="com.totalchange.consequences.ConsequencesBean" />
<%
  // Make a call to initiate this bean
  consequencesBean.initiate(request, response);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
  <title>Consequences</title>
</head>
<body bgcolor="#ffffff">
<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,0,0" width="790" height="500" id="consequences" align="middle">
  <param name="allowScriptAccess" value="sameDomain" />
  <param name="movie" value="consequences.swf?<jsp:getProperty name="consequencesBean" property="flashParams" />&make_flash_refresh=<%= new com.totalchange.consequences.RandomGUID().toString() %>" />
  <param name="quality" value="high" />
  <param name="bgcolor" value="#ffffff" />
  <embed src="consequences.swf?<jsp:getProperty name="consequencesBean" property="flashParams" />&make_flash_refresh=<%= new com.totalchange.consequences.RandomGUID().toString() %>" quality="high" bgcolor="#ffffff" width="790" height="500" name="consequences" align="middle" allowScriptAccess="sameDomain" type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer" />
</object>
</body>
</html>