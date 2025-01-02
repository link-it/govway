/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.core.commons.search;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for porta-applicativa complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="porta-applicativa"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="stato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="id-soggetto" type="{http://www.openspcoop2.org/core/commons/search}id-soggetto" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="tipo_servizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="nome_servizio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="versione_servizio" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="mode_azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="nome_azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="nome_porta_delegante_azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="canale" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="porta-applicativa-servizio-applicativo" type="{http://www.openspcoop2.org/core/commons/search}porta-applicativa-servizio-applicativo" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="porta-applicativa-azione" type="{http://www.openspcoop2.org/core/commons/search}porta-applicativa-azione" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "porta-applicativa", 
  propOrder = {
  	"nome",
  	"stato",
  	"idSoggetto",
  	"tipoServizio",
  	"nomeServizio",
  	"versioneServizio",
  	"modeAzione",
  	"nomeAzione",
  	"nomePortaDeleganteAzione",
  	"canale",
  	"portaApplicativaServizioApplicativo",
  	"portaApplicativaAzione"
  }
)

@XmlRootElement(name = "porta-applicativa")

public class PortaApplicativa extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public PortaApplicativa() {
    super();
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getStato() {
    return this.stato;
  }

  public void setStato(java.lang.String stato) {
    this.stato = stato;
  }

  public IdSoggetto getIdSoggetto() {
    return this.idSoggetto;
  }

  public void setIdSoggetto(IdSoggetto idSoggetto) {
    this.idSoggetto = idSoggetto;
  }

  public java.lang.String getTipoServizio() {
    return this.tipoServizio;
  }

  public void setTipoServizio(java.lang.String tipoServizio) {
    this.tipoServizio = tipoServizio;
  }

  public java.lang.String getNomeServizio() {
    return this.nomeServizio;
  }

  public void setNomeServizio(java.lang.String nomeServizio) {
    this.nomeServizio = nomeServizio;
  }

  public java.lang.Integer getVersioneServizio() {
    return this.versioneServizio;
  }

  public void setVersioneServizio(java.lang.Integer versioneServizio) {
    this.versioneServizio = versioneServizio;
  }

  public java.lang.String getModeAzione() {
    return this.modeAzione;
  }

  public void setModeAzione(java.lang.String modeAzione) {
    this.modeAzione = modeAzione;
  }

  public java.lang.String getNomeAzione() {
    return this.nomeAzione;
  }

  public void setNomeAzione(java.lang.String nomeAzione) {
    this.nomeAzione = nomeAzione;
  }

  public java.lang.String getNomePortaDeleganteAzione() {
    return this.nomePortaDeleganteAzione;
  }

  public void setNomePortaDeleganteAzione(java.lang.String nomePortaDeleganteAzione) {
    this.nomePortaDeleganteAzione = nomePortaDeleganteAzione;
  }

  public java.lang.String getCanale() {
    return this.canale;
  }

  public void setCanale(java.lang.String canale) {
    this.canale = canale;
  }

  public void addPortaApplicativaServizioApplicativo(PortaApplicativaServizioApplicativo portaApplicativaServizioApplicativo) {
    this.portaApplicativaServizioApplicativo.add(portaApplicativaServizioApplicativo);
  }

  public PortaApplicativaServizioApplicativo getPortaApplicativaServizioApplicativo(int index) {
    return this.portaApplicativaServizioApplicativo.get( index );
  }

  public PortaApplicativaServizioApplicativo removePortaApplicativaServizioApplicativo(int index) {
    return this.portaApplicativaServizioApplicativo.remove( index );
  }

  public List<PortaApplicativaServizioApplicativo> getPortaApplicativaServizioApplicativoList() {
    return this.portaApplicativaServizioApplicativo;
  }

  public void setPortaApplicativaServizioApplicativoList(List<PortaApplicativaServizioApplicativo> portaApplicativaServizioApplicativo) {
    this.portaApplicativaServizioApplicativo=portaApplicativaServizioApplicativo;
  }

  public int sizePortaApplicativaServizioApplicativoList() {
    return this.portaApplicativaServizioApplicativo.size();
  }

  public void addPortaApplicativaAzione(PortaApplicativaAzione portaApplicativaAzione) {
    this.portaApplicativaAzione.add(portaApplicativaAzione);
  }

  public PortaApplicativaAzione getPortaApplicativaAzione(int index) {
    return this.portaApplicativaAzione.get( index );
  }

  public PortaApplicativaAzione removePortaApplicativaAzione(int index) {
    return this.portaApplicativaAzione.remove( index );
  }

  public List<PortaApplicativaAzione> getPortaApplicativaAzioneList() {
    return this.portaApplicativaAzione;
  }

  public void setPortaApplicativaAzioneList(List<PortaApplicativaAzione> portaApplicativaAzione) {
    this.portaApplicativaAzione=portaApplicativaAzione;
  }

  public int sizePortaApplicativaAzioneList() {
    return this.portaApplicativaAzione.size();
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.commons.search.model.PortaApplicativaModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.commons.search.PortaApplicativa.modelStaticInstance==null){
  			org.openspcoop2.core.commons.search.PortaApplicativa.modelStaticInstance = new org.openspcoop2.core.commons.search.model.PortaApplicativaModel();
	  }
  }
  public static org.openspcoop2.core.commons.search.model.PortaApplicativaModel model(){
	  if(org.openspcoop2.core.commons.search.PortaApplicativa.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.commons.search.PortaApplicativa.modelStaticInstance;
  }


  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=true,nillable=false)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="stato",required=false,nillable=false)
  protected java.lang.String stato;

  @XmlElement(name="id-soggetto",required=true,nillable=false)
  protected IdSoggetto idSoggetto;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="tipo_servizio",required=true,nillable=false)
  protected java.lang.String tipoServizio;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome_servizio",required=true,nillable=false)
  protected java.lang.String nomeServizio;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="integer")
  @XmlElement(name="versione_servizio",required=true,nillable=false)
  protected java.lang.Integer versioneServizio;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="mode_azione",required=true,nillable=false)
  protected java.lang.String modeAzione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome_azione",required=true,nillable=false)
  protected java.lang.String nomeAzione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome_porta_delegante_azione",required=true,nillable=false)
  protected java.lang.String nomePortaDeleganteAzione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="canale",required=true,nillable=false)
  protected java.lang.String canale;

  @XmlElement(name="porta-applicativa-servizio-applicativo",required=true,nillable=false)
  private List<PortaApplicativaServizioApplicativo> portaApplicativaServizioApplicativo = new ArrayList<>();

  /**
   * Use method getPortaApplicativaServizioApplicativoList
   * @return List&lt;PortaApplicativaServizioApplicativo&gt;
  */
  public List<PortaApplicativaServizioApplicativo> getPortaApplicativaServizioApplicativo() {
  	return this.getPortaApplicativaServizioApplicativoList();
  }

  /**
   * Use method setPortaApplicativaServizioApplicativoList
   * @param portaApplicativaServizioApplicativo List&lt;PortaApplicativaServizioApplicativo&gt;
  */
  public void setPortaApplicativaServizioApplicativo(List<PortaApplicativaServizioApplicativo> portaApplicativaServizioApplicativo) {
  	this.setPortaApplicativaServizioApplicativoList(portaApplicativaServizioApplicativo);
  }

  /**
   * Use method sizePortaApplicativaServizioApplicativoList
   * @return lunghezza della lista
  */
  public int sizePortaApplicativaServizioApplicativo() {
  	return this.sizePortaApplicativaServizioApplicativoList();
  }

  @XmlElement(name="porta-applicativa-azione",required=true,nillable=false)
  private List<PortaApplicativaAzione> portaApplicativaAzione = new ArrayList<>();

  /**
   * Use method getPortaApplicativaAzioneList
   * @return List&lt;PortaApplicativaAzione&gt;
  */
  public List<PortaApplicativaAzione> getPortaApplicativaAzione() {
  	return this.getPortaApplicativaAzioneList();
  }

  /**
   * Use method setPortaApplicativaAzioneList
   * @param portaApplicativaAzione List&lt;PortaApplicativaAzione&gt;
  */
  public void setPortaApplicativaAzione(List<PortaApplicativaAzione> portaApplicativaAzione) {
  	this.setPortaApplicativaAzioneList(portaApplicativaAzione);
  }

  /**
   * Use method sizePortaApplicativaAzioneList
   * @return lunghezza della lista
  */
  public int sizePortaApplicativaAzione() {
  	return this.sizePortaApplicativaAzioneList();
  }

}
