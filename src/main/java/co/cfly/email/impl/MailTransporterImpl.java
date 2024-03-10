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

package co.cfly.email.impl;

import co.cfly.email.api.EmailMessage;
import co.cfly.email.api.MailTransporter;
import co.cfly.email.impl.util.MailUtility;
import jakarta.mail.Session;

public class MailTransporterImpl implements MailTransporter {

    private Session session;

    public MailTransporterImpl(Session session) {
        this.session = session;
    }

    @Override
    public EmailMessage send(EmailMessage emailMessage) {
        MailUtility.send(emailMessage, session);
        return emailMessage;
    }

}
