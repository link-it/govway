/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.sql;

import java.util.Iterator;

import org.openspcoop2.utils.TipiDatabase;

/**
 * Implementazione per HyperSQL
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DerbyQueryObject extends SQLQueryObjectCore {

	public DerbyQueryObject(TipiDatabase tipoDatabase) {
		super(tipoDatabase);
	}

	
	
	@Override
	public String getUnixTimestampConversion(String column){
		//return "((datediff('ss',CAST('1970-01-01 00:00:00' AS TIMESTAMP),"+column+")-3600)*1000)";
		return "( "+ 
				"("+
				"(UNIX_TIMESTAMP("+column+")*1000) - "+
				"((EXTRACT(HOUR FROM TIMEZONE()))*60*60*1000)" +
				") + "+
				"(CAST(CHAR ("+column+") AS INTEGER))" +
				")"
				;
	}
	
	@Override
	public String getDiffUnixTimestamp(String columnMax,String columnMin){
		return "( "+getUnixTimestampConversion(columnMax)+" - "+getUnixTimestampConversion(columnMin)+" )";
	}
	
	
	
	@Override
	public ISQLQueryObject addSelectAvgTimestampField(String field, String alias)
			throws SQLQueryObjectException {
		if(field==null)
			throw new SQLQueryObjectException("field avg non puo' essere null");
		// Trasformo in UNIX_TIMESTAMP
		String fieldSQL = "avg("+this.getUnixTimestampConversion(field)+")";
		if(alias != null){
			// fieldSQL = fieldSQL + " as "+alias;
			fieldSQL = fieldSQL +this.getDefaultAliasFieldKeyword()+alias;
		}
		this._engine_addSelectField(null,fieldSQL,null,false,true);
		this.fieldNames.add(alias);
		return this;
	}

	@Override
	public ISQLQueryObject addSelectMaxTimestampField(String field, String alias)
			throws SQLQueryObjectException {
		if(field==null)
			throw new SQLQueryObjectException("field avg non puo' essere null");
		// Trasformo in UNIX_TIMESTAMP
		String fieldSQL = "max("+this.getUnixTimestampConversion(field)+")";
		if(alias != null){
			//fieldSQL = fieldSQL + " as "+alias;
			fieldSQL = fieldSQL +this.getDefaultAliasFieldKeyword()+alias;
		}
		this._engine_addSelectField(null,fieldSQL,null,false,true);
		this.fieldNames.add(alias);
		return this;
	}

	@Override
	public ISQLQueryObject addSelectMinTimestampField(String field, String alias)
			throws SQLQueryObjectException {
		if(field==null)
			throw new SQLQueryObjectException("field avg non puo' essere null");
		// Trasformo in UNIX_TIMESTAMP
		String fieldSQL = "min("+this.getUnixTimestampConversion(field)+")";
		if(alias != null){
			//fieldSQL = fieldSQL + " as "+alias;
			fieldSQL = fieldSQL +this.getDefaultAliasFieldKeyword()+alias;
		}
		this._engine_addSelectField(null,fieldSQL,null,false,true);
		this.fieldNames.add(alias);
		return this;
	}

	@Override
	public ISQLQueryObject addSelectSumTimestampField(String field, String alias)
			throws SQLQueryObjectException {
		if(field==null)
			throw new SQLQueryObjectException("field avg non puo' essere null");
		// Trasformo in UNIX_TIMESTAMP
		String fieldSQL = "sum("+this.getUnixTimestampConversion(field)+")";
		if(alias != null){
			//fieldSQL = fieldSQL + " as "+alias;
			fieldSQL = fieldSQL +this.getDefaultAliasFieldKeyword()+alias;
		}
		this._engine_addSelectField(null,fieldSQL,null,false,true);
		this.fieldNames.add(alias);
		return this;
	}
	
	
	
	
	
	
	@Override
	public ISQLQueryObject addFromTable(ISQLQueryObject subSelect)
			throws SQLQueryObjectException {
		StringBuilder bf = new StringBuilder();
		bf.append(" ( ");
		bf.append(subSelect.createSQLQuery());
		bf.append(" ) ");
		this.addFromTable(bf.toString());
		return this;
	}
	
	
	
	
	@Override
	protected EscapeSQLConfiguration getEscapeSQLConfiguration(){
		
		EscapeSQLConfiguration config = new EscapeSQLConfiguration();
		config.addCharacter('_');
		config.addCharacter('%');
		config.addCharacter('\\');
		config.setUseEscapeClausole(true);
		config.setEscape('\\');
		
		// special
		config.addCharacterWithOtherEscapeChar('\'','\'');
		
		return config;
	}
	
	
	
	
	
	
	@Override
	public String createSQLQuery() throws SQLQueryObjectException{
		return this.createSQLQuery(false);
	}
	private String createSQLQuery(boolean union) throws SQLQueryObjectException{
		
		this.precheckBuildQuery();
		
		StringBuilder bf = new StringBuilder();
		
		bf.append("SELECT ");
		
		// forzatura di indici
		Iterator<String> itForceIndex = this.forceIndexTableNames.iterator();
		while(itForceIndex.hasNext()){
			bf.append(" "+itForceIndex.next()+" ");
		}
		
		if(this.isSelectDistinct())
			bf.append(" DISTINCT ");
		
		// select field
		if(this.fields.size()==0){
			bf.append("*");
		}else{
			Iterator<String> it = this.fields.iterator();
			boolean first = true;
			while(it.hasNext()){
				if(!first)
					bf.append(",");
				else
					first = false;
				bf.append(it.next());
			}
		}
		
		bf.append(getSQL(false,false,false,union));
		
		//if( this.offset>=0 || this.limit>=0)
		//	System.out.println("SQL ["+bf.toString()+"]");
		
		return bf.toString();
	}

	
	
	
	@Override
	public String _createSQLDelete() throws SQLQueryObjectException {
		StringBuilder bf = new StringBuilder();
				
		bf.append("DELETE ");
		
		bf.append(getSQL(true,false,false,false));
		return bf.toString();
	}
	
	
	
	

	
	/**
	 * @return SQL
	 * @throws SQLQueryObjectException
	 */
	private String getSQL(boolean delete,boolean update,boolean conditions,boolean union) throws SQLQueryObjectException {
		StringBuilder bf = new StringBuilder();

		if(this.selectForUpdate){
			this.checkSelectForUpdate(update, delete, union);
		}
		
		if(update==false && conditions==false){
			// From
			bf.append(" FROM ");
			
			// Table dove effettuare la ricerca 'FromTable'
			if(this.tables.size()==0){
				throw new SQLQueryObjectException("Tabella di ricerca (... FROM Table ...) non definita");
			}else{
				if(delete && this.tables.size()>2)
					throw new SQLQueryObjectException("Non e' possibile effettuare una delete con piu' di una tabella alla volta");
				Iterator<String> it = this.tables.iterator();
				boolean first = true;
				while(it.hasNext()){
					if(!first)
						bf.append(",");
					else
						first = false;
					bf.append(it.next());
				}
			}
		}
		
		// Condizioni di Where
		if(this.conditions.size()>0){
			
			if(conditions==false)
				bf.append(" WHERE ");
			
			if(this.notBeforeConditions){
				bf.append("NOT (");
			}
			
			for(int i=0; i<this.conditions.size(); i++){
				if(i>0){
					if(this.andLogicOperator){
						bf.append(" AND ");
					}else{
						bf.append(" OR ");
					}
				}
				bf.append(this.conditions.get(i));
			}
			
			if(this.notBeforeConditions){
				bf.append(")");
			}
		}
		
		// Condizione GroupBy
		if((this.getGroupByConditions().size()>0) && (delete==false) && (update==false) && (conditions==false)){
			bf.append(" GROUP BY ");
			Iterator<String> it = this.getGroupByConditions().iterator();
			boolean first = true;
			while(it.hasNext()){
				if(!first)
					bf.append(",");
				else
					first = false;
				bf.append(it.next());
			}
		}
		
		// Condizione OrderBy
//		if(union==false){ La condizione di OrderBy DEVE essere generata. In SQLServer e MySQL viene generata durante la condizione di LIMIT/OFFSET
		// NOTA: L'order by insieme al LIMIT e OFFSET, all'interno di sotto-select come in questo caso della union, funziona solo con HSQL 2.x
			if((this.orderBy.size()>0) && (delete==false) && (update==false) && (conditions==false) ){
				bf.append(" ORDER BY ");
				Iterator<String> it = this.orderBy.iterator();
				boolean first = true;
				while(it.hasNext()){
					String column = it.next();
					if(!first)
						bf.append(",");
					else
						first = false;
					bf.append(column);
					boolean sortTypeAsc = this.sortTypeAsc;
					if(this.orderBySortType.containsKey(column)){
						sortTypeAsc = this.orderBySortType.get(column);
					}
					if(sortTypeAsc){
						bf.append(" ASC ");
					}else{
						bf.append(" DESC ");
					}
				}
			}
//		}

			
		// Limit e Offset
		//if(this.limit>0 || this.offset>0){
		// Rilascio vincolo di order by in caso di limit impostato.
		// Il vincolo rimane per l'offset, per gestire le select annidate di qualche implementazioni come Oracle,SQLServer ...
		if(this.offset>=0){
			if(this.orderBy.size()==0){
				throw new SQLQueryObjectException("Condizioni di OrderBy richieste");
			}
		}
		
		
		// Offset
		if((this.offset>=0) && (delete==false) && (update==false) && (conditions==false)){
			bf.append(" OFFSET ");
			bf.append(this.offset);
			bf.append(" ROWS ");
		}
		
		// Limit (con offset)
		if((this.limit>0) && (delete==false) && (update==false) && (conditions==false)){
			bf.append(" FETCH NEXT ");
			bf.append(this.limit);
			bf.append(" ROWS ONLY ");
		}
		
		// ForUpdate
		if(conditions==false){
			if(this.selectForUpdate){
				bf.append(" FOR UPDATE ");
			}
		}
		
		return bf.toString();
	}
	
	
	
	@Override
	public String createSQLUnion(boolean unionAll,
			ISQLQueryObject... sqlQueryObject) throws SQLQueryObjectException {
		
		// Controllo parametro su cui effettuare la UNION
		this.checkUnionField(false,sqlQueryObject);
		
		if(this.selectForUpdate){
			this.checkSelectForUpdate(false, false, true);
		}
		
		StringBuilder bf = new StringBuilder();
		
		bf.append("SELECT ");
		
		// Non ha senso, la union fa gia la distinct, a meno di usare la unionAll ma in quel caso non si vuole la distinct
		// if(this.isSelectDistinct())
		//	bf.append(" DISTINCT ");
		
		// forzatura di indici
		Iterator<String> itForceIndex = this.forceIndexTableNames.iterator();
		while(itForceIndex.hasNext()){
			bf.append(" "+itForceIndex.next()+" ");
		}
				
		// select field
		if(this.fields.size()==0){
			bf.append("*");
		}else{
			Iterator<String> it = this.fields.iterator();
			boolean first = true;
			while(it.hasNext()){
				if(!first)
					bf.append(",");
				else
					first = false;
				bf.append(it.next());
			}
		}
		
		bf.append(" FROM ( ");
		
		for(int i=0; i<sqlQueryObject.length; i++){
			
			if(((DerbyQueryObject)sqlQueryObject[i]).selectForUpdate){
				try{
					((DerbyQueryObject)sqlQueryObject[i]).checkSelectForUpdate(false, false, true);
				}catch(Exception e){
					throw new SQLQueryObjectException("Parametro SqlQueryObject["+i+"] non valido: "+e.getMessage());
				}
			}
			
			if(i>0){
				bf.append(" UNION ");
				if(unionAll){
					bf.append(" ALL ");
				}
			}
			
			bf.append("( ");
			
			bf.append(((DerbyQueryObject)sqlQueryObject[i]).createSQLQuery(true));
			
			bf.append(") ");
		}
		
		bf.append(" ) as subquery"+getSerial()+" ");
		
		// Condizione GroupBy
		if((this.getGroupByConditions().size()>0) ){
			bf.append(" GROUP BY ");
			Iterator<String> it = this.getGroupByConditions().iterator();
			boolean first = true;
			while(it.hasNext()){
				if(!first)
					bf.append(",");
				else
					first = false;
				bf.append(it.next());
			}
		}
		
		// Condizione OrderBy
		if(this.orderBy.size()>0){
			bf.append(" ORDER BY ");
			Iterator<String> it = this.orderBy.iterator();
			boolean first = true;
			while(it.hasNext()){
				String column = it.next();
				if(!first)
					bf.append(",");
				else
					first = false;
				bf.append(column);
				boolean sortTypeAsc = this.sortTypeAsc;
				if(this.orderBySortType.containsKey(column)){
					sortTypeAsc = this.orderBySortType.get(column);
				}
				if(sortTypeAsc){
					bf.append(" ASC ");
				}else{
					bf.append(" DESC ");
				}
			}
		}
		
		// Limit e Offset
		//if(this.limit>0 || this.offset>0){
		// Rilascio vincolo di order by in caso di limit impostato.
		// Il vincolo rimane per l'offset, per gestire le select annidate di qualche implementazioni come Oracle,SQLServer ...
		if(this.offset>=0){
			if(this.orderBy.size()==0){
				throw new SQLQueryObjectException("Condizioni di OrderBy richieste");
			}
		}
		
		// Offset
		if(this.offset>=0){
			bf.append(" OFFSET ");
			bf.append(this.offset);
			bf.append(" ROWS ");
		}
		
		// Limit 
		if(this.limit>0){
			bf.append(" FETCH NEXT ");
			bf.append(this.limit);
			bf.append(" ROWS ONLY ");
		}
		
		return bf.toString();
		
	}

	@Override
	public String createSQLUnionCount(boolean unionAll, String aliasCount,
			ISQLQueryObject... sqlQueryObject) throws SQLQueryObjectException {
		// Controllo parametro su cui effettuare la UNION
		this.checkUnionField(true,sqlQueryObject);
		
		if(aliasCount==null){
			throw new SQLQueryObjectException("Alias per il count non definito");
		}
		
		StringBuilder bf = new StringBuilder();
		
		bf.append("SELECT count(*) "+this.getDefaultAliasFieldKeyword()+" ");
		bf.append(aliasCount);
		bf.append(" FROM ( ");
		
		bf.append( this.createSQLUnion(unionAll, sqlQueryObject) );
		
		bf.append(" ) as subquery"+getSerial()+" ");
			
		return bf.toString();
	}
	
	
	

	@Override
	public String _createSQLUpdate() throws SQLQueryObjectException {

		StringBuilder bf = new StringBuilder();
		bf.append("UPDATE ");
		bf.append(this.updateTable);
		bf.append(" SET ");
		for(int i=0; i<this.updateFieldsName.size(); i++){
			if(i>0)
				bf.append(" , ");
			bf.append(this.updateFieldsName.get(i));
			bf.append(" = ");
			bf.append(this.updateFieldsValue.get(i));
		}
		bf.append(getSQL(false,true,false,false));
		return bf.toString();
	}

	

	
	
	/* ---------------- WHERE CONDITIONS ------------------ */
	
	@Override
	public String _createSQLConditions() throws SQLQueryObjectException {
		
		StringBuilder bf = new StringBuilder();
		bf.append(getSQL(false,false,true,false));
		return bf.toString();
	}

	

	
	
	
	
	

}
