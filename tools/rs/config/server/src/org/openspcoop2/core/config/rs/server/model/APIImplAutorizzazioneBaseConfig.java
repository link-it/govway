package org.openspcoop2.core.config.rs.server.model;

import org.openspcoop2.core.config.rs.server.model.AllAnyEnum;
import org.openspcoop2.core.config.rs.server.model.FonteEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

public class APIImplAutorizzazioneBaseConfig  {
  
  @Schema(description = "")
  private Boolean puntuale = true;
  
  @Schema(description = "")
  private Boolean ruoli = false;
  
  @Schema(description = "")
  private FonteEnum ruoliFonte = null;
  
  @Schema(description = "")
  private AllAnyEnum ruoliRichiesti = null;
 /**
   * Get puntuale
   * @return puntuale
  **/
  @JsonProperty("puntuale")
  public Boolean isPuntuale() {
    return this.puntuale;
  }

  public void setPuntuale(Boolean puntuale) {
    this.puntuale = puntuale;
  }

  public APIImplAutorizzazioneBaseConfig puntuale(Boolean puntuale) {
    this.puntuale = puntuale;
    return this;
  }

 /**
   * Get ruoli
   * @return ruoli
  **/
  @JsonProperty("ruoli")
  public Boolean isRuoli() {
    return this.ruoli;
  }

  public void setRuoli(Boolean ruoli) {
    this.ruoli = ruoli;
  }

  public APIImplAutorizzazioneBaseConfig ruoli(Boolean ruoli) {
    this.ruoli = ruoli;
    return this;
  }

 /**
   * Get ruoliFonte
   * @return ruoliFonte
  **/
  @JsonProperty("ruoli_fonte")
  public FonteEnum getRuoliFonte() {
    return this.ruoliFonte;
  }

  public void setRuoliFonte(FonteEnum ruoliFonte) {
    this.ruoliFonte = ruoliFonte;
  }

  public APIImplAutorizzazioneBaseConfig ruoliFonte(FonteEnum ruoliFonte) {
    this.ruoliFonte = ruoliFonte;
    return this;
  }

 /**
   * Get ruoliRichiesti
   * @return ruoliRichiesti
  **/
  @JsonProperty("ruoli_richiesti")
  public AllAnyEnum getRuoliRichiesti() {
    return this.ruoliRichiesti;
  }

  public void setRuoliRichiesti(AllAnyEnum ruoliRichiesti) {
    this.ruoliRichiesti = ruoliRichiesti;
  }

  public APIImplAutorizzazioneBaseConfig ruoliRichiesti(AllAnyEnum ruoliRichiesti) {
    this.ruoliRichiesti = ruoliRichiesti;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class APIImplAutorizzazioneBaseConfig {\n");
    
    sb.append("    puntuale: ").append(APIImplAutorizzazioneBaseConfig.toIndentedString(this.puntuale)).append("\n");
    sb.append("    ruoli: ").append(APIImplAutorizzazioneBaseConfig.toIndentedString(this.ruoli)).append("\n");
    sb.append("    ruoliFonte: ").append(APIImplAutorizzazioneBaseConfig.toIndentedString(this.ruoliFonte)).append("\n");
    sb.append("    ruoliRichiesti: ").append(APIImplAutorizzazioneBaseConfig.toIndentedString(this.ruoliRichiesti)).append("\n");
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
