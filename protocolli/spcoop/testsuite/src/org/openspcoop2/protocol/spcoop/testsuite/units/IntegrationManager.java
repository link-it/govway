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



package org.openspcoop2.protocol.spcoop.testsuite.units;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;

import javax.xml.rpc.Stub;
import javax.xml.ws.BindingProvider;

import org.apache.axis.Message;
import org.openspcoop2.testsuite.axis14.Axis14SoapUtils;
import org.openspcoop2.testsuite.clients.ClientCore;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.SOAPEngine;
import org.openspcoop2.testsuite.core.TestSuiteProperties;
import org.openspcoop2.testsuite.core.asincrono.RepositoryConsegnaRisposteAsincroneSimmetriche;
import org.openspcoop2.testsuite.core.asincrono.RepositoryCorrelazioneIstanzeAsincrone;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatiServizio;
import org.openspcoop2.testsuite.server.ServerRicezioneRispostaAsincronaSimmetrica;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CooperazioneSPCoopBase;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.SPCoopTestsuiteLogger;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test sui profili di collaborazione implementati nella Porta di Dominio
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrationManager {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "IntegrationManager";
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni info = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBase = 
		new CooperazioneBase(false,SOAPVersion.SOAP11,  this.info, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());


	private static boolean addIDUnivoco = true;

	private static boolean use_axis14_engine = Utilities.testSuiteProperties.isSoapEngineAxis14();
	private static boolean use_cxf_engine = Utilities.testSuiteProperties.isSoapEngineCxf();
	
	
	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	@AfterGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
	} 
	
	
	
	

	/** Contatore utilizzato per la costruzione dell'Identificato (proprietario della testsuite) 
	 * inserito nell'header della richiesta 
	 * */
	private static int counter=0;
	private static synchronized int getCounter(){
		counter++;
		return counter;
	}
	
	/** IntegrationManager */
	public static org.openspcoop2.pdd.services.axis14.PD_PortType getIntegrationManagerPD_axis14(String url,String username,String password) throws Exception{ 
		org.openspcoop2.pdd.services.axis14.PDServiceLocator locator = new org.openspcoop2.pdd.services.axis14.PDServiceLocator();
	    locator.setPDEndpointAddress(url);
	    org.openspcoop2.pdd.services.axis14.PD_PortType port = locator.getPD();
	    if(username !=null && password!=null){
	            // to use Basic HTTP Authentication: 
	            ((Stub) port)._setProperty(javax.xml.rpc.Call.USERNAME_PROPERTY, username);
	            ((Stub) port)._setProperty(javax.xml.rpc.Call.PASSWORD_PROPERTY, password);
	    }
	    return port;
	}
	public static org.openspcoop2.pdd.services.axis14.MessageBox_PortType getIntegrationManagerMessageBox_axis14(String url,String username,String password) throws Exception{ 
		org.openspcoop2.pdd.services.axis14.MessageBoxServiceLocator locator = new org.openspcoop2.pdd.services.axis14.MessageBoxServiceLocator();
	    locator.setMessageBoxEndpointAddress(url);
	    org.openspcoop2.pdd.services.axis14.MessageBox_PortType port = locator.getMessageBox();
	    if(username !=null && password!=null){
	            // to use Basic HTTP Authentication: 
	            ((Stub) port)._setProperty(javax.xml.rpc.Call.USERNAME_PROPERTY, username);
	            ((Stub) port)._setProperty(javax.xml.rpc.Call.PASSWORD_PROPERTY, password);
	    }
	    return port;
	}
	public static org.openspcoop2.pdd.services.cxf.PD getIntegrationManagerPD_cxf(String url,String username,String password) throws Exception{ 
		org.openspcoop2.pdd.services.cxf.PDService locator = null;
		locator = new org.openspcoop2.pdd.services.cxf.PDService();
		org.openspcoop2.pdd.services.cxf.PD port = locator.getPD();
		BindingProvider imProviderPD = (BindingProvider)port;
		imProviderPD.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
		if(username !=null && password!=null){
			// to use Basic HTTP Authentication: 
			imProviderPD.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
			imProviderPD.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
		}
		return port;
	}
	public static org.openspcoop2.pdd.services.cxf.MessageBox getIntegrationManagerMessageBox_cxf(String url,String username,String password) throws Exception{ 
		org.openspcoop2.pdd.services.cxf.MessageBoxService locator = null;
		locator = new org.openspcoop2.pdd.services.cxf.MessageBoxService();
		org.openspcoop2.pdd.services.cxf.MessageBox port = locator.getMessageBox();
		BindingProvider imProviderPD = (BindingProvider)port;
		imProviderPD.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
		if(username !=null && password!=null){
			// to use Basic HTTP Authentication: 
			imProviderPD.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
			imProviderPD.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
		}
		return port;
	}
	public static org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage toIntegrationManagerMessage_axis14(org.openspcoop2.pdd.services.cxf.IntegrationManagerMessage msg_cxf){
		if(msg_cxf==null){
			return null;
		}
		org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage msg_axis14 = new org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage();
		msg_axis14.setIDApplicativo(msg_cxf.getIDApplicativo());
		msg_axis14.setImbustamento(msg_cxf.isImbustamento());
		msg_axis14.setMessage(msg_cxf.getMessage());
		msg_axis14.setProtocolHeaderInfo(toProtocolHeaderInfo_axis14(msg_cxf.getProtocolHeaderInfo()));
		msg_axis14.setServizioApplicativo(msg_cxf.getServizioApplicativo());
		return msg_axis14;
	} 
	public static org.openspcoop2.pdd.services.cxf.IntegrationManagerMessage toIntegrationManagerMessage_cxf(org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage msg_axis14){
		if(msg_axis14==null){
			return null;
		}
		org.openspcoop2.pdd.services.cxf.IntegrationManagerMessage msg_cxf= new org.openspcoop2.pdd.services.cxf.IntegrationManagerMessage();
		msg_cxf.setIDApplicativo(msg_axis14.getIDApplicativo());
		msg_cxf.setImbustamento(msg_axis14.isImbustamento());
		msg_cxf.setMessage(msg_axis14.getMessage());
		msg_cxf.setProtocolHeaderInfo(toProtocolHeaderInfo_cxf(msg_axis14.getProtocolHeaderInfo()));
		msg_cxf.setServizioApplicativo(msg_axis14.getServizioApplicativo());
		return msg_cxf;
	} 
	public static org.openspcoop2.pdd.services.axis14.ProtocolHeaderInfo toProtocolHeaderInfo_axis14(org.openspcoop2.pdd.services.cxf.ProtocolHeaderInfo protocol_cxf){
		if(protocol_cxf==null){
			return null;
		}
		org.openspcoop2.pdd.services.axis14.ProtocolHeaderInfo protocol_axis14 = new org.openspcoop2.pdd.services.axis14.ProtocolHeaderInfo();
		
		protocol_axis14.setTipoMittente(protocol_cxf.getTipoMittente());
		protocol_axis14.setMittente(protocol_cxf.getMittente());
		
		protocol_axis14.setTipoDestinatario(protocol_cxf.getTipoDestinatario());
		protocol_axis14.setDestinatario(protocol_cxf.getDestinatario());
		
		protocol_axis14.setTipoServizio(protocol_cxf.getTipoServizio());
		protocol_axis14.setServizio(protocol_cxf.getServizio());
		
		protocol_axis14.setAzione(protocol_cxf.getAzione());
		
		protocol_axis14.setID(protocol_cxf.getID());
		protocol_axis14.setRiferimentoMessaggio(protocol_cxf.getRiferimentoMessaggio());
		
		protocol_axis14.setIdCollaborazione(protocol_cxf.getIdCollaborazione());
		
		return protocol_axis14;
	}
	public static org.openspcoop2.pdd.services.cxf.ProtocolHeaderInfo toProtocolHeaderInfo_cxf(org.openspcoop2.pdd.services.axis14.ProtocolHeaderInfo protocol_axis14){
		if(protocol_axis14==null){
			return null;
		}
		org.openspcoop2.pdd.services.cxf.ProtocolHeaderInfo protocol_cxf = new org.openspcoop2.pdd.services.cxf.ProtocolHeaderInfo();
		
		protocol_cxf.setTipoMittente(protocol_axis14.getTipoMittente());
		protocol_cxf.setMittente(protocol_axis14.getMittente());
		
		protocol_cxf.setTipoDestinatario(protocol_axis14.getTipoDestinatario());
		protocol_cxf.setDestinatario(protocol_axis14.getDestinatario());
		
		protocol_cxf.setTipoServizio(protocol_axis14.getTipoServizio());
		protocol_cxf.setServizio(protocol_axis14.getServizio());
		
		protocol_cxf.setAzione(protocol_axis14.getAzione());
		
		protocol_cxf.setID(protocol_axis14.getID());
		protocol_cxf.setRiferimentoMessaggio(protocol_axis14.getRiferimentoMessaggio());
		
		protocol_cxf.setIdCollaborazione(protocol_axis14.getIdCollaborazione());
		
		return protocol_cxf;
	}
	public static org.openspcoop2.pdd.services.axis14.IntegrationManagerException toProtocolHeaderInfo_axis14(org.openspcoop2.pdd.services.cxf.IntegrationManagerException exception_cxf){
		if(exception_cxf==null){
			return null;
		}
		org.openspcoop2.pdd.services.axis14.IntegrationManagerException exception_axis14 = new org.openspcoop2.pdd.services.axis14.IntegrationManagerException();
		exception_axis14.setCodiceEccezione(exception_cxf.getCodiceEccezione());
		exception_axis14.setDescrizioneEccezione(exception_cxf.getDescrizioneEccezione());
		exception_axis14.setIdentificativoFunzione(exception_cxf.getIdentificativoFunzione());
		exception_axis14.setIdentificativoPorta(exception_cxf.getIdentificativoPorta());
		exception_axis14.setOraRegistrazione(exception_cxf.getOraRegistrazione());
		exception_axis14.setTipoEccezione(exception_cxf.getTipoEccezione());
		return exception_axis14;
	}
	public static org.openspcoop2.pdd.services.cxf.IntegrationManagerException toProtocolHeaderInfo_cxf(org.openspcoop2.pdd.services.axis14.IntegrationManagerException exception_axis14){
		if(exception_axis14==null){
			return null;
		}
		org.openspcoop2.pdd.services.cxf.IntegrationManagerException exception_cxf = new org.openspcoop2.pdd.services.cxf.IntegrationManagerException();
		exception_cxf.setCodiceEccezione(exception_axis14.getCodiceEccezione());
		exception_cxf.setDescrizioneEccezione(exception_axis14.getDescrizioneEccezione());
		exception_cxf.setIdentificativoFunzione(exception_axis14.getIdentificativoFunzione());
		exception_cxf.setIdentificativoPorta(exception_axis14.getIdentificativoPorta());
		exception_cxf.setOraRegistrazione(exception_axis14.getOraRegistrazione());
		exception_cxf.setTipoEccezione(exception_axis14.getTipoEccezione());
		return exception_cxf;
	}
	public String[] toArray(List<String> list){
		if(list!=null && list.size()>0){
			String[] s = new String[list.size()];
			s = list.toArray(s);
			return s;
		}else{
			return null;
		}
	}
		
	
	
	

	/***
	 * Test per il profilo di collaborazione OneWay
	 */
	Repository repositoryOneWay=new Repository();
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ONEWAY"})
	public void oneWay() throws TestSuiteException, Exception{	
		Reporter.log("SOAPEngine axis14["+use_axis14_engine+"] cxf["+use_cxf_engine+"]");
		gestioneProfiloOneway(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY, null, null, this.repositoryOneWay,null,true);
	}
	@DataProvider (name="OneWay")
	public Object[][]testOneWay() throws Exception{
		String id=this.repositoryOneWay.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ONEWAY"},dataProvider="OneWay",dependsOnMethods={"oneWay"})
	public void testOneWay(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,null, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}



	/***
	 * Test per il profilo di collaborazione Sincrono
	 */
	Repository repositorySincrono=new Repository();
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".SINCRONO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincrono() throws Exception{
		Reporter.log("SOAPEngine axis14["+use_axis14_engine+"] cxf["+use_cxf_engine+"]");
		// Costruzione msg di richiesta
		SOAPEngine utility = new SOAPEngine(null);
		utility.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,addIDUnivoco);

		// SPCoopMessage
		Message msgAxis = utility.getRequestMessage();
		org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage msg = new org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		msgAxis.writeTo(bout);
		bout.flush();
		bout.close();
		msg.setMessage(bout.toByteArray());
		
		// IntegrationManager
		org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage msgRisposta = null;
		if(use_axis14_engine){
			org.openspcoop2.pdd.services.axis14.PD_PortType im = getIntegrationManagerPD_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/PD"), 
					null,null);
			msgRisposta = im.invocaPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO, msg);
		}
		else{
			org.openspcoop2.pdd.services.cxf.PD im = getIntegrationManagerPD_cxf(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/PD"), 
					null,null);
			org.openspcoop2.pdd.services.cxf.IntegrationManagerMessage msgRisposta_cxf = im.invocaPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO, toIntegrationManagerMessage_cxf(msg));
			msgRisposta = toIntegrationManagerMessage_axis14(msgRisposta_cxf);
		}
		
		
		// Msg di risposta
		Message msgAxisResponse = Axis14SoapUtils.build(msgRisposta.getMessage(), false);
		
		// Test uguaglianza Body
		Reporter.log("Controllo risposta ottenuta");
		Assert.assertTrue(ClientCore.isEqualsSentAndResponseMessage(msgAxis,msgAxisResponse));
		
		// Controlla che sia uguale a quello ritornato nell'header della risposta
	    if(msgRisposta.getProtocolHeaderInfo().getID()==null)
	    	throw new TestSuiteException("ID e-Gov non presenta nell'header del trasporto della ricevuta.");
	    
		// Identificativo della richiesta
	    String identificativoRichiestaAsincrona=msgRisposta.getProtocolHeaderInfo().getID();
	    this.repositorySincrono.add(identificativoRichiestaAsincrona);
	    
	    Reporter.log("[Sincrono] Gestione richiesta con id="+identificativoRichiestaAsincrona+" effettata.");
	    
	    // Attesa terminazione messaggio
	    org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties testsuiteProperties = 
	    	org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance();
	    org.openspcoop2.testsuite.core.TestSuiteProperties testsuitePropertiesLIB = 
	    	org.openspcoop2.testsuite.core.TestSuiteProperties.getInstance();
	    if(testsuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
	    	
	    	DatabaseComponent dbFruitore = null;
	    	DatabaseComponent dbErogatore = null;
	    	try {
	    	
	    		/** DatabaseComponent */
	    		dbFruitore = DatabaseProperties.getDatabaseComponentFruitore();
	    		dbErogatore = DatabaseProperties.getDatabaseComponentErogatore();
	    		
				if(dbFruitore!=null){
					long countTimeout = System.currentTimeMillis() + (1000* testsuitePropertiesLIB.getTimeoutProcessamentoMessaggiOpenSPCoop());
					while(dbFruitore.getVerificatoreMessaggi().countMsgOpenSPCoop(identificativoRichiestaAsincrona)!=0 && countTimeout>System.currentTimeMillis()){
						try{
						Thread.sleep(testsuitePropertiesLIB.getCheckIntervalProcessamentoMessaggiOpenSPCoop());
						}catch(Exception e){}
					}
				}
				if(dbErogatore!=null){
					long countTimeout = System.currentTimeMillis() + (1000*testsuitePropertiesLIB.getTimeoutProcessamentoMessaggiOpenSPCoop());
					while(dbErogatore.getVerificatoreMessaggi().countMsgOpenSPCoop(identificativoRichiestaAsincrona)!=0 && countTimeout>System.currentTimeMillis()){
						try{
							Thread.sleep(testsuitePropertiesLIB.getCheckIntervalProcessamentoMessaggiOpenSPCoop());
						}catch(Exception e){}
					}
				}
	    	}catch(Exception e){
	    		Reporter.log("[Sincrono] Errore durante il controllo: "+e.getMessage());
	    	}finally{
	    		try{
	    			if(dbFruitore!=null){
	    				dbFruitore.close();
	    			}
	    		}catch(Exception e){}
	    		try{
	    			if(dbErogatore!=null){
	    				dbErogatore.close();
	    			}
	    		}catch(Exception e){}
	    	}
		}
	}
	@DataProvider (name="Sincrono")
	public Object[][]testSincrono()throws Exception{
		String id=this.repositorySincrono.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".SINCRONO"},dataProvider="Sincrono",dependsOnMethods={"sincrono"})
	public void testSincrono(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,null, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}








	/***
	 * Test per il profilo di collaborazione Asincrono Simmetrico, modalita asincrona
	 */
	RepositoryConsegnaRisposteAsincroneSimmetriche repositoryConsegnaRisposteAsincroneSimmetriche_modalitaAsincrona = new RepositoryConsegnaRisposteAsincroneSimmetriche(Utilities.testSuiteProperties.timeToSleep_repositoryAsincronoSimmetrico());
	RepositoryCorrelazioneIstanzeAsincrone repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaAsincrona =
		new RepositoryCorrelazioneIstanzeAsincrone();
	ServerRicezioneRispostaAsincronaSimmetrica serverRicezioneRispostaAsincronaSimmetrica_modalitaAsincrona = 
		new ServerRicezioneRispostaAsincronaSimmetrica(Utilities.testSuiteProperties.getWorkerNumber(),Utilities.testSuiteProperties.getSocketAsincronoSimmetrico_modalitaAsincrona(),
				this.repositoryConsegnaRisposteAsincroneSimmetriche_modalitaAsincrona);
	@Test (groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ASINCRONO_SIMMETRICO_asincr"})
	public void startServerRicezioneRispostaAsincronaSimmetrica_modalitaAsincrona(){
		this.serverRicezioneRispostaAsincronaSimmetrica_modalitaAsincrona.start();
	}
	@Test (groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ASINCRONO_SIMMETRICO_asincr"},description="Test di tipo asincrono simmetrico con modalita asincrona",dependsOnMethods="startServerRicezioneRispostaAsincronaSimmetrica_modalitaAsincrona")
	public void asincronoSimmetrico_ModalitaAsincrona() throws Exception{
		Reporter.log("SOAPEngine axis14["+use_axis14_engine+"] cxf["+use_cxf_engine+"]");
		// Costruzione msg di richiesta
		SOAPEngine utility = new SOAPEngine(null);
		utility.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,addIDUnivoco);
				
		// Info PortaCorrelata per profilo asincrono simmetrico
		String idPortaDelegataCorrelata = 
			org.openspcoop2.testsuite.core.Utilities.getHeaderValue(
					org.openspcoop2.testsuite.core.CostantiTestSuite.UTILIZZO_INTEGRATION_MANAGER+
					CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_CORRELATO_MODALITA_ASINCRONA,
					IntegrationManager.getCounter());
		utility.setIDTestSuiteHeader(idPortaDelegataCorrelata);
		
		// SPCoopMessage
		Message msgAxis = utility.getRequestMessage();
		org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage msg = new org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		msgAxis.writeTo(bout);
		bout.flush();
		bout.close();
		msg.setMessage(bout.toByteArray());
		
		// IntegrationManager
		org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage msgRisposta = null;
		if(use_axis14_engine){
			org.openspcoop2.pdd.services.axis14.PD_PortType im = getIntegrationManagerPD_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/PD"), 
					"profiloAsincrono_richiestaAsincrona","123456");
			msgRisposta = im.invocaPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA, msg);
		}
		else{
			org.openspcoop2.pdd.services.cxf.PD im = getIntegrationManagerPD_cxf(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/PD"), 
					"profiloAsincrono_richiestaAsincrona","123456");
			org.openspcoop2.pdd.services.cxf.IntegrationManagerMessage msgRisposta_cxf = im.invocaPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA, toIntegrationManagerMessage_cxf(msg));
			msgRisposta = toIntegrationManagerMessage_axis14(msgRisposta_cxf);
		}
		
		// Msg di risposta
		Message msgAxisResponse = Axis14SoapUtils.build(msgRisposta.getMessage(), false,false,false);
		
		// Test uguaglianza Body
		if(msgAxisResponse.getSOAPBody()!=null && msgAxisResponse.getSOAPBody().hasChildNodes()){
			Reporter.log("Controllo risposta ottenuta");
			String idBody=org.openspcoop2.testsuite.core.Utilities.getIDFromOpenSPCoopOKMessage(SPCoopTestsuiteLogger.getInstance(),msgAxisResponse);
		    // Controlla che sia uguale a quello ritornato nell'header della risposta
		    if(idBody==null)
		    	throw new TestSuiteException("ID e-Gov non presenta nella ricevuta OpenSPCoopOK.");
		    if(msgRisposta.getProtocolHeaderInfo().getID()==null)
		    	throw new TestSuiteException("ID e-Gov non presenta nell'header del trasporto della ricevuta.");
		    if(msgRisposta.getProtocolHeaderInfo().getID().equals(idBody)==false)
		    	throw new TestSuiteException("ID e-Gov presente nell'header del trasporto della ricevuta differisce dall'id egov presente nel messaggio OpenSPCoopOK della ricevuta.");
		}
		    
		// Identificativo della richiesta
	    String identificativoRichiestaAsincrona=msgRisposta.getProtocolHeaderInfo().getID();
      
	    Reporter.log("[AsincronoSimmetrico_modalitaAsincrona] Gestione richiesta con id="+identificativoRichiestaAsincrona+" effettata.");
		
	    //	Aspetto una risposta asincrona
	    // Ritira il messagggio dal ServerRicezioneRispostaAsincronaSimmetrica
	    Message receivedMessageRispostaAsincrona=this.repositoryConsegnaRisposteAsincroneSimmetriche_modalitaAsincrona.get(idPortaDelegataCorrelata); 
	    // Raccolgo identificativo della risposta asincrona
	    String identificativoRispostaAsincrona= org.openspcoop2.testsuite.core.Utilities.getValueFromHeaders(receivedMessageRispostaAsincrona,TestSuiteProperties.getInstance().getIdMessaggioTrasporto());
	    // Raccolgo riferimentoAsincrono della risposta asincrona
	    String riferimentoAsincrono= org.openspcoop2.testsuite.core.Utilities.getValueFromHeaders(receivedMessageRispostaAsincrona, TestSuiteProperties.getInstance().getRiferimentoAsincronoTrasporto());
	    // Check riferimentoAsincrono uguale a identificativo richiesta
	    if(identificativoRichiestaAsincrona.equals(riferimentoAsincrono)==false){
	    	throw new TestSuiteException("RiferimentoAsincrono ritornato con la risposta ["+riferimentoAsincrono+"] non e' uguale all'identificativo di correlazione atteso ["+identificativoRichiestaAsincrona+"]");
	    }
	    
	    // Attesa terminazione messaggi
	    org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties testsuiteProperties = 
	    	org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance();
	    org.openspcoop2.testsuite.core.TestSuiteProperties testsuitePropertiesLIB = 
	    	org.openspcoop2.testsuite.core.TestSuiteProperties.getInstance();
	    if(testsuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
	    	
	    	DatabaseComponent dbFruitore = null;
	    	DatabaseComponent dbErogatore = null;
	    	try {
	    	
	    		/** DatabaseComponent */
	    		dbFruitore = DatabaseProperties.getDatabaseComponentFruitore();
	    		dbErogatore = DatabaseProperties.getDatabaseComponentErogatore();
	    		
				if(dbFruitore!=null){
					long countTimeout = System.currentTimeMillis() + (1000* testsuitePropertiesLIB.getTimeoutProcessamentoMessaggiOpenSPCoop());
					while(dbFruitore.getVerificatoreMessaggi().countMsgOpenSPCoop_profiloAsincrono(identificativoRichiestaAsincrona,identificativoRispostaAsincrona)!=0 && countTimeout>System.currentTimeMillis()){
						try{
						Thread.sleep(testsuitePropertiesLIB.getCheckIntervalProcessamentoMessaggiOpenSPCoop());
						}catch(Exception e){}
					}
				}
				if(dbErogatore!=null){
					long countTimeout = System.currentTimeMillis() + (1000*testsuitePropertiesLIB.getTimeoutProcessamentoMessaggiOpenSPCoop());
					while(dbErogatore.getVerificatoreMessaggi().countMsgOpenSPCoop_profiloAsincrono(identificativoRichiestaAsincrona,identificativoRispostaAsincrona)!=0 && countTimeout>System.currentTimeMillis()){
						try{
							Thread.sleep(testsuitePropertiesLIB.getCheckIntervalProcessamentoMessaggiOpenSPCoop());
						}catch(Exception e){}
					}
				}
	    	}catch(Exception e){
	    		Reporter.log("[AsincronoSimmetrico_modalitaAsincrona] Errore durante il controllo: "+e.getMessage());
	    	}finally{
	    		try{
	    			if(dbFruitore!=null){
	    				dbFruitore.close();
	    			}
	    		}catch(Exception e){}
	    		try{
	    			if(dbErogatore!=null){
	    				dbErogatore.close();
	    			}
	    		}catch(Exception e){}
	    	}
		}
	    
	    // Metto i due id nel repository
	    this.repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaAsincrona.add(identificativoRichiestaAsincrona,identificativoRispostaAsincrona);
	    
	    Reporter.log("[AsincronoSimmetrico_modalitaAsincrona] Ricezione risposta correlata a "+identificativoRichiestaAsincrona+" , correlata a "+identificativoRichiestaAsincrona+" effettata.");
		
		
		// Test uguaglianza Body
		Reporter.log("Controllo risposta ottenuta");
		Assert.assertTrue(ClientCore.isEqualsSentAndResponseMessage(msgAxis,receivedMessageRispostaAsincrona));

	}
	@DataProvider (name="AsincronoSimmetrico_ModalitaAsincrona")
	public Object[][]testAsincronoSimmetrico_ModalitaAsincrona() throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaAsincrona.getNextIDRichiesta();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ASINCRONO_SIMMETRICO_asincr"},dataProvider="AsincronoSimmetrico_ModalitaAsincrona",dependsOnMethods={"asincronoSimmetrico_ModalitaAsincrona"})
	public void testAsincronoSimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testAsincronoSimmetrico_ModalitaAsincrona(data, id, 
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO,
					CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA , checkServizioApplicativo,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_CORRELATO_ASINCRONO_SIMMETRICO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_CORRELATO_ASINCRONO_SIMMETRICO,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}

	}
	@DataProvider (name="RispostaAsincronoSimmetrico_ModalitaAsincrona")
	public Object[][]testRispostaAsincronoSimmetrico_ModalitaAsincrona() throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaAsincrona.getNextIDRisposta();
		String idCorrelazioneAsincrona = this.repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaAsincrona.getIDRichiestaByReference(id);
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,idCorrelazioneAsincrona,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,idCorrelazioneAsincrona,true}	
		};
	}
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ASINCRONO_SIMMETRICO_asincr"},dataProvider="RispostaAsincronoSimmetrico_ModalitaAsincrona",dependsOnMethods={"testAsincronoSimmetrico_ModalitaAsincrona"})
	public void testRispostaAsincronoSimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testRispostaAsincronoSimmetrico_ModalitaAsincrona(data, id, 
					idCorrelazioneAsincrona,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_CORRELATO_ASINCRONO_SIMMETRICO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_CORRELATO_ASINCRONO_SIMMETRICO,
					CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA, 
					checkServizioApplicativo,this.repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaAsincrona,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@Test (groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ASINCRONO_SIMMETRICO_asincr"},dependsOnMethods={"testRispostaAsincronoSimmetrico_ModalitaAsincrona"})
	public void stopServerRicezioneRispostaAsincronaSimmetrica_modalitaAsincrona(){
		this.serverRicezioneRispostaAsincronaSimmetrica_modalitaAsincrona.closeSocket();
	}








	/***
	 * Test per il profilo di collaborazione Asincrono Simmetrico, modalita sincrona
	 */
	RepositoryConsegnaRisposteAsincroneSimmetriche repositoryConsegnaRisposteAsincroneSimmetriche_modalitaSincrona = new RepositoryConsegnaRisposteAsincroneSimmetriche(Utilities.testSuiteProperties.timeToSleep_repositoryAsincronoSimmetrico());
	RepositoryCorrelazioneIstanzeAsincrone repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaSincrona = new RepositoryCorrelazioneIstanzeAsincrone();
	ServerRicezioneRispostaAsincronaSimmetrica serverRicezioneRispostaAsincronaSimmetrica_modalitaSincrona = 
		new ServerRicezioneRispostaAsincronaSimmetrica(Utilities.testSuiteProperties.getWorkerNumber(),Utilities.testSuiteProperties.getSocketAsincronoSimmetrico_modalitaSincrona(),
				this.repositoryConsegnaRisposteAsincroneSimmetriche_modalitaSincrona);
	@Test (groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ASINCRONO_SIMMETRICO_sincr"})
	public void startServerRicezioneRispostaAsincronaSimmetrica_modalitaSincrona(){
		this.serverRicezioneRispostaAsincronaSimmetrica_modalitaSincrona.start();
	}
	@Test (groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ASINCRONO_SIMMETRICO_sincr"},description="Test di tipo asincrono simmetrico con modalita asincrona",dependsOnMethods="startServerRicezioneRispostaAsincronaSimmetrica_modalitaSincrona")
	public void asincronoSimmetrico_ModalitaSincrona() throws Exception{		
		Reporter.log("SOAPEngine axis14["+use_axis14_engine+"] cxf["+use_cxf_engine+"]");
		
		// Costruzione msg di richiesta
		SOAPEngine utility = new SOAPEngine(null);
		utility.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,addIDUnivoco);
				
		// Info PortaCorrelata per profilo asincrono simmetrico
		String idPortaDelegataCorrelata = 
			org.openspcoop2.testsuite.core.Utilities.getHeaderValue(
					org.openspcoop2.testsuite.core.CostantiTestSuite.UTILIZZO_INTEGRATION_MANAGER+
					CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_CORRELATO_MODALITA_SINCRONA,
					IntegrationManager.getCounter());
		utility.setIDTestSuiteHeader(idPortaDelegataCorrelata);
		
		// SPCoopMessage
		Message msgAxis = utility.getRequestMessage();
		org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage msg = new org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		msgAxis.writeTo(bout);
		bout.flush();
		bout.close();
		msg.setMessage(bout.toByteArray());
		
		// IntegrationManager
		org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage msgRisposta = null;
		if(use_axis14_engine){
			org.openspcoop2.pdd.services.axis14.PD_PortType im = getIntegrationManagerPD_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/PD"), 
					"profiloAsincrono_richiestaSincrona","123456");
			msgRisposta = im.invocaPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA, msg);
		}
		else{
			org.openspcoop2.pdd.services.cxf.PD im = getIntegrationManagerPD_cxf(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/PD"), 
					"profiloAsincrono_richiestaSincrona","123456");
			org.openspcoop2.pdd.services.cxf.IntegrationManagerMessage msgRisposta_cxf = im.invocaPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA, toIntegrationManagerMessage_cxf(msg));
			msgRisposta = toIntegrationManagerMessage_axis14(msgRisposta_cxf);
		}
		
		// Msg di risposta
		Message msgAxisResponse = Axis14SoapUtils.build(msgRisposta.getMessage(), false);
		
		// Test uguaglianza Body
		Reporter.log("Controllo risposta ottenuta");
		Assert.assertTrue(ClientCore.isEqualsSentAndResponseMessage(msgAxis,msgAxisResponse));
		
		// Identificativo della richiesta
	    String identificativoRichiestaAsincrona=msgRisposta.getProtocolHeaderInfo().getID();
      
	    Reporter.log("[AsincronoSimmetrico_modalitaSincrona] Gestione richiesta con id="+identificativoRichiestaAsincrona+" effettata.");
		
	    //	Aspetto una risposta asincrona
	    // Ritira il messagggio dal ServerRicezioneRispostaAsincronaSimmetrica
	    Message receivedMessageRispostaAsincrona=this.repositoryConsegnaRisposteAsincroneSimmetriche_modalitaSincrona.get(idPortaDelegataCorrelata); 
	    // Raccolgo identificativo della risposta asincrona
	    String identificativoRispostaAsincrona= org.openspcoop2.testsuite.core.Utilities.getValueFromHeaders(receivedMessageRispostaAsincrona,TestSuiteProperties.getInstance().getIdMessaggioTrasporto());
	    // Raccolgo riferimentoAsincrono della risposta asincrona
	    String riferimentoAsincrono= org.openspcoop2.testsuite.core.Utilities.getValueFromHeaders(receivedMessageRispostaAsincrona, TestSuiteProperties.getInstance().getRiferimentoAsincronoTrasporto());
	    // Check riferimentoAsincrono uguale a identificativo richiesta
	    if(identificativoRichiestaAsincrona.equals(riferimentoAsincrono)==false){
	    	throw new TestSuiteException("RiferimentoAsincrono ritornato con la risposta ["+riferimentoAsincrono+"] non e' uguale all'identificativo di correlazione atteso ["+identificativoRichiestaAsincrona+"]");
	    }
	    
	    // Attesa terminazione messaggi
	    org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties testsuiteProperties = 
	    	org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance();
	    org.openspcoop2.testsuite.core.TestSuiteProperties testsuitePropertiesLIB = 
	    	org.openspcoop2.testsuite.core.TestSuiteProperties.getInstance();
	    if(testsuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
	    	
	    	DatabaseComponent dbFruitore = null;
	    	DatabaseComponent dbErogatore = null;
	    	try {
	    	
	    		/** DatabaseComponent */
	    		dbFruitore = DatabaseProperties.getDatabaseComponentFruitore();
	    		dbErogatore = DatabaseProperties.getDatabaseComponentErogatore();
	    		
				if(dbFruitore!=null){
					long countTimeout = System.currentTimeMillis() + (1000* testsuitePropertiesLIB.getTimeoutProcessamentoMessaggiOpenSPCoop());
					while(dbFruitore.getVerificatoreMessaggi().countMsgOpenSPCoop_profiloAsincrono(identificativoRichiestaAsincrona,identificativoRispostaAsincrona)!=0 && countTimeout>System.currentTimeMillis()){
						try{
						Thread.sleep(testsuitePropertiesLIB.getCheckIntervalProcessamentoMessaggiOpenSPCoop());
						}catch(Exception e){}
					}
				}
				if(dbErogatore!=null){
					long countTimeout = System.currentTimeMillis() + (1000*testsuitePropertiesLIB.getTimeoutProcessamentoMessaggiOpenSPCoop());
					while(dbErogatore.getVerificatoreMessaggi().countMsgOpenSPCoop_profiloAsincrono(identificativoRichiestaAsincrona,identificativoRispostaAsincrona)!=0 && countTimeout>System.currentTimeMillis()){
						try{
							Thread.sleep(testsuitePropertiesLIB.getCheckIntervalProcessamentoMessaggiOpenSPCoop());
						}catch(Exception e){}
					}
				}
	    	}catch(Exception e){
	    		Reporter.log("[AsincronoSimmetrico_modalitaSincrona] Errore durante il controllo: "+e.getMessage());
	    	}finally{
	    		try{
	    			if(dbFruitore!=null){
	    				dbFruitore.close();
	    			}
	    		}catch(Exception e){}
	    		try{
	    			if(dbErogatore!=null){
	    				dbErogatore.close();
	    			}
	    		}catch(Exception e){}
	    	}
		}
	    
	    // Metto i due id nel repository
	    this.repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaSincrona.add(identificativoRichiestaAsincrona,identificativoRispostaAsincrona);
	    
	    Reporter.log("[AsincronoSimmetrico_modalitaSincrona] Ricezione risposta correlata a "+identificativoRichiestaAsincrona+" , correlata a "+identificativoRichiestaAsincrona+" effettata.");
		
		
		// Test uguaglianza Body
		Reporter.log("Controllo risposta ottenuta");
		Assert.assertTrue(ClientCore.isEqualsSentAndResponseMessage(msgAxis,receivedMessageRispostaAsincrona));
		
	}
	@DataProvider (name="AsincronoSimmetrico_ModalitaSincrona")
	public Object[][]testAsincronoSimmetrico_ModalitaSincrona() throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaSincrona.getNextIDRichiesta();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ASINCRONO_SIMMETRICO_sincr"},dataProvider="AsincronoSimmetrico_ModalitaSincrona",dependsOnMethods={"asincronoSimmetrico_ModalitaSincrona"})
	public void testAsincronoSimmetrico_ModalitaSincrona(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testAsincronoSimmetrico_ModalitaSincrona(data, id, 
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO,
					CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA, checkServizioApplicativo,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_CORRELATO_ASINCRONO_SIMMETRICO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_CORRELATO_ASINCRONO_SIMMETRICO,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@DataProvider (name="RispostaAsincronoSimmetrico_ModalitaSincrona")
	public Object[][]testRispostaAsincronoSimmetrico_ModalitaSincrona() throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaSincrona.getNextIDRisposta();
		String idCorrelazioneAsincrona = this.repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaSincrona.getIDRichiestaByReference(id);
		try {
			Thread.sleep(2000); // aspetto tempo per tracciamento ricevuta asincrona simmetrica risposta (caso particolare per ASmodAsincrona)
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,idCorrelazioneAsincrona,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,idCorrelazioneAsincrona,true}	
		};
	}
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ASINCRONO_SIMMETRICO_sincr"},dataProvider="RispostaAsincronoSimmetrico_ModalitaSincrona",dependsOnMethods={"testAsincronoSimmetrico_ModalitaSincrona"})
	public void testRispostaAsincronoSimmetrico_ModalitaSincrona(DatabaseComponent data,String id, String idCorrelazioneAsincrona,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testRispostaAsincronoSimmetrico_ModalitaSincrona(data, id, idCorrelazioneAsincrona, 
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_CORRELATO_ASINCRONO_SIMMETRICO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_CORRELATO_ASINCRONO_SIMMETRICO,
					CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@Test (groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ASINCRONO_SIMMETRICO_sincr"},dependsOnMethods={"testRispostaAsincronoSimmetrico_ModalitaSincrona"})
	public void stopServerRicezioneRispostaAsincronaSimmetrica_modalitaSincrona(){
		this.serverRicezioneRispostaAsincronaSimmetrica_modalitaSincrona.closeSocket();
	}







	/***
	 * Test per il profilo di collaborazione Asincrono Asimmetrico, modalita asincrona
	 */
	RepositoryCorrelazioneIstanzeAsincrone repositoryCorrelazioneIstanzeAsincroneAsimmetriche_modalitaAsincrona = new RepositoryCorrelazioneIstanzeAsincrone();
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ASINCRONO_ASIMMETRICO_asincr"})
	public void asincronoAsimmetrico_ModalitaAsincrona() throws TestSuiteException, Exception{
		Reporter.log("SOAPEngine axis14["+use_axis14_engine+"] cxf["+use_cxf_engine+"]");
		
		this.gestioneProfiloAsincronoAsimmetrico(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA, 
				CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_MODALITA_ASINCRONA, 
				false, "AsincronoAsimmetrico_modalitaAsincrona", this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_modalitaAsincrona);
	}
	@DataProvider (name="AsincronoAsimmetrico_ModalitaAsincrona")
	public Object[][]testAsincronoAsimmetrico_ModalitaAsincrona()throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_modalitaAsincrona.getNextIDRichiesta();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ASINCRONO_ASIMMETRICO_asincr"},dataProvider="AsincronoAsimmetrico_ModalitaAsincrona",dependsOnMethods={"asincronoAsimmetrico_ModalitaAsincrona"})
	public void testAsincronoAsimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testAsincronoAsimmetrico_ModalitaAsincrona(data, id, 
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO,
					CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_ASINCRONA, checkServizioApplicativo,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_CORRELATO_ASINCRONO_ASIMMETRICO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_CORRELATO_ASINCRONO_ASIMMETRICO,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@DataProvider (name="RispostaAsincronoAsimmetrico_ModalitaAsincrona")
	public Object[][]testRispostaAsincronoAsimmetrico_ModalitaAsincrona() throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_modalitaAsincrona.getNextIDRisposta();
		String idCorrelazioneAsincrona = this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_modalitaAsincrona.getIDRichiestaByReference(id);
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,idCorrelazioneAsincrona,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,idCorrelazioneAsincrona,true}	
		};
	}
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ASINCRONO_ASIMMETRICO_asincr"},dataProvider="RispostaAsincronoAsimmetrico_ModalitaAsincrona",dependsOnMethods={"testAsincronoAsimmetrico_ModalitaAsincrona"})
	public void testRispostaAsincronoAsimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testRispostaAsincronoAsimmetrico_ModalitaAsincrona(data, id, idCorrelazioneAsincrona,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_CORRELATO_ASINCRONO_ASIMMETRICO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_CORRELATO_ASINCRONO_ASIMMETRICO,
					CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_ASINCRONA, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}






	/***
	 * Test per il profilo di collaborazione Asincrono Asimmetrico, modalita sincrona
	 */
	RepositoryCorrelazioneIstanzeAsincrone repositoryCorrelazioneIstanzeAsincroneAsimmetriche_modalitaSincrona = new RepositoryCorrelazioneIstanzeAsincrone();
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ASINCRONO_ASIMMETRICO_sincr"})
	public void asincronoAsimmetrico_modalitaSincrona() throws Exception{
		Reporter.log("SOAPEngine axis14["+use_axis14_engine+"] cxf["+use_cxf_engine+"]");
		this.gestioneProfiloAsincronoAsimmetrico(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA, 
				CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_MODALITA_SINCRONA, 
				true, "AsincronoAsimmetrico_modalitaSincrona", this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_modalitaSincrona);
	}
	@DataProvider (name="AsincronoAsimmetrico_modalitaSincrona")
	public Object[][]testAsincronoAsimmetrico_modalitaSincrona()throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_modalitaSincrona.getNextIDRichiesta();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ASINCRONO_ASIMMETRICO_sincr"},dataProvider="AsincronoAsimmetrico_modalitaSincrona",dependsOnMethods={"asincronoAsimmetrico_modalitaSincrona"})
	public void testAsincronoAsimmetrico_modalitaSincrona(DatabaseComponent data,String id, boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testAsincronoAsimmetrico_modalitaSincrona(data, id, 
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO,
					CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_SINCRONA,checkServizioApplicativo,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_CORRELATO_ASINCRONO_ASIMMETRICO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_CORRELATO_ASINCRONO_ASIMMETRICO,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@DataProvider (name="RispostaAsincronoAsimmetrico_modalitaSincrona")
	public Object[][]testRispostaAsincronoAsimmetrico_modalitaSincrona() throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_modalitaSincrona.getNextIDRisposta();
		String idCorrelazioneAsincrona = this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_modalitaSincrona.getIDRichiestaByReference(id);
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,idCorrelazioneAsincrona,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,idCorrelazioneAsincrona,true}	
		};
	}
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ASINCRONO_ASIMMETRICO_sincr"},dataProvider="RispostaAsincronoAsimmetrico_modalitaSincrona",dependsOnMethods={"testAsincronoAsimmetrico_modalitaSincrona"})
	public void testRispostaAsincronoAsimmetrico_modalitaSincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testRispostaAsincronoAsimmetrico_modalitaSincrona(data, id, idCorrelazioneAsincrona,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_CORRELATO_ASINCRONO_ASIMMETRICO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_CORRELATO_ASINCRONO_ASIMMETRICO,
					CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_SINCRONA,checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}


	
	
	


	/***
	 * Test per il profilo di collaborazione OneWay su configurazione loopback
	 */
	Repository repositoryOneWayLoopback=new Repository();
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ONEWAY_LOOPBACK"})
	public void oneWayLoopback() throws TestSuiteException, Exception{		
		Reporter.log("SOAPEngine axis14["+use_axis14_engine+"] cxf["+use_cxf_engine+"]");
		gestioneProfiloOneway(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_LOOPBACK, null, null, this.repositoryOneWayLoopback,null,true);
	}
	@DataProvider (name="OneWayLoopback")
	public Object[][]testOneWayLoopback() throws Exception{
		String id=this.repositoryOneWayLoopback.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true},	
		};
	}
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ONEWAY_LOOPBACK"},dataProvider="OneWayLoopback",dependsOnMethods={"oneWayLoopback"})
	public void testOneWayLoopback(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("[SOAP] Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("[SOAP] Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("[SOAP] Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("[SOAP] Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id,datiServizio));
			Reporter.log("[SOAP] Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("[SOAP] Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("[SOAP] Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			if(checkServizioApplicativo){
				Reporter.log("[SOAP] Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			}
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}

	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Asincrono Asimmetrico, modalita asincrona (azione correlata)
	 */
	RepositoryCorrelazioneIstanzeAsincrone repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaAsincrona = new RepositoryCorrelazioneIstanzeAsincrone();
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ASINCRONO_ASIMMETRICO_asin_azCorrelata"})
	public void asincronoAsimmetrico_AzioneCorrelata_ModalitaAsincrona() throws TestSuiteException, Exception{
		Reporter.log("SOAPEngine axis14["+use_axis14_engine+"] cxf["+use_cxf_engine+"]");
		this.gestioneProfiloAsincronoAsimmetrico(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_AZIONE_CORRELATA_MODALITA_ASINCRONA, 
				CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_AZIONE_CORRELATA_MODALITA_ASINCRONA, 
				false, "AsincronoAsimmetrico_modalitaAsincrona_azioneCorrelata", this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaAsincrona);
	}
	@DataProvider (name="AsincronoAsimmetrico_AzioneCorrelata_ModalitaAsincrona")
	public Object[][]testAsincronoAsimmetrico_AzioneCorrelata_ModalitaAsincrona()throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaAsincrona.getNextIDRichiesta();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ASINCRONO_ASIMMETRICO_asin_azCorrelata"},dataProvider="AsincronoAsimmetrico_AzioneCorrelata_ModalitaAsincrona",dependsOnMethods={"asincronoAsimmetrico_AzioneCorrelata_ModalitaAsincrona"})
	public void testAsincronoAsimmetrico_AzioneCorrelata_ModalitaAsincrona(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testAsincronoAsimmetrico_ModalitaAsincrona(data, id, 
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO,
					CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_AZIONE_CORRELATA_MODALITA_ASINCRONA, checkServizioApplicativo,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@DataProvider (name="RispostaAsincronoAsimmetrico_AzioneCorrelata_ModalitaAsincrona")
	public Object[][]testRispostaAsincronoAsimmetrico_AzioneCorrelata_ModalitaAsincrona() throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaAsincrona.getNextIDRisposta();
		String idCorrelazioneAsincrona = this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaAsincrona.getIDRichiestaByReference(id);
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,idCorrelazioneAsincrona,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,idCorrelazioneAsincrona,true}	
		};
	}
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ASINCRONO_ASIMMETRICO_asin_azCorrelata"},dataProvider="RispostaAsincronoAsimmetrico_AzioneCorrelata_ModalitaAsincrona",dependsOnMethods={"testAsincronoAsimmetrico_AzioneCorrelata_ModalitaAsincrona"})
	public void testRispostaAsincronoAsimmetrico_AzioneCorrelata_ModalitaAsincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testRispostaAsincronoAsimmetrico_ModalitaAsincrona(data, id, idCorrelazioneAsincrona,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO,
					CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_CORRELATA_AZIONE_CORRELATA_MODALITA_ASINCRONA, 
					checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Asincrono Asimmetrico, modalita sincrona (azione correlata)
	 */
	RepositoryCorrelazioneIstanzeAsincrone repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaSincrona = new RepositoryCorrelazioneIstanzeAsincrone();
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ASINCRONO_ASIMMETRICO_sin_azCorrelata"})
	public void asincronoAsimmetrico_AzioneCorrelata_modalitaSincrona() throws Exception{
		Reporter.log("SOAPEngine axis14["+use_axis14_engine+"] cxf["+use_cxf_engine+"]");
		this.gestioneProfiloAsincronoAsimmetrico(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_AZIONE_CORRELATA_MODALITA_SINCRONA, 
				CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_AZIONE_CORRELATA_MODALITA_SINCRONA, 
				true, "AsincronoAsimmetrico_modalitaSincrona_azioneCorrelata", this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaSincrona);
	}
	@DataProvider (name="AsincronoAsimmetrico_AzioneCorrelata_modalitaSincrona")
	public Object[][]testAsincronoAsimmetrico_AzioneCorrelata_modalitaSincrona()throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaSincrona.getNextIDRichiesta();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ASINCRONO_ASIMMETRICO_sin_azCorrelata"},dataProvider="AsincronoAsimmetrico_AzioneCorrelata_modalitaSincrona",dependsOnMethods={"asincronoAsimmetrico_AzioneCorrelata_modalitaSincrona"})
	public void testAsincronoAsimmetrico_AzioneCorrelata_modalitaSincrona(DatabaseComponent data,String id, boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testAsincronoAsimmetrico_modalitaSincrona(data, id, 
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO,
					CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_AZIONE_CORRELATA_MODALITA_SINCRONA,checkServizioApplicativo,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@DataProvider (name="RispostaAsincronoAsimmetrico_AzioneCorrelata_modalitaSincrona")
	public Object[][]testRispostaAsincronoAsimmetrico_AzioneCorrelata_modalitaSincrona() throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaSincrona.getNextIDRisposta();
		String idCorrelazioneAsincrona = this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaSincrona.getIDRichiestaByReference(id);
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,idCorrelazioneAsincrona,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,idCorrelazioneAsincrona,true}	
		};
	}
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".ASINCRONO_ASIMMETRICO_sin_azCorrelata"},dataProvider="RispostaAsincronoAsimmetrico_AzioneCorrelata_modalitaSincrona",dependsOnMethods={"testAsincronoAsimmetrico_AzioneCorrelata_modalitaSincrona"})
	public void testRispostaAsincronoAsimmetrico_AzioneCorrelata_modalitaSincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testRispostaAsincronoAsimmetrico_modalitaSincrona(data, id, idCorrelazioneAsincrona,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO,
					CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_CORRELATA_AZIONE_CORRELATA_MODALITA_SINCRONA,
					checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// MESSAGE BOX
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".MESSAGE_BOX"})
	public void message_box() throws Exception{
		Reporter.log("SOAPEngine axis14["+use_axis14_engine+"] cxf["+use_cxf_engine+"]");
		
		// IntegrationManager
		org.openspcoop2.pdd.services.axis14.MessageBox_PortType imSilGop1_axis14 = null;
		org.openspcoop2.pdd.services.axis14.MessageBox_PortType imSilGop2_axis14 = null;
		org.openspcoop2.pdd.services.axis14.MessageBox_PortType imSilGop3_axis14 = null;
		org.openspcoop2.pdd.services.axis14.MessageBox_PortType imSilCredenzialiErrate_axis14 = null;
		if(use_axis14_engine){
			imSilGop1_axis14 = getIntegrationManagerMessageBox_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/MessageBox"), 
				"gop1","123456");
			imSilGop2_axis14 = getIntegrationManagerMessageBox_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/MessageBox"), 
				"gop2","123456");
			imSilGop3_axis14 = getIntegrationManagerMessageBox_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/MessageBox"), 
				"gop3","123456");
			imSilCredenzialiErrate_axis14 = getIntegrationManagerMessageBox_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/MessageBox"), 
				"usernameNonEsistente","passwordErrata");
		}
		org.openspcoop2.pdd.services.cxf.MessageBox imSilGop1_cxf = null;
		org.openspcoop2.pdd.services.cxf.MessageBox imSilGop2_cxf = null;
		org.openspcoop2.pdd.services.cxf.MessageBox imSilGop3_cxf = null;
		org.openspcoop2.pdd.services.cxf.MessageBox imSilCredenzialiErrate_cxf = null;
		if(use_cxf_engine){
			imSilGop1_cxf = getIntegrationManagerMessageBox_cxf(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/MessageBox"), 
				"gop1","123456");
			imSilGop2_cxf = getIntegrationManagerMessageBox_cxf(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/MessageBox"), 
				"gop2","123456");
			imSilGop3_cxf = getIntegrationManagerMessageBox_cxf(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/MessageBox"), 
				"gop3","123456");
			imSilCredenzialiErrate_cxf = getIntegrationManagerMessageBox_cxf(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/MessageBox"), 
				"usernameNonEsistente","passwordErrata");
		}
		
		
		// Costruzione msg di richiesta
		SOAPEngine utility = new SOAPEngine(null);
		utility.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,addIDUnivoco);
		Message msgAxis = utility.getRequestMessage();
		SOAPEngine utility2 = new SOAPEngine(null);
		utility2.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,addIDUnivoco);
		Message msgAxis2 = utility.getRequestMessage();
		SOAPEngine utility3 = new SOAPEngine(null);
		utility3.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,addIDUnivoco);
		Message msgAxis3 = utility.getRequestMessage();
		SOAPEngine utility4 = new SOAPEngine(null);
		utility4.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,addIDUnivoco);
		Message msgAxis4 = utility.getRequestMessage();
		
		String [] idMsgSilGop1 = null;
		String [] idMsgSilGop3 = null;
		
		
		
		
		// Pulizia repository per test
		if(use_axis14_engine){
			try{
				imSilGop1_axis14.deleteAllMessages();
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				Reporter.log("Pulizia repository SilGop1 MessageBox non riuscito, codice eccezione: "+e.getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getCodiceEccezione()));
			}
		}
		else if(use_cxf_engine){
			try{
				imSilGop1_cxf.deleteAllMessages();
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				Reporter.log("Pulizia repository SilGop1 MessageBox non riuscito, codice eccezione: "+e.getFaultInfo().getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getFaultInfo().getCodiceEccezione()));
			}
		}
		
		if(use_axis14_engine){
			try{
				imSilGop3_axis14.deleteAllMessages();
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				Reporter.log("Pulizia repository SilGop3 MessageBox non riuscito, codice eccezione: "+e.getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getCodiceEccezione()));
			}
		}
		else if(use_cxf_engine){
			try{
				imSilGop3_cxf.deleteAllMessages();
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				Reporter.log("Pulizia repository SilGop3 MessageBox non riuscito, codice eccezione: "+e.getFaultInfo().getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getFaultInfo().getCodiceEccezione()));
			}
		}
		
		
		
		
		
		
		
		Reporter.log("0. Condizioni di errore...");
		if(use_axis14_engine){
			try{
				imSilCredenzialiErrate_axis14.getAllMessagesId();
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				Reporter.log("0. Condizioni di errore in MessageBox per UsernameErroto non riuscito, codice eccezione: "+e.getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA).equals(e.getCodiceEccezione()));
			}
		}
		else if(use_cxf_engine){
			try{
				imSilCredenzialiErrate_cxf.getAllMessagesId();
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				Reporter.log("0. Condizioni di errore in MessageBox per UsernameErroto non riuscito, codice eccezione: "+e.getFaultInfo().getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA).equals(e.getFaultInfo().getCodiceEccezione()));
			}
		}
		
		if(use_axis14_engine){
			try{
				imSilGop1_axis14.getMessage("IDEGOV_XXXXX_NON_ESISTENTE_XXXX");
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				Reporter.log("0. Condizioni di errore in MessageBox per ID non esistente non riuscito, codice eccezione: "+e.getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO).equals(e.getCodiceEccezione()));
			}
		}
		else if(use_cxf_engine){
			try{
				imSilGop1_cxf.getMessage("IDEGOV_XXXXX_NON_ESISTENTE_XXXX");
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				Reporter.log("0. Condizioni di errore in MessageBox per ID non esistente non riuscito, codice eccezione: "+e.getFaultInfo().getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO).equals(e.getFaultInfo().getCodiceEccezione()));
			}
		}
		
		if(use_axis14_engine){
			try{
				imSilGop1_axis14.deleteMessage("IDEGOV_XXXXX_NON_ESISTENTE_XXXX");
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				Reporter.log("0. Condizioni di errore in MessageBox per ID non esistente non riuscito, codice eccezione: "+e.getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO).equals(e.getCodiceEccezione()));
			}
		}
		else if(use_cxf_engine){
			try{
				imSilGop1_cxf.deleteMessage("IDEGOV_XXXXX_NON_ESISTENTE_XXXX");
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				Reporter.log("0. Condizioni di errore in MessageBox per ID non esistente non riuscito, codice eccezione: "+e.getFaultInfo().getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO).equals(e.getFaultInfo().getCodiceEccezione()));
			}
		}
		
		
		
		
		
		
		Reporter.log("1. Prelevo messaggi in MessageBox, non presenti...");
		if(use_axis14_engine){
			try{
				imSilGop1_axis14.getAllMessagesId();
				throw new Exception("1. getAllMessageId per SilGop1 non ha generato SPCoopException");
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				Reporter.log("1. Prelevo messaggi in MessageBox per SilGop1 non riuscito, codice eccezione: "+e.getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getCodiceEccezione()));
			}
		}
		else if(use_cxf_engine){
			try{
				imSilGop1_cxf.getAllMessagesId();
				throw new Exception("1. getAllMessageId per SilGop1 non ha generato SPCoopException");
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				Reporter.log("1. Prelevo messaggi in MessageBox per SilGop1 non riuscito, codice eccezione: "+e.getFaultInfo().getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getFaultInfo().getCodiceEccezione()));
			}
		}
			
		if(use_axis14_engine){
			try{
				imSilGop2_axis14.getAllMessagesId();
				throw new Exception("1. getAllMessageId per SilGop2 non ha generato SPCoopException");
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				Reporter.log("1. Prelevo messaggi per SilGop2 in MessageBox non riuscito, codice eccezione: "+e.getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getCodiceEccezione()));
			}
		}
		else if(use_cxf_engine){
			try{
				imSilGop2_cxf.getAllMessagesId();
				throw new Exception("1. getAllMessageId per SilGop2 non ha generato SPCoopException");
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				Reporter.log("1. Prelevo messaggi per SilGop2 in MessageBox non riuscito, codice eccezione: "+e.getFaultInfo().getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getFaultInfo().getCodiceEccezione()));
			}
		}
		
		if(use_axis14_engine){
			try{
				imSilGop3_axis14.getAllMessagesId();
				throw new Exception("1. getAllMessageId per SilGop3 non ha generato SPCoopException");
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				Reporter.log("1. Prelevo messaggi per SilGop3 in MessageBox non riuscito, codice eccezione: "+e.getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getCodiceEccezione()));
			}
		}
		else if(use_cxf_engine){
			try{
				imSilGop3_cxf.getAllMessagesId();
				throw new Exception("1. getAllMessageId per SilGop3 non ha generato SPCoopException");
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				Reporter.log("1. Prelevo messaggi per SilGop3 in MessageBox non riuscito, codice eccezione: "+e.getFaultInfo().getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getFaultInfo().getCodiceEccezione()));
			}
		}
		
		
		
		
		Reporter.log("2. Pubblicazione messaggio in MessageBox...");
		String idEGov = gestioneProfiloOneway(CostantiTestSuite.PORTA_DELEGATA_MESSAGE_BOX, null, null, null,msgAxis,false);
		try{
			Thread.sleep(10000);
		}catch(Exception e){}
		Reporter.log("2. Pubblicazione messaggio in MessageBox eseguito per id:"+idEGov);
		
		
		
		
		
		Reporter.log("3. Prelevo id messaggi in MessageBox, con identita SilGop1...");
		if(use_axis14_engine){
			try{
				idMsgSilGop1 = imSilGop1_axis14.getAllMessagesId();
				if(idMsgSilGop1==null || idMsgSilGop1.length<=0){
					throw new Exception("Messaggi non presenti per SilGop1");
				}
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop1 ["+e.getCodiceEccezione()+"]: "+e.getDescrizioneEccezione());
			}
		}
		else if(use_cxf_engine){
			try{
				idMsgSilGop1 = toArray(imSilGop1_cxf.getAllMessagesId());
				if(idMsgSilGop1==null || idMsgSilGop1.length<=0){
					throw new Exception("Messaggi non presenti per SilGop1");
				}
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop1 ["+e.getFaultInfo().getCodiceEccezione()+"]: "+e.getFaultInfo().getDescrizioneEccezione());
			}
		}
		Reporter.log("Numero messaggi presenti per SilGop1:"+idMsgSilGop1.length);
		Assert.assertTrue(idMsgSilGop1.length==1);
		Reporter.log("ID eGov ritornato["+idMsgSilGop1[0]+"] idAtteso["+idEGov+"]");
		Assert.assertTrue(idEGov.equals(idMsgSilGop1[0]));
		
		Reporter.log("3. Prelevo id messaggi in MessageBox, con identita SilGop3...");
		if(use_axis14_engine){
			try{
				idMsgSilGop3 = imSilGop3_axis14.getAllMessagesId();
				if(idMsgSilGop3==null || idMsgSilGop3.length<=0){
					throw new Exception("Messaggi non presenti per SilGop3");
				}
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop3 ["+e.getCodiceEccezione()+"]: "+e.getDescrizioneEccezione());
			}
		}
		else if(use_cxf_engine){
			try{
				idMsgSilGop3 = toArray(imSilGop3_cxf.getAllMessagesId());
				if(idMsgSilGop3==null || idMsgSilGop3.length<=0){
					throw new Exception("Messaggi non presenti per SilGop3");
				}
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop3 ["+e.getFaultInfo().getCodiceEccezione()+"]: "+e.getFaultInfo().getDescrizioneEccezione());
			}
		}
		Reporter.log("Numero messaggi presenti per SilGop3:"+idMsgSilGop1.length);
		Assert.assertTrue(idMsgSilGop3.length==1);
		Reporter.log("ID eGov ritornato["+idMsgSilGop3[0]+"] idAtteso["+idEGov+"]");
		Assert.assertTrue(idEGov.equals(idMsgSilGop3[0]));
		
		
		
		
		
		
		Reporter.log("4. Pubblicazione messaggio numero#2 in MessageBox...");
		String idEGov2 = gestioneProfiloOneway(CostantiTestSuite.PORTA_DELEGATA_MESSAGE_BOX, null, null, null,msgAxis2,false);
		try{
			Thread.sleep(10000);
		}catch(Exception e){}
		Reporter.log("4. Pubblicazione messaggio numero#2 in MessageBox eseguito per id:"+idEGov2);
		
		
		
		
		
		Reporter.log("5. Prelevo id messaggi in MessageBox, con identita SilGop1...");
		if(use_axis14_engine){
			try{
				idMsgSilGop1 = imSilGop1_axis14.getAllMessagesId();
				if(idMsgSilGop1==null || idMsgSilGop1.length<=0){
					throw new Exception("Messaggi non presenti per SilGop1");
				}
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop1 ["+e.getCodiceEccezione()+"]: "+e.getDescrizioneEccezione());
			}
		}
		else if(use_cxf_engine){
			try{
				idMsgSilGop1 = toArray(imSilGop1_cxf.getAllMessagesId());
				if(idMsgSilGop1==null || idMsgSilGop1.length<=0){
					throw new Exception("Messaggi non presenti per SilGop1");
				}
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop1 ["+e.getFaultInfo().getCodiceEccezione()+"]: "+e.getFaultInfo().getDescrizioneEccezione());
			}
		}
		Reporter.log("Numero messaggi presenti per SilGop1:"+idMsgSilGop1.length);
		Assert.assertTrue(idMsgSilGop1.length==2);
		Reporter.log("ID eGov ritornato["+idMsgSilGop1[0]+"] idAtteso["+idEGov+"]");
		Assert.assertTrue(idEGov.equals(idMsgSilGop1[0]));
		Reporter.log("ID eGov ritornato["+idMsgSilGop1[1]+"] idAtteso["+idEGov2+"]");
		Assert.assertTrue(idEGov2.equals(idMsgSilGop1[1]));
		
		Reporter.log("5. Prelevo id messaggi in MessageBox, con identita SilGop3...");
		if(use_axis14_engine){
			try{
				idMsgSilGop3 = imSilGop3_axis14.getAllMessagesId();
				if(idMsgSilGop3==null || idMsgSilGop3.length<=0){
					throw new Exception("Messaggi non presenti per SilGop3");
				}
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop3 ["+e.getCodiceEccezione()+"]: "+e.getDescrizioneEccezione());
			}
		}
		else if(use_cxf_engine){
			try{
				idMsgSilGop3 = toArray(imSilGop3_cxf.getAllMessagesId());
				if(idMsgSilGop3==null || idMsgSilGop3.length<=0){
					throw new Exception("Messaggi non presenti per SilGop3");
				}
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop3 ["+e.getFaultInfo().getCodiceEccezione()+"]: "+e.getFaultInfo().getDescrizioneEccezione());
			}
		}
		Reporter.log("Numero messaggi presenti per SilGop3:"+idMsgSilGop3.length);
		Assert.assertTrue(idMsgSilGop3.length==2);
		Reporter.log("ID eGov ritornato["+idMsgSilGop3[0]+"] idAtteso["+idEGov+"]");
		Assert.assertTrue(idEGov.equals(idMsgSilGop3[0]));
		Reporter.log("ID eGov ritornato["+idMsgSilGop3[1]+"] idAtteso["+idEGov2+"]");
		Assert.assertTrue(idEGov2.equals(idMsgSilGop3[1]));
		
		
		
		
		
		
		Reporter.log("6. Prelevamento messaggi in MessageBox ...");
		
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop1 e id ["+idMsgSilGop1[0]+"]...");
		this.utilityGetAndTestMsgInBOX(imSilGop1_axis14, imSilGop1_cxf, idMsgSilGop1[0], msgAxis,false,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRATION_MANAGER);
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop1 e id ["+idMsgSilGop1[0]+"] effettuato.");
		
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop1 e id ["+idMsgSilGop1[1]+"]...");
		this.utilityGetAndTestMsgInBOX(imSilGop1_axis14, imSilGop1_cxf, idMsgSilGop1[1], msgAxis2,false,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRATION_MANAGER);
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop1 e id ["+idMsgSilGop1[1]+"] effettuato.");
		
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop3 e id ["+idMsgSilGop3[0]+"]...");
		this.utilityGetAndTestMsgInBOX(imSilGop3_axis14, imSilGop3_cxf, idMsgSilGop3[0], msgAxis,true,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRATION_MANAGER); // il msg ritorna sbustato
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop3 e id ["+idMsgSilGop3[0]+"] effettuato.");
		
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop3 e id ["+idMsgSilGop3[1]+"]...");
		this.utilityGetAndTestMsgInBOX(imSilGop3_axis14, imSilGop3_cxf, idMsgSilGop3[1], msgAxis2,true,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRATION_MANAGER); // il msg ritorna sbustato
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop3 e id ["+idMsgSilGop3[1]+"] effettuato.");
		
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop2 e id ["+idMsgSilGop1[0]+"]...");
		try{
			this.utilityGetAndTestMsgInBOX(imSilGop2_axis14, imSilGop2_cxf, idMsgSilGop1[0], msgAxis,false,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRATION_MANAGER);
			throw new Exception("6. Prelevo messaggio con identita SilGop2 non ha generato SPCoopException");
		}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
			Reporter.log("Prelevo messaggi in MessageBox con SilGop2 non riuscito, codice eccezione: "+e.getCodiceEccezione());
			Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO).equals(e.getCodiceEccezione()));
		}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
			Reporter.log("Prelevo messaggi in MessageBox con SilGop2 non riuscito, codice eccezione: "+e.getFaultInfo().getCodiceEccezione());
			Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO).equals(e.getFaultInfo().getCodiceEccezione()));
		}
		
		Reporter.log("6. Prelevamento messaggi in MessageBox terminato");
		
		
		
		
		
		
		Reporter.log("7. Pubblicazione messaggio numero#3 in MessageBox per Nuovo servizio e Azione1 (Sottoscrive solo SilGop1) (INVOCAZIONE DINAMICA!)...");
		String idEGov3 = gestioneProfiloOneway(CostantiTestSuite.PORTA_DELEGATA_MESSAGE_BOX_GET_MESSAGE1, null, null, null,msgAxis3,false,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_MESSAGE_BOX,CostantiTestSuite.SPCOOP_NOME_SERVIZIO_MESSAGE_BOX,
				CostantiTestSuite.SPCOOP_SERVIZIO_MESSAGE_BOX_AZIONE_INTEGRATION_MANAGER);
		try{
			Thread.sleep(10000);
		}catch(Exception e){}
		Reporter.log("7. Pubblicazione messaggio numero#3 in MessageBox eseguito per id:"+idEGov3);
		
		Reporter.log("7. Pubblicazione messaggio numero#4 in MessageBox per Nuovo servizio e Azione2 (Sottoscrive solo SilGop1) (INVOCAZIONE DINAMICA!)...");
		String idEGov4 = gestioneProfiloOneway(CostantiTestSuite.PORTA_DELEGATA_MESSAGE_BOX_GET_MESSAGE2, null, null, null,msgAxis4,false,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_MESSAGE_BOX,CostantiTestSuite.SPCOOP_NOME_SERVIZIO_MESSAGE_BOX,
				CostantiTestSuite.SPCOOP_SERVIZIO_MESSAGE_BOX_AZIONE2_INTEGRATION_MANAGER);
		try{
			Thread.sleep(10000);
		}catch(Exception e){}
		Reporter.log("7. Pubblicazione messaggio numero#4 in MessageBox eseguito per id:"+idEGov4);
		
		
		
		
		
		
		
		Reporter.log("8. Prelevo id messaggi in MessageBox, con identita SilGop1...");
		if(use_axis14_engine){
			try{
				idMsgSilGop1 = imSilGop1_axis14.getAllMessagesId();
				if(idMsgSilGop1==null || idMsgSilGop1.length<=0){
					throw new Exception("Messaggi non presenti per SilGop1");
				}
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop1 ["+e.getCodiceEccezione()+"]: "+e.getDescrizioneEccezione());
			}
		}
		else if(use_cxf_engine){
			try{
				idMsgSilGop1 = toArray(imSilGop1_cxf.getAllMessagesId());
				if(idMsgSilGop1==null || idMsgSilGop1.length<=0){
					throw new Exception("Messaggi non presenti per SilGop1");
				}
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop1 ["+e.getFaultInfo().getCodiceEccezione()+"]: "+e.getFaultInfo().getDescrizioneEccezione());
			}
		}
		Reporter.log("Numero messaggi presenti per SilGop1:"+idMsgSilGop1.length);
		Assert.assertTrue(idMsgSilGop1.length==4);
		Reporter.log("ID eGov ritornato["+idMsgSilGop1[0]+"] idAtteso["+idEGov+"]");
		Assert.assertTrue(idEGov.equals(idMsgSilGop1[0]));
		Reporter.log("ID eGov ritornato["+idMsgSilGop1[1]+"] idAtteso["+idEGov2+"]");
		Assert.assertTrue(idEGov2.equals(idMsgSilGop1[1]));
		Reporter.log("ID eGov ritornato["+idMsgSilGop1[2]+"] idAtteso["+idEGov3+"]");
		Assert.assertTrue(idEGov3.equals(idMsgSilGop1[2]));
		Reporter.log("ID eGov ritornato["+idMsgSilGop1[3]+"] idAtteso["+idEGov4+"]");
		Assert.assertTrue(idEGov4.equals(idMsgSilGop1[3]));
		
		Reporter.log("8. Prelevo id messaggi in MessageBox, con identita SilGop3...");
		if(use_axis14_engine){
			try{
				idMsgSilGop3 = imSilGop3_axis14.getAllMessagesId();
				if(idMsgSilGop3==null || idMsgSilGop3.length<=0){
					throw new Exception("Messaggi non presenti per SilGop3");
				}
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop3 ["+e.getCodiceEccezione()+"]: "+e.getDescrizioneEccezione());
			}
		}
		else if(use_cxf_engine){
			try{
				idMsgSilGop3 = toArray(imSilGop3_cxf.getAllMessagesId());
				if(idMsgSilGop3==null || idMsgSilGop3.length<=0){
					throw new Exception("Messaggi non presenti per SilGop3");
				}
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop3 ["+e.getFaultInfo().getCodiceEccezione()+"]: "+e.getFaultInfo().getDescrizioneEccezione());
			}
		}
		Reporter.log("Numero messaggi presenti per SilGop3:"+idMsgSilGop3.length);
		Assert.assertTrue(idMsgSilGop3.length==2);
		Reporter.log("ID eGov ritornato["+idMsgSilGop3[0]+"] idAtteso["+idEGov+"]");
		Assert.assertTrue(idEGov.equals(idMsgSilGop3[0]));
		Reporter.log("ID eGov ritornato["+idMsgSilGop3[1]+"] idAtteso["+idEGov2+"]");
		Assert.assertTrue(idEGov2.equals(idMsgSilGop3[1]));
		
		
		
		
		
		
		Reporter.log("9. Prelevamento messaggi in MessageBox ...");
		
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop1 e id ["+idMsgSilGop1[0]+"]...");
		this.utilityGetAndTestMsgInBOX(imSilGop1_axis14, imSilGop1_cxf, idMsgSilGop1[0], msgAxis,false,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRATION_MANAGER);
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop1 e id ["+idMsgSilGop1[0]+"] effettuato.");
		
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop1 e id ["+idMsgSilGop1[1]+"]...");
		this.utilityGetAndTestMsgInBOX(imSilGop1_axis14, imSilGop1_cxf, idMsgSilGop1[1], msgAxis2,false,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRATION_MANAGER);
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop1 e id ["+idMsgSilGop1[1]+"] effettuato.");
		
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop1 e id ["+idMsgSilGop1[2]+"]...");
		this.utilityGetAndTestMsgInBOX(imSilGop1_axis14, imSilGop1_cxf, idMsgSilGop1[2], msgAxis3,false,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_MESSAGE_BOX,CostantiTestSuite.SPCOOP_SERVIZIO_MESSAGE_BOX_AZIONE_INTEGRATION_MANAGER);
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop1 e id ["+idMsgSilGop1[2]+"] effettuato.");
		
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop1 e id ["+idMsgSilGop1[3]+"]...");
		this.utilityGetAndTestMsgInBOX(imSilGop1_axis14, imSilGop1_cxf, idMsgSilGop1[3], msgAxis4,false,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_MESSAGE_BOX,CostantiTestSuite.SPCOOP_SERVIZIO_MESSAGE_BOX_AZIONE2_INTEGRATION_MANAGER);
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop1 e id ["+idMsgSilGop1[3]+"] effettuato.");
		
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop3 e id ["+idMsgSilGop3[0]+"]...");
		this.utilityGetAndTestMsgInBOX(imSilGop3_axis14, imSilGop3_cxf, idMsgSilGop3[0], msgAxis,true,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRATION_MANAGER); // il msg ritorna sbustato
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop3 e id ["+idMsgSilGop3[0]+"] effettuato.");
		
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop3 e id ["+idMsgSilGop3[1]+"]...");
		this.utilityGetAndTestMsgInBOX(imSilGop3_axis14, imSilGop3_cxf, idMsgSilGop3[1], msgAxis2,true,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRATION_MANAGER); // il msg ritorna sbustato
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop3 e id ["+idMsgSilGop3[1]+"] effettuato.");
		
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop3 e id ["+idMsgSilGop1[2]+"] (Msg pubblicato solo per SilGop1)...");
		try{
			this.utilityGetAndTestMsgInBOX(imSilGop3_axis14, imSilGop3_cxf, idMsgSilGop1[2], msgAxis3,false,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRATION_MANAGER);
			throw new Exception("6. Prelevo messaggio con identita SilGop3 del msg #3 non ha generato SPCoopException");
		}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
			Reporter.log("Prelevo messaggi in MessageBox con SilGop3 non riuscito, codice eccezione: "+e.getCodiceEccezione());
			Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO).equals(e.getCodiceEccezione()));
		}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
			Reporter.log("Prelevo messaggi in MessageBox con SilGop3 non riuscito, codice eccezione: "+e.getFaultInfo().getCodiceEccezione());
			Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO).equals(e.getFaultInfo().getCodiceEccezione()));
		}
		
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop2 e id ["+idMsgSilGop1[0]+"]...");
		try{
			this.utilityGetAndTestMsgInBOX(imSilGop2_axis14, imSilGop2_cxf, idMsgSilGop1[0], msgAxis,false,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_MESSAGE_BOX,CostantiTestSuite.SPCOOP_SERVIZIO_MESSAGE_BOX_AZIONE_INTEGRATION_MANAGER);
			throw new Exception("6. Prelevo messaggio con identita SilGop2 non ha generato SPCoopException");
		}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
			Reporter.log("Prelevo messaggi in MessageBox con SilGop2 non riuscito, codice eccezione: "+e.getCodiceEccezione());
			Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO).equals(e.getCodiceEccezione()));
		}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
			Reporter.log("Prelevo messaggi in MessageBox con SilGop2 non riuscito, codice eccezione: "+e.getFaultInfo().getCodiceEccezione());
			Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO).equals(e.getFaultInfo().getCodiceEccezione()));
		}
		
		Reporter.log("9. Prelevamento messaggi in MessageBox terminato");
		
		
		
		
		
		
		
		
		Reporter.log("10. Prelevamento id messaggi in MessageBox con Filtri...");
		
		// Servizio ComunicazioneVariazione Azione GetMessage  SILGOP1
		String [] idMsgSilGop1_serviceComunicazioneVariazioneGetMessage = null;
		if(use_axis14_engine){
			try{
				idMsgSilGop1_serviceComunicazioneVariazioneGetMessage = imSilGop1_axis14.getAllMessagesIdByService(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
						CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
						CostantiTestSuite.SPCOOP_SERVIZIO_MESSAGE_BOX_AZIONE_INTEGRATION_MANAGER);
				if(idMsgSilGop1_serviceComunicazioneVariazioneGetMessage==null || idMsgSilGop1_serviceComunicazioneVariazioneGetMessage.length<=0){
					throw new Exception("Messaggi non presenti per SilGop1");
				}
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop1 ["+e.getCodiceEccezione()+"]: "+e.getDescrizioneEccezione());
			}
		}
		else if(use_cxf_engine){
			try{
				idMsgSilGop1_serviceComunicazioneVariazioneGetMessage = toArray(imSilGop1_cxf.getAllMessagesIdByService(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
						CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
						CostantiTestSuite.SPCOOP_SERVIZIO_MESSAGE_BOX_AZIONE_INTEGRATION_MANAGER));
				if(idMsgSilGop1_serviceComunicazioneVariazioneGetMessage==null || idMsgSilGop1_serviceComunicazioneVariazioneGetMessage.length<=0){
					throw new Exception("Messaggi non presenti per SilGop1");
				}
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop1 ["+e.getFaultInfo().getCodiceEccezione()+"]: "+e.getFaultInfo().getDescrizioneEccezione());
			}
		}
		Reporter.log("Numero messaggi presenti per SilGop1:"+idMsgSilGop1_serviceComunicazioneVariazioneGetMessage.length);
		Assert.assertTrue(idMsgSilGop1_serviceComunicazioneVariazioneGetMessage.length==2);
		Reporter.log("ID eGov ritornato["+idMsgSilGop1_serviceComunicazioneVariazioneGetMessage[0]+"] idAtteso["+idEGov+"]");
		Assert.assertTrue(idEGov.equals(idMsgSilGop1_serviceComunicazioneVariazioneGetMessage[0]));
		Reporter.log("ID eGov ritornato["+idMsgSilGop1_serviceComunicazioneVariazioneGetMessage[1]+"] idAtteso["+idEGov2+"]");
		Assert.assertTrue(idEGov2.equals(idMsgSilGop1_serviceComunicazioneVariazioneGetMessage[1]));
		
		// Servizio MessageBox Azione GetMessage  SILGOP1
		String [] idMsgSilGop1_serviceMessageBoxGetMessage = null;
		if(use_axis14_engine){
			try{
				idMsgSilGop1_serviceMessageBoxGetMessage = imSilGop1_axis14.getAllMessagesIdByService(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_MESSAGE_BOX, 
						CostantiTestSuite.SPCOOP_NOME_SERVIZIO_MESSAGE_BOX, 
						CostantiTestSuite.SPCOOP_SERVIZIO_MESSAGE_BOX_AZIONE_INTEGRATION_MANAGER);
				if(idMsgSilGop1_serviceMessageBoxGetMessage==null || idMsgSilGop1_serviceMessageBoxGetMessage.length<=0){
					throw new Exception("Messaggi non presenti per SilGop1");
				}
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop1 ["+e.getCodiceEccezione()+"]: "+e.getDescrizioneEccezione());
			}
		}
		else if(use_cxf_engine){
			try{
				idMsgSilGop1_serviceMessageBoxGetMessage = toArray(imSilGop1_cxf.getAllMessagesIdByService(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_MESSAGE_BOX, 
						CostantiTestSuite.SPCOOP_NOME_SERVIZIO_MESSAGE_BOX, 
						CostantiTestSuite.SPCOOP_SERVIZIO_MESSAGE_BOX_AZIONE_INTEGRATION_MANAGER));
				if(idMsgSilGop1_serviceMessageBoxGetMessage==null || idMsgSilGop1_serviceMessageBoxGetMessage.length<=0){
					throw new Exception("Messaggi non presenti per SilGop1");
				}
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop1 ["+e.getFaultInfo().getCodiceEccezione()+"]: "+e.getFaultInfo().getDescrizioneEccezione());
			}
		}
		Reporter.log("Numero messaggi presenti per SilGop1:"+idMsgSilGop1_serviceMessageBoxGetMessage.length);
		Assert.assertTrue(idMsgSilGop1_serviceMessageBoxGetMessage.length==1);
		Reporter.log("ID eGov ritornato["+idMsgSilGop1_serviceMessageBoxGetMessage[0]+"] idAtteso["+idEGov3+"]");
		Assert.assertTrue(idEGov3.equals(idMsgSilGop1_serviceMessageBoxGetMessage[0]));
		
		// Servizio MessageBox Azione GetMessage    SILGOP1
		String [] idMsgSilGop1_serviceMessageBoxGetMessage2 = null;
		if(use_axis14_engine){
			try{
				idMsgSilGop1_serviceMessageBoxGetMessage2 = imSilGop1_axis14.getAllMessagesIdByService(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_MESSAGE_BOX, 
						CostantiTestSuite.SPCOOP_NOME_SERVIZIO_MESSAGE_BOX, 
						CostantiTestSuite.SPCOOP_SERVIZIO_MESSAGE_BOX_AZIONE2_INTEGRATION_MANAGER);
				if(idMsgSilGop1_serviceMessageBoxGetMessage2==null || idMsgSilGop1_serviceMessageBoxGetMessage2.length<=0){
					throw new Exception("Messaggi non presenti per SilGop1");
				}
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop1 ["+e.getCodiceEccezione()+"]: "+e.getDescrizioneEccezione());
			}
		}
		else if(use_cxf_engine){
			try{
				idMsgSilGop1_serviceMessageBoxGetMessage2 = toArray(imSilGop1_cxf.getAllMessagesIdByService(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_MESSAGE_BOX, 
						CostantiTestSuite.SPCOOP_NOME_SERVIZIO_MESSAGE_BOX, 
						CostantiTestSuite.SPCOOP_SERVIZIO_MESSAGE_BOX_AZIONE2_INTEGRATION_MANAGER));
				if(idMsgSilGop1_serviceMessageBoxGetMessage2==null || idMsgSilGop1_serviceMessageBoxGetMessage2.length<=0){
					throw new Exception("Messaggi non presenti per SilGop1");
				}
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop1 ["+e.getFaultInfo().getCodiceEccezione()+"]: "+e.getFaultInfo().getDescrizioneEccezione());
			}
		}
		Reporter.log("Numero messaggi presenti per SilGop1:"+idMsgSilGop1_serviceMessageBoxGetMessage2.length);
		Assert.assertTrue(idMsgSilGop1_serviceMessageBoxGetMessage2.length==1);
		Reporter.log("ID eGov ritornato["+idMsgSilGop1_serviceMessageBoxGetMessage2[0]+"] idAtteso["+idEGov4+"]");
		Assert.assertTrue(idEGov4.equals(idMsgSilGop1_serviceMessageBoxGetMessage2[0]));

		// Servizio ComunicazioneVariazione Azione GetMessage  SILGOP3
		String [] idMsgSilGop3_serviceComunicazioneVariazioneGetMessage = null;
		if(use_axis14_engine){
			try{
				idMsgSilGop3_serviceComunicazioneVariazioneGetMessage = imSilGop3_axis14.getAllMessagesIdByService(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
						CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
						CostantiTestSuite.SPCOOP_SERVIZIO_MESSAGE_BOX_AZIONE_INTEGRATION_MANAGER);
				if(idMsgSilGop3_serviceComunicazioneVariazioneGetMessage==null || idMsgSilGop3_serviceComunicazioneVariazioneGetMessage.length<=0){
					throw new Exception("Messaggi non presenti per SilGop1");
				}
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop1 ["+e.getCodiceEccezione()+"]: "+e.getDescrizioneEccezione());
			}
		}
		else if(use_cxf_engine){
			try{
				idMsgSilGop3_serviceComunicazioneVariazioneGetMessage = toArray(imSilGop3_cxf.getAllMessagesIdByService(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
						CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
						CostantiTestSuite.SPCOOP_SERVIZIO_MESSAGE_BOX_AZIONE_INTEGRATION_MANAGER));
				if(idMsgSilGop3_serviceComunicazioneVariazioneGetMessage==null || idMsgSilGop3_serviceComunicazioneVariazioneGetMessage.length<=0){
					throw new Exception("Messaggi non presenti per SilGop1");
				}
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop1 ["+e.getFaultInfo().getCodiceEccezione()+"]: "+e.getFaultInfo().getDescrizioneEccezione());
			}
		}
		Reporter.log("Numero messaggi presenti per SilGop1:"+idMsgSilGop3_serviceComunicazioneVariazioneGetMessage.length);
		Assert.assertTrue(idMsgSilGop3_serviceComunicazioneVariazioneGetMessage.length==2);
		Reporter.log("ID eGov ritornato["+idMsgSilGop3_serviceComunicazioneVariazioneGetMessage[0]+"] idAtteso["+idEGov+"]");
		Assert.assertTrue(idEGov.equals(idMsgSilGop3_serviceComunicazioneVariazioneGetMessage[0]));
		Reporter.log("ID eGov ritornato["+idMsgSilGop3_serviceComunicazioneVariazioneGetMessage[1]+"] idAtteso["+idEGov2+"]");
		Assert.assertTrue(idEGov2.equals(idMsgSilGop3_serviceComunicazioneVariazioneGetMessage[1]));
		
		// Servizio MessageBox Azione GetMessage    SILGOP3 : non presente
		if(use_axis14_engine){
			try{
				imSilGop3_axis14.getAllMessagesIdByService(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_MESSAGE_BOX, 
						CostantiTestSuite.SPCOOP_NOME_SERVIZIO_MESSAGE_BOX, 
						CostantiTestSuite.SPCOOP_SERVIZIO_MESSAGE_BOX_AZIONE_INTEGRATION_MANAGER);
				throw new Exception("Eccezione non sollevata per getAllMessageIdByServices per SilGop3");
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				Reporter.log("Prelevo messaggi in MessageBox con SilGop3 non riuscito, codice eccezione: "+e.getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getCodiceEccezione()));
			}
		}
		else if(use_cxf_engine){
			try{
				imSilGop3_cxf.getAllMessagesIdByService(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_MESSAGE_BOX, 
						CostantiTestSuite.SPCOOP_NOME_SERVIZIO_MESSAGE_BOX, 
						CostantiTestSuite.SPCOOP_SERVIZIO_MESSAGE_BOX_AZIONE_INTEGRATION_MANAGER);
				throw new Exception("Eccezione non sollevata per getAllMessageIdByServices per SilGop3");
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				Reporter.log("Prelevo messaggi in MessageBox con SilGop3 non riuscito, codice eccezione: "+e.getFaultInfo().getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getFaultInfo().getCodiceEccezione()));
			}
		}
		Reporter.log("10. Prelevamento id messaggi in MessageBox con Filtri terminato");
		
		
		
		
		
		
		
		Reporter.log("11. Prelevamento messaggi in MessageBox ottenuti grazie ai filtri in test N.9...");
		
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop1 e id ["+idMsgSilGop1[0]+"]...");
		this.utilityGetAndTestMsgInBOX(imSilGop1_axis14, imSilGop1_cxf, idMsgSilGop1_serviceComunicazioneVariazioneGetMessage[0], msgAxis,false,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRATION_MANAGER);
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop1 e id ["+idMsgSilGop1[0]+"] effettuato.");
		
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop1 e id ["+idMsgSilGop1[1]+"]...");
		this.utilityGetAndTestMsgInBOX(imSilGop1_axis14, imSilGop1_cxf, idMsgSilGop1_serviceComunicazioneVariazioneGetMessage[1], msgAxis2,false,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRATION_MANAGER);
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop1 e id ["+idMsgSilGop1[1]+"] effettuato.");
		
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop1 e id ["+idMsgSilGop1_serviceMessageBoxGetMessage[0]+"]...");
		this.utilityGetAndTestMsgInBOX(imSilGop1_axis14, imSilGop1_cxf, idMsgSilGop1_serviceMessageBoxGetMessage[0], msgAxis3,false,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_MESSAGE_BOX,CostantiTestSuite.SPCOOP_SERVIZIO_MESSAGE_BOX_AZIONE_INTEGRATION_MANAGER);
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop1 e id ["+idMsgSilGop1_serviceMessageBoxGetMessage[0]+"] effettuato.");
		
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop1 e id ["+idMsgSilGop1_serviceMessageBoxGetMessage2[0]+"]...");
		this.utilityGetAndTestMsgInBOX(imSilGop1_axis14, imSilGop1_cxf, idMsgSilGop1_serviceMessageBoxGetMessage2[0], msgAxis4,false,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_MESSAGE_BOX,CostantiTestSuite.SPCOOP_SERVIZIO_MESSAGE_BOX_AZIONE2_INTEGRATION_MANAGER);
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop1 e id ["+idMsgSilGop1_serviceMessageBoxGetMessage2[0]+"] effettuato.");
		
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop3 e id ["+idMsgSilGop3_serviceComunicazioneVariazioneGetMessage[0]+"]...");
		this.utilityGetAndTestMsgInBOX(imSilGop3_axis14, imSilGop3_cxf, idMsgSilGop3_serviceComunicazioneVariazioneGetMessage[0], msgAxis,true,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRATION_MANAGER); // il msg ritorna sbustato
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop3 e id ["+idMsgSilGop3_serviceComunicazioneVariazioneGetMessage[0]+"] effettuato.");
		
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop3 e id ["+idMsgSilGop3_serviceComunicazioneVariazioneGetMessage[1]+"]...");
		this.utilityGetAndTestMsgInBOX(imSilGop3_axis14, imSilGop3_cxf, idMsgSilGop3_serviceComunicazioneVariazioneGetMessage[1], msgAxis2,true,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRATION_MANAGER); // il msg ritorna sbustato
		Reporter.log("Prelevamento messaggo in MessageBox, con identita SilGop3 e id ["+idMsgSilGop3_serviceComunicazioneVariazioneGetMessage[1]+"] effettuato.");
		
		Reporter.log("11. Prelevamento messaggi in MessageBox ottenuti grazie ai filtri in test N.9 effettuato");
		
		
		
		
		
		
		
		
		
		
		Reporter.log("12. Prelevo id messaggi in MessageBox con getNext, con identita SilGop1...");
		String [] idMsgSilGop1NEXT2 = null;
		if(use_axis14_engine){
			try{
				idMsgSilGop1NEXT2 = imSilGop1_axis14.getNextMessagesId(2);
				if(idMsgSilGop1NEXT2==null || idMsgSilGop1NEXT2.length<=0){
					throw new Exception("Messaggi non presenti per SilGop1");
				}
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop1 ["+e.getCodiceEccezione()+"]: "+e.getDescrizioneEccezione());
			}
		}
		else if(use_cxf_engine){
			try{
				idMsgSilGop1NEXT2 = toArray(imSilGop1_cxf.getNextMessagesId(2));
				if(idMsgSilGop1NEXT2==null || idMsgSilGop1NEXT2.length<=0){
					throw new Exception("Messaggi non presenti per SilGop1");
				}
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop1 ["+e.getFaultInfo().getCodiceEccezione()+"]: "+e.getFaultInfo().getDescrizioneEccezione());
			}
		}
		Reporter.log("Numero messaggi presenti per SilGop1:"+idMsgSilGop1NEXT2.length);
		Assert.assertTrue(idMsgSilGop1NEXT2.length==2);
		Reporter.log("ID eGov ritornato["+idMsgSilGop1NEXT2[0]+"] idAtteso["+idEGov+"]");
		Assert.assertTrue(idEGov.equals(idMsgSilGop1NEXT2[0]));
		Reporter.log("ID eGov ritornato["+idMsgSilGop1NEXT2[1]+"] idAtteso["+idEGov2+"]");
		Assert.assertTrue(idEGov2.equals(idMsgSilGop1NEXT2[1]));

		String [] idMsgSilGop1NEXT2ByFiltroServizio = null;
		if(use_axis14_engine){
			try{
				idMsgSilGop1NEXT2ByFiltroServizio = imSilGop1_axis14.getNextMessagesIdByService(2, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
						CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
						CostantiTestSuite.SPCOOP_SERVIZIO_MESSAGE_BOX_AZIONE_INTEGRATION_MANAGER);
				if(idMsgSilGop1NEXT2ByFiltroServizio==null || idMsgSilGop1NEXT2ByFiltroServizio.length<=0){
					throw new Exception("Messaggi non presenti per SilGop1");
				}
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop1 ["+e.getCodiceEccezione()+"]: "+e.getDescrizioneEccezione());
			}
		}
		else if(use_cxf_engine){
			try{
				idMsgSilGop1NEXT2ByFiltroServizio = toArray(imSilGop1_cxf.getNextMessagesIdByService(2, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, 
						CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 
						CostantiTestSuite.SPCOOP_SERVIZIO_MESSAGE_BOX_AZIONE_INTEGRATION_MANAGER));
				if(idMsgSilGop1NEXT2ByFiltroServizio==null || idMsgSilGop1NEXT2ByFiltroServizio.length<=0){
					throw new Exception("Messaggi non presenti per SilGop1");
				}
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop1 ["+e.getFaultInfo().getCodiceEccezione()+"]: "+e.getFaultInfo().getDescrizioneEccezione());
			}
		}
		Reporter.log("Numero messaggi presenti per SilGop1:"+idMsgSilGop1NEXT2ByFiltroServizio.length);
		Assert.assertTrue(idMsgSilGop1NEXT2ByFiltroServizio.length==2);
		Reporter.log("ID eGov ritornato["+idMsgSilGop1NEXT2ByFiltroServizio[0]+"] idAtteso["+idEGov+"]");
		Assert.assertTrue(idEGov.equals(idMsgSilGop1NEXT2ByFiltroServizio[0]));
		Reporter.log("ID eGov ritornato["+idMsgSilGop1NEXT2ByFiltroServizio[1]+"] idAtteso["+idEGov2+"]");
		Assert.assertTrue(idEGov2.equals(idMsgSilGop1NEXT2ByFiltroServizio[1]));
		
		String [] idMsgSilGop1NEXT2ByFiltroServizioMessageBox = null;
		if(use_axis14_engine){
			try{
				idMsgSilGop1NEXT2ByFiltroServizioMessageBox = imSilGop1_axis14.getNextMessagesIdByService(2, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_MESSAGE_BOX, 
						CostantiTestSuite.SPCOOP_NOME_SERVIZIO_MESSAGE_BOX, 
						null);
				if(idMsgSilGop1NEXT2ByFiltroServizioMessageBox==null || idMsgSilGop1NEXT2ByFiltroServizioMessageBox.length<=0){
					throw new Exception("Messaggi non presenti per SilGop1");
				}
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop1 ["+e.getCodiceEccezione()+"]: "+e.getDescrizioneEccezione());
			}
		}
		else if(use_cxf_engine){
			try{
				idMsgSilGop1NEXT2ByFiltroServizioMessageBox = toArray(imSilGop1_cxf.getNextMessagesIdByService(2, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_MESSAGE_BOX, 
						CostantiTestSuite.SPCOOP_NOME_SERVIZIO_MESSAGE_BOX, 
						null));
				if(idMsgSilGop1NEXT2ByFiltroServizioMessageBox==null || idMsgSilGop1NEXT2ByFiltroServizioMessageBox.length<=0){
					throw new Exception("Messaggi non presenti per SilGop1");
				}
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop1 ["+e.getFaultInfo().getCodiceEccezione()+"]: "+e.getFaultInfo().getDescrizioneEccezione());
			}
		}
		Reporter.log("Numero messaggi presenti per SilGop1:"+idMsgSilGop1NEXT2ByFiltroServizioMessageBox.length);
		Assert.assertTrue(idMsgSilGop1NEXT2ByFiltroServizioMessageBox.length==2);
		Reporter.log("ID eGov ritornato["+idMsgSilGop1NEXT2ByFiltroServizioMessageBox[0]+"] idAtteso["+idEGov3+"]");
		Assert.assertTrue(idEGov3.equals(idMsgSilGop1NEXT2ByFiltroServizioMessageBox[0]));
		Reporter.log("ID eGov ritornato["+idMsgSilGop1NEXT2ByFiltroServizioMessageBox[1]+"] idAtteso["+idEGov4+"]");
		Assert.assertTrue(idEGov4.equals(idMsgSilGop1NEXT2ByFiltroServizioMessageBox[1]));
		
		Reporter.log("12. Prelevo id messaggi in MessageBox con getNext, con identita SilGop1 effettuato");
		
		
		
		
		
		
		
		
		Reporter.log("13. Delete idegov ["+idEGov+"] per identita SilGop1...");
		if(use_axis14_engine){
			try{
				imSilGop1_axis14.deleteMessage(idEGov);
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				throw new Exception("Delete idegov ["+idEGov+"] non riuscito per SilGop1 ["+e.getCodiceEccezione()+"]: "+e.getDescrizioneEccezione());
			}
		}
		else if(use_cxf_engine){
			try{
				imSilGop1_cxf.deleteMessage(idEGov);
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				throw new Exception("Delete idegov ["+idEGov+"] non riuscito per SilGop1 ["+e.getFaultInfo().getCodiceEccezione()+"]: "+e.getFaultInfo().getDescrizioneEccezione());
			}
		}
		
		// Check eliminazione SOLO per SilGop1
		if(use_axis14_engine){
			try{
				idMsgSilGop1 = imSilGop1_axis14.getAllMessagesId();
				if(idMsgSilGop1==null || idMsgSilGop1.length<=0){
					throw new Exception("Messaggi non presenti per SilGop1");
				}
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop1 ["+e.getCodiceEccezione()+"]: "+e.getDescrizioneEccezione());
			}
		}
		else if(use_cxf_engine){
			try{
				idMsgSilGop1 = toArray(imSilGop1_cxf.getAllMessagesId());
				if(idMsgSilGop1==null || idMsgSilGop1.length<=0){
					throw new Exception("Messaggi non presenti per SilGop1");
				}
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop1 ["+e.getFaultInfo().getCodiceEccezione()+"]: "+e.getFaultInfo().getDescrizioneEccezione());
			}
		}
		Reporter.log("Numero messaggi presenti per SilGop1:"+idMsgSilGop1.length);
		Assert.assertTrue(idMsgSilGop1.length==3);
		Reporter.log("ID eGov ritornato["+idMsgSilGop1[0]+"] idAtteso["+idEGov2+"]");
		Assert.assertTrue(idEGov2.equals(idMsgSilGop1[0]));
		Reporter.log("ID eGov ritornato["+idMsgSilGop1[1]+"] idAtteso["+idEGov3+"]");
		Assert.assertTrue(idEGov3.equals(idMsgSilGop1[1]));
		Reporter.log("ID eGov ritornato["+idMsgSilGop1[2]+"] idAtteso["+idEGov4+"]");
		Assert.assertTrue(idEGov4.equals(idMsgSilGop1[2]));
		
		Reporter.log("8. Prelevo id messaggi in MessageBox, con identita SilGop3...");
		if(use_axis14_engine){
			try{
				idMsgSilGop3 = imSilGop3_axis14.getAllMessagesId();
				if(idMsgSilGop3==null || idMsgSilGop3.length<=0){
					throw new Exception("Messaggi non presenti per SilGop3");
				}
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop3 ["+e.getCodiceEccezione()+"]: "+e.getDescrizioneEccezione());
			}
		}
		else if(use_cxf_engine){
			try{
				idMsgSilGop3 = toArray(imSilGop3_cxf.getAllMessagesId());
				if(idMsgSilGop3==null || idMsgSilGop3.length<=0){
					throw new Exception("Messaggi non presenti per SilGop3");
				}
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop3 ["+e.getFaultInfo().getCodiceEccezione()+"]: "+e.getFaultInfo().getDescrizioneEccezione());
			}
		}
		Reporter.log("Numero messaggi presenti per SilGop3:"+idMsgSilGop3.length);
		Assert.assertTrue(idMsgSilGop3.length==2);
		Reporter.log("ID eGov ritornato["+idMsgSilGop3[0]+"] idAtteso["+idEGov+"]");
		Assert.assertTrue(idEGov.equals(idMsgSilGop3[0]));
		Reporter.log("ID eGov ritornato["+idMsgSilGop3[1]+"] idAtteso["+idEGov2+"]");
		Assert.assertTrue(idEGov2.equals(idMsgSilGop3[1]));
		
		// Eliminazione messaggio non presente
		if(use_axis14_engine){
			try{
				imSilGop1_axis14.deleteMessage(idEGov);
				throw new Exception("Delete idegov ["+idEGov+"] non riuscito per SilGop1, eccezione non sollevata");
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				Reporter.log("Delete idegov ["+idEGov+"] in MessageBox con SilGop1 non riuscito, codice eccezione: "+e.getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO).equals(e.getCodiceEccezione()));
			}
		}
		else if(use_cxf_engine){
			try{
				imSilGop1_cxf.deleteMessage(idEGov);
				throw new Exception("Delete idegov ["+idEGov+"] non riuscito per SilGop1, eccezione non sollevata");
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				Reporter.log("Delete idegov ["+idEGov+"] in MessageBox con SilGop1 non riuscito, codice eccezione: "+e.getFaultInfo().getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_407_INTEGRATION_MANAGER_MSG_RICHIESTO_NON_TROVATO).equals(e.getFaultInfo().getCodiceEccezione()));
			}
		}
		
		Reporter.log("13. Delete idegov ["+idEGov+"] per identita SilGop1 effettuato");
		
		
		
		
		
		
		
		Reporter.log("14. Delete all message per SilGop3...");
		
		if(use_axis14_engine){
			try{
				imSilGop3_axis14.deleteAllMessages();
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				throw new Exception("Delete all message non riuscito per SilGop3 ["+e.getCodiceEccezione()+"]: "+e.getDescrizioneEccezione());
			}
		}
		else if(use_cxf_engine){
			try{
				imSilGop3_cxf.deleteAllMessages();
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				throw new Exception("Delete all message non riuscito per SilGop3 ["+e.getFaultInfo().getCodiceEccezione()+"]: "+e.getFaultInfo().getDescrizioneEccezione());
			}
		}
		
		// Check eliminazione SOLO per SilGop3
		if(use_axis14_engine){
			try{
				idMsgSilGop1 = imSilGop1_axis14.getAllMessagesId();
				if(idMsgSilGop1==null || idMsgSilGop1.length<=0){
					throw new Exception("Messaggi non presenti per SilGop1");
				}
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop1 ["+e.getCodiceEccezione()+"]: "+e.getDescrizioneEccezione());
			}
		}
		else if(use_cxf_engine){
			try{
				idMsgSilGop1 = toArray(imSilGop1_cxf.getAllMessagesId());
				if(idMsgSilGop1==null || idMsgSilGop1.length<=0){
					throw new Exception("Messaggi non presenti per SilGop1");
				}
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				throw new Exception("Prelievo messaggi non riuscito per SilGop1 ["+e.getFaultInfo().getCodiceEccezione()+"]: "+e.getFaultInfo().getDescrizioneEccezione());
			}
		}
		Reporter.log("Numero messaggi presenti per SilGop1:"+idMsgSilGop1.length);
		Assert.assertTrue(idMsgSilGop1.length==3);
		Reporter.log("ID eGov ritornato["+idMsgSilGop1[0]+"] idAtteso["+idEGov2+"]");
		Assert.assertTrue(idEGov2.equals(idMsgSilGop1[0]));
		Reporter.log("ID eGov ritornato["+idMsgSilGop1[1]+"] idAtteso["+idEGov3+"]");
		Assert.assertTrue(idEGov3.equals(idMsgSilGop1[1]));
		Reporter.log("ID eGov ritornato["+idMsgSilGop1[2]+"] idAtteso["+idEGov4+"]");
		Assert.assertTrue(idEGov4.equals(idMsgSilGop1[2]));
		
		Reporter.log("Prelevo id messaggi in MessageBox, con identita SilGop3...");
		if(use_axis14_engine){
			try{
				idMsgSilGop3 = imSilGop3_axis14.getAllMessagesId();
				throw new Exception("Eccezione Messaggi non presenti per SilGop3 non lanciata");
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				Reporter.log(" Prelevo id messaggi all message in MessageBox con SilGop3 non riuscito, codice eccezione: "+e.getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getCodiceEccezione()));
			}
		}
		else if(use_cxf_engine){
			try{
				idMsgSilGop3 = toArray(imSilGop3_cxf.getAllMessagesId());
				throw new Exception("Eccezione Messaggi non presenti per SilGop3 non lanciata");
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				Reporter.log(" Prelevo id messaggi all message in MessageBox con SilGop3 non riuscito, codice eccezione: "+e.getFaultInfo().getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getFaultInfo().getCodiceEccezione()));
			}
		}
		
		if(use_axis14_engine){
			try{
				imSilGop1_axis14.deleteAllMessages();
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				throw new Exception("Delete all message non riuscito per SilGop1 ["+e.getCodiceEccezione()+"]: "+e.getDescrizioneEccezione());
			}
		}
		else if(use_cxf_engine){
			try{
				imSilGop1_cxf.deleteAllMessages();
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				throw new Exception("Delete all message non riuscito per SilGop1 ["+e.getFaultInfo().getCodiceEccezione()+"]: "+e.getFaultInfo().getDescrizioneEccezione());
			}
		}
		
		Reporter.log("Prelevo id messaggi in MessageBox, con identita SilGop1...");
		if(use_axis14_engine){
			try{
				idMsgSilGop1 = imSilGop1_axis14.getAllMessagesId();
				throw new Exception("Eccezione Messaggi non presenti per SilGop1 non lanciata");
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				Reporter.log(" Prelevo id messaggi all message in MessageBox con SilGop1 non riuscito, codice eccezione: "+e.getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getCodiceEccezione()));
			}
		}
		else if(use_cxf_engine){
			try{
				idMsgSilGop1 = toArray(imSilGop1_cxf.getAllMessagesId());
				throw new Exception("Eccezione Messaggi non presenti per SilGop1 non lanciata");
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				Reporter.log(" Prelevo id messaggi all message in MessageBox con SilGop1 non riuscito, codice eccezione: "+e.getFaultInfo().getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getFaultInfo().getCodiceEccezione()));
			}
		}
		
		Reporter.log("14. Delete all message per SilGop3/SilGop1 effettuato");
		
	}
	
	
	
	Repository repositorySincronoIM=new Repository();
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".MESSAGE_BOX"},dependsOnMethods={"message_box"})
	public void message_box_invocazione_per_riferimento() throws Exception{
		
		
		// IntegrationManager
		org.openspcoop2.pdd.services.axis14.MessageBox_PortType imSilGop1_axis14 = null;
		org.openspcoop2.pdd.services.axis14.PD_PortType imSilGop1_invokePD_axis14 = null;
		if(use_axis14_engine){
			imSilGop1_axis14 = getIntegrationManagerMessageBox_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/MessageBox"), 
					"gop1","123456");
			imSilGop1_invokePD_axis14 = 
					getIntegrationManagerPD_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/PD"), 
						"gop1","123456");
		}
		org.openspcoop2.pdd.services.cxf.MessageBox imSilGop1_cxf = null;
		org.openspcoop2.pdd.services.cxf.PD imSilGop1_invokePD_cxf = null;
		if(use_cxf_engine){
			imSilGop1_cxf = getIntegrationManagerMessageBox_cxf(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/MessageBox"), 
					"gop1","123456");
			imSilGop1_invokePD_cxf = 
					getIntegrationManagerPD_cxf(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/PD"), 
						"gop1","123456");
		}

		
		// Costruzione msg di richiesta
		SOAPEngine utility = new SOAPEngine(null);
		utility.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,addIDUnivoco);
		Message msgAxis = utility.getRequestMessage();
		
		// Pulizia repository per test
		if(use_axis14_engine){
			try{
				imSilGop1_axis14.deleteAllMessages();
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				Reporter.log("Pulizia repository SilGop1 MessageBox non riuscito, codice eccezione: "+e.getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getCodiceEccezione()));
			}
		}
		else if(use_cxf_engine){
			try{
				imSilGop1_cxf.deleteAllMessages();
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				Reporter.log("Pulizia repository SilGop1 MessageBox non riuscito, codice eccezione: "+e.getFaultInfo().getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getFaultInfo().getCodiceEccezione()));
			}
		}
		
		Reporter.log("1. Pubblicazione messaggio in MessageBox Azione1 (Sottoscrive solo SilGop1) (INVOCAZIONE DINAMICA!)...");
		String idEGov = gestioneProfiloOneway(CostantiTestSuite.PORTA_DELEGATA_MESSAGE_BOX_GET_MESSAGE1, null, null, null,msgAxis,false,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_MESSAGE_BOX,CostantiTestSuite.SPCOOP_NOME_SERVIZIO_MESSAGE_BOX,
				CostantiTestSuite.SPCOOP_SERVIZIO_MESSAGE_BOX_AZIONE_INTEGRATION_MANAGER);
		try{
			Thread.sleep(10000);
		}catch(Exception e){}
		Reporter.log("1. Pubblicazione messaggio in MessageBox Azione1 (Sottoscrive solo SilGop1) (INVOCAZIONE DINAMICA!) eseguito per id:"+idEGov);
		
		
		
		Reporter.log("2. Invio per Riferimento");
		
		// SPCoopMessage
		org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage msg = new org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage();
				
		// IntegrationManager
		org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage msgRisposta = null;
		if(use_axis14_engine){
			msgRisposta = imSilGop1_invokePD_axis14.invocaPortaDelegataPerRiferimento(CostantiTestSuite.PORTA_DELEGATA_MESSAGE_BOX_INVOCAZIONE_PER_RIFERIMENTO, msg , idEGov); 
		}
		else if(use_cxf_engine){
			org.openspcoop2.pdd.services.cxf.IntegrationManagerMessage msgRisposta_cxf =
					imSilGop1_invokePD_cxf.invocaPortaDelegataPerRiferimento(CostantiTestSuite.PORTA_DELEGATA_MESSAGE_BOX_INVOCAZIONE_PER_RIFERIMENTO, toIntegrationManagerMessage_cxf(msg) , idEGov);
			msgRisposta = toIntegrationManagerMessage_axis14(msgRisposta_cxf);
		}
		
		// Msg di risposta
		Message msgAxisResponse = Axis14SoapUtils.build(msgRisposta.getMessage(), false);
		
		// Test uguaglianza Body
		Reporter.log("Controllo risposta ottenuta");
		Assert.assertTrue(ClientCore.isEqualsSentAndResponseMessage(msgAxis,msgAxisResponse));
		
		// Controlla che sia uguale a quello ritornato nell'header della risposta
	    if(msgRisposta.getProtocolHeaderInfo().getID()==null)
	    	throw new TestSuiteException("ID e-Gov non presenta nell'header del trasporto della ricevuta.");
	    
		// Identificativo della richiesta
	    String identificativoRichiestaAsincrona=msgRisposta.getProtocolHeaderInfo().getID();
	    this.repositorySincronoIM.add(identificativoRichiestaAsincrona);
	    
	    Reporter.log("[Sincrono] Gestione richiesta con id="+identificativoRichiestaAsincrona+" effettata.");
	    
	    // Attesa terminazione messaggio
	    org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties testsuiteProperties = 
	    	org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance();
	    org.openspcoop2.testsuite.core.TestSuiteProperties testsuitePropertiesLIB = 
	    	org.openspcoop2.testsuite.core.TestSuiteProperties.getInstance();
	    if(testsuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
	    	
	    	DatabaseComponent dbFruitore = null;
	    	DatabaseComponent dbErogatore = null;
	    	try {
	    	
	    		/** DatabaseComponent */
	    		dbFruitore = DatabaseProperties.getDatabaseComponentFruitore();
	    		dbErogatore = DatabaseProperties.getDatabaseComponentErogatore();
	    		
				if(dbFruitore!=null){
					long countTimeout = System.currentTimeMillis() + (1000* testsuitePropertiesLIB.getTimeoutProcessamentoMessaggiOpenSPCoop());
					while(dbFruitore.getVerificatoreMessaggi().countMsgOpenSPCoop(identificativoRichiestaAsincrona)!=0 && countTimeout>System.currentTimeMillis()){
						try{
						Thread.sleep(testsuitePropertiesLIB.getCheckIntervalProcessamentoMessaggiOpenSPCoop());
						}catch(Exception e){}
					}
				}
				if(dbErogatore!=null){
					long countTimeout = System.currentTimeMillis() + (1000*testsuitePropertiesLIB.getTimeoutProcessamentoMessaggiOpenSPCoop());
					while(dbErogatore.getVerificatoreMessaggi().countMsgOpenSPCoop(identificativoRichiestaAsincrona)!=0 && countTimeout>System.currentTimeMillis()){
						try{
							Thread.sleep(testsuitePropertiesLIB.getCheckIntervalProcessamentoMessaggiOpenSPCoop());
						}catch(Exception e){}
					}
				}
	    	}catch(Exception e){
	    		Reporter.log("[Sincrono] Errore durante il controllo: "+e.getMessage());
	    	}finally{
	    		try{
	    			if(dbFruitore!=null){
	    				dbFruitore.close();
	    			}
	    		}catch(Exception e){}
	    		try{
	    			if(dbErogatore!=null){
	    				dbErogatore.close();
	    			}
	    		}catch(Exception e){}
	    	}
		}
		
		
	    Reporter.log("XXX. Delete all message per SilGop1 ....");
		
	    if(use_axis14_engine){
			try{
				imSilGop1_axis14.deleteAllMessages();
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				throw new Exception("Delete all message non riuscito per SilGop1 ["+e.getCodiceEccezione()+"]: "+e.getDescrizioneEccezione());
			}
	    }
	    else if(use_cxf_engine){
	    	try{
				imSilGop1_cxf.deleteAllMessages();
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				throw new Exception("Delete all message non riuscito per SilGop1 ["+e.getFaultInfo().getCodiceEccezione()+"]: "+e.getFaultInfo().getDescrizioneEccezione());
			}
	    }
		
		Reporter.log("Prelevo id messaggi in MessageBox, con identita SilGop1...");
		if(use_axis14_engine){
			try{
				imSilGop1_axis14.getAllMessagesId();
				throw new Exception("Eccezione Messaggi non presenti per SilGop1 non lanciata");
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				Reporter.log(" Prelevo id messaggi all message in MessageBox con SilGop1 non riuscito, codice eccezione: "+e.getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getCodiceEccezione()));
			}
		}
		else if(use_cxf_engine){
			try{
				imSilGop1_cxf.getAllMessagesId();
				throw new Exception("Eccezione Messaggi non presenti per SilGop1 non lanciata");
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				Reporter.log(" Prelevo id messaggi all message in MessageBox con SilGop1 non riuscito, codice eccezione: "+e.getFaultInfo().getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getFaultInfo().getCodiceEccezione()));
			}
		}
		
		Reporter.log("XXX. Delete all message per SilGop1 effettuato");
		
	}
	@DataProvider (name="SincronoIM")
	public Object[][]testSincronoIM()throws Exception{
		String id=this.repositorySincronoIM.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	private CooperazioneBaseInformazioni infoIM = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBaseIM = 
		new CooperazioneBase(false,SOAPVersion.SOAP11,  this.infoIM, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	@Test(groups={IntegrationManager.ID_GRUPPO,IntegrationManager.ID_GRUPPO+".MESSAGE_BOX"},dataProvider="SincronoIM",dependsOnMethods={"message_box_invocazione_per_riferimento"})
	public void testSincronoIM(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBaseIM.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,null, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ---------------- UTILITY ------------------------------ */
	
	private void gestioneProfiloAsincronoAsimmetrico(String PD,String PDCorrelata,boolean modalitaSincrona,String tipo,
			RepositoryCorrelazioneIstanzeAsincrone repositoryCorrelazioneAsincrona) throws Exception{
		//		 Costruzione msg di richiesta
		SOAPEngine utility = new SOAPEngine(null);
		utility.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,addIDUnivoco);

		// SPCoopMessage
		Message msgAxis = utility.getRequestMessage();
		org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage msg = new org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		msgAxis.writeTo(bout);
		bout.flush();
		bout.close();
		msg.setMessage(bout.toByteArray());
		
		// IntegrationManager
		org.openspcoop2.pdd.services.axis14.PD_PortType im_axis14 = null;
		if(use_axis14_engine){
			im_axis14 = getIntegrationManagerPD_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/PD"), 
					null,null);
		}
		org.openspcoop2.pdd.services.cxf.PD im_cxf = null;
		if(use_cxf_engine){
			im_cxf = getIntegrationManagerPD_cxf(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/PD"), 
					null,null);
		}
		
		org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage msgRisposta = null;
		if(use_axis14_engine){
			msgRisposta = im_axis14.invocaPortaDelegata(PD, msg);
		}
		else if(use_cxf_engine){
			org.openspcoop2.pdd.services.cxf.IntegrationManagerMessage msgRisposta_cxf = im_cxf.invocaPortaDelegata(PD, toIntegrationManagerMessage_cxf(msg));
			msgRisposta = toIntegrationManagerMessage_axis14(msgRisposta_cxf);
		}
		
		
		// Msg di risposta
		Message msgAxisResponse = Axis14SoapUtils.build(msgRisposta.getMessage(), false,false,false);
		
		// Test uguaglianza Body
		Reporter.log("Controllo risposta ottenuta");
		if(msgRisposta.getProtocolHeaderInfo().getID()==null)
			throw new TestSuiteException("ID e-Gov non presenta nell'header del trasporto della ricevuta.");
		   
		if(modalitaSincrona){
			Assert.assertTrue(ClientCore.isEqualsSentAndResponseMessage(msgAxis,msgAxisResponse));
		}else{
			if(msgAxisResponse.getSOAPBody()!=null && msgAxisResponse.getSOAPBody().hasFault()){
				String idBody=org.openspcoop2.testsuite.core.Utilities.getIDFromOpenSPCoopOKMessage(SPCoopTestsuiteLogger.getInstance(),msgAxisResponse);
				// Controlla che sia uguale a quello ritornato nell'header della risposta
				if(idBody==null)
					throw new TestSuiteException("ID e-Gov non presenta nella ricevuta OpenSPCoopOK.");
				if(msgRisposta.getProtocolHeaderInfo().getID().equals(idBody)==false)
					throw new TestSuiteException("ID e-Gov presente nell'header del trasporto della ricevuta differisce dall'id egov presente nel messaggio OpenSPCoopOK della ricevuta.");
			}
		}
		
		// Identificativo della richiesta
	    String identificativoRichiestaAsincrona=msgRisposta.getProtocolHeaderInfo().getID();
	    Reporter.log("["+tipo+"] Gestione richiesta con id="+identificativoRichiestaAsincrona+" effettata.");

	    
		// Attesa prima della generazione della risposta asincrona
	    org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties testsuiteProperties = 
	    	org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance();
	    org.openspcoop2.testsuite.core.TestSuiteProperties testsuitePropertiesLIB = 
	    	org.openspcoop2.testsuite.core.TestSuiteProperties.getInstance();
	    if(testsuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
	    	
	    	DatabaseComponent dbFruitore = null;
	    	DatabaseComponent dbErogatore = null;
	    	try {
	    	
	    		/** DatabaseComponent */
	    		dbFruitore = DatabaseProperties.getDatabaseComponentFruitore();
	    		dbErogatore = DatabaseProperties.getDatabaseComponentErogatore();
	    		
				if(dbFruitore!=null){
					long countTimeout = System.currentTimeMillis() + (1000* testsuitePropertiesLIB.getTimeoutProcessamentoMessaggiOpenSPCoop());
					while(dbFruitore.getVerificatoreMessaggi().countMsgOpenSPCoop(identificativoRichiestaAsincrona)!=0 && countTimeout>System.currentTimeMillis()){
						try{
						Thread.sleep(testsuitePropertiesLIB.getCheckIntervalProcessamentoMessaggiOpenSPCoop());
						}catch(Exception e){}
					}
				}
				if(dbErogatore!=null){
					long countTimeout = System.currentTimeMillis() + (1000*testsuitePropertiesLIB.getTimeoutProcessamentoMessaggiOpenSPCoop());
					while(dbErogatore.getVerificatoreMessaggi().countMsgOpenSPCoop(identificativoRichiestaAsincrona)!=0 && countTimeout>System.currentTimeMillis()){
						try{
							Thread.sleep(testsuitePropertiesLIB.getCheckIntervalProcessamentoMessaggiOpenSPCoop());
						}catch(Exception e){}
					}
				}
	    	}catch(Exception e){
	    		Reporter.log("["+tipo+"] Errore durante il controllo: "+e.getMessage());
	    	}finally{
	    		try{
	    			if(dbFruitore!=null){
	    				dbFruitore.close();
	    			}
	    		}catch(Exception e){}
	    		try{
	    			if(dbErogatore!=null){
	    				dbErogatore.close();
	    			}
	    		}catch(Exception e){}
	    	}
		}else{
			try{
				Thread.sleep(testsuiteProperties.getIntervalloGenerazioneRispostaAsincrona());
			}
			catch(InterruptedException i){}
		}	   	    

	    // Costruzione msg di risposta
		utility = new SOAPEngine(null);
		utility.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,addIDUnivoco);

		// SPCoopMessage
		Message msgAxisRichiestaStatoAsincrona = utility.getRequestMessage();
		org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage msgRichiestaStatoAsincrona = new org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage();
		ByteArrayOutputStream boutRichiestaStatoAsincrona = new ByteArrayOutputStream();
		msgAxisRichiestaStatoAsincrona.writeTo(boutRichiestaStatoAsincrona);
		boutRichiestaStatoAsincrona.flush();
		boutRichiestaStatoAsincrona.close();
		msgRichiestaStatoAsincrona.setMessage(boutRichiestaStatoAsincrona.toByteArray());
		
		// RiferimentoMessaggio
		org.openspcoop2.pdd.services.axis14.ProtocolHeaderInfo infoEGOV = new org.openspcoop2.pdd.services.axis14.ProtocolHeaderInfo();
		infoEGOV.setRiferimentoMessaggio(identificativoRichiestaAsincrona);
		msgRichiestaStatoAsincrona.setProtocolHeaderInfo(infoEGOV);
		
		// IntegrationManager
		org.openspcoop2.pdd.services.axis14.PD_PortType imRichiestaStatoAsincrona_axis14 = null;
		if(use_axis14_engine){
			imRichiestaStatoAsincrona_axis14 = getIntegrationManagerPD_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/PD"), 
					null,null);
		}
		org.openspcoop2.pdd.services.cxf.PD imRichiestaStatoAsincrona_cxf = null;
		if(use_cxf_engine){
			imRichiestaStatoAsincrona_cxf = getIntegrationManagerPD_cxf(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/PD"), 
					null,null);
		}
		
		org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage msgRispostaRichiestaStato = null;
		if(use_axis14_engine){
			if(modalitaSincrona){
				msgRispostaRichiestaStato = imRichiestaStatoAsincrona_axis14.sendRichiestaStatoAsincronaAsimmetrica(PDCorrelata, msgRichiestaStatoAsincrona);
			}else{
				msgRispostaRichiestaStato = imRichiestaStatoAsincrona_axis14.invocaPortaDelegata(PDCorrelata, msgRichiestaStatoAsincrona);
			}
		}
		else if(use_cxf_engine){
			org.openspcoop2.pdd.services.cxf.IntegrationManagerMessage msgRisposta_cxf = null;
			if(modalitaSincrona){
				msgRisposta_cxf = imRichiestaStatoAsincrona_cxf.sendRichiestaStatoAsincronaAsimmetrica(PDCorrelata, toIntegrationManagerMessage_cxf(msgRichiestaStatoAsincrona));
			}else{
				msgRisposta_cxf = imRichiestaStatoAsincrona_cxf.invocaPortaDelegata(PDCorrelata, toIntegrationManagerMessage_cxf(msgRichiestaStatoAsincrona));
			}
			msgRispostaRichiestaStato = toIntegrationManagerMessage_axis14(msgRisposta_cxf);
		}
		
		// Msg di risposta
		Message msgAxisResponseRichiestaStato = null;
		if(modalitaSincrona){
			msgAxisResponseRichiestaStato = Axis14SoapUtils.build(msgRispostaRichiestaStato.getMessage(), false);
		}else{
			msgAxisResponseRichiestaStato = Axis14SoapUtils.build(msgRispostaRichiestaStato.getMessage(), false,false,false);
		}
		
		// Test uguaglianza Body
		Reporter.log("Controllo risposta ottenuta");
		Assert.assertTrue(ClientCore.isEqualsSentAndResponseMessage(msgAxisRichiestaStatoAsincrona,msgAxisResponseRichiestaStato));
		if(msgRispostaRichiestaStato.getProtocolHeaderInfo().getID()==null)
	    	throw new TestSuiteException("ID e-Gov non presenta nell'header del trasporto della ricevuta della richiesta stato.");
	   
		// Identificativo della richiesta stato
	    String identificativoRichiestaStatoAsincrona=msgRispostaRichiestaStato.getProtocolHeaderInfo().getID();
	    Reporter.log("["+tipo+"] Gestione richiesta stato con id="+identificativoRichiestaStatoAsincrona+" effettata.");

		// Attesa terminazione messaggi
	    if(testsuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
	    	
	    	DatabaseComponent dbFruitore = null;
	    	DatabaseComponent dbErogatore = null;
	    	try {
	    	
	    		/** DatabaseComponent */
	    		dbFruitore = DatabaseProperties.getDatabaseComponentFruitore();
	    		dbErogatore = DatabaseProperties.getDatabaseComponentErogatore();
	    		
				if(dbFruitore!=null){
					long countTimeout = System.currentTimeMillis() + (1000* testsuitePropertiesLIB.getTimeoutProcessamentoMessaggiOpenSPCoop());
					while(dbFruitore.getVerificatoreMessaggi().countMsgOpenSPCoop_profiloAsincrono(identificativoRichiestaAsincrona,identificativoRichiestaStatoAsincrona)!=0 && countTimeout>System.currentTimeMillis()){
						try{
						Thread.sleep(testsuitePropertiesLIB.getCheckIntervalProcessamentoMessaggiOpenSPCoop());
						}catch(Exception e){}
					}
				}
				if(dbErogatore!=null){
					long countTimeout = System.currentTimeMillis() + (1000*testsuitePropertiesLIB.getTimeoutProcessamentoMessaggiOpenSPCoop());
					while(dbErogatore.getVerificatoreMessaggi().countMsgOpenSPCoop_profiloAsincrono(identificativoRichiestaAsincrona,identificativoRichiestaStatoAsincrona)!=0 && countTimeout>System.currentTimeMillis()){
						try{
							Thread.sleep(testsuitePropertiesLIB.getCheckIntervalProcessamentoMessaggiOpenSPCoop());
						}catch(Exception e){}
					}
				}
	    	}catch(Exception e){
	    		Reporter.log("["+tipo+"] Errore durante il controllo: "+e.getMessage());
	    	}finally{
	    		try{
	    			if(dbFruitore!=null){
	    				dbFruitore.close();
	    			}
	    		}catch(Exception e){}
	    		try{
	    			if(dbErogatore!=null){
	    				dbErogatore.close();
	    			}
	    		}catch(Exception e){}
	    	}
		}
	    
	    // Metto i due id nel repository
	    repositoryCorrelazioneAsincrona.add(identificativoRichiestaAsincrona,identificativoRichiestaStatoAsincrona);
	    
	    Reporter.log("["+tipo+"] Gestione richiesta-stato con id="+identificativoRichiestaStatoAsincrona+" , correlata a "+identificativoRichiestaAsincrona+" effettata.");

	}
	
	
	
	public static String gestioneProfiloOneway(String PD,String user,String password,Repository repository,Message msgAxisDaInviare, boolean attesaTerminazioneMessaggi) throws Exception{
		return gestioneProfiloOneway(PD, user, password, repository, msgAxisDaInviare, attesaTerminazioneMessaggi, null, null, null, null, null);
	}
	public static String gestioneProfiloOneway(String PD,String user,String password,Repository repository,Message msgAxisDaInviare, boolean attesaTerminazioneMessaggi,
			String tipoSoggettoErogatore,String soggettoErogatore,String tipoServizio,String servizio,String azione) throws Exception{
		// Costruzione msg di richiesta
		SOAPEngine utility = new SOAPEngine(null);
		utility.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,addIDUnivoco);

		// SPCoopMessage
		Message msgAxis = null;
		if(msgAxisDaInviare==null)
			msgAxis = utility.getRequestMessage();
		else
			msgAxis = msgAxisDaInviare;
		org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage msg = new org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		msgAxis.writeTo(bout);
		bout.flush();
		bout.close();
		msg.setMessage(bout.toByteArray());
		
		// SPCoopHeader INFO
		if(soggettoErogatore!=null || servizio!=null || azione!=null){
			org.openspcoop2.pdd.services.axis14.ProtocolHeaderInfo h = new  org.openspcoop2.pdd.services.axis14.ProtocolHeaderInfo();
			h.setTipoDestinatario(tipoSoggettoErogatore);
			h.setDestinatario(soggettoErogatore);
			h.setTipoServizio(tipoServizio);
			h.setServizio(servizio);
			h.setAzione(azione);
			msg.setProtocolHeaderInfo(h);
		}
		
		// IntegrationManager
		org.openspcoop2.pdd.services.axis14.PD_PortType im_axis14 = null;
		if(use_axis14_engine){
			im_axis14 = getIntegrationManagerPD_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/PD"), 
					user,password);
		}
		org.openspcoop2.pdd.services.cxf.PD im_cxf = null;
		if(use_cxf_engine){
			im_cxf = getIntegrationManagerPD_cxf(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/PD"), 
					user,password);
		}
		
		org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage msgRisposta = null;
		if(use_axis14_engine){
			msgRisposta = im_axis14.invocaPortaDelegata(PD, msg);
		}
		else if(use_cxf_engine){
			org.openspcoop2.pdd.services.cxf.IntegrationManagerMessage msgRisposta_cxf = im_cxf.invocaPortaDelegata(PD, toIntegrationManagerMessage_cxf(msg));
			msgRisposta = toIntegrationManagerMessage_axis14(msgRisposta_cxf);
		}
		
		
		// Msg di risposta
		Message msgAxisResponse = Axis14SoapUtils.build(msgRisposta.getMessage(), false,false,false);
		
		// Test uguaglianza Body
		if(msgAxisResponse.getSOAPBody()!=null && msgAxisResponse.getSOAPBody().hasChildNodes()){
			Reporter.log("Controllo risposta ottenuta");
			String idBody=org.openspcoop2.testsuite.core.Utilities.getIDFromOpenSPCoopOKMessage(SPCoopTestsuiteLogger.getInstance(),msgAxisResponse);
		    // Controlla che sia uguale a quello ritornato nell'header della risposta
		    if(idBody==null)
		    	throw new TestSuiteException("ID e-Gov non presenta nella ricevuta OpenSPCoopOK.");
		    if(msgRisposta.getProtocolHeaderInfo().getID()==null)
		    	throw new TestSuiteException("ID e-Gov non presenta nell'header del trasporto della ricevuta.");
		    if(msgRisposta.getProtocolHeaderInfo().getID().equals(idBody)==false)
		    	throw new TestSuiteException("ID e-Gov presente nell'header del trasporto della ricevuta differisce dall'id egov presente nel messaggio OpenSPCoopOK della ricevuta.");
		}
		    
		// Identificativo della richiesta
	    String identificativoRichiestaAsincrona=msgRisposta.getProtocolHeaderInfo().getID();
	    if(repository!=null)
	    	repository.add(identificativoRichiestaAsincrona);
	    
	    Reporter.log("[OneWay] Gestione richiesta con id="+identificativoRichiestaAsincrona+" effettata.");
	    
	    // Attesa terminazione messaggio
	    org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties testsuiteProperties = 
	    	org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance();
	    org.openspcoop2.testsuite.core.TestSuiteProperties testsuitePropertiesLIB = 
	    	org.openspcoop2.testsuite.core.TestSuiteProperties.getInstance();
	    if(attesaTerminazioneMessaggi && testsuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
	    	
	    	DatabaseComponent dbFruitore = null;
	    	DatabaseComponent dbErogatore = null;
	    	try {
	    	
	    		/** DatabaseComponent */
	    		dbFruitore = DatabaseProperties.getDatabaseComponentFruitore();
	    		dbErogatore = DatabaseProperties.getDatabaseComponentErogatore();
	    		
				if(dbFruitore!=null){
					long countTimeout = System.currentTimeMillis() + (1000* testsuitePropertiesLIB.getTimeoutProcessamentoMessaggiOpenSPCoop());
					while(dbFruitore.getVerificatoreMessaggi().countMsgOpenSPCoop(identificativoRichiestaAsincrona)!=0 && countTimeout>System.currentTimeMillis()){
						try{
						Thread.sleep(testsuitePropertiesLIB.getCheckIntervalProcessamentoMessaggiOpenSPCoop());
						}catch(Exception e){}
					}
				}
				if(dbErogatore!=null){
					long countTimeout = System.currentTimeMillis() + (1000*testsuitePropertiesLIB.getTimeoutProcessamentoMessaggiOpenSPCoop());
					while(dbErogatore.getVerificatoreMessaggi().countMsgOpenSPCoop(identificativoRichiestaAsincrona)!=0 && countTimeout>System.currentTimeMillis()){
						try{
							Thread.sleep(testsuitePropertiesLIB.getCheckIntervalProcessamentoMessaggiOpenSPCoop());
						}catch(Exception e){}
					}
				}
	    	}catch(Exception e){
	    		Reporter.log("[oneWay] Errore durante il controllo: "+e.getMessage());
	    	}finally{
	    		try{
	    			if(dbFruitore!=null){
	    				dbFruitore.close();
	    			}
	    		}catch(Exception e){}
	    		try{
	    			if(dbErogatore!=null){
	    				dbErogatore.close();
	    			}
	    		}catch(Exception e){}
	    	}
		}
	    
	    
	    return identificativoRichiestaAsincrona;
	}
	
	private void utilityGetAndTestMsgInBOX(org.openspcoop2.pdd.services.axis14.MessageBox_PortType im_axis14,org.openspcoop2.pdd.services.cxf.MessageBox im_cxf,
			String idMessaggioDaPrelevare,Message msgPubblicato,boolean bodyStream,
			String servizio,String azione) throws Exception{
		org.openspcoop2.pdd.services.axis14.IntegrationManagerMessage msgTmp1 = null;
		try{
			if(use_axis14_engine){
				msgTmp1 = im_axis14.getMessage(idMessaggioDaPrelevare);
			}
			else if(use_cxf_engine){
				msgTmp1 = toIntegrationManagerMessage_axis14(im_cxf.getMessage(idMessaggioDaPrelevare));
			}
			if(msgTmp1==null || msgTmp1.getMessage()==null){
				throw new Exception("Messaggio "+idMessaggioDaPrelevare+" non presente...");
			}
			if(msgTmp1.getProtocolHeaderInfo()==null){
				throw new Exception("Messaggio "+idMessaggioDaPrelevare+" senza SPCoop Header Info...");
			}
			org.openspcoop2.pdd.services.axis14.ProtocolHeaderInfo h = msgTmp1.getProtocolHeaderInfo();
			
			Reporter.log("ID eGov ritornato in SPCoopHeaderInfo ["+h.getID()+"] idAtteso["+idMessaggioDaPrelevare+"]");
			Assert.assertTrue(idMessaggioDaPrelevare.equals(h.getID()));
			
			Reporter.log("Mittente ritornato in SPCoopHeaderInfo ["+h.getTipoMittente()+"]/["+h.getMittente()+"] Atteso["
					+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE+"]/["+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+"]");
			Assert.assertTrue(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE.equals(h.getTipoMittente()));
			Assert.assertTrue(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE.equals(h.getMittente()));
			
			Reporter.log("Destinatario ritornato in SPCoopHeaderInfo ["+h.getTipoDestinatario()+"]/["+h.getDestinatario()+"] Atteso["
					+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+"]/["+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE+"]");
			Assert.assertTrue(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE.equals(h.getTipoDestinatario()));
			Assert.assertTrue(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE.equals(h.getDestinatario()));
			
			Reporter.log("Servizio ritornato in SPCoopHeaderInfo ["+h.getTipoServizio()+"]/["+h.getServizio()+"] Atteso["
					+CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"]/["+servizio+"]");
			Assert.assertTrue(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY.equals(h.getTipoServizio()));
			Assert.assertTrue(servizio.equals(h.getServizio()));
			
			Reporter.log("Azione ritornata in SPCoopHeaderInfo ["+h.getAzione()+"] Atteso["
					+azione+"]");
			Assert.assertTrue(azione.equals(h.getAzione()));
		}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
			Reporter.log("Prelevo messaggi non riuscito ["+e.getCodiceEccezione()+"]: "+e.getDescrizioneEccezione());
			throw e;
		}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
			Reporter.log("Prelevo messaggi non riuscito ["+e.getFaultInfo().getCodiceEccezione()+"]: "+e.getFaultInfo().getDescrizioneEccezione());
			throw e;
		}
		// Converto messaggio in AxisMessage
		Reporter.log("Converto messaggio ["+idMessaggioDaPrelevare+"] in AxisMessage...");
		Message tmp1 = null;
		tmp1 = Axis14SoapUtils.build(msgTmp1.getMessage(), bodyStream);
		Reporter.log("Test uguaglianza messaggio pubblicato e ricevuto...");
		Assert.assertTrue(ClientCore.isEqualsSentAndResponseMessage(msgPubblicato,tmp1));
	}
}
