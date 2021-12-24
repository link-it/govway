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

import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ArchiveGruppo
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveGruppo implements IArchiveObject {

	public static String buildKey(String nomeGruppo) throws ProtocolException{
		
		if(nomeGruppo==null){
			throw new ProtocolException("nomeGruppo non fornito");
		}
		
		StringBuilder bf = new StringBuilder();
		bf.append("Gruppo_");
		bf.append(nomeGruppo);
		return bf.toString();
	}
	
	@Override
	public String key() throws ProtocolException {
		return ArchiveGruppo.buildKey(this.idGruppo.getNome());
	}
	
	
	
	private IDGruppo idGruppo;

	private org.openspcoop2.core.registry.Gruppo gruppo;
	
	private ArchiveIdCorrelazione idCorrelazione; // permette di correlare più oggetti tra di loro 

	public ArchiveGruppo(org.openspcoop2.core.registry.Gruppo gruppo, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		
		if(gruppo==null){
			throw new ProtocolException("Gruppo non fornito");
		}
		if(gruppo.getNome()==null){
			throw new ProtocolException("Gruppo.nome non definito");
		}
		this.idGruppo = new IDGruppo(gruppo.getNome());
		this.gruppo = gruppo;
		
		this.idCorrelazione = idCorrelazione;
		
	}
	
	
	public IDGruppo getIdGruppo() {
		return this.idGruppo;
	}
	public org.openspcoop2.core.registry.Gruppo getGruppo() {
		return this.gruppo;
	}
	
	public ArchiveIdCorrelazione getIdCorrelazione() {
		return this.idCorrelazione;
	}

}
