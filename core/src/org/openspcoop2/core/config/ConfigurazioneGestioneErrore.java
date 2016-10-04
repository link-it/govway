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
 * &lt;complexType name="configurazione-gestione-errore">
 * 		&lt;sequence>
 * 			&lt;element name="componente-cooperazione" type="{http://www.openspcoop2.org/core/config}gestione-errore" minOccurs="0" maxOccurs="1"/>
 * 			&lt;element name="componente-integrazione" type="{http://www.openspcoop2.org/core/config}gestione-errore" minOccurs="0" maxOccurs="1"/>
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
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
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
