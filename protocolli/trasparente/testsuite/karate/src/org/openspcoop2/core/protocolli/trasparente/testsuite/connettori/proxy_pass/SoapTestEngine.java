/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.HttpLibraryMode;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
* SoapTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author Tommaso Burlon (tommaso.burlon@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class SoapTestEngine extends ConfigLoader {

	
	
	private HttpLibraryMode mode = null;
	protected void setLibraryMode(HttpLibraryMode mode) {
		this.mode = mode;
	}
	
	// cookie
	
	@Test
	public void erogazione_setCookie() throws Exception {
		RestTestEngine._setCookie(false, TipoServizio.EROGAZIONE, "end-without-slash", this.mode);
	}
	@Test
	public void fruizione_setCookie() throws Exception {
		RestTestEngine._setCookie(false, TipoServizio.FRUIZIONE, "end-without-slash", this.mode);
	}

	@Test
	public void erogazione_setCookie_endWithSlashConnettore() throws Exception {
		RestTestEngine._setCookie(false, TipoServizio.EROGAZIONE, "end-with-slash", this.mode);
	}
	@Test
	public void fruizione_setCookie_endWithSlashConnettore() throws Exception {
		RestTestEngine._setCookie(false, TipoServizio.FRUIZIONE, "end-with-slash", this.mode);
	}
	
	
	// cookie (disabilitato proxy pass)
	
	@Test
	public void erogazione_setCookie_gestioneProxyPassDisabilitata() throws Exception {
		RestTestEngine._setCookie_nonTradotto(false, TipoServizio.EROGAZIONE, "disabled", this.mode);
	}
	@Test
	public void fruizione_setCookie_gestioneProxyPassDisabilitata() throws Exception {
		RestTestEngine._setCookie_nonTradotto(false, TipoServizio.FRUIZIONE, "disabled", this.mode);
	}

	
	// cookie (custom, not match header SetCookie)
	
	@Test
	public void erogazione_setCookie_gestioneProxyPassCustom() throws Exception {
		RestTestEngine._setCookie_nonTradotto(false, TipoServizio.EROGAZIONE, "custom", this.mode);
	}
	@Test
	public void fruizione_setCookie_gestioneProxyPassCustom() throws Exception {
		RestTestEngine._setCookie_nonTradotto(false, TipoServizio.FRUIZIONE, "custom", this.mode);
	}
	
	
	
	
	// relative location
	
	@Test
	public void erogazione_relative_location() throws Exception {
		RestTestEngine._location(false, TipoServizio.EROGAZIONE,"end-without-slash","",HttpConstants.REDIRECT_LOCATION, this.mode);
	}
	@Test
	public void fruizione_relative_location() throws Exception {
		RestTestEngine._location(false, TipoServizio.FRUIZIONE,"end-without-slash","",HttpConstants.REDIRECT_LOCATION, this.mode);
	}
	
	@Test
	public void erogazione_relative_contentLocation() throws Exception {
		RestTestEngine._location(false, TipoServizio.EROGAZIONE,"end-without-slash","",HttpConstants.CONTENT_LOCATION, this.mode);
	}
	@Test
	public void fruizione_relative_contentLocation() throws Exception {
		RestTestEngine._location(false, TipoServizio.FRUIZIONE,"end-without-slash","",HttpConstants.CONTENT_LOCATION, this.mode);
	}
	
	@Test
	public void erogazione_relative_location_endWithSlashConnettore() throws Exception {
		RestTestEngine._location(false, TipoServizio.EROGAZIONE,"end-with-slash","",HttpConstants.REDIRECT_LOCATION, this.mode);
	}
	@Test
	public void fruizione_relative_location_endWithSlashConnettore() throws Exception {
		RestTestEngine._location(false, TipoServizio.FRUIZIONE,"end-with-slash","",HttpConstants.REDIRECT_LOCATION, this.mode);
	}
	
	@Test
	public void erogazione_relative_contentLocation_endWithSlashConnettore() throws Exception {
		RestTestEngine._location(false, TipoServizio.EROGAZIONE,"end-with-slash","",HttpConstants.CONTENT_LOCATION, this.mode);
	}
	@Test
	public void fruizione_relative_contentLocation_endWithSlashConnettore() throws Exception {
		RestTestEngine._location(false, TipoServizio.FRUIZIONE,"end-with-slash","",HttpConstants.CONTENT_LOCATION, this.mode);
	}
	
	
	// relative location (disabilitato proxy pass)
	
	@Test
	public void erogazione_relative_location_gestioneProxyPassDisabilitata() throws Exception {
		RestTestEngine._location_nonTradotta_conParametri(false, TipoServizio.EROGAZIONE,"disabled","",HttpConstants.REDIRECT_LOCATION, this.mode);
		RestTestEngine._location_nonTradotta_senzaParametri(false, TipoServizio.EROGAZIONE,"disabled","",HttpConstants.REDIRECT_LOCATION, this.mode);
	}
	@Test
	public void fruizione_relative_location_gestioneProxyPassDisabilitata() throws Exception {
		RestTestEngine._location_nonTradotta_conParametri(false, TipoServizio.FRUIZIONE,"disabled","",HttpConstants.REDIRECT_LOCATION, this.mode);
		RestTestEngine._location_nonTradotta_senzaParametri(false, TipoServizio.FRUIZIONE,"disabled","",HttpConstants.REDIRECT_LOCATION, this.mode);
	}
	
	@Test
	public void erogazione_relative_contentLocation_gestioneProxyPassDisabilitata() throws Exception {
		RestTestEngine._location_nonTradotta_conParametri(false, TipoServizio.EROGAZIONE,"disabled","",HttpConstants.CONTENT_LOCATION, this.mode);
		RestTestEngine._location_nonTradotta_senzaParametri(false, TipoServizio.EROGAZIONE,"disabled","",HttpConstants.CONTENT_LOCATION, this.mode);
	}
	@Test
	public void fruizione_relative_contentLocation_gestioneProxyPassDisabilitata() throws Exception {
		RestTestEngine._location_nonTradotta_conParametri(false, TipoServizio.FRUIZIONE,"disabled","",HttpConstants.CONTENT_LOCATION, this.mode);
		RestTestEngine._location_nonTradotta_senzaParametri(false, TipoServizio.FRUIZIONE,"disabled","",HttpConstants.CONTENT_LOCATION, this.mode);
	}
	
	// relative location (custom)
	
	@Test
	public void erogazione_relative_location_gestioneProxyPassCustom() throws Exception {
		RestTestEngine._location_custom(false, TipoServizio.EROGAZIONE,"custom","",HttpConstants.REDIRECT_LOCATION, false, this.mode);
		
		RestTestEngine._location_custom(false, TipoServizio.EROGAZIONE,"custom","",HttpConstants.CONTENT_LOCATION, false, this.mode);
		
		RestTestEngine._location_custom(false, TipoServizio.EROGAZIONE,"custom","",RestTestEngine.HEADER_HTTP_CUSTOM_LOCATION, true, this.mode);
	}
	@Test
	public void fruizione_relative_location_gestioneProxyPassCustom() throws Exception {
		RestTestEngine._location_custom(false, TipoServizio.FRUIZIONE,"custom","",HttpConstants.REDIRECT_LOCATION, false, this.mode);
		
		RestTestEngine._location_custom(false, TipoServizio.FRUIZIONE,"custom","",HttpConstants.CONTENT_LOCATION, false, this.mode);
		
		RestTestEngine._location_custom(false, TipoServizio.FRUIZIONE,"custom","",RestTestEngine.HEADER_HTTP_CUSTOM_LOCATION, true, this.mode);
	}
	

	

	
	// absolute location
	
	@Test
	public void erogazione_absolute_location() throws Exception {
		RestTestEngine._location(false, TipoServizio.EROGAZIONE,"end-without-slash",RestTestEngine.REQUEST_URI,HttpConstants.REDIRECT_LOCATION, this.mode);
	}
	@Test
	public void fruizione_absolute_location() throws Exception {
		RestTestEngine._location(false, TipoServizio.FRUIZIONE,"end-without-slash",RestTestEngine.REQUEST_URI,HttpConstants.REDIRECT_LOCATION, this.mode);
	}
	
	@Test
	public void erogazione_absolute_contentLocation() throws Exception {
		RestTestEngine._location(false, TipoServizio.EROGAZIONE,"end-without-slash",RestTestEngine.REQUEST_URI,HttpConstants.CONTENT_LOCATION, this.mode);
	}
	@Test
	public void fruizione_absolute_contentLocation() throws Exception {
		RestTestEngine._location(false, TipoServizio.FRUIZIONE,"end-without-slash",RestTestEngine.REQUEST_URI,HttpConstants.CONTENT_LOCATION, this.mode);
	}
	
	@Test
	public void erogazione_absolute_location_endWithSlashConnettore() throws Exception {
		RestTestEngine._location(false, TipoServizio.EROGAZIONE,"end-with-slash",RestTestEngine.REQUEST_URI,HttpConstants.REDIRECT_LOCATION, this.mode);
	}
	@Test
	public void fruizione_absolute_location_endWithSlashConnettore() throws Exception {
		RestTestEngine._location(false, TipoServizio.FRUIZIONE,"end-with-slash",RestTestEngine.REQUEST_URI,HttpConstants.REDIRECT_LOCATION, this.mode);
	}
	
	@Test
	public void erogazione_absolute_contentLocation_endWithSlashConnettore() throws Exception {
		RestTestEngine._location(false, TipoServizio.EROGAZIONE,"end-with-slash",RestTestEngine.REQUEST_URI,HttpConstants.CONTENT_LOCATION, this.mode);
	}
	@Test
	public void fruizione_absolute_contentLocation_endWithSlashConnettore() throws Exception {
		RestTestEngine._location(false, TipoServizio.FRUIZIONE,"end-with-slash",RestTestEngine.REQUEST_URI,HttpConstants.CONTENT_LOCATION, this.mode);
	}
	
	
	
	// absolute location (no match)
	
	@Test
	public void erogazione_absolute_location_noMatchRequestUri() throws Exception {
		RestTestEngine._location_nonTradotta_conParametri(false, TipoServizio.EROGAZIONE,"end-without-slash",RestTestEngine.REQUEST_URI_2,HttpConstants.REDIRECT_LOCATION, this.mode);
		
		RestTestEngine._location_nonTradotta_conParametri(false, TipoServizio.EROGAZIONE,"end-without-slash",RestTestEngine.REQUEST_URI_3,HttpConstants.REDIRECT_LOCATION, this.mode);
		
		RestTestEngine._location_nonTradotta_conParametri(false, TipoServizio.EROGAZIONE,"end-without-slash",RestTestEngine.REQUEST_URI_4,HttpConstants.REDIRECT_LOCATION, this.mode);
		
		RestTestEngine._location_nonTradotta_senzaParametri(false, TipoServizio.EROGAZIONE,"end-without-slash",RestTestEngine.REQUEST_URI_2,HttpConstants.REDIRECT_LOCATION, this.mode);
		
		RestTestEngine._location_nonTradotta_senzaParametri(false, TipoServizio.EROGAZIONE,"end-without-slash",RestTestEngine.REQUEST_URI_3,HttpConstants.REDIRECT_LOCATION, this.mode);
		
		RestTestEngine._location_nonTradotta_senzaParametri(false, TipoServizio.EROGAZIONE,"end-without-slash",RestTestEngine.REQUEST_URI_4,HttpConstants.REDIRECT_LOCATION, this.mode);
	}
	@Test
	public void fruizione_absolute_location_noMatchRequestUri() throws Exception {
		RestTestEngine._location_nonTradotta_conParametri(false, TipoServizio.FRUIZIONE,"end-without-slash",RestTestEngine.REQUEST_URI_2,HttpConstants.REDIRECT_LOCATION, this.mode);
		
		RestTestEngine._location_nonTradotta_conParametri(false, TipoServizio.FRUIZIONE,"end-without-slash",RestTestEngine.REQUEST_URI_3,HttpConstants.REDIRECT_LOCATION, this.mode);
		
		RestTestEngine._location_nonTradotta_conParametri(false, TipoServizio.FRUIZIONE,"end-without-slash",RestTestEngine.REQUEST_URI_4,HttpConstants.REDIRECT_LOCATION, this.mode);
		
		RestTestEngine._location_nonTradotta_senzaParametri(false, TipoServizio.FRUIZIONE,"end-without-slash",RestTestEngine.REQUEST_URI_2,HttpConstants.REDIRECT_LOCATION, this.mode);
		
		RestTestEngine._location_nonTradotta_senzaParametri(false, TipoServizio.FRUIZIONE,"end-without-slash",RestTestEngine.REQUEST_URI_3,HttpConstants.REDIRECT_LOCATION, this.mode);
		
		RestTestEngine._location_nonTradotta_senzaParametri(false, TipoServizio.FRUIZIONE,"end-without-slash",RestTestEngine.REQUEST_URI_4,HttpConstants.REDIRECT_LOCATION, this.mode);
	}
	
	@Test
	public void erogazione_absolute_contentLocation_noMatchRequestUri() throws Exception {
		RestTestEngine._location_nonTradotta_conParametri(false, TipoServizio.EROGAZIONE,"end-without-slash",RestTestEngine.REQUEST_URI_2,HttpConstants.CONTENT_LOCATION, this.mode);
		
		RestTestEngine._location_nonTradotta_conParametri(false, TipoServizio.EROGAZIONE,"end-without-slash",RestTestEngine.REQUEST_URI_3,HttpConstants.CONTENT_LOCATION, this.mode);
		
		RestTestEngine._location_nonTradotta_conParametri(false, TipoServizio.EROGAZIONE,"end-without-slash",RestTestEngine.REQUEST_URI_4,HttpConstants.CONTENT_LOCATION, this.mode);
		
		RestTestEngine._location_nonTradotta_senzaParametri(false, TipoServizio.EROGAZIONE,"end-without-slash",RestTestEngine.REQUEST_URI_2,HttpConstants.CONTENT_LOCATION, this.mode);
		
		RestTestEngine._location_nonTradotta_senzaParametri(false, TipoServizio.EROGAZIONE,"end-without-slash",RestTestEngine.REQUEST_URI_3,HttpConstants.CONTENT_LOCATION, this.mode);
		
		RestTestEngine._location_nonTradotta_senzaParametri(false, TipoServizio.EROGAZIONE,"end-without-slash",RestTestEngine.REQUEST_URI_4,HttpConstants.CONTENT_LOCATION, this.mode);
	}
	@Test
	public void fruizione_absolute_contentLocation_noMatchRequestUri() throws Exception {
		RestTestEngine._location_nonTradotta_conParametri(false, TipoServizio.FRUIZIONE,"end-without-slash",RestTestEngine.REQUEST_URI_2,HttpConstants.CONTENT_LOCATION, this.mode);
		
		RestTestEngine._location_nonTradotta_conParametri(false, TipoServizio.FRUIZIONE,"end-without-slash",RestTestEngine.REQUEST_URI_3,HttpConstants.CONTENT_LOCATION, this.mode);
		
		RestTestEngine._location_nonTradotta_conParametri(false, TipoServizio.FRUIZIONE,"end-without-slash",RestTestEngine.REQUEST_URI_4,HttpConstants.CONTENT_LOCATION, this.mode);
		
		RestTestEngine._location_nonTradotta_senzaParametri(false, TipoServizio.FRUIZIONE,"end-without-slash",RestTestEngine.REQUEST_URI_2,HttpConstants.CONTENT_LOCATION, this.mode);
		
		RestTestEngine._location_nonTradotta_senzaParametri(false, TipoServizio.FRUIZIONE,"end-without-slash",RestTestEngine.REQUEST_URI_3,HttpConstants.CONTENT_LOCATION, this.mode);
		
		RestTestEngine._location_nonTradotta_senzaParametri(false, TipoServizio.FRUIZIONE,"end-without-slash",RestTestEngine.REQUEST_URI_4,HttpConstants.CONTENT_LOCATION, this.mode);
	}

	
	
	// absolute location (disabilitato proxy pass)
	
	@Test
	public void erogazione_absolute_location_gestioneProxyPassDisabilitata() throws Exception {
		RestTestEngine._location_nonTradotta_conParametri(false, TipoServizio.EROGAZIONE,"disabled",RestTestEngine.REQUEST_URI,HttpConstants.REDIRECT_LOCATION, this.mode);
		RestTestEngine._location_nonTradotta_senzaParametri(false, TipoServizio.EROGAZIONE,"disabled",RestTestEngine.REQUEST_URI,HttpConstants.REDIRECT_LOCATION, this.mode);
	}
	@Test
	public void fruizione_absolute_location_gestioneProxyPassDisabilitata() throws Exception {
		RestTestEngine._location_nonTradotta_conParametri(false, TipoServizio.FRUIZIONE,"disabled",RestTestEngine.REQUEST_URI,HttpConstants.REDIRECT_LOCATION, this.mode);
		RestTestEngine._location_nonTradotta_senzaParametri(false, TipoServizio.FRUIZIONE,"disabled",RestTestEngine.REQUEST_URI,HttpConstants.REDIRECT_LOCATION, this.mode);
	}
	
	@Test
	public void erogazione_absolute_contentLocation_gestioneProxyPassDisabilitata() throws Exception {
		RestTestEngine._location_nonTradotta_conParametri(false, TipoServizio.EROGAZIONE,"disabled",RestTestEngine.REQUEST_URI,HttpConstants.CONTENT_LOCATION, this.mode);
		RestTestEngine._location_nonTradotta_senzaParametri(false, TipoServizio.EROGAZIONE,"disabled",RestTestEngine.REQUEST_URI,HttpConstants.CONTENT_LOCATION, this.mode);
	}
	@Test
	public void fruizione_absolute_contentLocation_gestioneProxyPassDisabilitata() throws Exception {
		RestTestEngine._location_nonTradotta_conParametri(false, TipoServizio.FRUIZIONE,"disabled",RestTestEngine.REQUEST_URI,HttpConstants.CONTENT_LOCATION, this.mode);
		RestTestEngine._location_nonTradotta_senzaParametri(false, TipoServizio.FRUIZIONE,"disabled",RestTestEngine.REQUEST_URI,HttpConstants.CONTENT_LOCATION, this.mode);
	}
	
	
	// absolute location (custom)
	
	@Test
	public void erogazione_absolute_location_gestioneProxyPassCustom() throws Exception {
		RestTestEngine._location_custom(false, TipoServizio.EROGAZIONE,"custom",RestTestEngine.REQUEST_URI,HttpConstants.REDIRECT_LOCATION, false, this.mode);
		
		RestTestEngine._location_custom(false, TipoServizio.EROGAZIONE,"custom",RestTestEngine.REQUEST_URI,HttpConstants.CONTENT_LOCATION, false, this.mode);
		
		RestTestEngine._location_custom(false, TipoServizio.EROGAZIONE,"custom",RestTestEngine.REQUEST_URI,RestTestEngine.HEADER_HTTP_CUSTOM_LOCATION, true, this.mode);
	}
	@Test
	public void fruizione_absolute_location_gestioneProxyPassCustom() throws Exception {
		RestTestEngine._location_custom(false, TipoServizio.FRUIZIONE,"custom",RestTestEngine.REQUEST_URI,HttpConstants.REDIRECT_LOCATION, false, this.mode);
		
		RestTestEngine._location_custom(false, TipoServizio.FRUIZIONE,"custom",RestTestEngine.REQUEST_URI,HttpConstants.CONTENT_LOCATION, false, this.mode);
		
		RestTestEngine._location_custom(false, TipoServizio.FRUIZIONE,"custom",RestTestEngine.REQUEST_URI,RestTestEngine.HEADER_HTTP_CUSTOM_LOCATION, true, this.mode);
	}

}
