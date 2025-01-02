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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaGestioneIdentificazioneFallita;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaRispostaIdentificazione;
import java.io.Serializable;


/** <p>Java class for correlazione-applicativa-risposta-elemento complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="correlazione-applicativa-risposta-elemento"&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="identificazione" type="{http://www.openspcoop2.org/core/config}CorrelazioneApplicativaRispostaIdentificazione" use="optional" default="contentBased"/&gt;
 * 		&lt;attribute name="pattern" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="identificazione-fallita" type="{http://www.openspcoop2.org/core/config}CorrelazioneApplicativaGestioneIdentificazioneFallita" use="optional" default="blocca"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "correlazione-applicativa-risposta-elemento")

@XmlRootElement(name = "correlazione-applicativa-risposta-elemento")

public class CorrelazioneApplicativaRispostaElemento extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public CorrelazioneApplicativaRispostaElemento() {
    super();
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public void setIdentificazioneRawEnumValue(String value) {
    this.identificazione = (CorrelazioneApplicativaRispostaIdentificazione) CorrelazioneApplicativaRispostaIdentificazione.toEnumConstantFromString(value);
  }

  public String getIdentificazioneRawEnumValue() {
    if(this.identificazione == null){
    	return null;
    }else{
    	return this.identificazione.toString();
    }
  }

  public org.openspcoop2.core.config.constants.CorrelazioneApplicativaRispostaIdentificazione getIdentificazione() {
    return this.identificazione;
  }

  public void setIdentificazione(org.openspcoop2.core.config.constants.CorrelazioneApplicativaRispostaIdentificazione identificazione) {
    this.identificazione = identificazione;
  }

  public java.lang.String getPattern() {
    return this.pattern;
  }

  public void setPattern(java.lang.String pattern) {
    this.pattern = pattern;
  }

  public void setIdentificazioneFallitaRawEnumValue(String value) {
    this.identificazioneFallita = (CorrelazioneApplicativaGestioneIdentificazioneFallita) CorrelazioneApplicativaGestioneIdentificazioneFallita.toEnumConstantFromString(value);
  }

  public String getIdentificazioneFallitaRawEnumValue() {
    if(this.identificazioneFallita == null){
    	return null;
    }else{
    	return this.identificazioneFallita.toString();
    }
  }

  public org.openspcoop2.core.config.constants.CorrelazioneApplicativaGestioneIdentificazioneFallita getIdentificazioneFallita() {
    return this.identificazioneFallita;
  }

  public void setIdentificazioneFallita(org.openspcoop2.core.config.constants.CorrelazioneApplicativaGestioneIdentificazioneFallita identificazioneFallita) {
    this.identificazioneFallita = identificazioneFallita;
  }

  private static final long serialVersionUID = 1L;



  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=false)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String identificazioneRawEnumValue;

  @XmlAttribute(name="identificazione",required=false)
  protected CorrelazioneApplicativaRispostaIdentificazione identificazione = (CorrelazioneApplicativaRispostaIdentificazione) CorrelazioneApplicativaRispostaIdentificazione.toEnumConstantFromString("contentBased");

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="pattern",required=false)
  protected java.lang.String pattern;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String identificazioneFallitaRawEnumValue;

  @XmlAttribute(name="identificazione-fallita",required=false)
  protected CorrelazioneApplicativaGestioneIdentificazioneFallita identificazioneFallita = (CorrelazioneApplicativaGestioneIdentificazioneFallita) CorrelazioneApplicativaGestioneIdentificazioneFallita.toEnumConstantFromString("blocca");

}
