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

package com.outjected.email.impl.templating.freemarker;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.outjected.email.api.TemplateProvider;
import com.outjected.email.api.TemplatingException;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author Cody Lerum
 */
public class FreeMarkerTemplate implements TemplateProvider
{
    private Configuration configuration;
    private Map<String, Object> rootMap = new HashMap<String, Object>();
    private InputStream inputStream;

    public FreeMarkerTemplate(InputStream inputStream)
    {
        this.inputStream = inputStream;
        configuration = new Configuration();
        configuration.setObjectWrapper(new DefaultObjectWrapper());
    }

    public FreeMarkerTemplate(String string)
    {
        this(new ByteArrayInputStream(string.getBytes()));
    }

    public FreeMarkerTemplate(File file) throws FileNotFoundException
    {
        this(new FileInputStream(file));
    }

    public String merge(Map<String, Object> context)
    {
        rootMap.putAll(context);

        StringWriter writer = new StringWriter();

        try
        {
            Template template = new Template("mailGenerated", new InputStreamReader(inputStream), configuration);
            template.process(rootMap, writer);
        }
        catch (IOException e)
        {
            throw new TemplatingException("Error creating template", e);
        }
        catch (TemplateException e)
        {
            throw new TemplatingException("Error rendering output", e);
        }

        return writer.toString();
    }
}
