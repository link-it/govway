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
package org.openspcoop2.core.transazioni;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for dump-contenuto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dump-contenuto"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="valore" type="{http://www.openspcoop2.org/core/transazioni}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="valore-as-bytes" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="dump-timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "dump-contenuto", 
  propOrder = {
  	"nome",
  	"valore",
  	"valoreAsBytes",
  	"dumpTimestamp"
  }
)

@XmlRootElement(name = "dump-contenuto")

public class DumpContenuto extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public DumpContenuto() {
    super();
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getValore() {
    return this.valore;
  }

  public void setValore(java.lang.String valore) {
    this.valore = valore;
  }

  public byte[] getValoreAsBytes() {
    return this.valoreAsBytes;
  }

  public void setValoreAsBytes(byte[] valoreAsBytes) {
    this.valoreAsBytes = valoreAsBytes;
  }

  public java.util.Date getDumpTimestamp() {
    return this.dumpTimestamp;
  }

  public void setDumpTimestamp(java.util.Date dumpTimestamp) {
    this.dumpTimestamp = dumpTimestamp;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=true,nillable=false)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="valore",required=true,nillable=false)
  protected java.lang.String valore;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="base64Binary")
  @XmlElement(name="valore-as-bytes",required=false,nillable=false)
  protected byte[] valoreAsBytes;

  @jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @jakarta.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlElement(name="dump-timestamp",required=true,nillable=false,type=java.lang.String.class)
  protected java.util.Date dumpTimestamp;

}
