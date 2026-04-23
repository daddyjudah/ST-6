package com.mycompany.app;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class ProgramTest {
  @Test
  void gameStartsWithEmptyBoard() {
    Game game = new Game();

    assertEquals(State.PLAYING, game.state);
    assertEquals('X', game.player1.symbol);
    assertEquals('O', game.player2.symbol);
    assertArrayEquals(new char[] {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}, game.board);
  }

  @Test
  void checkStateDetectsWinsAndDraw() {
    Game game = new Game();

    game.symbol = 'X';
    assertEquals(State.XWIN, game.checkState(new char[] {'X', 'X', 'X', ' ', ' ', ' ', ' ', ' ', ' '}));

    game.symbol = 'O';
    assertEquals(State.OWIN, game.checkState(new char[] {'O', ' ', ' ', 'O', ' ', ' ', 'O', ' ', ' '}));

    game.symbol = 'X';
    assertEquals(State.DRAW, game.checkState(new char[] {'X', 'O', 'X', 'X', 'O', 'O', 'O', 'X', 'X'}));
    assertEquals(State.PLAYING, game.checkState(new char[] {'X', 'O', ' ', ' ', 'O', ' ', ' ', 'X', ' '}));
  }

  @Test
  void generateMovesReturnsOnlyEmptyCells() {
    Game game = new Game();
    ArrayList<Integer> moves = new ArrayList<>();

    game.generateMoves(new char[] {'X', ' ', 'O', ' ', 'X', 'O', ' ', ' ', 'X'}, moves);

    assertEquals(4, moves.size());
    assertEquals(1, moves.get(0));
    assertEquals(3, moves.get(1));
    assertEquals(6, moves.get(2));
    assertEquals(7, moves.get(3));
  }

  @Test
  void evaluatePositionReturnsExpectedScores() {
    Game game = new Game();
    Player player = new Player();
    player.symbol = 'X';

    game.symbol = 'X';
    assertEquals(Game.INF, game.evaluatePosition(new char[] {'X', 'X', 'X', ' ', ' ', ' ', ' ', ' ', ' '}, player));

    game.symbol = 'O';
    assertEquals(-Game.INF, game.evaluatePosition(new char[] {'O', 'O', 'O', ' ', ' ', ' ', ' ', ' ', ' '}, player));

    game.symbol = 'X';
    assertEquals(0, game.evaluatePosition(new char[] {'X', 'O', 'X', 'X', 'O', 'O', 'O', 'X', 'X'}, player));
  }

  @Test
  void minimaxFindsImmediateWinningMove() {
    Game game = new Game();

    char[] board = {'O', 'O', ' ', 'X', 'X', ' ', ' ', ' ', ' '};
    int move = game.MiniMax(board, game.player2);

    assertEquals(3, move);
  }

  @Test
  void minAndMaxMoveHandleTerminalPositions() {
    Game game = new Game();

    game.symbol = 'O';
    assertEquals(Game.INF, game.MinMove(new char[] {'O', 'O', 'O', 'X', 'X', ' ', ' ', ' ', ' '}, game.player2));

    game.symbol = 'X';
    assertEquals(Game.INF, game.MaxMove(new char[] {'X', 'X', 'X', 'O', 'O', ' ', ' ', ' ', ' '}, game.player1));
  }

  @Test
  void panelBuildsGameAndCells() throws Exception {
    TicTacToePanel panel = new TicTacToePanel(new GridLayout(3, 3));
    Game game = extractGame(panel);
    TicTacToeCell[] cells = extractCells(panel);

    assertEquals('X', game.cplayer.symbol);
    assertEquals(0, cells[0].getNum());
    assertEquals(2, cells[8].getRow());
    assertEquals(2, cells[8].getCol());
  }

  @Test
  void cellStoresCoordinatesAndMarker() {
    TicTacToeCell cell = new TicTacToeCell(4, 1, 2);

    assertEquals(' ', cell.getMarker());
    cell.setMarker("X");

    assertEquals('X', cell.getMarker());
    assertEquals("X", cell.getText());
    assertFalse(cell.isEnabled());
    assertEquals(4, cell.getNum());
    assertEquals(2, cell.getRow());
    assertEquals(1, cell.getCol());
  }

  @Test
  void utilityPrintMethodsWriteToConsole() {
    PrintStream originalOut = System.out;
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    System.setOut(new PrintStream(output));

    try {
      Utility.print(new char[] {'X', 'O', 'X', ' ', ' ', ' ', ' ', ' ', ' '});
      Utility.print(new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9});
      ArrayList<Integer> moves = new ArrayList<>();
      moves.add(2);
      moves.add(5);
      Utility.print(moves);
    } finally {
      System.setOut(originalOut);
    }

    String text = output.toString();
    assertTrue(text.contains("X-O-X-"));
    assertTrue(text.contains("1-2-3-4-5-6-7-8-9-"));
    assertTrue(text.contains("2-5-"));
  }

  @Test
  void panelActionMarksHumanAndComputerMoves() throws Exception {
    TicTacToePanel panel = new TicTacToePanel(new GridLayout(3, 3));
    TicTacToeCell[] cells = extractCells(panel);
    Game game = extractGame(panel);

    panel.actionPerformed(new ActionEvent(cells[0], ActionEvent.ACTION_PERFORMED, "click"));

    assertEquals('X', cells[0].getMarker());
    assertEquals(State.PLAYING, game.state);
    int markedCells = 0;
    for (int i = 0; i < 9; i++) {
      if (cells[i].getMarker() != ' ') {
        markedCells++;
      }
    }
    assertEquals(2, markedCells);
    assertEquals('X', game.cplayer.symbol);
  }

  private static Game extractGame(TicTacToePanel panel) throws Exception {
    Field field = TicTacToePanel.class.getDeclaredField("game");
    field.setAccessible(true);
    return (Game) field.get(panel);
  }

  private static TicTacToeCell[] extractCells(TicTacToePanel panel) throws Exception {
    Field field = TicTacToePanel.class.getDeclaredField("cells");
    field.setAccessible(true);
    return (TicTacToeCell[]) field.get(panel);
  }
}
