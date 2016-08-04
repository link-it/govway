/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.security.message.signature;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;

/**
 * SunAttachmentBase64ContentTransform
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SunAttachmentBase64ContentTransform extends com.sun.org.apache.xml.internal.security.transforms.implementations.TransformBase64Decode {


	protected XMLSignatureInput sunEnginePerformTransform(XMLSignatureInput input,
			Transform _transformObject) throws IOException,
			CanonicalizationException, TransformationException {
		try{
			Method m = this.getClass().getSuperclass().
					getDeclaredMethod("enginePerformTransform", XMLSignatureInput.class, Transform.class);
			return (XMLSignatureInput) m.invoke(this, input, _transformObject);
		}catch(Exception e){
			throw new TransformationException(e.getMessage(),e);
		}
	}

	protected XMLSignatureInput sunEnginePerformTransform(XMLSignatureInput input,
			OutputStream os, Transform _transformObject) throws IOException,
			CanonicalizationException, TransformationException {
		try{
			Method m = this.getClass().getSuperclass().
					getDeclaredMethod("enginePerformTransform", XMLSignatureInput.class, OutputStream.class, Transform.class);
			return (XMLSignatureInput) m.invoke(this, input, os, _transformObject);
		}catch(Exception e){
			throw new TransformationException(e.getMessage(),e);
		}
	}
}
