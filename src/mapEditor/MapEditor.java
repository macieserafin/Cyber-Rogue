package mapEditor;

import tile.Tile;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class MapEditor {

    private static final int TILE_SIZE = 20;
    private static final String MAP_DIR = "src/mapEditor/maps";

    private final int ROWS;
    private final int COLS;

    private int selectedTile = 0;
    private final int[][] map;
    private final JButton[][] buttons;

    private final Tile[] tiles = new Tile[6];

    public MapEditor(int rows, int cols) {
        this.ROWS = rows;
        this.COLS = cols;

        this.map = new int[ROWS][COLS];
        this.buttons = new JButton[ROWS][COLS];

        initTiles();

        JFrame frame = new JFrame("Map Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(COLS * TILE_SIZE + 240, ROWS * TILE_SIZE + 120);
        frame.setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel(new GridLayout(ROWS, COLS, 0, 0));
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
                button.setMargin(new Insets(0, 0, 0, 0));
                button.setFocusable(false);
                button.setBorderPainted(false);
                button.setContentAreaFilled(false);
                button.setOpaque(true);

                setButtonTile(button, 0);

                int finalRow = row;
                int finalCol = col;
                button.addActionListener(e -> {
                    map[finalRow][finalCol] = selectedTile;
                    setButtonTile(button, selectedTile);
                });

                buttons[row][col] = button;
                gridPanel.add(button);
            }
        }

        JPanel tilePanel = new JPanel(new GridLayout(0, 1, 6, 6));
        tilePanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        frame.add(tilePanel, BorderLayout.EAST);

        JButton grassButton = new JButton("Grass (0)");
        JButton wallButton = new JButton("Wall (1)");
        JButton waterButton = new JButton("Water (2)");
        JButton earthButton = new JButton("Earth (3)");
        JButton treeButton = new JButton("Tree (4)");
        JButton sandButton = new JButton("Sand (5)");
        JButton resetButton = new JButton("Reset");
        JButton exportButton = new JButton("Export Map");

        grassButton.addActionListener(e -> selectedTile = 0);
        wallButton.addActionListener(e -> selectedTile = 1);
        waterButton.addActionListener(e -> selectedTile = 2);
        earthButton.addActionListener(e -> selectedTile = 3);
        treeButton.addActionListener(e -> selectedTile = 4);
        sandButton.addActionListener(e -> selectedTile = 5);
        resetButton.addActionListener(e -> resetMap());
        exportButton.addActionListener(e -> exportMap());

        tilePanel.add(grassButton);
        tilePanel.add(wallButton);
        tilePanel.add(waterButton);
        tilePanel.add(earthButton);
        tilePanel.add(treeButton);
        tilePanel.add(sandButton);
        tilePanel.add(resetButton);
        tilePanel.add(exportButton);

        frame.setVisible(true);
    }

    private void initTiles() {
        // 0
        tiles[0] = new Tile();
        tiles[0].image = loadTileImage("tiles/grass.png");

        // 1
        tiles[1] = new Tile();
        tiles[1].image = loadTileImage("tiles/wall.png");
        tiles[1].collision = true;

        // 2
        tiles[2] = new Tile();
        tiles[2].image = loadTileImage("tiles/water.png");
        tiles[2].collision = true;

        // 3
        tiles[3] = new Tile();
        tiles[3].image = loadTileImage("tiles/earth.png");

        // 4
        tiles[4] = new Tile();
        tiles[4].image = loadTileImage("tiles/tree.png");
        tiles[4].collision = true;

        // 5
        tiles[5] = new Tile();
        tiles[5].image = loadTileImage("tiles/sand.png");
    }

    private BufferedImage loadTileImage(String resourcePath) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalStateException("Brak zasobu: " + resourcePath + " (sprawdź folder resources)");
            }
            return ImageIO.read(in);
        } catch (IOException e) {
            throw new RuntimeException("Nie udało się wczytać obrazka: " + resourcePath, e);
        }
    }

    private void setButtonTile(JButton button, int tileType) {
        BufferedImage img = tiles[tileType].image;
        Image scaled = img.getScaledInstance(TILE_SIZE, TILE_SIZE, Image.SCALE_FAST);
        button.setIcon(new ImageIcon(scaled));
    }

    private void resetMap() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                map[row][col] = 0;
                setButtonTile(buttons[row][col], 0);
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
            }

            JOptionPane.showMessageDialog(null, "Mapa zapisana jako:\n" + fullPath.toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Błąd zapisu mapy: " + e.getMessage());
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