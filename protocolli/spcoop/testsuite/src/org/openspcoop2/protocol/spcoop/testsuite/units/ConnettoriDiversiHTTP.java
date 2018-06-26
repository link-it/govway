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



package org.openspcoop2.protocol.spcoop.testsuite.units;

import java.io.ByteArrayInputStream;
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

import org.openspcoop2.message.constants.MessageType;
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
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.clients.ClientOneWay;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.clients.ClientSincrono;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.openspcoop2.utils.transport.http.HttpConstants;
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
			new CooperazioneBase(false,MessageType.SOAP_11,  this.info, 
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
			keyTrasporto = testsuiteProperties.getNomeServizioTrasporto().replace("X-", "").replaceAll("-", "");
			valoreAtteso = CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY;
			checkInformazioneEgov("Proprieta' di trasporto",msg, keyTrasporto, valoreAtteso);
			
			// VersioneServizio
			keyTrasporto = testsuiteProperties.getVersioneServizioTrasporto().replace("X-", "").replaceAll("-", "");
			valoreAtteso = "1";
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
			keyTrasporto = HttpConstants.USER_AGENT.replace("X-", "").replaceAll("-", "");
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

	
	
	
	
	
	
	
	/* **************** FILE *********************** */
	
	
	Repository repositoryFILE_serializeFile=new Repository();
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".FILE_serializeFile"})
	public void FILE_serializeFile() throws TestSuiteException, Exception{
		this.collaborazioneSPCoopBase.sincrono(this.repositoryFILE_serializeFile,CostantiTestSuite.PORTA_DELEGATA_FILE_SERIALIZE_FILE,addIDUnivoco,true);
	}
	@DataProvider (name="FILE_serializeFile")
	public Object[][]testFILE_serializeFile() throws Exception{
		String id=this.repositoryFILE_serializeFile.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".FILE_serializeFile"},dataProvider="FILE_serializeFile",dependsOnMethods={"FILE_serializeFile"})
	public void testFILE_serializeFile(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_SERIALIZE_FILE, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	Repository repositoryFILE_serializeFileAndHeaders=new Repository();
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".FILE_serializeFileAndHeaders"})
	public void FILE_serializeFileAndHeaders() throws TestSuiteException, Exception{
		this.collaborazioneSPCoopBase.sincrono(this.repositoryFILE_serializeFileAndHeaders,CostantiTestSuite.PORTA_DELEGATA_FILE_SERIALIZE_FILE_AND_HEADERS,addIDUnivoco,true);
	}
	@DataProvider (name="FILE_serializeFileAndHeaders")
	public Object[][]testFILE_serializeFileAndHeaders() throws Exception{
		String id=this.repositoryFILE_serializeFileAndHeaders.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".FILE_serializeFileAndHeaders"},dataProvider="FILE_serializeFileAndHeaders",dependsOnMethods={"FILE_serializeFileAndHeaders"})
	public void testFILE_serializeFileAndHeaders(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_SERIALIZE_FILE_AND_HEADERS, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	Repository repositoryFILE_serializeDynamicFile=new Repository();
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".FILE_serializeDynamicFile"})
	public void FILE_serializeDynamicFile() throws TestSuiteException, Exception{
		this.collaborazioneSPCoopBase.sincrono(this.repositoryFILE_serializeDynamicFile,CostantiTestSuite.PORTA_DELEGATA_FILE_SERIALIZE_DYNAMIC_FILE,addIDUnivoco,true);
	}
	@DataProvider (name="FILE_serializeDynamicFile")
	public Object[][]testFILE_serializeDynamicFile() throws Exception{
		String id=this.repositoryFILE_serializeDynamicFile.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".FILE_serializeDynamicFile"},dataProvider="FILE_serializeDynamicFile",dependsOnMethods={"FILE_serializeDynamicFile"})
	public void testFILE_serializeDynamicFile(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_SERIALIZE_DYNAMIC_FILE, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	Repository repositoryFILE_serializeFile_sbustamentoSOAP=new Repository();
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".FILE_serializeFile_sbustamentoSOAP"})
	public void FILE_serializeFile_sbustamentoSOAP() throws TestSuiteException, Exception{
		this.collaborazioneSPCoopBase.sincrono(this.repositoryFILE_serializeFile_sbustamentoSOAP,CostantiTestSuite.PORTA_DELEGATA_FILE_SERIALIZE_FILE_SBUSTAMENTO_SOAP,addIDUnivoco,true);
	}
	@DataProvider (name="FILE_serializeFile_sbustamentoSOAP")
	public Object[][]testFILE_serializeFile_sbustamentoSOAP() throws Exception{
		String id=this.repositoryFILE_serializeFile_sbustamentoSOAP.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".FILE_serializeFile_sbustamentoSOAP"},dataProvider="FILE_serializeFile_sbustamentoSOAP",dependsOnMethods={"FILE_serializeFile_sbustamentoSOAP"})
	public void testFILE_serializeFile_sbustamentoSOAP(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_SERIALIZE_FILE_SBUSTAMENTO_SOAP, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	Repository repositoryFILE_serializeAndReturnFile=new Repository();
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".FILE_serializeAndReturnFile"},description="Test di tipo FILE_serializeAndReturnFile, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void FILE_serializeAndReturnFile() throws Exception{
		
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(this.repositoryFILE_serializeAndReturnFile);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_FILE_SERIALIZE_AND_RETURN_FILE);
		client.connectToSoapEngine(MessageType.SOAP_11);
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,addIDUnivoco);
		client.run();
		
		org.apache.axis.Message atteso = 
				new org.apache.axis.Message(new ByteArrayInputStream(org.openspcoop2.utils.resources.FileSystemUtilities.
							readBytesFromFile(Utilities.testSuiteProperties.getSoapSenzaHeaderFileName())));
		org.apache.axis.Message receivedMessage = client.getResponseMessage();
		Assert.assertTrue(ClientCore.isEqualsSentAndResponseMessage(atteso, receivedMessage));
		
	}
	@DataProvider (name="FILE_serializeAndReturnFile")
	public Object[][]testFILE_serializeAndReturnFile()throws Exception{
		String id=this.repositoryFILE_serializeAndReturnFile.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".FILE_serializeAndReturnFile"},dataProvider="FILE_serializeAndReturnFile",dependsOnMethods={"FILE_serializeAndReturnFile"})
	public void testFILE_serializeAndReturnFile(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_SERIALIZE_AND_RETURN_FILE, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	Repository repositoryFILE_serializeAndReturnFileAndHeaders=new Repository();
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".FILE_serializeAndReturnFileAndHeaders"},description="Test di tipo FILE_serializeAndReturnFileAndHeaders, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void FILE_serializeAndReturnFileAndHeaders() throws Exception{
		
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(this.repositoryFILE_serializeAndReturnFileAndHeaders);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_FILE_SERIALIZE_AND_RETURN_FILE_AND_HEADERS);
		client.connectToSoapEngine(MessageType.SOAP_11);
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,addIDUnivoco);
		client.run();
		
		org.apache.axis.Message atteso = 
				new org.apache.axis.Message(new ByteArrayInputStream(org.openspcoop2.utils.resources.FileSystemUtilities.
							readBytesFromFile(Utilities.testSuiteProperties.getSoapSenzaHeaderFileName())));
		org.apache.axis.Message receivedMessage = client.getResponseMessage();
		Assert.assertTrue(ClientCore.isEqualsSentAndResponseMessage(atteso, receivedMessage));
		
	}
	@DataProvider (name="FILE_serializeAndReturnFileAndHeaders")
	public Object[][]testFILE_serializeAndReturnFileAndHeaders()throws Exception{
		String id=this.repositoryFILE_serializeAndReturnFileAndHeaders.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".FILE_serializeAndReturnFileAndHeaders"},dataProvider="FILE_serializeAndReturnFileAndHeaders",dependsOnMethods={"FILE_serializeAndReturnFileAndHeaders"})
	public void testFILE_serializeAndReturnFileAndHeaders(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_SERIALIZE_AND_RETURN_FILE_AND_HEADERS, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	Repository repositoryFILE_serializeAndReturnDynamicFile=new Repository();
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".FILE_serializeAndReturnDynamicFile"},description="Test di tipo FILE_serializeAndReturnDynamicFile, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void FILE_serializeAndReturnDynamicFile() throws Exception{
		
		this.collaborazioneSPCoopBase.sincrono(this.repositoryFILE_serializeAndReturnDynamicFile,CostantiTestSuite.PORTA_DELEGATA_FILE_SERIALIZE_AND_RETURN_DYNAMIC_FILE,addIDUnivoco);
		
	}
	@DataProvider (name="FILE_serializeAndReturnDynamicFile")
	public Object[][]testFILE_serializeAndReturnDynamicFile()throws Exception{
		String id=this.repositoryFILE_serializeAndReturnDynamicFile.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".FILE_serializeAndReturnDynamicFile"},dataProvider="FILE_serializeAndReturnDynamicFile",dependsOnMethods={"FILE_serializeAndReturnDynamicFile"})
	public void testFILE_serializeAndReturnDynamicFile(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_SERIALIZE_AND_RETURN_DYNAMIC_FILE, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	Repository repositoryFILE_serializeAndReturnFile_sbustamentoSOAP=new Repository();
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".FILE_serializeAndReturnFile_sbustamentoSOAP"},description="Test di tipo FILE_serializeAndReturnFile_sbustamentoSOAP, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void FILE_serializeAndReturnFile_sbustamentoSOAP() throws Exception{
		
		// Creazione client Sincrono
		ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryFILE_serializeAndReturnFile_sbustamentoSOAP);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore()+CostantiTestSuite.PORTA_DELEGATA_FILE_SERIALIZE_AND_RETURN_FILE_SBUSTAMENTO_SOAP);
		client.connectToSoapEngine();	
		client.setMessaggioXMLRichiesta(org.openspcoop2.utils.resources.FileSystemUtilities.readBytesFromFile(Utilities.testSuiteProperties.getSoap11FileName()));
		client.setRispostaDaGestire(true);
		client.run();
		
		org.apache.axis.Message atteso = 
				new org.apache.axis.Message(new ByteArrayInputStream(org.openspcoop2.utils.resources.FileSystemUtilities.
							readBytesFromFile(Utilities.testSuiteProperties.getXmlSenzaSoapFileName())),true);
		java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
		atteso.writeTo(bout);
		bout.flush();
		bout.close();
		
		org.apache.axis.Message receivedMessage = new org.apache.axis.Message(new ByteArrayInputStream(client.getMessaggioXMLRisposta()),false);
		java.io.ByteArrayOutputStream boutReceived = new java.io.ByteArrayOutputStream();
		receivedMessage.writeTo(boutReceived);
		boutReceived.flush();
		boutReceived.close();
		
		boolean check = ClientCore.isEqualsSentAndResponseMessage(atteso, receivedMessage);
		if(!check){
			System.out.println("ATTESO ["+bout.toString()+"]");
			System.out.println("RECEIVED ["+boutReceived.toString()+"]");
		}
		Assert.assertTrue(check);
		
	}
	@DataProvider (name="FILE_serializeAndReturnFile_sbustamentoSOAP")
	public Object[][]testFILE_serializeAndReturnFile_sbustamentoSOAP()throws Exception{
		String id=this.repositoryFILE_serializeAndReturnFile_sbustamentoSOAP.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".FILE_serializeAndReturnFile_sbustamentoSOAP"},dataProvider="FILE_serializeAndReturnFile_sbustamentoSOAP",dependsOnMethods={"FILE_serializeAndReturnFile_sbustamentoSOAP"})
	public void testFILE_serializeAndReturnFile_sbustamentoSOAP(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_SERIALIZE_AND_RETURN_FILE_SBUSTAMENTO_SOAP, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	Repository repositoryFILE_serializeAndReturnFileAsync=new Repository();
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".FILE_serializeAndReturnFileAsync"})
	public void FILE_serializeAndReturnFileAsync() throws TestSuiteException, Exception{
		this.collaborazioneSPCoopBase.oneWay(this.repositoryFILE_serializeAndReturnFileAsync,
				CostantiTestSuite.PORTA_DELEGATA_FILE_SERIALIZE_AND_RETURN_FILE_ASYNC,addIDUnivoco,
				"fruitoreSerializeAndReturnFileAsync","123456");
		
		// Check file serializzato in asincrono
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		org.apache.axis.Message atteso = 
				new org.apache.axis.Message(new ByteArrayInputStream(org.openspcoop2.utils.resources.FileSystemUtilities.
							readBytesFromFile(Utilities.testSuiteProperties.getSoapSenzaHeaderFileName())),false);
		java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
		atteso.writeTo(bout);
		bout.flush();
		bout.close();
		
		org.apache.axis.Message receivedMessage = new org.apache.axis.Message(new ByteArrayInputStream(org.openspcoop2.utils.resources.FileSystemUtilities.
				readBytesFromFile("/var/tmp/TEST/ASYNC/RES.xml")),false);
		receivedMessage.getSOAPHeader().removeContents();
		java.io.ByteArrayOutputStream boutReceived = new java.io.ByteArrayOutputStream();
		receivedMessage.writeTo(boutReceived);
		boutReceived.flush();
		boutReceived.close();
		
		boolean check = ClientCore.isEqualsSentAndResponseMessage(atteso, receivedMessage);
		if(!check){
			System.out.println("ATTESO ["+bout.toString()+"]");
			System.out.println("RECEIVED ["+boutReceived.toString()+"]");
		}
		Assert.assertTrue(check);
		
	}
	@DataProvider (name="FILE_serializeAndReturnFileAsync")
	public Object[][]testFILE_serializeAndReturnFileAsync() throws Exception{
		String id=this.repositoryFILE_serializeAndReturnFileAsync.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,false}	
		};
	}
	@Test(groups={ConnettoriDiversiHTTP.ID_GRUPPO,ConnettoriDiversiHTTP.ID_GRUPPO+".FILE_serializeAndReturnFileAsync"},
			dataProvider="FILE_serializeAndReturnFileAsync",dependsOnMethods={"FILE_serializeAndReturnFileAsync"})
	public void testFILE_serializeAndReturnFileAsync(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_SERIALIZE_AND_RETURN_FILE_ASYNC, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	

}
