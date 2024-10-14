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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.opzioni_avanzate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.CertificateUtils;
import org.openspcoop2.utils.resources.AbstractBaseThread;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
 * HttpProxyThread
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpProxyThread extends AbstractBaseThread {

	private Process process;
	private File fAuth;	
	private File fCert;	
	private File fScript;	
	private InputStream isDebug;
	private InputStream isError;
	
	public HttpProxyThread(String command, int port, String username, String password) throws IOException, UtilsException {

		String com = "mitmdump";
		if(command!=null) {
			com = command;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("#!/bin/bash\n\n").append(com);
		sb.append(" --mode regular");
		sb.append(" --listen-port ").append(port+"");
		sb.append(" -w /tmp/mitmdump-log.mitm");
		sb.append(" --set ssl_insecure=true");
		
		String keystore = "/etc/govway/keys/erogatore.jks";
		org.openspcoop2.utils.certificate.KeyStore k = new org.openspcoop2.utils.certificate.KeyStore(keystore, "openspcoop");
		String pemPublic = CertificateUtils.toPEM((java.security.cert.X509Certificate)k.getCertificate("erogatore"));
		String pemPrivate = CertificateUtils.toPEM(k.getPrivateKey("erogatore", "openspcoop"));
		String total = pemPrivate+pemPublic;
		/**System.out.println("CERTS ["+total+"]");*/
		this.fCert = File.createTempFile("mitmdumpAuth", ".cert");
		FileSystemUtilities.writeFile(this.fCert, total.getBytes());
		sb.append(" --certs ").append(this.fCert.getAbsolutePath());
		
		if(username!=null && StringUtils.isNotEmpty(username) && 
				password!=null && StringUtils.isNotEmpty(password)) {
			this.fAuth = File.createTempFile("mitmdumpAuth", ".py");
			FileSystemUtilities.writeFile(this.fAuth, getBasicAuth(username, password).getBytes());
			sb.append(" -s ").append(this.fAuth.getAbsolutePath());
		}
		
		
		this.fScript = File.createTempFile("startHttpProxy", ".sh");
		FileSystemUtilities.writeFile(this.fScript, sb.toString().getBytes());
		if(this.fScript.setExecutable(true)) {
			// nop
		}
	}
	
	private static String getBasicAuth(String username, String password) throws UtilsException {
		String basicAuthUSERNAME = "UTENTEGOVWAY";
		String basicAuthPASSWORD = "PASSWORDGOVWAY";
		String s = Utilities.getAsString(HttpProxyThread.class.getResourceAsStream("/org/openspcoop2/core/protocolli/trasparente/testsuite/connettori/opzioni_avanzate/HttpProxyBasicAuth.py"),Charset.UTF_8.getValue());
		s = s.replace(basicAuthUSERNAME, username);
		return s.replace(basicAuthPASSWORD, password);
	}
	
	@Override
	protected void process() {
				
		if(this.process == null) {
			try {
				String command = FileSystemUtilities.readFile(this.fScript);
				System.out.println("INVOKE from file '"+this.fScript.getAbsolutePath()+"':\n\t '"+command+"'");
				
				this.process = Runtime.getRuntime().exec(this.fScript.getAbsolutePath());
				System.out.println("PROCESS FINISH");
				//System.out.println("ESCE?");
				
				this.isDebug = this.process.getInputStream();
				this.isError = this.process.getErrorStream();
				
			}catch(Throwable t) {
				System.out.println("INVOKE ERROR");
				t.printStackTrace(System.out);
				//new RuntimeException(t.getMessage(),t);
			}
		}
	}
	
	@Override
	public synchronized void close() {
		
		if(this.process!=null) {
			
			this.process.descendants().forEach(ph -> {
			    ph.destroy();
			});
	
			this.process.destroy();
			
			boolean terminated = false;
			while(terminated == false){
				try{
					Utilities.sleep(500);
					this.process.exitValue();
					terminated = true;
				}catch(java.lang.IllegalThreadStateException exit){}
			}
			
			debugMsg(true);
			
			this.process = null;
		}
		
		if(this.isDebug!=null) {
			try {
				this.isDebug.close();
			}catch(Throwable t) {}
			this.isDebug = null;
		}
		if(this.isError!=null) {
			try {
				this.isError.close();
			}catch(Throwable t) {}
			this.isError = null;
		}
		
		if(this.fAuth!=null) {
			this.fAuth.delete();
			this.fAuth = null;
		}
		if(this.fCert!=null) {
			this.fCert.delete();
			this.fCert = null;
		}
		if(this.fScript!=null) {
			this.fScript.delete();
			this.fScript = null;
		}
	}

	public boolean debugMsg(boolean waitStop) {
		if(this.process==null) {
			System.out.println("Process unitialized");
			return false;
		}
		boolean terminated = false;
		int exitValue = -1;
		if(waitStop) {
			while(terminated == false){
				try{
					Utilities.sleep(500);
					this.process.exitValue();
					terminated = true;
				}catch(java.lang.IllegalThreadStateException exit){}
			}
		}
		else {	
			try{
				exitValue = this.process.exitValue();
				terminated = true;
			}catch(java.lang.IllegalThreadStateException exit){}
		}
		
		if(!terminated) {
			System.out.println("Process running");
			return true;
		}
		else {
			System.out.println("Process terminated with code: "+exitValue);
		}

		try(java.io.BufferedInputStream berror = new java.io.BufferedInputStream(this.isError);){

			StringBuilder stampaError = new StringBuilder();
			int read = 0;
			while((read = berror.read())!=-1){
				stampaError.append((char)read);
			}
			if(stampaError.length()>0) {
				System.out.println("ERROR stream: "+stampaError.toString());
			}
		}catch(Throwable t) {
			System.out.println("Reading error stream failed: "+t.getMessage());
			//t.printStackTrace(System.out);
		}
		
		try(java.io.BufferedInputStream bin = new java.io.BufferedInputStream(this.isDebug);){

			StringBuilder stampa = new StringBuilder();
			int read = 0;
			while((read = bin.read())!=-1){
				stampa.append((char)read);
			}
			if(stampa.length()>0){
				System.out.println("DEBUG stream: "+stampa.toString());
			}
		}catch(Throwable t) {
			System.out.println("Reading stream failed: "+t.getMessage());
			//t.printStackTrace(System.out);
		}
		
		return false;
	}
	
	
	
	public static final int PORT_NO_AUTH = 59900;
	public static final int PORT_AUTH = 59901;
	
	public static HttpProxyThread newHttpProxyThreadNoAuth(String command, int waitStartupServer) throws Exception {
		return newHttpProxyThreadNoAuth(command, waitStartupServer, PORT_NO_AUTH, null, null);
	}
	public static HttpProxyThread newHttpProxyThreadAuth(String command, int waitStartupServer, String username, String password) throws Exception {
		return newHttpProxyThreadNoAuth(command, waitStartupServer, PORT_AUTH, username, password);
	}
	private static HttpProxyThread newHttpProxyThreadNoAuth(String command, int waitStartupServer, int port, String username, String password) throws Exception {
		
		 boolean started = false;
		 int index = 0;
		 HttpProxyThread httpProxyThread = null;
		 int tentativi = 30; // quando succede l'errore di indirizzo già utilizzato è perchè impiega molto tempo a rilasciare la porta in ambiente jenkins
		 while(!started && index<tentativi) {
		 
			 httpProxyThread = new HttpProxyThread(command, port, username, password);
			 try {
				 try {
					 httpProxyThread.start();
					 System.out.println("START, sleep ...");
					
					 Utilities.sleep(waitStartupServer);
				 }catch(Throwable t) {
					 // ignore
				 }
				 
				 started = httpProxyThread.debugMsg(false);
			 }finally {
				 if(!started) {
					 index++;
					 System.out.println("NOT STARTED (iteration: "+index+")");
					 // rilascio risorse
					 httpProxyThread.setStop(true);
					 httpProxyThread.waitShutdown(200, 10000);
					 httpProxyThread.close();
				 }
			 }
			 			 
		 }
		
		 System.out.println("STARTED");
		 
		 return httpProxyThread;
	}
	
	public static void stopHttpProxyThread(HttpProxyThread httpProxyThread, int waitStopServer) throws Exception {
		httpProxyThread.setStop(true);
		httpProxyThread.waitShutdown(200, 10000);
		httpProxyThread.close();
		
		// anche se il processo esce, il rilascio della porta serve impiega più tempo
		try {
			System.out.println("STOP, sleep ...");
			
			Utilities.sleep(waitStopServer);
		}catch(Throwable t) {
			// ignore
		}
		System.out.println("STOP");
	}
}
