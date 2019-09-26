package com.dongnaoedu.mall.manager.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.dongnaoedu.mall.common.exception.MallException;
import com.dongnaoedu.mall.common.pojo.DataTablesResult;
import com.dongnaoedu.mall.manager.mapper.TbBaseMapper;
import com.dongnaoedu.mall.manager.mapper.TbLogMapper;
import com.dongnaoedu.mall.manager.mapper.TbOrderItemMapper;
import com.dongnaoedu.mall.manager.mapper.TbShiroFilterMapper;
import com.dongnaoedu.mall.manager.pojo.TbBase;
import com.dongnaoedu.mall.manager.pojo.TbLog;
import com.dongnaoedu.mall.manager.pojo.TbLogExample;
import com.dongnaoedu.mall.manager.pojo.TbOrderItem;
import com.dongnaoedu.mall.manager.pojo.TbShiroFilter;
import com.dongnaoedu.mall.manager.pojo.TbShiroFilterExample;
import com.dongnaoedu.mall.manager.service.SystemService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * @author allen
 */
@Service("systemService")
public class SystemServiceImpl implements SystemService {

	@Autowired
	private TbShiroFilterMapper tbShiroFilterMapper;
	@Autowired
	private TbBaseMapper tbBaseMapper;
	@Autowired
	private TbLogMapper tbLogMapper;
	@Autowired
	private TbOrderItemMapper tbOrderItemMapper;

	@Value("${BASE_ID}")
	private String BASE_ID;

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<TbShiroFilter> getShiroFilter() {

		TbShiroFilterExample example = new TbShiroFilterExample();
		example.setOrderByClause("sort_order");
		List<TbShiroFilter> list = tbShiroFilterMapper.selectByExample(example);
		if (list == null) {
			throw new MallException("获取shiro过滤链失败");
		}
		return list;
	}

	@Override
	public Long countShiroFilter() {

		TbShiroFilterExample example = new TbShiroFilterExample();
		Long result = tbShiroFilterMapper.countByExample(example);
		if (result == null) {
			throw new MallException("获取shiro过滤链数目失败");
		}
		return result;
	}

	@Override
	@Transactional
	public int addShiroFilter(TbShiroFilter tbShiroFilter) {

		if (tbShiroFilterMapper.insert(tbShiroFilter) != 1) {
			throw new MallException("添加shiro过滤链失败");
		}
		return 1;
	}

	@Override
	@Transactional
	public int updateShiroFilter(TbShiroFilter tbShiroFilter) {

		if (tbShiroFilterMapper.updateByPrimaryKey(tbShiroFilter) != 1) {
			throw new MallException("更新shiro过滤链失败");
		}
		return 1;
	}

	@Override
	@Transactional
	public int deleteShiroFilter(int id) {

		if (tbShiroFilterMapper.deleteByPrimaryKey(id) != 1) {
			throw new MallException("删除shiro过滤链失败");
		}
		return 1;
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public TbBase getBase() {

		TbBase tbBase = tbBaseMapper.selectByPrimaryKey(Integer.valueOf(BASE_ID));
		if (tbBase == null) {
			throw new MallException("获取基础设置失败");
		}
		return tbBase;
	}

	@Override
	@Transactional
	public int updateBase(TbBase tbBase) {

		if (tbBaseMapper.updateByPrimaryKey(tbBase) != 1) {
			throw new MallException("更新基础设置失败");
		}
		return 1;
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public TbOrderItem getWeekHot() {

		List<TbOrderItem> list = tbOrderItemMapper.getWeekHot();
		if (list == null) {
			throw new MallException("获取热销商品数据失败");
		}
		if (list.size() == 0) {
			TbOrderItem tbOrderItem = new TbOrderItem();
			tbOrderItem.setTotal(0);
			tbOrderItem.setTitle("暂无数据");
			tbOrderItem.setPicPath("");
			return tbOrderItem;
		}
		return list.get(0);
	}

	@Override
	@Transactional
	public int addLog(TbLog tbLog) {

		if (tbLogMapper.insert(tbLog) != 1) {
			throw new MallException("保存日志失败");
		}
		return 1;
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DataTablesResult getLogList(int draw, int start, int length, String search, String orderCol,
			String orderDir) {

		DataTablesResult result = new DataTablesResult();
		// 分页
		PageHelper.startPage(start / length + 1, length);
		List<TbLog> list = tbLogMapper.selectByMulti("%" + search + "%", orderCol, orderDir);
		PageInfo<TbLog> pageInfo = new PageInfo<>(list);

		result.setRecordsFiltered((int) pageInfo.getTotal());
		result.setRecordsTotal(Math.toIntExact(countLog()));

		result.setDraw(draw);
		result.setData(list);
		return result;
	}

	@Override
	public Long countLog() {

		TbLogExample example = new TbLogExample();
		Long result = tbLogMapper.countByExample(example);
		if (result == null) {
			throw new MallException("获取日志数量失败");
		}
		return result;
	}

	@Override
	@Transactional
	public int deleteLog(int id) {

		if (tbLogMapper.deleteByPrimaryKey(id) != 1) {
			throw new MallException("删除日志失败");
		}
		return 1;
	}
}
