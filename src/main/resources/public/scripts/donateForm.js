$('form').submit(function (e) {
    var formData = {};
    $("#donateform").serializeArray().map(function(x){formData[x.name] = x.value;});
    $.post('/pharma/api/donate', JSON.stringify(formData), null, 'json')
        .done(function (data) {
            console.log(data);
        });
});