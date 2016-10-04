/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.activation.CommandInfo;
import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;

import org.slf4j.Logger;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;

/**
 * DataContentHandlerManager
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DataContentHandlerManager {

	private Logger log;
	
	public DataContentHandlerManager(Logger log){
		this.log = log;
	}
	
	public void initMailcap(){
		// NOTA, anche se gia' esistenti le registrazioni, è importante farlo caricare per fargliele vedere.
		
		try{
			this.log.info("Search mailcap.default ...");
			Enumeration<URL> en = DataContentHandlerManager.class.getClassLoader().getResources("/META-INF/mailcap.default");
			if(en!=null){
				while (en.hasMoreElements()) {
					URL url = (URL) en.nextElement();
					this.log.info("Find mailcap.default ["+url.toString()+"]");
					this.log.info("Read mailcap.default ["+url.toString()+"] ...");
					byte[] tmp = Utilities.getAsByteArray(url);
					this.log.info("Read mailcap.default ["+url.toString()+"]: "+new String(tmp));
					addMimeTypesIntoMailcap(tmp);
				}
			}
		}catch(Exception e){
			this.log.error("Mailcap.default search error: "+e.getMessage(),e);
		}
		
		try{
			this.log.info("Search mailcap ...");
			Enumeration<URL> en = DataContentHandlerManager.class.getClassLoader().getResources("/META-INF/mailcap");
			if(en!=null){
				while (en.hasMoreElements()) {
					URL url = (URL) en.nextElement();
					this.log.info("Find mailcap ["+url.toString()+"]");
					this.log.info("Read mailcap ["+url.toString()+"] ...");
					byte[] tmp = Utilities.getAsByteArray(url);
					this.log.info("Read mailcap ["+url.toString()+"]: "+new String(tmp));
					addMimeTypesIntoMailcap(tmp);
				}
			}
		}catch(Exception e){
			this.log.error("Mailcap search error: "+e.getMessage(),e);
		}
	}
	
	
	public List<String> readMimeTypesRegistrati() {
		
		MailcapCommandMap mcap = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
		
		String [] gestiti = mcap.getMimeTypes();
		List<String> gestitiAsList = new ArrayList<String>();
		if(gestiti!=null){
			this.log.info("MimeTypes registrati: ["+gestiti.length+"]");
			for (int i = 0; i < gestiti.length; i++) {
				this.log.info("MimeType registrato: ["+gestiti[i]+"]");
				gestitiAsList.add(gestiti[i]);
				CommandInfo [] cis = mcap.getAllCommands(gestiti[i]);
				for (int j = 0; j < cis.length; j++) {
					this.log.info("\t["+j+"] "+cis[j].getCommandName()+" = "+cis[j].getCommandClass());
				}
			}
		}
		else{
			this.log.info("Non risultano registrati MimeTypes");
		}
		
		return gestitiAsList;
	}
	
	public void addMimeTypeIntoMailcap(String commandInfo) throws UtilsException{
		
		if(commandInfo.contains(";;")==false){
			throw new UtilsException("Formato non corretto, non è stato riscontrato il carattere ';;'");
		}
		String [] split = commandInfo.split(";;");
		if(split==null || split.length!=2){
			throw new UtilsException("Formato non corretto");
		}
		
		MailcapCommandMap mcap = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
		mcap.addMailcap(commandInfo);
		CommandMap.setDefaultCommandMap(mcap);
		
		this.log.info("Registrato in Mailcap il mimetype ["+split[0]+"]: "+commandInfo);
	}
	
	public void addMimeTypesIntoMailcap(byte[] mailcap) throws IOException, UtilsException{
		
		List<String> gestitiAsList = this.readMimeTypesRegistrati();
		
		BufferedReader br = null;
		InputStreamReader isr = null;
		ByteArrayInputStream bin = null;
		try{
			bin = new ByteArrayInputStream(mailcap);
			isr = new InputStreamReader(bin);
			br = new BufferedReader(isr);
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				// Print the content on the console
				//System.out.println (strLine);
				if(strLine!=null && strLine.startsWith("#")==false){
					if(strLine.contains(";;")){
						String [] split = strLine.split(";;");
						if(gestitiAsList.contains(split[0])==false){
							gestitiAsList.add(split[0]);
							this.addMimeTypeIntoMailcap(strLine);
						}
					}
				}
			}
		}finally{
			try{
				br.close();
			}catch(Exception eClose){}
			try{
				isr.close();
			}catch(Exception eClose){}
			try{
				bin.close();
			}catch(Exception eClose){}
		}
		
	}
	
}
