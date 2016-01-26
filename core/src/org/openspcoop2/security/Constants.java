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
package org.openspcoop2.security;

/**
 * Constants
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Constants {
    
    public static final String URI_SOAP11_ENV =
        "http://schemas.xmlsoap.org/soap/envelope/";
    public static final String URI_SOAP12_ENV =
        "http://www.w3.org/2003/05/soap-envelope";
    public static final String URI_SOAP11_NEXT_ACTOR =
        "http://schemas.xmlsoap.org/soap/actor/next";
    public static final String URI_SOAP12_NEXT_ROLE =
        "http://www.w3.org/2003/05/soap-envelope/role/next";
    public static final String URI_SOAP12_NONE_ROLE =
        "http://www.w3.org/2003/05/soap-envelope/role/none";
    public static final String URI_SOAP12_ULTIMATE_ROLE =
        "http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver";
    
    public static final String C14N_OMIT_COMMENTS = 
        "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
    public static final String C14N_WITH_COMMENTS = 
        "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
    public static final String C14N_EXCL_OMIT_COMMENTS = 
        "http://www.w3.org/2001/10/xml-exc-c14n#";
    public static final String C14N_EXCL_WITH_COMMENTS = 
        "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
    
    public static final String KEYTRANSPORT_RSA15 = 
        "http://www.w3.org/2001/04/xmlenc#rsa-1_5";
    public static final String KEYTRANSPORT_RSAOEP = 
        "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p";
    public static final String KEYTRANSPORT_TRIPLE_DES = 
            "http://www.w3.org/2001/04/xmlenc#kw-tripledes";
    
    public static final String TRIPLE_DES = 
        "http://www.w3.org/2001/04/xmlenc#tripledes-cbc";
    public static final String AES_128 = 
        "http://www.w3.org/2001/04/xmlenc#aes128-cbc";
    public static final String AES_256 = 
        "http://www.w3.org/2001/04/xmlenc#aes256-cbc";
    public static final String AES_192 = 
        "http://www.w3.org/2001/04/xmlenc#aes192-cbc";
    public static final String AES_128_GCM = 
        "http://www.w3.org/2009/xmlenc11#aes128-gcm";
    public static final String AES_192_GCM = 
        "http://www.w3.org/2009/xmlenc11#aes192-gcm";
    public static final String AES_256_GCM = 
        "http://www.w3.org/2009/xmlenc11#aes256-gcm";
    public static final String DSA = 
        "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
    public static final String RSA = 
        "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
    public static final String RSA_SHA1 = 
        "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
    public static final String SHA1 = 
        "http://www.w3.org/2000/09/xmldsig#sha1";
    public static final String HMAC_SHA1 = 
        "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
    public static final String HMAC_SHA256 = 
        "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
    public static final String HMAC_SHA384 = 
        "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
    public static final String HMAC_SHA512 = 
        "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
    public static final String HMAC_MD5 = 
        "http://www.w3.org/2001/04/xmldsig-more#hmac-md5";
	
}
