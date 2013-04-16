package nl.unionsoft.sysstate.web.mvc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller()
public class CacheController {
    @RequestMapping(value = "/cache/index", method = RequestMethod.GET)
    public ModelAndView index() {
        final ModelAndView modelAndView = new ModelAndView("cache-manager");

        final List<String> cacheManagerNames = new ArrayList<String>();
        for (final CacheManager cacheManager : CacheManager.ALL_CACHE_MANAGERS) {
            cacheManagerNames.add(cacheManager.getName());
        }
        modelAndView.addObject("cacheManagerNames", cacheManagerNames);
        return modelAndView;
    }

    @RequestMapping(value = "/cache/{cacheManager}/index", method = RequestMethod.GET)
    public ModelAndView indexCacheManager(@PathVariable("cacheManager") final String cacheManager) {
        final ModelAndView modelAndView = new ModelAndView("cache-stats");

        final CacheManager manager = CacheManager.getCacheManager(cacheManager);

        final String[] cacheNames = manager.getCacheNames();
        final Map<String, Cache> cacheStats = new HashMap<String, Cache>();
        for (final String cacheName : cacheNames) {
            final Cache cache = manager.getCache(cacheName);
            cacheStats.put(cacheName, cache);
        }
        modelAndView.addObject("cacheStats", cacheStats);
        return modelAndView;
    }

    @RequestMapping(value = "/cache/{cacheManager}/clear", method = RequestMethod.POST)
    public ModelAndView clear(@PathVariable("cacheManager") final String cacheManager, @RequestParam("cacheNames") String[] cacheNames) {
        final CacheManager manager = CacheManager.getCacheManager(cacheManager);
        if (cacheNames != null) {
            for (final String cacheName : cacheNames) {
                final Cache cache = manager.getCache(cacheName);
                if (cache != null) {
                    cache.removeAll();
                }
            }
        }
        return new ModelAndView("redirect:/cache/" + manager.getName() + "/index.html");
    }
}
