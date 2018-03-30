/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.lib.core.lang;

import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.util.MNls;
import de.mhus.lib.core.util.MNlsProvider;
import de.mhus.lib.core.util.Nls;

public class MObject extends MLog implements MNlsProvider, Nls {
	
	private MNls nls;

	public MObject() {
	}

	@Override
	public String toString() {
		return MSystem.toString(this);
	}

	@Override
	public String nls(String text) {
		return MNls.find(this, text);
	}

	@Override
	public MNls getNls() {
		if (nls == null)
			nls = MNls.lookup(this);
		return nls;
	}

}
