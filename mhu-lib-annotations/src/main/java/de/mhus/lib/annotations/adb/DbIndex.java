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
package de.mhus.lib.annotations.adb;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Use this to add a list of indexes to a column. Use comma as separator. The simplest way to name
 * the indexes is by numbers. If the index starts with an 'u' the index will be defined as an unique
 * index. Do not name the index to long most database engines have restrictions about the length of
 * an index name.
 *
 * @author mikehummel
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DbIndex {

    enum TYPE {
        AUTO,
        INDEX,
        UNIQUE
    }

    public static final String UNIQUE = "u";

    String[] value();

    TYPE type() default TYPE.AUTO;

    String hints() default "";

    String[] fields() default {};
}
