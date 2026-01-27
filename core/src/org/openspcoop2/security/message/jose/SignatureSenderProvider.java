/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

package org.openspcoop2.security.message.jose;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.mvc.properties.provider.InputValidationUtils;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderInfo;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.core.mvc.properties.utils.MultiPropertiesUtilities;
import org.openspcoop2.security.message.constants.SecurityConstants;

/**     
 * SignatureSenderProvider
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SignatureSenderProvider extends KeyStoreSecurityProvider {

	public SignatureSenderProvider() {
		super();
	}

	
	@Override
	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		super.validate(mapProperties);
		
		Properties defaultP = MultiPropertiesUtilities.getDefaultProperties(mapProperties);
		
		Properties p = mapProperties.get("signaturePropRefId");
		if(p!=null && p.size()>0 &&
			!p.containsKey(SecurityConstants.JOSE_KEYSTORE) && !p.containsKey(SecurityConstants.JOSE_KEYSTORE_JWKSET)) {
			// altrimenti è stato fatto inject del keystore
			String file = p.getProperty(SecurityConstants.JOSE_KEYSTORE_FILE);
			if(file!=null && StringUtils.isNotEmpty(file)) {
				InputValidationUtils.validateTextAreaInput(file, "Signature - KeyStore - File");
			}
		}
		
		String file = defaultP.getProperty("joseX509Url");
		if(file!=null && StringUtils.isNotEmpty(file)) {
			InputValidationUtils.validateTextAreaInput(file, "X.509 Certificate - URL");
		}
		
		file = defaultP.getProperty("joseJWKSetUrl");
		if(file!=null && StringUtils.isNotEmpty(file)) {
			InputValidationUtils.validateTextAreaInput(file, "Public Key - URL");
		}
		
		file = defaultP.getProperty("joseCriticalHeaders");
		if(file!=null && StringUtils.isNotEmpty(file)) {
			InputValidationUtils.validateTextAreaInput(file, "Content Type (cty) - Critical Headers (crit)");
		}


	}

	private static final String LABEL_INFO_HEADER = "Il valore può essere definito come costante o contenere parti dinamiche risolte a runtime dal Gateway.\nLe espressioni utilizzabili sono:";
	private static final String LABEL_INFO_TOKEN = "<b>${tokenInfo:FIELD}</b>: permette di accedere ai claim di un token (es. ${tokenInfo:sub})";
	private static final String LABEL_INFO_HTTP_HEADER = "<b>${header:NAME}</b>: valore presente nell'header http che possiede il nome 'NAME'";
	private static final String LABEL_INFO_CONTEXT = "<b>${context:FIELD}</b>: permette di accedere al contesto della richiesta";
	private static final String LABEL_INFO_BUSTA = "<b>${busta:FIELD}</b>: permette di utilizzare informazioni generiche del profilo (es. ${busta:mittente})";

	@Override
	public ProviderInfo getProviderInfo(String id) throws ProviderException {
		if("issuer".equals(id) || "audienceManual".equals(id) || "ttl".equals(id)) {
			ProviderInfo pInfo = new ProviderInfo();
			pInfo.setHeaderBody(LABEL_INFO_HEADER);
			List<String> listBody = new ArrayList<>();
			listBody.add(LABEL_INFO_TOKEN);
			listBody.add(LABEL_INFO_HTTP_HEADER);
			listBody.add(LABEL_INFO_CONTEXT);
			listBody.add(LABEL_INFO_BUSTA);
			pInfo.setListBody(listBody);
			return pInfo;
		}
		else if("httpStatusCodes".equals(id)) {
			ProviderInfo pInfo = new ProviderInfo();
			pInfo.setHeaderBody("Codici HTTP di risposta del backend che attivano la firma.");
			List<String> listBody = new ArrayList<>();
			listBody.add("<b>Singolo codice</b>: 200");
			listBody.add("<b>Lista</b>: 200,202,404");
			listBody.add("<b>Range</b>: 200-299");
			listBody.add("<b>Range aperto</b>: -299 (fino a 299), 500- (da 500 in poi)");
			pInfo.setListBody(listBody);
			return pInfo;
		}
		return super.getProviderInfo(id);
	}

}
