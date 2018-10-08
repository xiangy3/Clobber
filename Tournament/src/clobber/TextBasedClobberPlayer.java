/* Copyright (C) Mike Zmuda - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Mike Zmuda <zmudam@miamioh.edu>, 2010-2015
 */

package clobber;
import game.*;

import java.util.*;


public class TextBasedClobberPlayer extends GamePlayer {
	public Scanner input = new Scanner(System.in);
	
	public TextBasedClobberPlayer(String n) 
	{
		super(n, "Clobber");
	}
	private float evaluateState(ClobberState cs) {
		float homeScore = 0;
		float awayScore = 0;
		
		for (int row = 0; row < ClobberState.ROWS; row++) {
			for (int col = 0; col < ClobberState.COLS; col++) {
				
				// If the stone is a home symbol
				if (cs.board[row][col] == ClobberState.homeSym) {
					homeScore += evaluateStone(cs, row, col);
				}
				
				// If the stone is an away symbol
				else if (cs.board[row][col] == ClobberState.awaySym) {
					awayScore += evaluateStone(cs, row, col);
				}
			}
		}
		
		// Return the heuristic
		if (homeScore > awayScore) {
			return homeScore / awayScore;
		}
		else return -awayScore / homeScore;
	}
	
	private float evaluateStone(ClobberState cs, int row, int col) {
		float score = 1;
		char friend = cs.board[row][col];
		
		// Check if the symbol is empty
		if (friend == ClobberState.emptySym) {
			return score;
		}
		
		char opponent = (friend == ClobberState.homeSym) ? ClobberState.awaySym : ClobberState.homeSym;
		
		/** Check for opponent symbols that can take this stone **/
		if (ClobberMove.posOK(row + 1, col) && cs.board[row + 1][col] == opponent) score++;
		if (ClobberMove.posOK(row - 1, col) && cs.board[row - 1][col] == opponent) score++;
		if (ClobberMove.posOK(row, col + 1) && cs.board[row][col + 1] == opponent) score++;
		if (ClobberMove.posOK(row, col - 1) && cs.board[row][col - 1] == opponent) score++;
		
		/** Check for friend symbols that can take opponent stones **/
		if (ClobberMove.posOK(row + 1, col + 1) && cs.board[row + 1][col + 1] == friend)
			if (cs.board[row + 1][col] == opponent || cs.board[row][col + 1] == opponent) score++;
		if (ClobberMove.posOK(row + 1, col - 1) && cs.board[row + 1][col - 1] == friend)
			if (cs.board[row + 1][col] == opponent || cs.board[row][col - 1] == opponent) score++;
		if (ClobberMove.posOK(row - 1, col + 1) && cs.board[row - 1][col + 1] == friend)
			if (cs.board[row - 1][col] == opponent || cs.board[row][col + 1] == opponent) score++;
		if (ClobberMove.posOK(row - 1, col - 1) && cs.board[row - 1][col - 1] == friend)
			if (cs.board[row - 1][col] == opponent || cs.board[row][col - 1] == opponent) score++;
		
		// Return the total score
		return score;
	}
	public GameMove getMove(GameState state, String lastMove)
	{
		ClobberMove theMove = new ClobberMove();
		System.out.println("Current board:");
		System.out.println(state);
		while (true) {
			System.out.printf("Enter your move as row1 col1 row2 col2%n");
			System.out.printf("You are currently playing %s%n", state.who);
			String moveText = input.nextLine();
			theMove.parseMove(moveText);
			if (state.moveOK(theMove)) {
				break;
			} else {
				System.out.println("Illegal move, try again");
			}
		}
		return theMove;
	}
	public static void main(String [] args)
	{
		GamePlayer p = new TextBasedClobberPlayer("Text Clobber");
		p.compete(args, 1);
	}
}
