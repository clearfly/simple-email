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

package co.cfly.email.impl.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import co.cfly.email.api.ContentDisposition;
import co.cfly.email.api.EmailMessage;
import co.cfly.email.impl.attachments.InputStreamAttachment;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.ParseException;

public class MessageConverter {

    static final Logger log = Logger.getLogger(MessageConverter.class.getName());

    private final ContentDisposition defaultDisposition;
    private EmailMessage emailMessage;

    private MessageConverter(ContentDisposition defaultDisposition) {
        this.defaultDisposition = defaultDisposition;
    }

    public static EmailMessage convert(Message m) throws MessagingException, UnsupportedEncodingException {
        return convert(m, ContentDisposition.INLINE);
    }

    public static EmailMessage convert(Message m, ContentDisposition defaultDisposition) throws MessagingException, UnsupportedEncodingException {
        MessageConverter mc = new MessageConverter(defaultDisposition);
        return mc.convertMessage(m);
    }

    private EmailMessage convertMessage(Message m) throws MessagingException, UnsupportedEncodingException {
        emailMessage = new EmailMessage();

        try {
            emailMessage.getFromAddresses().addAll(MailUtility.getInternetAddressses(m.getFrom()));
            emailMessage.getToAddresses().addAll(MailUtility.getInternetAddressses(m.getRecipients(RecipientType.TO)));
            emailMessage.getCcAddresses().addAll(MailUtility.getInternetAddressses(m.getRecipients(RecipientType.CC)));
            emailMessage.getBccAddresses().addAll(MailUtility.getInternetAddressses(m.getRecipients(RecipientType.BCC)));
            emailMessage.setSubject(m.getSubject());
            emailMessage.setMessageId(m.getHeader("Message-ID")[0]);
            emailMessage.getHeaders().addAll(MailUtility.getHeaders(m.getAllHeaders()));

            if (m.getContentType().toLowerCase().contains("multipart/")) {
                addMultiPart((MimeMultipart) m.getContent());
            }
            else if (m.isMimeType("text/html")) {
                emailMessage.setHtmlBody(convertBodyPart(m));
            }
            else if (m.isMimeType("text/plain")) {
                emailMessage.setTextBody(convertBodyPart(m));
            }
            else if (Optional.ofNullable(m.getDisposition()).orElse("inline").startsWith(Part.ATTACHMENT)) {
                addAttachment(m);
            }
        }
        catch (UnsupportedEncodingException e) {
            throw e;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return emailMessage;
    }

    private void addAttachment(Message m) throws MessagingException {
        try {
            ContentDisposition attachmentDisposition = determineContentDisposition(m.getDisposition());
            emailMessage.addAttachment(new InputStreamAttachment(MailUtility.decodeString(m.getFileName()), m.getContentType(), attachmentDisposition, m.getInputStream()));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addMultiPart(MimeMultipart mp) throws MessagingException {
        try {
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart bp = mp.getBodyPart(i);
                if (bp.getContentType().toLowerCase().contains("multipart/")) {
                    addMultiPart((MimeMultipart) bp.getContent());
                }
                else {
                    addPart(mp.getBodyPart(i));
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addPart(BodyPart bp) throws MessagingException {
        try {
            if (bp.getContentType().toLowerCase().contains("multipart/")) {
                addMultiPart((MimeMultipart) bp.getContent());
            }
            else if (bp.getContentType().toLowerCase().contains("text/plain")) {
                emailMessage.setTextBody(convertBodyPart(bp));
            }
            else if (bp.getContentType().toLowerCase().contains("text/html")) {
                emailMessage.setHtmlBody(convertBodyPart(bp));
            }
            else {
                try {
                    final ContentDisposition attachmentDisposition = determineContentDisposition(bp.getDisposition());
                    final String fileName = MailUtility.decodeString(bp.getFileName());
                    emailMessage.addAttachment(new InputStreamAttachment(fileName, bp.getContentType(), attachmentDisposition, bp.getInputStream()));
                }
                catch (ParseException e) {
                    log.info("Failed to parse attachment in %s: %s".formatted(Optional.ofNullable(emailMessage.getMessageId()).orElse("Unknown Message-Id"), e.getMessage()));
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ContentDisposition determineContentDisposition(String disposition) {
        ContentDisposition attachmentDisposition = defaultDisposition;
        try {
            if (Objects.nonNull(disposition)) {
                attachmentDisposition = ContentDisposition.mapValue(disposition);
            }
        }
        catch (UnsupportedOperationException e) {
            // NOOP - Fall back to default disposition if disposition is unknown
        }
        return attachmentDisposition;
    }

    private String convertBodyPart(Part part) throws MessagingException, UnsupportedEncodingException {
        try {
            if (part.getContent() instanceof String value) {
                return value;
            }
            else {
                return new String(part.getInputStream().readAllBytes(), MailUtility.determineCharset(part).orElse(StandardCharsets.UTF_8));
            }
        }
        catch (UnsupportedEncodingException e) {
            throw e;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
