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
package org.openspcoop2.core.plugins;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for configurazione-servizio-azione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurazione-servizio-azione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="azione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="id-configurazione-servizio" type="{http://www.openspcoop2.org/core/plugins}id-configurazione-servizio" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "configurazione-servizio-azione", 
  propOrder = {
  	"azione",
  	"idConfigurazioneServizio"
  }
)

@XmlRootElement(name = "configurazione-servizio-azione")

public class ConfigurazioneServizioAzione extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ConfigurazioneServizioAzione() {
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

  public java.lang.String getAzione() {
    return this.azione;
  }

  public void setAzione(java.lang.String azione) {
    this.azione = azione;
  }

  public IdConfigurazioneServizio getIdConfigurazioneServizio() {
    return this.idConfigurazioneServizio;
  }

  public void setIdConfigurazioneServizio(IdConfigurazioneServizio idConfigurazioneServizio) {
    this.idConfigurazioneServizio = idConfigurazioneServizio;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;

  private static org.openspcoop2.core.plugins.model.ConfigurazioneServizioAzioneModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.plugins.ConfigurazioneServizioAzione.modelStaticInstance==null){
  			org.openspcoop2.core.plugins.ConfigurazioneServizioAzione.modelStaticInstance = new org.openspcoop2.core.plugins.model.ConfigurazioneServizioAzioneModel();
	  }
  }
  public static org.openspcoop2.core.plugins.model.ConfigurazioneServizioAzioneModel model(){
	  if(org.openspcoop2.core.plugins.ConfigurazioneServizioAzione.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.plugins.ConfigurazioneServizioAzione.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="azione",required=true,nillable=false)
  protected java.lang.String azione;

  @XmlElement(name="id-configurazione-servizio",required=true,nillable=false)
  protected IdConfigurazioneServizio idConfigurazioneServizio;

}
