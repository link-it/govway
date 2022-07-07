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
package org.openspcoop2.pdd.core.dynamic;

import java.io.Serializable;
import java.util.Map;

/**
 * Template
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Template implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private byte[] template;
	private Map<String, byte[]> templateIncludes;
	
	public Template(String name, byte[] template) {
		this(name, template, null);
	}
	public Template(String name, byte[] template, Map<String, byte[]> templateIncludes) {
		this.name = name;
		this.template = template;
		this.templateIncludes = templateIncludes;
	}

	public String getName() {
		return this.name;
	}
	public byte[] getTemplate() {
		return this.template;
	}
	public Map<String, byte[]> getTemplateIncludes() {
		return this.templateIncludes;
	}
	
	private transient freemarker.template.Template templateFreeMarker;
	private transient org.apache.velocity.Template templateVelocity;
	private ZipTemplate templateZip;
	
	private org.openspcoop2.utils.Semaphore lock = new org.openspcoop2.utils.Semaphore("Template");
	
	private void initTemplateFreeMarker() throws DynamicException{
		if(this.templateFreeMarker==null) {
			try {
				this.lock.acquire("initTemplateFreeMarker");
			}catch(Throwable t) {
				throw new DynamicException(t.getMessage(),t);
			}
			try {
				this.templateFreeMarker = DynamicUtils.buildFreeMarkerTemplate(this);
			}finally {
				this.lock.release("initTemplateFreeMarker");
			}
		}
	}
	public freemarker.template.Template getTemplateFreeMarker() throws DynamicException{
		if(this.templateFreeMarker==null) {
			initTemplateFreeMarker();
		}
		return this.templateFreeMarker;
	}
	
	private void initTemplateVelocity() throws DynamicException{
		if(this.templateVelocity==null) {
			try {
				this.lock.acquire("initTemplateVelocity");
			}catch(Throwable t) {
				throw new DynamicException(t.getMessage(),t);
			}
			try {
				this.templateVelocity = DynamicUtils.buildVelocityTemplate(this);
			}finally {
				this.lock.release("initTemplateVelocity");
			}
		}
	}
	public org.apache.velocity.Template getTemplateVelocity() throws DynamicException{
		if(this.templateVelocity==null) {
			initTemplateVelocity();
		}
		return this.templateVelocity;
	}
	
	private void initTemplateZip() throws DynamicException{
		if(this.templateZip==null) {
			try {
				this.lock.acquire("initTemplateZip");
			}catch(Throwable t) {
				throw new DynamicException(t.getMessage(),t);
			}
			try {
				this.templateZip = new ZipTemplate(this.name, this.template);
			}finally {
				this.lock.release("initTemplateZip");
			}
		}
	}
	public ZipTemplate getZipTemplate() throws DynamicException{
		if(this.templateZip==null) {
			initTemplateZip();
		}
		return this.templateZip;
	}
}
