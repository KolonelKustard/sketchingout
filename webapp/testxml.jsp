<%@ page import="java.io.*" %>
<%@ page import="java.net.*" %>
<%
	/*
	 * This page is a simple test bed.  If no parameters are posted to it
	 * then responds with the html below.  If there's xml sent to it then
	 * the request is submitted and dealt with.
	 */
	if (request.getParameter("xmlin") != null) {
		// Make sure aware that output is going to be xml
		response.setContentType("text/xml");
		
		// Submit submitted xml to the submitted url.
		// First get a reference to the url and open a connection...
		URL url = new URL("http://localhost:8080/consequences/consequences");
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		
		// Make sure set to POST
		urlConn.setRequestMethod("POST");
		urlConn.setDoOutput(true);
		
		// Then setup output stream
		OutputStreamWriter outStr = new OutputStreamWriter(urlConn.getOutputStream());
		
		// And output the xml
		outStr.write(request.getParameter("xmlin"));
		outStr.flush();
		outStr.close();
		
		// Now get the input stream and send it on to the output writer
		InputStreamReader inStr = new InputStreamReader(urlConn.getInputStream());
		int inByte = inStr.read();
		while (inByte > -1) {
			out.write(inByte);
			inByte = inStr.read();
		}
		inStr.close();
	}
	else {
%>
<html>
<head>
  <title>Consequences Test XML Input/Output</title>
</head>
<body>
  <form method="POST" target="testresult">
    <table>
      <tr>
        <td valign="top">XML:</td>
        <td valign="top"><textarea name="xmlin" cols="60" rows="10"></textarea></td>
        <td valign="top"><input type="submit" /></td>
      </tr>
    </table>
  </form>
</body>
</html>
<%
	}
%>