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
		"/commons/pages/welcome.jsf",
		"/commons/pages/about.jsf",
			
		"/commons/pages/statoPdd.jsf",

		// Transazioni
		"/transazioni/pages/list/transazioni.jsf",
		
		// Esiti Live
		"/transazioni/pages/form/esitiLive.jsf",

		// Password Utenti
		"/core/pages/form/user.jsf",

		// Stats
		"/stat/pages/list/configurazioniGenerali.jsf",
		"/stat/pages/form/dettaglioConfigurazione.jsf",
		
		//Servlet Configurazioni Exporter
		"/configurazioniexporter",

		// Eventi
		"/eventi/pages/form/evento.jsf",
		"/eventi/pages/list/eventi.jsf",

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
		"/commons/pages/welcome.jsf",	
		"/commons/pages/about.jsf",
			
		// Configurazione Utenti
		"/core/pages/form/user.jsf",
			
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
		"/commons/pages/statoPdd.jsf",			

		// welcome page
		"/commons/pages/welcome.jsf",
			
		// Monitoraggio
		"/commons/pages/about.jsf",
		"/transazioni/pages/list/transazioni.jsf",
		"/transazioni/pages/form/dettagliMessaggio.jsf",
		"/transazioni/pages/form/dettaglioDump.jsf",
		"/transazioni/pages/form/esitiLive.jsf",
		"/transazioni/pages/form/fault.jsf",
		"/transazioni/pages/form/headerTrasporto.jsf",
		"/transazioni/pages/form/visualizzaDiagnostici.jsf",
		"/transazioni/pages/form/visualizzaTraccia.jsf",

		// Configurazione Utenti
		"/core/pages/form/user.jsf",
		
		// Servlet Exporter
		"/diagnosticiexporter",
		"/tracceexporter",
		"/transazioniexporter",
		"/transazionicsvexporter",

		// Stats
		"/stat/pages/form/analisiStatistica.jsf",
		"/stat/pages/form/andamentoTemporale.jsf",
		"/stat/pages/form/andamentoTemporaleGrafico.jsf",
		"/stat/pages/form/distribSA.jsf",
		"/stat/pages/form/distribSAGrafico.jsf",
		"/stat/pages/form/distribServizio.jsf",
		"/stat/pages/form/distribServizioGrafico.jsf",
		"/stat/pages/form/distribSoggetto.jsf",
		"/stat/pages/form/distribSoggettoGrafico.jsf",
		"/stat/pages/form/distribAzione.jsf",
		"/stat/pages/form/distribAzioneGrafico.jsf",
		"/stat/pages/form/statsPersonalizzate.jsf",
		"/stat/pages/form/statsPersonalizzateGrafico.jsf",
		"/stat/pages/list/configurazioniGenerali.jsf",
		"/stat/pages/form/dettaglioConfigurazione.jsf",
		
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
		{"/commons/pages/statoPdd.jsf",ApplicationBean.FUNZIONALITA_STATUS_PDD},
		{"/commons/pages/about.jsf",ApplicationBean.FUNZIONALITA_GENERICHE},
		{"/commons/pages/welcome.jsf",ApplicationBean.FUNZIONALITA_GENERICHE},

		// Monitoraggio
		{"/transazioni/pages/list/transazioni.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/transazioni/pages/form/dettagliMessaggio.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/transazioni/pages/form/dettaglioDump.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/transazioni/pages/form/fault.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/transazioni/pages/form/headerTrasporto.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/transazioni/pages/form/visualizzaDiagnostici.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		{"/transazioni/pages/form/visualizzaTraccia.jsf",ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE},
		// Export Transazioni
		{"/diagnosticiexporter",ApplicationBean.FUNZIONALITA_EXPORT_TRANSAZIONI},
		{"/tracceexporter",ApplicationBean.FUNZIONALITA_EXPORT_TRANSAZIONI},
		{"/transazioniexporter",ApplicationBean.FUNZIONALITA_EXPORT_TRANSAZIONI},
		{"/transazionicsvexporter",ApplicationBean.FUNZIONALITA_EXPORT_TRANSAZIONI},

		// Esiti Live
		{"/transazioni/pages/form/esitiLive.jsf",ApplicationBean.FUNZIONALITA_ESITI_LIVE},

		// Eventi
		{"/eventi/pages/form/evento.jsf",ApplicationBean.FUNZIONALITA_EVENTI},
		{"/eventi/pages/list/eventi.jsf",ApplicationBean.FUNZIONALITA_EVENTI},

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
		{"/core/pages/form/user.jsf",ApplicationBean.FUNZIONALITA_UTENTI},

		// Sonde
		{"/pages/form/sonda.jsf",ApplicationBean.FUNZIONALITA_SONDE_APPLICATIVE},
		{"/pages/list/sonde.jsf",ApplicationBean.FUNZIONALITA_SONDE_APPLICATIVE},

			// Stats
		{"/stat/pages/form/analisiStatistica.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/list/configurazioniGenerali.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/dettaglioConfigurazione.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		
		//Servlet Configurazioni Exporter
		{"/configurazioniexporter",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		
		{"/stat/pages/form/andamentoTemporale.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/andamentoTemporaleGrafico.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/distribSA.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/distribSAGrafico.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/distribServizio.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/distribServizioGrafico.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/distribSoggetto.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/distribSoggettoGrafico.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/distribAzione.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		{"/stat/pages/form/distribAzioneGrafico.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_BASE},
		// Statistiche personalizzate
		{"/stat/pages/form/statsPersonalizzate.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_PERSONALIZZATE},
		{"/stat/pages/form/statsPersonalizzateGrafico.jsf",ApplicationBean.FUNZIONALITA_STATISTICHE_PERSONALIZZATE},

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
			"/stat/pages/form/andamentoTemporaleGrafico.jsf",
			"/stat/pages/form/distribSAGrafico.jsf",
			"/stat/pages/form/distribServizioGrafico.jsf",
			"/stat/pages/form/distribSoggettoGrafico.jsf",
			"/stat/pages/form/distribAzioneGrafico.jsf",
			"/stat/pages/form/statsPersonalizzateGrafico.jsf",
			// Esiti Live
			"/transazioni/pages/form/esitiLive.jsf",
			// Summary
			"/commons/pages/welcome.jsf"
	};
}
