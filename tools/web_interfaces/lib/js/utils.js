/**
 * Funzioni di utilita' utilizzate dalle view
 * 
 * @author Stefano Corallo <corallo@link.it>
  */

/*
 * Questo deve essere incluso alla fine di tutte le altre inclusioni di script, nell'header, nella pagina jsp
 * Utilizza la libreria jquery
 */
 $(document).ready(function(){
 	
 	String.prototype.format = function()
	{
	    var str = this;
	    for(var i=0;i<arguments.length;i++)
	    {	
	        var re = new RegExp('\\{' + (i) + '\\}','gm');
	        str = str.replace(re, arguments[i]);
	    }
	    return str;
	}
 	
 	if($( "#confermaEliminazioneModal" ).length > 0){
 		$( "#confermaEliminazioneModal" ).dialog({
 	      resizable: false,
 	     dialogClass: "no-close",
 	     autoOpen: false,
 	      height: "auto",
 	      width: 400,
 	      modal: true,
 	      buttons: {
 	    	 'Annulla' : function() {
 	 	          $( this ).dialog( "close" );
 	 	        }
 	      ,
 	        "Conferma Rimozione": function() {
 	        	visualizzaAjaxStatus();
 	        	RemoveEntries();
 	        	$( this ).dialog( "close" );
 	        }
 	      }
 	    });
 	}
 	
 	if($( "#selezioneRichiestaModal" ).length > 0){
 		$( "#selezioneRichiestaModal" ).dialog({
 	      resizable: false,
 	     dialogClass: "no-close",
 	     autoOpen: false,
 	      height: "auto",
 	      width: 400,
 	      modal: true,
 	      buttons: {
 	    	 'Chiudi' : function() {
 	 	          $( this ).dialog( "close" );
 	 	        }
 	      }
 	    });
 	}
 	
 	if($( "#dataElementInfoModal" ).length > 0){
 		$( "#dataElementInfoModal" ).dialog({
 	      resizable: false,
 	      autoOpen: false,
 	      height: 350,
 	      width: 500,
 	      modal: true
 	    });
 	}
 	
	if($("[name=selectcheckbox]").length>0){
		if($("#rem_btn").length==1){
		    $("#rem_btn").click(function(){
		    	if($("input[name=selectcheckbox]:checked").length > 0)
		    		$( "#confermaEliminazioneModal" ).dialog( "open" );
			else
				$( "#selezioneRichiestaModal" ).dialog( "open" );
			    //RemoveEntries();
			});
		
//		//imposto funzione di confirm dialog
//		$("#rem_btn").confirm({
//			  msg:'Eliminare gli elementi selezionati?',
//			  timeout:5000,
//			  dialogShow:'fadeIn',
//			  dialogSpeed:'slow',
//			  buttons: {
//			  	ok: 'Si',
//			  	cancel: 'Annulla',
//			    wrapper:'<button></button>',
//			    separator:'  '
//			  }  
//			})
		    
		    
		}
	}
	
	if($( "#visualizzaInformazioniCifrateModal" ).length > 0){
 		$( "#visualizzaInformazioniCifrateModal" ).dialog({
 	      resizable: true,
 	      autoOpen: false,
 	      height: "auto",
 	      width: "660px",
 	      modal: true,
 	      buttons: {
			'Visualizza' : visualizzaValoreDecodificato
 	      }
 	    });
 	}
 	
 	if($( "#alertInformazioniCifrateModal" ).length > 0){
 		$( "#alertInformazioniCifrateModal" ).dialog({
 	      resizable: false,
 	      autoOpen: false,
 	      height: "auto",
 	      width: 500,
 	      modal: true,
 	      buttons: {
 	    	 'Copia' : copiaValoreDecodificato
 	      }
 	    });
 	}
 	
 	if($( "#downloadInformazioniCifrateModal" ).length > 0){
 		$( "#downloadInformazioniCifrateModal" ).dialog({
 	      resizable: false,
 	      autoOpen: false,
 	      height: "auto",
 	      width: 500,
 	      modal: true,
 	      buttons: {
 	    	 'Download' : downloadValoreDecodificato
 	      }
 	    });
 	}
 	
 	if($( "#erroreInformazioniCifrateModal" ).length > 0){
 		$( "#erroreInformazioniCifrateModal" ).dialog({
 	      resizable: false,
 	     dialogClass: "no-close",
 	     autoOpen: false,
 	     height: "auto",
 	     width: "auto",
 	     modal: true,
 	     buttons: {
 	    	'Chiudi' : function() {
 	          $( this ).dialog( "close" );
 	        }
 	      }
 	    });
 	}
 	
 	if($( "#operazioneAjaxModal" ).length > 0){
 		$( "#operazioneAjaxModal" ).dialog({
 	      resizable: false,
 	     dialogClass: "no-close",
 	     autoOpen: false,
 	     height: "auto",
 	     width: 660,
 	     modal: true,
 	     buttons: {
 	    	'Chiudi' : function() {
 	          $( this ).dialog( "close" );
 	        }
 	      }
 	    });
 	}
 
 });
 
 function downloadValoreDecodificato(evt) {
    // Recupero la URL e il valore da decodificare
    var urlLockDecoder = $("#__i_hidden_lockurl_").val();
    
    // addTabID
	urlLockDecoder = addTabIdParam(urlLockDecoder,true);

    // Resettare i valori di lock dopo l'operazione
    resetValoriLock();

    // Effettua il download del file
	window.location.href = urlLockDecoder;

    // Chiude il dialog una volta completato il processo
    $(this).dialog("close");
}

function visualizzaValoreDecodificato(evt) {
	// recupero la url
	var urlLockDecoder = $("#__i_hidden_lockurl_").val();
	var valToDecode = $("#__i_hidden_lockvalue_").val();
	var labelElementoForm = $("#__i_hidden_locklabel_").val();
	
	// addTabID
	urlLockDecoder = addTabIdParam(urlLockDecoder,true);
	
	// chiamata ajax
	visualizzaAjaxStatus();
	
	$.ajax({
		url : urlLockDecoder,
		method: 'POST',
		async : false,
		data: {
	        secret: valToDecode
	    },
	    contentType: 'application/x-www-form-urlencoded',
		success: function(data, textStatus, jqXHR){
			var esito = data.esito;
			var dettaglio = data.dettaglioEsito;
			
			if(esito == 'errors'){
				// visualizzare errore ricevuto nella modale prevista
				mostraErroreInformazioniCifrateModal(dettaglio);
				nascondiAjaxStatus();
				$("#visualizzaInformazioniCifrateModal").dialog( "close" );
			} else {
				// inserimento del valore nella text area
				$("textarea[id^='txtA_ne_dec']").val(dettaglio);
				
				// visualizzo la text area 
				$("#txtA_ne_dec").show();
				
				// visualizzo il pulsante di copia
				// si mostra la span, non la sola icona: e' la span il comando raggiungibile da
				// tastiera, e finche' resta visibile e' attivabile anche senza un valore da copiare
				$("#spanIconCopy_dec").show();
				
				// nascondo la nota
				$("#visualizzaInformazioniCifrateModalPropNota").hide();
				
				// nascondo il tasto visualizza
				$("#visualizzaInformazioniCifrateModal").parent().children('.ui-dialog-buttonpane').hide();
				
				// imposto label elemento form
				$("#visualizzaInformazioniCifrateModal").prev().children('span').text(labelElementoForm);
				
				// ripristino ombreggiatura
				$("#visualizzaInformazioniCifrateModal").dialog("close");
				$("#visualizzaInformazioniCifrateModal").dialog("open");
				
				nascondiAjaxStatus();
			}
		},
		error: function(data, textStatus, jqXHR){
			var val = data.responseURL;
			document.location = val;
			
			// visualizzare errore ricevuto nella modale prevista
			//mostraErroreInformazioniCifrateModal(data.responseText);
			//nascondiAjaxStatus();
			//$("#visualizzaInformazioniCifrateModal").dialog( "close" );
		}
	});
	
	// resetto i valori lock
	resetValoriLock();
}
 
 function copiaValoreDecodificato(evt) {
    // Recupero la URL e il valore da decodificare
    var urlLockDecoder = $("#__i_hidden_lockurl_").val();
    var valToDecode = $("#__i_hidden_lockvalue_").val();

	// addTabID
	urlLockDecoder = addTabIdParam(urlLockDecoder,true);

    // Chiamata AJAX per decodificare il valore
    visualizzaAjaxStatus();

    $.ajax({
        url: urlLockDecoder,
        method: 'POST',
        async: false,
        data: {
            secret: valToDecode
        },
        contentType: 'application/x-www-form-urlencoded',
        success: function(data, textStatus, jqXHR) {
			var esito = data.esito;
			var dettaglio = data.dettaglioEsito;
			
			if(esito == 'errors'){
				// visualizzare errore ricevuto nella modale prevista
				mostraErroreInformazioniCifrateModal(dettaglio);
				nascondiAjaxStatus();
				$("#alertInformazioniCifrateModal").dialog( "close" );
			} else {
				var valueToCopy = dettaglio;

	            // Copia il valore nella clipboard
	            var copiatoOK = copyTextToClipboard(valueToCopy);
	
	            // Nasconde lo stato AJAX dopo la copia
	            nascondiAjaxStatus();
	
	            // Mostra il tooltip se la copia è avvenuta con successo
	            if (copiatoOK) {
	                showTooltipAndFadeOut(evt);
	            }
			}
        },
        error: function(data, textStatus, jqXHR) {
			var val = data.responseURL;
			document.location = val;
			
			// visualizzare errore ricevuto nella modale prevista
			//mostraErroreInformazioniCifrateModal(data.responseText);
			//nascondiAjaxStatus();
			//$("#alertInformazioniCifrateModal").dialog( "close" );
        }
    });

    // Resettare i valori di lock dopo l'operazione
    resetValoriLock();

    // Chiude il dialog una volta completato il processo
    $(this).dialog("close");
}
 
function showSlider(select){ 
        if(select.length > 0) {
        		var label = select.closest('div').children('label');
                 // var td = select.closest('td').prev('td'); 
                 setPercentuale(label,select[0].selectedIndex + 1 );
                 var slider = $( "<div id='slider' class='prop-slider'></div>" ).insertAfter( select ).slider({
                      min: 1, max: 100, range: "min", value: select[ 0 ].selectedIndex + 1,
                      slide: function( event, ui ) {
                    	  var label = select.closest('div').children('label');
                                 //var td = select.closest('td').prev('td');                                       
                                 select[ 0 ].selectedIndex = ui.value - 1 ;
                                 setPercentuale(label,select[0].selectedIndex + 1);
                        },
                      change: function( event, ui ) {
                                return postBack(select[0].name);
                        }
                });
                                
                select.change(function() {
                        slider.slider( "value", this.selectedIndex +1);
                });

                select.hide();
        }
}

function setPercentuale(td,value){
        var tdText = td.text();
        var idxSx = tdText.indexOf('(');
        var idxDx = tdText.indexOf(')');
               
         if(idxSx > -1 && idxDx > -1){
                var base = tdText.substring(0,idxSx +1 );
                var fine = tdText.substring(idxDx-1);
                var newText = base + value + fine;
                td.text(newText);
        }
}

function changeTooltipPosition(event) {
	var tooltipX;
	var tooltipY;
	/* Distinguere l'attivazione col puntatore da quella da tastiera guardando le coordinate non
	   funziona: un evento sintetico non ne ha, l'attivazione con Invio di un <button> nativo le
	   riporta a zero, e in alcuni percorsi arrivano valori residui che sembrano validi. Il
	   discriminante affidabile e' 'detail', che vale 0 per un'attivazione da tastiera e almeno 1
	   per un clic vero. */
	var nativo = event ? event.originalEvent : null;
	var conPuntatore = !!nativo && typeof nativo.detail === 'number' && nativo.detail > 0
		&& typeof event.pageX === 'number' && typeof event.pageY === 'number';
	if (conPuntatore) {
		tooltipX = event.pageX - 8;
		tooltipY = event.pageY + 8;
	} else {
		/* Attivazione da tastiera: il riquadro va collocato sotto il comando attivato, altrimenti
		   finirebbe in una posizione priva di senso — invisibile, oppure nell'angolo della pagina. */
		var $comando = $(event && event.currentTarget ? event.currentTarget : document.activeElement);
		var pos = $comando.offset() || { top: 0, left: 0 };
		tooltipX = pos.left;
		tooltipY = pos.top + ($comando.outerHeight() || 0) + 8;
	}
	$('div.copyTooltip').css({top: tooltipY, left: tooltipX});
};

function showTooltip(event) {
	$('div.copyTooltip').remove();
	var $riquadro = $('<div class="copyTooltip" role="status"></div>').appendTo('body');
	changeTooltipPosition(event);
	setTimeout(function() {
		$riquadro.text('Copiato');
	}, 0);
};

function showTooltipAndFadeOut(event) {
    showTooltip(event);

    // Imposta il timeout per far sparire il div dopo 2 secondi
    setTimeout(function() {
        $('div.copyTooltip').fadeOut('slow'); // Scompare gradualmente in 1 secondo
    }, 1000); // Tempo di attesa in millisecondi (1 secondi)
}



function copyTextToClipboard(text) {
	  if (navigator.clipboard && navigator.clipboard.writeText) {
	    navigator.clipboard.writeText(text).then(function() {
	      console.log('Valore Copiato ' + text);
	    }, function() {
	      console.log('Copia non effettuata');
	    });
	    return true;
	  }

	  // Fallback per browser che non supportano navigator.clipboard
	  var textArea = document.createElement("textarea");
	  textArea.style.position = 'fixed';
	  textArea.style.top = 0;
	  textArea.style.left = 0;
	  textArea.style.width = '2em';
	  textArea.style.height = '2em';
	  textArea.style.padding = 0;
	  textArea.style.border = 'none';
	  textArea.style.outline = 'none';
	  textArea.style.boxShadow = 'none';
	  textArea.style.background = 'transparent';

	  textArea.value = text;

	  document.body.appendChild(textArea);
	  textArea.focus();
	  textArea.select();

	  var successful = false;
	  try {
	    successful = document.execCommand('copy');

	    if(successful) {
	    	console.log('Valore Copiato ' + text);
	    } else {
	    	console.log('Copia non effettuata');
	    }
	  } catch (err) {
		successful = false;
	  }

	  document.body.removeChild(textArea);
	  return successful;
}
		
function setValoriLock(url,valore,label){
		$("#__i_hidden_lockurl_").val(url);
		$("#__i_hidden_lockvalue_").val(valore);
		if(label) {
			$("#__i_hidden_locklabel_").val(label);
		} else {
			$("#__i_hidden_locklabel_").val('');
		} 
}

function resetValoriLock(){
	setValoriLock('','','');
}

// Funzione per estrarre il nome del file dall'header Content-Disposition
function getFilenameFromContentDisposition(xhr) {
    var contentDisposition = xhr.getResponseHeader('Content-Disposition');
    var filename = '';
    if (contentDisposition && contentDisposition.indexOf('attachment') !== -1) {
        var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
        var matches = filenameRegex.exec(contentDisposition);
        if (matches != null && matches[1]) {
            filename = matches[1].replaceAll(/['"]/g, ''); // Rimuovi eventuali apici o virgolette
        }
    }
    return filename;
}


// Gestione della copia negli appunti

function copyToClipboard(containerId, copyMessageId, event) {
	// Ottieni il contenuto da copiare utilizzando jQuery e l'attributo data-copy
    var copyText = $('#' + containerId).attr('data-copy');

    if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(copyText).then(function() {
            showCopyMessage(copyMessageId, event);
        });
    } else {
        // Fallback per browser che non supportano navigator.clipboard
        var textarea = $('<textarea>');
        $('body').append(textarea);
        textarea.val(copyText).select();
        document.execCommand('copy');
        textarea.remove();
        showCopyMessage(copyMessageId, event);
    }
}

function showCopyMessage(copyMessageId, event) {
    var messageDiv = $('#' + copyMessageId);
    /* Il riquadro e' una regione 'live' (role="status"), che annuncia le modifiche del proprio
       contenuto: il testo e' pero' gia' presente nel markup, quindi renderlo visibile non
       basterebbe a farlo annunciare. Lo si svuota e reinserisce a ogni copia. */
    var testo = messageDiv.data('gwTesto');
    if (typeof testo !== 'string') {
        testo = messageDiv.text();
        messageDiv.data('gwTesto', testo);
    }
    messageDiv.text('');
    setTimeout(function() {
        messageDiv.text(testo);
    }, 0);
	var targetPosition = $(event.currentTarget).position();
	var targetWidth = $(event.currentTarget).width();
    messageDiv.css({
		top: targetPosition.top - 30, // Posiziona il messaggio 30px sopra il button
		left: targetPosition.left + targetWidth + 10, // Posiziona il messaggio 10px a destra del button
        visibility: 'visible',
        /* Va ripristinata anche l'opacita': nasconderlo la porta a 0 come stile in linea, e senza
           questo il messaggio comparirebbe una volta sola, restando poi invisibile ai clic
           successivi pur avendo 'visibility: visible'. */
        opacity: 1
    });

    // Nascondi il messaggio dopo 2 secondi
    setTimeout(function() {
        messageDiv.css({
            visibility: 'hidden',
            opacity: 0
        });
    }, 2000); // Nascondi il messaggio dopo 2 secondi
}

// Funzione per gestire il mouseenter con delay
function handleMouseEnterTriggerElement(buttonId, delay, hideTimeout) {
    /* Un solo pulsante visibile alla volta: gli altri vengono spenti subito. Senza questo,
       passando rapidamente sulle righe di un elenco ogni riga attende il proprio timeout e si
       vede una scia di icone accese. Cosi' l'attesa prima di nascondere puo' restare comoda
       per raggiungere il pulsante, senza produrre quella scia. */
    $('.copy-box').not('#' + buttonId).removeClass('copy-box-visibile');
    setTimeout(function() {
        /* Si commuta una classe e non lo stile in linea: uno stile in linea vincerebbe sulle
           regole CSS, impedendo di rendere il pulsante visibile quando la cella che lo contiene
           riceve il focus da tastiera (cfr. 'td:focus-within .copy-box' in linkit-base.css). */
        $('#' + buttonId).addClass('copy-box-visibile');
    }, delay); // Delay di visibilità
	
    clearTimeout(hideTimeout);
}

// Funzione per gestire il mouseleave con timeout
function handleMouseLeaveHandler(buttonId, timeout) {
    var hideTimeout = setTimeout(function() {
        /* Il pulsante resta visibile finche' ha il focus grazie alla regola '.copy-box:focus'
           (cfr. linkit-base.css): qui basta gestire il puntatore. */
        if (!$('#' + buttonId + ':hover').length) {
			$('#' + buttonId).removeClass('copy-box-visibile');
        }
    }, timeout); // Timeout per nascondere
	
	return hideTimeout;
}

/* Funzione generica per gestire gli eventi del tasto copia.
   NOTA: qui esisteva anche una versione a tre parametri che delegava a questa passando 500 e 1000.
   In JavaScript l'overload non esiste: la seconda definizione sostituiva la prima, e i chiamanti —
   che passano tre argomenti — lasciavano 'delay' e 'hideTimeoutDelay' a 'undefined'. Poiche'
   'setTimeout(fn, undefined)' equivale a 0 ms, il pulsante scompariva nell'istante in cui il
   puntatore lasciava il valore, prima che si potesse raggiungerlo. I valori predefiniti sono
   quindi dichiarati qui: comparsa immediata, e un'attesa breve prima di nascondere, sufficiente a
   spostare il puntatore dal valore al pulsante. Che non si accumuli una scia di icone accese lo
   garantisce 'handleMouseEnterTriggerElement', che ne tiene visibile una sola. */
function setupCopyButtonEvents(triggerElementId, buttonId, copyMessageId, delay, hideTimeoutDelay) {
	delay = (typeof delay === 'number') ? delay : 0;
	hideTimeoutDelay = (typeof hideTimeoutDelay === 'number') ? hideTimeoutDelay : 400;
	var hideTimeout;
	
    $('#' + triggerElementId).on('mouseenter', function() {
        handleMouseEnterTriggerElement(buttonId, delay, hideTimeout);
    });

    $('#' + triggerElementId).on('mouseleave', function() {
        hideTimeout = handleMouseLeaveHandler(buttonId, hideTimeoutDelay);
    });

    $('#' + buttonId).on('mouseenter', function() {
		$('#' + buttonId).addClass('copy-box-visibile');
        clearTimeout(hideTimeout); // Cancella il timeout
    });

    $('#' + buttonId).on('mouseleave', function() {
		hideTimeout = handleMouseLeaveHandler(buttonId, hideTimeoutDelay); // Rinnova il timeout
    });

    $('#' + buttonId).on('click', function(event) {
        copyToClipboard(triggerElementId, copyMessageId, event);
    });
}
   
   