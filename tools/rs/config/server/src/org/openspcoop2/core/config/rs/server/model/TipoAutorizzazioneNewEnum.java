package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets TipoAutorizzazioneNewEnum
 */
public enum TipoAutorizzazioneNewEnum {
DISABILITATO("disabilitato"),
  ABILITATO("abilitato"),
  XACML_POLICY("xacml-Policy");

  private String value;

  TipoAutorizzazioneNewEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static TipoAutorizzazioneNewEnum fromValue(String text) {
    for (TipoAutorizzazioneNewEnum b : TipoAutorizzazioneNewEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
