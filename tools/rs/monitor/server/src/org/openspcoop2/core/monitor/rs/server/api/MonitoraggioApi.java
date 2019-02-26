package org.openspcoop2.core.monitor.rs.server.api;

import org.joda.time.DateTime;
import org.openspcoop2.core.monitor.rs.server.model.Problem;
import org.openspcoop2.core.monitor.rs.server.model.RisultatoRicercaTransazioni;
import org.openspcoop2.core.monitor.rs.server.model.TransazioneDetail;
import org.openspcoop2.core.monitor.rs.server.model.TransazioneSearchFilter;

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
     * Ricerca semplificata delle transazioni in base ai parametri di uso più comune
     *
     * Permette di recuperare i dettagli delle transazioni, gestite su GovWay, ricercandole in base a parametri di filtro di uso più comune
     *
     */
    @GET
    @Path("/monitoraggio/transazioni")
    @Produces({ "application/json" })
    @Operation(summary = "Ricerca semplificata delle transazioni in base ai parametri di uso più comune", tags={ "Monitoraggio" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "ricerca completata con successo", content = @Content(schema = @Schema(implementation = RisultatoRicercaTransazioni.class))),
        @ApiResponse(responseCode = "400", description = "La richiesta inviata ha prodotto un errore", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public RisultatoRicercaTransazioni baseSearchTransaction(@QueryParam("profilo") String profilo, @QueryParam("dataDa") DateTime dataDa, @QueryParam("dataA") DateTime dataA, @QueryParam("soggettoLocale") String soggettoLocale, @QueryParam("soggettoRemoto") String soggettoRemoto, @QueryParam("servizio") String servizio, @QueryParam("azione") String azione, @QueryParam("esito") String esito, @QueryParam("idApplicativo") String idApplicativo, @QueryParam("idMessaggio") String idMessaggio, @QueryParam("idT") String idT, @QueryParam("pagina") Integer pagina, @QueryParam("risultatiPerPagina") @Max(200) Integer risultatiPerPagina, @QueryParam("ordinamento") String ordinamento);

    /**
     * dettaglio della transazione
     *
     * Questa operazione consente di ottenere il dettaglio di una transazione
     *
     */
    @GET
    @Path("/monitoraggio/transazioni/{id}")
    @Produces({ "application/json" })
    @Operation(summary = "dettaglio della transazione", tags={ "Monitoraggio" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "dettaglio transazione restituito correttamente", content = @Content(schema = @Schema(implementation = TransazioneDetail.class))),
        @ApiResponse(responseCode = "400", description = "La richiesta inviata ha prodotto un errore", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Transazione non esistente") })
    public TransazioneDetail getTransazione(@PathParam("id") String id);

    /**
     * ricerca transazioni
     *
     * Questa operazione consente di effettuare una ricerca nell&#x27;archivio delle transazioni specificando i criteri di filtro
     *
     */
    @POST
    @Path("/transazioni/ricerca")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Operation(summary = "ricerca transazioni", tags={ "Monitoraggio" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "ricerca completata correttamente", content = @Content(schema = @Schema(implementation = Object.class))),
        @ApiResponse(responseCode = "400", description = "La richiesta inviata ha prodotto un errore", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public Object searchTransazioni(@Valid TransazioneSearchFilter body, @QueryParam("pagina") Integer pagina, @QueryParam("risultatiPerPagina") @Max(200) Integer risultatiPerPagina, @QueryParam("ordinamento") String ordinamento);
}
