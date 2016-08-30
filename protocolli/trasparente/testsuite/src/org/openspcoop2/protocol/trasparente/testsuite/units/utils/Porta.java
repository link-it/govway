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



package org.openspcoop2.protocol.trasparente.testsuite.units.utils;

import java.util.Date;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.trasparente.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.core.TrasparenteTestsuiteLogger;
import org.openspcoop2.protocol.trasparente.testsuite.core.Utilities;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.utils.date.DateManager;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Test sui profili di collaborazione implementati nella Porta di Dominio
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Porta {

	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni info;	
	protected CooperazioneTrasparenteBase collaborazioneTrasparenteBase;

	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni infoAutenticato;	
	protected CooperazioneTrasparenteBase collaborazioneTrasparenteBaseAutenticato;

	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni infoFault200;	
	protected CooperazioneTrasparenteBase collaborazioneTrasparenteBaseFault200;

	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni infoFault500;	
	protected CooperazioneTrasparenteBase collaborazioneTrasparenteBaseFault500;

	private String portaDelegataOneWay;
	private String portaDelegataSincrono;
	
	private String portaDelegataOneWayFault200;
	private String portaDelegataSincronoFault200;
	
	private String portaDelegataOneWayFault500;
	private String portaDelegataSincronoFault500;
	
	private String portaDelegataOneWayAutenticata;
	private String usernameOneWayAutenticata;
	private String passwordOneWayAutenticata;
	
	private String portaDelegataSincronoAutenticata;
	private String usernameSincronoAutenticata;
	private String passwordSincronoAutenticata;

	private boolean stateful;
	
	public Porta(SOAPVersion soapVersion, boolean attachments, boolean stateful, boolean isApplicativa, IDSoggetto soggettoFruitore, IDSoggetto soggettoFruitoreAutenticato, IDSoggetto soggettoFruitoreFault500, IDSoggetto soggettoFruitoreFault200, IDSoggetto erogatore) {
		this.stateful = stateful;
		
		this.info = org.openspcoop2.protocol.trasparente.testsuite.core.CooperazioneTrasparenteBase.getCooperazioneBaseInformazioni(soggettoFruitore,
				erogatore,
				false,CostantiTestSuite.PROXY_PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
		this.collaborazioneTrasparenteBase = 
				new CooperazioneTrasparenteBase(attachments, isApplicativa, soapVersion, this.info, 
						org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance(), 
						DatabaseProperties.getInstance(), TrasparenteTestsuiteLogger.getInstance());
		this.infoAutenticato = org.openspcoop2.protocol.trasparente.testsuite.core.CooperazioneTrasparenteBase.getCooperazioneBaseInformazioni(soggettoFruitoreAutenticato,
				erogatore,
				false,CostantiTestSuite.PROXY_PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
		this.collaborazioneTrasparenteBaseAutenticato = 
				new CooperazioneTrasparenteBase(attachments, isApplicativa, soapVersion, this.infoAutenticato, 
						org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance(), 
						DatabaseProperties.getInstance(), TrasparenteTestsuiteLogger.getInstance());
		this.infoFault200 = org.openspcoop2.protocol.trasparente.testsuite.core.CooperazioneTrasparenteBase.getCooperazioneBaseInformazioni(soggettoFruitoreFault200,
				erogatore,
				false,CostantiTestSuite.PROXY_PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
		this.collaborazioneTrasparenteBaseFault200 = 
				new CooperazioneTrasparenteBase(attachments, isApplicativa, soapVersion, this.infoFault200, 
						org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance(), 
						DatabaseProperties.getInstance(), TrasparenteTestsuiteLogger.getInstance());
		this.collaborazioneTrasparenteBaseFault200.setResponseIsFault(true);
		this.infoFault500 = org.openspcoop2.protocol.trasparente.testsuite.core.CooperazioneTrasparenteBase.getCooperazioneBaseInformazioni(soggettoFruitoreFault500,
				erogatore,
				false,CostantiTestSuite.PROXY_PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
		this.collaborazioneTrasparenteBaseFault500 = 
				new CooperazioneTrasparenteBase(attachments, isApplicativa, soapVersion, this.infoFault500, 
						org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance(), 
						DatabaseProperties.getInstance(), TrasparenteTestsuiteLogger.getInstance());
		this.collaborazioneTrasparenteBaseFault500.setResponseIsFault(true);
	}

	private Date dataAvvioGruppoTest = null;
	
	@BeforeClass
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	
	@AfterClass
	public void testOpenspcoopCoreLog() throws Exception{
		FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
	} 

	
	public void setPortaDelegataSincrono(String portaDelegataSincrono) {
		this.portaDelegataSincrono = portaDelegataSincrono;
	}	

	public void setPortaDelegataOneWay(String portaDelegataOneWay) {
		this.portaDelegataOneWay = portaDelegataOneWay;
	}

	public void setPortaDelegataOneWayFault200(String portaDelegataOneWayFault200) {
		this.portaDelegataOneWayFault200 = portaDelegataOneWayFault200;
	}

	public void setPortaDelegataSincronoFault200(
			String portaDelegataSincronoFault200) {
		this.portaDelegataSincronoFault200 = portaDelegataSincronoFault200;
	}

	public void setPortaDelegataOneWayFault500(String portaDelegataOneWayFault500) {
		this.portaDelegataOneWayFault500 = portaDelegataOneWayFault500;
	}

	public void setPortaDelegataSincronoFault500(
			String portaDelegataSincronoFault500) {
		this.portaDelegataSincronoFault500 = portaDelegataSincronoFault500;
	}

	public void setPortaDelegataOneWayAutenticata(
			String portaDelegataOneWayAutenticata) {
		this.portaDelegataOneWayAutenticata = portaDelegataOneWayAutenticata;
	}

	public void setUsernameOneWayAutenticata(String usernameOneWayAutenticata) {
		this.usernameOneWayAutenticata = usernameOneWayAutenticata;
	}

	public void setPasswordOneWayAutenticata(String passwordOneWayAutenticata) {
		this.passwordOneWayAutenticata = passwordOneWayAutenticata;
	}

	public void setPortaDelegataSincronoAutenticata(
			String portaDelegataSincronoAutenticata) {
		this.portaDelegataSincronoAutenticata = portaDelegataSincronoAutenticata;
	}

	public void setUsernameSincronoAutenticata(String usernameSincronoAutenticata) {
		this.usernameSincronoAutenticata = usernameSincronoAutenticata;
	}

	public void setPasswordSincronoAutenticata(String passwordSincronoAutenticata) {
		this.passwordSincronoAutenticata = passwordSincronoAutenticata;
	}

	
	public void oneWay(Repository repositoryOneWay) throws TestSuiteException, Exception{
		this.collaborazioneTrasparenteBase.oneWay(repositoryOneWay,this.portaDelegataOneWay,true, null, null);
	}

	public void oneWayFault200(Repository repositoryOneWay) throws TestSuiteException, Exception{
		if(this.stateful) {
			this.collaborazioneTrasparenteBaseFault200.oneWay(repositoryOneWay,this.portaDelegataOneWayFault200,true, null, null);
		} else {
			try {
				this.collaborazioneTrasparenteBaseFault200.oneWay(repositoryOneWay,this.portaDelegataOneWayFault200,true, null, null);
				Assert.fail("Test OneWayFault200, atteso fault");
			} catch(Exception e) {
				if(!(e instanceof org.apache.axis.AxisFault))
					Assert.fail("Test OneWayFault200, atteso "+org.apache.axis.AxisFault.class.getName()+", trovato " + e.getClass().getName());
			}
		}
	}

	public void oneWayFault500(Repository repositoryOneWay) throws TestSuiteException, Exception{
		if(this.stateful) {
			this.collaborazioneTrasparenteBaseFault500.oneWay(repositoryOneWay,this.portaDelegataOneWayFault500,true, null, null, false);
		} else {
			try {
				this.collaborazioneTrasparenteBaseFault500.oneWay(repositoryOneWay,this.portaDelegataOneWayFault500,true, null, null);
				Assert.fail("Test OneWayFault500, atteso fault");
			} catch(Exception e) {
				if(!(e instanceof org.apache.axis.AxisFault))
					Assert.fail("Test OneWayFault500, atteso "+org.apache.axis.AxisFault.class.getName()+", trovato " + e.getClass().getName());
			}
		}
	}

	public void oneWayAutenticato(Repository repositoryOneWay) throws TestSuiteException, Exception{
		this.collaborazioneTrasparenteBaseAutenticato.oneWay(repositoryOneWay,this.portaDelegataOneWayAutenticata,true, this.usernameOneWayAutenticata, this.passwordOneWayAutenticata);
	}

	public void testOneWay(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneTrasparenteBase.testOneWay(data, msgDiagData,id, CostantiTestSuite.PROXY_TIPO_SERVIZIO,
					CostantiTestSuite.PROXY_NOME_SERVIZIO_ONEWAY,checkServizioApplicativo);
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagData.close();
			}catch(Exception e){}
		}
	}

	public void testOneWayFault500(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneTrasparenteBaseFault500.testFaultOneWay(data, msgDiagData,id, CostantiTestSuite.PROXY_TIPO_SERVIZIO,
					CostantiTestSuite.PROXY_NOME_SERVIZIO_ONEWAY,true, this.stateful,checkServizioApplicativo);
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagData.close();
			}catch(Exception e){}
		}
	}

	public void testOneWayFault200(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneTrasparenteBaseFault200.testFaultOneWay(data, msgDiagData,id, CostantiTestSuite.PROXY_TIPO_SERVIZIO,
					CostantiTestSuite.PROXY_NOME_SERVIZIO_ONEWAY,false, this.stateful,checkServizioApplicativo);
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagData.close();
			}catch(Exception e){}
		}
	}

	public void testOneWayAutenticato(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo, Date date) throws Exception{
		try{
			this.collaborazioneTrasparenteBaseAutenticato.testOneWayAutenticato(data, msgDiagData,id, CostantiTestSuite.PROXY_TIPO_SERVIZIO,
					CostantiTestSuite.PROXY_NOME_SERVIZIO_ONEWAY,checkServizioApplicativo, date);
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagData.close();
			}catch(Exception e){}
		}
	}

	/***
	 * Test per il profilo di collaborazione Sincrono
	 */
	public void sincrono(Repository repository) throws TestSuiteException, Exception{
		this.collaborazioneTrasparenteBase.sincrono(repository,this.portaDelegataSincrono,true);
	}

	public void sincronoFault200(Repository repository) throws TestSuiteException, Exception{
		try {
			this.collaborazioneTrasparenteBaseFault200.sincrono(repository,this.portaDelegataSincronoFault200,true);
			Assert.fail("Test SincronoFault200, atteso fault");
		} catch(Exception e) {
			if(!(e instanceof org.apache.axis.AxisFault))
				Assert.fail("Test SincronoFault200, atteso "+org.apache.axis.AxisFault.class.getName()+", trovato " + e.getClass().getName());
		}
	}

	public void sincronoFault500(Repository repository) throws TestSuiteException, Exception{
		try {
			this.collaborazioneTrasparenteBaseFault500.sincrono(repository,this.portaDelegataSincronoFault500,true);
			Assert.fail("Test SincronoFault500, atteso fault");
		} catch(Exception e) {
			if(!(e instanceof org.apache.axis.AxisFault))
				Assert.fail("Test SincronoFault500, atteso "+org.apache.axis.AxisFault.class.getName()+", trovato " + e.getClass().getName());
		}
	}

	public void testSincrono(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneTrasparenteBase.testSincrono(data, msgDiagData,id, CostantiTestSuite.PROXY_TIPO_SERVIZIO,
					CostantiTestSuite.PROXY_NOME_SERVIZIO_SINCRONO, checkServizioApplicativo);
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagData.close();
			}catch(Exception e){}
		}
	}

	public void testSincronoFault200(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneTrasparenteBaseFault200.testFaultSincrono(data, msgDiagData,id, CostantiTestSuite.PROXY_TIPO_SERVIZIO,
					CostantiTestSuite.PROXY_NOME_SERVIZIO_SINCRONO, false, this.stateful);
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagData.close();
			}catch(Exception e){}
		}
	}

	public void testSincronoFault500(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneTrasparenteBaseFault500.testFaultSincrono(data, msgDiagData,id, CostantiTestSuite.PROXY_TIPO_SERVIZIO,
					CostantiTestSuite.PROXY_NOME_SERVIZIO_SINCRONO, true, this.stateful);
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagData.close();
			}catch(Exception e){}
		}
	}

	public void sincronoAutenticato(Repository repository) throws TestSuiteException, Exception{
		this.collaborazioneTrasparenteBaseAutenticato.sincrono(repository,this.portaDelegataSincronoAutenticata,true,this.usernameSincronoAutenticata,this.passwordSincronoAutenticata);
	}
	
	public void testSincronoAutenticato(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData,String id,boolean checkServizioApplicativo, Date date) throws Exception{
		try{
			this.collaborazioneTrasparenteBaseAutenticato.testSincronoAutenticato(data, msgDiagData,id, CostantiTestSuite.PROXY_TIPO_SERVIZIO,
					CostantiTestSuite.PROXY_NOME_SERVIZIO_SINCRONO, checkServizioApplicativo, date);
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				msgDiagData.close();
			}catch(Exception e){}
		}
	}
	
	
	
	public static Object[][]_getDataProvider(Repository repository) throws Exception{
		return _getDataProvider(repository,true);
	}
	public static Object[][]_getDataProvider(Repository repository, boolean checkServizioApplicativo) throws Exception{
		String id= repository.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(), DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id,checkServizioApplicativo}	
		};
	}

	
	
	
	
	public static void _oneWay(Porta port, Repository repository) throws TestSuiteException, Exception{
		port.oneWay(repository);
	}
	
	public static void _oneWayFault200(Porta port, Repository repository) throws TestSuiteException, Exception{
		port.oneWayFault200(repository);
	}
	
	public static void _oneWayFault500(Porta port, Repository repository) throws TestSuiteException, Exception{
		port.oneWayFault500(repository);
	}
	
	public static void _testOneWay(Porta port, DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		port.testOneWay(data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	public static void _testOneWayFault200(Porta port, DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		port.testOneWayFault200(data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	public static void _testOneWayFault500(Porta port, DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		port.testOneWayFault500(data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	public static void _testOneWayAutenticato(Porta port, DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo, Date date) throws Exception{
		port.testOneWayAutenticato(data, msgDiagData, id, checkServizioApplicativo, date);
	}	
	
	public static void _sincrono(Porta port, Repository repository) throws TestSuiteException, Exception{
		port.sincrono(repository);
	}
	
	public static void _sincronoFault200(Porta port, Repository repository) throws TestSuiteException, Exception{
		port.sincronoFault200(repository);
	}
	
	public static void _sincronoFault500(Porta port, Repository repository) throws TestSuiteException, Exception{
		port.sincronoFault500(repository);
	}
	
	public static void _testSincrono(Porta port, DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		port.testSincrono(data, msgDiagData, id, checkServizioApplicativo);
	}

	public static void _testSincronoFault200(Porta port, DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		port.testSincronoFault200(data, msgDiagData, id, checkServizioApplicativo);
	}

	public static void _testSincronoFault500(Porta port, DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		port.testSincronoFault500(data, msgDiagData, id, checkServizioApplicativo);
	}

	public static void _testSincronoAutenticato(Porta port, DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo, Date date) throws Exception{
		port.testSincronoAutenticato(data, msgDiagData, id, checkServizioApplicativo, date);
	}

	public static void _sincronoAutenticato(Porta port, Repository repository) throws TestSuiteException, Exception{
		port.sincronoAutenticato(repository);
	}
	
	public static void _oneWayAutenticato(Porta port, Repository repository) throws TestSuiteException, Exception{
		port.oneWayAutenticato(repository);
	}

}
