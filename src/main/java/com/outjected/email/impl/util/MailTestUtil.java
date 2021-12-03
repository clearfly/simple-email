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
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

public class MailTestUtil {
    public static String getAddressHeader(String address) {
        return address;
    }

    public static String getAddressHeader(String name, String address) {
        return name + " <" + address + ">";
    }

    public static String getStringContent(MimeMultipart mmp, int index) throws IOException, MessagingException {
        return getStringContent(mmp.getBodyPart(index));
    }

    public static String getStringContent(BodyPart bodyPart) throws IOException, MessagingException {
        return (String) bodyPart.getContent();
    }
}
