/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

public class FruizioneModIRestRispostaSicurezzaMessaggioContemporaneita  {
  
  @Schema(required = true, description = "")
  private ModISicurezzaMessaggioRestTokenChoiseEnum identificativo = null;
  
  @Schema(required = true, description = "")
  private ModISicurezzaMessaggioRestSameDifferentEnum audience = null;
  
  @Schema(description = "")
  private String audienceAtteso = null;
 /**
   * Get identificativo
   * @return identificativo
  **/
  @JsonProperty("identificativo")
  @NotNull
  @Valid
  public ModISicurezzaMessaggioRestTokenChoiseEnum getIdentificativo() {
    return this.identificativo;
  }

  public void setIdentificativo(ModISicurezzaMessaggioRestTokenChoiseEnum identificativo) {
    this.identificativo = identificativo;
  }

  public FruizioneModIRestRispostaSicurezzaMessaggioContemporaneita identificativo(ModISicurezzaMessaggioRestTokenChoiseEnum identificativo) {
    this.identificativo = identificativo;
    return this;
  }

 /**
   * Get audience
   * @return audience
  **/
  @JsonProperty("audience")
  @NotNull
  @Valid
  public ModISicurezzaMessaggioRestSameDifferentEnum getAudience() {
    return this.audience;
  }

  public void setAudience(ModISicurezzaMessaggioRestSameDifferentEnum audience) {
    this.audience = audience;
  }

  public FruizioneModIRestRispostaSicurezzaMessaggioContemporaneita audience(ModISicurezzaMessaggioRestSameDifferentEnum audience) {
    this.audience = audience;
    return this;
  }

 /**
   * Get audienceAtteso
   * @return audienceAtteso
  **/
  @JsonProperty("audience_atteso")
  @Valid
 @Size(max=4000)  public String getAudienceAtteso() {
    return this.audienceAtteso;
  }

  public void setAudienceAtteso(String audienceAtteso) {
    this.audienceAtteso = audienceAtteso;
  }

  public FruizioneModIRestRispostaSicurezzaMessaggioContemporaneita audienceAtteso(String audienceAtteso) {
    this.audienceAtteso = audienceAtteso;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FruizioneModIRestRispostaSicurezzaMessaggioContemporaneita {\n");
    
    sb.append("    identificativo: ").append(FruizioneModIRestRispostaSicurezzaMessaggioContemporaneita.toIndentedString(this.identificativo)).append("\n");
    sb.append("    audience: ").append(FruizioneModIRestRispostaSicurezzaMessaggioContemporaneita.toIndentedString(this.audience)).append("\n");
    sb.append("    audienceAtteso: ").append(FruizioneModIRestRispostaSicurezzaMessaggioContemporaneita.toIndentedString(this.audienceAtteso)).append("\n");
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
