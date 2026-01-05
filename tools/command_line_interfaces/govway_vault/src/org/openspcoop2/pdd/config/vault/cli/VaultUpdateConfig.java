/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.utils.certificate.byok.BYOKManager;

/**
* VaultUpdateConfig
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class VaultUpdateConfig {

	public static final String SECURITY_IN="-sec_in";
	public static final String SECURITY_OUT="-sec_out";
	
	/** NO! I secreti interni a GovWay sono protetti con security public static final String KMS_IN="-kms_in";
	public static final String KMS_OUT="-kms_out";*/
	
	public static final String PLAIN_IN="-plain_in";
	public static final String PLAIN_OUT="-plain_out";
	
	public static final String REPORT="-report";
	
	private static final String UNKNOW_OPTION = "(unknown option '";
	
	public static String getUsage() {
		return SECURITY_IN+"|"+
				/**KMS_IN+"|"+*/
				PLAIN_IN+
				"[=id] "+
				SECURITY_OUT+"|"+
				/**KMS_OUT+"|"+*/
				PLAIN_OUT+")[=id] "+
				"["+REPORT+"=path]";
	}
	
	private boolean inSecurityMode = false;
	/**private boolean inKmsMode = false;*/
	private boolean inPlainMode = false;
	private String inId = null;
	
	private boolean outSecurityMode = false;
	/**private boolean outKmsMode = false;*/
	private boolean outPlainMode = false;
	private String outId = null;
	
	private String reportPath = null;
	
	public VaultUpdateConfig(String[] args, String utilizzoErrato) throws CoreException{
		if(args.length<2 || args[0]==null || args[1]==null) {
			throw new CoreException(utilizzoErrato);
		}
			
		parseFirstArgument(args, utilizzoErrato);
		parseSecondArgument(args, utilizzoErrato);
		
		if(args.length>2 && args[2]!=null) {
			parseThirdArgument(args, utilizzoErrato);
		}
	}
	
	private void parseFirstArgument(String[] args, String utilizzoErrato) throws CoreException {
		if(!(args[0].contains("="))) {
			if(PLAIN_IN.equals(args[0])) {
				this.inPlainMode=true;
			}
			else {
				throw new CoreException("(= not found in first param '"+args[0]+"') "+utilizzoErrato);
			}
		}
		else {
			if(args[0].startsWith(SECURITY_IN+"=") && args[0].length()>(SECURITY_IN+"=").length()) {
				this.inSecurityMode=true;
				this.inId = args[0].substring((SECURITY_IN+"=").length());
			}
			/**else if(args[0].startsWith(KMS_IN+"=") && args[0].length()>(KMS_IN+"=").length()) {
				this.inKmsMode=true;
				this.inId = args[0].substring((KMS_IN+"=").length());
			}*/
			else {
				throw new CoreException(UNKNOW_OPTION+args[0]+"') "+utilizzoErrato);
			}
		}
	}
	
	private void parseSecondArgument(String[] args, String utilizzoErrato) throws CoreException {
		if(!(args[1].contains("="))) {
			if(PLAIN_OUT.equals(args[1])) {
				this.outPlainMode=true;
			}
			else {
				throw new CoreException("(= not found in second param '"+args[1]+"') "+utilizzoErrato);
			}
		}
		else {
			if(args[1].startsWith(SECURITY_OUT+"=") && args[1].length()>(SECURITY_OUT+"=").length()) {
				this.outSecurityMode=true;
				this.outId = args[1].substring((SECURITY_OUT+"=").length());
			}
			/**else if(args[1].startsWith(KMS_OUT+"=") && args[1].length()>(KMS_OUT+"=").length()) {
				this.outKmsMode=true;
				this.outId = args[1].substring((KMS_OUT+"=").length());
			}*/
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
			if(args[2].startsWith(REPORT+"=") && args[2].length()>(REPORT+"=").length()) {
				this.reportPath = args[2].substring((REPORT+"=").length());
			}
			else {
				throw new CoreException(UNKNOW_OPTION+args[2]+"') "+utilizzoErrato);
			}
			
			File fFilePath = new File(this.reportPath);
			String prefix = "(File '"+fFilePath.getAbsolutePath()+"' ";
			if(fFilePath.exists()) {
				throw new CoreException(prefix+"already exists) "+utilizzoErrato);
			}
		}
	}
	
	public boolean isInSecurityMode() {
		return this.inSecurityMode;
	}

	/**public boolean isInKmsMode() {
		return this.inKmsMode;
	}*/

	public boolean isInPlainMode() {
		return this.inPlainMode;
	}

	public String getInId() {
		return this.inId;
	}

	public boolean isOutSecurityMode() {
		return this.outSecurityMode;
	}

	/**public boolean isOutKmsMode() {
		return this.outKmsMode;
	}*/

	public boolean isOutPlainMode() {
		return this.outPlainMode;
	}

	public String getOutId() {
		return this.outId;
	}

	public String getReportPath() {
		return this.reportPath;
	}
	
	public void validate(BYOKManager byokManager) throws CoreException {
		
		/**String kmsPrefix = "Kms '";*/
		
		if(this.inSecurityMode && !byokManager.existsSecurityEngineByType(this.inId)){
			throw new CoreException("Security policy '"+this.inId+"' not found");
		}
		/**if(this.inKmsMode && !byokManager.existsKMSConfigByType(this.inId)) {
			throw new CoreException(kmsPrefix+this.inId+"' not exists");
		}
		else if(this.inKmsMode && !byokManager.isKMSUsedInSecurityUnwrapConfig(this.inId, new StringBuilder())) {
			throw new CoreException(kmsPrefix+this.inId+"' unusable for unwrap operation");
		}*/
		
		if(this.outSecurityMode && !byokManager.existsSecurityEngineByType(this.outId)){
			throw new CoreException("Security policy '"+this.outId+"' not found");
		}
		/**if(this.outKmsMode && !byokManager.existsKMSConfigByType(this.outId)) {
			throw new CoreException(kmsPrefix+this.outId+"' not exists");
		}
		else if(this.outKmsMode && !byokManager.isKMSUsedInSecurityWrapConfig(this.outId, new StringBuilder())) {
			throw new CoreException(kmsPrefix+this.outId+"' unusable for wrap operation");
		}*/
	}
}
