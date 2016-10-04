/*
 * OpenSPCoop - Customizable API Gateway 
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


package org.openspcoop2.pdd.core.node;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.GestoreMessaggiException;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.mdb.GenericMessage;
import org.openspcoop2.pdd.mdb.threads.MessaggioSerializzato;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
import org.openspcoop2.utils.resources.Loader;

/**
 * Classe utilizzata per la spedizione di messaggi contenuti nell'architettura di OpenSPCoop (versione JMS).
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class NodeSenderDB extends AbstractCore implements INodeSender{

	private final static String ID_MODULO= "NODE_SENDER_DB";

	/** adapterJDBC di OpenSPCoop di OpenSPCoop */
	private static IJDBCAdapter adapter=null;

	private static OpenSPCoop2Properties propertiesReader; 
	private static Logger log; 


	private static boolean isInitialized = false;

	private synchronized void init() throws NodeException{
		if (!NodeSenderDB.isInitialized) {
			try{

				NodeSenderDB.propertiesReader = OpenSPCoop2Properties.getInstance();
				NodeSenderDB.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
				
				String jdbcAdapter = NodeSenderDB.propertiesReader.getRepositoryJDBCAdapter();
				if(NodeSenderDB.propertiesReader.getDatabaseType()!=null && TipiDatabase.DEFAULT.equals(jdbcAdapter)){
					NodeSenderDB.adapter = JDBCAdapterFactory.createJDBCAdapter(NodeSenderDB.propertiesReader.getDatabaseType());
				}
				else{
					//	initDBManager();
					ClassNameProperties classNameProperties = ClassNameProperties.getInstance();
					//		Ricerco connettore
					String adapterClass = classNameProperties.getJDBCAdapter(jdbcAdapter);
					if(adapterClass == null){
						NodeSenderDB.log.error("Inizializzione GestoreMessaggi non riuscita: AdapterClass non registrata ["+NodeSenderDB.propertiesReader.getRepositoryJDBCAdapter()+"]");
					}
					NodeSenderDB.adapter = (IJDBCAdapter) Loader.getInstance().newInstance(adapterClass);
				}
			}catch(Exception e){
				NodeSenderDB.log.error("Inizializzione GestoreMessaggi non riuscita: AdapterClass non trovata ["+NodeSenderDB.propertiesReader.getRepositoryJDBCAdapter()+"]:"+e.getMessage(),e);
				System.out.println("Inizializzazione NODESENDER FALLITA: " + e);
				return;
			}
			NodeSenderDB.isInitialized=true;
		}
	}




	/**
	 * Spedizione di un messaggio  
	 *
	 * @param msg Messaggio
	 * @param destinazione Modulo di destinazione del msg
	 * @param codicePorta Codice Porta per cui effettuare la receive
	 * @param idModulo Nodo destinatario per cui effettuare la ricezione. 
	 * @param idMessaggio Identificativo del messaggio
	 * 
	 */
	@Override
	public void send(Serializable msg, String destinazione, MsgDiagnostico msgDiag,
			IDSoggetto codicePorta, String idModulo, String idMessaggio, GestoreMessaggi gm) throws NodeException {

		if (!NodeSenderDB.isInitialized) try { init(); } catch (Exception e) {
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Inizializzazione NODESENDER FALLITA",e);
			return;
		}

		MessaggioSerializzato messaggioSerializzato = new MessaggioSerializzato(idMessaggio, (GenericMessage) msg);

		//serializzao il messaggio:
		ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
		byte[] msgByte;
		try {
			ObjectOutput out = new ObjectOutputStream(bos);
			out.writeObject(messaggioSerializzato);
			out.close();
			msgByte = bos.toByteArray();	
		} catch (IOException e) {
			NodeSenderDB.log.error(NodeSenderDB.ID_MODULO + " Serializzazione messaggio fallita: "+e.getMessage(),e);
			return;
		}

		try {
			gm.aggiungiMessaggioSerializzato(NodeSenderDB.adapter,msgByte);
		} catch (GestoreMessaggiException e) {
			throw new NodeException(e);
		}


		msgDiag.highDebug("ObjectMessage send (NOP operation).");
	}


}
