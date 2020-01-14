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

package org.openspcoop2.pdd.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.ConfigurazioneUrlInvocazione;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.RuoloContesto;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.manifest.Context;
import org.openspcoop2.protocol.manifest.WebEmptyContext;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.utils.PorteNamingUtils;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.regexp.RegExpNotFoundException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;

/**
 * UrlInvocazioneAPI
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UrlInvocazioneAPI implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String baseUrl;
	private String context;
	
	public String getBaseUrl() {
		return this.baseUrl;
	}
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	public String getContext() {
		return this.context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getUrl() {
		if(this.baseUrl==null) {
			return null;
		}
		return Utilities.buildUrl(this.baseUrl, this.context);
	}

	
	
	
	// *** STATIC UTILITIES **
	
	public static UrlInvocazioneAPI getConfigurazioneUrlInvocazione(ConfigurazioneUrlInvocazione configurazioneUrlInvocazione, 
			IProtocolFactory<?> protocolFactory, RuoloContesto ruolo, ServiceBinding serviceBinding, 
			String interfaceName, IDSoggetto soggettoOperativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		if(configurazioneUrlInvocazione==null) {
			configurazioneUrlInvocazione = new ConfigurazioneUrlInvocazione();
			initConfigurazioneProtocolloUrlInvocazione(configurazioneUrlInvocazione);
		}
		
		// normalizzo interfaceName
		String interfaceNameNormalizzata = interfaceName;
		if(interfaceNameNormalizzata!=null) {
			try {
				PorteNamingUtils utils = new PorteNamingUtils(protocolFactory);
				if( RuoloContesto.PORTA_DELEGATA.equals(ruolo) ) {
					interfaceNameNormalizzata = utils.normalizePD(interfaceName);
				}
				else {
					interfaceNameNormalizzata = utils.normalizePA(interfaceName);
				}
			}catch(Exception e){
				protocolFactory.getLogger().debug("Normalizzazione interface name '"+interfaceName+"' non riuscita: "+e.getMessage(),e);
			}
		}
		
		// Regola di default
		ConfigurazioneUrlInvocazioneRegola regolaDefault = getConfigurazioneUrlInvocazioneDefault(configurazioneUrlInvocazione, protocolFactory, ruolo, serviceBinding, interfaceNameNormalizzata); // default
		
		// Colleziono regole compatibili
		ConfigurazioneUrlInvocazioneRegola regola = null;
		Map<String, ConfigurazioneUrlInvocazioneRegola> regole = new HashMap<>();
		for (ConfigurazioneUrlInvocazioneRegola check : configurazioneUrlInvocazione.getRegolaList()) {
			regole.put(check.getPosizione()+"", check);
		}
		if(regole.size()>0) {
			List<String> posizioni = new ArrayList<>();
			posizioni.addAll(regole.keySet());
			Collections.sort(posizioni);
			for (String posizione : posizioni) {
				ConfigurazioneUrlInvocazioneRegola check = regole.get(posizione);
				try {
					//System.out.println("ESAMINO REGOLA ALLA POSIZIONE '"+posizione+"'");
					String contesto = getContext(protocolFactory, ruolo, serviceBinding, interfaceNameNormalizzata);
					if(isMatchRegolaUrlInvocazione(check, protocolFactory, ruolo, serviceBinding, contesto, soggettoOperativo)) { 
						regola = processMatchRegolaUrlInvocazione(check, contesto); // risolve eventuale match di espressioni regolari.
						break;
					}	
				}catch(Exception e) {
					protocolFactory.getLogger().error("Errore durante il processamento della regola-"+posizione+" '"+check.getNome()+"': "+e.getMessage(),e);
				}
			}
		}
		if(regola==null) {
			regola = regolaDefault;
		}
		
		UrlInvocazioneAPI url = new UrlInvocazioneAPI();
		if(regola.getBaseUrl()!=null) {
			url.setBaseUrl(regola.getBaseUrl());
		}
		else {
			if( RuoloContesto.PORTA_DELEGATA.equals(ruolo) && configurazioneUrlInvocazione.getBaseUrlFruizione()!=null ) {
				url.setBaseUrl(configurazioneUrlInvocazione.getBaseUrlFruizione());
			}
			else {
				url.setBaseUrl(configurazioneUrlInvocazione.getBaseUrl());
			}
		}
		url.setContext(regola.getContestoEsterno());
		
		return url;
	}
	
	private static void initConfigurazioneProtocolloUrlInvocazione(ConfigurazioneUrlInvocazione configurazioneUrlInvocazione) throws DriverConfigurazioneException {
		try {
			if(configurazioneUrlInvocazione.getBaseUrl()==null) {
				configurazioneUrlInvocazione.setBaseUrl(CostantiConfigurazione.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_URL_INVOCAZIONE_PREFIX);
			}
			
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	private static ConfigurazioneUrlInvocazioneRegola getConfigurazioneUrlInvocazioneDefault(ConfigurazioneUrlInvocazione configurazioneUrlInvocazione, 
			IProtocolFactory<?> protocolFactory, RuoloContesto ruolo, ServiceBinding serviceBinding, String interfaceNameNormalizzata) throws DriverConfigurazioneException {
	
		try {
			
			ConfigurazioneUrlInvocazioneRegola regola = new ConfigurazioneUrlInvocazioneRegola();
			regola.setNome(CostantiConfigurazione.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_URL_INVOCAZIONE_DEFAULT_RULE_NAME);
			regola.setPosizione(-1); // non serve, verrà usata questa regola
			regola.setStato(StatoFunzionalita.ABILITATO);
			regola.setDescrizione(CostantiConfigurazione.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_URL_INVOCAZIONE_DEFAULT_RULE_DESCRIPTION);
			regola.setRegexpr(false);
			regola.setRegola(""); // non serve, verrà usata questa regola per produrre la url di invocazione
			
			regola.setContestoEsterno(getContext(protocolFactory, ruolo, serviceBinding, interfaceNameNormalizzata));
			
			regola.setBaseUrl(null); // viene usata quella di default
			regola.setProtocollo(protocolFactory.getProtocol());
			regola.setRuolo(ruolo);
			if(serviceBinding!=null) {
				regola.setServiceBinding(ServiceBinding.REST.equals(serviceBinding) ? org.openspcoop2.core.config.constants.ServiceBinding.REST : org.openspcoop2.core.config.constants.ServiceBinding.SOAP);
			}		
			
			return regola;

		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	private static boolean isMatchRegolaUrlInvocazione(ConfigurazioneUrlInvocazioneRegola check, 
			IProtocolFactory<?> protocolFactory, RuoloContesto ruolo, ServiceBinding serviceBinding, 
			String contesto, IDSoggetto soggettoOperativoInterfaccia) throws DriverConfigurazioneException {
		
		if(StatoFunzionalita.DISABILITATO.equals(check.getStato())) {
			return false;
		}
		
		if(check.getProtocollo()!=null) {
			if(protocolFactory==null) {
				return false;
			}
			if(!check.getProtocollo().equals(protocolFactory.getProtocol())) {
				return false;
			}
		}
		
		if(check.getRuolo()!=null) {
			if(ruolo==null) {
				return false;
			}
			if(!check.getRuolo().equals(ruolo)) {
				return false;
			}	
		}
		
		if(check.getServiceBinding()!=null) {
			if(serviceBinding==null) {
				return false;
			}
			if(!check.getServiceBinding().name().equals(serviceBinding.name())) {
				return false;
			}	
		}
		
		if(check.getSoggetto()!=null && check.getSoggetto().getTipo()!=null && check.getSoggetto().getNome()!=null) {
			if(soggettoOperativoInterfaccia==null) {
				return false;
			}
			try {
				if(!check.getSoggetto().toIDSoggetto().equals(soggettoOperativoInterfaccia)) {
					return false;
				}
			}catch(Exception e) {
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}
		
		if(contesto==null) {
			return false;
		}
		if(check.isRegexpr()) {
			try {
				return RegularExpressionEngine.isMatch(contesto, check.getRegola());
			}catch(RegExpNotFoundException notFound) {
				return  false;
			}catch(Exception e) {
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}
		else {
			String contestDaVerificare = contesto;
			if(contesto.startsWith("/")) {
				if(check.getRegola().startsWith("/")==false) {
					if(contesto.length()==1) {
						contestDaVerificare = "";
					}
					else {
						contestDaVerificare = contestDaVerificare.substring(1);
					}
				}
			}
			else {
				if(check.getRegola().startsWith("/")) {
					contestDaVerificare = "/"+contestDaVerificare;
				}
			}
			return contestDaVerificare.startsWith(check.getRegola());
		}
	}
	
	private static ConfigurazioneUrlInvocazioneRegola processMatchRegolaUrlInvocazione(ConfigurazioneUrlInvocazioneRegola regolaParam, String contesto) throws DriverConfigurazioneException {
		
		ConfigurazioneUrlInvocazioneRegola regola = (ConfigurazioneUrlInvocazioneRegola) regolaParam.clone();
		
		if(regola.isRegexpr()) {
			try {
				List<String> list = RegularExpressionEngine.getAllStringMatchPattern(contesto, regola.getRegola());
				String newContesto = regola.getContestoEsterno();
				if(newContesto==null) {
					newContesto = "";
				}
				String baseUrl = regola.getBaseUrl();
				for (int i = 0; i < list.size(); i++) {
					String found = list.get(i);
					if(found==null) {
						found = "";
					}
					
					String key = "${"+i+"}";
					while(newContesto.contains(key)) {
						newContesto = newContesto.replace(key, found);
					}
					if(baseUrl!=null) {
						while(baseUrl.contains(key)) {
							baseUrl = baseUrl.replace(key, found);
						}	
					}
				}
				regola.setContestoEsterno(newContesto);
				regola.setBaseUrl(baseUrl);
				return regola;
			}
//			catch(RegExpNotFoundException notFound) { NON dovrebbe succedere, gestito prima con l'if
//				return  null; 
//			}
			catch(Exception e) {
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}
		else {
			return regola;
		}
		
	}
	
	
	private static String getContext(IProtocolFactory<?> protocolFactory, RuoloContesto ruolo, ServiceBinding serviceBinding, String interfaceNameNormalizzata) throws DriverConfigurazioneException {
	
		try {
						
			String contextWithoutBinding = null;
			String contextWithRestBinding = null;
			String contextWithSoapBinding = null;
			if(protocolFactory.getManifest().getWeb().getEmptyContext()!=null) {
				WebEmptyContext ctx = protocolFactory.getManifest().getWeb().getEmptyContext();
				if(ctx.getBinding()==null) {
					if(contextWithoutBinding==null) {
						contextWithoutBinding = "";
					}
				}
				else if(org.openspcoop2.protocol.manifest.constants.ServiceBinding.REST.equals(ctx.getBinding())) {
					if(contextWithRestBinding==null) {
						contextWithRestBinding = "";
					}
				}
				else if(org.openspcoop2.protocol.manifest.constants.ServiceBinding.SOAP.equals(ctx.getBinding())) {
					if(contextWithSoapBinding==null) {
						contextWithSoapBinding = "";
					}
				}
			}
			if(protocolFactory.getManifest().getWeb().sizeContextList()>0) {
				for (Context ctx : protocolFactory.getManifest().getWeb().getContextList()) {
					if(ctx.getBinding()==null) {
						if(contextWithoutBinding==null) {
							contextWithoutBinding = ctx.getName();
						}
					}
					else if(org.openspcoop2.protocol.manifest.constants.ServiceBinding.REST.equals(ctx.getBinding())) {
						if(contextWithRestBinding==null) {
							contextWithRestBinding = ctx.getName();
						}
					}
					else if(org.openspcoop2.protocol.manifest.constants.ServiceBinding.SOAP.equals(ctx.getBinding())) {
						if(contextWithSoapBinding==null) {
							contextWithSoapBinding = ctx.getName();
						}
					}
				}
			}
			
			// Assegno i contesti se non trovati altri.
			if(contextWithRestBinding==null) {
				contextWithRestBinding = contextWithoutBinding;
			}
			if(contextWithSoapBinding==null) {
				contextWithSoapBinding = contextWithoutBinding;
			}
			if(contextWithoutBinding==null) {
				throw new Exception("Contesto senza uno specifico binding non indicato");
			}
			
			String contestoEsterno = null;
			if(serviceBinding!=null) {
				switch (serviceBinding) {
				case REST:
					contestoEsterno = RuoloContesto.PORTA_DELEGATA.equals(ruolo) ? 
							CostantiConfigurazione.getDefaultValueParametroConfigurazioneProtocolloPrefixUrlInvocazionePd(contextWithRestBinding) :
							CostantiConfigurazione.getDefaultValueParametroConfigurazioneProtocolloPrefixUrlInvocazionePa(contextWithRestBinding);
					break;
				case SOAP:
					contestoEsterno = RuoloContesto.PORTA_DELEGATA.equals(ruolo) ? 
							CostantiConfigurazione.getDefaultValueParametroConfigurazioneProtocolloPrefixUrlInvocazionePd(contextWithSoapBinding) :
							CostantiConfigurazione.getDefaultValueParametroConfigurazioneProtocolloPrefixUrlInvocazionePa(contextWithSoapBinding);
					break;
				}
			}
			else {
				contestoEsterno = RuoloContesto.PORTA_DELEGATA.equals(ruolo) ? 
						CostantiConfigurazione.getDefaultValueParametroConfigurazioneProtocolloPrefixUrlInvocazionePd(contextWithoutBinding) :
						CostantiConfigurazione.getDefaultValueParametroConfigurazioneProtocolloPrefixUrlInvocazionePa(contextWithoutBinding);
			}
			
			if("trasparente".equalsIgnoreCase(protocolFactory.getProtocol())) {
				if(RuoloContesto.PORTA_APPLICATIVA.equals(ruolo)) {
					if(contestoEsterno.endsWith("in/")) {
						contestoEsterno = contestoEsterno.substring(0, contestoEsterno.length()-"in/".length());
					}
					else if(contestoEsterno.endsWith("in")) {
						contestoEsterno = contestoEsterno.substring(0, contestoEsterno.length()-"in".length());
					}
				}
			}
			else if("sdi".equalsIgnoreCase(protocolFactory.getProtocol())) {
				if(RuoloContesto.PORTA_DELEGATA.equals(ruolo)) {
					if(contestoEsterno.endsWith("out/")) {
						contestoEsterno = contestoEsterno + "xml2soap/";
					}
					else if(contestoEsterno.endsWith("out")) {
						contestoEsterno = contestoEsterno + "/xml2soap/";
					}
				}
			}
			
			
			boolean useInterfaceName = false;
			if(RuoloContesto.PORTA_APPLICATIVA.equals(ruolo)) {
				useInterfaceName = protocolFactory.createProtocolIntegrationConfiguration().useInterfaceNameInImplementationInvocationURL(serviceBinding);
			}
			else {
				useInterfaceName = protocolFactory.createProtocolIntegrationConfiguration().useInterfaceNameInSubscriptionInvocationURL(serviceBinding);
			}
						
			if(useInterfaceName && interfaceNameNormalizzata!=null && !"".equals(interfaceNameNormalizzata)) {
				contestoEsterno = contestoEsterno + "/" +interfaceNameNormalizzata;
			}
			
			while(contestoEsterno.contains("//")) {
				contestoEsterno = contestoEsterno.replace("//", "/");
			}
						
			return contestoEsterno;
			
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
}
