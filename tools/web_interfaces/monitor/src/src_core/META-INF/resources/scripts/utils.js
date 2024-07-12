function IEVersione(){
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

function isIE(){
    return IEVersione() > -1;
}


function aggiungiComandoMostraPassword(inputPasswordId){
	
	 // Estrai gli elementi dal DOM
    const passwordInput = document.getElementById(inputPasswordId);
    const parent = passwordInput.parentNode;
    const spanId = inputPasswordId + '_eye_span';
    const iconId = inputPasswordId + '_eye';
    const jQueryIconId = '#' + iconId;
    
    const iconVisibility = 'visibility';
    const iconVisibilityTooltip = 'Mostra';
    const iconVisibilityOff = 'visibility_off';
    const iconVisibilityOffTooltip = 'Nascondi';
    
    const firstParagraph = parent.querySelector('p');
    
    if(firstParagraph) {
		// Sgancia firstP dal parent
    	parent.removeChild(firstParagraph);
	}

	// Crea il div esterno
    const divEsterno = document.createElement("div");
	divEsterno.className = "lock-container";
	
	// Crea il div interno
    const divInterno = document.createElement("div");
	divInterno.className = "lock-input-container";
	divEsterno.appendChild(divInterno);
	
    // Appendi divEsterno al posto di passwordInput
    parent.appendChild(divEsterno);
    
    // Sgancia passwordInput dal parent
    parent.removeChild(passwordInput);
		
	// appendi passwordInput a divInterno
	divInterno.appendChild(passwordInput);
	
	 if(firstParagraph) {
		// ripristino firstP nel parent
    	parent.appendChild(firstParagraph);
	}

    // Crea lo span che conterrà l'icona dell'occhio
    const span = document.createElement("span");
    span.id = spanId;
    span.className = "lock-span-comandi-input";
    divInterno.appendChild(span);

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
      var tooltip = passwordInput.type === "password" ? iconVisibilityTooltip : iconVisibilityOffTooltip;
      jQuery(this).attr('title', tooltip);
      
    });
}
