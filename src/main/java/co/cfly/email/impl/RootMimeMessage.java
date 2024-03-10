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

import java.io.InputStream;

import jakarta.mail.Header;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

/**
 * Extends {@link MimeMessage} to allow for the setting of the Message-ID
 */
public class RootMimeMessage extends MimeMessage {

    private String messageId;
    private String envelopeFrom;

    public RootMimeMessage(Session session) {
        super(session);
    }

    public RootMimeMessage(Session session, InputStream inputStream) throws MessagingException {
        super(session, inputStream);
    }

    @Override
    protected void updateMessageID() throws MessagingException {
        Header header = new Header("Message-ID", messageId);
        setHeader(header.getName(), header.getValue());
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getEnvelopeFrom() {
        return envelopeFrom;
    }

    public void setEnvelopeFrom(String envelopeFrom) {
        this.envelopeFrom = envelopeFrom;
    }
}
