"use strict";
var Poker = Poker || {};
Poker.ResourcePreloader = Class.extend({
    completionListener : null,
    init : function(contextPath, completionListener, browserNotSupportedListener,images,skin) {
        this.completionListener = completionListener;

        var loader = new PxLoader();
        var tableLoader = new PxLoader();
        if(images!=null && images.length > 0) {
            for(var i = 0; i<images.length; i++) {
                if(images[i]!="") {
                    if(images[i].indexOf(skin + "/images/cards")!=-1 || images[i].indexOf(skin + "/images/table")!=-1)   {
                        tableLoader.addImage(contextPath + "/skins" + images[i]);
                    } else {
                        loader.addImage(contextPath + "/skins" + images[i]);
                    }
                }
            }
        } else {
            this.onComplete();
        }
        loader.addCompletionListener(function(){
            tableLoader.start();
            self.onComplete();
        });
        var self = this;
        loader.addProgressListener(function(e) {
            self.onProgress(e.completedCount, e.totalCount);
        });
        loader.start();
    },
    onComplete : function() {
        $(".loading-progressbar").height(0).css({padding:0, margin:0});
        this.completionListener();
        Poker.AppCtx.getSoundRepository().loadSounds();
    },
    onProgress : function(completedCount, totalCount) {
        $(".loading-progressbar .progress").width((100*completedCount/totalCount) + "%");
    }
});