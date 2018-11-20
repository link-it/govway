package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets FormatoRestEnum
 */
public enum FormatoRestEnum {
WADL("Wadl"),
  SWAGGER2_0("Swagger2.0"),
  OPENAPI3_0("OpenApi3.0");

  private String value;

  FormatoRestEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static FormatoRestEnum fromValue(String text) {
    for (FormatoRestEnum b : FormatoRestEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
