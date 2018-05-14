package org.openspcoop2.web.monitor.transazioni.exporter;

public class ExportException extends Exception {

	private static final long serialVersionUID = -4194056094835622653L;

	public ExportException() {
		
	}

	public ExportException(String message) {
		super(message);
	}

	public ExportException(Throwable cause) {
		super(cause);
	}

	public ExportException(String message, Throwable cause) {
		super(message, cause);
	}

}
