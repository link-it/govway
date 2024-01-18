/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package it.gov.spcoop.sica.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for accordoCooperazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accordoCooperazione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="descrizione" type="{http://spcoop.gov.it/sica/manifest}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="specificaSemiformale" type="{http://spcoop.gov.it/sica/manifest}SpecificaSemiformale" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="allegati" type="{http://spcoop.gov.it/sica/manifest}ElencoAllegati" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="elencoPartecipanti" type="{http://spcoop.gov.it/sica/manifest}ElencoPartecipanti" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="serviziComposti" type="{http://spcoop.gov.it/sica/manifest}ElencoServiziComposti" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="nome" type="{http://spcoop.gov.it/sica/manifest}string" use="required"/&gt;
 * 		&lt;attribute name="versione" type="{http://spcoop.gov.it/sica/manifest}string" use="optional"/&gt;
 * 		&lt;attribute name="dataCreazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="required"/&gt;
 * 		&lt;attribute name="dataPubblicazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="optional"/&gt;
 * 		&lt;attribute name="firmato" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * 		&lt;attribute name="riservato" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * 		&lt;attribute name="coordinatore" type="{http://spcoop.gov.it/sica/manifest}anyURI" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "accordoCooperazione", 
  propOrder = {
  	"descrizione",
  	"specificaSemiformale",
  	"allegati",
  	"elencoPartecipanti",
  	"serviziComposti"
  }
)

@XmlRootElement(name = "accordoCooperazione")

public class AccordoCooperazione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public AccordoCooperazione() {
    super();
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
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

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getVersione() {
    return this.versione;
  }

  public void setVersione(java.lang.String versione) {
    this.versione = versione;
  }

  public java.util.Date getDataCreazione() {
    return this.dataCreazione;
  }

  public void setDataCreazione(java.util.Date dataCreazione) {
    this.dataCreazione = dataCreazione;
  }

  public java.util.Date getDataPubblicazione() {
    return this.dataPubblicazione;
  }

  public void setDataPubblicazione(java.util.Date dataPubblicazione) {
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

  public java.net.URI getCoordinatore() {
    return this.coordinatore;
  }

  public void setCoordinatore(java.net.URI coordinatore) {
    this.coordinatore = coordinatore;
  }

  private static final long serialVersionUID = 1L;

  private static it.gov.spcoop.sica.manifest.model.AccordoCooperazioneModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(it.gov.spcoop.sica.manifest.AccordoCooperazione.modelStaticInstance==null){
  			it.gov.spcoop.sica.manifest.AccordoCooperazione.modelStaticInstance = new it.gov.spcoop.sica.manifest.model.AccordoCooperazioneModel();
	  }
  }
  public static it.gov.spcoop.sica.manifest.model.AccordoCooperazioneModel model(){
	  if(it.gov.spcoop.sica.manifest.AccordoCooperazione.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return it.gov.spcoop.sica.manifest.AccordoCooperazione.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="descrizione",required=true,nillable=false)
  protected java.lang.String descrizione;

  @XmlElement(name="specificaSemiformale",required=false,nillable=false)
  protected SpecificaSemiformale specificaSemiformale;

  @XmlElement(name="allegati",required=false,nillable=false)
  protected ElencoAllegati allegati;

  @XmlElement(name="elencoPartecipanti",required=true,nillable=false)
  protected ElencoPartecipanti elencoPartecipanti;

  @XmlElement(name="serviziComposti",required=false,nillable=false)
  protected ElencoServiziComposti serviziComposti;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="versione",required=false)
  protected java.lang.String versione;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="dataCreazione",required=true)
  protected java.util.Date dataCreazione;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="dataPubblicazione",required=false)
  protected java.util.Date dataPubblicazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="firmato",required=false)
  protected boolean firmato = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="riservato",required=false)
  protected boolean riservato = false;

  @javax.xml.bind.annotation.XmlSchemaType(name="anyURI")
  @XmlAttribute(name="coordinatore",required=false)
  protected java.net.URI coordinatore;

}
