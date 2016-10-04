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
public interface ISQLQueryObject {
	
	/** LIMIT DEFAULT VALUE */
	public static final int LIMIT_DEFAULT_VALUE = 1000;
	
	

	
	
	// SELECT FIELDS NORMALI
	
	/**
	 * Aggiunge un field alla select, se non sono presenti field, viene utilizzato '*'
	 * es: SELECT nomeField FROM ....
	 * 
	 * @param nomeField Nome del Field
	 */
	public ISQLQueryObject addSelectField(String nomeField) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge un field alla select, se non sono presenti field, viene utilizzato '*'
	 * es: SELECT nomeTabella.nomeField FROM ....
	 * 
	 * @param nomeTabella Nome della tabella su cui reperire il field
	 * @param nomeField Nome del Field
	 */
	public ISQLQueryObject addSelectField(String nomeTabella,String nomeField) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge un field alla select, se non sono presenti field, viene utilizzato '*'
	 * es: SELECT nomeField FROM ....
	 * 
	 * @param nomeField Nome del Field
	 */
	public ISQLQueryObject addSelectAliasField(String nomeField,String alias) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge un field alla select, se non sono presenti field, viene utilizzato '*'
	 * es: SELECT nomeTabella.nomeField FROM ....
	 * 
	 * @param nomeTabella Nome della tabella su cui reperire il field
	 * @param nomeField Nome del Field
	 * @param alias Alias
	 */
	public ISQLQueryObject addSelectAliasField(String nomeTabella,String nomeField,String alias) throws SQLQueryObjectException;
	
	
	
	// SELECT FIELDS COALESCE
	
	/**
	 * Aggiunge un field alla select definendolo tramite la funzione coalesce
	 * es: SELECT coalesce(nomeField, 'VALORE') as alias FROM ....
	 * 
	 * @param nomeField Nome del Field
	 * @param alias Alias
	 * @param valore Valore utilizzato in caso il campo sia null
	 */
	public ISQLQueryObject addSelectCoalesceField(String nomeField,String alias,String valore) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge un field alla select definendolo tramite la funzione coalesce
	 * es: SELECT coalesce(nomeTabella.nomeField, 'VALORE') as alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param nomeField Nome del Field
	 * @param alias Alias
	 * @param valore Valore utilizzato in caso il campo sia null
	 */
	public ISQLQueryObject addSelectCoalesceField(String aliasTabella,String nomeField,String alias,String valore) throws SQLQueryObjectException;
	
	
	
	
	// SELECT FIELDS COUNTS
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>fieldCount</var>
	 * es: SELECT count(*) as alias FROM ....
	 * 
	 * @param alias Alias
	 */
	public ISQLQueryObject addSelectCountField(String alias) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>fieldCount</var>
	 * es: SELECT count(fieldCount) as alias FROM ....
	 * 
	 * @param fieldCount Nome del Field
	 * @param alias Alias
	 */
	public ISQLQueryObject addSelectCountField(String fieldCount,String alias) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>fieldCount</var>
	 * es: SELECT count(DISTINCT fieldCount) as alias FROM ....
	 * 
	 * @param fieldCount Nome del Field
	 * @param alias Alias
	 */
	public ISQLQueryObject addSelectCountField(String fieldCount,String alias,boolean distinct) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>fieldCount</var>
	 * es: SELECT count(nomeTabella.fieldCount) as alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param fieldCount Nome del Field
	 * @param alias Alias
	 */
	public ISQLQueryObject addSelectCountField(String aliasTabella,String fieldCount,String alias) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>fieldCount</var>
	 * es: SELECT count(DISTINCT nomeTabella.fieldCount) as alias FROM ....
	 *
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param fieldCount Nome del Field
	 * @param alias Alias
	 */
	public ISQLQueryObject addSelectCountField(String aliasTabella,String fieldCount,String alias,boolean distinct) throws SQLQueryObjectException;
	
	
	
	// SELECT FIELDS AVG
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>fieldAvg</var>
	 * es: SELECT avg(fieldAvg) as alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	public ISQLQueryObject addSelectAvgField(String field,String alias) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>fieldAvg</var>
	 * es: SELECT avg(nomeTabella.fieldAvg) as alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	public ISQLQueryObject addSelectAvgField(String aliasTabella,String field,String alias) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>fieldAvg</var> (di tipo Timestamp)
	 * es: SELECT avg(fieldAvg) as alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	public ISQLQueryObject addSelectAvgTimestampField(String field,String alias) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>fieldAvg</var> (di tipo Timestamp)
	 * es: SELECT avg(nomeTabella.fieldAvg) as alias FROM ....
	 *
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	public ISQLQueryObject addSelectAvgTimestampField(String aliasTabella,String field,String alias) throws SQLQueryObjectException;
	
	
	
	
	
	// SELECT FIELDS MAX
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var> 
	 * es: SELECT max(field) as alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	public ISQLQueryObject addSelectMaxField(String field,String alias) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var> 
	 * es: SELECT max(nomeTabella.field) as alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias alias
	 */
	public ISQLQueryObject addSelectMaxField(String aliasTabella,String field,String alias) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT max(field) as alias FROM ....
	 * 
	  * @param field Nome del Field
	 * @param alias Alias
	 */
	public ISQLQueryObject addSelectMaxTimestampField(String field,String alias) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT max(nomeTabella.field) as alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	public ISQLQueryObject addSelectMaxTimestampField(String aliasTabella,String field,String alias) throws SQLQueryObjectException;
	
	
	
	
	
	// SELECT FIELDS MIN
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var>
	 * es: SELECT min(field) as alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	public ISQLQueryObject addSelectMinField(String field,String alias) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var>
	 * es: SELECT min(nomeTabella.field) as alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	public ISQLQueryObject addSelectMinField(String aliasTabella,String field,String alias) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT min(field) as alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	public ISQLQueryObject addSelectMinTimestampField(String field,String alias) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT min(nomeTabella.field) as alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	public ISQLQueryObject addSelectMinTimestampField(String aliasTabella,String field,String alias) throws SQLQueryObjectException;
	
	
	
	
	
	
	
	
	
	// SELECT FIELDS SUM
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var>
	 * es: SELECT sum(field) as alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	public ISQLQueryObject addSelectSumField(String field,String alias) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var>
	 * es: SELECT sum(nomeTabella.field) as alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	public ISQLQueryObject addSelectSumField(String aliasTabella,String field,String alias) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT sum(field) as alias FROM ....
	 * 
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	public ISQLQueryObject addSelectSumTimestampField(String field,String alias) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge un field count alla select, con un alias, del campo <var>field</var> (di tipo Timestamp)
	 * es: SELECT sum(nomeTabella.field) as alias FROM ....
	 * 
	 * @param aliasTabella Alias della tabella su cui reperire il field
	 * @param field Nome del Field
	 * @param alias Alias
	 */
	public ISQLQueryObject addSelectSumTimestampField(String aliasTabella,String field,String alias) throws SQLQueryObjectException;
	
	
	
	
	
	
	
	
	
	// SELECT FORCE INDEX
	
	/**
	 * Aggiunge l'istruzione SQL per forzare l'utilizzo dell'indice indicato nel parametro nella lettura della tabella indicata
	 * es: SELECT '/*+ index(nomeTabella indexName) *'  FROM ....
	 * 
	 * @param nomeTabella Nome della tabella su cui forzare l'indice
	 * @param indexName Nome dell'indice
	 */
	public ISQLQueryObject addSelectForceIndex(String nomeTabella,String indexName) throws SQLQueryObjectException;
	
	
	
	// SET DISTINCTS IN CIMA ALLA SELECT
	
	/**
	 * Aggiunge un field alla select, se non sono presenti field, viene utilizzato '*'
	 * es: SELECT DISTINCT nomeField,nomeFiled2.... FROM ....
	 * 
	 * @param value Indicazione sul distinct
	 */
	public void setSelectDistinct(boolean value) throws SQLQueryObjectException;
	
	
	
	// FIELDS/TABLE NAME
	
	/**
	 * Ritorna i nomi dei fields impostati (se e' stato utilizzato un alias ritorna il valore dell'alias)
	 * 
	 * @return nomi dei fields impostati (se e' stato utilizzato un alias ritorna il valore dell'alias)
	 * @throws SQLQueryObjectException
	 */
	public Vector<String> getFieldsName() throws SQLQueryObjectException;
	
	/**
	 * Indicazione se e' il nome rappresenta una funzione
	 * 
	 * @return Indicazione se e' il nome rappresenta una funzione
	 * @throws SQLQueryObjectException
	 */
	public Boolean isFieldNameForFunction(String fieldName) throws SQLQueryObjectException;
	
	/**
	 * Ritorna i nomi dei fields impostati (se e' stato utilizzato un alias ritorna comunque il nome della colonna)
	 * 
	 * @return nomi dei fields impostati (se e' stato utilizzato un alias ritorna comunque il nome della colonna)
	 * @throws SQLQueryObjectException
	 */
	//public Vector<String> getFields() throws SQLQueryObjectException;
	
	/**
	 * Ritorna i nomi delle tabelle impostate (se e' stato utilizzato un alias ritorna il valore dell'alias)
	 * 
	 * @return nomi delle tabelle impostate (se e' stato utilizzato un alias ritorna il valore dell'alias)
	 * @throws SQLQueryObjectException
	 */
	public Vector<String> getTablesName() throws SQLQueryObjectException;
	
	/**
	 * Ritorna i nomi delle tabelle impostate (se e' stato utilizzato un alias ritorna comunque il nome della tabella)
	 * 
	 * @return nomi delle tabelle impostate (se e' stato utilizzato un alias ritorna comunque il nome della tabella)
	 * @throws SQLQueryObjectException
	 */
	//public Vector<String> getTables() throws SQLQueryObjectException;
	
	
	
	
	
	
	
	// FIELDS UNIX FUNCTIONS
	
	/**
	 * Ritorna la conversione in UnixTimestamp della Colonna
	 * 
	 * @param column colonna da convertire in UnixTimestamp
	 * @return conversione in UnixTimestamp della Colonna
	 */
	public String getUnixTimestampConversion(String column);
	
	/**
	 * Ritorna l'intervallo in unixTimestamp tra le due colonne fornite
	 * 
	 * @param columnMax colonna con intervallo temporale maggiore
	 * @param columnMin colonna con intervallo temporale minore
	 * @return ritorna l'intervallo in unixTimestamp tra le due colonne fornite
	 */
	public String getDiffUnixTimestamp(String columnMax,String columnMin);
	
	
	
	
	
	
	// FIELDS/TABLE ALIAS

	// GESTIONE ALIAS 'as'
	// Non tutti i database supportano l'alias 'as', ad esempio Oracle genera un errore se viene usato l'alias con le tabelle.
	/**
	 * Ritorna l'alias di default utilizzabile per un field
	 * 
	 * @return Ritorna l'alias di default utilizzabile per un field
	 */
	public String getDefaultAliasFieldKeyword();

	/**
	 * Ritorna l'alias di default utilizzabile per una tabella
	 * 
	 * @return Ritorna l'alias di default utilizzabile per una tabella
	 */
	public String getDefaultAliasTableKeyword();

	/**
	 * Ritorna gli aliases utilizzabili per un field
	 * 
	 * @return Ritorna gli aliases utilizzabili per un field
	 */
	public List<String> getSupportedAliasesField();
	
	/**
	 * Ritorna gli aliases utilizzabili per una tabella
	 * 
	 * @return Ritorna gli aliases utilizzabili per una tabella
	 */
	public List<String> getSupportedAliasesTable();
	
	

	
	
	// FROM
	
	/**
	 * Aggiunge una tabella di ricerca del from
	 * es: SELECT * from tabella as tabella
	 * 
	 * @param tabella
	 */
	public ISQLQueryObject addFromTable(String tabella) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge una tabella di ricerca del from
	 * es: SELECT * from tabella as alias
	 * 
	 * @param tabella Tabella
	 * @param alias Alias
	 */
	public ISQLQueryObject addFromTable(String tabella,String alias) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge una tabella di ricerca del from
	 * es: SELECT * from ( subSelect )
	 * 
	 * @param subSelect subSelect
	 */
	public ISQLQueryObject addFromTable(ISQLQueryObject subSelect) throws SQLQueryObjectException;
	
	
	
	
	// WHERE
	
	/**
	 * Aggiunge una condizione di ricerca (e associa un operatore logico, se le condizioni di ricerca sono piu' di una)
	 * es: SELECT * from tabella WHERE (condition)
	 * 
	 * @param condition
	 */
	public ISQLQueryObject addWhereCondition(String condition) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge una condizione di ricerca (e associa un operatore logico, se le condizioni di ricerca sono piu' di una)
	 * Imposta come operatore logico di una successiva condizione (se esiste) l'AND se true, l'OR se false
	 * es: SELECT * from tabella WHERE ((condition1) andLogicOperator (condition2) etc.....)
	 * 
	 * @param andLogicOperator
	 * @param conditions
	 */
	public ISQLQueryObject addWhereCondition(boolean andLogicOperator,String... conditions) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge una condizione di ricerca (e associa un operatore logico, se le condizioni di ricerca sono piu' di una)
	 * Imposta come operatore logico di una successiva condizione (se esiste) l'AND se true, l'OR se false
	 * es: SELECT * from tabella WHERE ( [NOT] ( (condition1) andLogicOperator (condition2) etc.....) )
	 * Se il parametro not is true, aggiunge il not davanti alle condizioni
	 * 
	 * @param andLogicOperator
	 * @param conditions
	 */
	public ISQLQueryObject addWhereCondition(boolean andLogicOperator,boolean not,String... conditions) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge una condizione di ricerca (e associa un operatore logico, se le condizioni di ricerca sono piu' di una)
	 * es: SELECT * from tabella WHERE (field is null)
	 * 
	 * @param field Field da verificare
	 */
	public ISQLQueryObject addWhereIsNullCondition(String field) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge una condizione di ricerca (e associa un operatore logico, se le condizioni di ricerca sono piu' di una)
	 * es: SELECT * from tabella WHERE (field is not null)
	 * 
	 * @param field Field da verificare
	 */
	public ISQLQueryObject addWhereIsNotNullCondition(String field) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge una condizione di ricerca in un insieme di valori
	 * esempio concreto:    SELECT * from tabella WHERE id IN (a,b,c,d);
	 * Se viene indicato stringValueType a true, ogni valore viene trattato come stringa e nella sql prodotto viene aggiunto il carattere ' all'inizio e alla fine.
	 * 
	 * @param field Field su cui effettuare la select
	 * @param valore insieme di valori su cui effettuare il controllo
	 * @throws SQLQueryObjectException
	 */
	public ISQLQueryObject addWhereINCondition(String field,boolean stringValueType,String ... valore) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge una condizione di ricerca tra due valori
	 * esempio concreto:    SELECT * from tabella WHERE id BETWEEN a AND b
	 * Se viene indicato stringValueType a true, ogni valore viene trattato come stringa e nella sql prodotto viene aggiunto il carattere ' all'inizio e alla fine.
	 * 
	 * @param field Field su cui effettuare la select
	 * @param stringValueType indica se i valori devono essere trattati come stringhe o meno
	 * @param leftValue Valore dell'intervallo sinistro
	 * @param rightValue Valore dell'intervallo destro
	 * @return SQLQueryObjectException
	 * @throws SQLQueryObjectException
	 */
	public ISQLQueryObject addWhereBetweenCondition(String field,boolean stringValueType,String leftValue,String rightValue) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge una condizione di ricerca
	 * es: SELECT * from tabella WHERE (columnName LIKE 'searchPattern')
	 * 
	 * @param columnName
	 * @param searchPattern
	 */
	public ISQLQueryObject addWhereLikeCondition(String columnName,String searchPattern) throws SQLQueryObjectException;
	public ISQLQueryObject addWhereLikeCondition(String columnName,String searchPattern, boolean escape) throws SQLQueryObjectException;
		
	/**
	 * Ritorna una condizione di ricerca
	 * es: "columnName LIKE 'searchPattern'"
	 * 
	 * @param columnName
	 * @param searchPattern
	 */
	public String getWhereLikeCondition(String columnName,String searchPattern) throws SQLQueryObjectException;
	public String getWhereLikeCondition(String columnName,String searchPattern,boolean escape) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge una condizione di ricerca
	 * es: SELECT * from tabella WHERE (columnName LIKE 'searchPattern')
	 * 
	 * @param columnName
	 * @param searchPattern
	 * @param contains true se la parola deve essere contenuta nella colonna, false se deve essere esattamente la colonna
	 * @param caseInsensitive
	 */
	public ISQLQueryObject addWhereLikeCondition(String columnName,String searchPattern, boolean contains, boolean caseInsensitive) throws SQLQueryObjectException;
	public ISQLQueryObject addWhereLikeCondition(String columnName,String searchPattern, boolean escape, boolean contains, boolean caseInsensitive) throws SQLQueryObjectException;
	
	/**
	 * Ritorna una condizione di ricerca
	 * es: "columnName LIKE 'searchPattern'"
	 * 
	 * @param columnName
	 * @param searchPattern
	 * @param contains true se la parola deve essere contenuta nella colonna, false se deve essere esattamente la colonna
	 * @param caseInsensitive
	 */
	public String getWhereLikeCondition(String columnName,String searchPattern, boolean contains, boolean caseInsensitive) throws SQLQueryObjectException;
	public String getWhereLikeCondition(String columnName,String searchPattern, boolean escape, boolean contains, boolean caseInsensitive) throws SQLQueryObjectException;
	
	
	/**
	 * Aggiunge una condizione di ricerca con EXISTS
	 * La query su cui viene effettuato il controllo di exists e' definito dal parametro sqlQueryObject
	 * es. SELECT * from tabella WHERE [NOT] EXISTS ( sqlQueryObject.createSQLQuery() )
	 * 
	 * @param notExists Indicazione se applicare la negazione all'exists
	 * @param sqlQueryObject query su cui viene effettuato il controllo di exists, la query viene prodotta attraverso il metodo createSQLQuery()
	 * @throws SQLQueryObjectException
	 */
	public ISQLQueryObject addWhereExistsCondition(boolean notExists,ISQLQueryObject sqlQueryObject) throws SQLQueryObjectException;
	
	/**
	 * Ritorna una condizione di ricerca con EXISTS
	 * La query su cui viene effettuato il controllo di exists e' definito dal parametro sqlQueryObject
	 * es. SELECT * from tabella WHERE [NOT] EXISTS ( sqlQueryObject.createSQLQuery() )
	 * 
	 * @param notExists Indicazione se applicare la negazione all'exists
	 * @param sqlQueryObject query su cui viene effettuato il controllo di exists, la query viene prodotta attraverso il metodo createSQLQuery()
	 * @throws SQLQueryObjectException
	 */
	public String getWhereExistsCondition(boolean notExists,ISQLQueryObject sqlQueryObject) throws SQLQueryObjectException;
	
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
	public ISQLQueryObject addWhereSelectSQLCondition(boolean notExists,String field,ISQLQueryObject sqlQueryObject) throws SQLQueryObjectException;
	
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
	public ISQLQueryObject addWhereINSelectSQLCondition(boolean notExists,String field,ISQLQueryObject sqlQueryObject) throws SQLQueryObjectException;
	
	/**
	 * Imposta come operatore logico tra due o piu' condizioni esistenti l'AND se true, l'OR se false
	 * 
	 * @param andLogicOperator
	 */
	public void setANDLogicOperator(boolean andLogicOperator) throws SQLQueryObjectException; // se false viene utilizzato OR tra le condizioni
	
	/**
	 * Imposta il NOT prima delle condizioni NOT ( conditions )
	 * 
	 * @param not
	 */
	public void setNOTBeforeConditions(boolean not) throws SQLQueryObjectException; // se true viene utilizzato il NOT davanti alle condizioni
	
	
	// ESCAPE
	/**
	 * Effettua l'escape di un valore di tipo stringa
	 * 
	 * @param value valore su cui effettuare l'escape
	 * @return valore su cui e' stato effettuato l'escape
	 * @throws SQLQueryObjectException
	 */
	public String escapeStringValue(String value) throws SQLQueryObjectException;
	
	/**
	 * Effettua l'escape di un pattern
	 * 
	 * @param pattern pattern su cui effettuare l'escape
	 * @return pattern su cui e' stato effettuato l'escape
	 * @throws SQLQueryObjectException
	 */
	public EscapeSQLPattern escapePatternValue(String pattern) throws SQLQueryObjectException;
	
	
	
	// GROUP BY
	
	/**
	 * Aggiunge una condizione di GroupBy per i field con il nome sottostante.
	 * I field devono essere precedentemente stati inseriti come SelectField
	 * 
	 * @param groupByNomeField Nome del field di raggruppamento
	 */
	public ISQLQueryObject addGroupBy(String groupByNomeField) throws SQLQueryObjectException;
	
	
	// ORDER BY
	
	/**
	 * Aggiunge una condizione di OrderBy per i field con il nome sottostante.
	 * I field devono essere precedentemente stati inseriti come SelectField
	 * 
	 * @param orderByNomeField Nome del field di ordinamento
	 */
	public ISQLQueryObject addOrderBy(String orderByNomeField) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge una condizione di OrderBy per i field con il nome sottostante.
	 * I field devono essere precedentemente stati inseriti come SelectField
	 * 
	 * @param orderByNomeField Nome del field di ordinamento
	 * @param asc Imposta la stringa di ordinamento (Crescente:true/Decrescente:false)
	 */
	public ISQLQueryObject addOrderBy(String orderByNomeField, boolean asc) throws SQLQueryObjectException;
	
	/**
	 * Imposta la stringa di ordinamento (Crescente:true/Decrescente:false)
	 */
	public void setSortType(boolean asc) throws SQLQueryObjectException;
	
	
	
	// LIMIT E OFFSET
	
	/**
	 * Aggiunge un limite ai risultati ritornati
	 * 
	 * @param limit limite dei risultati ritornati
	 */
	public void setLimit(int limit) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge un offset per i risultati ritornati
	 * 
	 * @param offset offset per i risultati ritornati
	 */
	public void setOffset(int offset) throws SQLQueryObjectException;
	
	
	
	
	// SELECT FOR UPDATE
	
	public void setSelectForUpdate(boolean selectForUpdate) throws SQLQueryObjectException;
	
	
	
	
	// GENERAZIONE SQL
	
	/**
	 * Crea una SQL Query con i dati dell'oggetto
	 * 
	 * @return SQL Query
	 */
	public String createSQLQuery() throws SQLQueryObjectException;	
	
//	/**
//	 * Genera la Stringa SQL
//	 *  
//	 * @param delete genera una stringa SQL adatta per una delete
//	 * @return Stringa SQL
//	 */
//	public String toString(boolean delete);
	
	
	/**
	 * Crea la stringa SQL per l'unione dei parametri presi in sqlQueryObject
	 * Il booleano 'unionAll' indica se i vari parametri devono essere uniti con 'UNION'  o con 'UNION ALL'
	 * 
	 * In caso nell'oggetto viene impostato il LIMIT o OFFSET, questo viene utilizzato per effettuare LIMIT e/o OFFSET dell'unione
	 * 
	 * In caso l'ORDER by viene impostato nell'oggetto, questo viene utilizzato per effettuare ORDER BY dell'unione
	 * 
	 * @param unionAll
	 * @param sqlQueryObject
	 * @return stringa SQL per l'unione dei parametri presi in sqlQueryObject
	 * @throws SQLQueryObjectException
	 */
	public String createSQLUnion(boolean unionAll,ISQLQueryObject... sqlQueryObject) throws SQLQueryObjectException;
	
	/**
	 * Crea la stringa SQL per l'unione dei parametri presi in sqlQueryObject (viene effettuato il count)
	 * Il booleano 'unionAll' indica se i vari parametri devono essere uniti con 'UNION'  o con 'UNION ALL'
	 * 
	 * 
	 * @param unionAll
	 * @param sqlQueryObject
	 * @return stringa SQL per l'unione dei parametri presi in sqlQueryObject (viene effettuato il count)
	 * @throws SQLQueryObjectException
	 */
	public String createSQLUnionCount(boolean unionAll,String aliasCount,ISQLQueryObject... sqlQueryObject) throws SQLQueryObjectException;
	
	
	
	
	/* ---------------- UPDATE ------------------ */
	
	/**
	 * Definisce la tabella su cui deve essere effettuato l'update
	 * es: UPDATE table set ...
	 * 
	 * @param nomeTabella Nome della tabella
	 */
	public ISQLQueryObject addUpdateTable(String nomeTabella) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge un field per l'update
	 * es: UPDATE table set nomeField=valueField WHERE ...
	 * 
	 * @param nomeField Nome del Field
	 */
	public ISQLQueryObject addUpdateField(String nomeField,String valueField) throws SQLQueryObjectException;
	
	/**
	 * Crea una SQL per una operazione di Update con i dati dell'oggetto
	 * 
	 * @return SQL per una operazione di Update
	 */
	public String createSQLUpdate() throws SQLQueryObjectException;
	
	
	
	
	
	/* ---------------- INSERT ------------------ */
	
	/**
	 * Definisce la tabella su cui deve essere effettuato l'insert
	 * es: INSERT INTO table (XX) VALUES (VV) 
	 * 
	 * @param nomeTabella Nome della tabella
	 */
	public ISQLQueryObject addInsertTable(String nomeTabella) throws SQLQueryObjectException;
	
	/**
	 * Aggiunge un field per la insert
	 * es: INSERT INTO table (nomeField) VALUES (valueField)
	 * 
	 * @param nomeField Nome del Field
	 */
	public ISQLQueryObject addInsertField(String nomeField,String valueField) throws SQLQueryObjectException;
	
	/**
	 * Crea una SQL per una operazione di Insert con i dati dell'oggetto
	 * 
	 * @return SQL per una operazione di Insert
	 */
	public String createSQLInsert() throws SQLQueryObjectException;
	
	
	
	
	
	
	/* ---------------- DELETE ------------------ */
	
	/**
	 * Aggiunge una tabella di ricerca del from
	 * es: DELETE from tabella
	 * 
	 * @param tabella
	 */
	public ISQLQueryObject addDeleteTable(String tabella) throws SQLQueryObjectException;
	
	
	/**
	 * Crea una SQL per una operazione di Delete con i dati dell'oggetto
	 * 
	 * @return SQL per una operazione di Delete
	 */
	public String createSQLDelete() throws SQLQueryObjectException;
	
	
	
	
	
	/* ---------------- WHERE CONDITIONS ------------------ */
	
	/**
	 * Crea le condizioni presenti senza anteporre il comando (INSERT,CREATE,UPDATE), le tabelle interessate e il 'WHERE'
	 * 
	 * @return SQL Query
	 */
	public String createSQLConditions() throws SQLQueryObjectException;	
	
	
	
	
	/* ---------------- NEW SQL QUERY OBJECT ------------------ */
	
	/**
	 * Inizializza un nuovo SQLQueryObject
	 * 
	 * @return SQLQueryObject
	 * 
	 * @throws SQLQueryObjectException
	 */
	public ISQLQueryObject newSQLQueryObject() throws SQLQueryObjectException;	
	
	/**
	 * Indicazione sul tipo di database
	 * 
	 * @return tipo di database
	 * 
	 * @throws SQLQueryObjectException
	 */
	public String getTipoDatabase() throws SQLQueryObjectException;	
	
	/**
	 * Indicazione sul tipo di database
	 * 
	 * @return tipo di database
	 * 
	 * @throws SQLQueryObjectException
	 */
	public TipiDatabase getTipoDatabaseOpenSPCoop2() throws SQLQueryObjectException;	
}
