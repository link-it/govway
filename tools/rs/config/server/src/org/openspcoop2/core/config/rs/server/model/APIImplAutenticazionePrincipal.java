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

public class APIImplAutenticazionePrincipal  implements OneOfAPIImplAutenticazione, OneOfControlloAccessiAutenticazioneAutenticazione, OneOfGruppoNuovaConfigurazioneAutenticazione {
  
  @Schema(required = true, description = "")
  private TipoAutenticazioneEnum tipo = null;
  
  @Schema(required = true, description = "")
  private TipoAutenticazionePrincipal tipoPrincipal = null;
  
  @Schema(description = "")
  private TipoAutenticazionePrincipalToken token = null;
  
  @Schema(description = "indica il nome dell'header http in caso di autenticazione principal 'header-based',  il nome del parametro della query nel caso di autenticazione principal 'form-based' o il nome del claim in caso di autenticazione principal 'token' con tipo di claim 'custom' ")
 /**
   * indica il nome dell'header http in caso di autenticazione principal 'header-based',  il nome del parametro della query nel caso di autenticazione principal 'form-based' o il nome del claim in caso di autenticazione principal 'token' con tipo di claim 'custom'   
  **/
  private String nome = null;
  
  @Schema(example = "indica l'espressione regolare da utilizzare in caso di autenticazione 'url-based'", description = "")
  private String pattern = null;
  
  @Schema(example = "false", description = "")
  private Boolean forward = false;
  
  @Schema(example = "false", description = "")
  private Boolean opzionale = false;
 /**
   * Get tipo
   * @return tipo
  **/
  @Override
@JsonProperty("tipo")
  @NotNull
  @Valid
  public TipoAutenticazioneEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoAutenticazioneEnum tipo) {
    this.tipo = tipo;
  }

  public APIImplAutenticazionePrincipal tipo(TipoAutenticazioneEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get tipoPrincipal
   * @return tipoPrincipal
  **/
  @JsonProperty("tipo_principal")
  @NotNull
  @Valid
  public TipoAutenticazionePrincipal getTipoPrincipal() {
    return this.tipoPrincipal;
  }

  public void setTipoPrincipal(TipoAutenticazionePrincipal tipoPrincipal) {
    this.tipoPrincipal = tipoPrincipal;
  }

  public APIImplAutenticazionePrincipal tipoPrincipal(TipoAutenticazionePrincipal tipoPrincipal) {
    this.tipoPrincipal = tipoPrincipal;
    return this;
  }

 /**
   * Get token
   * @return token
  **/
  @JsonProperty("token")
  @Valid
  public TipoAutenticazionePrincipalToken getToken() {
    return this.token;
  }

  public void setToken(TipoAutenticazionePrincipalToken token) {
    this.token = token;
  }

  public APIImplAutenticazionePrincipal token(TipoAutenticazionePrincipalToken token) {
    this.token = token;
    return this;
  }

 /**
   * indica il nome dell'header http in caso di autenticazione principal 'header-based',  il nome del parametro della query nel caso di autenticazione principal 'form-based' o il nome del claim in caso di autenticazione principal 'token' con tipo di claim 'custom' 
   * @return nome
  **/
  @JsonProperty("nome")
  @Valid
 @Size(max=255)  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public APIImplAutenticazionePrincipal nome(String nome) {
    this.nome = nome;
    return this;
  }

 /**
   * Get pattern
   * @return pattern
  **/
  @JsonProperty("pattern")
  @Valid
  public String getPattern() {
    return this.pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public APIImplAutenticazionePrincipal pattern(String pattern) {
    this.pattern = pattern;
    return this;
  }

 /**
   * Get forward
   * @return forward
  **/
  @JsonProperty("forward")
  @Valid
  public Boolean isForward() {
    return this.forward;
  }

  public void setForward(Boolean forward) {
    this.forward = forward;
  }

  public APIImplAutenticazionePrincipal forward(Boolean forward) {
    this.forward = forward;
    return this;
  }

 /**
   * Get opzionale
   * @return opzionale
  **/
  @JsonProperty("opzionale")
  @Valid
  public Boolean isOpzionale() {
    return this.opzionale;
  }

  public void setOpzionale(Boolean opzionale) {
    this.opzionale = opzionale;
  }

  public APIImplAutenticazionePrincipal opzionale(Boolean opzionale) {
    this.opzionale = opzionale;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class APIImplAutenticazionePrincipal {\n");
    
    sb.append("    tipo: ").append(APIImplAutenticazionePrincipal.toIndentedString(this.tipo)).append("\n");
    sb.append("    tipoPrincipal: ").append(APIImplAutenticazionePrincipal.toIndentedString(this.tipoPrincipal)).append("\n");
    sb.append("    token: ").append(APIImplAutenticazionePrincipal.toIndentedString(this.token)).append("\n");
    sb.append("    nome: ").append(APIImplAutenticazionePrincipal.toIndentedString(this.nome)).append("\n");
    sb.append("    pattern: ").append(APIImplAutenticazionePrincipal.toIndentedString(this.pattern)).append("\n");
    sb.append("    forward: ").append(APIImplAutenticazionePrincipal.toIndentedString(this.forward)).append("\n");
    sb.append("    opzionale: ").append(APIImplAutenticazionePrincipal.toIndentedString(this.opzionale)).append("\n");
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
