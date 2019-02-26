package org.openspcoop2.core.monitor.rs.server.model;

import org.openspcoop2.core.monitor.rs.server.model.FiltroTemporale;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.Valid;

public class RicercaIntervalloTemporale extends FiltroTemporale {
  public enum ProtocolloEnum {
    SPCOOP("spcoop"),
    TRASPARENTE("trasparente"),
    SDI("sdi"),
    EDELIVERY("edelivery");

    private String value;

    ProtocolloEnum(String value) {
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
    public static ProtocolloEnum fromValue(String text) {
      for (ProtocolloEnum b : ProtocolloEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }  
  @Schema(example = "trasparente", description = "")
  private ProtocolloEnum protocollo = null;
  
  @Schema(example = "EnteInterno", description = "")
  private String soggettoLocale = null;
  
  @Schema(example = "EnteEsterno", description = "")
  private String soggettoRemoto = null;
  
  @Schema(example = "applicativo1", description = "")
  private String applicativo = null;
  
  @Schema(example = "servizio1", description = "")
  private String servizio = null;
  
  @Schema(example = "azione1", description = "")
  private String azione = null;
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
   * Get protocollo
   * @return protocollo
  **/
  @JsonProperty("protocollo")
  @Valid
  public String getProtocollo() {
    if (this.protocollo == null) {
      return null;
    }
    return this.protocollo.getValue();
  }

  public void setProtocollo(ProtocolloEnum protocollo) {
    this.protocollo = protocollo;
  }

  public RicercaIntervalloTemporale protocollo(ProtocolloEnum protocollo) {
    this.protocollo = protocollo;
    return this;
  }

 /**
   * Get soggettoLocale
   * @return soggettoLocale
  **/
  @JsonProperty("soggettoLocale")
  @Valid
  public String getSoggettoLocale() {
    return this.soggettoLocale;
  }

  public void setSoggettoLocale(String soggettoLocale) {
    this.soggettoLocale = soggettoLocale;
  }

  public RicercaIntervalloTemporale soggettoLocale(String soggettoLocale) {
    this.soggettoLocale = soggettoLocale;
    return this;
  }

 /**
   * Get soggettoRemoto
   * @return soggettoRemoto
  **/
  @JsonProperty("soggettoRemoto")
  @Valid
  public String getSoggettoRemoto() {
    return this.soggettoRemoto;
  }

  public void setSoggettoRemoto(String soggettoRemoto) {
    this.soggettoRemoto = soggettoRemoto;
  }

  public RicercaIntervalloTemporale soggettoRemoto(String soggettoRemoto) {
    this.soggettoRemoto = soggettoRemoto;
    return this;
  }

 /**
   * Get applicativo
   * @return applicativo
  **/
  @JsonProperty("applicativo")
  @Valid
  public String getApplicativo() {
    return this.applicativo;
  }

  public void setApplicativo(String applicativo) {
    this.applicativo = applicativo;
  }

  public RicercaIntervalloTemporale applicativo(String applicativo) {
    this.applicativo = applicativo;
    return this;
  }

 /**
   * Get servizio
   * @return servizio
  **/
  @JsonProperty("servizio")
  @Valid
  public String getServizio() {
    return this.servizio;
  }

  public void setServizio(String servizio) {
    this.servizio = servizio;
  }

  public RicercaIntervalloTemporale servizio(String servizio) {
    this.servizio = servizio;
    return this;
  }

 /**
   * Get azione
   * @return azione
  **/
  @JsonProperty("azione")
  @Valid
  public String getAzione() {
    return this.azione;
  }

  public void setAzione(String azione) {
    this.azione = azione;
  }

  public RicercaIntervalloTemporale azione(String azione) {
    this.azione = azione;
    return this;
  }

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

  public RicercaIntervalloTemporale tipologia(TipologiaEnum tipologia) {
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

  public RicercaIntervalloTemporale esito(EsitoEnum esito) {
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

  public RicercaIntervalloTemporale dettaglioEsito(DettaglioEsitoEnum dettaglioEsito) {
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

  public RicercaIntervalloTemporale contesto(ContestoEnum contesto) {
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

  public RicercaIntervalloTemporale evento(String evento) {
    this.evento = evento;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RicercaIntervalloTemporale {\n");
    sb.append("    ").append(RicercaIntervalloTemporale.toIndentedString(super.toString())).append("\n");
    sb.append("    protocollo: ").append(RicercaIntervalloTemporale.toIndentedString(this.protocollo)).append("\n");
    sb.append("    soggettoLocale: ").append(RicercaIntervalloTemporale.toIndentedString(this.soggettoLocale)).append("\n");
    sb.append("    soggettoRemoto: ").append(RicercaIntervalloTemporale.toIndentedString(this.soggettoRemoto)).append("\n");
    sb.append("    applicativo: ").append(RicercaIntervalloTemporale.toIndentedString(this.applicativo)).append("\n");
    sb.append("    servizio: ").append(RicercaIntervalloTemporale.toIndentedString(this.servizio)).append("\n");
    sb.append("    azione: ").append(RicercaIntervalloTemporale.toIndentedString(this.azione)).append("\n");
    sb.append("    tipologia: ").append(RicercaIntervalloTemporale.toIndentedString(this.tipologia)).append("\n");
    sb.append("    esito: ").append(RicercaIntervalloTemporale.toIndentedString(this.esito)).append("\n");
    sb.append("    dettaglioEsito: ").append(RicercaIntervalloTemporale.toIndentedString(this.dettaglioEsito)).append("\n");
    sb.append("    contesto: ").append(RicercaIntervalloTemporale.toIndentedString(this.contesto)).append("\n");
    sb.append("    evento: ").append(RicercaIntervalloTemporale.toIndentedString(this.evento)).append("\n");
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
