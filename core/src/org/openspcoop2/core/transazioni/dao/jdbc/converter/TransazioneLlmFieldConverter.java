package org.openspcoop2.core.transazioni.dao.jdbc.converter;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.transazioni.TransazioneLlm;


/**     
 * TransazioneLlmFieldConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransazioneLlmFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public TransazioneLlmFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public TransazioneLlmFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return TransazioneLlm.model();
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
		
		if(field.equals(TransazioneLlm.model().ID_TRANSAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".id_transazione";
			}else{
				return "id_transazione";
			}
		}
		if(field.equals(TransazioneLlm.model().DATA_INGRESSO_RICHIESTA)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".data_ingresso_richiesta";
			}else{
				return "data_ingresso_richiesta";
			}
		}
		if(field.equals(TransazioneLlm.model().LLM_PROVIDER)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".llm_provider";
			}else{
				return "llm_provider";
			}
		}
		if(field.equals(TransazioneLlm.model().LLM_MODEL)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".llm_model";
			}else{
				return "llm_model";
			}
		}
		if(field.equals(TransazioneLlm.model().LLM_PROVIDER_BINDING)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".llm_provider_binding";
			}else{
				return "llm_provider_binding";
			}
		}
		if(field.equals(TransazioneLlm.model().TOKEN_INPUT)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".token_input";
			}else{
				return "token_input";
			}
		}
		if(field.equals(TransazioneLlm.model().TOKEN_OUTPUT)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".token_output";
			}else{
				return "token_output";
			}
		}
		if(field.equals(TransazioneLlm.model().COST_ESTIMATED)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".cost_estimated";
			}else{
				return "cost_estimated";
			}
		}


		return super.toColumn(field,returnAlias,appendTablePrefix);
		
	}
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(field.equals(TransazioneLlm.model().ID_TRANSAZIONE)){
			return this.toTable(TransazioneLlm.model(), returnAlias);
		}
		if(field.equals(TransazioneLlm.model().DATA_INGRESSO_RICHIESTA)){
			return this.toTable(TransazioneLlm.model(), returnAlias);
		}
		if(field.equals(TransazioneLlm.model().LLM_PROVIDER)){
			return this.toTable(TransazioneLlm.model(), returnAlias);
		}
		if(field.equals(TransazioneLlm.model().LLM_MODEL)){
			return this.toTable(TransazioneLlm.model(), returnAlias);
		}
		if(field.equals(TransazioneLlm.model().LLM_PROVIDER_BINDING)){
			return this.toTable(TransazioneLlm.model(), returnAlias);
		}
		if(field.equals(TransazioneLlm.model().TOKEN_INPUT)){
			return this.toTable(TransazioneLlm.model(), returnAlias);
		}
		if(field.equals(TransazioneLlm.model().TOKEN_OUTPUT)){
			return this.toTable(TransazioneLlm.model(), returnAlias);
		}
		if(field.equals(TransazioneLlm.model().COST_ESTIMATED)){
			return this.toTable(TransazioneLlm.model(), returnAlias);
		}


		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(TransazioneLlm.model())){
			return CostantiDB.TRANSAZIONI_LLM;
		}


		return super.toTable(model,returnAlias);
		
	}

}
