package com.paritytrading.parity.book;

import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;

/**
 * an item in multi-source order book. 多源订单项。
 * @author dust Sep 30, 2019
 *
 */
public class MultiSrc {
    
    /**
     * 当前订单的价格。（冗余)
     */
    private final long price;
    /**
     * 多路来源及其size的集合。
     */
    private final Int2LongOpenHashMap srcSizeMap;
    
    MultiSrc(long price){
        this.price  = price;
        this.srcSizeMap = new Int2LongOpenHashMap();
    }
    
    
    long size() {
        return srcSizeMap.values().stream().mapToLong(Long::valueOf).sum();
    }
    
    /**
     * 增加一个增量到指定的来源。Adds an increment to value currently associated with a key.
     * @param quantity increment quantity.
     * @param source
     * @return the new value
     */
    long addTo(long quantity, int source) {
        return srcSizeMap.addTo(source, quantity);
    }
    
    /**
     * 更新指定来源的数量。
     * @param quantity
     * @param source
     * @return
     */
    long updateTo(long quantity, int source) {
        long oldSize = srcSizeMap.get(source);
        long newSize = oldSize + quantity;
        
        if(newSize > 0) {
            srcSizeMap.put(source, newSize);
        }
        else {
            srcSizeMap.remove(source);
        }
        return newSize;
    }

}
