/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.outjected.email.util;

import com.outjected.email.api.SessionConfig;
import com.outjected.email.impl.SimpleMailConfig;

/**
 * 
 * @author Cody Lerum
 * 
 */
public class TestMailConfigs {

    public static SessionConfig standardConfig() {
        final SimpleMailConfig mailConfig = new SimpleMailConfig();
        mailConfig.setServerPort(25252);
        return mailConfig;
    }

    public static SimpleMailConfig gmailConfig() {
        SimpleMailConfig mailConfig = new SimpleMailConfig();
        mailConfig.setServerHost("localhost");
        mailConfig.setServerPort(8978);
        mailConfig.setUsername("test");
        mailConfig.setPassword("test12!");
        mailConfig.setAuth(true);
        return mailConfig;
    }
}
