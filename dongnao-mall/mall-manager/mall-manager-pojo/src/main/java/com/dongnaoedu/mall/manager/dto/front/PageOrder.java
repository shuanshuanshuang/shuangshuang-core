package com.dongnaoedu.mall.manager.dto.front;

import java.io.Serializable;
import java.util.List;

/**
 * @author allen
 */
public class PageOrder implements Serializable {
	private static final long serialVersionUID = 1L;

	private int total;

	private List<Order> data;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<Order> getData() {
		return data;
	}

	public void setData(List<Order> data) {
		this.data = data;
	}
}
