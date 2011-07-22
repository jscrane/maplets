package org.syzygy.util.midp;

import junit.framework.TestCase;

public class TestHttpUtil extends TestCase
{
    public void testUrlEncode() throws Exception
    {
        assertEquals("%22Decoded+data%21%22", HttpUtil.urlEncode("\"Decoded data!\""));
        assertEquals("We%27re+%231%21", HttpUtil.urlEncode("We're #1!"));
    }
}