var xhrRequest
function searchCourses(){
    // Set up the request.
    $.ajax({
        type: "GET",
        url: "performclasssearch",
        success: function(data){
            parseCourseResultset(data);
            //alert("ok");
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
                hourStart: scheduleObjArray[i].hoursStart,
                hourEnd: scheduleObjArray[i].hoursEnd,
                openTimes: scheduleObjArray[i].openTimes,
                closedTimes: scheduleObjArray[i].closeTimes,
                selectedDivs: scheduleObjArray[i].selectedDiv,
                valid: scheduleObjArray[i].valid,
                classTimes: scheduleObjArray[i].classTimes,
                selectedCourses: scheduleObjArray[i].selectedCourses,
            };
            scheduleTabs.push(tempTab);
        }
        //create divs in schedule footer
        for (var i = 0; i < scheduleTabs.length; i++){
            if(scheduleTabs[i].valid == true){
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
        }
        var index = -1;
        for (i = 0; i < scheduleTabs.length; i++){
            if (scheduleTabs[i].valid == true){
                loadScheduleTab(i);
                index = i;
                break;
            }
        }
        if (index == -1){
            loadScheduleTab(-1);
            //loadScheduleOverlay();
        }
    }
}
function addCourseToSchedule(course){
    
    alert(course.days);
    // place course in schedule timeslots:
    // parse out days:
    var courseDays = [];
    for (var i = 0; i < course.days.length; i = i + 2){
        var tempDay = course.days.substr(i, 2);
        for (var k = 0; k < days.length; k++){
            if (tempDay == days[k].substr(0, 2))
                courseDays.push(k);
        }
    }
    
    // parse out hours:
    var courseStartTime = Math.floor(parseInt(course.startTime) / 100);
    var courseEndTime = Math.floor(parseInt(course.endTime) / 100);
    alert(courseStartTime + " " + courseEndTime);
    // find timeslot divs that match the days and times
    for (var i = 0; i < courseDays.length; i++){
        for (var k = courseStartTime; k < courseEndTime + 1; k++){
            classTimes.push("timeslot-div-" + courseDays[i] + "-" + k);
            selectedDivs.push("timeslot-div-" + courseDays[i] + "-" + k);
            //$("#timeslot-div-" + courseDays[i] + "-" + k).addClass("timeslot-class-div");
        }
    }
    selectedCourses.push(course);
    setSelectedCourses(userName, selectedCourses);
    setClassTimes(userName, classTimes);
    setSelectedDiv(userName, selectedDivs);
    
    $("#timeslot-list").html("");
    $("#hour-list").html("");
    $("#day-list").html("");
    createDivs();
    alert("ok");
}
