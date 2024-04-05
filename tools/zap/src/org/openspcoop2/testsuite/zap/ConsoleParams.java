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
package org.openspcoop2.testsuite.zap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
 * ConsoleParams
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConsoleParams {

	public static final String CONSOLE_USAGE = "url username password scanTypes scanUrls falsePositives";
	
	public static final String RECURSE_ENABLED = "True";
	public static final String RECURSE_DISABLED = "False";
	
	public static final String SUBTREE_ONLY_ENABLED = "True";
	public static final String SUBTREE_ONLY_DISABLED = "False";
	
	public static final String INSCOPE_ONLY_ENABLED = "True";
	public static final String INSCOPE_ONLY_DISABLED = "False";
	
	
	private String baseUrl;
	private String username;
	private String password;
	private ConsoleScanTypes scanTypes;
	private SortedMap<String> scanUrls;
	private List<ConsoleFalsePositive> falsePositives;
	private String baseConfigDirName;
	
	private String usageMsg;
	private int consoleArgs;

	public ConsoleParams(String[] args) throws UtilsException, IOException {
		
		this.consoleArgs = 6;
		
		this.usageMsg = ZAPContext.PREFIX+ConsoleParams.CONSOLE_USAGE+ZAPReport.SUFFIX;
		
		String urlArg = null;
		int index = ZAPContext.START_ARGS;
		if(args.length>(index)) {
			urlArg = args[index+0];
		}
		if(urlArg==null || StringUtils.isEmpty(urlArg)) {
			throw new UtilsException("ERROR: argument 'url' undefined"+this.usageMsg);
		}
		this.baseUrl = urlArg;
		if(!this.baseUrl.endsWith("/")) {
			this.baseUrl=this.baseUrl+"/";
		}
		
		if(args.length>(index+1)) {
			this.username = args[index+1];
		}
		if(this.username==null || StringUtils.isEmpty(this.username)) {
			throw new UtilsException("ERROR: argument 'username' undefined"+this.usageMsg);
		}
		
		if(args.length>(index+2)) {
			this.password = args[index+2];
		}
		if(this.password==null || StringUtils.isEmpty(this.password)) {
			throw new UtilsException("ERROR: argument 'password' undefined"+this.usageMsg);
		}

		this.scanTypes = new ConsoleScanTypes(args, this.usageMsg, index);
		
		this.initUrls(args, index);
		
		this.initFalsePositives(args, index);
	}
	
	private void initUrls(String[] args, int index) throws UtilsException, FileNotFoundException {
		
		String scanUrlsArg = null;
		if(args.length>(index+4)) {
			scanUrlsArg = args[index+4];
		}
		if(scanUrlsArg==null || StringUtils.isEmpty(scanUrlsArg)) {
			throw new UtilsException("ERROR: argument 'scanUrls' undefined"+this.usageMsg);
		}
		
		File scanUrlsF = new File(scanUrlsArg);
		String prefix = "ERROR: file argument 'scanUrls="+scanUrlsF.getAbsolutePath()+"' ";
		if(!scanUrlsF.exists()) {
			throw new UtilsException(prefix+"not exists"+this.usageMsg);
		}
		if(!scanUrlsF.canRead()) {
			throw new UtilsException(prefix+"cannot read"+this.usageMsg);
		}
		
		this.baseConfigDirName = scanUrlsF.getParentFile().getName();
		
		String content = FileSystemUtilities.readFile(scanUrlsF);
		if(content==null || StringUtils.isEmpty(content)) {
			throw new UtilsException(prefix+"is empty"+this.usageMsg);
		}
		this.scanUrls = PropertiesUtilities.convertTextToSortedMap(content);
		if(this.scanUrls==null || this.scanUrls.isEmpty()) {
			throw new UtilsException(prefix+"is defined without url"+this.usageMsg);
		}

	}
	
	private void initFalsePositives(String[] args, int index) throws UtilsException, IOException {
		
		String falsePositivesArg = null;
		if(args.length>(index+5)) {
			falsePositivesArg = args[index+5];
		}
		if(falsePositivesArg==null || StringUtils.isEmpty(falsePositivesArg)) {
			throw new UtilsException("ERROR: argument 'falsePositives' undefined"+this.usageMsg);
		}
		
		File falsePositivesF = new File(falsePositivesArg);
		String prefix = "ERROR: file argument 'falsePositives="+falsePositivesF.getAbsolutePath()+"' ";
		if(!falsePositivesF.exists()) {
			throw new UtilsException(prefix+"not exists"+this.usageMsg);
		}
		if(!falsePositivesF.canRead()) {
			throw new UtilsException(prefix+"cannot read"+this.usageMsg);
		}
		
		String content = FileSystemUtilities.readFile(falsePositivesF);
		if(content==null || StringUtils.isEmpty(content)) {
			throw new UtilsException(prefix+"is empty"+this.usageMsg);
		}
		this.falsePositives = ConsoleFalsePositive.parse(content);

	}
	
	public String getBaseUrl() {
		return this.baseUrl;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public ConsoleScanTypes getScanTypes() {
		return this.scanTypes;
	}

	public SortedMap<String> getScanUrls() {
		return this.scanUrls;
	}

	public String getUsageMsg() {
		return this.usageMsg;
	}

	public int getConsoleArgs() {
		return this.consoleArgs;
	}
	
	public List<ConsoleFalsePositive> getFalsePositives() {
		return this.falsePositives;
	}
	
	public String getBaseConfigDirName() {
		return this.baseConfigDirName;
	}
}
