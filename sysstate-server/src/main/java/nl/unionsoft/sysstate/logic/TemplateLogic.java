package nl.unionsoft.sysstate.logic;

import java.util.Collection;

import nl.unionsoft.sysstate.domain.Template;

public interface TemplateLogic {
    public Template getTemplate(String templateId);

    public void createOrUpdate(Template template);

    public void delete(String templateId);

    public Collection<Template> getTemplates();

    public void restore(String templateId);

}
