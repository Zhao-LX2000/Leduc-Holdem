package com.zlucelia.game.policy;

import com.zlucelia.game.Move;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class StrategyNode implements Serializable {
    public final Move[] ACTIONS;
    private String info_set;
    private Map<Move, Double> strategy_sum;
    private Map<Move, Double> regret_sum;

    public Map<Move, Double> avg_regret_sum;

    public StrategyNode(String info_set, Move[] ACTIONS) {
        this.ACTIONS = ACTIONS;
        this.info_set = info_set;
        strategy_sum = new HashMap<>();
        regret_sum = new HashMap<>();
        for (Move action : ACTIONS) {
            strategy_sum.put(action, 0.0);
            regret_sum.put(action, 0.0);
        }
    }
    public Map<Move, Double> get_strategy(double realization_weight){
        double normalizing_sum = 0;
        Map<Move, Double> strategy = new HashMap<>();

        for (Move action : ACTIONS) {
            strategy.put(action, regret_sum.get(action) > 0 ? regret_sum.get(action) : 0.0);
            normalizing_sum += strategy.get(action);
        }

        for (Move action : ACTIONS) {
            if (normalizing_sum > 0){
                strategy.put(action, strategy.get(action) / normalizing_sum);
            } else {
                strategy.put(action, 1.0 / ACTIONS.length);
            }
            strategy_sum.put(action, strategy_sum.get(action) + strategy.get(action) * realization_weight);  //一开始是1
        }
        return strategy;
    }

    public Map<Move, Double> get_avg_strategy(){
        double normalizing_sum = 0;
        Map<Move, Double> avg_strategy = new HashMap<>();
        for (Move action : ACTIONS) {
            avg_strategy.put(action,avg_strategy.getOrDefault(action, 0.0) + strategy_sum.get(action));
            normalizing_sum += avg_strategy.get(action);
        }

        for (Move action : ACTIONS) {
            if (normalizing_sum > 0){
                avg_strategy.put(action, avg_strategy.get(action) / normalizing_sum);
            } else {
                avg_strategy.put(action, 1.0 / ACTIONS.length);
            }
        }
        if (normalizing_sum <= 0){
            avg_strategy.put(Move.CHECK, 1.0);
        }
        return avg_strategy;
    }

    public void give_regrets(Map<Move, Double> regrets){
        for (Move action : ACTIONS) {
            regret_sum.put(action, regret_sum.get(action) + regrets.get(action));
        }
    }
    public String toString(){
        Map<Move, Double> avg_strategy = this.get_avg_strategy();
        String summary = "";
        for (Move move : avg_strategy.keySet()) {
            summary += String.format("-%s%s:[%f]-", info_set, move,avg_strategy.get(move));
        }
        return summary;
    }
}
