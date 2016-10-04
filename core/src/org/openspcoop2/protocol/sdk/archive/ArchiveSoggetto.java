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

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ArchiveSoggetto
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveSoggetto implements IArchiveObject {

	public static String buildKey(String tipoSoggetto,String nomeSoggetto) throws ProtocolException{
		
		if(tipoSoggetto==null){
			throw new ProtocolException("tipoSoggetto non fornito");
		}
		if(nomeSoggetto==null){
			throw new ProtocolException("nomeSoggetto non fornito");
		}
		
		StringBuffer bf = new StringBuffer();
		bf.append("Soggetto_");
		bf.append(tipoSoggetto);
		bf.append("/");
		bf.append(nomeSoggetto);
		return bf.toString();
	}
	
	@Override
	public String key() throws ProtocolException {
		return ArchiveSoggetto.buildKey(this.idSoggetto.getTipo(), this.idSoggetto.getNome());
	}
	
	
	
	private IDSoggetto idSoggetto;
	private org.openspcoop2.core.registry.Soggetto soggettoRegistro;
	private org.openspcoop2.core.config.Soggetto soggettoConfigurazione;
	
	private ArchiveIdCorrelazione idCorrelazione; // permette di correlare più oggetti tra di loro 
	
	public ArchiveSoggetto(org.openspcoop2.core.registry.Soggetto soggettoRegistro, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		
		if(soggettoRegistro==null){
			throw new ProtocolException("SoggettoRegistro non fornito");
		}
		if(soggettoRegistro.getNome()==null){
			throw new ProtocolException("SoggettoRegistro.nome non definito");
		}
		if(soggettoRegistro.getTipo()==null){
			throw new ProtocolException("SoggettoRegistro.tipo non definito");
		}
		this.soggettoRegistro = soggettoRegistro;
	
		this.idSoggetto = new IDSoggetto(soggettoRegistro.getTipo(), soggettoRegistro.getNome());
		
		this.idCorrelazione = idCorrelazione;
		
	}
	public ArchiveSoggetto(org.openspcoop2.core.config.Soggetto soggettoConfigurazione, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		
		if(soggettoConfigurazione==null){
			throw new ProtocolException("SoggettoConfigurazione non fornito");
		}
		if(soggettoConfigurazione.getNome()==null){
			throw new ProtocolException("SoggettoConfigurazione.nome non definito");
		}
		if(soggettoConfigurazione.getTipo()==null){
			throw new ProtocolException("SoggettoConfigurazione.tipo non definito");
		}
		this.soggettoConfigurazione = soggettoConfigurazione;
		
		this.idSoggetto = new IDSoggetto(soggettoConfigurazione.getTipo(), soggettoConfigurazione.getNome());
		
		this.idCorrelazione = idCorrelazione;
		
	}
	public ArchiveSoggetto(org.openspcoop2.core.config.Soggetto soggettoConfigurazione,
			org.openspcoop2.core.registry.Soggetto soggettoRegistro, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		
		if(soggettoRegistro==null){
			throw new ProtocolException("SoggettoRegistro non fornito");
		}
		if(soggettoRegistro.getNome()==null){
			throw new ProtocolException("SoggettoRegistro.nome non definito");
		}
		if(soggettoRegistro.getTipo()==null){
			throw new ProtocolException("SoggettoRegistro.tipo non definito");
		}
		this.soggettoRegistro = soggettoRegistro;
	
		if(soggettoConfigurazione==null){
			throw new ProtocolException("SoggettoConfigurazione non fornito");
		}
		if(soggettoConfigurazione.getNome()==null){
			throw new ProtocolException("SoggettoConfigurazione.nome non definito");
		}
		if(soggettoRegistro.getNome().equals(soggettoConfigurazione.getNome())==false){
			throw new ProtocolException("SoggettoRegistro.nome e SoggettoConfigurazione.nome devono coincidere");
		}
		if(soggettoConfigurazione.getTipo()==null){
			throw new ProtocolException("SoggettoConfigurazione.tipo non definito");
		}
		if(soggettoRegistro.getTipo().equals(soggettoConfigurazione.getTipo())==false){
			throw new ProtocolException("SoggettoRegistro.tipo e SoggettoConfigurazione.tipo devono coincidere");
		}
		this.soggettoConfigurazione = soggettoConfigurazione;
		
		this.idSoggetto = new IDSoggetto(soggettoRegistro.getTipo(), soggettoRegistro.getNome());
		
		this.idCorrelazione = idCorrelazione;
		
	}
	
	
	public IDSoggetto getIdSoggetto() {
		return this.idSoggetto;
	}
	public org.openspcoop2.core.registry.Soggetto getSoggettoRegistro() {
		return this.soggettoRegistro;
	}
	public org.openspcoop2.core.config.Soggetto getSoggettoConfigurazione() {
		return this.soggettoConfigurazione;
	}
	
	public ArchiveIdCorrelazione getIdCorrelazione() {
		return this.idCorrelazione;
	}
}
