$(document).ready(function () {
    setPageInfo();
    fillMusicTypesList();
    fillRolesList();
    setActionForEditButton();
    setActionForInfoButton();
    setActionForSaveButton();
    setActionForDeleteButton()
});

function setPageInfo() {
    $.ajax({
        url: '/view',
        type: 'post',
        async: false,
        complete: function (result) {
            var answerObj = JSON.parse(result.responseText);
            var currentUser = JSON.parse(answerObj.currentUser);
            var usersList = JSON.parse(answerObj.users);
            showGreeting(currentUser);
            showUsersList(usersList);
        }
    })
}

function fillMusicTypesList() {
    $.ajax({
        url: '/show_mt',
        type: 'get',
        complete: function (result) {
            var answer = JSON.parse(result.responseText);
            var array = JSON.parse(answer.types);
            array.forEach(function (item) {
                $('#mt').append('<option value="' + item.id + '">' + item.genre + '</option>')
            });
        }
    })
}

function fillRolesList() {
    $.ajax({
        url: '/show_roles',
        type: 'get',
        complete: function (result) {
            var answer = JSON.parse(result.responseText);
            var array = JSON.parse(answer.roles);
            array.forEach(function (item) {
                $('#role').append('<option value="' + item.id + '">' + item.title + '</option>')
            });
        }
    })
}

function setActionForEditButton() {
    $('#edit').on("click", function () {
        $('.form-control[name!="login"]').attr('disabled', false);
        var  userId = $('#id').val();
        $.ajax({
            url: '/edit',
            type: 'get',
            data: {"id": userId},
            complete: function (result) {
                var answerObj = JSON.parse(result.responseText);
                var editUser = JSON.parse(answerObj.editUser);
                var currentUserRole = answerObj.currentUserRole;
                setUserInfoToEditForm(editUser);
                hideUnavailableRoles(editUser.role.id, currentUserRole);
            }
        });
    })
}

function setActionForInfoButton() {
    var btn = $('.info');
    $(btn).on('click', function () {
        var userId = $(this).attr('id');
        $.ajax({
            url: '/edit',
            type: 'get',
            data: {'id': userId},
            complete: function (result) {
                var answerObject = JSON.parse(result.responseText);
                var currentUserRole = answerObject.currentUserRole;
                var editUser = JSON.parse(answerObject.editUser);
                setUserInfoToEditForm(editUser);
                hideUnavailableRoles(editUser.role.id, currentUserRole);
                $('.form-control[id!="role"]').attr('disabled', true);
                if (currentUserRole !== 'USER') {
                    $('#save-btn').attr('disabled', false);
                }
            }
        })
    });
}

function setActionForSaveButton() {
    $('#save-btn').on('click', function () {
        var formData = $('form').serializeArray();
        $.ajax({
            url: '/edit',
            type: 'post',
            data: formData,
            complete: function (result) {
                var answer = JSON.parse(result.responseText);
                if (answer.updated) {
                    document.location.reload();
                }
            }
        })
    })
}

function setActionForDeleteButton() {
    $('#delete').on('click', function () {
        var id = $('#id').val();
        $.ajax({
            url: '/delete',
            data: {'id' : id},
            success: function () {
                document.location.replace('/');
            }
        })
    })
}

function showGreeting(user) {
    $('#greeting').html('<h3> Hello, ' + user.login + '!</h3>');
    $('#id').val(user.id);
}

function showUsersList(array) {
    var table = $('#list_of_users').find('table');
    array.forEach(function (item) {
        table.append('<tr><td>' + item.login + '</td><td>' + item.address.country + '</td><td>' + item.role.title
            + '</td><td><button id="' + item.id + '" class="info btn" data-toggle="modal" data-target="#modal">'
            + 'Info</button></td></tr>');
    })
}

function setUserInfoToEditForm(user) {
    $('option').attr('disabled', false);
    $('[name="login"]').val(user.login);
    $('#password').val(user.password);
    $('#country').val(user.address.country);
    $('#city').val(user.address.city);
    $('#role').find('[value="'+ user.role.id + '"]').attr("selected", true);
    user.types.forEach(function (item) {
        $('#mt').find('[value="'+ item.id + '"]').attr("selected", true);
    })
}

function hideUnavailableRoles(editUserRole, currentUserRole) {
    if (currentUserRole === 'USER' || editUserRole === 1) {
        $('#role').find('[value!="'+ editUserRole + '"]').attr('disabled', true);
    } else if (currentUserRole === 'MANDATOR') {
        $('#role').find('[value="1"]').attr('disabled', true);
    }
}

