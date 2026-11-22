/* Italian initialisation for the jQuery UI date picker plugin. */
/* Written by Apaella (apaella@gmail.com). */
$(document).ready(function(){
	$.datepicker.regional['it'] = {clearText: 'Svuota', closeText: 'Chiudi',
		/* uguali al nome accessibile delle frecce (jquery-ui-datepicker-a11y.js): il testo del comando,
		   che il widget mette anche nel tooltip, deve essere contenuto nel suo nome (WCAG 2.5.3) */
		prevText: 'Mese precedente', nextText: 'Mese successivo',
		currentText: 'Oggi', weekHeader: 'Sett.',   /* la colonna dei numeri di settimana era senza intestazione */
		dayNamesMin: ['Do','Lu','Ma','Me','Gio','Ve','Sa'],
		dayNamesShort: ['Dom','Lun','Mar','Mer','Gio','Ven','Sab'],
		dayNames: ['Domenica','Lunedi','Martedi','Mercoledi','Giovedi','Venerdi','Sabato'],
		monthNamesShort: ['Gen','Feb','Mar','Apr','Mag','Giu',
		'Lug','Ago','Set','Ott','Nov','Dic'],
		monthNames: ['Gennaio','Febbraio','Marzo','Aprile','Maggio','Giugno',
		'Luglio','Agosto','Settembre','Ottobre','Novembre','Dicembre'],
		dateFormat: 'dd/mm/yy', firstDay: 1};
	$.datepicker.setDefaults($.datepicker.regional['it']);
});
