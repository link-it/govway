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
package it.gov.fatturapa.sdi.fatturapa.v1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for AnagraficaType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AnagraficaType">
 * 		&lt;sequence>
 * 			&lt;element name="Denominazione" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="Nome" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="Cognome" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="Titolo" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}normalizedString" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="CodEORI" type="{http://www.fatturapa.gov.it/sdi/fatturapa/v1.0}string" minOccurs="0" maxOccurs="1"/>
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
@XmlType(name = "AnagraficaType", 
  propOrder = {
  	"denominazione",
  	"nome",
  	"cognome",
  	"titolo",
  	"codEORI"
  }
)

@XmlRootElement(name = "AnagraficaType")

public class AnagraficaType extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public AnagraficaType() {
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

  public java.lang.String getDenominazione() {
    return this.denominazione;
  }

  public void setDenominazione(java.lang.String denominazione) {
    this.denominazione = denominazione;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getCognome() {
    return this.cognome;
  }

  public void setCognome(java.lang.String cognome) {
    this.cognome = cognome;
  }

  public java.lang.String getTitolo() {
    return this.titolo;
  }

  public void setTitolo(java.lang.String titolo) {
    this.titolo = titolo;
  }

  public java.lang.String getCodEORI() {
    return this.codEORI;
  }

  public void setCodEORI(java.lang.String codEORI) {
    this.codEORI = codEORI;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="Denominazione",required=true,nillable=false)
  protected java.lang.String denominazione;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="Nome",required=true,nillable=false)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="Cognome",required=true,nillable=false)
  protected java.lang.String cognome;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(javax.xml.bind.annotation.adapters.NormalizedStringAdapter.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="normalizedString")
  @XmlElement(name="Titolo",required=false,nillable=false)
  protected java.lang.String titolo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="CodEORI",required=false,nillable=false)
  protected java.lang.String codEORI;

}
