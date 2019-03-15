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
package org.openspcoop2.core.monitor.rs.server.model;

import org.joda.time.DateTime;
import org.openspcoop2.utils.service.beans.DiagnosticoSeveritaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class Evento  {
  
  @Schema(description = "")
  private Long id = null;
  
  @Schema(description = "")
  private DateTime oraRegistrazione = null;
  
  @Schema(description = "")
  private DiagnosticoSeveritaEnum severita = null;
  
  @Schema(description = "")
  private String tipo = null;
  
  @Schema(description = "")
  private String codice = null;
  
  @Schema(description = "")
  private String origine = null;
 /**
   * Get id
   * @return id
  **/
  @JsonProperty("id")
  @Valid
  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Evento id(Long id) {
    this.id = id;
    return this;
  }

 /**
   * Get oraRegistrazione
   * @return oraRegistrazione
  **/
  @JsonProperty("ora_registrazione")
  @Valid
  public DateTime getOraRegistrazione() {
    return this.oraRegistrazione;
  }

  public void setOraRegistrazione(DateTime oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
  }

  public Evento oraRegistrazione(DateTime oraRegistrazione) {
    this.oraRegistrazione = oraRegistrazione;
    return this;
  }

 /**
   * Get severita
   * @return severita
  **/
  @JsonProperty("severita")
  @Valid
  public DiagnosticoSeveritaEnum getSeverita() {
    return this.severita;
  }

  public void setSeverita(DiagnosticoSeveritaEnum severita) {
    this.severita = severita;
  }

  public Evento severita(DiagnosticoSeveritaEnum severita) {
    this.severita = severita;
    return this;
  }

 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @Valid
  public String getTipo() {
    return this.tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public Evento tipo(String tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get codice
   * @return codice
  **/
  @JsonProperty("codice")
  @Valid
  public String getCodice() {
    return this.codice;
  }

  public void setCodice(String codice) {
    this.codice = codice;
  }

  public Evento codice(String codice) {
    this.codice = codice;
    return this;
  }

 /**
   * Get origine
   * @return origine
  **/
  @JsonProperty("origine")
  @Valid
  public String getOrigine() {
    return this.origine;
  }

  public void setOrigine(String origine) {
    this.origine = origine;
  }

  public Evento origine(String origine) {
    this.origine = origine;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Evento {\n");
    
    sb.append("    id: ").append(Evento.toIndentedString(this.id)).append("\n");
    sb.append("    oraRegistrazione: ").append(Evento.toIndentedString(this.oraRegistrazione)).append("\n");
    sb.append("    severita: ").append(Evento.toIndentedString(this.severita)).append("\n");
    sb.append("    tipo: ").append(Evento.toIndentedString(this.tipo)).append("\n");
    sb.append("    codice: ").append(Evento.toIndentedString(this.codice)).append("\n");
    sb.append("    origine: ").append(Evento.toIndentedString(this.origine)).append("\n");
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
