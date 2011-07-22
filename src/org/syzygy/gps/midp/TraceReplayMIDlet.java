package org.syzygy.gps.midp;

public class TraceReplayMIDlet extends ReplayMIDlet
{
    public TraceReplayMIDlet() throws Exception
    {
        super("Traces");
        this.traces = props.getProperty("gps.midlet.traces");
    }

    protected String getLocations()
    {
        return traces;
    }

    private final String traces;
}
