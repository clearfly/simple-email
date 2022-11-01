package com.outjected.email;

import com.outjected.email.impl.util.HtmlToPlainText;
import org.junit.Assert;
import org.junit.Test;

public class HtmlToTextTest {

    @Test
    public void convert() {
        final String html = "<a href=\"https://www.clearfly.net/\">Foo</a>";
        final String text = HtmlToPlainText.convert(html);
        Assert.assertEquals("Foo ( https://www.clearfly.net/ )", text);
    }
}
