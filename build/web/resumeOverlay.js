function changeName(){
    $("#resume_name_reflection").html($("#resume_name").val());
}

function changeAddress(){
    $("#resume_address_reflection").html($("#resume_address").val());
}

function changeState(){
    $("#resume_state_reflection").html($("#resume_state").val());
}

function changeCity(){
    $("#resume_city_reflection").html($("#resume_city").val() + ",&nbsp;" + $("#resume_state").val());
}

