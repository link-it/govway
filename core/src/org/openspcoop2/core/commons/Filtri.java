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




package org.openspcoop2.core.commons;

import java.util.ArrayList;
import java.util.List;


/**
 * Filtri
 * 
 * @author Stefano Corallo - corallo@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */

public final class Filtri
{

	
	public final static String FILTRO_PROTOCOLLO = "filtroProtocollo";
	public final static String FILTRO_PROTOCOLLI = "filtroProtocolli";
	
	public final static String FILTRO_SOGGETTO_DEFAULT = "filtroSoggettoDefault";
	
	public final static String FILTRO_DOMINIO = "filtroDominio";
	
	public final static String FILTRO_STATO_ACCORDO = "filtroStatoAccordo";
		
	public final static String FILTRO_SERVICE_BINDING = "filtroServiceBinding";
	
	public final static String FILTRO_HTTP_METHOD = "filtroHttpMethod";
	
	public final static String FILTRO_SOGGETTO = "filtroSoggetto";
	
	public final static String FILTRO_SOGGETTO_EROGATORE_CONTAINS = "filtroSoggettoErogatoreContains";
	
	public final static String FILTRO_RUOLO_SERVIZIO_APPLICATIVO = "filtroRuoloSA";
	public final static String VALUE_FILTRO_RUOLO_SERVIZIO_APPLICATIVO_EROGATORE = "Erogatore";
	public final static String VALUE_FILTRO_RUOLO_SERVIZIO_APPLICATIVO_FRUITORE = "Fruitore";
	
	public final static String FILTRO_TIPO_SERVIZIO_APPLICATIVO = "filtroTipoSA";
	
	public final static String FILTRO_TIPO_SOGGETTO = "filtroTipoSoggetto";
	
	public final static String FILTRO_TIPO_CREDENZIALI = "filtroTipoCredenziali";
	public final static String FILTRO_CREDENZIALE = "filtroCredenziale";
	public final static String FILTRO_CREDENZIALE_ISSUER = "filtroCredenzialeIssuer";
	public final static String FILTRO_CREDENZIALE_TOKEN_POLICY = "filtroCredenzialeTokenPolicy";
	
	public final static String FILTRO_RUOLO_TIPOLOGIA = "filtroRuoloTipologia";
	public final static String FILTRO_RUOLO_CONTESTO = "filtroRuoloContesto";
	
	public final static String FILTRO_SCOPE_TIPOLOGIA = "filtroScopeTipologia";
	public final static String FILTRO_SCOPE_CONTESTO = "filtroScopeContesto";
	
	public final static String FILTRO_API_CONTESTO = "filtroApiContesto";
	public final static String FILTRO_API_CONTESTO_VALUE_EROGAZIONE_FRUIZIONE = "ErogazioneFruizione";
	public final static String FILTRO_API_CONTESTO_VALUE_APPLICATIVI = "Applicativi";
	public final static String FILTRO_API_CONTESTO_VALUE_SOGGETTI = "Soggetti";
	public final static String FILTRO_API_IMPLEMENTAZIONE = "filtroApiImpl";
	
	public final static String FILTRO_SERVIZIO_APPLICATIVO = "filtroSA";
	
	public final static String FILTRO_UTENTE = "filtroUtente";
	
	public final static String FILTRO_AZIONE = "filtroAzione";
	
	public final static String FILTRO_RUOLO = "filtroRuolo";
	public final static String FILTRO_RUOLO_NOME = "Ruolo";
	public final static String FILTRO_RUOLO_VALORE_ENTRAMBI = "Qualsiasi";
	public final static String FILTRO_RUOLO_VALORE_FRUIZIONE = "Fruizione";
	public final static String FILTRO_RUOLO_VALORE_EROGAZIONE = "Erogazione";
	
	public final static String FILTRO_APPLICABILITA = "filtroRuolo";
	public final static String FILTRO_APPLICABILITA_NOME = "Applicabilita";
	public final static String FILTRO_APPLICABILITA_VALORE_QUALSIASI = "Qualsiasi";
	public final static String FILTRO_APPLICABILITA_VALORE_FRUIZIONE = "Fruizione";
	public final static String FILTRO_APPLICABILITA_VALORE_EROGAZIONE = "Erogazione";
	public final static String FILTRO_APPLICABILITA_VALORE_IMPLEMENTAZIONE_API = "ImplementazioneApi";
	public final static String FILTRO_APPLICABILITA_VALORE_CONFIGURAZIONE = "Configurazione";
	
	public final static String FILTRO_STATO = "filtroStato";
	public final static String FILTRO_STATO_NOME = "Stato";
	public final static String FILTRO_STATO_VALORE_QUALSIASI = "Qualsiasi";
	public final static String FILTRO_STATO_VALORE_ABILITATO = "Abilitato";
	public final static String FILTRO_STATO_VALORE_DISABILITATO = "Disabilitato";
	public final static String FILTRO_STATO_VALORE_OK = "Ok";
	public final static String FILTRO_STATO_VALORE_WARNING = "Warning";
	public final static String FILTRO_STATO_VALORE_ERROR = "Error";
	
	public final static String FILTRO_TIPO_POLICY = "filtroTipoPolicy";
	public final static String FILTRO_TIPO_POLICY_BUILT_IN = "built-in";
	public final static String FILTRO_TIPO_POLICY_UTENTE = "utente";
	
	public final static String FILTRO_TIPO_RISORSA_POLICY = "filtroTipoRisorsaPolicy";
	
	public final static String FILTRO_TIPO_TOKEN_POLICY = "filtroTipoTokenPolicy";
	
	public final static String FILTRO_GRUPPO_SERVICE_BINDING = "filtroGruppoServiceBinding";
	
	public final static String FILTRO_GRUPPO = "filtroGruppo";
	
	public final static String FILTRO_API = "filtroApi";
	
	public final static String FILTRO_CANALE = "filtroCanale";
	public final static String PREFIX_VALUE_CANALE_DEFAULT = "__DEFAULT__ ";

	public final static String FILTRO_TIPO_PLUGIN_CLASSI = "filtroTipoPluginClassi";
	
	public final static String FILTRO_PROP_PLUGIN_CLASSI = "filtroPropPluginClassi";
	
	public final static String FILTRO_AUTENTICAZIONE_TOKEN_TIPO = "filtroAutenticazioneTokenTipo";
	
	public final static String FILTRO_AUTENTICAZIONE_TRASPORTO_TIPO = "filtroAutenticazioneTrasportoTipo";
	public final static String FILTRO_AUTENTICAZIONE_TRASPORTO_TIPO_PLUGIN = "filtroAutenticazioneTrasportoTipoPlugin";
	
	public final static String FILTRO_CONFIGURAZIONE_DUMP_TIPO = "filtroConfigurazioneDumpTipo";
	public final static String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_DEFAULT = "Default";
	public final static String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO = "Ridefinito (abilitato)";
	public final static String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER = "Ridefinito (solo header)";
	public final static String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RICHIESTA = "Ridefinito (abilitato sulla richiesta)";
	public final static String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RICHIESTA = "Ridefinito (solo header della richiesta)";
	public final static String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RISPOSTA = "Ridefinito (abilitato sulla risposta)";
	public final static String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RISPOSTA = "Ridefinito (solo header della risposta)";
	public final static String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RICHIESTA_USCITA = "Ridefinito (abilitato sulla richiesta in uscita)";
	public final static String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RICHIESTA_USCITA = "Ridefinito (solo header della richiesta in uscita)";
	public final static String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RISPOSTA_USCITA = "Ridefinito (abilitato sulla risposta in uscita)";
	public final static String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RISPOSTA_USCITA = "Ridefinito (solo header della risposta in uscita)";
	public final static String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RICHIESTA_INGRESSO = "Ridefinito (abilitato sulla richiesta in ingresso)";
	public final static String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RICHIESTA_INGRESSO = "Ridefinito (solo header della richiesta in ingresso)";
	public final static String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RISPOSTA_INGRESSO = "Ridefinito (abilitato sulla risposta in ingresso)";
	public final static String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RISPOSTA_INGRESSO = "Ridefinito (solo header della risposta in ingresso)";
	public final static String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_DISABILITATO = "Ridefinito (disabilitato)"; // and su richiesta e risposta
	public final static String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_DISABILITATO_RICHIESTA = "Ridefinito (disabilitato sulla richiesta)";
	public final static String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_DISABILITATO_RISPOSTA = "Ridefinito (disabilitato sulla risposta)";
	
	public final static String FILTRO_CONFIGURAZIONE_STATO = "filtroConfigurazioneStato";
	public final static String FILTRO_CONFIGURAZIONE_STATO_VALORE_ABILITATO = "Abilitato";
	public final static String FILTRO_CONFIGURAZIONE_STATO_VALORE_DISABILITATO = "Disabilitato";
		
	public final static String FILTRO_CONFIGURAZIONE_RATE_LIMITING_STATO = "filtroConfigurazioneRateLimitingStato";
	public final static String FILTRO_CONFIGURAZIONE_RATE_LIMITING_STATO_VALORE_ABILITATO = "Abilitato";
	public final static String FILTRO_CONFIGURAZIONE_RATE_LIMITING_STATO_VALORE_DISABILITATO = "Disabilitato";
	
	public final static String FILTRO_CONFIGURAZIONE_VALIDAZIONE_STATO = "filtroConfigurazioneValidazioneStato";
	public final static String FILTRO_CONFIGURAZIONE_VALIDAZIONE_STATO_VALORE_ABILITATO = "Abilitato";
	public final static String FILTRO_CONFIGURAZIONE_VALIDAZIONE_STATO_VALORE_DISABILITATO = "Disabilitato";
	
	public final static String FILTRO_CONFIGURAZIONE_CACHE_RISPOSTA_STATO = "filtroConfigurazioneCacheRispostaStato";
	public final static String FILTRO_CONFIGURAZIONE_CACHE_RISPOSTA_STATO_VALORE_ABILITATO = "Abilitato";
	public final static String FILTRO_CONFIGURAZIONE_CACHE_RISPOSTA_STATO_VALORE_DISABILITATO = "Disabilitato";
	
	public final static String FILTRO_CONFIGURAZIONE_MESSAGE_SECURITY_STATO = "filtroConfigurazioneMessageSecurityStato";
	public final static String FILTRO_CONFIGURAZIONE_MESSAGE_SECURITY_VALORE_ABILITATO = "Abilitato";
	public final static String FILTRO_CONFIGURAZIONE_MESSAGE_SECURITY_STATO_VALORE_ABILITATO_RICHIESTA = "Abilitato sulla richiesta";
	public final static String FILTRO_CONFIGURAZIONE_MESSAGE_SECURITY_STATO_VALORE_ABILITATO_RISPOSTA = "Abilitato sulla risposta";
	public final static String FILTRO_CONFIGURAZIONE_MESSAGE_SECURITY_VALORE_DISABILITATO = "Disabilitato";
	
	public final static String FILTRO_CONFIGURAZIONE_MTOM_STATO = "filtroConfigurazioneMTOMStato";
	public final static String FILTRO_CONFIGURAZIONE_MTOM_VALORE_ABILITATO = "Abilitato";
	public final static String FILTRO_CONFIGURAZIONE_MTOM_STATO_VALORE_ABILITATO_RICHIESTA = "Abilitato sulla richiesta";
	public final static String FILTRO_CONFIGURAZIONE_MTOM_STATO_VALORE_ABILITATO_RISPOSTA = "Abilitato sulla risposta";
	public final static String FILTRO_CONFIGURAZIONE_MTOM_VALORE_DISABILITATO = "Disabilitato";
	
	public final static String FILTRO_CONFIGURAZIONE_TRASFORMAZIONE_STATO = "filtroConfigurazioneTrasformazione";
	public final static String FILTRO_CONFIGURAZIONE_TRASFORMAZIONE_STATO_VALORE_ABILITATO = "Abilitato";
	public final static String FILTRO_CONFIGURAZIONE_TRASFORMAZIONE_STATO_VALORE_DISABILITATO = "Disabilitato";
	
	public final static String FILTRO_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_STATO = "filtroConfigurazioneCorrelazioneApplicativaStato";
	public final static String FILTRO_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_VALORE_ABILITATO = "Abilitato";
	public final static String FILTRO_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_STATO_VALORE_ABILITATO_RICHIESTA = "Abilitato sulla richiesta";
	public final static String FILTRO_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_STATO_VALORE_ABILITATO_RISPOSTA = "Abilitato sulla risposta";
	public final static String FILTRO_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_VALORE_DISABILITATO = "Disabilitato";
	
	public final static String FILTRO_CONFIGURAZIONE_CORS_TIPO = "filtroConfigurazioneCorsTipo";
	public final static String FILTRO_CONFIGURAZIONE_CORS_TIPO_VALORE_DEFAULT = "Default";
	public final static String FILTRO_CONFIGURAZIONE_CORS_TIPO_VALORE_RIDEFINITO_ABILITATO = "Ridefinito (abilitato)";
	public final static String FILTRO_CONFIGURAZIONE_CORS_TIPO_VALORE_RIDEFINITO_DISABILITATO = "Ridefinito (disabilitato)"; 
	
	public final static String FILTRO_CONFIGURAZIONE_CORS_ORIGIN = "filtroConfigurazioneCorsOrigin";
	
	public final static String FILTRO_CONNETTORE_TIPO = "filtroConnettoreTipo";
	public final static String FILTRO_CONNETTORE_TIPO_VALORE_IM = "IM";
	public final static String FILTRO_CONNETTORE_TIPO_PLUGIN = "filtroConnettoreTipoPlugin";
	public final static String FILTRO_CONNETTORE_TOKEN_POLICY = "filtroConnettoreTokenPolicy";
	public final static String FILTRO_CONNETTORE_ENDPOINT = "filtroConnettoreEndpoint";
	public final static String FILTRO_CONNETTORE_KEYSTORE = "filtroConnettoreKeystore";
	
	public final static String FILTRO_CONNETTORE_DEBUG = "filtroConnettoreDebug";
	public final static String FILTRO_CONNETTORE_DEBUG_VALORE_ABILITATO = "Abilitato";
	public final static String FILTRO_CONNETTORE_DEBUG_VALORE_DISABILITATO = "Disabilitato";
	
	public final static String FILTRO_CONNETTORE_MULTIPLO_NOME = "filtroConnettoreMulNome";
	public final static String FILTRO_CONNETTORE_MULTIPLO_FILTRO = "filtroConnettoreMulFiltro";
	
	public final static String FILTRO_MODI_SICUREZZA_CANALE = "filtroModiSicCanale";
	public final static String FILTRO_MODI_SICUREZZA_MESSAGGIO = "filtroModiSicMessaggio";
	public final static String FILTRO_MODI_KEYSTORE_PATH = "filtroModiKeystorePath";
	public final static String FILTRO_MODI_KEYSTORE_SUBJECT = "filtroModiKeystoreSubject";
	public final static String FILTRO_MODI_KEYSTORE_ISSUER = "filtroModiKeystoreIssuer";
	public final static String FILTRO_MODI_SICUREZZA_TOKEN = "filtroModiTokenStato";
	public final static String FILTRO_MODI_SICUREZZA_TOKEN_POLICY = "filtroModiTokenPolicy";
	public final static String FILTRO_MODI_SICUREZZA_TOKEN_CLIENT_ID = "filtroModiTokenClientId";
	public final static String FILTRO_MODI_AUDIENCE = "filtroModiAudience";
	public final static String FILTRO_MODI_DIGEST_RICHIESTA = "filtroModiDigestRich";
	public final static String FILTRO_MODI_INFORMAZIONI_UTENTE = "filtroModiInfoUtente";
	
	public final static String FILTRO_PROPRIETA_NOME = "filtroPropNome";
	public final static String FILTRO_PROPRIETA_VALORE = "filtroPropValore";
	
	public static List<String> convertToTipiSoggetti(String filterProtocollo, String filterProtocolli) throws CoreException {
		List<String> tipoSoggettiProtocollo = null;
		if(filterProtocollo!=null && !"".equals(filterProtocollo)) {
			try {
				tipoSoggettiProtocollo = ProtocolFactoryReflectionUtils.getOrganizationTypes(filterProtocollo);
			}catch(Exception e) {
				throw new CoreException(e.getMessage(),e);
			}
		}
		else if(filterProtocolli!=null && !"".equals(filterProtocolli)) {
			List<String> protocolli = Filtri.convertToList(filterProtocolli);
			if(protocolli!=null && protocolli.size()>0) {
				tipoSoggettiProtocollo = new ArrayList<>();
				for (String protocollo : protocolli) {
					try {
						List<String> tipi = ProtocolFactoryReflectionUtils.getOrganizationTypes(protocollo);
						if(tipi!=null && tipi.size()>0) {
							tipoSoggettiProtocollo.addAll(tipi);
						}
					}catch(Exception e) {
						throw new CoreException(e.getMessage(),e);
					}
				}
				if(tipoSoggettiProtocollo.size()<=0) {
					tipoSoggettiProtocollo = null;
				}
			}
		}
		return tipoSoggettiProtocollo;
	}
	
	public static List<String> convertToTipiServizi(String filterProtocollo, String filterProtocolli) throws CoreException {
		List<String> tipoServiziProtocollo = null;
		if(filterProtocollo!=null && !"".equals(filterProtocollo)) {
			try {
				tipoServiziProtocollo = ProtocolFactoryReflectionUtils.getServiceTypes(filterProtocollo);
			}catch(Exception e) {
				throw new CoreException(e.getMessage(),e);
			}
		}
		else if(filterProtocolli!=null && !"".equals(filterProtocolli)) {
			List<String> protocolli = Filtri.convertToList(filterProtocolli);
			if(protocolli!=null && protocolli.size()>0) {
				tipoServiziProtocollo = new ArrayList<>();
				for (String protocollo : protocolli) {
					try {
						List<String> tipi = ProtocolFactoryReflectionUtils.getServiceTypes(protocollo);
						if(tipi!=null && tipi.size()>0) {
							tipoServiziProtocollo.addAll(tipi);
						}
					}catch(Exception e) {
						throw new CoreException(e.getMessage(),e);
					}
				}
				if(tipoServiziProtocollo.size()<=0) {
					tipoServiziProtocollo = null;
				}
			}
		}
		return tipoServiziProtocollo;
	}
	
	
	
	public static String convertToString(List<String> listSrc) {
		if(listSrc==null || listSrc.size()<=0) {
			return null;
		}
		StringBuilder bf = new StringBuilder();
		for (String src : listSrc) {
			if(bf.length()>0) {
				bf.append(",");
			}
			bf.append(src);
		}
		return bf.toString();
	}
	public static List<String> convertToList(String src) {
		if(src==null) {
			return null;
		}
		List<String> l = new ArrayList<>();
		if(src.contains(",")) {
			String [] tmp = src.split(",");
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].trim());
			}
		}
		else {
			l.add(src);
		}
		return l;
	}
	
}
