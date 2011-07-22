package org.syzygy.util.midp;

import org.syzygy.util.PropertySource;

import javax.microedition.midlet.MIDlet;

public final class MIDletBackedPropertySource implements PropertySource
{
    public MIDletBackedPropertySource(PropertySource props, MIDlet midlet)
    {
        this.props = props;
        this.midlet = midlet;
    }

    public String getProperty(String propertyName) throws Exception
    {
        String p = props.getProperty(propertyName);
        return p == null ? midlet.getAppProperty(propertyName) : p;
    }

    private final PropertySource props;
    private final MIDlet midlet;
}
