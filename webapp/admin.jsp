<%@page import="java.sql.*" %>
<%@page import="com.totalchange.sketchingout.*" %>
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
	
	// Check for requested things to do like delete or unlock drawings and stuff
	if (request.getParameter("del_d") != null) {
		// Delete a drawing
		pstmt = conn.prepareStatement("DELETE FROM drawings WHERE id = ?");
		pstmt.setString(1, request.getParameter("del_d"));
		pstmt.execute();
		pstmt.close();
	}
	
	if (request.getParameter("unl_d") != null) {
		// Unlock a drawing by setting the locked time to one second ago
		pstmt = conn.prepareStatement("UPDATE drawings SET locked = ? WHERE id = ?");
		pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis() - 1000));
		pstmt.setString(2, request.getParameter("unl_d"));
		pstmt.execute();
		pstmt.close();
	}
	
	if (request.getParameter("pub_d") != null) {
		// Make a private drawing public by making the distinguished ID null
		pstmt = conn.prepareStatement("UPDATE drawings SET distinguished_id = NULL WHERE id = ?");
		pstmt.setString(1, request.getParameter("pub_d"));
		pstmt.execute();
		pstmt.close();
	}
	
	if (request.getParameter("complete") != null) {
		// Follow the completion process for the drawing
		CompleteDrawingProcessor.process(request.getParameter("complete"));
	}
%>
<html>
<head>
<title>SketchingOut Admin Page</title>
</head>
<body>
<h1>SketchingOut Admin</h1>
<p><a href="admin.jsp">Refresh</a></p>
<h2>You</h2>
<form action="" method="GET">
	<label for="uid">User ID:</label>
	<input name="uid" type="text" value="<%= userID %>" size="50"></input>
	<input name="submit" type="submit" value="Change">
</form>
<%
	// Setup a loop to show drawings in various different states
	// Setup the standard SQL template
	String sqlFields = "SELECT friendly_id, id, distinguished_id, completed, width, " +
		"height, locked, stage, stage_1_author_name, stage_1_author_email, " +
		"stage_2_author_name, stage_2_author_email, stage_3_author_name, " +
		"stage_3_author_email, stage_4_author_name, stage_4_author_email FROM drawings ";
	String sqlOrder = " ORDER BY friendly_id";

	// Run through a loop that will get the same values but different where clauses
	// to show all the drawings...
	for (int num = 0; num < 4; num++) {
		String tableTitle = "";
		pstmt = null;
		
		// Decide on the values for this stage
		if (num == 0) {
			tableTitle = "Available Public Drawings";
			pstmt = conn.prepareStatement(
				sqlFields + 
				"WHERE " +
				"  completed = 'N' AND " +
				"  locked < ?" +
				sqlOrder
			);
			
			pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
		}
		else if (num == 1) {
			tableTitle = "Locked Public Drawings";
			pstmt = conn.prepareStatement(
				sqlFields + 
				"WHERE " +
				"  completed = 'N' AND " +
				"  distinguished_id IS NULL AND " +
				"  locked >= ?" +
				sqlOrder
			);
			
			pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
		}
		else if (num == 2) {
			tableTitle = "Locked Private Drawings";
			pstmt = conn.prepareStatement(
				sqlFields + 
				"WHERE " +
				"  completed = 'N' AND " +
				"  distinguished_id IS NOT NULL AND " +
				"  locked >= ?" +
				sqlOrder
			);
			
			pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
		}
		else if (num == 3) {
			tableTitle = "Complete Drawings";
			pstmt = conn.prepareStatement(
				sqlFields + 
				"WHERE " +
				"  completed = 'Y'" +
				sqlOrder
			);
		}
		
		%>
		<p>&nbsp;</p>
		<h2><%= tableTitle %></h2>
		<table border="1">
			<tr>
				<td nowrap><b>Friendly ID</b></td>
				<td nowrap><b>Actual ID</b></td>
				<td nowrap><b>Distinguished ID</b></td>
				<td nowrap><b>Complete</b></td>
				<td nowrap><b>Locked Until</b></td>
				<td nowrap><b>Width</b></td>
				<td nowrap><b>Height</b></td>
				<td nowrap><b>Stage</b></td>
				<td nowrap><b>Stage 1 Name</b></td>
				<td nowrap><b>Stage 1 Email</b></td>
				<td nowrap><b>Stage 2 Name</b></td>
				<td nowrap><b>Stage 2 Email</b></td>
				<td nowrap><b>Stage 3 Name</b></td>
				<td nowrap><b>Stage 3 Email</b></td>
				<td nowrap><b>Stage 4 Name</b></td>
				<td nowrap><b>Stage 4 Email</b></td>
			</tr>
			<%
			// Execute query
			res = pstmt.executeQuery();
			
			// Go through results
			while (res.next()) {
				%>
				<tr>
					<td nowrap><a href="drawing?type=pdf&scale=100&loss=0&id=<%= res.getInt("friendly_id") %>" target="pic"><%= res.getInt("friendly_id") %></a></td>
					<td nowrap><%= res.getString("id") %></td>
					<td nowrap><a href="?pub_d=<%= res.getString("id") %>"><%= res.getString("distinguished_id") %></a></td>
					<td nowrap><%= res.getString("completed") %></td>
					<td nowrap><a href="?unl_d=<%= res.getString("id") %>"><%= res.getTimestamp("locked") %></a></td>
					<td nowrap><%= res.getInt("width") %></td>
					<td nowrap><%= res.getInt("height") %></td>
					<td nowrap><%= res.getInt("stage") %></td>
					<td nowrap><%= res.getString("stage_1_author_name") %></td>
					<td nowrap><%= res.getString("stage_1_author_email") %></td>
					<td nowrap><%= res.getString("stage_2_author_name") %></td>
					<td nowrap><%= res.getString("stage_2_author_email") %></td>
					<td nowrap><%= res.getString("stage_3_author_name") %></td>
					<td nowrap><%= res.getString("stage_3_author_email") %></td>
					<td nowrap><%= res.getString("stage_4_author_name") %></td>
					<td nowrap><%= res.getString("stage_4_author_email") %></td>
					<td nowrap><a href="?complete=<%= res.getString("id") %>">Complete</a></td>
					<td nowrap><a href="?del_d=<%= res.getString("id") %>">Delete</a></td>
				</tr>
				<%
			}
			%>
		</table>
		<%
		// Close queries
		res.close();
		pstmt.close();
	}
	
	// Show the gallery drawings
	pstmt = conn.prepareStatement("SELECT * FROM gallery ORDER BY friendly_id");
	res = pstmt.executeQuery();
	%>
	<p>&nbsp;</p>
	<h2>Gallery</h2>
	<table border="1">
		<tr>
			<td nowrap><b>Friendly ID</b></td>
			<td nowrap><b>Thumb</b></td>
			<td nowrap><b>Anim SWF</b></td>
			<td nowrap><b>PDF</b></td>
			<td nowrap><b>Width</b></td>
			<td nowrap><b>Height</b></td>
			<td nowrap><b>Stage</b></td>
			<td nowrap><b>Stage 1 Name</b></td>
			<td nowrap><b>Stage 2 Name</b></td>
			<td nowrap><b>Stage 3 Name</b></td>
			<td nowrap><b>Stage 4 Name</b></td>
			<td nowrap><b>Actual ID</b></td>
		</tr>
		<%
		while (res.next()) {
			%>
			<tr>
				<td nowrap><%= res.getInt("friendly_id") %></td>
				<td nowrap><a href="<%= SketchingoutSettings.URL_DRAWING_STORE + res.getString("thumbnail_filename") %>" target="thumb"><%= res.getString("thumbnail_filename") %></td>
				<td nowrap><a href="<%= SketchingoutSettings.URL_DRAWING_STORE + res.getString("anim_swf_filename") %>" target="animswf"><%= res.getString("anim_swf_filename") %></td>
				<td nowrap><a href="<%= SketchingoutSettings.URL_DRAWING_STORE + res.getString("pdf_filename") %>" target="pdf"><%= res.getString("pdf_filename") %></a></td>
				<td nowrap><%= res.getInt("width") %></td>
				<td nowrap><%= res.getInt("height") %></td>
				<td nowrap><%= res.getInt("stage") %></td>
				<td nowrap><%= res.getString("stage_1_author_name") %></td>
				<td nowrap><%= res.getString("stage_2_author_name") %></td>
				<td nowrap><%= res.getString("stage_3_author_name") %></td>
				<td nowrap><%= res.getString("stage_4_author_name") %></td>
				<td nowrap><%= res.getString("id") %></td>
			</tr>
			<%
		}
		%>
	</table>
	<%
	// Close gallery query
	res.close();
	pstmt.close();
%>
<p>&nbsp;</p>
<h2>Server Settings</h2>
<table border="1">
  <tr>
    <td><b>Public Drawing Lock Time:</b></td>
    <td><%= SketchingoutSettings.DEFAULT_LOCK_SECS %> secs (<%= (double)SketchingoutSettings.DEFAULT_LOCK_SECS / 60 / 60 %>hrs)</td>
  </tr>
  <tr>
    <td><b>Private Drawing Lock Time:</b></td>
    <td><%= SketchingoutSettings.PRIVATE_LOCK_SECS %> secs (<%= (double)SketchingoutSettings.PRIVATE_LOCK_SECS / 60 / 60 %>hrs)</td>
  </tr>
  <tr>
    <td><b>Number of Stages:</b></td>
    <td><%= SketchingoutSettings.MAX_NUM_STAGES %></td>
  </tr>
  <tr>
    <td><b>SMTP Address:</b></td>
    <td><%= SketchingoutSettings.SMTP_SERVER_ADDR + ":" + SketchingoutSettings.SMTP_SERVER_PORT %></td>
  </tr>
  <tr>
  	<td><b>SketchingOut Font:</b></td>
  	<td><%= new java.io.File(SketchingoutSettings.SKETCHING_OUT_FONT).getAbsolutePath() %></td>
  </tr>
  <tr>
  	<td><b>Gallery Directory:</b></td>
  	<td><%= new java.io.File(SketchingoutSettings.FS_DRAWING_STORE).getAbsolutePath() %></td>
  </tr>
  <tr>
  	<td><b>Containers WebApp Root:</b></td>
  	<td><%= new java.io.File(getServletContext().getRealPath("./")).getAbsolutePath() %></td>
  </tr>
</table>
</body>
</html>
<%
	// Close DB connection
	conn.close();
%>