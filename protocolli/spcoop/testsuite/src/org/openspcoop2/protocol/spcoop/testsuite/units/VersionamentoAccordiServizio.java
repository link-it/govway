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

import java.util.Date;

import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CooperazioneSPCoopBase;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.SPCoopTestsuiteLogger;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test sui profili di collaborazione implementati nella Porta di Dominio
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class VersionamentoAccordiServizio {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "VersionamentoAccordiServizio";
	
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
	 * Test per l'accordo di servizio ASComunicazioneVariazione, senza versione e senza soggetto referente
	 */
	Repository repositoryOneWay=new Repository();
	@Test(groups={VersionamentoAccordiServizio.ID_GRUPPO,VersionamentoAccordiServizio.ID_GRUPPO+".ONEWAY"})
	public void oneWay() throws TestSuiteException, Exception{
		this.collaborazioneSPCoopBase.oneWay(this.repositoryOneWay,CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY,addIDUnivoco);
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
	@Test(groups={VersionamentoAccordiServizio.ID_GRUPPO,VersionamentoAccordiServizio.ID_GRUPPO+".ONEWAY"},dataProvider="OneWay",dependsOnMethods={"oneWay"})
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
	 * Test per il profilo di collaborazione Sincrono per accordo SPC/MinisteroFruitore:ASComunicazioneVariazione:1.0
	 */
	Repository repositoryVersionamento1=new Repository();
	@Test(groups={VersionamentoAccordiServizio.ID_GRUPPO,VersionamentoAccordiServizio.ID_GRUPPO+".SINCRONO_SOGGREFERENTE_NOME_VERSIONE"})
	public void sincronoVersionamento1() throws TestSuiteException, Exception{
		this.collaborazioneSPCoopBase.sincrono(this.repositoryVersionamento1,CostantiTestSuite.PORTA_DELEGATA_VERSIONAMENTO_SOGGREFERENTE_NOME_VERSIONE,addIDUnivoco,false);
	}
	@DataProvider (name="sincronoVersionamento1")
	public Object[][]testSincronoVersionamento1() throws Exception{
		String id=this.repositoryVersionamento1.getNext();
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
	@Test(groups={VersionamentoAccordiServizio.ID_GRUPPO,VersionamentoAccordiServizio.ID_GRUPPO+".SINCRONO_SOGGREFERENTE_NOME_VERSIONE"},
			dataProvider="sincronoVersionamento1",dependsOnMethods={"sincronoVersionamento1"})
	public void testSincronoVersionamento1(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VERSIONAMENTO_ACCORDI,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VERSIONAMENTO_ACCORDI_SOGGREFERENTE_NOME_VERSIONE,
					CostantiTestSuite.SPCOOP_NOME_AZIONE_VERSIONAMENTO_ACCORDI_SOGGREFERENTE_NOME_VERSIONE, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono per accordo SPC/MinisteroFruitore:ASComunicazioneVariazione
	 */
	Repository repositoryVersionamento2=new Repository();
	@Test(groups={VersionamentoAccordiServizio.ID_GRUPPO,VersionamentoAccordiServizio.ID_GRUPPO+".SINCRONO_SOGGREFERENTE_NOME"})
	public void sincronoVersionamento2() throws TestSuiteException, Exception{
		this.collaborazioneSPCoopBase.sincrono(this.repositoryVersionamento2,CostantiTestSuite.PORTA_DELEGATA_VERSIONAMENTO_SOGGREFERENTE_NOME,addIDUnivoco,false);
	}
	@DataProvider (name="sincronoVersionamento2")
	public Object[][]testSincronoVersionamento2() throws Exception{
		String id=this.repositoryVersionamento2.getNext();
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
	@Test(groups={VersionamentoAccordiServizio.ID_GRUPPO,VersionamentoAccordiServizio.ID_GRUPPO+".SINCRONO_SOGGREFERENTE_NOME"},
			dataProvider="sincronoVersionamento2",dependsOnMethods={"sincronoVersionamento2"})
	public void testSincronoVersionamento2(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VERSIONAMENTO_ACCORDI,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VERSIONAMENTO_ACCORDI_SOGGREFERENTE_NOME,
					CostantiTestSuite.SPCOOP_NOME_AZIONE_VERSIONAMENTO_ACCORDI_SOGGREFERENTE_NOME, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono per accordo ASComunicazioneVariazione:1.0
	 */
	Repository repositoryVersionamento3=new Repository();
	@Test(groups={VersionamentoAccordiServizio.ID_GRUPPO,VersionamentoAccordiServizio.ID_GRUPPO+".SINCRONO_NOME_VERSIONE"})
	public void sincronoVersionamento3() throws TestSuiteException, Exception{
		this.collaborazioneSPCoopBase.sincrono(this.repositoryVersionamento3,CostantiTestSuite.PORTA_DELEGATA_VERSIONAMENTO_NOME_VERSIONE,addIDUnivoco,false);
	}
	@DataProvider (name="sincronoVersionamento3")
	public Object[][]testSincronoVersionamento3() throws Exception{
		String id=this.repositoryVersionamento3.getNext();
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
	@Test(groups={VersionamentoAccordiServizio.ID_GRUPPO,VersionamentoAccordiServizio.ID_GRUPPO+".SINCRONO_NOME_VERSIONE"},
			dataProvider="sincronoVersionamento3",dependsOnMethods={"sincronoVersionamento3"})
	public void testSincronoVersionamento3(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_VERSIONAMENTO_ACCORDI,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_VERSIONAMENTO_ACCORDI_NOME_VERSIONE,
					CostantiTestSuite.SPCOOP_NOME_AZIONE_VERSIONAMENTO_ACCORDI_NOME_VERSIONE, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
}
