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

import javax.mail.internet.MimeBodyPart;

/**
 * Defines the available Dispostions for attachments in an email Message.
 * <p/>
 * <p>
 * INLINE is used where an attachment should be displayed in the body of the message such as a image reference in an
 * HTML message body
 * </p>
 * <p/>
 * <p>
 * ATTACHMENT is used for standard file attachments to a message.
 * </p>
 * 
 * @author Cody Lerum
 */
public enum ContentDisposition
{
    ATTACHMENT(MimeBodyPart.ATTACHMENT),
    INLINE(MimeBodyPart.INLINE);

    private String headerValue;

    private ContentDisposition(String headerValue)
    {
        this.headerValue = headerValue;
    }

    public String headerValue()
    {
        return headerValue;
    }

    public static ContentDisposition mapValue(String value)
    {
        if (value.equals(MimeBodyPart.ATTACHMENT))
        {
            return ContentDisposition.ATTACHMENT;
        }
        else if (value.equals(MimeBodyPart.INLINE))
        {
            return ContentDisposition.INLINE;
        }
        else
        {
            throw new UnsupportedOperationException("Unsupported Content DispostionType: " + value);
        }
    }
}
