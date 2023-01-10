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

package org.openspcoop2.core.transazioni.utils;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.ExpressionUtils;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.expression.IExpression;


/**     
 * TransazioniIndexUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransazioniIndexUtils {

	private static final String PROP_NAME_SOLO_COLONNE_INDICIZZATE_FULL_INDEX_SEARCH = "SoloColonneIndiceFullIndexSearch";
	private static final String PROP_NAME_SOLO_COLONNE_INDICIZZATE_FULL_INDEX_STATS = "SoloColonneIndiceFullIndexStats";
	
	public static void enableSoloColonneIndicizzateFullIndexSearch(IExpression expr) {
		ExpressionUtils.enable(expr, PROP_NAME_SOLO_COLONNE_INDICIZZATE_FULL_INDEX_SEARCH);
	}
	public static boolean isEnabledSoloColonneIndicizzateFullIndexSearch(IExpression expr) {
		return ExpressionUtils.isEnabled(expr, PROP_NAME_SOLO_COLONNE_INDICIZZATE_FULL_INDEX_SEARCH);
	}
	
	public static void enableSoloColonneIndicizzateFullIndexStats(IExpression expr) {
		ExpressionUtils.enable(expr, PROP_NAME_SOLO_COLONNE_INDICIZZATE_FULL_INDEX_STATS);
	}
	public static boolean isEnabledSoloColonneIndicizzateFullIndexStats(IExpression expr) {
		return ExpressionUtils.isEnabled(expr, PROP_NAME_SOLO_COLONNE_INDICIZZATE_FULL_INDEX_STATS);
	}
	
	
	// -- CREATE INDEX INDEX_TR_SEARCH ON transazioni 	
	public static List<IField> LISTA_COLONNE_INDEX_TR_SEARCH = new ArrayList<IField>();
	static {
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().DATA_INGRESSO_RICHIESTA);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().ESITO);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().ESITO_CONTESTO);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().PDD_RUOLO);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().PDD_CODICE);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().TIPO_SOGGETTO_EROGATORE);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().NOME_SOGGETTO_EROGATORE);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().TIPO_SERVIZIO);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().NOME_SERVIZIO);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().VERSIONE_SERVIZIO);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().AZIONE);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().TIPO_SOGGETTO_FRUITORE);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().NOME_SOGGETTO_FRUITORE);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().TRASPORTO_MITTENTE);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().TOKEN_ISSUER);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().TOKEN_CLIENT_ID);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().TOKEN_SUBJECT);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().TOKEN_USERNAME);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().TOKEN_MAIL);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().ID_CORRELAZIONE_APPLICATIVA);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().ID_CORRELAZIONE_APPLICATIVA_RISPOSTA);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().PROTOCOLLO);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().CLIENT_ADDRESS);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().GRUPPI);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().URI_API);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().EVENTI_GESTIONE);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().CLUSTER_ID);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().ID_TRANSAZIONE);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().DATA_USCITA_RICHIESTA);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().DATA_INGRESSO_RISPOSTA);
		LISTA_COLONNE_INDEX_TR_SEARCH.add(Transazione.model().DATA_USCITA_RISPOSTA);

	}
	
	// -- CREATE INDEX INDEX_TR_STATS ON transazioni 
	public static List<IField> LISTA_COLONNE_INDEX_TR_STATS = new ArrayList<IField>();
	static {
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().DATA_INGRESSO_RICHIESTA);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().PDD_RUOLO);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().PDD_CODICE);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().TIPO_SOGGETTO_FRUITORE);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().NOME_SOGGETTO_FRUITORE);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().TIPO_SOGGETTO_EROGATORE);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().NOME_SOGGETTO_EROGATORE);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().TIPO_SERVIZIO);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().NOME_SERVIZIO);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().VERSIONE_SERVIZIO);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().AZIONE);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().TRASPORTO_MITTENTE);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().TOKEN_ISSUER);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().TOKEN_CLIENT_ID);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().TOKEN_SUBJECT);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().TOKEN_USERNAME);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().TOKEN_MAIL);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().ESITO);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().ESITO_CONTESTO);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().CLIENT_ADDRESS);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().GRUPPI);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().URI_API);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().DATA_USCITA_RICHIESTA);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().DATA_INGRESSO_RISPOSTA);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().DATA_USCITA_RISPOSTA);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().RICHIESTA_INGRESSO_BYTES);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().RICHIESTA_USCITA_BYTES);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().RISPOSTA_INGRESSO_BYTES);
		LISTA_COLONNE_INDEX_TR_STATS.add(Transazione.model().RISPOSTA_USCITA_BYTES);
	}
}