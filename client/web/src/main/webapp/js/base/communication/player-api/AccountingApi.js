"use strict";
var Poker = Poker || {};

Poker.AccountingApi = Class.extend({
    playerApiBaseUrl : null,
    loadingOverlay : null,
    /**
     * @type Poker.Map
     */
    locks : null,
    init : function(playerApiBaseUrl) {
        this.playerApiBaseUrl = playerApiBaseUrl;
        this.loadingOverlay = new Poker.LoadingOverlay();
        this.locks = new Poker.Map();
    },

    doGet : function(url,lock) {
        if(lock) {
            if(!this.lock(url)){
                return;
            }
        }
        var sessionToken = Poker.MyPlayer.sessionToken;
        var response =  $.ajax(url + "?r="+Math.random()+"&session="+sessionToken,{
            contentType : "application/json",
            type : "GET"
        });
        var self = this;
        response.always(function(){
            self.unlock(url);
        });
        return response;
    },
    doPost : function(url,body,lock){
        if(lock) {
            if(!this.lock(url)){
                return;
            }
        }
        var sessionToken = Poker.MyPlayer.sessionToken;
        var response =  $.ajax(url + "?r="+Math.random()+"&session="+sessionToken,{
            contentType : "application/json",
            type : "POST",
            data: body
        });
        var self = this;
        response.always(function(){
            self.unlock(url);
        });
        return response;
    },
    lock : function(url) {
        if(this.locks.contains(url)){
            return false;
        }
        this.locks.put(url,url);
        this.loadingOverlay.show();
        return true;
    },
    unlock : function(url) {
        this.locks.remove(url);
        if(this.locks.size()==0) {
            this.loadingOverlay.hide();
        }

    },
    requestConfig : function() {
        var url = this.playerApiBaseUrl + "/player/bitcoin/config";
        return this.doGet(url);
    },
    cancelWithdraw : function(withdrawId) {
        var url = this.playerApiBaseUrl + "/player/bitcoin/withdraw/cancel/" + withdrawId;
        return this.doGet(url,true);
    },
    requestDeposits : function() {
        var url = this.playerApiBaseUrl + "/player/bitcoin/deposits/list";
        return this.doGet(url);
    },
    requestWithdrawals : function() {
        var url = this.playerApiBaseUrl + "/player/bitcoin/withdrawals/list";
        return this.doGet(url);
    },
    requestWalletAddress : function() {
        var url = this.playerApiBaseUrl + "/player/bitcoin/address";
        return this.doGet(url);
    },
    requestPendingWithdrawals : function() {
        var url = this.playerApiBaseUrl + "/player/bitcoin/withdrawals/pending";
        return this.doGet(url);
    },
    requestWithdrawAddress : function() {
        var url = this.playerApiBaseUrl + "/player/bitcoin/withdraw/address";
        return this.doGet(url);
    },
    withdraw : function(amount) {
        var url = this.playerApiBaseUrl + "/player/bitcoin/withdraw/request";
        return this.doPost(url, amount,true);
    },
    saveWithdrawAddress : function(address) {
        var url = this.playerApiBaseUrl + "/player/bitcoin/withdraw/address";
        return this.doPost(url, address,true);
    },
    requestQRCode : function() {
        var url = this.playerApiBaseUrl + "/player/bitcoin/qrcode/data-uri";
        return this.doGet(url);
    }
});