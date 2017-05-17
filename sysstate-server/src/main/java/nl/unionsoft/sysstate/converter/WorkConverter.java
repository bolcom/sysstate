package nl.unionsoft.sysstate.converter;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import nl.unionsoft.commons.converter.Converter;
import nl.unionsoft.sysstate.common.dto.WorkDto;
import nl.unionsoft.sysstate.domain.Work;
@Service("workConverter")
public class WorkConverter implements Converter<WorkDto, Work> {

    @Override
    public WorkDto convert(Work work) {
        if (work == null) {
            return null;
        }
        WorkDto dto = new WorkDto();
        dto.setInitialized(new DateTime(work.getInitialized()));
        dto.setNodeId(work.getNodeId());
        dto.setReference(work.getReference());
        dto.setState(work.getState());
        return dto;

    }

}
