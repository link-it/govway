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

import java.io.IOException;
import java.util.Date;

import javax.xml.soap.SOAPException;

import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.asincrono.RepositoryConsegnaRisposteAsincroneSimmetriche;
import org.openspcoop2.testsuite.core.asincrono.RepositoryCorrelazioneIstanzeAsincrone;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatiServizio;
import org.openspcoop2.testsuite.server.ServerRicezioneRispostaAsincronaSimmetrica;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.message.SOAPVersion;
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
 * Test sui profili di collaborazione implementati nella Porta di Dominio e registrati nel Registro dei Servizi come port type e operation
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortTypes {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "PortTypes";
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni info = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBase = 
		new CooperazioneBase(false, SOAPVersion.SOAP11, this.info, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());


	private static boolean addIDUnivoco = true;



	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	@AfterGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
	} 
	
	
	
	
	/***
	 * Test per il profilo di collaborazione OneWay
	 */
	Repository repositoryOneWay=new Repository();
	@Test(groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ONEWAY"})
	public void oneWay() throws TestSuiteException, Exception{
		this.collaborazioneSPCoopBase.oneWay(this.repositoryOneWay,CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_PORT_TYPE,addIDUnivoco);
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
	@Test(groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ONEWAY"},dataProvider="OneWay",dependsOnMethods={"oneWay"})
	public void testOneWay(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY_PORT_TYPE,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY_PORT_TYPE,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_PORT_TYPE_AZIONE_TEST, checkServizioApplicativo,
					null);
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
	@Test(groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".SINCRONO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincrono() throws TestSuiteException, IOException, SOAPException{
		this.collaborazioneSPCoopBase.sincrono(this.repositorySincrono,CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_PORT_TYPE,addIDUnivoco);
	}
	@DataProvider (name="Sincrono")
	public Object[][]testSincrono()throws Exception{
		String id=this.repositorySincrono.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".SINCRONO"},dataProvider="Sincrono",dependsOnMethods={"sincrono"})
	public void testSincrono(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO_PORT_TYPE,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO_PORT_TYPE,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_PORT_TYPE_AZIONE_TEST, checkServizioApplicativo,
				null);
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
	@Test (groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ASINCRONO_SIMMETRICO_asincr"})
	public void startServerRicezioneRispostaAsincronaSimmetrica_modalitaAsincrona(){
		this.serverRicezioneRispostaAsincronaSimmetrica_modalitaAsincrona.start();
	}
	@Test (groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ASINCRONO_SIMMETRICO_asincr"},description="Test di tipo asincrono simmetrico con modalita asincrona",dependsOnMethods="startServerRicezioneRispostaAsincronaSimmetrica_modalitaAsincrona")
	public void asincronoSimmetrico_ModalitaAsincrona() throws Exception{
		this.collaborazioneSPCoopBase.asincronoSimmetrico_modalitaAsincrona(
				CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_MODALITA_ASINCRONA_PORT_TYPE,
				CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_CORRELATO_MODALITA_ASINCRONA_PORT_TYPE,
				"profiloAsincrono_richiestaAsincrona","123456",
				this.repositoryConsegnaRisposteAsincroneSimmetriche_modalitaAsincrona, 
				this.repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaAsincrona,addIDUnivoco);
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
	@Test(groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ASINCRONO_SIMMETRICO_asincr"},dataProvider="AsincronoSimmetrico_ModalitaAsincrona",dependsOnMethods={"asincronoSimmetrico_ModalitaAsincrona"})
	public void testAsincronoSimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testAsincronoSimmetrico_ModalitaAsincrona(data, id, 
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO_PORT_TYPE,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO_PORT_TYPE,
					CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_RICHIESTA_MODALITA_ASINCRONA_PORT_TYPE , checkServizioApplicativo,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_CORRELATO_ASINCRONO_SIMMETRICO_PORT_TYPE,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_CORRELATO_ASINCRONO_SIMMETRICO_PORT_TYPE,
					id);
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
	@Test(groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ASINCRONO_SIMMETRICO_asincr"},dataProvider="RispostaAsincronoSimmetrico_ModalitaAsincrona",dependsOnMethods={"testAsincronoSimmetrico_ModalitaAsincrona"})
	public void testRispostaAsincronoSimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testRispostaAsincronoSimmetrico_ModalitaAsincrona(data, id, 
					idCorrelazioneAsincrona,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_CORRELATO_ASINCRONO_SIMMETRICO_PORT_TYPE,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_CORRELATO_ASINCRONO_SIMMETRICO_PORT_TYPE,
					CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_RISPOSTA_MODALITA_ASINCRONA_PORT_TYPE, 
					checkServizioApplicativo,this.repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaAsincrona,
					idCorrelazioneAsincrona);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@Test (groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ASINCRONO_SIMMETRICO_asincr"},dependsOnMethods={"testRispostaAsincronoSimmetrico_ModalitaAsincrona"})
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
	@Test (groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ASINCRONO_SIMMETRICO_sincr"})
	public void startServerRicezioneRispostaAsincronaSimmetrica_modalitaSincrona(){
		this.serverRicezioneRispostaAsincronaSimmetrica_modalitaSincrona.start();
	}
	@Test (groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ASINCRONO_SIMMETRICO_sincr"},description="Test di tipo asincrono simmetrico con modalita asincrona",dependsOnMethods="startServerRicezioneRispostaAsincronaSimmetrica_modalitaSincrona")
	public void asincronoSimmetrico_ModalitaSincrona() throws Exception{		
		this.collaborazioneSPCoopBase.asincronoSimmetrico_modalitaSincrona(
				CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_MODALITA_SINCRONA_PORT_TYPE,
				CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_SIMMETRICO_CORRELATO_MODALITA_SINCRONA_PORT_TYPE,
				"profiloAsincrono_richiestaSincrona","123456",
				this.repositoryConsegnaRisposteAsincroneSimmetriche_modalitaSincrona, this.repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaSincrona,addIDUnivoco);
	}
	@DataProvider (name="AsincronoSimmetrico_ModalitaSincrona")
	public Object[][]testAsincronoSimmetrico_ModalitaSincrona() throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaSincrona.getNextIDRichiesta();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ASINCRONO_SIMMETRICO_sincr"},dataProvider="AsincronoSimmetrico_ModalitaSincrona",dependsOnMethods={"asincronoSimmetrico_ModalitaSincrona"})
	public void testAsincronoSimmetrico_ModalitaSincrona(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testAsincronoSimmetrico_ModalitaSincrona(data, id, 
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO_PORT_TYPE,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO_PORT_TYPE,
					CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_RICHIESTA_MODALITA_SINCRONA_PORT_TYPE, checkServizioApplicativo,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_CORRELATO_ASINCRONO_SIMMETRICO_PORT_TYPE,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_CORRELATO_ASINCRONO_SIMMETRICO_PORT_TYPE,
					id);
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
	@Test(groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ASINCRONO_SIMMETRICO_sincr"},dataProvider="RispostaAsincronoSimmetrico_ModalitaSincrona",dependsOnMethods={"testAsincronoSimmetrico_ModalitaSincrona"})
	public void testRispostaAsincronoSimmetrico_ModalitaSincrona(DatabaseComponent data,String id, String idCorrelazioneAsincrona,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testRispostaAsincronoSimmetrico_ModalitaSincrona(data, id, idCorrelazioneAsincrona, 
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_CORRELATO_ASINCRONO_SIMMETRICO_PORT_TYPE,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_CORRELATO_ASINCRONO_SIMMETRICO_PORT_TYPE,
					CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_RISPOSTA_MODALITA_SINCRONA_PORT_TYPE, checkServizioApplicativo,
					idCorrelazioneAsincrona);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@Test (groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ASINCRONO_SIMMETRICO_sincr"},dependsOnMethods={"testRispostaAsincronoSimmetrico_ModalitaSincrona"})
	public void stopServerRicezioneRispostaAsincronaSimmetrica_modalitaSincrona(){
		this.serverRicezioneRispostaAsincronaSimmetrica_modalitaSincrona.closeSocket();
	}







	/***
	 * Test per il profilo di collaborazione Asincrono Asimmetrico, modalita asincrona
	 */
	RepositoryCorrelazioneIstanzeAsincrone repositoryCorrelazioneIstanzeAsincroneAsimmetriche_modalitaAsincrona = new RepositoryCorrelazioneIstanzeAsincrone();
	@Test(groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ASINCRONO_ASIMMETRICO_asincr"})
	public void asincronoAsimmetrico_ModalitaAsincrona() throws TestSuiteException, Exception{
		this.collaborazioneSPCoopBase.asincronoAsimmetrico_modalitaAsincrona(
				CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_MODALITA_ASINCRONA_PORT_TYPE,
				CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_MODALITA_ASINCRONA_PORT_TYPE,
				this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_modalitaAsincrona,addIDUnivoco);
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
	@Test(groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ASINCRONO_ASIMMETRICO_asincr"},dataProvider="AsincronoAsimmetrico_ModalitaAsincrona",dependsOnMethods={"asincronoAsimmetrico_ModalitaAsincrona"})
	public void testAsincronoAsimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testAsincronoAsimmetrico_ModalitaAsincrona(data, id, 
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO_PORT_TYPE,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO_PORT_TYPE,
					CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_RICHIESTA_MODALITA_ASINCRONA_PORT_TYPE, checkServizioApplicativo,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_CORRELATO_ASINCRONO_ASIMMETRICO_PORT_TYPE,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_CORRELATO_ASINCRONO_ASIMMETRICO_PORT_TYPE,
					id);
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
	@Test(groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ASINCRONO_ASIMMETRICO_asincr"},dataProvider="RispostaAsincronoAsimmetrico_ModalitaAsincrona",dependsOnMethods={"testAsincronoAsimmetrico_ModalitaAsincrona"})
	public void testRispostaAsincronoAsimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testRispostaAsincronoAsimmetrico_ModalitaAsincrona(data, id, idCorrelazioneAsincrona,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_CORRELATO_ASINCRONO_ASIMMETRICO_PORT_TYPE,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_CORRELATO_ASINCRONO_ASIMMETRICO_PORT_TYPE,
					CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_RICHIESTA_STATO_MODALITA_ASINCRONA_PORT_TYPE, 
					checkServizioApplicativo,
					idCorrelazioneAsincrona);
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
	@Test(groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ASINCRONO_ASIMMETRICO_sincr"})
	public void asincronoAsimmetrico_modalitaSincrona() throws TestSuiteException, IOException, SOAPException{
		this.collaborazioneSPCoopBase.asincronoAsimmetrico_modalitaSincrona(
				CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_MODALITA_SINCRONA_PORT_TYPE,
				CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_MODALITA_SINCRONA_PORT_TYPE,
				this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_modalitaSincrona,addIDUnivoco);
	}
	@DataProvider (name="AsincronoAsimmetrico_modalitaSincrona")
	public Object[][]testAsincronoAsimmetrico_modalitaSincrona()throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_modalitaSincrona.getNextIDRichiesta();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ASINCRONO_ASIMMETRICO_sincr"},dataProvider="AsincronoAsimmetrico_modalitaSincrona",dependsOnMethods={"asincronoAsimmetrico_modalitaSincrona"})
	public void testAsincronoAsimmetrico_modalitaSincrona(DatabaseComponent data,String id, boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testAsincronoAsimmetrico_modalitaSincrona(data, id, 
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO_PORT_TYPE,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO_PORT_TYPE,
					CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_RICHIESTA_MODALITA_SINCRONA_PORT_TYPE,checkServizioApplicativo,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_CORRELATO_ASINCRONO_ASIMMETRICO_PORT_TYPE,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_CORRELATO_ASINCRONO_ASIMMETRICO_PORT_TYPE,
					id);
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
	@Test(groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ASINCRONO_ASIMMETRICO_sincr"},dataProvider="RispostaAsincronoAsimmetrico_modalitaSincrona",dependsOnMethods={"testAsincronoAsimmetrico_modalitaSincrona"})
	public void testRispostaAsincronoAsimmetrico_modalitaSincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testRispostaAsincronoAsimmetrico_modalitaSincrona(data, id, idCorrelazioneAsincrona,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_CORRELATO_ASINCRONO_ASIMMETRICO_PORT_TYPE,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_CORRELATO_ASINCRONO_ASIMMETRICO_PORT_TYPE,
					CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_RICHIESTA_STATO_MODALITA_SINCRONA_PORT_TYPE,
					checkServizioApplicativo,
					idCorrelazioneAsincrona);
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
	@Test(groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ONEWAY_LOOPBACK"})
	public void oneWayLoopback() throws TestSuiteException, Exception{
		this.collaborazioneSPCoopBase.oneWay(this.repositoryOneWayLoopback,CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_LOOPBACK_PORT_TYPE,addIDUnivoco);
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
	@Test(groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ONEWAY_LOOPBACK"},dataProvider="OneWayLoopback",dependsOnMethods={"oneWayLoopback"})
	public void testOneWayLoopback(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("[SOAP] Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("[SOAP] Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("[SOAP] Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("[SOAP] Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY_PORT_TYPE, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY_PORT_TYPE, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("[SOAP] Controllo valore Azione Busta con riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_PORT_TYPE_AZIONE_TEST));
			Reporter.log("[SOAP] Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("[SOAP] Controllo valore Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(id, null));
			Reporter.log("[SOAP] Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("[SOAP] Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
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
	@Test(groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ASINCRONO_ASIMMETRICO_asin_azCorrelata"})
	public void asincronoAsimmetrico_AzioneCorrelata_ModalitaAsincrona() throws TestSuiteException, Exception{
		this.collaborazioneSPCoopBase.asincronoAsimmetrico_modalitaAsincrona(
				CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_AZIONE_CORRELATA_MODALITA_ASINCRONA_PORT_TYPE,
				CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_AZIONE_CORRELATA_MODALITA_ASINCRONA_PORT_TYPE,
				this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaAsincrona,addIDUnivoco);
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
	@Test(groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ASINCRONO_ASIMMETRICO_asin_azCorrelata"},dataProvider="AsincronoAsimmetrico_AzioneCorrelata_ModalitaAsincrona",dependsOnMethods={"asincronoAsimmetrico_AzioneCorrelata_ModalitaAsincrona"})
	public void testAsincronoAsimmetrico_AzioneCorrelata_ModalitaAsincrona(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testAsincronoAsimmetrico_ModalitaAsincrona(data, id, 
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO_PORT_TYPE_OPERATION_CORRELATA,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO_PORT_TYPE_OPERATION_CORRELATA,
					CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_RICHIESTA_MODALITA_ASINCRONA_PORT_TYPE_OPERATION_CORRELATA, checkServizioApplicativo,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO_PORT_TYPE_OPERATION_CORRELATA,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO_PORT_TYPE_OPERATION_CORRELATA,
					id);
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
	@Test(groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ASINCRONO_ASIMMETRICO_asin_azCorrelata"},dataProvider="RispostaAsincronoAsimmetrico_AzioneCorrelata_ModalitaAsincrona",dependsOnMethods={"testAsincronoAsimmetrico_AzioneCorrelata_ModalitaAsincrona"})
	public void testRispostaAsincronoAsimmetrico_AzioneCorrelata_ModalitaAsincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testRispostaAsincronoAsimmetrico_ModalitaAsincrona(data, id, idCorrelazioneAsincrona,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO_PORT_TYPE_OPERATION_CORRELATA,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO_PORT_TYPE_OPERATION_CORRELATA,
					CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_RICHIESTA_STATO_MODALITA_ASINCRONA_PORT_TYPE_OPERATION_CORRELATA, 
					checkServizioApplicativo,
					idCorrelazioneAsincrona);
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
	@Test(groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ASINCRONO_ASIMMETRICO_sin_azCorrelata"})
	public void asincronoAsimmetrico_AzioneCorrelata_modalitaSincrona() throws TestSuiteException, IOException, SOAPException{
		this.collaborazioneSPCoopBase.asincronoAsimmetrico_modalitaSincrona(
				CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_AZIONE_CORRELATA_MODALITA_SINCRONA_PORT_TYPE,
				CostantiTestSuite.PORTA_DELEGATA_PROFILO_ASINCRONO_ASIMMETRICO_CORRELATO_AZIONE_CORRELATA_MODALITA_SINCRONA_PORT_TYPE,
				this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaSincrona,addIDUnivoco);
	}
	@DataProvider (name="AsincronoAsimmetrico_AzioneCorrelata_modalitaSincrona")
	public Object[][]testAsincronoAsimmetrico_AzioneCorrelata_modalitaSincrona()throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaSincrona.getNextIDRichiesta();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ASINCRONO_ASIMMETRICO_sin_azCorrelata"},dataProvider="AsincronoAsimmetrico_AzioneCorrelata_modalitaSincrona",dependsOnMethods={"asincronoAsimmetrico_AzioneCorrelata_modalitaSincrona"})
	public void testAsincronoAsimmetrico_AzioneCorrelata_modalitaSincrona(DatabaseComponent data,String id, boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testAsincronoAsimmetrico_modalitaSincrona(data, id, 
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO_PORT_TYPE_OPERATION_CORRELATA,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO_PORT_TYPE_OPERATION_CORRELATA,
					CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_RICHIESTA_MODALITA_SINCRONA_PORT_TYPE_OPERATION_CORRELATA,checkServizioApplicativo,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO_PORT_TYPE_OPERATION_CORRELATA,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO_PORT_TYPE_OPERATION_CORRELATA,
					id);
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
	@Test(groups={PortTypes.ID_GRUPPO,PortTypes.ID_GRUPPO+".ASINCRONO_ASIMMETRICO_sin_azCorrelata"},dataProvider="RispostaAsincronoAsimmetrico_AzioneCorrelata_modalitaSincrona",dependsOnMethods={"testAsincronoAsimmetrico_AzioneCorrelata_modalitaSincrona"})
	public void testRispostaAsincronoAsimmetrico_AzioneCorrelata_modalitaSincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testRispostaAsincronoAsimmetrico_modalitaSincrona(data, id, idCorrelazioneAsincrona,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO_PORT_TYPE_OPERATION_CORRELATA,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO_PORT_TYPE_OPERATION_CORRELATA,
					CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_RICHIESTA_STATO_MODALITA_SINCRONA_PORT_TYPE_OPERATION_CORRELATA,
					checkServizioApplicativo,
					idCorrelazioneAsincrona);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
}
