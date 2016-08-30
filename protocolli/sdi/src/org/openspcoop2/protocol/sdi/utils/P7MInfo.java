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

package org.openspcoop2.protocol.sdi.utils;

import java.io.ByteArrayInputStream;

import org.slf4j.Logger;
import org.apache.soap.encoding.soapenc.Base64;

/**
 * P7MInfo
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class P7MInfo {

	/*
	 * Infatti il formato del file rispecchia quanto indicato nella delibera CNIPA numero 45 del 9 Novembre 2009 all’articolo 21, comma 3.
	 * 
	 * La delibera cita quanto segue:
	 * “Per la codifica della busta crittografica deve essere utilizzato il formato
	 * ASN.1 (ISO/IEC8824) in rappresentazione binaria (ISO/IEC 8825, BER - DER) o
	 * alfanumerica ottenuta applicando la trasformazione BASE 64 (RFC 1421, RFC
	 * 2045). La testata e la coda previsti nelle specifiche RFC 1421 e RFC 2045
	 * possono essere assenti.”
	 *
	 * In pratica la fattura P7M puo' essere ricevuta direttamente come
	 * rappresentazione binaria o codificata in base64.
	 * 
	 **/
	
	private byte[] xmlDecoded;
	private boolean base64Encoded = false;
	
	public P7MInfo(byte [] fattura, Logger log) throws Throwable{
		
		try{
			org.bouncycastle.cms.CMSSignedData cmsSignedData = new org.bouncycastle.cms.CMSSignedData(new ByteArrayInputStream(fattura));
			this.xmlDecoded = (byte[]) cmsSignedData.getSignedContent().getContent();
			
		}catch(Throwable e){
			
			byte[] decoded = null;
			try{
				decoded = Base64.decode(new String(fattura));
			}catch(Throwable eDecode){
				log.error("DecodificaBase64 non riuscita: "+eDecode.getMessage(), eDecode);
				throw e; // lancio l'eccezione originale, poiche' piu' interessante. La seconda mi informa solo che non e' una rappresentazione Base64
			}
			this.base64Encoded = true;
			
			try{
				org.bouncycastle.cms.CMSSignedData cmsSignedData = new org.bouncycastle.cms.CMSSignedData(new ByteArrayInputStream(decoded));
				this.xmlDecoded = (byte[]) cmsSignedData.getSignedContent().getContent();				
			}catch(Throwable eSecond){
				throw eSecond;
			}
		}
		
	}
	
	public byte[] getXmlDecoded() {
		return this.xmlDecoded;
	}
	public boolean isBase64Encoded() {
		return this.base64Encoded;
	}
	
}
