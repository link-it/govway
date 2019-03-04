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
public interface ErogazioniGruppiApi  {

    /**
     * Aggiunta di azioni o risorse dell'API al gruppo
     *
     * Questa operazione consente di aggiungere azioni o risorse dell'API al gruppo
     *
     */
    @POST
    @Path("/erogazioni/{nome}/{versione}/gruppi/{nome_gruppo}/azioni")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Aggiunta di azioni o risorse dell'API al gruppo", tags={ "erogazioni-gruppi" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Azioni aggiunte con con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void addErogazioneGruppoAzioni(@Valid GruppoAzioni body, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_gruppo") @Size(max=255) String nomeGruppo, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("tipo_servizio") String tipoServizio);

    /**
     * Creazione di un gruppo di azioni o risorse dell'API erogata
     *
     * Questa operazione consente di creare un gruppo di azioni o risorse dell'API erogata
     *
     */
    @POST
    @Path("/erogazioni/{nome}/{versione}/gruppi")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Creazione di un gruppo di azioni o risorse dell'API erogata", tags={ "erogazioni-gruppi" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Gruppo creato con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void createErogazioneGruppo(@Valid Gruppo body, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("tipo_servizio") String tipoServizio);

    /**
     * Elimina il gruppo identificato dal nome
     *
     * Questa operazione consente di eliminare il gruppo identificato dal nome
     *
     */
    @DELETE
    @Path("/erogazioni/{nome}/{versione}/gruppi/{nome_gruppo}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina il gruppo identificato dal nome", tags={ "erogazioni-gruppi" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Gruppo eliminate con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void deleteErogazioneGruppo(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_gruppo") @Size(max=255) String nomeGruppo, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("tipo_servizio") String tipoServizio);

    /**
     * Elimina l'azione o la risorsa dell'API associata al gruppo
     *
     * Questa operazione consente di eliminare l'azione o la risorsa dell'API associate al gruppo
     *
     */
    @DELETE
    @Path("/erogazioni/{nome}/{versione}/gruppi/{nome_gruppo}/azioni/{nome_azione}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina l'azione o la risorsa dell'API associata al gruppo", tags={ "erogazioni-gruppi" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Azione eliminate con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void deleteErogazioneGruppoAzione(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_gruppo") @Size(max=255) String nomeGruppo, @PathParam("nome_azione") String nomeAzione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("tipo_servizio") String tipoServizio);

    /**
     * Ricerca i gruppi in cui sono stati classificate le azioni o le risorse dell'API
     *
     * Elenca i gruppi in cui sono stati classificate le azioni o le risorse dell'API
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/gruppi")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Ricerca i gruppi in cui sono stati classificate le azioni o le risorse dell'API", tags={ "erogazioni-gruppi" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Ricerca eseguita correttamente", content = @Content(schema = @Schema(implementation = ListaGruppi.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ListaGruppi findAllErogazioneGruppi(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("tipo_servizio") String tipoServizio, @QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset, @QueryParam("azione") String azione);

    /**
     * Restituisce azioni/risorse associate al gruppo identificato dal nome
     *
     * Questa operazione consente di ottenere le azioni associate al gruppo identificato dal nome
     *
     */
    @GET
    @Path("/erogazioni/{nome}/{versione}/gruppi/{nome_gruppo}")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce azioni/risorse associate al gruppo identificato dal nome", tags={ "erogazioni-gruppi" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Elenco di azioni/risorse restituite con successo", content = @Content(schema = @Schema(implementation = GruppoAzioni.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public GruppoAzioni getErogazioneGruppoAzioni(@PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_gruppo") @Size(max=255) String nomeGruppo, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("tipo_servizio") String tipoServizio);

    /**
     * Consente di modificare il nome del gruppo
     *
     * Questa operazione consente di aggiornare il nome di un gruppo
     *
     */
    @PUT
    @Path("/erogazioni/{nome}/{versione}/gruppi/{nome_gruppo}")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Consente di modificare il nome del gruppo", tags={ "erogazioni-gruppi" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Configurazione del gruppo aggiornata correttamente"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "200", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateErogazioneGruppoNome(@Valid GruppoNome body, @PathParam("nome") String nome, @PathParam("versione") @Min(1) Integer versione, @PathParam("nome_gruppo") @Size(max=255) String nomeGruppo, @QueryParam("profilo") ProfiloEnum profilo, @QueryParam("soggetto") String soggetto, @QueryParam("tipo_servizio") String tipoServizio);
}
