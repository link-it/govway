/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

public class TrasformazioneRegolaParametro extends org.openspcoop2.utils.beans.BaseBean implements Serializable , Cloneable {
  public TrasformazioneRegolaParametro() {
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

  public void set_value_conversioneTipo(String value) {
    this.conversioneTipo = (TrasformazioneRegolaParametroTipoAzione) TrasformazioneRegolaParametroTipoAzione.toEnumConstantFromString(value);
  }

  public String get_value_conversioneTipo() {
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

  public void set_value_identificazioneFallita(String value) {
    this.identificazioneFallita = (TrasformazioneIdentificazioneRisorsaFallita) TrasformazioneIdentificazioneRisorsaFallita.toEnumConstantFromString(value);
  }

  public String get_value_identificazioneFallita() {
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

  @XmlTransient
  private Long id;



  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_conversioneTipo;

  @XmlAttribute(name="conversione-tipo",required=true)
  protected TrasformazioneRegolaParametroTipoAzione conversioneTipo;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="nome",required=true)
  protected java.lang.String nome;

  @javax.xml.bind.annotation.XmlSchemaType(name="string")
  @XmlAttribute(name="valore",required=false)
  protected java.lang.String valore;

  @javax.xml.bind.annotation.XmlTransient
  protected java.lang.String _value_identificazioneFallita;

  @XmlAttribute(name="identificazione-fallita",required=false)
  protected TrasformazioneIdentificazioneRisorsaFallita identificazioneFallita;

}
