
/**********************************************/
// Sample search info
/**********************************************/
function courseObj(){
    this.courseNum = "";
    this.dept = "";
    this.name = "";
    this.desc = "";
    this.startTimes = "";
    this.endTimes = "";
    this.flag = false;
    this.sectionNum = "";
    this.components = "";
    this.requirements = "";
    this.credits = 0;
    this.startTime = "";
    this.endTime = "";
    this.room = "";
    this.days = "";
    this.instructor = "";
}

var courseInfoArray = [];
var numLists = 4;
var numCoursesPerList = [0, 0, 0, 0];


function parseCourseResultset(data){    
    //alert(data);
    var tempCourseArray = data.split("ENTRY_END");
    courseInfoArray[0] = [];
    courseInfoArray[1] = [];
    courseInfoArray[2] = [];
    courseInfoArray[3] = [];
    for (var i = 0; i < tempCourseArray.length; i++){ 
        var cObj = new courseObj();
        var tempCourseParts = tempCourseArray[i].split("FIELD_END");
        for (var k = 0; k < tempCourseParts.length; k++){
            var coursePart = tempCourseParts[k].split("~");
            if (coursePart[0] == "Dept")
                cObj.dept = coursePart[1];
            else if (coursePart[0] == "CNum")
                cObj.courseNum = coursePart[1];
            else if (coursePart[0] == "Name")
                cObj.name = coursePart[1];
            else if (coursePart[0] == "Comp")
                cObj.components = coursePart[1];
            else if (coursePart[0] == "Req")
                cObj.requirements = coursePart[1];
            else if (coursePart[0] == "Desc")
                cObj.desc = coursePart[1];
            else if (coursePart[0] == "Cr")
                cObj.credits = coursePart[1];
            else if (coursePart[0] == "SNum")
                cObj.sectionNum = coursePart[1];
            else if (coursePart[0] == "STime")
                cObj.startTime = coursePart[1];
            else if (coursePart[0] == "ETime")
                cObj.endTime = coursePart[1];
            else if (coursePart[0] == "Days")
                cObj.days = coursePart[1];
            else if (coursePart[0] == "Room")
                cObj.room = coursePart[1];
            else if (coursePart[0] == "Inst")
                cObj.instructor = coursePart[1];
            else if (coursePart[0] == "Flag"){
                //alert(coursePart[1]);
                if (coursePart[1] == "t")    
                    cObj.flag = true;
                else if (coursePart[1] == "f")
                    cObj.flag = false;
            }
            
            //alert(coursePart[0]);
        }
        //alert(cObj.flag);
        courseInfoArray[0].push(cObj);
        courseInfoArray[1].push(cObj);
        courseInfoArray[2].push(cObj);
        
    }
    numCoursesPerList[0] = courseInfoArray[0].length;
    numCoursesPerList[1] = courseInfoArray[1].length;
    numCoursesPerList[2] = courseInfoArray[2].length;
}


function loadList(num) {
    var listName = document.getElementById("list_section_header_" + num).innerHTML;
    if (document.getElementById("list_section_" + num).innerHTML == "") {
        var listContents = document.getElementById("list_section_" + num).innerHTML + "<ul id = \"listTest\">";
        var courseList = "";
        for(i = 0; i < numCoursesPerList[num]; i++)
        {
            courseList +=
            "<div class='collapse' id = 'result" + (i+1) + "'>" +
                "<div class='box'>" +
                    "<div class='arrow'></div>" +
                    "<div class='arrow-border'></div>" +
                    "<div class='course_info' id = 'course" + num + i + "'>" + 
                    //courseInfoArray[0][0] + 
                    "</div>" +
                "</div>" +
            "</div>";
        
        }
        document.getElementById("course_info_" + num).innerHTML = courseList;
        
        for (i = 0; i < numCoursesPerList[num]; i++)
        {
            //APPEND NEW DIVS FOR EACH PART
            var $wrapperDiv = $("<div>").addClass("course_wrapper");
            var $basicInfoDiv = $("<div>").addClass("course_basic-info");
            var $descDiv = $("<p>").addClass("course_desc");
            var $flagDiv = $("<div>").addClass("course_flag");
            
            $("#course" + num + i).append($wrapperDiv);
            $wrapperDiv.append($basicInfoDiv);
            $wrapperDiv.append($descDiv);
            $wrapperDiv.append($flagDiv);
            if (courseInfoArray[num][i].flag == true)
                $flagDiv.css({"background": "green"});
            
            $basicInfoDiv.html(
                courseInfoArray[num][i].dept + 
                courseInfoArray[num][i].courseNum + ": " + 
                courseInfoArray[num][i].name + 
                "<br />Section: " + courseInfoArray[num][i].sectionNum +
                "<br />" + courseInfoArray[num][i].days + "&nbsp;&nbsp;&nbsp;&nbsp;" +
                 courseInfoArray[num][i].startTime + " - " + courseInfoArray[num][i].endTime

            );
            $descDiv.html(courseInfoArray[num][i].desc);
            
            
        }
        
        
        for(i = 0; i < numCoursesPerList[num]; i++){
        
            listContents +=  
                "<li><div class='popbox'><a class='open' href='#' id = 'list" + num + "res" + (i+1) + "'>" +     
                courseInfoArray[num][i].dept + ": " + courseInfoArray[num][i].courseNum + "</a>" +
                "</div></li>";
        }
        listContents += "</ul>";
        document.getElementById("list_section_" + num).innerHTML = listContents;
        //document.getElementById("list_section_" + num).style.z_index = "999";
        
        if (num != 3){
            for (var i = 0; i < numCoursesPerList[num] - 1; i++){
                $("#list" + num + "res" + (i+1)).click(function(event){
                    var num1 = parseInt($(event.target).attr("id").substring(4, $(event.target).attr("id").indexOf("res")));
                    var num2 = parseInt($(event.target).attr("id").substring($(event.target).attr("id").indexOf("res") + 3, $(event.target).attr("id").length));
                    addCourseToSchedule(courseInfoArray[num1][num2 - 1]);
                });
            }
        }
        else{
            for (var i = 0; i < numCoursesPerList[num] - 1; i++){
                $("#list" + num + "res" + (i+1)).click(function(event){
                    var num1 = parseInt($(event.target).attr("id").substring(4, $(event.target).attr("id").indexOf("res")));
                    var num2 = parseInt($(event.target).attr("id").substring($(event.target).attr("id").indexOf("res") + 3, $(event.target).attr("id").length));
                    removeCourseFromSchedule(courseInfoArray[num1][num2 - 1]);
                });
            }
        }
        
            /*
            "<li><a href='#'>Result 2</a></li>" +
            "<li><a href='#'>Result 3</a></li>" +
            "<li><a href='#'>Result 4</a></li>" +
            "<li><a href='#'>Result 5</a></li>" +
            "<li><a href='#'>Result 6</a></li>" +
            "<li><a href='#'>Result 7</a></li>" +
            "<li><a href='#'>Result 8</a></li>" +
            "<li><a href='#'>Result 9</a></li>" +
            "</ul>";
            */
        document.getElementById("list_section_header_" + num).style.backgroundImage = "url(images/1.png)";
        document.getElementById("list_section_header_" + num).style.color = "#FFFFFF";
        document.getElementById("list_section_header_" + num).innerHTML = listName.replace("(+)", "(-)");
         $(document).ready(function(){
     $('.popbox').popbox({
       'open'          : '.open',
       'box'           : '.box',
       'arrow'         : '.arrow',
       'arrow_border'  : '.arrow_border',
       'close'         : '.close',
       'course_info'   : '.course_info'
      });
   });
    }
    
    else {
        document.getElementById("list_section_" + num).innerHTML = "";
        document.getElementById("list_section_header_" + num).style.backgroundImage = "url(images/2.png)";
        document.getElementById("list_section_header_" + num).style.color = "#000000";
        document.getElementById("list_section_header_" + num).innerHTML = listName.replace("(-)", "(+)");
			
    }
}




/*******************************************************************/
// LEFT COLUMN TABS
var currentSearchTab = 0;

function loadTab(tabNum){
        
    if (tabNum == currentSearchTab)
        return;
     
    else {
        //change bg to hover bg
        var newTabNum = (tabNum % 2) + 1;
        document.getElementById("tab" + tabNum).style.backgroundImage = "url(images/tab.png)";
        document.getElementById("tab" + tabNum).style.color = "#FFFFFF";
        
        document.getElementById("tab" + newTabNum).style.backgroundImage = "url(images/tab2.png)";
        document.getElementById("tab" + newTabNum).style.color = "#000000";
        currentSearchTab = tabNum;
        
        //insert relevant content
        if (tabNum == 1){
            $("#left_scroll_wrapper").load("left_tab_1.html");
        }
        
        else if (tabNum == 2){
            $("#left_scroll_wrapper").load("left_tab_2.html");
        }
    }
        
}
//loadTab(2);

function loadInstDeptLists(){
    // code for loading college names and department names
    // similar to functions for profile
    
}





