package com.cart.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cart.entity.TbUser;
import com.cart.service.UserService;

@Controller
public class IndexController {
	@Autowired
	UserService userService;

	@RequestMapping("/{index}")
	public String index(@PathVariable String index) {
		System.out.println(index);
		return index;
	}

}
