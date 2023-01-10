/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.io.Entry;
import org.openspcoop2.utils.io.ZipUtilities;

/**
 * ZipTemplate
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ZipTemplate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private byte[] zip;
	
	public ZipTemplate(String name, byte[] zip) {
		this.name = name;
		this.zip = zip;
	}

	public String getName() {
		return this.name;
	}
	public byte[] getZip() {
		return this.zip;
	}
	
	
	private Template templateFreeMarker;
	private Template templateVelocity;
	
	private transient org.openspcoop2.utils.Semaphore _lock = null;
	private synchronized void initLock() {
		if(this._lock==null) {
			this._lock = new org.openspcoop2.utils.Semaphore("ZipTemplate"); 
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
				this.templateFreeMarker = buildTemplate(Costanti.ZIP_INDEX_ENTRY_FREEMARKER);
			}finally {
				this.getLock().release("initTemplateFreeMarker");
			}
		}
	}
	public Template getTemplateFreeMarker() throws DynamicException{
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
				this.templateVelocity = buildTemplate(Costanti.ZIP_INDEX_ENTRY_VELOCITY);
			}finally {
				this.getLock().release("initTemplateVelocity");
			}
		}
	}
	public Template getTemplateVelocity() throws DynamicException{
		if(this.templateVelocity==null) {
			initTemplateVelocity();
		}
		return this.templateVelocity;
	}
	
	
	
	private Template buildTemplate(String indexEntryName) throws DynamicException {
		List<Entry> entries = null;
		try {
			entries = ZipUtilities.read(this.zip);
		}catch(Exception e) {
			throw new DynamicException(e.getMessage(),e);
		}
		if(entries.isEmpty()) {
			throw new DynamicException("Entries not found");
		}
		byte[] template = null;
		Map<String, byte[]> templateIncludes = new HashMap<>();
		for (Entry entry : entries) {
			if(indexEntryName.equals(entry.getName())) {
				template = entry.getContent();
			}
			else if(!entry.getName().contains("/") && !entry.getName().contains("\\") && template==null) {
				// prende il primo
				template = entry.getContent();
			}
			else {
				templateIncludes.put(entry.getName(), entry.getContent());
			}
		}
		
		Template t = new Template(this.name, template, templateIncludes);
		
		return t;
	}
	
}
