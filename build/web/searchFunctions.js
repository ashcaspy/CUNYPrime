var xhrRequest
function searchCourses(){
    // Set up the request.
    $.ajax({
        type: "GET",
        url: "performclasssearch",
        success: function(data){
            $("#footer").html(data);
        }
    });  
}