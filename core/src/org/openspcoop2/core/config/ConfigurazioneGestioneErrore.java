/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for configurazione-gestione-errore complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-gestione-errore"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="componente-cooperazione" type="{http://www.openspcoop2.org/core/config}gestione-errore" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="componente-integrazione" type="{http://www.openspcoop2.org/core/config}gestione-errore" minOccurs="0" maxOccurs="1"/&gt;
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
@XmlType(name = "configurazione-gestione-errore", 
  propOrder = {
  	"componenteCooperazione",
  	"componenteIntegrazione"
  }
)

@XmlRootElement(name = "configurazione-gestione-errore")

public class ConfigurazioneGestioneErrore extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ConfigurazioneGestioneErrore() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return Long.valueOf(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=Long.valueOf(-1);
  }

  public GestioneErrore getComponenteCooperazione() {
    return this.componenteCooperazione;
  }

  public void setComponenteCooperazione(GestioneErrore componenteCooperazione) {
    this.componenteCooperazione = componenteCooperazione;
  }

  public GestioneErrore getComponenteIntegrazione() {
    return this.componenteIntegrazione;
  }

  public void setComponenteIntegrazione(GestioneErrore componenteIntegrazione) {
    this.componenteIntegrazione = componenteIntegrazione;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="componente-cooperazione",required=false,nillable=false)
  protected GestioneErrore componenteCooperazione;

  @XmlElement(name="componente-integrazione",required=false,nillable=false)
  protected GestioneErrore componenteIntegrazione;

}
