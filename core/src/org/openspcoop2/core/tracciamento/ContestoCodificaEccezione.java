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
package org.openspcoop2.core.tracciamento;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.tracciamento.constants.TipoCodificaEccezione;
import java.io.Serializable;


/** <p>Java class for ContestoCodificaEccezione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ContestoCodificaEccezione">
 * 		<xsd:simpleContent>
 * 			<xsd:extension base="{http://www.w3.org/2001/XMLSchema}string">
 * 				&lt;attribute name="tipo" type="{http://www.openspcoop2.org/core/tracciamento}TipoCodificaEccezione" use="required"/>
 * 			</xsd:extension>
 * 		</xsd:simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContestoCodificaEccezione")

@XmlRootElement(name = "ContestoCodificaEccezione")

public class ContestoCodificaEccezione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ContestoCodificaEccezione() {
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

  public String getBase() {
    if(this.base!=null && ("".equals(this.base)==false)){
		return this.base.trim();
	}else{
		return null;
	}

  }

  public void setBase(String base) {
    this.base=base;
  }

  public void set_value_tipo(String value) {
    this.tipo = (TipoCodificaEccezione) TipoCodificaEccezione.toEnumConstantFromString(value);
  }

  public String get_value_tipo() {
    if(this.tipo == null){
    	return null;
    }else{
    	return this.tipo.toString();
    }
  }

  public org.openspcoop2.core.tracciamento.constants.TipoCodificaEccezione getTipo() {
    return this.tipo;
  }

  public void setTipo(org.openspcoop2.core.tracciamento.constants.TipoCodificaEccezione tipo) {
    this.tipo = tipo;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @javax.xml.bind.annotation.XmlValue()
  public String base;

  @XmlTransient
  protected java.lang.String _value_tipo;

  @XmlAttribute(name="tipo",required=true)
  protected TipoCodificaEccezione tipo;

}
