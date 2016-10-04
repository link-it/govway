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

<SCRIPT LANGUAGE = JavaScript1.2>

function EseguiConferma(servlet) {
		
  setEditModeValue('ok');
	
  if (servlet)
	  nomeServlet_Custom_Ok = servlet+'?actionConfirm=ok';
  else
	  addConfirmAction('ok');
  	
  EseguiOp();
};

function AnnullaConferma(servlet) {
	
	setEditModeValue('no');
	
	  if (servlet)
		  nomeServlet_Custom_No = servlet+'?actionConfirm=no';
	  else
		  addConfirmAction('no');
		 
   Annulla();
};

function addConfirmAction(value){
	var myin = document.createElement("input");
    myin.type='hidden';
    myin.name='actionConfirm';
    myin.value=value;
    document.form.appendChild(myin);
}

function setEditModeValue(value){
	var elementEditMode = document.form.elements['edit-mode'];
	
	if(elementEditMode) {
		
	} else {
		var elementEditMode = document.createElement("input");
		elementEditMode.type='hidden';
		elementEditMode.name='edit-mode';
	    document.form.appendChild(elementEditMode);
	}
	
	if(value == 'ok')
		elementEditMode.value='end';
	else 
		elementEditMode.value='in_progress';
	
}

</SCRIPT>