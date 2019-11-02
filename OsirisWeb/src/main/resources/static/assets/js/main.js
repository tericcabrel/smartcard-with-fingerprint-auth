var stompClient = null;

var cardEvent = {
    successCode: "36864",
    cardSelected: "36865",
    cardRemoved: "14000",
    cardLocked: "27010",
    pinRequired: "25345"
};

var biometricEvent = {
    successCode: "12500",
    uploadFailed: "12400",
    captureFailed: "12300",
    noScannerAvailable: "12200",
    sdkNotInitialized: "12100"
};

var messageCodes = {
    "36864": "Executed successfully!",
    "36865": "A card inserted !",
    "14000": "The card has been removed",
    "15000": "Card Internal error",
    "25344": "Invalid PIN Code",
    "25345": "Authentication with PIN Code is required!",
    "27010": "The card is locked!",
    "12100": "Library is not initialized",
    "12200": "No fingerprint scanner found",
    "12300": "Fingerprint capture failed",
    "12400": "Failed to send the file ont the server",
    "12500": "Fingerprint registered successfully !"
};

var toastifyOptions = {
    text: "",
    duration: 5000,
    newWindow: true,
    close: true,
    className: "osiris-toast",
    gravity: "top", // `top` or `bottom`
    position: 'center', // `left`, `center` or `right`
    // backgroundColor: "linear-gradient(to right, #00b09b, #96c93d)",
    stopOnFocus: true, // Prevents dismissing of toast on hover
    onClick: function(){} // Callback after click
};

var element = {
    sidebar: {
      btnViewCard: $("#btn-view-card-info"),
      btnBiometricAuth: $("#btn-biometric-auth")
    },
    pinModal: {
        id: $("#pin-modal"),
        input: $("#input-pin"),
        btnPin: $("#btn-send-pin")
    },
    userModal: {
        id: $("#user-modal"),
        btnReset: $("#btn-reset"),
        btnUnblock: $("#btn-unblock"),
        info: {
            cardUid: $("#card-uid"),
            cardName: $("#card-name"),
            cardBirth: $("#card-birth"),
            btnUpdateName: $("#btn-update-name"),
            btnUpdateBirth: $("#btn-update-birth")
        }
    },
    register: {
        form: {
            uid: $("#uid"),
            name: $("#name"),
            birth: $("#birth"),
            finger: $("#finger"),
            fingerImg: $("#finger-img"),
            btnSaveCard: $("#btn-save-card"),
            btnFingerprint: $("#btn-get-finger")
        }
    },
    fingerprintModal: {
        id: $("#fingerprint-modal"),
        resultBox: $("#finger-result"),
        failedIcon: $("#auth-failed"),
        successIcon: $("#auth-success"),
        welcomeMessage: $("#welcome-message"),
        btnAuth: $("#btn-auth-fingerprint")
    }
};

var CardState = function () {
    this.inserted = 0;
    this.authenticated = 0;
    this.locked = 0;
    this.pinRemain = 3;
    this.storageKey = "osiris_data";

    this.save = function () {
        var data = {
            inserted: this.inserted,
            authenticated: this.authenticated,
            locked: this.locked,
            pinRemain: this.pinRemain
        };

        localStorage.setItem(this.storageKey, JSON.stringify(data));
    };

    this.reset = function () {
        var data = {
            inserted: 0,
            authenticated: 0,
            locked: 0,
            pinRemain: 3
        };

        localStorage.setItem(this.storageKey, JSON.stringify(data));
        this.init();
    };

    this.init = function () {
        var storageData = localStorage.getItem(this.storageKey);
        if (storageData === null) {
            this.save();
        } else {
            var parsed = JSON.parse(storageData);
            this.inserted = parsed.inserted;
            this.authenticated = parsed.authenticated;
            this.locked = parsed.locked;
            this.pinRemain = parsed.pinRemain;
        }
    }
};

var cardState = new CardState();

var getBody = function (data) {
    var body = JSON.parse(data.body);
    console.log('Body => ', body);

    return body.message;
};

var showToast = function (message, logInConsole) {
    if (logInConsole !== undefined) {
        console.log(message);
    }

    toastifyOptions.text = message;
    Toastify(toastifyOptions).showToast();
};

var updateCardItem = function (element, value) {
    var badgeSuccess = "badge-success",
        badgeError = "badge-danger",
        iconSuccess = "fa-check",
        iconError = "fa-times";

    if (value === 1) {
        element.removeClass(badgeError).addClass(badgeSuccess);
        element.children("i").eq(0).removeClass(iconError).addClass(iconSuccess);
    } else {
        element.removeClass(badgeSuccess).addClass(badgeError);
        element.children("i").eq(0).removeClass(iconSuccess).addClass(iconError);
    }
};

var updateCardState = function(cardState) {
    var cardInserted = $("#sd-inserted"),
        cardLocked = $("#sd-locked"),
        cardAuthenticated = $("#sd-auth"),
        cardPinRemain = $("#sd-pin-remain");

    updateCardItem(cardInserted, cardState.inserted);
    updateCardItem(cardLocked, cardState.locked);
    updateCardItem(cardAuthenticated, cardState.authenticated);
    cardPinRemain.text(cardState.pinRemain);
};

var connect = function() {
    var socket = new SockJS('/osiris');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/cardInserted', function (data) {
            var message = getBody(data);
            if (message === cardEvent.cardSelected) {
                cardState.inserted = 1;
                cardState.save();
                updateCardState(cardState);

                showToast(messageCodes[message]);
            } else {
                showToast("Card inserted but failed to connect to it!", true);
            }
        });

        stompClient.subscribe('/topic/cardRemoved', function (data) {
            var message = getBody(data);
            if (message === cardEvent.cardRemoved) {
                cardState.reset();
                updateCardState(cardState);

                showToast(messageCodes[message]);
            } else {
                showToast("Card inserted but failed to connect to it!", true);
            }
        });

        stompClient.subscribe('/topic/pinAuth', function (data) {
            var message = getBody(data);
            if (message === cardEvent.successCode) {
                cardState.authenticated = 1;
                cardState.save();
                updateCardState(cardState);

                showToast("Success: Valid PIN Code");
                element.pinModal.id.modal('hide');
                element.pinModal.input.val('');
            } else {
                cardState.pinRemain--;

                if (cardState.pinRemain <= 0) {
                    showToast("The card is locked!", true);
                    cardState.authenticated = 0;
                    cardState.locked = 1;
                } else {
                    showToast(messageCodes[message] + ": Attempt remaining = " + cardState.pinRemain, true);
                }
                cardState.save();
                updateCardState(cardState);
            }
        });

        stompClient.subscribe('/topic/cardUnblock', function (data) {
            var message = getBody(data);
            if (message === cardEvent.successCode) {
                cardState.locked = 0;
                cardState.pinRemain = 3;
                cardState.authenticated = 0;
                cardState.save();
                updateCardState(cardState);

                showToast("The card has been unblocked successfully!");
                element.userModal.id.modal('hide');
            } else {
                showToast("An error occurred ! Try again later");
            }
        });

        stompClient.subscribe('/topic/cardSetData', function (data) {
            var message = getBody(data);
            if (message === cardEvent.successCode) {
                showToast("Data saved successfully in the card!");
            } else {
                showToast("An error occurred with code: "+ message + " ! Try again later");
            }
        });

        stompClient.subscribe('/topic/cardGetData', function (data) {
            var message = getBody(data);
            var array = message.split('|');
            if (array.length === 4) {
                element.userModal.info.cardUid.text(array[0]);
                element.userModal.info.cardName.val(array[1]);
                element.userModal.info.cardBirth.val(array[2]);
            } else if (message === cardEvent.cardLocked) {
                showToast("The card is locked!");
            } else {
                showToast("An error occurred with code: " + message);
            }
        });

        stompClient.subscribe('/topic/cardSetName', function (data) {
            var message = getBody(data);
            if (message === cardEvent.successCode) {
                showToast("Name updated successfully !");
                // TODO Send request to update in the database
            } else {
                showToast("An error occurred with code: "+ message + " ! Try again later");
            }
        });

        stompClient.subscribe('/topic/cardSetBirth', function (data) {
            var message = getBody(data);
            if (message === cardEvent.successCode) {
                showToast("Birth data updated successfully !");
                // TODO Send request to update in the database
            } else {
                showToast("An error occurred with code: "+ message + " ! Try again later");
            }
        });

        stompClient.subscribe('/topic/cardReset', function (data) {
            var message = getBody(data);

            if (message === cardEvent.successCode) {
                showToast("Card resetted successfully!");
                element.userModal.info.cardUid.text('');
                element.userModal.info.cardName.val('');
                element.userModal.info.cardBirth.val(moment().format('YYYY-MM-DD'));
            } else if (message === cardEvent.cardLocked) {
                showToast(messageCodes[message]);
            } else if (message === cardEvent.pinRequired) {
                showToast(messageCodes[message]);
            } else {
                showToast("An error occurred with code: " + message);
            }
        });

        stompClient.subscribe('/topic/enrollment', function (data) {
            var message = getBody(data);
            if (message === biometricEvent.successCode) {
                showToast(messageCodes[message]);
                element.register.form.finger.val("yes");
                var uid = element.register.form.uid.val();
                console.log('http://localhost:7000/uploads/static/' + uid + '/' + uid + '.bmp');
                element.register.form.fingerImg.attr('src', 'http://localhost:7000/uploads/static/' + uid + '/' + uid + '.bmp');
            } else {
                showToast(messageCodes[message] ? messageCodes[message] : "An error occurred with code: "+ message + "! Try again later");
            }
        });

        stompClient.subscribe('/topic/authFingerprint', function (data) {
            var message = getBody(data);
            var array = message.split('|');
            if (array.length === 4) {
                element.fingerprintModal.resultBox.removeClass('badge-light badge-danger').addClass('badge-success');
                element.fingerprintModal.failedIcon.addClass('hidden');
                element.fingerprintModal.successIcon.removeClass('hidden');
                element.fingerprintModal.welcomeMessage.html('Welcome <b>'+array[1]+'</b>');
            } else {
                // showToast("An error occurred with code: " + message);
                element.fingerprintModal.resultBox.removeClass('badge-light badge-success').addClass('badge-danger');
                element.fingerprintModal.failedIcon.removeClass('hidden');
                element.fingerprintModal.successIcon.addClass('hidden');
            }
        });
    });
};

var disconnect = function() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
};

$(function () {
    cardState.init();

    updateCardState(cardState);

    connect();

    element.pinModal.btnPin.click(function (e) {
        e.preventDefault();

        if (cardState.pinRemain === 0) {
            showToast("The card is locked !");
            return;
        }

        var pinCode = element.pinModal.input.val();
        if (pinCode.length !== 6) {
            return;
        }

        stompClient.send("/app/pinAuthentication", {}, JSON.stringify({ code: "pin", message: pinCode }));
    });

    element.userModal.btnUnblock.click(function (e) {
        e.preventDefault();

        bootbox.confirm({
            title: "Card Unblock",
            message: "Are you sure you want to unblock the card?",
            callback: function (result) {
                if (result) {
                    stompClient.send("/app/cardUnblock", {}, JSON.stringify({ code: "unblock", message: "unblock" }));
                }
            }
        });
    });

    element.sidebar.btnViewCard.click(function (e) {
        e.preventDefault();

        if (cardState.locked === 1) {
            showToast("The card is locked !");
            return;
        }

        stompClient.send("/app/cardGetData", {}, JSON.stringify({ code: "get", message: 'getData' }));
    });

    element.userModal.info.btnUpdateName.click(function (e) {
        e.preventDefault();

        var name = element.userModal.info.cardName.val();

        if (name.length === 1) {
            showToast("The field cannot be empty !");
            return;
        }

        stompClient.send("/app/cardSetName", {}, JSON.stringify({ code: "set", message: name }));
    });

    element.userModal.info.btnUpdateBirth.click(function (e) {
        e.preventDefault();

        var birth = element.userModal.info.cardBirth.val();

        if (birth.length === 1) {
            showToast("The field cannot be empty !");
            return;
        }

        stompClient.send("/app/cardSetBirth", {}, JSON.stringify({ code: "set", message: birth }));
    });

    element.userModal.btnReset.click(function (e) {
       e.preventDefault();

        if (cardState.locked === 1) {
            showToast('The card is locked !');
            return;
        }

       if (cardState.authenticated === 0) {
           showToast('This action required to be authenticated!');
           return;
       }

        stompClient.send("/app/cardReset", {}, JSON.stringify({ code: "reset", message: "reset" }));
    });

    element.fingerprintModal.btnAuth.click(function (e) {
        e.preventDefault();

        element.fingerprintModal.resultBox.removeClass('badge-success badge-danger').addClass('badge-light');
        element.fingerprintModal.failedIcon.addClass('hidden');
        element.fingerprintModal.successIcon.addClass('hidden');
        element.fingerprintModal.welcomeMessage.empty();

        stompClient.send("/app/authFingerprint", {}, JSON.stringify({ code: "authFinger", message: "authFinger" }));
    });
});