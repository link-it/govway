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

package org.openspcoop2.pdd.core.jmx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.constants.StatoCheck;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntime;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.config.InvokerNodiRuntime;
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
			List<String> l = new ArrayList<String>();
			l.add(nomeNodoRuntime);
			mapErrori.put(errore, l);
		}
	}
	
	public static void printErrore(Map<String,List<String>> mapErrori, StringBuilder sbDetails) {
		if(!mapErrori.isEmpty()) {
			if(mapErrori.size()==1) {
				if(sbDetails.length()>0) {
					sbDetails.append("\n");
				}
				sbDetails.append(mapErrori.keySet().iterator().next());
			}
			else {
				boolean first = true;
				for (String errore : mapErrori.keySet()) {
					List<String> nodiRuntimeErrore = mapErrori.get(errore);
					StringBuilder sbNodir = new StringBuilder();
					for (String nodo : nodiRuntimeErrore) {
						if(sbNodir.length()>0) {
							sbNodir.append(", ");
						}
						sbNodir.append(nodo);
					}
					
					if(sbDetails.length()>0) {
						sbDetails.append("\n");
					}
					else {
						if(first) {
							first = false;
						}
						else {
							sbDetails.append("\n");
						}
					}
					sbDetails.append("(").append(sbNodir.toString()).append(") ");
					sbDetails.append(errore);
				}
			}
		}
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
	
	private InvokerNodiRuntime invoker;
	private ConfigurazioneNodiRuntime config;
	private List<String> nodiRuntime;
	public AbstractConfigChecker(InvokerNodiRuntime invoker, ConfigurazioneNodiRuntime config, List<String> nodiRuntime) {
		this.invoker = invoker;
		this.config = config;
		this.nodiRuntime = nodiRuntime;
	}
	
	public void checkApplicativo(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,
		    boolean ssl, boolean sicurezzaModi, org.openspcoop2.core.config.ServizioApplicativo servizioApplicativo,
		    int sogliaWarningGiorni) throws Exception {
		
		StringBuilder _sbError = new StringBuilder();
		
		StringBuilder _sbWarning = new StringBuilder();
		StringBuilder _sbWarningModi = new StringBuilder();
		
		if(ssl) {
			checkCertificate(_sbError, _sbWarning, 
					sogliaWarningGiorni,
					servizioApplicativo);
		}
		if(_sbError.length()<=0) {
			if(sicurezzaModi) {
				checkCertificateModI(_sbError, _sbWarning, 
						sogliaWarningGiorni,
						servizioApplicativo);
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
	
	public void checkSoggetto(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,
		    boolean ssl, org.openspcoop2.core.registry.Soggetto soggetto, 
		    int sogliaWarningGiorni) throws Exception {
		
		StringBuilder _sbError = new StringBuilder();
		
		StringBuilder _sbWarning = new StringBuilder();
		StringBuilder _sbWarningModi = new StringBuilder();
		
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
			else if(_sbWarningModi.length()>0) {
				sbDetailsWarning.append(_sbWarningModi.toString());
			}
		}
		
	}
	
	public void checkErogazione(StringBuilder sbDetailsError, StringBuilder sbDetailsWarning,
			boolean connettoreSsl, org.openspcoop2.core.config.Connettore connettore,
			boolean sicurezzaModi, org.openspcoop2.core.registry.AccordoServizioParteSpecifica asps,
			int sogliaWarningGiorni) throws Exception {
		
		StringBuilder _sbError = new StringBuilder();
		
		StringBuilder _sbWarning = new StringBuilder();
		StringBuilder _sbWarningModi = new StringBuilder();
		
		if(connettoreSsl) {
			checkCertificate(_sbError, _sbWarning, 
					sogliaWarningGiorni,
					connettore);
		}
		if(_sbError.length()<=0) {
			if(sicurezzaModi) {
				checkCertificateModI(_sbError, _sbWarning, 
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
		
		StringBuilder _sbError = new StringBuilder();
		
		StringBuilder _sbWarning = new StringBuilder();
		StringBuilder _sbWarningModi = new StringBuilder();
		
		if(connettoreSsl) {
			checkCertificate(_sbError, _sbWarning, 
					sogliaWarningGiorni,
					connettore);
		}
		if(_sbError.length()<=0) {
			if(sicurezzaModi) {
				checkCertificateModI(_sbError, _sbWarning, 
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
		StringBuilder _sbWarningModi = new StringBuilder();
		
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
			else if(_sbWarningModi.length()>0) {
				sbDetailsWarning.append(_sbWarningModi.toString());
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
		
		Map<String,List<String>> mapErrori = new HashMap<String,List<String>>();
		Map<String,List<String>> mapWarning = new HashMap<String,List<String>>();
		
		String error = StatoCheck.ERROR.toString();
		String warn = StatoCheck.WARN.toString();
		String ok = StatoCheck.OK.toString();
		
		List<String> nodiRuntime = new ArrayList<String>();
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
			AbstractConfigChecker.printErrore(mapErrori, sbDetailsError);
		}
		else if(!mapWarning.isEmpty()) {
			AbstractConfigChecker.printErrore(mapWarning, sbDetailsWarning);
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
		
		Map<String,List<String>> mapErrori = new HashMap<String,List<String>>();
		Map<String,List<String>> mapWarning = new HashMap<String,List<String>>();
		
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
			AbstractConfigChecker.printErrore(mapErrori, sbDetailsError);
		}
		else if(!mapWarning.isEmpty()) {
			AbstractConfigChecker.printErrore(mapWarning, sbDetailsWarning);
		}

	}
	
}