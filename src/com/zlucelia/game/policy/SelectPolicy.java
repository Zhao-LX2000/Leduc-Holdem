package com.zlucelia.game.policy;

import com.zlucelia.game.GameNode;
import com.zlucelia.game.PokerGame;

public interface SelectPolicy {
    GameNode select(GameNode node, String history);
    void setGame(PokerGame game);
    void setCard(String card);
}
