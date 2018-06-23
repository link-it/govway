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

package org.openspcoop2.protocol.abstraction.template;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.activation.FileDataSource;

import org.openspcoop2.protocol.abstraction.constants.CostantiAbstraction;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.io.ZipUtilities;
import org.openspcoop2.utils.resources.TemplateUtils;

import freemarker.template.Template;

/**     
 * TemplateCore
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TemplateCore {

	protected TemplateCore(boolean erogazione) throws ProtocolException{
		
		this.zipName = "openspcoop_core_template";
		this.rootDirName = CostantiAbstraction.TEMPLATES_CORE_DIR;
		try{
			String baseUrl = "/"+CostantiAbstraction.TEMPLATES_DIR+"/"+CostantiAbstraction.TEMPLATES_CORE_DIR+"."+CostantiAbstraction.ZIP_EXTENSION;
			InputStream is = TemplateCore.class.getResourceAsStream(baseUrl);
			if(is==null){
				throw new ProtocolException("Resource with url ["+baseUrl+"] not found");
			}
			byte[]zipFile = Utilities.getAsByteArray(is);
			is.close();
			this.updateTemplates(zipFile);
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
		
		if(erogazione){
			this.zipName = "openspcoop_erogazione_template";
			this.rootDirName = CostantiAbstraction.TEMPLATES_EROGAZIONE_DIR;
		}
		else{
			this.zipName = "openspcoop_fruizione_template";
			this.rootDirName = CostantiAbstraction.TEMPLATES_FRUIZIONE_DIR;
		}
	}
	
	private String zipName;
	private String rootDirName;
	public String getZipName() {
		return this.zipName;
	}
	public String getRootDirName() {
		return this.rootDirName;
	}

	private byte[] pdd;
	private byte[] soggetto;
	private byte[] fruitore;
	
	public byte[] getPdd() {
		return this.pdd;
	}
	public void setPdd(byte[] pdd) {
		this.pdd = pdd;
	}
	public Template getTemplatePdd() throws IOException {
		return TemplateUtils.buildTemplate("pdd",this.pdd);
	}
	
	public byte[] getSoggetto() {
		return this.soggetto;
	}
	public void setSoggetto(byte[] soggetto) {
		this.soggetto = soggetto;
	}
	public Template getTemplateSoggetto() throws IOException {
		return TemplateUtils.buildTemplate("soggetto",this.soggetto);
	}
	
	
	public byte[] getFruitore() {
		return this.fruitore;
	}
	public void setFruitore(byte[] fruitore) {
		this.fruitore = fruitore;
	}
	public Template getTemplateFruitore() throws IOException {
		return TemplateUtils.buildTemplate("fruitore",this.fruitore);
	}
	
	
	
	
	
	public void updateTemplates(byte[] zip) throws ProtocolException{
		File tmp = null;
		FileOutputStream fout = null; 
		try{
			tmp = File.createTempFile(this.zipName, ".zip");
			
			fout = new FileOutputStream(tmp);
			fout.write(zip);
			fout.flush();
			fout.close();

			this.updateTemplates(tmp);
			
			if(tmp!=null)
				tmp.delete();
			
		}catch(Exception e){
			if(tmp!=null)
				throw new ProtocolException("["+tmp.getAbsolutePath()+"] "+ e.getMessage(),e);
			else
				throw new ProtocolException(e.getMessage(),e);
		}finally{
			try{
				if(fout!=null)
					fout.close();
			}catch(Exception eClose){}
		}
		
	}
	public void updateTemplates(File zip) throws ProtocolException{
		ZipFile zipFile = null;
		try{
			zipFile = new ZipFile(zip);
			this.updateTemplates(zipFile);
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}finally{
			try{
				if(zipFile!=null)
					zipFile.close();
			}catch(Exception eClose){}
		}
	}
	public void updateTemplates(ZipFile zip) throws ProtocolException{
		try{
			
			String rootDir = null;
			
			boolean foundPdd = false;
			boolean foundSoggetto = false;
			boolean foundFruitore = false;
			Hashtable<String, Boolean> mapFound = new Hashtable<String, Boolean>();
			
			Iterator<ZipEntry> it = ZipUtilities.entries(zip, true);
			while (it.hasNext()) {
				ZipEntry zipEntry = (ZipEntry) it.next();
				String entryName = ZipUtilities.operativeSystemConversion(zipEntry.getName());
				
				//System.out.println("FILE NAME:  "+entryName);
				//System.out.println("SIZE:  "+entry.getSize());

				// Il codice dopo fissa il problema di inserire una directory nel package.
				// Commentare la riga sotto per ripristinare il vecchio comportamento.
				if(rootDir==null){
					// Calcolo ROOT DIR
					rootDir=ZipUtilities.getRootDir(entryName);
				}
				
				if(zipEntry.isDirectory()) {
					continue; // directory
				}
				else {
					FileDataSource fds = new FileDataSource(entryName);
					String nome = fds.getName();
					String tipo = nome.substring(nome.lastIndexOf(".")+1,nome.length()); 
					tipo = tipo.toUpperCase();
					//System.out.println("VERIFICARE NAME["+nome+"] TIPO["+tipo+"]");
					
					InputStream inputStream = zip.getInputStream(zipEntry);
					byte[]xml = Utilities.getAsByteArray(inputStream);

					try{
						
						// ********** configurazione ****************
						String rootDirNameExpected = this.rootDirName+File.separatorChar;
						if(rootDir.equals(rootDirNameExpected) ){
														
							if(entryName.endsWith("."+CostantiAbstraction.FTL_EXTENSION) == false ){
								throw new ProtocolException("Elemento ["+entryName+"] non atteso (E' richiesto un file template .ftl)");
							}
							
							if(entryName.startsWith(rootDirNameExpected+CostantiAbstraction.TEMPLATES_PDD+File.separatorChar) ){
								if(foundPdd==true){
									throw new ProtocolException("Elemento ["+entryName+"] non atteso (E' possibile indicare solamente una definizione di porta di dominio)");
								}
								this.pdd = xml;
								foundPdd = true;
							}
							else if(entryName.startsWith(rootDirNameExpected+CostantiAbstraction.TEMPLATES_SOGGETTO+File.separatorChar) ){
								if(foundSoggetto==true){
									throw new ProtocolException("Elemento ["+entryName+"] non atteso (E' possibile indicare solamente una definizione di soggetto)");
								}
								this.soggetto = xml;
								foundSoggetto = true;
							}
							else if(entryName.startsWith(rootDirNameExpected+CostantiAbstraction.TEMPLATES_FRUITORE+File.separatorChar) ){
								if(foundFruitore==true){
									throw new ProtocolException("Elemento ["+entryName+"] non atteso (E' possibile indicare solamente una definizione di fruitore)");
								}
								this.fruitore = xml;
								foundFruitore = true;
							}
							else{
								this.updateOtherResource(entryName, inputStream, xml, mapFound);
							}
							
						}
						else{
							throw new ProtocolException("Elemento ["+entryName+"] non atteso");
						}
						
					}finally{
						try{
							if(inputStream!=null){
								inputStream.close();
							}
						}catch(Exception eClose){}
					}
				}
			}

		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	public void updateOtherResource(String entryName,InputStream inputStream,byte[]xml,Hashtable<String, Boolean> mapFound) throws ProtocolException{
		throw new ProtocolException("Elemento ["+entryName+"] non atteso");
	}
}
