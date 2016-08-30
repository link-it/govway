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
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.PortaApplicativa;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.PortaImpl;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * PortaApplicativaNonAutenticato
 * 
 * @author Andreea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaApplicativaNonAutenticato extends PortaImpl {

	/* TEST ONE WAY */
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".ONEWAY",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".ONEWAY.SOAP11.STATEFUL"})
	public void oneWay_paSOAP11Stateful() throws TestSuiteException, Exception{
		Porta._oneWay(this.paSOAP11Stateful, this.repositoryPortaApplicativaOneWaySOAP11Stateful);
	}

	Repository repositoryPortaApplicativaOneWaySOAP11Stateful=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"OneWaySOAP11Stateful")
	public Object[][]testPortaApplicativaOneWaySOAP11Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaOneWaySOAP11Stateful);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".ONEWAY",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".ONEWAY.SOAP11.STATEFUL"},dataProvider=PortaApplicativa.ID_GRUPPO+"OneWaySOAP11Stateful",dependsOnMethods={"oneWay_paSOAP11Stateful"})
	public void testOneWay_paSOAP11Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWay(this.paSOAP11Stateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".ONEWAY",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".ONEWAY.SOAP11.STATELESS"})
	public void oneWay_paSOAP11Stateless() throws TestSuiteException, Exception{
		Porta._oneWay(this.paSOAP11Stateless, this.repositoryPortaApplicativaOneWaySOAP11Stateless);
	}

	Repository repositoryPortaApplicativaOneWaySOAP11Stateless=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"OneWaySOAP11Stateless")
	public Object[][]testPortaApplicativaOneWaySOAP11Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaOneWaySOAP11Stateless);
	}
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".ONEWAY",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".ONEWAY.SOAP11.STATELESS"},dataProvider=PortaApplicativa.ID_GRUPPO+"OneWaySOAP11Stateless",dependsOnMethods={"oneWay_paSOAP11Stateless"})
	public void testOneWay_paSOAP11Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWay(this.paSOAP11Stateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".ONEWAY",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".ONEWAY.SOAP12.STATEFUL"})
	public void oneWay_paSOAP12Stateful() throws TestSuiteException, Exception{
		Porta._oneWay(this.paSOAP12Stateful, this.repositoryPortaApplicativaOneWaySOAP12Stateful);
	}

	Repository repositoryPortaApplicativaOneWaySOAP12Stateful=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"OneWaySOAP12Stateful")
	public Object[][]testPortaApplicativaOneWaySOAP12Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaOneWaySOAP12Stateful);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".ONEWAY",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".ONEWAY.SOAP12.STATEFUL"},dataProvider=PortaApplicativa.ID_GRUPPO+"OneWaySOAP12Stateful",dependsOnMethods={"oneWay_paSOAP12Stateful"})
	public void testOneWay_paSOAP12Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWay(this.paSOAP12Stateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".ONEWAY",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".ONEWAY.SOAP12.STATELESS"})
	public void oneWay_paSOAP12Stateless() throws TestSuiteException, Exception{
		Porta._oneWay(this.paSOAP12Stateless, this.repositoryPortaApplicativaOneWaySOAP12Stateless);
	}

	Repository repositoryPortaApplicativaOneWaySOAP12Stateless=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"OneWaySOAP12Stateless")
	public Object[][]testPortaApplicativaOneWaySOAP12Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaOneWaySOAP12Stateless);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".ONEWAY",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".ONEWAY.SOAP12.STATELESS"},dataProvider=PortaApplicativa.ID_GRUPPO+"OneWaySOAP12Stateless",dependsOnMethods={"oneWay_paSOAP12Stateless"})
	public void testOneWay_paSOAP12Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWay(this.paSOAP12Stateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".ONEWAY",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".ONEWAY.SOAP11.STATEFUL.ATTACHMENTS"})
	public void oneWay_paSOAP11WithAttachmentsStateful() throws TestSuiteException, Exception{
		Porta._oneWay(this.paSOAP11WithAttachmentsStateful, this.repositoryPortaApplicativaOneWaySOAP11WithAttachmentsStateful);
	}

	Repository repositoryPortaApplicativaOneWaySOAP11WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"OneWaySOAP11WithAttachmentsStateful")
	public Object[][]testPortaApplicativaOneWaySOAP11WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaOneWaySOAP11WithAttachmentsStateful);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".ONEWAY",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".ONEWAY.SOAP11.STATEFUL.ATTACHMENTS"},dataProvider=PortaApplicativa.ID_GRUPPO+"OneWaySOAP11WithAttachmentsStateful",dependsOnMethods={"oneWay_paSOAP11WithAttachmentsStateful"})
	public void testOneWay_paSOAP11WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWay(this.paSOAP11WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".ONEWAY",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".ONEWAY.SOAP11.STATELESS.ATTACHMENTS"})
	public void oneWay_paSOAP11WithAttachmentsStateless() throws TestSuiteException, Exception{
		Porta._oneWay(this.paSOAP11WithAttachmentsStateless, this.repositoryPortaApplicativaOneWaySOAP11WithAttachmentsStateless);
	}

	Repository repositoryPortaApplicativaOneWaySOAP11WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"OneWaySOAP11WithAttachmentsStateless")
	public Object[][]testPortaApplicativaOneWaySOAP11WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaOneWaySOAP11WithAttachmentsStateless);
	}
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".ONEWAY",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".ONEWAY.SOAP11.STATELESS.ATTACHMENTS"},dataProvider=PortaApplicativa.ID_GRUPPO+"OneWaySOAP11WithAttachmentsStateless",dependsOnMethods={"oneWay_paSOAP11WithAttachmentsStateless"})
	public void testOneWay_paSOAP11WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWay(this.paSOAP11WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".ONEWAY",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".ONEWAY.SOAP12.STATEFUL.ATTACHMENTS"})
	public void oneWay_paSOAP12WithAttachmentsStateful() throws TestSuiteException, Exception{
		Porta._oneWay(this.paSOAP12WithAttachmentsStateful, this.repositoryPortaApplicativaOneWaySOAP12WithAttachmentsStateful);
	}

	Repository repositoryPortaApplicativaOneWaySOAP12WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"OneWaySOAP12WithAttachmentsStateful")
	public Object[][]testPortaApplicativaOneWaySOAP12WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaOneWaySOAP12WithAttachmentsStateful);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".ONEWAY",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".ONEWAY.SOAP12.STATEFUL.ATTACHMENTS"},dataProvider=PortaApplicativa.ID_GRUPPO+"OneWaySOAP12WithAttachmentsStateful",dependsOnMethods={"oneWay_paSOAP12WithAttachmentsStateful"})
	public void testOneWay_paSOAP12WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWay(this.paSOAP12WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".ONEWAY",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".ONEWAY.SOAP12.STATELESS.ATTACHMENTS"})
	public void oneWay_paSOAP12WithAttachmentsStateless() throws TestSuiteException, Exception{
		Porta._oneWay(this.paSOAP12WithAttachmentsStateless, this.repositoryPortaApplicativaOneWaySOAP12WithAttachmentsStateless);
	}

	Repository repositoryPortaApplicativaOneWaySOAP12WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"OneWaySOAP12WithAttachmentsStateless")
	public Object[][]testPortaApplicativaOneWaySOAP12WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaOneWaySOAP12WithAttachmentsStateless);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".ONEWAY",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".ONEWAY.SOAP12.STATELESS.ATTACHMENTS"},dataProvider=PortaApplicativa.ID_GRUPPO+"OneWaySOAP12WithAttachmentsStateless",dependsOnMethods={"oneWay_paSOAP12WithAttachmentsStateless"})
	public void testOneWay_paSOAP12WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWay(this.paSOAP12WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo);
	}	

	/* TEST SINCRONO */
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".SINCRONO",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".SINCRONO.SOAP11.STATEFUL"})
	public void sincrono_paSOAP11Stateful() throws TestSuiteException, Exception{
		Porta._sincrono(this.paSOAP11Stateful, this.repositoryPortaApplicativaSincronoSOAP11Stateful);
	}

	Repository repositoryPortaApplicativaSincronoSOAP11Stateful=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"SincronoSOAP11Stateful")
	public Object[][]testPortaApplicativaSincronoSOAP11Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaSincronoSOAP11Stateful);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".SINCRONO",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".SINCRONO.SOAP11.STATEFUL"},dataProvider=PortaApplicativa.ID_GRUPPO+"SincronoSOAP11Stateful",dependsOnMethods={"sincrono_paSOAP11Stateful"})
	public void testSincrono_paSOAP11Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincrono(this.paSOAP11Stateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".SINCRONO",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".SINCRONO.SOAP11.STATELESS"})
	public void sincrono_paSOAP11Stateless() throws TestSuiteException, Exception{
		Porta._sincrono(this.paSOAP11Stateless, this.repositoryPortaApplicativaSincronoSOAP11Stateless);
	}

	Repository repositoryPortaApplicativaSincronoSOAP11Stateless=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"SincronoSOAP11Stateless")
	public Object[][]testPortaApplicativaSincronoSOAP11Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaSincronoSOAP11Stateless);
	}
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".SINCRONO",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".SINCRONO.SOAP11.STATELESS"},dataProvider=PortaApplicativa.ID_GRUPPO+"SincronoSOAP11Stateless",dependsOnMethods={"sincrono_paSOAP11Stateless"})
	public void testSincrono_paSOAP11Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincrono(this.paSOAP11Stateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".SINCRONO",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".SINCRONO.SOAP12.STATEFUL"})
	public void sincrono_paSOAP12Stateful() throws TestSuiteException, Exception{
		Porta._sincrono(this.paSOAP12Stateful, this.repositoryPortaApplicativaSincronoSOAP12Stateful);
	}

	Repository repositoryPortaApplicativaSincronoSOAP12Stateful=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"SincronoSOAP12Stateful")
	public Object[][]testPortaApplicativaSincronoSOAP12Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaSincronoSOAP12Stateful);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".SINCRONO",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".SINCRONO.SOAP12.STATEFUL"},dataProvider=PortaApplicativa.ID_GRUPPO+"SincronoSOAP12Stateful",dependsOnMethods={"sincrono_paSOAP12Stateful"})
	public void testSincrono_paSOAP12Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincrono(this.paSOAP12Stateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".SINCRONO",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".SINCRONO.SOAP12.STATELESS"})
	public void sincrono_paSOAP12Stateless() throws TestSuiteException, Exception{
		Porta._sincrono(this.paSOAP12Stateless, this.repositoryPortaApplicativaSincronoSOAP12Stateless);
	}

	Repository repositoryPortaApplicativaSincronoSOAP12Stateless=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"SincronoSOAP12Stateless")
	public Object[][]testPortaApplicativaSincronoSOAP12Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaSincronoSOAP12Stateless);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".SINCRONO",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".SINCRONO.SOAP12.STATELESS"},dataProvider=PortaApplicativa.ID_GRUPPO+"SincronoSOAP12Stateless",dependsOnMethods={"sincrono_paSOAP12Stateless"})
	public void testSincrono_paSOAP12Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincrono(this.paSOAP12Stateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".SINCRONO",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".SINCRONO.SOAP11.STATEFUL.ATTACHMENTS"})
	public void sincrono_paSOAP11WithAttachmentsStateful() throws TestSuiteException, Exception{
		Porta._sincrono(this.paSOAP11WithAttachmentsStateful, this.repositoryPortaApplicativaSincronoSOAP11WithAttachmentsStateful);
	}

	Repository repositoryPortaApplicativaSincronoSOAP11WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"SincronoSOAP11WithAttachmentsStateful")
	public Object[][]testPortaApplicativaSincronoSOAP11WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaSincronoSOAP11WithAttachmentsStateful);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".SINCRONO",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".SINCRONO.SOAP11.STATEFUL.ATTACHMENTS"},dataProvider=PortaApplicativa.ID_GRUPPO+"SincronoSOAP11WithAttachmentsStateful",dependsOnMethods={"sincrono_paSOAP11WithAttachmentsStateful"})
	public void testSincrono_paSOAP11WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincrono(this.paSOAP11WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".SINCRONO",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".SINCRONO.SOAP11.STATELESS.ATTACHMENTS"})
	public void sincrono_paSOAP11WithAttachmentsStateless() throws TestSuiteException, Exception{
		Porta._sincrono(this.paSOAP11WithAttachmentsStateless, this.repositoryPortaApplicativaSincronoSOAP11WithAttachmentsStateless);
	}

	Repository repositoryPortaApplicativaSincronoSOAP11WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"SincronoSOAP11WithAttachmentsStateless")
	public Object[][]testPortaApplicativaSincronoSOAP11WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaSincronoSOAP11WithAttachmentsStateless);
	}
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".SINCRONO",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".SINCRONO.SOAP11.STATELESS.ATTACHMENTS"},dataProvider=PortaApplicativa.ID_GRUPPO+"SincronoSOAP11WithAttachmentsStateless",dependsOnMethods={"sincrono_paSOAP11WithAttachmentsStateless"})
	public void testSincrono_paSOAP11WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincrono(this.paSOAP11WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".SINCRONO",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".SINCRONO.SOAP12.STATEFUL.ATTACHMENTS"})
	public void sincrono_paSOAP12WithAttachmentsStateful() throws TestSuiteException, Exception{
		Porta._sincrono(this.paSOAP12WithAttachmentsStateful, this.repositoryPortaApplicativaSincronoSOAP12WithAttachmentsStateful);
	}

	Repository repositoryPortaApplicativaSincronoSOAP12WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"SincronoSOAP12WithAttachmentsStateful")
	public Object[][]testPortaApplicativaSincronoSOAP12WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaSincronoSOAP12WithAttachmentsStateful);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".SINCRONO",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".SINCRONO.SOAP12.STATEFUL.ATTACHMENTS"},dataProvider=PortaApplicativa.ID_GRUPPO+"SincronoSOAP12WithAttachmentsStateful",dependsOnMethods={"sincrono_paSOAP12WithAttachmentsStateful"})
	public void testSincrono_paSOAP12WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincrono(this.paSOAP12WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo);
	}	

	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".SINCRONO",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".SINCRONO.SOAP12.STATELESS.ATTACHMENTS"})
	public void sincrono_paSOAP12WithAttachmentsStateless() throws TestSuiteException, Exception{
		Porta._sincrono(this.paSOAP12WithAttachmentsStateless, this.repositoryPortaApplicativaSincronoSOAP12WithAttachmentsStateless);
	}

	Repository repositoryPortaApplicativaSincronoSOAP12WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"SincronoSOAP12WithAttachmentsStateless")
	public Object[][]testPortaApplicativaSincronoSOAP12WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaSincronoSOAP12WithAttachmentsStateless);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"NonAutenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".SINCRONO",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".SINCRONO.SOAP12.STATELESS.ATTACHMENTS"},dataProvider=PortaApplicativa.ID_GRUPPO+"SincronoSOAP12WithAttachmentsStateless",dependsOnMethods={"sincrono_paSOAP12WithAttachmentsStateless"})
	public void testSincrono_paSOAP12WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincrono(this.paSOAP12WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo);
	}	
	

}
