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
    // document.form.action vale http://localhost:8080/pddConsole/servlet.do
    // A me serve solo il pezzo /pddConsole/servlet.do
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
		if (nome.length > 0 && nome != "idhid") {
		    var tipo = document.form.elements[k].type;
		    var valore = "";
		    if (tipo == "text" || tipo == "file" || tipo == "hidden" || tipo == "textarea")
			valore = document.form.elements[k].value;
		    if (tipo == "select-one") {
			for (var j=0; j<document.form.elements[k].options.length; j++)
			    if (document.form.elements[k].options[j].selected == true)
				valore = document.form.elements[k].options[j].value;
		    }
		    if (tipo == "checkbox") {
			if (document.form.elements[k].checked)
			    valore = "yes";
			else
			    valore = "no";
		    }
		    if (tipo == "text" || tipo == "hidden" || tipo == "select-one" || tipo == "checkbox" || tipo == "textarea") {
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
