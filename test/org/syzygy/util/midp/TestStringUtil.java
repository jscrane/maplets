package org.syzygy.util.midp;

import junit.framework.TestCase;

public class TestStringUtil extends TestCase
{
    public void testSplit()
    {
        String s = "hello world, blah blah";
        String[] split = StringUtil.split(s, ' ');
        assertEquals(4, split.length);
        assertEquals("hello", split[0]);
        assertEquals("world,", split[1]);
        assertEquals("blah", split[2]);
        assertEquals("blah", split[3]);
    }
}