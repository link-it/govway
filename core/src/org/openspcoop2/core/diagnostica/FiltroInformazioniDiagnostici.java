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
package org.openspcoop2.core.diagnostica;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for filtro-informazioni-diagnostici complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="filtro-informazioni-diagnostici">
 * 		&lt;sequence>
 * 			&lt;element name="codice" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="messaggio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="severita" type="{http://www.openspcoop2.org/core/diagnostica}positiveInteger" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="modulo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="identificativo-risposta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "filtro-informazioni-diagnostici", 
  propOrder = {
  	"codice",
  	"messaggio",
  	"severita",
  	"modulo",
  	"identificativoRisposta"
  }
)

@XmlRootElement(name = "filtro-informazioni-diagnostici")

public class FiltroInformazioniDiagnostici extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public FiltroInformazioniDiagnostici() {
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

  public java.lang.String getCodice() {
    return this.codice;
  }

  public void setCodice(java.lang.String codice) {
    this.codice = codice;
  }

  public java.lang.String getMessaggio() {
    return this.messaggio;
  }

  public void setMessaggio(java.lang.String messaggio) {
    this.messaggio = messaggio;
  }

  public java.lang.Integer getSeverita() {
    return this.severita;
  }

  public void setSeverita(java.lang.Integer severita) {
    this.severita = severita;
  }

  public java.lang.String getModulo() {
    return this.modulo;
  }

  public void setModulo(java.lang.String modulo) {
    this.modulo = modulo;
  }

  public java.lang.String getIdentificativoRisposta() {
    return this.identificativoRisposta;
  }

  public void setIdentificativoRisposta(java.lang.String identificativoRisposta) {
    this.identificativoRisposta = identificativoRisposta;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="codice",required=false,nillable=false)
  protected java.lang.String codice;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="messaggio",required=false,nillable=false)
  protected java.lang.String messaggio;

  @javax.xml.bind.annotation.XmlSchemaType(name="positiveInteger")
  @XmlElement(name="severita",required=false,nillable=false)
  protected java.lang.Integer severita;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="modulo",required=false,nillable=false)
  protected java.lang.String modulo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo-risposta",required=false,nillable=false)
  protected java.lang.String identificativoRisposta;

}
