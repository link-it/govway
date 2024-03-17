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
package org.openspcoop2.core.protocolli.trasparente.testsuite.tracciamento;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.monitor.sdk.transaction.FaseTracciamento;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
* FileTraceParser
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class FileTraceParser {

	public static FileTraceParser parse( FaseTracciamento fase, boolean client, boolean server, 
			String contentRequest, String contentResponse, 
			boolean expected, boolean tokenInfoExpected,
			String logDiagnostico) throws FileNotFoundException, UtilsException {
		
		File file = null;
		boolean token = false;
		switch (fase) {
		case IN_REQUEST:
			file = TestTracciamentoCostanti.fileTraceInRequestProps;
			break;
		case OUT_REQUEST:
			file = TestTracciamentoCostanti.fileTraceOutRequestProps;
			token = tokenInfoExpected;
			break;
		case OUT_RESPONSE:
			file = TestTracciamentoCostanti.fileTraceOutResponseProps;
			token = tokenInfoExpected;
			break;
		case POST_OUT_RESPONSE:
			file = TestTracciamentoCostanti.fileTracePostOutResponseProps;
			token = tokenInfoExpected;
			break;
		}
		
		if(FaseTracciamento.POST_OUT_RESPONSE.equals(fase)) {
			Utilities.sleep(100);
			if(!file.exists() || file.length()<=0) {
				Utilities.sleep(500);
			}
			if(!file.exists() || file.length()<=0) {
				Utilities.sleep(2000);
			}
			if(!file.exists() || file.length()<=0) {
				Utilities.sleep(5000);
			}
		}
		
		if(file.exists() && file.length()>0) {
			if(!expected) {
				throw new UtilsException("File '"+file.getAbsolutePath()+"' non atteso");
			}
			FileTraceParser f = new FileTraceParser(file);
			f.check(fase, client, server, contentRequest, contentResponse, token,
					logDiagnostico);
			return f;
		}
		else {
			if(expected) {
				if(file.exists()) {
					throw new UtilsException("File '"+file.getAbsolutePath()+"' vuoto");
				}
				else {
					throw new UtilsException("File '"+file.getAbsolutePath()+"' non esistente");
				}
			}
			return null;
		}
		
	}
	
	private Map<String, String> values = new HashMap<>();
	private File file;
	
	public FileTraceParser(File file) throws FileNotFoundException, UtilsException {
		
		this.file = file;
		
		String content = FileSystemUtilities.readFile(file);
		
		String [] split = content.split("\\|");
		if(split==null || split.length==0) {
			throw new UtilsException("Invalid content in file '"+file.getAbsolutePath()+"'");
		}
		for (String s : split) {
			int index = s.indexOf(":");
			if(index<=0){
				throw new UtilsException("Invalid content in file '"+file.getAbsolutePath()+"', token ["+s+"]");
			}
			String key = s.substring(0,index);
			String value = s.substring(index+1, s.length());
			/**System.out.println("ADD ["+key+"]=["+value+"]");*/
			this.values.put(key, value);
		}
		
	}
	
	private String getPrefix(boolean in, boolean request) {
		if(in && request) {
			return "InRequest";
		}
		else if(!in && request) {
			return "OutRequest";
		}
		else if(in && !request) {
			return "InResponse";
		}
		else {
			return "OutResponse";
		}
	}
	
	public void check(FaseTracciamento fase, boolean client, boolean server, 
			String contentRequest, String contentResponse, 
			boolean tokenInfoExpected,
			String logDiagnostico) throws UtilsException {
		try {
			String v = this.values.get("Fase");
			if(!fase.name().equals(v)) {
				throw new Exception("Attesa fase '"+fase+"' trovata '"+v+"'");
			}
			
			checkContent(fase, client, server, contentRequest, contentResponse,
					logDiagnostico);
			
			String tokenInfo = this.values.get("TokenInfo");
			if(tokenInfo==null || StringUtils.isEmpty(tokenInfo.trim())) {
				if(tokenInfoExpected) {
					throw new UtilsException("Atteso tokenInfo non trovato");
				}
			}
			else {
				if(!tokenInfoExpected) {
					throw new UtilsException("TokenInfo non atteso: ["+tokenInfo+"]");
				}
				String c = new String(Base64Utilities.decode(tokenInfo));
				if(!(c.toLowerCase().contains("client_id".toLowerCase()))) {
					throw new UtilsException("TokenInfo claim 'client_id' non trovato in: "+c);
				}
			}
			
		}catch(Exception e) {
			throw new UtilsException("["+this.file.getAbsolutePath()+"] "+e.getMessage(),e);
		}
	}
	
	public void checkContent(FaseTracciamento fase, boolean client, boolean server, String contentRequest, String contentResponse,
			String logDiagnostico) throws UtilsException {
		
		boolean erroreInRequest = TestTracciamentoCostanti.ERRORE_FILETRACE_FASE_IN_REQUEST.equals(logDiagnostico);
		boolean erroreOutRequest = TestTracciamentoCostanti.ERRORE_FILETRACE_FASE_OUT_REQUEST.equals(logDiagnostico);
		boolean erroreOutResponse = TestTracciamentoCostanti.ERRORE_FILETRACE_FASE_OUT_RESPONSE.equals(logDiagnostico);
		
		// inRequest
		String key = getPrefix(true, true);
		check(key, client, client, contentRequest);
		
		// outRequest
		key = getPrefix(false, true);
		switch (fase) {
		case IN_REQUEST:
			check(key, false, false, contentRequest);
			break;
		case OUT_REQUEST:
			check(key, (erroreInRequest || erroreOutRequest) ? false : true, false, contentRequest);
			break;
		case OUT_RESPONSE:
		case POST_OUT_RESPONSE:
			check(key, 
					(erroreInRequest || erroreOutRequest) ? false : server, 
					(erroreInRequest || erroreOutRequest) ? false : server, 
					contentRequest);
			break;
		}
		
		// inResponse
		key = getPrefix(true, false);
		switch (fase) {
		case IN_REQUEST:
		case OUT_REQUEST:
			check(key, false, false, contentResponse);
			break;
		case OUT_RESPONSE:
			check(key, 
					(erroreInRequest || erroreOutRequest) ? false : server, 
					(erroreInRequest || erroreOutRequest) ? false : server, 
					contentResponse);
			break;
		case POST_OUT_RESPONSE:
			check(key, 
					(erroreInRequest || erroreOutRequest) ? false : server, 
					(erroreInRequest || erroreOutRequest) ? false : server, 
					contentResponse);
			break;
		}
		
		// outResponse
		key = getPrefix(false, false);
		switch (fase) {
		case IN_REQUEST:
		case OUT_REQUEST:
			check(key, false, false, contentResponse);
			break;
		case OUT_RESPONSE:
			check(key, true, false, contentResponse);
			break;
		case POST_OUT_RESPONSE:
			check(key, client, client, 
					(erroreInRequest || erroreOutRequest || erroreOutResponse) ? TestTracciamentoCostanti.ERRORE_503_PREFIX  :contentResponse);
			break;
		}
	}
	private void check(String key, boolean attesiHeader, boolean attesiPayload, String content) throws UtilsException {
		String header = key+"Header";
		String headersFound = this.values.get(header);
		if(headersFound==null || StringUtils.isEmpty(headersFound.trim())) {
			if(attesiHeader) {
				throw new UtilsException("Attesi header '"+header+"' non trovati");
			}
		}
		else {
			if(!attesiHeader) {
				throw new UtilsException("Header '"+header+"' non attesi");
			}
			String c = new String(Base64Utilities.decode(headersFound));
			if(!(c.toLowerCase().contains("GovWay-Transaction-ID".toLowerCase())) && !(c.toLowerCase().contains("Content-Type".toLowerCase()))) {
				throw new UtilsException("Header 'GovWay-Transaction-ID' o 'Content-Type' non trovato in ["+c+"]");
			}
		}
		
		String payload = key+"Content";
		String contentFound = this.values.get(payload);
		if(contentFound==null || StringUtils.isEmpty(contentFound.trim())) {
			if(attesiPayload) {
				throw new UtilsException("Atteso payload '"+payload+"' non trovato");
			}
		}
		else {
			if(!attesiPayload) {
				throw new UtilsException("Payload '"+payload+"' non atteso");
			}
			String c = new String(Base64Utilities.decode(contentFound));
			if(TestTracciamentoCostanti.ERRORE_503_PREFIX.equals(content)) {
				if(c.startsWith(content)) {
					throw new UtilsException("Payload '"+payload+"' differente da quello atteso");
				}
			}
			else {
				if(!content.equals(c)) {
					throw new UtilsException("Payload '"+payload+"' differente da quello atteso");
				}
			}
		}

	}
		
}
