"use strict";
var Poker = Poker || {};

Poker.ChatManager = Class.extend({
    /**
     * @type Poker.Map
     */
    mutedPlayers : null,
    init : function() {
        this.mutedPlayers = new Poker.Map();
    },
    mutePlayer : function(playerId) {
        this.mutedPlayers.put(playerId,playerId);
    },
    unmutePlayer : function(playerId) {
        this.mutedPlayers.remove(playerId);
    },
    isMuted : function(playerId) {
        return this.mutedPlayers.contains(playerId);
    }
});