/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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


package org.openspcoop2.testsuite.gui.commons;


import static org.testng.Assert.assertEquals;

import org.openspcoop2.testsuite.core.CostantiTestSuite;
import org.testng.Reporter;

import com.thoughtworks.selenium.Selenium;

/**
 *
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Login {

	public static Selenium loginOK (SeleniumClient seleniumClient,String tipoLogin,String username,String password,boolean validate) throws AssertionError{
		
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = "";
		/*if (tipoLogin.equals("regserv"))
		    webAppContext = seleniumClient.getRegservContext();
		if (tipoLogin.equals("pdd"))
		    webAppContext = seleniumClient.getPddContext();*/
		if (tipoLogin.equals("pddConsole"))
		    webAppContext = seleniumClient.getCtrlstatContext();
		if (tipoLogin.equals("ge"))
		    webAppContext = seleniumClient.getGEContext();
		
		Reporter.log("loginOK: Context ["+webAppContext+"] Value ["+webAppContext+"]");
		selenium_browser.open("/"+webAppContext+"/login.do");
		selenium_browser.type("login", username);
		selenium_browser.type("password", password);
		selenium_browser.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium_browser.waitForPageToLoad("30000");
		assertEquals(selenium_browser.isTextPresent("Login effettuato con successo"),true);
		return selenium_browser;
	}
	
	
	public static Selenium loginKO (SeleniumClient seleniumClient,String tipoLogin,String username,String bad_password,boolean validate) throws AssertionError{
		
		Selenium selenium_browser = seleniumClient.getSeleniumBrowser();
		String webAppContext = "";
		/*if (tipoLogin.equals("regserv"))
		    webAppContext = seleniumClient.getRegservContext();
		if (tipoLogin.equals("pdd"))
		    webAppContext = seleniumClient.getPddContext();*/
		if (tipoLogin.equals("pddConsole"))
		    webAppContext = seleniumClient.getCtrlstatContext();
		if (tipoLogin.equals("ge"))
		    webAppContext = seleniumClient.getGEContext();
		
		Reporter.log("loginOK: Context ["+webAppContext+"] Value ["+webAppContext+"]");
		selenium_browser.open("/"+webAppContext+"/logout.do");
		selenium_browser.type("login", username);
		selenium_browser.type("password", bad_password);
		selenium_browser.click("//input[@value='"+CostantiTestSuite.BOTTONE_INVIA+"']");
		selenium_browser.waitForPageToLoad("30000");
		assertEquals(selenium_browser.isTextPresent("Login o password errata!"),true);
		return selenium_browser;
	}
}


