package org.openspcoop2.security.message.jose;

import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.utils.security.JOSERepresentation;

public class JOSEUtils {

	public static JOSERepresentation toJOSERepresentation(String mode) throws SecurityException {
		if(SecurityConstants.SIGNATURE_MODE_SELF_CONTAINED.equals(mode)) {
			return JOSERepresentation.SELF_CONTAINED;
		}
		else if(SecurityConstants.SIGNATURE_MODE_COMPACT.equals(mode)) {
			return JOSERepresentation.COMPACT;
		} 
		else if(SecurityConstants.SIGNATURE_MODE_DETACHED.equals(mode)) {
			return JOSERepresentation.DETACHED;
		} 
		else {
			throw new SecurityException("Mode '"+mode+"' unsupported");
		}
	}
	

}
