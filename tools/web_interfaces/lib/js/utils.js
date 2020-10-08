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
 
 });
 
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
