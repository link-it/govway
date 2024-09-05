/**
 * Funzioni di utilita'
 *
 * @author Giuliano Pintori <pintori@link.it>
 */

/* Funzioni di utilita' utilizzate in tutte le pagine */

function generaUrl() {
	return convertFormAsURL(document.form);
}

function convertFormAsURL(theForm) {
    var params = '';

    for (var k=0; k<theForm.elements.length; k++) {
		var nome = theForm.elements[k].name;
		if (nome && nome.length > 0 && nome != "idhid") {
		    var tipo = theForm.elements[k].type;
		    var valore = "";
		    if ( tipo == "hidden"){
				valore = theForm.elements[k].value;
				params += "&" + nome + "=" + valore;
	    	}
	    }
    }

	return params;
}

function formHasParam(theForm, name){
	for (var k=0; k<theForm.elements.length; k++) {
		var nome = theForm.elements[k].name;
		if (nome && nome.length > 0) {
		    if ( nome === name){
				return true;
	    	}
	    }
    }

	return false;
}

function elementIsCheckbox(theForm, name){
	for (var k=0; k<theForm.elements.length; k++) {
		var nome = theForm.elements[k].name;
		if (nome && nome.length > 0) {
		    if ( nome === name){
				var tipo = theForm.elements[k].type;
		
				if (tipo === "checkbox") {
					return true;
				}				
	    	}
	    }
    }

	return false;
}

function addHidden(theForm, name, value) {
	// controllo di sicurezza per evitare di aggiungere due volte il parametro con lo stesso nome.
	if(elementIsCheckbox(theForm, name) || !formHasParam(theForm,name)){
	    // Create a hidden input element, and append it to the form:
	    var input = document.createElement('input');
	    input.type = 'hidden';
	    input.name = name;
	    input.value = value;
	    theForm.appendChild(input);
    }
}

function addTabIdParamToHref(element, event){
	var urlDest = element.prop('href');

    if(event.which == 3) {
    	// console.log("right click: " + urlDest);
    } else
    if(event.which == 2) {
    	// console.log("center click: " + urlDest);
    } else
    if(event.which == 1) {
    	// aggiungi tab id a tutti i link cliccati col tasto sinistro
    	if(urlDest) {
	    	// console.log("left click: " + urlDest);

	    	var targetDest = element.prop('target');

	    	if(targetDest && targetDest == '_blank') {
	    		return;
	    	}

	    	var newUrlDest = addTabIdParam(urlDest);
	    	element.prop('href',newUrlDest);
    	} else {
    		// console.log("href non trovato per l'elemento di tipo: " + $(this));
    	}
    } else {
    	console.log("click non riconosciuto: " + urlDest);
    }
}

function white(str) {
  for (var n=0; n<str.length; n++){
    if (str.charAt(n) == ' '){
      ok = false;
    }
  }
}

function URLEncode(url) {
  var SAFECHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_.!~*'()";
  var HEX = "0123456789ABCDEF";

  var encoded = "";
  for (var i=0; i<url.length; i++) {
    var ch = url.charAt(i);
    if (ch == " ") {
      encoded += "+";
    } else {
      if (SAFECHARS.indexOf(ch) != -1) {
	encoded += ch;
      } else {
	var charCode = ch.charCodeAt(0);
	if (charCode > 255) {
	  encoded += "+";
	} else {
	  encoded += "%";
	  encoded += HEX.charAt((charCode >> 4) & 0xF);
	  encoded += HEX.charAt(charCode & 0xF);
	}
      }
    }
  }

  return encoded;
}

function customInputNumberChangeEventHandler(e){

	if (e.target.value == '') {
    	// do nothing
    } else {
    	if(e.target.min){
    		if(parseInt(e.target.min) > parseInt(e.target.value)){
    			e.target.value = e.target.min;
    		}
    	}

    	if(e.target.max){
    		if(parseInt(e.target.max) < parseInt(e.target.value)){
    			e.target.value = e.target.max;
    		}
    	}
    }
}

function inputNumberChangeEventHandler(e){
	if (e.target.value == '') {
    	if(e.target.min){
         e.target.value = e.target.min;
    	} else {
    		e.target.value = 0;
    	}
    } else {
    	if(e.target.min){
    		if(parseInt(e.target.min) > parseInt(e.target.value)){
    			e.target.value = e.target.min;
    		}
    	}

    	if(e.target.max){
    		if(parseInt(e.target.max) < parseInt(e.target.value)){
    			e.target.value = e.target.max;
    		}
    	}
    }
}

function urlHasParam(href, name){
	return href && href.indexOf(name) > -1;
}

function addTabIdParam(href, addPrevTabParam){

	if(tabValue != '' && !urlHasParam(href,tabSessionKey)){
		var param = (tabSessionKey + "="+tabValue);

		if((href != '#' && href.indexOf('#tabs-') == -1)){
	        if (href.charAt(href.length - 1) === '?') //Very unlikely
	            href = href + param;
	        else if (href.indexOf('?') > 0)
	        	href = href + '&' + param;
	        else
	        	href = href + '?' + param;

	        if(addPrevTabParam && !urlHasParam(href,prevTabSessionKey)) {
				var paramPrevTab = (prevTabSessionKey + "="+tabValue);
				return href + '&' + paramPrevTab;
			}
	    }
	}
    return href;
}

function addParamToURL(href, paramKey, paramValue){

	if(paramValue != '' && !urlHasParam(href,paramKey)){
		var param = (paramKey + "="+paramValue);

		if((href != '#' && href.indexOf('#tabs-') == -1)){
	        if (href.charAt(href.length - 1) === '?') //Very unlikely
	            href = href + param;
	        else if (href.indexOf('?') > 0)
	        	href = href + '&' + param;
	        else
	        	href = href + '?' + param;

	    }
	}
    return href;
}


function visualizzaAjaxStatus(){
	if($("#ajax_status_div").length>0){
		$("#ajax_status_div").css('display', 'block');
	}
}

function nascondiAjaxStatus(){
	if($("#ajax_status_div").length>0){
		$("#ajax_status_div").css('display', 'none');
	}
}

function goToLocation(location){
	if(location) {
		//addTabID
		location = addTabIdParam(location,true);
		document.location = location;
	}
}



function mostraDataElementInfoModal(title,body){
	$("#dataElementInfoModal").prev().children('span').text(title);
	$("#dataElementInfoModalBody").html(body);
	$("#dataElementInfoModal").dialog("open");
}

/* Funzioni di utilita' per le pagine form */

function inizializzaSelectSezione(idDiv){
	var divElem = $('#'+ idDiv + '');

	if(divElem.length > 0){
    	if(divElem.find("select" ).length > 0){
    		// elimino eventuali plugin gia' applicati
    		divElem.find("select" ).each(function() {
    			var wrapper = $( this ).parent();
    			if(wrapper.prop('id').indexOf('_wrapper') > -1) {
    				// appendo la select come secondo elemento dopo la label
    				var labelProp = $( this ).parent().parent().children().first();
    				labelProp.after($( this ));
    				wrapper.remove();
    				$( this ).css('width','');
    				$( this ).css('height','');
    			}

    			var checkID = $( this ).prop('id') + '_hidden_chk';
    			if($( '#' + checkID ).length > 0) {
    				var val = $( '#' + checkID ).prop('value');
    				if(val && val == 'true'){
    					$( this ).searchable({disableInput : false});
    				} else {
    					$( this ).searchable({disableInput : true});
    				}
    			} else {
    				$( this ).searchable({disableInput : true});
    			}
    		});
    	}
	}
}

/* Funzioni di utilita' per le pagine con le liste */

function inizializzaSelectFiltro(){
	if($('select[id^=filterValue_]').length > 0){
		// elimino eventuali plugin gia' applicati
		$('select[id^=filterValue_]').each(function() {
			var wrapper = $( this ).parent();
			if(wrapper.prop('id').indexOf('_wrapper') > -1) {
				$( this ).appendTo($( this ).parent().parent());
				wrapper.remove();
				$( this ).css('width','');
				$( this ).css('height','');
			}

			var checkID = $( this ).prop('id') + '_hidden_chk';
			if($( '#' + checkID ).length > 0) {
				var val = $( '#' + checkID ).prop('value');
				if(val && val == 'true'){
					$( this ).searchable({disableInput : false});
				} else {
					$( this ).searchable({disableInput : true});
				}
			} else {
				$( this ).searchable({disableInput : true});
			}
		});
	}
}

function formSubmit(){
	return false;
}

function isModificaUrlRicerca(formAction, urlToCheck){
	// hack hash documento impostato, la parte di url che contiene la # bisogna eliminarla dal check
	if(formAction.indexOf('#') > 0) {
		formAction = formAction.substring(0, formAction.indexOf('#'));
	}
	if(formAction.indexOf('?') > 0) {
		formAction = formAction.substring(0, formAction.indexOf('?'));
	}

	return ieEndsWith(formAction, urlToCheck);
}

function ieEndsWith(str, suffix){
	return str.indexOf(suffix, str.length - suffix.length) !== -1;
}

function Change(theForm,dataElementName) {
	Change(theForm,dataElementName,false);
}
function Change(theForm,dataElementName,fromFilters) {

	if( fromFilters ){
		var formAction = theForm.action;

		// hack actionvuota
		if(formAction == ''){
			formAction = document.location.href;
		}

		if(isModificaUrlRicerca(formAction,'Add.do')){
			theForm.action=formAction.replace('Add.do','List.do');
		}
		if(isModificaUrlRicerca(formAction,'Change.do')){
			theForm.action=formAction.replace('Change.do','List.do');
		}
		if(isModificaUrlRicerca(formAction,'Del.do')){
			theForm.action=formAction.replace('Del.do','List.do');
		}
	}

    //aggiungo parametro per indicare che si tratta di postback e azzero idhid
    addHidden(theForm, 'isPostBack' , true);
    if(dataElementName!=null)
    	addHidden(theForm, 'postBackElementName' , dataElementName);
    addHidden(theForm, 'index' , 0);
    addHidden(theForm, 'iddati' , iddati);

    // formatParams

     if (formatPar != null && formatPar != ""){
    	var pairs = ((formatPar[0] === '?' || formatPar[0] === '&') ? formatPar.substr(1) : formatPar).split('&');
    	for (var i = 0; i < pairs.length; i++) {
        	var pair = pairs[i].split('=');
        	addHidden(theForm, pair[0] , pair[1]);
    	}
     }
     if (params != null && params != ""){
	   var pairs = ((params[0] === '?' || params[0] === '&') ? params.substr(1) : params).split('&');
	   for (var i = 0; i < pairs.length; i++) {
	       var pair = pairs[i].split('=');
	       addHidden(theForm, pair[0] , pair[1]);
	   }
     }

	// evito di mandare indietro al server il valore degli elementi hidden che si utilizzano per la creazione delle finestre DialogInfo.
	 eliminaElementiHidden(theForm);

	// elimino codice html dall'input testuale
     for (var k=0; k<theForm.elements.length; k++) {
   		// elimino codice html dall'input testuale
   		 var tipo = theForm.elements[k].type;
		 if (tipo == "text" || tipo == "textarea" || tipo == "number"){
			var valore = theForm.elements[k].value;
			theForm.elements[k].value = HtmlSanitizer.SanitizeHtml(valore);
		}
     }

  // aggiungo parametro idTab
  	  if(tabValue != ''){
  	  	addHidden(theForm, tabSessionKey , tabValue);
  	    addHidden(theForm, prevTabSessionKey , tabValue);
  	  }
    // form submit
    theForm.submit();
}

function Reset(theForm) {
  if (nr != 0) {
    return false;
  }
  nr = 1;

  theForm.reset();
  for (var k=0; k< theForm.elements.length; k++) {
	var name = theForm.elements[k].name;
	if (name == "search"){
		theForm.elements[k].value="";
	} else {
		var tipo = theForm.elements[k].type;
		if (tipo == "select-one" || tipo == "select-multiple") {
			theForm.elements[k].selectedIndex = 0;
		} else if (tipo == "text" || tipo == "textarea"|| tipo == "number") {
			theForm.elements[k].value="";
		} else if (tipo == "checkbox") {
			theForm.elements[k].checked=false;
		}
	}
  }

  addHidden(theForm, 'index' , 0);
  addHidden(theForm, 'iddati' , iddati);
  addHidden(theForm, 'pageSize' , pageSize);
  addHidden(theForm, '_searchDone' , true);

  // formatParams

   if (formatPar != null && formatPar != ""){
  	var pairs = ((formatPar[0] === '?' || formatPar[0] === '&') ? formatPar.substr(1) : formatPar).split('&');
  	for (var i = 0; i < pairs.length; i++) {
      	var pair = pairs[i].split('=');
      	addHidden(theForm, pair[0] , pair[1]);
  	}
   }
   if (params != null && params != ""){
	   var pairs = ((params[0] === '?' || params[0] === '&') ? params.substr(1) : params).split('&');
	   for (var i = 0; i < pairs.length; i++) {
	       var pair = pairs[i].split('=');
	       addHidden(theForm, pair[0] , pair[1]);
	   }
  }

   // imposto la destinazione
   theForm.action = nomeServletList;

   // evito di mandare indietro al server il valore degli elementi hidden che si utilizzano per la creazione delle finestre DialogInfo.
   eliminaElementiHidden(theForm);

   // aggiungo parametro idTab
   if(tabValue != ''){
   	addHidden(theForm, tabSessionKey , tabValue);
   	addHidden(theForm, prevTabSessionKey , tabValue);
   }
  // form submit
  theForm.submit();

};

function Search(theForm) {
  if (nr != 0) {
    return false;
  }
  nr = 1;

  addHidden(theForm, 'index' , 0);
  addHidden(theForm, 'iddati' , iddati);
  addHidden(theForm, 'pageSize' , pageSize);
  addHidden(theForm, '_searchDone' , true);

  // formatParams

   if (formatPar != null && formatPar != ""){
  	var pairs = ((formatPar[0] === '?' || formatPar[0] === '&') ? formatPar.substr(1) : formatPar).split('&');
  	for (var i = 0; i < pairs.length; i++) {
      	var pair = pairs[i].split('=');
      	addHidden(theForm, pair[0] , pair[1]);
  	}
   }
   if (params != null && params != ""){
	   var pairs = ((params[0] === '?' || params[0] === '&') ? params.substr(1) : params).split('&');
	   for (var i = 0; i < pairs.length; i++) {
	       var pair = pairs[i].split('=');
	       addHidden(theForm, pair[0] , pair[1]);
	   }
   }

  // imposto la destinazione
  theForm.action = nomeServletList;

	// evito di mandare indietro al server il valore degli elementi hidden che si utilizzano per la creazione delle finestre DialogInfo.
	eliminaElementiHidden(theForm);
	
	// elimino codice html dall'input testuale
  	for (var k=0; k<theForm.elements.length; k++) {
		 var tipo = theForm.elements[k].type;
		 if (tipo == "text" || tipo == "textarea" || tipo == "number"){
			var valore = theForm.elements[k].value;
			theForm.elements[k].value = HtmlSanitizer.SanitizeHtml(valore);
		}
	}

  // aggiungo parametro idTab
  if(tabValue != ''){
  	addHidden(theForm, tabSessionKey , tabValue);
  	addHidden(theForm, prevTabSessionKey , tabValue);
  }
  // form submit
  theForm.submit();

};

function checkAll(){
	if(n > 0){
		var chkAll = $("#chkAll:checked").length;

		if(chkAll > 0) {
			SelectAll(document.form);
		} else {
			DeselectAll(document.form);
		}
	}
}

function SelectAll(theForm) {
  if (n > 1) {
    for (var c = 0; c < theForm.selectcheckbox.length; c++)
      theForm.selectcheckbox[c].checked = true;
  } else {
    theForm.selectcheckbox.checked = true;
  }
};

function DeselectAll(theForm) {
  if (n > 1) {
    for (var c = 0; c < theForm.selectcheckbox.length; c++)
      theForm.selectcheckbox[c].checked = false;
  } else {
    theForm.selectcheckbox.checked = false;
  }
};

function eliminaElementiHidden(theForm){
	//evito di mandare indietro al server il valore degli elementi hidden che si utilizzano per la creazione delle finestre DialogInfo.
  	for (var k=0; k<theForm.elements.length; k++) {
		var nome = theForm.elements[k].name;

		if(nome != null){
			if(nome.indexOf("__i_hidden") > -1
				|| nome.indexOf("url_entry_") > -1){
				theForm.elements[k].value = '';
			}
		}
	}	
}

