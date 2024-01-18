/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

	private static final String LA_COLONNA_PREFIX = "La colonna ";
	private static final String TABELLA_PREFIX = "Tabella ";
	private static final String WHERE_CONDITION_PREFIX = "Where Condition ";
	private static final String FIELD_NAME_PREFIX = "Field name ";
	
	private static final String ALIAS_TABELLA_INDICATO_NON_ESISTE = "L'alias indicato non corrisponde ad un alias effettivo associato ad una tabella";
	private static final String NESSUN_FIELD_IMPOSTATO = "Nessun field impostato";
	private static final String GIA_ESISTENTE_TRA_CONDIZIONI_WHERE = " gia' esistente tra le condizioni di where";
	private static final String FIELD_NAME_IS_NULL_OR_EMPTY = "Field name is null or empty string";
	protected static final String CONDIZIONI_ORDER_BY_RICHESTE = "Condizioni di OrderBy richieste";
	protected static final String TABELLA_RICERCA_FROM_NON_DEFINITA = "Tabella di ricerca (... FROM Table ...) non definita";
	
	private static final String NOME_TABELLA_DEVE_ESSERE_DIVERSO_NULL = "nomeTabella non puo' essere null";
	protected static final String FIELD_DEVE_ESSERE_DIVERSO_NULL = "field non puo' essere null";
	private static final String IS_NULL_CONDITION_DEVE_ESSERE_DIVERSO_NULL = "IsNullCondition field non puo' essere null";
	
	private static final String LOWER_PREFIX = "( lower(";
	private static final String ESCAPE_SEPARATOR = " ESCAPE ";
	private static final String LIKE_SEPARATOR = " LIKE ";
	
	protected static final String FROM_SEPARATOR = " FROM ";
	protected static final String FROM_SEPARATOR_APERTURA = " FROM ( ";
	protected static final String WHERE_SEPARATOR = " WHERE ";
	protected static final String AND_SEPARATOR = " AND ";
	protected static final String OR_SEPARATOR = " OR ";
	protected static final String ASC_SEPARATOR = " ASC ";
	protected static final String DESC_SEPARATOR = " DESC ";
	protected static final String GROUP_BY_SEPARATOR = " GROUP BY ";
	protected static final String ORDER_BY_SEPARATOR = " ORDER BY ";
	protected static final String LIMIT_SEPARATOR = " LIMIT ";
	protected static final String NOT_SEPARATOR_APERTURA = " NOT ( ";
	
	protected static final String SELECT_SEPARATOR_CON_INIZIO_APERTURA = " ( SELECT ";
	
	protected static final String AS_SUBQUERY_SUFFIX = " ) as subquery";
	
	private static SecureRandom rndEngine = null;
	private static synchronized void initRandom() {
		if(rndEngine==null) {
			rndEngine = new SecureRandom();
		}
	}
	protected static java.util.Random getRandom() {
		if(rndEngine==null) {
			initRandom();
		}
		return rndEngine;
	}
	
	/** List di Field esistenti: i nomi dei fields impostati (se e' stato utilizzato un alias ritorna comunque il nome della colonna) */
	List<String> fields = new ArrayList<>();
	/** List di NomiField esistenti: i nomi dei fields impostati (se e' stato utilizzato un alias ritorna il valore dell'alias) */
	List<String> fieldNames = new ArrayList<>();
	/** Mapping tra alias e indicazione se e' una function: i nomi dei fields impostati (se e' stato utilizzato un alias ritorna il valore dell'alias) */
	Map<String, Boolean> fieldNameIsFunction = new HashMap<>();
	/** Mapping tra alias (key) e field names (value) (sono presenti i mapping solo per le colonne per cui e' stato definito un alias) */
	Map<String, String> alias = new HashMap<>();
	
	/** List di Tabelle esistenti */
	List<String> tables = new ArrayList<>();
	List<String> tableNames = new ArrayList<>();
	List<String> tableAlias = new ArrayList<>();
	
	/** List di Field esistenti */
	List<String> conditions = new ArrayList<>();
	public int sizeConditions(){
		return this.conditions.size();
	}
	
	/** List di indici forzati */
	List<String> forceIndexTableNames = new ArrayList<>();
	
	/** OperatorLogic */
	boolean andLogicOperator = false;
	
	/** Not */
	boolean notBeforeConditions = false;
	
	/** GroupBy di Field esistenti */
	private List<String> groupBy = new ArrayList<>();
	
	/** OrderBy di Field esistenti */
	List<String> orderBy = new ArrayList<>();
	Map<String, Boolean> orderBySortType = new HashMap<>();
	
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
	/** List di Field per l'update */
	List<String> updateFieldsName = new ArrayList<>();
	List<String> updateFieldsValue = new ArrayList<>();
	/** Tabella per l'update */
	String updateTable = null;

	/* INSERT */
	/** List di Field per l'insert */
	List<String> insertFieldsName = new ArrayList<>();
	List<String> insertFieldsValue = new ArrayList<>();
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
	protected SQLQueryObjectCore(TipiDatabase tipoDatabase){
		this.tipoDatabase = tipoDatabase;
	}
	
	




	// UTILITIES

	protected void precheckBuildQuery() throws SQLQueryObjectException{

		if(!this.precheckQuery){
			return;
		}

		// Check Offset
		if(this.offset>=0 &&
			this.orderBy.isEmpty()){
			throw new SQLQueryObjectException("Condizioni di OrderBy richieste");
		}
		
		// Check GroupBy
		precheckBuildQueryGroupBy();
		
		// Check Select For Update
		if(this.selectForUpdate){
			if(this.groupBy!=null && !this.groupBy.isEmpty()){
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
	private void precheckBuildQueryGroupBy() throws SQLQueryObjectException{
		if(this.groupBy!=null && !this.groupBy.isEmpty()){

			// verifico che tutte le condizioni di order by siano presente anche nei field di group by
			precheckBuildQueryGroupByOrderBy();

			// verifico che tutte le condizioni di group by siano presenti anche nei field
			for (String groupByCheck : this.groupBy) {
				boolean exists = false;
				for (String field : this.fields) {
					if(normalizeField(groupByCheck).equals(normalizeField(field))){
						exists = true;
						break;
					}
				}
				if(!exists){
					throw new SQLQueryObjectException(LA_COLONNA_PREFIX+groupByCheck+" utilizzata nella condizione di GROUP BY deve essere anche selezionato come select field");
				}
			}
		}
	}
	private void precheckBuildQueryGroupByOrderBy() throws SQLQueryObjectException{
		if(!this.orderBy.isEmpty()){
			for (String order : this.orderBy) {
				precheckBuildQueryGroupByOrderBy(order);
			}
		}
	}
	private void precheckBuildQueryGroupByOrderBy(String order) throws SQLQueryObjectException{
		boolean exists = false;
		for (String groupByCheck : this.groupBy) {
			if(normalizeField(groupByCheck).equals(normalizeField(order))){
				exists = true;
				break;
			}
		}
		if(!exists){
			String orderField = normalizeField(order);
			try{
				if(this.isFieldNameForFunction(orderField)==null || (!this.isFieldNameForFunction(orderField).booleanValue())){
					throw new SQLQueryObjectException(LA_COLONNA_PREFIX+order+" utilizzata nella condizione di ORDER BY deve apparire anche in una condizione di GROUP BY");
				}
			}catch(SQLQueryObjectException sqlObject){
				throw new SQLQueryObjectException(LA_COLONNA_PREFIX+order+" utilizzata nella condizione di ORDER BY deve apparire anche in una condizione di GROUP BY",sqlObject);
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
					if(aliases!=null && !aliases.isEmpty()){
						for (String aliasCheck : aliases) {
							if(fieldRicercaAliasa.contains(aliasCheck)){
								split = fieldRicercaAliasa.split(aliasCheck);
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

		// Alcuni valori sono standard dei vendor dei database (es. gestione delle date)
		// Il problema è che se contengono dei '.' o dei caratteri alias rientrano erroneamnete nei punti 2 e 3 dove invece non dovrebbero rientraci.
		// Per questo motivo viene quindi prima richiesto al vendor se effettuare o meno la classica normalizzazione del field in base a tali valori
		// sul field in essere
		if(!this.continueNormalizeField(field)){
			return field;
		}
		
		// 2. Altrimenti devo verificare se vi e' un prefisso di tabella davanti
		// Devono essere eliminati le TABELLE. e lasciare solo il field
		int indexOf = field.indexOf(".");
		if( (indexOf!=-1) && ((indexOf+1)<field.length()) ){
			field = field.substring(indexOf+1);
		}

		// 3. Anche se ho eliminato il prefisso della tabella, potrei avere come field una stringa tipo 'colonna as alias'
		// In tal caso devo tornare solo alias.
		List<String> aliasModeSupportati = new ArrayList<>();
		for (Iterator<?> iterator = this.getSupportedAliasesField().iterator(); iterator.hasNext();) {
			aliasModeSupportati.add((String)iterator.next());
		}
		if(!aliasModeSupportati.contains(" ")){
			aliasModeSupportati.add(" ");
		}
		if(!aliasModeSupportati.contains(" as ")){
			aliasModeSupportati.add(" as ");
		}
		// itero su tutti gli alias (lastIndexOf utile per i casi di min/max/avg/sum timestamp field dove vengono usato anche altri alias internamente)
		for (Iterator<?> iterator = aliasModeSupportati.iterator(); iterator.hasNext();) {
			String aliasCheck = (String) iterator.next();
			int aliasLength = aliasCheck.length(); //4
			// Inoltre se vi sono alias, devono essere eliminati i field davanti agli alias e lasciati solo gli alias:
			// Es. SCORRETTO :  SELECT id as IDAlias from (select id as IDAlias...)
			// Es. CORRETTO :  SELECT IDAlias from (select id as IDAlias...)
			String fLowerCase = field.toLowerCase();
			/**indexOf = fLowerCase.lastIndexOf(" as ");*/
			indexOf = fLowerCase.lastIndexOf(aliasCheck);
			if( (indexOf!=-1) && ((indexOf+aliasLength)<field.length()) ){
				field = field.substring(indexOf+aliasLength);
				field = field.trim();
				break;
			}
		}
		return field;	

	}
	protected boolean continueNormalizeField(String normalizeField){
		
		// Alcuni valori sono standard dei vendor dei database (es. gestione delle date)
		// Il problema è che se contengono dei '.' o dei caratteri alias rientrano erroneamnete nei punti 2 e 3 della normalizzazione
		// Per questo motivo viene quindi prima richiesto al vendor se effettuare o meno la classica normalizzazione del field in base a tali valori
		// sul field in essere
		
		// Vedere quali database sovrascrivono questo metodo
		// Ad esempio SQLServer
		
		if(normalizeField!=null) {
			// nop
		}
		
		return true;
		
	}
	
	protected String getCaseCondition(Case caseValue) throws SQLQueryObjectException {
		if(caseValue==null) {
			throw new SQLQueryObjectException("Field caseValue is null");
		}
		if(caseValue.getValori()==null || caseValue.getValori().isEmpty() || 
				caseValue.getCondizioni()==null || caseValue.getCondizioni().isEmpty()) {
			throw new SQLQueryObjectException("Field caseValue non contiene condizioni");
		}
		if(caseValue.getValori().size()!=caseValue.getCondizioni().size()) {
			throw new SQLQueryObjectException("Field caseValue contiene condizioni con  un numero di valori differenti dalle condizioni di where?");
		}
		if(caseValue.getTipoColonna()==null) {
			throw new SQLQueryObjectException("Field caseValue non contiene il tipo della colonna");
		}
			
		StringBuilder bf = new StringBuilder();
		bf.append("CASE");
		for (int i = 0; i < caseValue.getValori().size(); i++) {
			String valore = caseValue.getValori().get(i);
			String condizione = caseValue.getCondizioni().get(i);
			bf.append(" WHEN ").append(condizione);
			bf.append(" THEN ");
			bf.append(getPrefixCastValue(caseValue.getTipoColonna(),caseValue.getDimensioneColonna()));
			if(caseValue.isStringValueType()){
				bf.append("'");
				bf.append(escapeStringValue(valore));
				bf.append("'");
			}
			else{
				bf.append(valore);
			}
			bf.append(getSuffixCastValue(caseValue.getTipoColonna(),caseValue.getDimensioneColonna()));
		}
		if(caseValue.getValoreDefault()!=null) {
			bf.append(" ELSE ");
			bf.append(getPrefixCastValue(caseValue.getTipoColonna(),caseValue.getDimensioneColonna()));
			if(caseValue.isStringValueType()){
				bf.append("'");
				bf.append(escapeStringValue(caseValue.getValoreDefault()));
				bf.append("'");
			}
			else{
				bf.append(caseValue.getValoreDefault());
			}
			bf.append(getSuffixCastValue(caseValue.getTipoColonna(),caseValue.getDimensioneColonna()));
		}
		bf.append(" END");
					
		return bf.toString();
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
		return this.engineAddSelectField(nomeTabella,nomeField,null,true,false);
	}
	/**
	 * Aggiunge un field alla select, se non sono presenti field, viene utilizzato '*'
	 * es: SELECT nomeField FROM ....
	 * 
	 * @param nomeField Nome del Field
	 */
	@Override
	public ISQLQueryObject addSelectAliasField(String nomeField,String alias) throws SQLQueryObjectException{
		return this.engineAddSelectField(null,nomeField,alias,true,false);
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
		return this.engineAddSelectField(nomeTabella,nomeField,alias,true,false);
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
		return engineAddSelectField(null, nomeField, alias, true, "coalesce(",",'"+escapeStringValue(valore)+"')",true);
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
		if(!this.tableAlias.contains(aliasTabella)){
			throw new SQLQueryObjectException(ALIAS_TABELLA_INDICATO_NON_ESISTE);
		}
		return engineAddSelectField(aliasTabella, nomeField, alias, true, "coalesce(",",'"+escapeStringValue(valore)+"')",true);
	}

	
	
	
	// SELECT FIELDS CASE
	
	/**
	 * Aggiunge un field alla select definendolo tramite la funzione coalesce
	 * es: SELECT coalesce(nomeField, 'VALORE') as alias FROM ....
	 * 
	 * @param caseField Case
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectCaseField(Case caseField, String alias) throws SQLQueryObjectException{
		
		if(alias==null || "".equals(alias))
			throw new SQLQueryObjectException("Alias is null or empty string");
		
		String caseValue = this.getCaseCondition(caseField);
		
		String field = "("+caseValue+")"+this.getDefaultAliasFieldKeyword()+alias;
		this.fields.add(field);
		this.fieldNames.add(alias);
		this.fieldNameIsFunction.put(alias, false);
		
		return this;
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
			/**fieldSQL = fieldSQL + " as "+alias;*/
			fieldSQL = fieldSQL + getDefaultAliasFieldKeyword() + alias;
		}
		this.engineAddSelectField(null,fieldSQL,null,false,true);
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
			/**fieldSQL = fieldSQL + " as "+alias;*/
			fieldSQL = fieldSQL + getDefaultAliasFieldKeyword() + alias;
		}
		this.engineAddSelectField(null,fieldSQL,null,false,true);
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
		if(!this.tableAlias.contains(aliasTabella)){
			throw new SQLQueryObjectException(ALIAS_TABELLA_INDICATO_NON_ESISTE);
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
		if(!this.tableAlias.contains(aliasTabella)){
			throw new SQLQueryObjectException(ALIAS_TABELLA_INDICATO_NON_ESISTE);
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
		engineAddSelectField(null, field, alias, true, "avg(",")",true);
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
			throw new SQLQueryObjectException(NOME_TABELLA_DEVE_ESSERE_DIVERSO_NULL);
		if(!this.tableAlias.contains(aliasTabella)){
			throw new SQLQueryObjectException(ALIAS_TABELLA_INDICATO_NON_ESISTE);
		}
		engineAddSelectField(aliasTabella, field, alias, true, "avg(",")",true);
		return this;
	}

	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>fieldAvg</var> (di tipo Timestamp)
	 * es: SELECT avg(fieldAvg) as alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	/**@Override
	public abstract ISQLQueryObject addSelectAvgTimestampField(String field,String alias) throws SQLQueryObjectException;*/

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
		if(!this.tableAlias.contains(aliasTabella)){
			throw new SQLQueryObjectException(ALIAS_TABELLA_INDICATO_NON_ESISTE);
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
			throw new SQLQueryObjectException(FIELD_DEVE_ESSERE_DIVERSO_NULL);
		engineAddSelectField(null, field, alias, true, "max(",")",true);
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
			throw new SQLQueryObjectException(FIELD_DEVE_ESSERE_DIVERSO_NULL);
		if(aliasTabella==null)
			throw new SQLQueryObjectException(NOME_TABELLA_DEVE_ESSERE_DIVERSO_NULL);
		if(!this.tableAlias.contains(aliasTabella)){
			throw new SQLQueryObjectException(ALIAS_TABELLA_INDICATO_NON_ESISTE);
		}
		engineAddSelectField(aliasTabella, field, alias, true, "max(",")",true);
		return this;
	}

	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT max(field) as alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	/**@Override
	public abstract ISQLQueryObject addSelectMaxTimestampField(String field,String alias) throws SQLQueryObjectException;*/

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
		if(!this.tableAlias.contains(aliasTabella)){
			throw new SQLQueryObjectException(ALIAS_TABELLA_INDICATO_NON_ESISTE);
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
			throw new SQLQueryObjectException(FIELD_DEVE_ESSERE_DIVERSO_NULL);
		engineAddSelectField(null, field, alias, true, "min(",")",true);
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
			throw new SQLQueryObjectException(FIELD_DEVE_ESSERE_DIVERSO_NULL);
		if(aliasTabella==null)
			throw new SQLQueryObjectException(NOME_TABELLA_DEVE_ESSERE_DIVERSO_NULL);
		if(!this.tableAlias.contains(aliasTabella)){
			throw new SQLQueryObjectException(ALIAS_TABELLA_INDICATO_NON_ESISTE);
		}
		engineAddSelectField(aliasTabella, field, alias, true, "min(",")",true);
		return this;
	}

	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT min(field) as alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias
	 */
	/**@Override
	public abstract ISQLQueryObject addSelectMinTimestampField(String field,String alias) throws SQLQueryObjectException;*/

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
		if(!this.tableAlias.contains(aliasTabella)){
			throw new SQLQueryObjectException(ALIAS_TABELLA_INDICATO_NON_ESISTE);
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
			throw new SQLQueryObjectException(FIELD_DEVE_ESSERE_DIVERSO_NULL);
		engineAddSelectField(null, field, alias, true, "sum(",")",true);
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
			throw new SQLQueryObjectException(FIELD_DEVE_ESSERE_DIVERSO_NULL);
		if(aliasTabella==null)
			throw new SQLQueryObjectException(NOME_TABELLA_DEVE_ESSERE_DIVERSO_NULL);
		if(!this.tableAlias.contains(aliasTabella)){
			throw new SQLQueryObjectException(ALIAS_TABELLA_INDICATO_NON_ESISTE);
		}
		engineAddSelectField(aliasTabella, field, alias, true, "sum(",")",true);
		return this;
	}

	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT sum(field) as alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias
	 */
	/**@Override
	public abstract ISQLQueryObject addSelectSumTimestampField(String field,String alias) throws SQLQueryObjectException;*/

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
		if(!this.tableAlias.contains(aliasTabella)){
			throw new SQLQueryObjectException(ALIAS_TABELLA_INDICATO_NON_ESISTE);
		}
		addSelectSumTimestampField(aliasTabella+"."+field, alias);
		return this;
	}

	
	
	
	
	
	// DATE TIME
	
	protected static final String DATE_PART_YEAR = "YEAR";
	protected static final String DATE_PART_MONTH = "MONTH";
	protected static final String DATE_PART_DAY = "DAY";
	
	protected static final String TIME_PART_HOUR = "HOUR";
	protected static final String TIME_PART_MINUTE = "MINUTE";
	protected static final String TIME_PART_SECOND = "SECOND";
	
	protected String getDateTimePart(DateTimePartEnum dateTimePart) throws SQLQueryObjectException {
		switch (dateTimePart) {
		case YEAR:
			return DATE_PART_YEAR;
		case MONTH:
			return DATE_PART_MONTH;
		case DAY:
			return DATE_PART_DAY;
		case HOUR:
			return TIME_PART_HOUR;
		case MINUTE:
			return TIME_PART_MINUTE;
		case SECOND:
			return TIME_PART_SECOND;
		}
		throw new SQLQueryObjectException("DateTimePartEnum '"+dateTimePart+"' unknown");
	}
	public String getExtractDateTimePartFromTimestampFieldPrefix(DateTimePartEnum dateTimePart) throws SQLQueryObjectException {
		if(dateTimePart==null) {
			throw new SQLQueryObjectException("dateTimePart undefined");
		}
		String dateTimePartString = getDateTimePart(dateTimePart);
		return "EXTRACT("+dateTimePartString+FROM_SEPARATOR; 
	}
	public String getExtractDateTimePartFromTimestampFieldSuffix(DateTimePartEnum dateTimePart) throws SQLQueryObjectException {
		if(dateTimePart==null) {
			throw new SQLQueryObjectException("dateTimePart undefined");
		}
		return ")"; 
	}
	
	private ISQLQueryObject addSelectTimestampFieldEngine(String field,String alias, DateTimePartEnum dateTimePart) throws SQLQueryObjectException{
		if(field==null)
			throw new SQLQueryObjectException(FIELD_DEVE_ESSERE_DIVERSO_NULL); 
		engineAddSelectField(null, field, alias, true, 
				getExtractDateTimePartFromTimestampFieldPrefix(dateTimePart),
				getExtractDateTimePartFromTimestampFieldSuffix(dateTimePart),
				true);
		return this;
	}
	private ISQLQueryObject addSelectTimestampFieldEngine(String aliasTabella,String field,String alias, DateTimePartEnum dateTimePart) throws SQLQueryObjectException{
		if(field==null)
			throw new SQLQueryObjectException(FIELD_DEVE_ESSERE_DIVERSO_NULL);
		if(aliasTabella==null)
			throw new SQLQueryObjectException(NOME_TABELLA_DEVE_ESSERE_DIVERSO_NULL);
		if(!this.tableAlias.contains(aliasTabella)){
			throw new SQLQueryObjectException(ALIAS_TABELLA_INDICATO_NON_ESISTE);
		}
		engineAddSelectField(aliasTabella, field, alias, true, 
				getExtractDateTimePartFromTimestampFieldPrefix(dateTimePart),
				getExtractDateTimePartFromTimestampFieldSuffix(dateTimePart),
				true);
		return this;
	}
	
	/**
	 * Aggiunge un field alla select che si occupa di estrarre l'anno dal campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT EXTRACT(YEAR FROM field) as alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectYearTimestampField(String field,String alias) throws SQLQueryObjectException{
		return addSelectTimestampFieldEngine(field, alias, DateTimePartEnum.YEAR);
	}
	
	/**
	 * Aggiunge un field alla select che si occupa di estrarre l'anno dal campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT EXTRACT(YEAR FROM nomeTabella.field) as alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectYearTimestampField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		return addSelectTimestampFieldEngine(aliasTabella, field, alias, DateTimePartEnum.YEAR);
	}
	
	/**
	 * Aggiunge un field alla select che si occupa di estrarre il mese dal campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT EXTRACT(MONTH FROM field) as alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectMonthTimestampField(String field,String alias) throws SQLQueryObjectException{
		return addSelectTimestampFieldEngine(field, alias, DateTimePartEnum.MONTH);
	}
	
	/**
	 * Aggiunge un field alla select che si occupa di estrarre il mese dal campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT EXTRACT(MONTH FROM nomeTabella.field) as alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectMonthTimestampField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		return addSelectTimestampFieldEngine(aliasTabella, field, alias, DateTimePartEnum.MONTH);
	}
	
	/**
	 * Aggiunge un field alla select che si occupa di estrarre il giorno dal campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT EXTRACT(DAY FROM field) as alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectDayTimestampField(String field,String alias) throws SQLQueryObjectException{
		return addSelectTimestampFieldEngine(field, alias, DateTimePartEnum.DAY);
	}
	
	/**
	 * Aggiunge un field alla select che si occupa di estrarre il giorno dal campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT EXTRACT(DAY FROM nomeTabella.field) as alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectDayTimestampField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		return addSelectTimestampFieldEngine(aliasTabella, field, alias, DateTimePartEnum.DAY);
	}
	
	/**
	 * Aggiunge un field alla select che si occupa di estrarre l'ora dal campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT EXTRACT(HOUR FROM field) as alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectHourTimestampField(String field,String alias) throws SQLQueryObjectException{
		return addSelectTimestampFieldEngine(field, alias, DateTimePartEnum.HOUR);
	}
	
	/**
	 * Aggiunge un field alla select che si occupa di estrarre l'ora dal campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT EXTRACT(HOUR FROM nomeTabella.field) as alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectHourTimestampField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		return addSelectTimestampFieldEngine(aliasTabella, field, alias, DateTimePartEnum.HOUR);
	}
	
	/**
	 * Aggiunge un field alla select che si occupa di estrarre i minuti dal campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT EXTRACT(MINUTE FROM field) as alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectMinuteTimestampField(String field,String alias) throws SQLQueryObjectException{
		return addSelectTimestampFieldEngine(field, alias, DateTimePartEnum.MINUTE);
	}
	
	/**
	 * Aggiunge un field alla select che si occupa di estrarre i minuti dal campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT EXTRACT(MINUTE FROM nomeTabella.field) as alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectMinuteTimestampField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		return addSelectTimestampFieldEngine(aliasTabella, field, alias, DateTimePartEnum.MINUTE);
	}
	
	/**
	 * Aggiunge un field alla select che si occupa di estrarre i secondi dal campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT EXTRACT(SECOND FROM field) as alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectSecondTimestampField(String field,String alias) throws SQLQueryObjectException{
		return addSelectTimestampFieldEngine(field, alias, DateTimePartEnum.SECOND);
	}
	
	/**
	 * Aggiunge un field alla select che si occupa di estrarre i secondi dal campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT EXTRACT(SECOND FROM nomeTabella.field) as alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectSecondTimestampField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		return addSelectTimestampFieldEngine(aliasTabella, field, alias, DateTimePartEnum.SECOND);
	}
	
	
	
	
	
	
	private static final String DAY_FORMAT_FULL_DAY_NAME = "DAY";
	private static final String DAY_FORMAT_SHORT_DAY_NAME = "DY";
	private static final String DAY_FORMAT_DAY_OF_YEAR = "DDD";
	private static final String DAY_FORMAT_DAY_OF_WEEK = "D";
	
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
		throw new SQLQueryObjectException("DayFormatEnum '"+dayFormat+"' unknown");
	}
	public String getExtractDayFormatFromTimestampFieldPrefix(DayFormatEnum dayFormat) throws SQLQueryObjectException {
		if(dayFormat==null) {
			throw new SQLQueryObjectException("dayFormat undefined");
		}
		return "TO_CHAR("; 
	}
	public String getExtractDayFormatFromTimestampFieldSuffix(DayFormatEnum dayFormat) throws SQLQueryObjectException {
		if(dayFormat==null) {
			throw new SQLQueryObjectException("dayFormat undefined");
		}
		String dayFormatString = getDayFormat(dayFormat);
		return ", '"+dayFormatString+"')"; 
	}
	
	private ISQLQueryObject addSelectTimestampFieldEngine(String field,String alias, DayFormatEnum dayFormatEnum) throws SQLQueryObjectException{
		if(field==null)
			throw new SQLQueryObjectException(FIELD_DEVE_ESSERE_DIVERSO_NULL); 
		engineAddSelectField(null, field, alias, true, 
				getExtractDayFormatFromTimestampFieldPrefix(dayFormatEnum),
				getExtractDayFormatFromTimestampFieldSuffix(dayFormatEnum),
				true);
		return this;
	}
	private ISQLQueryObject addSelectTimestampFieldEngine(String aliasTabella,String field,String alias, DayFormatEnum dayFormatEnum) throws SQLQueryObjectException{
		if(field==null)
			throw new SQLQueryObjectException(FIELD_DEVE_ESSERE_DIVERSO_NULL);
		if(aliasTabella==null)
			throw new SQLQueryObjectException(NOME_TABELLA_DEVE_ESSERE_DIVERSO_NULL);
		if(!this.tableAlias.contains(aliasTabella)){
			throw new SQLQueryObjectException(ALIAS_TABELLA_INDICATO_NON_ESISTE);
		}
		engineAddSelectField(aliasTabella, field, alias, true, 
				getExtractDayFormatFromTimestampFieldPrefix(dayFormatEnum),
				getExtractDayFormatFromTimestampFieldSuffix(dayFormatEnum),
				true);
		return this;
	}
	
	/**
	 * Aggiunge un field alla select che si occupa di estrarre il giorno, dal campo <var>field</var> (di tipo Timestamp), visualizzandolo nella forma 'full day name'
	 * es: TO_CHAR(field, 'DAY') AS alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectFullDayNameTimestampField(String field,String alias) throws SQLQueryObjectException{
		return addSelectTimestampFieldEngine(field, alias, DayFormatEnum.FULL_DAY_NAME);
	}
	
	/**
	 * Aggiunge un field alla select che si occupa di estrarre il giorno, dal campo <var>field</var> (di tipo Timestamp), visualizzandolo nella forma 'full day name'
	 * es: TO_CHAR(nomeTabella.field, 'DAY') AS alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectFullDayNameTimestampField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		return addSelectTimestampFieldEngine(aliasTabella, field, alias, DayFormatEnum.FULL_DAY_NAME);
	}
	
	/**
	 * Aggiunge un field alla select che si occupa di estrarre il giorno, dal campo <var>field</var> (di tipo Timestamp), visualizzandolo nella forma 'short day name'
	 * es: TO_CHAR(field, 'DY') AS alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectShortDayNameTimestampField(String field,String alias) throws SQLQueryObjectException{
		return addSelectTimestampFieldEngine(field, alias, DayFormatEnum.SHORT_DAY_NAME);
	}
	
	/**
	 * Aggiunge un field alla select che si occupa di estrarre il giorno, dal campo <var>field</var> (di tipo Timestamp), visualizzandolo nella forma 'short day name'
	 * es: TO_CHAR(nomeTabella.field, 'DY') AS alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectShortDayNameTimestampField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		return addSelectTimestampFieldEngine(aliasTabella, field, alias, DayFormatEnum.SHORT_DAY_NAME);
	}
	
	/**
	 * Aggiunge un field alla select che si occupa di estrarre il giorno, dal campo <var>field</var> (di tipo Timestamp), visualizzandolo nella forma 'day of year'
	 * es: TO_CHAR(field, 'DDD') AS alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectDayOfYearTimestampField(String field,String alias) throws SQLQueryObjectException{
		return addSelectTimestampFieldEngine(field, alias, DayFormatEnum.DAY_OF_YEAR);
	}
	
	/**
	 * Aggiunge un field alla select che si occupa di estrarre il giorno, dal campo <var>field</var> (di tipo Timestamp), visualizzandolo nella forma 'day of year'
	 * es: TO_CHAR(nomeTabella.field, 'DDD') AS alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectDayOfYearTimestampField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		return addSelectTimestampFieldEngine(aliasTabella, field, alias, DayFormatEnum.DAY_OF_YEAR);
	}
	
	/**
	 * Aggiunge un field alla select che si occupa di estrarre il giorno, dal campo <var>field</var> (di tipo Timestamp), visualizzandolo nella forma 'day of week'
	 * es: TO_CHAR(field, 'D') AS alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectDayOfWeekTimestampField(String field,String alias) throws SQLQueryObjectException{
		return addSelectTimestampFieldEngine(field, alias, DayFormatEnum.DAY_OF_WEEK);
	}
	
	/**
	 * Aggiunge un field alla select che si occupa di estrarre il giorno, dal campo <var>field</var> (di tipo Timestamp), visualizzandolo nella forma 'day of week'
	 * es: TO_CHAR(nomeTabella.field, 'D') AS alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	@Override
	public ISQLQueryObject addSelectDayOfWeekTimestampField(String aliasTabella,String field,String alias) throws SQLQueryObjectException{
		return addSelectTimestampFieldEngine(aliasTabella, field, alias, DayFormatEnum.DAY_OF_WEEK);
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
		// per adesso implementato solamente per oracle
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
		if(this.distinct &&
			this.fields.isEmpty()){
			throw new SQLQueryObjectException("Per usare la select distinct devono essere indicati dei select field");
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
	public List<String> getFieldsName() throws SQLQueryObjectException{
		if(this.fieldNames==null || this.fieldNames.isEmpty()){
			throw new SQLQueryObjectException(NESSUN_FIELD_IMPOSTATO);
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
		if(this.fieldNames==null || this.fieldNames.isEmpty()){
			throw new SQLQueryObjectException(NESSUN_FIELD_IMPOSTATO);
		}
		if(!this.fieldNameIsFunction.containsKey(fieldName)){
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
	public List<String> getFields() throws SQLQueryObjectException{
		if(this.fields==null || this.fields.isEmpty()){
			throw new SQLQueryObjectException(NESSUN_FIELD_IMPOSTATO);
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
	public List<String> getTablesName() throws SQLQueryObjectException{
		if(this.tableNames==null || this.tableNames.isEmpty()){
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
	public List<String> getTables() throws SQLQueryObjectException{
		if(this.tables==null || this.tables.isEmpty()){
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
	/**@Override
	public abstract String getUnixTimestampConversion(String column);*/
	
	/**
	 * Ritorna l'intervallo in unixTimestamp tra le due colonne fornite
	 * 
	 * @param columnMax colonna con intervallo temporale maggiore
	 * @param columnMin colonna con intervallo temporale minore
	 * @return ritorna l'intervallo in unixTimestamp tra le due colonne fornite
	 */
	/**@Override
	public abstract String getDiffUnixTimestamp(String columnMax,String columnMin);*/

	
	
	
	
	
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
		return getSupportedAliasesEngine();
	}
	
	/**
	 * Ritorna gli aliases utilizzabili per una tabella
	 * 
	 * @return Ritorna gli aliases utilizzabili per una tabella
	 */
	@Override
	public List<String> getSupportedAliasesTable(){
		return getSupportedAliasesEngine();
	}
	
	private List<String> getSupportedAliasesEngine(){
		List<String> lista = new ArrayList<>();
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
	protected ISQLQueryObject engineAddSelectField(String nomeTabella,String nomeField,String alias,boolean addFieldName,boolean isFunction) throws SQLQueryObjectException{
		return engineAddSelectField(nomeTabella, nomeField, alias, addFieldName, null, null,isFunction);
	}
	protected ISQLQueryObject engineAddSelectField(String nomeTabella,String nomeField,String alias,boolean addFieldName,
			String functionPrefix,String functionSuffix,boolean isFunction) throws SQLQueryObjectException{
		if(nomeField==null || "".equals(nomeField))
			throw new SQLQueryObjectException("Field is null or empty string");
		
		checkEngineAddSelectField(nomeField, alias);
		
		if(nomeTabella!=null && (!"".equals(nomeTabella))){
			engineAddSelectField(nomeTabella, nomeField, alias,
					functionPrefix, functionSuffix);
		}else{
			engineAddSelectField(nomeField, alias,
					functionPrefix, functionSuffix);
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
	private void checkEngineAddSelectField(String nomeField,String alias) throws SQLQueryObjectException {
		if(alias!=null){
			if(this.fields.contains("*")){
				throw new SQLQueryObjectException("Alias "+alias+" del field "+nomeField+" non utilizzabile tra i select fields. La presenza del select field '*' non permette di inserirne altri");
			}
			if(this.fieldNames.contains(alias))
				throw new SQLQueryObjectAlreadyExistsException("Alias "+alias+" gia inserito tra i select fields");
		}else{
			if(!"*".equals(nomeField) &&
				this.fields.contains("*")){
				throw new SQLQueryObjectException("Field "+nomeField+" non utilizzabile tra i select fields. La presenza del select field '*' non permette di inserirne altri");
			}
			if(this.fields.contains(nomeField))
				throw new SQLQueryObjectAlreadyExistsException("Field "+nomeField+" gia inserito tra i select fields");
		}
	}
	private void engineAddSelectField(String nomeTabella, String nomeField, String alias,
			String functionPrefix,String functionSuffix) throws SQLQueryObjectException {
		if(!this.tableNames.contains(nomeTabella))
			throw new SQLQueryObjectException(TABELLA_PREFIX+nomeTabella+" non esiste tra le tabelle su cui effettuare la ricerca (se nella addFromTable e' stato utilizzato l'alias per la tabella, utilizzarlo anche come parametro in questo metodo)");
		String nomeTabellaConField = nomeTabella+"."+nomeField;
		if(functionPrefix!=null){
			nomeTabellaConField = functionPrefix + nomeTabellaConField;
		}
		if(functionSuffix!=null){
			nomeTabellaConField = nomeTabellaConField  + functionSuffix ;
		}
		if(alias!=null){
			/**this.fields.add(nomeTabellaConField+" as "+alias);*/
			this.fields.add(nomeTabellaConField+this.getDefaultAliasFieldKeyword()+alias);
			this.alias.put(alias, nomeTabellaConField);
		}else{
			this.fields.add(nomeTabellaConField);
		}
	}
	private void engineAddSelectField(String nomeField, String alias,
			String functionPrefix,String functionSuffix) {
		String tmp = nomeField + "";
		if(functionPrefix!=null){
			tmp = functionPrefix + tmp;
		}
		if(functionSuffix!=null){
			tmp = tmp + functionSuffix ;
		}
		if(alias!=null){
			/**this.fields.add(tmp+" as "+alias);*/
			this.fields.add(tmp+this.getDefaultAliasFieldKeyword()+alias);
			this.alias.put(alias, tmp);
		}else{
			this.fields.add(tmp);
		}
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
			throw new SQLQueryObjectAlreadyExistsException(TABELLA_PREFIX+tabella+" gia' esistente tra le tabella su cui effettuare la ricerca");
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
			throw new SQLQueryObjectAlreadyExistsException(TABELLA_PREFIX+tabella+" gia' esistente tra le tabella su cui effettuare la ricerca");
		this.tableNames.add(alias);
		/**this.tables.add(tabella+" as "+alias);*/
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
			throw new SQLQueryObjectException(WHERE_CONDITION_PREFIX+condition+GIA_ESISTENTE_TRA_CONDIZIONI_WHERE);
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
		StringBuilder buildCondition = new StringBuilder();

		if(not){
			buildCondition.append("( NOT ");
		}

		buildCondition.append("( ");
		addWhereCondition(buildCondition, andLogicOperator, conditions);
		buildCondition.append(" )");

		if(not){
			buildCondition.append(")");
		}

		if((buildCondition.indexOf("?")==-1) && this.conditions.contains(buildCondition.toString()))
			throw new SQLQueryObjectException(WHERE_CONDITION_PREFIX+buildCondition.toString()+GIA_ESISTENTE_TRA_CONDIZIONI_WHERE);
		this.conditions.add(buildCondition.toString());
		return this;
	}
	private void addWhereCondition(StringBuilder buildCondition, boolean andLogicOperator,String... conditions) throws SQLQueryObjectException {
		for(int i=0; i<conditions.length; i++){
			if(i>0){
				if(andLogicOperator){
					buildCondition.append(AND_SEPARATOR);
				}else{
					buildCondition.append(OR_SEPARATOR);
				}
			}
			if(conditions[i]==null || "".equals(conditions[i]))
				throw new SQLQueryObjectException("Where Condition["+i+"] is null or empty string");
			buildCondition.append("(");
			buildCondition.append(conditions[i]);
			buildCondition.append(")");
		}
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
			throw new SQLQueryObjectException(IS_NULL_CONDITION_DEVE_ESSERE_DIVERSO_NULL);
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
			throw new SQLQueryObjectException(IS_NULL_CONDITION_DEVE_ESSERE_DIVERSO_NULL);
		}
		this.addWhereCondition(field+" is not null");
		return this;
	}
	
	/**
	 * Aggiunge una condizione di ricerca (e associa un operatore logico, se le condizioni di ricerca sono piu' di una)
	 * es: SELECT * from tabella WHERE (field = '')
	 * 
	 * @param field Field da verificare
	 */
	@Override
	public ISQLQueryObject addWhereIsEmptyCondition(String field) throws SQLQueryObjectException{
		if(field==null || "".equals(field)){
			throw new SQLQueryObjectException(IS_NULL_CONDITION_DEVE_ESSERE_DIVERSO_NULL);
		}
		this.addWhereCondition(field+" = ''");
		return this;
	}
	
	/**
	 * Aggiunge una condizione di ricerca (e associa un operatore logico, se le condizioni di ricerca sono piu' di una)
	 * es: SELECT * from tabella WHERE (field &lt;&gt; '')
	 * 
	 * @param field Field da verificare
	 */
	@Override
	public ISQLQueryObject addWhereIsNotEmptyCondition(String field) throws SQLQueryObjectException{
		if(field==null || "".equals(field)){
			throw new SQLQueryObjectException(IS_NULL_CONDITION_DEVE_ESSERE_DIVERSO_NULL);
		}
		this.addWhereCondition(field+" <> ''");
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
		StringBuilder bf = new StringBuilder(field);
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
		StringBuilder bf = new StringBuilder(field);
		bf.append(" BETWEEN ");
		if(stringValueType){
			bf.append("'");
			bf.append(escapeStringValue(leftValue));
			bf.append("'");
		}
		else{
			bf.append(leftValue);
		}
		bf.append(AND_SEPARATOR);
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
				escapeClausole = ESCAPE_SEPARATOR+"'"+escapePattern.getEscapeClausole()+"'";
			}
			buildCondition = "( "+columnName+LIKE_SEPARATOR+"'"+escapePattern.getEscapeValue()+"'"+escapeClausole+" )";
		}
		else{
			buildCondition = "( "+columnName+LIKE_SEPARATOR+"'"+searchPattern+"' )";
		}
		return buildCondition;
	}

	/**
	 * Aggiunge una condizione di ricerca
	 * es: SELECT * from tabella WHERE (columnName LIKE 'searc"+LIKE_SEPARATOR+"rn')
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
		if((buildCondition.indexOf("?")==-1) && this.conditions.contains(buildCondition))
			throw new SQLQueryObjectException(WHERE_CONDITION_PREFIX+buildCondition+GIA_ESISTENTE_TRA_CONDIZIONI_WHERE);
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

	@Override
	public String getWhereLikeCondition(String columnName,String searchPattern, LikeConfig c) throws SQLQueryObjectException{
		return this.createWhereLikeCondition(columnName,searchPattern,
				c.isEscape(),
				c.isContains(), c.isStartsWith(), c.isEndsWith(),
				c.isCaseInsensitive());
	}
	
	private String createWhereLikeCondition(String columnName,String searchPattern,
			boolean escape, 
			boolean contains, boolean startsWith, boolean endsWith,  
			boolean caseInsensitive) throws SQLQueryObjectException{
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
			buildCondition = createWhereLikeConditionEscapeEngine(columnName, searchPattern,
					contains, startsWith, endsWith,  
					caseInsensitive);
		}
		else{
			buildCondition = createWhereLikeConditionNoEscapeEngine(columnName, searchPattern,
					contains, startsWith, endsWith,  
					caseInsensitive);
		}

		return buildCondition;
	}
	private String createWhereLikeConditionEscapeEngine(String columnName,String searchPattern,
			boolean contains, boolean startsWith, boolean endsWith,  
			boolean caseInsensitive) throws SQLQueryObjectException {
		String buildCondition = null;
		if(caseInsensitive) {
			buildCondition = createWhereLikeConditionEscapeEngineCaseInsensitive(columnName, searchPattern,
					contains, startsWith, endsWith);
		}
		else {
			buildCondition = createWhereLikeConditionEscapeEngineCaseSensitive(columnName, searchPattern,
					contains, startsWith, endsWith);
		}
		return buildCondition;
	}
	private String createWhereLikeConditionEscapeEngineCaseInsensitive(String columnName,String searchPattern,
			boolean contains, boolean startsWith, boolean endsWith) throws SQLQueryObjectException {
		String buildCondition = null;
		EscapeSQLPattern escapePattern = escapePatternValue(searchPattern);
		String escapeClausole = "";
		if(escapePattern.isUseEscapeClausole()){
			escapeClausole = ESCAPE_SEPARATOR+"'"+escapePattern.getEscapeClausole()+"'";
		}
		if(contains) {
			buildCondition =LOWER_PREFIX+columnName+")"+LIKE_SEPARATOR+"'%"+escapePattern.getEscapeValue().toLowerCase()+"%'"+escapeClausole+" )";
		}
		else if(startsWith) {
			buildCondition =LOWER_PREFIX+columnName+")"+LIKE_SEPARATOR+"'"+escapePattern.getEscapeValue().toLowerCase()+"%'"+escapeClausole+" )";
		}
		else if(endsWith) {
			buildCondition =LOWER_PREFIX+columnName+")"+LIKE_SEPARATOR+"'%"+escapePattern.getEscapeValue().toLowerCase()+"'"+escapeClausole+" )";
		}
		else {
			buildCondition =LOWER_PREFIX+columnName+")"+LIKE_SEPARATOR+"'"+escapePattern.getEscapeValue().toLowerCase()+"'"+escapeClausole+" )";
		}	
		return buildCondition;
	}
	private String createWhereLikeConditionEscapeEngineCaseSensitive(String columnName,String searchPattern,
			boolean contains, boolean startsWith, boolean endsWith) throws SQLQueryObjectException {
		String buildCondition = null;
		EscapeSQLPattern escapePattern = escapePatternValue(searchPattern);
		String escapeClausole = "";
		if(escapePattern.isUseEscapeClausole()){
			escapeClausole = ESCAPE_SEPARATOR+"'"+escapePattern.getEscapeClausole()+"'";
		}
		if(contains) {
			buildCondition ="( "+columnName+LIKE_SEPARATOR+"'%"+escapePattern.getEscapeValue()+"%'"+escapeClausole+" )";
		}
		else if(startsWith) {
			buildCondition ="( "+columnName+LIKE_SEPARATOR+"'"+escapePattern.getEscapeValue()+"%'"+escapeClausole+" )";
		}
		else if(endsWith) {
			buildCondition ="( "+columnName+LIKE_SEPARATOR+"'%"+escapePattern.getEscapeValue()+"'"+escapeClausole+" )";
		}
		else {
			buildCondition ="( "+columnName+LIKE_SEPARATOR+"'"+escapePattern.getEscapeValue()+"'"+escapeClausole+" )";
		}	
		return buildCondition;
	}
	private String createWhereLikeConditionNoEscapeEngine(String columnName,String searchPattern,
			boolean contains, boolean startsWith, boolean endsWith,  
			boolean caseInsensitive) {
		String buildCondition = null;
		if(caseInsensitive) {
			buildCondition = createWhereLikeConditionNoEscapeEngineCaseInsensitive(columnName, searchPattern,
					contains, startsWith, endsWith);
		}
		else {
			buildCondition = createWhereLikeConditionNoEscapeEngineCaseSensitive(columnName, searchPattern,
					contains, startsWith, endsWith);
		}				
		return buildCondition;
	}
	private String createWhereLikeConditionNoEscapeEngineCaseInsensitive(String columnName,String searchPattern,
			boolean contains, boolean startsWith, boolean endsWith) {
		String buildCondition = null;
		if(contains) {
			buildCondition =LOWER_PREFIX+columnName+")"+LIKE_SEPARATOR+"'%"+searchPattern.toLowerCase()+"%' )";
		}
		else if(startsWith) {
			buildCondition =LOWER_PREFIX+columnName+")"+LIKE_SEPARATOR+"'"+searchPattern.toLowerCase()+"%' )";
		}
		else if(endsWith) {
			buildCondition =LOWER_PREFIX+columnName+")"+LIKE_SEPARATOR+"'%"+searchPattern.toLowerCase()+"' )";
		}
		else {
			buildCondition =LOWER_PREFIX+columnName+")"+LIKE_SEPARATOR+"'"+searchPattern.toLowerCase()+"' )";
		}				
		return buildCondition;
	}
	private String createWhereLikeConditionNoEscapeEngineCaseSensitive(String columnName,String searchPattern,
			boolean contains, boolean startsWith, boolean endsWith) {
		String buildCondition = null;
		if(contains) {
			buildCondition ="( "+columnName+LIKE_SEPARATOR+"'%"+searchPattern+"%' )";
		}
		else if(startsWith) {
			buildCondition ="( "+columnName+LIKE_SEPARATOR+"'"+searchPattern+"%' )";
		}
		else if(endsWith) {
			buildCondition ="( "+columnName+LIKE_SEPARATOR+"'%"+searchPattern+"' )";
		}
		else {
			buildCondition ="( "+columnName+LIKE_SEPARATOR+"'"+searchPattern+"' )";
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

		String buildCondition = this.createWhereLikeCondition(columnName, searchPattern, 
				escape, 
				contains, false, false, 
				caseInsensitive);
		if((buildCondition.indexOf("?")==-1) && this.conditions.contains(buildCondition))
			throw new SQLQueryObjectException(WHERE_CONDITION_PREFIX+buildCondition+GIA_ESISTENTE_TRA_CONDIZIONI_WHERE);
		this.conditions.add(buildCondition);
		return this;
	}

	@Override
	public ISQLQueryObject addWhereLikeCondition(String columnName,String searchPattern, LikeConfig likeConfig) throws SQLQueryObjectException{
		
		String buildCondition = this.createWhereLikeCondition(columnName, searchPattern, 
				likeConfig.isEscape(), 
				likeConfig.isContains(), likeConfig.isStartsWith(), likeConfig.isEndsWith(), 
				likeConfig.isCaseInsensitive());
		if((buildCondition.indexOf("?")==-1) && this.conditions.contains(buildCondition))
			throw new SQLQueryObjectException(WHERE_CONDITION_PREFIX+buildCondition+GIA_ESISTENTE_TRA_CONDIZIONI_WHERE);
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
		return this.createWhereLikeCondition(columnName, searchPattern, 
				true, 
				contains, false, false,
				caseInsensitive);
	}
	@Override
	public String getWhereLikeCondition(String columnName,String searchPattern,boolean escape, boolean contains, boolean caseInsensitive) throws SQLQueryObjectException{
		return this.createWhereLikeCondition(columnName, searchPattern, 
				escape, 
				contains, false, false,
				caseInsensitive);
	}

	
	
	
	private ISQLQueryObject addWhereCondition(String columnName,String searchPattern, DateTimePartEnum dateTimePart) throws SQLQueryObjectException{
		if(columnName==null || "".equals(columnName)){
			throw new SQLQueryObjectException(FIELD_DEVE_ESSERE_DIVERSO_NULL);
		}
		String field = this.getExtractDateTimePartFromTimestampFieldPrefix(dateTimePart)+columnName+this.getExtractDateTimePartFromTimestampFieldSuffix(dateTimePart);
		this.addWhereCondition(field+" = '"+searchPattern+"'");
		return this;
	}

	/**
	 * Aggiunge una condizione di ricerca
	 * es: SELECT * from tabella WHERE (EXTRACT(YEAR FROM columnName) = 'pattern')
	 * 
	 * @param columnName
	 * @param searchPattern
	 */
	@Override
	public ISQLQueryObject addWhereYearCondition(String columnName,String searchPattern) throws SQLQueryObjectException{
		return addWhereCondition(columnName, searchPattern, DateTimePartEnum.YEAR);
	}
	
	/**
	 * Aggiunge una condizione di ricerca
	 * es: SELECT * from tabella WHERE (EXTRACT(MONTH FROM columnName) = 'pattern')
	 * 
	 * @param columnName
	 * @param searchPattern
	 */
	@Override
	public ISQLQueryObject addWhereMonthCondition(String columnName,String searchPattern) throws SQLQueryObjectException{
		return addWhereCondition(columnName, searchPattern, DateTimePartEnum.MONTH);
	}
	
	/**
	 * Aggiunge una condizione di ricerca
	 * es: SELECT * from tabella WHERE (EXTRACT(DAY FROM columnName) = 'pattern')
	 * 
	 * @param columnName
	 * @param searchPattern
	 */
	@Override
	public ISQLQueryObject addWhereDayCondition(String columnName,String searchPattern) throws SQLQueryObjectException{
		return addWhereCondition(columnName, searchPattern, DateTimePartEnum.DAY);
	}
	
	/**
	 * Aggiunge una condizione di ricerca
	 * es: SELECT * from tabella WHERE (EXTRACT(HOUR FROM columnName) = 'pattern')
	 * 
	 * @param columnName
	 * @param searchPattern
	 */
	@Override
	public ISQLQueryObject addWhereHourCondition(String columnName,String searchPattern) throws SQLQueryObjectException{
		return addWhereCondition(columnName, searchPattern, DateTimePartEnum.HOUR);
	}
	
	/**
	 * Aggiunge una condizione di ricerca
	 * es: SELECT * from tabella WHERE (EXTRACT(MINUTE FROM columnName) = 'pattern')
	 * 
	 * @param columnName
	 * @param searchPattern
	 */
	@Override
	public ISQLQueryObject addWhereMinuteCondition(String columnName,String searchPattern) throws SQLQueryObjectException{
		return addWhereCondition(columnName, searchPattern, DateTimePartEnum.MINUTE);
	}
	
	/**
	 * Aggiunge una condizione di ricerca
	 * es: SELECT * from tabella WHERE (EXTRACT(SECOND FROM columnName) = 'pattern')
	 * 
	 * @param columnName
	 * @param searchPattern
	 */
	@Override
	public ISQLQueryObject addWhereSecondCondition(String columnName,String searchPattern) throws SQLQueryObjectException{
		return addWhereCondition(columnName, searchPattern, DateTimePartEnum.SECOND);
	}
	
	
	
	private ISQLQueryObject addWhereCondition(String columnName,String searchPattern, DayFormatEnum dayFormatEnum) throws SQLQueryObjectException{
		if(columnName==null || "".equals(columnName)){
			throw new SQLQueryObjectException(FIELD_DEVE_ESSERE_DIVERSO_NULL);
		}
		String field = this.getExtractDayFormatFromTimestampFieldPrefix(dayFormatEnum)+columnName+this.getExtractDayFormatFromTimestampFieldSuffix(dayFormatEnum);
		this.addWhereCondition(field+" = '"+searchPattern+"'");
		return this;
	}
	
	/**
	 * Aggiunge una condizione di ricerca
	 * es: SELECT * from tabella WHERE (TO_CHAR(field, 'DAY') = 'pattern')
	 * 
	 * @param columnName
	 * @param searchPattern
	 */
	@Override
	public ISQLQueryObject addWhereFullDayNameCondition(String columnName,String searchPattern) throws SQLQueryObjectException{
		return addWhereCondition(columnName, searchPattern, DayFormatEnum.FULL_DAY_NAME);
	}
	
	/**
	 * Aggiunge una condizione di ricerca
	 * es: SELECT * from tabella WHERE (TO_CHAR(field, 'DY') = 'pattern')
	 * 
	 * @param columnName
	 * @param searchPattern
	 */
	@Override
	public ISQLQueryObject addWhereShortDayNameCondition(String columnName,String searchPattern) throws SQLQueryObjectException{
		return addWhereCondition(columnName, searchPattern, DayFormatEnum.SHORT_DAY_NAME);
	}
	
	/**
	 * Aggiunge una condizione di ricerca
	 * es: SELECT * from tabella WHERE (TO_CHAR(field, 'DDD') = 'pattern')
	 * 
	 * @param columnName
	 * @param searchPattern
	 */
	@Override
	public ISQLQueryObject addWhereDayOfYearCondition(String columnName,String searchPattern) throws SQLQueryObjectException{
		return addWhereCondition(columnName, searchPattern, DayFormatEnum.DAY_OF_YEAR);
	}
	
	/**
	 * Aggiunge una condizione di ricerca
	 * es: SELECT * from tabella WHERE (TO_CHAR(field, 'D') = 'pattern')
	 * 
	 * @param columnName
	 * @param searchPattern
	 */
	@Override
	public ISQLQueryObject addWhereDayOfWeekCondition(String columnName,String searchPattern) throws SQLQueryObjectException{
		return addWhereCondition(columnName, searchPattern, DayFormatEnum.DAY_OF_WEEK);
	}
	
	
	
	
	
	
	private String createWhereExistsCondition(boolean notExists,ISQLQueryObject sqlQueryObject)throws SQLQueryObjectException{
		if(sqlQueryObject==null)
			throw new SQLQueryObjectException("ISQLQueryObject, su cui viene effettuato il controllo di exists, non fornito");

		if(sqlQueryObject instanceof SQLQueryObjectCore){
			// http://stackoverflow.com/questions/5119190/oracle-sql-order-by-in-subquery-problems
			SQLQueryObjectCore core = (SQLQueryObjectCore) sqlQueryObject;
			if(core.orderBy!=null && !core.orderBy.isEmpty()){
				throw new SQLQueryObjectException("ISQLQueryObject, su cui viene effettuato il controllo di exists, non deve possedere condizioni di order by");
			}
			if(core.offset>=0){
				throw new SQLQueryObjectException("ISQLQueryObject, su cui viene effettuato il controllo di exists, non deve possedere la condizione di OFFSET");
			}
		}

		StringBuilder bf = new StringBuilder();
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
			checkWhereSqlConditionConditionLimitOffse(in, core);
		}

		StringBuilder bf = new StringBuilder();
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
	private void checkWhereSqlConditionConditionLimitOffse(boolean in,SQLQueryObjectCore core) throws SQLQueryObjectException {
		// http://stackoverflow.com/questions/5119190/oracle-sql-order-by-in-subquery-problems
		if(core.orderBy!=null && !core.orderBy.isEmpty()){
			throw new SQLQueryObjectException("ISQLQueryObject, su cui viene effettuato il controllo di exists, non deve possedere condizioni di order by");
		}
		if(core.offset>=0){
			throw new SQLQueryObjectException("ISQLQueryObject, su cui viene effettuato il controllo di exists, non deve possedere la condizione di OFFSET");
		}
		if(!in &&
			core.limit>1){
			throw new SQLQueryObjectException("ISQLQueryObject, su cui viene effettuato il controllo di exists, non deve possedere la condizione di LIMIT o al massimo puo' essere definita con valore '1')");
		}
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
			StringBuilder str =  new StringBuilder();
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

		EscapeSQLConfiguration escapeConfig = this.getEscapeSQLConfiguration();

		StringBuilder str =  new StringBuilder();
		char[] v = pattern.toCharArray();
		for(int i=0; i<v.length; i++){
			
			if(escapeConfig.isDefaultEscape(v[i])){
				str.append(escapeConfig.getEscape());

				if(escapeConfig.isUseEscapeClausole()){
					escapeSqlPattern.setUseEscapeClausole(true);
					escapeSqlPattern.setEscapeClausole(escapeConfig.getEscape());
				}
			}
			else if(escapeConfig.isOtherEscape(v[i])){
				str.append(escapeConfig.getOtherEscapeCharacter(v[i]));
			}
			
			str.append(v[i]);

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
		/**
		 * Non e' sempre vero
		 * if(this.fields.contains(groupByNomeField)==false){
		 *	throw new SQLQueryObjectException("GroupBy Condition field non e' registrati tra i select field");
		}*/

		/**if(this.tableAlias.size()>0){
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

	public List<String> getGroupByConditions() throws SQLQueryObjectException{
		if(this.groupBy!=null && !this.groupBy.isEmpty() &&
			this.fields.isEmpty()){
			throw new SQLQueryObjectException("Non e' possibile utilizzare condizioni di group by se non sono stati indicati select field");
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
		/**
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
			else if(!this.getGroupByConditions().isEmpty()){
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
	/**@Override
	public abstract String createSQLQuery() throws SQLQueryObjectException;*/

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
			checkUnionFieldCount();
		}

		if(this.offset>=0 &&
			this.fields.isEmpty()) {
			throw new SQLQueryObjectException("Non e' possibile usare offset se non e' stato indicato alcun field nella select piu' esterna della union");
		}
		if(this.limit>0 &&
			this.fields.isEmpty()) {
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
		checkUnionFieldExists(sqlQueryObject);

		// Check nomi fields			
		checkUnionFieldNames(sqlQueryObject);

		// Check presenza order by DEVE essere accompagnata da un offset in modo che venga generata la condizione di order by su Oracle/SLQServer per le condizioni
		checkUnionFieldOrderOffset(sqlQueryObject);
		
		// Check Select For Update
		if(this.selectForUpdate){
			throw new SQLQueryObjectException("Non è possibile abilitare il comando 'selectForUpdate' con la UNION");
		}
	}
	protected void checkUnionFieldCount() throws SQLQueryObjectException{
		if(this.orderBy!=null && !this.orderBy.isEmpty()){
			throw new SQLQueryObjectException("Non e' possibile usare order by in una count");
		}
		if(this.limit>0){
			throw new SQLQueryObjectException("Non e' possibile usare limit in una count");
		}
		if(this.offset>=0){
			throw new SQLQueryObjectException("Non e' possibile usare offset in una count");
		}
	}
	protected void checkUnionFieldExists(ISQLQueryObject... sqlQueryObject)throws SQLQueryObjectException{
		for(int i=0;i<sqlQueryObject.length;i++){
			ISQLQueryObject sqlQueryObjectDaVerificare = sqlQueryObject[i];
			List<String> nomiFieldSqlQueryObjectDaVerificare = sqlQueryObjectDaVerificare.getFieldsName();
			if(nomiFieldSqlQueryObjectDaVerificare==null || nomiFieldSqlQueryObjectDaVerificare.isEmpty()){
				throw new SQLQueryObjectException("La select numero "+(i+1)+" non possiede fields?");
			}
		}
	}
	protected void checkUnionFieldNames(ISQLQueryObject... sqlQueryObject)throws SQLQueryObjectException{
		/**		for(int i=0;i<sqlQueryObject.length;i++){
		//			System.out.println("CHECK ["+i+"] ...");
		//			List<String> nomiFieldSqlQueryObjectDaVerificare = sqlQueryObject[i].getFieldsName();
		//			for (String string : nomiFieldSqlQueryObjectDaVerificare) {
		//				System.out.println("\t-"+string);
		//			}
		//		}*/
		for(int i=0;i<sqlQueryObject.length;i++){

			List<String> nomiFieldSqlQueryObjectDaVerificare = sqlQueryObject[i].getFieldsName();
			String [] nomi = nomiFieldSqlQueryObjectDaVerificare.toArray(new String[1]);

			for(int indiceField =0; indiceField<nomi.length;indiceField++){

				String fieldDaVerificare = nomi[indiceField];

				for(int altriSqlObject=0;altriSqlObject<sqlQueryObject.length;altriSqlObject++){

					if(altriSqlObject==i){
						// e' l'oggetto in questione
						continue;
					}

					if( (!sqlQueryObject[altriSqlObject].getFieldsName().contains(fieldDaVerificare)) &&
							(!sqlQueryObject[altriSqlObject].getFieldsName().contains("*")) ){
						throw new SQLQueryObjectException("Field ["+fieldDaVerificare+"] trovato nella select numero "+(i+1) +" non presente nella select numero "+(altriSqlObject+1) +" (Se sono campi diversi usare lo stesso alias)");
					}
				}
			}
		}
	}
	protected void checkUnionFieldOrderOffset(ISQLQueryObject... sqlQueryObject)throws SQLQueryObjectException{
		for(int i=0;i<sqlQueryObject.length;i++){
			SQLQueryObjectCore sqlQueryObjectDaVerificare = (SQLQueryObjectCore) sqlQueryObject[i];
			if(sqlQueryObjectDaVerificare.orderBy!=null && !sqlQueryObjectDaVerificare.orderBy.isEmpty() &&
				sqlQueryObjectDaVerificare.offset<0){
				/**throw new SQLQueryObjectException("La select numero "+(i+1)+" per poter essere utilizzata nella UNION, siccome presenta condizioni di ordinamento, deve specificare anche l'offset");*/
				sqlQueryObjectDaVerificare.setOffset(0); // Se viene messo il limit, l'offset cmq parte da 0
			}
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
			throw new SQLQueryObjectException(FIELD_NAME_IS_NULL_OR_EMPTY);
		if(valueField==null)
			throw new SQLQueryObjectException("Field value is null");
		if(this.updateFieldsName.contains(nomeField)){
			throw new SQLQueryObjectException(FIELD_NAME_PREFIX+nomeField+" gia inserito tra gli update fields");
		}else{
			this.updateFieldsName.add(nomeField);
			this.updateFieldsValue.add(valueField);
		}
		return this;
	}
	
	@Override
	public ISQLQueryObject addUpdateField(String nomeField,Case caseValue) throws SQLQueryObjectException{
		if(nomeField==null || "".equals(nomeField))
			throw new SQLQueryObjectException(FIELD_NAME_IS_NULL_OR_EMPTY);
		
		if(this.updateFieldsName.contains(nomeField)){
			throw new SQLQueryObjectException(FIELD_NAME_PREFIX+nomeField+" gia inserito tra gli update fields");
		}else{
			String caseCondition = getCaseCondition(caseValue);
									
			this.updateFieldsName.add(nomeField);
			this.updateFieldsValue.add(caseCondition);
		}
		return this;
	}
	protected String getPrefixCastValue(CastColumnType type, int length) {
		if(type!=null && length>0) {
			// nop
		}
		return "";
	}
	protected String getSuffixCastValue(CastColumnType type, int length) {
		if(type!=null || length>0) {
			// nop
		}
		return "";
	}
	
	@Override
	public String createSQLUpdate() throws SQLQueryObjectException{
		
		if(this.updateTable==null)
			throw new SQLQueryObjectException("Nome Tabella per l'aggiornamento non definito");
		if(this.updateFieldsName.isEmpty())
			throw new SQLQueryObjectException("Nessuna coppia nome/valore da aggiornare presente");
		if(this.updateFieldsName.size()!= this.updateFieldsValue.size()){
			throw new SQLQueryObjectException("FieldsName.size["+this.updateFieldsName.size()+"] <> FieldsValue.size["+this.updateFieldsValue.size()+"]");
		}
		
		if(this.forceSelectForUpdateDisabledForNotQueryMethod){
			
			// Per permettere di usare l'oggetto sqlQueryObject con più comandi
			boolean oldValueSelectForUpdate = this.selectForUpdate;
			try{
				this.selectForUpdate = false;
				return this.createSQLUpdateEngine();
			}finally{
				this.selectForUpdate = oldValueSelectForUpdate;
			}
			
		}
		else{
			return this.createSQLUpdateEngine();
		}
	}
	
	protected abstract String createSQLUpdateEngine() throws SQLQueryObjectException;





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
			throw new SQLQueryObjectException(FIELD_NAME_IS_NULL_OR_EMPTY);
		if(valueField==null)
			throw new SQLQueryObjectException("Field value is null");
		if(this.insertFieldsName.contains(nomeField)){
			throw new SQLQueryObjectException(FIELD_NAME_PREFIX+nomeField+" gia inserito tra gli insert fields");
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
		if(this.insertFieldsName.isEmpty())
			throw new SQLQueryObjectException("Nessuna coppia nome/valore da inserire presente");
		if(this.insertFieldsName.size()!= this.insertFieldsValue.size()){
			throw new SQLQueryObjectException("FieldsName.size <> FieldsValue.size");
		}

		if(this.forceSelectForUpdateDisabledForNotQueryMethod){
		
			// Per permettere di usare l'oggetto sqlQueryObject con più comandi
			boolean oldValueSelectForUpdate = this.selectForUpdate;
			try{
				this.selectForUpdate = false;
				return this.createSQLInsertEngine();
			}finally{
				this.selectForUpdate = oldValueSelectForUpdate;
			}
			
		}
		else{
			return this.createSQLInsertEngine();
		}
		

	}
	private String createSQLInsertEngine() {
		StringBuilder bf = new StringBuilder();
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
		if(this.tables.isEmpty()){
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
				return this.createSQLDeleteEngine();
			}finally{
				this.selectForUpdate = oldValueSelectForUpdate;
			}
			
		}
		else{
			return this.createSQLDeleteEngine();
		}
	}
	
	protected abstract String createSQLDeleteEngine() throws SQLQueryObjectException;
	
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
		if(this.conditions.isEmpty())
			throw new SQLQueryObjectException("Nessuna condizione presente");
		
		if(this.forceSelectForUpdateDisabledForNotQueryMethod){
			
			// Per permettere di usare l'oggetto sqlQueryObject con più comandi
			boolean oldValueSelectForUpdate = this.selectForUpdate;
			try{
				this.selectForUpdate = false;
				return this.createSQLConditionsEngine();
			}finally{
				this.selectForUpdate = oldValueSelectForUpdate;
			}
			
		}
		else{
			return this.createSQLConditionsEngine();
		}
	}

	protected abstract String createSQLConditionsEngine() throws SQLQueryObjectException;







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
