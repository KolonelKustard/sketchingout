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
		URL url = new URL(request.getParameter("sendto"));
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
  <p>
    Enter XML in the text box below and click Submit to send data to consquences
    and get xml response back.
  </p>
  <form method="POST" target="testresult">
    <table>
      <tr><td>Submit to:</td><td><input type="text" name="sendto" size="60" value="http://localhost:8080/consequences/consequences" /></td></tr>
      <tr><td>XML:</td><td><input type="textarea" name="xmlin" size="60" /></td></tr>
    </table>
    <input type="submit" />
  </form>
</body>
</html>
<%
	}
%>