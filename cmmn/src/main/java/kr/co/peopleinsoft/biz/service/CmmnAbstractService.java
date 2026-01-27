package kr.co.peopleinsoft.biz.service;

import jakarta.annotation.Resource;
import kr.co.peopleinsoft.biz.mapper.CmmnMapper;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;

public abstract class CmmnAbstractService extends EgovAbstractServiceImpl {
	@Resource(name = "cmmnMapper")
	protected CmmnMapper cmmnMapper;
}