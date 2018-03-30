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
package de.mhus.lib.core.vault;

import java.io.IOException;
import java.util.UUID;

import de.mhus.lib.core.parser.ParseException;
import de.mhus.lib.errors.NotSupportedException;

public interface VaultEntry {

	/**
	 * Returns the unique id of the entry.
	 * @return The unique id
	 */
	UUID getId();
	
	/**
	 * Returns the type of the entry as string. A list of default
	 * types is defined in MVault.
	 * 
	 * @return The type of the entry, never null.
	 */
	String getType();
	
	/**
	 * Return a readable description describe the key and/or the usage.
	 * @return
	 */
	String getDescription();
	
	/**
	 * Return the value of the entry as text.
	 * 
	 * @return The entry as text.
	 */
	String getValue();
	
	/**
	 * Try to adapt the entry to the given class or interface.
	 * @param ifc
	 * @return The requested interface or class.
	 * @throws NotSupportedException Thrown if the entry can't be adapted to the interface.
	 * @throws IOException 
	 * @throws Exception 
	 */
	<T> T adaptTo(Class<? extends T> ifc) throws NotSupportedException, ParseException;
}
