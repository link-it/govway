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
  white(document.form.login.value);
  white(document.form.password.value);
  if (ok == false) {
    ok = true;
    var win = window.open("?op=alert&msg=NoSpace", "winAlert", "width=200,height=130");
    win.focus();
    return false;
  } else { document.form.submit(); }
};