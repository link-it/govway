/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.web.ctrlstat.servlet.aps.llm;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.ActionForward;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataServizioApplicativo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.lib.mvc.Parameter;

/**
 * Utility comune per la famiglia di servlet {@code LlmConnettoriMultipli*}.
 *
 * <p>Risoluzione del container LLM allineata alla logica standard di individuazione del
 * connettore "corrente":</p>
 * <ul>
 *   <li><b>Erogazione</b>: il container vive sul connettore del SA mappato alla
 *       PortaApplicativa di riferimento. Per un gruppo specifico la PA e' identificata
 *       da {@code idPorta}; assente, si usa la PA default mappata all'APS. Namespace
 *       {@code core.config}.</li>
 *   <li><b>Fruizione</b>: simmetricamente, il container vive sul connettore del SA
 *       mappato alla PortaDelegata identificata da {@code idPortaDelegata}; assente, il
 *       container vive direttamente su {@code fr.getConnettore()}. Namespace
 *       {@code core.registry} per il caso fruitore-default, {@code core.config} per il
 *       caso PD ridefinita.</li>
 * </ul>
 *
 * <p>Il save segue lo stesso pattern: {@code saCore.performUpdateOperation(sa)} quando il
 * container vive su un SA, {@code apsCore.performUpdateOperation(asps)} quando vive sul
 * Fruitore registry.</p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class LlmConnettoriMultipliUtils {

	private LlmConnettoriMultipliUtils() {
		// utility class
	}

	// -------------------------------------------------------------------------
	// Helper di base
	// -------------------------------------------------------------------------

	public static boolean isFruitoreContext(String idFruitoreParam, String idSoggettoFruitoreParam) {
		return (idFruitoreParam != null && !idFruitoreParam.isEmpty())
				|| (idSoggettoFruitoreParam != null && !idSoggettoFruitoreParam.isEmpty());
	}

	public static Fruitore resolveFruitore(AccordoServizioParteSpecifica asps,
			String idFruitoreParam, String idSoggettoFruitoreParam) {
		if (asps == null) {
			return null;
		}
		if (idFruitoreParam != null && !idFruitoreParam.isEmpty()) {
			long id = Long.parseLong(idFruitoreParam);
			for (Fruitore fr : asps.getFruitoreList()) {
				if (fr.getId() != null && fr.getId().longValue() == id) {
					return fr;
				}
			}
			return null;
		}
		if (idSoggettoFruitoreParam != null && !idSoggettoFruitoreParam.isEmpty()) {
			long idSogg = Long.parseLong(idSoggettoFruitoreParam);
			for (Fruitore fr : asps.getFruitoreList()) {
				if (fr.getIdSoggetto() != null && fr.getIdSoggetto().longValue() == idSogg) {
					return fr;
				}
			}
			return null;
		}
		return null;
	}

	/**
	 * Verifica se nel container LLM corrente esiste già un {@code ConnettoreLlmProviderRef}
	 * con la property {@code llm-policy} pari al valore indicato. Garantisce il vincolo
	 * di unicità "un solo provider per policy" per container.
	 *
	 * @param excludeRefNome se non null, esclude dal controllo il ref con questo nome
	 *        (uso in Change: il provider che si sta modificando non deve auto-bloccarsi).
	 */
	public static boolean existsProviderInContainer(AccordoServizioParteSpecifica asps,
			String idFruitoreParam, String idSoggettoFruitoreParam,
			String idPortaParam, String idPortaDelegataParam,
			PorteApplicativeCore paCore, PorteDelegateCore pdCore, ServiziApplicativiCore saCore,
			String llmPolicy, String excludeRefNome) throws Exception {
		boolean fruitoreCtx = isFruitoreContext(idFruitoreParam, idSoggettoFruitoreParam);
		boolean useRegistry = fruitoreCtx && (idPortaDelegataParam == null || idPortaDelegataParam.isEmpty());
		if (useRegistry) {
			Fruitore fr = resolveFruitore(asps, idFruitoreParam, idSoggettoFruitoreParam);
			if (fr == null || fr.getConnettore() == null || fr.getConnettore().getConnettoreLlm() == null) return false;
			for (org.openspcoop2.core.registry.ConnettoreLlmProviderRef ref : fr.getConnettore().getConnettoreLlm().getProviderList()) {
				if (excludeRefNome != null && excludeRefNome.equals(ref.getNome())) continue;
				if (matchesRegistryPolicy(ref, llmPolicy)) return true;
			}
			return false;
		}
		ServizioApplicativo sa = fruitoreCtx
				? resolveServizioApplicativoFruizione(idPortaDelegataParam, pdCore, saCore)
				: resolveServizioApplicativoErogazione(asps, idPortaParam, paCore, saCore);
		if (sa == null || sa.getInvocazioneServizio() == null || sa.getInvocazioneServizio().getConnettore() == null
				|| sa.getInvocazioneServizio().getConnettore().getConnettoreLlm() == null) return false;
		for (org.openspcoop2.core.config.ConnettoreLlmProviderRef ref : sa.getInvocazioneServizio().getConnettore().getConnettoreLlm().getProviderList()) {
			if (excludeRefNome != null && excludeRefNome.equals(ref.getNome())) continue;
			if (matchesConfigPolicy(ref, llmPolicy)) return true;
		}
		return false;
	}

	private static boolean matchesConfigPolicy(org.openspcoop2.core.config.ConnettoreLlmProviderRef ref, String llmPolicy) {
		if (ref.getPropertyList() == null) return false;
		for (org.openspcoop2.core.config.Property p : ref.getPropertyList()) {
			if (org.openspcoop2.core.constants.CostantiConnettori.CONNETTORE_LLM_POLICY.equals(p.getNome())) {
				return llmPolicy.equals(p.getValore());
			}
		}
		return false;
	}

	private static boolean matchesRegistryPolicy(org.openspcoop2.core.registry.ConnettoreLlmProviderRef ref, String llmPolicy) {
		if (ref.getPropertyList() == null) return false;
		for (org.openspcoop2.core.registry.Property p : ref.getPropertyList()) {
			if (org.openspcoop2.core.constants.CostantiConnettori.CONNETTORE_LLM_POLICY.equals(p.getNome())) {
				return llmPolicy.equals(p.getValore());
			}
		}
		return false;
	}

	public static AccordoServizioParteSpecifica loadAsps(AccordiServizioParteSpecificaCore apsCore, String idAspsParam) throws Exception {
		if (idAspsParam == null || idAspsParam.isEmpty()) {
			return null;
		}
		return apsCore.getAccordoServizioParteSpecifica(Long.parseLong(idAspsParam));
	}

	/**
	 * Costruisce il breadcrumb per le pagine LLM differenziando erogazione e fruizione:
	 * <ul>
	 *   <li>Erogazione: {@code Erogazioni > [label_aps] > [labelCorrente]}</li>
	 *   <li>Fruizione:  {@code Fruizioni > [label_aps] > [labelCorrente]}</li>
	 * </ul>
	 * Il segmento intermedio (label APS) e' linkato al change dell'erogazione/fruizione.
	 */
	/**
	 * Aggiunge ai dati gli hidden field con i parametri di contesto della servlet LLM
	 * ({@code idAsps}, {@code idFruitore}, {@code idSoggettoFruitore}, {@code idPorta},
	 * {@code idPortaDelegata} e, opzionalmente, {@code llmProviderNome}) cosi' da
	 * preservare il contesto durante i postback dinamici del form connettore.
	 */
	public static void addHiddenContextFields(AccordiServizioParteSpecificaHelper apsHelper,
			List<DataElement> dati, boolean includeProviderNome) throws Exception {
		addHidden(dati, LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_ASPS,
				apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_ASPS));
		addHidden(dati, LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_FRUITORE,
				apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_FRUITORE));
		addHidden(dati, LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_SOGGETTO_FRUITORE,
				apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_SOGGETTO_FRUITORE));
		addHidden(dati, LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_PORTA,
				apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_PORTA));
		addHidden(dati, LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_PORTA_DELEGATA,
				apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_PORTA_DELEGATA));
		if (includeProviderNome) {
			addHidden(dati, LlmConnettoriMultipliCostanti.PARAMETRO_LLM_PROVIDER_NOME,
					apsHelper.getParameter(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_PROVIDER_NOME));
		}
	}

	/**
	 * Costruisce un ActionForward di redirect alla servlet List preservando i parametri di
	 * contesto presenti nel request corrente ({@code idAsps}, {@code idFruitore},
	 * {@code idSoggettoFruitore}, {@code idPorta}, {@code idPortaDelegata}). Usato come
	 * target del forward "Ok" dopo Add/Change/Del per non perdere il contesto.
	 */
	public static ActionForward redirectToList(AccordiServizioParteSpecificaHelper apsHelper) throws Exception {
		StringBuilder url = new StringBuilder("/").append(LlmConnettoriMultipliCostanti.SERVLET_NAME_LLM_CONNETTORI_MULTIPLI_LIST);
		boolean first = true;
		first = appendIfPresent(url, first, LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_ASPS,
				apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_ASPS));
		first = appendIfPresent(url, first, LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_FRUITORE,
				apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_FRUITORE));
		first = appendIfPresent(url, first, LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_SOGGETTO_FRUITORE,
				apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_SOGGETTO_FRUITORE));
		first = appendIfPresent(url, first, LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_PORTA,
				apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_PORTA));
		appendIfPresent(url, first, LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_PORTA_DELEGATA,
				apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_PORTA_DELEGATA));
		return new ActionForward(url.toString(), true);
	}

	/** Chiave di sessione per il messaggio pending da mostrare al prossimo render della List. */
	private static final String SESSION_PENDING_LIST_MESSAGE = "llmConnettoriMultipli.pendingListMessage";
	private static final String SESSION_PENDING_LIST_MESSAGE_TYPE = "llmConnettoriMultipli.pendingListMessageType";

	/** Imposta un messaggio che verrà mostrato dalla List al prossimo render (sopravvive al redirect). */
	public static void setPendingListMessage(HttpSession session, String message, MessageType messageType) {
		if (message == null || message.isEmpty()) return;
		session.setAttribute(SESSION_PENDING_LIST_MESSAGE, message);
		session.setAttribute(SESSION_PENDING_LIST_MESSAGE_TYPE, messageType != null ? messageType : Costanti.MESSAGE_TYPE_ERROR);
	}

	/** Recupera (e rimuove) il messaggio pending impostato da una servlet precedente. */
	public static void consumePendingListMessage(HttpSession session, PageData pd) {
		Object msg = session.getAttribute(SESSION_PENDING_LIST_MESSAGE);
		if (msg instanceof String) {
			Object type = session.getAttribute(SESSION_PENDING_LIST_MESSAGE_TYPE);
			pd.setMessage((String) msg, type instanceof MessageType ? (MessageType) type : Costanti.MESSAGE_TYPE_ERROR);
			session.removeAttribute(SESSION_PENDING_LIST_MESSAGE);
			session.removeAttribute(SESSION_PENDING_LIST_MESSAGE_TYPE);
		}
	}

	private static boolean appendIfPresent(StringBuilder sb, boolean first, String name, String value) {
		if (value == null || value.isEmpty()) return first;
		sb.append(first ? "?" : "&").append(name).append("=").append(value);
		return false;
	}

	private static void addHidden(List<DataElement> dati, String name, String value) {
		if (value == null || value.isEmpty()) return;
		DataElement de = new DataElement();
		de.setName(name);
		de.setType(DataElementType.HIDDEN);
		de.setValue(value);
		dati.add(de);
	}

	public static List<Parameter> buildBreadcrumb(ConsoleHelper helper, AccordoServizioParteSpecifica asps,
			boolean fruitoreCtx, String labelCorrente) throws Exception {
		return buildBreadcrumb(helper, asps, fruitoreCtx, labelCorrente, false, null);
	}

	/**
	 * Variante con livello intermedio "Connettori" cliccabile (link alla List) usato dalle
	 * pagine Add/Change per indicare il contesto della lista di provenienza.
	 *
	 * @param includeConnettoriLevel se {@code true} aggiunge il segmento "Connettori" linkato alla list
	 * @param listLinkParams parametri da appendere all'URL della list (idAsps, idFruitore, ecc.)
	 */
	public static List<Parameter> buildBreadcrumb(ConsoleHelper helper, AccordoServizioParteSpecifica asps,
			boolean fruitoreCtx, String labelCorrente,
			boolean includeConnettoriLevel, Parameter[] listLinkParams) throws Exception {
		List<Parameter> lst = new ArrayList<>();
		String radiceLabel = fruitoreCtx ? ErogazioniCostanti.LABEL_ASPS_FRUIZIONI : ErogazioniCostanti.LABEL_ASPS_EROGAZIONI;
		lst.add(new Parameter(radiceLabel, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
		if (asps != null) {
			List<Parameter> parentParams = new ArrayList<>();
			parentParams.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId() + ""));
			// In contesto fruizione il servlet 'aspsErogazioniChange' richiede in piu' tipo/nome
			// del soggetto fruitore per discriminare il record da renderizzare (cfr.
			// ErogazioniHelper:990-996). Senza questi parametri la pagina di dettaglio fallisce.
			if (fruitoreCtx) {
				String idFruitoreParam = helper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_FRUITORE);
				String idSoggettoFruitoreParam = helper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_SOGGETTO_FRUITORE);
				org.openspcoop2.core.registry.Fruitore fr = resolveFruitore(asps, idFruitoreParam, idSoggettoFruitoreParam);
				if (fr != null) {
					if (fr.getTipo() != null) {
						parentParams.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE, fr.getTipo()));
					}
					if (fr.getNome() != null) {
						parentParams.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE, fr.getNome()));
					}
				}
			}
			lst.add(new Parameter(helper.getLabelIdServizio(asps), ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_CHANGE,
					parentParams.toArray(new Parameter[0])));
		}
		if (includeConnettoriLevel) {
			lst.add(new Parameter(LlmConnettoriMultipliCostanti.LABEL_LLM_CONNETTORI_MULTIPLI,
					LlmConnettoriMultipliCostanti.SERVLET_NAME_LLM_CONNETTORI_MULTIPLI_LIST,
					listLinkParams != null ? listLinkParams : new Parameter[0]));
		}
		lst.add(new Parameter(labelCorrente, null));
		return lst;
	}

	/**
	 * Costruisce l'array di parametri di contesto (idAsps + eventuali fruitore/porta) per il
	 * link "Connettori" del breadcrumb. Letti dal request corrente via {@code apsHelper}.
	 */
	public static Parameter[] buildListLinkParams(AccordiServizioParteSpecificaHelper apsHelper) throws Exception {
		List<Parameter> params = new ArrayList<>();
		String idAsps = apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_ASPS);
		if (idAsps != null && !idAsps.isEmpty()) params.add(new Parameter(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_ASPS, idAsps));
		String idFr = apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_FRUITORE);
		if (idFr != null && !idFr.isEmpty()) params.add(new Parameter(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_FRUITORE, idFr));
		String idSF = apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_SOGGETTO_FRUITORE);
		if (idSF != null && !idSF.isEmpty()) params.add(new Parameter(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_SOGGETTO_FRUITORE, idSF));
		String idP = apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_PORTA);
		if (idP != null && !idP.isEmpty()) params.add(new Parameter(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_PORTA, idP));
		String idPD = apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_PORTA_DELEGATA);
		if (idPD != null && !idPD.isEmpty()) params.add(new Parameter(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_PORTA_DELEGATA, idPD));
		return params.toArray(new Parameter[0]);
	}

	// -------------------------------------------------------------------------
	// Erogazione (container LLM su SA Erogatore mappato a PA specifica o default)
	// -------------------------------------------------------------------------

	/**
	 * Risolve la PortaApplicativa target per il contesto erogazione: se {@code idPortaParam}
	 * e' valorizzato, carica la PA con quell'id; altrimenti carica la PA default mappata
	 * all'APS.
	 */
	public static PortaApplicativa resolvePortaApplicativa(AccordoServizioParteSpecifica asps,
			String idPortaParam, PorteApplicativeCore paCore) throws Exception {
		if (asps == null) {
			return null;
		}
		if (idPortaParam != null && !idPortaParam.isEmpty()) {
			return paCore.getPortaApplicativa(Long.parseLong(idPortaParam));
		}
		IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
		IDPortaApplicativa idPA = paCore.getIDPortaApplicativaAssociataDefault(idServizio);
		return idPA == null ? null : paCore.getPortaApplicativa(idPA);
	}

	/**
	 * Carica il SA Erogatore (primo paSA della PA target) per il contesto erogazione.
	 * Ritorna null se la PA non e' risolvibile o non ha SA associati.
	 */
	public static ServizioApplicativo resolveServizioApplicativoErogazione(AccordoServizioParteSpecifica asps,
			String idPortaParam,
			PorteApplicativeCore paCore, ServiziApplicativiCore saCore) throws Exception {
		PortaApplicativa pa = resolvePortaApplicativa(asps, idPortaParam, paCore);
		if (pa == null || pa.sizeServizioApplicativoList() == 0) {
			return null;
		}
		PortaApplicativaServizioApplicativo paSA = pa.getServizioApplicativo(0);
		IDServizioApplicativo idSA = new IDServizioApplicativo();
		idSA.setIdSoggettoProprietario(new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
		idSA.setNome(paSA.getNome());
		return saCore.getServizioApplicativo(idSA);
	}

	/**
	 * Container LLM per il contesto erogazione: connettore (config) del SA Erogatore
	 * mappato alla PA specifica/default. Ritorna null se non risolvibile.
	 */
	public static org.openspcoop2.core.config.Connettore resolveContainerErogazione(
			AccordoServizioParteSpecifica asps, String idPortaParam,
			PorteApplicativeCore paCore, ServiziApplicativiCore saCore) throws Exception {
		ServizioApplicativo sa = resolveServizioApplicativoErogazione(asps, idPortaParam, paCore, saCore);
		if (sa == null || sa.getInvocazioneServizio() == null) {
			return null;
		}
		return sa.getInvocazioneServizio().getConnettore();
	}

	// -------------------------------------------------------------------------
	// Fruizione (container LLM su fr.getConnettore() o su SA della PD ridefinita)
	// -------------------------------------------------------------------------

	/**
	 * Risolve la PortaDelegata target per il contesto fruizione: se {@code idPortaDelegataParam}
	 * e' valorizzato carica la PD con quell'id; altrimenti null (= il container vive su
	 * {@code fr.getConnettore()} registry, senza ridefinizione per gruppo).
	 */
	public static PortaDelegata resolvePortaDelegata(String idPortaDelegataParam,
			PorteDelegateCore pdCore) throws Exception {
		if (idPortaDelegataParam == null || idPortaDelegataParam.isEmpty()) {
			return null;
		}
		return pdCore.getPortaDelegata(Long.parseLong(idPortaDelegataParam));
	}

	/**
	 * Carica il SA fruitore della PD ridefinita per il contesto fruizione/gruppo.
	 */
	public static ServizioApplicativo resolveServizioApplicativoFruizione(String idPortaDelegataParam,
			PorteDelegateCore pdCore, ServiziApplicativiCore saCore) throws Exception {
		PortaDelegata pd = resolvePortaDelegata(idPortaDelegataParam, pdCore);
		if (pd == null || pd.sizeServizioApplicativoList() == 0) {
			return null;
		}
		PortaDelegataServizioApplicativo paSA = pd.getServizioApplicativo(0);
		IDServizioApplicativo idSA = new IDServizioApplicativo();
		idSA.setIdSoggettoProprietario(new IDSoggetto(pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario()));
		idSA.setNome(paSA.getNome());
		return saCore.getServizioApplicativo(idSA);
	}
}
