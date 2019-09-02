package com.nmghr.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.nmghr.basic.core.service.IBaseService;

@Component
public class LogEventListener implements ApplicationListener<ApplicationReadyEvent>, Ordered  {

	@Autowired
	protected IBaseService baseService;

	
	@Override
	public int getOrder() {
		return 100;
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent arg0) {
		new LogQueueThread(baseService).start();
	}

}
