/*
 * Copyright 2014 Parity authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.paritytrading.parity.book;

/**
 * An order. 用户订单。
 */
public class Order {

    /**
     * 用户订单持有所属订单表的引用。
     */
    private final OrderBook book;

    private final Side side;
    private final long price;

    /**
     * 用户订单的剩余数量。
     */
    private long remainingQuantity;

    Order(OrderBook book, Side side, long price, long size) {
        this.book = book;

        this.side  = side;
        this.price = price;

        this.remainingQuantity = size;
    }

    /**
     * Get the order book.
     *
     * @return the order book
     */
    public OrderBook getOrderBook() {
        return book;
    }

    /**
     * Get the price.
     *
     * @return the price
     */
    public long getPrice() {
        return price;
    }

    /**
     * Get the side.
     *
     * @return the side
     */
    public Side getSide() {
        return side;
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
     * 重设交易数量。比如个性订单数量。
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
