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

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
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
 * AutenticazionePortaApplicativa
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AutenticazionePortaApplicativa {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "AutenticazionePortaApplicativa";
	
	private static boolean addIDUnivoco = true;

	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups=AutenticazionePortaApplicativa.ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	private Vector<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new Vector<ErroreAttesoOpenSPCoopLogCore>();
	@AfterGroups (alwaysRun=true , groups=AutenticazionePortaApplicativa.ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	
	
	
	
	@DataProvider(name="noneProvider")
	public Object[][] noneProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazioneDisabilitata()}, // nessuna credenziale
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic", "123456")}, 
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic2", "123456")}, 
				{CredenzialiInvocazione.getAutenticazioneBasic("credenzialeErrata", "credenzialeErrata")} // credenziali errate
		};
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO+".NONE"},dataProvider="noneProvider")
	public void testNone(CredenzialiInvocazione credenzialiInvocazione) throws Exception{
		// Qualsiasi siano le credenziali l'invocazione va a buon fine
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_NONE, null,
				credenzialiInvocazione, addIDUnivoco, 
				null, null, false, null,200);
	}
	
	
	
	
	
	
	
	
	
	
	
	@DataProvider(name="basicProvider")
	public Object[][] basicProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazioneDisabilitata(), null, 
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic", "123456"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_BASIC,
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic2", "123456"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_BASIC_2,
							null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneBasic("credenzialeErrata", "credenzialeErrata"), null,
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO.getCodice(), true, 500} // credenziali errate
		};
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO+".BASIC"},dataProvider="basicProvider")
	public void testBasic(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_BASIC, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO+".BASIC_OPTIONAL"},dataProvider="basicProvider")
	public void testBasicOptional(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_OPTIONAL_BASIC, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta,  DateManager.getDate(), 
				200);
	}
	
	
	
	
	
	
	
	
	@DataProvider(name="sslProvider")
	public Object[][] sslProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazioneDisabilitata(), null,  
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/openspcoop2/keys/client1_trasparente.jks", "openspcoopjks", "openspcoop"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_SSL,
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/openspcoop2/keys/client2_trasparente.jks", "openspcoopjks", "openspcoop"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_SSL_2,
							null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/openspcoop2/keys/client3_trasparente.jks", "openspcoopjks", "openspcoop"), null, 
								null,-1, true, 200}, // credenziali corrette (anche se non registrate sul registro)
		};
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO+".SSL"},dataProvider="sslProvider")
	public void testSsl(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_SSL, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO+".SSL_OPTIONAL"},dataProvider="sslProvider")
	public void testSslOptional(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_OPTIONAL_SSL, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta,  DateManager.getDate(), 
				200);
	}
	
	
	
	
	
	
	@DataProvider(name="principalProvider")
	public Object[][] principalProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazioneDisabilitata(), null, 
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL,
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL_2,
							null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparentePrincipal1", "Op3nSPC@@p2"), null, 
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale (non si passa tramite il container e il principal non viene valorizzato)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal3", "Op3nSPC@@p2"), null, 
						null,-1, true, 200}, // credenziali corrette (anche se non registrate sul registro)
				{CredenzialiInvocazione.getAutenticazionePrincipal("credenzialeErrata", "credenzialeErrata"), null, 
						null, -1,true, 401} // credenziali errate (non registrate nel container)
		};
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL"},dataProvider="principalProvider")
	public void testPrincipal(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_PRINCIPAL, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest,
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
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_OPTIONAL"},dataProvider="principalProvider")
	public void testPrincipalOptional(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		// Fatta eccezione per le credenziali non riconosciute dal container ssl
		int stato = 200;
		if(returnCodeAtteso==401) {
			stato = 401;
		}
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_OPTIONAL_PRINCIPAL, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),
				stato);
	}
	
}
