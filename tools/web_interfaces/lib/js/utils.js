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
