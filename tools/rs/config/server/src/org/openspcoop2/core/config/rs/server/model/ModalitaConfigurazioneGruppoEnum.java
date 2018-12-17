package org.openspcoop2.core.config.rs.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets ModalitaConfigurazioneGruppoEnum
 */
public enum ModalitaConfigurazioneGruppoEnum {
EREDITA("eredita"),
  NUOVA("nuova");

  private String value;

  ModalitaConfigurazioneGruppoEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(this.value);
  }

  @JsonCreator
  public static ModalitaConfigurazioneGruppoEnum fromValue(String text) {
    for (ModalitaConfigurazioneGruppoEnum b : ModalitaConfigurazioneGruppoEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
  
}
