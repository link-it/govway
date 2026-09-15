function IEVersione(){
    var ua = window.navigator.userAgent;

    var msie = ua.indexOf('MSIE ');
    if (msie > 0) {
      // IE 10 or older => return version number
      return Number.parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
    }

    var trident = ua.indexOf('Trident/');
    if (trident > 0) {
      // IE 11 => version
      var rv = ua.indexOf('rv:');
      return Number.parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
    }

    var edge = ua.indexOf('Edge/');
    if (edge > 0) {
      // Edge (IE 12+) => version
      return Number.parseInt(ua.substring(edge + 5, ua.indexOf('.', edge)), 10);
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
      jQuery(this).prop('title', tooltip);
      
    });
}


/* L'ultima voce del breadcrumb e' il titolo della pagina, ma e' resa come semplice
   testo: JSF non consente di aggiungere l'attributo 'role' a un h:outputText, quindi
   la semantica di intestazione viene applicata qui. */
function gwApplicaTitoloPagina(){

	const titoli = document.querySelectorAll('.ultimo-path:not([role])');

	for(let i = 0; i < titoli.length; i++){
		titoli[i].setAttribute('role', 'heading');
		titoli[i].setAttribute('aria-level', '1');
	}
}

/* Radio e checkbox sono esclusi: appartengono a gruppi in cui ogni opzione ha gia' la
   propria etichetta, e puntare l'etichetta del blocco alla prima opzione assegnerebbe
   due etichette allo stesso controllo. Sono esclusi anche i pulsanti, che prendono il
   nome dal proprio 'value'.

   NOTA: questo file viene rivalutato dal meccanismo AJAX, quindi non puo' contenere
   dichiarazioni non ripetibili a livello globale: un 'const' qui provocherebbe
   'Identifier has already been declared' e l'intera seconda valutazione verrebbe
   abortita. Da qui la funzione anziche' la costante. */
function gwSelettoreControlliEtichettabili(){
	return 'input:not([type=hidden]):not([type=button]):not([type=submit]):not([type=image]):not([type=radio]):not([type=checkbox]):not([aria-hidden="true"]), select, textarea';
}

function gwControlloSenzaNome(controllo){

	if(controllo.getAttribute('aria-label') || controllo.getAttribute('aria-labelledby')){
		return false;
	}
	if(controllo.labels && controllo.labels.length > 0){
		return false;
	}
	return true;
}

/* I blocchi 'prop' del taglib dell'applicazione rendono un <label> e, subito dopo, il
   controllo, ma senza associarli. Non si puo' fare con 'h:outputLabel for', che
   risolverebbe al client id del componente: per una rich:comboBox e' il <div> esterno,
   non il campo di testo al suo interno. L'associazione viene quindi stabilita qui,
   sull'id effettivo del primo controllo del blocco. */
function gwAssociaEtichette(){

	const blocchi = document.querySelectorAll('div.prop');

	for(let i = 0; i < blocchi.length; i++){

		const blocco = blocchi[i];

		let etichetta = null;
		for(let j = 0; j < blocco.children.length; j++){
			if(blocco.children[j].tagName === 'LABEL'){
				etichetta = blocco.children[j];
				break;
			}
		}

		if(!etichetta || etichetta.getAttribute('for')){
			continue;
		}

		const controllo = blocco.querySelector(gwSelettoreControlliEtichettabili());

		if(!controllo || !controllo.id || !gwControlloSenzaNome(controllo)){
			continue;
		}

		etichetta.setAttribute('for', controllo.id);
	}
}

/* Fuori dai blocchi 'prop' etichetta e controllo stanno in celle adiacenti di una
   tabella di impaginazione: e' il caso dello spinner 'Numero Risultati' nei grafici.
   L'associazione viene stabilita solo quando nell'ambito considerato c'e' un unico
   controllo privo di nome, cosi' non si creano abbinamenti arbitrari. */
function gwAssociaEtichetteAdiacenti(){

	const etichette = document.querySelectorAll('label:not([for])');

	for(let i = 0; i < etichette.length; i++){

		const etichetta = etichette[i];

		if(etichetta.closest('div.prop')){
			continue; // gia' trattata da gwAssociaEtichette
		}

		const ambito = etichetta.closest('tr') || etichetta.parentElement;
		if(!ambito){
			continue;
		}

		const candidati = [];
		const controlli = ambito.querySelectorAll(gwSelettoreControlliEtichettabili());
		for(let j = 0; j < controlli.length; j++){
			if(!gwControlloSenzaNome(controlli[j])){
				continue;
			}
			candidati.push(controlli[j]);
		}

		if(candidati.length !== 1 || !candidati[0].id){
			continue;
		}

		etichetta.setAttribute('for', candidati[0].id);
	}
}

/* Alcuni controlli non hanno un'etichetta visibile per come e' impaginata la pagina:
   la 'prop' che li contiene ha etichetta vuota per allineare il campo agli altri,
   oppure il controllo e' reso da solo nel piede della tabella. Per questi il nome va
   dichiarato, con la stessa dicitura che l'applicazione usa altrove. La chiave e' il
   suffisso dell'id, per non dipendere dal prefisso del contenitore JSF. */
function gwNomiDichiarati(){
	return [
		{ suffisso: 'rowsToDisplayCombocomboboxField',          nome: 'Elementi per pagina' },
		{ suffisso: 'NumeroDimensioniCombocomboboxField',       nome: 'Numero Dimensioni' },
		{ suffisso: 'NumeroDimensioniCustomCombocomboboxField', nome: 'Numero Dimensioni' },
		/* Il menu dell'utente in testata mostra solo un'icona: con il ruolo 'button' che
		   la libreria gli assegna per renderlo utilizzabile da tastiera, resterebbe un
		   comando senza nome. */
		{ suffisso: 'menuUtente',                               nome: 'Menu utente' }
	];
}

function gwApplicaNomiDichiarati(){

	const dichiarati = gwNomiDichiarati();

	for(let i = 0; i < dichiarati.length; i++){

		const controlli = document.querySelectorAll('[id$="' + dichiarati[i].suffisso + '"]');

		for(let j = 0; j < controlli.length; j++){
			if(!gwControlloSenzaNome(controlli[j])){
				continue;
			}
			controlli[j].setAttribute('aria-label', dichiarati[i].nome);
		}
	}
}

/* La maniglia dello slider e' un elemento con ruolo 'slider', non un controllo di
   form: un '<label for>' non puo' puntarla, perche' 'for' vale solo per gli elementi
   etichettabili. Il nome viene quindi collegato con 'aria-labelledby' all'etichetta
   che si trova nello stesso ambito, con la stessa condizione di sicurezza usata per
   i controlli: una sola etichetta. */
function gwAssociaNomiSlider(){

	const maniglie = document.querySelectorAll('[role="slider"]:not([aria-label]):not([aria-labelledby])');

	for(let i = 0; i < maniglie.length; i++){

		const maniglia = maniglie[i];

		/* La maniglia sta annidata nella tabella interna dello slider, mentre
		   l'etichetta e' in una cella della riga che contiene lo slider: la riga piu'
		   vicina non ne contiene alcuna. Si risale di riga in riga finche' se ne
		   trova una con una sola etichetta, con un limite alla risalita. */
		let etichetta = null;
		let ambito = maniglia.closest('tr');

		for(let livello = 0; livello < 4 && ambito; livello++){
			const trovate = ambito.querySelectorAll('label');
			if(trovate.length === 1){
				etichetta = trovate[0];
				break;
			}
			if(trovate.length > 1){
				break; // ambito troppo ampio: meglio nessun nome che uno sbagliato
			}
			ambito = ambito.parentElement ? ambito.parentElement.closest('tr') : null;
		}

		if(!etichetta){
			continue;
		}
		if(!etichetta.id){
			// id derivato da quello della maniglia, cosi' resta stabile fra i re-render
			etichetta.id = (maniglia.id || ('gw-slider-' + i)) + '-etichetta';
		}

		maniglia.setAttribute('aria-labelledby', etichetta.id);
	}
}

/* I comandi di selezione sotto le liste — «Seleziona tutti gli elementi visualizzati»,
   «Seleziona i primi N elementi», «Annulla selezione» — sono resi come '<a href="#">' ma
   non navigano: agiscono sulla pagina. Da tastiera Invio li attivava, la barra
   spaziatrice no, perche' su un collegamento fa scorrere la pagina invece di attivare.
   Vengono quindi dichiarati per quello che sono, pulsanti, e la barra spaziatrice ne
   emette il clic. */
function gwPulsantiDiSelezione(){

	const comandi = document.querySelectorAll('a.selectionInfoItem[href="#"]:not([role])');

	for(let i = 0; i < comandi.length; i++){

		const comando = comandi[i];
		comando.setAttribute('role', 'button');

		comando.addEventListener('keydown', function(evento) {
			const tasto = evento.keyCode || evento.which;
			if(tasto !== 32){   // SPAZIO: Invio lo gestisce gia' il collegamento
				return;
			}
			evento.preventDefault();
			comando.click();
		});
	}
}

/* Le tabelle rendono il piede come '<tfoot>' collocato PRIMA del '<tbody>', come
   richiedeva HTML 4. Il browser lo disegna in basso, ma l'ordine di tabulazione segue
   quello del documento: i comandi del piede — «Seleziona tutti gli elementi
   visualizzati», «Seleziona i primi N elementi», «Annulla selezione», la dimensione
   pagina — si incontravano quindi PRIMA delle righe, e partendo da una riga non si
   raggiungevano piu' andando avanti con Tab (WCAG 2.4.3, Ordine del focus).

   Spostare il '<tfoot>' in fondo alla tabella e' valido in HTML5 e non cambia la resa:
   il piede resta disegnato in basso. Lo spostamento e' idempotente, e viene evitato se
   il fuoco si trova dentro il piede, per non perderlo. */
function gwOrdinaPiediTabella(){

	const tabelle = document.querySelectorAll('table');

	for(let i = 0; i < tabelle.length; i++){

		const tabella = tabelle[i];
		const piede = tabella.tFoot;

		if(!piede || piede === tabella.lastElementChild){
			continue;
		}
		if(document.activeElement && piede.contains(document.activeElement)){
			continue;
		}

		tabella.appendChild(piede);
	}
}

/* Testo del suggerimento associato a un elemento, se ne ha uno. Il componente tiene
   sul nodo del suggerimento la propria istanza, con l'id dell'elemento che lo mostra:
   e' l'unico modo esatto di risalire dall'uno all'altro. Il contenuto e' preso dal
   riquadro vero e proprio, ripulito dagli script che il componente vi annida. */
function gwTestoSuggerimento(elemento){

	if(!elemento.id){
		return null;
	}

	const suggerimenti = document.querySelectorAll('.rich-tool-tip');

	for(let i = 0; i < suggerimenti.length; i++){

		const istanza = suggerimenti[i].component;
		if(!istanza || istanza.parentId !== elemento.id){
			continue;
		}

		const contenuto = document.getElementById(suggerimenti[i].id + 'content') || suggerimenti[i];
		const copia = contenuto.cloneNode(true);
		const nonTesto = copia.querySelectorAll('script, style');
		for(let j = 0; j < nonTesto.length; j++){
			nonTesto[j].remove();
		}

		const testo = (copia.textContent || '').replace(/\s+/g, ' ').trim();
		return testo || null;
	}

	return null;
}

/* I comandi nella barra del filtro di ricerca — «Visualizza Filtri di Ricerca»,
   «Nascondi Filtri di Ricerca», «Applica nuovamente i Filtri di Ricerca» — sono icone
   dentro l'intestazione di un pannello richiudibile: il clic funziona perche' risale
   all'intestazione, ma non hanno ne' ruolo ne' 'tabindex' e da tastiera non si
   raggiungono (WCAG 2.1.1).

   Il nome lo fornisce il suggerimento che gia' li accompagna, quindi non c'e' nulla da
   tradurre qui: un comando senza suggerimento viene lasciato stare, perche' sarebbe un
   pulsante muto. Lo stato aperto/chiuso si dichiara solo quando la zona contiene un
   unico comando; se ne contiene piu' d'uno, ad annunciare cosa succede bastano i nomi,
   che sono gia' distinti. */
function gwComandiBarraFiltro(){

	const zone = document.querySelectorAll('.rich-stglpnl-marker');

	for(let i = 0; i < zone.length; i++){

		const zona = zone[i];
		const candidati = zona.querySelectorAll('[id]');
		const comandi = [];

		for(let j = 0; j < candidati.length; j++){
			const nome = gwTestoSuggerimento(candidati[j]);
			if(nome){
				comandi.push({ elemento: candidati[j], nome: nome });
			}
		}

		for(let j = 0; j < comandi.length; j++){

			const comando = comandi[j].elemento;
			if(comando.getAttribute('data-gw-comando-barra') === 'si'){
				continue;
			}

			comando.setAttribute('data-gw-comando-barra', 'si');
			comando.setAttribute('role', 'button');
			comando.setAttribute('tabindex', '0');
			comando.setAttribute('aria-label', comandi[j].nome);

			if(comandi.length === 1){
				comando.setAttribute('aria-expanded', zona.id.endsWith('_switch_on') ? 'true' : 'false');
			}

			comando.addEventListener('keydown', function(evento) {
				const tasto = evento.keyCode || evento.which;
				if(tasto !== 13 && tasto !== 32){
					return;
				}
				evento.preventDefault();
				this.click();
			});
		}
	}
}

/* Le icone material sono realizzate con un carattere iconografico: il testo dell'elemento
   e' il nome della legatura ('hourglass_empty', 'check', 'search'), che il browser include
   nel testo accessibile. Cosi' il nome della riga di una transazione diventava «... Fruizione
   GovWayRUN@ENTE hourglass_empty check Data: ...», e uno screen reader legge nomi di icone
   in inglese al posto di nulla.

   I glifi vengono quindi nascosti alle tecnologie assistive. Sono decorativi: il significato
   e' sempre scritto accanto (la durata, l'esito, l'etichetta della voce di menu). Restano
   pienamente visibili, e nulla cambia per chi usa il mouse.

   Un glifo che fosse l'unico contenuto di un comando ne diventerebbe l'unico nome, e
   nasconderlo lo lascerebbe muto: quel caso viene escluso. */
function gwGlifiDecorativi(){

	const glifi = document.querySelectorAll('i.material-icons:not([aria-hidden]), i.material-symbols-outlined:not([aria-hidden])');

	for(let i = 0; i < glifi.length; i++){

		const glifo = glifi[i];
		const comando = glifo.closest('a, button, [role="button"], [role="link"]');

		if(comando && !gwHaAltroTesto(comando, glifo)){
			continue;   // e' l'unico nome del comando: nasconderlo lo renderebbe muto
		}

		glifo.setAttribute('aria-hidden', 'true');
	}
}

/* Vero se il comando conserva del testo anche togliendo il glifo indicato. */
function gwHaAltroTesto(comando, glifo){

	const copia = comando.cloneNode(true);

	const glifiCopia = copia.querySelectorAll('i.material-icons, i.material-symbols-outlined');
	for(let i = 0; i < glifiCopia.length; i++){
		glifiCopia[i].remove();
	}
	/* script e style stanno nel DOM ma non sono testo letto */
	const nonTesto = copia.querySelectorAll('script, style');
	for(let i = 0; i < nonTesto.length; i++){
		nonTesto[i].remove();
	}

	if((copia.textContent || '').trim()){
		return true;
	}
	/* nessun testo proprio: puo' avere un nome dichiarato, o un'immagine con alt */
	if(comando.getAttribute('aria-label') || comando.getAttribute('aria-labelledby') || comando.getAttribute('title')){
		return true;
	}
	const immagini = copia.querySelectorAll('img[alt]');
	for(let i = 0; i < immagini.length; i++){
		if((immagini[i].getAttribute('alt') || '').trim()){
			return true;
		}
	}
	return false;
}

/* Le righe dell'elenco delle transazioni aprono il dettaglio con un gestore di clic
   attaccato al '<div>' della riga, che non ha ne' ruolo ne' 'tabindex': da tastiera il
   dettaglio non si raggiunge, e con Tab si passa dalla casella di spunta di una riga a
   quella della successiva (WCAG 2.1.1).

   La riga viene resa un collegamento: il nome glielo danno i suoi stessi contenuti, che
   sono quanto uno screen reader leggerebbe comunque. Si attiva con Invio e non con la
   barra spaziatrice: e' il comportamento dei collegamenti, e in un elenco lungo la barra
   deve continuare a scorrere la pagina. */
function gwRigheOperabili(){

	const righe = document.querySelectorAll('.rowTransazione:not([data-gw-riga-tastiera])');

	for(let i = 0; i < righe.length; i++){

		const riga = righe[i];

		riga.setAttribute('data-gw-riga-tastiera', 'si');
		riga.setAttribute('role', 'link');
		riga.setAttribute('tabindex', '0');

		riga.addEventListener('keydown', function(evento) {
			const tasto = evento.keyCode || evento.which;
			if(tasto !== 13){   // INVIO
				return;
			}
			evento.preventDefault();
			riga.click();
		});
	}
}

function gwApplicaCorrezioniAccessibilita(){

	gwApplicaTitoloPagina();
	/* prima dei due passaggi: cosi' un controllo con nome dichiarato risulta gia'
	   nominato e nessuna etichetta vuota gli viene associata */
	gwApplicaNomiDichiarati();
	gwAssociaEtichette();
	gwAssociaEtichetteAdiacenti();
	gwAssociaNomiSlider();
	gwPulsantiDiSelezione();
	gwGlifiDecorativi();
	gwComandiBarraFiltro();
	gwRigheOperabili();
	gwOrdinaPiediTabella();
}

/* I re-render AJAX sostituiscono interi rami del DOM, azzerando quanto applicato
   sopra: l'osservatore lo rimette, accorpando le notifiche in un solo passaggio per
   fotogramma. */
function gwOsservaCorrezioniAccessibilita(){

	gwApplicaCorrezioniAccessibilita();

	if(!window.MutationObserver){
		return;
	}

	let inAttesa = false;

	const osservatore = new MutationObserver(function() {
		if(inAttesa){
			return;
		}
		inAttesa = true;
		window.requestAnimationFrame(function() {
			inAttesa = false;
			gwApplicaCorrezioniAccessibilita();
		});
	});

	osservatore.observe(document.body, { childList: true, subtree: true });
}

jQuery(document).ready(gwOsservaCorrezioniAccessibilita);
