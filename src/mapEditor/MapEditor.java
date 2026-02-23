package mapEditor;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class MapEditor {
    private static int ROWS;
    private static int COLS;
    private static final int TILE_SIZE = 20;
    private int selectedTile = 0;
    private int[][] map;
    private JButton[][] buttons;
    private static final String MAP_DIR = "src/mapEditor/maps";

    public MapEditor(int rows, int cols) {
        ROWS = rows;
        COLS = cols;
        map = new int[ROWS][COLS];
        buttons = new JButton[ROWS][COLS];

        JFrame frame = new JFrame("Map Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(COLS * TILE_SIZE + 200, ROWS * TILE_SIZE + 100);
        frame.setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel(new GridLayout(ROWS, COLS));
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
                button.setBackground(getTileColor(0));
                int finalRow = row;
                int finalCol = col;
                button.addActionListener(e -> {
                    map[finalRow][finalCol] = selectedTile;
                    button.setBackground(getTileColor(selectedTile));
                });
                buttons[row][col] = button;
                gridPanel.add(button);
            }
        }

        JPanel tilePanel = new JPanel(new GridLayout(5, 1));
        frame.add(tilePanel, BorderLayout.EAST);

        JButton grassButton = new JButton("Grass (0)");
        JButton wallButton = new JButton("Wall (1)");
        JButton waterButton = new JButton("Water (2)");
        JButton resetButton = new JButton("Reset");
        JButton exportButton = new JButton("Export Map");

        grassButton.addActionListener(e -> selectedTile = 0);
        wallButton.addActionListener(e -> selectedTile = 1);
        waterButton.addActionListener(e -> selectedTile = 2);
        resetButton.addActionListener(e -> resetMap());
        exportButton.addActionListener(e -> exportMap());

        tilePanel.add(grassButton);
        tilePanel.add(wallButton);
        tilePanel.add(waterButton);
        tilePanel.add(resetButton);
        tilePanel.add(exportButton);

        frame.setVisible(true);
    }

    private Color getTileColor(int tileType) {
        return switch (tileType) {
            case 0 -> Color.GREEN;
            case 1 -> Color.GRAY;
            case 2 -> Color.BLUE;
            default -> Color.WHITE;
        };
    }

    private void resetMap() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                map[row][col] = 0;
                buttons[row][col].setBackground(getTileColor(0));
            }
        }
    }

    private void exportMap() {
        try {
            Files.createDirectories(Path.of(MAP_DIR));

            String fileName = getNextFileName();
            Path fullPath = Path.of(MAP_DIR, fileName);

            try (BufferedWriter writer = Files.newBufferedWriter(fullPath, StandardOpenOption.CREATE)) {
                for (int row = 0; row < ROWS; row++) {
                    for (int col = 0; col < COLS; col++) {
                        writer.write(map[row][col] + (col == COLS - 1 ? "" : " "));
                    }
                    writer.newLine();
                }
                JOptionPane.showMessageDialog(null, "Mapa zapisana jako " + fullPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getNextFileName() {
        int mapNumber = 1;
        while (Files.exists(Path.of(MAP_DIR, String.format("map%02d.txt", mapNumber)))) {
            mapNumber++;
        }
        return String.format("map%02d.txt", mapNumber);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String rowsInput = JOptionPane.showInputDialog("Podaj liczbę wierszy (np. 12):");
            String colsInput = JOptionPane.showInputDialog("Podaj liczbę kolumn (np. 16):");

            try {
                int rows = Integer.parseInt(rowsInput);
                int cols = Integer.parseInt(colsInput);

                if (rows <= 0 || cols <= 0) {
                    JOptionPane.showMessageDialog(null, "Wymiary muszą być większe od zera!");
                    return;
                }

                new MapEditor(rows, cols);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Nieprawidłowy format liczby!");
            }
        });
    }
}
