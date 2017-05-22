<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML>
<html>
<head>
    <title></title>

    <meta name="viewport" content="width=device-width, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="default">

    <link rel="apple-touch-icon" href="${cp}/skins/${skin}/images/lobby/icon.png" />
    <link rel="stylesheet" type="text/css" href="${cp}/skins/default/css/gritter/css/jquery.gritter.css"/>
    <link rel="stylesheet" type="text/css" href="${cp}/skins/default/css/browser-support.css"/>
    <link rel="stylesheet/less" type="text/css" href="${cp}/js/lib/bootstrap/less/bootstrap.less"/>

    <link id="defaultSkinCss" rel="stylesheet/less" type="text/css" href="${cp}/skins/default/less/base.less" />

    <!-- All less files are imported in this base.less-->
    <link id="skinCss" rel="stylesheet/less" type="text/css" href="${cp}/skins/${skin}/less/base.less" />

    <c:if test="${not empty cssOverride}">
        <link id="overrideSkinCss" rel="stylesheet/less" type="text/css" href="${cssOverride}" />
    </c:if>

    <script type="text/javascript" src="${cp}/skins/${skin}/skin-config.js"></script>
    <script type="text/javascript" src="${cp}/skins/${skin}/preload-images.js"></script>

    <script type="text/javascript"  src="${cp}/js/lib/less-1.4.1.min.js"></script>

    <script type="text/javascript" src="${cp}/js/lib/classjs.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/modernizr-2.6.2-custom.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/jquery-ui-1.8.21.custom.min.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/jquery.ui.touch-punch.js"></script>
    <script type="text/javascript" src="${cp}/js/base/jquery-plugins/touch-click.js"></script>
    <script type="text/javascript" src="${cp}/js/base/jquery-plugins/relative-offset.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/jquery.nicescroll.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/moment.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/purl.js"></script>


    <script type="text/javascript" src="${cp}/js/lib/handlebars.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/json2.js"></script>

    <script type="text/javascript" src="${cp}/js/lib/org/cometd.js"></script>

    <script type="text/javascript" src="${cp}/js/base/ui/CanvasProgressbar.js"></script>

    <script type="text/javascript" src="${cp}/js/lib/cubeia/firebase-js-api-1.10.0-javascript.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/cubeia/firebase-protocol-1.10.0-javascript.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/poker-protocol-1.0-SNAPSHOT.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/routing-service-protocol-1.0-SNAPSHOT.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/quo.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/i18next-1.6.0.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/jquery.gritter.js"></script>


    <script type="text/javascript" src="${cp}/js/lib/PxLoader-0.1.js"></script>
    <script type="text/javascript" src="${cp}/js/lib/PxLoaderImage-0.1.js"></script>



    <script src="${cp}/js/base/Utils.js" type="text/javascript"></script>
    <script src="${cp}/js/base/ProtocolUtils.js" type="text/javascript"></script>

    <script type="text/javascript" src="${cp}/js/base/data/Map.js"></script>
    <script type="text/javascript" src="${cp}/js/base/PeriodicalUpdater.js"></script>
    <script type="text/javascript" src="${cp}/js/base/OperatorConfig.js"></script>
    <script type="text/javascript" src="${cp}/js/base/MyPlayer.js"></script>
    <script type="text/javascript" src="${cp}/js/base/PlayerTableStatus.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/NotificationsManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/AchievementManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/communication/achievement/AchievementPacketHandler.js"></script>
    <script type="text/javascript" src="${cp}/js/base/TimeStatistics.js"></script>
    <script type="text/javascript" src="${cp}/js/base/communication/PingManager.js"></script>


    <script src="${cp}/js/base/communication/poker-game/ActionUtils.js" type="text/javascript"></script>
    <script src="${cp}/js/base/communication/poker-game/PokerPacketHandler.js" type="text/javascript"></script>
    <script src="${cp}/js/base/communication/poker-game/PokerRequestHandler.js"  type="text/javascript"></script>
    <script src="${cp}/js/base/communication/poker-game/PokerSequence.js" type="text/javascript"></script>

    <script src="${cp}/js/base/communication/lobby/LobbyPacketHandler.js" type="text/javascript"></script>
    <script src="${cp}/js/base/communication/lobby/LobbyRequestHandler.js" type="text/javascript"></script>

    <script src="${cp}/js/base/communication/handhistory/HandHistoryRequestHandler.js" type="text/javascript"></script>
    <script src="${cp}/js/base/communication/handhistory/HandHistoryPacketHandler.js" type="text/javascript"></script>
    <script src="${cp}/js/base/ui/HandHistoryLayout.js" type="text/javascript"></script>
    <script src="${cp}/js/base/HandHistoryManager.js" type="text/javascript"></script>

    <script src="${cp}/js/base/communication/connection/ConnectionManager.js" type="text/javascript"></script>
    <script src="${cp}/js/base/communication/connection/ConnectionPacketHandler.js" type="text/javascript"></script>

    <script type="text/javascript" src="${cp}/js/base/communication/tournament/TournamentPacketHandler.js"></script>
    <script type="text/javascript" src="${cp}/js/base/communication/tournament/TournamentRequestHandler.js"></script>

    <script type="text/javascript" src="${cp}/js/base/communication/table/TableRequestHandler.js"></script>
    <script type="text/javascript" src="${cp}/js/base/communication/table/TablePacketHandler.js"></script>

    <script src="${cp}/js/base/communication/CommunicationManager.js" type="text/javascript"></script>

    <script type="text/javascript" src="${cp}/js/base/communication/player-api/PlayerApi.js"></script>
    <script type="text/javascript" src="${cp}/js/base/communication/player-api/AccountingApi.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/profile/Profile.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/profile/MyProfile.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/profile/ProfileManager.js"></script>

    <script type="text/javascript" src="${cp}/js/base/Settings.js"></script>

    <script type="text/javascript" src="${cp}/js/base/ui/BetSlider.js"></script>
    <script type="text/javascript" src="${cp}/js/base/Action.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/actions/CheckBoxAction.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/actions/BlindsActions.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/actions/ActionButton.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/actions/AbstractTableButtons.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/actions/ActionButtons.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/actions/TableButtons.js"></script>

    <script type="text/javascript" src="${cp}/js/base/ui/MyActionsManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/data/LobbyData.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/BasicMenu.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/LobbyLayoutManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/LobbyManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/Player.js"></script>
    <script type="text/javascript" src="${cp}/js/base/Table.js"></script>
    <script type="text/javascript" src="${cp}/js/base/TableManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/Clock.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/PotTransferAnimator.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/Log.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/TableEventLog.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/ChatInput.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/TableLayoutManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/TemplateManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/cards/DynamicHand.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/Seat.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/MyPlayerSeat.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/cards/Card.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/cards/CommunityCard.js"></script>
    <script type="text/javascript" src="${cp}/js/base/Pot.js"></script>
    <script type="text/javascript" src="${cp}/js/base/Hand.js"></script>
    <script type="text/javascript" src="${cp}/js/base/jquery-plugins/describe.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/CSSUtils.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/animation/Transform.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/animation/Animation.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/animation/CSSClassAnimation.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/animation/TransformAnimation.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/animation/AnimationManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/DealerButton.js"></script>

    <script type="text/javascript" src="${cp}/js/base/Navigation.js"></script>
    <script type="text/javascript" src="${cp}/js/base/sound/SoundSource.js"></script>
    <script type="text/javascript" src="${cp}/js/base/sound/SoundPlayer.js"></script>
    <script type="text/javascript" src="${cp}/js/base/sound/SoundManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/sound/SoundRepository.js"></script>
    <script type="text/javascript" src="${cp}/js/base/sound/Sounds.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/FutureActionType.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/FutureActions.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/Dialog.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/DialogManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/DisconnectDialog.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/BuyInDialog.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/TournamentBuyInDialog.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/View.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/TabView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/ResponsiveTabView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/CreditsView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/LoginView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/TableView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/MultiTableView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/TournamentView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/SoundSettingsView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/DevSettingsView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/ViewManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/ExternalPageView.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/MainMenuManager.js"></script>

    <script type="text/javascript" src="${cp}/js/base/ui/views/AccountPageManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ApplicationContext.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/views/ViewSwiper.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/ContextMenu.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/Sharing.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/Pager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/tournaments/TournamentList.js"></script>
    <script type="text/javascript" src="${cp}/js/base/tournaments/Tournament.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/tournaments/TournamentLayoutManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/tournaments/TournamentManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ui/LoadingOverlay.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ChatManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/ResourcePreLoader.js"></script>

    <script type="text/javascript" src="${cp}/js/base/communication/lobby/Unsubscribe.js"></script>


    <script type="text/javascript" src="${cp}/js/base/dev/MockEventManager.js"></script>
    <script type="text/javascript" src="${cp}/js/base/dev/PositionEditor.js"></script>
    <script type="text/javascript" src="${cp}/js/base/dev/DevTools.js"></script>

    <script type="text/javascript" src="${cp}/js/base/cs-leaderboard.js"></script>


    <c:if test="${not empty operatorId}">
        <script type="text/javascript">
            Poker.OperatorConfig.operatorId = ${operatorId};
            Poker.SkinConfiguration.operatorId = ${operatorId};
        </script>
    </c:if>
    <c:if test="${not empty clientTitle}">
        <script type="text/javascript">
            Poker.SkinConfiguration.title = "${clientTitle}";
        </script>
    </c:if>
    <c:if test="${not empty token}">
        <script type="text/javascript">
            Poker.MyPlayer.loginToken = "${token}";
            Poker.MyPlayer.pureToken = ${pureToken};
        </script>
    </c:if>

    <script type="text/javascript">

        var contextPath = "${cp}";

        $(document).ready(function(){

            var browserSupported =  function() {
                if(Modernizr && Modernizr.websockets && Modernizr.csstransitions) {
                    console.log("browser supported");
                    return true;
                } else {
                    document.getElementById("loadingView").style.display = "none";
                    document.getElementById("mainMenuContainer").style.display = "none";
                    document.getElementById("viewPort").style.display = "none";
                    document.getElementById("browserNotSupported").style.display = "";
                    return false;
                }

            }
            if(browserSupported()) {
                //to clear the stored user add #clear to the url
                if(document.location.hash.indexOf("clear") != -1){
                    Poker.Utils.removeStoredUser();
                }

                if(Poker.SkinConfiguration.onLoad) {
                    console.log("SkinConfig onLoad");
                    Poker.SkinConfiguration.onLoad();
                }

                //less.watch(); //development only
                $(".describe").describe();

                $("title").html(Poker.SkinConfiguration.title);


                var onResourcesLoaded = function() {
                    Poker.AppCtx.getConnectionManager().onResourcesLoaded();
                };
                var onApplicationWired = function() {
                    new Poker.ResourcePreloader('${cp}',onResourcesLoaded, browserNotSupported, Poker.SkinConfiguration.preLoadImages, Poker.SkinConfiguration.name);
                };


                i18n.init({ fallbackLng: 'en', postProcess: 'sprintf', resGetPath: '${cp}/i18n/__lng__.json' }, function(){
                    $("body").i18n();

                    <c:choose>
                    <c:when test="${not empty firebaseHost}">
                    var requestHost = "${firebaseHost}";
                    </c:when>
                    <c:otherwise>
                    var requestHost = window.location.hostname;
                    </c:otherwise>
                    </c:choose>
                    <c:choose>
                    <c:when test="${not empty firebaseHttpPort}">
                    var webSocketPort = ${firebaseHttpPort};
                    </c:when>
                    <c:otherwise>
                    var webSocketPort = 9191;
                    </c:otherwise>
                    </c:choose>

                    var webSocketUrl = requestHost ? requestHost : "localhost";

                    console.log("connecting to WS: " + webSocketUrl + ":" + webSocketPort);

                    //handles the lobby UI
                    Poker.AppCtx.wire({
                        webSocketUrl : webSocketUrl,
                        webSocketPort : webSocketPort,
                        tournamentLobbyUpdateInterval : 10000,
                        playerApiBaseUrl : "${playerApiBaseUrl}",
                        operatorApiBaseUrl : "${operatorApiBaseUrl}",
                        secure : ${secureConnection}
                    });
                    onApplicationWired();
                });
            }
        });



    </script>

</head>
<body>
<div id="browserNotSupported" class="browser-not-supported" style="display:none;">
    <h2>Browser not supported</h2>
    <p>It seems like you are using an outdated browser. To be able to play you need to upgrade your browser.</p>
    <p>Click on the icons below to download a new browser or upgrade your current one.</p>
    <div class="browser-list">
        <div class="browser">
            <a target="_blank" href="http://www.google.com/chrome/"><img src="${cp}/skins/default/images/chrome-icon.png"/></a>
        </div>
        <div class="browser">
            <a target="_blank" href="http://www.mozilla.org/firefox/"><img src="${cp}/skins/default/images/firefox-icon.png"/></a>
        </div>
        <div class="browser">
            <a target="_blank" href="http://www.apple.com/safari/"><img src="${cp}/skins/default/images/safari-icon.png"/></a>
        </div>
        <div class="browser">
            <a target="_blank" href="http://windows.microsoft.com/en-us/internet-explorer/download-ie"><img src="${cp}/skins/default/images/ie-icon.png"/></a>
        </div>
    </div>
</div>
<div class="view-port" id="viewPort">
    <div id="toolbar" style="display:none;">
        <div class="main-menu-button">
        </div>
        <div class="tabs-container">
            <ul id="tabItems" class="tabs">
            </ul>
        </div>
        <div class="user-panel">
            <div class="user-panel-name username"></div>
            <div class="user-panel-avatar"></div>
        </div>
    </div>

    <div class="toolbar-background"></div>




    <div id="mainMenuContainer" class="main-menu-container" style="">
        <ul id="mainMenuList">

        </ul>
    </div>
    <div class="menu-overlay slidable" style="display: none;">

    </div>

    <div id="soundSettingsView" class="config-view" style="display: none;">
        <h1>Sound Settings</h1>
        <h2>Configuration</h2>
        <div class="group">
            <div class="item">
                <fieldset class="toggle">
                    <input id="soundEnabled" type="checkbox">
                    <label onclick="" for="soundEnabled">
                        Game Play Sounds
                        <div class="setting-description">Cards and chips sounds</div>
                    </label>
                    <span class="toggle-button"></span>
                </fieldset>
            </div>
            <div class="item">
                <fieldset class="toggle">
                    <input id="soundAlertsEnabled" type="checkbox">
                    <label onclick="" for="soundAlertsEnabled">
                        Alert Sounds</label>
                    <span class="toggle-button"></span>
                </fieldset>
                <div class="setting-description">Turn notifications etc.</div>
            </div>
        </div>
    </div>

    <div id="devSettingsView" class="config-view" style="display: none;">
        <h1>Development config</h1>
        <h2>Communication</h2>
        <div class="group">
            <div class="item">
                <fieldset class="toggle">
                    <input id="freezeComEnabled" type="checkbox">
                    <label onclick="" for="freezeComEnabled">Freeze communication</label>
                    <span class="toggle-button"></span>
                </fieldset>
            </div>
        </div>
        <h2>Experimental features</h2>
        <div class="group">
            <div class="item">
                <fieldset class="toggle">
                    <input id="swipeEnabled" type="checkbox">
                    <label onclick="" for="swipeEnabled">Swipe to change tabs</label>
                    <span class="toggle-button"></span>
                </fieldset>
            </div>
            <div class="item">
                <span>Something else goes here</span>
            </div>
        </div>
    </div>

    <div class="view-container slidable">
        <div class="table-view-container" style="display:none;">
            <div class="multi-view-switch multi">
            </div>
            <div class="hand-ranking-icon"></div>
            <div id="handRankingsView" class="rankings-view" style="display:none;">
                <a id="closeHandRankings">Close</a>
                <div class="scroll-container nice-scroll">
                    <div class="hand">
                        <div class="cards">
                            <img src="${cp}/skins/${skin}/images/cards/as.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/ks.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/qs.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/js.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/ts.svg"/>
                        </div>
                        <div class="description">
                            <h4 class="hand-name">1. Royal flush</h4>
                            <p> Ace, King, Queen, Jack, Ten all of the same suit.</p>
                        </div>
                    </div>

                    <div class="hand">

                        <div class="cards">
                            <img src="${cp}/skins/${skin}/images/cards/9s.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/8s.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/7s.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/6s.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/5s.svg"/>
                        </div>
                        <div class="description">
                            <h4 class="hand-name">2. Straight flush</h4>
                            <p>Any five card sequence in the same suit.</p>
                        </div>

                    </div>

                    <div class="hand">

                        <div class="cards">
                            <img src="${cp}/skins/${skin}/images/cards/ks.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/kh.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/kc.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/kd.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/5s.svg"/>
                        </div>
                        <div class="description">
                            <h4 class="hand-name">3. Four of a kind</h4>
                            <p>Four cards of the same index.</p>
                        </div>
                    </div>

                    <div class="hand">

                        <div class="cards">
                            <img src="${cp}/skins/${skin}/images/cards/qs.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/qh.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/qc.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/5d.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/5s.svg"/>
                        </div>
                        <div class="description">
                            <h4 class="hand-name">4. Full house</h4>
                            <p>Three of a kind combined with a pair</p>
                        </div>
                    </div>

                    <div class="hand">

                        <div class="cards">
                            <img src="${cp}/skins/${skin}/images/cards/qh.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/8h.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/5h.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/3h.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/2h.svg"/>
                        </div>
                        <div class="description">
                            <h4 class="hand-name">5. Flush</h4>
                            <p>Five cards of the same suit</p>
                        </div>
                    </div>

                    <div class="hand">

                        <div class="cards">
                            <img src="${cp}/skins/${skin}/images/cards/as.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/kd.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/qh.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/jc.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/ts.svg"/>
                        </div>

                        <div class="description">
                            <h4 class="hand-name">6. Straight</h4>
                            <p>Five cards in sequence</p>
                        </div>
                    </div>
                    <div class="hand">

                        <div class="cards">
                            <img src="${cp}/skins/${skin}/images/cards/as.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/ad.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/ah.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/jc.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/ts.svg"/>
                        </div>
                        <div class="description">
                            <h4 class="hand-name">7. Three of a kind</h4>
                            <p>Three cards of the same index</p>
                        </div>
                    </div>

                    <div class="hand">

                        <div class="cards">
                            <img src="${cp}/skins/${skin}/images/cards/as.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/ad.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/jh.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/jc.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/ts.svg"/>
                        </div>
                        <div class="description">
                            <h4 class="hand-name">8. Two pair</h4>
                        </div>
                    </div>

                    <div class="hand">

                        <div class="cards">
                            <img src="${cp}/skins/${skin}/images/cards/as.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/ad.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/9h.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/4c.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/3s.svg"/>
                        </div>
                        <div class="description">
                            <h4 class="hand-name">9. Pair</h4>
                        </div>
                    </div>

                    <div class="hand">

                        <div class="cards">
                            <img src="${cp}/skins/${skin}/images/cards/as.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/jd.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/9h.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/4c.svg"/>
                            <img src="${cp}/skins/${skin}/images/cards/3s.svg"/>
                        </div>
                        <div class="description">
                            <h4 class="hand-name">10. High Card</h4>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div id="loginView" class="loading-view">
            <div class="loading-view-container">
                <div class="login-dialog">
                    <div class="logo-container"></div>
                    <div class="loading-box">
                        <div class="loading-progressbar">
                            <div class="progress"></div>
                        </div>
                        <div class="login-container" style="display:none;">
                            <div class="login-input-container">
                                <input name="user" class="describe" id="user" type="text" title="Username" value="" />
                                <input name="pwd" class="describe" id="pwd" type="password" title="Password" value=""/>
                            </div>
                            <div id="loginButton" class="login-button">
                                <span data-i18n="login.login"></span>
                            </div>
                        </div>
                        <div class="status-label" style="font-size:90%;">
                            <span class="connect-status"></span>
                        </div>
                    </div>
                    <div class="powered-by">
                        <img src="${cp}/skins/default/images/poweredby.png"/>
                    </div>
                </div>
            </div>
        </div>
        <div id="creditsView" style="display:none;">
            <div class="container">
                <div class="row" style="margin-bottom: 5px;">
                    <div class="col-sm-12">
                        <a class="close-button  default-btn">Close</a>
                    </div>
                </div>
                <div class="row">
                    <div class="col-sm-12">
                        <nav class="navbar-inverse navbar-variant credits-navbar" role="navigation">
                            <div class="navbar-header">
                                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target=".navbar-variant-collapse">
                                    <span class="sr-only">Toggle navigation</span>
                                    <span class="icon-bar"></span>
                                    <span class="icon-bar"></span>
                                    <span class="icon-bar"></span>
                                </button>
                                <a class="nav-active-item">Cash Games</a>
                            </div>
                            <div class="navbar-collapse navbar-variant-collapse collapse">
                                <ul class="nav nav-pills">
                                    <li id="depositMenuItem" class="active"><a>Deposit</a></li>
                                    <li id="withdrawMenuItem"><a>Withdraw</a></li>
                                    <li id="transactionsMenuItem"><a>Transactions</a></li>
                                    <li><a>&nbsp;</a></li>
                                </ul>
                            </div>
                        </nav>
                    </div>
                </div>
                <div class="tab-container deposit-container"  style="display:none;">
                    <div class="row">
                        <div class="col-sm-6">
                            <h2>Deposit</h2>
                            <h3>Your Personal Bitcoin Wallet Address</h3>
                            <p>
                                Send Bitcoins to this wallet address and they will automatically be put on your Poker account! Just scan the QR-code or copy the walled address into your Bitcoin client.
                            </p>
                            <div class="deposit-wallet-container">
                                <div class="qr-code-container">
                                    <img src="" id="walletAddressQR"/>
                                </div>
                                <div class="wallet-address-container">
                                    <label>Deposit Address:</label>
                                    <div class="form-control">
                                        <input type="text" id="walletAddress" disabled/>
                                    </div>

                                    <span class="min-deposit">Min deposit is <span class="min-deposit-amount"></span></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="tab-container withdraw-container"  style="display:none;">
                    <div class="row">
                        <div class="col-sm-6">
                            <h2>Withdraw</h2>
                            <div class="edit-address-container" style="display:none;">
                                <form class="form-inline">
                                    <div class="edit-address-error alert alert-danger" style="display:none;">
                                        Unable to save address. Make sure it is a valid address.
                                    </div>
                                    <div class="alert alert-info alert-no-address">
                                        You have to specify a withdraw address to be able to withdraw
                                    </div>
                                    <div class="form-group withdraw-address-container">
                                        <label>New Withdraw Address:</label>
                                        <input id="newWithdrawAddress" class="form-control" type="text"/>
                                    </div>
                                    <div class="form-group buttons-container">
                                        <a class="cancel-button btn default-btn">Cancel</a>
                                        <a class="save-button btn default-btn">Save</a>
                                    </div>

                                </form>

                            </div>
                            <div class="withdraw-amount-container" style="display:none;">
                                <form class="form-inline">
                                    <div class="withdraw-error alert alert-danger" style="display:none;">
                                        Unable to request withdraw.

                                    </div>
                                    <div class="form-group withdraw-address-container">
                                        <p>Withdrawable balance: <span class="xmb-balance"></span>. Min amount: <span class="min-withdraw-amount">1 mBTC</span></p>
                                        <label>Withdraw to address:</label>
                                        <div class="form-control">
                                            <input id="currentWithdrawAddress" class="current-address" disabled>
                                            <a class="btn btn-info edit-address"></a>
                                        </div>
                                    </div>

                                </form>
                                <br/>
                                <div class="form-inline" role="form">
                                    <div class="form-group amount-group" o>
                                        <label>Amount:</label>
                                        <input id="withdrawAmount" class="form-control withdraw-amount" autocomplete="off" type="text"/>
                                        <a class="withdraw-button btn default-btn">Withdraw</a>
                                    </div>
                                </div>
                            </div>
                            <br/>
                            <h3>Pending Withdrawals</h3>
                            <div class="pending-withdrawals-container">
                                You currently have no pending withdrawals
                            </div>
                        </div>
                    </div>
                </div>
                <div class="tab-container transaction-container"  style="display:none;">
                    <div class="row">
                        <div class="col-sm-12">
                            <nav class="navbar-inverse navbar-limits transactions-navbar">
                                <ul class="nav nav-pills">
                                    <li id="depositListItem"><a>Deposits</a></li>
                                    <li id="withdrawListItem"><a>Withdrawals</a></li>
                                </ul>
                            </nav>
                            <div class="withdraw-list-container"></div>
                        </div>
                    </div>
                </div>


            </div>
        </div>

        <div id="lobbyView" class="lobby-container"  style="display:none;">
            <div class="container">
                <div class="row">
                    <div class="col-sm-12">
                        <div class="logo-container">
                        </div>
                        <nav class="navbar-inverse currencies">
                            <div class="filter-group currencies">
                                <ul id="currencyMenu" class="nav nav-pills">
                                    <li class="filter-button">
                                        <a data-i18n="lobby.filters.currency" class="description">Select Currency:</a>
                                    </li>
                                </ul>
                            </div>
                        </nav>
                    </div>
                    <div class="col-sm-4">

                    </div>
                </div>
                <div class="row">
                    <div class="col-sm-12">
                        <iframe id="lobbyTopPromotionsIframe" class="lobby-top-promotions-iframe loading" scrolling="no"  marginheight="0" frameBorder="0"></iframe>
                        <div class="top-promo-loading-container">
                           <div class="iframe-loading"></div>
                        </div>

                    </div>
                </div>
                <div class="row">

                    <div class="col-sm-8">
                        <nav class="navbar-inverse navbar-top" role="navigation">
                            <div class="navbar-header">
                                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target=".navbar-top-collapse">
                                    <span class="sr-only">Toggle navigation</span>
                                    <span class="icon-bar"></span>
                                    <span class="icon-bar"></span>
                                    <span class="icon-bar"></span>
                                </button>
                                <a class="nav-active-item">Cash Games</a>
                            </div>
                            <div class="navbar-collapse navbar-top-collapse collapse">
                                <ul class="nav nav-pills">
                                    <li class="active" id="cashGameMenu">
                                        <a class="lobby-link"  data-i18n="lobby.menu.cash-games">
                                            [Cash Games]
                                        </a>
                                    </li>
                                    <li id="sitAndGoMenu" ><a class="lobby-link" data-i18n="lobby.menu.sit-n-gos">[Sit &amp; Go's]</a></li>
                                    <li id="tournamentMenu"><a class="lobby-link" data-i18n="lobby.menu.tournaments">[Tournaments]</a></li>
                                </ul>
                            </div>

                        </nav>

                        <nav class="navbar-inverse navbar-variant filter cashgame-filter" role="navigation">
                            <div class="navbar-header">
                                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target=".navbar-variant-collapse">
                                    <span class="sr-only">Toggle navigation</span>
                                    <span class="icon-bar"></span>
                                    <span class="icon-bar"></span>
                                    <span class="icon-bar"></span>
                                </button>
                                <a class="nav-active-item">Game Type</a>
                            </div>
                            <div class="navbar-collapse navbar-variant-collapse collapse">
                                <ul class="nav nav-pills">
                                    <li id="variantTEXAS_HOLDEM"><a>Hold'em</a></li>
                                    <li id="variantOMAHA"><a>Omaha</a></li>
                                    <li id="variantCRAZY_PINEAPPLE"><a>Crazy Holdem</a></li>
                                    <li id="variantSEVEN_CARD_STUD"><a>7 Card Stud</a></li>
                                    <li id="variantFIVE_CARD_STUD"><a>5 Card Stud</a></li>
                                    <li id="variantTELESINA"><a>Telesina</a></li>
                                </ul>
                            </div>
                        </nav>
                        <nav class="navbar-inverse navbar-limits filter cashgame-filter sitandgo-filter">
                            <ul class="nav nav-pills">
                                <li id="limitsNL"><a>No Limit</a></li>
                                <li id="limitsPL"><a>Pot Limit</a></li>
                                <li id="limitsFL"><a>Fixed Limit</a></li>
                            </ul>
                        </nav>
                        <div class="lobby-checkbox-filter tournament-filter sitandgo-filter">
                            <input class="checkbox" checked="checked" id="registeringOnly" type="checkbox"/>
                            <label class="checkbox-icon-label" for="registeringOnly"  data-i18n="lobby.filters.show-registering">
                                Show registering only
                            </label>
                        </div>
                        <div class="lobby-checkbox-filter cashgame-filter">
                            <input class="checkbox" type="checkbox" id="hideFullTables"/>
                            <label class="checkbox-icon-label" for="hideFullTables"  data-i18n="lobby.filters.full">
                                Hide full tables
                            </label>
                            <input class="checkbox" type="checkbox" id="hideEmptyTables"/>
                            <label class="checkbox-icon-label" for="hideEmptyTables" data-i18n="lobby.filters.empty">
                                Hide empty tables
                            </label>

                        </div>
                          <div id="tableListContainer"></div>
                    </div>
                    <div class="col-sm-4">
                        <iframe id="lobbyRightPromotionsIframe" class="lobby-right-promotions-iframe" marginheight="0" scrolling="no"  frameBorder="0"></iframe>
                        <div id="leaderboardContainer" style="display:none;">
                            <div class="icon-title">
                                <div class="icon leaderboard"></div>
                                <div class="text">Current Top Winners</div>
                            </div>
                            <div id="leaderboard" data-leaderboard-id="top_winnings_trm" data-nr-of-items="5" class="leaderboard"></div>
                        </div>
                    </div>
                </div>

        </div>

        </div>
        <div class="user-overlay-container" style="display: none;">
            <h1>Account</h1>
            <div class="account-button logout-link">
                <span data-i18n="user.log-out"></span>
            </div>
            <div id="internalAccountContent">
                <div class="account-block details" id="account_details">
                    <div class="left">
                        <div id="accountAvatar" class="player-avatar"></div>
                        <div id="userLevel" class="level"></div>
                    </div>
                    <div class="right">
                        <div id="user_name" class="name"></div>

                        <div id="xpContainer" class="xp">
                            <div class="xp-progress-container">
                                <div id="xpProgress" class="bar"></div>
                            </div>
                            <div class="xp-info">
                                <div class="next-level-container">Next level: <span id="nextLevel"></span></div>
                                <div class="current-xp" id="currentXp"></div>
                            </div>
                        </div>
                    </div>
              </div>
              <div class="account-block" id="accountBalancesContainer">

              </div>
              <div class="account-block " id="bonusCollectContainer">
                    <h3>Top up</h3>
                    <div id="topUpCurrencies"></div>
                    <div class="refill_field">

                        <div class="top-up-progress">
                            <div class="small-label">
                                Time to next top up:  <span id="coolDownLabel"></span>
                            </div>

                            <div class="list_field">
                                <div class="progress_container">
                                    <div class="progress_bar" id="coolDownProgress"></div>
                                </div>
                            </div>
                            <div class="refill-button-container">
                                <div id="refillButton" class="account-button refill-unavailable">Top Up!</div>
                            </div>
                        </div>

                        <div class="balance-too-high">
                            You are able to top up once every <span id="bonusCoolDownTime"></span> hours when
                            your balance is <span id="bonusBalanceLowerLimit"></span> or less.
                        </div>
                    </div>

                </div>
            </div>
            <iframe id="accountIframe" class="account-iframe" scrolling="no"></iframe>
            <div class="account-buttons">
                <div class="account-button" id="editProfileButton">
                    Profile
                </div>
                <div class="account-button" id="buyCreditsButton">
                    Buy Credits
                </div>
            </div>

        </div>
        <div class="xp-progress-notification" style="display:none;">

            <div class="xp-progress-container">
                <div class="bar" style="width:50%;"></div>
            </div>

        </div>

        <div class="profile-view" id="editProfileView" style="display: none;">
            <iframe class="external-view-iframe"></iframe>
            <a class="close-button  default-btn">Close</a>
        </div>
        <div class="buy-credits-view" id="buyCreditsView"  style="display: none;">
            <iframe class="external-view-iframe"></iframe>
            <a class="close-button default-btn">Close</a>
        </div>
        <div class="buy-credits-view" id="externalPageView"  style="display: none;">
            <iframe class="external-view-iframe"></iframe>
            <a class="close-button default-btn">Close</a>
        </div>
    </div>
    <div class="top-loading-overlay" id="topLoadingOverlay" style="display:none;">
        <div class="loading-overlay-progress"></div>
    </div>
</div>
<script id="balanceTemplate" type="text/mustache">
    {{#accounts}}
    <div class="account-row">
        <span class="balance-header">{{accountLabel role}}</span>
        <div>
            <span class="account-balance">{{{formattedBalance}}}</span>
        </div>
    </div>
    {{/accounts}}

</script>

<div id="emptySeatTemplate" style="display: none;">
    <div class="avatar-base">
        <div class="open-seat">{{t "table.open"}}</div>
    </div>
</div>

<div id="seatTemplate" style="display: none;">
    <div class="avatar-base">

    </div>
    <div class="avatar-base-border">

    </div>
    <div class="player-name">
        {{name}}
    </div>
    <div class="avatar">

    </div>
    <div class="cards-container cards-container-player">

    </div>

    <div class="player-status">

    </div>

    <div class="seat-balance balance">

    </div>
    <div class="action-text">

    </div>
    <div class="action-amount balance">
        <span></span>
    </div>
    <div class="hand-strength">

    </div>
    <div class="player-level level">

    </div>
    <div class="player-item">

    </div>
    <div class="player-award">

    </div>
    <div class="seat-progressbar">
        <canvas width="20" height="20"></canvas>
    </div>
</div>

<script type="text/mustache" id="currencyFilterTemplate">
    <li class="filter-button currency" id="filterButton{{code}}"><a>{{longName}}</a></li>
</script>
<script type="text/mustache" id="playerCardTemplate" style="display: none;">
    <img class="card-image number-{{cardNum}}" src="{{backgroundImage}}" id="c{{domId}}" style=""/>
</script>
<script type="text/mustache" id="communityCardTemplate" style="display: none;">
    <div id="communityCard-{{domId}}" class="community-card-container">
        <img class="card-image" src="{{backgroundImage}}" id="communityCardImage-{{domId}}"/>
    </div>
</script>
<div id="mainPotTemplate" style="display: none;">
        <div class="balance pot-container pot-container-{{potId}}">
            <div class="value"><span class="pot-value pot-{{potId}}">{{amount}}</span></div>
        </div>
</div>
<div id="myPlayerSeatTemplate" style="display:none;">
    <div class="player-name">
        {{name}}
    </div>

    <div class="seat-balance balance">

    </div>
    <div class="avatar-base">

    </div>
    <div class="avatar-base-border">

    </div>
    <div class="avatar">

    </div>
    <div class="action-amount balance">

    </div>
    <div class="cards-container">

    </div>
    <div class="player-status"></div>
    <div class="action-text">

    </div>
    <div class="player-level level">

    </div>
    <div class="hand-strength">

    </div>
    <div class="player-item">

    </div>
    <div class="player-award">

    </div>
    <div class="discard-description" style="display: none;">
        {{t "discard.description" }}
        <div class="discard-arrow"></div>
    </div>
</div>

<script type="text/mustache" id="depositListTemplate">
    <table width="100%" class="deposits-table">
        <thead>

        <th>Id</th>
        <th>Requested</th>
        <th class="amount">Amount (mBTC)</th>
        <th>Status</th>
        <th>Confirmed</th>
        <th>Block depth</th>
        <th>To Address</th>
        </thead>
        <tbody>
        {{#deposits}}
        <tr>
            <td class="id">{{id}}</td>
            <td>{{date created}}</td>
            <td class="amount">{{mbtc satoshis}}</td>
            <td class="status">{{handlingStatus}}</td>
            <td class="confirmed">{{confirmed}}</td>
            <td class="block-depth">{{blockDepth}}</td>
            <td class="to-address">{{toAddress}}</td>
        </tr>
        {{/deposits}}
        </tbody>
    </table>

</script>
<script type="text/mustache" id="withdrawListTemplate">
    <table width="100%" class="withdrawals-table">
        <thead>

        <th>Id</th>
        <th>Requested</th>
        <th class="amount">Amount (mBTC)</th>
        <th>Status</th>
        <th>To Address</th>
        </thead>
        <tbody>
        {{#withdrawals }}
        <tr>
            <td class="id">{{id}}</td>
            <td>{{date created}}</td>
            <td class="amount">{{mbtc satoshis}}</td>
            <td class="status">{{status}}</td>
            <td class="to-address">{{toAddress}}</td>
        </tr>
        {{/withdrawals}}
        </tbody>
    </table>

</script>
<script type="text/mustache" id="transactionTemplate">
    <table width="80%" class="pending-table">
        <thead>

            <th>Requested</th>
            <th class="amount">Amount (mBTC)</th>
            <th></th>
        </thead>
        <tbody>
        {{#withdrawals }}
            <tr>
                <td>{{date created}}</td>
                <td class="amount">{{mbtc satoshis}}</td>
                <td class="cancel" rowspan="2"><a class="btn default-btn" id="cancelWithdrawal{{id}}">Cancel</a></td>
            </tr>
            <tr>
                <td colspan="3" class="to-address">{{toAddress}}</td>

            </tr>
        {{/withdrawals}}
        </tbody>
    </table>

</script>
<script type="text/mustache" id="sitAndGoLobbyListTemplate">
    <table class="table lobby-list-table">
        <thead class="table-item-header">
            <tr>
                <th class="table-name">{{t "lobby.list.name"}}</th>
                <th class="buy-in buy-in-sort sorting">{{t "lobby.list.buy-in"}}</th>
                <th class="seated capacity-sort sorting">{{t "lobby.list.players"}}</th>
            </tr>
        </thead>
        <tbody class="table-list-item-container">
        </tbody>
    </table>
</script>

<script type="text/mustache" id="tournamentLobbyListTemplate">
    <table class="table lobby-list-table">
        <thead class="table-item-header">
            <th class="table-name">{{t "lobby.list.name"}}</th>

            <th></th>
        </thead>
        <tbody class="table-list-item-container">

        </tbody>
    </table>
</script>

<script type="text/mustache" id="tableLobbyListTemplate">
    <table class="table lobby-list-table dataTable">
        <thead class="table-item-header">
            <tr>
                <th class="table-name name-sort sorting">{{t "lobby.list.name"}}</th>
                <th class="seated capacity-sort sorting">{{t "lobby.list.seated"}}</th>
                <th class="blinds blinds-sort sorting">{{t "lobby.list.blinds"}}</th>
                <th class="play-text"></th>
            </tr>
        </thead>
        <tbody class="table-list-item-container">

        </tbody>
    </table>
</script>

<script type="text/mustache" id="tableListItemTemplate" style="display: none;">
    <tr class="table-item  {{tableStatus}}" id="tableItem{{id}}">
        <td class="table-name">{{name}}</td>
        <td class="seated">{{seated}}/{{capacity}}</td>
        <td class="blinds">{{currencyMultiple smallBlind bigBlind '/' currencyCode}}</td>
        <td class="play-text hidden-phone" ><a class="btn btn-lobby" >{{t "lobby.list.go-to-table"}}</a></td>
    </tr>
</script>
<script  type="text/mustache" id="sitAndGoListItemTemplate" style="display: none;">
    <tr class="table-item sit-and-go  {{tableStatus}}" id="sitAndGoItem{{id}}">
        <td class="table-name" colspan="3">
            <div class="list-item-name">
                {{name}}
                {{#requiresLevel}}
                <div class="requires-level">
                    {{renderLock level}}
                    <div class="level level-{{level}}"></div>
                </div>
                {{/requiresLevel}}
            </div>
            <div class="lobby-item-details">
                <div class="list-item status {{status}}">{{status}}</div>
                <div class="list-item buy-in">{{currencyMultiple buyIn fee '+' buyInCurrencyCode}}</div>
                <div class="list-item seated">{{registered}}/{{capacity}}</div>
            </div>
        </td>
        <td class="play-text"><a class="btn btn-lobby">{{t "lobby.list.go-to-lobby"}}</a></td>
    </tr>
</script>
<script type="text/mustache" id="tournamentListItemTemplate" style="display: none;">
    <tr class="table-item tournament {{tableStatus}}" id="tournamentItem{{id}}">
        <td class="table-name">
            <div class="list-item-name">
                {{name}}


            </div>
            <div class="list-item">{{date startTime}}</div>
            <div class="lobby-item-details">
                <div class="list-item status {{status}}">{{status}}</div>
                <div class="list-item">{{currencyMultiple buyIn fee '+' buyInCurrencyCode}}</div>
                <div class="list-item">{{registered}}/{{capacity}}</div>


            </div>
        </td>

        <td class="play-text"><a class="btn btn-lobby">{{t "lobby.list.go-to-lobby"}}</a></td>
    </tr>
</script>
<div id="potTransferTemplate" style="display: none;">
        <div id="{{ptId}}" class="pot-transfer" style="visibility: hidden;">
        <div class="balance pot-container"><div class="value"><span>{{amount}}</span></div></div>
    </div>
</div>

<script type="text/mustache" id="tableViewTemplate" style="display:none;">
    <div id="tableView-{{tableId}}" class="table-container">
        <div class="table-logo"></div>
        <div id="seatContainer-{{tableId}}" class="default-poker-table table-{{capacity}}">
            <div class="seat" id="seat0-{{tableId}}">

            </div>
            <div class="seat" id="seat1-{{tableId}}">

            </div>
            <div class="seat" id="seat2-{{tableId}}">

            </div>
            <div class="seat" id="seat3-{{tableId}}">

            </div>
            <div class="seat" id="seat4-{{tableId}}">

            </div>
            <div class="seat" id="seat5-{{tableId}}">

            </div>
            <div class="seat" id="seat6-{{tableId}}">

            </div>
            <div class="seat" id="seat7-{{tableId}}">

            </div>
            <div class="seat" id="seat8-{{tableId}}">

            </div>
            <div class="seat" id="seat9-{{tableId}}">

            </div>
            <div class="action-button action-leave" style="display: none;">
                <span>{{t "table.buttons.leave"}}</span>
            </div>
            <div class="my-player-seat" id="myPlayerSeat-{{tableId}}">

            </div>
                <div class="click-area-0">

                </div>
            <div class="table-info" style="display:none;">
                <div class="blinds">
                    {{t "table.blinds" }} <span class="table-blinds-value value">10/20</span>
                </div>
                <div class="tournament-info">
                    <div class="time-to-next-level" style="display:none;">
                        {{t "table.level"}} <span class="time-to-next-level-value time"></span>
                    </div>
                </div>
            </div>
            <div class="community-cards">

            </div>
            <div class="total-pot">
                {{t "table.pot" }} <span><span class="amount"></span></span>
            </div>
            <div class="main-pot">

            </div>
            <div class="dealer-button" style="display:none;">
                <img src="${cp}/skins/${skin}/images/table/dealer-button.png"/>
            </div>
        </div>
        <div class="hand-history" style="display:none;">
            {{t "table.hand-history" }}
        </div>

        <div class="bottom-bar">
            <ul class="table-log-tabs">
                <li class="show-log-tab active"><a>Dealer</a></li>
                <li class="show-chat-tab">
                    <a>Chat <span class="new-chat-messages" style="display:none;">+1</span></a>

                </li>
            </ul>
            <div class="table-log-container">
                <div class="table-event-log-settings" style="display:none;"></div>
                <div class="table-event-log-container">
                    <div class="table-event-log">
                    </div>
                </div>
                <div class="table-chat-container" style="display:none;">
                    <div class="table-event-log">
                    </div>
                    <input type="text" class="chat-input describe" title="{{t 'table.log.chat-input-desc'}}"/>
                </div>

            </div>
            <div class="own-player" id="myPlayerSeat-{{tableId}}Info" style="display:none;">
                <div class="name" id="myPlayerName-{{tableId}}"></div>
                <div class="balance" id="myPlayerBalance-{{tableId}}"></div>
                <div class="no-more-blinds">
                    <input class="checkbox" type="checkbox" id="noMoreBlinds-{{tableId}}"/>
                    <label class="checkbox-icon-label" for="noMoreBlinds-{{tableId}}">
                        {{t "table.buttons.no-more-blinds" }}
                    </label>
                </div>
                    <div class="sit-out-next-hand">
                        <input class="checkbox" type="checkbox" id="sitOutNextHand-{{tableId}}"/>
                        <label class="checkbox-icon-label" for="sitOutNextHand-{{tableId}}">
                            {{t "table.buttons.sit-out-next"}}
                        </label>
                    </div>
            </div>



            <div id="userActActions-{{tableId}}" class="user-actions">
                <div class="action-button action-fold"  style="display: none;">
                    <span>{{t "table.buttons.fold"}}</span>
                </div>
                <div class="action-button action-call" style="display: none;">
                    <span class="amount"></span>
                    <span>{{t "table.buttons.call"}}</span>
                </div>
                <div class="action-button action-bring-in" style="display: none;">
                    <span class="amount"></span>
                    <span>{{t "table.buttons.bring-in"}}</span>
                </div>
                <div class="action-button action-check"  style="display: none;">
                    <span>{{t "table.buttons.check"}}</span>
                </div>
                <div class="action-button action-raise" style="display: none;">
                    <span class="amount"></span>
                    <span>{{t "table.buttons.raise"}}</span>
                </div>
                <div class="action-button action-bet"  style="display: none;">
                    <span class="amount"></span>
                    <span>{{t "table.buttons.bet"}}</span>
                </div>
                <div class="action-button action-big-blind"  style="display: none;">
                    <span class="amount"></span>
                    <span>{{t "table.buttons.big-blind"}}</span>
                </div>
                <div class="action-button action-small-blind"  style="display: none;">
                    <span class="amount"></span>
                    <span>{{t "table.buttons.small-blind"}}</span>
                </div>

                <div class="action-button fixed-action-bet" style="display:none;">
                    <span class="amount"></span>
                    <span>{{t "table.buttons.bet"}}</span>
                </div>
                <div class="action-button fixed-action-raise" style="display:none;">
                    <span class="amount"></span>
                    <span>{{t "table.buttons.raise-to"}}</span>
                </div>
                <div class="action-button action-join"style="display: none;">
                    <span>{{t "table.buttons.join"}}</span>
                </div>

                <div class="action-button action-sit-in" style="display: none;">
                    <span>{{t "table.buttons.sit-in"}}</span>
                </div>
                <div class="action-button action-rebuy" style="display: none;">
                    <span>{{t "table.buttons.rebuy"}}</span>
                </div>
                <div class="action-button action-decline-rebuy" style="display: none;">
                    <span>{{t "table.buttons.decline"}}</span>
                </div>
                <div class="action-button action-add-on" style="display: none;">
                    <span>{{t "table.buttons.add-on"}}</span>
                </div>
                <div class="action-button action-discard" style="display: none;">
                    <span>{{t "table.buttons.discard"}}</span>
                </div>
            </div>
            <div id="futureActions-{{tableId}}" class="future-actions" style="display:none;">
                    <div class="future-action check" style="display:none;">
                        <input class="checkbox" type="checkbox" id="future-check-{{tableId}}"/>
                        <label class="checkbox-icon-label" for="future-check-{{tableId}}">{{t "table.future.check"}}</label>
                    </div>

                    <div class="future-action check-or-fold" style="display:none;">
                        <input class="checkbox" type="checkbox" id="future-check-or-fold-{{tableId}}"/>
                        <label class="checkbox-icon-label" for="future-check-or-fold-{{tableId}}">{{t "table.future.check-fold"}}</label>
                    </div>

                    <div class="future-action call-current-bet" style="display:none;">
                        <input class="checkbox" type="checkbox" id="future-call-current-bet-{{tableId}}"/>
                        <label class="checkbox-icon-label" for="future-call-current-bet-{{tableId}}">{{t "table.future.call"}} <span class="amount"></span></label>
                    </div>

                    <div class="future-action check-or-call-any" style="display:none;">
                        <input class="checkbox" type="checkbox" id="future-check-or-call-any-{{tableId}}"/>
                        <label class="checkbox-icon-label" for="future-check-or-call-any-{{tableId}}">{{t "table.future.check-call-any"}}</label>
                    </div>
                    <div class="future-action call-any" style="display:none;">
                        <input class="checkbox" type="checkbox" id="future-call-any-{{tableId}}"/>
                        <label class="checkbox-icon-label" for="future-call-any-{{tableId}}">{{t "table.future.call-any"}}</label>
                    </div>

                    <div class="future-action fold" style="display:none;">
                        <input class="checkbox" type="checkbox" id="future-fold-{{tableId}}"/>
                        <label class="checkbox-icon-label" for="future-fold-{{tableId}}">{{t "table.future.fold"}}</label>
                    </div>

                    <div class="future-action raise" style="display:none;">
                        <input class="checkbox" type="checkbox" id="future-raise-{{tableId}}"/>
                        <label class="checkbox-icon-label" for="future-raise-{{tableId}}">{{t "table.future.raise-to"}} <span class="amount"></span></label>
                    </div>

                    <div class="future-action raise-any" style="display:none;">
                        <input class="checkbox" type="checkbox" id="future-raise-any-{{tableId}}"/>
                        <label class="checkbox-icon-label" for="future-raise-any-{{tableId}}">{{t "table.future.raise-any"}}</label>
                    </div>

            </div>

            <div id="waitForBigBlind-{{tableId}}" class="wait-for-big-blind" style="display:none;">
                <input class="checkbox" type="checkbox" id="wait-for-big-blind-cb-{{tableId}}" checked="checked"/>
                <label class="checkbox-icon-label" for="wait-for-big-blind-cb-{{tableId}}">{{t "table.wait-for-big-blind"}}</label>
                <div>{{t "table.wait-for-big-blind-description"}}</div>
            </div>
        <div id="myPlayerSeat-{{tableId}}Progressbar" class="canvas-progress-bar">
            <canvas width="20" height="20"></canvas>
        </div>

    </div>
</div>
</script>
<script type="text/mustache" id="notificationTemplate" style="display: none;">
    {{text}}
    <div class="notification-actions">
    </div>
</script>
<div id="disconnectDialog" style="display: none;">
    <h1 data-i18n="disconnect-dialog.title"></h1>
    <p class="message disconnect-reconnecting">
        <span data-i18n="disconnect-dialog.message"></span> (<span data-i18n="disconnect-dialog.attempt"></span> <span class="reconnectAttempt"></span>)
        <br/>
        <br/>
    </p>
    <p class="stopped-reconnecting" style="display: none;" data-i18n="disconnect-dialog.unable-to-reconnect">
    </p>
    <p class="dialog-buttons stopped-reconnecting" style="display: none;">
        <a class="dialog-ok-button" data-i18n="disconnect-dialog.reload">
        </a>
    </p>
</div>
<div id="buyInDialog" style="display: none;">
</div>
<div id="genericDialog" style="display: none;">
    <h1>Header</h1>
    <p class="message">Message</p>
    <p class="dialog-buttons">
            <a class="dialog-cancel-button" style="display:none;" data-i18n="generic-dialog.cancel">
               Cancel
            </a>
            <a class="dialog-ok-button" data-i18n="generic-dialog.continue">
                Continue
            </a>
    </p>

</div>
<script type="text/mustache" id="tournamentBuyInContent">
        <h1>{{t "buy-in.buy-in-at"}} {{name}}</h1>
        <div class="buy-in-row">
            <span class="desc">{{t "buy-in.your-balance" }}</span>  <span class="balance buyin-balance">{{currencySymbol balance currencyCode}}</span>
        </div>
        <div class="buy-in-row">
            <span class="desc">{{t "buy-in.buy-in" }}</span>  <span class="balance buyin-max-amount">{{currencyMultiple buyIn fee '+' currencyCode}}</span>
        </div>
        <div class="buy-in-row">
            <span class="buyin-error" style="display: none;"></span>
        </div>
        <p class="dialog-buttons">
            <a class="dialog-cancel-button">
                {{t "buy-in.cancel" }}
        </a>
        <a class="dialog-ok-button">
                {{t "buy-in.ok-button" }}
        </a>
    </p>
</script>
<script type="text/mustache" id="cashGamesBuyInContent">
    <h1>Buy-in at table <span class="buyin-table-name">{{title}}</span></h1>
    <div class="buy-in-row">
        <span class="desc">{{t "buy-in.your-balance" }}</span>  <span class="balance buyin-balance">{{currencyAmountSymbol balance currencyCode}}</span>
    </div>
    <div class="buy-in-row max-amount-container">
        <span class="desc">{{t "buy-in.max-amount" }}</span>  <span class="balance buyin-max-amount">{{currencyAmountSymbol maxAmount currencyCode}}</span>
    </div>
    <div class="buy-in-row">
        <span class="desc">{{t "buy-in.min-amount" }}</span>  <span class="balance buyin-min-amount">{{currencyAmountSymbol minAmount currencyCode}}</span>
    </div>

    <div class="buy-in-row input-container">
        <span class="desc">{{t "buy-in.buy-in-amount" }}</span>
        <input type="text" class="buyin-amount dialog-input" value="" />
    </div>
    <div class="buy-in-row buy-in-amount-errors">
        <span class="insufficient-funds" style="display: none;">
            {{t "buy-in.insufficient-funds"}}
        </span>
        <span class="too-much-funds" style="display: none;">
            {{t "buy-in.too-much-funds"}}
        </span>
    </div>
    <div class="buy-in-row">
        <span class="buyin-error" style="display: none;"></span>
    </div>
    <p class="dialog-buttons">
        <a class="dialog-cancel-button">
            {{t "buy-in.cancel" }}
        </a>
        <a  class="dialog-ok-button">
            {{t "buy-in.ok-button" }}
        </a>
    </p>
</script>
<script type="text/mustache" id="menuItemTemplate">
    <li class="{{cssClass}}">
        <div class="icon">
        </div>
        <div class="text">
            {{title}}
            <span class="description">{{description}}</span>
        </div>
    </li>

</script>
<script type="text/mustache" id="tabTemplate">
    <li>
        <div class="tab-content">
            <div class="tab-index"></div>
            <div class="mini-cards"></div>
            <span class="name">{{name}}</span>
        </div>
    </li>
</script>
<script type="text/mustache" id="miniCardTemplate" style="display: none;">
    <div id="miniCard-{{domId}}" class="mini-card-container">
         <img src="${cp}/skins/${skin}/images/cards/{{cardString}}.svg"/>
    </div>
</script>
<script type="text/mustache" id="tournamentTemplate" style="display:none;">
    <div id="tournamentView{{tournamentId}}" class="tournament-view responsive-view">
        <div class="container">
            <div class="row">
                <a class="register-button leave-action">{{t "tournament-lobby.close" }}</a>
            </div>
            <div class="row">
                <div class="col-sm-7">
                    <h3 class="tournament-name">
                        <div  style="display:inline-block;">
                           <span class="tournament-name-title">{{name}}</span>
                            <div class="requires-level" style="display:none;">
                                <div class="lock"></div>
                                <div class="level"></div>
                            </div>
                        </div>

                    </h3>
                    <h4 class="tournament-start-date"></h4>

                    <p class="tournament-description"></p>
                    <a class="register-button register-action">{{t "tournament-lobby.register" }}</a>
                    <a class="register-button unregister-action">{{t "tournament-lobby.unregister" }}</a>
                    <a class="register-button take-seat-action">{{t "tournament-lobby.go-to-table" }}</a>
                    <a class="register-button loading-action">{{t "tournament-lobby.please-wait" }}</a>
                    <span class="tournament-full">{{t "tournament-lobby.tournament-full"}}</span>
                </div>
                <div class="col-sm-5">
                    <div class="info-section tournament-info"></div>
                </div>
            </div>
            <div class="row players-row">
                <div class="col-sm-12">
                <nav class="navbar-inverse tournament-navbar">
                    <div class="navbar-header">
                        <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target=".navbar-tournament-collapse">
                            <span class="sr-only">Toggle navigation</span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                        </button>
                        <a class="nav-active-item"></a>
                    </div>
                    <div class="navbar-collapse navbar-tournament-collapse collapse">
                        <ul class="nav nav-pills">
                            <li class="players-link active"><a>{{t "tournament-lobby.menu.players" }}</a></li>
                            <li class="tables-link"><a>{{t "tournament-lobby.menu.tables" }}</a></li>
                            <li class="payouts-link"><a>{{t "tournament-lobby.menu.payouts" }}</a></li>
                            <li class="blinds-link"><a>{{t "tournament-lobby.menu.blinds-structure" }}</a></li>
                        </ul>
                    </div>

                    </nav>
                </div>
            </div>
            <div class="row">
                 <div class="col-sm-7">
                     <div class="row players-row tournament-section">
                        <div class="col-sm-12">
                            <div class="tournament-statistics">
                                <div>Remaining players: <span class="remaining-players"></span></div>

                            </div>
                            <div>
                                <input type="text" class="filter-input" placeholder="{{t 'tournament-lobby.players.search' }}"/>
                            </div>
                            <table class="table default-table player-list">
                                <thead>
                                <tr>
                                    <th colspan="2">{{t "tournament-lobby.players.player" }}</th>
                                    <th>{{t "tournament-lobby.players.stack" }}</th>
                                    <th class="winnings">{{t "tournament-lobby.players.winnings" }}</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td colspan="4">{{t "tournament-lobby.players.loading" }}</td>
                                </tr>
                                </tbody>
                            </table>
                            <div class="player-list-pager">

                            </div>
                        </div>
                    </div>
                    <div class="row tables-row tournament-section" style="display:none;">
                        <div class="col-sm-12">
                            <table class="table default-table table-list">
                                <thead>
                                <tr>
                                    <th colspan="2">{{t "tournament-lobby.tables.tables" }}</th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td colspan="2">{{t "tournament-lobby.tables.no-tables" }}</td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <div class="row payouts-row tournament-section" style="display:none;">
                        <div class="col-sm-12 payout-structure">

                        </div>
                    </div>

                    <div class="row blinds-row tournament-section"  style="display:none;">
                        <div class="col-sm-12 blinds-structure">

                        </div>
                    </div>
                 </div>
                 <div class="col-sm-5">
                     <div class="row chat-row">
                        <div class="col-sm-12">
                            <div class="table-chat-container">
                                <div class="lobby-chat table-event-log">
                                </div>
                                <input type="text" class="chat-input describe" placeholder="{{t 'table.log.chat-input-desc'}}">
                            </div>
                        </div>
                     </div>

                 </div>
            </div>
        </div>
    </div>
</script>
<script type="text/mustache" id="tournamentInfoTemplate">
    <h4 class="icon-header">Tournament info</h4>
    {{^sitAndGo}}
    <div class="stats-item">{{t "tournament-lobby.info.registration-starts" }} <span>{{date registrationStartTime}}</span></div>
    {{/sitAndGo}}
    <div class="stats-item">{{t "tournament-lobby.tournament-id"}} <span>{{tournamentId}}</span> </div>
    <div class="stats-item">{{t "tournament-lobby.info.game-type" }} <span>{{gameType}}</span></div>
    <div class="stats-item">{{t "tournament-lobby.info.buy-in" }} <span>{{currencyMultiple buyIn fee '+' buyInCurrencyCode}}</span></div>
    <div class="stats-item">
        {{t "tournament-lobby.info.status" }}
            <span class="status-container">
                <span class="status-0-{{tournamentStatus}}">{{t "tournament-lobby.info.announced" }}</span>
                <span class="status-1-{{tournamentStatus}}">{{t "tournament-lobby.info.registering" }}</span>
                <span class="status-2-{{tournamentStatus}}">{{t "tournament-lobby.info.running" }}</span>
                <span class="status-3-{{tournamentStatus}}">{{t "tournament-lobby.info.break" }}</span>
                <span class="status-4-{{tournamentStatus}}">{{t "tournament-lobby.info.break" }}</span>
                <span class="status-5-{{tournamentStatus}}">{{t "tournament-lobby.info.finished" }}</span>
                <span class="status-6-{{tournamentStatus}}">{{t "tournament-lobby.info.cancelled" }}</span>
                <span class="status-7-{{tournamentStatus}}">{{t "tournament-lobby.info.closed" }}</span>
            </span>
    </div>
    {{#sitAndGo}}
    <div class="stats-item">{{t "tournament-lobby.info.players" }} <span>{{minPlayers}}</span></div>
    {{/sitAndGo}}
    {{^sitAndGo}}
    <div class="stats-item">{{t "tournament-lobby.info.max-players" }} <span>{{maxPlayers}}</span> {{t "tournament-lobby.info.min-players" }} <span>{{minPlayers}}</span> </div>
    {{/sitAndGo}}
    <div class="stats-item">{{t "tournament-lobby.payouts.prize-pool" }}  <span>{{currency prizePool}}</span></div>


</script>
<script type="text/mustache" id="tournamentStatsTemplate">
    <h4>{{t "tournament-lobby.stats.title" }}</h4>
    <div class="stats-item">{{t "tournament-lobby.stats.max-stack" }} <span>{{chipStatistics.maxStack}}</span></div>
    <div class="stats-item">{{t "tournament-lobby.stats.min-stack" }} <span>{{chipStatistics.minStack}}</span></div>
    <div class="stats-item">{{t "tournament-lobby.stats.average-stack" }} <span>{{chipStatistics.averageStack}}</span></div>
    <div class="stats-item">{{t "tournament-lobby.stats.current-level" }} <span>{{levelInfo.currentLevel}}</span></div>
    <div class="stats-item">{{t "tournament-lobby.stats.players-left" }} <span>{{playersLeft.remainingPlayers}}/{{playersLeft.registeredPlayers}}</span></div>
</script>
<script type="text/mustache" id="handHistoryViewTemplate">

    <div id="handHistoryView{{id}}" class="hand-history-container" style="display:none;">
        <h1>{{t "hand-history.title"}}<a class="close-button default-btn">{{t "hand-history.close"}}</a></h1>

        <div class="hand-ids-container">
            <div class="hand-ids-header">
                <div class="start-time">{{t "hand-history.start-time"}}</div>
                <div class="table-name">{{t "hand-history.table-name"}}</div>
            </div>
            <div class="hand-ids">
            </div>
        </div>
        <div class="paging-container">
            <div class="previous">{{t "hand-history.previous"}}</div>
            <div class="next">{{t "hand-history.next"}}</div>
        </div>
        <div class="hand-log">

        </div>
    </div>

</script>
<script type="text/mustache" id="tournamentPayoutStructureTemplate" style="display:none;">

    <table class="table default-table player-list">
        <thead>
            <tr>
                <th> {{t "tournament-lobby.payouts.position" }}</th>
                <th>{{t "tournament-lobby.payouts.amount" }}</th>
            </tr>
        </thead>
        <tbody class="">
            {{#payouts}}
            <tr class="payout info-list-item">
                <td>{{position}}</td>
                <td>{{currency payoutAmount}}</td>
            </tr>
            {{/payouts}}
        </tbody>
    </table>

</script>
<script type="text/mustache" id="handHistoryIdsTemplate" style="display:none;">
   <p class="no-hands" style="display:none;">
       {{t "hand-history.no-history" }}
   </p>
    <ul>
       {{#summaries}}
        <li id="hand-{{id}}">
            <div class="start-time">{{startTime}}</div>
            <div class="table-name">{{table.tableName}}</div>

        </li>
       {{/summaries}}
   </ul>
</script>
<script type="text/mustache" id="handHistoryLogTemplate" style="display:none;">
    <h2>{{t "hand-history.hand-info" }}</h2>
    <p>
        {{t "hand-history.hand-id" }} <span>{{id}}</span><br/>
        {{t "hand-history.table-name"}} <span>{{table.tableName}}</span><br/>
        {{t "hand-history.table-id" }} <span>{{table.tableId}}</span><br/>
        {{t "hand-history.start-time" }} <span>{{startTime}}</span>
    </p>
    <h2>Seats</h2>
    {{#seats}}
        <div class="seat-group">
            <div>{{t "hand-history.player-name" }} <span>{{name}}</span></div>
            <div>{{t "hand-history.position" }} <span>{{seatId}}</span></div>
            <div>{{t "hand-history.initial-balance" }} <span>{{initialBalance}}</span></div>
        </div>
    {{/seats}}

    <h2>{{t "hand-history.events" }}</h2>
    <div class="events">
        {{#events}}
        <p class="event">
           {{#player}}
                {{name}} {{action}} {{amount.amount}}
                {{#playerCardsDealt}}
                {{t "hand-history.was-dealt" }}
                {{/playerCardsDealt}}
            {{/player}}
            {{#tableCards}}
                {{t "hand-history.community-cards" }}
            {{/tableCards}}
            {{#playerCardsExposed}}
                {{t "hand-history.shows" }}
            {{/playerCardsExposed}}
            {{#playerHand}}
                {{name}} {{t "hand-history.has" }} {{handDescription}}:
            {{/playerHand}}
            {{#bestHandCards}}
                {{text}}
            {{/bestHandCards}}

            {{#cards}}
            {{text}}
            {{/cards}}
        </p>
        {{/events}}
    </div>
    <h2>Results</h2>
   {{#results}}
        {{#res}}
        <p class="results">
            <div>{{t "hand-history.player-name" }} <span>{{name}}</span></div>
            <div>{{t "hand-history.total-bet" }} <span>{{totalBet}}</span></div>
            <div>{{t "hand-history.total-win" }} <span>{{totalWin}}</span></div>
        </p>
       {{/res}}
    {{/results}}

</script>
<script type="text/mustache" id="tournamentBlindsStructureTemplate" style="display:none;">
    <table class="table default-table">
        <thead>
            <tr>
                <th>{{t "tournament-lobby.blinds-structure.blinds" }}</th>
                <th>{{t "tournament-lobby.blinds-structure.duration" }}</th>
            </tr>
        </thead>
        <tbody>
         {{#blindsLevels}}
            <tr>
                <td>
                    {{#isBreak}}
                        {{t "tournament-lobby.blinds-structure.break" }}
                    {{/isBreak}}
                    {{^isBreak}}
                        {{currency smallBlind}}/{{currency bigBlind}}
                    {{/isBreak}}
                </td>
                <td>{{durationInMinutes}}</td>
            </tr>
         {{/blindsLevels}}
        </tbody>
    </table>
</script>

<script type="text/mustache" id="tournamentPlayerListItem" style="display:none;">
    <tr>
        <td class="position">{{position}}</td>
        <td class="player-{{playerId}}"><div class="generic-avatar"></div><div class="level" style="display:none;"></div>{{name}}</td>
        <td>{{currency stackSize}}</td>
        <td class="winnings">{{currency winnings}}</td>
        <td>
            <div class="go-to-table-{{playerId}} btn-lobby" style="display:none;"><span class="go-to-table-label">Table</span> &raquo;</div>
        </td>
    </tr>
</script>
<script type="text/mustache" id="tournamentTableListItem" style="display:none;">
    <tr>
        <td>Table {{index}}</td>
        <td><a class="btn btn-lobby" id="tournamentTable{{id}}">Go to table</a></td>
    </tr>
</script>

<script type="text/mustache" id="playerActionLogTemplate" style="display:none;">
   <div>{{name}} {{action}} {{#showAmount}} {{amount}} {{/showAmount}}</div>
</script>
<script type="text/mustache" id="communityCardsLogTemplate" style="display:none;">
    <div>{{t "table-log.community-cards"}} {{#cards}}&nbsp;{{cardIcon cardString}}{{/cards}}</div>
</script>
<script type="text/mustache" id="playerCardsExposedLogTemplate" style="display:none;">
    <div>{{player.name}} {{t "table-log.shows"}} {{#cards}}&nbsp;{{cardIcon cardString}}{{/cards}}</div>
</script>
<script type="text/mustache" id="playerHandStrengthLogTemplate" style="display:none;">
    <div>{{player.name}} {{t "table-log.has"}} {{#hand}}&nbsp;{{text}}{{/hand}} {{#cardStrings}}&nbsp;{{cardIcon .}}{{/cardStrings}}</div>
</script>
<script type="text/mustache" id="potTransferLogTemplate" style="display:none;">
    <div>{{player.name}} {{t "table-log.wins"}} {{amount}}</div>
</script>
<script type="text/mustache" id="newHandLogTemplate" style="display:none;">
    <div class="hand-started">{{t "table-log.hand-started"}}{{handId}} </div>
</script>
<script type="text/mustache" id="chatMessageTemplate" style="display:none;">
    <div class="chat-message"><span class="chat-player-name">{{player.name}}:</span> {{message}} </div>
</script>
<script type="text/mustache" id="overLayDialogTemplate" style="display:none;">
    <div class="dialog-overlay" id="{{dialogId}}">
        <div class="dialog-content">
        </div>
    </div>
</script>

<!-- UserVoice JavaScript SDK (only needed once on a page) -->
<script>
    (function(){

    var id = "${userVoiceId}";
    if (!id) {
        console.log("No UserVoiceID, skipping user voice");
        return;
    } else {
        var uv=document.createElement('script');
        uv.type='text/javascript';
        uv.async=true;uv.src='//widget.uservoice.com/'+id+'.js';
        var s=document.getElementsByTagName('script')[0];
        s.parentNode.insertBefore(uv,s)
    }
})();

// A tab to launch the Classic Widget -->

    UserVoice = window.UserVoice || [];
    UserVoice.push(['showTab', 'classic_widget', {
        mode: 'feedback',
        primary_color: '#a01800',
        link_color: '#670f00',
        forum_id: 200038,
        tab_label: 'Feedback',
        tab_color: '#a01800',
        tab_position: 'middle-right',
        tab_inverted: false
    }]);
</script>



<!-- Google Analytics -->
<script type="text/javascript">

    var id = "${googleAnalyticsId}";

    var _gaq = _gaq || [];

    if (!id) {
        console.log("No Analytics id, skipping analytics");
    } else {
        _gaq.push(['_setAccount', id]);
        _gaq.push(['_trackPageview']);
        (function() {
            var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
            ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
            var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
        })();

    }

    $.ga = {
        _trackEvent:function(event, action, label, value) {
            if (label == undefined) {
                if (Poker.MyPlayer.id) {
                    label = ""+Poker.MyPlayer.id;
                } else {
                    label = "";
                }
            } else {
                label = ""+label;
            }

            if (value == undefined) {
                if (Poker.OperatorConfig.operatorId) {
                    value = Poker.OperatorConfig.operatorId;
                } else {
                    value = 0;
                }
            }
            _gaq.push(['_trackEvent', event, action, label, value ]);
        }
    };

</script>

</body>
</html>
