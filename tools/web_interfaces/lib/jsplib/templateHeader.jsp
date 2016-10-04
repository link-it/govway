<%--
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>



<%@ page session="true" import="java.util.Vector, org.openspcoop2.web.lib.mvc.*" %>

<%
String iddati = "";
String ct = request.getContentType();
if (ct != null && (ct.indexOf("multipart/form-data") != -1)) {
  iddati = (String) session.getValue("iddati");
} else {
  iddati = request.getParameter("iddati");
}
String gdString = "GeneralData";
if (iddati != null && !iddati.equals("notdefined"))
  gdString += iddati;
else
  iddati = "notdefined";
GeneralData gd = (GeneralData) session.getValue(gdString);
%>

<tr height=25>
    <td width=130 class=red>&nbsp;</td>
    <td background=images/wire.gif class=bgtopdx>
    	<% if(gd.isUsaTitleImg()) {%>
    	<img src=images/<%= gd.getTitleImg() %> height=25>
    	<% } else { %>
    		<span class="consoleTitle"><%= gd.getTitle() %></span>
    	<% } %>
    </td>
    <td width=120>&nbsp;</td>
    </tr>

    <tr>
    <td height=15 class=blue><img src=images/spacer.gif alt='' width=130 height=1></td>
    <td align=right background=images/wireblu.gif class=menu01>

<%
Vector v = gd.getHeaderLinks();
GeneralLink l;
String toAppend = "";
for (int i = 0; i < v.size(); i++) {
  if (i == v.size()-1)
    toAppend = "";
  else
    toAppend = " | ";
  l = (GeneralLink) v.elementAt(i);
  if (!l.getLabel().equals("")) {
    if (!l.getUrl().equals("")) {
      if (!l.getTarget().equals("")) {
        //url+target
	if (l.getTarget().equals("_blank")) {
          %><a class=menu01 onClick="var win = window.open('<%= l.getUrl() %>', '<%= l.getLabel().replace(' ', '_') %>', 'width=900,height=700,resizable=yes,scrollbars=yes');win.focus();return false;" target='<%= l.getTarget() %>' href=<%= l.getUrl() %>><%= l.getLabel() %></a><%= toAppend %><%
	}else if("new".equals(l.getTarget())){
		%><a class=menu01 target='_blank' href=<%= l.getUrl() %>><%= l.getLabel() %></a><%= toAppend %><%
	}else {
          %><a class=menu01 target='<%= l.getTarget() %>' href=<%= l.getUrl() %>><%= l.getLabel() %></a><%= toAppend %><%
	}
      } else {
        //solo url
        %><a class=menu01 href=<%= l.getUrl() %>><%= l.getLabel() %></a><%= toAppend %><%
      }
    } else {
      if (!l.getOnClick().equals("")) {
        //onClick
        %><a class=menu01 href='' onClick="<%= l.getOnClick() %>; return false;"><%= l.getLabel() %></a><%= toAppend %><%
      } else {
	//solo stringa
        %><%= l.getLabel() %><%
      }
    }
  }
}
%>
</td>
<td class=blue></td>
</tr>
