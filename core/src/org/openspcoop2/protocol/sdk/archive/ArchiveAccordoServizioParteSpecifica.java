/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ArchiveAccordoServizioParteSpecifica
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveAccordoServizioParteSpecifica implements IArchiveObject {

	public static String buildKey(String tipoSoggetto,String nomeSoggetto,String tipoServizio,String nomeServizio,Integer versione) throws ProtocolException{
		
		if(tipoSoggetto==null){
			throw new ProtocolException("tipoSoggetto non fornito");
		}
		if(nomeSoggetto==null){
			throw new ProtocolException("nomeSoggetto non fornito");
		}
		if(tipoServizio==null){
			throw new ProtocolException("tipoServizio non fornito");
		}
		if(nomeServizio==null){
			throw new ProtocolException("nomeServizio non fornito");
		}
		if(versione==null){
			throw new ProtocolException("versioneServizio non fornita");
		}
		
		StringBuffer bf = new StringBuffer();
		bf.append("AccordoServizioParteSpecifica_");
		bf.append(tipoSoggetto);
		bf.append("/");
		bf.append(nomeSoggetto);
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
		String tipoSoggetto = "-";
		String nomeSoggetto = "-";
		String tipoServizio = this.accordoServizioParteSpecifica.getTipo();
		String nomeServizio = this.accordoServizioParteSpecifica.getNome();
		Integer versione = null;
		if(this.accordoServizioParteSpecifica.getTipoSoggettoErogatore()!=null){
			tipoSoggetto = this.accordoServizioParteSpecifica.getTipoSoggettoErogatore();
		}
		if(this.accordoServizioParteSpecifica.getNomeSoggettoErogatore()!=null){
			nomeSoggetto = this.accordoServizioParteSpecifica.getNomeSoggettoErogatore();
		}
		if(this.accordoServizioParteSpecifica.getVersione()!=null){
			versione = this.accordoServizioParteSpecifica.getVersione();
		}
		return ArchiveAccordoServizioParteSpecifica.buildKey(tipoSoggetto, nomeSoggetto, 
				tipoServizio,nomeServizio,versione);
	}
	
	
	
	private IDSoggetto idSoggettoErogatore;
	private IDServizio idAccordoServizioParteSpecifica;
	private IDAccordo idAccordoServizioParteComune;
	private AccordoServizioParteSpecifica accordoServizioParteSpecifica;
	
	private List<MappingErogazionePortaApplicativa> mappingPorteApplicativeAssociate;
	public List<MappingErogazionePortaApplicativa> getMappingPorteApplicativeAssociate() {
		return this.mappingPorteApplicativeAssociate;
	}
	public void setMappingPorteApplicativeAssociate(
			List<MappingErogazionePortaApplicativa> mappingPorteApplicativeAssociate) {
		this.mappingPorteApplicativeAssociate = mappingPorteApplicativeAssociate;
	}



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
		if(idSoggettoProprietario==null){
			throw new ProtocolException("idSoggettoProprietario non fornito");
		}
		if(idSoggettoProprietario.getTipo()==null){
			throw new ProtocolException("idSoggettoProprietario.tipo non definito");
		}
		if(idSoggettoProprietario.getNome()==null){
			throw new ProtocolException("idSoggettoProprietario.nome non definito");
		}
		accordoServizioParteSpecifica.setTipoSoggettoErogatore(idSoggettoProprietario.getTipo());
		accordoServizioParteSpecifica.setNomeSoggettoErogatore(idSoggettoProprietario.getNome());
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
			throw new ProtocolException("AccordoServizioParteSpecifica non fornito");
		}
		if(accordoServizioParteSpecifica.getTipo()==null){
			throw new ProtocolException("AccordoServizioParteSpecifica.tipo non definito");
		}
		if(accordoServizioParteSpecifica.getNome()==null){
			throw new ProtocolException("AccordoServizioParteSpecifica.nome non definito");
		}
		this.accordoServizioParteSpecifica = accordoServizioParteSpecifica;
		
		if(informationMissingManagementEnabled==false){
			if(accordoServizioParteSpecifica.getVersione()==null){
				throw new ProtocolException("AccordoServizioParteSpecifica.versione non definito");
			}		
			if(accordoServizioParteSpecifica.getTipoSoggettoErogatore()==null){
				throw new ProtocolException("AccordoServizioParteSpecifica.servizio.tipoSoggettoErogatore non definito");
			}
			if(accordoServizioParteSpecifica.getNomeSoggettoErogatore()==null){
				throw new ProtocolException("AccordoServizioParteSpecifica.servizio.nomeSoggettoErogatore non definito");
			}
		
			this.idSoggettoErogatore = 
					new IDSoggetto(accordoServizioParteSpecifica.getTipoSoggettoErogatore(), 
							accordoServizioParteSpecifica.getNomeSoggettoErogatore());
			
			try{
				this.idAccordoServizioParteSpecifica = IDServizioFactory.getInstance().getIDServizioFromValues(accordoServizioParteSpecifica.getTipo(), 
						accordoServizioParteSpecifica.getNome(), 
						this.idSoggettoErogatore, 
						accordoServizioParteSpecifica.getVersione());
			}catch(Exception e){
				throw new ProtocolException(e.getMessage(),e);
			}
						
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
	public IDServizio getIdAccordoServizioParteSpecifica() {
		return this.idAccordoServizioParteSpecifica;
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
