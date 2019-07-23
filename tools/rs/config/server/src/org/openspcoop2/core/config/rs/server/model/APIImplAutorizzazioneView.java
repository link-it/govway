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
package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.APIImplAutorizzazioneConfig;
import org.openspcoop2.core.config.rs.server.model.APIImplAutorizzazioneCustom;
import org.openspcoop2.core.config.rs.server.model.APIImplAutorizzazioneXACMLViewConfig;
import org.openspcoop2.core.config.rs.server.model.TipoAutorizzazioneEnum;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class APIImplAutorizzazioneView  {
  
  @Schema(required = true, description = "")
  private TipoAutorizzazioneEnum tipo = null;
  
  @Schema(description = "")
  private APIImplAutorizzazioneConfig configurazione = null;
  
  @Schema(description = "")
  private APIImplAutorizzazioneXACMLViewConfig configurazioneXacml = null;
  
  @Schema(description = "")
  private APIImplAutorizzazioneCustom configurazioneCustom = null;
 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @NotNull
  @Valid
  public TipoAutorizzazioneEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoAutorizzazioneEnum tipo) {
    this.tipo = tipo;
  }

  public APIImplAutorizzazioneView tipo(TipoAutorizzazioneEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get configurazione
   * @return configurazione
  **/
  @JsonProperty("configurazione")
  @Valid
  public APIImplAutorizzazioneConfig getConfigurazione() {
    return this.configurazione;
  }

  public void setConfigurazione(APIImplAutorizzazioneConfig configurazione) {
    this.configurazione = configurazione;
  }

  public APIImplAutorizzazioneView configurazione(APIImplAutorizzazioneConfig configurazione) {
    this.configurazione = configurazione;
    return this;
  }

 /**
   * Get configurazioneXacml
   * @return configurazioneXacml
  **/
  @JsonProperty("configurazione_xacml")
  @Valid
  public APIImplAutorizzazioneXACMLViewConfig getConfigurazioneXacml() {
    return this.configurazioneXacml;
  }

  public void setConfigurazioneXacml(APIImplAutorizzazioneXACMLViewConfig configurazioneXacml) {
    this.configurazioneXacml = configurazioneXacml;
  }

  public APIImplAutorizzazioneView configurazioneXacml(APIImplAutorizzazioneXACMLViewConfig configurazioneXacml) {
    this.configurazioneXacml = configurazioneXacml;
    return this;
  }

 /**
   * Get configurazioneCustom
   * @return configurazioneCustom
  **/
  @JsonProperty("configurazione_custom")
  @Valid
  public APIImplAutorizzazioneCustom getConfigurazioneCustom() {
    return this.configurazioneCustom;
  }

  public void setConfigurazioneCustom(APIImplAutorizzazioneCustom configurazioneCustom) {
    this.configurazioneCustom = configurazioneCustom;
  }

  public APIImplAutorizzazioneView configurazioneCustom(APIImplAutorizzazioneCustom configurazioneCustom) {
    this.configurazioneCustom = configurazioneCustom;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class APIImplAutorizzazioneView {\n");
    
    sb.append("    tipo: ").append(APIImplAutorizzazioneView.toIndentedString(this.tipo)).append("\n");
    sb.append("    configurazione: ").append(APIImplAutorizzazioneView.toIndentedString(this.configurazione)).append("\n");
    sb.append("    configurazioneXacml: ").append(APIImplAutorizzazioneView.toIndentedString(this.configurazioneXacml)).append("\n");
    sb.append("    configurazioneCustom: ").append(APIImplAutorizzazioneView.toIndentedString(this.configurazioneCustom)).append("\n");
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
