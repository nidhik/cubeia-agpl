"use strict";

var Poker = Poker || {};
Poker.CreditsView = Poker.ResponsiveTabView.extend({
    /**
     * @type Poker.PlayerApi
     */
    playerApi : null,
    accountingApi : null,
    closed : false,
    templateManager : null,
    sectionMenu : null,
    profileUpdated : null,
    init : function(closeFunction) {
        var self = this;
        this.profileUpdated = function(profile){
            self.updateProfile(profile);
        };
        Poker.AppCtx.getProfileManager().addProfileChangeListener(this.profileUpdated);
        this.templateManager = Poker.AppCtx.getTemplateManager();
        this.playerApi = Poker.AppCtx.getPlayerApi();
        this.accountingApi = Poker.AppCtx.getAccountingApi();
        this.removeElementOnClose = false;
        var viewElement = "#creditsView";
        this._super(viewElement,"Credits","C");
        $(viewElement).addClass("responsive-view");
        this.fixedSizeView = true;

        this.getViewElement().find(".close-button").off().click(function(){
            $(this).off();
            closeFunction();
        });
        this.getViewElement().find(".save-button").off().click(function(){
            self.saveAddress();
        });
        this.getViewElement().find(".withdraw-button").off().click(function(){
            self.requestWithdraw();
        });
        this.getViewElement().find(".edit-address").off().click(function(){
            self.displayEditAddress();
        });
        this.getViewElement().find(".cancel-button").off().click(function(){
            self.cancelEdit();
        });
        $("#withdrawAmount").off().on("keyup",function(e){
            self.validateAmount($(this));
        });

        var transactionsMenu = new Poker.BasicMenu(".transactions-navbar");
        transactionsMenu.addItem("#depositListItem",function(){
            self.requestDeposits();
        });
        transactionsMenu.addItem("#withdrawListItem",function(){
            self.requestWithdrawals();
        });

        var sectionMenu = new Poker.BasicMenu(".credits-navbar");
        sectionMenu.addItem("#depositMenuItem",function(){
            self.showSection("deposit");
            self.requestAddress();

        });
        this.requestAddress();
        this.requestQRCode();

        sectionMenu.addItem("#withdrawMenuItem",function(){
            self.showSection("withdraw");
            self.requestWithdrawAddress();
            self.requestPendingWithdrawals();
        });

        sectionMenu.addItem("#transactionsMenuItem",function(){
            self.showSection("transaction");
            transactionsMenu.selectItem("#depositListItem");

        });
        sectionMenu.selectItem("#depositMenuItem");
        this.variantMenu = sectionMenu;
        this.accountingApi.requestConfig()
            .done(function(config){
                self.updateConfig(config);
            }).fail(function(){
                console.log("Unable to fetch wallet config");
            });


    },
    updateConfig : function(config) {
        var format = function(amount) {
          return Poker.Utils.formatCurrency(parseInt(amount)/100000) + " mBTC";
        };
        this.getViewElement().find(".min-withdraw-amount").html(format(config.minWithdrawLimitInSatoshis));
        this.getViewElement().find(".min-deposit-amount").html(format(config.minDepositInSatoshis));
    },
    /**
     * @param {Poker.MyProfile}
     */
    updateProfile : function(profile){
        var self = this;
        var accounts = profile.accounts;
        console.log("accounts = ", profile.accounts);
        if(accounts!=null && accounts.length > 0) {
            $.each(accounts,function(i,a){
                if(a.currencyCode == "XMB" && a.role == "MAIN") {
                    self.getViewElement().find(".xmb-balance").html(a.formattedBalance);
                }
            });
        }
    },
    validateAmount : function(input) {
        var val = $.trim(input.val());

        if(!val.match(/(^[1-9][0-9]*$)|(^[0-9]+(\.[0-9]{1,2})$)/g)) {
            input.parent().addClass("has-error");
            return false;
        } else {
            input.parent().removeClass("has-error");
            return true;
        }
    },
    showSection : function(id) {
        this.getViewElement().find(".tab-container").hide();
        this.getViewElement().find("."+id+"-container").show();
    },
    requestAddress : function() {
        var self = this;
        this.accountingApi.requestWalletAddress()
            .done(function(data){
                $("#walletAddress").val(data.toAddress);
                self.requestQRCode();
            }).fail(function(){

            }); console.log("Unable to request deposit address");
    },
    requestQRCode : function() {
        this.accountingApi.requestQRCode().done(function(data){
                $("#walletAddressQR").prop("src",data);
            }).fail(function(){
                console.log("Unable to fetch QR-code");
            });
    },
    saveAddress : function(){
        var self = this;
        var address = $.trim($("#newWithdrawAddress").val());
        var response = this.accountingApi.saveWithdrawAddress(address)
        response.done(function(data){
            console.log("Saved withdraw address", data);
            $("#newWithdrawAddress").val("");
            self.displayWithdrawAddress(data);
        }).fail(function(){
            self.displayNewAddressError();
        });
    },
    requestWithdrawals : function() {
        var self = this;
        this.accountingApi.requestWithdrawals().done(function(withdrawals){
                console.log("withdrawals=", withdrawals);

                var template = self.templateManager.getRenderTemplate("withdrawListTemplate");
                self.getViewElement().find(".withdraw-list-container").html(template.render(withdrawals));
            }).fail(function(){
                console.log("Unable to fetch withdrawals");
            }
        );
    },
    requestDeposits : function() {
        var self = this;
        this.accountingApi.requestDeposits().done(function(deposits){
                console.log("deposits=", deposits);
                var template = self.templateManager.getRenderTemplate("depositListTemplate");
                self.getViewElement().find(".withdraw-list-container").html(template.render(deposits));
            }).fail(function(){
                console.log("Unable to request deposits");
            }
        );
    },
    requestWithdrawAddress : function() {
        var self = this;
        this.accountingApi.requestWithdrawAddress().done(function(data){
            self.displayWithdrawAddress(data);
            console.log("request WithdrawAddress ", data);
        }).fail(function(){
            console.log("error requesting withdraw address");
        });
    },
    cancelEdit : function() {
        $("#newWithdrawAddress").val("");
        this.displayCurrentAddress();
    },
    displayEditAddress : function() {
        this.getViewElement().find(".edit-address-error").hide();
        this.getViewElement().find(".edit-address-container").show();
        this.getViewElement().find(".withdraw-amount-container").hide();
    },
    displayNewAddressError : function() {
        this.getViewElement().find(".edit-address-error").show();
    },
    displayCurrentAddress : function() {
        this.getViewElement().find(".alert-no-address").hide();
        this.getViewElement().find(".edit-address-container").hide();
        this.getViewElement().find(".withdraw-amount-container").show();
    },
    displayWithdrawAddress : function(withdrawAddress) {
        if(withdrawAddress==null) {
           this.displayEditAddress();
           this.getViewElement().find(".alert-no-address").show();
        } else {
            this.displayCurrentAddress();
            $("#currentWithdrawAddress").val(withdrawAddress.toAddress);
        }
    },
    requestWithdraw : function() {
        var self = this;
        if(!this.validateAmount($("#withdrawAmount"))) {
            return;
        }
        var withdrawAmount = $.trim($("#withdrawAmount").val());
        var amount = Math.floor((parseFloat(withdrawAmount)*100000));
        console.log("withdraw amount = " + amount);
        var response = this.accountingApi.withdraw(""+amount)
        response.done(function(data){
            self.getViewElement().find(".withdraw-error").hide();
            $("#withdrawAmount").val("");
            self.requestPendingWithdrawals();
        }).fail(function(){
            self.getViewElement().find(".withdraw-error").show();
        });
    },
    requestPendingWithdrawals : function() {
        Poker.AppCtx.getProfileManager().loadMyPlayerProfile();
        var self = this;
        this.accountingApi.requestPendingWithdrawals().done(function(pending){
                var template = self.templateManager.getRenderTemplate("transactionTemplate");
                self.getViewElement().find(".pending-withdrawals-container").html(template.render(pending));
                $.each(pending.withdrawals,function(i,w){
                    $("#cancelWithdrawal"+ w.id).off().click(function(){
                        self.accountingApi.cancelWithdraw(w.id).done(
                        function(withdrawal){
                            self.requestPendingWithdrawals();
                        }).fail(function(){
                            console.log("Unable to cancel withdraw");
                        });
                    });
                });
            }).fail(function(){

            });
    },
    close : function() {
        this._super();
        Poker.AppCtx.getProfileManager().removeProfileChangeListener(this.profileUpdated);
    }

});