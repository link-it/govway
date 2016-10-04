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



<%@ page session="true" import="java.util.*, org.openspcoop2.web.lib.mvc.*" %>

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
%>

<form name='form' method='get' onSubmit='return false;' id="form">

<%
Hashtable hidden = pd.getHidden();
if (hidden!=null) {
    for (Enumeration e = hidden.keys() ; e.hasMoreElements() ;) {
	String key = (String) e.nextElement();
	String value = (String) hidden.get(key);
	%><input type=hidden name=<%= key %> value=<%= value %>><%
    }
}
%>

<td valign=top background=images/plugsx.gif class=corpoTesto>
<p><span class=history>

<jsp:include page="/jsplib/titlelist.jsp" flush="true">
	<jsp:param name="showListInfos" value="true"/>
</jsp:include>

<%= pd.getPageDescription() %>
</p>

<%
String message = pd.getMessage();
if (!message.equals("")) {
  %><%= message %><br><br><%
}

if (pd.getSearch().equals("on") || (pd.getSearch().equals("auto") && pd.getNumEntries() > 10)) {
  String searchDescription = pd.getSearchDescription();
  if (!searchDescription.equals("")) {
    %><span class=history>Ricerca:</span>
    <br>
    <%= searchDescription %><br><br><%
  }
  %><input type=text name=search size=15>
  <input type=button value=Ricerca onClick=Search(document.form.search.value)>
  <br><br>
  <%if (!searchDescription.equals("")) {%>
  Per annullare una precedente ricerca cliccare su 'Ricerca' senza fornire criteri di filtro<br><br>
  <% } %>
  <%
}

if (pd.getFilter() != null) {
  DataElement filtro = pd.getFilter();
  String [] values = filtro.getValues();
  String [] labels = filtro.getLabels();
  String selezionato = filtro.getSelected();

  %>
  <span class=history><%= filtro.getLabel() %></span>&nbsp;&nbsp;
  <select name=filter onChange=Filtra()>
  <%
  
  for (int i = 0; i < values.length; i++) {
    if (values[i].equals(selezionato)) {
      %><option value='<%= values[i] %>' selected><%= labels[i] %><%
    } else {
      %><option value='<%= values[i] %>'><%= labels[i] %><%
    }
  }
  %></select><br><br><%
}
%>

<table border=0 cellpadding=0 cellspacing=0>

<tr> 
<td background=images/dothsx.gif height=7 colspan=3 valign=top class=bgbottomsx></td>
</tr>

<tr> 
<td valign=top>

<table border=0 cellpadding=0 cellspacing=0 class=tabella>
<tr>

<%
String [] labels = pd.getLabels();
if(labels!=null){
	for (int i = 0; i < labels.length;i++) {
	  %><td class=table01header><%= labels[i] %></td><%
	}
}
if (pd.getSelect()) {
  %>
  <td class=table01header width=30>Selected</td>
  <%
}
%>
</tr>
<%

//inizio entries

Vector v = pd.getDati();
String stile;
for (int i = 0; i < v.size(); i++) {
  //per ogni entry:

  %><tr><%

  //stringa contenente l identificativo dell'elemento che verra associato al checkbox per l eliminazione
  String idToRemove = null;
  
  if ((i % 2) == 0) {
    stile = "table01pari";
  } else {
    stile = "table01dispari";
  }

  Vector e = (Vector) v.elementAt(i);
  for (int j = 0; j < e.size(); j++) {

    DataElement de = (DataElement) e.elementAt(j);

    if (!de.getType().equals("hidden")) {
      %><td class=<%= stile %>><%
    }

    if (de.getType().equals("text")) {
    	//setto l'id del campo checkbfull_002dlist_jsp.java:182ox che serve per l'eliminazione
    	//in caso non sia gia' stato settato.
    	//if(idToRemove==null) idToRemove = de.getValue();
    	if(idToRemove==null) idToRemove = de.getIdToRemove();
    	
      if (!de.getUrl().equals("")) {
    		//tooltip
	  		String tip = "";
	  		boolean showTip=false;
	  		if(de.getToolTip()!=null && !"".equals(de.getToolTip())){
	  			tip=de.getToolTip();
	  			showTip=true;
	  		}
	  		//se la lunghezza del dato impostato come value e' > della larghezza della
	  		//colonna allora accorcio il dato ed imposto il tooltip
	  		//15 e' il valore di default impostato nel dataelement
	  		int size=de.getSize();
	  		String res=de.getValue();
	  		if(size>15 && de.getValue().trim().length()>size){
	  			res=de.getValue().trim().substring(0,size)+" ...";
	  			//se nn e' stato specificato un tip precedentemente
	  			//metto come tip il valore completo del campo
	  			if(!showTip) tip=de.getValue();
	  			
	  			showTip=true;
	  		}

        if (!de.getTarget().equals("")) {
          //url+target
          if(showTip){
        	  %><a class="<%= stile %>" title="<%= tip %>" target="<%= de.getTarget() %>" href="<%= de.getUrl() %>"><%= res %></a><%
          }else{
        	  %><a class="<%= stile %>" target="<%= de.getTarget() %>" href="<%= de.getUrl() %>"><%= res %></a><%  
          }
          
        } else {
	  		//url only
	  		if(showTip){
	  			%><a class="<%= stile %>" title="<%= tip %>" href="<%= de.getUrl() %>"><%= res %></a><%
	  		}else{
	  			%><a class="<%= stile %>" href="<%= de.getUrl() %>"><%= res %></a><%
	  		}
	  		
	}
      } else {
	//no url
	if (!de.getOnClick().equals("")) {
	  //onclick
	  %><a class=<%= stile %> href='' onClick="<%= de.getOnClick() %>; return false;"><%= de.getValue() %></a><%
	} else {
	  //string only
	  %><%= de.getValue() %><%
	}
      }

    } else {

      // Tipo hidden
      if (de.getType().equals("hidden")) {
	%><input type=hidden name=<%= de.getName() %> value=<%= de.getValue() %>><%

      } else {

	// Tipo image
	if (de.getType().equals("image")) {

	  String[] stValue = de.getValue().split("\\s");
	  String[] stUrl = de.getUrl().split("\\s");
	  String[] stOnClick = de.getOnClick().split("\\s");

	  // Ciclo sulla lista di immagini
	  for (int x=0; x<stValue.length; x++) {

	    // Se e' definito 'Url'
	    if (stUrl.length > x && stUrl[x] != "") {
	      %><a class=<%= stile %> href=<%= stUrl[x] %>><img border=0 src=images/<%= stValue[x] %>></a>&nbsp;<%
	    } else {

	      // Se e' definito 'OnClick'
	      if (stOnClick.length > x && stOnClick[x] != "") {
		%><a class=<%= stile %> href='' onClick=\"<%= stOnClick[x] %>; return false;\"><img border=0 src=images/<%= stValue[x] %>></a>&nbsp;<%
	      } else {

		// Solo immagine
		%><img border=0 src=images/<%= stValue[x] %>>&nbsp;<%
	      }
	    }
	  }
	} else {

          if (de.getType().equals("radio")) {

	    String[] stValues = de.getValues();
	    String[] stLabels = de.getLabels();

	    // Ciclo sulla lista di valori 
	    for (int r = 0; r < stValues.length; r++) {
	      if (stValues[r].equals(de.getSelected())) {
                 %><input type=radio checked name='<%= de.getName() %>' value='<%= stValues[r] %>'>&nbsp;&nbsp;<%= stLabels[r] %><%
	      } else {
                 %><input type=radio name='<%= de.getName() %>' value='<%= stValues[r] %>'>&nbsp;&nbsp;<%= stLabels[r] %><%
              }
	      if (r<stValues.length-1) {
		%><br><%
              }
	    }
	  }
	}
      }
    }

    if (!de.getType().equals("hidden")) {
      %></td><%
    }
  }

  if (pd.getSelect()) {
    %>
    <td class=<%= stile %>><div align=center>
    <input id='_<% if(idToRemove!=null) out.write(idToRemove);else out.write(""+i); %>' type=checkbox name=selectcheckbox value='<% if(idToRemove!=null) out.write(idToRemove);else out.write(""+i); %>'/>
    </div></td><%
  }
  %></tr><%

}

//fine entries

//Bottoni SelectAll, DeselectAll
if (pd.getSelect()) {
  %>
  <tr>
  <td colspan=<%= labels.length+1 %> class=table01footer><div align=right>
  <input type=button onClick=SelectAll() value='Seleziona Tutti'>
  <input type=button onClick=DeselectAll() value='Deseleziona Tutti'>
  <%

  //Bottone di Add
  if (pd.getAddButton()) {
    %><input type=button onClick=AddEntry() value='Aggiungi'><%
  }

  //Bottone di Remove
  if (pd.getRemoveButton()) {
    %><input id='rem_btn' type=button  value='Rimuovi Selezionati'><%
  }

  %>
  </div></td>
  </tr>
  <%
}
else{
	%>
	 <tr>
	  <td colspan=<%= labels.length+1 %> class=table01footer><div align=right>
  	  &nbsp;</div></td>
  	</tr>
  	<%
}

%>
</table>
<br>
<%

//Bottone Previous
if (pd.getIndex() != 0) {
  %><input type=button onClick=PrevPage(document.form.limit.options[document.form.limit.selectedIndex].value) value='Prev'><%
}

//Scelta numero di entries da visualizzare
if ((pd.getNumEntries() > 20) || (pd.getIndex() != 0)) {
  %><select name=limit onChange=CambiaVisualizzazione(document.form.limit.options[selectedIndex].value)><%
  switch (pd.getPageSize()) {
    case 20 :
	%>
	<option value="20" selected="selected">20 Entries</option>
	<option value="75">75 Entries</option>
	<option value="125">125 Entries</option>
	<option value="250">250 Entries</option>
	<option value="500">500 Entries</option>
	<option value="1000">1000 Entries</option>
	<%
	break;
    case 75 :
	%>
	<option value="20">20 Entries</option>
	<option value="75" selected="selected">75 Entries</option>
	<option value="125">125 Entries</option>
	<option value="250">250 Entries</option>
	<option value="500">500 Entries</option>
	<option value="1000">1000 Entries</option>
	<%
	break;
    case 125 :
	%><option value="20">20 Entries</option>
	<option value="75">75 Entries</option>
	<option value="125" selected="selected">125 Entries</option>
	<option value="250">250 Entries</option>
	<option value="500">500 Entries</option>
	<option value="1000">1000 Entries</option><%
	break;
    case 250 :
	%>
	<option value="20">20 Entries</option>
	<option value="75">75 Entries</option>
	<option value="125">125 Entries</option>
	<option value="250" selected="selected">250 Entries</option>
	<option value="500">500 Entries</option>
	<option value="1000">1000 Entries</option>
	<%
	break;
    case 500 :
    	%>
    	<option value="20">20 Entries</option>
    	<option value="75">75 Entries</option>
    	<option value="125">125 Entries</option>
    	<option value="250">250 Entries</option>
    	<option value="500" selected="selected">500 Entries</option>
    	<option value="1000">1000 Entries</option>
    	<%
    	break;
    case 1000 :
    	%>
    	<option value="20">20 Entries</option>
    	<option value="75">75 Entries</option>
    	<option value="125">125 Entries</option>
    	<option value="250">250 Entries</option>
    	<option value="500">500 Entries</option>
    	<option value="1000" selected="selected">1000 Entries</option>
    	<%
    	break;
  }
  %></select><%
}

//Bottone Next
if (pd.getPageSize() != 0) {
  if (pd.getIndex()+pd.getPageSize() < pd.getNumEntries()) {
    %><input type=button onClick=NextPage() value='Next'><%
  }
}
%>
</td>
<%

Vector areaBottoni = pd.getAreaBottoni();
if (areaBottoni != null) {
  %><td valign=top><img src=images/spacer.gif width=10 height=1></td>
  <td class=table01dispari valign=top nowrap><%
  for (int i = 0; i < areaBottoni.size(); i++) {
    AreaBottoni area = (AreaBottoni) areaBottoni.elementAt(i);
    String title = area.getTitle();
    Vector bottoni = area.getBottoni();
    %><p><%
    if (!title.equals("")) {
      %><strong><%= title %></strong><br>
      <img src=images/dothdx.gif width=80 height=9><br><%
    }

    for (int b = 0; b < bottoni.size(); b++) {
      DataElement bottone = (DataElement) bottoni.elementAt(b);
      %>
      <input type=button onClick=<%= bottone.getOnClick() %> value='&gt;'>
      <em><%= bottone.getValue() %></em><br><%
    }
    %></p><%
  }
  %></td><%
}

%>
</tr>
</table>
<br>
</td>
</form>
