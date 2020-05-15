/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.rs.server.model.TipoAutenticazioneHttps;
import org.openspcoop2.core.config.rs.server.model.TipoKeystore;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class AuthenticationHttpsCertificato  implements OneOfAuthenticationHttpsCertificato {
  
  @Schema(required = true, description = "")
  private TipoAutenticazioneHttps tipo = null;
  
  @Schema(required = true, description = "")
  private byte[] archivio = null;
  
  @Schema(example = "alias", description = "")
  private String alias = null;
  
  @Schema(example = "changeit", description = "")
  private String password = null;
  
  @Schema(required = true, description = "")
  private TipoKeystore tipoCertificato = null;
  
  @Schema(example = "false", description = "")
  private Boolean strictVerification = false;
 /**
   * Get tipo
   * @return tipo
  **/
  @Override
@JsonProperty("tipo")
  @NotNull
  @Valid
  public TipoAutenticazioneHttps getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoAutenticazioneHttps tipo) {
    this.tipo = tipo;
  }

  public AuthenticationHttpsCertificato tipo(TipoAutenticazioneHttps tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get archivio
   * @return archivio
  **/
  @JsonProperty("archivio")
  @NotNull
  @Valid
  public byte[] getArchivio() {
    return this.archivio;
  }

  public void setArchivio(byte[] archivio) {
    this.archivio = archivio;
  }

  public AuthenticationHttpsCertificato archivio(byte[] archivio) {
    this.archivio = archivio;
    return this;
  }

 /**
   * Get alias
   * @return alias
  **/
  @JsonProperty("alias")
  @Valid
 @Size(max=255)  public String getAlias() {
    return this.alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public AuthenticationHttpsCertificato alias(String alias) {
    this.alias = alias;
    return this;
  }

 /**
   * Get password
   * @return password
  **/
  @JsonProperty("password")
  @Valid
 @Size(max=255)  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public AuthenticationHttpsCertificato password(String password) {
    this.password = password;
    return this;
  }

 /**
   * Get tipoCertificato
   * @return tipoCertificato
  **/
  @JsonProperty("tipo_certificato")
  @NotNull
  @Valid
  public TipoKeystore getTipoCertificato() {
    return this.tipoCertificato;
  }

  public void setTipoCertificato(TipoKeystore tipoCertificato) {
    this.tipoCertificato = tipoCertificato;
  }

  public AuthenticationHttpsCertificato tipoCertificato(TipoKeystore tipoCertificato) {
    this.tipoCertificato = tipoCertificato;
    return this;
  }

 /**
   * Get strictVerification
   * @return strictVerification
  **/
  @JsonProperty("strict_verification")
  @Valid
  public Boolean isStrictVerification() {
    return this.strictVerification;
  }

  public void setStrictVerification(Boolean strictVerification) {
    this.strictVerification = strictVerification;
  }

  public AuthenticationHttpsCertificato strictVerification(Boolean strictVerification) {
    this.strictVerification = strictVerification;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuthenticationHttpsCertificato {\n");
    
    sb.append("    tipo: ").append(AuthenticationHttpsCertificato.toIndentedString(this.tipo)).append("\n");
    sb.append("    archivio: ").append(AuthenticationHttpsCertificato.toIndentedString(this.archivio)).append("\n");
    sb.append("    alias: ").append(AuthenticationHttpsCertificato.toIndentedString(this.alias)).append("\n");
    sb.append("    password: ").append(AuthenticationHttpsCertificato.toIndentedString(this.password)).append("\n");
    sb.append("    tipoCertificato: ").append(AuthenticationHttpsCertificato.toIndentedString(this.tipoCertificato)).append("\n");
    sb.append("    strictVerification: ").append(AuthenticationHttpsCertificato.toIndentedString(this.strictVerification)).append("\n");
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
