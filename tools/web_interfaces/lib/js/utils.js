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
				$("#iconCopy_dec").show();
				
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
	var tooltipX = event.pageX - 8;
	var tooltipY = event.pageY + 8;
	$('div.copyTooltip').css({top: tooltipY, left: tooltipX});
};

function showTooltip(event) {
	$('div.copyTooltip').remove();
	$('<div class="copyTooltip">Copiato</div>').appendTo('body');
	changeTooltipPosition(event);
};

function showTooltipAndFadeOut(event) {
    showTooltip(event);

    // Imposta il timeout per far sparire il div dopo 2 secondi
    setTimeout(function() {
        $('div.copyTooltip').fadeOut('slow'); // Scompare gradualmente in 1 secondo
    }, 1000); // Tempo di attesa in millisecondi (1 secondi)
}



function copyTextToClipboard(text) {
	  var textArea = document.createElement("textarea");

	  //
	  // *** This styling is an extra step which is likely not required. ***
	  //
	  // Why is it here? To ensure:
	  // 1. the element is able to have focus and selection.
	  // 2. if element was to flash render it has minimal visual impact.
	  // 3. less flakyness with selection and copying which **might** occur if
	  //    the textarea element is not visible.
	  //
	  // The likelihood is the element won't even render, not even a
	  // flash, so some of these are just precautions. However in
	  // Internet Explorer the element is visible whilst the popup
	  // box asking the user for permission for the web page to
	  // copy to the clipboard.
	  //

	  // Place in top-left corner of screen regardless of scroll position.
	  textArea.style.position = 'fixed';
	  textArea.style.top = 0;
	  textArea.style.left = 0;

	  // Ensure it has a small width and height. Setting to 1px / 1em
	  // doesn't work as this gives a negative w/h on some browsers.
	  textArea.style.width = '2em';
	  textArea.style.height = '2em';

	  // We don't need padding, reducing the size if it does flash render.
	  textArea.style.padding = 0;

	  // Clean up any borders.
	  textArea.style.border = 'none';
	  textArea.style.outline = 'none';
	  textArea.style.boxShadow = 'none';

	  // Avoid flash of white box if rendered for any reason.
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
		var successful = false;
// 		    console.log('Oops, unable to copy');
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
            filename = matches[1].replace(/['"]/g, ''); // Rimuovi eventuali apici o virgolette
        }
    }
    return filename;
}


// Gestione della copia negli appunti

function copyToClipboard(containerId, copyMessageId, event) {
	// Ottieni il contenuto da copiare utilizzando jQuery e l'attributo data-copy
    var copyText = $('#' + containerId).attr('data-copy');

    // Crea un elemento temporaneo di input per copiare il testo
    var textarea = $('<textarea>');
    $('body').append(textarea);
    textarea.val(copyText).select();
    document.execCommand('copy');
    textarea.remove();

	// Mostra il messaggio di conferma vicino al cursore
    showCopyMessage(copyMessageId, event);
}

function showCopyMessage(copyMessageId, event) {
    var messageDiv = $('#' + copyMessageId);
	var targetPosition = $(event.currentTarget).position();
	var targetWidth = $(event.currentTarget).width();
    messageDiv.css({
		top: targetPosition.top - 30, // Posiziona il messaggio 30px sopra il button
		left: targetPosition.left + targetWidth + 10, // Posiziona il messaggio 10px a destra del button
        visibility: 'visible'
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
    setTimeout(function() {
        $('#' + buttonId).css('visibility', 'visible');
    }, delay); // Delay di visibilità
	
    clearTimeout(hideTimeout);
}

// Funzione per gestire il mouseleave con timeout
function handleMouseLeaveHandler(buttonId, timeout) {
    var hideTimeout = setTimeout(function() {
        if (!$('#' + buttonId + ':hover').length) {
			$('#' + buttonId).css('visibility', 'hidden');
        }
    }, timeout); // Timeout per nascondere
	
	return hideTimeout;
}

// Funzione generica per gestire gli eventi del tasto copia
function setupCopyButtonEvents(triggerElementId, buttonId, copyMessageId, delay, hideTimeoutDelay) {
	var hideTimeout;
	
    $('#' + triggerElementId).on('mouseenter', function() {
        handleMouseEnterTriggerElement(buttonId, delay, hideTimeout);
    });

    $('#' + triggerElementId).on('mouseleave', function() {
        hideTimeout = handleMouseLeaveHandler(buttonId, hideTimeoutDelay);
    });

    $('#' + buttonId).on('mouseenter', function() {
        clearTimeout(hideTimeout); // Cancella il timeout
    });

    $('#' + buttonId).on('mouseleave', function() {
		hideTimeout = handleMouseLeaveHandler(buttonId, hideTimeoutDelay); // Rinnova il timeout
    });

    $('#' + buttonId).on('click', function(event) {
        copyToClipboard(triggerElementId, copyMessageId, event);
    });
}
   
   