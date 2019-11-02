var registerElement = {
    form: {
        uid: $("#uid"),
        name: $("#name"),
        birth: $("#birth"),
        finger: $("#finger"),
        fingerImg: $("#finger-img"),
        btnSaveCard: $("#btn-save-card"),
        btnFingerprint: $("#btn-get-finger")
    }
};

$(function () {
    registerElement.form.btnSaveCard.click(function (e) {
        e.preventDefault();

        var uid = registerElement.form.uid.val(),
            name = registerElement.form.name.val(),
            birth = registerElement.form.birth.val();

        var content = uid + "|" + name + "|" + birth;
        console.log(content);

        if (uid.length === 0 || name.length === 0 || birth === 0) {
            showToast("All the field are required");
            return;
        }

        stompClient.send("/app/cardSetData", {}, JSON.stringify({ code: "unblock", message: content }));
    });

    registerElement.form.btnFingerprint.click(function (e) {
        e.preventDefault();

        var uid = registerElement.form.uid.val();

        if (uid.length === 0) {
            showToast("All the field are required");
            return;
        }

        stompClient.send("/app/enrollment", {}, JSON.stringify({ code: "enrollment", message: uid }));
    });
});