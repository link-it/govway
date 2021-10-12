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

import javax.validation.constraints.*;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class ConnettoreLoadBalancerStickySession  {
  
  @Schema(required = true, description = "")
  private ConnettoreLoadBalancerSessionIdEnum identificativo = null;
  
  @Schema(description = "La semantica cambia in funzione del tipo:   * cookie: nome del cookie   * header-based: nome dell'header   * url-based: espressione regolare da applicare alla url di invocazione   * form-based: nome del parametro della url   * content-based: xpath o json path da applicare sul contenuto   * indirizzo-ip: il campo non deve essere valorizzato, come identificativo di sessione viene utilizzato l'indirizzo ip del socket   * indirizzo-ip-forwarded: il campo non deve essere valorizzato, come identificativo di sessione viene utilizzato l'indirizzo ip inoltrato via header http   * template: template in formato 'govway'   * freemarker: template in formato 'freemarker'   * velocity: template in formato 'velocity'")
 /**
   * La semantica cambia in funzione del tipo:   * cookie: nome del cookie   * header-based: nome dell'header   * url-based: espressione regolare da applicare alla url di invocazione   * form-based: nome del parametro della url   * content-based: xpath o json path da applicare sul contenuto   * indirizzo-ip: il campo non deve essere valorizzato, come identificativo di sessione viene utilizzato l'indirizzo ip del socket   * indirizzo-ip-forwarded: il campo non deve essere valorizzato, come identificativo di sessione viene utilizzato l'indirizzo ip inoltrato via header http   * template: template in formato 'govway'   * freemarker: template in formato 'freemarker'   * velocity: template in formato 'velocity'  
  **/
  private String nome = null;
  
  @Schema(description = "durata della sessione in secondi")
 /**
   * durata della sessione in secondi  
  **/
  private Integer maxAge = null;
 /**
   * Get identificativo
   * @return identificativo
  **/
  @JsonProperty("identificativo")
  @NotNull
  @Valid
  public ConnettoreLoadBalancerSessionIdEnum getIdentificativo() {
    return this.identificativo;
  }

  public void setIdentificativo(ConnettoreLoadBalancerSessionIdEnum identificativo) {
    this.identificativo = identificativo;
  }

  public ConnettoreLoadBalancerStickySession identificativo(ConnettoreLoadBalancerSessionIdEnum identificativo) {
    this.identificativo = identificativo;
    return this;
  }

 /**
   * La semantica cambia in funzione del tipo:   * cookie: nome del cookie   * header-based: nome dell'header   * url-based: espressione regolare da applicare alla url di invocazione   * form-based: nome del parametro della url   * content-based: xpath o json path da applicare sul contenuto   * indirizzo-ip: il campo non deve essere valorizzato, come identificativo di sessione viene utilizzato l'indirizzo ip del socket   * indirizzo-ip-forwarded: il campo non deve essere valorizzato, come identificativo di sessione viene utilizzato l'indirizzo ip inoltrato via header http   * template: template in formato 'govway'   * freemarker: template in formato 'freemarker'   * velocity: template in formato 'velocity'
   * @return nome
  **/
  @JsonProperty("nome")
  @Valid
 @Size(max=4000)  public String getNome() {
    return this.nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public ConnettoreLoadBalancerStickySession nome(String nome) {
    this.nome = nome;
    return this;
  }

 /**
   * durata della sessione in secondi
   * @return maxAge
  **/
  @JsonProperty("max_age")
  @Valid
  public Integer getMaxAge() {
    return this.maxAge;
  }

  public void setMaxAge(Integer maxAge) {
    this.maxAge = maxAge;
  }

  public ConnettoreLoadBalancerStickySession maxAge(Integer maxAge) {
    this.maxAge = maxAge;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConnettoreLoadBalancerStickySession {\n");
    
    sb.append("    identificativo: ").append(ConnettoreLoadBalancerStickySession.toIndentedString(this.identificativo)).append("\n");
    sb.append("    nome: ").append(ConnettoreLoadBalancerStickySession.toIndentedString(this.nome)).append("\n");
    sb.append("    maxAge: ").append(ConnettoreLoadBalancerStickySession.toIndentedString(this.maxAge)).append("\n");
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
