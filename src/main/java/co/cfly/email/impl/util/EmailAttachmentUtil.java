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

package co.cfly.email.impl.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import co.cfly.email.impl.attachments.BaseAttachment;

public class EmailAttachmentUtil {
    public static Map<String, BaseAttachment> getEmailAttachmentMap(Collection<BaseAttachment> attachments) {
        Map<String, BaseAttachment> emailAttachmentMap = new HashMap<>();

        for (BaseAttachment ea : attachments) {
            if (!Strings.isNullOrBlank(ea.getFileName())) {
                emailAttachmentMap.put(ea.getFileName(), ea);
            }
        }
        return emailAttachmentMap;
    }
}
