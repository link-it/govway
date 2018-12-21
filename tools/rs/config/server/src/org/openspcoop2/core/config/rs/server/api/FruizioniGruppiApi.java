package org.openspcoop2.core.config.rs.server.api;

import org.openspcoop2.core.config.rs.server.model.Gruppo;
import org.openspcoop2.core.config.rs.server.model.GruppoAzioni;
import org.openspcoop2.core.config.rs.server.model.GruppoNome;
import org.openspcoop2.core.config.rs.server.model.ListaGruppi;
import org.openspcoop2.core.config.rs.server.model.Problem;
import org.openspcoop2.core.config.rs.server.model.ProfiloEnum;

import javax.ws.rs.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * GovWay Config API
 *
 * <p>Servizi per la configurazione di GovWay
 *
 */
@Path("/")
public interface FruizioniGruppiApi  {

    /**
     * Aggiunta di azioni o risorse dell&#x27;API al gruppo
     *
     * Questa operazione consente di aggiungere azioni o risorse dell&#x27;API al gruppo
     *
     */
    @POST
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/gruppi/{nome_gruppo}/azioni")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Aggiunta di azioni o risorse dell'API al gruppo", tags={ "fruizioni-gruppi" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Azioni aggiunte con con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void addAzioni(@Valid GruppoAzioni body, @PathParam("erogatore") String erogatore, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_gruppo") @Size(max=255) String nomeGruppo, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Creazione di un gruppo di azioni o risorse dell&#x27;API fruita
     *
     * Questa operazione consente di creare un gruppo di azioni o risorse dell&#x27;API fruita
     *
     */
    @POST
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/gruppi")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Creazione di un gruppo di azioni o risorse dell'API fruita", tags={ "fruizioni-gruppi" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Gruppo creato con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void create(@Valid Gruppo body, @PathParam("erogatore") String erogatore, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Elimina il gruppo identificato dal nome
     *
     * Questa operazione consente di eliminare il gruppo identificato dal nome
     *
     */
    @DELETE
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/gruppi/{nome_gruppo}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina il gruppo identificato dal nome", tags={ "fruizioni-gruppi" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Gruppo eliminate con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void delete(@PathParam("erogatore") String erogatore, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_gruppo") @Size(max=255) String nomeGruppo, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Elimina l&#x27;azione o la risorsa dell&#x27;API associatia al gruppo
     *
     * Questa operazione consente di eliminare l&#x27;azione o la risorsa dell&#x27;API associata al gruppo
     *
     */
    @DELETE
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/gruppi/{nome_gruppo}/azioni/{nome_azione}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina l'azione o la risorsa dell'API associatia al gruppo", tags={ "fruizioni-gruppi" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Azione eliminata con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void deleteAzione(@PathParam("erogatore") String erogatore, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_gruppo") @Size(max=255) String nomeGruppo, @PathParam("nome_azione") String nomeAzione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Ricerca i gruppi in cui sono stati classificate le azioni o le risorse dell&#x27;API
     *
     * Elenca i gruppi in cui sono stati classificate le azioni o le risorse dell&#x27;API
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/gruppi")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Ricerca i gruppi in cui sono stati classificate le azioni o le risorse dell'API", tags={ "fruizioni-gruppi" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Ricerca eseguita correttamente", content = @Content(schema = @Schema(implementation = ListaGruppi.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ListaGruppi findAll(@PathParam("erogatore") String erogatore, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset, @QueryParam("azione") String azione);

    /**
     * Restituisce azioni/risorse associate al gruppo identificato dal nome
     *
     * Questa operazione consente di ottenere le azioni associate al gruppo identificato dal nome
     *
     */
    @GET
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/gruppi/{nome_gruppo}")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce azioni/risorse associate al gruppo identificato dal nome", tags={ "fruizioni-gruppi" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Elenco di azioni/risorse restituite con successo", content = @Content(schema = @Schema(implementation = GruppoAzioni.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public GruppoAzioni getAzioni(@PathParam("erogatore") String erogatore, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_gruppo") @Size(max=255) String nomeGruppo, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);

    /**
     * Consente di modificare il nome del gruppo
     *
     * Questa operazione consente di aggiornare il nome di un gruppo
     *
     */
    @PUT
    @Path("/fruizioni/{erogatore}/{nome}/{versione}/gruppi/{nome_gruppo}")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare il nome del gruppo", tags={ "fruizioni-gruppi" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione del gruppo aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateNome(@Valid GruppoNome body, @PathParam("erogatore") String erogatore, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_gruppo") @Size(max=255) String nomeGruppo, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto);
}
