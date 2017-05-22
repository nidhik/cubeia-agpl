"use strict";
var Poker = Poker || {};
Poker.AccountPageManager = Class.extend({
    templateManager : null,
    menuItemTemplate : null,
    activeView : null,
    userPanel : null,
    userOverlay : null,
    buyCreditsView : null,
    editProfileView : null,
    currentBonus : null,
    init : function() {
        this.templateManager = Poker.AppCtx.getTemplateManager();
        this.menuItemTemplate = "menuItemTemplate";
        this.userPanel = $(".user-panel");
        this.userOverlay = $(".user-overlay-container");
        this.setupUserPanel();
        var self = this;
        var vm =  Poker.AppCtx.getViewManager();
        $("#refillButton").click(function(e){
            self.requestTopUp();
        });
        $("#editProfileButton").click(function(e){
            self.closeAccountOverlay();
            if(self.editProfileView==null) {
                var url = Poker.OperatorConfig.getProfilePageUrl();
                self.editProfileView = new Poker.ExternalPageView(
                    "editProfileView","Profile","C",self.addToken(url),function(){
                        vm.removeView(self.editProfileView);
                        self.editProfileView = null;
                    });
                self.editProfileView.fixedSizeView = true;
               vm.addView(self.editProfileView);

            }
            Poker.AppCtx.getViewManager().activateView(self.editProfileView);
        });
        $("#buyCreditsButton").click(function(e){
            self.closeAccountOverlay();
            if(self.buyCreditsView==null) {
                var url = Poker.OperatorConfig.getBuyCreditsUrl();
                if(url!=null && url=="internal") {
                     self.buyCreditsView = new Poker.CreditsView(function(){
                         Poker.AppCtx.getViewManager().removeView(self.buyCreditsView);
                         self.buyCreditsView = null;
                     });

                } else {
                    self.buyCreditsView = new Poker.ExternalPageView(
                        "buyCreditsView","Buy credits","C",self.addToken(url),
                        function(){
                            vm.removeView(self.buyCreditsView);
                            self.buyCreditsView = null;
                        });
                    self.buyCreditsView.fixedSizeView = true;
                }

                Poker.AppCtx.getViewManager().addView(self.buyCreditsView);

            }
            Poker.AppCtx.getViewManager().activateView(self.buyCreditsView);

        });

        $(".logout-link").click(function() {
            self.closeAccountOverlay();
            self.logout();
        });

        Poker.AppCtx.getProfileManager().addProfileChangeListener(
            /**
             * @param {Poker.MyProfile} profile
             */
            function(profile){
                $("#user_name").html(profile.name);
                if( profile.avatarUrl != null) {
                    $(".user-panel-avatar").addClass("user-panel-custom-avatar").css("backgroundImage","url('"+profile.avatarUrl+"')");
                    $("#accountAvatar").css("backgroundImage","url('"+profile.avatarUrl+"')");
                }
                if(profile.bonuses.length>0) {
                    self.onBonusInfo(profile);
                }
                if(profile && profile.level>0)  {
                    var totalLevelXp = profile.nextLevelXp - profile.thisLevelXp;
                    var progress = 100*(profile.xp - profile.thisLevelXp)/totalLevelXp;
                    $("#nextLevel").html(1+profile.level);
                    $("#userLevel").attr("class","").addClass("level").addClass("level-"+profile.level);
                    if(profile.nextLevelXp) {
                        $("#xpProgress").width(progress+"%");
                        $("#currentXp").html(profile.xp + " / " + profile.nextLevelXp);
                    }

                } else {
                    $("#xpContainer").hide();
                }

            });

    },
    onLogin : function(playerId,name) {
        var self = this;
        $(".username").html(name);
        $(".user-id").html(playerId);
        Poker.AppCtx.getProfileManager().loadMyPlayerProfile();

    },
    displayDefaultAvatar : function(playerId){
        $(".user-panel-avatar").addClass("avatar" + (playerId % 9));
    },
    logout : function() {
        $.ga._trackEvent("user_navigation", "clicked_logout");
        Poker.Utils.removeStoredUser();
        var logout_url = Poker.OperatorConfig.getLogoutUrl();
        if(!logout_url) {
            Poker.AppCtx.getCommunicationManager().setIgnoreNextForceLogout();
            Poker.AppCtx.getCommunicationManager().getConnector().logout(true);
            document.location.reload();
        } else {
            var dialogManager = Poker.AppCtx.getDialogManager();
            dialogManager.displayGenericDialog({
                container:  Poker.AppCtx.getViewManager().getActiveView().getViewElement(),
                header: i18n.t("account.logout"),
                message: i18n.t("account.logout-warning"),
                displayCancelButton: true
            }, function() {
                Poker.AppCtx.getPlayerApi().invalidateSession().always(function(){
                    Poker.AppCtx.getCommunicationManager().setIgnoreNextForceLogout();
                    Poker.AppCtx.getCommunicationManager().getConnector().logout(true);
                    document.location = logout_url;
                });

            });
        }
    },
    addToken : function(url) {
        return url + "?userSessionToken="+Poker.MyPlayer.sessionToken+"&playerId="+Poker.MyPlayer.id + "&skin=" + Poker.SkinConfiguration.name
            +"&operatorId=" + Poker.SkinConfiguration.operatorId + "&operatorAuthToken=" + Poker.MyPlayer.loginToken + "&r="+Math.random();
    },
    closeAccountOverlay : function() {
        this.userOverlay.hide();
        this.userPanel.removeClass("active");
    },
    openAccountOverlay : function() {
        this.userOverlay.show();
        this.userPanel.addClass("active");
        this.openAccountFrame();
    },
    setupUserPanel : function() {
        var self = this;
        this.userPanel.click(function(e){
            if(self.userOverlay.is(":visible")) {
                self.closeAccountOverlay();
                $(document).off("mouseup.account");
            } else {
                self.openAccountOverlay();
            }
            $(document).on("mouseup.account",function(e){
                if(self.userPanel.has(e.target).length === 0
                    && self.userOverlay.has(e.target).length === 0) {
                    self.closeAccountOverlay();
                    $(document).off("mouseup.account");
                }
            });
        });
    },
    createParametersFromCurrencies: function(currencies) {
        var parameters = "";
        for (var i = 0; i < currencies.length; i++) {
            var currency = currencies[i];
            parameters += "&" + currency.code + "=" + currency.name;
        }
        return parameters;
    },
    openAccountFrame : function() {
        $.ga._trackEvent("user_navigation", "open_account_frame");

        var url = Poker.OperatorConfig.getAccountInfoUrl();
        if(url!=null && (url=="" || url=="internal")) {
           this.displayInternalAccountPage();
        } else {
            $("#internalAccountContent").hide();
            var iframe = $("#accountIframe");
            var urlWithParams = this.addToken(url);
            // Add currency params so we can show currencies in the way specified by the operator.
            urlWithParams += this.createParametersFromCurrencies(Poker.OperatorConfig.getEnabledCurrencies());
            iframe.attr("src", urlWithParams);
        }

    },
    displayInternalAccountPage : function() {
        $("#internalAccountContent").show();
        $("#accountIframe").hide();
        Poker.AppCtx.getProfileManager().loadMyPlayerProfile();
    },
    /**
     * @param {Poker.MyProfile} profile
     */
    onBonusInfo : function(profile) {
        var self = this;
        var template = Poker.AppCtx.getTemplateManager().getRenderTemplate("balanceTemplate");
        var displayAccounts = [];
        $.each(profile.accounts,function(i,a){
            if(a.role=="MAIN" || (a.role == "BONUS" && a.amount &&  parseFloat(a.amount)>0)) {
                displayAccounts.push(a);
            }
        });
        $("#accountBalancesContainer").html(template.render({accounts : displayAccounts }));

        $("#topUpCurrencies").empty();

        $.each(profile.bonuses,function(i,bonus){
            var currencyName = Poker.Utils.translateCurrencyCode(bonus.currencyCode);
            $("#topUpCurrencies").append($("<div/>").attr("id","topUp"+bonus.currencyCode).html(currencyName).click(function(e){
                self.displayTopUpInfo(bonus);
            }));
        });
        var currentBonus = this.getCurrentBonus(profile.bonuses);

        this.displayTopUpInfo(currentBonus);
        if(profile.bonuses && profile.bonuses.length<2) {
            $("#topUpCurrencies").hide();
        } else {
            $("#topUpCurrencies").show();
        }
    },
    getCurrentBonus : function(bonuses) {
        var name = this.getCurrentBonusName();
        if(name==null) {
            return bonuses[0];
        } else {
            for(var i = 0; i<bonuses.length; i++) {
                var b = bonuses[i];
                if(b.name == name) {
                    return b;
                }
            }
            return bonuses[0];
        }

    },
    getCurrentBonusName : function(){
        if(this.currentBonus == null) {
            return null;
        }
        return this.currentBonus.name;
    },
    displayTopUpInfo : function(bonus) {

        this.currentBonus = bonus;
        $("#topUpCurrencies .active").removeClass("active");
        $("#topUp"+bonus.currencyCode).addClass("active");
        if(bonus.timeToNextCollect>0) {
            $("#coolDownProgress").show();
            $("#bonusCollectContainer .top-up-progress").show();
            $("#bonusCollectContainer .balance-too-high").hide();
            var fractionRemaining = 100-(100 * bonus.timeToNextCollect / bonus.coolDown);
            $("#coolDownProgress").width(fractionRemaining+"%");
            $("#refillButton").attr("class","").addClass("refill-unavailable");
            var time = new Date().getTime()+bonus.timeToNextCollect;
            $("#coolDownLabel").html(moment(time).fromNow());
        } else if(bonus.canCollect == true) {
            $("#bonusCollectContainer .top-up-progress").show();
            $("#bonusCollectContainer .balance-too-high").hide();
            $("#coolDownProgress").width("100%");
            $("#refillButton").attr("class","").addClass("refill-available");
            $("#coolDownLabel").html("Top up is available!");
        } else {
            $("#bonusCollectContainer .balance-too-high").show();
            $("#bonusCollectContainer .top-up-progress").hide();
            $("#bonusCoolDownTime").html((bonus.coolDown/3600000));
            $("#bonusBalanceLowerLimit").html(bonus.bonusBalanceLowerLimit);
        }
    },
    requestTopUp : function() {
        var self = this;
        Poker.AppCtx.getProfileManager().requestTopUp(this.currentBonus.name);
    }
});

