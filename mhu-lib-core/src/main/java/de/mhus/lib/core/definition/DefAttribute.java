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
package de.mhus.lib.core.definition;

import de.mhus.lib.core.MCast;
import de.mhus.lib.errors.MException;

public class DefAttribute implements IDefAttribute {

	private String name;
	private Object value;

	public DefAttribute(String name, Object value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public void inject(DefComponent parent) throws MException {
		parent.setString(name, MCast.objectToString(value) );
	}

	public String getName() {
		return name;
	}
	
	public Object getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return name + "=" + value;
	}
	
}
