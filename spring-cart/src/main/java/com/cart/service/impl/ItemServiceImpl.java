package com.cart.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cart.dao.TbItemMapper;
import com.cart.entity.TbItem;
import com.cart.service.ItemService;
@Service
public class ItemServiceImpl implements ItemService {
	@Autowired
	TbItemMapper itemMapper;

	public TbItem getItem(Long itemId) {

		return itemMapper.selectByPrimaryKey(itemId);
	}
}
