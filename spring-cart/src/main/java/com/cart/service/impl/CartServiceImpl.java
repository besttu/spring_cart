package com.cart.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cart.common.Const;
import com.cart.common.Const.Cart;
import com.cart.common.JsonUtils;
import com.cart.dao.TbItemMapper;
import com.cart.entity.TbItem;
import com.cart.entity.TbItemExample;
import com.cart.service.CartService;
import com.cart.service.ItemService;
import com.cart.util.CookieUtils;

@Service
public class CartServiceImpl implements CartService {
	@Autowired
	TbItemMapper tbMapper;

	public List<TbItem> getAllItem() {
		// TODO Auto-generated method stub
		List<TbItem> selectByExample = tbMapper.selectByExample(null);

		System.out.println(selectByExample);
		return selectByExample;
	}

	/**
	 * 从cookie中获取所有购物车
	 * 
	 * @return
	 */
	public List<TbItem> getAllCartByCookie(HttpServletRequest request, HttpServletResponse response) {
		String cookieValue = CookieUtils.getCookieValue(request, Const.Cart.CART_COOKIES);
		List<TbItem> jsonToList = null;
		if (StringUtils.isNotBlank(cookieValue)) {
			jsonToList = JsonUtils.jsonToList(cookieValue, TbItem.class);
		} else {
			jsonToList = new ArrayList<TbItem>();
		}

		return jsonToList;
	}

	public void addCart(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		// 通过id获取商品
		TbItem item = tbMapper.selectByPrimaryKey(itemId);
		List<TbItem> allCartByCookie = this.getAllCartByCookie(request, response);
		// 获取库存
		Integer stock = item.getNum();
		// 判断是否是新添加的商品
		boolean flag = false;
		for (TbItem t : allCartByCookie) {
			if (t.getId().equals(itemId)) {
				System.out.println(t);
				// 超出库存最大数量
				if (stock < Math.addExact(t.getNum(), num)) {
					throw new RuntimeException("超出最大库存数量");
				}
				// 超出最大数量限制
				if (Const.Cart.CART_MAX_LIMIT < t.getNum() + num) {
					throw new RuntimeException("超出最大数量限制");
				}
				t.setNum(t.getNum() + num);
				// 1表示商品选中
				item.setStatus((byte) 1);
				flag = true;
				break;
			}
		}
		// 新添加的商品
		if (!flag) {
			item.setNum(num);
			// 1表示商品选中
			item.setStatus((byte) 1);
			allCartByCookie.add(item);
		}
		// 转化为json数据，并存储在cookie
		CookieUtils.setCookie(request, response, Const.Cart.CART_COOKIES, JsonUtils.objectToJson(allCartByCookie));
	}

	public void update(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		List<TbItem> allCartByCookie = this.getAllCartByCookie(request, response);
		TbItem item = tbMapper.selectByPrimaryKey(itemId);
		// 获取库存
		Integer stock = item.getNum();
		for (TbItem t : allCartByCookie) {
			if (t.getId().equals(itemId)) {
				// 超出库存最大数量
				if (stock < Math.addExact(t.getNum(), num)) {
					throw new RuntimeException("超出最大库存数量");
				}
				// 超出最大数量限制
				if (Const.Cart.CART_MAX_LIMIT < t.getNum() + num) {
					throw new RuntimeException("超出最大数量限制");
				}
				t.setNum(num);
				// 1表示商品选中
				item.setStatus((byte) 1);
				break;
			}
		}
		// 转化为json数据，并存储在cookie
		CookieUtils.setCookie(request, response, Const.Cart.CART_COOKIES, JsonUtils.objectToJson(allCartByCookie));
	}

	public List<TbItem> showCart(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		return this.getAllCartByCookie(request, response);
	}

	public void updateStatus(HttpServletRequest request, HttpServletResponse response, Long itemId, Boolean status) {
		// TODO Auto-generated method stub
		List<TbItem> allCartByCookie = this.getAllCartByCookie(request, response);
		for (TbItem t : allCartByCookie) {
			if (t.getId().equals(itemId)) {
				if (status) {
					t.setStatus((byte) 1);
				} else {
					t.setStatus((byte) 0);
				}
				break;
			}
		}
		// 转化为json数据，并存储在cookie
		CookieUtils.setCookie(request, response, Const.Cart.CART_COOKIES, JsonUtils.objectToJson(allCartByCookie));

	}

	public void deleteByItemId(HttpServletRequest request, HttpServletResponse response, Long itemId) {
		// TODO Auto-generated method stub
		List<TbItem> allCartByCookie = this.getAllCartByCookie(request, response);
		for (TbItem t : allCartByCookie) {
			if (t.getId().equals(itemId)) {
				allCartByCookie.remove(t);
				break;
			}
		}
		// 转化为json数据，并存储在cookie
		CookieUtils.setCookie(request, response, Const.Cart.CART_COOKIES, JsonUtils.objectToJson(allCartByCookie));
	}

}
