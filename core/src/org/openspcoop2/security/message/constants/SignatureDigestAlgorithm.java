package org.openspcoop2.security.message.constants;

public enum SignatureDigestAlgorithm {

	SHA1("http://www.w3.org/2000/09/xmldsig#sha1"),
	SHA224("http://www.w3.org/2001/04/xmldsig-more#sha224"),
	SHA256("http://www.w3.org/2001/04/xmlenc#sha256"),
	SHA384("http://www.w3.org/2000/09/xmldsig#sha384"),
	SHA512("http://www.w3.org/2001/04/xmlenc#sha512");
	
	private String uri;
	SignatureDigestAlgorithm(String uri) {
		this.uri = uri;
	}
	
	public String getUri() {
		return this.uri;
	}
}
