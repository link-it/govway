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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.proxy_pass;

import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
* SoapTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SoapTest extends ConfigLoader {

	
	// cookie
	
	@Test
	public void erogazione_setCookie() throws Exception {
		RestTest._setCookie(false, TipoServizio.EROGAZIONE, "end-without-slash");
	}
	@Test
	public void fruizione_setCookie() throws Exception {
		RestTest._setCookie(false, TipoServizio.FRUIZIONE, "end-without-slash");
	}

	@Test
	public void erogazione_setCookie_endWithSlashConnettore() throws Exception {
		RestTest._setCookie(false, TipoServizio.EROGAZIONE, "end-with-slash");
	}
	@Test
	public void fruizione_setCookie_endWithSlashConnettore() throws Exception {
		RestTest._setCookie(false, TipoServizio.FRUIZIONE, "end-with-slash");
	}
	
	
	// cookie (disabilitato proxy pass)
	
	@Test
	public void erogazione_setCookie_gestioneProxyPassDisabilitata() throws Exception {
		RestTest._setCookie_nonTradotto(false, TipoServizio.EROGAZIONE, "disabled");
	}
	@Test
	public void fruizione_setCookie_gestioneProxyPassDisabilitata() throws Exception {
		RestTest._setCookie_nonTradotto(false, TipoServizio.FRUIZIONE, "disabled");
	}

	
	// cookie (custom, not match header SetCookie)
	
	@Test
	public void erogazione_setCookie_gestioneProxyPassCustom() throws Exception {
		RestTest._setCookie_nonTradotto(false, TipoServizio.EROGAZIONE, "custom");
	}
	@Test
	public void fruizione_setCookie_gestioneProxyPassCustom() throws Exception {
		RestTest._setCookie_nonTradotto(false, TipoServizio.FRUIZIONE, "custom");
	}
	
	
	
	
	// relative location
	
	@Test
	public void erogazione_relative_location() throws Exception {
		RestTest._location(false, TipoServizio.EROGAZIONE,"end-without-slash","",HttpConstants.REDIRECT_LOCATION);
	}
	@Test
	public void fruizione_relative_location() throws Exception {
		RestTest._location(false, TipoServizio.FRUIZIONE,"end-without-slash","",HttpConstants.REDIRECT_LOCATION);
	}
	
	@Test
	public void erogazione_relative_contentLocation() throws Exception {
		RestTest._location(false, TipoServizio.EROGAZIONE,"end-without-slash","",HttpConstants.CONTENT_LOCATION);
	}
	@Test
	public void fruizione_relative_contentLocation() throws Exception {
		RestTest._location(false, TipoServizio.FRUIZIONE,"end-without-slash","",HttpConstants.CONTENT_LOCATION);
	}
	
	@Test
	public void erogazione_relative_location_endWithSlashConnettore() throws Exception {
		RestTest._location(false, TipoServizio.EROGAZIONE,"end-with-slash","",HttpConstants.REDIRECT_LOCATION);
	}
	@Test
	public void fruizione_relative_location_endWithSlashConnettore() throws Exception {
		RestTest._location(false, TipoServizio.FRUIZIONE,"end-with-slash","",HttpConstants.REDIRECT_LOCATION);
	}
	
	@Test
	public void erogazione_relative_contentLocation_endWithSlashConnettore() throws Exception {
		RestTest._location(false, TipoServizio.EROGAZIONE,"end-with-slash","",HttpConstants.CONTENT_LOCATION);
	}
	@Test
	public void fruizione_relative_contentLocation_endWithSlashConnettore() throws Exception {
		RestTest._location(false, TipoServizio.FRUIZIONE,"end-with-slash","",HttpConstants.CONTENT_LOCATION);
	}
	
	
	// relative location (disabilitato proxy pass)
	
	@Test
	public void erogazione_relative_location_gestioneProxyPassDisabilitata() throws Exception {
		RestTest._location_nonTradotta(false, TipoServizio.EROGAZIONE,"disabled","",HttpConstants.REDIRECT_LOCATION);
	}
	@Test
	public void fruizione_relative_location_gestioneProxyPassDisabilitata() throws Exception {
		RestTest._location_nonTradotta(false, TipoServizio.FRUIZIONE,"disabled","",HttpConstants.REDIRECT_LOCATION);
	}
	
	@Test
	public void erogazione_relative_contentLocation_gestioneProxyPassDisabilitata() throws Exception {
		RestTest._location_nonTradotta(false, TipoServizio.EROGAZIONE,"disabled","",HttpConstants.CONTENT_LOCATION);
	}
	@Test
	public void fruizione_relative_contentLocation_gestioneProxyPassDisabilitata() throws Exception {
		RestTest._location_nonTradotta(false, TipoServizio.FRUIZIONE,"disabled","",HttpConstants.CONTENT_LOCATION);
	}
	
	// relative location (custom)
	
	@Test
	public void erogazione_relative_location_gestioneProxyPassCustom() throws Exception {
		RestTest._location_custom(false, TipoServizio.EROGAZIONE,"custom","",HttpConstants.REDIRECT_LOCATION, false);
		
		RestTest._location_custom(false, TipoServizio.EROGAZIONE,"custom","",HttpConstants.CONTENT_LOCATION, false);
		
		RestTest._location_custom(false, TipoServizio.EROGAZIONE,"custom","",RestTest.HEADER_HTTP_CUSTOM_LOCATION, true);
	}
	@Test
	public void fruizione_relative_location_gestioneProxyPassCustom() throws Exception {
		RestTest._location_custom(false, TipoServizio.FRUIZIONE,"custom","",HttpConstants.REDIRECT_LOCATION, false);
		
		RestTest._location_custom(false, TipoServizio.FRUIZIONE,"custom","",HttpConstants.CONTENT_LOCATION, false);
		
		RestTest._location_custom(false, TipoServizio.FRUIZIONE,"custom","",RestTest.HEADER_HTTP_CUSTOM_LOCATION, true);
	}
	

	

	
	// absolute location
	
	@Test
	public void erogazione_absolute_location() throws Exception {
		RestTest._location(false, TipoServizio.EROGAZIONE,"end-without-slash",RestTest.REQUEST_URI,HttpConstants.REDIRECT_LOCATION);
	}
	@Test
	public void fruizione_absolute_location() throws Exception {
		RestTest._location(false, TipoServizio.FRUIZIONE,"end-without-slash",RestTest.REQUEST_URI,HttpConstants.REDIRECT_LOCATION);
	}
	
	@Test
	public void erogazione_absolute_contentLocation() throws Exception {
		RestTest._location(false, TipoServizio.EROGAZIONE,"end-without-slash",RestTest.REQUEST_URI,HttpConstants.CONTENT_LOCATION);
	}
	@Test
	public void fruizione_absolute_contentLocation() throws Exception {
		RestTest._location(false, TipoServizio.FRUIZIONE,"end-without-slash",RestTest.REQUEST_URI,HttpConstants.CONTENT_LOCATION);
	}
	
	@Test
	public void erogazione_absolute_location_endWithSlashConnettore() throws Exception {
		RestTest._location(false, TipoServizio.EROGAZIONE,"end-with-slash",RestTest.REQUEST_URI,HttpConstants.REDIRECT_LOCATION);
	}
	@Test
	public void fruizione_absolute_location_endWithSlashConnettore() throws Exception {
		RestTest._location(false, TipoServizio.FRUIZIONE,"end-with-slash",RestTest.REQUEST_URI,HttpConstants.REDIRECT_LOCATION);
	}
	
	@Test
	public void erogazione_absolute_contentLocation_endWithSlashConnettore() throws Exception {
		RestTest._location(false, TipoServizio.EROGAZIONE,"end-with-slash",RestTest.REQUEST_URI,HttpConstants.CONTENT_LOCATION);
	}
	@Test
	public void fruizione_absolute_contentLocation_endWithSlashConnettore() throws Exception {
		RestTest._location(false, TipoServizio.FRUIZIONE,"end-with-slash",RestTest.REQUEST_URI,HttpConstants.CONTENT_LOCATION);
	}
	
	
	
	// absolute location (no match)
	
	@Test
	public void erogazione_absolute_location_noMatchRequestUri() throws Exception {
		RestTest._location_nonTradotta(false, TipoServizio.EROGAZIONE,"end-without-slash",RestTest.REQUEST_URI_2,HttpConstants.REDIRECT_LOCATION);
		
		RestTest._location_nonTradotta(false, TipoServizio.EROGAZIONE,"end-without-slash",RestTest.REQUEST_URI_3,HttpConstants.REDIRECT_LOCATION);
		
		RestTest._location_nonTradotta(false, TipoServizio.EROGAZIONE,"end-without-slash",RestTest.REQUEST_URI_4,HttpConstants.REDIRECT_LOCATION);
	}
	@Test
	public void fruizione_absolute_location_noMatchRequestUri() throws Exception {
		RestTest._location_nonTradotta(false, TipoServizio.FRUIZIONE,"end-without-slash",RestTest.REQUEST_URI_2,HttpConstants.REDIRECT_LOCATION);
		
		RestTest._location_nonTradotta(false, TipoServizio.FRUIZIONE,"end-without-slash",RestTest.REQUEST_URI_3,HttpConstants.REDIRECT_LOCATION);
		
		RestTest._location_nonTradotta(false, TipoServizio.FRUIZIONE,"end-without-slash",RestTest.REQUEST_URI_4,HttpConstants.REDIRECT_LOCATION);
	}
	
	@Test
	public void erogazione_absolute_contentLocation_noMatchRequestUri() throws Exception {
		RestTest._location_nonTradotta(false, TipoServizio.EROGAZIONE,"end-without-slash",RestTest.REQUEST_URI_2,HttpConstants.CONTENT_LOCATION);
		
		RestTest._location_nonTradotta(false, TipoServizio.EROGAZIONE,"end-without-slash",RestTest.REQUEST_URI_3,HttpConstants.CONTENT_LOCATION);
		
		RestTest._location_nonTradotta(false, TipoServizio.EROGAZIONE,"end-without-slash",RestTest.REQUEST_URI_4,HttpConstants.CONTENT_LOCATION);
	}
	@Test
	public void fruizione_absolute_contentLocation_noMatchRequestUri() throws Exception {
		RestTest._location_nonTradotta(false, TipoServizio.FRUIZIONE,"end-without-slash",RestTest.REQUEST_URI_2,HttpConstants.CONTENT_LOCATION);
		
		RestTest._location_nonTradotta(false, TipoServizio.FRUIZIONE,"end-without-slash",RestTest.REQUEST_URI_3,HttpConstants.CONTENT_LOCATION);
		
		RestTest._location_nonTradotta(false, TipoServizio.FRUIZIONE,"end-without-slash",RestTest.REQUEST_URI_4,HttpConstants.CONTENT_LOCATION);
	}

	
	
	// absolute location (disabilitato proxy pass)
	
	@Test
	public void erogazione_absolute_location_gestioneProxyPassDisabilitata() throws Exception {
		RestTest._location_nonTradotta(false, TipoServizio.EROGAZIONE,"disabled",RestTest.REQUEST_URI,HttpConstants.REDIRECT_LOCATION);
	}
	@Test
	public void fruizione_absolute_location_gestioneProxyPassDisabilitata() throws Exception {
		RestTest._location_nonTradotta(false, TipoServizio.FRUIZIONE,"disabled",RestTest.REQUEST_URI,HttpConstants.REDIRECT_LOCATION);
	}
	
	@Test
	public void erogazione_absolute_contentLocation_gestioneProxyPassDisabilitata() throws Exception {
		RestTest._location_nonTradotta(false, TipoServizio.EROGAZIONE,"disabled",RestTest.REQUEST_URI,HttpConstants.CONTENT_LOCATION);
	}
	@Test
	public void fruizione_absolute_contentLocation_gestioneProxyPassDisabilitata() throws Exception {
		RestTest._location_nonTradotta(false, TipoServizio.FRUIZIONE,"disabled",RestTest.REQUEST_URI,HttpConstants.CONTENT_LOCATION);
	}
	
	
	// absolute location (custom)
	
	@Test
	public void erogazione_absolute_location_gestioneProxyPassCustom() throws Exception {
		RestTest._location_custom(false, TipoServizio.EROGAZIONE,"custom",RestTest.REQUEST_URI,HttpConstants.REDIRECT_LOCATION, false);
		
		RestTest._location_custom(false, TipoServizio.EROGAZIONE,"custom",RestTest.REQUEST_URI,HttpConstants.CONTENT_LOCATION, false);
		
		RestTest._location_custom(false, TipoServizio.EROGAZIONE,"custom",RestTest.REQUEST_URI,RestTest.HEADER_HTTP_CUSTOM_LOCATION, true);
	}
	@Test
	public void fruizione_absolute_location_gestioneProxyPassCustom() throws Exception {
		RestTest._location_custom(false, TipoServizio.FRUIZIONE,"custom",RestTest.REQUEST_URI,HttpConstants.REDIRECT_LOCATION, false);
		
		RestTest._location_custom(false, TipoServizio.FRUIZIONE,"custom",RestTest.REQUEST_URI,HttpConstants.CONTENT_LOCATION, false);
		
		RestTest._location_custom(false, TipoServizio.FRUIZIONE,"custom",RestTest.REQUEST_URI,RestTest.HEADER_HTTP_CUSTOM_LOCATION, true);
	}

}
