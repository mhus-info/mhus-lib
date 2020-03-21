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
package de.mhus.lib.core.definition;

import java.util.LinkedList;
import java.util.Properties;

import de.mhus.lib.core.config.HashConfig;
import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.lib.errors.MException;

public class DefComponent extends HashConfig implements IDefDefinition {

    private static final long serialVersionUID = 1L;
    private String tag;
    private LinkedList<IDefDefinition> definitions = new LinkedList<IDefDefinition>();

    public DefComponent(String tag, IDefDefinition... definitions) {
        super(tag, null);
        this.tag = tag;
        addDefinition(definitions);
    }

    public DefComponent addAttribute(String name, Object value) {
        return addDefinition(new DefAttribute(name, value));
    }

    public DefComponent addDefinition(IDefDefinition... def) {
        if (def == null) return this;
        for (IDefDefinition d : def) if (d != null) definitions.add(d);
        return this;
    }

    public LinkedList<IDefDefinition> definitions() {
        return definitions;
    }

    @Override
    public void inject(DefComponent parent) throws MException {
        if (parent != null) {
            parent.setConfig(tag, this);
        }
        for (IDefDefinition d : definitions) {
            d.inject(this);
        }
    }

    public void fillNls(Properties p) throws MException {

        String nls = getString("nls", null);
        if (nls == null) nls = getString("name", null);
        if (nls != null && isProperty("title")) {
            p.setProperty(nls + "_title", getString("title", null));
        }
        if (nls != null && isProperty("description")) {
            p.setProperty(nls + "_description", getString("description", null));
        }

        fill(this, p);
    }

    private void fill(HashConfig config, Properties p) throws MException {
        for (ResourceNode<?> c : config.getNodes()) {
            if (c instanceof DefComponent) ((DefComponent) c).fillNls(p);
            else fill((HashConfig) c, p);
        }
    }
}