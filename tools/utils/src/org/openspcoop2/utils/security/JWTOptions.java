/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

package org.openspcoop2.utils.security;

import java.util.ArrayList;
import java.util.List;

/**
 * JWEOptions
 * 
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class JWTOptions {

	private JOSESerialization serialization;
	
	private boolean permitUseHeaderX5C = true;
	private boolean permitUseHeaderX5U = true;
	private boolean permitUseHeaderX5T = true;
	private boolean permitUseHeaderX5T_256 = true;
	private boolean permitUseHeaderJWK= true;
	private boolean permitUseHeaderJKU = true;
	private boolean permitUseHeaderKID = true;
	

	public JWTOptions(JOSESerialization serialization) {
		this.serialization = serialization;
	}
	
	public JOSESerialization getSerialization() {
		return this.serialization;
	}
	public void setSerialization(JOSESerialization serialization) {
		this.serialization = serialization;
	}
	
	public boolean isPermitUseHeaderX5C() {
		return this.permitUseHeaderX5C;
	}
	public void setPermitUseHeaderX5C(boolean permitUseHeaderX5C) {
		this.permitUseHeaderX5C = permitUseHeaderX5C;
	}

	public boolean isPermitUseHeaderX5U() {
		return this.permitUseHeaderX5U;
	}
	public void setPermitUseHeaderX5U(boolean permitUseHeaderX5U) {
		this.permitUseHeaderX5U = permitUseHeaderX5U;
	}

	public boolean isPermitUseHeaderX5T() {
		return this.permitUseHeaderX5T;
	}
	public void setPermitUseHeaderX5T(boolean permitUseHeaderX5T) {
		this.permitUseHeaderX5T = permitUseHeaderX5T;
	}

	public boolean isPermitUseHeaderX5T_256() {
		return this.permitUseHeaderX5T_256;
	}
	public void setPermitUseHeaderX5T_256(boolean permitUseHeaderX5T_256) {
		this.permitUseHeaderX5T_256 = permitUseHeaderX5T_256;
	}
	
	public boolean isPermitUseHeaderJWK() {
		return this.permitUseHeaderJWK;
	}
	public void setPermitUseHeaderJWK(boolean permitUseHeaderJWK) {
		this.permitUseHeaderJWK = permitUseHeaderJWK;
	}

	public boolean isPermitUseHeaderJKU() {
		return this.permitUseHeaderJKU;
	}
	public void setPermitUseHeaderJKU(boolean permitUseHeaderJKU) {
		this.permitUseHeaderJKU = permitUseHeaderJKU;
	}
	
	public boolean isPermitUseHeaderKID() {
		return this.permitUseHeaderKID;
	}
	public void setPermitUseHeaderKID(boolean permitUseHeaderKID) {
		this.permitUseHeaderKID = permitUseHeaderKID;
	}
	
	
	public List<String> headersNotPermitted(org.apache.cxf.rs.security.jose.common.JoseHeaders hdrs){
		List<String> list = new ArrayList<>();
		if(hdrs.getX509Chain()!=null && !hdrs.getX509Chain().isEmpty()) {
			if(this.isPermitUseHeaderX5C()==false) {
				list.add(JwtHeaders.JWT_HDR_X5C);
			}
		}
		if(hdrs.getJsonWebKey()!=null) {
			if(this.isPermitUseHeaderJWK()==false) {
				list.add(JwtHeaders.JWT_HDR_JWK);
			}
		}
		if(hdrs.getX509Url()!=null) {
			if(this.isPermitUseHeaderX5U()==false) {
				list.add(JwtHeaders.JWT_HDR_X5U);
			}
		}
		if(hdrs.getJsonWebKeysUrl()!=null) {
			if(this.isPermitUseHeaderJKU()==false) {
				list.add(JwtHeaders.JWT_HDR_JKU);
			}
		}
		if(hdrs.getX509Thumbprint()!=null) {
			if(this.isPermitUseHeaderX5T()==false) {
				list.add(JwtHeaders.JWT_HDR_X5T);
			}
		}
		if(hdrs.getX509ThumbprintSHA256()!=null) {
			if(this.isPermitUseHeaderX5T_256()==false) {
				list.add(JwtHeaders.JWT_HDR_X5t_S256);
			}
		}
		if(hdrs.getKeyId()!=null) {
			if(this.isPermitUseHeaderKID()==false) {
				list.add(JwtHeaders.JWT_HDR_KID);
			}
		}
		return list;
	}
}
