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
package de.mhus.lib.core.console;

import java.io.IOException;

import de.mhus.lib.core.MCast;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.MSystem;

public class CmdConsole extends SimpleConsole {

    public CmdConsole() {
        super();
    }

    @Override
    public void resetTerminal() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
        }
    }
    /*
    Status for device CON:
    ----------------------
    Lines: 300
    Columns: 80
    Keyboard rate: 31
    Keyboard delay: 1
    Code page: 437
     */

    public String[] getRawSettings() throws IOException {
        return MSystem.execute("cmd.exe", "/c", "mode con").toArray();
    }

    public void loadSettings() {

        try {
            String[] res = getRawSettings();
            String[] parts = res[0].split("\n");
            int cnt = 0;
            for (String p : parts) {
                p = p.trim();
                if (cnt == 2)
                    height = MCast.toint(MString.afterIndex(p, ' ').trim(), DEFAULT_HEIGHT);
                else if (cnt == 3)
                    width = MCast.toint(MString.afterIndex(p, ' ').trim(), DEFAULT_WIDTH);
                cnt++;
            }
        } catch (IOException e) {
        }
    }
}
