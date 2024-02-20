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

package co.cfly.email.api;

import java.util.Map;

import co.cfly.email.impl.attachments.BaseAttachment;

public class MailContext {

    private Map<String, BaseAttachment> attachments;

    public MailContext(Map<String, BaseAttachment> attachments) {
        this.attachments = attachments;
    }

    public String insert(String fileName) {
        final BaseAttachment attachment = attachments.get(fileName);

        if (attachment == null) {
            throw new RuntimeException("Unable to find attachment: " + fileName);
        }
        else {
            return "cid:" + attachment.getContentId();
        }
    }
}
