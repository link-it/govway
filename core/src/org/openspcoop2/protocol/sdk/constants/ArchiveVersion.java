/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.sdk.constants;

import java.io.BufferedReader;
import java.io.StringReader;


/**
 *  ArchiveVersion
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum ArchiveVersion {

	V_UNDEFINED, // indica una versione precedente a qualsiasi sistema di versionamento
	V_1,
	V_OTHER; // indica la prima versione in cui sono stati introdotti i sistemi di versionamento 

	public static final ArchiveVersion OPENSPCOOP2_ARCHIVE_ACTUAL_VERSION = ArchiveVersion.V_1;
	
	public static ArchiveVersion toArchiveVersion(byte [] content){
		return toArchiveVersion(new String(content));
	} 
	public static ArchiveVersion toArchiveVersion(String content){
		if(content == null){
			return V_UNDEFINED;
		}
		
		StringReader sr = new StringReader(content);
		BufferedReader br = new BufferedReader(sr);
		try{
			String v = br.readLine();
			
			if(V_1.name().equals(v)){
				return V_1;
			}
			else{
				return V_OTHER; // se non comprendo la versione
			}
		}catch(Exception e){
			return V_UNDEFINED;
		}
	}
	public static String toProductVersion(byte[] content){
		return toProductVersion(new String(content));
	}
	public static String toProductVersion(String content){
		if(content == null){
			return null;
		}
		
		StringReader sr = new StringReader(content);
		BufferedReader br = new BufferedReader(sr);
		try{
			String v = br.readLine();
			StringBuffer bf = new StringBuffer();
			while((v=br.readLine())!=null){
				if(bf.length()>0){
					bf.append("\n");
				}
				bf.append(v);
			}
			return bf.toString();
			
		}catch(Exception e){
			return null;
		}
	}
	
	public static String getContentFileVersion(String pddVersion){
		return OPENSPCOOP2_ARCHIVE_ACTUAL_VERSION.name()+"\n"+pddVersion;
	}
}
