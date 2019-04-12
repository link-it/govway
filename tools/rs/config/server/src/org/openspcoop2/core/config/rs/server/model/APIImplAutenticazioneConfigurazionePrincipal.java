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

import org.openspcoop2.core.config.rs.server.model.TipoAutenticazionePrincipal;
import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class APIImplAutenticazioneConfigurazionePrincipal  {
  
  @Schema(required = true, description = "")
  private TipoAutenticazionePrincipal tipo = null;
  
  @Schema(example = "indica il nome dell'header http in caso di autenticazione 'header-based' o il nome del parametro della query nel caso di autenticazione 'form-based'", description = "")
  private String nome = null;
  
  @Schema(example = "indica l'espressione regolare da utilizzare in caso di autenticazione 'url-based'", description = "")
  private String pattern = null;
  
  @Schema(description = "")
  private Boolean forward = false;
 /**
   * Get tipo
   * @return tipo
  **/
  @JsonProperty("tipo")
  @NotNull
  @Valid
  public TipoAutenticazionePrincipal getTipo() {
    return this.tipo;
  }

  public void setTipo(TipoAutenticazionePrincipal tipo) {
    this.tipo = tipo;
  }

  public APIImplAutenticazioneConfigurazionePrincipal tipo(TipoAutenticazionePrincipal tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get nome
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

  public APIImplAutenticazioneConfigurazionePrincipal nome(String nome) {
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

  public APIImplAutenticazioneConfigurazionePrincipal pattern(String pattern) {
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

  public APIImplAutenticazioneConfigurazionePrincipal forward(Boolean forward) {
    this.forward = forward;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class APIImplAutenticazioneConfigurazionePrincipal {\n");
    
    sb.append("    tipo: ").append(APIImplAutenticazioneConfigurazionePrincipal.toIndentedString(this.tipo)).append("\n");
    sb.append("    nome: ").append(APIImplAutenticazioneConfigurazionePrincipal.toIndentedString(this.nome)).append("\n");
    sb.append("    pattern: ").append(APIImplAutenticazioneConfigurazionePrincipal.toIndentedString(this.pattern)).append("\n");
    sb.append("    forward: ").append(APIImplAutenticazioneConfigurazionePrincipal.toIndentedString(this.forward)).append("\n");
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
