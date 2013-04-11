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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.mail.internet.InternetAddress;

/**
 * Stores information about an EmailMessage while it is being built and after sending
 * 
 * @author Cody Lerum
 */
public class EmailMessage {
    private String charset = Charset.defaultCharset().name();
    private ContentType rootContentType = ContentType.MIXED;
    private EmailMessageType type = EmailMessageType.STANDARD;
    private String messageId;
    private String lastMessageId;
    private List<InternetAddress> fromAddresses = new ArrayList<InternetAddress>();
    private List<InternetAddress> replyToAddresses = new ArrayList<InternetAddress>();
    private List<InternetAddress> toAddresses = new ArrayList<InternetAddress>();
    private List<InternetAddress> ccAddresses = new ArrayList<InternetAddress>();
    private List<InternetAddress> bccAddresses = new ArrayList<InternetAddress>();
    private List<Header> headers = new ArrayList<Header>();

    private String subject;
    private String textBody;
    private String htmlBody;

    private List<EmailAttachment> attachments = new ArrayList<EmailAttachment>();

    private List<InternetAddress> deliveryReceiptAddresses = new ArrayList<InternetAddress>();
    private List<InternetAddress> readReceiptAddresses = new ArrayList<InternetAddress>();

    private MessagePriority importance = MessagePriority.NORMAL;

    /**
     * Get the charset used to encode the EmailMessage
     * 
     * @return charset of the EmailMessage
     */
    public String getCharset() {
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
    public ContentType getRootContentType() {
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
    public EmailMessageType getType() {
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
    public String getMessageId() {
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
     * Gets the Message-ID after the EmailMeassage has been sent.
     * 
     * @return Message-ID which was set on the last send.
     */
    public String getLastMessageId() {
        return lastMessageId;
    }

    /**
     * Sents the Message-ID after the EmailMeassage has been sent.
     * 
     * @param lastMessageId Message-ID to be set.
     */
    public void setLastMessageId(String lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    /**
     * Get the Collection of FROM addresses on the EmailMeassage
     * 
     * @return Collection of InternetAddresses addresses
     */
    public List<InternetAddress> getFromAddresses() {
        return fromAddresses;
    }

    public void setFromAddresses(List<InternetAddress> fromAddresses) {
        this.fromAddresses = fromAddresses;
    }

    /**
     * Adds a single InternetAddress to the FROM addresses on the EmailMessage
     * 
     * @param fromAddress
     */
    public void addFromAddress(InternetAddress fromAddress) {
        this.fromAddresses.add(fromAddress);
    }

    /**
     * Adds a Collection of InternetAddress to the FROM addresses on the EmailMessage
     * 
     * @param fromAddresses
     */
    public void addFromAddresses(Collection<InternetAddress> fromAddresses) {
        this.fromAddresses.addAll(fromAddresses);
    }

    /**
     * Get the Collection of REPLY-TO addresses on the EmailMeassage
     * 
     * @return Collection of InternetAddresses addresses
     */
    public List<InternetAddress> getReplyToAddresses() {
        return replyToAddresses;
    }

    public void setReplyToAddresses(List<InternetAddress> replyToAddresses) {
        this.replyToAddresses = replyToAddresses;
    }

    /**
     * Adds a single InternetAddress to the REPLY-TO addresses on the EmailMessage
     * 
     * @param replyToAddress InternetAddress to set
     */
    public void addReplyToAddress(InternetAddress replyToAddress) {
        this.replyToAddresses.add(replyToAddress);
    }

    /**
     * Adds a Collection of InternetAddress to the REPLY-TO addresses on the
     * 
     * @param replyToAddresses Collection of InternetAddress to add
     */
    public void addReplyToAddresses(Collection<InternetAddress> replyToAddresses) {
        this.replyToAddresses.addAll(replyToAddresses);
    }

    /**
     * Get the Collection of TO addresses on the EmailMeassage
     * 
     * @return Collection of InternetAddresses addresses
     */
    public List<InternetAddress> getToAddresses() {
        return toAddresses;
    }

    /**
     * Adds a single InternetAddress to the TO addresses on the EmailMessage
     * 
     * @param toAddress InternetAddress to set
     */
    public void addToAddress(InternetAddress toAddress) {
        this.toAddresses.add(toAddress);
    }

    /**
     * Adds a Collection of InternetAddress to the TO addresses on the
     * 
     * @param toAddresses Collection of InternetAddress to add
     */
    public void addToAddresses(Collection<InternetAddress> toAddresses) {
        this.toAddresses.addAll(toAddresses);
    }

    public void setToAddresses(List<InternetAddress> toAddresses) {
        this.toAddresses = toAddresses;
    }

    /**
     * Remove an InternetAddress from the TO addressses
     * 
     * @param toAddress
     * @return true if address was removed. false if it did not exist.
     */
    public boolean removeToAddress(InternetAddress toAddress) {
        return toAddresses.remove(toAddress);
    }

    /**
     * Get the Collection of CC addresses on the EmailMeassage
     * 
     * @return Collection of InternetAddresses addresses
     */
    public List<InternetAddress> getCcAddresses() {
        return ccAddresses;
    }

    public void setCcAddresses(List<InternetAddress> ccAddresses) {
        this.ccAddresses = ccAddresses;
    }

    /**
     * Adds a single InternetAddress to the CC addresses on the EmailMessage
     * 
     * @param ccAddress InternetAddress to set
     */
    public void addCcAddress(InternetAddress ccAddress) {
        this.ccAddresses.add(ccAddress);
    }

    /**
     * Adds a Collection of InternetAddress to the CC addresses on the
     * 
     * @param ccAddresses Collection of InternetAddress to add
     */
    public void addCcAddresses(Collection<InternetAddress> ccAddresses) {
        this.ccAddresses.addAll(ccAddresses);
    }

    /**
     * Remove an InternetAddress from the CC addressses
     * 
     * @param ccAddress
     * @return true if address was removed. false if it did not exist.
     */
    public boolean removeCcAddress(InternetAddress ccAddress) {
        return ccAddresses.remove(ccAddress);
    }

    /**
     * Get the Collection of BCC addresses on the EmailMeassage
     * 
     * @return Collection of InternetAddresses addresses
     */
    public List<InternetAddress> getBccAddresses() {
        return bccAddresses;
    }

    public void setBccAddresses(List<InternetAddress> bccAddresses) {
        this.bccAddresses = bccAddresses;
    }

    /**
     * Adds a single InternetAddress to the BCC addresses on the EmailMessage
     * 
     * @param bccAddress InternetAddress to set
     */
    public void addBccAddress(InternetAddress bccAddress) {
        this.bccAddresses.add(bccAddress);
    }

    /**
     * Adds a Collection of InternetAddress to the BCC addresses on the
     * 
     * @param bccAddresses Collection of InternetAddress to add
     */
    public void addBccAddresses(Collection<InternetAddress> bccAddresses) {
        this.bccAddresses.addAll(bccAddresses);
    }

    /**
     * Remove an InternetAddress from the BCC addressses
     * 
     * @param bccAddress
     * @return true if address was removed. false if it did not exist.
     */
    public boolean removeBccAddress(InternetAddress bccAddress) {
        return bccAddresses.remove(bccAddress);
    }

    /**
     * Get a Collection of additional headers added to the EmailMessage
     * 
     * @return Collection of Header
     */
    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    /**
     * Add a single Header to the EmailMessage
     * 
     * @param header Header to set
     */
    public void addHeader(Header header) {
        headers.add(header);
    }

    /**
     * Add a Collection of Header to the EmailMessage
     * 
     * @param headers Collection of Header to add to EmailMessage
     */
    public void addHeaders(Collection<Header> headers) {
        this.headers.addAll(headers);
    }

    /**
     * Get the Subject of the EmailMessage
     * 
     * @return The Subject
     */
    public String getSubject() {
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
    public String getTextBody() {
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
    public String getHtmlBody() {
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
    public List<InternetAddress> getDeliveryReceiptAddresses() {
        return deliveryReceiptAddresses;
    }

    public void setDeliveryReceiptAddresses(List<InternetAddress> deliveryReceiptAddresses) {
        this.deliveryReceiptAddresses = deliveryReceiptAddresses;
    }

    /**
     * Adds a InternetAddress as a Delivery Receipt address
     * 
     * @param address InternetAddress to be added
     */
    public void addDeliveryReceiptAddress(InternetAddress address) {
        deliveryReceiptAddresses.add(address);
    }

    /**
     * Adds a Collection of InternetAddress as a Delivery Receipt address
     * 
     * @param deliveryReceiptAddresses Collection of InternetAddress to be added
     */
    public void addDeliveryReceiptAddresses(Collection<InternetAddress> deliveryReceiptAddresses) {
        deliveryReceiptAddresses.addAll(deliveryReceiptAddresses);
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
     * Adds a InternetAddress as a Read Receipt address
     * 
     * @param address InternetAddress to be added
     */
    public void addReadReceiptAddress(InternetAddress address) {
        readReceiptAddresses.add(address);
    }

    /**
     * Adds a Collection of InternetAddress as a Read Receipt address
     * 
     * @param readReceiptAddresses Collection of InternetAddress to be added
     */
    public void addReadReceiptAddresses(Collection<InternetAddress> readReceiptAddresses) {
        readReceiptAddresses.addAll(readReceiptAddresses);
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
     * Adds an EmailAttachment to the EmailMessage
     * 
     * @param attachment EmailAttachment to be added
     */
    public void addAttachment(EmailAttachment attachment) {
        attachments.add(attachment);
    }

    /**
     * Adds a Collection of EmailAttachment to the EmailMessage
     * 
     * @param attachments Collection of EmailAttachment
     */
    public void addAttachments(Collection<? extends EmailAttachment> attachments) {
        for (EmailAttachment e : attachments) {
            addAttachment(e);
        }
    }

    /**
     * Gets a Collection representing all the Attachments on the EmailMessage
     * 
     * @return Collection of EmailAttachment
     */
    public List<EmailAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<EmailAttachment> attachments) {
        this.attachments = attachments;
    }
}
