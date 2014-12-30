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

package com.outjected.email.impl;

import javax.mail.Session;

import com.outjected.email.api.EmailMessage;
import com.outjected.email.api.MailTransporter;
import com.outjected.email.impl.util.MailUtility;

/**
 * 
 * @author Cody Lerum
 * 
 */
public class MailTransporterImpl implements MailTransporter {

    private Session session;

    public MailTransporterImpl(Session session) {
        this.session = session;
    }

    public EmailMessage send(EmailMessage emailMessage) {
        MailUtility.send(emailMessage, session);
        return emailMessage;
    }

}
