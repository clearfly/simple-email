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

public enum RecipientType {
    TO("To"),
    CC("Cc"),
    BCC("Bcc");

    private final String typeName;

     RecipientType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public jakarta.mail.Message.RecipientType asJavaMailType() {
        return switch (this) {
            case TO -> jakarta.mail.Message.RecipientType.TO;
            case CC -> jakarta.mail.Message.RecipientType.CC;
            case BCC -> jakarta.mail.Message.RecipientType.BCC;
        };
    }
}
