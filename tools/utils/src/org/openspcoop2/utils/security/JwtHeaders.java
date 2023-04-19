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

package org.openspcoop2.utils.security;

import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.cxf.common.util.Base64UrlUtility;
import org.apache.cxf.rs.security.jose.common.KeyManagementUtils;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.apache.cxf.rt.security.crypto.MessageDigestUtils;
import org.openspcoop2.utils.UtilsException;

/**	
 * JwtHeaders
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JwtHeaders {

	public static final String JWT_HDR_ALG = "alg"; // (Algorithm) Header Parameter
	public static final String JWT_HDR_JKU = "jku"; //  (JWK Set URL) Header Parameter
	public static final String JWT_HDR_JWK = "jwk"; // (JSON Web Key) Header Parameter
	public static final String JWT_HDR_KID = "kid"; // (Key ID) Header Parameter
	public static final String JWT_HDR_X5U = "x5u"; // (X.509 URL) Header Parameter
	public static final String JWT_HDR_X5C = "x5c"; // (X.509 Certificate Chain) Header Parameter
	public static final String JWT_HDR_X5T = "x5t"; // (X.509 Certificate SHA-1 Thumbprint) Header Parameter
	public static final String JWT_HDR_X5t_S256 = "x5t#S256"; // (X.509 Certificate SHA-256 Thumbprint) Header Parameter
	public static final String JWT_HDR_TYP = "typ"; // (Type) Header Parameter
	public static final String JWT_HDR_CTY = "cty"; // (Content Type) Header Parameter
	public static final String JWT_HDR_CRIT = "crit"; // (Critical) Header Parameter
	public static final String JWT_HDR_ENC = "enc"; // (Encryption Algorithm) Header Parameter [solo in jwe]
	public static final String JWT_HDR_ZIP = "zip"; // (Compression Algorithm) Header Parameter [solo in jwe]
	 
	private String type;
	private String contentType;
	private String kid; 
	private List<String> criticalHeaders = new ArrayList<>();
	private URI x509Url;
	private List<X509Certificate> x509c = new ArrayList<>(); // i certificati servono anche per sha1 e sha256, il field addX5C serve quindi per capire se poi far uscire anche X5C
	private boolean addX5C = false;
	boolean x509IncludeCertSha1 = false;
	boolean x509IncludeCertSha256 = false;
	private URI jwkUrl;
	private JsonWebKey jwKey;
	private HashMap<String, String> extensions = new HashMap<>();
	
	public void setType(String type) {
		this.type = type;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getKid() {
		return this.kid;
	}
	public void addCriticalHeader(String hdr) {
		this.criticalHeaders.add(hdr);
	}
	public void setX509Url(URI x509Url) {
		this.x509Url = x509Url;
	}
	public void addX509cert(X509Certificate x509c) {
		this.x509c.add(x509c);
	}
	public void setAddX5C(boolean addX5C) {
		this.addX5C = addX5C;
	}
	public boolean isX509IncludeCertSha1() {
		return this.x509IncludeCertSha1;
	}
	public boolean isX509IncludeCertSha256() {
		return this.x509IncludeCertSha256;
	}
	public void setJwkUrl(URI jwkUrl) {
		this.jwkUrl = jwkUrl;
	}
	public void setJwKey(JsonWebKey jwKey) {
		this.jwKey = jwKey;
	}
	public void setJwKey(JsonWebKeys jsonWebKeys, String alias) throws UtilsException {
		this.jwKey = JsonUtils.readKey(jsonWebKeys, alias);
	}
	public void addExtension(String hdr, String value) {
		this.extensions.put(hdr, value);
	}
	
	public String getType() {
		return this.type;
	}
	public String getContentType() {
		return this.contentType;
	}
	public void setKid(String kid) {
		this.kid = kid;
	}
	public List<String> getCriticalHeaders() {
		return this.criticalHeaders;
	}
	public URI getX509Url() {
		return this.x509Url;
	}
	public boolean isAddX5C() {
		return this.addX5C;
	}
	public List<X509Certificate> getX509c() {
		return this.x509c;
	}
	public void setX509IncludeCertSha1(boolean includeCertSha1) {
		this.x509IncludeCertSha1 = includeCertSha1;
	}
	public void setX509IncludeCertSha256(boolean includeCertSha256) {
		this.x509IncludeCertSha256 = includeCertSha256;
	}
	public URI getJwkUrl() {
		return this.jwkUrl;
	}
	public JsonWebKey getJwKey() {
		return this.jwKey;
	}
	public HashMap<String, String> getExtensions() {
		return this.extensions;
	}
	
	public List<String> headers(){
		List<String> list = new ArrayList<>();
		if(this.type!=null) {
			list.add(JWT_HDR_TYP);
		}
		if(this.contentType!=null) {
			list.add(JWT_HDR_CTY);
		}
		if(this.kid!=null) {
			list.add(JWT_HDR_KID);
		}
		if(this.criticalHeaders!=null && !this.criticalHeaders.isEmpty()) {
			list.add(JWT_HDR_CRIT);
		}
		if(this.x509Url!=null) {
			list.add(JWT_HDR_X5U);
		}
		if(this.x509c!=null && !this.x509c.isEmpty()) {
			// fix: lo aggiungo solo se non c'è la url. Nell'oggetto JwtHreader il certificato ho dovuto mettercelo per creare i sha
			//if(!list.contains(JWT_HDR_X5U)) {
			// il fix era errato, aggiunto field apposito 'addX5C'
			if(this.addX5C) {
				list.add(JWT_HDR_X5C);
			}
		}
		if(this.x509IncludeCertSha1 && this.x509c!=null && !this.x509c.isEmpty()) {
			list.add(JWT_HDR_X5T);
		}
		if(this.x509IncludeCertSha256) {
			list.add(JWT_HDR_X5t_S256);
		}
		if(this.jwkUrl!=null) {
			list.add(JWT_HDR_JKU);
		}
		if(this.jwKey!=null) {
			list.add(JWT_HDR_JWK);
		}
		if(this.extensions!=null && !this.extensions.isEmpty()) {
			Iterator<String> hdrIt = this.extensions.keySet().iterator();
			while (hdrIt.hasNext()) {
				String hdr = (String) hdrIt.next();
				list.add(hdr);
			}
		}
		return list;
	}
	
	public void fillJwsHeaders(org.apache.cxf.rs.security.jose.common.JoseHeaders hdrs, boolean forceOverride, String algorithm) throws Exception {
		if(this.type!=null) {
			if(!hdrs.containsHeader(JWT_HDR_TYP) || forceOverride) {
				hdrs.setHeader(JWT_HDR_TYP, this.type);
			}
		}
		if(this.contentType!=null) {
			if(!hdrs.containsHeader(JWT_HDR_CTY) || forceOverride) {
				hdrs.setContentType(this.contentType);
			}
		}
		if(this.kid!=null) {
			if(!hdrs.containsHeader(JWT_HDR_KID) || forceOverride) {
				hdrs.setKeyId(this.kid);
			}
		}
		if(this.criticalHeaders!=null && !this.criticalHeaders.isEmpty()) {
			List<String> headers = new ArrayList<>();
			if(hdrs.containsHeader(JWT_HDR_CRIT)) {
				headers = hdrs.getCritical();
				if(headers==null) {
					headers = new ArrayList<>();
				}
			}
			for (String ch : this.criticalHeaders) {
				if(headers.contains(ch)==false) {
					headers.add(ch);
				}
			}
			/*
			StringBuilder bf = new StringBuilder();
			for (String ch : headers) {
				if(bf.length()>0) {
					bf.append(",");
				}
				bf.append("\"").append(ch).append("\"");
			}
			hdrs.setHeader(JWT_HDR_CRIT, "["+bf.toString()+"]");*/
			hdrs.setCritical(headers);
		}
		if(this.x509Url!=null) {
			if(!hdrs.containsHeader(JWT_HDR_X5U) || forceOverride) {
				hdrs.setX509Url(this.x509Url.toString());
			}
		}
		if(this.x509c!=null && !this.x509c.isEmpty()) {
			if(!hdrs.containsHeader(JWT_HDR_X5C) || forceOverride) {
				// fix: lo aggiungo solo se non c'è la url. Nell'oggetto JwtHreader il certificato ho dovuto mettercelo per creare i sha
				//if(!hdrs.containsHeader(JWT_HDR_X5U)) {
				// il fix era errato, aggiunto field apposito 'addX5C'
				if(this.addX5C) {
					X509Certificate[] chain = this.x509c.toArray(new X509Certificate[1]);
					hdrs.setX509Chain(KeyManagementUtils.encodeX509CertificateChain(chain));
				}
			}
		}
		if(this.x509IncludeCertSha1 && this.x509c!=null && !this.x509c.isEmpty()) {
			if(!hdrs.containsHeader(JWT_HDR_X5T) || forceOverride) {
				X509Certificate[] chain = this.x509c.toArray(new X509Certificate[1]);
				byte[] digestB = MessageDigestUtils.createDigest(chain[0].getEncoded(), MessageDigestUtils.ALGO_SHA_1);
				String digest = Base64UrlUtility.encode(digestB);
				hdrs.setX509Thumbprint(digest);
			}
		}
		if(this.x509IncludeCertSha256) {
			if(!hdrs.containsHeader(JWT_HDR_X5t_S256) || forceOverride) {
				X509Certificate[] chain = this.x509c.toArray(new X509Certificate[1]);
				byte[] digestB = MessageDigestUtils.createDigest(chain[0].getEncoded(), MessageDigestUtils.ALGO_SHA_256);
				String digest = Base64UrlUtility.encode(digestB);
				hdrs.setX509ThumbprintSHA256(digest);
			}
		}
		if(this.jwkUrl!=null) {
			if(!hdrs.containsHeader(JWT_HDR_JKU) || forceOverride) {
				hdrs.setJsonWebKeysUrl(this.jwkUrl.toString());
			}
		}
		if(this.jwKey!=null) {
			if(!hdrs.containsHeader(JWT_HDR_JWK) || forceOverride) {
				JwkUtils.includeCertChain(this.jwKey, hdrs, algorithm);
				JwkUtils.includePublicKey(this.jwKey, hdrs, algorithm);
			}
		}
		if(this.extensions!=null && !this.extensions.isEmpty()) {
			Iterator<String> hdrIt = this.extensions.keySet().iterator();
			while (hdrIt.hasNext()) {
				String hdr = (String) hdrIt.next();
				if(!hdrs.containsHeader(hdr) || forceOverride) {
					String value = this.extensions.get(hdr);
					hdrs.setHeader(hdr,value);
				}
			}
		}
	}
	
}
