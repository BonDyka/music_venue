$(document).ready(function () {
    fillSelectOptions();

    //remove alert background color for form elements.
    $('.input-item').on('click', function () {
        $(this).removeClass('empty-field');
        $('#msg').text('');
    });

    $('#signin').on('click', function () {
        var data = checkAndPrepareFormData('#auth_form');
        if ($.isEmptyObject(data)) {
            $('#msg').text('All fields must to be filled!');
        } else {
            $.ajax({
                url: '/signin',
                type: 'GET',
                data: data,
                complete: function (result) {
                    var answerObj = JSON.parse(result.responseText);
                    if (answerObj.logged) {
                        document.location.replace(answerObj.nextPage);
                    } else {
                        $('#msg').text(answerObj.msg);
                    }
                }
            });
        }
    });

    $('#signup').on('click', function () {
        var data = checkAndPrepareFormData('#reg_form');
        if ($.isEmptyObject(data)) {
            $('#msg').text('All fields must to be filled!');
        } else {
            $.ajax({
                url : "/signup",
                type : "post",
                data : data,
                complete : function (result) {
                    var answerJson = JSON.parse(result.responseText);
                    $('#msg').text(answerObj.msg);
                }
            });
        }
    });
});

function fillSelectOptions() {
    $.ajax({
        url: '/show_mt',
        type: 'get',
        complete: function (result) {
            var answerObj = JSON.parse(result.responseText);
            var array = JSON.parse(answerObj.types);
            $('select').append(prepareOptionsHTML(array));
        }
    });

}

function prepareOptionsHTML(array) {
    var result = '';
    for (var i = 0; i < array.length; i++) {
        result += '<option value=' + array[i].id + '>' + array[i].genre + '</option>\n\r';
    }
    return result;
}

function checkAndPrepareFormData(formId) {
    var checksResult = true;
    var result = {};
    $(formId).find('.input-item').each(function () {
        var dataValue = $(this).val();
        if (dataValue === '' || dataValue === null) {
            $(this).addClass('empty-field');
            checksResult = checksResult && false;
        }
        result[this.name] = dataValue;
    });
    if (!checksResult) {
        result = {}
    }
    return result;
}