/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Properties;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.transport.IFilter;
import org.slf4j.Logger;

import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;

/**
 * GestoreFilter
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class GestoreFilter implements Filter{

	private Logger log = LoggerManager.getPddMonitorCoreLogger();

	private SortedMap<IFilter> filterChain = null;
	private boolean gestioneFiltriAbilitata = false;
	private boolean last = false;
	public static final String PARAMETER_LAST= "last";
	private String myName = "Gestore Filtri";

	@Override
	public void init(FilterConfig config) throws ServletException {

		this.filterChain = new SortedMap<IFilter>();
		
		try{
			String lastS = config.getInitParameter(PARAMETER_LAST);
			if(lastS != null && lastS.length() >0)
				this.last = Boolean.parseBoolean(lastS);
			
				this.myName = this.last ? "Gestore Filtri Last" : "Gestore Filtri First";
			
			this.gestioneFiltriAbilitata = PddMonitorProperties.getInstance(this.log).isGestoreFiltriEnabled();
		}catch(Exception e){
			this.log.error("Errore durante la init del GestoreFilter: " + e.getMessage(),e); 
			this.gestioneFiltriAbilitata = false;
		}

		try{
			if(this.gestioneFiltriAbilitata) {
				String tipo = this.last ? "last" : "first";
				List<String> listaNomiFiltri = PddMonitorProperties.getInstance(this.log).getListaFiltri(tipo);

				this.log.debug(this.myName + " init dei filtri: ["+listaNomiFiltri+"]"); 
				for (String nomeFiltro : listaNomiFiltri) {
					String className =  PddMonitorProperties.getInstance(this.log).getClassNameFiltro(tipo, nomeFiltro);
					Properties propertiesFiltro = PddMonitorProperties.getInstance(this.log).getPropertiesFiltro(tipo, nomeFiltro);

					IFilter filtro = this.initFiltro(this.log, className);
					filtro.init(propertiesFiltro);

					this.filterChain.add(nomeFiltro, filtro);
				}
			}
		}catch(Exception e){
			this.log.error("Errore durante la init del Gestore Filtri: "+e.getMessage(), e);
			throw new ServletException(e);
		}

	}

	@Override
	public void destroy() {
		this.log.debug("DISTRUZIONE FILTRO: " + this.myName); 
		
		List<String> nomiFiltri = this.filterChain.keys(); 
		if(this.gestioneFiltriAbilitata && nomiFiltri.size() > 0) {
			for (int i = 0 ; i< nomiFiltri.size() ; i++) {
				this.filterChain.get(i).destroy();
			}
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain)
					throws IOException, ServletException {
		List<String> nomiFiltri = this.filterChain.keys(); 
		
		if(this.gestioneFiltriAbilitata && nomiFiltri.size() > 0) {
			this.log.debug(this.myName + " invocazione metodo doInput per i filtri indicati in corso...");
			for (int i = 0 ; i< nomiFiltri.size() ; i++) {
				String nomeFiltro = nomiFiltri.get(i);
				try{
					this.log.debug(this.myName + " esecuzione del metodo ["+nomeFiltro+".doInput] in corso...");
					this.filterChain.get(i).doInput(request, response, filterChain);
					this.log.debug(this.myName + " esecuzione del metodo ["+nomeFiltro+".doInput] completata.");
				}catch(IOException e){
					this.log.error(this.myName + " errore durante l'esecuzione del metodo ["+nomeFiltro+".doInput]: "+e.getMessage(), e);
					throw e;
				}catch (ServletException e) {
					this.log.error(this.myName + " errore durante l'esecuzione del metodo ["+nomeFiltro+".doInput]: "+e.getMessage(), e);
					throw e;
				}
			}
			this.log.debug(this.myName + " invocazione metodo doInput per i filtri indicati completata.");
		}

		filterChain.doFilter(request, response);

		if(this.gestioneFiltriAbilitata && nomiFiltri.size() > 0) {
			this.log.debug(this.myName + " invocazione metodo doOutput per i filtri indicati in corso...");
			for (int i = nomiFiltri.size() -1; i >= 0 ; i--) {
				String nomeFiltro = nomiFiltri.get(i);
				try{
					this.log.debug(this.myName + " esecuzione del metodo ["+nomeFiltro+".doOutput] in corso...");
					this.filterChain.get(i).doOutput(request, response, filterChain);
					this.log.debug(this.myName + " esecuzione del metodo ["+nomeFiltro+".doOutput] completata.");
				}catch(IOException e){
					this.log.error(this.myName + " errore durante l'esecuzione del metodo ["+nomeFiltro+".doOutput]: "+e.getMessage(), e);
					throw e;
				}catch (ServletException e) {
					this.log.error(this.myName + " errore durante l'esecuzione del metodo ["+nomeFiltro+".doOutput]: "+e.getMessage(), e);
					throw e;
				}
			}
			this.log.debug(this.myName + " invocazione metodo doOutput per i filtri indicati completata.");
		}
	}


	private IFilter initFiltro(Logger log, String className) throws Exception {
		log.debug("Caricamento classe ["+className+"] in corso...");
		try{
			Class<?> c = Class.forName(className);
			Constructor<?> constructor = c.getConstructor(Logger.class);
			IFilter p = (IFilter) constructor.newInstance(log);
			return  p;
		} catch (Exception e) {
			throw new Exception("Impossibile caricare la classe indicata ["+className+"] " + e.getMessage(), e);
		}
	}
}
