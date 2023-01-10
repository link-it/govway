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
package org.openspcoop2.protocol.sdk.archive;

import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ArchiveAllarme
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveAllarme implements IArchiveObject {

	public static String buildKey(RuoloPorta ruoloPorta, String nomePorta, String alias) throws ProtocolException{
		
		if(alias==null){
			throw new ProtocolException("alias non fornito");
		}
		
		StringBuilder bf = new StringBuilder();
		bf.append("Allarme_");
		bf.append(alias);
		if(ruoloPorta!=null && nomePorta!=null) {
			bf.append("#");
			bf.append(ruoloPorta.toString());
			bf.append("_");
			bf.append(nomePorta);
		}
		return bf.toString();
	}
	
	@Override
	public String key() throws ProtocolException {
		return ArchiveAllarme.buildKey(this.ruoloPorta, this.nomePorta, this.alias);
	}
	
	
	
	private RuoloPorta ruoloPorta;
	private String nomePorta;
	private String alias;
	private org.openspcoop2.core.allarmi.Allarme allarme;
	private boolean allarmeGlobale;
	
	private ArchiveIdCorrelazione idCorrelazione; // permette di correlare più oggetti tra di loro 

	public ArchiveAllarme(org.openspcoop2.core.allarmi.Allarme allarme, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		
		if(allarme==null){
			throw new ProtocolException("Allarme non fornito");
		}
		if(allarme.getAlias()==null){
			throw new ProtocolException("Allarme.alias non definito");
		}
		this.alias = allarme.getAlias();
		if(allarme!=null && allarme.getFiltro()!=null) {
			this.ruoloPorta = allarme.getFiltro().getRuoloPorta();
			this.nomePorta = allarme.getFiltro().getNomePorta();
		}
		this.allarme = allarme;
		
		this.allarmeGlobale = allarme.getFiltro()==null || allarme.getFiltro().getNomePorta()==null || "".equals(allarme.getFiltro().getNomePorta());
				
		this.idCorrelazione = idCorrelazione;
		
	}
	
	
	public String getAlias() {
		return this.alias;
	}
	public RuoloPorta getRuoloPorta() {
		return this.ruoloPorta;
	}
	public String getNomePorta() {
		return this.nomePorta;
	}
	public org.openspcoop2.core.allarmi.Allarme getAllarme() {
		return this.allarme;
	}
	
	public boolean isAllarmeGlobale() {
		return this.allarmeGlobale;
	}
	
	public ArchiveIdCorrelazione getIdCorrelazione() {
		return this.idCorrelazione;
	}

}
