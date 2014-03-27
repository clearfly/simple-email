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

package com.outjected.email.impl.util;

import java.io.IOException;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

import com.outjected.email.api.ContentDisposition;
import com.outjected.email.api.EmailMessage;
import com.outjected.email.api.MailException;
import com.outjected.email.impl.attachments.InputStreamAttachment;

/**
 * 
 * @author Cody Lerum
 * 
 */
public class MessageConverter
{

    private EmailMessage emailMessage;

    public static EmailMessage convert(Message m) throws MailException
    {
        MessageConverter mc = new MessageConverter();
        return mc.convertMessage(m);
    }

    public EmailMessage convertMessage(Message m) throws MailException
    {
        emailMessage = new EmailMessage();

        try
        {
            emailMessage.setFromAddresses(MailUtility.getInternetAddressses(m.getFrom()));
            emailMessage.setToAddresses(MailUtility.getInternetAddressses(m.getRecipients(RecipientType.TO)));
            emailMessage.setCcAddresses(MailUtility.getInternetAddressses(m.getRecipients(RecipientType.CC)));
            emailMessage.setBccAddresses(MailUtility.getInternetAddressses(m.getRecipients(RecipientType.BCC)));
            emailMessage.setSubject(m.getSubject());
            emailMessage.setMessageId(m.getHeader("Message-ID")[0]);
            emailMessage.addHeaders(MailUtility.getHeaders(m.getAllHeaders()));

            if (m.getContentType().toLowerCase().contains("multipart/"))
            {
                addMultiPart((MimeMultipart) m.getContent());
            }
            else if (m.isMimeType("text/plain"))
            {
                emailMessage.setTextBody((String) m.getContent());
            }
        }
        catch (IOException e)
        {
            throw new MailException(e);
        }
        catch (MessagingException e)
        {
            throw new MailException(e);
        }

        return emailMessage;
    }

    private void addMultiPart(MimeMultipart mp) throws MessagingException, IOException
    {
        for (int i = 0; i < mp.getCount(); i++)
        {
            BodyPart bp = mp.getBodyPart(i);
            if (bp.getContentType().toLowerCase().contains("multipart/"))
            {
                addMultiPart((MimeMultipart) bp.getContent());
            }
            else
            {
                addPart(mp.getBodyPart(i));
            }
        }
    }

    private void addPart(BodyPart bp) throws MessagingException, IOException
    {

        if (bp.getContentType().toLowerCase().contains("multipart/"))
        {
            addMultiPart((MimeMultipart) bp.getContent());
        }
        else if (bp.getContentType().toLowerCase().contains("text/plain"))
        {
            emailMessage.setTextBody((String) bp.getContent());
        }
        else if (bp.getContentType().toLowerCase().contains("text/html"))
        {
            emailMessage.setHtmlBody((String) bp.getContent());
        }
        else if (bp.getContentType().toLowerCase().contains("application/octet-stream"))
        {
            emailMessage.addAttachment(new InputStreamAttachment(bp.getFileName(), bp.getContentType(),
                    ContentDisposition.mapValue(bp.getDisposition()), bp.getInputStream()));
        }
    }
}
