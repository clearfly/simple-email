/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package com.outjected.email.impl;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;

import com.outjected.email.api.ContentDisposition;
import com.outjected.email.api.ContentType;
import com.outjected.email.api.EmailAttachment;
import com.outjected.email.api.EmailContact;
import com.outjected.email.api.EmailMessage;
import com.outjected.email.api.EmailMessageType;
import com.outjected.email.api.Header;
import com.outjected.email.api.ICalMethod;
import com.outjected.email.api.MailContext;
import com.outjected.email.api.MailMessage;
import com.outjected.email.api.MailTransporter;
import com.outjected.email.api.MessagePriority;
import com.outjected.email.api.SendFailedException;
import com.outjected.email.api.SessionConfig;
import com.outjected.email.api.TemplateProvider;
import com.outjected.email.impl.attachments.BaseAttachment;
import com.outjected.email.impl.attachments.FileAttachment;
import com.outjected.email.impl.attachments.InputStreamAttachment;
import com.outjected.email.impl.util.EmailAttachmentUtil;
import com.outjected.email.impl.util.HtmlToPlainText;
import com.outjected.email.impl.util.MailUtility;

/**
 * @author Cody Lerum
 */
public class MailMessageImpl implements MailMessage {

    private EmailMessage emailMessage;

    private MailTransporter mailTransporter;
    private Session session;
    private SessionConfig mailConfig;

    private TemplateProvider subjectTemplate;
    private TemplateProvider textTemplate;
    private TemplateProvider htmlTemplate;
    private Map<String, Object> templateContext = new HashMap<>();
    private boolean templatesMerged;
    private boolean createTextAlternative = false;

    private MailMessageImpl() {
        emailMessage = new EmailMessage();

    }

    public MailMessageImpl(Session session) {
        this();
        this.session = session;
    }

    public MailMessageImpl(MailTransporter mailTransporter) {
        this();
        this.mailTransporter = mailTransporter;
    }

    public MailMessageImpl(SessionConfig mailConfig) {
        this();
        this.mailConfig = mailConfig;
    }

    // Begin Addressing

    @Override public MailMessage from(String address) {
        from(MailUtility.internetAddress(address));
        return this;
    }

    @Override public MailMessage from(InternetAddress emailAddress) {
        if (emailAddress != null) {
            emailMessage.getFromAddresses().clear();
            emailMessage.getFromAddresses().add(emailAddress);
        }
        return this;
    }

    @Override public MailMessage from(EmailContact emailContact) {
        if (emailContact != null) {
            from(MailUtility.internetAddress(emailContact));
        }
        return this;
    }

    @Override public MailMessage replyTo(String address) {
        replyTo(MailUtility.internetAddress(address));
        return this;
    }

    @Override public MailMessage replyTo(InternetAddress emailAddress) {
        if (emailAddress != null) {
            emailMessage.getReplyToAddresses().clear();
            emailMessage.getReplyToAddresses().add(emailAddress);
        }
        return this;
    }

    @Override public MailMessage replyTo(EmailContact emailContact) {
        if (emailContact != null) {
            replyTo(MailUtility.internetAddress(emailContact));
        }
        return this;
    }

    @Override public MailMessage addHeader(String name, String value) {
        emailMessage.getHeaders().add(new Header(name, value));
        return this;
    }

    @Override public MailMessage addCustomVariable(String name, String value) {
        emailMessage.getCustomVariables().put(name, value);
        return this;
    }

    @Override public MailMessage to(String... address) {
        emailMessage.getToAddresses().addAll(MailUtility.internetAddress(address));
        return this;
    }

    @Override public MailMessage to(InternetAddress emailAddress) {
        if (emailAddress != null) {
            emailMessage.getToAddresses().add(emailAddress);
        }
        return this;
    }

    @Override public MailMessage to(EmailContact emailContact) {
        if (emailContact != null) {
            emailMessage.getToAddresses().add(MailUtility.internetAddress(emailContact));
        }
        return this;
    }

    @Override public MailMessage to(Collection<? extends EmailContact> emailContacts) {
        emailMessage.getToAddresses().addAll(MailUtility.internetAddress(emailContacts));
        return this;
    }

    @Override public MailMessage cc(String... address) {
        emailMessage.getCcAddresses().addAll(MailUtility.internetAddress(address));
        return this;
    }

    @Override public MailMessage cc(InternetAddress emailAddress) {
        if (emailAddress != null) {
            emailMessage.getCcAddresses().add(emailAddress);
        }
        return this;
    }

    @Override public MailMessage cc(EmailContact emailContact) {
        if (emailContact != null) {
            emailMessage.getCcAddresses().add(MailUtility.internetAddress(emailContact));
        }
        return this;
    }

    @Override public MailMessage cc(Collection<? extends EmailContact> emailContacts) {
        emailMessage.getCcAddresses().addAll(MailUtility.internetAddress(emailContacts));
        return this;
    }

    @Override public MailMessage bcc(String... address) {
        emailMessage.getBccAddresses().addAll(MailUtility.internetAddress(address));
        return this;
    }

    @Override public MailMessage bcc(InternetAddress emailAddress) {
        if (emailAddress != null) {
            emailMessage.getBccAddresses().add(emailAddress);
        }
        return this;
    }

    @Override public MailMessage bcc(EmailContact emailContact) {
        if (emailContact != null) {
            emailMessage.getBccAddresses().add(MailUtility.internetAddress(emailContact));
        }
        return this;
    }

    @Override public MailMessage bcc(Collection<? extends EmailContact> emailContacts) {
        emailMessage.getBccAddresses().addAll(MailUtility.internetAddress(emailContacts));
        return this;
    }

    @Override public MailMessage envelopeFrom(EmailContact emailContact) {
        if (emailContact != null) {
            emailMessage.setEnvelopeFrom(MailUtility.internetAddress(emailContact));
        }
        return this;
    }

    @Override public MailMessage envelopeFrom(String address) {
        if (address != null) {
            emailMessage.setEnvelopeFrom(MailUtility.internetAddress(address));
        }
        return this;
    }

    // End Addressing

    @Override public MailMessage subject(String value) {
        emailMessage.setSubject(value);
        return this;
    }

    @Override public MailMessage deliveryReceipt(String address) {
        emailMessage.getDeliveryReceiptAddresses().add(MailUtility.internetAddress(address));
        return this;
    }

    @Override public MailMessage readReceipt(String address) {
        emailMessage.getReadReceiptAddresses().add(MailUtility.internetAddress(address));
        return this;
    }

    @Override public MailMessage importance(MessagePriority messagePriority) {
        emailMessage.setImportance(messagePriority);
        return this;
    }

    @Override public MailMessage messageId(String messageId) {
        emailMessage.setMessageId(messageId);
        return this;
    }

    @Override public MailMessage bodyText(String text) {
        emailMessage.setTextBody(text);
        return this;
    }

    @Override public MailMessage bodyHtml(String html) {
        emailMessage.setHtmlBody(html);
        return this;
    }

    @Override public MailMessage createTextAlternative(boolean value){
        createTextAlternative = value;
        return this;
    }

    @Override public MailMessage bodyHtmlTextAlt(String html, String text) {
        emailMessage.setTextBody(text);
        emailMessage.setHtmlBody(html);
        return this;
    }

    // Begin Attachments

    @Override public MailMessage addAttachment(EmailAttachment attachment) {
        emailMessage.addAttachment(attachment);
        return this;
    }

    @Override public MailMessage addAttachments(Collection<EmailAttachment> attachments) {
        emailMessage.addAttachments(attachments);
        return this;
    }

    @Override public MailMessage addAttachment(String fileName, String mimeType, ContentDisposition contentDispostion, byte[] bytes) {
        addAttachment(new BaseAttachment(fileName, mimeType, contentDispostion, bytes));
        return this;
    }

    @Override public MailMessage addAttachment(String fileName, String mimeType, ContentDisposition contentDispostion, InputStream inputStream) {
        addAttachment(new InputStreamAttachment(fileName, mimeType, contentDispostion, inputStream));
        return this;
    }

    @Override public MailMessage addAttachment(ContentDisposition contentDispostion, File file) {
        addAttachment(new FileAttachment(contentDispostion, file));
        return this;
    }

    // End Attachments

    // Begin Calendar

    @Override public MailMessage iCal(String htmlBody, String textBody, ICalMethod method, byte[] bytes) {
        emailMessage.setType(EmailMessageType.INVITE_ICAL);
        emailMessage.setHtmlBody(htmlBody);
        emailMessage.setTextBody(textBody);
        emailMessage.addAttachment(new BaseAttachment(null, "text/calendar;method=" + method, ContentDisposition.INLINE, bytes, "urn:content-classes:calendarmessage"));
        return this;
    }

    @Override public MailMessage iCal(String textBody, ICalMethod method, byte[] bytes) {
        emailMessage.setType(EmailMessageType.INVITE_ICAL);
        emailMessage.setTextBody(textBody);
        emailMessage.addAttachment(new BaseAttachment(null, "text/calendar;method=" + method, ContentDisposition.INLINE, bytes, "urn:content-classes:calendarmessage"));
        return this;
    }

    // End Calendar

    @Override public MailMessage subject(TemplateProvider subject) {
        subjectTemplate = subject;
        return this;
    }

    @Override public MailMessage bodyText(TemplateProvider textBody) {
        textTemplate = textBody;
        return this;
    }

    @Override public MailMessage bodyHtml(TemplateProvider htmlBody) {
        htmlTemplate = htmlBody;
        return this;
    }

    @Override public MailMessage bodyHtmlTextAlt(TemplateProvider htmlBody, TemplateProvider textBody) {
        bodyHtml(htmlBody);
        bodyText(textBody);
        return this;
    }

    @Override public MailMessage charset(String charset) {
        emailMessage.setCharset(charset);
        return this;
    }

    @Override public MailMessage contentType(ContentType contentType) {
        emailMessage.setRootContentType(contentType);
        return this;
    }

    @Override public MailMessage put(String key, Object value) {
        templateContext.put(key, value);
        return this;
    }

    @Override public MailMessage put(Map<String, Object> values) {
        templateContext.putAll(values);
        return this;
    }

    @Override public EmailMessage getEmailMessage() {
        return emailMessage;
    }

    @Override public void setEmailMessage(EmailMessage emailMessage) {
        this.emailMessage = emailMessage;
    }

    public void setMailTransporter(MailTransporter mailTransporter) {
        this.mailTransporter = mailTransporter;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    @Override public EmailMessage mergeTemplates() {

        put("mailContext", new MailContext(EmailAttachmentUtil.getEmailAttachmentMap(emailMessage.getAttachments())));

        if (subjectTemplate != null) {
            emailMessage.setSubject(subjectTemplate.merge(templateContext));
        }

        if (textTemplate != null) {
            emailMessage.setTextBody(textTemplate.merge(templateContext));
        }

        if (htmlTemplate != null) {
            emailMessage.setHtmlBody(htmlTemplate.merge(templateContext));
        }

        if (emailMessage.getHtmlBody() != null && createTextAlternative) {
            emailMessage.setTextBody(HtmlToPlainText.convert(emailMessage.getHtmlBody()));
        }

        templatesMerged = true;

        return emailMessage;
    }

    public EmailMessage send(MailTransporter mailTransporter) throws SendFailedException {
        if (!templatesMerged) {
            mergeTemplates();
        }

        try {
            mailTransporter.send(emailMessage);
        }
        catch (Exception e) {
            throw new SendFailedException("Send Failed", e);
        }

        return emailMessage;
    }

    private EmailMessage send(Session session) throws SendFailedException {
        return send(new MailTransporterImpl(session));
    }

    public EmailMessage send(SessionConfig mailConfig) {
        return send(MailUtility.createSession(mailConfig));
    }

    @Override public EmailMessage send() throws SendFailedException {
        if (mailTransporter != null) {
            return send(mailTransporter);
        }
        else if (session != null) {
            return send(session);
        }
        else if (mailConfig != null) {
            return send(mailConfig);
        }
        else {
            throw new SendFailedException("No Resource availiable to send. How was this constructed?");
        }
    }
}
