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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

import jakarta.mail.internet.MimeUtility;
import jakarta.xml.bind.annotation.XmlElement;

public class Header implements Serializable {

    private String name;
    private String value;

    public Header() {
        // Required for JAX-B
    }

    public Header(String name, String value) {
        this.name = name;

        try {
            this.value = MimeUtility.fold(name.length() + 2, MimeUtility.encodeText(value));
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to create header", e);
        }
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Header) {
            return getName().equalsIgnoreCase(((Header) o).getName()) && getValue().equalsIgnoreCase(((Header) o).getValue());
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName().toLowerCase(), getValue().toLowerCase());
    }
}
