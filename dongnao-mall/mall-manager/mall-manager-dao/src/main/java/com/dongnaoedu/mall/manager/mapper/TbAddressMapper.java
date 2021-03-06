package com.dongnaoedu.mall.manager.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dongnaoedu.mall.manager.pojo.TbAddress;
import com.dongnaoedu.mall.manager.pojo.TbAddressExample;

public interface TbAddressMapper {

	long countByExample(TbAddressExample example);

	int deleteByExample(TbAddressExample example);

	int deleteByPrimaryKey(Long addressId);

	int insert(TbAddress record);

	int insertSelective(TbAddress record);

	List<TbAddress> selectByExample(TbAddressExample example);

	TbAddress selectByPrimaryKey(Long addressId);

	int updateByExampleSelective(@Param("record") TbAddress record, @Param("example") TbAddressExample example);

	int updateByExample(@Param("record") TbAddress record, @Param("example") TbAddressExample example);

	int updateByPrimaryKeySelective(TbAddress record);

	int updateByPrimaryKey(TbAddress record);

}