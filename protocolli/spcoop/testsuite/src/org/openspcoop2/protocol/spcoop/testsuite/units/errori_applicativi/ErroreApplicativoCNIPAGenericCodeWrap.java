/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ErroreApplicativoCNIPAGenericCodeWrap extends ErroreApplicativoCNIPA {

	
	public ErroreApplicativoCNIPAGenericCodeWrap() {
		super(true, false);
	}
	

	@BeforeGroups (alwaysRun=true , groups=ErroreApplicativoCNIPA.ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		super._testOpenspcoopCoreLog_raccoltaTempoAvvioTest();
	} 	
	@AfterGroups (alwaysRun=true , groups=ErroreApplicativoCNIPA.ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		super._testOpenspcoopCoreLog();
	} 

	
	
	
	/** ERRORI 4XX */
	
	@DataProvider (name="personalizzazioniErroriApplicativi4XX")
	public Object[][] personalizzazioniErroriApplicativi4XX(){
		return super._personalizzazioniErroriApplicativi4XX();
	}
	
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".ErroriApplicativi4XX"},dataProvider="personalizzazioniErroriApplicativi4XX")
	public void testErroriApplicativi4XX(String username,String password,String portaDelegata) throws Exception{
		super._testErroriApplicativi4XX(username, password, portaDelegata);
	}
	
	/** SERVIZIO TUNNEL SOAP: ERRORI 4XX */
	
	@DataProvider (name="personalizzazioniErroriApplicativi4XXTunnelSOAP")
	public Object[][] personalizzazioniErroriApplicativi4XXTunnelSOAP(){
		return super._personalizzazioniErroriApplicativi4XXTunnelSOAP();
	}
	
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".ErroriApplicativi4XX_TUNNEL_SOAP"},dataProvider="personalizzazioniErroriApplicativi4XXTunnelSOAP")
	public void testErroriApplicativi4XXTunnelSOAP(String username,String password,String portaDelegata) throws Exception{
		super._testErroriApplicativi4XXTunnelSOAP(username,password,portaDelegata);
	}

	
	
	
	/** ERRORI 5XX */
	
	@DataProvider (name="personalizzazioniErroriApplicativi5XX")
	public Object[][] personalizzazioniErroriApplicativi5XX(){
		return super._personalizzazioniErroriApplicativi5XX();
	}
	
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".ErroriApplicativi5XX"},dataProvider="personalizzazioniErroriApplicativi5XX")
	public void testErroriApplicativi5XX(String username,String password,String portaDelegata) throws Exception{
		super._testErroriApplicativi5XX(username, password, portaDelegata);
	}
	
	/** SERVIZIO TUNNEL SOAP: ERRORI 5XX */
	
	@DataProvider (name="personalizzazioniErroriApplicativi5XXTunnelSOAP")
	public Object[][] personalizzazioniErroriApplicativi5XXTunnelSOAP(){
		return super._personalizzazioniErroriApplicativi5XXTunnelSOAP();
	}
	
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".ErroriApplicativi5XX_TUNNEL_SOAP"},dataProvider="personalizzazioniErroriApplicativi5XXTunnelSOAP")
	public void testErroriApplicativi5XXTunnelSOAP(String username,String password,String portaDelegata) throws Exception{
		super._testErroriApplicativi5XXTunnelSOAP(username, password, portaDelegata);
	}

	
	
	
	/** SOAP FAULT APPLICATIVO ARRICCHITO */
	
	@DataProvider (name="personalizzazioniFaultApplicativi")
	public Object[][] personalizzazioniFaultApplicativi(){
		return super._personalizzazioniFaultApplicativi();
	}
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".SOAP_FAULT_APPLICATIVO"},dataProvider="personalizzazioniFaultApplicativi")
	public void testFaultApplicativoArricchitoFAULTCNIPA_Default(String servizioApplicativoFruitore) throws Exception{
		super._testFaultApplicativoArricchitoFAULTCNIPA_Default(servizioApplicativoFruitore);
	}
	
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".SOAP_FAULT_APPLICATIVO_2"},dataProvider="personalizzazioniFaultApplicativi")
	public void testFaultApplicativoArricchitoFAULTCNIPA_FaultGeneratoConPrefixErrato(String servizioApplicativoFruitore) throws Exception{
		super._testFaultApplicativoArricchitoFAULTCNIPA_FaultGeneratoConPrefixErrato(servizioApplicativoFruitore);
	}

	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".SOAP_FAULT_APPLICATIVO_3"},dataProvider="personalizzazioniFaultApplicativi")
	public void testFaultApplicativoArricchitoFAULTCNIPA_FaultGeneratoConSenzaPrefix(String servizioApplicativoFruitore) throws Exception{
		super._testFaultApplicativoArricchitoFAULTCNIPA_FaultGeneratoConSenzaPrefix(servizioApplicativoFruitore);
	}

	
	
	/** SERVIZIO TUNNEL SOAP: SOAP FAULT APPLICATIVO ARRICCHITO */
	
	@DataProvider (name="personalizzazioniFaultApplicativiTunnelSOAP")
	public Object[][] personalizzazioniFaultApplicativiTunnelSOAP(){
		return super._personalizzazioniFaultApplicativiTunnelSOAP();
	}
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".SOAP_FAULT_APPLICATIVO_TUNNEL_SOAP"},dataProvider="personalizzazioniFaultApplicativiTunnelSOAP")
	public void testFaultApplicativoArricchitoFAULTCNIPA_Default_TunnelSOAP(String servizioApplicativoFruitore) throws Exception{
		super._testFaultApplicativoArricchitoFAULTCNIPA_Default_TunnelSOAP(servizioApplicativoFruitore);
	}

	
	
	
	/** SOAP FAULT PDD ARRICCHITO */
	
	@DataProvider (name="personalizzazioniFaultApplicativiPdD")
	public Object[][] personalizzazioniFaultApplicativiPdD(){
		return super._personalizzazioniFaultApplicativiPdD();
	}
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".SOAP_FAULT_PDD"},dataProvider="personalizzazioniFaultApplicativiPdD")
	public void testFaultPddArricchitoFAULTCNIPA_Default(String servizioApplicativoFruitore) throws Exception{
		super._testFaultPddArricchitoFAULTCNIPA_Default(servizioApplicativoFruitore);
	}
	
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".SOAP_FAULT_PDD_2"},dataProvider="personalizzazioniFaultApplicativiPdD")
	public void testFaultPddArricchitoFAULTCNIPA_FaultGeneratoTramiteServizioWEBReale(String servizioApplicativoFruitore) throws Exception{
		super._testFaultPddArricchitoFAULTCNIPA_FaultGeneratoTramiteServizioWEBReale(servizioApplicativoFruitore);
	}
	
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".SOAP_FAULT_PDD_3"},dataProvider="personalizzazioniFaultApplicativiPdD")
	public void testFaultPddArricchitoFAULTCNIPA_FaultGeneratoSenzaDetails(String servizioApplicativoFruitore) throws Exception{
		super._testFaultPddArricchitoFAULTCNIPA_FaultGeneratoSenzaDetails(servizioApplicativoFruitore);
	}
	
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".SOAP_FAULT_PDD_4"},dataProvider="personalizzazioniFaultApplicativiPdD")
	public void testFaultPddArricchitoFAULTCNIPA_FaultGeneratoConPrefixErrato(String servizioApplicativoFruitore) throws Exception{
		super._testFaultPddArricchitoFAULTCNIPA_FaultGeneratoConPrefixErrato(servizioApplicativoFruitore);
	}

	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".SOAP_FAULT_PDD_5"},dataProvider="personalizzazioniFaultApplicativiPdD")
	public void testFaultPddArricchitoFAULTCNIPA_FaultGeneratoConSenzaPrefix(String servizioApplicativoFruitore) throws Exception{
		super._testFaultPddArricchitoFAULTCNIPA_FaultGeneratoConSenzaPrefix(servizioApplicativoFruitore);
	}

	
	
	
	/** SERVIZIO TUNNEL SOAP: SOAP FAULT PDD ARRICCHITO */
	
	@DataProvider (name="personalizzazioniFaultApplicativiPdDTunnelSOAP")
	public Object[][] personalizzazioniFaultApplicativiPdDTunnelSOAP(){
		return super._personalizzazioniFaultApplicativiPdDTunnelSOAP();
	}
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".SOAP_FAULT_PDD_TUNNEL_SOAP"},dataProvider="personalizzazioniFaultApplicativiPdDTunnelSOAP")
	public void testFaultPddArricchitoFAULTCNIPA_Default_TunnelSOAP(String servizioApplicativoFruitore) throws Exception{
		super._testFaultPddArricchitoFAULTCNIPA_Default_TunnelSOAP(servizioApplicativoFruitore);
	}

	
	
	
	/** ERRORE CONNETTORE PDD: connection refused ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreConnectionRefused")
	public Object[][] personalizzazioniErroreConnectionRefused(){
		return super._personalizzazioniErroreConnectionRefused();
	}
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".CONNECTION_REFUSED_PDD"},dataProvider="personalizzazioniErroreConnectionRefused")
	public void testServizioApplicativoConnectionRefused(String servizioApplicativoFruitore) throws Exception{
		super._testServizioApplicativoConnectionRefused(servizioApplicativoFruitore);
	}

	/** SERVIZIO TUNNEL SOAP: ERRORE CONNETTORE, connection refused ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreConnectionRefusedTunnelSOAP")
	public Object[][] personalizzazioniErroreConnectionRefusedTunnelSOAP(){
		return super._personalizzazioniErroreConnectionRefusedTunnelSOAP();
	}
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".CONNECTION_REFUSED_PDD_TUNNEL_SOAP"},dataProvider="personalizzazioniErroreConnectionRefusedTunnelSOAP")
	public void testServizioApplicativoConnectionRefused_TunnelSOAP(String servizioApplicativoFruitore) throws Exception{
		super._testServizioApplicativoConnectionRefused_TunnelSOAP(servizioApplicativoFruitore);
	}

	
	
	
	/** ERRORE CONNETTORE SA: connection refused ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreConnectionRefusedServizioApplicativo")
	public Object[][] personalizzazioniErroreConnectionRefusedServizioApplicativo(){
		return super._personalizzazioniErroreConnectionRefusedServizioApplicativo();
	}
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".CONNECTION_REFUSED_SA"},dataProvider="personalizzazioniErroreConnectionRefusedServizioApplicativo")
	public void testServizioApplicativoConnectionRefusedServizioApplicativo(String servizioApplicativoFruitore) throws Exception{
		super._testServizioApplicativoConnectionRefusedServizioApplicativo(servizioApplicativoFruitore);
	}

	/** SERVIZIO TUNNEL SOAP: ERRORE CONNETTORE SA, connection refused ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreConnectionRefusedServizioApplicativoTunnelSOAP")
	public Object[][] personalizzazioniErroreConnectionRefusedServizioApplicativoTunnelSOAP(){
		return super._personalizzazioniErroreConnectionRefusedServizioApplicativoTunnelSOAP();
	}
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".CONNECTION_REFUSED_SA_TUNNEL_SOAP"},
			dataProvider="personalizzazioniErroreConnectionRefusedServizioApplicativoTunnelSOAP")
	public void testServizioApplicativoConnectionRefusedServizioApplicativo_TunnelSOAP(String servizioApplicativoFruitore) throws Exception{
		super._testServizioApplicativoConnectionRefusedServizioApplicativo_TunnelSOAP(servizioApplicativoFruitore);
	}

	
	
	
	/** ERRORE CONNETTORE PDD: Connect Timed Out ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreConnectTimedOut")
	public Object[][] personalizzazioniErroreConnectTimedOut(){
		return super._personalizzazioniErroreTimedOut();
	}
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".CONNECT_TIMED_OUT_PDD"},dataProvider="personalizzazioniErroreConnectTimedOut")
	public void testServizioApplicativoConnectTimedOut(String servizioApplicativoFruitore) throws Exception{
		super._testServizioApplicativoTimedOut(servizioApplicativoFruitore, false);
	}
	
	/** SERVIZIO TUNNEL SOAP: ERRORE CONNETTORE, Connect Timed Out ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreConnectTimedOutTunnelSOAP")
	public Object[][] personalizzazioniErroreConnectTimedOutTunnelSOAP(){
		return super._personalizzazioniErroreTimedOutTunnelSOAP();
	}
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".CONNECT_TIMED_OUT_PDD_TUNNEL_SOAP"},dataProvider="personalizzazioniErroreConnectTimedOutTunnelSOAP")
	public void testServizioApplicativoConnectTimedOut_TunnelSOAP(String servizioApplicativoFruitore) throws Exception{
		super._testServizioApplicativoTimedOut_TunnelSOAP(servizioApplicativoFruitore, false);
	}

	
	
	
	/** ERRORE CONNETTORE SA: Connect Timed Out ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreConnectTimedOutServizioApplicativo")
	public Object[][] personalizzazioniErroreConnectTimedOutServizioApplicativo(){
		return super._personalizzazioniErroreTimedOutServizioApplicativo();
	}
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".CONNECT_TIMED_OUT_SA"},dataProvider="personalizzazioniErroreConnectTimedOutServizioApplicativo")
	public void testServizioApplicativoConnectTimedOutServizioApplicativo(String servizioApplicativoFruitore) throws Exception{
		super._testServizioApplicativoTimedOutServizioApplicativo(servizioApplicativoFruitore, false);
	}

	/** SERVIZIO TUNNEL SOAP: ERRORE CONNETTORE SA, Connect Timed Out ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreConnectTimedOutServizioApplicativoTunnelSOAP")
	public Object[][] personalizzazioniErroreConnectTimedOutServizioApplicativoTunnelSOAP(){
		return super._personalizzazioniErroreTimedOutServizioApplicativoTunnelSOAP();
	}
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".CONNECT_TIMED_OUT_SA_TUNNEL_SOAP"},
			dataProvider="personalizzazioniErroreConnectTimedOutServizioApplicativoTunnelSOAP")
	public void testServizioApplicativoConnectTimedOutServizioApplicativo_TunnelSOAP(String servizioApplicativoFruitore) throws Exception{
		super._testServizioApplicativoTimedOutServizioApplicativo_TunnelSOAP(servizioApplicativoFruitore, false);		
	}

	
	
	
	
	/** ERRORE CONNETTORE PDD: Read Timed Out ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreReadTimedOut")
	public Object[][] personalizzazioniErroreReadTimedOut(){
		return super._personalizzazioniErroreTimedOut();
	}
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".READ_TIMED_OUT_PDD"},dataProvider="personalizzazioniErroreReadTimedOut")
	public void testServizioApplicativoReadTimedOut(String servizioApplicativoFruitore) throws Exception{
		super._testServizioApplicativoTimedOut(servizioApplicativoFruitore, true);
	}
	
	/** SERVIZIO TUNNEL SOAP: ERRORE CONNETTORE, Read Timed Out ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreReadTimedOutTunnelSOAP")
	public Object[][] personalizzazioniErroreReadTimedOutTunnelSOAP(){
		return super._personalizzazioniErroreTimedOutTunnelSOAP();
	}
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".READ_TIMED_OUT_PDD_TUNNEL_SOAP"},dataProvider="personalizzazioniErroreReadTimedOutTunnelSOAP")
	public void testServizioApplicativoReadTimedOut_TunnelSOAP(String servizioApplicativoFruitore) throws Exception{
		super._testServizioApplicativoTimedOut_TunnelSOAP(servizioApplicativoFruitore, true);
	}

	
	
	
	/** ERRORE CONNETTORE SA: Read Timed Out ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreReadTimedOutServizioApplicativo")
	public Object[][] personalizzazioniErroreReadTimedOutServizioApplicativo(){
		return super._personalizzazioniErroreTimedOutServizioApplicativo();
	}
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".READ_TIMED_OUT_SA"},dataProvider="personalizzazioniErroreReadTimedOutServizioApplicativo")
	public void testServizioApplicativoReadTimedOutServizioApplicativo(String servizioApplicativoFruitore) throws Exception{
		super._testServizioApplicativoTimedOutServizioApplicativo(servizioApplicativoFruitore, true);
	}

	/** SERVIZIO TUNNEL SOAP: ERRORE CONNETTORE SA, Read Timed Out ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreReadTimedOutServizioApplicativoTunnelSOAP")
	public Object[][] personalizzazioniErroreReadTimedOutServizioApplicativoTunnelSOAP(){
		return super._personalizzazioniErroreTimedOutServizioApplicativoTunnelSOAP();
	}
	@Test(groups={CostantiErrori.ID_GRUPPO_ERRORI,ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".READ_TIMED_OUT_SA_TUNNEL_SOAP"},
			dataProvider="personalizzazioniErroreReadTimedOutServizioApplicativoTunnelSOAP")
	public void testServizioApplicativoReadTimedOutServizioApplicativo_TunnelSOAP(String servizioApplicativoFruitore) throws Exception{
		super._testServizioApplicativoTimedOutServizioApplicativo_TunnelSOAP(servizioApplicativoFruitore, true);		
	}
}
