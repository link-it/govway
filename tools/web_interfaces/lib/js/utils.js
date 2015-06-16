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
 	
	//Controllo su pddAdd
	var what = $("form").attr("action");

	if(typeof what === "string")
		if(what.indexOf("/pddChange.do") != -1)
		{
		//chiamo la funzione di check sul form
		//in base al tipo di tipo selezionato disabilita determinati campi del form
		changePdDType();
		}

	if($("[name=selectcheckbox]").length>0){
		if($("#rem_btn").length==1){
		    $("#rem_btn").click(function(){
			    RemoveEntries();
			});
		
		//imposto funzione di confirm dialog
		$("#rem_btn").confirm({
			  msg:'Eliminare gli elementi selezionati?',
			  timeout:5000,
			  dialogShow:'fadeIn',
			  dialogSpeed:'slow',
			  buttons: {
			  	ok: 'Si',
			  	cancel: 'Annulla',
			    wrapper:'<button></button>',
			    separator:'  '
			  }  
			})
		    }
	}
	
 });

function showSlider(select){ 
        if(select.length > 0) {
                 var td = select.closest('td').prev('td'); 
                 setPercentuale(td,select[0].selectedIndex + 1 );
                 var slider = $( "<div id='slider'></div>" ).insertAfter( select ).slider({
                      min: 1, max: 100, range: "min", value: select[ 0 ].selectedIndex + 1,
                      slide: function( event, ui ) {
                                 var td = select.closest('td').prev('td');                                       
                                 select[ 0 ].selectedIndex = ui.value - 1 ;
                                 setPercentuale(td,select[0].selectedIndex + 1);
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

