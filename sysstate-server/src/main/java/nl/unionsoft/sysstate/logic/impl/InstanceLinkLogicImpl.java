package nl.unionsoft.sysstate.logic.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

import nl.unionsoft.sysstate.common.logic.InstanceLinkLogic;
import nl.unionsoft.sysstate.dao.InstanceLinkDao;
import nl.unionsoft.sysstate.domain.InstanceLink;

import org.springframework.stereotype.Service;

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

    @Override
    @Transactional
    public void link(Long fromInstanceId, List<Long> toInstanceIds, String name) {

        for (Long toInstanceId : toInstanceIds) {
            link(fromInstanceId, toInstanceId, name);
        }

        for (InstanceLink instanceLink : instanceLinkDao.retrieve(fromInstanceId, name)) {
            Long toId = instanceLink.getTo().getId();
            if (!toInstanceIds.contains(toId)) {
                unlink(fromInstanceId, toId, name);
            }
        }

    }

}
