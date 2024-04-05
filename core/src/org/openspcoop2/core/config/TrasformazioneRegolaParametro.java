/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
import org.openspcoop2.core.config.constants.TrasformazioneIdentificazioneRisorsaFallita;
import org.openspcoop2.core.config.constants.TrasformazioneRegolaParametroTipoAzione;
import java.io.Serializable;


/** <p>Java class for trasformazione-regola-parametro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="trasformazione-regola-parametro"&gt;
 * 		&lt;attribute name="conversione-tipo" type="{http://www.openspcoop2.org/core/config}TrasformazioneRegolaParametroTipoAzione" use="required"/&gt;
 * 		&lt;attribute name="nome" type="{http://www.w3.org/2001/XMLSchema}string" use="required"/&gt;
 * 		&lt;attribute name="valore" type="{http://www.w3.org/2001/XMLSchema}string" use="optional"/&gt;
 * 		&lt;attribute name="identificazione-fallita" type="{http://www.openspcoop2.org/core/config}TrasformazioneIdentificazioneRisorsaFallita" use="optional"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * @version $Rev$, $Date$
 * 
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "trasformazione-regola-parametro")

@XmlRootElement(name = "trasformazione-regola-parametro")

public class TrasformazioneRegolaParametro extends org.openspcoop2.utils.beans.BaseBeanWithId implements Serializable , Cloneable {
  public TrasformazioneRegolaParametro() {
    super();
  }

  public void setConversioneTipoRawEnumValue(String value) {
    this.conversioneTipo = (TrasformazioneRegolaParametroTipoAzione) TrasformazioneRegolaParametroTipoAzione.toEnumConstantFromString(value);
  }

  public String getConversioneTipoRawEnumValue() {
    if(this.conversioneTipo == null){
    	return null;
    }else{
    	return this.conversioneTipo.toString();
    }
  }

  public org.openspcoop2.core.config.constants.TrasformazioneRegolaParametroTipoAzione getConversioneTipo() {
    return this.conversioneTipo;
  }

  public void setConversioneTipo(org.openspcoop2.core.config.constants.TrasformazioneRegolaParametroTipoAzione conversioneTipo) {
    this.conversioneTipo = conversioneTipo;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  public java.lang.String getValore() {
    return this.valore;
  }

  public void setValore(java.lang.String valore) {
    this.valore = valore;
  }

  public void setIdentificazioneFallitaRawEnumValue(String value) {
    this.identificazioneFallita = (TrasformazioneIdentificazioneRisorsaFallita) TrasformazioneIdentificazioneRisorsaFallita.toEnumConstantFromString(value);
  }

  public String getIdentificazioneFallitaRawEnumValue() {
    if(this.identificazioneFallita == null){
    	return null;
    }else{
    	return this.identificazioneFallita.toString();
    }
  }

  public org.openspcoop2.core.config.constants.TrasformazioneIdentificazioneRisorsaFallita getIdentificazioneFallita() {
    return this.identificazioneFallita;
  }

  public void setIdentificazioneFallita(org.openspcoop2.core.config.constants.TrasformazioneIdentificazioneRisorsaFallita identificazioneFallita) {
    this.identificazioneFallita = identificazioneFallita;
  }

  private static final long serialVersionUID = 1L;



  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String conversioneTipoRawEnumValue;

  @XmlAttribute(name="conversione-tipo",required=true)
  protected TrasformazioneRegolaParametroTipoAzione conversioneTipo;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="valore",required=false)
  protected java.lang.String valore;

  @jakarta.xml.bind.annotation.XmlTransient
  protected java.lang.String identificazioneFallitaRawEnumValue;

  @XmlAttribute(name="identificazione-fallita",required=false)
  protected TrasformazioneIdentificazioneRisorsaFallita identificazioneFallita;

}
