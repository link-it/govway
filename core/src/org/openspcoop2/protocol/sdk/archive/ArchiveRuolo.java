/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ArchiveRuolo
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveRuolo implements IArchiveObject {

	public static String buildKey(String nomeRuolo) throws ProtocolException{
		
		if(nomeRuolo==null){
			throw new ProtocolException("nomeRuolo non fornito");
		}
		
		StringBuffer bf = new StringBuffer();
		bf.append("Ruolo_");
		bf.append(nomeRuolo);
		return bf.toString();
	}
	
	@Override
	public String key() throws ProtocolException {
		return ArchiveRuolo.buildKey(this.idRuolo.getNome());
	}
	
	
	
	private IDRuolo idRuolo;

	private org.openspcoop2.core.registry.Ruolo ruolo;
	
	private ArchiveIdCorrelazione idCorrelazione; // permette di correlare più oggetti tra di loro 

	public ArchiveRuolo(org.openspcoop2.core.registry.Ruolo ruolo, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		
		if(ruolo==null){
			throw new ProtocolException("Ruolo non fornito");
		}
		if(ruolo.getNome()==null){
			throw new ProtocolException("Ruolo.nome non definito");
		}
		this.idRuolo = new IDRuolo(ruolo.getNome());
		this.ruolo = ruolo;
		
		this.idCorrelazione = idCorrelazione;
		
	}
	
	
	public IDRuolo getIdRuolo() {
		return this.idRuolo;
	}
	public org.openspcoop2.core.registry.Ruolo getRuolo() {
		return this.ruolo;
	}
	
	public ArchiveIdCorrelazione getIdCorrelazione() {
		return this.idCorrelazione;
	}

}
