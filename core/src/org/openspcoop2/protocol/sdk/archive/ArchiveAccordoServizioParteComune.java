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

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ArchiveAccordoServizioParteComune
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveAccordoServizioParteComune implements IArchiveObject {

	public static String buildKey(String tipoSoggetto,String nomeSoggetto,String nomeAccordo,String versione) throws ProtocolException{
		
		if(tipoSoggetto==null){
			throw new ProtocolException("tipoSoggetto non fornito");
		}
		if(nomeSoggetto==null){
			throw new ProtocolException("nomeSoggetto non fornito");
		}
		if(nomeAccordo==null){
			throw new ProtocolException("nomeAccordo non fornito");
		}
		if(versione==null){
			throw new ProtocolException("versione non fornita");
		}
		
		StringBuffer bf = new StringBuffer();
		bf.append("AccordoServizioParteComune_");
		bf.append(tipoSoggetto);
		bf.append("/");
		bf.append(nomeSoggetto);
		bf.append("_");
		bf.append(nomeAccordo);
		bf.append("_");
		bf.append(versione);
		return bf.toString();
	}
	
	@Override
	public String key() throws ProtocolException {
		String tipoSoggetto = "-";
		String nomeSoggetto = "-";
		String nomeAccordo = this.accordoServizioParteComune.getNome();
		String versione = "-";
		if(this.accordoServizioParteComune.getSoggettoReferente()!=null){
			if(this.accordoServizioParteComune.getSoggettoReferente().getTipo()!=null){
				tipoSoggetto = this.accordoServizioParteComune.getSoggettoReferente().getTipo();
			}
			if(this.accordoServizioParteComune.getSoggettoReferente().getNome()!=null){
				nomeSoggetto = this.accordoServizioParteComune.getSoggettoReferente().getNome();
			}
		}
		if(this.accordoServizioParteComune.getVersione()!=null){
			versione = this.accordoServizioParteComune.getVersione();
		}
		return ArchiveAccordoServizioParteComune.buildKey(tipoSoggetto, nomeSoggetto, 
				nomeAccordo,versione);
	}
	
	
	
	protected IDSoggetto idSoggettoReferente;
	protected IDAccordo idAccordoServizioParteComune;
	protected AccordoServizioParteComune accordoServizioParteComune;
	
	private ArchiveIdCorrelazione idCorrelazione; // permette di correlare più oggetti tra di loro 
		
	
	
	public ArchiveAccordoServizioParteComune(IDSoggetto idSoggettoProprietario, AccordoServizioParteComune accordoServizioParteComune, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		this(injectProprietario(idSoggettoProprietario, accordoServizioParteComune),idCorrelazione, false);
	}
	public ArchiveAccordoServizioParteComune(IDSoggetto idSoggettoProprietario, AccordoServizioParteComune accordoServizioParteComune, ArchiveIdCorrelazione idCorrelazione, boolean informationMissingManagementEnabled) throws ProtocolException{
		this(injectProprietario(idSoggettoProprietario, accordoServizioParteComune),idCorrelazione, informationMissingManagementEnabled);
	}
	public ArchiveAccordoServizioParteComune(AccordoServizioParteComune accordoServizioParteComune, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		this(accordoServizioParteComune,idCorrelazione,false);
	}	
	public ArchiveAccordoServizioParteComune(AccordoServizioParteComune accordoServizioParteComune, ArchiveIdCorrelazione idCorrelazione, boolean informationMissingManagementEnabled) throws ProtocolException{
		this.update(accordoServizioParteComune, informationMissingManagementEnabled);
		this.idCorrelazione = idCorrelazione;
	}
	private static AccordoServizioParteComune injectProprietario(IDSoggetto idSoggettoProprietario, AccordoServizioParteComune accordoServizioParteComune) throws ProtocolException{
		if(accordoServizioParteComune==null){
			throw new ProtocolException("AccordoServizioParteComune non fornito");
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
		IdSoggetto soggettoReferente = new IdSoggetto();
		soggettoReferente.setTipo(idSoggettoProprietario.getTipo());
		soggettoReferente.setNome(idSoggettoProprietario.getNome());
		accordoServizioParteComune.setSoggettoReferente(soggettoReferente);
		return accordoServizioParteComune;
	}
	
	
	
	public void update() throws ProtocolException{
		this.update(this.accordoServizioParteComune, false);
	}
	public void update(AccordoServizioParteComune accordoServizioParteComune) throws ProtocolException{
		this.update(accordoServizioParteComune, false);
	}
	public void update(AccordoServizioParteComune accordoServizioParteComune, boolean informationMissingManagementEnabled) throws ProtocolException{
		
		if(accordoServizioParteComune==null){
			throw new ProtocolException("AccordoServizioParteComune non fornito");
		}
		if(accordoServizioParteComune.getNome()==null){
			throw new ProtocolException("AccordoServizioParteComune.nome non definito");
		}
		this.accordoServizioParteComune = accordoServizioParteComune;
		
		if(informationMissingManagementEnabled==false){
			if(accordoServizioParteComune.getVersione()==null){
				throw new ProtocolException("AccordoServizioParteComune.versione non definito");
			}
			if(accordoServizioParteComune.getSoggettoReferente()==null){
				throw new ProtocolException("AccordoServizioParteComune.soggettoReferente non definito");
			}
			if(accordoServizioParteComune.getSoggettoReferente().getTipo()==null){
				throw new ProtocolException("AccordoServizioParteComune.soggettoReferente.tipo non definito");
			}
			if(accordoServizioParteComune.getSoggettoReferente().getNome()==null){
				throw new ProtocolException("AccordoServizioParteComune.soggettoReferente.nome non definito");
			}
			
			this.idSoggettoReferente = 
					new IDSoggetto(accordoServizioParteComune.getSoggettoReferente().getTipo(), 
							accordoServizioParteComune.getSoggettoReferente().getNome());
			
			try{
				this.idAccordoServizioParteComune = IDAccordoFactory.getInstance().getIDAccordoFromAccordo(accordoServizioParteComune);
			}catch(Exception e){
				throw new ProtocolException(e.getMessage(),e);
			}
		}
	}
	
	
	public IDSoggetto getIdSoggettoReferente() {
		return this.idSoggettoReferente;
	}
	public IDAccordo getIdAccordoServizioParteComune() {
		return this.idAccordoServizioParteComune;
	}
	public AccordoServizioParteComune getAccordoServizioParteComune() {
		return this.accordoServizioParteComune;
	}
	
	public ArchiveIdCorrelazione getIdCorrelazione() {
		return this.idCorrelazione;
	}
}
