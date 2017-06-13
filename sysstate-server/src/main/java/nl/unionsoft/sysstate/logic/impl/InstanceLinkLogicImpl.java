package nl.unionsoft.sysstate.logic.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import nl.unionsoft.sysstate.common.dto.InstanceLinkDto;
import nl.unionsoft.sysstate.common.dto.InstanceLinkDto.Direction;
import nl.unionsoft.sysstate.common.logic.InstanceLinkLogic;
import nl.unionsoft.sysstate.converter.InstanceLinkConverter;
import nl.unionsoft.sysstate.dao.InstanceDao;
import nl.unionsoft.sysstate.dao.InstanceLinkDao;
import nl.unionsoft.sysstate.domain.InstanceLink;

@Service("instanceLinkLogic")
public class InstanceLinkLogicImpl implements InstanceLinkLogic {

    @Inject
    @Named("instanceLinkDao")
    private InstanceLinkDao instanceLinkDao;

    @Inject
    @Named("instanceDao")
    private InstanceDao instanceDao;

    @Inject
    private InstanceLinkConverter instanceLinkConverter;

    @Override
    public void link(Long fromInstanceId, Long toInstanceId, String name) {
        instanceLinkDao.create(fromInstanceId, toInstanceId, name);
    }

    @Override
    public void unlink(Long fromInstanceId, Long toInstanceId, String name) {
        instanceLinkDao.delete(fromInstanceId, toInstanceId, name);
    }

    @Override
    public void link(Long fromInstanceId, List<Long> toInstanceIds, String name) {
        for (Long toInstanceId : toInstanceIds) {
            link(fromInstanceId, toInstanceId, name);
        }

        for (InstanceLink instanceLink : instanceLinkDao.getOutgoingInstanceLinks(fromInstanceId, name)) {
            Long toId = instanceLink.getTo().getId();
            if (!toInstanceIds.contains(toId)) {
                unlink(fromInstanceId, toId, name);
            }
        }
    }

    @Override
    public List<InstanceLinkDto> getInstanceLinks(Long instanceId) {

        List<InstanceLinkDto> results = new ArrayList<>();
        //@formatter:off
        results.addAll(instanceLinkDao.getOutgoingInstanceLinks(instanceId).parallelStream()
                .map(iil -> instanceLinkConverter
                        .convert(iil, Direction.OUTGOING))
                .collect(Collectors.toList()));
        results.addAll(instanceLinkDao.getIncommingInstanceLinks(instanceId).parallelStream()
                .map(iil -> instanceLinkConverter
                        .convert(iil, Direction.INCOMMING))
                .collect(Collectors.toList()));
        //@formatter:on        
        return results;
    }

}
