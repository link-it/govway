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
package org.openspcoop2.core.controllo_congestione;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for configurazione-generale complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-generale">
 * 		&lt;sequence>
 * 			&lt;element name="controllo-traffico" type="{http://www.openspcoop2.org/core/controllo_congestione}configurazione-controllo-traffico" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="tempi-risposta-fruizione" type="{http://www.openspcoop2.org/core/controllo_congestione}tempi-risposta-fruizione" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="tempi-risposta-erogazione" type="{http://www.openspcoop2.org/core/controllo_congestione}tempi-risposta-erogazione" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="rate-limiting" type="{http://www.openspcoop2.org/core/controllo_congestione}configurazione-rate-limiting" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="cache" type="{http://www.openspcoop2.org/core/controllo_congestione}cache" minOccurs="1" maxOccurs="1"/>
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
@XmlType(name = "configurazione-generale", 
  propOrder = {
  	"controlloTraffico",
  	"tempiRispostaFruizione",
  	"tempiRispostaErogazione",
  	"rateLimiting",
  	"cache"
  }
)

@XmlRootElement(name = "configurazione-generale")

public class ConfigurazioneGenerale extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ConfigurazioneGenerale() {
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

  public ConfigurazioneControlloTraffico getControlloTraffico() {
    return this.controlloTraffico;
  }

  public void setControlloTraffico(ConfigurazioneControlloTraffico controlloTraffico) {
    this.controlloTraffico = controlloTraffico;
  }

  public TempiRispostaFruizione getTempiRispostaFruizione() {
    return this.tempiRispostaFruizione;
  }

  public void setTempiRispostaFruizione(TempiRispostaFruizione tempiRispostaFruizione) {
    this.tempiRispostaFruizione = tempiRispostaFruizione;
  }

  public TempiRispostaErogazione getTempiRispostaErogazione() {
    return this.tempiRispostaErogazione;
  }

  public void setTempiRispostaErogazione(TempiRispostaErogazione tempiRispostaErogazione) {
    this.tempiRispostaErogazione = tempiRispostaErogazione;
  }

  public ConfigurazioneRateLimiting getRateLimiting() {
    return this.rateLimiting;
  }

  public void setRateLimiting(ConfigurazioneRateLimiting rateLimiting) {
    this.rateLimiting = rateLimiting;
  }

  public Cache getCache() {
    return this.cache;
  }

  public void setCache(Cache cache) {
    this.cache = cache;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.controllo_congestione.model.ConfigurazioneGeneraleModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.controllo_congestione.ConfigurazioneGenerale.modelStaticInstance==null){
  			org.openspcoop2.core.controllo_congestione.ConfigurazioneGenerale.modelStaticInstance = new org.openspcoop2.core.controllo_congestione.model.ConfigurazioneGeneraleModel();
	  }
  }
  public static org.openspcoop2.core.controllo_congestione.model.ConfigurazioneGeneraleModel model(){
	  if(org.openspcoop2.core.controllo_congestione.ConfigurazioneGenerale.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.controllo_congestione.ConfigurazioneGenerale.modelStaticInstance;
  }


  @XmlElement(name="controllo-traffico",required=true,nillable=false)
  protected ConfigurazioneControlloTraffico controlloTraffico;

  @XmlElement(name="tempi-risposta-fruizione",required=true,nillable=false)
  protected TempiRispostaFruizione tempiRispostaFruizione;

  @XmlElement(name="tempi-risposta-erogazione",required=true,nillable=false)
  protected TempiRispostaErogazione tempiRispostaErogazione;

  @XmlElement(name="rate-limiting",required=true,nillable=false)
  protected ConfigurazioneRateLimiting rateLimiting;

  @XmlElement(name="cache",required=true,nillable=false)
  protected Cache cache;

}
