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

package org.openspcoop2.pdd.core.jmx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.constants.StatoCheck;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntime;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.config.InvokerNodiRuntime;
import org.openspcoop2.pdd.core.connettori.ConnettoreCheck;
import org.openspcoop2.protocol.registry.CertificateCheck;
import org.openspcoop2.protocol.registry.RegistroServiziReader;
import org.slf4j.Logger;


/**
 * AbstractConfigChecker
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractConfigChecker {

	private static String getErrorMessage(String risorsa,String nomeNodoRuntime,Exception e) {
		return "Errore durante la verifica dei certificati (jmxResource '"+risorsa+"') (node:"+nomeNodoRuntime+"): "+e.getMessage();
	}
	private static String getErrorMessageClasseNonGestita(Object o) {
		return "Classe '"+o.getClass().getName()+"' non gestita";
	}
	
	public static void addErrore(Map<String,List<String>> mapErrori, String errore, String nomeNodoRuntime) {
		if(mapErrori.containsKey(errore)) {
			List<String> l = mapErrori.get(errore);
			l.add(nomeNodoRuntime);
		}
		else {
			List<String> l = new ArrayList<>();
			l.add(nomeNodoRuntime);
			mapErrori.put(errore, l);
		}
	}
	
	public static void printErrore(Map<String,List<String>> mapErrori, StringBuilder sbDetails) {
		printErrore(mapErrori, sbDetails, -1, "");
	}
	public static void printErrore(Map<String,List<String>> mapErrori, StringBuilder sbDetails, int nodes, String multipleNodeSeparator) {
		if(!mapErrori.isEmpty()) {
			if(mapErrori.size()==1) {
				if(sbDetails.length()>0) {
					sbDetails.append("\n");
				}
				String errore = mapErrori.keySet().iterator().next();
				
				List<String> nodiRuntimeErrore = mapErrori.get(errore);
				if(nodes>1 && (nodiRuntimeErrore!=null && nodiRuntimeErrore.size()!=nodes)) {
					String nodesLabel = printNodes(mapErrori, errore);
					sbDetails.append("(").append(nodesLabel).append(") ");
				}
				
				sbDetails.append(errore);
			}
			else {
				boolean first = true;
				for (String errore : mapErrori.keySet()) {
					if(sbDetails.length()>0) {
						sbDetails.append("\n");
						sbDetails.append(multipleNodeSeparator);
					}
					else {
						if(first) {
							first = false;
						}
						else {
							sbDetails.append("\n");
							sbDetails.append(multipleNodeSeparator);
						}
					}
					
					String nodesLabel = printNodes(mapErrori, errore);
					sbDetails.append("(").append(nodesLabel).append(") ");
					
					sbDetails.append(errore);
				}
			}
		}
	}
	private static String printNodes(Map<String,List<String>> mapErrori, String errore) {
		List<String> nodiRuntimeErrore = mapErrori.get(errore);
		StringBuilder sbNodir = new StringBuilder();
		for (String nodo : nodiRuntimeErrore) {
			if(sbNodir.length()>0) {
				sbNodir.append(", ");
			}
			sbNodir.append(nodo);
		}
		return sbNodir.toString();
	}
	
	protected String getMultipleNodeSeparator() {
		return "";
	}
	
	public abstract void error(String msg);
	public abstract void error(String msg, Throwable t);
	public abstract Logger getInternalLogger();
	
	public abstract boolean isUseApiCertificatoApplicativoById() throws CoreException;
	public abstract boolean isUseApiCertificatoSoggettoById() throws CoreException;
	
	public abstract String getJmxResourceType() throws CoreException;
	public abstract String getJmxResourceNomeRisorsaConfigurazionePdD() throws CoreException;
	public abstract String getJmxResourceNomeRisorsaAccessoRegistroServizi() throws CoreException;
	
	public abstract String getJmxResourceNomeMetodoCheckConnettoreById() throws CoreException;
	public abstract String getJmxResourceNomeMetodoCheckCertificatoApplicativoById() throws CoreException;
	public abstract String getJmxResourceNomeMetodoCheckCertificatoModIApplicativoById() throws CoreException;
	public abstract String getJmxResourceNomeMetodoCheckCertificatoSoggettoById() throws CoreException;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsById() throws CoreException;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiModIErogazioneById() throws CoreException;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiModIFruizioneById() throws CoreException;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiMessageSecurityErogazioneById() throws CoreException;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiMessageSecurityFruizioneById() throws CoreException;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiJvm() throws CoreException;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsTokenPolicyValidazione() throws CoreException;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiValidazioneJwtTokenPolicyValidazione() throws CoreException;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiForwardToJwtTokenPolicyValidazione() throws CoreException;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsTokenPolicyNegoziazione() throws CoreException;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiSignedJwtTokenPolicyNegoziazione() throws CoreException;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsAttributeAuthority() throws CoreException;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiAttributeAuthorityJwtRichiesta() throws CoreException;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiAttributeAuthorityJwtRisposta() throws CoreException;
	
	private InvokerNodiRuntime invoker;
	private ConfigurazioneNodiRuntime config;
	private List<String> nodiRuntime;
	protected AbstractConfigChecker(InvokerNodiRuntime invoker, ConfigurazioneNodiRuntime config, List<String> nodiRuntime) {
		this.invoker = invoker;
		this.config = config;
		this.nodiRuntime = nodiRuntime;
	}
	
	public void checkApplicativo(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,
		    boolean ssl, boolean sicurezzaModi, boolean connettoreHttps, org.openspcoop2.core.config.ServizioApplicativo servizioApplicativo,
		    int sogliaWarningGiorni) throws CoreException {
		
		StringBuilder sbError = new StringBuilder();
		
		StringBuilder sbWarning = new StringBuilder();
		StringBuilder sbWarningModi = new StringBuilder();
		StringBuilder sbWarningConnettore = new StringBuilder();
		
		if(ssl) {
			checkCertificate(sbError, sbWarning, 
					sogliaWarningGiorni,
					servizioApplicativo);
		}
		
		if(sbError.length()<=0 &&
			sicurezzaModi) {
			checkCertificateModI(sbError, sbWarningModi, 
					sogliaWarningGiorni,
					servizioApplicativo);
		}
		
		if(sbError.length()<=0 &&
			connettoreHttps) {
			org.openspcoop2.core.config.Connettore connettore = null;
			if(servizioApplicativo.getInvocazioneServizio()!=null) {
				connettore = servizioApplicativo.getInvocazioneServizio().getConnettore();
			}
			if(connettore!=null) {
				checkCertificate(sbError, sbWarningConnettore, 
						sogliaWarningGiorni,
						connettore);	
			}
		}
		
		if(sbError.length()>0) {
			sbDetailsError.append(sbError.toString());
		}
		else {
			if(sbWarning.length()>0) {
				sbDetailsWarning.append(sbWarning.toString());
			}
			else if(sbWarningModi.length()>0) {
				sbDetailsWarning.append(sbWarningModi.toString());
			}
			else if(sbWarningConnettore.length()>0) {
				sbDetailsWarning.append(sbWarningConnettore.toString());
			}
		}
		
	}
	
	public void checkSoggetto(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,
		    boolean ssl, org.openspcoop2.core.registry.Soggetto soggetto, 
		    int sogliaWarningGiorni) throws CoreException {
		
		StringBuilder sbError = new StringBuilder();
		
		StringBuilder sbWarning = new StringBuilder();
		
		if(ssl) {
			checkCertificate(sbError, sbWarning, 
					sogliaWarningGiorni,
					soggetto);
		}
		
		if(sbError.length()>0) {
			sbDetailsError.append(sbError.toString());
		}
		else {
			if(sbWarning.length()>0) {
				sbDetailsWarning.append(sbWarning.toString());
			}
		}
		
	}
	
	public void checkErogazione(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,
			boolean connettoreSsl, org.openspcoop2.core.config.Connettore connettore,
			boolean sicurezzaModi, 
			boolean messageSecurity, 
			org.openspcoop2.core.registry.AccordoServizioParteSpecifica asps,
			int sogliaWarningGiorni) throws CoreException {
		List<org.openspcoop2.core.config.Connettore> connettori = null;
		if(connettore!=null) {
			connettori = new ArrayList<>();
			connettori.add(connettore);
		}
		checkErogazione(sbDetailsError, sbDetailsWarning,
				connettoreSsl, connettori,
				sicurezzaModi, 
				messageSecurity, 
				asps,
				sogliaWarningGiorni);
	}
	public void checkErogazione(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,
			boolean connettoreSsl, List<org.openspcoop2.core.config.Connettore> connettori,
			boolean sicurezzaModi, 
			boolean messageSecurity, 
			org.openspcoop2.core.registry.AccordoServizioParteSpecifica asps,
			int sogliaWarningGiorni) throws CoreException {
		
		StringBuilder sbError = new StringBuilder();
		
		StringBuilder sbWarning = new StringBuilder();
		StringBuilder sbWarningModi = new StringBuilder();
		StringBuilder sbWarningSicurezzaMessaggio = new StringBuilder();
		
		if(connettoreSsl && connettori!=null && !connettori.isEmpty()) {
			for (org.openspcoop2.core.config.Connettore connettore : connettori) {
				StringBuilder sbWarningConnettore = new StringBuilder();
				if(sbError.length()<=0) {
					checkCertificate(sbError, sbWarningConnettore, 
							sogliaWarningGiorni,
							connettore);	
				}
				if(sbWarning.length()<=0 && sbWarningConnettore.length()>0) {
					sbWarning.append(sbWarningConnettore.toString()); // tengo solo un warning alla volta, come per gli errori
				}
			}
		}
		
		if(sbError.length()<=0 &&
			sicurezzaModi) {
			checkCertificateModI(sbError, sbWarningModi, 
					sogliaWarningGiorni,
					asps);
		}
		
		if(sbError.length()<=0 &&
				messageSecurity) {
				checkCertificateMessageSecurity(sbError, sbWarningSicurezzaMessaggio, 
						sogliaWarningGiorni,
						asps);
			}
		
		if(sbError.length()>0) {
			sbDetailsError.append(sbError.toString());
		}
		else {
			if(sbWarning.length()>0) {
				sbDetailsWarning.append(sbWarning.toString());
			}
			else if(sbWarningModi.length()>0) {
				sbDetailsWarning.append(sbWarningModi.toString());
			}
			else if(sbWarningSicurezzaMessaggio.length()>0) {
				sbDetailsWarning.append(sbWarningSicurezzaMessaggio.toString());
			}
		}
		
	}
	
	public void checkFruizione(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,
			boolean connettoreSsl, org.openspcoop2.core.registry.Connettore connettore,
			boolean sicurezzaModi, 
			boolean messageSecurity,
			org.openspcoop2.core.registry.AccordoServizioParteSpecifica asps, org.openspcoop2.core.registry.Fruitore fruitore,
			int sogliaWarningGiorni) throws CoreException {
		List<org.openspcoop2.core.registry.Connettore> connettori = null;
		if(connettore!=null) {
			connettori = new ArrayList<>();
			connettori.add(connettore);
		}
		checkFruizione(sbDetailsError, sbDetailsWarning,
				connettoreSsl, connettori,
				sicurezzaModi, 
				messageSecurity,
				asps, fruitore,
				sogliaWarningGiorni);
	}
	public void checkFruizione(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,
			boolean connettoreSsl, List<org.openspcoop2.core.registry.Connettore> connettori,
			boolean sicurezzaModi, 
			boolean messageSecurity,
			org.openspcoop2.core.registry.AccordoServizioParteSpecifica asps, org.openspcoop2.core.registry.Fruitore fruitore,
			int sogliaWarningGiorni) throws CoreException {
		
		if(asps!=null) {
			// nop
		}
		
		StringBuilder sbError = new StringBuilder();
		
		StringBuilder sbWarning = new StringBuilder();
		StringBuilder sbWarningModi = new StringBuilder();
		StringBuilder sbWarningSicurezzaMessaggio = new StringBuilder();
		
		if(connettoreSsl && connettori!=null && !connettori.isEmpty()) {
			for (org.openspcoop2.core.registry.Connettore connettore : connettori) {
				StringBuilder sbWarningConnettore = new StringBuilder();
				if(sbError.length()<=0) {
					checkCertificate(sbError, sbWarningConnettore, 
							sogliaWarningGiorni,
							connettore);	
				}
				if(sbWarning.length()<=0 && sbWarningConnettore.length()>0) {
					sbWarning.append(sbWarningConnettore.toString()); // tengo solo un warning alla volta, come per gli errori
				}
			}
		}
		
		if(sbError.length()<=0 &&
			sicurezzaModi) {
			checkCertificateModI(sbError, sbWarningModi, 
					sogliaWarningGiorni,
					fruitore);
		}
		
		if(sbError.length()<=0 &&
				messageSecurity) {
			checkCertificateMessageSecurity(sbError, sbWarningSicurezzaMessaggio, 
					sogliaWarningGiorni,
					fruitore);
		}
		
		if(sbError.length()>0) {
			sbDetailsError.append(sbError.toString());
		}
		else {
			if(sbWarning.length()>0) {
				sbDetailsWarning.append(sbWarning.toString());
			}
			else if(sbWarningModi.length()>0) {
				sbDetailsWarning.append(sbWarningModi.toString());
			}
			else if(sbWarningSicurezzaMessaggio.length()>0) {
				sbDetailsWarning.append(sbWarningSicurezzaMessaggio.toString());
			}
		}
		
	}
	
	
	public void checkConfigurazioneJvm(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,
		    int sogliaWarningGiorni) throws CoreException {
		
		StringBuilder sbError = new StringBuilder();
		
		StringBuilder sbWarning = new StringBuilder();
		
		checkCertificate(sbError, sbWarning, 
					sogliaWarningGiorni,
					new org.openspcoop2.core.config.Configurazione());
		
		if(sbError.length()>0) {
			sbDetailsError.append(sbError.toString());
		}
		else {
			if(sbWarning.length()>0) {
				sbDetailsWarning.append(sbWarning.toString());
			}
		}
		
	}
	
	
	public void checkTokenPolicyValidazione(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,
			boolean httpsDynamicDiscovery, boolean httpsValidazioneJWT, boolean httpsIntrospection, boolean httpsUserInfo, 
			boolean validazioneJwt, boolean forwardToJwt,
			GenericProperties gp,
			int sogliaWarningGiorni) throws CoreException {
		
		StringBuilder sbError = new StringBuilder();
		
		StringBuilder sbWarningDynamicDiscovery = new StringBuilder();
		StringBuilder sbWarningValidazioneJwtHttps = new StringBuilder();
		StringBuilder sbWarningIntrospection = new StringBuilder();
		StringBuilder sbWarningUserInfo = new StringBuilder();
		StringBuilder sbWarningValidazioneJwt = new StringBuilder();
		StringBuilder sbWarningForwardToJwt = new StringBuilder();
		
		if(httpsDynamicDiscovery) {
			checkCertificateGenericProperties(sbError, sbWarningDynamicDiscovery, 
					sogliaWarningGiorni,
					getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsTokenPolicyValidazione(), 
					gp.getNome(), ConnettoreCheck.POLICY_TIPO_ENDPOINT_DYNAMIC_DISCOVERY);
		}
		if(sbError.length()<=0 &&
			httpsValidazioneJWT) {
			checkCertificateGenericProperties(sbError, sbWarningValidazioneJwtHttps, 
					sogliaWarningGiorni,
					getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsTokenPolicyValidazione(), 
					gp.getNome(), ConnettoreCheck.POLICY_TIPO_ENDPOINT_VALIDAZIONE_JWT);
		}
		if(sbError.length()<=0 &&
			httpsIntrospection) {
			checkCertificateGenericProperties(sbError, sbWarningIntrospection, 
					sogliaWarningGiorni,
					getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsTokenPolicyValidazione(), 
					gp.getNome(), ConnettoreCheck.POLICY_TIPO_ENDPOINT_INTROSPECTION);
		}
		if(sbError.length()<=0 &&
			httpsUserInfo) {
			checkCertificateGenericProperties(sbError, sbWarningUserInfo, 
					sogliaWarningGiorni,
					getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsTokenPolicyValidazione(), 
					gp.getNome(), ConnettoreCheck.POLICY_TIPO_ENDPOINT_USERINFO);
		}
		if(sbError.length()<=0 &&
			validazioneJwt) {
			checkCertificateGenericProperties(sbError, sbWarningValidazioneJwt, 
					sogliaWarningGiorni,
					getJmxResourceNomeMetodoCheckCertificatiValidazioneJwtTokenPolicyValidazione(),
					gp.getNome());
		}
		if(sbError.length()<=0 &&
			forwardToJwt) {
			checkCertificateGenericProperties(sbError, sbWarningForwardToJwt, 
					sogliaWarningGiorni,
					getJmxResourceNomeMetodoCheckCertificatiForwardToJwtTokenPolicyValidazione(),
					gp.getNome());
		}
		
		if(sbError.length()>0) {
			sbDetailsError.append(sbError.toString());
		}
		else {
			if(sbWarningDynamicDiscovery.length()>0) {
				sbDetailsWarning.append(sbWarningDynamicDiscovery.toString());
			}
			else if(sbWarningValidazioneJwtHttps.length()>0) {
				sbDetailsWarning.append(sbWarningValidazioneJwtHttps.toString());
			}
			else if(sbWarningIntrospection.length()>0) {
				sbDetailsWarning.append(sbWarningIntrospection.toString());
			}
			else if(sbWarningUserInfo.length()>0) {
				sbDetailsWarning.append(sbWarningUserInfo.toString());
			}
			else if(sbWarningValidazioneJwt.length()>0) {
				sbDetailsWarning.append(sbWarningValidazioneJwt.toString());
			}
			else if(sbWarningForwardToJwt.length()>0) {
				sbDetailsWarning.append(sbWarningForwardToJwt.toString());
			}
		}
		
	}
	
	
	public void checkTokenPolicyNegoziazione(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,
			boolean https, boolean signedJwt,
			GenericProperties gp,
			int sogliaWarningGiorni) throws CoreException {
		
		StringBuilder sbError = new StringBuilder();
		
		StringBuilder sbWarning = new StringBuilder();
		StringBuilder sbWarningSignedJwt = new StringBuilder();
		
		if(https) {
			checkCertificateGenericProperties(sbError, sbWarning, 
					sogliaWarningGiorni,
					getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsTokenPolicyNegoziazione(),
					gp.getNome());
		}
		if(sbError.length()<=0 &&
			signedJwt) {
			checkCertificateGenericProperties(sbError, sbWarningSignedJwt, 
					sogliaWarningGiorni,
					getJmxResourceNomeMetodoCheckCertificatiSignedJwtTokenPolicyNegoziazione(), 
					gp.getNome());
		}
		
		if(sbError.length()>0) {
			sbDetailsError.append(sbError.toString());
		}
		else {
			if(sbWarning.length()>0) {
				sbDetailsWarning.append(sbWarning.toString());
			}
			else if(sbWarningSignedJwt.length()>0) {
				sbDetailsWarning.append(sbWarningSignedJwt.toString());
			}
		}
		
	}
	
	
	public void checkAttributeAuthority(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,
			boolean https, boolean jwtRichiesta, boolean jwtRisposta,
			GenericProperties gp,
			int sogliaWarningGiorni) throws CoreException {
		
		StringBuilder sbError = new StringBuilder();
		
		StringBuilder sbWarning = new StringBuilder();
		StringBuilder sbWarningJwtRichiesta = new StringBuilder();
		StringBuilder sbWarningJwtRisposta = new StringBuilder();
		
		if(https) {
			checkCertificateGenericProperties(sbError, sbWarning, 
					sogliaWarningGiorni,
					getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsAttributeAuthority(),
					gp.getNome());
		}
		if(sbError.length()<=0 &&
			jwtRichiesta) {
			checkCertificateGenericProperties(sbError, sbWarningJwtRichiesta, 
					sogliaWarningGiorni,
					getJmxResourceNomeMetodoCheckCertificatiAttributeAuthorityJwtRichiesta(),
					gp.getNome());
		}
		if(sbError.length()<=0 &&
			jwtRisposta) {
			checkCertificateGenericProperties(sbError, sbWarningJwtRisposta, 
					sogliaWarningGiorni,
					getJmxResourceNomeMetodoCheckCertificatiAttributeAuthorityJwtRisposta(),
					gp.getNome());
		}
		
		if(sbError.length()>0) {
			sbDetailsError.append(sbError.toString());
		}
		else {
			if(sbWarning.length()>0) {
				sbDetailsWarning.append(sbWarning.toString());
			}
			else if(sbWarningJwtRichiesta.length()>0) {
				sbDetailsWarning.append(sbWarningJwtRichiesta.toString());
			}
			else if(sbWarningJwtRisposta.length()>0) {
				sbDetailsWarning.append(sbWarningJwtRisposta.toString());
			}
		}
		
	}
	
	
	private void checkCertificate(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,  
			int sogliaWarningGiorni,
			Object o) throws CoreException {
		
		String risorsa = null;
		String metodo = null;
		boolean useApi = false;
		boolean applicativo = false;
		boolean soggetto = false;
		boolean connettoreErogazione = false;
		boolean connettoreFruizione = false;
		long idObject = -1;
		boolean withId = true;
		if(o instanceof ServizioApplicativo) {
			applicativo = true;
		}
		else if(o instanceof Soggetto) {
			soggetto = true;
		}
		else if(o instanceof org.openspcoop2.core.config.Connettore) {
			connettoreErogazione = true;
		}
		else if(o instanceof org.openspcoop2.core.registry.Connettore) {
			connettoreFruizione = true;
		}
		else if(o instanceof org.openspcoop2.core.config.Configurazione) {
			risorsa = this.getJmxResourceNomeRisorsaConfigurazionePdD();
			metodo = this.getJmxResourceNomeMetodoCheckCertificatiJvm();
			withId = false;
		}
		else {
			throw new CoreException(getErrorMessageClasseNonGestita(o));
		}
		
		if(applicativo) {
			idObject = ((ServizioApplicativo)o).getId();
			useApi = this.isUseApiCertificatoApplicativoById();
			if(!useApi) {
				risorsa = this.getJmxResourceNomeRisorsaConfigurazionePdD();
				metodo = this.getJmxResourceNomeMetodoCheckCertificatoApplicativoById();
			}
		}
		else if(soggetto) {
			idObject = ((Soggetto)o).getId();
			useApi = this.isUseApiCertificatoSoggettoById();
			if(!useApi) {
				risorsa = this.getJmxResourceNomeRisorsaAccessoRegistroServizi();
				metodo = this.getJmxResourceNomeMetodoCheckCertificatoSoggettoById();
			}
		}
		else if(connettoreErogazione) {
			idObject = ((org.openspcoop2.core.config.Connettore)o).getId();
			risorsa = this.getJmxResourceNomeRisorsaConfigurazionePdD();
			metodo = this.getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsById();
		}
		else if(connettoreFruizione) {
			idObject = ((org.openspcoop2.core.registry.Connettore)o).getId();
			risorsa = this.getJmxResourceNomeRisorsaAccessoRegistroServizi();
			metodo = this.getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsById();
		}
		
		Map<String,List<String>> mapErrori = new HashMap<>();
		Map<String,List<String>> mapWarning = new HashMap<>();
		
		String error = StatoCheck.ERROR.toString();
		String warn = StatoCheck.WARN.toString();
		String ok = StatoCheck.OK.toString();
		
		List<String> nodiRuntimeEsaminati = new ArrayList<>();
		if(useApi) {
			nodiRuntimeEsaminati.add("SDK");
		}
		else {
			nodiRuntimeEsaminati.addAll(this.nodiRuntime);
		}
		
		for (String nomeNodoRuntime : nodiRuntimeEsaminati) {
			String stato = null;
			String descrizione = null;
			String errorDetail = null;
			String warnDetail = null;
			try{		
				if(useApi) {
					boolean addCertificateDetails = true;
					String separator = ": ";
					String newLine = "\n";
					try {
						CertificateCheck statoCheck = null;
						if(applicativo) {
							statoCheck = ConfigurazionePdDReader.checkCertificatoApplicativo(((ServizioApplicativo)o), sogliaWarningGiorni, 
									addCertificateDetails, separator, newLine,
									this.getInternalLogger());
						}
						else if(soggetto) {
							statoCheck = RegistroServiziReader.checkCertificatoSoggetto(((Soggetto)o), sogliaWarningGiorni, 
									addCertificateDetails, separator, newLine,
									this.getInternalLogger());
						}
						else {
							throw new CoreException("Incorrect invocation: (useApi:"+useApi+" applicativo:"+applicativo+" soggetto:"+soggetto+")");
						}
						stato = statoCheck.toString(newLine);
					}catch(Exception e){
						stato = JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA+e.getMessage();
					}
				}
				else {
					if(this.config.containsNode(nomeNodoRuntime)) {
						descrizione = this.config.getDescrizione(nomeNodoRuntime);
					}
					
					if(withId) {
						stato = this.invoker.invokeJMXMethod(nomeNodoRuntime, this.getJmxResourceType(),
								risorsa, 
								metodo,
								idObject, sogliaWarningGiorni);
					}
					else {
						stato = this.invoker.invokeJMXMethod(nomeNodoRuntime, this.getJmxResourceType(),
								risorsa, 
								metodo,
								sogliaWarningGiorni);
					}
				}
				
				if(stato!=null && stato.equals(error)){
					errorDetail = stato;
				}
				else if(stato!=null && stato.startsWith(error+"\n")){
					errorDetail = stato.substring((error+"\n").length());
				}
				else if(stato!=null && stato.equals(warn)){
					warnDetail = warn;
				}
				else if(stato!=null && stato.startsWith(warn+"\n")){
					warnDetail = stato.substring((warn+"\n").length());
				}
				else if(stato!=null && stato.startsWith(ok)){
					// nop
				}
				else {
					errorDetail = stato;
				}
			}catch(Exception e){
				this.error(getErrorMessage(risorsa,nomeNodoRuntime,e),e);
				stato = e.getMessage();
				errorDetail = stato;
			}
			
			if(errorDetail!=null) {
				AbstractConfigChecker.addErrore(mapErrori, errorDetail, 
						descrizione!=null ? descrizione : nomeNodoRuntime);
			}
			else if(warnDetail!=null) {
				AbstractConfigChecker.addErrore(mapWarning, warnDetail, 
						descrizione!=null ? descrizione : nomeNodoRuntime);
			}
		}
		
		if(!mapErrori.isEmpty()) {
			AbstractConfigChecker.printErrore(mapErrori, sbDetailsError, nodiRuntimeEsaminati.size(), getMultipleNodeSeparator());
		}
		else if(!mapWarning.isEmpty()) {
			AbstractConfigChecker.printErrore(mapWarning, sbDetailsWarning, nodiRuntimeEsaminati.size(), getMultipleNodeSeparator());
		}

	}
	
	private void checkCertificateModI(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning, 
			int sogliaWarningGiorni,
			Object o) throws CoreException {
		
		String risorsa = null;
		String metodo = null;
		boolean applicativo = false;
		boolean erogazione = false;
		boolean fruizione = false;
		long idObject = -1;
		if(o instanceof ServizioApplicativo) {
			applicativo = true;
		}
		else if(o instanceof org.openspcoop2.core.registry.AccordoServizioParteSpecifica) {
			erogazione = true;
		}
		else if(o instanceof org.openspcoop2.core.registry.Fruitore) {
			fruizione = true;
		}
		else {
			throw new CoreException(getErrorMessageClasseNonGestita(o));
		}
		
		if(applicativo) {
			idObject = ((ServizioApplicativo)o).getId();
			risorsa = this.getJmxResourceNomeRisorsaConfigurazionePdD();
			metodo = this.getJmxResourceNomeMetodoCheckCertificatoModIApplicativoById();
		}
		else if(erogazione) {
			idObject = ((org.openspcoop2.core.registry.AccordoServizioParteSpecifica)o).getId();
			risorsa = this.getJmxResourceNomeRisorsaAccessoRegistroServizi();
			metodo = this.getJmxResourceNomeMetodoCheckCertificatiModIErogazioneById();
		}
		else if(fruizione) {
			idObject = ((org.openspcoop2.core.registry.Fruitore)o).getId();
			risorsa = this.getJmxResourceNomeRisorsaAccessoRegistroServizi();
			metodo = this.getJmxResourceNomeMetodoCheckCertificatiModIFruizioneById();
		}
		
		Map<String,List<String>> mapErrori = new HashMap<>();
		Map<String,List<String>> mapWarning = new HashMap<>();
		
		String error = StatoCheck.ERROR.toString();
		String warn = StatoCheck.WARN.toString();
		String ok = StatoCheck.OK.toString();
		
		for (String nomeNodoRuntime : this.nodiRuntime) {
			String stato = null;
			String descrizione = null;
			String errorDetail = null;
			String warnDetail = null;
			try{		
				if(this.config.containsNode(nomeNodoRuntime)) {
					descrizione = this.config.getDescrizione(nomeNodoRuntime);
				}
				
				stato = this.invoker.invokeJMXMethod(nomeNodoRuntime, this.getJmxResourceType(),
						risorsa, 
						metodo,
						idObject, sogliaWarningGiorni);
				
				if(stato!=null && stato.equals(error)){
					errorDetail = stato;
				}
				else if(stato!=null && stato.startsWith(error+"\n")){
					errorDetail = stato.substring((error+"\n").length());
				}
				else if(stato!=null && stato.equals(warn)){
					warnDetail = warn;
				}
				else if(stato!=null && stato.startsWith(warn+"\n")){
					warnDetail = stato.substring((warn+"\n").length());
				}
				else if(stato!=null && stato.startsWith(ok)){
					// nop
				}
				else {
					errorDetail = stato;
				}
			}catch(Exception e){
				this.error(getErrorMessage(risorsa,nomeNodoRuntime,e),e);
				stato = e.getMessage();
				errorDetail = stato;
			}
			
			if(errorDetail!=null) {
				AbstractConfigChecker.addErrore(mapErrori, errorDetail, 
						descrizione!=null ? descrizione : nomeNodoRuntime);
			}
			else if(warnDetail!=null) {
				AbstractConfigChecker.addErrore(mapWarning, warnDetail, 
						descrizione!=null ? descrizione : nomeNodoRuntime);
			}
		}
		
		if(!mapErrori.isEmpty()) {
			AbstractConfigChecker.printErrore(mapErrori, sbDetailsError, this.nodiRuntime.size(), getMultipleNodeSeparator());
		}
		else if(!mapWarning.isEmpty()) {
			AbstractConfigChecker.printErrore(mapWarning, sbDetailsWarning, this.nodiRuntime.size(), getMultipleNodeSeparator());
		}

	}
	
	private void checkCertificateMessageSecurity(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning, 
			int sogliaWarningGiorni,
			Object o) throws CoreException {
		
		String risorsa = null;
		String metodo = null;
		boolean erogazione = false;
		boolean fruizione = false;
		long idObject = -1;
		if(o instanceof org.openspcoop2.core.registry.AccordoServizioParteSpecifica) {
			erogazione = true;
		}
		else if(o instanceof org.openspcoop2.core.registry.Fruitore) {
			fruizione = true;
		}
		else {
			throw new CoreException(getErrorMessageClasseNonGestita(o));
		}
		
		if(erogazione) {
			idObject = ((org.openspcoop2.core.registry.AccordoServizioParteSpecifica)o).getId();
			risorsa = this.getJmxResourceNomeRisorsaConfigurazionePdD();
			metodo = this.getJmxResourceNomeMetodoCheckCertificatiMessageSecurityErogazioneById();
		}
		else if(fruizione) {
			idObject = ((org.openspcoop2.core.registry.Fruitore)o).getId();
			risorsa = this.getJmxResourceNomeRisorsaConfigurazionePdD();
			metodo = this.getJmxResourceNomeMetodoCheckCertificatiMessageSecurityFruizioneById();
		}
		
		Map<String,List<String>> mapErrori = new HashMap<>();
		Map<String,List<String>> mapWarning = new HashMap<>();
		
		String error = StatoCheck.ERROR.toString();
		String warn = StatoCheck.WARN.toString();
		String ok = StatoCheck.OK.toString();
		
		for (String nomeNodoRuntime : this.nodiRuntime) {
			String stato = null;
			String descrizione = null;
			String errorDetail = null;
			String warnDetail = null;
			try{		
				if(this.config.containsNode(nomeNodoRuntime)) {
					descrizione = this.config.getDescrizione(nomeNodoRuntime);
				}
				
				stato = this.invoker.invokeJMXMethod(nomeNodoRuntime, this.getJmxResourceType(),
						risorsa, 
						metodo,
						idObject, sogliaWarningGiorni);
				
				if(stato!=null && stato.equals(error)){
					errorDetail = stato;
				}
				else if(stato!=null && stato.startsWith(error+"\n")){
					errorDetail = stato.substring((error+"\n").length());
				}
				else if(stato!=null && stato.equals(warn)){
					warnDetail = warn;
				}
				else if(stato!=null && stato.startsWith(warn+"\n")){
					warnDetail = stato.substring((warn+"\n").length());
				}
				else if(stato!=null && stato.startsWith(ok)){
					// nop
				}
				else {
					errorDetail = stato;
				}
			}catch(Exception e){
				this.error(getErrorMessage(risorsa,nomeNodoRuntime,e),e);
				stato = e.getMessage();
				errorDetail = stato;
			}
			
			if(errorDetail!=null) {
				AbstractConfigChecker.addErrore(mapErrori, errorDetail, 
						descrizione!=null ? descrizione : nomeNodoRuntime);
			}
			else if(warnDetail!=null) {
				AbstractConfigChecker.addErrore(mapWarning, warnDetail, 
						descrizione!=null ? descrizione : nomeNodoRuntime);
			}
		}
		
		if(!mapErrori.isEmpty()) {
			AbstractConfigChecker.printErrore(mapErrori, sbDetailsError, this.nodiRuntime.size(), getMultipleNodeSeparator());
		}
		else if(!mapWarning.isEmpty()) {
			AbstractConfigChecker.printErrore(mapWarning, sbDetailsWarning, this.nodiRuntime.size(), getMultipleNodeSeparator());
		}

	}
	
	private void checkCertificateGenericProperties(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning, 
			int sogliaWarningGiorni,
			String metodo, String nomePolicy) throws CoreException {
		checkCertificateGenericProperties(sbDetailsError, sbDetailsWarning, 
				sogliaWarningGiorni,
				metodo, nomePolicy, null); 
	}
	private void checkCertificateGenericProperties(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning, 
			int sogliaWarningGiorni,
			String metodo, String nomePolicy, String tipo) throws CoreException {
		
		String risorsa = this.getJmxResourceNomeRisorsaConfigurazionePdD();
				
		Map<String,List<String>> mapErrori = new HashMap<>();
		Map<String,List<String>> mapWarning = new HashMap<>();
		
		String error = StatoCheck.ERROR.toString();
		String warn = StatoCheck.WARN.toString();
		String ok = StatoCheck.OK.toString();
		
		for (String nomeNodoRuntime : this.nodiRuntime) {
			String stato = null;
			String descrizione = null;
			String errorDetail = null;
			String warnDetail = null;
			try{		
				if(this.config.containsNode(nomeNodoRuntime)) {
					descrizione = this.config.getDescrizione(nomeNodoRuntime);
				}
				
				if(tipo!=null) {
					stato = this.invoker.invokeJMXMethod(nomeNodoRuntime, this.getJmxResourceType(),
							risorsa, 
							metodo,
							nomePolicy, tipo, sogliaWarningGiorni);
				}
				else {
					stato = this.invoker.invokeJMXMethod(nomeNodoRuntime, this.getJmxResourceType(),
							risorsa, 
							metodo,
							nomePolicy, sogliaWarningGiorni);
				}
				
				if(stato!=null && stato.equals(error)){
					errorDetail = stato;
				}
				else if(stato!=null && stato.startsWith(error+"\n")){
					errorDetail = stato.substring((error+"\n").length());
				}
				else if(stato!=null && stato.equals(warn)){
					warnDetail = warn;
				}
				else if(stato!=null && stato.startsWith(warn+"\n")){
					warnDetail = stato.substring((warn+"\n").length());
				}
				else if(stato!=null && stato.startsWith(ok)){
					// nop
				}
				else {
					errorDetail = stato;
				}
			}catch(Exception e){
				this.error(getErrorMessage(risorsa,nomeNodoRuntime,e),e);
				stato = e.getMessage();
				errorDetail = stato;
			}
			
			if(errorDetail!=null) {
				AbstractConfigChecker.addErrore(mapErrori, errorDetail, 
						descrizione!=null ? descrizione : nomeNodoRuntime);
			}
			else if(warnDetail!=null) {
				AbstractConfigChecker.addErrore(mapWarning, warnDetail, 
						descrizione!=null ? descrizione : nomeNodoRuntime);
			}
		}
		
		if(!mapErrori.isEmpty()) {
			AbstractConfigChecker.printErrore(mapErrori, sbDetailsError, this.nodiRuntime.size(), getMultipleNodeSeparator());
		}
		else if(!mapWarning.isEmpty()) {
			AbstractConfigChecker.printErrore(mapWarning, sbDetailsWarning, this.nodiRuntime.size(), getMultipleNodeSeparator());
		}

	}
}
