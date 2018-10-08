/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package connect4;

import game.*;

public class MiniMaxConnect4Player extends BaseConnect4Player {
	public int depthLimit;

	// A Connect4Move with a scored (how well it evaluates)
	protected class ScoredConnect4Move extends Connect4Move {
		public ScoredConnect4Move(int c, double s) {
			super(c);
			score = s;
		}

		public void set(int c, double s) {
			col = c;
			score = s;
		}

		public double score;
	}

	public MiniMaxConnect4Player(String nname, int d) {
		super(nname);
		depthLimit = d;
	}

	/**
	 * Determines if a board represents a completed game. If it is, the
	 * evaluation values for these boards is recorded (i.e., 0 for a draw +X,
	 * for a HOME win and -X for an AWAY win.
	 * 
	 * @param brd
	 *            Connect4 board to be examined
	 * @param mv
	 *            where to place the score information; column is irrelevant
	 * @return true if the brd is a terminal state
	 */
	protected boolean terminalValue(GameState brd, ScoredConnect4Move mv) {
		GameState.Status status = brd.getStatus();
		boolean isTerminal = true;

		if (status == GameState.Status.HOME_WIN) {
			mv.set(0, MAX_SCORE);
		} else if (status == GameState.Status.AWAY_WIN) {
			mv.set(0, -MAX_SCORE);
		} else if (status == GameState.Status.DRAW) {
			mv.set(0, 0);
		} else {
			isTerminal = false;
		}
		return isTerminal;
	}

	/**
	 * Performs the a depth limited minimax algorithm. It leaves it's move
	 * recommendation at mvStack[currDepth].
	 * 
	 * @param brd
	 *            current board state
	 * @param currDepth
	 *            current depth in the search
	 */
	private ScoredConnect4Move minimax(Connect4State brd, int currDepth) {
		boolean toMaximize = (brd.getWho() == GameState.Who.HOME);
		ScoredConnect4Move theBestMove = new ScoredConnect4Move(0, 0);
		boolean isTerminal = terminalValue(brd, theBestMove);

		if (isTerminal) {
			;
		} else if (currDepth == depthLimit) {
			theBestMove.set(0, evalBoard(brd));
		} else {
			double bestScore = (brd.getWho() == GameState.Who.HOME ? Double.NEGATIVE_INFINITY
																	: Double.POSITIVE_INFINITY);
			theBestMove.score = bestScore;

			int[] columns = new int[COLS];
			for (int j = 0; j < COLS; j++) {
				columns[j] = j;
			}
			
			for (int i = 0; i < Connect4State.NUM_COLS; i++) {
				int c = columns[i];
				if (brd.numInCol[c] < Connect4State.NUM_ROWS) {
					Connect4State brdCopy = (Connect4State)brd.clone();
					brdCopy.makeMove(new Connect4Move(c));
					ScoredConnect4Move thisMove = minimax(brdCopy, currDepth + 1);

					if (toMaximize && thisMove.score > theBestMove.score) {
						theBestMove.set(c, thisMove.score);
					} else if (!toMaximize && thisMove.score < theBestMove.score) {
						theBestMove.set(c, thisMove.score);
					}
				}
			}
		}
		return theBestMove;
	}

	public GameMove getMove(GameState brd, String lastMove) {
		return minimax((Connect4State) brd, 0);
	}

	public static void main(String[] args) {
		int depth = 2;
		GamePlayer p = new MiniMaxConnect4Player("C4MMF1" + depth, depth);
		p.compete(args);
	}
}
