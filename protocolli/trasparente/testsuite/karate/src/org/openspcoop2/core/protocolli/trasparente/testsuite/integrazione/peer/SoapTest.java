package org.openspcoop2.core.protocolli.trasparente.testsuite.integrazione.peer;

import org.junit.Test;
import org.openspcoop2.core.config.constants.ServiceBinding;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.TipoServizio;
import org.openspcoop2.utils.UtilsException;

/**
 * SoapTest
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoapTest extends ConfigLoader {

	@Test
	public void testErogazioneSimple() throws UtilsException {
		PeerUtilities.testSimple(TipoServizio.EROGAZIONE, ServiceBinding.SOAP);
	}

	@Test
	public void testErogazioneCustom() throws UtilsException {
		PeerUtilities.testCustom(TipoServizio.EROGAZIONE, ServiceBinding.SOAP);
	}
	
	@Test
	public void testErogazioneOverwrite() throws UtilsException {
		PeerUtilities.testOverwrite(TipoServizio.EROGAZIONE, ServiceBinding.SOAP);
	}
	
	@Test
	public void testFruizioneSimple() throws UtilsException {
		PeerUtilities.testSimple(TipoServizio.FRUIZIONE, ServiceBinding.SOAP);
	}

	@Test
	public void testFruizioneCustom() throws UtilsException {
		PeerUtilities.testCustom(TipoServizio.FRUIZIONE, ServiceBinding.SOAP);
	}
	
	@Test
	public void testFruizioneOverwrite() throws UtilsException {
		PeerUtilities.testOverwrite(TipoServizio.FRUIZIONE, ServiceBinding.SOAP);
	}
	
}