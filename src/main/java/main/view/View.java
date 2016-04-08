package main.view;

import main.controller.IController;
import main.model.*;
import main.view.painters.DeskPainter;
import main.view.painters.DeskPaintersFactory;
import main.view.painters.DrawingType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Dmitriy Albot
 */
public class View implements IView, GraphicsObserver, ModelObserver {
    private IController controller;
    private IModel model;
    private JFrame frame;
    private JPanel desk;
    private JPanel configPanel;
    private JButton startConnection;
    private JButton stopConnection;
    private JButton clearScreen;
    private JRadioButton pauseConnection;
    private JSlider accuracy;
    private JTextField host;
    private JTextField port;
    private JLabel status;
    private JComboBox<DrawingType> drawingTypeJComboBox;
    private DeskPainter deskPainter;
    private static View instance;
    private ServerStatus serverStatus;


    private View(IModel model) {
        this.model = model;
        serverStatus = ServerStatus.SERVER_IS_UNAVAILABLE;
        model.addGraphicsObserver(this);
        model.addModelObserver(this);
        initGraphics();
    }

    public static View getInstance(IModel model) {
        if (instance == null) {
            instance = new View(model);
        }
        return instance;
    }

    private void initGraphics() {
        frame = new JFrame("Graphical client");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        configPanel = new JPanel();
        GroupLayout layout = new GroupLayout(configPanel);
        configPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        desk = new DrawingBoard();
        desk.setBackground(new Color(255, 255, 255));
        startConnection = new JButton("Start connection");
        pauseConnection = new JRadioButton("Pause connection");
        pauseConnection.setEnabled(false);
        status = new JLabel();
        drawingTypeJComboBox = new JComboBox<>(DrawingType.values());
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(drawingTypeJComboBox)
                .addComponent(startConnection)
                .addComponent(pauseConnection));
        layout.linkSize(drawingTypeJComboBox);
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(drawingTypeJComboBox)
                .addComponent(startConnection)
                .addComponent(pauseConnection));
        layout.linkSize(desk);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.add(desk);
        frame.add(BorderLayout.EAST, configPanel);
        frame.add(BorderLayout.SOUTH, status);
        frame.setSize(screenSize);
        frame.setVisible(true);
        buttonListenersConfig();
    }

    private void buttonListenersConfig() {
        startConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.startConnection();
                startConnection.setEnabled(false);
                drawingTypeJComboBox.setEnabled(false);
                deskPainter = DeskPaintersFactory.getPainter((DrawingType) drawingTypeJComboBox.getSelectedItem());
            }
        });
        pauseConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pauseConnection.isSelected()) {
                    controller.pauseConnection();
                } else {
                    controller.resumeConnection();
                }
            }
        });
    }

    @Override
    public void updateGraphics() {
        deskPainter.draw(desk, model.getCommandPool());
        desk.repaint();
    }

    @Override
    public void updateModelObserver() {
        serverStatus = model.getServerStatus();
        switch (serverStatus) {
            case SERVER_IS_UNAVAILABLE:
                startConnection.setEnabled(true);
                pauseConnection.setEnabled(false);
                drawingTypeJComboBox.setEnabled(true);
                status.setText("Server is unavailable");
                break;
            case SERVER_IS_AVAILABLE:
                status.setText("Connection established");
                pauseConnection.setEnabled(true);
                break;
            default:
                status.setText("Unknown server status");
                break;
        }
    }

    @Override
    public void setController(IController controller) {
        this.controller = controller;
    }
}
