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



package org.openspcoop2.protocol.sdk.diagnostica;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.core.diagnostica.DominioSoggetto;
import org.openspcoop2.core.diagnostica.DominioTransazione;
import org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione;
import org.openspcoop2.core.diagnostica.Proprieta;
import org.openspcoop2.core.diagnostica.Protocollo;
import org.openspcoop2.core.diagnostica.Servizio;
import org.openspcoop2.core.diagnostica.Soggetto;
import org.openspcoop2.core.diagnostica.SoggettoIdentificativo;
import org.openspcoop2.core.diagnostica.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;

/**
 *
 * Bean Contenente le informazioni relative ai messaggi diagnostici di correlazione
 * 
 * @author Stefano Corallo <corallo@link.it>
 * @author Lorenzo Nardi <nardi@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class MsgDiagnosticoCorrelazione implements Serializable{

	private static final long serialVersionUID = 7616051015989365464L;

	// msgdiagnostico
	private org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione infoProtocolloTransazione;


	public MsgDiagnosticoCorrelazione() {
		this.infoProtocolloTransazione = new InformazioniProtocolloTransazione();
	}
	public MsgDiagnosticoCorrelazione(org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione infoProtocolloTransazione) {
		this.infoProtocolloTransazione = infoProtocolloTransazione;
	}



	// base

	public org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione getInformazioniProtocolloTransazione() {
		return this.infoProtocolloTransazione;
	}
	public void setInformazioniProtocolloTransazione(org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione infoProtocolloTransazione) {
		this.infoProtocolloTransazione = infoProtocolloTransazione;
	}



	// id  [Wrapper]

	public Long getId() {
		return this.infoProtocolloTransazione.getId();
	}
	public void setId(Long id) {
		this.infoProtocolloTransazione.setId(id);
	}
	
	
	
	

	// delegata [wrapper]

	public boolean isDelegata() {
		switch (this.infoProtocolloTransazione.getTipoPdD()) {
		case PORTA_DELEGATA:
			return true;
		case PORTA_APPLICATIVA:
			return false;
		}
		return false;
	}
	public void setDelegata(boolean isDelegata) {
		if(isDelegata){
			this.infoProtocolloTransazione.setTipoPdD(TipoPdD.PORTA_DELEGATA);
		}
		else{
			this.infoProtocolloTransazione.setTipoPdD(TipoPdD.PORTA_APPLICATIVA);
		}
	}




	// idBusta [wrapper]

	public String getIdBusta() {
		return this.infoProtocolloTransazione.getIdentificativoRichiesta();
	}
	public void setIdBusta(String idBusta) {
		this.infoProtocolloTransazione.setIdentificativoRichiesta(idBusta);
	}   




	// dominio [wrapper]

	public IDSoggetto getIdSoggetto() {
		if(this.infoProtocolloTransazione.getDominio()!=null){
			IDSoggetto idSoggetto = null;
			if(this.infoProtocolloTransazione.getDominio().getIdentificativoPorta()!=null){
				if(idSoggetto==null){
					idSoggetto = new IDSoggetto();
				}
				idSoggetto.setCodicePorta(this.infoProtocolloTransazione.getDominio().getIdentificativoPorta());
			}
			if(this.infoProtocolloTransazione.getDominio().getSoggetto()!=null){
				if(idSoggetto==null){
					idSoggetto = new IDSoggetto();
				}
				idSoggetto.setTipo(this.infoProtocolloTransazione.getDominio().getSoggetto().getTipo());
				idSoggetto.setNome(this.infoProtocolloTransazione.getDominio().getSoggetto().getBase());
			}
			return idSoggetto;
		}

		return null;
	}
	public void setIdSoggetto(IDSoggetto idPorta) {
		if(idPorta!=null){
			if(this.infoProtocolloTransazione.getDominio()==null){
				this.infoProtocolloTransazione.setDominio(new DominioTransazione());
			}
			this.infoProtocolloTransazione.getDominio().setIdentificativoPorta(idPorta.getCodicePorta());
			if(this.infoProtocolloTransazione.getDominio().getSoggetto()==null){
				this.infoProtocolloTransazione.getDominio().setSoggetto(new DominioSoggetto());
			}
			this.infoProtocolloTransazione.getDominio().getSoggetto().setBase(idPorta.getNome());
			this.infoProtocolloTransazione.getDominio().getSoggetto().setTipo(idPorta.getTipo());
		}else{
			this.infoProtocolloTransazione.setDominio(null);
		}
	}




	// id  [InformazioniProtocollo]

	public InformazioniProtocollo getInformazioniProtocollo() {

		// fruitore
		IDSoggetto fruitore = null;
		if(this.infoProtocolloTransazione.getFruitore()!=null){
			if(this.infoProtocolloTransazione.getFruitore().getIdentificativoPorta()!=null){
				if(fruitore==null){
					fruitore = new IDSoggetto();
				}
				fruitore.setCodicePorta(this.infoProtocolloTransazione.getFruitore().getIdentificativoPorta());
			}
			if(this.infoProtocolloTransazione.getFruitore().getIdentificativo()!=null){
				if(this.infoProtocolloTransazione.getFruitore().getIdentificativo().getBase()!=null){
					if(fruitore==null){
						fruitore = new IDSoggetto();
					}
					fruitore.setNome(this.infoProtocolloTransazione.getFruitore().getIdentificativo().getBase());
				}
				if(this.infoProtocolloTransazione.getFruitore().getIdentificativo().getTipo()!=null){
					if(fruitore==null){
						fruitore = new IDSoggetto();
					}
					fruitore.setTipo(this.infoProtocolloTransazione.getFruitore().getIdentificativo().getTipo());
				}
			}			
		}

		// erogatore
		IDSoggetto erogatore = null;
		if(this.infoProtocolloTransazione.getErogatore()!=null){
			if(this.infoProtocolloTransazione.getErogatore().getIdentificativoPorta()!=null){
				if(erogatore==null){
					erogatore = new IDSoggetto();
				}
				erogatore.setCodicePorta(this.infoProtocolloTransazione.getErogatore().getIdentificativoPorta());
			}
			if(this.infoProtocolloTransazione.getErogatore().getIdentificativo()!=null){
				if(this.infoProtocolloTransazione.getErogatore().getIdentificativo().getBase()!=null){
					if(erogatore==null){
						erogatore = new IDSoggetto();
					}
					erogatore.setNome(this.infoProtocolloTransazione.getErogatore().getIdentificativo().getBase());
				}
				if(this.infoProtocolloTransazione.getErogatore().getIdentificativo().getTipo()!=null){
					if(erogatore==null){
						erogatore = new IDSoggetto();
					}
					erogatore.setTipo(this.infoProtocolloTransazione.getErogatore().getIdentificativo().getTipo());
				}
			}			
		}

		// servizio
		String nomeServizio = null;
		String tipoServizio = null;
		Integer versioneServizio = null;
		boolean existsDatiServizio = false;
		if(this.infoProtocolloTransazione.getServizio()!=null){
			nomeServizio = this.infoProtocolloTransazione.getServizio().getBase();
			tipoServizio = this.infoProtocolloTransazione.getServizio().getTipo();
			versioneServizio = this.infoProtocolloTransazione.getServizio().getVersione();
			existsDatiServizio = true;
		}

		// azione
		String azione = this.infoProtocolloTransazione.getAzione();


		InformazioniProtocollo info = null;
		if(fruitore!=null || erogatore!=null || existsDatiServizio || azione!=null){
			info = new InformazioniProtocollo();
			info.setFruitore(fruitore);
			info.setErogatore(erogatore);
			info.setTipoServizio(tipoServizio);
			info.setServizio(nomeServizio);
			info.setVersioneServizio(versioneServizio);
			info.setAzione(azione);
		}
		return info;

	}
	public void setInformazioniProtocollo(InformazioniProtocollo informazioniBusta) {
		if(informazioniBusta!=null){

			// fruitore
			if(informazioniBusta.getFruitore()!=null && 
					(informazioniBusta.getFruitore().getCodicePorta()!=null || informazioniBusta.getFruitore().getTipo()!=null  || informazioniBusta.getFruitore().getNome()!=null) ){
				if(this.infoProtocolloTransazione.getFruitore()==null){
					this.infoProtocolloTransazione.setFruitore(new Soggetto());
				}
				this.infoProtocolloTransazione.getFruitore().setIdentificativoPorta(informazioniBusta.getFruitore().getCodicePorta());
				if(informazioniBusta.getFruitore().getTipo()!=null || informazioniBusta.getFruitore().getNome()!=null){
					if(this.infoProtocolloTransazione.getFruitore().getIdentificativo()==null){
						this.infoProtocolloTransazione.getFruitore().setIdentificativo(new SoggettoIdentificativo());
					}
					this.infoProtocolloTransazione.getFruitore().getIdentificativo().setBase(informazioniBusta.getFruitore().getNome());
					this.infoProtocolloTransazione.getFruitore().getIdentificativo().setTipo(informazioniBusta.getFruitore().getTipo());
				}
				else{
					this.infoProtocolloTransazione.getFruitore().setIdentificativo(null);
				}
			}
			else{
				this.infoProtocolloTransazione.setFruitore(null);
			}

			// erogatore
			if(informazioniBusta.getErogatore()!=null && 
					(informazioniBusta.getErogatore().getCodicePorta()!=null || informazioniBusta.getErogatore().getTipo()!=null  || informazioniBusta.getErogatore().getNome()!=null) ){
				if(this.infoProtocolloTransazione.getErogatore()==null){
					this.infoProtocolloTransazione.setErogatore(new Soggetto());
				}
				this.infoProtocolloTransazione.getErogatore().setIdentificativoPorta(informazioniBusta.getErogatore().getCodicePorta());
				if(informazioniBusta.getErogatore().getTipo()!=null || informazioniBusta.getErogatore().getNome()!=null){
					if(this.infoProtocolloTransazione.getErogatore().getIdentificativo()==null){
						this.infoProtocolloTransazione.getErogatore().setIdentificativo(new SoggettoIdentificativo());
					}
					this.infoProtocolloTransazione.getErogatore().getIdentificativo().setBase(informazioniBusta.getErogatore().getNome());
					this.infoProtocolloTransazione.getErogatore().getIdentificativo().setTipo(informazioniBusta.getErogatore().getTipo());
				}
				else{
					this.infoProtocolloTransazione.getErogatore().setIdentificativo(null);
				}
			}
			else{
				this.infoProtocolloTransazione.setErogatore(null);
			}

			// servizio
			if(informazioniBusta.getServizio()!=null || informazioniBusta.getTipoServizio()!=null || informazioniBusta.getVersioneServizio()!=null){
				if(this.infoProtocolloTransazione.getServizio()==null){
					this.infoProtocolloTransazione.setServizio(new Servizio());
				}
				this.infoProtocolloTransazione.getServizio().setBase(informazioniBusta.getServizio());
				this.infoProtocolloTransazione.getServizio().setTipo(informazioniBusta.getTipoServizio());
				this.infoProtocolloTransazione.getServizio().setVersione(informazioniBusta.getVersioneServizio());
			}else{
				this.infoProtocolloTransazione.setServizio(null);
			}

			// azione
			this.infoProtocolloTransazione.setAzione(informazioniBusta.getAzione());
		}
		else{
			this.infoProtocolloTransazione.setFruitore(null);
			this.infoProtocolloTransazione.setErogatore(null);
			this.infoProtocolloTransazione.setServizio(null);
			this.infoProtocolloTransazione.setAzione(null);
		}
	}




	// gdo [wrapper]

	public Date getGdo() {
		return this.infoProtocolloTransazione.getOraRegistrazione();
	}
	public void setGdo(Date gdo) {
		this.infoProtocolloTransazione.setOraRegistrazione(gdo);
	}




	// nomePorta [wrapper]

	public String getNomePorta() {
		return this.infoProtocolloTransazione.getNomePorta();
	}
	public void setNomePorta(String nomePorta) {
		this.infoProtocolloTransazione.setNomePorta(nomePorta);
	}



	// serviziApplicativi [wrapper]

	public List<String> getServiziApplicativiList() {
		return this.infoProtocolloTransazione.getServizioApplicativoList();
	}
	public void setServiziApplicativiList(List<String> serviziApplicativiList) {
		this.infoProtocolloTransazione.setServizioApplicativoList(serviziApplicativiList);
	}
	public int sizeServiziApplicativiList(){
		return this.infoProtocolloTransazione.sizeServizioApplicativoList();
	}





	// correlazioneApplicativa [wrapper]

	public String getCorrelazioneApplicativa() {
		return this.infoProtocolloTransazione.getIdentificativoCorrelazioneRichiesta();
	}
	public void setCorrelazioneApplicativa(String correlazioneApplicativa) {
		this.infoProtocolloTransazione.setIdentificativoCorrelazioneRichiesta(correlazioneApplicativa);
	}
	public String getCorrelazioneApplicativaRisposta() {
		return this.infoProtocolloTransazione.getIdentificativoCorrelazioneRisposta();
	}
	public void setCorrelazioneApplicativaRisposta(String correlazioneApplicativaRisposta) {
		this.infoProtocolloTransazione.setIdentificativoCorrelazioneRisposta(correlazioneApplicativaRisposta);
	}





	// protocollo [wrapper]

	public String getProtocollo() {
		if(this.infoProtocolloTransazione.getProtocollo()!=null)
			return this.infoProtocolloTransazione.getProtocollo().getIdentificativo();
		return null;
	}

	public void setProtocollo(String protocollo) {
		if(protocollo!=null){
			if(this.infoProtocolloTransazione.getProtocollo()==null){
				this.infoProtocolloTransazione.setProtocollo(new Protocollo());
			}
			this.infoProtocolloTransazione.getProtocollo().setIdentificativo(protocollo);
		}
		else{
			if(this.infoProtocolloTransazione.getProtocollo()!=null){
				if(this.infoProtocolloTransazione.getProtocollo().sizeProprietaList()<=0){
					this.infoProtocolloTransazione.setProtocollo(null);
				}
				else{
					this.infoProtocolloTransazione.getProtocollo().setIdentificativo(null);
				}
			}
		}
	}




	// properties [wrapped]

	public void addProperty(String key,String value){
		// Per evitare nullPointer durante la serializzazione
		// Non deve essere inserito nemmeno il valore ""
		if(value!=null && !"".equals(value)){
			if(this.infoProtocolloTransazione.getProtocollo()==null){
				this.infoProtocolloTransazione.setProtocollo(new Protocollo());
			}
			Proprieta proprieta = new Proprieta();
			proprieta.setNome(key);
			proprieta.setValore(value);
			this.infoProtocolloTransazione.getProtocollo().addProprieta(proprieta);
		}
	}

	public int sizeProperties(){
		if(this.infoProtocolloTransazione.getProtocollo()!=null){
			return this.infoProtocolloTransazione.getProtocollo().sizeProprietaList();
		}
		return 0;
	}

	public String getProperty(String key){
		if(this.infoProtocolloTransazione.getProtocollo()!=null){
			for (int i = 0; i < this.infoProtocolloTransazione.getProtocollo().sizeProprietaList(); i++) {
				Proprieta proprieta = this.infoProtocolloTransazione.getProtocollo().getProprieta(i);
				if(proprieta.getNome().equals(key)){
					return proprieta.getValore();
				}
			}
		}
		return null;
	}

	public String removeProperty(String key){
		if(this.infoProtocolloTransazione.getProtocollo()!=null){
			for (int i = 0; i < this.infoProtocolloTransazione.getProtocollo().sizeProprietaList(); i++) {
				Proprieta proprieta = this.infoProtocolloTransazione.getProtocollo().getProprieta(i);
				if(proprieta.getNome().equals(key)){
					this.infoProtocolloTransazione.getProtocollo().removeProprieta(i);
					return proprieta.getValore();
				}
			}
		}
		return null;
	}

	public String[] getPropertiesValues() {
		List<String> propertiesValues = new ArrayList<String>();
		if(this.infoProtocolloTransazione.getProtocollo()!=null){
			for (int i = 0; i < this.infoProtocolloTransazione.getProtocollo().sizeProprietaList(); i++) {
				Proprieta proprieta = this.infoProtocolloTransazione.getProtocollo().getProprieta(i);
				propertiesValues.add(proprieta.getValore());
			}
		}
		if(propertiesValues.size()>0){
			return propertiesValues.toArray(new String[1]);
		}
		else{
			return null;
		}
	}

	public String[] getPropertiesNames() {
		List<String> propertiesValues = new ArrayList<String>();
		if(this.infoProtocolloTransazione.getProtocollo()!=null){
			for (int i = 0; i < this.infoProtocolloTransazione.getProtocollo().sizeProprietaList(); i++) {
				Proprieta proprieta = this.infoProtocolloTransazione.getProtocollo().getProprieta(i);
				propertiesValues.add(proprieta.getNome());
			}
		}
		if(propertiesValues.size()>0){
			return propertiesValues.toArray(new String[1]);
		}
		else{
			return null;
		}
	}

	public void setProperties(Hashtable<String, String> params) {
		Enumeration<String> keys = params.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			this.addProperty(key, params.get(key));
		}
	}

	public Hashtable<String, String> getProperties() {
		Hashtable<String, String> map = new Hashtable<String, String>();
		if(this.infoProtocolloTransazione.getProtocollo()!=null){
			for (int i = 0; i < this.infoProtocolloTransazione.getProtocollo().sizeProprietaList(); i++) {
				Proprieta proprieta = this.infoProtocolloTransazione.getProtocollo().getProprieta(i);
				map.put(proprieta.getNome(), proprieta.getValore());
			}
		}
		return map;
	}


}


