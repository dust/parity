package com.paritytrading.parity.book;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import it.unimi.dsi.fastutil.longs.Long2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.longs.LongComparators;
import it.unimi.dsi.fastutil.longs.LongSortedSet;

/**
 * An aggregate order book(Thread safety. 多源订单簿。它是线程安全的。
 * 
 * @author dust Sep 30, 2019
 *
 */
public class AggregateOrderBook /*implements IOrderBook*/ {

    private final long instrument;
    private final Long2ObjectRBTreeMap<MultiSrc> bids;
    private final Long2ObjectRBTreeMap<MultiSrc> asks;
    private final ReadWriteLock bidLock;
    private final ReadWriteLock askLock;

    AggregateOrderBook(long instrument) {
        this.instrument = instrument;

        this.bids = new Long2ObjectRBTreeMap<MultiSrc>(LongComparators.OPPOSITE_COMPARATOR);
        this.asks = new Long2ObjectRBTreeMap<MultiSrc>(LongComparators.NATURAL_COMPARATOR);

        this.bidLock = new ReentrantReadWriteLock();
        this.askLock = new ReentrantReadWriteLock();
    }

    /**
     * Get the instrument
     * 
     * @return
     */
    public long getInstrument() {
        return instrument;
    }

    /**
     * Get the best bid price.
     * 
     * @return
     */
    public long getBestBidPrice() {
        try {
            bidLock.readLock().tryLock();
            if (bids.isEmpty()) {
                return 0;
            }

            return bids.firstLongKey();
        } finally {
            bidLock.readLock().unlock();
        }
    }

    /**
     * Get the bid prices.
     * 
     * @return
     */
    public LongSortedSet getBidPrices() {
        try {
            bidLock.readLock().tryLock();
            return bids.keySet();
        } finally {
            bidLock.readLock().unlock();
        }
    }

    /**
     * Get a bid level size.
     * 
     * @param price
     * @return
     */
    public long getBidSize(long price) {
        try {
            bidLock.readLock().tryLock();
            return bids.get(price).size();
        } finally {
            bidLock.readLock().unlock();
        }
    }

    /**
     * Get the best ask price.
     * 
     * @return
     */
    public long getBestAskPrice() {
        try {
            askLock.readLock().tryLock();
            if (asks.isEmpty()) {
                return 0;
            }

            return asks.firstLongKey();
        } finally {
            askLock.readLock().unlock();
        }
    }

    /**
     * Get the ask prices;
     * 
     * @return
     */
    public long getBestAskPrices() {
        try {
            bidLock.readLock().tryLock();

            if (asks.isEmpty()) {
                return 0;
            }
            return asks.firstLongKey();
        } finally {
            askLock.readLock().unlock();
        }
    }

    /**
     * Get an ask level size.
     */
    public long getAskSize(long price) {
        try {
            askLock.readLock().tryLock();

            return asks.get(price).size();
        } finally {
            askLock.readLock().unlock();
        }
    }

    /**
     * Get the ask prices.
     */
    public LongSortedSet getAskPrices() {
        try {
            askLock.readLock().tryLock();
            return asks.keySet();
        } finally {
            askLock.readLock().unlock();
        }
    }

    /**
     * add order from someone source.
     * 
     * @param side
     * @param price
     * @param quantity
     * @param source
     * @return the best bid or offer has change?
     */
    boolean add(Side side, long price, long quantity, int source) {
        // bids or asks

        Lock lock = side == Side.BUY ? bidLock.writeLock() : askLock.writeLock();
        Long2ObjectRBTreeMap<MultiSrc> levels = getLevels(side);
        try {
            lock.tryLock();

            if (!levels.containsKey(price)) {
                MultiSrc multiSrc = new MultiSrc(price);
                multiSrc.addTo(quantity, source);
                levels.put(price, multiSrc);
            } else {
                levels.get(price).addTo(quantity, source);
            }

            return price == levels.firstLongKey();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 增量更新某个来源的订单数。
     * 
     * @param side
     * @param price
     * @param quantity
     * @param source
     * @return
     */
    boolean update(Side side, long price, long quantity, int source) {
        Lock lock = side == Side.BUY ? bidLock.writeLock() : askLock.writeLock();
        Long2ObjectRBTreeMap<MultiSrc> levels = getLevels(side);

        try {
            lock.tryLock();
            if (!levels.containsKey(price)) {
                MultiSrc multiSrc = new MultiSrc(price);
                multiSrc.addTo(quantity, source);
                levels.put(price, multiSrc);
            } else {
                long newSize = levels.get(price).updateTo(quantity, source);
                if (newSize <= 0) {
                    levels.remove(price);
                }
            }

            return price == levels.firstLongKey();
        } finally {
            lock.unlock();
        }
    }

    private Long2ObjectRBTreeMap<MultiSrc> getLevels(Side side) {
        return side == Side.BUY ? bids : asks;
    }

}
