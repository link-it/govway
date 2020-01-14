/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;

/**
 * ConsoleStartupListener
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ConsoleStartupListener extends AbstractConsoleStartupListener{


	@Override
	public void contextInitialized(ServletContextEvent evt) {

		super.contextInitialized(evt);

		@SuppressWarnings("unused")
		PddMonitorProperties govwayMonitorProperties = null;
		try{
			govwayMonitorProperties = PddMonitorProperties.getInstance(AbstractConsoleStartupListener.log);
		} catch (Exception e) {
			String msgErrore = "Errore durante l'inizializzazione delle proprietà della govwayMonitor: " + e.getMessage();
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
