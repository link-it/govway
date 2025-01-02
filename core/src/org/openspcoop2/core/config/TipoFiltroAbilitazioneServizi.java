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
package org.openspcoop2.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for tipo-filtro-abilitazione-servizi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tipo-filtro-abilitazione-servizi"&gt;
 * 		&lt;attribute name="tipo-soggetto-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="soggetto-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="identificativo-porta-fruitore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="tipo-soggetto-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="soggetto-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="identificativo-porta-erogatore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="tipo-servizio" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="servizio" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="versione-servizio" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" use="optional"/&gt;
 * 		&lt;attribute name="azione" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
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

public class TipoFiltroAbilitazioneServizi extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public TipoFiltroAbilitazioneServizi() {
    super();
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

  public java.lang.Integer getVersioneServizio() {
    return this.versioneServizio;
  }

  public void setVersioneServizio(java.lang.Integer versioneServizio) {
    this.versioneServizio = versioneServizio;
  }

  public java.lang.String getAzione() {
    return this.azione;
  }

  public void setAzione(java.lang.String azione) {
    this.azione = azione;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo-soggetto-fruitore",required=false)
  protected java.lang.String tipoSoggettoFruitore;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="soggetto-fruitore",required=false)
  protected java.lang.String soggettoFruitore;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="identificativo-porta-fruitore",required=false)
  protected java.lang.String identificativoPortaFruitore;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo-soggetto-erogatore",required=false)
  protected java.lang.String tipoSoggettoErogatore;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="soggetto-erogatore",required=false)
  protected java.lang.String soggettoErogatore;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="identificativo-porta-erogatore",required=false)
  protected java.lang.String identificativoPortaErogatore;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="tipo-servizio",required=false)
  protected java.lang.String tipoServizio;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="servizio",required=false)
  protected java.lang.String servizio;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="unsignedInt")
  @XmlAttribute(name="versione-servizio",required=false)
  protected java.lang.Integer versioneServizio;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="azione",required=false)
  protected java.lang.String azione;

}
