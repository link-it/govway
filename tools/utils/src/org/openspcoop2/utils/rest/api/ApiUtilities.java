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

package org.openspcoop2.utils.rest.api;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.beans.BaseBean;
import org.openspcoop2.utils.rest.ProcessingException;
import org.openspcoop2.utils.rest.ValidatorException;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;

/**
 * ApiOperation
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ApiUtilities extends BaseBean {

	private static List<String> _static_list_characters = new ArrayList<String>();
	static {
		for (int i = 'A'; i <= 'Z'; i++) {
			_static_list_characters.add(((char)i)+"");
		}
		for (int i = 'a'; i <= 'z'; i++) {
			_static_list_characters.add(((char)i)+"");
		}
		String chars = ".-_*;:";
		char[] cs = chars.toCharArray();
		for (int i = 0; i < cs.length; i++) {
			_static_list_characters.add(cs[i]+"");
		}
	}
	
	public static void validatePath(String path) throws ValidatorException {
		if(path.contains("{") || path.contains("}") ) {
			
			if(path.contains("{") && !path.contains("}") ) {
				throw new ValidatorException("Dynamic path '{' without closing '}' ") ;
			}
			if(!path.contains("{") && path.contains("}") ) {
				throw new ValidatorException("Dynamic path '}' without opening '{' ") ;
			}
			int countOpen = 0;
			int countClose = 0;
			boolean open = false;
			for (int i = 0; i < path.length(); i++) {
				String charAt = path.charAt(i)+"";
				if(charAt.equals("{")) {
					countOpen++;
					open = true;
				}
				else if(charAt.equals("}")) {
					if(!open) {
						throw new ValidatorException("Dynamic path malformed; found '}' before '{'") ;
					}
					open = false;
					countClose++;
				}
			}
			if(countOpen != countClose) {
				throw new ValidatorException("Dynamic path malformed; found "+countOpen+" '{' and "+countClose+" closing '}' ") ;
			}
			
			String specialCharStart = null;
			String specialCharEnd = null;
			for (String check : _static_list_characters) {
				if(path.contains(check)==false) {
					if(specialCharStart==null) {
						specialCharStart = check;
					}
					else if(specialCharEnd==null) {
						specialCharEnd = check;
						break;
					} 
				}
			}
			String pathConverted = new String(path);
			while(pathConverted.contains("{")){
				pathConverted = pathConverted.replace("{", specialCharStart);
			}
			while(pathConverted.contains("}")){
				pathConverted = pathConverted.replace("}", specialCharStart);
			}
			
//			System.out.println("SPECIAL START: "+specialCharStart);
//			System.out.println("SPECIAL END: "+specialCharEnd);
//			System.out.println("SPECIAL pathConverted: "+pathConverted);
			
			try {
				URI.create(pathConverted);
				//System.out.println("VALIDATE PATH ("+path+")");
			}catch(Exception e) {
				//System.out.println("ERRORE ORIGINALE: "+e.getMessage());
				String msg = e.getMessage();
				while(msg.contains(pathConverted)) {
					msg = msg.replace(pathConverted, path);
				}
				//System.out.println("ERRORE CONVERTITO: "+msg);
				throw new ValidatorException("Dynamic path malformed; "+msg,e);
			}
		}
		else {
			try {
				URI.create(path);
			}catch(Exception e) {
				throw new ValidatorException("Path malformed; "+e.getMessage(),e);
			}
		}
	}
	
	
	public static ApiOperation findOperation(Api api, HttpRequestMethod httpMethod, String url, boolean exactlyLength) throws ProcessingException{

		String[] urlList = extractUrlList(api.getBaseURL(), url);

		return getOperation(urlList, api, httpMethod, exactlyLength);
	}
	public static String[] extractUrlList(URL baseURI, String url) throws ProcessingException{
		if(url == null)
			throw new ProcessingException("URL non fornita");

		List<String> urlList = new ArrayList<String>();

		if(baseURI != null) {
			if(url.startsWith(baseURI.toString())) {
				url = url.substring(baseURI.toString().length(), url.length());
			}
		}

		for(String s : url.split("/")) {
			if(s!=null && !s.equals("")) {
				urlList.add(s);
			}
		}

		return urlList.toArray(new String[] {});

	}
	
	private static ApiOperation getOperation(List<ApiOperation> list, String[] url, HttpRequestMethod httpMethod, boolean exactlyLength) throws ProcessingException{
		int levelExactlyMatch = -1;
		int levelDinamicMatch = -1;
		ApiOperation apiOpExactlyMatch = null; 
		ApiOperation apiOpDynamicMatch = null; 
		for(int i = 0; i< list.size(); i++) {
			
			ApiOperation apiOp = list.get(i);
			
			if(apiOp.getHttpMethod()!=null) {
				if(!apiOp.getHttpMethod().equals(httpMethod)){
					continue;
				}
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
	
	private static ApiOperation getOperation(String[] url, Api api, HttpRequestMethod httpMethod, boolean exactlyLength) throws ProcessingException{

		// Prima cerco operazioni con method specifico e path specifico
		// prima della versione 3.3.0.p2: L'interfaccia non permette la creazione di risorse con un metodo preciso e qualsiasi path
		// dopo la versione 3.3.0.p2: l'interfaccia consente la creazione di risorse con un metodo preciso e qualsiasi path ma controlla che non vi siano casi che possano essere inclusi in più path
		List<ApiOperation> listMethodAndPath = new ArrayList<>();
		List<ApiOperation> listQualsiasiMethodAndPath = new ArrayList<>();
		List<ApiOperation> listMethodAndQualsiasiPath = new ArrayList<>();
		List<ApiOperation> listQualsiasi = new ArrayList<>();
		for (ApiOperation apiOperation : api.getOperations()) {
			if(apiOperation.getHttpMethod()!=null && apiOperation.getPath()!=null) {
				listMethodAndPath.add(apiOperation);
			}
			else if(apiOperation.getPath()!=null) {
				listQualsiasiMethodAndPath.add(apiOperation);
			}
			else if(apiOperation.getHttpMethod()!=null) {
				if(apiOperation.getHttpMethod().equals(httpMethod)){
					listMethodAndQualsiasiPath.add(apiOperation);
				}
			}
			else {
				listQualsiasi.add(apiOperation);
			}
		}
		
		ApiOperation op = getOperation(listMethodAndPath, url, httpMethod, exactlyLength);
		if(op==null) {
			op = getOperation(listQualsiasiMethodAndPath, url, httpMethod, exactlyLength);
		}
		
		if(op!=null) {
			return op;
		}
		
		if(listMethodAndQualsiasiPath.size()>0 && listQualsiasi.size()>0) {
			throw new ProcessingException("Found more resource with path '*' (both httpMethod that '*')");
		}
		
		if(listMethodAndQualsiasiPath.size()>0) {
			if(listMethodAndQualsiasiPath.size()>1) {
				throw new ProcessingException("Found more resource with httpMethod '"+listMethodAndQualsiasiPath.get(0).getHttpMethod()+"' and path '*'");
			}
			else if(listMethodAndQualsiasiPath.size()==1) {
				return  listMethodAndQualsiasiPath.get(0);
			}
		}
		
		if(listQualsiasi.size()>0) {
			if(listQualsiasi.size()>1) {
				throw new ProcessingException("Found more resource with httpMethod '*' and path '*'");
			}
			else if(listQualsiasi.size()==1) {
				return  listQualsiasi.get(0);
			}
		}
		
		return null;
	}
	
}
