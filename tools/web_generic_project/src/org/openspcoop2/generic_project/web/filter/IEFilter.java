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
package org.openspcoop2.generic_project.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.generic_project.web.utils.BrowserInfo;
import org.openspcoop2.generic_project.web.utils.BrowserInfo.BrowserFamily;
import org.openspcoop2.generic_project.web.utils.BrowserUtils;


/****
* IEFilter Filtro JSF che fissa la visualizzazione dell'applicazione in modalita IE-8 Compatibile, per i browser con versione &gt;=8
* 
* @author Pintori Giuliano (pintori@link.it)
* @author $Author$
* @version $Rev$, $Date$
 *
 */
public class IEFilter implements Filter {

	private static boolean FORZA_COMPATIBILITA_IE8 = false;
	private static String FORZA_COMPATIBILITA_IE8_KEY = "forzaCompatibilitaIE8";
	
	public static void initialize(boolean forzaCompatibilitaIE8){
		IEFilter.FORZA_COMPATIBILITA_IE8 = forzaCompatibilitaIE8;
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if(filterConfig != null){
			String forzaCompatibilitaIE8S = filterConfig.getInitParameter(FORZA_COMPATIBILITA_IE8_KEY);
			if(StringUtils.isNotEmpty(forzaCompatibilitaIE8S)){
				IEFilter.FORZA_COMPATIBILITA_IE8 = Boolean.parseBoolean(forzaCompatibilitaIE8S);
			}
		}
	}

	@Override

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		// Analisi dello user agent
		String userAgent = ((HttpServletRequest) request).getHeader("User-Agent");

		if(userAgent != null) {

			try{

				//log.info("Decodifica Browser da Header UserAgent ["+userAgent+"]");
				BrowserInfo browserInfo = BrowserUtils.getBrowserInfo(userAgent);
				
//				String browsername = browserInfo.getBrowserName();
//				Double browserversion = browserInfo.getVersion();

				//log.info("Browser Riconosciuto: Name ["+browsername+"] Version ["+browserversion+"].");

				if(browserInfo.getBrowserFamily().equals(BrowserFamily.IE)){
					
					//Imposto l'header http necessario per forzare la visualizzazione. se previsto dalla configurazione
					if(IEFilter.FORZA_COMPATIBILITA_IE8)
						((HttpServletResponse) response).setHeader("X-UA-Compatible", "IE=EmulateIE8");

					// Risolvo anche il problema di ie9 che non visualizza il contenuto dei file css della libreria Richfaces.
					// Esso invia solo "text/css" all'interno dell' Accept header.
					// la classe HtmlRenderUtils lancia un eccezione poiche' non gestisce questo tipo di Accept
					// allora la soluzione e' una patch del codice oppure aggiornare il valore dell'header Accept per far si che non
					// venga sollevata l'eccezione: applico questa soluzione impostando "text/css, */*" al posto di "text/css".

					String accept = ((HttpServletRequest) request).getHeader("Accept");

					if ("text/css".equals(accept)) {

						chain.doFilter(new IE9HttpServletRequestWrapper((HttpServletRequest) request), response);

					}
				}
			}catch(Exception e){
				//log.info("Browser non riconosciuto.");
			}
		}
		chain.doFilter(request, response);
	}



	@Override

	public void destroy() {
	}

	// Request Wrapper. Catches the getHeader for Accept. When it is text/css we will return simply "text/css, */*"

	private class IE9HttpServletRequestWrapper extends HttpServletRequestWrapper {
		public IE9HttpServletRequestWrapper(HttpServletRequest request) {
			super(request);
		}

		@Override

		public String getHeader(String name) {
			String header = super.getHeader(name);

			if ("text/css".equalsIgnoreCase(header)) {
				header = "text/css, */*";
			}

			return header;
		}
	}
}