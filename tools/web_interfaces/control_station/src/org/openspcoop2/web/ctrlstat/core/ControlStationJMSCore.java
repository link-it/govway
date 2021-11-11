/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import java.util.List;
import java.util.Map;
import java.util.Properties;

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
		long idTable = operazioneDaSmistare.getIDTable();
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
		StringBuilder idOperazione = new StringBuilder();
		idOperazione.append("[" + operazione.name() + "]");
		idOperazione.append("[" + oggettoDaSmistare.name() + "]");

		Map<OperationsParameter, List<String>> params = operazioneDaSmistare.getParameters();
		if(params!=null && !params.isEmpty()) {
			// aggiungo informazioni il filtro
			for (OperationsParameter key : params.keySet()) {
				List<String> values = params.get(key);
				for (String value : values) {
					idOperazione.append("[" + value + "]");
				}
	
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
