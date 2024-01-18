/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.rs.server.model.BaseCredenziali;
import org.openspcoop2.core.config.rs.server.model.DominioEnum;
import org.openspcoop2.core.config.rs.server.model.ListaSoggetti;
import org.openspcoop2.core.config.rs.server.model.ModalitaAccessoEnum;
import org.openspcoop2.core.config.rs.server.model.Problem;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.core.config.rs.server.model.Soggetto;

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
public interface SoggettiApi  {

    /**
     * Creazione di un soggetto
     *
     * Questa operazione consente di creare un soggetto
     *
     */
    @POST
    @Path("/soggetti")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Creazione di un soggetto", tags={ "soggetti" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "Resource created"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "409", description = "Conflict (L'entità che si vuole creare risulta già esistente)", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void createSoggetto(@Valid Soggetto body, @QueryParam("profilo") ProfiloEnum profilo);

    /**
     * Elimina un soggetto
     *
     * Questa operazione consente di eliminare un soggetto identificato dal nome
     *
     */
    @DELETE
    @Path("/soggetti/{nome}")
    @Produces({ "application/problem+json" })
    @Operation(summary = "Elimina un soggetto", tags={ "soggetti" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Soggetto eliminato con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void deleteSoggetto(@PathParam("nome") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String nome, @QueryParam("profilo") ProfiloEnum profilo);

    /**
     * Ricerca soggetti
     *
     * Elenca i soggetti registrati
     *
     */
    @GET
    @Path("/soggetti")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Ricerca soggetti", tags={ "soggetti" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Ricerca eseguita correttamente", content = @Content(schema = @Schema(implementation = ListaSoggetti.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public ListaSoggetti findAllSoggetti(@QueryParam("profilo") ProfiloEnum profilo, @QueryParam("q") String q, @QueryParam("limit") Integer limit, @QueryParam("offset") @DefaultValue("0") Integer offset, @QueryParam("dominio") DominioEnum dominio, @QueryParam("ruolo") @Pattern(regexp="^[_A-Za-z][\\-\\._A-Za-z0-9]*$") @Size(max=255) String ruolo, @QueryParam("tipo_credenziali") ModalitaAccessoEnum tipoCredenziali, @QueryParam("profilo_qualsiasi") @DefaultValue("false") Boolean profiloQualsiasi);

    /**
     * Restituisce il dettaglio di un soggetto
     *
     * Questa operazione consente di ottenere il dettaglio di un soggetto identificato dal nome
     *
     */
    @GET
    @Path("/soggetti/{nome}")
    @Produces({ "application/json", "application/problem+json" })
    @Operation(summary = "Restituisce il dettaglio di un soggetto", tags={ "soggetti" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Dati del soggetto restituiti con successo", content = @Content(schema = @Schema(implementation = Soggetto.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public Soggetto getSoggetto(@PathParam("nome") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String nome, @QueryParam("profilo") ProfiloEnum profilo);

    /**
     * Modifica le credenziali associate ad un soggetto
     *
     * Questa operazione consente di aggiornare le credenziali associate ad un soggetto identificato dal nome
     *
     */
    @PUT
    @Path("/soggetti/{nome}/credenziali")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Modifica le credenziali associate ad un soggetto", tags={ "soggetti" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Resource updated"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateCredenzialiSoggetto(@Valid BaseCredenziali body, @PathParam("nome") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String nome, @QueryParam("profilo") ProfiloEnum profilo);

    /**
     * Modifica i dati di un soggetto
     *
     * Questa operazione consente di aggiornare i dati di un soggetto identificato dal nome
     *
     */
    @PUT
    @Path("/soggetti/{nome}")
    @Consumes({ "application/json" })
    @Produces({ "application/problem+json" })
    @Operation(summary = "Modifica i dati di un soggetto", tags={ "soggetti" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "204", description = "Soggetto aggiornato con successo"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "401", description = "Non sono state fornite le credenziali necessarie", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "403", description = "Autorizzazione non concessa per l'operazione richiesta", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "503", description = "Service Unavailable", content = @Content(schema = @Schema(implementation = Problem.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected error", content = @Content(schema = @Schema(implementation = Problem.class))) })
    public void updateSoggetto(@Valid Soggetto body, @PathParam("nome") @Pattern(regexp="^[0-9A-Za-z][\\-A-Za-z0-9]*$") @Size(max=255) String nome, @QueryParam("profilo") ProfiloEnum profilo);
}
