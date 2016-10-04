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
package org.openspcoop2.core.registry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for documento complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="documento">
 * 		&lt;sequence>
 * 			&lt;element name="byte-contenuto" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="ruolo" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="tipo-proprietario-documento" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="id-proprietario-documento" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" use="optional"/>
 * 		&lt;attribute name="tipo" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="file" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="ora-registrazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "documento", 
  propOrder = {
  	"byteContenuto"
  }
)

@XmlRootElement(name = "documento")

public class Documento extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Documento() {
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

  public byte[] getByteContenuto() {
    return this.byteContenuto;
  }

  public void setByteContenuto(byte[] byteContenuto) {
    this.byteContenuto = byteContenuto;
  }

  public java.lang.String getRuolo() {
    return this.ruolo;
  }

  public void setRuolo(java.lang.String ruolo) {
    this.ruolo = ruolo;
  }

  public java.lang.String getTipoProprietarioDocumento() {
    return this.tipoProprietarioDocumento;
  }

  public void setTipoProprietarioDocumento(java.lang.String tipoProprietarioDocumento) {
    this.tipoProprietarioDocumento = tipoProprietarioDocumento;
  }

  public java.lang.Long getIdProprietarioDocumento() {
    return this.idProprietarioDocumento;
  }

  public void setIdProprietarioDocumento(java.lang.Long idProprietarioDocumento) {
    this.idProprietarioDocumento = idProprietarioDocumento;
  }

  public java.lang.String getTipo() {
    return this.tipo;
  }

  public void setTipo(java.lang.String tipo) {
    this.tipo = tipo;
  }

  public java.lang.String getFile() {
    return this.file;
  }

  public void setFile(java.lang.String file) {
    this.file = file;
  }

  public java.util.Date getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(java.util.Date oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlElement(name="byte-contenuto",required=false,nillable=false)
  protected byte[] byteContenuto;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="ruolo",required=false)
  protected java.lang.String ruolo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo-proprietario-documento",required=false)
  protected java.lang.String tipoProprietarioDocumento;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.Long idProprietarioDocumento;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo",required=true)
  protected java.lang.String tipo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="file",required=true)
  protected java.lang.String file;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="ora-registrazione",required=false)
  protected java.util.Date oraRegistrazione;

}
