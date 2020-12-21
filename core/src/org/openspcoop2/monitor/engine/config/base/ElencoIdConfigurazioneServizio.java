/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.engine.config.base;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for elenco-id-configurazione-servizio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="elenco-id-configurazione-servizio"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="id-configurazione-servizio" type="{http://www.openspcoop2.org/monitor/engine/config/base}id-configurazione-servizio" minOccurs="0" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "elenco-id-configurazione-servizio", 
  propOrder = {
  	"idConfigurazioneServizio"
  }
)

@XmlRootElement(name = "elenco-id-configurazione-servizio")

public class ElencoIdConfigurazioneServizio extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ElencoIdConfigurazioneServizio() {
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

  public void addIdConfigurazioneServizio(IdConfigurazioneServizio idConfigurazioneServizio) {
    this.idConfigurazioneServizio.add(idConfigurazioneServizio);
  }

  public IdConfigurazioneServizio getIdConfigurazioneServizio(int index) {
    return this.idConfigurazioneServizio.get( index );
  }

  public IdConfigurazioneServizio removeIdConfigurazioneServizio(int index) {
    return this.idConfigurazioneServizio.remove( index );
  }

  public List<IdConfigurazioneServizio> getIdConfigurazioneServizioList() {
    return this.idConfigurazioneServizio;
  }

  public void setIdConfigurazioneServizioList(List<IdConfigurazioneServizio> idConfigurazioneServizio) {
    this.idConfigurazioneServizio=idConfigurazioneServizio;
  }

  public int sizeIdConfigurazioneServizioList() {
    return this.idConfigurazioneServizio.size();
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="id-configurazione-servizio",required=true,nillable=false)
  protected List<IdConfigurazioneServizio> idConfigurazioneServizio = new ArrayList<IdConfigurazioneServizio>();

  /**
   * @deprecated Use method getIdConfigurazioneServizioList
   * @return List&lt;IdConfigurazioneServizio&gt;
  */
  @Deprecated
  public List<IdConfigurazioneServizio> getIdConfigurazioneServizio() {
  	return this.idConfigurazioneServizio;
  }

  /**
   * @deprecated Use method setIdConfigurazioneServizioList
   * @param idConfigurazioneServizio List&lt;IdConfigurazioneServizio&gt;
  */
  @Deprecated
  public void setIdConfigurazioneServizio(List<IdConfigurazioneServizio> idConfigurazioneServizio) {
  	this.idConfigurazioneServizio=idConfigurazioneServizio;
  }

  /**
   * @deprecated Use method sizeIdConfigurazioneServizioList
   * @return lunghezza della lista
  */
  @Deprecated
  public int sizeIdConfigurazioneServizio() {
  	return this.idConfigurazioneServizio.size();
  }

}
