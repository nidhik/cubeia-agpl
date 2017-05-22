"use strict";
var Poker = Poker || {};
Poker.CanvasProgressbar = Class.extend({
    canvas : null,
    context : null,
    startTime: 0,
    progressTime : null,
    timer : null,
    settings : null,
    defaultSettings : { border : false },
    soundManager : null,
    secondWarning : false,
    init : function(canvas,settings,soundManager) {
        var self = this;
        this.settings = $.extend({},this.defaultSettings , settings);
        this.canvas = $(canvas);
        self.setSize();
        setTimeout(function(){
            self.setSize();
        },500);

        this.soundManager = soundManager;

        $(window).on("resizeEnd redrawTable",function(){
            self.setSize();
        });
        this.canvas.parent().hide();
    },
    setSize : function(){
        var w = this.canvas.parent().width();
        var h = this.canvas.parent().height();
        this.canvas.attr("width",w);
        this.canvas.attr("height",h);
        this.draw();
    },
    getProgressAngle : function(progress) {
        return (2 * Math.PI * progress) - Math.PI/2;
    },
    getProgress : function() {
        if(this.startTime==0) {
            return 0;
        }
        var currentTime = new Date().getTime();
        var timeElapsed = currentTime - this.startTime;
        var progress = timeElapsed / this.progressTime;
        return progress > 1 ? 1 : progress;
    },
    getColor : function(progress) {
        if(progress > 0.8) {
            return "#c42e05";
        } else if(progress > 0.5) {
            return "#ffaa16";
        }
        return '#b3d800';
    },
    start : function(progressTime) {
        this.secondWarning=false;
        this.setSize();
        this.canvas.parent().show();
        this.startTime = new Date().getTime();
        this.progressTime = progressTime;
        var self = this;
        this.interval = setInterval(function(){
            self.draw();
        },50);
    },
    stop : function() {
        this.startTime = 0;
        this.canvas.parent().hide();
        if(this.interval!=null) {
            clearInterval(this.interval);
        }
        var ctx = this.getContext();
        ctx.clearRect(0,0,this.canvas.width()*2,this.canvas.height()*2);
    },
    draw : function() {
        var ctx = this.getContext();
        var point = this.getCenter();
        var progress = this.getProgress();

        ctx.clearRect(0,0,this.canvas.width(),this.canvas.height());
        ctx.globalCompositeOperation = 'source-over';

        if(this.settings.border == true) {
            ctx.beginPath();
            ctx.arc(point.left, point.top, this.getRadius(), 0,2*Math.PI, false);
            ctx.fillStyle = this.getColor(progress);
            ctx.fill();
        }
        ctx.beginPath();
        ctx.arc(point.left, point.top, this.getRadius()*0.9, 0,2*Math.PI, false);
        ctx.fillStyle = "#444";
        ctx.fill();

        ctx.beginPath();
        ctx.moveTo(point.left,point.top);
        ctx.arc(point.left, point.top, this.getRadius()*0.9, -Math.PI/2, this.getProgressAngle(progress), false);
        ctx.lineTo(point.left,point.top);
        ctx.fillStyle = this.getColor(progress);
        ctx.fill();

        var r = this.getRadius() * 0.4;
        ctx.globalCompositeOperation = 'destination-out';
        ctx.beginPath();
        ctx.arc(point.left,point.top,r,0, 2 * Math.PI,false);
        ctx.fill();

        ctx.globalCompositeOperation = 'source-over';
        ctx.beginPath();
        ctx.arc(point.left, point.top, this.getRadius()*0.2, 0,2*Math.PI, false);
        ctx.fillStyle = this.getColor(progress);
        ctx.fill();

        if(progress>=0.5 && this.secondWarning==false) {
            this.playWarningSound(Poker.Sounds.TIME_WARNING);
            this.secondWarning = true;
        }

        if(progress>=1) {
            clearInterval(this.interval);
        }

    },
    playWarningSound : function(sound) {
      if(typeof(this.soundManager)!="undefined") {
          this.soundManager.handlePlaySound(sound);
      }
    },
    getRadius : function() {
        return Math.floor(this.canvas.height()/2);
    },
    getCenter : function() {
        var height = this.canvas.height();
        var width = this.canvas.width();
        return { top : Math.floor(height/2), left : Math.floor(width/2) };
    },
    getContext : function() {
        if(this.context==null) {
            this.context=this.canvas.get(0).getContext("2d");
        }
        return this.context;
    }
});