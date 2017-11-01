package com.cart.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cart.dao.TbUserMapper;
import com.cart.entity.TbUser;
import com.cart.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	TbUserMapper userMapper;

	public TbUser getUserbyId(Long id) {
		// TODO Auto-generated method stub
		return userMapper.selectByPrimaryKey(id);
	}

}
