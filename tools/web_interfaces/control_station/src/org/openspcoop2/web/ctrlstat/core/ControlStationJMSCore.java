/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

package org.openspcoop2.web.ctrlstat.core;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.InitialContext;

import org.openspcoop2.web.ctrlstat.costanti.OperationsParameter;
import org.openspcoop2.web.ctrlstat.costanti.TipoOggettoDaSmistare;
import org.openspcoop2.web.lib.queue.costanti.Operazione;

/**
 * ControlStationJMSCore
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ControlStationJMSCore {

	/**
	 * Inoltra l'operazione nella coda dello smistatore in caso di errori lancia
	 * un'eccezione che verra' gestita dal chiamante
	 * 
	 * @param operazioneDaSmistare
	 * @throws Exception
	 */
	public static void setDati(OperazioneDaSmistare operazioneDaSmistare,String smistatoreQueue,String cfName,Properties cfProp) throws Exception {

		QueueConnection qc = null;
		QueueSession qs = null;

		// try{

		// Estraggo i dati dall'Operazione da smistare, che tanto mi servono
		// per settare la StringProperty
		int idTable = operazioneDaSmistare.getIDTable();
		Operazione operazione = operazioneDaSmistare.getOperazione();
		String pdd = operazioneDaSmistare.getPdd();
		TipoOggettoDaSmistare oggettoDaSmistare = operazioneDaSmistare.getOggetto();

		ControlStationCore.log.debug("[ControlStationCore::setDati] id[" + idTable + "] operazione[" + operazione.name() + 
				"] pdd[" + pdd + "] oggetto[" + oggettoDaSmistare.name() + "]");

		InitialContext ctx = new InitialContext(cfProp);
		Queue queue = (Queue) ctx.lookup(smistatoreQueue);
		QueueConnectionFactory qcf = (QueueConnectionFactory) ctx.lookup(cfName);
		qc = qcf.createQueueConnection();
		qs = qc.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		QueueSender sender = qs.createSender(queue);
		ctx.close();

		// Create a message
		ObjectMessage message = qs.createObjectMessage(operazioneDaSmistare);

		// Preparo la StringProperty, che serve per il filtro
		StringBuffer idOperazione = new StringBuffer();
		idOperazione.append("[" + operazione.name() + "]");
		idOperazione.append("[" + oggettoDaSmistare.name() + "]");

		Hashtable<OperationsParameter, Vector<String>> params = operazioneDaSmistare.getParameters();
		Enumeration<OperationsParameter> keys = params.keys();

		// aggiungo informazioni il filtro
		while (keys.hasMoreElements()) {
			OperationsParameter key = keys.nextElement();

			Vector<String> values = params.get(key);
			for (String value : values) {
				idOperazione.append("[" + value + "]");
			}

		}

		ControlStationCore.log.debug("[ControlStationCore::setDati] id=[" + idOperazione.toString() + "]");
		message.setStringProperty("ID", idOperazione.toString());

		// send a message
		sender.send(message);

		// fix: 19/01
		qs.close();
		qc.close();

	}
	
}
