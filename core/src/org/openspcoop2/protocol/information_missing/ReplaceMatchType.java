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
package org.openspcoop2.protocol.information_missing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for replaceMatchType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="replaceMatchType">
 * 		&lt;sequence>
 * 			&lt;element name="nome" type="{http://www.openspcoop2.org/protocol/information_missing}ReplaceMatchFieldType" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="tipo" type="{http://www.openspcoop2.org/protocol/information_missing}ReplaceMatchFieldType" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "replaceMatchType", 
  propOrder = {
  	"nome",
  	"tipo"
  }
)

@XmlRootElement(name = "replaceMatchType")

public class ReplaceMatchType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ReplaceMatchType() {
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

  public ReplaceMatchFieldType getNome() {
    return this.nome;
  }

  public void setNome(ReplaceMatchFieldType nome) {
    this.nome = nome;
  }

  public ReplaceMatchFieldType getTipo() {
    return this.tipo;
  }

  public void setTipo(ReplaceMatchFieldType tipo) {
    this.tipo = tipo;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="nome",required=false,nillable=false)
  protected ReplaceMatchFieldType nome;

  @XmlElement(name="tipo",required=false,nillable=false)
  protected ReplaceMatchFieldType tipo;

}
