package com.zlucelia.game;

import com.zlucelia.game.policy.CFRPolicy;
import com.zlucelia.game.policy.FixedPolicy;
import com.zlucelia.game.policy.RandomPolicy;
import com.zlucelia.game.policy.SelectPolicy;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameTest {
    public static void main(String[] args) throws Exception {
        evaluate("CFRPolicy", 10000, false);
    }

    /**
     *
     * @param PolicyA 玩家一使用的策略
     * @param hands  进行的局数
     * @param train  （仅仅对CFR有效）true采用训练方式，false采用读取模型方式
     * @throws Exception
     */
    public static void evaluate(String PolicyA , int hands, boolean train) throws Exception {
        List<String> list = Arrays.asList("J","J","Q","Q","K","K");
        int k = 0;
        int[] CountA = new int[3];
        int[] CountB = new int[3];
        int[] pota = new int[3];
        XYSeries series1 = new XYSeries("RandomPolicy");
        XYSeries series2 = new XYSeries("FixedPolicy");
        XYSeries series3 = new XYSeries("CFRPolicy");
        XYSeries series11 = new XYSeries("RandomPolicy");
        XYSeries series22 = new XYSeries("FixedPolicy");
        XYSeries series33 = new XYSeries("CFRPolicy");
        XYSeries[] series = new XYSeries[]{series1, series2, series3};
        XYSeries[] seriess = new XYSeries[]{series11, series22, series33};
        SelectPolicy[] policies = new SelectPolicy[3];
        SelectPolicy PlayerA_Policy;
        if(PolicyA == "CFRPolicy" && !train){
            FileInputStream in=new FileInputStream("policy_1668486791423");
            ObjectInputStream objread=new ObjectInputStream(in);
            PlayerA_Policy = (CFRPolicy)objread.readObject();
            objread.close();
            FileInputStream in2=new FileInputStream("policy_1668650503440");
            ObjectInputStream objread2 =new ObjectInputStream(in2);
            policies[2] = (CFRPolicy)objread2.readObject();
            objread2.close();
        } else {
            PlayerA_Policy = (SelectPolicy)Class.forName("com.zlucelia.game.policy."+ PolicyA).getConstructor(String.class).newInstance(list.get(0));
            policies[2] = new CFRPolicy(list.get(1));
        }
        policies[0] = new RandomPolicy(list.get(1));
        policies[1] = new FixedPolicy(list.get(1));
        while(k++ < hands){
            Collections.shuffle(list);
            int n = 0;
            for (SelectPolicy policy : policies) {
                GameNode rootNode = new GameNode(new State(Player.PLAYER1, 1, 1, 1, 0, null));
                PokerGame pokerGame = new PokerGame(list.get(0), list.get(1), list.get(2));
                pokerGame.setPolicy(PlayerA_Policy, policy);
                String history = "";
                while(!rootNode.getChildren().isEmpty()){
                    rootNode = pokerGame.selectNext(rootNode, history);
                    history = history + "" + rootNode.getNodeState().getMoveString();
                }
                int[] result = pokerGame.currentPlayerResult(rootNode.getNodeState());
                if(result[0] == 1){
                    CountA[n]++;
                } else if(result[0] == 2){
                    CountB[n]++;
                }
                pota[n] += result[1];
                series[n].add(k, pota[n]);
                seriess[n].add(k * 1.0, CountA[n] * 1.0 / k);
                n++;
            }
        }
        System.out.println("=============" + PolicyA);
        System.out.println("玩家A Versus RandomPolicy胜场:" + CountA[0]);
        System.out.println("玩家A Versus RandomPolicy败场:" + CountB[0]);
        System.out.println("玩家A Versus FixedPolicy胜场:" + CountA[1]);
        System.out.println("玩家A Versus FixedPolicy败场:" + CountB[1]);
        System.out.println("玩家A Versus CFRPolicy胜场:" + CountA[2]);
        System.out.println("玩家A Versus CFRPolicy败场:" + CountB[2]);
        System.out.println("玩家A筹码Versus RandomPolicy:" + pota[0]);
        System.out.println("玩家A筹码Versus FixedPolicy:" + pota[1]);
        System.out.println("玩家A筹码Versus CFRPolicy:" + pota[2]);
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        xySeriesCollection.addSeries(series1);
        xySeriesCollection.addSeries(series2);
        xySeriesCollection.addSeries(series3);
        XYSeriesCollection xySeriesCollection2 = new XYSeriesCollection();
        xySeriesCollection2.addSeries(series11);
        xySeriesCollection2.addSeries(series22);
        xySeriesCollection2.addSeries(series33);
        JFreeChart chart = ChartFactory.createXYLineChart("PlayerA Accumulated Assets" + "(" + PolicyA + ")", "Hands Played","Pot",xySeriesCollection, PlotOrientation.VERTICAL, true, true, false);
        JFreeChart chart2 = ChartFactory.createXYLineChart("Average Winning Rate" + "(" + PolicyA + ")", "Hands Played","Winning Rate",xySeriesCollection2, PlotOrientation.VERTICAL, true, true, false);
        ChartFrame frame1=new ChartFrame("Game",chart);
        ChartFrame frame2=new ChartFrame("Game",chart2);
        frame1.setVisible(true);
        frame1.setSize(600,600);
        frame2.setVisible(true);
        frame2.setSize(600,600);
    }

}
