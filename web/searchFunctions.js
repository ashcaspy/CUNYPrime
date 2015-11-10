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


var scheduleObjArray = new Array();
function loadUserSchedules(loaded){
    if (loaded == false){
        var boolvar = false;
        getSchedules(userName, scheduleObjArray, boolvar);
    }
    else if (loaded == true){
        for(var i = 0; i < scheduleObjArray.length; i++)
        {
            var tempTab = {
                dayStart: scheduleObjArray[i].dayStart,
                dayEnd: scheduleObjArray[i].dayEnd,
                hourStart: scheduleObjArray[i].hourStart,
                hourEnd: scheduleObjArray[i].hourEnd,
                openTimes: scheduleObjArray[i].openTimes,
                closedTimes: scheduleObjArray[i].closeTimes,
                selectedDivs: scheduleObjArray[i].selectedDiv,
                valid: true,
            };
            scheduleTabs.push(tempTab);
        }
        $("#footer").html(scheduleTabs[0].openTimes);

        //create divs in schedule footer
        for (var i = 0; i < scheduleTabs.length; i++){
            var $tabDiv = $("<div>").addClass("schedule-tab-selector");
            $tabDiv.attr("id", "schedule-tab-" + i);
            $tabDiv.css({
                "width" : "7vw",
                "height" : "3vh",
                "position" : "relative",
                "left" : 1 + "vw",
                "top" : "0.5vh",
                "background-image" : "url(images/schedule-tab.png)",
                "float" : "left",
                "border-bottom-left-radius" : "10px",
                "border-bottom-right-radius" : "10px",
            });
            var schedName = "Schedule" + (i+1);
            $tabDiv.html("<a href = '#' class = 'tab-links'>" + schedName + "</a>");

            $("#schedule_footer").append($tabDiv);    
            $tabDiv.click(function(e){

                e.preventDefault();
                var index = $(e.target).parent().attr("id").substring(13, $(e.target).parent().attr("id").length);
                loadScheduleTab(index);

            });
        }
        loadScheduleTab(0);
    }
}