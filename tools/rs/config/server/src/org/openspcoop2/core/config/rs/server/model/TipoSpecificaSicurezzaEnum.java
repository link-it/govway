package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets TipoSpecificaSicurezzaEnum
 */
public enum TipoSpecificaSicurezzaEnum {
WS_POLICY("WS-Policy"),
  XACML_POLICY("XACML-Policy"),
  LINGUAGGIO_NATURALE("Linguaggio Naturale");

  private String value;

  TipoSpecificaSicurezzaEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static TipoSpecificaSicurezzaEnum fromValue(String text) {
    for (TipoSpecificaSicurezzaEnum b : TipoSpecificaSicurezzaEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
