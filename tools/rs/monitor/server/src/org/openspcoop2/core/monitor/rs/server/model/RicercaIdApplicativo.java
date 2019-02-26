package org.openspcoop2.core.monitor.rs.server.model;

import org.openspcoop2.core.monitor.rs.server.model.FiltroTemporale;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.Valid;

public class RicercaIdApplicativo extends FiltroTemporale {
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
  
  @Schema(description = "")
  private Boolean ricercaEsatta = null;
  
  @Schema(description = "")
  private Boolean caseSensitive = null;
  
  @Schema(example = "abc123", description = "")
  private String idApplicativo = null;
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

  public RicercaIdApplicativo protocollo(ProtocolloEnum protocollo) {
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

  public RicercaIdApplicativo soggettoLocale(String soggettoLocale) {
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

  public RicercaIdApplicativo soggettoRemoto(String soggettoRemoto) {
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

  public RicercaIdApplicativo applicativo(String applicativo) {
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

  public RicercaIdApplicativo servizio(String servizio) {
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

  public RicercaIdApplicativo azione(String azione) {
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

  public RicercaIdApplicativo tipologia(TipologiaEnum tipologia) {
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

  public RicercaIdApplicativo esito(EsitoEnum esito) {
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

  public RicercaIdApplicativo dettaglioEsito(DettaglioEsitoEnum dettaglioEsito) {
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

  public RicercaIdApplicativo contesto(ContestoEnum contesto) {
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

  public RicercaIdApplicativo evento(String evento) {
    this.evento = evento;
    return this;
  }

 /**
   * Get ricercaEsatta
   * @return ricercaEsatta
  **/
  @JsonProperty("ricercaEsatta")
  @Valid
  public Boolean isRicercaEsatta() {
    return this.ricercaEsatta;
  }

  public void setRicercaEsatta(Boolean ricercaEsatta) {
    this.ricercaEsatta = ricercaEsatta;
  }

  public RicercaIdApplicativo ricercaEsatta(Boolean ricercaEsatta) {
    this.ricercaEsatta = ricercaEsatta;
    return this;
  }

 /**
   * Get caseSensitive
   * @return caseSensitive
  **/
  @JsonProperty("caseSensitive")
  @Valid
  public Boolean isCaseSensitive() {
    return this.caseSensitive;
  }

  public void setCaseSensitive(Boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
  }

  public RicercaIdApplicativo caseSensitive(Boolean caseSensitive) {
    this.caseSensitive = caseSensitive;
    return this;
  }

 /**
   * Get idApplicativo
   * @return idApplicativo
  **/
  @JsonProperty("idApplicativo")
  @Valid
  public String getIdApplicativo() {
    return this.idApplicativo;
  }

  public void setIdApplicativo(String idApplicativo) {
    this.idApplicativo = idApplicativo;
  }

  public RicercaIdApplicativo idApplicativo(String idApplicativo) {
    this.idApplicativo = idApplicativo;
    return this;
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RicercaIdApplicativo {\n");
    sb.append("    ").append(RicercaIdApplicativo.toIndentedString(super.toString())).append("\n");
    sb.append("    protocollo: ").append(RicercaIdApplicativo.toIndentedString(this.protocollo)).append("\n");
    sb.append("    soggettoLocale: ").append(RicercaIdApplicativo.toIndentedString(this.soggettoLocale)).append("\n");
    sb.append("    soggettoRemoto: ").append(RicercaIdApplicativo.toIndentedString(this.soggettoRemoto)).append("\n");
    sb.append("    applicativo: ").append(RicercaIdApplicativo.toIndentedString(this.applicativo)).append("\n");
    sb.append("    servizio: ").append(RicercaIdApplicativo.toIndentedString(this.servizio)).append("\n");
    sb.append("    azione: ").append(RicercaIdApplicativo.toIndentedString(this.azione)).append("\n");
    sb.append("    tipologia: ").append(RicercaIdApplicativo.toIndentedString(this.tipologia)).append("\n");
    sb.append("    esito: ").append(RicercaIdApplicativo.toIndentedString(this.esito)).append("\n");
    sb.append("    dettaglioEsito: ").append(RicercaIdApplicativo.toIndentedString(this.dettaglioEsito)).append("\n");
    sb.append("    contesto: ").append(RicercaIdApplicativo.toIndentedString(this.contesto)).append("\n");
    sb.append("    evento: ").append(RicercaIdApplicativo.toIndentedString(this.evento)).append("\n");
    sb.append("    ricercaEsatta: ").append(RicercaIdApplicativo.toIndentedString(this.ricercaEsatta)).append("\n");
    sb.append("    caseSensitive: ").append(RicercaIdApplicativo.toIndentedString(this.caseSensitive)).append("\n");
    sb.append("    idApplicativo: ").append(RicercaIdApplicativo.toIndentedString(this.idApplicativo)).append("\n");
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
