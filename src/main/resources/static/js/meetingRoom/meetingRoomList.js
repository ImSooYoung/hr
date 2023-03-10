let listTable = document.querySelector('#listTable');
createTable();

function createTable(){
    let tbl= "<table id='meetingTable' >";

    for (let i = 1; i <= 10; i++) {
        tbl+= "<tr>";
        for (let j = 8; j <= 20; j++) {
            if (i == 1) {
                if (j == 8) {
                    tbl += `<th class="text-center bg-dark p-2 text-dark bg-opacity-10"></th>`;
                } else {
                    tbl += `<th class="text-center bg-dark p-2 text-dark bg-opacity-10" >${j}:00</th>`;
                }
            }else {
                if (j == 8) {
                    tbl += `<th style="width: 100px;"><input type="radio" class="btn-check" id="회의실${i-1}" name="rooms"  autocomplete="off" value="회의실${i-1}" onclick='getCheckboxValue(event)'>
                            <label class="btn btn-outline-dark "  style="width: 100px; border: none; font-weight: bolder" for="회의실${i-1}">회의실${i-1}</label></th>`;
                } else if(j == 9) {
                    tbl += `<td id="0${j}:00">`;
                } else {
                    tbl += `<td id="${j}:00">`;
                }
            }
        }
        tbl+= "</tr>";
    }
    tbl+= "</table>";
    listTable.innerHTML =tbl;
}

var dragging = false;
var dragSelectIds = [];

var $td = $('td');
var startCell = null;
const inputDate = document.querySelector('#input_submit');
let mapTemp = new Map([
    ["09:00", 1],
    ["10:00", 2],
    ["11:00", 3],
    ["12:00", 4],
    ["13:00", 5],
    ["14:00", 6],
    ["15:00", 7],
    ["16:00", 8],
    ["17:00", 9],
    ["18:00", 10],
    ["19:00", 11],
    ["20:00", 12]
]);

// 회의실 체크박스
function getCheckboxValue(event)  {
    let results = '';
    if(event.target.checked)  {
        results = event.target.value;
    }
    console.log('회의실 : ' + results)

}

function end(e) {
    dragSelectIds = [];

    $td.removeClass('selected-item');

    $(cellsBetween(startCell, e.target)).each(function() {
        $(this).addClass('selected-item');
        dragSelectIds.push($(this).attr('id'));

        console.log('this'+ $(this).children());
    });

}



function cellsBetween(start, end) {
    var bounds, elementsInside;
    elementsInside = [start, end];
    do {
        bounds = getBoundsForElements(elementsInside);
        var elementsInsideAfterExpansion = rectangleSelect('td', bounds);
        if (elementsInside.length === elementsInsideAfterExpansion.length) {
            return elementsInside;
        } else {
            elementsInside = elementsInsideAfterExpansion;
        }
    } while (true);
}

function isPointBetween(point, x1, x2) {
    return (point >= x1 && point <= x2) || (point <= x1 && point >= x2);
}

function rectangleSelect(selector, bounds) {
    var elements = [];
    jQuery(selector).each(function() {
        var $this = jQuery(this);
        var offset = $this.offset();
        var x = offset.left;
        var y = offset.top;
        var w = $this.outerWidth();
        var h = $this.outerHeight();
        if ((isPointBetween(x + 1, bounds.minX, bounds.maxX) && isPointBetween(y + 1, bounds.minY, bounds.maxY)) ||
            (isPointBetween(x + w - 1, bounds.minX, bounds.maxX) && isPointBetween(y + h - 1, bounds.minY, bounds.maxY))
        ) {
            elements.push($this.get(0));
        }
    });
    return elements;
}

function getBoundsForElements(elements) {
    var x1 = elements.reduce(function(currMinX, element) {
        var elementLeft = $(element).offset().left;
        return currMinX && currMinX < elementLeft ? currMinX : elementLeft;
    }, undefined);
    var x2 = elements.reduce(function(currMaxX, element) {
        var elementRight = $(element).offset().left + $(element).outerWidth();
        return currMaxX && currMaxX > elementRight ? currMaxX : elementRight;
    }, undefined);
    var y1 = elements.reduce(function(currMinY, element) {
        var elementTop = $(element).offset().top;
        return currMinY && currMinY < elementTop ? currMinY : elementTop;

    }, undefined);
    var y2 = elements.reduce(function(currMaxY, element) {
        var elementBottom = $(element).offset().top + $(element).outerHeight();
        return currMaxY && currMaxY > elementBottom ? currMaxY : elementBottom;
    }, undefined);
    return {
        minX: x1,
        maxX: x2,
        minY: y1,
        maxY: y2
    };
}

$td.on('mousedown', function(e) {
    if(e.target.style.background == 'green'){
        alert('예약된 회의실입니다.');
        return;
    } else {
        dragging= true;
        startCell= e.target;
    }
    end(e);
});
$td.on('mouseenter', function(e) {
    if (!dragging) {
        return;
    }
    end(e);
});
$td.on('mouseup', function(e) {
    if(e.target.style.background == 'green'){
        alert('예약된 회의실입니다.');
        return;
    } else{
        dragging= false;
        console.log(dragSelectIds);
    }


});

// 오늘 이후로 시간 예약하지 못하게
var now_utc = Date.now()
var timeOff = new Date().getTimezoneOffset()*60000;
var today = new Date(now_utc-timeOff).toISOString().split("T")[0];
document.getElementById("reserveDate").setAttribute("min", today);


// list 첫 화면 오늘 날짜로 맞추기.
window.onload = function() {
    today = new Date();
    today = today.toISOString().slice(0, 10);
    bir = document.getElementById("reserveDate");
    bir.value = today;
}


// input date 값 받아오기.
function input() {
    const dday = document.querySelector('#reserveDate').value;
    console.log(dday)
    const tableView = document.querySelector('#tableView');
    tableView.style = "width: 100%; overflow: auto;";

    // 데이터를 불러옴
    let allMeetingList = [];
    axios.get('/api/org/meetingList/byDate/'+ dday)
        .then(response => {
            console.log(response.data)
            allMeetingList = response.data;
            updateTable(allMeetingList);
        })
        .catch(err => {
            console.log(err)
        })

}

function updateTable(list){

    //초기화
    for(let i = 0; i < listTable.rows.length; i++){
        for(let n = 0; n<13; n++){
            listTable.rows[i].cells[n].style.background='';
        }
    }

    let j = 0;
    for(let i = 0; i < listTable.rows.length; i++){

        if(j >= list.length){
            break;
        }

        while(listTable.rows[i].cells[0].innerText == list[j].roomName){
            let start = mapTemp.get(list[j].start);
            let end = mapTemp.get(list[j].end);
            checkBackground(start, end);
            j++;

            if(j >= list.length){
                break;
            }
        }

        function checkBackground (start, end){
            for(let n = start; n< end; n++){
                listTable.rows[i].cells[n].style.background="green";
            }
        }

    }
}
// 데이터 전송
const btnRegisterRoom = document.getElementById('btnRegisterRoom');
btnRegisterRoom.addEventListener('click', function () {
    console.log('selected:', dragSelectIds);
    let start = 0
    let end = 0
    let day = document.querySelector('#reserveDate').value;
    let room = 0;
    let roomPlace = 0


    if (dragSelectIds.length > 0) {
        start = mapTemp.get(dragSelectIds[0])+8 + ":00";
        end = mapTemp.get(dragSelectIds[dragSelectIds.length - 1])+9 + ":00";

    }

    // 회의실 이름 넘기기
    var room_length = document.getElementsByName("rooms").length;

    for (var i=0; i<room_length; i++) {
        if (document.getElementsByName("rooms")[i].checked == true) {
            room = document.getElementsByName("rooms")[i].value;
        }
    }

    if (room === 0) {
        alert('회의실을 선택해주세요.')
        return;
    } else if (start === 0) {
        alert('시간을 선택해주세요.')
        return;
    }

    // 회의실 위치 넘기기
    if(room == "회의실1") {
        roomPlace = "본관 1층 102호"
    }
    else if(room == "회의실2") {
        roomPlace = "본관 2층 203호"
    }
    else if(room == "회의실3") {
        roomPlace = "본관 2층 205호"
    }
    else if(room == "회의실4") {
        roomPlace = "본관 3층 303호"
    }
    else if(room == "회의실5") {
        roomPlace = "본관 5층 502호"
    }
    else if(room == "회의실6") {
        roomPlace = "별관 2층 203호"
    }
    else if(room == "회의실7") {
        roomPlace = "별관 3층 305호"
    }
    else if(room == "회의실8") {
        roomPlace = "별관 4층 402호"
    }
    else if(room == "회의실9") {
        roomPlace = "별관 4층 403호"
    }



    console.log(`start=${dragSelectIds[0]}, end=${end}, day=${day},room=${room},roomPlace=${roomPlace}`);

    location.href = `/meetingRoom/create?start=${dragSelectIds[0]}&end=${end}&day=${day}&room=${room}&roomPlace=${roomPlace}`;

});




