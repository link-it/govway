/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.monitor.engine.dynamic;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.Date;

import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
 * Plugin
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PluginJar implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String nome;
	private Date date; // data salvata sulla configurazione
	
	//private byte[] content; // occupa spazio inutile
	private File fContent;
	
	private String url;
	
	private URL resourceURL;
	
	public PluginJar(String nome, Date data, byte[] contenuto) throws Exception {
		this.nome = nome;
		this.date = data;
		//this.content = contenuto;
		this.fContent = File.createTempFile("plugin_"+this.nome, ".raw");
		FileSystemUtilities.writeFile(this.fContent, contenuto);
		this.resourceURL = this.fContent.toURI().toURL();
	}
	public PluginJar(String nome, Date data, String url) throws Exception {
		this.nome = nome;
		this.date = data;
		this.url = url;
		
		String uriPattern = ".+://.+";
		boolean matchUri = RegularExpressionEngine.isMatch(this.url, uriPattern);
		if(matchUri) {
			this.resourceURL = new URL(this.url);
		}
		else {
			File f = new File(this.url);
			if(f.exists()) {
				this.resourceURL = f.toURI().toURL();
			}
			else {
				this.resourceURL = new URL(this.url);
			}
		}
	}
	
	public void close() {
		if(this.fContent!=null) {
			this.fContent.delete();
		}
	}
	
	public String getNome() {
		return this.nome;
	}
	public Date getDate() {
		return this.date;
	}
	public URL getResourceURL() {
		return this.resourceURL;
	}
}
