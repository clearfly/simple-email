package com.outjected.email;

import java.io.IOException;
import java.util.UUID;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.io.Resources;
import com.outjected.email.api.ContentDisposition;
import com.outjected.email.api.EmailMessage;
import com.outjected.email.api.Header;
import com.outjected.email.api.MessagePriority;
import com.outjected.email.impl.attachments.BaseAttachment;
import com.outjected.email.util.XMLUtil;

public class XMLTests {

    @Test public void simple() throws AddressException, JAXBException, IOException {
        EmailMessage msg = new EmailMessage();
        msg.setMessageId(UUID.randomUUID().toString() + "@test.org");
        msg.setImportance(MessagePriority.HIGH);
        msg.getFromAddresses().add(new InternetAddress("from@test.org", "Mr. From"));
        msg.getToAddresses().add(new InternetAddress("to@test.org"));
        msg.getCcAddresses().add(new InternetAddress("cc@test.org"));
        msg.getBccAddresses().add(new InternetAddress("bcc@test.org"));
        msg.setSubject("subject");
        msg.setTextBody("text body");
        msg.setHtmlBody("html body");
        msg.addAttachment(new BaseAttachment("myfile.txt", "text/plain", ContentDisposition.ATTACHMENT, Resources.toByteArray(Resources.getResource("template.text.velocity"))));
        msg.addAttachment(new BaseAttachment("myfile2.txt", "text/plain", ContentDisposition.ATTACHMENT, Resources.toByteArray(Resources.getResource("template.text.velocity"))));
        msg.setEnvelopeFrom(new InternetAddress("env-from@test.org"));
        msg.getReplyToAddresses().add(new InternetAddress("reply-to@test.org"));
        msg.getHeaders().add(new Header("Sender", "sender@test.org"));
        msg.getHeaders().add(new Header("X-Sender", "xsender@test.org"));
        msg.getCustomVariables().put("foo", "bar");
        msg.getCustomVariables().put("x", "y");

        String xml = XMLUtil.marshal(msg);
        EmailMessage umsg = XMLUtil.unmarshal(EmailMessage.class, xml);
        Assert.assertTrue(msg.getType().equals(umsg.getType()));
        Assert.assertTrue(msg.getCharset().equals(umsg.getCharset()));
        Assert.assertTrue(msg.getImportance().equals(umsg.getImportance()));
        Assert.assertTrue(msg.getToAddresses().get(0).equals(umsg.getToAddresses().get(0)));
        Assert.assertTrue(msg.getFromAddresses().get(0).equals(umsg.getFromAddresses().get(0)));
        Assert.assertTrue(msg.getCcAddresses().get(0).equals(umsg.getCcAddresses().get(0)));
        Assert.assertTrue(msg.getBccAddresses().get(0).equals(umsg.getBccAddresses().get(0)));
        Assert.assertTrue(msg.getSubject().equals(umsg.getSubject()));
        Assert.assertTrue(msg.getTextBody().equals(umsg.getTextBody()));
        Assert.assertTrue(msg.getHtmlBody().equals(umsg.getHtmlBody()));
        Assert.assertTrue(msg.getMessageId().equals(umsg.getMessageId()));
        Assert.assertTrue(msg.getAttachments().get(0).getFileName().equals(umsg.getAttachments().get(0).getFileName()));
        Assert.assertEquals(msg.getCustomVariables().get("foo"), umsg.getCustomVariables().get("foo"));
        Assert.assertEquals(msg.getCustomVariables().get("x"), umsg.getCustomVariables().get("x"));
    }
}
