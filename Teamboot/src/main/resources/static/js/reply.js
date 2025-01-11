// 비동기 함수 사용 연습,
// axios ,
// 키워드, async , 함수의 선언부 앞에 사용하고,
// 키워드, await, 비동기적으로 받아온 함수 앞에 사용, 반환 타입, Promise,

// 비동기 함수의 핵심, 통보,
// 비동기 함수가 동작을 하고서, 무언가 알려주기,
//예) 카페,
//
// 비동기함수, async
// 커피 주문 할 때,
// 주문을 받는 사람이 1명만 있으면, 1명이 혼자서,

// await
// 1) 주문 받고,
// 2) 커피 만들고,
// 3) 커피 전달, -> 통보!! -> Promise

// 만약, 손님이 여러명, 1명의 커피가 완성 되기까지 대기.

// 점원 3명,
//

//연습용
// axios 도구 역할, 비동기 통신으로 문법 작성시,
// 콜백 함수 안에 또 콜백 함수를 작성해서, 콜백 지옥, 표현.
//  ex( function doA(  doB(
//   또다른 함수들을 호출하는 형식으로,
//   ),
// 문법 작성시는, 동기적 함수 표현식으로 사용하되, 실제 동작은 비동기적으로 합니다.
// 결론, 비동기 통신을 이용하되, 문법 작성은 마치 동기적으로 동작하는 것처럼 작성,

async function get(bno) {
    const result = await axios.get(`/replies/list/${bno}`)
    console.log(result)
}

// 댓글 전체 목록
// 순서1, 서버로부터 , 댓글 목록 데이터 받아오기
// async function getList({bno,page,size,goLast}) {
//     const result = await axios.get(`/replies/list/${bno}`,
//         {params: {page,size}})
//     // console.log(result)
//     return result.data
// }

// 마지막 댓글 위치로 이동하기.
async function getList({bno,page,size,goLast}) {
    const result = await axios.get(`/replies/list/${bno}`,
        {params: {page,size}})
    // console.log(result)
    if(goLast){
        const total = result.data.total
        // 마지막 댓글 페이지 여부 ,
        // 만약, 전쳇 갯수 101개.
        // 101/10 = 10.1, 올림 : 11, 마지막 페이지 의미.
        // 75/10 = 7.5 올림 : 8  마지막 페이지 의미.
        const lastPage = parseInt(Math.ceil(total/size))
        return getList({bno:bno,page:lastPage, size:size})
    }
    return result.data
}

//댓글 등록
async function addReply(replyObj){
    // 등록 후, response = {"rno": 102}
    const response = await axios.post(`/replies/`, replyObj)
    return response.data
}

//댓글 조회 , rno : 댓글 번호
async function getReply(rno){
    const response = await axios.get(`/replies/${rno}`)
    return response.data
}

// 댓글 수정, 수정할 댓글 내용 :  replyObj 이용하기.
async function updateReply(replyObj){
    const response = await axios.put(`/replies/${replyObj.rno}`,replyObj)
    return response.data
}

// 댓글 삭제, 댓글 번호만 필요함 rno
async function deleteReply(rno){
    const response = await axios.delete(`/replies/${rno}`)
    return response.data
}








