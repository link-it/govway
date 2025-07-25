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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/** <p>Java class for autorizzazione-ruoli complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="autorizzazione-ruoli"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="ruolo" type="{http://www.openspcoop2.org/core/config}ruolo" minOccurs="1" maxOccurs="unbounded"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="match" type="{http://www.openspcoop2.org/core/config}RuoloTipoMatch" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "autorizzazione-ruoli", 
  propOrder = {
  	"ruolo"
  }
)

@XmlRootElement(name = "autorizzazione-ruoli")

public class AutorizzazioneRuoli extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public AutorizzazioneRuoli() {
    super();
  }

  public void addRuolo(Ruolo ruolo) {
    this.ruolo.add(ruolo);
  }

  public Ruolo getRuolo(int index) {
    return this.ruolo.get( index );
  }

  public Ruolo removeRuolo(int index) {
    return this.ruolo.remove( index );
  }

  public List<Ruolo> getRuoloList() {
    return this.ruolo;
  }

  public void setRuoloList(List<Ruolo> ruolo) {
    this.ruolo=ruolo;
  }

  public int sizeRuoloList() {
    return this.ruolo.size();
  }

  public void setMatchRawEnumValue(String value) {
    this.match = (RuoloTipoMatch) RuoloTipoMatch.toEnumConstantFromString(value);
  }

  public String getMatchRawEnumValue() {
    if(this.match == null){
    	return null;
    }else{
    	return this.match.toString();
    }
  }

  public org.openspcoop2.core.config.constants.RuoloTipoMatch getMatch() {
    return this.match;
  }

  public void setMatch(org.openspcoop2.core.config.constants.RuoloTipoMatch match) {
    this.match = match;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="ruolo",required=true,nillable=false)
  private List<Ruolo> ruolo = new ArrayList<>();

  /**
   * Use method getRuoloList
   * @return List&lt;Ruolo&gt;
  */
  public List<Ruolo> getRuolo() {
  	return this.getRuoloList();
  }

  /**
   * Use method setRuoloList
   * @param ruolo List&lt;Ruolo&gt;
  */
  public void setRuolo(List<Ruolo> ruolo) {
  	this.setRuoloList(ruolo);
  }

  /**
   * Use method sizeRuoloList
   * @return lunghezza della lista
  */
  public int sizeRuolo() {
  	return this.sizeRuoloList();
  }

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String matchRawEnumValue;

  @XmlAttribute(name="match",required=false)
  protected RuoloTipoMatch match;

}
