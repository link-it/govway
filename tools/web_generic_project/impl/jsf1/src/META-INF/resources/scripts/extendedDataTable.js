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
