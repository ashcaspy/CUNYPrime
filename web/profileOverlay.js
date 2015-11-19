var profile_loaded = false;
var resume_loaded = false;
var scheduler_loaded = false;
var loaderTimeout;


function selectSchedulerLoad(){
    if (!scheduler_loaded){
        if (resume_loaded)
            fadeResume(true);
        if (profile_loaded)
            fadeProfile(true);
        //setTimeout(loaderTmeout){
        loadScheduler();
        //, 500);
    }
    else
        fadeScheduler(false);
}


function selectProfileLoad(){
    if (!profile_loaded){
        if (resume_loaded)
            fadeResume(true);
        if (scheduler_loaded)
            fadeScheduler(true);
        loadProfile();
    }
    else
        fadeProfile(false);
}

function selectResumeLoad(){
    if (!resume_loaded){
        if (profile_loaded)
            fadeProfile(true);
        if (scheduler_loaded)
            fadeScheduler(true);
        loadResume();
    }
    else
        fadeResume(false);
    
}


function loadProfile(){
 
    $("#profile").fadeIn("slow");
    $("#profile_blanket").fadeIn("fast");
    //$("#profile").load("profileContent.html");
    profile_loaded = true;
}

function fadeProfile(needBlanket){
    $("#profile").fadeOut("fast");
    if (!needBlanket) fadeBlanket();
    profile_loaded = false;
}

function loadResume(){
    $("#resume").fadeIn("slow");
    $("#profile_blanket").fadeIn("fast");
    //$("#resume").load("resumeContent.html");
    resume_loaded = true;
    
}

function fadeResume(needBlanket){
    $("#resume").fadeOut("fast");
    if (!needBlanket) fadeBlanket();
    resume_loaded = false;
}

function loadScheduler(){
 
    $("#scheduler").fadeIn("slow");
    $("#profile_blanket").fadeIn("fast");
    //$("#scheduler").load("schedulerContent.html");
    scheduler_loaded = true;
    
    if (currentSearchTab != 0)
        loadTab(currentSearchTab);
    else
        loadTab(1);
    if (currentScheduleTab == -1)
        loadScheduleOverlay();
}

function fadeScheduler(needBlanket){
    $("#scheduler").fadeOut("fast");
    if (needBlanket == false) fadeBlanket();
    scheduler_loaded = false;
    fadeScheduleOverlay();
}

function fadeBlanket(){
    $("#profile_blanket").fadeOut("fast");   
}

function loadLoginOverlay(){
    $("#login_overlay").fadeIn("fast");
    $("#login_blanket").fadeIn("fast");
}

function fadeLoginOverlay(){
    $("#login_overlay").fadeOut("fast");
    $("#login_blanket").fadeOut("fast");
}

function loadScheduleOverlay(){
    $("#schedule_overlay").fadeIn("fast");
    $("#schedule_blanket").fadeIn("fast");
}

function fadeScheduleOverlay(){
    $("#schedule_overlay").fadeOut("fast");
    $("#schedule_blanket").fadeOut("fast");
}