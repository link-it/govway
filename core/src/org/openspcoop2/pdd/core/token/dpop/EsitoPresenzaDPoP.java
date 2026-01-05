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



package org.openspcoop2.pdd.core.token.dpop;

import org.openspcoop2.pdd.core.token.EsitoToken;

/**
 * Esito di un processo di ricerca DPoP token.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class EsitoPresenzaDPoP extends EsitoToken implements java.io.Serializable {


	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private boolean presente;

	private String headerHttp;

	private String propertyUrl;


	public boolean isPresente() {
		return this.presente;
	}

	public void setPresente(boolean presente) {
		this.presente = presente;
	}

	public String getHeaderHttp() {
		return this.headerHttp;
	}

	public void setHeaderHttp(String headerHttp) {
		this.headerHttp = headerHttp;
	}

	public String getPropertyUrl() {
		return this.propertyUrl;
	}

	public void setPropertyUrl(String propertyUrl) {
		this.propertyUrl = propertyUrl;
	}






	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();

		bf.append("DPoP presente: ");
		bf.append(this.presente);

		bf.append(super.toString());

		return bf.toString();
	}
}
