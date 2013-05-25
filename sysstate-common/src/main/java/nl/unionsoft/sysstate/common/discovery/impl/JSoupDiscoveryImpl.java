package nl.unionsoft.sysstate.common.discovery.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;

import nl.unionsoft.sysstate.common.dto.InstanceDto;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSoupDiscoveryImpl extends HttpDiscoveryImpl {

    private static final Logger LOG = LoggerFactory.getLogger(JSoupDiscoveryImpl.class);

    @Override
    public Collection<? extends InstanceDto> handleEntity(HttpEntity httpEntity, Properties configuration) throws IOException {
        Collection<? extends InstanceDto> result = null;
        InputStream contentStream = null;
        try {
            if (httpEntity != null) {
                contentStream = httpEntity.getContent();
                final Document document = Jsoup.parse(contentStream, "UTF-8", configuration.getProperty(URL), Parser.xmlParser());
                result = handleJsoup(document, configuration);
            }
        } catch(final IllegalStateException e) {
            LOG.error("Caught IllegalStateException", e);

        } catch(final IOException e) {
            LOG.error("Caught IOException", e);
        } finally {
            IOUtils.closeQuietly(contentStream);
        }
        return result;
    }

    public Collection<? extends InstanceDto> handleJsoup(Document document, Properties configuration) {
        return null;
    }

}
