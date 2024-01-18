/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.logger;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.utils.Map;
import org.openspcoop2.utils.MapKey;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;

/**
 * DiagnosticInputStream
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DiagnosticInputStream extends FilterInputStream {

	private static final String COMPLETATA = "completata";
	private static final String FALLITA = "fallita";
	public static final MapKey<String> DIAGNOSTIC_INPUT_STREAM_REQUEST_COMPLETE_DATE = Map.newMapKey("DiagnosticInputStreamRequestCompleteDate");
	public static final MapKey<String> DIAGNOSTIC_INPUT_STREAM_REQUEST_ERROR_DATE = Map.newMapKey("DiagnosticInputStreamRequestErrorDate");
	public static final MapKey<String> DIAGNOSTIC_INPUT_STREAM_RESPONSE_COMPLETE_DATE = Map.newMapKey("DiagnosticInputStreamResponseCompleteDate");
	public static final MapKey<String> DIAGNOSTIC_INPUT_STREAM_RESPONSE_ERROR_DATE = Map.newMapKey("DiagnosticInputStreamResponseErrorDate");
	
	private InputStream isWrapped = null;
	private String idModuloFunzionale;
	private String identificativo;
	private boolean request;
	private MsgDiagnostico msgDiagnostico;
	private Logger log;
	private Map<Object> ctx;
	private boolean readSomeBytes = false;
	
	private static boolean setDateEmptyStream = true;
	public static boolean isSetDateEmptyStream() {
		return setDateEmptyStream;
	}
	public static void setSetDateEmptyStream(boolean setDateEmptyStream) {
		DiagnosticInputStream.setDateEmptyStream = setDateEmptyStream;
	}

	public DiagnosticInputStream(InputStream inputStream, String idModuloFunzionale, String identificativo, boolean request,
			MsgDiagnostico msgDiagnostico, Logger log, Map<Object> ctx) {
		super(inputStream);
		this.idModuloFunzionale = idModuloFunzionale;
		this.identificativo = identificativo;
		this.request = request;
		this.msgDiagnostico = msgDiagnostico;
		this.log = log;
		this.ctx = ctx;
		
		this.isWrapped = inputStream;
	}
	
	public InputStream getIsWrapped() {
		return this.isWrapped;
	}

	@Override
	public int read() throws IOException {
		try {
			int res = super.read();
			if (res == -1) {
				registerDate(null);
				emitLog(null);
				emitDiagnostic(null);
			}
			else {
				if(!this.readSomeBytes) {
					this.readSomeBytes=true;
				}
			}
			return res;
		}catch(IOException io) {
			registerDate(io);
			emitLog(io);
			emitDiagnostic(io);
			throw io;
		}catch(Throwable t) {
			registerDate(t);
			emitLog(t);
			emitDiagnostic(t);
			throw t;
		}
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		try {
			int res = super.read(b, off, len);
			if (res == -1) {
				registerDate(null);
				emitLog(null);
				emitDiagnostic(null);
			}
			else {
				if(!this.readSomeBytes) {
					this.readSomeBytes=true;
				}
			}
			return res;
		}catch(IOException io) {
			registerDate(io);
			emitLog(io);
			emitDiagnostic(io);
			throw io;
		}catch(Throwable t) {
			registerDate(t);
			emitLog(t);
			emitDiagnostic(t);
			throw t;
		}
	}

	private void registerDate(Throwable t) {
		if(this.ctx!=null && 
				(this.readSomeBytes 
						|| 
				DiagnosticInputStream.setDateEmptyStream) // per far si che le due date siano le stesse
			) {
			Date d = DateManager.getDate();
			if(t!=null) {
				this.ctx.put(this.request ? DIAGNOSTIC_INPUT_STREAM_REQUEST_ERROR_DATE : DIAGNOSTIC_INPUT_STREAM_RESPONSE_ERROR_DATE, d);
			}
			else {
				this.ctx.put(this.request ? DIAGNOSTIC_INPUT_STREAM_REQUEST_COMPLETE_DATE : DIAGNOSTIC_INPUT_STREAM_RESPONSE_COMPLETE_DATE, d);
			}
		}
	}
	
	private void emitLog(Throwable t) {
		if(this.log!=null) {
			StringBuilder sb = new StringBuilder();
			sb.append("Lettura payload '").append(this.identificativo).append("' ");
			if(t!=null) {
				sb.append("fallita: ").append(t.getMessage());
			}
			else {
				sb.append("completata con successo");
			}
			String msg = sb.toString();
			if(t!=null) {
				this.log.error(msg, t);
			}else{
				this.log.debug(msg);
			}
		}
	}
	private void emitDiagnostic(Throwable t) {
		if(this.msgDiagnostico!=null && 
				this.idModuloFunzionale!=null && StringUtils.isNotEmpty(this.idModuloFunzionale) &&
				this.identificativo!=null && StringUtils.isNotEmpty(this.identificativo)) {
			try {
				if(t!=null) {
					String msg = Utilities.readFirstErrorValidMessageFromException(t);
					this.msgDiagnostico.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msg);
					this.msgDiagnostico.logPersonalizzato(this.idModuloFunzionale, this.identificativo+"."+FALLITA);
				}
				else {
					this.msgDiagnostico.logPersonalizzato(this.idModuloFunzionale, this.identificativo+"."+COMPLETATA);
				}
			}catch(Throwable tLog) {
				if(this.log!=null) {
					this.log.error("Emissione diagnostico per lettura payload '"+this.identificativo+"' fallita: "+tLog.getMessage(),tLog);
				}
			}
		}
	}
}
