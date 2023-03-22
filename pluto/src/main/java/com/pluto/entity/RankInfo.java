package com.pluto.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RankInfo {
    //
    private String mAddress = "";
    //
    private int mRank = -1;
    //
    private int mScore = 0;
    //
    private List<RankData> mRankList = new ArrayList<>();

    /**
     *
     * @return
     */
    public String getAddress() {
        return mAddress;
    }

    /**
     *
     * @param address
     */
    public void setAddress(String address) {
        mAddress = address;
    }

    /**
     *
     * @return
     */
    public int getRank() {
        return mRank;
    }

    /**
     *
     * @return
     */
    public int getScore() {
        return mScore;
    }

    /**
     *
     * @return
     */
    public List<RankData> getRankList() {
        return mRankList;
    }

    /**
     *
     * @param rank
     * @param score
     * @param array
     */
    public void parseData(String address, int rank, int score, JSONArray array) {
        mAddress = address;
        mRank = rank;
        mScore = score;
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject item = (JSONObject) array.get(i);
                RankData data = new RankData();
                data.setAddress(item.getString("address"));
                data.setScore(item.getInt("score"));
                mRankList.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
