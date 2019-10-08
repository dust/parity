package com.paritytrading.parity.book;





import com.paritytrading.parity.util.Instrument;

import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

/**
 * 交易对的报价逻辑封装。
 * @author dust Sep 30, 2019
 *
 */
public class MarketFeed {

    /**
     * 所有交易对的聚合订单表。
     */
    private final Long2ObjectArrayMap<AggregateOrderBook> books;
    
    /**
     * 已提交的订单集合。以订单id为序。
     */
    private final Long2ObjectOpenHashMap<MultiSrcOrder> orders;
    
//    /**
//     * 订单变化事件监听器。向外传播事件。
//     */
//    private final MarketListener listener;
    
    public MarketFeed(/*MarketListener listener*/) {
        this.books = new Long2ObjectArrayMap<AggregateOrderBook>();
        this.orders = new Long2ObjectOpenHashMap<MultiSrcOrder>();
        
//        this.listener = listener;
    }
    
    public AggregateOrderBook open(Instrument instrument) {
        return books.computeIfAbsent(instrument.asLong(), (key) -> new AggregateOrderBook(key));
    }
    
    public MultiSrcOrder find(long orderId) {
        return orders.get(orderId);
    }
    
    /**
     * 增加一个新的多源订单项到order book.
     * @param instrument
     * @param orderId  用户订单id. 唯一
     * @param side
     * @param price
     * @param size
     * @param source
     */
    public void add(long instrument, long orderId, Side side, long price, long size, int source) {
     // 冪等性。是否已提交过。orderId必须全局唯一。
        if (orderId > 0 && orders.containsKey(orderId)) {
            return;
        }

        AggregateOrderBook book = books.get(instrument);
        // 无效交易对
        if (book == null) {
            return;
        }
        
        boolean bbo = book.add(side, price, size, source);
        if(orderId > 0) {
            MultiSrcOrder order = new MultiSrcOrder(book, side, price, size, source);
            orders.put(orderId, order);
        }
        
//        if(listener != null) {
//        listener.update(book, bbo);
//        }
    }
    
    public void modify(long instrument, long orderId, Side side, long price, long size, int source) {
        long newSize = Math.max(0, size);
        if(orderId > 0) {
            MultiSrcOrder order = orders.get(orderId);
            
            if(order == null) {
                return;
            }
            
            if(newSize == 0) {
                orders.remove(orderId);
            }
            else {
                order.setRemainingQuantity(newSize);
            }
        }
        
        AggregateOrderBook book = books.get(instrument);
        if (book == null) {
            return;
        }
        
        
        boolean bbo = book.update(side, price, newSize, source);
//      if(listener != null) {
//      listener.update(book, bbo);
//      }
        
    }
    
    
}
