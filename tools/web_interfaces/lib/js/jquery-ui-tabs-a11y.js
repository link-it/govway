/*
 * Adattamento di accessibilita' del widget 'tabs' di jQuery UI.
 *
 * Dalla versione 1.12 jQuery UI assegna role="tab" all'elemento <li> e vi lascia dentro l'ancora
 * originale, resa non tabulabile con tabindex="-1". Il risultato viola il criterio per cui un
 * controllo interattivo non deve contenere altri elementi focalizzabili: il tabindex negativo non
 * e' sufficiente, perche' le tecnologie assistive raggiungono comunque l'elemento, e uno screen
 * reader annuncia un collegamento dentro ogni scheda.
 *
 * L'ancora serve soltanto durante il riconoscimento dei tab, che il widget effettua con il
 * selettore "> li:has(a[href])" dentro _processTabs; dopo, il pannello viene risolto tramite
 * l'attributo aria-controls dell'<li> ed i gestori di evento sono gia' registrati. Qui l'href
 * viene quindi conservato in un data attribute, ripristinato prima di ogni _processTabs e rimosso
 * subito dopo, insieme al tabindex.
 *
 * ATTENZIONE: i due metodi sovrascritti sono interni al widget (prefisso underscore). Ad ogni
 * aggiornamento di jQuery UI questo file va riverificato. Un'eventuale rottura e' comunque
 * evidente: senza _isLocal il primo click solleva un'eccezione, senza _processTabs la barra delle
 * schede non viene costruita.
 */
(function($) {
	'use strict';

	if (!$ || !$.ui || !$.ui.tabs || !$.ui.tabs.prototype) {
		return;
	}

	var prototipo = $.ui.tabs.prototype;
	var ATTRIBUTO_HREF = 'data-gw-tab-href';

	// Un'ancora priva di href non ha nulla da caricare da remoto: e' locale per definizione.
	// Senza questa verifica load() valuta 'new URL(anchor.href)' su una stringa vuota e solleva.
	var isLocalOriginale = prototipo._isLocal;
	prototipo._isLocal = function(anchor) {
		if (!anchor || !anchor.getAttribute('href')) {
			return true;
		}
		return isLocalOriginale.apply(this, arguments);
	};

	var processTabsOriginale = prototipo._processTabs;
	prototipo._processTabs = function() {
		// ripristino l'href prima del riconoscimento dei tab, cosi' funziona anche su refresh()
		this.element.find('a[' + ATTRIBUTO_HREF + ']').each(function() {
			var ancora = $(this);
			ancora.attr('href', ancora.attr(ATTRIBUTO_HREF));
		});

		processTabsOriginale.apply(this, arguments);

		// da qui in avanti l'ancora non serve piu': la si rende non focalizzabile
		this.anchors.each(function() {
			var ancora = $(this);
			ancora.attr(ATTRIBUTO_HREF, ancora.attr('href')).removeAttr('href').removeAttr('tabindex');
		});
	};

})(jQuery);
