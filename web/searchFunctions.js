var xhrRequest;
function searchCourses(type){
    // Set up the request.
    loadSearchOverlay();
    
    // Validate college name
    var collegeValue = "";
    var openTimes = new Array();
    var closedTimes = new Array();
    var classTimes = new Array();
    var reqs = new Array();
    
    if (document.getElementById("search_school") === null || $("#search_school").val() == "none"){
        alert("Please select a school from the dropdown, or upload a DIG report.");
            
        fadeSearchOverlay();
        return;
    }
    else
        collegeValue = $("#search_school").val();
    
    // Validate open/closed times
    if (currentScheduleTab == -1){
        if (type == "TIME_FOCUSED_SEARCH"){
            alert("You haven't made a schedule yet!");
            fadeSearchOverlay();
            return;
        }
    }
    else if (scheduleTabs[currentScheduleTab].openTimes.length == 0){
        if (type == "TIME_FOCUSED_SEARCH"){
            alert("Please select your availability first!");
            fadeSearchOverlay();
            return;
        }
    }
    else{
        openTimes = scheduleTabs[currentScheduleTab].openTimes;
        closedTimes = scheduleTabs[currentScheduleTab].closedTimes;
        classTimes = scheduleTabs[currentScheduleTab].classTimes;

        for (var i = 0; i < openTimes.length; i++){
            if (openTimes[i].length < 17)
                openTimes[i] = openTimes[i].substring(0, 15) + "0" + openTimes[i].charAt(16);
        }

        for (var i = 0; i < closedTimes.length; i++){
            if (closedTimes[i].length < 17)
                closedTimes[i] = closedTimes[i].substring(0, 15) + "0" + closedTimes[i].charAt(16);
        }

        for (var i = 0; i < classTimes.length; i++){
            if (classTimes[i].length < 17)
                classTimes[i] = classTimes[i].substring(0, 15) + "0" + classTimes[i].charAt(16);
        }
    }
    
    if (type == "REQ_FOCUSED_SEARCH"){
        if ($("#req_list_dropdown").val() == "none"){
            alert("Please upload a DIG report and select a requirement from the dropdown!");
            fadeSearchOverlay();
         return;
        }
        else{
            reqs = courseReqObjs[$("#req_list_dropdown").val()].reqs;
        }
    }
    else if (type == "TIME_FOCUSED_SEARCH"){
        if ($("#req_list_dropdown").val() == "none"){
            for (var i = 0; i < allReqCourses.length; i++){
                reqs.concat(courseReqObjs[i].reqs);
            }
        }
        else
            reqs = courseReqObjs[$("#req_list_dropdown").val()].reqs;
    }
    
            
    
    
    
    // Validate Reqs
    if (allReqCourses.length == 0 && type == "REQ_FOCUSED_SEARCH"){
        alert("Please upload a DIG report with your requirements first!");
        fadeSearchOverlay();
        return;
    }
    
    
    if (type == "DEFAULT_SEARCH"){
    
        var values = {
            "college_value": collegeValue,
            "term_value": $("#search_term").val(),
            "dept_value": $("#search_dept").val(),
            "course_num_value": $("#search_course_num").val(),
            "keyword_value": $("#search_keyword").val(),
            "prof_value": $("#search_prof").val(),
            "id_num": id_num,
            "search_type": type
        };
    }
    else{       
        if (type == "TIME_FOCUSED_SEARCH"){
            var values = {
                "college_value": $("#search_school").val(),
                "term_value": $("#search_term").val(),
                "reqs": JSON.stringify(reqs),
                "sched_open": JSON.stringify(openTimes),
                "sched_closed": JSON.stringify(closedTimes),
                "sched_class": JSON.stringify(classTimes),
                "id_num": id_num,
                "search_type": type
            };
        }
        else if (type == "REQ_FOCUSED_SEARCH"){
            var values = {
                "college_value": $("#search_school").val(),
                "term_value": $("#search_term").val(),
                "reqs": JSON.stringify(reqs),
                "sched_open": JSON.stringify(openTimes),
                "sched_closed": JSON.stringify(closedTimes),
                "sched_class": JSON.stringify(classTimes),
                "id_num": id_num,
                "search_type": type
            };
        }

    }
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

//var scheduleObjArray = new Array();
function loadUserSchedules(loaded){
    if (loaded == false){
        var boolvar = false;
        getSchedules(userName, scheduleTabs, boolvar);
    }
    else if (loaded == true){
        
        /*
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
        */
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
    for (var i = 0; i < scheduleTabs[currentScheduleTab].selectedCourses.length; i++){
        var tempstart = parseInt(scheduleTabs[currentScheduleTab].selectedCourses[i].startTime) / 100;
        var tempend = parseInt(scheduleTabs[currentScheduleTab].selectedCourses[i].endTime) / 100;
        if ((specificStartTime > tempstart && specificStartTime < tempend) ||(specificEndTime > tempstart && specificEndTime < tempend)){
            var tempdays = [];
            for (var j = 0; j < scheduleTabs[currentScheduleTab].selectedCourses[i].days.length; j = j + 2){
                var tempDay = scheduleTabs[currentScheduleTab].selectedCourses[i].days.substr(j, 2);
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
                        
                scheduleTabs[currentScheduleTab].classTimes.push("timeslot-div-" + courseDays[i] + "-" + k);
                scheduleTabs[currentScheduleTab].selectedDivs.push("timeslot-div-" + courseDays[i] + "-" + k);

            }
        }
    }
    
    scheduleTabs[currentScheduleTab].selectedCourses.push(course);
    setSelectedCourses(userName, scheduleTabs[currentScheduleTab].selectedCourses);
    setClassTimes(userName, scheduleTabs[currentScheduleTab].classTimes);
    setSelectedDiv(userName, scheduleTabs[currentScheduleTab].selectedDivs);
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

        scheduleTabs[currentScheduleTab].selectedCourses.splice($.inArray(course, scheduleTabs[currentScheduleTab].selectedCourses), 1);
        setSelectedCourses(userName, scheduleTabs[currentScheduleTab].selectedCourses);
        setClassTimes(userName, scheduleTabs[currentScheduleTab].classTimes);
        setSelectedDiv(userName, scheduleTabs[currentScheduleTab].selectedDivs);
        courseInfoArray[3] = scheduleTabs[currentScheduleTab].selectedCourses;
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



function getListOfReqs(){
    var list = "<option value = \"none\">-Select Requirement-</option>";
    for (var i = 0; i < majorReqNames.length; i++){
        list = list + "<option value = \"" + i + "\">" +  majorReqNames[i] + "</option>";
    }
    $("#req_list_dropdown").html(list);
}