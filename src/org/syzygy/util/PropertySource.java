package org.syzygy.util;

public interface PropertySource
{
    String getProperty(String propertyName) throws Exception;
}
