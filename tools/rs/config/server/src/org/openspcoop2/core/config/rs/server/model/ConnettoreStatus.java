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
package org.openspcoop2.core.config.rs.server.model;

import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class ConnettoreStatus  implements OneOfApplicativoServerConnettore, OneOfConnettoreErogazioneConnettore, OneOfConnettoreFruizioneConnettore {
  
  @Schema(required = true, description = "")
  private ConnettoreEnum tipo = null;
  
  @Schema(required = true, description = "")
  private TipoRispostaStatusEnum risposta = null;
  
  @Schema(description = "")
  private Boolean verificaConnettivita = false;
  
  @Schema(description = "")
  private ConnettoreStatusVerificaStatistica verificaStatistica = null;
  
  @Schema(description = "")
  private Boolean debug = false;
 /**
   * Get tipo
   * @return tipo
  **/
  @Override
@JsonProperty("tipo")
  @NotNull
  @Valid
  public ConnettoreEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(ConnettoreEnum tipo) {
    this.tipo = tipo;
  }

  public ConnettoreStatus tipo(ConnettoreEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get risposta
   * @return risposta
  **/
  @JsonProperty("risposta")
  @NotNull
  @Valid
  public TipoRispostaStatusEnum getRisposta() {
    return this.risposta;
  }

  public void setRisposta(TipoRispostaStatusEnum risposta) {
    this.risposta = risposta;
  }

  public ConnettoreStatus risposta(TipoRispostaStatusEnum risposta) {
    this.risposta = risposta;
    return this;
  }

 /**
   * Get verificaConnettivita
   * @return verificaConnettivita
  **/
  @JsonProperty("verifica_connettivita")
  @Valid
  public Boolean isVerificaConnettivita() {
    return this.verificaConnettivita;
  }

  public void setVerificaConnettivita(Boolean verificaConnettivita) {
    this.verificaConnettivita = verificaConnettivita;
  }

  public ConnettoreStatus verificaConnettivita(Boolean verificaConnettivita) {
    this.verificaConnettivita = verificaConnettivita;
    return this;
  }

 /**
   * Get verificaStatistica
   * @return verificaStatistica
  **/
  @JsonProperty("verifica_statistica")
  @Valid
  public ConnettoreStatusVerificaStatistica getVerificaStatistica() {
    return this.verificaStatistica;
  }

  public void setVerificaStatistica(ConnettoreStatusVerificaStatistica verificaStatistica) {
    this.verificaStatistica = verificaStatistica;
  }

  public ConnettoreStatus verificaStatistica(ConnettoreStatusVerificaStatistica verificaStatistica) {
    this.verificaStatistica = verificaStatistica;
    return this;
  }

 /**
   * Get debug
   * @return debug
  **/
  @JsonProperty("debug")
  @Valid
  public Boolean isDebug() {
    return this.debug;
  }

  public void setDebug(Boolean debug) {
    this.debug = debug;
  }

  public ConnettoreStatus debug(Boolean debug) {
    this.debug = debug;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreStatus {\n");
    
    sb.append("    tipo: ").append(ConnettoreStatus.toIndentedString(this.tipo)).append("\n");
    sb.append("    risposta: ").append(ConnettoreStatus.toIndentedString(this.risposta)).append("\n");
    sb.append("    verificaConnettivita: ").append(ConnettoreStatus.toIndentedString(this.verificaConnettivita)).append("\n");
    sb.append("    verificaStatistica: ").append(ConnettoreStatus.toIndentedString(this.verificaStatistica)).append("\n");
    sb.append("    debug: ").append(ConnettoreStatus.toIndentedString(this.debug)).append("\n");
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
