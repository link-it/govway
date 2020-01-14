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

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

import org.openspcoop2.web.monitor.core.utils.BrowserInfo;
import org.openspcoop2.web.monitor.core.utils.BrowserInfo.BrowserFamily;
import org.openspcoop2.web.monitor.core.utils.Costanti;

/****
 * IEFilter Filtro JSF che fissa la visualizzazione dell'applicazione in modalita IE-8 Compatibile, per i browser con versione >=8
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class IEFilter implements Filter {


	private static Logger log = LoggerWrapperFactory.getLogger(IEFilter.class);

	@Override

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		// Analisi dello user agent
		String userAgent = ((HttpServletRequest) request).getHeader(Costanti.USER_AGENT_HEADER_NAME);

		if(userAgent != null) {

			try{

				log.debug("Decodifica Browser da Header UserAgent ["+userAgent+"]");
				BrowserInfo browserInfo = BrowserInfo.getBrowserInfo(userAgent);

				String browsername = browserInfo.getBrowserName();
				Double browserversion = browserInfo.getVersion();

				log.debug("Browser Riconosciuto: Name ["+browsername+"] Version ["+browserversion+"].");

				if(browserInfo.getBrowserFamily().equals(BrowserFamily.IE)){

					//Imposto l'header http necessario per forzare la visualizzazione.
//					((HttpServletResponse) response).setHeader("X-UA-Compatible", "IE=EmulateIE8");
					// per tutte le versioni
					((HttpServletResponse) response).setHeader("X-UA-Compatible", "IE=edge");

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
				log.debug("Browser non riconosciuto.");
			}
		}
		chain.doFilter(request, response);
	}



	@Override

	public void destroy() {
	}

	// Request Wrapper. Catches the getHeader for Accept. When it is text/css we will return simply "text/css, */*"
	public class IE9HttpServletRequestWrapper extends HttpServletRequestWrapper {
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