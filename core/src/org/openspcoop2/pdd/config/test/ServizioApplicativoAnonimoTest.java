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
package org.openspcoop2.pdd.config.test;

import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.protocol.sdk.state.RequestFruitore;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.RequestThreadContext;

/**
 * Verifica {@link ConfigurazionePdDManager#getServizioApplicativo(IDServizioApplicativo, RequestInfo, boolean)}:
 * per il servizio applicativo anonimo del fruitore restituisce null invece di sollevare l'eccezione,
 * mentre in tutti gli altri casi si comporta come {@link ConfigurazionePdDManager#getServizioApplicativo(IDServizioApplicativo, RequestInfo)}.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServizioApplicativoAnonimoTest {

	public static void main(String[] args) throws Exception {
		test();
	}

	private static int verifiche = 0;

	public static void test() throws Exception {
		verifiche = 0;
		ConfigurazionePdDManager manager = new ConfigurazionePdDManager();
		IDSoggetto fruitore = new IDSoggetto("gw", "Fruitore");

		// 1) applicativo anonimo del fruitore
		IDServizioApplicativo idAnonimo = idSA(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO, fruitore);
		RequestInfo riAnonimo = requestInfo(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO, null, null);
		checkEquals("anonimo: comportamento originale", "ERRORE org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound: Servizio applicativo anonimo",
				risolvi(() -> manager.getServizioApplicativo(idAnonimo, riAnonimo)));
		checkEquals("anonimo: con eccezione richiesta", "ERRORE org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound: Servizio applicativo anonimo",
				risolvi(() -> manager.getServizioApplicativo(idAnonimo, riAnonimo, true)));
		checkEquals("anonimo: senza eccezione", null, risolvi(() -> manager.getServizioApplicativo(idAnonimo, riAnonimo, false)));

		// 2) applicativo identificato tramite le credenziali di trasporto: restituito in entrambi i casi
		IDServizioApplicativo idApplicativo = idSA("Applicativo", fruitore);
		ServizioApplicativo applicativo = new ServizioApplicativo();
		applicativo.setNome("Applicativo");
		RequestInfo riIdentificato = requestInfo(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO, idApplicativo, applicativo);
		checkEquals("identificato: comportamento originale", "Applicativo", risolvi(() -> manager.getServizioApplicativo(idApplicativo, riIdentificato)));
		checkEquals("identificato: senza eccezione", "Applicativo", risolvi(() -> manager.getServizioApplicativo(idApplicativo, riIdentificato, false)));

		// 3) applicativo non anonimo e non presente nel contesto: entrambe le varianti proseguono con la lettura della configurazione (stesso esito)
		IDServizioApplicativo idAltro = idSA("Altro", fruitore);
		String originale = risolvi(() -> manager.getServizioApplicativo(idAltro, riAnonimo));
		String variante = risolvi(() -> manager.getServizioApplicativo(idAltro, riAnonimo, false));
		check("non anonimo: lettura della configurazione ("+originale+")", originale!=null && !originale.contains("Servizio applicativo anonimo"));
		checkEquals("non anonimo: stesso esito", originale, variante);

		// 4) nome 'anonimo' ma fruitore non anonimo nel contesto: nessuna differenza tra le varianti
		RequestInfo riSenzaAnonimo = requestInfo(null, null, null);
		checkEquals("contesto senza anonimo: stesso esito", risolvi(() -> manager.getServizioApplicativo(idAnonimo, riSenzaAnonimo)),
				risolvi(() -> manager.getServizioApplicativo(idAnonimo, riSenzaAnonimo, false)));

		System.out.println("Test servizio applicativo anonimo completato: "+verifiche+" verifiche effettuate");
	}

	private static IDServizioApplicativo idSA(String nome, IDSoggetto proprietario) {
		IDServizioApplicativo id = new IDServizioApplicativo();
		id.setNome(nome);
		id.setIdSoggettoProprietario(proprietario);
		return id;
	}

	private static RequestInfo requestInfo(String anonimo, IDServizioApplicativo idApplicativo, ServizioApplicativo applicativo) {
		RequestFruitore rf = new RequestFruitore();
		rf.setServizioApplicativoFruitoreAnonimo(anonimo);
		rf.setIdServizioApplicativoFruitore(idApplicativo);
		rf.setServizioApplicativoFruitore(applicativo);
		RequestThreadContext ctx = new RequestThreadContext("test", null);
		ctx.setRequestFruitoreTrasportoInfo(rf);
		RequestInfo ri = new RequestInfo();
		ri.setRequestThreadContext(ctx);
		return ri;
	}

	@FunctionalInterface
	private interface Lettura {
		ServizioApplicativo leggi() throws Exception;
	}
	private static String risolvi(Lettura l) {
		try {
			ServizioApplicativo sa = l.leggi();
			return sa==null ? null : sa.getNome();
		}catch(Exception e) {
			return "ERRORE "+e.getClass().getName()+": "+e.getMessage();
		}
	}

	private static void check(String descrizione, boolean condizione) throws Exception {
		verifiche++;
		if(!condizione) {
			throw new Exception("Verifica fallita: "+descrizione);
		}
	}
	private static void checkEquals(String descrizione, String atteso, String ottenuto) throws Exception {
		verifiche++;
		if(atteso==null ? ottenuto!=null : !atteso.equals(ottenuto)) {
			throw new Exception("Verifica fallita: "+descrizione+"\natteso:   "+atteso+"\nottenuto: "+ottenuto);
		}
	}
}
