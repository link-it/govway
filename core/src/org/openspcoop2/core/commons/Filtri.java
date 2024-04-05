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
	private Filtri() {}

	
	public static final String FILTRO_PROTOCOLLO = "filtroProtocollo";
	public static final String FILTRO_PROTOCOLLI = "filtroProtocolli";
	
	public static final String FILTRO_SOGGETTO_DEFAULT = "filtroSoggettoDefault";
	
	public static final String FILTRO_DOMINIO = "filtroDominio";
	
	public static final String FILTRO_STATO_ACCORDO = "filtroStatoAccordo";
		
	public static final String FILTRO_SERVICE_BINDING = "filtroServiceBinding";
	
	public static final String FILTRO_HTTP_METHOD = "filtroHttpMethod";
	
	public static final String FILTRO_SOGGETTO = "filtroSoggetto";
	
	public static final String FILTRO_SOGGETTO_EROGATORE_CONTAINS = "filtroSoggettoErogatoreContains";
	
	public static final String FILTRO_RUOLO_SERVIZIO_APPLICATIVO = "filtroRuoloSA";
	public static final String VALUE_FILTRO_RUOLO_SERVIZIO_APPLICATIVO_EROGATORE = "Erogatore";
	public static final String VALUE_FILTRO_RUOLO_SERVIZIO_APPLICATIVO_FRUITORE = "Fruitore";
	
	public static final String FILTRO_TIPO_SERVIZIO_APPLICATIVO = "filtroTipoSA";
	
	public static final String FILTRO_TIPO_SOGGETTO = "filtroTipoSoggetto";
	
	public static final String FILTRO_TIPO_CREDENZIALI = "filtroTipoCredenziali";
	public static final String FILTRO_CREDENZIALE = "filtroCredenziale";
	public static final String FILTRO_CREDENZIALE_ISSUER = "filtroCredenzialeIssuer";
	public static final String FILTRO_CREDENZIALE_TOKEN_POLICY = "filtroCredenzialeTokenPolicy";
	
	public static final String FILTRO_RUOLO_TIPOLOGIA = "filtroRuoloTipologia";
	public static final String FILTRO_RUOLO_CONTESTO = "filtroRuoloContesto";
	
	public static final String FILTRO_SCOPE_TIPOLOGIA = "filtroScopeTipologia";
	public static final String FILTRO_SCOPE_CONTESTO = "filtroScopeContesto";
	
	public static final String FILTRO_API_CONTESTO = "filtroApiContesto";
	public static final String FILTRO_API_CONTESTO_VALUE_EROGAZIONE_FRUIZIONE = "ErogazioneFruizione";
	public static final String FILTRO_API_CONTESTO_VALUE_APPLICATIVI = "Applicativi";
	public static final String FILTRO_API_CONTESTO_VALUE_SOGGETTI = "Soggetti";
	public static final String FILTRO_API_IMPLEMENTAZIONE = "filtroApiImpl";
	
	public static final String FILTRO_SERVIZIO_APPLICATIVO = "filtroSA";
	
	public static final String FILTRO_UTENTE = "filtroUtente";
	
	public static final String FILTRO_AZIONE = "filtroAzione";
	
	public static final String FILTRO_RUOLO = "filtroRuolo";
	public static final String FILTRO_RUOLO_NOME = "Ruolo";
	public static final String FILTRO_RUOLO_VALORE_ENTRAMBI = "Qualsiasi";
	public static final String FILTRO_RUOLO_VALORE_FRUIZIONE = "Fruizione";
	public static final String FILTRO_RUOLO_VALORE_EROGAZIONE = "Erogazione";
	
	public static final String FILTRO_APPLICABILITA = "filtroRuolo";
	public static final String FILTRO_APPLICABILITA_NOME = "Applicabilita";
	public static final String FILTRO_APPLICABILITA_VALORE_QUALSIASI = "Qualsiasi";
	public static final String FILTRO_APPLICABILITA_VALORE_FRUIZIONE = "Fruizione";
	public static final String FILTRO_APPLICABILITA_VALORE_EROGAZIONE = "Erogazione";
	public static final String FILTRO_APPLICABILITA_VALORE_IMPLEMENTAZIONE_API = "ImplementazioneApi";
	public static final String FILTRO_APPLICABILITA_VALORE_CONFIGURAZIONE = "Configurazione";
	
	public static final String FILTRO_STATO = "filtroStato";
	public static final String FILTRO_STATO_NOME = "Stato";
	public static final String FILTRO_STATO_VALORE_QUALSIASI = "Qualsiasi";
	public static final String FILTRO_STATO_VALORE_ABILITATO = "Abilitato";
	public static final String FILTRO_STATO_VALORE_DISABILITATO = "Disabilitato";
	public static final String FILTRO_STATO_VALORE_OK = "Ok";
	public static final String FILTRO_STATO_VALORE_WARNING = "Warning";
	public static final String FILTRO_STATO_VALORE_ERROR = "Error";
	
	public static final String FILTRO_TIPO_POLICY = "filtroTipoPolicy";
	public static final String FILTRO_TIPO_POLICY_BUILT_IN = "built-in";
	public static final String FILTRO_TIPO_POLICY_UTENTE = "utente";
	
	public static final String FILTRO_TIPO_RISORSA_POLICY = "filtroTipoRisorsaPolicy";
	
	public static final String FILTRO_TIPO_TOKEN_POLICY = "filtroTipoTokenPolicy";
	
	public static final String FILTRO_GRUPPO_SERVICE_BINDING = "filtroGruppoServiceBinding";
	
	public static final String FILTRO_GRUPPO = "filtroGruppo";
	
	public static final String FILTRO_API = "filtroApi";
	
	public static final String FILTRO_CANALE = "filtroCanale";
	public static final String PREFIX_VALUE_CANALE_DEFAULT = "__DEFAULT__ ";

	public static final String FILTRO_TIPO_PLUGIN_CLASSI = "filtroTipoPluginClassi";
	
	public static final String FILTRO_PROP_PLUGIN_CLASSI = "filtroPropPluginClassi";
	
	public static final String FILTRO_AUTENTICAZIONE_TOKEN_TIPO = "filtroAutenticazioneTokenTipo";
	
	public static final String FILTRO_AUTENTICAZIONE_TRASPORTO_TIPO = "filtroAutenticazioneTrasportoTipo";
	public static final String FILTRO_AUTENTICAZIONE_TRASPORTO_TIPO_PLUGIN = "filtroAutenticazioneTrasportoTipoPlugin";
	
	public static final String FILTRO_CONFIGURAZIONE_TRANSAZIONI = "filtroConfigurazioneTransazioni";
	public static final String FILTRO_CONFIGURAZIONE_TRANSAZIONI_VALORE_DEFAULT = "Default";
	public static final String FILTRO_CONFIGURAZIONE_TRANSAZIONI_VALORE_RIDEFINITO_ABILITATO_DATABASE_O_FILETRACE = "Ridefinito (abilitato database o filetrace)";
	public static final String FILTRO_CONFIGURAZIONE_TRANSAZIONI_VALORE_RIDEFINITO_ABILITATO_SOLO_DATABASE = "Ridefinito (abilitato solo database)";
	public static final String FILTRO_CONFIGURAZIONE_TRANSAZIONI_VALORE_RIDEFINITO_ABILITATO_SOLO_FILETRACE = "Ridefinito (abilitato solo filetrace)";
	public static final String FILTRO_CONFIGURAZIONE_TRANSAZIONI_VALORE_RIDEFINITO_ABILITATO_DATABASE_E_FILETRACE = "Ridefinito (abilitato sia database che filetrace)";
	public static final String FILTRO_CONFIGURAZIONE_TRANSAZIONI_VALORE_RIDEFINITO_DISABILITATO = "Ridefinito (disabilitato)";
	
	public static final String FILTRO_CONFIGURAZIONE_DUMP_TIPO = "filtroConfigurazioneDumpTipo";
	public static final String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_DEFAULT = "Default";
	public static final String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO = "Ridefinito (abilitato)";
	public static final String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER = "Ridefinito (solo header)";
	public static final String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RICHIESTA = "Ridefinito (abilitato sulla richiesta)";
	public static final String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RICHIESTA = "Ridefinito (solo header della richiesta)";
	public static final String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RISPOSTA = "Ridefinito (abilitato sulla risposta)";
	public static final String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RISPOSTA = "Ridefinito (solo header della risposta)";
	public static final String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RICHIESTA_USCITA = "Ridefinito (abilitato sulla richiesta in uscita)";
	public static final String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RICHIESTA_USCITA = "Ridefinito (solo header della richiesta in uscita)";
	public static final String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RISPOSTA_USCITA = "Ridefinito (abilitato sulla risposta in uscita)";
	public static final String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RISPOSTA_USCITA = "Ridefinito (solo header della risposta in uscita)";
	public static final String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RICHIESTA_INGRESSO = "Ridefinito (abilitato sulla richiesta in ingresso)";
	public static final String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RICHIESTA_INGRESSO = "Ridefinito (solo header della richiesta in ingresso)";
	public static final String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_RISPOSTA_INGRESSO = "Ridefinito (abilitato sulla risposta in ingresso)";
	public static final String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_ABILITATO_SOLO_HEADER_RISPOSTA_INGRESSO = "Ridefinito (solo header della risposta in ingresso)";
	public static final String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_DISABILITATO = "Ridefinito (disabilitato)"; // and su richiesta e risposta
	public static final String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_DISABILITATO_RICHIESTA = "Ridefinito (disabilitato sulla richiesta)";
	public static final String FILTRO_CONFIGURAZIONE_DUMP_TIPO_VALORE_RIDEFINITO_DISABILITATO_RISPOSTA = "Ridefinito (disabilitato sulla risposta)";
	
	public static final String FILTRO_CONFIGURAZIONE_STATO = "filtroConfigurazioneStato";
	public static final String FILTRO_CONFIGURAZIONE_STATO_VALORE_ABILITATO = "Abilitato";
	public static final String FILTRO_CONFIGURAZIONE_STATO_VALORE_DISABILITATO = "Disabilitato";
		
	public static final String FILTRO_CONFIGURAZIONE_RATE_LIMITING_STATO = "filtroConfigurazioneRateLimitingStato";
	public static final String FILTRO_CONFIGURAZIONE_RATE_LIMITING_STATO_VALORE_ABILITATO = "Abilitato";
	public static final String FILTRO_CONFIGURAZIONE_RATE_LIMITING_STATO_VALORE_DISABILITATO = "Disabilitato";
	
	public static final String FILTRO_CONFIGURAZIONE_VALIDAZIONE_STATO = "filtroConfigurazioneValidazioneStato";
	public static final String FILTRO_CONFIGURAZIONE_VALIDAZIONE_STATO_VALORE_ABILITATO = "Abilitato";
	public static final String FILTRO_CONFIGURAZIONE_VALIDAZIONE_STATO_VALORE_DISABILITATO = "Disabilitato";
	
	public static final String FILTRO_CONFIGURAZIONE_CACHE_RISPOSTA_STATO = "filtroConfigurazioneCacheRispostaStato";
	public static final String FILTRO_CONFIGURAZIONE_CACHE_RISPOSTA_STATO_VALORE_ABILITATO = "Abilitato";
	public static final String FILTRO_CONFIGURAZIONE_CACHE_RISPOSTA_STATO_VALORE_DISABILITATO = "Disabilitato";
	
	public static final String FILTRO_CONFIGURAZIONE_MESSAGE_SECURITY_STATO = "filtroConfigurazioneMessageSecurityStato";
	public static final String FILTRO_CONFIGURAZIONE_MESSAGE_SECURITY_VALORE_ABILITATO = "Abilitato";
	public static final String FILTRO_CONFIGURAZIONE_MESSAGE_SECURITY_STATO_VALORE_ABILITATO_RICHIESTA = "Abilitato sulla richiesta";
	public static final String FILTRO_CONFIGURAZIONE_MESSAGE_SECURITY_STATO_VALORE_ABILITATO_RISPOSTA = "Abilitato sulla risposta";
	public static final String FILTRO_CONFIGURAZIONE_MESSAGE_SECURITY_VALORE_DISABILITATO = "Disabilitato";
	
	public static final String FILTRO_CONFIGURAZIONE_MTOM_STATO = "filtroConfigurazioneMTOMStato";
	public static final String FILTRO_CONFIGURAZIONE_MTOM_VALORE_ABILITATO = "Abilitato";
	public static final String FILTRO_CONFIGURAZIONE_MTOM_STATO_VALORE_ABILITATO_RICHIESTA = "Abilitato sulla richiesta";
	public static final String FILTRO_CONFIGURAZIONE_MTOM_STATO_VALORE_ABILITATO_RISPOSTA = "Abilitato sulla risposta";
	public static final String FILTRO_CONFIGURAZIONE_MTOM_VALORE_DISABILITATO = "Disabilitato";
	
	public static final String FILTRO_CONFIGURAZIONE_TRASFORMAZIONE_STATO = "filtroConfigurazioneTrasformazione";
	public static final String FILTRO_CONFIGURAZIONE_TRASFORMAZIONE_STATO_VALORE_ABILITATO = "Abilitato";
	public static final String FILTRO_CONFIGURAZIONE_TRASFORMAZIONE_STATO_VALORE_DISABILITATO = "Disabilitato";
	
	public static final String FILTRO_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_STATO = "filtroConfigurazioneCorrelazioneApplicativaStato";
	public static final String FILTRO_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_VALORE_ABILITATO = "Abilitato";
	public static final String FILTRO_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_STATO_VALORE_ABILITATO_RICHIESTA = "Abilitato sulla richiesta";
	public static final String FILTRO_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_STATO_VALORE_ABILITATO_RISPOSTA = "Abilitato sulla risposta";
	public static final String FILTRO_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_VALORE_DISABILITATO = "Disabilitato";
	
	public static final String FILTRO_CONFIGURAZIONE_CORS_TIPO = "filtroConfigurazioneCorsTipo";
	public static final String FILTRO_CONFIGURAZIONE_CORS_TIPO_VALORE_DEFAULT = "Default";
	public static final String FILTRO_CONFIGURAZIONE_CORS_TIPO_VALORE_RIDEFINITO_ABILITATO = "Ridefinito (abilitato)";
	public static final String FILTRO_CONFIGURAZIONE_CORS_TIPO_VALORE_RIDEFINITO_DISABILITATO = "Ridefinito (disabilitato)"; 
	
	public static final String FILTRO_CONFIGURAZIONE_CORS_ORIGIN = "filtroConfigurazioneCorsOrigin";
	
	public static final String FILTRO_CONNETTORE_TIPO = "filtroConnettoreTipo";
	public static final String FILTRO_CONNETTORE_TIPO_VALORE_IM = "IM";
	public static final String FILTRO_CONNETTORE_TIPO_PLUGIN = "filtroConnettoreTipoPlugin";
	public static final String FILTRO_CONNETTORE_TOKEN_POLICY = "filtroConnettoreTokenPolicy";
	public static final String FILTRO_CONNETTORE_ENDPOINT = "filtroConnettoreEndpoint";
	public static final String FILTRO_CONNETTORE_KEYSTORE = "filtroConnettoreKeystore";
	
	public static final String FILTRO_CONNETTORE_DEBUG = "filtroConnettoreDebug";
	public static final String FILTRO_CONNETTORE_DEBUG_VALORE_ABILITATO = "Abilitato";
	public static final String FILTRO_CONNETTORE_DEBUG_VALORE_DISABILITATO = "Disabilitato";
	
	public static final String FILTRO_CONNETTORE_MULTIPLO_NOME = "filtroConnettoreMulNome";
	public static final String FILTRO_CONNETTORE_MULTIPLO_FILTRO = "filtroConnettoreMulFiltro";
	
	public static final String FILTRO_MODI_SICUREZZA_CANALE = "filtroModiSicCanale";
	public static final String FILTRO_MODI_SICUREZZA_MESSAGGIO = "filtroModiSicMessaggio";
	public static final String FILTRO_MODI_SORGENTE_TOKEN = "filtroModiGenToken";
	public static final String FILTRO_MODI_KEYSTORE_PATH = "filtroModiKeystorePath";
	public static final String FILTRO_MODI_KEYSTORE_SUBJECT = "filtroModiKeystoreSubject";
	public static final String FILTRO_MODI_KEYSTORE_ISSUER = "filtroModiKeystoreIssuer";
	public static final String FILTRO_MODI_SICUREZZA_TOKEN = "filtroModiTokenStato";
	public static final String FILTRO_MODI_SICUREZZA_TOKEN_POLICY = "filtroModiTokenPolicy";
	public static final String FILTRO_MODI_SICUREZZA_TOKEN_CLIENT_ID = "filtroModiTokenClientId";
	public static final String FILTRO_MODI_AUDIENCE = "filtroModiAudience";
	public static final String FILTRO_MODI_DIGEST_RICHIESTA = "filtroModiDigestRich";
	public static final String FILTRO_MODI_INFORMAZIONI_UTENTE = "filtroModiInfoUtente";
	
	public static final String FILTRO_PROPRIETA_NOME = "filtroPropNome";
	public static final String FILTRO_PROPRIETA_VALORE = "filtroPropValore";
	
	public static final String FILTRO_REMOTE_STORE_ID = "filtroRemoteStoreId";
	public static final String FILTRO_REMOTE_STORE_KEY_KID = "filtroRemoteStoreKeyKid";
	public static final String FILTRO_REMOTE_STORE_KEY_CLIENT_ID = "filtroRemoteStoreKeyClientId";
	public static final String FILTRO_REMOTE_STORE_KEY_ORGANIZZAZIONE = "filtroRemoteStoreKeyOrganizzazione";
	
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
			tipoSoggettiProtocollo = convertToTipiSoggetti(filterProtocolli);
		}
		return tipoSoggettiProtocollo;
	}
	private static List<String> convertToTipiSoggetti(String filterProtocolli) throws CoreException {
		List<String> tipoSoggettiProtocollo = null;
		List<String> protocolli = Filtri.convertToList(filterProtocolli);
		if(protocolli!=null && !protocolli.isEmpty()) {
			tipoSoggettiProtocollo = new ArrayList<>();
			for (String protocollo : protocolli) {
				try {
					List<String> tipi = ProtocolFactoryReflectionUtils.getOrganizationTypes(protocollo);
					if(tipi!=null && !tipi.isEmpty()) {
						tipoSoggettiProtocollo.addAll(tipi);
					}
				}catch(Exception e) {
					throw new CoreException(e.getMessage(),e);
				}
			}
			if(tipoSoggettiProtocollo.isEmpty()) {
				tipoSoggettiProtocollo = null;
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
			tipoServiziProtocollo = convertToTipiServizi(filterProtocolli);
		}
		return tipoServiziProtocollo;
	}
	private static List<String> convertToTipiServizi(String filterProtocolli) throws CoreException {
		List<String> tipoServiziProtocollo = null;
		List<String> protocolli = Filtri.convertToList(filterProtocolli);
		if(protocolli!=null && !protocolli.isEmpty()) {
			tipoServiziProtocollo = new ArrayList<>();
			for (String protocollo : protocolli) {
				try {
					List<String> tipi = ProtocolFactoryReflectionUtils.getServiceTypes(protocollo);
					if(tipi!=null && !tipi.isEmpty()) {
						tipoServiziProtocollo.addAll(tipi);
					}
				}catch(Exception e) {
					throw new CoreException(e.getMessage(),e);
				}
			}
			if(tipoServiziProtocollo.isEmpty()) {
				tipoServiziProtocollo = null;
			}
		}
		return tipoServiziProtocollo;
	}
	
	
	
	public static String convertToString(List<String> listSrc) {
		if(listSrc==null || listSrc.isEmpty()) {
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
		List<String> l = null;
		if(src==null) {
			return l;
		}
		l = new ArrayList<>();
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
