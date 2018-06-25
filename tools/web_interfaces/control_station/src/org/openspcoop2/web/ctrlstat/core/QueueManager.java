/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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


package org.openspcoop2.web.ctrlstat.core;

import java.util.Properties;

import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.naming.NamingException;

import org.openspcoop2.utils.resources.GestoreJNDI;
import org.openspcoop2.web.lib.queue.config.QueueProperties;

/**
 * QueueManager
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class QueueManager {

	private static QueueManager manager;
	private static String cfName;
	private static Properties cfProp;

	private QueueConnectionFactory qcf;
	private GestoreJNDI jndi;

	private QueueManager() {
		try {
			// leggo parametri dal file properties
			QueueManager.readQueueProperties();
			// Inizializzo il gestorejndi
			this.jndi = new GestoreJNDI(QueueManager.cfProp);

		} catch (Exception e) {
		}
	}

	public static QueueManager getInstance() {
		if (QueueManager.manager == null) {
			QueueManager.manager = new QueueManager();
		}

		return QueueManager.manager;
	}

	public Queue getQueue(String queueName) throws Exception {
		return (Queue) this.jndi.lookup(queueName);
	}

	public QueueConnectionFactory getQueueConnectionFactory() throws NamingException {
		// istanzio connection factory
		try {
			this.qcf = (QueueConnectionFactory) this.jndi.lookup(QueueManager.cfName);
		} catch (Exception e) {
			// ignore
		}
		if (this.qcf == null) {
			throw new NamingException("ConnectionFactory non inizializzata.");
		}

		return this.qcf;
	}

	private static void readQueueProperties() throws Exception {

		QueueProperties queueProperties = QueueProperties.getInstance();
		
		QueueManager.cfName = queueProperties.getConnectionFactory();
		
		QueueManager.cfProp = queueProperties.getConnectionFactoryContext();

	}

}
