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
package de.mhus.lib.form;

import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.util.MNls;
import de.mhus.lib.core.util.MNlsProvider;

public class Item {

	private String key;
	private String caption;
	private MNlsProvider provider;
	private String parent;

	public Item(String parent, String key, String caption) {
		this.key = key;
		this.caption = caption;
		this.parent = parent;
	}
	
	public Item(String key, String caption) {
		this.key = key;
		this.caption = caption;
	}
	
	public void setNlsProvider(MNlsProvider provider) {
		this.provider = provider;
	}
	
	@Override
	public String toString() {
		return MNls.find(provider, caption);
	}
	
	public String getKey() {
		return key;
	}
	
	public String getParent() {
		return parent;
	}
	
	public String getCaption() {
		return caption;
	}
	
	public boolean equals(Object in) {
		if (in instanceof Item)
			return MSystem.equals( ((Item)in).getKey(), key );
		return key.equals(in);
	}
}
