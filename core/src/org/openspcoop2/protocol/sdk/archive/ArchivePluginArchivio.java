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

import org.openspcoop2.core.config.RegistroPlugin;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ArchivePluginArchivio
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchivePluginArchivio implements IPositionArchiveObject {

	public static String buildKey(String nome) throws ProtocolException{
		
		if(nome==null){
			throw new ProtocolException("nome non fornito");
		}
		
		StringBuilder bf = new StringBuilder();
		bf.append("PluginArchivio_");
		bf.append(nome);
		return bf.toString();
	}
	
	@Override
	public String key() throws ProtocolException {
		return ArchivePluginArchivio.buildKey(this.nome);
	}
	
	@Override
	public int position() throws ProtocolException {
		return this.posizione;
	}
	
	
	private String nome;
	private int posizione;
	private RegistroPlugin plugin;
	
	private ArchiveIdCorrelazione idCorrelazione; // permette di correlare più oggetti tra di loro 

	public ArchivePluginArchivio(RegistroPlugin plugin, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		
		if(plugin==null){
			throw new ProtocolException("Plugin non fornito");
		}
		if(plugin.getNome()==null){
			throw new ProtocolException("Plugin.nome non definito");
		}
		this.nome = plugin.getNome();
		this.posizione = plugin.getPosizione();
		this.plugin = plugin;
		
		this.idCorrelazione = idCorrelazione;
		
	}
	
	public String getNome() {
		return this.nome;
	}
	public RegistroPlugin getPlugin() {
		return this.plugin;
	}
	
	public ArchiveIdCorrelazione getIdCorrelazione() {
		return this.idCorrelazione;
	}

}
