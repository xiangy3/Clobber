package clobber;

import java.util.ArrayList;

import game.GameMove;
import game.GamePlayer;
import game.GameState;
import game.GameState.Who;

public class xiangy3 extends GamePlayer {

	public final int DEPTH = 4;

	public xiangy3(String nickname) {
		super(nickname, "Clobber");
	}

	public GameMove getMove(GameState state, String lastMove) {
		ClobberState board = (ClobberState) state;
		ArrayList<ClobberMove> list = new ArrayList<ClobberMove>();
		ClobberMove mv = new ClobberMove();
		for (int r = 0; r < ClobberState.ROWS; r++) {
			for (int c = 0; c < ClobberState.COLS; c++) {
				mv.row1 = r;
				mv.col1 = c;
				mv.row2 = r - 1;
				mv.col2 = c;
				if (board.moveOK(mv)) {
					list.add((ClobberMove) mv.clone());
				}
				mv.row2 = r + 1;
				mv.col2 = c;
				if (board.moveOK(mv)) {
					list.add((ClobberMove) mv.clone());
				}
				mv.row2 = r;
				mv.col2 = c - 1;
				if (board.moveOK(mv)) {
					list.add((ClobberMove) mv.clone());
				}
				mv.row2 = r;
				mv.col2 = c + 1;
				if (board.moveOK(mv)) {
					list.add((ClobberMove) mv.clone());
				}
			}
		}

		double n = Double.NEGATIVE_INFINITY;
		double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;
		double value;

		ClobberMove nextMove = null;

		for (ClobberMove move : list) {

			ClobberState bd = (ClobberState) board.clone();
			bd.makeMove(move);
			value = alphaBeta(bd, 1, alpha, beta, list);
			alpha = Math.max(alpha, value);
			if (value > n) {
				n = value;
				nextMove = move;
			}
		}
		return nextMove;
	}

	public double alphaBeta(ClobberState board, int depth, double alpha, double beta, ArrayList<ClobberMove> list) {
		boolean toMaximize = (board.getWho() == GameState.Who.HOME);
		boolean toMinimize = !toMaximize;

		double a = Double.NEGATIVE_INFINITY;
		double b = Double.POSITIVE_INFINITY;
		double value = Double.NEGATIVE_INFINITY;

		if (list.size() == 0) {
			return countDifferene(board) > 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
		} else if (depth == DEPTH) {
			return evaluateBoard(board);
		}
		for (ClobberMove move : list) {
			ClobberState bd = (ClobberState) board.clone();
			bd.makeMove(move);
			double moveValue = alphaBeta(bd, depth + 1, a, b, list);
			if (toMinimize) {
				value = Math.min(moveValue, value);
				beta = Math.min(moveValue, beta);
				if (moveValue <= alpha) {
					return value;
				}
			} else {
				value = Math.max(moveValue, value);
				alpha = Math.max(moveValue, alpha);

				if (moveValue >= beta) {
					return value;
				}
			}
		}
		return value;
	}

	public double atEdgeCornerValue(ClobberState board) {
		double value = 0;

		char home;
		char away;
		GameState.Who who = board.getWho();

		if (who == Who.AWAY) {
			away = ClobberState.awaySym;
			home = ClobberState.homeSym;
		} else {
			away = ClobberState.homeSym;
			home = ClobberState.awaySym;
		}

		for (int c = 0; c < ClobberState.COLS; c++) {
			if (c < 2 || c > 3) {
				if (board.board[0][c] == home)
					value++;
				else if (board.board[0][c] == away)
					value--;

				if (board.board[ClobberState.ROWS - 1][c] == home)
					value++;
				else if (board.board[ClobberState.ROWS - 1][c] == away)
					value--;
			}
		}

		for (int r = 1; r < ClobberState.ROWS - 1; r++) {
			if (r < 2 || r > 3) {
				if (board.board[r][0] == home)
					value++;
				else if (board.board[r][0] == away)
					value--;

				if (board.board[r][ClobberState.COLS - 1] == home)
					value++;
				else if (board.board[r][ClobberState.COLS - 1] == away)
					value--;
			}
		}

		return value;
	}
	public double countDifferene(ClobberState board) {
		double value = 0;

		char home;
		char away;
		GameState.Who who = board.getWho();

		if (who == Who.AWAY) {
			away = ClobberState.awaySym;
			home = ClobberState.homeSym;
		} else {
			away = ClobberState.homeSym;
			home = ClobberState.awaySym;
		}

		for (int r = 0; r < ClobberState.ROWS; r++) {
			for (int c = 0; c < ClobberState.COLS; c++) {
				if (board.board[r][c] == away) {
					value--;
				} else if (board.board[r][c] == home) {
					value++;
				}
			}
		}

		return value;
	}

	public double canTake(ClobberState board) {
		double valueOne = 0;
		double valueTwo = 0;
		char home;
		char away;
		GameState.Who who = board.getWho();
		if (who == Who.AWAY) {
			away = ClobberState.awaySym;
			home = ClobberState.homeSym;
		} else {
			away = ClobberState.homeSym;
			home = ClobberState.awaySym;
		}
		for (int i = 0; i < ClobberState.ROWS; i++) {
			for (int j = 0; j < ClobberState.COLS; j++) {
				if (board.board[i][j] == away) {
					valueOne += canTake(board, i, j);
				} else if (board.board[i][j] == home) {
					valueTwo += canTake(board, i, j);
				}
			}
		}
		return valueOne - valueTwo;
	}

	public double canTake(ClobberState board, int r, int c) {
		double score = 0;
		char thisPiece = board.board[r][c];
		char otherPiece;
		if (thisPiece == ClobberState.emptySym) {
			otherPiece = ClobberState.awaySym;
		} else {
			otherPiece = ClobberState.homeSym;
		}

		if (ClobberMove.posOK(r + 1, c) && board.board[r + 1][c] == otherPiece)
			score++;
		if (ClobberMove.posOK(r - 1, c) && board.board[r - 1][c] == otherPiece)
			score++;
		if (ClobberMove.posOK(r, c + 1) && board.board[r + 1][c + 1] == otherPiece)
			score++;
		if (ClobberMove.posOK(r, c - 1) && board.board[r + 1][c - 1] == otherPiece)
			score++;

		return score;
	}

	public double touchEachOther(ClobberState board) {
		double value = 0;

		char home;
		char away;
		GameState.Who who = board.getWho();

		if (who == Who.AWAY) {
			away = ClobberState.awaySym;
			home = ClobberState.homeSym;
		} else {
			away = ClobberState.homeSym;
			home = ClobberState.awaySym;
		}

		for (int r = 0; r < ClobberState.ROWS; r++) {
			for (int c = 0; c < ClobberState.COLS; c++) {
				if (board.board[r - 1][c] == home) {
					value++;
				}
				if (board.board[r + 1][c] == home) {
					value++;
				}
				if (board.board[r][c - 1] == home) {
					value++;
				}
				if (board.board[r][c + 1] == home) {
					value++;
				}
				if (board.board[r - 1][c] == away) {
					value--;
				}
				if (board.board[r + 1][c] == away) {
					value--;
				}
				if (board.board[r][c - 1] == away) {
					value--;
				}
				if (board.board[r][c + 1] == away) {
					value--;
				}
			}
		}
		return value;
	}

	public double evaluateBoard(ClobberState board) {
		double take = canTake(board);
		double edgeCornerValue = atEdgeCornerValue(board);
		double countDifferene = countDifferene(board);
		double touchValue = touchEachOther(board);
		return (take * 3 + edgeCornerValue * 3 + countDifferene + touchValue * 2);
	}

	public static void main(String[] args) {
		GamePlayer p = new xiangy3("xiangy3");
		p.compete(args);
	}

}
