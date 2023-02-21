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
package org.openspcoop2.core.config.driver.db;

import static org.openspcoop2.core.constants.CostantiDB.CREATE;
import static org.openspcoop2.core.constants.CostantiDB.DELETE;
import static org.openspcoop2.core.constants.CostantiDB.UPDATE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.IExtendedInfo;
import org.openspcoop2.core.config.AttributeAuthority;
import org.openspcoop2.core.config.ConfigurazionePortaHandler;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.MessageSecurityFlow;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.core.config.MtomProcessor;
import org.openspcoop2.core.config.MtomProcessorFlow;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.PortaDelegataServizio;
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegataSoggettoErogatore;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.Scope;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaCacheDigestQueryParameter;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.constants.CRUDType;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_soggettiLIB
 * 
 * @author Stefano Corallo - corallo@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_porteDelegateLIB {



	
	/**
	 * 
	 * @param type
	 * @param aPD
	 * @param con
	 * @throws DriverConfigurazioneException
	 */
	public static long CRUDPortaDelegata(int type, PortaDelegata aPD, Connection con) throws DriverConfigurazioneException {

		if (aPD == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaDelegata] Porta Delegata non valida.");

		// parametri necessari
		String nomePorta = aPD.getNome();
		String nomeProprietario = aPD.getNomeSoggettoProprietario();
		String tipoProprietario = aPD.getTipoSoggettoProprietario();
		if (nomePorta == null || nomePorta.equals(""))
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaDelegata] Nome della Porta Delegata non valido.");
		if (nomeProprietario == null || nomeProprietario.equals(""))
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaDelegata] Nome proprietario Porta Delegata non valido.");
		if (tipoProprietario == null || tipoProprietario.equals(""))
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaDelegata] Tipo proprietario della Porta Delegata non valido.");

		PreparedStatement stm = null;
		//PreparedStatement stm1 = null;
		String sqlQuery;
		ResultSet rs = null;

		String autenticazione = aPD.getAutenticazione();
		String autorizzazione = aPD.getAutorizzazione();
		String autorizzazioneXacmlPolicy = aPD.getXacmlPolicy();
		String autorizzazioneContenuto = aPD.getAutorizzazioneContenuto();
		String descrizione = aPD.getDescrizione();
		GestioneToken gestioneToken = aPD.getGestioneToken();

		PortaDelegataAzione azione = aPD.getAzione();
		long idAzione = ((azione != null && azione.getId() != null) ? azione.getId() : -1);

		long idSoggettoProprietario;
		try {
			idSoggettoProprietario = DBUtils.getIdSoggetto(nomeProprietario, tipoProprietario, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
		} catch (Exception e1) {
			throw new DriverConfigurazioneException(e1);
		}
		long idPortaDelegata = -1;

		PortaDelegataSoggettoErogatore soggErogatore = aPD.getSoggettoErogatore();
		long idSoggettoErogatore = ((soggErogatore != null && soggErogatore.getId() != null) ? soggErogatore.getId() : -1);
		//System.out.println("PRIMA PD SOGGETTO: "+idSoggettoErogatore);
		// SEMPRE, VA AGGIORNATO ANCHE IN UPDATE if(idSoggettoErogatore<=0 &&
		if(
				soggErogatore!=null && soggErogatore.getTipo()!=null && soggErogatore.getNome()!=null) {
			try {
				idSoggettoErogatore = DBUtils.getIdSoggetto(soggErogatore.getNome(), soggErogatore.getTipo(), con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
				//System.out.println("DOPO PD SOGGETTO: "+idSoggettoErogatore);
			} catch (Throwable e1) {
				DriverConfigurazioneDB_LIB.log.debug(e1.getMessage(),e1); // potrebbe non esistere la tabella
			}
		}
		
		PortaDelegataServizio servizio = aPD.getServizio();
		long idServizioPD = ((servizio != null && servizio.getId() != null) ? servizio.getId() : -1);
		//System.out.println("PRIMA PD: "+idServizioPD);
		// SEMPRE, VA AGGIORNATO ANCHE IN UPDATE if(idServizioPD<=0 &&
		if(
				idSoggettoErogatore>0 && servizio!=null && servizio.getTipo()!=null && servizio.getNome()!=null && servizio.getVersione()!=null && servizio.getVersione()>0) {
			try {
				idServizioPD = DBUtils.getIdServizio(servizio.getNome(), servizio.getTipo(), servizio.getVersione(), idSoggettoErogatore, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
				//System.out.println("DOPO PD: "+idServizioPD);
			} catch (Throwable e1) {
				DriverConfigurazioneDB_LIB.log.debug(e1.getMessage(),e1); // potrebbe non esistere la tabella
			}
		}

		MtomProcessor mtomProcessor = aPD.getMtomProcessor();
		MTOMProcessorType mtomMode_request = null;
		MTOMProcessorType mtomMode_response = null;
		if(mtomProcessor!=null){
			if(mtomProcessor.getRequestFlow()!=null){
				mtomMode_request = mtomProcessor.getRequestFlow().getMode();
			}
			if(mtomProcessor.getResponseFlow()!=null){
				mtomMode_response = mtomProcessor.getResponseFlow().getMode();
			}
		}
		
		MessageSecurity messageSecurity = aPD.getMessageSecurity();
		String messageSecurityStatus = aPD.getStatoMessageSecurity();
		StatoFunzionalita messageSecurityApplyMtom_request = null;
		StatoFunzionalita messageSecurityApplyMtom_response = null;
		String securityRequestMode = null;
		String securityResponseMode = null;
		if(messageSecurity!=null){
			if(messageSecurity.getRequestFlow()!=null){
				messageSecurityApplyMtom_request = messageSecurity.getRequestFlow().getApplyToMtom();
				securityRequestMode = messageSecurity.getRequestFlow().getMode();
			}
			if(messageSecurity.getResponseFlow()!=null){
				messageSecurityApplyMtom_response = messageSecurity.getResponseFlow().getApplyToMtom();
				securityResponseMode = messageSecurity.getResponseFlow().getMode();
			}
		}
		
		CorrelazioneApplicativa corrApp = aPD.getCorrelazioneApplicativa();
		CorrelazioneApplicativaRisposta corrAppRisposta = aPD.getCorrelazioneApplicativaRisposta();
		
		String msg_diag_severita = null;
		String tracciamento_esiti = null;
		if(aPD.getTracciamento()!=null){
			msg_diag_severita = DriverConfigurazioneDB_LIB.getValue(aPD.getTracciamento().getSeverita());
			tracciamento_esiti = aPD.getTracciamento().getEsiti();
		}
		
		CorsConfigurazione corsConfigurazione = aPD.getGestioneCors();
		String cors_stato = null;
		String cors_tipo = null; 
		String cors_all_allow_origins = null; 
		String cors_all_allow_methods = null; 
		String cors_all_allow_headers = null; 
		String cors_allow_credentials = null; 
		int cors_allow_max_age = CostantiDB.FALSE;
		Integer cors_allow_max_age_seconds = null;
		String cors_allow_origins = null; 
		String cors_allow_headers = null; 
		String cors_allow_methods = null; 
		String cors_allow_expose_headers = null; 
		if(corsConfigurazione!=null) {
			cors_stato = DriverConfigurazioneDB_LIB.getValue(corsConfigurazione.getStato());
			cors_tipo = DriverConfigurazioneDB_LIB.getValue(corsConfigurazione.getTipo());
			cors_all_allow_origins = DriverConfigurazioneDB_LIB.getValue(corsConfigurazione.getAccessControlAllAllowOrigins());
			cors_all_allow_methods = DriverConfigurazioneDB_LIB.getValue(corsConfigurazione.getAccessControlAllAllowMethods());
			cors_all_allow_headers = DriverConfigurazioneDB_LIB.getValue(corsConfigurazione.getAccessControlAllAllowHeaders());
			cors_allow_credentials = DriverConfigurazioneDB_LIB.getValue(corsConfigurazione.getAccessControlAllowCredentials());
			if(corsConfigurazione.getAccessControlMaxAge()!=null) {
				cors_allow_max_age = CostantiDB.TRUE;
				cors_allow_max_age_seconds = corsConfigurazione.getAccessControlMaxAge();	
			}
			if(corsConfigurazione.getAccessControlAllowOrigins()!=null && corsConfigurazione.getAccessControlAllowOrigins().sizeOriginList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < corsConfigurazione.getAccessControlAllowOrigins().sizeOriginList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(corsConfigurazione.getAccessControlAllowOrigins().getOrigin(i));
				}
				cors_allow_origins = bf.toString();
			}
			if(corsConfigurazione.getAccessControlAllowHeaders()!=null && corsConfigurazione.getAccessControlAllowHeaders().sizeHeaderList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < corsConfigurazione.getAccessControlAllowHeaders().sizeHeaderList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(corsConfigurazione.getAccessControlAllowHeaders().getHeader(i));
				}
				cors_allow_headers = bf.toString();
			}
			if(corsConfigurazione.getAccessControlAllowMethods()!=null && corsConfigurazione.getAccessControlAllowMethods().sizeMethodList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < corsConfigurazione.getAccessControlAllowMethods().sizeMethodList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(corsConfigurazione.getAccessControlAllowMethods().getMethod(i));
				}
				cors_allow_methods = bf.toString();
			}
			if(corsConfigurazione.getAccessControlExposeHeaders()!=null && corsConfigurazione.getAccessControlExposeHeaders().sizeHeaderList()>0) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < corsConfigurazione.getAccessControlExposeHeaders().sizeHeaderList(); i++) {
					if(i>0) {
						bf.append(",");
					}
					bf.append(corsConfigurazione.getAccessControlExposeHeaders().getHeader(i));
				}
				cors_allow_expose_headers = bf.toString();
			}
		}
		
		ResponseCachingConfigurazione responseCachingConfigurazone = aPD.getResponseCaching();
		String response_cache_stato = null;
		Integer response_cache_seconds = null;
		Long response_cache_max_msg_size = null;
		String response_cache_hash_url = null;
		String response_cache_hash_query = null;
		String response_cache_hash_query_list = null;
		String response_cache_hash_headers = null;
		String response_cache_hash_headers_list = null;
		String response_cache_hash_payload = null;
		boolean response_cache_noCache = true;
		boolean response_cache_maxAge = true;
		boolean response_cache_noStore = true;
		List<ResponseCachingConfigurazioneRegola> response_cache_regole = null;
		if(responseCachingConfigurazone!=null) {
			response_cache_stato = DriverConfigurazioneDB_LIB.getValue(responseCachingConfigurazone.getStato());
			response_cache_seconds = responseCachingConfigurazone.getCacheTimeoutSeconds();
			response_cache_max_msg_size = responseCachingConfigurazone.getMaxMessageSize();
			if(responseCachingConfigurazone.getControl()!=null) {
				response_cache_noCache = responseCachingConfigurazone.getControl().getNoCache();
				response_cache_maxAge = responseCachingConfigurazone.getControl().getMaxAge();
				response_cache_noStore = responseCachingConfigurazone.getControl().getNoStore();
			}
			if(responseCachingConfigurazone.getHashGenerator()!=null) {
				response_cache_hash_url = DriverConfigurazioneDB_LIB.getValue(responseCachingConfigurazone.getHashGenerator().getRequestUri());
				
				response_cache_hash_query = DriverConfigurazioneDB_LIB.getValue(responseCachingConfigurazone.getHashGenerator().getQueryParameters());
				if(StatoFunzionalitaCacheDigestQueryParameter.SELEZIONE_PUNTUALE.equals(responseCachingConfigurazone.getHashGenerator().getQueryParameters())) {
					if(responseCachingConfigurazone.getHashGenerator().getQueryParameterList()!=null && responseCachingConfigurazone.getHashGenerator().sizeQueryParameterList()>0) {
						StringBuilder bf = new StringBuilder();
						for (int i = 0; i < responseCachingConfigurazone.getHashGenerator().sizeQueryParameterList(); i++) {
							if(i>0) {
								bf.append(",");
							}
							bf.append(responseCachingConfigurazone.getHashGenerator().getQueryParameter(i));
						}
						response_cache_hash_query_list = bf.toString();
					}
				}
				
				response_cache_hash_headers = DriverConfigurazioneDB_LIB.getValue(responseCachingConfigurazone.getHashGenerator().getHeaders());
				if(StatoFunzionalita.ABILITATO.equals(responseCachingConfigurazone.getHashGenerator().getHeaders())) {
					if(responseCachingConfigurazone.getHashGenerator().getHeaderList()!=null && responseCachingConfigurazone.getHashGenerator().sizeHeaderList()>0) {
						StringBuilder bf = new StringBuilder();
						for (int i = 0; i < responseCachingConfigurazone.getHashGenerator().sizeHeaderList(); i++) {
							if(i>0) {
								bf.append(",");
							}
							bf.append(responseCachingConfigurazone.getHashGenerator().getHeader(i));
						}
						response_cache_hash_headers_list = bf.toString();
					}
				}
				
				response_cache_hash_payload = DriverConfigurazioneDB_LIB.getValue(responseCachingConfigurazone.getHashGenerator().getPayload());
			}
			response_cache_regole = responseCachingConfigurazone.getRegolaList();
		}
		
		ConfigurazionePortaHandler configHandlers = aPD.getConfigurazioneHandler();
		
		ExtendedInfoManager extInfoManager = ExtendedInfoManager.getInstance();
		IExtendedInfo extInfoConfigurazioneDriver = extInfoManager.newInstanceExtendedInfoPortaDelegata();
		
		try {
			int n = 0;
			int i = 0;
			switch (type) {
			case CREATE:

				//soggetto erogatore
				String tipoSoggErogatore = (soggErogatore != null ? soggErogatore.getTipo() : null);
				String nomeSoggErogatore = (soggErogatore != null ? soggErogatore.getNome() : null);
				if(tipoSoggErogatore==null || tipoSoggErogatore.equals("")) throw new DriverConfigurazioneException("Tipo Soggetto Erogatore non impostato.");
				if(nomeSoggErogatore==null || nomeSoggErogatore.equals("")) throw new DriverConfigurazioneException("Nome Soggetto Erogatore non impostato.");

				//servizio
				String tipoServizio = (servizio != null ? servizio.getTipo() : null);
				String nomeServizio = (servizio != null ? servizio.getNome() : null);
				Integer versioneServizio = (servizio != null ? servizio.getVersione() : null);
				if(tipoServizio==null || tipoServizio.equals("")) throw new DriverConfigurazioneException("Tipo Servizio non impostato.");
				if(nomeServizio==null || nomeServizio.equals("")) throw new DriverConfigurazioneException("Nome Servizio non impostato.");
				if(versioneServizio==null) throw new DriverConfigurazioneException("Versione Servizio non impostato.");

				//Azione
				String nomeAzione = (azione != null ? azione.getNome() : null);
				String patternAzione = (azione != null ? azione.getPattern() : null);
				String nomePortaDeleganteAzione = (azione != null ? azione.getNomePortaDelegante() : null);
				StatoFunzionalita forceInterfaceBased = (azione != null ? azione.getForceInterfaceBased() : null);
				PortaDelegataAzioneIdentificazione modeAzione = (azione != null ? azione.getIdentificazione() : null);
				//Se il bean Azione nn e' presente allora non controllo nulla
				if(azione!=null){
					if(modeAzione==null || modeAzione.equals("")) 
						modeAzione = PortaDelegataAzioneIdentificazione.STATIC;
					switch (modeAzione) {
					case CONTENT_BASED:
					case URL_BASED:
					case HEADER_BASED:
						if(patternAzione==null || patternAzione.equals("")) throw new DriverConfigurazioneException("Pattern Azione non impostato.");
						nomeAzione=null;
						break;
					case DELEGATED_BY:
						if(nomePortaDeleganteAzione==null || nomePortaDeleganteAzione.equals("")) throw new DriverConfigurazioneException("Nome Porta Delegante Azione non impostata.");
						nomeAzione=null;
						break;
					case INPUT_BASED:
					case SOAP_ACTION_BASED:
					case INTERFACE_BASED:
						//nessun campo obbligatorio
						break;
					case STATIC:
						//se non c'e' l'id dell'azione ci deve essere il nome
						if(idAzione<=0){
							if(nomeAzione==null || nomeAzione.equals("")) throw new DriverConfigurazioneException("Nome Azione non impostato.");
						}
						patternAzione=null;
						break;
					default:
						break;
					}
				}

				// create
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addInsertField("nome_porta", "?");
				sqlQueryObject.addInsertField("descrizione", "?");
				sqlQueryObject.addInsertField("id_soggetto_erogatore", "?");
				sqlQueryObject.addInsertField("tipo_soggetto_erogatore", "?");
				sqlQueryObject.addInsertField("nome_soggetto_erogatore", "?");
				sqlQueryObject.addInsertField("id_servizio", "?");
				sqlQueryObject.addInsertField("tipo_servizio", "?");
				sqlQueryObject.addInsertField("nome_servizio", "?");
				sqlQueryObject.addInsertField("versione_servizio", "?");
				sqlQueryObject.addInsertField("id_azione", "?");
				sqlQueryObject.addInsertField("nome_azione", "?");
				sqlQueryObject.addInsertField("mode_azione", "?");
				sqlQueryObject.addInsertField("pattern_azione", "?");
				sqlQueryObject.addInsertField("nome_porta_delegante_azione", "?");
				sqlQueryObject.addInsertField("force_interface_based_azione", "?");
				sqlQueryObject.addInsertField("autenticazione", "?");
				sqlQueryObject.addInsertField("autenticazione_opzionale", "?");
				sqlQueryObject.addInsertField("token_policy", "?");
				sqlQueryObject.addInsertField("token_opzionale", "?");
				sqlQueryObject.addInsertField("token_validazione", "?");
				sqlQueryObject.addInsertField("token_introspection", "?");
				sqlQueryObject.addInsertField("token_user_info", "?");
				sqlQueryObject.addInsertField("token_forward", "?");
				sqlQueryObject.addInsertField("token_options", "?");
				sqlQueryObject.addInsertField("token_authn_issuer", "?");
				sqlQueryObject.addInsertField("token_authn_client_id", "?");
				sqlQueryObject.addInsertField("token_authn_subject", "?");
				sqlQueryObject.addInsertField("token_authn_username", "?");
				sqlQueryObject.addInsertField("token_authn_email", "?");
				sqlQueryObject.addInsertField("autorizzazione", "?");
				sqlQueryObject.addInsertField("autorizzazione_xacml", "?");
				sqlQueryObject.addInsertField("autorizzazione_contenuto", "?");
				sqlQueryObject.addInsertField("mtom_request_mode", "?");
				sqlQueryObject.addInsertField("mtom_response_mode", "?");
				sqlQueryObject.addInsertField("security", "?");
				sqlQueryObject.addInsertField("security_mtom_req", "?");
				sqlQueryObject.addInsertField("security_mtom_res", "?");
				sqlQueryObject.addInsertField("security_request_mode", "?");
				sqlQueryObject.addInsertField("security_response_mode", "?");
				sqlQueryObject.addInsertField("id_soggetto", "?");
				sqlQueryObject.addInsertField("ricevuta_asincrona_sim", "?");
				sqlQueryObject.addInsertField("ricevuta_asincrona_asim", "?");
				sqlQueryObject.addInsertField("integrazione", "?");
				sqlQueryObject.addInsertField("scadenza_correlazione_appl", "?");
				sqlQueryObject.addInsertField("validazione_contenuti_stato", "?");
				sqlQueryObject.addInsertField("validazione_contenuti_tipo", "?");
				sqlQueryObject.addInsertField("validazione_contenuti_mtom", "?");
				sqlQueryObject.addInsertField("allega_body", "?");
				sqlQueryObject.addInsertField("scarta_body", "?");
				sqlQueryObject.addInsertField("gestione_manifest", "?");
				sqlQueryObject.addInsertField("stateless", "?");
				sqlQueryObject.addInsertField("local_forward", "?");
				sqlQueryObject.addInsertField("local_forward_pa", "?");
				sqlQueryObject.addInsertField("ruoli_match", "?");
				sqlQueryObject.addInsertField("token_sa_stato", "?");
				sqlQueryObject.addInsertField("token_ruoli_stato", "?");
				sqlQueryObject.addInsertField("token_ruoli_match", "?");
				sqlQueryObject.addInsertField("token_ruoli_tipologia", "?");
				sqlQueryObject.addInsertField("scope_stato", "?");
				sqlQueryObject.addInsertField("scope_match", "?");
				sqlQueryObject.addInsertField("ricerca_porta_azione_delegata", "?");
				sqlQueryObject.addInsertField("msg_diag_severita", "?");
				sqlQueryObject.addInsertField("tracciamento_esiti", "?");
				sqlQueryObject.addInsertField("stato", "?");
				// cors
				sqlQueryObject.addInsertField("cors_stato", "?");
				sqlQueryObject.addInsertField("cors_tipo", "?");
				sqlQueryObject.addInsertField("cors_all_allow_origins", "?");
				sqlQueryObject.addInsertField("cors_all_allow_methods", "?");
				sqlQueryObject.addInsertField("cors_all_allow_headers", "?");
				sqlQueryObject.addInsertField("cors_allow_credentials", "?");
				sqlQueryObject.addInsertField("cors_allow_max_age", "?");
				sqlQueryObject.addInsertField("cors_allow_max_age_seconds", "?");
				sqlQueryObject.addInsertField("cors_allow_origins", "?");
				sqlQueryObject.addInsertField("cors_allow_headers", "?");
				sqlQueryObject.addInsertField("cors_allow_methods", "?");
				sqlQueryObject.addInsertField("cors_allow_expose_headers", "?");
				// responseCaching
				sqlQueryObject.addInsertField("response_cache_stato", "?");
				sqlQueryObject.addInsertField("response_cache_seconds", "?");
				sqlQueryObject.addInsertField("response_cache_max_msg_size", "?");
				sqlQueryObject.addInsertField("response_cache_control_nocache", "?");
				sqlQueryObject.addInsertField("response_cache_control_maxage", "?");
				sqlQueryObject.addInsertField("response_cache_control_nostore", "?");
				sqlQueryObject.addInsertField("response_cache_hash_url", "?");
				sqlQueryObject.addInsertField("response_cache_hash_query", "?");
				sqlQueryObject.addInsertField("response_cache_hash_query_list", "?");
				sqlQueryObject.addInsertField("response_cache_hash_headers", "?");
				sqlQueryObject.addInsertField("response_cache_hash_hdr_list", "?");
				sqlQueryObject.addInsertField("response_cache_hash_payload", "?");
				// id
				sqlQueryObject.addInsertField("id_accordo", "?");
				sqlQueryObject.addInsertField("id_port_type", "?");
				// options
				sqlQueryObject.addInsertField("options", "?");
				// options
				sqlQueryObject.addInsertField("canale", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);
				int index = 1;
				stm.setString(index++, nomePorta);
				stm.setString(index++, descrizione);
				stm.setLong(index++, idSoggettoErogatore);				
				stm.setString(index++, tipoSoggErogatore);
				stm.setString(index++, nomeSoggErogatore);
				stm.setLong(index++, idServizioPD);
				stm.setString(index++, tipoServizio);
				stm.setString(index++, nomeServizio);
				stm.setInt(index++, versioneServizio);
				stm.setLong(index++, idAzione);
				stm.setString(index++, nomeAzione);
				if(modeAzione!=null){
					stm.setString(index++, modeAzione.toString());
				}
				else{
					stm.setString(index++, null);
				}
				stm.setString(index++, patternAzione);
				stm.setString(index++, nomePortaDeleganteAzione);
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(forceInterfaceBased));
				// autenticazione
				stm.setString(index++, autenticazione);
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getAutenticazioneOpzionale()) : null);
				// token
				stm.setString(index++, gestioneToken!=null ? gestioneToken.getPolicy() : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getTokenOpzionale()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getValidazione()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getIntrospection()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getUserInfo()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getForward()) : null);
				stm.setString(index++, gestioneToken!=null ? gestioneToken.getOptions() : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getIssuer()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getClientId()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getSubject()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getUsername()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getEmail()) : null);
				// autorizzazione
				stm.setString(index++, autorizzazione);
				stm.setString(index++, autorizzazioneXacmlPolicy);
				stm.setString(index++, autorizzazioneContenuto);
				// mtom
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(mtomMode_request));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(mtomMode_response));
				// messageSecurity
				stm.setString(index++, messageSecurityStatus);
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(messageSecurityApplyMtom_request));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(messageSecurityApplyMtom_response));
				stm.setString(index++, securityRequestMode);
				stm.setString(index++, securityResponseMode);
				// proprietario
				stm.setLong(index++, idSoggettoProprietario);
				//ricevuta asincrona_asimmetrica/simmetrica
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(aPD.getRicevutaAsincronaSimmetrica()));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(aPD.getRicevutaAsincronaAsimmetrica()));				
				//integrazione
				stm.setString(index++, aPD.getIntegrazione());
				//correlazione applicativa scadenza
				stm.setString(index++, aPD.getCorrelazioneApplicativa()!=null ? aPD.getCorrelazioneApplicativa().getScadenza() : null);
				//validazione xsd
				stm.setString(index++, aPD.getValidazioneContenutiApplicativi()!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getValidazioneContenutiApplicativi().getStato()) : null);
				stm.setString(index++, aPD.getValidazioneContenutiApplicativi()!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getValidazioneContenutiApplicativi().getTipo()) : null);
				stm.setString(index++, aPD.getValidazioneContenutiApplicativi()!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getValidazioneContenutiApplicativi().getAcceptMtomMessage()) : null);

				// InvocazionePorta: funzionalita' attachment
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getAllegaBody()) : null);
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getScartaBody()) : null);
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getGestioneManifest()) : null);
				
				// Stateless
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getStateless()) : null);
				
				// LocalForward
				stm.setString(index++, aPD!=null && aPD.getLocalForward()!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getLocalForward().getStato()) : null);
				stm.setString(index++, aPD!=null && aPD.getLocalForward()!=null ? aPD.getLocalForward().getPortaApplicativa() : null);
				
				// Ruoli
				stm.setString(index++, aPD!=null && aPD.getRuoli()!=null && aPD.getRuoli().getMatch()!=null ? 
						aPD.getRuoli().getMatch().getValue() : null);
				
				// Token sa
				stm.setString(index++, aPD!=null && aPD.getAutorizzazioneToken()!=null && aPD.getAutorizzazioneToken().getAutorizzazioneApplicativi()!=null ? 
						DriverConfigurazioneDB_LIB.getValue(aPD.getAutorizzazioneToken().getAutorizzazioneApplicativi()) : null);
				
				// Token ruoli
				stm.setString(index++, aPD!=null && aPD.getAutorizzazioneToken()!=null && aPD.getAutorizzazioneToken().getAutorizzazioneRuoli()!=null ? 
						DriverConfigurazioneDB_LIB.getValue(aPD.getAutorizzazioneToken().getAutorizzazioneRuoli()) : null);
				stm.setString(index++, aPD!=null && aPD.getAutorizzazioneToken()!=null && aPD.getAutorizzazioneToken().getRuoli()!=null && 
						aPD.getAutorizzazioneToken().getRuoli().getMatch()!=null ? 
						aPD.getAutorizzazioneToken().getRuoli().getMatch().getValue() : null);
				stm.setString(index++, aPD!=null && aPD.getAutorizzazioneToken()!=null && aPD.getAutorizzazioneToken().getTipologiaRuoli()!=null ? 
						aPD.getAutorizzazioneToken().getTipologiaRuoli().getValue() : null);
				
				// Scope
				stm.setString(index++, aPD!=null && aPD.getScope()!=null && aPD.getScope().getStato()!=null ? 
						DriverConfigurazioneDB_LIB.getValue(aPD.getScope().getStato()) : null);
				stm.setString(index++, aPD!=null && aPD.getScope()!=null && aPD.getScope().getMatch()!=null ? 
						aPD.getScope().getMatch().getValue() : null);
				
				// Ricerca Porta Azione Delegata
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getRicercaPortaAzioneDelegata()) : null);
				
				// Tracciamento
				stm.setString(index++, msg_diag_severita);
				stm.setString(index++, tracciamento_esiti);
				
				// Stato
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getStato()) : null);
				
				// cors
				stm.setString(index++, cors_stato);
				stm.setString(index++, cors_tipo);
				stm.setString(index++, cors_all_allow_origins);
				stm.setString(index++, cors_all_allow_methods);
				stm.setString(index++, cors_all_allow_headers);
				stm.setString(index++, cors_allow_credentials);
				stm.setInt(index++, cors_allow_max_age);
				if(cors_allow_max_age_seconds!=null) {
					stm.setInt(index++, cors_allow_max_age_seconds);
				}
				else {
					stm.setNull(index++, java.sql.Types.INTEGER);
				}
				stm.setString(index++, cors_allow_origins);
				stm.setString(index++, cors_allow_headers);
				stm.setString(index++, cors_allow_methods);
				stm.setString(index++, cors_allow_expose_headers);
				
				// responseCaching
				stm.setString(index++, response_cache_stato);
				if(response_cache_seconds!=null) {
					stm.setInt(index++, response_cache_seconds);
				}
				else {
					stm.setNull(index++, java.sql.Types.INTEGER);
				}
				if(response_cache_max_msg_size!=null) {
					stm.setLong(index++, response_cache_max_msg_size);
				}
				else {
					stm.setNull(index++, java.sql.Types.BIGINT);
				}
				stm.setInt(index++, response_cache_noCache ? CostantiDB.TRUE : CostantiDB.FALSE);
				stm.setInt(index++, response_cache_maxAge ? CostantiDB.TRUE : CostantiDB.FALSE);
				stm.setInt(index++, response_cache_noStore ? CostantiDB.TRUE : CostantiDB.FALSE);
				stm.setString(index++, response_cache_hash_url);
				stm.setString(index++, response_cache_hash_query);
				stm.setString(index++, response_cache_hash_query_list);
				stm.setString(index++, response_cache_hash_headers);
				stm.setString(index++, response_cache_hash_headers_list);
				stm.setString(index++, response_cache_hash_payload);
				
				//idaccordo
				stm.setLong(index++, aPD.getIdAccordo()!=null ? aPD.getIdAccordo() : -1L);
				stm.setLong(index++, aPD.getIdPortType() !=null ? aPD.getIdPortType() : -1L);
				
				// options
				stm.setString(index++, aPD.getOptions());
				
				// canale
				stm.setString(index++, aPD.getCanale());
				
				DriverConfigurazioneDB_LIB.log.debug("eseguo query: " + 
						DBUtils.formatSQLString(sqlQuery, nomePorta, descrizione, 
								idSoggettoErogatore, tipoSoggErogatore, nomeSoggErogatore, 
								idServizioPD, tipoServizio, nomeServizio, 
								idAzione, nomeAzione, modeAzione, patternAzione, nomePortaDeleganteAzione, DriverConfigurazioneDB_LIB.getValue(forceInterfaceBased),
								autenticazione, aPD.getAutenticazioneOpzionale(),
								(gestioneToken!=null ? gestioneToken.getPolicy() : null),
								(gestioneToken!=null ? gestioneToken.getTokenOpzionale() : null),
								(gestioneToken!=null ? gestioneToken.getValidazione() : null),
								(gestioneToken!=null ? gestioneToken.getIntrospection() : null),
								(gestioneToken!=null ? gestioneToken.getUserInfo() : null),
								(gestioneToken!=null ? gestioneToken.getForward() : null),
								(gestioneToken!=null ? gestioneToken.getOptions() : null),
								((gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? gestioneToken.getAutenticazione().getIssuer() : null),
								((gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? gestioneToken.getAutenticazione().getClientId() : null),
								((gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? gestioneToken.getAutenticazione().getSubject() : null),
								((gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? gestioneToken.getAutenticazione().getUsername() : null),
								((gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? gestioneToken.getAutenticazione().getEmail() : null),
								autorizzazione, autorizzazioneXacmlPolicy, autorizzazioneContenuto,
								mtomMode_request, mtomMode_response,
								messageSecurityStatus, messageSecurityApplyMtom_request, messageSecurityApplyMtom_response, securityRequestMode, securityResponseMode,
								idSoggettoProprietario,
								aPD.getRicevutaAsincronaSimmetrica(),aPD.getRicevutaAsincronaAsimmetrica(),aPD.getIntegrazione(),
								(aPD.getCorrelazioneApplicativa()!=null ? aPD.getCorrelazioneApplicativa().getScadenza() : null),
								(aPD.getValidazioneContenutiApplicativi()!=null ? aPD.getValidazioneContenutiApplicativi().getStato() : null),
								(aPD.getValidazioneContenutiApplicativi()!=null ? aPD.getValidazioneContenutiApplicativi().getTipo() : null),
								(aPD.getValidazioneContenutiApplicativi()!=null ? aPD.getValidazioneContenutiApplicativi().getAcceptMtomMessage() : null),
								aPD.getAllegaBody(),aPD.getScartaBody(),aPD.getGestioneManifest(),aPD.getStateless(),aPD.getLocalForward(),
								(aPD!=null && aPD.getRuoli()!=null && aPD.getRuoli().getMatch()!=null ? 
										aPD.getRuoli().getMatch().getValue() : null),
								(aPD!=null && aPD.getAutorizzazioneToken()!=null && aPD.getAutorizzazioneToken().getAutorizzazioneApplicativi()!=null ? 
										DriverConfigurazioneDB_LIB.getValue(aPD.getAutorizzazioneToken().getAutorizzazioneApplicativi()) : null),
								(aPD!=null && aPD.getAutorizzazioneToken()!=null && aPD.getAutorizzazioneToken().getAutorizzazioneRuoli()!=null ? 
										DriverConfigurazioneDB_LIB.getValue(aPD.getAutorizzazioneToken().getAutorizzazioneRuoli()) : null),
								(aPD!=null && aPD.getAutorizzazioneToken()!=null && aPD.getAutorizzazioneToken().getRuoli()!=null && 
									aPD.getAutorizzazioneToken().getRuoli().getMatch()!=null ? 
										aPD.getAutorizzazioneToken().getRuoli().getMatch().getValue() : null),
								(aPD.getAutorizzazioneToken()!=null && aPD.getAutorizzazioneToken().getTipologiaRuoli()!=null ? 
										aPD.getAutorizzazioneToken().getTipologiaRuoli().getValue() : null),
								(aPD!=null && aPD.getScope()!=null && aPD.getScope().getStato()!=null ? 
										aPD.getScope().getStato() : null),
								(aPD!=null && aPD.getScope()!=null && aPD.getScope().getMatch()!=null ? 
										aPD.getScope().getMatch().getValue() : null),
								aPD.getRicercaPortaAzioneDelegata(),
								msg_diag_severita,tracciamento_esiti,
								aPD.getStato(),
								cors_stato, cors_tipo, cors_all_allow_origins, cors_all_allow_methods, cors_all_allow_headers, cors_allow_credentials, cors_allow_max_age, cors_allow_max_age_seconds,
								cors_allow_origins, cors_allow_headers, cors_allow_methods, cors_allow_expose_headers,
								response_cache_stato, response_cache_seconds, response_cache_max_msg_size, 
								(response_cache_noCache ? CostantiDB.TRUE : CostantiDB.FALSE),
								(response_cache_maxAge ? CostantiDB.TRUE : CostantiDB.FALSE),
								(response_cache_noStore ? CostantiDB.TRUE : CostantiDB.FALSE),
								response_cache_hash_url, response_cache_hash_query, response_cache_hash_query_list, response_cache_hash_headers, response_cache_hash_headers_list, response_cache_hash_payload,
								aPD.getIdAccordo(),aPD.getIdPortType(),
								aPD.getOptions(),
								aPD.getCanale()));
				n = stm.executeUpdate();
				stm.close();

				DriverConfigurazioneDB_LIB.log.debug("Inserted " + n + " row(s).");

				// recupero l'id della porta delegata appena inserita
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.addWhereCondition("nome_porta = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idSoggettoProprietario);
				stm.setString(2, nomePorta);

				rs = stm.executeQuery();

				if (rs.next()) {
					idPortaDelegata = rs.getLong("id");
					aPD.setId(idPortaDelegata);
					rs.close();
					stm.close();
				} else {
					rs.close();
					stm.close();
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaDelegata(CREATE)] Impossibile recuperare l'ID della PortaDelegata appena create.");
				}

				if(mtomProcessor!=null){
					
					MtomProcessorFlowParameter reqParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_MTOM_REQUEST);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("content_type", "?");
					sqlQueryObject.addInsertField("required", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					i = 0;
					if(mtomProcessor.getRequestFlow()!=null){
						MtomProcessorFlow flow = mtomProcessor.getRequestFlow();
						for (i = 0; i < flow.sizeParameterList(); i++) {
							reqParam = flow.getParameter(i);
							stm.setLong(1, idPortaDelegata);
							stm.setString(2, reqParam.getNome());
							stm.setString(3, reqParam.getPattern());
							stm.setString(4, reqParam.getContentType());
							stm.setInt(5, reqParam.getRequired() ? CostantiDB.TRUE : CostantiDB.FALSE);

							stm.executeUpdate();
						}	
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " mtom request flow con id=" + idPortaDelegata);

					MtomProcessorFlowParameter resParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_MTOM_RESPONSE);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("content_type", "?");
					sqlQueryObject.addInsertField("required", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
					
					i = 0;
					if(mtomProcessor.getResponseFlow()!=null){
						MtomProcessorFlow flow = mtomProcessor.getResponseFlow();
						for (i = 0; i < flow.sizeParameterList(); i++) {
							resParam = flow.getParameter(i);
							stm.setLong(1, idPortaDelegata);
							stm.setString(2, resParam.getNome());
							stm.setString(3, resParam.getPattern());
							stm.setString(4, resParam.getContentType());
							stm.setInt(5, resParam.getRequired() ? CostantiDB.TRUE : CostantiDB.FALSE);
	
							stm.executeUpdate();
						}
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " mtom response flow con id=" + idPortaDelegata);
					
				}
				
				// se security abilitato setto la lista
				//if ((messageSecurity != null) && CostantiConfigurazione.ABILITATO.toString().equals(messageSecurityStatus) )  {
				// Devo settarli sempre se ci sono, in modo che lo switch abilitato-disabilitato funzioni
				if ((messageSecurity != null) )  {
					MessageSecurityFlowParameter reqParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					i = 0;
					if(messageSecurity.getRequestFlow()!=null){
						MessageSecurityFlow flow = messageSecurity.getRequestFlow();
						for (i = 0; i < flow.sizeParameterList(); i++) {
							reqParam = flow.getParameter(i);
							stm.setLong(1, idPortaDelegata);
							stm.setString(2, reqParam.getNome());
							stm.setString(3, reqParam.getValore());

							stm.executeUpdate();
						}	
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " request flow con id=" + idPortaDelegata);

					MessageSecurityFlowParameter resParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
					
					i = 0;
					if(messageSecurity.getResponseFlow()!=null){
						MessageSecurityFlow flow = messageSecurity.getResponseFlow();
						for (i = 0; i < flow.sizeParameterList(); i++) {
							resParam = flow.getParameter(i);
							stm.setLong(1, idPortaDelegata);
							stm.setString(2, resParam.getNome());
							stm.setString(3, resParam.getValore());
	
							stm.executeUpdate();
						}
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " response flow con id=" + idPortaDelegata);
				}

				// la lista di correlazioni applicative contiene tutti e soli gli elementi necessari quindi resetto la lista nel db e riscrivo la lista nuova
				if (corrApp != null) {

					//inserisco i valori presenti nella lista 
					CorrelazioneApplicativaElemento cae = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome_elemento", "?");
					sqlQueryObject.addInsertField("mode_correlazione", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("identificazione_fallita", "?");
					sqlQueryObject.addInsertField("riuso_id", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					for (i = 0; i < corrApp.sizeElementoList(); i++) {
						cae = corrApp.getElemento(i);
						stm.setLong(1, idPortaDelegata);
						stm.setString(2, cae.getNome());
						stm.setString(3, DriverConfigurazioneDB_LIB.getValue(cae.getIdentificazione()));
						if (cae.getPattern() != null)
							stm.setString(4, cae.getPattern());
						else
							stm.setString(4, "");
						stm.setString(5, DriverConfigurazioneDB_LIB.getValue(cae.getIdentificazioneFallita()));
						stm.setString(6, DriverConfigurazioneDB_LIB.getValue(cae.getRiusoIdentificativo()));
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " correlazione applicativa con id=" + idPortaDelegata);
				}
				
				// la lista di correlazioni applicative contiene tutti e soli gli elementi necessari quindi resetto la lista nel db e riscrivo la lista nuova
				if (corrAppRisposta != null) {

					//inserisco i valori presenti nella lista 
					CorrelazioneApplicativaRispostaElemento cae = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE_RISPOSTA);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome_elemento", "?");
					sqlQueryObject.addInsertField("mode_correlazione", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("identificazione_fallita", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					for (i = 0; i < corrAppRisposta.sizeElementoList(); i++) {
						cae = corrAppRisposta.getElemento(i);
						stm.setLong(1, idPortaDelegata);
						stm.setString(2, cae.getNome());
						stm.setString(3, DriverConfigurazioneDB_LIB.getValue(cae.getIdentificazione()));
						if (cae.getPattern() != null)
							stm.setString(4, cae.getPattern());
						else
							stm.setString(4, "");
						stm.setString(5, DriverConfigurazioneDB_LIB.getValue(cae.getIdentificazioneFallita()));
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " correlazione applicativa risposta con id=" + idPortaDelegata);
				}
				
				// serviziapplicativi
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_SA);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);

				for (i = 0; i < aPD.sizeServizioApplicativoList(); i++) {
					PortaDelegataServizioApplicativo servizioApplicativo = aPD.getServizioApplicativo(i);
					String nomeSA = servizioApplicativo.getNome();
					//il tipo e il nome proprietario servizio applicativo sono gli stessi della porta delegata
					String nomeProprietarioSA = aPD.getNomeSoggettoProprietario(); //servizioApplicativo.getNomeSoggettoProprietario();
					String tipoProprietarioSA = aPD.getTipoSoggettoProprietario(); //servizioApplicativo.getTipoSoggettoProprietario();
					if (nomeSA == null || nomeSA.equals(""))
						throw new DriverConfigurazioneException("Nome del ServizioApplicativo associato non valido.");
					if (nomeProprietarioSA == null || nomeProprietarioSA.equals(""))
						throw new DriverConfigurazioneException("Nome Proprietario del ServizioApplicativo associato non valido.");
					if (tipoProprietarioSA == null || tipoProprietarioSA.equals(""))
						throw new DriverConfigurazioneException("Tipo Proprietario del ServizioApplicativo associato non valido.");

					long idSA = DriverConfigurazioneDB_serviziApplicativiLIB.getIdServizioApplicativo(nomeSA, tipoProprietarioSA, nomeProprietarioSA, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);

					if (idSA <= 0)
						throw new DriverConfigurazioneException("Impossibile recuperare l'id del Servizio Applicativo [" + nomeSA + "] di [" + tipoProprietarioSA + "/" + nomeProprietarioSA + "]");

					stm.setLong(1, idPortaDelegata);
					stm.setLong(2, idSA);
					stm.executeUpdate();
				}
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Inseriti " + i + " associazioni ServizioApplicativo<->PortaDelegata associati alla PortaDelegata[" + idPortaDelegata + "]");

				// set prop autenticazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("valore", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);
				for (i = 0; i < aPD.sizeProprietaAutenticazioneList(); i++) {
					Proprieta propProtocollo = aPD.getProprietaAutenticazione(i);
					stm.setLong(1, aPD.getId());
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
				}
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Inseriti " + i + " SetProtocolPropAutenticazione associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				// set prop autorizzazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("valore", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);
				for (i = 0; i < aPD.sizeProprietaAutorizzazioneList(); i++) {
					Proprieta propProtocollo = aPD.getProprietaAutorizzazione(i);
					stm.setLong(1, aPD.getId());
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
				}
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Inseriti " + i + " SetProtocolPropAutorizzazione associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				// set prop autorizzazione contenuto
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("valore", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);
				for (i = 0; i < aPD.sizeProprietaAutorizzazioneContenutoList(); i++) {
					Proprieta propProtocollo = aPD.getProprietaAutorizzazioneContenuto(i);
					stm.setLong(1, aPD.getId());
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
				}
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Inseriti " + i + " SetProtocolPropAutorizzazioneContenuto associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				
				// set prop rate limiting
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_RATE_LIMITING_PROP);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("valore", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);
				for (i = 0; i < aPD.sizeProprietaRateLimitingList(); i++) {
					Proprieta propProtocollo = aPD.getProprietaRateLimiting(i);
					stm.setLong(1, aPD.getId());
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
				}
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Inseriti " + i + " SetProtocolPropRateLimiting associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				
				
				
				// set prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_PROP);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("valore", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);
				for (i = 0; i < aPD.sizeProprietaList(); i++) {
					Proprieta propProtocollo = aPD.getProprieta(i);
					stm.setLong(1, aPD.getId());
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
				}
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Inseriti " + i + " SetProtocolProp associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				// Ruoli
				n=0;
				if(aPD.getRuoli()!=null && aPD.getRuoli().sizeRuoloList()>0){
					for (int j = 0; j < aPD.getRuoli().sizeRuoloList(); j++) {
						Ruolo ruolo = aPD.getRuoli().getRuolo(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_RUOLI);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("ruolo", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, aPD.getId());
						stm.setString(2, ruolo.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunto ruolo[" + ruolo.getNome() + "] alla PortaDelegata[" + idPortaDelegata + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunti " + n + " ruoli alla PortaDelegata[" + idPortaDelegata + "]");
				
				// Scope
				n=0;
				if(aPD.getScope()!=null && aPD.getScope().sizeScopeList()>0){
					for (int j = 0; j < aPD.getScope().sizeScopeList(); j++) {
						Scope scope = aPD.getScope().getScope(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_SCOPE);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("scope", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, aPD.getId());
						stm.setString(2, scope.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunto scope[" + scope.getNome() + "] alla PortaDelegata[" + idPortaDelegata + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunti " + n + " scope alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				// serviziapplicativi (token)
				if(aPD.getAutorizzazioneToken()!=null && aPD.getAutorizzazioneToken().getServiziApplicativi()!=null &&
						aPD.getAutorizzazioneToken().getServiziApplicativi().sizeServizioApplicativoList()>0) {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_TOKEN_SA);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
	
					for (i = 0; i < aPD.getAutorizzazioneToken().getServiziApplicativi().sizeServizioApplicativoList(); i++) {
						PortaDelegataServizioApplicativo servizioApplicativo = aPD.getAutorizzazioneToken().getServiziApplicativi().getServizioApplicativo(i);
						String nomeSA = servizioApplicativo.getNome();
						//il tipo e il nome proprietario servizio applicativo sono gli stessi della porta delegata
						String nomeProprietarioSA = aPD.getNomeSoggettoProprietario(); //servizioApplicativo.getNomeSoggettoProprietario();
						String tipoProprietarioSA = aPD.getTipoSoggettoProprietario(); //servizioApplicativo.getTipoSoggettoProprietario();
						if (nomeSA == null || nomeSA.equals(""))
							throw new DriverConfigurazioneException("Nome del ServizioApplicativo associato non valido.");
						if (nomeProprietarioSA == null || nomeProprietarioSA.equals(""))
							throw new DriverConfigurazioneException("Nome Proprietario del ServizioApplicativo associato non valido.");
						if (tipoProprietarioSA == null || tipoProprietarioSA.equals(""))
							throw new DriverConfigurazioneException("Tipo Proprietario del ServizioApplicativo associato non valido.");
	
						long idSA = DriverConfigurazioneDB_serviziApplicativiLIB.getIdServizioApplicativo(nomeSA, tipoProprietarioSA, nomeProprietarioSA, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
	
						if (idSA <= 0)
							throw new DriverConfigurazioneException("Impossibile recuperare l'id del Servizio Applicativo [" + nomeSA + "] di [" + tipoProprietarioSA + "/" + nomeProprietarioSA + "]");
	
						stm.setLong(1, idPortaDelegata);
						stm.setLong(2, idSA);
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inseriti " + i + " associazioni ServizioApplicativo<->PortaDelegata (token) associati alla PortaDelegata[" + idPortaDelegata + "]");
				}
				
				// Ruoli (Token)
				n=0;
				if(aPD.getAutorizzazioneToken()!=null && aPD.getAutorizzazioneToken().getRuoli()!=null &&
						aPD.getAutorizzazioneToken().getRuoli().sizeRuoloList()>0){
					for (int j = 0; j < aPD.getAutorizzazioneToken().getRuoli().sizeRuoloList(); j++) {
						Ruolo ruolo = aPD.getAutorizzazioneToken().getRuoli().getRuolo(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_TOKEN_RUOLI);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("ruolo", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, aPD.getId());
						stm.setString(2, ruolo.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunto ruolo[" + ruolo.getNome() + "] (token) alla PortaDelegata[" + idPortaDelegata + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunti " + n + " ruoli (token) alla PortaDelegata[" + idPortaDelegata + "]");
				
				// Azioni
				n=0;
				if(aPD.getAzione()!=null && aPD.getAzione().sizeAzioneDelegataList()>0){
					for (int j = 0; j < aPD.getAzione().sizeAzioneDelegataList(); j++) {
						String azioneDelegata = aPD.getAzione().getAzioneDelegata(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_AZIONI);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("azione", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, aPD.getId());
						stm.setString(2, azioneDelegata);
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunto azione delegata [" + azioneDelegata + "] alla PortaDelegata[" + idPortaDelegata + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunte " + n + " azioni delegate alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				// Cache Regole
				n=0;
				if(response_cache_regole!=null && response_cache_regole.size()>0){
					for (int j = 0; j < response_cache_regole.size(); j++) {
						ResponseCachingConfigurazioneRegola regola = response_cache_regole.get(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_CACHE_REGOLE);
						sqlQueryObject.addInsertField("id_porta", "?");
						if(regola.getReturnCodeMin()!=null && regola.getReturnCodeMin()>0) {
							sqlQueryObject.addInsertField("status_min", "?");
						}
						if(regola.getReturnCodeMax()!=null && regola.getReturnCodeMax()>0) {
							sqlQueryObject.addInsertField("status_max", "?");
						}
						sqlQueryObject.addInsertField("fault", "?");
						if(regola.getCacheTimeoutSeconds()!=null && regola.getCacheTimeoutSeconds()>0) {
							sqlQueryObject.addInsertField("cache_seconds", "?");
						}
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						int indexCR = 1;
						stm.setLong(indexCR++, aPD.getId());
						if(regola.getReturnCodeMin()!=null && regola.getReturnCodeMin()>0) {
							stm.setInt(indexCR++, regola.getReturnCodeMin());
						}
						if(regola.getReturnCodeMax()!=null && regola.getReturnCodeMax()>0) {
							stm.setInt(indexCR++, regola.getReturnCodeMax());
						}
						stm.setInt(indexCR++, regola.getFault() ? CostantiDB.TRUE : CostantiDB.FALSE);
						if(regola.getCacheTimeoutSeconds()!=null && regola.getCacheTimeoutSeconds()>0) {
							stm.setInt(indexCR++, regola.getCacheTimeoutSeconds());
						}
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunta regola di cache alla PortaDelegata[" + idPortaDelegata + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunte " + n + " regole di cache alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				// AttributeAuthority
				n=0;
				if(aPD.sizeAttributeAuthorityList()>0){
					for (int j = 0; j < aPD.sizeAttributeAuthorityList(); j++) {
						AttributeAuthority aa = aPD.getAttributeAuthority(j);
						
						String attributi = null;
						if(aa.sizeAttributoList()>0) {
							StringBuilder bf = new StringBuilder();
							for (int aaI = 0; aaI < aa.sizeAttributoList(); aaI++) {
								if(aaI>0) {
									bf.append(",");
								}
								bf.append(aa.getAttributo(aaI));
							}
							attributi = bf.toString();
						}
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_ATTRIBUTE_AUTHORITY);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("nome", "?");
						sqlQueryObject.addInsertField("attributi", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, aPD.getId());
						stm.setString(2, aa.getNome());
						stm.setString(3, attributi);
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunto A.A.[" + aa.getNome() + "] alla PortaDelegata[" + idPortaDelegata + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunte " + n + " A.A. alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				// dumpConfigurazione
				DriverConfigurazioneDB_dumpLIB.CRUDDumpConfigurazione(type, con, aPD.getDump(), aPD.getId(), CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_PD);
				
				// trasformazioni
				DriverConfigurazioneDB_trasformazioniLIB.CRUDTrasformazioni(type, con, aPD.getTrasformazioni(), aPD.getId(), true);
				
				// handlers
				if(configHandlers!=null) {
					if(configHandlers.getRequest()!=null) {
						DriverConfigurazioneDB_handlerLIB.CRUDConfigurazioneMessageHandlers(type, con, aPD.getId(), null, true, configHandlers.getRequest());
					}
					if(configHandlers.getResponse()!=null) {
						DriverConfigurazioneDB_handlerLIB.CRUDConfigurazioneMessageHandlers(type, con, aPD.getId(), null, false, configHandlers.getResponse());
					}
				}
				
				// extendedInfo
				i=0;
				if(aPD.sizeExtendedInfoList()>0){
					if(extInfoConfigurazioneDriver!=null){
						for (i = 0; i < aPD.sizeExtendedInfoList(); i++) {
							extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDB_LIB.log, aPD, aPD.getExtendedInfo(i), CRUDType.CREATE);
						}
					}
				}
				DriverConfigurazioneDB_LIB.log.debug("Inseriti " + i + " associazioni ExtendedInfo<->PortaDelegata associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				break;

			case UPDATE:
				//soggetto erogatore
				tipoSoggErogatore = (soggErogatore != null ? soggErogatore.getTipo() : null);
				nomeSoggErogatore = (soggErogatore != null ? soggErogatore.getNome() : null);
				if(tipoSoggErogatore==null || tipoSoggErogatore.equals("")) throw new DriverConfigurazioneException("Tipo Soggetto Erogatore non impostato.");
				if(nomeSoggErogatore==null || nomeSoggErogatore.equals("")) throw new DriverConfigurazioneException("Nome Soggetto Erogatore non impostato.");

				//servizio
				tipoServizio = (servizio != null ? servizio.getTipo() : null);
				nomeServizio = (servizio != null ? servizio.getNome() : null);
				versioneServizio = (servizio != null ? servizio.getVersione() : null);
				if(tipoServizio==null || tipoServizio.equals("")) throw new DriverConfigurazioneException("Tipo Servizio non impostato.");
				if(nomeServizio==null || nomeServizio.equals("")) throw new DriverConfigurazioneException("Nome Servizio non impostato.");
				if(versioneServizio==null) throw new DriverConfigurazioneException("Versione Servizio non impostato.");

				//Azione
				nomeAzione = (azione != null ? azione.getNome() : null);
				patternAzione = (azione != null ? azione.getPattern() : null);
				modeAzione = (azione != null ? azione.getIdentificazione() : null);
				nomePortaDeleganteAzione = (azione != null ? azione.getNomePortaDelegante() : null);
				forceInterfaceBased = (azione != null ? azione.getForceInterfaceBased() : null);
				//Se il bean Azione nn e' presente allora non controllo nulla
				if(azione!=null){
					if(modeAzione==null || modeAzione.equals("")) 
						modeAzione = PortaDelegataAzioneIdentificazione.STATIC;
					switch (modeAzione) {
					case CONTENT_BASED:
					case URL_BASED:
					case HEADER_BASED:
						if(patternAzione==null || patternAzione.equals("")) throw new DriverConfigurazioneException("Pattern Azione non impostato.");
						nomeAzione=null;
						break;
					case DELEGATED_BY:
						if(nomePortaDeleganteAzione==null || nomePortaDeleganteAzione.equals("")) throw new DriverConfigurazioneException("Nome Porta Delegante Azione non impostata.");
						nomeAzione=null;
						break;
					case INPUT_BASED:
					case SOAP_ACTION_BASED:
					case INTERFACE_BASED:
						//nessun campo obbligatorio
						break;
					case STATIC:
						//se non c'e' l'id dell'azione ci deve essere il nome
						if(idAzione<=0){
							if(nomeAzione==null || nomeAzione.equals("")) throw new DriverConfigurazioneException("Nome Azione non impostato.");
						}
						patternAzione=null;
						break;
					default:
						break;
					}
				}

				// update
				String oldNomePD = null;
				if(aPD.getOldIDPortaDelegataForUpdate()!=null){
					oldNomePD = aPD.getOldIDPortaDelegataForUpdate().getNome();
				}
				DriverConfigurazioneDB_LIB.log.debug("OLD-PD["+oldNomePD+"] PD["+nomePorta+"]");
				
				if (oldNomePD == null || oldNomePD.equals("")){
					DriverConfigurazioneDB_LIB.log.debug("old nomePD is null, assegno: "+nomePorta);
					oldNomePD = nomePorta;
				}

				// if(aPD.getId() == null || aPD.getId()<=0) throw new
				// DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaDelegata(UPDATE)]
				// L'id della porta e' necessario per effettuare l'UPDATE.");
				idPortaDelegata = DBUtils.getIdPortaDelegata(oldNomePD, con, DriverConfigurazioneDB_LIB.tipoDB);
				// Puo' darsi che l'old soggetto e il nuovo soggetto siano la stesso soggetto della tabella. E' stato cambiato il nome.
				if(idPortaDelegata<=0) {
					idPortaDelegata = DBUtils.getIdPortaDelegata(oldNomePD, con, DriverConfigurazioneDB_LIB.tipoDB);
				}
				if(idPortaDelegata<=0) 
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaDelegata(UPDATE)] id porta delegata non trovato nome["+oldNomePD+"]");
				aPD.setId(idPortaDelegata);
				
				
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addUpdateField("nome_porta", "?");
				sqlQueryObject.addUpdateField("descrizione", "?");
				sqlQueryObject.addUpdateField("id_soggetto_erogatore", "?");
				sqlQueryObject.addUpdateField("tipo_soggetto_erogatore", "?");
				sqlQueryObject.addUpdateField("nome_soggetto_erogatore", "?");
				sqlQueryObject.addUpdateField("id_servizio", "?");
				sqlQueryObject.addUpdateField("tipo_servizio", "?");
				sqlQueryObject.addUpdateField("nome_servizio", "?");
				sqlQueryObject.addUpdateField("versione_servizio", "?");
				sqlQueryObject.addUpdateField("id_azione", "?");
				sqlQueryObject.addUpdateField("nome_azione", "?");
				sqlQueryObject.addUpdateField("mode_azione", "?");
				sqlQueryObject.addUpdateField("pattern_azione", "?");
				sqlQueryObject.addUpdateField("nome_porta_delegante_azione", "?");
				sqlQueryObject.addUpdateField("force_interface_based_azione", "?");
				sqlQueryObject.addUpdateField("autenticazione", "?");
				sqlQueryObject.addUpdateField("autenticazione_opzionale", "?");
				sqlQueryObject.addUpdateField("token_policy", "?");
				sqlQueryObject.addUpdateField("token_opzionale", "?");
				sqlQueryObject.addUpdateField("token_validazione", "?");
				sqlQueryObject.addUpdateField("token_introspection", "?");
				sqlQueryObject.addUpdateField("token_user_info", "?");
				sqlQueryObject.addUpdateField("token_forward", "?");
				sqlQueryObject.addUpdateField("token_options", "?");
				sqlQueryObject.addUpdateField("token_authn_issuer", "?");
				sqlQueryObject.addUpdateField("token_authn_client_id", "?");
				sqlQueryObject.addUpdateField("token_authn_subject", "?");
				sqlQueryObject.addUpdateField("token_authn_username", "?");
				sqlQueryObject.addUpdateField("token_authn_email", "?");
				sqlQueryObject.addUpdateField("autorizzazione", "?");
				sqlQueryObject.addUpdateField("autorizzazione_xacml", "?");
				sqlQueryObject.addUpdateField("autorizzazione_contenuto", "?");
				sqlQueryObject.addUpdateField("mtom_request_mode", "?");
				sqlQueryObject.addUpdateField("mtom_response_mode", "?");
				sqlQueryObject.addUpdateField("security", "?");
				sqlQueryObject.addUpdateField("security_mtom_req", "?");
				sqlQueryObject.addUpdateField("security_mtom_res", "?");
				sqlQueryObject.addUpdateField("security_request_mode", "?");
				sqlQueryObject.addUpdateField("security_response_mode", "?");
				sqlQueryObject.addUpdateField("id_soggetto", "?");
				sqlQueryObject.addUpdateField("ricevuta_asincrona_sim", "?");
				sqlQueryObject.addUpdateField("ricevuta_asincrona_asim", "?");
				sqlQueryObject.addUpdateField("integrazione", "?");
				sqlQueryObject.addUpdateField("scadenza_correlazione_appl", "?");
				sqlQueryObject.addUpdateField("validazione_contenuti_stato", "?");
				sqlQueryObject.addUpdateField("validazione_contenuti_tipo", "?");
				sqlQueryObject.addUpdateField("validazione_contenuti_mtom", "?");				
				sqlQueryObject.addUpdateField("allega_body", "?");
				sqlQueryObject.addUpdateField("scarta_body", "?");
				sqlQueryObject.addUpdateField("gestione_manifest", "?");
				sqlQueryObject.addUpdateField("stateless", "?");
				sqlQueryObject.addUpdateField("local_forward", "?");
				sqlQueryObject.addUpdateField("local_forward_pa", "?");
				sqlQueryObject.addUpdateField("ruoli_match", "?");
				sqlQueryObject.addUpdateField("token_sa_stato", "?");
				sqlQueryObject.addUpdateField("token_ruoli_stato", "?");
				sqlQueryObject.addUpdateField("token_ruoli_match", "?");
				sqlQueryObject.addUpdateField("token_ruoli_tipologia", "?");
				sqlQueryObject.addUpdateField("scope_stato", "?");
				sqlQueryObject.addUpdateField("scope_match", "?");
				sqlQueryObject.addUpdateField("ricerca_porta_azione_delegata", "?");
				sqlQueryObject.addUpdateField("msg_diag_severita", "?");
				sqlQueryObject.addUpdateField("tracciamento_esiti", "?");
				sqlQueryObject.addUpdateField("stato", "?");
				// cors
				sqlQueryObject.addUpdateField("cors_stato", "?");
				sqlQueryObject.addUpdateField("cors_tipo", "?");
				sqlQueryObject.addUpdateField("cors_all_allow_origins", "?");
				sqlQueryObject.addUpdateField("cors_all_allow_methods", "?");
				sqlQueryObject.addUpdateField("cors_all_allow_headers", "?");
				sqlQueryObject.addUpdateField("cors_allow_credentials", "?");
				sqlQueryObject.addUpdateField("cors_allow_max_age", "?");
				sqlQueryObject.addUpdateField("cors_allow_max_age_seconds", "?");
				sqlQueryObject.addUpdateField("cors_allow_origins", "?");
				sqlQueryObject.addUpdateField("cors_allow_headers", "?");
				sqlQueryObject.addUpdateField("cors_allow_methods", "?");
				sqlQueryObject.addUpdateField("cors_allow_expose_headers", "?");
				// responseCaching
				sqlQueryObject.addUpdateField("response_cache_stato", "?");
				sqlQueryObject.addUpdateField("response_cache_seconds", "?");
				sqlQueryObject.addUpdateField("response_cache_max_msg_size", "?");
				sqlQueryObject.addUpdateField("response_cache_control_nocache", "?");
				sqlQueryObject.addUpdateField("response_cache_control_maxage", "?");
				sqlQueryObject.addUpdateField("response_cache_control_nostore", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_url", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_query", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_query_list", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_headers", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_hdr_list", "?");
				sqlQueryObject.addUpdateField("response_cache_hash_payload", "?");
				// id
				sqlQueryObject.addUpdateField("id_accordo", "?");
				sqlQueryObject.addUpdateField("id_port_type", "?");
				// options
				sqlQueryObject.addUpdateField("options", "?");
				// canale
				sqlQueryObject.addUpdateField("canale", "?");
				sqlQueryObject.addWhereCondition("id=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLUpdate();
				stm = con.prepareStatement(sqlQuery);

				index = 1;
				stm.setString(index++, nomePorta);
				stm.setString(index++, descrizione);
				stm.setLong(index++, idSoggettoErogatore);
				stm.setString(index++, tipoSoggErogatore);
				stm.setString(index++, nomeSoggErogatore);
				stm.setLong(index++, idServizioPD);
				stm.setString(index++, tipoServizio);
				stm.setString(index++, nomeServizio);
				stm.setInt(index++, versioneServizio);
				stm.setLong(index++, idAzione);
				stm.setString(index++, nomeAzione);
				if(modeAzione!=null)
					stm.setString(index++, modeAzione.toString());
				else
					stm.setString(index++, null);
				stm.setString(index++, patternAzione);
				stm.setString(index++, nomePortaDeleganteAzione);
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(forceInterfaceBased));
				// autenticazione
				stm.setString(index++, autenticazione);
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getAutenticazioneOpzionale()) : null);
				// token
				stm.setString(index++, gestioneToken!=null ? gestioneToken.getPolicy() : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getTokenOpzionale()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getValidazione()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getIntrospection()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getUserInfo()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDB_LIB.getValue(gestioneToken.getForward()) : null);
				stm.setString(index++, gestioneToken!=null ? gestioneToken.getOptions() : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getIssuer()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getClientId()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getSubject()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getUsername()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDB_LIB.getValue(gestioneToken.getAutenticazione().getEmail()) : null);
				// autorizzazione
				stm.setString(index++, autorizzazione);
				stm.setString(index++, autorizzazioneXacmlPolicy);
				stm.setString(index++, autorizzazioneContenuto);
				// mtom
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(mtomMode_request));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(mtomMode_response));
				// messageSecurity
				stm.setString(index++, messageSecurityStatus);
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(messageSecurityApplyMtom_request));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(messageSecurityApplyMtom_response));
				stm.setString(index++, securityRequestMode);
				stm.setString(index++, securityResponseMode);
				// soggettoProprietario
				stm.setLong(index++, idSoggettoProprietario);
				//ricevuta asincrona_asimmetrica/simmetrica
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(aPD.getRicevutaAsincronaSimmetrica()));
				stm.setString(index++, DriverConfigurazioneDB_LIB.getValue(aPD.getRicevutaAsincronaAsimmetrica()));
				//integrazione
				stm.setString(index++, aPD.getIntegrazione());
				//scadenza correlazione applicativa
				stm.setString(index++, aPD.getCorrelazioneApplicativa()!=null ? aPD.getCorrelazioneApplicativa().getScadenza() : null);
				//validazione xsd
				stm.setString(index++, aPD.getValidazioneContenutiApplicativi()!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getValidazioneContenutiApplicativi().getStato()) : null);
				stm.setString(index++, aPD.getValidazioneContenutiApplicativi()!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getValidazioneContenutiApplicativi().getTipo()) : null);
				stm.setString(index++, aPD.getValidazioneContenutiApplicativi()!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getValidazioneContenutiApplicativi().getAcceptMtomMessage()) : null);
				// InvocazionePorta: funzionalita' attachment
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getAllegaBody()) : null);
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getScartaBody()) : null);
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getGestioneManifest()) : null);
				// Stateless
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getStateless()) : null);
				// LocalForward
				stm.setString(index++, aPD!=null && aPD.getLocalForward()!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getLocalForward().getStato()) : null);
				stm.setString(index++, aPD!=null && aPD.getLocalForward()!=null ? aPD.getLocalForward().getPortaApplicativa() : null);
				// Ruoli
				stm.setString(index++, aPD!=null && aPD.getRuoli()!=null && aPD.getRuoli().getMatch()!=null ? 
						aPD.getRuoli().getMatch().getValue() : null);
				// Token sa
				stm.setString(index++, aPD!=null && aPD.getAutorizzazioneToken()!=null && aPD.getAutorizzazioneToken().getAutorizzazioneApplicativi()!=null ? 
						DriverConfigurazioneDB_LIB.getValue(aPD.getAutorizzazioneToken().getAutorizzazioneApplicativi()) : null);	
				// Token ruoli
				stm.setString(index++, aPD!=null && aPD.getAutorizzazioneToken()!=null && aPD.getAutorizzazioneToken().getAutorizzazioneRuoli()!=null ? 
						DriverConfigurazioneDB_LIB.getValue(aPD.getAutorizzazioneToken().getAutorizzazioneRuoli()) : null);
				stm.setString(index++, aPD!=null && aPD.getAutorizzazioneToken()!=null && aPD.getAutorizzazioneToken().getRuoli()!=null && 
						aPD.getAutorizzazioneToken().getRuoli().getMatch()!=null ? 
						aPD.getAutorizzazioneToken().getRuoli().getMatch().getValue() : null);
				stm.setString(index++, aPD!=null && aPD.getAutorizzazioneToken()!=null && aPD.getAutorizzazioneToken().getTipologiaRuoli()!=null ? 
						aPD.getAutorizzazioneToken().getTipologiaRuoli().getValue() : null);
				// Scope
				stm.setString(index++, aPD!=null && aPD.getScope()!=null && aPD.getScope().getStato()!=null ? 
						DriverConfigurazioneDB_LIB.getValue(aPD.getScope().getStato()) : null);
				stm.setString(index++, aPD!=null && aPD.getScope()!=null && aPD.getScope().getMatch()!=null ? 
						aPD.getScope().getMatch().getValue() : null);
				// RicercaPortaAzioneDelegata
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getRicercaPortaAzioneDelegata()) : null);
				// Tracciamento
				stm.setString(index++, msg_diag_severita);
				stm.setString(index++, tracciamento_esiti);
				// Stato
				stm.setString(index++, aPD!=null ? DriverConfigurazioneDB_LIB.getValue(aPD.getStato()) : null);
				// cors
				stm.setString(index++, cors_stato);
				stm.setString(index++, cors_tipo);
				stm.setString(index++, cors_all_allow_origins);
				stm.setString(index++, cors_all_allow_methods);
				stm.setString(index++, cors_all_allow_headers);
				stm.setString(index++, cors_allow_credentials);
				stm.setInt(index++, cors_allow_max_age);
				if(cors_allow_max_age_seconds!=null) {
					stm.setInt(index++, cors_allow_max_age_seconds);
				}
				else {
					stm.setNull(index++, java.sql.Types.INTEGER);
				}
				stm.setString(index++, cors_allow_origins);
				stm.setString(index++, cors_allow_headers);
				stm.setString(index++, cors_allow_methods);
				stm.setString(index++, cors_allow_expose_headers);
				
				// responseCaching
				stm.setString(index++, response_cache_stato);
				if(response_cache_seconds!=null) {
					stm.setInt(index++, response_cache_seconds);
				}
				else {
					stm.setNull(index++, java.sql.Types.INTEGER);
				}
				if(response_cache_max_msg_size!=null) {
					stm.setLong(index++, response_cache_max_msg_size);
				}
				else {
					stm.setNull(index++, java.sql.Types.BIGINT);
				}
				stm.setInt(index++, response_cache_noCache ? CostantiDB.TRUE : CostantiDB.FALSE);
				stm.setInt(index++, response_cache_maxAge ? CostantiDB.TRUE : CostantiDB.FALSE);
				stm.setInt(index++, response_cache_noStore ? CostantiDB.TRUE : CostantiDB.FALSE);
				stm.setString(index++, response_cache_hash_url);
				stm.setString(index++, response_cache_hash_query);
				stm.setString(index++, response_cache_hash_query_list);
				stm.setString(index++, response_cache_hash_headers);
				stm.setString(index++, response_cache_hash_headers_list);
				stm.setString(index++, response_cache_hash_payload);
				//idAccordo
				stm.setLong(index++,aPD.getIdAccordo() != null ? aPD.getIdAccordo() : -1L);
				stm.setLong(index++, aPD.getIdPortType() != null ? aPD.getIdPortType() : -1L);
				// options
				stm.setString(index++, aPD.getOptions());
				// canale
				stm.setString(index++, aPD.getCanale());
				
				// where
				stm.setLong(index++, idPortaDelegata);

				//DriverConfigurazioneDB_LIB.log.debug("eseguo query: " + DBUtils.formatSQLString(sqlQuery, nomePorta, descrizione, 
				// soggErogatore.getId(), soggErogatore.getTipo(), soggErogatore.getNome(), soggErogatore.getIdentificazione(), servizio.getId(), 
				// servizio.getTipo(), servizio.getNome(), servizio.getIdentificazione(), azione.getId(), azione.getNome(), azione.getIdentificazione(), 
				// autenticazione, autorizzazione, messageSecurityStatus, idSoggettoProprietario, idPortaDelegata));

				n = stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Updated " + n + " row(s).");

				
				
				// mtom
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MTOM_REQUEST);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				stm.executeUpdate();
				stm.close();

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MTOM_RESPONSE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				stm.executeUpdate();
				stm.close();
				
				if(mtomProcessor!=null){
				
					MtomProcessorFlowParameter reqParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_MTOM_REQUEST);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("content_type", "?");
					sqlQueryObject.addInsertField("required", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
	
					i = 0;
					if(mtomProcessor.getRequestFlow()!=null){
						MtomProcessorFlow flow = mtomProcessor.getRequestFlow();
						for (i = 0; i < flow.sizeParameterList(); i++) {
							reqParam = flow.getParameter(i);
							stm.setLong(1, idPortaDelegata);
							stm.setString(2, reqParam.getNome());
							stm.setString(3, reqParam.getPattern());
							stm.setString(4, reqParam.getContentType());
							stm.setInt(5, reqParam.getRequired() ? CostantiDB.TRUE : CostantiDB.FALSE);
	
							stm.executeUpdate();
						}	
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " mtom request flow con id=" + idPortaDelegata);
	
					MtomProcessorFlowParameter resParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_MTOM_RESPONSE);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("content_type", "?");
					sqlQueryObject.addInsertField("required", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
					
					i = 0;
					if(mtomProcessor.getResponseFlow()!=null){
						MtomProcessorFlow flow = mtomProcessor.getResponseFlow();
						for (i = 0; i < flow.sizeParameterList(); i++) {
							resParam = flow.getParameter(i);
							stm.setLong(1, idPortaDelegata);
							stm.setString(2, resParam.getNome());
							stm.setString(3, resParam.getPattern());
							stm.setString(4, resParam.getContentType());
							stm.setInt(5, resParam.getRequired() ? CostantiDB.TRUE : CostantiDB.FALSE);
	
							stm.executeUpdate();
						}
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " mtom response flow con id=" + idPortaDelegata);
					
				}
				
				
				
				// se security abilitato setto la lista
				// la lista contiene tutti e soli gli elementi necessari quindi resetto la lista nel db e riscrivo la lista nuova
				//if ((messageSecurity != null) && CostantiConfigurazione.ABILITATO.toString().equals(messageSecurityStatus) )  {
				// Devo settarli sempre se ci sono, in modo che lo switch abilitato-disabilitato funzioni
				if ((messageSecurity != null) )  {

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST);
					sqlQueryObject.addWhereCondition("id_porta=?");
					sqlQuery = sqlQueryObject.createSQLDelete();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, idPortaDelegata);
					stm.executeUpdate();
					stm.close();

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
					sqlQueryObject.addWhereCondition("id_porta=?");
					sqlQuery = sqlQueryObject.createSQLDelete();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, idPortaDelegata);
					stm.executeUpdate();
					stm.close();

					//inserisco i valori presenti nella lista 
					MessageSecurityFlowParameter reqParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					i = 0;
					if(messageSecurity.getRequestFlow()!=null){
						MessageSecurityFlow flow = messageSecurity.getRequestFlow();
						for (i = 0; i < flow.sizeParameterList(); i++) {
							reqParam = flow.getParameter(i);
							stm.setLong(1, idPortaDelegata);
							stm.setString(2, reqParam.getNome());
							stm.setString(3, reqParam.getValore());
							stm.executeUpdate();
						}
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " request flow con id=" + idPortaDelegata);

					MessageSecurityFlowParameter resParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
					
					i = 0;
					if(messageSecurity.getResponseFlow()!=null){
						MessageSecurityFlow flow = messageSecurity.getResponseFlow();
						for (i = 0; i < flow.sizeParameterList(); i++) {
							resParam = flow.getParameter(i);
							stm.setLong(1, idPortaDelegata);
							stm.setString(2, resParam.getNome());
							stm.setString(3, resParam.getValore());
							stm.executeUpdate();
						}
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " response flow con id=" + idPortaDelegata);
				}


				// la lista di correlazioni applicative contiene tutti e soli gli elementi necessari quindi resetto la lista nel db e riscrivo la lista nuova
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				stm.executeUpdate();
				stm.close();
				
				if (corrApp != null) {
					//inserisco i valori presenti nella lista 
					CorrelazioneApplicativaElemento cae = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome_elemento", "?");
					sqlQueryObject.addInsertField("mode_correlazione", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("identificazione_fallita", "?");
					sqlQueryObject.addInsertField("riuso_id", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					for (i = 0; i < corrApp.sizeElementoList(); i++) {
						cae = corrApp.getElemento(i);
						stm.setLong(1, idPortaDelegata);
						stm.setString(2, cae.getNome());
						stm.setString(3, DriverConfigurazioneDB_LIB.getValue(cae.getIdentificazione()));
						if (cae.getPattern() != null)
							stm.setString(4, cae.getPattern());
						else
							stm.setString(4, "");
						stm.setString(5, DriverConfigurazioneDB_LIB.getValue(cae.getIdentificazioneFallita()));
						stm.setString(6, DriverConfigurazioneDB_LIB.getValue(cae.getRiusoIdentificativo()));
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " correlazione applicativa con id=" + idPortaDelegata);
				}

				// la lista di correlazioni applicative risposta contiene tutti e soli gli elementi necessari quindi resetto la lista nel db e riscrivo la lista nuova
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				stm.executeUpdate();
				stm.close();
				
				if (corrAppRisposta != null) {
					//inserisco i valori presenti nella lista 
					CorrelazioneApplicativaRispostaElemento cae = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE_RISPOSTA);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome_elemento", "?");
					sqlQueryObject.addInsertField("mode_correlazione", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("identificazione_fallita", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					for (i = 0; i < corrAppRisposta.sizeElementoList(); i++) {
						cae = corrAppRisposta.getElemento(i);
						stm.setLong(1, idPortaDelegata);
						stm.setString(2, cae.getNome());
						stm.setString(3, DriverConfigurazioneDB_LIB.getValue(cae.getIdentificazione()));
						if (cae.getPattern() != null)
							stm.setString(4, cae.getPattern());
						else
							stm.setString(4, "");
						stm.setString(5, DriverConfigurazioneDB_LIB.getValue(cae.getIdentificazioneFallita()));
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDB_LIB.log.debug("Inserted " + i + " correlazione applicativa risposta con id=" + idPortaDelegata);
				}
				
				/*Sincronizzazione servizi applicativi*/
				//la lista dei servizi applicativi passata contiene tutti e soli i servizi applicativi necessari
				//quindi nel db devono essere presenti tutti e solo quelli presenti nella lista
				//se la lista e' vuota allora i servizi applicativi vanno cancellati

				//TODO possibile ottimizzazione in termini di tempo
				//cancello i servizi applicativi associati alla porta e inserisco tutti e soli quelli presenti in lista
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_SA);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" servizi applicativi associati alla Porta Delegata "+idPortaDelegata);
				//scrivo la lista nel db
				n=0;
				for (i = 0; i < aPD.sizeServizioApplicativoList(); i++) {
					PortaDelegataServizioApplicativo servizioApplicativo = aPD.getServizioApplicativo(i);

					String nomeSA = servizioApplicativo.getNome();
					//il tipo e il nome proprietario servizio applicativo sono gli stessi della porta delegata
					//controllo se sono settati gli old, perche potrei essere in un caso di update
					String nomeProprietarioSA = aPD.getNomeSoggettoProprietario(); 
					String tipoProprietarioSA = aPD.getTipoSoggettoProprietario(); 

					if (nomeSA == null || nomeSA.equals(""))
						throw new DriverConfigurazioneException("Nome del ServizioApplicativo associato non valido.");
					if (nomeProprietarioSA == null || nomeProprietarioSA.equals(""))
						throw new DriverConfigurazioneException("Nome Proprietario del ServizioApplicativo associato non valido.");
					if (tipoProprietarioSA == null || tipoProprietarioSA.equals(""))
						throw new DriverConfigurazioneException("Tipo Proprietario del ServizioApplicativo associato non valido.");

					long idSA = DriverConfigurazioneDB_serviziApplicativiLIB.getIdServizioApplicativo(nomeSA, tipoProprietarioSA, nomeProprietarioSA, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);

					if (idSA <= 0)
						throw new DriverConfigurazioneException("Impossibile recuperare l'id del Servizio Applicativo [" + nomeSA + "] di [" + tipoProprietarioSA + "/" + nomeProprietarioSA + "]");

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_SA);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, idPortaDelegata);
					stm.setLong(2, idSA);

					stm.executeUpdate();
					stm.close();
					n++;
					DriverConfigurazioneDB_LIB.log.debug("Aggiunta associazione PortaDelegata<->ServizioApplicativo [" + idPortaDelegata + "]<->[" + idSA + "]");
				}

				DriverConfigurazioneDB_LIB.log.debug("Aggiunti " + n + " associazioni PortaDelegata<->ServizioApplicativo associati alla PortaDelegata[" + idPortaDelegata + "]");

				
				
				/*Proprieta Autenticazione associate alla Porta Delegata*/

				//La lista di proprieta contiene tutte e sole le proprieta associate alla porta
				//cancello le proprieta per poi sincronizzarle con la lista passata
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Eliminate "+n+" proprieta di autenticazione associate alla Porta Delegata "+idPortaDelegata);
				// set prop
				int newProps = 0;
				for (i = 0; i < aPD.sizeProprietaAutenticazioneList(); i++) {
					Proprieta propProtocollo = aPD.getProprietaAutenticazione(i);

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					stm.setLong(1, idPortaDelegata);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
					stm.close();
					newProps++;
				}
				DriverConfigurazioneDB_LIB.log.debug("Inserted " + newProps + " SetProtocolProp Autenticazione associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				
				/*Proprieta Autorizzazione associate alla Porta Delegata*/

				//La lista di proprieta contiene tutte e sole le proprieta associate alla porta
				//cancello le proprieta per poi sincronizzarle con la lista passata
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Eliminate "+n+" proprieta di autorizzazione associate alla Porta Delegata "+idPortaDelegata);
				// set prop
				newProps = 0;
				for (i = 0; i < aPD.sizeProprietaAutorizzazioneList(); i++) {
					Proprieta propProtocollo = aPD.getProprietaAutorizzazione(i);

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					stm.setLong(1, idPortaDelegata);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
					stm.close();
					newProps++;
				}
				DriverConfigurazioneDB_LIB.log.debug("Inserted " + newProps + " SetProtocolProp Autorizzazione associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				
				
				/*Proprieta Autorizzazione Contenuti associate alla Porta Delegata*/

				//La lista di proprieta contiene tutte e sole le proprieta associate alla porta
				//cancello le proprieta per poi sincronizzarle con la lista passata
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Eliminate "+n+" proprieta di autorizzazione contenuti associate alla Porta Delegata "+idPortaDelegata);
				// set prop
				newProps = 0;
				for (i = 0; i < aPD.sizeProprietaAutorizzazioneContenutoList(); i++) {
					Proprieta propProtocollo = aPD.getProprietaAutorizzazioneContenuto(i);

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					stm.setLong(1, idPortaDelegata);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
					stm.close();
					newProps++;
				}
				DriverConfigurazioneDB_LIB.log.debug("Inserted " + newProps + " SetProtocolProp AutorizzazioneContenuto associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				
				
				
				/*Proprieta Rate Limiting associate alla Porta Delegata*/

				//La lista di proprieta contiene tutte e sole le proprieta associate alla porta
				//cancello le proprieta per poi sincronizzarle con la lista passata
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_RATE_LIMITING_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Eliminate "+n+" proprieta di rate limiting associate alla Porta Delegata "+idPortaDelegata);
				// set prop
				newProps = 0;
				for (i = 0; i < aPD.sizeProprietaRateLimitingList(); i++) {
					Proprieta propProtocollo = aPD.getProprietaRateLimiting(i);

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_RATE_LIMITING_PROP);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					stm.setLong(1, idPortaDelegata);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
					stm.close();
					newProps++;
				}
				DriverConfigurazioneDB_LIB.log.debug("Inserted " + newProps + " SetProtocolPropRateLimiting associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				
				
				
				
				/*Proprieta associate alla Porta Delegata*/

				//La lista di proprieta contiene tutte e sole le proprieta associate alla porta
				//cancello le proprieta per poi sincronizzarle con la lista passata
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Eliminate "+n+" proprieta associate alla Porta Delegata "+idPortaDelegata);
				// set prop
				newProps = 0;
				for (i = 0; i < aPD.sizeProprietaList(); i++) {
					Proprieta propProtocollo = aPD.getProprieta(i);

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_PROP);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					stm.setLong(1, idPortaDelegata);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
					stm.close();
					newProps++;
				}
				DriverConfigurazioneDB_LIB.log.debug("Inserted " + newProps + " SetProtocolProp associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				// Ruoli
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_RUOLI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" ruoli associati alla Porta Delegata "+idPortaDelegata);
				
				n=0;
				if(aPD.getRuoli()!=null && aPD.getRuoli().sizeRuoloList()>0){
					for (int j = 0; j < aPD.getRuoli().sizeRuoloList(); j++) {
						Ruolo ruolo = aPD.getRuoli().getRuolo(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_RUOLI);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("ruolo", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idPortaDelegata);
						stm.setString(2, ruolo.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunto ruolo[" + ruolo.getNome() + "] alla PortaDelegata[" + idPortaDelegata + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunti " + n + " ruoli alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				
				// Scope
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_SCOPE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" scope associati alla Porta Delegata "+idPortaDelegata);
				
				n=0;
				if(aPD.getScope()!=null && aPD.getScope().sizeScopeList()>0){
					for (int j = 0; j < aPD.getScope().sizeScopeList(); j++) {
						Scope scope = aPD.getScope().getScope(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_SCOPE);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("scope", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idPortaDelegata);
						stm.setString(2, scope.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunto scope[" + scope.getNome() + "] alla PortaDelegata[" + idPortaDelegata + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunti " + n + " scope alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				
				
				/*Sincronizzazione servizi applicativi token */
				//la lista dei servizi applicativi passata contiene tutti e soli i servizi applicativi necessari
				//quindi nel db devono essere presenti tutti e solo quelli presenti nella lista
				//se la lista e' vuota allora i servizi applicativi vanno cancellati

				//TODO possibile ottimizzazione in termini di tempo
				//cancello i servizi applicativi associati alla porta e inserisco tutti e soli quelli presenti in lista
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_TOKEN_SA);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" servizi applicativi (token) associati alla Porta Delegata "+idPortaDelegata);
			
				// serviziapplicativi (token)
				if(aPD.getAutorizzazioneToken()!=null && aPD.getAutorizzazioneToken().getServiziApplicativi()!=null &&
						aPD.getAutorizzazioneToken().getServiziApplicativi().sizeServizioApplicativoList()>0) {
					//scrivo la lista nel db
					n=0;
					for (i = 0; i < aPD.getAutorizzazioneToken().getServiziApplicativi().sizeServizioApplicativoList(); i++) {
						PortaDelegataServizioApplicativo servizioApplicativo = aPD.getAutorizzazioneToken().getServiziApplicativi().getServizioApplicativo(i);
	
						String nomeSA = servizioApplicativo.getNome();
						//il tipo e il nome proprietario servizio applicativo sono gli stessi della porta delegata
						//controllo se sono settati gli old, perche potrei essere in un caso di update
						String nomeProprietarioSA = aPD.getNomeSoggettoProprietario(); 
						String tipoProprietarioSA = aPD.getTipoSoggettoProprietario(); 
	
						if (nomeSA == null || nomeSA.equals(""))
							throw new DriverConfigurazioneException("Nome del ServizioApplicativo associato non valido.");
						if (nomeProprietarioSA == null || nomeProprietarioSA.equals(""))
							throw new DriverConfigurazioneException("Nome Proprietario del ServizioApplicativo associato non valido.");
						if (tipoProprietarioSA == null || tipoProprietarioSA.equals(""))
							throw new DriverConfigurazioneException("Tipo Proprietario del ServizioApplicativo associato non valido.");
	
						long idSA = DriverConfigurazioneDB_serviziApplicativiLIB.getIdServizioApplicativo(nomeSA, tipoProprietarioSA, nomeProprietarioSA, con, DriverConfigurazioneDB_LIB.tipoDB,DriverConfigurazioneDB_LIB.tabellaSoggetti);
	
						if (idSA <= 0)
							throw new DriverConfigurazioneException("Impossibile recuperare l'id del Servizio Applicativo [" + nomeSA + "] di [" + tipoProprietarioSA + "/" + nomeProprietarioSA + "]");
	
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_TOKEN_SA);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idPortaDelegata);
						stm.setLong(2, idSA);
	
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunta associazione PortaDelegata<->ServizioApplicativo (token) [" + idPortaDelegata + "]<->[" + idSA + "]");
					}
	
					DriverConfigurazioneDB_LIB.log.debug("Aggiunti " + n + " associazioni PortaDelegata<->ServizioApplicativo (token) associati alla PortaDelegata[" + idPortaDelegata + "]");
				}
				
				
				
				// Ruoli (token)
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_TOKEN_RUOLI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" ruoli (token) associati alla Porta Delegata "+idPortaDelegata);
				
				n=0;
				if(aPD.getAutorizzazioneToken()!=null && aPD.getAutorizzazioneToken().getRuoli()!=null &&
						aPD.getAutorizzazioneToken().getRuoli().sizeRuoloList()>0){
					for (int j = 0; j < aPD.getAutorizzazioneToken().getRuoli().sizeRuoloList(); j++) {
						Ruolo ruolo = aPD.getAutorizzazioneToken().getRuoli().getRuolo(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_TOKEN_RUOLI);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("ruolo", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idPortaDelegata);
						stm.setString(2, ruolo.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunto ruolo[" + ruolo.getNome() + "] (token) alla PortaDelegata[" + idPortaDelegata + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunti " + n + " ruoli (token) alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				// Azioni
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_AZIONI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" azioni delegate associate alla Porta Delegata "+idPortaDelegata);
				
				n=0;
				if(aPD.getAzione()!=null && aPD.getAzione().sizeAzioneDelegataList()>0){
					for (int j = 0; j < aPD.getAzione().sizeAzioneDelegataList(); j++) {
						String azioneDelegata = aPD.getAzione().getAzioneDelegata(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_AZIONI);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("azione", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idPortaDelegata);
						stm.setString(2, azioneDelegata);
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunta azione delegata [" + azioneDelegata + "] alla PortaDelegata[" + idPortaDelegata + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunte " + n + " azioni delegate alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				
				
				// Cache Regole
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_CACHE_REGOLE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" regole di cache associate alla Porta Delegata "+idPortaDelegata);
				
				n=0;
				if(response_cache_regole!=null && response_cache_regole.size()>0){
					for (int j = 0; j < response_cache_regole.size(); j++) {
						ResponseCachingConfigurazioneRegola regola = response_cache_regole.get(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_CACHE_REGOLE);
						sqlQueryObject.addInsertField("id_porta", "?");
						if(regola.getReturnCodeMin()!=null && regola.getReturnCodeMin()>0) {
							sqlQueryObject.addInsertField("status_min", "?");
						}
						if(regola.getReturnCodeMax()!=null && regola.getReturnCodeMax()>0) {
							sqlQueryObject.addInsertField("status_max", "?");
						}
						sqlQueryObject.addInsertField("fault", "?");
						if(regola.getCacheTimeoutSeconds()!=null && regola.getCacheTimeoutSeconds()>0) {
							sqlQueryObject.addInsertField("cache_seconds", "?");
						}
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						int indexCR = 1;
						stm.setLong(indexCR++, idPortaDelegata);
						if(regola.getReturnCodeMin()!=null && regola.getReturnCodeMin()>0) {
							stm.setInt(indexCR++, regola.getReturnCodeMin());
						}
						if(regola.getReturnCodeMax()!=null && regola.getReturnCodeMax()>0) {
							stm.setInt(indexCR++, regola.getReturnCodeMax());
						}
						stm.setInt(indexCR++, regola.getFault() ? CostantiDB.TRUE : CostantiDB.FALSE);
						if(regola.getCacheTimeoutSeconds()!=null && regola.getCacheTimeoutSeconds()>0) {
							stm.setInt(indexCR++, regola.getCacheTimeoutSeconds());
						}
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunta regola di cache alla PortaDelegata[" + idPortaDelegata + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunte " + n + " regole di cache alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				
				// AttributeAuthority
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_ATTRIBUTE_AUTHORITY);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellate "+n+" A.A. associate alla Porta Delegata "+idPortaDelegata);
				
				n=0;
				if(aPD.sizeAttributeAuthorityList()>0){
					for (int j = 0; j < aPD.sizeAttributeAuthorityList(); j++) {
						AttributeAuthority aa = aPD.getAttributeAuthority(j);
						
						String attributi = null;
						if(aa.sizeAttributoList()>0) {
							StringBuilder bf = new StringBuilder();
							for (int aaI = 0; aaI < aa.sizeAttributoList(); aaI++) {
								if(aaI>0) {
									bf.append(",");
								}
								bf.append(aa.getAttributo(aaI));
							}
							attributi = bf.toString();
						}
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_DELEGATE_ATTRIBUTE_AUTHORITY);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("nome", "?");
						sqlQueryObject.addInsertField("attributi", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idPortaDelegata);
						stm.setString(2, aa.getNome());
						stm.setString(3, attributi);
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDB_LIB.log.debug("Aggiunta A.A.[" + aa.getNome() + "] alla PortaDelegata[" + idPortaDelegata + "]");
					}
				}
				
				DriverConfigurazioneDB_LIB.log.debug("Aggiunte " + n + " A.A. alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				
				
				// dumpConfigurazione
				DriverConfigurazioneDB_dumpLIB.CRUDDumpConfigurazione(type, con, aPD.getDump(), idPortaDelegata, CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_PD);
				
				
				// trasformazioni
				DriverConfigurazioneDB_trasformazioniLIB.CRUDTrasformazioni(type, con, aPD.getTrasformazioni(), idPortaDelegata, true);
				
				
				// Handlers
				DriverConfigurazioneDB_handlerLIB.CRUDConfigurazioneMessageHandlers(type, con, idPortaDelegata, null, true, (configHandlers!=null) ? configHandlers.getRequest() : null);
				DriverConfigurazioneDB_handlerLIB.CRUDConfigurazioneMessageHandlers(type, con, idPortaDelegata, null, false, (configHandlers!=null) ? configHandlers.getResponse() : null);
				
				
				// extendedInfo
				if(extInfoConfigurazioneDriver!=null){
					extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log, aPD, CRUDType.UPDATE);
				}
				
				i=0;
				if(aPD.sizeExtendedInfoList()>0){
					if(extInfoConfigurazioneDriver!=null){
						for (i = 0; i < aPD.sizeExtendedInfoList(); i++) {
							extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDB_LIB.log, aPD, aPD.getExtendedInfo(i), CRUDType.UPDATE);
						}
					}
				}
				DriverConfigurazioneDB_LIB.log.debug("Aggiunte " + i + " associazioni ExtendedInfo<->PortaDelegata associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				break;

			case DELETE:
				// delete
				idPortaDelegata = DBUtils.getIdPortaDelegata(nomePorta, con, DriverConfigurazioneDB_LIB.tipoDB);
				if (idPortaDelegata <= 0)
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaDelegata(DELETE)] Non e' stato possibile recuperare l'id della Porta Delegata, necessario per effettuare la DELETE.");

				// dumpConfigurazione
				DriverConfigurazioneDB_dumpLIB.CRUDDumpConfigurazione(type, con, aPD.getDump(), idPortaDelegata, CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_PD);
				
				// trasformazioni
				DriverConfigurazioneDB_trasformazioniLIB.CRUDTrasformazioni(type, con, aPD.getTrasformazioni(), idPortaDelegata, true);
				
				// Handlers
				DriverConfigurazioneDB_handlerLIB.CRUDConfigurazioneMessageHandlers(type, con, idPortaDelegata, null, true, (configHandlers!=null) ? configHandlers.getRequest() : null);
				DriverConfigurazioneDB_handlerLIB.CRUDConfigurazioneMessageHandlers(type, con, idPortaDelegata, null, false, (configHandlers!=null) ? configHandlers.getResponse() : null);
				
				// AttributeAuthority
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_ATTRIBUTE_AUTHORITY);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellate "+n+" A.A. associate alla Porta Delegata "+idPortaDelegata);
				
				// Cache Regole
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_CACHE_REGOLE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" regole di cache associate alla Porta Delegata "+idPortaDelegata);
				
				// azioni delegate
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_AZIONI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" azioni delegate associate alla Porta Delegata "+idPortaDelegata);
				
				// ruoli (token)
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_TOKEN_RUOLI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" ruoli (token) associati alla Porta Delegata "+idPortaDelegata);
				
				// servizi applicativi (token)
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_TOKEN_SA);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " associazioni PortaDelegata<->ServizioApplicativo (token) associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				// ruoli
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_RUOLI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" ruoli associati alla Porta Delegata "+idPortaDelegata);
				
				// scope
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_SCOPE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Cancellati "+n+" scope associati alla Porta Delegata "+idPortaDelegata);
				
				// mtom
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MTOM_REQUEST);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " request flow con id=" + idPortaDelegata);

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MTOM_RESPONSE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " response flow con id=" + idPortaDelegata);

				
				// message security
				//if ( CostantiConfigurazione.ABILITATO.toString().equals(messageSecurityStatus) )  {
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " request flow con id=" + idPortaDelegata);

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " response flow con id=" + idPortaDelegata);
				//}

				// servizi applicativi
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_SA);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " associazioni PortaDelegata<->ServizioApplicativo associati alla PortaDelegata[" + idPortaDelegata + "]");

				// cancello anche le flow di request/response associate a questa
				// porta applicativa
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " security_request flow associate alla PortaDelegata[" + idPortaDelegata + "]");

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " security_response flow associate alla PortaDelegata[" + idPortaDelegata + "]");

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);

				n = stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " correlazione associate alla PortaDelegata[" + idPortaDelegata + "]");

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);

				n = stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " correlazione per la rispsota associate alla PortaDelegata[" + idPortaDelegata + "]");

				// cancello le prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " SetProtocolProp associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				// cancello le prop di rate limiting
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_RATE_LIMITING_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " SetProtocolPropRateLimiting associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				// cancello le prop di autorizzazione contenuti
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " SetProtocolPropAutorizzazioneContenuto associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				// cancello le prop di autorizzazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " SetProtocolPropAutorizzazione associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				
				// cancello le prop di autenticazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " SetProtocolPropAutenticazione associati alla PortaDelegata[" + idPortaDelegata + "]");
				
				// extendedInfo
				if(extInfoConfigurazioneDriver!=null){
					extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDB_LIB.log, aPD, CRUDType.DELETE);
				}
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDB_LIB.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_DELEGATE);
				sqlQueryObject.addWhereCondition("id=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaDelegata);
				DriverConfigurazioneDB_LIB.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idPortaDelegata));
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDB_LIB.log.debug("Deleted " + n + " row(s).");


				break;
			}
			return idPortaDelegata;
		} catch (DriverConfigurazioneException e) {
			throw e;
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaDelegata] SQLException : " + se.getMessage(),se);
		} catch (Exception e) {
			throw new DriverConfigurazioneException("Errore durante operazione("+type+") CRUDPortaDelegata.",e);
		}finally {
			//Chiudo statement and resultset
			try{
				if(rs!=null) rs.close();
				if(stm!=null) stm.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}


	
}
