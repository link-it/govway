/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.rs.server.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

/**
  * URL fornita dal client tramite header HTTP o parametro della URL
 **/
@Schema(description="URL fornita dal client tramite header HTTP o parametro della URL")
public class FruizioneModIScambioAsincronoUrlCallbackClient extends ModIScambioAsincronoParametro implements OneOfFruizioneModIScambioAsincronoUrlCallback {
  
  @Schema(requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, description = "")
  private ModIScambioAsincronoUrlCallbackSorgenteEnum sorgente = null;
  
  @Schema(description = "")
  private ModIScambioAsincronoCodificaEnum codifica = null;
  
  @Schema(description = "")
  private Boolean obbligatoria = false;
 /**
   * Get sorgente
   * @return sorgente
  **/
  @Override
@JsonProperty("sorgente")
  @NotNull
  @Valid
  public ModIScambioAsincronoUrlCallbackSorgenteEnum getSorgente() {
    return this.sorgente;
  }

  public void setSorgente(ModIScambioAsincronoUrlCallbackSorgenteEnum sorgente) {
    this.sorgente = sorgente;
  }

  public FruizioneModIScambioAsincronoUrlCallbackClient sorgente(ModIScambioAsincronoUrlCallbackSorgenteEnum sorgente) {
    this.sorgente = sorgente;
    return this;
  }

 /**
   * Get codifica
   * @return codifica
  **/
  @JsonProperty("codifica")
  @Valid
  public ModIScambioAsincronoCodificaEnum getCodifica() {
    return this.codifica;
  }

  public void setCodifica(ModIScambioAsincronoCodificaEnum codifica) {
    this.codifica = codifica;
  }

  public FruizioneModIScambioAsincronoUrlCallbackClient codifica(ModIScambioAsincronoCodificaEnum codifica) {
    this.codifica = codifica;
    return this;
  }

 /**
   * Get obbligatoria
   * @return obbligatoria
  **/
  @JsonProperty("obbligatoria")
  @Valid
  public Boolean isObbligatoria() {
    return this.obbligatoria;
  }

  public void setObbligatoria(Boolean obbligatoria) {
    this.obbligatoria = obbligatoria;
  }

  public FruizioneModIScambioAsincronoUrlCallbackClient obbligatoria(Boolean obbligatoria) {
    this.obbligatoria = obbligatoria;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FruizioneModIScambioAsincronoUrlCallbackClient {\n");
    sb.append("    ").append(FruizioneModIScambioAsincronoUrlCallbackClient.toIndentedString(super.toString())).append("\n");
    sb.append("    sorgente: ").append(FruizioneModIScambioAsincronoUrlCallbackClient.toIndentedString(this.sorgente)).append("\n");
    sb.append("    codifica: ").append(FruizioneModIScambioAsincronoUrlCallbackClient.toIndentedString(this.codifica)).append("\n");
    sb.append("    obbligatoria: ").append(FruizioneModIScambioAsincronoUrlCallbackClient.toIndentedString(this.obbligatoria)).append("\n");
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
