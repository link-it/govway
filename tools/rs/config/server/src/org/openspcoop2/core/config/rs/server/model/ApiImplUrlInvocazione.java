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

public class ApiImplUrlInvocazione  {
  
  @Schema(required = true, description = "")
  private ModalitaIdentificazioneAzioneEnum modalita = null;
  
  @Schema(description = "XPath/JsonPath nel caso di modalità 'content-based' o espressione regolare nel caso 'url-based'")
 /**
   * XPath/JsonPath nel caso di modalità 'content-based' o espressione regolare nel caso 'url-based'  
  **/
  private String pattern = null;
  
  @Schema(description = "Nome dell'header http nel caso di modalità 'header-based' o nome dell'unica azione in caso di modalità 'static'")
 /**
   * Nome dell'header http nel caso di modalità 'header-based' o nome dell'unica azione in caso di modalità 'static'  
  **/
  private String nome = null;
  
  @Schema(description = "Indicazione se oltre alla modalità indicata per individuare l'azione viene usata comunque la modalità 'interface-based'")
 /**
   * Indicazione se oltre alla modalità indicata per individuare l'azione viene usata comunque la modalità 'interface-based'  
  **/
  private Boolean forceInterface = true;
 /**
   * Get modalita
   * @return modalita
  **/
  @JsonProperty("modalita")
  @NotNull
  @Valid
  public ModalitaIdentificazioneAzioneEnum getModalita() {
    return this.modalita;
  }

  public void setModalita(ModalitaIdentificazioneAzioneEnum modalita) {
    this.modalita = modalita;
  }

  public ApiImplUrlInvocazione modalita(ModalitaIdentificazioneAzioneEnum modalita) {
    this.modalita = modalita;
    return this;
  }

 /**
   * XPath/JsonPath nel caso di modalità 'content-based' o espressione regolare nel caso 'url-based'
   * @return pattern
  **/
  @JsonProperty("pattern")
  @Valid
 @Size(max=255)  public String getPattern() {
    return this.pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public ApiImplUrlInvocazione pattern(String pattern) {
    this.pattern = pattern;
    return this;
  }

 /**
   * Nome dell'header http nel caso di modalità 'header-based' o nome dell'unica azione in caso di modalità 'static'
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

  public ApiImplUrlInvocazione nome(String nome) {
    this.nome = nome;
    return this;
  }

 /**
   * Indicazione se oltre alla modalità indicata per individuare l'azione viene usata comunque la modalità 'interface-based'
   * @return forceInterface
  **/
  @JsonProperty("force_interface")
  @Valid
  public Boolean isForceInterface() {
    return this.forceInterface;
  }

  public void setForceInterface(Boolean forceInterface) {
    this.forceInterface = forceInterface;
  }

  public ApiImplUrlInvocazione forceInterface(Boolean forceInterface) {
    this.forceInterface = forceInterface;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApiImplUrlInvocazione {\n");
    
    sb.append("    modalita: ").append(ApiImplUrlInvocazione.toIndentedString(this.modalita)).append("\n");
    sb.append("    pattern: ").append(ApiImplUrlInvocazione.toIndentedString(this.pattern)).append("\n");
    sb.append("    nome: ").append(ApiImplUrlInvocazione.toIndentedString(this.nome)).append("\n");
    sb.append("    forceInterface: ").append(ApiImplUrlInvocazione.toIndentedString(this.forceInterface)).append("\n");
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
