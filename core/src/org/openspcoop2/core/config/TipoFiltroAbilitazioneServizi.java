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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for tipo-filtro-abilitazione-servizi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tipo-filtro-abilitazione-servizi">
 * 		&lt;attribute name="tipo-soggetto-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="soggetto-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="identificativo-porta-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="tipo-soggetto-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="soggetto-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="identificativo-porta-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="tipo-servizio" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="servizio" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * 		&lt;attribute name="azione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo-filtro-abilitazione-servizi")

@XmlRootElement(name = "tipo-filtro-abilitazione-servizi")

public class TipoFiltroAbilitazioneServizi extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public TipoFiltroAbilitazioneServizi() {
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

  public java.lang.String getTipoSoggettoFruitore() {
    return this.tipoSoggettoFruitore;
  }

  public void setTipoSoggettoFruitore(java.lang.String tipoSoggettoFruitore) {
    this.tipoSoggettoFruitore = tipoSoggettoFruitore;
  }

  public java.lang.String getSoggettoFruitore() {
    return this.soggettoFruitore;
  }

  public void setSoggettoFruitore(java.lang.String soggettoFruitore) {
    this.soggettoFruitore = soggettoFruitore;
  }

  public java.lang.String getIdentificativoPortaFruitore() {
    return this.identificativoPortaFruitore;
  }

  public void setIdentificativoPortaFruitore(java.lang.String identificativoPortaFruitore) {
    this.identificativoPortaFruitore = identificativoPortaFruitore;
  }

  public java.lang.String getTipoSoggettoErogatore() {
    return this.tipoSoggettoErogatore;
  }

  public void setTipoSoggettoErogatore(java.lang.String tipoSoggettoErogatore) {
    this.tipoSoggettoErogatore = tipoSoggettoErogatore;
  }

  public java.lang.String getSoggettoErogatore() {
    return this.soggettoErogatore;
  }

  public void setSoggettoErogatore(java.lang.String soggettoErogatore) {
    this.soggettoErogatore = soggettoErogatore;
  }

  public java.lang.String getIdentificativoPortaErogatore() {
    return this.identificativoPortaErogatore;
  }

  public void setIdentificativoPortaErogatore(java.lang.String identificativoPortaErogatore) {
    this.identificativoPortaErogatore = identificativoPortaErogatore;
  }

  public java.lang.String getTipoServizio() {
    return this.tipoServizio;
  }

  public void setTipoServizio(java.lang.String tipoServizio) {
    this.tipoServizio = tipoServizio;
  }

  public java.lang.String getServizio() {
    return this.servizio;
  }

  public void setServizio(java.lang.String servizio) {
    this.servizio = servizio;
  }

  public java.lang.String getAzione() {
    return this.azione;
  }

  public void setAzione(java.lang.String azione) {
    this.azione = azione;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo-soggetto-fruitore",required=false)
  protected java.lang.String tipoSoggettoFruitore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="soggetto-fruitore",required=false)
  protected java.lang.String soggettoFruitore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="identificativo-porta-fruitore",required=false)
  protected java.lang.String identificativoPortaFruitore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo-soggetto-erogatore",required=false)
  protected java.lang.String tipoSoggettoErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="soggetto-erogatore",required=false)
  protected java.lang.String soggettoErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="identificativo-porta-erogatore",required=false)
  protected java.lang.String identificativoPortaErogatore;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo-servizio",required=false)
  protected java.lang.String tipoServizio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="servizio",required=false)
  protected java.lang.String servizio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="azione",required=false)
  protected java.lang.String azione;

}
