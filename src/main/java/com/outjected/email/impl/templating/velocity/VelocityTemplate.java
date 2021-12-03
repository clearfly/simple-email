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

package com.outjected.email.impl.templating.velocity;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

import com.outjected.email.api.TemplateProvider;
import com.outjected.email.api.TemplatingException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

public class VelocityTemplate implements TemplateProvider {
    private VelocityEngine velocityEngine;
    private String template;

    public VelocityTemplate(String template) {
        velocityEngine = new VelocityEngine();
        this.template = template;
    }

    public VelocityTemplate(File file) throws IOException {
        this(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
    }

    @Override
    public String merge(Map<String, Object> context) {
        final StringWriter writer = new StringWriter();
        final VelocityContext velocityContext = new VelocityContext(context);

        try {
            velocityEngine.evaluate(velocityContext, writer, "mailGenerated", template);
        }
        catch (ResourceNotFoundException | ParseErrorException e) {
            throw new TemplatingException("Unable to find template", e);
        }
        catch (MethodInvocationException e) {
            throw new TemplatingException("Error processing method referenced in context", e);
        }

        return writer.toString();
    }
}
