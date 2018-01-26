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
package org.openspcoop2.core.integrazione;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for esito-richiesta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="esito-richiesta">
 * 		&lt;sequence>
 * 			&lt;element name="identificativo-messaggio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/>
 * 		&lt;/sequence>
 * 		&lt;attribute name="stato" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/>
 * &lt;/complexType>
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "esito-richiesta", 
  propOrder = {
  	"identificativoMessaggio"
  }
)

@XmlRootElement(name = "esito-richiesta")

public class EsitoRichiesta extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public EsitoRichiesta() {
  }

  public java.lang.String getIdentificativoMessaggio() {
    return this.identificativoMessaggio;
  }

  public void setIdentificativoMessaggio(java.lang.String identificativoMessaggio) {
    this.identificativoMessaggio = identificativoMessaggio;
  }

  public java.lang.String getStato() {
    return this.stato;
  }

  public void setStato(java.lang.String stato) {
    this.stato = stato;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.integrazione.model.EsitoRichiestaModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.integrazione.EsitoRichiesta.modelStaticInstance==null){
  			org.openspcoop2.core.integrazione.EsitoRichiesta.modelStaticInstance = new org.openspcoop2.core.integrazione.model.EsitoRichiestaModel();
	  }
  }
  public static org.openspcoop2.core.integrazione.model.EsitoRichiestaModel model(){
	  if(org.openspcoop2.core.integrazione.EsitoRichiesta.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.integrazione.EsitoRichiesta.modelStaticInstance;
  }


  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="identificativo-messaggio",required=true,nillable=false)
  protected java.lang.String identificativoMessaggio;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="stato",required=true)
  protected java.lang.String stato;

}
