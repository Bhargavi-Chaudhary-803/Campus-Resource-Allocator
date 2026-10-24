package com.campus.simulator;

import com.campus.simulator.ui.component.UITheme;
import com.campus.simulator.ui.view.MainFrame;

import javax.swing.*;
import java.awt.GraphicsEnvironment;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main application entrypoint for the Campus Resource Allocation Simulator.
 */
public class App {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        LOGGER.info("Starting Campus Resource Allocation Simulator...");

        // Initialize typography, Look & Feel, and UI constants
        UITheme.applyGlobalTheme();

        // Assemble domain, repository, and service contexts
        SimulationContext context = SimulationContext.getInstance();

        // Launch Main Window strictly on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                if (GraphicsEnvironment.isHeadless()) {
                    LOGGER.info("Headless environment detected. Context initialized successfully without display.");
                    return;
                }
                MainFrame mainFrame = new MainFrame(context);
                mainFrame.setVisible(true);
                LOGGER.info("Main application window initialized successfully.");
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Fatal error launching UI on Event Dispatch Thread", ex);
            }
        });
    }
}
