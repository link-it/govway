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

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ArchiveAccordoCooperazione
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveAccordoCooperazione implements IArchiveObject {

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
		bf.append("AccordoCooperazione_");
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
		String nomeAccordo = this.accordoCooperazione.getNome();
		String versione = "-";
		if(this.accordoCooperazione.getSoggettoReferente()!=null){
			if(this.accordoCooperazione.getSoggettoReferente().getTipo()!=null){
				tipoSoggetto = this.accordoCooperazione.getSoggettoReferente().getTipo();
			}
			if(this.accordoCooperazione.getSoggettoReferente().getNome()!=null){
				nomeSoggetto = this.accordoCooperazione.getSoggettoReferente().getNome();
			}
		}
		if(this.accordoCooperazione.getVersione()!=null){
			versione = this.accordoCooperazione.getVersione();
		}
		return ArchiveAccordoCooperazione.buildKey(tipoSoggetto, nomeSoggetto, 
				nomeAccordo,versione);
	}
	
	
	private IDSoggetto idSoggettoReferente;
	private IDAccordoCooperazione idAccordoCooperazione;
	private List<IDSoggetto> idSoggettiPartecipanti = new ArrayList<IDSoggetto>();
	private AccordoCooperazione accordoCooperazione;
	
	private ArchiveIdCorrelazione idCorrelazione; // permette di correlare più oggetti tra di loro 
	
	
	public ArchiveAccordoCooperazione(IDSoggetto idSoggettoProprietario, AccordoCooperazione accordoCooperazione, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		this(injectProprietario(idSoggettoProprietario, accordoCooperazione), idCorrelazione, false);
	}
	public ArchiveAccordoCooperazione(IDSoggetto idSoggettoProprietario, AccordoCooperazione accordoCooperazione, ArchiveIdCorrelazione idCorrelazione, boolean informationMissingManagementEnabled) throws ProtocolException{
		this(injectProprietario(idSoggettoProprietario, accordoCooperazione), idCorrelazione, informationMissingManagementEnabled);
	}
	public ArchiveAccordoCooperazione(AccordoCooperazione accordoCooperazione, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException{
		this(accordoCooperazione,idCorrelazione,false);
	}	
	public ArchiveAccordoCooperazione(AccordoCooperazione accordoCooperazione, ArchiveIdCorrelazione idCorrelazione, boolean informationMissingManagementEnabled) throws ProtocolException{
		this.update(accordoCooperazione, informationMissingManagementEnabled);
		this.idCorrelazione = idCorrelazione;
	}
	private static AccordoCooperazione injectProprietario(IDSoggetto idSoggettoProprietario, AccordoCooperazione accordoCooperazione) throws ProtocolException{
		if(accordoCooperazione==null){
			throw new ProtocolException("AccordoCooperazione non fornito");
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
		accordoCooperazione.setSoggettoReferente(soggettoReferente);
		return accordoCooperazione;
	}

	
	
	public void update() throws ProtocolException{
		this.update(this.accordoCooperazione, false);
	}
	public void update(AccordoCooperazione accordoCooperazione) throws ProtocolException{
		this.update(accordoCooperazione, false);
	}
	public void update(AccordoCooperazione accordoCooperazione, boolean informationMissingManagementEnabled) throws ProtocolException{
		
		if(accordoCooperazione==null){
			throw new ProtocolException("AccordoCooperazione non fornito");
		}
		if(accordoCooperazione.getNome()==null){
			throw new ProtocolException("AccordoCooperazione.nome non definito");
		}
		this.accordoCooperazione = accordoCooperazione;
		
		if(informationMissingManagementEnabled==false){
			if(accordoCooperazione.getVersione()==null){
				throw new ProtocolException("AccordoCooperazione.versione non definito");
			}
			if(accordoCooperazione.getSoggettoReferente()==null){
				throw new ProtocolException("AccordoCooperazione.soggettoReferente non definito");
			}
			if(accordoCooperazione.getSoggettoReferente().getTipo()==null){
				throw new ProtocolException("AccordoCooperazione.soggettoReferente.tipo non definito");
			}
			if(accordoCooperazione.getSoggettoReferente().getNome()==null){
				throw new ProtocolException("AccordoCooperazione.soggettoReferente.nome non definito");
			}
		
			this.idSoggettoReferente = 
					new IDSoggetto(accordoCooperazione.getSoggettoReferente().getTipo(), 
							accordoCooperazione.getSoggettoReferente().getNome());
			
			try{
				this.idAccordoCooperazione = IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromAccordo(accordoCooperazione);
			}catch(Exception e){
				throw new ProtocolException(e.getMessage(),e);
			}
			
			// Questo controllo non ci vuole. Altrimenti non e' possibile esportare un accordo che non possiede soggetti partecipanti
			// Comunque il controllo viene effettuato quando si cerca di portare un accordo allo stato finale.
//			if(accordoCooperazione.getElencoPartecipanti()==null){
//				throw new ProtocolException("AccordoCooperazione.elencoPartecipanti non definito");
//			}
			if(accordoCooperazione.getElencoPartecipanti()!=null){
				for (int i = 0; i < accordoCooperazione.getElencoPartecipanti().sizeSoggettoPartecipanteList(); i++) {
					IdSoggetto partecipante = accordoCooperazione.getElencoPartecipanti().getSoggettoPartecipante(i);
					if(partecipante==null){
						throw new ProtocolException("AccordoCooperazione.elencoPartecipanti["+i+"] non definito");
					}
					if(partecipante.getTipo()==null){
						throw new ProtocolException("AccordoCooperazione.elencoPartecipanti["+i+"].tipo non definito");
					}
					if(partecipante.getNome()==null){
						throw new ProtocolException("AccordoCooperazione.elencoPartecipanti["+i+"].nome non definito");
					}
					IDSoggetto idSoggettoPartecipante = new IDSoggetto(partecipante.getTipo(), partecipante.getNome());
					this.idSoggettiPartecipanti.add(idSoggettoPartecipante);
				}
			}
		}
	}
	
	
	public IDSoggetto getIdSoggettoReferente() {
		return this.idSoggettoReferente;
	}
	public IDAccordoCooperazione getIdAccordoCooperazione() {
		return this.idAccordoCooperazione;
	}
	public List<IDSoggetto> getIdSoggettiPartecipanti() {
		return this.idSoggettiPartecipanti;
	}
	public AccordoCooperazione getAccordoCooperazione() {
		return this.accordoCooperazione;
	}
	
	public ArchiveIdCorrelazione getIdCorrelazione() {
		return this.idCorrelazione;
	}

}
