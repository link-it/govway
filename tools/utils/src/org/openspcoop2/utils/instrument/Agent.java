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

package org.openspcoop2.utils.instrument;

import java.lang.instrument.Instrumentation;

/**
 * Agent
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Agent
{
    private static volatile Instrumentation instrumentAgent;
    
    public static void premain(final String args, final Instrumentation instr) {
        Agent.instrumentAgent = instr;
    }
    
    protected static Instrumentation getInstrumentation() {
        final Instrumentation instr = Agent.instrumentAgent;
        if (instr == null) {
            throw new IllegalStateException("Agent not initialized");
        }
        return instr;
    }
    
}
