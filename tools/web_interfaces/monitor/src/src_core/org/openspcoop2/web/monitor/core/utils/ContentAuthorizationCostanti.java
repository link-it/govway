package org.openspcoop2.web.monitor.core.utils;

import org.openspcoop2.web.monitor.core.bean.ApplicationBean;

public class ContentAuthorizationCostanti {

	// Elenco dei path che sono disponibili sempre
	public static String [] listaPathConsentiti = {
		"/a4j/",
		"/resources/",
		"/images/",
		"/css/",
		"/fonts/",
		"/scripts/",
		"/public/error.jsf",
		"/public/timeoutPage.jsf",
		"/public/login.jsf",
		"/FusionCharts/",
		"/index.jsp",
		"/timeout.jsp",
		"/report/statistica",
		"/report/configurazione"
	};

	// Elenco delle pagine che sono disponibili al ruolo amministratore
	public static String[] listaPagineRuoloAmministratore = {
		// welcome page
		"/pages/welcome.jsf",
		"/pages/about.jsf",
			
		"/pages/statoPdd.jsf",

		// Transazioni
		"/pages/list/transazioni.jsf",
		
		// Esiti Live
		"/pages/form/esitiLive.jsf",

		// Auditing
		"/pages/form/auditing.jsf",

		// Configurazione Utenti
		"/pages/list/user.jsf",
		"/pages/form/user.jsf",

		// Stats
		"/pages/stats/configurazioniGenerali.jsf",
		"/pages/stats/form/dettaglioConfigurazione.jsf",
		
		//Servlet Configurazioni Exporter
		"/configurazioniexporter",

		// Eventi
		"/pages/form/evento.jsf",
		"/pages/list/eventi.jsf",

		//Processi
		"/pages/list/processi.jsf",
		"/pages/list/dettagliMessaggioProcesso.jsf",
		"/pages/list/dettaglioDumpProcesso.jsf",
		"/pages/list/faultProcesso.jsf",
		"/pages/list/headerTrasportoProcesso.jsf",
		"/pages/list/visualizzaDiagnosticiProcesso.jsf",
		"/pages/list/visualizzaTracciaProcesso.jsf",
		
		//Servlet Processi Exporter
		"/processiexporter",
		"/processiconfigexporter"

	}; 

	// Elenco delle pagine che sono disponibili al ruolo configuratore
	public static String[] listaPagineRuoloConfiguratore = {

		// welcome page
		"/pages/welcome.jsf",	
		"/pages/about.jsf",
			
		// Configurazione Utenti
		"/pages/form/user.jsf",
			
		// Configurazioni
		"/pages/configuratore/form/configurazioneServizio.jsf",
		"/pages/configuratore/form/configurazioneServizioAzione.jsf",
		"/pages/configuratore/form/dump.jsf",
		"/pages/configuratore/form/dumpXpath.jsf",
		"/pages/configuratore/form/ricerca.jsf",
		"/pages/configuratore/form/ricercaParam.jsf",
		"/pages/configuratore/form/statistica.jsf",
		"/pages/configuratore/form/statisticaParam.jsf",
		"/pages/configuratore/form/transazioni.jsf",
		"/pages/configuratore/list/configurazioneServizi.jsf",
		"/pages/configuratore/list/configurazioneServiziAzioni.jsf",
		"/pages/configuratore/list/dump.jsf",
		"/pages/configuratore/list/dumpXpath.jsf",
		"/pages/configuratore/list/ricercaParams.jsf",
		"/pages/configuratore/list/ricerche.jsf",
		"/pages/configuratore/list/statisticaParams.jsf",
		"/pages/configuratore/list/statistiche.jsf",

		// Sonde
		"/pages/form/sonda.jsf",
		"/pages/list/sonde.jsf",

		//Processi
		"/pages/configuratore/list/processi.jsf",
		"/pages/configuratore/form/processi.jsf",

		// Allarmi
		"/pages/list/allarmi.jsf",
		"/pages/list/allarmeParams.jsf",
		"/pages/form/configurazioneAllarme.jsf",
		"/pages/form/configurazioneAllarmeParam.jsf",
		"/pages/list/configurazioneAllarmeParams.jsf"
	};

	// Elenco delle pagine che sono disponibili al ruolo operatore
	public static String[] listaPagineRuoloOperatore = {
		"/pages/statoPdd.jsf",			

		// welcome page
		"/pages/welcome.jsf",
			
		// Monitoraggio
		"/pages/about.jsf",
		"/pages/list/transazioni.jsf",
		"/pages/form/dettagliMessaggio.jsf",
		"/pages/form/dettaglioDump.jsf",
		"/pages/form/esitiLive.jsf",
		"/pages/form/fault.jsf",
		"/pages/form/headerTrasporto.jsf",
		"/pages/form/visualizzaDiagnostici.jsf",
		"/pages/form/visualizzaTraccia.jsf",

		// Configurazione Utenti
		"/pages/form/user.jsf",
		
		// Servlet Exporter
		"/diagnosticiexporter",
		"/tracceexporter",
		"/transazioniexporter",
		"/transazionicsvexporter",

		// Stats
		"/pages/stats/analisiStatistica.jsf",
		"/pages/stats/andamentoTemporale.jsf",
		"/pages/stats/andamentoTemporaleGrafico.jsf",
		"/pages/stats/distribSA.jsf",
		"/pages/stats/distribSAGrafico.jsf",
		"/pages/stats/distribServizio.jsf",
		"/pages/stats/distribServizioGrafico.jsf",
		"/pages/stats/distribSoggetto.jsf",
		"/pages/stats/distribSoggettoGrafico.jsf",
		"/pages/stats/distribAzione.jsf",
		"/pages/stats/distribAzioneGrafico.jsf",
		"/pages/stats/statsPersonalizzate.jsf",
		"/pages/stats/statsPersonalizzateGrafico.jsf",
		"/pages/stats/configurazioniGenerali.jsf",
		"/pages/stats/form/dettaglioConfigurazione.jsf",
		
		//Servlet Configurazioni Exporter
		"/configurazioniexporter",

		// Allarmi
		"/pages/list/statoAllarmi.jsf",
		"/pages/list/allarmeHistory.jsf",
		"/pages/form/allarme.jsf",
		
		// Reports
		"/pages/list/reports.jsf",
		"/pages/form/report.jsf",
		
		// servlet di export
		"/reportexporter"
	};

	public static String[][] listaPagineModuli ={

		// Stato della porta
		{"/pages/statoPdd.jsf",ApplicationBean.FUNZIONALITA_STATUS_PDD},
		{"/pages/about.jsf",ApplicationBean.FUNZIONALITA_GENERICHE},
		{"/pages/welcome.jsf",ApplicationBean.FUNZIONALITA_GENERICHE},

		// Monitoraggio
		{"/pages/list/transazioni.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/pages/form/dettagliMessaggio.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/pages/form/dettaglioDump.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/pages/form/fault.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/pages/form/headerTrasporto.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/pages/form/visualizzaDiagnostici.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/pages/form/visualizzaTraccia.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		// Export Transazioni
		{"/diagnosticiexporter",ApplicationBean.FUNZIONALITA_EXPORT_TRANSAZIONI},
		{"/tracceexporter",ApplicationBean.FUNZIONALITA_EXPORT_TRANSAZIONI},
		{"/transazioniexporter",ApplicationBean.FUNZIONALITA_EXPORT_TRANSAZIONI},
		{"/transazionicsvexporter",ApplicationBean.FUNZIONALITA_EXPORT_TRANSAZIONI},

		// Esiti Live
		{"/pages/form/esitiLive.jsf",ApplicationBean.FUNZIONALITA_ESITI_LIVE},

		// Auditing
		{"/pages/form/auditing.jsf",ApplicationBean.FUNZIONALITA_AUDITING},

		// Eventi
		{"/pages/form/evento.jsf",ApplicationBean.FUNZIONALITA_EVENTI},
		{"/pages/list/eventi.jsf",ApplicationBean.FUNZIONALITA_EVENTI},

		//Processi
		{"/pages/configuratore/list/processi.jsf",ApplicationBean.FUNZIONALITA_PROCESSI},
		{"/pages/configuratore/form/processi.jsf",ApplicationBean.FUNZIONALITA_PROCESSI},
		{"/pages/list/processi.jsf",ApplicationBean.FUNZIONALITA_PROCESSI},
		{"/pages/list/dettagliMessaggioProcesso.jsf",ApplicationBean.FUNZIONALITA_PROCESSI},
		{"/pages/list/dettaglioDumpProcesso.jsf",ApplicationBean.FUNZIONALITA_PROCESSI},
		{"/pages/list/faultProcesso.jsf",ApplicationBean.FUNZIONALITA_PROCESSI},
		{"/pages/list/headerTrasportoProcesso.jsf",ApplicationBean.FUNZIONALITA_PROCESSI},
		{"/pages/list/visualizzaDiagnosticiProcesso.jsf",ApplicationBean.FUNZIONALITA_PROCESSI},
		{"/pages/list/visualizzaTracciaProcesso.jsf",ApplicationBean.FUNZIONALITA_PROCESSI},
		//Export Processi
		{"/processiexporter",ApplicationBean.FUNZIONALITA_EXPORT_PROCESSI},
		{"/processiconfigexporter",ApplicationBean.FUNZIONALITA_EXPORT_PROCESSI},
		

		// Configurazioni
		{"/pages/configuratore/form/configurazioneServizio.jsf",ApplicationBean.FUNZIONALITA_ANALISI_DATI},
		{"/pages/configuratore/form/configurazioneServizioAzione.jsf",ApplicationBean.FUNZIONALITA_ANALISI_DATI},
		{"/pages/configuratore/list/configurazioneServizi.jsf",ApplicationBean.FUNZIONALITA_ANALISI_DATI},
		{"/pages/configuratore/list/configurazioneServiziAzioni.jsf",ApplicationBean.FUNZIONALITA_ANALISI_DATI},

		{"/pages/configuratore/form/transazioni.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_CONTENUTI},

		{"/pages/configuratore/list/dump.jsf",ApplicationBean.FUNZIONALITA_ARCHIVIAZIONE_DATI},
		{"/pages/configuratore/list/dumpXpath.jsf",ApplicationBean.FUNZIONALITA_ARCHIVIAZIONE_DATI},
		{"/pages/configuratore/form/dump.jsf",ApplicationBean.FUNZIONALITA_ARCHIVIAZIONE_DATI},
		{"/pages/configuratore/form/dumpXpath.jsf",ApplicationBean.FUNZIONALITA_ARCHIVIAZIONE_DATI},

		{"/pages/configuratore/form/ricerca.jsf",ApplicationBean.FUNZIONALITA_RICERCHE_PERSONALIZZATE},
		{"/pages/configuratore/form/ricercaParam.jsf",ApplicationBean.FUNZIONALITA_RICERCHE_PERSONALIZZATE},
		{"/pages/configuratore/list/ricercaParams.jsf",ApplicationBean.FUNZIONALITA_RICERCHE_PERSONALIZZATE},
		{"/pages/configuratore/list/ricerche.jsf",ApplicationBean.FUNZIONALITA_RICERCHE_PERSONALIZZATE},

		{"/pages/configuratore/list/statisticaParams.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_PERSONALIZZATE},
		{"/pages/configuratore/list/statistiche.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_PERSONALIZZATE},
		{"/pages/configuratore/form/statistica.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_PERSONALIZZATE},
		{"/pages/configuratore/form/statisticaParam.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_PERSONALIZZATE},

		//Utenti
		{"/pages/list/user.jsf",ApplicationBean.FUNZIONALITA_UTENTI},
		{"/pages/form/user.jsf",ApplicationBean.FUNZIONALITA_UTENTI},

		// Sonde
		{"/pages/form/sonda.jsf",ApplicationBean.FUNZIONALITA_SONDE_APPLICATIVE},
		{"/pages/list/sonde.jsf",ApplicationBean.FUNZIONALITA_SONDE_APPLICATIVE},

			// Stats
		{"/pages/stats/analisiStatistica.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/pages/stats/configurazioniGenerali.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/pages/stats/form/dettaglioConfigurazione.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		
		//Servlet Configurazioni Exporter
		{"/configurazioniexporter",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		
		{"/pages/stats/andamentoTemporale.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/pages/stats/andamentoTemporaleGrafico.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/pages/stats/distribSA.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/pages/stats/distribSAGrafico.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/pages/stats/distribServizio.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/pages/stats/distribServizioGrafico.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/pages/stats/distribSoggetto.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/pages/stats/distribSoggettoGrafico.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/pages/stats/distribAzione.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/pages/stats/distribAzioneGrafico.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		// Statistiche personalizzate
		{"/pages/stats/statsPersonalizzate.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_PERSONALIZZATE},
		{"/pages/stats/statsPersonalizzateGrafico.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_PERSONALIZZATE},

		// Allarmi
		{"/pages/form/allarme.jsf",ApplicationBean.FUNZIONALITA_ALLARMI},
		{"/pages/list/allarmi.jsf",ApplicationBean.FUNZIONALITA_ALLARMI},
		{"/pages/list/allarmeHistory.jsf",ApplicationBean.FUNZIONALITA_ALLARMI},
		{"/pages/list/allarmeParams.jsf",ApplicationBean.FUNZIONALITA_ALLARMI},
		{"/pages/form/configurazioneAllarmeParam.jsf",ApplicationBean.FUNZIONALITA_ALLARMI},
		{"/pages/form/configurazioneAllarme.jsf",ApplicationBean.FUNZIONALITA_ALLARMI},
		{"/pages/list/configurazioneAllarmeParams.jsf",ApplicationBean.FUNZIONALITA_ALLARMI},
		{"/pages/list/statoAllarmi.jsf",ApplicationBean.FUNZIONALITA_ALLARMI},

		// Reports
		{"/pages/form/report.jsf",ApplicationBean.FUNZIONALITA_REPORT},
		{"/pages/list/reports.jsf",ApplicationBean.FUNZIONALITA_REPORT},
		
		// Export Report
		{"/reportexporter",ApplicationBean.FUNZIONALITA_REPORT}
	};

	// Elenco delle pagine sulle quali non abilitare la compatibilita' IE8
	public static String[] listaPagineNoIE8 = {
			// Stats
			"/pages/stats/andamentoTemporaleGrafico.jsf",
			"/pages/stats/distribSAGrafico.jsf",
			"/pages/stats/distribServizioGrafico.jsf",
			"/pages/stats/distribSoggettoGrafico.jsf",
			"/pages/stats/distribAzioneGrafico.jsf",
			"/pages/stats/statsPersonalizzateGrafico.jsf",
			// Esiti Live
			"/pages/form/esitiLive.jsf",
			// Summary
			"/pages/welcome.jsf"
	};
}
