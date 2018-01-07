package com.tweeter.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Controller
@Api(tags="Ping")
@RequestMapping("/")
public class PingController {
	
	private static final Logger LOG = LoggerFactory.getLogger(PingController.class);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="ping" , method = RequestMethod.GET)
	@ApiOperation(value = "ping", notes = "pings the service")
	public ResponseEntity ping(){
		LOG.info("The System is up and received a ping request.............");
		return new ResponseEntity("System is up", HttpStatus.OK);
	}

	
}