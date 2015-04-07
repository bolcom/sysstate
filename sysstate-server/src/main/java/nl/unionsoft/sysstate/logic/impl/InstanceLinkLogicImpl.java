package nl.unionsoft.sysstate.logic.impl;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.stereotype.Service;

import nl.unionsoft.sysstate.common.dto.InstanceLinkDto;
import nl.unionsoft.sysstate.common.logic.InstanceLinkLogic;
import nl.unionsoft.sysstate.dao.InstanceLinkDao;

@Service("instanceLinkLogic")
public class InstanceLinkLogicImpl implements InstanceLinkLogic {

    @Inject
    @Named("instanceLinkDao")
    private InstanceLinkDao instanceLinkDao;

    @Override
    public void link(Long fromInstanceId, Long toInstanceId, String name) {
        instanceLinkDao.create(fromInstanceId, toInstanceId, name);
    }

    @Override
    public void unlink(Long fromInstanceId, Long toInstanceId, String name) {
        instanceLinkDao.delete(fromInstanceId, toInstanceId, name);
    }
    

}
