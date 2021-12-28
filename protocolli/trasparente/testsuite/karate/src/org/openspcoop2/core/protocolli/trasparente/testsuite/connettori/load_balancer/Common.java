package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.load_balancer;

import java.util.Map;

public class Common {
	
	public static final String ID_CONNETTORE_REPLY_PREFIX = "GovWay-TestSuite-";
	public static final String HEADER_ID_CONNETTORE = ID_CONNETTORE_REPLY_PREFIX + "id_connettore";
	
	
	public static final int durataBloccante = Integer
			.valueOf(System.getProperty("connettori.load_balancer.least_connections.durata_bloccante"));
	
	
	public static final int delayRichiesteBackground = Integer
			.valueOf(System.getProperty("connettori.load_balancer.least_connections.delay_richieste_background"));


	public static void printMap(Map<String,Integer> howManys) {
		for (var e : howManys.entrySet()) {
			System.out.println(e.getKey() + ": " + e.getValue());
		}
	}
}
