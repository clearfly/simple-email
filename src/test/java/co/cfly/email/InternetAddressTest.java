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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import co.cfly.email.api.EmailContact;
import co.cfly.email.api.InvalidAddressException;
import co.cfly.email.api.MailMessage;
import co.cfly.email.impl.BasicEmailContact;
import co.cfly.email.impl.MailMessageImpl;
import co.cfly.email.util.TestMailConfigs;
import jakarta.mail.internet.InternetAddress;
import org.junit.Assert;
import org.junit.Test;

public class InternetAddressTest {

    @Test
    public void validAddresses() {
        MailMessage m = new MailMessageImpl(TestMailConfigs.standardConfig());

        BasicEmailContact seam = new BasicEmailContact("seam@domain.test");
        BasicEmailContact seamey = new BasicEmailContact("seamey@domain.test");

        Collection<EmailContact> addresses = new ArrayList<>();
        addresses.add(seamey);

        m.from("Seam Seamerson<seam@domain.test>");
        m.from("seam@domain.test");
        m.from(seam);

        m.to("seam@domain.test", "Seam Seamerson<seam@domain.test>");
        m.to("Seam Seamerson<seam@domain.test>");
        m.to("seam@domain.test");
        m.to(seam);
        m.to(addresses);

        m.cc("seam@domain.test", "Seam Seamerson<seam@domain.test>");
        m.cc("Seam Seamerson<seam@domain.test>");
        m.cc("seam@domain.test");
        m.cc(seam);
        m.cc(addresses);

        m.bcc("seam@domain.test", "Seam Seamerson<seam@domain.test>");
        m.bcc("Seam Seamerson<seam@domain.test>");
        m.bcc("seam@domain.test");
        m.bcc(seam);
        m.bcc(addresses);

        m.replyTo("Seam Seamerson<seam@domain.test>");
        m.replyTo("seam@domain.test");
        m.replyTo(seam);
    }

    @Test(expected = InvalidAddressException.class)
    public void invalidFromSimpleAddresses() {
        MailMessage m = new MailMessageImpl(TestMailConfigs.standardConfig());

        m.from("woo foo @bar.com");
    }

    @Test(expected = InvalidAddressException.class)
    public void invalidFromFullAddresses() {
        MailMessage m = new MailMessageImpl(TestMailConfigs.standardConfig());

        m.from("Woo");
    }

    @Test(expected = InvalidAddressException.class)
    public void invalidToSimpleAddresses() {
        MailMessage m = new MailMessageImpl(TestMailConfigs.standardConfig());

        m.to("woo foo @bar.com");
    }

    @Test(expected = InvalidAddressException.class)
    public void invalidToFullAddresses() {
        MailMessage m = new MailMessageImpl(TestMailConfigs.standardConfig());

        m.to("foo @bar.com", "Woo");
    }

    @Test(expected = InvalidAddressException.class)
    public void invalidCcSimpleAddresses() {
        MailMessage m = new MailMessageImpl(TestMailConfigs.standardConfig());

        m.cc("woo foo @bar.com");
    }

    @Test(expected = InvalidAddressException.class)
    public void invalidCcFullAddresses() {
        MailMessage m = new MailMessageImpl(TestMailConfigs.standardConfig());

        m.cc("foo @bar.com", "Woo");
    }

    @Test(expected = InvalidAddressException.class)
    public void invalidBccSimpleAddresses() {
        MailMessage m = new MailMessageImpl(TestMailConfigs.standardConfig());

        m.bcc("woo foo @bar.com");
    }

    @Test(expected = InvalidAddressException.class)
    public void invalidbccFullAddresses() {
        MailMessage m = new MailMessageImpl(TestMailConfigs.standardConfig());

        m.bcc("foo @bar.com", "Woo");
    }

    @Test(expected = InvalidAddressException.class)
    public void invalidReplyToSimpleAddresses() {
        MailMessage m = new MailMessageImpl(TestMailConfigs.standardConfig());

        m.replyTo("woo foo @bar.com");
    }

    @Test(expected = InvalidAddressException.class)
    public void invalidReplyToFullAddresses() {
        MailMessage m = new MailMessageImpl(TestMailConfigs.standardConfig());
        m.replyTo("Woo");
    }

    @Test(expected = InvalidAddressException.class)
    public void invalidDeliveryReceipt() {
        MailMessage m = new MailMessageImpl(TestMailConfigs.standardConfig());

        m.deliveryReceipt("woo foo @bar.com");
    }

    @Test(expected = InvalidAddressException.class)
    public void invalidReadReceipt() {
        MailMessage m = new MailMessageImpl(TestMailConfigs.standardConfig());

        m.readReceipt("woo foo @bar.com");
    }

    @Test
    public void duplicateSingle() {
        MailMessage m = new MailMessageImpl(TestMailConfigs.standardConfig());
        m.to("test@bar.com");
        m.to("Test@Bar.com");
        m.to("Testy<tEsT@bar.com>");
        Assert.assertEquals(1, m.getEmailMessage().getToAddresses().size());
        Assert.assertEquals("test@bar.com", m.getEmailMessage().getToAddresses().stream().findFirst().orElseThrow().getAddress());
    }

    @Test
    public void duplicateMultiple() {
        MailMessage m = new MailMessageImpl(TestMailConfigs.standardConfig());
        m.to("test@bar.com");
        m.to("Test@Bar.com");
        m.to("Testy<tEsT@bar.com>");
        m.to("Testy<foo@foo.com>");
        m.to("Testy Two<foo@foo.com>");

        List<InternetAddress> toAddresses = m.getEmailMessage().getToAddresses().stream().toList();

        Assert.assertEquals(2, toAddresses.size());
        Assert.assertEquals("test@bar.com", toAddresses.get(0).toString());
        Assert.assertEquals("Testy <foo@foo.com>", toAddresses.get(1).toString());

    }
}
