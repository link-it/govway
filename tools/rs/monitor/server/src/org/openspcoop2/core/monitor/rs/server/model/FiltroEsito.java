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
package org.openspcoop2.core.monitor.rs.server.model;

import org.openspcoop2.core.monitor.rs.server.model.EsitoTransazioneFullSearchEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class FiltroEsito  {
  
  @Schema(required = true, description = "")
  private EsitoTransazioneFullSearchEnum tipo = null;
  
  @Schema(description = "informazione utilizzata per escludere le richieste scartate; viene utilizzata solamente nel caso il tipo sia: qualsiasi, fallite, fallite_e_fault")
 /**
   * informazione utilizzata per escludere le richieste scartate; viene utilizzata solamente nel caso il tipo sia: qualsiasi, fallite, fallite_e_fault  
  **/
  private Boolean escludiScartate = true;
  
  @Schema(description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "tipo", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = DettaglioEsitoSingleCode.class, name = "qualsiasi"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = DettaglioEsitoSingleCode.class, name = "ok"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = DettaglioEsitoSingleCode.class, name = "fallite"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = DettaglioEsitoSingleCode.class, name = "fallite_e_fault"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = DettaglioEsitoSingleCode.class, name = "errori_consegna"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = DettaglioEsitoSingleCode.class, name = "richieste_scartate"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = DettaglioEsitoListCode.class, name = "personalizzato")  })
  private OneOfFiltroEsitoDettaglio dettaglio = null;
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
   * Get dettaglio
   * @return dettaglio
  **/
  @JsonProperty("dettaglio")
  @Valid
  public OneOfFiltroEsitoDettaglio getDettaglio() {
    return this.dettaglio;
  }

  public void setDettaglio(OneOfFiltroEsitoDettaglio dettaglio) {
    this.dettaglio = dettaglio;
  }

  public FiltroEsito dettaglio(OneOfFiltroEsitoDettaglio dettaglio) {
    this.dettaglio = dettaglio;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FiltroEsito {\n");
    
    sb.append("    tipo: ").append(FiltroEsito.toIndentedString(this.tipo)).append("\n");
    sb.append("    escludiScartate: ").append(FiltroEsito.toIndentedString(this.escludiScartate)).append("\n");
    sb.append("    dettaglio: ").append(FiltroEsito.toIndentedString(this.dettaglio)).append("\n");
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
