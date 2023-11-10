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
package org.openspcoop2.core.registry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for proprieta-oggetto complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="proprieta-oggetto"&gt;
 * 		&lt;attribute name="utente-richiedente" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="data-creazione" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="optional"/&gt;
 * 		&lt;attribute name="utente-ultima-modifica" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="data-ultima-modifica" type="{http://www.w3.org/2001/XMLSchema}dateTime" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "proprieta-oggetto")

@XmlRootElement(name = "proprieta-oggetto")

public class ProprietaOggetto extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ProprietaOggetto() {
    super();
  }

  public java.lang.String getUtenteRichiedente() {
    return this.utenteRichiedente;
  }

  public void setUtenteRichiedente(java.lang.String utenteRichiedente) {
    this.utenteRichiedente = utenteRichiedente;
  }

  public java.util.Date getDataCreazione() {
    return this.dataCreazione;
  }

  public void setDataCreazione(java.util.Date dataCreazione) {
    this.dataCreazione = dataCreazione;
  }

  public java.lang.String getUtenteUltimaModifica() {
    return this.utenteUltimaModifica;
  }

  public void setUtenteUltimaModifica(java.lang.String utenteUltimaModifica) {
    this.utenteUltimaModifica = utenteUltimaModifica;
  }

  public java.util.Date getDataUltimaModifica() {
    return this.dataUltimaModifica;
  }

  public void setDataUltimaModifica(java.util.Date dataUltimaModifica) {
    this.dataUltimaModifica = dataUltimaModifica;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="utente-richiedente",required=false)
  protected java.lang.String utenteRichiedente;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="data-creazione",required=false)
  protected java.util.Date dataCreazione;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="utente-ultima-modifica",required=false)
  protected java.lang.String utenteUltimaModifica;

  @javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(org.openspcoop2.utils.jaxb.DateTime2String.class)
  @javax.xml.bind.annotation.XmlSchemaType(name="dateTime")
  @XmlAttribute(name="data-ultima-modifica",required=false)
  protected java.util.Date dataUltimaModifica;

}
