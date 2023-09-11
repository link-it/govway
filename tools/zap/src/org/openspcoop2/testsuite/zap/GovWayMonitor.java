/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
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
package org.openspcoop2.testsuite.zap;

import java.io.IOException;

import org.openspcoop2.utils.UtilsException;
import org.zaproxy.clientapi.core.ClientApiException;

/**
 * GovWayConsole
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GovWayMonitor extends AbstractConsoleScan {
	
	public static void main(String[] args) throws UtilsException, ClientApiException, IOException {
				
		GovWayMonitor console = new GovWayMonitor();
		console.scan(args);
		
	}
	
	@Override
	protected String getLoginUrl(String baseUrl) {
		return baseUrl+"public/login.jsf";
	}
	@Override
	protected String getLogoutUrl(String baseUrl) {
		return baseUrl+"public/logout.jsf";
	}
	
	@Override
	protected String getLoginRequestData() {
		return "AJAXREQUEST=_viewRoot&j_id44=j_id44&username={%username%}&password={%password%}&javax.faces.ViewState=j_id3&submitBtn=submitBtn&";
	}
	
	@Override
	protected String getLoggedInIndicator() {
		return "Profilo:";
	}
	@Override
	protected String getLoggedOutIndicator() {
		return null;
	}
	
}
