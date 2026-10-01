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
package org.openspcoop2.testsuite.core;

import java.util.Properties;

import javax.naming.Context;

import org.apache.activemq.broker.BrokerService;
import org.openspcoop2.testsuite.units.CooperazioneBase;

/**
 * Broker JMS (ActiveMQ) avviato all'interno della JVM della testsuite quando non è disponibile il broker interno all'application server
 * (application server tomcat) o in modalità jenkins (opzione '-Djenkins=true').
 *
 * In tali modalità la configurazione installata tramite 'preparazioneAmbiente' indirizza i connettori JMS di GovWay verso questo broker
 * (vedi testsuite/ant/openspcoop2-commons-testsuite-config.xml) e nel classpath di GovWay devono essere presenti
 * le librerie client indicate in testsuite/jms.README.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EmbeddedJMSBroker {

	private EmbeddedJMSBroker() {}

	/** Indirizzo del broker: deve corrispondere a quello indicato nella configurazione dei connettori JMS di GovWay */
	public static final String BROKER_URL = "tcp://127.0.0.1:61616";
	public static final String CONNECTION_FACTORY = "ConnectionFactory";

	private static final String CONTEXT_FACTORY = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";

	private static BrokerService broker = null;

	/**
	 * Indica se i test JMS devono utilizzare il broker embedded: application server tomcat o modalità jenkins.
	 */
	public static boolean isRequired(String applicationServer) {
		return (applicationServer!=null && applicationServer.startsWith("tomcat")) || CooperazioneBase.isJenkins();
	}

	public static synchronized boolean isStarted() {
		return broker!=null;
	}

	/**
	 * Avvia il broker, se non già avviato; viene arrestato al termine della JVM.
	 */
	public static synchronized void start() throws TestSuiteException {
		if(broker!=null) {
			return;
		}
		try {
			BrokerService b = new BrokerService();
			b.setBrokerName("govwayTestsuite");
			b.setPersistent(false);
			b.setUseJmx(false);
			b.setAdvisorySupport(false);
			b.setUseShutdownHook(true);
			b.addConnector(BROKER_URL);
			b.start();
			if(!b.waitUntilStarted()) {
				throw new TestSuiteException("Avvio del broker JMS embedded '"+BROKER_URL+"' non completato");
			}
			broker = b;
			System.out.println("INFO: avviato broker JMS embedded '"+BROKER_URL+"'");
		}catch(TestSuiteException e) {
			throw e;
		}catch(Exception e) {
			throw new TestSuiteException("Avvio del broker JMS embedded '"+BROKER_URL+"' non riuscito", e);
		}
	}

	/**
	 * Proprietà del contesto JNDI per la lookup della connection factory (CONNECTION_FACTORY) e delle destinazioni indicate.
	 */
	public static Properties getJNDIContext(String queueJndiName, String topicJndiName) {
		Properties p = new Properties();
		p.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY);
		p.put(Context.PROVIDER_URL, BROKER_URL);
		p.put("connectionFactoryNames", CONNECTION_FACTORY);
		if(queueJndiName!=null) {
			p.put("queue."+queueJndiName, getPhysicalName(queueJndiName));
		}
		if(topicJndiName!=null) {
			p.put("topic."+topicJndiName, getPhysicalName(topicJndiName));
		}
		return p;
	}
	private static String getPhysicalName(String jndiName) {
		// es. queue/openspcoop2TestQueue -> openspcoop2TestQueue
		int index = jndiName.lastIndexOf('/');
		return index>=0 ? jndiName.substring(index+1) : jndiName;
	}
}
