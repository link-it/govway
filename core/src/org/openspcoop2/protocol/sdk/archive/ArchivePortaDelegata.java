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

import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ArchivePortaDelegata
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchivePortaDelegata implements IArchiveObject {

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
		bf.append("PortaDelegata_");
		bf.append(tipoSoggetto);
		bf.append("/");
		bf.append(nomeSoggetto);
		bf.append("_");
		bf.append(nomePorta);
		return bf.toString();
	}
	
	@Override
	public String key() throws ProtocolException {
		return ArchivePortaDelegata.buildKey(this.idSoggettoProprietario.getTipo(), this.idSoggettoProprietario.getNome(), 
					this.idPortaDelegata.getLocationPD());
	}
	
	

	private IDSoggetto idSoggettoProprietario;
	private IDPortaDelegata idPortaDelegata;
	private PortaDelegata portaDelegata;
	
	private ArchiveIdCorrelazione idCorrelazione; // permette di correlare più oggetti tra di loro 
	
	
	
	public ArchivePortaDelegata(IDSoggetto idSoggettoProprietario, PortaDelegata portaDelegata, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		this(injectProprietario(idSoggettoProprietario, portaDelegata),idCorrelazione, false);
	}
	public ArchivePortaDelegata(IDSoggetto idSoggettoProprietario, PortaDelegata portaDelegata, ArchiveIdCorrelazione idCorrelazione, boolean informationMissingManagementEnabled) throws ProtocolException{
		this(injectProprietario(idSoggettoProprietario, portaDelegata), idCorrelazione, informationMissingManagementEnabled);
	}
	public ArchivePortaDelegata(PortaDelegata portaDelegata, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		this(portaDelegata,idCorrelazione,false);
	}	
	public ArchivePortaDelegata(PortaDelegata portaDelegata, ArchiveIdCorrelazione idCorrelazione, boolean informationMissingManagementEnabled) throws ProtocolException{
		this.update(portaDelegata, informationMissingManagementEnabled);
		this.idCorrelazione = idCorrelazione;
	}
	private static PortaDelegata injectProprietario(IDSoggetto idSoggettoProprietario, PortaDelegata portaDelegata) throws ProtocolException{
		if(portaDelegata==null){
			throw new ProtocolException("PortaDelegata non fornita");
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
		portaDelegata.setTipoSoggettoProprietario(idSoggettoProprietario.getTipo());
		portaDelegata.setNomeSoggettoProprietario(idSoggettoProprietario.getNome());
		return portaDelegata;
	}
	
	
	
	public void update() throws ProtocolException{
		this.update(this.portaDelegata, false);
	}
	public void update(PortaDelegata portaDelegata) throws ProtocolException{
		this.update(portaDelegata, false);
	}
	public void update(PortaDelegata portaDelegata, boolean informationMissingManagementEnabled) throws ProtocolException{
		
		if(portaDelegata==null){
			throw new ProtocolException("PortaDelegata non fornita");
		}
		if(portaDelegata.getNome()==null){
			throw new ProtocolException("PortaDelegata.nome non definito");
		}
		this.portaDelegata = portaDelegata;
		
		if(informationMissingManagementEnabled==false){
		
			if(this.portaDelegata.getTipoSoggettoProprietario()==null){
				throw new ProtocolException("PortaDelegata.tipoSoggettoProprietario non definito");
			}
			if(this.portaDelegata.getNomeSoggettoProprietario()==null){
				throw new ProtocolException("PortaDelegata.nomeSoggettoProprietario non definito");
			}
			this.idSoggettoProprietario = 
					new IDSoggetto(this.portaDelegata.getTipoSoggettoProprietario(), 
							this.portaDelegata.getNomeSoggettoProprietario());
			
			this.idPortaDelegata = new IDPortaDelegata();
			this.idPortaDelegata.setSoggettoFruitore(this.idSoggettoProprietario);
			this.idPortaDelegata.setLocationPD(this.portaDelegata.getNome());
			if(this.portaDelegata.getLocation()!=null && !"".equals(this.portaDelegata.getLocation())){
				this.idPortaDelegata.setLocationPD(this.portaDelegata.getLocation());
			}
			
		}
	}
	
	
	
	
	
	
	public IDSoggetto getIdSoggettoProprietario() {
		return this.idSoggettoProprietario;
	}
	public IDPortaDelegata getIdPortaDelegata() {
		return this.idPortaDelegata;
	}
	public PortaDelegata getPortaDelegata() {
		return this.portaDelegata;
	}
	
	public ArchiveIdCorrelazione getIdCorrelazione() {
		return this.idCorrelazione;
	}
}
