window.LottoGamesApi = (function () {
    "use strict";

    const apiUrl = 'http://localhost:8080/api/gameshub/gamevy/';

    const responseToDivCallBack = function () {
    };

    const config = {
        token: function (playerId, token) {
            return {
                url: apiUrl + 'CreateToken',
                payload: {
                    "GameCode": "hydra_not_mobile_sw",
                    "PlayerId": playerId,
                    "Account": {"UserName": "lottolandd", "Password": "lottoland123!"},
                    "Token": token
                },
                success: responseToDivCallBack
            };
        },
        balance: function (token) {
            return {
                url: apiUrl + "GetBalance",
                payload: {
                    "Account": {"UserName": "lottolandd", "Password": "lottoland123!"},
                    "Token": token
                },
                success: responseToDivCallBack
            };
        },
        debit: function (playerId, token, debit, credit, transactionId, roundId) {
            return {
                url: apiUrl + "DebitAndCredit",
                payload: {
                    "RoundId": roundId,
                    "TransactionId": transactionId,
                    "DebitAmount": debit,
                    "CreditAmount": credit,
                    "CreditType": "NormalWin",
                    "Feature": "Normal",
                    "GameCode": "hydra_not_mobile_sw",
                    "PlayerId": playerId,
                    "Account": {"UserName": "lottolandd", "Password": "lottoland123!"},
                    "Token": token
                },
                success: responseToDivCallBack
            };
        }
    };

    function sendDebitCredit() {
        getFromAPI(config.debit(getPlayerId(), getToken(), getStake(), getWin(), uuid(), uuid()));
    }

    function sendBalance() {
        getFromAPI(config.balance(getToken()));
    }

    function sendToken() {
        getFromAPI(config.token(getPlayerId(), getToken()));
    }

    function uuid() {
        function s4() {
            return Math.floor((1 + Math.random()) * 0x10000)
                .toString(16)
                .substring(1);
        }

        return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
            s4() + '-' + s4() + s4() + s4();
    }

    function getPlayerId() {
        return $('#playerID').val();
    }

    function getToken() {
        return $('#token').val();
    }

    function getStake() {
        return $('#stake').val();
    }

    function getWin() {
        return $('#win').val();
    }

    function setOutput(outputText) {
        const timestamp = new Date().toISOString().slice(11, 23) + " ";
        const markup = "<tr><td>" + timestamp + "</td><td>" + outputText + "</td></tr>";
        $('#log').find('tbody').prepend(markup);
    }

    function idCheck() {
        return $('#playerID').val().length >= 6;

    }

    function getFromAPI(config) {
        if (!idCheck()) {
            return;
        }
        const payloadString = JSON.stringify(config.payload);
        console.log("REQUEST: " + payloadString);
        $.ajax({
            contentType: 'application/json; charset=utf-8',
            url: config.url,
            type: 'POST',
            data: payloadString,
            success: function (response) {
                console.log("RESPONSE: " + JSON.stringify(response, null, 2));
            }
        }).done(function (result) {
            config.success(result);
            setOutput("RESPONSE OK: " + JSON.stringify(result, null, 2));
        }).fail(function (err) {
            setOutput('REQUEST FAIL: ' + JSON.stringify(err, null, 2));
        });
    }

    return {
        sendDebitCredit: sendDebitCredit,
        sendBalance: sendBalance,
        sendToken: sendToken
    };
})();