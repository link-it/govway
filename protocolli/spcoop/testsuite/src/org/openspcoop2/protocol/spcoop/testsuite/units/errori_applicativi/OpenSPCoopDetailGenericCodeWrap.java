/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.spcoop.testsuite.units.errori_applicativi;

import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test su richieste applicative malformate indirizzate alla Porta di Dominio
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoopDetailGenericCodeWrap extends OpenSPCoopDetail {

	public OpenSPCoopDetailGenericCodeWrap() {
		super(true, false);
	}
	
	

	
	@BeforeGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		super._testOpenspcoopCoreLog_raccoltaTempoAvvioTest();
	} 	
	@AfterGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		super._testOpenspcoopCoreLog();
	} 
	
	
	
	
	
	// TODO: Il tunnel SOAP, avendo come default, la restituzione di un errore XML (errore cnipa = xml), non viene mai generato un detail openspcoop.
	
	

	
	/** ERRORI 4XX */
	
	@DataProvider (name="personalizzazioniErroriApplicativi4XX")
	public Object[][] personalizzazioniErroriApplicativi4XX(){
		return super._personalizzazioniErroriApplicativi4XX();
	}
	
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,OpenSPCoopDetail.ID_GRUPPO,OpenSPCoopDetail.ID_GRUPPO+".ErroriApplicativi4XX"},dataProvider="personalizzazioniErroriApplicativi4XX")
	public void testErroriApplicativi4XX(String username,String password,String portaDelegata) throws Exception{
		super._testErroriApplicativi4XX(username, password, portaDelegata);
	}

	
	
	
	
	/** ERRORI 5XX */
	
	@DataProvider (name="personalizzazioniErroriApplicativi5XX")
	public Object[][] personalizzazioniErroriApplicativi5XX(){
		return super._personalizzazioniErroriApplicativi5XX();
	}
	
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,OpenSPCoopDetail.ID_GRUPPO,OpenSPCoopDetail.ID_GRUPPO+".ErroriApplicativi5XX"},dataProvider="personalizzazioniErroriApplicativi5XX")
	public void testErroriApplicativi5XX(String username,String password,String portaDelegata) throws Exception{
		super._testErroriApplicativi5XX(username, password, portaDelegata);
	}

	
	
	
	/** SOAP FAULT APPLICATIVO ARRICCHITO */
	
	@DataProvider (name="personalizzazioniFaultApplicativi")
	public Object[][] personalizzazioniFaultApplicativi(){
		return super._personalizzazioniFaultApplicativi();
	}
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,OpenSPCoopDetail.ID_GRUPPO,OpenSPCoopDetail.ID_GRUPPO+".SOAP_FAULT_APPLICATIVO"},dataProvider="personalizzazioniFaultApplicativi")
	public void testFaultApplicativoArricchitoFAULTCNIPA_Default(String servizioApplicativoFruitore) throws Exception{
		super._testFaultApplicativoArricchitoFAULTCNIPA_Default(servizioApplicativoFruitore);
	}

	
	
	
	/** SOAP FAULT PDD ARRICCHITO */
	
	@DataProvider (name="personalizzazioniFaultApplicativiPdD")
	public Object[][] personalizzazioniFaultApplicativiPdD(){
		return super._personalizzazioniFaultApplicativiPdD();
	}
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,OpenSPCoopDetail.ID_GRUPPO,OpenSPCoopDetail.ID_GRUPPO+".SOAP_FAULT_PDD"},dataProvider="personalizzazioniFaultApplicativiPdD")
	public void testFaultPddArricchitoFAULTCNIPA_Default(String servizioApplicativoFruitore) throws Exception{
		super._testFaultPddArricchitoFAULTCNIPA_Default(servizioApplicativoFruitore);
	}

	
	
	
	/** ERRORE CONNETTORE PDD: connection refused ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreConnectionRefused")
	public Object[][] personalizzazioniErroreConnectionRefused(){
		return super._personalizzazioniErroreConnectionRefused();
	}
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,OpenSPCoopDetail.ID_GRUPPO,OpenSPCoopDetail.ID_GRUPPO+".CONNECTION_REFUSED_PDD"},dataProvider="personalizzazioniErroreConnectionRefused")
	public void testServizioApplicativoConnectionRefused(String servizioApplicativoFruitore) throws Exception{
		super._testServizioApplicativoConnectionRefused(servizioApplicativoFruitore);
	}

	
	
	
	/** ERRORE CONNETTORE SA: connection refused ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreConnectionRefusedServizioApplicativo")
	public Object[][] personalizzazioniErroreConnectionRefusedServizioApplicativo(){
		return super._personalizzazioniErroreConnectionRefusedServizioApplicativo();
	}
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,OpenSPCoopDetail.ID_GRUPPO,OpenSPCoopDetail.ID_GRUPPO+".CONNECTION_REFUSED_SA"},dataProvider="personalizzazioniErroreConnectionRefusedServizioApplicativo")
	public void testServizioApplicativoConnectionRefusedServizioApplicativo(String servizioApplicativoFruitore) throws Exception{
		super._testServizioApplicativoConnectionRefusedServizioApplicativo(servizioApplicativoFruitore);
	}

	
	
	
	/** ERRORE CONNETTORE PDD: Connect Timed Out ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreConnectTimedOut")
	public Object[][] personalizzazioniErroreConnectTimedOut(){
		return super._personalizzazioniErroreTimedOut();
	}
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,OpenSPCoopDetail.ID_GRUPPO,OpenSPCoopDetail.ID_GRUPPO+".CONNECT_TIMED_OUT_PDD"},dataProvider="personalizzazioniErroreConnectTimedOut")
	public void testServizioApplicativoConnectTimedOut(String servizioApplicativoFruitore) throws Exception{
		super._testServizioApplicativoTimedOutServizioApplicativo(servizioApplicativoFruitore, false);
	}

	
	
	
	/** ERRORE CONNETTORE SA: Connect Timed Out ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreConnectTimedOutServizioApplicativo")
	public Object[][] personalizzazioniErroreConnectTimedOutServizioApplicativo(){
		return super._personalizzazioniErroreTimedOutServizioApplicativo();
	}
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,OpenSPCoopDetail.ID_GRUPPO,OpenSPCoopDetail.ID_GRUPPO+".CONNECT_TIMED_OUT_SA"},dataProvider="personalizzazioniErroreConnectTimedOutServizioApplicativo")
	public void testServizioApplicativoConnectTimedOutServizioApplicativo(String servizioApplicativoFruitore) throws Exception{
		super._testServizioApplicativoTimedOutServizioApplicativo(servizioApplicativoFruitore, false);
	}

	
	
	
	/** ERRORE CONNETTORE PDD: Read Timed Out ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreReadTimedOut")
	public Object[][] personalizzazioniErroreReadTimedOut(){
		return super._personalizzazioniErroreTimedOut();
	}
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,OpenSPCoopDetail.ID_GRUPPO,OpenSPCoopDetail.ID_GRUPPO+".READ_TIMED_OUT_PDD"},dataProvider="personalizzazioniErroreReadTimedOut")
	public void testServizioApplicativoReadTimedOut(String servizioApplicativoFruitore) throws Exception{
		super._testServizioApplicativoTimedOutServizioApplicativo(servizioApplicativoFruitore, true);
	}

	
	
	
	/** ERRORE CONNETTORE SA: Read Timed Out ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreReadTimedOutServizioApplicativo")
	public Object[][] personalizzazioniErroreReadTimedOutServizioApplicativo(){
		return super._personalizzazioniErroreTimedOutServizioApplicativo();
	}
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,OpenSPCoopDetail.ID_GRUPPO,OpenSPCoopDetail.ID_GRUPPO+".READ_TIMED_OUT_SA"},dataProvider="personalizzazioniErroreReadTimedOutServizioApplicativo")
	public void testServizioApplicativoReadTimedOutServizioApplicativo(String servizioApplicativoFruitore) throws Exception{
		super._testServizioApplicativoTimedOutServizioApplicativo(servizioApplicativoFruitore, true);
	}
}
