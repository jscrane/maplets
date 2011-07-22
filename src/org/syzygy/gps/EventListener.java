package org.syzygy.gps;

public interface EventListener
{
    void zoomIn();

    void zoomOut();

    void pan(int x, int y);

    void speedUp();

    void slowDown();

    void exit();
}
