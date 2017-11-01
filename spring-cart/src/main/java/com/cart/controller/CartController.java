package com.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.druid.support.json.JSONUtils;
import com.cart.common.Const;
import com.cart.common.JsonUtils;
import com.cart.entity.TbItem;
import com.cart.entity.TbUser;
import com.cart.service.CartService;
import com.cart.service.ItemService;
import com.cart.service.UserService;
import com.cart.util.CookieUtils;

@Controller
@RequestMapping("/cart")
public class CartController {
	@Autowired
	CartService cartService;

	/**
	 * 添加购物车
	 * 
	 * @param itemId
	 * @param num
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/add/{itemId}")
	public String addCart(@PathVariable Long itemId, @RequestParam(defaultValue = "1") Integer num,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			Long userId = this.getStatus(request);
			if (userId != null) {
				// 登陆状态下操作数据库
				cartService.addCart(itemId, userId, num);
			} else {
				// 非登录状态下操作cookie
				cartService.addCart(itemId, num, request, response);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// 可能会超出库存，或者其他原因
			e.printStackTrace();
			System.out.println(e.getMessage());
			return "cartFail";
		}
		return "cartSuccess";
	}

	/**
	 * 修改购物车数量
	 * 
	 * @param itemId
	 * @param num
	 * @param request
	 * @param response
	 */
	@RequestMapping("/update/{itemId}/{num}")
	public void update(@PathVariable Long itemId, @PathVariable Integer num, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			Long userId = this.getStatus(request);
			if (userId != null) {
				// 登陆状态下操作数据库
				cartService.update(itemId, userId, num);
			} else {
				// 非登录状态下操作cookie
				cartService.update(itemId, num, request, response);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	/**
	 * 展示所选购物车
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping("/cart")
	public String showCart(HttpServletRequest request, HttpServletResponse response, Model model) {
		Long userId = this.getStatus(request);
		List<TbItem> items = null;
		try {
			if (userId != null) {
				items = cartService.showCart(userId);
			} else {
				items = cartService.showCart(request, response);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}

		model.addAttribute("cartList", items);
		return "cart";
	}

	/**
	 * 修改购物车的选中状态
	 * 
	 * @param request
	 * @param response
	 * @param itemId
	 * @param status
	 */
	@RequestMapping("/updateStatus/{itemId}/{status}")
	public void updateStatus(HttpServletRequest request, HttpServletResponse response, @PathVariable Long itemId,
			@PathVariable Boolean status) {
		Long userId = this.getStatus(request);
		try {
			if (userId != null) {
				// 登陆状态下操作数据库
				cartService.updateStatus(itemId, userId, status);
			} else {
				// 非登录状态下操作cookie
				cartService.updateStatus(request, response, itemId, status);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * 通过id删除购物车
	 * 
	 * @param request
	 * @param response
	 * @param itemId
	 */
	@RequestMapping("/delete/{itemId}")
	public void deleteByItemId(HttpServletRequest request, HttpServletResponse response, @PathVariable Long itemId) {
		Long userId = this.getStatus(request);
		if (userId != null) {
			// 登陆状态下操作数据库
			cartService.deleteByItemId(itemId, userId);
		} else {
			// 非登录状态下操作cookie
			cartService.deleteByItemId(request, response, itemId);
		}

	}

	/**
	 * 判断用户是否登陆
	 * 
	 * @param request
	 * @return
	 */
	private Long getStatus(HttpServletRequest request) {
		HttpSession session = request.getSession();
		TbUser user = (TbUser) session.getAttribute(Const.User.SESSION_USER);
		if (session.getAttribute(Const.User.SESSION_USER) != null) {
			return user.getId();
		} else {
			return null;
		}

	}

}
