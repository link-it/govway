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

import java.util.Date;

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
 * PortaDelegataAutenticato
 * 
 * @author Andreea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaDelegataAutenticato extends PortaImpl {

	
	/* TEST ONE WAY AUTENTICATO */

	Date date_oneWayAutenticato_pdSOAP11Stateful;
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP11.STATEFUL"})
	public void oneWayAutenticato_pdSOAP11Stateful() throws TestSuiteException, Exception{
		this.date_oneWayAutenticato_pdSOAP11Stateful = new Date();
		Porta._oneWayAutenticato(this.pdSOAP11Stateful, this.repositoryPortaDelegataOneWayAutenticatoSOAP11Stateful);
	}

	Repository repositoryPortaDelegataOneWayAutenticatoSOAP11Stateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWayAutenticatoSOAP11Stateful")
	public Object[][]testPortaDelegataOneWayAutenticatoSOAP11Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWayAutenticatoSOAP11Stateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP11.STATEFUL"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWayAutenticatoSOAP11Stateful",dependsOnMethods={"oneWayAutenticato_pdSOAP11Stateful"})
	public void testOneWayAutenticato_pdSOAP11Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWayAutenticato(this.pdSOAP11Stateful, data, msgDiagData, id, checkServizioApplicativo, this.date_oneWayAutenticato_pdSOAP11Stateful);
	}	

	
	Date date_oneWayAutenticato_pdSOAP11Stateless;
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP11.STATELESS"})
	public void oneWayAutenticato_pdSOAP11Stateless() throws TestSuiteException, Exception{
		this.date_oneWayAutenticato_pdSOAP11Stateless = new Date();
		Porta._oneWayAutenticato(this.pdSOAP11Stateless, this.repositoryPortaDelegataOneWayAutenticatoSOAP11Stateless);
	}

	Repository repositoryPortaDelegataOneWayAutenticatoSOAP11Stateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWayAutenticatoSOAP11Stateless")
	public Object[][]testPortaDelegataOneWayAutenticatoSOAP11Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWayAutenticatoSOAP11Stateless);
	}
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP11.STATELESS"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWayAutenticatoSOAP11Stateless",dependsOnMethods={"oneWayAutenticato_pdSOAP11Stateless"})
	public void testOneWayAutenticato_pdSOAP11Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWayAutenticato(this.pdSOAP11Stateless, data, msgDiagData, id, checkServizioApplicativo,this.date_oneWayAutenticato_pdSOAP11Stateless);
	}	
	
	Date date_oneWayAutenticato_pdSOAP12Stateful;
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP12.STATEFUL"})
	public void oneWayAutenticato_pdSOAP12Stateful() throws TestSuiteException, Exception{
		this.date_oneWayAutenticato_pdSOAP12Stateful = new Date();
		Porta._oneWayAutenticato(this.pdSOAP12Stateful, this.repositoryPortaDelegataOneWayAutenticatoSOAP12Stateful);
	}

	Repository repositoryPortaDelegataOneWayAutenticatoSOAP12Stateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWayAutenticatoSOAP12Stateful")
	public Object[][]testPortaDelegataOneWayAutenticatoSOAP12Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWayAutenticatoSOAP12Stateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP12.STATEFUL"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWayAutenticatoSOAP12Stateful",dependsOnMethods={"oneWayAutenticato_pdSOAP12Stateful"})
	public void testOneWayAutenticato_pdSOAP12Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWayAutenticato(this.pdSOAP12Stateful, data, msgDiagData, id, checkServizioApplicativo, this.date_oneWayAutenticato_pdSOAP12Stateful);
	}	

	
	Date date_oneWayAutenticato_pdSOAP12Stateless;
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP12.STATELESS"})
	public void oneWayAutenticato_pdSOAP12Stateless() throws TestSuiteException, Exception{
		this.date_oneWayAutenticato_pdSOAP12Stateless = new Date();
		Porta._oneWayAutenticato(this.pdSOAP12Stateless, this.repositoryPortaDelegataOneWayAutenticatoSOAP12Stateless);
	}

	Repository repositoryPortaDelegataOneWayAutenticatoSOAP12Stateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWayAutenticatoSOAP12Stateless")
	public Object[][]testPortaDelegataOneWayAutenticatoSOAP12Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWayAutenticatoSOAP12Stateless);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP12.STATELESS"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWayAutenticatoSOAP12Stateless",dependsOnMethods={"oneWayAutenticato_pdSOAP12Stateless"})
	public void testOneWayAutenticato_pdSOAP12Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWayAutenticato(this.pdSOAP12Stateless, data, msgDiagData, id, checkServizioApplicativo, this.date_oneWayAutenticato_pdSOAP12Stateless);
	}	
	
	
	Date date_oneWayAutenticato_pdSOAP11WithAttachmentsStateful;
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP11.STATEFUL.ATTACHMENTS"})
	public void oneWayAutenticato_pdSOAP11WithAttachmentsStateful() throws TestSuiteException, Exception{
		this.date_oneWayAutenticato_pdSOAP11WithAttachmentsStateful = new Date();		
		Porta._oneWayAutenticato(this.pdSOAP11WithAttachmentsStateful, this.repositoryPortaDelegataOneWayAutenticatoSOAP11WithAttachmentsStateful);
	}

	Repository repositoryPortaDelegataOneWayAutenticatoSOAP11WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWayAutenticatoSOAP11WithAttachmentsStateful")
	public Object[][]testPortaDelegataOneWayAutenticatoSOAP11WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWayAutenticatoSOAP11WithAttachmentsStateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP11.STATEFUL.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWayAutenticatoSOAP11WithAttachmentsStateful",dependsOnMethods={"oneWayAutenticato_pdSOAP11WithAttachmentsStateful"})
	public void testOneWayAutenticato_pdSOAP11WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWayAutenticato(this.pdSOAP11WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo, this.date_oneWayAutenticato_pdSOAP11WithAttachmentsStateful);
	}	

	
	Date date_oneWayAutenticato_pdSOAP11WithAttachmentsStateless;
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP11.STATELESS.ATTACHMENTS"})
	public void oneWayAutenticato_pdSOAP11WithAttachmentsStateless() throws TestSuiteException, Exception{
		this.date_oneWayAutenticato_pdSOAP11WithAttachmentsStateless = new Date();
		Porta._oneWayAutenticato(this.pdSOAP11WithAttachmentsStateless, this.repositoryPortaDelegataOneWayAutenticatoSOAP11WithAttachmentsStateless);
	}

	Repository repositoryPortaDelegataOneWayAutenticatoSOAP11WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWayAutenticatoSOAP11WithAttachmentsStateless")
	public Object[][]testPortaDelegataOneWayAutenticatoSOAP11WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWayAutenticatoSOAP11WithAttachmentsStateless);
	}
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP11.STATELESS.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWayAutenticatoSOAP11WithAttachmentsStateless",dependsOnMethods={"oneWayAutenticato_pdSOAP11WithAttachmentsStateless"})
	public void testOneWayAutenticato_pdSOAP11WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWayAutenticato(this.pdSOAP11WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo, this.date_oneWayAutenticato_pdSOAP11WithAttachmentsStateless);
	}	
	
	
	Date date_oneWayAutenticato_pdSOAP12WithAttachmentsStateful;
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP12.STATEFUL.ATTACHMENTS"})
	public void oneWayAutenticato_pdSOAP12WithAttachmentsStateful() throws TestSuiteException, Exception{
		this.date_oneWayAutenticato_pdSOAP12WithAttachmentsStateful = new Date();
		Porta._oneWayAutenticato(this.pdSOAP12WithAttachmentsStateful, this.repositoryPortaDelegataOneWayAutenticatoSOAP12WithAttachmentsStateful);
	}

	Repository repositoryPortaDelegataOneWayAutenticatoSOAP12WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWayAutenticatoSOAP12WithAttachmentsStateful")
	public Object[][]testPortaDelegataOneWayAutenticatoSOAP12WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWayAutenticatoSOAP12WithAttachmentsStateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP12.STATEFUL.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWayAutenticatoSOAP12WithAttachmentsStateful",dependsOnMethods={"oneWayAutenticato_pdSOAP12WithAttachmentsStateful"})
	public void testOneWayAutenticato_pdSOAP12WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWayAutenticato(this.pdSOAP12WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo, this.date_oneWayAutenticato_pdSOAP12WithAttachmentsStateful);
	}	

	
	Date date_oneWayAutenticato_pdSOAP12WithAttachmentsStateless;
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP12.STATELESS.ATTACHMENTS"})
	public void oneWayAutenticato_pdSOAP12WithAttachmentsStateless() throws TestSuiteException, Exception{
		this.date_oneWayAutenticato_pdSOAP12WithAttachmentsStateless = new Date();
		Porta._oneWayAutenticato(this.pdSOAP12WithAttachmentsStateless, this.repositoryPortaDelegataOneWayAutenticatoSOAP12WithAttachmentsStateless);
	}

	Repository repositoryPortaDelegataOneWayAutenticatoSOAP12WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"OneWayAutenticatoSOAP12WithAttachmentsStateless")
	public Object[][]testPortaDelegataOneWayAutenticatoSOAP12WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataOneWayAutenticatoSOAP12WithAttachmentsStateless);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP12.STATELESS.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"OneWayAutenticatoSOAP12WithAttachmentsStateless",dependsOnMethods={"oneWayAutenticato_pdSOAP12WithAttachmentsStateless"})
	public void testOneWayAutenticato_pdSOAP12WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWayAutenticato(this.pdSOAP12WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo, this.date_oneWayAutenticato_pdSOAP12WithAttachmentsStateless);
	}	

	/* TEST SINCRONO AUTENTICATO */
	
	Date date_sincronoAutenticato_pdSOAP11Stateful;
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP11.STATEFUL"})
	public void sincronoAutenticato_pdSOAP11Stateful() throws TestSuiteException, Exception{
		this.date_sincronoAutenticato_pdSOAP11Stateful = new Date();
		Porta._sincronoAutenticato(this.pdSOAP11Stateful, this.repositoryPortaDelegataSincronoAutenticatoSOAP11Stateful);
	}

	Repository repositoryPortaDelegataSincronoAutenticatoSOAP11Stateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoAutenticatoSOAP11Stateful")
	public Object[][]testPortaDelegataSincronoAutenticatoSOAP11Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoAutenticatoSOAP11Stateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP11.STATEFUL"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoAutenticatoSOAP11Stateful",dependsOnMethods={"sincronoAutenticato_pdSOAP11Stateful"})
	public void testSincronoAutenticato_pdSOAP11Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoAutenticato(this.pdSOAP11Stateful, data, msgDiagData, id, checkServizioApplicativo, this.date_sincronoAutenticato_pdSOAP11Stateful);
	}	

	
	Date date_sincronoAutenticato_pdSOAP11Stateless;
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP11.STATELESS"})
	public void sincronoAutenticato_pdSOAP11Stateless() throws TestSuiteException, Exception{
		this.date_sincronoAutenticato_pdSOAP11Stateless = new Date();
		Porta._sincronoAutenticato(this.pdSOAP11Stateless, this.repositoryPortaDelegataSincronoAutenticatoSOAP11Stateless);
	}

	Repository repositoryPortaDelegataSincronoAutenticatoSOAP11Stateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoAutenticatoSOAP11Stateless")
	public Object[][]testPortaDelegataSincronoAutenticatoSOAP11Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoAutenticatoSOAP11Stateless);
	}
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP11.STATELESS"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoAutenticatoSOAP11Stateless",dependsOnMethods={"sincronoAutenticato_pdSOAP11Stateless"})
	public void testSincronoAutenticato_pdSOAP11Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoAutenticato(this.pdSOAP11Stateless, data, msgDiagData, id, checkServizioApplicativo, this.date_sincronoAutenticato_pdSOAP11Stateless);
	}	
	
	
	Date date_sincronoAutenticato_pdSOAP12Stateful;
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP12.STATEFUL"})
	public void sincronoAutenticato_pdSOAP12Stateful() throws TestSuiteException, Exception{
		this.date_sincronoAutenticato_pdSOAP12Stateful = new Date();
		Porta._sincronoAutenticato(this.pdSOAP12Stateful, this.repositoryPortaDelegataSincronoAutenticatoSOAP12Stateful);
	}

	Repository repositoryPortaDelegataSincronoAutenticatoSOAP12Stateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoAutenticatoSOAP12Stateful")
	public Object[][]testPortaDelegataSincronoAutenticatoSOAP12Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoAutenticatoSOAP12Stateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP12.STATEFUL"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoAutenticatoSOAP12Stateful",dependsOnMethods={"sincronoAutenticato_pdSOAP12Stateful"})
	public void testSincronoAutenticato_pdSOAP12Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoAutenticato(this.pdSOAP12Stateful, data, msgDiagData, id, checkServizioApplicativo, this.date_sincronoAutenticato_pdSOAP12Stateful);
	}	


	Date date_sincronoAutenticato_pdSOAP12Stateless;
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP12.STATELESS"})
	public void sincronoAutenticato_pdSOAP12Stateless() throws TestSuiteException, Exception{
		this.date_sincronoAutenticato_pdSOAP12Stateless = new Date();
		Porta._sincronoAutenticato(this.pdSOAP12Stateless, this.repositoryPortaDelegataSincronoAutenticatoSOAP12Stateless);
	}

	Repository repositoryPortaDelegataSincronoAutenticatoSOAP12Stateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoAutenticatoSOAP12Stateless")
	public Object[][]testPortaDelegataSincronoAutenticatoSOAP12Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoAutenticatoSOAP12Stateless);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP12.STATELESS"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoAutenticatoSOAP12Stateless",dependsOnMethods={"sincronoAutenticato_pdSOAP12Stateless"})
	public void testSincronoAutenticato_pdSOAP12Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoAutenticato(this.pdSOAP12Stateless, data, msgDiagData, id, checkServizioApplicativo, this.date_sincronoAutenticato_pdSOAP12Stateless);
	}	
	
	
	
	Date date_sincronoAutenticato_pdSOAP11WithAttachmentsStateful;
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP11.STATEFUL.ATTACHMENTS"})
	public void sincronoAutenticato_pdSOAP11WithAttachmentsStateful() throws TestSuiteException, Exception{
		this.date_sincronoAutenticato_pdSOAP11WithAttachmentsStateful = new Date();
		Porta._sincronoAutenticato(this.pdSOAP11WithAttachmentsStateful, this.repositoryPortaDelegataSincronoAutenticatoSOAP11WithAttachmentsStateful);
	}

	Repository repositoryPortaDelegataSincronoAutenticatoSOAP11WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoAutenticatoSOAP11WithAttachmentsStateful")
	public Object[][]testPortaDelegataSincronoAutenticatoSOAP11WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoAutenticatoSOAP11WithAttachmentsStateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP11.STATEFUL.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoAutenticatoSOAP11WithAttachmentsStateful",dependsOnMethods={"sincronoAutenticato_pdSOAP11WithAttachmentsStateful"})
	public void testSincronoAutenticato_pdSOAP11WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoAutenticato(this.pdSOAP11WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo, this.date_sincronoAutenticato_pdSOAP11WithAttachmentsStateful);
	}	

	
	
	Date date_sincronoAutenticato_pdSOAP11WithAttachmentsStateless;
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP11.STATELESS.ATTACHMENTS"})
	public void sincronoAutenticato_pdSOAP11WithAttachmentsStateless() throws TestSuiteException, Exception{
		this.date_sincronoAutenticato_pdSOAP11WithAttachmentsStateless = new Date();
		Porta._sincronoAutenticato(this.pdSOAP11WithAttachmentsStateless, this.repositoryPortaDelegataSincronoAutenticatoSOAP11WithAttachmentsStateless);
	}

	Repository repositoryPortaDelegataSincronoAutenticatoSOAP11WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoAutenticatoSOAP11WithAttachmentsStateless")
	public Object[][]testPortaDelegataSincronoAutenticatoSOAP11WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoAutenticatoSOAP11WithAttachmentsStateless);
	}
	
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP11",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP11.STATELESS.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoAutenticatoSOAP11WithAttachmentsStateless",dependsOnMethods={"sincronoAutenticato_pdSOAP11WithAttachmentsStateless"})
	public void testSincronoAutenticato_pdSOAP11WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoAutenticato(this.pdSOAP11WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo, this.date_sincronoAutenticato_pdSOAP11WithAttachmentsStateless);
	}	
	
	
	Date date_sincronoAutenticato_pdSOAP12WithAttachmentsStateful;
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP12.STATEFUL.ATTACHMENTS"})
	public void sincronoAutenticato_pdSOAP12WithAttachmentsStateful() throws TestSuiteException, Exception{
		this.date_sincronoAutenticato_pdSOAP12WithAttachmentsStateful = new Date();
		Porta._sincronoAutenticato(this.pdSOAP12WithAttachmentsStateful, this.repositoryPortaDelegataSincronoAutenticatoSOAP12WithAttachmentsStateful);
	}

	Repository repositoryPortaDelegataSincronoAutenticatoSOAP12WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoAutenticatoSOAP12WithAttachmentsStateful")
	public Object[][]testPortaDelegataSincronoAutenticatoSOAP12WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoAutenticatoSOAP12WithAttachmentsStateful);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP12.STATEFUL.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoAutenticatoSOAP12WithAttachmentsStateful",dependsOnMethods={"sincronoAutenticato_pdSOAP12WithAttachmentsStateful"})
	public void testSincronoAutenticato_pdSOAP12WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoAutenticato(this.pdSOAP12WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo, this.date_sincronoAutenticato_pdSOAP12WithAttachmentsStateful);
	}	
	
	
	Date date_sincronoAutenticato_pdSOAP12WithAttachmentsStateless;
	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP12.STATELESS.ATTACHMENTS"})
	public void sincronoAutenticato_pdSOAP12WithAttachmentsStateless() throws TestSuiteException, Exception{
		this.date_sincronoAutenticato_pdSOAP12WithAttachmentsStateless = new Date();
		Porta._sincronoAutenticato(this.pdSOAP12WithAttachmentsStateless, this.repositoryPortaDelegataSincronoAutenticatoSOAP12WithAttachmentsStateless);
	}

	Repository repositoryPortaDelegataSincronoAutenticatoSOAP12WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaDelegata.ID_GRUPPO+"SincronoAutenticatoSOAP12WithAttachmentsStateless")
	public Object[][]testPortaDelegataSincronoAutenticatoSOAP12WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaDelegataSincronoAutenticatoSOAP12WithAttachmentsStateless);
	}

	@Test(groups={PortaDelegata.ID_GRUPPO,PortaDelegata.ID_GRUPPO+"Autenticato",PortaDelegata.ID_GRUPPO+".ATTACHMENTS",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaDelegata.ID_GRUPPO+".SOAP12",PortaDelegata.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP12.STATELESS.ATTACHMENTS"},dataProvider=PortaDelegata.ID_GRUPPO+"SincronoAutenticatoSOAP12WithAttachmentsStateless",dependsOnMethods={"sincronoAutenticato_pdSOAP12WithAttachmentsStateless"})
	public void testSincronoAutenticato_pdSOAP12WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoAutenticato(this.pdSOAP12WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo, this.date_sincronoAutenticato_pdSOAP12WithAttachmentsStateless);
	}


}
