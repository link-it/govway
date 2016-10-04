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
package org.openspcoop2.protocol.manifest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for registroServizi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="registroServizi">
 * 		&lt;sequence>
 * 			&lt;element name="soggetti" type="{http://www.openspcoop2.org/protocol/manifest}soggetti" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="servizi" type="{http://www.openspcoop2.org/protocol/manifest}servizi" minOccurs="1" maxOccurs="1"/>
 * 			&lt;element name="versioni" type="{http://www.openspcoop2.org/protocol/manifest}versioni" minOccurs="1" maxOccurs="1"/>
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
@XmlType(name = "registroServizi", 
  propOrder = {
  	"soggetti",
  	"servizi",
  	"versioni"
  }
)

@XmlRootElement(name = "registroServizi")

public class RegistroServizi extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public RegistroServizi() {
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

  public Soggetti getSoggetti() {
    return this.soggetti;
  }

  public void setSoggetti(Soggetti soggetti) {
    this.soggetti = soggetti;
  }

  public Servizi getServizi() {
    return this.servizi;
  }

  public void setServizi(Servizi servizi) {
    this.servizi = servizi;
  }

  public Versioni getVersioni() {
    return this.versioni;
  }

  public void setVersioni(Versioni versioni) {
    this.versioni = versioni;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @XmlElement(name="soggetti",required=true,nillable=false)
  protected Soggetti soggetti;

  @XmlElement(name="servizi",required=true,nillable=false)
  protected Servizi servizi;

  @XmlElement(name="versioni",required=true,nillable=false)
  protected Versioni versioni;

}
