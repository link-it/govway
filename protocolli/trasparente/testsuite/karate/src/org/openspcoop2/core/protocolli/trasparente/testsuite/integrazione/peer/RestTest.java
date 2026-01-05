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
package org.openspcoop2.core.protocolli.trasparente.testsuite.integrazione.peer;

import org.junit.Test;
import org.openspcoop2.core.config.constants.ServiceBinding;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.UtilsException;

/**
 * RestTest
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RestTest extends ConfigLoader {

	@Test
	public void testErogazioneSimple() throws UtilsException {
		PeerUtilities.testSimple(TipoServizio.EROGAZIONE, ServiceBinding.REST);
	}

	@Test
	public void testErogazioneCustom() throws UtilsException {
		PeerUtilities.testCustom(TipoServizio.EROGAZIONE, ServiceBinding.REST);
	}
	
	@Test
	public void testErogazioneOverwrite() throws UtilsException {
		PeerUtilities.testOverwrite(TipoServizio.EROGAZIONE, ServiceBinding.REST);
	}
	
	@Test
	public void testFruizioneSimple() throws UtilsException {
		PeerUtilities.testSimple(TipoServizio.FRUIZIONE, ServiceBinding.REST);
	}

	@Test
	public void testFruizioneCustom() throws UtilsException {
		PeerUtilities.testCustom(TipoServizio.FRUIZIONE, ServiceBinding.REST);
	}
	
	@Test
	public void testFruizioneOverwrite() throws UtilsException {
		PeerUtilities.testOverwrite(TipoServizio.FRUIZIONE, ServiceBinding.REST);
	}
	
}
