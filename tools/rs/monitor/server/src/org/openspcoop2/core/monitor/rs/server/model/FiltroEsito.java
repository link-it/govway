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
package org.openspcoop2.core.monitor.rs.server.model;

import java.util.List;
import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class FiltroEsito  {
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private EsitoTransazioneFullSearchEnum tipo = null;
  
  @Schema(description = "informazione utilizzata per escludere le richieste scartate; viene utilizzata solamente nel caso il tipo sia: qualsiasi, fallite, fallite_e_fault")
 /**
   * informazione utilizzata per escludere le richieste scartate; viene utilizzata solamente nel caso il tipo sia: qualsiasi, fallite, fallite_e_fault  
  **/
  private Boolean escludiScartate = false;
  
  @Schema(description = "")
  private Integer codice = null;
  
  @Schema(description = "")
  private List<Integer> codici = null;
 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @NotNull
  @Valid
  public EsitoTransazioneFullSearchEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(EsitoTransazioneFullSearchEnum tipo) {
    this.tipo = tipo;
  }

  public FiltroEsito tipo(EsitoTransazioneFullSearchEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * informazione utilizzata per escludere le richieste scartate; viene utilizzata solamente nel caso il tipo sia: qualsiasi, fallite, fallite_e_fault
   * @return escludiScartate
  **/
  @JsonProperty("escludi_scartate")
  @Valid
  public Boolean isEscludiScartate() {
    return this.escludiScartate;
  }

  public void setEscludiScartate(Boolean escludiScartate) {
    this.escludiScartate = escludiScartate;
  }

  public FiltroEsito escludiScartate(Boolean escludiScartate) {
    this.escludiScartate = escludiScartate;
    return this;
  }

 /**
   * Get codice
   * @return codice
  **/
  @JsonProperty("codice")
  @Valid
  public Integer getCodice() {
    return this.codice;
  }

  public void setCodice(Integer codice) {
    this.codice = codice;
  }

  public FiltroEsito codice(Integer codice) {
    this.codice = codice;
    return this;
  }

 /**
   * Get codici
   * @return codici
  **/
  @JsonProperty("codici")
  @Valid
  public List<Integer> getCodici() {
    return this.codici;
  }

  public void setCodici(List<Integer> codici) {
    this.codici = codici;
  }

  public FiltroEsito codici(List<Integer> codici) {
    this.codici = codici;
    return this;
  }

  public FiltroEsito addCodiciItem(Integer codiciItem) {
    this.codici.add(codiciItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FiltroEsito {\n");
    
    sb.append("    tipo: ").append(FiltroEsito.toIndentedString(this.tipo)).append("\n");
    sb.append("    escludiScartate: ").append(FiltroEsito.toIndentedString(this.escludiScartate)).append("\n");
    sb.append("    codice: ").append(FiltroEsito.toIndentedString(this.codice)).append("\n");
    sb.append("    codici: ").append(FiltroEsito.toIndentedString(this.codici)).append("\n");
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
