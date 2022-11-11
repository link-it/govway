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

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;

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
	
	private boolean correctClassBackwardCompatibility = false;
	private synchronized void _correctClassBackwardCompatibility() {
		if(!this.correctClassBackwardCompatibility) {
			Map<String, String> map = null;
			OpenSPCoop2Properties op2 = OpenSPCoop2Properties.getInstance();
			if(op2!=null) {
				map = op2.getTrasformazioni_backwardCompatibility();
			}
			if(map!=null && !map.isEmpty()) {
				if(this.template!=null) {
					boolean modify = false;
					String s = new String(this.template);
					for (String key : map.keySet()) {
						if(s.contains(key)) {
							String newValue = map.get(key);
							s = s.replaceAll(key, newValue);
							modify = true;
						}
					}
					if(modify) {
						this.template = s.getBytes();
					}
				}
				if(this.templateIncludes!=null && !this.templateIncludes.isEmpty()) {
					for (String idTemplate : this.templateIncludes.keySet()) {
						byte[]template = this.templateIncludes.get(idTemplate);
						if(template!=null) {
							boolean modify = false;
							String s = new String(template);
							for (String key : map.keySet()) {
								if(s.contains(key)) {
									String newValue = map.get(key);
									s = s.replaceAll(key, newValue);
									modify = true;
								}
							}
							if(modify) {
								template = s.getBytes();
								this.templateIncludes.put(idTemplate, template); // update
							}
						}
					}
				}
			}
			this.correctClassBackwardCompatibility=true;
		}
	}
	private void correctClassBackwardCompatibility() {
		if(!this.correctClassBackwardCompatibility) {
			this._correctClassBackwardCompatibility();
		}
	}
	
	private transient freemarker.template.Template templateFreeMarker;
	private transient org.apache.velocity.Template templateVelocity;
	private ZipTemplate templateZip;
	
	private transient org.openspcoop2.utils.Semaphore _lock = null;
	private synchronized void initLock() {
		if(this._lock==null) {
			this._lock = new org.openspcoop2.utils.Semaphore("Template"); 
		}
	}
	public org.openspcoop2.utils.Semaphore getLock(){
		if(this._lock==null) {
			initLock();
		}
		return this._lock;
	}
	
	private void initTemplateFreeMarker() throws DynamicException{
		if(this.templateFreeMarker==null) {
			try {
				this.getLock().acquire("initTemplateFreeMarker");
			}catch(Throwable t) {
				throw new DynamicException(t.getMessage(),t);
			}
			try {
				this.correctClassBackwardCompatibility();
				this.templateFreeMarker = DynamicUtils.buildFreeMarkerTemplate(this);
			}finally {
				this.getLock().release("initTemplateFreeMarker");
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
				this.getLock().acquire("initTemplateVelocity");
			}catch(Throwable t) {
				throw new DynamicException(t.getMessage(),t);
			}
			try {
				this.correctClassBackwardCompatibility();
				this.templateVelocity = DynamicUtils.buildVelocityTemplate(this);
			}finally {
				this.getLock().release("initTemplateVelocity");
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
				this.getLock().acquire("initTemplateZip");
			}catch(Throwable t) {
				throw new DynamicException(t.getMessage(),t);
			}
			try {
				this.templateZip = new ZipTemplate(this.name, this.template);
			}finally {
				this.getLock().release("initTemplateZip");
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
