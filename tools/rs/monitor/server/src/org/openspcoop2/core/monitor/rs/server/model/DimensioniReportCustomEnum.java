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
package org.openspcoop2.core.monitor.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets DimensioniReportCustomEnum
 */
public enum DimensioniReportCustomEnum {
TAG("tag"),
  API("api"),
  API_IMPLEMENTATION("api_implementation"),
  OPERATION("operation"),
  LOCAL_ORGANIZATION("local_organization"),
  REMOTE_ORGANIZATION("remote_organization"),
  CLIENT_ORGANIZATION("client_organization"),
  PROVIDER_ORGANIZATION("provider_organization"),
  TOKEN_ISSUER("token_issuer"),
  TOKEN_CLIENTID("token_clientid"),
  TOKEN_SUBJECT("token_subject"),
  TOKEN_USERNAME("token_username"),
  TOKEN_EMAIL("token_email"),
  TOKEN_PDND_ORGANIZATION("token_pdnd_organization"),
  PRINCIPAL("principal"),
  CLIENT("client"),
  TOKEN_CLIENT("token_client"),
  IP_ADDRESS("ip_address"),
  RESULT("result");

  private String value;

  DimensioniReportCustomEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static DimensioniReportCustomEnum fromValue(String text) {
    for (DimensioniReportCustomEnum b : DimensioniReportCustomEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
