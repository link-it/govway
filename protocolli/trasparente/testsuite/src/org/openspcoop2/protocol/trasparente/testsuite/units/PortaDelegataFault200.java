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
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.PortaDelegata;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.PortaImpl;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * PortaDelegataFault200
 * 
 * @author Andreea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaDelegataFault200 extends PortaImpl {
	/* TEST ONE WAY FAULT.200 */
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP11.STATEFUL"})
	public void oneWayFault200_pdSOAP11Stateful() throws TestSuiteException, Exception{
		if(this.doTestStateful==false){
			return;
		}
		Porta._oneWayFault200(this.pdSOAP11Stateful, this.repositoryPortaDelegataOneWayFault200_Soap11Stateful);
	}

	Repository repositoryPortaDelegataOneWayFault200_Soap11Stateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWayFault200_Soap11Stateful")
	public Object[][]testPortaDelegataOneWayFault200_Soap11Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWayFault200_Soap11Stateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP11.STATEFUL"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWayFault200_Soap11Stateful",dependsOnMethods={"oneWayFault200_pdSOAP11Stateful"})
	public void testOneWayFault200_pdSOAP11Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		if(this.doTestStateful==false){
			return;
		}
		Porta._testOneWayFault200(this.pdSOAP11Stateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP11.STATELESS"})
	public void oneWayFault200_pdSOAP11Stateless() throws TestSuiteException, Exception{
		Porta._oneWayFault200(this.pdSOAP11Stateless, this.repositoryPortaDelegataOneWayFault200_Soap11Stateless);
	}

	Repository repositoryPortaDelegataOneWayFault200_Soap11Stateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWayFault200_Soap11Stateless")
	public Object[][]testPortaDelegataOneWayFault200_Soap11Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWayFault200_Soap11Stateless);
	}
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP11.STATELESS"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWayFault200_Soap11Stateless",dependsOnMethods={"oneWayFault200_pdSOAP11Stateless"})
	public void testOneWayFault200_pdSOAP11Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWayFault200(this.pdSOAP11Stateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP12.STATEFUL"})
	public void oneWayFault200_pdSOAP12Stateful() throws TestSuiteException, Exception{
		if(this.doTestStateful==false){
			return;
		}
		Porta._oneWayFault200(this.pdSOAP12Stateful, this.repositoryPortaDelegataOneWayFault200_Soap12Stateful);
	}

	Repository repositoryPortaDelegataOneWayFault200_Soap12Stateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWayFault200_Soap12Stateful")
	public Object[][]testPortaDelegataOneWayFault200_Soap12Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWayFault200_Soap12Stateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP12.STATEFUL"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWayFault200_Soap12Stateful",dependsOnMethods={"oneWayFault200_pdSOAP12Stateful"})
	public void testOneWayFault200_pdSOAP12Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		if(this.doTestStateful==false){
			return;
		}
		Porta._testOneWayFault200(this.pdSOAP12Stateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP12.STATELESS"})
	public void oneWayFault200_pdSOAP12Stateless() throws TestSuiteException, Exception{
		Porta._oneWayFault200(this.pdSOAP12Stateless, this.repositoryPortaDelegataOneWayFault200_Soap12Stateless);
	}

	Repository repositoryPortaDelegataOneWayFault200_Soap12Stateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWayFault200_Soap12Stateless")
	public Object[][]testPortaDelegataOneWayFault200_Soap12Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWayFault200_Soap12Stateless);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP12.STATELESS"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWayFault200_Soap12Stateless",dependsOnMethods={"oneWayFault200_pdSOAP12Stateless"})
	public void testOneWayFault200_pdSOAP12Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWayFault200(this.pdSOAP12Stateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP11.STATEFUL.ATTACHMENTS"})
	public void oneWayFault200_pdSOAP11WithAttachmentsStateful() throws TestSuiteException, Exception{
		if(this.doTestStateful==false){
			return;
		}
		Porta._oneWayFault200(this.pdSOAP11WithAttachmentsStateful, this.repositoryPortaDelegataOneWayFault200_Soap11WithAttachmentsStateful);
	}

	Repository repositoryPortaDelegataOneWayFault200_Soap11WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWayFault200_Soap11WithAttachmentsStateful")
	public Object[][]testPortaDelegataOneWayFault200_Soap11WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWayFault200_Soap11WithAttachmentsStateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP11.STATEFUL.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWayFault200_Soap11WithAttachmentsStateful",dependsOnMethods={"oneWayFault200_pdSOAP11WithAttachmentsStateful"})
	public void testOneWayFault200_pdSOAP11WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		if(this.doTestStateful==false){
			return;
		}
		Porta._testOneWayFault200(this.pdSOAP11WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP11.STATELESS.ATTACHMENTS"})
	public void oneWayFault200_pdSOAP11WithAttachmentsStateless() throws TestSuiteException, Exception{
		Porta._oneWayFault200(this.pdSOAP11WithAttachmentsStateless, this.repositoryPortaDelegataOneWayFault200_Soap11WithAttachmentsStateless);
	}

	Repository repositoryPortaDelegataOneWayFault200_Soap11WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWayFault200_Soap11WithAttachmentsStateless")
	public Object[][]testPortaDelegataOneWayFault200_Soap11WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWayFault200_Soap11WithAttachmentsStateless);
	}
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP11.STATELESS.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWayFault200_Soap11WithAttachmentsStateless",dependsOnMethods={"oneWayFault200_pdSOAP11WithAttachmentsStateless"})
	public void testOneWayFault200_pdSOAP11WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWayFault200(this.pdSOAP11WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP12.STATEFUL.ATTACHMENTS"})
	public void oneWayFault200_pdSOAP12WithAttachmentsStateful() throws TestSuiteException, Exception{
		if(this.doTestStateful==false){
			return;
		}
		Porta._oneWayFault200(this.pdSOAP12WithAttachmentsStateful, this.repositoryPortaDelegataOneWayFault200_Soap12WithAttachmentsStateful);
	}

	Repository repositoryPortaDelegataOneWayFault200_Soap12WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWayFault200_Soap12WithAttachmentsStateful")
	public Object[][]testPortaDelegataOneWayFault200_Soap12WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWayFault200_Soap12WithAttachmentsStateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP12.STATEFUL.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWayFault200_Soap12WithAttachmentsStateful",dependsOnMethods={"oneWayFault200_pdSOAP12WithAttachmentsStateful"})
	public void testOneWayFault200_pdSOAP12WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		if(this.doTestStateful==false){
			return;
		}
		Porta._testOneWayFault200(this.pdSOAP12WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP12.STATELESS.ATTACHMENTS"})
	public void oneWayFault200_pdSOAP12WithAttachmentsStateless() throws TestSuiteException, Exception{
		Porta._oneWayFault200(this.pdSOAP12WithAttachmentsStateless, this.repositoryPortaDelegataOneWayFault200_Soap12WithAttachmentsStateless);
	}

	Repository repositoryPortaDelegataOneWayFault200_Soap12WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWayFault200_Soap12WithAttachmentsStateless")
	public Object[][]testPortaDelegataOneWayFault200_Soap12WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWayFault200_Soap12WithAttachmentsStateless);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY_FAULT.200.SOAP12.STATELESS.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWayFault200_Soap12WithAttachmentsStateless",dependsOnMethods={"oneWayFault200_pdSOAP12WithAttachmentsStateless"})
	public void testOneWayFault200_pdSOAP12WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWayFault200(this.pdSOAP12WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	/* TEST SINCRONO FAULT.200 */
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP11.STATELESS"})
	public void sincronoFault200_pdSOAP11Stateless() throws TestSuiteException, Exception{
		Porta._sincronoFault200(this.pdSOAP11Stateless, this.repositoryPortaDelegataSincronoFault200_Soap11Stateless);
	}

	Repository repositoryPortaDelegataSincronoFault200_Soap11Stateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoFault200_Soap11Stateless")
	public Object[][]testPortaDelegataSincronoFault200_Soap11Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoFault200_Soap11Stateless);
	}
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP11.STATELESS"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoFault200_Soap11Stateless",dependsOnMethods={"sincronoFault200_pdSOAP11Stateless"})
	public void testSincronoFault200_pdSOAP11Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoFault200(this.pdSOAP11Stateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP11.STATEFUL"})
	public void sincronoFault200_pdSOAP11Stateful() throws TestSuiteException, Exception{
		Porta._sincronoFault200(this.pdSOAP11Stateful, this.repositoryPortaDelegataSincronoFault200_Soap11Stateful);
	}

	Repository repositoryPortaDelegataSincronoFault200_Soap11Stateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoFault200_Soap11Stateful")
	public Object[][]testPortaDelegataSincronoFault200_Soap11Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoFault200_Soap11Stateful);
	}
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP11.STATEFUL"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoFault200_Soap11Stateful",dependsOnMethods={"sincronoFault200_pdSOAP11Stateful"})
	public void testSincronoFault200_pdSOAP11Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoFault200(this.pdSOAP11Stateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP12.STATELESS"})
	public void sincronoFault200_pdSOAP12Stateless() throws TestSuiteException, Exception{
		Porta._sincronoFault200(this.pdSOAP12Stateless, this.repositoryPortaDelegataSincronoFault200_Soap12Stateless);
	}

	Repository repositoryPortaDelegataSincronoFault200_Soap12Stateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoFault200_Soap12Stateless")
	public Object[][]testPortaDelegataSincronoFault200_Soap12Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoFault200_Soap12Stateless);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP12.STATELESS"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoFault200_Soap12Stateless",dependsOnMethods={"sincronoFault200_pdSOAP12Stateless"})
	public void testSincronoFault200_pdSOAP12Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoFault200(this.pdSOAP12Stateless, data, msgDiagData, id, checkServizioApplicativo);
	}	

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP12.STATEFUL"})
	public void sincronoFault200_pdSOAP12Stateful() throws TestSuiteException, Exception{
		Porta._sincronoFault200(this.pdSOAP12Stateful, this.repositoryPortaDelegataSincronoFault200_Soap12Stateful);
	}

	Repository repositoryPortaDelegataSincronoFault200_Soap12Stateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoFault200_Soap12Stateful")
	public Object[][]testPortaDelegataSincronoFault200_Soap12Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoFault200_Soap12Stateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP12.STATEFUL"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoFault200_Soap12Stateful",dependsOnMethods={"sincronoFault200_pdSOAP12Stateful"})
	public void testSincronoFault200_pdSOAP12Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoFault200(this.pdSOAP12Stateful, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP11.STATELESS.ATTACHMENTS"})
	public void sincronoFault200_pdSOAP11WithAttachmentsStateless() throws TestSuiteException, Exception{
		Porta._sincronoFault200(this.pdSOAP11WithAttachmentsStateless, this.repositoryPortaDelegataSincronoFault200_Soap11WithAttachmentsStateless);
	}

	Repository repositoryPortaDelegataSincronoFault200_Soap11WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoFault200_Soap11WithAttachmentsStateless")
	public Object[][]testPortaDelegataSincronoFault200_Soap11WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoFault200_Soap11WithAttachmentsStateless);
	}
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP11.STATELESS.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoFault200_Soap11WithAttachmentsStateless",dependsOnMethods={"sincronoFault200_pdSOAP11WithAttachmentsStateless"})
	public void testSincronoFault200_pdSOAP11WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoFault200(this.pdSOAP11WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP12.STATELESS.ATTACHMENTS"})
	public void sincronoFault200_pdSOAP12WithAttachmentsStateless() throws TestSuiteException, Exception{
		Porta._sincronoFault200(this.pdSOAP12WithAttachmentsStateless, this.repositoryPortaDelegataSincronoFault200_Soap12WithAttachmentsStateless);
	}

	Repository repositoryPortaDelegataSincronoFault200_Soap12WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoFault200_Soap12WithAttachmentsStateless")
	public Object[][]testPortaDelegataSincronoFault200_Soap12WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoFault200_Soap12WithAttachmentsStateless);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP12.STATELESS.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoFault200_Soap12WithAttachmentsStateless",dependsOnMethods={"sincronoFault200_pdSOAP12WithAttachmentsStateless"})
	public void testSincronoFault200_pdSOAP12WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoFault200(this.pdSOAP12WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo);
	}

	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP12.STATEFUL.ATTACHMENTS"})
	public void sincronoFault200_pdSOAP12WithAttachmentsStateful() throws TestSuiteException, Exception{
		Porta._sincronoFault200(this.pdSOAP12WithAttachmentsStateful, this.repositoryPortaDelegataSincronoFault200_Soap12WithAttachmentsStateful);
	}

	Repository repositoryPortaDelegataSincronoFault200_Soap12WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoFault200_Soap12WithAttachmentsStateful")
	public Object[][]testPortaDelegataSincronoFault200_Soap12WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoFault200_Soap12WithAttachmentsStateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP12.STATEFUL.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoFault200_Soap12WithAttachmentsStateful",dependsOnMethods={"sincronoFault200_pdSOAP12WithAttachmentsStateful"})
	public void testSincronoFault200_pdSOAP12WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoFault200(this.pdSOAP12WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP11.STATEFUL.ATTACHMENTS"})
	public void sincronoFault200_pdSOAP11WithAttachmentsStateful() throws TestSuiteException, Exception{
		Porta._sincronoFault200(this.pdSOAP11WithAttachmentsStateful, this.repositoryPortaDelegataSincronoFault200_Soap11WithAttachmentsStateful);
	}

	Repository repositoryPortaDelegataSincronoFault200_Soap11WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoFault200_Soap11WithAttachmentsStateful")
	public Object[][]testPortaDelegataSincronoFault200_Soap11WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoFault200_Soap11WithAttachmentsStateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Fault200",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO_FAULT.200.SOAP11.STATEFUL.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoFault200_Soap11WithAttachmentsStateful",dependsOnMethods={"sincronoFault200_pdSOAP11WithAttachmentsStateful"})
	public void testSincronoFault200_pdSOAP11WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoFault200(this.pdSOAP11WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo);
	}

}
