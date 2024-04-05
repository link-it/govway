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
package org.openspcoop2.core.config.rs.server.model;

import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class ErogazioneModIRest  implements OneOfErogazioneModi, OneOfErogazioneModIModi {
  
  @Schema(required = true, description = "")
  private TipoApiEnum protocollo = null;
  
  @Schema(required = true, description = "")
  private ErogazioneModIRestRichiesta richiesta = null;
  
  @Schema(required = true, description = "")
  private ErogazioneModIRestRisposta risposta = null;
 /**
   * Get protocollo
   * @return protocollo
  **/
  @Override
@JsonProperty("protocollo")
  @NotNull
  @Valid
  public TipoApiEnum getProtocollo() {
    return this.protocollo;
  }

  public void setProtocollo(TipoApiEnum protocollo) {
    this.protocollo = protocollo;
  }

  public ErogazioneModIRest protocollo(TipoApiEnum protocollo) {
    this.protocollo = protocollo;
    return this;
  }

 /**
   * Get richiesta
   * @return richiesta
  **/
  @JsonProperty("richiesta")
  @NotNull
  @Valid
  public ErogazioneModIRestRichiesta getRichiesta() {
    return this.richiesta;
  }

  public void setRichiesta(ErogazioneModIRestRichiesta richiesta) {
    this.richiesta = richiesta;
  }

  public ErogazioneModIRest richiesta(ErogazioneModIRestRichiesta richiesta) {
    this.richiesta = richiesta;
    return this;
  }

 /**
   * Get risposta
   * @return risposta
  **/
  @JsonProperty("risposta")
  @NotNull
  @Valid
  public ErogazioneModIRestRisposta getRisposta() {
    return this.risposta;
  }

  public void setRisposta(ErogazioneModIRestRisposta risposta) {
    this.risposta = risposta;
  }

  public ErogazioneModIRest risposta(ErogazioneModIRestRisposta risposta) {
    this.risposta = risposta;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErogazioneModIRest {\n");
    
    sb.append("    protocollo: ").append(ErogazioneModIRest.toIndentedString(this.protocollo)).append("\n");
    sb.append("    richiesta: ").append(ErogazioneModIRest.toIndentedString(this.richiesta)).append("\n");
    sb.append("    risposta: ").append(ErogazioneModIRest.toIndentedString(this.risposta)).append("\n");
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
