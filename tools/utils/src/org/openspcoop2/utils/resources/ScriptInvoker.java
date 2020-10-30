/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;

/**
* ScriptInvoker
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class ScriptInvoker {

	private String script = null;
	private int exitValue = -1;
	private String outputStream = null;
	private String errorStream = null;

	public ScriptInvoker(String script){
		this.script = script;
	}
	public ScriptInvoker(){
	}
	
	public void run() throws UtilsException{
		this.run("");
	}
	

	public void run(String ... parameters) throws UtilsException {
		this.run(null, parameters);
	}
	
	
	public void run(File workingDir) throws UtilsException {
		this.run((File)null, "");
	}
	
	public void run(File workingDir, String ... parameters) throws UtilsException{
		
		try{
		
			if(this.script==null){
				throw new UtilsException("Script non fornito");
			}
			
			java.lang.Runtime runtime = java.lang.Runtime.getRuntime();
	
			// Invoco lo script
			List<String> script = new ArrayList<String>();
			script.add(this.script);
			if(parameters!=null){
				for (int i = 0; i < parameters.length; i++) {
					script.add(parameters[i]);
				}
			}
	
			java.lang.Process processStatus = null;
			if (workingDir!=null) {
				processStatus = runtime.exec(script.toArray(new String[1]),null,workingDir);
			} else {
				processStatus = runtime.exec(script.toArray(new String[1]));
			}
			
			java.io.BufferedInputStream berror = new java.io.BufferedInputStream(processStatus.getErrorStream());
			java.io.BufferedInputStream bin = new java.io.BufferedInputStream(processStatus.getInputStream());

			boolean terminated = false;
			while(terminated == false){
				try{
					Utilities.sleep(500);
					this.exitValue = processStatus.exitValue();
					terminated = true;
				}catch(java.lang.IllegalThreadStateException exit){}
			}

			StringBuilder stampa = new StringBuilder();
			int read = 0;
			while((read = bin.read())!=-1){
				stampa.append((char)read);
			}
			if(stampa.length()>0){
				this.outputStream = stampa.toString();
			}
				
			StringBuilder stampaError = new StringBuilder();
			read = 0;
			while((read = berror.read())!=-1){
				stampaError.append((char)read);
			}
			if(stampaError.length()>0)
				this.errorStream = stampaError.toString();
						
		}catch(Exception e){
			throw new UtilsException(e.getMessage(), e);
		}
	}

	public int getExitValue() {
		return this.exitValue;
	}

	public void setExitValue(int exitValue) {
		this.exitValue = exitValue;
	}

	public String getOutputStream() {
		return this.outputStream;
	}

	public void setOutputStream(String outputStream) {
		this.outputStream = outputStream;
	}

	public String getErrorStream() {
		return this.errorStream;
	}

	public void setErrorStream(String errorStream) {
		this.errorStream = errorStream;
	}

	public String getScript() {
		return this.script;
	}

	public void setScript(String script) {
		this.script = script;
	}
	
}
