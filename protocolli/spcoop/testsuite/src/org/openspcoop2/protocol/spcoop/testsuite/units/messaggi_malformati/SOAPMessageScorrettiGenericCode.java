/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.spcoop.testsuite.units.messaggi_malformati;

import java.util.List;

import javax.xml.soap.SOAPException;

import org.openspcoop2.testsuite.core.TestSuiteException;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


/**
 * Test sui profili di collaborazione implementati nella Porta di Dominio
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SOAPMessageScorrettiGenericCode extends SOAPMessageScorretti {

	public SOAPMessageScorrettiGenericCode() {
		super(true);
	}

	/**
	 * NOTA:
	 * Usare i seguenti grupi:
	 * SOAPMessageScorretti.RICHIESTA_ALTRI_DATI
	 * SOAPMessageScorretti.PD_XML_RICHIESTA
	 * SOAPMessageScorretti.PA_XML_RICHIESTA
	 * SOAPMessageScorretti.PD_XML_RISPOSTA
	 * SOAPMessageScorretti.PA_XML_RISPOSTA
	 * SOAPMessageScorretti.INTEGRATION_MANAGER
	 * SOAPMessageScorretti.PD2SOAP
	 **/
	


	@BeforeGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		super._testOpenspcoopCoreLog_raccoltaTempoAvvioTest();
	} 	
	@AfterGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		super._testOpenspcoopCoreLog();
	} 



	// ------------- CONTENT TYPE NON SUPPORTATO ------------------

	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".RICHIESTA_ALTRI_DATI",
			SOAPMessageScorretti.ID_GRUPPO+".CONTENT_TYPE_NON_SUPPORTATO_PD"})
	public void contentTypeNonSupportato_PD()throws TestSuiteException,SOAPException, Exception{
		super._contentTypeNonSupportato_PD();
	}

	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".RICHIESTA_ALTRI_DATI",
			SOAPMessageScorretti.ID_GRUPPO+".CONTENT_TYPE_NON_SUPPORTATO_PA"})
	public void contentTypeNonSupportato_PA()throws TestSuiteException,SOAPException, Exception{
		super._contentTypeNonSupportato_PA();
	}

	
	
	
	// ------------- SOAP HEADER DON'T UNDERSTAND  ------------------

	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".RICHIESTA_ALTRI_DATI",
			SOAPMessageScorretti.ID_GRUPPO+".HEADER_NOT_UNDERSTAND_PD"})
	public void headerDontUnderstand_PD()throws TestSuiteException,SOAPException, Exception{
		super._headerDontUnderstand_PD();
	}

	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".RICHIESTA_ALTRI_DATI",
			SOAPMessageScorretti.ID_GRUPPO+".HEADER_NOT_UNDERSTAND_PA"})
	public void headerDontUnderstand_PA()throws TestSuiteException,SOAPException, Exception{
		super._headerDontUnderstand_PA();
	}

	
	
	
	// ------------- SOAP ENVELOPE NAMESPACE SCORRETTI ------------------

	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".RICHIESTA_ALTRI_DATI",
			SOAPMessageScorretti.ID_GRUPPO+".NAMESPACE_ERRATO_PD"})
	public void namespaceErrato_PD()throws TestSuiteException,SOAPException, Exception{
		super._namespaceErrato_PD();
	}

	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".RICHIESTA_ALTRI_DATI",
			SOAPMessageScorretti.ID_GRUPPO+".NAMESPACE_ERRATO_PA"})
	public void namespaceErrato_PA()throws TestSuiteException,SOAPException, Exception{
		super._namespaceErrato_PA();
	}

	
	
	
	// ------------- STRUTTURA XML ERRATA ------------------

	@DataProvider (name="strutturaSoapErrata")
	public Object[][] strutturaSoapErrata() throws Exception{
		return super._strutturaSoapErrata();
	}

	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaDelegata_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PortaDelegata_Richiesta(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi);
	}
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD2SOAP",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD2SOAP_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaDelegata_PD2SOAP_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PortaDelegata_PD2SOAP_Richiesta(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi);
	}
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_STATEFUL_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaDelegata_stateful_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PortaDelegata_stateful_Richiesta(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi);
	}
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_INTEGRAZIONE_CONTENT_BASED_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaDelegata_integrazioneContentBased_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PortaDelegata_integrazioneContentBased_Richiesta(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi);
	}
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaDelegata_correlazioneApplicativaContentBased_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PortaDelegata_correlazioneApplicativaContentBased_Richiesta(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi);
	}
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_VALIDAZIONE_CONTENUTI_WARN_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaDelegata_validazioneContenutiApplicativiWarningOnly_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PortaDelegata_validazioneContenutiApplicativiWarningOnly_Richiesta(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi);
	}
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_VALIDAZIONE_CONTENUTI_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaDelegata_validazioneContenutiApplicativi_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PortaDelegata_validazioneContenutiApplicativi_Richiesta(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi);
	}

	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_WSSECURITY_ENCRYPT_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaDelegata_WSSecurityEncrypt_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PortaDelegata_WSSecurityEncrypt_Richiesta(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi);
	}
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_WSSECURITY_SIGNATURE_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaDelegata_WSSecuritySignature_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PortaDelegata_WSSecuritySignature_Richiesta(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi);
	}

	
	
	
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PA_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaApplicativa_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PortaApplicativa_Richiesta(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi);
	}
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PA_STATEFUL_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaApplicativa_stateful_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PortaApplicativa_stateful_Richiesta(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi);
	}
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PA_CORRELAZIONE_APPLICATIVA_CONTENT_BASED_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaApplicativa_correlazioneApplicativaContentBased_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PortaApplicativa_correlazioneApplicativaContentBased_Richiesta(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi);
	}
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PA_VALIDAZIONE_CONTENUTI_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaApplicativa_validazioneContenutiApplicativi_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PortaApplicativa_validazioneContenutiApplicativi_Richiesta(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi);
	}

	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PA_WSSECURITY_ENCRYPT_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaApplicativa_WSSecurityEncrypt_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PortaApplicativa_WSSecurityEncrypt_Richiesta(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi);
	}
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PA_WSSECURITY_SIGNATURE_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaApplicativa_WSSecuritySignature_Richiesta(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PortaApplicativa_WSSecuritySignature_Richiesta(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi);
	}

	
	
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RICHIESTA",
			SOAPMessageScorretti.ID_GRUPPO+".BUSTA_ERRATA_PA_RICHIESTA"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_PortaApplicativa_Richiesta_bustaSintatticamenteErrata(byte[] messaggioXMLRichiesta,String identificativoTest,
			List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PortaApplicativa_Richiesta_bustaSintatticamenteErrata(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi);
	}

	
	
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_CASO1"})
	public void strutturaXMLErrata_PA_RispostaApplicativa_Body_wrap()throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PA_RispostaApplicativa_Body(false);
	}
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_CASO1"})
	public void strutturaXMLErrata_PA_RispostaApplicativa_Body_unwrap()throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PA_RispostaApplicativa_Body(true);
	}

	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_CASO2"})
	public void strutturaXMLErrata_PA_RispostaApplicativa_BodyFirstChild_wrap()throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PA_RispostaApplicativa_BodyFirstChild(false);
	}
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_CASO2"})
	public void strutturaXMLErrata_PA_RispostaApplicativa_BodyFirstChild_unwrap()throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PA_RispostaApplicativa_BodyFirstChild(true);
	}

	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_CASO3"})
	public void strutturaXMLErrata_PA_RispostaApplicativa_InsideBody_wrap()throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PA_RispostaApplicativa_InsideBody(false);
	}
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_CASO3"})
	public void strutturaXMLErrata_PA_RispostaApplicativa_InsideBody_unwrap()throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PA_RispostaApplicativa_InsideBody(true);
	}

	
	
	
	/**
	 * Messaggio applicativo di risposta ottenuto dal servizio applicativo malformato in modalita stateful
	 */
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_STATEFUL",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_STATEFUL_CASO1"})
	public void strutturaXMLErrata_PA_RispostaApplicativa_Stateful_Body_wrap()throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PA_RispostaApplicativa_Stateful_Body(false);
	}
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_STATEFUL",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_STATEFUL_CASO1"})
	public void strutturaXMLErrata_PA_RispostaApplicativa_Stateful_Body_unwrap()throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PA_RispostaApplicativa_Stateful_Body(true);
	}

	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_STATEFUL",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_STATEFUL_CASO2"})
	public void strutturaXMLErrata_PA_RispostaApplicativa_Stateful_BodyFirstChild_wrap()throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PA_RispostaApplicativa_Stateful_BodyFirstChild(false);
	}
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_STATEFUL",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_STATEFUL_CASO2"})
	public void strutturaXMLErrata_PA_RispostaApplicativa_Stateful_BodyFirstChild_unwrap()throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PA_RispostaApplicativa_Stateful_BodyFirstChild(true);
	}

	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_STATEFUL",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_STATEFUL_CASO3"})
	public void strutturaXMLErrata_PA_RispostaApplicativa_Stateful_InsideBody_wrap()throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PA_RispostaApplicativa_Stateful_InsideBody(false);
	}
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PA_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_STATEFUL",
			SOAPMessageScorretti.ID_GRUPPO+".RISPOSTA_APPLICATIVA_XML_ERRATO_PA_STATEFUL_CASO3"})
	public void strutturaXMLErrata_PA_RispostaApplicativa_Stateful_InsideBody_unwrap()throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_PA_RispostaApplicativa_Stateful_InsideBody(true);
	}

	
	
	
	/**
	 * Body errato ritornato dalla PdD Destinataria
	 */
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_RISPOSTA_PDD"})
	public void strutturaXMLBodyRispostaPdDErrato_PD_wrap()throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLBodyRispostaPdDErrato_PD(false);
	}
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_RISPOSTA_PDD"})
	public void strutturaXMLBodyRispostaPdDErrato_PD_unwrap()throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLBodyRispostaPdDErrato_PD(true);
	}

	/**
	 * Header eGov errato ritornato dalla PdD Destinataria
	 */
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_RISPOSTA_HEADER_PDD"})
	public void strutturaXMLHeaderRispostaPdDErrato_PD_wrap()throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLHeaderRispostaPdDErrato_PD(false);
	}
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_XML_ERRATA_PD_RISPOSTA_HEADER_PDD"})
	public void strutturaXMLHeaderRispostaPdDErrato_PD_unwrap()throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLHeaderRispostaPdDErrato_PD(true);
	}
	
	
	
	
	/**
	 * ContentType errato ritornato dalla PdD Destinataria
	 */
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_CONTENT_TYPE_ERRATO_PD_RISPOSTA_PDD"})
	public void strutturaContentTypeRispostaPdDErrato_PD_wrap()throws TestSuiteException,SOAPException, Exception{
		super._strutturaContentTypeRispostaPdDErrato_PD(false);
	}
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,
			SOAPMessageScorretti.ID_GRUPPO+".PD_XML_RISPOSTA",
			SOAPMessageScorretti.ID_GRUPPO+".STRUTTURA_CONTENT_TYPE_ERRATO_PD_RISPOSTA_PDD"})
	public void strutturaContentTypeRispostaPdDErrato_PD_unwrap()throws TestSuiteException,SOAPException, Exception{
		super._strutturaContentTypeRispostaPdDErrato_PD(true);
	}

	
	
	
	@Test(groups={SOAPMessageScorretti.ID_GRUPPO,SOAPMessageScorretti.ID_GRUPPO+".INTEGRATION_MANAGER"},dataProvider="strutturaSoapErrata")
	public void strutturaXMLErrata_integrationManager(byte[] messaggioXMLRichiesta,String identificativoTest,List<String> motivoErroreParser,List<String> listErroriAttesi)throws TestSuiteException,SOAPException, Exception{
		super._strutturaXMLErrata_integrationManager(messaggioXMLRichiesta, identificativoTest, motivoErroreParser, listErroriAttesi);
	}

}
