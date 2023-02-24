package com.zlucelia.game;

import com.zlucelia.game.policy.SelectPolicy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PokerGame implements Serializable {
    Map<String, Integer> map = new HashMap<>();
    public String PlayerA_Card;
    public String PlayerB_Card;
    public String Public_Card;
    public SelectPolicy PlayerA_Policy;
    public SelectPolicy PlayerB_Policy;

    public PokerGame(String playerA_Card, String playerB_Card, String public_Card) {
        map.put("J", 1);
        map.put("Q", 2);
        map.put("K", 3);
        map.put("KK", 9);
        map.put("QQ", 8);
        map.put("JJ", 7);
        map.put("KQ", 6);
        map.put("KJ", 5);
        map.put("QJ", 4);
        PlayerA_Card = playerA_Card;
        PlayerB_Card = playerB_Card;
        Public_Card = public_Card;
    }
    public void setPolicy(SelectPolicy playerA_Policy, SelectPolicy playerB_Policy) {
        playerA_Policy.setGame(this);
        playerA_Policy.setCard(PlayerA_Card);
        playerB_Policy.setGame(this);
        playerB_Policy.setCard(PlayerB_Card);
        PlayerA_Policy = playerA_Policy;
        PlayerB_Policy = playerB_Policy;
    }
    public int[] currentPlayerResult(State state) {
        int[] res = new int[3];
        if(state.getMove() == Move.FOLD){
            if(state.getPlayer() == Player.PLAYER1){
                res[0] = 1;
                res[1] = state.getPotB();
                res[2] = -1 * state.getPotB();
            } else {
                res[0] = 2;
                res[1] = -1 * state.getPotA();
                res[2] = state.getPotA();
            }
            return res;
        }
        if(state.getRound() == 3){
            String resA = map.get(PlayerA_Card) >= map.get(Public_Card) ? PlayerA_Card + "" + Public_Card :  Public_Card + "" + PlayerA_Card;
            String resB = map.get(PlayerB_Card) >= map.get(Public_Card) ? PlayerB_Card + "" + Public_Card :  Public_Card + "" + PlayerB_Card;

            if(map.get(resA) > map.get(resB)){
                res[0] = 1;
                res[1] = state.getPotB();
                res[2] = -1 * state.getPotB();
            } else if (map.get(resA) < map.get(resB)){
                res[0] = 2;
                res[1] = -1 * state.getPotA();
                res[2] = state.getPotA();
            } else {
                res[0] = 0;
                res[1] = 0;
                res[2] = 0;
            }
        } else {
            System.out.printf("wrong");
            return null;
        }
        return res;
    }

    public int getCFRUtility(State state) {
        if(state.getMove() == Move.FOLD){
            if(state.getPlayer() == Player.PLAYER1){
                return state.getPotB();
            } else {
                return state.getPotA();
            }
        }
        if(state.getRound() == 3){
            String resA = map.get(PlayerA_Card) >= map.get(Public_Card) ? PlayerA_Card + "" + Public_Card :  Public_Card + "" + PlayerA_Card;
            String resB = map.get(PlayerB_Card) >= map.get(Public_Card) ? PlayerB_Card + "" + Public_Card :  Public_Card + "" + PlayerB_Card;
            if(map.get(resA) > map.get(resB)){
                if(state.getPlayer() == Player.PLAYER1){
                    return state.getPotB();
                } else {
                    return -1 * state.getPotB();
                }
            } else if (map.get(resA) < map.get(resB)){
                if(state.getPlayer() == Player.PLAYER1){
                    return -1 * state.getPotA();
                } else {
                    return state.getPotA();
                }
            } else {
                return 0;
            }
        }
        return 0;
    }

    public GameNode selectNext(GameNode rootNode, String history) {
        Player player = rootNode.getPlayer();
        if(player == Player.PLAYER1){
            return PlayerA_Policy.select(rootNode, history);
        } else {
            return PlayerB_Policy.select(rootNode, history);
        }
    }
}
