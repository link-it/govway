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
package org.openspcoop2.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for generic-properties complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="generic-properties"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="property" type="{http://www.openspcoop2.org/core/config}Property" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 			&lt;element name="proprieta-oggetto" type="{http://www.openspcoop2.org/core/config}proprieta-oggetto" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="tipologia" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "generic-properties", 
  propOrder = {
  	"property",
  	"proprietaOggetto"
  }
)

@XmlRootElement(name = "generic-properties")

public class GenericProperties extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public GenericProperties() {
    super();
  }

  public void addProperty(Property property) {
    this.property.add(property);
  }

  public Property getProperty(int index) {
    return this.property.get( index );
  }

  public Property removeProperty(int index) {
    return this.property.remove( index );
  }

  public List<Property> getPropertyList() {
    return this.property;
  }

  public void setPropertyList(List<Property> property) {
    this.property=property;
  }

  public int sizePropertyList() {
    return this.property.size();
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

  public java.lang.String getTipologia() {
    return this.tipologia;
  }

  public void setTipologia(java.lang.String tipologia) {
    this.tipologia = tipologia;
  }

  public java.lang.String getTipo() {
    return this.tipo;
  }

  public void setTipo(java.lang.String tipo) {
    this.tipo = tipo;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="property",required=true,nillable=false)
  private List<Property> property = new ArrayList<>();

  /**
   * Use method getPropertyList
   * @return List&lt;Property&gt;
  */
  public List<Property> getProperty() {
  	return this.getPropertyList();
  }

  /**
   * Use method setPropertyList
   * @param property List&lt;Property&gt;
  */
  public void setProperty(List<Property> property) {
  	this.setPropertyList(property);
  }

  /**
   * Use method sizePropertyList
   * @return lunghezza della lista
  */
  public int sizeProperty() {
  	return this.sizePropertyList();
  }

  @XmlElement(name="proprieta-oggetto",required=false,nillable=false)
  protected ProprietaOggetto proprietaOggetto;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=false)
  protected java.lang.String descrizione;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipologia",required=true)
  protected java.lang.String tipologia;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo",required=true)
  protected java.lang.String tipo;

}
