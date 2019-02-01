/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package com.outjected.email.api;

import java.nio.charset.Charset;
import java.util.*;

import javax.mail.internet.InternetAddress;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.outjected.email.impl.attachments.BaseAttachment;

/**
 * Stores information about an EmailMessage while it is being built and after sending
 *
 * @author Cody Lerum
 */
@XmlRootElement @XmlType(propOrder = { "messageId", "importance", "charset", "fromAddresses", "replyToAddresses", "toAddresses", "ccAddresses", "bccAddresses", "envelopeFrom",
        "deliveryReceiptAddresses", "readReceiptAddresses", "subject", "textBody", "htmlBody", "headers", "customVariables", "rootContentType", "type", "attachments" }) public class EmailMessage {
    private String charset = Charset.defaultCharset().name();
    private ContentType rootContentType = ContentType.MIXED;
    private EmailMessageType type = EmailMessageType.STANDARD;
    private String messageId;
    private List<InternetAddress> fromAddresses = new ArrayList<InternetAddress>();
    private List<InternetAddress> replyToAddresses = new ArrayList<InternetAddress>();
    private List<InternetAddress> toAddresses = new ArrayList<InternetAddress>();
    private List<InternetAddress> ccAddresses = new ArrayList<InternetAddress>();
    private List<InternetAddress> bccAddresses = new ArrayList<InternetAddress>();
    private InternetAddress envelopeFrom;
    private List<Header> headers = new ArrayList<>();
    private Map<String, String> customVariables = new TreeMap<>();

    private String subject;
    private String textBody;
    private String htmlBody;

    private List<BaseAttachment> attachments = new ArrayList<BaseAttachment>();

    private List<InternetAddress> deliveryReceiptAddresses = new ArrayList<InternetAddress>();
    private List<InternetAddress> readReceiptAddresses = new ArrayList<InternetAddress>();

    private MessagePriority importance = MessagePriority.NORMAL;

    /**
     * Get the charset used to encode the EmailMessage
     *
     * @return charset of the EmailMessage
     */
    @XmlElement public String getCharset() {
        return charset;
    }

    /**
     * Override the default charset of the JVM
     *
     * @param charset
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * Get the Root Mime ContentType of the EmailMessage
     *
     * @return Root Mime ContentType of the EmailMessage
     */
    @XmlElement public ContentType getRootContentType() {
        return rootContentType;
    }

    /**
     * Set the Root Mime ContentType of the EmailMessage
     *
     * @param rootContentType SubType to set
     */
    public void setRootContentType(ContentType rootContentType) {
        this.rootContentType = rootContentType;
    }

    /**
     * Get the current EmailMessageType of the EmailMessage
     *
     * @return EmailMessageType of this EmailMessage
     */
    @XmlElement public EmailMessageType getType() {
        return type;
    }

    /**
     * Sets the EmailMessageType of the EmailMessage
     *
     * @param type EmailMessageType to set on the EmailMessage
     */
    public void setType(EmailMessageType type) {
        this.type = type;
    }

    /**
     * Gets the Message-ID of the EmailMeassage. This nulled after sending.
     *
     * @return Message-ID of the EmailMeassage
     */
    @XmlElement public String getMessageId() {
        return messageId;
    }

    /**
     * Sets the Message-ID for the EmailMeassage. Should be in RFC822 format
     *
     * @param messageId Globally unique Message-ID example 1234.5678@test.com
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * Get the Collection of FROM addresses on the EmailMeassage
     *
     * @return Collection of InternetAddresses addresses
     */
    @XmlElement public List<InternetAddress> getFromAddresses() {
        return fromAddresses;
    }

    public void setFromAddresses(List<InternetAddress> fromAddresses) {
        this.fromAddresses = fromAddresses;
    }

    /**
     * Get the Collection of REPLY-TO addresses on the EmailMeassage
     *
     * @return Collection of InternetAddresses addresses
     */
    @XmlElement public List<InternetAddress> getReplyToAddresses() {
        return replyToAddresses;
    }

    public void setReplyToAddresses(List<InternetAddress> replyToAddresses) {
        this.replyToAddresses = replyToAddresses;
    }

    /**
     * Get the Collection of TO addresses on the EmailMeassage
     *
     * @return Collection of InternetAddresses addresses
     */
    @XmlElement public List<InternetAddress> getToAddresses() {
        return toAddresses;
    }

    /**
     * Get the Collection of CC addresses on the EmailMeassage
     *
     * @return Collection of InternetAddresses addresses
     */
    @XmlElement public List<InternetAddress> getCcAddresses() {
        return ccAddresses;
    }

    public void setCcAddresses(List<InternetAddress> ccAddresses) {
        this.ccAddresses = ccAddresses;
    }

    /**
     * Get the Collection of BCC addresses on the EmailMeassage
     *
     * @return Collection of InternetAddresses addresses
     */
    @XmlElement public List<InternetAddress> getBccAddresses() {
        return bccAddresses;
    }

    public void setBccAddresses(List<InternetAddress> bccAddresses) {
        this.bccAddresses = bccAddresses;
    }

    /**
     * Gets the "Envelope From" address which is used for error messages
     *
     * @return
     */
    @XmlElement public InternetAddress getEnvelopeFrom() {
        return envelopeFrom;
    }

    /**
     * Sets the "Envelope From" address which is used for error messages
     *
     * @param address
     */
    public void setEnvelopeFrom(InternetAddress address) {
        this.envelopeFrom = address;
    }

    /**
     * Get a Collection of additional headers added to the EmailMessage
     *
     * @return Collection of Header
     */
    @XmlElementWrapper(name = "headers") @XmlElement(name = "header") public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    @XmlElementWrapper(name = "customVariables") @XmlElement(name = "customVariable") public Map<String, String> getCustomVariables() {
        return customVariables;
    }

    public void setCustomVariables(Map<String, String> customVariables) {
        this.customVariables = customVariables;
    }

    /**
     * Get the Subject of the EmailMessage
     *
     * @return The Subject
     */
    @XmlElement public String getSubject() {
        return subject;
    }

    /**
     * Sets the Subject on the EmailMessage
     *
     * @param subject Subject to be set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Get the Text Body of the EmailMessage
     *
     * @return The EmailMessage Text Body.
     */
    @XmlElement public String getTextBody() {
        return textBody;
    }

    /**
     * Set the Text Body of the EmailMessage
     *
     * @param textBody Text Body to be set
     */
    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }

    /**
     * Get the HTML Body of the EmailMessage
     *
     * @return The EmailMessage HTML Body.
     */
    @XmlElement public String getHtmlBody() {
        return htmlBody;
    }

    /**
     * Set the HTML Body of the EmailMessage
     *
     * @param htmlBody HTML Body to be set
     */
    public void setHtmlBody(String htmlBody) {
        this.htmlBody = htmlBody;
    }

    /**
     * Get the collection of InternetAddress which are Delivery Reciept addresses
     *
     * @return Collection of InternetAddress
     */
    @XmlElement public List<InternetAddress> getDeliveryReceiptAddresses() {
        return deliveryReceiptAddresses;
    }

    public void setDeliveryReceiptAddresses(List<InternetAddress> deliveryReceiptAddresses) {
        this.deliveryReceiptAddresses = deliveryReceiptAddresses;
    }

    /**
     * Get the collection of InternetAddress which are Read Receipt addresses
     *
     * @return Collection of InternetAddress
     */
    public List<InternetAddress> getReadReceiptAddresses() {
        return readReceiptAddresses;
    }

    public void setReadReceiptAddresses(List<InternetAddress> readReceiptAddresses) {
        this.readReceiptAddresses = readReceiptAddresses;
    }

    /**
     * Get the Current Importance of the EmailMessage. Default is normal. No Header added
     *
     * @return MessagePriority of EmailMessage
     */
    public MessagePriority getImportance() {
        return importance;
    }

    /**
     * Sets the MessagePriority of the EmailMessage
     *
     * @param importance MessagePriority to be set.
     */
    public void setImportance(MessagePriority importance) {
        this.importance = importance;
    }

    /**
     * Adds an BaseAttachment to the EmailMessage
     *
     * @param attachment EmailAttachment to be added
     */
    public void addAttachment(BaseAttachment attachment) {
        attachments.add(attachment);
    }

    /**
     * Adds an EmailAttachment to the EmailMessage
     *
     * @param attachment EmailAttachment to be added
     */
    public void addAttachment(EmailAttachment attachment) {
        BaseAttachment ba = new BaseAttachment(attachment.getFileName(), attachment.getMimeType(), attachment.getContentDisposition(), attachment.getBytes());
        attachments.add(ba);
    }

    /**
     * Adds a Collection of EmailAttachment to the EmailMessage
     *
     * @param attachments Collection of EmailAttachment
     */
    public void addAttachments(Collection<EmailAttachment> attachments) {
        for (EmailAttachment e : attachments) {
            addAttachment(e);
        }
    }

    /**
     * Gets a Collection representing all the Attachments on the EmailMessage
     *
     * @return Collection of EmailAttachment
     */
    @XmlElementWrapper(name = "attachments") @XmlElement(name = "attachment") public List<BaseAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<BaseAttachment> attachments) {
        this.attachments = attachments;
    }
}
