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

import org.openspcoop2.core.constants.CostantiConnettori;

/**
 * Conversioni bidirezionali fra {@code Connettore} concreto (config namespace)
 * e {@code ConnettoreLlmProviderRef} (config/registry) per la persistenza
 * dei provider concreti dentro il container LLM.
 *
 * <p>Il provider concreto e' modellato a tutti gli effetti come un Connettore
 * (tipo + property + binding); il bean intermedio {@link org.openspcoop2.core.config.ConnettoreLlmProviderRef}
 * lo rappresenta dentro il container. La conversione aggiunge la property
 * {@code llm-policy} (= nome del LLM Provider) e i {@link org.openspcoop2.core.config.ConnettoreLlmBinding}
 * con i modelli abilitati.</p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class LlmConnettoriProviderRefUtils {

	private LlmConnettoriProviderRefUtils() {
		// utility class
	}

	/** Costruisce un {@code ConnettoreLlmProviderRef} (config) a partire dal Connettore concreto compilato dall'utente. */
	public static org.openspcoop2.core.config.ConnettoreLlmProviderRef buildConfigRefFromConfig(
			org.openspcoop2.core.config.Connettore concreto, String containerNome, String llmPolicy, String[] llmBindings) {
		org.openspcoop2.core.config.ConnettoreLlmProviderRef ref = new org.openspcoop2.core.config.ConnettoreLlmProviderRef();
		ref.setNome((containerNome != null ? containerNome : "") + "_" + llmPolicy);
		ref.setTipo(concreto.getTipo());
		if (concreto.getPropertyList() != null) {
			for (org.openspcoop2.core.config.Property p : concreto.getPropertyList()) {
				org.openspcoop2.core.config.Property copy = new org.openspcoop2.core.config.Property();
				copy.setNome(p.getNome());
				copy.setValore(p.getValore());
				ref.addProperty(copy);
			}
		}
		addConfigProperty(ref, CostantiConnettori.CONNETTORE_LLM_POLICY, llmPolicy);
		if (llmBindings != null) {
			for (String b : llmBindings) {
				if (b != null && !b.isEmpty()) {
					org.openspcoop2.core.config.ConnettoreLlmBinding bind = new org.openspcoop2.core.config.ConnettoreLlmBinding();
					bind.setNome(b);
					ref.addBinding(bind);
				}
			}
		}
		return ref;
	}

	/** Costruisce un {@code ConnettoreLlmProviderRef} (registry) a partire dal Connettore concreto compilato dall'utente. */
	public static org.openspcoop2.core.registry.ConnettoreLlmProviderRef buildRegistryRefFromConfig(
			org.openspcoop2.core.config.Connettore concreto, String containerNome, String llmPolicy, String[] llmBindings) {
		org.openspcoop2.core.registry.ConnettoreLlmProviderRef ref = new org.openspcoop2.core.registry.ConnettoreLlmProviderRef();
		ref.setNome((containerNome != null ? containerNome : "") + "_" + llmPolicy);
		ref.setTipo(concreto.getTipo());
		if (concreto.getPropertyList() != null) {
			for (org.openspcoop2.core.config.Property p : concreto.getPropertyList()) {
				org.openspcoop2.core.registry.Property copy = new org.openspcoop2.core.registry.Property();
				copy.setNome(p.getNome());
				copy.setValore(p.getValore());
				ref.addProperty(copy);
			}
		}
		addRegistryProperty(ref, CostantiConnettori.CONNETTORE_LLM_POLICY, llmPolicy);
		if (llmBindings != null) {
			for (String b : llmBindings) {
				if (b != null && !b.isEmpty()) {
					org.openspcoop2.core.registry.ConnettoreLlmBinding bind = new org.openspcoop2.core.registry.ConnettoreLlmBinding();
					bind.setNome(b);
					ref.addBinding(bind);
				}
			}
		}
		return ref;
	}

	/** Inverso di {@link #buildConfigRefFromConfig}: estrae un Connettore (config) a partire dal ref. */
	public static org.openspcoop2.core.config.Connettore extractConfigConnettore(org.openspcoop2.core.config.ConnettoreLlmProviderRef ref) {
		org.openspcoop2.core.config.Connettore c = new org.openspcoop2.core.config.Connettore();
		c.setNome(ref.getNome());
		c.setTipo(ref.getTipo());
		if (ref.getPropertyList() != null) {
			for (org.openspcoop2.core.config.Property p : ref.getPropertyList()) {
				c.addProperty(p);
			}
		}
		return c;
	}

	/** Inverso di {@link #buildRegistryRefFromConfig}: estrae un Connettore (config) a partire dal ref registry. */
	public static org.openspcoop2.core.config.Connettore extractConfigConnettoreFromRegistry(org.openspcoop2.core.registry.ConnettoreLlmProviderRef ref) {
		org.openspcoop2.core.config.Connettore c = new org.openspcoop2.core.config.Connettore();
		c.setNome(ref.getNome());
		c.setTipo(ref.getTipo());
		if (ref.getPropertyList() != null) {
			for (org.openspcoop2.core.registry.Property p : ref.getPropertyList()) {
				org.openspcoop2.core.config.Property copy = new org.openspcoop2.core.config.Property();
				copy.setNome(p.getNome());
				copy.setValore(p.getValore());
				c.addProperty(copy);
			}
		}
		return c;
	}

	public static String[] extractConfigBindings(org.openspcoop2.core.config.ConnettoreLlmProviderRef ref) {
		if (ref == null || ref.getBindingList() == null) return new String[0];
		String[] arr = new String[ref.getBindingList().size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = ref.getBindingList().get(i).getNome();
		}
		return arr;
	}

	public static String[] extractRegistryBindings(org.openspcoop2.core.registry.ConnettoreLlmProviderRef ref) {
		if (ref == null || ref.getBindingList() == null) return new String[0];
		String[] arr = new String[ref.getBindingList().size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = ref.getBindingList().get(i).getNome();
		}
		return arr;
	}

	public static String findConfigProperty(org.openspcoop2.core.config.ConnettoreLlmProviderRef ref, String name) {
		if (ref == null || ref.getPropertyList() == null) return null;
		for (org.openspcoop2.core.config.Property p : ref.getPropertyList()) {
			if (name.equals(p.getNome())) return p.getValore();
		}
		return null;
	}

	public static String findRegistryProperty(org.openspcoop2.core.registry.ConnettoreLlmProviderRef ref, String name) {
		if (ref == null || ref.getPropertyList() == null) return null;
		for (org.openspcoop2.core.registry.Property p : ref.getPropertyList()) {
			if (name.equals(p.getNome())) return p.getValore();
		}
		return null;
	}

	private static void addConfigProperty(org.openspcoop2.core.config.ConnettoreLlmProviderRef ref, String name, String value) {
		org.openspcoop2.core.config.Property p = new org.openspcoop2.core.config.Property();
		p.setNome(name); p.setValore(value); ref.addProperty(p);
	}

	private static void addRegistryProperty(org.openspcoop2.core.registry.ConnettoreLlmProviderRef ref, String name, String value) {
		org.openspcoop2.core.registry.Property p = new org.openspcoop2.core.registry.Property();
		p.setNome(name); p.setValore(value); ref.addProperty(p);
	}

}
