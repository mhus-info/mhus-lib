/**
 * Copyright 2018 Mike Hummel
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.lib.logging;

import de.mhus.lib.core.lang.MObject;
import de.mhus.lib.logging.adapters.JavaLoggerHandler;
import de.mhus.lib.logging.adapters.Log4JAppender;

public class ConfigureDefault extends MObject {

    public ConfigureDefault() {
        log().d("configure default logger adapters");
        try {
            if (Class.forName("org.apache.log4j.Logger") != null) {
                log().d("configure log4j");
                Log4JAppender.configure();
            }
        } catch (Throwable t) {
        }

        log().d("configure java logger");
        JavaLoggerHandler.configure();

        try {
            if (Class.forName("org.apache.log4j.Logger") != null) {
                log().d("configure slf4j");
                //	SLF4JAppender.configure();
            }
        } catch (Throwable t) {
        }
    }
}
