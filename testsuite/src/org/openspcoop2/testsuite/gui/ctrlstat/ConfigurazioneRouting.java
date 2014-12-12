/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
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


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.openspcoop2.core.config.Route;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.RoutingTableDestinazione;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.testsuite.core.CostantiTestSuite;
import org.openspcoop2.testsuite.gui.commons.SeleniumClient;
import org.openspcoop2.testsuite.gui.commons.TestUtil;
import org.testng.Reporter;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

/**
 *
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneRouting {

	
	/**
	 * Dato un oggetto Configurazione, si occupa di gestire la Rotta di Default e le eventuali Rotte Statiche se presenti
	 * @param seleniumClient
	 * @param routingTable
	 * @param validate
	 * @param operationType
	 * @return Selenium
	 * @throws AssertionError
	 */
	public static Selenium CRUDOperation(SeleniumClient seleniumClient,RoutingTable routingTable,boolean validate,int operationType) throws AssertionError
	{
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		selenium.open("/"+webAppContext+"/routing.do");
		
		if(routingTable!=null){
			
			selenium.select("rottaenabled","label="+(routingTable.isAbilitata()?CostantiConfigurazione.ABILITATO.toString():CostantiConfigurazione.DISABILITATO.toString()));
			try{
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch (SeleniumException e) {
				// ignore
			}
			if(routingTable.getDefault()!=null && routingTable.getDefault().sizeRouteList()>0){
				Route rotta = routingTable.getDefault().getRoute(0);	
				CRUDRotta(selenium, rotta, operationType);
			}
			
			selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			
			switch (operationType) {
			case CostantiDB.CREATE:
				CRUDDestinazioni(seleniumClient, routingTable, validate, CostantiDB.DELETE);
				CRUDDestinazioni(seleniumClient, routingTable, validate, CostantiDB.CREATE);
				break;

			case CostantiDB.UPDATE:
				CRUDDestinazioni(seleniumClient, routingTable, validate, CostantiDB.DELETE);
				CRUDDestinazioni(seleniumClient, routingTable, validate, CostantiDB.CREATE);
				break;
			}
			
		}
		
		return selenium;
	}
	
	private static void CRUDRotta(Selenium selenium,Route rotta,int operationType) throws AssertionError
	{
		assertNotNull(rotta,"La rotta e' nulla.");
		if(rotta.getGateway()!=null){
			
			String tipo=rotta.getGateway().getTipo()!=null ? rotta.getGateway().getTipo() :"";
			String nome=rotta.getGateway().getNome()!=null ? rotta.getGateway().getNome() :"";
			
			//rotta tipo gateway
			selenium.select("tiporotta", "gateway");
			try{
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch (SeleniumException e) {
				//ignore
			}
			selenium.type("tiposoggrotta", tipo);
			selenium.type("nomesoggrotta", nome);
			
			selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		}
		
		if(rotta.getRegistro()!=null){
			
			//rotta tipo registro
			selenium.select("tiporotta", "registro");
			try{
				selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
			}catch (SeleniumException e) {
				//ignore
			}
			selenium.select("registrorotta", rotta.getRegistro().getNome()!=null ? rotta.getRegistro().getNome() : "all");
			
			selenium.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
			selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());
		}
	}
	
	private static Selenium CRUDDestinazioni(SeleniumClient seleniumClient,RoutingTable routingTable,boolean validate,int operationType) throws AssertionError
	{
		Selenium selenium = seleniumClient.getSeleniumBrowser();
		String webAppContext = seleniumClient.getCtrlstatContext();
		
		if(CostantiDB.DELETE==operationType){
			TestUtil.deleteAll(seleniumClient, webAppContext, "routingList", validate);
			return selenium;
		}
		
		if(routingTable!=null && routingTable.sizeDestinazioneList()>0){
			for (int i = 0; i < routingTable.sizeDestinazioneList(); i++) {
				RoutingTableDestinazione destinazione = routingTable.getDestinazione(i);
				String tipoDest=destinazione.getTipo();
				String nomeDest=destinazione.getNome();
				
				switch (operationType) {
				case CostantiDB.CREATE:
					//aggiungo destinazione
					Reporter.log("Creo destinazione ...["+tipoDest+"/"+nomeDest+"]");
					selenium.open("/"+webAppContext+"/routingAdd.do");
					selenium.type("tipo", tipoDest);
					selenium.type("nome", nomeDest);
					
					break;

				case CostantiDB.UPDATE:
					//in caso di update il nome e il tipo destinazione
					//non sono moficabili, quindi modifico solo le info sulla rotta
					selenium.open("/"+webAppContext+"/routingList.do");
					selenium.click("link="+tipoDest+"/"+nomeDest);
					selenium.waitForPageToLoad(TestUtil.getWaitPageToLoadTime());

					break;
				}
				
				Reporter.log("Rotte presenti nella destinazione ["+tipoDest+"/"+nomeDest+"] "+destinazione.sizeRouteList());
				if(destinazione.sizeRouteList()>0){
					//prendo la rotta della destinazione
					Route rotta = destinazione.getRoute(0);
					CRUDRotta(selenium, rotta, operationType);
				}
				
				assertEquals(selenium.getText("link="+tipoDest+"/"+nomeDest), tipoDest+"/"+nomeDest);
				//ssertEquals(selenium.isTextPresent(nomeDest), true);
			}
			
		}	
		
		
		return selenium;
	}
	
}


