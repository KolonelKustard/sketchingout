<%@page import="java.sql.*" %>
<%@page import="com.totalchange.consequences.*" %>
<%
	// First thing have to do is setup the user cookies.  this page lets the
	// user id be specified, so try and get it.
	String userID = request.getParameter("uid");
	
	// If the user id is null, get the user id from the cookie.
	if (userID == null) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int num = 0; num < cookies.length; num++) {
				if (cookies[num].getName().equals("consequences")) {
					userID = cookies[num].getValue();
					break;
				}
			}
		}
	}
	
	// If the user ID is still blank, make a new one
	if ((userID == null) || (userID.equals(""))) {
		userID = new RandomGUID().toString();
	}
	
	// Set the user ID into the consequences cookie.
	Cookie cookie = new Cookie("consequences", userID);
	cookie.setMaxAge(60*60*24*365);
	response.addCookie(cookie);
	
	// Make a DB connection
	Connection conn = SQLWrapper.makeConnection();
	
	// Make blank prepared statement and resultset
	PreparedStatement pstmt;
	ResultSet res;
%>
<html>
<head>
<title>Consequences Admin Page</title>
</head>
<body>
<h1>Consequences Admin</h1>
<h2>You</h2>
<p><a href="?uid=">Click here to reset yourself to someone completely new</a></p>
<%
	// Look up the current user
	pstmt = SQLWrapper.getUser(conn, userID);
	res = pstmt.executeQuery();
	
	// If got a result, get user details
	String name = "Blank User";
	String email = "(not yet submitted)";
	if (res.first()) {
		name = res.getString("name");
		email = res.getString("email");
	}
	
	// Close query
	res.close();
	pstmt.close();
%>
<table border="1">
  <tr>
    <td>User ID:</td>
    <td><%= userID %></td>
  </tr>
  <tr>
    <td>Name:</td>
    <td><%= name %></td>
  </tr>
  <tr>
    <td>Email:</td>
    <td><%= email %></td>
  </tr>
</table>
<p>&nbsp;</p>
<h2>Users</h2>
<%
	// Open a query to get all users
	pstmt = conn.prepareStatement("SELECT id, name, email FROM users");
	res = pstmt.executeQuery();
%>
<table border="1">
  <tr>
    <td><b>ID (click to assume identity)</b></td>
    <td><b>Name</b></td>
    <td><b>Email</b></td>
  </tr>
<%
	while (res.next()) {
%>
  <tr>
    <td><a href="?uid=<%= res.getString("id") %>"><%= res.getString("id") %></a></td>
    <td><%= res.getString("name") %></td>
    <td><%= res.getString("email") %></td>
  </tr>
<%
	}
%>
</table>
<%
	// Close query
	res.close();
	pstmt.close();
%>
<p>&nbsp;</p>
<h2>Private Drawings</h2>
<%
	// Open query to get all private drawings
	pstmt = conn.prepareStatement("SELECT friendly_id, id, distinguished_id, locked, " +
		"stage FROM drawings WHERE completed = 'N' AND distinguished_id IS NOT NULL AND " +
		"locked > ? ORDER BY friendly_id");
	pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
	res = pstmt.executeQuery();
%>
<table border="1">
  <tr>
    <td><b>Friendly ID</b></td>
    <td><b>Actual ID</b></td>
    <td><b>Distinguished ID (sent to recipient user)</b></td>
    <td><b>Locked Until</b></td>
    <td><b>Stage</b></td>
  </tr>
<%
	while (res.next()) {
%>
  <tr>
    <td><%= res.getInt("friendly_id") %></td>
    <td><%= res.getString("id") %></td>
    <td><%= res.getString("distinguished_id") %></td>
    <td><%= res.getTimestamp("locked") %></td>
    <td><%= res.getInt("stage") %></td>
  </tr>
<%
	}
%>
</table>
<%
	// Close query
	res.close();
	pstmt.close();
%>
<p>&nbsp;</p>
<h2>Public Drawings</h2>
<table border="1">
</table>
<p>&nbsp;</p>
<h2>Locked Drawings</h2>
<table border="1">
</table>
<p>&nbsp;</p>
<h2>Completed Drawings</h2>
<table border="1">
</table>
</body>
</html>
<%
	// Close DB connection
	conn.close();
%>