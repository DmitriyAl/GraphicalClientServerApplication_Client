package main.view.painters;

import main.model.Command;
import main.model.Point;
import main.view.painters.exceptions.NoSuchColorInLibraryException;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * @author Albot Dmitriy
 */
public class DotToDotDeskPainter implements DeskPainter {

    @Override
    public void draw(final JPanel panel, Deque<Command> commands) { //todo fix
        Graphics graphics = panel.getGraphics();
        Command command = commands.peekLast();
        Point point = command.getPoint();
        Color color;
        try {
            color = ColorLibrary.getColor(command.getPoint().getColor());
        } catch (NoSuchColorInLibraryException e) {
            color = new Color(0, 0, 0);
        }
        graphics.setColor(color);
        int width = panel.getWidth();
        int height = panel.getHeight();
        graphics.drawOval((int) (point.getX() * width), (int) (point.getY() * height), 5, 5);
//        return graphics;
    }

    @Override
    public void clearScreen() {

    }
}
