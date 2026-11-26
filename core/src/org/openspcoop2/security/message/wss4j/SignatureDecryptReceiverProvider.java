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


package org.openspcoop2.security.message.wss4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.mvc.properties.provider.InputValidationUtils;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderInfo;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;

/**     
 * Provider della maschera 'WS-Security Signature Decrypt' in ricezione: verifica della firma (truststore, campi 'truststore*')
 * e decifratura (keystore, campi 'keystore*') nella stessa configurazione.
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SignatureDecryptReceiverProvider extends KeyStoreSecurityProvider {

	public SignatureDecryptReceiverProvider() {
		super();
	}

	
	@Override
	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {
		super.validate(mapProperties);
		
		Properties p = mapProperties.get("signaturePropRefId");
		if(p!=null && p.size()>0) {
			validateFile(p.getProperty("org.apache.ws.security.crypto.merlin.file"), "Signature - TrustStore - Path");
			validateFile(p.getProperty("org.apache.ws.security.crypto.merlin.x509crl.file"), "Signature - TrustStore - CRL File(s)");
		}
		
		p = mapProperties.get("decryptionPropRefId");
		if(p!=null && p.size()>0) {
			validateFile(p.getProperty("org.apache.ws.security.crypto.merlin.file"), "Decryption - KeyStore - Path");
		}
		
		SignatureReceiverProvider.validateAlgoritmiAccettati(mapProperties);
	}
	
	/** Campo 'Decifra solo Firmati' della maschera (proprietà WSS4J 'requireSignedEncryptedDataElements') */
	public static final String ID_REQUIRE_SIGNED_ENCRYPTED_DATA_ELEMENTS = "requireSignedEncryptedDataElements";
	private static final String LABEL_INFO_REQUIRE_SIGNED_HEADER = "Vengono decifrati solo gli elementi cifrati coperti da una firma già verificata.";
	private static final String LABEL_INFO_REQUIRE_SIGNED_RIFIUTO = "Un messaggio con elementi cifrati non firmati, o in cui la firma non precede gli elementi di cifratura nell'header WS-Security, viene <b>rifiutato prima della decifratura</b>.";
	private static final String LABEL_INFO_REQUIRE_SIGNED_ERRORE = "L'errore restituito è <b>neutro</b>: non dipende dall'esito della decifratura e non consente al mittente di ottenere informazioni sul contenuto cifrato (es. attacchi 'padding oracle' sugli algoritmi in modalità CBC).";
	private static final String LABEL_INFO_REQUIRE_SIGNED_ORDINE = "Richiede che il mittente cifri e poi firmi ('Cifratura &gt;&gt; Firma'), includendo nella firma tutti gli elementi cifrati.";

	@Override
	public ProviderInfo getProviderInfo(String id) throws ProviderException {
		if(ID_REQUIRE_SIGNED_ENCRYPTED_DATA_ELEMENTS.equals(id)) {
			ProviderInfo pInfo = new ProviderInfo();
			pInfo.setHeaderBody(LABEL_INFO_REQUIRE_SIGNED_HEADER);
			List<String> listBody = new ArrayList<>();
			listBody.add(LABEL_INFO_REQUIRE_SIGNED_RIFIUTO);
			listBody.add(LABEL_INFO_REQUIRE_SIGNED_ERRORE);
			listBody.add(LABEL_INFO_REQUIRE_SIGNED_ORDINE);
			pInfo.setListBody(listBody);
			return pInfo;
		}
		return super.getProviderInfo(id);
	}

	private static void validateFile(String file, String fieldName) throws ProviderValidationException {
		if(file!=null && StringUtils.isNotEmpty(file)) {
			InputValidationUtils.validateTextAreaInput(file, fieldName);
		}
	}
	
}
