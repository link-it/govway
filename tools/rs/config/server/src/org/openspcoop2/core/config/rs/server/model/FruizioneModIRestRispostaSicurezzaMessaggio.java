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

import java.util.List;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class FruizioneModIRestRispostaSicurezzaMessaggio  {
  
  @Schema(description = "")
  private ModISicurezzaMessaggioRestRiferimentoX509Risposta riferimentoX509 = null;
  
  @Schema(description = "")
  private List<ModISicurezzaMessaggioRestRiferimentoX509> riferimentoX509Risposta = null;
  
  @Schema(required = true, description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "modalita", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ModITrustStoreDefault.class, name = "default"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ModITrustStoreRidefinito.class, name = "ridefinito")  })
  private OneOfFruizioneModIRestRispostaSicurezzaMessaggioTruststore truststore = null;
  
  @Schema(description = "")
  private Boolean verificaAudience = true;
 /**
   * Get riferimentoX509
   * @return riferimentoX509
  **/
  @JsonProperty("riferimento_x509")
  @Valid
  public ModISicurezzaMessaggioRestRiferimentoX509Risposta getRiferimentoX509() {
    return this.riferimentoX509;
  }

  public void setRiferimentoX509(ModISicurezzaMessaggioRestRiferimentoX509Risposta riferimentoX509) {
    this.riferimentoX509 = riferimentoX509;
  }

  public FruizioneModIRestRispostaSicurezzaMessaggio riferimentoX509(ModISicurezzaMessaggioRestRiferimentoX509Risposta riferimentoX509) {
    this.riferimentoX509 = riferimentoX509;
    return this;
  }

 /**
   * Get riferimentoX509Risposta
   * @return riferimentoX509Risposta
  **/
  @JsonProperty("riferimento_x509_risposta")
  @Valid
 @Size(min=1,max=3)  public List<ModISicurezzaMessaggioRestRiferimentoX509> getRiferimentoX509Risposta() {
    return this.riferimentoX509Risposta;
  }

  public void setRiferimentoX509Risposta(List<ModISicurezzaMessaggioRestRiferimentoX509> riferimentoX509Risposta) {
    this.riferimentoX509Risposta = riferimentoX509Risposta;
  }

  public FruizioneModIRestRispostaSicurezzaMessaggio riferimentoX509Risposta(List<ModISicurezzaMessaggioRestRiferimentoX509> riferimentoX509Risposta) {
    this.riferimentoX509Risposta = riferimentoX509Risposta;
    return this;
  }

  public FruizioneModIRestRispostaSicurezzaMessaggio addRiferimentoX509RispostaItem(ModISicurezzaMessaggioRestRiferimentoX509 riferimentoX509RispostaItem) {
    this.riferimentoX509Risposta.add(riferimentoX509RispostaItem);
    return this;
  }

 /**
   * Get truststore
   * @return truststore
  **/
  @JsonProperty("truststore")
  @NotNull
  @Valid
  public OneOfFruizioneModIRestRispostaSicurezzaMessaggioTruststore getTruststore() {
    return this.truststore;
  }

  public void setTruststore(OneOfFruizioneModIRestRispostaSicurezzaMessaggioTruststore truststore) {
    this.truststore = truststore;
  }

  public FruizioneModIRestRispostaSicurezzaMessaggio truststore(OneOfFruizioneModIRestRispostaSicurezzaMessaggioTruststore truststore) {
    this.truststore = truststore;
    return this;
  }

 /**
   * Get verificaAudience
   * @return verificaAudience
  **/
  @JsonProperty("verifica_audience")
  @Valid
  public Boolean isVerificaAudience() {
    return this.verificaAudience;
  }

  public void setVerificaAudience(Boolean verificaAudience) {
    this.verificaAudience = verificaAudience;
  }

  public FruizioneModIRestRispostaSicurezzaMessaggio verificaAudience(Boolean verificaAudience) {
    this.verificaAudience = verificaAudience;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FruizioneModIRestRispostaSicurezzaMessaggio {\n");
    
    sb.append("    riferimentoX509: ").append(FruizioneModIRestRispostaSicurezzaMessaggio.toIndentedString(this.riferimentoX509)).append("\n");
    sb.append("    riferimentoX509Risposta: ").append(FruizioneModIRestRispostaSicurezzaMessaggio.toIndentedString(this.riferimentoX509Risposta)).append("\n");
    sb.append("    truststore: ").append(FruizioneModIRestRispostaSicurezzaMessaggio.toIndentedString(this.truststore)).append("\n");
    sb.append("    verificaAudience: ").append(FruizioneModIRestRispostaSicurezzaMessaggio.toIndentedString(this.verificaAudience)).append("\n");
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
