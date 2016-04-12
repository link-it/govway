<%--
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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



<%@ page session="true" import="java.util.Vector, org.apache.commons.lang.StringEscapeUtils ,org.openspcoop2.web.lib.mvc.*" %>

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
String displayImage = request.getParameter("displayImage");
if (displayImage == null)
  displayImage = "true";
%>

<%
boolean mime = false;
Vector dati = pd.getDati();
for (int i = 0; i < dati.size(); i++) {
  DataElement de = (DataElement) dati.elementAt(i);
  if (de.getType().equals("file")) {
    mime = true;
  }
}
if (mime) {
  %><form name=form ENCTYPE='multipart/form-data' action='<%= gd.getUrl() %>' method=post><%
} else {
  %><form name=form action='<%= gd.getUrl() %>' method=post><%
}
%>

<% if (displayImage.equals("true")) { %>
  <td valign=top background=images/plugsx.gif class=corpoTesto>
<% } else { %>
  <td valign=top class=corpoTesto>
<% } %>

<p><span class=history>

<jsp:include page="/jsplib/titlelist.jsp" flush="true" />

    <%= pd.getPageDescription() %>
    </p>
<%
String message = pd.getMessage();
if (!message.equals("")) {
  %>
  <%= message %><br><br>
  <%
}
%>

<table border=0 cellspacing=0 cellpadding=2 class=tabella>

<%
boolean affiancato = false;

boolean elementsRequiredEnabled = true;
if (pd.getMode().equals("view-noeditbutton")) {
	elementsRequiredEnabled = false;
}

boolean elementsRequired = false;
if(elementsRequiredEnabled){
	for (int i = 0; i < dati.size(); i++) {
	  DataElement de = (DataElement) dati.elementAt(i);
	  if(de.isRequired()){
	     elementsRequired=true;
	     break;
	  }
	}
	if(elementsRequired){
		DataElement de = new DataElement();
		de.setLabel("<BR/>* Campi obbligatori");
		de.setType("text");
		de.setName("CampiObbligatori");
		de.setValue(" ");
		dati.addElement(de);
	}
}

for (int i = 0; i < dati.size(); i++) {
  DataElement de = (DataElement) dati.elementAt(i);
  
  String type = de.getType();
  String rowName="row_"+de.getName();
  
  if (!affiancato) {
    %><tr class=table01pari name=<%= rowName %>><%
  }
  
  if (type.equals("text")) {
    if (!affiancato) {
      %><td height=14 class=history><%
      if (!de.getLabel(elementsRequiredEnabled).equals("")) {
        %><%= de.getLabel(elementsRequiredEnabled) %>&nbsp;<%
      } else {
        %>&nbsp;<%
      }
      %></td>
      <td><%
    }
    if (de.getValue() != null && !de.getValue().equals("")) {
      %><%= de.getValue() %><%
    } else {
	if (pd.getMode().equals("view-noeditbutton")) {
	%><%
	}
	else{
      %>not defined<%
	}
    }
    %><input type=hidden name="<%= de.getName() %>" value="<%= de.getValue() %>"><%
    if (!de.getAffiancato()) {
      %></td><%
    } else {
      %>&nbsp;&nbsp;<%
    }

  } else {

    if (type.equals("textedit")) {
      if (!affiancato) {
        %><td height=14 class=history><%= de.getLabel(elementsRequiredEnabled) %>&nbsp;
        </td>
        <td><%
      }
      if (pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) {
        if (de.getValue() != null && !de.getValue().equals("")) {
          %><%= de.getValue() %><%
        } else {
	  if (pd.getMode().equals("view-noeditbutton")) {
		%><%
	  }
	  else{
          	%>not defined<%
	  }
        }
      } else {
        %><input size=<%= de.getSize() %> name="<%= de.getName() %>" value="<%= de.getValue() %>"><%
      }
      if (!de.getAffiancato()) {
        %></td><%
      } else {
        %>&nbsp;&nbsp;<%
      }

    } else {

      if (type.equals("file")) {
        if (pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) {
          if (!affiancato) {
            if (de.getAffiancato()) {
	      // modalita' view, precedente non affiancato, successivo affiancato
	      // = inizio riga, scrivo label, inizio td dati
              %><td height=14 class=history><%
              if (!de.getLabel(elementsRequiredEnabled).equals("")) {
                %><%= de.getLabel(elementsRequiredEnabled) %>&nbsp;<%
              } else {
                %>&nbsp<%
              }
              %></td>
              <td><%= de.getValue() %><%
            } else {
	      // modalita' view, precedente non affiancato, successivo non affiancato
	      // = inizio riga, scrivo label, inizio td dati, scrivo value, chiudo td dati
              %><td height=14 class=history><%
              if (!de.getLabel(elementsRequiredEnabled).equals("")) {
                %><%= de.getLabel(elementsRequiredEnabled) %>&nbsp;<%
              } else {
                %>&nbsp<%
              }
              %></td>
              <td><%
	      if (de.getValue() != null && !de.getValue().equals("")) {
	        %><%= de.getValue() %><%
	      } else {
	        %>not defined<%
	      }
	      %></td><%
	    }
          } else {
            if (!de.getAffiancato()) {
              // modalita' view, precedente affiancato, successivo non affiancato
              // = scrivo value e chiudo td
              %><%= de.getValue() %></td><%
            } else {
              // modalita' view, precedente affiancato, successivo affiancato
              // = scrivo value
	      %><%= de.getValue() %><%
            }
          }
        } else {
          if (!affiancato) {
            %><td height=14 class=history><%
            if (!de.getLabel(elementsRequiredEnabled).equals("")) {
              %><%= de.getLabel(elementsRequiredEnabled) %>&nbsp;<%
            } else {
              %>&nbsp;<%
            }
            %></td>
            <td><%
          }
          %><small><input size='<%= de.getSize() %>' type=file name="<%= de.getName()  %>"></small><%
          if (!de.getAffiancato()) {
            %></td><%
          } else {
            %>&nbsp;&nbsp;<%
          }
        }

      } else {
      
        if (type.equals("hidden")) {
	  %><input type=hidden name="<%= de.getName()  %>" value="<%= de.getValue()  %>"><%

	} else {

          if (type.equals("crypt")) {
            if (!affiancato) {
              %><td height=14 class=history><%
              if (!de.getLabel(elementsRequiredEnabled).equals("")) {
                %><%= de.getLabel(elementsRequiredEnabled) %>&nbsp;<%
              } else {
                %>&nbsp;<%
              }
              %></td>
              <td><%
            }
            if (pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) {
		%>********<%
	    } else {
		%><input size=<%= de.getSize() %> type=password name="<%= de.getName()  %>" value="<%= de.getValue()  %>"><%
	    }
            if (!de.getAffiancato()) {
	      %></td><%
	    } else {
	      %>&nbsp;&nbsp;<%
	    }

	  } else {

            if ( (type.equals("textarea") || type.equals("textarea-noedit")) && de.isLabelAffiancata() ) {
              String inputId = "txtA" + i;
	      if (type.equals("textarea-noedit")){
		inputId = "txtA_ne" + i; 
	      }
            	
	      if (!affiancato) {
		%><td height=14 class=history><%= de.getLabel(elementsRequiredEnabled) %>&nbsp;
		</td>
		<td><%
	      }
	      if (pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) {
		if (de.getValue() != null && !de.getValue().equals("")) {
		  %><%= de.getValue() %><%
		} else {
		  if (pd.getMode().equals("view-noeditbutton")) {
			%><%
		  }
		  else{
		  	%>not defined<%
		  }
		}
	      } else {
		if (type.equals("textarea")){
			%><textarea id="<%=inputId %>" rows='<%= de.getRows() %>' cols='<%= de.getCols() %>' name="<%= de.getName()  %>"><%= de.getValue() %></textarea><%
		}else{
			%><textarea id="<%=inputId %>" readonly rows='<%= de.getRows() %>' cols='<%= de.getCols() %>' name="<%= de.getName()  %>"><%= de.getValue() %></textarea><%
		}
	      }
	      if (!de.getAffiancato()) {
		%></td><%
	      } else {
		%>&nbsp;&nbsp;<%
	      }	      

          } else {

            if ( (type.equals("textarea") || type.equals("textarea-noedit")) && !de.isLabelAffiancata() ) {
               String inputId = "txtA" + i;
	      if (type.equals("textarea-noedit")){
		inputId = "txtA_ne" + i; 
	      }
            	// <td height=14 class=history><%= de.getLabel(elementsRequiredEnabled) % >&nbsp;</td>
              if (affiancato) {
                //il precedente elemento non puo' essere affiancato: chiudo la linea e inizio la nuova
                %></td></tr><tr class=table01pari><%
              }
              %>
             
              <td colspan=2 height=14 class=history>
              <% if(de.getLabel(elementsRequiredEnabled) != null && !de.getLabel(elementsRequiredEnabled).isEmpty() ){%>
               		<label for="<%=inputId %>" style="display: block;"><%= de.getLabel(elementsRequiredEnabled) %>&nbsp;</label>
               <% }	
		if (type.equals("textarea")){
			%><textarea id="<%=inputId %>" rows='<%= de.getRows() %>' cols='<%= de.getCols() %>' name="<%= de.getName()  %>"><%= de.getValue() %></textarea><%
		}else{
			%><textarea id="<%=inputId %>" readonly rows='<%= de.getRows() %>' cols='<%= de.getCols() %>' name="<%= de.getName()  %>"><%= de.getValue() %></textarea><%
		}%>
              </td><%
              

            } else {

              if (type.equals("select")) {
	        if (!affiancato) {
		  %><td height=14 class=history><%= de.getLabel(elementsRequiredEnabled) %>&nbsp;
		  </td>
		  <td valign=middle><%
	        }
                if (pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) {
	   	  if (de.getSelected() != "") {
		    %><%= de.getSelected() %><%
		  } else {
		    if (pd.getMode().equals("view-noeditbutton")) {
			 %><%
		    }else{
			%>not defined<%
		    }
		  }
	        } else {
		  %><select name="<%= de.getName()  %>"<%
                  if (!de.getSubType().equals("")) {
		    %> size='<%= de.getRows() %>' <%= de.getSubType() %><%
		  }
                  if (!de.getOnChange().equals("")) {
		    %> onChange="<%= de.getOnChange() %>"<%
		  }
		  %>><%
		  String [] values = de.getValues();
                  if (values != null) {
		    String [] labels = de.getLabels();
		    for (int v = 0; v < values.length; v++) {
		      if (labels != null) {
			if (values[v].equals(de.getSelected())) {
                          %><option value="<%= values[v]  %>" selected><%= labels[v] %><%
                        } else {
                          %><option value="<%= values[v]  %>"><%= labels[v] %><%
                        }
		      } else {
		        if (values[v].equals(de.getSelected())) {
		          %><option value="<%= values[v]  %>" selected><%= values[v] %><%
		        } else {
		          %><option value="<%= values[v]  %>"><%= values[v] %><%
		        }
		      }
		    }
                  }
		  %></select><%
	        }
	        if (!de.getAffiancato()) {
		  %></td><%
	        } else {
		  %>&nbsp;&nbsp;<%
	        }

	      } else {

                if (type.equals("button")) {
                  if (!affiancato) {
		    %><td height=14 class=history><%= de.getLabel(elementsRequiredEnabled) %>&nbsp;</td>
			<td><%
		  }
		  %><input type=button onClick="<%= de.getOnClick() %>" value="<%= de.getValue() %>"><%
	          if (!de.getAffiancato()) {
		    %></td><%
		  } else {
		    %>&nbsp;&nbsp;<%
		  }

	        } else {

		  if (type.equals("checkbox")) {
		    if (!affiancato) {
		      %><td height=14 class=history><%= de.getLabel(elementsRequiredEnabled) %>&nbsp;</td>
			<td><%
		    }
		    if (de.getSelected().equals("yes")) {
	              if (pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) {
			%>ON<%
		      } else {
		        %><input type=checkbox name="<%= de.getName()  %>" value=yes checked='true' 
		        <% if (!de.getOnClick().equals(""))%> 
		        		onClick="<%= de.getOnClick() %>"><%
		      }
	            } else {
	              if (pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) {
			%>OFF<%
		      } else {
			%><input type=checkbox name="<%= de.getName()  %>" value=yes 
				<% if (!de.getOnClick().equals(""))%> 
		        		onClick="<%= de.getOnClick() %>"><%
		      }
	            }
	            if (!de.getAffiancato()) {
		      %></td><%
		    } else {
		      %>&nbsp;&nbsp;<%
		    }

	          } else {
		 
		    if (type.equals("note")) {
                      if (!affiancato) {
                        //il precedente elemento non puo' essere affiancato:
                        //chiudo la linea e inizio la nuova
                        %></td></tr><tr class=table01pari><%
                      }
                      %><td colspan=2 height=14 class=history>
		      <% if (de.getLabel(elementsRequiredEnabled)!=null){ %>
			  <%= de.getLabel(elementsRequiredEnabled) %>
		      <% } %>
		      <% if (de.getValue()!=null){ %>
                          <%= de.getValue() %>
		      <% } %>
                      </td><%

                    } else {

 
		    if (type.equals("link")) {
		      if (!affiancato) {
			//il precedente elemento non puo' essere affiancato:
		        //chiudo la linea e inizio la nuova
			%></td></tr><tr class=table01pari><%
		      }
		      %><td colspan=2 height=14 class=history>
		      <a href="<%= de.getUrl() %>"><%= de.getValue() %></a>
	    	      </td><%

	            } else {
		
		      if (type.equals("radio")) {
		        if (affiancato) {
		      	  //il precedente elemento non puo' essere affiancato:
		          //chiudo la linea e inizio la nuova
			  %></td></tr><tr class=table01pari><%
		        }
		        %><td height=14 class=history><%= de.getLabel(elementsRequiredEnabled) %>&nbsp;
	    		</td>
	    		<td><%
	    	        if (pd.getMode().equals("view") || pd.getMode().equals("view-noeditbutton")) {
			  if (!de.getSelected().equals("")) {
			    %><%= de.getSelected() %><%
	 		  } else {
		   	    %>not defined<%
			  }
		    	} else {
			  String [] values = de.getValues();
			  String [] labels = de.getLabels();
			  for (int v = 0; v < values.length; v++) {
		    	    if (values[v].equals(de.getSelected())) {
			      %><input type=radio checked name="<%= de.getName()  %>" value="<%= values[v]  %>">&nbsp;&nbsp;<%= labels[v] %><%
			    } else {
			      %><input type=radio name="<%= de.getName()  %>" value="<%= values[v]  %>">&nbsp;&nbsp;<%= labels[v] %><%
		    	    }
			    if (v<values.length-1) {
			      %><br><%
			    }
		          }
			}
		        if (!de.getAffiancato()) {
		    	  %></td><%
		        } else {
		    	  %>&nbsp;&nbsp;<%
		        }

		      } else {

		        if (type.equals("title")) {
		          if (affiancato) {
		      	    //il precedente elemento non puo' essere affiancato:
		            //chiudo la linea e inizio la nuova
			    %></td></tr><tr class=table01pari><%
			  }
			  %><td colspan=2 height=14 class=table01header>
	    		  <%= de.getLabel(elementsRequiredEnabled) %>&nbsp;
			  </td><%
			}


		      } //else radio		 
                  	} //else label 
		    } //else link
		  }  //else checkbutton
		} //else button
	      } //else select
            } //else textarea/textarea-noedit label-affiancata
            } //else textarea-noedit label-non-affiancata
          } //else crypt
        } //else hidden
      } // else file
    } // else textedit
  } // else text

  if (!de.getAffiancato() || de.getType().equals("link") || de.getType().equals("textarea") || de.getType().equals("textarea-noedit")) {
    %></tr><%
    affiancato = false;
  } else {
    affiancato = true;
  }
} // for

if (pd.getMode().equals("view")) {
  %><tr>
  <td colspan=2 class=table01footer><div align=right><%
  String [][] bottoni = pd.getBottoni();
  if ((bottoni != null) && (bottoni.length > 0)) {
    for (int i = 0; i < bottoni.length; i++) {
      %><input type=button onClick="<%= bottoni[i][1] %>" value="<%= bottoni[i][0] %>">&nbsp;<%
    }
  } else {
    %><input type=button onClick=EditPage() value=Edit><%
  }
  %></div>
  </td>
  </tr>
  <%
} else {
  if (pd.getMode().equals("view-noeditbutton") || pd.getMode().equals("view-nobutton") ) {
    %><tr>
    <td colspan=2 class=table01footerNoButtons><img src=images/spacer.gif></td>
    </tr><%
  } else {  
    %><tr>
    <td colspan=2 class=table01footer>
    <div align=right><input type=submit onClick='CheckDati();return false;' value=Invia>
    <input type=button onClick='document.form.reset();' value=Cancella></div>
    </td>
    </tr><%
  }
}
%>

</table>
</td>
</form>
