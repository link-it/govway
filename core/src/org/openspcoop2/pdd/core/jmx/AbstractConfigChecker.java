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

package org.openspcoop2.pdd.core.jmx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public abstract boolean isUseApiCertificatoApplicativoById() throws Exception;
	public abstract boolean isUseApiCertificatoSoggettoById() throws Exception;
	
	public abstract String getJmxResourceType() throws Exception;
	public abstract String getJmxResourceNomeRisorsaConfigurazionePdD() throws Exception;
	public abstract String getJmxResourceNomeRisorsaAccessoRegistroServizi() throws Exception;
	
	public abstract String getJmxResourceNomeMetodoCheckConnettoreById() throws Exception;
	public abstract String getJmxResourceNomeMetodoCheckCertificatoApplicativoById() throws Exception;
	public abstract String getJmxResourceNomeMetodoCheckCertificatoModIApplicativoById() throws Exception;
	public abstract String getJmxResourceNomeMetodoCheckCertificatoSoggettoById() throws Exception;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsById() throws Exception;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiModIErogazioneById() throws Exception;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiModIFruizioneById() throws Exception;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiJvm() throws Exception;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsTokenPolicyValidazione() throws Exception;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiValidazioneJwtTokenPolicyValidazione() throws Exception;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiForwardToJwtTokenPolicyValidazione() throws Exception;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsTokenPolicyNegoziazione() throws Exception;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiSignedJwtTokenPolicyNegoziazione() throws Exception;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsAttributeAuthority() throws Exception;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiAttributeAuthorityJwtRichiesta() throws Exception;
	public abstract String getJmxResourceNomeMetodoCheckCertificatiAttributeAuthorityJwtRisposta() throws Exception;
	
	private InvokerNodiRuntime invoker;
	private ConfigurazioneNodiRuntime config;
	private List<String> nodiRuntime;
	public AbstractConfigChecker(InvokerNodiRuntime invoker, ConfigurazioneNodiRuntime config, List<String> nodiRuntime) {
		this.invoker = invoker;
		this.config = config;
		this.nodiRuntime = nodiRuntime;
	}
	
	public void checkApplicativo(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,
		    boolean ssl, boolean sicurezzaModi, boolean connettoreHttps, org.openspcoop2.core.config.ServizioApplicativo servizioApplicativo,
		    int sogliaWarningGiorni) throws Exception {
		
		StringBuilder _sbError = new StringBuilder();
		
		StringBuilder _sbWarning = new StringBuilder();
		StringBuilder _sbWarningModi = new StringBuilder();
		StringBuilder _sbWarningConnettore = new StringBuilder();
		
		if(ssl) {
			checkCertificate(_sbError, _sbWarning, 
					sogliaWarningGiorni,
					servizioApplicativo);
		}
		if(_sbError.length()<=0) {
			if(sicurezzaModi) {
				checkCertificateModI(_sbError, _sbWarningModi, 
						sogliaWarningGiorni,
						servizioApplicativo);
			}
		}
		if(_sbError.length()<=0) {
			if(connettoreHttps) {
				org.openspcoop2.core.config.Connettore connettore = null;
				if(servizioApplicativo.getInvocazioneServizio()!=null) {
					connettore = servizioApplicativo.getInvocazioneServizio().getConnettore();
				}
				if(connettore!=null) {
					checkCertificate(_sbError, _sbWarningConnettore, 
							sogliaWarningGiorni,
							connettore);	
				}
			}
		}
		
		if(_sbError.length()>0) {
			sbDetailsError.append(_sbError.toString());
		}
		else {
			if(_sbWarning.length()>0) {
				sbDetailsWarning.append(_sbWarning.toString());
			}
			else if(_sbWarningModi.length()>0) {
				sbDetailsWarning.append(_sbWarningModi.toString());
			}
			else if(_sbWarningConnettore.length()>0) {
				sbDetailsWarning.append(_sbWarningConnettore.toString());
			}
		}
		
	}
	
	public void checkSoggetto(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,
		    boolean ssl, org.openspcoop2.core.registry.Soggetto soggetto, 
		    int sogliaWarningGiorni) throws Exception {
		
		StringBuilder _sbError = new StringBuilder();
		
		StringBuilder _sbWarning = new StringBuilder();
		
		if(ssl) {
			checkCertificate(_sbError, _sbWarning, 
					sogliaWarningGiorni,
					soggetto);
		}
		
		if(_sbError.length()>0) {
			sbDetailsError.append(_sbError.toString());
		}
		else {
			if(_sbWarning.length()>0) {
				sbDetailsWarning.append(_sbWarning.toString());
			}
		}
		
	}
	
	public void checkErogazione(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,
			boolean connettoreSsl, org.openspcoop2.core.config.Connettore connettore,
			boolean sicurezzaModi, org.openspcoop2.core.registry.AccordoServizioParteSpecifica asps,
			int sogliaWarningGiorni) throws Exception {
		List<org.openspcoop2.core.config.Connettore> connettori = null;
		if(connettore!=null) {
			connettori = new ArrayList<>();
			connettori.add(connettore);
		}
		checkErogazione(sbDetailsError, sbDetailsWarning,
				connettoreSsl, connettori,
				sicurezzaModi, asps,
				sogliaWarningGiorni);
	}
	public void checkErogazione(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,
			boolean connettoreSsl, List<org.openspcoop2.core.config.Connettore> connettori,
			boolean sicurezzaModi, org.openspcoop2.core.registry.AccordoServizioParteSpecifica asps,
			int sogliaWarningGiorni) throws Exception {
		
		StringBuilder _sbError = new StringBuilder();
		
		StringBuilder _sbWarning = new StringBuilder();
		StringBuilder _sbWarningModi = new StringBuilder();
		
		if(connettoreSsl && connettori!=null && !connettori.isEmpty()) {
			for (org.openspcoop2.core.config.Connettore connettore : connettori) {
				StringBuilder _sbWarning_connettore = new StringBuilder();
				if(_sbError.length()<=0) {
					checkCertificate(_sbError, _sbWarning_connettore, 
							sogliaWarningGiorni,
							connettore);	
				}
				if(_sbWarning.length()<=0 && _sbWarning_connettore.length()>0) {
					_sbWarning.append(_sbWarning_connettore.toString()); // tengo solo un warning alla volta, come per gli errori
				}
			}
		}
		if(_sbError.length()<=0) {
			if(sicurezzaModi) {
				checkCertificateModI(_sbError, _sbWarningModi, 
						sogliaWarningGiorni,
						asps);
			}
		}
		
		if(_sbError.length()>0) {
			sbDetailsError.append(_sbError.toString());
		}
		else {
			if(_sbWarning.length()>0) {
				sbDetailsWarning.append(_sbWarning.toString());
			}
			else if(_sbWarningModi.length()>0) {
				sbDetailsWarning.append(_sbWarningModi.toString());
			}
		}
		
	}
	
	public void checkFruizione(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,
			boolean connettoreSsl, org.openspcoop2.core.registry.Connettore connettore,
			boolean sicurezzaModi, org.openspcoop2.core.registry.AccordoServizioParteSpecifica asps, org.openspcoop2.core.registry.Fruitore fruitore,
			int sogliaWarningGiorni) throws Exception {
		List<org.openspcoop2.core.registry.Connettore> connettori = null;
		if(connettore!=null) {
			connettori = new ArrayList<>();
			connettori.add(connettore);
		}
		checkFruizione(sbDetailsError, sbDetailsWarning,
				connettoreSsl, connettori,
				sicurezzaModi, asps, fruitore,
				sogliaWarningGiorni);
	}
	public void checkFruizione(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,
			boolean connettoreSsl, List<org.openspcoop2.core.registry.Connettore> connettori,
			boolean sicurezzaModi, org.openspcoop2.core.registry.AccordoServizioParteSpecifica asps, org.openspcoop2.core.registry.Fruitore fruitore,
			int sogliaWarningGiorni) throws Exception {
		
		StringBuilder _sbError = new StringBuilder();
		
		StringBuilder _sbWarning = new StringBuilder();
		StringBuilder _sbWarningModi = new StringBuilder();
		
		if(connettoreSsl && connettori!=null && !connettori.isEmpty()) {
			for (org.openspcoop2.core.registry.Connettore connettore : connettori) {
				StringBuilder _sbWarning_connettore = new StringBuilder();
				if(_sbError.length()<=0) {
					checkCertificate(_sbError, _sbWarning_connettore, 
							sogliaWarningGiorni,
							connettore);	
				}
				if(_sbWarning.length()<=0 && _sbWarning_connettore.length()>0) {
					_sbWarning.append(_sbWarning_connettore.toString()); // tengo solo un warning alla volta, come per gli errori
				}
			}
		}
		if(_sbError.length()<=0) {
			if(sicurezzaModi) {
				checkCertificateModI(_sbError, _sbWarningModi, 
						sogliaWarningGiorni,
						fruitore);
			}
		}
		
		if(_sbError.length()>0) {
			sbDetailsError.append(_sbError.toString());
		}
		else {
			if(_sbWarning.length()>0) {
				sbDetailsWarning.append(_sbWarning.toString());
			}
			else if(_sbWarningModi.length()>0) {
				sbDetailsWarning.append(_sbWarningModi.toString());
			}
		}
		
	}
	
	
	public void checkConfigurazioneJvm(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,
		    int sogliaWarningGiorni) throws Exception {
		
		StringBuilder _sbError = new StringBuilder();
		
		StringBuilder _sbWarning = new StringBuilder();
		
		checkCertificate(_sbError, _sbWarning, 
					sogliaWarningGiorni,
					new org.openspcoop2.core.config.Configurazione());
		
		if(_sbError.length()>0) {
			sbDetailsError.append(_sbError.toString());
		}
		else {
			if(_sbWarning.length()>0) {
				sbDetailsWarning.append(_sbWarning.toString());
			}
		}
		
	}
	
	
	public void checkTokenPolicyValidazione(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,
			boolean httpsIntrospection, boolean httpsUserInfo, boolean validazioneJwt, boolean forwardToJwt,
			GenericProperties gp,
			int sogliaWarningGiorni) throws Exception {
		
		StringBuilder _sbError = new StringBuilder();
		
		StringBuilder _sbWarningIntrospection = new StringBuilder();
		StringBuilder _sbWarningUserInfo = new StringBuilder();
		StringBuilder _sbWarningValidazioneJwt = new StringBuilder();
		StringBuilder _sbWarningForwardToJwt = new StringBuilder();
		
		if(httpsIntrospection) {
			checkCertificateGenericProperties(_sbError, _sbWarningIntrospection, 
					sogliaWarningGiorni,
					getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsTokenPolicyValidazione(), 
					gp.getNome(), ConnettoreCheck.POLICY_TIPO_ENDPOINT_INTROSPECTION);
		}
		if(_sbError.length()<=0) {
			if(httpsUserInfo) {
				checkCertificateGenericProperties(_sbError, _sbWarningUserInfo, 
						sogliaWarningGiorni,
						getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsTokenPolicyValidazione(), 
						gp.getNome(), ConnettoreCheck.POLICY_TIPO_ENDPOINT_USERINFO);
			}
		}
		if(_sbError.length()<=0) {
			if(validazioneJwt) {
				checkCertificateGenericProperties(_sbError, _sbWarningValidazioneJwt, 
						sogliaWarningGiorni,
						getJmxResourceNomeMetodoCheckCertificatiValidazioneJwtTokenPolicyValidazione(),
						gp.getNome());
			}
		}
		if(_sbError.length()<=0) {
			if(forwardToJwt) {
				checkCertificateGenericProperties(_sbError, _sbWarningForwardToJwt, 
						sogliaWarningGiorni,
						getJmxResourceNomeMetodoCheckCertificatiForwardToJwtTokenPolicyValidazione(),
						gp.getNome());
			}
		}
		
		if(_sbError.length()>0) {
			sbDetailsError.append(_sbError.toString());
		}
		else {
			if(_sbWarningIntrospection.length()>0) {
				sbDetailsWarning.append(_sbWarningIntrospection.toString());
			}
			else if(_sbWarningUserInfo.length()>0) {
				sbDetailsWarning.append(_sbWarningUserInfo.toString());
			}
			else if(_sbWarningValidazioneJwt.length()>0) {
				sbDetailsWarning.append(_sbWarningValidazioneJwt.toString());
			}
			else if(_sbWarningForwardToJwt.length()>0) {
				sbDetailsWarning.append(_sbWarningForwardToJwt.toString());
			}
		}
		
	}
	
	
	public void checkTokenPolicyNegoziazione(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,
			boolean https, boolean signedJwt,
			GenericProperties gp,
			int sogliaWarningGiorni) throws Exception {
		
		StringBuilder _sbError = new StringBuilder();
		
		StringBuilder _sbWarning = new StringBuilder();
		StringBuilder _sbWarningSignedJwt = new StringBuilder();
		
		if(https) {
			checkCertificateGenericProperties(_sbError, _sbWarning, 
					sogliaWarningGiorni,
					getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsTokenPolicyNegoziazione(),
					gp.getNome());
		}
		if(_sbError.length()<=0) {
			if(signedJwt) {
				checkCertificateGenericProperties(_sbError, _sbWarningSignedJwt, 
						sogliaWarningGiorni,
						getJmxResourceNomeMetodoCheckCertificatiSignedJwtTokenPolicyNegoziazione(), 
						gp.getNome());
			}
		}
		
		if(_sbError.length()>0) {
			sbDetailsError.append(_sbError.toString());
		}
		else {
			if(_sbWarning.length()>0) {
				sbDetailsWarning.append(_sbWarning.toString());
			}
			else if(_sbWarningSignedJwt.length()>0) {
				sbDetailsWarning.append(_sbWarningSignedJwt.toString());
			}
		}
		
	}
	
	
	public void checkAttributeAuthority(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,
			boolean https, boolean jwtRichiesta, boolean jwtRisposta,
			GenericProperties gp,
			int sogliaWarningGiorni) throws Exception {
		
		StringBuilder _sbError = new StringBuilder();
		
		StringBuilder _sbWarning = new StringBuilder();
		StringBuilder _sbWarningJwtRichiesta = new StringBuilder();
		StringBuilder _sbWarningJwtRisposta = new StringBuilder();
		
		if(https) {
			checkCertificateGenericProperties(_sbError, _sbWarning, 
					sogliaWarningGiorni,
					getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsAttributeAuthority(),
					gp.getNome());
		}
		if(_sbError.length()<=0) {
			if(jwtRichiesta) {
				checkCertificateGenericProperties(_sbError, _sbWarningJwtRichiesta, 
						sogliaWarningGiorni,
						getJmxResourceNomeMetodoCheckCertificatiAttributeAuthorityJwtRichiesta(),
						gp.getNome());
			}
		}
		if(_sbError.length()<=0) {
			if(jwtRisposta) {
				checkCertificateGenericProperties(_sbError, _sbWarningJwtRisposta, 
						sogliaWarningGiorni,
						getJmxResourceNomeMetodoCheckCertificatiAttributeAuthorityJwtRisposta(),
						gp.getNome());
			}
		}
		
		if(_sbError.length()>0) {
			sbDetailsError.append(_sbError.toString());
		}
		else {
			if(_sbWarning.length()>0) {
				sbDetailsWarning.append(_sbWarning.toString());
			}
			else if(_sbWarningJwtRichiesta.length()>0) {
				sbDetailsWarning.append(_sbWarningJwtRichiesta.toString());
			}
			else if(_sbWarningJwtRisposta.length()>0) {
				sbDetailsWarning.append(_sbWarningJwtRisposta.toString());
			}
		}
		
	}
	
	
	private void checkCertificate(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,  
			int sogliaWarningGiorni,
			Object o) throws Exception {
		
		String risorsa = null;
		String metodo = null;
		boolean useApi = false;
		boolean applicativo = false;
		boolean soggetto = false;
		@SuppressWarnings("unused")
		boolean connettoreErogazione = false;
		@SuppressWarnings("unused")
		boolean connettoreFruizione = false;
		long idObject = -1;
		boolean withId = true;
		if(o instanceof ServizioApplicativo) {
			applicativo = true;
			idObject = ((ServizioApplicativo)o).getId();
			useApi = this.isUseApiCertificatoApplicativoById();
			if(!useApi) {
				risorsa = this.getJmxResourceNomeRisorsaConfigurazionePdD();
				metodo = this.getJmxResourceNomeMetodoCheckCertificatoApplicativoById();
			}
		}
		else if(o instanceof Soggetto) {
			soggetto = true;
			idObject = ((Soggetto)o).getId();
			useApi = this.isUseApiCertificatoSoggettoById();
			if(!useApi) {
				risorsa = this.getJmxResourceNomeRisorsaAccessoRegistroServizi();
				metodo = this.getJmxResourceNomeMetodoCheckCertificatoSoggettoById();
			}
		}
		else if(o instanceof org.openspcoop2.core.config.Connettore) {
			connettoreErogazione = true;
			idObject = ((org.openspcoop2.core.config.Connettore)o).getId();
			risorsa = this.getJmxResourceNomeRisorsaConfigurazionePdD();
			metodo = this.getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsById();
		}
		else if(o instanceof org.openspcoop2.core.registry.Connettore) {
			connettoreFruizione = true;
			idObject = ((org.openspcoop2.core.registry.Connettore)o).getId();
			risorsa = this.getJmxResourceNomeRisorsaAccessoRegistroServizi();
			metodo = this.getJmxResourceNomeMetodoCheckCertificatiConnettoreHttpsById();
		}
		else if(o instanceof org.openspcoop2.core.config.Configurazione) {
			risorsa = this.getJmxResourceNomeRisorsaConfigurazionePdD();
			metodo = this.getJmxResourceNomeMetodoCheckCertificatiJvm();
			withId = false;
		}
		else {
			throw new Exception("Classe '"+o.getClass().getName()+"' non gestita");
		}
		
		Map<String,List<String>> mapErrori = new HashMap<>();
		Map<String,List<String>> mapWarning = new HashMap<>();
		
		String error = StatoCheck.ERROR.toString();
		String warn = StatoCheck.WARN.toString();
		String ok = StatoCheck.OK.toString();
		
		List<String> nodiRuntime = new ArrayList<>();
		if(useApi) {
			nodiRuntime.add("SDK");
		}
		else {
			nodiRuntime.addAll(this.nodiRuntime);
		}
		
		for (String nomeNodoRuntime : nodiRuntime) {
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
							throw new Exception("Incorrect invocation: (useApi:"+useApi+" applicativo:"+applicativo+" soggetto:"+soggetto+")");
						}
						stato = statoCheck.toString(newLine);
					}catch(Throwable e){
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
				this.error("Errore durante la verifica dei certificati (jmxResource '"+risorsa+"') (node:"+nomeNodoRuntime+"): "+e.getMessage(),e);
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
			AbstractConfigChecker.printErrore(mapErrori, sbDetailsError, nodiRuntime.size(), getMultipleNodeSeparator());
		}
		else if(!mapWarning.isEmpty()) {
			AbstractConfigChecker.printErrore(mapWarning, sbDetailsWarning, nodiRuntime.size(), getMultipleNodeSeparator());
		}

	}
	
	private void checkCertificateModI(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning, 
			int sogliaWarningGiorni,
			Object o) throws Exception {
		
		String risorsa = null;
		String metodo = null;
		@SuppressWarnings("unused")
		boolean applicativo = false;
		@SuppressWarnings("unused")
		boolean erogazione = false;
		@SuppressWarnings("unused")
		boolean fruizione = false;
		long idObject = -1;
		if(o instanceof ServizioApplicativo) {
			applicativo = true;
			idObject = ((ServizioApplicativo)o).getId();
			risorsa = this.getJmxResourceNomeRisorsaConfigurazionePdD();
			metodo = this.getJmxResourceNomeMetodoCheckCertificatoModIApplicativoById();
		}
		else if(o instanceof org.openspcoop2.core.registry.AccordoServizioParteSpecifica) {
			erogazione = true;
			idObject = ((org.openspcoop2.core.registry.AccordoServizioParteSpecifica)o).getId();
			risorsa = this.getJmxResourceNomeRisorsaAccessoRegistroServizi();
			metodo = this.getJmxResourceNomeMetodoCheckCertificatiModIErogazioneById();
		}
		else if(o instanceof org.openspcoop2.core.registry.Fruitore) {
			fruizione = true;
			idObject = ((org.openspcoop2.core.registry.Fruitore)o).getId();
			risorsa = this.getJmxResourceNomeRisorsaAccessoRegistroServizi();
			metodo = this.getJmxResourceNomeMetodoCheckCertificatiModIFruizioneById();
		}
		else {
			throw new Exception("Classe '"+o.getClass().getName()+"' non gestita");
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
				this.error("Errore durante la verifica dei certificati (jmxResource '"+risorsa+"') (node:"+nomeNodoRuntime+"): "+e.getMessage(),e);
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
			String metodo, String nomePolicy) throws Exception {
		checkCertificateGenericProperties(sbDetailsError, sbDetailsWarning, 
				sogliaWarningGiorni,
				metodo, nomePolicy, null); 
	}
	private void checkCertificateGenericProperties(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning, 
			int sogliaWarningGiorni,
			String metodo, String nomePolicy, String tipo) throws Exception {
		
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
				this.error("Errore durante la verifica dei certificati (jmxResource '"+risorsa+"') (node:"+nomeNodoRuntime+"): "+e.getMessage(),e);
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
