/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ArchiveSoggetto
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchivePdd implements IArchiveObject {

	public static String buildKey(String nomePdd) throws ProtocolException{
		
		if(nomePdd==null){
			throw new ProtocolException("nomePdd non fornito");
		}
		
		StringBuffer bf = new StringBuffer();
		bf.append("PortaDominio_");
		bf.append(nomePdd);
		return bf.toString();
	}
	
	@Override
	public String key() throws ProtocolException {
		return ArchivePdd.buildKey(this.nomePdd);
	}
	
	
	
	private String nomePdd;
	private org.openspcoop2.core.registry.PortaDominio portaDominio;
	
	private ArchiveIdCorrelazione idCorrelazione; // permette di correlare più oggetti tra di loro 

	public ArchivePdd(org.openspcoop2.core.registry.PortaDominio portaDominio, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		
		if(portaDominio==null){
			throw new ProtocolException("PortaDominio non fornito");
		}
		if(portaDominio.getNome()==null){
			throw new ProtocolException("PortaDominio.nome non definito");
		}
		this.nomePdd = portaDominio.getNome();
		this.portaDominio = portaDominio;
		
		this.idCorrelazione = idCorrelazione;
		
	}
	
	
	public String getNomePdd() {
		return this.nomePdd;
	}
	public org.openspcoop2.core.registry.PortaDominio getPortaDominio() {
		return this.portaDominio;
	}
	
	public ArchiveIdCorrelazione getIdCorrelazione() {
		return this.idCorrelazione;
	}

}
