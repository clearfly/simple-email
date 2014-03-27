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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;

import com.outjected.email.api.TemplateProvider;
import com.outjected.email.api.TemplatingException;

/**
 * @author Cody Lerum
 */
public class VelocityTemplate implements TemplateProvider
{
    private VelocityEngine velocityEngine;
    private VelocityContext velocityContext;
    private InputStream inputStream;

    public VelocityTemplate(InputStream inputStream)
    {
        velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
                "org.apache.velocity.runtime.log.NullLogChute");
        this.inputStream = inputStream;
    }

    public VelocityTemplate(String string)
    {
        this(new ByteArrayInputStream(string.getBytes()));
    }

    public VelocityTemplate(File file) throws FileNotFoundException
    {
        this(new FileInputStream(file));
    }

    public String merge(Map<String, Object> context)
    {
        StringWriter writer = new StringWriter();

        velocityContext = new VelocityContext(context);

        try
        {
            velocityEngine.evaluate(velocityContext, writer, "mailGenerated", new InputStreamReader(inputStream));
        }
        catch (ResourceNotFoundException e)
        {
            throw new TemplatingException("Unable to find template", e);
        }
        catch (ParseErrorException e)
        {
            throw new TemplatingException("Unable to find template", e);
        }
        catch (MethodInvocationException e)
        {
            throw new TemplatingException("Error processing method referenced in context", e);
        }

        return writer.toString();
    }
}
