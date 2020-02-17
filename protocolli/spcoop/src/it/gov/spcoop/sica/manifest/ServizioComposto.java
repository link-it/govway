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
package it.gov.spcoop.sica.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for servizioComposto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="servizioComposto"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="descrizione" type="{http://spcoop.gov.it/sica/manifest}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="specificaSemiformale" type="{http://spcoop.gov.it/sica/manifest}SpecificaSemiformale" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="allegati" type="{http://spcoop.gov.it/sica/manifest}ElencoAllegati" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="specificaInterfaccia" type="{http://spcoop.gov.it/sica/manifest}SpecificaInterfaccia" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="specificaConversazione" type="{http://spcoop.gov.it/sica/manifest}SpecificaConversazione" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="riferimentoAccordoCooperazione" type="{http://spcoop.gov.it/sica/manifest}anyURI" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="serviziComponenti" type="{http://spcoop.gov.it/sica/manifest}ElencoServiziComponenti" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="specificaCoordinamento" type="{http://spcoop.gov.it/sica/manifest}SpecificaCoordinamento" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="nome" type="{http://spcoop.gov.it/sica/manifest}string" use="required"/&gt;
 * 		&lt;attribute name="versione" type="{http://spcoop.gov.it/sica/manifest}string" use="optional"/&gt;
 * 		&lt;attribute name="dataCreazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="required"/&gt;
 * 		&lt;attribute name="dataPubblicazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="optional"/&gt;
 * 		&lt;attribute name="firmato" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * 		&lt;attribute name="riservato" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * 		&lt;attribute name="pubblicatore" type="{http://spcoop.gov.it/sica/manifest}anyURI" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "servizioComposto", 
  propOrder = {
  	"descrizione",
  	"specificaSemiformale",
  	"allegati",
  	"specificaInterfaccia",
  	"specificaConversazione",
  	"riferimentoAccordoCooperazione",
  	"serviziComponenti",
  	"specificaCoordinamento"
  }
)

@XmlRootElement(name = "servizioComposto")

public class ServizioComposto extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ServizioComposto() {
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

  public SpecificaInterfaccia getSpecificaInterfaccia() {
    return this.specificaInterfaccia;
  }

  public void setSpecificaInterfaccia(SpecificaInterfaccia specificaInterfaccia) {
    this.specificaInterfaccia = specificaInterfaccia;
  }

  public SpecificaConversazione getSpecificaConversazione() {
    return this.specificaConversazione;
  }

  public void setSpecificaConversazione(SpecificaConversazione specificaConversazione) {
    this.specificaConversazione = specificaConversazione;
  }

  public java.net.URI getRiferimentoAccordoCooperazione() {
    return this.riferimentoAccordoCooperazione;
  }

  public void setRiferimentoAccordoCooperazione(java.net.URI riferimentoAccordoCooperazione) {
    this.riferimentoAccordoCooperazione = riferimentoAccordoCooperazione;
  }

  public ElencoServiziComponenti getServiziComponenti() {
    return this.serviziComponenti;
  }

  public void setServiziComponenti(ElencoServiziComponenti serviziComponenti) {
    this.serviziComponenti = serviziComponenti;
  }

  public SpecificaCoordinamento getSpecificaCoordinamento() {
    return this.specificaCoordinamento;
  }

  public void setSpecificaCoordinamento(SpecificaCoordinamento specificaCoordinamento) {
    this.specificaCoordinamento = specificaCoordinamento;
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

  public java.net.URI getPubblicatore() {
    return this.pubblicatore;
  }

  public void setPubblicatore(java.net.URI pubblicatore) {
    this.pubblicatore = pubblicatore;
  }

  private static final long serialVersionUID = 1L;

  private static it.gov.spcoop.sica.manifest.model.ServizioCompostoModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(it.gov.spcoop.sica.manifest.ServizioComposto.modelStaticInstance==null){
  			it.gov.spcoop.sica.manifest.ServizioComposto.modelStaticInstance = new it.gov.spcoop.sica.manifest.model.ServizioCompostoModel();
	  }
  }
  public static it.gov.spcoop.sica.manifest.model.ServizioCompostoModel model(){
	  if(it.gov.spcoop.sica.manifest.ServizioComposto.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return it.gov.spcoop.sica.manifest.ServizioComposto.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="descrizione",required=true,nillable=false)
  protected java.lang.String descrizione;

  @XmlElement(name="specificaSemiformale",required=false,nillable=false)
  protected SpecificaSemiformale specificaSemiformale;

  @XmlElement(name="allegati",required=false,nillable=false)
  protected ElencoAllegati allegati;

  @XmlElement(name="specificaInterfaccia",required=true,nillable=false)
  protected SpecificaInterfaccia specificaInterfaccia;

  @XmlElement(name="specificaConversazione",required=false,nillable=false)
  protected SpecificaConversazione specificaConversazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="anyURI")
  @XmlElement(name="riferimentoAccordoCooperazione",required=true,nillable=false)
  protected java.net.URI riferimentoAccordoCooperazione;

  @XmlElement(name="serviziComponenti",required=true,nillable=false)
  protected ElencoServiziComponenti serviziComponenti;

  @XmlElement(name="specificaCoordinamento",required=false,nillable=false)
  protected SpecificaCoordinamento specificaCoordinamento;

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
  @XmlAttribute(name="pubblicatore",required=false)
  protected java.net.URI pubblicatore;

}
