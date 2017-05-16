package nl.unionsoft.sysstate.plugins.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.inject.Named;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.piccolo.io.IllegalCharException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.oxm.xmlbeans.XmlBeansMarshaller;

import nl.unionsoft.sysstate.common.dto.InstanceDto;
import nl.unionsoft.sysstate.common.dto.StateDto;
import nl.unionsoft.sysstate.common.enums.StateType;
import nl.unionsoft.sysstate.common.util.StateUtil;

@Named("xmlBeanStateResolver")
public abstract class XMLBeanStateResolverImpl extends HttpStateResolverImpl {
    private static final Logger LOG = LoggerFactory.getLogger(XMLBeanStateResolverImpl.class);

    @Autowired
    private XmlBeansMarshaller xmlBeansMarshaller;

    @Override
    public void handleEntity(final HttpEntity httpEntity, final  Map<String, String> configuration, final StateDto state,  InstanceDto instance) throws IOException {

        InputStream contentStream = null;
        try {
            if (httpEntity != null) {
                contentStream = httpEntity.getContent();
                final XmlObject xmlObject = (XmlObject) xmlBeansMarshaller.unmarshal(new StreamSource(contentStream));
                handleXmlObject(xmlObject, state, configuration, instance);
            }
        } catch (UnmarshallingFailureException e) {
            state.setState(StateType.ERROR);
            LOG.error("Unable to unmarshal object [" + e.getMessage()+ "]");
            state.setDescription("XML FAILURE");
            state.appendMessage(StateUtil.exceptionAsMessage(e));
        } catch (IllegalCharException e) {
            state.setState(StateType.ERROR);
            LOG.error("Caught IllegalCharException while unmarshalling object", e);
            state.setDescription("XML FAILURE");
            state.appendMessage(StateUtil.exceptionAsMessage(e));
        } catch (final IOException e) {
            LOG.error("Caught IOException", e);
            state.setState(StateType.ERROR);
            state.setDescription(e.getMessage());
        } finally {
            IOUtils.closeQuietly(contentStream);
        }
    }

    protected abstract void handleXmlObject(final XmlObject xmlObject, final StateDto state,  Map<String, String> configuration, final InstanceDto instance);

    public XmlBeansMarshaller getXmlBeansMarshaller() {
        return xmlBeansMarshaller;
    }

    public void setXmlBeansMarshaller(final XmlBeansMarshaller xmlBeansMarshaller) {
        this.xmlBeansMarshaller = xmlBeansMarshaller;
    }

}
