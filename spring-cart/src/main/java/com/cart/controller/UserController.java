package com.cart.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cart.common.Const;
import com.cart.common.ServerResponse;
import com.cart.entity.TbCart;
import com.cart.entity.TbItem;
import com.cart.entity.TbUser;
import com.cart.service.CartService;
import com.cart.service.UserService;
import com.cart.util.CookieUtils;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	UserService userService;
	@Autowired
	CartService cartService;

	/**
	 * 模拟登陆
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/login")
	@ResponseBody
	public ServerResponse login(HttpServletRequest request, HttpServletResponse response) {
		try {
			TbUser user = userService.getUserbyId(7L);
			request.getSession().setAttribute(Const.User.SESSION_USER, user);
			// 同步cookie中的数据

			// 首先把cookies转化为cart并储存到数据库
			cartService.ItemConvertCart(request, response, user.getId());
			// 然后 删除cookie
			CookieUtils.deleteCookie(request, response, Const.Cart.CART_COOKIES);
			return ServerResponse.createBySuccess();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ServerResponse.createByError();
		}
	}

	/**
	 * 模拟退出
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/logout")
	@ResponseBody
	public ServerResponse logout(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.getAttribute(Const.User.SESSION_USER) != null) {
			session.removeAttribute(Const.Cart.CART_COOKIES);
			return ServerResponse.createBySuccessMessage("退出成功");
		}
		return ServerResponse.createByErrorMessage("当前没有用户登陆");

	}

}
