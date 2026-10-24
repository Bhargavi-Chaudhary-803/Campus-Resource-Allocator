package com.campus;

import com.campus.gui.MainFrame;
import com.campus.gui.UITheme;

import javax.swing.SwingUtilities;

/**
 * =========================================================================
 * APPLICATION ENTRY POINT: CAMPUS RESOURCE ALLOCATION SIMULATOR
 * =========================================================================
 * A clean, standard Java desktop simulator built to showcase:
 * - Object-Oriented Programming Principles (Encapsulation, Inheritance, Polymorphism, Abstraction)
 * - Software Design Patterns (Strategy, Factory Method, Singleton)
 * - Core Java Capabilities (Collections, Streams, Custom Exceptions, Multithreading, File I/O)
 * - Clean Modern Swing UI (Blue & Off-White palette with tight-spaced Inter typography)
 *
 * Compilation:
 *   javac -d bin -sourcepath src src/com/campus/Main.java
 *
 * Execution:
 *   java -cp bin com.campus.Main
 */
public class Main {

    public static void main(String[] args) {
        // Apply clean modern look and feel defaults
        UITheme.applyGlobalDefaults();

        // Launch GUI safely on the Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainFrame app = new MainFrame();
            app.setVisible(true);
        });
    }
}
