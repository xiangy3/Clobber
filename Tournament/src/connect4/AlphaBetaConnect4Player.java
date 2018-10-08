/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package connect4;
import connect4.MiniMaxConnect4Player.ScoredConnect4Move;
import game.*;


// AlphaBetaConnect4Player is identical to MiniMaxConnect4Player
// except for the search process, which uses alpha beta pruning.

public class AlphaBetaConnect4Player extends MiniMaxConnect4Player {
	public AlphaBetaConnect4Player(String nname, int d)
	{ super(nname, d); }

	/**
	 * Performs alpha beta pruning.
	 * @param brd
	 * @param currDepth
	 * @param alpha
	 * @param beta
	 */
	private ScoredConnect4Move alphaBeta(Connect4State brd, int currDepth,
										double alpha, double beta) {
		boolean toMaximize = (brd.getWho() == GameState.Who.HOME);
		boolean toMinimize = !toMaximize;
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
					ScoredConnect4Move thisMove = alphaBeta(brdCopy, currDepth + 1, alpha, beta);

					if (toMaximize && thisMove.score > theBestMove.score) {
						theBestMove.set(c, thisMove.score);
					} else if (!toMaximize && thisMove.score < theBestMove.score) {
						theBestMove.set(c, thisMove.score);
					}
					// Update alpha and beta. Perform pruning, if possible.
					if (toMinimize) {
						beta = Math.min(theBestMove.score, beta);
						if (theBestMove.score <= alpha) {
							return theBestMove;
						}
					} else {
						alpha = Math.max(theBestMove.score, alpha);
						if (theBestMove.score >= beta) {
							return theBestMove;
						}
					}
				}
			}
		}
		theBestMove.col=1;
		
		return theBestMove;
	}
	public GameMove getMove(GameState brd, String lastMove)
	{ 
		return alphaBeta((Connect4State)brd, 0, Double.NEGATIVE_INFINITY, 
										 Double.POSITIVE_INFINITY);
	}
	
	public static char [] toChars(String x)
	{
		char [] res = new char [x.length()];
		for (int i=0; i<x.length(); i++)
			res[i] = x.charAt(i);
		return res;
	}
	
	public static void main(String [] args)
	{
		int depth = 6;
		GamePlayer p = new AlphaBetaConnect4Player("C4 A-B F1 " + depth, depth);
		p.compete(args);

		//p.init();
		String brd = "...BRR."+
					 "....B.."+
					 "......."+
					 "......."+
					 "......."+
					 "......."+
					 "[HOME 4 GAME_ON]";
		
		Connect4State state = new Connect4State();
		state.parseMsgString(brd);
		GameMove mv = p.getMove(state, "");
		System.out.println("Original board");
		System.out.println(state.toString());
		System.out.println("Move: " + mv.toString());
		System.out.println("Board after move");
		state.makeMove(mv);
		System.out.println(state.toString());
		
	}
}
