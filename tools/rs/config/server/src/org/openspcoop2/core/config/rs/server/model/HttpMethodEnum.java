package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets HttpMethodEnum
 */
public enum HttpMethodEnum {
QUALSIASI("Qualsiasi"),
  GET("GET"),
  POST("POST"),
  PUT("PUT"),
  DELETE("DELETE"),
  OPTIONS("OPTIONS"),
  HEAD("HEAD"),
  TRACE("TRACE"),
  PATCH("PATCH"),
  LINK("LINK"),
  UNLINK("UNLINK");

  private String value;

  HttpMethodEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static HttpMethodEnum fromValue(String text) {
    for (HttpMethodEnum b : HttpMethodEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
