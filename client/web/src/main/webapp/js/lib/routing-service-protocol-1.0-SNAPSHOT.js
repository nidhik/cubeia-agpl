// I AM AUTO-GENERATED, DON'T CHECK ME INTO SUBVERSION (or else...)

var com = com || {};
com.cubeia = com.cubeia || {};
com.cubeia.games = com.cubeia.games || {};
com.cubeia.games.poker = com.cubeia.games.poker || {};
com.cubeia.games.poker.routing = com.cubeia.games.poker.routing || {};
com.cubeia.games.poker.routing.service = com.cubeia.games.poker.routing.service || {};
com.cubeia.games.poker.routing.service.io = com.cubeia.games.poker.routing.service.io || {};
com.cubeia.games.poker.routing.service.io.protocol = com.cubeia.games.poker.routing.service.io.protocol || {};


com.cubeia.games.poker.routing.service.io.protocol.BalanceInfoRequest = function () {
    this.classId = function () {
        return com.cubeia.games.poker.routing.service.io.protocol.BalanceInfoRequest.CLASSID
    };
    this.save = function () {
        return[]
    };
    this.load = function (a) {
    };
    this.getNormalizedObject = function () {
        var a = {};
        var b;
        a.summary = "com.cubeia.games.poker.routing.service.io.protocol.BalanceInfoRequest";
        a.details = {};
        return a
    }
};
com.cubeia.games.poker.routing.service.io.protocol.BalanceInfoRequest.CLASSID = 9;
com.cubeia.games.poker.routing.service.io.protocol.BalanceInfoResponse = function () {
    this.classId = function () {
        return com.cubeia.games.poker.routing.service.io.protocol.BalanceInfoResponse.CLASSID
    };
    this.balanceInWallet = {};
    this.save = function () {
        var a = new FIREBASE.ByteArray();
        a.writeString(this.balanceInWallet);
        return a
    };
    this.load = function (a) {
        this.balanceInWallet = a.readString()
    };
    this.getNormalizedObject = function () {
        var a = {};
        var b;
        a.summary = "com.cubeia.games.poker.routing.service.io.protocol.BalanceInfoResponse";
        a.details = {};
        a.details.balanceInWallet = this.balanceInWallet;
        return a
    }
};
com.cubeia.games.poker.routing.service.io.protocol.BalanceInfoResponse.CLASSID = 10;
com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderRequestHand = function () {
    this.classId = function () {
        return com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderRequestHand.CLASSID
    };
    this.handId = {};
    this.save = function () {
        var a = new FIREBASE.ByteArray();
        a.writeString(this.handId);
        return a
    };
    this.load = function (a) {
        this.handId = a.readString()
    };
    this.getNormalizedObject = function () {
        var a = {};
        var b;
        a.summary = "com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderRequestHand";
        a.details = {};
        a.details.handId = this.handId;
        return a
    }
};
com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderRequestHand.CLASSID = 3;
com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderRequestHandIds = function () {
    this.classId = function () {
        return com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderRequestHandIds.CLASSID
    };
    this.tableId = {};
    this.count = {};
    this.time = {};
    this.save = function () {
        var a = new FIREBASE.ByteArray();
        a.writeInt(this.tableId);
        a.writeInt(this.count);
        a.writeString(this.time);
        return a
    };
    this.load = function (a) {
        this.tableId = a.readInt();
        this.count = a.readInt();
        this.time = a.readString()
    };
    this.getNormalizedObject = function () {
        var a = {};
        var b;
        a.summary = "com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderRequestHandIds";
        a.details = {};
        a.details.tableId = this.tableId;
        a.details.count = this.count;
        a.details.time = this.time;
        return a
    }
};
com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderRequestHandIds.CLASSID = 1;
com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderRequestHandSummaries = function () {
    this.classId = function () {
        return com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderRequestHandSummaries.CLASSID
    };
    this.tableId = {};
    this.count = {};
    this.time = {};
    this.save = function () {
        var a = new FIREBASE.ByteArray();
        a.writeInt(this.tableId);
        a.writeInt(this.count);
        a.writeString(this.time);
        return a
    };
    this.load = function (a) {
        this.tableId = a.readInt();
        this.count = a.readInt();
        this.time = a.readString()
    };
    this.getNormalizedObject = function () {
        var a = {};
        var b;
        a.summary = "com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderRequestHandSummaries";
        a.details = {};
        a.details.tableId = this.tableId;
        a.details.count = this.count;
        a.details.time = this.time;
        return a
    }
};
com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderRequestHandSummaries.CLASSID = 7;
com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderRequestHands = function () {
    this.classId = function () {
        return com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderRequestHands.CLASSID
    };
    this.tableId = {};
    this.count = {};
    this.time = {};
    this.save = function () {
        var a = new FIREBASE.ByteArray();
        a.writeInt(this.tableId);
        a.writeInt(this.count);
        a.writeString(this.time);
        return a
    };
    this.load = function (a) {
        this.tableId = a.readInt();
        this.count = a.readInt();
        this.time = a.readString()
    };
    this.getNormalizedObject = function () {
        var a = {};
        var b;
        a.summary = "com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderRequestHands";
        a.details = {};
        a.details.tableId = this.tableId;
        a.details.count = this.count;
        a.details.time = this.time;
        return a
    }
};
com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderRequestHands.CLASSID = 5;
com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHand = function () {
    this.classId = function () {
        return com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHand.CLASSID
    };
    this.hand = {};
    this.save = function () {
        var a = new FIREBASE.ByteArray();
        a.writeString(this.hand);
        return a
    };
    this.load = function (a) {
        this.hand = a.readString()
    };
    this.getNormalizedObject = function () {
        var a = {};
        var b;
        a.summary = "com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHand";
        a.details = {};
        a.details.hand = this.hand;
        return a
    }
};
com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHand.CLASSID = 4;
com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHandIds = function () {
    this.classId = function () {
        return com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHandIds.CLASSID
    };
    this.tableId = {};
    this.handIds = {};
    this.save = function () {
        var a = new FIREBASE.ByteArray();
        a.writeInt(this.tableId);
        a.writeString(this.handIds);
        return a
    };
    this.load = function (a) {
        this.tableId = a.readInt();
        this.handIds = a.readString()
    };
    this.getNormalizedObject = function () {
        var a = {};
        var b;
        a.summary = "com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHandIds";
        a.details = {};
        a.details.tableId = this.tableId;
        a.details.handIds = this.handIds;
        return a
    }
};
com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHandIds.CLASSID = 2;
com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHandSummaries = function () {
    this.classId = function () {
        return com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHandSummaries.CLASSID
    };
    this.tableId = {};
    this.handSummaries = {};
    this.save = function () {
        var a = new FIREBASE.ByteArray();
        a.writeInt(this.tableId);
        a.writeString(this.handSummaries);
        return a
    };
    this.load = function (a) {
        this.tableId = a.readInt();
        this.handSummaries = a.readString()
    };
    this.getNormalizedObject = function () {
        var a = {};
        var b;
        a.summary = "com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHandSummaries";
        a.details = {};
        a.details.tableId = this.tableId;
        a.details.handSummaries = this.handSummaries;
        return a
    }
};
com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHandSummaries.CLASSID = 8;
com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHands = function () {
    this.classId = function () {
        return com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHands.CLASSID
    };
    this.tableId = {};
    this.hands = {};
    this.save = function () {
        var a = new FIREBASE.ByteArray();
        a.writeInt(this.tableId);
        a.writeString(this.hands);
        return a
    };
    this.load = function (a) {
        this.tableId = a.readInt();
        this.hands = a.readString()
    };
    this.getNormalizedObject = function () {
        var a = {};
        var b;
        a.summary = "com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHands";
        a.details = {};
        a.details.tableId = this.tableId;
        a.details.hands = this.hands;
        return a
    }
};
com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHands.CLASSID = 6;
com.cubeia.games.poker.routing.service.io.protocol.PokerProtocolMessage = function () {
    this.classId = function () {
        return com.cubeia.games.poker.routing.service.io.protocol.PokerProtocolMessage.CLASSID
    };
    this.packet = [];
    this.save = function () {
        var a = new FIREBASE.ByteArray();
        a.writeInt(this.packet.length);
        a.writeArray(this.packet);
        return a
    };
    this.load = function (b) {
        var a = b.readInt();
        this.packet = b.readArray(a)
    };
    this.getNormalizedObject = function () {
        var a = {};
        var b;
        a.summary = "com.cubeia.games.poker.routing.service.io.protocol.PokerProtocolMessage";
        a.details = {};
        a.details.packet = [];
        for (b = 0; b < this.packet.length; b++) {
            a.details.packet.push(this.packet[b].getNormalizedObject())
        }
        return a
    }
};
com.cubeia.games.poker.routing.service.io.protocol.PokerProtocolMessage.CLASSID = 13;
com.cubeia.games.poker.routing.service.io.protocol.ProtocolObjectFactory = {};
com.cubeia.games.poker.routing.service.io.protocol.ProtocolObjectFactory.create = function (c, a) {
    var b;
    switch (c) {
        case com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderRequestHandIds.CLASSID:
            b = new com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderRequestHandIds();
            b.load(a);
            return b;
        case com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHandIds.CLASSID:
            b = new com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHandIds();
            b.load(a);
            return b;
        case com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderRequestHand.CLASSID:
            b = new com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderRequestHand();
            b.load(a);
            return b;
        case com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHand.CLASSID:
            b = new com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHand();
            b.load(a);
            return b;
        case com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderRequestHands.CLASSID:
            b = new com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderRequestHands();
            b.load(a);
            return b;
        case com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHands.CLASSID:
            b = new com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHands();
            b.load(a);
            return b;
        case com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderRequestHandSummaries.CLASSID:
            b = new com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderRequestHandSummaries();
            b.load(a);
            return b;
        case com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHandSummaries.CLASSID:
            b = new com.cubeia.games.poker.routing.service.io.protocol.HandHistoryProviderResponseHandSummaries();
            b.load(a);
            return b;
        case com.cubeia.games.poker.routing.service.io.protocol.BalanceInfoRequest.CLASSID:
            b = new com.cubeia.games.poker.routing.service.io.protocol.BalanceInfoRequest();
            b.load(a);
            return b;
        case com.cubeia.games.poker.routing.service.io.protocol.BalanceInfoResponse.CLASSID:
            b = new com.cubeia.games.poker.routing.service.io.protocol.BalanceInfoResponse();
            b.load(a);
            return b;
        case com.cubeia.games.poker.routing.service.io.protocol.TournamentIdRequest.CLASSID:
            b = new com.cubeia.games.poker.routing.service.io.protocol.TournamentIdRequest();
            b.load(a);
            return b;
        case com.cubeia.games.poker.routing.service.io.protocol.TournamentIdResponse.CLASSID:
            b = new com.cubeia.games.poker.routing.service.io.protocol.TournamentIdResponse();
            b.load(a);
            return b;
        case com.cubeia.games.poker.routing.service.io.protocol.PokerProtocolMessage.CLASSID:
            b = new com.cubeia.games.poker.routing.service.io.protocol.PokerProtocolMessage();
            b.load(a);
            return b
    }
    return null
};
com.cubeia.games.poker.routing.service.io.protocol.TournamentIdRequest = function () {
    this.classId = function () {
        return com.cubeia.games.poker.routing.service.io.protocol.TournamentIdRequest.CLASSID
    };
    this.name = {};
    this.tags = [];
    this.save = function () {
        var a = new FIREBASE.ByteArray();
        a.writeString(this.name);
        a.writeInt(this.tags.length);
        var b;
        for (b = 0; b < this.tags.length; b++) {
            a.writeString(this.tags[b])
        }
        return a
    };
    this.load = function (a) {
        this.name = a.readString();
        var b;
        var c = a.readInt();
        this.tags = [];
        for (b = 0; b < c; b++) {
            this.tags.push(a.readString())
        }
    };
    this.getNormalizedObject = function () {
        var a = {};
        var b;
        a.summary = "com.cubeia.games.poker.routing.service.io.protocol.TournamentIdRequest";
        a.details = {};
        a.details.name = this.name;
        a.details.tags = [];
        for (b = 0; b < this.tags.length; b++) {
            a.details.tags.push(this.tags[b].getNormalizedObject())
        }
        return a
    }
};
com.cubeia.games.poker.routing.service.io.protocol.TournamentIdRequest.CLASSID = 11;
com.cubeia.games.poker.routing.service.io.protocol.TournamentIdResponse = function () {
    this.classId = function () {
        return com.cubeia.games.poker.routing.service.io.protocol.TournamentIdResponse.CLASSID
    };
    this.id = {};
    this.save = function () {
        var a = new FIREBASE.ByteArray();
        a.writeInt(this.id);
        return a
    };
    this.load = function (a) {
        this.id = a.readInt()
    };
    this.getNormalizedObject = function () {
        var a = {};
        var b;
        a.summary = "com.cubeia.games.poker.routing.service.io.protocol.TournamentIdResponse";
        a.details = {};
        a.details.id = this.id;
        return a
    }
};
com.cubeia.games.poker.routing.service.io.protocol.TournamentIdResponse.CLASSID = 12;