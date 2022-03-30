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
package org.openspcoop2.protocol.sdk.archive;

import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ArchivePluginClasse
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchivePluginClasse implements IArchiveObject {

	public static String buildKey(String tipoPlugin, String tipo) throws ProtocolException{
		
		if(tipoPlugin==null){
			throw new ProtocolException("tipoPlugin non fornito");
		}
		if(tipo==null){
			throw new ProtocolException("tipo non fornito");
		}
		
		StringBuilder bf = new StringBuilder();
		bf.append("PluginClasse_");
		bf.append(tipoPlugin);
		bf.append("_");
		bf.append(tipo);
		return bf.toString();
	}
	
	@Override
	public String key() throws ProtocolException {
		return ArchivePluginClasse.buildKey(this.tipoPlugin, this.tipo);
	}
	
	
	
	private String tipoPlugin;
	private String tipo;
	private Plugin plugin;
	
	private ArchiveIdCorrelazione idCorrelazione; // permette di correlare più oggetti tra di loro 

	public ArchivePluginClasse(Plugin plugin, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		
		if(plugin==null){
			throw new ProtocolException("Plugin non fornito");
		}
		if(plugin.getTipoPlugin()==null){
			throw new ProtocolException("Plugin.tipoPlugin non definito");
		}
		if(plugin.getTipo()==null){
			throw new ProtocolException("Plugin.tipo non definito");
		}
		this.tipoPlugin = plugin.getTipoPlugin();
		this.tipo = plugin.getTipo();
		this.plugin = plugin;
		
		this.idCorrelazione = idCorrelazione;
		
	}
	
	public String getTipoPlugin() {
		return this.tipoPlugin;
	}
	public String getTipo() {
		return this.tipo;
	}
	public Plugin getPlugin() {
		return this.plugin;
	}
	
	public ArchiveIdCorrelazione getIdCorrelazione() {
		return this.idCorrelazione;
	}

}
