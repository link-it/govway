package org.openspcoop2.security.message.constants;

public enum SignatureC14NAlgorithm {
	
	INCLUSIVE_C14N_10_OMITS_COMMENTS("http://www.w3.org/TR/2001/REC-xml-c14n-20010315","Inclusive XML Canonicalization 1.0 (omits comments)"),
	INCLUSIVE_C14N_10_WITH_COMMENTS("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments","Inclusive XML Canonicalization 1.0 (with comments)"),
	INCLUSIVE_C14N_11_OMITS_COMMENTS("http://www.w3.org/2006/12/xml-c14n11","Inclusive XML Canonicalization 1.1 (omits comments)"),
	INCLUSIVE_C14N_11_WITH_COMMENTS("http://www.w3.org/2006/12/xml-c14n11#WithComments","Inclusive XML Canonicalization 1.1 (with comments)"),
	EXCLUSIVE_C14N_10_OMITS_COMMENTS("http://www.w3.org/2001/10/xml-exc-c14n#","Exclusive XML Canonicalization 1.0 (omits comments)"),
	EXCLUSIVE_C14N_10_WITH_COMMENTS("http://www.w3.org/2001/10/xml-exc-c14n#WithComments","Exclusive XML Canonicalization 1.0 (with comments)");
	
	private String uri;
	private String label;
	SignatureC14NAlgorithm(String uri,String label) {
		this.uri = uri;
		this.label = label;
	}
	
	public String getUri() {
		return this.uri;
	}
	public String getLabel() {
		return this.label;
	}
}
