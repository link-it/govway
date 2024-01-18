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

public class ConnettoreJms  implements OneOfApplicativoServerConnettore, OneOfConnettoreErogazioneConnettore, OneOfConnettoreFruizioneConnettore {
  
  @Schema(required = true, description = "")
  private ConnettoreEnum tipo = null;
  
  @Schema(description = "")
  private Boolean debug = false;
  
  @Schema(required = true, description = "")
  private String nome = null;
  
  @Schema(required = true, description = "")
  private ConnettoreJmsTipoEnum tipoCoda = null;
  
  @Schema(required = true, description = "")
  private ConnettoreJmsSendAsEnum sendAs = null;
  
  @Schema(required = true, description = "")
  private String connectionFactory = null;
  
  @Schema(description = "")
  private String utente = null;
  
  @Schema(description = "")
  private String password = null;
  
  @Schema(description = "")
  private String jndiInitialContext = null;
  
  @Schema(description = "")
  private String jndiUrlPgkPrefixes = null;
  
  @Schema(description = "")
  private String jndiProviderUrl = null;
 /**
   * Get tipo
   * @return tipo
  **/
  @Override
@JsonProperty("tipo")
  @NotNull
  @Valid
  public ConnettoreEnum getTipo() {
    return this.tipo;
  }

  public void setTipo(ConnettoreEnum tipo) {
    this.tipo = tipo;
  }

  public ConnettoreJms tipo(ConnettoreEnum tipo) {
    this.tipo = tipo;
    return this;
  }

 /**
   * Get debug
   * @return debug
  **/
  @JsonProperty("debug")
  @Valid
  public Boolean isDebug() {
    return this.debug;
  }

  public void setDebug(Boolean debug) {
    this.debug = debug;
  }

  public ConnettoreJms debug(Boolean debug) {
    this.debug = debug;
    return this;
  }

 /**
   * Get nome
   * @return nome
  **/
  @JsonProperty("nome")
  @NotNull
  @Valid
 @Size(max=255)  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public ConnettoreJms nome(String nome) {
    this.nome = nome;
    return this;
  }

 /**
   * Get tipoCoda
   * @return tipoCoda
  **/
  @JsonProperty("tipo_coda")
  @NotNull
  @Valid
  public ConnettoreJmsTipoEnum getTipoCoda() {
    return this.tipoCoda;
  }

  public void setTipoCoda(ConnettoreJmsTipoEnum tipoCoda) {
    this.tipoCoda = tipoCoda;
  }

  public ConnettoreJms tipoCoda(ConnettoreJmsTipoEnum tipoCoda) {
    this.tipoCoda = tipoCoda;
    return this;
  }

 /**
   * Get sendAs
   * @return sendAs
  **/
  @JsonProperty("send_as")
  @NotNull
  @Valid
  public ConnettoreJmsSendAsEnum getSendAs() {
    return this.sendAs;
  }

  public void setSendAs(ConnettoreJmsSendAsEnum sendAs) {
    this.sendAs = sendAs;
  }

  public ConnettoreJms sendAs(ConnettoreJmsSendAsEnum sendAs) {
    this.sendAs = sendAs;
    return this;
  }

 /**
   * Get connectionFactory
   * @return connectionFactory
  **/
  @JsonProperty("connection_factory")
  @NotNull
  @Valid
 @Size(max=255)  public String getConnectionFactory() {
    return this.connectionFactory;
  }

  public void setConnectionFactory(String connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

  public ConnettoreJms connectionFactory(String connectionFactory) {
    this.connectionFactory = connectionFactory;
    return this;
  }

 /**
   * Get utente
   * @return utente
  **/
  @JsonProperty("utente")
  @Valid
 @Size(max=255)  public String getUtente() {
    return this.utente;
  }

  public void setUtente(String utente) {
    this.utente = utente;
  }

  public ConnettoreJms utente(String utente) {
    this.utente = utente;
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

  public ConnettoreJms password(String password) {
    this.password = password;
    return this;
  }

 /**
   * Get jndiInitialContext
   * @return jndiInitialContext
  **/
  @JsonProperty("jndi_initial_context")
  @Valid
 @Size(max=255)  public String getJndiInitialContext() {
    return this.jndiInitialContext;
  }

  public void setJndiInitialContext(String jndiInitialContext) {
    this.jndiInitialContext = jndiInitialContext;
  }

  public ConnettoreJms jndiInitialContext(String jndiInitialContext) {
    this.jndiInitialContext = jndiInitialContext;
    return this;
  }

 /**
   * Get jndiUrlPgkPrefixes
   * @return jndiUrlPgkPrefixes
  **/
  @JsonProperty("jndi_url_pgk_prefixes")
  @Valid
 @Size(max=255)  public String getJndiUrlPgkPrefixes() {
    return this.jndiUrlPgkPrefixes;
  }

  public void setJndiUrlPgkPrefixes(String jndiUrlPgkPrefixes) {
    this.jndiUrlPgkPrefixes = jndiUrlPgkPrefixes;
  }

  public ConnettoreJms jndiUrlPgkPrefixes(String jndiUrlPgkPrefixes) {
    this.jndiUrlPgkPrefixes = jndiUrlPgkPrefixes;
    return this;
  }

 /**
   * Get jndiProviderUrl
   * @return jndiProviderUrl
  **/
  @JsonProperty("jndi_provider_url")
  @Valid
 @Size(max=255)  public String getJndiProviderUrl() {
    return this.jndiProviderUrl;
  }

  public void setJndiProviderUrl(String jndiProviderUrl) {
    this.jndiProviderUrl = jndiProviderUrl;
  }

  public ConnettoreJms jndiProviderUrl(String jndiProviderUrl) {
    this.jndiProviderUrl = jndiProviderUrl;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreJms {\n");
    
    sb.append("    tipo: ").append(ConnettoreJms.toIndentedString(this.tipo)).append("\n");
    sb.append("    debug: ").append(ConnettoreJms.toIndentedString(this.debug)).append("\n");
    sb.append("    nome: ").append(ConnettoreJms.toIndentedString(this.nome)).append("\n");
    sb.append("    tipoCoda: ").append(ConnettoreJms.toIndentedString(this.tipoCoda)).append("\n");
    sb.append("    sendAs: ").append(ConnettoreJms.toIndentedString(this.sendAs)).append("\n");
    sb.append("    connectionFactory: ").append(ConnettoreJms.toIndentedString(this.connectionFactory)).append("\n");
    sb.append("    utente: ").append(ConnettoreJms.toIndentedString(this.utente)).append("\n");
    sb.append("    password: ").append(ConnettoreJms.toIndentedString(this.password)).append("\n");
    sb.append("    jndiInitialContext: ").append(ConnettoreJms.toIndentedString(this.jndiInitialContext)).append("\n");
    sb.append("    jndiUrlPgkPrefixes: ").append(ConnettoreJms.toIndentedString(this.jndiUrlPgkPrefixes)).append("\n");
    sb.append("    jndiProviderUrl: ").append(ConnettoreJms.toIndentedString(this.jndiProviderUrl)).append("\n");
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
