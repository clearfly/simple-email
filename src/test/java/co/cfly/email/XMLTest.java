package co.cfly.email;

import java.io.IOException;
import java.util.UUID;

import co.cfly.email.api.ContentDisposition;
import co.cfly.email.api.EmailMessage;
import co.cfly.email.api.Header;
import co.cfly.email.api.MessagePriority;
import co.cfly.email.impl.attachments.BaseAttachment;
import co.cfly.email.util.XMLUtil;
import com.google.common.io.Resources;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.xml.bind.JAXBException;
import org.junit.Assert;
import org.junit.Test;

public class XMLTest {

    @Test
    public void simple() throws AddressException, IOException, JAXBException {
        EmailMessage msg = new EmailMessage();
        msg.setMessageId(UUID.randomUUID() + "@test.org");
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
        Assert.assertEquals(msg.getType(), umsg.getType());
        Assert.assertEquals(msg.getCharset(), umsg.getCharset());
        Assert.assertEquals(msg.getImportance(), umsg.getImportance());
        Assert.assertEquals(msg.getToAddresses().stream().findFirst().orElseThrow(), umsg.getToAddresses().stream().findFirst().orElseThrow());
        Assert.assertEquals(msg.getFromAddresses().stream().findFirst().orElseThrow(), umsg.getFromAddresses().stream().findFirst().orElseThrow());
        Assert.assertEquals(msg.getCcAddresses().stream().findFirst().orElseThrow(), umsg.getCcAddresses().stream().findFirst().orElseThrow());
        Assert.assertEquals(msg.getBccAddresses().stream().findFirst().orElseThrow(), umsg.getBccAddresses().stream().findFirst().orElseThrow());
        Assert.assertEquals(msg.getSubject(), umsg.getSubject());
        Assert.assertEquals(msg.getTextBody(), umsg.getTextBody());
        Assert.assertEquals(msg.getHtmlBody(), umsg.getHtmlBody());
        Assert.assertEquals(msg.getMessageId(), umsg.getMessageId());
        Assert.assertEquals(msg.getAttachments().get(0).getFileName(), umsg.getAttachments().get(0).getFileName());
        Assert.assertEquals(msg.getCustomVariables().get("foo"), umsg.getCustomVariables().get("foo"));
        Assert.assertEquals(msg.getCustomVariables().get("x"), umsg.getCustomVariables().get("x"));
    }
}
