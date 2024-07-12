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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.byok.BYOKWrappedValue;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.pdd.core.byok.DriverBYOK;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.byok.BYOKInstance;
import org.openspcoop2.utils.certificate.byok.BYOKManager;
import org.openspcoop2.utils.certificate.byok.BYOKRequestParams;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
* VaultEncDecUtilities
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class VaultEncDecUtilities {

	private VaultEncDecConfig encDecConfig = null;
	
	public VaultEncDecUtilities(VaultEncDecConfig c) {
		this.encDecConfig = c;
	}
	
	public void process() throws CoreException {

		try {
			
			byte[]input = null;
			VaultTools.logCoreDebug("Lettura input ...");
			if(this.encDecConfig.isInSystemMode()) {
				input = this.encDecConfig.getInText().getBytes();
			}
			else {
				input = FileSystemUtilities.readBytesFromFile(this.encDecConfig.getInFilePath());
			}
			VaultTools.logCoreDebug("Lettura input completata");
			
			byte [] output = null;
			
			if(this.encDecConfig.isSecurityMode()) {
				output = processBySecurity(input);
			}
			else {
				output = processByKsm(input);
			}
			
			VaultTools.logCoreDebug("Serializzazione output ...");
			if(this.encDecConfig.isOutSystemMode()) {
				VaultTools.logOutput(new String(output));
			}
			else if(this.encDecConfig.isOutFileMode()) {
				File f = new File(this.encDecConfig.getOutFilePath());
				FileSystemUtilities.writeFile(f, output);
				String op = this.encDecConfig.isEncode() ? "Encrypted" : "Decrypted";
				VaultTools.logOutput(op+" content in '"+f.getAbsolutePath()+"'");
			}
			else {
				throw new CoreException("Unsupported mode");
			}
			VaultTools.logCoreDebug("Serializzazione output completata");
		}
		catch(Exception t) {
			VaultTools.logCoreError(t.getMessage(),t);
			throw new CoreException(t.getMessage(),t);
		}

	}
	public byte[] processBySecurity(byte[] input) throws UtilsException {
		String policy = this.encDecConfig.getId();
		if(policy==null || StringUtils.isEmpty(policy)) {
			policy = BYOKManager.getSecurityRemoteEngineGovWayPolicy();
		}
		if(policy==null || StringUtils.isEmpty(policy)) {
			policy = BYOKManager.getSecurityEngineGovWayPolicy();
		}
		
		VaultTools.logCoreDebug("Cifratura tramite security policy '"+policy+"' ...");
		
		DriverBYOK driver = new DriverBYOK(VaultTools.getLogCore(), policy, policy);
		
		byte [] output = null;
		if(this.encDecConfig.isEncode()) {
			BYOKWrappedValue v = driver.wrap(input);
			output = v.getWrappedValue().getBytes();
		}
		else {
			output = driver.unwrap(input);
		}
		
		if(Arrays.equals(input, output)) {
			throw new UtilsException("Unwrap failed");
		}
		
		VaultTools.logCoreDebug("Cifratura tramite security policy '"+policy+"' completata");
		
		return output;
	}
	public byte[] processByKsm(byte[] input) throws UtilsException {
		String ksmId = this.encDecConfig.getId();
		VaultTools.logCoreDebug("Cifratura tramite ksm '"+ksmId+"' ...");
		
		Map<String, Object> dynamicMap = DriverBYOK.buildDynamicMap(VaultTools.getLogCore());
		Map<String, String> inputMap = new HashMap<>();
		BYOKRequestParams requestParams = BYOKRequestParams.getBYOKRequestParamsByKsmId(ksmId, inputMap, dynamicMap);
        
		BYOKInstance instance = BYOKInstance.newInstance(VaultTools.getLogCore(), requestParams, input);
		
		byte[] output = DriverBYOK.processInstance(instance, true);
		
		VaultTools.logCoreDebug("Cifratura tramite ksm '"+ksmId+"' completata");
		
		return output;
	}
}
