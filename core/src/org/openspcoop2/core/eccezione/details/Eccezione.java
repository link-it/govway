/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.core.eccezione.details;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.eccezione.details.constants.TipoEccezione;
import java.io.Serializable;


/** <p>Java class for eccezione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eccezione">
 * 		&lt;attribute name="codice" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="descrizione" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * 		&lt;attribute name="rilevanza" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="contesto-codifica" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="tipo" type="{http://www.openspcoop2.org/core/eccezione/details}TipoEccezione" use="required"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eccezione")

@XmlRootElement(name = "eccezione")

public class Eccezione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public Eccezione() {
  }

  public java.lang.String getCodice() {
    return this.codice;
  }

  public void setCodice(java.lang.String codice) {
    this.codice = codice;
  }

  public java.lang.String getDescrizione() {
    return this.descrizione;
  }

  public void setDescrizione(java.lang.String descrizione) {
    this.descrizione = descrizione;
  }

  public java.lang.String getRilevanza() {
    return this.rilevanza;
  }

  public void setRilevanza(java.lang.String rilevanza) {
    this.rilevanza = rilevanza;
  }

  public java.lang.String getContestoCodifica() {
    return this.contestoCodifica;
  }

  public void setContestoCodifica(java.lang.String contestoCodifica) {
    this.contestoCodifica = contestoCodifica;
  }

  public void set_value_tipo(String value) {
    this.tipo = (TipoEccezione) TipoEccezione.toEnumConstantFromString(value);
  }

  public String get_value_tipo() {
    if(this.tipo == null){
    	return null;
    }else{
    	return this.tipo.toString();
    }
  }

  public org.openspcoop2.core.eccezione.details.constants.TipoEccezione getTipo() {
    return this.tipo;
  }

  public void setTipo(org.openspcoop2.core.eccezione.details.constants.TipoEccezione tipo) {
    this.tipo = tipo;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="codice",required=true)
  protected java.lang.String codice;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="descrizione",required=true)
  protected java.lang.String descrizione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="rilevanza",required=false)
  protected java.lang.String rilevanza;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="contesto-codifica",required=false)
  protected java.lang.String contestoCodifica;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipo;

  @XmlAttribute(name="tipo",required=true)
  protected TipoEccezione tipo;

}
