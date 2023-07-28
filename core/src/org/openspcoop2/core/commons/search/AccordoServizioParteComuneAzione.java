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
package org.openspcoop2.core.commons.search;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;


/** <p>Java class for accordo-servizio-parte-comune-azione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accordo-servizio-parte-comune-azione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="id-accordo-servizio-parte-comune" type="{http://www.openspcoop2.org/core/commons/search}id-accordo-servizio-parte-comune" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "accordo-servizio-parte-comune-azione", 
  propOrder = {
  	"nome",
  	"idAccordoServizioParteComune"
  }
)

@XmlRootElement(name = "accordo-servizio-parte-comune-azione")

public class AccordoServizioParteComuneAzione extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public AccordoServizioParteComuneAzione() {
    super();
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public IdAccordoServizioParteComune getIdAccordoServizioParteComune() {
    return this.idAccordoServizioParteComune;
  }

  public void setIdAccordoServizioParteComune(IdAccordoServizioParteComune idAccordoServizioParteComune) {
    this.idAccordoServizioParteComune = idAccordoServizioParteComune;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.commons.search.model.AccordoServizioParteComuneAzioneModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione.modelStaticInstance==null){
  			org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione.modelStaticInstance = new org.openspcoop2.core.commons.search.model.AccordoServizioParteComuneAzioneModel();
	  }
  }
  public static org.openspcoop2.core.commons.search.model.AccordoServizioParteComuneAzioneModel model(){
	  if(org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione.modelStaticInstance;
  }


  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlElement(name="nome",required=true,nillable=false)
  protected java.lang.String nome;

  @XmlElement(name="id-accordo-servizio-parte-comune",required=true,nillable=false)
  protected IdAccordoServizioParteComune idAccordoServizioParteComune;

}
