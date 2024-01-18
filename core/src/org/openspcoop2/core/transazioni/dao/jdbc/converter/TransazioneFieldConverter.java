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
package org.openspcoop2.core.transazioni.dao.jdbc.converter;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.transazioni.Transazione;


/**     
 * TransazioneFieldConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransazioneFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public TransazioneFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public TransazioneFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return Transazione.model();
	}
	
	@Override
	public TipiDatabase getDatabaseType() throws ExpressionException {
		return this.databaseType;
	}
	


	@Override
	public String toColumn(IField field,boolean returnAlias,boolean appendTablePrefix) throws ExpressionException {
		
		// In the case of columns with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the column containing the alias
		
		if(field.equals(Transazione.model().ID_TRANSAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".id";
			}else{
				return "id";
			}
		}
		if(field.equals(Transazione.model().STATO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".stato";
			}else{
				return "stato";
			}
		}
		if(field.equals(Transazione.model().RUOLO_TRANSAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".ruolo_transazione";
			}else{
				return "ruolo_transazione";
			}
		}
		if(field.equals(Transazione.model().ESITO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".esito";
			}else{
				return "esito";
			}
		}
		if(field.equals(Transazione.model().ESITO_SINCRONO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".esito_sincrono";
			}else{
				return "esito_sincrono";
			}
		}
		if(field.equals(Transazione.model().CONSEGNE_MULTIPLE_IN_CORSO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".consegne_multiple";
			}else{
				return "consegne_multiple";
			}
		}
		if(field.equals(Transazione.model().ESITO_CONTESTO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".esito_contesto";
			}else{
				return "esito_contesto";
			}
		}
		if(field.equals(Transazione.model().PROTOCOLLO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".protocollo";
			}else{
				return "protocollo";
			}
		}
		if(field.equals(Transazione.model().TIPO_RICHIESTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_richiesta";
			}else{
				return "tipo_richiesta";
			}
		}
		if(field.equals(Transazione.model().CODICE_RISPOSTA_INGRESSO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".codice_risposta_ingresso";
			}else{
				return "codice_risposta_ingresso";
			}
		}
		if(field.equals(Transazione.model().CODICE_RISPOSTA_USCITA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".codice_risposta_uscita";
			}else{
				return "codice_risposta_uscita";
			}
		}
		if(field.equals(Transazione.model().DATA_ACCETTAZIONE_RICHIESTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_accettazione_richiesta";
			}else{
				return "data_accettazione_richiesta";
			}
		}
		if(field.equals(Transazione.model().DATA_INGRESSO_RICHIESTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_ingresso_richiesta";
			}else{
				return "data_ingresso_richiesta";
			}
		}
		if(field.equals(Transazione.model().DATA_INGRESSO_RICHIESTA_STREAM)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_ingresso_richiesta_stream";
			}else{
				return "data_ingresso_richiesta_stream";
			}
		}
		if(field.equals(Transazione.model().DATA_USCITA_RICHIESTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_uscita_richiesta";
			}else{
				return "data_uscita_richiesta";
			}
		}
		if(field.equals(Transazione.model().DATA_USCITA_RICHIESTA_STREAM)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_uscita_richiesta_stream";
			}else{
				return "data_uscita_richiesta_stream";
			}
		}
		if(field.equals(Transazione.model().DATA_ACCETTAZIONE_RISPOSTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_accettazione_risposta";
			}else{
				return "data_accettazione_risposta";
			}
		}
		if(field.equals(Transazione.model().DATA_INGRESSO_RISPOSTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_ingresso_risposta";
			}else{
				return "data_ingresso_risposta";
			}
		}
		if(field.equals(Transazione.model().DATA_INGRESSO_RISPOSTA_STREAM)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_ingresso_risposta_stream";
			}else{
				return "data_ingresso_risposta_stream";
			}
		}
		if(field.equals(Transazione.model().DATA_USCITA_RISPOSTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_uscita_risposta";
			}else{
				return "data_uscita_risposta";
			}
		}
		if(field.equals(Transazione.model().DATA_USCITA_RISPOSTA_STREAM)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_uscita_risposta_stream";
			}else{
				return "data_uscita_risposta_stream";
			}
		}
		if(field.equals(Transazione.model().RICHIESTA_INGRESSO_BYTES)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".richiesta_ingresso_bytes";
			}else{
				return "richiesta_ingresso_bytes";
			}
		}
		if(field.equals(Transazione.model().RICHIESTA_USCITA_BYTES)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".richiesta_uscita_bytes";
			}else{
				return "richiesta_uscita_bytes";
			}
		}
		if(field.equals(Transazione.model().RISPOSTA_INGRESSO_BYTES)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".risposta_ingresso_bytes";
			}else{
				return "risposta_ingresso_bytes";
			}
		}
		if(field.equals(Transazione.model().RISPOSTA_USCITA_BYTES)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".risposta_uscita_bytes";
			}else{
				return "risposta_uscita_bytes";
			}
		}
		if(field.equals(Transazione.model().PDD_CODICE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".pdd_codice";
			}else{
				return "pdd_codice";
			}
		}
		if(field.equals(Transazione.model().PDD_TIPO_SOGGETTO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".pdd_tipo_soggetto";
			}else{
				return "pdd_tipo_soggetto";
			}
		}
		if(field.equals(Transazione.model().PDD_NOME_SOGGETTO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".pdd_nome_soggetto";
			}else{
				return "pdd_nome_soggetto";
			}
		}
		if(field.equals(Transazione.model().PDD_RUOLO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".pdd_ruolo";
			}else{
				return "pdd_ruolo";
			}
		}
		if(field.equals(Transazione.model().FAULT_INTEGRAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".fault_integrazione";
			}else{
				return "fault_integrazione";
			}
		}
		if(field.equals(Transazione.model().FORMATO_FAULT_INTEGRAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".formato_fault_integrazione";
			}else{
				return "formato_fault_integrazione";
			}
		}
		if(field.equals(Transazione.model().FAULT_COOPERAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".fault_cooperazione";
			}else{
				return "fault_cooperazione";
			}
		}
		if(field.equals(Transazione.model().FORMATO_FAULT_COOPERAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".formato_fault_cooperazione";
			}else{
				return "formato_fault_cooperazione";
			}
		}
		if(field.equals(Transazione.model().TIPO_SOGGETTO_FRUITORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_soggetto_fruitore";
			}else{
				return "tipo_soggetto_fruitore";
			}
		}
		if(field.equals(Transazione.model().NOME_SOGGETTO_FRUITORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_soggetto_fruitore";
			}else{
				return "nome_soggetto_fruitore";
			}
		}
		if(field.equals(Transazione.model().IDPORTA_SOGGETTO_FRUITORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".idporta_soggetto_fruitore";
			}else{
				return "idporta_soggetto_fruitore";
			}
		}
		if(field.equals(Transazione.model().INDIRIZZO_SOGGETTO_FRUITORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".indirizzo_soggetto_fruitore";
			}else{
				return "indirizzo_soggetto_fruitore";
			}
		}
		if(field.equals(Transazione.model().TIPO_SOGGETTO_EROGATORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_soggetto_erogatore";
			}else{
				return "tipo_soggetto_erogatore";
			}
		}
		if(field.equals(Transazione.model().NOME_SOGGETTO_EROGATORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_soggetto_erogatore";
			}else{
				return "nome_soggetto_erogatore";
			}
		}
		if(field.equals(Transazione.model().IDPORTA_SOGGETTO_EROGATORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".idporta_soggetto_erogatore";
			}else{
				return "idporta_soggetto_erogatore";
			}
		}
		if(field.equals(Transazione.model().INDIRIZZO_SOGGETTO_EROGATORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".indirizzo_soggetto_erogatore";
			}else{
				return "indirizzo_soggetto_erogatore";
			}
		}
		if(field.equals(Transazione.model().ID_MESSAGGIO_RICHIESTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".id_messaggio_richiesta";
			}else{
				return "id_messaggio_richiesta";
			}
		}
		if(field.equals(Transazione.model().ID_MESSAGGIO_RISPOSTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".id_messaggio_risposta";
			}else{
				return "id_messaggio_risposta";
			}
		}
		if(field.equals(Transazione.model().DATA_ID_MSG_RICHIESTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_id_msg_richiesta";
			}else{
				return "data_id_msg_richiesta";
			}
		}
		if(field.equals(Transazione.model().DATA_ID_MSG_RISPOSTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_id_msg_risposta";
			}else{
				return "data_id_msg_risposta";
			}
		}
		if(field.equals(Transazione.model().PROFILO_COLLABORAZIONE_OP_2)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".profilo_collaborazione_op2";
			}else{
				return "profilo_collaborazione_op2";
			}
		}
		if(field.equals(Transazione.model().PROFILO_COLLABORAZIONE_PROT)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".profilo_collaborazione_prot";
			}else{
				return "profilo_collaborazione_prot";
			}
		}
		if(field.equals(Transazione.model().ID_COLLABORAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".id_collaborazione";
			}else{
				return "id_collaborazione";
			}
		}
		if(field.equals(Transazione.model().URI_ACCORDO_SERVIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".uri_accordo_servizio";
			}else{
				return "uri_accordo_servizio";
			}
		}
		if(field.equals(Transazione.model().TIPO_SERVIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_servizio";
			}else{
				return "tipo_servizio";
			}
		}
		if(field.equals(Transazione.model().NOME_SERVIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_servizio";
			}else{
				return "nome_servizio";
			}
		}
		if(field.equals(Transazione.model().VERSIONE_SERVIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".versione_servizio";
			}else{
				return "versione_servizio";
			}
		}
		if(field.equals(Transazione.model().AZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".azione";
			}else{
				return "azione";
			}
		}
		if(field.equals(Transazione.model().ID_ASINCRONO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".id_asincrono";
			}else{
				return "id_asincrono";
			}
		}
		if(field.equals(Transazione.model().TIPO_SERVIZIO_CORRELATO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_servizio_correlato";
			}else{
				return "tipo_servizio_correlato";
			}
		}
		if(field.equals(Transazione.model().NOME_SERVIZIO_CORRELATO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_servizio_correlato";
			}else{
				return "nome_servizio_correlato";
			}
		}
		if(field.equals(Transazione.model().HEADER_PROTOCOLLO_RICHIESTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".header_protocollo_richiesta";
			}else{
				return "header_protocollo_richiesta";
			}
		}
		if(field.equals(Transazione.model().DIGEST_RICHIESTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".digest_richiesta";
			}else{
				return "digest_richiesta";
			}
		}
		if(field.equals(Transazione.model().PROTOCOLLO_EXT_INFO_RICHIESTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".prot_ext_info_richiesta";
			}else{
				return "prot_ext_info_richiesta";
			}
		}
		if(field.equals(Transazione.model().HEADER_PROTOCOLLO_RISPOSTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".header_protocollo_risposta";
			}else{
				return "header_protocollo_risposta";
			}
		}
		if(field.equals(Transazione.model().DIGEST_RISPOSTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".digest_risposta";
			}else{
				return "digest_risposta";
			}
		}
		if(field.equals(Transazione.model().PROTOCOLLO_EXT_INFO_RISPOSTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".prot_ext_info_risposta";
			}else{
				return "prot_ext_info_risposta";
			}
		}
		if(field.equals(Transazione.model().TRACCIA_RICHIESTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".traccia_richiesta";
			}else{
				return "traccia_richiesta";
			}
		}
		if(field.equals(Transazione.model().TRACCIA_RISPOSTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".traccia_risposta";
			}else{
				return "traccia_risposta";
			}
		}
		if(field.equals(Transazione.model().DIAGNOSTICI)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".diagnostici";
			}else{
				return "diagnostici";
			}
		}
		if(field.equals(Transazione.model().DIAGNOSTICI_LIST_1)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".diagnostici_list_1";
			}else{
				return "diagnostici_list_1";
			}
		}
		if(field.equals(Transazione.model().DIAGNOSTICI_LIST_2)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".diagnostici_list_2";
			}else{
				return "diagnostici_list_2";
			}
		}
		if(field.equals(Transazione.model().DIAGNOSTICI_LIST_EXT)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".diagnostici_list_ext";
			}else{
				return "diagnostici_list_ext";
			}
		}
		if(field.equals(Transazione.model().DIAGNOSTICI_EXT)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".diagnostici_ext";
			}else{
				return "diagnostici_ext";
			}
		}
		if(field.equals(Transazione.model().ERROR_LOG)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".error_log";
			}else{
				return "error_log";
			}
		}
		if(field.equals(Transazione.model().WARNING_LOG)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".warning_log";
			}else{
				return "warning_log";
			}
		}
		if(field.equals(Transazione.model().ID_CORRELAZIONE_APPLICATIVA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".id_correlazione_applicativa";
			}else{
				return "id_correlazione_applicativa";
			}
		}
		if(field.equals(Transazione.model().ID_CORRELAZIONE_APPLICATIVA_RISPOSTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".id_correlazione_risposta";
			}else{
				return "id_correlazione_risposta";
			}
		}
		if(field.equals(Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".servizio_applicativo_fruitore";
			}else{
				return "servizio_applicativo_fruitore";
			}
		}
		if(field.equals(Transazione.model().SERVIZIO_APPLICATIVO_EROGATORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".servizio_applicativo_erogatore";
			}else{
				return "servizio_applicativo_erogatore";
			}
		}
		if(field.equals(Transazione.model().OPERAZIONE_IM)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".operazione_im";
			}else{
				return "operazione_im";
			}
		}
		if(field.equals(Transazione.model().LOCATION_RICHIESTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".location_richiesta";
			}else{
				return "location_richiesta";
			}
		}
		if(field.equals(Transazione.model().LOCATION_RISPOSTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".location_risposta";
			}else{
				return "location_risposta";
			}
		}
		if(field.equals(Transazione.model().NOME_PORTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome_porta";
			}else{
				return "nome_porta";
			}
		}
		if(field.equals(Transazione.model().CREDENZIALI)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".credenziali";
			}else{
				return "credenziali";
			}
		}
		if(field.equals(Transazione.model().LOCATION_CONNETTORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".location_connettore";
			}else{
				return "location_connettore";
			}
		}
		if(field.equals(Transazione.model().URL_INVOCAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".url_invocazione";
			}else{
				return "url_invocazione";
			}
		}
		if(field.equals(Transazione.model().TRASPORTO_MITTENTE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".trasporto_mittente";
			}else{
				return "trasporto_mittente";
			}
		}
		if(field.equals(Transazione.model().TOKEN_ISSUER)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".token_issuer";
			}else{
				return "token_issuer";
			}
		}
		if(field.equals(Transazione.model().TOKEN_CLIENT_ID)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".token_client_id";
			}else{
				return "token_client_id";
			}
		}
		if(field.equals(Transazione.model().TOKEN_SUBJECT)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".token_subject";
			}else{
				return "token_subject";
			}
		}
		if(field.equals(Transazione.model().TOKEN_USERNAME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".token_username";
			}else{
				return "token_username";
			}
		}
		if(field.equals(Transazione.model().TOKEN_MAIL)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".token_mail";
			}else{
				return "token_mail";
			}
		}
		if(field.equals(Transazione.model().TOKEN_INFO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".token_info";
			}else{
				return "token_info";
			}
		}
		if(field.equals(Transazione.model().TEMPI_ELABORAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tempi_elaborazione";
			}else{
				return "tempi_elaborazione";
			}
		}
		if(field.equals(Transazione.model().DUPLICATI_RICHIESTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".duplicati_richiesta";
			}else{
				return "duplicati_richiesta";
			}
		}
		if(field.equals(Transazione.model().DUPLICATI_RISPOSTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".duplicati_risposta";
			}else{
				return "duplicati_risposta";
			}
		}
		if(field.equals(Transazione.model().CLUSTER_ID)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".cluster_id";
			}else{
				return "cluster_id";
			}
		}
		if(field.equals(Transazione.model().SOCKET_CLIENT_ADDRESS)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".socket_client_address";
			}else{
				return "socket_client_address";
			}
		}
		if(field.equals(Transazione.model().TRANSPORT_CLIENT_ADDRESS)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".transport_client_address";
			}else{
				return "transport_client_address";
			}
		}
		if(field.equals(Transazione.model().CLIENT_ADDRESS)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".client_address";
			}else{
				return "client_address";
			}
		}
		if(field.equals(Transazione.model().EVENTI_GESTIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".eventi_gestione";
			}else{
				return "eventi_gestione";
			}
		}
		if(field.equals(Transazione.model().TIPO_API)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_api";
			}else{
				return "tipo_api";
			}
		}
		if(field.equals(Transazione.model().URI_API)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".uri_api";
			}else{
				return "uri_api";
			}
		}
		if(field.equals(Transazione.model().GRUPPI)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".gruppi";
			}else{
				return "gruppi";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.ID_TRANSAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".id_transazione";
			}else{
				return "id_transazione";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.PROTOCOLLO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".protocollo";
			}else{
				return "protocollo";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.SERVIZIO_APPLICATIVO_EROGATORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".servizio_applicativo_erogatore";
			}else{
				return "servizio_applicativo_erogatore";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.DATA_CONSEGNA_EROGATORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_consegna_erogatore";
			}else{
				return "data_consegna_erogatore";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.TIPO_MESSAGGIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo_messaggio";
			}else{
				return "tipo_messaggio";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.FORMATO_MESSAGGIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".formato_messaggio";
			}else{
				return "formato_messaggio";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.CONTENT_TYPE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".content_type";
			}else{
				return "content_type";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.CONTENT_LENGTH)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".content_length";
			}else{
				return "content_length";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.MULTIPART_CONTENT_TYPE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".multipart_content_type";
			}else{
				return "multipart_content_type";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.MULTIPART_CONTENT_ID)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".multipart_content_id";
			}else{
				return "multipart_content_id";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.MULTIPART_CONTENT_LOCATION)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".multipart_content_location";
			}else{
				return "multipart_content_location";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.MULTIPART_HEADER.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.MULTIPART_HEADER.VALORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".valore";
			}else{
				return "valore";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.MULTIPART_HEADER.DUMP_TIMESTAMP)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".dump_timestamp";
			}else{
				return "dump_timestamp";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.BODY)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".body";
			}else{
				return "body";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.HEADER_TRASPORTO.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.HEADER_TRASPORTO.VALORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".valore";
			}else{
				return "valore";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.HEADER_TRASPORTO.DUMP_TIMESTAMP)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".dump_timestamp";
			}else{
				return "dump_timestamp";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.CONTENT_TYPE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".content_type";
			}else{
				return "content_type";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.CONTENT_ID)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".content_id";
			}else{
				return "content_id";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.CONTENT_LOCATION)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".content_location";
			}else{
				return "content_location";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.ALLEGATO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".allegato";
			}else{
				return "allegato";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.HEADER.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.HEADER.VALORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".valore";
			}else{
				return "valore";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.HEADER.DUMP_TIMESTAMP)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".dump_timestamp";
			}else{
				return "dump_timestamp";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.DUMP_TIMESTAMP)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".dump_timestamp";
			}else{
				return "dump_timestamp";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.HEADER_EXT)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".header_ext";
			}else{
				return "header_ext";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.CONTENUTO.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.CONTENUTO.VALORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".valore";
			}else{
				return "valore";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.CONTENUTO.VALORE_AS_BYTES)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".valore_as_bytes";
			}else{
				return "valore_as_bytes";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.CONTENUTO.DUMP_TIMESTAMP)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".dump_timestamp";
			}else{
				return "dump_timestamp";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.DUMP_TIMESTAMP)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".dump_timestamp";
			}else{
				return "dump_timestamp";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.POST_PROCESS_HEADER)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".post_process_header";
			}else{
				return "post_process_header";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.POST_PROCESS_FILENAME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".post_process_filename";
			}else{
				return "post_process_filename";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.POST_PROCESS_CONTENT)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".post_process_content";
			}else{
				return "post_process_content";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.POST_PROCESS_CONFIG_ID)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".post_process_config_id";
			}else{
				return "post_process_config_id";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.POST_PROCESS_TIMESTAMP)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".post_process_timestamp";
			}else{
				return "post_process_timestamp";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.POST_PROCESSED)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".post_processed";
			}else{
				return "post_processed";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.MULTIPART_HEADER_EXT)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".multipart_header_ext";
			}else{
				return "multipart_header_ext";
			}
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.HEADER_EXT)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".header_ext";
			}else{
				return "header_ext";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.ID_TRANSAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".id_transazione";
			}else{
				return "id_transazione";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.SERVIZIO_APPLICATIVO_EROGATORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".servizio_applicativo_erogatore";
			}else{
				return "servizio_applicativo_erogatore";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.CONNETTORE_NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".connettore_nome";
			}else{
				return "connettore_nome";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DATA_REGISTRAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_registrazione";
			}else{
				return "data_registrazione";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.CONSEGNA_TERMINATA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".consegna_terminata";
			}else{
				return "consegna_terminata";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DATA_MESSAGGIO_SCADUTO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_messaggio_scaduto";
			}else{
				return "data_messaggio_scaduto";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DETTAGLIO_ESITO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".dettaglio_esito";
			}else{
				return "dettaglio_esito";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.CONSEGNA_TRASPARENTE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".consegna_trasparente";
			}else{
				return "consegna_trasparente";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.CONSEGNA_INTEGRATION_MANAGER)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".consegna_im";
			}else{
				return "consegna_im";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.IDENTIFICATIVO_MESSAGGIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".identificativo_messaggio";
			}else{
				return "identificativo_messaggio";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DATA_ACCETTAZIONE_RICHIESTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_accettazione_richiesta";
			}else{
				return "data_accettazione_richiesta";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DATA_USCITA_RICHIESTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_uscita_richiesta";
			}else{
				return "data_uscita_richiesta";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DATA_ACCETTAZIONE_RISPOSTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_accettazione_risposta";
			}else{
				return "data_accettazione_risposta";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DATA_INGRESSO_RISPOSTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_ingresso_risposta";
			}else{
				return "data_ingresso_risposta";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.RICHIESTA_USCITA_BYTES)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".richiesta_uscita_bytes";
			}else{
				return "richiesta_uscita_bytes";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.RISPOSTA_INGRESSO_BYTES)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".risposta_ingresso_bytes";
			}else{
				return "risposta_ingresso_bytes";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.LOCATION_CONNETTORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".location_connettore";
			}else{
				return "location_connettore";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.CODICE_RISPOSTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".codice_risposta";
			}else{
				return "codice_risposta";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.FAULT)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".fault";
			}else{
				return "fault";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.FORMATO_FAULT)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".formato_fault";
			}else{
				return "formato_fault";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DATA_PRIMO_TENTATIVO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_primo_tentativo";
			}else{
				return "data_primo_tentativo";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.NUMERO_TENTATIVI)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".numero_tentativi";
			}else{
				return "numero_tentativi";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.CLUSTER_ID_PRESA_IN_CARICO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".cluster_id_in_coda";
			}else{
				return "cluster_id_in_coda";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.CLUSTER_ID_CONSEGNA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".cluster_id_consegna";
			}else{
				return "cluster_id_consegna";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DATA_ULTIMO_ERRORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_ultimo_errore";
			}else{
				return "data_ultimo_errore";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DETTAGLIO_ESITO_ULTIMO_ERRORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".dettaglio_esito_ultimo_errore";
			}else{
				return "dettaglio_esito_ultimo_errore";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.CODICE_RISPOSTA_ULTIMO_ERRORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".codice_risposta_ultimo_errore";
			}else{
				return "codice_risposta_ultimo_errore";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.ULTIMO_ERRORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".ultimo_errore";
			}else{
				return "ultimo_errore";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.LOCATION_ULTIMO_ERRORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".location_ultimo_errore";
			}else{
				return "location_ultimo_errore";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.CLUSTER_ID_ULTIMO_ERRORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".cluster_id_ultimo_errore";
			}else{
				return "cluster_id_ultimo_errore";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.FAULT_ULTIMO_ERRORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".fault_ultimo_errore";
			}else{
				return "fault_ultimo_errore";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.FORMATO_FAULT_ULTIMO_ERRORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".formato_fault_ultimo_errore";
			}else{
				return "formato_fault_ultimo_errore";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DATA_PRIMO_PRELIEVO_IM)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_primo_prelievo_im";
			}else{
				return "data_primo_prelievo_im";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DATA_PRELIEVO_IM)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_prelievo_im";
			}else{
				return "data_prelievo_im";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.NUMERO_PRELIEVI_IM)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".numero_prelievi_im";
			}else{
				return "numero_prelievi_im";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DATA_ELIMINAZIONE_IM)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_eliminazione_im";
			}else{
				return "data_eliminazione_im";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.CLUSTER_ID_PRELIEVO_IM)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".cluster_id_prelievo_im";
			}else{
				return "cluster_id_prelievo_im";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.CLUSTER_ID_ELIMINAZIONE_IM)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".cluster_id_eliminazione_im";
			}else{
				return "cluster_id_eliminazione_im";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_EXTENDED_INFO.NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(Transazione.model().TRANSAZIONE_EXTENDED_INFO.VALORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".valore";
			}else{
				return "valore";
			}
		}


		return super.toColumn(field,returnAlias,appendTablePrefix);
		
	}
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(field.equals(Transazione.model().ID_TRANSAZIONE)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().STATO)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().RUOLO_TRANSAZIONE)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().ESITO)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().ESITO_SINCRONO)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().CONSEGNE_MULTIPLE_IN_CORSO)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().ESITO_CONTESTO)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().PROTOCOLLO)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().TIPO_RICHIESTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().CODICE_RISPOSTA_INGRESSO)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().CODICE_RISPOSTA_USCITA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().DATA_ACCETTAZIONE_RICHIESTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().DATA_INGRESSO_RICHIESTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().DATA_INGRESSO_RICHIESTA_STREAM)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().DATA_USCITA_RICHIESTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().DATA_USCITA_RICHIESTA_STREAM)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().DATA_ACCETTAZIONE_RISPOSTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().DATA_INGRESSO_RISPOSTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().DATA_INGRESSO_RISPOSTA_STREAM)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().DATA_USCITA_RISPOSTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().DATA_USCITA_RISPOSTA_STREAM)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().RICHIESTA_INGRESSO_BYTES)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().RICHIESTA_USCITA_BYTES)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().RISPOSTA_INGRESSO_BYTES)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().RISPOSTA_USCITA_BYTES)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().PDD_CODICE)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().PDD_TIPO_SOGGETTO)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().PDD_NOME_SOGGETTO)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().PDD_RUOLO)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().FAULT_INTEGRAZIONE)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().FORMATO_FAULT_INTEGRAZIONE)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().FAULT_COOPERAZIONE)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().FORMATO_FAULT_COOPERAZIONE)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().TIPO_SOGGETTO_FRUITORE)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().NOME_SOGGETTO_FRUITORE)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().IDPORTA_SOGGETTO_FRUITORE)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().INDIRIZZO_SOGGETTO_FRUITORE)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().TIPO_SOGGETTO_EROGATORE)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().NOME_SOGGETTO_EROGATORE)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().IDPORTA_SOGGETTO_EROGATORE)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().INDIRIZZO_SOGGETTO_EROGATORE)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().ID_MESSAGGIO_RICHIESTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().ID_MESSAGGIO_RISPOSTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().DATA_ID_MSG_RICHIESTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().DATA_ID_MSG_RISPOSTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().PROFILO_COLLABORAZIONE_OP_2)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().PROFILO_COLLABORAZIONE_PROT)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().ID_COLLABORAZIONE)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().URI_ACCORDO_SERVIZIO)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().TIPO_SERVIZIO)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().NOME_SERVIZIO)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().VERSIONE_SERVIZIO)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().AZIONE)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().ID_ASINCRONO)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().TIPO_SERVIZIO_CORRELATO)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().NOME_SERVIZIO_CORRELATO)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().HEADER_PROTOCOLLO_RICHIESTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().DIGEST_RICHIESTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().PROTOCOLLO_EXT_INFO_RICHIESTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().HEADER_PROTOCOLLO_RISPOSTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().DIGEST_RISPOSTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().PROTOCOLLO_EXT_INFO_RISPOSTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().TRACCIA_RICHIESTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().TRACCIA_RISPOSTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().DIAGNOSTICI)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().DIAGNOSTICI_LIST_1)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().DIAGNOSTICI_LIST_2)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().DIAGNOSTICI_LIST_EXT)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().DIAGNOSTICI_EXT)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().ERROR_LOG)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().WARNING_LOG)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().ID_CORRELAZIONE_APPLICATIVA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().ID_CORRELAZIONE_APPLICATIVA_RISPOSTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().SERVIZIO_APPLICATIVO_EROGATORE)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().OPERAZIONE_IM)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().LOCATION_RICHIESTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().LOCATION_RISPOSTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().NOME_PORTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().CREDENZIALI)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().LOCATION_CONNETTORE)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().URL_INVOCAZIONE)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().TRASPORTO_MITTENTE)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().TOKEN_ISSUER)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().TOKEN_CLIENT_ID)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().TOKEN_SUBJECT)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().TOKEN_USERNAME)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().TOKEN_MAIL)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().TOKEN_INFO)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().TEMPI_ELABORAZIONE)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().DUPLICATI_RICHIESTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().DUPLICATI_RISPOSTA)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().CLUSTER_ID)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().SOCKET_CLIENT_ADDRESS)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().TRANSPORT_CLIENT_ADDRESS)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().CLIENT_ADDRESS)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().EVENTI_GESTIONE)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().TIPO_API)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().URI_API)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().GRUPPI)){
			return this.toTable(Transazione.model(), returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.ID_TRANSAZIONE)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.PROTOCOLLO)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.SERVIZIO_APPLICATIVO_EROGATORE)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.DATA_CONSEGNA_EROGATORE)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.TIPO_MESSAGGIO)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.FORMATO_MESSAGGIO)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.CONTENT_TYPE)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.CONTENT_LENGTH)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.MULTIPART_CONTENT_TYPE)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.MULTIPART_CONTENT_ID)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.MULTIPART_CONTENT_LOCATION)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.MULTIPART_HEADER.NOME)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO.MULTIPART_HEADER, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.MULTIPART_HEADER.VALORE)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO.MULTIPART_HEADER, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.MULTIPART_HEADER.DUMP_TIMESTAMP)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO.MULTIPART_HEADER, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.BODY)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.HEADER_TRASPORTO.NOME)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO.HEADER_TRASPORTO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.HEADER_TRASPORTO.VALORE)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO.HEADER_TRASPORTO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.HEADER_TRASPORTO.DUMP_TIMESTAMP)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO.HEADER_TRASPORTO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.CONTENT_TYPE)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO.ALLEGATO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.CONTENT_ID)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO.ALLEGATO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.CONTENT_LOCATION)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO.ALLEGATO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.ALLEGATO)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO.ALLEGATO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.HEADER.NOME)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.HEADER, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.HEADER.VALORE)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.HEADER, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.HEADER.DUMP_TIMESTAMP)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.HEADER, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.DUMP_TIMESTAMP)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO.ALLEGATO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.HEADER_EXT)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO.ALLEGATO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.CONTENUTO.NOME)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO.CONTENUTO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.CONTENUTO.VALORE)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO.CONTENUTO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.CONTENUTO.VALORE_AS_BYTES)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO.CONTENUTO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.CONTENUTO.DUMP_TIMESTAMP)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO.CONTENUTO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.DUMP_TIMESTAMP)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.POST_PROCESS_HEADER)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.POST_PROCESS_FILENAME)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.POST_PROCESS_CONTENT)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.POST_PROCESS_CONFIG_ID)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.POST_PROCESS_TIMESTAMP)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.POST_PROCESSED)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.MULTIPART_HEADER_EXT)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO, returnAlias);
		}
		if(field.equals(Transazione.model().DUMP_MESSAGGIO.HEADER_EXT)){
			return this.toTable(Transazione.model().DUMP_MESSAGGIO, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.ID_TRANSAZIONE)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.SERVIZIO_APPLICATIVO_EROGATORE)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.CONNETTORE_NOME)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DATA_REGISTRAZIONE)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.CONSEGNA_TERMINATA)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DATA_MESSAGGIO_SCADUTO)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DETTAGLIO_ESITO)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.CONSEGNA_TRASPARENTE)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.CONSEGNA_INTEGRATION_MANAGER)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.IDENTIFICATIVO_MESSAGGIO)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DATA_ACCETTAZIONE_RICHIESTA)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DATA_USCITA_RICHIESTA)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DATA_ACCETTAZIONE_RISPOSTA)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DATA_INGRESSO_RISPOSTA)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.RICHIESTA_USCITA_BYTES)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.RISPOSTA_INGRESSO_BYTES)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.LOCATION_CONNETTORE)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.CODICE_RISPOSTA)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.FAULT)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.FORMATO_FAULT)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DATA_PRIMO_TENTATIVO)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.NUMERO_TENTATIVI)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.CLUSTER_ID_PRESA_IN_CARICO)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.CLUSTER_ID_CONSEGNA)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DATA_ULTIMO_ERRORE)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DETTAGLIO_ESITO_ULTIMO_ERRORE)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.CODICE_RISPOSTA_ULTIMO_ERRORE)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.ULTIMO_ERRORE)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.LOCATION_ULTIMO_ERRORE)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.CLUSTER_ID_ULTIMO_ERRORE)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.FAULT_ULTIMO_ERRORE)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.FORMATO_FAULT_ULTIMO_ERRORE)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DATA_PRIMO_PRELIEVO_IM)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DATA_PRELIEVO_IM)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.NUMERO_PRELIEVI_IM)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.DATA_ELIMINAZIONE_IM)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.CLUSTER_ID_PRELIEVO_IM)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER.CLUSTER_ID_ELIMINAZIONE_IM)){
			return this.toTable(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_EXTENDED_INFO.NOME)){
			return this.toTable(Transazione.model().TRANSAZIONE_EXTENDED_INFO, returnAlias);
		}
		if(field.equals(Transazione.model().TRANSAZIONE_EXTENDED_INFO.VALORE)){
			return this.toTable(Transazione.model().TRANSAZIONE_EXTENDED_INFO, returnAlias);
		}


		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(Transazione.model())){
			return CostantiDB.TRANSAZIONI;
		}
		if(model.equals(Transazione.model().DUMP_MESSAGGIO)){
			return CostantiDB.DUMP_MESSAGGI;
		}
		if(model.equals(Transazione.model().DUMP_MESSAGGIO.MULTIPART_HEADER)){
			return CostantiDB.DUMP_MULTIPART_HEADER;
		}
		if(model.equals(Transazione.model().DUMP_MESSAGGIO.HEADER_TRASPORTO)){
			return CostantiDB.DUMP_HEADER_TRASPORTO;
		}
		if(model.equals(Transazione.model().DUMP_MESSAGGIO.ALLEGATO)){
			return CostantiDB.DUMP_ALLEGATI;
		}
		if(model.equals(Transazione.model().DUMP_MESSAGGIO.ALLEGATO.HEADER)){
			return CostantiDB.DUMP_ALLEGATI_HEADER;
		}
		if(model.equals(Transazione.model().DUMP_MESSAGGIO.CONTENUTO)){
			return CostantiDB.DUMP_CONTENUTI;
		}
		if(model.equals(Transazione.model().TRANSAZIONE_APPLICATIVO_SERVER)){
			return CostantiDB.TRANSAZIONI_APPLICATIVI_SERVER;
		}
		if(model.equals(Transazione.model().TRANSAZIONE_EXTENDED_INFO)){
			return CostantiDB.TRANSAZIONI_EXTENDED_INFO;
		}


		return super.toTable(model,returnAlias);
		
	}

}
