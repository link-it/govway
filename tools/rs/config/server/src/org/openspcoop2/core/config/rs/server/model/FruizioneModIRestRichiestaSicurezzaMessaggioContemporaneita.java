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

import java.util.List;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class FruizioneModIRestRichiestaSicurezzaMessaggioContemporaneita  {
  
  @Schema(required = true, description = "")
  private ModISicurezzaMessaggioRestSameDifferentEnum identificativo = null;
  
  @Schema(description = "")
  private ModISicurezzaMessaggioRestTokenChoiseEnum usaComeIdMessaggio = null;
  
  @Schema(required = true, description = "")
  private ModISicurezzaMessaggioRestSameDifferentEnum audience = null;
  
  @Schema(description = "")
  private String audienceAtteso = null;
  
  @Schema(description = "Indicare i claims richiesti (nome=valore); è possibile elencare differenti valori ammissibili separandoli con la virgola")
 /**
   * Indicare i claims richiesti (nome=valore); è possibile elencare differenti valori ammissibili separandoli con la virgola  
  **/
  private List<String> claimsBearer = null;
  
  @Schema(description = "Indicare i claims richiesti (nome=valore); è possibile elencare differenti valori ammissibili separandoli con la virgola")
 /**
   * Indicare i claims richiesti (nome=valore); è possibile elencare differenti valori ammissibili separandoli con la virgola  
  **/
  private List<String> claimsAgid = null;
 /**
   * Get identificativo
   * @return identificativo
  **/
  @JsonProperty("identificativo")
  @NotNull
  @Valid
  public ModISicurezzaMessaggioRestSameDifferentEnum getIdentificativo() {
    return this.identificativo;
  }

  public void setIdentificativo(ModISicurezzaMessaggioRestSameDifferentEnum identificativo) {
    this.identificativo = identificativo;
  }

  public FruizioneModIRestRichiestaSicurezzaMessaggioContemporaneita identificativo(ModISicurezzaMessaggioRestSameDifferentEnum identificativo) {
    this.identificativo = identificativo;
    return this;
  }

 /**
   * Get usaComeIdMessaggio
   * @return usaComeIdMessaggio
  **/
  @JsonProperty("usa_come_id_messaggio")
  @Valid
  public ModISicurezzaMessaggioRestTokenChoiseEnum getUsaComeIdMessaggio() {
    return this.usaComeIdMessaggio;
  }

  public void setUsaComeIdMessaggio(ModISicurezzaMessaggioRestTokenChoiseEnum usaComeIdMessaggio) {
    this.usaComeIdMessaggio = usaComeIdMessaggio;
  }

  public FruizioneModIRestRichiestaSicurezzaMessaggioContemporaneita usaComeIdMessaggio(ModISicurezzaMessaggioRestTokenChoiseEnum usaComeIdMessaggio) {
    this.usaComeIdMessaggio = usaComeIdMessaggio;
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

  public FruizioneModIRestRichiestaSicurezzaMessaggioContemporaneita audience(ModISicurezzaMessaggioRestSameDifferentEnum audience) {
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

  public FruizioneModIRestRichiestaSicurezzaMessaggioContemporaneita audienceAtteso(String audienceAtteso) {
    this.audienceAtteso = audienceAtteso;
    return this;
  }

 /**
   * Indicare i claims richiesti (nome=valore); è possibile elencare differenti valori ammissibili separandoli con la virgola
   * @return claimsBearer
  **/
  @JsonProperty("claims_bearer")
  @Valid
  public List<String> getClaimsBearer() {
    return this.claimsBearer;
  }

  public void setClaimsBearer(List<String> claimsBearer) {
    this.claimsBearer = claimsBearer;
  }

  public FruizioneModIRestRichiestaSicurezzaMessaggioContemporaneita claimsBearer(List<String> claimsBearer) {
    this.claimsBearer = claimsBearer;
    return this;
  }

  public FruizioneModIRestRichiestaSicurezzaMessaggioContemporaneita addClaimsBearerItem(String claimsBearerItem) {
    this.claimsBearer.add(claimsBearerItem);
    return this;
  }

 /**
   * Indicare i claims richiesti (nome=valore); è possibile elencare differenti valori ammissibili separandoli con la virgola
   * @return claimsAgid
  **/
  @JsonProperty("claims_agid")
  @Valid
  public List<String> getClaimsAgid() {
    return this.claimsAgid;
  }

  public void setClaimsAgid(List<String> claimsAgid) {
    this.claimsAgid = claimsAgid;
  }

  public FruizioneModIRestRichiestaSicurezzaMessaggioContemporaneita claimsAgid(List<String> claimsAgid) {
    this.claimsAgid = claimsAgid;
    return this;
  }

  public FruizioneModIRestRichiestaSicurezzaMessaggioContemporaneita addClaimsAgidItem(String claimsAgidItem) {
    this.claimsAgid.add(claimsAgidItem);
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FruizioneModIRestRichiestaSicurezzaMessaggioContemporaneita {\n");
    
    sb.append("    identificativo: ").append(FruizioneModIRestRichiestaSicurezzaMessaggioContemporaneita.toIndentedString(this.identificativo)).append("\n");
    sb.append("    usaComeIdMessaggio: ").append(FruizioneModIRestRichiestaSicurezzaMessaggioContemporaneita.toIndentedString(this.usaComeIdMessaggio)).append("\n");
    sb.append("    audience: ").append(FruizioneModIRestRichiestaSicurezzaMessaggioContemporaneita.toIndentedString(this.audience)).append("\n");
    sb.append("    audienceAtteso: ").append(FruizioneModIRestRichiestaSicurezzaMessaggioContemporaneita.toIndentedString(this.audienceAtteso)).append("\n");
    sb.append("    claimsBearer: ").append(FruizioneModIRestRichiestaSicurezzaMessaggioContemporaneita.toIndentedString(this.claimsBearer)).append("\n");
    sb.append("    claimsAgid: ").append(FruizioneModIRestRichiestaSicurezzaMessaggioContemporaneita.toIndentedString(this.claimsAgid)).append("\n");
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
