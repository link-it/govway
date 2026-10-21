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
package org.openspcoop2.protocol.basic.registry.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.protocol.basic.registry.IdentificazionePortaApplicativa;
import org.openspcoop2.protocol.basic.registry.IdentificazionePortaDelegata;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaPorteApplicative;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaPorteDelegate;
import org.openspcoop2.protocol.sdk.registry.RegistryException;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.protocol.sdk.state.RequestConfig;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
 * Verifica l'utilizzo della cache, mantenuta nella RequestConfig, delle porte delegate per azione
 * ({@link IdentificazionePortaApplicativa#find(String)} e {@link IdentificazionePortaDelegata#find(String)}):
 * il risultato della ricerca (anche vuoto) deve essere riutilizzato dalle richieste successive senza interrogare
 * nuovamente la configurazione, mentre gli errori non devono essere mantenuti.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IdentificazionePortaCacheTest {

	public static void main(String[] args) throws Exception {
		test();
	}

	private static int verifiche = 0;
	private static final Logger LOG = LoggerWrapperFactory.getLogger(IdentificazionePortaCacheTest.class);

	public static void test() throws Exception {
		verifiche = 0;
		testRequestConfig();
		testPortaApplicativa();
		testPortaDelegata();
		System.out.println("Test cache identificazione porte per azione completato: "+verifiche+" verifiche effettuate");
	}


	/* ********** RequestConfig ********* */

	private static void testRequestConfig() throws Exception {
		RequestConfig rc = new RequestConfig();

		ProtocolFiltroRicercaPorteApplicative filtroPA = new ProtocolFiltroRicercaPorteApplicative();
		filtroPA.setNomePortaDelegante("pa");
		filtroPA.setAzione("azione");
		BooleanNullable flag = BooleanNullable.NULL();
		List<IDPortaApplicativa> listPA = rc.getPorteApplicativeByFiltroRicerca(filtroPA, flag);
		check("PA: cache non inizializzata", flag.getValue()==null && listPA!=null && listPA.isEmpty());
		rc.addPorteApplicativeByFiltroRicerca(filtroPA, new ArrayList<>(), "id");
		flag = BooleanNullable.NULL();
		listPA = rc.getPorteApplicativeByFiltroRicerca(filtroPA, flag);
		check("PA: lista vuota in cache", Boolean.TRUE.equals(flag.getValue()) && listPA!=null && listPA.isEmpty());
		ProtocolFiltroRicercaPorteApplicative altroFiltroPA = new ProtocolFiltroRicercaPorteApplicative();
		altroFiltroPA.setNomePortaDelegante("pa");
		altroFiltroPA.setAzione("altraAzione");
		flag = BooleanNullable.NULL();
		listPA = rc.getPorteApplicativeByFiltroRicerca(altroFiltroPA, flag);
		check("PA: filtro non presente in cache", flag.getValue()==null && listPA==null);

		ProtocolFiltroRicercaPorteDelegate filtroPD = new ProtocolFiltroRicercaPorteDelegate();
		filtroPD.setNomePortaDelegante("pd");
		filtroPD.setAzione("azione");
		flag = BooleanNullable.NULL();
		List<IDPortaDelegata> listPD = rc.getPorteDelegateByFiltroRicerca(filtroPD, flag);
		check("PD: cache non inizializzata", flag.getValue()==null && listPD!=null && listPD.isEmpty());
		List<IDPortaDelegata> trovate = new ArrayList<>();
		trovate.add(idPD("pd_azione"));
		rc.addPorteDelegateByFiltroRicerca(filtroPD, trovate, "id");
		flag = BooleanNullable.NULL();
		listPD = rc.getPorteDelegateByFiltroRicerca(filtroPD, flag);
		check("PD: lista in cache", Boolean.TRUE.equals(flag.getValue()) && listPD!=null && listPD.size()==1 && "pd_azione".equals(listPD.get(0).getNome()));
		ProtocolFiltroRicercaPorteDelegate altroFiltroPD = new ProtocolFiltroRicercaPorteDelegate();
		altroFiltroPD.setNomePortaDelegante("pd");
		altroFiltroPD.setAzione("altraAzione");
		flag = BooleanNullable.NULL();
		listPD = rc.getPorteDelegateByFiltroRicerca(altroFiltroPD, flag);
		check("PD: filtro non presente in cache", flag.getValue()==null && listPD==null);
	}


	/* ********** Lettore della configurazione simulato ********* */

	/** Configurazione simulata: porte per azione definite, errore da restituire e conteggio delle invocazioni */
	private static class Configurazione {
		private final Map<String, String> porteAzione = new HashMap<>(); // azione -> nome porta
		private boolean errore = false;
		private final Map<String, Integer> invocazioni = new HashMap<>();
		int invocazioni(String metodo) {
			return this.invocazioni.getOrDefault(metodo, 0);
		}
		void reset() {
			this.invocazioni.clear();
		}
	}

	private static IConfigIntegrationReader reader(Configurazione conf) {
		InvocationHandler h = (proxy, method, args) -> {
			String name = method.getName();
			conf.invocazioni.merge(name, 1, Integer::sum);
			switch (name) {
				case "findIdPorteApplicative": {
					String azione = ((ProtocolFiltroRicercaPorteApplicative) args[0]).getAzione();
					if(conf.errore) {
						throw new RegistryException("errore simulato");
					}
					if(!conf.porteAzione.containsKey(azione)) {
						throw new RegistryNotFound("non trovata");
					}
					List<IDPortaApplicativa> l = new ArrayList<>();
					l.add(idPA(conf.porteAzione.get(azione)));
					return l;
				}
				case "getPortaApplicativa": {
					PortaApplicativa pa = new PortaApplicativa();
					pa.setNome(((IDPortaApplicativa) args[0]).getNome());
					return pa;
				}
				case "findIdPorteDelegate": {
					String azione = ((ProtocolFiltroRicercaPorteDelegate) args[0]).getAzione();
					if(conf.errore) {
						throw new RegistryException("errore simulato");
					}
					if(!conf.porteAzione.containsKey(azione)) {
						throw new RegistryNotFound("non trovata");
					}
					List<IDPortaDelegata> l = new ArrayList<>();
					l.add(idPD(conf.porteAzione.get(azione)));
					return l;
				}
				case "getPortaDelegata": {
					PortaDelegata pd = new PortaDelegata();
					pd.setNome(((IDPortaDelegata) args[0]).getNome());
					return pd;
				}
				default:
					throw new UnsupportedOperationException(name);
			}
		};
		return (IConfigIntegrationReader) Proxy.newProxyInstance(IdentificazionePortaCacheTest.class.getClassLoader(), new Class<?>[] {IConfigIntegrationReader.class}, h);
	}

	private static RequestInfo requestInfo(RequestConfig rc) {
		RequestInfo ri = new RequestInfo();
		ri.setIdTransazione("id-test");
		ri.setRequestConfig(rc);
		return ri;
	}


	/* ********** Porta Applicativa ********* */

	@FunctionalInterface
	private interface Identificazione {
		/** Esegue la find e restituisce il nome della porta per azione individuata (null se non presente) */
		String find(String azione) throws Exception;
	}

	private static Identificazione identificazionePA(IConfigIntegrationReader reader, RequestInfo ri) throws Exception {
		PortaApplicativa pa = new PortaApplicativa();
		pa.setNome("pa_default");
		pa.setRicercaPortaAzioneDelegata(StatoFunzionalita.ABILITATO);
		return azione -> {
			// una nuova istanza per ogni richiesta, come avviene nel gateway
			IdentificazionePortaApplicativa id = new IdentificazionePortaApplicativa(LOG, null, (IRegistryReader) null, reader, ri, pa);
			if(!id.find(azione)) {
				return "ERRORE";
			}
			IDPortaApplicativa idPA = id.getIDPortaApplicativa(azione);
			PortaApplicativa paAzione = id.getPortaApplicativa(azione);
			if(idPA==null) {
				return paAzione==null ? null : "INCOERENTE";
			}
			return paAzione!=null && idPA.getNome().equals(paAzione.getNome()) ? idPA.getNome() : "INCOERENTE";
		};
	}

	private static Identificazione identificazionePD(IConfigIntegrationReader reader, RequestInfo ri) throws Exception {
		PortaDelegata pd = new PortaDelegata();
		pd.setNome("pd_default");
		pd.setRicercaPortaAzioneDelegata(StatoFunzionalita.ABILITATO);
		return azione -> {
			IdentificazionePortaDelegata id = new IdentificazionePortaDelegata(LOG, null, (IRegistryReader) null, reader, ri, pd);
			if(!id.find(azione)) {
				return "ERRORE";
			}
			IDPortaDelegata idPD = id.getIDPortaDelegata(azione);
			PortaDelegata pdAzione = id.getPortaDelegata(azione);
			if(idPD==null) {
				return pdAzione==null ? null : "INCOERENTE";
			}
			return pdAzione!=null && idPD.getNome().equals(pdAzione.getNome()) ? idPD.getNome() : "INCOERENTE";
		};
	}

	private static void testPortaApplicativa() throws Exception {
		verificaPorta("PA", "findIdPorteApplicative", "getPortaApplicativa", IdentificazionePortaCacheTest::identificazionePA);
	}
	private static void testPortaDelegata() throws Exception {
		verificaPorta("PD", "findIdPorteDelegate", "getPortaDelegata", IdentificazionePortaCacheTest::identificazionePD);
	}

	@FunctionalInterface
	private interface IdentificazioneFactory {
		Identificazione create(IConfigIntegrationReader reader, RequestInfo ri) throws Exception;
	}

	private static void verificaPorta(String tipo, String metodoRicerca, String metodoLettura, IdentificazioneFactory factory) throws Exception {
		Configurazione conf = new Configurazione();
		conf.porteAzione.put("azioneSpecifica", tipo.toLowerCase()+"_azioneSpecifica");
		IConfigIntegrationReader reader = reader(conf);

		// 1) senza RequestConfig: la ricerca viene effettuata ad ogni richiesta
		Identificazione senzaCache = factory.create(reader, requestInfo(null));
		for (int i = 0; i < 3; i++) {
			checkEquals(tipo+": senza RequestConfig, nessuna porta per azione #"+i, null, senzaCache.find("azione"));
		}
		check(tipo+": senza RequestConfig, ricerca ad ogni richiesta", conf.invocazioni(metodoRicerca)==3);

		// 2) con RequestConfig: porta per azione non presente (lista vuota), ricerca effettuata solo alla prima richiesta
		conf.reset();
		RequestConfig rc = new RequestConfig();
		Identificazione conCache = factory.create(reader, requestInfo(rc));
		for (int i = 0; i < 3; i++) {
			checkEquals(tipo+": nessuna porta per azione #"+i, null, conCache.find("azione"));
		}
		check(tipo+": porta per azione non presente, una sola ricerca ("+conf.invocazioni(metodoRicerca)+")", conf.invocazioni(metodoRicerca)==1);

		// 3) porta per azione presente: una sola ricerca, la porta viene letta ad ogni richiesta (come in precedenza)
		for (int i = 0; i < 3; i++) {
			checkEquals(tipo+": porta per azione #"+i, tipo.toLowerCase()+"_azioneSpecifica", conCache.find("azioneSpecifica"));
		}
		check(tipo+": porta per azione presente, una sola ricerca per azione", conf.invocazioni(metodoRicerca)==2);
		check(tipo+": porta per azione letta ad ogni richiesta", conf.invocazioni(metodoLettura)==3);

		// 4) errori: non mantenuti in cache, la ricerca viene ripetuta
		conf.reset();
		conf.errore = true;
		for (int i = 0; i < 2; i++) {
			checkEquals(tipo+": errore #"+i, "ERRORE", conCache.find("azioneConErrore"));
		}
		check(tipo+": errore non mantenuto in cache", conf.invocazioni(metodoRicerca)==2);
		conf.errore = false;
		checkEquals(tipo+": dopo l'errore", null, conCache.find("azioneConErrore"));
		checkEquals(tipo+": dopo l'errore (cache)", null, conCache.find("azioneConErrore"));
		check(tipo+": dopo l'errore, una sola nuova ricerca", conf.invocazioni(metodoRicerca)==3);

		// 5) nuova RequestConfig (es. dopo l'invalidazione della cache): la ricerca viene rieffettuata e vede la nuova configurazione
		conf.reset();
		conf.porteAzione.put("azione", tipo.toLowerCase()+"_nuova");
		checkEquals(tipo+": RequestConfig esistente, risultato in cache", null, conCache.find("azione"));
		check(tipo+": RequestConfig esistente, nessuna ricerca", conf.invocazioni(metodoRicerca)==0);
		Identificazione dopoReset = factory.create(reader, requestInfo(new RequestConfig()));
		checkEquals(tipo+": nuova RequestConfig, nuova porta per azione", tipo.toLowerCase()+"_nuova", dopoReset.find("azione"));
		check(tipo+": nuova RequestConfig, ricerca effettuata", conf.invocazioni(metodoRicerca)==1);
	}


	/* ********** UTILITY ********* */

	private static IDPortaApplicativa idPA(String nome) {
		IDPortaApplicativa id = new IDPortaApplicativa();
		id.setNome(nome);
		return id;
	}
	private static IDPortaDelegata idPD(String nome) {
		IDPortaDelegata id = new IDPortaDelegata();
		id.setNome(nome);
		return id;
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
