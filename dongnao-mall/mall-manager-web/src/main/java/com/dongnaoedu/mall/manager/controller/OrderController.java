package com.dongnaoedu.mall.manager.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dongnaoedu.mall.common.pojo.DataTablesResult;
import com.dongnaoedu.mall.common.pojo.Result;
import com.dongnaoedu.mall.common.utils.ResultUtil;
import com.dongnaoedu.mall.manager.dto.OrderDetail;
import com.dongnaoedu.mall.manager.service.OrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author allen
 */
@RestController
@Api(description = "订单管理")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@RequestMapping(value = "/order/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取订单列表")
	public DataTablesResult getOrderList(int draw, int start, int length, @RequestParam("search[value]") String search,
			@RequestParam("order[0][column]") int orderCol, @RequestParam("order[0][dir]") String orderDir) {

		// 获取客户端需要排序的列
		String[] cols = { "checkbox", "order_id", "payment", "shipping_code", "user_id", "buyer_nick", "create_time",
				"update_time", "payment_time", "close_time", "end_time", "status" };
		String orderColumn = cols[orderCol];
		// 默认排序列
		if (orderColumn == null) {
			orderColumn = "create_time";
		}
		// 获取排序方式 默认为desc(asc)
		if (orderDir == null) {
			orderDir = "desc";
		}
		DataTablesResult result = orderService.getOrderList(draw, start, length, search, orderColumn, orderDir);
		return result;
	}

	@RequestMapping(value = "/order/count", method = RequestMethod.GET)
	@ApiOperation(value = "获取订单总数")
	public Result<Object> getOrderCount() {

		Long result = orderService.countOrder();
		return new ResultUtil<Object>().setData(result);
	}

	@RequestMapping(value = "/order/detail/{orderId}", method = RequestMethod.GET)
	@ApiOperation(value = "获取订单详情")
	public Result<Object> getOrderDetail(@PathVariable String orderId) {

		OrderDetail orderDetail = orderService.getOrderDetail(orderId);
		return new ResultUtil<Object>().setData(orderDetail);
	}

	@RequestMapping(value = "/order/remark", method = RequestMethod.POST)
	@ApiOperation(value = "订单备注")
	public Result<Object> remark(@RequestParam String orderId, @RequestParam(required = false) String message) {

		orderService.remark(orderId, message);
		return new ResultUtil<Object>().setData(null);
	}

	@RequestMapping(value = "/order/deliver", method = RequestMethod.POST)
	@ApiOperation(value = "订单发货")
	public Result<Object> deliver(@RequestParam String orderId, @RequestParam String shippingName,
			@RequestParam String shippingCode, @RequestParam BigDecimal postFee) {

		orderService.deliver(orderId, shippingName, shippingCode, postFee);
		return new ResultUtil<Object>().setData(null);
	}

	@RequestMapping(value = "/order/cancel/{orderId}", method = RequestMethod.GET)
	@ApiOperation(value = "订单取消")
	public Result<Object> cancelOrderByAdmin(@PathVariable String orderId) {

		orderService.cancelOrderByAdmin(orderId);
		return new ResultUtil<Object>().setData(null);
	}

	@RequestMapping(value = "/order/del/{ids}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除订单")
	public Result<Object> getUserInfo(@PathVariable String[] ids) {

		for (String id : ids) {
			orderService.deleteOrder(id);
		}
		return new ResultUtil<Object>().setData(null);
	}
}
