/*
 * Adattamento di accessibilita' del widget 'datepicker' di jQuery UI.
 *
 * Il calendario si apre quando il campo riceve il fuoco, ma da tastiera resta inutilizzabile:
 * le frecce da sole non muovono nulla, perche' jQuery UI le riserva alla combinazione con Ctrl
 * (Ctrl+frecce per giorno e settimana). E' una convenzione che nessuno conosce e che non e'
 * quella prevista per i selettori di date, dove le frecce si usano nude. Il pannello, inoltre,
 * non dichiara niente: nessun ruolo di finestra di dialogo, nessuna griglia, celle senza nome.
 * Uno screen reader annuncia una tabella di numeri e nessuno spostamento (WCAG 2.1.1, 4.1.2).
 *
 * Qui le frecce fanno quello che ci si aspetta, delegando agli stessi metodi interni che il
 * widget usa gia' per Ctrl+frecce: nessuna aritmetica di date viene riscritta, e restano valide
 * le date minime e massime configurate. Il pannello diventa una finestra di dialogo con una
 * griglia, le cui celle portano la data per esteso, e ogni spostamento viene annunciato in una
 * regione 'live'. Le combinazioni originali continuano a funzionare.
 *
 * Il comportamento col mouse non cambia in alcun modo.
 *
 * ATTENZIONE: i metodi sovrascritti sono interni al widget (prefisso underscore) e vanno
 * riverificati ad ogni aggiornamento di jQuery UI. Vanno inoltre sovrascritti PRIMA della prima
 * chiamata a .datepicker(): il widget registra il gestore della tastiera per riferimento, quindi
 * un campo gia' inizializzato conserverebbe quello originale.
 */
(function ($) {
	'use strict';

	if (!$ || !$.datepicker || !$.datepicker.constructor) {
		return;
	}

	var ETICHETTA_PANNELLO = 'Calendario';
	var ETICHETTA_MESE_PRECEDENTE = 'Mese precedente';
	var ETICHETTA_MESE_SUCCESSIVO = 'Mese successivo';
	var ETICHETTA_MESE = 'Mese';
	var ETICHETTA_ANNO = 'Anno';
	var ID_ANNUNCI = 'gw-datepicker-annuncio';
	var prototipo = $.datepicker.constructor.prototype;

	/* Regione 'live' fuori dallo schermo ma non nascosta alle tecnologie assistive:
	   'display:none' e 'visibility:hidden' non verrebbero letti. */
	function regioneAnnunci() {
		var regione = document.getElementById(ID_ANNUNCI);
		if (!regione) {
			regione = document.createElement('div');
			regione.id = ID_ANNUNCI;
			regione.setAttribute('role', 'status');
			regione.setAttribute('aria-live', 'polite');
			regione.style.cssText = 'position:absolute;width:1px;height:1px;margin:-1px;padding:0;' +
					'overflow:hidden;clip:rect(0 0 0 0);white-space:nowrap;border:0;';
			document.body.appendChild(regione);
		}
		return regione;
	}

	function annuncia(testo) {
		if (!testo) {
			return;
		}
		var regione = regioneAnnunci();
		/* svuotare prima costringe la tecnologia assistiva a rileggere anche un testo uguale,
		   caso frequente quando si torna sullo stesso giorno */
		regione.textContent = '';
		window.setTimeout(function () { regione.textContent = testo; }, 30);
	}

	function nomiDeiMesi(inst) {
		var impostazioni = (inst && inst.settings && inst.settings.monthNames) || $.datepicker._defaults.monthNames;
		return impostazioni || [];
	}

	function nomeDellaData(inst, giorno) {
		var mesi = nomiDeiMesi(inst);
		var mese = mesi[inst.drawMonth] !== undefined ? mesi[inst.drawMonth] : (inst.drawMonth + 1);
		return giorno + ' ' + mese + ' ' + inst.drawYear;
	}

	function decoraPannello(inst) {
		if (!inst || !inst.dpDiv || !inst.dpDiv.length) {
			return;
		}
		var pannello = inst.dpDiv[0];
		pannello.setAttribute('role', 'dialog');
		pannello.setAttribute('aria-label', ETICHETTA_PANNELLO);

		/* Le frecce di mese sono <a> SENZA href: non sono focalizzabili, quindi col Tab non ci si
		   arriva. Diventano comandi veri. Le due select hanno un nome in inglese ('Select month'),
		   scritto dal widget: lo si sostituisce con quello della lingua dell'interfaccia. */
		var nomi = [['.ui-datepicker-prev', ETICHETTA_MESE_PRECEDENTE], ['.ui-datepicker-next', ETICHETTA_MESE_SUCCESSIVO],
					['select.ui-datepicker-month', ETICHETTA_MESE], ['select.ui-datepicker-year', ETICHETTA_ANNO]];
		nomi.forEach(function (coppia) {
			var e = pannello.querySelector(coppia[0]);
			if (!e) return;
			e.setAttribute('aria-label', coppia[1]);
			if (e.tagName === 'A') {
				e.setAttribute('role', 'button');
				e.setAttribute('tabindex', '0');
			}
		});

		var tabella = pannello.querySelector('table.ui-datepicker-calendar');
		if (!tabella) {
			return;
		}
		tabella.setAttribute('role', 'grid');
		$(tabella).find('tr').attr('role', 'row');
		$(tabella).find('thead th').attr('role', 'columnheader');
		$(tabella).find('tbody td').each(function () {
			this.setAttribute('role', 'gridcell');
			var giorno = this.querySelector('a');
			if (!giorno) {
				/* celle vuote di inizio e fine mese, e colonna del numero di settimana */
				this.setAttribute('aria-disabled', 'true');
				this.removeAttribute('aria-selected');
				return;
			}
			this.removeAttribute('aria-disabled');
			/* i giorni non sono fermate di tabulazione: ci si muove con le frecce, restando
			   sul campo, e il pannello non aggiunge quaranta fermate alla pagina */
			giorno.setAttribute('tabindex', attivo(this) ? '0' : '-1');
			giorno.setAttribute('aria-label', nomeDellaData(inst, giorno.textContent));
			if ($(this).hasClass('ui-datepicker-today')) {
				giorno.setAttribute('aria-current', 'date');
			} else {
				giorno.removeAttribute('aria-current');
			}
			this.setAttribute('aria-selected', $(this).hasClass('ui-datepicker-current-day') ? 'true' : 'false');
		});
	}

	/* Il giorno "attivo" e' quello su cui si e' arrivati con le frecce; in mancanza, quello scelto;
	   in mancanza di entrambi, oggi. E' l'unica cella nell'ordine di tabulazione. */
	function attivo(cella) {
		var c = cella.classList;
		return c.contains('ui-datepicker-days-cell-over') || c.contains('ui-datepicker-current-day') ||
			(c.contains('ui-datepicker-today') && !cella.closest('table').querySelector('.ui-datepicker-days-cell-over, .ui-datepicker-current-day'));
	}

	function cellaAttiva(pannello) {
		return pannello.querySelector('td.ui-datepicker-days-cell-over a, td.ui-datepicker-current-day a, td.ui-datepicker-today a, td a');
	}

	function annunciaGiornoCorrente(campo) {
		if (!$.datepicker._datepickerShowing) {
			return;
		}
		var inst = $.datepicker._getInst(campo);
		if (inst) {
			annuncia(nomeDellaData(inst, inst.selectedDay));
		}
	}

	var mostraOriginale = prototipo._showDatepicker;
	prototipo._showDatepicker = function (input) {
		var esito = mostraOriginale.apply(this, arguments);
		var campo = (input && input.target) ? input.target : input;
		if (campo && campo.setAttribute) {
			var inst = $.datepicker._getInst(campo);
			/* 'aria-expanded' e 'aria-haspopup' non sono ammessi su una casella di testo semplice
			   (axe: aria-allowed-attr). Il campo, con un pannello che si apre e una griglia da cui
			   scegliere, e' a tutti gli effetti un combobox: lo si dichiara, e gli attributi
			   diventano quelli propri del ruolo. */
			campo.setAttribute('role', 'combobox');
			campo.setAttribute('aria-haspopup', 'dialog');
			campo.setAttribute('aria-expanded', 'true');
			if (inst && inst.dpDiv && inst.dpDiv.length && inst.dpDiv[0].id) {
				campo.setAttribute('aria-controls', inst.dpDiv[0].id);
			}
			decoraPannello(inst);
			impiantoPannello(inst);
			annunciaGiornoCorrente(campo);
		}
		return esito;
	};

	var nascondiOriginale = prototipo._hideDatepicker;
	prototipo._hideDatepicker = function () {
		var campo = $.datepicker._lastInput;
		var esito = nascondiOriginale.apply(this, arguments);
		if (campo && campo.setAttribute) {
			campo.setAttribute('aria-expanded', 'false');
		}
		return esito;
	};

	/* Ogni ridisegno (cambio di mese, spostamento del giorno) ricostruisce la tabella:
	   ruoli e nomi vanno rimessi. */
	var aggiornaOriginale = prototipo._updateDatepicker;
	prototipo._updateDatepicker = function (inst) {
		var esito = aggiornaOriginale.apply(this, arguments);
		decoraPannello(inst);
		return esito;
	};

	/* jQuery UI appende il pannello in fondo al <body>: nell'ordine del documento viene dopo tutto
	   il resto, quindi col Tab dal campo si finisce nel campo seguente e ai comandi del calendario
	   non ci si arriva mai. Il fuoco lo si governa a mano, come in una finestra di dialogo:
	   campo -> mese precedente -> mese -> anno -> mese successivo -> giorno -> campo. */
	function comandiInOrdine(pannello) {
		var lista = ['.ui-datepicker-prev', 'select.ui-datepicker-month', 'select.ui-datepicker-year', '.ui-datepicker-next']
			.map(function (sel) { return pannello.querySelector(sel); })
			.filter(function (e) { return e && !e.classList.contains('ui-state-disabled'); });
		var giorno = cellaAttiva(pannello);
		if (giorno) lista.push(giorno);
		return lista;
	}

	function spostaNelPannello(pannello, campo, indietro) {
		var lista = comandiInOrdine(pannello);
		if (!lista.length) return false;
		var posizione = lista.indexOf(document.activeElement);
		if (posizione === -1) {                       // si arriva dal campo
			(indietro ? lista[lista.length - 1] : lista[0]).focus();
			return true;
		}
		var prossimo = posizione + (indietro ? -1 : 1);
		if (prossimo < 0 || prossimo >= lista.length) { campo.focus(); return true; }   // si torna al campo
		lista[prossimo].focus();
		return true;
	}

	function impiantoPannello(inst) {
		var pannello = inst && inst.dpDiv && inst.dpDiv[0];
		if (!pannello || pannello.getAttribute('data-gw-tastiera')) return;
		pannello.setAttribute('data-gw-tastiera', 'si');
		pannello.addEventListener('keydown', function (evento) {
			var campo = $.datepicker._lastInput;
			if (!campo || !$.datepicker._datepickerShowing) return;
			var suGiorno = document.activeElement && document.activeElement.closest &&
					document.activeElement.closest('td') && document.activeElement.tagName === 'A';
			var passo = null;
			switch (evento.keyCode) {
				case 9:  /* TAB */
					if (spostaNelPannello(pannello, campo, evento.shiftKey)) {
						evento.preventDefault(); evento.stopPropagation();
					}
					return;
				case 27: /* ESC */
					/* prima il fuoco, poi la chiusura: il campo apre il calendario quando riceve
					   il fuoco, quindi chiudendo per primi lo si riaprirebbe subito dopo */
					campo.focus();
					$.datepicker._hideDatepicker();
					evento.preventDefault(); evento.stopPropagation();
					return;
				case 13: /* INVIO */
				case 32: /* SPAZIO */
					if (document.activeElement && document.activeElement.matches('.ui-datepicker-prev, .ui-datepicker-next')) {
						document.activeElement.click();     // <a> senza href: il clic va scatenato
						evento.preventDefault(); evento.stopPropagation();
						/* il pannello e' stato ridisegnato: il fuoco torna sul comando equivalente */
						var stesso = pannello.querySelector(document.activeElement ? '.ui-datepicker-prev' : '.ui-datepicker-prev');
						if (stesso) stesso.focus();
					}
					return;
				case 37: passo = -1; break;
				case 39: passo = 1; break;
				case 38: passo = -7; break;
				case 40: passo = 7; break;
				default: return;
			}
			if (!suGiorno) return;                       // sulle select le frecce sono loro
			var inst2 = $.datepicker._getInst(campo);
			if (inst2) inst2._keyEvent = true;
			$.datepicker._adjustDate(campo, passo, 'D');
			annunciaGiornoCorrente(campo);
            var nuova = cellaAttiva(pannello);           // il ridisegno ha sostituito le celle
            if (nuova) nuova.focus();
			evento.preventDefault(); evento.stopPropagation();
		}, false);
	}

	var tastieraOriginale = prototipo._doKeyDown;
	prototipo._doKeyDown = function (event) {
		var aperto = $.datepicker._datepickerShowing && $.datepicker._lastInput === event.target;

		if (aperto && !event.ctrlKey && !event.metaKey && !event.altKey && !event.shiftKey) {
			var passo = null;
			switch (event.keyCode) {
				case 37: passo = -1; break;  /* FRECCIA SINISTRA: giorno precedente */
				case 39: passo = 1; break;   /* FRECCIA DESTRA:   giorno successivo */
				case 38: passo = -7; break;  /* FRECCIA SU:       settimana precedente */
				case 40: passo = 7; break;   /* FRECCIA GIU:      settimana successiva */
				default: passo = null;
			}
			if (event.keyCode === 9 && !event.shiftKey) {    /* TAB: entra nel calendario */
				var inst9 = $.datepicker._getInst(event.target);
				var pannello9 = inst9 && inst9.dpDiv && inst9.dpDiv[0];
				if (pannello9 && spostaNelPannello(pannello9, event.target, false)) {
					event.preventDefault();
					event.stopPropagation();
					return false;
				}
			}
			if (passo !== null) {
				/* il widget disegna l'evidenza sul giorno raggiunto solo se sa che lo spostamento
				   viene dalla tastiera: e' quanto fa in cima al gestore originale, che qui non
				   viene invocato. Senza questa riga la data si muove ma non si vede muovere. */
				var inst = $.datepicker._getInst(event.target);
				if (inst) {
					inst._keyEvent = true;
				}
				$.datepicker._adjustDate(event.target, passo, 'D');
				annunciaGiornoCorrente(event.target);
				event.preventDefault();
				event.stopPropagation();
				return false;
			}
		}

		var esito = tastieraOriginale.apply(this, arguments);
		/* Pagina su e giu', Ctrl+frecce, Inizio: lo spostamento lo fa il widget, qui si annuncia */
		if (aperto) {
			annunciaGiornoCorrente(event.target);
		}
		return esito;
	};
})(jQuery);
