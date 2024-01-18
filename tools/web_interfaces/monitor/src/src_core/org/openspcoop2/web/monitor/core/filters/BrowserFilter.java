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
package org.openspcoop2.web.monitor.core.filters;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.web.monitor.core.core.Utils;
import org.openspcoop2.web.monitor.core.listener.IEFilter;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.BrowserInfo;
import org.openspcoop2.web.monitor.core.utils.BrowserInfo.BrowserFamily;
import org.openspcoop2.web.monitor.core.utils.ContentAuthorizationManager;
import org.openspcoop2.web.monitor.core.utils.Costanti;
import org.slf4j.Logger;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

/**
 * BrowserFilter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class BrowserFilter implements Filter {

	/** Logger utilizzato per debug. * */
	private static Logger log = LoggerManager.getPddMonitorCoreLogger();
	
	private static Map<BrowserFamily, Double> mappaAbilitazioneGraficiSVG;
	private static Map<BrowserFamily, Double> mappaAbilitazioneVistaTransazioniCustom;

	// configurazione filtro
	@SuppressWarnings("unused")
	private FilterConfig filterConfig = null;

	public static final String PARAMETRO_SVG = "usaSVG";
	private static final String PARAMETRO_SVG_FORM_STATS = "sf_usaSVG";
	private static final String PARAMETRO_SVG_FORM_MENU = "tf_usaSVG";
	private static final String PARAMETRO_SVG_POLL_STATO = "ps_usaSVG";
	private static final String PARAMETRO_GENERA_REPORT = "generaReport";
	private static final String PARAMETRO_ESITI_LIVE = "esiti_live";
	private static final String PARAMETRO_TIPO_REPORT = "tipoReportCombo";
	private static final String PARAMETRO_TIPO_REPORT_TABELLA = "Tabella";
	private List<String> listaPagineNoIE8 = null;
	
	private static synchronized void loadMappaBrowser(){
		if(mappaAbilitazioneGraficiSVG == null)
			mappaAbilitazioneGraficiSVG = new EnumMap<>(BrowserInfo.BrowserFamily.class);

		mappaAbilitazioneGraficiSVG.put(BrowserFamily.CHROME, 4D);
		mappaAbilitazioneGraficiSVG.put(BrowserFamily.FIREFOX, 3D);
		mappaAbilitazioneGraficiSVG.put(BrowserFamily.IE, 9D);
		mappaAbilitazioneGraficiSVG.put(BrowserFamily.OPERA, 10.1D);
		mappaAbilitazioneGraficiSVG.put(BrowserFamily.SAFARI, 3.2D);
		
		if(mappaAbilitazioneVistaTransazioniCustom == null)
			mappaAbilitazioneVistaTransazioniCustom = new EnumMap<>(BrowserInfo.BrowserFamily.class);

		mappaAbilitazioneVistaTransazioniCustom.put(BrowserFamily.CHROME, 29D);
		mappaAbilitazioneVistaTransazioniCustom.put(BrowserFamily.FIREFOX, 28D);
		mappaAbilitazioneVistaTransazioniCustom.put(BrowserFamily.IE, 11D);
		mappaAbilitazioneVistaTransazioniCustom.put(BrowserFamily.OPERA, 12.1D);
		mappaAbilitazioneVistaTransazioniCustom.put(BrowserFamily.SAFARI, 9D);
	}

	public static boolean disabilitaGraficiSVG(BrowserInfo browserInfo){
		boolean disabilita = false;

		if(mappaAbilitazioneGraficiSVG == null)
			loadMappaBrowser();

		if(browserInfo != null){
			Double versione = mappaAbilitazioneGraficiSVG.get(browserInfo.getBrowserFamily());

			if(versione != null && browserInfo.getVersion() != null && versione.doubleValue() >= browserInfo.getVersion().doubleValue())
				disabilita = true;
		}

		return disabilita;
	}
	
	public static boolean abilitaVisualizzazioneTransazioniCustom(BrowserInfo browserInfo){
		boolean abilita = false;

		if(mappaAbilitazioneVistaTransazioniCustom == null)
			loadMappaBrowser();

		if(browserInfo != null){
			Double versione = mappaAbilitazioneVistaTransazioniCustom.get(browserInfo.getBrowserFamily());

			if(versione != null && browserInfo.getVersion() != null && browserInfo.getVersion().doubleValue() >= versione.doubleValue())
				abilita = true;
		}

		return abilita;
	}


	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		// Analisi dello user agent
		String userAgent = request.getHeader(Costanti.USER_AGENT_HEADER_NAME);
		
		if(userAgent != null) {

			try{

				log.debug("Decodifica Browser da Header UserAgent [{}]", userAgent);
				String urlRichiesta = request.getServletPath(); 
				log.debug("Richiesta Risorsa [{}]", urlRichiesta);
				BrowserInfo browserInfo = BrowserInfo.getBrowserInfo(userAgent);

				String browsername = browserInfo.getBrowserName();
				Double browserversion = browserInfo.getVersion();

				log.debug("Browser Riconosciuto: Name [{}] Version [{}].", browsername, browserversion);

				//controllo se e' presente il parametro usaSVG
				boolean usaSVG = usaSVG(request);
				
				if(browserInfo.getBrowserFamily().equals(BrowserFamily.IE)){

					boolean abilitaModalitaIE8 = true;
					if(usaSVG){
						log.debug("Richiesto Accesso per La risorsa protetta.");
						abilitaModalitaIE8 = disabilitaGraficiSVG(browserInfo);
					}

					log.debug("La risorsa richiesta {}verra' visualizzata in modalita compatibilita IE8.", (abilitaModalitaIE8 ? "" : "non "));
					//Imposto l'header http necessario per forzare la visualizzazione.
					// per tutte le versioni
					response.setHeader("X-UA-Compatible", "IE=edge");

					// Risolvo anche il problema di ie9 che non visualizza il contenuto dei file css della libreria Richfaces.
					// Esso invia solo "text/css" all'interno dell' Accept header.
					// la classe HtmlRenderUtils lancia un eccezione poiche' non gestisce questo tipo di Accept
					// allora la soluzione e' una patch del codice oppure aggiornare il valore dell'header Accept per far si che non
					// venga sollevata l'eccezione: applico questa soluzione impostando "text/css, */*" al posto di "text/css".

					String accept = request.getHeader("Accept");

					if ("text/css".equals(accept)) {
						chain.doFilter(new IEFilter().new IE9HttpServletRequestWrapper(request), response);

					}
					chain.doFilter(request, response);
				}
				else if(browserInfo.getBrowserFamily().equals(BrowserFamily.FIREFOX)){
					// solo sui path /a4j/*

					List<String> lst = new ArrayList<>();
					lst.add("/a4j/");
					if(ContentAuthorizationManager.getInstance().contains(urlRichiesta, lst )){
						log.debug("Applico Fix per le risorse A4j in firefox.");
						// Dalla classe RichFacesFirefox11Filter Filtro per le risorse JSF in firefox

						chain.doFilter(new HttpServletRequestWrapper(request) {
							@Override
							public String getRequestURI() {
								try {
									return URLDecoder.decode(super.getRequestURI(), "UTF-8");
								} catch (UnsupportedEncodingException e) {

									throw new IllegalStateException("Cannot decode request URI.", e);
								}
							}
						}, response);
					}else 
						chain.doFilter(request, response);
				} else {
					chain.doFilter(request, response);
				}
			} catch(Exception e){
				log.debug("Browser non riconosciuto.");
				chain.doFilter(request, response);
			}
		}
	}

	/***************************************************************************
	 * Metodo destroy
	 */
	@Override
	public void destroy() {
		log.debug("DISTRUIZIONE FILTRO: BrowserFilter");
		this.filterConfig = null;
	}

	/***************************************************************************
	 * Init
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {
		this.filterConfig = config;
		
		try {
			this.listaPagineNoIE8 = Arrays.asList(ContentAuthorizationManager.getInstance().getListaPagineNoIE8());
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
	
	
	/**
	 * Controlla che la pagina richiesta sia tra quelle che non necessitano di filtro sui contenuti,
	 * sono "libere" le pagine di login e timeout, e i path delle risorse richiesta dinamicamente dal framework 
	 *	
	 */
	public boolean usaSVG(HttpServletRequest httpServletRequest) {
		int svgLength = 0;
		String svg = null;
		
		
		Enumeration<String> parameterNames = httpServletRequest.getParameterNames(); 
		
		while (parameterNames.hasMoreElements()) {
			String parName = parameterNames.nextElement();
			String parValue = httpServletRequest.getParameter(parName);
			log.trace("Parametro [{}] con Valore [{}].", parName, parValue);
			if(parName!= null && parName.endsWith(PARAMETRO_SVG)){
				svg = parValue;
				
				log.trace("Parametro [{}] con Valore [{}] Utilizzato per pilotare il disegno dei grafici.", parName, parValue);
				
				// controllo solo nei form delle statistiche se sto nella schermata form non devo fare cambio di modalita' se navigo si.
				if(parName.endsWith(PARAMETRO_SVG_FORM_STATS)){
					 String paramGeneraReport = getParamValue(httpServletRequest, PARAMETRO_GENERA_REPORT);
					 String paramTipoReport = getParamValue(httpServletRequest, PARAMETRO_TIPO_REPORT);
					 log.trace("Caso speciale Form Statistiche: Parametro [{}] con Valore [{}] Utilizzato per pilotare il disegno dei grafici.", PARAMETRO_GENERA_REPORT, paramGeneraReport);
					 if(StringUtils.isEmpty(paramGeneraReport))
						 svg = null;
					 else {
						 if(StringUtils.isNotEmpty(paramTipoReport) && paramTipoReport.equals(PARAMETRO_TIPO_REPORT_TABELLA)){
							 log.trace("Caso speciale Form Statistiche: Parametro [{}] con Valore [{}] Visualizzazione del report in forma di tabella.", PARAMETRO_TIPO_REPORT, paramTipoReport);
							 svg = null;
						 }
					 }
				}
				
				// attivo il controllo SVG solo se ho cliccato nel menu' esitiLive
				if(parName.endsWith(PARAMETRO_SVG_FORM_MENU)){
					 String paramEsitiLive =  getParamValue(httpServletRequest, PARAMETRO_ESITI_LIVE);
					 log.trace("Caso speciale Menu': Parametro [{}] con Valore [{}] Utilizzato per pilotare il disegno dei grafici.", PARAMETRO_ESITI_LIVE, paramEsitiLive);
					 if(StringUtils.isEmpty(paramEsitiLive))
						 svg = null;
				}
				
				// attivo il controllo SVG quando il polling dello stato si refresha 
				if(parName.endsWith(PARAMETRO_SVG_POLL_STATO)){
					boolean thisResource = !Utils.isContentAuthorizationRequiredForThisResource(httpServletRequest, this.listaPagineNoIE8);
					
					log.trace("Caso speciale Menu': Parametro [{}] con Valore [{}] Utilizzato per pilotare il disegno dei grafici.", PARAMETRO_SVG_POLL_STATO, thisResource);
					if(!thisResource)
						 svg = null;
				}
				
				break;
			}
		}

		if (svg != null) {
			svgLength = svg.length();
		} 
		
		log.trace("Attivo controllo SVG [{}]", (svgLength > 0 ? "SI" : "NO" ));
		
		return svgLength > 0;
	}
	
	public static String getParamValue(HttpServletRequest httpServletRequest, String paramName){
		Enumeration<String> parameterNames = httpServletRequest.getParameterNames(); 
		
		while (parameterNames.hasMoreElements()) {
			String parName = parameterNames.nextElement();
			if(parName!= null && parName.contains(paramName)){
				String parameterValue = httpServletRequest.getParameter(parName);
				log.debug("Trovato Parametro [{}] con Valore [{}].", parName, parameterValue);
				return parameterValue;
			}
		}
		return null;
			
	}
}
