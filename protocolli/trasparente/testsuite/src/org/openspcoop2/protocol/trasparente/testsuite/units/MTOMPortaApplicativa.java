package org.openspcoop2.protocol.trasparente.testsuite.units;

import java.util.Date;

import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.core.Utilities;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.MTOMThread;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.MTOMUtilities;
import org.openspcoop2.utils.date.DateManager;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

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
	@AfterGroups (alwaysRun=true , groups=MTOMPortaApplicativa.ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
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
	} 
	

	

	@Test(groups={MTOMPortaApplicativa.ID_GRUPPO,MTOMPortaApplicativa.ID_GRUPPO+".SOAP11"})
	public void testMTOM_Soap11() throws Exception{
		
		boolean soap11 = true;
		boolean portaApplicativa = false; // deve essere false
		MTOMUtilities.testMTOM(soap11, portaApplicativa, 
				org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getServizioRicezioneBusteErogatore()+
        				CostantiTestSuite.PORTA_APPLICATIVA_MTOM_SOAP11, 
        				CostantiTestSuite.USERNAME_PORTA_APPLICATIVA_MTOM,
        				CostantiTestSuite.PASSWORD_PORTA_APPLICATIVA_MTOM, addIDUnivoco);
	}
	
	
	
	@Test(groups={MTOMPortaApplicativa.ID_GRUPPO,MTOMPortaApplicativa.ID_GRUPPO+".SOAP12"})
	public void testMTOM_Soap12() throws Exception{
		
		boolean soap12 = false;
		boolean portaApplicativa = false; // deve essere false
		MTOMUtilities.testMTOM(soap12, portaApplicativa, 
				org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance().getServizioRicezioneBusteErogatore()+
        				CostantiTestSuite.PORTA_APPLICATIVA_MTOM_SOAP12, 
        				CostantiTestSuite.USERNAME_PORTA_APPLICATIVA_MTOM, 
        				CostantiTestSuite.PASSWORD_PORTA_APPLICATIVA_MTOM, addIDUnivoco);
	}


}
