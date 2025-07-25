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
package org.openspcoop2.core.controllo_traffico;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for configurazione-rate-limiting-proprieta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-rate-limiting-proprieta"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="valore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "configurazione-rate-limiting-proprieta", 
  propOrder = {
  	"nome",
  	"valore"
  }
)

@XmlRootElement(name = "configurazione-rate-limiting-proprieta")

public class ConfigurazioneRateLimitingProprieta extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public ConfigurazioneRateLimitingProprieta() {
    super();
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getValore() {
    return this.valore;
  }

  public void setValore(java.lang.String valore) {
    this.valore = valore;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.controllo_traffico.model.ConfigurazioneRateLimitingProprietaModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimitingProprieta.modelStaticInstance==null){
  			org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimitingProprieta.modelStaticInstance = new org.openspcoop2.core.controllo_traffico.model.ConfigurazioneRateLimitingProprietaModel();
	  }
  }
  public static org.openspcoop2.core.controllo_traffico.model.ConfigurazioneRateLimitingProprietaModel model(){
	  if(org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimitingProprieta.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimitingProprieta.modelStaticInstance;
  }


  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=true,nillable=false)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="valore",required=true,nillable=false)
  protected java.lang.String valore;

}
