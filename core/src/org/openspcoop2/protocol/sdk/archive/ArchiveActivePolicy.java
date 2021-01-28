/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ArchiveActivePolicy
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveActivePolicy implements IPositionArchiveObject {

	public static String buildKey(RuoloPolicy ruoloPorta, String nomePorta, String aliasPolicy) throws ProtocolException{
		
		if(aliasPolicy==null){
			throw new ProtocolException("aliasPolicy non fornito");
		}
		
		StringBuilder bf = new StringBuilder();
		bf.append("ControlloTraffico_ActivePolicy_");
		bf.append(aliasPolicy);
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
		return ArchiveActivePolicy.buildKey(this.ruoloPorta, this.nomePorta, this.aliasPolicy);
	}
	
	@Override
	public int position() throws ProtocolException {
		return this.posizione;
	}
	
	
	private RuoloPolicy ruoloPorta;
	private String nomePorta;
	private String aliasPolicy;
	private org.openspcoop2.core.controllo_traffico.AttivazionePolicy policy;
	private boolean policyGlobale;
	
	private int posizione;
	
	private ArchiveIdCorrelazione idCorrelazione; // permette di correlare più oggetti tra di loro 

	public ArchiveActivePolicy(org.openspcoop2.core.controllo_traffico.AttivazionePolicy policy, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		
		if(policy==null){
			throw new ProtocolException("Policy non fornito");
		}
		if(policy.getAlias()==null){
			throw new ProtocolException("Policy.alias non definito");
		}
		this.aliasPolicy = policy.getAlias();
		if(policy!=null && policy.getFiltro()!=null) {
			this.ruoloPorta = policy.getFiltro().getRuoloPorta();
			this.nomePorta = policy.getFiltro().getNomePorta();
		}
		this.policy = policy;
		
		this.posizione = policy.getPosizione();
		
		this.policyGlobale = policy.getFiltro()==null || policy.getFiltro().getNomePorta()==null || "".equals(policy.getFiltro().getNomePorta());
				
		this.idCorrelazione = idCorrelazione;
		
	}
	
	
	public String getAliasPolicy() {
		return this.aliasPolicy;
	}
	public RuoloPolicy getRuoloPorta() {
		return this.ruoloPorta;
	}
	public String getNomePorta() {
		return this.nomePorta;
	}
	public org.openspcoop2.core.controllo_traffico.AttivazionePolicy getPolicy() {
		return this.policy;
	}
	
	public boolean isPolicyGlobale() {
		return this.policyGlobale;
	}
	
	public ArchiveIdCorrelazione getIdCorrelazione() {
		return this.idCorrelazione;
	}

}
