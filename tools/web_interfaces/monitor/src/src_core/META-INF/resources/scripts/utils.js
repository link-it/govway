function getIEVersion() {
	var ua = window.navigator.userAgent;

	var msie = ua.indexOf('MSIE ');
	if (msie > 0) {
		// IE 10 or older => return version number
		return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
	}

	var trident = ua.indexOf('Trident/');
	if (trident > 0) {
		// IE 11 => version
		var rv = ua.indexOf('rv:');
		return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
	}

	var edge = ua.indexOf('Edge/');
	if (edge > 0) {
		// Edge (IE 12+) => version
		return parseInt(ua.substring(edge + 5, ua.indexOf('.', edge)), 10);
	}

	return -1;
}

function aggiungiComandoMostraPassword(inputPasswordId){
	
	 // Estrai gli elementi dal DOM
    const passwordInput = document.getElementById(inputPasswordId);
    const spanId = inputPasswordId + '_eye_span';
    const iconId = inputPasswordId + '_eye';
    const jQueryIconId = '#' + iconId;
    
    const iconVisibility = 'visibility';
    const iconVisibilityOff = 'visibility_off';

    // Crea lo span che conterrà l'icona dell'occhio
    const span = document.createElement("span");
    span.id = spanId;
    span.className = "span-password-eye";
    passwordInput.parentNode.insertBefore(span, passwordInput.nextSibling);
//    passwordInput.parentNode.appendChild(span);

    // Crea l'icona dell'occhio
    const eyeIcon = document.createElement("i");
    eyeIcon.id = iconId;
    eyeIcon.className = "material-icons md-24";
    eyeIcon.textContent = iconVisibility;
    span.appendChild(eyeIcon);

    // Gestisci il clic sull'icona dell'occhio
    jQuery(jQueryIconId).click(function() {
      // toggle the type attribute
      passwordInput.type = passwordInput.type === "password" ? "text" : "password";

      // toggle the eye slash icon
      jQuery(this).text(passwordInput.type === 'password' ? iconVisibility : iconVisibilityOff);
    });
}