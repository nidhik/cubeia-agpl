"use strict";

var Poker = Poker || {};

Poker.PlayerApi = Class.extend({
    playerApiBaseUrl : null,
    operatorBaseUrl : null,

    init : function(playerApiBaseUrl,operatorApiBaseUrl) {
        this.playerApiBaseUrl = playerApiBaseUrl;
        this.operatorApiBaseUrl = operatorApiBaseUrl;
    },
    invalidateSession : function(){
        return $.ajax(contextPath + "/logout",{ type : "GET" });
    },
    /**
     * Retrieves the player profile for a specific player
     * @param {Number} playerId id of the player to get the profile for
     * @param {String} sessionToken authentication token to the player api
     * @param {Function} callback success callback
     * @param {Function} errorCallback error callback
     */
    requestPlayerProfile : function(playerId,sessionToken,callback,errorCallback) {
        var url = this.playerApiBaseUrl + "/public/player/"+playerId+"/profile?session="+sessionToken;
        $.ajax(url, {
            method : "GET",
            contentType : "application/json",
            success : function(data) {
                callback(data);
            },
            error : function() {
                console.log("Error while fetching player profile " + url);
                if(typeof(errorCallback)!="undefined") {
                    errorCallback();
                }
            }

        });
    },
    isLeaderboardEnabled : function() {
        var enabled = this.operatorApiBaseUrl!=null && this.operatorApiBaseUrl!="";
        return enabled;
    },
    requestExperienceInfo : function(sessionToken,callback,errorCallback) {
        var url = this.playerApiBaseUrl + "/player/experience/poker";
        this.requestInfo(url,sessionToken,"GET",callback,errorCallback);
    },
    requestBonusInfo : function(sessionToken,callback,errorCallback) {
        var url = this.playerApiBaseUrl + "/player/bonus";
        this.requestInfo(url,sessionToken,"GET",callback,errorCallback);
    },

    requestAccountInfo : function(sessionToken,callback,errorCallback) {
        var url = this.playerApiBaseUrl + "/player/profile";
        this.requestInfo(url,sessionToken,"GET",callback,errorCallback);

    },
    requestTopUp : function(bonusName,sessionToken,callback,errorCallback) {
        var url = this.playerApiBaseUrl + "/player/bonus/"+bonusName;
        this.requestInfo(url,sessionToken,"POST",callback,errorCallback);
    },


    requestInfo : function(url,sessionToken,method,callback,errorCallback) {
        if(this.playerApiBaseUrl==null || this.playerApiBaseUrl=="") {
            return;
        }
        $.ajax(url + "?r="+Math.random()+"&session="+sessionToken, {
            type : method,
            contentType : "application/json",
            success : function(data) {
                callback(data);
            },
            error : function() {
                console.log("Error while fetching " + url);
                if(typeof(errorCallback)!="undefined") {
                    errorCallback();
                }
            }

        });
    },
    requestLeaderboard : function(leaderboardId,global, callback,errorCallback){
        if(this.operatorApiBaseUrl==null || this.operatorBaseUrl=="") {
            return;
        }
        var globalPath = global ? "/global" : "";
        $.ajax(this.operatorApiBaseUrl + '/public/leaderboard/'+Poker.SkinConfiguration.operatorId+'/' + leaderboardId + globalPath + "?r="+Math.random(),{
            method : "GET",
            contentType : "application/json",
            success : function(data) {
                callback(data);
            },
            error : function(e) {
               errorCallback(e);
            }
        });
    }


});