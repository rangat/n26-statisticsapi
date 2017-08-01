package com.rrt.n26.objects;

public class StatisticsResponse {
    private Double sum;
    private Double avg;
    private Double max;
    private Double min;
    private Long count;

    //Default in the event that there are no transactions
    public StatisticsResponse() {
        this.sum = 0d;
        this.avg = null;
        this.max = null;
        this.min = null;
        this.count = 0L;
    }

    public StatisticsResponse(Double sum, Double avg, Double max, Double min, Long count) {
        this.sum = sum;
        this.avg = avg;
        this.max = max;
        this.min = min;
        this.count = count;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public Double getAvg() {
        return avg;
    }

    public void setAvg(Double avg) {
        this.avg = avg;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatisticsResponse that = (StatisticsResponse) o;

        if (sum != null ? !sum.equals(that.sum) : that.sum != null) return false;
        if (avg != null ? !avg.equals(that.avg) : that.avg != null) return false;
        if (max != null ? !max.equals(that.max) : that.max != null) return false;
        if (min != null ? !min.equals(that.min) : that.min != null) return false;
        return count != null ? count.equals(that.count) : that.count == null;
    }

    @Override
    public int hashCode() {
        int result = sum != null ? sum.hashCode() : 0;
        result = 31 * result + (avg != null ? avg.hashCode() : 0);
        result = 31 * result + (max != null ? max.hashCode() : 0);
        result = 31 * result + (min != null ? min.hashCode() : 0);
        result = 31 * result + (count != null ? count.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StatisticsResponse{" +
                "sum=" + sum +
                ", avg=" + avg +
                ", max=" + max +
                ", min=" + min +
                ", count=" + count +
                '}';
    }
}
