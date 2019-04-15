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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.outjected.email.api.AttachmentException;
import com.outjected.email.api.ContentDisposition;
import com.outjected.email.api.Header;

/**
 * @author Cody Lerum
 */
public class FileAttachment extends BaseAttachment {
    private static final long serialVersionUID = 1L;

    public FileAttachment(ContentDisposition contentDisposition, Path path) {
        super();
        try {
            super.setFileName(path.getFileName().toString());
            super.setMimeType(Files.probeContentType(path));
            super.setContentDisposition(contentDisposition);
            super.setBytes(Files.readAllBytes(path));
        }
        catch (IOException e) {
            throw new AttachmentException("Wasn't able to create email attachment from File: " + path.getFileName().toString(), e);
        }
    }

    public FileAttachment(ContentDisposition contentDisposition, Path path, String contentClass) {
        this(contentDisposition, path);
        super.addHeader(new Header("Content-Class", contentClass));
    }

    public FileAttachment(ContentDisposition contentDisposition, File file) {
        this(contentDisposition, file.toPath());
    }

    public FileAttachment(ContentDisposition contentDisposition, File file, String contentClass) {
        this(contentDisposition, file.toPath());
        super.addHeader(new Header("Content-Class", contentClass));
    }
}
