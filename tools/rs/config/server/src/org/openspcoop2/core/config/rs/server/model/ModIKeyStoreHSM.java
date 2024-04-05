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

public class ModIKeyStoreHSM  implements OneOfModIKeyStoreRidefinitoDatiKeystore {
  
  @Schema(required = true, description = "")
  private ModIKeystoreTipologiaEnum tipologia = null;
  
  @Schema(required = true, description = "")
  private String pcks11Tipo = null;
  
  @Schema(example = "pwd", required = true, description = "alias della chiave privata")
 /**
   * alias della chiave privata  
  **/
  private String keyAlias = null;
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

  public ModIKeyStoreHSM tipologia(ModIKeystoreTipologiaEnum tipologia) {
    this.tipologia = tipologia;
    return this;
  }

 /**
   * Get pcks11Tipo
   * @return pcks11Tipo
  **/
  @JsonProperty("pcks11_tipo")
  @NotNull
  @Valid
 @Size(max=255)  public String getPcks11Tipo() {
    return this.pcks11Tipo;
  }

  public void setPcks11Tipo(String pcks11Tipo) {
    this.pcks11Tipo = pcks11Tipo;
  }

  public ModIKeyStoreHSM pcks11Tipo(String pcks11Tipo) {
    this.pcks11Tipo = pcks11Tipo;
    return this;
  }

 /**
   * alias della chiave privata
   * @return keyAlias
  **/
  @JsonProperty("key_alias")
  @NotNull
  @Valid
 @Size(max=255)  public String getKeyAlias() {
    return this.keyAlias;
  }

  public void setKeyAlias(String keyAlias) {
    this.keyAlias = keyAlias;
  }

  public ModIKeyStoreHSM keyAlias(String keyAlias) {
    this.keyAlias = keyAlias;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModIKeyStoreHSM {\n");
    
    sb.append("    tipologia: ").append(ModIKeyStoreHSM.toIndentedString(this.tipologia)).append("\n");
    sb.append("    pcks11Tipo: ").append(ModIKeyStoreHSM.toIndentedString(this.pcks11Tipo)).append("\n");
    sb.append("    keyAlias: ").append(ModIKeyStoreHSM.toIndentedString(this.keyAlias)).append("\n");
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
