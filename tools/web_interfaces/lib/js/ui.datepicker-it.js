/* Italian initialisation for the jQuery UI date picker plugin. */
/* Written by Apaella (apaella@gmail.com). */
$(document).ready(function(){
	$.datepicker.regional['it'] = {clearText: 'Svuota', closeText: 'Chiudi',
		prevText: '&lt;Prec', nextText: 'Succ&gt;',
		currentText: 'Oggi', weekHeader: 'Sm',
		dayNamesMin: ['Do','Lu','Ma','Me','Gio','Ve','Sa'],
		dayNamesShort: ['Dom','Lun','Mar','Mer','Gio','Ven','Sab'],
		dayNames: ['Domenica','Lunedi','Martedi','Mercoledi','Giovedi','Venerdi','Sabato'],
		monthNamesShort: ['Gen','Feb','Mar','Apr','Mag','Giu',
		'Lug','Ago','Set','Ott','Nov','Dic'],
		monthNames: ['Gennaio','Febbraio','Marzo','Aprile','Maggio','Giugno',
		'Luglio','Agosto','Settembre','Ottobre','Novembre','Dicembre'],
		dateFormat: 'dd/mm/yy', firstDay: 0};
	$.datepicker.setDefaults($.datepicker.regional['it']);
});
