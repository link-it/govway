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


