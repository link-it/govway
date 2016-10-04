/*
 * OpenSPCoop - Customizable API Gateway 
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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.openspcoop2.utils.TipiDatabase;


/**
 * Classe dove vengono forniti utility per la conversione di comandi SQL per database diversi 
 * 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class SQLQueryObjectCore implements ISQLQueryObject{

	/** Vector di Field esistenti: i nomi dei fields impostati (se e' stato utilizzato un alias ritorna comunque il nome della colonna) */
	Vector<String> fields = new Vector<String>();
	/** Vector di NomiField esistenti: i nomi dei fields impostati (se e' stato utilizzato un alias ritorna il valore dell'alias) */
	Vector<String> fieldNames = new Vector<String>();
	/** Mapping tra alias e indicazione se e' una function: i nomi dei fields impostati (se e' stato utilizzato un alias ritorna il valore dell'alias) */
	Hashtable<String, Boolean> fieldNameIsFunction = new Hashtable<String, Boolean>();
	/** Mapping tra alias (key) e field names (value) (sono presenti i mapping solo per le colonne per cui e' stato definito un alias) */
	Hashtable<String, String> alias = new Hashtable<String, String>();
	
	/** Vector di Tabelle esistenti */
	Vector<String> tables = new Vector<String>();
	Vector<String> tableNames = new Vector<String>();
	Vector<String> tableAlias = new Vector<String>();
	
	/** Vector di Field esistenti */
	Vector<String> conditions = new Vector<String>();
	public int sizeConditions(){
		return this.conditions.size();
	}
	
	/** Vector di indici forzati */
	Vector<String> forceIndexTableNames = new Vector<String>();
	
	/** OperatorLogic */
	boolean andLogicOperator = false;
	
	/** Not */
	boolean notBeforeConditions = false;
	
	/** GroupBy di Field esistenti */
	private Vector<String> groupBy = new Vector<String>();
	
	/** OrderBy di Field esistenti */
	Vector<String> orderBy = new Vector<String>();
	Hashtable<String, Boolean> orderBySortType = new Hashtable<String, Boolean>();
	
	/** Tipo di ordinamento */
	boolean sortTypeAsc = true;
	
	/** Distinct */
	private boolean distinct = false;
	
	/** Limit */
	int limit = -1;
	/** Offset */
	int offset = -1;

	/** SelectForUpdate */
	boolean selectForUpdate = false;
	
	/* UPDATE */
	/** Vector di Field per l'update */
	Vector<String> updateFieldsName = new Vector<String>();
	Vector<String> updateFieldsValue = new Vector<String>();
	/** Tabella per l'update */
	String updateTable = null;

	/* INSERT */
	/** Vector di Field per l'insert */
	Vector<String> insertFieldsName = new Vector<String>();
	Vector<String> insertFieldsValue = new Vector<String>();
	/** Tabella per l'insert */
	String insertTable = null;

	/* TipoDatabase */
	private TipiDatabase tipoDatabase;

	/* preCheck */
	private boolean precheckQuery = true;
	public void setPrecheckQuery(boolean precheckQuery) {
		this.precheckQuery = precheckQuery;
	}

	// Increment
	private int serial = 0;
	protected synchronized int getSerial(){
		this.serial++;
		return this.serial;
	}

	// Force to false SelectForUpdate in CRUD Method
	private boolean forceSelectForUpdateDisabledForNotQueryMethod = false; // viene usato nel progetto generic forzato a true per JDBC_SQLObjectFactory
	public void setForceSelectForUpdateDisabledForNotQueryMethod(boolean forceSelectForUpdateDisabledForNotQueryMethod) {
		this.forceSelectForUpdateDisabledForNotQueryMethod = forceSelectForUpdateDisabledForNotQueryMethod;
	}


	// COSTRUTTORE
	public SQLQueryObjectCore(TipiDatabase tipoDatabase){
		this.tipoDatabase = tipoDatabase;
	}
	
	




	// UTILITIES

	protected void precheckBuildQuery() throws SQLQueryObjectException{

		if(this.precheckQuery==false){
			return;
		}

		// Check Offset
		if(this.offset>=0){
			if(this.orderBy.size()==0){
				throw new SQLQueryObjectException("Condizioni di OrderBy richieste");
			}
		}
		
		// Check GroupBy
		if(this.groupBy.size()>0){

			// verifico che tutte le condizioni di order by siano presente anche nei field di group by
			if(this.orderBy.size()>0){
				for (String order : this.orderBy) {
					boolean exists = false;
					for (String groupBy : this.groupBy) {
						if(normalizeField(groupBy).equals(normalizeField(order))){
							exists = true;
							break;
						}
					}
					if(!exists){
						String orderField = normalizeField(order);
						try{
							if(this.isFieldNameForFunction(orderField)==false){
								throw new SQLQueryObjectException("La colonna "+order+" utilizzata nella condizione di ORDER BY deve apparire anche in una condizione di GROUP BY");
							}
						}catch(SQLQueryObjectException sqlObject){
							throw new SQLQueryObjectException("La colonna "+order+" utilizzata nella condizione di ORDER BY deve apparire anche in una condizione di GROUP BY",sqlObject);
						}
					}
				}
			}

			// verifico che tutte le condizioni di group by siano presenti anche nei field
			for (String groupBy : this.groupBy) {
				boolean exists = false;
				for (String field : this.fields) {
					if(normalizeField(groupBy).equals(normalizeField(field))){
						exists = true;
						break;
					}
				}
				if(!exists){
					throw new SQLQueryObjectException("La colonna "+groupBy+" utilizzata nella condizione di GROUP BY deve essere anche selezionato come select field");
				}
			}
		}
		
		// Check Select For Update
		if(this.selectForUpdate){
			if(this.groupBy!=null && this.groupBy.size()>0){
				throw new SQLQueryObjectException("Non è possibile abilitare il comando 'selectForUpdate' se viene utilizzata la condizione di GROUP BY");
			}
			else if(this.distinct){
				throw new SQLQueryObjectException("Non è possibile abilitare il comando 'selectForUpdate' se viene utilizzata la clausola DISTINCT");
			}
			else if(this.limit>=0){
				throw new SQLQueryObjectException("Non è possibile abilitare il comando 'selectForUpdate' se viene utilizzata la clausola LIMIT");
			}
			else if(this.offset>=0){
				throw new SQLQueryObjectException("Non è possibile abilitare il comando 'selectForUpdate' se viene utilizzata la clausola OFFSET");
			}
		}
	}

	protected String normalizeField(String field){
		return this.normalizeField(field, true);
	}
	protected String normalizeField(String field, boolean firstSearchFromAliasesField){

		if(firstSearchFromAliasesField){
			// 1. Viene fornito il nome della colonna su database.
			// Potrebbe essere stato mappato con un alias nella select field.
			// In tal caso deve essere ricavato ed utilizzato l'alias
			Iterator<String> itRicercaAlias = this.fields.iterator();
			while(itRicercaAlias.hasNext()){
				String fieldRicercaAliasa = itRicercaAlias.next();
				String [] split = null;
				if(fieldRicercaAliasa.contains(" as ")){
					split = fieldRicercaAliasa.split(" as ");
				}else if(fieldRicercaAliasa.contains(" As ")){
					split = fieldRicercaAliasa.split(" As ");
				}else if(fieldRicercaAliasa.contains(" aS ")){
					split = fieldRicercaAliasa.split(" aS ");
				}else if(fieldRicercaAliasa.contains(" AS ")){
					split = fieldRicercaAliasa.split(" AS ");
				}else if(fieldRicercaAliasa.contains(" ")){
					split = fieldRicercaAliasa.split(" ");
				}else if(fieldRicercaAliasa.contains(this.getDefaultAliasFieldKeyword())){
					split = fieldRicercaAliasa.split(this.getDefaultAliasFieldKeyword());
				}
				if(split==null){
					List<String> aliases = this.getSupportedAliasesField();
					if(aliases!=null && aliases.size()>0){
						for (String alias : aliases) {
							if(fieldRicercaAliasa.contains(alias)){
								split = fieldRicercaAliasa.split(alias);
								break;
							}
						}
					}
				}
				if(split!=null && split.length==2){
					split[0] = split[0].trim();
					split[1] = split[1].trim();
					if(field.equals(split[0])){
						return split[1];
					}
				}
			}
		}

		// 2. Altrimenti devo verificare se vi e' un prefisso di tabella davanti
		// Devono essere eliminati le TABELLE. e lasciare solo il field
		int indexOf = field.indexOf(".");
		if( (indexOf!=-1) && ((indexOf+1)<field.length()) ){
			field = field.substring(indexOf+1);
		}

		// 3. Anche se ho eliminato il prefisso della tabella, potrei avere come field una stringa tipo 'colonna as alias'
		// In tal caso devo tornare solo alias.
		List<String> aliasModeSupportati = new ArrayList<String>();
		for (Iterator<?> iterator = this.getSupportedAliasesField().iterator(); iterator.hasNext();) {
			aliasModeSupportati.add((String)iterator.next());
		}
		if(aliasModeSupportati.contains(" ")==false){
			aliasModeSupportati.add(" ");
		}
		if(aliasModeSupportati.contains(" as ")==false){
			aliasModeSupportati.add(" as ");
		}
		// itero su tutti gli alias (lastIndexOf utile per i casi di min/max/avg/sum timestamp field dove vengono usato anche altri alias internamente)
		for (Iterator<?> iterator = aliasModeSupportati.iterator(); iterator.hasNext();) {
			String alias = (String) iterator.next();
			int aliasLength = alias.length(); //4;
			// Inoltre se vi sono alias, devono essere eliminati i field davanti agli alias e lasciati solo gli alias:
			// Es. SCORRETTO :  SELECT id as IDAlias from (select id as IDAlias...)
			// Es. CORRETTO :  SELECT IDAlias from (select id as IDAlias...)
			String fLowerCase = field.toLowerCase();
			//indexOf = fLowerCase.lastIndexOf(" as ");
			indexOf = fLowerCase.lastIndexOf(alias);
			if( (indexOf!=-1) && ((indexOf+aliasLength)<field.length()) ){
				field = field.substring(indexOf+aliasLength);
				field = field.trim();
				break;
			}
		}
		return field;	

	}






	// SELECT FIELDS NORMALI

	/**
	 * Aggiunge un field alla select, se non sono presenti field, viene utilizzato '*'
	 * es: SELECT nomeField FROM ....
	 * 
	 * @param nomeField Nome del Field
	 */
	@Override
	public ISQLQueryObject addSelectField(String nomeField) throws SQLQueryObjectException{
		return this.addSelectField(null,nomeField);
	}
	/**
	 * Aggiunge un field alla select, se non sono presenti field, viene utilizzato '*'
	 * es: SELECT nomeTabella.nomeField FROM ....
	 * 
	 * @param nomeTabella Nome della tabella su cui reperire il field
	 * @param nomeField Nome del Field
	 */
	@Override
	public ISQLQueryObject addSelectField(String nomeTabella,String nomeField) throws SQLQueryObjectException{
		return this._engine_addSelectField(nomeTabella,nomeField,null,true,false);
	}
	/**
	 * Aggiunge un field alla select, se non sono presenti field, viene utilizzato '*'
	 * es: SELECT nomeField FROM ....
	 * 
	 * @param nomeField Nome del Field
	 */
	@Override
	public ISQLQueryObject addSelectAliasField(String nomeField,String alias) throws SQLQueryObjectException{
		return this._engine_addSelectField(null,nomeField,alias,true,false);
	}
	/**
	 * Aggiunge un field alla select, se non sono presenti field, viene utilizzato '*'
	 * es: SELECT nomeTabella.nomeField FROM ....
	 * 
	 * @param nomeTabella Nome della tabella su cui reperire il field
	 * @param nomeField Nome del Field
	 */
	@Override
	public ISQLQueryObject addSelectAliasField(String nomeTabella,String nomeField,String alias) throws SQLQueryObjectException{
		return this._engine_addSelectField(nomeTabella,nomeField,alias,true,false);
	}




	// SELECT FIELDS COALESCE

	/**
	 * Aggiunge un field alla select definendolo tramite la funzione coalesce
	 * es: SELECT coalesce(nomeField, 'VALORE') as alias FROM ....
	 * 
	 * @param nomeField Nome del Field
	 * @param alias Alias
	 * @param valore Valore utilizzato in caso il campo sia null
	 */
	@Override
	public ISQLQueryObject addSelectCoalesceField(String nomeField,String alias,String valore) throws SQLQueryObjectException{
		return _engine_addSelectField(null, nomeField, alias, true, "coalesce(",",'"+escapeStringValue(valore)+"')",true);
	}

	/**
	 * Aggiunge un field alla select definendolo tramite la funzione coalesce
	 * es: SELECT coalesce(nomeTabella.nomeField, 'VALORE') as alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param nomeField Nome del Field
	 * @param alias Alias
	 * @param valore Valore utilizzato in caso il campo sia null
	 */
	@Override
	public ISQLQueryObject addSelectCoalesceField(String aliasTabella,String nomeField,String alias,String valore) throws SQLQueryObjectException{
		if(this.tableAlias.contains(aliasTabella)==false){
			throw new SQLQueryObjectException("L'alias indicato non corrisponde ad un alias effettivo associato ad una tabella");
		}
		return _engine_addSelectField(aliasTabella, nomeField, alias, true, "coalesce(",",'"+escapeStringValue(valore)+"')",true);
	}





	// SELECT FIELDS COUNTS

	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>fieldCount</var>
	 * es: SELECT count(*) as alias FROM ....
	 * 
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectCountField(String alias) throws SQLQueryObjectException{
		String fieldSQL = "count(*)";
		if(alias != null){
			//fieldSQL = fieldSQL + " as "+alias;
			fieldSQL = fieldSQL + getDefaultAliasFieldKeyword() + alias;
		}
		this._engine_addSelectField(null,fieldSQL,null,false,true);
		this.fieldNames.add(alias);
		this.fieldNameIsFunction.put(alias, true);
		return this;
	}

	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>fieldCount</var>
	 * es: SELECT count(fieldCount) as alias FROM ....
	 * 
	 * @param fieldCount Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectCountField(String fieldCount,String alias) throws SQLQueryObjectException{
		if(fieldCount==null)
			fieldCount = "*";
		String fieldSQL = "count("+fieldCount+")";
		if(alias != null){
			//fieldSQL = fieldSQL + " as "+alias;
			fieldSQL = fieldSQL + getDefaultAliasFieldKeyword() + alias;
		}
		this._engine_addSelectField(null,fieldSQL,null,false,true);
		this.fieldNames.add(alias);
		this.fieldNameIsFunction.put(alias, true);
		return this;
	}

	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>fieldCount</var>
	 * es: SELECT count(DISTINCT fieldCount) as alias FROM ....
	 * 
	 * @param fieldCount Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectCountField(String fieldCount,String alias,boolean distinct) throws SQLQueryObjectException{
		if(fieldCount==null && distinct)
			throw new SQLQueryObjectException("Non e' possibile utilizzare DISTINCT senza specificare un fieldCount");
		if(distinct)
			addSelectCountField("DISTINCT "+fieldCount,alias);
		else
			addSelectCountField(fieldCount,alias);
		return this;
	}

	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>fieldCount</var>
	 * es: SELECT count(nomeTabella.fieldCount) as alias FROM ....
	 * 
	 * @param aliasTabella Nome della tabella su cui reperire il field
	 * @param fieldCount Alias del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectCountField(String aliasTabella,String fieldCount,String alias) throws SQLQueryObjectException{
		if(this.tableAlias.contains(aliasTabella)==false){
			throw new SQLQueryObjectException("L'alias indicato non corrisponde ad un alias effettivo associato ad una tabella");
		}
		this.addSelectCountField(aliasTabella+"."+fieldCount, alias);
		return this;
	}

	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>fieldCount</var>
	 * es: SELECT count(DISTINCT nomeTabella.fieldCount) as alias FROM ....
	 *
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param fieldCount Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectCountField(String aliasTabella,String fieldCount,String alias,boolean distinct) throws SQLQueryObjectException{
		if(this.tableAlias.contains(aliasTabella)==false){
			throw new SQLQueryObjectException("L'alias indicato non corrisponde ad un alias effettivo associato ad una tabella");
		}
		this.addSelectCountField(aliasTabella+"."+fieldCount, alias, distinct);
		return this;
	}





	// SELECT FIELDS AVG

	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>fieldAvg</var>
	 * es: SELECT count(fieldAvg) as alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectAvgField(String field,String alias) throws SQLQueryObjectException{
		if(field==null)
			throw new SQLQueryObjectException("field avg non puo' essere null");
		_engine_addSelectField(null, field, alias, true, "avg(",")",true);
		return this;
	}

	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>fieldAvg</var>
	 * es: SELECT avg(nomeTabella.fieldAvg) as alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectAvgField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		if(field==null)
			throw new SQLQueryObjectException("field avg non puo' essere null");
		if(aliasTabella==null)
			throw new SQLQueryObjectException("nomeTabella non puo' essere null");
		if(this.tableAlias.contains(aliasTabella)==false){
			throw new SQLQueryObjectException("L'alias indicato non corrisponde ad un alias effettivo associato ad una tabella");
		}
		_engine_addSelectField(aliasTabella, field, alias, true, "avg(",")",true);
		return this;
	}

	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>fieldAvg</var> (di tipo Timestamp)
	 * es: SELECT avg(fieldAvg) as alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public abstract ISQLQueryObject addSelectAvgTimestampField(String field,String alias) throws SQLQueryObjectException;

	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>fieldAvg</var> (di tipo Timestamp)
	 * es: SELECT avg(nomeTabella.fieldAvg) as alias FROM ....
	 *
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectAvgTimestampField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		if(this.tableAlias.contains(aliasTabella)==false){
			throw new SQLQueryObjectException("L'alias indicato non corrisponde ad un alias effettivo associato ad una tabella");
		}
		addSelectAvgTimestampField(aliasTabella+"."+field, alias);
		return this;
	}







	// SELECT FIELDS MAX

	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var> 
	 * es: SELECT max(field) as alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectMaxField(String field,String alias) throws SQLQueryObjectException{
		if(field==null)
			throw new SQLQueryObjectException("field non puo' essere null");
		_engine_addSelectField(null, field, alias, true, "max(",")",true);
		return this;
	}

	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var> 
	 * es: SELECT max(nomeTabella.field) as alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias alias
	 */
	@Override
	public ISQLQueryObject addSelectMaxField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		if(field==null)
			throw new SQLQueryObjectException("field non puo' essere null");
		if(aliasTabella==null)
			throw new SQLQueryObjectException("nomeTabella non puo' essere null");
		if(this.tableAlias.contains(aliasTabella)==false){
			throw new SQLQueryObjectException("L'alias indicato non corrisponde ad un alias effettivo associato ad una tabella");
		}
		_engine_addSelectField(aliasTabella, field, alias, true, "max(",")",true);
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
	public abstract ISQLQueryObject addSelectMaxTimestampField(String field,String alias) throws SQLQueryObjectException;

	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT max(nomeTabella.field) as alias FROM ....
	 * 
	 * @param aliasTabella alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectMaxTimestampField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		if(this.tableAlias.contains(aliasTabella)==false){
			throw new SQLQueryObjectException("L'alias indicato non corrisponde ad un alias effettivo associato ad una tabella");
		}
		addSelectMaxTimestampField(aliasTabella+"."+field, alias);
		return this;
	}











	// SELECT FIELDS MIN

	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var>
	 * es: SELECT min(field) as alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectMinField(String field,String alias) throws SQLQueryObjectException{
		if(field==null)
			throw new SQLQueryObjectException("field non puo' essere null");
		_engine_addSelectField(null, field, alias, true, "min(",")",true);
		return this;
	}

	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var>
	 * es: SELECT min(nomeTabella.field) as alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias
	 */
	@Override
	public ISQLQueryObject addSelectMinField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		if(field==null)
			throw new SQLQueryObjectException("field non puo' essere null");
		if(aliasTabella==null)
			throw new SQLQueryObjectException("nomeTabella non puo' essere null");
		if(this.tableAlias.contains(aliasTabella)==false){
			throw new SQLQueryObjectException("L'alias indicato non corrisponde ad un alias effettivo associato ad una tabella");
		}
		_engine_addSelectField(aliasTabella, field, alias, true, "min(",")",true);
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
	public abstract ISQLQueryObject addSelectMinTimestampField(String field,String alias) throws SQLQueryObjectException;

	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT min(nomeTabella.field) as alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias
	 */
	@Override
	public ISQLQueryObject addSelectMinTimestampField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		if(this.tableAlias.contains(aliasTabella)==false){
			throw new SQLQueryObjectException("L'alias indicato non corrisponde ad un alias effettivo associato ad una tabella");
		}
		addSelectMinTimestampField(aliasTabella+"."+field, alias);
		return this;
	}










	// SELECT FIELDS SUM

	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var>
	 * es: SELECT sum(field) as alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectSumField(String field,String alias) throws SQLQueryObjectException{
		if(field==null)
			throw new SQLQueryObjectException("field non puo' essere null");
		_engine_addSelectField(null, field, alias, true, "sum(",")",true);
		return this;
	}

	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var>
	 * es: SELECT sum(nomeTabella.field) as alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias
	 */
	@Override
	public ISQLQueryObject addSelectSumField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		if(field==null)
			throw new SQLQueryObjectException("field non puo' essere null");
		if(aliasTabella==null)
			throw new SQLQueryObjectException("nomeTabella non puo' essere null");
		if(this.tableAlias.contains(aliasTabella)==false){
			throw new SQLQueryObjectException("L'alias indicato non corrisponde ad un alias effettivo associato ad una tabella");
		}
		_engine_addSelectField(aliasTabella, field, alias, true, "sum(",")",true);
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
	public abstract ISQLQueryObject addSelectSumTimestampField(String field,String alias) throws SQLQueryObjectException;

	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT sum(nomeTabella.field) as alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias
	 */
	@Override
	public ISQLQueryObject addSelectSumTimestampField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		if(this.tableAlias.contains(aliasTabella)==false){
			throw new SQLQueryObjectException("L'alias indicato non corrisponde ad un alias effettivo associato ad una tabella");
		}
		addSelectSumTimestampField(aliasTabella+"."+field, alias);
		return this;
	}









	// SELECT FORCE INDEX

	/**
	 * Aggiunge l'istruzione SQL per forzare l'utilizzo dell'indice indicato nel parametro nella lettura della tabella indicata
	 * es: SELECT '/*+ index(nomeTabella indexName) *'  FROM ....
	 * 
	 * @param nomeTabella Nome della tabella su cui forzare l'indice
	 * @param indexName Nome dell'indice
	 */
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







	// SET DISTINCTS IN CIMA ALLA SELECT

	/**
	 * Aggiunge un field alla select, se non sono presenti field, viene utilizzato '*'
	 * es: SELECT DISTINCT nomeField,nomeFiled2.... FROM ....
	 * 
	 * @param value Indicazione sul distinct
	 */
	@Override
	public void setSelectDistinct(boolean value) throws SQLQueryObjectException{
		this.distinct = value;

	}
	public boolean isSelectDistinct() throws SQLQueryObjectException{
		if(this.distinct){
			if(this.fields.size()<=0){
				throw new SQLQueryObjectException("Per usare la select distinct devono essere indicati dei select field");
			}
		}
		return this.distinct;
	}






	// FIELDS/TABLE NAME

	/**
	 * Ritorna i nomi dei fields impostati (se e' stato utilizzato un alias ritorna il valore dell'alias)
	 * 
	 * @return i nomi dei fields impostati (se e' stato utilizzato un alias ritorna il valore dell'alias)
	 * @throws SQLQueryObjectException
	 */
	@Override
	public Vector<String> getFieldsName() throws SQLQueryObjectException{
		if(this.fieldNames==null || this.fieldNames.size()==0){
			throw new SQLQueryObjectException("Nessun field impostato");
		}

		return this.fieldNames;
	}
	
	/**
	 * Indicazione se e' il nome rappresenta una funzione
	 * 
	 * @return Indicazione se e' il nome rappresenta una funzione
	 * @throws SQLQueryObjectException
	 */
	@Override
	public Boolean isFieldNameForFunction(String fieldName) throws SQLQueryObjectException{
		if(this.fieldNames==null || this.fieldNames.size()==0){
			throw new SQLQueryObjectException("Nessun field impostato");
		}
		if(this.fieldNameIsFunction.containsKey(fieldName)==false){
			throw new SQLQueryObjectException("Field ["+fieldName+"] non presente (se durante la definizione del field e' stato usato un 'alias' utilizzarlo come parametro di questo metodo)");
		}
		return this.fieldNameIsFunction.get(fieldName);
	}

	/**
	 * Ritorna i nomi dei fields impostati (se e' stato utilizzato un alias ritorna comunque il nome della colonna)
	 * 
	 * @return nomi dei fields impostati (se e' stato utilizzato un alias ritorna comunque il nome della colonna)
	 * @throws SQLQueryObjectException
	 */
	//@Override
	public Vector<String> getFields() throws SQLQueryObjectException{
		if(this.fields==null || this.fields.size()==0){
			throw new SQLQueryObjectException("Nessun field impostato");
		}

		return this.fields;
	}

	/**
	 * Ritorna i nomi delle tabelle impostate (se e' stato utilizzato un alias ritorna il valore dell'alias)
	 * 
	 * @return nomi delle tabelle impostate (se e' stato utilizzato un alias ritorna il valore dell'alias)
	 * @throws SQLQueryObjectException
	 */
	@Override
	public Vector<String> getTablesName() throws SQLQueryObjectException{
		if(this.tableNames==null || this.tableNames.size()==0){
			throw new SQLQueryObjectException("Nessuna tabella impostata");
		}

		return this.tableNames;
	}

	/**
	 * Ritorna i nomi delle tabelle impostate (se e' stato utilizzato un alias ritorna comunque il nome della tabella)
	 * 
	 * @return nomi delle tabelle impostate (se e' stato utilizzato un alias ritorna comunque il nome della tabella)
	 * @throws SQLQueryObjectException
	 */
	//@Override
	public Vector<String> getTables() throws SQLQueryObjectException{
		if(this.tables==null || this.tables.size()==0){
			throw new SQLQueryObjectException("Nessuna tabella impostata");
		}

		return this.tables;
	}



	
	
	
	
	/// FIELDS UNIX FUNCTIONS
	
	/**
	 * Ritorna la conversione in UnixTimestamp della Colonna
	 * 
	 * @param column colonna da convertire in UnixTimestamp
	 * @return conversione in UnixTimestamp della Colonna
	 */
	@Override
	public abstract String getUnixTimestampConversion(String column);
	
	/**
	 * Ritorna l'intervallo in unixTimestamp tra le due colonne fornite
	 * 
	 * @param columnMax colonna con intervallo temporale maggiore
	 * @param columnMin colonna con intervallo temporale minore
	 * @return ritorna l'intervallo in unixTimestamp tra le due colonne fornite
	 */
	@Override
	public abstract String getDiffUnixTimestamp(String columnMax,String columnMin);

	
	
	
	
	
	// FIELDS/TABLE ALIAS
	
	// GESTIONE ALIAS 'as'
	// Non tutti i database supportano l'alias 'as', ad esempio Oracle genera un errore se viene usato l'alias con le tabelle.
	/**
	 * Ritorna l'alias di default utilizzabile per un field
	 * 
	 * @return Ritorna l'alias di default utilizzabile per un field
	 */
	@Override
	public String getDefaultAliasFieldKeyword(){
		return this.getSupportedAliasesField().get(0);
	}

	/**
	 * Ritorna l'alias di default utilizzabile per una tabella
	 * 
	 * @return Ritorna l'alias di default utilizzabile per una tabella
	 */
	@Override
	public String getDefaultAliasTableKeyword(){
		return this.getSupportedAliasesTable().get(0);
	}

	/**
	 * Ritorna gli aliases utilizzabili per un field
	 * 
	 * @return Ritorna gli aliases utilizzabili per un field
	 */
	@Override
	public List<String> getSupportedAliasesField(){
		List<String> lista = new ArrayList<String>();
		lista.add(" as ");
		lista.add(" ");
		return lista;
	}
	
	/**
	 * Ritorna gli aliases utilizzabili per una tabella
	 * 
	 * @return Ritorna gli aliases utilizzabili per una tabella
	 */
	@Override
	public List<String> getSupportedAliasesTable(){
		List<String> lista = new ArrayList<String>();
		lista.add(" as ");
		lista.add(" ");
		return lista;
	}
	



	
	
	

	// Engine

	/**
	 * Aggiunge un field alla select, se non sono presenti field, viene utilizzato '*'
	 * es: SELECT nomeTabella.nomeField FROM ....
	 * 
	 * @param nomeTabella Nome della tabella su cui reperire il field
	 * @param nomeField Nome del Field
	 */
	protected ISQLQueryObject _engine_addSelectField(String nomeTabella,String nomeField,String alias,boolean addFieldName,boolean isFunction) throws SQLQueryObjectException{
		return _engine_addSelectField(nomeTabella, nomeField, alias, addFieldName, null, null,isFunction);
	}
	protected ISQLQueryObject _engine_addSelectField(String nomeTabella,String nomeField,String alias,boolean addFieldName,
			String functionPrefix,String functionSuffix,boolean isFunction) throws SQLQueryObjectException{
		if(nomeField==null || "".equals(nomeField))
			throw new SQLQueryObjectException("Field is null or empty string");
		if(alias!=null){
			if(this.fields.contains("*")){
				throw new SQLQueryObjectException("Alias "+alias+" del field "+nomeField+" non utilizzabile tra i select fields. La presenza del select field '*' non permette di inserirne altri");
			}
			if(this.fieldNames.contains(alias))
				throw new SQLQueryObjectAlreadyExistsException("Alias "+alias+" gia inserito tra i select fields");
		}else{
			if("*".equals(nomeField)==false){
				if(this.fields.contains("*")){
					throw new SQLQueryObjectException("Field "+nomeField+" non utilizzabile tra i select fields. La presenza del select field '*' non permette di inserirne altri");
				}
			}
			if(this.fields.contains(nomeField))
				throw new SQLQueryObjectAlreadyExistsException("Field "+nomeField+" gia inserito tra i select fields");
		}
		if(nomeTabella!=null && (!"".equals(nomeTabella))){

			if(this.tableNames.contains(nomeTabella)==false)
				throw new SQLQueryObjectException("Tabella "+nomeTabella+" non esiste tra le tabelle su cui effettuare la ricerca (se nella addFromTable e' stato utilizzato l'alias per la tabella, utilizzarlo anche come parametro in questo metodo)");
			String nomeTabellaConField = nomeTabella+"."+nomeField;
			if(functionPrefix!=null){
				nomeTabellaConField = functionPrefix + nomeTabellaConField;
			}
			if(functionSuffix!=null){
				nomeTabellaConField = nomeTabellaConField  + functionSuffix ;
			}
			if(alias!=null){
				//this.fields.add(nomeTabellaConField+" as "+alias);
				this.fields.add(nomeTabellaConField+this.getDefaultAliasFieldKeyword()+alias);
				this.alias.put(alias, nomeTabellaConField);
			}else{
				this.fields.add(nomeTabellaConField);
			}
		}else{
			String tmp = new String(nomeField);
			if(functionPrefix!=null){
				tmp = functionPrefix + tmp;
			}
			if(functionSuffix!=null){
				tmp = tmp + functionSuffix ;
			}
			if(alias!=null){
				//this.fields.add(tmp+" as "+alias);
				this.fields.add(tmp+this.getDefaultAliasFieldKeyword()+alias);
				this.alias.put(alias, tmp);
			}else{
				this.fields.add(tmp);
			}
		}
		if(addFieldName){
			if(alias!=null){
				this.fieldNames.add(alias);
				this.fieldNameIsFunction.put(alias, isFunction);
			}else{
				this.fieldNames.add(nomeField);
				this.fieldNameIsFunction.put(nomeField, isFunction);
			}
		}
		return this;
	}







	// FROM

	/**
	 * Aggiunge una tabella di ricerca del from
	 * es: SELECT * from tabella as tabella
	 * 
	 * @param tabella
	 */
	@Override
	public ISQLQueryObject addFromTable(String tabella) throws SQLQueryObjectException{
		if(tabella==null || "".equals(tabella))
			throw new SQLQueryObjectException("Tabella is null or empty string");
		if(this.tableNames.contains(tabella))
			throw new SQLQueryObjectAlreadyExistsException("Tabella "+tabella+" gia' esistente tra le tabella su cui effettuare la ricerca");
		this.tableNames.add(tabella);
		this.tables.add(tabella);
		return this;
	}

	/**
	 * Aggiunge una tabella di ricerca del from
	 * es: SELECT * from tabella as alias
	 * 
	 * @param tabella Tabella
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addFromTable(String tabella,String alias) throws SQLQueryObjectException{
		if(tabella==null || "".equals(tabella))
			throw new SQLQueryObjectException("Tabella is null or empty string");
		if(alias==null || "".equals(alias))
			throw new SQLQueryObjectException("Alias tabella is null or empty string");
		if(this.tableNames.contains(alias))
			throw new SQLQueryObjectAlreadyExistsException("Tabella "+tabella+" gia' esistente tra le tabella su cui effettuare la ricerca");
		this.tableNames.add(alias);
		//this.tables.add(tabella+" as "+alias);
		this.tables.add(tabella+this.getDefaultAliasTableKeyword()+alias);
		this.tableAlias.add(alias);
		return this;
	}




	// WHERE

	/**
	 * Aggiunge una condizione di ricerca (e associa un operatore logico, se le condizioni di ricerca sono piu' di una)
	 * es: SELECT * from tabella WHERE (condition)
	 * 
	 * @param condition
	 */
	@Override
	public ISQLQueryObject addWhereCondition(String condition) throws SQLQueryObjectException{
		if(condition==null || "".equals(condition))
			throw new SQLQueryObjectException("Where Condition is null or empty string");
		String buildCondition = "( "+condition+" )";
		if( (buildCondition.indexOf("?")==-1) && this.conditions.contains(buildCondition))
			throw new SQLQueryObjectException("Where Condition "+condition+" gia' esistente tra le condizioni di where");
		this.conditions.add(buildCondition);
		return this;
	}
	/**
	 * Aggiunge una condizione di ricerca (e associa un operatore logico, se le condizioni di ricerca sono piu' di una)
	 * Imposta come operatore logico di una successiva condizione (se esiste) l'AND se true, l'OR se false
	 * es: SELECT * from tabella WHERE ((condition1) andLogicOperator (condition2) etc.....)
	 * 
	 * @param andLogicOperator
	 * @param conditions
	 */
	@Override
	public ISQLQueryObject addWhereCondition(boolean andLogicOperator,String... conditions) throws SQLQueryObjectException{
		this.addWhereCondition(andLogicOperator, false, conditions);
		return this;
	}


	/**
	 * Aggiunge una condizione di ricerca (e associa un operatore logico, se le condizioni di ricerca sono piu' di una)
	 * Imposta come operatore logico di una successiva condizione (se esiste) l'AND se true, l'OR se false
	 * es: SELECT * from tabella WHERE ( [NOT] ( (condition1) andLogicOperator (condition2) etc.....) )
	 * Se il parametro not is true, aggiunge il not davanti alle condizioni
	 * 
	 * @param andLogicOperator
	 * @param not
	 * @param conditions
	 */
	@Override
	public ISQLQueryObject addWhereCondition(boolean andLogicOperator,boolean not,String... conditions) throws SQLQueryObjectException{
		if(conditions==null || conditions.length<=0)
			throw new SQLQueryObjectException("Where Conditions non esistenti");
		StringBuffer buildCondition = new StringBuffer();

		if(not){
			buildCondition.append("( NOT ");
		}

		buildCondition.append("( ");
		for(int i=0; i<conditions.length; i++){
			if(i>0){
				if(andLogicOperator){
					buildCondition.append(" AND ");
				}else{
					buildCondition.append(" OR ");
				}
			}
			if(conditions[i]==null || "".equals(conditions[i]))
				throw new SQLQueryObjectException("Where Condition["+i+"] is null or empty string");
			buildCondition.append("(");
			buildCondition.append(conditions[i]);
			buildCondition.append(")");
		}
		buildCondition.append(" )");

		if(not){
			buildCondition.append(")");
		}

		if((buildCondition.indexOf("?")==-1) && this.conditions.contains(buildCondition.toString()))
			throw new SQLQueryObjectException("Where Condition "+buildCondition.toString()+" gia' esistente tra le condizioni di where");
		this.conditions.add(buildCondition.toString());
		return this;
	}


	/**
	 * Aggiunge una condizione di ricerca (e associa un operatore logico, se le condizioni di ricerca sono piu' di una)
	 * es: SELECT * from tabella WHERE (field is null)
	 * 
	 * @param field Field da verificare
	 */
	@Override
	public ISQLQueryObject addWhereIsNullCondition(String field) throws SQLQueryObjectException{
		if(field==null || "".equals(field)){
			throw new SQLQueryObjectException("IsNullCondition field non puo' essere null");
		}
		this.addWhereCondition(field+" is null");
		return this;
	}

	/**
	 * Aggiunge una condizione di ricerca (e associa un operatore logico, se le condizioni di ricerca sono piu' di una)
	 * es: SELECT * from tabella WHERE (field is not null)
	 * 
	 * @param field Field da verificare
	 */
	@Override
	public ISQLQueryObject addWhereIsNotNullCondition(String field) throws SQLQueryObjectException{
		if(field==null || "".equals(field)){
			throw new SQLQueryObjectException("IsNullCondition field non puo' essere null");
		}
		this.addWhereCondition(field+" is not null");
		return this;
	}


	/**
	 * Aggiunge una condizione di ricerca in un insieme di valori
	 * esempio concreto:    SELECT * from tabella WHERE id IN (a,b,c,d);
	 * Se viene indicato stringValueType a true, ogni valore viene trattoto come stringa e nella sql prodotto viene aggiunto il carattere ' all'inizio e alla fine.
	 * 
	 * @param field Field su cui effettuare la select
	 * @param valore insieme di valori su cui effettuare il controllo
	 * @throws SQLQueryObjectException
	 */
	@Override
	public ISQLQueryObject addWhereINCondition(String field,boolean stringValueType,String ... valore) throws SQLQueryObjectException{
		if(valore==null || valore.length<1){
			throw new SQLQueryObjectException("Deve essere fornito almeno un valore");
		}
		StringBuffer bf = new StringBuffer(field);
		bf.append(" IN ( ");
		for (int i = 0; i < valore.length; i++) {
			if(i>0){
				bf.append(",");
			}
			if(stringValueType){
				bf.append("'");
				bf.append(escapeStringValue(valore[i]));
				bf.append("'");
			}
			else{
				bf.append(valore[i]);
			}
		}
		bf.append(")");
		this.addWhereCondition(bf.toString());
		return this;
	}

	/**
	 * Aggiunge una condizione di ricerca tra due valori
	 * esempio concreto:    SELECT * from tabella WHERE id BEETWEEN a AND b
	 * Se viene indicato stringValueType a true, ogni valore viene trattato come stringa e nella sql prodotto viene aggiunto il carattere ' all'inizio e alla fine.
	 * 
	 * @param field Field su cui effettuare la select
	 * @param stringValueType indica se i valori devono essere trattati come stringhe o meno
	 * @param leftValue Valore dell'intervallo sinistro
	 * @param rightValue Valore dell'intervallo destro
	 * @return SQLQueryObjectException
	 * @throws SQLQueryObjectException
	 */
	@Override
	public ISQLQueryObject addWhereBetweenCondition(String field,boolean stringValueType,String leftValue,String rightValue) throws SQLQueryObjectException{
		if(leftValue==null){
			throw new SQLQueryObjectException("Deve essere fornito un valore per l'intervallo sinistro");
		}
		if(rightValue==null){
			throw new SQLQueryObjectException("Deve essere fornito un valore per l'intervallo destro");
		}
		StringBuffer bf = new StringBuffer(field);
		bf.append(" BETWEEN ");
		if(stringValueType){
			bf.append("'");
			bf.append(escapeStringValue(leftValue));
			bf.append("'");
		}
		else{
			bf.append(leftValue);
		}
		bf.append(" AND ");
		if(stringValueType){
			bf.append("'");
			bf.append(escapeStringValue(rightValue));
			bf.append("'");
		}
		else{
			bf.append(rightValue);
		}
		this.addWhereCondition(bf.toString());
		return this;
	}

	private String createWhereLikeCondition(String columnName,String searchPattern,boolean escape) throws SQLQueryObjectException{
		if(columnName==null || "".equals(columnName))
			throw new SQLQueryObjectException("Where Condition column name is null or empty string");
		if(searchPattern==null || "".equals(searchPattern))
			throw new SQLQueryObjectException("Where Condition searchPattern is null or empty string");
		if(searchPattern.length()>1){
			if(searchPattern.startsWith("'"))
				searchPattern = searchPattern.substring(1);
			if(searchPattern.endsWith("'"))
				searchPattern = searchPattern.substring(0,searchPattern.length()-1);
		}
		String buildCondition = null;
		if(escape){
			EscapeSQLPattern escapePattern = escapePatternValue(searchPattern);
			String escapeClausole = "";
			if(escapePattern.isUseEscapeClausole()){
				escapeClausole = " ESCAPE '"+escapePattern.getEscapeClausole()+"'";
			}
			buildCondition = "( "+columnName+" LIKE '"+escapePattern.getEscapeValue()+"'"+escapeClausole+" )";
		}
		else{
			buildCondition = "( "+columnName+" LIKE '"+searchPattern+"' )";
		}
		return buildCondition;
	}

	/**
	 * Aggiunge una condizione di ricerca
	 * es: SELECT * from tabella WHERE (columnName LIKE 'searchPattern')
	 * 
	 * @param columnName
	 * @param searchPattern
	 */
	@Override
	public ISQLQueryObject addWhereLikeCondition(String columnName,String searchPattern) throws SQLQueryObjectException{
		return addWhereLikeCondition(columnName, searchPattern,true);
	}
	@Override
	public ISQLQueryObject addWhereLikeCondition(String columnName,String searchPattern,boolean escape) throws SQLQueryObjectException{

		String buildCondition = this.createWhereLikeCondition(columnName, searchPattern,escape);
		if((buildCondition.indexOf("?")==-1) && this.conditions.contains(buildCondition.toString()))
			throw new SQLQueryObjectException("Where Condition "+buildCondition.toString()+" gia' esistente tra le condizioni di where");
		this.conditions.add(buildCondition);
		return this;
	}

	/**
	 * Ritorna una condizione di ricerca
	 * es: "columnName LIKE 'searchPattern'"
	 * 
	 * @param columnName
	 * @param searchPattern
	 */
	@Override
	public String getWhereLikeCondition(String columnName,String searchPattern) throws SQLQueryObjectException{
		return this.createWhereLikeCondition(columnName, searchPattern,true);
	}
	@Override
	public String getWhereLikeCondition(String columnName,String searchPattern,boolean escape) throws SQLQueryObjectException{
		return this.createWhereLikeCondition(columnName, searchPattern,escape);
	}

	private String createWhereLikeCondition(String columnName,String searchPattern,boolean escape, boolean contains, boolean caseInsensitive) throws SQLQueryObjectException{
		if(columnName==null || "".equals(columnName))
			throw new SQLQueryObjectException("Where Condition column name is null or empty string");
		if(searchPattern==null || "".equals(searchPattern))
			throw new SQLQueryObjectException("Where Condition searchPattern is null or empty string");
		if(searchPattern.length()>1){
			if(searchPattern.startsWith("'"))
				searchPattern = searchPattern.substring(1);
			if(searchPattern.endsWith("'"))
				searchPattern = searchPattern.substring(0,searchPattern.length()-1);
		}
		String buildCondition = null;
		if(escape){
			EscapeSQLPattern escapePattern = escapePatternValue(searchPattern);
			String escapeClausole = "";
			if(escapePattern.isUseEscapeClausole()){
				escapeClausole = " ESCAPE '"+escapePattern.getEscapeClausole()+"'";
			}
			if(contains && caseInsensitive)
				buildCondition ="( lower("+columnName+") LIKE '%"+escapePattern.getEscapeValue().toLowerCase()+"%'"+escapeClausole+" )";
			else if(contains)
				buildCondition ="( "+columnName+" LIKE '%"+escapePattern.getEscapeValue()+"%'"+escapeClausole+" )";
			else if(caseInsensitive)
				buildCondition ="( lower("+columnName+") LIKE '"+escapePattern.getEscapeValue().toLowerCase()+"'"+escapeClausole+" )";
			else
				buildCondition ="( "+columnName+" LIKE '"+escapePattern.getEscapeValue()+"'"+escapeClausole+" )";
		}
		else{
			if(contains && caseInsensitive)
				buildCondition ="( lower("+columnName+") LIKE '%"+searchPattern.toLowerCase()+"%' )";
			else if(contains)
				buildCondition ="( "+columnName+" LIKE '%"+searchPattern+"%' )";
			else if(caseInsensitive)
				buildCondition ="( lower("+columnName+") LIKE '"+searchPattern.toLowerCase()+"' )";
			else
				buildCondition ="( "+columnName+" LIKE '"+searchPattern+"' )";
		}

		return buildCondition;
	}

	/**
	 * Aggiunge una condizione di ricerca
	 * es: SELECT * from tabella WHERE (columnName LIKE 'searchPattern')
	 * 
	 * @param columnName
	 * @param searchPattern
	 * @param contains true se la parola deve essere contenuta nella colonna, false se deve essere esattamente la colonna
	 * @param caseInsensitive
	 */
	@Override
	public ISQLQueryObject addWhereLikeCondition(String columnName,String searchPattern, boolean contains, boolean caseInsensitive) throws SQLQueryObjectException{
		return addWhereLikeCondition(columnName,searchPattern,true,contains,caseInsensitive);
	}
	@Override
	public ISQLQueryObject addWhereLikeCondition(String columnName,String searchPattern, boolean escape, boolean contains, boolean caseInsensitive) throws SQLQueryObjectException{

		String buildCondition = this.createWhereLikeCondition(columnName, searchPattern, escape, contains, caseInsensitive);
		if((buildCondition.indexOf("?")==-1) && this.conditions.contains(buildCondition.toString()))
			throw new SQLQueryObjectException("Where Condition "+buildCondition.toString()+" gia' esistente tra le condizioni di where");
		this.conditions.add(buildCondition);
		return this;
	}

	/**
	 * Ritorna una condizione di ricerca
	 * es: "columnName LIKE 'searchPattern'"
	 * 
	 * @param columnName
	 * @param searchPattern
	 * @param contains true se la parola deve essere contenuta nella colonna, false se deve essere esattamente la colonna
	 * @param caseInsensitive
	 */
	@Override
	public String getWhereLikeCondition(String columnName,String searchPattern, boolean contains, boolean caseInsensitive) throws SQLQueryObjectException{
		return this.createWhereLikeCondition(columnName, searchPattern, true, contains, caseInsensitive);
	}
	@Override
	public String getWhereLikeCondition(String columnName,String searchPattern,boolean escape, boolean contains, boolean caseInsensitive) throws SQLQueryObjectException{
		return this.createWhereLikeCondition(columnName, searchPattern, escape, contains, caseInsensitive);
	}

	private String createWhereExistsCondition(boolean notExists,ISQLQueryObject sqlQueryObject)throws SQLQueryObjectException{
		if(sqlQueryObject==null)
			throw new SQLQueryObjectException("ISQLQueryObject, su cui viene effettuato il controllo di exists, non fornito");

		if(sqlQueryObject instanceof SQLQueryObjectCore){
			// http://stackoverflow.com/questions/5119190/oracle-sql-order-by-in-subquery-problems
			SQLQueryObjectCore core = (SQLQueryObjectCore) sqlQueryObject;
			if(core.orderBy!=null && core.orderBy.size()>0){
				throw new SQLQueryObjectException("ISQLQueryObject, su cui viene effettuato il controllo di exists, non deve possedere condizioni di order by");
			}
			if(core.offset>=0){
				throw new SQLQueryObjectException("ISQLQueryObject, su cui viene effettuato il controllo di exists, non deve possedere la condizione di OFFSET");
			}
		}

		StringBuffer bf = new StringBuffer();
		if(notExists)
			bf.append("NOT ");
		bf.append("EXISTS (");
		bf.append(sqlQueryObject.createSQLQuery());
		bf.append(" )");
		return bf.toString();
	}

	/**
	 * Aggiunge una condizione di ricerca con EXISTS
	 * La query su cui viene effettuato il controllo di exists e' definito dal parametro sqlQueryObject
	 * es. SELECT * from tabella WHERE [NOT] EXISTS ( sqlQueryObject.createSQLQuery() )
	 * 
	 * @param notExists Indicazione se applicare la negazione all'exists
	 * @param sqlQueryObject query su cui viene effettuato il controllo di exists, la query viene prodotta attraverso il metodo createSQLQuery()
	 * @throws SQLQueryObjectException
	 */
	@Override
	public ISQLQueryObject addWhereExistsCondition(boolean notExists,ISQLQueryObject sqlQueryObject) throws SQLQueryObjectException{
		this.addWhereCondition(this.createWhereExistsCondition(notExists, sqlQueryObject));
		return this;
	}

	/**
	 * Ritorna una condizione di ricerca con EXISTS
	 * La query su cui viene effettuato il controllo di exists e' definito dal parametro sqlQueryObject
	 * es. SELECT * from tabella WHERE [NOT] EXISTS ( sqlQueryObject.createSQLQuery() )
	 * 
	 * @param notExists Indicazione se applicare la negazione all'exists
	 * @param sqlQueryObject query su cui viene effettuato il controllo di exists, la query viene prodotta attraverso il metodo createSQLQuery()
	 * @throws SQLQueryObjectException
	 */
	@Override
	public String getWhereExistsCondition(boolean notExists,ISQLQueryObject sqlQueryObject) throws SQLQueryObjectException{
		return this.createWhereExistsCondition(notExists, sqlQueryObject);
	}


	/**
	 * Aggiunge una condizione di ricerca con sotto-select
	 * La query su cui viene effettuato la ricerca della sotto-select e' definita dal parametro sqlQueryObject
	 * es. SELECT * from tabella WHERE [NOT] field=( sqlQueryObject.createSQLQuery() )
	 * esempio concreto:    SELECT * from tabella WHERE id=(select rif from riferimenti);
	 * 
	 * @param notExists Indicazione se applicare la negazione 
	 * @param field Field su cui effettuare la select
	 * @param sqlQueryObject query su cui viene effettuato la ricerca, la query viene prodotta attraverso il metodo createSQLQuery()
	 * @throws SQLQueryObjectException
	 */
	@Override
	public ISQLQueryObject addWhereSelectSQLCondition(boolean notExists,String field,ISQLQueryObject sqlQueryObject) throws SQLQueryObjectException{
		this.addWhereCondition(this.createWhereSQLConditionCondition(notExists,false,field,sqlQueryObject));
		return this;
	}

	/**
	 * Aggiunge una condizione di ricerca con sotto-select
	 * La query su cui viene effettuato la ricerca della sotto-select e' definita dal parametro sqlQueryObject
	 * es. SELECT * from tabella WHERE [NOT] field IN ( sqlQueryObject.createSQLQuery() )
	 * esempio concreto:    SELECT * from tabella WHERE id IN (select rif from riferimenti);
	 * 
	 * @param notExists Indicazione se applicare la negazione 
	 * @param field Field su cui effettuare la select
	 * @param sqlQueryObject query su cui viene effettuato la ricerca, la query viene prodotta attraverso il metodo createSQLQuery()
	 * @throws SQLQueryObjectException
	 */
	@Override
	public ISQLQueryObject addWhereINSelectSQLCondition(boolean notExists,String field,ISQLQueryObject sqlQueryObject) throws SQLQueryObjectException{
		this.addWhereCondition(this.createWhereSQLConditionCondition(notExists,true,field,sqlQueryObject));
		return this;
	}

	private String createWhereSQLConditionCondition(boolean notExists, boolean in,String field,ISQLQueryObject sqlQueryObject)throws SQLQueryObjectException{

		if(sqlQueryObject==null)
			throw new SQLQueryObjectException("ISQLQueryObject, su cui viene costruita la ricerca, non fornito");
		if(field==null)
			throw new SQLQueryObjectException("Field non fornito");

		if(sqlQueryObject instanceof SQLQueryObjectCore){
			// http://stackoverflow.com/questions/5119190/oracle-sql-order-by-in-subquery-problems
			SQLQueryObjectCore core = (SQLQueryObjectCore) sqlQueryObject;
			if(core.orderBy!=null && core.orderBy.size()>0){
				throw new SQLQueryObjectException("ISQLQueryObject, su cui viene effettuato il controllo di exists, non deve possedere condizioni di order by");
			}
			if(core.offset>=0){
				throw new SQLQueryObjectException("ISQLQueryObject, su cui viene effettuato il controllo di exists, non deve possedere la condizione di OFFSET");
			}
			if(!in){
				if(core.limit>1){
					throw new SQLQueryObjectException("ISQLQueryObject, su cui viene effettuato il controllo di exists, non deve possedere la condizione di LIMIT o al massimo puo' essere definita con valore '1')");
				}
			}
		}

		StringBuffer bf = new StringBuffer();
		if(notExists)
			bf.append("NOT ");
		bf.append(field);
		if(in)
			bf.append(" IN ");
		else
			bf.append(" = ");
		bf.append(" (");
		bf.append(sqlQueryObject.createSQLQuery());
		bf.append(" )");
		return bf.toString();
	}

	/**
	 * Imposta come operatore logico tra due o piu' condizioni esistenti l'AND se true, l'OR se false
	 * 
	 * @param andLogicOperator
	 */
	@Override
	public void setANDLogicOperator(boolean andLogicOperator) throws SQLQueryObjectException{
		this.andLogicOperator = andLogicOperator;
	} // se false viene utilizzato OR tra le condizioni

	/**
	 * Imposta il NOT prima delle condizioni NOT ( conditions )
	 * 
	 * @param not
	 */
	@Override
	public void setNOTBeforeConditions(boolean not) throws SQLQueryObjectException{
		this.notBeforeConditions = not;
	} // se true viene utilizzato il NOT davanti alle condizioni






	// ESCAPE
	/**
	 * Effettua l'escape di un valore di tipo stringa
	 * 
	 * @param value valore su cui effettuare l'escape
	 * @return valore su cui e' stato effettuato l'escape
	 * @throws SQLQueryObjectException
	 */
	@Override
	public String escapeStringValue(String value) throws SQLQueryObjectException{

		return SQLQueryObjectCore.getEscapeStringValue(value);

	}
	public static String getEscapeStringValue(String value) throws SQLQueryObjectException{

		if(value==null){
			throw new SQLQueryObjectException("Valore non fornito per escape");
		}
		// converte ' in ''
		int index = value.indexOf('\'');
		if(index>=0){
			StringBuffer str =  new StringBuffer();
			char[] v = value.toCharArray();
			for(int i=0; i<v.length; i++){
				if(v[i]=='\''){
					str.append('\'');
				}
				str.append(v[i]);
			}
			return str.toString();
		}

		return value;
	}

	/**
	 * Effettua l'escape di un pattern
	 * 
	 * @param pattern pattern su cui effettuare l'escape
	 * @return pattern su cui e' stato effettuato l'escape
	 * @throws SQLQueryObjectException
	 */
	@Override
	public EscapeSQLPattern escapePatternValue(String pattern)
			throws SQLQueryObjectException {

		EscapeSQLPattern escapeSqlPattern = new EscapeSQLPattern();

		if(pattern==null){
			throw new SQLQueryObjectException("Valore non fornito per escape");
		}
		// converte il carattere '  in ''
		// fa l'escape dei caratteri speciali

		EscapeSQLConfiguration escapeConfig = this.getEscapeSQLConfiguration();

		char[] special_char = escapeConfig.getSpecial_char();

		StringBuffer str =  new StringBuffer();
		char[] v = pattern.toCharArray();
		boolean escape=false;
		for(int i=0; i<v.length; i++){
			if(v[i]=='\''){
				str.append('\'');
			} else {
				for(int j = 0 ; (j< special_char.length) && !escape;j++) {
					if(v[i]==special_char[j]){
						str.append(escapeConfig.getEscapeClausole());
						escape=true;
						if(escapeConfig.isUseEscapeClausole()){
							escapeSqlPattern.setUseEscapeClausole(true);
							escapeSqlPattern.setEscapeClausole(escapeConfig.getEscapeClausole());
						}
					}
				}
			}
			str.append(v[i]);
			if(escape) {
				escape=false;
			}
		}

		String returnStr = str.toString();	
		escapeSqlPattern.setEscapeValue(returnStr);
		return escapeSqlPattern;

	}
	protected abstract EscapeSQLConfiguration getEscapeSQLConfiguration();





	// GROUP BY

	/**
	 * Aggiunge una condizione di GroupBy per i field con il nome sottostante.
	 * I field devono essere precedentemente stati inseriti come SelectField
	 * 
	 * @param groupByNomeField Nome del field di raggruppamento
	 */
	@Override
	public ISQLQueryObject addGroupBy(String groupByNomeField) throws SQLQueryObjectException{
		if(groupByNomeField==null || "".equals(groupByNomeField))
			throw new SQLQueryObjectException("GroupBy Condition is null or empty string");
		/*
		 * Non e' sempre vero
		 * if(this.fields.contains(groupByNomeField)==false){
		 *	throw new SQLQueryObjectException("GroupBy Condition field non e' registrati tra i select field");
		}*/

		/*if(this.tableAlias.size()>0){
			throw new SQLQueryObjectException("Non e' possibile utilizzare alias nelle tabelle se poi si vuole usufruire della condizione GroupBy");
		} IL PROBLEMA DOVREBBE ESSERE STATO RISOLTO CON LA GESTIONE DEGLI ALIAS
		 */
		if(this.alias.containsKey(groupByNomeField)){
			this.groupBy.add(this.alias.get(groupByNomeField));
		}
		else{
			this.groupBy.add(groupByNomeField);	
		}
		return this;
	}

	public Vector<String> getGroupByConditions() throws SQLQueryObjectException{
		if(this.groupBy!=null && this.groupBy.size()>0){
			if(this.fields.size()<=0){
				throw new SQLQueryObjectException("Non e' possibile utilizzare condizioni di group by se non sono stati indicati select field");
			}
		}
		return this.groupBy;
	}


	// ORDER BY

	/**
	 * Aggiunge una condizione di OrderBy per i field con il nome sottostante.
	 * I field devono essere precedentemente stati inseriti come SelectField
	 * 
	 * @param orderByNomeField Nome del field di ordinamento
	 */
	@Override
	public ISQLQueryObject addOrderBy(String orderByNomeField) throws SQLQueryObjectException{
		if(orderByNomeField==null || "".equals(orderByNomeField))
			throw new SQLQueryObjectException("OrderBy Condition is null or empty string");
		/*
		 * Non e' sempre vero
		 * if(this.fields.contains(orderByNomeField)==false){
		 *	throw new SQLQueryObjectException("OrderBy Condition field non e' registrati tra i select field");
		}*/
		this.orderBy.add(orderByNomeField);
		return this;
	}

	/**
	 * Aggiunge una condizione di OrderBy per i field con il nome sottostante.
	 * I field devono essere precedentemente stati inseriti come SelectField
	 * 
	 * @param orderByNomeField Nome del field di ordinamento
	 * @param asc Imposta la stringa di ordinamento (Crescente:true/Decrescente:false)
	 */
	@Override
	public ISQLQueryObject addOrderBy(String orderByNomeField, boolean asc) throws SQLQueryObjectException{
		this.addOrderBy(orderByNomeField);
		this.orderBySortType.put(orderByNomeField, asc);
		return this;
	}

	/**
	 * Imposta la stringa di ordinamento (Crescente:true/Decrescente:false)
	 */
	@Override
	public void setSortType(boolean sort) throws SQLQueryObjectException{
		if(this.orderBy.isEmpty())
			throw new SQLQueryObjectException("OrderBy Conditions non definite");
		this.sortTypeAsc = sort;
	}




	// LIMIT E OFFSET

	/**
	 * Aggiunge un limite ai risultati ritornati
	 * 
	 * @param limit limite dei risultati ritornati
	 */
	@Override
	public void setLimit(int limit) throws SQLQueryObjectException{
		this.limit = limit;
	}

	public int getLimit() {
		return this.limit;
	}

	/**
	 * Aggiunge un offset per i risultati ritornati
	 * 
	 * @param offset offset per i risultati ritornati
	 */
	@Override
	public void setOffset(int offset) throws SQLQueryObjectException{
		this.offset = offset;
	}

	public int getOffset() {
		return this.offset;
	}

	
	// SELECT FOR UPDATE
	
	public boolean isSelectForUpdate() {
		return this.selectForUpdate;
	}
	
	@Override
	public void setSelectForUpdate(boolean selectForUpdate) throws SQLQueryObjectException {
		this.selectForUpdate = selectForUpdate;
	}
	
	protected void checkSelectForUpdate(boolean update,boolean delete, boolean union) throws SQLQueryObjectException{
		if(this.selectForUpdate){
			String tipo = null;
			if(update){
				tipo = "UPDATE";
			}
			else if(delete){
				tipo = "DELETE";
			}
			else if(union){
				tipo = "UNION";
			}
			else if(this.getGroupByConditions().size()>0){
				tipo = "GROUP BY";
			}
			else if(this.isSelectDistinct()){
				tipo = "DISTINCT";
			}
			else if(this.limit>=0){
				tipo = "LIMIT";
			}
			else if(this.offset>=0){
				tipo = "OFFSET";
			}
			if(tipo!=null)
				throw new SQLQueryObjectException("Utilizzo dell'opzione 'FOR UPDATE' non permesso con il comando "+tipo);
		}
	}
	
	

	// GENERAZIONE SQL

	/**
	 * Crea una SQL Query con i dati dell'oggetto
	 * 
	 * @return SQL Query
	 */
	@Override
	public abstract String createSQLQuery() throws SQLQueryObjectException;

	@Override
	public String toString(){
		try{
			return this.createSQLQuery();
		}catch(Exception e){
			return "Oggetto non corretto: "+e.getMessage();
		}
	}

	protected void checkUnionField(boolean count,ISQLQueryObject... sqlQueryObject)throws SQLQueryObjectException{

		if(count){
			if(this.orderBy!=null && this.orderBy.size()>0){
				throw new SQLQueryObjectException("Non e' possibile usare order by in una count");
			}
			if(this.limit>0){
				throw new SQLQueryObjectException("Non e' possibile usare limit in una count");
			}
			if(this.offset>=0){
				throw new SQLQueryObjectException("Non e' possibile usare offset in una count");
			}
		}

		if(this.offset>=0){
			if(this.fields.size()<=0)
				throw new SQLQueryObjectException("Non e' possibile usare offset se non e' stato indicato alcun field nella select piu' esterna della union");
		}
		if(this.limit>0){
			if(this.fields.size()<=0)
				throw new SQLQueryObjectException("Non e' possibile usare limit se non e' stato indicato alcun field nella select piu' esterna della union");
		}

		if(this.distinct){
			throw new SQLQueryObjectException("Non e' possibile usare distinct nella select piu' esterna della union (utilizza il parametro boolean unionAll)");
		}

		if(sqlQueryObject==null || sqlQueryObject.length<=0){
			throw new SQLQueryObjectException("Parametro is null");
		}
		if(sqlQueryObject.length==1){
			throw new SQLQueryObjectException("Parametro contiene un solo sqlQueryObject (minimo 2)");
		}

		// Check presenza fields
		for(int i=0;i<sqlQueryObject.length;i++){
			ISQLQueryObject sqlQueryObjectDaVerificare = sqlQueryObject[i];
			Vector<String> nomiFieldSqlQueryObjectDaVerificare = sqlQueryObjectDaVerificare.getFieldsName();
			if(nomiFieldSqlQueryObjectDaVerificare==null || nomiFieldSqlQueryObjectDaVerificare.size()==0){
				throw new SQLQueryObjectException("La select numero "+(i+1)+" non possiede fields?");
			}
		}

		// Check nomi fields			
		//		for(int i=0;i<sqlQueryObject.length;i++){
		//			System.out.println("CHECK ["+i+"] ...");
		//			Vector<String> nomiFieldSqlQueryObjectDaVerificare = sqlQueryObject[i].getFieldsName();
		//			for (String string : nomiFieldSqlQueryObjectDaVerificare) {
		//				System.out.println("\t-"+string);
		//			}
		//		}
		for(int i=0;i<sqlQueryObject.length;i++){

			Vector<String> nomiFieldSqlQueryObjectDaVerificare = sqlQueryObject[i].getFieldsName();
			String [] nomi = nomiFieldSqlQueryObjectDaVerificare.toArray(new String[1]);

			for(int indiceField =0; indiceField<nomi.length;indiceField++){

				String fieldDaVerificare = nomi[indiceField];

				for(int altriSqlObject=0;altriSqlObject<sqlQueryObject.length;altriSqlObject++){

					if(altriSqlObject==i){
						// e' l'oggetto in questione
						continue;
					}

					if( (sqlQueryObject[altriSqlObject].getFieldsName().contains(fieldDaVerificare) == false) &&
							(sqlQueryObject[altriSqlObject].getFieldsName().contains("*") == false) ){
						throw new SQLQueryObjectException("Field ["+fieldDaVerificare+"] trovato nella select numero "+(i+1) +" non presente nella select numero "+(altriSqlObject+1) +" (Se sono campi diversi usare lo stesso alias)");
					}
				}
			}
		}

		// Check presenza order by DEVE essere accompagnata da un offset in modo che venga generata la condizione di order by su Oracle/SLQServer per le condizioni
		for(int i=0;i<sqlQueryObject.length;i++){
			SQLQueryObjectCore sqlQueryObjectDaVerificare = (SQLQueryObjectCore) sqlQueryObject[i];
			if(sqlQueryObjectDaVerificare.orderBy!=null && sqlQueryObjectDaVerificare.orderBy.size()>0){
				if(sqlQueryObjectDaVerificare.offset<0){
					//throw new SQLQueryObjectException("La select numero "+(i+1)+" per poter essere utilizzata nella UNION, siccome presenta condizioni di ordinamento, deve specificare anche l'offset");
					sqlQueryObjectDaVerificare.setOffset(0); // Se viene messo il limit, l'offset cmq parte da 0
				}
			}
		}
		
		// Check Select For Update
		if(this.selectForUpdate){
			throw new SQLQueryObjectException("Non è possibile abilitare il comando 'selectForUpdate' con la UNION");
		}
	}



	/* ---------------- UPDATE ------------------ */

	/**
	 * Definisce la tabella su cui deve essere effettuato l'update
	 * es: UPDATE table set ...
	 * 
	 * @param nomeTabella Nome della tabella
	 */
	@Override
	public ISQLQueryObject addUpdateTable(String nomeTabella) throws SQLQueryObjectException{
		if(nomeTabella==null || "".equals(nomeTabella))
			throw new SQLQueryObjectException("Nome tabella is null or empty string");
		this.updateTable = nomeTabella;
		return this;
	}

	/**
	 * Aggiunge un field per l'update
	 * es: UPDATE table set nomeField=valueField WHERE ...
	 * 
	 * @param nomeField Nome del Field
	 */
	@Override
	public ISQLQueryObject addUpdateField(String nomeField,String valueField) throws SQLQueryObjectException{
		if(nomeField==null || "".equals(nomeField))
			throw new SQLQueryObjectException("Field name is null or empty string");
		if(valueField==null)
			throw new SQLQueryObjectException("Field value is null");
		if(this.updateFieldsName.contains(nomeField)){
			throw new SQLQueryObjectException("Field name "+nomeField+" gia inserito tra gli update fields");
		}else{
			this.updateFieldsName.add(nomeField);
			this.updateFieldsValue.add(valueField);
		}
		return this;
	}
	
	@Override
	public String createSQLUpdate() throws SQLQueryObjectException{
		
		if(this.updateTable==null)
			throw new SQLQueryObjectException("Nome Tabella per l'aggiornamento non definito");
		if(this.updateFieldsName.size()<=0)
			throw new SQLQueryObjectException("Nessuna coppia nome/valore da aggiornare presente");
		if(this.updateFieldsName.size()!= this.updateFieldsValue.size()){
			throw new SQLQueryObjectException("FieldsName.size["+this.updateFieldsName.size()+"] <> FieldsValue.size["+this.updateFieldsValue.size()+"]");
		}
		
		if(this.forceSelectForUpdateDisabledForNotQueryMethod){
			
			// Per permettere di usare l'oggetto sqlQueryObject con più comandi
			boolean oldValueSelectForUpdate = this.selectForUpdate;
			try{
				this.selectForUpdate = false;
				return this._createSQLUpdate();
			}finally{
				this.selectForUpdate = oldValueSelectForUpdate;
			}
			
		}
		else{
			return this._createSQLUpdate();
		}
	}
	
	protected abstract String _createSQLUpdate() throws SQLQueryObjectException;





	/* ---------------- INSERT ------------------ */

	/**
	 * Definisce la tabella su cui deve essere effettuato l'insert
	 * es: INSERT INTO table (XX) VALUES (VV) 
	 * 
	 * @param nomeTabella Nome della tabella
	 */
	@Override
	public ISQLQueryObject addInsertTable(String nomeTabella) throws SQLQueryObjectException{
		if(nomeTabella==null || "".equals(nomeTabella))
			throw new SQLQueryObjectException("Nome tabella is null or empty string");
		this.insertTable = nomeTabella;
		return this;
	}

	/**
	 * Aggiunge un field per la insert
	 * es: INSERT INTO table (nomeField) VALUES (valueField)
	 * 
	 * @param nomeField Nome del Field
	 */
	@Override
	public ISQLQueryObject addInsertField(String nomeField,String valueField) throws SQLQueryObjectException{
		if(nomeField==null || "".equals(nomeField))
			throw new SQLQueryObjectException("Field name is null or empty string");
		if(valueField==null)
			throw new SQLQueryObjectException("Field value is null");
		if(this.insertFieldsName.contains(nomeField)){
			throw new SQLQueryObjectException("Field name "+nomeField+" gia inserito tra gli insert fields");
		}else{
			this.insertFieldsName.add(nomeField);
			this.insertFieldsValue.add(valueField);
		}
		return this;
	}

	/**
	 * Crea una SQL per una operazione di Insert con i dati dell'oggetto
	 * 
	 * @return SQL per una operazione di Insert
	 */
	@Override
	public String createSQLInsert() throws SQLQueryObjectException{
		
		if(this.insertTable==null)
			throw new SQLQueryObjectException("Nome Tabella per l'inserimento non definito");
		if(this.insertFieldsName.size()<=0)
			throw new SQLQueryObjectException("Nessuna coppia nome/valore da inserire presente");
		if(this.insertFieldsName.size()!= this.insertFieldsValue.size()){
			throw new SQLQueryObjectException("FieldsName.size <> FieldsValue.size");
		}

		if(this.forceSelectForUpdateDisabledForNotQueryMethod){
		
			// Per permettere di usare l'oggetto sqlQueryObject con più comandi
			boolean oldValueSelectForUpdate = this.selectForUpdate;
			try{
				this.selectForUpdate = false;
				return this._createSQLInsert();
			}finally{
				this.selectForUpdate = oldValueSelectForUpdate;
			}
			
		}
		else{
			return this._createSQLInsert();
		}
		

	}
	private String _createSQLInsert() throws SQLQueryObjectException{
		StringBuffer bf = new StringBuffer();
		bf.append("INSERT INTO ");
		bf.append(this.insertTable);
		bf.append(" (");
		for(int i=0; i<this.insertFieldsName.size(); i++){
			if(i>0)
				bf.append(",");
			bf.append(this.insertFieldsName.get(i));
		}
		bf.append(") VALUES (");
		for(int i=0; i<this.insertFieldsValue.size(); i++){
			if(i>0)
				bf.append(",");
			bf.append(this.insertFieldsValue.get(i));
		}
		bf.append(")");
		return bf.toString();
	}





	/* ---------------- DELETE ------------------ */

	/**
	 * Aggiunge una tabella di ricerca del from
	 * es: DELETE from tabella
	 * 
	 * @param tabella
	 */
	@Override
	public ISQLQueryObject addDeleteTable(String tabella) throws SQLQueryObjectException{
		checkDeleteTable(tabella);
		this.addFromTable(tabella);
		return this;
	}
	
	@Override
	public String createSQLDelete() throws SQLQueryObjectException {

		// Table dove effettuare la ricerca 'FromTable'
		if(this.tables.size()==0){
			throw new SQLQueryObjectException("Non e' possibile creare un comando di delete senza aver definito le tabelle su cui apportare l'eliminazione dei dati");
		}else{
			Iterator<String> it = this.tables.iterator();
			while(it.hasNext()){
				String table = it.next();
				checkDeleteTable(table);
			}
		}

		if(this.forceSelectForUpdateDisabledForNotQueryMethod){
			
			// Per permettere di usare l'oggetto sqlQueryObject con più comandi
			boolean oldValueSelectForUpdate = this.selectForUpdate;
			try{
				this.selectForUpdate = false;
				return this._createSQLDelete();
			}finally{
				this.selectForUpdate = oldValueSelectForUpdate;
			}
			
		}
		else{
			return this._createSQLDelete();
		}
	}
	
	protected abstract String _createSQLDelete() throws SQLQueryObjectException;
	
	private void checkDeleteTable(String tabella) throws SQLQueryObjectException{
		// Controllo quelli standard (da fare per impostare il controllo in ogni classe)
		if(tabella.contains(" as ") || tabella.contains(" ")){
			throw new SQLQueryObjectException("Non e' possibile utilizzare tabelle definite tramite alias in caso di delete");
		}

		List<String> asModeSupportati = this.getSupportedAliasesTable();
		for (Iterator<?> iterator = asModeSupportati.iterator(); iterator.hasNext();) {
			String aliasMode = (String) iterator.next();
			if(tabella.contains(aliasMode)){
				throw new SQLQueryObjectException("Non e' possibile utilizzare tabelle definite tramite alias in caso di delete");
			}
		}
	}
	
	
	
	
	
	/* ---------------- WHERE CONDITIONS ------------------ */
	
	@Override
	public String createSQLConditions() throws SQLQueryObjectException{
		
		if(this.conditions==null)
			throw new SQLQueryObjectException("Condizioni non definite");
		if(this.conditions.size()<=0)
			throw new SQLQueryObjectException("Nessuna condizione presente");
		
		if(this.forceSelectForUpdateDisabledForNotQueryMethod){
			
			// Per permettere di usare l'oggetto sqlQueryObject con più comandi
			boolean oldValueSelectForUpdate = this.selectForUpdate;
			try{
				this.selectForUpdate = false;
				return this._createSQLConditions();
			}finally{
				this.selectForUpdate = oldValueSelectForUpdate;
			}
			
		}
		else{
			return this._createSQLConditions();
		}
	}

	protected abstract String _createSQLConditions() throws SQLQueryObjectException;







	/* ---------------- NEW SQL QUERY OBJECT ------------------ */

	/**
	 * Inizializza un nuovo SQLQueryObject
	 * 
	 * @return SQLQueryObject
	 * 
	 * @throws SQLQueryObjectException
	 */
	@Override
	public ISQLQueryObject newSQLQueryObject() throws SQLQueryObjectException{
		return SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
	}

	/**
	 * Indicazione sul tipo di database
	 * 
	 * @return tipo di database
	 * 
	 * @throws SQLQueryObjectException
	 */
	@Override
	public String getTipoDatabase() throws SQLQueryObjectException{
		return this.tipoDatabase.toString();
	}

	/**
	 * Indicazione sul tipo di database
	 * 
	 * @return tipo di database
	 * 
	 * @throws SQLQueryObjectException
	 */
	@Override
	public TipiDatabase getTipoDatabaseOpenSPCoop2() throws SQLQueryObjectException{
		return this.tipoDatabase;
	}
}
