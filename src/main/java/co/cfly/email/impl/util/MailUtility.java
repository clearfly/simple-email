/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package co.cfly.email.impl.util;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import co.cfly.email.api.EmailContact;
import co.cfly.email.api.EmailMessage;
import co.cfly.email.api.EmailMessageType;
import co.cfly.email.api.Header;
import co.cfly.email.api.InvalidAddressException;
import co.cfly.email.api.MailException;
import co.cfly.email.api.RecipientType;
import co.cfly.email.api.SendFailedException;
import co.cfly.email.api.SessionConfig;
import co.cfly.email.impl.BaseMailMessage;
import co.cfly.email.impl.MailSessionAuthenticator;
import co.cfly.email.impl.RootMimeMessage;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeUtility;
import jakarta.mail.internet.ParseException;

public class MailUtility {

    public static final String DOMAIN_PROPERTY_KEY = "co.cfly.email.domainName";
    public static final Pattern CHARSET_EXTRACT = Pattern.compile("charset\\s*=\\s*\"?([^\";]*)\"?", Pattern.CASE_INSENSITIVE);

    public static InternetAddress internetAddress(String address) throws InvalidAddressException {
        try {
            return new InternetAddress(address, true);
        }
        catch (AddressException e) {
            throw new InvalidAddressException("Must be in format of a@b.com or Name<a@b.com> but was: \"" + address + "\"", e);
        }
    }

    public static Collection<InternetAddress> internetAddress(String... addresses) throws InvalidAddressException {
        ArrayList<InternetAddress> result = new ArrayList<>();

        for (String address : addresses) {
            result.add(MailUtility.internetAddress(address));
        }
        return result;
    }

    public static InternetAddress internetAddress(String address, String name) throws InvalidAddressException {
        InternetAddress internetAddress;
        try {
            internetAddress = new InternetAddress(address);
            internetAddress.setPersonal(name);
            return internetAddress;
        }
        catch (AddressException | UnsupportedEncodingException e) {
            throw new InvalidAddressException(e);
        }
    }

    public static InternetAddress internetAddress(EmailContact emailContact) throws InvalidAddressException {
        if (Strings.isNullOrBlank(emailContact.getName())) {
            return MailUtility.internetAddress(emailContact.getAddress());
        }
        else {
            return MailUtility.internetAddress(emailContact.getAddress(), emailContact.getName());
        }
    }

    public static Collection<InternetAddress> internetAddress(Collection<? extends EmailContact> emailContacts) throws InvalidAddressException {
        return emailContacts.stream().filter(Objects::nonNull).map(MailUtility::internetAddress).collect(Collectors.toList());
    }

    public static InternetAddress[] getInternetAddressses(InternetAddress emailAddress) {
        return new InternetAddress[] { emailAddress };
    }

    public static InternetAddress[] getInternetAddressses(Collection<InternetAddress> recipients) {
        return recipients.stream().filter(Objects::nonNull).toArray(InternetAddress[]::new);
    }

    public static String getHostName() {
        try {
            java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
            return localMachine.getHostName();
        }
        catch (UnknownHostException e) {
            return "localhost";
        }
    }

    public static Collection<InternetAddress> getInternetAddressses(Address[] addresses) throws InvalidAddressException {
        final ArrayList<InternetAddress> result = new ArrayList<>();
        if (addresses != null) {
            for (Address a : addresses) {
                if (a.getType().equals("rfc822")) {
                    try {
                        result.add(new InternetAddress(a.toString()));
                    }
                    catch (AddressException e) {
                        throw new InvalidAddressException(e);
                    }
                }
                else {
                    throw new InvalidAddressException("Not type RFC822");
                }
            }
        }
        return result;
    }

    public static List<Header> getHeaders(Enumeration<?> allHeaders) {
        List<Header> result = new ArrayList<>();
        while (allHeaders.hasMoreElements()) {
            jakarta.mail.Header h = (jakarta.mail.Header) allHeaders.nextElement();
            result.add(new Header(h.getName(), h.getValue()));
        }
        return result;
    }

    public static Session createSession(SessionConfig mailConfig) {

        Session session;

        Properties props = new Properties();

        if (!Strings.isNullOrBlank(mailConfig.getServerHost()) && mailConfig.getServerPort() > 0) {
            props.setProperty("mail.smtp.host", mailConfig.getServerHost());
            props.setProperty("mail.smtp.port", mailConfig.getServerPort().toString());
            props.setProperty("mail.smtp.starttls.enable", mailConfig.getEnableTls().toString());
            props.setProperty("mail.smtp.starttls.required", mailConfig.getRequireTls().toString());
            props.setProperty("mail.smtp.ssl.enable", mailConfig.getEnableSsl().toString());
            props.setProperty("mail.smtp.auth", mailConfig.getAuth().toString());
        }
        else {
            throw new MailException("Server Host and Server  Port must be set in MailConfig");
        }

        if (!Strings.isNullOrBlank(mailConfig.getDomainName())) {
            props.put(MailUtility.DOMAIN_PROPERTY_KEY, mailConfig.getDomainName());
        }

        if (mailConfig.getUsername() != null && !mailConfig.getUsername().isEmpty() && mailConfig.getPassword() != null && !mailConfig.getPassword().isEmpty()) {
            MailSessionAuthenticator authenticator = new MailSessionAuthenticator(mailConfig.getUsername(), mailConfig.getPassword());

            session = Session.getInstance(props, authenticator);
        }
        else {
            session = Session.getInstance(props, null);
        }

        return session;
    }

    public static String headerStripper(String header) {
        if (!Strings.isNullOrBlank(header)) {
            String s = header.trim();

            if (s.matches("^<.*>$")) {
                return header.substring(1, header.length() - 1);
            }
            else {
                return header;
            }
        }
        else {
            return header;
        }
    }

    public static RootMimeMessage createMimeMessage(EmailMessage e, Session session) {
        BaseMailMessage b = new BaseMailMessage(session, e.getCharset(), e.getRootContentType());

        if (!Strings.isNullOrBlank(e.getMessageId())) {
            b.setMessageID(e.getMessageId());
        }

        b.setFrom(e.getFromAddresses());
        b.addRecipients(RecipientType.TO, e.getToAddresses());
        b.addRecipients(RecipientType.CC, e.getCcAddresses());
        b.addRecipients(RecipientType.BCC, e.getBccAddresses());
        b.setReplyTo(e.getReplyToAddresses());
        b.addDeliveryRecieptAddresses(e.getDeliveryReceiptAddresses());
        b.addReadRecieptAddresses(e.getReadReceiptAddresses());
        b.setImportance(e.getImportance());
        b.addHeaders(e.getHeaders());
        b.setEnvelopeFrom(MailUtility.nullSafeAddress(e.getEnvelopeFrom()));

        if (e.getSubject() != null) {
            b.setSubject(e.getSubject());
        }

        if (e.getType() == EmailMessageType.STANDARD) {

            if (e.getHtmlBody() != null && e.getTextBody() != null) {
                b.setHTMLTextAlt(e.getHtmlBody(), e.getTextBody());
            }
            else if (e.getTextBody() != null) {
                b.setText(e.getTextBody());
            }
            else if (e.getHtmlBody() != null) {
                b.setHTML(e.getHtmlBody());
            }

            b.addAttachments(e.getAttachments());
        }
        else if (e.getType() == EmailMessageType.INVITE_ICAL) {
            if (e.getHtmlBody() != null) {
                b.setHTMLNotRelated(e.getHtmlBody());
            }
            else {
                b.setText(e.getTextBody());
            }
            b.addAttachments(e.getAttachments());
        }
        else {
            throw new SendFailedException("Unsupported Message Type: " + e.getType());
        }

        return b.getFinalizedMessage();
    }

    public static String nullSafeAddress(InternetAddress value) {
        return value != null ? value.getAddress() : null;
    }

    public static void send(EmailMessage e, Session session) throws SendFailedException {
        RootMimeMessage msg = MailUtility.createMimeMessage(e, session);
        try {
            Transport.send(msg);
        }
        catch (MessagingException e1) {
            throw new SendFailedException("Send Failed", e1);
        }

        try {
            e.setMessageId(MailUtility.headerStripper(msg.getMessageID()));
        }
        catch (MessagingException e1) {
            throw new SendFailedException("Unable to read Message-ID from sent message");
        }
    }

    /**
     * Evaluate a string to determine if it is RFC 2047 encoded. If so, attempt to decode. If not, or upon exception, return
     * original value
     *
     * @param value String to be evaluated
     * @return The evaluated and possibly decoded string
     */
    public static String decodeString(String value) {
        if (Objects.isNull(value)) {
            return null;
        }
        if (value.matches("^=\\?.*\\?[bBqQ]\\?.*\\?=$")) {
            try {
                return MimeUtility.decodeWord(value);
            }
            catch (ParseException | UnsupportedEncodingException e) {
                return value;
            }
        }
        else {
            return value;
        }
    }

    /**
     * Determines the content type of the part, or empty if it cannot determine
     */
    public static Charset determineCharset(Part part, Charset defaultValue) throws MessagingException {
        return Arrays.stream(Optional.ofNullable(part.getHeader("Content-Type")).orElseGet(() -> new String[0])).findFirst().map(value -> {
            Matcher matcher = CHARSET_EXTRACT.matcher(value);
            if (matcher.find()) {
                String charsetName = matcher.group(1).trim().toUpperCase();
                return switch (charsetName) {
                    case "CP-850" -> Charset.forName("CP850");
                    default -> Charset.forName(charsetName);
                };
            }
            else {
                return null;
            }
        }).orElse(defaultValue);
    }
}
