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
package org.openspcoop2.core.transazioni.dao.jdbc.converter;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;


/**     
 * TransazioneApplicativoServerFieldConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransazioneApplicativoServerFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public TransazioneApplicativoServerFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public TransazioneApplicativoServerFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return TransazioneApplicativoServer.model();
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
		
		if(field.equals(TransazioneApplicativoServer.model().ID_TRANSAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".id_transazione";
			}else{
				return "id_transazione";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().SERVIZIO_APPLICATIVO_EROGATORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".servizio_applicativo_erogatore";
			}else{
				return "servizio_applicativo_erogatore";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().CONNETTORE_NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".connettore_nome";
			}else{
				return "connettore_nome";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_REGISTRAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_registrazione";
			}else{
				return "data_registrazione";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().CONSEGNA_TERMINATA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".consegna_terminata";
			}else{
				return "consegna_terminata";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_MESSAGGIO_SCADUTO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_messaggio_scaduto";
			}else{
				return "data_messaggio_scaduto";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().DETTAGLIO_ESITO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".dettaglio_esito";
			}else{
				return "dettaglio_esito";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().CONSEGNA_TRASPARENTE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".consegna_trasparente";
			}else{
				return "consegna_trasparente";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().CONSEGNA_INTEGRATION_MANAGER)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".consegna_im";
			}else{
				return "consegna_im";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().IDENTIFICATIVO_MESSAGGIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".identificativo_messaggio";
			}else{
				return "identificativo_messaggio";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RICHIESTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_accettazione_richiesta";
			}else{
				return "data_accettazione_richiesta";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_USCITA_RICHIESTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_uscita_richiesta";
			}else{
				return "data_uscita_richiesta";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RISPOSTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_accettazione_risposta";
			}else{
				return "data_accettazione_risposta";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_INGRESSO_RISPOSTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_ingresso_risposta";
			}else{
				return "data_ingresso_risposta";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().RICHIESTA_USCITA_BYTES)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".richiesta_uscita_bytes";
			}else{
				return "richiesta_uscita_bytes";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().RISPOSTA_INGRESSO_BYTES)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".risposta_ingresso_bytes";
			}else{
				return "risposta_ingresso_bytes";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().LOCATION_CONNETTORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".location_connettore";
			}else{
				return "location_connettore";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().CODICE_RISPOSTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".codice_risposta";
			}else{
				return "codice_risposta";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().FAULT)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".fault";
			}else{
				return "fault";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().FORMATO_FAULT)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".formato_fault";
			}else{
				return "formato_fault";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_PRIMO_TENTATIVO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_primo_tentativo";
			}else{
				return "data_primo_tentativo";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().NUMERO_TENTATIVI)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".numero_tentativi";
			}else{
				return "numero_tentativi";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().CLUSTER_ID_PRESA_IN_CARICO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".cluster_id_in_coda";
			}else{
				return "cluster_id_in_coda";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().CLUSTER_ID_CONSEGNA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".cluster_id_consegna";
			}else{
				return "cluster_id_consegna";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_ULTIMO_ERRORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_ultimo_errore";
			}else{
				return "data_ultimo_errore";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().DETTAGLIO_ESITO_ULTIMO_ERRORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".dettaglio_esito_ultimo_errore";
			}else{
				return "dettaglio_esito_ultimo_errore";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().CODICE_RISPOSTA_ULTIMO_ERRORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".codice_risposta_ultimo_errore";
			}else{
				return "codice_risposta_ultimo_errore";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().ULTIMO_ERRORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".ultimo_errore";
			}else{
				return "ultimo_errore";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().LOCATION_ULTIMO_ERRORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".location_ultimo_errore";
			}else{
				return "location_ultimo_errore";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().CLUSTER_ID_ULTIMO_ERRORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".cluster_id_ultimo_errore";
			}else{
				return "cluster_id_ultimo_errore";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().FAULT_ULTIMO_ERRORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".fault_ultimo_errore";
			}else{
				return "fault_ultimo_errore";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().FORMATO_FAULT_ULTIMO_ERRORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".formato_fault_ultimo_errore";
			}else{
				return "formato_fault_ultimo_errore";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_PRIMO_PRELIEVO_IM)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_primo_prelievo_im";
			}else{
				return "data_primo_prelievo_im";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_PRELIEVO_IM)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_prelievo_im";
			}else{
				return "data_prelievo_im";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().NUMERO_PRELIEVI_IM)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".numero_prelievi_im";
			}else{
				return "numero_prelievi_im";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_ELIMINAZIONE_IM)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_eliminazione_im";
			}else{
				return "data_eliminazione_im";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().CLUSTER_ID_PRELIEVO_IM)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".cluster_id_prelievo_im";
			}else{
				return "cluster_id_prelievo_im";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().CLUSTER_ID_ELIMINAZIONE_IM)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".cluster_id_eliminazione_im";
			}else{
				return "cluster_id_eliminazione_im";
			}
		}


		return super.toColumn(field,returnAlias,appendTablePrefix);
		
	}
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(field.equals(TransazioneApplicativoServer.model().ID_TRANSAZIONE)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().SERVIZIO_APPLICATIVO_EROGATORE)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().CONNETTORE_NOME)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_REGISTRAZIONE)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().CONSEGNA_TERMINATA)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_MESSAGGIO_SCADUTO)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().DETTAGLIO_ESITO)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().CONSEGNA_TRASPARENTE)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().CONSEGNA_INTEGRATION_MANAGER)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().IDENTIFICATIVO_MESSAGGIO)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RICHIESTA)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_USCITA_RICHIESTA)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_ACCETTAZIONE_RISPOSTA)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_INGRESSO_RISPOSTA)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().RICHIESTA_USCITA_BYTES)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().RISPOSTA_INGRESSO_BYTES)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().LOCATION_CONNETTORE)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().CODICE_RISPOSTA)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().FAULT)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().FORMATO_FAULT)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_PRIMO_TENTATIVO)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().NUMERO_TENTATIVI)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().CLUSTER_ID_PRESA_IN_CARICO)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().CLUSTER_ID_CONSEGNA)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_ULTIMO_ERRORE)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().DETTAGLIO_ESITO_ULTIMO_ERRORE)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().CODICE_RISPOSTA_ULTIMO_ERRORE)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().ULTIMO_ERRORE)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().LOCATION_ULTIMO_ERRORE)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().CLUSTER_ID_ULTIMO_ERRORE)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().FAULT_ULTIMO_ERRORE)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().FORMATO_FAULT_ULTIMO_ERRORE)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_PRIMO_PRELIEVO_IM)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_PRELIEVO_IM)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().NUMERO_PRELIEVI_IM)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_ELIMINAZIONE_IM)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().CLUSTER_ID_PRELIEVO_IM)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().CLUSTER_ID_ELIMINAZIONE_IM)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}


		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(TransazioneApplicativoServer.model())){
			return CostantiDB.TRANSAZIONI_APPLICATIVI_SERVER;
		}


		return super.toTable(model,returnAlias);
		
	}

}
