package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.Map.Entry;



@Slf4j
public class SlopeOne {
    private Map<Integer, Map<Integer, Double>> diff = new HashMap<>();
    private Map<Integer, Map<Integer, Integer>> freq = new HashMap<>();
    private Map<Integer, HashMap<Integer, Double>> inputData;
    private Map<Integer, HashMap<Integer, Double>> outputData = new HashMap<>();

    private Set<Integer> items = new HashSet<>();

    public void calc(Map<Integer, Set<Integer>> likes) {
        inputData = prepareInputData(likes);
        buildDifferencesMatrix(inputData);
        predict(inputData);
    }

    public List<Integer> getRecommendations(Integer userId) {
        HashMap<Integer, Double> data = outputData.get(userId);
        List<Integer> result = new ArrayList<>();
        if (data == null) {
            return result;
        }
        for (Entry<Integer, Double> e : data.entrySet()) {
            if (e.getValue() > 0.0 && !inputData.get(userId).containsKey(e.getKey())) {
                result.add(e.getKey());
            }

        }
        return result;
    }

    private Map<Integer, HashMap<Integer, Double>> prepareInputData(Map<Integer, Set<Integer>> likes) {
        Map<Integer, HashMap<Integer, Double>> data = new HashMap<>();
        for (Entry<Integer, Set<Integer>> e : likes.entrySet()) {
            data.put(e.getKey(), new HashMap<>());
            for (Integer filmId : e.getValue()) {
                items.add(filmId);
                data.get(e.getKey()).put(filmId, 1.0);
            }
        }
        return data;
    }

    private void buildDifferencesMatrix(Map<Integer, HashMap<Integer, Double>> data) {
        for (HashMap<Integer, Double> user : data.values()) {
            for (Entry<Integer, Double> e : user.entrySet()) {
                if (!diff.containsKey(e.getKey())) {
                    diff.put(e.getKey(), new HashMap<Integer, Double>());
                    freq.put(e.getKey(), new HashMap<Integer, Integer>());
                }
                for (Entry<Integer, Double> e2 : user.entrySet()) {
                    int oldCount = 0;
                    if (freq.get(e.getKey()).containsKey(e2.getKey())) {
                        oldCount = freq.get(e.getKey()).get(e2.getKey()).intValue();
                    }
                    double oldDiff = 0.0;
                    if (diff.get(e.getKey()).containsKey(e2.getKey())) {
                        oldDiff = diff.get(e.getKey()).get(e2.getKey()).doubleValue();
                    }
                    double observedDiff = e.getValue() - e2.getValue();
                    freq.get(e.getKey()).put(e2.getKey(), oldCount + 1);
                    diff.get(e.getKey()).put(e2.getKey(), oldDiff + observedDiff);
                }
            }
        }
        for (Integer j : diff.keySet()) {
            for (Integer i : diff.get(j).keySet()) {
                double oldValue = diff.get(j).get(i).doubleValue();
                int count = freq.get(j).get(i).intValue();
                diff.get(j).put(i, oldValue / count);
            }
        }
    }

    private void predict(Map<Integer, HashMap<Integer, Double>> data) {
        HashMap<Integer, Double> uPred = new HashMap<Integer, Double>();
        HashMap<Integer, Integer> uFreq = new HashMap<Integer, Integer>();
        for (Integer j : diff.keySet()) {
            uFreq.put(j, 0);
            uPred.put(j, 0.0);
        }
        for (Entry<Integer, HashMap<Integer, Double>> e : data.entrySet()) {
            for (Integer j : e.getValue().keySet()) {
                for (Integer k : diff.keySet()) {
                    try {
                        double predictedValue = diff.get(k).get(j).doubleValue() + e.getValue().get(j).doubleValue();
                        double finalValue = predictedValue * freq.get(k).get(j).intValue();
                        uPred.put(k, uPred.get(k) + finalValue);
                        uFreq.put(k, uFreq.get(k) + freq.get(k).get(j).intValue());
                    } catch (NullPointerException e1) {
                        log.info(e1.getMessage());
                    }
                }
            }
            HashMap<Integer, Double> clean = new HashMap<Integer, Double>();
            for (Integer j : uPred.keySet()) {
                if (uFreq.get(j) > 0) {
                    clean.put(j, uPred.get(j).doubleValue() / uFreq.get(j).intValue());
                }
            }
            for (Integer j : items) {
                if (e.getValue().containsKey(j)) {
                    clean.put(j, e.getValue().get(j));
                } else if (!clean.containsKey(j)) {
                    clean.put(j, -1.0);
                }
            }
            outputData.put(e.getKey(), clean);
        }
    }


    private void print(HashMap<Integer, Double> hashMap) {
        NumberFormat formatter = new DecimalFormat("#0.000");
        for (Integer j : hashMap.keySet()) {
            System.out.println(" " + j + " --> " + formatter.format(hashMap.get(j).doubleValue()));
        }
    }
}
