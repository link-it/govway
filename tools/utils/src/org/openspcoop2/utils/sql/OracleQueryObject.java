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



package org.openspcoop2.utils.sql;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openspcoop2.utils.TipiDatabase;



/**
 * Classe dove vengono forniti utility per la conversione di comandi SQL per il database oracle 
 * 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OracleQueryObject extends SQLQueryObjectCore{
	
	public OracleQueryObject(TipiDatabase tipoDatabase) {
		super(tipoDatabase);
	}
	
	
	// GESTIONE LIMIT SENZA OFFSET
	// Se viene impostato OrderBy, e l'offset non e' impostato, deve essere impostato, altrimenti il limit funziona, pero' non ritorna un risultato ordinato.
	@Override
	public ISQLQueryObject addOrderBy(String orderByNomeField) throws SQLQueryObjectException{
		if(this.offset<0 && this.limit>=0){
			super.setOffset(0);
		}
		return super.addOrderBy(orderByNomeField);
	}
	@Override
	public void setLimit(int limit) throws SQLQueryObjectException{
		if(this.offset<0 && !this.orderBy.isEmpty()){
			this.offset = 0;
		}
		super.setLimit(limit);
	}
	
	
	
	// GESTIONE ALIAS 'as'
	// Non tutti i database supportano l'alias 'as', ad esempio Oracle genera un errore se viene usato l'alias con le tabelle.
	@Override
	public String getDefaultAliasTableKeyword(){
		return this.getSupportedAliasesTable().get(0);
	}
	@Override
	public List<String> getSupportedAliasesTable(){
		List<String> lista = new ArrayList<>();
		lista.add(" ");
		return lista;
	}
	
	
	
	
	
	
	@Override
	public String getUnixTimestampConversion(String column){
		String timeZone = null;/**"Europe/Rome";*/
		timeZone = java.util.TimeZone.getDefault().getID(); // fix: uso il timeZone di sistema
		return "("+
				"(((cast("+column+" as date) - to_date('19700101','YYYYMMDD')) * 86400 * 1000) + to_number(to_char("+column+",'FF3'))) - "+				
				"( (EXTRACT (timezone_hour from FROM_TZ ("+column+",(TZ_OFFSET('"+timeZone+"'))))) *60*60*1000 ) "+
				")";
				
	}
	
	@Override
	public String getDiffUnixTimestamp(String columnMax,String columnMin){
		return "("+
				"(extract( day from  ("+columnMax+" - "+columnMin+"))*24*60*60*1000)"+
				"+"+
				"(extract( hour from ("+columnMax+" - "+columnMin+"))*60*60*1000)"+
				"+"+
				"(extract( minute from ("+columnMax+" - "+columnMin+"))*60*1000)"+
				"+"+
				"round(extract( second from ("+columnMax+" - "+columnMin+"))*1000)"+
				")";
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
			throw new SQLQueryObjectException(SQLQueryObjectCore.FIELD_DEVE_ESSERE_DIVERSO_NULL);
		// Trasformo in UNIX_TIMESTAMP
		String fieldSQL = "avg("+this.getUnixTimestampConversion(field)+")";
		if(alias != null){
			/**fieldSQL = fieldSQL + " as "+alias;*/
			fieldSQL = fieldSQL + this.getDefaultAliasFieldKeyword() + alias;
		}
		this.engineAddSelectField(null,fieldSQL,null,false,true);
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
			throw new SQLQueryObjectException(SQLQueryObjectCore.FIELD_DEVE_ESSERE_DIVERSO_NULL);
		// Trasformo in UNIX_TIMESTAMP
		String fieldSQL = "max("+this.getUnixTimestampConversion(field)+")";
		if(alias != null){
			/**fieldSQL = fieldSQL + " as "+alias;*/
			fieldSQL = fieldSQL + this.getDefaultAliasFieldKeyword() + alias;
		}
		this.engineAddSelectField(null,fieldSQL,null,false,true);
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
			throw new SQLQueryObjectException(SQLQueryObjectCore.FIELD_DEVE_ESSERE_DIVERSO_NULL);
		// Trasformo in UNIX_TIMESTAMP
		String fieldSQL = "min("+this.getUnixTimestampConversion(field)+")";
		if(alias != null){
			/**fieldSQL = fieldSQL + " as "+alias;*/
			fieldSQL = fieldSQL + this.getDefaultAliasFieldKeyword() + alias;
		}
		this.engineAddSelectField(null,fieldSQL,null,false,true);
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
			throw new SQLQueryObjectException(SQLQueryObjectCore.FIELD_DEVE_ESSERE_DIVERSO_NULL);
		// Trasformo in UNIX_TIMESTAMP
		String fieldSQL = "sum("+this.getUnixTimestampConversion(field)+")";
		if(alias != null){
			/**fieldSQL = fieldSQL + " as "+alias;*/
			fieldSQL = fieldSQL + this.getDefaultAliasFieldKeyword() + alias;
		}
		this.engineAddSelectField(null,fieldSQL,null,false,true);
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
	public ISQLQueryObject addSelectForceIndex(String nomeTabella,String indexName) throws SQLQueryObjectException{
		if(nomeTabella==null || "".equals(nomeTabella))
			throw new SQLQueryObjectException("Nome tabela is null or empty string");
		if(indexName==null || "".equals(indexName))
			throw new SQLQueryObjectException("Nome indice is null or empty string");
		String forceIndex = "/*+ index("+nomeTabella+" "+indexName+") */";
		if(this.forceIndexTableNames.contains(forceIndex)){
			throw new SQLQueryObjectException("Forzatura all'utilizzo dell'indice ("+forceIndex+") gia inserito tra le forzature");
		}
		this.forceIndexTableNames.add(forceIndex);
		return this;
	}
	

	
	
	
	
	@Override
	public String getExtractDayFormatFromTimestampFieldPrefix(DayFormatEnum dayFormat) throws SQLQueryObjectException {
		if(dayFormat==null) {
			throw new SQLQueryObjectException("dayFormat undefined");
		}
		if(DayFormatEnum.FULL_DAY_NAME.equals(dayFormat)) {
			return "TRIM(BOTH FROM TO_CHAR(";
		}
		else {
			return super.getExtractDayFormatFromTimestampFieldPrefix(dayFormat);
		}
	}
	@Override
	public String getExtractDayFormatFromTimestampFieldSuffix(DayFormatEnum dayFormat) throws SQLQueryObjectException {
		if(DayFormatEnum.FULL_DAY_NAME.equals(dayFormat)) {
			return super.getExtractDayFormatFromTimestampFieldSuffix(dayFormat) + ")"; // chiusura trim
		}
		else {
			return super.getExtractDayFormatFromTimestampFieldSuffix(dayFormat);
		}
	}
	
	
	
	
	
	
	
	
	
	
	@Override
	public ISQLQueryObject addWhereIsEmptyCondition(String field) throws SQLQueryObjectException{
		// In oracle le stringhe vuote equivalgono a null
		return this.addWhereIsNullCondition(field);
	}
	
	@Override
	public ISQLQueryObject addWhereIsNotEmptyCondition(String field) throws SQLQueryObjectException{
		// In oracle le stringhe vuote equivalgono a null
		return this.addWhereIsNotNullCondition(field);
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
		
		StringBuilder bf = new StringBuilder();
		
		bf.append("SELECT ");
		
		// forzatura di indici
		if( !(this.offset>=0 || this.limit>=0) ){
			Iterator<String> itForceIndex = this.forceIndexTableNames.iterator();
			while(itForceIndex.hasNext()){
				bf.append(" "+itForceIndex.next()+" ");
			}
		}
		
		if(this.isSelectDistinct())
			bf.append(" DISTINCT ");
		
		// select field
		addSQLQuerySelectField(bf);
		
		bf.append(getSQL(false,false,false,union));
		
		/**if( this.offset>=0 || this.limit>=0)
		//	System.out.println("SQL ["+bf.toString()+"]");*/
		
		return bf.toString();
	}
	private void addSQLQuerySelectField(StringBuilder bf) {
		if(this.fields.isEmpty()){
			bf.append("*");
		}else{
			Iterator<String> it = this.fields.iterator();
			boolean first = true;
			while(it.hasNext()){
				if(!first)
					bf.append(",");
				else
					first = false;

				String field = it.next();
				/**if( this.offset>=0 || this.limit>=0){*/
				// Rilascio vincolo di order by in caso di limit impostato.
				// Il vincolo rimane per l'offset, per gestire le select annidate di qualche implementazioni come Oracle,SQLServer ...
				if( this.offset>=0 ){
					
					field = this.normalizeField(field, false);

				} 
				bf.append(field);
			}
		}
	}
	

	/**
	 * Crea una SQL per una operazione di Delete con i dati dell'oggetto
	 * 
	 * @return SQL per una operazione di Delete
	 */
	@Override
	public String createSQLDeleteEngine() throws SQLQueryObjectException{
		
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
		
		if(!update && !conditions){
			// From
			bf.append(SQLQueryObjectCore.FROM_SEPARATOR);
			
			
			// Se e' presente Offset
			/**if( (this.offset>=0 || this.limit>=0) && (delete==false)){*/
			// Rilascio vincolo di order by in caso di limit impostato.
			// Il vincolo rimane per l'offset, per gestire le select annidate di qualche implementazioni come Oracle,SQLServer ...
			if( (this.offset>=0) && (!delete)){
				
				/**java.util.List<String> aliasOrderByDistinct = new java.util.ArrayList<>();*/
								
				if(!this.isSelectDistinct()){
				
					bf.append(SQLQueryObjectCore.SELECT_SEPARATOR_CON_INIZIO_APERTURA);
					
					Iterator<String> itForceIndex = this.forceIndexTableNames.iterator();
					while(itForceIndex.hasNext()){
						bf.append(" "+itForceIndex.next()+" ");
					}
									
					if(this.fields.isEmpty()){
						Iterator<String> it = this.tableNames.iterator();
						boolean first = true;
						while(it.hasNext()){
							if(!first)
								bf.append(",");
							else
								first = false;
							String t = it.next();
							bf.append(t+".*");
						}
					}else{
						Iterator<String> it = this.fields.iterator();
						boolean first = true;
						while(it.hasNext()){
							if(!first)
								bf.append(",");
							else
								first = false;
							String f = it.next();
							bf.append(f);
						}
					}
					
					
					bf.append(" , ROW_NUMBER() OVER ( ORDER BY ");
					
					// Condizione OrderBy
					if(this.orderBy.isEmpty()){
						throw new SQLQueryObjectException(SQLQueryObjectCore.CONDIZIONI_ORDER_BY_RICHESTE);
					}
					if(!this.orderBy.isEmpty()){
						Iterator<String> it = this.orderBy.iterator();
						boolean first = true;
						while(it.hasNext()){
							if(!first)
								bf.append(",");
							else
								first = false;
							String condizione = it.next();
							if(this.alias.containsKey(condizione)){
								bf.append(this.alias.get(condizione));
							}else{
								bf.append(condizione);
							}
							boolean sortTypeAsc = this.sortTypeAsc;
							if(this.orderBySortType.containsKey(condizione)){
								sortTypeAsc = this.orderBySortType.get(condizione);
							}
							if(sortTypeAsc){
								bf.append(SQLQueryObjectCore.ASC_SEPARATOR);
							}else{
								bf.append(SQLQueryObjectCore.DESC_SEPARATOR);
							}
						}
					}
				
					bf.append(" ) AS rowNumber ");
					
				
					bf.append(SQLQueryObjectCore.FROM_SEPARATOR);
				
								
					// Table dove effettuare la ricerca 'FromTable'
					if(this.tables.isEmpty()){
						throw new SQLQueryObjectException(SQLQueryObjectCore.TABELLA_RICERCA_FROM_NON_DEFINITA);
					}else{
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
				
					// Condizioni di Where
					if(!this.conditions.isEmpty()){
					
						bf.append(SQLQueryObjectCore.WHERE_SEPARATOR);
						
						if(this.notBeforeConditions){
							bf.append(SQLQueryObjectCore.NOT_SEPARATOR_APERTURA);
						}
						
						for(int i=0; i<this.conditions.size(); i++){
							
							if(i>0){
								if(this.andLogicOperator){
									bf.append(SQLQueryObjectCore.AND_SEPARATOR);
								}else{
									bf.append(SQLQueryObjectCore.OR_SEPARATOR);
								}
							}
							String cond = this.conditions.get(i);				
							bf.append(cond);
						}
						
						if(this.notBeforeConditions){
							bf.append(" )");
						}
					}
					
					// Condizione GroupBy
					if((!this.getGroupByConditions().isEmpty()) && (!delete)){
						bf.append(SQLQueryObjectCore.GROUP_BY_SEPARATOR);
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
					
					// ForUpdate (Non si può utilizzarlo con offset o limit in oracle)
/**					if(this.selectForUpdate){
//						bf.append(" FOR UPDATE ");
//					}*/
				
				}else{
					
					bf.append(SQLQueryObjectCore.SELECT_SEPARATOR_CON_INIZIO_APERTURA);
					
					Iterator<String> itForceIndex = this.forceIndexTableNames.iterator();
					while(itForceIndex.hasNext()){
						bf.append(" "+itForceIndex.next()+" ");
					}
					
					if(this.fields.isEmpty()){
						Iterator<String> it = this.tableNames.iterator();
						boolean first = true;
						while(it.hasNext()){
							if(!first)
								bf.append(",");
							else
								first = false;
							String t = it.next();
							bf.append(t+".*");
						}
					}else{
						Iterator<String> it = this.fields.iterator();
						boolean first = true;
						while(it.hasNext()){
							if(!first)
								bf.append(",");
							else
								first = false;
							String field = it.next();
							
							field = this.normalizeField(field, false);
							
							bf.append(field);
						}
					}
					
					// Cerco inoltre se ci sono delle condizioni di ORDER BY, ed in tal caso le metto come Alias
					/** NON FUNZIONA
					int contatoreAlias = 0;
					if(this.orderBy.size()>0){
						Iterator<String> it = this.orderBy.iterator();
						while(it.hasNext()){
							String fieldOrder = it.next();
							contatoreAlias++;
							String a = "ALIASFIELD"+contatoreAlias;
							aliasOrderByDistinct.add(a);
							// In questa lista di select field inoltre aggiunto l'alias.
							if(this.fields.size()==0)
								bf.append(a);
							else
								bf.append(","+a);
							// Verra' aggiunto nelle select piu' interna.
							//this.fields.add(fieldOrder +" as "+a);
							this.fields.add(fieldOrder +this.getAliasFieldKeyword()+a);
						}
					}
					*/
					
					bf.append(" , ROWNUM AS rowNumber");
				
					bf.append(SQLQueryObjectCore.FROM_SEPARATOR);
					
					bf.append(" ( SELECT DISTINCT ");
					
						if(this.fields.isEmpty()){
							bf.append("*");
						}else{
							Iterator<String> it = this.fields.iterator();
							boolean first = true;
							while(it.hasNext()){
								if(!first)
									bf.append(",");
								else
									first = false;
								String f = it.next();
								bf.append(f);
							}
						}
						
						bf.append(SQLQueryObjectCore.FROM_SEPARATOR);
					
						// Table dove effettuare la ricerca 'FromTable'
						if(this.tables.isEmpty()){
							throw new SQLQueryObjectException(SQLQueryObjectCore.TABELLA_RICERCA_FROM_NON_DEFINITA);
						}else{
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
					
						bf.append(" ");
						
						// Condizioni di Where
						if(!this.conditions.isEmpty()){
						
							bf.append(SQLQueryObjectCore.WHERE_SEPARATOR);
							
							if(this.notBeforeConditions){
								bf.append(SQLQueryObjectCore.NOT_SEPARATOR_APERTURA);
							}
							
							for(int i=0; i<this.conditions.size(); i++){
								
								if(i>0){
									if(this.andLogicOperator){
										bf.append(SQLQueryObjectCore.AND_SEPARATOR);
									}else{
										bf.append(SQLQueryObjectCore.OR_SEPARATOR);
									}
								}
								String cond = this.conditions.get(i);				
								bf.append(cond);
							}
							
							if(this.notBeforeConditions){
								bf.append(" )");
							}
						}
						
						// Condizione GroupBy
						if((!this.getGroupByConditions().isEmpty()) && (!delete)){
							bf.append(SQLQueryObjectCore.GROUP_BY_SEPARATOR);
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
						if(this.orderBy.isEmpty()){
							throw new SQLQueryObjectException(SQLQueryObjectCore.CONDIZIONI_ORDER_BY_RICHESTE);
						}
						if(!this.orderBy.isEmpty()){
							bf.append(SQLQueryObjectCore.ORDER_BY_SEPARATOR);
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
									bf.append(SQLQueryObjectCore.ASC_SEPARATOR);
								}else{
									bf.append(SQLQueryObjectCore.DESC_SEPARATOR);
								}
							}
						}
						
					bf.append(" ) ");
					
				}
				
				bf.append(" ) WHERE ( ");
				if(this.offset>=0){
					bf.append(" rowNumber > ");
					bf.append(this.offset);
				}
				if(this.limit>=0){
					if(this.offset>=0)
						bf.append(" AND");
					bf.append(" rowNumber <=  ");
					if(this.offset>=0)
						bf.append(this.offset+this.limit);
					else
						bf.append(this.limit);
				}
				bf.append(" )");
				
				
				// ORDER BY FINALE
				if(!union &&
					(!this.orderBy.isEmpty())
					){
					bf.append(SQLQueryObjectCore.ORDER_BY_SEPARATOR);
					Iterator<String> it = this.orderBy.iterator();
					boolean first = true;
					while(it.hasNext()){
						if(!first)
							bf.append(",");
						else
							first = false;
						String originalField = it.next();
						
						String field = this.normalizeField(originalField);
						
						bf.append(field);
						
						boolean sortTypeAsc = this.sortTypeAsc;
						if(this.orderBySortType.containsKey(originalField)){
							sortTypeAsc = this.orderBySortType.get(originalField);
						}
						if(sortTypeAsc){
							bf.append(SQLQueryObjectCore.ASC_SEPARATOR);
						}else{
							bf.append(SQLQueryObjectCore.DESC_SEPARATOR);
						}
					}
				}
				
				/** 
				 * OLD ALIAS
				if(aliasOrderByDistinct.size()>0){
					bf.append(SQLQueryObjectCore.ORDER_BY_SEPARATOR);
					Iterator<String> it = aliasOrderByDistinct.iterator();
					boolean first = true;
					while(it.hasNext()){
						if(!first)
							bf.append(",");
						else
							first = false;
						bf.append(it.next());
					}
					if(this.sortTypeAsc){
						bf.append(SQLQueryObjectCore.ASC_SEPARATOR);
					}else{
						bf.append(SQLQueryObjectCore.DESC_SEPARATOR);
					}
				}
				*/
				
			}else{
				
				// Offset non presente
				
				// Table dove effettuare la ricerca 'FromTable'
				if(this.tables.isEmpty()){
					throw new SQLQueryObjectException(SQLQueryObjectCore.TABELLA_RICERCA_FROM_NON_DEFINITA);
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
				
				// Condizioni di Where
				if(!this.conditions.isEmpty()){
					bf.append(SQLQueryObjectCore.WHERE_SEPARATOR);
					
					if(this.notBeforeConditions){
						bf.append("NOT (");
					}
					
					for(int i=0; i<this.conditions.size(); i++){
						if(i>0){
							if(this.andLogicOperator){
								bf.append(SQLQueryObjectCore.AND_SEPARATOR);
							}else{
								bf.append(SQLQueryObjectCore.OR_SEPARATOR);
							}
						}
						bf.append(this.conditions.get(i));
					}
					
					if(this.notBeforeConditions){
						bf.append(")");
					}
					
				}
						
				// LIMIT
				if(this.limit>=0){
					if(this.conditions.isEmpty()){
						bf.append(SQLQueryObjectCore.WHERE_SEPARATOR);
					}else{
						bf.append(SQLQueryObjectCore.AND_SEPARATOR);
					}
					bf.append(" ( rownum <= ");
					bf.append(this.limit);
					bf.append(" ) ");
				}
				
				// Condizione GroupBy
				if((!this.getGroupByConditions().isEmpty()) && (!delete)){
					bf.append(SQLQueryObjectCore.GROUP_BY_SEPARATOR);
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
				if(!union &&
					((!this.orderBy.isEmpty()) && (!delete))
					){
					bf.append(SQLQueryObjectCore.ORDER_BY_SEPARATOR);
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
							bf.append(SQLQueryObjectCore.ASC_SEPARATOR);
						}else{
							bf.append(SQLQueryObjectCore.DESC_SEPARATOR);
						}
					}
				}
				
				// ForUpdate
				if(this.selectForUpdate){
					bf.append(" FOR UPDATE ");
				}
			}
			
		}else{
			
			// UPDATE, conditions
			
			// Condizioni di Where
			if(!this.conditions.isEmpty()){
				
				if(!conditions)
					bf.append(SQLQueryObjectCore.WHERE_SEPARATOR);
				
				if(this.notBeforeConditions){
					bf.append("NOT (");
				}
				
				for(int i=0; i<this.conditions.size(); i++){
					if(i>0){
						if(this.andLogicOperator){
							bf.append(SQLQueryObjectCore.AND_SEPARATOR);
						}else{
							bf.append(SQLQueryObjectCore.OR_SEPARATOR);
						}
					}
					bf.append(this.conditions.get(i));
				}
				
				if(this.notBeforeConditions){
					bf.append(")");
				}
				
			}
			
			// Non genero per le condizioni, per update viene sollevata eccezione prima
			// ForUpdate
/**			if(this.selectForUpdate){
//				bf.append(" FOR UPDATE ");
//			}*/
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
	 * Sintassi OracleSQL:
	 * 
	 * SELECT * from
	 * (
	 * (  SELECT fields... FROM tabella WHERE conditions  )
	 * UNION [ALL]
	 * (  SELECT fields... FROM tabella WHERE conditions  )
	 * )
	 * WHERE ROWNUM&lt;=LIMIT+OFFSET AND ROWNUM&gt;OFFSET
	 * ORDER BY gdo,tipo_fruitore,fruitore,tipo_erogatore,erogatore,tipo_servizio,servizio DESC  
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
		
		StringBuilder bf = new StringBuilder();
		
		bf.append("SELECT ");
		
//		// Non ha senso, la union fa gia la distinct, a meno di usare la unionAll ma in quel caso non si vuole la distinct
/**		// if(this.isSelectDistinct())
//		//	bf.append(" DISTINCT ");*/
		
		
		if( this.offset<0 ){
			
			Iterator<String> itForceIndex = this.forceIndexTableNames.iterator();
			while(itForceIndex.hasNext()){
				bf.append(" "+itForceIndex.next()+" ");
			}
			
			if(this.fields.isEmpty()){
				bf.append("* ");
			}else{
				Iterator<String> it = this.fields.iterator();
				boolean first = true;
				while(it.hasNext()){
					if(!first)
						bf.append(",");
					else
						first = false;
					String f = it.next();
					bf.append(f);
				}
				bf.append(" ");
			}
	
		}
		else{
			bf.append("* ");
		}
	
		bf.append(SQLQueryObjectCore.FROM_SEPARATOR);
		
		// Se e' presente Offset
		/**if( (this.offset>=0 || this.limit>=0) ){*/
		// Rilascio vincolo di order by in caso di limit impostato.
		// Il vincolo rimane per l'offset, per gestire le select annidate di qualche implementazioni come Oracle,SQLServer ...
		if( this.offset>=0 ){
			
			bf.append(SQLQueryObjectCore.SELECT_SEPARATOR_CON_INIZIO_APERTURA);
			
			Iterator<String> itForceIndex = this.forceIndexTableNames.iterator();
			while(itForceIndex.hasNext()){
				bf.append(" "+itForceIndex.next()+" ");
			}
			
			if(this.fields.isEmpty()){
				Iterator<String> it = this.tableNames.iterator();
				boolean first = true;
				while(it.hasNext()){
					if(!first)
						bf.append(",");
					else
						first = false;
					String t = it.next();
					bf.append(t+".*");
				}
			}else{
				Iterator<String> it = this.fields.iterator();
				boolean first = true;
				while(it.hasNext()){
					if(!first)
						bf.append(",");
					else
						first = false;
					String f = it.next();
					bf.append(f);
				}
			}
			
			bf.append(" , ROW_NUMBER() OVER ( ORDER BY ");
			
			// Condizione OrderBy
			if(this.orderBy.isEmpty()){
				throw new SQLQueryObjectException(SQLQueryObjectCore.CONDIZIONI_ORDER_BY_RICHESTE);
			}
			if(!this.orderBy.isEmpty()){
				Iterator<String> it = this.orderBy.iterator();
				boolean first = true;
				while(it.hasNext()){
					if(!first)
						bf.append(",");
					else
						first = false;
					String condizione = it.next();
					if(this.alias.containsKey(condizione)){
						bf.append(this.alias.get(condizione));
					}else{
						bf.append(condizione);
					}
					boolean sortTypeAsc = this.sortTypeAsc;
					if(this.orderBySortType.containsKey(condizione)){
						sortTypeAsc = this.orderBySortType.get(condizione);
					}
					if(sortTypeAsc){
						bf.append(SQLQueryObjectCore.ASC_SEPARATOR);
					}else{
						bf.append(SQLQueryObjectCore.DESC_SEPARATOR);
					}
				}
			}
			
			bf.append(" ) AS rowNumber ");
			
			
			// Table dove effettuare la ricerca 'FromTable'
			bf.append(SQLQueryObjectCore.FROM_SEPARATOR_APERTURA);
			
			for(int i=0; i<sqlQueryObject.length; i++){
				
				if(((OracleQueryObject)sqlQueryObject[i]).selectForUpdate){
					try{
						((OracleQueryObject)sqlQueryObject[i]).checkSelectForUpdate(false, false, true);
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
				
				bf.append(((OracleQueryObject)sqlQueryObject[i]).createSQLQuery(true));
				
				bf.append(") ");
			}
			
			bf.append(" ) ");
			
			// Condizioni di Where
			if(!this.conditions.isEmpty()){
			
				bf.append(SQLQueryObjectCore.WHERE_SEPARATOR);
				
				if(this.notBeforeConditions){
					bf.append(SQLQueryObjectCore.NOT_SEPARATOR_APERTURA);
				}
				
				for(int i=0; i<this.conditions.size(); i++){
					
					if(i>0){
						if(this.andLogicOperator){
							bf.append(SQLQueryObjectCore.AND_SEPARATOR);
						}else{
							bf.append(SQLQueryObjectCore.OR_SEPARATOR);
						}
					}
					String cond = this.conditions.get(i);
					bf.append(cond);
				}
				
				if(this.notBeforeConditions){
					bf.append(" )");
				}
			}
			
			// Condizione GroupBy
			if((!this.getGroupByConditions().isEmpty())){
				bf.append(SQLQueryObjectCore.GROUP_BY_SEPARATOR);
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
			
			bf.append(" ) WHERE ( ");
			if(this.offset>=0){
				bf.append(" rowNumber > ");
				bf.append(this.offset);
			}
			if(this.limit>=0){
				if(this.offset>=0)
					bf.append(" AND");
				bf.append(" rowNumber <=  ");
				if(this.offset>=0)
					bf.append(this.offset+this.limit);
				else
					bf.append(this.limit);
			}
			bf.append(" )");
		
		
		}else{
			
			// no offset
			
			bf.append(" ( ");
			
			for(int i=0; i<sqlQueryObject.length; i++){
				
				if(((OracleQueryObject)sqlQueryObject[i]).selectForUpdate){
					try{
						((OracleQueryObject)sqlQueryObject[i]).checkSelectForUpdate(false, false, true);
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
				
				bf.append(((OracleQueryObject)sqlQueryObject[i]).createSQLQuery(true));
				
				bf.append(") ");
			}
			
			bf.append(") ");

			// LIMIT
			if(this.limit>=0){
				bf.append(SQLQueryObjectCore.WHERE_SEPARATOR);
				bf.append(" ( rownum <= ");
				bf.append(this.limit);
				bf.append(" ) ");
			}	
			
			// Condizione GroupBy
			if((!this.getGroupByConditions().isEmpty())){
				bf.append(SQLQueryObjectCore.GROUP_BY_SEPARATOR);
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
			if(!this.orderBy.isEmpty()){
				bf.append(SQLQueryObjectCore.ORDER_BY_SEPARATOR);
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
						bf.append(SQLQueryObjectCore.ASC_SEPARATOR);
					}else{
						bf.append(SQLQueryObjectCore.DESC_SEPARATOR);
					}
				}
			}
		}
		
/**		bf.append(" ) ");*/
		
		return bf.toString();
	}
	
	
	/**
	 * Crea la stringa SQL per l'unione dei parametri presi in sqlQueryObject (viene effettuato il count)
	 * Il booleano 'unionAll' indica se i vari parametri devono essere uniti con 'UNION'  o con 'UNION ALL'
	 * 
	 * Sintassi OracleSQL:
	 * 
	 * select count(*) as aliasCount FROM (
	 * 
	 * (  SELECT fields... FROM tabella WHERE conditions  )
	 * UNION [ALL]
	 * (  SELECT fields... FROM tabella WHERE conditions  )
	 * 
	 * )
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
		
		StringBuilder bf = new StringBuilder();
		
		bf.append("SELECT count(*) "+this.getDefaultAliasFieldKeyword()+" ");
		bf.append(aliasCount);
		bf.append(SQLQueryObjectCore.FROM_SEPARATOR_APERTURA);
		
		bf.append( this.createSQLUnion(unionAll, sqlQueryObject) );
		
		bf.append(") ");
		
		return bf.toString();
		
	}
	
	
	
	/**
	 * Crea una SQL per una operazione di Update con i dati dell'oggetto
	 * 
	 * @return SQL per una operazione di Update
	 */
	@Override
	public String createSQLUpdateEngine() throws SQLQueryObjectException{
		
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
	
	/**
	 * Crea le condizioni presenti senza anteporre il comando (INSERT,CREATE,UPDATE), le tabelle interessate e il 'WHERE'
	 * 
	 * @return SQL Query
	 */
	@Override
	public String createSQLConditionsEngine() throws SQLQueryObjectException{
		
		StringBuilder bf = new StringBuilder();
		bf.append(getSQL(false,false,true,false));
		return bf.toString();
	}
	
	
	
}
