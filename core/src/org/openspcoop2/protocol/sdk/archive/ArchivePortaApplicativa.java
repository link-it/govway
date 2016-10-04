/*
 * OpenSPCoop - Customizable API Gateway 
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

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaApplicativaByNome;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ArchivePortaApplicativa
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchivePortaApplicativa implements IArchiveObject {

	public static String buildKey(String tipoSoggetto,String nomeSoggetto,String nomePorta) throws ProtocolException{
		
		if(tipoSoggetto==null){
			throw new ProtocolException("tipoSoggetto non fornito");
		}
		if(nomeSoggetto==null){
			throw new ProtocolException("nomeSoggetto non fornito");
		}
		if(nomePorta==null){
			throw new ProtocolException("nomePorta non fornito");
		}
		
		StringBuffer bf = new StringBuffer();
		bf.append("PortaApplicativa_");
		bf.append(tipoSoggetto);
		bf.append("/");
		bf.append(nomeSoggetto);
		bf.append("_");
		bf.append(nomePorta);
		return bf.toString();
	}
	
	@Override
	public String key() throws ProtocolException {
		return ArchivePortaApplicativa.buildKey(this.idSoggettoProprietario.getTipo(), this.idSoggettoProprietario.getNome(), 
					this.idPortaApplicativaByNome.getNome());
	}
	
	
	
	private IDSoggetto idSoggettoProprietario;
	private IDServizio idServizio;
	private IDPortaApplicativa idPortaApplicativa;
	private IDPortaApplicativaByNome idPortaApplicativaByNome;
	private PortaApplicativa portaApplicativa;
	
	private ArchiveIdCorrelazione idCorrelazione; // permette di correlare più oggetti tra di loro 
	
	
	
	public ArchivePortaApplicativa(IDSoggetto idSoggettoProprietario, PortaApplicativa portaApplicativa, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		this(injectProprietario(idSoggettoProprietario, portaApplicativa), idCorrelazione, false);
	}
	public ArchivePortaApplicativa(IDSoggetto idSoggettoProprietario, PortaApplicativa portaApplicativa, ArchiveIdCorrelazione idCorrelazione, boolean informationMissingManagementEnabled) throws ProtocolException{
		this(injectProprietario(idSoggettoProprietario, portaApplicativa),idCorrelazione, informationMissingManagementEnabled);
	}
	public ArchivePortaApplicativa(PortaApplicativa portaApplicativa, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		this(portaApplicativa,idCorrelazione,false);
	}	
	public ArchivePortaApplicativa(PortaApplicativa portaApplicativa, ArchiveIdCorrelazione idCorrelazione, boolean informationMissingManagementEnabled) throws ProtocolException{
		this.update(portaApplicativa, informationMissingManagementEnabled);
		this.idCorrelazione = idCorrelazione;
	}
	private static PortaApplicativa injectProprietario(IDSoggetto idSoggettoProprietario, PortaApplicativa portaApplicativa) throws ProtocolException{
		if(portaApplicativa==null){
			throw new ProtocolException("PortaApplicativa non fornita");
		}
		if(idSoggettoProprietario==null){
			throw new ProtocolException("idSoggettoProprietario non fornito");
		}
		if(idSoggettoProprietario.getTipo()==null){
			throw new ProtocolException("idSoggettoProprietario.tipo non definito");
		}
		if(idSoggettoProprietario.getNome()==null){
			throw new ProtocolException("idSoggettoProprietario.nome non definito");
		}
		portaApplicativa.setTipoSoggettoProprietario(idSoggettoProprietario.getTipo());
		portaApplicativa.setNomeSoggettoProprietario(idSoggettoProprietario.getNome());
		return portaApplicativa;
	}
	
	
	
	public void update() throws ProtocolException{
		this.update(this.portaApplicativa, false);
	}
	public void update(PortaApplicativa portaApplicativa) throws ProtocolException{
		this.update(portaApplicativa, false);
	}
	public void update(PortaApplicativa portaApplicativa, boolean informationMissingManagementEnabled) throws ProtocolException{
		
		if(portaApplicativa==null){
			throw new ProtocolException("PortaApplicativa non fornita");
		}
		if(portaApplicativa.getNome()==null){
			throw new ProtocolException("PortaApplicativa.nome non definito");
		}
		this.portaApplicativa = portaApplicativa;
		
		if(informationMissingManagementEnabled==false){
		
			if(this.portaApplicativa.getTipoSoggettoProprietario()==null){
				throw new ProtocolException("PortaApplicativa.tipoSoggettoProprietario non definito");
			}
			if(this.portaApplicativa.getNomeSoggettoProprietario()==null){
				throw new ProtocolException("PortaApplicativa.nomeSoggettoProprietario non definito");
			}
			this.idSoggettoProprietario = 
					new IDSoggetto(this.portaApplicativa.getTipoSoggettoProprietario(), 
							this.portaApplicativa.getNomeSoggettoProprietario());
			
			this.idPortaApplicativaByNome = new IDPortaApplicativaByNome();
			this.idPortaApplicativaByNome.setNome(this.portaApplicativa.getNome());
			this.idPortaApplicativaByNome.setSoggetto(this.idSoggettoProprietario);
			
			if(portaApplicativa.getServizio()==null){
				throw new ProtocolException("PortaApplicativa.servizio non definito");
			}
			if(portaApplicativa.getServizio().getTipo()==null){
				throw new ProtocolException("PortaApplicativa.servizio.tipo non definito");
			}
			if(portaApplicativa.getServizio().getNome()==null){
				throw new ProtocolException("PortaApplicativa.servizio.nome non definito");
			}
			this.idServizio = new IDServizio(this.idSoggettoProprietario, 
					portaApplicativa.getServizio().getTipo(), 
					portaApplicativa.getServizio().getNome());
			if(portaApplicativa.getAzione()!=null &&
					portaApplicativa.getAzione().getNome()!=null){
				this.idServizio.setAzione(portaApplicativa.getAzione().getNome());
			}
			
			this.idPortaApplicativa = new IDPortaApplicativa();
			this.idPortaApplicativa.setIDServizio(this.idServizio);
		
		}
	}
	
	
	
	
	public IDSoggetto getIdSoggettoProprietario() {
		return this.idSoggettoProprietario;
	}
	public IDServizio getIdServizio() {
		return this.idServizio;
	}
	public IDPortaApplicativa getIdPortaApplicativa() {
		return this.idPortaApplicativa;
	}
	public IDPortaApplicativaByNome getIdPortaApplicativaByNome() {
		return this.idPortaApplicativaByNome;
	}
	public PortaApplicativa getPortaApplicativa() {
		return this.portaApplicativa;
	}
	
	public ArchiveIdCorrelazione getIdCorrelazione() {
		return this.idCorrelazione;
	}
}
