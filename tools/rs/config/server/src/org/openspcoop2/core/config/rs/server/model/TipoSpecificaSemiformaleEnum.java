package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets TipoSpecificaSemiformaleEnum
 */
public enum TipoSpecificaSemiformaleEnum {
UML("UML"),
  HTML("HTML"),
  XSD("XSD"),
  XML("XML"),
  JSON("JSON"),
  YAML("YAML"),
  LINGUAGGIO_NATURALE("Linguaggio Naturale");

  private String value;

  TipoSpecificaSemiformaleEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static TipoSpecificaSemiformaleEnum fromValue(String text) {
    for (TipoSpecificaSemiformaleEnum b : TipoSpecificaSemiformaleEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
