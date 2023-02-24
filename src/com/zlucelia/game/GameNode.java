package com.zlucelia.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameNode {
    private Map<Move, GameNode> children = new HashMap<>();
    private GameNode parent = null;
    private State nodeState;

    public GameNode(State state) {
        this.nodeState = state;
        generateChildren();
    }

    public GameNode(State state, GameNode parent) {
        this.nodeState = state;
        this.parent = parent;
    }

    public void generateChildren() {
        if (nodeState.getMove() != Move.FOLD && nodeState.getRound() != 3) {
            ArrayList<Move> legalMoves = State.generateLegalMoves(this);
            for (Move move : legalMoves) {
                GameNode child = new GameNode(State.applyMove(nodeState.getPlayer(),nodeState, move), this);
                children.put(move, child);
                child.generateChildren();
            }
        }
    }

    public Map<Move, GameNode> getChildren() {
        return children;
    }

    public GameNode getParent() {
        return parent;
    }

    public State getNodeState() {
        return this.nodeState;
    }

    public Player getPlayer() {
        return nodeState.getPlayer();
    }

    public void printNode(int depth) {
        System.out.println("depth:" + depth);
        System.out.println("player:" + nodeState.getPlayer());
        System.out.println("round:" + nodeState.getRound());
        System.out.println("move:" + nodeState.getMove());
        if(nodeState.getMove() == Move.FOLD || nodeState.getRound() == 3){
            System.out.println("over");
        }
        System.out.println("---------------------------------");
        if(!children.isEmpty()){
            for (GameNode value : children.values()) {
                value.printNode(depth + 1);
            }
        }
    }
}
