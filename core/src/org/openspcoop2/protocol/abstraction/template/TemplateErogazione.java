/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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
 * TemplateErogazione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TemplateErogazione extends TemplateCore {

	public TemplateErogazione() throws ProtocolException{
		
		super(true);
		
		try{
			String baseUrl = "/"+CostantiAbstraction.TEMPLATES_DIR+"/"+CostantiAbstraction.TEMPLATES_EROGAZIONE_DIR+"."+CostantiAbstraction.ZIP_EXTENSION;
			InputStream is = TemplateErogazione.class.getResourceAsStream(baseUrl);
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
	
	private List<byte[]> accordiParteSpecifica = new ArrayList<byte[]>();
	private List<byte[]> serviziApplicativi = new ArrayList<byte[]>();
	private List<byte[]> porteApplicative = new ArrayList<byte[]>();
	
	
	public List<byte[]> getAccordiParteSpecifica() {
		return this.accordiParteSpecifica;
	}
	public void setAccordiParteSpecifica(List<byte[]> accordiParteSpecifica) {
		this.accordiParteSpecifica = accordiParteSpecifica;
	}
	public List<Template> getTemplateAccordiParteSpecifica() throws IOException {
		List<Template> templates = new ArrayList<Template>();
		for (int i = 0; i < this.accordiParteSpecifica.size(); i++) {
			templates.add(TemplateUtils.buildTemplate("asps_"+"i", this.accordiParteSpecifica.get(i)));
		}
		return templates;
	}
	
	
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
	
	public List<byte[]> getPorteApplicative() {
		return this.porteApplicative;
	}
	public void setPorteApplicative(List<byte[]> porteApplicative) {
		this.porteApplicative = porteApplicative;
	}
	public List<Template> getTemplatePorteApplicative() throws IOException {
		List<Template> templates = new ArrayList<Template>();
		for (int i = 0; i < this.porteApplicative.size(); i++) {
			templates.add(TemplateUtils.buildTemplate("pa_"+"i", this.porteApplicative.get(i)));
		}
		return templates;
	}
	
	
	@Override
	public void updateOtherResource(String entryName,InputStream inputStream,byte[]xml,Hashtable<String, Boolean> mapFound) throws ProtocolException{
		
		String rootDirNameExpected = this.getRootDirName()+File.separatorChar;
		
		if(entryName.startsWith(rootDirNameExpected+CostantiAbstraction.TEMPLATES_ACCORDI_PARTE_SPECIFICA+File.separatorChar) ){
			if(mapFound.containsKey("asps")==false || mapFound.get("asps")==false){
				this.getAccordiParteSpecifica().clear();
			}
			this.getAccordiParteSpecifica().add(xml);
			mapFound.put("asps", true);
		}
		else if(entryName.startsWith(rootDirNameExpected+CostantiAbstraction.TEMPLATES_PORTE_APPLICATIVE+File.separatorChar) ){
			if(mapFound.containsKey("pa")==false || mapFound.get("pa")==false){
				this.getPorteApplicative().clear();
			}
			this.getPorteApplicative().add(xml);
			mapFound.put("pa", true);
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
