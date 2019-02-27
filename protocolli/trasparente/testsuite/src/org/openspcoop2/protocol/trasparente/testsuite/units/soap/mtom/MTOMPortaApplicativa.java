/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.protocol.trasparente.testsuite.units.soap.mtom;

import java.util.Date;
import java.util.Vector;

import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.core.Utilities;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.MTOMThread;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.MTOMUtilities;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.utils.date.DateManager;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

/**
 * MTOMPortaApplicativa
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MTOMPortaApplicativa {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "MTOMPortaApplicativa";
	
	private static boolean addIDUnivoco = true;

	
	private Date dataAvvioGruppoTest = null;
	private MTOMThread threadSoap11 = null;
	private MTOMThread threadSoap12 = null;
	@BeforeGroups (alwaysRun=true , groups=MTOMPortaApplicativa.ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
		
		this.threadSoap11 = new MTOMThread(Utilities.testSuiteProperties.getMtomSoap11Socket(), true, Integer.MAX_VALUE);
		this.threadSoap11.start();
		this.threadSoap12 = new MTOMThread(Utilities.testSuiteProperties.getMtomSoap12Socket(), false, Integer.MAX_VALUE);
		this.threadSoap12.start();
	} 	
	private Vector<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new Vector<ErroreAttesoOpenSPCoopLogCore>();
	@AfterGroups (alwaysRun=true , groups=MTOMPortaApplicativa.ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		if(this.threadSoap11!=null){
			this.threadSoap11.setStop(true);
			while(this.threadSoap11.isFinished()==false){
				try{
					Thread.sleep(1000);		
				}catch(Exception e){}
			}
		}
		if(this.threadSoap12!=null){
			this.threadSoap12.setStop(true);
			while(this.threadSoap12.isFinished()==false){
				try{
					Thread.sleep(1000);		
				}catch(Exception e){}
			}
		}
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	
	private void waitStart(boolean soap11){
		if(soap11){
			if(this.threadSoap11!=null){
				while(this.threadSoap11.isStarted()==false){
					try{
						Thread.sleep(1000);		
					}catch(Exception e){}
				}
			}
		}
		else{
			if(this.threadSoap12!=null){
				while(this.threadSoap12.isStarted()==false){
					try{
						Thread.sleep(1000);		
					}catch(Exception e){}
				}
			}
		}
	}

	

	@Test(groups={MTOMPortaApplicativa.ID_GRUPPO,MTOMPortaApplicativa.ID_GRUPPO+".SOAP11"})
	public void testMTOM_Soap11() throws Exception{
		waitStart(MTOMUtilities.SOAP11);
		MTOMUtilities.testMTOM(MTOMUtilities.SOAP11, MTOMUtilities.PORTA_APPLICATIVA,
				org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getServizioRicezioneBusteErogatore()+
        				CostantiTestSuite.PORTA_APPLICATIVA_MTOM_SOAP11, 
        				CostantiTestSuite.USERNAME_PORTA_APPLICATIVA_MTOM,
        				CostantiTestSuite.PASSWORD_PORTA_APPLICATIVA_MTOM, addIDUnivoco, 
        				MTOMUtilities.OTHER_ATTACHMENTS_ENABLED,
        				CostantiTestSuite.PROXY_SERVIZIO_MTOM_AZIONE_ECHO,
        				3,3);
	}
	
	
	
	@Test(groups={MTOMPortaApplicativa.ID_GRUPPO,MTOMPortaApplicativa.ID_GRUPPO+".SOAP12"})
	public void testMTOM_Soap12() throws Exception{
		waitStart(MTOMUtilities.SOAP12);
		MTOMUtilities.testMTOM(MTOMUtilities.SOAP12, MTOMUtilities.PORTA_APPLICATIVA,
				org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getServizioRicezioneBusteErogatore()+
        				CostantiTestSuite.PORTA_APPLICATIVA_MTOM_SOAP12, 
        				CostantiTestSuite.USERNAME_PORTA_APPLICATIVA_MTOM, 
        				CostantiTestSuite.PASSWORD_PORTA_APPLICATIVA_MTOM, addIDUnivoco, 
        				MTOMUtilities.OTHER_ATTACHMENTS_ENABLED,
        				CostantiTestSuite.PROXY_SERVIZIO_MTOM_AZIONE_ECHO,
        				3,3);
	}
	
	
	
	
	
	
	
	
	
	
	@Test(groups={MTOMPortaApplicativa.ID_GRUPPO,MTOMPortaApplicativa.ID_GRUPPO+".SOAP11_VALIDAZIONE"})
	public void testMTOM_Soap11_validazione() throws Exception{
		waitStart(MTOMUtilities.SOAP11);
		MTOMUtilities.testMTOM(MTOMUtilities.SOAP11, MTOMUtilities.PORTA_APPLICATIVA,
				org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getServizioRicezioneBusteErogatore()+
        				CostantiTestSuite.PORTA_APPLICATIVA_MTOM_SOAP11_VALIDAZIONE, 
        				CostantiTestSuite.USERNAME_PORTA_APPLICATIVA_MTOM,
        				CostantiTestSuite.PASSWORD_PORTA_APPLICATIVA_MTOM, addIDUnivoco, 
        				MTOMUtilities.OTHER_ATTACHMENTS_ENABLED,
        				CostantiTestSuite.PROXY_SERVIZIO_MTOM_AZIONE_ECHO,
        				3,3);
	}
	
	
	
	@Test(groups={MTOMPortaApplicativa.ID_GRUPPO,MTOMPortaApplicativa.ID_GRUPPO+".SOAP12_VALIDAZIONE"})
	public void testMTOM_Soap12_validazione() throws Exception{
		waitStart(MTOMUtilities.SOAP12);
		MTOMUtilities.testMTOM(MTOMUtilities.SOAP12, MTOMUtilities.PORTA_APPLICATIVA,
				org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getServizioRicezioneBusteErogatore()+
        				CostantiTestSuite.PORTA_APPLICATIVA_MTOM_SOAP12_VALIDAZIONE, 
        				CostantiTestSuite.USERNAME_PORTA_APPLICATIVA_MTOM, 
        				CostantiTestSuite.PASSWORD_PORTA_APPLICATIVA_MTOM, addIDUnivoco, 
        				MTOMUtilities.OTHER_ATTACHMENTS_ENABLED,
        				CostantiTestSuite.PROXY_SERVIZIO_MTOM_AZIONE_ECHO,
        				3,3);
	}
	
	
	
	
	
	
	
	@Test(groups={MTOMPortaApplicativa.ID_GRUPPO,MTOMPortaApplicativa.ID_GRUPPO+".SOAP11_UNPACKAGE_PACKAGE_ALL"})
	public void testMTOM_Soap11_unpackagePackage_all() throws Exception{
		waitStart(MTOMUtilities.SOAP11);
		MTOMUtilities.testMTOM(MTOMUtilities.SOAP11, MTOMUtilities.PORTA_APPLICATIVA,
				org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getServizioRicezioneBusteErogatore()+
        				CostantiTestSuite.PORTA_APPLICATIVA_MTOM_SOAP11_UNPACKAGE_PACKAGE,
        				CostantiTestSuite.USERNAME_PORTA_APPLICATIVA_MTOM,
        				CostantiTestSuite.PASSWORD_PORTA_APPLICATIVA_MTOM, addIDUnivoco, 
        				MTOMUtilities.OTHER_ATTACHMENTS_ENABLED,
        				CostantiTestSuite.PROXY_SERVIZIO_MTOM_AZIONE_UNPACKAGE_PACKAGE,
        				0,3);
	}
	
	
	
	@Test(groups={MTOMPortaApplicativa.ID_GRUPPO,MTOMPortaApplicativa.ID_GRUPPO+".SOAP12_UNPACKAGE_PACKAGE_ALL"})
	public void testMTOM_Soap12_unpackagePackage_all() throws Exception{
		waitStart(MTOMUtilities.SOAP12);
		MTOMUtilities.testMTOM(MTOMUtilities.SOAP12, MTOMUtilities.PORTA_APPLICATIVA,
				org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getServizioRicezioneBusteErogatore()+
        				CostantiTestSuite.PORTA_APPLICATIVA_MTOM_SOAP12_UNPACKAGE_PACKAGE,
        				CostantiTestSuite.USERNAME_PORTA_APPLICATIVA_MTOM, 
        				CostantiTestSuite.PASSWORD_PORTA_APPLICATIVA_MTOM, addIDUnivoco, 
        				MTOMUtilities.OTHER_ATTACHMENTS_ENABLED,
        				CostantiTestSuite.PROXY_SERVIZIO_MTOM_AZIONE_UNPACKAGE_PACKAGE,
        				0,3);
	}
	
	
	
	
	
	
	@Test(groups={MTOMPortaApplicativa.ID_GRUPPO,MTOMPortaApplicativa.ID_GRUPPO+".SOAP11_UNPACKAGE_PACKAGE_XML"})
	public void testMTOM_Soap11_unpackagePackage_xml() throws Exception{
		waitStart(MTOMUtilities.SOAP11);
		MTOMUtilities.testMTOM(MTOMUtilities.SOAP11, MTOMUtilities.PORTA_APPLICATIVA,
				org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getServizioRicezioneBusteErogatore()+
        				CostantiTestSuite.PORTA_APPLICATIVA_MTOM_SOAP11_UNPACKAGE_PACKAGE,
        				CostantiTestSuite.USERNAME_PORTA_APPLICATIVA_MTOM,
        				CostantiTestSuite.PASSWORD_PORTA_APPLICATIVA_MTOM, addIDUnivoco, 
        				MTOMUtilities.OTHER_ATTACHMENTS_DISABLED,
        				CostantiTestSuite.PROXY_SERVIZIO_MTOM_AZIONE_UNPACKAGE_PACKAGE,
        				0,1);
	}
	
	
	
	@Test(groups={MTOMPortaApplicativa.ID_GRUPPO,MTOMPortaApplicativa.ID_GRUPPO+".SOAP12_UNPACKAGE_PACKAGE_XML"})
	public void testMTOM_Soap12_unpackagePackage_xml() throws Exception{
		waitStart(MTOMUtilities.SOAP12);
		MTOMUtilities.testMTOM(MTOMUtilities.SOAP12, MTOMUtilities.PORTA_APPLICATIVA,
				org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getServizioRicezioneBusteErogatore()+
        				CostantiTestSuite.PORTA_APPLICATIVA_MTOM_SOAP12_UNPACKAGE_PACKAGE,
        				CostantiTestSuite.USERNAME_PORTA_APPLICATIVA_MTOM, 
        				CostantiTestSuite.PASSWORD_PORTA_APPLICATIVA_MTOM, addIDUnivoco, 
        				MTOMUtilities.OTHER_ATTACHMENTS_DISABLED,
        				CostantiTestSuite.PROXY_SERVIZIO_MTOM_AZIONE_UNPACKAGE_PACKAGE,
        				0,1);
	}
	
	
	
	
	
	
	
	
	
	
	@Test(groups={MTOMPortaApplicativa.ID_GRUPPO,MTOMPortaApplicativa.ID_GRUPPO+".SOAP11_PACKAGE_UNPACKAGE_ALL"})
	public void testMTOM_Soap11_packageUnpackage_all() throws Exception{
		waitStart(MTOMUtilities.SOAP11);
		MTOMUtilities.testNoMTOM(MTOMUtilities.SOAP11, MTOMUtilities.PORTA_APPLICATIVA,
						CostantiTestSuite.PORTA_APPLICATIVA_MTOM_SOAP11_PACKAGE_UNPACKAGE,
        				CostantiTestSuite.USERNAME_PORTA_APPLICATIVA_MTOM,
        				CostantiTestSuite.PASSWORD_PORTA_APPLICATIVA_MTOM, addIDUnivoco, 
        				MTOMUtilities.OTHER_ATTACHMENTS_ENABLED,
        				CostantiTestSuite.PROXY_SERVIZIO_MTOM_AZIONE_PACKAGE_UNPACKAGE,
        				3,0);
	}
	
	
	
	@Test(groups={MTOMPortaApplicativa.ID_GRUPPO,MTOMPortaApplicativa.ID_GRUPPO+".SOAP12_PACKAGE_UNPACKAGE_ALL"})
	public void testMTOM_Soap12_packageUnpackage_all() throws Exception{
		waitStart(MTOMUtilities.SOAP12);
		MTOMUtilities.testNoMTOM(MTOMUtilities.SOAP12, MTOMUtilities.PORTA_APPLICATIVA,
						CostantiTestSuite.PORTA_APPLICATIVA_MTOM_SOAP12_PACKAGE_UNPACKAGE,
        				CostantiTestSuite.USERNAME_PORTA_APPLICATIVA_MTOM, 
        				CostantiTestSuite.PASSWORD_PORTA_APPLICATIVA_MTOM, addIDUnivoco, 
        				MTOMUtilities.OTHER_ATTACHMENTS_ENABLED,
        				CostantiTestSuite.PROXY_SERVIZIO_MTOM_AZIONE_PACKAGE_UNPACKAGE,
        				3,0);
	}
	
	
	
	
	
	
	@Test(groups={MTOMPortaApplicativa.ID_GRUPPO,MTOMPortaApplicativa.ID_GRUPPO+".SOAP11_PACKAGE_UNPACKAGE_XML"})
	public void testMTOM_Soap11_packageUnpackage_xml() throws Exception{
		waitStart(MTOMUtilities.SOAP11);
		MTOMUtilities.testNoMTOM(MTOMUtilities.SOAP11, MTOMUtilities.PORTA_APPLICATIVA,
						CostantiTestSuite.PORTA_APPLICATIVA_MTOM_SOAP11_PACKAGE_UNPACKAGE,
        				CostantiTestSuite.USERNAME_PORTA_APPLICATIVA_MTOM,
        				CostantiTestSuite.PASSWORD_PORTA_APPLICATIVA_MTOM, addIDUnivoco, 
        				MTOMUtilities.OTHER_ATTACHMENTS_DISABLED,
        				CostantiTestSuite.PROXY_SERVIZIO_MTOM_AZIONE_PACKAGE_UNPACKAGE,
        				1,0);
	}
	
	
	
	@Test(groups={MTOMPortaApplicativa.ID_GRUPPO,MTOMPortaApplicativa.ID_GRUPPO+".SOAP12_PACKAGE_UNPACKAGE_XML"})
	public void testMTOM_Soap12_packageUnpackage_xml() throws Exception{
		waitStart(MTOMUtilities.SOAP12);
		MTOMUtilities.testNoMTOM(MTOMUtilities.SOAP12, MTOMUtilities.PORTA_APPLICATIVA,
						CostantiTestSuite.PORTA_APPLICATIVA_MTOM_SOAP12_PACKAGE_UNPACKAGE,
        				CostantiTestSuite.USERNAME_PORTA_APPLICATIVA_MTOM, 
        				CostantiTestSuite.PASSWORD_PORTA_APPLICATIVA_MTOM, addIDUnivoco, 
        				MTOMUtilities.OTHER_ATTACHMENTS_DISABLED,
        				CostantiTestSuite.PROXY_SERVIZIO_MTOM_AZIONE_PACKAGE_UNPACKAGE,
        				1,0);
	}

	
	
	
	
	
	
	
	
	
	
	@Test(groups={MTOMPortaApplicativa.ID_GRUPPO,MTOMPortaApplicativa.ID_GRUPPO+".SOAP11_VERIFY_OK"})
	public void testMTOM_Soap11_verifyOk() throws Exception{
		waitStart(MTOMUtilities.SOAP11);
		MTOMUtilities.testMTOM(MTOMUtilities.SOAP11, MTOMUtilities.PORTA_APPLICATIVA,
				org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getServizioRicezioneBusteErogatore()+
        				CostantiTestSuite.PORTA_APPLICATIVA_MTOM_SOAP11_VERIFY_OK, 
        				CostantiTestSuite.USERNAME_PORTA_APPLICATIVA_MTOM,
        				CostantiTestSuite.PASSWORD_PORTA_APPLICATIVA_MTOM, addIDUnivoco, 
        				MTOMUtilities.OTHER_ATTACHMENTS_ENABLED,
        				CostantiTestSuite.PROXY_SERVIZIO_MTOM_AZIONE_ECHO,
        				3,3);
	}
	
	
	
	@Test(groups={MTOMPortaApplicativa.ID_GRUPPO,MTOMPortaApplicativa.ID_GRUPPO+".SOAP12_VERIFY_OK"})
	public void testMTOM_Soap12_verifyOk() throws Exception{
		waitStart(MTOMUtilities.SOAP12);
		MTOMUtilities.testMTOM(MTOMUtilities.SOAP12, MTOMUtilities.PORTA_APPLICATIVA,
				org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getServizioRicezioneBusteErogatore()+
        				CostantiTestSuite.PORTA_APPLICATIVA_MTOM_SOAP12_VERIFY_OK, 
        				CostantiTestSuite.USERNAME_PORTA_APPLICATIVA_MTOM, 
        				CostantiTestSuite.PASSWORD_PORTA_APPLICATIVA_MTOM, addIDUnivoco, 
        				MTOMUtilities.OTHER_ATTACHMENTS_ENABLED,
        				CostantiTestSuite.PROXY_SERVIZIO_MTOM_AZIONE_ECHO,
        				3,3);
	}
	

	
	
	
	
	
	
	@Test(groups={MTOMPortaApplicativa.ID_GRUPPO,MTOMPortaApplicativa.ID_GRUPPO+".SOAP11_VERIFY_KO_REQUEST"})
	public void testMTOM_Soap11_verifyKo_request() throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		waitStart(MTOMUtilities.SOAP11);
		String msgErrore = "XpathEngine (expr(richiesta)://{http://www.openspcoop2.org/example/server/mtom}richiesta) found element ({http://www.openspcoop2.org/example/server/mtom}richiesta) without childs, mtom optimize packaging require xop:Include element";
		MTOMUtilities.testMTOM(MTOMUtilities.SOAP11, MTOMUtilities.PORTA_APPLICATIVA, 
				org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getServizioRicezioneBusteErogatore()+
        				CostantiTestSuite.PORTA_APPLICATIVA_MTOM_SOAP11_VERIFY_KO_REQUEST,
        				CostantiTestSuite.USERNAME_PORTA_APPLICATIVA_MTOM, 
        				CostantiTestSuite.PASSWORD_PORTA_APPLICATIVA_MTOM, addIDUnivoco, 
        				MTOMUtilities.OTHER_ATTACHMENTS_ENABLED,
        				CostantiTestSuite.PROXY_SERVIZIO_MTOM_AZIONE_ECHO,
        				3,3,
        				true,"Processamento MTOM (verify) della richiesta fallito: "+msgErrore,true,dataInizioTest);
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);		
		err.setMsgErrore(msgErrore);
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	
	
	@Test(groups={MTOMPortaApplicativa.ID_GRUPPO,MTOMPortaApplicativa.ID_GRUPPO+".SOAP12_VERIFY_KO_REQUEST"})
	public void testMTOM_Soap12_verifyKo_request() throws Exception{

		Date dataInizioTest = DateManager.getDate();
		
		waitStart(MTOMUtilities.SOAP12);
		String msgErrore = "XpathEngine (expr(richiesta)://{http://www.openspcoop2.org/example/server/mtom}richiesta) found element ({http://www.openspcoop2.org/example/server/mtom}richiesta) without childs, mtom optimize packaging require xop:Include element";
		MTOMUtilities.testMTOM(MTOMUtilities.SOAP12, MTOMUtilities.PORTA_APPLICATIVA, 
				org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getServizioRicezioneBusteErogatore()+
        				CostantiTestSuite.PORTA_APPLICATIVA_MTOM_SOAP12_VERIFY_KO_REQUEST,
        				CostantiTestSuite.USERNAME_PORTA_APPLICATIVA_MTOM, 
        				CostantiTestSuite.PASSWORD_PORTA_APPLICATIVA_MTOM, addIDUnivoco,
        				MTOMUtilities.OTHER_ATTACHMENTS_ENABLED,
        				CostantiTestSuite.PROXY_SERVIZIO_MTOM_AZIONE_ECHO,
        				3,3,
        				true,"Processamento MTOM (verify) della richiesta fallito: "+msgErrore,true,dataInizioTest);
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);		
		err.setMsgErrore(msgErrore);
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	
	
	
	
	
	
	
	
	@Test(groups={MTOMPortaApplicativa.ID_GRUPPO,MTOMPortaApplicativa.ID_GRUPPO+".SOAP11_VERIFY_KO_RESPONSE"})
	public void testMTOM_Soap11_verifyKo_response() throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		waitStart(MTOMUtilities.SOAP11);
		String msgErrore = "The attachment has wrong content-type (expected:application/altroTipoDiverso): application/octet-stream";
		MTOMUtilities.testMTOM(MTOMUtilities.SOAP11, MTOMUtilities.PORTA_APPLICATIVA, 
				org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getServizioRicezioneBusteErogatore()+
        				CostantiTestSuite.PORTA_APPLICATIVA_MTOM_SOAP11_VERIFY_KO_RESPONSE,
        				CostantiTestSuite.USERNAME_PORTA_APPLICATIVA_MTOM, 
        				CostantiTestSuite.PASSWORD_PORTA_APPLICATIVA_MTOM, addIDUnivoco, 
        				MTOMUtilities.OTHER_ATTACHMENTS_ENABLED,
        				CostantiTestSuite.PROXY_SERVIZIO_MTOM_AZIONE_ECHO,
        				3,3,
        				true,msgErrore,false,dataInizioTest);
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);		
		err.setMsgErrore(msgErrore);
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	
	
	@Test(groups={MTOMPortaApplicativa.ID_GRUPPO,MTOMPortaApplicativa.ID_GRUPPO+".SOAP12_VERIFY_KO_RESPONSE"})
	public void testMTOM_Soap12_verifyKo_response() throws Exception{

		Date dataInizioTest = DateManager.getDate();
		
		waitStart(MTOMUtilities.SOAP12);
		String msgErrore = "The attachment has wrong content-type (expected:application/altroTipoDiverso): application/octet-stream";
		MTOMUtilities.testMTOM(MTOMUtilities.SOAP12, MTOMUtilities.PORTA_APPLICATIVA, 
				org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getServizioRicezioneBusteErogatore()+
        				CostantiTestSuite.PORTA_APPLICATIVA_MTOM_SOAP12_VERIFY_KO_RESPONSE,
        				CostantiTestSuite.USERNAME_PORTA_APPLICATIVA_MTOM, 
        				CostantiTestSuite.PASSWORD_PORTA_APPLICATIVA_MTOM, addIDUnivoco,
        				MTOMUtilities.OTHER_ATTACHMENTS_ENABLED,
        				CostantiTestSuite.PROXY_SERVIZIO_MTOM_AZIONE_ECHO,
        				3,3,
        				true,msgErrore,false,dataInizioTest);
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);		
		err.setMsgErrore(msgErrore);
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
}
