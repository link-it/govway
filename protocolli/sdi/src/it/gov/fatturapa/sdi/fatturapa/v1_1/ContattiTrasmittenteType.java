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
package it.gov.fatturapa.sdi.fatturapa.v1_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for ContattiTrasmittenteType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ContattiTrasmittenteType">
 * 		&lt;sequence>
 * 			&lt;element name="Telefono" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}normalizedString" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="Email" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.1}string" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContattiTrasmittenteType", 
  propOrder = {
  	"telefono",
  	"email"
  }
)

@XmlRootElement(name = "ContattiTrasmittenteType")

public class ContattiTrasmittenteType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ContattiTrasmittenteType() {
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

  public java.lang.String getTelefono() {
    return this.telefono;
  }

  public void setTelefono(java.lang.String telefono) {
    this.telefono = telefono;
  }

  public java.lang.String getEmail() {
    return this.email;
  }

  public void setEmail(java.lang.String email) {
    this.email = email;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="Telefono",required=false,nillable=false)
  protected java.lang.String telefono;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="Email",required=false,nillable=false)
  protected java.lang.String email;

}
