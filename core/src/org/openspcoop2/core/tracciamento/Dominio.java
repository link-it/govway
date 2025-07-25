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
package org.openspcoop2.core.tracciamento;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.tracciamento.constants.TipoPdD;
import java.io.Serializable;


/** <p>Java class for dominio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dominio"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="identificativo-porta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="soggetto" type="{http://www.openspcoop2.org/core/tracciamento}dominio-soggetto" minOccurs="1" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="funzione" type="{http://www.openspcoop2.org/core/tracciamento}TipoPdD" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dominio", 
  propOrder = {
  	"identificativoPorta",
  	"soggetto"
  }
)

@XmlRootElement(name = "dominio")

public class Dominio extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Dominio() {
    super();
  }

  public java.lang.String getIdentificativoPorta() {
    return this.identificativoPorta;
  }

  public void setIdentificativoPorta(java.lang.String identificativoPorta) {
    this.identificativoPorta = identificativoPorta;
  }

  public DominioSoggetto getSoggetto() {
    return this.soggetto;
  }

  public void setSoggetto(DominioSoggetto soggetto) {
    this.soggetto = soggetto;
  }

  public void setFunzioneRawEnumValue(String value) {
    this.funzione = (TipoPdD) TipoPdD.toEnumConstantFromString(value);
  }

  public String getFunzioneRawEnumValue() {
    if(this.funzione == null){
    	return null;
    }else{
    	return this.funzione.toString();
    }
  }

  public org.openspcoop2.core.tracciamento.constants.TipoPdD getFunzione() {
    return this.funzione;
  }

  public void setFunzione(org.openspcoop2.core.tracciamento.constants.TipoPdD funzione) {
    this.funzione = funzione;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo-porta",required=true,nillable=false)
  protected java.lang.String identificativoPorta;

  @XmlElement(name="soggetto",required=true,nillable=false)
  protected DominioSoggetto soggetto;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String funzioneRawEnumValue;

  @XmlAttribute(name="funzione",required=false)
  protected TipoPdD funzione;

}
