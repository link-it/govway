/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.util.List;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
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
			String tipoSoggettoErogatore,String nomeSoggettoErogatore,String tipoServizio,String nomeServizio,Integer versione) throws ProtocolException{
		
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
		if(tipoServizio==null){
			throw new ProtocolException("tipoServizio non fornito");
		}
		if(nomeServizio==null){
			throw new ProtocolException("nomeServizio non fornito");
		}
		if(versione==null){
			throw new ProtocolException("versioneServizio non fornito");
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
		bf.append(tipoServizio);
		bf.append("/");
		bf.append(nomeServizio);
		bf.append("_");
		bf.append(versione);
		return bf.toString();
	}
	
	@Override
	public String key() throws ProtocolException {
		return ArchiveFruitore.buildKey(this.idSoggettoFruitore.getTipo(),this.idSoggettoFruitore.getNome(),
				this.idAccordoServizioParteSpecifica.getSoggettoErogatore().getTipo(), this.idAccordoServizioParteSpecifica.getSoggettoErogatore().getNome(), 
				this.idAccordoServizioParteSpecifica.getTipo(),this.idAccordoServizioParteSpecifica.getNome(),this.idAccordoServizioParteSpecifica.getVersione());
	}
	
	
	
	private IDSoggetto idSoggettoFruitore;
	private IDServizio idAccordoServizioParteSpecifica;
	private Fruitore fruitore;
	
	private List<MappingFruizionePortaDelegata> mappingPorteDelegateAssociate;
	public List<MappingFruizionePortaDelegata> getMappingPorteDelegateAssociate() {
		return this.mappingPorteDelegateAssociate;
	}
	public void setMappingPorteDelegateAssociate(List<MappingFruizionePortaDelegata> mappingPorteDelegateAssociate) {
		this.mappingPorteDelegateAssociate = mappingPorteDelegateAssociate;
	}



	private ArchiveIdCorrelazione idCorrelazione; // permette di correlare più oggetti tra di loro 
	
	
	public ArchiveFruitore(IDServizio idAccordoServizioParteSpecifica, Fruitore fruitore, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		this(idAccordoServizioParteSpecifica, fruitore, idCorrelazione, false);
	}
	public ArchiveFruitore(IDServizio idAccordoServizioParteSpecifica, Fruitore fruitore, ArchiveIdCorrelazione idCorrelazione, boolean informationMissingManagementEnabled) throws ProtocolException{
		update(idAccordoServizioParteSpecifica, fruitore, informationMissingManagementEnabled);
		this.idCorrelazione = idCorrelazione;
	}
	
	
	
	public void update() throws ProtocolException{
		this.update(this.idAccordoServizioParteSpecifica, this.fruitore, false);
	}
	public void update(IDServizio idAccordoServizioParteSpecifica, Fruitore fruitore) throws ProtocolException{
		this.update(idAccordoServizioParteSpecifica, fruitore, false);
	}
	public void update(IDServizio idAccordoServizioParteSpecifica, Fruitore fruitore, boolean informationMissingManagementEnabled) throws ProtocolException{
		
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
		if(idAccordoServizioParteSpecifica.getTipo()==null){
			throw new ProtocolException("idAccordoServizioParteSpecifica.tipo non definito");
		}
		if(idAccordoServizioParteSpecifica.getNome()==null){
			throw new ProtocolException("idAccordoServizioParteSpecifica.nome non definito");
		}
		
		if(informationMissingManagementEnabled==false){
			
			if(idAccordoServizioParteSpecifica.getVersione()==null){
				throw new ProtocolException("idAccordoServizioParteSpecifica.versione non definito");
			}
			if(idAccordoServizioParteSpecifica.getSoggettoErogatore()==null){
				throw new ProtocolException("idAccordoServizioParteSpecifica.soggettoErogatore non definito");
			}
			if(idAccordoServizioParteSpecifica.getSoggettoErogatore().getTipo()==null){
				throw new ProtocolException("idAccordoServizioParteSpecifica.soggettoErogatore.tipo non definito");
			}
			if(idAccordoServizioParteSpecifica.getSoggettoErogatore().getNome()==null){
				throw new ProtocolException("idAccordoServizioParteSpecifica.soggettoErogatore.nome non definito");
			}
			
		}
		
		this.idAccordoServizioParteSpecifica = idAccordoServizioParteSpecifica;
		
	}
	
	
	
	
	public IDSoggetto getIdSoggettoFruitore() {
		return this.idSoggettoFruitore;
	}
	public IDServizio getIdAccordoServizioParteSpecifica() {
		return this.idAccordoServizioParteSpecifica;
	}
	public Fruitore getFruitore() {
		return this.fruitore;
	}

	public ArchiveIdCorrelazione getIdCorrelazione() {
		return this.idCorrelazione;
	}
}
