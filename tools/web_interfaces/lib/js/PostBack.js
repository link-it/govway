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
    
    //aggiungo parametro per indicare che si tratta di postback e azzero idhid
    location += "?isPostBack=true&edit-mode=in_progress_postback";
    if(dataElementName!=null)
	location += "&postBackElementName="+dataElementName;
    
    //nn piu necessario xe' devo sempre aggiungere un &
    //var appendAnd = false;
    
    for (var k=0; k<document.form.elements.length; k++) {
		var nome = document.form.elements[k].name;
		if (nome && nome.length > 0 && nome != "idhid" && nome != "edit-mode") {
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
// || (tipo == "file" && valore != "")) {
			
			    //if (appendAnd)
		
			    	location += "&" + nome + "=" + encodeURIComponent(valore);
		
		//		else {
		//		    location += "?" + nome + "=" + encodeURIComponent(valore);
		//		    appendAnd = true;
		//		}
		    }
		}
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
    if(dataElementName!=null)
    	addHidden(document.form, 'postBackElementName' , dataElementName);
    
    // dump 
    var dump = true;
    
   
	for (var k=0; k<document.form.elements.length; k++) {
		var nome = document.form.elements[k].name;
		
		if(nome == "edit-mode")
			document.form.elements[k].value = 'in_progress_postback';
		
		if(dump) { 
		    var tipo = document.form.elements[k].type;
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
    
    // form submit
    document.form.submit();
}	
