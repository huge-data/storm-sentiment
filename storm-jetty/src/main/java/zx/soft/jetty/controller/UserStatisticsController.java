package zx.soft.jetty.controller;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import zx.soft.jetty.service.UserService;

/**
 * 用户统计数据
 * 
 * @author wanggang
 *
 */
@Controller
@RequestMapping("/users/{uid}")
public class UserStatisticsController {

	@Inject
	private UserService userService;

	@RequestMapping(value = "/gender", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody
	int queryGenderByUid(@PathVariable long uid, @RequestParam long mid) {
		return userService.queryGenderByUid(uid, mid);
	}

}
