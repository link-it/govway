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
		if(field.equals(TransazioneApplicativoServer.model().CODICE_RISPOSTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".codice_risposta";
			}else{
				return "codice_risposta";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_PRIMO_TENTATIVO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_primo_tentativo";
			}else{
				return "data_primo_tentativo";
			}
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_ULTIMO_ERRORE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_ultimo_errore";
			}else{
				return "data_ultimo_errore";
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
		if(field.equals(TransazioneApplicativoServer.model().NUMERO_TENTATIVI)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".numero_tentativi";
			}else{
				return "numero_tentativi";
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
		if(field.equals(TransazioneApplicativoServer.model().CODICE_RISPOSTA)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_PRIMO_TENTATIVO)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().DATA_ULTIMO_ERRORE)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().CODICE_RISPOSTA_ULTIMO_ERRORE)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().ULTIMO_ERRORE)){
			return this.toTable(TransazioneApplicativoServer.model(), returnAlias);
		}
		if(field.equals(TransazioneApplicativoServer.model().NUMERO_TENTATIVI)){
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
