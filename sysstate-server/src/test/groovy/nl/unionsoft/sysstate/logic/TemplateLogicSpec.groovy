package nl.unionsoft.sysstate.logic

import org.springframework.context.ApplicationContext;

import nl.unionsoft.sysstate.common.logic.TemplateLogic
import nl.unionsoft.sysstate.converter.TemplateConverter
import nl.unionsoft.sysstate.dao.TemplateDao
import nl.unionsoft.sysstate.logic.impl.TemplateLogicImpl
import spock.lang.Specification

class TemplateLogicSpec extends Specification {


    TemplateLogic templateLogic;

    def templateDao = Mock(TemplateDao)
    def applicationContext = Mock(ApplicationContext)

    void setup() {
        templateLogic = new TemplateLogicImpl(new TemplateConverter(), templateDao, applicationContext)
    }
    def "Can Has lol"() {
        
        throw new IllegalStateException("meh")
    }
}
