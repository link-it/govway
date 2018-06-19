package org.openspcoop2.security.message.constants;

public enum EncrypSymmetricKeyWrapAlgorithm {

	TRIPLEDES("http://www.w3.org/2001/04/xmlenc#kw-tripledes"),
	AES_128("http://www.w3.org/2001/04/xmlenc#kw-aes128"),
	AES_256("http://www.w3.org/2001/04/xmlenc#kw-aes256"),
	AES_192("http://www.w3.org/2001/04/xmlenc#kw-aes192"),
	CAMELLIA_128("http://www.w3.org/2001/04/xmldsig-more#kw-camellia128"),
	CAMELLIA_192("http://www.w3.org/2001/04/xmldsig-more#kw-camellia192"),
	CAMELLIA_256("http://www.w3.org/2001/04/xmldsig-more#kw-camellia256"),
	SEED_128("http://www.w3.org/2007/05/xmldsig-more#kw-seed128");
	 
	
	private String uri;
	EncrypSymmetricKeyWrapAlgorithm(String uri) {
		this.uri = uri;
	}
	
	public String getUri() {
		return this.uri;
	}
}
