/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
package org.openspcoop2.utils.transport.http;

import java.io.IOException;
import java.net.Socket;

import javax.net.ssl.SSLSocket;

import org.apache.hc.client5.http.ssl.TlsSocketStrategy;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
 * WrappedLogTlsSocketStrategy
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WrappedLogTlsSocketStrategy implements TlsSocketStrategy {

	
	private TlsSocketStrategy delegate;
    private Logger log;
    private String prefixLog;
    private String clientCertificateConfigurated;
    
	@Override
	public SSLSocket upgrade(Socket arg0, String arg1, int arg2, Object arg3, HttpContext arg4) throws IOException {
		return new WrappedLogSSLSocket(this.delegate.upgrade(arg0, arg1, arg2, arg3, arg4),
        		this.log, this.prefixLog, this.clientCertificateConfigurated);
	}
	
	public WrappedLogTlsSocketStrategy(TlsSocketStrategy ss0, Logger log, String prefixLog, String clientCertificateConfigurated) {
        this.delegate = ss0;
        this.log = log;
        this.prefixLog = prefixLog;
        if(this.log==null) {
        	this.log = LoggerWrapperFactory.getLogger(WrappedLogSSLSocketFactory.class);
        }
        if(this.prefixLog==null) {
        	this.prefixLog="";
        }
        this.clientCertificateConfigurated = clientCertificateConfigurated;
    }

}
