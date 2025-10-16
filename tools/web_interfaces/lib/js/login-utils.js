/**
 * Funzioni di utilita' 
 * 
 * @author Giuliano Pintori <pintori@link.it>
 */

/* Funzioni di utilita' utilizzate nelle pagine login.jsp, logout.jsp e nelle pagine custom loginAS.jsp, logoutAS.jsp e loginFailure.jsp */

var ok = true;
function white(str){
  for(var n=0; n<str.length; n++){
    if (str.charAt(n) == " "){
      ok = false;
    }
  }
};

function CheckDati() {
	document.form.submit();
};

function inizializzaSelectSezione(idDiv){};
