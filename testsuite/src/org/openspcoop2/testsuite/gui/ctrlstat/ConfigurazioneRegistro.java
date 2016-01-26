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


package org.openspcoop2.testsuite.gui.ctrlstat;

import static org.testng.Assert.assertNotNull;

import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.Cache;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.testsuite.core.CostantiTestSuite;
import org.openspcoop2.testsuite.gui.commons.SeleniumClient;
import org.openspcoop2.testsuite.gui.commons.TestUtil;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Test Lib Configurazione/Registro
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneRegistro {

	
	/**
	 * Dato un oggetto Configurazione, si occupa di impostare la configurazione relativa al registro delle PDD
	 * Abilita/Disabilita la chache e crea/modifica i registri presenti nell'oggetto configurazione
	 * @param seleniumClient
	 * @param accessoRegistro
	 * @param validate
	 * @param operationType
	 * @return Selenium
	 * @throws AssertionError
	 */
	public static Selenium CRUDOperation(SeleniumClient seleniumClient,AccessoRegistro accessoRegistro,boolean validate,int operationType) throws AssertionError
	{
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		Cache cache = accessoRegistro!=null ? accessoRegistro.getCache() : null;
		
		selenium.open("/"+webAppContext+"/registro.do");
		
		if(cache!=null){
			String alg = cache.getAlgoritmo()!=null ? cache.getAlgoritmo().toString().toUpperCase() : "";
			String dim = cache.getDimensione()!=null ? cache.getDimensione() : "";
			String idlecache = cache.getItemIdleTime()!=null ? cache.getItemIdleTime() : "";
			String lifecache = cache.getItemLifeSecond()!=null ? cache.getItemLifeSecond() : "";
			
			selenium.select("statocache", "label="+CostantiConfigurazione.ABILITATO.toString());
			try{
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch (SeleniumException e) {
				//ignore
			}
			selenium.type("dimensionecache", dim );
			selenium.select("algoritmocache", "label="+alg);
			selenium.type("idlecache", idlecache);
			selenium.type("lifecache", lifecache);
		}else{
			selenium.select("statocache", "label="+CostantiConfigurazione.DISABILITATO.toString());
			try{
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch (SeleniumException e) {
				//ignore
			}
		}
		
		selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		
		switch (operationType) {
		case CostantiDB.CREATE:
			CRUDRegistro(seleniumClient, accessoRegistro, validate, operationType);
			break;

		case CostantiDB.UPDATE:
			deleteAllRegistri(seleniumClient, validate);
			CRUDRegistro(seleniumClient, accessoRegistro, validate, CostantiDB.CREATE);
			break;
		}
		
		
		return selenium;
	}
	
	
	private static void CRUDRegistro(SeleniumClient seleniumClient,AccessoRegistro registro,boolean validate,int operationType) throws AssertionError
	{
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
			
		//Lista registri
		if(registro!=null && registro.sizeRegistroList()>0){

			for (int i = 0; i < registro.sizeRegistroList(); i++) {
				AccessoRegistroRegistro rr = registro.getRegistro(i);
				String location = rr.getLocation();
				String nome = rr.getNome();
				String tipo = rr.getTipo().toString();
				String pwd  = rr.getPassword();
				String user = rr.getUser();

				switch (operationType) {
				case CostantiDB.CREATE:
					selenium.open("/"+webAppContext+"/registriAdd.do");

					selenium.type("nome", nome);
					break;

				case CostantiDB.UPDATE:
					selenium.open("/"+webAppContext+"/registriList.do");
					selenium.click("link="+nome);
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
					
					break;
				}
				
				selenium.type("location", location);

				selenium.select("tipo", "label="+tipo);
				try{
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
				}catch (SeleniumException e) {
					//ignore
				}
				if("uddi".equals(tipo)){					
					selenium.type("utente", user);
					selenium.type("password", pwd);
					selenium.type("confpw", pwd);
				}

				selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());

				Assert.assertEquals(selenium.getText("link="+nome),nome);
			}
		}	
	}
	
	public static void deleteAllRegistri(SeleniumClient seleniumClient,boolean validate) throws AssertionError
	{
		assertNotNull(seleniumClient,"Il client seleniun e' null.");
		//Note: si effettua la cancellazione dell'intera lista.
		String webAppContext = seleniumClient.getCtrlstatContext();
		TestUtil.deleteAll(seleniumClient, webAppContext, "registriList", validate);
	}
}


