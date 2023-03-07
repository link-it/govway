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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.RuoloTipologia;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;


/** <p>Java class for porta-applicativa-autorizzazione-token complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="porta-applicativa-autorizzazione-token"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="servizi-applicativi" type="{http://www.openspcoop2.org/core/config}porta-applicativa-autorizzazione-servizi-applicativi" minOccurs="0" maxOccurs="1"/&gt;
 * 			&lt;element name="ruoli" type="{http://www.openspcoop2.org/core/config}autorizzazione-ruoli" minOccurs="0" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="autorizzazione-applicativi" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="autorizzazione-ruoli" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="disabilitato"/&gt;
 * 		&lt;attribute name="tipologia-ruoli" type="{http://www.openspcoop2.org/core/config}RuoloTipologia" use="optional" default="qualsiasi"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "porta-applicativa-autorizzazione-token", 
  propOrder = {
  	"serviziApplicativi",
  	"ruoli"
  }
)

@XmlRootElement(name = "porta-applicativa-autorizzazione-token")

public class PortaApplicativaAutorizzazioneToken extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public PortaApplicativaAutorizzazioneToken() {
    super();
  }

  public PortaApplicativaAutorizzazioneServiziApplicativi getServiziApplicativi() {
    return this.serviziApplicativi;
  }

  public void setServiziApplicativi(PortaApplicativaAutorizzazioneServiziApplicativi serviziApplicativi) {
    this.serviziApplicativi = serviziApplicativi;
  }

  public AutorizzazioneRuoli getRuoli() {
    return this.ruoli;
  }

  public void setRuoli(AutorizzazioneRuoli ruoli) {
    this.ruoli = ruoli;
  }

  public void set_value_autorizzazioneApplicativi(String value) {
    this.autorizzazioneApplicativi = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_autorizzazioneApplicativi() {
    if(this.autorizzazioneApplicativi == null){
    	return null;
    }else{
    	return this.autorizzazioneApplicativi.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getAutorizzazioneApplicativi() {
    return this.autorizzazioneApplicativi;
  }

  public void setAutorizzazioneApplicativi(org.openspcoop2.core.config.constants.StatoFunzionalita autorizzazioneApplicativi) {
    this.autorizzazioneApplicativi = autorizzazioneApplicativi;
  }

  public void set_value_autorizzazioneRuoli(String value) {
    this.autorizzazioneRuoli = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_autorizzazioneRuoli() {
    if(this.autorizzazioneRuoli == null){
    	return null;
    }else{
    	return this.autorizzazioneRuoli.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getAutorizzazioneRuoli() {
    return this.autorizzazioneRuoli;
  }

  public void setAutorizzazioneRuoli(org.openspcoop2.core.config.constants.StatoFunzionalita autorizzazioneRuoli) {
    this.autorizzazioneRuoli = autorizzazioneRuoli;
  }

  public void set_value_tipologiaRuoli(String value) {
    this.tipologiaRuoli = (RuoloTipologia) RuoloTipologia.toEnumConstantFromString(value);
  }

  public String get_value_tipologiaRuoli() {
    if(this.tipologiaRuoli == null){
    	return null;
    }else{
    	return this.tipologiaRuoli.toString();
    }
  }

  public org.openspcoop2.core.config.constants.RuoloTipologia getTipologiaRuoli() {
    return this.tipologiaRuoli;
  }

  public void setTipologiaRuoli(org.openspcoop2.core.config.constants.RuoloTipologia tipologiaRuoli) {
    this.tipologiaRuoli = tipologiaRuoli;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="servizi-applicativi",required=false,nillable=false)
  protected PortaApplicativaAutorizzazioneServiziApplicativi serviziApplicativi;

  @XmlElement(name="ruoli",required=false,nillable=false)
  protected AutorizzazioneRuoli ruoli;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_autorizzazioneApplicativi;

  @XmlAttribute(name="autorizzazione-applicativi",required=false)
  protected StatoFunzionalita autorizzazioneApplicativi = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_autorizzazioneRuoli;

  @XmlAttribute(name="autorizzazione-ruoli",required=false)
  protected StatoFunzionalita autorizzazioneRuoli = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("disabilitato");

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_tipologiaRuoli;

  @XmlAttribute(name="tipologia-ruoli",required=false)
  protected RuoloTipologia tipologiaRuoli = (RuoloTipologia) RuoloTipologia.toEnumConstantFromString("qualsiasi");

}
