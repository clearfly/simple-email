/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package com.outjected.email.impl.util;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.outjected.email.api.EmailContact;
import com.outjected.email.api.EmailMessage;
import com.outjected.email.api.EmailMessageType;
import com.outjected.email.api.Header;
import com.outjected.email.api.InvalidAddressException;
import com.outjected.email.api.MailException;
import com.outjected.email.api.RecipientType;
import com.outjected.email.api.SendFailedException;
import com.outjected.email.api.SessionConfig;
import com.outjected.email.impl.BaseMailMessage;
import com.outjected.email.impl.MailSessionAuthenticator;
import com.sun.mail.smtp.SMTPMessage;

/**
 * @author Cody Lerum
 */
public class MailUtility
{

    public static final String DOMAIN_PROPERTY_KEY = "com.outjected.email.domainName";

    public static InternetAddress internetAddress(String address) throws InvalidAddressException
    {
        try
        {
            return new InternetAddress(address);
        }
        catch (AddressException e)
        {
            throw new InvalidAddressException("Must be in format of a@b.com or Name<a@b.com> but was: \""
                    + address + "\"", e);
        }
    }

    public static Collection<InternetAddress> internetAddress(String... addresses)
            throws InvalidAddressException
    {
        ArrayList<InternetAddress> result = new ArrayList<InternetAddress>();

        for (String address : addresses)
        {
            result.add(MailUtility.internetAddress(address));
        }
        return result;
    }

    public static InternetAddress internetAddress(String address, String name)
            throws InvalidAddressException
    {
        InternetAddress internetAddress;
        try
        {
            internetAddress = new InternetAddress(address);
            internetAddress.setPersonal(name);
            return internetAddress;
        }
        catch (AddressException e)
        {
            throw new InvalidAddressException(e);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new InvalidAddressException(e);
        }
    }

    public static InternetAddress internetAddress(EmailContact emailContact)
            throws InvalidAddressException
    {
        if (Strings.isNullOrBlank(emailContact.getName()))
        {
            return MailUtility.internetAddress(emailContact.getAddress());
        }
        else
        {
            return MailUtility.internetAddress(emailContact.getAddress(), emailContact.getName());
        }
    }

    public static Collection<InternetAddress> internetAddress(
            Collection<? extends EmailContact> emailContacts) throws InvalidAddressException
    {
        Set<InternetAddress> internetAddresses = new HashSet<InternetAddress>();

        for (EmailContact ec : emailContacts)
        {
            internetAddresses.add(MailUtility.internetAddress(ec));
        }

        return internetAddresses;
    }

    public static InternetAddress[] getInternetAddressses(InternetAddress emailAddress)
    {
        InternetAddress[] internetAddresses = { emailAddress };

        return internetAddresses;
    }

    public static InternetAddress[] getInternetAddressses(Collection<InternetAddress> recipients)
    {
        InternetAddress[] result = new InternetAddress[recipients.size()];
        recipients.toArray(result);
        return result;
    }

    public static String getHostName()
    {
        try
        {
            java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
            return localMachine.getHostName();
        }
        catch (UnknownHostException e)
        {
            return "localhost";
        }
    }

    public static List<InternetAddress> getInternetAddressses(Address[] addresses)
            throws InvalidAddressException
    {
        List<InternetAddress> result = new ArrayList<InternetAddress>();
        if (addresses != null)
        {
            for (Address a : addresses)
            {
                if (a.getType().equals("rfc822"))
                {
                    try
                    {
                        result.add(new InternetAddress(a.toString()));
                    }
                    catch (AddressException e)
                    {
                        throw new InvalidAddressException(e);
                    }
                }
                else
                {
                    throw new InvalidAddressException("Not type RFC822");
                }
            }
        }
        return result;
    }

    public static List<Header> getHeaders(Enumeration<?> allHeaders)
    {
        List<Header> result = new LinkedList<Header>();
        while (allHeaders.hasMoreElements())
        {
            javax.mail.Header h = (javax.mail.Header) allHeaders.nextElement();
            result.add(new Header(h.getName(), h.getValue()));
        }
        return result;
    }

    public static Session createSession(SessionConfig mailConfig)
    {

        if (!Strings.isNullOrBlank(mailConfig.getJndiSessionName()))
        {
            try
            {
                return InitialContext.doLookup(mailConfig.getJndiSessionName());
            }
            catch (NamingException e)
            {
                throw new MailException("Unable to lookup JNDI JavaMail Session", e);
            }
        }

        Session session;

        Properties props = new Properties();

        if (!Strings.isNullOrBlank(mailConfig.getServerHost()) && mailConfig.getServerPort() > 0)
        {
            props.setProperty("mail.smtp.host", mailConfig.getServerHost());
            props.setProperty("mail.smtp.port", mailConfig.getServerPort().toString());
            props.setProperty("mail.smtp.starttls.enable", mailConfig.getEnableTls().toString());
            props.setProperty("mail.smtp.starttls.required", mailConfig.getRequireTls().toString());
            props.setProperty("mail.smtp.ssl.enable", mailConfig.getEnableSsl().toString());
            props.setProperty("mail.smtp.auth", mailConfig.getAuth().toString());
        }
        else
        {
            throw new MailException("Server Host and Server  Port must be set in MailConfig");
        }

        if (!Strings.isNullOrBlank(mailConfig.getDomainName()))
        {
            props.put(MailUtility.DOMAIN_PROPERTY_KEY, mailConfig.getDomainName());
        }

        if (mailConfig.getUsername() != null && mailConfig.getUsername().length() != 0
                && mailConfig.getPassword() != null && mailConfig.getPassword().length() != 0)
        {
            MailSessionAuthenticator authenticator =
                    new MailSessionAuthenticator(mailConfig.getUsername(), mailConfig.getPassword());

            session = Session.getInstance(props, authenticator);
        }
        else
        {
            session = Session.getInstance(props, null);
        }

        return session;
    }

    public static String headerStripper(String header)
    {
        if (!Strings.isNullOrBlank(header))
        {
            String s = header.trim();

            if (s.matches("^<.*>$"))
            {
                return header.substring(1, header.length() - 1);
            }
            else
            {
                return header;
            }
        }
        else
        {
            return header;
        }
    }

    public static SMTPMessage createMimeMessage(EmailMessage e, Session session)
    {
        BaseMailMessage b = new BaseMailMessage(session, e.getCharset(), e.getRootContentType());

        if (!Strings.isNullOrBlank(e.getMessageId()))
        {
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
        b.setEnvelopeFrom(MailUtility.nullSafeInternetAddressAsString(e.getEnvelopeFrom()));

        if (e.getSubject() != null)
        {
            b.setSubject(e.getSubject());
        }

        if (e.getType() == EmailMessageType.STANDARD)
        {

            if (e.getHtmlBody() != null && e.getTextBody() != null)
            {
                b.setHTMLTextAlt(e.getHtmlBody(), e.getTextBody());
            }
            else if (e.getTextBody() != null)
            {
                b.setText(e.getTextBody());
            }
            else if (e.getHtmlBody() != null)
            {
                b.setHTML(e.getHtmlBody());
            }

            b.addAttachments(e.getAttachments());
        }
        else if (e.getType() == EmailMessageType.INVITE_ICAL)
        {
            b.setHTMLNotRelated(e.getHtmlBody());
            b.addAttachments(e.getAttachments());
        }
        else
        {
            throw new SendFailedException("Unsupported Message Type: " + e.getType());
        }

        return b.getFinalizedMessage();
    }

    public static String nullSafeInternetAddressAsString(InternetAddress value)
    {
        return value != null ? value.toString() : null;
    }

    public static void send(EmailMessage e, Session session) throws SendFailedException
    {
        SMTPMessage msg = MailUtility.createMimeMessage(e, session);
        try
        {
            Transport.send(msg);
        }
        catch (MessagingException e1)
        {
            throw new SendFailedException("Send Failed", e1);
        }

        try
        {
            e.setMessageId(null);
            e.setLastMessageId(MailUtility.headerStripper(msg.getMessageID()));
        }
        catch (MessagingException e1)
        {
            throw new SendFailedException("Unable to read Message-ID from sent message");
        }
    }
}
