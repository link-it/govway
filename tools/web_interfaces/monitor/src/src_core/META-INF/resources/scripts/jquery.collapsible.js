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
        
        /* La legenda apre e chiude la sezione, ma e' un <legend> nudo: senza ruolo ne'
           'tabindex' si attiva con il solo mouse (WCAG 2.1.1) e non dichiara il proprio
           stato (WCAG 4.1.2). Diventa un comando, con lo stato aperto/chiuso annunciato. */
        lf.attr('role', 'button').attr('tabindex', '0')
          .attr('aria-expanded', settings.closed ? 'false' : 'true')
          .keydown(function(e) {
        	if (e.which !== 13 && e.which !== 32) return true;
        	e.preventDefault();
        	jQuery(this).click();
        	return false;
          });

        //attach dell'handler sull'evento click sul tag legend
		lf.addClass('collapsible').click(function() {
			if (obj.hasClass('collapsed'))
				obj.removeClass('collapsed').addClass('collapsible');
	
			jQuery(this).removeClass('collapsed');
	
			obj.children().not('legend').toggle("fast", function() {
			 
				 if (jQuery(this).is(":visible")) {
					obj.find("legend:first").addClass('collapsible').attr('aria-expanded', 'true');
				 } else {
					obj.addClass('collapsed').find("legend").addClass('collapsed').attr('aria-expanded', 'false');
				 }
			 });
		});
		
		if (settings.closed) {
			obj.addClass('collapsed').find("legend:first").addClass('collapsed');
			obj.children().not("legend:first").css('display', 'none');
		}
	});
};