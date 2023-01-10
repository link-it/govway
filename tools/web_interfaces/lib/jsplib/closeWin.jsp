<%--
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<!DOCTYPE html>
<%@page import="org.openspcoop2.web.lib.mvc.Costanti"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%
String randomNonce = (String) request.getAttribute(Costanti.REQUEST_ATTRIBUTE_CSP_RANDOM_NONCE);
%>
<HTML>
<HEAD>
<meta charset="UTF-8">
<script type="text/javascript" src="js/jquery-latest.js" nonce="<%= randomNonce %>"></script>
<script type="text/javascript" nonce="<%= randomNonce %>">
function closeWin() {
	  window.close(); 
}

$(document).ready(function(){
	closeWin();
});
</script>
</HEAD>
<BODY>
</BODY>
</HTML>
