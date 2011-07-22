package org.syzygy.gps.midp;

public class BookmarkMIDlet extends ReplayMIDlet
{
    public BookmarkMIDlet() throws Exception
    {
        super("Bookmarks");
        this.bookmarks = props.getProperty("gps.midlet.bookmarks");
    }

    protected String getLocations()
    {
        return bookmarks;
    }

    private final String bookmarks;
}
