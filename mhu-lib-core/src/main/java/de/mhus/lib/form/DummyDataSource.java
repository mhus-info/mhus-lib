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

public class DummyDataSource extends FormControlAdapter implements DataSource {

	public boolean getBoolean(UiComponent component, String name, boolean def) {
		System.out.println("getBoolean " + component.getName() + "." + name);
		return true;
	}

	public int getInt(UiComponent component, String name, int def) {
		System.out.println("getInt " + component.getName() + "." + name);
		return def;
	}
	
	public String getString(UiComponent component, String name, String def) {
		System.out.println("getString " + component.getName() + "." + name);
		return def;
	}
	
	public Object getObject(UiComponent component, String name, Object def) {
		System.out.println("getObject " + component.getName() + "." + name);
		return def;
	}

	@Override
	public void setObject(UiComponent component, String name, Object value) {
		System.out.println("setObject " + component.getName() + "." + name + ": " + value);
	}

	@Override
	public void focus(UiComponent component) {
		System.out.println("Focus " + component.getName());
		super.focus(component);
	}

	@Override
	public boolean newValue(UiComponent component, Object newValue) {
		return true;
	}

	@Override
	public void reverted(UiComponent component) {
		System.out.println("Reverted " + component.getName());
	}

	@Override
	public void attachedForm(MForm form) {
		System.out.println("Attached " + form.getClass());
	}

	@Override
	public DataSource getNext() {
		return null;
	}

	@Override
	public void valueSet(UiComponent component) {
		System.out.println("valueSet " + component.getName());
	}

}
