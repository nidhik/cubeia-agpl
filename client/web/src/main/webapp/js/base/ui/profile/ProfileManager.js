"use strict";
var Poker = Poker || {};

Poker.ProfileManager = Class.extend({
    playerApi : null,
    /**
     * @type {Poker.MyProfile}
     */
    myPlayerProfile : null,

    listeners : null,
    init : function() {
        this.playerApi = Poker.AppCtx.getPlayerApi();
        this.myPlayerProfile = new Poker.MyProfile();
        this.listeners = [];
    },
    addProfileChangeListener : function(listener){
        this.listeners.push(listener);
    },
    removeProfileChangeListener : function(listener) {
        var index = this.listeners.indexOf(listener);
        this.listeners.splice(index,1);
    },
    notifyListeners : function() {
        for(var i =0; i<this.listeners.length; i++) {
            this.listeners[i](this.myPlayerProfile);
        }
    },
    requestTopUp : function(name) {
        var self = this;
        Poker.AppCtx.getPlayerApi().requestTopUp(name,Poker.MyPlayer.sessionToken,
            function(profile){
                self.handleBonusInfo(profile);
            },
            function(){
                console.log("Error when requesting top-up");
            }
        );
    },
    updateXp : function(xp,level) {

        var before = this.calculateProgress();
        this.myPlayerProfile.xp = xp;
        if(this.myPlayerProfile.level != level) {
            return;
        }
        var after = this.calculateProgress();

        if(after-before>5) {
            var am = new Poker.AnimationManager();
            var container = $(".xp-progress-notification");
            var bar = container.find(".bar");
            bar.width(before+"%");
            container.show();
            var hideXp  = new Poker.CSSClassAnimation(container)
                .addClass("hide-xp").addCallback(function(){
                    setTimeout(function(){
                        container.hide().removeClass("hide-xp").removeClass("show-xp");
                    },1000);
                });


            var progressXp =  new Poker.CSSAttributeAnimation(bar)
                .addAttribute("width",after+"%")
                .addCallback(function(){
                    hideXp.start(am);
                });

            var showXp = new Poker.CSSClassAnimation(container)
                .addClass("show-xp")
                .addCallback(function(){
                    progressXp.start(am);
                });
            showXp.start(am);
        }

    },
    updateLevel : function(level,xp) {
        this.myPlayerProfile.level = level;
        this.myPlayerProfile.xp = xp;
        this.notifyListeners();
    },
    calculateProgress : function() {
        var profile = this.myPlayerProfile;
        var totalLevelXp = profile.nextLevelXp - profile.thisLevelXp;
        var progress = 100*(profile.xp - profile.thisLevelXp)/totalLevelXp;
        return progress;
    },
    loadMyPlayerProfile : function() {
        var self = this;
        var token = Poker.MyPlayer.sessionToken;
        this.playerApi.requestPlayerProfile(Poker.MyPlayer.id,token,
            function(profile){
                self.handleProfile(profile);
            },
            function(){
                console.log("unable to fetch player profile");
            });

        this.playerApi.requestBonusInfo(token,
            function(bonusInfo){
                self.handleBonusInfo(bonusInfo);;
            },
            function(){
                console.log("unable to fetch player bonus info");
        });
        this.playerApi.requestExperienceInfo(token,
            function(experienceInfo){
                self.handleExperienceInfo(experienceInfo);
            },function(){
                console.log("unable to fetch XP info");
            });

    },
    handleExperienceInfo : function(experienceInfo) {
        if(!experienceInfo) {
            return;
        }
        //{"player":"505","xp":1,"level":1,"thisLevelXp":0,"nextLevelXp":20}
        this.myPlayerProfile.xp = experienceInfo.xp;
        this.myPlayerProfile.level = experienceInfo.level;
        this.myPlayerProfile.nextLevelXp = experienceInfo.nextLevelXp;
        this.myPlayerProfile.thisLevelXp = experienceInfo.thisLevelXp;
    },
    handleProfile : function(profile) {
        this.myPlayerProfile.name = this.extractName(profile);
        this.myPlayerProfile.avatarUrl = profile.externalAvatarUrl;
        this.myPlayerProfile.level = profile.level;
        this.notifyListeners();
    },
    handleBonusInfo : function(bonusInfo) {
        var self = this;
        $.each(bonusInfo.accounts,function(i,a){
            if(Poker.OperatorConfig.isCurrencyEnabled(a.currency)) {

                self.myPlayerProfile.addBalance(a.balance, a.currency, a.role);

            }
        });
        $.each(bonusInfo.bonuses,function(i,b){
            self.myPlayerProfile.addBonus(b.timeToNextCollect, b.coolDown, b.canCollect, b.bonusBalanceLowerLimit, b.bonusName, b.currencyCode);
        });
        this.notifyListeners();
    },
    extractName : function(profile) {
        var name = "";
        if(typeof(profile.screenname)!="undefined") {
            name = profile.screenname;
        } else if(typeof(profile.externalUsername)!="undefined") {
            name = profile.externalUsername;
        } else if(typeof(profile.username)!="undefined") {
            name = profile.username;
        }
        return name;
    }

});