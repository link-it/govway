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

public class FruizioneModIOAuth extends BaseFruizioneModIOAuth implements OneOfFruizioneModi, OneOfFruizioneModIModi {
  
  @Schema(requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, description = "")
  private TipoConfigurazioneFruizioneEnum protocollo = null;
  
  @Schema(description = "")
  @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = "modalita", visible = true )
  @com.fasterxml.jackson.annotation.JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ModIKeyStoreDefault.class, name = "default"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ModIKeyStoreRidefinito.class, name = "ridefinito")  })
  private OneOfFruizioneModIOAuthKeystore keystore = null;
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

  public FruizioneModIOAuth protocollo(TipoConfigurazioneFruizioneEnum protocollo) {
    this.protocollo = protocollo;
    return this;
  }

 /**
   * Get keystore
   * @return keystore
  **/
  @JsonProperty("keystore")
  @Valid
  public OneOfFruizioneModIOAuthKeystore getKeystore() {
    return this.keystore;
  }

  public void setKeystore(OneOfFruizioneModIOAuthKeystore keystore) {
    this.keystore = keystore;
  }

  public FruizioneModIOAuth keystore(OneOfFruizioneModIOAuthKeystore keystore) {
    this.keystore = keystore;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FruizioneModIOAuth {\n");
    sb.append("    ").append(FruizioneModIOAuth.toIndentedString(super.toString())).append("\n");
    sb.append("    protocollo: ").append(FruizioneModIOAuth.toIndentedString(this.protocollo)).append("\n");
    sb.append("    keystore: ").append(FruizioneModIOAuth.toIndentedString(this.keystore)).append("\n");
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
