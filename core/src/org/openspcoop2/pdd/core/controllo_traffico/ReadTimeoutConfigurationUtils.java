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

package org.openspcoop2.pdd.core.controllo_traffico;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.id.IDException;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.core.connettori.ConnettoreUtils;
import org.openspcoop2.pdd.core.controllo_traffico.policy.PolicyDati;
import org.openspcoop2.pdd.core.controllo_traffico.policy.PolicyVerifier;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**     
 * ReadTimeoutConfigurationUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ReadTimeoutConfigurationUtils {
	
	private ReadTimeoutConfigurationUtils() {}

	public static final String CONFIG_GLOBALE_PROPERTY = "Configurazione globale: ";
	public static final String CONFIG_TIMEOUT = "Timeout (ms): ";
	public static final String CONFIG_PROFILO = "Profilo: ";
	public static final String CONFIG_TIPOLOGIA = "Tipologia: ";
	public static final String CONFIG_TIPOLOGIA_VALUE_DELEGATA = "fruizione";
	public static final String CONFIG_TIPOLOGIA_VALUE_APPLICATIVA = "erogazione";
	public static final String CONFIG_FRUITORE = "Fruitore: ";
	public static final String CONFIG_EROGATORE = "Erogatore: ";
	public static final String CONFIG_SERVIZIO = "Servizio: ";
	public static final String CONFIG_GRUPPO = "Gruppo: ";
	public static final String CONFIG_CONNETTORE = "Connettore: ";
	public static final String CONFIG_POLICY_NEGOZIAZIONE = "Token Policy Negoziazione: ";
	public static final String CONFIG_POLICY_VALIDAZIONE_DYNAMIC_DISCOVERY = "Token Policy Dynamic Discovery: ";
	public static final String CONFIG_POLICY_VALIDAZIONE_JWT = "Token Policy Validazione JWT: ";
	public static final String CONFIG_POLICY_VALIDAZIONE_INTROSPECTION = "Token Policy Introspection: ";
	public static final String CONFIG_POLICY_VALIDAZIONE_USER_INFO = "Token Policy UserInfo: ";
	public static final String CONFIG_ATTRIBUTE_AUTHORITY = "Attribute Authority: ";
	public static final String CONFIG_ATTRIBUTE_AUTHORITY_VALIDAZIONE_RISPOSTA_JWT = "Attribute Authority - Risposta JWT: ";
	public static final String CONFIG_NOME_PORTA_INBOUND_PROPERTY = "Porta Inbound: ";
	public static final String CONFIG_NOME_PORTA_OUTBOUND_PROPERTY = "Porta Outbound: ";
	public static final String CONFIG_NOME_PORTA_PROPERTY = "Porta: ";

	public static final String ID_POLICY_NEGOZIAZIONE = "tokenRetrieve:";
	public static final String ID_POLICY_VALIDAZIONE_DYNAMIC_DISCOVERY = "tokenDynamicDiscovery:";
	public static final String ID_POLICY_VALIDAZIONE_JWT = "tokenJwtValidation:";
	public static final String ID_POLICY_VALIDAZIONE_INTROSPECTION = "tokenIntrospection:";
	public static final String ID_POLICY_VALIDAZIONE_USER_INFO = "tokenUserInfo:";
	public static final String ID_ATTRIBUTE_AUTHORITY = "attributeAuthority:";
	public static final String ID_ATTRIBUTE_AUTHORITY_VALIDAZIONE_RISPOSTA_JWT = "attributeAuthorityResponseJwtValidation:";
	
	public static PolicyDati convertToPolicyDati(String configurazione) throws IDException {
		PolicyDati dati = new PolicyDati();
		String [] tmp = configurazione.split("\n");
		if(tmp!=null && tmp.length>0) {
			dati.setProfilo(parse(tmp, CONFIG_PROFILO));
			dati.setRuoloPorta(parseRuoloPolicy(tmp, CONFIG_TIPOLOGIA));
			dati.setIdFruitore(parseIdSoggetto(tmp, CONFIG_FRUITORE));
			dati.setIdServizio(parseIDServizio(tmp));
			IDSoggetto erogatore = parseIdSoggetto(tmp, CONFIG_EROGATORE);
			if(erogatore!=null) {
				if(dati.getIdServizio()!=null) {
					dati.setIdServizio(IDServizioFactory.getInstance().getIDServizioFromValuesWithoutCheck(
							dati.getIdServizio().getTipo(), 
							dati.getIdServizio().getNome(), 
							erogatore.getTipo(), 
							erogatore.getNome(), 
							dati.getIdServizio().getVersione()));
				}
				else {
					dati.setIdServizio(IDServizioFactory.getInstance().getIDServizioFromValuesWithoutCheck(
							null, 
							null, 
							erogatore.getTipo(), 
							erogatore.getNome(), 
							-1));
				}
			}
			dati.setGruppo(parse(tmp, CONFIG_GRUPPO));
			dati.setConnettore(parse(tmp, CONFIG_CONNETTORE));
			
			dati.setTokenPolicyNegoziazione(parse(tmp, CONFIG_POLICY_NEGOZIAZIONE));
			
			dati.setTokenPolicyValidazioneDynamicDiscovery(parse(tmp, CONFIG_POLICY_VALIDAZIONE_DYNAMIC_DISCOVERY));
			dati.setTokenPolicyValidazioneJwt(parse(tmp, CONFIG_POLICY_VALIDAZIONE_JWT));
			dati.setTokenPolicyValidazioneIntrospection(parse(tmp, CONFIG_POLICY_VALIDAZIONE_INTROSPECTION));
			dati.setTokenPolicyValidazioneUserInfo(parse(tmp, CONFIG_POLICY_VALIDAZIONE_USER_INFO));
			
			dati.setAttributeAuthority(parse(tmp, CONFIG_ATTRIBUTE_AUTHORITY));
			dati.setAttributeAuthorityResponseJwt(parse(tmp, CONFIG_ATTRIBUTE_AUTHORITY_VALIDAZIONE_RISPOSTA_JWT));
			
			dati.setNomePorta(parse(tmp, CONFIG_NOME_PORTA_INBOUND_PROPERTY));
			if(dati.getNomePorta()==null) {
				dati.setNomePorta(parse(tmp, CONFIG_NOME_PORTA_OUTBOUND_PROPERTY));
			}
			if(dati.getNomePorta()==null) {
				dati.setNomePorta(parse(tmp, CONFIG_NOME_PORTA_PROPERTY));
			}
		}
		return dati;
	}
	private static String parse(String [] tmp, String prefix) {
		for (String s : tmp) {
			if(s!=null) {
				s = s.trim();
				if(s.startsWith(prefix) && s.length()>prefix.length()) {
					return s.substring(prefix.length(),s.length());
				}
			}
		}
		return null;
	}
	private static RuoloPolicy parseRuoloPolicy(String [] tmp, String prefix) {
		String v = parse(tmp, prefix);
		if(CONFIG_TIPOLOGIA_VALUE_APPLICATIVA.equals(v)) {
			return RuoloPolicy.APPLICATIVA;
		}
		else if(CONFIG_TIPOLOGIA_VALUE_DELEGATA.equals(v)) {
			return RuoloPolicy.DELEGATA;
		}
		return null;
	}
	private static IDSoggetto parseIdSoggetto(String [] tmp, String prefix) throws IDException {
		String v = parse(tmp, prefix);
		if(v!=null) {
			return IDSoggetto.toIDSoggetto(v);
		}
		return null;
	}
	private static IDServizio parseIDServizio(String [] tmp) throws IDException {
		String v = parse(tmp, CONFIG_SERVIZIO);
		if(v!=null) {
			return parseIDServizio(v);
		}
		return null;
	}
	private static IDServizio parseIDServizio(String formatString) throws IDException {
		String [] tmp = formatString.split("/");
		if(tmp.length!=3) {
			throw new IDException("Formato '"+formatString+"' non supportato, attesi 3 valori, trovati "+tmp.length);
		}
		String tipo = tmp[0];
		String nome = tmp[1];
		String versioneS = tmp[2];
		int versione = Integer.parseInt(versioneS);
		return IDServizioFactory.getInstance().getIDServizioFromValuesWithoutCheck(tipo, nome, null, null, versione);
	}
	
	public static List<String> buildConfigurazioneEventoAsList(PolicyDati dati, Integer sogliaMs, Boolean globale) {
		List<String> l = new ArrayList<>();
		if(globale!=null) {
			/** l.add(PolicyUtilities.GLOBALE_PROPERTY+globale); Lasciandolo in prima posizione non verrà serializzato su DB, ma verrà fornito solo all'interfaccia di notifica passiva */
			l.add(CONFIG_GLOBALE_PROPERTY+globale);
		}
		if(sogliaMs!=null) {
			l.add(CONFIG_TIMEOUT+sogliaMs);
		}
		if(dati!=null) {
			addConfigurazioneEventoAsList(dati, l);
		}
		return l;
	}
	private static void addConfigurazioneEventoAsList(PolicyDati dati, List<String> l) {
		if(dati.getProfilo()!=null) {
			l.add(CONFIG_PROFILO+dati.getProfilo());
		}
		addConfigurazioneEventoRuoloPorta(dati, l);
		if(dati.getIdFruitore()!=null) {
			l.add(CONFIG_FRUITORE+dati.getIdFruitore().toFormatString());
		}
		if(dati.getIdServizio()!=null) {
			if(dati.getIdServizio().getSoggettoErogatore()!=null &&
					dati.getIdServizio().getSoggettoErogatore().getTipo()!=null &&
					dati.getIdServizio().getSoggettoErogatore().getNome()!=null) {
				l.add(CONFIG_EROGATORE+dati.getIdServizio().getSoggettoErogatore().toFormatString());
			}
			if(dati.getIdServizio().getNome()!=null && dati.getIdServizio().getTipo()!=null && dati.getIdServizio().getVersione()!=null) {
				l.add(CONFIG_SERVIZIO+toFormatStringIDServizio(dati.getIdServizio()));
			}
		}
		if(dati.getGruppo()!=null) {
			l.add(CONFIG_GRUPPO+dati.getGruppo());
		}
		if(dati.getConnettore()!=null) {
			l.add(CONFIG_CONNETTORE+dati.getConnettore());
		}
		if(dati.getTokenPolicyNegoziazione()!=null) {
			l.add(CONFIG_POLICY_NEGOZIAZIONE+dati.getTokenPolicyNegoziazione());
		}
		if(dati.getTokenPolicyValidazioneDynamicDiscovery()!=null) {
			l.add(CONFIG_POLICY_VALIDAZIONE_DYNAMIC_DISCOVERY+dati.getTokenPolicyValidazioneDynamicDiscovery());
		}
		if(dati.getTokenPolicyValidazioneJwt()!=null) {
			l.add(CONFIG_POLICY_VALIDAZIONE_JWT+dati.getTokenPolicyValidazioneJwt());
		}
		if(dati.getTokenPolicyValidazioneIntrospection()!=null) {
			l.add(CONFIG_POLICY_VALIDAZIONE_INTROSPECTION+dati.getTokenPolicyValidazioneIntrospection());
		}
		if(dati.getTokenPolicyValidazioneUserInfo()!=null) {
			l.add(CONFIG_POLICY_VALIDAZIONE_USER_INFO+dati.getTokenPolicyValidazioneUserInfo());
		}
		if(dati.getAttributeAuthority()!=null) {
			l.add(CONFIG_ATTRIBUTE_AUTHORITY+dati.getAttributeAuthority());
		}
		if(dati.getAttributeAuthorityResponseJwt()!=null) {
			l.add(CONFIG_ATTRIBUTE_AUTHORITY_VALIDAZIONE_RISPOSTA_JWT+dati.getAttributeAuthorityResponseJwt());
		}
		addConfigurazioneEventoNomePorta(dati, l);
	}
	private static String toFormatStringIDServizio(IDServizio idServizio){
		StringBuilder sb = new StringBuilder();
		sb.append(idServizio.getTipo());
		sb.append("/");
		sb.append(idServizio.getNome());
		sb.append("/");
		sb.append(idServizio.getVersione());
		return sb.toString();
	}
	private static void addConfigurazioneEventoRuoloPorta(PolicyDati dati, List<String> l) {
		if(dati.getRuoloPorta()!=null) {
			switch (dati.getRuoloPorta()) {
			case DELEGATA:
				l.add(CONFIG_TIPOLOGIA+CONFIG_TIPOLOGIA_VALUE_DELEGATA);
				break;
			case APPLICATIVA:
				l.add(CONFIG_TIPOLOGIA+CONFIG_TIPOLOGIA_VALUE_APPLICATIVA);
				break;
			default:
				break;
			}
		}
	}
	private static void addConfigurazioneEventoNomePorta(PolicyDati dati, List<String> l) {
		if(dati.getNomePorta()!=null) {
			if(dati.getRuoloPorta()!=null) {
				switch (dati.getRuoloPorta()) {
				case DELEGATA:
					l.add(CONFIG_NOME_PORTA_OUTBOUND_PROPERTY+dati.getNomePorta());
					break;
				case APPLICATIVA:
					l.add(CONFIG_NOME_PORTA_INBOUND_PROPERTY+dati.getNomePorta());
					break;
				default:
					l.add(CONFIG_NOME_PORTA_PROPERTY+dati.getNomePorta());
					break;
				}
			}
			else {
				l.add(CONFIG_NOME_PORTA_PROPERTY+dati.getNomePorta());
			}
		}
	}
	
	private static String buildConfigurazioneEvento(PolicyDati dati, int sogliaMs, boolean globale) {
		
		List<String> l = buildConfigurazioneEventoAsList(dati, sogliaMs, globale);
		StringBuilder sb = new StringBuilder();
		if(!l.isEmpty()) {
			for (String v : l) {
				if(sb.length()>0) {
					sb.append("\n");
				}
				sb.append(v);
			}
		}
		return sb.toString();
		
	}
	
	public static SogliaReadTimeout buildSogliaRequestTimeout(int sogliaMs, boolean delegata, IProtocolFactory<?> protocolFactory) {
		return buildSogliaTimeout(sogliaMs, delegata, protocolFactory);
	}
	public static SogliaReadTimeout buildSogliaResponseTimeout(int sogliaMs, boolean delegata, IProtocolFactory<?> protocolFactory) {
		return buildSogliaTimeout(sogliaMs, delegata, protocolFactory);
	}
	private static SogliaReadTimeout buildSogliaTimeout(int sogliaMs, boolean delegata, IProtocolFactory<?> protocolFactory) {
		SogliaReadTimeout soglia = new SogliaReadTimeout();
		soglia.setConfigurazioneGlobale(true);
		soglia.setSogliaMs(sogliaMs);
				
		String api = "APINonIndividuata";
		soglia.setIdConfigurazione(api);
		
		PolicyDati dati = new PolicyDati();
		if(protocolFactory!=null) {
			dati.setProfilo(protocolFactory.getProtocol());
		}
		dati.setRuoloPorta(delegata ? RuoloPolicy.DELEGATA : RuoloPolicy.APPLICATIVA);
		
		soglia.setConfigurazione(buildConfigurazioneEvento(dati, sogliaMs, true));
				
		return soglia;
	}
	
	public static SogliaReadTimeout buildSogliaRequestTimeout(int sogliaMs, boolean configurazioneGlobale, PortaDelegata pd, 
			ReadTimeoutContextParam readTimeoutContextParam) throws DriverConfigurazioneException, ProtocolException {
		return buildSogliaTimeout(sogliaMs, configurazioneGlobale, pd.getNome(), RuoloPolicy.DELEGATA, null, null,
				readTimeoutContextParam);
	}
	public static SogliaReadTimeout buildSogliaRequestTimeout(int sogliaMs, boolean configurazioneGlobale, PortaApplicativa pa, 
			ReadTimeoutContextParam readTimeoutContextParam) throws DriverConfigurazioneException, ProtocolException {
		return buildSogliaTimeout(sogliaMs, configurazioneGlobale, pa.getNome(), RuoloPolicy.APPLICATIVA, null, null,
				readTimeoutContextParam);
	}
	
	public static SogliaReadTimeout buildSogliaResponseTimeout(int sogliaMs, boolean configurazioneGlobale, PortaDelegata pd, PolicyTimeoutConfig policyConfig, 
			ReadTimeoutContextParam readTimeoutContextParam) throws DriverConfigurazioneException, ProtocolException {
		return buildSogliaTimeout(sogliaMs, configurazioneGlobale, pd.getNome(), RuoloPolicy.DELEGATA, null, policyConfig,
				readTimeoutContextParam);
	}
	public static SogliaReadTimeout buildSogliaResponseTimeout(int sogliaMs, boolean configurazioneGlobale, PortaApplicativa pa, String nomeConnettoreAsincrono, PolicyTimeoutConfig policyConfig, 
			ReadTimeoutContextParam readTimeoutContextParam) throws DriverConfigurazioneException, ProtocolException {
		return buildSogliaTimeout(sogliaMs, configurazioneGlobale, pa.getNome(), RuoloPolicy.APPLICATIVA, nomeConnettoreAsincrono, policyConfig,
				readTimeoutContextParam);
	}
	
	private static SogliaReadTimeout buildSogliaTimeout(int sogliaMs, boolean configurazioneGlobale, String nomePorta, RuoloPolicy ruoloPorta, String nomeConnettoreAsincrono, PolicyTimeoutConfig policyConfig, 
			ReadTimeoutContextParam readTimeoutContextParam) throws DriverConfigurazioneException, ProtocolException {
		
		SogliaReadTimeout soglia = new SogliaReadTimeout();
		soglia.setSogliaMs(sogliaMs);
		
		soglia.setConfigurazioneGlobale(configurazioneGlobale);
		
		PolicyDati dati = readConfigurazione(nomePorta, ruoloPorta, nomeConnettoreAsincrono, policyConfig, readTimeoutContextParam);
		
		soglia.setIdConfigurazione(dati.getIdentificativo());
		
		soglia.setConfigurazione(buildConfigurazioneEvento(dati, sogliaMs, configurazioneGlobale));
				
		return soglia;
	}
	
	private static PolicyDati readConfigurazione(String nomePorta, RuoloPolicy ruoloPorta, String nomeConnettoreAsincrono, PolicyTimeoutConfig policyConfig, ReadTimeoutContextParam readTimeoutContextParam) throws DriverConfigurazioneException, ProtocolException {
		
		ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance(readTimeoutContextParam.getState());
		
		StringBuilder sb = new StringBuilder();
		
		PolicyDati dati = RuoloPolicy.DELEGATA.equals(ruoloPorta) ?
				PolicyVerifier.getIdAPIFruizione(nomePorta, configPdDManager, readTimeoutContextParam.getRequestInfo(), readTimeoutContextParam.getProtocolFactory())
				:
				PolicyVerifier.getIdAPIErogazione(nomePorta, configPdDManager, readTimeoutContextParam.getRequestInfo(), readTimeoutContextParam.getProtocolFactory());
		String api = dati.getIdentificativo();
		if(api!=null) {
			api = api.trim();
		}
		sb.append(api);
		
		if(RuoloPolicy.APPLICATIVA.equals(ruoloPorta)) {
			if(nomeConnettoreAsincrono!=null) {
				sb.append(" (connettore '").append(nomeConnettoreAsincrono).append("')");
				dati.setConnettore(nomeConnettoreAsincrono);
			}
			else {
				String connettoriMultipli = ConnettoreUtils.getNomeConnettori(readTimeoutContextParam.getContext());
				if(connettoriMultipli!=null) {
					sb.append(" (connettore '").append(connettoriMultipli).append("')");
					dati.setConnettore(connettoriMultipli);
				}
			}
		}
		
		addPolicyInfo(sb, policyConfig, dati);
		
		dati.setIdentificativo(sb.toString()); // aggiorno
		
		return dati;
	}
	
	public static void addPolicyInfo(StringBuilder sb, PolicyTimeoutConfig policyConfig) {
		addPolicyInfo(sb, policyConfig, null);
	}
	public static void addPolicyInfo(StringBuilder sb, PolicyTimeoutConfig policyConfig, PolicyDati policyDati) {
		
		addPolicyInfoTokenPolicyNegoziazione(sb, policyConfig, policyDati);
		
		addPolicyInfoTokenPolicyValidazione(sb, policyConfig, policyDati);
		
		addPolicyInfoAttributeAuthority(sb, policyConfig, policyDati);
	}
	
	private static void addPolicyInfoTokenPolicyNegoziazione(StringBuilder sb, PolicyTimeoutConfig policyConfig, PolicyDati policyDati) {
		if(policyConfig!=null && policyConfig.getPolicyNegoziazione()!=null) {
			sb.append(" (").append(ID_POLICY_NEGOZIAZIONE).append(" '").append(policyConfig.getPolicyNegoziazione()).append("')");
			if(policyDati!=null) {
				policyDati.setTokenPolicyNegoziazione(policyConfig.getPolicyNegoziazione());
			}
		}
	}
	private static void addPolicyInfoTokenPolicyValidazione(StringBuilder sb, PolicyTimeoutConfig policyConfig, PolicyDati policyDati) {
		addPolicyInfoTokenPolicyValidazioneDynamicDiscovery(sb, policyConfig, policyDati);
		addPolicyInfoTokenPolicyValidazioneJwt(sb, policyConfig, policyDati);
		addPolicyInfoTokenPolicyValidazioneIntrospection(sb, policyConfig, policyDati);
		addPolicyInfoTokenPolicyValidazioneUserInfo(sb, policyConfig, policyDati);
	}
	private static void addPolicyInfoTokenPolicyValidazioneDynamicDiscovery(StringBuilder sb, PolicyTimeoutConfig policyConfig, PolicyDati policyDati) {
		if(policyConfig!=null && policyConfig.getPolicyValidazioneDynamicDiscovery()!=null) {
			sb.append(" (").append(ID_POLICY_VALIDAZIONE_DYNAMIC_DISCOVERY).append(" '").append(policyConfig.getPolicyValidazioneDynamicDiscovery()).append("')");
			if(policyDati!=null) {
				policyDati.setTokenPolicyValidazioneDynamicDiscovery(policyConfig.getPolicyValidazioneDynamicDiscovery());
			}
		}
	}
	private static void addPolicyInfoTokenPolicyValidazioneJwt(StringBuilder sb, PolicyTimeoutConfig policyConfig, PolicyDati policyDati) {
		if(policyConfig!=null && policyConfig.getPolicyValidazioneJwt()!=null) {
			sb.append(" (").append(ID_POLICY_VALIDAZIONE_JWT).append(" '").append(policyConfig.getPolicyValidazioneJwt()).append("')");
			if(policyDati!=null) {
				policyDati.setTokenPolicyValidazioneJwt(policyConfig.getPolicyValidazioneJwt());
			}
		}
	}
	private static void addPolicyInfoTokenPolicyValidazioneIntrospection(StringBuilder sb, PolicyTimeoutConfig policyConfig, PolicyDati policyDati) {
		if(policyConfig!=null && policyConfig.getPolicyValidazioneIntrospection()!=null) {
			sb.append(" (").append(ID_POLICY_VALIDAZIONE_INTROSPECTION).append(" '").append(policyConfig.getPolicyValidazioneIntrospection()).append("')");
			if(policyDati!=null) {
				policyDati.setTokenPolicyValidazioneIntrospection(policyConfig.getPolicyValidazioneIntrospection());
			}
		}
	}
	private static void addPolicyInfoTokenPolicyValidazioneUserInfo(StringBuilder sb, PolicyTimeoutConfig policyConfig, PolicyDati policyDati) {
		if(policyConfig!=null && policyConfig.getPolicyValidazioneUserInfo()!=null) {
			sb.append(" (").append(ID_POLICY_VALIDAZIONE_USER_INFO).append(" '").append(policyConfig.getPolicyValidazioneUserInfo()).append("')");
			if(policyDati!=null) {
				policyDati.setTokenPolicyValidazioneUserInfo(policyConfig.getPolicyValidazioneUserInfo());
			}
		}
	}
	private static void addPolicyInfoAttributeAuthority(StringBuilder sb, PolicyTimeoutConfig policyConfig, PolicyDati policyDati) {
		addPolicyInfoAttributeAuthorityEndpoint(sb, policyConfig, policyDati);
		addPolicyInfoAttributeAuthorityValidazioneJwtResponse(sb, policyConfig, policyDati);
	}
	private static void addPolicyInfoAttributeAuthorityEndpoint(StringBuilder sb, PolicyTimeoutConfig policyConfig, PolicyDati policyDati) {
		if(policyConfig!=null && policyConfig.getAttributeAuthority()!=null) {
			sb.append(" (").append(ID_ATTRIBUTE_AUTHORITY).append(" '").append(policyConfig.getAttributeAuthority()).append("')");
			if(policyDati!=null) {
				policyDati.setAttributeAuthority(policyConfig.getAttributeAuthority());
			}
		}
	}
	private static void addPolicyInfoAttributeAuthorityValidazioneJwtResponse(StringBuilder sb, PolicyTimeoutConfig policyConfig, PolicyDati policyDati) {
		if(policyConfig!=null && policyConfig.getAttributeAuthorityResponseJwt()!=null) {
			sb.append(" (").append(ID_ATTRIBUTE_AUTHORITY_VALIDAZIONE_RISPOSTA_JWT).append(" '").append(policyConfig.getAttributeAuthorityResponseJwt()).append("')");
			if(policyDati!=null) {
				policyDati.setAttributeAuthorityResponseJwt(policyConfig.getAttributeAuthorityResponseJwt());
			}
		}
	}
	
}
