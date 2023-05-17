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
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;
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
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneSoggetto;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.Scope;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaCacheDigestQueryParameter;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.ExtendedInfoManager;
import org.openspcoop2.core.constants.CRUDType;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_soggettiLIB
 * 
 * @author Stefano Corallo - corallo@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_porteApplicativeLIB {


	public static long CRUDPortaApplicativa(int type, PortaApplicativa aPA, Connection con) throws DriverConfigurazioneException {
		if (aPA == null)
			throw new DriverConfigurazioneException("Porta Applicativa non valida.");
		// parametri necessari
		String nomePorta = aPA.getNome();
		String nomeProprietario = aPA.getNomeSoggettoProprietario();
		String tipoProprietario = aPA.getTipoSoggettoProprietario();
		if (nomePorta == null || nomePorta.equals(""))
			throw new DriverConfigurazioneException("Nome della Porta Applicativa non valido.");
		if (nomeProprietario == null || nomeProprietario.equals(""))
			throw new DriverConfigurazioneException("Nome proprietario Porta Applicativa non valido.");
		if (tipoProprietario == null || tipoProprietario.equals(""))
			throw new DriverConfigurazioneException("Tipo proprietario della Porta Applicativa non valido.");

		PreparedStatement stm = null;
		String sqlQuery = "";
		ResultSet rs = null;

		String descrizione = aPA.getDescrizione();

		String autenticazione = aPA.getAutenticazione();
		String autorizzazione = aPA.getAutorizzazione();
		String autorizzazioneXacmlPolicy = aPA.getXacmlPolicy();
		GestioneToken gestioneToken = aPA.getGestioneToken(); 
		
		PortaApplicativaAzione azione = aPA.getAzione();
		PortaApplicativaServizio servizio = aPA.getServizio();
		long idServizio = ((servizio != null && servizio.getId() != null) ? servizio.getId() : -1);
		//System.out.println("PRIMA PA: "+idServizio);
		// SEMPRE, VA AGGIORNATO ANCHE IN UPDATE if(idServizio<=0 &&
		if(
				servizio!=null && servizio.getTipo()!=null && servizio.getNome()!=null && servizio.getVersione()!=null && servizio.getVersione()>0) {
			try {
				idServizio = DBUtils.getIdServizio(servizio.getNome(), servizio.getTipo(), servizio.getVersione(), nomeProprietario, tipoProprietario, con, DriverConfigurazioneDBLib.tipoDB,DriverConfigurazioneDBLib.tabellaSoggetti);
				//System.out.println("DOPO PA: "+idServizio);
			} catch (Throwable e1) {
				DriverConfigurazioneDBLib.log.debug(e1.getMessage(),e1); // potrebbe non esistere la tabella
			}
		}
		// long idServizio = (servizio !=null ?
		// getIdServizio(servizio.getNome(),
		// servizio.getTipo(),aPA.getNomeSoggettoProprietario(),aPA.getTipoSoggettoProprietario()
		// , con) : -1);

		PortaApplicativaSoggettoVirtuale soggVirt = aPA.getSoggettoVirtuale();
		String tipoSoggVirt = (soggVirt != null ? soggVirt.getTipo() : null);
		String nomeSoggVirt = (soggVirt != null ? soggVirt.getNome() : null);
		//long idSoggVirt = ((soggVirt != null && soggVirt.getId() != null) ? soggVirt.getId() : -1);
		long idSoggVirt=-1;
		try {
			idSoggVirt = DBUtils.getIdSoggetto(nomeSoggVirt, tipoSoggVirt, con, DriverConfigurazioneDBLib.tipoDB,DriverConfigurazioneDBLib.tabellaSoggetti);
		} catch (CoreException e1) {
			DriverConfigurazioneDBLib.log.error(e1.getMessage(),e1);
		}

		Proprieta propProtocollo = null;

		MtomProcessor mtomProcessor = aPA.getMtomProcessor();
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
		
		MessageSecurity messageSecurity = aPA.getMessageSecurity();
		String messageSecurityStatus = aPA.getStatoMessageSecurity();
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
		
		CorrelazioneApplicativa corrApp = aPA.getCorrelazioneApplicativa();
		CorrelazioneApplicativaRisposta corrAppRisposta = aPA.getCorrelazioneApplicativaRisposta();

		String msg_diag_severita = null;
		String tracciamento_esiti = null;
		if(aPA.getTracciamento()!=null){
			msg_diag_severita = DriverConfigurazioneDBLib.getValue(aPA.getTracciamento().getSeverita());
			tracciamento_esiti = aPA.getTracciamento().getEsiti();
		}
		
		CorsConfigurazione corsConfigurazione = aPA.getGestioneCors();
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
			cors_stato = DriverConfigurazioneDBLib.getValue(corsConfigurazione.getStato());
			cors_tipo = DriverConfigurazioneDBLib.getValue(corsConfigurazione.getTipo());
			cors_all_allow_origins = DriverConfigurazioneDBLib.getValue(corsConfigurazione.getAccessControlAllAllowOrigins());
			cors_all_allow_methods = DriverConfigurazioneDBLib.getValue(corsConfigurazione.getAccessControlAllAllowMethods());
			cors_all_allow_headers = DriverConfigurazioneDBLib.getValue(corsConfigurazione.getAccessControlAllAllowHeaders());
			cors_allow_credentials = DriverConfigurazioneDBLib.getValue(corsConfigurazione.getAccessControlAllowCredentials());
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
		
		ResponseCachingConfigurazione responseCachingConfigurazone = aPA.getResponseCaching();
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
			response_cache_stato = DriverConfigurazioneDBLib.getValue(responseCachingConfigurazone.getStato());
			response_cache_seconds = responseCachingConfigurazone.getCacheTimeoutSeconds();
			response_cache_max_msg_size = responseCachingConfigurazone.getMaxMessageSize();
			if(responseCachingConfigurazone.getControl()!=null) {
				response_cache_noCache = responseCachingConfigurazone.getControl().getNoCache();
				response_cache_maxAge = responseCachingConfigurazone.getControl().getMaxAge();
				response_cache_noStore = responseCachingConfigurazone.getControl().getNoStore();
			}
			if(responseCachingConfigurazone.getHashGenerator()!=null) {
				response_cache_hash_url = DriverConfigurazioneDBLib.getValue(responseCachingConfigurazone.getHashGenerator().getRequestUri());
				
				response_cache_hash_query = DriverConfigurazioneDBLib.getValue(responseCachingConfigurazone.getHashGenerator().getQueryParameters());
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
				
				response_cache_hash_headers = DriverConfigurazioneDBLib.getValue(responseCachingConfigurazone.getHashGenerator().getHeaders());
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
				
				response_cache_hash_payload = DriverConfigurazioneDBLib.getValue(responseCachingConfigurazone.getHashGenerator().getPayload());
			}
			response_cache_regole = responseCachingConfigurazone.getRegolaList();
		}
		
		String behaviour = null;
		if(aPA.getBehaviour()!=null) {
			behaviour = aPA.getBehaviour().getNome();
		}
		
		ConfigurazionePortaHandler configHandlers = aPA.getConfigurazioneHandler();
		
		ExtendedInfoManager extInfoManager = ExtendedInfoManager.getInstance();
		IExtendedInfo extInfoConfigurazioneDriver = extInfoManager.newInstanceExtendedInfoPortaApplicativa();
		
		try {
			int n = 0;
			int i = 0;
			long idPortaApplicativa = 0;
			long idProprietario = DBUtils.getIdSoggetto(nomeProprietario, tipoProprietario, con, DriverConfigurazioneDBLib.tipoDB,DriverConfigurazioneDBLib.tabellaSoggetti);
			// preparo lo statement in base al tipo di operazione
			switch (type) {
			case CREATE:
				// CREATE

				//campi obbligatori
				//servizio ci deve essere l'id oppure tipo e nome
				String tipoServizio = (servizio != null ? servizio.getTipo() : null);
				String nomeServizio = (servizio != null ? servizio.getNome() : null);
				Integer versioneServizio = (servizio != null ? servizio.getVersione() : null);
				//se l'id non e' valido allora devono esserci necessariamente il tipo e il nome
				if(idServizio<=0){
					if(tipoServizio==null || tipoServizio.equals("")) throw new DriverConfigurazioneException("Tipo Servizio non impostato.");
					if(nomeServizio==null || nomeServizio.equals("")) throw new DriverConfigurazioneException("Nome Servizio non impostato.");
					if(versioneServizio==null) throw new DriverConfigurazioneException("Nome Servizio non impostato.");
				}

				//Azione
				String nomeAzione = (azione != null ? azione.getNome() : null);
				String patternAzione = (azione != null ? azione.getPattern() : null);
				String nomePortaDeleganteAzione = (azione != null ? azione.getNomePortaDelegante() : null);
				StatoFunzionalita forceInterfaceBased = (azione != null ? azione.getForceInterfaceBased() : null);
				PortaApplicativaAzioneIdentificazione modeAzione = (azione != null ? azione.getIdentificazione() : null);
				//Se il bean Azione nn e' presente allora non controllo nulla
				if(azione!=null){
					if(modeAzione==null || modeAzione.equals("")) 
						modeAzione = PortaApplicativaAzioneIdentificazione.STATIC;
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
					case PROTOCOL_BASED:
						//nessun campo obbligatorio
						break;
					case STATIC:
						//ci deve essere il nome
						if(nomeAzione==null || nomeAzione.equals("")) throw new DriverConfigurazioneException("Nome Azione non impostato.");
						patternAzione=null;
						break;
					default:
						break;
					}
				}
				
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addInsertField("nome_porta", "?");
				sqlQueryObject.addInsertField("descrizione", "?");
				sqlQueryObject.addInsertField("id_soggetto_virtuale", "?");
				sqlQueryObject.addInsertField("tipo_soggetto_virtuale", "?");
				sqlQueryObject.addInsertField("nome_soggetto_virtuale", "?");
				sqlQueryObject.addInsertField("id_servizio", "?");
				sqlQueryObject.addInsertField("tipo_servizio", "?");
				sqlQueryObject.addInsertField("servizio", "?");
				sqlQueryObject.addInsertField("versione_servizio", "?");
				sqlQueryObject.addInsertField("azione", "?");
				sqlQueryObject.addInsertField("mode_azione", "?");
				sqlQueryObject.addInsertField("pattern_azione", "?");
				sqlQueryObject.addInsertField("nome_porta_delegante_azione", "?");
				sqlQueryObject.addInsertField("force_interface_based_azione", "?");
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
				sqlQueryObject.addInsertField("validazione_contenuti_stato", "?");
				sqlQueryObject.addInsertField("validazione_contenuti_tipo", "?");
				sqlQueryObject.addInsertField("validazione_contenuti_mtom", "?");
				sqlQueryObject.addInsertField("allega_body", "?");
				sqlQueryObject.addInsertField("scarta_body", "?");
				sqlQueryObject.addInsertField("gestione_manifest", "?");
				sqlQueryObject.addInsertField("stateless", "?");
				sqlQueryObject.addInsertField("behaviour", "?");
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
				// servizio applicativo default
				sqlQueryObject.addInsertField("id_sa_default", "?");
				// id
				sqlQueryObject.addInsertField("id_accordo", "?");
				sqlQueryObject.addInsertField("id_port_type", "?");
				sqlQueryObject.addInsertField("scadenza_correlazione_appl", "?");
				// options
				sqlQueryObject.addInsertField("options", "?");
				// canale
				sqlQueryObject.addInsertField("canale", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);
				
				int index = 1;
				
				stm.setString(index++, nomePorta);
				stm.setString(index++, descrizione);
				stm.setLong(index++, idSoggVirt);
				stm.setString(index++, tipoSoggVirt);//tipo sogg virt
				stm.setString(index++, nomeSoggVirt); //nome sogg virt
				stm.setLong(index++, idServizio);
				stm.setString(index++, tipoServizio);
				stm.setString(index++, nomeServizio);
				stm.setInt(index++, versioneServizio);
				stm.setString(index++, (azione != null ? azione.getNome() : null));
				if(modeAzione!=null){
					stm.setString(index++, modeAzione.toString());
				}
				else{
					stm.setString(index++, null);
				}
				stm.setString(index++, patternAzione);
				stm.setString(index++, nomePortaDeleganteAzione);
				stm.setString(index++, DriverConfigurazioneDBLib.getValue(forceInterfaceBased));
				// mtom
				stm.setString(index++, DriverConfigurazioneDBLib.getValue(mtomMode_request));
				stm.setString(index++, DriverConfigurazioneDBLib.getValue(mtomMode_response));
				// messageSecurity
				stm.setString(index++, messageSecurityStatus);
				stm.setString(index++, DriverConfigurazioneDBLib.getValue(messageSecurityApplyMtom_request));
				stm.setString(index++, DriverConfigurazioneDBLib.getValue(messageSecurityApplyMtom_response));
				stm.setString(index++, securityRequestMode);
				stm.setString(index++, securityResponseMode);
				// proprietario
				stm.setLong(index++, idProprietario);
				//ricevuta asincrona_asimmetrica/simmetrica
				stm.setString(index++, aPA.getRicevutaAsincronaSimmetrica()!=null ? DriverConfigurazioneDBLib.getValue(aPA.getRicevutaAsincronaSimmetrica()) : null);
				stm.setString(index++, aPA.getRicevutaAsincronaAsimmetrica()!=null ? DriverConfigurazioneDBLib.getValue(aPA.getRicevutaAsincronaAsimmetrica()) : null);
				//integrazione
				stm.setString(index++, aPA.getIntegrazione()!=null ? aPA.getIntegrazione() : null);
				//validazione xsd
				stm.setString(index++, aPA.getValidazioneContenutiApplicativi()!=null ? DriverConfigurazioneDBLib.getValue(aPA.getValidazioneContenutiApplicativi().getStato()) : null);
				stm.setString(index++, aPA.getValidazioneContenutiApplicativi()!=null ? DriverConfigurazioneDBLib.getValue(aPA.getValidazioneContenutiApplicativi().getTipo()) : null);
				stm.setString(index++, aPA.getValidazioneContenutiApplicativi()!=null ? DriverConfigurazioneDBLib.getValue(aPA.getValidazioneContenutiApplicativi().getAcceptMtomMessage()) : null);
				
				// InvocazionePorta: funzionalita' attachment
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDBLib.getValue(aPA.getAllegaBody()) : null);
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDBLib.getValue(aPA.getScartaBody()) : null);
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDBLib.getValue(aPA.getGestioneManifest()) : null);
				
				// Stateless
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDBLib.getValue(aPA.getStateless()) : null);
				
				// Behaviour
				stm.setString(index++, behaviour);
				
				// Autenticazione
				stm.setString(index++, autenticazione);
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDBLib.getValue(aPA.getAutenticazioneOpzionale()) : null);
				// token
				stm.setString(index++, gestioneToken!=null ? gestioneToken.getPolicy() : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDBLib.getValue(gestioneToken.getTokenOpzionale()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDBLib.getValue(gestioneToken.getValidazione()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDBLib.getValue(gestioneToken.getIntrospection()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDBLib.getValue(gestioneToken.getUserInfo()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDBLib.getValue(gestioneToken.getForward()) : null);
				stm.setString(index++, gestioneToken!=null ? gestioneToken.getOptions() : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDBLib.getValue(gestioneToken.getAutenticazione().getIssuer()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDBLib.getValue(gestioneToken.getAutenticazione().getClientId()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDBLib.getValue(gestioneToken.getAutenticazione().getSubject()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDBLib.getValue(gestioneToken.getAutenticazione().getUsername()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDBLib.getValue(gestioneToken.getAutenticazione().getEmail()) : null);
				// Autorizzazione
				stm.setString(index++, autorizzazione);
				stm.setString(index++, autorizzazioneXacmlPolicy);
				stm.setString(index++, aPA!=null ? aPA.getAutorizzazioneContenuto() : null);
				
				// Ruoli
				stm.setString(index++, aPA!=null && aPA.getRuoli()!=null && aPA.getRuoli().getMatch()!=null ? 
						aPA.getRuoli().getMatch().getValue() : null);
				
				// Token sa
				stm.setString(index++, aPA!=null && aPA.getAutorizzazioneToken()!=null && aPA.getAutorizzazioneToken().getAutorizzazioneApplicativi()!=null ? 
						DriverConfigurazioneDBLib.getValue(aPA.getAutorizzazioneToken().getAutorizzazioneApplicativi()) : null);
				
				// Token ruoli
				stm.setString(index++, aPA!=null && aPA.getAutorizzazioneToken()!=null && aPA.getAutorizzazioneToken().getAutorizzazioneRuoli()!=null ? 
						DriverConfigurazioneDBLib.getValue(aPA.getAutorizzazioneToken().getAutorizzazioneRuoli()) : null);
				stm.setString(index++, aPA!=null && aPA.getAutorizzazioneToken()!=null && aPA.getAutorizzazioneToken().getRuoli()!=null && 
						aPA.getAutorizzazioneToken().getRuoli().getMatch()!=null ? 
						aPA.getAutorizzazioneToken().getRuoli().getMatch().getValue() : null);
				stm.setString(index++, aPA!=null && aPA.getAutorizzazioneToken()!=null && aPA.getAutorizzazioneToken().getTipologiaRuoli()!=null ? 
						aPA.getAutorizzazioneToken().getTipologiaRuoli().getValue() : null);
				
				// Scope
				stm.setString(index++, aPA!=null && aPA.getScope()!=null && aPA.getScope().getStato()!=null ? 
						DriverConfigurazioneDBLib.getValue(aPA.getScope().getStato()) : null);
				stm.setString(index++, aPA!=null && aPA.getScope()!=null && aPA.getScope().getMatch()!=null ? 
						aPA.getScope().getMatch().getValue() : null);
				
				// RicercaPortaAzioneDelegata
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDBLib.getValue(aPA.getRicercaPortaAzioneDelegata()) : null);
				
				// Tracciamento
				stm.setString(index++, msg_diag_severita);
				stm.setString(index++, tracciamento_esiti);
				
				// Stato
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDBLib.getValue(aPA.getStato()) : null);
				
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
				
				// servizio applicativo default
				long idServizioApplicativoDefault = -1;
				if(aPA.getServizioApplicativoDefault()!=null) {
					idServizioApplicativoDefault = DBUtils.getIdServizioApplicativo(aPA.getServizioApplicativoDefault(), tipoProprietario, nomeProprietario, 
							con, DriverConfigurazioneDBLib.tipoDB,DriverConfigurazioneDBLib.tabellaSoggetti);
					if(idServizioApplicativoDefault<=0) {
						throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaApplicativa(CREATE)] Impossibile recuperare l'ID del servizio applicativo di default '"+aPA.getServizioApplicativoDefault()+"'.");
					}
				}
				stm.setLong(index++, idServizioApplicativoDefault);
				
				//idaccordo
				stm.setLong(index++, aPA.getIdAccordo()!=null ? aPA.getIdAccordo() : -1L);
				stm.setLong(index++, aPA.getIdPortType() !=null ? aPA.getIdPortType() : -1L);
				
				// ScadenzaCorrelazioneApplicativa
				stm.setString(index++, aPA.getCorrelazioneApplicativa()!=null ? aPA.getCorrelazioneApplicativa().getScadenza() : null);
				
				// options
				stm.setString(index++, aPA.getOptions());
				
				// canale
				stm.setString(index++, aPA.getCanale());
				
				n = stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Created " + n + " row(s)");

				// recupero l'id della porta applicativa appena inserita
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.addWhereCondition("nome_porta = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idProprietario);
				stm.setString(2, nomePorta);

				rs = stm.executeQuery();

				if (rs.next()) {
					idPortaApplicativa = rs.getLong("id");
					aPA.setId(idPortaApplicativa);
				} else {
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaApplicativa(CREATE)] Impossibile recuperare l'ID della PortaApplicativa appena create.");
				}
				rs.close();
				stm.close();
				
				
				if(mtomProcessor!=null){
					
					MtomProcessorFlowParameter reqParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_MTOM_REQUEST);
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
							stm.setLong(1, idPortaApplicativa);
							stm.setString(2, reqParam.getNome());
							stm.setString(3, reqParam.getPattern());
							stm.setString(4, reqParam.getContentType());
							stm.setInt(5, reqParam.getRequired() ? CostantiDB.TRUE : CostantiDB.FALSE);

							stm.executeUpdate();
						}	
					}
					stm.close();
					DriverConfigurazioneDBLib.log.debug("Inserted " + i + " mtom request flow con id=" + idPortaApplicativa);

					MtomProcessorFlowParameter resParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_MTOM_RESPONSE);
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
							stm.setLong(1, idPortaApplicativa);
							stm.setString(2, resParam.getNome());
							stm.setString(3, resParam.getPattern());
							stm.setString(4, resParam.getContentType());
							stm.setInt(5, resParam.getRequired() ? CostantiDB.TRUE : CostantiDB.FALSE);
	
							stm.executeUpdate();
						}
					}
					stm.close();
					DriverConfigurazioneDBLib.log.debug("Inserted " + i + " mtom response flow con id=" + idPortaApplicativa);
					
				}
				
				// se security abilitato setto la lista
				//if ((messageSecurity != null) && CostantiConfigurazione.ABILITATO.toString().equals(messageSecurityStatus) )  {
				// Devo settarli sempre se ci sono, in modo che lo switch abilitato-disabilitato funzioni
				if ((messageSecurity != null) )  {
					MessageSecurityFlowParameter reqParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST);
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
							stm.setLong(1, idPortaApplicativa);
							stm.setString(2, reqParam.getNome());
							stm.setString(3, reqParam.getValore());
							stm.executeUpdate();
						}
					}
					stm.close();
					DriverConfigurazioneDBLib.log.debug("Inserted " + i + " request flow con id=" + idPortaApplicativa);

					MessageSecurityFlowParameter resParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
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
							stm.setLong(1, idPortaApplicativa);
							stm.setString(2, resParam.getNome());
							stm.setString(3, resParam.getValore());
							stm.executeUpdate();
						}
					}
					stm.close();
					DriverConfigurazioneDBLib.log.debug("Inserted " + i + " response flow con id=" + idPortaApplicativa);
				}

				// la lista di correlazioni applicative contiene tutti e soli gli elementi necessari quindi resetto la lista nel db e riscrivo la lista nuova
				if (corrApp != null) {

					//inserisco i valori presenti nella lista 
					CorrelazioneApplicativaElemento cae = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome_elemento", "?");
					sqlQueryObject.addInsertField("mode_correlazione", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("identificazione_fallita", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					for (i = 0; i < corrApp.sizeElementoList(); i++) {
						cae = corrApp.getElemento(i);
						stm.setLong(1, idPortaApplicativa);
						stm.setString(2, cae.getNome());
						stm.setString(3, DriverConfigurazioneDBLib.getValue(cae.getIdentificazione()));
						if (cae.getPattern() != null)
							stm.setString(4, cae.getPattern());
						else
							stm.setString(4, "");
						stm.setString(5, DriverConfigurazioneDBLib.getValue(cae.getIdentificazioneFallita()));
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDBLib.log.debug("Inserted " + i + " correlazione applicativa con id=" + idPortaApplicativa);
				}
				
				// la lista di correlazioni applicative risposta contiene tutti e soli gli elementi necessari quindi resetto la lista nel db e riscrivo la lista nuova
				if (corrAppRisposta != null) {

					//inserisco i valori presenti nella lista 
					CorrelazioneApplicativaRispostaElemento cae = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE_RISPOSTA);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome_elemento", "?");
					sqlQueryObject.addInsertField("mode_correlazione", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("identificazione_fallita", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					for (i = 0; i < corrAppRisposta.sizeElementoList(); i++) {
						cae = corrAppRisposta.getElemento(i);
						stm.setLong(1, idPortaApplicativa);
						stm.setString(2, cae.getNome());
						stm.setString(3, DriverConfigurazioneDBLib.getValue(cae.getIdentificazione()));
						if (cae.getPattern() != null)
							stm.setString(4, cae.getPattern());
						else
							stm.setString(4, "");
						stm.setString(5, DriverConfigurazioneDBLib.getValue(cae.getIdentificazioneFallita()));
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDBLib.log.debug("Inserted " + i + " correlazione applicativa risposta con id=" + idPortaApplicativa);
				}
				
				// serviziapplicativi
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
				sqlQueryObject.addInsertField("connettore_nome", "?");
				sqlQueryObject.addInsertField("connettore_notifica", "?");
				sqlQueryObject.addInsertField("connettore_descrizione", "?");
				sqlQueryObject.addInsertField("connettore_stato", "?");
				sqlQueryObject.addInsertField("connettore_scheduling", "?");
				sqlQueryObject.addInsertField("connettore_filtri", "?");
				sqlQueryObject.addInsertField("connettore_coda", "?");
				sqlQueryObject.addInsertField("connettore_priorita", "?");
				sqlQueryObject.addInsertField("connettore_max_priorita", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);

				List<Long> idsPA_SA = new ArrayList<>();
				for (i = 0; i < aPA.sizeServizioApplicativoList(); i++) {
					PortaApplicativaServizioApplicativo servizioApplicativo = aPA.getServizioApplicativo(i);
					String nomeSA = servizioApplicativo.getNome();
					//nome/tipo soggetto proprietario servizio applicativo sono gli stessi della porta applicativa
					String nomeProprietarioSA = aPA.getNomeSoggettoProprietario();//servizioApplicativo.getNomeSoggettoProprietario();
					String tipoProprietarioSA = aPA.getTipoSoggettoProprietario();//servizioApplicativo.getTipoSoggettoProprietario();
					if (nomeSA == null || nomeSA.equals(""))
						throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)::Nome del ServizioApplicativo associato non valido.");
					if (nomeProprietarioSA == null || nomeProprietarioSA.equals(""))
						throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)::Nome Proprietario del ServizioApplicativo associato non valido.");
					if (tipoProprietarioSA == null || tipoProprietarioSA.equals(""))
						throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)::Tipo Proprietario del ServizioApplicativo associato non valido.");

					long idSA = DriverConfigurazioneDB_serviziApplicativiLIB.getIdServizioApplicativo(nomeSA, tipoProprietarioSA, nomeProprietarioSA, con, DriverConfigurazioneDBLib.tipoDB,DriverConfigurazioneDBLib.tabellaSoggetti);

					if (idSA <= 0)
						throw new DriverConfigurazioneException("Impossibile recuperare l'id del Servizio Applicativo [" + nomeSA + "] di [" + tipoProprietarioSA + "/" + nomeProprietarioSA + "]");

					idsPA_SA.add(idSA);
					
					int indexSA = 1;
					stm.setLong(indexSA++, idPortaApplicativa);
					stm.setLong(indexSA++, idSA);
					stm.setString(indexSA++, servizioApplicativo.getDatiConnettore()!=null ? servizioApplicativo.getDatiConnettore().getNome() : null);
					stm.setInt(indexSA++, servizioApplicativo.getDatiConnettore()!=null ? (servizioApplicativo.getDatiConnettore().isNotifica() ? CostantiDB.TRUE : CostantiDB.FALSE) : CostantiDB.FALSE);
					stm.setString(indexSA++, servizioApplicativo.getDatiConnettore()!=null ? servizioApplicativo.getDatiConnettore().getDescrizione() : null);
					stm.setString(indexSA++, servizioApplicativo.getDatiConnettore()!=null ? DriverConfigurazioneDBLib.getValue(servizioApplicativo.getDatiConnettore().getStato()) : null);
					stm.setString(indexSA++, servizioApplicativo.getDatiConnettore()!=null ? DriverConfigurazioneDBLib.getValue(servizioApplicativo.getDatiConnettore().getScheduling()) : null);
					
					String filtri = null; 
					if(servizioApplicativo.getDatiConnettore()!=null) {
						if(servizioApplicativo.getDatiConnettore().getFiltroList()!=null && servizioApplicativo.getDatiConnettore().sizeFiltroList()>0) {
							StringBuilder bf = new StringBuilder();
							for (int k = 0; k < servizioApplicativo.getDatiConnettore().sizeFiltroList(); k++) {
								if(k>0) {
									bf.append(",");
								}
								bf.append(servizioApplicativo.getDatiConnettore().getFiltro(k));
							}
							filtri = bf.toString();
						}
					}
					stm.setString(indexSA++, filtri);
					
					if(servizioApplicativo.getDatiConnettore()!=null) {
						stm.setString(indexSA++, servizioApplicativo.getDatiConnettore().getCoda()!=null ? servizioApplicativo.getDatiConnettore().getCoda() : CostantiConfigurazione.CODA_DEFAULT);
						stm.setString(indexSA++, servizioApplicativo.getDatiConnettore().getPriorita()!=null ? servizioApplicativo.getDatiConnettore().getPriorita() : CostantiConfigurazione.PRIORITA_DEFAULT);
						stm.setInt(indexSA++, servizioApplicativo.getDatiConnettore().isPrioritaMax() ? CostantiDB.TRUE : CostantiDB.FALSE);
					}
					else {
						stm.setString(indexSA++, CostantiConfigurazione.CODA_DEFAULT);
						stm.setString(indexSA++, CostantiConfigurazione.PRIORITA_DEFAULT);
						stm.setInt(indexSA++, CostantiDB.FALSE);
					}
					
					stm.executeUpdate();
					
				}
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Inseriti " + i + " servizi applicativi associati alla PortaApplicativa[" + idPortaApplicativa + "]");

				
				
				// serviziapplicativi props			
				for (i = 0; i < aPA.sizeServizioApplicativoList(); i++) {
					PortaApplicativaServizioApplicativo servizioApplicativo = aPA.getServizioApplicativo(i);
					String nomeSA = servizioApplicativo.getNome();
					
					if(servizioApplicativo.getDatiConnettore()!=null && servizioApplicativo.getDatiConnettore().sizeProprietaList()>0) {
					
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
						sqlQueryObject.addSelectField("id");
						sqlQueryObject.addWhereCondition("id_porta=?");
						sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
						sqlQueryObject.setANDLogicOperator(true);
						
						long idSA = idsPA_SA.get(i);
						
						long idPA_SA = -1;
						sqlQuery = sqlQueryObject.createSQLQuery();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idPortaApplicativa);
						stm.setLong(2, idSA);
						rs = stm.executeQuery();
						if(rs.next()) {
							idPA_SA = rs.getLong("id");
						}
						rs.close();
						stm.close();
						
						if(idPA_SA<=0) {
							throw new DriverConfigurazioneException("Impossibile recuperare l'id della registrazione del Servizio Applicativo [" + nomeSA + "](id:"+idSA+") alla porta applicativa con id:"+idPortaApplicativa );
						}
						
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_SA_PROPS);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("nome", "?");
						sqlQueryObject.addInsertField("valore", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						
						int j = 0;
						for ( ; j < servizioApplicativo.getDatiConnettore().sizeProprietaList(); j++) {
							
							Proprieta p = servizioApplicativo.getDatiConnettore().getProprieta(j);
							
							if(p.getValore()==null || "".equals(p.getValore())) {
								continue; // non devo serializzare valori vuoti o null (DB oracle non lo permette)
							}
							
							stm.setLong(1, idPA_SA);
							stm.setString(2, p.getNome());
							stm.setString(3, p.getValore());
							stm.executeUpdate();
							
						}
	
						stm.close();
						DriverConfigurazioneDBLib.log.debug("Inseriti " + j + " SetSAProp associati al Servizio Applicativo [" + nomeSA + "](id:"+idSA+") della PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				
				// set prop behaviour
				if(aPA.getBehaviour()!=null) {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_BEHAVIOUR_PROPS);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
					for (i = 0; i < aPA.getBehaviour().sizeProprietaList(); i++) {
						propProtocollo = aPA.getBehaviour().getProprieta(i);
						stm.setLong(1, idPortaApplicativa);
						stm.setString(2, propProtocollo.getNome());
						stm.setString(3, propProtocollo.getValore());
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDBLib.log.debug("Inseriti " + i + " SeBehaviourProp associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				}
				
				
				
				// set prop autenticazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("valore", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);
				for (i = 0; i < aPA.sizeProprietaAutenticazioneList(); i++) {
					propProtocollo = aPA.getProprietaAutenticazione(i);
					stm.setLong(1, idPortaApplicativa);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
				}
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Inseriti " + i + " SetProtocolPropAutenticazione associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				// set prop autorizzazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("valore", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);
				for (i = 0; i < aPA.sizeProprietaAutorizzazioneList(); i++) {
					propProtocollo = aPA.getProprietaAutorizzazione(i);
					stm.setLong(1, idPortaApplicativa);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
				}
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Inseriti " + i + " SetProtocolPropAutorizzazione associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				// set prop autorizzazione contenuti
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("valore", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);
				for (i = 0; i < aPA.sizeProprietaAutorizzazioneContenutoList(); i++) {
					propProtocollo = aPA.getProprietaAutorizzazioneContenuto(i);
					stm.setLong(1, idPortaApplicativa);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
				}
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Inseriti " + i + " SetProtocolPropAutorizzazioneContenuti associati alla PortaApplicativa[" + idPortaApplicativa + "]");
								
				
				
				// set prop rate limiting
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_RATE_LIMITING_PROP);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("valore", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);
				for (i = 0; i < aPA.sizeProprietaRateLimitingList(); i++) {
					propProtocollo = aPA.getProprietaRateLimiting(i);
					stm.setLong(1, idPortaApplicativa);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
				}
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Inseriti " + i + " SetProtocolPropRateLimiting associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				
				
				// set prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_PROP);
				sqlQueryObject.addInsertField("id_porta", "?");
				sqlQueryObject.addInsertField("nome", "?");
				sqlQueryObject.addInsertField("valore", "?");
				sqlQuery = sqlQueryObject.createSQLInsert();
				stm = con.prepareStatement(sqlQuery);
				for (i = 0; i < aPA.sizeProprietaList(); i++) {
					propProtocollo = aPA.getProprieta(i);
					stm.setLong(1, idPortaApplicativa);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
				}
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Inseriti " + i + " SetProtocolProp associati alla PortaApplicativa[" + idPortaApplicativa + "]");

				
				// Ruoli
				n=0;
				if(aPA.getRuoli()!=null && aPA.getRuoli().sizeRuoloList()>0){
					for (int j = 0; j < aPA.getRuoli().sizeRuoloList(); j++) {
						Ruolo ruolo = aPA.getRuoli().getRuolo(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_RUOLI);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("ruolo", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, aPA.getId());
						stm.setString(2, ruolo.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDBLib.log.debug("Aggiunto ruolo[" + ruolo.getNome() + "] alla PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				DriverConfigurazioneDBLib.log.debug("Aggiunti " + n + " ruoli alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				// Scope
				n=0;
				if(aPA.getScope()!=null && aPA.getScope().sizeScopeList()>0){
					for (int j = 0; j < aPA.getScope().sizeScopeList(); j++) {
						Scope scope = aPA.getScope().getScope(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_SCOPE);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("scope", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, aPA.getId());
						stm.setString(2, scope.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDBLib.log.debug("Aggiunto scope[" + scope.getNome() + "] alla PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				DriverConfigurazioneDBLib.log.debug("Aggiunti " + n + " scope alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				// Soggetti
				n=0;
				if(aPA.getSoggetti()!=null && aPA.getSoggetti().sizeSoggettoList()>0){
					for (int j = 0; j < aPA.getSoggetti().sizeSoggettoList(); j++) {
						PortaApplicativaAutorizzazioneSoggetto soggetto = aPA.getSoggetti().getSoggetto(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_SOGGETTI);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("tipo_soggetto", "?");
						sqlQueryObject.addInsertField("nome_soggetto", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, aPA.getId());
						stm.setString(2, soggetto.getTipo());
						stm.setString(3, soggetto.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDBLib.log.debug("Aggiunto soggetto [" + soggetto.getTipo() + "/"+soggetto.getNome()+"] alla PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				DriverConfigurazioneDBLib.log.debug("Aggiunti " + n + " soggetti alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				// serviziapplicativi autorizzati
				if(aPA.getServiziApplicativiAutorizzati()!=null && aPA.getServiziApplicativiAutorizzati().sizeServizioApplicativoList()>0) {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
	
					for (i = 0; i < aPA.getServiziApplicativiAutorizzati().sizeServizioApplicativoList(); i++) {
						PortaApplicativaAutorizzazioneServizioApplicativo servizioApplicativo = aPA.getServiziApplicativiAutorizzati().getServizioApplicativo(i);
						String nomeSA = servizioApplicativo.getNome();
						String nomeProprietarioSA = servizioApplicativo.getNomeSoggettoProprietario();
						String tipoProprietarioSA = servizioApplicativo.getTipoSoggettoProprietario();
						if (nomeSA == null || nomeSA.equals(""))
							throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)[Auth]::Nome del ServizioApplicativo associato non valido.");
						if (nomeProprietarioSA == null || nomeProprietarioSA.equals(""))
							throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)[Auth]::Nome Proprietario del ServizioApplicativo associato non valido.");
						if (tipoProprietarioSA == null || tipoProprietarioSA.equals(""))
							throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)[Auth]::Tipo Proprietario del ServizioApplicativo associato non valido.");
	
						long idSA = DriverConfigurazioneDB_serviziApplicativiLIB.getIdServizioApplicativo(nomeSA, tipoProprietarioSA, nomeProprietarioSA, con, DriverConfigurazioneDBLib.tipoDB,DriverConfigurazioneDBLib.tabellaSoggetti);
	
						if (idSA <= 0)
							throw new DriverConfigurazioneException("Impossibile recuperare l'id del Servizio Applicativo [" + nomeSA + "] di [" + tipoProprietarioSA + "/" + nomeProprietarioSA + "]");
	
						stm.setLong(1, idPortaApplicativa);
						stm.setLong(2, idSA);
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDBLib.log.debug("Inseriti " + i + " servizi applicativi autorizzati associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				}
				
				
				// serviziapplicativi token autorizzati
				if(aPA.getAutorizzazioneToken()!=null && aPA.getAutorizzazioneToken().getServiziApplicativi()!=null &&
						aPA.getAutorizzazioneToken().getServiziApplicativi().sizeServizioApplicativoList()>0) {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_TOKEN_SA);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
	
					for (i = 0; i < aPA.getAutorizzazioneToken().getServiziApplicativi().sizeServizioApplicativoList(); i++) {
						PortaApplicativaAutorizzazioneServizioApplicativo servizioApplicativo = aPA.getAutorizzazioneToken().getServiziApplicativi().getServizioApplicativo(i);
						String nomeSA = servizioApplicativo.getNome();
						String nomeProprietarioSA = servizioApplicativo.getNomeSoggettoProprietario();
						String tipoProprietarioSA = servizioApplicativo.getTipoSoggettoProprietario();
						if (nomeSA == null || nomeSA.equals(""))
							throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)[TokenAuth]::Nome del ServizioApplicativo associato non valido.");
						if (nomeProprietarioSA == null || nomeProprietarioSA.equals(""))
							throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)[TokenAuth]::Nome Proprietario del ServizioApplicativo associato non valido.");
						if (tipoProprietarioSA == null || tipoProprietarioSA.equals(""))
							throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)[TokenAuth]::Tipo Proprietario del ServizioApplicativo associato non valido.");
	
						long idSA = DriverConfigurazioneDB_serviziApplicativiLIB.getIdServizioApplicativo(nomeSA, tipoProprietarioSA, nomeProprietarioSA, con, DriverConfigurazioneDBLib.tipoDB,DriverConfigurazioneDBLib.tabellaSoggetti);
	
						if (idSA <= 0)
							throw new DriverConfigurazioneException("Impossibile recuperare l'id del Servizio Applicativo [" + nomeSA + "] di [" + tipoProprietarioSA + "/" + nomeProprietarioSA + "]");
	
						stm.setLong(1, idPortaApplicativa);
						stm.setLong(2, idSA);
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDBLib.log.debug("Inseriti " + i + " servizi applicativi autorizzati (token) associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				}
				
				
				// Ruoli (token)
				n=0;
				if(aPA.getAutorizzazioneToken()!=null && aPA.getAutorizzazioneToken().getRuoli()!=null && aPA.getAutorizzazioneToken().getRuoli().sizeRuoloList()>0){
					for (int j = 0; j < aPA.getAutorizzazioneToken().getRuoli().sizeRuoloList(); j++) {
						Ruolo ruolo = aPA.getAutorizzazioneToken().getRuoli().getRuolo(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_TOKEN_RUOLI);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("ruolo", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, aPA.getId());
						stm.setString(2, ruolo.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDBLib.log.debug("Aggiunto ruolo[" + ruolo.getNome() + "] (token) alla PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				DriverConfigurazioneDBLib.log.debug("Aggiunti " + n + " ruoli (token) alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				// Azioni
				n=0;
				if(aPA.getAzione()!=null && aPA.getAzione().sizeAzioneDelegataList()>0){
					for (int j = 0; j < aPA.getAzione().sizeAzioneDelegataList(); j++) {
						String azioneDelegata = aPA.getAzione().getAzioneDelegata(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_AZIONI);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("azione", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, aPA.getId());
						stm.setString(2, azioneDelegata);
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDBLib.log.debug("Aggiunto azione delegata [" + azioneDelegata + "] alla PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				DriverConfigurazioneDBLib.log.debug("Aggiunte " + n + " azioni delegate alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				// Cache Regole
				n=0;
				if(response_cache_regole!=null && response_cache_regole.size()>0){
					for (int j = 0; j < response_cache_regole.size(); j++) {
						ResponseCachingConfigurazioneRegola regola = response_cache_regole.get(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_CACHE_REGOLE);
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
						stm.setLong(indexCR++, aPA.getId());
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
						DriverConfigurazioneDBLib.log.debug("Aggiunta regola di cache alla PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				DriverConfigurazioneDBLib.log.debug("Aggiunte " + n + " regole di cache alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				// AttributeAuthority
				n=0;
				if(aPA.sizeAttributeAuthorityList()>0){
					for (int j = 0; j < aPA.sizeAttributeAuthorityList(); j++) {
						AttributeAuthority aa = aPA.getAttributeAuthority(j);
						
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
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_ATTRIBUTE_AUTHORITY);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("nome", "?");
						sqlQueryObject.addInsertField("attributi", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, aPA.getId());
						stm.setString(2, aa.getNome());
						stm.setString(3, attributi);
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDBLib.log.debug("Aggiunto A.A.[" + aa.getNome() + "] alla PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				DriverConfigurazioneDBLib.log.debug("Aggiunte " + n + " A.A. alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				
				// dumpConfigurazione
				DriverConfigurazioneDB_dumpLIB.CRUDDumpConfigurazione(type, con, aPA.getDump(), aPA.getId(), CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_PA);
				
				
				// trasformazioni
				DriverConfigurazioneDBTrasformazioniLib.CRUDTrasformazioni(type, con, aPA.getTrasformazioni(), aPA.getId(), false);
				
								
				// handlers
				if(configHandlers!=null) {
					if(configHandlers.getRequest()!=null) {
						DriverConfigurazioneDB_handlerLIB.CRUDConfigurazioneMessageHandlers(type, con, null, aPA.getId(), true, configHandlers.getRequest());
					}
					if(configHandlers.getResponse()!=null) {
						DriverConfigurazioneDB_handlerLIB.CRUDConfigurazioneMessageHandlers(type, con, null, aPA.getId(), false, configHandlers.getResponse());
					}
				}
			
				
				// extendedInfo
				i=0;
				if(aPA.sizeExtendedInfoList()>0){
					if(extInfoConfigurazioneDriver!=null){
						for (i = 0; i < aPA.sizeExtendedInfoList(); i++) {
							extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDBLib.log, aPA, aPA.getExtendedInfo(i), CRUDType.CREATE);
						}
					}
				}
				DriverConfigurazioneDBLib.log.debug("Aggiunte " + i + " associazioni ExtendedInfo<->PortaApplicativa associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				break;

			case UPDATE:
				// UPDATE
				String oldNomePA = null;
				if(aPA.getOldIDPortaApplicativaForUpdate()!=null){
					oldNomePA = aPA.getOldIDPortaApplicativaForUpdate().getNome();
				}
				if (oldNomePA == null || oldNomePA.equals(""))
					oldNomePA = nomePorta;

				//campi obbligatori
				//servizio ci deve essere l'id oppure tipo e nome
				tipoServizio = (servizio != null ? servizio.getTipo() : null);
				nomeServizio = (servizio != null ? servizio.getNome() : null);
				versioneServizio = (servizio != null ? servizio.getVersione() : null);
				//se l'id non e' valido allora devono esserci necessariamente il tipo e il nome
				if(idServizio<=0){
					if(tipoServizio==null || tipoServizio.equals("")) throw new DriverConfigurazioneException("Tipo Servizio non impostato.");
					if(nomeServizio==null || nomeServizio.equals("")) throw new DriverConfigurazioneException("Nome Servizio non impostato.");
					if(versioneServizio==null) throw new DriverConfigurazioneException("Versione Servizio non impostato.");
				}
				
				//Azione
				nomeAzione = (azione != null ? azione.getNome() : null);
				patternAzione = (azione != null ? azione.getPattern() : null);
				nomePortaDeleganteAzione = (azione != null ? azione.getNomePortaDelegante() : null);
				forceInterfaceBased = (azione != null ? azione.getForceInterfaceBased() : null);
				modeAzione = (azione != null ? azione.getIdentificazione() : null);
				//Se il bean Azione nn e' presente allora non controllo nulla
				if(azione!=null){
					if(modeAzione==null || modeAzione.equals("")) 
						modeAzione = PortaApplicativaAzioneIdentificazione.STATIC;
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
					case PROTOCOL_BASED:
						//nessun campo obbligatorio
						break;
					case STATIC:
						//ci deve essere il nome
						if(nomeAzione==null || nomeAzione.equals("")) throw new DriverConfigurazioneException("Nome Azione non impostato.");
						patternAzione=null;
						break;
					default:
						break;
					}
				}

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addUpdateTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addUpdateField("descrizione", "?");
				sqlQueryObject.addUpdateField("id_soggetto_virtuale", "?");
				sqlQueryObject.addUpdateField("tipo_soggetto_virtuale", "?");
				sqlQueryObject.addUpdateField("nome_soggetto_virtuale", "?");
				sqlQueryObject.addUpdateField("id_servizio", "?");
				sqlQueryObject.addUpdateField("tipo_servizio", "?");
				sqlQueryObject.addUpdateField("servizio", "?");
				sqlQueryObject.addUpdateField("versione_servizio", "?");
				sqlQueryObject.addUpdateField("azione", "?");
				sqlQueryObject.addUpdateField("mode_azione", "?");
				sqlQueryObject.addUpdateField("pattern_azione", "?");
				sqlQueryObject.addUpdateField("nome_porta_delegante_azione", "?");
				sqlQueryObject.addUpdateField("force_interface_based_azione", "?");
				sqlQueryObject.addUpdateField("mtom_request_mode", "?");
				sqlQueryObject.addUpdateField("mtom_response_mode", "?");
				sqlQueryObject.addUpdateField("security", "?");
				sqlQueryObject.addUpdateField("security_mtom_req", "?");
				sqlQueryObject.addUpdateField("security_mtom_res", "?");
				sqlQueryObject.addUpdateField("security_request_mode", "?");
				sqlQueryObject.addUpdateField("security_response_mode", "?");
				sqlQueryObject.addUpdateField("nome_porta", "?");
				sqlQueryObject.addUpdateField("ricevuta_asincrona_sim", "?");
				sqlQueryObject.addUpdateField("ricevuta_asincrona_asim", "?");
				sqlQueryObject.addUpdateField("integrazione", "?");
				sqlQueryObject.addUpdateField("validazione_contenuti_stato", "?");
				sqlQueryObject.addUpdateField("validazione_contenuti_tipo", "?");
				sqlQueryObject.addUpdateField("validazione_contenuti_mtom", "?");	
				sqlQueryObject.addUpdateField("id_soggetto", "?");
				sqlQueryObject.addUpdateField("allega_body", "?");
				sqlQueryObject.addUpdateField("scarta_body", "?");
				sqlQueryObject.addUpdateField("gestione_manifest", "?");
				sqlQueryObject.addUpdateField("stateless", "?");
				sqlQueryObject.addUpdateField("behaviour", "?");
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
				// servizio applicativo default
				sqlQueryObject.addUpdateField("id_sa_default", "?");
				// id
				sqlQueryObject.addUpdateField("id_accordo", "?");
				sqlQueryObject.addUpdateField("id_port_type", "?");
				sqlQueryObject.addUpdateField("scadenza_correlazione_appl", "?");
				// options
				sqlQueryObject.addUpdateField("options", "?");
				// canale
				sqlQueryObject.addUpdateField("canale", "?");
				
				sqlQueryObject.addWhereCondition("nome_porta=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLUpdate();
				stm = con.prepareStatement(sqlQuery);

				idPortaApplicativa = DBUtils.getIdPortaApplicativa(oldNomePA, con, DriverConfigurazioneDBLib.tipoDB);
				//  Puo' darsi che l'old soggetto e il nuovo soggetto siano la stesso soggetto della tabella. E' stato cambiato il nome.
				if(idPortaApplicativa<=0) {
					idPortaApplicativa = DBUtils.getIdPortaApplicativa(oldNomePA, con, DriverConfigurazioneDBLib.tipoDB);
				}
				if(idPortaApplicativa<=0) 
					throw new DriverConfigurazioneException("Impossibile recuperare l'id della Porta Applicativa nomePA["+oldNomePA+"] (old["+
							(aPA.getOldIDPortaApplicativaForUpdate()!=null?aPA.getOldIDPortaApplicativaForUpdate().getNome():null)+"]");
										
				index = 1;
				
				stm.setString(index++, descrizione);
				stm.setLong(index++, idSoggVirt);
				stm.setString(index++, tipoSoggVirt);//(soggVirt != null ? soggVirt.getTipo() : null));
				stm.setString(index++, nomeSoggVirt);//(soggVirt != null ? soggVirt.getNome() : null));
				stm.setLong(index++, idServizio);
				stm.setString(index++, tipoServizio);
				stm.setString(index++, nomeServizio);
				stm.setInt(index++, versioneServizio);
				stm.setString(index++, (azione != null ? azione.getNome() : null));
				if(modeAzione!=null){
					stm.setString(index++, modeAzione.toString());
				}
				else{
					stm.setString(index++, null);
				}
				stm.setString(index++, patternAzione);
				stm.setString(index++, nomePortaDeleganteAzione);
				stm.setString(index++, DriverConfigurazioneDBLib.getValue(forceInterfaceBased));
				// mtom
				stm.setString(index++, DriverConfigurazioneDBLib.getValue(mtomMode_request));
				stm.setString(index++, DriverConfigurazioneDBLib.getValue(mtomMode_response));
				// messageSecurity
				stm.setString(index++, messageSecurityStatus);
				stm.setString(index++, DriverConfigurazioneDBLib.getValue(messageSecurityApplyMtom_request));
				stm.setString(index++, DriverConfigurazioneDBLib.getValue(messageSecurityApplyMtom_response));
				stm.setString(index++, securityRequestMode);
				stm.setString(index++, securityResponseMode);
				// nomePorta
				stm.setString(index++, nomePorta);
				//ricevuta asincrona_asimmetrica/simmetrica
				stm.setString(index++, aPA.getRicevutaAsincronaSimmetrica()!=null ? DriverConfigurazioneDBLib.getValue(aPA.getRicevutaAsincronaSimmetrica()) : null);
				stm.setString(index++, aPA.getRicevutaAsincronaAsimmetrica()!=null ? DriverConfigurazioneDBLib.getValue(aPA.getRicevutaAsincronaAsimmetrica()) : null);
				//integrazione
				stm.setString(index++, aPA.getIntegrazione()!=null ? aPA.getIntegrazione() : null);
				//validazione xsd
				stm.setString(index++, aPA.getValidazioneContenutiApplicativi()!=null ? DriverConfigurazioneDBLib.getValue(aPA.getValidazioneContenutiApplicativi().getStato()) : null);
				stm.setString(index++, aPA.getValidazioneContenutiApplicativi()!=null ? DriverConfigurazioneDBLib.getValue(aPA.getValidazioneContenutiApplicativi().getTipo()) : null);
				stm.setString(index++, aPA.getValidazioneContenutiApplicativi()!=null ? DriverConfigurazioneDBLib.getValue(aPA.getValidazioneContenutiApplicativi().getAcceptMtomMessage()) : null);
				stm.setLong(index++, idProprietario);//il nuovo proprietario se cambiato
				// InvocazionePorta: funzionalita' attachment
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDBLib.getValue(aPA.getAllegaBody()) : null);
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDBLib.getValue(aPA.getScartaBody()) : null);
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDBLib.getValue(aPA.getGestioneManifest()) : null);
				// Stateless
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDBLib.getValue(aPA.getStateless()) : null);
				// Behaviour
				stm.setString(index++, behaviour);
				// Autenticazione
				stm.setString(index++, autenticazione);
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDBLib.getValue(aPA.getAutenticazioneOpzionale()) : null);
				// token
				stm.setString(index++, gestioneToken!=null ? gestioneToken.getPolicy() : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDBLib.getValue(gestioneToken.getTokenOpzionale()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDBLib.getValue(gestioneToken.getValidazione()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDBLib.getValue(gestioneToken.getIntrospection()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDBLib.getValue(gestioneToken.getUserInfo()) : null);
				stm.setString(index++, gestioneToken!=null ? DriverConfigurazioneDBLib.getValue(gestioneToken.getForward()) : null);
				stm.setString(index++, gestioneToken!=null ? gestioneToken.getOptions() : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDBLib.getValue(gestioneToken.getAutenticazione().getIssuer()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDBLib.getValue(gestioneToken.getAutenticazione().getClientId()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDBLib.getValue(gestioneToken.getAutenticazione().getSubject()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDBLib.getValue(gestioneToken.getAutenticazione().getUsername()) : null);
				stm.setString(index++, (gestioneToken!=null && gestioneToken.getAutenticazione()!=null) ? 
						DriverConfigurazioneDBLib.getValue(gestioneToken.getAutenticazione().getEmail()) : null);
				// Autorizzazione
				stm.setString(index++, autorizzazione);
				stm.setString(index++, autorizzazioneXacmlPolicy);
				stm.setString(index++, aPA!=null ? aPA.getAutorizzazioneContenuto() : null);
				// Ruoli
				stm.setString(index++, aPA!=null && aPA.getRuoli()!=null && aPA.getRuoli().getMatch()!=null ? 
						aPA.getRuoli().getMatch().getValue() : null);
				// Token sa
				stm.setString(index++, aPA!=null && aPA.getAutorizzazioneToken()!=null && aPA.getAutorizzazioneToken().getAutorizzazioneApplicativi()!=null ? 
						DriverConfigurazioneDBLib.getValue(aPA.getAutorizzazioneToken().getAutorizzazioneApplicativi()) : null);	
				// Token ruoli
				stm.setString(index++, aPA!=null && aPA.getAutorizzazioneToken()!=null && aPA.getAutorizzazioneToken().getAutorizzazioneRuoli()!=null ? 
						DriverConfigurazioneDBLib.getValue(aPA.getAutorizzazioneToken().getAutorizzazioneRuoli()) : null);
				stm.setString(index++, aPA!=null && aPA.getAutorizzazioneToken()!=null && aPA.getAutorizzazioneToken().getRuoli()!=null && 
						aPA.getAutorizzazioneToken().getRuoli().getMatch()!=null ? 
						aPA.getAutorizzazioneToken().getRuoli().getMatch().getValue() : null);
				stm.setString(index++, aPA!=null && aPA.getAutorizzazioneToken()!=null && aPA.getAutorizzazioneToken().getTipologiaRuoli()!=null ? 
						aPA.getAutorizzazioneToken().getTipologiaRuoli().getValue() : null);
				// Scope
				stm.setString(index++, aPA!=null && aPA.getScope()!=null && aPA.getScope().getStato()!=null ? 
						DriverConfigurazioneDBLib.getValue(aPA.getScope().getStato()) : null);
				stm.setString(index++, aPA!=null && aPA.getScope()!=null && aPA.getScope().getMatch()!=null ? 
						aPA.getScope().getMatch().getValue() : null);
				// RicercaPortaAzioneDelegata
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDBLib.getValue(aPA.getRicercaPortaAzioneDelegata()) : null);
				// Tracciamento
				stm.setString(index++, msg_diag_severita);
				stm.setString(index++, tracciamento_esiti);
				// Stato
				stm.setString(index++, aPA!=null ? DriverConfigurazioneDBLib.getValue(aPA.getStato()) : null);
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
				
				// servizio applicativo default
				idServizioApplicativoDefault = -1;
				if(aPA.getServizioApplicativoDefault()!=null) {
					idServizioApplicativoDefault = DBUtils.getIdServizioApplicativo(aPA.getServizioApplicativoDefault(), tipoProprietario, nomeProprietario, 
							con, DriverConfigurazioneDBLib.tipoDB,DriverConfigurazioneDBLib.tabellaSoggetti);
					if(idServizioApplicativoDefault<=0) {
						throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaApplicativa(CREATE)] Impossibile recuperare l'ID del servizio applicativo di default '"+aPA.getServizioApplicativoDefault()+"'.");
					}
				}
				stm.setLong(index++, idServizioApplicativoDefault);
				
				// id
				stm.setLong(index++, aPA.getIdAccordo() !=null ? aPA.getIdAccordo() : -1L);
				stm.setLong(index++, aPA.getIdPortType() !=null ? aPA.getIdPortType() : -1L);
				// ScadenzaCorrelazioneApplicativa
				stm.setString(index++, aPA.getCorrelazioneApplicativa()!=null ? aPA.getCorrelazioneApplicativa().getScadenza() : null);
				// options
				stm.setString(index++, aPA.getOptions());
				// canale
				stm.setString(index++, aPA.getCanale());
				
				// where
				stm.setString(index++, oldNomePA);

				n = stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Updated " + n + " row(s).");

				
				
				// mtom
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MTOM_REQUEST);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				stm.executeUpdate();
				stm.close();

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MTOM_RESPONSE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				stm.executeUpdate();
				stm.close();
				
				if(mtomProcessor!=null){
				
					MtomProcessorFlowParameter reqParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_MTOM_REQUEST);
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
							stm.setLong(1, idPortaApplicativa);
							stm.setString(2, reqParam.getNome());
							stm.setString(3, reqParam.getPattern());
							stm.setString(4, reqParam.getContentType());
							stm.setInt(5, reqParam.getRequired() ? CostantiDB.TRUE : CostantiDB.FALSE);
	
							stm.executeUpdate();
						}	
					}
					stm.close();
					DriverConfigurazioneDBLib.log.debug("Inserted " + i + " mtom request flow con id=" + idPortaApplicativa);
	
					MtomProcessorFlowParameter resParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_MTOM_RESPONSE);
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
							stm.setLong(1, idPortaApplicativa);
							stm.setString(2, resParam.getNome());
							stm.setString(3, resParam.getPattern());
							stm.setString(4, resParam.getContentType());
							stm.setInt(5, resParam.getRequired() ? CostantiDB.TRUE : CostantiDB.FALSE);
	
							stm.executeUpdate();
						}
					}
					stm.close();
					DriverConfigurazioneDBLib.log.debug("Inserted " + i + " mtom response flow con id=" + idPortaApplicativa);
					
				}
				
				
				// se security abilitato setto la lista
				// la lista contiene tutti e soli gli elementi necessari quindi resetto la lista nel db e riscrivo la lista nuova
				//if ((messageSecurity != null) && CostantiConfigurazione.ABILITATO.toString().equals(messageSecurityStatus) )  {
				// Devo settarli sempre se ci sono, in modo che lo switch abilitato-disabilitato funzioni
				if ((messageSecurity != null) )  {

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST);
					sqlQueryObject.addWhereCondition("id_porta=?");
					sqlQuery = sqlQueryObject.createSQLDelete();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, idPortaApplicativa);
					stm.executeUpdate();
					stm.close();

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
					sqlQueryObject.addWhereCondition("id_porta=?");
					sqlQuery = sqlQueryObject.createSQLDelete();
					stm = con.prepareStatement(sqlQuery);
					stm.setLong(1, idPortaApplicativa);
					stm.executeUpdate();
					stm.close();

					//inserisco i valori presenti nella lista 
					MessageSecurityFlowParameter reqParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST);
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
							stm.setLong(1, idPortaApplicativa);
							stm.setString(2, reqParam.getNome());
							stm.setString(3, reqParam.getValore());
							stm.executeUpdate();
						}
					}
					stm.close();
					DriverConfigurazioneDBLib.log.debug("Inserted " + i + " request flow con id=" + idPortaApplicativa);

					MessageSecurityFlowParameter resParam = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
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
							stm.setLong(1, idPortaApplicativa);
							stm.setString(2, resParam.getNome());
							stm.setString(3, resParam.getValore());
							stm.executeUpdate();
						}
					}
					stm.close();
					DriverConfigurazioneDBLib.log.debug("Inserted " + i + " response flow con id=" + idPortaApplicativa);
				}

				
				// la lista di correlazioni applicative contiene tutti e soli gli elementi necessari quindi resetto la lista nel db e riscrivo la lista nuova
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				stm.executeUpdate();
				stm.close();
				
				if (corrApp != null) {
					//inserisco i valori presenti nella lista 
					CorrelazioneApplicativaElemento cae = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome_elemento", "?");
					sqlQueryObject.addInsertField("mode_correlazione", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("identificazione_fallita", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					for (i = 0; i < corrApp.sizeElementoList(); i++) {
						cae = corrApp.getElemento(i);
						stm.setLong(1, idPortaApplicativa);
						stm.setString(2, cae.getNome());
						stm.setString(3, DriverConfigurazioneDBLib.getValue(cae.getIdentificazione()));
						if (cae.getPattern() != null)
							stm.setString(4, cae.getPattern());
						else
							stm.setString(4, "");
						stm.setString(5, DriverConfigurazioneDBLib.getValue(cae.getIdentificazioneFallita()));
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDBLib.log.debug("Inserted " + i + " correlazione applicativa con id=" + idPortaApplicativa);
				}
				
				// la lista di correlazioni applicative risposta contiene tutti e soli gli elementi necessari quindi resetto la lista nel db e riscrivo la lista nuova
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				stm.executeUpdate();
				stm.close();
				
				if (corrAppRisposta != null) {
					//inserisco i valori presenti nella lista 
					CorrelazioneApplicativaRispostaElemento cae = null;
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE_RISPOSTA);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome_elemento", "?");
					sqlQueryObject.addInsertField("mode_correlazione", "?");
					sqlQueryObject.addInsertField("pattern", "?");
					sqlQueryObject.addInsertField("identificazione_fallita", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					for (i = 0; i < corrAppRisposta.sizeElementoList(); i++) {
						cae = corrAppRisposta.getElemento(i);
						stm.setLong(1, idPortaApplicativa);
						stm.setString(2, cae.getNome());
						stm.setString(3, DriverConfigurazioneDBLib.getValue(cae.getIdentificazione()));
						if (cae.getPattern() != null)
							stm.setString(4, cae.getPattern());
						else
							stm.setString(4, "");
						stm.setString(5, DriverConfigurazioneDBLib.getValue(cae.getIdentificazioneFallita()));
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDBLib.log.debug("Inserted " + i + " correlazione applicativa risposta con id=" + idPortaApplicativa);
				}
		
				
				/*Sincronizzazione servizi applicativi*/
				//la lista dei servizi applicativi passata contiene tutti e soli i servizi applicativi necessari
				//quindi nel db devono essere presenti tutti e solo quelli presenti nella lista

				
				
				// serviziapplicativi props			
				
				idsPA_SA = new ArrayList<>(); 
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs = stm.executeQuery();
				while(rs.next()) {
					idsPA_SA.add(rs.getLong("id"));
				}
				rs.close();
				stm.close();
				
				if(!idsPA_SA.isEmpty()) {
					for (Long idsapa : idsPA_SA) {
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SA_PROPS);
						sqlQueryObject.addWhereCondition("id_porta=?");
						sqlQuery = sqlQueryObject.createSQLDelete();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idsapa);
						n=stm.executeUpdate();
						stm.close();
						DriverConfigurazioneDBLib.log.debug("Eliminate "+n+" proprieta relative all'associazione '"+idsapa+"' (Porta Applicativa "+idPortaApplicativa+")");
					}
				}
				
				
				//TODO possibile ottimizzazione in termini di tempo
				//cancello i servizi applicativi associati alla porta e inserisco tutti e soli quelli presenti in lista
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Cancellati "+n+" servizi applicativi associati alla Porta Applicativa "+idPortaApplicativa);
				
				
				
				//scrivo la lista nel db
				n=0;
				idsPA_SA = new ArrayList<>();
				for (i = 0; i < aPA.sizeServizioApplicativoList(); i++) {
					PortaApplicativaServizioApplicativo servizioApplicativo = aPA.getServizioApplicativo(i);

					String nomeSA = servizioApplicativo.getNome();
					//il tipo e il nome proprietario servizio applicativo sono gli stessi della porta delegata
					String nomeProprietarioSA = aPA.getNomeSoggettoProprietario(); 
					String tipoProprietarioSA = aPA.getTipoSoggettoProprietario(); 
					if (nomeSA == null || nomeSA.equals(""))
						throw new DriverConfigurazioneException("Nome del ServizioApplicativo associato non valido.");
					if (nomeProprietarioSA == null || nomeProprietarioSA.equals(""))
						throw new DriverConfigurazioneException("Nome Proprietario del ServizioApplicativo associato non valido.");
					if (tipoProprietarioSA == null || tipoProprietarioSA.equals(""))
						throw new DriverConfigurazioneException("Tipo Proprietario del ServizioApplicativo associato non valido.");

					long idSA = DriverConfigurazioneDB_serviziApplicativiLIB.getIdServizioApplicativo(nomeSA, tipoProprietarioSA, nomeProprietarioSA, con, DriverConfigurazioneDBLib.tipoDB,DriverConfigurazioneDBLib.tabellaSoggetti);

					if (idSA <= 0)
						throw new DriverConfigurazioneException("Impossibile recuperare l'id del Servizio Applicativo [" + nomeSA + "] di [" + tipoProprietarioSA + "/" + nomeProprietarioSA + "]");

					idsPA_SA.add(idSA);
					
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_SA);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
					sqlQueryObject.addInsertField("connettore_nome", "?");
					sqlQueryObject.addInsertField("connettore_notifica", "?");
					sqlQueryObject.addInsertField("connettore_descrizione", "?");
					sqlQueryObject.addInsertField("connettore_stato", "?");
					sqlQueryObject.addInsertField("connettore_scheduling", "?");
					sqlQueryObject.addInsertField("connettore_filtri", "?");
					sqlQueryObject.addInsertField("connettore_coda", "?");
					sqlQueryObject.addInsertField("connettore_priorita", "?");
					sqlQueryObject.addInsertField("connettore_max_priorita", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
				
					int indexSA = 1;
					stm.setLong(indexSA++, idPortaApplicativa);
					stm.setLong(indexSA++, idSA);
					stm.setString(indexSA++, servizioApplicativo.getDatiConnettore()!=null ? servizioApplicativo.getDatiConnettore().getNome() : null);
					stm.setInt(indexSA++, servizioApplicativo.getDatiConnettore()!=null ? (servizioApplicativo.getDatiConnettore().isNotifica() ? CostantiDB.TRUE : CostantiDB.FALSE) : CostantiDB.FALSE);
					stm.setString(indexSA++, servizioApplicativo.getDatiConnettore()!=null ? servizioApplicativo.getDatiConnettore().getDescrizione() : null);
					stm.setString(indexSA++, servizioApplicativo.getDatiConnettore()!=null ? DriverConfigurazioneDBLib.getValue(servizioApplicativo.getDatiConnettore().getStato()) : null);
					stm.setString(indexSA++, servizioApplicativo.getDatiConnettore()!=null ? DriverConfigurazioneDBLib.getValue(servizioApplicativo.getDatiConnettore().getScheduling()) : null);
					
					String filtri = null; 
					if(servizioApplicativo.getDatiConnettore()!=null) {
						if(servizioApplicativo.getDatiConnettore().getFiltroList()!=null && servizioApplicativo.getDatiConnettore().sizeFiltroList()>0) {
							StringBuilder bf = new StringBuilder();
							for (int k = 0; k < servizioApplicativo.getDatiConnettore().sizeFiltroList(); k++) {
								if(k>0) {
									bf.append(",");
								}
								bf.append(servizioApplicativo.getDatiConnettore().getFiltro(k));
							}
							filtri = bf.toString();
						}
					}
					stm.setString(indexSA++, filtri);
					
					if(servizioApplicativo.getDatiConnettore()!=null) {
						stm.setString(indexSA++, servizioApplicativo.getDatiConnettore().getCoda()!=null ? servizioApplicativo.getDatiConnettore().getCoda() : CostantiConfigurazione.CODA_DEFAULT);
						stm.setString(indexSA++, servizioApplicativo.getDatiConnettore().getPriorita()!=null ? servizioApplicativo.getDatiConnettore().getPriorita() : CostantiConfigurazione.PRIORITA_DEFAULT);
						stm.setInt(indexSA++, servizioApplicativo.getDatiConnettore().isPrioritaMax() ? CostantiDB.TRUE : CostantiDB.FALSE);
					}
					else {
						stm.setString(indexSA++, CostantiConfigurazione.CODA_DEFAULT);
						stm.setString(indexSA++, CostantiConfigurazione.PRIORITA_DEFAULT);
						stm.setInt(indexSA++, CostantiDB.FALSE);
					}

					stm.executeUpdate();
					stm.close();
					n++;
					DriverConfigurazioneDBLib.log.debug("Aggiunta associazione PortaApplicativa<->ServizioApplicativo [" + idPortaApplicativa + "]<->[" + idSA + "]");
				}

				DriverConfigurazioneDBLib.log.debug("Aggiunti " + n + " associazioni PortaApplicativa<->ServizioApplicativo associati alla PortaDelegata[" + idPortaApplicativa + "]");


				// serviziapplicativi props			
				
				for (i = 0; i < aPA.sizeServizioApplicativoList(); i++) {
					PortaApplicativaServizioApplicativo servizioApplicativo = aPA.getServizioApplicativo(i);
					String nomeSA = servizioApplicativo.getNome();
					
					if(servizioApplicativo.getDatiConnettore()!=null && servizioApplicativo.getDatiConnettore().sizeProprietaList()>0) {
					
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
						sqlQueryObject.addSelectField("id");
						sqlQueryObject.addWhereCondition("id_porta=?");
						sqlQueryObject.addWhereCondition("id_servizio_applicativo=?");
						sqlQueryObject.setANDLogicOperator(true);
						sqlQuery = sqlQueryObject.createSQLQuery();
						
						long idSA = idsPA_SA.get(i);
						
						long idPA_SA = -1;
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idPortaApplicativa);
						stm.setLong(2, idSA);
						rs = stm.executeQuery();
						if(rs.next()) {
							idPA_SA = rs.getLong("id");
						}
						rs.close();
						stm.close();
						
						if(idPA_SA<=0) {
							throw new DriverConfigurazioneException("Impossibile recuperare l'id della registrazione del Servizio Applicativo [" + nomeSA + "](id:"+idSA+") alla porta applicativa con id:"+idPortaApplicativa );
						}
						
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_SA_PROPS);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("nome", "?");
						sqlQueryObject.addInsertField("valore", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						
						int j = 0;
						for ( ; j < servizioApplicativo.getDatiConnettore().sizeProprietaList(); j++) {
							
							Proprieta p = servizioApplicativo.getDatiConnettore().getProprieta(j);
							
							if(p.getValore()==null || "".equals(p.getValore())) {
								continue; // non devo serializzare valori vuoti o null (DB oracle non lo permette)
							}
							
							stm.setLong(1, idPA_SA);
							stm.setString(2, p.getNome());
							stm.setString(3, p.getValore());
							stm.executeUpdate();
							
						}
	
						stm.close();
						DriverConfigurazioneDBLib.log.debug("Inseriti " + j + " SetSAProp associati al Servizio Applicativo [" + nomeSA + "](id:"+idSA+") della PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				
				// set prop behaviour
				
				//TODO possibilie ottimizzazione
				//La lista di proprieta contiene tutte e sole le proprieta associate alla porta
				//cancello le proprieta per poi sincronizzarle con la lista passata
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_BEHAVIOUR_PROPS);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				
				if(aPA.getBehaviour()!=null) {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_BEHAVIOUR_PROPS);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
					for (i = 0; i < aPA.getBehaviour().sizeProprietaList(); i++) {
						propProtocollo = aPA.getBehaviour().getProprieta(i);
						stm.setLong(1, idPortaApplicativa);
						stm.setString(2, propProtocollo.getNome());
						stm.setString(3, propProtocollo.getValore());
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDBLib.log.debug("Inseriti " + i + " SeBehaviourProp associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				}
				
				
				
				
				
				
				
				
				
				/*Proprieta autenticazione associate alla Porta Applicativa*/
				//TODO possibilie ottimizzazione
				//La lista di proprieta contiene tutte e sole le proprieta associate alla porta
				//cancello le proprieta per poi sincronizzarle con la lista passata
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Eliminate "+n+" proprieta di autenticazione associate alla Porta Applicativa "+idPortaApplicativa);
				// set prop
				int newProps = 0;
				for (i = 0; i < aPA.sizeProprietaAutenticazioneList(); i++) {
					propProtocollo = aPA.getProprietaAutenticazione(i);

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					stm.setLong(1, idPortaApplicativa);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
					stm.close();
					newProps++;
				}
				DriverConfigurazioneDBLib.log.debug("Inserted " + newProps + " SetProtocolPropAutenticazione associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				/*Proprieta autorizzazione associate alla Porta Applicativa*/
				//TODO possibilie ottimizzazione
				//La lista di proprieta contiene tutte e sole le proprieta associate alla porta
				//cancello le proprieta per poi sincronizzarle con la lista passata
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Eliminate "+n+" proprieta di autorizzazione associate alla Porta Applicativa "+idPortaApplicativa);
				// set prop
				newProps = 0;
				for (i = 0; i < aPA.sizeProprietaAutorizzazioneList(); i++) {
					propProtocollo = aPA.getProprietaAutorizzazione(i);

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					stm.setLong(1, idPortaApplicativa);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
					stm.close();
					newProps++;
				}
				DriverConfigurazioneDBLib.log.debug("Inserted " + newProps + " SetProtocolPropAutorizzazione associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				
				/*Proprieta autorizzazione contenuti associate alla Porta Applicativa*/
				//TODO possibilie ottimizzazione
				//La lista di proprieta contiene tutte e sole le proprieta associate alla porta
				//cancello le proprieta per poi sincronizzarle con la lista passata
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Eliminate "+n+" proprieta di autorizzazione contenuti associate alla Porta Applicativa "+idPortaApplicativa);
				// set prop
				newProps = 0;
				for (i = 0; i < aPA.sizeProprietaAutorizzazioneContenutoList(); i++) {
					propProtocollo = aPA.getProprietaAutorizzazioneContenuto(i);

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					stm.setLong(1, idPortaApplicativa);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
					stm.close();
					newProps++;
				}
				DriverConfigurazioneDBLib.log.debug("Inserted " + newProps + " SetProtocolPropAutorizzazioneContenuto associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				
				
				
				
				
				
				/*Proprieta rate limiting associate alla Porta Applicativa*/
				//TODO possibilie ottimizzazione
				//La lista di proprieta contiene tutte e sole le proprieta associate alla porta
				//cancello le proprieta per poi sincronizzarle con la lista passata
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_RATE_LIMITING_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Eliminate "+n+" proprieta di rate limiting associate alla Porta Applicativa "+idPortaApplicativa);
				// set prop
				newProps = 0;
				for (i = 0; i < aPA.sizeProprietaRateLimitingList(); i++) {
					propProtocollo = aPA.getProprietaRateLimiting(i);

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_RATE_LIMITING_PROP);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					stm.setLong(1, idPortaApplicativa);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
					stm.close();
					newProps++;
				}
				DriverConfigurazioneDBLib.log.debug("Inserted " + newProps + " SetProtocolPropRateLimiting associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				
				
				
				/*Proprieta associate alla Porta Applicativa*/
				//TODO possibilie ottimizzazione
				//La lista di proprieta contiene tutte e sole le proprieta associate alla porta
				//cancello le proprieta per poi sincronizzarle con la lista passata
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Eliminate "+n+" proprieta associate alla Porta Applicativa "+idPortaApplicativa);
				// set prop
				newProps = 0;
				for (i = 0; i < aPA.sizeProprietaList(); i++) {
					propProtocollo = aPA.getProprieta(i);

					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_PROP);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("nome", "?");
					sqlQueryObject.addInsertField("valore", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);

					stm.setLong(1, idPortaApplicativa);
					stm.setString(2, propProtocollo.getNome());
					stm.setString(3, propProtocollo.getValore());
					stm.executeUpdate();
					stm.close();
					newProps++;
				}
				DriverConfigurazioneDBLib.log.debug("Inserted " + newProps + " SetProtocolProp associati alla PortaApplicativa[" + idPortaApplicativa + "]");

				
				
				// Ruoli
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_RUOLI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Cancellati "+n+" ruoli associati alla Porta Applicativa "+idPortaApplicativa);
				
				n=0;
				if(aPA.getRuoli()!=null && aPA.getRuoli().sizeRuoloList()>0){
					for (int j = 0; j < aPA.getRuoli().sizeRuoloList(); j++) {
						Ruolo ruolo = aPA.getRuoli().getRuolo(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_RUOLI);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("ruolo", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idPortaApplicativa);
						stm.setString(2, ruolo.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDBLib.log.debug("Aggiunto ruolo[" + ruolo.getNome() + "] alla PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				DriverConfigurazioneDBLib.log.debug("Aggiunti " + n + " ruoli alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				// Scope
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SCOPE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Cancellati "+n+" scope associati alla Porta Applicativa "+idPortaApplicativa);
				
				n=0;
				if(aPA.getScope()!=null && aPA.getScope().sizeScopeList()>0){
					for (int j = 0; j < aPA.getScope().sizeScopeList(); j++) {
						Scope scope = aPA.getScope().getScope(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_SCOPE);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("scope", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idPortaApplicativa);
						stm.setString(2, scope.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDBLib.log.debug("Aggiunto scope[" + scope.getNome() + "] alla PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				DriverConfigurazioneDBLib.log.debug("Aggiunti " + n + " scope alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				// Soggetti
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SOGGETTI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Cancellati "+n+" soggetti associati alla Porta Applicativa "+idPortaApplicativa);
				
				n=0;
				if(aPA.getSoggetti()!=null && aPA.getSoggetti().sizeSoggettoList()>0){
					for (int j = 0; j < aPA.getSoggetti().sizeSoggettoList(); j++) {
						PortaApplicativaAutorizzazioneSoggetto soggetto = aPA.getSoggetti().getSoggetto(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_SOGGETTI);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("tipo_soggetto", "?");
						sqlQueryObject.addInsertField("nome_soggetto", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idPortaApplicativa);
						stm.setString(2, soggetto.getTipo());
						stm.setString(3, soggetto.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDBLib.log.debug("Aggiunto soggetto [" + soggetto.getTipo() + "/"+soggetto.getNome()+"] alla PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				DriverConfigurazioneDBLib.log.debug("Aggiunti " + n + " soggetti alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				
				//la lista dei servizi applicativi passata contiene tutti e soli i servizi applicativi necessari
				//quindi nel db devono essere presenti tutti e solo quelli presenti nella lista

				//TODO possibile ottimizzazione in termini di tempo
				//cancello i servizi applicativi associati alla porta e inserisco tutti e soli quelli presenti in lista
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Cancellati "+n+" servizi applicativi autorizzati associati alla Porta Applicativa "+idPortaApplicativa);
				
				//scrivo la lista nel db
				if(aPA.getServiziApplicativiAutorizzati()!=null && aPA.getServiziApplicativiAutorizzati().sizeServizioApplicativoList()>0) {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
	
					for (i = 0; i < aPA.getServiziApplicativiAutorizzati().sizeServizioApplicativoList(); i++) {
						PortaApplicativaAutorizzazioneServizioApplicativo servizioApplicativoAutorizzato = aPA.getServiziApplicativiAutorizzati().getServizioApplicativo(i);
						String nomeSA = servizioApplicativoAutorizzato.getNome();
						String nomeProprietarioSA = servizioApplicativoAutorizzato.getNomeSoggettoProprietario();
						String tipoProprietarioSA = servizioApplicativoAutorizzato.getTipoSoggettoProprietario();
						if (nomeSA == null || nomeSA.equals(""))
							throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)[Auth]::Nome del ServizioApplicativo associato non valido.");
						if (nomeProprietarioSA == null || nomeProprietarioSA.equals(""))
							throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)[Auth]::Nome Proprietario del ServizioApplicativo associato non valido.");
						if (tipoProprietarioSA == null || tipoProprietarioSA.equals(""))
							throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)[Auth]::Tipo Proprietario del ServizioApplicativo associato non valido.");
	
						long idSA = DriverConfigurazioneDB_serviziApplicativiLIB.getIdServizioApplicativo(nomeSA, tipoProprietarioSA, nomeProprietarioSA, con, DriverConfigurazioneDBLib.tipoDB,DriverConfigurazioneDBLib.tabellaSoggetti);
	
						if (idSA <= 0)
							throw new DriverConfigurazioneException("Impossibile recuperare l'id del Servizio Applicativo [" + nomeSA + "] di [" + tipoProprietarioSA + "/" + nomeProprietarioSA + "]");
	
						stm.setLong(1, idPortaApplicativa);
						stm.setLong(2, idSA);
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDBLib.log.debug("Inseriti " + i + " servizi applicativi autorizzati associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				}
				
				
				
				
				//la lista dei servizi applicativi passata contiene tutti e soli i servizi applicativi necessari
				//quindi nel db devono essere presenti tutti e solo quelli presenti nella lista

				//TODO possibile ottimizzazione in termini di tempo
				//cancello i servizi applicativi associati alla porta e inserisco tutti e soli quelli presenti in lista
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_TOKEN_SA);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Cancellati "+n+" servizi applicativi autorizzati (token) associati alla Porta Applicativa "+idPortaApplicativa);
				
				//scrivo la lista nel db
				if(aPA.getAutorizzazioneToken()!=null && aPA.getAutorizzazioneToken().getServiziApplicativi()!=null && 
						aPA.getAutorizzazioneToken().getServiziApplicativi().sizeServizioApplicativoList()>0) {
					sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
					sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_TOKEN_SA);
					sqlQueryObject.addInsertField("id_porta", "?");
					sqlQueryObject.addInsertField("id_servizio_applicativo", "?");
					sqlQuery = sqlQueryObject.createSQLInsert();
					stm = con.prepareStatement(sqlQuery);
	
					for (i = 0; i < aPA.getAutorizzazioneToken().getServiziApplicativi().sizeServizioApplicativoList(); i++) {
						PortaApplicativaAutorizzazioneServizioApplicativo servizioApplicativoAutorizzato = aPA.getAutorizzazioneToken().getServiziApplicativi().getServizioApplicativo(i);
						String nomeSA = servizioApplicativoAutorizzato.getNome();
						String nomeProprietarioSA = servizioApplicativoAutorizzato.getNomeSoggettoProprietario();
						String tipoProprietarioSA = servizioApplicativoAutorizzato.getTipoSoggettoProprietario();
						if (nomeSA == null || nomeSA.equals(""))
							throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)[TokenAuth]::Nome del ServizioApplicativo associato non valido.");
						if (nomeProprietarioSA == null || nomeProprietarioSA.equals(""))
							throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)[TokenAuth]::Nome Proprietario del ServizioApplicativo associato non valido.");
						if (tipoProprietarioSA == null || tipoProprietarioSA.equals(""))
							throw new DriverConfigurazioneException("[CRUDPortaApplicativa(CREATE)[TokenAuth]::Tipo Proprietario del ServizioApplicativo associato non valido.");
	
						long idSA = DriverConfigurazioneDB_serviziApplicativiLIB.getIdServizioApplicativo(nomeSA, tipoProprietarioSA, nomeProprietarioSA, con, DriverConfigurazioneDBLib.tipoDB,DriverConfigurazioneDBLib.tabellaSoggetti);
	
						if (idSA <= 0)
							throw new DriverConfigurazioneException("Impossibile recuperare l'id del Servizio Applicativo [" + nomeSA + "] di [" + tipoProprietarioSA + "/" + nomeProprietarioSA + "]");
	
						stm.setLong(1, idPortaApplicativa);
						stm.setLong(2, idSA);
						stm.executeUpdate();
					}
					stm.close();
					DriverConfigurazioneDBLib.log.debug("Inseriti " + i + " servizi applicativi autorizzati (token) associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				}
				
				
				
				// Ruoli (token)
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_TOKEN_RUOLI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Cancellati "+n+" ruoli (token) associati alla Porta Applicativa "+idPortaApplicativa);
				
				n=0;
				if(aPA.getAutorizzazioneToken()!=null && aPA.getAutorizzazioneToken().getRuoli()!=null && aPA.getAutorizzazioneToken().getRuoli().sizeRuoloList()>0){
					for (int j = 0; j < aPA.getAutorizzazioneToken().getRuoli().sizeRuoloList(); j++) {
						Ruolo ruolo = aPA.getAutorizzazioneToken().getRuoli().getRuolo(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_TOKEN_RUOLI);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("ruolo", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idPortaApplicativa);
						stm.setString(2, ruolo.getNome());
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDBLib.log.debug("Aggiunto ruolo[" + ruolo.getNome() + "] (token) alla PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				DriverConfigurazioneDBLib.log.debug("Aggiunti " + n + " ruoli (token) alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				// Azioni
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_AZIONI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Cancellati "+n+" azioni associati alla Porta Applicativa "+idPortaApplicativa);
				
				n=0;
				if(aPA.getAzione()!=null && aPA.getAzione().sizeAzioneDelegataList()>0){
					for (int j = 0; j < aPA.getAzione().sizeAzioneDelegataList(); j++) {
						String azioneDelegata = aPA.getAzione().getAzioneDelegata(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_AZIONI);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("azione", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idPortaApplicativa);
						stm.setString(2, azioneDelegata);
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDBLib.log.debug("Aggiunto azione delegata [" + azioneDelegata + "] alla PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				DriverConfigurazioneDBLib.log.debug("Aggiunte " + n + " azione delegate alla PortaApplicativa[" + idPortaApplicativa + "]");
				
			
				
				
				// Cache Regole
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_CACHE_REGOLE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Cancellati "+n+" regole di cache associate alla PortaApplicativa "+idPortaApplicativa);
				
				n=0;
				if(response_cache_regole!=null && response_cache_regole.size()>0){
					for (int j = 0; j < response_cache_regole.size(); j++) {
						ResponseCachingConfigurazioneRegola regola = response_cache_regole.get(j);
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_CACHE_REGOLE);
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
						stm.setLong(indexCR++, idPortaApplicativa);
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
						DriverConfigurazioneDBLib.log.debug("Aggiunta regola di cache alla PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				DriverConfigurazioneDBLib.log.debug("Aggiunte " + n + " regole di cache alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				// AttributeAuthority
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_ATTRIBUTE_AUTHORITY);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Cancellate "+n+" A.A. associate alla Porta Applicativa "+idPortaApplicativa);
				
				n=0;
				if(aPA.sizeAttributeAuthorityList()>0){
					for (int j = 0; j < aPA.sizeAttributeAuthorityList(); j++) {
						AttributeAuthority aa = aPA.getAttributeAuthority(j);
						
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
						
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addInsertTable(CostantiDB.PORTE_APPLICATIVE_ATTRIBUTE_AUTHORITY);
						sqlQueryObject.addInsertField("id_porta", "?");
						sqlQueryObject.addInsertField("nome", "?");
						sqlQueryObject.addInsertField("attributi", "?");
						sqlQuery = sqlQueryObject.createSQLInsert();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idPortaApplicativa);
						stm.setString(2, aa.getNome());
						stm.setString(3, attributi);
						stm.executeUpdate();
						stm.close();
						n++;
						DriverConfigurazioneDBLib.log.debug("Aggiunto A.A.[" + aa.getNome() + "] alla PortaApplicativa[" + idPortaApplicativa + "]");
					}
				}
				
				DriverConfigurazioneDBLib.log.debug("Aggiunte " + n + " A.A. alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				
				
				
				// dumpConfigurazione
				DriverConfigurazioneDB_dumpLIB.CRUDDumpConfigurazione(type, con, aPA.getDump(), idPortaApplicativa, CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_PA);
				
				
				// trasformazioni
				DriverConfigurazioneDBTrasformazioniLib.CRUDTrasformazioni(type, con, aPA.getTrasformazioni(), idPortaApplicativa, false);

				
				// Handlers
				DriverConfigurazioneDB_handlerLIB.CRUDConfigurazioneMessageHandlers(type, con, null, idPortaApplicativa, true, (configHandlers!=null) ? configHandlers.getRequest() : null);
				DriverConfigurazioneDB_handlerLIB.CRUDConfigurazioneMessageHandlers(type, con, null, idPortaApplicativa, false, (configHandlers!=null) ? configHandlers.getResponse() : null);
						
				
				// extendedInfo
				if(extInfoConfigurazioneDriver!=null){
					extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDBLib.log,  aPA, CRUDType.UPDATE);
				}
				
				i=0;
				if(aPA.sizeExtendedInfoList()>0){
					if(extInfoConfigurazioneDriver!=null){
						for (i = 0; i < aPA.sizeExtendedInfoList(); i++) {
							extInfoConfigurazioneDriver.createExtendedInfo(con, DriverConfigurazioneDBLib.log,  aPA, aPA.getExtendedInfo(i), CRUDType.UPDATE);
						}
					}
				}
				DriverConfigurazioneDBLib.log.debug("Aggiunte " + i + " associazioni ExtendedInfo<->PortaApplicativa associati alla PortaApplicativa[" + idPortaApplicativa + "]");
								
				break;

			case DELETE:
				// DELETE
				// if(aPA.getId()==null || aPA.getId()<=0) throw new
				// DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaApplicativa(DELETE)]
				// id della PortaApplicativa non valida.");


				idPortaApplicativa = DBUtils.getIdPortaApplicativa(nomePorta, con, DriverConfigurazioneDBLib.tipoDB);
				if (idPortaApplicativa <= 0)
					throw new DriverConfigurazioneException("Non e' stato possibile recuperare l'id della Porta Applicativa.");

				// dumpConfigurazione
				DriverConfigurazioneDB_dumpLIB.CRUDDumpConfigurazione(type, con, aPA.getDump(), idPortaApplicativa, CostantiDB.DUMP_CONFIGURAZIONE_PROPRIETARIO_PA);
				
				// trasformazioni
				DriverConfigurazioneDBTrasformazioniLib.CRUDTrasformazioni(type, con, aPA.getTrasformazioni(), idPortaApplicativa, false);
				
				// Handlers
				DriverConfigurazioneDB_handlerLIB.CRUDConfigurazioneMessageHandlers(type, con, null, idPortaApplicativa, true, (configHandlers!=null) ? configHandlers.getRequest() : null);
				DriverConfigurazioneDB_handlerLIB.CRUDConfigurazioneMessageHandlers(type, con, null, idPortaApplicativa, false, (configHandlers!=null) ? configHandlers.getResponse() : null);
				
				// AttributeAuthority
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_ATTRIBUTE_AUTHORITY);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Cancellate "+n+" A.A. associate alla Porta Applicativa "+idPortaApplicativa);
				
				// Cache Regole
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_CACHE_REGOLE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Cancellati "+n+" regole di cache associate alla Porta Applicativa "+idPortaApplicativa);
				
				// azioni delegate
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_AZIONI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Cancellate "+n+" azioni delegate associate alla Porta Applicativa "+idPortaApplicativa);
				
				// ruoli (token)
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_TOKEN_RUOLI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Cancellati "+n+" ruoli (token) associati alla Porta Applicativa "+idPortaApplicativa);
				
				// sa autorizzati (token)
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_TOKEN_SA);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Cancellati "+n+" servizi applicativi autorizzati (token) associati alla Porta Applicativa "+idPortaApplicativa);
				
				// sa autorizzati
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Cancellati "+n+" servizi applicativi autorizzati associati alla Porta Applicativa "+idPortaApplicativa);
				
				// soggetti
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SOGGETTI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Cancellati "+n+" soggetti associati alla Porta Applicativa "+idPortaApplicativa);
				
				// ruoli
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_RUOLI);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Cancellati "+n+" ruoli associati alla Porta Applicativa "+idPortaApplicativa);
				
				// scope
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SCOPE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Cancellati "+n+" scope associati alla Porta Applicativa "+idPortaApplicativa);
				
				// mtom
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MTOM_REQUEST);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Deleted " + n + " request flow con id=" + idPortaApplicativa);

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MTOM_RESPONSE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Deleted " + n + " response flow con id=" + idPortaApplicativa);
				
				
				// message security
				//if ( CostantiConfigurazione.ABILITATO.toString().equals(messageSecurityStatus) )  {
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Deleted " + n + " request flow con id=" + idPortaApplicativa);

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Deleted " + n + " response flow con id=" + idPortaApplicativa);
				//}

				// serviziapplicativi props
				idsPA_SA = new ArrayList<>(); 
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				rs = stm.executeQuery();
				while(rs.next()) {
					idsPA_SA.add(rs.getLong("id"));
				}
				rs.close();
				stm.close();
				
				if(!idsPA_SA.isEmpty()) {
					for (Long idsapa : idsPA_SA) {
						sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
						sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SA_PROPS);
						sqlQueryObject.addWhereCondition("id_porta=?");
						sqlQuery = sqlQueryObject.createSQLDelete();
						stm = con.prepareStatement(sqlQuery);
						stm.setLong(1, idsapa);
						n=stm.executeUpdate();
						stm.close();
						DriverConfigurazioneDBLib.log.debug("Eliminate "+n+" proprieta relative all'associazione '"+idsapa+"' (Porta Applicativa "+idPortaApplicativa+")");
					}
				}
				
				// serviziapplicativi
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Deleted " + n + " servizi applicativi associati alla PortaApplicativa[" + idPortaApplicativa + "]");

				// cancello le prop relative al behaviour
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_BEHAVIOUR_PROPS);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDBLib.log.debug("Deleted " + n + " BehaviourProp associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				// cancello anche le flow di request/response associate a questa
				// porta applicativa
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDBLib.log.debug("Deleted " + n + " security_request flow associate alla PortaApplicativa[" + idPortaApplicativa + "]");

				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDBLib.log.debug("Deleted " + n + " security_response flow associate alla PortaApplicativa[" + idPortaApplicativa + "]");

				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);

				n = stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDBLib.log.debug("Deleted " + n + " correlazione associate alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_CORRELAZIONE_RISPOSTA);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);

				n = stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDBLib.log.debug("Deleted " + n + " correlazione della risposta associate alla PortaApplicativa[" + idPortaApplicativa + "]");
								
				// cancello le prop
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDBLib.log.debug("Deleted " + n + " SetProtocolProp associati alla PortaApplicativa[" + idPortaApplicativa + "]");

				// cancello le prop di rate limiting
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_RATE_LIMITING_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDBLib.log.debug("Deleted " + n + " SetProtocolPropRateLimiting associati alla PortaApplicativa[" + idPortaApplicativa + "]");

				
				// cancello le prop di autorizzazione contenuti
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDBLib.log.debug("Deleted " + n + " SetProtocolPropAutorizzazioneContenuto associati alla PortaApplicativa[" + idPortaApplicativa + "]");

				// cancello le prop di autorizzazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_AUTORIZZAZIONE_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDBLib.log.debug("Deleted " + n + " SetProtocolPropAutorizzazione associati alla PortaApplicativa[" + idPortaApplicativa + "]");
				
				// cancello le prop di autenticazione
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE_AUTENTICAZIONE_PROP);
				sqlQueryObject.addWhereCondition("id_porta=?");
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				n=stm.executeUpdate();
				stm.close();
				if (n > 0)
					DriverConfigurazioneDBLib.log.debug("Deleted " + n + " SetProtocolPropAutenticazione associati alla PortaApplicativa[" + idPortaApplicativa + "]");

				// extendedInfo
				if(extInfoConfigurazioneDriver!=null){
					extInfoConfigurazioneDriver.deleteAllExtendedInfo(con, DriverConfigurazioneDBLib.log,  aPA, CRUDType.DELETE);
				}
				
				// porta applicativa
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(DriverConfigurazioneDBLib.tipoDB);
				sqlQueryObject.addDeleteTable(CostantiDB.PORTE_APPLICATIVE);
				sqlQueryObject.addWhereCondition("id=?");
				sqlQueryObject.addWhereCondition("id_soggetto=?");
				sqlQueryObject.addWhereCondition("nome_porta=?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLDelete();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, idPortaApplicativa);
				stm.setLong(2, idProprietario);
				stm.setString(3, nomePorta);
				n=stm.executeUpdate();
				stm.close();
				DriverConfigurazioneDBLib.log.debug("Deleted " + n + " row(s).");

				break;
			}

			return idPortaApplicativa;
		} catch (DriverConfigurazioneException e) {
			throw e;
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB_LIB::CRUDPortaApplicativa] SQLException [" + se.getMessage() + "].",se);
		} catch (Exception e) {
			throw new DriverConfigurazioneException("Errore durante operazione("+type+") CRUDPortaApplicativa.",e);
		}finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
		}
	}
	
}
