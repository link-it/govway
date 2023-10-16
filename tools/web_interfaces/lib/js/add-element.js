$(document).ready(function(){

  	// info
  	if($(".spanIconInfoBox").length>0){
  		$(".spanIconInfoBox").click(function(){
  			var iconInfoBoxId = $(this).parent().prop('id');
  			var idx = iconInfoBoxId.substring(iconInfoBoxId.indexOf("_")+1);
  			console.log(idx);
  			if(idx) {
				var label = $("#hidden_title_iconInfo_"+ idx).val();
				var body = $("#hidden_body_iconInfo_"+ idx).val();
				mostraDataElementInfoModal(label,body);
  			}
		});
  	}
  	
  	if($(".iconInfoBox-cb-info").length>0){
  		$(".iconInfoBox-cb-info").click(function(){
  			var iconInfoBoxId = $(this).prop('id');
  			var idx = iconInfoBoxId.substring(iconInfoBoxId.indexOf("_")+1);
  			console.log(idx);
  			if(idx) {
				var label = $("#hidden_title_iconInfo_"+ idx).val();
				var body = $("#hidden_body_iconInfo_"+ idx).val();
				mostraDataElementInfoModal(label,body);
  			}
		});
  	}
  	
    //date time tracciamento
    //date time diagnostica
    $(":input[name='datainizio']").datepicker({dateFormat: 'yy-mm-dd', showOtherMonths: true, selectOtherMonths: false, showWeek: true, firstDay: 1, changeMonth: true, changeYear: true});
    $(":input[name='datafine']").datepicker({dateFormat: 'yy-mm-dd', showOtherMonths: true, selectOtherMonths: false, showWeek: true, firstDay: 1, changeMonth: true, changeYear: true});

    showSlider($("select[name*='percentuale']:not([type=hidden])"));
    
    var numInputs = Array.from(document.querySelectorAll('input[type=number]'));

   	for(var i = 0 ; i < numInputs.length; i++){
   		if(numInputs[i].hasAttribute('gw-function')){
   			$(numInputs[i]).on('change', window[numInputs[i].getAttribute('gw-function')]);
   		} else {
   			$(numInputs[i]).on('change', inputNumberChangeEventHandler	);
   		}
   	}
    
    scrollToPostBackElement(destElement);
});