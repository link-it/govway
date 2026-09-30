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
	var charCode = ch.codePointAt(0);
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
    		if(Number.parseInt(e.target.min) > Number.parseInt(e.target.value)){
    			e.target.value = e.target.min;
    		}
    	}

    	if(e.target.max){
    		if(Number.parseInt(e.target.max) < Number.parseInt(e.target.value)){
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
    		if(Number.parseInt(e.target.min) > Number.parseInt(e.target.value)){
    			e.target.value = e.target.min;
    		}
    	}

    	if(e.target.max){
    		if(Number.parseInt(e.target.max) < Number.parseInt(e.target.value)){
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

/* Download con indicatore di operazione in corso.
   Un download non provoca il caricamento di una nuova pagina: l'indicatore mostrato al click resterebbe visibile
   per sempre. La pagina genera quindi un token casuale e lo invia con la richiesta; il server, quando il file è pronto
   e subito prima di inviarlo, lo restituisce nel cookie GW_DOWNLOAD_<token> (ServletUtils.addDownloadTokenCookie).
   Alla comparsa del cookie l'indicatore viene rimosso. Il token nel nome del cookie evita che download avviati
   in parallelo da più schede della console si sovrascrivano o si cancellino a vicenda il cookie.
   Se la richiesta termina con una pagina (es. errore), la navigazione rimuove l'indicatore senza bisogno del cookie. */
var GW_DOWNLOAD_TOKEN_PARAM = 'downloadToken';
var GW_DOWNLOAD_TOKEN_COOKIE_PREFIX = 'GW_DOWNLOAD_';
var GW_DOWNLOAD_POLL_MS = 500;
var GW_DOWNLOAD_TIMEOUT_MS = 10 * 60 * 1000;
var GW_DOWNLOAD_MSG_IN_CORSO = 'Preparazione del file in corso: il download partirà al termine.';
var GW_DOWNLOAD_MSG_AVVIATO = 'Il file è pronto: download avviato.';
var GW_DOWNLOAD_MSG_TIMEOUT = 'La preparazione del file sta richiedendo più tempo del previsto: il download partirà comunque al termine.';
var gwDownloadInCorso = null;

function gwGeneraDownloadToken() {
	var c = window.crypto || window.msCrypto;
	if (!c || !c.getRandomValues) {
		return null;
	}
	var bytes = new Uint8Array(16);
	c.getRandomValues(bytes);
	var token = '';
	for (var i = 0; i < bytes.length; i++) {
		token += ('0' + bytes[i].toString(16)).slice(-2);
	}
	return token;
}

/* Path con cui il server imposta il cookie: il context path della console (le pagine sono servite al primo livello). */
function gwDownloadCookiePath() {
	var path = window.location.pathname;
	var idx = path.lastIndexOf('/');
	path = idx > 0 ? path.substring(0, idx) : '';
	return path !== '' ? path : '/';
}

function gwLeggiDownloadTokenCookie(token) {
	var nome = GW_DOWNLOAD_TOKEN_COOKIE_PREFIX + token;
	var cookies = document.cookie ? document.cookie.split(';') : [];
	for (var i = 0; i < cookies.length; i++) {
		var c = cookies[i].replace(/^\s+/, '');
		if (c.indexOf(nome + '=') === 0) {
			return c.substring(nome.length + 1);
		}
	}
	return null;
}

function gwCancellaDownloadTokenCookie(token) {
	var secure = window.location.protocol === 'https:' ? '; Secure' : '';
	// 'expires' oltre a 'Max-Age': Internet Explorer non supporta 'Max-Age'
	document.cookie = GW_DOWNLOAD_TOKEN_COOKIE_PREFIX + token + '=; expires=Thu, 01 Jan 1970 00:00:00 GMT; Max-Age=0; path=' + gwDownloadCookiePath() + '; SameSite=Strict' + secure;
}

function gwAnnunciaStatoDownload(messaggio) {
	var regione = document.getElementById('gw_download_status');
	if (regione) {
		regione.textContent = messaggio ? messaggio : '';
	}
}

/* Imposta (o sostituisce) un campo hidden: addHidden non aggiorna il valore di un campo già presente. */
function gwImpostaHidden(theForm, name, value) {
	var el = theForm.elements[name];
	if (el && el.type === 'hidden') {
		el.value = value;
	} else {
		addHidden(theForm, name, value);
	}
}

/* Avvia l'attesa del download: mostra l'indicatore e restituisce il token da inviare al server
   (null se il browser non consente di generarlo: in tal caso l'indicatore non viene mostrato). */
function gwAvviaAttesaDownload(theForm, button) {
	var token = gwGeneraDownloadToken();
	if (!token) {
		return null;
	}
	var attesa = { token: token, form: theForm, button: button, avvio: Date.now(), timer: null };
	gwDownloadInCorso = attesa;
	visualizzaAjaxStatus();
	if (theForm) {
		theForm.setAttribute('aria-busy', 'true');
	}
	if (button) {
		// aria-disabled e non disabled: il bottone mantiene il fuoco, che altrimenti andrebbe perso
		button.setAttribute('aria-disabled', 'true');
	}
	gwAnnunciaStatoDownload(GW_DOWNLOAD_MSG_IN_CORSO);
	attesa.timer = setInterval(function() {
		if (gwLeggiDownloadTokenCookie(attesa.token) === attesa.token) {
			gwTerminaAttesaDownload(attesa, GW_DOWNLOAD_MSG_AVVIATO);
			if (attesa.onAvviato) {
				attesa.onAvviato();
			}
		} else if (Date.now() - attesa.avvio > GW_DOWNLOAD_TIMEOUT_MS) {
			gwTerminaAttesaDownload(attesa, GW_DOWNLOAD_MSG_TIMEOUT);
		}
	}, GW_DOWNLOAD_POLL_MS);
	return token;
}

function gwTerminaAttesaDownload(attesa, messaggio) {
	if (!attesa || gwDownloadInCorso !== attesa) {
		return;
	}
	gwDownloadInCorso = null;
	if (attesa.timer) {
		clearInterval(attesa.timer);
	}
	gwCancellaDownloadTokenCookie(attesa.token);
	nascondiAjaxStatus();
	if (attesa.form) {
		attesa.form.removeAttribute('aria-busy');
		var el = attesa.form.elements[GW_DOWNLOAD_TOKEN_PARAM];
		if (el && el.parentNode) {
			el.parentNode.removeChild(el);
		}
	}
	if (attesa.button) {
		attesa.button.removeAttribute('aria-disabled');
	}
	gwAnnunciaStatoDownload(messaggio);
}

/* Invio di una form il cui esito è il download di un file. 'invia' è la funzione di invio della pagina (es. CheckDati):
   se restituisce false (es. validazione non superata) l'attesa viene annullata. */
function gwAvviaDownloadConIndicatore(theForm, button, invia) {
	if (gwDownloadInCorso) {
		return false;
	}
	var attesa = null;
	var token = gwAvviaAttesaDownload(theForm, button);
	if (token) {
		attesa = gwDownloadInCorso;
		gwImpostaHidden(theForm, GW_DOWNLOAD_TOKEN_PARAM, token);
	}
	var esito = invia();
	if (esito === false) {
		gwTerminaAttesaDownload(attesa, null);
		return false;
	}
	if (attesa && typeof nr !== 'undefined') {
		// la pagina resta la stessa: una volta avviato il download si consente di ripetere l'operazione
		// (CheckDati blocca gli invii successivi al primo tramite 'nr').
		attesa.onAvviato = function() {
			nr = 0;
		};
	}
	return esito;
}

/* Ritorno alla pagina dalla cache del browser (tasto Indietro): nessun download è in attesa. */
window.addEventListener('pageshow', function(event) {
	if (event.persisted && gwDownloadInCorso) {
		gwTerminaAttesaDownload(gwDownloadInCorso, null);
	}
});

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

  // Validazione client-side dei filtri di ricerca: blocca il submit e evidenzia
  // i campi con caratteri non ammessi dal pattern server-side (vedi utils.jsp).
  // L'ajax-status era gia' stato attivato dall'onclick del bottone, lo nascondiamo
  // se la validazione blocca il submit.
  if (typeof gwValidateForm === "function" && !gwValidateForm(theForm)) {
    if (typeof nascondiAjaxStatus === "function") {
      nascondiAjaxStatus();
    }
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

function singleCheckboxListener(theForm, state){

	var totalCheckboxes = theForm.selectcheckbox.length; // Get total number of checkboxes except #selectAll

	var selectedCheckboxes = 0;
	for (var c = 0; c < theForm.selectcheckbox.length; c++) {
		if(theForm.selectcheckbox[c].checked === true){
			selectedCheckboxes ++;
		}
	}
	    
    if (state === true) {
        if (selectedCheckboxes === totalCheckboxes) {
            $('#chkAll').prop('checked', true); // Select #selectAll if all other checkboxes are selected
        }
    } else {
        $('#chkAll').prop('checked', false); // Deselect #selectAll if a checkbox is unchecked
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

function customAction(form,functionName) {
	addHidden(form, functionName , 'true');
	Search(form);
}

/* --- Accessibilita': comandi resi con elementi non nativi -------------------------------
   Diversi comandi della console sono <span> o <a> senza href a cui il JavaScript aggancia un
   clic. Il browser non li mette nell'ordine di tabulazione e non li attiva con la tastiera,
   quindi risultano utilizzabili solo col mouse (WCAG 2.1.1). Le due funzioni seguenti li
   espongono come comandi e ne consentono l'attivazione con Invio e con la barra spaziatrice. */

/* Elementi che il JavaScript rende cliccabili, con il nome da usare quando non se ne trova uno
   nel markup. La lista e' esplicita perche' non ogni gestore di clic identifica un comando: i
   contenitori che delegano non vanno esposti. Il nome predefinito non e' un dettaglio: un elemento
   con 'role=button' e senza nome e' peggio di nessun pulsante, perche' un lettore di schermo lo
   annuncia come "pulsante" e basta. In diverse pagine questi comandi non hanno alcun 'title', ne'
   proprio ne' di un antenato. */
var GW_COMANDI = [
	{ selettore: '.spanIconInfoBox',          nome: 'Informazioni' },
	{ selettore: '.spanIconInfoBox-cb-info',  nome: 'Informazioni' },
	{ selettore: '.iconInfoBox-cb-info',      nome: 'Informazioni' },
	{ selettore: '.spanIconInfoBoxList',      nome: 'Informazioni' },
	{ selettore: '.spanIconInfoBox-copyLock', nome: 'Copia informazione cifrata' },
	{ selettore: '.spanIconInfoBox-viewLock', nome: 'Visualizza informazione cifrata' },
	{ selettore: '.spanIconUsoBoxList',       nome: 'Utilizzo' },
	{ selettore: '.spanIconCopyBox',          nome: 'Copia' },
	{ selettore: '.copy-box',                 nome: 'Copia' },
	{ selettore: '#iconaPanelListaSpan',      nome: 'Filtri di ricerca' },
	{ selettore: '[id^="spanIconMenu_"]',     nome: 'Azioni disponibili' }
];

/* Il nome accessibile viene preso dal 'title' dell'elemento o, se assente, dal primo antenato che
   ne ha uno: nel markup della console il tooltip risiede spesso sul contenitore. */
function gwNomeComando(el) {
	var $el = jQuery(el);
	var t = ($el.attr('title') || '').trim();
	if (t) {
		return t;
	}
	var $anc = $el.closest('[title]');
	return $anc.length ? ($anc.attr('title') || '').trim() : '';
}

function gwEsponiComandi(ambito) {
	var $ambito = jQuery(ambito || document);
	/* Il menu' azioni apre un elenco di voci: va dichiarato, altrimenti l'utente non sa che
	   attivandolo comparira' un menu'. */
	$ambito.find('[id^="spanIconMenu_"]').attr('aria-haspopup', 'menu');

	for (var k = 0; k < GW_COMANDI.length; k++) {
		var voce = GW_COMANDI[k];
		$ambito.find(voce.selettore).each(function() {
			var $c = jQuery(this);
			if ($c.attr('role') === 'button') {
				return;                                                   // gia' esposto
			}
			if (this.querySelector('a[href],button,input,select,textarea')) {
				return;                                                   // delega a un comando nativo
			}
			var nome = $c.attr('aria-label') || ($c.attr('aria-labelledby') ? 'x' : '') || gwNomeComando(this) || voce.nome;
			if (!nome) {
				return;   // senza un nome non lo si espone: un pulsante anonimo e' peggio di nessun pulsante
			}
			$c.attr('role', 'button');
			$c.attr('tabindex', '0');
			if (!$c.attr('aria-label') && !$c.attr('aria-labelledby') && !($c.attr('title') || '').trim()) {
				$c.attr('aria-label', nome);
			}
		});
	}
}

/* Un'area con contenuto scorrevole deve poter ricevere il focus, altrimenti le frecce non
   hanno un bersaglio e il testo che eccede l'altezza disponibile risulta illeggibile da
   tastiera (WCAG 2.1.1). Riguarda le finestre modali della console, che rendono testi di
   lunghezza non prevedibile. Si marcano solo le aree che scorrono davvero, verificato a
   finestra aperta: prima dell'apertura le dimensioni non sono ancora definite. */
function gwEsponiAreeScorrevoli(radice) {
	var $aree = jQuery();
	jQuery(radice).find('*').addBack().each(function() {
		var st = window.getComputedStyle(this);
		var puoScorrere = /(auto|scroll)/.test(st.overflowY) || /(auto|scroll)/.test(st.overflowX);
		var scorre = this.scrollHeight > this.clientHeight + 1 || this.scrollWidth > this.clientWidth + 1;
		if (puoScorrere && scorre) {
			jQuery(this).attr('tabindex', '0');
			$aree = $aree.add(this);
		}
	});
	return $aree;
}

jQuery(function() {
	gwEsponiComandi(document);

	/* 'role=button' non attiva il clic da tastiera: lo si emula una volta per tutte, con un
	   gestore delegato che copre anche i comandi aggiunti al DOM successivamente. */
	jQuery(document).on('keydown', '[role="button"][tabindex="0"]:not([id^="spanIconMenu_"])', function(e) {
		if (e.key === 'Enter' || e.key === ' ') {
			e.preventDefault();
			jQuery(this).trigger('click');
		}
	});

	/* Il menu' azioni e' escluso dall'attivatore generico: il plugin 'jquery.context-menu' non
	   ascolta 'click' ma 'mousedown', verificando quale tasto del mouse e' stato premuto
	   (cfr. jquery.context-menu.src.js, 'element.mousedown'). Da tastiera si emette quindi
	   l'evento che il plugin attende, indicando il tasto sinistro. */
	jQuery(document).on('keydown', '[id^="spanIconMenu_"]', function(e) {
		if (e.key !== 'Enter' && e.key !== ' ') {
			return;
		}
		e.preventDefault();
		/* Il plugin posiziona il menu' sull'ultima posizione nota del puntatore, che aprendo da
		   tastiera e' ferma altrove: il menu' comparirebbe lontano dal comando. Si aggiorna quindi
		   quella posizione emettendo un 'mousemove' sulle coordinate del comando, il che usa il
		   meccanismo che il plugin ha gia' (cfr. jquery.context-menu.src.js, '$(window).mousemove'). */
		var r = this.getBoundingClientRect();
		jQuery(window).trigger(jQuery.Event('mousemove', {
			pageX: r.left + window.scrollX,
			pageY: r.bottom + window.scrollY
		}));
		jQuery(this).trigger(jQuery.Event('mousedown', { which: 1 }));
	});

	/* Nelle viste custom degli elenchi la riga intera e' cliccabile via JavaScript e aggiunge
	   l'identificativo di tab alla URL. Il collegamento sul titolo serve a rendere la riga
	   raggiungibile da tastiera e annunciabile: la navigazione resta al gestore della riga,
	   a cui il clic arriva per propagazione, quindi qui si annulla solo il default. */
	jQuery(document).on('click', 'a.titoloEntry', function(e) {
		e.preventDefault();
	});

	jQuery(document).on('dialogopen', function(e) {
		var $aree = gwEsponiAreeScorrevoli(e.target);
		gwEsponiComandi(e.target);
		if (!$aree.length) {
			return;
		}
		/* Se la finestra non contiene altri comandi (le modali informative sono solo testo) il
		   focus va sull'area scorrevole, così le frecce funzionano subito. Se invece contiene
		   campi o pulsanti, jQuery UI ha già portato il focus sul primo: non glielo si toglie. */
		var altri = jQuery(e.target)
			.find('a[href], button, input:not([type=hidden]), select, textarea, [role="button"][tabindex="0"]')
			.not($aree).filter(':visible');
		if (!altri.length) {
			$aree.first().trigger('focus');
		}
	});
});
