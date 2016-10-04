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



package org.openspcoop2.protocol.spcoop.testsuite.units;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Properties;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageEOFException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;

import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CooperazioneSPCoopBase;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.SPCoopTestsuiteLogger;
import org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteTransformer;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.testsuite.axis14.Axis14SoapUtils;
import org.openspcoop2.testsuite.clients.ClientCore;
import org.openspcoop2.testsuite.clients.ClientOneWay;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.testng.Assert;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test sui connettori diversi da HTTP/HTTPS
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoriDiversiHTTP {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "ConnettoriDiversiHTTP";
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni info = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
				false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBase = 
			new CooperazioneBase(false,SOAPVersion.SOAP11,  this.info, 
					org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
					DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());



	
	private static boolean addIDUnivoco = true;
	
	
	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
				
		TestSuiteTransformer.sequentialForced = true;
		
		// Sottoscrizione al topic
		creaSottoscrittoreTopic();
	} 	
	@AfterGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		
		// Rilascio sottoscrizione
		rilasciaSottoscrittoreTopic();
	} 
	
	

	
	// UTILITIES JMS
	
	TopicConnectionFactory cf_topic = null;
	TopicConnection c_topic = null;
	TopicSession s_topic = null;
	TopicSubscriber receiver_topic = null;
	
	private void creaSottoscrittoreTopic() throws Exception{
	
		//String version_jbossas = Utilities.readApplicationServerVersion();
		
		TestSuiteProperties prop = TestSuiteProperties.getInstance();
			
		Properties properties = prop.getJMS_JNDIContext();
		if(prop.getJMSUsername()!=null && prop.getJMSPassword()!=null){
			properties.put(Context.SECURITY_PRINCIPAL, prop.getJMSUsername());
			properties.put(Context.SECURITY_CREDENTIALS, prop.getJMSPassword());
		}
		GestoreJNDI gestoreJNDI = new GestoreJNDI(properties);
		
		Topic d = (Topic) gestoreJNDI.lookup(prop.getJMSTopic());
		
		this.cf_topic = (TopicConnectionFactory) gestoreJNDI.lookup(prop.getJMSConnectionFactory());
		if(prop.getJMSUsername()!=null && prop.getJMSPassword()!=null){
			this.c_topic = this.cf_topic.createTopicConnection(prop.getJMSUsername(),prop.getJMSPassword());
		}else{
			this.c_topic = this.cf_topic.createTopicConnection();
		}
		this.s_topic = this.c_topic.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
		this.receiver_topic =  this.s_topic.createSubscriber(d);
		
		this.c_topic.start();
	}
	private void rilasciaSottoscrittoreTopic(){
		try{
			if(this.receiver_topic!=null)this.receiver_topic.close();
		}catch(Exception eClose){}
		try{
			if(this.s_topic!=null)this.s_topic.close();
		}catch(Exception eClose){}
		try{
			if(this.c_topic!=null)this.c_topic.stop();
		}catch(Exception eClose){}
		try{
			if(this.c_topic!=null)this.c_topic.close();
		}catch(Exception eClose){}
	}
	
	private Object readObjectJMS(boolean queue,boolean textMode,boolean checkInfoEGov,String azione,String idEGov) throws Exception{
		
		ConnectionFactory cf = null;
		Connection c = null;
		Session s = null;
		MessageConsumer receiver = null;
		try{
		
			TestSuiteProperties prop = TestSuiteProperties.getInstance();
			
			Properties properties = prop.getJMS_JNDIContext();
			if(prop.getJMSUsername()!=null && prop.getJMSPassword()!=null){
				properties.put(Context.SECURITY_PRINCIPAL, prop.getJMSUsername());
				properties.put(Context.SECURITY_CREDENTIALS, prop.getJMSPassword());
			}
			GestoreJNDI gestoreJNDI = new GestoreJNDI(properties);
			
			if(queue){
				Destination d = (Destination) gestoreJNDI.lookup(prop.getJMSQueue());
				cf = (ConnectionFactory) gestoreJNDI.lookup(prop.getJMSConnectionFactory());
				if(prop.getJMSUsername()!=null && prop.getJMSPassword()!=null){
					c = cf.createConnection(prop.getJMSUsername(),prop.getJMSPassword());
				}
				else{
					c = cf.createConnection();
				}
				s = c.createSession(false,Session.AUTO_ACKNOWLEDGE);
				receiver = s.createConsumer(d);
				c.start();
			}
			else{
				cf = this.cf_topic;
				c = this.c_topic;
				s = this.s_topic;
				receiver = this.receiver_topic;
			}
			
			Object o = null;
			Message msg = null;
			if(textMode){
				//System.out.println("RICEVO TEXT...");
				TextMessage received = (TextMessage) receiver.receive(1000);
				//System.out.println("RICEVO TEXT OK");
				if(received==null){
					throw new Exception("Messaggio non presente");
				}
				o = received.getText();
				msg = received;
			}
			else{
				//System.out.println("RICEVO BYTEs...");
				BytesMessage received = (BytesMessage) receiver.receive(10000);
				//System.out.println("RICEVO BYTEs OK");
				if(received==null){
					throw new Exception("Messaggio non presente");
				}
				ByteArrayOutputStream content = new ByteArrayOutputStream();
				boolean endStream = false;
				while(!endStream){
				    try  {
					content.write(received.readByte());
				    }catch(MessageEOFException  end) { endStream = true;}
				}
				o = content.toByteArray();
				msg = received;
			}
			
			// Header di trasporto
			org.openspcoop2.testsuite.core.TestSuiteProperties testsuiteProperties = org.openspcoop2.testsuite.core.TestSuiteProperties.getInstance();
						
			// Tipo Mittente
			String keyTrasporto = testsuiteProperties.getTipoMittenteTrasporto().replace("X-", "").replaceAll("-", "");
			String valoreAtteso = this.collaborazioneSPCoopBase.getMittente().getTipo();
			checkInformazioneEgov("Proprieta' di trasporto",msg, keyTrasporto, valoreAtteso);
			
			// Mittente
			keyTrasporto = testsuiteProperties.getMittenteTrasporto().replace("X-", "").replaceAll("-", "");
			valoreAtteso = this.collaborazioneSPCoopBase.getMittente().getNome();
			checkInformazioneEgov("Proprieta' di trasporto",msg, keyTrasporto, valoreAtteso);
			
			// Tipo Destinatario
			keyTrasporto = testsuiteProperties.getTipoDestinatarioTrasporto().replace("X-", "").replaceAll("-", "");
			valoreAtteso = this.collaborazioneSPCoopBase.getDestinatario().getTipo();
			checkInformazioneEgov("Proprieta' di trasporto",msg, keyTrasporto, valoreAtteso);
			
			// Destinatario
			keyTrasporto = testsuiteProperties.getDestinatarioTrasporto().replace("X-", "").replaceAll("-", "");
			valoreAtteso = this.collaborazioneSPCoopBase.getDestinatario().getNome();
			checkInformazioneEgov("Proprieta' di trasporto",msg, keyTrasporto, valoreAtteso);
			
			// Tipo Servizio
			keyTrasporto = testsuiteProperties.getTipoServizioTrasporto().replace("X-", "").replaceAll("-", "");
			valoreAtteso = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY;
			checkInformazioneEgov("Proprieta' di trasporto",msg, keyTrasporto, valoreAtteso);
			
			// Servizio
			keyTrasporto = testsuiteProperties.getServizioTrasporto().replace("X-", "").replaceAll("-", "");
			valoreAtteso = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY;
			checkInformazioneEgov("Proprieta' di trasporto",msg, keyTrasporto, valoreAtteso);
			
			// Azione
			keyTrasporto = testsuiteProperties.getAzioneTrasporto().replace("X-", "").replaceAll("-", "");
			valoreAtteso = azione;
			checkInformazioneEgov("Proprieta' di trasporto",msg, keyTrasporto, valoreAtteso);
			
			// Azione
			keyTrasporto = testsuiteProperties.getIdMessaggioTrasporto().replace("X-", "").replaceAll("-", "");
			valoreAtteso = idEGov;
			checkInformazioneEgov("Proprieta' di trasporto",msg, keyTrasporto, valoreAtteso);
			
			// UserAgent
			keyTrasporto = CostantiPdD.HEADER_HTTP_USER_AGENT.replace("X-", "").replaceAll("-", "");
			String value = null;
			try{
				value = msg.getStringProperty(keyTrasporto);
			}catch(Exception e){
				throw new Exception("Proprieta' di trasporto ["+keyTrasporto+"] non riscontrata nell'header di trasporto JMS: "+e.getMessage());
			}
			if(value.contains("OpenSPCoop")==false){
				throw new Exception("Proprieta' di trasporto ["+keyTrasporto+"] presente nell'header di trasporto JMS ["+value+"] con un valore diverso da quello atteso["+value+"] (Non contiene OpenSPCoop)");
			}
			
			// PdD
			keyTrasporto = CostantiPdD.HEADER_HTTP_X_PDD.replace("X-", "").replaceAll("-", "");
			try{
				value = msg.getStringProperty(keyTrasporto);
			}catch(Exception e){
				throw new Exception("Proprieta' di trasporto ["+keyTrasporto+"] non riscontrata nell'header di trasporto JMS: "+e.getMessage());
			}
			if(value.contains("OpenSPCoop")==false){
				throw new Exception("Proprieta' di trasporto ["+keyTrasporto+"] presente nell'header di trasporto JMS ["+value+"] con un valore diverso da quello atteso["+value+"] (Non contiene OpenSPCoop)");
			}
			
			// PdDDetails
			keyTrasporto = CostantiPdD.HEADER_HTTP_X_PDD_DETAILS.replace("X-", "").replaceAll("-", "");
			try{
				value = msg.getStringProperty(keyTrasporto);
			}catch(Exception e){
				throw new Exception("Proprieta' di trasporto ["+keyTrasporto+"] non riscontrata nell'header di trasporto JMS: "+e.getMessage());
			}
			if(value==null || "".equals(value)){
				throw new Exception("Proprieta' di trasporto ["+keyTrasporto+"] presente nell'header di trasporto JMS ["+value+"] con un valore diverso da quello atteso["+value+"] (Non definito)");
			}
			
			if(checkInfoEGov){

				String suffix = null;
				String key = null;
				valoreAtteso = null;
				if(queue){
					suffix = "QUEUE";
				}
				else{
					suffix = "TOPIC";
				}
				
				// Tipo Mittente
				key = ("tipoMitt"+suffix);
				valoreAtteso = this.collaborazioneSPCoopBase.getMittente().getTipo();
				checkInformazioneEgov("Proprieta' SPCoop",msg, key, valoreAtteso);
				
				// Mittente
				key = ("mitt"+suffix);
				valoreAtteso = this.collaborazioneSPCoopBase.getMittente().getNome();
				checkInformazioneEgov("Proprieta' SPCoop",msg, key, valoreAtteso);
				
				// Tipo Destinatario
				key = ("tipoDestinatario"+suffix);
				valoreAtteso = this.collaborazioneSPCoopBase.getDestinatario().getTipo();
				checkInformazioneEgov("Proprieta' SPCoop",msg, key, valoreAtteso);
				
				// Destinatario
				key = ("destinatario"+suffix);
				valoreAtteso = this.collaborazioneSPCoopBase.getDestinatario().getNome();
				checkInformazioneEgov("Proprieta' SPCoop",msg, key, valoreAtteso);
				
				// Tipo Servizio
				key = ("tipoServizio"+suffix);
				valoreAtteso = CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY;
				checkInformazioneEgov("Proprieta' SPCoop",msg, key, valoreAtteso);
				
				// Servizio
				key = ("servizio"+suffix);
				valoreAtteso = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY;
				checkInformazioneEgov("Proprieta' SPCoop",msg, key, valoreAtteso);
				
				// Azione
				key = ("azione"+suffix);
				valoreAtteso = azione;
				checkInformazioneEgov("Proprieta' SPCoop",msg, key, valoreAtteso);
				
				// IDEGov
				key = ("id"+suffix);
				valoreAtteso = idEGov;
				checkInformazioneEgov("Proprieta' SPCoop",msg, key, valoreAtteso);
				
			}
			
			return o;
			
		}finally{
			if(queue){
				try{
					if(receiver!=null)receiver.close();
				}catch(Exception eClose){}
				try{
					if(s!=null)s.close();
				}catch(Exception eClose){}
				try{
					if(c!=null)c.stop();
				}catch(Exception eClose){}
				try{
					if(c!=null)c.close();
				}catch(Exception eClose){}
			}
		}

	}
	private void checkInformazioneEgov(String label,Message msg,String key,String valoreAtteso) throws Exception{
		String value = null;
		try{
			value = msg.getStringProperty(key);
		}catch(Exception e){
			throw new Exception(label+" ["+key+"] non riscontrata nell'header di trasporto JMS: "+e.getMessage());
		}
		if(valoreAtteso.equals(value)==false){
			throw new Exception(label+" ["+key+"] presente nell'header di trasporto JMS ["+value+"] con un valore diverso da quello atteso["+valoreAtteso+"]");
		}
	}
	
	private String lastIDUnivocoGenerato = null;
	private org.apache.axis.Message invocazione(Repository repository,String portaDelegata) throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			// Creazione client OneWay
			ClientOneWay client=new ClientOneWay(repository);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,addIDUnivoco);

			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			client.run();
			
			this.lastIDUnivocoGenerato = client.getLastIDUnivocoGenerato();
			return client.sentMessage;
			
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}
	}
	
	public static boolean QUEUE = true;
	public static boolean TOPIC = false;
	public static boolean TEXT_MESSAGE = true;
	public static boolean BYTES_MESSAGE = false;
	public static boolean PROPAGAZIONE_EGOV = true;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	/***
	 * Test per JMS come TextObject su Queue
	 */
	Repository repositoryJMS_text_queue=new Repository();
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".TEXT_QUEUE"})
	public void jmsTextQueue() throws TestSuiteException, Exception{
		
		// Invocazione SPCoop
		org.apache.axis.Message sentMessage = this.invocazione(this.repositoryJMS_text_queue,CostantiTestSuite.PORTA_DELEGATA_JMS_TEXT_QUEUE);
		
		// Consuma messaggio sulla coda
		String id=this.repositoryJMS_text_queue.getNext();
		String response = (String) readObjectJMS(QUEUE, TEXT_MESSAGE, !PROPAGAZIONE_EGOV, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_JMS_TEXT_QUEUE, id);
		this.repositoryJMS_text_queue.add(id);
		Assert.assertTrue(response.contains(this.lastIDUnivocoGenerato));
		org.apache.axis.Message responseMessage = Axis14SoapUtils.build(response.getBytes(), false);
		
		// Verifica
		ClientCore.isEqualsSentAndResponseMessage(sentMessage, responseMessage);
	}
	@DataProvider (name="jmsTextQueue")
	public Object[][]testJmsTextQueue() throws Exception{
		String id=this.repositoryJMS_text_queue.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id}	
		};
	}
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".TEXT_QUEUE"},dataProvider="jmsTextQueue",dependsOnMethods={"jmsTextQueue"})
	public void testJmsTextQueue(DatabaseComponent data,String id) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_JMS_TEXT_QUEUE, false,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}

	
	
	
	
	
	
	
	
	
	/***
	 * Test per JMS come BytesObject su Queue
	 */
	Repository repositoryJMS_bytes_queue=new Repository();
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".BYTES_QUEUE"})
	public void jmsBytesQueue() throws TestSuiteException, Exception{
		
		// Invocazione SPCoop
		org.apache.axis.Message sentMessage = this.invocazione(this.repositoryJMS_bytes_queue,CostantiTestSuite.PORTA_DELEGATA_JMS_BYTES_QUEUE);

		// Consuma messaggio sulla coda
		String id=this.repositoryJMS_bytes_queue.getNext();
		byte[] response = (byte[]) readObjectJMS(QUEUE, BYTES_MESSAGE, !PROPAGAZIONE_EGOV, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_JMS_BYTES_QUEUE, id);
		this.repositoryJMS_bytes_queue.add(id);
		Assert.assertTrue(new String(response).contains(this.lastIDUnivocoGenerato));
		org.apache.axis.Message responseMessage = Axis14SoapUtils.build(response, false);
		
		// Verifica
		ClientCore.isEqualsSentAndResponseMessage(sentMessage, responseMessage);
	}
	@DataProvider (name="jmsBytesQueue")
	public Object[][]testJmsBytesQueue() throws Exception{
		String id=this.repositoryJMS_bytes_queue.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id}	
		};
	}
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".BYTES_QUEUE"},dataProvider="jmsBytesQueue",dependsOnMethods={"jmsBytesQueue"})
	public void testJmsBytesQueue(DatabaseComponent data,String id) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_JMS_BYTES_QUEUE, false,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per JMS come TextObject su Topic
	 */
	Repository repositoryJMS_text_topic=new Repository();
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".TEXT_TOPIC"})
	public void jmsTextTopic() throws TestSuiteException, Exception{
		
		// Invocazione SPCoop
		org.apache.axis.Message sentMessage = this.invocazione(this.repositoryJMS_text_topic,CostantiTestSuite.PORTA_DELEGATA_JMS_TEXT_TOPIC);
		
		// Consuma messaggio sulla coda
		String id=this.repositoryJMS_text_topic.getNext();
		String response = (String) readObjectJMS(TOPIC, TEXT_MESSAGE, !PROPAGAZIONE_EGOV, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_JMS_TEXT_TOPIC, id);
		this.repositoryJMS_text_topic.add(id);
		Assert.assertTrue(response.contains(this.lastIDUnivocoGenerato));
		org.apache.axis.Message responseMessage = Axis14SoapUtils.build(response.getBytes(), false);
		
		// Verifica
		ClientCore.isEqualsSentAndResponseMessage(sentMessage, responseMessage);
	}
	@DataProvider (name="jmsTextTopic")
	public Object[][]testJmsTextTopic() throws Exception{
		String id=this.repositoryJMS_text_topic.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id}	
		};
	}
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".TEXT_TOPIC"},dataProvider="jmsTextTopic",dependsOnMethods={"jmsTextTopic"})
	public void testJmsTextTopic(DatabaseComponent data,String id) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_JMS_TEXT_TOPIC, false,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per JMS come BytesObject su Topic
	 */
	Repository repositoryJMS_bytes_topic=new Repository();
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".BYTES_TOPIC"})
	public void jmsBytesTopic() throws TestSuiteException, Exception{
		
		// Invocazione SPCoop
		org.apache.axis.Message sentMessage = this.invocazione(this.repositoryJMS_bytes_topic,CostantiTestSuite.PORTA_DELEGATA_JMS_BYTES_TOPIC);
		
		// Consuma messaggio sulla coda
		String id=this.repositoryJMS_bytes_topic.getNext();
		byte[] response = (byte[]) readObjectJMS(TOPIC, BYTES_MESSAGE, !PROPAGAZIONE_EGOV, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_JMS_BYTES_TOPIC, id);
		this.repositoryJMS_bytes_topic.add(id);
		Assert.assertTrue(new String(response).contains(this.lastIDUnivocoGenerato));
		org.apache.axis.Message responseMessage = Axis14SoapUtils.build(response, false);
		
		// Verifica
		ClientCore.isEqualsSentAndResponseMessage(sentMessage, responseMessage);
	}
	@DataProvider (name="jmsBytesTopic")
	public Object[][]testJmsBytesTopic() throws Exception{
		String id=this.repositoryJMS_bytes_topic.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id}	
		};
	}
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".BYTES_TOPIC"},dataProvider="jmsBytesTopic",dependsOnMethods={"jmsBytesTopic"})
	public void testJmsBytesTopic(DatabaseComponent data,String id) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_JMS_BYTES_TOPIC, false,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per JMS come TextObject su Queue
	 */
	Repository repositoryJMS_text_queue_propagazioneEGov=new Repository();
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".TEXT_QUEUE_PROPAGAZIONE_EGOV"})
	public void jmsTextQueuePropagazioneEGov() throws TestSuiteException, Exception{
		
		// Invocazione SPCoop
		org.apache.axis.Message sentMessage = this.invocazione(this.repositoryJMS_text_queue_propagazioneEGov,CostantiTestSuite.PORTA_DELEGATA_JMS_INFO_EGOV_QUEUE);
		
		// Consuma messaggio sulla coda
		String id=this.repositoryJMS_text_queue_propagazioneEGov.getNext();
		String response = (String) readObjectJMS(QUEUE, TEXT_MESSAGE, PROPAGAZIONE_EGOV, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_JMS_INFO_EGOV_QUEUE, id);
		this.repositoryJMS_text_queue_propagazioneEGov.add(id);
		Assert.assertTrue(response.contains(this.lastIDUnivocoGenerato));
		org.apache.axis.Message responseMessage = Axis14SoapUtils.build(response.getBytes(), false);
		
		// Verifica
		ClientCore.isEqualsSentAndResponseMessage(sentMessage, responseMessage);
	}
	@DataProvider (name="jmsTextQueuePropagazioneEGov")
	public Object[][]testJmsTextQueuePropagazioneEGov() throws Exception{
		String id=this.repositoryJMS_text_queue_propagazioneEGov.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id}	
		};
	}
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".TEXT_QUEUE_PROPAGAZIONE_EGOV"},dataProvider="jmsTextQueuePropagazioneEGov",dependsOnMethods={"jmsTextQueuePropagazioneEGov"})
	public void testJmsTextQueuePropagazioneEGov(DatabaseComponent data,String id) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_JMS_INFO_EGOV_QUEUE, false,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per JMS come TextObject su Topic
	 */
	Repository repositoryJMS_text_topic_propagazioneEGov=new Repository();
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".TEXT_TOPIC_PROPAGAZIONE_EGOV"})
	public void jmsTextTopicPropagazioneEGov() throws TestSuiteException, Exception{
		
		// Invocazione SPCoop
		org.apache.axis.Message sentMessage = this.invocazione(this.repositoryJMS_text_topic_propagazioneEGov,CostantiTestSuite.PORTA_DELEGATA_JMS_INFO_EGOV_TOPIC);
		
		// Consuma messaggio sulla coda
		String id=this.repositoryJMS_text_topic_propagazioneEGov.getNext();
		String response = (String) readObjectJMS(TOPIC, TEXT_MESSAGE, PROPAGAZIONE_EGOV, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_JMS_INFO_EGOV_TOPIC,id);
		this.repositoryJMS_text_topic_propagazioneEGov.add(id);
		Assert.assertTrue(response.contains(this.lastIDUnivocoGenerato));
		org.apache.axis.Message responseMessage = Axis14SoapUtils.build(response.getBytes(), false);
		
		// Verifica
		ClientCore.isEqualsSentAndResponseMessage(sentMessage, responseMessage);
	}
	@DataProvider (name="jmsTextTopicPropagazioneEGov")
	public Object[][]testJmsTextTopicPropagazioneEGov() throws Exception{
		String id=this.repositoryJMS_text_topic_propagazioneEGov.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id}	
		};
	}
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".TEXT_TOPIC_PROPAGAZIONE_EGOV"},dataProvider="jmsTextTopicPropagazioneEGov",dependsOnMethods={"jmsTextTopicPropagazioneEGov"})
	public void testJmsTextTopicPropagazioneEGov(DatabaseComponent data,String id) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_JMS_INFO_EGOV_TOPIC, false,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per JMS come TextObject su Queue
	 */
	Repository repositoryJMS_text_queue_sbustamentoSOAP=new Repository();
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".TEXT_QUEUE_SBUSTAMENTO_SOAP"})
	public void jmsTextQueuesbustamentoSOAP() throws TestSuiteException, Exception{
		
		// Invocazione SPCoop
		org.apache.axis.Message sentMessage = this.invocazione(this.repositoryJMS_text_queue_sbustamentoSOAP,CostantiTestSuite.PORTA_DELEGATA_JMS_SBUSTAMENTO_SOAP_QUEUE);
		
		// Consuma messaggio sulla coda
		String id=this.repositoryJMS_text_queue_sbustamentoSOAP.getNext();
		String response = (String) readObjectJMS(QUEUE, TEXT_MESSAGE, !PROPAGAZIONE_EGOV, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_JMS_SBUSTAMENTO_SOAP_QUEUE,id);
		this.repositoryJMS_text_queue_sbustamentoSOAP.add(id);
		Assert.assertTrue(response.contains(this.lastIDUnivocoGenerato));
		Assert.assertTrue(!response.contains("Envelope"));
		org.apache.axis.Message responseMessage = Axis14SoapUtils.build(response.getBytes(), true); // effettuo imbustamento!
		
		// Verifica
		ClientCore.isEqualsSentAndResponseMessage(sentMessage, responseMessage);
	}
	@DataProvider (name="jmsTextQueuesbustamentoSOAP")
	public Object[][]testJmsTextQueuesbustamentoSOAP() throws Exception{
		String id=this.repositoryJMS_text_queue_sbustamentoSOAP.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id}	
		};
	}
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".TEXT_QUEUE_SBUSTAMENTO_SOAP"},dataProvider="jmsTextQueuesbustamentoSOAP",dependsOnMethods={"jmsTextQueuesbustamentoSOAP"})
	public void testJmsTextQueuesbustamentoSOAP(DatabaseComponent data,String id) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_JMS_SBUSTAMENTO_SOAP_QUEUE, false,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per JMS come TextObject su Topic
	 */
	Repository repositoryJMS_text_topic_sbustamentoSOAP=new Repository();
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".TEXT_TOPIC_SBUSTAMENTO_SOAP"})
	public void jmsTextTopicsbustamentoSOAP() throws TestSuiteException, Exception{
		
		// Invocazione SPCoop
		org.apache.axis.Message sentMessage = this.invocazione(this.repositoryJMS_text_topic_sbustamentoSOAP,CostantiTestSuite.PORTA_DELEGATA_JMS_SBUSTAMENTO_SOAP_TOPIC);
		
		// Consuma messaggio sulla coda
		String id=this.repositoryJMS_text_topic_sbustamentoSOAP.getNext();
		String response = (String) readObjectJMS(TOPIC, TEXT_MESSAGE, !PROPAGAZIONE_EGOV, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_JMS_SBUSTAMENTO_SOAP_TOPIC,id);
		this.repositoryJMS_text_topic_sbustamentoSOAP.add(id);
		Assert.assertTrue(response.contains(this.lastIDUnivocoGenerato));
		Assert.assertTrue(!response.contains("Envelope"));
		org.apache.axis.Message responseMessage = Axis14SoapUtils.build(response.getBytes(), true); // effettuo imbustamento!
		
		// Verifica
		ClientCore.isEqualsSentAndResponseMessage(sentMessage, responseMessage);
	}
	@DataProvider (name="jmsTextTopicsbustamentoSOAP")
	public Object[][]testJmsTextTopicsbustamentoSOAP() throws Exception{
		String id=this.repositoryJMS_text_topic_sbustamentoSOAP.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id}	
		};
	}
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".TEXT_TOPIC_SBUSTAMENTO_SOAP"},dataProvider="jmsTextTopicsbustamentoSOAP",dependsOnMethods={"jmsTextTopicsbustamentoSOAP"})
	public void testJmsTextTopicsbustamentoSOAP(DatabaseComponent data,String id) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_JMS_SBUSTAMENTO_SOAP_TOPIC, false,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test Connettore SAAJ Oneway
	 */
	Repository repositorySAAJOneway=new Repository();
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".SAAJ_ONEWAY"})
	public void saajOneway() throws TestSuiteException, Exception{
		
		// Invocazione SPCoop
		this.collaborazioneSPCoopBase.oneWay(this.repositorySAAJOneway,CostantiTestSuite.PORTA_DELEGATA_SAAJ_ONEWAY,addIDUnivoco);
		
	}
	@DataProvider (name="saajOneway")
	public Object[][]testSaajOnewayTest() throws Exception{
		String id=this.repositorySAAJOneway.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id}	
		};
	}
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".SAAJ_ONEWAY"},dataProvider="saajOneway",dependsOnMethods={"saajOneway"})
	public void testSaajOnewayTest(DatabaseComponent data,String id) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_SAAJ, false,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	/***
	 * Test Connettore SAAJ Sincrono
	 */
	Repository repositorySAAJSincrono=new Repository();
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".SAAJ_SINCRONO"})
	public void saajSincrono() throws TestSuiteException, Exception{
		
		// Invocazione SPCoop
		this.collaborazioneSPCoopBase.sincrono(this.repositorySAAJSincrono,CostantiTestSuite.PORTA_DELEGATA_SAAJ_SINCRONO,addIDUnivoco);
		
	}
	@DataProvider (name="saajSincrono")
	public Object[][]testSaajSincronoTest() throws Exception{
		String id=this.repositorySAAJSincrono.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id}	
		};
	}
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".SAAJ_SINCRONO"},dataProvider="saajSincrono",dependsOnMethods={"saajSincrono"})
	public void testSaajSincronoTest(DatabaseComponent data,String id) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_SAAJ, false,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	/***
	 * Test Connettore HTTPCORE Oneway
	 */
	Repository repositoryHTTPCOREOneway=new Repository();
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".HTTPCORE_ONEWAY"})
	public void httpCoreOneway() throws TestSuiteException, Exception{
		
		// Invocazione SPCoop
		this.collaborazioneSPCoopBase.oneWay(this.repositoryHTTPCOREOneway,CostantiTestSuite.PORTA_DELEGATA_HTTPCORE_ONEWAY,addIDUnivoco);
		
	}
	@DataProvider (name="httpCoreOneway")
	public Object[][]testHttpCoreOnewayTest() throws Exception{
		String id=this.repositoryHTTPCOREOneway.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id}	
		};
	}
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".HTTPCORE_ONEWAY"},dataProvider="httpCoreOneway",dependsOnMethods={"httpCoreOneway"})
	public void testHttpCoreOnewayTest(DatabaseComponent data,String id) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_HTTPCORE, false,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	/***
	 * Test Connettore HTTPCORE Sincrono
	 */
	Repository repositoryHTTPCORESincrono=new Repository();
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".HTTPCORE_SINCRONO"})
	public void httpCoreSincrono() throws TestSuiteException, Exception{
		
		// Invocazione SPCoop
		this.collaborazioneSPCoopBase.sincrono(this.repositoryHTTPCORESincrono,CostantiTestSuite.PORTA_DELEGATA_HTTPCORE_SINCRONO,addIDUnivoco);
		
	}
	@DataProvider (name="httpCoreSincrono")
	public Object[][]testHttpCoreSincronoTest() throws Exception{
		String id=this.repositoryHTTPCORESincrono.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id}	
		};
	}
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".HTTPCORE_SINCRONO"},dataProvider="httpCoreSincrono",dependsOnMethods={"httpCoreSincrono"})
	public void testHttpCoreSincronoTest(DatabaseComponent data,String id) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_HTTPCORE, false,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
}
