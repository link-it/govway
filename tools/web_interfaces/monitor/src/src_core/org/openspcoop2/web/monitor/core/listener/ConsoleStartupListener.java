package org.openspcoop2.web.monitor.core.listener;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.openspcoop2.utils.Utilities;

import org.openspcoop2.monitor.engine.alarm.AlarmConfigProperties;
import org.openspcoop2.monitor.engine.alarm.AlarmEngineConfig;
import org.openspcoop2.monitor.engine.alarm.AlarmManager;
import org.openspcoop2.web.monitor.core.bean.ApplicationBean;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import it.link.pddoe.license.constants.TipoModulo;
import it.link.pddoe.license.constants.TipoProdotto;

public class ConsoleStartupListener extends AbstractConsoleStartupListener{


	@Override
	public void contextInitialized(ServletContextEvent evt) {

		super.contextInitialized(evt);

		PddMonitorProperties pddMonitorProperties = null;
		try{
			pddMonitorProperties = PddMonitorProperties.getInstance(org.openspcoop2.web.monitor.core.listener.ConsoleStartupListener.log);
		} catch (Exception e) {
			String msgErrore = "Errore durante l'inizializzazione delle proprietà della pddMonitor: " + e.getMessage();
			org.openspcoop2.web.monitor.core.listener.ConsoleStartupListener.log.error(
					//					throw new ServletException(
					msgErrore,e);
			throw new RuntimeException(msgErrore,e);
		}

		// Attendo eventuale inizializzazione della licenza (fatta con un thread in super.contextInitialized)
		int indexWaitLicense = 0;
		while(org.openspcoop2.web.monitor.core.listener.ConsoleStartupListener.licenseReadStartup==null){
			Utilities.sleep(500);
			indexWaitLicense++;
			// attendo al massimo 1 minuto
			if(indexWaitLicense>120){
				break;
			}
		}


		// check autorizzazione
		if(ApplicationBean.getInstance().isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_STATISTICHE_BASE)){
			try{
				if(TipoProdotto.ENTRY.equals(org.openspcoop2.web.monitor.core.listener.ConsoleStartupListener.getLicenseProductType())){
					throw new Exception("Non è possibile utilizzare la funzionalità delle statistiche. La funzionalità richiede un tipo di prodotto "+TipoProdotto.STANDARD.getValue()+" o "+TipoProdotto.PRO_ACTIVE.getValue());
				}
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione della console (check moduli): " + e.getMessage();
				org.openspcoop2.web.monitor.core.listener.ConsoleStartupListener.log.error(
						//					throw new ServletException(
						msgErrore,e);
				//throw new RuntimeException(msgErrore,e);
				// non devo lanciare eccezione, ma disabilitare la funzionalità
				// altrimenti quando installo un nuovo archivio con il software aggiornato, non riesco poi ad aggioranre la licenza
				ApplicationBean.getInstance().disabilitaFunzionalita(ApplicationBean.FUNZIONALITA_STATISTICHE_BASE);
			}
		}
		if(ApplicationBean.getInstance().isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_EVENTI)){
			try{
				if(org.openspcoop2.web.monitor.core.listener.ConsoleStartupListener.isEnabledAlmostOneModule()==false){
					throw new Exception("Non è possibile utilizzare la funzionalità degli eventi poichè richiede una versione del prodotto 'modulare'");
				}
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione della console (check moduli): " + e.getMessage();
				org.openspcoop2.web.monitor.core.listener.ConsoleStartupListener.log.error(
						//					throw new ServletException(
						msgErrore,e);
				//throw new RuntimeException(msgErrore,e);
				// non devo lanciare eccezione, ma disabilitare la funzionalità
				// altrimenti quando installo un nuovo archivio con il software aggiornato, non riesco poi ad aggioranre la licenza
				ApplicationBean.getInstance().disabilitaFunzionalita(ApplicationBean.FUNZIONALITA_EVENTI);
			}
		}
		if(ApplicationBean.getInstance().isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_ALLARMI)){
			try{
				if(org.openspcoop2.web.monitor.core.listener.ConsoleStartupListener.isEnabledModule(TipoModulo.ALLARMI)==false){
					throw new Exception("Non è possibile utilizzare la funzionalità degli allarmi. Non è stata rilevata una licenza che includa tale funzionalità");
				}
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione della console (check moduli): " + e.getMessage();
				org.openspcoop2.web.monitor.core.listener.ConsoleStartupListener.log.error(
						//					throw new ServletException(
						msgErrore,e);
				//throw new RuntimeException(msgErrore,e);
				// non devo lanciare eccezione, ma disabilitare la funzionalità
				// altrimenti quando installo un nuovo archivio con il software aggiornato, non riesco poi ad aggioranre la licenza
				ApplicationBean.getInstance().disabilitaFunzionalita(ApplicationBean.FUNZIONALITA_ALLARMI);
			}
		}
		if(ApplicationBean.getInstance().isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_SONDE_APPLICATIVE)){
			try{
				if(org.openspcoop2.web.monitor.core.listener.ConsoleStartupListener.isEnabledModule(TipoModulo.SONDE_APPLICATIVE)==false){
					throw new Exception("Non è possibile utilizzare la funzionalità delle sonde applicative. Non è stata rilevata una licenza che includa tale funzionalità");
				}
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione della console (check moduli): " + e.getMessage();
				org.openspcoop2.web.monitor.core.listener.ConsoleStartupListener.log.error(
						//					throw new ServletException(
						msgErrore,e);
				//throw new RuntimeException(msgErrore,e);
				// non devo lanciare eccezione, ma disabilitare la funzionalità
				// altrimenti quando installo un nuovo archivio con il software aggiornato, non riesco poi ad aggioranre la licenza
				ApplicationBean.getInstance().disabilitaFunzionalita(ApplicationBean.FUNZIONALITA_SONDE_APPLICATIVE);
			}
		}
		if(ApplicationBean.getInstance().isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_TRANSAZIONI_CONTENUTI) ||
				ApplicationBean.getInstance().isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_RICERCHE_PERSONALIZZATE) ||
				ApplicationBean.getInstance().isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_STATISTICHE_PERSONALIZZATE) ){
			try{
				if(!org.openspcoop2.web.monitor.core.listener.ConsoleStartupListener.isEnabledModule(TipoModulo.DATA_ANALYSIS) && 
						!org.openspcoop2.web.monitor.core.listener.ConsoleStartupListener.isEnabledModule(TipoModulo.DATA_ANALYSIS_SDK)){
					throw new Exception("Non è possibile utilizzare funzionalità di data analysis. Non è stata rilevata una licenza che includa tale funzionalità");
				}
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione della console (check moduli): " + e.getMessage();
				org.openspcoop2.web.monitor.core.listener.ConsoleStartupListener.log.error(
						//					throw new ServletException(
						msgErrore,e);
				//throw new RuntimeException(msgErrore,e);
				// non devo lanciare eccezione, ma disabilitare la funzionalità
				// altrimenti quando installo un nuovo archivio con il software aggiornato, non riesco poi ad aggioranre la licenza
				ApplicationBean.getInstance().disabilitaFunzionalita(ApplicationBean.FUNZIONALITA_TRANSAZIONI_CONTENUTI);
				ApplicationBean.getInstance().disabilitaFunzionalita(ApplicationBean.FUNZIONALITA_RICERCHE_PERSONALIZZATE);
				ApplicationBean.getInstance().disabilitaFunzionalita(ApplicationBean.FUNZIONALITA_STATISTICHE_PERSONALIZZATE);
			}

			try{
				if(pddMonitorProperties.isVisualizzaPluginsSDK()){
					if(org.openspcoop2.web.monitor.core.listener.ConsoleStartupListener.isEnabledModule(TipoModulo.DATA_ANALYSIS_SDK)==false){
						throw new Exception("Non è possibile utilizzare funzionalità di data analysis con SDK. Non è stata rilevata una licenza che includa tale funzionalità");
					}
				}
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione della console (check moduli): " + e.getMessage();
				org.openspcoop2.web.monitor.core.listener.ConsoleStartupListener.log.error(
						//					throw new ServletException(
						msgErrore,e);
				//throw new RuntimeException(msgErrore,e);
				// non devo lanciare eccezione, ma disabilitare la funzionalità
				// altrimenti quando installo un nuovo archivio con il software aggiornato, non riesco poi ad aggioranre la licenza
				//ApplicationBean.getInstance().disabilitaFunzionalita(ApplicationBean.ARCHIVIO_MESSAGGI);
				PddMonitorProperties.disabilitaFunzionalitaPluginsSDK();
			}

		}
		try{
			if(pddMonitorProperties.isAttivoDumpModeChoice()){
				if(org.openspcoop2.web.monitor.core.listener.ConsoleStartupListener.isEnabledModule(TipoModulo.ARCHIVIO_MESSAGGI)==false){
					throw new Exception("Non è possibile utilizzare la funzionalità di dump near real-time. Non è stata rilevata una licenza che includa tale funzionalità");
				}
			}
		} catch (Exception e) {
			String msgErrore = "Errore durante l'inizializzazione della console (check moduli): " + e.getMessage();
			org.openspcoop2.web.monitor.core.listener.ConsoleStartupListener.log.error(
					//					throw new ServletException(
					msgErrore,e);
			//throw new RuntimeException(msgErrore,e);
			// non devo lanciare eccezione, ma disabilitare la funzionalità
			// altrimenti quando installo un nuovo archivio con il software aggiornato, non riesco poi ad aggioranre la licenza
			//ApplicationBean.getInstance().disabilitaFunzionalita(ApplicationBean.ARCHIVIO_MESSAGGI);
			PddMonitorProperties.disabilitaFunzionalitaArchiviazioneMessaggi();
		}
		if(ApplicationBean.getInstance().isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_REPORT)){
			try{
				if(org.openspcoop2.web.monitor.core.listener.ConsoleStartupListener.isEnabledModule(TipoModulo.LOG_ANALYSIS)==false){
					throw new Exception("Non è possibile utilizzare la funzionalità dei reports. Non è stata rilevata una licenza che includa tale funzionalità");
				}
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione della console (check moduli): " + e.getMessage();
				org.openspcoop2.web.monitor.core.listener.ConsoleStartupListener.log.error(
						//					throw new ServletException(
						msgErrore,e);
				//throw new RuntimeException(msgErrore,e);
				// non devo lanciare eccezione, ma disabilitare la funzionalità
				// altrimenti quando installo un nuovo archivio con il software aggiornato, non riesco poi ad aggioranre la licenza
				ApplicationBean.getInstance().disabilitaFunzionalita(ApplicationBean.FUNZIONALITA_REPORT);
			}
		}

		// inizializza gli Allarmi
		if(ApplicationBean.getInstance().isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_ALLARMI)){
			try {

				AlarmEngineConfig alarmConfig = AlarmConfigProperties.getAlarmConfiguration(org.openspcoop2.web.monitor.core.listener.ConsoleStartupListener.log, 
						pddMonitorProperties.getAllarmiConfigurazione());
				AlarmManager.setAlarmEngineConfig(alarmConfig);

			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione della configurazione degli allarmi: " + e.getMessage();
				org.openspcoop2.web.monitor.core.listener.ConsoleStartupListener.log.error(
						//					throw new ServletException(
						msgErrore,e);
				throw new RuntimeException(msgErrore,e);
			}
		}


		ServletContext servletContext = evt.getServletContext();

		InputStream isFont = null;

		try{
			String fontFileName = PddMonitorProperties.getInstance(log).getConsoleFont();
			
			log.debug("Caricato Font dal file: ["+fontFileName+"] in corso... ");
			
			isFont = servletContext.getResourceAsStream("/fonts/"+ fontFileName);

			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			Font fontCaricato = Font.createFont(Font.PLAIN, isFont);
			
			log.debug("Caricato Font: ["+fontCaricato.getName()+"] FontName: ["+fontCaricato.getFontName()+"] FontFamily: ["+fontCaricato.getFamily()+"] FontStyle: ["+fontCaricato.getStyle()+"]");
			
			ge.registerFont(fontCaricato);

			log.debug("Check Graphics Environment: is HeadeLess ["+java.awt.GraphicsEnvironment.isHeadless()+"]");

			log.debug("Elenco Nomi Font disponibili: " + Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
			
			PddMonitorProperties.getInstance(log).setConsoleFontName(fontCaricato.getName());
			PddMonitorProperties.getInstance(log).setConsoleFontFamilyName(fontCaricato.getFamily());
			PddMonitorProperties.getInstance(log).setConsoleFontStyle(fontCaricato.getStyle());
			
			log.debug("Caricato Font dal file: ["+fontFileName+"] completato.");
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		} finally {
			if(isFont != null){
				try {	isFont.close(); } catch (IOException e) {	}
			}
		}
	}




	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		super.contextDestroyed(arg0);
	}

}
