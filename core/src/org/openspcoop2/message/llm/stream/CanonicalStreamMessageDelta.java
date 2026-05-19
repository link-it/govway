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
package org.openspcoop2.message.llm.stream;

import org.openspcoop2.message.llm.CanonicalStopReason;
import org.openspcoop2.message.llm.CanonicalUsage;

/**
 * Finalizzazione del messaggio con {@code stop_reason} e usage finale.
 * Anthropic {@code message_delta}, OpenAI ultimo chunk con {@code finish_reason}
 * più (opzionale) extra chunk con campo {@code usage}.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class CanonicalStreamMessageDelta extends CanonicalStreamEvent {

	private CanonicalStopReason stopReason;
	private String stopSequence;
	private CanonicalUsage usage;

	public CanonicalStreamMessageDelta() {
		super("message_delta");
	}

	public CanonicalStreamMessageDelta(CanonicalStopReason stopReason, CanonicalUsage usage) {
		super("message_delta");
		this.stopReason = stopReason;
		this.usage = usage;
	}

	public CanonicalStopReason getStopReason() {
		return this.stopReason;
	}

	public void setStopReason(CanonicalStopReason stopReason) {
		this.stopReason = stopReason;
	}

	public String getStopSequence() {
		return this.stopSequence;
	}

	public void setStopSequence(String stopSequence) {
		this.stopSequence = stopSequence;
	}

	public CanonicalUsage getUsage() {
		return this.usage;
	}

	public void setUsage(CanonicalUsage usage) {
		this.usage = usage;
	}
}
