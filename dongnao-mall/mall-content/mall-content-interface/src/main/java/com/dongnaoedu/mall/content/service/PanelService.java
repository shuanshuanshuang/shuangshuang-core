package com.dongnaoedu.mall.content.service;

import java.util.List;

import com.dongnaoedu.mall.common.pojo.ZTreeNode;
import com.dongnaoedu.mall.manager.pojo.TbPanel;

/**
 * @author allen
 */
public interface PanelService {

	/**
	 * 通过id获取板块
	 * 
	 * @param id
	 * @return
	 */
	TbPanel getTbPanelById(int id);

	/**
	 * 获取板块类目
	 * 
	 * @param position
	 * @param showAll
	 * @return
	 */
	List<ZTreeNode> getPanelList(int position, boolean showAll);

	/**
	 * 添加板块
	 * 
	 * @param tbPanel
	 * @return
	 */
	int addPanel(TbPanel tbPanel);

	/**
	 * 更新板块
	 * 
	 * @param tbPanel
	 * @return
	 */
	int updatePanel(TbPanel tbPanel);

	/**
	 * 删除板块
	 * 
	 * @param id
	 * @return
	 */
	int deletePanel(int id);
}
