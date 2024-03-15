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
package org.openspcoop2.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for tracciamento-configurazione-filetrace complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tracciamento-configurazione-filetrace"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="dump-in" type="{http://www.openspcoop2.org/core/config}tracciamento-configurazione-filetrace-connector" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="dump-out" type="{http://www.openspcoop2.org/core/config}tracciamento-configurazione-filetrace-connector" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="config" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tracciamento-configurazione-filetrace", 
  propOrder = {
  	"dumpIn",
  	"dumpOut"
  }
)

@XmlRootElement(name = "tracciamento-configurazione-filetrace")

public class TracciamentoConfigurazioneFiletrace extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public TracciamentoConfigurazioneFiletrace() {
    super();
  }

  public TracciamentoConfigurazioneFiletraceConnector getDumpIn() {
    return this.dumpIn;
  }

  public void setDumpIn(TracciamentoConfigurazioneFiletraceConnector dumpIn) {
    this.dumpIn = dumpIn;
  }

  public TracciamentoConfigurazioneFiletraceConnector getDumpOut() {
    return this.dumpOut;
  }

  public void setDumpOut(TracciamentoConfigurazioneFiletraceConnector dumpOut) {
    this.dumpOut = dumpOut;
  }

  public java.lang.String getConfig() {
    return this.config;
  }

  public void setConfig(java.lang.String config) {
    this.config = config;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="dump-in",required=false,nillable=false)
  protected TracciamentoConfigurazioneFiletraceConnector dumpIn;

  @XmlElement(name="dump-out",required=false,nillable=false)
  protected TracciamentoConfigurazioneFiletraceConnector dumpOut;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="config",required=false)
  protected java.lang.String config;

}
