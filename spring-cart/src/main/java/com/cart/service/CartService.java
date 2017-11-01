package com.cart.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cart.entity.TbItem;

/**
 * 购物车服务
 * 
 * @author admin
 *
 */
public interface CartService {
	public List<TbItem> getAllItem();

	/**
	 * 从cookie中获取所有购物车
	 * 
	 * @return
	 */
	public List<TbItem> getAllCartByCookie(HttpServletRequest request, HttpServletResponse response);

	public void addCart(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response)
			throws Exception;

	public void update(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response);

	public List<TbItem> showCart(HttpServletRequest request, HttpServletResponse response);

	public void updateStatus(HttpServletRequest request, HttpServletResponse response,Long itemId, Boolean status);

	public void deleteByItemId(HttpServletRequest request, HttpServletResponse response, Long itemId);

}
