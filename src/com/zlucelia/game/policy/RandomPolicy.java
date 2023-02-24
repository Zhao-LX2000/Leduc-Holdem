package com.zlucelia.game.policy;

import com.zlucelia.game.GameNode;
import com.zlucelia.game.Move;
import com.zlucelia.game.PokerGame;

import java.util.Random;

public class RandomPolicy implements SelectPolicy{

    public Random random;

    public RandomPolicy(String card) {
        this.random = new Random();
    }

    @Override
    public GameNode select(GameNode node, String history) {
        Move[] moves = node.getChildren().keySet().toArray(new Move[node.getChildren().size()]);
        return node.getChildren().get(moves[random.nextInt(node.getChildren().size())]);
    }

    @Override
    public void setGame(PokerGame game) {

    }

    @Override
    public void setCard(String card) {

    }
}
