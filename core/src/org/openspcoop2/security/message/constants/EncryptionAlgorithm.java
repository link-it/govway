package org.openspcoop2.security.message.constants;

public enum EncryptionAlgorithm {

	TRIPLEDES("http://www.w3.org/2001/04/xmlenc#tripledes-cbc"),
	AES_128("http://www.w3.org/2001/04/xmlenc#aes128-cbc"),
	AES_256("http://www.w3.org/2001/04/xmlenc#aes192-cbc"),
	AES_192("http://www.w3.org/2001/04/xmlenc#aes256-cbc"),
	AES_128_GCM("http://www.w3.org/2009/xmlenc11#aes128-gcm"),
	AES_192_GCM("http://www.w3.org/2009/xmlenc11#aes192-gcm"),
	AES_256_GCM("http://www.w3.org/2009/xmlenc11#aes256-gcm"),
	SEED_128("http://www.w3.org/2007/05/xmldsig-more#seed128-cbc"),
	CAMELLIA_128("http://www.w3.org/2001/04/xmldsig-more#camellia128-cbc"),
	CAMELLIA_192("http://www.w3.org/2001/04/xmldsig-more#camellia192-cbc"),
	CAMELLIA_256("http://www.w3.org/2001/04/xmldsig-more#camellia256-cbc");
	
	private String uri;
	EncryptionAlgorithm(String uri) {
		this.uri = uri;
	}
	
	public String getUri() {
		return this.uri;
	}
}
