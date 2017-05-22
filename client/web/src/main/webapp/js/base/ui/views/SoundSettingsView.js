"use strict";
var Poker = Poker || {};
Poker.SoundSettingsView = Poker.View.extend({
    init : function(viewElementId,name) {
        this._super(viewElementId,name);
    },
    activate : function() {
        this._super();
        Poker.Settings.bindSettingToggle($("#soundEnabled"),Poker.Settings.Param.SOUND_ENABLED);
        Poker.Settings.bindSettingToggle($("#soundAlertsEnabled"),Poker.Settings.Param.SOUND_ALERTS_ENABLED,true);
        $.ga._trackEvent("user_navigation", "open_sfx_page");
    },
    deactivate : function() {
        this._super();
        $("#soundEnabled").unbind();
        $("#soundAlertsEnabled").unbind();
        $.ga._trackEvent("user_navigation", "close_sfx_page");
    }
});