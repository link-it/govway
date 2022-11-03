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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;
import java.security.cert.Certificate;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.openspcoop2.utils.certificate.CertificateUtils;
import org.openspcoop2.utils.resources.Charset;
import org.slf4j.Logger;

/**
 * WrappedLogSSLSocket
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WrappedLogSSLSocket extends SSLSocket {

	private SSLSocket delegate;
    private Logger log;
    private String prefixLog;
    private boolean clientCertificateConfigurated;
    private String clientCertificateConfiguratedPath;

    public WrappedLogSSLSocket(SSLSocket s, Logger log, String prefixLog, String clientCertificateConfigurated) {
    	this.delegate = s;
        this.log = log;
        this.prefixLog = prefixLog;
        this.clientCertificateConfiguratedPath = clientCertificateConfigurated;
        this.clientCertificateConfigurated = this.clientCertificateConfiguratedPath!=null;
    }

    @Override
	public OutputStream getOutputStream() throws IOException {
        return new WrappedLogOutputStream(this.delegate.getOutputStream(), this.log, this.prefixLog);
    }

    private static class WrappedLogOutputStream extends FilterOutputStream {

		private static final String CHARSET = Charset.UTF_8.getValue();
        private Logger log;
        private String prefixLog;
        private StringBuilder sb;

        public WrappedLogOutputStream(OutputStream out, Logger log, String prefixLog) {
            super(out);
            this.log = log;
            this.prefixLog = prefixLog;
            this.sb = new StringBuilder(this.prefixLog);
        }

        @Override
		public void write(byte[] b, int off, int len)
                throws IOException {
        	this.sb.append(new String(b, off, len, CHARSET));
            this.out.write(b, off, len);
        }

        @Override
		public void write(int b) throws IOException {
        	this.sb.append(b);
            this.out.write(b);
        }
        
        @Override
		public void write(byte[] b) throws IOException {
        	if(b!=null) {
        		this.sb.append(new String(b));
        		this.out.write(b);
        	}
		}

        @Override
        public void close() throws IOException {
            this.log.info(this.sb.toString());
            super.close();
        }
        
		@Override
		public void flush() throws IOException {
			super.flush();
		}
    }
  
   	@Override
   	public void startHandshake() throws IOException {
   		StringBuilder sb = new StringBuilder(this.prefixLog);
   		sb.append("startHandshake");
   		this.log.info(sb.toString());
   		this.delegate.startHandshake();
   	}
    
    private WrappedLogHandshakeCompletedListener handshakeCompletedListener = null;
	@Override
   	public void addHandshakeCompletedListener(HandshakeCompletedListener listener) {
    	this.handshakeCompletedListener = new WrappedLogHandshakeCompletedListener(this, listener);
    	this.delegate.addHandshakeCompletedListener(this.handshakeCompletedListener);
   	}
   	@Override
   	public void removeHandshakeCompletedListener(HandshakeCompletedListener listener) {
   		this.delegate.removeHandshakeCompletedListener(this.handshakeCompletedListener);
   	}
    
    private static class WrappedLogHandshakeCompletedListener implements HandshakeCompletedListener {

    	private HandshakeCompletedListener delegate;
    	private WrappedLogSSLSocket sslSocket;
        private Logger log;
        private String prefixLog;
        private StringBuilder sb;
        private StringBuilder sbError;
            	
		public WrappedLogHandshakeCompletedListener(WrappedLogSSLSocket sslSocket, HandshakeCompletedListener delegate) {
    		this.delegate = delegate;
    		this.sslSocket = sslSocket;
    		this.log = sslSocket.log;
    		this.prefixLog = sslSocket.prefixLog;
    		this.sb = new StringBuilder(this.prefixLog);
    		this.sbError = new StringBuilder(); // prefisso aggiunto dopo
    	}
    	
		@Override
		public void handshakeCompleted(HandshakeCompletedEvent event) {
			this.delegate.handshakeCompleted(event);
			
//			boolean needClientAuth = this.sslSocket.getNeedClientAuth();
//			boolean useClientMode = this.sslSocket.getUseClientMode();
//			boolean wClientAuth = this.sslSocket.getWantClientAuth();
			
			this.sb.append("handshakeCompleted");
			if(event!=null) {
				
				if(event.getCipherSuite()!=null) {
					this.sb.append("\nCipherSuite: "+event.getCipherSuite());
				}
				
				if(event.getLocalPrincipal()!=null) {
					this.sb.append("\nLocalPrincipal: "+event.getLocalPrincipal().getName());
				}
				else if(this.sslSocket.clientCertificateConfigurated){
					this.sbError.append("LocalPrincipal");
				}
				if(event.getLocalCertificates()!=null && event.getLocalCertificates().length>0) {
					this.sb.append("\nLocalCertificates: "+event.getLocalCertificates().length);
					this.sb.append("\n");
					print(event.getLocalCertificates(), "LocalCertificate");
				}
				else if(this.sslSocket.clientCertificateConfigurated){
					if(this.sbError.length()>0) {
						this.sbError.append(",");
					}
					this.sbError.append("LocalCertificates");
				}
				
				try {
					if(event.getPeerPrincipal()!=null) {
						this.sb.append("\nPeerPrincipal: "+event.getPeerPrincipal().getName());
					}
				}catch(Exception e) {
					this.sb.append("\nPeerPrincipal: "+e.getMessage());
				}
				try {
					if(event.getPeerCertificates()!=null && event.getPeerCertificates().length>0) {
						this.sb.append("\nPeerCertificates: "+event.getPeerCertificates().length);
						this.sb.append("\n");
						print(event.getPeerCertificates(), "PeerCertificate");
					}
				}catch(Exception e) {
					this.sb.append("\nPeerCertificates: "+e.getMessage());
				}
				
			}
			
			this.log.info(this.sb.toString());
			if(this.sbError.length()>0) {
				this.sbError.append(" non inviato, nonostante sia configurato un certificato client (keystore: "+this.sslSocket.clientCertificateConfiguratedPath+")");
				this.log.error(this.prefixLog+this.sbError.toString());
			}
		}
		
		private void print(Certificate [] certs, String tipo) {
			for (int j = 0; j < certs.length; j++) {
				if(certs[j] instanceof java.security.cert.X509Certificate) {
					java.security.cert.X509Certificate x509 = (java.security.cert.X509Certificate) certs[j];
					CertificateUtils.printCertificate(this.sb, x509, tipo+"-"+j, true);
				}
				else {
					this.sb.append("#### Certificate["+tipo+"-"+j+"]\n");
					this.sb.append("Certificate["+tipo+"-"+j+"] non è X509");
				}
			}
		}
   	
    }
    

   	@Override
   	public boolean getEnableSessionCreation() {
   		boolean returnValue = this.delegate.getEnableSessionCreation(); 
   		StringBuilder sb = new StringBuilder(this.prefixLog);
   		sb.append("getEnableSessionCreation=").append(returnValue);
   		this.log.info(sb.toString());
   		return returnValue;
   	}

   	@Override
   	public String[] getEnabledCipherSuites() {
   		String[] cipherSuites = this.delegate.getEnabledCipherSuites();
   		if(cipherSuites!=null && cipherSuites.length>0) {
   			StringBuilder sb = new StringBuilder(this.prefixLog);
   			sb.append("EnabledCipherSuites: ");
   			for (String cs : cipherSuites) {
   				if(sb.length()>0) {
   					sb.append(",");
   				}
   				sb.append(cs);
			}
   			this.log.info(sb.toString());
   		} 
   		return cipherSuites;
   	}
   	
   	@Override
   	public String[] getSupportedCipherSuites() {
   		String[] cipherSuites = this.delegate.getSupportedCipherSuites();
   		if(cipherSuites!=null && cipherSuites.length>0) {
   			StringBuilder sb = new StringBuilder(this.prefixLog);
   			sb.append("SupportedCipherSuites: ");
   			for (String cs : cipherSuites) {
   				if(sb.length()>0) {
   					sb.append(",");
   				}
   				sb.append(cs);
			}
   			this.log.info(sb.toString());
   		} 
   		return cipherSuites;
   	}

   	@Override
   	public String[] getEnabledProtocols() {
   		String[] enabledProtocols = this.delegate.getEnabledProtocols();
   		if(enabledProtocols!=null && enabledProtocols.length>0) {
   			StringBuilder sb = new StringBuilder(this.prefixLog);		
   			sb.append("EnabledProtocols: ");
   			for (String ep : enabledProtocols) {
   				if(sb.length()>0) {
   					sb.append(",");
   				}
   				sb.append(ep);
			}
   			this.log.info(sb.toString());
   		} 
   		return enabledProtocols;
   	}
   	
   	@Override
   	public String[] getSupportedProtocols() {
   		String[] supportedProtocols = this.delegate.getSupportedProtocols();
   		if(supportedProtocols!=null && supportedProtocols.length>0) {
   			StringBuilder sb = new StringBuilder(this.prefixLog);		
   			sb.append("SupportedProtocols: ");
   			for (String ep : supportedProtocols) {
   				if(sb.length()>0) {
   					sb.append(",");
   				}
   				sb.append(ep);
			}
   			this.log.info(sb.toString());
   		} 
   		return supportedProtocols;
   	}

   	@Override
   	public boolean getNeedClientAuth() {
   		boolean returnValue = this.delegate.getNeedClientAuth(); 
   		StringBuilder sb = new StringBuilder(this.prefixLog);
   		sb.append("getNeedClientAuth=").append(returnValue);
   		this.log.info(sb.toString());
   		return returnValue;
   	}
   	
   	@Override
   	public boolean getUseClientMode() {
   		boolean returnValue = this.delegate.getUseClientMode(); 
   		StringBuilder sb = new StringBuilder(this.prefixLog);
   		sb.append("getUseClientMode=").append(returnValue);
   		this.log.info(sb.toString());
   		return returnValue;
   	}
   	
   	@Override
   	public boolean getWantClientAuth() {
   		boolean returnValue = this.delegate.getWantClientAuth(); 
   		StringBuilder sb = new StringBuilder(this.prefixLog);
   		sb.append("getWantClientAuth=").append(returnValue);
   		this.log.info(sb.toString());
   		return returnValue;
   	}
   	
   	@Override
   	public SSLSession getHandshakeSession() {
   		return this.delegate.getHandshakeSession();
   	}

   	@Override
   	public SSLParameters getSSLParameters() {
   		return this.delegate.getSSLParameters();
   	}

   	@Override
   	public SSLSession getSession() {
   		return this.delegate.getSession();
   	}

   	@Override
   	public void setEnableSessionCreation(boolean flag) {
   		this.delegate.setEnableSessionCreation(flag);
   	}

   	@Override
   	public void setEnabledCipherSuites(String[] suites) {
   		this.delegate.setEnabledCipherSuites(suites);
   	}

   	@Override
   	public void setEnabledProtocols(String[] protocols) {
   		this.delegate.setEnabledProtocols(protocols);
   	}

   	@Override
   	public void setNeedClientAuth(boolean need) {
   		this.delegate.setNeedClientAuth(need);
   	}

   	@Override
   	public void setSSLParameters(SSLParameters params) {
   		this.delegate.setSSLParameters(params);
   	}

   	@Override
   	public void setUseClientMode(boolean mode) {
   		this.delegate.setUseClientMode(mode);
   	}

   	@Override
   	public void setWantClientAuth(boolean want) {
   		this.delegate.setWantClientAuth(want);
   	}

   	// METODI NON MODIFICATI
   	
   	@Override
	public void connect(SocketAddress endpoint) throws IOException {
		this.delegate.connect(endpoint);
	}

	@Override
	public void connect(SocketAddress endpoint, int timeout) throws IOException {
		this.delegate.connect(endpoint, timeout);
	}

	@Override
	public void bind(SocketAddress bindpoint) throws IOException {
		this.delegate.bind(bindpoint);
	}

	@Override
	public InetAddress getInetAddress() {
		return this.delegate.getInetAddress();
	}

	@Override
	public InetAddress getLocalAddress() {
		return this.delegate.getLocalAddress();
	}

	@Override
	public int getPort() {
		return this.delegate.getPort();
	}

	@Override
	public int getLocalPort() {
		return this.delegate.getLocalPort();
	}

	@Override
	public SocketAddress getRemoteSocketAddress() {
		return this.delegate.getRemoteSocketAddress();
	}

	@Override
	public SocketAddress getLocalSocketAddress() {
		return this.delegate.getLocalSocketAddress();
	}

	@Override
	public SocketChannel getChannel() {
		return this.delegate.getChannel();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return this.delegate.getInputStream();
	}

	@Override
	public void setTcpNoDelay(boolean on) throws SocketException {
		this.delegate.setTcpNoDelay(on);
	}

	@Override
	public boolean getTcpNoDelay() throws SocketException {
		return this.delegate.getTcpNoDelay();
	}

	@Override
	public void setSoLinger(boolean on, int linger) throws SocketException {
		this.delegate.setSoLinger(on, linger);
	}

	@Override
	public int getSoLinger() throws SocketException {
		return this.delegate.getSoLinger();
	}

	@Override
	public void sendUrgentData(int data) throws IOException {
		this.delegate.sendUrgentData(data);
	}

	@Override
	public void setOOBInline(boolean on) throws SocketException {
		this.delegate.setOOBInline(on);
	}

	@Override
	public boolean getOOBInline() throws SocketException {
		return this.delegate.getOOBInline();
	}

	@Override
	public synchronized void setSoTimeout(int timeout) throws SocketException {
		this.delegate.setSoTimeout(timeout);
	}

	@Override
	public synchronized int getSoTimeout() throws SocketException {
		return this.delegate.getSoTimeout();
	}

	@Override
	public synchronized void setSendBufferSize(int size) throws SocketException {
		this.delegate.setSendBufferSize(size);
	}

	@Override
	public synchronized int getSendBufferSize() throws SocketException {
		return this.delegate.getSendBufferSize();
	}

	@Override
	public synchronized void setReceiveBufferSize(int size) throws SocketException {
		this.delegate.setReceiveBufferSize(size);
	}

	@Override
	public synchronized int getReceiveBufferSize() throws SocketException {
		return this.delegate.getReceiveBufferSize();
	}

	@Override
	public void setKeepAlive(boolean on) throws SocketException {
		this.delegate.setKeepAlive(on);
	}

	@Override
	public boolean getKeepAlive() throws SocketException {
		return this.delegate.getKeepAlive();
	}

	@Override
	public void setTrafficClass(int tc) throws SocketException {
		this.delegate.setTrafficClass(tc);
	}

	@Override
	public int getTrafficClass() throws SocketException {
		return this.delegate.getTrafficClass();
	}

	@Override
	public void setReuseAddress(boolean on) throws SocketException {
		this.delegate.setReuseAddress(on);
	}

	@Override
	public boolean getReuseAddress() throws SocketException {
		return this.delegate.getReuseAddress();
	}

	@Override
	public synchronized void close() throws IOException {
		this.delegate.close();
	}

	@Override
	public void shutdownInput() throws IOException {
		this.delegate.shutdownInput();
	}

	@Override
	public void shutdownOutput() throws IOException {
		this.delegate.shutdownOutput();
	}

	@Override
	public String toString() {
		return this.delegate.toString();
	}

	@Override
	public boolean isConnected() {
		return this.delegate.isConnected();
	}

	@Override
	public boolean isBound() {
		return this.delegate.isBound();
	}

	@Override
	public boolean isClosed() {
		return this.delegate.isClosed();
	}

	@Override
	public boolean isInputShutdown() {
		return this.delegate.isInputShutdown();
	}

	@Override
	public boolean isOutputShutdown() {
		return this.delegate.isOutputShutdown();
	}

	@Override
	public void setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
		this.delegate.setPerformancePreferences(connectionTime, latency, bandwidth);
	}

	@Override
	public int hashCode() {
		return this.delegate.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this.delegate.equals(obj);
	}
}
