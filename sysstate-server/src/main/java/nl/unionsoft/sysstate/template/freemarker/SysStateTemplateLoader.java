package nl.unionsoft.sysstate.template.freemarker;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.inject.Inject;

import nl.unionsoft.sysstate.common.dto.TemplateDto;
import nl.unionsoft.sysstate.logic.TemplateLogic;

import org.springframework.stereotype.Service;

import freemarker.cache.TemplateLoader;
@Service("sysStateTemplateLoader")
public class SysStateTemplateLoader implements TemplateLoader  {

    
    @Inject
    private TemplateLogic templateLogic;
    
    @Override
    public Object findTemplateSource(String name) throws IOException {
        return templateLogic.getTemplate(name);
    }

    @Override
    public long getLastModified(Object templateSource) {
        return -1;
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
         TemplateDto template = (TemplateDto) templateSource;
         return new StringReader(template.getContent());
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
        
    }

}
