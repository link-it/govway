package org.openspcoop2.core.transazioni.dao.jdbc.converter;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.utils.TipiDatabase;

import org.openspcoop2.core.transazioni.TransazioneExport;


/**     
 * TransazioneExportFieldConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransazioneExportFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public TransazioneExportFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public TransazioneExportFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return TransazioneExport.model();
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
		
		if(field.equals(TransazioneExport.model().INTERVALLO_INIZIO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".intervallo_inizio";
			}else{
				return "intervallo_inizio";
			}
		}
		if(field.equals(TransazioneExport.model().INTERVALLO_FINE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".intervallo_fine";
			}else{
				return "intervallo_fine";
			}
		}
		if(field.equals(TransazioneExport.model().NOME)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".nome";
			}else{
				return "nome";
			}
		}
		if(field.equals(TransazioneExport.model().EXPORT_STATE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".export_state";
			}else{
				return "export_state";
			}
		}
		if(field.equals(TransazioneExport.model().EXPORT_ERROR)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".export_error";
			}else{
				return "export_error";
			}
		}
		if(field.equals(TransazioneExport.model().EXPORT_TIME_START)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".export_time_start";
			}else{
				return "export_time_start";
			}
		}
		if(field.equals(TransazioneExport.model().EXPORT_TIME_END)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".export_time_end";
			}else{
				return "export_time_end";
			}
		}
		if(field.equals(TransazioneExport.model().DELETE_STATE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".delete_state";
			}else{
				return "delete_state";
			}
		}
		if(field.equals(TransazioneExport.model().DELETE_ERROR)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".delete_error";
			}else{
				return "delete_error";
			}
		}
		if(field.equals(TransazioneExport.model().DELETE_TIME_START)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".delete_time_start";
			}else{
				return "delete_time_start";
			}
		}
		if(field.equals(TransazioneExport.model().DELETE_TIME_END)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".delete_time_end";
			}else{
				return "delete_time_end";
			}
		}


		return super.toColumn(field,returnAlias,appendTablePrefix);
		
	}
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(field.equals(TransazioneExport.model().INTERVALLO_INIZIO)){
			return this.toTable(TransazioneExport.model(), returnAlias);
		}
		if(field.equals(TransazioneExport.model().INTERVALLO_FINE)){
			return this.toTable(TransazioneExport.model(), returnAlias);
		}
		if(field.equals(TransazioneExport.model().NOME)){
			return this.toTable(TransazioneExport.model(), returnAlias);
		}
		if(field.equals(TransazioneExport.model().EXPORT_STATE)){
			return this.toTable(TransazioneExport.model(), returnAlias);
		}
		if(field.equals(TransazioneExport.model().EXPORT_ERROR)){
			return this.toTable(TransazioneExport.model(), returnAlias);
		}
		if(field.equals(TransazioneExport.model().EXPORT_TIME_START)){
			return this.toTable(TransazioneExport.model(), returnAlias);
		}
		if(field.equals(TransazioneExport.model().EXPORT_TIME_END)){
			return this.toTable(TransazioneExport.model(), returnAlias);
		}
		if(field.equals(TransazioneExport.model().DELETE_STATE)){
			return this.toTable(TransazioneExport.model(), returnAlias);
		}
		if(field.equals(TransazioneExport.model().DELETE_ERROR)){
			return this.toTable(TransazioneExport.model(), returnAlias);
		}
		if(field.equals(TransazioneExport.model().DELETE_TIME_START)){
			return this.toTable(TransazioneExport.model(), returnAlias);
		}
		if(field.equals(TransazioneExport.model().DELETE_TIME_END)){
			return this.toTable(TransazioneExport.model(), returnAlias);
		}


		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(TransazioneExport.model())){
			return "transazioni_export";
		}


		return super.toTable(model,returnAlias);
		
	}

}
