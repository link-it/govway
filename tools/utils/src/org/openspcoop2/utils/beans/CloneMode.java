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

package org.openspcoop2.utils.beans;

/**
 * Modalità con cui viene effettuato il clone di un {@link BaseBean}
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum CloneMode {

	/** Le informazioni di reflection necessarie al clone (field, metodi get/set/clone) vengono calcolate una sola volta per classe e mantenute in cache */
	REFLECTION_CACHE,

	/** I metodi necessari al clone vengono ricercati tramite reflection ad ogni invocazione */
	REFLECTION;

}
