/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
package org.openspcoop2.utils.certificate.ocsp.test;

import java.io.File;
import java.io.InputStream;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.AbstractBaseThread;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
 * OpenSSLThread
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSSLThread extends AbstractBaseThread {

	private Process process;
	private File fCert;
	private File fKey;
	private File fCA;
	private File fIndex;	
	private File fScript;	
	private InputStream isDebug;
	private InputStream isError;
	
	public OpenSSLThread(String command, int port, String fCert, String fKey, String fCA, String fIndex, boolean forceWrongNonceResponseValue) throws Exception {
		try(InputStream is = OCSPTest.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/test/"+fCert)){
			byte[] content = Utilities.getAsByteArray(is);
			this.fCert = File.createTempFile("cert", ".pem");
			FileSystemUtilities.writeFile(this.fCert, content);
		}
		try(InputStream is = OCSPTest.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/test/"+fKey)){
			byte[] content = Utilities.getAsByteArray(is);
			this.fKey = File.createTempFile("cert", ".key");
			FileSystemUtilities.writeFile(this.fKey, content);
		}
		try(InputStream is = OCSPTest.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/test/"+fCA)){
			byte[] content = Utilities.getAsByteArray(is);
			this.fCA = File.createTempFile("certificateAuthority", ".pem");
			FileSystemUtilities.writeFile(this.fCA, content);
		}
		try(InputStream is = OCSPTest.class.getResourceAsStream("/org/openspcoop2/utils/certificate/ocsp/test/"+fIndex)){
			byte[] content = Utilities.getAsByteArray(is);
			this.fIndex = File.createTempFile("index", ".txt");
			FileSystemUtilities.writeFile(this.fIndex, content);
		}
		
		String com = "/usr/bin/openssl";
		if(command!=null) {
			com = command;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("#!/bin/bash\n\n").append(com);
		sb.append(" ocsp");
		sb.append(" -index ").append(this.fIndex.getAbsolutePath());
		sb.append(" -port ").append(port+"");
		sb.append(" -rsigner ").append(this.fCert.getAbsolutePath());
		sb.append(" -rkey ").append(this.fKey.getAbsolutePath());
		sb.append(" -CA ").append(this.fCA.getAbsolutePath());
		sb.append(" -text ");
		sb.append(" -out /tmp/ocsp.log");
		if(forceWrongNonceResponseValue) {
			sb.append(" -nonce ");
		}
		
		this.fScript = File.createTempFile("startOCSPResponder", ".sh");
		FileSystemUtilities.writeFile(this.fScript, sb.toString().getBytes());
		this.fScript.setExecutable(true);
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
		
		if(this.fCert!=null) {
			this.fCert.delete();
			this.fCert = null;
		}
		if(this.fKey!=null) {
			this.fKey.delete();
			this.fKey = null;
		}
		if(this.fCA!=null) {
			this.fCA.delete();
			this.fCA = null;
		}
		if(this.fIndex!=null) {
			this.fIndex.delete();
			this.fIndex = null;
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
}
