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
package de.mhus.lib.core.lang;

import de.mhus.lib.basics.Valueable;

public class LongValue implements Valueable<Long> {

    public LongValue() {}

    public LongValue(long initial) {
        value = initial;
    }

    public long value;

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object in) {
        if (in == null) return false;
        if (in instanceof Number) return ((Number) in).longValue() == value;
        return false;
    }

    @Override
    public Long getValue() {
        return value;
    }

    public long get() {
        return value;
    }

    public void set(long value) {
        this.value = value;
    }
}
