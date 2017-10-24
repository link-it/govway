package org.openspcoop2.utils.json;

import java.util.ArrayList;
import java.util.List;

public class ValidationResponse {

	public enum ESITO {OK, KO}
	private ESITO esito;
	private Exception exception;
	private List<String> errors;
	public ESITO getEsito() {
		return this.esito;
	}
	public void setEsito(ESITO esito) {
		this.esito = esito;
	}
	public List<String> getErrors() {
		if(this.errors == null) this.errors = new ArrayList<String>();
		return this.errors;
	}
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	public Exception getException() {
		return this.exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
}
