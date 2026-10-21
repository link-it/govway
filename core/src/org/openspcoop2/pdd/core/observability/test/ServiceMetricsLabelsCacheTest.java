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
package org.openspcoop2.pdd.core.observability.test;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.pdd.core.observability.ServiceMetricsLabels;
import org.openspcoop2.protocol.sdk.state.RequestConfig;
import org.openspcoop2.protocol.sdk.state.RequestInfo;

/**
 * Verifica la cache delle label delle metriche di dettaglio ({@link ServiceMetricsLabels#build}) nella {@link RequestConfig}:
 * chiave per gruppo (porta) e azione, label restituite dalla cache senza essere ricostruite, condivisione della cache
 * tra le copie della RequestConfig e assenza di cache se il gruppo non e' identificato.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServiceMetricsLabelsCacheTest {

	public static void main(String[] args) throws Exception {
		test();
	}

	private static int verifiche = 0;

	public static void test() throws Exception {
		verifiche = 0;
		IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromValuesWithoutCheck("StressTestRest", "gw", "ENTE", 1);

		// 1) erogazione: chiave per gruppo e azione
		RequestConfig rcPA = new RequestConfig();
		rcPA.setIdPortaApplicativa(idPA("gw_ENTE/gw_StressTestRestProxy/v1"));
		check("chiave erogazione", "gw_ENTE/gw_StressTestRestProxy/v1#PUT_test#api", cacheKey(rcPA, "PUT_test", idAccordo));
		check("chiave erogazione senza api", "gw_ENTE/gw_StressTestRestProxy/v1#PUT_test#", cacheKey(rcPA, "PUT_test", null));
		check("chiave erogazione senza azione", "gw_ENTE/gw_StressTestRestProxy/v1##api", cacheKey(rcPA, null, idAccordo));
		check("chiave per azione differente", "false", String.valueOf(cacheKey(rcPA, "PUT_test", idAccordo).equals(cacheKey(rcPA, "PUT_test2", idAccordo))));

		// 2) label presenti nella RequestConfig: restituite senza ricostruzione (stessa istanza)
		Map<String,String> labels = Collections.unmodifiableMap(new LinkedHashMap<>(Map.of("service", "StressTestRestProxy")));
		rcPA.addMetricsLabels(cacheKey(rcPA, "PUT_test", idAccordo), labels);
		RequestInfo ri = new RequestInfo();
		ri.setRequestConfig(rcPA);
		checkSame("label dalla cache", labels, ServiceMetricsLabels.build(ri, "PUT_test", idAccordo));

		// 3) le copie della RequestConfig condividono la cache delle label
		RequestConfig copia = new RequestConfig();
		copia.copyFrom(rcPA);
		RequestInfo riCopia = new RequestInfo();
		riCopia.setRequestConfig(copia);
		checkSame("label dalla copia", labels, ServiceMetricsLabels.build(riCopia, "PUT_test", idAccordo));

		// 4) fruizione: chiave sul gruppo della porta delegata
		RequestConfig rcPD = new RequestConfig();
		rcPD.setIdPortaDelegataDefault(idPD("gw_ENTE/gw_ENTE/gw_StressTestRestProxyFruitore/v1"));
		rcPD.setIdPortaDelegata(idPD("__gw_ENTE/gw_ENTE/gw_StressTestRestProxyFruitore/v1__Specific1"));
		check("chiave fruizione", "__gw_ENTE/gw_ENTE/gw_StressTestRestProxyFruitore/v1__Specific1#GET_x#api", cacheKey(rcPD, "GET_x", idAccordo));

		// 5) gruppo non identificato: nessuna cache
		check("chiave senza porta", "null", String.valueOf(cacheKey(new RequestConfig(), "PUT_test", idAccordo)));
		RequestConfig rcFruizioneSenzaGruppo = new RequestConfig();
		rcFruizioneSenzaGruppo.setIdPortaDelegataDefault(idPD("gw_ENTE/gw_ENTE/gw_X/v1"));
		check("chiave fruizione senza gruppo", "null", String.valueOf(cacheKey(rcFruizioneSenzaGruppo, "PUT_test", idAccordo)));
		check("chiave senza RequestConfig", "null", String.valueOf(cacheKey(null, "PUT_test", idAccordo)));

		// 6) label in cache non modificabili
		try {
			ServiceMetricsLabels.build(ri, "PUT_test", idAccordo).put("x", "y");
			throw new Exception("Le label in cache devono essere non modificabili");
		}catch(UnsupportedOperationException e) {
			verifiche++;
		}

		System.out.println("Test cache delle label delle metriche completato: "+verifiche+" verifiche effettuate");
	}

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
	private static String cacheKey(RequestConfig rc, String action, IDAccordo idAccordo) throws Exception {
		Method m = ServiceMetricsLabels.class.getDeclaredMethod("getCacheKey", RequestConfig.class, String.class, IDAccordo.class);
		m.setAccessible(true);
		return (String) m.invoke(null, rc, action, idAccordo);
	}
	private static void check(String nome, String atteso, String ottenuto) throws Exception {
		if(!atteso.equals(ottenuto)) {
			throw new Exception("Verifica '"+nome+"' fallita: atteso ["+atteso+"] ottenuto ["+ottenuto+"]");
		}
		verifiche++;
	}
	private static void checkSame(String nome, Object atteso, Object ottenuto) throws Exception {
		if(atteso!=ottenuto) {
			throw new Exception("Verifica '"+nome+"' fallita: attesa la stessa istanza");
		}
		verifiche++;
	}
}
