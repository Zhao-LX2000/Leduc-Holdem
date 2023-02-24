package com.zlucelia.game.policy;

import com.zlucelia.game.GameNode;
import com.zlucelia.game.PokerGame;
import com.zlucelia.game.Move;

import java.util.Map;

public class FixedPolicy implements SelectPolicy{

    String card;

    public FixedPolicy(String card){
        this.card = card;
    }
    @Override
    public GameNode select(GameNode node, String history) {
        Map<Move, GameNode> children = node.getChildren();
        switch (card){
            case "K": {
                return children.getOrDefault(Move.RAISE,children.get(Move.CALL));
            }
            case "Q": {
                return children.get(Move.CALL);
            }
            case "J": {
                return children.getOrDefault(Move.FOLD,children.get(Move.CALL));
            }
            default:return null;
        }
    }

    @Override
    public void setGame(PokerGame game) {
    }

    @Override
    public void setCard(String card) {
        this.card = card;
    }


}
