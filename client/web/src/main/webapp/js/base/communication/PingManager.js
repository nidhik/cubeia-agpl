"use strict";
var Poker = Poker || {};

Poker.PingManager = Class.extend({

    pingData : null,
    sessionPingStats : null,
    disconnects : 0,
    startTime : null,
    lastReport : 0,
    init : function() {
        this.sessionPingStats = new Poker.TimeStatistics(10);

        this.start();
    },
    start : function() {
        setInterval(function(){
            var cm = Poker.AppCtx.getConnectionManager();
            if(cm.connected==true) {
                cm.sendVersionPacket();
            }
        },240000)
    },
    onDisconnect : function() {
        this.disconnects++;
    },
    versionPacketSent : function() {
        this.startTime = new Date().getTime();
    },
    versionPacketReceived : function() {
        var now = new Date().getTime();
        var pingTime = now - this.startTime;
        this.sessionPingStats.add(pingTime);
        var p = this.sessionPingStats;
        console.log("Session ping: max="+ p.max + ", min="+ p.min + ",count="+ p.count+",average="+ p.getAverage());
        if(this.sessionPingStats.count>=5) {
            this.report();
        }
    },
    report : function() {
        var now = new Date().getTime();
        var elapsed = now-this.lastReport;
        if(elapsed>1000000)   {
            var p = this.sessionPingStats;
            var query = "?disconnects="+this.disconnects+"&max="+ p.max + "&min="+ p.min + "&count="+ p.count+"&average="+ p.getAverage()+"&pid="+Poker.MyPlayer.id;
            $.ajax(contextPath + "/poker/ping"+query,{
                type : "GET"
            });
            this.lastReport = now;
        }

    }

});