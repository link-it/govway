package org.openspcoop2.web.monitor.core.listener;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;


public class ConsoleStartupListener extends AbstractConsoleStartupListener{


	@Override
	public void contextInitialized(ServletContextEvent evt) {

		super.contextInitialized(evt);

		@SuppressWarnings("unused")
		PddMonitorProperties pddMonitorProperties = null;
		try{
			pddMonitorProperties = PddMonitorProperties.getInstance(AbstractConsoleStartupListener.log);
		} catch (Exception e) {
			String msgErrore = "Errore durante l'inizializzazione delle proprietà della pddMonitor: " + e.getMessage();
			AbstractConsoleStartupListener.log.error(
					//					throw new ServletException(
					msgErrore,e);
			throw new RuntimeException(msgErrore,e);
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
