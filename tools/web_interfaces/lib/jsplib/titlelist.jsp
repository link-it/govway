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
String pdString = "PageData";
if (iddati != null && !iddati.equals("notdefined")) {
  gdString += iddati;
  pdString += iddati;
}
else
  iddati = "notdefined";
GeneralData gd = (GeneralData) session.getValue(gdString);
PageData pd = (PageData) session.getAttribute(pdString);
String showListInfos = request.getParameter("showListInfos");
Vector v = pd.getDati();
%>

<%
Vector titlelist = pd.getTitleList();
if (titlelist != null && titlelist.size() > 0) {
  GeneralLink l;
  for (int i = 0; i < titlelist.size(); i++) {
    l = (GeneralLink) titlelist.elementAt(i);
    if (!l.getLabel().equals("")) {
      if (i != titlelist.size()-1) {
        if (!l.getUrl().equals("")) {
          //non ultimo con url
          %><a href=<%= l.getUrl() %>><%= l.getLabel() %></a> &gt; <%
        } else {
          //non ultimo ma senza url
          %><%= l.getLabel() %> &gt; <%
        }
      } else {
        //ultimo
        %><%= l.getLabel() %><%
	if (showListInfos != null && showListInfos.equals("true")) {
	  int index = pd.getIndex();
	  if (pd.getNumEntries() > 0)
	    index++;
	  %> [<%= index %>-<%= v.size()+pd.getIndex() %>] su <%= pd.getNumEntries() %><%
	}
      }
    }
  }
%>
</span><br>
<img src=images/dothdx.gif width=80 height=9><img src=images/dothdx.gif width=80 height=9><br>
<%
}
%>

