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
 * PortaApplicativaAutenticato
 * 
 * @author Andreea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaApplicativaAutenticato extends PortaImpl {

	
	/* TEST ONE WAY AUTENTICATO */
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP11.STATEFUL"})
	public void oneWayAutenticato_paSOAP11Stateful() throws TestSuiteException, Exception{
		Porta._oneWayAutenticato(this.paSOAP11Stateful, this.repositoryPortaApplicativaOneWayAutenticatoSOAP11Stateful);
	}

	Repository repositoryPortaApplicativaOneWayAutenticatoSOAP11Stateful=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"OneWayAutenticatoSOAP11Stateful")
	public Object[][]testPortaApplicativaOneWayAutenticatoSOAP11Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaOneWayAutenticatoSOAP11Stateful);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP11.STATEFUL"},dataProvider=PortaApplicativa.ID_GRUPPO+"OneWayAutenticatoSOAP11Stateful",dependsOnMethods={"oneWayAutenticato_paSOAP11Stateful"})
	public void testOneWayAutenticato_paSOAP11Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWayAutenticato(this.paSOAP11Stateful, data, msgDiagData, id, checkServizioApplicativo, null);
	}	

	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP11.STATELESS"})
	public void oneWayAutenticato_paSOAP11Stateless() throws TestSuiteException, Exception{
		Porta._oneWayAutenticato(this.paSOAP11Stateless, this.repositoryPortaApplicativaOneWayAutenticatoSOAP11Stateless);
	}

	Repository repositoryPortaApplicativaOneWayAutenticatoSOAP11Stateless=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"OneWayAutenticatoSOAP11Stateless")
	public Object[][]testPortaApplicativaOneWayAutenticatoSOAP11Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaOneWayAutenticatoSOAP11Stateless);
	}
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP11.STATELESS"},dataProvider=PortaApplicativa.ID_GRUPPO+"OneWayAutenticatoSOAP11Stateless",dependsOnMethods={"oneWayAutenticato_paSOAP11Stateless"})
	public void testOneWayAutenticato_paSOAP11Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWayAutenticato(this.paSOAP11Stateless, data, msgDiagData, id, checkServizioApplicativo, null);
	}	
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP12.STATEFUL"})
	public void oneWayAutenticato_paSOAP12Stateful() throws TestSuiteException, Exception{
		Porta._oneWayAutenticato(this.paSOAP12Stateful, this.repositoryPortaApplicativaOneWayAutenticatoSOAP12Stateful);
	}

	Repository repositoryPortaApplicativaOneWayAutenticatoSOAP12Stateful=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"OneWayAutenticatoSOAP12Stateful")
	public Object[][]testPortaApplicativaOneWayAutenticatoSOAP12Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaOneWayAutenticatoSOAP12Stateful);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP12.STATEFUL"},dataProvider=PortaApplicativa.ID_GRUPPO+"OneWayAutenticatoSOAP12Stateful",dependsOnMethods={"oneWayAutenticato_paSOAP12Stateful"})
	public void testOneWayAutenticato_paSOAP12Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWayAutenticato(this.paSOAP12Stateful, data, msgDiagData, id, checkServizioApplicativo, null);
	}	

	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP12.STATELESS"})
	public void oneWayAutenticato_paSOAP12Stateless() throws TestSuiteException, Exception{
		Porta._oneWayAutenticato(this.paSOAP12Stateless, this.repositoryPortaApplicativaOneWayAutenticatoSOAP12Stateless);
	}

	Repository repositoryPortaApplicativaOneWayAutenticatoSOAP12Stateless=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"OneWayAutenticatoSOAP12Stateless")
	public Object[][]testPortaApplicativaOneWayAutenticatoSOAP12Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaOneWayAutenticatoSOAP12Stateless);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP12.STATELESS"},dataProvider=PortaApplicativa.ID_GRUPPO+"OneWayAutenticatoSOAP12Stateless",dependsOnMethods={"oneWayAutenticato_paSOAP12Stateless"})
	public void testOneWayAutenticato_paSOAP12Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWayAutenticato(this.paSOAP12Stateless, data, msgDiagData, id, checkServizioApplicativo, null);
	}	
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP11.STATEFUL.ATTACHMENTS"})
	public void oneWayAutenticato_paSOAP11WithAttachmentsStateful() throws TestSuiteException, Exception{
		Porta._oneWayAutenticato(this.paSOAP11WithAttachmentsStateful, this.repositoryPortaApplicativaOneWayAutenticatoSOAP11WithAttachmentsStateful);
	}

	Repository repositoryPortaApplicativaOneWayAutenticatoSOAP11WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"OneWayAutenticatoSOAP11WithAttachmentsStateful")
	public Object[][]testPortaApplicativaOneWayAutenticatoSOAP11WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaOneWayAutenticatoSOAP11WithAttachmentsStateful);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP11.STATEFUL.ATTACHMENTS"},dataProvider=PortaApplicativa.ID_GRUPPO+"OneWayAutenticatoSOAP11WithAttachmentsStateful",dependsOnMethods={"oneWayAutenticato_paSOAP11WithAttachmentsStateful"})
	public void testOneWayAutenticato_paSOAP11WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWayAutenticato(this.paSOAP11WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo, null);
	}	

	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP11.STATELESS.ATTACHMENTS"})
	public void oneWayAutenticato_paSOAP11WithAttachmentsStateless() throws TestSuiteException, Exception{
		Porta._oneWayAutenticato(this.paSOAP11WithAttachmentsStateless, this.repositoryPortaApplicativaOneWayAutenticatoSOAP11WithAttachmentsStateless);
	}

	Repository repositoryPortaApplicativaOneWayAutenticatoSOAP11WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"OneWayAutenticatoSOAP11WithAttachmentsStateless")
	public Object[][]testPortaApplicativaOneWayAutenticatoSOAP11WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaOneWayAutenticatoSOAP11WithAttachmentsStateless);
	}
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP11.STATELESS.ATTACHMENTS"},dataProvider=PortaApplicativa.ID_GRUPPO+"OneWayAutenticatoSOAP11WithAttachmentsStateless",dependsOnMethods={"oneWayAutenticato_paSOAP11WithAttachmentsStateless"})
	public void testOneWayAutenticato_paSOAP11WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWayAutenticato(this.paSOAP11WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo, null);
	}	
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP12.STATEFUL.ATTACHMENTS"})
	public void oneWayAutenticato_paSOAP12WithAttachmentsStateful() throws TestSuiteException, Exception{
		Porta._oneWayAutenticato(this.paSOAP12WithAttachmentsStateful, this.repositoryPortaApplicativaOneWayAutenticatoSOAP12WithAttachmentsStateful);
	}

	Repository repositoryPortaApplicativaOneWayAutenticatoSOAP12WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"OneWayAutenticatoSOAP12WithAttachmentsStateful")
	public Object[][]testPortaApplicativaOneWayAutenticatoSOAP12WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaOneWayAutenticatoSOAP12WithAttachmentsStateful);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP12.STATEFUL.ATTACHMENTS"},dataProvider=PortaApplicativa.ID_GRUPPO+"OneWayAutenticatoSOAP12WithAttachmentsStateful",dependsOnMethods={"oneWayAutenticato_paSOAP12WithAttachmentsStateful"})
	public void testOneWayAutenticato_paSOAP12WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWayAutenticato(this.paSOAP12WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo, null);
	}	

	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP12.STATELESS.ATTACHMENTS"})
	public void oneWayAutenticato_paSOAP12WithAttachmentsStateless() throws TestSuiteException, Exception{
		Porta._oneWayAutenticato(this.paSOAP12WithAttachmentsStateless, this.repositoryPortaApplicativaOneWayAutenticatoSOAP12WithAttachmentsStateless);
	}

	Repository repositoryPortaApplicativaOneWayAutenticatoSOAP12WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"OneWayAutenticatoSOAP12WithAttachmentsStateless")
	public Object[][]testPortaApplicativaOneWayAutenticatoSOAP12WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaOneWayAutenticatoSOAP12WithAttachmentsStateless);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".ONEWAY_AUTENTICATO.SOAP12.STATELESS.ATTACHMENTS"},dataProvider=PortaApplicativa.ID_GRUPPO+"OneWayAutenticatoSOAP12WithAttachmentsStateless",dependsOnMethods={"oneWayAutenticato_paSOAP12WithAttachmentsStateless"})
	public void testOneWayAutenticato_paSOAP12WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testOneWayAutenticato(this.paSOAP12WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo, null);
	}	

	/* TEST SINCRONO AUTENTICATO */
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP11.STATEFUL"})
	public void sincronoAutenticato_paSOAP11Stateful() throws TestSuiteException, Exception{
		Porta._sincronoAutenticato(this.paSOAP11Stateful, this.repositoryPortaApplicativaSincronoAutenticatoSOAP11Stateful);
	}

	Repository repositoryPortaApplicativaSincronoAutenticatoSOAP11Stateful=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"SincronoAutenticatoSOAP11Stateful")
	public Object[][]testPortaApplicativaSincronoAutenticatoSOAP11Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaSincronoAutenticatoSOAP11Stateful);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP11.STATEFUL"},dataProvider=PortaApplicativa.ID_GRUPPO+"SincronoAutenticatoSOAP11Stateful",dependsOnMethods={"sincronoAutenticato_paSOAP11Stateful"})
	public void testSincronoAutenticato_paSOAP11Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoAutenticato(this.paSOAP11Stateful, data, msgDiagData, id, checkServizioApplicativo, null);
	}	

	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP11.STATELESS"})
	public void sincronoAutenticato_paSOAP11Stateless() throws TestSuiteException, Exception{
		Porta._sincronoAutenticato(this.paSOAP11Stateless, this.repositoryPortaApplicativaSincronoAutenticatoSOAP11Stateless);
	}

	Repository repositoryPortaApplicativaSincronoAutenticatoSOAP11Stateless=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"SincronoAutenticatoSOAP11Stateless")
	public Object[][]testPortaApplicativaSincronoAutenticatoSOAP11Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaSincronoAutenticatoSOAP11Stateless);
	}
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP11.STATELESS"},dataProvider=PortaApplicativa.ID_GRUPPO+"SincronoAutenticatoSOAP11Stateless",dependsOnMethods={"sincronoAutenticato_paSOAP11Stateless"})
	public void testSincronoAutenticato_paSOAP11Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoAutenticato(this.paSOAP11Stateless, data, msgDiagData, id, checkServizioApplicativo, null);
	}	
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP12.STATEFUL"})
	public void sincronoAutenticato_paSOAP12Stateful() throws TestSuiteException, Exception{
		Porta._sincronoAutenticato(this.paSOAP12Stateful, this.repositoryPortaApplicativaSincronoAutenticatoSOAP12Stateful);
	}

	Repository repositoryPortaApplicativaSincronoAutenticatoSOAP12Stateful=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"SincronoAutenticatoSOAP12Stateful")
	public Object[][]testPortaApplicativaSincronoAutenticatoSOAP12Stateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaSincronoAutenticatoSOAP12Stateful);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP12.STATEFUL"},dataProvider=PortaApplicativa.ID_GRUPPO+"SincronoAutenticatoSOAP12Stateful",dependsOnMethods={"sincronoAutenticato_paSOAP12Stateful"})
	public void testSincronoAutenticato_paSOAP12Stateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoAutenticato(this.paSOAP12Stateful, data, msgDiagData, id, checkServizioApplicativo, null);
	}	

	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP12.STATELESS"})
	public void sincronoAutenticato_paSOAP12Stateless() throws TestSuiteException, Exception{
		Porta._sincronoAutenticato(this.paSOAP12Stateless, this.repositoryPortaApplicativaSincronoAutenticatoSOAP12Stateless);
	}

	Repository repositoryPortaApplicativaSincronoAutenticatoSOAP12Stateless=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"SincronoAutenticatoSOAP12Stateless")
	public Object[][]testPortaApplicativaSincronoAutenticatoSOAP12Stateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaSincronoAutenticatoSOAP12Stateless);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP12.STATELESS"},dataProvider=PortaApplicativa.ID_GRUPPO+"SincronoAutenticatoSOAP12Stateless",dependsOnMethods={"sincronoAutenticato_paSOAP12Stateless"})
	public void testSincronoAutenticato_paSOAP12Stateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoAutenticato(this.paSOAP12Stateless, data, msgDiagData, id, checkServizioApplicativo, null);
	}	
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP11.STATEFUL.ATTACHMENTS"})
	public void sincronoAutenticato_paSOAP11WithAttachmentsStateful() throws TestSuiteException, Exception{
		Porta._sincronoAutenticato(this.paSOAP11WithAttachmentsStateful, this.repositoryPortaApplicativaSincronoAutenticatoSOAP11WithAttachmentsStateful);
	}

	Repository repositoryPortaApplicativaSincronoAutenticatoSOAP11WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"SincronoAutenticatoSOAP11WithAttachmentsStateful")
	public Object[][]testPortaApplicativaSincronoAutenticatoSOAP11WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaSincronoAutenticatoSOAP11WithAttachmentsStateful);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP11.STATEFUL.ATTACHMENTS"},dataProvider=PortaApplicativa.ID_GRUPPO+"SincronoAutenticatoSOAP11WithAttachmentsStateful",dependsOnMethods={"sincronoAutenticato_paSOAP11WithAttachmentsStateful"})
	public void testSincronoAutenticato_paSOAP11WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoAutenticato(this.paSOAP11WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo, null);
	}	

	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP11.STATELESS.ATTACHMENTS"})
	public void sincronoAutenticato_paSOAP11WithAttachmentsStateless() throws TestSuiteException, Exception{
		Porta._sincronoAutenticato(this.paSOAP11WithAttachmentsStateless, this.repositoryPortaApplicativaSincronoAutenticatoSOAP11WithAttachmentsStateless);
	}

	Repository repositoryPortaApplicativaSincronoAutenticatoSOAP11WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"SincronoAutenticatoSOAP11WithAttachmentsStateless")
	public Object[][]testPortaApplicativaSincronoAutenticatoSOAP11WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaSincronoAutenticatoSOAP11WithAttachmentsStateless);
	}
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP11",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP11.STATELESS.ATTACHMENTS"},dataProvider=PortaApplicativa.ID_GRUPPO+"SincronoAutenticatoSOAP11WithAttachmentsStateless",dependsOnMethods={"sincronoAutenticato_paSOAP11WithAttachmentsStateless"})
	public void testSincronoAutenticato_paSOAP11WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoAutenticato(this.paSOAP11WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo, null);
	}	
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP12.STATEFUL.ATTACHMENTS"})
	public void sincronoAutenticato_paSOAP12WithAttachmentsStateful() throws TestSuiteException, Exception{
		Porta._sincronoAutenticato(this.paSOAP12WithAttachmentsStateful, this.repositoryPortaApplicativaSincronoAutenticatoSOAP12WithAttachmentsStateful);
	}

	Repository repositoryPortaApplicativaSincronoAutenticatoSOAP12WithAttachmentsStateful=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"SincronoAutenticatoSOAP12WithAttachmentsStateful")
	public Object[][]testPortaApplicativaSincronoAutenticatoSOAP12WithAttachmentsStateful() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaSincronoAutenticatoSOAP12WithAttachmentsStateful);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP12.STATEFUL.ATTACHMENTS"},dataProvider=PortaApplicativa.ID_GRUPPO+"SincronoAutenticatoSOAP12WithAttachmentsStateful",dependsOnMethods={"sincronoAutenticato_paSOAP12WithAttachmentsStateful"})
	public void testSincronoAutenticato_paSOAP12WithAttachmentsStateful(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoAutenticato(this.paSOAP12WithAttachmentsStateful, data, msgDiagData, id, checkServizioApplicativo, null);
	}	
	
	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP12.STATELESS.ATTACHMENTS"})
	public void sincronoAutenticato_paSOAP12WithAttachmentsStateless() throws TestSuiteException, Exception{
		Porta._sincronoAutenticato(this.paSOAP12WithAttachmentsStateless, this.repositoryPortaApplicativaSincronoAutenticatoSOAP12WithAttachmentsStateless);
	}

	Repository repositoryPortaApplicativaSincronoAutenticatoSOAP12WithAttachmentsStateless=new Repository();
	@DataProvider (name=PortaApplicativa.ID_GRUPPO+"SincronoAutenticatoSOAP12WithAttachmentsStateless")
	public Object[][]testPortaApplicativaSincronoAutenticatoSOAP12WithAttachmentsStateless() throws Exception{
		return Porta._getDataProvider(this.repositoryPortaApplicativaSincronoAutenticatoSOAP12WithAttachmentsStateless);
	}

	@Test(groups={PortaApplicativa.ID_GRUPPO,PortaApplicativa.ID_GRUPPO+"Autenticato",PortaApplicativa.ID_GRUPPO+".ATTACHMENTS",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO",PortaApplicativa.ID_GRUPPO+".SOAP12",PortaApplicativa.ID_GRUPPO+".SINCRONO_AUTENTICATO.SOAP12.STATELESS.ATTACHMENTS"},dataProvider=PortaApplicativa.ID_GRUPPO+"SincronoAutenticatoSOAP12WithAttachmentsStateless",dependsOnMethods={"sincronoAutenticato_paSOAP12WithAttachmentsStateless"})
	public void testSincronoAutenticato_paSOAP12WithAttachmentsStateless(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiagData, String id,boolean checkServizioApplicativo) throws Exception{
		Porta._testSincronoAutenticato(this.paSOAP12WithAttachmentsStateless, data, msgDiagData, id, checkServizioApplicativo, null);
	}


}
