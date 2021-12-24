/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.config;

import java.io.Serializable;

import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.GruppoAccordo;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ForwardProxy
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ForwardProxy implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String GOVWAY_SEPARATORE = "-"; 
	
	public static final String GOVWAY_PREFISSO_TAG_PROXY = "tag"+GOVWAY_SEPARATORE;
	public static final String GOVWAY_PREFISSO_DOMINIO_PROXY = "dominio"+GOVWAY_SEPARATORE;
	public static final String GOVWAY_PREFISSO_PROTOCOLLO_PROXY = "profilo"+GOVWAY_SEPARATORE;
	
	public static final String GOVWAY_PROXY_ENABLED = "govway-proxy-enable";
	
	public static final String GOVWAY_PROXY = "govway-proxy";
	public static final String GOVWAY_FRUIZIONI_PROXY = "govway-fruizioni-proxy";
	public static final String GOVWAY_EROGAZIONI_PROXY = "govway-erogazioni-proxy";
	
	public static final String GOVWAY_PROXY_HEADER = "govway-proxy-header";
	public static final String GOVWAY_FRUIZIONI_PROXY_HEADER = "govway-fruizioni-proxy-header";
	public static final String GOVWAY_EROGAZIONI_PROXY_HEADER = "govway-erogazioni-proxy-header";
	public static final String GOVWAY_PROXY_HEADER_BASE64 = "govway-proxy-header-base64";
	public static final String GOVWAY_FRUIZIONI_PROXY_HEADER_BASE64 = "govway-fruizioni-proxy-header-base64";
	public static final String GOVWAY_EROGAZIONI_PROXY_HEADER_BASE64 = "govway-erogazioni-proxy-header-base64";
	
	public static final String GOVWAY_PROXY_QUERY = "govway-proxy-query";
	public static final String GOVWAY_FRUIZIONI_PROXY_QUERY = "govway-fruizioni-proxy-query";
	public static final String GOVWAY_EROGAZIONI_PROXY_QUERY = "govway-erogazioni-proxy-query";
	public static final String GOVWAY_PROXY_QUERY_BASE64 = "govway-proxy-query-base64";
	public static final String GOVWAY_FRUIZIONI_PROXY_QUERY_BASE64 = "govway-fruizioni-proxy-query-base64";
	public static final String GOVWAY_EROGAZIONI_PROXY_QUERY_BASE64 = "govway-erogazioni-proxy-query-base64";
	
	public static final String DEFAULT_GOVWAY_PROXY_HEADER = "GovWay-APIAddress";
	public static final boolean DEFAULT_GOVWAY_PROXY_HEADER_BASE64 = false; 
	public static final String DEFAULT_GOVWAY_PROXY_QUERY = "govway_api_address";
	public static final boolean DEFAULT_GOVWAY_PROXY_QUERY_BASE64 = false; 
	
	public static boolean isProxyEnabled() {
		String enable = System.getProperty(GOVWAY_PROXY_ENABLED);
		if(enable!=null && !"".equals(enable)) {
			return "true".equalsIgnoreCase(enable);
		}
		return OpenSPCoop2Properties.getInstance().isForwardProxyEnable();
	}
	
	
	public static ForwardProxy getProxyConfigurazione(boolean fruizione, IDSoggetto dominio, IDServizio idServizio) throws DriverConfigurazioneException{
		
		try {
			
			// **** Configurazione di default ****
			
			// Cerco se ne esiste una specifica per il protocollo e la fruizione/erogazione
			ForwardProxyConfigurazione defaultConfig = readForwardProxyConfigurazione(true, dominio.getTipo(), fruizione);
			
			// Cerco se ne esiste una specifica per il protocollo che vale sia per la fruizione che per l'erogazione
			if(defaultConfig==null) {
				defaultConfig = readForwardProxyConfigurazione(true, dominio.getTipo(), null);
			}
			
			// Cerco se ne esiste una specifica per la fruizione/erogazione
			if(defaultConfig==null) {
				defaultConfig = readForwardProxyConfigurazione(false, null, fruizione);
			}
			
			// Cerco se ne esiste una che vale sia per la fruizione che per l'erogazione
			if(defaultConfig==null) {
				defaultConfig = readForwardProxyConfigurazione(false, null, null);
			}
			
			// Configurazione di default
			if(defaultConfig==null) {
				defaultConfig = OpenSPCoop2Properties.getInstance().getForwardProxyConfigurazioneDefault();
			}
					
			
			
			// **** Regola ****
			
			// primo cerco per tag
			RegistroServiziManager registroServiziManager = RegistroServiziManager.getInstance();
			AccordoServizioParteSpecifica asps = registroServiziManager.getAccordoServizioParteSpecifica(idServizio, null, false);
			IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
			AccordoServizioParteComune aspc = registroServiziManager.getAccordoServizioParteComune(idAccordo, null, false);
			if(aspc.getGruppi()!=null && aspc.getGruppi().sizeGruppoList()>0) {
				for (GruppoAccordo gruppo : aspc.getGruppi().getGruppoList()) {
					// prima per specifica fruizione/erogazione
					ForwardProxy regola = ForwardProxy.readForwardProxyTag(gruppo.getNome(), fruizione, defaultConfig);
					if(regola!=null && regola.isEnabled()) {
						return regola;
					}
					// poi per configurazione che vale sia per la fruizione che per l'erogazione
					regola = ForwardProxy.readForwardProxyTag(gruppo.getNome(), null, defaultConfig);
					if(regola!=null && regola.isEnabled()) {
						return regola;
					}
				}
			}
			
			// poi cerco per dominio
			
			// prima per specifico tipo (es. modipa, gw, aoo, spc) e nome soggetto per specifica fruizione/erogazione
			ForwardProxy regola = ForwardProxy.readConfigurazioneDominioByTipo(dominio.getTipo(), dominio.getNome(), fruizione, defaultConfig);
			if(regola!=null && regola.isEnabled()) {
				return regola;
			}
			// poi per specifico tipo (es. modipa, gw, aoo, spc) e nome soggetto che vale sia per la fruizione che per l'erogazione
			regola = ForwardProxy.readConfigurazioneDominioByTipo(dominio.getTipo(), dominio.getNome(), null, defaultConfig);
			if(regola!=null && regola.isEnabled()) {
				return regola;
			}
			
			// poi più generico sul protocollo e nome soggetto per specifica fruizione/erogazione
			regola = ForwardProxy.readConfigurazioneDominioByProtocollo(dominio.getTipo(), dominio.getNome(), fruizione, defaultConfig);
			if(regola!=null && regola.isEnabled()) {
				return regola;
			}
			// poi più generico sul protocollo e nome soggetto che vale sia per la fruizione che per l'erogazione
			regola = ForwardProxy.readConfigurazioneDominioByProtocollo(dominio.getTipo(), dominio.getNome(), null, defaultConfig);
			if(regola!=null && regola.isEnabled()) {
				return regola;
			}
			
			// poi solamente per nome soggetto per specifica fruizione/erogazione
			regola = ForwardProxy.readConfigurazioneDominio(dominio.getNome(), fruizione, defaultConfig);
			if(regola!=null && regola.isEnabled()) {
				return regola;
			}
			// poi solamente per nome soggetto che vale sia per la fruizione che per l'erogazione
			regola = ForwardProxy.readConfigurazioneDominio(dominio.getNome(), null, defaultConfig);
			if(regola!=null && regola.isEnabled()) {
				return regola;
			}
			
			
			// poi cerco per protocollo per specifica fruizione/erogazione
			regola = ForwardProxy.readConfigurazioneProtocollo(dominio.getTipo(), fruizione, defaultConfig);
			if(regola!=null && regola.isEnabled()) {
				return regola;
			}
			// poi cerco per protocollo che vale sia per la fruizione che per l'erogazione
			regola = ForwardProxy.readConfigurazioneProtocollo(dominio.getTipo(), null, defaultConfig);
			if(regola!=null && regola.isEnabled()) {
				return regola;
			}
			
			
			// poi cerco configurazione generica per specifica fruizione/erogazione
			regola = ForwardProxy.readConfigurazione(fruizione, defaultConfig);
			if(regola!=null && regola.isEnabled()) {
				return regola;
			}
			// altrimenti ritorno regola di default (eventualmente disabilitata per motivi di caching)
			return ForwardProxy.readConfigurazione(null, defaultConfig);
			
		}catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		
	}
	
	
	private static ForwardProxyConfigurazione readForwardProxyConfigurazione(boolean byProtocollo, String tipoSoggetto, Boolean fruizione) throws ProtocolException {
		
		String systemPropertyProxyHeader = GOVWAY_PROXY_HEADER;
		String systemPropertyProxyHeaderBase64 = GOVWAY_PROXY_HEADER_BASE64;
		String systemPropertyProxyQuery = GOVWAY_PROXY_QUERY;
		String systemPropertyProxyQueryBase64 = GOVWAY_PROXY_QUERY_BASE64;
		
		if(fruizione!=null) {
			systemPropertyProxyHeader = fruizione ? GOVWAY_FRUIZIONI_PROXY_HEADER : GOVWAY_EROGAZIONI_PROXY_HEADER;
			systemPropertyProxyHeaderBase64 = fruizione ? GOVWAY_FRUIZIONI_PROXY_HEADER_BASE64 : GOVWAY_EROGAZIONI_PROXY_HEADER_BASE64;
			systemPropertyProxyQuery = fruizione ? GOVWAY_FRUIZIONI_PROXY_QUERY : GOVWAY_EROGAZIONI_PROXY_QUERY;
			systemPropertyProxyQueryBase64 = fruizione ? GOVWAY_FRUIZIONI_PROXY_QUERY_BASE64 : GOVWAY_EROGAZIONI_PROXY_QUERY_BASE64;
		}
		
		if(byProtocollo) {
			String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(tipoSoggetto);
			systemPropertyProxyHeader= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyHeader;
			systemPropertyProxyHeaderBase64= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyHeaderBase64;
			systemPropertyProxyQuery= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyQuery;
			systemPropertyProxyQueryBase64= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyQueryBase64;
		}
		
		String header = System.getProperty(systemPropertyProxyHeader);
		String query = System.getProperty(systemPropertyProxyQuery);
			
		if( 
				(header==null || "".equals(header)) &&
				(query==null || "".equals(query)) 
			) {
			// verrà usata quella di default
			return null;
		}
		
		ForwardProxyConfigurazione config = new ForwardProxyConfigurazione();
				
		if(header!=null && !"".equals(header)) {
			config.header = header;
			
			String base64 = System.getProperty(systemPropertyProxyHeaderBase64);
			if(base64!=null && !"".equals(base64)) {
				if("true".equalsIgnoreCase(base64)) {
					config.headerBase64 = true;
				}
				else if("false".equalsIgnoreCase(base64)) {
					config.headerBase64 = false;
				}
				else {
					config.headerBase64 = DEFAULT_GOVWAY_PROXY_HEADER_BASE64;
				}
			}
			else {
				config.headerBase64 = DEFAULT_GOVWAY_PROXY_HEADER_BASE64;
			}
		}
		
		if(query!=null && !"".equals(query)) {
			config.query = query;
			
			String base64 = System.getProperty(systemPropertyProxyQueryBase64);
			if(base64!=null && !"".equals(base64)) {
				if("true".equalsIgnoreCase(base64)) {
					config.queryBase64 = true;
				}
				else if("false".equalsIgnoreCase(base64)) {
					config.queryBase64 = false;
				}
				else {
					config.queryBase64 = DEFAULT_GOVWAY_PROXY_QUERY_BASE64;
				}
			}
			else {
				config.queryBase64 = DEFAULT_GOVWAY_PROXY_QUERY_BASE64;
			}
		}
		
		return config;
		
	}
	
	private static ForwardProxy readForwardProxyTag(String tag, Boolean fruizione, ForwardProxyConfigurazione defaultConfig) throws ProtocolException {
		return readForwardProxy(tag, false, null, null, fruizione, defaultConfig);
	}
	private static ForwardProxy readConfigurazioneDominioByTipo(String tipoSoggetto, String nomeSoggetto, Boolean fruizione, ForwardProxyConfigurazione defaultConfig) throws ProtocolException {
		return readForwardProxy(null, false, tipoSoggetto, nomeSoggetto, fruizione, defaultConfig);
	}
	private static ForwardProxy readConfigurazioneDominioByProtocollo(String tipoSoggetto, String nomeSoggetto, Boolean fruizione, ForwardProxyConfigurazione defaultConfig) throws ProtocolException {
		return readForwardProxy(null, true, tipoSoggetto, nomeSoggetto, fruizione, defaultConfig);
	}
	private static ForwardProxy readConfigurazioneDominio(String nomeSoggetto, Boolean fruizione, ForwardProxyConfigurazione defaultConfig) throws ProtocolException {
		return readForwardProxy(null, false, null, nomeSoggetto, fruizione, defaultConfig);
	}
	private static ForwardProxy readConfigurazioneProtocollo(String tipoSoggetto, Boolean fruizione, ForwardProxyConfigurazione defaultConfig) throws ProtocolException {
		return readForwardProxy(null, true, tipoSoggetto, null, fruizione, defaultConfig);
	}
	private static ForwardProxy readConfigurazione(Boolean fruizione, ForwardProxyConfigurazione defaultConfig) throws ProtocolException {
		return readForwardProxy(null, false, null, null, fruizione, defaultConfig);
	}
	private static ForwardProxy readForwardProxy(String tag, boolean byProtocollo, String tipoSoggetto, String nomeSoggetto, Boolean fruizione,
			ForwardProxyConfigurazione defaultConfig) throws ProtocolException {
		
		String systemPropertyProxy = GOVWAY_PROXY;
		String systemPropertyProxyHeader = GOVWAY_PROXY_HEADER;
		String systemPropertyProxyHeaderBase64 = GOVWAY_PROXY_HEADER_BASE64;
		String systemPropertyProxyQuery = GOVWAY_PROXY_QUERY;
		String systemPropertyProxyQueryBase64 = GOVWAY_PROXY_QUERY_BASE64;
		
		if(fruizione!=null) {
			systemPropertyProxy = fruizione ? GOVWAY_FRUIZIONI_PROXY : GOVWAY_EROGAZIONI_PROXY;
			systemPropertyProxyHeader = fruizione ? GOVWAY_FRUIZIONI_PROXY_HEADER : GOVWAY_EROGAZIONI_PROXY_HEADER;
			systemPropertyProxyHeaderBase64 = fruizione ? GOVWAY_FRUIZIONI_PROXY_HEADER_BASE64 : GOVWAY_EROGAZIONI_PROXY_HEADER_BASE64;
			systemPropertyProxyQuery = fruizione ? GOVWAY_FRUIZIONI_PROXY_QUERY : GOVWAY_EROGAZIONI_PROXY_QUERY;
			systemPropertyProxyQueryBase64 = fruizione ? GOVWAY_FRUIZIONI_PROXY_QUERY_BASE64 : GOVWAY_EROGAZIONI_PROXY_QUERY_BASE64;
		}
		
		if(tag!=null) {
			systemPropertyProxy= GOVWAY_PREFISSO_TAG_PROXY+ tag +GOVWAY_SEPARATORE+ systemPropertyProxy;
			systemPropertyProxyHeader= GOVWAY_PREFISSO_TAG_PROXY+ tag +GOVWAY_SEPARATORE+ systemPropertyProxyHeader;
			systemPropertyProxyHeaderBase64= GOVWAY_PREFISSO_TAG_PROXY+ tag +GOVWAY_SEPARATORE+ systemPropertyProxyHeaderBase64;
			systemPropertyProxyQuery= GOVWAY_PREFISSO_TAG_PROXY+ tag +GOVWAY_SEPARATORE+ systemPropertyProxyQuery;
			systemPropertyProxyQueryBase64= GOVWAY_PREFISSO_TAG_PROXY+ tag +GOVWAY_SEPARATORE+ systemPropertyProxyQueryBase64;
		}
		else if(nomeSoggetto!=null) {
			if(tipoSoggetto==null) {
				systemPropertyProxy= GOVWAY_PREFISSO_DOMINIO_PROXY + nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxy;
				systemPropertyProxyHeader= GOVWAY_PREFISSO_DOMINIO_PROXY + nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyHeader;
				systemPropertyProxyHeaderBase64= GOVWAY_PREFISSO_DOMINIO_PROXY + nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyHeaderBase64;
				systemPropertyProxyQuery= GOVWAY_PREFISSO_DOMINIO_PROXY + nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyQuery;
				systemPropertyProxyQueryBase64= GOVWAY_PREFISSO_DOMINIO_PROXY + nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyQueryBase64;
			}
			else {
				if(byProtocollo) {
					String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(tipoSoggetto);
					systemPropertyProxy= GOVWAY_PREFISSO_DOMINIO_PROXY + protocollo+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxy;
					systemPropertyProxyHeader= GOVWAY_PREFISSO_DOMINIO_PROXY + protocollo+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyHeader;
					systemPropertyProxyHeaderBase64= GOVWAY_PREFISSO_DOMINIO_PROXY + protocollo+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyHeaderBase64;
					systemPropertyProxyQuery= GOVWAY_PREFISSO_DOMINIO_PROXY + protocollo+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyQuery;
					systemPropertyProxyQueryBase64= GOVWAY_PREFISSO_DOMINIO_PROXY + protocollo+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyQueryBase64;
				}
				else {
					systemPropertyProxy= GOVWAY_PREFISSO_DOMINIO_PROXY + tipoSoggetto+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxy;
					systemPropertyProxyHeader= GOVWAY_PREFISSO_DOMINIO_PROXY + tipoSoggetto+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyHeader;
					systemPropertyProxyHeaderBase64= GOVWAY_PREFISSO_DOMINIO_PROXY + tipoSoggetto+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyHeaderBase64;
					systemPropertyProxyQuery= GOVWAY_PREFISSO_DOMINIO_PROXY + tipoSoggetto+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyQuery;
					systemPropertyProxyQueryBase64= GOVWAY_PREFISSO_DOMINIO_PROXY + tipoSoggetto+GOVWAY_SEPARATORE+nomeSoggetto +GOVWAY_SEPARATORE+ systemPropertyProxyQueryBase64;
				}
			}
		}
		else if(byProtocollo) {
			String protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(tipoSoggetto);
			systemPropertyProxy= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxy;
			systemPropertyProxyHeader= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyHeader;
			systemPropertyProxyHeaderBase64= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyHeaderBase64;
			systemPropertyProxyQuery= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyQuery;
			systemPropertyProxyQueryBase64= GOVWAY_PREFISSO_PROTOCOLLO_PROXY+ protocollo +GOVWAY_SEPARATORE+ systemPropertyProxyQueryBase64;
		}
		
		ForwardProxy sConfig = new ForwardProxy();
		
		String url = System.getProperty(systemPropertyProxy);
		if(url!=null && !"".equals(url)) {
			sConfig.enabled=true;
			sConfig.url = url;
			
			String header = System.getProperty(systemPropertyProxyHeader);
			String query = System.getProperty(systemPropertyProxyQuery);
			
			if( 
				(header==null || "".equals(header)) &&
				(query==null || "".equals(query)) 
			) {
				// verrà usata quella di default
				sConfig.config = defaultConfig;
			}
			else {
				
				ForwardProxyConfigurazione config = new ForwardProxyConfigurazione();
				sConfig.config = config;
				
				if(header!=null && !"".equals(header)) {
					config.header = header;
					
					String base64 = System.getProperty(systemPropertyProxyHeaderBase64);
					if(base64!=null && !"".equals(base64)) {
						if("true".equalsIgnoreCase(base64)) {
							config.headerBase64 = true;
						}
						else if("false".equalsIgnoreCase(base64)) {
							config.headerBase64 = false;
						}
						else {
							config.headerBase64 = DEFAULT_GOVWAY_PROXY_HEADER_BASE64;
						}
					}
					else {
						config.headerBase64 = DEFAULT_GOVWAY_PROXY_HEADER_BASE64;
					}
				}
				
				if(query!=null && !"".equals(query)) {
					config.query = query;
					
					String base64 = System.getProperty(systemPropertyProxyQueryBase64);
					if(base64!=null && !"".equals(base64)) {
						if("true".equalsIgnoreCase(base64)) {
							config.queryBase64 = true;
						}
						else if("false".equalsIgnoreCase(base64)) {
							config.queryBase64 = false;
						}
						else {
							config.queryBase64 = DEFAULT_GOVWAY_PROXY_QUERY_BASE64;
						}
					}
					else {
						config.queryBase64 = DEFAULT_GOVWAY_PROXY_QUERY_BASE64;
					}
				}
				
			}
		}
		
		return sConfig;
		
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("enable: ").append(this.enabled);
		if(this.enabled) {
			sb.append("\n").append("url: ").append(this.url);
		}
		if(this.config!=null) {
			sb.append("\n").append(this.config.toString());
		}
		return sb.toString();
	}
		
	
	private boolean enabled = false;
	private String url;
	private ForwardProxyConfigurazione config;
	
	public ForwardProxy() {
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ForwardProxyConfigurazione getConfig() {
		return this.config;
	}

	public void setConfig(ForwardProxyConfigurazione config) {
		this.config = config;
	}

	
}
