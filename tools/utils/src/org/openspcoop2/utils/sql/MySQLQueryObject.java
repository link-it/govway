/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
 * Classe dove vengono forniti utility per la conversione di comandi SQL per il database mysql 
 * 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MySQLQueryObject extends SQLQueryObjectCore{
		
	
	
	public MySQLQueryObject(TipiDatabase tipoDatabase) {
		super(tipoDatabase);
	}


	
	@Override
	public String getUnixTimestampConversion(String column){
		return "(UNIX_TIMESTAMP("+column+")*1000)";
	}
	
	@Override
	public String getDiffUnixTimestamp(String columnMax,String columnMin){
		return "( "+getUnixTimestampConversion(columnMax)+" - "+getUnixTimestampConversion(columnMin)+" )";
	}
	
	
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>fieldAvg</var> (di tipo Timestamp)
	 * es: SELECT avg(fieldAvg) as alias FROM ....
	 * 
	  * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectAvgTimestampField(String field,String alias) throws SQLQueryObjectException{
		if(field==null)
			throw new SQLQueryObjectException("field avg non puo' essere null");
		// Trasformo in UNIX_TIMESTAMP
		String fieldSQL = "avg("+this.getUnixTimestampConversion(field)+")";
		if(alias != null){
			//fieldSQL = fieldSQL + " as "+alias;
			fieldSQL = fieldSQL + this.getDefaultAliasFieldKeyword()+alias;
		}
		this._engine_addSelectField(null,fieldSQL,null,false,true);
		this.fieldNames.add(alias);
		return this;
	}
	
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT max(field) as alias FROM ....
	 * 
	  * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectMaxTimestampField(String field,String alias) throws SQLQueryObjectException{
		if(field==null)
			throw new SQLQueryObjectException("field non puo' essere null");
		// Trasformo in UNIX_TIMESTAMP
		String fieldSQL = "max("+this.getUnixTimestampConversion(field)+")";
		if(alias != null){
			//fieldSQL = fieldSQL + " as "+alias;
			fieldSQL = fieldSQL + this.getDefaultAliasFieldKeyword()+alias;
		}
		this._engine_addSelectField(null,fieldSQL,null,false,true);
		this.fieldNames.add(alias);
		return this;
	}
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT min(field) as alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias
	 */
	@Override
	public ISQLQueryObject addSelectMinTimestampField(String field,String alias) throws SQLQueryObjectException{
		if(field==null)
			throw new SQLQueryObjectException("field non puo' essere null");
		// Trasformo in UNIX_TIMESTAMP
		String fieldSQL = "min("+this.getUnixTimestampConversion(field)+")";
		if(alias != null){
			//fieldSQL = fieldSQL + " as "+alias;
			fieldSQL = fieldSQL + this.getDefaultAliasFieldKeyword()+alias;
		}
		this._engine_addSelectField(null,fieldSQL,null,false,true);
		this.fieldNames.add(alias);
		return this;
	}
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT sum(field) as alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias
	 */
	@Override
	public ISQLQueryObject addSelectSumTimestampField(String field,String alias) throws SQLQueryObjectException{
		if(field==null)
			throw new SQLQueryObjectException("field non puo' essere null");
		// Trasformo in UNIX_TIMESTAMP
		String fieldSQL = "sum("+this.getUnixTimestampConversion(field)+")";
		if(alias != null){
			//fieldSQL = fieldSQL + " as "+alias;
			fieldSQL = fieldSQL + this.getDefaultAliasFieldKeyword()+alias;
		}
		this._engine_addSelectField(null,fieldSQL,null,false,true);
		this.fieldNames.add(alias);
		return this;
	}
	
	
	/**
	 * Aggiunge una tabella di ricerca del from
	 * es: SELECT * from ( subSelect )
	 * 
	 * @param subSelect subSelect
	 */
	@Override
	public ISQLQueryObject addFromTable(ISQLQueryObject subSelect) throws SQLQueryObjectException{
		StringBuffer bf = new StringBuffer();
		bf.append(" ( ");
		bf.append(subSelect.createSQLQuery());
		bf.append(" ) as subquery"+getSerial()+" ");
		this.addFromTable(bf.toString());
		return this;
	}
	
	
	
	@Override
	protected EscapeSQLConfiguration getEscapeSQLConfiguration(){
		EscapeSQLConfiguration config = new EscapeSQLConfiguration();
		config.setSpecial_char(new char[]  {'_','%'});
		config.setUseEscapeClausole(false);
		config.setEscapeClausole('\\');
		return config;
	}

	
	
	/**
	 * Crea una SQL Query con i dati dell'oggetto
	 * 
	 * @return SQL Query
	 */
	@Override
	public String createSQLQuery() throws SQLQueryObjectException{
		return this.createSQLQuery(false);
	}
	private String createSQLQuery(boolean union) throws SQLQueryObjectException{
		
		this.precheckBuildQuery();
		
		StringBuffer bf = new StringBuffer();
		
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

	
	/**
	 * Crea una SQL per una operazione di Delete con i dati dell'oggetto
	 * 
	 * @return SQL per una operazione di Delete
	 */
	@Override
	public String _createSQLDelete() throws SQLQueryObjectException{
		StringBuffer bf = new StringBuffer();
				
		bf.append("DELETE ");
		
		bf.append(getSQL(true,false,false,false));
		return bf.toString();
	}
	
	
	
	/**
	 * @return SQL
	 * @throws SQLQueryObjectException
	 */
	private String getSQL(boolean delete,boolean update,boolean conditions,boolean union) throws SQLQueryObjectException {
		StringBuffer bf = new StringBuffer();
		
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
			if((this.orderBy.size()>0) && (delete==false) && (update==false) && (conditions==false)){
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
		
		// Limit
		if((this.limit>0) && (delete==false) && (update==false) && (conditions==false)){
			bf.append(" LIMIT ");
			bf.append(this.limit);
		}
		
		// Offset
		if((this.offset>=0) && (delete==false) && (update==false) && (conditions==false)){
			if(this.limit<=0){
				bf.append(" LIMIT ");
				//bf.append(ISQLQueryObject.LIMIT_DEFAULT_VALUE);
				// Ci metto un valore MOLTO ALTO in modo da essere implementato come per gli altri database quasi come se non ci fosse il limit
				bf.append(Integer.MAX_VALUE);
			}
			bf.append(" OFFSET ");
			bf.append(this.offset);
		}
			
		// ForUpdate
		if(conditions==false){
			if(this.selectForUpdate){
				bf.append(" FOR UPDATE ");
			}
		}
		
		return bf.toString();
	}
	
	
	
	/**
	 * Crea la stringa SQL per l'unione dei parametri presi in sqlQueryObject 
	 * Il booleano 'unionAll' indica se i vari parametri devono essere uniti con 'UNION'  o con 'UNION ALL'
	 * 
	 * In caso nell'oggetto viene impostato il LIMIT o OFFSET, questo viene utilizzato per effettuare LIMIT e/o OFFSET dell'unione
	 * 
	 * In caso l'ORDER by viene impostato nell'oggetto, questo viene utilizzato per effettuare ORDER BY dell'unione
	 * 
	 * 
	 * Sintassi MySQL:
	 * 
	 * select * FROM (
	 * (  SELECT fields... FROM tabella WHERE conditions  )
	 * UNION [ALL]
	 * (  SELECT fields... FROM tabella WHERE conditions  )
	 * ) as subquery
	 * 
	 * ORDER BY gdo,tipo_fruitore,fruitore,tipo_erogatore,erogatore,tipo_servizio,servizio DESC  
	 * LIMIT 1000 
	 * OFFSET 0;
	 * 
	 * 
	 * @param unionAll
	 * @param sqlQueryObject
	 * @return stringa SQL per l'unione dei parametri presi in sqlQueryObject
	 * @throws SQLQueryObjectException
	 */
	@Override
	public String createSQLUnion(boolean unionAll,ISQLQueryObject... sqlQueryObject) throws SQLQueryObjectException{
		
		// Controllo parametro su cui effettuare la UNION
		this.checkUnionField(false,sqlQueryObject);
		
		if(this.selectForUpdate){
			this.checkSelectForUpdate(false, false, true);
		}
		
		StringBuffer bf = new StringBuffer();
		
		bf.append("SELECT ");
		
		// forzatura di indici
		Iterator<String> itForceIndex = this.forceIndexTableNames.iterator();
		while(itForceIndex.hasNext()){
			bf.append(" "+itForceIndex.next()+" ");
		}
		
		// Non ha senso, la union fa gia la distinct, a meno di usare la unionAll ma in quel caso non si vuole la distinct
		// if(this.isSelectDistinct())
		//	bf.append(" DISTINCT ");
		
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
			
			if(((MySQLQueryObject)sqlQueryObject[i]).selectForUpdate){
				try{
					((MySQLQueryObject)sqlQueryObject[i]).checkSelectForUpdate(false, false, true);
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
			
			bf.append(((MySQLQueryObject)sqlQueryObject[i]).createSQLQuery(true));
			
			bf.append(") ");
		}
		
		bf.append(" ) as subquery"+getSerial()+" ");
		
		// Condizione GroupBy
		if((this.getGroupByConditions().size()>0)){
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
		
		// Limit
		if(this.limit>0){
			bf.append(" LIMIT ");
			bf.append(this.limit);
		}
		
		// Offset
		if(this.offset>=0){
			if(this.limit<=0){
				bf.append(" LIMIT ");
				//bf.append(ISQLQueryObject.LIMIT_DEFAULT_VALUE);
				// Ci metto un valore MOLTO ALTO in modo da essere implementato come per gli altri database quasi come se non ci fosse il limit
				bf.append(Integer.MAX_VALUE);
			}
			bf.append(" OFFSET ");
			bf.append(this.offset);
		}
		
		return bf.toString();
	}
	
	/**
	 * Crea la stringa SQL per l'unione dei parametri presi in sqlQueryObject (viene effettuato il count)
	 * Il booleano 'unionAll' indica se i vari parametri devono essere uniti con 'UNION'  o con 'UNION ALL'
	 * 
	 * Sintassi MySQL:
	 * 
	 * select count(*) as aliasCount FROM (
	 * 
	 * (  SELECT fields... FROM tabella WHERE conditions  )
	 * UNION [ALL]
	 * (  SELECT fields... FROM tabella WHERE conditions  )
	 * 
	 * ) as subquery
	 * 
	 * @param unionAll
	 * @param sqlQueryObject
	 * @return stringa SQL per l'unione dei parametri presi in sqlQueryObject (viene effettuato il count)
	 * @throws SQLQueryObjectException
	 */
	@Override
	public String createSQLUnionCount(boolean unionAll,String aliasCount,ISQLQueryObject... sqlQueryObject) throws SQLQueryObjectException{
		
		// Controllo parametro su cui effettuare la UNION
		this.checkUnionField(true,sqlQueryObject);
		
		if(aliasCount==null){
			throw new SQLQueryObjectException("Alias per il count non definito");
		}
		
		StringBuffer bf = new StringBuffer();
		
		bf.append("SELECT count(*) "+this.getDefaultAliasFieldKeyword()+" ");
		bf.append(aliasCount);
		bf.append(" FROM ( ");
		
		bf.append( this.createSQLUnion(unionAll, sqlQueryObject) );
				
		bf.append(" ) as subquery"+getSerial()+" ");
		
		return bf.toString();
		
	}
	
	
	
	/**
	 * Crea una SQL per una operazione di Update con i dati dell'oggetto
	 * 
	 * @return SQL per una operazione di Update
	 */
	@Override
	public String _createSQLUpdate() throws SQLQueryObjectException{
		
		StringBuffer bf = new StringBuffer();
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
	
	/**
	 * Crea le condizioni presenti senza anteporre il comando (INSERT,CREATE,UPDATE), le tabelle interessate e il 'WHERE'
	 * 
	 * @return SQL Query
	 */
	@Override
	public String _createSQLConditions() throws SQLQueryObjectException{
		
		StringBuffer bf = new StringBuffer();
		bf.append(getSQL(false,false,true,false));
		return bf.toString();
	}
}
