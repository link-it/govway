package org.openspcoop2.security.message.constants;

public enum EncryptKeyTransportAlgorithm {

	RSA_OAEP("http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p"),
	RSA_OAEP_11("http://www.w3.org/2009/xmlenc11#rsa-oaep"),
	RSA_v1dot5("http://www.w3.org/2001/04/xmlenc#rsa-1_5"),
	DIFFIE_HELLMAN("http://www.w3.org/2001/04/xmlenc#dh");
	
	private String uri;
	EncryptKeyTransportAlgorithm(String uri) {
		this.uri = uri;
	}
	
	public String getUri() {
		return this.uri;
	}
}
