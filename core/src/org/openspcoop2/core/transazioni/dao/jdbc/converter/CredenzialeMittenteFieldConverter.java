package org.openspcoop2.core.transazioni.dao.jdbc.converter;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.utils.TipiDatabase;

import org.openspcoop2.core.transazioni.CredenzialeMittente;


/**     
 * CredenzialeMittenteFieldConverter
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CredenzialeMittenteFieldConverter extends AbstractSQLFieldConverter {

	private TipiDatabase databaseType;
	
	public CredenzialeMittenteFieldConverter(String databaseType){
		this.databaseType = TipiDatabase.toEnumConstant(databaseType);
	}
	public CredenzialeMittenteFieldConverter(TipiDatabase databaseType){
		this.databaseType = databaseType;
	}


	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return CredenzialeMittente.model();
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
		
		if(field.equals(CredenzialeMittente.model().TIPO)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".tipo";
			}else{
				return "tipo";
			}
		}
		if(field.equals(CredenzialeMittente.model().CREDENZIALE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".credenziale";
			}else{
				return "credenziale";
			}
		}
		if(field.equals(CredenzialeMittente.model().ORA_REGISTRAZIONE)){
			if(appendTablePrefix){
				return this.toAliasTable(field)+".ora_registrazione";
			}else{
				return "ora_registrazione";
			}
		}


		return super.toColumn(field,returnAlias,appendTablePrefix);
		
	}
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(field.equals(CredenzialeMittente.model().TIPO)){
			return this.toTable(CredenzialeMittente.model(), returnAlias);
		}
		if(field.equals(CredenzialeMittente.model().CREDENZIALE)){
			return this.toTable(CredenzialeMittente.model(), returnAlias);
		}
		if(field.equals(CredenzialeMittente.model().ORA_REGISTRAZIONE)){
			return this.toTable(CredenzialeMittente.model(), returnAlias);
		}


		return super.toTable(field,returnAlias);
		
	}

	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		
		// In the case of table with alias, using parameter returnAlias​​, 
		// it is possible to drive the choice whether to return only the alias or 
		// the full definition of the table containing the alias
		
		if(model.equals(CredenzialeMittente.model())){
			return "credenziale_mittente";
		}


		return super.toTable(model,returnAlias);
		
	}

}
