package org.openspcoop2.core.monitor.rs.server.model;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.Valid;

public class FiltroTransazione  {
  public enum TipologiaEnum {
    FRUZIONE("fruzione"),
    EROGAZIONE("erogazione");

    private String value;

    TipologiaEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return this.value;
    }

    @Override
    public String toString() {
      return String.valueOf(this.value);
    }
    @JsonCreator
    public static TipologiaEnum fromValue(String text) {
      for (TipologiaEnum b : TipologiaEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(example = "fruizione", description = "")
  private TipologiaEnum tipologia = null;
  public enum EsitoEnum {
    FALLITE("fallite"),
    FAULT_APPLICATIVO("fault applicativo"),
    COMPLETATE_CON_SUCCESSO("completate con successo");

    private String value;

    EsitoEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return this.value;
    }

    @Override
    public String toString() {
      return String.valueOf(this.value);
    }
    @JsonCreator
    public static EsitoEnum fromValue(String text) {
      for (EsitoEnum b : EsitoEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(example = "fallite", description = "")
  private EsitoEnum esito = null;
  public enum DettaglioEsitoEnum {
    OK("ok"),
    OK_PRESENZA_ANOMALIE("ok (presenza anomalie"),
    FAULT_APPLICATIVO("fault applicativo"),
    AUTENTICAZIONE_FALLITA("autenticazione fallita"),
    AUTORIZZAZIONE_NEGATA("autorizzazione negata"),
    RICHIESTA_CLIENT_RIFIUTATA("richiesta client rifiutata"),
    ERRORE_DI_CONNESSIONE("errore di connessione"),
    ERRORE_DI_PROTOCOLLO("errore di protocollo"),
    VIOLAZIONE_RATE_LIMITING("violazione rate limiting"),
    VIOLAZIONE_RATE_LIMITING_WARNINGONLY("violazione rate limiting warningOnly"),
    SUPERAMENTO_LIMITE_RICHIESTE("superamento limite richieste"),
    SUPERAMENTO_LIMITE_RICHIESTE_WARNINGONLY("superamento limite richieste warningOnly"),
    FAULT_PDD_ESTERNA("fault pdd esterna"),
    CONTENUTO_RICHIESTA_NON_RICONOSCIUTO("contenuto richiesta non riconosciuto"),
    CONTENUTO_RISPOSTA_NON_RICONOSCIUTO("contenuto risposta non riconosciuto"),
    CONNESSIONE_CLIENT_INTERROTTA("connessione client interrotta"),
    FAULT_GENERATO_DALLA_PDD("fault generato dalla pdd"),
    ERRORE_INTERNO_PDD("errore interno pdd");

    private String value;

    DettaglioEsitoEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return this.value;
    }

    @Override
    public String toString() {
      return String.valueOf(this.value);
    }
    @JsonCreator
    public static DettaglioEsitoEnum fromValue(String text) {
      for (DettaglioEsitoEnum b : DettaglioEsitoEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(example = "ok", description = "")
  private DettaglioEsitoEnum dettaglioEsito = null;
  public enum ContestoEnum {
    APPLICATIVO("applicativo"),
    SISTEMA("sistema");

    private String value;

    ContestoEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return this.value;
    }

    @Override
    public String toString() {
      return String.valueOf(this.value);
    }
    @JsonCreator
    public static ContestoEnum fromValue(String text) {
      for (ContestoEnum b : ContestoEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(example = "applicativo", description = "")
  private ContestoEnum contesto = null;
  
  @Schema(example = "evento1", description = "")
  private String evento = null;
 /**
   * Get tipologia
   * @return tipologia
  **/
  @JsonProperty("tipologia")
  @Valid
  public String getTipologia() {
    if (this.tipologia == null) {
      return null;
    }
    return this.tipologia.getValue();
  }

  public void setTipologia(TipologiaEnum tipologia) {
    this.tipologia = tipologia;
  }

  public FiltroTransazione tipologia(TipologiaEnum tipologia) {
    this.tipologia = tipologia;
    return this;
  }

 /**
   * Get esito
   * @return esito
  **/
  @JsonProperty("esito")
  @Valid
  public String getEsito() {
    if (this.esito == null) {
      return null;
    }
    return this.esito.getValue();
  }

  public void setEsito(EsitoEnum esito) {
    this.esito = esito;
  }

  public FiltroTransazione esito(EsitoEnum esito) {
    this.esito = esito;
    return this;
  }

 /**
   * Get dettaglioEsito
   * @return dettaglioEsito
  **/
  @JsonProperty("dettaglioEsito")
  @Valid
  public String getDettaglioEsito() {
    if (this.dettaglioEsito == null) {
      return null;
    }
    return this.dettaglioEsito.getValue();
  }

  public void setDettaglioEsito(DettaglioEsitoEnum dettaglioEsito) {
    this.dettaglioEsito = dettaglioEsito;
  }

  public FiltroTransazione dettaglioEsito(DettaglioEsitoEnum dettaglioEsito) {
    this.dettaglioEsito = dettaglioEsito;
    return this;
  }

 /**
   * Get contesto
   * @return contesto
  **/
  @JsonProperty("contesto")
  @Valid
  public String getContesto() {
    if (this.contesto == null) {
      return null;
    }
    return this.contesto.getValue();
  }

  public void setContesto(ContestoEnum contesto) {
    this.contesto = contesto;
  }

  public FiltroTransazione contesto(ContestoEnum contesto) {
    this.contesto = contesto;
    return this;
  }

 /**
   * Get evento
   * @return evento
  **/
  @JsonProperty("evento")
  @Valid
  public String getEvento() {
    return this.evento;
  }

  public void setEvento(String evento) {
    this.evento = evento;
  }

  public FiltroTransazione evento(String evento) {
    this.evento = evento;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FiltroTransazione {\n");
    
    sb.append("    tipologia: ").append(FiltroTransazione.toIndentedString(this.tipologia)).append("\n");
    sb.append("    esito: ").append(FiltroTransazione.toIndentedString(this.esito)).append("\n");
    sb.append("    dettaglioEsito: ").append(FiltroTransazione.toIndentedString(this.dettaglioEsito)).append("\n");
    sb.append("    contesto: ").append(FiltroTransazione.toIndentedString(this.contesto)).append("\n");
    sb.append("    evento: ").append(FiltroTransazione.toIndentedString(this.evento)).append("\n");
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
