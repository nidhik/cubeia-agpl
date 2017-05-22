<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML>
<html>
<head>
    <title></title>

    <meta name="viewport" content="width=device-width, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="default">

    <link rel="apple-touch-icon" href="${cp}/skins/${skin}/images/lobby/icon.png" />
    <link rel="stylesheet/less" type="text/css" href="${cp}/js/lib/bootstrap/less/bootstrap.less"/>

    <link id="defaultSkinCss" rel="stylesheet/less" type="text/css" href="${cp}/skins/default/less/base.less" />

    <!-- All less files are imported in this base.less-->
    <link id="skinCss" rel="stylesheet/less" type="text/css" href="${cp}/skins/${skin}/less/base.less" />

    <c:if test="${not empty cssOverride}">
        <link id="overrideSkinCss" rel="stylesheet/less" type="text/css" href="${cssOverride}" />
    </c:if>

    <script type="text/javascript"  src="${cp}/js/lib/less-1.4.1.min.js"></script>

</head>
<body>


    <div class="view-port" id="viewPort">
       <div id="loginView" class="loading-view">
           <div class="loading-view-container">
               <div class="login-dialog">
                   <div class="logo-container"></div>
                   <div class="loading-box">
                       <div class="session-timed-out-container">
                           <div class="">
                               Your session has timed out, please <a href="${logoutUrl}">click here </a> to login.
                           </div>
                       </div>

                   </div>
                   <div class="powered-by">
                       <img src="${cp}/skins/default/images/poweredby.png"/>
                   </div>
               </div>
           </div>
       </div>
    </div>

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
