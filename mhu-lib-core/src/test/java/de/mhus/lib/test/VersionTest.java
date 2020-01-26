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
package de.mhus.lib.test;

import de.mhus.lib.core.util.Version;
import de.mhus.lib.core.util.VersionRange;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class VersionTest {

    @Test
    public void testVersion() {
        System.out.println("testVersion");
        {
            Version v = new Version("1");
            System.out.println(v);
            assertEquals(1, v.size());
            assertEquals(1, v.getVersion(0));
        }
        {
            Version v = new Version("1.2");
            System.out.println(v);
            assertEquals(2, v.size());
            assertEquals(1, v.getVersion(0));
            assertEquals(2, v.getVersion(1));
        }
        {
            Version v = new Version("1.2.3");
            System.out.println(v);
            assertEquals(3, v.size());
            assertEquals(1, v.getVersion(0));
            assertEquals(2, v.getVersion(1));
            assertEquals(3, v.getVersion(2));
        }
        {
            Version v = new Version("1.2.3-SNAPSHOT");
            System.out.println(v);
            assertEquals(3, v.size());
            assertEquals(1, v.getVersion(0));
            assertEquals(2, v.getVersion(1));
            assertEquals(3, v.getVersion(2));
        }
    }

    @Test
    public void testRange() {
        System.out.println("testRange");
        Version v = new Version("1.2.3-SNAPSHOT");
        {
            VersionRange r = new VersionRange("1.2.3");
            System.out.println(r);
            assertEquals(false, r.isRange());
            assertEquals(true, r.includes(v));
        }
        {
            VersionRange r = new VersionRange("[1.2.3,2.0.0]");
            System.out.println(r);
            assertEquals(true, r.isRange());
            assertEquals(true, r.includes(v));
        }
        {
            VersionRange r = new VersionRange("1.2.3,2.0.0");
            System.out.println(r);
            assertEquals(true, r.isRange());
            assertEquals(true, r.includes(v));
        }
        {
            VersionRange r = new VersionRange("[1.0.0,1.2.3)");
            System.out.println(r);
            assertEquals(true, r.isRange());
            assertEquals(false, r.includes(v));
        }
        {
            VersionRange r = new VersionRange("(1.2.3,2.0.0)");
            System.out.println(r);
            assertEquals(true, r.isRange());
            assertEquals(false, r.includes(v));
        }
    }

    @Test
    public void testTransform() {
        System.out.println("testTransform");
        Version v = new Version("1.2.3-SNAPSHOT");
        System.out.println(v);
        VersionRange r = v.toRange();
        System.out.println(r);
        assertEquals(true, r.includes(v));

        v = new Version("0");
        System.out.println(v);
        assertEquals(false, r.includes(v));
        v = new Version("2");
        System.out.println(v);
        assertEquals(true, r.includes(v));
    }

    @Test
    public void testCompare() {
        System.out.println("testCompare");
        assertEquals(true, new Version("2").compareTo(new Version("1.2.3")) > 0);
        assertEquals(true, new Version("1.3").compareTo(new Version("1.2.3")) > 0);

        assertEquals(true, new Version("1.2.3.1").compareTo(new Version("1.2.3")) > 0);
        assertEquals(true, new Version("1.2.3").compareTo(new Version("1.2.2")) > 0);

        assertEquals(true, new Version("1.2.3").compareTo(new Version("2")) < 0);
        assertEquals(true, new Version("2").compareTo(new Version("2.1")) < 0);
    }
}
