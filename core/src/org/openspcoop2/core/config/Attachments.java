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
package org.openspcoop2.core.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;


/** <p>Java class for attachments complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="attachments"&gt;
 * 		&lt;attribute name="gestione-manifest" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "attachments")

@XmlRootElement(name = "attachments")

public class Attachments extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public Attachments() {
    super();
  }

  public void setGestioneManifestRawEnumValue(String value) {
    this.gestioneManifest = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String getGestioneManifestRawEnumValue() {
    if(this.gestioneManifest == null){
    	return null;
    }else{
    	return this.gestioneManifest.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getGestioneManifest() {
    return this.gestioneManifest;
  }

  public void setGestioneManifest(org.openspcoop2.core.config.constants.StatoFunzionalita gestioneManifest) {
    this.gestioneManifest = gestioneManifest;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String gestioneManifestRawEnumValue;

  @XmlAttribute(name="gestione-manifest",required=false)
  protected StatoFunzionalita gestioneManifest = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

}
