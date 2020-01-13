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
import org.openspcoop2.utils.service.beans.DiagnosticoSeveritaEnum;
import org.openspcoop2.core.monitor.rs.server.model.EsitoTransazioneSimpleSearchEnum;
import org.openspcoop2.core.monitor.rs.server.model.Evento;
import org.openspcoop2.core.monitor.rs.server.model.FiltroRicercaRuoloTransazioneEnum;
import org.openspcoop2.core.monitor.rs.server.model.ListaEventi;
import org.openspcoop2.core.monitor.rs.server.model.ListaTransazioni;
import org.openspcoop2.core.monitor.rs.server.model.Problem;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.core.monitor.rs.server.model.RicercaIdApplicativo;
import org.openspcoop2.core.monitor.rs.server.model.RicercaIntervalloTemporale;
import org.openspcoop2.core.monitor.rs.server.model.TipoMessaggioEnum;
import org.openspcoop2.utils.service.beans.TransazioneExt;
import java.util.UUID;

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
public interface MonitoraggioApi  {

    /**
     * Ricerca di Eventi
     *
     * Questa operazione consente di effettuare una ricerca nell'archivio degli eventi specificando i criteri di filtraggio
     *
     */
    @GET
    @Path("/monitoraggio/eventi")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Ricerca di Eventi", tags={ "Monitoraggio" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Ricerca eventi completata con successo", content = @Content(schema = @Schema(implementation = ListaEventi.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ListaEventi findAllEventi(@QueryParam("data_inizio") @NotNull DateTime dataInizio, @QueryParam("data_fine") @NotNull DateTime dataFine, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit, @QueryParam("severita") DiagnosticoSeveritaEnum severita, @QueryParam("tipo") String tipo, @QueryParam("codice") String codice, @QueryParam("origine") String origine, @QueryParam("ricerca_esatta") Boolean ricercaEsatta, @QueryParam("case_sensitive") Boolean caseSensitive);

    /**
     * Ricerca completa delle transazioni per intervallo temporale
     *
     * Permette di recuperare i dettagli delle transazioni, gestite su GovWay, limitando i risultati di ricerca ad un intervallo di date
     *
     */
    @POST
    @Path("/monitoraggio/transazioni")
    @Consumes({ "application/json" })
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Ricerca completa delle transazioni per intervallo temporale", tags={ "Monitoraggio" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "ricerca completata con successo", content = @Content(schema = @Schema(implementation = ListaTransazioni.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ListaTransazioni findAllTransazioniByFullSearch(@Valid RicercaIntervalloTemporale body, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto);

    /**
     * Ricerca completa delle transazioni in base all'identificativo applicativo
     *
     * Permette di recuperare i dettagli delle transazioni, gestite su GovWay, ricercandole in base all'identificativo applicativo e i parametri di filtro completi
     *
     */
    @POST
    @Path("/monitoraggio/transazioni/id_applicativo")
    @Consumes({ "application/json" })
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Ricerca completa delle transazioni in base all'identificativo applicativo", tags={ "Monitoraggio" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "ricerca completata con successo", content = @Content(schema = @Schema(implementation = ListaTransazioni.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ListaTransazioni findAllTransazioniByIdApplicativoFullSearch(@Valid RicercaIdApplicativo body, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto);

    /**
     * Ricerca semplificata delle transazioni in base all'identificativo applicativo
     *
     * Permette di recuperare i dettagli delle transazioni, gestite su GovWay, ricercandole in base all'identificativo applicativo e i parametri di filtro di uso più comune
     *
     */
    @GET
    @Path("/monitoraggio/transazioni/id_applicativo")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Ricerca semplificata delle transazioni in base all'identificativo applicativo", tags={ "Monitoraggio" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "ricerca completata con successo", content = @Content(schema = @Schema(implementation = ListaTransazioni.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ListaTransazioni findAllTransazioniByIdApplicativoSimpleSearch(@QueryParam("data_inizio") @NotNull DateTime dataInizio, @QueryParam("data_fine") @NotNull DateTime dataFine, @QueryParam("tipo") @NotNull FiltroRicercaRuoloTransazioneEnum tipo, @QueryParam("id_applicativo") @NotNull String idApplicativo, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit, @QueryParam("sort") String sort, @QueryParam("id_cluster") String idCluster, @QueryParam("soggetto_remoto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggettoRemoto, @QueryParam("soggetto_erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggettoErogatore, @QueryParam("tag") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String tag, @QueryParam("nome_servizio") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nomeServizio, @QueryParam("tipo_servizio") @Size(max=20) String tipoServizio, @QueryParam("versione_servizio") @Min(1) Integer versioneServizio, @QueryParam("azione") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String azione, @QueryParam("esito") EsitoTransazioneSimpleSearchEnum esito, @QueryParam("ricerca_esatta") Boolean ricercaEsatta, @QueryParam("case_sensitive") Boolean caseSensitive);

    /**
     * Ricerca semplificata delle transazioni in base all'identificativo messaggio
     *
     * Permette di recuperare i dettagli delle transazioni, gestite su GovWay, ricercandole in base all'identificativo messaggio
     *
     */
    @GET
    @Path("/monitoraggio/transazioni/id_messaggio")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Ricerca semplificata delle transazioni in base all'identificativo messaggio", tags={ "Monitoraggio" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "dettaglio transazione restituito correttamente", content = @Content(schema = @Schema(implementation = ListaTransazioni.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ListaTransazioni findAllTransazioniByIdMessaggio(@QueryParam("tipo_messaggio") @NotNull TipoMessaggioEnum tipoMessaggio, @QueryParam("id") @NotNull String id, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit, @QueryParam("sort") String sort);

    /**
     * Ricerca semplificata delle transazioni in base ai parametri di uso più comune
     *
     * Permette di recuperare i dettagli delle transazioni, gestite su GovWay, ricercandole in base a parametri di filtro di uso più comune
     *
     */
    @GET
    @Path("/monitoraggio/transazioni")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Ricerca semplificata delle transazioni in base ai parametri di uso più comune", tags={ "Monitoraggio" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "ricerca completata con successo", content = @Content(schema = @Schema(implementation = ListaTransazioni.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ListaTransazioni findAllTransazioniBySimpleSearch(@QueryParam("data_inizio") @NotNull DateTime dataInizio, @QueryParam("data_fine") @NotNull DateTime dataFine, @QueryParam("tipo") @NotNull FiltroRicercaRuoloTransazioneEnum tipo, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggetto, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit, @QueryParam("sort") String sort, @QueryParam("id_cluster") String idCluster, @QueryParam("soggetto_remoto") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggettoRemoto, @QueryParam("soggetto_erogatore") @Pattern(regexp="^[0-9A-Za-z]+$") @Size(max=255) String soggettoErogatore, @QueryParam("tag") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String tag, @QueryParam("nome_servizio") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String nomeServizio, @QueryParam("tipo_servizio") @Size(max=20) String tipoServizio, @QueryParam("versione_servizio") @Min(1) Integer versioneServizio, @QueryParam("azione") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String azione, @QueryParam("esito") EsitoTransazioneSimpleSearchEnum esito);

    /**
     * Dettaglio di un evento
     *
     * Permette di recuperare il dettaglio di un evento in base al suo identificativo
     *
     */
    @GET
    @Path("/monitoraggio/eventi/{id}")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Dettaglio di un evento", tags={ "Monitoraggio" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Recupero evento completato con successo", content = @Content(schema = @Schema(implementation = Evento.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public Evento getEvento(@PathParam("id") Long id);

    /**
     * Dettaglio della transazione
     *
     * Questa operazione consente di ottenere il dettaglio di una transazione
     *
     */
    @GET
    @Path("/monitoraggio/transazioni/{id}")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Dettaglio della transazione", tags={ "Monitoraggio" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "dettaglio transazione restituito correttamente", content = @Content(schema = @Schema(implementation = TransazioneExt.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public TransazioneExt getTransazione(@PathParam("id") UUID id);
}
