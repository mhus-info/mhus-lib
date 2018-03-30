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
package de.mhus.lib.vaadin.layouter;

import java.util.HashMap;

public class LayModel {

	private XLayElement root;
	private HashMap<String, XLayElement> elements;

	public LayModel(XLayElement root, HashMap<String, XLayElement> elements) {
		this.root = root;
		this.elements = elements;
	}
	
	public XLayElement getRoot() {
		return root;
	}

	public XLayElement get(String name) {
		return elements.get(name);
	}
	
}
