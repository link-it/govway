package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets FormatoSoapEnum
 */
public enum FormatoSoapEnum {
_1("Wsdl1.1");

  private String value;

  FormatoSoapEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static FormatoSoapEnum fromValue(String text) {
    for (FormatoSoapEnum b : FormatoSoapEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
