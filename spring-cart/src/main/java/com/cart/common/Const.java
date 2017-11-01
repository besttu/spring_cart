package com.cart.common;

import java.util.Set;

import ch.qos.logback.classic.joran.action.InsertFromJNDIAction;

/**
 * 全局变量
 * 
 * @author admin
 *
 */
public class Const {

	public interface User {
		String SESSION_USER = "user";
	}

	public interface Cart {
		Byte CHECKED = 1;// 即购物车选中状态
		Byte UN_CHECKED = 0;// 购物车中未选中状态
		String CART_COOKIES = "CART_COOKIES";
		// 商品限制
		Integer CART_MAX_LIMIT = 200;
	}
}
