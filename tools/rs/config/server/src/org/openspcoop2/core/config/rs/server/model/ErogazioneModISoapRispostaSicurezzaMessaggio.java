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
import jakarta.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

public class ErogazioneModISoapRispostaSicurezzaMessaggio  {
  
  @Schema(description = "")
  private ModISicurezzaMessaggioSoapAlgoritmoFirma algoritmo = null;
  
  @Schema(description = "")
  private List<String> headerSoapFirmare = null;
  
  @Schema(description = "")
  private ModISicurezzaMessaggioSoapFormaCanonicaXml formaCanonicaXml = null;
  
  @Schema(description = "")
  private ModISicurezzaMessaggioSoapRiferimentoX509 riferimentoX509 = null;
  
  @Schema(description = "")
  private Boolean certificateChain = null;
  
  @Schema(description = "")
  private Boolean includiSignatureToken = null;
  
  @Schema(required = true, description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "modalita", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ModIKeyStoreDefault.class, name = "default"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ModIKeyStoreRidefinito.class, name = "ridefinito")  })
  private OneOfErogazioneModISoapRispostaSicurezzaMessaggioKeystore keystore = null;
  
  @Schema(description = "")
  private Integer timeToLive = 300;
 /**
   * Get algoritmo
   * @return algoritmo
  **/
  @JsonProperty("algoritmo")
  @Valid
  public ModISicurezzaMessaggioSoapAlgoritmoFirma getAlgoritmo() {
    return this.algoritmo;
  }

  public void setAlgoritmo(ModISicurezzaMessaggioSoapAlgoritmoFirma algoritmo) {
    this.algoritmo = algoritmo;
  }

  public ErogazioneModISoapRispostaSicurezzaMessaggio algoritmo(ModISicurezzaMessaggioSoapAlgoritmoFirma algoritmo) {
    this.algoritmo = algoritmo;
    return this;
  }

 /**
   * Get headerSoapFirmare
   * @return headerSoapFirmare
  **/
  @JsonProperty("header_soap_firmare")
  @Valid
  public List<String> getHeaderSoapFirmare() {
    return this.headerSoapFirmare;
  }

  public void setHeaderSoapFirmare(List<String> headerSoapFirmare) {
    this.headerSoapFirmare = headerSoapFirmare;
  }

  public ErogazioneModISoapRispostaSicurezzaMessaggio headerSoapFirmare(List<String> headerSoapFirmare) {
    this.headerSoapFirmare = headerSoapFirmare;
    return this;
  }

  public ErogazioneModISoapRispostaSicurezzaMessaggio addHeaderSoapFirmareItem(String headerSoapFirmareItem) {
    this.headerSoapFirmare.add(headerSoapFirmareItem);
    return this;
  }

 /**
   * Get formaCanonicaXml
   * @return formaCanonicaXml
  **/
  @JsonProperty("forma_canonica_xml")
  @Valid
  public ModISicurezzaMessaggioSoapFormaCanonicaXml getFormaCanonicaXml() {
    return this.formaCanonicaXml;
  }

  public void setFormaCanonicaXml(ModISicurezzaMessaggioSoapFormaCanonicaXml formaCanonicaXml) {
    this.formaCanonicaXml = formaCanonicaXml;
  }

  public ErogazioneModISoapRispostaSicurezzaMessaggio formaCanonicaXml(ModISicurezzaMessaggioSoapFormaCanonicaXml formaCanonicaXml) {
    this.formaCanonicaXml = formaCanonicaXml;
    return this;
  }

 /**
   * Get riferimentoX509
   * @return riferimentoX509
  **/
  @JsonProperty("riferimento_x509")
  @Valid
  public ModISicurezzaMessaggioSoapRiferimentoX509 getRiferimentoX509() {
    return this.riferimentoX509;
  }

  public void setRiferimentoX509(ModISicurezzaMessaggioSoapRiferimentoX509 riferimentoX509) {
    this.riferimentoX509 = riferimentoX509;
  }

  public ErogazioneModISoapRispostaSicurezzaMessaggio riferimentoX509(ModISicurezzaMessaggioSoapRiferimentoX509 riferimentoX509) {
    this.riferimentoX509 = riferimentoX509;
    return this;
  }

 /**
   * Get certificateChain
   * @return certificateChain
  **/
  @JsonProperty("certificate_chain")
  @Valid
  public Boolean isCertificateChain() {
    return this.certificateChain;
  }

  public void setCertificateChain(Boolean certificateChain) {
    this.certificateChain = certificateChain;
  }

  public ErogazioneModISoapRispostaSicurezzaMessaggio certificateChain(Boolean certificateChain) {
    this.certificateChain = certificateChain;
    return this;
  }

 /**
   * Get includiSignatureToken
   * @return includiSignatureToken
  **/
  @JsonProperty("includi_signature_token")
  @Valid
  public Boolean isIncludiSignatureToken() {
    return this.includiSignatureToken;
  }

  public void setIncludiSignatureToken(Boolean includiSignatureToken) {
    this.includiSignatureToken = includiSignatureToken;
  }

  public ErogazioneModISoapRispostaSicurezzaMessaggio includiSignatureToken(Boolean includiSignatureToken) {
    this.includiSignatureToken = includiSignatureToken;
    return this;
  }

 /**
   * Get keystore
   * @return keystore
  **/
  @JsonProperty("keystore")
  @NotNull
  @Valid
  public OneOfErogazioneModISoapRispostaSicurezzaMessaggioKeystore getKeystore() {
    return this.keystore;
  }

  public void setKeystore(OneOfErogazioneModISoapRispostaSicurezzaMessaggioKeystore keystore) {
    this.keystore = keystore;
  }

  public ErogazioneModISoapRispostaSicurezzaMessaggio keystore(OneOfErogazioneModISoapRispostaSicurezzaMessaggioKeystore keystore) {
    this.keystore = keystore;
    return this;
  }

 /**
   * Get timeToLive
   * @return timeToLive
  **/
  @JsonProperty("time_to_live")
  @Valid
  public Integer getTimeToLive() {
    return this.timeToLive;
  }

  public void setTimeToLive(Integer timeToLive) {
    this.timeToLive = timeToLive;
  }

  public ErogazioneModISoapRispostaSicurezzaMessaggio timeToLive(Integer timeToLive) {
    this.timeToLive = timeToLive;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErogazioneModISoapRispostaSicurezzaMessaggio {\n");
    
    sb.append("    algoritmo: ").append(ErogazioneModISoapRispostaSicurezzaMessaggio.toIndentedString(this.algoritmo)).append("\n");
    sb.append("    headerSoapFirmare: ").append(ErogazioneModISoapRispostaSicurezzaMessaggio.toIndentedString(this.headerSoapFirmare)).append("\n");
    sb.append("    formaCanonicaXml: ").append(ErogazioneModISoapRispostaSicurezzaMessaggio.toIndentedString(this.formaCanonicaXml)).append("\n");
    sb.append("    riferimentoX509: ").append(ErogazioneModISoapRispostaSicurezzaMessaggio.toIndentedString(this.riferimentoX509)).append("\n");
    sb.append("    certificateChain: ").append(ErogazioneModISoapRispostaSicurezzaMessaggio.toIndentedString(this.certificateChain)).append("\n");
    sb.append("    includiSignatureToken: ").append(ErogazioneModISoapRispostaSicurezzaMessaggio.toIndentedString(this.includiSignatureToken)).append("\n");
    sb.append("    keystore: ").append(ErogazioneModISoapRispostaSicurezzaMessaggio.toIndentedString(this.keystore)).append("\n");
    sb.append("    timeToLive: ").append(ErogazioneModISoapRispostaSicurezzaMessaggio.toIndentedString(this.timeToLive)).append("\n");
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
