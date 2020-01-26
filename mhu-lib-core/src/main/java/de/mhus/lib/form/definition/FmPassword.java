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
package de.mhus.lib.form.definition;

import de.mhus.lib.basics.consts.Identifier;
import de.mhus.lib.core.M;
import de.mhus.lib.core.definition.IDefAttribute;

public class FmPassword extends IFmElement {

    private static final long serialVersionUID = 1L;

    public FmPassword(
            Identifier ident, String title, String description, IDefAttribute... definitions) {
        this(M.n(ident), title, description, definitions);
    }

    public FmPassword(String name, String title, String description, IDefAttribute... definitions) {
        this(name, new FaNls(title, description));
        addDefinition(definitions);
    }

    public FmPassword(String name, IDefAttribute... definitions) {
        super(name, definitions);
        setString("type", "password");
    }
}
