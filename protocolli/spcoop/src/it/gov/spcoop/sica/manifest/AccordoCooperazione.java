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
package it.gov.spcoop.sica.manifest;

import java.io.Serializable;
import java.util.Date;


/** <p>Java class AccordoCooperazione.
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 */

public class AccordoCooperazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  private Long id;

  protected String descrizione;

  protected SpecificaSemiformale specificaSemiformale;

  protected ElencoAllegati allegati;

  protected ElencoPartecipanti elencoPartecipanti;

  protected ElencoServiziComposti serviziComposti;

  protected String nome;

  protected String versione;

  protected Date dataCreazione;

  protected Date dataPubblicazione;

  protected boolean firmato;

  protected boolean riservato;

  protected String coordinatore;


  public AccordoCooperazione() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public String getDescrizione() {
    if(this.descrizione!=null && ("".equals(this.descrizione)==false)){
		return this.descrizione.trim();
	}else{
		return null;
	}

  }

  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }

  public SpecificaSemiformale getSpecificaSemiformale() {
    return this.specificaSemiformale;
  }

  public void setSpecificaSemiformale(SpecificaSemiformale specificaSemiformale) {
    this.specificaSemiformale = specificaSemiformale;
  }

  public ElencoAllegati getAllegati() {
    return this.allegati;
  }

  public void setAllegati(ElencoAllegati allegati) {
    this.allegati = allegati;
  }

  public ElencoPartecipanti getElencoPartecipanti() {
    return this.elencoPartecipanti;
  }

  public void setElencoPartecipanti(ElencoPartecipanti elencoPartecipanti) {
    this.elencoPartecipanti = elencoPartecipanti;
  }

  public ElencoServiziComposti getServiziComposti() {
    return this.serviziComposti;
  }

  public void setServiziComposti(ElencoServiziComposti serviziComposti) {
    this.serviziComposti = serviziComposti;
  }

  public String getNome() {
    if(this.nome!=null && ("".equals(this.nome)==false)){
		return this.nome.trim();
	}else{
		return null;
	}

  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getVersione() {
    if(this.versione!=null && ("".equals(this.versione)==false)){
		return this.versione.trim();
	}else{
		return null;
	}

  }

  public void setVersione(String versione) {
    this.versione = versione;
  }

  public Date getDataCreazione() {
    return this.dataCreazione;
  }

  public void setDataCreazione(Date dataCreazione) {
    this.dataCreazione = dataCreazione;
  }

  public Date getDataPubblicazione() {
    return this.dataPubblicazione;
  }

  public void setDataPubblicazione(Date dataPubblicazione) {
    this.dataPubblicazione = dataPubblicazione;
  }

  public boolean isFirmato() {
    return this.firmato;
  }

  public boolean getFirmato() {
    return this.firmato;
  }

  public void setFirmato(boolean firmato) {
    this.firmato = firmato;
  }

  public boolean isRiservato() {
    return this.riservato;
  }

  public boolean getRiservato() {
    return this.riservato;
  }

  public void setRiservato(boolean riservato) {
    this.riservato = riservato;
  }

  public String getCoordinatore() {
    if(this.coordinatore!=null && ("".equals(this.coordinatore)==false)){
		return this.coordinatore.trim();
	}else{
		return null;
	}

  }

  public void setCoordinatore(String coordinatore) {
    this.coordinatore = coordinatore;
  }

  private static final long serialVersionUID = 1L;

	@Override
	public String serialize(org.openspcoop2.utils.beans.WriteToSerializerType type) throws org.openspcoop2.utils.UtilsException {
		if(type!=null && org.openspcoop2.utils.beans.WriteToSerializerType.JAXB.equals(type)){
			throw new org.openspcoop2.utils.UtilsException("Jaxb annotations not generated");
		}
		else{
			return super.serialize(type);
		}
	}
	@Override
	public String toXml_Jaxb() throws org.openspcoop2.utils.UtilsException {
		throw new org.openspcoop2.utils.UtilsException("Jaxb annotations not generated");
	}

  public static final String DESCRIZIONE = "descrizione";

  public static final String SPECIFICA_SEMIFORMALE = "specificaSemiformale";

  public static final String ALLEGATI = "allegati";

  public static final String ELENCO_PARTECIPANTI = "elencoPartecipanti";

  public static final String SERVIZI_COMPOSTI = "serviziComposti";

  public static final String NOME = "nome";

  public static final String VERSIONE = "versione";

  public static final String DATA_CREAZIONE = "dataCreazione";

  public static final String DATA_PUBBLICAZIONE = "dataPubblicazione";

  public static final String FIRMATO = "firmato";

  public static final String RISERVATO = "riservato";

  public static final String COORDINATORE = "coordinatore";

}
