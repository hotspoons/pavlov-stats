package net.realact.pavlovstats.models.dtos;

import java.util.List;

public class RequestResponse<T> {
    private int offset;
    private int amount;
    private int resultCount;
    private Sort sort;
    private boolean ascending;
    private String q;
    private List<T> results;



    public enum Sort{
        KDA,
        DKA,
        ADK,
        KDR,
        LAST_PLAYED,
        GAMES,
        NAME
    }
    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}
