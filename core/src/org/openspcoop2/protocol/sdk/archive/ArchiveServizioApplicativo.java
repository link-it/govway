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

import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ArchiveServizioApplicativo
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveServizioApplicativo implements IArchiveObject {

	public static String buildKey(String tipoSoggetto,String nomeSoggetto,String nomeSA) throws ProtocolException{
		
		if(tipoSoggetto==null){
			throw new ProtocolException("tipoSoggetto non fornito");
		}
		if(nomeSoggetto==null){
			throw new ProtocolException("nomeSoggetto non fornito");
		}
		if(nomeSA==null){
			throw new ProtocolException("nomeSA non fornito");
		}
		
		StringBuffer bf = new StringBuffer();
		bf.append("ServizioApplicativo_");
		bf.append(tipoSoggetto);
		bf.append("/");
		bf.append(nomeSoggetto);
		bf.append("_");
		bf.append(nomeSA);
		return bf.toString();
	}
	
	
	@Override
	public String key() throws ProtocolException {
		return ArchiveServizioApplicativo.buildKey(this.idSoggettoProprietario.getTipo(), this.idSoggettoProprietario.getNome(), 
					this.idServizioApplicativo.getNome());
	}
	
	

	private IDSoggetto idSoggettoProprietario;
	private IDServizioApplicativo idServizioApplicativo;
	private ServizioApplicativo servizioApplicativo;
	
	private ArchiveIdCorrelazione idCorrelazione; // permette di correlare più oggetti tra di loro 
	

	
	public ArchiveServizioApplicativo(IDSoggetto idSoggettoProprietario, ServizioApplicativo servizioApplicativo, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		this(injectProprietario(idSoggettoProprietario, servizioApplicativo), idCorrelazione, false);
	}
	public ArchiveServizioApplicativo(IDSoggetto idSoggettoProprietario, ServizioApplicativo servizioApplicativo, ArchiveIdCorrelazione idCorrelazione, boolean informationMissingManagementEnabled) throws ProtocolException{
		this(injectProprietario(idSoggettoProprietario, servizioApplicativo), idCorrelazione, informationMissingManagementEnabled);
	}
	public ArchiveServizioApplicativo(ServizioApplicativo servizioApplicativo, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		this(servizioApplicativo, idCorrelazione,false);
	}	
	public ArchiveServizioApplicativo(ServizioApplicativo servizioApplicativo, ArchiveIdCorrelazione idCorrelazione, boolean informationMissingManagementEnabled) throws ProtocolException{
		this.update(servizioApplicativo, informationMissingManagementEnabled);
		this.idCorrelazione = idCorrelazione;
	}
	private static ServizioApplicativo injectProprietario(IDSoggetto idSoggettoProprietario, ServizioApplicativo servizioApplicativo) throws ProtocolException{
		if(servizioApplicativo==null){
			throw new ProtocolException("ServizioApplicativo non fornito");
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
		servizioApplicativo.setTipoSoggettoProprietario(idSoggettoProprietario.getTipo());
		servizioApplicativo.setNomeSoggettoProprietario(idSoggettoProprietario.getNome());
		return servizioApplicativo;
	}
	
	
	
	
	public void update() throws ProtocolException{
		this.update(this.servizioApplicativo, false);
	}
	public void update(ServizioApplicativo servizioApplicativo) throws ProtocolException{
		this.update(servizioApplicativo, false);
	}
	public void update(ServizioApplicativo servizioApplicativo, boolean informationMissingManagementEnabled) throws ProtocolException{
		
		if(servizioApplicativo==null){
			throw new ProtocolException("ServizioApplicativo non fornito");
		}
		if(servizioApplicativo.getNome()==null){
			throw new ProtocolException("ServizioApplicativo.nome non definito");
		}
		this.servizioApplicativo = servizioApplicativo;
		
		if(informationMissingManagementEnabled==false){
			
			if(this.servizioApplicativo.getTipoSoggettoProprietario()==null){
				throw new ProtocolException("ServizioApplicativo.tipoSoggettoProprietario non definito");
			}
			if(this.servizioApplicativo.getNomeSoggettoProprietario()==null){
				throw new ProtocolException("ServizioApplicativo.nomeSoggettoProprietario non definito");
			}
			this.idSoggettoProprietario = 
					new IDSoggetto(this.servizioApplicativo.getTipoSoggettoProprietario(), 
							this.servizioApplicativo.getNomeSoggettoProprietario());
			
			this.idServizioApplicativo = new IDServizioApplicativo();
			this.idServizioApplicativo.setIdSoggettoProprietario(this.idSoggettoProprietario);
			this.idServizioApplicativo.setNome(this.servizioApplicativo.getNome());
			
		}
	}
	
	
	
	
	public IDSoggetto getIdSoggettoProprietario() {
		return this.idSoggettoProprietario;
	}
	public IDServizioApplicativo getIdServizioApplicativo() {
		return this.idServizioApplicativo;
	}
	public ServizioApplicativo getServizioApplicativo() {
		return this.servizioApplicativo;
	}

	public ArchiveIdCorrelazione getIdCorrelazione() {
		return this.idCorrelazione;
	}
}
