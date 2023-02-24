package com.zlucelia.game.policy;

import com.zlucelia.game.*;

import java.io.*;
import java.util.*;

public class CFRPolicy implements SelectPolicy, Serializable {
    private final transient HashMap<Move, String> action_set = new HashMap<>();
    private Map<String, StrategyNode> nodeMap;
    public transient PokerGame pokerGame;
    public transient String playerCard;

    public CFRPolicy(String playerCard) throws IOException {
        action_set.put(Move.CALL, "c");
        action_set.put(Move.RAISE, "r");
        action_set.put(Move.FOLD, "f");
        nodeMap = new HashMap<>();
        List<String> list = Arrays.asList("J", "J", "Q", "Q", "K", "K");
        this.playerCard = playerCard;
        for (int i = 0; i < 10000; i++) {
            Collections.shuffle(list);
            this.pokerGame = new PokerGame(list.get(0), list.get(1), list.get(2));
            State state = new State(Player.PLAYER1, 1, 1, 1, 0, null);
            cfr(list, new GameNode(state), "", 1.0, 1.0);
            System.out.println(i + " train over");
        }
        File f=new File("policy_"  + new Date(System.currentTimeMillis()).getTime());
        FileOutputStream out=new FileOutputStream(f);
        ObjectOutputStream objwrite=new ObjectOutputStream(out);
        objwrite.writeObject(this);
        objwrite.flush();
        objwrite.close();
    }

    @Override
    public GameNode select(GameNode node, String history) {
        String keyCard1 =playerCard;
        String keyCard2 =( node.getNodeState().getRound() >= 2 ? pokerGame.Public_Card : "") ;
        String key = keyCard1 + "" + keyCard2 + history;
        StrategyNode strategyNode = nodeMap.get(key);
        GameNode next = null;
        double max = Double.MIN_VALUE;
        if( strategyNode.avg_regret_sum == null){
            strategyNode.avg_regret_sum = strategyNode.get_avg_strategy();
        }
        if(strategyNode.avg_regret_sum.get(Move.CHECK) != null){
            strategyNode.avg_regret_sum.remove(Move.CHECK);
            Map<Move, GameNode> children = node.getChildren();
            switch (playerCard){
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
        for (Move move : node.getChildren().keySet()) {
            Double aDouble = strategyNode.avg_regret_sum.get(move);
            if(aDouble > max){
                max = aDouble;
                next = node.getChildren().get(move);
            }
        }
        return next;
    }

    @Override
    public void setGame(PokerGame game) {
        pokerGame = game;
    }

    @Override
    public void setCard(String card) {
        playerCard = card;
    }

    private double cfr(List cards, GameNode curNode, String history, double p1, double p2 ){
        int cardIndex = curNode.getPlayer() == Player.PLAYER1 ? 0 : 1;

        if(curNode.getNodeState().getRound() == 3 || curNode.getNodeState().getMove() == Move.FOLD){
            return pokerGame.getCFRUtility(curNode.getNodeState());
        }
        String info_set;

        if(curNode.getNodeState().getRound() >= 2) {
            info_set  = cards.get(cardIndex) + "" + cards.get(2) + "" + history; //info_set为 卡牌数 + 历史步
        } else {
            info_set = cards.get(cardIndex) + "" + history;
        }

        StrategyNode node = nodeMap.get(info_set); //在map里找
        if (node == null){
            Move[] moves = curNode.getChildren().keySet().toArray(new Move[curNode.getChildren().size()]);
            node = new StrategyNode(info_set, moves);
            nodeMap.put(info_set, node);
        }

        double node_utility = 0;
        Map<Move, Double> utility = new HashMap<>();
        Map<Move, Double> regrets = new HashMap<>();
        Map<Move, Double> strategy = cardIndex == 0 ? node.get_strategy(p1) : node.get_strategy(p2);
        String new_action;
        for(Move action : curNode.getChildren().keySet()){
            new_action = action_set.get(action);

            if (cardIndex == 0){
                utility.put(action, -1 * cfr(cards, curNode.getChildren().get(action), history + new_action, p1 * strategy.get(action), p2));
            } else {
                utility.put(action, -1 * cfr(cards, curNode.getChildren().get(action),history + new_action, p1, p2 * strategy.get(action)));
            }
            node_utility += strategy.get(action) * utility.get(action);
        }
        for (Move action: curNode.getChildren().keySet()){
            regrets.put(action,utility.getOrDefault(action, 0.0) - node_utility);
        }

        node.give_regrets(regrets);

        return node_utility;
    }



}
