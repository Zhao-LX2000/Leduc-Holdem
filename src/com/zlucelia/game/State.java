package com.zlucelia.game;

import java.util.ArrayList;

public class State {
    public Player player;
    public int round;
    int PotA;
    int PotB;
    int lastRaiseNum;
    Move move;

    public State(Player player, int round, int potA, int potB, int lastRaiseNum, Move move) {
        this.player = player;
        this.round = round;
        PotA = potA;
        PotB = potB;
        this.lastRaiseNum = lastRaiseNum;
        this.move = move;
    }

    public static ArrayList<Move> generateLegalMoves(GameNode node) {
        State state = node.getNodeState();
        GameNode parent = node.getParent();
        ArrayList<Move> list = new ArrayList<>();
        if(state.isPotAEqualPotB()){
            list.add(Move.RAISE);
            list.add(Move.CALL);
        } else if(state.getMove() == Move.RAISE){
            if(parent.getNodeState().getMove() == Move.RAISE){
                list.add(Move.CALL);
                list.add(Move.FOLD);
            } else {
                list.add(Move.CALL);
                list.add(Move.RAISE);
                list.add(Move.FOLD);
            }
        }
        return list;
    }

    public static State applyMove(Player player, State state, Move move) {
        switch (move){
            case CALL:{
                if(state.isPotAEqualPotB()){
                    if(player == Player.PLAYER1)
                        return new State(Player.PLAYER2, state.getRound(), state.getPotA(), state.getPotB(), state.getLastRaiseNum(), Move.CALL);
                    else return new State(Player.PLAYER1, state.getRound() + 1, state.getPotA(), state.getPotB(), 0, Move.CALL);
                } else {
                    State s;
                    if(state.getRound() == 1){
                        s = new State(Player.PLAYER1, state.getRound() + 1, state.getPotA(), state.getPotB(), 0, Move.CALL);
                    } else {
                        if(player == Player.PLAYER1){
                            s = new State(Player.PLAYER2, state.getRound() + 1, state.getPotA(), state.getPotB(), 0, Move.CALL);
                        } else {
                            s = new State(Player.PLAYER1, state.getRound() + 1, state.getPotA(), state.getPotB(), 0, Move.CALL);
                        }
                    }
                    s.callPot(player);
                    return s;
                }
            }
            case RAISE:{
                int raiseNum = state.getLastRaiseNum() + state.getRound() * 2;
                Player nextPlayer = player == Player.PLAYER1 ? Player.PLAYER2 : Player.PLAYER1;
                State s = new State(nextPlayer, state.getRound(), state.getPotA(), state.getPotB(), raiseNum, Move.RAISE);
                s.raisePot(player, raiseNum);
                return s;
            }

            case FOLD:{
                Player nextPlayer = player == Player.PLAYER1 ? Player.PLAYER2 : Player.PLAYER1;
                return new State(nextPlayer, state.getRound() + 1, state.getPotA(), state.getPotB(), state.getLastRaiseNum(), Move.FOLD);
            }
        }
        return null;
    }

    public int getLastRaiseNum() {
        return lastRaiseNum;
    }


    public Player getPlayer() {
        return player;
    }

    public int getRound() {
        return round;
    }


    public Move getMove() {
        return move;
    }

    public String getMoveString() {
        switch (move){
            case RAISE:return "r";
            case CALL:return "c";
            case FOLD:return "f";
            default:return "";
        }
    }


    public int getPotA() {
        return PotA;
    }


    public int getPotB() {
        return PotB;
    }


    public boolean isPotAEqualPotB(){
        return this.PotB == this.PotA;
    }

    public void callPot(Player player) {
        switch (player){
            case PLAYER1: PotA = PotB;break;
            case PLAYER2: PotB = PotA;break;
            default:break;
        }
    }

    public void raisePot(Player player, int raiseCount) {
        switch (player){
            case PLAYER1: PotA += raiseCount;break;
            case PLAYER2: PotB += raiseCount;break;
            default:break;
        }
    }

}
