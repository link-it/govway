/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.date.DateUtils;

/**
 * SQLServerQueryObject
 * 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SQLServerQueryObject extends SQLQueryObjectCore {
	/**private boolean sottoselect = false;*/


	public SQLServerQueryObject(TipiDatabase tipoDatabase) {
		super(tipoDatabase);
	}


	
	@Override
	protected boolean continueNormalizeField(String normalizeField){
		
		// Alcuni valori sono standard dei vendor dei database (es. gestione delle date)
		// Il problema è che se contengono dei '.' o dei caratteri alias rientrano erroneamnete nei punti 2 e 3 della normalizzazione
		// Per questo motivo viene quindi prima richiesto al vendor se effettuare o meno la classica normalizzazione del field in base a tali valori
		// sul field in essere
		
		boolean contains = (normalizeField!=null && 
				// Con differenza su timezone:
//				normalizeField.contains("(CAST(DATEDIFF(second,{d '1970-01-01'}") 
//				&& 
//				normalizeField.contains("as BIGINT)*1000) + (DATEPART(ms") 
//				&&
//				normalizeField.contains("(CAST(DATEDIFF(HOUR,GETUTCDATE(),convert(datetime")
				
				// senza differenza su timezone:
//				normalizeField.contains("(CAST(DATEDIFF(second,{d '1970-01-01'}") 
//				&& 
//				normalizeField.contains("as BIGINT)*1000) + (DATEPART(ms") 
				
				// altro modo
				normalizeField.contains("(CAST(DATEDIFF(s, '1970-01-01 00:00:00',")
				&& 
				normalizeField.contains("as BIGINT)*1000) + (DATEPART(ms") 
				
				);
		
		return !contains;
		
	}
	
	
	
	
	/**
	 * Ritorna una costante  di tipo 'timestamp'
	 * 
	 * @param date Costante
	 */
	@Override
	public String getSelectTimestampConstantField(Date date) throws SQLQueryObjectException{
		SimpleDateFormat sqlDateformat = DateUtils.getDefaultDateTimeFormatter("yyyy-MM-dd HH:mm:ss.SSS");
		return "CAST('"+sqlDateformat.format(date)+"' AS datetime2)";
	}
	
	
	
	
	@Override
	public String getUnixTimestampConversion(String column){
		// Con differenza su timezone:
/**		String format = "yyyy-MM-dd HH:mm:ss";
//		java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat (format);
//		return "("+
//		" (CAST(DATEDIFF(second,{d '1970-01-01'},"+column+") as BIGINT)*1000) + (DATEPART(ms,"+column+"))"+
//		" - "+
//		" (CAST(DATEDIFF(HOUR,GETUTCDATE(),convert(datetime, '"+dateformat.format(org.openspcoop2.utils.date.DateManager.getDate())+"', 120)) as BIGINT)*60*60*1000) "+
//		")";*/
		
		// senza differenza su timezone:
/**		return " (CAST(DATEDIFF(second,{d '1970-01-01'},"+column+") as BIGINT)*1000) + (DATEPART(ms,"+column+"))";*/
		
		// altro modo
		return "(CAST(DATEDIFF(s, '1970-01-01 00:00:00', "+column+") as BIGINT)*1000) + (DATEPART(ms,"+column+"))";
	}

	@Override
	public String getDiffUnixTimestamp(String columnMax,String columnMin){
		return "( "+getUnixTimestampConversion(columnMax)+" - "+getUnixTimestampConversion(columnMin)+" )";
	}





	@Override
	public ISQLQueryObject addSelectAvgTimestampField(String field,
			String alias) throws SQLQueryObjectException {
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


	@Override
	public ISQLQueryObject addSelectMaxTimestampField(String field, String alias)
			throws SQLQueryObjectException {
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


	@Override
	public ISQLQueryObject addSelectMinTimestampField(String field, String alias)
			throws SQLQueryObjectException {
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


	@Override
	public ISQLQueryObject addSelectSumTimestampField(String field, String alias)
			throws SQLQueryObjectException {
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





	@Override
	public ISQLQueryObject addFromTable(ISQLQueryObject subSelect)
			throws SQLQueryObjectException {
		StringBuilder bf = new StringBuilder();
		bf.append(" ( ");
		bf.append(subSelect.createSQLQuery());
		bf.append(" ) ");
		// Devo forzare l'utilizzo di un alias su una sottoselct dentro il FROM	
		// Genero Tre caratteri alafabetici casuali per dare un alias del tipo "tabellaXXX"
		StringBuilder subselectalias = new StringBuilder();
		subselectalias.append("tabella");
		int rnd; 
		char base;
		for (int count=0 ; count < 3; count++ ){
			rnd = (SQLQueryObjectCore.getRandom().nextInt(52) );
			base = (rnd < 26) ? 'A' : 'a';
			subselectalias.append((char) (base + rnd % 26));			
		}
		this.addFromTable(bf.toString(),subselectalias.toString());
		return this;
	}




	@Override
	protected EscapeSQLConfiguration getEscapeSQLConfiguration(){
		
		EscapeSQLConfiguration config = new EscapeSQLConfiguration();
		config.addCharacter('_');
		config.addCharacter('%');
		config.addCharacter('\\');
		config.addCharacter('[');
		config.addCharacter(']');
		config.addCharacter('^');
		config.setUseEscapeClausole(true);
		config.setEscape('\\');
		
		// special
		config.addCharacterWithOtherEscapeChar('\'','\'');

		return config;
	}



	
	
	
	@Override
	public String getExtractDateTimePartFromTimestampFieldPrefix(DateTimePartEnum dateTimePart) throws SQLQueryObjectException {
		if(dateTimePart==null) {
			throw new SQLQueryObjectException("dateTimePart undefined");
		}
		String dateTimePartString = getDateTimePart(dateTimePart);
		return "DATEPART("+dateTimePartString+FROM_SEPARATOR; 
	}

	
	
	
	
	private static final String DAY_FORMAT_FULL_DAY_NAME = "dddd";
	private static final String DAY_FORMAT_SHORT_DAY_NAME = "ddd";
	private static final String DAY_FORMAT_DAY_OF_YEAR = "dy";
	private static final String DAY_FORMAT_DAY_OF_WEEK = "WEEKDAY";
	
	private SQLQueryObjectException newSQLQueryObjectExceptionDayFormatEnum(DayFormatEnum dayFormat) {
		return new SQLQueryObjectException("DayFormatEnum '"+dayFormat+"' unknown");
	}
	@Override
	protected String getDayFormat(DayFormatEnum dayFormat) throws SQLQueryObjectException {
		switch (dayFormat) {
		case FULL_DAY_NAME:
			return DAY_FORMAT_FULL_DAY_NAME;
		case SHORT_DAY_NAME:
			return DAY_FORMAT_SHORT_DAY_NAME;
		case DAY_OF_YEAR:
			return DAY_FORMAT_DAY_OF_YEAR;
		case DAY_OF_WEEK:
			return DAY_FORMAT_DAY_OF_WEEK;
		}
		throw newSQLQueryObjectExceptionDayFormatEnum(dayFormat);
	}
	@Override
	public String getExtractDayFormatFromTimestampFieldPrefix(DayFormatEnum dayFormat) throws SQLQueryObjectException {
		if(dayFormat==null) {
			throw new SQLQueryObjectException("dayFormat undefined");
		}
		switch (dayFormat) {
		case FULL_DAY_NAME:
		case SHORT_DAY_NAME:
			return "FORMAT("; 
		case DAY_OF_YEAR:
			return "DATENAME("+getDayFormat(dayFormat)+" , "; 
		case DAY_OF_WEEK:
			return "DATEPART("+getDayFormat(dayFormat)+", "; 
		}
		throw newSQLQueryObjectExceptionDayFormatEnum(dayFormat);
	}
	@Override
	public String getExtractDayFormatFromTimestampFieldSuffix(DayFormatEnum dayFormat) throws SQLQueryObjectException {
		if(dayFormat==null) {
			throw new SQLQueryObjectException("dayFormat undefined");
		}
		switch (dayFormat) {
		case FULL_DAY_NAME:
		case SHORT_DAY_NAME:
			String dayFormatString = getDayFormat(dayFormat);
			return ", '"+dayFormatString+"')"; 
		case DAY_OF_YEAR:
		case DAY_OF_WEEK:
			return ")"; 
		}
		throw newSQLQueryObjectExceptionDayFormatEnum(dayFormat);
	}
	
	
	
	


	@Override
	public String createSQLQuery() throws SQLQueryObjectException{
		return this.createSQLQuery(false);
	}
	private String createSQLQuery(boolean union) throws SQLQueryObjectException{

		this.precheckBuildQuery();

		StringBuilder bf = new StringBuilder();

/**		StringBuilder cursorName = null;
//		if(this.selectForUpdate){
//			
//			cursorName = new StringBuilder();
//			cursorName.append("cursorName");
//			Random rand = new Random();
//			int rnd; 
//			char base;
//			for (int count=0 ; count < 3; count++ ){
//				rnd = (rand.nextInt(52) );
//				base = (rnd < 26) ? 'A' : 'a';
//				cursorName.append((char) (base + rnd % 26));			
//			}
//			
//			bf.append("DECLARE "+cursorName+" CURSOR FOR ");
//		}*/
		
		bf.append("SELECT ");

		if(this.isSelectDistinct())
			bf.append(" DISTINCT ");

		// Limit (senza offset)
		if(this.offset<0 && this.limit>0){
			bf.append(" TOP ");
			bf.append(this.limit);
			bf.append(" ");
		}
		else{
			// Questa istruzione ci vuole altrimenti in presenza di order by, group by si ottiene il seguente errore:
			// The ORDER BY clause is invalid in views, inline functions, derived tables, subqueries, and common table expressions, unless TOP or FOR XML is also specified.
			bf.append("TOP 100 PERCENT ");
		}

		// forzatura di indici
		if( !(this.offset>=0 || this.limit>=0) ){
			Iterator<String> itForceIndex = this.forceIndexTableNames.iterator();
			while(itForceIndex.hasNext()){
				bf.append(" "+itForceIndex.next()+" ");
			}
		}

		// select field
		addSQLQuerySelectField(bf);

		bf.append(getSQL(false,false,false,union));

		/**if( this.offset>=0 || this.limit>=0)
		//	System.out.println("SQL ["+bf.toString()+"]");*/
		
/**		if(this.selectForUpdate){
//			
//			bf.append("OPEN "+cursorName.toString()+" ");
//			bf.append("FETCH NEXT FROM "+cursorName.toString()+" ");
//			bf.append("WHILE @@FETCH_STATUS = 0 ");
//			bf.append("BEGIN ");
//			bf.append("FETCH NEXT FROM "+cursorName.toString()+" ");
//			bf.append("END ");
//			bf.append("CLOSE "+cursorName.toString()+" ");
//			bf.append("DEALLOCATE "+cursorName.toString()+" ");
//			
//		}*/
		
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
				if( this.offset>=0 ){

					field = this.normalizeField(field, false);

				} 
				bf.append(field);
			}
		}
	}





	@Override
	public String createSQLDeleteEngine() throws SQLQueryObjectException {

		StringBuilder bf = new StringBuilder();

		bf.append("DELETE ");

		bf.append(getSQL(true,false,false,false));
		return bf.toString();
	}





	private String getSQL(boolean delete,boolean update,boolean conditions,boolean union) throws SQLQueryObjectException {	
		StringBuilder bf = new StringBuilder();

		if(this.selectForUpdate){
			this.checkSelectForUpdate(update, delete, union);
		}
		
		if(!update && !conditions){
			// From
			bf.append(SQLQueryObjectCore.FROM_SEPARATOR);


			// Se e' presente Offset o Limit
			/**if( (this.offset>=0 || this.limit>=0) && (delete==false)) {*/
			// Rilascio vincolo di order by in caso di limit impostato.
			// Il vincolo rimane per l'offset, per gestire le select annidate di qualche implementazioni come Oracle,SQLServer ...
			if( (this.offset>=0) && (!delete)){

				/**java.util.List<String> aliasOrderByDistinct = new java.util.ArrayList<>();*/

				/**if(this.isSelectDistinct()==false)*/			
				bf.append(SQLQueryObjectCore.SELECT_SEPARATOR_CON_INIZIO_APERTURA);
				/**else
				//	bf.append(" ( SELECT DISTINCT ");*/

				// Questa istruzione ci vuole altrimenti in presenza di order by, group by si ottiene il seguente errore:
				// The ORDER BY clause is invalid in views, inline functions, derived tables, subqueries, and common table expressions, unless TOP or FOR XML is also specified.
				// In questo segmento di select forse non server?
				/** bf.append("TOP 100 PERCENT ");*/

				Iterator<String> itForceIndex = this.forceIndexTableNames.iterator();
				while(itForceIndex.hasNext()){
					bf.append(" "+itForceIndex.next()+" ");
				}

				if(this.isSelectDistinct()){
					// select field
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
							if( this.offset>=0 ){

								field = this.normalizeField(field, false);

							} 
							bf.append(field);
						}
					}
				}
				else{			
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
						/**System.out.println("=======================");
						System.out.println("alias: "+this.alias);
						System.out.println("condizione: "+condizione);
						System.out.println("KEY: "+(this.alias.containsKey(condizione)));
						System.out.println("VALUE: "+(this.alias.containsValue(condizione)));
						System.out.println("DISTINCT: "+(this.isSelectDistinct()));
						System.out.println("=======================");*/
						if(this.alias.containsKey(condizione)){
							if(this.isSelectDistinct()){
								bf.append(condizione);
							}
							else{
								bf.append(this.alias.get(condizione));
							}
						}else if(this.alias.containsValue(condizione)) {
							bf.append(condizione);
						}
						else{
							bf.append(this.normalizeField(condizione,false));
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

				if(this.isSelectDistinct()){

					bf.append(" ( SELECT DISTINCT TOP 100 PERCENT ");

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
				}

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

				if(this.isSelectDistinct()){

					// Order solo in presenza di select distinct
					if((!this.orderBy.isEmpty()) && (!delete)){
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

					// Devo forzare l'utilizzo di un alias su una sottoselct dentro il FROM			
					bf.append(" ) ");
					bf.append(this.getDefaultAliasFieldKeyword());
					bf.append("tableSelectRaw");
					// Genero Tre caratteri alfabetici casuali per dare un alias del tipo "tabellaXXX"
					int rnd; 
					char base;
					for (int count=0 ; count < 3; count++ ){
						rnd = (SQLQueryObjectCore.getRandom().nextInt(52) );
						base = (rnd < 26) ? 'A' : 'a';
						bf.append((char) (base + rnd % 26));

					}
				}


				// Devo forzare l'utilizzo di un alias su una sottoselct dentro il FROM			
				bf.append(" ) ");
				bf.append(this.getDefaultAliasFieldKeyword());
				bf.append("tabella");
				// Genero Tre caratteri alfabetici casuali per dare un alias del tipo "tabellaXXX"
				int rnd; 
				char base;
				for (int count=0 ; count < 3; count++ ){
					rnd = (SQLQueryObjectCore.getRandom().nextInt(52) );
					base = (rnd < 26) ? 'A' : 'a';
					bf.append((char) (base + rnd % 26));

				}
				bf.append(" WHERE ( ");

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

				// ForUpdate (Non si può utilizzarlo con offset o limit in oracle)
/**				if(this.selectForUpdate){
//					bf.append(" FOR UPDATE ");
//				}*/
				
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

				// For Update
				if(this.selectForUpdate){
					bf.append(" WITH (ROWLOCK) ");
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
					(!this.orderBy.isEmpty()) && (!delete)
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
				
//				// ForUpdate
/**				if(this.selectForUpdate){
//					bf.append(" FOR UPDATE ");
//				}*/
			}
		}else{

			// UPDATE, conditions

			// Non genero per le condizioni, per update viene sollevata eccezione prima
			// For Update
/**			if(this.selectForUpdate){
//				bf.append(" WITH (ROWLOCK) ");
//			}*/
			
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
			
//			// ForUpdate
/**			if(this.selectForUpdate){
//				bf.append(" FOR UPDATE ");
//			}*/
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

		// Non ha senso, la union fa gia la distinct, a meno di usare la unionAll ma in quel caso non si vuole la distinct
		/** if(this.isSelectDistinct())
		//	bf.append(" DISTINCT ");*/

		// Se e' presente Offset o Limit
		// Rilascio vincolo di order by in caso di limit impostato.
		// Il vincolo rimane per l'offset, per gestire le select annidate di qualche implementazioni come Oracle,SQLServer ...
		/**if( (this.offset>=0 || this.limit>=0) ){*/
		if( this.offset>=0 ){

			bf.append("SELECT TOP 100 PERCENT * from ");

			bf.append(SQLQueryObjectCore.SELECT_SEPARATOR_CON_INIZIO_APERTURA);

			// Questa istruzione ci vuole altrimenti in presenza di order by, group by si ottiene il seguente errore:
			// The ORDER BY clause is invalid in views, inline functions, derived tables, subqueries, and common table expressions, unless TOP or FOR XML is also specified.
			// In questo segmento di select forse non server?
			/** bf.append("TOP 100 PERCENT ");*/

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

				if(((SQLServerQueryObject)sqlQueryObject[i]).selectForUpdate){
					try{
						((SQLServerQueryObject)sqlQueryObject[i]).checkSelectForUpdate(false, false, true);
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

				bf.append(((SQLServerQueryObject)sqlQueryObject[i]).createSQLQuery(true));

				bf.append(") ");
			}

			bf.append(SQLQueryObjectCore.AS_SUBQUERY_SUFFIX+getSerial()+" ");

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

			bf.append(SQLQueryObjectCore.AS_SUBQUERY_SUFFIX+getSerial()+" ");

			bf.append(" WHERE ( ");
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

			bf.append("SELECT ");

			// Limit (senza offset)
			if(this.offset<0 && this.limit>0){
				bf.append("TOP ");
				bf.append(this.limit);
				bf.append(" ");
			}
			else{
				// Questa istruzione ci vuole altrimenti in presenza di order by, group by si ottiene il seguente errore:
				// The ORDER BY clause is invalid in views, inline functions, derived tables, subqueries, and common table expressions, unless TOP or FOR XML is also specified.
				bf.append("TOP 100 PERCENT ");
			}


			Iterator<String> itForceIndex = this.forceIndexTableNames.iterator();
			while(itForceIndex.hasNext()){
				bf.append(" "+itForceIndex.next()+" ");
			}

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

			bf.append(SQLQueryObjectCore.FROM_SEPARATOR_APERTURA);

			for(int i=0; i<sqlQueryObject.length; i++){

				if(((SQLServerQueryObject)sqlQueryObject[i]).selectForUpdate){
					try{
						((SQLServerQueryObject)sqlQueryObject[i]).checkSelectForUpdate(false, false, true);
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

				bf.append(((SQLServerQueryObject)sqlQueryObject[i]).createSQLQuery(true));

				bf.append(") ");
			}

			bf.append(SQLQueryObjectCore.AS_SUBQUERY_SUFFIX+getSerial()+" ");


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
		bf.append(SQLQueryObjectCore.FROM_SEPARATOR_APERTURA);

		bf.append( this.createSQLUnion(unionAll, sqlQueryObject) );

		bf.append(SQLQueryObjectCore.AS_SUBQUERY_SUFFIX+getSerial()+" ");

		return bf.toString();
	}

	@Override
	public String createSQLUpdateEngine() throws SQLQueryObjectException {
		
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
	public String createSQLConditionsEngine() throws SQLQueryObjectException {
		
		StringBuilder bf = new StringBuilder();
		bf.append(getSQL(false,false,true,false));
		return bf.toString();
	}



}
