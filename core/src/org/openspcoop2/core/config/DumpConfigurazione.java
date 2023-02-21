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
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import java.io.Serializable;


/** <p>Java class for dump-configurazione complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dump-configurazione"&gt;
 * 		&lt;sequence&gt;
 * 			&lt;element name="richiesta-ingresso" type="{http://www.openspcoop2.org/core/config}dump-configurazione-regola" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="richiesta-uscita" type="{http://www.openspcoop2.org/core/config}dump-configurazione-regola" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="risposta-ingresso" type="{http://www.openspcoop2.org/core/config}dump-configurazione-regola" minOccurs="1" maxOccurs="1"/&gt;
 * 			&lt;element name="risposta-uscita" type="{http://www.openspcoop2.org/core/config}dump-configurazione-regola" minOccurs="1" maxOccurs="1"/&gt;
 * 		&lt;/sequence&gt;
 * 		&lt;attribute name="realtime" type="{http://www.openspcoop2.org/core/config}StatoFunzionalita" use="optional" default="abilitato"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dump-configurazione", 
  propOrder = {
  	"richiestaIngresso",
  	"richiestaUscita",
  	"rispostaIngresso",
  	"rispostaUscita"
  }
)

@XmlRootElement(name = "dump-configurazione")

public class DumpConfigurazione extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public DumpConfigurazione() {
    super();
  }

  public DumpConfigurazioneRegola getRichiestaIngresso() {
    return this.richiestaIngresso;
  }

  public void setRichiestaIngresso(DumpConfigurazioneRegola richiestaIngresso) {
    this.richiestaIngresso = richiestaIngresso;
  }

  public DumpConfigurazioneRegola getRichiestaUscita() {
    return this.richiestaUscita;
  }

  public void setRichiestaUscita(DumpConfigurazioneRegola richiestaUscita) {
    this.richiestaUscita = richiestaUscita;
  }

  public DumpConfigurazioneRegola getRispostaIngresso() {
    return this.rispostaIngresso;
  }

  public void setRispostaIngresso(DumpConfigurazioneRegola rispostaIngresso) {
    this.rispostaIngresso = rispostaIngresso;
  }

  public DumpConfigurazioneRegola getRispostaUscita() {
    return this.rispostaUscita;
  }

  public void setRispostaUscita(DumpConfigurazioneRegola rispostaUscita) {
    this.rispostaUscita = rispostaUscita;
  }

  public void set_value_realtime(String value) {
    this.realtime = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString(value);
  }

  public String get_value_realtime() {
    if(this.realtime == null){
    	return null;
    }else{
    	return this.realtime.toString();
    }
  }

  public org.openspcoop2.core.config.constants.StatoFunzionalita getRealtime() {
    return this.realtime;
  }

  public void setRealtime(org.openspcoop2.core.config.constants.StatoFunzionalita realtime) {
    this.realtime = realtime;
  }

  private static final long serialVersionUID = 1L;



  @XmlElement(name="richiesta-ingresso",required=true,nillable=false)
  protected DumpConfigurazioneRegola richiestaIngresso;

  @XmlElement(name="richiesta-uscita",required=true,nillable=false)
  protected DumpConfigurazioneRegola richiestaUscita;

  @XmlElement(name="risposta-ingresso",required=true,nillable=false)
  protected DumpConfigurazioneRegola rispostaIngresso;

  @XmlElement(name="risposta-uscita",required=true,nillable=false)
  protected DumpConfigurazioneRegola rispostaUscita;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_realtime;

  @XmlAttribute(name="realtime",required=false)
  protected StatoFunzionalita realtime = (StatoFunzionalita) StatoFunzionalita.toEnumConstantFromString("abilitato");

}
