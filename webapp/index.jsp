<jsp:useBean id="sketchingoutBean" scope="page" class="com.totalchange.sketchingout.SketchingoutBean" />
<%
  // Make a call to initiate this bean
  sketchingoutBean.initiate(request, response);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
  <title>Sketching Out</title>
</head>
<body bgcolor="#ffffff">
<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,0,0" width="790" height="500" id="sketchingout" align="middle">
  <param name="allowScriptAccess" value="sameDomain" />
  <param name="movie" value="sketchingout.swf?<jsp:getProperty name="sketchingoutBean" property="flashParams" />&make_flash_refresh=<%= new com.totalchange.sketchingout.RandomGUID().toString() %>" />
  <param name="quality" value="high" />
  <param name="bgcolor" value="#ffffff" />
  <embed src="sketchingout.swf?<jsp:getProperty name="sketchingoutBean" property="flashParams" />&make_flash_refresh=<%= new com.totalchange.sketchingout.RandomGUID().toString() %>" quality="high" bgcolor="#ffffff" width="790" height="500" name="sketchingout" align="middle" allowScriptAccess="sameDomain" type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer" />
</object>
<table width="790">
	<tr>
	<%
		sketchingoutBean.connect();
		for (int num = 0; num < sketchingoutBean.getNumThumbnails(); num++) {
			%>
			<td><img src="<%= sketchingoutBean.getNextThumbnail() %>" /></td>
			<%
		}
		sketchingoutBean.disconnect();
	%>
	</tr>
</table>
</body>
</html>