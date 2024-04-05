/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.monitor;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for openspcoop2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="openspcoop2"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="stato-pdd" type="{http://www.openspcoop2.org/pdd/monitor}stato-pdd" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="messaggio" type="{http://www.openspcoop2.org/pdd/monitor}messaggio" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "openspcoop2", 
  propOrder = {
  	"statoPdd",
  	"messaggio"
  }
)

@XmlRootElement(name = "openspcoop2")

public class Openspcoop2 extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Openspcoop2() {
    super();
  }

  public StatoPdd getStatoPdd() {
    return this.statoPdd;
  }

  public void setStatoPdd(StatoPdd statoPdd) {
    this.statoPdd = statoPdd;
  }

  public Messaggio getMessaggio() {
    return this.messaggio;
  }

  public void setMessaggio(Messaggio messaggio) {
    this.messaggio = messaggio;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="stato-pdd",required=true,nillable=false)
  protected StatoPdd statoPdd;

  @XmlElement(name="messaggio",required=true,nillable=false)
  protected Messaggio messaggio;

}
