/**
 * Funzioni di utilita' 
 * 
 * @author Giuliano Pintori <pintori@link.it>
 */

/* Funzioni di utilita' utilizzate in tutte le pagine */

function addHidden(theForm, name, value) {
    // Create a hidden input element, and append it to the form:
    var input = document.createElement('input');
    input.type = 'hidden';
    input.name = name;
    input.value = value;
    theForm.appendChild(input);
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

function addTabIdParam(href, addPrevTabParam){
	
	if(tabValue != ''){
		var param = (tabSessionKey + "="+tabValue);
		
		if((href != '#' && href.indexOf('#tabs-') == -1)){
	        if (href.charAt(href.length - 1) === '?') //Very unlikely
	            href = href + param;
	        else if (href.indexOf('?') > 0)
	        	href = href + '&' + param;
	        else
	        	href = href + '?' + param;
	        
	        if(addPrevTabParam) {
				var paramPrevTab = (prevTabSessionKey + "="+tabValue);
				return href + '&' + paramPrevTab;
			}
	    }
	}
    return href;
}

function addParamToURL(href, paramKey, paramValue){
	
	if(paramValue != ''){
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

function Change(form,dataElementName) {
	Change(form,dataElementName,false);
}
function Change(form,dataElementName,fromFilters) {
    
	if( fromFilters ){
		var formAction = form.action;
		
		// hack actionvuota
		if(formAction == ''){
			formAction = document.location.href;
		}
		
		if(isModificaUrlRicerca(formAction,'Add.do')){
			form.action=formAction.replace('Add.do','List.do');
		}
		if(isModificaUrlRicerca(formAction,'Change.do')){
			form.action=formAction.replace('Change.do','List.do');
		}
		if(isModificaUrlRicerca(formAction,'Del.do')){
			form.action=formAction.replace('Del.do','List.do');
		}
	}
	
    //aggiungo parametro per indicare che si tratta di postback e azzero idhid
    addHidden(form, 'isPostBack' , true);
    if(dataElementName!=null)
    	addHidden(document.form, 'postBackElementName' , dataElementName);
    addHidden(form, 'index' , 0);
    addHidden(form, 'iddati' , iddati);
  
    // formatParams
    
     if (formatPar != null && formatPar != ""){
    	var pairs = ((formatPar[0] === '?' || formatPar[0] === '&') ? formatPar.substr(1) : formatPar).split('&');
    	for (var i = 0; i < pairs.length; i++) {
        	var pair = pairs[i].split('=');
        	addHidden(form, pair[0] , pair[1]);
    	}
     }
     if (params != null && params != ""){
	   var pairs = ((params[0] === '?' || params[0] === '&') ? params.substr(1) : params).split('&');
	   for (var i = 0; i < pairs.length; i++) {
	       var pair = pairs[i].split('=');
	       addHidden(form, pair[0] , pair[1]);
	   }
     }
     
     // evito di mandare indietro al server il valore degli elementi hidden che si utilizzano per la creazione delle finestre DialogInfo.
     for (var k=0; k<document.form.elements.length; k++) {
   		var nome = document.form.elements[k].name;
   		var hiddenInfo = nome!=null ? nome.indexOf("__i_hidden") : -1;

   		if(hiddenInfo > -1) {
   			document.form.elements[k].value = '';
   		}
     }
        
  // aggiungo parametro idTab
  	  if(tabValue != ''){
  	  	addHidden(document.form, tabSessionKey , tabValue);
  	    addHidden(document.form, prevTabSessionKey , tabValue);
  	  }
    // form submit
    document.form.submit();
}

function Reset(form) {
  if (nr != 0) {
    return false;
  }
  nr = 1;
  
  document.form.reset();
  for (var k=0; k<document.form.elements.length; k++) {
	var name = document.form.elements[k].name;
	if (name == "search"){
		document.form.elements[k].value="";
	} else {
		var tipo = document.form.elements[k].type;
		if (tipo == "select-one" || tipo == "select-multiple") {
			document.form.elements[k].selectedIndex = 0;
		} else if (tipo == "text" || tipo == "textarea"|| tipo == "number") {
			document.form.elements[k].value="";
		} else if (tipo == "checkbox") {
			document.form.elements[k].checked=false;
		}
	}
  }

  addHidden(form, 'index' , 0);
  addHidden(form, 'iddati' , iddati);
  addHidden(form, 'pageSize' , pageSize);
  addHidden(form, '_searchDone' , true);

  // formatParams
  
   if (formatPar != null && formatPar != ""){
  	var pairs = ((formatPar[0] === '?' || formatPar[0] === '&') ? formatPar.substr(1) : formatPar).split('&');
  	for (var i = 0; i < pairs.length; i++) {
      	var pair = pairs[i].split('=');
      	addHidden(form, pair[0] , pair[1]);
  	}
   }
   if (params != null && params != ""){
	   var pairs = ((params[0] === '?' || params[0] === '&') ? params.substr(1) : params).split('&');
	   for (var i = 0; i < pairs.length; i++) {
	       var pair = pairs[i].split('=');
	       addHidden(form, pair[0] , pair[1]);
	   }
  }
  
   // imposto la destinazione
   document.form.action = nomeServletList;
   
   // evito di mandare indietro al server il valore degli elementi hidden che si utilizzano per la creazione delle finestre DialogInfo.
   for (var k=0; k<document.form.elements.length; k++) {
 		var nome = document.form.elements[k].name;
 		var hiddenInfo = nome!=null ? nome.indexOf("__i_hidden") : -1;

 		if(hiddenInfo > -1) {
 			document.form.elements[k].value = '';
 		}
   }
   
   // aggiungo parametro idTab
   if(tabValue != ''){
   	addHidden(document.form, tabSessionKey , tabValue);
   	addHidden(document.form, prevTabSessionKey , tabValue);
   }
  // form submit
  document.form.submit();
 
};

function Search(form) {
  if (nr != 0) {
    return false;
  }
  nr = 1;

  addHidden(form, 'index' , 0);
  addHidden(form, 'iddati' , iddati);
  addHidden(form, 'pageSize' , pageSize);
  addHidden(form, '_searchDone' , true);

  // formatParams
  
   if (formatPar != null && formatPar != ""){
  	var pairs = ((formatPar[0] === '?' || formatPar[0] === '&') ? formatPar.substr(1) : formatPar).split('&');
  	for (var i = 0; i < pairs.length; i++) {
      	var pair = pairs[i].split('=');
      	addHidden(form, pair[0] , pair[1]);
  	}
   }
   if (params != null && params != ""){
	   var pairs = ((params[0] === '?' || params[0] === '&') ? params.substr(1) : params).split('&');
	   for (var i = 0; i < pairs.length; i++) {
	       var pair = pairs[i].split('=');
	       addHidden(form, pair[0] , pair[1]);
	   }
   }

  // imposto la destinazione
  document.form.action = nomeServletList;
  
  //evito di mandare indietro al server il valore degli elementi hidden che si utilizzano per la creazione delle finestre DialogInfo.
  for (var k=0; k<document.form.elements.length; k++) {
		var nome = document.form.elements[k].name;
		var hiddenInfo = nome!=null ? nome.indexOf("__i_hidden") : -1;

		if(hiddenInfo > -1) {
			document.form.elements[k].value = '';
		}
  }
      
  // aggiungo parametro idTab
  if(tabValue != ''){
  	addHidden(document.form, tabSessionKey , tabValue);
  	addHidden(document.form, prevTabSessionKey , tabValue);
  }
  // form submit
  document.form.submit();
 
};

function checkAll(){
	if(n > 0){
		var chkAll = $("#chkAll:checked").length;
		
		if(chkAll > 0) {
			SelectAll();
		} else {
			DeselectAll();
		}
	}
}

function SelectAll() {
  if (n > 1) {
    for (var c = 0; c < document.form.selectcheckbox.length; c++)
      document.form.selectcheckbox[c].checked = true;
  } else {
    document.form.selectcheckbox.checked = true;
  }
};

function DeselectAll() {
  if (n > 1) {
    for (var c = 0; c < document.form.selectcheckbox.length; c++)
      document.form.selectcheckbox[c].checked = false;
  } else {
    document.form.selectcheckbox.checked = false;
  }
};



