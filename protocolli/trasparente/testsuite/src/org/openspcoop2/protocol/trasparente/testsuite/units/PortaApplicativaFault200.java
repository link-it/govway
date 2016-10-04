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
package org.openspcoop2.protocol.trasparente.testsuite.units;

import org.openspcoop2.protocol.trasparente.testsuite.units.utils.Porta;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.PortaApplicativa;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.PortaImpl;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * PortaApplicativaFault200
 * 
 * @author Andreea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaApplicativaFault200 extends PortaImpl {
	/* TEST ONE WAY FAULT.200 */
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP11.STATEFUL"})
	public void oneWayFault200_paSOAP11Stateful() throws TestSuiteException, Exception{
		if(this.doTestStateful==false){
			return;
		}
		Porta._oneWayFault200(this.paSOAP11Stateful, this.repositoryPortaApplicativaOneWayFault200_Soap11Stateful);
	}

	Repository repositoryPortaApplicativaOneWayFault200_Soap11Stateful=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"OneWayFault200_Soap11Stateful")
	public Object[][]testPortaApplicativaOneWayFault200_Soap11Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaOneWayFault200_Soap11Stateful);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP11.STATEFUL"},dataProvider=PortaApplicativa.ID_GRUPPO+"OneWayFault200_Soap11Stateful",dependsOnMethods={"oneWayFault200_paSOAP11Stateful"})
	public void testOneWayFault200_paSOAP11Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		if(this.doTestStateful==false){
			return;
		}
		Porta._testOneWayFault200(this.paSOAP11Stateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP11.STATELESS"})
	public void oneWayFault200_paSOAP11Stateless() throws TestSuiteException, Exception{
		Porta._oneWayFault200(this.paSOAP11Stateless, this.repositoryPortaApplicativaOneWayFault200_Soap11Stateless);
	}

	Repository repositoryPortaApplicativaOneWayFault200_Soap11Stateless=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"OneWayFault200_Soap11Stateless")
	public Object[][]testPortaApplicativaOneWayFault200_Soap11Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaOneWayFault200_Soap11Stateless);
	}
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP11.STATELESS"},dataProvider=PortaApplicativa.ID_GRUPPO+"OneWayFault200_Soap11Stateless",dependsOnMethods={"oneWayFault200_paSOAP11Stateless"})
	public void testOneWayFault200_paSOAP11Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWayFault200(this.paSOAP11Stateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP12.STATEFUL"})
	public void oneWayFault200_paSOAP12Stateful() throws TestSuiteException, Exception{
		if(this.doTestStateful==false){
			return;
		}
		Porta._oneWayFault200(this.paSOAP12Stateful, this.repositoryPortaApplicativaOneWayFault200_Soap12Stateful);
	}

	Repository repositoryPortaApplicativaOneWayFault200_Soap12Stateful=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"OneWayFault200_Soap12Stateful")
	public Object[][]testPortaApplicativaOneWayFault200_Soap12Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaOneWayFault200_Soap12Stateful);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP12.STATEFUL"},dataProvider=PortaApplicativa.ID_GRUPPO+"OneWayFault200_Soap12Stateful",dependsOnMethods={"oneWayFault200_paSOAP12Stateful"})
	public void testOneWayFault200_paSOAP12Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		if(this.doTestStateful==false){
			return;
		}
		Porta._testOneWayFault200(this.paSOAP12Stateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP12.STATELESS"})
	public void oneWayFault200_paSOAP12Stateless() throws TestSuiteException, Exception{
		Porta._oneWayFault200(this.paSOAP12Stateless, this.repositoryPortaApplicativaOneWayFault200_Soap12Stateless);
	}

	Repository repositoryPortaApplicativaOneWayFault200_Soap12Stateless=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"OneWayFault200_Soap12Stateless")
	public Object[][]testPortaApplicativaOneWayFault200_Soap12Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaOneWayFault200_Soap12Stateless);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP12.STATELESS"},dataProvider=PortaApplicativa.ID_GRUPPO+"OneWayFault200_Soap12Stateless",dependsOnMethods={"oneWayFault200_paSOAP12Stateless"})
	public void testOneWayFault200_paSOAP12Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWayFault200(this.paSOAP12Stateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP11.STATEFUL.ATTACHMENTS"})
	public void oneWayFault200_paSOAP11WithAttachmentsStateful() throws TestSuiteException, Exception{
		if(this.doTestStateful==false){
			return;
		}
		Porta._oneWayFault200(this.paSOAP11WithAttachmentsStateful, this.repositoryPortaApplicativaOneWayFault200_Soap11WithAttachmentsStateful);
	}

	Repository repositoryPortaApplicativaOneWayFault200_Soap11WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"OneWayFault200_Soap11WithAttachmentsStateful")
	public Object[][]testPortaApplicativaOneWayFault200_Soap11WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaOneWayFault200_Soap11WithAttachmentsStateful);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP11.STATEFUL.ATTACHMENTS"},dataProvider=PortaApplicativa.ID_GRUPPO+"OneWayFault200_Soap11WithAttachmentsStateful",dependsOnMethods={"oneWayFault200_paSOAP11WithAttachmentsStateful"})
	public void testOneWayFault200_paSOAP11WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		if(this.doTestStateful==false){
			return;
		}
		Porta._testOneWayFault200(this.paSOAP11WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP11.STATELESS.ATTACHMENTS"})
	public void oneWayFault200_paSOAP11WithAttachmentsStateless() throws TestSuiteException, Exception{
		Porta._oneWayFault200(this.paSOAP11WithAttachmentsStateless, this.repositoryPortaApplicativaOneWayFault200_Soap11WithAttachmentsStateless);
	}

	Repository repositoryPortaApplicativaOneWayFault200_Soap11WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"OneWayFault200_Soap11WithAttachmentsStateless")
	public Object[][]testPortaApplicativaOneWayFault200_Soap11WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaOneWayFault200_Soap11WithAttachmentsStateless);
	}
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP11.STATELESS.ATTACHMENTS"},dataProvider=PortaApplicativa.ID_GRUPPO+"OneWayFault200_Soap11WithAttachmentsStateless",dependsOnMethods={"oneWayFault200_paSOAP11WithAttachmentsStateless"})
	public void testOneWayFault200_paSOAP11WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWayFault200(this.paSOAP11WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP12.STATEFUL.ATTACHMENTS"})
	public void oneWayFault200_paSOAP12WithAttachmentsStateful() throws TestSuiteException, Exception{
		if(this.doTestStateful==false){
			return;
		}
		Porta._oneWayFault200(this.paSOAP12WithAttachmentsStateful, this.repositoryPortaApplicativaOneWayFault200_Soap12WithAttachmentsStateful);
	}

	Repository repositoryPortaApplicativaOneWayFault200_Soap12WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"OneWayFault200_Soap12WithAttachmentsStateful")
	public Object[][]testPortaApplicativaOneWayFault200_Soap12WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaOneWayFault200_Soap12WithAttachmentsStateful);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP12.STATEFUL.ATTACHMENTS"},dataProvider=PortaApplicativa.ID_GRUPPO+"OneWayFault200_Soap12WithAttachmentsStateful",dependsOnMethods={"oneWayFault200_paSOAP12WithAttachmentsStateful"})
	public void testOneWayFault200_paSOAP12WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		if(this.doTestStateful==false){
			return;
		}
		Porta._testOneWayFault200(this.paSOAP12WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP12.STATELESS.ATTACHMENTS"})
	public void oneWayFault200_paSOAP12WithAttachmentsStateless() throws TestSuiteException, Exception{
		Porta._oneWayFault200(this.paSOAP12WithAttachmentsStateless, this.repositoryPortaApplicativaOneWayFault200_Soap12WithAttachmentsStateless);
	}

	Repository repositoryPortaApplicativaOneWayFault200_Soap12WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"OneWayFault200_Soap12WithAttachmentsStateless")
	public Object[][]testPortaApplicativaOneWayFault200_Soap12WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaOneWayFault200_Soap12WithAttachmentsStateless);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP12.STATELESS.ATTACHMENTS"},dataProvider=PortaApplicativa.ID_GRUPPO+"OneWayFault200_Soap12WithAttachmentsStateless",dependsOnMethods={"oneWayFault200_paSOAP12WithAttachmentsStateless"})
	public void testOneWayFault200_paSOAP12WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWayFault200(this.paSOAP12WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	/* TEST SINCRONO FAULT.200 */
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP11.STATELESS"})
	public void sincronoFault200_paSOAP11Stateless() throws TestSuiteException, Exception{
		Porta._sincronoFault200(this.paSOAP11Stateless, this.repositoryPortaApplicativaSincronoFault200_Soap11Stateless);
	}

	Repository repositoryPortaApplicativaSincronoFault200_Soap11Stateless=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"SincronoFault200_Soap11Stateless")
	public Object[][]testPortaApplicativaSincronoFault200_Soap11Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaSincronoFault200_Soap11Stateless);
	}
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP11.STATELESS"},dataProvider=PortaApplicativa.ID_GRUPPO+"SincronoFault200_Soap11Stateless",dependsOnMethods={"sincronoFault200_paSOAP11Stateless"})
	public void testSincronoFault200_paSOAP11Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoFault200(this.paSOAP11Stateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP11.STATEFUL"})
	public void sincronoFault200_paSOAP11Stateful() throws TestSuiteException, Exception{
		Porta._sincronoFault200(this.paSOAP11Stateful, this.repositoryPortaApplicativaSincronoFault200_Soap11Stateful);
	}

	Repository repositoryPortaApplicativaSincronoFault200_Soap11Stateful=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"SincronoFault200_Soap11Stateful")
	public Object[][]testPortaApplicativaSincronoFault200_Soap11Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaSincronoFault200_Soap11Stateful);
	}
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP11.STATEFUL"},dataProvider=PortaApplicativa.ID_GRUPPO+"SincronoFault200_Soap11Stateful",dependsOnMethods={"sincronoFault200_paSOAP11Stateful"})
	public void testSincronoFault200_paSOAP11Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoFault200(this.paSOAP11Stateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP12.STATELESS"})
	public void sincronoFault200_paSOAP12Stateless() throws TestSuiteException, Exception{
		Porta._sincronoFault200(this.paSOAP12Stateless, this.repositoryPortaApplicativaSincronoFault200_Soap12Stateless);
	}

	Repository repositoryPortaApplicativaSincronoFault200_Soap12Stateless=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"SincronoFault200_Soap12Stateless")
	public Object[][]testPortaApplicativaSincronoFault200_Soap12Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaSincronoFault200_Soap12Stateless);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP12.STATELESS"},dataProvider=PortaApplicativa.ID_GRUPPO+"SincronoFault200_Soap12Stateless",dependsOnMethods={"sincronoFault200_paSOAP12Stateless"})
	public void testSincronoFault200_paSOAP12Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoFault200(this.paSOAP12Stateless, data, msgDiagData, id, checkServizioApplicativo);
	}	

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP12.STATEFUL"})
	public void sincronoFault200_paSOAP12Stateful() throws TestSuiteException, Exception{
		Porta._sincronoFault200(this.paSOAP12Stateful, this.repositoryPortaApplicativaSincronoFault200_Soap12Stateful);
	}

	Repository repositoryPortaApplicativaSincronoFault200_Soap12Stateful=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"SincronoFault200_Soap12Stateful")
	public Object[][]testPortaApplicativaSincronoFault200_Soap12Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaSincronoFault200_Soap12Stateful);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP12.STATEFUL"},dataProvider=PortaApplicativa.ID_GRUPPO+"SincronoFault200_Soap12Stateful",dependsOnMethods={"sincronoFault200_paSOAP12Stateful"})
	public void testSincronoFault200_paSOAP12Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoFault200(this.paSOAP12Stateful, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP11.STATELESS.ATTACHMENTS"})
	public void sincronoFault200_paSOAP11WithAttachmentsStateless() throws TestSuiteException, Exception{
		Porta._sincronoFault200(this.paSOAP11WithAttachmentsStateless, this.repositoryPortaApplicativaSincronoFault200_Soap11WithAttachmentsStateless);
	}

	Repository repositoryPortaApplicativaSincronoFault200_Soap11WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"SincronoFault200_Soap11WithAttachmentsStateless")
	public Object[][]testPortaApplicativaSincronoFault200_Soap11WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaSincronoFault200_Soap11WithAttachmentsStateless);
	}
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP11.STATELESS.ATTACHMENTS"},dataProvider=PortaApplicativa.ID_GRUPPO+"SincronoFault200_Soap11WithAttachmentsStateless",dependsOnMethods={"sincronoFault200_paSOAP11WithAttachmentsStateless"})
	public void testSincronoFault200_paSOAP11WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoFault200(this.paSOAP11WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP12.STATELESS.ATTACHMENTS"})
	public void sincronoFault200_paSOAP12WithAttachmentsStateless() throws TestSuiteException, Exception{
		Porta._sincronoFault200(this.paSOAP12WithAttachmentsStateless, this.repositoryPortaApplicativaSincronoFault200_Soap12WithAttachmentsStateless);
	}

	Repository repositoryPortaApplicativaSincronoFault200_Soap12WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"SincronoFault200_Soap12WithAttachmentsStateless")
	public Object[][]testPortaApplicativaSincronoFault200_Soap12WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaSincronoFault200_Soap12WithAttachmentsStateless);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP12.STATELESS.ATTACHMENTS"},dataProvider=PortaApplicativa.ID_GRUPPO+"SincronoFault200_Soap12WithAttachmentsStateless",dependsOnMethods={"sincronoFault200_paSOAP12WithAttachmentsStateless"})
	public void testSincronoFault200_paSOAP12WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoFault200(this.paSOAP12WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo);
	}

	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP12.STATEFUL.ATTACHMENTS"})
	public void sincronoFault200_paSOAP12WithAttachmentsStateful() throws TestSuiteException, Exception{
		Porta._sincronoFault200(this.paSOAP12WithAttachmentsStateful, this.repositoryPortaApplicativaSincronoFault200_Soap12WithAttachmentsStateful);
	}

	Repository repositoryPortaApplicativaSincronoFault200_Soap12WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"SincronoFault200_Soap12WithAttachmentsStateful")
	public Object[][]testPortaApplicativaSincronoFault200_Soap12WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaSincronoFault200_Soap12WithAttachmentsStateful);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP12.STATEFUL.ATTACHMENTS"},dataProvider=PortaApplicativa.ID_GRUPPO+"SincronoFault200_Soap12WithAttachmentsStateful",dependsOnMethods={"sincronoFault200_paSOAP12WithAttachmentsStateful"})
	public void testSincronoFault200_paSOAP12WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoFault200(this.paSOAP12WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP11.STATEFUL.ATTACHMENTS"})
	public void sincronoFault200_paSOAP11WithAttachmentsStateful() throws TestSuiteException, Exception{
		Porta._sincronoFault200(this.paSOAP11WithAttachmentsStateful, this.repositoryPortaApplicativaSincronoFault200_Soap11WithAttachmentsStateful);
	}

	Repository repositoryPortaApplicativaSincronoFault200_Soap11WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"SincronoFault200_Soap11WithAttachmentsStateful")
	public Object[][]testPortaApplicativaSincronoFault200_Soap11WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaSincronoFault200_Soap11WithAttachmentsStateful);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Fault200",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP11.STATEFUL.ATTACHMENTS"},dataProvider=PortaApplicativa.ID_GRUPPO+"SincronoFault200_Soap11WithAttachmentsStateful",dependsOnMethods={"sincronoFault200_paSOAP11WithAttachmentsStateful"})
	public void testSincronoFault200_paSOAP11WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoFault200(this.paSOAP11WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo);
	}

}
