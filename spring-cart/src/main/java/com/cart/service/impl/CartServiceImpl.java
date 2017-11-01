package com.cart.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cart.common.Const;
import com.cart.common.Const.Cart;
import com.cart.common.JsonUtils;
import com.cart.dao.TbCartMapper;
import com.cart.dao.TbItemMapper;
import com.cart.entity.TbCart;
import com.cart.entity.TbCartExample;
import com.cart.entity.TbCartExample.Criteria;
import com.cart.entity.TbItem;
import com.cart.entity.TbItemExample;
import com.cart.service.CartService;
import com.cart.service.ItemService;
import com.cart.util.CookieUtils;

@Service
public class CartServiceImpl implements CartService {
	@Autowired
	TbItemMapper tbMapper;
	@Autowired
	TbCartMapper cartMapper;

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
			// 超出库存最大数量
			if (stock < num) {
				throw new RuntimeException("超出最大库存数量");
			}
			// 超出最大数量限制
			if (Const.Cart.CART_MAX_LIMIT < num) {
				throw new RuntimeException("超出最大数量限制");
			}
			item.setNum(num);
			// 1表示商品选中
			item.setStatus(Const.Cart.CHECKED);
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
				t.setUpdated(new Date());
				t.setStatus(Const.Cart.CHECKED);
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
					t.setStatus(Const.Cart.CHECKED);
				} else {
					t.setStatus(Const.Cart.UN_CHECKED);
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

	private TbCart getCartByItemIdAndUserId(Long itemId, Long userId) {
		TbCartExample example = new TbCartExample();
		Criteria c = example.createCriteria();
		c.andItemIdEqualTo(itemId);
		c.andUserIdEqualTo(userId);
		List<TbCart> selectByExample = cartMapper.selectByExample(example);
		if (selectByExample.isEmpty()) {
			return null;
		}
		return selectByExample.get(0);
	}

	/**
	 * 把数据存储到数据库
	 * 
	 * @param request
	 * @param response
	 * @param userId
	 */
	public void ItemConvertCart(HttpServletRequest request, HttpServletResponse response, Long userId) {
		List<TbItem> allCartByCookie = this.getAllCartByCookie(request, response);
		TbCart cart = new TbCart();
		Date date = new Date();

		for (TbItem t : allCartByCookie) {
			TbCart c = this.getCartByItemIdAndUserId(t.getId(), userId);
			if (c != null) {
				// 如果用户购物车表中用数据，在原有的数量上加上cookie中的数量
				c.setNum(t.getNum() + c.getNum());
				c.setUpdated(new Date());
				cartMapper.updateByPrimaryKey(c);
				continue;
			}
			cart.setItemId(t.getId());
			cart.setNum(t.getNum());
			cart.setItemPrice(t.getPrice());
			cart.setCreated(date);
			cart.setUpdated(date);
			cart.setItemImage(t.getImage());
			cart.setItemTitle(t.getTitle());
			cart.setUserId(userId);
			cartMapper.insert(cart);
		}

	}

	public void addCart(Long itemId, Long userId, Integer num) {
		// TODO Auto-generated method stub
		// 通过id获取商品
		TbCart cart = this.getCartByItemIdAndUserId(itemId, userId);
		Date date = new Date();
		TbItem t = tbMapper.selectByPrimaryKey(itemId);
		Integer stock = t.getNum();
		if (cart != null) {
			// 超出库存最大数量
			if (stock < Math.addExact(cart.getNum(), num)) {
				throw new RuntimeException("超出最大库存数量");
			}
			// 超出最大数量限制
			if (Const.Cart.CART_MAX_LIMIT < cart.getNum() + num) {
				throw new RuntimeException("超出最大数量限制");
			}

			// 购物表中有该信息，在原来的数量上+增加的数量
			cart.setNum(cart.getNum() + num);
			cart.setUpdated(date);
			cartMapper.updateByPrimaryKeySelective(cart);
			return;
		}

		// 超出库存最大数量
		if (stock < num) {
			throw new RuntimeException("超出最大库存数量");
		}
		// 超出最大数量限制
		if (Const.Cart.CART_MAX_LIMIT < num) {
			throw new RuntimeException("超出最大数量限制");
		}

		cart = new TbCart();
		cart.setItemId(t.getId());
		cart.setNum(num);
		cart.setItemPrice(t.getPrice());
		cart.setCreated(date);
		cart.setUpdated(date);
		cart.setItemImage(t.getImage());
		cart.setItemTitle(t.getTitle());
		cart.setUserId(userId);
		cartMapper.insert(cart);
	}

	public List<TbItem> showCart(Long userId) {
		// TODO Auto-generated method stub
		TbCartExample example = new TbCartExample();
		Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(userId);
		List<TbCart> selectByExample = cartMapper.selectByExample(example);
		TbItem item = null;
		List<TbItem> lists = new ArrayList<TbItem>();
		for (TbCart c : selectByExample) {
			item = new TbItem();
			item.setNum(c.getNum());
			item.setPrice(c.getItemPrice());
			item.setStatus(c.getChecked());
			item.setImage(c.getItemImage());
			item.setId(c.getItemId());
			item.setTitle(c.getItemTitle());
			lists.add(item);
		}
		return lists;

	}

	public void update(Long itemId, Long userId, Integer num) {
		// TODO Auto-generated method stub
		TbCart cart = this.getCartByItemIdAndUserId(itemId, userId);
		TbItem item = tbMapper.selectByPrimaryKey(itemId);
		Integer stock = item.getNum();
		cart.setUpdated(new Date());
		cart.setNum(num);
		if (stock < Math.addExact(cart.getNum(), num)) {
			throw new RuntimeException("超出最大库存数量");
		}
		// 超出最大数量限制
		if (Const.Cart.CART_MAX_LIMIT < cart.getNum() + num) {
			throw new RuntimeException("超出最大数量限制");
		}

		cartMapper.updateByPrimaryKeySelective(cart);
	}

	public void updateStatus(Long itemId, Long userId, Boolean status) {
		// TODO Auto-generated method stub
		TbCart cartByItemIdAndUserId = this.getCartByItemIdAndUserId(itemId, userId);
		if (status) {
			cartByItemIdAndUserId.setChecked(Const.Cart.CHECKED);
		} else {
			cartByItemIdAndUserId.setChecked(Const.Cart.UN_CHECKED);
		}
		cartByItemIdAndUserId.setUpdated(new Date());
		cartMapper.updateByPrimaryKeySelective(cartByItemIdAndUserId);

	}

	public void deleteByItemId(Long itemId, Long userId) {
		// TODO Auto-generated method stub
		TbCart cart = this.getCartByItemIdAndUserId(itemId, userId);
		cartMapper.deleteByPrimaryKey(cart.getId());
	}

}
