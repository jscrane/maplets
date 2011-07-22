package org.syzygy.gps.midp;

import org.syzygy.util.PropertySource;
import org.syzygy.util.midp.MIDletBackedPropertySource;
import org.syzygy.util.midp.RmsPropertySource;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public abstract class PropertyConfiguredMIDlet extends MIDlet
{
    protected final Display display;
    protected final PropertySource props;

    protected PropertyConfiguredMIDlet()
    {
        PropertySource rms = new RmsPropertySource(getAppProperty("gps.midlet.title"));
        this.props = new MIDletBackedPropertySource(rms, this);
        this.display = Display.getDisplay(this);
    }

    protected void error(Exception e, String context)
    {
        message(e, "Error!", context, true);
    }

    protected void message(Exception e, String title, String context, boolean fatal)
    {
        message(title, context + ": " + e, fatal);
    }

    protected void quit(boolean unconditionally)
    {
        try {
            destroyApp(true);
        } catch (MIDletStateChangeException _) {
            // hmmm
        }
    }

    protected void message(String title, String msg, final boolean fatal)
    {
        Alert alert = new Alert(title, msg, null, AlertType.ERROR);
        alert.setCommandListener(new CommandListener()
        {
            public void commandAction(Command c, Displayable d)
            {
                if (fatal)
                    quit(true);
            }
        });
        alert.setTimeout(Alert.FOREVER);
        display.setCurrent(alert);
    }

    protected abstract class ExceptionHandler extends Thread
    {
        public ExceptionHandler(String message)
        {
            this.message = message;
        }

        public void run()
        {
            try {
                doRun();
            } catch (Exception e) {
                error(e, message);
            }
        }

        protected abstract void doRun() throws Exception;

        private final String message;
    }
}
