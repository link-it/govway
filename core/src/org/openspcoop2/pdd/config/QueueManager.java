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



package org.openspcoop2.pdd.config;

import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Session;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.IMonitoraggioRisorsa;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.mdb.Imbustamento;
import org.openspcoop2.pdd.mdb.ImbustamentoRisposte;
import org.openspcoop2.pdd.mdb.InoltroBuste;
import org.openspcoop2.pdd.mdb.InoltroRisposte;
import org.openspcoop2.pdd.mdb.Sbustamento;
import org.openspcoop2.pdd.mdb.SbustamentoRisposte;
import org.openspcoop2.pdd.services.RicezioneBuste;
import org.openspcoop2.pdd.services.RicezioneContenutiApplicativi;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.GestoreJNDI;


/**
 * Contiene la gestione delle connessioni al broker delle code JMS.
 * Il nome della risorsa JNDI da cui e' possibili attingere connessioni verso il Provider JMS, 
 * viene selezionato attraverso le impostazioni lette dal file 'openspcoop2.properties'
 * e gestite attraverso l'utilizzo della classe  {@link org.openspcoop2.pdd.config.OpenSPCoop2Properties}.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class QueueManager implements java.io.Serializable,IMonitoraggioRisorsa{


	/**
	 * 
	 */
	private static final long serialVersionUID = 2542909452367582138L;
	/** Coda su cui il webService 'RicezioneContenutiApplicativi_XXX' sta' attendendo una risposta */
	public static Queue QUEUE_RICEZIONE_CONTENUTI_APPLICATIVI;
	/** Coda su cui il webService 'RicezioneBuste_XXX' sta' attendendo una risposta */
	public static Queue QUEUE_RICEZIONE_BUSTE;
	/** Coda su cui l'MDB 'Imbustamento' sta' attendendo un messaggio */
	public static Queue QUEUE_IMBUSTAMENTO;
	/** Coda su cui l'MDB 'ImbustamentoRisposte' sta' attendendo un messaggio */
	public static Queue QUEUE_IMBUSTAMENTO_RISPOSTE;
	/** Coda su cui l'MDB 'Sbustamento' sta' attendendo un messaggio */
	public static Queue QUEUE_SBUSTAMENTO;
	/** Coda su cui l'MDB 'SbustamentoRisposte' sta' attendendo un messaggio */
	public static Queue QUEUE_SBUSTAMENTO_RISPOSTE;
	/** Coda su cui l'MDB 'InoltroBuste' sta' attendendo un messaggio */
	public static Queue QUEUE_INOLTRO_BUSTE;
	/** Coda su cui l'MDB 'InoltroRisposte' sta' attendendo un messaggio */
	public static Queue QUEUE_INOLTRO_RISPOSTE;
	/** Coda su cui l'MDB 'ConsegnaMessaggiSoap' sta' attendendo un messaggio */
	public static Queue QUEUE_CONSEGNA_CONTENUTI_APPLICATIVI;

	
	/** OpenSPCoopProperties */
	private static OpenSPCoop2Properties openspcoopProperties = OpenSPCoop2Properties.getInstance();
	
	/** Informazione sui proprietari che hanno richiesto una connessione */
	protected static Hashtable<String,Resource> risorseInGestione = new Hashtable<String,Resource>();
	
	public static String[] getStatoRisorse() throws Exception{
		
		Object[] o = QueueManager.risorseInGestione.values().toArray(new Resource[0]);
		if(o==null)
			return null;
		Resource[] resources = (Resource[]) o;
		if(resources==null || resources.length<=0)
			return null;
	
		String [] r = new String[resources.length];
		for(int i=0; i<resources.length; i++){
			Resource rr = resources[i];
			r[i] = rr.getIdentificativoPorta()+"."+rr.getModuloFunzionale();
			if(rr.getIdTransazione()!=null){
				r[i] = r[i] +"."+rr.getIdTransazione();
			}
			r[i] = r[i] +" ("+rr.getDate().toString()+")";
		}
		return r;
	}


	/** QueueManager */
	private static QueueManager manager = null;

	/**
	 * Il Metodo si occupa di inizializzare il QueueManager 
	 *
	 * @param jndiName Nome JNDI del QueueConnectionFactory
	 * @param contextFactory Contesto JNDI da utilizzare per la connection factory/openSPCoopQueueManager
	 * 
	 */
	public static void initialize(String jndiName,
			java.util.Properties contextFactory) throws Exception{

		// Provo ad ottenere un QueueManager
		QueueManager.manager = new QueueManager(jndiName,contextFactory);

	}
	
	/**
	 * Il Metodo si occupa di inizializzare le code di ricezione 
	 *
	 * @param contextQueue Contesto JNDI da utilizzare per le code interne
	 * 
	 */
	public static void initializeQueueNodeReceiver(java.util.Properties contextQueue) throws Exception{

		// Tabella per i nomi jndi delle code
		java.util.Hashtable<String,String> nomiJndi = 
			OpenSPCoop2Properties.getInstance().getJNDIQueueName(true,false);
			
		// Inizializzazione Code
		GestoreJNDI jndiQueue = new GestoreJNDI(contextQueue);
		QueueManager.QUEUE_RICEZIONE_CONTENUTI_APPLICATIVI = 
			(Queue) jndiQueue.lookup(nomiJndi.get(RicezioneContenutiApplicativi.ID_MODULO));
		QueueManager.QUEUE_RICEZIONE_BUSTE= 
			(Queue) jndiQueue.lookup(nomiJndi.get(RicezioneBuste.ID_MODULO));

	}


	/**
	 * Il Metodo si occupa di inizializzare le code di spedizione 
	 *
	 * @param contextQueue Contesto JNDI da utilizzare per le code interne
	 * 
	 */
	public static void initializeQueueNodeSender(java.util.Properties contextQueue) throws Exception{

		// Tabella per i nomi jndi delle code
		java.util.Hashtable<String,String> nomiJndi = OpenSPCoop2Properties.getInstance().getJNDIQueueName(false,true);
		
		// Inizializzazione Code
		GestoreJNDI jndiQueue = new GestoreJNDI(contextQueue);
		QueueManager.QUEUE_IMBUSTAMENTO= 
			(Queue) jndiQueue.lookup(nomiJndi.get(Imbustamento.ID_MODULO));
		QueueManager.QUEUE_IMBUSTAMENTO_RISPOSTE= 
			(Queue) jndiQueue.lookup(nomiJndi.get(ImbustamentoRisposte.ID_MODULO));
		QueueManager.QUEUE_SBUSTAMENTO= 
			(Queue) jndiQueue.lookup(nomiJndi.get(Sbustamento.ID_MODULO));
		QueueManager.QUEUE_SBUSTAMENTO_RISPOSTE= 
			(Queue) jndiQueue.lookup(nomiJndi.get(SbustamentoRisposte.ID_MODULO));
		QueueManager.QUEUE_INOLTRO_BUSTE= 
			(Queue) jndiQueue.lookup(nomiJndi.get(InoltroBuste.ID_MODULO));
		QueueManager.QUEUE_INOLTRO_RISPOSTE= 
			(Queue) jndiQueue.lookup(nomiJndi.get(InoltroRisposte.ID_MODULO));
		QueueManager.QUEUE_CONSEGNA_CONTENUTI_APPLICATIVI= 
			(Queue) jndiQueue.lookup(nomiJndi.get(ConsegnaContenutiApplicativi.ID_MODULO));
	}
	
	
	
	
	/**
	 * Ritorna l'istanza di questo QueueManager
	 *
	 * @return Istanza di QueueManager
	 * 
	 */
	public static QueueManager getInstance(){
		return QueueManager.manager;
	}

	
	/**
	 * Restituisce la coda associata al nodo con identificativo <var>nomeNodo</var>.
	 *
	 * @param idNodo Identificatore del nodo.
	 * 
	 */
	public Queue getQueue(String idNodo) throws Exception{
		if(idNodo.startsWith(RicezioneContenutiApplicativi.ID_MODULO))
			return QueueManager.QUEUE_RICEZIONE_CONTENUTI_APPLICATIVI;
		else if(idNodo.startsWith(RicezioneBuste.ID_MODULO))
			return QueueManager.QUEUE_RICEZIONE_BUSTE;
		else if(Imbustamento.ID_MODULO.equals(idNodo))
			return QueueManager.QUEUE_IMBUSTAMENTO;
		else if(ImbustamentoRisposte.ID_MODULO.equals(idNodo))
			return QueueManager.QUEUE_IMBUSTAMENTO_RISPOSTE;
		else if(Sbustamento.ID_MODULO.equals(idNodo))
			return QueueManager.QUEUE_SBUSTAMENTO;
		else if(SbustamentoRisposte.ID_MODULO.equals(idNodo))
			return QueueManager.QUEUE_SBUSTAMENTO_RISPOSTE;
		else if(InoltroBuste.ID_MODULO.equals(idNodo))
			return QueueManager.QUEUE_INOLTRO_BUSTE;
		else if(InoltroRisposte.ID_MODULO.equals(idNodo))
			return QueueManager.QUEUE_INOLTRO_RISPOSTE;
		else if(ConsegnaContenutiApplicativi.ID_MODULO.equals(idNodo))
			return QueueManager.QUEUE_CONSEGNA_CONTENUTI_APPLICATIVI;
		else 
			return null;
	}
	
	
	
	
	
	/** ConnectionFactory dove attingere connessioni */
	private ConnectionFactory qcf = null;
	/** MsgDiagnostico */
	private MsgDiagnostico msgDiag = null;
	
	/**
	 * Costruttore
	 *
	 * @param jndiName Nome JNDI del QueueConnectionFactory
	 * @param context Contesto JNDI da utilizzare
	 * 
	 */
	public QueueManager(String jndiName,java.util.Properties context) throws OpenSPCoop2ConfigurationException{

		this.msgDiag = new MsgDiagnostico("WrapperQueueManager");
		try {
			GestoreJNDI jndi = new GestoreJNDI(context);

			// ConnectionFactory
			this.qcf = (ConnectionFactory) jndi.lookup(jndiName);
		}
		catch(Exception e) {
			throw new OpenSPCoop2ConfigurationException("WrapperQueueManager: "+e.getMessage(),e);
		}

	}





	/**
	 * Ritorna un JMSObject che contiene una connessione/sessione al JMS Broker
	 *
	 * @param idPDD Identificatore della porta di dominio.
	 * @param modulo Modulo che richiede una connessione.
	 * @return JMSObject.
	 * 
	 */
	public Resource getResource(IDSoggetto idPDD,String modulo,String idTransazione) throws OpenSPCoop2ConfigurationException{
		
		Resource risorsa = new Resource();
		try {
			Connection con = this.qcf.createConnection();
			if(con == null)
				throw new OpenSPCoop2ConfigurationException("ConnessioneNonDisponibile");

			// Sessione
			Session s = con.createSession(false,QueueManager.openspcoopProperties.getAcknowledgeModeSessioneConnectionFactory());
			if(s == null){
				con.close();
				throw new OpenSPCoop2ConfigurationException("SessioneNonDisponibile");
			}

			// Object JMS
			
			JMSObject jms = new JMSObject();
			jms.setConnection(con);
			jms.setSession(s); 
			
			String idUnivoco = Resource.generaIdentificatoreUnivoco(idPDD, modulo);
			risorsa.setId(idUnivoco);
			risorsa.setDate(DateManager.getDate());
			risorsa.setIdentificativoPorta(idPDD);
			risorsa.setModuloFunzionale(modulo);
			risorsa.setResource(jms);
			risorsa.setResourceType(JMSObject.class.getName());
			risorsa.setIdTransazione(idTransazione);
				
			QueueManager.risorseInGestione.put(idUnivoco, risorsa);
			
			return risorsa;

		}
		catch(Exception e) {
			this.msgDiag.aggiornaFiltri();
			this.msgDiag.setDominio(idPDD);
			this.msgDiag.setFunzione("QueueManager."+modulo);
			this.msgDiag.logFatalError(e, "Richiesta connessione al QueueManager");
			throw new OpenSPCoop2ConfigurationException("getJMSObject: "+e.getMessage());
		}
	}

	/**
	 * Restituisce un JMSObject al pool
	 *
	 * @param idPDD Identificatore della porta di dominio.
	 * @param modulo Modulo che richiede una connessione.
	 * @param resource JMSObject.
	 * 
	 */
	public void releaseResource(IDSoggetto idPDD,String modulo,Resource resource) throws OpenSPCoop2ConfigurationException{
		try {
			if(resource!=null){
				if(resource.getResource()!=null){
					JMSObject jms = (JMSObject) resource.getResource();
					// Controllo dell'oggetto ricevuto
					if(jms == null){
						throw new OpenSPCoop2ConfigurationException("PassivateObject[JMSObjectNull]");
					}
					if(jms.getConnection()==null){
						throw new OpenSPCoop2ConfigurationException("PassivateObject[ConnectionNull]");
					}
					if(jms.getSession()==null){
						throw new OpenSPCoop2ConfigurationException("PassivateObject[SessionNull]");
					}

					// rilascio
					try{
						jms.getSession().close();
					}catch(Exception e){
						jms.getConnection().close();
						throw e;
					}
					jms.getConnection().close();
				}
				if(QueueManager.risorseInGestione.containsKey(resource.getId()))
					QueueManager.risorseInGestione.remove(resource.getId());
			}
		}
		catch(Exception e) {
			this.msgDiag.aggiornaFiltri();
			this.msgDiag.setDominio(idPDD);
			this.msgDiag.setFunzione("QueueManager."+modulo);
			this.msgDiag.logFatalError(e, "Rilasciata connessione al QueueManager");
			throw new OpenSPCoop2ConfigurationException("releaseJMSObject: "+e.getMessage());
		}
	}
	
	




	/**
	 * Metodo che verica la connessione ad una risorsa.
	 * Se la connessione non e' presente, viene lanciata una eccezione che contiene il motivo della mancata connessione
	 * 
	 * @throws DriverException eccezione che contiene il motivo della mancata connessione
	 */
	@Override
	public void isAlive() throws CoreException{
		Resource resource = null;
		IDSoggetto idSoggettAlive = new IDSoggetto();
		idSoggettAlive.setCodicePorta("QueueManager");
		idSoggettAlive.setTipo("QueueManager");
		idSoggettAlive.setNome("QueueManager");
		try{
			resource = this.getResource(idSoggettAlive, "CheckIsAlive", null);
			if(resource == null)
				throw new Exception("Resource is null");
			if(resource.getResource() == null)
				throw new Exception("JMSObject is null");
			JMSObject jmsObject = (JMSObject) resource.getResource();
			if(jmsObject.getConnection()==null)
				throw new Exception("Connessione is null");
			if(jmsObject.getSession()==null)
				throw new Exception("Sessione is null");
			Connection connectionJMS = jmsObject.getConnection();
			// test
			connectionJMS.getClientID();
		}catch(Exception e){
			throw new CoreException("Connessione al broker JMS non disponibile: "+e.getMessage(),e);
		}finally{
			try{
				this.releaseResource(idSoggettAlive, "CheckIsAlive" ,resource);
			}catch(Exception e){}
		}
	}
}

