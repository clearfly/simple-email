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

package com.outjected.email.api;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import jakarta.mail.internet.InternetAddress;

/**
 * Base interface for creating email messages.
 */
public interface MailMessage {

    /**
     * Convenience varargs method to add FROM address. Only one is allowed.
     *
     * @param address Address of the recipient eq "john.doe@example.com" or "John Doe<john.doe@example.com>"
     * @throws InvalidAddressException if address(es) are in an invalid format
     */
    MailMessage from(String address);

    /**
     * Adds a From Address
     *
     * @param emailAddress {@link InternetAddress} of the address to be added
     */
    MailMessage from(InternetAddress emailAddress);

    /**
     * Adds a From Address
     *
     * @param emailContact {@link EmailContact} of the address to be added
     */
    MailMessage from(EmailContact emailContact);

    /**
     * Convenience varargs method to add REPLY-TO address. Only one is allowed
     *
     * @param address Address of the recipient eq "john.doe@example.com" or "John Doe<john.doe@example.com>"
     * @throws InvalidAddressException if address(es) are in an invalid format
     */
    MailMessage replyTo(String address);

    /**
     * Adds a REPLY-TO Address
     *
     * @param emailAddress {@link InternetAddress} of the address to be added
     */
    MailMessage replyTo(InternetAddress emailAddress);

    /**
     * Adds a REPLY-TO Address
     *
     * @param emailContact {@link EmailContact} of the address to be added
     */
    MailMessage replyTo(EmailContact emailContact);

    /**
     * Add header to the message.
     *
     * @param name  Header name
     * @param value Header value
     */
    MailMessage addHeader(String name, String value);

    /**
     * Add header to the message.
     *
     * @param name  Header name
     * @param value Header value
     */
    MailMessage addCustomVariable(String name, String value);

    /**
     * Convenience varargs method to add TO address(es)
     *
     * @param address Address of the recipient eq "john.doe@example.com" or "John Doe<john.doe@example.com>"
     * @throws InvalidAddressException if address(es) are in an invalid format
     */
    MailMessage to(String... address);

    /**
     * Add TO recipient
     *
     * @param emailAddress {@link InternetAddress} of the address to be added
     */
    MailMessage to(InternetAddress emailAddress);

    /**
     * Add TO recipient
     *
     * @param emailContact {@link EmailContact} of the address to be added
     */
    MailMessage to(EmailContact emailContact);

    /**
     * Convenience method to add a TO recipients
     *
     * @param emailContacts Collection of {@link EmailContact} to be added
     */
    MailMessage to(Collection<? extends EmailContact> emailContacts);

    /**
     * Convenience varargs method to add CC address(es)
     *
     * @param address Address of the recipient eq "john.doe@example.com" or "John Doe<john.doe@example.com>"
     * @throws InvalidAddressException if address(es) are in an invalid format
     */
    MailMessage cc(String... address);

    /**
     * Add CC (Carbon Copy) recipient
     *
     * @param emailAddress {@link InternetAddress} of the address to be added
     */
    MailMessage cc(InternetAddress emailAddress);

    /**
     * Add CC recipient
     *
     * @param emailContact {@link EmailContact} of the address to be added
     */
    MailMessage cc(EmailContact emailContact);

    /**
     * Add collection of CC (Carbon Copy) recipients
     *
     * @param emailContacts Collection of {@link EmailContact} to be added
     */
    MailMessage cc(Collection<? extends EmailContact> emailContacts);

    /**
     * Convenience varargs method to add BCC address(es)
     *
     * @param address Address of the recipient eq "john.doe@example.com" or "John Doe<john.doe@example.com>"
     * @throws InvalidAddressException if address(es) are in an invalid format
     */
    MailMessage bcc(String... address);

    /**
     * Add BCC (Blind Carbon Copy) recipient
     *
     * @param emailAddress {@link InternetAddress} of the address to be added
     */
    MailMessage bcc(InternetAddress emailAddress);

    /**
     * Add BCC recipient
     *
     * @param emailContact {@link EmailContact} of the address to be added
     */
    MailMessage bcc(EmailContact emailContact);

    /**
     * Add collection of BCC (Blind Carbon Copy) recipients
     *
     * @param emailContacts Collection of {@link EmailContact} to be added
     */
    MailMessage bcc(Collection<? extends EmailContact> emailContacts);

    /**
     * Set the "Envelope From" address which is used for error messages
     */
    MailMessage envelopeFrom(EmailContact emailContact);

    /**
     * Set the "Envelope From" address which is used for error messages
     */
    MailMessage envelopeFrom(String address);

    // End Recipients

    // Begin Attachments

    /**
     * Adds Attachment to the message
     *
     * @param attachment {@link EmailAttachment} to be added
     */
    MailMessage addAttachment(EmailAttachment attachment);

    /**
     * Adds a Collection of Attachments to the message
     **/
    MailMessage addAttachments(Collection<EmailAttachment> attachments);

    /**
     * Adds Attachment to the message
     */
    MailMessage addAttachment(String fileName, String mimeType, ContentDisposition contentDispostion, byte[] bytes);

    /**
     * Adds Attachment to the message
     */
    MailMessage addAttachment(String fileName, String mimeType, ContentDisposition contentDispostion, InputStream inputStream);

    /**
     * Adds Attachment to the message
     */
    MailMessage addAttachment(ContentDisposition contentDispostion, File file);

    // End Attachements

    // Begin Flags

    /**
     * Sets the importance level of the message with a given {@link MessagePriority}
     *
     * @param messagePriority The priority level of the message.
     */
    MailMessage importance(MessagePriority messagePriority);

    /**
     * Request a delivery receipt "Return-Receipt-To" to the given address
     *
     * @param address Email address the receipt should be sent to
     * @throws InvalidAddressException if address is in invalid format
     */
    MailMessage deliveryReceipt(String address);

    /**
     * Request a read receipt "Disposition-Notification-To" to a given address
     *
     * @param address Email address the receipt should be sent to
     * @throws InvalidAddressException if address is in invalid format
     */
    MailMessage readReceipt(String address);

    /**
     * Set the Message-ID for the message.
     */
    MailMessage messageId(String messageId);

    // End Flags

    // Begin Calendar

    /**
     * Used for creating iCal Calendar Invites.
     */
    MailMessage iCal(String textBody, ICalMethod method, byte[] bytes);

    /**
     * Used for creating iCal Calendar Invites.
     */
    MailMessage iCal(String htmlBody, String textBody, ICalMethod method, byte[] bytes);

    // End Calendar

    // Begin Core

    /**
     * Set the subject on the message
     *
     * @param value Subject of the message
     */
    MailMessage subject(String value);

    /**
     * Sets the body of the message a plan text body represented by the supplied string
     *
     * @param text Plain text body
     */
    MailMessage bodyText(String text);

    /**
     * Sets the body of the message a HTML body represented by the supplied string
     *
     * @param html HTML body
     */
    MailMessage bodyHtml(String html);

    MailMessage createTextAlternative(boolean value);

    /**
     * Sets the body of the message to a HTML body with a plain text alternative
     *
     * @param html HTML body
     * @param text Plain text body
     */
    MailMessage bodyHtmlTextAlt(String html, String text);

    /**
     * Set the charset of the message. Otherwise defaults to Charset.defaultCharset()
     */
    MailMessage charset(String charset);

    /**
     * Set the Content Type of the message
     */
    MailMessage contentType(ContentType contentType);

    // End Core

    /**
     * Get the {@link EmailMessage} representing this {@link MailMessage}
     *
     * @return {@link EmailMessage} representing this {@link MailMessage}
     */
    EmailMessage getEmailMessage();

    /**
     * Set the {@link EmailMessage} representing this {@link MailMessage}
     */
    void setEmailMessage(EmailMessage emailMessage);

    /**
     * Merge the templates with the context
     *
     * @return {@link EmailMessage} representing this {@link MailMessage} after merging
     */
    EmailMessage mergeTemplates();

    /**
     * Send the Message
     *
     * @return {@link EmailMessage} which represents the {@link MailMessage} as sent
     * @throws SendFailedException If the messages fails to be sent.
     */
    EmailMessage send();

    // Templating Specific

    /**
     * Set the template to be used for the message subject
     */
    MailMessage subject(TemplateProvider subject);

    /**
     * Sets the text body of the message to the plain text output of the given template
     *
     * @param textBody {@link TemplateProvider} to use
     */
    MailMessage bodyText(TemplateProvider textBody);

    /**
     * Sets the HTML body of the message to the HTML output of the given template
     *
     * @param htmlBody {@link TemplateProvider} to use
     */
    MailMessage bodyHtml(TemplateProvider htmlBody);

    /**
     * Sets the body of the message to a HTML body with a plain text alternative output of the given templates
     *
     * @param htmlBody {@link TemplateProvider} to use for HTML portion of message
     * @param textBody {@link TemplateProvider} to use for Text alternative portion of message
     */
    MailMessage bodyHtmlTextAlt(TemplateProvider htmlBody, TemplateProvider textBody);

    /**
     * Places a variable in the templating engines context
     *
     * @param name  Reference name of the object
     * @param value the Object being placed in the context
     */
    MailMessage put(String name, Object value);

    /**
     * Places a Map of variable in the templating engines context
     *
     * @param values {@code Map<String, Object>} containing the variables to be placed in the context
     */
    MailMessage put(Map<String, Object> values);

    MailMessage enableClickTracking();

    MailMessage disableClickTracking();
}
