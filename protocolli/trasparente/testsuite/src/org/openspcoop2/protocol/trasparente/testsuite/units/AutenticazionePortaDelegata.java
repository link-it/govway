/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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

package org.openspcoop2.protocol.trasparente.testsuite.units;

import java.util.Date;
import java.util.Vector;

import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.AuthUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.CredenzialiInvocazione;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.utils.date.DateManager;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * AutenticazionePortaDelegata
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AutenticazionePortaDelegata {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "AutenticazionePortaDelegata";
	
	private static boolean addIDUnivoco = true;

	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups=AutenticazionePortaDelegata.ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	private Vector<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new Vector<ErroreAttesoOpenSPCoopLogCore>();
	@AfterGroups (alwaysRun=true , groups=AutenticazionePortaDelegata.ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	
	
	
	
	@DataProvider(name="basicProvider")
	public Object[][] basicProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazioneDisabilitata(), 
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic", "123456"), 
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic2", "123456"), 
							null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneBasic("credenzialeErrata", "credenzialeErrata"), 
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(), true, 500} // credenziali errate
		};
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".BASIC"},dataProvider="basicProvider")
	public void testBasic(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_BASIC, credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".BASIC_OPTIONAL"},dataProvider="basicProvider")
	public void testBasicOptional(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_OPTIONAL_BASIC, credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),  
				200);
	}
	
	
	
	
	
	
	
	@DataProvider(name="sslProvider")
	public Object[][] sslProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazioneDisabilitata(), 
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/client1_trasparente.jks", "openspcoopjks", "openspcoop"), 
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/client2_trasparente.jks", "openspcoopjks", "openspcoop"), 
							null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/client3_trasparente.jks", "openspcoopjks", "openspcoop"), 
								null,-1, true, 200}, // credenziali corrette (anche se non registrate sul registro)
		};
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".SSL"},dataProvider="sslProvider")
	public void testSsl(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_SSL, credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".SSL_OPTIONAL"},dataProvider="sslProvider")
	public void testSslOptional(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_OPTIONAL_SSL, credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),  
				200);
	}
	
	
	
	
	
	
	
	@DataProvider(name="principalProvider")
	public Object[][] principalProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazioneDisabilitata(), 
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
							null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparentePrincipal1", "Op3nSPC@@p2"), 
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale (non si passa tramite il container e il principal non viene valorizzato)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal3", "Op3nSPC@@p2"), 
					null,-1, true, 200}, // credenziali corrette (anche se non registrate sul registro)
				{CredenzialiInvocazione.getAutenticazionePrincipal("credenzialeErrata", "credenzialeErrata"), 
						null, -1,true, 401} // credenziali errate (non registrate nel container)
		};
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".PRINCIPAL"},dataProvider="principalProvider")
	public void testPrincipal(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_PRINCIPAL, credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest,
				returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".PRINCIPAL_OPTIONAL"},dataProvider="principalProvider")
	public void testPrincipalOptional(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo
		// Fatta eccezione per le credenziali non riconosciute dal container ssl
		int stato = 200;
		if(returnCodeAtteso==401) {
			stato = 401;
		}
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_OPTIONAL_PRINCIPAL, credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta,  DateManager.getDate(), 
				stato);
	}
	
}
