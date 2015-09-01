function singleCheckboxListener( inputId, state , showSelectAll, _totRows , _rows ){
	 //var tid = ":"+inputId+"_tbl";
	 var tid = inputId+"_tbl";

	 var allSize = jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_column_ckb']").not("[id$='selectedAllChbx']").size();
	 var size = jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_column_ckb']:checked").not("[id$='selectedAllChbx']").size();
	 if(showSelectAll){
    	if(state && _totRows>_rows ){
        	if(size >= allSize){
        		jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_checkAll']").attr("checked","on");
        		jQuery("[id$='"+inputId+"_selectedInfoLbl']").show().text("Selezionati "+size+" elementi su "+_totRows+".");
        	    jQuery("[id$='"+inputId+"_selectAllLink']").show().text("Seleziona tutti i "+_totRows+" elementi");
        	}
       	}else{
       		jQuery("[id$='"+inputId+"_selectedInfoLbl']").hide();
       		jQuery("[id$='"+inputId+"_selectAllLink']").hide();
       		jQuery("[id$='"+inputId+"_undoSelectAllLink']").hide();         
       		jQuery("[id$='"+inputId+"_selectedAllChbx']").removeAttr("checked"); 
       		jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_checkAll']").removeAttr("checked");
        }
    }
}
function checkAllCheckboxesInTable( inputId, state ,showSelectAll, _totRows, _rows ){
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
    	if(state && _totRows>_rows ){
        	jQuery("[id$='"+inputId+"_selectedInfoLbl']").show().text("Selezionati "+size+" elementi su "+_totRows+".");
        	
        	jQuery("[id$='"+inputId+"_selectAllLink']").show().text("Seleziona tutti i "+_totRows+" elementi");
       	}else{
       		jQuery("[id$='"+inputId+"_selectedInfoLbl']").hide();
       		jQuery("[id$='"+inputId+"_selectAllLink']").hide();
       		jQuery("[id$='"+inputId+"_undoSelectAllLink']").hide();         
       		jQuery("[id$='"+inputId+"_selectedAllChbx']").removeAttr("checked");     		
        }
        
        
    }
    
}
 
function selectAllListener(inputId, selectAll, _totRows){
//	var _totRows = #{value.rowCount};
	//var tid = ":"+inputId+"_tbl";
	 var tid = inputId+"_tbl";
	if(selectAll){
		jQuery("[id$='"+inputId+"_selectedAllChbx']").attr("checked","on");
		jQuery("[id$='"+inputId+"_selectedInfoLbl']").show().text("Selezionati tutti i "+_totRows+" elementi.");
		jQuery("[id$='"+inputId+"_selectAllLink']").hide();
		jQuery("[id$='"+inputId+"_undoSelectAllLink']").show().text("Annulla selezione.");
	}else{
		jQuery("[id$='"+inputId+"_selectedAllChbx']").removeAttr("checked");
		jQuery("[id$='"+inputId+"_selectedInfoLbl']").hide();
   		jQuery("[id$='"+inputId+"_selectAllLink']").hide();
   		jQuery("[id$='"+inputId+"_undoSelectAllLink']").hide();
   		//jQuery("div [id$='"+inputId+"'] input:checkbox").removeAttr("checked");
   		jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_column_ckb']").removeAttr("checked");
   		jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_checkAll']").removeAttr("checked");
	}
    
}

function showConfirm(containerId,name){
	var n = jQuery("div [id$='"+containerId+"'] input:checkbox[id$='column_ckb']:checked").length;
	if(n>0){
		Richfaces.showModalPanel(name);
	}
	return false;
 };

 function checkSelection(containerId){
	 var name = containerId + "_checkSelection";
	 var n = jQuery("div [id$='"+containerId+"_tbl'] input:checkbox[id$='column_ckb']:checked").length;
		if(n==0){
			Richfaces.showModalPanel(name);
			return false;
		}else{
			return true;
		}
 };
