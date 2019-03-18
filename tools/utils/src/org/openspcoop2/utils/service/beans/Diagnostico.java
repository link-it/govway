/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import org.joda.time.DateTime;
import org.openspcoop2.utils.service.beans.DiagnosticoSeveritaEnum;
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
 @XmlType(name = "Diagnostico", propOrder =
    { "data", "severitaCodice", "severita", "funzione", "codice", "messaggio"
})

@XmlRootElement(name="Diagnostico")
public class Diagnostico  {
  @XmlElement(name="data", required = true)
  
  @Schema(required = true, description = "")
  private DateTime data = null;
  @XmlElement(name="severita_codice", required = true)
  
  @Schema(required = true, description = "")
  private String severitaCodice = null;
  @XmlElement(name="severita", required = true)
  
  @Schema(required = true, description = "")
  private DiagnosticoSeveritaEnum severita = null;
  @XmlElement(name="funzione", required = true)
  
  @Schema(required = true, description = "")
  private String funzione = null;
  @XmlElement(name="codice", required = true)
  
  @Schema(required = true, description = "")
  private String codice = null;
  @XmlElement(name="messaggio", required = true)
  
  @Schema(example = "Generato messaggio di cooperazione di Errore con identificativo [0998f497-e05d-420a-a6b0-ff3bb718d2c4]", required = true, description = "")
  private String messaggio = null;
 /**
   * Get data
   * @return data
  **/
  @JsonProperty("data")
  @NotNull
  @Valid
  public DateTime getData() {
    return this.data;
  }

  public void setData(DateTime data) {
    this.data = data;
  }

  public Diagnostico data(DateTime data) {
    this.data = data;
    return this;
  }

 /**
   * Get severitaCodice
   * @return severitaCodice
  **/
  @JsonProperty("severita_codice")
  @NotNull
  @Valid
  public String getSeveritaCodice() {
    return this.severitaCodice;
  }

  public void setSeveritaCodice(String severitaCodice) {
    this.severitaCodice = severitaCodice;
  }

  public Diagnostico severitaCodice(String severitaCodice) {
    this.severitaCodice = severitaCodice;
    return this;
  }

 /**
   * Get severita
   * @return severita
  **/
  @JsonProperty("severita")
  @NotNull
  @Valid
  public DiagnosticoSeveritaEnum getSeverita() {
    return this.severita;
  }

  public void setSeverita(DiagnosticoSeveritaEnum severita) {
    this.severita = severita;
  }

  public Diagnostico severita(DiagnosticoSeveritaEnum severita) {
    this.severita = severita;
    return this;
  }

 /**
   * Get funzione
   * @return funzione
  **/
  @JsonProperty("funzione")
  @NotNull
  @Valid
  public String getFunzione() {
    return this.funzione;
  }

  public void setFunzione(String funzione) {
    this.funzione = funzione;
  }

  public Diagnostico funzione(String funzione) {
    this.funzione = funzione;
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

  public Diagnostico codice(String codice) {
    this.codice = codice;
    return this;
  }

 /**
   * Get messaggio
   * @return messaggio
  **/
  @JsonProperty("messaggio")
  @NotNull
  @Valid
  public String getMessaggio() {
    return this.messaggio;
  }

  public void setMessaggio(String messaggio) {
    this.messaggio = messaggio;
  }

  public Diagnostico messaggio(String messaggio) {
    this.messaggio = messaggio;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Diagnostico {\n");
    
    sb.append("    data: ").append(Diagnostico.toIndentedString(this.data)).append("\n");
    sb.append("    severitaCodice: ").append(Diagnostico.toIndentedString(this.severitaCodice)).append("\n");
    sb.append("    severita: ").append(Diagnostico.toIndentedString(this.severita)).append("\n");
    sb.append("    funzione: ").append(Diagnostico.toIndentedString(this.funzione)).append("\n");
    sb.append("    codice: ").append(Diagnostico.toIndentedString(this.codice)).append("\n");
    sb.append("    messaggio: ").append(Diagnostico.toIndentedString(this.messaggio)).append("\n");
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
