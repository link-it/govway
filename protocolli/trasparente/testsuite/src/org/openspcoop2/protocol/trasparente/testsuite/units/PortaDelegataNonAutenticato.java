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
 * PortaDelegataNonAutenticato
 * 
 * @author Andreea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaDelegataNonAutenticato extends PortaImpl {

	/* TEST ONE WAY */
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".ONEWAY",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY.SOAP11.STATEFUL"})
	public void oneWay_pdSOAP11Stateful() throws TestSuiteException, Exception{
		Porta._oneWay(this.pdSOAP11Stateful, this.repositoryPortaDelegataOneWaySOAP11Stateful);
	}

	Repository repositoryPortaDelegataOneWaySOAP11Stateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWaySOAP11Stateful")
	public Object[][]testPortaDelegataOneWaySOAP11Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWaySOAP11Stateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".ONEWAY",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY.SOAP11.STATEFUL"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWaySOAP11Stateful",dependsOnMethods={"oneWay_pdSOAP11Stateful"})
	public void testOneWay_pdSOAP11Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWay(this.pdSOAP11Stateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".ONEWAY",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY.SOAP11.STATELESS"})
	public void oneWay_pdSOAP11Stateless() throws TestSuiteException, Exception{
		Porta._oneWay(this.pdSOAP11Stateless, this.repositoryPortaDelegataOneWaySOAP11Stateless);
	}

	Repository repositoryPortaDelegataOneWaySOAP11Stateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWaySOAP11Stateless")
	public Object[][]testPortaDelegataOneWaySOAP11Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWaySOAP11Stateless);
	}
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".ONEWAY",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY.SOAP11.STATELESS"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWaySOAP11Stateless",dependsOnMethods={"oneWay_pdSOAP11Stateless"})
	public void testOneWay_pdSOAP11Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWay(this.pdSOAP11Stateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".ONEWAY",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY.SOAP12.STATEFUL"})
	public void oneWay_pdSOAP12Stateful() throws TestSuiteException, Exception{
		Porta._oneWay(this.pdSOAP12Stateful, this.repositoryPortaDelegataOneWaySOAP12Stateful);
	}

	Repository repositoryPortaDelegataOneWaySOAP12Stateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWaySOAP12Stateful")
	public Object[][]testPortaDelegataOneWaySOAP12Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWaySOAP12Stateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".ONEWAY",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY.SOAP12.STATEFUL"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWaySOAP12Stateful",dependsOnMethods={"oneWay_pdSOAP12Stateful"})
	public void testOneWay_pdSOAP12Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWay(this.pdSOAP12Stateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".ONEWAY",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY.SOAP12.STATELESS"})
	public void oneWay_pdSOAP12Stateless() throws TestSuiteException, Exception{
		Porta._oneWay(this.pdSOAP12Stateless, this.repositoryPortaDelegataOneWaySOAP12Stateless);
	}

	Repository repositoryPortaDelegataOneWaySOAP12Stateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWaySOAP12Stateless")
	public Object[][]testPortaDelegataOneWaySOAP12Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWaySOAP12Stateless);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".ONEWAY",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY.SOAP12.STATELESS"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWaySOAP12Stateless",dependsOnMethods={"oneWay_pdSOAP12Stateless"})
	public void testOneWay_pdSOAP12Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWay(this.pdSOAP12Stateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY.SOAP11.STATEFUL.ATTACHMENTS"})
	public void oneWay_pdSOAP11WithAttachmentsStateful() throws TestSuiteException, Exception{
		Porta._oneWay(this.pdSOAP11WithAttachmentsStateful, this.repositoryPortaDelegataOneWaySOAP11WithAttachmentsStateful);
	}

	Repository repositoryPortaDelegataOneWaySOAP11WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWaySOAP11WithAttachmentsStateful")
	public Object[][]testPortaDelegataOneWaySOAP11WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWaySOAP11WithAttachmentsStateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY.SOAP11.STATEFUL.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWaySOAP11WithAttachmentsStateful",dependsOnMethods={"oneWay_pdSOAP11WithAttachmentsStateful"})
	public void testOneWay_pdSOAP11WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWay(this.pdSOAP11WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY.SOAP11.STATELESS.ATTACHMENTS"})
	public void oneWay_pdSOAP11WithAttachmentsStateless() throws TestSuiteException, Exception{
		Porta._oneWay(this.pdSOAP11WithAttachmentsStateless, this.repositoryPortaDelegataOneWaySOAP11WithAttachmentsStateless);
	}

	Repository repositoryPortaDelegataOneWaySOAP11WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWaySOAP11WithAttachmentsStateless")
	public Object[][]testPortaDelegataOneWaySOAP11WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWaySOAP11WithAttachmentsStateless);
	}
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY.SOAP11.STATELESS.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWaySOAP11WithAttachmentsStateless",dependsOnMethods={"oneWay_pdSOAP11WithAttachmentsStateless"})
	public void testOneWay_pdSOAP11WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWay(this.pdSOAP11WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY.SOAP12.STATEFUL.ATTACHMENTS"})
	public void oneWay_pdSOAP12WithAttachmentsStateful() throws TestSuiteException, Exception{
		Porta._oneWay(this.pdSOAP12WithAttachmentsStateful, this.repositoryPortaDelegataOneWaySOAP12WithAttachmentsStateful);
	}

	Repository repositoryPortaDelegataOneWaySOAP12WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWaySOAP12WithAttachmentsStateful")
	public Object[][]testPortaDelegataOneWaySOAP12WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWaySOAP12WithAttachmentsStateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY.SOAP12.STATEFUL.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWaySOAP12WithAttachmentsStateful",dependsOnMethods={"oneWay_pdSOAP12WithAttachmentsStateful"})
	public void testOneWay_pdSOAP12WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWay(this.pdSOAP12WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY.SOAP12.STATELESS.ATTACHMENTS"})
	public void oneWay_pdSOAP12WithAttachmentsStateless() throws TestSuiteException, Exception{
		Porta._oneWay(this.pdSOAP12WithAttachmentsStateless, this.repositoryPortaDelegataOneWaySOAP12WithAttachmentsStateless);
	}

	Repository repositoryPortaDelegataOneWaySOAP12WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWaySOAP12WithAttachmentsStateless")
	public Object[][]testPortaDelegataOneWaySOAP12WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWaySOAP12WithAttachmentsStateless);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY.SOAP12.STATELESS.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWaySOAP12WithAttachmentsStateless",dependsOnMethods={"oneWay_pdSOAP12WithAttachmentsStateless"})
	public void testOneWay_pdSOAP12WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWay(this.pdSOAP12WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo);
	}	

	/* TEST SINCRONO */
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".SINCRONO",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO.SOAP11.STATEFUL"})
	public void sincrono_pdSOAP11Stateful() throws TestSuiteException, Exception{
		Porta._sincrono(this.pdSOAP11Stateful, this.repositoryPortaDelegataSincronoSOAP11Stateful);
	}

	Repository repositoryPortaDelegataSincronoSOAP11Stateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoSOAP11Stateful")
	public Object[][]testPortaDelegataSincronoSOAP11Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoSOAP11Stateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".SINCRONO",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO.SOAP11.STATEFUL"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoSOAP11Stateful",dependsOnMethods={"sincrono_pdSOAP11Stateful"})
	public void testSincrono_pdSOAP11Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincrono(this.pdSOAP11Stateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".SINCRONO",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO.SOAP11.STATELESS"})
	public void sincrono_pdSOAP11Stateless() throws TestSuiteException, Exception{
		Porta._sincrono(this.pdSOAP11Stateless, this.repositoryPortaDelegataSincronoSOAP11Stateless);
	}

	Repository repositoryPortaDelegataSincronoSOAP11Stateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoSOAP11Stateless")
	public Object[][]testPortaDelegataSincronoSOAP11Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoSOAP11Stateless);
	}
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".SINCRONO",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO.SOAP11.STATELESS"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoSOAP11Stateless",dependsOnMethods={"sincrono_pdSOAP11Stateless"})
	public void testSincrono_pdSOAP11Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincrono(this.pdSOAP11Stateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".SINCRONO",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO.SOAP12.STATEFUL"})
	public void sincrono_pdSOAP12Stateful() throws TestSuiteException, Exception{
		Porta._sincrono(this.pdSOAP12Stateful, this.repositoryPortaDelegataSincronoSOAP12Stateful);
	}

	Repository repositoryPortaDelegataSincronoSOAP12Stateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoSOAP12Stateful")
	public Object[][]testPortaDelegataSincronoSOAP12Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoSOAP12Stateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".SINCRONO",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO.SOAP12.STATEFUL"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoSOAP12Stateful",dependsOnMethods={"sincrono_pdSOAP12Stateful"})
	public void testSincrono_pdSOAP12Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincrono(this.pdSOAP12Stateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".SINCRONO",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO.SOAP12.STATELESS"})
	public void sincrono_pdSOAP12Stateless() throws TestSuiteException, Exception{
		Porta._sincrono(this.pdSOAP12Stateless, this.repositoryPortaDelegataSincronoSOAP12Stateless);
	}

	Repository repositoryPortaDelegataSincronoSOAP12Stateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoSOAP12Stateless")
	public Object[][]testPortaDelegataSincronoSOAP12Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoSOAP12Stateless);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".SINCRONO",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO.SOAP12.STATELESS"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoSOAP12Stateless",dependsOnMethods={"sincrono_pdSOAP12Stateless"})
	public void testSincrono_pdSOAP12Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincrono(this.pdSOAP12Stateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO.SOAP11.STATEFUL.ATTACHMENTS"})
	public void sincrono_pdSOAP11WithAttachmentsStateful() throws TestSuiteException, Exception{
		Porta._sincrono(this.pdSOAP11WithAttachmentsStateful, this.repositoryPortaDelegataSincronoSOAP11WithAttachmentsStateful);
	}

	Repository repositoryPortaDelegataSincronoSOAP11WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoSOAP11WithAttachmentsStateful")
	public Object[][]testPortaDelegataSincronoSOAP11WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoSOAP11WithAttachmentsStateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO.SOAP11.STATEFUL.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoSOAP11WithAttachmentsStateful",dependsOnMethods={"sincrono_pdSOAP11WithAttachmentsStateful"})
	public void testSincrono_pdSOAP11WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincrono(this.pdSOAP11WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO.SOAP11.STATELESS.ATTACHMENTS"})
	public void sincrono_pdSOAP11WithAttachmentsStateless() throws TestSuiteException, Exception{
		Porta._sincrono(this.pdSOAP11WithAttachmentsStateless, this.repositoryPortaDelegataSincronoSOAP11WithAttachmentsStateless);
	}

	Repository repositoryPortaDelegataSincronoSOAP11WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoSOAP11WithAttachmentsStateless")
	public Object[][]testPortaDelegataSincronoSOAP11WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoSOAP11WithAttachmentsStateless);
	}
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO.SOAP11.STATELESS.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoSOAP11WithAttachmentsStateless",dependsOnMethods={"sincrono_pdSOAP11WithAttachmentsStateless"})
	public void testSincrono_pdSOAP11WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincrono(this.pdSOAP11WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO.SOAP12.STATEFUL.ATTACHMENTS"})
	public void sincrono_pdSOAP12WithAttachmentsStateful() throws TestSuiteException, Exception{
		Porta._sincrono(this.pdSOAP12WithAttachmentsStateful, this.repositoryPortaDelegataSincronoSOAP12WithAttachmentsStateful);
	}

	Repository repositoryPortaDelegataSincronoSOAP12WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoSOAP12WithAttachmentsStateful")
	public Object[][]testPortaDelegataSincronoSOAP12WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoSOAP12WithAttachmentsStateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO.SOAP12.STATEFUL.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoSOAP12WithAttachmentsStateful",dependsOnMethods={"sincrono_pdSOAP12WithAttachmentsStateful"})
	public void testSincrono_pdSOAP12WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincrono(this.pdSOAP12WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO.SOAP12.STATELESS.ATTACHMENTS"})
	public void sincrono_pdSOAP12WithAttachmentsStateless() throws TestSuiteException, Exception{
		Porta._sincrono(this.pdSOAP12WithAttachmentsStateless, this.repositoryPortaDelegataSincronoSOAP12WithAttachmentsStateless);
	}

	Repository repositoryPortaDelegataSincronoSOAP12WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoSOAP12WithAttachmentsStateless")
	public Object[][]testPortaDelegataSincronoSOAP12WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoSOAP12WithAttachmentsStateless);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"NonAutenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO.SOAP12.STATELESS.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoSOAP12WithAttachmentsStateless",dependsOnMethods={"sincrono_pdSOAP12WithAttachmentsStateless"})
	public void testSincrono_pdSOAP12WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincrono(this.pdSOAP12WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	

}
