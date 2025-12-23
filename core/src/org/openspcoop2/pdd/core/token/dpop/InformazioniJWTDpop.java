/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import org.openspcoop2.pdd.core.token.AbstractInformazioniJWT;
import org.slf4j.Logger;

/**
 * InformazioniJWTDpop
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InformazioniJWTDpop extends AbstractInformazioniJWT {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public InformazioniJWTDpop() {
		super();
	}
	public InformazioniJWTDpop(Logger log, String base64, boolean infoNormalizzate) {
		super(log, base64, infoNormalizzate, "dpop");
	}

}
