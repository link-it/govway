/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.core.monitor.rs.server.api;

import org.joda.time.DateTime;
import org.openspcoop2.core.monitor.rs.server.model.EsitoTransazioneSimpleSearchEnum;
import org.openspcoop2.core.monitor.rs.server.model.FiltroRicercaRuoloTransazioneEnum;
import org.openspcoop2.core.monitor.rs.server.model.FormatoReportEnum;
import org.openspcoop2.core.monitor.rs.server.model.InfoImplementazioneApi;
import org.openspcoop2.core.monitor.rs.server.model.ListaRiepilogoApi;
import org.openspcoop2.core.monitor.rs.server.model.Problem;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.core.monitor.rs.server.model.RicercaConfigurazioneApi;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaAndamentoTemporale;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneApi;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneApplicativo;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneAzione;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneEsiti;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneSoggettoLocale;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneSoggettoRemoto;
import org.openspcoop2.core.monitor.rs.server.model.RicercaStatisticaDistribuzioneTokenInfo;
import org.openspcoop2.core.monitor.rs.server.model.Riepilogo;
import org.openspcoop2.core.monitor.rs.server.model.TipoInformazioneReportEnum;
import org.openspcoop2.core.monitor.rs.server.model.TipoReportEnum;
import org.openspcoop2.core.monitor.rs.server.model.TokenClaimEnum;
import org.openspcoop2.utils.service.beans.TransazioneRuoloEnum;
import org.openspcoop2.core.monitor.rs.server.model.UnitaTempoReportEnum;

import javax.ws.rs.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * GovWay Monitor API
 *
 * <p>Servizi per il monitoraggio di GovWay
 *
 */
@Path("/")
public interface ReportisticaApi  {

    /**
     * Recupera la configurazione di un servizio
     *
     * Consente di recuperare la configurazione di un servizio esportandola in formato csv
     *
     */
    @POST
    @Path("/reportistica/configurazione-api/esporta")
    @Consumes({ "application/json" })
    @Produces({ "text/csv", "application/problem+json" })
    @Operation(summary = "Recupera la configurazione di un servizio", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Report delle configurazioni generato correttamente", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] exportConfigurazioneApiByFullSearch(@Valid RicercaConfigurazioneApi body, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto);

    /**
     * Recupera la configurazione di un servizio
     *
     * Consente di recuperare la configurazione di un servizio esportandola in formato csv
     *
     */
    @GET
    @Path("/reportistica/configurazione-api/esporta")
    @Produces({ "text/csv", "application/problem+json" })
    @Operation(summary = "Recupera la configurazione di un servizio", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Report delle configurazioni generato correttamente", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] exportConfigurazioneApiBySimpleSearch(@QueryParam("tipo") @NotNull TransazioneRuoloEnum tipo, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("soggetto_remoto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggettoRemoto, @QueryParam("nome_servizio") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nomeServizio, @QueryParam("tipo_servizio") @Size(max=20) String tipoServizio, @QueryParam("versione_servizio") @Min(1) Integer versioneServizio);

    /**
     * Consente di recuperare l'elenco delle erogazioni o fruizioni che coinvolgono il soggetto scelto
     *
     * Ricerca le erogazioni e fruizioni registrate sul sistema
     *
     */
    @GET
    @Path("/reportistica/configurazione-api")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Consente di recuperare l'elenco delle erogazioni o fruizioni che coinvolgono il soggetto scelto", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Riepilogo ottenuto con successo", content = @Content(schema = @Schema(implementation = ListaRiepilogoApi.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ListaRiepilogoApi getConfigurazioneApi(@QueryParam("tipo") @NotNull TransazioneRuoloEnum tipo, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit);

    /**
     * Genera per mezzo di una ricerca articolata, un report statistico raggruppato per servizi
     *
     * Questa operazione consente di generare un report statistico raggrupato per servizi esportandolo nei formati più comuni 
     *
     */
    @POST
    @Path("/reportistica/analisi-statistica/distribuzione-api")
    @Consumes({ "application/json" })
    @Produces({ "text/csv", "application/pdf", "application/vnd.ms-excel", "text/xml", "application/json", "application/problem+json" })
    @Operation(summary = "Genera per mezzo di una ricerca articolata, un report statistico raggruppato per servizi", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Report statistico generato correttamente", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] getReportDistribuzioneApiByFullSearch(@Valid RicercaStatisticaDistribuzioneApi body, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto);

    /**
     * Genera un report statistico organizzato per API utilizzando una ricerca semplice
     *
     * Questa operazione consente di generare per mezzo di una ricerca semplice, un report statistico organizzato per API esportandolo nei formati più comuni
     *
     */
    @GET
    @Path("/reportistica/analisi-statistica/distribuzione-api")
    @Produces({ "text/csv", "application/pdf", "application/vnd.ms-excel", "text/xml", "application/json", "application/problem+json" })
    @Operation(summary = "Genera un report statistico organizzato per API utilizzando una ricerca semplice", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Report statistico generato correttamente", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] getReportDistribuzioneApiBySimpleSearch(@QueryParam("data_inizio") @NotNull DateTime dataInizio, @QueryParam("data_fine") @NotNull DateTime dataFine, @QueryParam("tipo") @NotNull FiltroRicercaRuoloTransazioneEnum tipo, @QueryParam("formato_report") @NotNull FormatoReportEnum formatoReport, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("soggetto_remoto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggettoRemoto, @QueryParam("tag") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String tag, @QueryParam("esito") EsitoTransazioneSimpleSearchEnum esito, @QueryParam("unita_tempo") UnitaTempoReportEnum unitaTempo, @QueryParam("tipo_report") TipoReportEnum tipoReport, @QueryParam("tipo_informazione_report") TipoInformazioneReportEnum tipoInformazioneReport);

    /**
     * Genera un report statistico per applicativo utilizzando una ricerca articolata
     *
     * Questa operazione consente di generare un report statistico raggruppato per applicativo ed esportandolo nei formati più comuni 
     *
     */
    @POST
    @Path("/reportistica/analisi-statistica/distribuzione-applicativo")
    @Consumes({ "application/json" })
    @Produces({ "text/csv", "application/pdf", "application/vnd.ms-excel", "text/xml", "application/json", "application/problem+json" })
    @Operation(summary = "Genera un report statistico per applicativo utilizzando una ricerca articolata", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Report statistico generato correttamente", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] getReportDistribuzioneApplicativoByFullSearch(@Valid RicercaStatisticaDistribuzioneApplicativo body, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto);

    /**
     * Genera un report statistico organizzato per Applicativi utilizzando una ricerca semplice
     *
     * Questa operazione consente di generare per mezzo di una ricerca semplice, un report statistico per applicativo esportandolo nei formati più comuni
     *
     */
    @GET
    @Path("/reportistica/analisi-statistica/distribuzione-applicativo")
    @Produces({ "text/csv", "application/pdf", "application/vnd.ms-excel", "text/xml", "application/json", "application/problem+json" })
    @Operation(summary = "Genera un report statistico organizzato per Applicativi utilizzando una ricerca semplice", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Report statistico generato correttamente", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] getReportDistribuzioneApplicativoBySimpleSearch(@QueryParam("data_inizio") @NotNull DateTime dataInizio, @QueryParam("data_fine") @NotNull DateTime dataFine, @QueryParam("tipo") @NotNull FiltroRicercaRuoloTransazioneEnum tipo, @QueryParam("formato_report") @NotNull FormatoReportEnum formatoReport, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("soggetto_remoto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggettoRemoto, @QueryParam("soggetto_erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggettoErogatore, @QueryParam("tag") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String tag, @QueryParam("nome_servizio") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nomeServizio, @QueryParam("tipo_servizio") @Size(max=20) String tipoServizio, @QueryParam("versione_servizio") @Min(1) Integer versioneServizio, @QueryParam("azione") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String azione, @QueryParam("esito") EsitoTransazioneSimpleSearchEnum esito, @QueryParam("unita_tempo") UnitaTempoReportEnum unitaTempo, @QueryParam("tipo_report") TipoReportEnum tipoReport, @QueryParam("tipo_informazione_report") TipoInformazioneReportEnum tipoInformazioneReport);

    /**
     * Genera un report statistico distribuito per azione utilizzando una ricerca articolata
     *
     * Questa operazione consente di generare un report statistico raggruppato distribuito per azione ed esportandolo nei formati più comuni 
     *
     */
    @POST
    @Path("/reportistica/analisi-statistica/distribuzione-azione")
    @Consumes({ "application/json" })
    @Produces({ "text/csv", "application/pdf", "application/vnd.ms-excel", "text/xml", "application/json", "application/problem+json" })
    @Operation(summary = "Genera un report statistico distribuito per azione utilizzando una ricerca articolata", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Report statistico generato correttamente", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] getReportDistribuzioneAzioneByFullSearch(@Valid RicercaStatisticaDistribuzioneAzione body, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto);

    /**
     * Genera un report statistico organizzato per Azioni utilizzando una ricerca semplice
     *
     * Questa operazione consente di generare per mezzo di una ricerca semplice, un report statistico per azione esportandolo nei formati più comuni
     *
     */
    @GET
    @Path("/reportistica/analisi-statistica/distribuzione-azione")
    @Produces({ "text/csv", "application/pdf", "application/vnd.ms-excel", "text/xml", "application/json", "application/problem+json" })
    @Operation(summary = "Genera un report statistico organizzato per Azioni utilizzando una ricerca semplice", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Report statistico generato correttamente", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] getReportDistribuzioneAzioneBySimpleSearch(@QueryParam("data_inizio") @NotNull DateTime dataInizio, @QueryParam("data_fine") @NotNull DateTime dataFine, @QueryParam("tipo") @NotNull FiltroRicercaRuoloTransazioneEnum tipo, @QueryParam("formato_report") @NotNull FormatoReportEnum formatoReport, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("soggetto_remoto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggettoRemoto, @QueryParam("soggetto_erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggettoErogatore, @QueryParam("tag") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String tag, @QueryParam("nome_servizio") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nomeServizio, @QueryParam("tipo_servizio") @Size(max=20) String tipoServizio, @QueryParam("versione_servizio") @Min(1) Integer versioneServizio, @QueryParam("esito") EsitoTransazioneSimpleSearchEnum esito, @QueryParam("unita_tempo") UnitaTempoReportEnum unitaTempo, @QueryParam("tipo_report") TipoReportEnum tipoReport, @QueryParam("tipo_informazione_report") TipoInformazioneReportEnum tipoInformazioneReport);

    /**
     * Genera un report statistico per andamento esiti per mezzo di una ricerca articolata
     *
     * Questa operazione consente di generare un report statistico per andamento esiti esportandolo nei formati più comuni 
     *
     */
    @POST
    @Path("/reportistica/analisi-statistica/distribuzione-esiti")
    @Consumes({ "application/json" })
    @Produces({ "text/csv", "application/pdf", "application/vnd.ms-excel", "text/xml", "application/json", "application/problem+json" })
    @Operation(summary = "Genera un report statistico per andamento esiti per mezzo di una ricerca articolata", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Report statistico generato correttamente", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] getReportDistribuzioneEsitiByFullSearch(@Valid RicercaStatisticaDistribuzioneEsiti body, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto);

    /**
     * Genera un report statistico per andamento esiti per mezzo di una ricerca semplice
     *
     * Questa operazione consente di generare per mezzo di una ricerca semplice, un report statistico per andamento esiti esportandolo nei formati più comuni
     *
     */
    @GET
    @Path("/reportistica/analisi-statistica/distribuzione-esiti")
    @Produces({ "text/csv", "application/pdf", "application/vnd.ms-excel", "text/xml", "application/json", "application/problem+json" })
    @Operation(summary = "Genera un report statistico per andamento esiti per mezzo di una ricerca semplice", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Report statistico generato correttamente", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] getReportDistribuzioneEsitiBySimpleSearch(@QueryParam("data_inizio") @NotNull DateTime dataInizio, @QueryParam("data_fine") @NotNull DateTime dataFine, @QueryParam("tipo") @NotNull FiltroRicercaRuoloTransazioneEnum tipo, @QueryParam("formato_report") @NotNull FormatoReportEnum formatoReport, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("soggetto_remoto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggettoRemoto, @QueryParam("soggetto_erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggettoErogatore, @QueryParam("tag") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String tag, @QueryParam("nome_servizio") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nomeServizio, @QueryParam("tipo_servizio") @Size(max=20) String tipoServizio, @QueryParam("versione_servizio") @Min(1) Integer versioneServizio, @QueryParam("azione") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String azione, @QueryParam("unita_tempo") UnitaTempoReportEnum unitaTempo, @QueryParam("tipo_report") TipoReportEnum tipoReport, @QueryParam("tipo_informazione_report") TipoInformazioneReportEnum tipoInformazioneReport);

    /**
     * Genera un report statistico per identificativo autenticato utilizzando una ricerca articolata
     *
     * Questa operazione consente di generare un report statistico raggruppato per identificativo autenticato ed esportandolo nei formati più comuni 
     *
     */
    @POST
    @Path("/reportistica/analisi-statistica/distribuzione-id-autenticato")
    @Consumes({ "application/json" })
    @Produces({ "text/csv", "application/pdf", "application/vnd.ms-excel", "text/xml", "application/json", "application/problem+json" })
    @Operation(summary = "Genera un report statistico per identificativo autenticato utilizzando una ricerca articolata", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Report statistico generato correttamente", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] getReportDistribuzioneIdAutenticatoByFullSearch(@Valid RicercaStatisticaDistribuzioneApplicativo body, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto);

    /**
     * Genera un report statistico organizzato per Identificativo Autenticato utilizzando una ricerca semplice
     *
     * Questa operazione consente di generare per mezzo di una ricerca semplice, un report statistico per identificativo autenticato esportandolo nei formati più comuni
     *
     */
    @GET
    @Path("/reportistica/analisi-statistica/distribuzione-id-autenticato")
    @Produces({ "text/csv", "application/pdf", "application/vnd.ms-excel", "text/xml", "application/json", "application/problem+json" })
    @Operation(summary = "Genera un report statistico organizzato per Identificativo Autenticato utilizzando una ricerca semplice", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Report statistico generato correttamente", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] getReportDistribuzioneIdAutenticatoBySimpleSearch(@QueryParam("data_inizio") @NotNull DateTime dataInizio, @QueryParam("data_fine") @NotNull DateTime dataFine, @QueryParam("tipo") @NotNull FiltroRicercaRuoloTransazioneEnum tipo, @QueryParam("formato_report") @NotNull FormatoReportEnum formatoReport, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("soggetto_remoto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggettoRemoto, @QueryParam("soggetto_erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggettoErogatore, @QueryParam("tag") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String tag, @QueryParam("nome_servizio") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nomeServizio, @QueryParam("tipo_servizio") @Size(max=20) String tipoServizio, @QueryParam("versione_servizio") @Min(1) Integer versioneServizio, @QueryParam("azione") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String azione, @QueryParam("esito") EsitoTransazioneSimpleSearchEnum esito, @QueryParam("unita_tempo") UnitaTempoReportEnum unitaTempo, @QueryParam("tipo_report") TipoReportEnum tipoReport, @QueryParam("tipo_informazione_report") TipoInformazioneReportEnum tipoInformazioneReport);

    /**
     * Genera un report statistico organizzato per Indirizzi IP
     *
     * Consente di generare un report degli indirizzo IP
     *
     */
    @POST
    @Path("/reportistica/analisi-statistica/distribuzione-indirizzo-ip")
    @Consumes({ "application/json" })
    @Produces({ "text/csv", "application/pdf", "application/vnd.ms-excel", "text/xml", "application/json", "application/problem+json" })
    @Operation(summary = "Genera un report statistico organizzato per Indirizzi IP", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Report statistico generato correttamente", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] getReportDistribuzioneIndirizzoIPByFullSearch(@Valid RicercaStatisticaDistribuzioneApplicativo body, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto);

    /**
     * Genera un report statistico organizzato organizzato per Indirizzo IP utilizzando una ricerca semplice
     *
     * Consente di generare un report degli indirizzo IP per mezzo di una ricerca semplice
     *
     */
    @GET
    @Path("/reportistica/analisi-statistica/distribuzione-indirizzo-ip")
    @Produces({ "text/csv", "application/pdf", "application/vnd.ms-excel", "text/xml", "application/json", "application/problem+json" })
    @Operation(summary = "Genera un report statistico organizzato organizzato per Indirizzo IP utilizzando una ricerca semplice", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Report statistico generato correttamente", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] getReportDistribuzioneIndirizzoIPBySimpleSearch(@QueryParam("data_inizio") @NotNull DateTime dataInizio, @QueryParam("data_fine") @NotNull DateTime dataFine, @QueryParam("tipo") @NotNull FiltroRicercaRuoloTransazioneEnum tipo, @QueryParam("formato_report") @NotNull FormatoReportEnum formatoReport, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("soggetto_remoto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggettoRemoto, @QueryParam("soggetto_erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggettoErogatore, @QueryParam("tag") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String tag, @QueryParam("nome_servizio") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nomeServizio, @QueryParam("tipo_servizio") @Size(max=20) String tipoServizio, @QueryParam("versione_servizio") @Min(1) Integer versioneServizio, @QueryParam("azione") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String azione, @QueryParam("esito") EsitoTransazioneSimpleSearchEnum esito, @QueryParam("unita_tempo") UnitaTempoReportEnum unitaTempo, @QueryParam("tipo_report") TipoReportEnum tipoReport, @QueryParam("tipo_informazione_report") TipoInformazioneReportEnum tipoInformazioneReport);

    /**
     * Genera un report statistico raggruppato per soggetto locale per mezzo di una ricerca articolata
     *
     * Questa operazione consente di generare un report statistico raggruppato per soggetto locale esportandolo nei formati più comuni 
     *
     */
    @POST
    @Path("/reportistica/analisi-statistica/distribuzione-soggetto-locale")
    @Consumes({ "application/json" })
    @Produces({ "text/csv", "application/pdf", "application/vnd.ms-excel", "text/xml", "application/json", "application/problem+json" })
    @Operation(summary = "Genera un report statistico raggruppato per soggetto locale per mezzo di una ricerca articolata", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Report statistico generato correttamente", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] getReportDistribuzioneSoggettoLocaleByFullSearch(@Valid RicercaStatisticaDistribuzioneSoggettoLocale body, @QueryParam("profilo") ProfiloEnum profilo);

    /**
     * Genera un report statistico per soggetto locale per mezzo di una ricerca semplice
     *
     * Questa operazione consente di generare per mezzo di una ricerca semplice, un report statistico per soggetto locale esportandolo nei formati più comuni
     *
     */
    @GET
    @Path("/reportistica/analisi-statistica/distribuzione-soggetto-locale")
    @Produces({ "text/csv", "application/pdf", "application/vnd.ms-excel", "text/xml", "application/json", "application/problem+json" })
    @Operation(summary = "Genera un report statistico per soggetto locale per mezzo di una ricerca semplice", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Report statistico generato correttamente", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] getReportDistribuzioneSoggettoLocaleBySimpleSearch(@QueryParam("data_inizio") @NotNull DateTime dataInizio, @QueryParam("data_fine") @NotNull DateTime dataFine, @QueryParam("tipo") @NotNull FiltroRicercaRuoloTransazioneEnum tipo, @QueryParam("formato_report") @NotNull FormatoReportEnum formatoReport, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto_remoto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggettoRemoto, @QueryParam("soggetto_erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggettoErogatore, @QueryParam("tag") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String tag, @QueryParam("nome_servizio") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nomeServizio, @QueryParam("tipo_servizio") @Size(max=20) String tipoServizio, @QueryParam("versione_servizio") @Min(1) Integer versioneServizio, @QueryParam("azione") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String azione, @QueryParam("esito") EsitoTransazioneSimpleSearchEnum esito, @QueryParam("unita_tempo") UnitaTempoReportEnum unitaTempo, @QueryParam("tipo_report") TipoReportEnum tipoReport, @QueryParam("tipo_informazione_report") TipoInformazioneReportEnum tipoInformazioneReport);

    /**
     * Genera un report statistico raggruppato per soggetto remoto per mezzo di una ricerca articolata
     *
     * Questa operazione consente di generare un report statistico raggruppato per soggetto remoto esportandolo nei formati più comuni 
     *
     */
    @POST
    @Path("/reportistica/analisi-statistica/distribuzione-soggetto-remoto")
    @Consumes({ "application/json" })
    @Produces({ "text/csv", "application/pdf", "application/vnd.ms-excel", "text/xml", "application/json", "application/problem+json" })
    @Operation(summary = "Genera un report statistico raggruppato per soggetto remoto per mezzo di una ricerca articolata", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Report statistico generato correttamente", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] getReportDistribuzioneSoggettoRemotoByFullSearch(@Valid RicercaStatisticaDistribuzioneSoggettoRemoto body, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto);

    /**
     * Genera un report statistico per soggetto remoto per mezzo di una ricerca semplice
     *
     * Questa operazione consente di generare per mezzo di una ricerca semplice, un report statistico per soggetto remoto esportandolo nei formati più comuni
     *
     */
    @GET
    @Path("/reportistica/analisi-statistica/distribuzione-soggetto-remoto")
    @Produces({ "text/csv", "application/pdf", "application/vnd.ms-excel", "text/xml", "application/json", "application/problem+json" })
    @Operation(summary = "Genera un report statistico per soggetto remoto per mezzo di una ricerca semplice", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Report statistico generato correttamente", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] getReportDistribuzioneSoggettoRemotoBySimpleSearch(@QueryParam("data_inizio") @NotNull DateTime dataInizio, @QueryParam("data_fine") @NotNull DateTime dataFine, @QueryParam("tipo") @NotNull FiltroRicercaRuoloTransazioneEnum tipo, @QueryParam("formato_report") @NotNull FormatoReportEnum formatoReport, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("soggetto_erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggettoErogatore, @QueryParam("tag") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String tag, @QueryParam("nome_servizio") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nomeServizio, @QueryParam("tipo_servizio") @Size(max=20) String tipoServizio, @QueryParam("versione_servizio") @Min(1) Integer versioneServizio, @QueryParam("azione") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String azione, @QueryParam("esito") EsitoTransazioneSimpleSearchEnum esito, @QueryParam("unita_tempo") UnitaTempoReportEnum unitaTempo, @QueryParam("tipo_report") TipoReportEnum tipoReport, @QueryParam("tipo_informazione_report") TipoInformazioneReportEnum tipoInformazioneReport);

    /**
     * Genera un report statistico per andamento temporale per mezzo di una ricerca articolata
     *
     * Questa operazione consente di generare un report statistico per andamento temporale esportandolo nei formati più comuni 
     *
     */
    @POST
    @Path("/reportistica/analisi-statistica/distribuzione-temporale")
    @Consumes({ "application/json" })
    @Produces({ "text/csv", "application/pdf", "application/vnd.ms-excel", "text/xml", "application/json", "application/problem+json" })
    @Operation(summary = "Genera un report statistico per andamento temporale per mezzo di una ricerca articolata", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Report statistico generato correttamente", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] getReportDistribuzioneTemporaleByFullSearch(@Valid RicercaStatisticaAndamentoTemporale body, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto);

    /**
     * Genera un report statistico per andamento temporale per mezzo di una ricerca semplice
     *
     * Questa operazione consente di generare per mezzo di una ricerca semplice, un report statistico per andamento temporale esportandolo nei formati più comuni
     *
     */
    @GET
    @Path("/reportistica/analisi-statistica/distribuzione-temporale")
    @Produces({ "text/csv", "application/pdf", "application/vnd.ms-excel", "text/xml", "application/json", "application/problem+json" })
    @Operation(summary = "Genera un report statistico per andamento temporale per mezzo di una ricerca semplice", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Report statistico generato correttamente", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] getReportDistribuzioneTemporaleBySimpleSearch(@QueryParam("data_inizio") @NotNull DateTime dataInizio, @QueryParam("data_fine") @NotNull DateTime dataFine, @QueryParam("tipo") @NotNull FiltroRicercaRuoloTransazioneEnum tipo, @QueryParam("formato_report") @NotNull FormatoReportEnum formatoReport, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("soggetto_remoto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggettoRemoto, @QueryParam("soggetto_erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggettoErogatore, @QueryParam("tag") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String tag, @QueryParam("nome_servizio") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nomeServizio, @QueryParam("tipo_servizio") @Size(max=20) String tipoServizio, @QueryParam("versione_servizio") @Min(1) Integer versioneServizio, @QueryParam("azione") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String azione, @QueryParam("esito") EsitoTransazioneSimpleSearchEnum esito, @QueryParam("unita_tempo") UnitaTempoReportEnum unitaTempo, @QueryParam("tipo_report") TipoReportEnum tipoReport, @QueryParam("tipo_informazione_report") TipoInformazioneReportEnum tipoInformazioneReport);

    /**
     * Genera un report statistico organizzato per Token Info
     *
     * Consente di generare un report raggruppato secondo un claim del token
     *
     */
    @POST
    @Path("/reportistica/analisi-statistica/distribuzione-token-info")
    @Consumes({ "application/json" })
    @Produces({ "text/csv", "application/pdf", "application/vnd.ms-excel", "text/xml", "application/json", "application/problem+json" })
    @Operation(summary = "Genera un report statistico organizzato per Token Info", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Report statistico generato correttamente", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] getReportDistribuzioneTokenInfoByFullSearch(@Valid RicercaStatisticaDistribuzioneTokenInfo body, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto);

    /**
     * Genera un report statistico organizzato organizzato per Token Info utilizzando una ricerca semplice
     *
     * Consente di generare un report raggruppato secondo un claim del token per mezzo di una ricerca semplice
     *
     */
    @GET
    @Path("/reportistica/analisi-statistica/distribuzione-token-info")
    @Produces({ "text/csv", "application/pdf", "application/vnd.ms-excel", "text/xml", "application/json", "application/problem+json" })
    @Operation(summary = "Genera un report statistico organizzato organizzato per Token Info utilizzando una ricerca semplice", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Report statistico generato correttamente", content = @Content(schema = @Schema(implementation = byte[].class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public byte[] getReportDistribuzioneTokenInfoBySimpleSearch(@QueryParam("data_inizio") @NotNull DateTime dataInizio, @QueryParam("data_fine") @NotNull DateTime dataFine, @QueryParam("tipo") @NotNull FiltroRicercaRuoloTransazioneEnum tipo, @QueryParam("formato_report") @NotNull FormatoReportEnum formatoReport, @QueryParam("claim") @NotNull TokenClaimEnum claim, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("soggetto_remoto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggettoRemoto, @QueryParam("soggetto_erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggettoErogatore, @QueryParam("tag") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String tag, @QueryParam("nome_servizio") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nomeServizio, @QueryParam("tipo_servizio") @Size(max=20) String tipoServizio, @QueryParam("versione_servizio") @Min(1) Integer versioneServizio, @QueryParam("azione") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String azione, @QueryParam("esito") EsitoTransazioneSimpleSearchEnum esito, @QueryParam("unita_tempo") UnitaTempoReportEnum unitaTempo, @QueryParam("tipo_report") TipoReportEnum tipoReport, @QueryParam("tipo_informazione_report") TipoInformazioneReportEnum tipoInformazioneReport);

    /**
     * Ottieni le informazioni generali sulle implementazioni di un Api
     *
     * Recupera le informazioni generali di una Api, come il nome e il numero di erogazioni e fruizioni registrate per esso
     *
     */
    @GET
    @Path("/reportistica/configurazione-api/riepilogo/api")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Ottieni le informazioni generali sulle implementazioni di un Api", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Informazioni sulla API ottenute con successo", content = @Content(schema = @Schema(implementation = InfoImplementazioneApi.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public InfoImplementazioneApi getRiepilogoApi(@QueryParam("tipo") @NotNull TransazioneRuoloEnum tipo, @QueryParam("nome_servizio") @NotNull @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nomeServizio, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("soggetto_remoto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggettoRemoto, @QueryParam("tipo_servizio") @Size(max=20) String tipoServizio, @QueryParam("versione_servizio") @Min(1) Integer versioneServizio);

    /**
     * Ottieni le informazioni generali sulle api e servizi di un soggetto
     *
     * Restituisce il numero e tipo di api che coinvolgono il soggetto, e un riepilogo del registro circa i soggetti e gli applicativi registrati.
     *
     */
    @GET
    @Path("/reportistica/configurazione-api/riepilogo")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Ottieni le informazioni generali sulle api e servizi di un soggetto", tags={ "Reportistica" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Riepilogo ottenuto con successo", content = @Content(schema = @Schema(implementation = Riepilogo.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public Riepilogo getRiepologoConfigurazioni(@QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto);
}
