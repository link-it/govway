/**
 * From an idea of Michael Irwin's jquery script (http://michael.theirwinfamily.net)
 */


jQuery.fn.collapse = function(options) {
	var defaults = {
		closed : false
	};
	settings = jQuery.extend({}, defaults, options);

	return this.each(function() {
		//obj e' il fieldset
		var obj = jQuery(this);
        
        //check se evento gia' bindato (sull'elemento legend), in tal caso non proseguo
        var lf = obj.find("legend:first");
        if(lf.hasClass('collapsible'))
        	return;
        
        //attach dell'handler sull'evento click sul tag legend
		lf.addClass('collapsible').click(function() {
			if (obj.hasClass('collapsed'))
				obj.removeClass('collapsed').addClass('collapsible');
	
			jQuery(this).removeClass('collapsed');
	
			obj.children().not('legend').toggle("fast", function() {
			 
				 if (jQuery(this).is(":visible"))
					obj.find("legend:first").addClass('collapsible');
				 else
					obj.addClass('collapsed').find("legend").addClass('collapsed');
			 });
		});
		
		if (settings.closed) {
			obj.addClass('collapsed').find("legend:first").addClass('collapsed');
			obj.children().not("legend:first").css('display', 'none');
		}
	});
};