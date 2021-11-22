function singleCheckboxListener( inputId, state , showSelectAll, _totRows , _rows, _useCount ){
	 //var tid = ":"+inputId+"_tbl";
	 var tid = inputId+"_tbl";

	 var allSize = jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_column_ckb']").not("[id$='selectedAllChbx']").size();
	 var size = jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_column_ckb']:checked").not("[id$='selectedAllChbx']").size();
	 if(showSelectAll){
		 if(_useCount){
	    	if(state && _totRows>_rows ){
	        	if(size >= allSize){
	        		jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_checkAll']").attr("checked","on");
	        		jQuery("[id$='"+inputId+"_selectedInfoLbl']").show().text("Selezionati "+size+" elementi su "+_totRows);
	        		jQuery("[id$='"+inputId+"_selectedInfoPipe1']").show();
	        	    jQuery("[id$='"+inputId+"_selectAllLink']").show().text("Seleziona tutti i "+_totRows+" elementi");
	        	}
	       	}else{
	       		jQuery("[id$='"+inputId+"_selectedInfoLbl']").hide();
	       		jQuery("[id$='"+inputId+"_selectedInfoPipe1']").hide();
	       		jQuery("[id$='"+inputId+"_selectAllLink']").hide();
	       		jQuery("[id$='"+inputId+"_undoSelectAllLink']").hide();         
	       		jQuery("[id$='"+inputId+"_selectedAllChbx']").removeAttr("checked"); 
	       		jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_checkAll']").removeAttr("checked");
	        }
	    } else {
	    	if(state){
	    		if(size >= allSize){
	    			jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_checkAll']").attr("checked","on");
	    		}
	    	} else {
	    		jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_checkAll']").removeAttr("checked");
	    	}
	    	
	    	if(size > 0){
	    		jQuery("[id$='"+inputId+"_selectedInfoLbl']").show().text("Selezionati "+size+" elementi");
	    		jQuery("[id$='"+inputId+"_selectedInfoPipe1']").show();
	    		jQuery("[id$='"+inputId+"_selectAllTableItemsLink']").show();
	    		jQuery("[id$='"+inputId+"_selectedInfoPipe2']").show();
	    		if(jQuery("[id$='"+inputId+"_checkVisualizzaSelezionePrimiElementi']").val() == 'true'){
	    			jQuery("[id$='"+inputId+"_selectAllLink']").show();
	    			jQuery("[id$='"+inputId+"_selectedInfoPipe3']").show();
	    		}
	    		jQuery("[id$='"+inputId+"_undoSelectAllLink']").show();
	    	} else {
	    		jQuery("[id$='"+inputId+"_selectedInfoLbl']").hide();
	    		jQuery("[id$='"+inputId+"_selectedInfoPipe1']").hide();
	    		jQuery("[id$='"+inputId+"_selectAllTableItemsLink']").hide();
	    		jQuery("[id$='"+inputId+"_selectedInfoPipe2']").hide();
	    		jQuery("[id$='"+inputId+"_selectAllLink']").hide();
	    		jQuery("[id$='"+inputId+"_selectedInfoPipe3']").hide();
	    		jQuery("[id$='"+inputId+"_undoSelectAllLink']").hide();
	    	}
	    }
    }
}
function checkAllCheckboxesInTable( inputId, state ,showSelectAll, _totRows, _rows, _useCount ){
    var size = 0;
  //var tid = ":"+inputId+"_tbl";
	 var tid = inputId+"_tbl";
    if(state){
    	size = jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_column_ckb']").not("[id$='selectedAllChbx']").attr("checked","on").size();
    }else{
    	jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_column_ckb']").removeAttr("checked");
    	size=0;
    }

    if(showSelectAll){
    	if(_useCount){
	    	if(state && _totRows>_rows ){
	        	jQuery("[id$='"+inputId+"_selectedInfoLbl']").show().text("Selezionati "+size+" elementi su "+_totRows);
	        	jQuery("[id$='"+inputId+"_selectedInfoPipe1']").show();
	        	jQuery("[id$='"+inputId+"_selectAllLink']").show().text("Seleziona tutti i "+_totRows+" elementi");
	       	}else{
	       		jQuery("[id$='"+inputId+"_selectedInfoLbl']").hide();
	       		jQuery("[id$='"+inputId+"_selectedInfoPipe1']").hide();
	       		jQuery("[id$='"+inputId+"_selectAllLink']").hide();
	       		jQuery("[id$='"+inputId+"_undoSelectAllLink']").hide();         
	       		jQuery("[id$='"+inputId+"_selectedAllChbx']").removeAttr("checked");     		
	        }
        } else {
        	if(state){
        		jQuery("[id$='"+inputId+"_selectedInfoLbl']").show().text("Selezionati "+size+" elementi");
        		jQuery("[id$='"+inputId+"_selectedInfoPipe1']").show();
    			jQuery("[id$='"+inputId+"_selectAllTableItemsLink']").hide();
	    		jQuery("[id$='"+inputId+"_selectedInfoPipe2']").hide();
	    		if(jQuery("[id$='"+inputId+"_checkVisualizzaSelezionePrimiElementi']").val() == 'true'){
	    			jQuery("[id$='"+inputId+"_selectAllLink']").show();
	    			jQuery("[id$='"+inputId+"_selectedInfoPipe3']").show();
	    		}
	    		jQuery("[id$='"+inputId+"_undoSelectAllLink']").show();
	    	} else {
	    		jQuery("[id$='"+inputId+"_selectedInfoLbl']").hide();
	    		jQuery("[id$='"+inputId+"_selectedInfoPipe1']").hide();
	    		jQuery("[id$='"+inputId+"_selectAllTableItemsLink']").hide();
	    		jQuery("[id$='"+inputId+"_selectedInfoPipe2']").hide();
	    		jQuery("[id$='"+inputId+"_selectAllLink']").hide();
	    		jQuery("[id$='"+inputId+"_selectedInfoPipe3']").hide();
	    		jQuery("[id$='"+inputId+"_undoSelectAllLink']").hide();
	    	}
        }
    }
    
}
 
function selectAllListener(inputId, selectAll, _totRows, _useCount, _labelSelezionaPrimiElementi){
//	var _totRows = #{value.rowCount};
	//var tid = ":"+inputId+"_tbl";
	 var tid = inputId+"_tbl";
	if(_useCount){
		if(selectAll){
			jQuery("[id$='"+inputId+"_selectedAllChbx']").attr("checked","on");
			jQuery("[id$='"+inputId+"_selectedInfoLbl']").show().text("Selezionati tutti i "+_totRows+" elementi");
			jQuery("[id$='"+inputId+"_selectedInfoPipe1']").show();
			jQuery("[id$='"+inputId+"_selectAllLink']").hide();
			jQuery("[id$='"+inputId+"_undoSelectAllLink']").show().text("Annulla selezione.");
		}else{
			jQuery("[id$='"+inputId+"_selectedAllChbx']").removeAttr("checked");
			jQuery("[id$='"+inputId+"_selectedInfoLbl']").hide();
			jQuery("[id$='"+inputId+"_selectedInfoPipe1']").hide();
	   		jQuery("[id$='"+inputId+"_selectAllLink']").hide();
	   		jQuery("[id$='"+inputId+"_undoSelectAllLink']").hide();
	   		//jQuery("div [id$='"+inputId+"'] input:checkbox").removeAttr("checked");
	   		jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_column_ckb']").removeAttr("checked");
	   		jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_checkAll']").removeAttr("checked");
		}
    } else {
    	if(selectAll){
    		jQuery("[id$='"+inputId+"_selectedAllChbx']").attr("checked","on");
    		jQuery("[id$='"+inputId+"_selectedInfoLbl']").show().text(_labelSelezionaPrimiElementi);
    		jQuery("[id$='"+inputId+"_selectedInfoPipe1']").show();
			jQuery("[id$='"+inputId+"_selectAllTableItemsLink']").show();
    		jQuery("[id$='"+inputId+"_selectedInfoPipe2']").show();
	    	jQuery("[id$='"+inputId+"_selectAllLink']").hide();
	    	jQuery("[id$='"+inputId+"_selectedInfoPipe3']").hide();
	    	jQuery("[id$='"+inputId+"_undoSelectAllLink']").show();
    	} else {
    		jQuery("[id$='"+inputId+"_selectedAllChbx']").removeAttr("checked");
    		jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_column_ckb']").removeAttr("checked");
	   		jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_checkAll']").removeAttr("checked");
    		jQuery("[id$='"+inputId+"_selectedInfoLbl']").hide();
    		jQuery("[id$='"+inputId+"_selectedInfoPipe1']").hide();
    		jQuery("[id$='"+inputId+"_selectAllTableItemsLink']").hide();
    		jQuery("[id$='"+inputId+"_selectedInfoPipe2']").hide();
    		jQuery("[id$='"+inputId+"_selectAllLink']").hide();
    		jQuery("[id$='"+inputId+"_selectedInfoPipe3']").hide();
    		jQuery("[id$='"+inputId+"_undoSelectAllLink']").hide();
    	}
    }
}

function showConfirm(containerId,name){
	var n = jQuery("div [id$='"+containerId+"'] input:checkbox[id$='column_ckb']:checked").length;
	if(n>0){
		Richfaces.showModalPanel(name);
	}
	return false;
 };

 function checkSelection(containerId,name){
	 var n = jQuery("div [id$='"+containerId+"_tbl'] input:checkbox[id$='column_ckb']:checked").length;
		if(n==0){
			Richfaces.showModalPanel(name);
			return false;
		}else{
			return true;
		}
 };
 
function getSelectedRows(inputId){
	var tid = inputId+"_tbl";
	var ids = jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_column_ckb']:checked").not("[id$='selectedAllChbx']");
	
	var toRet = '';
	for (var i =0 ; i < ids.length ; i++) {
		if(i > 0 ) toRet += ',';
		
		toRet += ids[i].id;
	} 
		
	return toRet;
}

function getSelectedAllValue(inputId){
	return jQuery("[id$='"+inputId+"_selectedAllChbx']").attr("checked");
}

function visualizzaPannelloComandi(containerId){
	 var n = jQuery("div [id$='"+containerId+"_tbl'] input:checkbox[id$='column_ckb']").length;
	 var _buttonsDiv = jQuery("[id$='" + containerId + "_buttonsDiv']");
	if(n==0){
		_buttonsDiv.hide();
	}else{
		_buttonsDiv.show();
	}
};

function visualizzaTastoRimuovi(containerId,deleteButtonId){
	 var n = jQuery("div [id$='"+containerId+"_tbl'] input:checkbox[id$='column_ckb']").length;
	 var deleteBtn = jQuery("[id$='" + deleteButtonId + "']");
	if(n==0){
		deleteBtn.hide();
	}else{
		deleteBtn.show();
	}
};

function visualizzaColonnaSelectAll(containerId){
	var n = jQuery("div [id$='"+containerId+"_tbl'] input:checkbox[id$='column_ckb']").length;
	var thCheckAll = jQuery("[id$='"+containerId+"_tbl:ckbClmnheader']");
	if(n==0){
		thCheckAll.hide();
	}else{
		thCheckAll.show();
	}
};

function checkCountNumeroRisultati(containerId){
	var tid = containerId+"_tbl";
	return jQuery("table [id$='"+tid+"'] tbody[id$='tb']").children().length > 0;
};

function visualizzaMessaggioNoData(containerId){
	var risultatiPresenti = checkCountNumeroRisultati(containerId);
	if(!risultatiPresenti) {
		jQuery("div [id$='"+containerId+"']").hide();
		jQuery("div [id$='"+containerId+"_nodata']").show();
	} 
};
