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
package de.mhus.lib.core.security;

import de.mhus.lib.errors.MException;

public interface ModifyAuthorizationApi {

    /**
     * Create or update resource ACL.
     *
     * @param resName
     * @param acl
     * @throws MException
     */
    void createAuthorization(String resName, String acl) throws MException;

    /**
     * Delete resource ACL.
     *
     * @param resName
     * @throws MException
     */
    void deleteAuthorization(String resName) throws MException;

    String getAuthorizationAcl(String string) throws MException;
}