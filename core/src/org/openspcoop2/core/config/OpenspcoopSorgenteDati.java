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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for openspcoop-sorgente-dati complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="openspcoop-sorgente-dati">
 * 		&lt;sequence>
 * 			&lt;element name="property" type="{http://www.openspcoop2.org/core/config}Property" minOccurs="0" maxOccurs="unbounded"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="nome-jndi" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="tipo-database" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
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

public class OpenspcoopSorgenteDati extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public OpenspcoopSorgenteDati() {
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

  @XmlTransient
  private Long id;



  @XmlElement(name="property",required=true,nillable=false)
  protected List<Property> property = new ArrayList<Property>();

  /**
   * @deprecated Use method getPropertyList
   * @return List<Property>
  */
  @Deprecated
  public List<Property> getProperty() {
  	return this.property;
  }

  /**
   * @deprecated Use method setPropertyList
   * @param property List<Property>
  */
  @Deprecated
  public void setProperty(List<Property> property) {
  	this.property=property;
  }

  /**
   * @deprecated Use method sizePropertyList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeProperty() {
  	return this.property.size();
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
