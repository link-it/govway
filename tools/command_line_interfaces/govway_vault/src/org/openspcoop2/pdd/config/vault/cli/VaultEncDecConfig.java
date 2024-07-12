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
package org.openspcoop2.pdd.config.vault.cli;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.utils.certificate.byok.BYOKManager;

/**
* VaultEncDecConfig
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class VaultEncDecConfig {
	
	public static final String SYSTEM_IN="-system_in";
	public static final String SYSTEM_OUT="-system_out";
	
	public static final String FILE_IN="-file_in";
	public static final String FILE_OUT="-file_out";
	
	public static final String SECURITY="-sec";
	public static final String KSM="-ksm";
		
	public static String getUsage() {
		return SYSTEM_IN+"|"+FILE_IN+"=text|path "+SYSTEM_OUT+"|"+FILE_OUT+"=path ["+SECURITY+"|"+KSM+"=id]";
	}
	
	private static final String UNKNOW_OPTION = "(unknown option '";
	
	private boolean encodingMode = false;
	
	private boolean inSystemMode = false;
	private String inText = null;
	private boolean inFileMode = false;
	private String inFilePath = null;
		
	private boolean outSystemMode = false;
	private boolean outFileMode = false;
	private String outFilePath = null;
	
	private boolean securityMode = true; // default
	private boolean ksmMode = false;
	private String id= null;
	
	public VaultEncDecConfig(String[] args, String utilizzoErrato, boolean encodingMode) throws CoreException{
		if(args.length<2 || args[0]==null || args[1]==null) {
			throw new CoreException(utilizzoErrato);
		}
			
		this.encodingMode = encodingMode;
		
		parseFirstArgument(args, utilizzoErrato);
		parseSecondArgument(args, utilizzoErrato);
		
		if(args.length>2 && args[2]!=null) {
			parseThirdArgument(args, utilizzoErrato);
		}
	}
	
	private void parseFirstArgument(String[] args, String utilizzoErrato) throws CoreException {
		if(!(args[0].contains("="))) {
			throw new CoreException("(= not found in first param '"+args[0]+"') "+utilizzoErrato);
		}
		else {
			if(args[0].startsWith(SYSTEM_IN+"=") && args[0].length()>(SYSTEM_IN+"=").length()) {
				this.inSystemMode=true;
				this.inText = args[0].substring((SYSTEM_IN+"=").length());
			}
			else if(args[0].startsWith(FILE_IN+"=") && args[0].length()>(FILE_IN+"=").length()) {
				parseFirstArgumentFile(args, utilizzoErrato);
			}
			else {
				throw new CoreException(UNKNOW_OPTION+args[0]+"') "+utilizzoErrato);
			}
		}
	}
	private void parseFirstArgumentFile(String[] args, String utilizzoErrato) throws CoreException {
		this.inFileMode=true;
		this.inFilePath = args[0].substring((FILE_IN+"=").length());
		
		File fFilePath = new File(this.inFilePath);
		String prefix = "(File '"+fFilePath.getAbsolutePath()+"' ";
		if(!fFilePath.exists()) {
			throw new CoreException(prefix+"not exists) "+utilizzoErrato);
		}
		if(!fFilePath.canRead()) {
			throw new CoreException(prefix+"cannot read) "+utilizzoErrato);
		}
		if(!fFilePath.isFile()) {
			throw new CoreException(prefix+"is not a file) "+utilizzoErrato);
		}
	}
	
	private void parseSecondArgument(String[] args, String utilizzoErrato) throws CoreException {
		if(!(args[1].contains("="))) {
			if(SYSTEM_OUT.equals(args[1])) {
				this.outSystemMode=true;
			}
			else {
				throw new CoreException("(= not found in second param '"+args[1]+"') "+utilizzoErrato);
			}
		}
		else {
			if(args[1].startsWith(FILE_OUT+"=") && args[1].length()>(FILE_OUT+"=").length()) {
				this.outFileMode=true;
				this.outFilePath = args[1].substring((FILE_OUT+"=").length());
				
				File fFilePath = new File(this.outFilePath);
				String prefix = "(File '"+fFilePath.getAbsolutePath()+"' ";
				if(fFilePath.exists()) {
					throw new CoreException(prefix+"already exists) "+utilizzoErrato);
				}
			}
			else {
				throw new CoreException(UNKNOW_OPTION+args[1]+"') "+utilizzoErrato);
			}
		}
	}
	
	private void parseThirdArgument(String[] args, String utilizzoErrato) throws CoreException {
		if(!(args[2].contains("="))) {
			throw new CoreException("(= not found in third param '"+args[2]+"') "+utilizzoErrato);
		}
		else {
			if(args[2].startsWith(SECURITY+"=") && args[2].length()>(SECURITY+"=").length()) {
				this.securityMode=true;
				this.id = args[2].substring((SECURITY+"=").length());
			}
			else if(args[2].startsWith(KSM+"=") && args[2].length()>(KSM+"=").length()) {
				this.securityMode=false;
				this.ksmMode=true;
				this.id = args[2].substring((KSM+"=").length());
			}
			else {
				throw new CoreException(UNKNOW_OPTION+args[2]+"') "+utilizzoErrato);
			}
		}
	}
	
	public boolean isEncode() {
		return this.encodingMode;
	}

	public boolean isInSystemMode() {
		return this.inSystemMode;
	}

	public String getInText() {
		return this.inText;
	}

	public boolean isInFileMode() {
		return this.inFileMode;
	}

	public String getInFilePath() {
		return this.inFilePath;
	}

	public boolean isOutSystemMode() {
		return this.outSystemMode;
	}

	public boolean isOutFileMode() {
		return this.outFileMode;
	}

	public String getOutFilePath() {
		return this.outFilePath;
	}	

	public boolean isSecurityMode() {
		return this.securityMode;
	}

	public boolean isKsmMode() {
		return this.ksmMode;
	}

	public String getId() {
		return this.id;
	}
	
	public void validate(BYOKManager byokManager) throws CoreException {
		
		String ksmPrefix = "Ksm '";
		
		if(this.securityMode){
			validateSecurityMode(byokManager);
		}
		if(this.ksmMode && !byokManager.existsKSMConfigByType(this.id)) {
			throw new CoreException(ksmPrefix+this.id+"' not exists");
		}
		else if(this.ksmMode && !this.encodingMode && !byokManager.getUnwrapTypes().contains(this.id)) {
			throw new CoreException(ksmPrefix+this.id+"' unusable for unwrap operation");
		}
		else if(this.ksmMode && this.encodingMode && !byokManager.getWrapTypes().contains(this.id)) {
			throw new CoreException(ksmPrefix+this.id+"' unusable for wrap operation");
		}
	}
	public void validateSecurityMode(BYOKManager byokManager) throws CoreException {
		String policy = this.id;
		if(policy==null || StringUtils.isEmpty(policy)) {
			policy = BYOKManager.getSecurityRemoteEngineGovWayPolicy();
		}
		if(policy==null || StringUtils.isEmpty(policy)) {
			policy = BYOKManager.getSecurityEngineGovWayPolicy();
		}
		if(policy==null || StringUtils.isEmpty(policy)) {
			throw new CoreException("Security policy default undefined (BYOK Disabled?)");
		}
		if(!byokManager.existsSecurityEngineByType(policy)) {
			throw new CoreException("Security policy '"+policy+"' not found");
		}
	}
}
