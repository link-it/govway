/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

package org.openspcoop2.utils.rest.api;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.beans.BaseBean;
import org.openspcoop2.utils.rest.ProcessingException;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;

/**
 * ApiOperation
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12564 $, $Date: 2017-01-11 14:31:31 +0100 (Wed, 11 Jan 2017) $
 */
public class ApiUtilities extends BaseBean {

	public static ApiOperation findOperation(Api api, HttpRequestMethod httpMethod, String url, boolean exactlyLength) throws ProcessingException{

		String[] urlList = extractUrlList(api.getBaseURL().toString(), url);

		return getOperation(urlList, api, httpMethod, exactlyLength);
	}
	private static String[] extractUrlList(String baseURI, String url) throws ProcessingException{
		if(url == null)
			throw new ProcessingException("URL non fornita");

		List<String> urlList = new ArrayList<String>();

		if(baseURI != null) {
			if(url.startsWith(baseURI)) {
				url = url.substring(baseURI.length(), url.length());
			}
		}

		for(String s : url.split("/")) {
			if(s!=null && !s.equals("")) {
				urlList.add(s);
			}
		}

		return urlList.toArray(new String[] {});

	}
	
	private static ApiOperation getOperation(String[] url, Api api, HttpRequestMethod httpMethod, boolean exactlyLength) throws ProcessingException{

		int levelExactlyMatch = -1;
		int levelDinamicMatch = -1;
		ApiOperation apiOpExactlyMatch = null; 
		ApiOperation apiOpDynamicMatch = null; 
		for(int i = 0; i< api.sizeOperations(); i++) {
			
			ApiOperation apiOp = api.getOperation(i);
			if(!apiOp.getHttpMethod().equals(httpMethod)){
				continue;
			}
			if(url.length<apiOp.sizePath()){
				continue;
			}
			int counterMatch = 0;
			boolean exactlyMatch = true;
			boolean dynamicMatch = true;
			for (int j = 0; j < apiOp.sizePath(); j++) {
				String path = null;
				try{
					path = apiOp.getPath(j);
				}catch(ProcessingException pe){}
				if(path!=null && !path.equalsIgnoreCase(url[j])){
					exactlyMatch = false;
					if(!apiOp.isDynamicPath(j)){
						dynamicMatch = false;
						break;
					}
				}
				counterMatch++;
			}
			if(exactlyMatch){
				if(counterMatch>levelExactlyMatch){
					levelExactlyMatch=counterMatch;
					apiOpExactlyMatch = apiOp;
				}
			}
			else if(dynamicMatch){
				// dynamic
				if(counterMatch>levelDinamicMatch){
					levelDinamicMatch=counterMatch;
					apiOpDynamicMatch = apiOp;
				}
			}
			
		}
		
		if(exactlyLength){
			if(levelExactlyMatch==url.length){
				return apiOpExactlyMatch;
			}
			else if(levelDinamicMatch==url.length){
				return apiOpDynamicMatch;
			}
			else{
				return null;
			}
		}
		else{
			if(levelExactlyMatch>levelDinamicMatch){
				return apiOpExactlyMatch;
			}
			else{
				return apiOpDynamicMatch;
			}
		}
		
	}
	
}
