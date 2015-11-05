
/**********************************************/
// Sample search info
/**********************************************/
function courseObj(cn, n, d, st, et, f){
    this.courseNum = cn;
    this.name = n;
    this.desc = d;
    this.startTimes = st;
    this.endTimes = et;
    this.flag = f;
    
}

var courseInfoArray = [];

var numLists = 3;
var numCoursesPerList = [9, 9, 9];

for (var i = 0; i < numLists; i++){
    for (var k = 0; k < numCoursesPerList[i] ; k++){
        var numStartTimes = 2;
        var numEndTimes = 2;
        var courseNum = "CSCI" + i + "" + k + "000";
        var name = "Course: " + i + ", " + k;
        var desc = "Sample course info.";
        var startTimes = [];
        for (var j = 0; j < numStartTimes; j++)
            startTimes.push(i + ":" + j + "" + k);
        var endTimes = [];
        for (var j = 0; j < numStartTimes; j++)
            endTimes.push(i + ":" + j + "" + k);
        var flag = true; 
        
        
        courseInfoArray[i][k] = new courseObj(courseNum, name, desc, startTimes, endTimes, flag);
    }
}

//courseInfoArray[0] = [];

// [listnum][course info]
courseInfoArray[0][0] = "<br /><br />Course Information Goes Here!<br /><br /><br /><br /><br /><br /><br /><br /><br /><br /><br /><br /><br /><br />";






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
                    "<div class='course_info'>" + 
                    //courseInfoArray[0][0] + 
                    "</div>" +
                "</div>" +
            "</div>";
        
        }
        document.getElementById("course_info_" + num).innerHTML = courseList;
        
        for (i = 0; i < numCoursesPerList[num]; i++)
        {
            $("#course_info_" + num + " .course_info").html(courseInfoArray[0][0]);
        }
        
        
        
        for(i = 0; i < 9; i++){
        
        listContents +=  
            "<li><div class='popbox'><a class='open' href='#' id = 'list" + num + "res" + (i+1) + "'>" +     
            "Result " + (i + 1) + "</a>" +
            "</div></li>";
        }
        listContents += "</ul>";
        document.getElementById("list_section_" + num).innerHTML = listContents;
        //document.getElementById("list_section_" + num).style.z_index = "999";
        
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

