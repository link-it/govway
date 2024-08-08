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

public class ModIKeyStoreFile extends BaseKeyStoreFile implements OneOfModIKeyStoreRidefinitoDatiKeystore {
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private ModIKeystoreTipologiaEnum tipologia = null;
  
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "")
  private ModIKeystoreEnum keystoreTipo = null;
 /**
   * Get tipologia
   * @return tipologia
  **/
  @Override
@JsonProperty("tipologia")
  @NotNull
  @Valid
  public ModIKeystoreTipologiaEnum getTipologia() {
    return this.tipologia;
  }

  public void setTipologia(ModIKeystoreTipologiaEnum tipologia) {
    this.tipologia = tipologia;
  }

  public ModIKeyStoreFile tipologia(ModIKeystoreTipologiaEnum tipologia) {
    this.tipologia = tipologia;
    return this;
  }

 /**
   * Get keystoreTipo
   * @return keystoreTipo
  **/
  @JsonProperty("keystore_tipo")
  @NotNull
  @Valid
  public ModIKeystoreEnum getKeystoreTipo() {
    return this.keystoreTipo;
  }

  public void setKeystoreTipo(ModIKeystoreEnum keystoreTipo) {
    this.keystoreTipo = keystoreTipo;
  }

  public ModIKeyStoreFile keystoreTipo(ModIKeystoreEnum keystoreTipo) {
    this.keystoreTipo = keystoreTipo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModIKeyStoreFile {\n");
    sb.append("    ").append(ModIKeyStoreFile.toIndentedString(super.toString())).append("\n");
    sb.append("    tipologia: ").append(ModIKeyStoreFile.toIndentedString(this.tipologia)).append("\n");
    sb.append("    keystoreTipo: ").append(ModIKeyStoreFile.toIndentedString(this.keystoreTipo)).append("\n");
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
