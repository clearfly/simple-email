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

package co.cfly.email.impl.attachments;

import java.util.ArrayList;
import java.util.Collection;

import co.cfly.email.api.ContentDisposition;
import co.cfly.email.api.Header;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.util.ByteArrayDataSource;

public class AttachmentPart extends MimeBodyPart {

    public AttachmentPart(DataSource dataSource, String uid, String fileName, Collection<Header> headers, ContentDisposition contentDisposition) {
        super();

        try {
            if (uid != null) {
                setContentID("<" + uid + ">");
            }
        }
        catch (MessagingException e1) {
            throw new RuntimeException("Unable to set unique content-id on attachment");
        }

        setData(dataSource);

        if (fileName != null) {
            try {
                setFileName(fileName);
            }
            catch (MessagingException e) {
                throw new RuntimeException("Unable to get FileName on attachment");
            }
        }

        if (headers != null) {
            for (Header header : headers) {
                try {
                    addHeader(header.getName(), header.getValue());

                }
                catch (MessagingException e) {
                    throw new RuntimeException("Unable to add Content-Class Header");
                }
            }
        }

        setContentDisposition(contentDisposition);
    }

    public AttachmentPart(byte[] bytes, String uid, String fileName, String mimeType, Collection<Header> headers, ContentDisposition contentDisposition) {
        this(getByteArrayDataSource(bytes, mimeType), uid, fileName, headers, contentDisposition);
    }

    public AttachmentPart(byte[] bytes, String uid, String fileName, String mimeType, ContentDisposition contentDisposition) {
        this(getByteArrayDataSource(bytes, mimeType), uid, fileName, new ArrayList<>(), contentDisposition);
    }

    public String getAttachmentFileName() {
        try {
            return getFileName();
        }
        catch (MessagingException e) {
            throw new RuntimeException("Unable to get File Name from attachment");
        }
    }

    public ContentDisposition getContentDisposition() {
        try {
            return ContentDisposition.mapValue(getDisposition());
        }
        catch (MessagingException e) {
            throw new RuntimeException("Unable to get Content-Disposition on attachment");
        }
    }

    public void setContentDisposition(ContentDisposition contentDisposition) {
        try {
            setDisposition(contentDisposition.headerValue());
        }
        catch (MessagingException e) {
            throw new RuntimeException("Unable to set Content-Disposition on attachment");
        }
    }

    private void setData(DataSource datasource) {
        try {
            setDataHandler(new DataHandler(datasource));
        }
        catch (MessagingException e) {
            throw new RuntimeException("Unable to set Data on attachment");
        }
    }

    private static ByteArrayDataSource getByteArrayDataSource(byte[] bytes, String mimeType) {
        return new ByteArrayDataSource(bytes, mimeType);
    }
}
