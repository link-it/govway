/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.config.RegistroPlugin;
import org.openspcoop2.core.config.RegistroPluginArchivio;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.regexp.RegExpException;
import org.openspcoop2.utils.regexp.RegExpNotFoundException;
import org.openspcoop2.utils.regexp.RegExpNotValidException;
import org.openspcoop2.utils.resources.DynamicClassLoader;

/**
 * Plugin
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Plugin implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String nome;
	public String getNome() {
		return this.nome;
	}

	private Date date; // data di creazione del plugin
	public Date getDate() {
		return this.date;
	}

	private List<String> compatibilita;
	
	private List<PluginJar> archivePlugin = new ArrayList<>();
	
	private transient DynamicClassLoader classLoader;
	
	public Plugin(RegistroPlugin plugin) throws UtilsException, IOException, RegExpException, RegExpNotValidException, RegExpNotFoundException {
		this.nome = plugin.getNome();
		this.date = plugin.getData();
		this.compatibilita = plugin.getCompatibilitaList();
		
		List<URL> listUrl = new ArrayList<>();
		
		for (RegistroPluginArchivio pluginJar : plugin.getArchivioList()) {
			init(pluginJar, listUrl);
		}

		this.classLoader = new DynamicClassLoader(listUrl.toArray(new URL[1]));
	}
	private void init(RegistroPluginArchivio pluginJar, List<URL> listUrl) throws UtilsException, IOException, RegExpException, RegExpNotValidException, RegExpNotFoundException {
		String archivioPrefix = "Archivio '"+pluginJar.getNome()+"' ";
		PluginJar plug = null;
		switch (pluginJar.getSorgente()) {
		case JAR:
			if(pluginJar.getContenuto()==null) {
				throw new UtilsException(archivioPrefix+"senza contenuto (sorgente: "+pluginJar.getSorgente()+")");
			}
			plug = new PluginJar(pluginJar.getNome(), pluginJar.getData(), pluginJar.getContenuto());
			this.archivePlugin.add(plug);
			listUrl.add(plug.getResourceURL());
			break;
		case URL:
			if(pluginJar.getUrl()==null) {
				throw new UtilsException(archivioPrefix+"senza url (sorgente: "+pluginJar.getSorgente()+")");
			}
			plug = new PluginJar(pluginJar.getNome(), pluginJar.getData(), pluginJar.getUrl());
			this.archivePlugin.add(plug);
			listUrl.add(plug.getResourceURL());
			break;
		case DIR:
			if(pluginJar.getDir()==null) {
				throw new UtilsException(archivioPrefix+"senza directory (sorgente: "+pluginJar.getSorgente()+")");
			}
			File fDir = new File(pluginJar.getDir());
			if(!fDir.exists()) {
				throw new UtilsException(archivioPrefix+"indica una directory '"+fDir.getAbsolutePath()+"' non esistente (sorgente: "+pluginJar.getSorgente()+")");
			}
			if(fDir.isDirectory()) {
				loadJar(pluginJar.getNome(), pluginJar.getData(), fDir, listUrl);
			}
			else {
				plug = new PluginJar(pluginJar.getNome(), pluginJar.getData(), pluginJar.getDir());
				this.archivePlugin.add(plug);
				listUrl.add(plug.getResourceURL());
			}
			break;
		}
	}
	
	private void loadJar(String nomePlugin, Date data, File dir, List<URL> listUrl) throws UtilsException, MalformedURLException, RegExpException, RegExpNotValidException, RegExpNotFoundException {
		
		List<File> dirs = new ArrayList<>();
		
		File [] files = dir.listFiles();
		if(files!=null && files.length>0) {
			for (int i = 0; i < files.length; i++) {
				File f = files[i];
				if(f.isFile()) {
					if(f.getName().endsWith(".jar")) {
						PluginJar plug = new PluginJar(nomePlugin+"#"+f.getAbsolutePath(), data, f.getAbsolutePath());
						this.archivePlugin.add(plug);
						listUrl.add(plug.getResourceURL());
					}
				}
				else if(f.isDirectory()) {
					dirs.add(f);
				}
			}
		}
		
		if(!dirs.isEmpty()) {
			for (File dirChild : dirs) {
				loadJar(nomePlugin, data, dirChild, listUrl);
			}
		}
	}
	
	
	
	public ClassLoader getClassLoader(TipoPlugin tipoClasseDaRicercare) {
		return this.getClassLoader(tipoClasseDaRicercare.getValue());
	}
	public ClassLoader getClassLoader(String tipoClasseCustomDaRicercare) {
		
		if(this.compatibilita!=null && !this.compatibilita.isEmpty() &&
			!this.compatibilita.contains(tipoClasseCustomDaRicercare)) {
			return null; // class loader non compatibile con il tipo cercato
		}
		
		return this.classLoader;
	}
	
	
	public void close() {
		if(!this.archivePlugin.isEmpty()) {
			for (PluginJar pluginJar : this.archivePlugin) {
				pluginJar.close();
			}
		}
	}
	
	
}
