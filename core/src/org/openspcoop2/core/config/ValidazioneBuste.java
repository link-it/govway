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
package org.openspcoop2.core.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.ValidazioneBusteTipoControllo;
import java.io.Serializable;


/** <p>Java class for validazione-buste complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="validazione-buste"&gt;
 * 		&lt;attribute name="stato" type="{http://www.openspcoop2.org/core/config}StatoFunzionalitaConWarning" use="optional" default="abilitato"/&gt;
 * 		&lt;attribute name="controllo" type="{http://www.openspcoop2.org/core/config}ValidazioneBusteTipoControllo" use="optional" default="normale"/&gt;
 * 		&lt;attribute name="profiloCollaborazione" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="manifestAttachments" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "validazione-buste")

@XmlRootElement(name = "validazione-buste")

public class ValidazioneBuste extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public ValidazioneBuste() {
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

  public void set_value_stato(String value) {
    this.stato = (StatoFunzionalitaConWarning) StatoFunzionalitaConWarning.toEnumConstantFromString(value);
  }

  public String get_value_stato() {
    if(this.stato == null){
    	return null;
    }else{
    	return this.stato.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning getStato() {
    return this.stato;
  }

  public void setStato(org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning stato) {
    this.stato = stato;
  }

  public void set_value_controllo(String value) {
    this.controllo = (ValidazioneBusteTipoControllo) ValidazioneBusteTipoControllo.toEnumConstantFromString(value);
  }

  public String get_value_controllo() {
    if(this.controllo == null){
    	return null;
    }else{
    	return this.controllo.toString();
    }
  }

  public org.openspcoop2.core.config.constants.ValidazioneBusteTipoControllo getControllo() {
    return this.controllo;
  }

  public void setControllo(org.openspcoop2.core.config.constants.ValidazioneBusteTipoControllo controllo) {
    this.controllo = controllo;
  }

  public void set_value_profiloCollaborazione(String value) {
    this.profiloCollaborazione = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_profiloCollaborazione() {
    if(this.profiloCollaborazione == null){
    	return null;
    }else{
    	return this.profiloCollaborazione.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getProfiloCollaborazione() {
    return this.profiloCollaborazione;
  }

  public void setProfiloCollaborazione(org.openspcoop2.core.config.constants.StatoFunzionalita profiloCollaborazione) {
    this.profiloCollaborazione = profiloCollaborazione;
  }

  public void set_value_manifestAttachments(String value) {
    this.manifestAttachments = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_manifestAttachments() {
    if(this.manifestAttachments == null){
    	return null;
    }else{
    	return this.manifestAttachments.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getManifestAttachments() {
    return this.manifestAttachments;
  }

  public void setManifestAttachments(org.openspcoop2.core.config.constants.StatoFunzionalita manifestAttachments) {
    this.manifestAttachments = manifestAttachments;
  }

  private static final long serialVersionUID = 1L;

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_stato;

  @XmlAttribute(name="stato",required=false)
  protected StatoFunzionalitaConWarning stato = (StatoFunzionalitaConWarning) StatoFunzionalitaConWarning.toEnumConstantFromString("abilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_controllo;

  @XmlAttribute(name="controllo",required=false)
  protected ValidazioneBusteTipoControllo controllo = (ValidazioneBusteTipoControllo) ValidazioneBusteTipoControllo.toEnumConstantFromString("normale");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_profiloCollaborazione;

  @XmlAttribute(name="profiloCollaborazione",required=false)
  protected StatoFunzionalita profiloCollaborazione = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_manifestAttachments;

  @XmlAttribute(name="manifestAttachments",required=false)
  protected StatoFunzionalita manifestAttachments = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

}
