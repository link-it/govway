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
    // document.form.action vale http://localhost:8080/govwayConsole/servlet.do
    // A me serve solo il pezzo /govwayConsole/servlet.do
    var firstSlash = document.form.action.indexOf("/");
    var secondSlash = document.form.action.indexOf("/", firstSlash+1);
    var thirdSlash = document.form.action.indexOf("/", secondSlash+1);
    var location = document.form.action.substr(thirdSlash);
    
    var navigationAnchor = null;
    //aggiungo parametro per indicare che si tratta di postback e azzero idhid
    location += "?isPostBack=true&edit-mode=in_progress_postback";
    if(dataElementName!=null){
    	location += "&postBackElementName="+dataElementName;
    	navigationAnchor = '#'+ dataElementName;
    }
    
    for (var k=0; k<document.form.elements.length; k++) {
		var nome = document.form.elements[k].name;
		var hiddenInfo = nome!=null ? nome.indexOf("__i_hidden") : -1;
		if (nome && nome.length > 0 && nome != "idhid" && nome != "edit-mode" && hiddenInfo == -1) {
		    var tipo = document.form.elements[k].type;
		    var valore = "";
		    if (tipo == "text" || tipo == "file" || tipo == "hidden" || tipo == "textarea"|| tipo == "number")
		    	valore = document.form.elements[k].value;
		    if (tipo == "select-one") {
		    	for (var j=0; j<document.form.elements[k].options.length; j++)
		    		if (document.form.elements[k].options[j].selected == true)
		    			valore = document.form.elements[k].options[j].value;
		    }
		    if (tipo == "select-multiple") {
				for (var j=0; j<document.form.elements[k].options.length; j++) {
				    if (document.form.elements[k].options[j].selected == true) {
				    	valore = document.form.elements[k].options[j].value;
				    	location += "&" + nome + "=" + encodeURIComponent(valore);
				    }
			    }
		    }
		    if (tipo == "checkbox") {
		    	if (document.form.elements[k].checked)
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

function addHidden(theForm, name, value) {
    // Create a hidden input element, and append it to the form:
    var input = document.createElement('input');
    input.type = 'hidden';
    input.name = name;
    input.value = value;
    theForm.appendChild(input);
}

function postVersion_postBack(dataElementName) {
        
    //aggiungo parametro per indicare che si tratta di postback e azzero idhid
    addHidden(document.form, 'isPostBack' , true);
//    addHidden(document.form, 'edit-mode' , 'in_progress_postback');
    var navigationAnchor = null;
    if(dataElementName!=null){
    	addHidden(document.form, 'postBackElementName' , dataElementName);
    	navigationAnchor = '#'+ dataElementName;
    }
    
    // dump 
    var dump = false;
    
   
	for (var k=0; k<document.form.elements.length; k++) {
		var nome = document.form.elements[k].name;
		
		if(nome == "edit-mode")
			document.form.elements[k].value = 'in_progress_postback';
		
		var tipo = document.form.elements[k].type;
		if (tipo == "checkbox") {
			if (!document.form.elements[k].checked){
  			   addHidden(document.form, nome , 'no');
			}
		}
		
		if(dump) { 
		    var valore = "";
		    if (tipo == "text" || tipo == "file" || tipo == "hidden" || tipo == "textarea" || tipo == "number")
			valore = document.form.elements[k].value;
		    if (tipo == "select-one") {
			for (var j=0; j<document.form.elements[k].options.length; j++)
			    if (document.form.elements[k].options[j].selected == true)
				valore = document.form.elements[k].options[j].value;
		    }
		    if (tipo == "select-multiple") {
				for (var j=0; j<document.form.elements[k].options.length; j++) {
				    if (document.form.elements[k].options[j].selected == true) {
				    	if(valore.length > 0)
				    		valore += ", ";
				    	
				    	valore += document.form.elements[k].options[j].value;
				    }
			    }
		    }
		    if (tipo == "checkbox") {
			if (document.form.elements[k].checked)
			    valore = "yes";
			else
			    valore = "no";
		    }
		   
		    console.log('Input ['+nome+'], Tipo ['+tipo+'], Valore ['+valore+']');
	    }
    }
    
    if(navigationAnchor!=null){
    	 document.form.action=navigationAnchor;
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
