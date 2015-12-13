
var days = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
var hours = ["12:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00", "12:00", "1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00"];
var dayStart = -1;
var dayEnd = -2;
var numDivsX = dayEnd - dayStart + 1;

var hourStart = -1, hourEnd = -2, ampm = "AM";
var selectedDivs = new Array();

var numDivsY = hourEnd - hourStart + 1;

var scheduleTabs = new Array();
var currentScheduleTab = -1;

function createDivs(){
    
    var $container = $("#timeslot-list");
    var $dayContainer = $("#day-list");
    var $hourContainer = $("#hour-list");
    
    var $days = $("<div>").addClass("day-divs");
    var $hours = $("<div>").addClass("hour-divs");
    var $temp;
    
    for (var i = 0; i < numDivsX; i++){
        for (var k = 0; k < numDivsY; k++){
            $temp = $("<div>").addClass("timeslot-divs");
            $temp.attr("id", "timeslot-div-" + (i+dayStart) + "-" + (k+hourStart));
            $temp.css({
                'top':    (($container.height() / numDivsY) * k),
                'left':   (($container.width() / numDivsX) * i),
                'width':  ($container.width() / numDivsX),
                'height': ($container.height() / numDivsY),
            });
            if ((i % 2 == 0 && k % 2 == 0) || (i % 2 == 1 && k % 2 == 1))
                $temp.css({"background": "lightgrey",});
            $temp.data("data-selected", "false");
            $temp.appendTo($container);
        }
    }
    
    $temp = $("<div>").addClass("day-divs");
    $temp.attr("id", "day-div-filler");
    $temp.css({
        "width" : $dayContainer.width() - ($("#timeslot-div-" + dayStart + "-" + hourStart).width() * (numDivsX)),
        "height" : "inherit",
        "background" : "inherit",
        
    });
    
    $hourContainer.css({"width" : $("day-div-filler").width()});
    $temp.appendTo($dayContainer);
    
    for (var i = 0; i < numDivsX; i++){
        
        $temp = $("<div>").addClass("day-divs");
        $temp.attr("id", "day-div-" + (i+dayStart));
        $temp.css({
            "top" : 0,
            "left" : (($dayContainer.width() - $("#day-div-filler").width()) / numDivsX) * i + $("#day-div-filler").width(),
            "width" : $("#timeslot-div-" + dayStart + "-" + hourStart).width(),
            "background" : "green",
            "display" : "table",
        });
        if (i%2 == 0)
            $temp.css({"background" : "grey"});
        else
            $temp.css({"background" : "lightgrey"});
        
        
        $temp.html("<p class = 'aligned-labels'>" + (days[i + dayStart]) +"</p>");
        $temp.appendTo($dayContainer);
    }
    for (var i = 0; i < numDivsY; i++){
        
        $temp = $("<div>").addClass("hour-divs");
        $temp.attr("id", "hour-div-" + (i+hourStart));
        $temp.css({
            "top" : ($hourContainer.height() / numDivsY) * i,
            "left" : 0,
            "height" : $hourContainer.height() / numDivsY,
            "background" : "green",
            display: "table",
        });
        if (i%2 == 0)
            $temp.css({"background" : "grey"});
        else
            $temp.css({"background" : "lightgrey"});
            
        if ((i + hourStart)/12 < 1)
            ampm = "am";
        else
            ampm = "pm";
        $temp.html("<p class = 'aligned-labels'>" + (hours[i + hourStart]) + "</p>");
        $temp.appendTo($hourContainer);
        
    }
    
     for (var i = 0; i < numDivsX; i++){
        for (var k = 0; k < numDivsY; k++){
            var $temp = $("#timeslot-div-" + (i+dayStart) + "-" + (k+hourStart));
            
            $temp.on("mouseenter", function(e){
                if ($.inArray(e.target.id, scheduleTabs[currentScheduleTab].selectedDivs) == -1){
                    $(e.target).css({"background" : "rgba(100, 100, 100, 0.5)",});
                }
                else if ($.inArray(e.target.id, scheduleTabs[currentScheduleTab].openTimes) != -1)
                    $(e.target).css({"background" : "green",});
                else if ($.inArray(e.target.id, scheduleTabs[currentScheduleTab].closedTimes) != -1)
                   $(e.target).css({"background" : "#111111",});
            });
            $temp.on("mouseleave", function(e){
                // parse id num of e.target
                var idName = e.target.id;                
                var i = parseInt(idName.substring(13, idName.indexOf("-", 13))) + dayStart;
                var k = parseInt(idName.substring(idName.indexOf("-", 13) + 1)) + hourStart;
                if ($(e.target).data("data-selected") == "false"){
                    if($.inArray(e.target.id, scheduleTabs[currentScheduleTab].selectedDivs) == -1){
                        if ((i % 2 == 0 && k % 2 == 0) || (i % 2 == 1 && k % 2 == 1)){
                            $(e.target).css({"background" : "lightgrey",});
                        }
                        else
                            $(e.target).css({"background" : "inherit",});
                    }
                    else if ($.inArray(e.target.id, scheduleTabs[currentScheduleTab].openTimes) != -1)
                        $(e.target).css({"background" : "lightgreen",});
                    else if ($.inArray(e.target.id, scheduleTabs[currentScheduleTab].closedTimes) != -1)
                        $(e.target).css({"background" : "#333333",});
                }
            });
        }
    }
    
    for (var i = 0; i < numDivsX; i++){
        for (var k = 0; k < numDivsY; k++){
            if ($.inArray("timeslot-div-" + (i+dayStart) + "-" + (k+hourStart), scheduleTabs[currentScheduleTab].openTimes) != -1){
                $("#timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)).css({"background" : "lightgreen",});
            }
            else if ($.inArray("timeslot-div-" + (i+dayStart) + "-" + (k+hourStart), scheduleTabs[currentScheduleTab].closedTimes) != -1)
                $("#timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)).css({"background" : "#333333",});
        }
    }
    
    
    for (var i = 0; i < scheduleTabs[currentScheduleTab].selectedCourses.length; i++){
        //for each day of that course
        var courseDays = [];
        for (var k = 0; k < scheduleTabs[currentScheduleTab].selectedCourses[i].days.length; k = k + 2){
            var tempDay = scheduleTabs[currentScheduleTab].selectedCourses[i].days.substr(k, 2);
            for (var j = 0; j < days.length; j++){
                if (tempDay == days[j].substr(0, 2))
                    courseDays.push(j);
            }
        }
        var daysOk = true;
        for (k = 0; k < courseDays.length; k++){
            if (courseDays[k] > dayEnd || courseDays[k] < dayStart)
                daysOk = false;
        }
        if (daysOk == true){
            for (k = 0; k < courseDays.length; k++){
                // create div
                var $tempCourseDiv = $("<div>").addClass("course_div_overlay");
                $tempCourseDiv.attr("id", "course-div-" + i + "-" + courseDays[k]);
                var courseStartTime = parseInt(scheduleTabs[currentScheduleTab].selectedCourses[i].startTime) / 100;
                var courseEndTime = parseInt(scheduleTabs[currentScheduleTab].selectedCourses[i].endTime) / 100;
                if (hourStart < courseStartTime && hourEnd + 1 > courseEndTime ){
                    var offsetStart = Math.ceil((courseStartTime - Math.floor(courseStartTime)) * 100);
                    var offsetEnd = Math.ceil((courseEndTime - Math.floor(courseEndTime)) * 100);
                    courseStartTime = Math.floor(courseStartTime);
                    courseEndTime = Math.floor(courseEndTime);


                    // set size/pos
                    $tempCourseDiv.css({
                        "width" : $("#timeslot-div-" + courseDays[k] + "-" + courseStartTime).width(),
                        "height" : ($("#timeslot-div-" + courseDays[k] + "-" + courseStartTime).height() * (courseEndTime - courseStartTime)) - ((offsetStart/60) * $("#timeslot-div-" + courseDays[k] + "-" + courseStartTime).height()) + ((offsetEnd/60) * $("#timeslot-div-" + courseDays[k] + "-" + courseStartTime).height()) + "px",

                        "top" : parseFloat($("#timeslot-div-" + courseDays[k] + "-" + courseStartTime).css("top")) + ((offsetStart/60) * $("#timeslot-div-" + courseDays[k] + "-" + courseStartTime).height()) + "px",

                        "left" : $("#timeslot-div-" + courseDays[k] + "-" + courseStartTime).css("left"),
                    });
                    $tempCourseDiv.appendTo($("#timeslot-list"));
                    $tempCourseDiv.html("<div>" + scheduleTabs[currentScheduleTab].selectedCourses[i].dept + " " + scheduleTabs[currentScheduleTab].selectedCourses[i].courseNum + "</div>");
                }
            }
        }
        
    }
    
    
}


$(function() {
    var $container = $('#timeslot-list');
    var $selection = $('<div>').addClass('selection-box');
    var $timeslot;
    var ctrlOn = false;
    $(document).on("keydown", function(event) {
        if (event.which == 17){
            ctrlOn = true;
        }
    });
    $(document).on("keyup", function(event) {
        if (event.which == 17){
            ctrlOn = false;
        }
    });
    $(window).resize(function(e){
        document.getElementById("day-list").innerHTML = "";
        document.getElementById("hour-list").innerHTML = "";
        document.getElementById("timeslot-list").innerHTML = "";
        createDivs();
    });

    
    $(document).click(function (e) {
        if (!$("#schedule").is(e.target) && $("#schedule").has(e.target).length === 0) {
            for(var i = 0; i < numDivsX; i++) {
                for (var k = 0; k < numDivsY; k++) {
                    $("#timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)).data("data-selected", "false");
                    if($.inArray("timeslot-div-" + (i+dayStart) + "-" + (k+hourStart), scheduleTabs[currentScheduleTab].selectedDivs) == -1){
                        if ((i % 2 == 0 && k % 2 == 0) || (i % 2 == 1 && k % 2 == 1))
                            $("#timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)).css({"background" : "lightgrey",});
                        else
                            $("#timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)).css({"background" : "inherit",});
                    }
                    else if ($.inArray("timeslot-div-" + (i+dayStart) + "-" + (k+hourStart), scheduleTabs[currentScheduleTab].closedTimes) > -1)
                        $("#timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)).css({"background" : "#333333",});
                    else if ($.inArray("timeslot-div-" + (i+dayStart) + "-" + (k+hourStart), scheduleTabs[currentScheduleTab].openTimes) > -1)
                        $("#timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)).css({"background" : "lightgreen",});
                }
            }
        }
    });
    
   
    
    $container.on('mousedown', function(e) {
        var click_y = e.pageY - $container.offset().top;
        var click_x = e.pageX - $container.offset().left;
        e.preventDefault();
        $selection.css({
          'top':    click_y,
          'left':   click_x,
          'width':  0,
          'height': 0,
          'background': "rgba(0, 0, 0, 0.5)",
          'position': "absolute"
        });
        $selection.appendTo($container);
        
        for(var i = 0; i < numDivsX; i++) {
            for (var k = 0; k < numDivsY; k++) {
                $timeslot = $("#timeslot-div-" + (i+dayStart) + "-" + (k+hourStart));

                if ((
                    ($timeslot.offset().top < $selection.offset().top) &&
                    ($timeslot.offset().left < $selection.offset().left) &&
                     ($timeslot.offset().top >= $selection.offset().top - $container.height() / numDivsY) &&
                    ($timeslot.offset().left >= $selection.offset().left - $container.width() / numDivsX)
                    )){
                    $timeslot.data("data-selected", "true");
                    if($.inArray($timeslot.attr("id"), scheduleTabs[currentScheduleTab].selectedDivs) == -1)
                        $timeslot.css({"background" : "rgba(100, 100, 100, 0.5)",});
                    else if ($.inArray($timeslot.attr("id"), scheduleTabs[currentScheduleTab].openTimes) > -1)
                        $timeslot.css({"background" : "green",});
                    else if ($.inArray($timeslot.attr("id"), scheduleTabs[currentScheduleTab].closedTimes) > -1)
                        $timeslot.css({"background" : "#111111",});
                }
                else if (ctrlOn == false){
                    $timeslot.data("data-selected", "false");
                    if ($.inArray($timeslot.attr("id"), scheduleTabs[currentScheduleTab].selectedDivs) == -1){
                        if ((i % 2 == 0 && k % 2 == 0) || (i % 2 == 1 && k % 2 == 1))
                            $timeslot.css({"background" : "lightgrey",});
                        else
                            $timeslot.css({"background" : "inherit",});
                    }
                    else if ($.inArray($timeslot.attr("id"), scheduleTabs[currentScheduleTab].openTimes) > -1)
                        $timeslot.css({"background" : "lightgreen",});
                    else if ($.inArray($timeslot.attr("id"), scheduleTabs[currentScheduleTab].closedTimes) > -1)
                        $timeslot.css({"background" : "#333333",});
                }
            }
        }
        
        $container.on('mousemove', function(e) {
            var move_x = e.pageX - $container.offset().left,
                move_y = e.pageY - $container.offset().top,
                width  = Math.abs(move_x - click_x),
                height = Math.abs(move_y - click_y),
                new_x, new_y;
            e.preventDefault();
            new_x = (move_x < click_x) ? (click_x - width) : click_x;
            new_y = (move_y < click_y) ? (click_y - height) : click_y;

            $selection.css({
              'width': width,
              'height': height,
              'top': new_y,
              'left': new_x
            });
            for(var i = 0; i < numDivsX; i++) {
                for (var k = 0; k < numDivsY; k++) {
                    $timeslot = $("#timeslot-div-" + (i+dayStart) + "-" + (k+hourStart));
                    
                    if (
                        ($timeslot.offset().top < $selection.height() + $selection.offset().top) &&
                        ($timeslot.offset().left < $selection.width() + $selection.offset().left) &&
                         ($timeslot.offset().top > $selection.offset().top - $container.height() / numDivsY) &&
                        ($timeslot.offset().left > $selection.offset().left - $container.width() / numDivsX)
                        ){
                        $timeslot.data("data-selected", "true");
                        if ($.inArray($timeslot.attr("id"), scheduleTabs[currentScheduleTab].selectedDivs) == -1)
                            $timeslot.css({"background" : "rgba(100, 100, 100, 0.5)",});
                        else if ($.inArray($timeslot.attr("id"), scheduleTabs[currentScheduleTab].openTimes) > -1)
                            $timeslot.css({"background" : "green",});
                        else if ($.inArray($timeslot.attr("id"), scheduleTabs[currentScheduleTab].closedTimes) > -1)
                            $timeslot.css({"background" : "#111111",});
                        
                        
                    }
                    else if (ctrlOn == false){
                        $timeslot.data("data-selected", "false");
                        if ($.inArray($timeslot.attr("id"), scheduleTabs[currentScheduleTab].selectedDivs) == -1){
                            if ((i % 2 == 0 && k % 2 == 0) || (i % 2 == 1 && k % 2 == 1))
                                $timeslot.css({"background" : "lightgrey",});
                            else
                                $timeslot.css({"background" : "inherit",});
                        }
                        else if ($.inArray($timeslot.attr("id"), scheduleTabs[currentScheduleTab].openTimes) > -1)
                            $timeslot.css({"background" : "lightgreen",});
                        else if ($.inArray($timeslot.attr("id"), scheduleTabs[currentScheduleTab].closedTimes) > -1)
                            $timeslot.css({"background" : "#333333",});
                    }
                }
            }
            
            
        }).on('mouseup', function(e) {
            $container.off('mousemove');
            $selection.remove();
            
        });
    });
});


/***************************************************/

// arrays containing open, closed, iffy, and reserved timeslots -- retrieved and stored in THIS schedule info (schedule specific, not profile, specific. one profile can  have multiple sets of available time)


function createTools() {    
    var $toolContainer = $("#schedule_header");
    var $open, $close, $clear, $days, $hours;
    
// CLOSED TOOL
    $closed = $("<div>").addClass("#schedule-tools");
    $closed.attr("id", "closed-times-tool");
    $closed.css({
        "top" : "0.5vh",
        "height" : "4vh",
        "right" : "0.25vw",
        "margin" : 0,
        "padding" : 0,
        "width" : "3vw",
        "background-image" : "url(images/2.png)",
        "position" : "relative",
        "display" : "table",
        "float" : "right",
        "border-radius" : "10px",
        
    });
    $closed.html("<a href = '#' class = 'tool-labels' style = 'border-radius: 10px;'>NO</a>");
    $closed.appendTo($toolContainer);
    $closed.on("mouseenter",function(e){
        e.preventDefault();
       $("#closed-times-tool a").css({"background-image" : "url(images/1.png)", "border-radius" : "10px", "color" : "white", "border" : "1px solid white",});
    });
    $closed.on("mouseleave", function(e){
        e.preventDefault();
        $("#closed-times-tool a").css({"background-image" : "url(images/2.png)", "border-radius" : "10px", "color" : "black", "border" : "none",});
    });
    $closed.click(function(e){
        e.preventDefault();
        var teststring = "";
        for(var i = 0; i < numDivsX; i++){
            for (var k = 0; k < numDivsY; k++){
                if ($("#timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)).data("data-selected") == "true"){
                    $("#timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)).css({"background" : "#333333",});
                    $("#timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)).data("data-selected", "false");
                        
                    if ($.inArray(("timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)), scheduleTabs[currentScheduleTab].closedTimes) == -1){
                         scheduleTabs[currentScheduleTab].closedTimes.push("timeslot-div-" + (i+dayStart) + "-" + (k+hourStart));
                        scheduleTabs[currentScheduleTab].selectedDivs.push("timeslot-div-" + (i+dayStart) + "-" + (k+hourStart));
                        
                        if ($.inArray(("timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)), scheduleTabs[currentScheduleTab].openTimes) > -1){
                             scheduleTabs[currentScheduleTab].openTimes.splice($.inArray(("timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)), scheduleTabs[currentScheduleTab].openTimes), 1);
                           
                        }
                    }
                    
                }
            }
        }
        setClosedTimes(userName, scheduleTabs[currentScheduleTab].closedTimes);
        setOpenTimes(userName, scheduleTabs[currentScheduleTab].openTimes);
        setSelectedDiv(userName, scheduleTabs[currentScheduleTab].selectedDivs);
    });
    
//OPEN TOOL
    $open = $("<div>").addClass("#schedule-tools");
    $open.attr("id", "open-times-tool");
    $open.css({
        "top" : "0.5vh",
        "right" : "0.5vw",
        "height" : "4vh",
        "margin" : 0,
        "padding" : 0,
        "width" : "3vw",
        "background-image" : "url(images/2.png)",
        "position" : "relative",
        "display" : "table",
        "float" : "right",
        "border-radius" : "10px",
    });
    
    $open.html("<a href = '#' class = 'tool-labels' style = 'border-radius: 10px;'>OK</a>");
    $open.appendTo($toolContainer);
    $open.on("mouseenter",function(e){
        e.preventDefault();
        $("#open-times-tool a").css({"background-image" : "url(images/1.png)", "border-radius" : "10px", "color" : "white", "border" : "1px solid white",});
    });
    $open.on("mouseleave",function(e){
        e.preventDefault();
        $("#open-times-tool a").css({"background-image" : "url(images/2.png)", "border-radius" : "10px", "color" : "black", "border" : "none",});
    });
    
    
    
    $open.click(function(e){
        e.preventDefault();
        var teststring = "";
        for(var i = 0; i < numDivsX; i++){
            for (var k = 0; k < numDivsY; k++){
                if ($("#timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)).data("data-selected") == "true"){
                    $("#timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)).css({"background" : "lightgreen",});
                    $("#timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)).data("data-selected", "false");    
                    if ($.inArray(("timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)), scheduleTabs[currentScheduleTab].openTimes) == -1){
                         scheduleTabs[currentScheduleTab].openTimes.push("timeslot-div-" + (i+dayStart) + "-" + (k+hourStart));
                        scheduleTabs[currentScheduleTab].selectedDivs.push("timeslot-div-" + (i+dayStart) + "-" + (k+hourStart));
                        
                        if ($.inArray(("timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)), scheduleTabs[currentScheduleTab].closedTimes) > -1){
                             scheduleTabs[currentScheduleTab].closedTimes.splice($.inArray(("timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)), scheduleTabs[currentScheduleTab].closedTimes), 1);
                           
                        }
                    }                    
                }
            }
        }
        
        
        setClosedTimes(userName, scheduleTabs[currentScheduleTab].closedTimes);
        setOpenTimes(userName, scheduleTabs[currentScheduleTab].openTimes);
        setSelectedDiv(userName, scheduleTabs[currentScheduleTab].selectedDivs);
    });
    
// CLEAR TOOL
    $clear = $("<div>").addClass("#schedule-tools");
    $clear.attr("id", "clear-times-tool");
    $clear.css({
        "top" : "0.5vh",
        "height" : "4vh",
        "right" : "0.75vw",
        "width" : "4vw",
        "margin" : 0,
        "padding" : 0,
        "background-image" : "url(images/2.png)",
        "position" : "relative",
        "display" : "table",
        "float" : "right",
        "border-radius" : "10px",
    });
    $clear.html("<a href = '#' class = 'tool-labels' style = 'border-radius: 10px;'>CLEAR</a>");
    $clear.appendTo($toolContainer);
    $clear.on("mouseenter",function(e){
        e.preventDefault();
        $("#clear-times-tool a").css({"background-image" : "url(images/1.png)", "border-radius" : "10px", "color" : "white", "border" : "1px solid white",});
    });
    $clear.on("mouseleave",function(e){
        e.preventDefault();
        $("#clear-times-tool a").css({"background-image" : "url(images/2.png)", "border-radius" : "10px", "color" : "black", "border" : "none",});
    });
    
    $clear.click(function(e){
        e.preventDefault();
        var teststring = "";
        for(var i = 0; i < numDivsX; i++){
            for (var k = 0; k < numDivsY; k++){
                if ($("#timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)).data("data-selected") == "true"){
                    if ((i % 2 == 0 && k % 2 == 0) || (i % 2 == 1 && k % 2 == 1))
                        $("#timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)).css({"background" : "lightgrey",});
                    else
                        $("#timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)).css({"background" : "inherit",});

                    $("#timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)).data("data-selected", "false");  
                    
                    if ($.inArray(("timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)), scheduleTabs[currentScheduleTab].openTimes) != -1){
                         scheduleTabs[currentScheduleTab].openTimes.splice($.inArray(("timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)), scheduleTabs[currentScheduleTab].openTimes), 1);
                         scheduleTabs[currentScheduleTab].selectedDivs.splice($.inArray(("timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)), scheduleTabs[currentScheduleTab].selectedDivs), 1);
                       
                    }
                    if ($.inArray(("timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)), scheduleTabs[currentScheduleTab].closedTimes) != -1){
                         scheduleTabs[currentScheduleTab].closedTimes.splice($.inArray(("timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)), scheduleTabs[currentScheduleTab].closedTimes), 1);
                         scheduleTabs[currentScheduleTab].selectedDivs.splice($.inArray(("timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)), scheduleTabs[currentScheduleTab].selectedDivs), 1);
                       
                    }
                    if ($.inArray(("timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)), scheduleTabs[currentScheduleTab].selectedDivs) != -1){
                        scheduleTabs[currentScheduleTab].selectedDivs.splice($.inArray(("timeslot-div-" + (i+dayStart) + "-" + (k+hourStart)), scheduleTabs[currentScheduleTab].selectedDivs), 1);
                       
                    }                 
                }
            }
        }
        
        setClosedTimes(userName, scheduleTabs[currentScheduleTab].closedTimes);
        setOpenTimes(userName, scheduleTabs[currentScheduleTab].openTimes);
        setSelectedDiv(userName, scheduleTabs[currentScheduleTab].selectedDivs);
        
    });
    
    
    
//Dropdown lists for day and time
    var $dayList1, $dayLabel1, $dayLabelLink1;
    var $dayList2, $dayLabel2, $dayLabelLink2;
    var $indivDay, $indivDayLink;
    
    $dayList1 = $("<div style: 'display: inline;'>").addClass(".schedule-tool-dropdown");
    $dayList1.attr("id", "day-selection-dropdown-from");
    $dayLabel1 = $("<div>").attr("id", "day-selection-dropdown-from-label");
    $dayLabelLink1 = $("<a>").attr("id", "day-selection-dropdown-from-label-link");
    $dayLabelLink1.attr("href", "#");
    $dayLabelLink1.html(days[dayStart]);
    $dayLabelLink1.appendTo($dayLabel1);
    $dayLabel1.appendTo($dayList1);
    $dayList1.appendTo($("#schedule"));
    $days = $("<ul>");
    for (var i = 0; i < days.length; i++){
        $indivDay = $("<li>");
        $indivDayLink = $("<a>");
        $indivDayLink.attr("href", "#");
        $indivDayLink.html(days[i]);
        $indivDay.append($indivDayLink);
        $indivDay.appendTo($days);
    }
    $days.appendTo($dayList1);
    
    $dayList1.click(function(e){
        //e.preventDefault();
        if ($("#day-selection-dropdown-from ul").css("display") != "block"){
            
            $("#day-selection-dropdown-from ul").css({"display" : "block"});
            $("#day-selection-dropdown-from ul li").css({"display" : "block"});
            $("#day-selection-dropdown-to ul").css({"display" : "none"});
            $("#day-selection-dropdown-to ul li").css({"display" : "none"});
            
            $("#hour-selection-dropdown-from ul").css({"display" : "none"});
            $("#hour-selection-dropdown-from ul li").css({"display" : "none"});
            $("#hour-selection-dropdown-to ul").css({"display" : "none"});
            $("#hour-selection-dropdown-to ul li").css({"display" : "none"});
            
            $("#ampm-selection-dropdown-from ul").css({"display" : "none"});
            $("#ampm-selection-dropdown-from ul li").css({"display" : "none"});
            $("#ampm-selection-dropdown-to ul").css({"display" : "none"});
            $("#ampm-selection-dropdown-to ul li").css({"display" : "none"});
            
            
        }
        else{
            $("#day-selection-dropdown-from ul").css({"display" : "none"});
            $("#day-selection-dropdown-from ul li").css({"display" : "none"});
            $("#day-selection-dropdown-from-label-link").html($(e.target).html());
            
            
            dayStart = $.inArray($(e.target).html(), days);
            if (dayStart > dayEnd){
                dayEnd = dayStart;
                $("#day-selection-dropdown-to-label-link").html($(e.target).html());
            }
            numDivsX = dayEnd - dayStart + 1;
            scheduleTabs[currentScheduleTab].dayStart = dayStart;
            scheduleTabs[currentScheduleTab].dayEnd = dayEnd;
            setDayStart(userName, dayStart);
            setDayEnd(userName, dayEnd);
            
            
            $days = $("<ul>");
            for (var i = dayStart; i < days.length; i++){
                $indivDay = $("<li>");
                $indivDayLink = $("<a>");
                $indivDayLink.attr("href", "#");
                $indivDayLink.html(days[i]);
                $indivDay.append($indivDayLink);
                $indivDay.appendTo($days);
            }
            $("#day-selection-dropdown-to-list").remove();
            $days.attr("id", "day-selection-dropdown-to-list");
            $days.appendTo($("#day-selection-dropdown-to"));
            document.getElementById("day-list").innerHTML = "";
            document.getElementById("hour-list").innerHTML = "";
            document.getElementById("timeslot-list").innerHTML = "";
            createDivs();
        }
    });
    
    var $dayFiller = $("<div>");
    $dayFiller.css({
        "position": "absolute",
        "top": 0,
        "left": "7.25vw",
        "height" : "5vh",
        "width" : "2vw",
        "text-align" : "center",
        "margin" : 0,
        "padding" : 0,
        "color" : "white",
        "font-weight" : "bold",
        
    });
    
    $dayFiller.html("<div style = 'display: table-cell; vertical-align: middle; height: inherit; width: inherit;'>to</div>");
    $("#schedule").append($dayFiller);
    
    
    $dayList2 = $("<div style: 'display: inline;>").addClass(".schedule-tool-dropdown");
    $dayList2.attr("id", "day-selection-dropdown-to");
    $dayLabel2 = $("<div>").attr("id", "day-selection-dropdown-to-label");
    $dayLabelLink2 = $("<a>").attr("id", "day-selection-dropdown-to-label-link");
    $dayLabelLink2.attr("href", "#");
    $dayLabelLink2.html(days[dayEnd]);
    $dayLabelLink2.appendTo($dayLabel2);
    $dayLabel2.appendTo($dayList2);
    $dayList2.appendTo($("#schedule"));
    $days = $("<ul>");
    $days.attr("id", "day-selection-dropdown-to-list");
    for (var i = dayStart; i < days.length; i++){
        $indivDay = $("<li>");
        $indivDayLink = $("<a>");
        $indivDayLink.attr("href", "#");
        $indivDayLink.html(days[i]);
        $indivDay.append($indivDayLink);
        $indivDay.appendTo($days);
    }
    $days.appendTo($dayList2);
    
    $dayList2.click(function(e){
        if ($("#day-selection-dropdown-to ul").css("display") != "block"){
            $("#day-selection-dropdown-to ul").css({"display" : "block"});
            $("#day-selection-dropdown-to ul li").css({"display" : "block"});
            
            $("#day-selection-dropdown-from ul").css({"display" : "none"});
            $("#day-selection-dropdown-from ul li").css({"display" : "none"});
            
            $("#hour-selection-dropdown-from ul").css({"display" : "none"});
            $("#hour-selection-dropdown-from ul li").css({"display" : "none"});
            $("#hour-selection-dropdown-to ul").css({"display" : "none"});
            $("#hour-selection-dropdown-to ul li").css({"display" : "none"});
            
            $("#ampm-selection-dropdown-from ul").css({"display" : "none"});
            $("#ampm-selection-dropdown-from ul li").css({"display" : "none"});
            $("#ampm-selection-dropdown-to ul").css({"display" : "none"});
            $("#ampm-selection-dropdown-to ul li").css({"display" : "none"});
            
        }
        else{
            $("#day-selection-dropdown-to ul").css({"display" : "none"});
            $("#day-selection-dropdown-to ul li").css({"display" : "none"});
            $("#day-selection-dropdown-to-label-link").html($(e.target).html());
            
            dayEnd = $.inArray($(e.target).html(), days);
            scheduleTabs[currentScheduleTab].dayStart = dayStart;
            scheduleTabs[currentScheduleTab].dayEnd = dayEnd;
            
            setDayStart(userName, dayStart);
            setDayEnd(userName, dayEnd);
            
            numDivsX = dayEnd - dayStart + 1;
            
            document.getElementById("day-list").innerHTML = "";
            document.getElementById("hour-list").innerHTML = "";
            document.getElementById("timeslot-list").innerHTML = "";
            createDivs();
            
        }
    });
    
// Dropdown lists for the hours
    var $hourList1, $hourLabel1, $hourLabelLink1;
    var $hourList2, $hourLabel2, $hourLabelLink2;
    var $ampmList1, $ampmLabel1, $ampmLabelLink1;
    var $ampmList2, $ampmLabel2, $ampmLabelLink2;
    var $indivHour, $indivHourLink;
    var $indivAMPM, $indivAMPMLink;
    
    $hourList1 = $("<div style: 'display: inline;'>").addClass(".schedule-tool-dropdown");
    $hourList1.attr("id", "hour-selection-dropdown-from");
    $hourLabel1 = $("<div>").attr("id", "hour-selection-dropdown-from-label");
    $hourLabelLink1 = $("<a>").attr("id", "hour-selection-dropdown-from-label-link");
    $hourLabelLink1.attr("href", "#");
    $hourLabelLink1.html(hours[hourStart]);
    $hourLabelLink1.appendTo($hourLabel1);
    $hourLabel1.appendTo($hourList1);
    $hourList1.appendTo($("#schedule"));
    $hours = $("<ul>");
    for (var i = 0; i < 12; i++){
        $indivHour = $("<li>");
        $indivHourLink = $("<a>");
        $indivHourLink.attr("href", "#");
        $indivHourLink.html(hours[i]);
        $indivHour.append($indivHourLink);
        $indivHour.appendTo($hours);
    }
    $hours.appendTo($hourList1);
    
    $hourList1.click(function(e){
        if ($("#hour-selection-dropdown-from ul").css("display") != "block"){
            $("#hour-selection-dropdown-from ul").css({"display" : "block"});
            $("#hour-selection-dropdown-from ul li").css({"display" : "block"});
            
            $("#day-selection-dropdown-to ul").css({"display" : "none"});
            $("#day-selection-dropdown-to ul li").css({"display" : "none"});
            
            $("#day-selection-dropdown-from ul").css({"display" : "none"});
            $("#day-selection-dropdown-from ul li").css({"display" : "none"});
            $("#hour-selection-dropdown-to ul").css({"display" : "none"});
            $("#hour-selection-dropdown-to ul li").css({"display" : "none"});
            
            $("#ampm-selection-dropdown-from ul").css({"display" : "none"});
            $("#ampm-selection-dropdown-from ul li").css({"display" : "none"});
            $("#ampm-selection-dropdown-to ul").css({"display" : "none"});
            $("#ampm-selection-dropdown-to ul li").css({"display" : "none"});
            
        }
        else{
            $("#hour-selection-dropdown-from ul").css({"display" : "none"});
            $("#hour-selection-dropdown-from ul li").css({"display" : "none"});
            $("#hour-selection-dropdown-from-label-link").html($(e.target).html());
            
            if ($("#ampm-selection-dropdown-from-label-link").html() == "AM")
                hourStart = $.inArray($(e.target).html(), hours);
            else
                hourStart = $.inArray($(e.target).html(), hours) + 12;
            
            if (hourStart > hourEnd){
                hourEnd = hourStart;
                $("#hour-selection-dropdown-to-label-link").html($(e.target).html());
            }
            
            scheduleTabs[currentScheduleTab].hourStart = hourStart;
            scheduleTabs[currentScheduleTab].hourEnd = hourEnd;
            setHoursStart(userName, hourStart);
            setHoursEnd(userName, hourEnd);
            
            numDivsY = hourEnd - hourStart + 1;
            
            $hours = $("<ul>");
            for (var i = 0; i < 12; i++){
                $indivHour = $("<li>");
                $indivHourLink = $("<a>");
                $indivHourLink.attr("href", "#");
                $indivHourLink.html(hours[i]);
                $indivHour.append($indivHourLink);
                $indivHour.appendTo($hours);
            }
            $("#hour-selection-dropdown-to-list").remove();
            $hours.attr("id", "hour-selection-dropdown-to-list");
            $hours.appendTo($("#hour-selection-dropdown-to"));
            document.getElementById("day-list").innerHTML = "";
            document.getElementById("hour-list").innerHTML = "";
            document.getElementById("timeslot-list").innerHTML = "";
            createDivs();
        }
    });
    
    var ampms;
    $ampmList = $("<div style: 'display: inline;'>").addClass(".schedule-tool-dropdown");
    $ampmList.attr("id", "ampm-selection-dropdown-from");
    $ampmLabel = $("<div>").attr("id", "ampm-selection-dropdown-from-label");
    $ampmLabelLink = $("<a>").attr("id", "ampm-selection-dropdown-from-label-link");
    $ampmLabelLink.attr("href", "#");
    $ampmLabelLink.html("AM");
    $ampmLabelLink.appendTo($ampmLabel);
    $ampmLabel.appendTo($ampmList);
    $ampmList.appendTo($("#schedule"));
    $ampms = $("<ul>");

    $indivAMPM = $("<li>");
    $indivAMPMLink = $("<a>");
    $indivAMPMLink.attr("href", "#");
    $indivAMPMLink.html("AM");
    $indivAMPM.append($indivAMPMLink);
    $indivAMPM.appendTo($ampms);
    
    $indivAMPM = $("<li>");
    $indivAMPMLink = $("<a>");
    $indivAMPMLink.attr("href", "#");
    $indivAMPMLink.html("PM");
    $indivAMPM.append($indivAMPMLink);
    $indivAMPM.appendTo($ampms);
    $ampms.appendTo($ampmList);
    
    $ampmList.click(function(e){
        if ($("#ampm-selection-dropdown-from ul").css("display") != "block"){
            $("#ampm-selection-dropdown-from ul").css({"display" : "block"});
            $("#ampm-selection-dropdown-from ul li").css({"display" : "block"});
            $("#day-selection-dropdown-to ul").css({"display" : "none"});
            $("#day-selection-dropdown-to ul li").css({"display" : "none"});
            
            $("#hour-selection-dropdown-from ul").css({"display" : "none"});
            $("#hour-selection-dropdown-from ul li").css({"display" : "none"});
            $("#hour-selection-dropdown-to ul").css({"display" : "none"});
            $("#hour-selection-dropdown-to ul li").css({"display" : "none"});
            
            $("#day-selection-dropdown-from ul").css({"display" : "none"});
            $("#day-selection-dropdown-from ul li").css({"display" : "none"});
            $("#ampm-selection-dropdown-to ul").css({"display" : "none"});
            $("#ampm-selection-dropdown-to ul li").css({"display" : "none"});
            
        }
        else{
            $("#ampm-selection-dropdown-from ul").css({"display" : "none"});
            $("#ampm-selection-dropdown-from ul li").css({"display" : "none"});
            $("#ampm-selection-dropdown-from-label-link").html($(e.target).html());
            
            if ($("#ampm-selection-dropdown-from-label-link").html() == "AM"){
                hourStart = hourStart%12;
            }
            else
                hourStart = hourStart%12 + 12;
            
            if (hourStart > hourEnd){
                hourEnd = hourStart;
                $("#hour-selection-dropdown-to-label-link").html($("#hour-selection-dropdown-from-label-link").html());
            }
            
            scheduleTabs[currentScheduleTab].hourStart = hourStart;
            scheduleTabs[currentScheduleTab].hourEnd = hourEnd;
            setHoursStart(userName, hourStart);
            setHoursEnd(userName, hourEnd);
            
            numDivsY = hourEnd - hourStart + 1;
            
            document.getElementById("day-list").innerHTML = "";
            document.getElementById("hour-list").innerHTML = "";
            document.getElementById("timeslot-list").innerHTML = "";
            createDivs();
        }
    });
    
    
    /**********************************************/
    
    
    var $hourFiller = $("<div>");
    $hourFiller.css({
        "position": "absolute",
        "top": 0,
        "left": "23.50vw",
        "height" : "5vh",
        "width" : "2vw",
        "text-align" : "center",
        "margin" : 0,
        "padding" : 0,
        "color" : "white",
        "font-weight" : "bold",
        
        
    });
    
    $hourFiller.html("<div style = 'display: table-cell; vertical-align: middle; height: inherit; width: inherit;'>to</div>");
    $("#schedule").append($hourFiller);
    
    
    $hourList2 = $("<div style: 'display: inline;>").addClass(".schedule-tool-dropdown");
    $hourList2.attr("id", "hour-selection-dropdown-to");
    $hourLabel2 = $("<div>").attr("id", "hour-selection-dropdown-to-label");
    $hourLabelLink2 = $("<a>").attr("id", "hour-selection-dropdown-to-label-link");
    $hourLabelLink2.attr("href", "#");
    $hourLabelLink2.html(hours[hourEnd]);
    $hourLabelLink2.appendTo($hourLabel2);
    $hourLabel2.appendTo($hourList2);
    $hourList2.appendTo($("#schedule"));
    $hours = $("<ul>");
    $hours.attr("id", "hour-selection-dropdown-to-list");
    for (var i = 0; i < 12; i++){
        $indivHour = $("<li>");
        $indivHourLink = $("<a>");
        $indivHourLink.attr("href", "#");
        $indivHourLink.html(hours[i]);
        $indivHour.append($indivHourLink);
        $indivHour.appendTo($hours);
    }
    $hours.appendTo($hourList2);
    
    $hourList2.click(function(e){
        if ($("#hour-selection-dropdown-to ul").css("display") != "block"){
            $("#hour-selection-dropdown-to ul").css({"display" : "block"});
            $("#hour-selection-dropdown-to ul li").css({"display" : "block"});
            
            $("#day-selection-dropdown-to ul").css({"display" : "none"});
            $("#day-selection-dropdown-to ul li").css({"display" : "none"});
            
            $("#hour-selection-dropdown-from ul").css({"display" : "none"});
            $("#hour-selection-dropdown-from ul li").css({"display" : "none"});
            $("#day-selection-dropdown-from ul").css({"display" : "none"});
            $("#day-selection-dropdown-from ul li").css({"display" : "none"});
            
            $("#ampm-selection-dropdown-from ul").css({"display" : "none"});
            $("#ampm-selection-dropdown-from ul li").css({"display" : "none"});
            $("#ampm-selection-dropdown-to ul").css({"display" : "none"});
            $("#ampm-selection-dropdown-to ul li").css({"display" : "none"});
            
        }
        else{
            $("#hour-selection-dropdown-to ul").css({"display" : "none"});
            $("#hour-selection-dropdown-to ul li").css({"display" : "none"});
            $("#hour-selection-dropdown-to-label-link").html($(e.target).html());
            
            if ($("#ampm-selection-dropdown-to-label-link").html() == "AM")
                hourEnd = $.inArray($(e.target).html(), hours);
            else
                hourEnd = $.inArray($(e.target).html(), hours) + 12;
            
            if (hourStart > hourEnd){
                hourStart = hourEnd;
                $("#hour-selection-dropdown-from-label-link").html($(e.target).html());
            }
            scheduleTabs[currentScheduleTab].hourStart = hourStart;
            scheduleTabs[currentScheduleTab].hourEnd = hourEnd;
            setHoursStart(userName, hourStart);
            setHoursEnd(userName, hourEnd);
            numDivsY = hourEnd - hourStart + 1;
            
            document.getElementById("day-list").innerHTML = "";
            document.getElementById("hour-list").innerHTML = "";
            document.getElementById("timeslot-list").innerHTML = "";
            createDivs();
            
        }
    });
    
    
    $ampmList = $("<div style: 'display: inline;'>").addClass(".schedule-tool-dropdown");
    $ampmList.attr("id", "ampm-selection-dropdown-to");
    $ampmLabel = $("<div>").attr("id", "ampm-selection-dropdown-to-label");
    $ampmLabelLink = $("<a>").attr("id", "ampm-selection-dropdown-to-label-link");
    $ampmLabelLink.attr("href", "#");
    $ampmLabelLink.html("PM");
    $ampmLabelLink.appendTo($ampmLabel);
    $ampmLabel.appendTo($ampmList);
    $ampmList.appendTo($("#schedule"));
    $ampms = $("<ul>");

    $indivAMPM = $("<li>");
    $indivAMPMLink = $("<a>");
    $indivAMPMLink.attr("href", "#");
    $indivAMPMLink.html("AM");
    $indivAMPM.append($indivAMPMLink);
    $indivAMPM.appendTo($ampms);
    
    $indivAMPM = $("<li>");
    $indivAMPMLink = $("<a>");
    $indivAMPMLink.attr("href", "#");
    $indivAMPMLink.html("PM");
    $indivAMPM.append($indivAMPMLink);
    $indivAMPM.appendTo($ampms);
    $ampms.appendTo($ampmList);
    
    $ampmList.click(function(e){
        if ($("#ampm-selection-dropdown-to ul").css("display") != "block"){
            $("#ampm-selection-dropdown-to ul").css({"display" : "block"});
            $("#ampm-selection-dropdown-to ul li").css({"display" : "block"});
            
            $("#day-selection-dropdown-to ul").css({"display" : "none"});
            $("#day-selection-dropdown-to ul li").css({"display" : "none"});
            
            $("#hour-selection-dropdown-from ul").css({"display" : "none"});
            $("#hour-selection-dropdown-from ul li").css({"display" : "none"});
            $("#hour-selection-dropdown-to ul").css({"display" : "none"});
            $("#hour-selection-dropdown-to ul li").css({"display" : "none"});
            
            $("#ampm-selection-dropdown-from ul").css({"display" : "none"});
            $("#ampm-selection-dropdown-from ul li").css({"display" : "none"});
            $("#day-selection-dropdown-from ul").css({"display" : "none"});
            $("#day-selection-dropdown-from ul li").css({"display" : "none"});
            
        }
        else{
            $("#ampm-selection-dropdown-to ul").css({"display" : "none"});
            $("#ampm-selection-dropdown-to ul li").css({"display" : "none"});
            $("#ampm-selection-dropdown-to-label-link").html($(e.target).html());
            
            if ($("#ampm-selection-dropdown-to-label-link").html() == "AM"){
                hourEnd = hourEnd%12;
            }
            else
                hourEnd = hourEnd%12 + 12;
            
            if (hourStart > hourEnd){
                hourStart = hourEnd;
                $("#hour-selection-dropdown-from-label-link").html($("#hour-selection-dropdown-to-label-link").html());
            }
            
            scheduleTabs[currentScheduleTab].hourStart = hourStart;
            scheduleTabs[currentScheduleTab].hourEnd = hourEnd;
            setHoursStart(userName, hourStart);
            setHoursEnd(userName, hourEnd);
            numDivsY = hourEnd - hourStart + 1;
            
            document.getElementById("day-list").innerHTML = "";
            document.getElementById("hour-list").innerHTML = "";
            document.getElementById("timeslot-list").innerHTML = "";
            createDivs();     
        }
    });
}



// temporary test values
// will load from user profile
function loadScheduleTabObjs(){
    
    var tempTab = {
        dayStart: 1,
        dayEnd: 5,
        hourStart: 8,
        hourEnd: 17,
        openTimes: new Array(),
        closedTimes: new Array(),
        selectedDivs: new Array(),
        valid: true,
    };
    tempTab.openTimes.push("timeslot-div-1-9");
    tempTab.closedTimes.push("timeslot-div-2-9");
    tempTab.selectedDivs.push("timeslot-div-1-9");
    tempTab.selectedDivs.push("timeslot-div-2-9");
    scheduleTabs.push(tempTab);
    
    tempTab = {
        dayStart: 2,
        dayEnd: 6,
        hourStart: 11,
        hourEnd: 22,
        openTimes: new Array(),
        closedTimes: new Array(),
        selectedDivs: new Array(),
        valid: true,
    };
    tempTab.openTimes.push("timeslot-div-3-13");
    tempTab.closedTimes.push("timeslot-div-3-14");
    tempTab.selectedDivs.push("timeslot-div-3-13");
    tempTab.selectedDivs.push("timeslot-div-3-14");
    scheduleTabs.push(tempTab);
    
    tempTab = {
        dayStart: 0,
        dayEnd: 4,
        hourStart: 5,
        hourEnd: 13,
        openTimes: new Array(),
        closedTimes: new Array(),
        selectedDivs: new Array(),
        valid: true,
    };
    tempTab.openTimes.push("timeslot-div-1-8");
    tempTab.closedTimes.push("timeslot-div-2-10");
    tempTab.selectedDivs.push("timeslot-div-1-8");
    tempTab.selectedDivs.push("timeslot-div-2-10");
    scheduleTabs.push(tempTab);
    
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
    
}

function loadScheduleTab(num){
    if (currentScheduleTab == num && num != -1)
        return;
    else if (num == -1){
        $("#day-list").html("");
        $("#hour-list").html("");
        $("#timeslot-list").html("");
        dayStart = -1;
        dayEnd = -2;
        hourStart = -1;
        hourEnd = -2;
        numDivsX = dayEnd - dayStart + 1;
        numDivsY = hourEnd - hourStart + 1;
        
    }
    else{
        $("#schedule-tab-" + num).css({"background-image" :"url(images/2.png)"});
        $("#schedule-tab-" + currentScheduleTab).css({"background-image" : "url(images/schedule-tab.png)"});

        currentScheduleTab = num;
        
        dayStart = scheduleTabs[num].dayStart;
        dayEnd = scheduleTabs[num].dayEnd;
        hourStart = scheduleTabs[num].hourStart;
        hourEnd = scheduleTabs[num].hourEnd;
        
        courseInfoArray[3] = [];
        courseInfoArray[3] = scheduleTabs[currentScheduleTab].selectedCourses;
        numCoursesPerList[3] = courseInfoArray[3].length;
                
        numDivsX = dayEnd - dayStart + 1;
        numDivsY = hourEnd - hourStart + 1;
        $("#day-selection-dropdown-from-label-link").html(days[dayStart]);
        $("#day-selection-dropdown-to-label-link").html(days[dayEnd]);
        $("#hour-selection-dropdown-from-label-link").html(hours[hourStart]);
        $("#hour-selection-dropdown-to-label-link").html(hours[hourEnd]);

        if (hourStart / 12 > 1)
            $("#ampm-selection-dropdown-from-label-link").html("PM");
        else
            $("#ampm-selection-dropdown-from-label-link").html("AM");


        var $days = $("<ul>");
        var $indivDay, $indivDayLink;
        for (var i = dayStart; i < days.length; i++){
            $indivDay = $("<li>");
            $indivDayLink = $("<a>");
            $indivDayLink.attr("href", "#");
            $indivDayLink.html(days[i]);
            $indivDay.append($indivDayLink);
            $indivDay.appendTo($days);
        }
        $("#day-selection-dropdown-to-list").remove();
        $days.attr("id", "day-selection-dropdown-to-list");
        $days.appendTo($("#day-selection-dropdown-to"));


        $("#day-list").html("");
        $("#hour-list").html("");
        $("#timeslot-list").html("");
        createDivs();
    }
}

/******************************************************************/
// Overlay logic and buttons
/******************************************************************/
function createScheduleFooterTools(){
    var $button;
    $button = $("<div>").addClass("schedule_footer_tool");
    $button.html("<a href = '#' id = 'schedule_footer_tool_new'>NEW</a>");
    $button.appendTo($("#schedule_footer"));   
    $button.hover(
        function(e){
            e.preventDefault();
            $("#schedule_footer_tool_new").css({
                "border": "1px solid white",
                "background-image":"url(images/1.png)",
                "color":"white",
            });
        },
        function(e){
            e.preventDefault();
            $("#schedule_footer_tool_new").css({
                "border" : "none",
                "background-image":"url(images/2.png)",
                "color" : "black",
            });
                 
    });
    $button.click(function(e){
        e.preventDefault();
        var counter = 0;
        for (i = 0; i < scheduleTabs.length; i++){
            if (scheduleTabs[i].valid == true)
                counter++;
        }
        if(counter >= 7){
            alert("Too many schedules! Delete one or more.");
            return;
        }
        var tempTab = {
            dayStart: 0,
            dayEnd: 6,
            hourStart: 0,
            hourEnd: 23,
            openTimes: new Array(),
            closedTimes: new Array(),
            selectedDivs: new Array(),
            valid: true,
            classTimes: new Array(),
            selectedCourses: new Array(),
        };
        
        scheduleTabs.push(tempTab);
        var $tabDiv = $("<div>").addClass("schedule-tab-selector");
        $tabDiv.attr("id", "schedule-tab-" + (scheduleTabs.length - 1));
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
        var schedName = "Schedule" + (scheduleTabs.length);
        $tabDiv.html("<a href = '#' class = 'tab-links'>" + schedName + "</a>");
        
        $("#schedule_footer").append($tabDiv);    
        $tabDiv.click(function(e){
            
            e.preventDefault();
            var index = $(e.target).parent().attr("id").substring(13, $(e.target).parent().attr("id").length);
            loadScheduleTab(index);
        });
        loadScheduleTab(scheduleTabs.length - 1);
        fadeScheduleOverlay();
        createSched(userName);        
        
    });
    
    
    $button = $("<div>").addClass("schedule_footer_tool");
    $button.html("<a href = '#' id = 'schedule_footer_tool_delete'>DELETE</a>");
    $button.appendTo($("#schedule_footer"));   
    $button.hover(
        function(e){
            e.preventDefault();
            $("#schedule_footer_tool_delete").css({
                "border": "1px solid white",
                "background-image":"url(images/1.png)",
                "color":"white",
            });
        },
        function(e){
            e.preventDefault();
            $("#schedule_footer_tool_delete").css({
                "border" : "none",
                "background-image":"url(images/2.png)",
                "color" : "black",
            });
    });
    $button.click(function(e){
        $("#schedule-tab-" + (currentScheduleTab)).remove();
        scheduleTabs[currentScheduleTab].valid = false;
        setValid(userName, false);
        
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
            loadScheduleOverlay();
        }
        
        
    });
}

function setupScheduleOverlay(){
    $("#schedule_overlay_link").click(function(e){
        e.preventDefault();
        var tempTab = {
            dayStart: 0,
            dayEnd: 6,
            hourStart: 0,
            hourEnd: 23,
            openTimes: new Array(),
            closedTimes: new Array(),
            selectedDivs: new Array(),
            valid: true,
            classTimes: new Array(),
            selectedCourses: new Array(),
        };
        
        scheduleTabs.push(tempTab);
        var $tabDiv = $("<div>").addClass("schedule-tab-selector");
        $tabDiv.attr("id", "schedule-tab-" + (scheduleTabs.length - 1));
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
        var schedName = "Schedule" + (scheduleTabs.length);
        $tabDiv.html("<a href = '#' class = 'tab-links'>" + schedName + "</a>");
        
        $("#schedule_footer").append($tabDiv);    
        $tabDiv.click(function(e){
            
            e.preventDefault();
            var index = $(e.target).parent().attr("id").substring(13, $(e.target).parent().attr("id").length);
            loadScheduleTab(index);
        });
        loadScheduleTab(scheduleTabs.length - 1);
        fadeScheduleOverlay();
        createSched(userName);
    });
}


