package org.syzygy.gps.midp;

import com.sun.lwuit.*;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.plaf.UIManager;
import com.sun.lwuit.util.Resources;
import org.syzygy.util.midp.FileUtil;
import org.syzygy.util.midp.RmsPropertySource;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

public final class SetupMIDlet extends PropertyConfiguredMIDlet
{
    private void tryCreateDirectory(String path)
    {
        try {
            FileUtil.createDirectory(path);
        } catch (IOException e) {
            error(e, "Creating " + path);
        }
    }

    public SetupMIDlet() throws Exception
    {
        Display.init(this);

        UIManager mgr = UIManager.getInstance();
        Resources res = Resources.open("/resources.res");
        mgr.setThemeProps(res.getTheme("business"));

        this.rms = new RmsPropertySource(getAppProperty("gps.midlet.title"));

        this.form = new Form("Settings");
        this.save = new Command("Save")
        {
            public void actionPerformed(ActionEvent event)
            {
                rms.setProperty("gps.receiver.url", url.getText());
                rms.setProperty("gps.channels", channels.getText());
                rms.setProperty("gps.midlet.maps", maps.getText());
                rms.setProperty("gps.midlet.traces", traces.getText());
                rms.setProperty("gps.midlet.bookmarks", bookmarks.getText());
                rms.setProperty("gps.coordinate.mapper", (String) map.get(mapper.getSelectedItem()));
                try {
                    rms.save();
                } catch (Exception e) {
                    error(e, "Saving settings");
                }
                tryCreateDirectory(maps.getText());
                tryCreateDirectory(traces.getText());
                tryCreateDirectory(bookmarks.getText());
                destroyApp(true);
            }
        };
        this.quit = new Command("Quit")
        {
            public void actionPerformed(ActionEvent e)
            {
                destroyApp(true);
            }
        };
        this.url = new TextField(props.getProperty("gps.receiver.url"));
        this.channels = new TextField(props.getProperty("gps.channels"), 2);
        this.maps = new TextField(props.getProperty("gps.midlet.maps"));
        this.traces = new TextField(props.getProperty("gps.midlet.traces"));
        this.bookmarks = new TextField(props.getProperty("gps.midlet.bookmarks"));

        String m = props.getProperty("gps.coordinate.mappers"), s = props.getProperty("gps.coordinate.mapper");
        Vector names = new Vector();
        int sel = -1;
        for (int i = 0; m.length() > 0; i++) {
            int c = m.indexOf(':'), p = m.indexOf('|');
            String n = m.substring(0, c);
            String cl = m.substring(c + 1, p);
            map.put(n, cl);
            if (cl.equals(s))
                sel = i;
            names.addElement(n);
            m = m.substring(p + 1);
        }

        this.mapper = new ComboBox(names);
        if (sel != -1)
            mapper.setSelectedIndex(sel);
    }

    protected void startApp()
    {
        form.setLayout(new BoxLayout(BoxLayout.Y_AXIS));

        Container a = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        a.addComponent(new Label("Receiver"));
        a.addComponent(url);
        form.addComponent(a);

        Container c = new Container();
        c.addComponent(new Label("Number of Channels"));
        channels.setInputMode("123");
        c.addComponent(channels);
        form.addComponent(c);

        Container m = new Container();
        m.addComponent(new Label("Mapper"));
        m.addComponent(mapper);
        form.addComponent(m);

        Container ms = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        ms.addComponent(new Label("Maps"));
        ms.addComponent(maps);
        form.addComponent(ms);

        Container t = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        t.addComponent(new Label("Traces"));
        t.addComponent(traces);
        form.addComponent(t);

        Container b = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        b.addComponent(new Label("Bookmarks"));
        b.addComponent(bookmarks);
        form.addComponent(b);

        form.addCommand(save);
        form.addCommand(quit);
        form.show();
    }

    protected void destroyApp(boolean unconditionally)
    {
        notifyDestroyed();
    }

    protected void pauseApp()
    {
    }

    private final RmsPropertySource rms;
    private final Form form;
    private final TextField url, channels, maps, traces, bookmarks;
    private final ComboBox mapper;
    private final Command save, quit;
    private final Hashtable map = new Hashtable();
}
