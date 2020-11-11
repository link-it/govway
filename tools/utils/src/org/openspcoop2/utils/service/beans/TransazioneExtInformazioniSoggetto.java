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
package org.openspcoop2.utils.service.beans;

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

@XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name = "TransazioneExtInformazioniSoggetto", propOrder =
    { "tipo", "codice", "indirizzo"
})

@XmlRootElement(name="TransazioneExtInformazioniSoggetto")
public class TransazioneExtInformazioniSoggetto  {
  @XmlElement(name="tipo", required = true)
  
  @Schema(required = true, description = "")
  private String tipo = null;
  @XmlElement(name="codice", required = true)
  
  @Schema(required = true, description = "")
  private String codice = null;
  @XmlElement(name="indirizzo")
  
  @Schema(description = "")
  private String indirizzo = null;
 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @NotNull
  @Valid
 @Pattern(regexp="^[a-z]{2,20}$") @Size(max=20)  public String getTipo() {
    return this.tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public TransazioneExtInformazioniSoggetto tipo(String tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get codice
   * @return codice
  **/
  @JsonProperty("codice")
  @NotNull
  @Valid
  public String getCodice() {
    return this.codice;
  }

  public void setCodice(String codice) {
    this.codice = codice;
  }

  public TransazioneExtInformazioniSoggetto codice(String codice) {
    this.codice = codice;
    return this;
  }

 /**
   * Get indirizzo
   * @return indirizzo
  **/
  @JsonProperty("indirizzo")
  @Valid
  public String getIndirizzo() {
    return this.indirizzo;
  }

  public void setIndirizzo(String indirizzo) {
    this.indirizzo = indirizzo;
  }

  public TransazioneExtInformazioniSoggetto indirizzo(String indirizzo) {
    this.indirizzo = indirizzo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransazioneExtInformazioniSoggetto {\n");
    
    sb.append("    tipo: ").append(TransazioneExtInformazioniSoggetto.toIndentedString(this.tipo)).append("\n");
    sb.append("    codice: ").append(TransazioneExtInformazioniSoggetto.toIndentedString(this.codice)).append("\n");
    sb.append("    indirizzo: ").append(TransazioneExtInformazioniSoggetto.toIndentedString(this.indirizzo)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private static String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
