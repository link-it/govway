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

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ArchiveFruitore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveFruitore implements IArchiveObject {

	public static String buildKey(String tipoSoggettoFruitore,String nomeSoggettoFruitore,
			String tipoSoggettoErogatore,String nomeSoggettoErogatore,String nomeAccordo,String versione) throws ProtocolException{
		
		if(tipoSoggettoFruitore==null){
			throw new ProtocolException("tipoSoggettoFruitore non fornito");
		}
		if(nomeSoggettoFruitore==null){
			throw new ProtocolException("nomeSoggettoFruitore non fornito");
		}
		
		if(tipoSoggettoErogatore==null){
			throw new ProtocolException("tipoSoggettoErogatore non fornito");
		}
		if(nomeSoggettoErogatore==null){
			throw new ProtocolException("nomeSoggettoErogatore non fornito");
		}
		if(nomeAccordo==null){
			throw new ProtocolException("nomeAccordo non fornito");
		}
		if(versione==null){
			throw new ProtocolException("versione non fornito");
		}
		
		StringBuffer bf = new StringBuffer();
		bf.append("Fruitore_");
		bf.append(tipoSoggettoFruitore);
		bf.append("/");
		bf.append(nomeSoggettoFruitore);
		bf.append("_AccordoServizioParteSpecifica_");
		bf.append(tipoSoggettoErogatore);
		bf.append("/");
		bf.append(nomeSoggettoErogatore);
		bf.append("_");
		bf.append(nomeAccordo);
		bf.append("_");
		bf.append(versione);
		return bf.toString();
	}
	
	@Override
	public String key() throws ProtocolException {
		return ArchiveFruitore.buildKey(this.idSoggettoFruitore.getTipo(),this.idSoggettoFruitore.getNome(),
				this.idAccordoServizioParteSpecifica.getSoggettoReferente().getTipo(), this.idAccordoServizioParteSpecifica.getSoggettoReferente().getNome(), 
					this.idAccordoServizioParteSpecifica.getNome(),this.idAccordoServizioParteSpecifica.getVersione());
	}
	
	
	
	private IDSoggetto idSoggettoFruitore;
	private IDAccordo idAccordoServizioParteSpecifica;
	private Fruitore fruitore;
	
	private List<String> serviziApplicativiAutorizzati = new ArrayList<String>();
	public List<String> getServiziApplicativiAutorizzati() {
		return this.serviziApplicativiAutorizzati;
	}

	private ArchiveIdCorrelazione idCorrelazione; // permette di correlare più oggetti tra di loro 
	
	
	public ArchiveFruitore(IDAccordo idAccordoServizioParteSpecifica, Fruitore fruitore, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		this(idAccordoServizioParteSpecifica, fruitore, idCorrelazione, false);
	}
	public ArchiveFruitore(IDAccordo idAccordoServizioParteSpecifica, Fruitore fruitore, ArchiveIdCorrelazione idCorrelazione, boolean informationMissingManagementEnabled) throws ProtocolException{
		update(idAccordoServizioParteSpecifica, fruitore, informationMissingManagementEnabled);
		this.idCorrelazione = idCorrelazione;
	}
	
	
	
	public void update() throws ProtocolException{
		this.update(this.idAccordoServizioParteSpecifica, this.fruitore, false);
	}
	public void update(IDAccordo idAccordoServizioParteSpecifica, Fruitore fruitore) throws ProtocolException{
		this.update(idAccordoServizioParteSpecifica, fruitore, false);
	}
	public void update(IDAccordo idAccordoServizioParteSpecifica, Fruitore fruitore, boolean informationMissingManagementEnabled) throws ProtocolException{
		
		if(fruitore==null){
			throw new ProtocolException("fruitore non definito");
		}
		if(fruitore.getTipo()==null){
			throw new ProtocolException("fruitore.tipo non definito");
		}
		if(fruitore.getNome()==null){
			throw new ProtocolException("fruitore.nome non definito");
		}
		this.idSoggettoFruitore = new IDSoggetto(fruitore.getTipo(), fruitore.getNome());
		
		this.fruitore = fruitore;
		
		if(idAccordoServizioParteSpecifica==null){
			throw new ProtocolException("idAccordoServizioParteSpecifica non fornito");
		}
		if(idAccordoServizioParteSpecifica.getNome()==null){
			throw new ProtocolException("idAccordoServizioParteSpecifica.nome non definito");
		}
		
		if(informationMissingManagementEnabled==false){
			
			if(idAccordoServizioParteSpecifica.getVersione()==null){
				throw new ProtocolException("idAccordoServizioParteSpecifica.versione non definito");
			}
			if(idAccordoServizioParteSpecifica.getSoggettoReferente()==null){
				throw new ProtocolException("idAccordoServizioParteSpecifica.soggettoReferente non definito");
			}
			if(idAccordoServizioParteSpecifica.getSoggettoReferente().getTipo()==null){
				throw new ProtocolException("idAccordoServizioParteSpecifica.soggettoReferente.tipo non definito");
			}
			if(idAccordoServizioParteSpecifica.getSoggettoReferente().getNome()==null){
				throw new ProtocolException("idAccordoServizioParteSpecifica.soggettoReferente.nome non definito");
			}
			
		}
		
		this.idAccordoServizioParteSpecifica = idAccordoServizioParteSpecifica;
		
	}
	
	
	
	
	public IDSoggetto getIdSoggettoFruitore() {
		return this.idSoggettoFruitore;
	}
	public IDAccordo getIdAccordoServizioParteSpecifica() {
		return this.idAccordoServizioParteSpecifica;
	}
	public Fruitore getFruitore() {
		return this.fruitore;
	}

	public ArchiveIdCorrelazione getIdCorrelazione() {
		return this.idCorrelazione;
	}
}
