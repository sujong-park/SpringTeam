// schedule.js
// 유저의 전체 일정 확인하기---------------------
function getCurrentUserMid() {
    const midDOM = document.querySelector("#mid");
    const mid = midDOM.innerText;

    return mid;
}

async function getUserSchedules(mid) {
    console.log(mid)
    const response = await fetch(`/schedule/${mid}`);
    const schedules = await response.json();


    const matchingSchedules = schedules.filter(schedule => schedule.matching === true); // 매칭룸에서 넘어온 데이터
    const personalSchedules = schedules.filter(schedule => schedule.matching === false); // 직접 추가된 일정 데이터

    const matchingEvents = matchingSchedules.map(schedule => ({
        title: schedule.schedulename,
        start: new Date(schedule.walkDate),  // LocalDate를 JavaScript Date 객체로 변환
        // end: new Date(schedule.schedulEnd),
        extendedProps: {
            day: schedule.walkDate,  // 날짜 그대로 사용
            time: schedule.walkTime,
            place: schedule.walkPlace,
            matching: schedule.matching
        }
    }));

    const personalEvents = personalSchedules.map(schedule => ({
        title: schedule.schedulename,
        start: new Date(schedule.schedulStart),  // 시작 시간
        end: new Date(schedule.schedulEnd),  // 끝 시간
        extendedProps: {
            time: schedule.walkTime,
            place: schedule.walkPlace,
            // end: new Date(schedule.schedulEnd),
            matching: schedule.matching
        }
    }));

    return [...matchingEvents, ...personalEvents];
}


//(모달) 상세 정보---------------------------
function displayEventDetails(eventDetails) {

    const modalTitle = document.getElementById('modalTitle');
    const modalDate = document.getElementById('modalDate');
    const modalTime = document.getElementById('modalTime');
    const modalPlace = document.getElementById('modalPlace');
    const modalMatching = document.getElementById("modalMatching");

    modalTitle.innerText = eventDetails.schedulename;// 일정 제목
    console.log(`${formatDate(eventDetails.startDate)} ~ ${formatDate(eventDetails.endDate)}`)
    console.log(formatDate(eventDetails.startDate))
    console.log(formatDate(eventDetails.endDate))

    console.log(modalDate);

    console.log('eventDetails.matching:', eventDetails.matching);
    if(Boolean(eventDetails.matching===true)){
        console.log('모달에 들어갈 데이터',formatDate(eventDetails.startDate))
        modalDate.innerText = formatDate(eventDetails.startDate);
    }else{
        console.log('매칭 데이터 안들어가나',formatDate(eventDetails.startDate))
        modalDate.innerText = `${formatDate(eventDetails.startDate)} ~ ${formatDate(eventDetails.endDate)}`;
    }

    // modalDate.innetText = eventDetails.matching === true ? formatDate(eventDetails.startDate) : `${formatDate(eventDetails.startDate)} ~ ${formatDate(eventDetails.endDate)}`;


    modalTime.innerText = formatTime(eventDetails.walkTime) || 'N/A';  // 시간 정보
    modalPlace.innerText = eventDetails.place || 'N/A';    // 장소 정보
    modalMatching.innerText = eventDetails.matching

    // 모달 띄우기
    document.getElementById('scheduleModal').style.display = 'flex';
}


function openModal(date) {
    const modal = document.getElementById('scheduleaddModal');

    const startDateInputDOM = document.getElementById("modalStartDate");
    const endDateInputDOM = document.getElementById("modalEndDate");

    startDateInputDOM.value = date.startDate;
    endDateInputDOM.value = date.endDate;

    modal.style.display = 'block'; // 모달 보이기
}


// 비동기 함수로 일정 저장
async function addSchedule(date) {
    const schedulename = document.getElementById('addmodalTitle').value;
    const schedulStart = new Date(date.startDate).toISOString().split('T')[0]; // "YYYY-MM-DD"
    const schedulEnd = new Date(date.endDate).toISOString().split('T')[0]; // "YYYY-MM-DD"
    const walkTime = document.getElementById('addmodalTime').value;
    const timeRegex = /^([01]?[0-9]|2[0-3]):([0-5][0-9])$/; // HH:mm 형식 검증
    const walkPlace = document.getElementById('addmodalPlace').value;

    if (!timeRegex.test(walkTime)) {
        alert('올바른 시간을 입력하세요. 예: 10:00');
        return;
    }
    const mid = getCurrentUserMid();
    const calendarDTO = {
        mid, // 실제 userId로 교체
        schedulename,
        schedulStart,
        schedulEnd,
        walkTime,
        walkPlace,
        matching: false
    };
    // 서버로 전송되는 데이터 확인
    console.log('Sending data:', calendarDTO);


    try {
        const response = await fetch('/schedule/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(calendarDTO)
        });

        alert("일정이 추가 되었습니다.")
        window.location.reload();
    } catch (error) {
        console.error('Error:', error);
        alert('서버 요청 중 오류가 발생했습니다.');
    }
    //-----------------------

    // if (response.ok) {
    //     alert('일정 추가 성공');
    //     location.reload(); // 새로 고침하여 일정 반영
    // } else {
    //     alert('일정 추가 실패함');
    // }
    //--------------------
    // .then(response => {
    //         if (response.ok) {
    //             alert('일정 추가 성공');
    //             document.getElementById('scheduleaddModal').style.display = 'none'; // 모달 닫기
    //             calendar.addEvent({
    //                 title: title,
    //                 start: startDate,
    //                 end: endDate,
    //                 allDay: false
    //             }); // 캘린더에 이벤트 추가
    //         } else {
    //             alert('일정 추가 실패');
    //         }
    //     })
    //         .catch(error => {
    //             console.error('Error:', error);
    //             alert('일정 추가 중 오류 발생');
    //         });
    // } catch (error) {
    //     console.error('Error:', error);
    //     alert('서버 요청 중 오류가 발생했습니다.');
    // }
    //--------------------
    //     if (response.ok) {
    //         alert('일정 추가 성공');
    //         document.getElementById('scheduleaddModal').style.display = 'none'; // 모달 닫기
    //
    //         const result = await response.json(); // 서버에서 반환하는 데이터를 받음
    //         // const { schedulename, schedulStart, schedulEnd, walkTime, walkPlace } = result;
    //         console.log('서버응답:', result); // 서버의 응답 확인
    //         // 캘린더에 이벤트 추가
    //         calendar.addEvent({
    //             title: schedulename,
    //             start: new Date(result.schedulStart),
    //             end: new Date(result.schedulEnd),
    //             allDay: false,
    //             extendedProps: {
    //                 time: result.walkTime,
    //                 place: result.walkPlace
    //             }
    //         });
    //
    //     } else {
    //         const errorResponse = await response.json();  // 오류 메시지 확인
    //         alert(`일정 추가 실패: ${errorResponse.message || '알 수 없는 오류'}`);
    //     }
    // } catch (error) {
    //     console.error('Error:', error);
    //     alert('서버 요청 중 오류가 발생했습니다.');
    // }
}

//
//
// // 일정 추가 서버에 요청 (POST)
// async function addScheduleToDB(scheduleData) {
//     try {
//         const response = await fetch('/schedule/add', {
//             method: 'POST',
//             headers: {
//                 'Content-Type': 'application/json',
//             },
//             body: JSON.stringify(scheduleData),
//         });
//         if (response.ok) {
//             alert('일정이 추가되었습니다!');
//             calendar.refetchEvents();  // 일정을 갱신
//         } else {
//             alert('일정 추가 실패');
//         }
//     } catch (error) {
//         console.error('일정 추가 오류:', error);
//     }
// }

//---------------------------------------------
// 시간 형식 변환 함수
function formatTime(walkTime) {
    if (!walkTime) return 'N/A';
    // walkTime이 문자열인지 확인하고, 문자열이 아니면 강제로 문자열로 변환
    if (typeof walkTime !== 'string') {
        walkTime = String(walkTime);  // 문자열로 변환
    }
    const timeParts = walkTime.split(',');
    const hours = timeParts[0].padStart(2, '0');  // 시간 앞에 0 추가
    const minutes = timeParts[1].padStart(2, '0');  // 분 앞에 0 추가
    return `${hours}시 ${minutes}분`;
}

// 날짜 형식 변환 함수
function formatDate(date) {
    if (!(date instanceof Date) || isNaN(date)) return 'N/A';  // 유효한 날짜가 아닐 경우 'N/A'

    // 날짜를 "yyyy/mm/dd" 형식으로 변환
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');  // 월을 두 자리로 맞춤
    const day = date.getDate().toString().padStart(2, '0');  // 일을 두 자리로 맞춤

    return `${year}년 ${month}월 ${day}일`;  // "yyyy-mm-dd" 형식으로 반환

}