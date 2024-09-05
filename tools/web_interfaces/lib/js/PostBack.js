/**
 * Funzioni utilizzate dalla addElement.jsp
 * @author Sandra Giangrandi <sandra@link.it>
 * @author Stefano Corallo <corallo@link.it>
 * @since 1.4
 */

function postBack() {
	postBack(null);
}
function postBack(dataElementName) {
	var theForm = document.form;
    // theForm.action vale http://localhost:8080/govwayConsole/servlet.do
    // A me serve solo il pezzo /govwayConsole/servlet.do
    var firstSlash = theForm.action.indexOf("/");
    var secondSlash = theForm.action.indexOf("/", firstSlash+1);
    var thirdSlash = theForm.action.indexOf("/", secondSlash+1);
    var location = theForm.action.substr(thirdSlash);
    
    var navigationAnchor = null;
    //aggiungo parametro per indicare che si tratta di postback e azzero idhid
    location += "?isPostBack=true&edit-mode=in_progress_postback";
    if(dataElementName!=null){
    	location += "&postBackElementName="+dataElementName;
    	navigationAnchor = '#'+ dataElementName;
    }
    
    // evito di mandare indietro al server il valore degli elementi hidden che si utilizzano per la creazione delle finestre DialogInfo.
	eliminaElementiHidden(theForm);
    
    for (var k=0; k<theForm.elements.length; k++) {
		var nome = theForm.elements[k].name;
		var hiddenInfo = nome!=null ? nome.indexOf("__i_hidden") : -1;
		if (nome && nome.length > 0 && nome != "idhid" && nome != "edit-mode" && hiddenInfo == -1) {
		    var tipo = theForm.elements[k].type;
		    var valore = "";
		    // elimino codice html dall'input testuale
		    if (tipo == "text" || tipo == "textarea" || tipo == "number"){
				var valoreTmp = theForm.elements[k].value;
				theForm.elements[k].value = HtmlSanitizer.SanitizeHtml(valoreTmp);
			}
		    
		    if (tipo == "text" || tipo == "file" || tipo == "hidden" || tipo == "textarea"|| tipo == "number")
		    	valore = theForm.elements[k].value;
		    if (tipo == "select-one") {
		    	for (var j=0; j<theForm.elements[k].options.length; j++)
		    		if (theForm.elements[k].options[j].selected == true)
		    			valore = theForm.elements[k].options[j].value;
		    }
		    if (tipo == "select-multiple") {
				for (var j=0; j<theForm.elements[k].options.length; j++) {
				    if (theForm.elements[k].options[j].selected == true) {
				    	valore = theForm.elements[k].options[j].value;
				    	location += "&" + nome + "=" + encodeURIComponent(valore);
				    }
			    }
		    }
		    if (tipo == "checkbox") {
		    	if (theForm.elements[k].checked)
		    		valore = "yes";
		    	else
		    		valore = "no";
		    }
		    if (tipo == "text" || tipo == "hidden" || tipo == "select-one" || tipo == "checkbox" || tipo == "textarea"|| tipo == "number") {
			    	location += "&" + nome + "=" + encodeURIComponent(valore);
		    }
		}
    }
    
    // addTabID
	location = addTabIdParam(location,true);
    
    if(navigationAnchor!=null){
    	location += navigationAnchor;
    }
    
    document.location = location;
}

function postVersion_postBack(dataElementName) {
        
    var theForm = document.form;
    //aggiungo parametro per indicare che si tratta di postback e azzero idhid
    addHidden(theForm, 'isPostBack' , true);
//    addHidden(theForm, 'edit-mode' , 'in_progress_postback');
    var navigationAnchor = null;
    if(dataElementName!=null){
    	addHidden(theForm, 'postBackElementName' , dataElementName);
    	navigationAnchor = '#'+ dataElementName;
    }
    
    // evito di mandare indietro al server il valore degli elementi hidden che si utilizzano per la creazione delle finestre DialogInfo.
	eliminaElementiHidden(theForm);
    
    // dump 
    var dump = false;
    
   
	for (var k=0; k<theForm.elements.length; k++) {
		var nome = theForm.elements[k].name;
		
		if(nome == "edit-mode")
			theForm.elements[k].value = 'in_progress_postback';
		
		var tipo = theForm.elements[k].type;
	
		// elimino codice html dall'input testuale
		if (tipo == "text" || tipo == "textarea" || tipo == "number"){
			var valore = theForm.elements[k].value;
			theForm.elements[k].value = HtmlSanitizer.SanitizeHtml(valore);
		}

		if (tipo == "checkbox") {
			if (!theForm.elements[k].checked){
  			   addHidden(theForm, nome , 'no');
			}
		}
		
		if(dump) { 
		    var valore = "";
		    if (tipo == "text" || tipo == "file" || tipo == "hidden" || tipo == "textarea" || tipo == "number")
			valore = theForm.elements[k].value;
		    if (tipo == "select-one") {
			for (var j=0; j<theForm.elements[k].options.length; j++)
			    if (theForm.elements[k].options[j].selected == true)
				valore = theForm.elements[k].options[j].value;
		    }
		    if (tipo == "select-multiple") {
				for (var j=0; j<theForm.elements[k].options.length; j++) {
				    if (theForm.elements[k].options[j].selected == true) {
				    	if(valore.length > 0)
				    		valore += ", ";
				    	
				    	valore += theForm.elements[k].options[j].value;
				    }
			    }
		    }
		    if (tipo == "checkbox") {
			if (theForm.elements[k].checked)
			    valore = "yes";
			else
			    valore = "no";
		    }
		   
		    console.log('Input ['+nome+'], Tipo ['+tipo+'], Valore ['+valore+']');
	    }
    }
    
    // Se la location url contiene dei parametri, vanno eliminati. Sono dovuti a precedenti eventi di postback via GET
    var checkActionUrl = document.location.href;
    var indexActionUrl = checkActionUrl.indexOf('?');
    var newActionUrl = checkActionUrl;
    //console.log('Precedente URL: ' + checkActionUrl);
    if (indexActionUrl >0 && indexActionUrl < (checkActionUrl.length - 1) ){
        newActionUrl = checkActionUrl.split('?')[0] + '?';
    }
    //console.log('Nuova URL: ' + newActionUrl);
    
    if(navigationAnchor!=null){
    	newActionUrl += navigationAnchor;
    }
    
    theForm.action=newActionUrl;
        
    // evito di mandare indietro al server il valore degli elementi hidden che si utilizzano per la creazione delle finestre DialogInfo.
	for (var k=0; k<theForm.elements.length; k++) {
		var nome = theForm.elements[k].name;
		var hiddenInfo = nome!=null ? nome.indexOf("__i_hidden") : -1;

		if(hiddenInfo > -1) {
			theForm.elements[k].value = '';
		}
	}
	
	// aggiungo parametro idTab
  if(tabValue != ''){
  	addHidden(theForm, tabSessionKey , tabValue);
  	addHidden(theForm, prevTabSessionKey , tabValue);
  }
  
	//console.log('ACTION FINALE: ' + theForm.action);
    // form submit
    theForm.submit();
}

function scrollToPostBackElement(destElement) {
	if(destElement) {
		console.log("ElemName");
		console.log(destElement);
		
		var elem = $('[name="'+ destElement+ '"]');
		if(elem.length > 0) {
			var elemPosiz = elem.closest('div.prop').prevAll('div.subtitle').find('span > a.navigatorAnchor');
			
			if(elemPosiz.length == 0) {
				elemPosiz = elem.closest('div.prop').parent().prev().find('span > a.navigatorAnchor');
			}
			
			if(elemPosiz.length == 0) {
				elemPosiz = elem.closest("fieldset").find('legend > a.navigatorAnchor');
			}
			
			if(elemPosiz.length == 0) {
				elemPosiz = elem.closest("fieldset").find('legend > a.navigatorAnchorClosable');
			}
			 
			if(elemPosiz.length > 0){
				var baseUrl = elemPosiz[0].name; //navAnc.attr('name');
				console.log(baseUrl);
				if(elemPosiz.length > 0)
					elemPosiz[0].scrollIntoView();
			}
		}
	}
}
