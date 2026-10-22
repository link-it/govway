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
package org.openspcoop2.pdd.logger.test;

import java.util.HashMap;
import java.util.Map;
import java.util.SplittableRandom;

import org.openspcoop2.pdd.logger.MsgDiagnostico;

/**
 * Verifica {@link MsgDiagnostico#replaceKeywords(String, Map)}: il risultato deve coincidere con quello
 * dell'algoritmo originale (scansione carattere per carattere) per qualsiasi messaggio, comprese keyword
 * non presenti nella mappa, non terminate, vuote ('@@') e valori che contengono il separatore.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ReplaceKeywordsTest {

	public static void main(String[] args) throws Exception {
		test();
	}

	private static int verifiche = 0;

	public static void test() throws Exception {
		verifiche = 0;

		Map<String,String> keywords = new HashMap<>();
		keywords.put("@a@", "valoreA");
		keywords.put("@ab@", "");
		keywords.put("@@", "vuota");
		keywords.put("@b@", "con@separatore");
		keywords.put("@idMessaggioRichiesta@", "c8e2f2a5-3b2a-4b1e-9a4e-1f2d3c4b5a69");
		keywords.put("@nomePorta@", "gw_ENTE/gw_TEST/v1");

		// casi notevoli
		String[] casi = {
			"", "@", "@@", "@@@", "@@@@", "a", "testo senza keyword",
			"@a@", "@a", "a@", "x@a@y", "@a@@a@", "@a@b@", "@ab@", "@c@", "@c@@a@",
			"prima @a@ in mezzo @b@ dopo", "keyword @non@ presente", "non terminata @a",
			"Ricevuta richiesta applicativa id[@idMessaggioRichiesta@] porta[@nomePorta@]",
			"email nome@dominio.it senza keyword", "@idMessaggioRichiesta@@nomePorta@",
		};
		for (String msg : casi) {
			check(msg, keywords);
		}

		// messaggi casuali su un alfabeto ridotto (molti separatori)
		SplittableRandom random = new SplittableRandom(20261021L);
		char[] alfabeto = {'@', '@', 'a', 'b', 'c', ' ', 'x'};
		for (int i = 0; i < 200000; i++) {
			int len = random.nextInt(40);
			StringBuilder sb = new StringBuilder(len);
			for (int j = 0; j < len; j++) {
				sb.append(alfabeto[random.nextInt(alfabeto.length)]);
			}
			check(sb.toString(), keywords);
		}

		// mappa vuota
		Map<String,String> vuota = new HashMap<>();
		for (String msg : casi) {
			check(msg, vuota);
		}

		// un messaggio senza keyword viene restituito senza copie
		String senzaKeyword = "messaggio senza keyword";
		if(!stessaIstanza(MsgDiagnostico.replaceKeywords(senzaKeyword, keywords), senzaKeyword)) {
			throw new Exception("Messaggio senza keyword: atteso la stessa istanza");
		}
		verifiche++;

		System.out.println("Test replaceKeywords completato: "+verifiche+" verifiche effettuate");
	}

	/** Confronto di identita' (non di contenuto): verifica che non venga creata una copia */
	private static boolean stessaIstanza(Object a, Object b) {
		return a == b;
	}

	private static void check(String msg, Map<String,String> keywords) throws Exception {
		String atteso = replaceKeywordsOriginale(msg, keywords);
		String ottenuto = MsgDiagnostico.replaceKeywords(msg, keywords);
		if(!atteso.equals(ottenuto)) {
			throw new Exception("Messaggio ["+msg+"]: atteso ["+atteso+"] ottenuto ["+ottenuto+"]");
		}
		verifiche++;
	}

	/** algoritmo originale di MsgDiagnostico.replaceKeywords, utilizzato come riferimento */
	private static String replaceKeywordsOriginale(String msg, Map<String,String> keywordLogPersonalizzati) {
		StringBuilder bf = new StringBuilder();
		StringBuilder keyword = new StringBuilder();
		boolean separator = false;
		char separatorChar = '@';
		for(int i=0; i<msg.length(); i++){
			char ch = msg.charAt(i);
			if(separatorChar == ch){
				if(!separator){
					keyword.append(separatorChar);
					separator = true;
				}
				else{
					keyword.append(separatorChar);
					String valoreRimpiazzato = keywordLogPersonalizzati.get(keyword.toString());
					if(valoreRimpiazzato==null){
						bf.append(keyword.toString());
					}else{
						bf.append(valoreRimpiazzato);
					}
					keyword.setLength(0);
					separator=false;
				}
			}else{
				if(separator){
					keyword.append(ch);
				}else{
					bf.append(ch);
				}
			}
		}
		if ( separator )
			bf.append(keyword.toString());
		return bf.toString();
	}
}
