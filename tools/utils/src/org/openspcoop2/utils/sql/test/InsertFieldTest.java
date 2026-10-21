/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.sql.test;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * Verifica l'aggiunta dei field di una insert ({@link ISQLQueryObject#addInsertField(String, String)}):
 * SQL prodotto, ordine dei field e controllo dei duplicati (stesso messaggio di errore, oggetto invariato dopo l'errore)
 * per tutti i tipi di database.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InsertFieldTest {

	public static void main(String[] args) throws Exception {
		test();
	}

	private static final int NUMERO_FIELD = 150;

	public static void test() throws Exception {
		int verifiche = 0;
		for (TipiDatabase tipo : TipiDatabase.values()) {
			if(TipiDatabase.DEFAULT.equals(tipo)) {
				continue;
			}

			// SQL con molti field: ordine dei nomi e dei valori invariato
			ISQLQueryObject sql = SQLObjectFactory.createSQLQueryObject(tipo);
			sql.addInsertTable("tabella");
			StringBuilder nomi = new StringBuilder();
			StringBuilder valori = new StringBuilder();
			for (int i = 0; i < NUMERO_FIELD; i++) {
				// nomi che differiscono solo per maiuscole/minuscole sono field distinti (confronto case sensitive)
				String nome = (i%2==0) ? "colonna_"+i : "COLONNA_"+(i-1);
				String valore = (i%3==0) ? "?" : "'v"+i+"'";
				sql.addInsertField(nome, valore);
				if(i>0) {
					nomi.append(",");
					valori.append(",");
				}
				nomi.append(nome);
				valori.append(valore);
			}
			String atteso = "INSERT INTO tabella ("+nomi+") VALUES ("+valori+")";
			check(tipo+": SQL di insert", atteso, sql.createSQLInsert());
			verifiche++;

			// field duplicato (primo, intermedio, ultimo): stesso errore di prima e oggetto invariato
			for (String duplicato : new String[] {"colonna_0", "COLONNA_"+(NUMERO_FIELD/2-1), "COLONNA_"+(NUMERO_FIELD-2)}) {
				String errore = null;
				try {
					sql.addInsertField(duplicato, "?");
				}catch(SQLQueryObjectException e) {
					errore = e.getMessage();
				}
				check(tipo+": errore per field duplicato "+duplicato, "Field name "+duplicato+" gia inserito tra gli insert fields", errore);
				check(tipo+": SQL invariato dopo il duplicato "+duplicato, atteso, sql.createSQLInsert());
				verifiche+=2;
			}

			// controlli sui parametri invariati
			check(tipo+": nome null", "Field name is null or empty string", errore(sql, null, "?"));
			check(tipo+": nome vuoto", "Field name is null or empty string", errore(sql, "", "?"));
			check(tipo+": valore null", "Field value is null", errore(sql, "colonna_nuova", null));
			verifiche+=3;
			// un field rifiutato per valore null non deve risultare inserito
			sql.addInsertField("colonna_nuova", "?");
			check(tipo+": field aggiunto dopo l'errore", atteso.replace(") VALUES (", ",colonna_nuova) VALUES (").replace(valori+")", valori+",?)"), sql.createSQLInsert());
			verifiche++;

			// un nuovo oggetto (newSQLQueryObject) non eredita i field già inseriti
			ISQLQueryObject nuovo = sql.newSQLQueryObject();
			nuovo.addInsertTable("tabella2");
			nuovo.addInsertField("colonna_0", "?");
			check(tipo+": nuovo oggetto indipendente", "INSERT INTO tabella2 (colonna_0) VALUES (?)", nuovo.createSQLInsert());
			verifiche++;
		}
		System.out.println("Test insert field completato: "+verifiche+" verifiche effettuate");
	}

	private static String errore(ISQLQueryObject sql, String nome, String valore) {
		try {
			sql.addInsertField(nome, valore);
			return null;
		}catch(SQLQueryObjectException e) {
			return e.getMessage();
		}
	}

	private static void check(String descrizione, String atteso, String ottenuto) throws Exception {
		if(atteso==null ? ottenuto!=null : !atteso.equals(ottenuto)) {
			throw new Exception("Verifica fallita: "+descrizione+"\natteso:   "+atteso+"\nottenuto: "+ottenuto);
		}
	}
}
