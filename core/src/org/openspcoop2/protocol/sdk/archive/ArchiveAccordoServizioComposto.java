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
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ArchiveAccordoServizioComposto
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveAccordoServizioComposto extends ArchiveAccordoServizioParteComune {

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
		bf.append("AccordoServizioComposto_");
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
		return ArchiveAccordoServizioComposto.buildKey(tipoSoggetto, nomeSoggetto, 
				nomeAccordo,versione);
	}
	
	
	private IDAccordoCooperazione idAccordoCooperazione;
	private List<IDServizio> idServiziComponenti = new ArrayList<IDServizio>();
		

	public ArchiveAccordoServizioComposto(IDSoggetto idSoggettoProprietario,AccordoServizioParteComune accordoServizioParteComune, ArchiveIdCorrelazione idCorrelazione)throws ProtocolException {
		super(idSoggettoProprietario, accordoServizioParteComune,idCorrelazione);
	}
	public ArchiveAccordoServizioComposto(IDSoggetto idSoggettoProprietario,AccordoServizioParteComune accordoServizioParteComune,
			ArchiveIdCorrelazione idCorrelazione,
			boolean informationMissingManagementEnabled) throws ProtocolException {
		super(idSoggettoProprietario, accordoServizioParteComune,idCorrelazione,informationMissingManagementEnabled);
	}
	public ArchiveAccordoServizioComposto(AccordoServizioParteComune accordoServizioParteComune, ArchiveIdCorrelazione idCorrelazione) throws ProtocolException {
		super(accordoServizioParteComune,idCorrelazione);
	}
	public ArchiveAccordoServizioComposto(AccordoServizioParteComune accordoServizioParteComune, 
			ArchiveIdCorrelazione idCorrelazione,
			boolean informationMissingManagementEnabled) throws ProtocolException {
		super(accordoServizioParteComune,idCorrelazione, informationMissingManagementEnabled);
	}
	
	
	@Override
	public void update(AccordoServizioParteComune accordoServizioComposto, boolean informationMissingManagementEnabled) throws ProtocolException{
		
		super.update(accordoServizioComposto, informationMissingManagementEnabled);
		
		if(accordoServizioComposto.getServizioComposto()==null){
			throw new ProtocolException("AccordoServizioComposto.servizioComposto non definito");
		}
		if(accordoServizioComposto.getServizioComposto().getAccordoCooperazione()==null){
			throw new ProtocolException("AccordoServizioComposto.servizioComposto.nomeAccordoCooperazione non definito");
		}
		try{
			this.idAccordoCooperazione = IDAccordoCooperazioneFactory.getInstance().getIDAccordoFromUri(accordoServizioComposto.getServizioComposto().getAccordoCooperazione());
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
		
		// Questo controllo non ci vuole. Altrimenti non e' possibile esportare un accordo che non possiede servizi composti
		// Comunque il controllo viene effettuato quando si cerca di portare un accordo allo stato finale.
//		if(accordoServizioComposto.getServizioComposto().getServizioComponenteList().size()<=1){
//			throw new ProtocolException("AccordoServizioComposto.servizioComposto.serviziComponenti trovati "+accordoServizioComposto.getServizioComposto().getServizioComponenteList().size()+" servizi, ne sono richiesti almeno 2");
//		}
		for (int i = 0; i < accordoServizioComposto.getServizioComposto().getServizioComponenteList().size(); i++) {
			if(this.idServiziComponenti==null){
				this.idServiziComponenti = new ArrayList<IDServizio>();
			}
			AccordoServizioParteComuneServizioCompostoServizioComponente sComponente = accordoServizioComposto.getServizioComposto().getServizioComponenteList().get(i);
			if(sComponente==null){
				throw new ProtocolException("AccordoServizioComposto.servizioComposto.serviziComponente["+i+"] non definito");
			}
			if(sComponente.getTipo()==null){
				throw new ProtocolException("AccordoServizioComposto.servizioComposto.serviziComponente["+i+"].tipo non definito");
			}
			if(sComponente.getNome()==null){
				throw new ProtocolException("AccordoServizioComposto.servizioComposto.serviziComponente["+i+"].nome non definito");
			}
			if(sComponente.getTipoSoggetto()==null){
				throw new ProtocolException("AccordoServizioComposto.servizioComposto.serviziComponente["+i+"].tipoSoggetto non definito");
			}
			if(sComponente.getNomeSoggetto()==null){
				throw new ProtocolException("AccordoServizioComposto.servizioComposto.serviziComponente["+i+"].nomeSoggetto non definito");
			}
			IDServizio idServizioComponente = new IDServizio(sComponente.getTipoSoggetto(), sComponente.getNomeSoggetto(), 
					sComponente.getTipo(), sComponente.getNome(), sComponente.getAzione());
			this.idServiziComponenti.add(idServizioComponente);
		}
		
	}
	
	
	
	public IDAccordoCooperazione getIdAccordoCooperazione() {
		return this.idAccordoCooperazione;
	}
	public List<IDServizio> getIdServiziComponenti() {
		return this.idServiziComponenti;
	}


}
