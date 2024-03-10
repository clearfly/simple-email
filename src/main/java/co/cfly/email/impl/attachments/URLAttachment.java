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

import java.io.IOException;
import java.net.URL;

import co.cfly.email.api.AttachmentException;
import co.cfly.email.api.ContentDisposition;
import co.cfly.email.api.Header;
import co.cfly.email.impl.util.Streams;
import jakarta.activation.URLDataSource;

public class URLAttachment extends BaseAttachment {

    public URLAttachment(String url, String fileName, ContentDisposition contentDisposition) {
        super();
        URLDataSource uds;
        try {
            uds = new URLDataSource(new URL(url));
            super.setFileName(fileName);
            super.setMimeType(uds.getContentType());
            super.setContentDisposition(contentDisposition);
            super.setBytes(Streams.toByteArray(uds.getInputStream()));
        }
        catch (IOException e) {
            throw new AttachmentException("Wasn't able to create email attachment from URL: " + url, e);
        }
    }

    public URLAttachment(String url, String fileName, ContentDisposition contentDisposition, String contentClass) {
        this(url, fileName, contentDisposition);
        super.addHeader(new Header("Content-Class", contentClass));
    }
}
