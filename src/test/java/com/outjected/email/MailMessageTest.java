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

package com.outjected.email;

import java.io.IOException;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.google.common.io.Resources;
import com.outjected.email.api.ContentDisposition;
import com.outjected.email.api.EmailMessage;
import com.outjected.email.api.InvalidAddressException;
import com.outjected.email.api.MessagePriority;
import com.outjected.email.api.SendFailedException;
import com.outjected.email.api.SessionConfig;
import com.outjected.email.impl.MailMessageImpl;
import com.outjected.email.impl.attachments.URLAttachment;
import com.outjected.email.impl.util.MailTestUtil;
import com.outjected.email.impl.util.MailUtility;
import com.outjected.email.impl.util.MessageConverter;
import com.outjected.email.util.TestMailConfigs;
import org.junit.Assert;
import org.junit.Test;
import org.subethamail.wiser.Wiser;

/**
 * @author Cody Lerum
 */
public class MailMessageTest {

    private final String fromName = "Seam Framework";
    private final String fromAddress = "seam@jboss.org";
    private final String replyToAddress = "no-reply@seam-mal.test";
    private final String toName = "Seamy Seamerson";
    private final String toAddress = "seamy.seamerson@seam-mail.test";

    private final String htmlBody = "<html><body><b>Hello</b> World!</body></html>";
    private final String textBody = "This is a Text Body!";

    private static final String ENVELOPE_FROM_ADDRESS = "ef@jboss.org";

    @Test
    public void testTextMailMessage() throws MessagingException, IOException {
        SessionConfig mailConfig = TestMailConfigs.standardConfig();

        String subject = "Text Message from Seam Mail - " + java.util.UUID.randomUUID();
        String messageId = "1234@seam.test.com";

        EmailMessage e;

        Wiser wiser = new Wiser(mailConfig.getServerPort());
        wiser.setHostname(mailConfig.getServerHost());
        try {
            wiser.start();

            e = new MailMessageImpl(mailConfig).from(MailTestUtil.getAddressHeader(fromName, fromAddress)).replyTo(replyToAddress).to(MailTestUtil.getAddressHeader(toName, toAddress)).subject(subject)
                    .bodyText(textBody).importance(MessagePriority.HIGH).messageId(messageId).envelopeFrom(ENVELOPE_FROM_ADDRESS).send();
        }
        finally {
            stop(wiser);
        }

        Assert.assertEquals("Didn't receive the expected amount of messages. Expected 1 got " + wiser.getMessages().size(), 1, wiser.getMessages().size());

        MimeMessage mess = wiser.getMessages().get(0).getMimeMessage();

        Assert.assertEquals(MailTestUtil.getAddressHeader(fromName, fromAddress), mess.getHeader("From", null));
        Assert.assertEquals(MailTestUtil.getAddressHeader(replyToAddress), mess.getHeader("Reply-To", null));
        Assert.assertEquals(MailTestUtil.getAddressHeader(toName, toAddress), mess.getHeader("To", null));
        Assert.assertEquals("Subject has been modified", subject, MimeUtility.unfold(mess.getHeader("Subject", null)));
        Assert.assertEquals(MessagePriority.HIGH.getPriority(), mess.getHeader("Priority", null));
        Assert.assertEquals(MessagePriority.HIGH.getX_priority(), mess.getHeader("X-Priority", null));
        Assert.assertEquals(MessagePriority.HIGH.getImportance(), mess.getHeader("Importance", null));
        Assert.assertTrue(mess.getHeader("Content-Type", null).startsWith("multipart/mixed"));
        Assert.assertEquals(messageId, MailUtility.headerStripper(mess.getHeader("Message-ID", null)));

        MimeMultipart mixed = (MimeMultipart) mess.getContent();
        BodyPart text = mixed.getBodyPart(0);

        Assert.assertTrue(mixed.getContentType().startsWith("multipart/mixed"));
        Assert.assertEquals(1, mixed.getCount());

        Assert.assertTrue("Incorrect Charset: " + e.getCharset(), text.getContentType().startsWith("text/plain; charset=" + e.getCharset()));
        Assert.assertEquals(textBody, MailTestUtil.getStringContent(text));
        EmailMessage convertedMessage = MessageConverter.convert(mess);
        Assert.assertEquals(convertedMessage.getSubject(), subject);
    }

    @Test
    public void testTextMailMessageSpecialCharacters() throws MessagingException, IOException {
        SessionConfig mailConfig = TestMailConfigs.standardConfig();

        String subject = "Sometimes subjects have special characters like ü - " + java.util.UUID.randomUUID();
        String specialTextBody = "This is a Text Body with a special character - ü";

        EmailMessage e;

        String messageId = "1234@seam.test.com";

        Wiser wiser = new Wiser(mailConfig.getServerPort());
        wiser.setHostname(mailConfig.getServerHost());
        try {
            wiser.start();

            e = new MailMessageImpl(mailConfig).from(MailTestUtil.getAddressHeader(fromName, fromAddress)).replyTo(replyToAddress).to(MailTestUtil.getAddressHeader(toName, toAddress)).subject(subject)
                    .bodyText(specialTextBody).importance(MessagePriority.HIGH).messageId(messageId).send();
        }
        finally {
            stop(wiser);
        }

        Assert.assertEquals("Didn't receive the expected amount of messages. Expected 1 got " + wiser.getMessages().size(), 1, wiser.getMessages().size());

        MimeMessage mess = wiser.getMessages().get(0).getMimeMessage();

        Assert.assertEquals("Subject has been modified", subject, MimeUtility.decodeText(MimeUtility.unfold(mess.getHeader("Subject", null))));

        MimeMultipart mixed = (MimeMultipart) mess.getContent();
        BodyPart text = mixed.getBodyPart(0);

        Assert.assertTrue(mixed.getContentType().startsWith("multipart/mixed"));
        Assert.assertEquals(1, mixed.getCount());

        Assert.assertTrue("Incorrect Charset: " + e.getCharset(), text.getContentType().startsWith("text/plain; charset=" + e.getCharset()));
        Assert.assertEquals(specialTextBody, MimeUtility.decodeText(MailTestUtil.getStringContent(text)));
        EmailMessage convertedMessage = MessageConverter.convert(mess);
        Assert.assertEquals(convertedMessage.getSubject(), subject);
    }

    @Test
    public void testHTMLMailMessage() throws MessagingException, IOException {
        SessionConfig mailConfig = TestMailConfigs.standardConfig();

        String subject = "HTML Message from Seam Mail - " + java.util.UUID.randomUUID();
        Person person = new Person(toName, toAddress);

        EmailMessage e;

        Wiser wiser = new Wiser(mailConfig.getServerPort());
        wiser.setHostname(mailConfig.getServerHost());
        String replyToName = "No Reply";
        try {
            wiser.start();
            e = new MailMessageImpl(mailConfig).from(MailTestUtil.getAddressHeader(fromName, fromAddress)).replyTo(MailTestUtil.getAddressHeader(replyToName, replyToAddress)).to(person).subject(
                    subject).bodyHtml(htmlBody).importance(MessagePriority.HIGH).addAttachment(new URLAttachment("https://design.jboss.org/seam/logo/final/seam_mail_85px.png", "seamLogo.png",
                            ContentDisposition.INLINE)).send();
        }
        finally {
            stop(wiser);
        }

        Assert.assertEquals("Didn't receive the expected amount of messages. Expected 1 got " + wiser.getMessages().size(), 1, wiser.getMessages().size());

        MimeMessage mess = wiser.getMessages().get(0).getMimeMessage();

        Assert.assertEquals(MailTestUtil.getAddressHeader(fromName, fromAddress), mess.getHeader("From", null));
        Assert.assertEquals(MailTestUtil.getAddressHeader(replyToName, replyToAddress), mess.getHeader("Reply-To", null));
        Assert.assertEquals(MailTestUtil.getAddressHeader(toName, toAddress), mess.getHeader("To", null));
        Assert.assertEquals("Subject has been modified", subject, MimeUtility.unfold(mess.getHeader("Subject", null)));
        Assert.assertEquals(MessagePriority.HIGH.getPriority(), mess.getHeader("Priority", null));
        Assert.assertEquals(MessagePriority.HIGH.getX_priority(), mess.getHeader("X-Priority", null));
        Assert.assertEquals(MessagePriority.HIGH.getImportance(), mess.getHeader("Importance", null));
        Assert.assertEquals(e.getMessageId(), MailUtility.headerStripper(mess.getHeader("Message-ID", null)));
        Assert.assertTrue(mess.getHeader("Content-Type", null).startsWith("multipart/mixed"));

        MimeMultipart mixed = (MimeMultipart) mess.getContent();
        MimeMultipart related = (MimeMultipart) mixed.getBodyPart(0).getContent();
        BodyPart html = related.getBodyPart(0);
        BodyPart attachment1 = related.getBodyPart(1);

        Assert.assertTrue(mixed.getContentType().startsWith("multipart/mixed"));
        Assert.assertEquals(1, mixed.getCount());

        Assert.assertTrue(related.getContentType().startsWith("multipart/related"));
        Assert.assertEquals(2, related.getCount());

        Assert.assertTrue(html.getContentType().startsWith("text/html"));
        Assert.assertEquals(htmlBody, MailTestUtil.getStringContent(html));

        Assert.assertTrue(attachment1.getContentType().startsWith("image/png;"));
        Assert.assertEquals("seamLogo.png", attachment1.getFileName());
        EmailMessage convertedMessage = MessageConverter.convert(mess);
        Assert.assertEquals(convertedMessage.getSubject(), subject);
    }

    @Test
    public void testHTMLTextAltMailMessage() throws MessagingException, IOException {
        SessionConfig mailConfig = TestMailConfigs.standardConfig();
        String subject = "HTML+Text Message from Seam Mail - " + java.util.UUID.randomUUID();
        Person person = new Person(toName, toAddress);
        Wiser wiser = new Wiser(mailConfig.getServerPort());
        wiser.setHostname(mailConfig.getServerHost());
        try {
            wiser.start();

            new MailMessageImpl(mailConfig).from(MailTestUtil.getAddressHeader(fromName, fromAddress)).to(person).subject(subject).bodyHtmlTextAlt(htmlBody, textBody).importance(MessagePriority.LOW)
                    .deliveryReceipt(fromAddress).readReceipt(fromAddress).addAttachment("template.text.velocity", "text/plain", ContentDisposition.ATTACHMENT, Resources.asByteSource(Resources
                            .getResource("template.text.velocity")).read()).addAttachment(new URLAttachment("https://design.jboss.org/seam/logo/final/seam_mail_85px.png", "seamLogo.png",
                                    ContentDisposition.INLINE)).send();
        }
        finally {
            stop(wiser);
        }

        Assert.assertEquals("Didn't receive the expected amount of messages. Expected 1 got " + wiser.getMessages().size(), 1, wiser.getMessages().size());

        MimeMessage mess = wiser.getMessages().get(0).getMimeMessage();

        Assert.assertEquals(MailTestUtil.getAddressHeader(fromName, fromAddress), mess.getHeader("From", null));
        Assert.assertEquals(MailTestUtil.getAddressHeader(toName, toAddress), mess.getHeader("To", null));
        Assert.assertEquals("Subject has been modified", subject, MimeUtility.unfold(mess.getHeader("Subject", null)));
        Assert.assertEquals(MessagePriority.LOW.getPriority(), mess.getHeader("Priority", null));
        Assert.assertEquals(MessagePriority.LOW.getX_priority(), mess.getHeader("X-Priority", null));
        Assert.assertEquals(MessagePriority.LOW.getImportance(), mess.getHeader("Importance", null));
        Assert.assertTrue(mess.getHeader("Content-Type", null).startsWith("multipart/mixed"));

        MimeMultipart mixed = (MimeMultipart) mess.getContent();
        MimeMultipart related = (MimeMultipart) mixed.getBodyPart(0).getContent();
        MimeMultipart alternative = (MimeMultipart) related.getBodyPart(0).getContent();
        BodyPart attachment = mixed.getBodyPart(1);
        BodyPart inlineAttachment = related.getBodyPart(1);

        BodyPart textAlt = alternative.getBodyPart(0);
        BodyPart html = alternative.getBodyPart(1);

        Assert.assertTrue(mixed.getContentType().startsWith("multipart/mixed"));
        Assert.assertEquals(2, mixed.getCount());

        Assert.assertTrue(related.getContentType().startsWith("multipart/related"));
        Assert.assertEquals(2, related.getCount());

        Assert.assertTrue(html.getContentType().startsWith("text/html"));
        Assert.assertEquals(htmlBody, MailTestUtil.getStringContent(html));

        Assert.assertTrue(textAlt.getContentType().startsWith("text/plain"));
        Assert.assertEquals(textBody, MailTestUtil.getStringContent(textAlt));

        Assert.assertTrue(attachment.getContentType().startsWith("text/plain"));
        Assert.assertEquals("template.text.velocity", attachment.getFileName());

        Assert.assertTrue(inlineAttachment.getContentType().startsWith("image/png;"));
        Assert.assertEquals("seamLogo.png", inlineAttachment.getFileName());
        EmailMessage convertedMessage = MessageConverter.convert(mess);
        Assert.assertEquals(convertedMessage.getSubject(), subject);
    }

    @Test
    public void testHTMLAutoTextAltMailMessage() throws MessagingException, IOException {
        SessionConfig mailConfig = TestMailConfigs.standardConfig();
        String subject = "HTML+Text Message from Seam Mail - " + java.util.UUID.randomUUID();
        Person person = new Person(toName, toAddress);
        Wiser wiser = new Wiser(mailConfig.getServerPort());
        wiser.setHostname(mailConfig.getServerHost());
        try {
            wiser.start();

            new MailMessageImpl(mailConfig).from(MailTestUtil.getAddressHeader(fromName, fromAddress)).to(person).subject(subject).bodyHtml(htmlBody).createTextAlternative(true).importance(
                    MessagePriority.LOW).deliveryReceipt(fromAddress).readReceipt(fromAddress).addAttachment("template.text.velocity", "text/plain", ContentDisposition.ATTACHMENT, Resources
                            .asByteSource(Resources.getResource("template.text.velocity")).read()).addAttachment(new URLAttachment("https://design.jboss.org/seam/logo/final/seam_mail_85px.png",
                                    "seamLogo.png", ContentDisposition.INLINE)).send();
        }
        finally {
            stop(wiser);
        }

        Assert.assertEquals("Didn't receive the expected amount of messages. Expected 1 got " + wiser.getMessages().size(), 1, wiser.getMessages().size());

        MimeMessage mess = wiser.getMessages().get(0).getMimeMessage();

        Assert.assertEquals(MailTestUtil.getAddressHeader(fromName, fromAddress), mess.getHeader("From", null));
        Assert.assertEquals(MailTestUtil.getAddressHeader(toName, toAddress), mess.getHeader("To", null));
        Assert.assertEquals("Subject has been modified", subject, MimeUtility.unfold(mess.getHeader("Subject", null)));
        Assert.assertEquals(MessagePriority.LOW.getPriority(), mess.getHeader("Priority", null));
        Assert.assertEquals(MessagePriority.LOW.getX_priority(), mess.getHeader("X-Priority", null));
        Assert.assertEquals(MessagePriority.LOW.getImportance(), mess.getHeader("Importance", null));
        Assert.assertTrue(mess.getHeader("Content-Type", null).startsWith("multipart/mixed"));

        MimeMultipart mixed = (MimeMultipart) mess.getContent();
        MimeMultipart related = (MimeMultipart) mixed.getBodyPart(0).getContent();
        MimeMultipart alternative = (MimeMultipart) related.getBodyPart(0).getContent();
        BodyPart attachment = mixed.getBodyPart(1);
        BodyPart inlineAttachment = related.getBodyPart(1);

        BodyPart textAlt = alternative.getBodyPart(0);
        BodyPart html = alternative.getBodyPart(1);

        Assert.assertTrue(mixed.getContentType().startsWith("multipart/mixed"));
        Assert.assertEquals(2, mixed.getCount());

        Assert.assertTrue(related.getContentType().startsWith("multipart/related"));
        Assert.assertEquals(2, related.getCount());

        Assert.assertTrue(html.getContentType().startsWith("text/html"));
        Assert.assertEquals(htmlBody, MailTestUtil.getStringContent(html));

        Assert.assertTrue(textAlt.getContentType().startsWith("text/plain"));
        Assert.assertEquals("Hello World!", MailTestUtil.getStringContent(textAlt));

        Assert.assertTrue(attachment.getContentType().startsWith("text/plain"));
        Assert.assertEquals("template.text.velocity", attachment.getFileName());

        Assert.assertTrue(inlineAttachment.getContentType().startsWith("image/png;"));
        Assert.assertEquals("seamLogo.png", inlineAttachment.getFileName());
        EmailMessage convertedMessage = MessageConverter.convert(mess);
        Assert.assertEquals(convertedMessage.getSubject(), subject);
    }

    @Test
    public void testTextMailMessageLongFields() throws MessagingException, IOException {
        SessionConfig mailConfig = TestMailConfigs.standardConfig();

        String subject = "Sometimes it is important to have a really long subject even if nobody is going to read it - " + java.util.UUID.randomUUID();

        String longFromName = "FromSometimesPeopleHaveNamesWhichAreALotLongerThanYouEverExpectedSomeoneToHaveSoItisGoodToTestUpTo100CharactersOrSo YouKnow?";
        String longFromAddress = "sometimesPeopleHaveNamesWhichAreALotLongerThanYouEverExpectedSomeoneToHaveSoItisGoodToTestUpTo100CharactersOrSo@jboss.org";
        String longToName = "ToSometimesPeopleHaveNamesWhichAreALotLongerThanYouEverExpectedSomeoneToHaveSoItisGoodToTestUpTo100CharactersOrSo YouKnow?";
        String longToAddress = "toSometimesPeopleHaveNamesWhichAreALotLongerThanYouEverExpectedSomeoneToHaveSoItisGoodToTestUpTo100CharactersOrSo.seamerson@seam-mail.test";
        String longCcName = "CCSometimesPeopleHaveNamesWhichAreALotLongerThanYouEverExpectedSomeoneToHaveSoItisGoodToTestUpTo100CharactersOrSo YouKnow? Hatty";
        String longCcAddress = "cCSometimesPeopleHaveNamesWhichAreALotLongerThanYouEverExpectedSomeoneToHaveSoItisGoodToTestUpTo100CharactersOrSo.hatty@jboss.org";

        EmailMessage e;

        Wiser wiser = new Wiser(mailConfig.getServerPort());
        wiser.setHostname(mailConfig.getServerHost());
        try {
            wiser.start();

            e = new MailMessageImpl(mailConfig).from(MailTestUtil.getAddressHeader(longFromName, longFromAddress)).to(MailTestUtil.getAddressHeader(longToName, longToAddress)).cc(MailTestUtil
                    .getAddressHeader(longCcName, longCcAddress)).subject(subject).bodyText(textBody).importance(MessagePriority.HIGH).send();
        }
        finally {
            stop(wiser);
        }

        Assert.assertEquals("Didn't receive the expected amount of messages. Expected 2 got " + wiser.getMessages().size(), 2, wiser.getMessages().size());

        MimeMessage mess = wiser.getMessages().get(0).getMimeMessage();

        Assert.assertEquals(MailTestUtil.getAddressHeader(longFromName, longFromAddress), mess.getHeader("From", null));
        Assert.assertEquals(MailTestUtil.getAddressHeader(longToName, longToAddress), mess.getHeader("To", null));
        Assert.assertEquals(MailTestUtil.getAddressHeader(longCcName, longCcAddress), mess.getHeader("CC", null));
        Assert.assertEquals("Subject has been modified", subject, MimeUtility.unfold(mess.getHeader("Subject", null)));
        Assert.assertEquals(MessagePriority.HIGH.getPriority(), mess.getHeader("Priority", null));
        Assert.assertEquals(MessagePriority.HIGH.getX_priority(), mess.getHeader("X-Priority", null));
        Assert.assertEquals(MessagePriority.HIGH.getImportance(), mess.getHeader("Importance", null));
        Assert.assertTrue(mess.getHeader("Content-Type", null).startsWith("multipart/mixed"));

        MimeMultipart mixed = (MimeMultipart) mess.getContent();
        BodyPart text = mixed.getBodyPart(0);

        Assert.assertTrue(mixed.getContentType().startsWith("multipart/mixed"));
        Assert.assertEquals(1, mixed.getCount());

        Assert.assertTrue("Incorrect Charset: " + e.getCharset(), text.getContentType().startsWith("text/plain; charset=" + e.getCharset()));
        Assert.assertEquals(textBody, MailTestUtil.getStringContent(text));
        EmailMessage convertedMessage = MessageConverter.convert(mess);
        Assert.assertEquals(convertedMessage.getSubject(), subject);
    }

    @Test(expected = SendFailedException.class)
    public void testTextMailMessageSendFailed() {
        SessionConfig mailConfig = TestMailConfigs.standardConfig();

        String subject = "Text Message from Seam Mail - " + java.util.UUID.randomUUID();
        String messageId = "1234@seam.test.com";

        // Port is one off so this should fail
        Wiser wiser = new Wiser(mailConfig.getServerPort() + 1);
        wiser.setHostname(mailConfig.getServerHost());

        try {
            wiser.start();

            new MailMessageImpl(mailConfig).from(MailTestUtil.getAddressHeader(fromName, fromAddress)).replyTo(replyToAddress).to(toAddress).subject(subject).bodyText(textBody).importance(
                    MessagePriority.HIGH).messageId(messageId).send();
        }
        finally {
            stop(wiser);
        }
    }

    @Test(expected = InvalidAddressException.class)
    public void testTextMailMessageInvalidAddress() throws SendFailedException {
        SessionConfig mailConfig = TestMailConfigs.standardConfig();

        String subject = "Text Message from Seam Mail - " + java.util.UUID.randomUUID();

        String messageId = "1234@seam.test.com";

        // Port is one off so this should fail
        Wiser wiser = new Wiser(mailConfig.getServerPort() + 1);
        wiser.setHostname(mailConfig.getServerHost());

        try {
            wiser.start();

            new MailMessageImpl(mailConfig).from("seam seamerson@test.com").replyTo(replyToAddress).to(toAddress, toName).subject(subject).bodyText(textBody).importance(MessagePriority.HIGH)
                    .messageId(messageId).send();
        }
        finally {
            stop(wiser);
        }
    }

    @Test
    public void testTextMailMessageUsingPerson() throws MessagingException, IOException {
        SessionConfig mailConfig = TestMailConfigs.standardConfig();

        String subject = "Text Message from Seam Mail - " + java.util.UUID.randomUUID();
        Person person = new Person(toName, toAddress);
        String messageId = "1234@seam.test.com";

        EmailMessage e;

        Wiser wiser = new Wiser(mailConfig.getServerPort());
        wiser.setHostname(mailConfig.getServerHost());
        try {
            wiser.start();

            e = new MailMessageImpl(mailConfig).from(MailTestUtil.getAddressHeader(fromName, fromAddress)).replyTo(replyToAddress).to(person).subject(subject).bodyText(textBody).importance(
                    MessagePriority.HIGH).messageId(messageId).send();
        }
        finally {
            stop(wiser);
        }

        Assert.assertEquals("Didn't receive the expected amount of messages. Expected 1 got " + wiser.getMessages().size(), 1, wiser.getMessages().size());

        MimeMessage mess = wiser.getMessages().get(0).getMimeMessage();

        Assert.assertEquals(MailTestUtil.getAddressHeader(fromName, fromAddress), mess.getHeader("From", null));
        Assert.assertEquals(MailTestUtil.getAddressHeader(replyToAddress), mess.getHeader("Reply-To", null));
        Assert.assertEquals(MailTestUtil.getAddressHeader(toName, toAddress), mess.getHeader("To", null));
        Assert.assertEquals("Subject has been modified", subject, MimeUtility.unfold(mess.getHeader("Subject", null)));
        Assert.assertEquals(MessagePriority.HIGH.getPriority(), mess.getHeader("Priority", null));
        Assert.assertEquals(MessagePriority.HIGH.getX_priority(), mess.getHeader("X-Priority", null));
        Assert.assertEquals(MessagePriority.HIGH.getImportance(), mess.getHeader("Importance", null));
        Assert.assertTrue(mess.getHeader("Content-Type", null).startsWith("multipart/mixed"));
        Assert.assertEquals(messageId, MailUtility.headerStripper(mess.getHeader("Message-ID", null)));

        MimeMultipart mixed = (MimeMultipart) mess.getContent();
        BodyPart text = mixed.getBodyPart(0);

        Assert.assertTrue(mixed.getContentType().startsWith("multipart/mixed"));
        Assert.assertEquals(1, mixed.getCount());

        Assert.assertTrue("Incorrect Charset: " + e.getCharset(), text.getContentType().startsWith("text/plain; charset=" + e.getCharset()));
        Assert.assertEquals(textBody, MailTestUtil.getStringContent(text));
        EmailMessage convertedMessage = MessageConverter.convert(mess);
        Assert.assertEquals(convertedMessage.getSubject(), subject);
    }

    @Test
    public void testTextMailMessageUsingDefaultSession() throws MessagingException, IOException {
        SessionConfig mailConfig = TestMailConfigs.standardConfig();
        Person person = new Person(toName, toAddress);
        String subject = "Text Message from Seam Mail - " + java.util.UUID.randomUUID();

        String messageId = "1234@seam.test.com";

        EmailMessage e;

        Wiser wiser = new Wiser(mailConfig.getServerPort());
        wiser.setHostname(mailConfig.getServerHost());
        try {
            wiser.start();
            e = new MailMessageImpl(mailConfig).from(MailTestUtil.getAddressHeader(fromName, fromAddress)).replyTo(replyToAddress).to(person).subject(subject).bodyText(textBody).importance(
                    MessagePriority.HIGH).messageId(messageId).send();
        }
        finally {
            stop(wiser);
        }

        Assert.assertEquals("Didn't receive the expected amount of messages. Expected 1 got " + wiser.getMessages().size(), 1, wiser.getMessages().size());

        MimeMessage mess = wiser.getMessages().get(0).getMimeMessage();

        Assert.assertEquals(MailTestUtil.getAddressHeader(fromName, fromAddress), mess.getHeader("From", null));
        Assert.assertEquals(MailTestUtil.getAddressHeader(replyToAddress), mess.getHeader("Reply-To", null));
        Assert.assertEquals(MailTestUtil.getAddressHeader(toName, toAddress), mess.getHeader("To", null));
        Assert.assertEquals("Subject has been modified", subject, MimeUtility.unfold(mess.getHeader("Subject", null)));
        Assert.assertEquals(MessagePriority.HIGH.getPriority(), mess.getHeader("Priority", null));
        Assert.assertEquals(MessagePriority.HIGH.getX_priority(), mess.getHeader("X-Priority", null));
        Assert.assertEquals(MessagePriority.HIGH.getImportance(), mess.getHeader("Importance", null));
        Assert.assertTrue(mess.getHeader("Content-Type", null).startsWith("multipart/mixed"));
        Assert.assertEquals(messageId, MailUtility.headerStripper(mess.getHeader("Message-ID", null)));

        MimeMultipart mixed = (MimeMultipart) mess.getContent();
        BodyPart text = mixed.getBodyPart(0);

        Assert.assertTrue(mixed.getContentType().startsWith("multipart/mixed"));
        Assert.assertEquals(1, mixed.getCount());

        Assert.assertTrue("Incorrect Charset: " + e.getCharset(), text.getContentType().startsWith("text/plain; charset=" + e.getCharset()));
        Assert.assertEquals(textBody, MailTestUtil.getStringContent(text));
        EmailMessage convertedMessage = MessageConverter.convert(mess);
        Assert.assertEquals(convertedMessage.getSubject(), subject);
    }

    @Test
    public void testTextMailWithCC() throws MessagingException {
        SessionConfig mailConfig = TestMailConfigs.standardConfig();
        String subject = "Text Message from Seam Mail - " + java.util.UUID.randomUUID();
        String messageId = "1234@seam.test.com";

        Wiser wiser = new Wiser(mailConfig.getServerPort());
        wiser.setHostname(mailConfig.getServerHost());
        String ccName = "Red Hatty";
        String ccAddress = "red.hatty@jboss.org";
        try {
            wiser.start();
            new MailMessageImpl(mailConfig).from(MailTestUtil.getAddressHeader(fromName, fromAddress)).replyTo(replyToAddress).to(MailTestUtil.getAddressHeader(toName, toAddress)).cc(MailTestUtil
                    .getAddressHeader(ccName, ccAddress)).subject(subject).bodyText(textBody).importance(MessagePriority.HIGH).messageId(messageId).envelopeFrom(ENVELOPE_FROM_ADDRESS).send();
        }
        finally {
            stop(wiser);
        }

        Assert.assertEquals("Didn't receive the expected amount of messages. Expected 2 got " + wiser.getMessages().size(), 2, wiser.getMessages().size());

        MimeMessage mess = wiser.getMessages().get(0).getMimeMessage();

        Assert.assertEquals(MailTestUtil.getAddressHeader(fromName, fromAddress), mess.getHeader("From", null));
        Assert.assertEquals(MailTestUtil.getAddressHeader(replyToAddress), mess.getHeader("Reply-To", null));
        Assert.assertEquals(MailTestUtil.getAddressHeader(toName, toAddress), mess.getHeader("To", null));
        Assert.assertEquals(MailTestUtil.getAddressHeader(ccName, ccAddress), mess.getHeader("CC", null));
        Assert.assertEquals("Subject has been modified", subject, MimeUtility.unfold(mess.getHeader("Subject", null)));
        Assert.assertEquals(MessagePriority.HIGH.getPriority(), mess.getHeader("Priority", null));
        Assert.assertEquals(MessagePriority.HIGH.getX_priority(), mess.getHeader("X-Priority", null));
        Assert.assertEquals(MessagePriority.HIGH.getImportance(), mess.getHeader("Importance", null));
        Assert.assertTrue(mess.getHeader("Content-Type", null).startsWith("multipart/mixed"));
        Assert.assertEquals(messageId, MailUtility.headerStripper(mess.getHeader("Message-ID", null)));

        mess = wiser.getMessages().get(1).getMimeMessage();
        Assert.assertEquals(MailTestUtil.getAddressHeader(fromName, fromAddress), mess.getHeader("From", null));
        Assert.assertEquals(MailTestUtil.getAddressHeader(replyToAddress), mess.getHeader("Reply-To", null));
        Assert.assertEquals(MailTestUtil.getAddressHeader(toName, toAddress), mess.getHeader("To", null));
        Assert.assertEquals(MailTestUtil.getAddressHeader(ccName, ccAddress), mess.getHeader("CC", null));
        Assert.assertEquals("Subject has been modified", subject, MimeUtility.unfold(mess.getHeader("Subject", null)));
        Assert.assertEquals(MessagePriority.HIGH.getPriority(), mess.getHeader("Priority", null));
        Assert.assertEquals(MessagePriority.HIGH.getX_priority(), mess.getHeader("X-Priority", null));
        Assert.assertEquals(MessagePriority.HIGH.getImportance(), mess.getHeader("Importance", null));
        Assert.assertTrue(mess.getHeader("Content-Type", null).startsWith("multipart/mixed"));
        Assert.assertEquals(messageId, MailUtility.headerStripper(mess.getHeader("Message-ID", null)));
    }

    /**
     * Wiser takes a fraction of a second to shutdown, so let it finish.
     */
    protected void stop(Wiser wiser) {
        wiser.stop();
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
