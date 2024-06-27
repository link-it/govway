/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.config.vault.cli.testsuite.secrets.test;

import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
* CustomRunner: per effettuare lo skip dei test successivi al fallimento di un test
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class CustomRunner extends BlockJUnit4ClassRunner {
    private static boolean testFailed = false;

    public static void setTestFailed(boolean testFailed) {
		CustomRunner.testFailed = testFailed;
	}

	public CustomRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    public void run(final RunNotifier notifier) {
        notifier.addListener(new RunListener() {
            @Override
            public void testFailure(org.junit.runner.notification.Failure failure) throws Exception {
            	setTestFailed(true);
            }
        });
        super.run(notifier);
    }

    @Override
    protected void runChild(final org.junit.runners.model.FrameworkMethod method, RunNotifier notifier) {
        if (testFailed) {
            notifier.fireTestIgnored(describeChild(method));
            return;
        }
        super.runChild(method, notifier);
    }
}
