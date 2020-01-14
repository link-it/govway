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
package org.openspcoop2.core.transazioni.dao.jdbc.fetch;

import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.dao.jdbc.utils.AbstractJDBCFetch;
import org.openspcoop2.generic_project.dao.jdbc.utils.JDBCParameterUtilities;
import org.openspcoop2.generic_project.exception.ServiceException;

import java.sql.ResultSet;
import java.util.Map;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IKeyGeneratorObject;

import org.openspcoop2.core.transazioni.TransazioneExtendedInfo;
import org.openspcoop2.core.transazioni.Transazione;


/**     
 * TransazioneFetch
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransazioneFetch extends AbstractJDBCFetch {

	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , ResultSet rs) throws ServiceException {
		
		try{
			JDBCParameterUtilities jdbcParameterUtilities =  
					new JDBCParameterUtilities(tipoDatabase);

			if(model.equals(Transazione.model())){
				Transazione object = new Transazione();
				setParameter(object, "setIdTransazione", Transazione.model().ID_TRANSAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "id", Transazione.model().ID_TRANSAZIONE.getFieldType()));
				setParameter(object, "setStato", Transazione.model().STATO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "stato", Transazione.model().STATO.getFieldType()));
				setParameter(object, "setRuoloTransazione", Transazione.model().RUOLO_TRANSAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "ruolo_transazione", Transazione.model().RUOLO_TRANSAZIONE.getFieldType()));
				setParameter(object, "setEsito", Transazione.model().ESITO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "esito", Transazione.model().ESITO.getFieldType()));
				setParameter(object, "setEsitoContesto", Transazione.model().ESITO_CONTESTO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "esito_contesto", Transazione.model().ESITO_CONTESTO.getFieldType()));
				setParameter(object, "setProtocollo", Transazione.model().PROTOCOLLO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "protocollo", Transazione.model().PROTOCOLLO.getFieldType()));
				setParameter(object, "setTipoRichiesta", Transazione.model().TIPO_RICHIESTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo_richiesta", Transazione.model().TIPO_RICHIESTA.getFieldType()));
				setParameter(object, "setCodiceRispostaIngresso", Transazione.model().CODICE_RISPOSTA_INGRESSO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "codice_risposta_ingresso", Transazione.model().CODICE_RISPOSTA_INGRESSO.getFieldType()));
				setParameter(object, "setCodiceRispostaUscita", Transazione.model().CODICE_RISPOSTA_USCITA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "codice_risposta_uscita", Transazione.model().CODICE_RISPOSTA_USCITA.getFieldType()));
				setParameter(object, "setDataAccettazioneRichiesta", Transazione.model().DATA_ACCETTAZIONE_RICHIESTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_accettazione_richiesta", Transazione.model().DATA_ACCETTAZIONE_RICHIESTA.getFieldType()));
				setParameter(object, "setDataIngressoRichiesta", Transazione.model().DATA_INGRESSO_RICHIESTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_ingresso_richiesta", Transazione.model().DATA_INGRESSO_RICHIESTA.getFieldType()));
				setParameter(object, "setDataUscitaRichiesta", Transazione.model().DATA_USCITA_RICHIESTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_uscita_richiesta", Transazione.model().DATA_USCITA_RICHIESTA.getFieldType()));
				setParameter(object, "setDataAccettazioneRisposta", Transazione.model().DATA_ACCETTAZIONE_RISPOSTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_accettazione_risposta", Transazione.model().DATA_ACCETTAZIONE_RISPOSTA.getFieldType()));
				setParameter(object, "setDataIngressoRisposta", Transazione.model().DATA_INGRESSO_RISPOSTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_ingresso_risposta", Transazione.model().DATA_INGRESSO_RISPOSTA.getFieldType()));
				setParameter(object, "setDataUscitaRisposta", Transazione.model().DATA_USCITA_RISPOSTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_uscita_risposta", Transazione.model().DATA_USCITA_RISPOSTA.getFieldType()));
				setParameter(object, "setRichiestaIngressoBytes", Transazione.model().RICHIESTA_INGRESSO_BYTES.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "richiesta_ingresso_bytes", Transazione.model().RICHIESTA_INGRESSO_BYTES.getFieldType()));
				setParameter(object, "setRichiestaUscitaBytes", Transazione.model().RICHIESTA_USCITA_BYTES.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "richiesta_uscita_bytes", Transazione.model().RICHIESTA_USCITA_BYTES.getFieldType()));
				setParameter(object, "setRispostaIngressoBytes", Transazione.model().RISPOSTA_INGRESSO_BYTES.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "risposta_ingresso_bytes", Transazione.model().RISPOSTA_INGRESSO_BYTES.getFieldType()));
				setParameter(object, "setRispostaUscitaBytes", Transazione.model().RISPOSTA_USCITA_BYTES.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "risposta_uscita_bytes", Transazione.model().RISPOSTA_USCITA_BYTES.getFieldType()));
				setParameter(object, "setPddCodice", Transazione.model().PDD_CODICE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "pdd_codice", Transazione.model().PDD_CODICE.getFieldType()));
				setParameter(object, "setPddTipoSoggetto", Transazione.model().PDD_TIPO_SOGGETTO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "pdd_tipo_soggetto", Transazione.model().PDD_TIPO_SOGGETTO.getFieldType()));
				setParameter(object, "setPddNomeSoggetto", Transazione.model().PDD_NOME_SOGGETTO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "pdd_nome_soggetto", Transazione.model().PDD_NOME_SOGGETTO.getFieldType()));
				setParameter(object, "set_value_pddRuolo", String.class,
					jdbcParameterUtilities.readParameter(rs, "pdd_ruolo", Transazione.model().PDD_RUOLO.getFieldType())+"");
				setParameter(object, "setFaultIntegrazione", Transazione.model().FAULT_INTEGRAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "fault_integrazione", Transazione.model().FAULT_INTEGRAZIONE.getFieldType()));
				setParameter(object, "setFormatoFaultIntegrazione", Transazione.model().FORMATO_FAULT_INTEGRAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "formato_fault_integrazione", Transazione.model().FORMATO_FAULT_INTEGRAZIONE.getFieldType()));
				setParameter(object, "setFaultCooperazione", Transazione.model().FAULT_COOPERAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "fault_cooperazione", Transazione.model().FAULT_COOPERAZIONE.getFieldType()));
				setParameter(object, "setFormatoFaultCooperazione", Transazione.model().FORMATO_FAULT_COOPERAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "formato_fault_cooperazione", Transazione.model().FORMATO_FAULT_COOPERAZIONE.getFieldType()));
				setParameter(object, "setTipoSoggettoFruitore", Transazione.model().TIPO_SOGGETTO_FRUITORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo_soggetto_fruitore", Transazione.model().TIPO_SOGGETTO_FRUITORE.getFieldType()));
				setParameter(object, "setNomeSoggettoFruitore", Transazione.model().NOME_SOGGETTO_FRUITORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome_soggetto_fruitore", Transazione.model().NOME_SOGGETTO_FRUITORE.getFieldType()));
				setParameter(object, "setIdportaSoggettoFruitore", Transazione.model().IDPORTA_SOGGETTO_FRUITORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "idporta_soggetto_fruitore", Transazione.model().IDPORTA_SOGGETTO_FRUITORE.getFieldType()));
				setParameter(object, "setIndirizzoSoggettoFruitore", Transazione.model().INDIRIZZO_SOGGETTO_FRUITORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "indirizzo_soggetto_fruitore", Transazione.model().INDIRIZZO_SOGGETTO_FRUITORE.getFieldType()));
				setParameter(object, "setTipoSoggettoErogatore", Transazione.model().TIPO_SOGGETTO_EROGATORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo_soggetto_erogatore", Transazione.model().TIPO_SOGGETTO_EROGATORE.getFieldType()));
				setParameter(object, "setNomeSoggettoErogatore", Transazione.model().NOME_SOGGETTO_EROGATORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome_soggetto_erogatore", Transazione.model().NOME_SOGGETTO_EROGATORE.getFieldType()));
				setParameter(object, "setIdportaSoggettoErogatore", Transazione.model().IDPORTA_SOGGETTO_EROGATORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "idporta_soggetto_erogatore", Transazione.model().IDPORTA_SOGGETTO_EROGATORE.getFieldType()));
				setParameter(object, "setIndirizzoSoggettoErogatore", Transazione.model().INDIRIZZO_SOGGETTO_EROGATORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "indirizzo_soggetto_erogatore", Transazione.model().INDIRIZZO_SOGGETTO_EROGATORE.getFieldType()));
				setParameter(object, "setIdMessaggioRichiesta", Transazione.model().ID_MESSAGGIO_RICHIESTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "id_messaggio_richiesta", Transazione.model().ID_MESSAGGIO_RICHIESTA.getFieldType()));
				setParameter(object, "setIdMessaggioRisposta", Transazione.model().ID_MESSAGGIO_RISPOSTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "id_messaggio_risposta", Transazione.model().ID_MESSAGGIO_RISPOSTA.getFieldType()));
				setParameter(object, "setDataIdMsgRichiesta", Transazione.model().DATA_ID_MSG_RICHIESTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_id_msg_richiesta", Transazione.model().DATA_ID_MSG_RICHIESTA.getFieldType()));
				setParameter(object, "setDataIdMsgRisposta", Transazione.model().DATA_ID_MSG_RISPOSTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "data_id_msg_risposta", Transazione.model().DATA_ID_MSG_RISPOSTA.getFieldType()));
				setParameter(object, "setProfiloCollaborazioneOp2", Transazione.model().PROFILO_COLLABORAZIONE_OP_2.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "profilo_collaborazione_op2", Transazione.model().PROFILO_COLLABORAZIONE_OP_2.getFieldType()));
				setParameter(object, "setProfiloCollaborazioneProt", Transazione.model().PROFILO_COLLABORAZIONE_PROT.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "profilo_collaborazione_prot", Transazione.model().PROFILO_COLLABORAZIONE_PROT.getFieldType()));
				setParameter(object, "setIdCollaborazione", Transazione.model().ID_COLLABORAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "id_collaborazione", Transazione.model().ID_COLLABORAZIONE.getFieldType()));
				setParameter(object, "setUriAccordoServizio", Transazione.model().URI_ACCORDO_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "uri_accordo_servizio", Transazione.model().URI_ACCORDO_SERVIZIO.getFieldType()));
				setParameter(object, "setTipoServizio", Transazione.model().TIPO_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo_servizio", Transazione.model().TIPO_SERVIZIO.getFieldType()));
				setParameter(object, "setNomeServizio", Transazione.model().NOME_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome_servizio", Transazione.model().NOME_SERVIZIO.getFieldType()));
				setParameter(object, "setVersioneServizio", Transazione.model().VERSIONE_SERVIZIO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "versione_servizio", Transazione.model().VERSIONE_SERVIZIO.getFieldType()));
				setParameter(object, "setAzione", Transazione.model().AZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "azione", Transazione.model().AZIONE.getFieldType()));
				setParameter(object, "setIdAsincrono", Transazione.model().ID_ASINCRONO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "id_asincrono", Transazione.model().ID_ASINCRONO.getFieldType()));
				setParameter(object, "setTipoServizioCorrelato", Transazione.model().TIPO_SERVIZIO_CORRELATO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo_servizio_correlato", Transazione.model().TIPO_SERVIZIO_CORRELATO.getFieldType()));
				setParameter(object, "setNomeServizioCorrelato", Transazione.model().NOME_SERVIZIO_CORRELATO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome_servizio_correlato", Transazione.model().NOME_SERVIZIO_CORRELATO.getFieldType()));
				setParameter(object, "setHeaderProtocolloRichiesta", Transazione.model().HEADER_PROTOCOLLO_RICHIESTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "header_protocollo_richiesta", Transazione.model().HEADER_PROTOCOLLO_RICHIESTA.getFieldType()));
				setParameter(object, "setDigestRichiesta", Transazione.model().DIGEST_RICHIESTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "digest_richiesta", Transazione.model().DIGEST_RICHIESTA.getFieldType()));
				setParameter(object, "setProtocolloExtInfoRichiesta", Transazione.model().PROTOCOLLO_EXT_INFO_RICHIESTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "prot_ext_info_richiesta", Transazione.model().PROTOCOLLO_EXT_INFO_RICHIESTA.getFieldType()));
				setParameter(object, "setHeaderProtocolloRisposta", Transazione.model().HEADER_PROTOCOLLO_RISPOSTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "header_protocollo_risposta", Transazione.model().HEADER_PROTOCOLLO_RISPOSTA.getFieldType()));
				setParameter(object, "setDigestRisposta", Transazione.model().DIGEST_RISPOSTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "digest_risposta", Transazione.model().DIGEST_RISPOSTA.getFieldType()));
				setParameter(object, "setProtocolloExtInfoRisposta", Transazione.model().PROTOCOLLO_EXT_INFO_RISPOSTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "prot_ext_info_risposta", Transazione.model().PROTOCOLLO_EXT_INFO_RISPOSTA.getFieldType()));
				setParameter(object, "setTracciaRichiesta", Transazione.model().TRACCIA_RICHIESTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "traccia_richiesta", Transazione.model().TRACCIA_RICHIESTA.getFieldType()));
				setParameter(object, "setTracciaRisposta", Transazione.model().TRACCIA_RISPOSTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "traccia_risposta", Transazione.model().TRACCIA_RISPOSTA.getFieldType()));
				setParameter(object, "setDiagnostici", Transazione.model().DIAGNOSTICI.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "diagnostici", Transazione.model().DIAGNOSTICI.getFieldType()));
				setParameter(object, "setDiagnosticiList1", Transazione.model().DIAGNOSTICI_LIST_1.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "diagnostici_list_1", Transazione.model().DIAGNOSTICI_LIST_1.getFieldType()));
				setParameter(object, "setDiagnosticiList2", Transazione.model().DIAGNOSTICI_LIST_2.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "diagnostici_list_2", Transazione.model().DIAGNOSTICI_LIST_2.getFieldType()));
				setParameter(object, "setDiagnosticiListExt", Transazione.model().DIAGNOSTICI_LIST_EXT.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "diagnostici_list_ext", Transazione.model().DIAGNOSTICI_LIST_EXT.getFieldType()));
				setParameter(object, "setDiagnosticiExt", Transazione.model().DIAGNOSTICI_EXT.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "diagnostici_ext", Transazione.model().DIAGNOSTICI_EXT.getFieldType()));
				setParameter(object, "setIdCorrelazioneApplicativa", Transazione.model().ID_CORRELAZIONE_APPLICATIVA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "id_correlazione_applicativa", Transazione.model().ID_CORRELAZIONE_APPLICATIVA.getFieldType()));
				setParameter(object, "setIdCorrelazioneApplicativaRisposta", Transazione.model().ID_CORRELAZIONE_APPLICATIVA_RISPOSTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "id_correlazione_risposta", Transazione.model().ID_CORRELAZIONE_APPLICATIVA_RISPOSTA.getFieldType()));
				setParameter(object, "setServizioApplicativoFruitore", Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "servizio_applicativo_fruitore", Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE.getFieldType()));
				setParameter(object, "setServizioApplicativoErogatore", Transazione.model().SERVIZIO_APPLICATIVO_EROGATORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "servizio_applicativo_erogatore", Transazione.model().SERVIZIO_APPLICATIVO_EROGATORE.getFieldType()));
				setParameter(object, "setOperazioneIm", Transazione.model().OPERAZIONE_IM.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "operazione_im", Transazione.model().OPERAZIONE_IM.getFieldType()));
				setParameter(object, "setLocationRichiesta", Transazione.model().LOCATION_RICHIESTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "location_richiesta", Transazione.model().LOCATION_RICHIESTA.getFieldType()));
				setParameter(object, "setLocationRisposta", Transazione.model().LOCATION_RISPOSTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "location_risposta", Transazione.model().LOCATION_RISPOSTA.getFieldType()));
				setParameter(object, "setNomePorta", Transazione.model().NOME_PORTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome_porta", Transazione.model().NOME_PORTA.getFieldType()));
				setParameter(object, "setCredenziali", Transazione.model().CREDENZIALI.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "credenziali", Transazione.model().CREDENZIALI.getFieldType()));
				setParameter(object, "setLocationConnettore", Transazione.model().LOCATION_CONNETTORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "location_connettore", Transazione.model().LOCATION_CONNETTORE.getFieldType()));
				setParameter(object, "setUrlInvocazione", Transazione.model().URL_INVOCAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "url_invocazione", Transazione.model().URL_INVOCAZIONE.getFieldType()));
				setParameter(object, "setTrasportoMittente", Transazione.model().TRASPORTO_MITTENTE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "trasporto_mittente", Transazione.model().TRASPORTO_MITTENTE.getFieldType()));
				setParameter(object, "setTokenIssuer", Transazione.model().TOKEN_ISSUER.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "token_issuer", Transazione.model().TOKEN_ISSUER.getFieldType()));
				setParameter(object, "setTokenClientId", Transazione.model().TOKEN_CLIENT_ID.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "token_client_id", Transazione.model().TOKEN_CLIENT_ID.getFieldType()));
				setParameter(object, "setTokenSubject", Transazione.model().TOKEN_SUBJECT.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "token_subject", Transazione.model().TOKEN_SUBJECT.getFieldType()));
				setParameter(object, "setTokenUsername", Transazione.model().TOKEN_USERNAME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "token_username", Transazione.model().TOKEN_USERNAME.getFieldType()));
				setParameter(object, "setTokenMail", Transazione.model().TOKEN_MAIL.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "token_mail", Transazione.model().TOKEN_MAIL.getFieldType()));
				setParameter(object, "setTokenInfo", Transazione.model().TOKEN_INFO.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "token_info", Transazione.model().TOKEN_INFO.getFieldType()));
				setParameter(object, "setTempiElaborazione", Transazione.model().TEMPI_ELABORAZIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tempi_elaborazione", Transazione.model().TEMPI_ELABORAZIONE.getFieldType()));
				setParameter(object, "setDuplicatiRichiesta", Transazione.model().DUPLICATI_RICHIESTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "duplicati_richiesta", Transazione.model().DUPLICATI_RICHIESTA.getFieldType()));
				setParameter(object, "setDuplicatiRisposta", Transazione.model().DUPLICATI_RISPOSTA.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "duplicati_risposta", Transazione.model().DUPLICATI_RISPOSTA.getFieldType()));
				setParameter(object, "setClusterId", Transazione.model().CLUSTER_ID.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "cluster_id", Transazione.model().CLUSTER_ID.getFieldType()));
				setParameter(object, "setSocketClientAddress", Transazione.model().SOCKET_CLIENT_ADDRESS.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "socket_client_address", Transazione.model().SOCKET_CLIENT_ADDRESS.getFieldType()));
				setParameter(object, "setTransportClientAddress", Transazione.model().TRANSPORT_CLIENT_ADDRESS.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "transport_client_address", Transazione.model().TRANSPORT_CLIENT_ADDRESS.getFieldType()));
				setParameter(object, "setClientAddress", Transazione.model().CLIENT_ADDRESS.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "client_address", Transazione.model().CLIENT_ADDRESS.getFieldType()));
				setParameter(object, "setEventiGestione", Transazione.model().EVENTI_GESTIONE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "eventi_gestione", Transazione.model().EVENTI_GESTIONE.getFieldType()));
				setParameter(object, "setTipoApi", Transazione.model().TIPO_API.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "tipo_api", Transazione.model().TIPO_API.getFieldType()));
				setParameter(object, "setGruppi", Transazione.model().GRUPPI.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "gruppi", Transazione.model().GRUPPI.getFieldType()));
				return object;
			}
			if(model.equals(Transazione.model().TRANSAZIONE_EXTENDED_INFO)){
				TransazioneExtendedInfo object = new TransazioneExtendedInfo();
				setParameter(object, "setId", Long.class,
					jdbcParameterUtilities.readParameter(rs, "id", Long.class));
				setParameter(object, "setNome", Transazione.model().TRANSAZIONE_EXTENDED_INFO.NOME.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "nome", Transazione.model().TRANSAZIONE_EXTENDED_INFO.NOME.getFieldType()));
				setParameter(object, "setValore", Transazione.model().TRANSAZIONE_EXTENDED_INFO.VALORE.getFieldType(),
					jdbcParameterUtilities.readParameter(rs, "valore", Transazione.model().TRANSAZIONE_EXTENDED_INFO.VALORE.getFieldType()));
				return object;
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by fetch: "+this.getClass().getName());
			}	
					
		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in fetch: "+e.getMessage(),e);
		}
		
	}
	
	@Override
	public Object fetch(TipiDatabase tipoDatabase, IModel<?> model , Map<String,Object> map ) throws ServiceException {
		
		try{

			if(model.equals(Transazione.model())){
				Transazione object = new Transazione();
				setParameter(object, "setIdTransazione", Transazione.model().ID_TRANSAZIONE.getFieldType(),
					this.getObjectFromMap(map,"id-transazione"));
				setParameter(object, "setStato", Transazione.model().STATO.getFieldType(),
					this.getObjectFromMap(map,"stato"));
				setParameter(object, "setRuoloTransazione", Transazione.model().RUOLO_TRANSAZIONE.getFieldType(),
					this.getObjectFromMap(map,"ruolo-transazione"));
				setParameter(object, "setEsito", Transazione.model().ESITO.getFieldType(),
					this.getObjectFromMap(map,"esito"));
				setParameter(object, "setEsitoContesto", Transazione.model().ESITO_CONTESTO.getFieldType(),
					this.getObjectFromMap(map,"esito-contesto"));
				setParameter(object, "setProtocollo", Transazione.model().PROTOCOLLO.getFieldType(),
					this.getObjectFromMap(map,"protocollo"));
				setParameter(object, "setTipoRichiesta", Transazione.model().TIPO_RICHIESTA.getFieldType(),
					this.getObjectFromMap(map,"tipo-richiesta"));
				setParameter(object, "setCodiceRispostaIngresso", Transazione.model().CODICE_RISPOSTA_INGRESSO.getFieldType(),
					this.getObjectFromMap(map,"codice-risposta-ingresso"));
				setParameter(object, "setCodiceRispostaUscita", Transazione.model().CODICE_RISPOSTA_USCITA.getFieldType(),
					this.getObjectFromMap(map,"codice-risposta-uscita"));
				setParameter(object, "setDataAccettazioneRichiesta", Transazione.model().DATA_ACCETTAZIONE_RICHIESTA.getFieldType(),
					this.getObjectFromMap(map,"data-accettazione-richiesta"));
				setParameter(object, "setDataIngressoRichiesta", Transazione.model().DATA_INGRESSO_RICHIESTA.getFieldType(),
					this.getObjectFromMap(map,"data-ingresso-richiesta"));
				setParameter(object, "setDataUscitaRichiesta", Transazione.model().DATA_USCITA_RICHIESTA.getFieldType(),
					this.getObjectFromMap(map,"data-uscita-richiesta"));
				setParameter(object, "setDataAccettazioneRisposta", Transazione.model().DATA_ACCETTAZIONE_RISPOSTA.getFieldType(),
					this.getObjectFromMap(map,"data-accettazione-risposta"));
				setParameter(object, "setDataIngressoRisposta", Transazione.model().DATA_INGRESSO_RISPOSTA.getFieldType(),
					this.getObjectFromMap(map,"data-ingresso-risposta"));
				setParameter(object, "setDataUscitaRisposta", Transazione.model().DATA_USCITA_RISPOSTA.getFieldType(),
					this.getObjectFromMap(map,"data-uscita-risposta"));
				setParameter(object, "setRichiestaIngressoBytes", Transazione.model().RICHIESTA_INGRESSO_BYTES.getFieldType(),
					this.getObjectFromMap(map,"richiesta-ingresso-bytes"));
				setParameter(object, "setRichiestaUscitaBytes", Transazione.model().RICHIESTA_USCITA_BYTES.getFieldType(),
					this.getObjectFromMap(map,"richiesta-uscita-bytes"));
				setParameter(object, "setRispostaIngressoBytes", Transazione.model().RISPOSTA_INGRESSO_BYTES.getFieldType(),
					this.getObjectFromMap(map,"risposta-ingresso-bytes"));
				setParameter(object, "setRispostaUscitaBytes", Transazione.model().RISPOSTA_USCITA_BYTES.getFieldType(),
					this.getObjectFromMap(map,"risposta-uscita-bytes"));
				setParameter(object, "setPddCodice", Transazione.model().PDD_CODICE.getFieldType(),
					this.getObjectFromMap(map,"pdd-codice"));
				setParameter(object, "setPddTipoSoggetto", Transazione.model().PDD_TIPO_SOGGETTO.getFieldType(),
					this.getObjectFromMap(map,"pdd-tipo-soggetto"));
				setParameter(object, "setPddNomeSoggetto", Transazione.model().PDD_NOME_SOGGETTO.getFieldType(),
					this.getObjectFromMap(map,"pdd-nome-soggetto"));
				setParameter(object, "set_value_pddRuolo", String.class,
					this.getObjectFromMap(map,"pdd-ruolo"));
				setParameter(object, "setFaultIntegrazione", Transazione.model().FAULT_INTEGRAZIONE.getFieldType(),
					this.getObjectFromMap(map,"fault-integrazione"));
				setParameter(object, "setFormatoFaultIntegrazione", Transazione.model().FORMATO_FAULT_INTEGRAZIONE.getFieldType(),
					this.getObjectFromMap(map,"formato-fault-integrazione"));
				setParameter(object, "setFaultCooperazione", Transazione.model().FAULT_COOPERAZIONE.getFieldType(),
					this.getObjectFromMap(map,"fault-cooperazione"));
				setParameter(object, "setFormatoFaultCooperazione", Transazione.model().FORMATO_FAULT_COOPERAZIONE.getFieldType(),
					this.getObjectFromMap(map,"formato-fault-cooperazione"));
				setParameter(object, "setTipoSoggettoFruitore", Transazione.model().TIPO_SOGGETTO_FRUITORE.getFieldType(),
					this.getObjectFromMap(map,"tipo-soggetto-fruitore"));
				setParameter(object, "setNomeSoggettoFruitore", Transazione.model().NOME_SOGGETTO_FRUITORE.getFieldType(),
					this.getObjectFromMap(map,"nome-soggetto-fruitore"));
				setParameter(object, "setIdportaSoggettoFruitore", Transazione.model().IDPORTA_SOGGETTO_FRUITORE.getFieldType(),
					this.getObjectFromMap(map,"idporta-soggetto-fruitore"));
				setParameter(object, "setIndirizzoSoggettoFruitore", Transazione.model().INDIRIZZO_SOGGETTO_FRUITORE.getFieldType(),
					this.getObjectFromMap(map,"indirizzo-soggetto-fruitore"));
				setParameter(object, "setTipoSoggettoErogatore", Transazione.model().TIPO_SOGGETTO_EROGATORE.getFieldType(),
					this.getObjectFromMap(map,"tipo-soggetto-erogatore"));
				setParameter(object, "setNomeSoggettoErogatore", Transazione.model().NOME_SOGGETTO_EROGATORE.getFieldType(),
					this.getObjectFromMap(map,"nome-soggetto-erogatore"));
				setParameter(object, "setIdportaSoggettoErogatore", Transazione.model().IDPORTA_SOGGETTO_EROGATORE.getFieldType(),
					this.getObjectFromMap(map,"idporta-soggetto-erogatore"));
				setParameter(object, "setIndirizzoSoggettoErogatore", Transazione.model().INDIRIZZO_SOGGETTO_EROGATORE.getFieldType(),
					this.getObjectFromMap(map,"indirizzo-soggetto-erogatore"));
				setParameter(object, "setIdMessaggioRichiesta", Transazione.model().ID_MESSAGGIO_RICHIESTA.getFieldType(),
					this.getObjectFromMap(map,"id-messaggio-richiesta"));
				setParameter(object, "setIdMessaggioRisposta", Transazione.model().ID_MESSAGGIO_RISPOSTA.getFieldType(),
					this.getObjectFromMap(map,"id-messaggio-risposta"));
				setParameter(object, "setDataIdMsgRichiesta", Transazione.model().DATA_ID_MSG_RICHIESTA.getFieldType(),
					this.getObjectFromMap(map,"data-id-msg-richiesta"));
				setParameter(object, "setDataIdMsgRisposta", Transazione.model().DATA_ID_MSG_RISPOSTA.getFieldType(),
					this.getObjectFromMap(map,"data-id-msg-risposta"));
				setParameter(object, "setProfiloCollaborazioneOp2", Transazione.model().PROFILO_COLLABORAZIONE_OP_2.getFieldType(),
					this.getObjectFromMap(map,"profilo-collaborazione-op2"));
				setParameter(object, "setProfiloCollaborazioneProt", Transazione.model().PROFILO_COLLABORAZIONE_PROT.getFieldType(),
					this.getObjectFromMap(map,"profilo-collaborazione-prot"));
				setParameter(object, "setIdCollaborazione", Transazione.model().ID_COLLABORAZIONE.getFieldType(),
					this.getObjectFromMap(map,"id-collaborazione"));
				setParameter(object, "setUriAccordoServizio", Transazione.model().URI_ACCORDO_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"uri-accordo-servizio"));
				setParameter(object, "setTipoServizio", Transazione.model().TIPO_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"tipo-servizio"));
				setParameter(object, "setNomeServizio", Transazione.model().NOME_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"nome-servizio"));
				setParameter(object, "setVersioneServizio", Transazione.model().VERSIONE_SERVIZIO.getFieldType(),
					this.getObjectFromMap(map,"versione-servizio"));
				setParameter(object, "setAzione", Transazione.model().AZIONE.getFieldType(),
					this.getObjectFromMap(map,"azione"));
				setParameter(object, "setIdAsincrono", Transazione.model().ID_ASINCRONO.getFieldType(),
					this.getObjectFromMap(map,"id-asincrono"));
				setParameter(object, "setTipoServizioCorrelato", Transazione.model().TIPO_SERVIZIO_CORRELATO.getFieldType(),
					this.getObjectFromMap(map,"tipo-servizio-correlato"));
				setParameter(object, "setNomeServizioCorrelato", Transazione.model().NOME_SERVIZIO_CORRELATO.getFieldType(),
					this.getObjectFromMap(map,"nome-servizio-correlato"));
				setParameter(object, "setHeaderProtocolloRichiesta", Transazione.model().HEADER_PROTOCOLLO_RICHIESTA.getFieldType(),
					this.getObjectFromMap(map,"header-protocollo-richiesta"));
				setParameter(object, "setDigestRichiesta", Transazione.model().DIGEST_RICHIESTA.getFieldType(),
					this.getObjectFromMap(map,"digest-richiesta"));
				setParameter(object, "setProtocolloExtInfoRichiesta", Transazione.model().PROTOCOLLO_EXT_INFO_RICHIESTA.getFieldType(),
					this.getObjectFromMap(map,"protocollo-ext-info-richiesta"));
				setParameter(object, "setHeaderProtocolloRisposta", Transazione.model().HEADER_PROTOCOLLO_RISPOSTA.getFieldType(),
					this.getObjectFromMap(map,"header-protocollo-risposta"));
				setParameter(object, "setDigestRisposta", Transazione.model().DIGEST_RISPOSTA.getFieldType(),
					this.getObjectFromMap(map,"digest-risposta"));
				setParameter(object, "setProtocolloExtInfoRisposta", Transazione.model().PROTOCOLLO_EXT_INFO_RISPOSTA.getFieldType(),
					this.getObjectFromMap(map,"protocollo-ext-info-risposta"));
				setParameter(object, "setTracciaRichiesta", Transazione.model().TRACCIA_RICHIESTA.getFieldType(),
					this.getObjectFromMap(map,"traccia-richiesta"));
				setParameter(object, "setTracciaRisposta", Transazione.model().TRACCIA_RISPOSTA.getFieldType(),
					this.getObjectFromMap(map,"traccia-risposta"));
				setParameter(object, "setDiagnostici", Transazione.model().DIAGNOSTICI.getFieldType(),
					this.getObjectFromMap(map,"diagnostici"));
				setParameter(object, "setDiagnosticiList1", Transazione.model().DIAGNOSTICI_LIST_1.getFieldType(),
					this.getObjectFromMap(map,"diagnostici-list1"));
				setParameter(object, "setDiagnosticiList2", Transazione.model().DIAGNOSTICI_LIST_2.getFieldType(),
					this.getObjectFromMap(map,"diagnostici-list2"));
				setParameter(object, "setDiagnosticiListExt", Transazione.model().DIAGNOSTICI_LIST_EXT.getFieldType(),
					this.getObjectFromMap(map,"diagnostici-list-ext"));
				setParameter(object, "setDiagnosticiExt", Transazione.model().DIAGNOSTICI_EXT.getFieldType(),
					this.getObjectFromMap(map,"diagnostici-ext"));
				setParameter(object, "setIdCorrelazioneApplicativa", Transazione.model().ID_CORRELAZIONE_APPLICATIVA.getFieldType(),
					this.getObjectFromMap(map,"id-correlazione-applicativa"));
				setParameter(object, "setIdCorrelazioneApplicativaRisposta", Transazione.model().ID_CORRELAZIONE_APPLICATIVA_RISPOSTA.getFieldType(),
					this.getObjectFromMap(map,"id-correlazione-applicativa-risposta"));
				setParameter(object, "setServizioApplicativoFruitore", Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE.getFieldType(),
					this.getObjectFromMap(map,"servizio-applicativo-fruitore"));
				setParameter(object, "setServizioApplicativoErogatore", Transazione.model().SERVIZIO_APPLICATIVO_EROGATORE.getFieldType(),
					this.getObjectFromMap(map,"servizio-applicativo-erogatore"));
				setParameter(object, "setOperazioneIm", Transazione.model().OPERAZIONE_IM.getFieldType(),
					this.getObjectFromMap(map,"operazione-im"));
				setParameter(object, "setLocationRichiesta", Transazione.model().LOCATION_RICHIESTA.getFieldType(),
					this.getObjectFromMap(map,"location-richiesta"));
				setParameter(object, "setLocationRisposta", Transazione.model().LOCATION_RISPOSTA.getFieldType(),
					this.getObjectFromMap(map,"location-risposta"));
				setParameter(object, "setNomePorta", Transazione.model().NOME_PORTA.getFieldType(),
					this.getObjectFromMap(map,"nome-porta"));
				setParameter(object, "setCredenziali", Transazione.model().CREDENZIALI.getFieldType(),
					this.getObjectFromMap(map,"credenziali"));
				setParameter(object, "setLocationConnettore", Transazione.model().LOCATION_CONNETTORE.getFieldType(),
					this.getObjectFromMap(map,"location-connettore"));
				setParameter(object, "setUrlInvocazione", Transazione.model().URL_INVOCAZIONE.getFieldType(),
					this.getObjectFromMap(map,"url-invocazione"));
				setParameter(object, "setTrasportoMittente", Transazione.model().TRASPORTO_MITTENTE.getFieldType(),
					this.getObjectFromMap(map,"trasporto-mittente"));
				setParameter(object, "setTokenIssuer", Transazione.model().TOKEN_ISSUER.getFieldType(),
					this.getObjectFromMap(map,"token-issuer"));
				setParameter(object, "setTokenClientId", Transazione.model().TOKEN_CLIENT_ID.getFieldType(),
					this.getObjectFromMap(map,"token-client-id"));
				setParameter(object, "setTokenSubject", Transazione.model().TOKEN_SUBJECT.getFieldType(),
					this.getObjectFromMap(map,"token-subject"));
				setParameter(object, "setTokenUsername", Transazione.model().TOKEN_USERNAME.getFieldType(),
					this.getObjectFromMap(map,"token-username"));
				setParameter(object, "setTokenMail", Transazione.model().TOKEN_MAIL.getFieldType(),
					this.getObjectFromMap(map,"token-mail"));
				setParameter(object, "setTokenInfo", Transazione.model().TOKEN_INFO.getFieldType(),
					this.getObjectFromMap(map,"token-info"));
				setParameter(object, "setTempiElaborazione", Transazione.model().TEMPI_ELABORAZIONE.getFieldType(),
					this.getObjectFromMap(map,"tempi-elaborazione"));
				setParameter(object, "setDuplicatiRichiesta", Transazione.model().DUPLICATI_RICHIESTA.getFieldType(),
					this.getObjectFromMap(map,"duplicati-richiesta"));
				setParameter(object, "setDuplicatiRisposta", Transazione.model().DUPLICATI_RISPOSTA.getFieldType(),
					this.getObjectFromMap(map,"duplicati-risposta"));
				setParameter(object, "setClusterId", Transazione.model().CLUSTER_ID.getFieldType(),
					this.getObjectFromMap(map,"cluster-id"));
				setParameter(object, "setSocketClientAddress", Transazione.model().SOCKET_CLIENT_ADDRESS.getFieldType(),
					this.getObjectFromMap(map,"socket-client-address"));
				setParameter(object, "setTransportClientAddress", Transazione.model().TRANSPORT_CLIENT_ADDRESS.getFieldType(),
					this.getObjectFromMap(map,"transport-client-address"));
				setParameter(object, "setClientAddress", Transazione.model().CLIENT_ADDRESS.getFieldType(),
					this.getObjectFromMap(map,"client-address"));
				setParameter(object, "setEventiGestione", Transazione.model().EVENTI_GESTIONE.getFieldType(),
					this.getObjectFromMap(map,"eventi-gestione"));
				setParameter(object, "setTipoApi", Transazione.model().TIPO_API.getFieldType(),
					this.getObjectFromMap(map,"tipo-api"));
				setParameter(object, "setGruppi", Transazione.model().GRUPPI.getFieldType(),
					this.getObjectFromMap(map,"gruppi"));
				return object;
			}
			if(model.equals(Transazione.model().TRANSAZIONE_EXTENDED_INFO)){
				TransazioneExtendedInfo object = new TransazioneExtendedInfo();
				setParameter(object, "setId", Long.class,
					this.getObjectFromMap(map,"transazione-extended-info.id"));
				setParameter(object, "setNome", Transazione.model().TRANSAZIONE_EXTENDED_INFO.NOME.getFieldType(),
					this.getObjectFromMap(map,"transazione-extended-info.nome"));
				setParameter(object, "setValore", Transazione.model().TRANSAZIONE_EXTENDED_INFO.VALORE.getFieldType(),
					this.getObjectFromMap(map,"transazione-extended-info.valore"));
				return object;
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by fetch: "+this.getClass().getName());
			}	
					
		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in fetch: "+e.getMessage(),e);
		}
		
	}
	
	
	@Override
	public IKeyGeneratorObject getKeyGeneratorObject( IModel<?> model )  throws ServiceException {
		
		try{

			if(model.equals(Transazione.model())){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("transazioni","id","seq_transazioni","transazioni_init_seq");
			}
			if(model.equals(Transazione.model().TRANSAZIONE_EXTENDED_INFO)){
				return new org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject("transazione_extended_info","id","seq_transazione_extended_info","transazione_extended_info_init_seq");
			}
			
			else{
				throw new ServiceException("Model ["+model.toString()+"] not supported by getKeyGeneratorObject: "+this.getClass().getName());
			}

		}catch(Exception e){
			throw new ServiceException("Model ["+model.toString()+"] occurs error in getKeyGeneratorObject: "+e.getMessage(),e);
		}
		
	}

}
