package nl.unionsoft.sysstate.plugins.http;

import nl.unionsoft.common.param.Param;

public class JSoupStateResolverConfig extends HttpStateResolverConfig{

    private String select;

    /**
     * @return the select
     */
    @Param(title = "Select")
    public String getSelect() {
        return select;
    }

    /**
     * @param select the select to set
     */
    public void setSelect(final String select) {
        this.select = select;
    }

}
