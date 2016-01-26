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
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ArchiveAccordoServizioParteSpecifica
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveAccordoServizioParteSpecifica implements IArchiveObject {

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
		bf.append("AccordoServizioParteSpecifica_");
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
		String nomeAccordo = this.accordoServizioParteSpecifica.getNome();
		String versione = "-";
		if(this.accordoServizioParteSpecifica.getServizio()!=null){
			if(this.accordoServizioParteSpecifica.getServizio().getTipoSoggettoErogatore()!=null){
				tipoSoggetto = this.accordoServizioParteSpecifica.getServizio().getTipoSoggettoErogatore();
			}
			if(this.accordoServizioParteSpecifica.getServizio().getNomeSoggettoErogatore()!=null){
				nomeSoggetto = this.accordoServizioParteSpecifica.getServizio().getNomeSoggettoErogatore();
			}
		}
		if(this.accordoServizioParteSpecifica.getVersione()!=null){
			versione = this.accordoServizioParteSpecifica.getVersione();
		}
		return ArchiveAccordoServizioParteSpecifica.buildKey(tipoSoggetto, nomeSoggetto, 
				nomeAccordo,versione);
	}
	
	
	
	private IDSoggetto idSoggettoErogatore;
	private IDAccordo idAccordoServizioParteSpecifica;
	private IDServizio idServizio;
	private IDAccordo idAccordoServizioParteComune;
	private AccordoServizioParteSpecifica accordoServizioParteSpecifica;
	
	private ArchiveIdCorrelazione idCorrelazione; // permette di correlare più oggetti tra di loro 
	
	
	
	public ArchiveAccordoServizioParteSpecifica(IDSoggetto idSoggettoProprietario, AccordoServizioParteSpecifica accordoServizioParteSpecifica, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		this(injectProprietario(idSoggettoProprietario, accordoServizioParteSpecifica),idCorrelazione, false);
	}
	public ArchiveAccordoServizioParteSpecifica(IDSoggetto idSoggettoProprietario, AccordoServizioParteSpecifica accordoServizioParteSpecifica, ArchiveIdCorrelazione idCorrelazione, boolean informationMissingManagementEnabled) throws ProtocolException{
		this(injectProprietario(idSoggettoProprietario, accordoServizioParteSpecifica), idCorrelazione, informationMissingManagementEnabled);
	}
	public ArchiveAccordoServizioParteSpecifica(AccordoServizioParteSpecifica accordoServizioParteSpecifica, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		this(accordoServizioParteSpecifica,idCorrelazione,false);
	}	
	public ArchiveAccordoServizioParteSpecifica(AccordoServizioParteSpecifica accordoServizioParteSpecifica, ArchiveIdCorrelazione idCorrelazione, boolean informationMissingManagementEnabled) throws ProtocolException{
		this.update(accordoServizioParteSpecifica, informationMissingManagementEnabled);
		this.idCorrelazione = idCorrelazione;
	}
	private static AccordoServizioParteSpecifica injectProprietario(IDSoggetto idSoggettoProprietario, AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws ProtocolException{
		if(accordoServizioParteSpecifica==null){
			throw new ProtocolException("AccordoServizioParteSpecifica non fornito");
		}
		if(accordoServizioParteSpecifica.getServizio()==null){
			throw new ProtocolException("AccordoServizioParteSpecifica.servizio non definito");
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
		accordoServizioParteSpecifica.getServizio().setTipoSoggettoErogatore(idSoggettoProprietario.getTipo());
		accordoServizioParteSpecifica.getServizio().setNomeSoggettoErogatore(idSoggettoProprietario.getNome());
		return accordoServizioParteSpecifica;
	}
	
	
	
	public void update() throws ProtocolException{
		this.update(this.accordoServizioParteSpecifica, false);
	}
	public void update(AccordoServizioParteSpecifica accordoServizioParteSpecifica) throws ProtocolException{
		this.update(accordoServizioParteSpecifica, false);
	}
	public void update(AccordoServizioParteSpecifica accordoServizioParteSpecifica, boolean informationMissingManagementEnabled) throws ProtocolException{
		
		if(accordoServizioParteSpecifica==null){
			throw new ProtocolException("AccordoServizioParteComune non fornito");
		}
		if(accordoServizioParteSpecifica.getNome()==null){
			throw new ProtocolException("AccordoServizioParteComune.nome non definito");
		}
		if(accordoServizioParteSpecifica.getServizio()==null){
			throw new ProtocolException("AccordoServizioParteSpecifica.servizio non definito");
		}
		if(accordoServizioParteSpecifica.getServizio().getTipo()==null){
			throw new ProtocolException("AccordoServizioParteComune.servizio.tipo non definito");
		}
		if(accordoServizioParteSpecifica.getServizio().getNome()==null){
			throw new ProtocolException("AccordoServizioParteComune.servizio.nome non definito");
		}
		this.accordoServizioParteSpecifica = accordoServizioParteSpecifica;
		
		if(informationMissingManagementEnabled==false){
			if(accordoServizioParteSpecifica.getVersione()==null){
				throw new ProtocolException("AccordoServizioParteComune.versione non definito");
			}		
			if(accordoServizioParteSpecifica.getServizio().getTipoSoggettoErogatore()==null){
				throw new ProtocolException("AccordoServizioParteComune.servizio.tipoSoggettoErogatore non definito");
			}
			if(accordoServizioParteSpecifica.getServizio().getNomeSoggettoErogatore()==null){
				throw new ProtocolException("AccordoServizioParteComune.servizio.nomeSoggettoErogatore non definito");
			}
		
			this.idSoggettoErogatore = 
					new IDSoggetto(accordoServizioParteSpecifica.getServizio().getTipoSoggettoErogatore(), 
							accordoServizioParteSpecifica.getServizio().getNomeSoggettoErogatore());
			
			try{
				this.idAccordoServizioParteSpecifica = IDAccordoFactory.getInstance().getIDAccordoFromAccordo(accordoServizioParteSpecifica);
			}catch(Exception e){
				throw new ProtocolException(e.getMessage(),e);
			}
			
			this.idServizio = new IDServizio(this.idSoggettoErogatore, 
					accordoServizioParteSpecifica.getServizio().getTipo(), 
					accordoServizioParteSpecifica.getServizio().getNome());
			
			if(accordoServizioParteSpecifica.getAccordoServizioParteComune()==null){
				throw new ProtocolException("AccordoServizioParteSpecifica.accordoServizioParteComune non definito");
			}
			try{
				this.idAccordoServizioParteComune = IDAccordoFactory.getInstance().getIDAccordoFromUri(accordoServizioParteSpecifica.getAccordoServizioParteComune());
			}catch(Exception e){
				throw new ProtocolException(e.getMessage(),e);
			}
		}
	}
	
	
	
	public IDSoggetto getIdSoggettoErogatore() {
		return this.idSoggettoErogatore;
	}
	public IDAccordo getIdAccordoServizioParteSpecifica() {
		return this.idAccordoServizioParteSpecifica;
	}
	public IDServizio getIdServizio() {
		return this.idServizio;
	}
	public IDAccordo getIdAccordoServizioParteComune() {
		return this.idAccordoServizioParteComune;
	}
	public AccordoServizioParteSpecifica getAccordoServizioParteSpecifica() {
		return this.accordoServizioParteSpecifica;
	}

	public ArchiveIdCorrelazione getIdCorrelazione() {
		return this.idCorrelazione;
	}
	
}
