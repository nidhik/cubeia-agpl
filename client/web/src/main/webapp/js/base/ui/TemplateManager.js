"use strict";
var Poker = Poker || {};
var globalTemplates = null;

Poker.TemplateManager = Class.extend({
    globalTemplates : null,
    init : function(preCacheTemplates) {
        if(globalTemplates==null) {
            globalTemplates = new Poker.Map();
        }

        if(preCacheTemplates && preCacheTemplates.length>0) {
            for(var i = 0; i<preCacheTemplates.length; i++) {
                this.getTemplate(preCacheTemplates[i]);
            }
        }

    },
    getTemplate : function(id) {
        if(globalTemplates.get(id)!=null) {
            return globalTemplates.get(id);
        } else {
            var el = $("#"+id);
            if(el.length==0) {
               throw "Template " + id + " not found";
            }
            var html = el.html();
            el.remove();
            if(html=="") {
                html =" ";
            }
            var template = Handlebars.compile(html);

            globalTemplates.put(id,template);
            return template;

        }
    },
    /**
     * Retrieves a "Render template" by a template id
     * that wraps the Mustache.render(template,data) call.
     *
     * Usage:
     * var rt = templateManager.getRenderTemplate("templateId");
     * var output = rt.render(jsonData);
     *
     * @param id
     * @return {Object}
     */
    getRenderTemplate : function(id) {
        var self = this;
        return { render : function(data) {
            return self.getTemplate(id)(data);
        }};
    },
    render : function(templateId,data) {
        return this.getTemplate(templateId)(data);
    }
});