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

package com.outjected.email.impl.attachments;

import java.io.IOException;
import java.io.InputStream;

import com.outjected.email.api.AttachmentException;
import com.outjected.email.api.ContentDisposition;
import com.outjected.email.api.Header;
import com.outjected.email.impl.util.Streams;

public class InputStreamAttachment extends BaseAttachment {

    public InputStreamAttachment(String fileName, String mimeType, ContentDisposition contentDisposition, InputStream inputStream) {
        super();

        try {
            super.setFileName(fileName);
            super.setMimeType(mimeType);
            super.setContentDisposition(contentDisposition);
            super.setBytes(Streams.toByteArray(inputStream));
        }
        catch (IOException e) {
            throw new AttachmentException("Wasn't able to create email attachment from InputStream");
        }
    }

    public InputStreamAttachment(String fileName, String mimeType, ContentDisposition contentDisposition, InputStream inputStream, String contentClass) {
        this(fileName, mimeType, contentDisposition, inputStream);
        super.addHeader(new Header("Content-Class", contentClass));
    }
}
