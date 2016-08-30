/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
 * PortaDelegataLocalForward
 * 
 * @author Andreea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaDelegataLocalForward extends PortaImpl {
	/* TEST ONE WAY LOCAL_FORWARD */
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"LocalForward",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD.SOAP11.STATEFUL"})
	public void oneWayLocalForward_pdSOAP11Stateful() throws TestSuiteException, Exception{
		PortaDelegata._oneWayLocalForward(this.pdSOAP11Stateful, this.repositoryPortaDelegataOneWayLocalForwardSOAP11Stateful);
	}

	Repository repositoryPortaDelegataOneWayLocalForwardSOAP11Stateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWayLocalForwardSOAP11Stateful")
	public Object[][]testPortaDelegataOneWayLocalForwardSOAP11Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWayLocalForwardSOAP11Stateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"LocalForward",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD.SOAP11.STATEFUL"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWayLocalForwardSOAP11Stateful",dependsOnMethods={"oneWayLocalForward_pdSOAP11Stateful"})
	public void testOneWayLocalForward_pdSOAP11Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		PortaDelegata._testOneWayLocalForward(this.pdSOAP11Stateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"LocalForward",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD.SOAP11.STATELESS"})
	public void oneWayLocalForward_pdSOAP11Stateless() throws TestSuiteException, Exception{
		PortaDelegata._oneWayLocalForward(this.pdSOAP11Stateless, this.repositoryPortaDelegataOneWayLocalForwardSOAP11Stateless);
	}

	Repository repositoryPortaDelegataOneWayLocalForwardSOAP11Stateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWayLocalForwardSOAP11Stateless")
	public Object[][]testPortaDelegataOneWayLocalForwardSOAP11Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWayLocalForwardSOAP11Stateless);
	}
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"LocalForward",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD.SOAP11.STATELESS"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWayLocalForwardSOAP11Stateless",dependsOnMethods={"oneWayLocalForward_pdSOAP11Stateless"})
	public void testOneWayLocalForward_pdSOAP11Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		PortaDelegata._testOneWayLocalForward(this.pdSOAP11Stateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"LocalForward",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD.SOAP12.STATEFUL"})
	public void oneWayLocalForward_pdSOAP12Stateful() throws TestSuiteException, Exception{
		PortaDelegata._oneWayLocalForward(this.pdSOAP12Stateful, this.repositoryPortaDelegataOneWayLocalForwardSOAP12Stateful);
	}

	Repository repositoryPortaDelegataOneWayLocalForwardSOAP12Stateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWayLocalForwardSOAP12Stateful")
	public Object[][]testPortaDelegataOneWayLocalForwardSOAP12Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWayLocalForwardSOAP12Stateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"LocalForward",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD.SOAP12.STATEFUL"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWayLocalForwardSOAP12Stateful",dependsOnMethods={"oneWayLocalForward_pdSOAP12Stateful"})
	public void testOneWayLocalForward_pdSOAP12Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		PortaDelegata._testOneWayLocalForward(this.pdSOAP12Stateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"LocalForward",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD.SOAP12.STATELESS"})
	public void oneWayLocalForward_pdSOAP12Stateless() throws TestSuiteException, Exception{
		PortaDelegata._oneWayLocalForward(this.pdSOAP12Stateless, this.repositoryPortaDelegataOneWayLocalForwardSOAP12Stateless);
	}

	Repository repositoryPortaDelegataOneWayLocalForwardSOAP12Stateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWayLocalForwardSOAP12Stateless")
	public Object[][]testPortaDelegataOneWayLocalForwardSOAP12Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWayLocalForwardSOAP12Stateless);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"LocalForward",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD.SOAP12.STATELESS"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWayLocalForwardSOAP12Stateless",dependsOnMethods={"oneWayLocalForward_pdSOAP12Stateless"})
	public void testOneWayLocalForward_pdSOAP12Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		PortaDelegata._testOneWayLocalForward(this.pdSOAP12Stateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"LocalForward",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD.SOAP11.STATEFUL.ATTACHMENTS"})
	public void oneWayLocalForward_pdSOAP11WithAttachmentsStateful() throws TestSuiteException, Exception{
		PortaDelegata._oneWayLocalForward(this.pdSOAP11WithAttachmentsStateful, this.repositoryPortaDelegataOneWayLocalForwardSOAP11WithAttachmentsStateful);
	}

	Repository repositoryPortaDelegataOneWayLocalForwardSOAP11WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWayLocalForwardSOAP11WithAttachmentsStateful")
	public Object[][]testPortaDelegataOneWayLocalForwardSOAP11WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWayLocalForwardSOAP11WithAttachmentsStateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"LocalForward",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD.SOAP11.STATEFUL.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWayLocalForwardSOAP11WithAttachmentsStateful",dependsOnMethods={"oneWayLocalForward_pdSOAP11WithAttachmentsStateful"})
	public void testOneWayLocalForward_pdSOAP11WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		PortaDelegata._testOneWayLocalForward(this.pdSOAP11WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"LocalForward",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD.SOAP11.STATELESS.ATTACHMENTS"})
	public void oneWayLocalForward_pdSOAP11WithAttachmentsStateless() throws TestSuiteException, Exception{
		PortaDelegata._oneWayLocalForward(this.pdSOAP11WithAttachmentsStateless, this.repositoryPortaDelegataOneWayLocalForwardSOAP11WithAttachmentsStateless);
	}

	Repository repositoryPortaDelegataOneWayLocalForwardSOAP11WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWayLocalForwardSOAP11WithAttachmentsStateless")
	public Object[][]testPortaDelegataOneWayLocalForwardSOAP11WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWayLocalForwardSOAP11WithAttachmentsStateless);
	}
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"LocalForward",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD.SOAP11.STATELESS.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWayLocalForwardSOAP11WithAttachmentsStateless",dependsOnMethods={"oneWayLocalForward_pdSOAP11WithAttachmentsStateless"})
	public void testOneWayLocalForward_pdSOAP11WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		PortaDelegata._testOneWayLocalForward(this.pdSOAP11WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"LocalForward",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD.SOAP12.STATEFUL.ATTACHMENTS"})
	public void oneWayLocalForward_pdSOAP12WithAttachmentsStateful() throws TestSuiteException, Exception{
		PortaDelegata._oneWayLocalForward(this.pdSOAP12WithAttachmentsStateful, this.repositoryPortaDelegataOneWayLocalForwardSOAP12WithAttachmentsStateful);
	}

	Repository repositoryPortaDelegataOneWayLocalForwardSOAP12WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWayLocalForwardSOAP12WithAttachmentsStateful")
	public Object[][]testPortaDelegataOneWayLocalForwardSOAP12WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWayLocalForwardSOAP12WithAttachmentsStateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"LocalForward",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD.SOAP12.STATEFUL.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWayLocalForwardSOAP12WithAttachmentsStateful",dependsOnMethods={"oneWayLocalForward_pdSOAP12WithAttachmentsStateful"})
	public void testOneWayLocalForward_pdSOAP12WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		PortaDelegata._testOneWayLocalForward(this.pdSOAP12WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"LocalForward",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD.SOAP12.STATELESS.ATTACHMENTS"})
	public void oneWayLocalForward_pdSOAP12WithAttachmentsStateless() throws TestSuiteException, Exception{
		PortaDelegata._oneWayLocalForward(this.pdSOAP12WithAttachmentsStateless, this.repositoryPortaDelegataOneWayLocalForwardSOAP12WithAttachmentsStateless);
	}

	Repository repositoryPortaDelegataOneWayLocalForwardSOAP12WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWayLocalForwardSOAP12WithAttachmentsStateless")
	public Object[][]testPortaDelegataOneWayLocalForwardSOAP12WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWayLocalForwardSOAP12WithAttachmentsStateless);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"LocalForward",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY_LOCAL_FORWARD.SOAP12.STATELESS.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWayLocalForwardSOAP12WithAttachmentsStateless",dependsOnMethods={"oneWayLocalForward_pdSOAP12WithAttachmentsStateless"})
	public void testOneWayLocalForward_pdSOAP12WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		PortaDelegata._testOneWayLocalForward(this.pdSOAP12WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo);
	}	

	/* TEST SINCRONO LOCAL_FORWARD */
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"LocalForward",PortaDelegata.ID_GRUPPO+".SINCRONO_LOCAL_FORWARD",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO_LOCAL_FORWARD.SOAP11.STATELESS"})
	public void sincronoLocalForward_pdSOAP11Stateless() throws TestSuiteException, Exception{
		PortaDelegata._sincronoLocalForward(this.pdSOAP11Stateless, this.repositoryPortaDelegataSincronoLocalForwardSOAP11Stateless);
	}

	Repository repositoryPortaDelegataSincronoLocalForwardSOAP11Stateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoLocalForwardSOAP11Stateless")
	public Object[][]testPortaDelegataSincronoLocalForwardSOAP11Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoLocalForwardSOAP11Stateless);
	}
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"LocalForward",PortaDelegata.ID_GRUPPO+".SINCRONO_LOCAL_FORWARD",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO_LOCAL_FORWARD.SOAP11.STATELESS"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoLocalForwardSOAP11Stateless",dependsOnMethods={"sincronoLocalForward_pdSOAP11Stateless"})
	public void testSincronoLocalForward_pdSOAP11Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		PortaDelegata._testSincronoLocalForward(this.pdSOAP11Stateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"LocalForward",PortaDelegata.ID_GRUPPO+".SINCRONO_LOCAL_FORWARD",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO_LOCAL_FORWARD.SOAP12.STATELESS"})
	public void sincronoLocalForward_pdSOAP12Stateless() throws TestSuiteException, Exception{
		PortaDelegata._sincronoLocalForward(this.pdSOAP12Stateless, this.repositoryPortaDelegataSincronoLocalForwardSOAP12Stateless);
	}

	Repository repositoryPortaDelegataSincronoLocalForwardSOAP12Stateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoLocalForwardSOAP12Stateless")
	public Object[][]testPortaDelegataSincronoLocalForwardSOAP12Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoLocalForwardSOAP12Stateless);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"LocalForward",PortaDelegata.ID_GRUPPO+".SINCRONO_LOCAL_FORWARD",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO_LOCAL_FORWARD.SOAP12.STATELESS"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoLocalForwardSOAP12Stateless",dependsOnMethods={"sincronoLocalForward_pdSOAP12Stateless"})
	public void testSincronoLocalForward_pdSOAP12Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		PortaDelegata._testSincronoLocalForward(this.pdSOAP12Stateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"LocalForward",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO_LOCAL_FORWARD",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO_LOCAL_FORWARD.SOAP11.STATELESS.ATTACHMENTS"})
	public void sincronoLocalForward_pdSOAP11WithAttachmentsStateless() throws TestSuiteException, Exception{
		PortaDelegata._sincronoLocalForward(this.pdSOAP11WithAttachmentsStateless, this.repositoryPortaDelegataSincronoLocalForwardSOAP11WithAttachmentsStateless);
	}

	Repository repositoryPortaDelegataSincronoLocalForwardSOAP11WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoLocalForwardSOAP11WithAttachmentsStateless")
	public Object[][]testPortaDelegataSincronoLocalForwardSOAP11WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoLocalForwardSOAP11WithAttachmentsStateless);
	}
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"LocalForward",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO_LOCAL_FORWARD",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO_LOCAL_FORWARD.SOAP11.STATELESS.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoLocalForwardSOAP11WithAttachmentsStateless",dependsOnMethods={"sincronoLocalForward_pdSOAP11WithAttachmentsStateless"})
	public void testSincronoLocalForward_pdSOAP11WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		PortaDelegata._testSincronoLocalForward(this.pdSOAP11WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"LocalForward",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO_LOCAL_FORWARD",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO_LOCAL_FORWARD.SOAP12.STATELESS.ATTACHMENTS"})
	public void sincronoLocalForward_pdSOAP12WithAttachmentsStateless() throws TestSuiteException, Exception{
		PortaDelegata._sincronoLocalForward(this.pdSOAP12WithAttachmentsStateless, this.repositoryPortaDelegataSincronoLocalForwardSOAP12WithAttachmentsStateless);
	}

	Repository repositoryPortaDelegataSincronoLocalForwardSOAP12WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoLocalForwardSOAP12WithAttachmentsStateless")
	public Object[][]testPortaDelegataSincronoLocalForwardSOAP12WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoLocalForwardSOAP12WithAttachmentsStateless);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"LocalForward",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO_LOCAL_FORWARD",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO_LOCAL_FORWARD.SOAP12.STATELESS.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoLocalForwardSOAP12WithAttachmentsStateless",dependsOnMethods={"sincronoLocalForward_pdSOAP12WithAttachmentsStateless"})
	public void testSincronoLocalForward_pdSOAP12WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		PortaDelegata._testSincronoLocalForward(this.pdSOAP12WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo);
	}

}
