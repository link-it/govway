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


/** <p>Java class for accordo-servizio-parte-comune-gruppo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="accordo-servizio-parte-comune-gruppo"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="id-gruppo" type="{http://www.openspcoop2.org/core/commons/search}id-gruppo" minOccurs="1" maxOccurs="1"/&gt;
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
@XmlType(name = "accordo-servizio-parte-comune-gruppo", 
  propOrder = {
  	"idGruppo",
  	"idAccordoServizioParteComune"
  }
)

@XmlRootElement(name = "accordo-servizio-parte-comune-gruppo")

public class AccordoServizioParteComuneGruppo extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public AccordoServizioParteComuneGruppo() {
    super();
  }

  public IdGruppo getIdGruppo() {
    return this.idGruppo;
  }

  public void setIdGruppo(IdGruppo idGruppo) {
    this.idGruppo = idGruppo;
  }

  public IdAccordoServizioParteComune getIdAccordoServizioParteComune() {
    return this.idAccordoServizioParteComune;
  }

  public void setIdAccordoServizioParteComune(IdAccordoServizioParteComune idAccordoServizioParteComune) {
    this.idAccordoServizioParteComune = idAccordoServizioParteComune;
  }

  private static final long serialVersionUID = 1L;

  private static org.openspcoop2.core.commons.search.model.AccordoServizioParteComuneGruppoModel modelStaticInstance = null;
  private static synchronized void initModelStaticInstance(){
	  if(org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo.modelStaticInstance==null){
  			org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo.modelStaticInstance = new org.openspcoop2.core.commons.search.model.AccordoServizioParteComuneGruppoModel();
	  }
  }
  public static org.openspcoop2.core.commons.search.model.AccordoServizioParteComuneGruppoModel model(){
	  if(org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo.modelStaticInstance==null){
	  		initModelStaticInstance();
	  }
	  return org.openspcoop2.core.commons.search.AccordoServizioParteComuneGruppo.modelStaticInstance;
  }


  @XmlElement(name="id-gruppo",required=true,nillable=false)
  protected IdGruppo idGruppo;

  @XmlElement(name="id-accordo-servizio-parte-comune",required=true,nillable=false)
  protected IdAccordoServizioParteComune idAccordoServizioParteComune;

}
