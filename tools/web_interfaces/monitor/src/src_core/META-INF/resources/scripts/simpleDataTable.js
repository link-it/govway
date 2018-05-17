function checkAllCheckboxesInTable( inputId, state ){
    var size = 0;
    var tid = ":"+inputId+"_tbl";
    if(state){
    	size = jQuery("table [id$='"+tid+"'] input:checkbox[id$='#{id}_column_ckb']").not("[id$='selectedAllChbx']").attr("checked","on").size();
    }else{
    	jQuery("table [id$='"+tid+"'] input:checkbox[id$='#{id}_column_ckb']").removeAttr("checked");
    	size=0;
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