package com.outjected.email;

import org.junit.Assert;
import org.junit.Test;

import com.outjected.email.impl.util.MailUtility;


public class MailUtilityTest {

    @Test
    public void decodeString() {
        Assert.assertNull(MailUtility.decodeString(null));
        Assert.assertEquals("Invoice.pdf", MailUtility.decodeString("=?utf-8?B?SW52b2ljZS5wZGY=?="));
        Assert.assertEquals("Invoice.pdf", MailUtility.decodeString("=?utf-8?b?SW52b2ljZS5wZGY=?="));
        Assert.assertEquals("Invoice.pdf", MailUtility.decodeString("Invoice.pdf"));
        Assert.assertEquals("Invoice.pdf", MailUtility.decodeString("=?us-ascii?Q?Invoice.pdf?="));
        Assert.assertEquals("Invoice.pdf", MailUtility.decodeString("=?utf-8?q?Invoice.pdf?="));
        Assert.assertEquals("this is some text", MailUtility.decodeString("=?iso-8859-1?q?this=20is=20some=20text?="));
    }
}
