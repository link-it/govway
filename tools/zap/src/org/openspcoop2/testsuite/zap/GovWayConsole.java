/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
public class GovWayConsole extends AbstractConsoleScan {
	
	public static void main(String[] args) throws UtilsException, ClientApiException, IOException {
				
		GovWayConsole console = new GovWayConsole();
		console.scan(args);
		
	}
	
	@Override
	protected String getLoginUrl(String baseUrl) {
		return baseUrl+"login.do";
	}
	@Override
	protected String getLogoutUrl(String baseUrl) {
		return baseUrl+"logout.do";
	}
	
	@Override
	protected String getLoginRequestData() {
		return "login={%username%}&password={%password%}";
	}
	
	@Override
	protected String getLoggedInIndicator() {
		return "Profilo:";
	}
	@Override
	protected String getLoggedOutIndicator() {
		return ".*Effettuare il login.*";
	}
	
	@Override
	protected boolean isDebug() {
		return false;
	}
	
	@Override
	protected boolean isUniqueClientApi() {
		return false;
	}
}
