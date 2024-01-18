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
package org.openspcoop2.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for porta-applicativa-servizio-applicativo-connettore complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="porta-applicativa-servizio-applicativo-connettore"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="filtro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="proprieta" type="{http://www.openspcoop2.org/core/config}proprieta" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="proprieta-oggetto" type="{http://www.openspcoop2.org/core/config}proprieta-oggetto" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="notifica" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional"/&gt;
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * 		&lt;attribute name="scheduling" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * 		&lt;attribute name="coda" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="priorita" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="priorita-max" type="{http://www.w3.org/2001/XMLSchema}boolean" use="optional" default="false"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "porta-applicativa-servizio-applicativo-connettore", 
  propOrder = {
  	"filtro",
  	"proprieta",
  	"proprietaOggetto"
  }
)

@XmlRootElement(name = "porta-applicativa-servizio-applicativo-connettore")

public class PortaApplicativaServizioApplicativoConnettore extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public PortaApplicativaServizioApplicativoConnettore() {
    super();
  }

  public void addFiltro(java.lang.String filtro) {
    this.filtro.add(filtro);
  }

  public java.lang.String getFiltro(int index) {
    return this.filtro.get( index );
  }

  public java.lang.String removeFiltro(int index) {
    return this.filtro.remove( index );
  }

  public List<java.lang.String> getFiltroList() {
    return this.filtro;
  }

  public void setFiltroList(List<java.lang.String> filtro) {
    this.filtro=filtro;
  }

  public int sizeFiltroList() {
    return this.filtro.size();
  }

  public void addProprieta(Proprieta proprieta) {
    this.proprieta.add(proprieta);
  }

  public Proprieta getProprieta(int index) {
    return this.proprieta.get( index );
  }

  public Proprieta removeProprieta(int index) {
    return this.proprieta.remove( index );
  }

  public List<Proprieta> getProprietaList() {
    return this.proprieta;
  }

  public void setProprietaList(List<Proprieta> proprieta) {
    this.proprieta=proprieta;
  }

  public int sizeProprietaList() {
    return this.proprieta.size();
  }

  public ProprietaOggetto getProprietaOggetto() {
    return this.proprietaOggetto;
  }

  public void setProprietaOggetto(ProprietaOggetto proprietaOggetto) {
    this.proprietaOggetto = proprietaOggetto;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public boolean isNotifica() {
    return this.notifica;
  }

  public boolean getNotifica() {
    return this.notifica;
  }

  public void setNotifica(boolean notifica) {
    this.notifica = notifica;
  }

  public void setStatoRawEnumValue(String value) {
    this.stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getStatoRawEnumValue() {
    if(this.stato == null){
    	return null;
    }else{
    	return this.stato.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getStato() {
    return this.stato;
  }

  public void setStato(org.openspcoop2.core.config.constants.StatoFunzionalita stato) {
    this.stato = stato;
  }

  public void setSchedulingRawEnumValue(String value) {
    this.scheduling = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getSchedulingRawEnumValue() {
    if(this.scheduling == null){
    	return null;
    }else{
    	return this.scheduling.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getScheduling() {
    return this.scheduling;
  }

  public void setScheduling(org.openspcoop2.core.config.constants.StatoFunzionalita scheduling) {
    this.scheduling = scheduling;
  }

  public java.lang.String getCoda() {
    return this.coda;
  }

  public void setCoda(java.lang.String coda) {
    this.coda = coda;
  }

  public java.lang.String getPriorita() {
    return this.priorita;
  }

  public void setPriorita(java.lang.String priorita) {
    this.priorita = priorita;
  }

  public boolean isPrioritaMax() {
    return this.prioritaMax;
  }

  public boolean getPrioritaMax() {
    return this.prioritaMax;
  }

  public void setPrioritaMax(boolean prioritaMax) {
    this.prioritaMax = prioritaMax;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="filtro",required=true,nillable=false)
  private List<java.lang.String> filtro = new ArrayList<>();

  /**
   * Use method getFiltroList
   * @return List&lt;java.lang.String&gt;
  */
  public List<java.lang.String> getFiltro() {
  	return this.getFiltroList();
  }

  /**
   * Use method setFiltroList
   * @param filtro List&lt;java.lang.String&gt;
  */
  public void setFiltro(List<java.lang.String> filtro) {
  	this.setFiltroList(filtro);
  }

  /**
   * Use method sizeFiltroList
   * @return lunghezza della lista
  */
  public int sizeFiltro() {
  	return this.sizeFiltroList();
  }

  @XmlElement(name="proprieta",required=true,nillable=false)
  private List<Proprieta> proprieta = new ArrayList<>();

  /**
   * Use method getProprietaList
   * @return List&lt;Proprieta&gt;
  */
  public List<Proprieta> getProprieta() {
  	return this.getProprietaList();
  }

  /**
   * Use method setProprietaList
   * @param proprieta List&lt;Proprieta&gt;
  */
  public void setProprieta(List<Proprieta> proprieta) {
  	this.setProprietaList(proprieta);
  }

  /**
   * Use method sizeProprietaList
   * @return lunghezza della lista
  */
  public int sizeProprieta() {
  	return this.sizeProprietaList();
  }

  @XmlElement(name="proprieta-oggetto",required=false,nillable=false)
  protected ProprietaOggetto proprietaOggetto;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="notifica",required=false)
  protected boolean notifica;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String statoRawEnumValue;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalita stato = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String schedulingRawEnumValue;

  @XmlAttribute(name="scheduling",required=false)
  protected StatoFunzionalita scheduling = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="coda",required=false)
  protected java.lang.String coda;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="priorita",required=false)
  protected java.lang.String priorita;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="boolean")
  @XmlAttribute(name="priorita-max",required=false)
  protected boolean prioritaMax = false;

}
