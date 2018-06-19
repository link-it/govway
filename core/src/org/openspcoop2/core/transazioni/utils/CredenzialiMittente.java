package org.openspcoop2.core.transazioni.utils;

import java.io.Serializable;

import org.openspcoop2.core.transazioni.CredenzialeMittente;

public class CredenzialiMittente implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CredenzialeMittente trasporto;
	
	private CredenzialeMittente token_issuer;
	private CredenzialeMittente token_clientId;
	private CredenzialeMittente token_subject;
	private CredenzialeMittente token_username;
	private CredenzialeMittente token_eMail;
	
	public CredenzialeMittente getTrasporto() {
		return this.trasporto;
	}
	public void setTrasporto(CredenzialeMittente trasporto) {
		this.trasporto = trasporto;
	}
	public CredenzialeMittente getToken_issuer() {
		return this.token_issuer;
	}
	public void setToken_issuer(CredenzialeMittente token_issuer) {
		this.token_issuer = token_issuer;
	}
	public CredenzialeMittente getToken_clientId() {
		return this.token_clientId;
	}
	public void setToken_clientId(CredenzialeMittente token_clientId) {
		this.token_clientId = token_clientId;
	}
	public CredenzialeMittente getToken_subject() {
		return this.token_subject;
	}
	public void setToken_subject(CredenzialeMittente token_subject) {
		this.token_subject = token_subject;
	}
	public CredenzialeMittente getToken_username() {
		return this.token_username;
	}
	public void setToken_username(CredenzialeMittente token_username) {
		this.token_username = token_username;
	}
	public CredenzialeMittente getToken_eMail() {
		return this.token_eMail;
	}
	public void setToken_eMail(CredenzialeMittente token_eMail) {
		this.token_eMail = token_eMail;
	}
}
