/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.protocol.abstraction.constants.CostantiAbstraction;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.TemplateUtils;

import freemarker.template.Template;

/**     
 * TemplateFruizione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TemplateFruizione extends TemplateCore {

	public TemplateFruizione() throws ProtocolException{
		
		super(false);
		
		try{
			String baseUrl = "/"+CostantiAbstraction.TEMPLATES_DIR+"/"+CostantiAbstraction.TEMPLATES_FRUIZIONE_DIR+"."+CostantiAbstraction.ZIP_EXTENSION;
			InputStream is = TemplateFruizione.class.getResourceAsStream(baseUrl);
			if(is==null){
				throw new ProtocolException("Resource with url ["+baseUrl+"] not found");
			}
			byte[]zipFile = Utilities.getAsByteArray(is);
			is.close();
			this.updateTemplates(zipFile);
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
		
	}
	
	private List<byte[]> serviziApplicativi = new ArrayList<byte[]>();
	private List<byte[]> porteDelegate = new ArrayList<byte[]>();
		
	public List<byte[]> getServiziApplicativi() {
		return this.serviziApplicativi;
	}
	public void setServiziApplicativi(List<byte[]> serviziApplicativi) {
		this.serviziApplicativi = serviziApplicativi;
	}
	public List<Template> getTemplateServiziApplicativi() throws IOException {
		List<Template> templates = new ArrayList<Template>();
		for (int i = 0; i < this.serviziApplicativi.size(); i++) {
			templates.add(TemplateUtils.buildTemplate("sa_"+"i", this.serviziApplicativi.get(i)));
		}
		return templates;
	}
	
	public List<byte[]> getPorteDelegate() {
		return this.porteDelegate;
	}
	public void setPorteDelegate(List<byte[]> porteDelegate) {
		this.porteDelegate = porteDelegate;
	}
	public List<Template> getTemplatePorteDelegate() throws IOException {
		List<Template> templates = new ArrayList<Template>();
		for (int i = 0; i < this.porteDelegate.size(); i++) {
			templates.add(TemplateUtils.buildTemplate("pd_"+"i", this.porteDelegate.get(i)));
		}
		return templates;
	}
	
	
	

	@Override
	public void updateOtherResource(String entryName,InputStream inputStream,byte[]xml,Hashtable<String, Boolean> mapFound) throws ProtocolException{
		
		String rootDirNameExpected = this.getRootDirName()+File.separatorChar;
		
		if(entryName.startsWith(rootDirNameExpected+CostantiAbstraction.TEMPLATES_PORTE_DELEGATE+File.separatorChar) ){
			if(mapFound.containsKey("pd")==false || mapFound.get("pd")==false){
				this.getPorteDelegate().clear();
			}
			this.getPorteDelegate().add(xml);
			mapFound.put("pd", true);
		}
		else if(entryName.startsWith(rootDirNameExpected+CostantiAbstraction.TEMPLATES_SERVIZI_APPLICATIVI+File.separatorChar) ){
			if(mapFound.containsKey("sa")==false || mapFound.get("sa")==false){
				this.getServiziApplicativi().clear();
			}
			this.getServiziApplicativi().add(xml);
			mapFound.put("sa", true);
		}
		else{
			throw new ProtocolException("Elemento ["+entryName+"] non atteso");
		}
		
	}
	
}
