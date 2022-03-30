/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
 * WrappedLogSSLSocketFactory
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WrappedLogSSLSocketFactory extends SSLSocketFactory {

	private SSLSocketFactory delegate;
    private Logger log;
    private String prefixLog;
    private String clientCertificateConfigurated;

    public WrappedLogSSLSocketFactory(SSLSocketFactory sf0, Logger log, String prefixLog, String clientCertificateConfigurated) {
        this.delegate = sf0;
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

    @Override
	public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return new WrappedLogSSLSocket((SSLSocket) this.delegate.createSocket(s, host, port, autoClose),
        		this.log, this.prefixLog, this.clientCertificateConfigurated);
    }
    
    @Override
	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
    	return new WrappedLogSSLSocket((SSLSocket) this.delegate.createSocket(host, port),
        		this.log, this.prefixLog, this.clientCertificateConfigurated);
	}

	@Override
	public Socket createSocket(InetAddress address, int port) throws IOException {
		return new WrappedLogSSLSocket((SSLSocket) this.delegate.createSocket(address, port),
        		this.log, this.prefixLog, this.clientCertificateConfigurated);
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
			throws IOException, UnknownHostException {
		return new WrappedLogSSLSocket((SSLSocket) this.delegate.createSocket(host, port, localHost, localPort),
        		this.log, this.prefixLog, this.clientCertificateConfigurated);
	}

	@Override
	public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
			throws IOException {
		return new WrappedLogSSLSocket((SSLSocket) this.delegate.createSocket(address, port, localAddress, localPort),
        		this.log, this.prefixLog, this.clientCertificateConfigurated);
	}
	
    @Override
	public Socket createSocket(Socket s, InputStream consumed, boolean autoClose) throws IOException {
    	return new WrappedLogSSLSocket((SSLSocket) this.delegate.createSocket(s, consumed, autoClose),
        		this.log, this.prefixLog, this.clientCertificateConfigurated);
	}

	@Override
	public Socket createSocket() throws IOException {
		return new WrappedLogSSLSocket((SSLSocket) this.delegate.createSocket(),
        		this.log, this.prefixLog, this.clientCertificateConfigurated);
	}

    
    // METODI NON MODIFICATI
    
	@Override
	public String[] getDefaultCipherSuites() {
		return this.delegate.getDefaultCipherSuites();
	}

	@Override
	public String[] getSupportedCipherSuites() {
		return this.delegate.getSupportedCipherSuites();
	}

	@Override
	public int hashCode() {
		return this.delegate.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this.delegate.equals(obj);
	}

	@Override
	public String toString() {
		return this.delegate.toString();
	}

}
