package org.openspcoop2.core.transazioni.utils;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.generic_project.beans.IAliasTableField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

public class AliasTableRicerchePersonalizzate {

	public static final String ALIAS_PREFIX = "op2T";
	
	public static List<String> addFromTable(IExpression expression, ISQLQueryObject sqlQueryObject,ISQLFieldConverter fieldConverter) throws ExpressionNotImplementedException, ExpressionException, SQLQueryObjectException{
		List<IField> iFields = expression.getFields(true);
		List<String> tabelleAggiunteAlias = new ArrayList<String>();
		if(iFields!=null && iFields.size()>0){
			for (IField iField : iFields) {
				if(iField instanceof IAliasTableField){
					IAliasTableField af = (IAliasTableField) iField;
					//System.out.println("CHECK ALIAS ["+af.getAliasTable()+"] ["+af.getFieldName()+"] ...");
					if(org.openspcoop2.core.transazioni.Transazione.model().DUMP_MESSAGGIO.CONTENUTO.NOME.equals(af.getField())){
						String aliasTabella = af.getAliasTable();
						//System.out.println("CHECK ALIAS DENTRO ["+af.getAliasTable()+"] ["+af.getFieldName()+"] ...");
						if(tabelleAggiunteAlias.contains(aliasTabella)==false){
							//Lo fa gia in automatico la gestione dell'Expression
							//sqlQueryObject.addFromTable(fieldConverter.toTable(Transazione.model().DUMP_MESSAGGIO.CONTENUTO), aliasTabella);
							//System.out.println("CHECK ALIAS ADD ["+af.getAliasTable()+"] ["+af.getFieldName()+"]");
							tabelleAggiunteAlias.add(aliasTabella);
						}
					}
				}
			}
		}
		return tabelleAggiunteAlias;
	}
	
}
