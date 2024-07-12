/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.web.monitor.core.listener;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;

import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.thread.ThreadExecutorManager;

/**
 * ConsoleStartupListener
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ConsoleStartupListener extends AbstractConsoleStartupListener{

	private InitRuntimeConfigReader initRuntimeConfigReader;

	@Override
	public void contextInitialized(ServletContextEvent evt) {

		super.contextInitialized(evt);

		PddMonitorProperties govwayMonitorProperties = null;
		try{
			govwayMonitorProperties = PddMonitorProperties.getInstance(AbstractConsoleStartupListener.log);
		} catch (Exception e) {
			String msgErrore = "Errore durante l'inizializzazione delle proprietà della govwayMonitor: " + e.getMessage();
			AbstractConsoleStartupListener.logError(
					//					throw new ServletException(
					msgErrore,e);
			throw new UtilsRuntimeException(msgErrore,e);
		}

		


		ServletContext servletContext = evt.getServletContext();

		InputStream isFont = null;

		try{
			String fontFileName = PddMonitorProperties.getInstance(log).getConsoleFont();
			
			logDebug("Caricato Font dal file: ["+fontFileName+"] in corso... ");
			
			isFont = servletContext.getResourceAsStream("/fonts/"+ fontFileName);

			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			Font fontCaricato = Font.createFont(Font.PLAIN, isFont);
			
			logDebug("Caricato Font: ["+fontCaricato.getName()+"] FontName: ["+fontCaricato.getFontName()+"] FontFamily: ["+fontCaricato.getFamily()+"] FontStyle: ["+fontCaricato.getStyle()+"]");
			
			ge.registerFont(fontCaricato);

			logDebug("Check Graphics Environment: is HeadeLess ["+java.awt.GraphicsEnvironment.isHeadless()+"]");

			logDebug("Elenco Nomi Font disponibili: " + Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
			
			PddMonitorProperties.getInstance(log).setConsoleFontName(fontCaricato.getName());
			PddMonitorProperties.getInstance(log).setConsoleFontFamilyName(fontCaricato.getFamily());
			PddMonitorProperties.getInstance(log).setConsoleFontStyle(fontCaricato.getStyle());
			
			logDebug("Caricato Font dal file: ["+fontFileName+"] completato.");
		}catch (Exception e) {
			logError(e.getMessage(),e);
		} finally {
			if(isFont != null){
				try {	isFont.close(); } catch (IOException e) {
					// ignore
				}
			}
		}
		
		// Inizializzazione Thread 
		try{
			ThreadExecutorManager.setup();
		}catch (Exception e) {
			logError(e.getMessage(),e);
			String msgErrore = "Errore durante l'inizializzazione del ThreadExecutorManager: "+e.getMessage();
			throw new UtilsRuntimeException(msgErrore,e);
		} 
		
		// InitRuntimeConfigReader
		if(this.isReInitSecretMaps()) {
			try{
				this.initRuntimeConfigReader = new InitRuntimeConfigReader(govwayMonitorProperties, this.isReInitSecretMaps());
				this.initRuntimeConfigReader.start();
				logInfo("RuntimeConfigReader avviato con successo.");
			} catch (Exception e) {
				String msgErrore = "Errore durante l'inizializzazione del RuntimeConfigReader: " + e.getMessage();
				logError(
						msgErrore,e);
				//throw new UtilsRuntimeException(msgErrore,e); non sollevo l'eccezione, e' solo una informazione informativa, non voglio mettere un vincolo che serve per forza un nodo acceso
			}
		}
	}




	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		super.contextDestroyed(arg0);
		
		logInfo("Shutdown pool thread ricerche ...");
        try {
            ThreadExecutorManager.shutdown();
            logInfo("Shutdown pool thread ricerche completato.");
        } 
        catch (InterruptedException e) {
        	logWarn("Shutdown pool thread ricerche fallito:" + e);
		    Thread.currentThread().interrupt();
        }
        catch (Exception e) {
            logWarn("Shutdown pool thread ricerche fallito:" + e);
        }
        
        if(this.initRuntimeConfigReader!=null) {
			this.initRuntimeConfigReader.setStop(true);
		}
	}

}
