function singleCheckboxListener( inputId, state , showSelectAll, _totRows , _rows, _useCount ){
	 //var tid = ":"+inputId+"_tbl";
	 var tid = inputId+"_tbl";

	 var allSize = jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_column_ckb']").not("[id$='selectedAllChbx']").length;
	 var size = jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_column_ckb']:checked").not("[id$='selectedAllChbx']").length;
	 if(showSelectAll){
		 if(_useCount){
	    	if(state && _totRows>_rows ){
	        	if(size >= allSize){
	        		jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_checkAll']").prop("checked", true);
	        		jQuery("[id$='"+inputId+"_selectedInfoLbl']").show().text("Selezionati "+size+" elementi su "+_totRows);
	        		jQuery("[id$='"+inputId+"_selectedInfoPipe1']").show();
	        	    jQuery("[id$='"+inputId+"_selectAllLink']").show().text("Seleziona tutti i "+_totRows+" elementi");
	        	}
	       	}else{
	       		jQuery("[id$='"+inputId+"_selectedInfoLbl']").hide();
	       		jQuery("[id$='"+inputId+"_selectedInfoPipe1']").hide();
	       		jQuery("[id$='"+inputId+"_selectAllLink']").hide();
	       		jQuery("[id$='"+inputId+"_undoSelectAllLink']").hide();         
	       		jQuery("[id$='"+inputId+"_selectedAllChbx']").prop("checked", false); 
	       		jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_checkAll']").prop("checked", false);
	        }
	    } else {
	    	if(state){
	    		if(size >= allSize){
	    			jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_checkAll']").prop("checked", true);
	    		}
	    	} else {
	    		jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_checkAll']").prop("checked", false);
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
    	size = jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_column_ckb']").not("[id$='selectedAllChbx']").prop("checked", true).length;
    }else{
    	jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_column_ckb']").prop("checked", false);
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
	       		jQuery("[id$='"+inputId+"_selectedAllChbx']").prop("checked", false);     		
	        }
        } else {
        	jQuery("[id$='"+inputId+"_selectedAllChbx']").prop("checked", false);
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
			jQuery("[id$='"+inputId+"_selectedAllChbx']").prop("checked", true);
			jQuery("[id$='"+inputId+"_selectedInfoLbl']").show().text("Selezionati tutti i "+_totRows+" elementi");
			jQuery("[id$='"+inputId+"_selectedInfoPipe1']").show();
			jQuery("[id$='"+inputId+"_selectAllLink']").hide();
			jQuery("[id$='"+inputId+"_undoSelectAllLink']").show().text("Annulla selezione.");
		}else{
			jQuery("[id$='"+inputId+"_selectedAllChbx']").prop("checked", false);
			jQuery("[id$='"+inputId+"_selectedInfoLbl']").hide();
			jQuery("[id$='"+inputId+"_selectedInfoPipe1']").hide();
	   		jQuery("[id$='"+inputId+"_selectAllLink']").hide();
	   		jQuery("[id$='"+inputId+"_undoSelectAllLink']").hide();
	   		//jQuery("div [id$='"+inputId+"'] input:checkbox").prop("checked", false);
	   		jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_column_ckb']").prop("checked", false);
	   		jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_checkAll']").prop("checked", false);
		}
    } else {
    	if(selectAll){
    		jQuery("[id$='"+inputId+"_selectedAllChbx']").prop("checked", true);
    		jQuery("[id$='"+inputId+"_selectedInfoLbl']").show().text(_labelSelezionaPrimiElementi);
    		jQuery("[id$='"+inputId+"_selectedInfoPipe1']").show();
			jQuery("[id$='"+inputId+"_selectAllTableItemsLink']").show();
    		jQuery("[id$='"+inputId+"_selectedInfoPipe2']").show();
	    	jQuery("[id$='"+inputId+"_selectAllLink']").hide();
	    	jQuery("[id$='"+inputId+"_selectedInfoPipe3']").hide();
	    	jQuery("[id$='"+inputId+"_undoSelectAllLink']").show();
    	} else {
    		jQuery("[id$='"+inputId+"_selectedAllChbx']").prop("checked", false);
    		jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_column_ckb']").prop("checked", false);
	   		jQuery("table [id$='"+tid+"'] input:checkbox[id$='"+inputId+"_checkAll']").prop("checked", false);
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
	return jQuery("[id$='"+inputId+"_selectedAllChbx']").prop("checked");
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

function abilitaColonnaSelectAll(containerId){
	var n = jQuery("div [id$='"+containerId+"_tbl'] input:checkbox[id$='column_ckb']").length;
	var thCheckAll = jQuery("[id$='"+containerId+"_tbl:"+containerId+"_checkAll']"); 
	if(n==0){
		thCheckAll.prop('disabled',true);
	}else{
		thCheckAll.prop('disabled',false);
	}
};

 ExtendedDataTable.DataTable.header.prototype.OnSepMouseMove = function(event) {
      if(this.dragColumnInfo && this.dragColumnInfo.mouseDown) {
           if(!this.dragColumnInfo.dragStarted) {
                this.dragColumnInfo.dragStarted = true;
                this._showSplitter(this.dragColumnInfo.srcElement.columnIndex);
           }
           var delta = Event.pointerX(event) - 
                this.dragColumnInfo.startX
           if (delta < this.minDelta) {
                delta = this.minDelta;
           }
           /*if (delta > this.maxDelta) {
                delta = this.maxDelta;
           }*/
           var x = this.dragColumnInfo.originalX + delta;
           var finalX = x - this.minColumnWidth - 6 //6 stands for sep span width;
           this.columnSplitter.moveToX(finalX);                     
           Event.stop(event);
      }
 }

 ExtendedDataTable.DataTable.header.prototype.OnSepMouseUp = function(event) {
      Event.stop(event);
      Event.stopObserving(document, 'mousemove', this.eventSepMouseMove);
      Event.stopObserving(document, 'mouseup', this.eventSepMouseUp);
      if(this.dragColumnInfo && this.dragColumnInfo.dragStarted) {

           this.dragColumnInfo.dragStarted = false;
           this.dragColumnInfo.mouseDown = false;

           var delta = Event.pointerX(event) - 
                this.dragColumnInfo.startX;
           if (delta < this.minDelta) {
                delta = this.minDelta;
           }
           /*if (delta > this.maxDelta) {
                delta = this.maxDelta;
           }*/
           var columnIndex = this.dragColumnInfo.srcElement.columnIndex;
           var newWidth = this.getColumnWidth(columnIndex) + delta;
           
           this.extDt.setColumnWidth(columnIndex, newWidth);
           this.setColumnWidth(columnIndex,newWidth);
           this.extDt.updateLayout();
           if (this.extDt.onColumnResize){
                //set properly value to this.columnWidths
                this.extDt.columnWidths = "";
                for (i=0; i<this.columnsNumber; i++){
                     this.extDt.columnWidths += "" + this.getColumnWidth(i) + ";";
                }//for
                this.extDt.onColumnResize(event, this.extDt.columnWidths);
           }
      }
      this._hideSplitter();
      
 }
 
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
