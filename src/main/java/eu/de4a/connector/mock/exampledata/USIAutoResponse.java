/*
 * Copyright (C) 2023, Partners of the EU funded DE4A project consortium
 *   (https://www.de4a.eu/consortium), under Grant Agreement No.870635
 * Author:
 *   Spanish Ministry of Economic Affairs and Digital Transformation -
 *     General Secretariat for Digital Administration (MAETD - SGAD)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.de4a.connector.mock.exampledata;

import lombok.Getter;

public enum USIAutoResponse {
    OFF(false, 0),
    IMMEDIATE(true, 100), //Some delay is still needed to make sure that the dt usi request comes after the do usi request is finished.
    DELAY_5_SEC(true, 5000),
    DELAY_10_SEC(true, 10000),
    DELAY_30_SEC(true, 30000),
    DELAY_40_SEC(true, 40000),
    DELAY_240_SEC(true, 240000);

    private final boolean useAutoResp;
    @Getter
    private final long wait;

    USIAutoResponse(boolean useAutoResp, long wait) {
        this.useAutoResp = useAutoResp;
        this.wait = wait;
    }

    public boolean useAutoResp() {
        return useAutoResp;
    }
}
