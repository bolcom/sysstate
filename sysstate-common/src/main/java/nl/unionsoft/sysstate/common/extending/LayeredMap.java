package nl.unionsoft.sysstate.common.extending;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

public class LayeredMap<K, V> extends HashMap<K, V> {

    private static final long serialVersionUID = -5824968836281690310L;

    private final LayeredMap<K, V> parent;

    private final String layerName;

    public LayeredMap() {
        this(null, null);
    }

    public LayeredMap(LayeredMap<K, V> parent) {
        this(parent, null);
    }

    public LayeredMap(LayeredMap<K, V> parent, String layerName) {
        this.parent = parent;
        this.layerName = StringUtils.defaultIfEmpty(layerName,"undefined");
    }

    @Override
    public V get(Object key) {
        V result = super.get(key);
        if (result == null && parent != null) {
            result = parent.get(key);
        }
        return result;
    }

    public LayeredMap<K, V> getParent() {
        return parent;
    }

    public String getLayerName() {
        return layerName;
    }

}
