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

package co.cfly.email;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import co.cfly.email.api.ContentDisposition;
import co.cfly.email.api.EmailMessage;
import co.cfly.email.api.MessagePriority;
import co.cfly.email.api.SendFailedException;
import co.cfly.email.api.SessionConfig;
import co.cfly.email.impl.MailMessageImpl;
import co.cfly.email.impl.attachments.URLAttachment;
import co.cfly.email.impl.templating.velocity.VelocityTemplate;
import co.cfly.email.impl.util.EmailAttachmentUtil;
import co.cfly.email.impl.util.MailTestUtil;
import co.cfly.email.impl.util.MessageConverter;
import co.cfly.email.util.SMTPAuthenticator;
import co.cfly.email.util.TestMailConfigs;
import com.google.common.io.Resources;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeUtility;
import org.junit.Assert;
import org.junit.Test;
import org.subethamail.smtp.auth.EasyAuthenticationHandlerFactory;
import org.subethamail.wiser.Wiser;

public class VelocityMailMessageTest {

    String fromName = "Seam Framework";
    String fromAddress = "seam@jboss.org";
    String replyToName = "No Reply";
    String replyToAddress = "no-reply@seam-mal.test";
    String toName = "Seamy Seamerson";
    String toAddress = "seamy.seamerson@seam-mail.test";

    @Test
    public void testVelocityTextMailMessage() throws IOException, jakarta.mail.MessagingException {
        SessionConfig mailConfig = TestMailConfigs.standardConfig();
        Person person = new Person(toName, toAddress);
        EmailMessage e;

        String uuid = java.util.UUID.randomUUID().toString();
        String subjectTemplate = "Text Message from $version Mail - " + uuid;
        String version = "Seam 3";
        String subject = "Text Message from " + version + " Mail - " + uuid;

        Wiser wiser = new Wiser(mailConfig.getServerPort());
        wiser.setHostname(mailConfig.getServerHost());
        try {
            wiser.start();

            e = new MailMessageImpl(mailConfig).from(MailTestUtil.getAddressHeader(fromName, fromAddress)).replyTo(replyToAddress).to(MailTestUtil.getAddressHeader(toName, toAddress))
                    .subject(new VelocityTemplate(subjectTemplate))
                    .bodyText(new VelocityTemplate(Resources.asCharSource(Resources.getResource("template.text.velocity"), StandardCharsets.UTF_8).read())).put("version", version)
                    .put("person", person).importance(MessagePriority.HIGH).send();
        }
        finally {
            stop(wiser);
        }

        Assert.assertEquals("Didn't receive the expected amount of messages. Expected 1 got " + wiser.getMessages().size(), 1, wiser.getMessages().size());

        MimeMessage mess = MailUtilityTest.fromWiser(wiser.getMessages().get(0));

        Assert.assertEquals(MailTestUtil.getAddressHeader(fromName, fromAddress), mess.getHeader("From", null));
        Assert.assertEquals(MailTestUtil.getAddressHeader(replyToAddress), mess.getHeader("Reply-To", null));
        Assert.assertEquals(MailTestUtil.getAddressHeader(toName, toAddress), mess.getHeader("To", null));
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
        Assert.assertEquals(expectedTextBody(person.getName(), version), MailTestUtil.getStringContent(text));
        EmailMessage convertedMessage = MessageConverter.convert(mess);
        Assert.assertEquals(convertedMessage.getSubject(), subject);
    }

    @Test
    public void testTextMailMessageSpecialCharacters() throws MessagingException, IOException {
        SessionConfig mailConfig = TestMailConfigs.standardConfig();
        EmailMessage e;

        String uuid = java.util.UUID.randomUUID().toString();
        String subjectTemplate = "Special Char ü from $version Mail - " + uuid;
        String version = "Seam 3";
        String subject = "Special Char ü from " + version + " Mail - " + uuid;
        String specialTextBody = "This is a Text Body with a special character - ü - $version";
        String mergedSpecialTextBody = "This is a Text Body with a special character - ü - " + version;

        String messageId = "1234@seam.test.com";

        Wiser wiser = new Wiser(mailConfig.getServerPort());
        wiser.setHostname(mailConfig.getServerHost());
        try {
            wiser.start();

            e = new MailMessageImpl(mailConfig).from(MailTestUtil.getAddressHeader(fromName, fromAddress)).replyTo(replyToAddress).to(MailTestUtil.getAddressHeader(toName, toAddress))
                    .subject(new VelocityTemplate(subjectTemplate)).bodyText(new VelocityTemplate(specialTextBody)).importance(MessagePriority.HIGH).messageId(messageId).put("version", version)
                    .send();
        }
        finally {
            stop(wiser);
        }

        Assert.assertEquals("Didn't receive the expected amount of messages. Expected 1 got " + wiser.getMessages().size(), 1, wiser.getMessages().size());

        MimeMessage mess = MailUtilityTest.fromWiser(wiser.getMessages().get(0));

        Assert.assertEquals("Subject has been modified", subject, MimeUtility.decodeText(MimeUtility.unfold(mess.getHeader("Subject", null))));

        MimeMultipart mixed = (MimeMultipart) mess.getContent();
        BodyPart text = mixed.getBodyPart(0);

        Assert.assertTrue(mixed.getContentType().startsWith("multipart/mixed"));
        Assert.assertEquals(1, mixed.getCount());

        Assert.assertTrue("Incorrect Charset: " + e.getCharset(), text.getContentType().startsWith("text/plain; charset=" + e.getCharset()));
        Assert.assertEquals(mergedSpecialTextBody, MimeUtility.decodeText(MailTestUtil.getStringContent(text)));
        EmailMessage convertedMessage = MessageConverter.convert(mess);
        Assert.assertEquals(convertedMessage.getSubject(), subject);
    }

    @Test
    public void testVelocityHTMLMailMessage() throws MessagingException, IOException {
        SessionConfig mailConfig = TestMailConfigs.standardConfig();
        Person person = new Person(toName, toAddress);
        String subject = "HTML Message from Seam Mail - " + java.util.UUID.randomUUID();
        String version = "Seam 3";
        EmailMessage emailMessage;

        Wiser wiser = new Wiser(mailConfig.getServerPort());
        wiser.setHostname(mailConfig.getServerHost());
        try {
            wiser.start();

            person.setName(toName);
            person.setEmail(toAddress);

            emailMessage = new MailMessageImpl(mailConfig).from(MailTestUtil.getAddressHeader(fromName, fromAddress)).replyTo(MailTestUtil.getAddressHeader(replyToName, replyToAddress)).to(person)
                    .subject(subject).bodyHtml(new VelocityTemplate(Resources.asCharSource(Resources.getResource("template.html.velocity"), StandardCharsets.UTF_8).read())).put("version", version)
                    .put("person", person).importance(MessagePriority.HIGH)
                    .addAttachment(new URLAttachment("https://design.jboss.org/seam/logo/final/seam_mail_85px.png", "seamLogo.png", ContentDisposition.INLINE)).send();
        }
        finally {
            stop(wiser);
        }

        Assert.assertEquals("Didn't receive the expected amount of messages. Expected 1 got " + wiser.getMessages().size(), 1, wiser.getMessages().size());

        MimeMessage mess = MailUtilityTest.fromWiser(wiser.getMessages().get(0));

        Assert.assertEquals(MailTestUtil.getAddressHeader(fromName, fromAddress), mess.getHeader("From", null));
        Assert.assertEquals(MailTestUtil.getAddressHeader(replyToName, replyToAddress), mess.getHeader("Reply-To", null));
        Assert.assertEquals(MailTestUtil.getAddressHeader(toName, toAddress), mess.getHeader("To", null));
        Assert.assertEquals("Subject has been modified", subject, MimeUtility.unfold(mess.getHeader("Subject", null)));
        Assert.assertEquals(MessagePriority.HIGH.getPriority(), mess.getHeader("Priority", null));
        Assert.assertEquals(MessagePriority.HIGH.getX_priority(), mess.getHeader("X-Priority", null));
        Assert.assertEquals(MessagePriority.HIGH.getImportance(), mess.getHeader("Importance", null));
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
        Assert.assertEquals(expectedHtmlBody(emailMessage, person.getName(), person.getEmail(), version), MailTestUtil.getStringContent(html));

        Assert.assertTrue(attachment1.getContentType().startsWith("image/png;"));
        Assert.assertEquals("seamLogo.png", attachment1.getFileName());
        EmailMessage convertedMessage = MessageConverter.convert(mess);
        Assert.assertEquals(convertedMessage.getSubject(), subject);
    }

    @Test
    public void testVelocityHTMLTextAltMailMessage() throws MessagingException, IOException {
        SessionConfig mailConfig = TestMailConfigs.standardConfig();
        Person person = new Person(toName, toAddress);
        String subject = "HTML+Text Message from Seam Mail - " + java.util.UUID.randomUUID();
        String version = "Seam 3";
        EmailMessage emailMessage;

        Wiser wiser = new Wiser(mailConfig.getServerPort());
        wiser.setHostname(mailConfig.getServerHost());
        try {
            wiser.start();

            emailMessage =
                    new MailMessageImpl(mailConfig).from(MailTestUtil.getAddressHeader(fromName, fromAddress)).to(MailTestUtil.getAddressHeader(person.getName(), person.getEmail())).subject(subject)
                            .put("version", version).put("person", person)
                            .bodyHtmlTextAlt(new VelocityTemplate(Resources.asCharSource(Resources.getResource("template.html.velocity"), StandardCharsets.UTF_8).read()),
                                    new VelocityTemplate(Resources.asCharSource(Resources.getResource("template.text.velocity"), StandardCharsets.UTF_8).read())).importance(MessagePriority.LOW)
                            .deliveryReceipt(fromAddress).readReceipt(fromAddress)
                            .addAttachment("template.html.velocity", "text/html", ContentDisposition.ATTACHMENT, Resources.asByteSource(Resources.getResource("template.html.velocity")).read())
                            .addAttachment(new URLAttachment("https://design.jboss.org/seam/logo/final/seam_mail_85px.png", "seamLogo.png", ContentDisposition.INLINE)).send();
        }
        finally {
            stop(wiser);
        }

        Assert.assertEquals("Didn't receive the expected amount of messages. Expected 1 got " + wiser.getMessages().size(), 1, wiser.getMessages().size());

        MimeMessage mess = MailUtilityTest.fromWiser(wiser.getMessages().get(0));

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
        Assert.assertEquals(expectedHtmlBody(emailMessage, person.getName(), person.getEmail(), version), MailTestUtil.getStringContent(html));

        Assert.assertTrue(textAlt.getContentType().startsWith("text/plain"));
        Assert.assertEquals(expectedTextBody(person.getName(), version), MailTestUtil.getStringContent(textAlt));

        Assert.assertTrue(attachment.getContentType().startsWith("text/html"));
        Assert.assertEquals("template.html.velocity", attachment.getFileName());

        Assert.assertTrue(inlineAttachment.getContentType().startsWith("image/png;"));
        Assert.assertEquals("seamLogo.png", inlineAttachment.getFileName());
        EmailMessage convertedMessage = MessageConverter.convert(mess);
        Assert.assertEquals(convertedMessage.getSubject(), subject);
    }

    @Test
    public void testSMTPSessionAuthentication() throws MessagingException, IOException {
        SessionConfig mailConfig = TestMailConfigs.standardConfig();
        Person person = new Person(toName, toAddress);
        String subject = "HTML+Text Message from Seam Mail - " + java.util.UUID.randomUUID();

        Wiser wiser = new Wiser(mailConfig.getServerPort());
        wiser.setHostname(mailConfig.getServerHost());
        wiser.getServer().setAuthenticationHandlerFactory(new EasyAuthenticationHandlerFactory(new SMTPAuthenticator("test", "test12!")));
        try {
            wiser.start();

            new MailMessageImpl(mailConfig).from(fromAddress).to(person.getEmail()).subject(subject).put("version", "Seam 3")
                    .bodyHtmlTextAlt(new VelocityTemplate(Resources.asCharSource(Resources.getResource("template.html.velocity"), StandardCharsets.UTF_8).read()),
                            new VelocityTemplate(Resources.asCharSource(Resources.getResource("template.text.velocity"), StandardCharsets.UTF_8).read())).importance(MessagePriority.LOW)
                    .deliveryReceipt(fromAddress).readReceipt(fromAddress)
                    .addAttachment("template.html.velocity", "text/html", ContentDisposition.ATTACHMENT, Resources.asByteSource(Resources.getResource("template.html.velocity")).read())
                    .addAttachment(new URLAttachment("https://design.jboss.org/seam/logo/final/seam_mail_85px.png", "seamLogo.png", ContentDisposition.INLINE)).send();
        }
        finally {
            stop(wiser);
        }

        Assert.assertEquals("Didn't receive the expected amount of messages. Expected 1 got " + wiser.getMessages().size(), 1, wiser.getMessages().size());

        MimeMessage mess = MailUtilityTest.fromWiser(wiser.getMessages().get(0));

        Assert.assertEquals("Subject has been modified", subject, MimeUtility.unfold(mess.getHeader("Subject", null)));
        EmailMessage convertedMessage = MessageConverter.convert(mess);
        Assert.assertEquals(convertedMessage.getSubject(), subject);
    }

    @Test(expected = SendFailedException.class)
    public void testVelocityTextMailMessageSendFailed() throws IOException {
        SessionConfig mailConfig = TestMailConfigs.standardConfig();
        String uuid = java.util.UUID.randomUUID().toString();
        String subject = "Text Message from $version Mail - " + uuid;
        String version = "Seam 3";

        // Port is two off so this should fail
        Wiser wiser = new Wiser(mailConfig.getServerPort() + 2);
        wiser.setHostname(mailConfig.getServerHost());
        try {
            wiser.start();

            new MailMessageImpl(mailConfig).from(fromAddress).replyTo(replyToAddress).to(toAddress).subject(new VelocityTemplate(subject))
                    .bodyText(new VelocityTemplate(Resources.asCharSource(Resources.getResource("template.text.velocity"), StandardCharsets.UTF_8).read())).put("version", version)
                    .importance(MessagePriority.HIGH).send();
        }
        finally {
            stop(wiser);
        }
    }

    /**
     * Wiser takes a fraction of a second to shut down, so let it finish.
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

    private static String expectedHtmlBody(EmailMessage emailMessage, String name, String email, String version) {

        return "<html xmlns=\"http://www.w3.org/1999/xhtml\">" + "\r\n" + "<body>" + "\r\n" + "<p><b>Dear <a href=\"mailto:" + email + "\">" + name + "</a>,</b></p>" + "\r\n"
                + "<p>This is an example <i>HTML</i> email sent by " + version + " and Velocity.</p>" + "\r\n" + "<p><img src=\"cid:" + EmailAttachmentUtil.getEmailAttachmentMap(
                emailMessage.getAttachments()).get("seamLogo.png").getContentId() + "\" /></p>" + "\r\n" + "<p>It has an alternative text body for mail readers that don't support html.</p>" + "\r\n"
                + "</body>" + "\r\n" + "</html>";
    }

    private static String expectedTextBody(String name, String version) {

        return "Hello " + name + ",\r\n" + "\r\n" + "This is the alternative text body for mail readers that don't support html. This was sent with " + version + " and Velocity.";
    }
}
