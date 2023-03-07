/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class FruizioneModIRest  implements OneOfFruizioneModi, OneOfFruizioneModIModi {
  
  @Schema(required = true, description = "")
  private TipoConfigurazioneFruizioneEnum protocollo = null;
  
  @Schema(required = true, description = "")
  private FruizioneModIRestRichiesta richiesta = null;
  
  @Schema(required = true, description = "")
  private FruizioneModIRestRisposta risposta = null;
 /**
   * Get protocollo
   * @return protocollo
  **/
  @Override
@JsonProperty("protocollo")
  @NotNull
  @Valid
  public TipoConfigurazioneFruizioneEnum getProtocollo() {
    return this.protocollo;
  }

  public void setProtocollo(TipoConfigurazioneFruizioneEnum protocollo) {
    this.protocollo = protocollo;
  }

  public FruizioneModIRest protocollo(TipoConfigurazioneFruizioneEnum protocollo) {
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
  public FruizioneModIRestRichiesta getRichiesta() {
    return this.richiesta;
  }

  public void setRichiesta(FruizioneModIRestRichiesta richiesta) {
    this.richiesta = richiesta;
  }

  public FruizioneModIRest richiesta(FruizioneModIRestRichiesta richiesta) {
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
  public FruizioneModIRestRisposta getRisposta() {
    return this.risposta;
  }

  public void setRisposta(FruizioneModIRestRisposta risposta) {
    this.risposta = risposta;
  }

  public FruizioneModIRest risposta(FruizioneModIRestRisposta risposta) {
    this.risposta = risposta;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FruizioneModIRest {\n");
    
    sb.append("    protocollo: ").append(FruizioneModIRest.toIndentedString(this.protocollo)).append("\n");
    sb.append("    richiesta: ").append(FruizioneModIRest.toIndentedString(this.richiesta)).append("\n");
    sb.append("    risposta: ").append(FruizioneModIRest.toIndentedString(this.risposta)).append("\n");
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
