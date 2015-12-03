var xhrRequest;
function searchCourses(type){
    // Set up the request.
    loadSearchOverlay();
    var values = {
        "college_value": $("#search_school").val(),
        "term_value": $("#search_term").val(),
        "dept_value": $("#search_dept").val(),
        "course_num_value": $("#search_course_num").val(),
        "keyword_value": $("#search_keyword").val(),
        "prof_value": $("#search_prof").val(),
        "reqs": JSON.stringify(courseReqObjs),
        "sched_open": JSON.stringify(scheduleTabs[currentScheduleTab].openTimes),
        "sched_closed": JSON.stringify(scheduleTabs[currentScheduleTab].closedTimes),
        "sched_class": JSON.stringify(scheduleTabs[currentScheduleTab].classTimes),
        "id_num": id_num,
        "search_type": type
    };
    
    $.ajax({
        type: "POST",
        url: "performclasssearch",
        data: values,
        success: function(data){
            fadeSearchOverlay();
            parseCourseResultset(data);
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
    
    var specificStartTime = parseInt(course.startTime) / 100;
    var specificEndTime = parseInt(course.endTime) / 100;
    
    
    //first run once to see if thereare  conflicts
    for (var i = 0; i < selectedCourses.length; i++){
        var tempstart = parseInt(selectedCourses[i].startTime) / 100;
        var tempend = parseInt(selectedCourses[i].endTime) / 100;
        if ((specificStartTime > tempstart && specificStartTime < tempend) ||(specificEndTime > tempstart && specificEndTime < tempend)){
            var tempdays = [];
            for (var j = 0; j < selectedCourses[i].days.length; j = j + 2){
                var tempDay = selectedCourses[i].days.substr(j, 2);
                for (var l = 0; l < days.length; l++){
                    if (tempDay == days[l].substr(0, 2))
                        tempdays.push(l);
                }
            }
            for (var k = 0; k < courseDays.length; k++){
                if ($.inArray(courseDays[k], tempdays) != -1){
                    alert("This course conflicts with another course in your schedule. Remove that course before adding this one.");
                    return;
                }
            }            
        }
    }
    
    // find timeslot divs that match the days and times
    for (var i = 0; i < courseDays.length; i++){
        for (var k = courseStartTime; k < courseEndTime + 1; k++){
            
            if ($.inArray(("timeslot-div-" + courseDays[i] + "-" + k), classTimes) == -1){
                        
                classTimes.push("timeslot-div-" + courseDays[i] + "-" + k);
                selectedDivs.push("timeslot-div-" + courseDays[i] + "-" + k);
                scheduleTabs[currentScheduleTab].classTimes.push("timeslot-div-" + courseDays[i] + "-" + k);
                scheduleTabs[currentScheduleTab].selectedDivs.push("timeslot-div-" + courseDays[i] + "-" + k);

                if ($.inArray(("timeslot-div-" + courseDays[i] + "-" + k), closedTimes) > -1){
                    closedTimes.splice($.inArray(("timeslot-div-" + courseDays[i] + "-" + k), closedTimes), 1);
                    scheduleTabs[currentScheduleTab].closedTimes.splice($.inArray(("timeslot-div-" + courseDays[i] + "-" + k), scheduleTabs[currentScheduleTab].closedTimes), 1);
                }
                if ($.inArray(("timeslot-div-" + courseDays[i] + "-" + k), openTimes) > -1){
                    openTimes.splice($.inArray(("timeslot-div-" + courseDays[i] + "-" + k), openTimes), 1);
                    scheduleTabs[currentScheduleTab].openTimes.splice($.inArray(("timeslot-div-" + courseDays[i] + "-" + k), scheduleTabs[currentScheduleTab].openTimes), 1);
                }
            }
        }
    }
    
    selectedCourses.push(course);
    setSelectedCourses(userName, selectedCourses);
    setClassTimes(userName, classTimes);
    setSelectedDiv(userName, selectedDivs);
    courseInfoArray[3] = selectedCourses;
    numCoursesPerList[3] = courseInfoArray[3].length;
    
   
    
    $("#timeslot-list").html("");
    $("#hour-list").html("");
    $("#day-list").html("");
    createDivs();
    loadList(3);
    loadList(3);
}


function removeCourseFromSchedule(course){
    if (confirm("Remove this course?")){
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
        // find timeslot divs that match the days and times
        for (var i = 0; i < courseDays.length; i++){
            for (var k = courseStartTime; k < courseEndTime + 1; k++){
                classTimes.splice($.inArray("timeslot-div-" + courseDays[i] + "-" + k, classTimes), 1);
                selectedDivs.splice($.inArray("timeslot-div-" + courseDays[i] + "-" + k, selectedDivs), 1);
                //$("#timeslot-div-" + courseDays[i] + "-" + k).addClass("timeslot-class-div");
            }
        }

        selectedCourses.splice($.inArray(course, selectedCourses), 1);
        setSelectedCourses(userName, selectedCourses);
        setClassTimes(userName, classTimes);
        setSelectedDiv(userName, selectedDivs);
        courseInfoArray[3] = selectedCourses;
        numCoursesPerList[3] = courseInfoArray[3].length;

        $("#timeslot-list").html("");
        $("#hour-list").html("");
        $("#day-list").html("");
        createDivs();
        loadList(3);
        loadList(3);
    }
}


var collegesLoaded = false;
function getCollegeList(){
    if (!collegesLoaded){
        $.ajax({
            type: "POST",
            url: "getcollegelist",
            success: function(data){
                $("#search_school").html($("#search_school").html() + data);
                collegesLoaded = true;
            }
        });
    }
}

function getTermAndDepts(){
    var value = $("#search_school").val();
    
    $.ajax({
        type: "POST",
        url: "gettermanddeptlist",
        data: {"college_value": value},
        success: function(data){
            var results = data.split("SPLIT");
            var defaultValue = "<option value = \"none\">-Select-</option>"
            $("#search_term").html(defaultValue + results[1]);
            $("#search_dept").html(defaultValue + results[0]);
            collegesLoaded = true;
        }
    });
}
