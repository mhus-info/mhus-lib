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
package de.mhus.lib.test;

import java.util.LinkedList;

import org.junit.Assert;

import de.mhus.lib.annotations.base.Bind;
import de.mhus.lib.core.lang.MBaseObject;

@Bind
public class MBaseTestInjectionObject extends MBaseObject {

	@Bind
	private LinkedList<String> list;

	public void test() {
		Assert.assertEquals(1,list.size());
	}
	
}
