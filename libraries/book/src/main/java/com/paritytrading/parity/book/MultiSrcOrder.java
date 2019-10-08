package com.paritytrading.parity.book;

public class MultiSrcOrder {
    
    /**
     * 用户订单持有所属订单表的引用。
     */
    private final AggregateOrderBook book;

    private final Side side;
    private final long price;
    /**
     * 订单源。
     */
    private final int source;

    /**
     * 用户订单的剩余数量。
     */
    private long remainingQuantity;

    /**
     * 多源用户订单（有唯一订单id）
     * @param book
     * @param side
     * @param price
     * @param size
     * @param source
     */
    MultiSrcOrder(AggregateOrderBook book, Side side, long price, long size, int source) {
        this.book = book;
        
        this.side = side;
        this.price = price;
        this.source = source;

        this.remainingQuantity = size;
    }
    
    public AggregateOrderBook getOrderBook() {
        return book;
    }
    
    public long getPrice() {
        return price;
    }
    
    public Side getSide() {
        return side;
    }
    
    public int getSource() {
        return source;
    }
    
    /**
     * Get the remaining quantity.
     *
     * @return the remaining quantity
     */
    public long getRemainingQuantity() {
        return remainingQuantity;
    }

    /**
     * 重设交易数量。比如修改订单数量。
     * @param remainingQuantity
     */
    void setRemainingQuantity(long remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    /**
     * 削减数量，比如部分成交。
     * @param quantity
     */
    void reduce(long quantity) {
        remainingQuantity -= quantity;
    }

}
