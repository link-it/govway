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

package org.openspcoop2.protocol.trasparente.testsuite.units.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.services.connector.RicezioneBusteConnector;
import org.openspcoop2.pdd.services.connector.RicezioneContenutiApplicativiConnector;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.constants.MessaggiFaultErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.trasparente.testsuite.core.CooperazioneTrasparenteBase;
import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.trasparente.testsuite.core.Utilities;
import org.openspcoop2.protocol.utils.ErroriProperties;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.testsuite.db.DatiServizio;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.testsuite.units.utils.ErroreApplicativoUtilities;
import org.openspcoop2.testsuite.units.utils.ExceptionCodeExpected;
import org.openspcoop2.testsuite.units.utils.OpenSPCoopDetailsUtilities;
import org.openspcoop2.testsuite.units.utils.ProblemUtilities;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.testng.Assert;
import org.testng.Reporter;

/**
 * AuthUtilities
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AuthUtilities {

	/** Gestore della Collaborazione di Base */
	private static CooperazioneBaseInformazioni infoPortaDelegata = CooperazioneTrasparenteBase.getCooperazioneBaseInformazioni(CostantiTestSuite.PROXY_SOGGETTO_FRUITORE,
			CostantiTestSuite.PROXY_SOGGETTO_EROGATORE_ESTERNO,
				false,CostantiTestSuite.PROXY_PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
//	private static CooperazioneBaseInformazioni infoPortaApplicativa = CooperazioneTrasparenteBase.getCooperazioneBaseInformazioni(CostantiTestSuite.PROXY_SOGGETTO_FRUITORE,
//			CostantiTestSuite.PROXY_SOGGETTO_EROGATORE,
//				false,CostantiTestSuite.PROXY_PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	
	
	public static void testPortaDelegata(String nomePorta,
			CredenzialiInvocazione credenzialiInvocazione, 
			boolean addIDUnivoco,
			boolean genericCode, IntegrationFunctionError integrationFunctionError, String erroreAtteso, CodiceErroreIntegrazione codiceErrore, boolean ricercaEsatta, Date dataInizioTest, int returnCodeAtteso) throws Exception{
		testPortaDelegata(nomePorta,
				credenzialiInvocazione, null, null,
				addIDUnivoco,
				genericCode, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, dataInizioTest, returnCodeAtteso,
				null);
	}
	public static void testPortaDelegata(String nomePorta,
			CredenzialiInvocazione credenzialiInvocazione, String generateCredenzialiInvocazioneAsHeader, String generateCredenzialiInvocazioneAsQuery,  
			boolean addIDUnivoco,
			boolean genericCode, IntegrationFunctionError integrationFunctionError, String erroreAtteso, CodiceErroreIntegrazione codiceErrore, boolean ricercaEsatta, Date dataInizioTest, int returnCodeAtteso) throws Exception{
		testPortaDelegata(nomePorta,
				credenzialiInvocazione, generateCredenzialiInvocazioneAsHeader, generateCredenzialiInvocazioneAsQuery,
				addIDUnivoco,
				genericCode, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, dataInizioTest, returnCodeAtteso,
				null);
	}
	
	public static void testPortaDelegata(String nomePorta,
			CredenzialiInvocazione credenzialiInvocazione, 
			boolean addIDUnivoco,
			boolean genericCode, IntegrationFunctionError integrationFunctionError, String erroreAtteso, CodiceErroreIntegrazione codiceErrore, boolean ricercaEsatta, Date dataInizioTest, int returnCodeAtteso,
			Integer readTimeout) throws Exception{
		test(true, nomePorta, 
				credenzialiInvocazione, null, null,
				addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, codiceErrore, null, ricercaEsatta, dataInizioTest, 
				CostantiTestSuite.PROXY_SOGGETTO_FRUITORE, CostantiTestSuite.PROXY_SOGGETTO_EROGATORE_ESTERNO,
				returnCodeAtteso, false,
				readTimeout);
	}
	public static void testPortaDelegata(String nomePorta,
			CredenzialiInvocazione credenzialiInvocazione, String generateCredenzialiInvocazioneAsHeader, String generateCredenzialiInvocazioneAsQuery, 
			boolean addIDUnivoco,
			boolean genericCode, IntegrationFunctionError integrationFunctionError, String erroreAtteso, CodiceErroreIntegrazione codiceErrore, boolean ricercaEsatta, Date dataInizioTest, int returnCodeAtteso,
			Integer readTimeout) throws Exception{
		test(true, nomePorta, 
				credenzialiInvocazione, generateCredenzialiInvocazioneAsHeader, generateCredenzialiInvocazioneAsQuery,
				addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, codiceErrore, null, ricercaEsatta, dataInizioTest, 
				CostantiTestSuite.PROXY_SOGGETTO_FRUITORE, CostantiTestSuite.PROXY_SOGGETTO_EROGATORE_ESTERNO,
				returnCodeAtteso, false,
				readTimeout);
	}
	
	public static void testPortaDelegata(String nomePorta,
			CredenzialiInvocazione credenzialiInvocazione, 
			boolean addIDUnivoco,
			boolean genericCode, IntegrationFunctionError integrationFunctionError, String erroreAtteso, CodiceErroreIntegrazione codiceErrore, boolean ricercaEsatta, Date dataInizioTest, int returnCodeAtteso, boolean checkOpenSPCoopDetail) throws Exception{
		testPortaDelegata(nomePorta,
				credenzialiInvocazione, null, null,
				addIDUnivoco,
				genericCode, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, dataInizioTest, returnCodeAtteso, checkOpenSPCoopDetail,
				null);
	}
	public static void testPortaDelegata(String nomePorta,
			CredenzialiInvocazione credenzialiInvocazione, String generateCredenzialiInvocazioneAsHeader, String generateCredenzialiInvocazioneAsQuery, 
			boolean addIDUnivoco,
			boolean genericCode, IntegrationFunctionError integrationFunctionError, String erroreAtteso, CodiceErroreIntegrazione codiceErrore, boolean ricercaEsatta, Date dataInizioTest, int returnCodeAtteso, boolean checkOpenSPCoopDetail) throws Exception{
		testPortaDelegata(nomePorta,
				credenzialiInvocazione, generateCredenzialiInvocazioneAsHeader, generateCredenzialiInvocazioneAsQuery, 
				addIDUnivoco,
				genericCode, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, dataInizioTest, returnCodeAtteso, checkOpenSPCoopDetail,
				null);
	}
	
	public static void testPortaDelegata(String nomePorta,
			CredenzialiInvocazione credenzialiInvocazione, 
			boolean addIDUnivoco,
			boolean genericCode, IntegrationFunctionError integrationFunctionError, String erroreAtteso, CodiceErroreIntegrazione codiceErrore, boolean ricercaEsatta, Date dataInizioTest, int returnCodeAtteso, boolean checkOpenSPCoopDetail,
			Integer readTimeout) throws Exception{
		test(true, nomePorta, 
				credenzialiInvocazione, null, null, 
				addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, codiceErrore, null, ricercaEsatta, dataInizioTest, 
				CostantiTestSuite.PROXY_SOGGETTO_FRUITORE, CostantiTestSuite.PROXY_SOGGETTO_EROGATORE_ESTERNO,
				returnCodeAtteso, checkOpenSPCoopDetail,
				readTimeout);
	}
	public static void testPortaDelegata(String nomePorta,
			CredenzialiInvocazione credenzialiInvocazione,  String generateCredenzialiInvocazioneAsHeader, String generateCredenzialiInvocazioneAsQuery, 
			boolean addIDUnivoco,
			boolean genericCode, IntegrationFunctionError integrationFunctionError, String erroreAtteso, CodiceErroreIntegrazione codiceErrore, boolean ricercaEsatta, Date dataInizioTest, int returnCodeAtteso, boolean checkOpenSPCoopDetail,
			Integer readTimeout) throws Exception{
		test(true, nomePorta, 
				credenzialiInvocazione, generateCredenzialiInvocazioneAsHeader, generateCredenzialiInvocazioneAsQuery, 
				addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, codiceErrore, null, ricercaEsatta, dataInizioTest, 
				CostantiTestSuite.PROXY_SOGGETTO_FRUITORE, CostantiTestSuite.PROXY_SOGGETTO_EROGATORE_ESTERNO,
				returnCodeAtteso, checkOpenSPCoopDetail,
				readTimeout);
	}
	
	public static void testPortaApplicativa(String nomePorta, IDSoggetto soggettoFruitore,
			CredenzialiInvocazione credenzialiInvocazione, 
			boolean addIDUnivoco,
			boolean genericCode, IntegrationFunctionError integrationFunctionError, String erroreAtteso, CodiceErroreCooperazione codiceErrore, boolean ricercaEsatta, Date dataInizioTest, int returnCodeAtteso) throws Exception{
		testPortaApplicativa(nomePorta, soggettoFruitore,
				credenzialiInvocazione, null, null,
				addIDUnivoco,
				genericCode, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, dataInizioTest, returnCodeAtteso,
				null);
	}
	public static void testPortaApplicativa(String nomePorta, IDSoggetto soggettoFruitore,
			CredenzialiInvocazione credenzialiInvocazione,  String generateCredenzialiInvocazioneAsHeader, String generateCredenzialiInvocazioneAsQuery,
			boolean addIDUnivoco,
			boolean genericCode, IntegrationFunctionError integrationFunctionError, String erroreAtteso, CodiceErroreCooperazione codiceErrore, boolean ricercaEsatta, Date dataInizioTest, int returnCodeAtteso) throws Exception{
		testPortaApplicativa(nomePorta, soggettoFruitore,
				credenzialiInvocazione, generateCredenzialiInvocazioneAsHeader, generateCredenzialiInvocazioneAsQuery,
				addIDUnivoco,
				genericCode, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, dataInizioTest, returnCodeAtteso,
				null);
	}
	
	public static void testPortaApplicativa(String nomePorta, IDSoggetto soggettoFruitore,
			CredenzialiInvocazione credenzialiInvocazione, 
			boolean addIDUnivoco,
			boolean genericCode, IntegrationFunctionError integrationFunctionError, String erroreAtteso, CodiceErroreCooperazione codiceErrore, boolean ricercaEsatta, Date dataInizioTest, int returnCodeAtteso,
			Integer readTimeout) throws Exception{
		test(false, nomePorta, 
				credenzialiInvocazione, null, null,
				addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, null, codiceErrore, ricercaEsatta, dataInizioTest, 
				soggettoFruitore, CostantiTestSuite.PROXY_SOGGETTO_EROGATORE,
				returnCodeAtteso, false,
				readTimeout);
	}
	public static void testPortaApplicativa(String nomePorta, IDSoggetto soggettoFruitore,
			CredenzialiInvocazione credenzialiInvocazione,  String generateCredenzialiInvocazioneAsHeader, String generateCredenzialiInvocazioneAsQuery,
			boolean addIDUnivoco,
			boolean genericCode, IntegrationFunctionError integrationFunctionError, String erroreAtteso, CodiceErroreCooperazione codiceErrore, boolean ricercaEsatta, Date dataInizioTest, int returnCodeAtteso,
			Integer readTimeout) throws Exception{
		test(false, nomePorta, 
				credenzialiInvocazione, generateCredenzialiInvocazioneAsHeader, generateCredenzialiInvocazioneAsQuery,
				addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, null, codiceErrore, ricercaEsatta, dataInizioTest, 
				soggettoFruitore, CostantiTestSuite.PROXY_SOGGETTO_EROGATORE,
				returnCodeAtteso, false,
				readTimeout);
	}
	
	public static void testPortaApplicativa(String nomePorta, IDSoggetto soggettoFruitore,
			CredenzialiInvocazione credenzialiInvocazione, 
			boolean addIDUnivoco,
			boolean genericCode, IntegrationFunctionError integrationFunctionError, String erroreAtteso, CodiceErroreCooperazione codiceErrore, boolean ricercaEsatta, Date dataInizioTest, int returnCodeAtteso, boolean checkOpenSPCoopDetail) throws Exception{
		testPortaApplicativa(nomePorta, soggettoFruitore,
				credenzialiInvocazione, null, null, 
				addIDUnivoco,
				genericCode, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, dataInizioTest, returnCodeAtteso, checkOpenSPCoopDetail,
				null);
	}
	public static void testPortaApplicativa(String nomePorta, IDSoggetto soggettoFruitore,
			CredenzialiInvocazione credenzialiInvocazione, String generateCredenzialiInvocazioneAsHeader, String generateCredenzialiInvocazioneAsQuery,
			boolean addIDUnivoco,
			boolean genericCode, IntegrationFunctionError integrationFunctionError, String erroreAtteso, CodiceErroreCooperazione codiceErrore, boolean ricercaEsatta, Date dataInizioTest, int returnCodeAtteso, boolean checkOpenSPCoopDetail) throws Exception{
		testPortaApplicativa(nomePorta, soggettoFruitore,
				credenzialiInvocazione, generateCredenzialiInvocazioneAsHeader, generateCredenzialiInvocazioneAsQuery, 
				addIDUnivoco,
				genericCode, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, dataInizioTest, returnCodeAtteso, checkOpenSPCoopDetail,
				null);
	}
	
	public static void testPortaApplicativa(String nomePorta, IDSoggetto soggettoFruitore,
			CredenzialiInvocazione credenzialiInvocazione, 
			boolean addIDUnivoco,
			boolean genericCode, IntegrationFunctionError integrationFunctionError, String erroreAtteso, CodiceErroreCooperazione codiceErrore, boolean ricercaEsatta, Date dataInizioTest, int returnCodeAtteso, boolean checkOpenSPCoopDetail,
			Integer readTimeout) throws Exception{
		test(false, nomePorta, 
				credenzialiInvocazione, null, null, 
				addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, null, codiceErrore, ricercaEsatta, dataInizioTest, 
				soggettoFruitore, CostantiTestSuite.PROXY_SOGGETTO_EROGATORE,
				returnCodeAtteso, checkOpenSPCoopDetail,
				readTimeout);
	}
	public static void testPortaApplicativa(String nomePorta, IDSoggetto soggettoFruitore,
			CredenzialiInvocazione credenzialiInvocazione, String generateCredenzialiInvocazioneAsHeader, String generateCredenzialiInvocazioneAsQuery,
			boolean addIDUnivoco,
			boolean genericCode, IntegrationFunctionError integrationFunctionError, String erroreAtteso, CodiceErroreCooperazione codiceErrore, boolean ricercaEsatta, Date dataInizioTest, int returnCodeAtteso, boolean checkOpenSPCoopDetail,
			Integer readTimeout) throws Exception{
		test(false, nomePorta, 
				credenzialiInvocazione, generateCredenzialiInvocazioneAsHeader, generateCredenzialiInvocazioneAsQuery, 
				addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, null, codiceErrore, ricercaEsatta, dataInizioTest, 
				soggettoFruitore, CostantiTestSuite.PROXY_SOGGETTO_EROGATORE,
				returnCodeAtteso, checkOpenSPCoopDetail,
				readTimeout);
	}
	
	private static void test(boolean portaDelegata, String nomePorta,
			CredenzialiInvocazione credenzialiInvocazione, String generateCredenzialiInvocazioneAsHeader, String generateCredenzialiInvocazioneAsQuery,
			boolean addIDUnivoco,
			boolean genericCode, IntegrationFunctionError integrationFunctionError, String erroreAttesoParam, CodiceErroreIntegrazione codiceErroreIntegrazione, CodiceErroreCooperazione codiceErroreCooperazone, boolean ricercaEsatta, Date dataInizioTest,
			IDSoggetto fruitore,IDSoggetto erogatore, int returnCodeAtteso, 
			boolean checkOpenSPCoopDetail, // presente solamente in caso di errore di processamento
			Integer readTimeout) throws Exception{
		
		ExceptionCodeExpected exceptionCodeExpected = null;
		String erroreAtteso = erroreAttesoParam;
		if(integrationFunctionError!=null) {
		
			ErroriProperties erroriProperties = ErroriProperties.getInstance(LoggerWrapperFactory.getLogger(AuthUtilities.class));
						
			String codiceErrore = null;
			int codiceErroreSpecificoNumerico = -1;
			if(portaDelegata) {
				codiceErrore = Utilities.toString(codiceErroreIntegrazione);
				codiceErroreSpecificoNumerico = codiceErroreIntegrazione.getCodice();
			}
			else {
				if(codiceErroreIntegrazione!=null) {
					codiceErrore = Utilities.toString(codiceErroreIntegrazione);
					codiceErroreSpecificoNumerico = codiceErroreIntegrazione.getCodice();
				}
				else {
					codiceErrore = Utilities.toString(codiceErroreCooperazone);
					codiceErroreSpecificoNumerico = codiceErroreCooperazone.getCodice();
				}
			}
			
			exceptionCodeExpected = new ExceptionCodeExpected();
			exceptionCodeExpected.setGenericCode(genericCode);
			exceptionCodeExpected.setIntegrationFunctionError(integrationFunctionError);
			exceptionCodeExpected.setProtocolException(codiceErroreCooperazone!=null);
			if(genericCode) {
				exceptionCodeExpected.setCodiceErrore(erroriProperties.getErrorType_noWrap(integrationFunctionError));
			}else {
				exceptionCodeExpected.setCodiceErrore(codiceErrore);
			}
			exceptionCodeExpected.setCodiceErroreSpecifico(codiceErrore);
			exceptionCodeExpected.setCodiceErroreSpecificoNumerico(codiceErroreSpecificoNumerico);
			
			if(genericCode) {
				if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
					erroreAtteso = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
				}
			}
		}
		
		
		_test(portaDelegata, nomePorta,
				credenzialiInvocazione, generateCredenzialiInvocazioneAsHeader, generateCredenzialiInvocazioneAsQuery, 
				addIDUnivoco, 
				erroreAtteso, exceptionCodeExpected, ricercaEsatta, dataInizioTest, 
				fruitore, erogatore, returnCodeAtteso, 
				checkOpenSPCoopDetail, 
				readTimeout);
			
	}
	private static void _test(boolean portaDelegata, String nomePorta,
			CredenzialiInvocazione credenzialiInvocazione, String generateCredenzialiInvocazioneAsHeader, String generateCredenzialiInvocazioneAsQuery,
			boolean addIDUnivoco,
			String erroreAtteso, ExceptionCodeExpected exceptionCodeExpected, boolean ricercaEsatta, Date dataInizioTest,
			IDSoggetto fruitore,IDSoggetto erogatore, int returnCodeAtteso, 
			boolean checkOpenSPCoopDetail, // presente solamente in caso di errore di processamento
			Integer readTimeout) throws Exception{
		
		java.io.FileInputStream fin = null;
		Repository repository=new Repository();
		int stato = -1;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			// Contesto SSL
			java.util.Hashtable<String, String> sslContext = null;
			if(credenzialiInvocazione!=null && credenzialiInvocazione.isCreateSSLContext()){
				Reporter.log("Creo contesto SSL");
				sslContext = new Hashtable<String, String>();
				if(credenzialiInvocazione.getPathKeystore()!=null){
					sslContext.put("trustStoreLocation", credenzialiInvocazione.getPathKeystore());
					sslContext.put("keyStoreLocation", credenzialiInvocazione.getPathKeystore());
				}
				else {
					throw new Exception("Keystore path undefined");
				}
				if(credenzialiInvocazione.getPasswordKeystore()!=null){
					sslContext.put("trustStorePassword", credenzialiInvocazione.getPasswordKeystore());
					sslContext.put("keyStorePassword", credenzialiInvocazione.getPasswordKeystore());
				}
				else {
					throw new Exception("Keystore password undefined");
				}
				if(credenzialiInvocazione.getPasswordKey()!=null){
					sslContext.put("keyPassword", credenzialiInvocazione.getPasswordKey());
				}
				else {
					throw new Exception("Key password undefined");
				}
				sslContext.put("hostnameVerifier", "false");
			}
	
			ClientHttpGenerico client=new ClientHttpGenerico(repository,sslContext);
			if(readTimeout!=null) {
				client.setConnectionReadTimeout(readTimeout);
			}
			client.setSoapAction("\"TEST\"");
			if(portaDelegata) {
				if(credenzialiInvocazione!=null && TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione()) &&
						generateCredenzialiInvocazioneAsHeader==null && generateCredenzialiInvocazioneAsQuery==null) {
					//System.out.println("Location ["+Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore_openspcoop2Sec()+"]");
					client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore_openspcoop2Sec());
				}
				else if(sslContext!=null){
					//System.out.println("Location ["+Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore_httpsConAutenticazioneClient()+"]");
					client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore_httpsConAutenticazioneClient());
				}else{
					//System.out.println("NoLocation ["+Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore()+"]");
					client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
				}
			}
			else {
				if(credenzialiInvocazione!=null && TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())&&
						generateCredenzialiInvocazioneAsHeader==null && generateCredenzialiInvocazioneAsQuery==null) {
					//System.out.println("Location ["+Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore_openspcoop2Sec()+"]");
					client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore_openspcoop2Sec());
				}
				else if(sslContext!=null){
					//System.out.println("Location ["+Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore_httpsConAutenticazioneClient()+"]");
					client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore_httpsConAutenticazioneClient());
				}else{
					//System.out.println("NoLocation ["+Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore()+"]");
					client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
				}
			}
			client.setPortaDelegata(nomePorta);
			if(generateCredenzialiInvocazioneAsQuery!=null) {
				String newUrl = nomePorta;
				if(newUrl.contains("?")) {
					newUrl = newUrl + "&";
				}
				else {
					newUrl = newUrl + "?";
				}
				newUrl = newUrl + generateCredenzialiInvocazioneAsQuery;
				newUrl = newUrl + "=";
				newUrl = newUrl + credenzialiInvocazione.getUsername();
				client.setPortaDelegata(newUrl);
			}
			client.connectToSoapEngine();
			if(generateCredenzialiInvocazioneAsHeader!=null) {
				if(credenzialiInvocazione.getUsername()!=null) {
					client.setProperty(generateCredenzialiInvocazioneAsHeader, credenzialiInvocazione.getUsername());
				}
			}
			else {
				if(credenzialiInvocazione.getUsername()!=null) {
					client.setUsername(credenzialiInvocazione.getUsername());
				}
				if(credenzialiInvocazione.getPassword()!=null) {
					client.setPassword(credenzialiInvocazione.getPassword());
				}
			}
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			client.setAttesaTerminazioneMessaggi(false);
			try {
				Reporter.log("Invoco...");
				client.run();
				stato = client.getCodiceStatoHTTP();
				if(erroreAtteso!=null) {
					if(stato==200 && returnCodeAtteso!=200) {
						throw new Exception("Atteso errore, ritornato ["+stato+"]");
					}
				}
				if(stato!=200) {
					// ho un codice differente da 200 ok e non è stato generato un SOAPFault
					// Si tratta di una terminazione http effettuata dal container, non ha senso effettuare ulteriori controlli
					Reporter.log("ReturnCode ritornato["+stato+"] atteso["+returnCodeAtteso+"]");
					Assert.assertTrue(stato==returnCodeAtteso);
					System.out.println("Richiesta bloccata dal container: "+stato);
					return;
				}
				Reporter.log("Invocazione terminata");
			} catch (AxisFault error) {
				stato = client.getCodiceStatoHTTP();
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				
				TipoPdD tipoPdD = null;
				String modulo = null;
				
				if(portaDelegata) {
					
					tipoPdD = TipoPdD.DELEGATA;
					modulo = RicezioneContenutiApplicativiConnector.ID_MODULO;
					Reporter.log("Modulo ["+modulo+"]");
					
					Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"] found["+error.getFaultActor()+"]");
					Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
					
					String codiceErrore = null;
					if(exceptionCodeExpected.isGenericCode()) {
						codiceErrore = (exceptionCodeExpected.getIntegrationFunctionError().isClientError() ? 
								org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT : org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER) +
								org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+exceptionCodeExpected.getCodiceErrore();
					}
					else {
						codiceErrore = exceptionCodeExpected.getCodiceErrore();
					}
					
					Reporter.log("Controllo fault code ["+codiceErrore+"], found["+error.getFaultCode().getLocalPart().trim()+"]");
					Assert.assertTrue(codiceErrore.equals(error.getFaultCode().getLocalPart()));
					
					Reporter.log("Controllo fault string ["+erroreAtteso+"], found["+error.getFaultString()+"]");
					if(ricercaEsatta) {
						Assert.assertTrue(erroreAtteso.equals(error.getFaultString()));
					}
					else {
						Assert.assertTrue(error.getFaultString().contains(erroreAtteso));
					}
					
					boolean erroreApplicativo = ErroreApplicativoUtilities.existsErroreApplicativo(error);
					boolean problem = ProblemUtilities.existsProblem(error);
					
					Assert.assertTrue(erroreApplicativo || problem); // vengono generati in caso di 5XX
									
					if(erroreApplicativo) {
						ErroreApplicativoUtilities.verificaFaultErroreApplicativo(error, 
							fruitore,tipoPdD,modulo, 
							exceptionCodeExpected, erroreAtteso, ricercaEsatta);
					}
					else {
						ProblemUtilities.verificaProblem(error, 
								fruitore,tipoPdD,modulo, 
								exceptionCodeExpected, erroreAtteso, ricercaEsatta);
					}
					
					
				}
				else {
					
					tipoPdD = TipoPdD.APPLICATIVA;
					modulo = RicezioneBusteConnector.ID_MODULO;
					Reporter.log("Modulo ["+modulo+"]");
					
					boolean erroreApplicativo = ErroreApplicativoUtilities.existsErroreApplicativo(error);
					boolean problem = ProblemUtilities.existsProblem(error);
					Assert.assertTrue(erroreApplicativo || problem);  // vengono generati in caso di 5XX
					
					if(problem) {
						Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"] found["+error.getFaultActor()+"]");
						Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
							
						String codiceErrore = null;
						if(exceptionCodeExpected.isGenericCode()) {
							codiceErrore = (exceptionCodeExpected.getIntegrationFunctionError().isClientError() ? 
									org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT : org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER) +
									org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+exceptionCodeExpected.getCodiceErrore();
						}
						else {
							codiceErrore = exceptionCodeExpected.getCodiceErrore();
						}
						
						Reporter.log("Controllo fault code ["+codiceErrore+"], found["+error.getFaultCode().getLocalPart().trim()+"]");
						Assert.assertTrue(codiceErrore.equals(error.getFaultCode().getLocalPart()));
						
						Reporter.log("Controllo fault string ["+erroreAtteso+"], found["+error.getFaultString()+"]");
						if(ricercaEsatta) {
							Assert.assertTrue(erroreAtteso.equals(error.getFaultString()));
						}
						else {
							Assert.assertTrue(error.getFaultString().contains(erroreAtteso));
						}
					}
					else {
						Reporter.log("Controllo actor code is null,  found["+error.getFaultActor()+"]");
						Assert.assertTrue(error.getFaultActor()==null);
						
						Reporter.log("Controllo fault code [Client], found["+error.getFaultCode().getLocalPart().trim()+"]");
						Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
						
						Reporter.log("Controllo fault string ["+MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE.toString()+"], found["+error.getFaultString()+"]");
						Assert.assertTrue(MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE.toString().equals(error.getFaultString()));
					}
						
					if(erroreApplicativo) {
						ErroreApplicativoUtilities.verificaFaultErroreApplicativo(error, 
							erogatore,tipoPdD,
							"PortaApplicativa",//modulo, 
							exceptionCodeExpected, erroreAtteso, ricercaEsatta);
						
						
						if(checkOpenSPCoopDetail){
							
							// openspcoop detail
							
							Assert.assertTrue(OpenSPCoopDetailsUtilities.existsOpenSPCoopDetails(error)); // vengono generati in caso di 5XX
							
							List<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail> eccezioniOD = 
									new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>();
								org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail eccOD = new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail();
								eccOD.setCodice(exceptionCodeExpected.getCodiceErroreSpecifico());
								eccOD.setDescrizione(erroreAtteso);
								eccOD.setCheckDescrizioneTramiteMatchEsatto(ricercaEsatta);
								eccezioniOD.add(eccOD);
							
							OpenSPCoopDetailsUtilities.verificaFaultOpenSPCoopDetail(error, 
									erogatore,TipoPdD.APPLICATIVA,modulo, 
									eccezioniOD, null);
						}
					}
					else {
						ProblemUtilities.verificaProblem(error, 
								fruitore,tipoPdD,modulo, 
								exceptionCodeExpected, erroreAtteso, ricercaEsatta);
					}

				}	

			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
		}
		
		Reporter.log("ReturnCode ritornato["+stato+"] atteso["+returnCodeAtteso+"]");
		Assert.assertTrue(stato==returnCodeAtteso);

		DatabaseComponent data = null;
		String id = repository.getNext();
		Reporter.log("ID Messaggio["+id+"]");
		if(erroreAtteso==null) {
			Assert.assertTrue(id!=null);
		}
		boolean checkDB = (id!=null);
		if(portaDelegata) {
			checkDB = (stato==200);
		}
		if(checkDB) {
			if(org.openspcoop2.protocol.trasparente.testsuite.units.utils.CooperazioneTrasparenteBase.protocolloEmetteTracce) {
				
				String tipoServizio = CostantiTestSuite.SOAP_TIPO_SERVIZIO;
				String nomeServizio = CostantiTestSuite.SOAP_NOME_SERVIZIO_SINCRONO;
				String nomeAzione = CostantiTestSuite.PROXY_SERVIZIO_SINCRONO_AZIONE_AGGIORNAMENTO;
				if(CostantiTestSuite.PORTA_DELEGATA_AUTH_BASIC.equals(nomePorta) ||
						CostantiTestSuite.PORTA_APPLICATIVA_AUTH_BASIC.equals(nomePorta)) {
					nomeAzione = CostantiTestSuite.PROXY_SERVIZIO_SINCRONO_AZIONE_AGGIORNAMENTO_BASIC;
				}
				else if(CostantiTestSuite.PORTA_DELEGATA_AUTH_BASIC_FORWARD_AUTHORIZATION.equals(nomePorta) ||
						CostantiTestSuite.PORTA_APPLICATIVA_AUTH_BASIC_FORWARD_AUTHORIZATION.equals(nomePorta)) {
					nomeAzione = CostantiTestSuite.PROXY_SERVIZIO_SINCRONO_AZIONE_AGGIORNAMENTO_BASIC_FORWARD;
				}
				else if(CostantiTestSuite.PORTA_DELEGATA_AUTH_PRINCIPAL_HEADER_CLEAN.equals(nomePorta) ||
						CostantiTestSuite.PORTA_APPLICATIVA_AUTH_PRINCIPAL_HEADER_CLEAN.equals(nomePorta) ||
						CostantiTestSuite.PORTA_DELEGATA_AUTH_OPTIONAL_PRINCIPAL_HEADER.equals(nomePorta) ||
						CostantiTestSuite.PORTA_APPLICATIVA_AUTH_OPTIONAL_PRINCIPAL_HEADER.equals(nomePorta)) {
					nomeAzione = CostantiTestSuite.PROXY_SERVIZIO_SINCRONO_AZIONE_AGGIORNAMENTO_PRINCIPAL_HEADER_CLEAN;
				}
				else if(CostantiTestSuite.PORTA_DELEGATA_AUTH_PRINCIPAL_HEADER_NOT_CLEAN.equals(nomePorta) ||
						CostantiTestSuite.PORTA_APPLICATIVA_AUTH_PRINCIPAL_HEADER_NOT_CLEAN.equals(nomePorta)) {
					nomeAzione = CostantiTestSuite.PROXY_SERVIZIO_SINCRONO_AZIONE_AGGIORNAMENTO_PRINCIPAL_HEADER_NOT_CLEAN;
				}
				else if(CostantiTestSuite.PORTA_DELEGATA_AUTH_PRINCIPAL_QUERY_CLEAN.equals(nomePorta) ||
						CostantiTestSuite.PORTA_APPLICATIVA_AUTH_PRINCIPAL_QUERY_CLEAN.equals(nomePorta) ||
						CostantiTestSuite.PORTA_DELEGATA_AUTH_OPTIONAL_PRINCIPAL_QUERY.equals(nomePorta) ||
						CostantiTestSuite.PORTA_APPLICATIVA_AUTH_OPTIONAL_PRINCIPAL_QUERY.equals(nomePorta)) {
					nomeAzione = CostantiTestSuite.PROXY_SERVIZIO_SINCRONO_AZIONE_AGGIORNAMENTO_PRINCIPAL_QUERY_CLEAN;
				}
				else if(CostantiTestSuite.PORTA_DELEGATA_AUTH_PRINCIPAL_QUERY_NOT_CLEAN.equals(nomePorta) ||
						CostantiTestSuite.PORTA_APPLICATIVA_AUTH_PRINCIPAL_QUERY_NOT_CLEAN.equals(nomePorta)) {
					nomeAzione = CostantiTestSuite.PROXY_SERVIZIO_SINCRONO_AZIONE_AGGIORNAMENTO_PRINCIPAL_QUERY_NOT_CLEAN;
				}
				
				try{
					boolean checkServizioApplicativo = false;
					if(portaDelegata)
						data = DatabaseProperties.getDatabaseComponentFruitore();
					else
						data = DatabaseProperties.getDatabaseComponentErogatore();
					testSincrono(data, id,
							fruitore, erogatore,
							tipoServizio, nomeServizio, nomeAzione,
							false,CostantiTestSuite.PROXY_PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI,
							checkServizioApplicativo, null, null, null);
				}catch(Exception e){
					throw e;
				}finally{
					data.close();
				}
			}
		}
		

		DatabaseMsgDiagnosticiComponent dataMsg = null;
		if(erroreAtteso!=null) {
			try{
				if(portaDelegata)
					dataMsg = DatabaseProperties.getDatabaseComponentDiagnosticaFruitore();
				else
					dataMsg = DatabaseProperties.getDatabaseComponentDiagnosticaErogatore();
				if(id!=null){
					if(returnCodeAtteso!=200) {
						Assert.assertTrue(dataMsg.isTracedErrorMsg(id));
					}
					// il messaggio di errore eventuale viene comunque registrato con livello info, se siamo in un contesto opzionale o di warning
	    			if(ricercaEsatta){
	    				Assert.assertTrue(dataMsg.isTracedMessaggio(id, erroreAtteso));
	    			}
	    			else{
	    				if(returnCodeAtteso==200 && portaDelegata) {
	    					boolean withId = dataMsg.isTracedMessaggio(id, true, erroreAtteso);
	    					boolean withoutId = dataMsg.isTracedMessaggioWithLike(dataInizioTest, erroreAtteso);
	    					Assert.assertTrue(withId || withoutId);
	    				}
	    				else {
	    					Assert.assertTrue(dataMsg.isTracedMessaggio(id, true, erroreAtteso));
	    				}
	    			}
				}
				else{
					Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(dataInizioTest, erroreAtteso));
				}
	
			}catch(Exception eInternal){
				throw eInternal;
			}finally{
				dataMsg.close();
			}
		}
			
	}
	
	private static void testSincrono(DatabaseComponent data,String id,
			IDSoggetto mittente, IDSoggetto destinatario,
			String tipoServizio,String servizio,String azione,
			boolean confermaRicezione, String inoltro, Inoltro inoltroSdk,
			boolean checkServizioApplicativo,String collaborazione,String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk) throws Exception{
		Reporter.log("Controllo tracciamento richiesta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
		Reporter.log("Controllo valore Mittente Busta con id: " +id+ " atteso:["+mittente+"] ");
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, mittente, null));
		Reporter.log("Controllo valore Destinatario Busta con id: " +id+ " atteso:["+destinatario+"] ");
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, destinatario, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id+ " atteso:["+tipoTempoAtteso+"] sdk["+tipoTempoAttesoSdk+"]");
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
		}
		Reporter.log("Controllo valore Servizio ["+servizio+"] Busta con id: " +id);
		DatiServizio datiServizio = new DatiServizio(tipoServizio,servizio, infoPortaDelegata.getServizio_versioneDefault());
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
		if(azione!=null){
			Reporter.log("Controllo valore Azione Busta con id: " +id+ " atteso:["+azione+"] ");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, azione));
		}
		Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id+ " atteso:["+infoPortaDelegata.getProfiloCollaborazione_protocollo_sincrono()+"] SINCRONO");
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, infoPortaDelegata.getProfiloCollaborazione_protocollo_sincrono(), 
				ProfiloDiCollaborazione.SINCRONO));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(id, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, confermaRicezione,inoltro, inoltroSdk));
		Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("Controllo lista trasmissione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, mittente, null, destinatario, null, true,tipoTempoAtteso, tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, mittente, null, destinatario, null));
		}
		if(checkServizioApplicativo){
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
		}
		Reporter.log("----------------------------------------------------------");

		Reporter.log("Controllo tracciamento risposta con riferimento id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));
		Reporter.log("Controllo valore Mittente Busta della risposta con riferimento id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, destinatario, null));
		Reporter.log("Controllo valore Destinatario Busta della risposta con riferimento id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, mittente, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
		}
		Reporter.log("Controllo valore Servizio Busta della risposta con riferimento id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizio));
		if(azione!=null){
			Reporter.log("Controllo valore Azione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, azione));
		}
		Reporter.log("Controllo valore Profilo di Collaborazione Busta della risposta con riferimento id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, infoPortaDelegata.getProfiloCollaborazione_protocollo_sincrono(), ProfiloDiCollaborazione.SINCRONO));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCollaborazione(id, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, confermaRicezione,inoltro,inoltroSdk));
		Reporter.log("Controllo che la busta non abbia generato eccezioni, riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("Controllo lista trasmissione, riferimento messaggio: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, destinatario,null, mittente, null));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, destinatario,null, mittente, null, true,tipoTempoAtteso,tipoTempoAttesoSdk));
		}

	}
}
