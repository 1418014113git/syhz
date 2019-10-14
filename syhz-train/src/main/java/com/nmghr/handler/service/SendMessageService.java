package com.nmghr.handler.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

@Service
public class SendMessageService {
	@Autowired
	private JmsMessagingTemplate jmsMessagingTemplate;

	public void sendMessage(List<Map<String, Object>> list, String queueName) {
		// 发送消息
		jmsMessagingTemplate.convertAndSend(queueName, JSON.toJSONString(list));
	}

	public void sendMessage(Map<String, Object> map, String queueName) {
		// 发送消息
		jmsMessagingTemplate.convertAndSend(queueName, JSON.toJSONString(map));
	}
}
