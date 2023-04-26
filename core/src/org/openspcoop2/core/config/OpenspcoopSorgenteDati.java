/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for openspcoop-sorgente-dati complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="openspcoop-sorgente-dati"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="property" type="{http://www.openspcoop2.org/core/config}Property" minOccurs="0" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="nome-jndi" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="tipo-database" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "openspcoop-sorgente-dati", 
  propOrder = {
  	"property"
  }
)

@XmlRootElement(name = "openspcoop-sorgente-dati")

public class OpenspcoopSorgenteDati extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public OpenspcoopSorgenteDati() {
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

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getNomeJndi() {
    return this.nomeJndi;
  }

  public void setNomeJndi(java.lang.String nomeJndi) {
    this.nomeJndi = nomeJndi;
  }

  public java.lang.String getTipoDatabase() {
    return this.tipoDatabase;
  }

  public void setTipoDatabase(java.lang.String tipoDatabase) {
    this.tipoDatabase = tipoDatabase;
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

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome-jndi",required=true)
  protected java.lang.String nomeJndi;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo-database",required=false)
  protected java.lang.String tipoDatabase;

}
