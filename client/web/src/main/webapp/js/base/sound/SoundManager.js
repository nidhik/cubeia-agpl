"use strict";
var Poker = Poker || {};

/**
 * The sound manager is responsible for playing sounds in the client.
 *
 * It's built for being able to handle multi tabling, where only some sounds should
 * be played (like alerts) if the table is not active.
 *
 * @type {Poker.SoundManager}
 */
Poker.SoundManager = Class.extend({
    /**
     * @type Poker.SoundPlayer
     */
    soundPlayer:null,
    /**
     * @type Number
     */
    tableId:null,

    ready : false,

    init:function (soundRepository, tableId) {
        this.soundPlayer = new Poker.SoundPlayer(soundRepository);
        this.tableId = tableId;
    },
    setReady : function(ready) {
        this.ready = ready;
    },
    playSound:function (sound, selection) {
        var playAlerts = this.alertsEnabled() && sound.alert;
        var playSounds = this.soundsEnabled() && !sound.alert;
        if ((playSounds || playAlerts) && this.ready == true) {
            var soundPlayer = this.soundPlayer;
            setTimeout(function() {
                soundPlayer.play(sound, selection);
            }, sound.delay);
        }
    },
    alertsEnabled : function() {
        return Poker.Settings.isEnabled(Poker.Settings.Param.SOUND_ALERTS_ENABLED,true);
    },
    soundsEnabled:function() {
        var check = Poker.Settings.isEnabled(Poker.Settings.Param.SOUND_ENABLED);
        return check;
    },

    handleTableUpdate:function(sound, tableId) {
        if (tableId != this.tableId) return;
        var selection = Math.floor(Math.random()*sound.soundList.length);
        this.playSound(sound, selection)
    },
    handlePlaySound:function(sound) {
        var selection = Math.floor(Math.random()*sound.soundList.length);
        this.playSound(sound, selection)
    },

    playerAction:function(actionType, tableId, player, amount) {
        var sound = Poker.Sounds[actionType.id];
        if (sound != undefined ) {
            var selection = Math.floor(Math.random()*sound.soundList.length);
            this.playSound(sound, selection)
        }
    }


});